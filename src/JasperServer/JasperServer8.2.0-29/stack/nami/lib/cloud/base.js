"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.BaseCloud = void 0;
const fs = require("fs");
const download_1 = require("../download");
/**
 * Implementation of `Cloud` interface that provides helpers for common operations ;
 * should not be created directly but via the `createCloud` function
 */
class BaseCloud {
    constructor(options) {
        this.name = options.cloudName;
        this.logger = options.logger;
        this.platform = options.platform;
        this.cloudTags = [];
        this._cloudUserData = {};
        this._cloudMetaData = {};
        this._accountId = options.accountId || "";
        this.ephemeralVolumePath = options.ephemeralVolumePath || "/mnt";
    }
    /**
     * Get user-data passed when creating the machine, getting specific field
     * for clouds that support key-value storage this uses the said storage, for clouds that pass
     * init script, it looks for `KEY=value` or `# KEY=value` in the user data and parses it ;
     * unless `now` is set to truthy value, the data can be cached and re-used
     * @param key Name of the field to retrieve
     * @param now If set to truthy value, do not use cached results
     */
    async getUserData(key, now) {
        if (now || !this._cloudUserData[key]) {
            this._cloudUserData[key] = this._getUserDataNow(key);
        }
        return this._cloudUserData[key];
    }
    async _getUserDataNow(key) {
        return undefined;
    }
    /**
     * Get user-data specific boolean field, returns true or false
     * @param key Name of the field to retrieve
     * @param now If set to truthy value, do not use cached results
     */
    async getUserDataBoolean(key, now) {
        return ["yes", "true", "y", "1"].indexOf((await this.getUserData(key, now) || "").toString().toLowerCase()) >= 0;
    }
    /**
     * Get the user-data script and match the environment variables. It also matches
     * commented variables with one '#'
     */
    async getAllUserDataVariables() {
        const regex = /^\s*export\s+(\w+=.*)$/gm;
        const script = (await this._getUserDataScript()) || "";
        const vars = script.match(regex) || [];
        return vars.map(v => v.replace(regex, "$1").trim());
    }
    /**
     * Return the raw user-data. This method should be implemented per cloud if supported
     */
    async _getUserDataScript() {
        return "";
    }
    /**
     * Method to parse user-data script and get the parameter from it
     * this method parses the script line by line and looks for `{{key}}=value`,
     * optionally prefixed by `#` and whitespace
     * @param script The user data script to parse
     * @param key The key to look for
     * @param value If specified, this is the default value returned
     */
    _getParameterFromUserData(script, key, value) {
        const keyRegExp = key.replace(/\./, "\\\\.");
        for (const l of script.split("\n")) {
            const match = l.match(`^(#|)\\s*${keyRegExp}\\s*=\\s*(.*)\\s*$`);
            if (match) {
                value = match[2];
                // remove quotes around the value
                if (((value[0] === "\"") && (value[value.length - 1] === "\""))
                    || ((value[0] === "'") && (value[value.length - 1] === "'"))) {
                    value = value.substr(1, value.length - 2);
                }
            }
        }
        return value;
    }
    /**
     * Get meta-data from the cloud, using one of predefined keys
     * unless `now` is set to truthy value, the data can be cached and re-used
     * @param key Name of the meta-data to get
     * @param now If set to truthy value, do not use cached results
     */
    async getMetaData(key, now) {
        if (now || !this._cloudMetaData[key]) {
            this._cloudMetaData[key] = this._getMetaDataNow(key);
        }
        return this._cloudMetaData[key];
    }
    async _getMetaDataNow(key) {
        if (key === "public-ipv4") {
            return this._getPublicIP();
        }
        else if (key === "public-hostname") {
            return this._getPublicIP();
        }
        else if (key === "server-domain") {
            return this._getPublicIP();
        }
        else if (key === "private-ipv4") {
            return this._getPrivateIP();
        }
        else if (key === "instance-type") {
            return "unknown";
        }
        else if (key === "account-id") {
            return this._accountId;
        }
        else {
            this.logger.warn(`Unknown metadata queried: ${key}`);
            return undefined;
        }
    }
    /**
     * By default data disk handling is disabled, it has to be implemented per cloud
     */
    async getDataDiskDetails() {
        return undefined;
    }
    async _getPublicIP() {
        let result;
        for (let timeout of [5000, 15000, 25000]) {
            for (let url of ["http://myip.bitnami.com", "http://myip2.bitnami.com"]) {
                const dl = new download_1.Download({
                    returnData: true,
                    timeout: timeout
                });
                try {
                    this.logger.trace(`Getting public IP using ${url} and timeout of ${timeout}`);
                    result = (await dl.download(url)).body.replace(/\s+/g, "");
                }
                catch (e) {
                    this.logger.warn(`Unable to resolve IP address using ${url}`, e);
                }
                if (result) {
                    return result;
                }
            }
        }
        throw new Error("Unable to resolve IP address using any available options");
    }
    async _getPrivateIP() {
        return (await this.platform.getPrivateIPAddresses())[0];
    }
    /**
     * Write to the cloud console, if available ; assumes an async functionality as some couds
     * may require networking for this
     * @param text to write to the console ; it will have newline character appended at the end
     */
    async writeToConsole(text) {
        fs.appendFileSync("/dev/console", `${text}\n`);
    }
    /**
     * Determine whether this deployment is part of Bitnami tests
     */
    async isBitnamiTestingMode() {
        let rc = !!process.env["BITNAMI_TESTING_MODE"];
        if (!rc) {
            rc = await this.getUserDataBoolean("bitnami_testing_mode");
        }
        return rc;
    }
    /**
     * Determine whether this deployment is part of an image customization
     */
    async isCustomizingMode() {
        return await this.getUserDataBoolean("SKIP_FIRST_BOOT");
    }
    /**
    * If necessary, the method has to be implemented per cloud
    */
    async createSharedDisk(options) {
        return undefined;
    }
}
exports.BaseCloud = BaseCloud;
;
