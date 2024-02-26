/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * This recipe is executed at first-boot time of an azure pre-baked image for MT.
 * The goal is to enable the system login when the user chooses to use 'bitnami' to log in.
 * This is necessary for the Azure MT pre-baked image, as the configuration of the machine
 * now happens through the firstboot provisioner event.
 */
"use strict";
(() => {
    const IMAGE_LOGIN = "bitnami";
    const TARGET_SHELL = "/bin/bash";
    recipes.register({
        id: "bitnami-enable-login",
        on: { firstboot: {} },
        conditions: {
            cloudTags: { any: ["azure"] },
            shouldInvoke: (input) => (provisioner.cloud.getUserName() === IMAGE_LOGIN)
        },
        recipeHandler: (input) => {
            logger.info(`Enabling login for user '${IMAGE_LOGIN}'.`);
            runProgram("usermod", ["-s", TARGET_SHELL, IMAGE_LOGIN]);
        }
    });
})();
