/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Add all found bin/sbin directories to PATH if they contain
 * at least one executable binary
 */
"use strict";
recipes.register({
    id: "add-to-system-path",
    on: { provisionMachine: { depends: ["system-packages"] }, beforeInitialize: {} },
    conditions: {
        platformTags: { any: ["linux"] },
    },
    recipeHandler: function (input) {
        let pathList = platform.getAdditionalSystemPaths();
        if (provisioner.tierDefinition.tags.indexOf("exclude-mariadb-in-path") > 0) {
            pathList = pathList.filter(p => !p.includes(`${platform.pathInfo.namiAppPath}/mariadb`));
        }
        platform.modifySystemPath({ add: pathList });
        platform.modifySudoersPath({ add: pathList, addAtBeginning: true });
    }
});
