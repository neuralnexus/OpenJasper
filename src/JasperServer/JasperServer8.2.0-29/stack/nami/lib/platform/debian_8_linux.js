"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.Debian8LinuxPlatform = void 0;
const debian_linux_1 = require("./debian_linux");
/**
 * Class for handling Debian 8 Linux distributions
 */
class Debian8LinuxPlatform extends debian_linux_1.DebianLinuxPlatform {
    constructor(options) {
        super(options);
        this.platformTags.push("debian-8");
    }
    /**
     * OS and/or distribution version i.e. `8` for Debian 8
     */
    get distroVersion() {
        return "8";
    }
}
exports.Debian8LinuxPlatform = Debian8LinuxPlatform;
