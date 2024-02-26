/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Create /opt/bitnami/scripts/environment.sh used by Bitnami modules
 */
"use strict";
/**
 * Handler to create string with environment variable exports
 */
function addEnvironmentVariablesSectionToFile(file, section, envVars) {
    const sectionString = `${section} settings`.toUpperCase();
    const envVarsString = envVars.map(envVar => `export ${envVar}`).join("\n");
    if (envVarsString !== "") {
        platform.createFileSection(file, {
            title: sectionString,
            contents: envVarsString,
            create: true,
        });
    }
}
recipes.register({
    id: "environment-script",
    on: { beforeInitialize: {} },
    conditions: {
        shouldInvoke: function hasModules() {
            return provisioner.tierDefinition.modules.length > 0;
        },
    },
    recipeHandler: async function (input) {
        const bitnamiRoot = "/opt/bitnami";
        const bitnamiScriptsRoot = `${bitnamiRoot}/scripts`;
        const bitnamiVarRoot = `${bitnamiRoot}/var`;
        // Define OS environment variables for images with Bitnami modules, for libcomponent.sh to work
        // This makes Bitnami VMs consistent with container images
        const distroName = `${provisioner.platform.distro}-${provisioner.platform.distroVersion}`;
        const osArch = provisioner.platform.osArch;
        const os = osArch.split("-")[0];
        const arch = osArch.split("-").slice(1).join("-");
        for (let path of glob.sync("{/root/{.bashrc,.profile},/home/*/{.bashrc,.profile},/etc/skel/{.bashrc,.profile}}")) {
            addEnvironmentVariablesSectionToFile(path, "Bitnami image metadata", [
                `OS_ARCH=${arch}`,
                `OS_FLAVOUR=${distroName}`,
                `OS_NAME=${os}`,
                `BITNAMI_APP_NAME=${provisioner.stackDefinition.details.key}`,
                `BITNAMI_IMAGE_VERSION=${provisioner.stackDefinition.details.version}`,
                `APP_VERSION=${provisioner.stackDefinition.details.version}`,
            ]);
        }
        // Allow the previously defined environment variables to be passed via sudo
        fs.writeFileSync("/etc/sudoers.d/99-bitnami-pkg-root", `Defaults env_keep += "OS_ARCH OS_FLAVOUR OS_NAME BITNAMI_APP_NAME BITNAMI_IMAGE_VERSION APP_VERSION"\n`);
        // Create environment file with environment variables specified in userdata
        // This file will be sourced when needed, in the corresponding {component}-env.sh file
        const envFile = `${bitnamiVarRoot}/user-data-env.sh`;
        addEnvironmentVariablesSectionToFile(envFile, "user data", await cloud.getAllUserDataVariables());
    }
});
