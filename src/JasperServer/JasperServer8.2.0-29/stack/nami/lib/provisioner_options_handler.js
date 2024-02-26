"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.ProvisionerOptionsHandler = void 0;
const fs = require("fs");
class ProvisionerOptionsHandler {
    constructor() {
        this.options = {};
    }
    /**
     * Parse options from text, reading each line as name=value parameters, skipping comments
     */
    initializeFromText(text) {
        for (let line of text.split("\n")) {
            let match = line.match(/^\s*(.+?)\s*=\s*(.*)\s*$/);
            // parse all lines that are not comments
            if (match && !match[1].match(/^#/)) {
                this.options[match[1]] = match[2];
            }
        }
    }
    /**
     * Read options from a file
     */
    initializeFromFile(path) {
        this.initializeFromText(fs.readFileSync(path).toString());
    }
    /**
     * Returns whether a specified option was provided
     */
    provided(key) {
        return Object.keys(this.options).indexOf(key) >= 0;
    }
    /**
     * Return whether specified option is set to true
     */
    isTrue(key, defaultValue) {
        return !!this.getValue(key, defaultValue ? "y" : "n").toLowerCase().match(/^y|t|1/);
    }
    /**
     * Return value for specified option
     */
    getValue(key, defaultValue) {
        return this.provided(key) ? this.options[key] : defaultValue;
    }
}
exports.ProvisionerOptionsHandler = ProvisionerOptionsHandler;
