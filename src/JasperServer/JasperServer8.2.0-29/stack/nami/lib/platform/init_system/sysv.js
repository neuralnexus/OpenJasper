"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.SysVInitSystem = void 0;
const fs = require("fs");
const run_program_1 = require("../../run_program");
/**
 * SysV based init system implementation
 */
class SysVInitSystem {
    enableService(name, startLevel, stopLevel) {
        run_program_1.runProgram("update-rc.d", [name, "defaults", startLevel, stopLevel]);
    }
    disableService(name) {
        run_program_1.runProgram("update-rc.d", ["-f", name, "remove"]);
    }
    startService(name) {
        run_program_1.runProgram("service", [name, "start"]);
    }
    stopService(name) {
        run_program_1.runProgram("service", [name, "stop"]);
    }
    restartService(name) {
        run_program_1.runProgram("service", [name, "restart"]);
    }
    isServiceStarted(name) {
        const result = run_program_1.runProgram("service", [name, "status"], {
            retrieveStdStreams: true
        });
        return result.code === 0;
    }
    installService(name, description, content, options) {
        const requiredStart = options.requiredStart || "$network $remote_fs";
        const requiredStop = options.requiredStop || "$network $remote_fs";
        description = description || name;
        const shortDescription = options.shortDescription || description;
        // TODO: should startRunLevels / stopRunLevels be moved to options ?
        const startRunLevels = "2 3 4 5";
        const stopRunLevels = "0 1 6";
        const initScriptName = `/etc/init.d/${name}`;
        let header = `#!/bin/bash
### BEGIN INIT INFO
# Provides:           ${name}
# Required-Start:     ${requiredStart}
# Required-Stop:      ${requiredStop}
# Default-Start:      ${startRunLevels}
# Default-Stop:       ${stopRunLevels}
# Short-Description:  ${shortDescription}
# Description:        ${description}
### END INIT INFO
`;
        fs.writeFileSync(initScriptName, `${header}${content}`);
        fs.chmodSync(initScriptName, "755");
    }
}
exports.SysVInitSystem = SysVInitSystem;
