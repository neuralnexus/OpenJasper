"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.GceCloud = void 0;
const fs = require("fs");
const path = require("path");
const base_1 = require("./base");
const download_1 = require("../download");
/**
 * Logic specific to Google Compute Engine cloud
 */
class GceCloud extends base_1.BaseCloud {
    constructor(options) {
        super(options);
        this._apiUrl = "http://metadata.google.internal/computeMetadata/v1";
        this.cloudTags = this.cloudTags.concat([
            "gce",
            "hypervisor",
            "system-services",
            "include-licenses"
        ]);
    }
    async getComputeMetadata(key) {
        const dl = new download_1.Download({
            returnData: true,
            ignoreErrors: true,
            headers: {
                "Metadata-Flavor": "Google"
            }
        });
        await dl.download(`${this._apiUrl}/${key}`);
        if (dl.statusCode < 400) {
            return dl.body;
        }
        else {
            return undefined;
        }
    }
    /**
     * Download the user data as attribute from metadata API
     */
    async _getUserDataNow(key) {
        return this.getComputeMetadata(`instance/attributes/${key}`);
    }
    ;
    /**
     * Determines which disk should be used as data disk
     * For Gce this is done by getting the `PROVISIONER_DATA_DISK` attribute and finding the disk
     * fallbacks in the future may include querying
     */
    async getDataDiskDetails() {
        let dataDisk = await this.getUserData("PROVISIONER_DATA_DISK");
        let dataDiskLink;
        const diskById = "/dev/disk/by-id";
        const dataDiskPath = `${diskById}/google-${dataDisk}`;
        if (!dataDisk) {
            return undefined;
        }
        if (!fs.existsSync(dataDiskPath)) {
            return undefined;
        }
        dataDiskLink = path.join(diskById, fs.readlinkSync(dataDiskPath));
        return {
            dataDiskName: undefined,
            dataDiskPartition: dataDiskLink
        };
    }
    async _getMetaDataNow(key) {
        if (key === "account-id") {
            const id = await this.getComputeMetadata("project/numeric-project-id");
            return id || "";
        }
        else {
            return super._getMetaDataNow(key);
        }
    }
}
exports.GceCloud = GceCloud;
;
