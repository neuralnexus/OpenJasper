const path = require('path');
const runAllWithLock = require('./utils/runAllWithLock');
const {argv} = require('yargs');

// This script allows to run any npm script with lock file.
// If there is any other process which executes this script with the same file -
// They will be executed serially
// Example: add this to package.json to execute tests with lock file:
//
// "test:lock": "dotenv -c -- node node_modules/js-sdk/scripts/run-lock --lock=test -- test",
//
// here npm script `test` will be executed with js-sdk/test.lock file
// so if you will add this `test:lock` script to all faf modules and run it - all tests will be executed serially

const lockFile = argv['lock'];

if (!lockFile) {
    console.log('Please pass "lock" parameter');
    process.exit(1);
}

const pathToLockFile = path.resolve(__dirname, '..', `${lockFile}.lock`);

runAllWithLock(argv['_'], pathToLockFile, {parallel: false})
