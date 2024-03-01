const fs = require('fs');
const path = require('path');
const runtimeLibsGenerateList = require("js-sdk/scripts/utils/runtimeLibsGenerateList");
const getFafDependencies = require('js-sdk/scripts/utils/getFafDependencies');
const mkdirp = require('mkdirp');

const RUNTIME_LIBS_REPORT_NAME = process.env.RUNTIME_LIBS_REPORT_NAME;

function convertReportToLibraryMap(libsReport) {
    return libsReport.split(/[\r\n]+/g).reduce((memo, item) => {
        if (item) {
            let nameAndVersion = item.split(/[\s]+/);

            memo[nameAndVersion[0]] = nameAndVersion[1];
        }

        return memo;
    }, {});
}

function generateDiff(libsReportMap, masterLibsReportMap) {
    const diff = {
        added: [],
        removed: [],
        changed: []
    };

    const checked = {};

    Object.keys(libsReportMap).forEach(library => {
        let version = libsReportMap[library];
        let libraryEntry = {
            name: library,
            version: version
        };

        let masterLibraryVersion = masterLibsReportMap[library];

        if (!masterLibraryVersion) {
            diff.added.push(libraryEntry);
        } else if (version !== masterLibraryVersion) {
            libraryEntry.masterVersion = masterLibraryVersion;
            diff.changed.push(libraryEntry);
        }

        checked[library] = true;
    });

    Object.keys(masterLibsReportMap).forEach(masterLibrary => {
        if (!checked[masterLibrary]) {
            diff.removed.push({
                name: masterLibrary,
                version: masterLibsReportMap[masterLibrary]
            });
        }
    });

    return diff;
}

function addedLibraryFormatter(library) {
    return `\t${library.name}: ${library.version}\n`;
}

function changedLibraryFormatter(library) {
    return `\t${library.name}: from [${library.masterVersion}] to [${library.version}]\n`;
}

function formatModifiedLibraries(title, libraries, format) {
    return libraries.reduce((memo, addedLibrary) => {
        memo += format(addedLibrary);

        return memo;
    }, `\n${title}:\n`);
}

function generateFailReport(diff) {
    let failReport = "";

    if (diff.added.length > 0) {
        failReport += formatModifiedLibraries(
            "The following libraries have been added",
            diff.added,
            addedLibraryFormatter);
    }

    if (diff.removed.length > 0) {
        failReport += formatModifiedLibraries(
            "The following libraries have been removed",
            diff.removed,
            addedLibraryFormatter);
    }

    if (diff.changed.length > 0) {
        failReport += formatModifiedLibraries(
            "The following libraries have been changed",
            diff.changed,
            changedLibraryFormatter);
    }

    return failReport;
}

function libsCheckFailed(diff) {
    const diffName = process.env.DIFF_REPORT_NAME
    const failReport = generateFailReport(diff);

    const dir = path.dirname(diffName);
    mkdirp(dir);
    fs.writeFileSync(diffName, failReport);
    console.log(failReport);

    const failMessage = "Library version check has been failed.";

    if (`${process.env.RUNTIME_LIBS_FAIL_BUILD}` === "false") {
        //Do not fail build in default CI setup or in non default DEV setup
        console.log(`[Warning]: ${failMessage}`);
    } else {
        //Fail build in non default CI setup or in default DEV setup
        console.log(`[Error]: ${failMessage}`);
        process.exit(1);
    }
}

function runtimeLibsGenerateReport() {
    const runtimeLibs = runtimeLibsGenerateList(process.cwd());
    const fafModules = getFafDependencies();
    const fafModulesMap = fafModules.reduce((memo, module) => {
        memo[module] = true;
        return memo;
    }, {});

    const list = Object.keys(runtimeLibs).filter(lib => !fafModulesMap[lib]);
    list.sort((a, b) => {return a.localeCompare(b)});

    const runtimeLibsString = list.reduce((memo, item) => {
        return memo + `${item} ${runtimeLibs[item].version}\n`
    }, "");

    const dir = path.dirname(RUNTIME_LIBS_REPORT_NAME);
    mkdirp.sync(dir);
    fs.writeFileSync(RUNTIME_LIBS_REPORT_NAME, runtimeLibsString);

    console.log(`library versions report has been generated to: ${RUNTIME_LIBS_REPORT_NAME}`);
}

function runtimeLibsCheck() {
    const libsReport = fs.readFileSync(RUNTIME_LIBS_REPORT_NAME, {encoding: 'utf8'});
    const masterLibsReport = fs.readFileSync(process.env.MASTER_REPORT_NAME, {encoding: 'utf8'});

    const libsReportMap = convertReportToLibraryMap(libsReport);
    const masterLibsReportMap = convertReportToLibraryMap(masterLibsReport);

    const diff = generateDiff(libsReportMap, masterLibsReportMap);
    const {added, removed, changed} = diff;

    if (added.length > 0 || removed.length > 0 || changed.length > 0) {
        libsCheckFailed(diff);
    } else {
        console.log("Library version check has been passed successfully");
    }
}

runtimeLibsGenerateReport();
runtimeLibsCheck();
