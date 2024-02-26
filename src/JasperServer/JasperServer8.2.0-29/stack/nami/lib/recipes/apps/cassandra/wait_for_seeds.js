/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Wait for Cassandra Seeds to be ready
 */
"use strict";
(() => {
    // tslint:disable:max-line-length
    // https://www.quora.com/How-many-seed-coordinator-nodes-should-you-have-in-a-10-node-cluster-of-cassandra-Can-you-have-everyone-be-a-coordinator
    // Seed nodes: In general it is recommended to have 2 seeds for the whole cluster.
    const numSeeds = 2;
    const cassandraPort = 9042;
    /**
     * Returns cassandra seeds: from 0 to the actual number of seeds
     */
    function getSeeds() {
        return provisioner.peerNodenames.slice(0, numSeeds);
    }
    /**
     * Returns true when the node is not seed
     */
    async function isNotSeed() {
        const seeds = getSeeds();
        const addresses = [
            await cloud.getMetaData("public-ipv4"),
            await cloud.getMetaData("private-ipv4"),
            provisioner.peerNodename
        ];
        logger.debug(`Checking if any of '${addresses}' is not one of the seed nodes: ${seeds}`);
        // If some of the addresses is found in seeds, this node is a seed
        const seedMatches = addresses.map(h => seeds.indexOf(h) > -1 ? true : false);
        logger.debug(`Seed matches: '${seedMatches}'`);
        // If none of the addresses were found in seeds, this node is not a seed
        return seedMatches.indexOf(true) < 0;
    }
    recipes.register({
        id: "cassandra-wait-for-seeds",
        on: {
            beforeInitialize: {
                depends: ["wait-for-dns"]
            }
        },
        conditions: {
            tierModules: { any: ["cassandra"] },
            instanceTier: { not: ["main"] },
            shouldInvoke: isNotSeed
        },
        recipeHandler: async function (input) {
            const timeout = 5000;
            const retries = 240;
            const seeds = getSeeds();
            logger.debug(`Waiting for port ${cassandraPort} on ${seeds}`);
            const promises = seeds.map(host => {
                return utils.waitForPort({ host, port: cassandraPort, timeout, retries, logger });
            });
            for (const promise of promises) {
                const ready = await promise;
                if (!ready) {
                    throw new Error("Unable to wait for seed nodes to be ready");
                }
            }
            logger.debug(`Waiting for port ${cassandraPort} on ${seeds} - Finished`);
        }
    });
})();
