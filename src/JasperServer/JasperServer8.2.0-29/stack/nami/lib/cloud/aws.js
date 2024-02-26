"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.AwsCloud = void 0;
const fs = require("fs");
const base_1 = require("./base");
const download_1 = require("../download");
const utils_1 = require("../utils");
const zlib_1 = require("zlib");
const run_program_1 = require("../run_program");
/**
 * Logic specific to AWS cloud
 */
class AwsCloud extends base_1.BaseCloud {
    constructor(options) {
        super(options);
        this._apiUrl = "http://169.254.169.254/2011-05-01";
        this._tokenUrl = "http://169.254.169.254/latest/api/token";
        this.cloudTags = this.cloudTags.concat([
            "aws",
            "hypervisor",
            "system-services"
        ]);
    }
    /**
     * For AWS, if PROVISIONER_CFN_INIT_ENABLED was passed in user data, call cfn-init and try to read
     * extra user-data files.
     */
    async getUserData(key, now) {
        if (now || !this._cloudUserData[key]) {
            let userdata = null;
            if (!this._extraUserDataFileContents) {
                await this._getExtraUserData();
            }
            if (this._extraUserDataFileContents && (this._extraUserDataFileContents !== "")) {
                userdata = this._getParameterFromUserData(this._extraUserDataFileContents, key);
            }
            this._cloudUserData[key] = userdata;
            if (this._cloudUserData[key]) {
                return this._cloudUserData[key];
            }
        }
        return super.getUserData(key, now);
    }
    /**
     * For AWS, download the user data script from metadata API
     */
    async _getUserDataScript() {
        if (!this._userDataScriptOnce) {
            this._userDataScriptOnce = await this._getUserDataScriptNow();
        }
        return this._userDataScriptOnce;
    }
    async _getUserDataScriptNow() {
        let token = await this._authToken();
        const dl = new download_1.Download({
            returnData: true,
            returnBuffer: true,
            ignoreErrors: true,
            headers: { "X-aws-ec2-metadata-token": token }
        });
        await dl.download(`${this._apiUrl}/user-data`);
        if (dl.statusCode < 400) {
            let body = dl.body;
            try {
                body = zlib_1.gunzipSync(dl.buffer).toString();
            }
            catch (e) {
                this.logger.debug("Unable to decompress user-data", e.message);
            }
            return body;
        }
        else {
            return undefined;
        }
    }
    /**
     * For AWS, get the data from user-data script
     */
    async _getUserDataNow(key) {
        const userDataScript = await this._getUserDataScript();
        if (!userDataScript) {
            return "";
        }
        return this._getParameterFromUserData(userDataScript, key);
    }
    /**
     * Read extra user-data passed from CFN init files. The file is deleted after saving the content
     */
    readExtraUserDataFile(extraUserDataFilePath) {
        this.logger.info(`Reading CFN user-data file ${extraUserDataFilePath}`);
        this._extraUserDataFileContents = fs.readFileSync(extraUserDataFilePath).toString();
        try {
            this.logger.debug(`Deleting CFN user-data file ${extraUserDataFilePath}`);
            fs.rmSync(extraUserDataFilePath);
        }
        catch (e) {
            throw new Error(`Unable to delete CFN user-data file: ${e.message}`);
        }
    }
    /**
     * If PROVISIONER_CFN_INIT_ENABLED was passed in user data, call cfn-init and try to read
     * extra user-data files.
     */
    async _getExtraUserData() {
        if (await this._getUserDataNow("PROVISIONER_CFN_INIT_ENABLED")) {
            try {
                run_program_1.runProgram("cfn-init", [
                    "--verbose",
                    "--stack", await this._getUserDataNow("PROVISIONER_CFN_STACK"),
                    "--resource", await this._getUserDataNow("PROVISIONER_CFN_INIT_RESOURCE"),
                    "--region", await this._getUserDataNow("PROVISIONER_CFN_REGION")
                ]);
            }
            catch (e) {
                throw new Error(`Unable to run cfn-init: ${e.message}`);
            }
            // if cfn-init created a user data file, read it
            const cfnUserDataFilePath = await this._getUserDataNow("PROVISIONER_CFN_INIT_FILE_PATH");
            if (fs.existsSync(cfnUserDataFilePath)) {
                this.readExtraUserDataFile(cfnUserDataFilePath);
            }
        }
    }
    async getDataDiskDetails() {
        let dataDiskPath = await this.getUserData("PROVISIONER_DATA_DISK");
        if (!(dataDiskPath && dataDiskPath.match(new RegExp("^/dev/")) && fs.existsSync(dataDiskPath))) {
            return undefined;
        }
        return {
            dataDiskName: undefined,
            dataDiskPartition: dataDiskPath
        };
    }
    /**
     * Get instance identity document and parse it as JSON
     */
    _getInstanceIdentityDocument() {
        if (!this._instanceIdentityDocumentOnce) {
            this._instanceIdentityDocumentOnce = this._getInstanceIdentityDocumentNow();
        }
        return this._instanceIdentityDocumentOnce;
    }
    async _getInstanceIdentityDocumentNow() {
        let token = await this._authToken();
        const dl = new download_1.Download({
            returnData: true,
            ignoreErrors: true,
            headers: { "X-aws-ec2-metadata-token": token }
        });
        await dl.download(`${this._apiUrl}/dynamic/instance-identity/document`);
        return JSON.parse(dl.body);
    }
    async _getMetaDataNow(key) {
        if (key === "account-id") {
            const data = await this._getInstanceIdentityDocument();
            return data.accountId || "";
        }
        else {
            return super._getMetaDataNow(key);
        }
    }
    async _authToken() {
        let curlArgs = ["-X", "PUT", "-H", "X-aws-ec2-metadata-token-ttl-seconds: 21600", this._tokenUrl];
        let token = "";
        await utils_1.retry(() => {
            token = run_program_1.runProgram("curl", curlArgs);
        });
        return token;
    }
}
exports.AwsCloud = AwsCloud;
;
