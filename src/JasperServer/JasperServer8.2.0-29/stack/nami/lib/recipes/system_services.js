/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Create init.d script that starts all services via provisioner
 */
"use strict";
(() => {
    const SERVICE_VERSION = 1;
    recipes.register({
        id: "system-services",
        on: { beforeInitialize: {} },
        conditions: {
            ifChanged: (input) => {
                return SERVICE_VERSION;
            },
            // don't run if the machine was provisioned externally
            shouldInvoke: (input) => !(input.provisioner.provisioned)
        },
        recipeHandler: function (input) {
            platform.createSystemService(provisioner.provisionerServiceName, {
                description: `Bitnami Init Script`,
                startCommand: `${platform.pathInfo.provisionerCommand} start`,
                stopCommand: `${platform.pathInfo.provisionerCommand} stop`,
                increaseLimits: true
            });
        }
    });
    /*
    * Create init.d script that triggers first boot on first boot
    */
    recipes.register({
        id: "system-services-firstboot",
        on: { provisionMachine: {} },
        conditions: {
            cloudTags: { all: ["system-services"] },
        },
        recipeHandler: function (input) {
            const logDir = "/opt/bitnami/var/log";
            const logFile = `${logDir}/first-boot.log`;
            const startCommand = "(" +
                `chmod 0600 ${logFile} ; ` +
                `${platform.pathInfo.provisionerCommand} --log-level ${logger.logLevelName} firstboot` +
                `) >>${logFile} 2>&1`;
            if (!fs.existsSync(logDir)) {
                fs.mkdirSync(logDir, { recursive: true });
            }
            platform.createSystemService(provisioner.provisionerServiceName, {
                description: "Bitnami services bootstrap",
                startCommand: startCommand,
                stopCommand: "echo \"Stopping not possible at this point\" ; exit 1"
            });
        }
    });
})();
