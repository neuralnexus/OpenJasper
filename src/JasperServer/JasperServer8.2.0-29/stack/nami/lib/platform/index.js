"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.getCurrentPlatform = void 0;
const debian_8_linux_1 = require("./debian_8_linux");
const debian_9_linux_1 = require("./debian_9_linux");
const debian_10_linux_1 = require("./debian_10_linux");
const debian_11_linux_1 = require("./debian_11_linux");
const ubuntu_linux_1 = require("./ubuntu_linux");
;
;
;
;
;
const platformClassMap = {
    "debian-8": debian_8_linux_1.Debian8LinuxPlatform,
    "debian-9": debian_9_linux_1.Debian9LinuxPlatform,
    "debian-10": debian_10_linux_1.Debian10LinuxPlatform,
    "debian-11": debian_11_linux_1.Debian11LinuxPlatform,
    "ubuntu-16": ubuntu_linux_1.UbuntuLinuxPlatform
};
/**
 * This is our "platform factory".
 * We try to get platform implementation given its name
 * assigning some default when available
 * @param options a PlatformOptions object
 */
function getCurrentPlatform(options) {
    let match;
    if (options.platformName) {
        if (match = options.platformName.match(/^(.*)-(x|amd)64/)) {
            options = Object.assign({}, options, { platformName: match[1], bits: 64 });
        }
        else if (match = options.platformName.match(/^(.*)-x86/)) {
            options = Object.assign({}, options, { platformName: match[1], bits: 32 });
        }
    }
    else {
        options = Object.assign({}, options, { platformName: "debian-11", bits: 64 });
    }
    if (platformClassMap[options.platformName]) {
        return new platformClassMap[options.platformName](options);
    }
    else {
        throw new Error("No platform specified");
    }
}
exports.getCurrentPlatform = getCurrentPlatform;
