/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Support for sending signal when ran using CloudFormation templates
 */
"use strict";
recipes.register({
    id: "aws-cfn-signal-install",
    on: { provisionMachine: { depends: ["system-packages"] } },
    conditions: {
        cloudTags: { any: ["aws"] },
        platformTags: { any: ["linux"] }
    },
    recipeHandler: async function (input) {
        platform.installPackages("python3-pip", "python3-setuptools");
        await utils.retry(async () => {
            runProgram("pip3", [
                "install",
                "https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-py3-2.0-10.tar.gz"
            ]);
        }, { logger, errorMessage: "Unable to install AWS CFN tools" });
    }
});
recipes.register({
    id: "aws-cfn-signal",
    on: { afterFirstboot: {}, afterFailedFirstboot: {} },
    conditions: {
        cloudTags: { any: ["aws"] },
        platformTags: { any: ["linux"] },
        // don't run if the machine was provisioned externally
        shouldInvoke: (input) => !(input.provisioner.provisioned)
    },
    recipeHandler: async function (input) {
        const resource = await cloud.getUserData("PROVISIONER_CFN_RESOURCE");
        const stack = await cloud.getUserData("PROVISIONER_CFN_STACK");
        const region = await cloud.getUserData("PROVISIONER_CFN_REGION");
        const signal = (input.eventName === "afterFirstboot") ? 0 : 1;
        if (resource && stack) {
            logger.info(`Notifying boot status from ${input.eventName}, code ${signal}`);
            try {
                runProgram("cfn-signal", ["-e", signal, "--stack", stack, "--resource", resource, "--region", region]);
            }
            catch (e) {
                logger.warn(`Unable to send signal to CloudFormation: ${e.message}; retrying`, e);
                try {
                    runProgram("cfn-signal", ["-e", signal, "--stack", stack, "--resource", resource, "--region", region]);
                }
                catch (e) {
                    logger.warn(`Unable to send signal to CloudFormation: ${e.message}; giving up`, e);
                }
            }
        }
    }
});
