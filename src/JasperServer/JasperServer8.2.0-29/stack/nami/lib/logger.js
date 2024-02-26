"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.WinstonLogger = exports.LogLevel = void 0;
const winston = require("winston");
const util = require("util");
// default levels
var LogLevel;
(function (LogLevel) {
    LogLevel[LogLevel["DISABLED"] = -1] = "DISABLED";
    LogLevel[LogLevel["ERROR"] = 0] = "ERROR";
    LogLevel[LogLevel["WARN"] = 1] = "WARN";
    LogLevel[LogLevel["INFO"] = 2] = "INFO";
    LogLevel[LogLevel["VERBOSE"] = 3] = "VERBOSE";
    LogLevel[LogLevel["DEBUG"] = 4] = "DEBUG";
    LogLevel[LogLevel["TRACE"] = 5] = "TRACE";
    LogLevel[LogLevel["TRACE8"] = 6] = "TRACE8";
})(LogLevel = exports.LogLevel || (exports.LogLevel = {}));
class WinstonLogger {
    constructor(options) {
        options = options || {};
        options = Object.assign({
            consoleLevel: options.consoleLevel || options.logLevel || LogLevel.ERROR,
            fileLevel: options.fileLevel || options.logLevel || LogLevel.INFO,
            logLevels: ["error", "warn", "info", "verbose", "debug", "trace", "trace8"],
        }, options || {});
        this._setLevels(options.logLevels);
        options.consoleLevel = this._getLevelId(options.consoleLevel);
        options.fileLevel = this._getLevelId(options.fileLevel);
        this._winston = new winston.Logger({
            levels: this._logLevelMap
        });
        this.maxLevel = Math.max(options.consoleLevel, options.fileLevel);
        this._recreateConsoleTransport(options.consoleLevel);
    }
    _recreateConsoleTransport(consoleLevel) {
        // try to remove the Console transport if it is there
        try {
            this._winston.remove(winston.transports.Console);
        }
        catch (e) {
            // no Console transport present
        }
        if (consoleLevel > LogLevel.DISABLED) {
            this._winston.add(winston.transports.Console, {
                level: this._logLevels[consoleLevel],
                timestamp: true
            });
        }
    }
    /**
     * Current log level, as enum
     */
    get logLevel() {
        return this.maxLevel;
    }
    set logLevel(value) {
        this.maxLevel = this._getLevelId(value);
        this._recreateConsoleTransport(value);
    }
    /**
     * Current log level, as string (name of the level)
     */
    get logLevelName() {
        return this._logLevels[this.logLevel];
    }
    _getLevelId(logLevel) {
        if (this._logLevelMap[logLevel]) {
            logLevel = this._logLevelMap[logLevel];
        }
        const logLevelId = parseInt(logLevel);
        if (isNaN(logLevelId)) {
            throw new Error(`Invalid log level ${logLevel}`);
        }
        return logLevelId;
    }
    _setLevels(logLevels) {
        let i = 0;
        this._logLevels = logLevels;
        this._logLevelMap = {};
        for (let level of logLevels) {
            this._logLevelMap[level] = i;
            i++;
        }
    }
    _log(level, args) {
        if (level > this.maxLevel) {
            return;
        }
        args = [].splice.call(args, 0);
        // if last argument is a stack trace, print it as trace
        if (args && (args.length > 0)) {
            if (args[args.length - 1] && args[args.length - 1].message && args[args.length - 1].stack) {
                let err = args.pop();
                if (this.maxLevel >= LogLevel.TRACE) {
                    let errorMessage = "\n" + err.stack;
                    args.push(errorMessage);
                }
                else {
                    args.push(err.message || "");
                }
            }
            else {
                if (args[args.length - 1] instanceof Function) {
                    let func = args.pop();
                    args.push(func.call(this));
                }
                // TODO: is this needed?
                if (args[args.length - 1] instanceof Object) {
                    let obj = args.pop();
                    args.push(util.inspect(obj));
                }
            }
        }
        this._winston.log(this._logLevels[level], args.join(" "));
    }
    /**
     * log one or more messages to log, as ERROR level
     * if last argument is an Error object, it will be logged as trace level if
     * log level is trace
     */
    error(...args) {
        this._log(LogLevel.ERROR, args);
    }
    /**
     * log one or more messages to log, as WARN level
     * if last argument is an Error object, it will be logged as trace level if
     * log level is trace
     */
    warn(...args) {
        this._log(LogLevel.WARN, args);
    }
    /**
     * log one or more messages to log, as INFO level
     * if last argument is an Error object, it will be logged as trace level if
     * log level is trace
     */
    info(...args) {
        this._log(LogLevel.INFO, args);
    }
    /**
     * log one or more messages to log, as VERBOSE level
     * if last argument is an Error object, it will be logged as trace level if
     * log level is trace
     */
    verbose(...args) {
        this._log(LogLevel.VERBOSE, args);
    }
    /**
     * log one or more messages to log, as DEBUG level
     * if last argument is an Error object, it will be logged as trace level if
     * log level is trace
     */
    debug(...args) {
        this._log(LogLevel.DEBUG, args);
    }
    /**
     * log one or more messages to log, as TRACE level
     * if last argument is an Error object, it will be logged as trace level if
     * log level is trace
     */
    trace(...args) {
        this._log(LogLevel.TRACE, args);
    }
    // Compatibility with nami-logger interface
    trace2(...args) {
        this.trace(args);
    }
    trace3(...args) {
        this.trace(args);
    }
    /**
     * Handle an error, by logging it as ERROR level and exiting proces
     * if last argument is an Error object, it will be logged as trace level if
     * log level is trace
     */
    handleError(...args) {
        this._log(LogLevel.ERROR, args);
        process.exit(1);
    }
}
exports.WinstonLogger = WinstonLogger;
