"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
var sysv_1 = require("./sysv");
Object.defineProperty(exports, "SysVInitSystem", { enumerable: true, get: function () { return sysv_1.SysVInitSystem; } });
var systemd_1 = require("./systemd");
Object.defineProperty(exports, "SystemDInitSystem", { enumerable: true, get: function () { return systemd_1.SystemDInitSystem; } });
