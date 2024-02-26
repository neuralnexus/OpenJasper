/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
/// <reference path="../../typings-recipe.d.ts" />
/*
 * Configure SSH server and client
 */
"use strict";
function getSSHConfigFile(name) {
    return fs.existsSync(`/etc/ssh/${name}`) ? `/etc/ssh/${name}` : `/etc/${name}`;
}
recipes.register({
    id: "ssh-settings",
    on: { provisionMachine: { depends: ["system-packages"] } },
    conditions: {
        platformTags: { any: ["linux"] },
    },
    recipeHandler: input => {
        const sshdConfigFile = getSSHConfigFile("sshd_config");
        const sshConfigfile = getSSHConfigFile("ssh_config");
        const clientAliveIntervalText = "\nClientAliveInterval 180\n";
        const excludedCiphers = [
            "arcfour",
            "arcfour256",
            "arcfour128",
            "3des-cbc",
            "aes128-cbc",
            "aes192-cbc",
            "aes256-cbc",
            "blowfish-cbc",
            "cast128-cbc",
            "rijndael-cbc@lysator.liu.se",
        ];
        const enabledCiphers = [
            "aes128-ctr",
            "aes192-ctr",
            "aes256-ctr",
            "aes128-gcm@openssh.com",
            "aes256-gcm@openssh.com",
            "chacha20-poly1305@openssh.com",
        ];
        // remove existing parameters
        runProgram("sed", ["-i",
            "-e", "s/^\\s*ClientAliveInterval/#ClientAliveInterval/g",
            "-e", "s/^\\s*Ciphers/#Ciphers/g",
            sshdConfigFile]);
        // add ClientAliveInterval setting
        fs.appendFileSync(sshdConfigFile, clientAliveIntervalText);
        // configure UseReoaming no for all hosts
        fs.appendFileSync(sshConfigfile, "\nHost *\n    UseRoaming no\n");
        // configure ciphers
        const availableCiphers = runProgram("ssh", [
            "-Q", "cipher"
        ]).split(/[\s,]+/).filter(cipher => {
            return ((cipher.length > 0) && (excludedCiphers.indexOf(cipher) < 0));
        });
        // detect ciphers that are not in the allowed list
        const unknownCiphers = availableCiphers.filter(cipher => {
            return (enabledCiphers.indexOf(cipher) < 0);
        });
        let resultCiphers = enabledCiphers.filter(cipher => {
            return (availableCiphers.indexOf(cipher) >= 0);
        });
        fs.appendFileSync(sshdConfigFile, `\nCiphers ${resultCiphers.join(",")}\n`);
        if (unknownCiphers.length > 0) {
            throw new Error(`Unknown SSH cipher(s): ${unknownCiphers.join(" ")}`);
        }
    }
});
recipes.register({
    id: "ssh-settings-password",
    on: { provisionMachine: { depends: ["system-packages", "ssh-settings"] } },
    conditions: {
        platformTags: { any: ["linux"] },
        cloudTags: { not: { any: ["azure"] } }
    },
    recipeHandler: input => {
        const sshdConfigFile = getSSHConfigFile("sshd_config");
        const passwordAuthenticationText = "\nPasswordAuthentication no\n";
        // remove existing parameters
        runProgram("sed", ["-i", "/PasswordAuthentication/d", sshdConfigFile]);
        // add ClientAliveInterval setting
        fs.appendFileSync(sshdConfigFile, passwordAuthenticationText);
    }
});
