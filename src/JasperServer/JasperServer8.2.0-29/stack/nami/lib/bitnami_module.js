"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.BitnamiModule = void 0;
const fs = require("fs");
const path = require("path");
const download_1 = require("./download");
const arguments_handler_1 = require("./arguments_handler");
const run_program_1 = require("./run_program");
const utils_1 = require("./utils");
const logger_1 = require("./logger");
/**
 * Generic bitnami module class
 */
class BitnamiModule {
    /**
     * Create instance of generic bitnami module handler
     * @param tierDefinition definition of current tier (part of multi-VM deployment)
     * @param moduleDefinition definition of this specific module
     */
    constructor(tierDefinition, moduleDefinition) {
        this.tierDefinition = tierDefinition;
        this.moduleDefinition = moduleDefinition;
        this.baseName = moduleDefinition.name + "-" + moduleDefinition.version;
        this.fullName = this.baseName + "-" + this.osArchDistro(tierDefinition.provisioner.platform);
        this.skipInitialize = !!moduleDefinition.skipInitialize;
        this.skipService = !!moduleDefinition.skipService;
        this.startAfterInitialize = !!moduleDefinition.startAfterInitialize;
        // Initialize Bash modules metadata
        this._installationRoot = this.provisioner.platform.pathInfo.namiAppPath;
        this._additionalFiles = moduleDefinition.additionalFiles || {};
        this._exports = moduleDefinition.exports || {};
        // We use ArgumentHandler to parse Provisioner variables such as appPassword
        this._environmentHandler = new arguments_handler_1.ArgumentHandler(moduleDefinition.environment || [], this.provisioner, this.logger);
    }
    /**
     * Url for downloading the bitnami module
     */
    get downloadUrl() {
        const downloadUrlOptionName = `${this.moduleDefinition.name}_nami_module_url`;
        if (this.provisioner.options.provided(downloadUrlOptionName)) {
            return this.provisioner.options.getValue(downloadUrlOptionName);
        }
        return this.moduleDefinition.tarballUrl || (`https://downloads.bitnami.com/files/stacksmith/${this.fullName}.tar.gz`);
    }
    /**
     * Instance of provisioner
     */
    get provisioner() {
        return this.tierDefinition.provisioner;
    }
    /**
     * Instance of logger
     */
    get logger() {
        return this.tierDefinition.provisioner.logger;
    }
    /**
     * Short name of the module - i.e. `wordpress`
     */
    get name() {
        return this.moduleDefinition.name;
    }
    /**
     * Version of the module - i.e. `4.5.2-0`
     */
    get version() {
        return this.moduleDefinition.version;
    }
    /**
     * Whether an object is a service and should be started/stopped when all services should be
     * stopped and started accordingly
     */
    get isService() {
        // Consider it includes a service if it exports the "start" method
        return !!this._exports["start"];
    }
    /**
     * List of environment variables used by the module
     */
    get environment() {
        if (!this._environment) {
            let args = this._environmentHandler.arguments;
            // Additional environment variables
            const distroName = `${this.provisioner.platform.distro}-${this.provisioner.platform.distroVersion}`;
            const osArch = this.provisioner.platform.osArch;
            const os = osArch.split("-")[0];
            const arch = osArch.split("-").slice(1).join("-");
            // Parse environment variables to be a key-value object
            // It needed to be in array format in order for ArgumentsHandler to support it
            this._environment = args.reduce(function (result, item, index, array) {
                const splittedEnvVar = item.split("=");
                const name = splittedEnvVar[0];
                const value = splittedEnvVar.slice(1).join("=");
                result[name] = value;
                return result;
            }, {
                PATH: `${this.provisioner.platform.getAdditionalSystemPaths().join(":")}:${process.env.PATH}`,
                OS_ARCH: arch,
                OS_FLAVOUR: distroName,
                OS_NAME: os,
            });
        }
        if (this.logger.logLevel >= logger_1.LogLevel.DEBUG) {
            this._environment.BITNAMI_DEBUG = "true";
            this.logger.debug(`Injecting the following environment: ${JSON.stringify(this._environment)}`);
        }
        return this._environment;
    }
    /**
     * Download a module from repository and unpack it in destination directory
     * @param destination Directory to unpack in, does not have to exist ;
     *                    as bitnami modules are shipped inside their directory,
     *                    the subdirectory will be created
     */
    async downloadAndUnpack(destination) {
        let result;
        fs.mkdirSync(destination, { recursive: true });
        await utils_1.retry(async () => {
            result = await new download_1.Download({
                unpackDirectory: destination,
                // Modules contain the relevant files at `<root-folder>/files/*` where `<root-folder>`
                // might be an arbitrary name.
                //
                // We strip the first two levels of nesting to ensure we uncompress the right content and
                // the rest of the logic will simply copy/move the content of the destination folder if
                // needed (see `installModuleFromDirectory` method).
                tarArguments: ["-xz", "--no-same-owner", "--strip-components=2"],
            }).download(this.downloadUrl);
        }, { logger: this.logger, errorMessage: "Unable to download bitnami module" });
    }
    /**
     * Install a module from directory where it was previously downloaded
     * (by provisioner or other tool)
     * @param directory Directory where module was downloaded to, NOT the subdirectory with module
     */
    installModuleFromDirectory(directory) {
        // The module has already been extracted, so we must simply copy the files to the installation
        // root directory.
        //
        // We use 'cp' with '-a' option to avoid converting hard links to files, which file.copy does
        // not currently support. This is the case for Git, where it causes issues with the output size
        // (>600M without hard links, 30M without).
        run_program_1.runProgram("cp", ["-R", "-a", `${directory}/.`, this._installationRoot], { logger: this.logger });
        // Create required files
        for (let filePath in this._additionalFiles) {
            this.logger.debug(`Creating ${filePath}`);
            fs.mkdirSync(path.dirname(filePath), { recursive: true });
            fs.writeFileSync(filePath, Buffer.from(this._additionalFiles[filePath].data.content, "base64").toString());
            fs.chmodSync(filePath, this._additionalFiles[filePath].data.mode);
        }
    }
    /**
     * Initialize a module ; arguments to pass to module are taken from its definition
     */
    initializeModule() {
        this.runExport("setup");
    }
    /**
     * Start or stop a module
     */
    serviceCommand(cmd) {
        this.runExport(cmd);
    }
    /**
     * Execute an exported function
     */
    runExport(cmd, args) {
        // Ensure the cmd is defined before running it
        if (this.exportsFunction(cmd)) {
            this.logger.verbose(`Running export ${cmd} for ${this.moduleDefinition.name}`);
            const env = this.environment;
            const bashCommand = this._exports[cmd].split(" ").concat(args || []).join(" ");
            const res = run_program_1.runProgram("bash", ["--login", "-c", bashCommand], {
                env, retrieveStdStreams: true, logger: this.logger, detachStdStreams: true,
            });
            process.stdout.write(res.stdout);
            process.stderr.write(res.stderr);
            if (res.code !== 0) {
                throw new Error(`Export ${cmd} for ${this.moduleDefinition.name} failed with exit code ${res.code}`);
            }
        }
        else {
            this.logger.debug(`Export ${cmd} is not defined for ${this.moduleDefinition.name}`);
        }
    }
    /**
     * Check if a module exports a certain function
     */
    exportsFunction(cmd) {
        return !!this._exports[cmd];
    }
    /**
     * Platform full-name, used for URLs - i.e. `linux-amd64-debian-9`
     */
    osArchDistro(platform) {
        return `${platform.osArch}-${platform.distro}-${platform.distroVersion}`;
    }
}
exports.BitnamiModule = BitnamiModule;
