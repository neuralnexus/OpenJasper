"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.LinuxDiskDevice = void 0;
const fs = require("fs");
const run_program_1 = require("../run_program");
const utils_1 = require("../utils");
class LinuxDiskDevice {
    /**
     * @param platform Platform instance for performing certain operations
     * @param device Name of the disk device
     */
    constructor(platform, device) {
        this.platform = platform;
        this.device = device;
        // this._parseProcPartitions();
        this._parsePartedOutput();
        this.logger = platform.logger;
        if (!this.diskSize) {
            this._parseProcPartitions();
        }
    }
    _parseProcPartitions() {
        const output = fs.readFileSync("/proc/partitions").toString();
        const deviceTail = this.device.match(/^.*\/(.*?)$/)[1];
        this.diskSize = 0;
        for (const line of output.split("\n")) {
            const match = line.match(/\s*([0-9]+)\s*([0-9]+)\s*([0-9]+)\s*(.*?)\s*$/);
            if (match) {
                const device = match[4];
                if (device === deviceTail) {
                    this.diskSize = parseInt(match[3]);
                }
            }
        }
        if (!this.diskSize) {
            throw new Error(`Unable to find device ${deviceTail} in /proc/partitions`);
        }
    }
    _parsePartedOutput(ignorePartitions) {
        let partitions = [];
        const result = run_program_1.runProgram("parted", [
            this.device, "unit", "B", "print"
        ], {
            retrieveStdStreams: true
        });
        this.partitionTable = undefined;
        if (result.code === 0) {
            const lines = result.stdout.split("\n");
            let isPartitionList = false;
            for (const line of lines) {
                let match;
                if (match = line.match(/^\s*Disk (.*?): ([0-9]+)k/)) {
                    this.diskSize = Math.round(parseInt(match[2]) / 1024);
                }
                else if (match = line.match(/^\s*Partition Table:\s*(.*)(\s|$)/)) {
                    if (match[1] !== "unknown") {
                        this.partitionTable = match[1];
                    }
                }
                else if (match = line.match(/^\s*Number\s/)) {
                    isPartitionList = true;
                }
                else if (isPartitionList) {
                    if (match = line.match("\\s*([0-9]+)\\s*([0-9]+)[A-Za-z]+\\s*" +
                        "([0-9]+)[A-Za-z]+\\s*" +
                        "([0-9]+)[A-Za-z]+\\s+(.*?)\\s+(.*)$")) {
                        partitions.push({
                            number: parseInt(match[1]),
                            start: Math.floor(parseInt(match[2]) / 1024),
                            end: Math.floor((parseInt(match[3]) + 1) / 1024),
                            type: match[5],
                            bootable: !!(match[6].match(/boot/))
                        });
                    }
                }
            }
        }
        if (!ignorePartitions) {
            this.partitions = partitions;
        }
    }
    _reloadPartitionTable() {
        const partprobeResult = run_program_1.runProgram("partprobe", [this.device], {
            retrieveStdStreams: true
        });
        if (partprobeResult.code !== 0) {
            this.logger.warn("Running partprobe failed; unable to reload list of partitions", partprobeResult.stdout + partprobeResult.stderr);
            return false;
        }
        else {
            return true;
        }
    }
    _hasPartitionChanged(a, b) {
        // if any object is null/undefined, it is a change
        if ((!a && b) || (a && !b)) {
            return true;
        }
        return ((a.number !== b.number)
            || (a.start !== b.start)
            || (a.end !== b.end)
            || (a.type !== b.type)
            || (a.bootable !== b.bootable));
    }
    _canResizePartition(a, b) {
        // if any object is null/undefined, it is a change
        if ((!a && b) || (a && !b)) {
            return false;
        }
        return ((a.number === b.number)
            && (a.start === b.start)
            && (a.type === b.type)
            && (b.end >= a.end));
    }
    async _setPartitionsUsingParted(partitions, allowDelete) {
        let partitionsChanged = false;
        let oldPartitions = {};
        let newPartitions = {};
        for (const partition of this.partitions) {
            oldPartitions[partition.number] = partition;
        }
        for (const partition of partitions) {
            newPartitions[partition.number] = partition;
        }
        if (!this.partitionTable) {
            run_program_1.runProgram("parted", [
                this.device, "mklabel", "gpt"
            ]);
            await utils_1.retry(async () => {
                this._parsePartedOutput(true);
                if (!this.partitionTable) {
                    throw new Error("Unable to re-read proper partition setup");
                }
            }, {
                attempts: 3,
                delay: 5,
                logger: this.logger,
                errorMessage: "Unable to configure partition table"
            });
        }
        for (const partition of this.partitions) {
            if (this._hasPartitionChanged(partition, newPartitions[partition.number])) {
                if (this._canResizePartition(partition, newPartitions[partition.number])) {
                    const end = `${newPartitions[partition.number].end * 1024 - 1}B`;
                    this.platform.logger.debug("Resizing partition", partition.number, "on device", this.device);
                    // see https://bugs.launchpad.net/ubuntu/+source/parted/+bug/1270203
                    // for more details on why "yes" and "---pretend-input-tty" is here
                    run_program_1.runProgram("parted", [
                        this.device, "---pretend-input-tty", "resizepart", partition.number,
                        "yes", end
                    ]);
                    if (!!(partition.bootable) !== !!(newPartitions[partition.number].bootable)) {
                        run_program_1.runProgram("parted", [
                            this.device, "toggle", partition.number, "boot"
                        ]);
                    }
                    oldPartitions[partition.number] = newPartitions[partition.number];
                }
                else {
                    if (!allowDelete) {
                        const msg = "Unable to delete partition with invalid settings";
                        this.platform.logger.warn(msg);
                        throw new Error(msg);
                    }
                    this.platform.logger.debug("Deleting partition", partition.number, "on device", this.device);
                    run_program_1.runProgram("parted", [
                        this.device, "rm", partition.number, "yes"
                    ]);
                }
                partitionsChanged = true;
            }
        }
        // delay to ensure partitions are re-read if they have changed
        if (partitionsChanged) {
            await utils_1.delayMs(5000);
        }
        for (const partition of partitions) {
            if (this._hasPartitionChanged(partition, oldPartitions[partition.number])) {
                this.platform.logger.debug("Creating partition", partition.number, "on device", this.device);
                run_program_1.runProgram("parted", [
                    this.device, "mkpart", "primary", partition.type,
                    `${partition.start * 1024}B`,
                    `${partition.end * 1024 - 1}B`
                ]);
                if (partition.bootable) {
                    run_program_1.runProgram("parted", [
                        this.device, "toggle", partition.number, "boot"
                    ]);
                }
            }
        }
        this._reloadPartitionTable();
        this._parsePartedOutput();
    }
    /**
     * Configure new list of partitions and apply changes
     * @param partitions list of new partitions
     * @param allowDelete Whether code should delete partitions already existing or fail
     */
    async setPartitions(partitions, allowDelete) {
        await this._setPartitionsUsingParted(partitions, allowDelete);
    }
}
exports.LinuxDiskDevice = LinuxDiskDevice;
