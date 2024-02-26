"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.DebianLinuxPlatform = void 0;
const fs = require("fs");
const linux_1 = require("./linux");
const run_program_1 = require("../run_program");
const utils_1 = require("../utils");
const init_system_1 = require("./init_system");
/**
 * Class for handling Debian Linux distributions
 */
class DebianLinuxPlatform extends linux_1.LinuxPlatform {
    constructor(options) {
        super(options);
        this.packagesToHold = [];
        this.platformTags.push("debian");
        this.systemPackages.push("acl", // required for Blacksmith Bash VMs persistence library
        "python", // should be installed to execute GCE startup scripts
        "sudo", "at", "less", "vim", "dnsutils", "build-essential", "libtool", "autoconf", "zlib1g-dev", "libxt6", "libxext6", "libxrender1", "libsm6", "libice6", "fontconfig", "libcups2", "libdbus-glib-1-2", "apt-transport-https", "ca-certificates", "locales");
    }
    /**
     * Debian distro identifier
     */
    get distro() {
        return "debian";
    }
    /**
     * Debian architecture name
     */
    get osArch() {
        return "linux-amd64";
    }
    cleanSystemPackages() {
        utils_1.retry(() => {
            run_program_1.runProgram("env", ["DEBIAN_FRONTEND=noninteractive",
                "apt-get", "clean"
            ]);
        }, { logger: this.logger, errorMessage: "Unable to clean system packages" });
    }
    installAndUpgradeSystemPackages(packages, upgrade = true) {
        let installedPackagesToHold = [];
        if (upgrade) {
            utils_1.retry(() => {
                run_program_1.runProgram("env", ["DEBIAN_FRONTEND=noninteractive",
                    "apt-get", "update"
                ]);
            }, { logger: this.logger, errorMessage: "Unable to update system packages" });
        }
        for (let packageName of this.packagesToHold) {
            this.logger.debug(`Checking package ${packageName} as to be kept back`);
            if (this._systemPackageInstalled(packageName)) {
                installedPackagesToHold.push(packageName);
            }
        }
        if (installedPackagesToHold.length > 0) {
            this.logger.info(`Marking package(s) ${installedPackagesToHold.join(" ")} to be kept back`);
            run_program_1.runProgram("apt-mark", ["hold"].concat(installedPackagesToHold));
        }
        else {
            this.logger.debug("Not marking any packages to be kept back");
        }
        if (upgrade) {
            utils_1.retry(() => {
                run_program_1.runProgram("env", ["DEBIAN_FRONTEND=noninteractive", "UCF_FORCE_CONFFNEW=1",
                    "apt-get", "-o Dpkg::Options::=\"--force-confnew\"", "-y", "dist-upgrade"
                ]);
            }, { logger: this.logger, errorMessage: "Unable to upgrade system packages" });
        }
        if (installedPackagesToHold.length > 0) {
            this.logger.info(`No longer markng package(s) ${installedPackagesToHold.join(" ")} to be kept back`);
            run_program_1.runProgram("apt-mark", ["unhold"].concat(installedPackagesToHold));
        }
        utils_1.retry(() => {
            this.installPackages(...packages);
        }, { logger: this.logger, errorMessage: "Unable to install system packages" });
        this._installLanguagePack();
    }
    installPackages(...packages) {
        if (packages.length > 0) {
            run_program_1.runProgram("env", ["DEBIAN_FRONTEND=noninteractive",
                "apt-get", "-y", "install"
            ].concat(packages));
        }
    }
    /**
     * Install language pack for Linux distributions that have the package ; for some Debian
     * flavors it is not available while it is not available in others (i.e. AWS vs Google)
     */
    _installLanguagePack() {
        if (this._systemPackageAvailable("language-pack-en")) {
            try {
                utils_1.retry(() => {
                    this.installPackages("language-pack-en");
                }, { logger: this.logger, errorMessage: "Unable to configure language pack" });
            }
            catch (e) {
                this.logger.warn("Unable to configurage language pack ; giving up");
            }
        }
        run_program_1.runProgram("sed", ["-i", "s/\\s*#\\s*en_US.UTF-8/en_US.UTF-8/g", "/etc/locale.gen"]);
        run_program_1.runProgram("locale-gen", ["en_US.UTF-8"]);
        run_program_1.runProgram("update-locale", [`LANG="en_US.UTF-8"`, `LANGUAGE="en_US.UTF-8"`, `LC_ALL="en_US.UTF-8"`]);
        if (!fs.readFileSync("/etc/default/locale").toString().match("en_US.UTF-8")) {
            throw new Error("Locale not configured");
        }
        this.logger.debug("Locale configured");
    }
    _systemPackageInstalled(packageName) {
        const runProgramResult = run_program_1.runProgram("dpkg", ["--status", packageName], {
            retrieveStdStreams: true
        });
        return !!((runProgramResult.code === 0) && runProgramResult.stdout.match(/Status:\s*install/));
    }
    _systemPackageAvailable(packageName) {
        const runProgramResult = run_program_1.runProgram("apt-cache", ["show", packageName], {
            retrieveStdStreams: true
        });
        return !!((runProgramResult.code === 0) && runProgramResult.stdout.match(new RegExp(`Package:\\s*${packageName}`)));
    }
    /**
     * Set contents in hosts file, but also in cloud-init template
     * @param contents Map of IP address to hostname or list of host names
     */
    setHostsFileItems(contents) {
        // Override cloud-init template
        if (fs.existsSync("/etc/cloud/templates/hosts.debian.tmpl")) {
            this.createFileSection("/etc/cloud/templates/hosts.debian.tmpl", this._fileSectionForHostsFile(contents));
        }
        // And then the file directly
        super.setHostsFileItems(contents);
    }
    getInitSystem() {
        if (!this.initSystem) {
            this.initSystem = new init_system_1.SysVInitSystem();
        }
        return this.initSystem;
    }
    openPorts(ports, onlyFromHosts = null) {
        throw new Error("Method not implemented.");
    }
}
exports.DebianLinuxPlatform = DebianLinuxPlatform;
