/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Use data disk for /bitnami volume, if disk is specified
 *
 * This script detects whether an additional disk or partition was provided
 * for storing the application data in /bitnami folder ; if it is, it will
 * create partition, create filesystem and then mount it, copying existing
 * data to new disk.
 */
"use strict";
recipes.register({
    id: "data-disk",
    on: { beforeInitialize: {}, beforeStart: {} },
    conditions: {
        ifChanged: (input) => {
            return platform.getUniqueBootId();
        }
    },
    recipeHandler: async function (input) {
        let dataDiskDetails = await provisioner.cloud.getDataDiskDetails();
        // set xfs for mongodb
        let fileSystemType = (provisioner.stackDefinition.details.key === "mongodb") ?
            "xfs" : "ext4";
        if (dataDiskDetails) {
            // initialize disk partition if disk was specified
            if (dataDiskDetails.dataDiskName) {
                if (!fs.existsSync(dataDiskDetails.dataDiskPartition)) {
                    let dataDiskDevice = platform.getDiskDevice(dataDiskDetails.dataDiskName);
                    logger.info(`Creating data disk partition using filesystem type ${fileSystemType}`);
                    await dataDiskDevice.setPartitions([
                        {
                            // ran into issues with GPT partition tables not supporting creation
                            // of partitions until very end, leave 1MB unused
                            start: 2048, end: (dataDiskDevice.diskSize - 1024),
                            number: 1, type: fileSystemType,
                        }
                    ]);
                    logger.info("Creating data disk partition filesystem");
                }
                else {
                    logger.info("Found data disk partition");
                }
            }
            logger.info("Mounting data disk filesystem");
            let dataRestored = false;
            try {
                platform.mount({
                    device: dataDiskDetails.dataDiskPartition,
                    mountPoint: platform.pathInfo.namiDataPath,
                    type: fileSystemType
                });
                dataRestored = true;
            }
            catch (e) {
                const temporaryMountPoint = `${platform.pathInfo.namiDataPath}.tmp`;
                logger.info(`Unable to mount data disk - creating ${fileSystemType} filesystem`);
                await utils.retry(() => {
                    runProgram(`mkfs.${fileSystemType}`, [dataDiskDetails.dataDiskPartition]);
                });
                logger.info("Copying existing data to volume");
                platform.mount({
                    device: dataDiskDetails.dataDiskPartition,
                    mountPoint: temporaryMountPoint,
                    type: fileSystemType
                });
                runProgram("cp", [
                    "-pfR", `${platform.pathInfo.namiDataPath}/.`, temporaryMountPoint
                ]);
                logger.info("Re-mounting data");
                runProgram("umount", [temporaryMountPoint]);
                fs.rmSync(temporaryMountPoint, { recursive: true });
                platform.mount({
                    device: dataDiskDetails.dataDiskPartition,
                    mountPoint: platform.pathInfo.namiDataPath,
                    type: fileSystemType
                });
            }
            // Create .restored file if the volume contains any persisted data
            // Keep out of the try/catch block to avoid formatting in the scenario where touching the file fails
            // Used in recipes to allow detecting if the solution has already been initialized in a previous deployment
            if (dataRestored)
                fs.writeFileSync(path.join(platform.pathInfo.namiDataPath, ".restored"), "");
        }
        else {
            logger.info("Data disk not present");
        }
    }
});
