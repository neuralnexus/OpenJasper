/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
"use strict";
(() => {
    /**
     * Common code to setup the swap file
     */
    function setupSwapFile(_input, sizeMB) {
        let swapfile = path.join(cloud.ephemeralVolumePath, ".swapfile");
        platform.createSwap(swapfile, sizeMB);
        platform.activateSwap(swapfile);
    }
    function setupSwapFileSmall(input) {
        setupSwapFile(input, 600);
    }
    function setupSwapFileRegular(input) {
        setupSwapFile(input, 1000);
    }
    /*
     * Recipe for creating swap file if required memory is less than 1GB
     */
    recipes.register({
        id: "swapfile-on-lowmem",
        on: { beforeInitialize: {}, beforeStart: {} },
        conditions: {
            shouldInvoke: (input) => {
                let memory = platform.getMemoryInfo().totalPhysicalMB;
                return (memory <= 1024) && !(input.provisioner.provisioned);
            },
            ifChanged: (input) => {
                return platform.getUniqueBootId();
            },
            tierTags: { not: ["custom-swap"] }
        },
        recipeHandler: setupSwapFileSmall
    });
    /*
     * Recipe for creating swap file if the asset is tagged with 'custom-swap'
     */
    recipes.register({
        id: "swapfile-on-tag",
        on: { beforeInitialize: {}, beforeStart: {} },
        conditions: {
            /*
              This should apply only to assets with the 'custom-swap' asset defined
              As of this change those were: redis, rabbitmq, mongodb & elasticsearch
            */
            platformTags: { any: ["linux"] },
            tierTags: { any: ["custom-swap"] }
        },
        recipeHandler: setupSwapFileRegular
    });
})();
