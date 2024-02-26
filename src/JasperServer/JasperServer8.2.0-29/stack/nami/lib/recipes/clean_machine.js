/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Clean machine
 */
"use strict";
recipes.register({
    id: "clean-machine",
    on: { cleanMachine: {} },
    conditions: {
        platformTags: { any: ["linux"] }
    },
    recipeHandler: async function (input) {
        let script = "rm -rf " +
            "/root/.*_history /home/*/.*_history /home/*/.sudo* " +
            "/opt/bitnami/*/logs/* /opt/bitnami/.tmp/* " +
            "/var/lib/cloud/* /media/* /tmp/* /mnt/* /etc/sysconfig/rh-cloud-firstboot " +
            "`find / -name \"*.log\" -o -name \"mysql.sock\" -o  -name \".s.PGSQL*\"`\n";
        fs.writeFileSync("/tmp/clean_machine", script);
        runProgram("sh", ["/tmp/clean_machine"]);
    }
});
