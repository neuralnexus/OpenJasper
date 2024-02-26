/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Run initialization scripts specified in the setupScripts object of a definition
 */
"use strict";
recipes.register({
    id: "setup-scripts",
    // Provisioning is performed in multi-tier images during build (boot for image-less platforms)
    // It is not executed for single VMs meaning this recipe needs to be called externally during build time
    on: { provisionMachine: { depends: ["system-packages"] } },
    conditions: {
        shouldInvoke: function hasModules() {
            return provisioner.tierDefinition.modules.length > 0;
        },
    },
    recipeHandler: function (input) {
        // We execute postunpack logic during provisioning, which happens within during the construction of the final image
        for (const m of provisioner.tierDefinition.modules) {
            m.runExport("postunpack");
        }
        // Execute setup scripts after postunpack scripts, which are image-specific scripts not tied to a specific component
        const env = { PATH: `${provisioner.platform.getAdditionalSystemPaths().join(":")}:${process.env.PATH}` };
        provisioner.tierDefinition.setupScripts.forEach(setupScript => {
            logger.info(`Running setup script ${setupScript}`);
            runProgram("bash", ["-c", setupScript], { env });
        });
    }
});
