"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.getShadowRecords = exports.getGroupRecords = exports.getPasswdRecords = void 0;
const fs = require("fs");
class BaseLinuxAccountHandler {
    constructor(filename, idField, nameField, fields) {
        this.filename = filename;
        this.idField = idField;
        this.nameField = nameField;
        this.fields = fields;
    }
    get records() {
        if (!this._records) {
            this.read();
        }
        return this._records;
    }
    set records(v) {
        this._records = v;
    }
    _compare(v1, v2) {
        const a1 = v1;
        const a2 = v2;
        return (v1 && v2
            && ((!this.idField) || (v1[this.idField] === v2[this.idField]))
            && ((!this.nameField) || (v1[this.nameField] === v2[this.nameField])));
    }
    _findItemId(v) {
        let result = -1;
        let idx = 0;
        for (const row of this.records) {
            if (this._compare(row, v)) {
                result = idx;
                break;
            }
            idx += 1;
        }
        return result;
    }
    add(value, allowDuplicates) {
        if (!allowDuplicates) {
            if (this._findItemId(value) >= 0) {
                throw new Error("Duplicate item found");
            }
        }
        this.records.push(value);
    }
    replace(oldValue, ...newValues) {
        let idx = this._findItemId(oldValue);
        if (idx >= 0) {
            this.records.splice(idx, 1, ...newValues);
            return true;
        }
        else {
            return false;
        }
    }
    remove(value) {
        return this.replace(value);
    }
    _find(field, value) {
        let result;
        for (const row of this.records) {
            if (row[field] === value) {
                result = row;
            }
        }
        return result;
    }
    findByName(name) {
        return this._find(this.nameField, name);
    }
    findById(id) {
        return this._find(this.idField, id);
    }
    // allow forgetting without re-reading
    forget() {
        this._records = undefined;
        return this;
    }
    read() {
        const lines = fs.readFileSync(this.filename).toString().split("\n");
        this._records = [];
        for (const linestring of lines) {
            let line = linestring.split(":");
            if (line.length >= this.fields.length) {
                let row = {};
                let i = 0;
                for (const field of this.fields) {
                    if (field) {
                        let value = line[i];
                        if (field.type === "number") {
                            value = parseInt(value);
                        }
                        else if (field.type === "list") {
                            value = (value || "").split(field.separator || ",");
                        }
                        row[field.name] = value;
                    }
                    i += 1;
                }
                this._records.push(row);
            }
        }
    }
    write() {
        let contents = "";
        const bakfilename = `${this.filename}-`;
        const tempfilename = `${this.filename}__`;
        for (const record of this.records) {
            let row = [];
            for (const field of this.fields) {
                if (field) {
                    let value = record[field.name];
                    if ((field.type === "list") && Array.isArray(value)) {
                        value = value.join(field.separator || ",");
                    }
                    row.push(value);
                }
                else {
                    row.push("x");
                }
            }
            contents += row.join(":") + "\n";
        }
        this.forget();
        fs.writeFileSync(tempfilename, contents);
        fs.rmSync(bakfilename);
        fs.renameSync(this.filename, bakfilename);
        fs.renameSync(tempfilename, this.filename);
    }
}
;
function getPasswdRecords() {
    return new BaseLinuxAccountHandler("/etc/passwd", "uid", "username", [
        { name: "username" },
        null,
        { name: "uid", type: "number" },
        { name: "gid", type: "number" },
        { name: "fullname", type: "list" },
        { name: "home" },
        { name: "shell" }
    ]);
}
exports.getPasswdRecords = getPasswdRecords;
;
function getGroupRecords() {
    return new BaseLinuxAccountHandler("/etc/group", "gid", "groupname", [
        { name: "groupname" },
        null,
        { name: "gid", type: "number" },
        { name: "members", type: "list" }
    ]);
}
exports.getGroupRecords = getGroupRecords;
;
function getShadowRecords() {
    if (!fs.existsSync("/etc/shadow")) {
        return undefined;
    }
    return new BaseLinuxAccountHandler("/etc/shadow", null, "username", [
        { name: "username" },
        { name: "encryptedPassword" },
        { name: "lastPasswordChange" },
        { name: "minimumPasswordAge" },
        { name: "maximumPasswordAge" },
        { name: "passwordWarningPeriod" },
        { name: "passwordInactivityPeriod" },
        { name: "accountExpirationDate" },
        { name: "reservedField" }
    ]);
}
exports.getShadowRecords = getShadowRecords;
