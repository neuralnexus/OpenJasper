/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Recipes for managing gonit
 */
"use strict";
// Support Monit configuration paths, used by Bash modules
const monitConfDir = "/etc/monit/conf.d";
const monitConfPattern = `${monitConfDir}/*.conf`;
const gonitConfFile = "/etc/gonit/gonitrc";
const gonitBinary = `${platform.pathInfo.namiAppPath}/gonit/bin/gonit`;
const gonitBinaryLinks = ["/usr/bin/gonit", "/usr/bin/monit"];
const gonitConfigurationHeader = `set httpd port 2812 and
    use address localhost
    allow localhost

include ${monitConfPattern}
`;
/*
 * Helper function for getting list of monit files
 */
function getMonitFiles() {
    return glob.sync(monitConfPattern);
}
;
const gonitConditions = {
    shouldInvoke: function (input) {
        // only create gonit configuration and run it if there are any services that support it
        return getMonitFiles().length > 0;
    }
};
/*
 * Recipe creating gonit configuration after modules are initialized
 */
recipes.register({
    id: "gonit-configuration",
    on: { afterInitialize: {} },
    conditions: gonitConditions,
    recipeHandler: function (input) {
        const monitFiles = getMonitFiles();
        let configuration = gonitConfigurationHeader;
        for (let filename of monitFiles) {
            // Files in /etc/monit/conf.d will be added dynamically
            if (monitConfDir !== path.dirname(filename)) {
                configuration += `\ninclude ${filename}`;
            }
        }
        configuration += "\n";
        if (!fs.existsSync(path.dirname(gonitConfFile))) {
            fs.mkdirSync(path.dirname(gonitConfFile));
        }
        fs.writeFileSync(gonitConfFile, configuration);
        fs.chmodSync(gonitConfFile, "0600");
        // Make gonit available through monit and gonit commands
        for (const link of gonitBinaryLinks) {
            fs.symlinkSync(gonitBinary, link);
        }
    }
});
/**
 * Recipe for starting gonit
 */
recipes.register({
    id: "gonit-start",
    on: { afterStart: {} },
    conditions: gonitConditions,
    recipeHandler: async function (input) {
        logger.info("Starting gonit monitoring service");
        runProgram(gonitBinary);
        // Wait a couple of seconds for Gonit to be able to properly process the 'monitor all' request
        runProgram("sleep", ["2"]);
        logger.info("Start monitoring all services from gonit");
        await utils.retry(() => {
            runProgram(gonitBinary, ["monitor", "all"]);
        }, { delay: 2, errorMessage: "Unable to execute 'gonit monitor all'. Retrying...", logger });
    }
});
/**
 * Recipe for stopping gonit
 */
recipes.register({
    id: "gonit-stop",
    on: { beforeStop: {} },
    conditions: gonitConditions,
    recipeHandler: function (input) {
        logger.info("Stop monitoring services from gonit");
        runProgram(gonitBinary, ["unmonitor", "all"]);
    }
});
