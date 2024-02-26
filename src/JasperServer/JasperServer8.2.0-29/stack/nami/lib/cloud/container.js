"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.ContainerCloud = void 0;
const base_1 = require("./base");
/**
 * Logic specific to be run inside a container
 */
class ContainerCloud extends base_1.BaseCloud {
    constructor(options) {
        super(options);
        this.cloudTags = this.cloudTags.concat([
            "container"
        ]);
    }
    /**
     * For containers, use the `PROVISIONER_USERDATA_` previx for environment variables
     * to pass custom data
     */
    async _getUserDataNow(key) {
        const env_key = `PROVISIONER_USERDATA_${key.toString().toUpperCase()}`;
        if (process.env[env_key]) {
            return process.env[env_key];
        }
        return undefined;
    }
}
exports.ContainerCloud = ContainerCloud;
;
