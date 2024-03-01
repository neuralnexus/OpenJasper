const spawn = require("child_process").spawn;
const exec = require('./yarnUtils');
const dotenv = require('dotenv');
const dotenvExpand = require('dotenv-expand');
const path = require('path');

// We have to hardcode dotenv here
// because we can not use dotenv-cli with any upgrade yarn command
// because it is trying to re-create node_modules/.bin filder
// which will be blocked by dotenv-cli in case if we will use it
const paths = ['.env', '.env.local'];
paths.forEach((pathitem) => {
    dotenvExpand(dotenv.config({ path: path.resolve(process.cwd(), pathitem) }))
});

const args = process.argv.slice(2);
const yarnCmd = /^win/.test(process.platform) ? "yarn.cmd" : "yarn";

function runYarn() {
    return new Promise((resolve, reject) => {
        const s = spawn(yarnCmd, args, {
            stdio: "inherit"
        });

        s.on('close', (code) => {
            if (code !== 0) {
                reject(code);
            }

            resolve();
        })
    })
}

(async function () {
    try {
        await exec(runYarn);
    } catch (error) {
        if (typeof error === "number") {
            process.exit(Number(error));
        } else {
            console.log(error);
            process.exit(1);
        }
    }
})()
