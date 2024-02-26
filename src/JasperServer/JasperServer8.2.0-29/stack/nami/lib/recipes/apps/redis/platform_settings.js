/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Configure Redis specific platform settings
 */
"use strict";
recipes.register({
    id: "redis-platform-settings",
    on: { beforeInitialize: {}, beforeStart: {} },
    conditions: {
        tierModules: { any: ["redis", "redis-sentinel", "gitlab", "gitlabee"] },
        platformTags: { any: ["linux"] }
    },
    recipeHandler: function (input) {
        // Kernel requirements for redis
        // https://redis.io/topics/admin#redis-setup-hints
        const kernelOptions = [
            "vm.overcommit_memory=1",
            "net.core.somaxconn=65535",
        ];
        const sysctlFile = "/etc/sysctl.d/99-bitnami.conf";
        try {
            if (!fs.existsSync(sysctlFile) || !fs.readFileSync(sysctlFile).toString().match(kernelOptions.join("\n"))) {
                fs.writeFileSync(sysctlFile, kernelOptions.join("\n"));
            }
            runProgram("sysctl", ["-p", sysctlFile]);
            fs.writeFileSync("/sys/kernel/mm/transparent_hugepage/enabled", "never");
        }
        catch (e) {
            logger.warn("Unable to set Redis kernel settings", e);
        }
    }
});
