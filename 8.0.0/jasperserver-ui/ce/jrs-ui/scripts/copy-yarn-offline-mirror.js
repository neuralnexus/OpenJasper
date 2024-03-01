const fs = require('fs');
const path = require('path');
const zip = require('bestzip');
const copyfiles = require('copyfiles');

const yarnOfflineMirrorFolderName = process.env.YARN_OFFLINE_MIRROR_NAME;
const destinationParent = process.env.YARN_OFFLINE_MIRROR_DIST;
const destination = `${destinationParent}/${yarnOfflineMirrorFolderName}`;

function cleanYarnOfflineCacheFolder() {
    const result = fs.readdirSync(destination);
    result.filter((path) => {
        return !path.match(/.+-[\w]+$/g);
    }).forEach((entry) => {
        fs.unlinkSync(`${destination}/${entry}`);
    });
}

function compress() {
    return zip({
        cwd: path.resolve(destinationParent),
        source: yarnOfflineMirrorFolderName,
        destination: `${yarnOfflineMirrorFolderName}.zip`
    }).catch((err) => {
        console.error(err.stack);
        process.exit(1);
    })
}

function copyToBuildFolder() {
    const source = path.resolve(`${process.cwd()}/../${yarnOfflineMirrorFolderName}`);
    return new Promise((resolve, reject) => {
        copyfiles([`${source}/*`, `${destination}`], source.split(/[\/\\]/g).length, (err) => {
            if (err) {
                reject(err)
            } else {
                resolve();
            }
        })
    })
}

(async function () {
    await copyToBuildFolder();
    cleanYarnOfflineCacheFolder();
    await compress();
})();
