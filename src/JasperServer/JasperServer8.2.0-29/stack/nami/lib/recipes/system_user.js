/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Rename system user to bitnami if that user does not currently exist
 */
"use strict";
(() => {
    const SYSTEM_USERNAME = "bitnami";
    recipes.register({
        id: "system-username",
        on: { provisionMachine: {} },
        conditions: {
            shouldInvoke: input => {
                try {
                    runProgram("id", [SYSTEM_USERNAME]);
                    return false;
                }
                catch (e) {
                    return true;
                }
            }
        },
        recipeHandler: input => {
            platform.renameSystemUserAndGroup(SYSTEM_USERNAME);
        }
    });
})();
