/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Wait for Zookeeper cluster to be ready
 */
"use strict";
const zkPort = 2181;
recipes.register({
    id: "kafka-wait-for-zk",
    on: { beforeInitialize: {} },
    conditions: {
        tierModules: { any: ["kafka"] },
        instanceTier: { any: ["broker"] }
    },
    recipeHandler: async function (input) {
        const timeout = 5000;
        const retries = 180;
        logger.debug(`Waiting for port ${zkPort} on ${provisioner.peerNodenames}`);
        const promises = provisioner.peerNodenames.map(host => {
            return utils.waitForPort({ host, port: zkPort, timeout, retries, logger });
        });
        for (const promise of promises) {
            if (!(await promise)) {
                throw new Error("Unable to wait for Zookeeper nodes to be ready");
            }
        }
        logger.debug(`Waiting for port ${zkPort} on ${provisioner.peerNodenames} - Finished`);
    }
});
