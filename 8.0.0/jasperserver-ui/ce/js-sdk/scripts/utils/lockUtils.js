const fs = require('fs');

function createLockFile(path, lockWaitInterval, lockWaitTimeout) {
    return new Promise((resolve, reject) => {
        try {
            fs.closeSync(fs.openSync(path, 'wx'));
            resolve();
        } catch (e) {
            const time = Date.now();
            const intervalId = setInterval(() => {
                try {
                    fs.closeSync(fs.openSync(path, 'wx'));
                    clearInterval(intervalId);
                    resolve();
                } catch (e) {
                    if (Date.now() - time > lockWaitTimeout) {
                        clearInterval(intervalId);
                        reject(`Lock file check timeout. Remove ${path} and try again`);
                    }

                    // At this moment lock file still exists. Waiting until it will be removed
                }
            }, lockWaitInterval);
        }
    })
}

function removeLockFile(path) {
    return new Promise((resolve) => {
        fs.unlink(path, (err) => {
            if (err) {
                console.error(err);
            }

            console.log(`Released lock file: [${path}]`)
            resolve();
        })
    })
}

async function execWithLockFile(callback, options = {}) {
    const {
        pathToLockFile,
        lockWaitInterval = ((process.env.LOCK_WAIT_INTERVAL ? Number(process.env.LOCK_WAIT_INTERVAL) : 1) * 1000),
        lockWaitTimeout = ((process.env.LOCK_WAIT_TIMEOUT ? Number(process.env.LOCK_WAIT_TIMEOUT) : 60) * 1000)
    } = options;

    if (!pathToLockFile) {
        throw new Error('Please provide path to lock file');
    }

    await createLockFile(pathToLockFile, lockWaitInterval, lockWaitTimeout);

    console.log(`Acquired lock file: [${pathToLockFile}]`)

    try {
        await Promise.resolve(callback());
    } catch (e) {
        throw e;
    } finally {
        await removeLockFile(pathToLockFile);
    }
}

module.exports = execWithLockFile;