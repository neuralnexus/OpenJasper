/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Configure MariaDB specific platform settings
 */
"use strict";
recipes.register({
    id: "mariadb-platform-settings",
    on: { beforeInitialize: {} },
    conditions: {
        tierModules: { any: ["mariadb"] },
        platformTags: { any: ["linux"] }
    },
    recipeHandler: function (input) {
        // Kernel requirements for mariadb
        const kernelOptions = [
            "fs.file-max=65536",
        ];
        const securityLimits = [
            "mysql soft nofile 65536",
            "mysql hard nofile 65536",
        ];
        const sysctlFile = "/etc/sysctl.d/99-bitnami.conf";
        try {
            fs.writeFileSync("/etc/security/limits.d/99-bitnami.conf", securityLimits.join("\n"));
            fs.writeFileSync(sysctlFile, kernelOptions.join("\n"));
            runProgram("sysctl", ["-p", sysctlFile]);
        }
        catch (e) {
            logger.warn("Unable to set MariaDB kernel settings", e);
        }
    }
});
