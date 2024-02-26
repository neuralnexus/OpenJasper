/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Recipe for applying app configurations specific to the instance size
 */
"use strict";
recipes.register({
    id: "resize-modules",
    on: { beforeStart: {} },
    conditions: {
        ifChanged: (input) => {
            return platform.getMemoryInfo().totalPhysicalMB;
        }
    },
    recipeHandler: async function (input) {
        const mem = platform.getMemoryInfo().totalPhysicalMB;
        for (const m of provisioner.tierDefinition.modules) {
            if (m.exportsFunction("resize")) {
                m.runExport("resize", ["--memory", `${mem}M`]);
            }
        }
    }
});
