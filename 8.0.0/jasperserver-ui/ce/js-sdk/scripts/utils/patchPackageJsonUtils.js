const fs = require('fs');
const path = require('path');
const {getFafModuleDir} = require('./fafModuleUtils');
const PACKAGE_JSON = 'package.json';

function findFafModuleMatchInPackageJson(fafModule, packageJson) {
    const match = packageJson.match(new RegExp(`\"${fafModule}\"\:\\s*\"[\\d\.]+\"`, "g"));
    return match && match[0];
}

function replacePathToFafModulesInPackageJson(cwd, pathToModule, fafDepsWithPath) {
    const pathToPackageJson = path.resolve(`${pathToModule}/${PACKAGE_JSON}`);
    const packageJson = String(fs.readFileSync(pathToPackageJson, {encoding: 'utf8'}));

    const matches = fafDepsWithPath.reduce((acc, fafModuleWithPath) => {
        const fafModule = fafModuleWithPath.name;
        const fafModulePath = fafModuleWithPath.path;
        const match = findFafModuleMatchInPackageJson(fafModule, packageJson);
        if (match) {
            const relativePath = path.relative(pathToModule, fafModulePath).replace(/\\/g, "/");
            acc[fafModule] = {
                oldDependency: match,
                newDependency: `"${fafModule}": "link:${relativePath}"`
            }
        }

        return acc
    }, {});

    const newPackageJson = Object.keys(matches).reduce((acc, key) => {
        const {oldDependency, newDependency} = matches[key];
        return acc.replace(oldDependency, newDependency);
    }, packageJson);

    const updatePackageJson = new Promise((resolve) => {
        if (pathToPackageJson !== newPackageJson) {
            fs.writeFile(pathToPackageJson, newPackageJson, (err) => {
                if (err) {
                    console.error(err);
                }

                resolve();
            });
        } else {
            resolve();
        }
    });

    return updatePackageJson.then(() => {
        return {[pathToModule]: matches}
    });
}

function replacePathToFafModulesInAllPackageJsonFiles(fafDependencies, cwd) {
    const fafDepsWithPath = fafDependencies.map(module => ({
        name: module,
        path: getFafModuleDir(module, cwd)
    }));

    const replacePromises = fafDepsWithPath
        .map(f => f.path)
        .concat([cwd])
        .map(path => replacePathToFafModulesInPackageJson(cwd, path, fafDepsWithPath));

    return Promise.all(replacePromises)
        .then((results) => {
            return results.reduce((acc, result) => {
                return {
                    ...acc,
                    ...result
                };
            }, {});
        })
}

function rollbackPathToFafModulesInPackageJson(pathToModule, matches) {
    if (!matches || Object.keys(matches).length === 0) {
        return Promise.resolve();
    }

    const pathToPackageJson = path.resolve(`${pathToModule}/${PACKAGE_JSON}`);
    const packageJson = String(fs.readFileSync(pathToPackageJson, {encoding: 'utf8'}));

    const newPackageJson = Object.keys(matches).reduce((acc, key) => {
        const {oldDependency, newDependency} = matches[key];
        return acc.replace(newDependency, oldDependency);
    }, packageJson);

    return new Promise((resolve) => {
        if (packageJson !== newPackageJson) {
            fs.writeFile(pathToPackageJson, newPackageJson, (err) => {
                if (err) {
                    console.error(err);
                }

                resolve();
            });
        } else {
            resolve();
        }
    });
}

function rollbackPathToFafModulesInAllPackageJsonFiles(packageJsonMatches) {
    if (packageJsonMatches) {
        const replacePromises = Object.keys(packageJsonMatches)
            .map(path => rollbackPathToFafModulesInPackageJson(path, packageJsonMatches[path]));

        return Promise.all(replacePromises)
    }
}

// If dependency in package.json is defined in the normal way like
// "someLibrary": "1.0.0"
// yarn will try to access this library in the remote registry
// this will not work for faf modules:
// they are defined as a normal libs (like: "js-sdk": "7.8.0")
// but yarn should not try to load them from the remote registry
// so here we temporarily replace all faf deps from this format: "js-sdk": "7.8.0"
// to this format: "js-sdk": "link:../js-sdk" - yarn understands it

module.exports = {
    replacePathToFafModulesInAllPackageJsonFiles,
    rollbackPathToFafModulesInAllPackageJsonFiles
};