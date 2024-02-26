"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.UbuntuLinuxPlatform = void 0;
const debian_linux_1 = require("./debian_linux");
/**
 * Class for handling Ubuntu Linux distributions
 */
class UbuntuLinuxPlatform extends debian_linux_1.DebianLinuxPlatform {
    constructor(options) {
        super(options);
        this.platformTags.push("ubuntu");
    }
    /**
     * Ubuntu distro identifier
     */
    get distro() {
        return "ubuntu";
    }
    /**
     * Ubuntu distro version
     */
    get distroVersion() {
        return "16.04";
    }
    /**
     * Ubuntu architecture name
     */
    get osArch() {
        return "linux-amd64";
    }
}
exports.UbuntuLinuxPlatform = UbuntuLinuxPlatform;
