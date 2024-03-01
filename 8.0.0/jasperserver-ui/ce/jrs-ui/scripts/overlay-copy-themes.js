const copyfiles = require('copyfiles');
const getFafDependencies = require('js-sdk/scripts/utils/getFafDependencies');
const {getFafModuleDir} = require('js-sdk/scripts/utils/fafModuleUtils');
const {argv} = require('yargs');

const overlayThemesDir = process.env.BUILD_THEMES_DIR;
const destination = argv["_"][0] || overlayThemesDir;

function copyModuleThemesFrom(destination, {path, up}) {
    return new Promise((resolve, reject) => {
        copyfiles([`${path}/*.*`, `${path}/**/*.*`, `${destination}`], {up}, (err) => {
            if (err) {
                reject(err)
            } else {
                resolve();
            }
        })
    })
}

function copyThemes(destination) {
    const fafModules = getFafDependencies();

    const promises = fafModules
        .map(function (module) {
            const path = `${getFafModuleDir(module)}/themes`;
            return {
                path: path,
                up: path.split(/[\/\\]/g).length
            };
        })
        .concat([{path: "themes", up: 1}])
        .map(copyModuleThemesFrom.bind(null, destination));

    return Promise.all(promises);
}

(async () => {
    await copyThemes(destination);
})();