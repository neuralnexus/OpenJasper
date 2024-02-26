"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.ArgumentHandler = void 0;
/**
 * Class for handling definition arguments based on the provisioner values
 */
class ArgumentHandler {
    /**
     * Create instance of arguments handler
     * @param argumentsDefinition   definition of the arguments to handle
     * @param provisioner           provisioner instance keeping the values to map
     * @param logger                internal logger
     */
    constructor(argumentsDefinition, provisioner, logger) {
        /**
         * Provisioner map
         * Maps function-keys to function-values
         *
         * TODO: Get rid of the `item` parameter in `nodePeers`
         */
        this.provisionerMap = {
            instanceData: () => this.provisioner.instanceData,
            peerAddress: () => this.provisioner.peerAddress,
            peerAddressHostname: () => this.provisioner.peerAddressHostname,
            localAddress: () => this.provisioner.localAddressHostname,
            peerAddressHost: () => this._getPeerAddressHost,
            peerUsername: () => this.provisioner.defaultOrPeerUsername,
            peerPassword: () => this._getPeerPassword,
            shortPeerPassword: () => this._getShortPeerPassword,
            extraServices: () => this.provisioner.extraServices,
            appUsername: () => this.provisioner.defaultOrAppUsername,
            appPassword: () => this.provisioner.defaultOrAppPassword,
            shortAppPassword: () => this.provisioner.defaultOrAppPassword.substring(0, 16),
            appDatabase: () => this.provisioner.defaultOrAppDatabase,
            clusterName: () => this.provisioner.defaultOrClusterName,
            clusterQuorum: () => this.provisioner.defaultOrClusterQuorum,
            persistentNode: () => this.provisioner.defaultOrPersistentNode,
            booleanInput: () => this.provisioner.defaultOrBooleanInput,
            configOverrides: () => this.provisioner.defaultOrConfigOverrides,
            snitch: () => this.provisioner.defaultOrSnitch,
            nodeIndex: () => this.provisioner.peerNodes.index.toString(),
            oneBasedNodeIndex: () => this.provisioner.oneBasedNodeIndex,
            nodeName: () => this.provisioner.peerNodename,
            nodeHostname: () => this.provisioner.peerHostname,
            nodePeers: (item) => this._getNodePeers(item),
            firstNodeName: () => this.provisioner.peerNodenames[0],
            publicEndpoint: () => this.provisioner.defaultOrPublicEndpoint,
        };
        /**
         * Digest map
         * Maps function-keys to function-values
         */
        this.digestMap = {
            join: (ha) => this.join(ha.join),
            concat: (ha) => this.concat(ha.concat),
            value: (ha) => this.value(ha),
            generatePassword: (ha) => this.generatePassword(ha.generatePassword),
            modifiers: (ha) => "",
            extraServiceValue: (ha) => this.getValueFromExtraServices(ha.extraServiceValue),
        };
        this.argumentsDefinition = argumentsDefinition;
        this.provisioner = provisioner;
        this._logger = logger;
    }
    /**
     * Instance of logger
     */
    get logger() {
        return this._logger;
    }
    /**
     * Render argument
     */
    renderArgument(argument) {
        if (typeof argument === "string") {
            return argument.toString();
        }
        else {
            return Object.keys(argument).map(k => this.digest(argument, k)).join("");
        }
    }
    /**
     * Join values within a separator
     * @param object containing the data to join
     * @param object.separator to join with
     * @param object.values    to join
     */
    join(object) {
        if (!object.values || object.values.length === 0) {
            throw new Error(`Unexpected 'join' argument with an empty 'values' property`);
        }
        const separator = object.separator ? object.separator : ",";
        return object.values.map(arg => this.renderArgument(arg)).join(separator);
    }
    /**
     * Concat an array of items
     * @param array of items to concat
     */
    concat(array) {
        return array.map(arg => this.renderArgument(arg)).join("");
    }
    /**
     * Get the provisioner value mapped to a item
     * @param item           to get the value from provisioner
     * @param item.value     to get from provisioner
     * @param item.modifiers to perform in certain values
     */
    value(item) {
        const value = item.value;
        const fnc = this.provisionerMap[value];
        if (fnc == null) {
            throw new Error(`Unable to handle '${value}' provisioner mapping.`);
        }
        return fnc(item);
    }
    /**
     * Get deployment peer address host
     */
    get _getPeerAddressHost() {
        let host = "";
        if (!this.provisioner.peerAddress) {
            this.logger.error("peerAddressHost was requested, but no peerAddress argument was found");
        }
        else {
            host = this.provisioner.peerAddress.split(".")[0];
        }
        return host;
    }
    /**
     * Get deployment peer password
     */
    get _getPeerPassword() {
        this.provisioner.warnIfPeerPasswordNotSet();
        return this.provisioner.defaultOrPeerPassword;
    }
    /**
     * Get deployment short peer password
     */
    get _getShortPeerPassword() {
        this.provisioner.warnIfPeerPasswordNotSet();
        return this.provisioner.defaultOrPeerPassword.substring(0, 16);
    }
    /**
     * Get deployment node peers
     * @param item containing modifiers
     */
    _getNodePeers(item) {
        // Get all nodenames
        let nodenames = this.provisioner.peerNodenames;
        let slice = [0, nodenames.length];
        // Get modifiers
        if (item.modifiers) {
            const modifiers = item.modifiers;
            if (modifiers.slice) {
                slice = modifiers.slice;
            }
            if (modifiers.append) {
                nodenames = nodenames.map(n => `${n}${modifiers.append}`);
            }
            if (modifiers.prepend) {
                nodenames = nodenames.map(n => `${modifiers.prepend}${n}`);
            }
        }
        return nodenames.slice(slice[0], slice[1]).join(",");
    }
    /**
     * Return object property for extra services
     * @param item containing path to object (name and property)
     * @param item.name of the service
     * @param item.property specific property of the service
     */
    getValueFromExtraServices(item) {
        const extraServices = this.provisioner.extraServices;
        if (!item.name) {
            throw new Error(`The provided service name ('${item.name}') is not correct`);
        }
        const service = extraServices.find(s => s.name === item.name);
        if (!service) {
            throw new Error(`Unable to find '${item.name}' service in the provisioned extra services: ` +
                JSON.stringify(extraServices, null, 2));
        }
        if (!item.property) {
            throw new Error(`The provided service property ('${item.property}') is not correct`);
        }
        const property = service[item.property];
        if (!property) {
            throw new Error(`Unable to find '${item.property}' property in '${item.name}' service: ` +
                JSON.stringify(service, null, 2));
        }
        return property;
    }
    /**
     * Generate a password from a seed (shared-unique-id).
     * If specified, it can be encoded as a fernet key.
     * @param prefix to use to form a shared unique ID
     * @param type is used to define the format of the generated password
     */
    generatePassword(arg) {
        let prefix = "";
        let type = "";
        if (typeof arg === "string") {
            prefix = arg;
        }
        else {
            prefix = arg.prefix;
            type = arg.type;
        }
        let password = this.provisioner.getDerivedSharedUniqueId(prefix);
        if (type === "fernet") {
            password = this.provisioner.getDerivedFernetKey(password || "");
        }
        return password;
    }
    /**
     * Digest an argument based on a given function
     * @param hashArgument to digest
     * @param fn           function to apply
     */
    digest(hashArgument, fn) {
        const fnc = this.digestMap[fn];
        if (fnc == null) {
            throw new Error(`Invalid property '${fn}' in object: ${JSON.stringify(hashArgument)}`);
        }
        return fnc(hashArgument);
    }
    /**
     * Arguments rendering
     */
    get arguments() {
        if (!this._arguments) {
            this._arguments = this.argumentsDefinition.map(arg => this.renderArgument(arg));
        }
        return this._arguments;
    }
}
exports.ArgumentHandler = ArgumentHandler;
