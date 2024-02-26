/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Configure Elasticsearch specific platform settings
 */
"use strict";
// Kernel requirements for elasticsearch
// https://www.elastic.co/guide/en/elasticsearch/reference/master/setting-system-settings.html#setting-system-settings
// https://www.elastic.co/guide/en/elasticsearch/reference/current/setup-configuration-memory.html#mlockall
recipes.register({
    id: "elasticsearch-platform-settings",
    on: { beforeInitialize: {} },
    conditions: {
        tierModules: { any: ["elasticsearch"] },
        platformTags: { any: ["linux"] }
    },
    recipeHandler: function (input) {
        const kernelOptions = [
            "kernel.shmmax=41943040",
            "vm.max_map_count=262144",
            "vm.swappiness=1",
            "fs.file-max=65536",
            "net.ipv4.tcp_challenge_ack_limit=1073741823",
        ];
        const securityLimits = [
            "elasticsearch soft nofile 65536",
            "elasticsearch hard nofile 65536",
            "elasticsearch soft nproc 32768",
            "elasticsearch hard nproc 32768",
            "elasticsearch soft memlock -1",
            "elasticsearch hard memlock -1"
        ];
        const sysctlFile = "/etc/sysctl.d/99-bitnami.conf";
        try {
            fs.writeFileSync("/etc/security/limits.d/99-bitnami.conf", securityLimits.join("\n"));
            fs.writeFileSync(sysctlFile, kernelOptions.join("\n"));
            fs.appendFileSync("/etc/pam.d/common-session", "\nsession required pam_limits.so");
            runProgram("sysctl", ["-p", sysctlFile]);
        }
        catch (e) {
            logger.warn("Unable to set Elasticsearch kernel settings", e);
        }
    }
});
