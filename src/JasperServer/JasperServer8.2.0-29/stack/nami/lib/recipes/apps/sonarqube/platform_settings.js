/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../../../typings-recipe.d.ts" />
/*
 * Configure SonarQube specific platform settings
 */
"use strict";
recipes.register({
    id: "sonarqube-platform-settings",
    on: { beforeInitialize: {} },
    conditions: {
        tierModules: { any: ["sonarqube"] },
        platformTags: { any: ["linux"] }
    },
    recipeHandler: function (input) {
        const kernelOptions = [
            "fs.file-max=65536",
            "vm.max_map_count=262144",
        ];
        const securityLimits = [
            "sonarqube soft nofile 65536",
            "sonarqube hard nofile 65536",
        ];
        const sysctlFile = "/etc/sysctl.d/99-bitnami.conf";
        try {
            fs.writeFileSync("/etc/security/limits.d/99-bitnami.conf", securityLimits.join("\n"));
            fs.writeFileSync(sysctlFile, kernelOptions.join("\n"));
            fs.appendFileSync("/etc/pam.d/common-session", "\nsession required pam_limits.so");
            runProgram("sysctl", ["-p", sysctlFile]);
        }
        catch (e) {
            logger.warn("Unable to set Sonarqube kernel settings", e);
        }
    }
});
