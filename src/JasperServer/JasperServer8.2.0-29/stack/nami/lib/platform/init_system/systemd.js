"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.SystemDInitSystem = void 0;
const fs = require("fs");
const run_program_1 = require("../../run_program");
/**
 * SystemD based init system implementation
 */
class SystemDInitSystem {
    enableService(name) {
        run_program_1.runProgram("systemctl", ["enable", `${name}.service`]);
    }
    disableService(name) {
        run_program_1.runProgram("systemctl", ["disable", `${name}.service`]);
    }
    startService(name) {
        run_program_1.runProgram("systemctl", ["start", `${name}.service`]);
    }
    stopService(name) {
        run_program_1.runProgram("systemctl", ["stop", `${name}.service`]);
    }
    restartService(name) {
        run_program_1.runProgram("systemctl", ["restart", `${name}.service`]);
    }
    isServiceStarted(name) {
        const result = run_program_1.runProgram("systemctl", ["status", `${name}.service`], {
            retrieveStdStreams: true
        });
        return result.code === 0;
    }
    installService(name, description, content, options) {
        const header = `#!/bin/bash\n# service: ${name}\n#\n`;
        const systemdLimits = "LimitNOFILE=65536\nLimitNPROC=32768\n";
        const initScriptName = `/etc/init.d/${name}`;
        fs.writeFileSync(initScriptName, `${header}${content}`);
        fs.chmodSync(initScriptName, "755");
        const startRunLevels = [2, 3, 4, 5];
        let before = options.before ||
            startRunLevels.map((l) => {
                return `runlevel${l}.target`;
            }).join(" ");
        before = before + " shutdown.target";
        const wants = options.wants || "network-online.target";
        const after = options.after || "network-online.target remote-fs.target";
        const serviceFile = `/etc/systemd/system/${name}.service`;
        const serviceContent = `[Unit]
SourcePath=/etc/init.d/${name}
Description=LSB: ${name} init script
Before=${before}
After=${after}
Wants=${wants}
Conflicts=shutdown.target

[Service]
Type=forking
Restart=no
TimeoutSec=30min
IgnoreSIGPIPE=no
KillMode=process
GuessMainPID=no
RemainAfterExit=yes
SysVStartPriority=1
ExecStart=${initScriptName} start
ExecStop=${initScriptName} stop
${systemdLimits}

# Output needs to appear in instance console output
StandardOutput=journal+console

[Install]
WantedBy=multi-user.target
`;
        fs.writeFileSync(serviceFile, serviceContent);
        fs.chmodSync(serviceFile, "755");
    }
}
exports.SystemDInitSystem = SystemDInitSystem;
