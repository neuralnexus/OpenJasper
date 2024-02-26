/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Temporary template for testing recipes more easily
 */
"use strict";
recipes.register({
    id: "platform",
    on: { beforeUnpack: {}, afterUnpack: {}, provisionMachine: {} },
    recipeHandler: async function (input) {
        if (input.eventName === "beforeUnpack") {
            await input.platform.initializePlatformBeforeUnpack();
        }
        else if (input.eventName === "afterUnpack") {
            await input.platform.initializePlatformAfterUnpack();
        }
        else if (input.eventName === "provisionMachine") {
            await input.platform.initializePlatformAfterProvisionSettings();
        }
    }
});
