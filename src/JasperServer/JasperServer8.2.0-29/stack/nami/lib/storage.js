"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.StorageManager = exports.StorageData = void 0;
const fs = require("fs");
const path = require("path");
/**
 * Class for handling single element that is stored ; a single element maps to single file on disk
 * and is written in an atomic way using `save` function;
 * currently all data is stored as ~/.provisioner/{{name}}.json`
 */
class StorageData {
    /**
     * Constructor
     * @param manager Instance of StorageManager creating this object
     * @param name Unique name for storage ; used for filename for storing the data
     * @param dataType Format to use for storing ; currently only `json` is supported
     */
    constructor(manager, name, dataType) {
        this.manager = manager;
        this.name = name;
        this.dataType = dataType;
        this.dataType = this.dataType || "json";
        this._fileName = path.join(manager.prefix, `${this.name}.${this.dataType}`);
        this.data = {};
    }
    /**
     * Load data from disk, overwriting any locally stored data
     */
    load() {
        if (fs.existsSync(this._fileName)) {
            if (this.dataType === "json") {
                this.data = JSON.parse(fs.readFileSync(this._fileName).toString());
            }
            else {
                this.data = fs.readFileSync(this._fileName).toString();
            }
        }
    }
    /**
     * Save data to disk, overwriting any previous data in the storage
     */
    save() {
        // TODO: backup old data ?
        if (this.dataType === "json") {
            fs.writeFileSync(this._fileName, JSON.stringify(this.data));
        }
        else {
            fs.writeFileSync(this._fileName, this.data);
        }
    }
}
exports.StorageData = StorageData;
/**
 * Class for handling storage for all provisioner based logic;
 * the class is mainly used to get instances of `StorageData` objects and makes sure only one
 * instance of `StorageData` object with same name is created - creating the same object when
 * first needed and then returning it
 */
class StorageManager {
    /**
     * Constructor
     * @param provisioner Instance of provisioner that this object will be used with
     * @param prefix Directory where data is stored; defaults to `~/.provisioner`
     */
    constructor(provisioner, prefix) {
        this.provisioner = provisioner;
        this.prefix = prefix;
        this._items = {};
        this.prefix = path.normalize(this.prefix || path.join(process.env.HOME, ".provisioner"));
    }
    /**
     * Get an instance of `StorageData` based on the name; makes sure only one
     * instance of `StorageData` object with same name is created - creating the same object when
     * first needed and then returning it
     */
    getItem(name, dataType) {
        if (!this._items[name]) {
            fs.mkdirSync(this.prefix, { recursive: true });
            this._items[name] = new StorageData(this, name, dataType);
            this._items[name].load();
        }
        return this._items[name];
    }
}
exports.StorageManager = StorageManager;
