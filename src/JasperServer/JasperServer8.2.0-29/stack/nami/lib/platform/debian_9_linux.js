"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.Debian9LinuxPlatform = void 0;
const debian_linux_1 = require("./debian_linux");
/**
 * Class for handling Debian 9 Linux distributions
 */
class Debian9LinuxPlatform extends debian_linux_1.DebianLinuxPlatform {
    constructor(options) {
        super(options);
        this.platformTags.push("debian-9");
    }
    /**
     * OS and/or distribution version i.e. `9` for Debian 9
     */
    get distroVersion() {
        return "9";
    }
}
exports.Debian9LinuxPlatform = Debian9LinuxPlatform;
