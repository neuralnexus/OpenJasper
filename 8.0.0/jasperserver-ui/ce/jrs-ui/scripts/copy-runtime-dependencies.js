const glob = require('glob');
const fs = require("fs");
const path = require("path");
const runtimeLibsGenerateList = require("js-sdk/scripts/utils/runtimeLibsGenerateList");
const getFafDependencies = require('js-sdk/scripts/utils/getFafDependencies');

const cwd = process.cwd();

const copyLibraryFolder = module.exports = function (library) {
    const libraryFolder = path.dirname(require.resolve(`${library.name}/package.json`, {paths: [cwd]}));

    const filesToCopy = library.patterns.reduce((acc, pattern) => {
        return acc.concat(glob.sync(pattern, {
            cwd: libraryFolder,
            realpath: false
        }));
    }, []);

    filesToCopy.forEach(p => {
        const fromPath = `${libraryFolder}/${p}`;
        const toPath = `${cwd}/${process.env.RUNTIME_DEPENDENCIES}/${library.name}/${p}`;
        fs.mkdirSync(path.dirname(toPath), {recursive: true});
        fs.copyFileSync(fromPath, toPath);
    });
};

function getRuntimeLibsFiles() {
    let packageJsonPath = `${cwd}/package.json`;
    let packageJson = JSON.parse(fs.readFileSync(packageJsonPath, {encoding: 'utf8'}));

    if (!packageJson.dependenciesFiles) {
        console.log(`[Error]: 'dependenciesFiles' property is missing in ${packageJsonPath} file`);
        process.exit(1);
    }

    return packageJson.dependenciesFiles;
}

function getPatternsToCopyThirdPartyLibs(runtimeLibsFiles, libraryName) {
    if (!runtimeLibsFiles[libraryName]) {
        console.log(`[Error]: Please specify in 'dependenciesFiles' property of [package.json]` +
            ` which files should be copied to [${process.env.RUNTIME_DEPENDENCIES}] from [${libraryName}] library.`);
        process.exit(1);
    }

    let patterns = runtimeLibsFiles[libraryName];

    if (patterns.length === 0) {
        patterns = [
            `!(node_modules)/**/*.*`,
            `*.*`
        ]
    }

    patterns.push('package.json')

    return patterns;
}

function copyThirdPartyLibs(runtimeLibs) {
    const runtimeLibsFiles = getRuntimeLibsFiles();
    const fafModules = getFafDependencies();
    const fafModulesMap = fafModules.reduce((memo, module) => {
        memo[module] = true;
        return memo;
    }, {});

    Object.keys(runtimeLibs).filter(lib => !fafModulesMap[lib]).map(libraryName => {
        let library = runtimeLibs[libraryName];

        let patterns = getPatternsToCopyThirdPartyLibs(runtimeLibsFiles, library.name);

        return {
            name: library.name,
            patterns: patterns
        };
    }).forEach(copyLibraryFolder);
}

const runtimeLibs = runtimeLibsGenerateList(cwd, false);

copyThirdPartyLibs(runtimeLibs);
