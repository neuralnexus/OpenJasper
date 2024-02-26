/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Activate Jetpack WP plugin and configure affiliate code to track Bitnami referrals
 */
"use strict";
recipes.register({
    id: "wordpress-jetpack",
    on: { afterInitialize: {} },
    conditions: {
        tierModules: { all: ["wordpress"] },
        cloudTags: { any: ["aws", "gce"] },
        shouldInvoke: () => !fs.existsSync(path.join(platform.pathInfo.namiDataPath, ".restored")),
    },
    recipeHandler: async function (input) {
        const pluginName = "jetpack";
        const affiliateID = "13153"; // Bitnami's affiliate ID on http://refer.wordpress.com account
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
        // Set Affiliate Code
        logger.info(`Setting Bitnami Affiliate Code...`);
        runCli(["option", "update", "jetpack_affiliate_code", affiliateID]);
    }
});
