/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Download CA certificate to use database with SSL connections
 */
"use strict";
recipes.register({
    id: "download-database-ssl",
    on: { beforeInitialize: {} },
    conditions: {
        tierModules: { all: ["mysql-client"] },
        tierTags: { any: ["requires-database-ssl"] },
        platformTags: { any: ["linux"] },
    },
    recipeHandler: async function (input) {
        const ca_cert_url = "https://www.digicert.com/CACerts/BaltimoreCyberTrustRoot.crt.pem";
        const ca_cert_url_2020 = "https://cacerts.digicert.com/DigiCertGlobalRootG2.crt.pem";
        const ca_cert_destination = path.join(platform.pathInfo.namiAppPath, "/mysql/certs/");
        const ca_cert_file = path.join(ca_cert_destination, "ca.crt.pem");
        const ca_cert_file_2020 = path.join(ca_cert_destination, "ca.crt.2020.pem");
        // Download ca cert
        fs.mkdirSync(ca_cert_destination, { recursive: true });
        await utils.retry(() => {
            runProgram("curl", ["-Lo", ca_cert_file, ca_cert_url]);
        });
        await utils.retry(() => {
            runProgram("curl", ["-Lo", ca_cert_file_2020, ca_cert_url_2020]);
        });
        const ca_cert_file_2020_content = fs.readFileSync(ca_cert_file_2020).toString();
        fs.appendFileSync(ca_cert_file, `\n${ca_cert_file_2020_content}`);
    }
});
