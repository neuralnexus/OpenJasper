/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Activate AMP WP plugin
 */
"use strict";
recipes.register({
    id: "wordpress-amp",
    on: { afterInitialize: {} },
    conditions: {
        tierModules: { all: ["wordpress"] },
        cloudTags: { any: ["gce"] },
        shouldInvoke: () => !fs.existsSync(path.join(platform.pathInfo.namiDataPath, ".restored")),
    },
    recipeHandler: async function (input) {
        const pluginName = "amp";
        function runCli(args) {
            return runProgram(path.join(platform.pathInfo.namiAppPath, "wp-cli/bin/wp"), args);
        }
        function isMultisite() {
            return !!fs.readFileSync(path.join(platform.pathInfo.namiAppPath, "wordpress/wp-config.php")).toString().match(/MULTISITE/);
        }
        // Activate the plugin
        logger.info(`Activating plugin ${pluginName}...`);
        const activateArgs = ["plugin", "activate", pluginName];
        if (isMultisite())
            activateArgs.push("--network");
        runCli(activateArgs);
    }
});
