/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Configure MongoDB specific platform settings
 */
"use strict";
recipes.register({
    id: "mongodb-platform-settings",
    on: { beforeInitialize: {}, beforeStart: {} },
    conditions: {
        tierModules: { any: ["mongodb"] },
        platformTags: { any: ["linux"] }
    },
    recipeHandler: function (input) {
        // change settings before service is started as this ca't be easily persisted in system config ;
        // use runProgram as fs.writeFileSync() does not change the settings appropriately
        try {
            runProgram("/bin/bash", ["-c", "echo never >/sys/kernel/mm/transparent_hugepage/enabled"]);
            runProgram("/bin/bash", ["-c", "echo never >/sys/kernel/mm/transparent_hugepage/defrag"]);
        }
        catch (e) {
            logger.warn("Unable to set MongoDB kernel settings", e);
        }
        ;
    }
});
