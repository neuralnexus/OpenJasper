const {argv} = require('yargs');

module.exports = function () {
    return !argv.reporters || String(argv.reporters).indexOf('coverage-istanbul') >= 0;
}