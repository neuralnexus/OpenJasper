"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.ResetProvisioner = void 0;
const fs = require("fs");
const path = require("path");
const glob = require("glob");
const run_program_1 = require("./run_program");
class ResetProvisioner {
    constructor(cli, subcommand) {
        this.cli = cli;
        this.provisioner = cli.provisioner;
        this.parser = cli.parser;
        this.subcommand = subcommand ? subcommand : "all"; // Execute everything if no subcommand is provided
        this.supportedSubcommands = ["clean", "download", "extract", "bootstrap", "restore", "all"];
        this.baseBundleDir = "/tmp/provisioner_reset/bundles";
        this.provisionerBundleFile = path.join(this.baseBundleDir, "latest");
        this.baseBackupDir = "/tmp/provisioner_reset/backups";
        this.backupDir = path.join(this.baseBackupDir, "latest");
        this.backupItems = [
            // "/etc/init.d/bitnami",
            "/etc/gonit/gonitrc",
            "/usr/bin/gonit",
            "/usr/bin/monit",
            "/root/.provisioner/",
            "/opt/bitnami/",
        ];
        this.backupItems = this.backupItems.concat(glob.sync("{/etc/logrotate.d/com.bitnami.*,/bitnami/*}"));
        this.dateString = new Date().toISOString().replace(/[T]/g, "-").replace(/(:|\..+$)/g, "");
        this.provisionerBinary = "/opt/bitnami/nami/bin/provisioner";
        this.configFile = "/root/.provisioner/configuration.json";
    }
    get logger() {
        return this.cli.provisioner.logger;
    }
    /**
     * Handler for reset CLI command
     */
    async mainCommand() {
        await this._initializeProperties();
        this._validate();
        if (this.subcommand === "clean") {
            await this.clean();
        }
        else if (this.subcommand === "download") {
            this.download();
        }
        else if (this.subcommand === "extract") {
            this.extract();
        }
        else if (this.subcommand === "bootstrap") {
            await this.bootstrap();
        }
        else if (this.subcommand === "restore") {
            await this.restore();
        }
        else { // all
            await this.clean();
            try {
                this.download();
                this.extract();
                await this.bootstrap();
            }
            catch (e) {
                this.logger.error(e);
                await this.restore();
            }
        }
    }
    async clean() {
        await this._stop();
        await this._backup();
    }
    download() {
        this.logger.info("==> Downloading provisioner bundle:", this.provisionerBundleUrl);
        const provisionerBundleFile = path.join(this.baseBundleDir, `${this.dateString}.tar.gz`);
        run_program_1.runProgram("curl", ["-Lo", provisionerBundleFile, this.provisionerBundleUrl]);
        fs.symlinkSync(provisionerBundleFile, this.provisionerBundleFile);
    }
    extract() {
        this.logger.info("==> Extracting provisioner bundle...");
        run_program_1.runProgram("tar", ["xzf", this.provisionerBundleFile, "-C", "/"]);
    }
    async bootstrap() {
        this._restoreConfig();
        await this._getPasswords();
        this._umount();
        await this._install();
        await this._start();
    }
    async restore() {
        this.logger.warn("==> Restoring backup...");
        this.logger.info("==> Restoring files and directories from:", this.backupDir);
        const itemsToRestore = this.backupItems.filter(f => fs.existsSync(path.join(this.backupDir, f)));
        itemsToRestore.forEach(dest => fs.rmSync(dest, { recursive: true }));
        itemsToRestore.forEach(dest => run_program_1.runProgram("cp", ["-rp", path.join(this.backupDir, dest), dest]));
        this.logger.info("==> Files restored. You might need to restart the services and mount volumes.");
    }
    async _initializeProperties() {
        this.provisioner.initializeLogger({ logLevel: this.parser.opts().logLevel });
        await this.provisioner.initializeProvisioner();
        this.provisioner.skipRecipes = ["remove-passwords"];
        if (this.parser.opts().provisionerBundleUrl !== undefined) {
            this.provisionerBundleUrl = this.parser.opts().provisionerBundleUrl;
        }
        fs.mkdirSync(this.baseBundleDir, { recursive: true });
        fs.mkdirSync(this.baseBackupDir, { recursive: true });
    }
    _validate() {
        if (process.getuid() !== 0) {
            this.logger.error("Please, run this command as root");
            process.exit(1);
        }
        if (!this.supportedSubcommands.includes(this.subcommand)) {
            this.logger.error("Please, specify a valid command:", this.supportedSubcommands);
            process.exit(1);
        }
        if (["all", "download"].includes(this.subcommand)) {
            if (this.parser.opts().provisionerBundleUrl === undefined) {
                this.logger.error("Please, specify the --provisioner-bundle-url");
                process.exit(1);
            }
        }
    }
    async _start() {
        this.logger.info("==> Starting services...");
        await this.provisioner.serviceCommand("start");
    }
    async _stop() {
        this.logger.info("==> Stopping services...");
        await this.provisioner.serviceCommand("stop");
    }
    async _install(cmd = "firstboot") {
        this.logger.info("==> Executing bootstrap:", cmd);
        if (cmd === "firstboot") {
            run_program_1.runProgram("touch", ["/proc"]); // Mock platform.getUniqueBootId()
            await this.cli.firstbootCommand();
        }
        else if (cmd === "initialize") {
            await this.cli.initializeCommand(true);
        }
        else {
            throw new Error(`==> ${cmd} is a supported command.`);
        }
    }
    async _backup() {
        const backupDir = path.join(this.baseBackupDir, this.dateString);
        this.logger.info("==> Backing up files and directories to:", backupDir);
        fs.mkdirSync(backupDir, { recursive: true });
        const backupItemsThatExist = this.backupItems.filter(f => fs.existsSync(f));
        backupItemsThatExist.forEach(origin => fs.renameSync(origin, path.join(backupDir, origin)));
        fs.symlinkSync(backupDir, this.backupDir);
        // The following files keep provisioner working
        fs.mkdirSync(path.dirname(this.provisionerBinary), { recursive: true });
        fs.symlinkSync(path.join(this.backupDir, this.provisionerBinary), this.provisionerBinary);
        fs.cpSync(path.join(this.backupDir, this.configFile), this.configFile);
    }
    _umount() {
        const bitnamiTargets = run_program_1.runProgram("df", ["--output=target"])
            .split("\n").filter(target => target.includes("bitnami"));
        this.logger.info("==> Umounting volumes:", bitnamiTargets);
        bitnamiTargets.forEach(target => run_program_1.runProgram("umount", [target]));
    }
    _restoreConfig() {
        const destination = "/root/.provisioner/configuration.json";
        this.logger.info("==> Restoring provisioner config file:", destination);
        fs.cpSync(path.join(this.backupDir, destination), destination);
    }
    async _getPasswords() {
        this.logger.info("==> Retrieving passwords...");
        await this.provisioner.initializeCloudValues();
        const userDataFile = "/var/lib/cloud/instance/user-data.txt";
        // Application password
        if (this.provisioner.appPassword) {
            // Do nothing
        }
        else if (this.parser.opts().appPassword !== undefined && this.parser.opts().appPassword !== "") {
            this.provisioner.appPassword = this.parser.opts().appPassword;
        }
        else if (fs.existsSync(userDataFile)) {
            const match = fs.readFileSync(userDataFile).toString().match(/--app-password "([^"]*)"/);
            if (match) {
                this.provisioner.appPassword = match[1];
            }
        }
        else {
            this.logger.warn("==> The application password couldn't be retrieved. Please, provide it as a flag.");
        }
        // Peer password
        if (this.provisioner.peerPassword) {
            // Do nothing
        }
        else if (this.parser.opts().peerPassword !== undefined && this.parser.opts().peerPassword !== "") {
            this.provisioner.peerPassword = this.parser.opts().peerPassword;
        }
        else if (fs.existsSync(userDataFile)) {
            const match = fs.readFileSync(userDataFile).toString().match(/--peer-password "([^"]*)"/);
            if (match) {
                this.provisioner.peerPassword = match[1];
            }
        }
        else {
            this.logger.warn("==> The peer password couldn't be retrieved. Please, provide it as a flag.");
        }
        this.logger.info("==> appPassword:", this.provisioner.appPassword);
        this.logger.info("==> peerPassword:", this.provisioner.peerPassword);
        this.provisioner.saveConfiguration();
    }
}
exports.ResetProvisioner = ResetProvisioner;
