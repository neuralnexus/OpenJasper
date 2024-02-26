/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Map specific ports based on application requirements.
 *
 * This script uses OS-specific port mapping (i.e. iptables on Linux) to
 * map appropriate ports if they are not exposed by application - such as
 * map port 3000 or 8080 as port 80 for application servers
 */
"use strict";
recipes.register({
    id: "port-mapping",
    on: { afterStart: {} },
    conditions: {
        cloudTags: { all: ["hypervisor"] },
    },
    recipeHandler: function (input) {
        const portMap = provisioner.tierDefinition.portMapping;
        platform.setPortMappings(portMap);
    }
});
