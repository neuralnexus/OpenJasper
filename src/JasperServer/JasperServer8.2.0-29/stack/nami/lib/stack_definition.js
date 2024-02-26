"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.TierDefinition = exports.StackDefinition = void 0;
const bitnami_module_1 = require("./bitnami_module");
const _ = require("lodash");
/**
 * Class for querying and managing entire stack definition
 */
class StackDefinition {
    /*
     * constructor
     * @param provisioner Instance of provisioner
     * @options Options for initialization
     */
    constructor(provisioner, options) {
        this.provisioner = provisioner;
        if (options && options.data) {
            this.data = options.data;
        }
        else {
            this.data = {};
        }
    }
    /*
     * Logger instance
     */
    get logger() {
        return this.provisioner.logger;
    }
    get details() {
        const details = this.data.details || {};
        return {
            key: details.key,
            mainModule: details.mainModule,
            name: details.name,
            version: details.version
        };
    }
    get mainTier() {
        const tiers = this.data.tiers || {};
        const mainTierName = "main";
        const tierName = tiers[mainTierName] ? mainTierName : this.provisioner.instanceTier;
        return this.getTier(tierName);
    }
    get mainModule() {
        const details = this.data.details || {};
        return this.mainTier.modules.filter(m => { return m.name === details.mainModule; })[0];
    }
    /*
     * Get tier definition based on the instance tier name and tier instance data; respective tier
     * must be defined in the stack definition metadata object passed at initialization
     *
     * If it exists a cloud specific tier with the format `${instanceTier}_${cloud}`, it will be used
     * instead. To avoid unexpected mistakes, the cloud specific tier should explicitly contain a tag
     * with the format `overwrites-${instanceTier}-tier`.
     * @param instanceTier name of the tier to create definition object for
     * @param instanceData data passed to the tier
     */
    getTier(instanceTier, instanceData) {
        const tiers = this.data.tiers || {};
        const cloudInstanceTier = `${instanceTier}_${this.provisioner.cloudName}`;
        if (tiers[cloudInstanceTier] && tiers[cloudInstanceTier].tags
            && tiers[cloudInstanceTier].tags.includes(`overwrites-${instanceTier}-tier`)) {
            this.logger.info(`Found cloud specific tier "${cloudInstanceTier}". Using it instead of "${instanceTier}"`);
            instanceTier = cloudInstanceTier;
        }
        const tierData = tiers[instanceTier];
        if (!tierData) {
            this.logger.error("Unable to find tier", instanceTier, "definition;", "available:", _.keys(tiers));
            throw new Error(`Unable to find tier definition for ${instanceTier}"`);
        }
        return new TierDefinition(this, tierData, instanceData);
    }
    /*
     * Tags associated with the stack
     */
    get tags() {
        if (this.data.tags) {
            if (!Array.isArray(this.data.tags)) {
                // handle incorrect metadata
                return [this.data.tags];
            }
            else {
                return this.data.tags;
            }
        }
        else {
            return [];
        }
    }
    /**
     * Default username - either based on the metadata in the definition or reasonable default
     */
    get defaultUsername() {
        return ((this.data.defaults || {}).username || "user");
    }
    /**
     * Default password - either based on the metadata in the definition or reasonable default
     */
    get defaultPassword() {
        return ((this.data.defaults || {}).password || "bitnami");
    }
    /**
     * Default peer username - either based on the metadata in the definition or reasonable default
     */
    get defaultPeerUsername() {
        return ((this.data.defaults || {}).peerUsername || "");
    }
    /**
     * Default peer password - either based on the metadata in the definition or reasonable default
     */
    get defaultPeerPassword() {
        return ((this.data.defaults || {}).peerPassword || "Bitnami1");
    }
    /**
     * Default database name if specified in the metadata, otherwise an empty string
     */
    get defaultDatabase() {
        return ((this.data.defaults || {}).database || "");
    }
    /**
     * Default cluster name if specified in the metadata, otherwise an arbitrary name
     */
    get defaultClusterName() {
        return ((this.data.defaults || {}).clusterName || "clustername");
    }
    /**
     * Default cluster quorum if specified in the metadata, otherwise an arbitrary number
     */
    get defaultClusterQuorum() {
        return ((this.data.defaults || {}).clusterQuorum || "2");
    }
    /**
     * Default persistent node if specified in the metadata, otherwise yes
     */
    get defaultPersistentNode() {
        return ((this.data.defaults || {}).persistentNode || "yes");
    }
    /**
     * Default boolean input if specified in the metadata, otherwise no
     */
    get defaultBooleanInput() {
        return ((this.data.defaults || {}).booleanInput || "no");
    }
    /**
     * Default config overrides if specified in the metadata, otherwise a sample comment
     */
    get defaultConfigOverrides() {
        return ((this.data.defaults || {}).configOverrides || "# No overrides");
    }
    /**
     * Default snitch if specified in the metadata, otherwise an empty string
     */
    get defaultSnitch() {
        return ((this.data.defaults || {}).snitch || "");
    }
    /**
     * Default public endpoint if specified in the metadata, otherwise an empty string
     */
    get defaultPublicEndpoint() {
        return ((this.data.defaults || {}).publicEndpoint || "");
    }
    /**
     * Default mount point for the shared disk if specified in the metadata, otherwise an empty string
     */
    get defaultSharedDiskMountPoint() {
        return ((this.data.defaults || {}).sharedDiskMountPoint || "");
    }
    /**
     * Retrieves all additional system packages that should be installed, if specified in definition
     * @argument key The nami dependencies key for this platform. i.e. "debian-10"
     * @returns an array of system packages
     */
    systemPackagesDependencies(key) {
        let deps = null;
        if (this.data.dependencies) {
            deps = (this.data.dependencies.systemPackages || {})[key];
        }
        return deps || [];
    }
}
exports.StackDefinition = StackDefinition;
/**
 * Class for querying and managing entire stack definition
 */
class TierDefinition {
    constructor(stackDefinition, data, instanceData) {
        this.stackDefinition = stackDefinition;
        this.data = data;
        this.instanceData = instanceData;
    }
    /**
     * List of modules in this tier
     */
    get modules() {
        const list = this.data.modules || [];
        if (!this._modules) {
            this._modules = _.map(list, item => {
                return new bitnami_module_1.BitnamiModule(this, item);
            });
        }
        return this._modules;
    }
    /**
     * List of modules in this tier that are services
     */
    get services() {
        return _.reject(this.modules, item => {
            return !item.isService;
        });
    }
    /**
     * Provisioner instance that this object was created for
     */
    get provisioner() {
        return this.stackDefinition.provisioner;
    }
    /**
     * Logger instance
     */
    get logger() {
        return this.stackDefinition.provisioner.logger;
    }
    /**
     * List of ports to wait for
     */
    get waitForPorts() {
        return this.data.waitForPorts || [];
    }
    /**
     * List of ports that this tier exposes publicly
     */
    get publicPorts() {
        return this.data.ports && this.data.ports.public || [];
    }
    /**
     * List of internal ports (for inter-node communication)
     */
    get internalPorts() {
        return this.data.ports && this.data.ports.internal || [];
    }
    /**
     * Optional delay to wait before performing initialization (after waiting for ports), in seconds
     */
    get initializationDelay() {
        return this.data.initializationDelay || 0;
    }
    /**
     * List of tags for this tier
     */
    get tags() {
        if (this.data.tags) {
            if (!Array.isArray(this.data.tags)) {
                // handle incorrect metadata
                return [this.data.tags];
            }
            else {
                return this.data.tags;
            }
        }
        else {
            return [];
        }
    }
    /**
     * Mapping of ports - the key should be the port that the application exposes ; the value
     * should be a list of ports that the app port will be exposed as - such as:
     * `{3000: [80]}` - this will map application port 3000 as port 80 of the machine
     */
    get portMapping() {
        if (!this._portMapping) {
            this._portMapping = {};
            if (this.data.portMapping) {
                for (let port of Object.keys(this.data.portMapping)) {
                    let portList = this.data.portMapping[port];
                    // support invalid YAML definition properly
                    if (!Array.isArray(portList)) {
                        portList = [portList];
                    }
                    this._portMapping[port] = portList.map((port) => { return parseInt(port); });
                }
            }
        }
        return this._portMapping;
    }
    /**
     * List of files to create in the file system
     */
    get additionalFiles() {
        return this.data.additionalFiles || {};
    }
    /**
     * List of scripts to execute before initializing modules
     */
    get setupScripts() {
        return this.data.setupScripts || [];
    }
    /**
     * Get all aliases that should be specified for a peer
     * @param peername Name of the peer ; currently only "default" and "localhost" are supported
     */
    getPeerHostnames(peername) {
        let hostnames;
        // only "default" and "localhost" peer name is supported now
        peername = peername || "default";
        hostnames = (this.data.peerHostnames || {})[peername];
        if (!hostnames) {
            // fallback to stack definition
            hostnames = (this.stackDefinition.data.peerHostnames || {})[peername];
        }
        return (hostnames || []);
    }
}
exports.TierDefinition = TierDefinition;
