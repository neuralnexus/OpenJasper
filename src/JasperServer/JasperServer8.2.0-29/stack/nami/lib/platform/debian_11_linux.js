"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.Debian11LinuxPlatform = void 0;
const debian_linux_1 = require("./debian_linux");
/**
 * Class for handling Debian 11 Linux distributions
 */
class Debian11LinuxPlatform extends debian_linux_1.DebianLinuxPlatform {
    constructor(options) {
        super(options);
        this.platformTags.push("debian-11");
    }
    /**
     * OS and/or distribution version i.e. `11` for Debian 11
     */
    get distroVersion() {
        return "11";
    }
}
exports.Debian11LinuxPlatform = Debian11LinuxPlatform;
