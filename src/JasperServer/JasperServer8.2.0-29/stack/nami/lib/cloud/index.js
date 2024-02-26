"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.createCloud = void 0;
const container_1 = require("./container");
const azure_1 = require("./azure");
const aws_1 = require("./aws");
const gce_1 = require("./gce");
const cloudMap = {
    container: container_1.ContainerCloud,
    aws: aws_1.AwsCloud,
    amazon: aws_1.AwsCloud,
    gce: gce_1.GceCloud,
    azure: azure_1.AzureCloud,
};
/**
 * Create an instance of appropriate class implementing the `Cloud` interface ;
 * this should not be called directly, but only once by the `Provisioner` object
 */
function createCloud(options) {
    if (cloudMap[options.cloudName]) {
        return new cloudMap[options.cloudName](options);
    }
    else {
        options.logger.warn("No cloud or unknown cloud specified -", "falling back to container-based cloud logic");
        return new container_1.ContainerCloud(options);
    }
}
exports.createCloud = createCloud;
