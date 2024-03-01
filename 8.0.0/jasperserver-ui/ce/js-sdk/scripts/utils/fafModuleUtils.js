const fs = require('fs');
const path = require('path');

const getAllFafModules = function (cwd = process.cwd()) {
    const packageJsonText = String(fs.readFileSync(`${cwd}/../package.json`, {encoding: 'utf8'}));
    const packageJson = JSON.parse(packageJsonText);
    return packageJson['faf-modules'];
}

const getFafModuleDir = function (fafModule, cwd = process.cwd()) {
    try {
        return path.dirname(require.resolve(`${fafModule}/package.json`, {paths: [cwd]}));
    } catch (e) {
        const message = `Module [${fafModule}] could not be resolved in the following path [${cwd}]`;
        throw new Error(message);
    }
}

module.exports = {
    getAllFafModules,
    getFafModuleDir
}