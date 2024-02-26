/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Install and upgrade system packages
 */
"use strict";
recipes.register({
    id: "system-packages",
    on: { provisionMachine: {} },
    conditions: {
        platformTags: { any: ["linux"] }
    },
    recipeHandler: async function (input) {
        const basePackages = platform.systemPackages;
        const distroKey = `${platform.distro}-${platform.distroVersion}`;
        const stackPackages = provisioner.stackDefinition.systemPackagesDependencies(distroKey);
        // prevent Azure agent upgrade on Azure
        if (cloud.cloudTags.indexOf("azure") >= 0) {
            // try to hold using both Debian and Ubuntu package names, for safety
            platform.packagesToHold.push("waagent", "walinuxagent");
        }
        logger.info(`Installing system packages for ${distroKey}`);
        logger.debug(`Base packages: ${basePackages.join(" ")}`);
        const systemUpgrade = cloud.cloudTags.indexOf("skip-system-upgrade") < 0;
        let packagesToInstall = basePackages;
        if (platform.platformTags.indexOf("skip-stack-packages") < 0) {
            packagesToInstall = packagesToInstall.concat(stackPackages);
            logger.debug(`Stack packages: ${stackPackages.join(" ")}`);
        }
        else {
            logger.debug("Skipping stack packages");
        }
        platform.installAndUpgradeSystemPackages(packagesToInstall, systemUpgrade);
    }
});
recipes.register({
    id: "system-packages-cleanup",
    on: { cleanMachine: {} },
    conditions: {
        platformTags: { any: ["linux"] }
    },
    recipeHandler: async function (input) {
        platform.cleanSystemPackages();
    }
});
