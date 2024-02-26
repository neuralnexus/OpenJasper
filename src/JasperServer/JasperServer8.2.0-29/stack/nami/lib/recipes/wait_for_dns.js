/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
"use strict";
recipes.register({
    id: "wait-for-dns",
    on: { beforeInitialize: {} },
    conditions: {
        cloudTags: { any: ["azure"] },
    },
    recipeHandler: async function (input) {
        const delay = 5;
        const attempts = 10;
        const hostname = runProgram("hostname").trim();
        try {
            logger.debug(`Trying to resolve own hostname '${hostname}'`);
            await utils.retry(async () => {
                await utils.resolveHostname(hostname);
            }, { logger, errorMessage: `Unable to resolve ${hostname}`, attempts, delay });
            logger.debug(`Trying to resolve own hostname '${hostname}' - Finished`);
        }
        catch (e) {
            logger.warn(`Unable to resolve ${hostname} after ${attempts} attempts`);
        }
    }
});
