/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Run configureHost for specific tiers and modules
 */
"use strict";
recipes.register({
    id: "create-configure-app-domain-script",
    on: { afterInitialize: {} },
    conditions: {
        shouldInvoke: () => provisioner.tierDefinition.modules.some((m) => {
            return m.exportsFunction("configureHost");
        }),
    },
    recipeHandler: function (input) {
        const scriptPath = path.join(platform.pathInfo.namiAppPath, "configure_app_domain");
        const scriptContents = `#!/bin/bash

if [[ "$(id -u)" -ne "0" ]]; then
    echo "This script must be run as a superuser"
    exit 1
fi

# Help menu
display_help() {
    echo "Usage: $0 [arguments...]"
    echo
    echo "Options:"
    echo "  -h, --help                         show help menu"
    echo "  --domain arg                       configure application domain and exit"
    echo "  --enable-automatic-configuration   enable automatic IP configuration"
    echo "  --disable-automatic-configuration  disable automatic IP configuration"
}

# Default options
domain=""
disable_automatic_updates=true

if [[ "$#" -eq 0 ]]; then
    echo "No arguments were specified"
    display_help
    exit 1
fi

# Parse CLI arguments
while [[ "$#" -gt 0 ]]; do
    case "$1" in
        --domain)
            shift
            domain="\${1:?missing domain value}"
            ;;
        --disable-automatic-configuration)
            disable_automatic_updates=true
            ;;
        --enable-automatic-configuration)
            disable_automatic_updates=false
            ;;
        -h|--help)
            display_help
            exit
            ;;
        *)
            echo "Unknown parameter $1"
            display_help
            exit 1
    esac
    shift
done

# Paths to modifier files
app_domain_file="${path.join(platform.pathInfo.namiAppPath, ".app_domain")}"
disable_file="${path.join(platform.pathInfo.namiAppPath, ".app_domain_disabled")}"

# Updates the domain for all installed apps
update_domains() {
  ${path.join(platform.pathInfo.namiAppPath, "nami/bin/provisioner")} \
    --only-recipes configure-host callRecipes afterStart
}

if "$disable_automatic_updates"; then
    # Configuring a domain will always disable automatic domain configuration when the IP address changes
    if [[ -n "$domain" ]]; then
        echo "Configuring domain to \${domain}"
        echo "$domain" > "$app_domain_file"
        rm -f "$disable_file"
        update_domains
    fi

    echo "Disabling automatic domain update for IP address changes"
    touch "$disable_file"
else
    echo "Enabling automatic domain update for IP address changes"
    rm -f "$app_domain_file" "$disable_file"
    update_domains
fi
`;
        fs.writeFileSync(scriptPath, scriptContents);
        fs.chmodSync(scriptPath, "0755");
    }
});
recipes.register({
    id: "configure-host",
    on: { afterStart: {} },
    conditions: {
        shouldInvoke: () => !fs.existsSync(path.join(platform.pathInfo.namiAppPath, ".app_domain_disabled")),
        ifChanged: (input) => {
            const appDomainFile = path.join(platform.pathInfo.namiAppPath, ".app_domain");
            // Externally provisioned machines store the machine IP type in a file
            const machineIpTypeFile = path.join(platform.pathInfo.namiAppPath, "var/data/machine_ip_type");
            if (fs.existsSync(appDomainFile)) {
                return fs.readFileSync(appDomainFile).toString().trim();
            }
            else if (fs.existsSync(machineIpTypeFile) && fs.readFileSync(machineIpTypeFile).toString().match("private")) {
                return provisioner.cloud.getMetaData("private-ipv4");
            }
            else {
                return provisioner.cloud.getMetaData("public-ipv4");
            }
        }
    },
    recipeHandler: async function (input) {
        let ip = input.newValue;
        for (const m of provisioner.tierDefinition.modules) {
            if (m.exportsFunction("configureHost")) {
                m.runExport("configureHost", [ip]);
            }
        }
    }
});
