const runAll = require('npm-run-all');
const execWithLockFile = require('./lockUtils');

module.exports = async function (patterns, pathToLockFile, options) {
    try {
        await execWithLockFile(() => {
            return runAll(patterns, {
                ...options,
                stdout: process.stdout
            });
        }, {
            pathToLockFile: pathToLockFile
        });
    } catch (error) {
        if (typeof error === "number") {
            process.exit(Number(error));
        } else {
            console.log(error);
            process.exit(1);
        }
    }
};