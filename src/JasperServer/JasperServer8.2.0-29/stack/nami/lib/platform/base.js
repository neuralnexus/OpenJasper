"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.BasePlatform = void 0;
const _ = require("lodash");
const fs = require("fs");
/**
 * Class providing base functionality
 */
class BasePlatform {
    constructor(options) {
        options = Object.assign({}, options || {});
        this.logger = options.logger;
        this.name = options.platformName;
        this.storageData = options.storageData;
        // assume 64 bits by default
        this.bits = options.bits || 64;
        this.platformTags = [
            `${this.bits}bit`
        ];
    }
    /**
     * Metadata about various platform specific information
     */
    get pathInfo() {
        if (!this._pathInfo) {
            this._initPathInfo();
        }
        return this._pathInfo;
    }
    _pathValueToArray(value) {
        let result = [];
        if (Array.isArray(value)) {
            result = value;
        }
        else if (!((value === null) || (value === undefined) || (value === ""))) {
            result = [value];
        }
        return result;
    }
    /**
     * Modify `pathString` by adding or removing paths and return modified string
     * @param options path elements to add and/or remove
     */
    modifyPathString(pathString, options) {
        let paths = pathString.split(this.pathInfo.pathSeparator);
        let newPaths = [];
        let toAdd = this._pathValueToArray(options.add);
        let toRemove = this._pathValueToArray(options.remove);
        let idx;
        this.logger.trace("modifyPathString: start", {
            from: pathString, list: paths, toAdd: toAdd, toRemove: toRemove
        });
        for (let path of paths) {
            idx = toRemove.indexOf(path);
            if (idx >= 0) {
                toRemove.splice(idx, 1);
            }
            else if (path.length > 0) {
                newPaths.push(path);
                // if already on list to add, remove it so we do not add it
                idx = toAdd.indexOf(path);
                if (idx >= 0) {
                    toAdd.splice(idx, 1);
                }
            }
        }
        paths = newPaths;
        if (options.addAtBeginning) {
            paths = toAdd.concat(paths);
        }
        else {
            paths = paths.concat(toAdd);
        }
        this.logger.trace("modifyPathString: converted", { list: paths });
        return paths.join(this.pathInfo.pathSeparator);
    }
    /**
     * Modify PATH environment variable for current process and new child processes
     * @param options path elements to add and/or remove
     */
    modifyProcessPath(options) {
        let path = process.env["PATH"] || "";
        process.env["PATH"] = this.modifyPathString(path, options);
    }
    _toPortMapping(portMappings) {
        let result = {};
        for (let srcPort of Object.keys(portMappings)) {
            let destPorts = portMappings[srcPort];
            srcPort = srcPort.toString();
            if ((destPorts === undefined) || (destPorts === null)) {
                destPorts = [];
            }
            else if (!Array.isArray(destPorts)) {
                destPorts = [destPorts];
            }
            for (let destPort of destPorts) {
                if (result[destPort.toString()]) {
                    throw new Error(`Error in port mapping - duplicated port ${destPort}`);
                }
                result[destPort.toString()] = srcPort.toString();
            }
        }
        return result;
    }
    _getPortMapping() {
        this.storageData.data.portMapping = this.storageData.data.portMapping || {};
        const bootId = this.storageData.data.portMapping.bootId || "";
        if (bootId.toString() !== this.getUniqueBootId()) {
            this.storageData.data.portMapping.ports = {};
        }
        return (this.storageData.data.portMapping.ports);
    }
    _storePortMapping(portMapping) {
        this.storageData.data.portMapping = this.storageData.data.portMapping || {};
        this.storageData.data.portMapping.bootId = this.getUniqueBootId();
        this.storageData.data.portMapping.ports = portMapping;
        this.storageData.save();
    }
    /**
     * Set port mappings that are managed by provisioner ; previous port mappings
     * set by provisioner will be overwritten, but other system rules will not
     * be modified
     * @param portMappings List of new mappings to apply
     */
    setPortMappings(portMappings) {
        const portMapping = this._toPortMapping(portMappings);
        const currentPortMapping = this._getPortMapping();
        // get list of all source ports (i.e. ports exposed by application)
        const allPorts = _.uniq(Object.keys(currentPortMapping).concat(Object.keys(portMapping)));
        // iterate over all ports exposed now/before by application
        for (let destPort of allPorts) {
            // detect if port mapping has changed ; if it has, add it
            if (currentPortMapping[destPort] !== portMapping[destPort]) {
                // remove mapping if no longer valid
                if (currentPortMapping[destPort]) {
                    this._deletePortMapping(currentPortMapping[destPort], destPort);
                }
                // add new mapping
                if (portMapping[destPort]) {
                    this._addPortMapping(portMapping[destPort], destPort);
                }
            }
        }
        this._storePortMapping(portMapping);
    }
    /**
     * Modify file contents to include a file section ; used in createFileSection function ;
     * this code adds new section at the end or replaces existing section if it exists ;
     * this is especially useful for adding or modifying parts of files - such as modifying
     * `/etc/hosts` or shell profiles to include additional items
     * @param contents Current contents of the file
     * @param options Options for the section to add/update
     */
    createFileContentsSection(contents, options) {
        let lines = contents.split("\n");
        let startIdx, endIdx;
        let section;
        let title = options.title || "PROVISIONER SECTION";
        let beginLine = options.beginLine || `# BEGIN ${title}`;
        let endLine = options.endLine || `# END ${title}`;
        startIdx = lines.indexOf(beginLine);
        endIdx = lines.indexOf(endLine);
        if ((startIdx < 0) || (endIdx < 0)) {
            startIdx = -1;
            endIdx = -1;
        }
        if ((startIdx >= 0) && (endIdx >= startIdx)) {
            // remove begin section
            lines.splice(startIdx, 1);
            // retrieve actual contents
            section = lines.splice(startIdx, endIdx - startIdx - 1).join("\n");
            // remove end section
            lines.splice(startIdx, 1);
        }
        else {
            section = "";
            startIdx = options.addAtBeginning ? 0 : lines.length;
        }
        if (options.contents) {
            section = options.contents;
        }
        else if (options.contentsCallback) {
            section = options.contentsCallback(section);
        }
        lines.splice(startIdx, 0, beginLine);
        lines.splice(startIdx + 1, 0, section);
        lines.splice(startIdx + 2, 0, endLine);
        // ensure file has proper EOL marker
        if (lines[lines.length - 1] !== "") {
            lines.push("");
        }
        return lines.join("\n");
    }
    /**
    * Modify contents of a file to include a file section ;
    * this code adds new section at the end or replaces existing section if it exists ;
    * this is especially useful for adding or modifying parts of files - such as modifying
    * `/etc/hosts` or shell profiles to include additional items
    * @param filename Path to the file to addd/update contents
    * @param options Options for the section to add/update
     */
    createFileSection(filename, options) {
        let contents = "";
        if (fs.existsSync(filename)) {
            contents = fs.readFileSync(filename).toString();
        }
        else if (!options.create) {
            throw new Error(`File ${filename} not found`);
        }
        contents = this.createFileContentsSection(contents, options);
        fs.writeFileSync(filename, contents);
    }
    _deletePortMapping(srcPort, destPort) {
        this.logger.debug(`Delete port mapping for ${srcPort} - ${destPort}`);
    }
    _addPortMapping(srcPort, destPort) {
        this.logger.debug(`Add port mapping for ${srcPort} - ${destPort}`);
    }
    async initializePlatformBeforeUnpack() {
        this.logger.debug("Performing pre-unpack platform initialization");
    }
    async initializePlatformAfterUnpack() {
        this.logger.debug("Performing post-unpack platform initialization");
    }
    async initializePlatformAfterProvisionSettings() {
        this.logger.debug("Performing provisioning platform initialization");
    }
}
exports.BasePlatform = BasePlatform;
