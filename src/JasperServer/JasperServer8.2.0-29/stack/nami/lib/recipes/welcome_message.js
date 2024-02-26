/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Recipe for adding information to welcome message
 */
"use strict";
const VERSION = 1;
const MOTD_FILE = "/etc/motd";
recipes.register({
    id: "welcome-message-create",
    on: { afterInitialize: {}, afterFailedInitialize: {} },
    conditions: {
        ifChanged: (input) => {
            return VERSION;
        },
        // don't run if the machine was provisioned externally
        shouldInvoke: (input) => !(input.provisioner.provisioned)
    },
    recipeHandler: async function (input) {
        let storage = provisioner.storageManager.getItem("recipe-welcome-message");
        let motd = "";
        if (fs.existsSync(MOTD_FILE)) {
            motd = fs.readFileSync(MOTD_FILE).toString();
        }
        motd += "       ___ _ _                   _\n";
        motd += "      | _ |_) |_ _ _  __ _ _ __ (_)\n";
        motd += "      | _ \\ |  _| ' \\/ _` | '  \\| |\n";
        motd += "      |___/_|\\__|_|_|\\__,_|_|_|_|_|\n";
        motd += "\n";
        motd += "************************************************************";
        motd += "\n";
        if (input.eventName === "afterInitialize") {
            // TODO: we need more metadata to show more meaningful message.
            motd += "  This is a Bitnami server.\n";
            motd += "\n";
            if (provisioner.tierDefinition.tags.indexOf("fixed-public-url") > 0) {
                motd += "  The application is available at:\n";
                motd += "\n";
                motd += `  ##PUBLIC_URL##\n`;
            }
            else if (provisioner.stackDefinition.tags.indexOf("requires-application-gateway") > 0) {
                // TODO It should get the public IP from the Application Gateway if there is one
            }
            else if (provisioner.tierDefinition.publicPorts.indexOf(443) >= 0) {
                motd += "  The application is available at:\n";
                motd += "\n";
                motd += `  https://##PUBLIC_IP##/\n`;
            }
            else if (provisioner.tierDefinition.publicPorts.indexOf(80) >= 0) {
                motd += "  The application is available at:\n";
                motd += "\n";
                motd += "  http://##PUBLIC_IP##/\n";
            }
        }
        else if (input.eventName === "afterFailedInitialize") {
            motd += "  The application initialization has failed with an error.\n";
            motd += "  More information can be found in the following log file:\n";
            motd += "\n";
            motd += "  /opt/bitnami/var/log/first-boot.log\n";
        }
        motd += "\n";
        motd += "************************************************************\n";
        storage.data.motd = motd;
        storage.save();
        if (provisioner.tierDefinition.tags.indexOf("fixed-public-url") > 0) {
            motd = motd.replace(/##PUBLIC_URL##/, await cloud.getUserData("PROVISIONER_PUBLIC_URL"));
        }
        else {
            motd = motd.replace(/##PUBLIC_IP##/, await provisioner.cloud.getMetaData("public-ipv4"));
        }
        fs.writeFileSync(MOTD_FILE, motd);
    }
});
/*
 * Recipe for writing message file before services are started
 */
recipes.register({
    id: "welcome-message",
    on: { beforeStart: {} },
    conditions: {
        tierTags: { not: ["fixed-public-url"] },
        ifChanged: (input) => {
            return provisioner.cloud.getMetaData("public-ipv4");
        },
        // don't run if the machine was provisioned externally
        shouldInvoke: (input) => !(input.provisioner.provisioned)
    },
    recipeHandler: async function (input) {
        let storage = provisioner.storageManager.getItem("recipe-welcome-message");
        // TODO: private / public IP address ?
        const publicIp = await provisioner.cloud.getMetaData("public-ipv4");
        let motd = storage.data.motd;
        motd = motd.replace(/##PUBLIC_IP##/, publicIp);
        fs.writeFileSync(MOTD_FILE, motd);
    }
});
