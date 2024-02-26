"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.LinuxPlatform = void 0;
const fs = require("fs");
const path = require("path");
const glob = require("glob");
const base_1 = require("./base");
const run_program_1 = require("../run_program");
const linux_disk_1 = require("./linux_disk");
const linux_user_group_1 = require("./linux_user_group");
const utils_1 = require("../utils");
;
/**
 * Class for all Linux based operating systems
 */
class LinuxPlatform extends base_1.BasePlatform {
    constructor(options) {
        super(options);
        this.systemPackages = [];
        this.systemPackages = [
            "curl", "wget", "zip", "unzip",
            "screen", "haveged",
            "xfsprogs",
            "logrotate" // logrotate recipe
        ];
    }
    /**
     * Information from login.defs
     */
    get loginPackageConfiguration() {
        if (!this._loginPackageConfiguration) {
            const fileContents = {
                UID_MIN: 1000,
                UID_MAX: 60000,
                SYS_UID_MIN: 101,
                GID_MIN: 1000,
                GID_MAX: 60000,
                SYS_GID_MIN: 101,
            };
            if (fs.existsSync("/etc/login.defs")) {
                let lines = fs.readFileSync("/etc/login.defs").toString().split("\n");
                const exp = new RegExp("^\\s*(.*?)\\s+(.*?)\\s*$");
                lines = lines.filter(l => {
                    return l.match(exp) && !l.match(/^\s*#/);
                });
                for (const match of lines.map(l => l.match(exp))) {
                    fileContents[match[1]] = match[2];
                }
            }
            this.logger.trace("loginPackageConfiguration: file is", fileContents);
            if (!fileContents.SYS_UID_MAX) {
                fileContents.SYS_UID_MAX = parseInt(fileContents.UID_MIN) - 1;
            }
            if (!fileContents.SYS_GID_MAX) {
                fileContents.SYS_GID_MAX = parseInt(fileContents.GID_MIN) - 1;
            }
            this._loginPackageConfiguration = {
                uidMin: parseInt(fileContents.UID_MIN),
                uidMax: parseInt(fileContents.UID_MAX),
                sysUidMin: parseInt(fileContents.SYS_UID_MIN),
                sysUidMax: parseInt(fileContents.SYS_UID_MAX),
                gidMin: parseInt(fileContents.GID_MIN),
                gidMax: parseInt(fileContents.GID_MAX),
                sysGidMin: parseInt(fileContents.SYS_GID_MIN),
                sysGidMax: parseInt(fileContents.SYS_GID_MAX),
                useUserGroups: ((fileContents.USERGROUPS_ENAB || "").toString().toUpperCase() ===
                    "YES")
            };
        }
        return this._loginPackageConfiguration;
    }
    _initPathInfo() {
        const namiRuntimeDirectory = "/opt/bitnami/nami";
        this._pathInfo = {
            pathSeparator: ":",
            namiRuntimeDirectory: namiRuntimeDirectory,
            namiRuntimeBinDirectory: `${namiRuntimeDirectory}/bin`,
            provisionerCommand: `${namiRuntimeDirectory}/bin/provisioner`,
            namiAppPath: "/opt/bitnami",
            namiDataPath: "/bitnami"
        };
        this.platformTags.push("linux");
        // tags for convenience
        if (this.bits.toString() === "64") {
            this.platformTags.push("linux64", "linux-x64", "linux-amd64");
        }
    }
    /**
     * Platform OS and architecture i.e. `linux-x86_64` for 64 bit Linux platform
     */
    get osArch() {
        return (this.bits.toString() === "64") ? "linux-x86_64" : "linux-x86";
    }
    /**
     * Get unique identifier, that will remain same until next machine reboot
     */
    getUniqueBootId() {
        if (!this._uniqueBootId) {
            try {
                this._uniqueBootId = fs.statSync("/proc").ctime.toISOString();
            }
            catch (e) {
                this._uniqueBootId = fs.statSync("/").ctime.toISOString();
                this.logger.debug("Unable to get boot time from /proc; falling back to root fs", e);
            }
        }
        return this._uniqueBootId;
    }
    /**
     * Retrieve information about memory
     */
    getMemoryInfo() {
        let contents = {};
        for (let line of fs.readFileSync("/proc/meminfo").toString().split("\n")) {
            let match = line.match(/^\s*(.*):\s+(.*)$/);
            if (match) {
                contents[match[1]] = match[2];
            }
        }
        let totalPhysicalKB = parseInt(contents["MemTotal"].replace(/ kB/, ""));
        let totalSwapKB = parseInt(contents["SwapTotal"].replace(/ kB/, ""));
        return {
            totalPhysicalMB: Math.round(totalPhysicalKB / 1024),
            totalSwapMB: Math.round(totalSwapKB / 1024),
        };
    }
    /**
     *  Get the disk free for a path, in MB.
     */
    getFreeDiskSpace(path) {
        let rootPath = "/" + `${path}`.split("/")[1];
        let freeMB = run_program_1.runProgram("df", ["-BM", "--output=avail", rootPath]).split("\n")[1].slice(0, -1);
        return Number(freeMB);
    }
    /**
     * Create a swap file in the swapfile file, of size mb
     * @param swapfile
     * @param mb
     */
    createSwap(swapfile, mb) {
        // prepare swap file
        this.logger.info("Creating swapfile: ", swapfile, " size: ", mb, " MB");
        if (this.getFreeDiskSpace(swapfile) > mb) {
            if (!fs.existsSync(`${swapfile}.complete`) && !fs.existsSync(`${swapfile}.lock`)) {
                fs.writeFileSync(`${swapfile}.lock`, "");
                run_program_1.runProgram("dd", [
                    "if=/dev/zero", `of=${swapfile}`, "bs=1048576", `count=${mb}`
                ]);
                run_program_1.runProgram("mkswap", [swapfile]);
                fs.chmodSync(swapfile, "600");
                fs.writeFileSync(`${swapfile}.complete`, "");
                fs.rmSync(`${swapfile}.lock`);
                this.logger.info("Swapfile created");
            }
            else {
                this.logger.info("Swapfile already exits");
            }
        }
        else {
            this.logger.info("Not enough free space to create swapfile");
        }
    }
    /**
     * Activates the swap on the given swapfile
     * This call is idempotent, it succees if called again after the swap is
     * already setup.
     * It will only throw and exception when the swap could not be activated.
     * @param swapfile
     */
    activateSwap(swapfile) {
        try {
            run_program_1.runProgram("swapon", [swapfile]);
        }
        catch (e) {
            if (!this._swapActive(swapfile)) {
                throw (e);
            }
        }
    }
    _swapActive(swapfile) {
        let swapStats = this._parseSwapOnStat(run_program_1.runProgram("swapon", ["-s"]));
        for (let swapStat of swapStats) {
            if (swapStat.filename === swapfile &&
                swapStat.type === "file" &&
                swapStat.sizeKB > 0) {
                return true;
            }
        }
        return false;
    }
    _parseSwapOnStat(swaponStat) {
        let lines = swaponStat.split("\n");
        const splitterRegex = /[ \t]+/;
        let titles = lines[0].split(splitterRegex);
        this._assertSwapStatTitles(titles);
        let dataLines = lines.slice(1);
        let swapStats = [];
        for (let line of dataLines) {
            let columns = line.split(splitterRegex);
            if (columns.length === 0 || (columns.length === 1 && columns[0] === "")) {
                continue; // skip empty lines, probably last line
            }
            if (columns.length < 3) {
                throw new Error(`Too few swap stat data columns(${columns.length}): '${columns}'`);
            }
            let swapEntry = {
                filename: columns[0],
                type: columns[1].toLowerCase(),
                sizeKB: +columns[2]
            };
            swapStats.push(swapEntry);
        }
        return swapStats;
    }
    _assertSwapStatTitles(titles) {
        if (titles.length < 3) {
            throw new Error(`Too few swap stat titles (${titles.length}): '${titles}'`);
        }
        if (titles[0].toLowerCase() !== "filename" ||
            titles[1].toLowerCase() !== "type" ||
            titles[2].toLowerCase() !== "size") {
            throw new Error(`Expected "filename type size ..." but got ${titles}`);
        }
    }
    _getNetworkInterfaceNamesWithIfconfig() {
        let lines = run_program_1.runProgram("ifconfig", ["-a"]).split("\n");
        const exp = new RegExp("^\\s*([^\\s]*)\\s+Link");
        lines = lines.filter((line) => { return !!(line.match(exp)); });
        lines = lines.map((line) => { return line.match(exp)[1]; });
        return lines;
    }
    _getNetworkInterfaceNamesWithProc() {
        let lines = fs.readFileSync("/proc/net/dev").toString().split("\n");
        const exp = new RegExp("^\\s*([^\\s]*):");
        lines = lines.filter((line) => { return !!(line.match(exp)); });
        lines = lines.map((line) => { return line.match(exp)[1]; });
        return lines;
    }
    _getNetworkInterfaceNames() {
        let result;
        try {
            result = this._getNetworkInterfaceNamesWithProc();
        }
        catch (e) {
            this.logger.warn("Unable to read /proc/net/dev", e);
            result = this._getNetworkInterfaceNamesWithIfconfig();
        }
        return result;
    }
    _modifyPortMapping(op, srcPort, destPort) {
        for (const device of this._getNetworkInterfaceNames()) {
            run_program_1.runProgram("iptables", [
                op, "PREROUTING", "-t", "nat", "-i", device, "-p", "tcp",
                "--dport", destPort, "-j", "REDIRECT", "--to-port", srcPort
            ]);
        }
    }
    _deletePortMapping(srcPort, destPort) {
        super._deletePortMapping(srcPort, destPort);
        this._modifyPortMapping("-D", srcPort, destPort);
    }
    _addPortMapping(srcPort, destPort) {
        super._addPortMapping(srcPort, destPort);
        this._modifyPortMapping("-A", srcPort, destPort);
    }
    /**
     * Commands that should be invoked when starting nami to increase common OS limits
     * for child processes - i.e. for Linux this will be passed to bash script
     */
    get increaseLimitsCommands() {
        return [
            // set limit of processes/threads
            "ulimit -u 64000",
            // set limit of open files
            "ulimit -n 100000",
            // set limit of virtual memory
            "ulimit -v unlimited",
            // set limit of file size
            "ulimit -f unlimited",
            // set limit of cpu time
            "ulimit -t unlimited",
            // set limit of memory size
            "ulimit -m unlimited",
            // set limit of locked-in-memory size
            "ulimit -l unlimited"
        ];
    }
    /**
     * Create a system service using init.d or systemd
     * The service will be registered, but not started immediately
     *
     * @param name Name of the service to use
     * @param options Options to use for registering the service
     */
    createSystemService(name, options) {
        const startLevel = options.startLevel || 80;
        const stopLevel = options.stopLevel || (100 - startLevel);
        const startCommand = options.startCommand;
        const stopCommand = options.stopCommand;
        const restartCommand = (options.restartCommand ?
            options.restartCommand :
            `${stopCommand} || exit 1\n    sleep 5\n    ${startCommand}`);
        const ulimitCommands = this.increaseLimitsCommands.length > 0 ?
            this.increaseLimitsCommands.join(" ; ") : "";
        let initScriptMain = `
case "$1" in
  start)
    ${options.increaseLimits ? ulimitCommands : ""}
    ${startCommand}
    exit $?
    ;;
  stop)
    ${stopCommand}
    exit $?
    ;;
  restart|force-reload|reload)
    ${options.increaseLimits ? ulimitCommands : ""}
    ${restartCommand}
    exit $?
    ;;
esac
`;
        this.getInitSystem().installService(name, options.description, initScriptMain, options);
        this.getInitSystem().enableService(name, startLevel, stopLevel);
    }
    /**
     * Get an instance of Disk class for specified device for managing partitions
     * @param name platform-specific device - i.e. `/dev/sda`
     */
    getDiskDevice(device) {
        return new linux_disk_1.LinuxDiskDevice(this, device);
    }
    /**
     * Get name of partition based on name of device and partition number - i.e.
     * `/dev/sda1`
     * @param deviceName name of device - i.e. `/dev/sda`
     * @param partition number of partition - i.e. `1`
     */
    getDiskPartitionName(deviceName, partition) {
        if (deviceName.match(/[0-9]$/)) {
            return `${deviceName}p${partition}`;
        }
        else {
            return `${deviceName}${partition}`;
        }
    }
    /**
     * List names of disk devices (not partitions) available on the system ; such
     * as `["/dev/sda", "/dev/sdb"]`
     */
    getDiskDeviceNames() {
        const suffixTypes = [
            "abcdefgh".split(""),
            "0123456789".split("")
        ];
        let result = [];
        for (const prefix of ["/dev/xvd", "/dev/sd", "/dev/hd", "/dev/nbd", "/dev/vd"]) {
            for (const suffixes of suffixTypes) {
                // check if first disk exists
                if (fs.existsSync(`${prefix}${suffixes[0]}`)) {
                    for (const suffix of suffixes) {
                        const device = `${prefix}${suffix}`;
                        if (fs.existsSync(device)) {
                            result.push(device);
                        }
                    }
                    return result;
                }
            }
        }
        return result;
    }
    /**
     * Returns the disk partition that mounts the root directory "/"
     * example: "/dev/sda1"
     */
    getRootDiskPartition() {
        let result = undefined;
        try {
            const mounts = fs.readFileSync("/proc/mounts").toString();
            result = mounts.match(new RegExp("^(/dev/\\w*) / ", "m"))[1];
        }
        catch (e) {
            this.logger.warn("Unable to obtain root disk partition", e);
        }
        return result;
    }
    /**
     * Returns the disk device with the partition that mounts the root directory "/"
     * example: "/dev/sda"
     */
    getRootDiskDevice() {
        let result = undefined;
        try {
            const rootDiskPartition = this.getRootDiskPartition();
            if (rootDiskPartition) {
                for (const diskDevice of this.getDiskDeviceNames()) {
                    if (rootDiskPartition.includes(diskDevice)) {
                        result = diskDevice;
                        break;
                    }
                }
            }
        }
        catch (e) {
            this.logger.warn("Unable to obtain root disk device", e);
        }
        return result;
    }
    _renameUserInSudoersFile(map) {
        const sudoersFiles = glob.sync("{/etc/sudoers,/etc/sudoers.d/cloud-init,/etc/sudoers.d/90-cloud*}");
        for (const sudoersFile of sudoersFiles) {
            let contents = fs.readFileSync(sudoersFile).toString().split("\n");
            const exp = new RegExp("^(\\s*)([^\\s]+)(\\s.*)$");
            contents = contents.map(line => {
                let match = line.match(exp);
                if (match) {
                    if (map[match[2]]) {
                        line = match[1] + map[match[2]] + match[3];
                    }
                }
                return line;
            });
            fs.writeFileSync(sudoersFile, contents.join("\n"));
        }
    }
    /*
     * Rename system user, without creating new user in the system
     */
    _renameSystemUser(username, user, options) {
        let args = [];
        if (options.newHome !== user.home) {
            args.push("-d", options.newHome);
        }
        args.push("-l", username);
        args.push(user.username);
        run_program_1.runProgram("usermod", args);
    }
    /*
     * Create new user with same uid/gid and same password, moving new user
     * entry above existing user in passwd file
     */
    _addDuplicatedSystemUser(username, user, options, passwdFileHandler) {
        const shadowFileHandler = linux_user_group_1.getShadowRecords();
        const previousUsername = user.username;
        let dupUser;
        let args = [
            "-d", options.newHome, "-M",
            "-u", options.uid,
            "-g", options.gid,
            "-s", user.shell,
            "-o"
        ];
        if (shadowFileHandler) {
            const shadowRecord = shadowFileHandler.findByName(user.username);
            if (shadowRecord) {
                args.push("-p", shadowRecord.encryptedPassword);
            }
        }
        args.push(username);
        // TODO: make sure secondary groups are also copied
        run_program_1.runProgram("useradd", args);
        if (options.newHome !== user.home) {
            try {
                run_program_1.runProgram("usermod", ["-d", options.newHome, previousUsername]);
            }
            catch (e) {
                this.logger.verbose("Unable to modify user home directory with usermod;", "falling back to modifying /etc/passwd manually", e);
                user = passwdFileHandler.forget().findByName(previousUsername);
                user.home = options.newHome;
                passwdFileHandler.write();
            }
        }
        user = passwdFileHandler.forget().findByName(user.username);
        dupUser = passwdFileHandler.findByName(username);
        // reorder passwd file
        passwdFileHandler.remove(dupUser);
        passwdFileHandler.replace(user, dupUser, user);
        passwdFileHandler.write();
    }
    renameSystemUserAndGroup(username, options) {
        const passwdFileHandler = linux_user_group_1.getPasswdRecords();
        const groupFileHandler = linux_user_group_1.getGroupRecords();
        let previousUsername;
        let result = false;
        let user, group;
        let groupname;
        options = Object.assign({
            uid: this.loginPackageConfiguration.uidMin,
            gid: this.loginPackageConfiguration.gidMin,
            groupname: username,
            keepOriginalUser: true
        }, options || {});
        groupname = options.groupname;
        user = passwdFileHandler.findById(options.uid);
        if (user) {
            previousUsername = user.username;
            group = groupFileHandler.findById(options.gid);
            this.logger.trace("renameSystemUserAndGroup: Retrieved records", {
                user: user, group: group, options: options
            });
            // TODO: improve
            if (previousUsername === username) {
                this.logger.info(`Not renaming user ${username} - already configured as ${options.uid}`);
                return false;
            }
            if (!options.newHome) {
                let homelist = user.home.split("/");
                homelist[0] = "/";
                homelist.pop();
                homelist.push(username);
                options.newHome = path.join.apply(null, homelist);
            }
            if (user.home !== options.newHome) {
                fs.renameSync(user.home, options.newHome);
            }
            if (options.keepOriginalUser) {
                this._addDuplicatedSystemUser(username, user, options, passwdFileHandler);
            }
            else {
                this._renameSystemUser(username, user, options);
            }
            if (group.groupname === user.username) {
                run_program_1.runProgram("groupmod", ["-n", options.groupname, group.groupname]);
            }
            let sudoersMap = {};
            sudoersMap[previousUsername] = username;
            sudoersMap[`%${previousUsername}`] = options.groupname;
            this._renameUserInSudoersFile(sudoersMap);
            // update cloud-init files
            if (fs.existsSync("/etc/cloud/cloud.cfg")) {
                run_program_1.runProgram("sed", ["-i",
                    "-e", `s/user:\\s*${previousUsername}/user: ${username}/`,
                    "-e", `s/name:\\s*${previousUsername}/name: ${username}/`,
                    "-e", `s/name:\\s*(ubuntu|debian)/name: ${username}/`,
                    "/etc/cloud/cloud.cfg"]);
            }
            result = true;
        }
        else {
            this.logger.warn("Unable to rename system user - no system user found");
        }
        return result;
    }
    /**
     * Modify system path by editing system configuration files ; only adding paths is supported
     * @param options path elements to add and/or remove
     */
    modifySystemPath(options) {
        if (options.remove && (options.remove.length > 0)) {
            throw new Error("Removal of items from system path not implemented");
        }
        const additionalPathCode = "\n" +
            `PATH=${this.modifyPathString("", options)}:\$PATH\n` +
            "export PATH\n";
        let systemDirectories = [];
        for (let file of glob.sync("{/root/{.bashrc,.profile},/home/*/{.bashrc,.profile},/etc/skel/{.bashrc,.profile}}")) {
            if (systemDirectories.indexOf(path.dirname(file)) < 0) {
                systemDirectories.push(path.dirname(file));
            }
        }
        ;
        for (let directory of systemDirectories) {
            for (let filename of [".bashrc", ".profile"]) {
                let file = `${directory}/${filename}`;
                let lstat = fs.lstatSync(directory);
                if (!fs.existsSync(file)) {
                    fs.writeFileSync(file, "");
                    fs.chmodSync(file, "0700");
                    fs.chownSync(file, lstat.uid, lstat.gid);
                }
                this.createFileSection(file, {
                    title: "PROVISIONER ADDED ENVIRONMENT SETTINGS",
                    contents: additionalPathCode,
                    addAtBeginning: true
                });
            }
        }
    }
    /**
     * Read secure_path from /etc/sudoers file so provisioner related changes can be applied
     */
    _readPathsFromSudoers(filename) {
        // default paths if not found in /etc/sudoers
        const defaultPaths = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin";
        let value = defaultPaths;
        const lines = fs.readFileSync(filename).toString().split("\n");
        let pattern = new RegExp("^(\\s*Defaults\\s*secure_path=\")(.*)(\"\\s*)");
        for (let line of lines) {
            let result = line.match(pattern);
            if (result) {
                value = result[2];
            }
        }
        return value;
    }
    /**
    * Modify path sudo command provides by editing sudoers file
    * @param options path elements to add and/or remove
     */
    modifySudoersPath(options) {
        let paths = this._readPathsFromSudoers("/etc/sudoers");
        paths = this.modifyPathString(paths, options);
        fs.writeFileSync("/etc/sudoers.d/99-additional-paths", `Defaults secure_path="${paths}"\n`);
    }
    /**
     * Set contents provisioner specific section of hosts file
     * @param contents Map of IP address to hostname or list of host names
     */
    setHostsFileItems(contents) {
        let sectionInfo = this._fileSectionForHostsFile(contents);
        this.createFileSection("/etc/hosts", sectionInfo);
    }
    _fileSectionForHostsFile(contents) {
        let hostsText = "";
        for (let address of Object.keys(contents || {})) {
            let line = `${address} `;
            if (contents[address] instanceof Array) {
                line += contents[address].join(" ");
            }
            else {
                line += contents[address];
            }
            hostsText += `${line}\n`;
        }
        return {
            title: "PROVISIONER MANAGED HOST RECORDS",
            contents: hostsText
        };
    }
    async _getDefaultRouteIPAddress() {
        let result;
        let ipAddress;
        try {
            ipAddress = await utils_1.resolveHostname("myip.bitnami.com", {
                // timeout of 15 seconds to avoid delays when DNS resolving is not working properly
                timeout: 15000
            });
        }
        catch (e) {
            this.logger.warn("Unable to resolve myip.bitnami.com IP to address", e);
        }
        // default routable IP address that should not be used and routed explicitly
        ipAddress = ipAddress || "203.0.133.0";
        try {
            result = run_program_1.runProgram("ip", ["route", "get", ipAddress]);
        }
        catch (e) {
            this.logger.trace("Unable to run ip route get", e);
        }
        if (result) {
            let match = result.match(/src\s+([0-9\.]+)(\s|$)/);
            if (match) {
                result = match[1];
            }
            else {
                result = undefined;
            }
        }
        return result;
    }
    /**
     * Get all current IP addresses
     */
    async getPrivateIPAddresses() {
        let ip;
        let output;
        let result = [];
        let localAddresses = [];
        // try to get IP address that is used for routing internet traffic first
        ip = await this._getDefaultRouteIPAddress();
        if (ip) {
            result.push(ip);
        }
        // iterave over all interfaces
        try {
            output = run_program_1.runProgram("ip", ["addr"]);
        }
        catch (e) {
            this.logger.trace("Unable to run ip addr", e);
        }
        if (output) {
            for (let line of output.split("\n")) {
                let match = line.match(/\sinet\s+([0-9\.]+)(\/|\s)/);
                if (match) {
                    ip = match[1];
                    // IP addresses matching docker or localhost should be added at the end of the list
                    if (ip.match(/^(127\.|172\.17\.)/)) {
                        if ((result.indexOf(ip) < 0) && (localAddresses.indexOf(ip) < 0)) {
                            localAddresses.push(ip);
                        }
                    }
                    else {
                        if (result.indexOf(ip) < 0) {
                            result.push(ip);
                        }
                    }
                }
            }
            result = result.concat(localAddresses);
        }
        return result;
    }
    /**
     * Shutdown or reboot the machine
     */
    shutdown(options) {
        let command;
        options = Object.assign({
            delay: 0,
            type: "poweroff"
        }, options);
        if (options.type === "reboot") {
            command = "reboot";
        }
        else {
            command = "poweroff";
        }
        // perform in the background, using both runInBackground and async shell script
        run_program_1.runProgram("sh", ["-c",
            `(sleep ${options.delay} ; ${command}) >/dev/null 2>/dev/null &`
        ], { runInBackground: true });
    }
    /**
     * Start a system service
     * @param name Name of the service
     */
    async startService(name) {
        this.getInitSystem().startService(name);
    }
    /**
     * Stop a system service
     * @param name Name of the service
     */
    async stopService(name) {
        this.getInitSystem().stopService(name);
    }
    /**
     * Restart a system service
     * @param name Name of the service
     */
    async restartService(name) {
        this.getInitSystem().restartService(name);
    }
    /**
     * Determine additional directories to add to PATH
     */
    getAdditionalSystemPaths() {
        const globPatterns = [
            // find all bin and sbin directories in packages
            `${this.pathInfo.namiAppPath}/{*,*/*,*/*/*}/{bin,sbin}/*`
        ];
        const excludedPaths = [
            // skip adding gonit to path
            `${this.pathInfo.namiAppPath}/gonit`,
            // skip adding stats binaries to path
            `${this.pathInfo.namiAppPath}/stats`,
            // skip adding nami/bin directory as it is explicitly added at the end
            this.pathInfo.namiRuntimeBinDirectory
        ];
        const excludedPattern = /^.*\/node_modules\/.*$/;
        let pathList = [];
        for (let globPattern of globPatterns) {
            for (let filename of glob.sync(globPattern, { absolute: true })) {
                let executable = true;
                try {
                    fs.accessSync(filename, fs.constants.X_OK);
                }
                catch (e) {
                    executable = false;
                }
                if (executable && !fs.lstatSync(filename).isDirectory()) {
                    let filepathList = filename.split("/");
                    filepathList[0] = "/";
                    filepathList.pop();
                    const dirname = path.join.apply(null, filepathList);
                    if (excludedPaths.indexOf(dirname) < 0 && pathList.indexOf(dirname) < 0 && !dirname.match(excludedPattern)) {
                        pathList.push(dirname);
                    }
                }
            }
        }
        pathList.push(this.pathInfo.namiRuntimeBinDirectory);
        return pathList;
    }
    reloadFirewall() {
        // Nothing to do here, iptables does not need this
    }
    setPortMappings(portMappings) {
        super.setPortMappings(portMappings);
        this.reloadFirewall();
    }
    /**
    * Mount a device in a given directory. If specified it adds a new entry in
    * the `/etc/fstab` file.
    * @param {MountOptions|MountOptionsShared} mountConfig Options for the mount command
    * @param {Object} [extraOptions]
    * @param {boolean} [extraOptions.addToFstab] Add entry in fstab to mount in every boot
    */
    mount(mountConfig, extraOptions) {
        // set default options
        const options = Object.assign({
            addToFstab: false
        }, typeof extraOptions === "undefined" ? {} : extraOptions);
        // Convert options object to a comma-separated string
        const mountOptionsString = (options) => {
            let optionsString = "";
            for (const key in options) {
                let opt;
                if (typeof options[key] === "boolean") {
                    if (options[key] === true) {
                        opt = key;
                    }
                    else {
                        continue;
                    }
                }
                else {
                    opt = `${key}=${options[key]}`;
                }
                optionsString = optionsString.concat(`${opt},`);
            }
            optionsString = optionsString.slice(0, -1);
            return optionsString;
        };
        // create mountpoint if not created
        fs.mkdirSync(mountConfig.mountPoint, { recursive: true });
        this.logger.debug("Mount configuration", mountConfig);
        run_program_1.runProgram("mount", [
            mountConfig.device,
            mountConfig.mountPoint,
            "--type", mountConfig.type,
            "--options", mountOptionsString(mountConfig.options)
        ]);
        if (options.addToFstab) {
            mountConfig.options.nofail = true; // Avoid VM hang during boot
            const fstabFile = "/etc/fstab";
            if (!!fs.readFileSync(fstabFile).toString().match(`(${mountConfig.device}|${mountConfig.mountPoint})`)) {
                const fstabEntryRegExp = `\\s?${mountConfig.device}\\s${mountConfig.mountPoint}.*`;
                if (!!fs.readFileSync(fstabFile).toString().match(fstabEntryRegExp)) {
                    // fstab contains an entry with the device and the mount point
                    this.logger.info(`Updating ${mountConfig.device} entry in fstab`);
                    run_program_1.runProgram("sed", [
                        "-i",
                        `s#${fstabEntryRegExp}#${mountConfig.device} ${mountConfig.mountPoint} ` +
                            `${mountConfig.type} ${mountOptionsString(mountConfig.options)}#g`,
                        fstabFile
                    ]);
                }
                else {
                    // fstab contains an entry with the device or mount point but not for the given mount point
                    throw new Error(`${mountConfig.device}/${mountConfig.mountPoint} entries in ${fstabFile} are not compatible`);
                }
            }
            else {
                this.logger.info(`Adding ${mountConfig.device} to ${fstabFile}`);
                fs.appendFileSync(fstabFile, `${mountConfig.device} ${mountConfig.mountPoint} ` +
                    `${mountConfig.type} ${mountOptionsString(mountConfig.options)}`);
            }
        }
    }
}
exports.LinuxPlatform = LinuxPlatform;
