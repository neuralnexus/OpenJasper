const copyfiles = require('copyfiles');
const getFafDependencies = require('js-sdk/scripts/utils/getFafDependencies');
const {getFafModuleDir} = require('js-sdk/scripts/utils/fafModuleUtils');
const {argv} = require('yargs');

const overlayBundlesDir = process.env.BUILD_BUNDLES_DIR;

function copyModuleThemesFrom({path, up}) {
    const destination = argv["_"][0] || overlayBundlesDir;
    copyfiles([`${path}/**/*.*`, `${path}/*.*`, `${destination}`], up, () => {})
}

const fafModules = getFafDependencies();

fafModules.map(function (module) {
    const path = `${getFafModuleDir(module)}/bundles`;
    return {
        path: path,
        up: path.split(/[\/\\]/g).length
    };
}).concat([{path: "bundles", up: 1}]).forEach(copyModuleThemesFrom);
