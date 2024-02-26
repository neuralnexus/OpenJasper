"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.askQuestion = exports.generateRandomPassword = exports.resolveHostname = exports.waitForPort = exports.waitForPorts = exports.retry = exports.delayMs = exports.createHashAsHex = void 0;
const crypto = require("crypto");
const dns_1 = require("dns");
const readline = require("readline");
const net_1 = require("net");
function createHashAsHex(input, type) {
    const hash = crypto.createHash(type || "sha256");
    hash.update(input);
    return hash.digest("hex");
}
exports.createHashAsHex = createHashAsHex;
async function delayMs(milliseconds) {
    return new Promise((resolve) => setTimeout(resolve, milliseconds));
}
exports.delayMs = delayMs;
async function retry(callback, options) {
    let i = 1;
    let result;
    options = Object.assign({
        attempts: 5,
        delay: 10,
        errorMessage: "Unable to perform operation"
    }, options);
    while (i <= options.attempts) {
        try {
            result = callback();
            if (result instanceof Promise) {
                result = await result;
            }
        }
        catch (e) {
            if (options.logger) {
                options.logger.warn(options.errorMessage, e);
            }
            if (i === options.attempts) {
                throw e;
            }
            await delayMs(options.delay * 1000);
            i += 1;
            continue;
        }
        break;
    }
    return result;
}
exports.retry = retry;
async function waitForPorts(options) {
    let success = true;
    const promises = options.ports.map(port => {
        return waitForPort(Object.assign({ port: port }, options));
    });
    for (const promise of promises) {
        if (!(await promise)) {
            success = false;
        }
    }
    return success;
}
exports.waitForPorts = waitForPorts;
async function waitForPort(options) {
    let i = 0;
    let startTime;
    let elapsedTime;
    let promise;
    let success;
    let socket;
    let timeout;
    options = Object.assign({
        host: "127.0.0.1",
        timeout: 15000,
        retries: 60
    }, options);
    const msg = `Waiting for host ${options.host} port ${options.port}:`;
    // retry multiple times
    for (i = 0; i < options.retries; i++) {
        startTime = Date.now();
        if (options.logger) {
            options.logger.debug(`${msg} attempt ${i} / ${options.retries}`);
        }
        // create a promise and either time out, return an error or success
        promise = new Promise((resolve, reject) => {
            timeout = setTimeout(() => {
                resolve("timeout");
            }, options.timeout);
            socket = new net_1.Socket();
            socket.once("connect", () => {
                resolve(true);
            });
            socket.once("error", () => {
                resolve("error");
            });
            socket.connect(options.port, options.host);
        });
        success = await promise;
        if (options.logger) {
            options.logger.debug(`${msg} result is ${success}`);
        }
        // clean up any pending resources
        clearTimeout(timeout);
        try {
            socket.removeAllListeners("error");
            socket.removeAllListeners("connect");
            socket.destroy();
        }
        catch (e) {
            // do not report error
        }
        if (success === true) {
            // if connection worked, return it
            return true;
        }
        else {
            // if connection was not successful, wait if the attempt was more than timeout
            // this will allow more predictable waitForPort behavior
            elapsedTime = Date.now() - startTime;
            elapsedTime = options.timeout - elapsedTime;
            if (elapsedTime) {
                await new Promise((resolve) => {
                    setTimeout(resolve, elapsedTime);
                });
            }
        }
    }
    return false;
}
exports.waitForPort = waitForPort;
/**
 * Helper to resolve hostname to IP address as a promise
 * if options or family is not specified, it tries to resolve IPv4 first, then IPv6
 * @param name Hostname or IP address
 * @param options Options to pass to resolve function from mdns
 */
async function resolveHostname(name, options) {
    // resolve hostname, try resolving IPv4 first
    if ((!options) || (!(options.family))) {
        let result;
        result = await resolveHostname(name, Object.assign({ family: 4 }, options));
        if (result) {
            return result;
        }
        return resolveHostname(name, Object.assign({ family: 6 }, options));
    }
    return new Promise((resolve, reject) => {
        let timer;
        if (options.timeout) {
            timer = setTimeout(() => {
                reject(new Error(`Resolving ${name} has timed out`));
            }, options.timeout);
        }
        dns_1.lookup(name, options, function (err, address, family) {
            if (timer) {
                // clean up any pending resources
                clearTimeout(timer);
            }
            if (err) {
                reject(err);
            }
            else {
                resolve(address);
            }
        });
    });
}
exports.resolveHostname = resolveHostname;
/**
 * Generate password in a secure way, using /dev/random or /dev/urandom as input if they are
 * available ;
 * the password will always contain an upper case character, lower case character and a digit
 * @param length number of characters the password should contain
 */
function generateRandomPassword(length) {
    let result = "";
    while (!((result.match(/[A-Z]/) && result.match(/[a-z]/) && result.match(/[0-9]/)))) {
        result = "";
        while (result.length < length) {
            result += crypto.pseudoRandomBytes(Math.max(length, 32)).toString().replace(/[^a-zA-Z0-9]/g, "");
        }
        result = result.slice(0, length);
    }
    return result;
}
exports.generateRandomPassword = generateRandomPassword;
;
/**
 * Ask a question
 */
async function askQuestion(message, options) {
    let answer;
    let readlineInterface = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });
    options = Object.assign({ type: "boolean" }, options);
    return new Promise(async (resolve, reject) => {
        if (options.type === "boolean") {
            while (true) {
                let line = await new Promise(resolve => {
                    readlineInterface.question(`${message}? [y/N]\n`, (answer) => {
                        readlineInterface.close();
                        resolve(answer.trim().toLowerCase());
                    });
                });
                if ((line[0] === "y") || (line[0] === "t")) {
                    resolve(true);
                    break;
                }
                else {
                    resolve(false);
                    break;
                }
            }
        }
        else {
            reject(`Unable to handle output for ${options.type} question`);
        }
    });
}
exports.askQuestion = askQuestion;
