"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.Provisioner = void 0;
const fs = require("fs");
const path = require("path");
const glob = require("glob");
const YAML = require("js-yaml");
const vm = require("vm");
const cloud_1 = require("./cloud");
const index_1 = require("./platform/index");
const stack_definition_1 = require("./stack_definition");
const storage_1 = require("./storage");
const utils_1 = require("./utils");
const download_1 = require("./download");
const logger_1 = require("./logger");
const os_1 = require("os"); /* TODO */
const recipe_runner_1 = require("./recipe_runner");
const download = require("./download");
const utils = require("./utils");
const run_program_1 = require("./run_program");
const provisioner_options_handler_1 = require("./provisioner_options_handler");
;
const defaultPeerNodes = { prefix: "", startIndex: 0, index: 0, count: 0 };
/**
 * Main class for the provisioner
 */
class Provisioner {
    constructor(platform) {
        /**
         * IP addresses or hostnames of every other peer in this deployment.
         * Usually used in peer2peer deployments where an index-based naming is not needed.
         */
        this.peerAddresses = [];
        /**
         * IP addresses of every node that can send traffic to internal ports.
         * Hostnames are not supported for now.
         * If not provided internal ports will be open for all, usually depending on some other
         * mechanism (ie a VPC) to restrict access to them from the outside.
         * Usually will be the same as peerAddresses, but it can also be used in other
         * topologies, for instance, in master-slave to allow slaves to connect to the master.
         */
        this.internalAddresses = [];
        /**
         * Username to authenticate to other peers - such as database user
         */
        this.peerUsername = "";
        /**
         * Password to authenticate to other peers - such as database password or `master` tier password
         */
        this.peerPassword = "";
        /**
         * Whether peer password was provided
         */
        this.peerPasswordSet = false;
        /**
         * String with public endpoint (IP or FQDN)
         */
        this.publicEndpoint = "";
        /**
         * Whether peer password was generated and should be stored
         */
        this.storePeerPassword = false;
        /**
         * Resolve host names to IP addresses if set
         */
        this.resolveNames = false;
        /**
         * Cloud account identifier that was specified via CLI
         */
        this.cloudAccountId = "";
        /**
         * Cloud storage account identifier.
         */
        this.storageAccountName = "";
        /**
         * Cloud storage account key.
         */
        this.storageAccountKey = "";
        /**
         * Unique deployment IDs
         */
        this.sharedUniqueId = "";
        /**
         * Instance of ProvisionerPeerNodes
         */
        this.peerNodes = Object.assign({}, defaultPeerNodes);
        /**
         * List of local aliases
         */
        this.localAliases = [];
        this.options = new provisioner_options_handler_1.ProvisionerOptionsHandler();
        /**
         * Temporary working directory for modules
         */
        this.modulesTempDir = "";
        if (platform) {
            this._platform = platform;
        }
    }
    /**
     * Initialize logger ; called from CLI after arguments are parsed
     */
    initializeLogger(options) {
        this._logger = new logger_1.WinstonLogger(options);
    }
    /**
     * Instance of Cloud object
     */
    get cloud() {
        if (!this._cloud) {
            this._cloud = cloud_1.createCloud({
                cloudName: this.cloudName,
                logger: this.logger,
                platform: this.platform,
                accountId: this.cloudAccountId
            });
        }
        return this._cloud;
    }
    /**
     * Instance of StorageManager, for reading and writing data
     */
    get storageManager() {
        if (!this._storageManager) {
            this._storageManager = new storage_1.StorageManager(this);
        }
        return this._storageManager;
    }
    /**
     * Stack definition, contains definition of all tiers in a multi-vm deployment
     */
    get stackDefinition() {
        if (!this._stackDefinition) {
            const stackConfig = this.storageManager.getItem("stackconfig");
            this._stackDefinition = new stack_definition_1.StackDefinition(this, {
                data: stackConfig.data || {}
            });
        }
        return this._stackDefinition;
    }
    /**
     * Returns a unique id based on deployment unique id ; this can be used to generate
     * one or more unique IDs that will be the same for same input on all nodes in a deployment
     */
    getDerivedSharedUniqueId(prefix, defaultValue) {
        if (this.sharedUniqueId && (this.sharedUniqueId !== "")) {
            return utils_1.createHashAsHex(`${prefix}-${this.sharedUniqueId}`);
        }
        else {
            return defaultValue;
        }
    }
    /**
     * Encode string to have the format of a Fernet key:
     * "Fernet key must be 32 url-safe base64-encoded bytes."
     * https://cryptography.io/en/latest/_modules/cryptography/fernet/#Fernet.generate_key
     * @param str String to encode as fernet key
     */
    getDerivedFernetKey(str) {
        return Buffer.from(utils_1.createHashAsHex(str).substring(0, 32)).toString("base64");
    }
    /**
     * Returns the deployemnt id common for machines launched as part of the multi-vm deployment
     */
    get uniqueDeploymentId() {
        return this.getDerivedSharedUniqueId("", "");
    }
    /**
     * Logger instance
     */
    get logger() {
        if (!this._logger) {
            throw new Error("Logger not yet initialized");
        }
        return this._logger;
    }
    /**
     * Current tier definition (single part of multi-vm deployment)
     * Note that it will be cached so that it can be handled from recipes
     */
    get tierDefinition() {
        if (!this._tierDefinition) {
            this._tierDefinition = this.stackDefinition.getTier(this.instanceTier, this.instanceData);
        }
        return this._tierDefinition;
    }
    /**
     * Determines whether the node is a primary node - i.e. one that has public IP
     * exposed and provides main functionality
     */
    get isPrimaryTier() {
        // this is a primary tier if node is one of main, master or frontend
        return ["main", "master", "frontend"].indexOf(this.instanceTier) >= 0;
    }
    /**
     * Instance of Platform object
     */
    get platform() {
        if (!this._platform) {
            this._platform = index_1.getCurrentPlatform({
                platformName: this.platformName,
                logger: this.logger,
                storageData: this.storageManager.getItem("platform")
            });
        }
        return this._platform;
    }
    /**
     * Perform initialization
     */
    async initializeProvisioner() {
        if (!this._initialized) {
            this.recipeRunner = new recipe_runner_1.RecipeRunner({
                provisioner: this,
                storageName: "reciperunner"
            });
            this.recipeRunner.loadInternalDefinitions(this.recipesPath);
            this.platform.modifyProcessPath({
                add: this.platform.pathInfo.namiRuntimeBinDirectory,
                addAtBeginning: true
            });
            this._initialized = true;
        }
    }
    _moduleTempDir(mod) {
        // The modules temp directory might be defined via CLI arguments so we can test local
        // modules that are not publicly available yet.
        if (!this.modulesTempDir) {
            this.modulesTempDir = fs.mkdtempSync("/tmp/provisioner-");
        }
        return `${this.modulesTempDir}/${mod.baseName}`;
    }
    /**
     * Download and unpack all required modules
     */
    async unpackModules(asyncDownload) {
        let promises = [];
        await this.callRecipes("beforeUnpack", { errorOnFail: true });
        for (const m of this.tierDefinition.modules) {
            // The expected module temp dir has the form `<modules-temp-dir>/<name>-<version>`
            const tempDir = this._moduleTempDir(m);
            // If the modules temp directory was provided externally, it might contain the uncompressed
            // modules already. If that's the case, we will use them directly.
            if (!fs.existsSync(tempDir)) {
                const promise = m.downloadAndUnpack(tempDir);
                this.logger.info("Downloading", m.name, m.version);
                this.logger.debug("Downloading from", m.downloadUrl, "to", tempDir);
                if (asyncDownload) {
                    promises.push(promise);
                }
                else {
                    await promise;
                }
            }
            else {
                this.logger.info(`Skipping download of ${m.name}-${m.version}. It is already present in ${tempDir}`);
            }
        }
        await Promise.all(promises);
        // Write additional files bundled in the definition
        for (let filePath in (this.tierDefinition.additionalFiles || {})) {
            this.logger.debug(`Creating ${filePath}`);
            fs.mkdirSync(path.dirname(filePath), { recursive: true });
            fs.writeFileSync(filePath, Buffer.from(this.tierDefinition.additionalFiles[filePath].data.content, "base64").toString());
            fs.chmodSync(filePath, this.tierDefinition.additionalFiles[filePath].data.mode);
        }
        for (const m of this.tierDefinition.modules) {
            const tempDir = this._moduleTempDir(m);
            await m.installModuleFromDirectory(tempDir);
        }
        await this.callRecipes("afterUnpack", { errorOnFail: true });
    }
    /**
     * Perform initial provisioning of the machine
     */
    async provisionMachine() {
        if (this.cloud.cloudTags.some((tag) => tag === "provisioned")) {
            this.logger.info("Skipping provisioning.");
        }
        else {
            this.logger.info("Provisioning machine.");
            await this.callRecipes("provisionMachine", { errorOnFail: true });
        }
    }
    /**
     * Perform clean up of the machine for creating an image or cloud-specific tarball
     */
    async cleanMachine() {
        await this.callRecipes("cleanMachine", { errorOnFail: true });
    }
    /**
     * Perform cleanup of the machine after unpacking process
     */
    async unpackCleanup() {
        await this.callRecipes("unpackCleanup", { errorOnFail: true });
    }
    /**
     * Initialize all modules
     */
    async initializeModules() {
        try {
            await this.callRecipes("beforeInitialize", { errorOnFail: true });
            for (const m of this.tierDefinition.modules) {
                if (m.skipInitialize) {
                    this.logger.debug("Skipping initialization for module", m.name);
                }
                else {
                    this.logger.info("Initializing module", m.name);
                    await m.initializeModule();
                    if (m.startAfterInitialize) {
                        // Ensure that the service is started with the proper memory configuration when startAfterInitialize is set
                        if (m.exportsFunction("resize")) {
                            const mem = this.platform.getMemoryInfo().totalPhysicalMB;
                            m.runExport("resize", ["--memory", `${mem}M`]);
                        }
                        this.logger.debug("Starting as service", m.name);
                        m.serviceCommand("start");
                    }
                }
            }
            await this.callRecipes("afterInitialize", { errorOnFail: true });
        }
        catch (e) {
            await this.callRecipes("afterFailedInitialize");
            throw e;
        }
    }
    /**
     * Get proper port number
     */
    get peerPort() {
        const port = this._peerPort ? parseInt(this._peerPort, 10) : undefined;
        if (port !== undefined) {
            if (isNaN(port) || port < 0) {
                throw new Error(`Invalid peer port: "${this._peerPort}"`);
            }
        }
        return port;
    }
    /**
     * Wait for all required ports ; returns only when all ports
     * can be connected to or throws an error otherwise
     */
    async waitForRequiredPorts() {
        const ports = this.tierDefinition.waitForPorts;
        if (ports.length === 0 || await this.cloud.getMetaData("private-ipv4") === this.peerAddress) {
            this.logger.debug("Waiting for ports", ports, "skipped on", this.peerAddress, new Date().toISOString());
        }
        else {
            this.logger.debug("Waiting for ports", ports, "started on", this.peerAddress, new Date().toISOString());
            if (!(await utils_1.waitForPorts({
                host: this.peerAddress,
                ports: ports,
                logger: this.logger
            }))) {
                throw new Error("Unable to wait for all ports to be open");
            }
            this.logger.debug("Waiting for ports", ports, "finished at", new Date().toISOString());
        }
    }
    /**
     * Initialize settings based on cloud-specific parameters passed ; this allows
     * clouds to pass information like tier via user-data
     */
    async initializeCloudValues() {
        // TODO: can this be moved to recipes somehow? not currently, because
        // the conditions for other recipes may depend on tier tags etc
        // Instance parameters
        const instanceTier = await this.cloud.getUserData("PROVISIONER_TIER");
        if (instanceTier && (instanceTier !== "")) {
            this.logger.info(`Setting instance tier to ${instanceTier} from cloud`);
            this.instanceTier = instanceTier;
            this._tierDefinition = undefined;
        }
        else {
            const tierMap = await this.cloud.getUserData("PROVISIONER_TIER_MAP");
            if (tierMap && (tierMap !== "")) {
                this.logger.info(`Setting instance tier based on query from cloud`);
                this.setInstanceTierFromQuery(tierMap);
            }
        }
        // Peer address
        const peerAddress = await this.cloud.getUserData("PROVISIONER_PEER_ADDRESS");
        if (peerAddress && (peerAddress !== "")) {
            this.logger.info(`Setting peer address to ${peerAddress} from cloud`);
            this.peerAddress = peerAddress;
        }
        // Peer port
        const peerPort = await this.cloud.getUserData("PROVISIONER_PEER_PORT");
        if (peerPort && (peerPort !== "")) {
            this.logger.info(`Setting peer port to ${peerPort} from cloud`);
            this._peerPort = peerPort;
        }
        // Shared unique ID
        const sharedUniqueId = await this.cloud.getUserData("PROVISIONER_SHARED_UNIQUE_ID_INPUT");
        if (sharedUniqueId && (sharedUniqueId !== "")) {
            this.logger.info(`Setting unique id from cloud using unique id input`);
            this.setSharedUniqueId({
                input: sharedUniqueId
            });
        }
        // Peer username
        const peerUsername = await this.cloud.getUserData("PROVISIONER_PEER_USERNAME");
        if (peerUsername && (peerUsername !== "")) {
            this.logger.info("Setting peer username from cloud");
            this.peerUsername = peerUsername;
        }
        // Peer password or peer password input (seed)
        const peerPassword = await this.cloud.getUserData("PROVISIONER_PEER_PASSWORD");
        if (peerPassword && (peerPassword !== "")) {
            this.logger.info(`Setting peer password from cloud`);
            await this.setPeerPassword({
                value: peerPassword
            });
        }
        else {
            const peerPasswordInput = await this.cloud.getUserData("PROVISIONER_PEER_PASSWORD_INPUT");
            if (peerPasswordInput && (peerPasswordInput !== "")) {
                this.logger.info(`Setting peer password based on input from cloud`);
                await this.setPeerPassword({
                    input: peerPasswordInput
                });
                if (!(sharedUniqueId && (sharedUniqueId !== ""))) {
                    this.logger.info(`Setting unique id from cloud using peer password input`);
                    this.setSharedUniqueId({
                        input: peerPasswordInput
                    });
                }
            }
        }
        // App username
        const appUsername = await this.cloud.getUserData("PROVISIONER_APP_USERNAME");
        if (appUsername && (appUsername !== "")) {
            this.logger.info("Setting app username from cloud");
            this.appUsername = appUsername;
        }
        // App password
        let appPassword = await this.cloud.getUserData("PROVISIONER_APP_PASSWORD");
        // Bitnami Launchpad
        if (!appPassword)
            appPassword = await this.cloud.getUserData("bitnami-base-password"); // Google LP & launcher
        if (!appPassword)
            appPassword = await this.cloud.getUserData("bitnami_application_password"); // Others
        if (appPassword && (appPassword !== "")) {
            this.logger.info("Setting app password from cloud");
            this.appPassword = appPassword;
        }
        // App database
        const appDatabase = await this.cloud.getUserData("PROVISIONER_APP_DATABASE");
        if (appDatabase && (appDatabase !== "")) {
            this.logger.info("Setting app database name from cloud");
            this.appDatabase = appDatabase;
        }
        // Database connection options
        const appRepository = await this.cloud.getUserData("PROVISIONER_APP_REPOSITORY");
        if (appRepository && (appRepository !== "")) {
            this.logger.info("Setting app repository from cloud");
            this.appRepository = appRepository;
        }
        // Peer connection options
        const peerConnectionOptions = await this.cloud.getUserData("PROVISIONER_PEER_CONNECTION_OPTIONS");
        if (peerConnectionOptions && (peerConnectionOptions !== "")) {
            this.logger.info("Setting peer connection options from cloud");
            this.peerConnectionOptions = peerConnectionOptions;
        }
        // Cluster name
        const clusterName = await this.cloud.getUserData("PROVISIONER_CLUSTER_NAME");
        if (clusterName && (clusterName !== "")) {
            this.logger.info("Setting cluster name from cloud");
            this.clusterName = clusterName;
        }
        // Enable debug
        const enableDebug = await this.cloud.getUserDataBoolean("PROVISIONER_ENABLE_DEBUG");
        if (enableDebug) {
            this.logger.logLevel = logger_1.LogLevel.TRACE;
        }
        // Peer nodes parameters
        const peerNodesIndex = await this.cloud.getUserData("PROVISIONER_PEER_NODES_INDEX");
        if (peerNodesIndex && peerNodesIndex.length > 0) {
            this.peerNodes.index = parseInt(peerNodesIndex);
        }
        const peerNodesStartIndex = await this.cloud.getUserData("PROVISIONER_PEER_NODES_FROM");
        if (peerNodesStartIndex && peerNodesStartIndex.length > 0) {
            this.peerNodes.startIndex = parseInt(peerNodesStartIndex);
        }
        const peerNodesCount = await this.cloud.getUserData("PROVISIONER_PEER_NODES_COUNT");
        if (peerNodesCount && peerNodesCount.length > 0) {
            this.peerNodes.count = parseInt(peerNodesCount);
        }
        const peerNodesPrefix = await this.cloud.getUserData("PROVISIONER_PEER_NODES_PREFIX");
        if (peerNodesIndex && peerNodesIndex.length > 0) {
            this.peerNodes.prefix = peerNodesPrefix;
        }
        // Extra services
        const rawExtraServices = await this.cloud.getUserData("PROVISIONER_EXTRA_SERVICES");
        if (rawExtraServices && (rawExtraServices !== "")) {
            this.logger.info("Setting extra services from cloud");
            this.setExtraServicesFromRaw(rawExtraServices);
        }
        // Public Endpoint
        const publicEndpoint = await this.cloud.getUserData("PROVISIONER_PUBLIC_ENDPOINT");
        if (publicEndpoint && (publicEndpoint !== "")) {
            this.logger.info("Setting public endpoint from cloud");
            this.publicEndpoint = publicEndpoint;
        }
        // Cluster quorun
        const clusterQuorum = await this.cloud.getUserData("PROVISIONER_CLUSTER_QUORUM");
        if (clusterQuorum && (clusterQuorum !== "")) {
            this.logger.info("Setting cluster quorum from cloud");
            this.clusterQuorum = clusterQuorum;
        }
        // Persistent node
        const persistentNode = await this.cloud.getUserData("PROVISIONER_PERSISTENT_NODE");
        if (persistentNode && (persistentNode !== "")) {
            this.logger.info("Setting persistent node from cloud");
            this.persistentNode = persistentNode;
        }
        // Boolean input
        const booleanInput = await this.cloud.getUserData("PROVISIONER_BOOLEAN_INPUT");
        if (booleanInput && (booleanInput !== "")) {
            this.logger.info("Setting boolean input from cloud");
            this.booleanInput = booleanInput;
        }
        // Config overrides
        const configOverrides = await this.cloud.getUserData("PROVISIONER_CONFIG_OVERRIDES");
        if (configOverrides && (configOverrides !== "")) {
            this.logger.info("Setting config overrides from cloud");
            this.configOverrides = configOverrides;
        }
        // Snitch
        const snitch = await this.cloud.getUserData("PROVISIONER_SNITCH");
        if (snitch && (snitch !== "")) {
            this.logger.info("Setting snitch from cloud");
            this.snitch = snitch;
        }
        // Resolve names
        const resolveNames = await this.cloud.getUserDataBoolean("PROVISIONER_RESOLVE_NAMES");
        if (resolveNames) {
            this.logger.info("Setting resolve names from cloud");
            this.resolveNames = true;
        }
        const storageAccountName = await this.cloud.getUserData("PROVISIONER_STORAGE_ACCOUNT_NAME");
        if (storageAccountName) {
            this.logger.info("Storage account name detected.");
            this.storageAccountName = storageAccountName;
        }
        const storageAccountKey = await this.cloud.getUserData("PROVISIONER_STORAGE_ACCOUNT_KEY");
        if (storageAccountKey) {
            this.logger.info("Storage account key detected.");
            this.storageAccountKey = storageAccountKey;
        }
        this.saveConfiguration();
        await this.modifyHostsFile();
    }
    /**
     * Call first boot recipes and wait for specified amount of seconds if specified
     */
    async firstbootDelay() {
        await this.callRecipes("firstboot");
        if (this.tierDefinition.initializationDelay > 0) {
            await utils_1.delayMs(this.tierDefinition.initializationDelay * 1000);
        }
    }
    /**
     * Start or stop all services in current tier
     * @param cmd Either `start` or `stop`
     * @param reverse Whether services should be stopped/started in reverse order
     */
    async serviceCommand(cmd, reverse) {
        let services = this.tierDefinition.services;
        let eventCmd = cmd.substring(0, 1).toUpperCase() + cmd.substring(1);
        if (reverse) {
            services = services.reverse();
        }
        // ensure PATH is set properly
        this.platform.modifyProcessPath({
            add: this.platform.getAdditionalSystemPaths()
        });
        await this.callRecipes(`before${eventCmd}`);
        for (const m of services) {
            if (!(m.skipInitialize || m.skipService)) {
                this.logger.info("Performing service", cmd, "operation for", m.name);
                m.serviceCommand(cmd);
            }
            else {
                this.logger.info("Skipping service", cmd, "operation for", m.name);
            }
        }
        await this.callRecipes(`after${eventCmd}`);
    }
    get provisionerServiceName() {
        return "bitnami";
    }
    _getValueFromInputOrValue(options, prefix) {
        let result;
        if (options.input) {
            result = utils_1.createHashAsHex(`${prefix || ""}${options.input}`);
        }
        else if (options.value) {
            result = options.value;
        }
        else {
            this.logger.warn("Unable to determine value - none of input and value specified");
        }
        return result;
    }
    /**
     * Generate hash for peerPassword based on any input string ; this can be used to pass
     * random, pseudo-random or unique data to all tiers and generate usable password from it
     */
    async setPeerPassword(options) {
        this.peerPassword = this._getValueFromInputOrValue(options);
        if (options.input) {
            // if the password was generated from input, store it if this node is
            // the one user accesses or store on all nodes for automated tests
            if (this.isPrimaryTier || (await this.cloud.isBitnamiTestingMode())) {
                this.storePeerPassword = true;
            }
        }
        this.peerPasswordSet = true;
    }
    /**
     * Set extra services from raw extra service parameter
     */
    setExtraServicesFromRaw(rawExtraServices) {
        this.extraServices = JSON.parse(Buffer.from(rawExtraServices, "base64").toString());
    }
    /**
     * Log an error message if peer password was not set
     */
    warnIfPeerPasswordNotSet() {
        if (!this.peerPasswordSet) {
            this.logger.warn("Peer password was not set - using default value");
        }
    }
    /**
     * Load configuration from StorageManager ; this will overwrite all current configuration
     * with the one stored on disk
     */
    loadConfiguration() {
        const config = this.storageManager.getItem("configuration");
        this._copyConfiguration(config.data, this, {
            instanceTier: "main",
            cloudName: null,
            platformName: null,
            peerNodes: Object.assign({}, defaultPeerNodes),
            cloudAccountId: ""
        });
    }
    /**
     * Save configuration to disk
     */
    saveConfiguration() {
        this.logger.info("Saving configuration info to disk");
        const config = this.storageManager.getItem("configuration");
        this._copyConfiguration(this, config.data, {});
        config.save();
    }
    _copyConfiguration(from, to, defaults) {
        for (const opt of [
            "instanceTier",
            "appUsername",
            "appPassword",
            "appDatabase",
            "appRepository",
            "clusterName",
            "clusterQuorum",
            "persistentNode",
            "booleanInput",
            "configOverrides",
            "snitch",
            "resolveNames",
            "peerAddress",
            "_peerPort",
            "peerUsername",
            "peerPassword",
            "peerConnectionOptions",
            "peerPasswordSet",
            "extraServices",
            "publicEndpoint",
            "storePeerPassword",
            "sharedUniqueId",
            "platformName",
            "peerNodes",
            "cloudName",
            "cloudAccountId",
            "storageAccountName",
            "storageAccountKey",
            "provisioned"
        ]) {
            to[opt] = from[opt] || defaults[opt];
        }
    }
    _parseDefinition(body) {
        const stackConfig = this.storageManager.getItem("stackconfig");
        let data;
        try {
            if (body.match("^\s*\{")) {
                data = JSON.parse(body);
            }
            else if (body.match("^\s*--")) {
                data = YAML.load(body);
            }
            else {
                throw new Error("unable to detect file format");
            }
        }
        catch (e) {
            throw new Error("Unable to parse stack definition: " + e.message);
        }
        stackConfig.data = data;
        stackConfig.save();
    }
    /**
     * Download stack definition from a remote URL and parse it
     */
    async downloadDefinition(url) {
        // TODO: i18n and encoding?
        const result = await new download_1.Download({
            returnData: true
        }).download(url);
        // TODO: errors ?
        this._parseDefinition(result.body);
    }
    /**
     * Load stack definition from a file and parse it
     */
    loadDefinition(fileName) {
        const contents = fs.readFileSync(fileName).toString();
        this._parseDefinition(contents);
    }
    /**
     * Evaluate code in a sandbox TODO
     */
    evalInContext(code, sandboxInfo) {
        let context = vm.createContext(Object.assign({
            fs: fs,
            path: path,
            glob: glob,
            Promise: Promise,
            RegExp: RegExp,
            console: console,
            process: process,
            require: require,
            utils: utils,
            download: download,
            runProgram: run_program_1.runProgram,
            provisioner: this,
            platform: this.platform,
            global: global,
            logger: this.logger,
            cloud: this.cloud,
            recipes: this.recipeRunner,
        }, sandboxInfo || {}));
        return new vm.Script(code).runInContext(context);
    }
    /**
     * Determine instance tier by using a specific format as input; the format
     * is a list of one or more tier=count items (or tier to indicate last tier)
     * followed by number indicating initial value for index and current index
     * (this allows passing cloud"s raw index of instance, regardless of initial
     *  value for the counter)
     *
     * For example:
     * master=1,slave,0,0 -- this means first instance and maps to master
     * master=1,slave,0,1 -- this means second instance and maps to slave
     * master=1,arbiter=1,slave,0,1 -- this means second instance and maps to arbiter
     * master=1,arbiter=1,slave,1,2 -- also means second instance and maps to arbiter
     */
    _determineInstanceTier(tierQuery) {
        let tier;
        let tierOptions = tierQuery.split(",");
        let tierIndex = tierOptions;
        if (tierOptions.length >= 2) {
            let tierIndex = parseInt(tierOptions.pop());
            const tierInitialOffset = parseInt(tierOptions.pop());
            tierIndex -= tierInitialOffset;
            let tierSourceIndex = 0;
            for (let value of tierOptions) {
                const nameValueMatch = value.match(/^(.*)=([0-9]+)$/);
                let currentTierName = value;
                let currentTierCount = tierIndex;
                if (nameValueMatch) {
                    currentTierName = nameValueMatch[1];
                    currentTierCount = parseInt(nameValueMatch[2]);
                }
                tierSourceIndex += currentTierCount;
                if (tierSourceIndex > tierIndex) {
                    tier = currentTierName;
                    break;
                }
            }
        }
        if (tier === undefined) {
            throw new Error("Unable to determine current tier from specification");
        }
        return tier;
    }
    /**
     * Set instance tier from query ; see `_determineInstanceTier` for more details
     */
    setInstanceTierFromQuery(tierQuery) {
        this.instanceTier = this._determineInstanceTier(tierQuery);
        this._tierDefinition = undefined;
    }
    /**
     * Wrapper for recipeRunner that properly handles skipRecipes and onlyRecipes options
     */
    async callRecipes(eventName, options) {
        options = Object.assign({}, options || {});
        options.skip = options.skip || this.skipRecipes;
        options.only = options.only || this.onlyRecipes;
        const result = await this.recipeRunner.call(eventName, options);
        if (result.failed.length > 0) {
            this.logger.warn(`Unable to run provisioner recipes for ${eventName}`);
        }
        return result;
    }
    _valueOrDefault(value, defaultValue) {
        value = value || "";
        if (value !== "") {
            return value;
        }
        else {
            return defaultValue;
        }
    }
    /**
     * Configured or default peer username
     */
    get defaultOrPeerUsername() {
        return this._valueOrDefault(this.peerUsername, this.stackDefinition.defaultPeerUsername);
    }
    /**
     * Configured or default peer username
     */
    get defaultOrPeerPassword() {
        return this._valueOrDefault(this.peerPassword, this.stackDefinition.defaultPeerPassword);
    }
    /**
     * Configured or default username
     */
    get defaultOrAppUsername() {
        return this._valueOrDefault(this.appUsername, this.stackDefinition.defaultUsername);
    }
    /**
     * Configured or default password
     */
    get defaultOrAppPassword() {
        return this._valueOrDefault(this.appPassword, this.stackDefinition.defaultPassword);
    }
    /**
     * Configured or default database name
     */
    get defaultOrAppDatabase() {
        return this._valueOrDefault(this.appDatabase, this.stackDefinition.defaultDatabase);
    }
    /**
     * Configured or default cluster name
     */
    get defaultOrClusterName() {
        return this._valueOrDefault(this.clusterName, this.stackDefinition.defaultClusterName);
    }
    /**
     * Configured or default cluster quorum
     */
    get defaultOrClusterQuorum() {
        return this._valueOrDefault(this.clusterQuorum, this.stackDefinition.defaultClusterQuorum);
    }
    /**
     * Configured or default persistent node
     */
    get defaultOrPersistentNode() {
        return this._valueOrDefault(this.persistentNode, this.stackDefinition.defaultPersistentNode);
    }
    /**
     * Configured or default boolean input
     */
    get defaultOrBooleanInput() {
        return this._valueOrDefault(this.booleanInput, this.stackDefinition.defaultBooleanInput);
    }
    /**
     * Configured or default config overrides
     */
    get defaultOrConfigOverrides() {
        return this._valueOrDefault(this.configOverrides, this.stackDefinition.defaultConfigOverrides);
    }
    /**
     * Configured or default snitch
     */
    get defaultOrSnitch() {
        return this._valueOrDefault(this.snitch, this.stackDefinition.defaultSnitch);
    }
    /**
     * Configured or default public endpoint
     */
    get defaultOrPublicEndpoint() {
        return this._valueOrDefault(this.publicEndpoint, this.stackDefinition.defaultPublicEndpoint);
    }
    /**
     * Get list of local aliases
     */
    appendLocalAliases(aliases) {
        if (aliases.length > 0) {
            this.localAliases = this.localAliases.concat(aliases);
        }
        else {
            throw new Error("Empty list of aliases");
        }
    }
    /**
     * Get hostname of provisioner peer to use
     */
    get peerAddressHostname() {
        // TODO: consider reading from definition file
        return "provisioner-peer";
    }
    /**
     * Get local hostname to use
     */
    get localAddressHostname() {
        // TODO: consider reading from definition file
        return "provisioner-local";
    }
    async modifyHostsFile() {
        let hostsMap = {};
        // Populate local aliases
        let aliases = this.tierDefinition.getPeerHostnames("local") || [];
        aliases = aliases.concat([this.localAddressHostname]);
        this.appendLocalAliases(aliases);
        if (this.peerAddress) {
            let ip = this.peerAddress;
            let hostnames;
            ip = await utils_1.resolveHostname(ip);
            this.logger.info(`Resolving peerAddress with IP (${ip}) for hosts file`);
            hostnames = [this.peerAddressHostname].concat(this.tierDefinition.getPeerHostnames() || []);
            hostsMap[ip] = hostnames;
            this.logger.info(`peerAddress resolved to [${hostnames.join(",")}]`);
        }
        else {
            this.logger.warn("No peerAddress provided. Skipping hosts file section");
        }
        if (this.localAliases.length > 0) {
            hostsMap[await this.cloud.getMetaData("private-ipv4")] = this.localAliases;
        }
        this.platform.setHostsFileItems(hostsMap);
    }
    /**
     * Indicate first boot finish status
     */
    async firstbootFinished(success) {
        fs.writeFileSync(`${this.platform.pathInfo.namiAppPath}/.firstboot.status`, success ? "true" : "false");
        if (success) {
            await this.callRecipes("afterFirstboot");
        }
        else {
            await this.callRecipes("afterFailedFirstboot");
        }
    }
    /**
     * Generate random password when deploying as single-vm ; only done if
     * application has a password and it was not provided via cloud values or via CLI during firstboot
     */
    async setRandomPasswordIfNotProvided() {
        if ((this.instanceTier === "main") && (((!this.appPassword) || (this.appPassword === "")) &&
            (this.stackDefinition.defaultPassword && (this.stackDefinition.defaultPassword !== "")))) {
            this.appPassword = utils_1.generateRandomPassword(12);
            const message = "\n" +
                "##################################################################\n" +
                "#                                                                #\n" +
                `#     Setting Bitnami application password to '${this.appPassword}'     #\n` +
                "#                                                                #\n" +
                "##################################################################\n" +
                "\n";
            this.logger.info(message);
            await this.cloud.writeToConsole(message);
        }
    }
    get peerHostname() {
        if (this.resolveNames === true) {
            return run_program_1.runProgram("hostname", ["-I"]).split(" ")[0].trim();
        }
        else {
            return os_1.hostname();
        }
    }
    get peerNodename() {
        if (this.peerNodes.count > 0 && this.peerNodes.prefix && (this.peerNodes.prefix !== "")) {
            return `${this.peerNodes.prefix}${this.peerNodes.index}`;
        }
        else {
            return os_1.hostname();
        }
    }
    get peerNodenames() {
        // if we have hardcoded peer addresses, use them
        if (this.peerAddresses.length > 0) {
            return this.peerAddresses;
        }
        if (this.peerNodes.count > 0 && this.peerNodes.prefix && (this.peerNodes.prefix !== "")) {
            let result = [];
            let i;
            for (i = 0; i < this.peerNodes.count; i++) {
                result.push(`${this.peerNodes.prefix}${this.peerNodes.startIndex + i}`);
            }
            return result;
        }
        else {
            return [this.peerNodename];
        }
    }
    get oneBasedNodeIndex() {
        const index = this.peerNodes.index - this.peerNodes.startIndex + 1;
        return index.toString();
    }
    /**
     * Remove app password and save configuration
     */
    removeAppPassword() {
        this.appPassword = "";
        this.saveConfiguration();
    }
    /**
     * Remove peer password and save configuration
     */
    removePeerPassword() {
        this.peerPassword = "";
        this.saveConfiguration();
    }
    /**
     * Generate unique deployment id from input specified from external source
     */
    setSharedUniqueId(options) {
        this.sharedUniqueId = this._getValueFromInputOrValue(options, "uniqueid-");
    }
    /**
     * Unpack a tarball with any additional files to be used for testing
     */
    async unpackCustomFiles() {
        const optionPrefix = "provisioner_custom_files";
        if (this.options.provided(`${optionPrefix}_tarball`)) {
            const url = this.options.getValue(`${optionPrefix}_tarball`);
            const prefix = this.options.getValue(`${optionPrefix}_prefix`, "/");
            this.logger.info(`Downloading ${url} and unpacking to ${prefix}`);
            await new download_1.Download({ unpackDirectory: prefix }).download(url);
        }
    }
}
exports.Provisioner = Provisioner;
