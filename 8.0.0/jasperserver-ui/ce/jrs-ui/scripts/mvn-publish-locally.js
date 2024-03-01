const {exec, execSync} = require("child_process");
const fs = require('fs');
const path = require('path');
const zip = require('bestzip');
const mkdirp = require('mkdirp');

const cwd = process.cwd();
const packageName = process.env.npm_package_name;
const mavenArtifactSourceDir = process.env.MAVEN_ARTIFACT_DIR;
const UNKNOWN_VERSION = "unknownVersion";

const overlayVersion = getOverlayVersion();
const artifactPath = `${cwd}/${mavenArtifactSourceDir}/${packageName}-${overlayVersion}.zip`;

function getOverlayVersionFromGit() {
    try {
        let gitVersion = execSync('git rev-parse --abbrev-ref HEAD');

        if (gitVersion instanceof Buffer) {
            gitVersion = gitVersion.toString();
        }

        gitVersion = gitVersion.replace(/[\r\n]+/g, "");

        const isValidBranchName = gitVersion.match(/^[\w\-.]+$/g);

        if (isValidBranchName) {
            return gitVersion + "-SNAPSHOT";
        } else {
            console.error(`\n[Error] Branch name ${gitVersion} does not meet name validation criteria (alpha-numeric, -, .)`);
            process.exit(1);
        }
    } catch (e) {
        return UNKNOWN_VERSION;
    }
}

function getOverlayVersion() {
    const version = process.env.BUILD_ARTIFACT_VERSION_NAME || getOverlayVersionFromGit();

    console.log(`\nFAF BUILD_ARTIFACT_VERSION_NAME is: ${version}`);

    return version;
}

function compress() {
    const overlay = process.env.BUILD_OVERLAY_DIR;
    const dir = path.dirname(artifactPath);

    mkdirp.sync(dir);

    return zip({
        cwd: overlay,
        source: '*',
        destination: artifactPath
    }).catch((err) => {
        console.error(err.stack);
        process.exit(1);
    })
}

function install(opts, cb) {
    const dashedArgs = [];
    let args = ["org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file"];

    const mavenLocalrepoPath = process.env.MAVEN_LOCALREPO_PATH;
    const mavenSettingsFile = process.env.MAVEN_SETTINGS_FILE;

    opts = opts ? opts : {};

    if (mavenLocalrepoPath) {
        opts["maven.repo.local"] = mavenLocalrepoPath;
        opts["localRepositoryPath"] = mavenLocalrepoPath;
    }
    if (mavenSettingsFile) {
        dashedArgs.push("--settings=" + mavenSettingsFile);
    }

    args = Object.keys(opts).reduce(function (memo, argName) {
        let argVal = opts[argName],

            newArg = "-D" + argName + "=" + argVal;

        memo.push(newArg);
        return memo;
    }, args);

    const cmd = "mvn --batch-mode " + args.join(" ") + " " + dashedArgs.join(" ");

    console.log("\nExecuting next command: " + cmd);

    return exec(cmd, cb);
}

function publishMavenArtifactLocally() {
    if (overlayVersion === UNKNOWN_VERSION) {
        [
            `The overlay version is unknown and it can't be published.`,
            `To fix this issue set BUILD_ARTIFACT_VERSION_NAME env variable`
        ].forEach(console.log);

        return;
    }

    console.log("\nArtifact path is: " + artifactPath);
    console.log("(artifact file exists)");

    if (!fs.existsSync(artifactPath)) {

        console.log("[Error]: There is no artifact file: " + artifactPath + "\n");

        process.exit(1);
    }

    const options = {
        file: artifactPath,
        groupId: "com.jaspersoft",
        artifactId: packageName,
        version: overlayVersion,
        packaging: "zip",
        generatePom: "true"
    };

    console.log("\nGoing to call mvn to put the artifact (zip file) into Maven's storage. Parameters to call are:");

    Object.entries(options).forEach(function (option) {
        const key = option[0];
        const value = option[1];
        console.log(key + "=" + value);
    });

    const installCallback = function (error, stdout, stderr) {

        let errorMessage = [],
            output = [];

        if (stdout) {
            output.push(stdout.toString());
        }
        if (stderr) {
            output.push(stderr.toString());
        }
        output = output.join("\n");

        console.log("\nThe output from MVN is: \n");
        console.log(output);
        console.log("");

        if (error) {
            errorMessage = [
                "\n[Error]: Failed to execute 'mvn'. The return code from MVN is: " + error.code
            ];
            if (error.signal) {
                errorMessage.push("The signal that has terminated the MVN is: " + error.signal);
            }
            errorMessage = errorMessage.join("\n") + "\n";
            console.log(errorMessage);
            process.exit(1);
        }

        if (output.indexOf("BUILD FAILED") !== -1) {
            console.log("[Error]: Can't install local maven artifact.\n");
            process.exit(1);
        }
    };

    install(options, installCallback);
}

compress().then(() => {
    publishMavenArtifactLocally();
});
