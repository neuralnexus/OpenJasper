const fs = require('fs');
const REGISTRY = (process.env.npm_config_registry || "").replace(/\/$/, "");
const DEFAULT_REGISTRY = process.env.npm_config_default_registry || "https://registry.yarnpkg.com";
const LOCK_FILE = "yarn.lock";

// Yarn does not allow to use custom registry yarn.lock exists
// because registry url is hardcoded in the yarn.lock
// So we have to manually update yarn.lock file in the preinstall script
// This will be implemented in yarn v2. see here: https://github.com/yarnpkg/yarn/issues/5892
if (REGISTRY !== DEFAULT_REGISTRY) {
    console.log(`\nUsing custom NPM registry: [${REGISTRY}]`);
    console.log(`yarn.lock will be updated to use it.`);
    console.log(`Custom npm registry will be supported in yarn v2 natively\n`);
    const lockFile = String(fs.readFileSync(LOCK_FILE, {encoding: "utf8"}));
    const newLockFile = lockFile.replace(urlToRegexp(DEFAULT_REGISTRY), REGISTRY);
    fs.writeFileSync(LOCK_FILE, newLockFile);
}

function urlToRegexp (url) {
    return new RegExp(url.replace(/\//g, "\\/").replace(/\./g, "\\."), "g");
}