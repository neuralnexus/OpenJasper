/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Configure Cassandra specific platform settings
 */
"use strict";
recipes.register({
    id: "cassandra-platform-settings",
    on: { beforeInitialize: {} },
    conditions: {
        tierModules: { any: ["cassandra"] },
        platformTags: { any: ["linux"] },
        cloudTags: { not: { any: ["virtual-machine"] } },
    },
    recipeHandler: function (input) {
        // Kernel requirements for cassandra
        // https://docs.datastax.com/en/dse/5.1/dse-admin/datastax_enterprise/config/configRecommendedSettings.html
        const kernelOptions = [
            // TCP settings
            "net.ipv4.tcp_keepalive_time=60",
            "net.ipv4.tcp_keepalive_probes=3",
            "net.ipv4.tcp_keepalive_intvl=10",
            "net.core.rmem_max=16777216",
            "net.core.wmem_max=16777216",
            "net.core.rmem_default=6777216",
            "net.core.wmem_default=16777216",
            "net.core.optmem_max=40960",
            "net.ipv4.tcp_rmem=4096 87380 16777216",
            "net.ipv4.tcp_wmem=096 65536 16777216",
            // User resource limits
            "vm.max_map_count=1048575",
        ];
        const securityLimits = [
            "cassandra - memlock unlimited",
            "cassandra - nofile 100000",
            "cassandra - nproc 32768",
            "cassandra - as unlimited",
        ];
        const sysctlFile = "/etc/sysctl.d/99-bitnami.conf";
        try {
            fs.writeFileSync("/etc/security/limits.d/99-bitnami.conf", securityLimits.join("\n"));
            fs.writeFileSync(sysctlFile, kernelOptions.join("\n"));
            fs.appendFileSync("/etc/pam.d/common-session", "\nsession required pam_limits.so");
            runProgram("sysctl", ["-p", sysctlFile]);
            // Disable defrag for hugepages
            fs.writeFileSync("/sys/kernel/mm/transparent_hugepage/defrag", "never");
            // Disable swap
            runProgram("swapoff", ["--all"]);
        }
        catch (e) {
            throw new Error(`Unable to set Cassandra kernel settings: ${e}`);
        }
    }
});
