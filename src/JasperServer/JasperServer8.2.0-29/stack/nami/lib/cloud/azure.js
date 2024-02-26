"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.AzureCloud = void 0;
const fs = require("fs");
const path = require("path");
const xpath = require("xpath");
const glob = require("glob");
const base_1 = require("./base");
const run_program_1 = require("../run_program");
const utils_1 = require("../utils");
const xmldom_1 = require("xmldom");
/**
 * Logic specific to Azure cloud (both ARM and ASM)
 */
class AzureCloud extends base_1.BaseCloud {
    constructor(options) {
        super(options);
        this.cloudTags = this.cloudTags.concat([
            "azure",
            "hypervisor",
            "system-services",
            "no-image"
        ]);
    }
    _getAzureDiskMapping() {
        const azureDiskPath = "/dev/disk/azure";
        let result = {};
        if (fs.existsSync(azureDiskPath)) {
            const matches = glob.sync("*", { cwd: azureDiskPath });
            this.logger.trace("getAzureDiskMapping: listing matches", () => {
                return {
                    azureDiskPath: azureDiskPath,
                    matches: matches
                };
            });
            for (const name of matches) {
                const namePath = path.join(azureDiskPath, name);
                if (fs.lstatSync(namePath).isSymbolicLink()) {
                    const diskPath = fs.readlinkSync(namePath);
                    const diskFullPath = path.join(azureDiskPath, diskPath);
                    result[path.normalize(diskFullPath)] = name;
                }
            }
        }
        return result;
    }
    /**
     * Determines which disk should be used as data disk
     * For Azure, this is done by getting all disks in `/dev/disk/azure` and then
     * finding out first available disk device that is not used by the said disks
     */
    async getDataDiskDetails() {
        const azureDiskMapping = this._getAzureDiskMapping();
        let dataDiskDeviceName;
        // detect first disk that is not a part of Azure setup
        // (i.e. skipping root disk and any local disk)
        for (const disk of this.platform.getDiskDeviceNames()) {
            if (!azureDiskMapping[disk]) {
                dataDiskDeviceName = disk;
                break;
            }
        }
        // return undefined if no data disk was found
        if (!dataDiskDeviceName) {
            return undefined;
        }
        return {
            dataDiskName: dataDiskDeviceName,
            dataDiskPartition: this.platform.getDiskPartitionName(dataDiskDeviceName, 1)
        };
    }
    /**
     * Parses the user-data file into a DOM.
     */
    _getUserDataDom() {
        const filePath = "/var/lib/waagent/ovf-env.xml";
        if (!fs.existsSync(filePath)) {
            this.logger.error(`Not able to read ${filePath}`);
            return "";
        }
        let fileContent = fs.readFileSync(filePath).toString();
        return new xmldom_1.DOMParser().parseFromString(fileContent);
    }
    /**
     * Provides an XPATH selector that defines the namespaces that are used to
     * retrieve data from the user-data file.
     */
    _getXpathSelector() {
        return xpath.useNamespaces({
            "ns0": "http://schemas.dmtf.org/ovf/environment/1",
            "ns1": "http://schemas.microsoft.com/windowsazure"
        });
    }
    /**
     * Get user-data passed when creating the machine.
     * It just returns the user-data content as it is.
     */
    async _getUserDataScript() {
        let doc = this._getUserDataDom();
        let select2 = this._getXpathSelector();
        let b64raw = select2("//ns1:CustomData/text()", doc)[0];
        if (b64raw === undefined) {
            return "";
        }
        return new Buffer(b64raw.data, "base64").toString("ascii");
    }
    /**
     * Retrieves the username used to access the machine.
     * Note that this function is not asynchronous and does not use _getUserDataNow(k).
     */
    getUserName() {
        let doc = this._getUserDataDom();
        let xpathSelector = this._getXpathSelector();
        let userNameField = xpathSelector("//ns1:UserName/text()", doc)[0];
        return userNameField.data;
    }
    /**
     * For Azure, get the data from user-data script
     */
    async _getUserDataNow(key) {
        let r = await this._getUserDataScript();
        if (!r) {
            return "";
        }
        return this._getParameterFromUserData(r, key);
    }
    /**
     * Creates a shared disk to be mounted in multiple instances.
     * For Azure, this is necessary to be done calling the API, not possible from the template.
     */
    async createSharedDisk(azureFileShareName, options) {
        if (typeof options.storageAccountName === "undefined" || typeof options.storageAccountKey === "undefined") {
            throw new Error("Storage account name or key are not defined");
        }
        const azStorage = (cmd, name) => {
            let res = {};
            try {
                res = run_program_1.runProgram("az", [
                    "storage", "share", cmd,
                    "--name", name,
                    "--account-name", options.storageAccountName,
                    "--account-key", options.storageAccountKey
                ]);
            }
            catch (e) {
                throw new Error(`Unable to connect to the Azure API: ${e}`);
            }
            return JSON.parse(res);
        };
        await utils_1.retry(() => {
            if (azStorage("exists", azureFileShareName).exists) {
                this.logger.debug(`The file share ${azureFileShareName} already exists in ${options.storageAccountName}`);
            }
            else {
                if (!azStorage("create", azureFileShareName).created) {
                    throw new Error(`The file share ${azureFileShareName} could not be created`);
                }
            }
        }, {
            attempts: 2,
            delay: 2,
        });
        return {
            device: `//${options.storageAccountName}.file.core.windows.net/${azureFileShareName}`,
            mountPoint: undefined,
            type: "cifs",
            options: {
                username: options.storageAccountName,
                password: options.storageAccountKey,
                guest: true
            }
        };
    }
}
exports.AzureCloud = AzureCloud;
;
