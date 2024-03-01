const fs = require("fs");
const {
    getAllFafModules,
    getFafModuleDir
} = require('./fafModuleUtils')

function readFafDependencies(modulePath, fafModulesNames) {
    let packageJson = JSON.parse(fs.readFileSync(`${modulePath}/package.json`, {encoding: 'utf8'}));

    return Object.keys(packageJson.dependencies).filter(dependency => {
        return fafModulesNames.includes(dependency);
    });
}

function readFafDependenciesWithTheirDependencies(cwd, fafModulesNames, memo = {}) {
    let fafDependencies = readFafDependencies(cwd, fafModulesNames);

    return fafDependencies.reduce((memo, dependency) => {
        if (!memo[dependency]) {
            const dependencyCwd = getFafModuleDir(dependency, cwd);
            const depsOfTheDeps = readFafDependencies(dependencyCwd, fafModulesNames);

            memo[dependency] = {
                name: dependency,
                deps: depsOfTheDeps
            };

            memo = readFafDependenciesWithTheirDependencies(dependencyCwd, fafModulesNames, memo);
        }

        return memo;
    }, memo);
}

function sortFafDependencies(fafDependensies) {
    let result = [],
        fafDependensiesCopy = fafDependensies.slice();

    while (fafDependensiesCopy.length > 0) {
        let lowest = fafDependensiesCopy[0];
        let index = 0;
        let i = 0;

        while (i < fafDependensiesCopy.length) {
            let dependency = fafDependensiesCopy[i];

            if (lowest.deps.includes(dependency.name)) {
                lowest = dependency;
                index = i;
                i = 0;
            } else {
                i += 1;
            }
        }

        result.push(lowest);
        fafDependensiesCopy.splice(index, 1);
    }

    return result;
}

module.exports = function () {
    const cwd = process.cwd();
    const fafModulesNames = getAllFafModules(cwd);
    const allFafDependencies = readFafDependenciesWithTheirDependencies(cwd, fafModulesNames);

    return sortFafDependencies(Object.values(allFafDependencies)).map(dependency => {
        return dependency.name;
    });
};