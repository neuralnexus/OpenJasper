"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.Debian10LinuxPlatform = void 0;
const debian_linux_1 = require("./debian_linux");
/**
 * Class for handling Debian 10 Linux distributions
 */
class Debian10LinuxPlatform extends debian_linux_1.DebianLinuxPlatform {
    constructor(options) {
        super(options);
        this.platformTags.push("debian-10");
    }
    /**
     * OS and/or distribution version i.e. `10` for Debian 10
     */
    get distroVersion() {
        return "10";
    }
}
exports.Debian10LinuxPlatform = Debian10LinuxPlatform;
