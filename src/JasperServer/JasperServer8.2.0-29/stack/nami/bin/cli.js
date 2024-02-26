#!/usr/bin/env node
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */

// in some cases the HOME env variable is not set,
// ensure for root it is /root before continuing
if ((process.env.HOME === undefined) && (process.getuid() === 0)) {
  process.env.HOME='/root';
}

var CLI = require('../lib/cli').CLI;

(async() => {
  try{
    await (new CLI(process.argv)).handle();
  } catch (e) {
    process.exit(1);
  }
})();
