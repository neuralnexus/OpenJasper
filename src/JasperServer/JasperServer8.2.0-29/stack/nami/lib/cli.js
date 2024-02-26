"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.CLI = void 0;
const util = require("util");
const fs = require("fs");
const path = require("path");
const provisioner_1 = require("./provisioner");
const utils_1 = require("./utils");
const commander_1 = require("commander");
const reset_cmd_1 = require("./reset_cmd");
class CLI {
    /**
     * Create CLI instance
     * @param argv List of arguments, usually `process.argv`
     */
    constructor(argv) {
        this.argv = argv;
        this._initializeNami();
        this._initializeParser();
    }
    /**
     * Instance of logger
     */
    get logger() {
        return this.provisioner.logger;
    }
    /**
     * Handle CLI invocation
     */
    async handle() {
        try {
            await this.parser.parseAsync(this.argv);
        }
        catch (e) {
            // we may not have logger here, so log to stderr
            process.stderr.write(`Unknown error: ${e.message}\n\n`);
            throw e;
        }
    }
    /**
     * Handler for unpack CLI command
     */
    async unpackCommand(clean) {
        try {
            await this._initializeOptionValues();
            await this.provisioner.unpackModules();
            await this.provisioner.unpackCustomFiles();
            if (this.parser.opts().performProvisioning) {
                await this.provisioner.provisionMachine();
            }
            if (clean) {
                await this.provisioner.unpackCleanup();
                // don't need to cleanup during the unpack command
                // in clouds without images
                if (this.provisioner.cloud.cloudTags.indexOf("no-image") < 0) {
                    await this.provisioner.cleanMachine();
                }
            }
            this.generateUnpackSummary();
        }
        catch (e) {
            this.logger.handleError("Unable to perform unpack operation", e);
        }
    }
    generateUnpackSummary() {
        const summaryModules = this.provisioner.tierDefinition.modules.map(m => ({ name: m.name, version: m.version }));
        const summary = {
            modules: summaryModules,
            packages: this.provisioner.stackDefinition.systemPackagesDependencies
        };
        fs.writeFileSync(path.join(this.parser.opts().outputFolder, "unpack-summary.json"), JSON.stringify(summary));
    }
    /**
     * Handler for provision CLI command
     */
    async provisionCommand(clean) {
        try {
            await this._initializeOptionValues();
            await this.provisioner.provisionMachine();
            if (clean) {
                await this.provisioner.cleanMachine();
            }
        }
        catch (e) {
            this.logger.handleError("Unable to perform provision operation", e);
        }
    }
    /**
     * Handler for initialize CLI command
     * @param all whether to perform all operations - such as wait for ports
     */
    async initializeCommand(all) {
        try {
            await this._initializeOptionValues();
            if (all) {
                await this.provisioner.waitForRequiredPorts();
            }
            await this.provisioner.initializeModules();
        }
        catch (e) {
            this.logger.handleError("Unable to perform initialize operation", e);
        }
    }
    /**
     * Handler for install CLI command
     * @param all whether to perform all operations - such as wait for ports
     */
    async installCommand(all) {
        try {
            await this._initializeOptionValues();
            await this.provisioner.unpackModules();
            await this.provisioner.unpackCustomFiles();
            if (all) {
                await this.provisioner.waitForRequiredPorts();
            }
            await this.provisioner.initializeModules();
        }
        catch (e) {
            this.logger.handleError("Unable to perform install operation", e);
        }
    }
    /**
     * Handler for firstboot CLI command
     */
    async firstbootCommand() {
        const shouldPerformProvisioning = this.parser.opts().performProvisioning;
        const shouldPerformReboot = this.parser.opts().reboot;
        // TODO: ensure this is only run once!
        try {
            await this._initializeOptionValues();
            await this.provisioner.initializeCloudValues();
            // Skip 1st Boot when customizing AMI
            if (await this.provisioner.cloud.isCustomizingMode()) {
                this.logger.info("Skipping First Boot. Customizing Mode enabled!");
            }
            else if (shouldPerformProvisioning && shouldPerformReboot) {
                await this.provisioner.provisionMachine();
                this.provisioner.platform.shutdown({ type: "reboot", delay: 30 });
            }
            else {
                this.logger.debug("Setting random password");
                await this.provisioner.setRandomPasswordIfNotProvided();
                if (shouldPerformProvisioning) {
                    this.logger.debug("Provisioning machine");
                    await this.provisioner.provisionMachine();
                }
                this.logger.debug("Waiting for ports");
                await this.provisioner.waitForRequiredPorts();
                this.logger.debug("First boot and wait");
                await this.provisioner.firstbootDelay();
                this.logger.debug("Initializing modules");
                await this.provisioner.initializeModules();
                if (shouldPerformReboot) {
                    await this.provisioner.firstbootFinished(true);
                    this.provisioner.platform.shutdown({ type: "reboot", delay: 30 });
                }
                else if (!this.provisioner.provisioned) {
                    // to avoid issue with wrong system service state, start servicesCommand
                    // using the system service for provisioner
                    // But ONLY if we are not already provisioned: the new pipeline doesn't need this.
                    // And will call start explicitly after firstboot, avoiding restarting a systemd
                    // service within it start execution.
                    this.logger.info("Restarting service");
                    this.provisioner.platform.restartService(this.provisioner.provisionerServiceName);
                    await this.provisioner.firstbootFinished(true);
                }
                else {
                    await this.provisioner.firstbootFinished(true);
                }
            }
        }
        catch (e) {
            await this.provisioner.firstbootFinished(false);
            this.logger.handleError("Unable to initialize instance", e);
        }
    }
    /**
     * Handler for stop, start and status CLI commands
     */
    async servicesCommand(cmd, reverse) {
        try {
            await this._initializeOptionValues();
            await this.provisioner.serviceCommand(cmd);
        }
        catch (e) {
            this.logger.handleError(`Unable to perform ${cmd} operation`, e);
            throw e;
        }
    }
    async _initializeOptionValues() {
        const optionMap = {
            cloudName: "cloudName",
            cloudAccountId: "cloudAccountId",
            storageAccountName: "storageAccountName",
            storageAccountKey: "storageAccountKey",
            platformName: "platformName",
            instanceTier: "instanceTier",
            instanceData: "instanceTierData",
            peerAddress: "peerAddress",
            _peerPort: "peerPort",
            peerUsername: "peerUsername",
            peerConnectionOptions: "peerConnectionOptions",
            publicEndpoint: "publicEndpoint",
            appUsername: "appUsername",
            appPassword: "appPassword",
            appDatabase: "appDatabase",
            appRepository: "appRepository",
            clusterName: "clusterName",
            clusterQuorum: "clusterQuorum",
            persistentNode: "persistentNode",
            booleanInput: "booleanInput",
            configOverrides: "configOverrides",
            snitch: "snitch",
            resolveNames: "resolveNames",
            recipesPath: "recipesPath",
            provisioned: "provisioned"
        };
        this.provisioner.initializeLogger({
            logLevel: this.parser.opts().logLevel
        });
        for (let key of Object.keys(optionMap)) {
            let optionName = optionMap[key];
            if (this._providedOption(optionName)) {
                this.provisioner[key] = this.parser.opts()[optionName];
            }
        }
        if (this._providedOption("instanceTierMap")) {
            this.provisioner.setInstanceTierFromQuery(this.parser.opts().instanceTierMap);
        }
        // initialize peer password if provided
        if (this._providedOption("peerPassword")) {
            await this.provisioner.setPeerPassword({
                value: this.parser.opts().peerPassword
            });
        }
        else if (this._providedOption("peerPasswordInput")) {
            await this.provisioner.setPeerPassword({
                input: this.parser.opts().peerPasswordInput
            });
        }
        // set unique id from either unique id input or from peer password input
        if (this._providedOption("sharedUniqueIdInput")) {
            this.provisioner.setSharedUniqueId({
                input: this.parser.opts().sharedUniqueIdInput
            });
        }
        else if (this._providedOption("peerPasswordInput")) {
            this.provisioner.setSharedUniqueId({
                input: this.parser.opts().peerPasswordInput
            });
        }
        // Initialize extraServices if provided
        if (this._providedOption("extraServices")) {
            this.provisioner.setExtraServicesFromRaw(this.parser.opts().extraServices);
        }
        this.provisioner.skipRecipes = this._getOptionAsArray("skipRecipes");
        this.provisioner.onlyRecipes = this._getOptionAsArray("onlyRecipes");
        if (this._providedOption("definitionFile")) {
            await this.provisioner.loadDefinition(this.parser.opts().definitionFile);
        }
        else if (this._providedOption("definitionFile")) {
            await this.provisioner.downloadDefinition(this.parser.opts().definitionUrl);
        }
        if (this._providedOption("peerNodesPrefix")) {
            this.provisioner.peerNodes.prefix = this.parser.opts().peerNodesPrefix;
        }
        if (this._providedOption("peerNodesFrom")) {
            this.provisioner.peerNodes.startIndex = parseInt(this.parser.opts().peerNodesFrom);
        }
        if (this._providedOption("peerNodesCount")) {
            this.provisioner.peerNodes.count = parseInt(this.parser.opts().peerNodesCount);
        }
        if (this._providedOption("peerNodesIndex")) {
            this.provisioner.peerNodes.index = parseInt(this.parser.opts().peerNodesIndex);
        }
        if (this._providedOption("peerNodesAddresses")) {
            this.provisioner.peerAddresses = this._getOptionAsArray("peerNodesAddresses");
        }
        if (this._providedOption("internalAddresses")) {
            this.provisioner.internalAddresses = this._getOptionAsArray("internalAddresses");
        }
        if (this._providedOption("optionsFile")) {
            this.provisioner.options.initializeFromFile(this.parser.opts().optionsFile);
        }
        if (this._providedOption("modulesTempDir")) {
            this.provisioner.modulesTempDir = this.parser.opts().modulesTempDir;
        }
        this.provisioner.saveConfiguration();
        // pass function so data is not referenced until actually logging debug info
        this.logger.debug("Initialized options", () => {
            return {
                cloudName: this.provisioner.cloudName,
                instanceTier: this.provisioner.instanceTier,
                instanceData: this.provisioner.instanceData,
                peerAddress: this.provisioner.peerAddress,
                peerPort: this.provisioner.peerPort,
                peerUsername: this.provisioner.peerUsername,
                peerConnectionOptions: this.provisioner.peerConnectionOptions,
                publicEndpoint: this.provisioner.publicEndpoint,
                extraServices: this.provisioner.extraServices,
                appUsername: this.provisioner.appUsername,
                appPassword: this.provisioner.appPassword,
                appDatabase: this.provisioner.appDatabase,
                appRepository: this.provisioner.appRepository,
                clusterName: this.provisioner.clusterName,
                clusterQuorum: this.provisioner.clusterQuorum,
                persistentNode: this.provisioner.persistentNode,
                booleanInput: this.provisioner.booleanInput,
                configOverrides: this.provisioner.configOverrides,
                snitch: this.provisioner.snitch,
                peerNodes: this.provisioner.peerNodes,
                storageAccountName: this.provisioner.storageAccountName
            };
        });
        await this.provisioner.initializeProvisioner();
    }
    /**
     * Handler for eval CLI command
     * @param opts Options passed from `Parser` ; used to get command to run
     */
    async evalCommand(code) {
        try {
            // TODO: is there any better way to extract this?
            let result;
            await this._initializeOptionValues();
            result = this.provisioner.evalInContext(code);
            if (result && (result instanceof Promise)) {
                result = await result;
            }
            if (result) {
                console.log(util.inspect(result));
            }
        }
        catch (e) {
            console.log(e.message + "\n" + (e.stack || ""));
            throw e;
        }
    }
    /**
     * Handler for callRecipes CLI command
     * @param event string ; event name to run
     */
    async callRecipesCommand(event) {
        try {
            let result;
            await this._initializeOptionValues();
            await this.provisioner.callRecipes(event, { errorOnFail: true });
        }
        catch (e) {
            console.log(e.message + "\n" + (e.stack || ""));
            throw e;
        }
        // context.runInContext(new vm.Script(code));
    }
    /**
     *
     */
    async retrieveSharedPasswordCommand() {
        await this._initializeOptionValues();
        if (!(this.provisioner.peerPassword && this.provisioner.peerPassword !== "")) {
            console.log("Currently there is no password stored in the system.");
            console.log("Either it was set manually or has already been retrieved.");
            process.exit(1);
        }
        try {
            if (true) {
                let verifyAnswer = (await utils_1.askQuestion("WARNING! This will show the password only once and delete it from the system.\n" +
                    "Do you want to continue", { type: "boolean" }));
                if (!verifyAnswer) {
                    process.exit(1);
                }
            }
            console.log(`\nThe password is:\n\n${this.provisioner.peerPassword}\n`);
            console.log("Please make sure that you have stored the password as it's removed from system");
            this.provisioner.removePeerPassword();
        }
        catch (e) {
            this.logger.handleError(`Unable to retrieve peer password`, e);
            process.exit(1);
        }
    }
    _initializeNami() {
        this.provisioner = new provisioner_1.Provisioner();
        this.provisioner.loadConfiguration();
    }
    _initializeParser() {
        this.parser = new commander_1.Command();
        this.parser.name("provisioner");
        this.parser.showHelpAfterError();
        this.parser.showSuggestionAfterError();
        this.parser.version("0.2.0", "--version");
        this.parser.addOption(new commander_1.Option("--log-level <string>", "Configures the verbosity of messages")
            .choices(["error", "warn", "info", "verbose", "debug", "trace", "trace8"])
            .default("info"));
        this.parser.option("--cloud-name <string>", "Cloud identifier", this.provisioner.cloudName);
        this.parser.option("--platform-name <string>", "Name of the platform", this.provisioner.platformName);
        this.parser.option("--definition-url <string>", "URL of the stack definition");
        this.parser.option("--definition-file <string>", "File with the stack defition");
        this.parser.option("--instance-tier <string>", "", this.provisioner.instanceTier);
        this.parser.option("--instance-tier-map <string>");
        this.parser.option("--instance-tier-data <string>");
        this.parser.option("--peer-nodes-prefix <string>");
        this.parser.option("--peer-nodes-count <integer>");
        this.parser.option("--peer-nodes-index <integer>");
        this.parser.option("--peer-nodes-from <integer>");
        this.parser.option("--peer-nodes-addresses <string>");
        this.parser.option("--internal-addresses <string>");
        this.parser.option("--peer-password-input <string>");
        this.parser.option("--shared-unique-id-input <string>");
        this.parser.option("--peer-username <string>");
        this.parser.option("--peer-password <string>");
        this.parser.option("--peer-address <string>");
        this.parser.option("--peer-port <integer>");
        this.parser.option("--peer-connection-options <string>");
        this.parser.addOption(new commander_1.Option("--extra-services <string>", "base64 encoded JSON string containing information about extra services to pass to provisioner")
            .hideHelp());
        this.parser.option("--public-endpoint <string>");
        this.parser.option("--app-username <string>");
        this.parser.option("--app-password <string>");
        this.parser.option("--app-database <string>");
        this.parser.option("--app-repository <string>");
        this.parser.option("--cluster-name <string>");
        this.parser.option("--cluster-quorum <string>");
        this.parser.option("--persistent-node <string>");
        this.parser.option("--boolean-input <string>", "", "no");
        this.parser.option("--config-overrides <string>", "", "# No overrides");
        this.parser.option("--snitch <string>");
        this.parser.option("--resolve-names", "", false);
        this.parser.option("--perform-provisioning", "", false);
        this.parser.option("--reboot", "", false);
        this.parser.addOption(new commander_1.Option("--recipes-path <string>").hideHelp());
        this.parser.addOption(new commander_1.Option("--skip-recipes <string>").hideHelp());
        this.parser.addOption(new commander_1.Option("--only-recipes <string>").hideHelp());
        this.parser.addOption(new commander_1.Option("--cloud-account-id <string>").hideHelp());
        this.parser.addOption(new commander_1.Option("--storage-account-name <string>").hideHelp());
        this.parser.addOption(new commander_1.Option("--storage-account-key <string>").hideHelp());
        this.parser.addOption(new commander_1.Option("--options-file <string>").hideHelp());
        this.parser.addOption(new commander_1.Option("--modules-temp-dir <string>").hideHelp());
        this.parser.option("--provisioned", "Only setup application files, skipping any other provisioning action including 'provision' recipes", false);
        this.parser.option("--output-folder <string>", "Output path wher generated assets should be stored (summaries)", "output");
        this.parser.command("unpack").action(async () => {
            await this.unpackCommand(false);
        });
        this.parser.command("unpackForTarball", { hidden: true }).action(async () => {
            await this.unpackCommand(true);
        });
        this.parser.command("initialize").action(async () => {
            await this.initializeCommand(false);
        });
        this.parser.command("initializeAll").action(async () => {
            await this.initializeCommand(true);
        });
        this.parser.command("install").action(async () => {
            await this.installCommand();
        });
        this.parser.command("provision").action(async () => {
            await this.provisionCommand(false);
        });
        this.parser.command("provisionForImage", { hidden: true }).action(async () => {
            await this.provisionCommand(true);
        });
        this.parser.command("firstboot").action(async () => {
            await this.firstbootCommand();
        });
        this.parser.command("start").action(async () => {
            await this.servicesCommand("start", false);
        });
        this.parser.command("stop").action(async () => {
            await this.servicesCommand("stop", true);
        });
        this.parser.command("status").action(async () => {
            await this.servicesCommand("status", false);
        });
        this.parser.command("eval", { hidden: true }).argument("<code>").action(async (code) => {
            await this.evalCommand(code);
        });
        this.parser.command("callRecipes", { hidden: true }).argument("<event>").action(async (event) => {
            await this.callRecipesCommand(event).catch(err => { process.exit(1); });
        });
        this.parser.command("retrieveSharedPassword").action(async () => {
            await this.retrieveSharedPasswordCommand();
        });
        this.parser.addOption(new commander_1.Option("--provisioner-bundle-url <string>", "The URL of a provisioner bundle to download and extract by the reset command")
            .hideHelp());
        this.parser.command("reset", { hidden: true }).argument("[subcommand]").description("Resets a provisioner deployment substituting it by a new one.\n\n" +
            "Subcommands:\n" +
            "  clean:     Stops, removes and creates a backup of the installed files.\n" +
            "  download:  Downloads a provisioner bundle.\n" +
            "  extract:   Extracts the latest downloaded provisioner bundle.\n" +
            "  bootstrap: Performs the Provisioner installation (firstboot).\n" +
            "  restore:   Restores the last backup overriding existing files.\n" +
            "\nIf no subcommand is provided, all of them are executed in sequence.").action(async (subcommand) => {
            await new reset_cmd_1.ResetProvisioner(this, subcommand).mainCommand();
        });
    }
    _providedOption(optionName) {
        return (this.parser.opts()[optionName] !== undefined && this.parser.opts()[optionName] !== "");
    }
    _getOptionAsArray(optionName) {
        const values = this.parser.opts()[optionName] || "";
        if (values === "") {
            return null;
        }
        return values.split(/[,;:\s]/).map(value => { return value.trim(); }).filter(value => {
            return true;
        });
    }
}
exports.CLI = CLI;
