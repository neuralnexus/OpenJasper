const path = require('path');
const getFafDependencies = require('./getFafDependencies');
const execWithLockFile = require('./lockUtils');
const {
    replacePathToFafModulesInAllPackageJsonFiles,
    rollbackPathToFafModulesInAllPackageJsonFiles
} = require('./patchPackageJsonUtils');

const PATH_TO_LOCK_FILE = path.resolve(__dirname, '../../yarnUtil.lock');

async function execWithPatchedPackageJson(callback) {
    const cwd = process.cwd();
    await execWithLockFile(async () => {
        const fafDependencies = getFafDependencies();
        const packageJsonMatches = await replacePathToFafModulesInAllPackageJsonFiles(fafDependencies, cwd);

        try {
            await Promise.resolve(callback());
        } catch (e) {
            throw e;
        } finally {
            await rollbackPathToFafModulesInAllPackageJsonFiles(packageJsonMatches);
        }
    }, {pathToLockFile: PATH_TO_LOCK_FILE})
}

module.exports = execWithPatchedPackageJson;