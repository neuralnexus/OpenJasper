"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.Download = void 0;
const crypto = require("crypto");
const child_process = require("child_process");
const _ = require("lodash");
const https = require("https");
const http = require("http");
const url = require("url");
const fs = require("fs");
/**
 * Class for performing multi-purpose downloads
 */
class Download {
    /**
     * Constructor ; does not perform actual download, the `download` method does
     * @options Options for performing download
     */
    constructor(options) {
        options = Object.assign({}, options);
        this.timeout = options.timeout;
        this.hashType = options.hashType;
        this.unpackDirectory = options.unpackDirectory;
        this.localFile = options.localFile;
        this.tarArguments = options.tarArguments || ["-xz", "--no-same-owner"];
        this.returnData = options.returnData;
        this.returnBuffer = options.returnBuffer;
        this.headers = options.headers || {};
        this.ignoreErrors = options.ignoreErrors;
    }
    _initReturnData() {
        if (this.returnData) {
            this.body = "";
            this._response.on("data", (d) => {
                this.body += d;
            });
        }
        if (this.returnBuffer) {
            this.buffer = new Buffer(0);
            this._response.on("data", (d) => {
                this.buffer = Buffer.concat([this.buffer, d]);
            });
        }
    }
    _initWriteToFile() {
        if (this.localFile) {
            this._file = fs.createWriteStream(this.localFile);
            this._response.pipe(this._file);
            this._filePromise = new Promise(resolve => {
                this._file.on("finish", () => {
                    resolve(this);
                });
            });
        }
    }
    _initRequestHandling() {
        this._requestPromise = new Promise((resolve, reject) => {
            this._response.on("end", () => {
                this.statusCode = this._response.statusCode;
                if (!this.ignoreErrors && (this.statusCode >= 400)) {
                    throw new Error(`HTTP request returned ${this.statusCode} HTTP status code`);
                }
                resolve(this);
            });
            this._request.on("error", (error) => {
                reject(error);
            });
        });
    }
    _initHash() {
        if (this.hashType) {
            this._hash = crypto.createHash(this.hashType);
            this._hashPromise = new Promise((resolve, reject) => {
                this._response.on("data", (d) => {
                    this._hash.update(d);
                });
                this._response.on("end", () => {
                    this.hash = this._hash.digest("hex");
                    resolve(this);
                });
            });
        }
    }
    _initUnpack() {
        if (this.unpackDirectory) {
            this._unpackPromise = new Promise((resolve, reject) => {
                this._unpackProcess = child_process.spawn("tar", this.tarArguments, {
                    cwd: this.unpackDirectory
                });
                this._response.pipe(this._unpackProcess.stdin);
                this._unpackProcess.on("close", (code) => {
                    if (code !== 0) {
                        reject(new Error(`Unpacking failed; tar exit code: ${code}`));
                    }
                    else {
                        resolve(this);
                    }
                });
            });
        }
    }
    async _waitForPromises(promises) {
        promises = _.reject(promises, p => {
            return ((p === undefined) || (p === null));
        });
        return Promise.all(promises);
    }
    /**
     * Perform the actual download ; the promise finishes when all requested operations
     * (unpack, hash, ...) have finished as well
     * @param requestUrlString URL to download ; can be `http` or `https` protocol
     */
    async download(requestUrlString) {
        let method;
        let reqUrl = url.parse(requestUrlString);
        if (reqUrl.protocol.match(/https/)) {
            method = https.request;
        }
        else {
            method = http.request;
        }
        await new Promise((_resolve, _reject) => {
            let options = {
                protocol: reqUrl.protocol,
                hostname: reqUrl.hostname,
                port: parseInt(reqUrl.port),
                path: reqUrl.path,
                headers: this.headers,
            };
            this._request = method(options, (response) => {
                this._response = response;
                _resolve(this);
            });
            this._request.on("error", (e) => {
                _reject(e);
            });
            if (this.timeout) {
                this._request.setTimeout(this.timeout, () => {
                    _reject(new Error("Request timed out"));
                });
            }
            this._request.end();
        });
        this._initRequestHandling();
        this._initWriteToFile();
        this._initHash();
        this._initUnpack();
        this._initReturnData();
        await this._waitForPromises([
            this._filePromise,
            this._hashPromise,
            this._unpackPromise,
            this._requestPromise
        ]);
        return this;
    }
}
exports.Download = Download;
