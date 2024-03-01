const {argv} = require('yargs');
const fs = require('fs');
const path = require('path');
const getFafDependencies = require('./getFafDependencies');
const {getFafModuleDir} = require('./fafModuleUtils')

const PACKAGE_JSON = 'package.json';

const log = (msg) => {
    argv.verbose && console.log(msg);
}

const readPackageJson = (path) => {
    return JSON.parse(fs.readFileSync(path, {encoding: 'utf8'}));
}

function readCurrentDependencyVersion(pathToModule, dependency) {
    const pathToDependency = getFafModuleDir(dependency, pathToModule);
    const pathToPackageJson = `${pathToDependency}/${PACKAGE_JSON}`;
    const packageJson = readPackageJson(pathToPackageJson);

    return packageJson.version;
}

function logRuntimeLibraries(runtimeLibs) {
    log("\nList of runtime libraries:");

    const list = Object.keys(runtimeLibs);
    list.sort((a, b) => {
        return a.localeCompare(b)
    });

    list.forEach(lib => {
        log(`${lib}: ${runtimeLibs[lib].version}`);
    });
}

function readLibraryVersionFromPackageJson(options) {
    let {
        module,
        pathToModule,
        pathToPackageJson,
        runtimeLibs,
        checkForImplicitDependencies,
        checkVersion
    } = options;

    const packageJson = readPackageJson(pathToPackageJson);
    const dependencies = packageJson.dependencies;

    if (checkForImplicitDependencies) {
        Object.keys(runtimeLibs).reduce((memo, libraryName) => {
            if (!dependencies[libraryName]) {
                console.log(`[Error]: Module [${module}] has no dependency [${libraryName}] declared ` +
                    `in it's package.json. Implicit dependencies are forbidden`);
                process.exit(1);
            }
        }, {});
    }

    runtimeLibs = Object.keys(dependencies).reduce((memo, dependency) => {
        let existingDependency = memo[dependency];
        let currentDependencyVersion = readCurrentDependencyVersion(pathToModule, dependency);
        let currentDependencyPath = dependencies[dependency];

        if (existingDependency && existingDependency.path !== currentDependencyPath && checkVersion) {
            console.log(`[Error]: The module [${module}] has dependency [${dependency}] with version ` +
                `[${currentDependencyPath}] but version [${existingDependency.path}] already declared by other modules.`)
            process.exit(1);
        } else if (existingDependency && existingDependency.version !== currentDependencyVersion && checkVersion) {
            console.log(`[Error]: The module [${module}] has dependency [${dependency}] with the following version ` +
                `in dependencies package.json: [${currentDependencyVersion}] but version [${existingDependency.version}] already declared by other modules.`)
            process.exit(1);
        } else if (!existingDependency) {
            runtimeLibs[dependency] = {
                name: dependency,
                version: currentDependencyVersion,
                path: currentDependencyPath
            };
        }

        return memo;
    }, runtimeLibs);

    return runtimeLibs;
}

module.exports = (cwd, checkVersion = true) => {
    const fafModules = getFafDependencies(cwd);

    let runtimeLibs = {};

    fafModules.forEach((module) => {
        let pathToModule = getFafModuleDir(module, cwd);
        let pathToPackageJson = `${pathToModule}/${PACKAGE_JSON}`;

        runtimeLibs = readLibraryVersionFromPackageJson({
            checkForImplicitDependencies: false,
            checkVersion,
            module: module,
            pathToModule: pathToModule,
            pathToPackageJson,
            runtimeLibs
        });
    });

    runtimeLibs = readLibraryVersionFromPackageJson({
        checkForImplicitDependencies: true,
        checkVersion,
        module: path.basename(cwd),
        cwd,
        pathToPackageJson: `${cwd}/${PACKAGE_JSON}`,
        runtimeLibs
    });

    logRuntimeLibraries(runtimeLibs);

    return runtimeLibs;
};
