"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.runProgram = void 0;
const fs = require("fs");
const child = require("child_process");
const signals = {
    SIGHUP: 1,
    SIGINT: 2,
    SIGQUIT: 3,
    SIGILL: 4,
    SIGTRAP: 5,
    SIGABRT: 6,
    SIGIOT: 6,
    SIGBUS: 10,
    SIGFPE: 8,
    SIGKILL: 9,
    SIGSEGV: 11,
    SIGSYS: 12,
    SIGPIPE: 13,
    SIGALRM: 14,
    SIGTERM: 15,
    SIGURG: 16,
    SIGSTOP: 17,
    SIGTSTP: 18,
    SIGCONT: 19,
    SIGCHLD: 20,
    SIGTTIN: 21,
    SIGTTOU: 22,
    SIGIO: 23,
    SIGXCPU: 24,
    SIGXFSZ: 25,
    SIGVTALRM: 26,
    SIGPROF: 27,
    SIGWINCH: 28,
    SIGINFO: 29,
    SIGUSR1: 30,
    SIGUSR2: 31,
};
const placeholderLogger = Object.fromEntries(["error", "warn", "info", "debug", "trace"].map((k) => { return [k, (_log) => { }]; }));
function _createTempFile() {
    let f = null;
    do {
        f = `/tmp/${Date.now()}${Math.random()}`;
    } while (fs.existsSync(f));
    fs.writeFileSync(f, "");
    return f;
}
function _argsArrayToString(arr) {
    return arr.map((arg) => {
        return `'${arg.replace(/'/g, "'\\''")}'`;
    }).join(" ");
}
/**
 * Run Program
 * @function $os~runProgram
 * @param {string} program - Program to execute
 * @param {string|string[]} arguments - Arguments. It can be either a string or an arry containing them
 * @param {RunProgramOptions} [options]
 * @param {boolean} [options.runInBackground=false] - Run the command in the background
 * @param {boolean} [options.retrieveStdStreams=false] - Returns a hash describing the process stdout, stderr and
 * exit code
 * @param {boolean} [options.detachStdStreams=false] - Save standard streams to temporary files while executing
 * the program (solves some processes hanging because of unclosed streams)
 * @param {string} [options.runAs=null] - User used to run the program as. Only when running as admin
 * @param {string} [options.cwd] - Working directory
 * @param {Object} [options.env={}] - Object containing extra environment variables to be made accesible to the running
 * process
 * @param {string} [options.input=null] - Value passed as stdin to the spawned process
 * @param {Object} [options.logger=null] - If not provided the global package logger will be used
 *
 * @example
 * // returns "Hello World"
 * runProgram('echo', 'Hello World')
 * @example <caption>Pass arguments as array</caption>
 * // returns mysql databases
 * runProgram('mysql', ['-uroot', '-pbitnami', '-e', 'show databases'], {runAs: 'mysql'});
 */
function runProgram(program, args, opts) {
    opts = Object.assign({}, {
        logger: null,
        runInBackground: false,
        detachStdStreams: false,
        retrieveStdStreams: false,
    }, opts);
    args = args || [];
    const cmdLogger = opts.logger || placeholderLogger;
    let stdoutFile = null;
    let stderrFile = null;
    const spawnOpts = Object.assign({
        maxBuffer: Infinity
    });
    spawnOpts.env = Object.assign({}, process.env, opts.env);
    ["input", "cwd"].forEach(function (k) {
        if (k in opts) {
            spawnOpts[k] = opts[k];
        }
    });
    if (opts.runInBackground) {
        spawnOpts.stdio = "ignore";
    }
    else if (opts.detachStdStreams) {
        stdoutFile = _createTempFile();
        stderrFile = _createTempFile();
        spawnOpts.stdio = ["ignore", fs.openSync(stdoutFile, "w+"), fs.openSync(stderrFile, "w+")];
    }
    if (process.getuid() === 0 && opts.runAs !== undefined) {
        // Because of the dependency loop, we need to do this here
        const getUid = (username) => parseInt(runProgram("id", ["-u", username]));
        spawnOpts.uid = getUid(opts.runAs);
    }
    let r = null;
    cmdLogger.debug(`[runProgram] Executing: ${program} ${args}`);
    if (opts.runInBackground && args[args.length - 1].toString().trim() !== "&") {
        let strArgs = _argsArrayToString([program].concat(args));
        strArgs += " &";
        const callArgs = ["-c", strArgs];
        cmdLogger.trace(`[runProgram] Executing internal command: '/bin/sh' ${JSON.stringify(callArgs)}`);
        r = child.spawnSync("/bin/sh", callArgs, spawnOpts);
    }
    else {
        cmdLogger.trace(`[runProgram] Executing internal command: ${program} ${JSON.stringify(args)}`);
        r = child.spawnSync(program, args, spawnOpts);
    }
    let stdResult;
    if (r.error !== undefined) {
        stdResult = Object.assign({ code: 1, stdout: "", stderr: r.error });
    }
    else {
        stdResult = Object.assign({ code: r.status, stderr: "", stdout: "" });
        if (opts.detachStdStreams) {
            Object.entries({ stderr: stderrFile, stdout: stdoutFile }).forEach(([key, file]) => {
                try {
                    stdResult[key] = fs.readFileSync(file, { encoding: "utf8" });
                }
                catch (e) {
                    stdResult[key] = "";
                }
            });
        }
        else if (opts.runInBackground) {
            stdResult.stderr = (r.status === 0) ? "" : "Error executing program";
            stdResult.stdout = "";
        }
        else {
            stdResult.stderr = r.stderr.toString();
            stdResult.stdout = r.stdout.toString();
        }
    }
    // spawn does not return an exit code if its process is killed, but its exit code is 'null'
    if (r.signal != null && typeof signals[r.signal] !== "undefined" && stdResult.code === null) {
        stdResult.code = 128 + signals[r.signal];
        if (stdResult.stderr === "") {
            stdResult.stderr = "Terminated\n";
        }
    }
    cmdLogger.debug(`[runProgram] RESULT: ${JSON.stringify(stdResult)}`);
    if (opts.retrieveStdStreams) {
        return stdResult;
    }
    if (stdResult.code !== 0) {
        let result = stdResult.stderr || "";
        if (result.toString().trim().length === 0) {
            result = `Program exited with exit code ${stdResult.code}`;
        }
        throw (new Error(result));
    }
    else {
        return stdResult.stdout;
    }
}
exports.runProgram = runProgram;
