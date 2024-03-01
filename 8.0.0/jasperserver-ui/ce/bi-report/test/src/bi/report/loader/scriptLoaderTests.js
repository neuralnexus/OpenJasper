/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import {loadScript} from 'src/bi/report/loader/scriptLoader';

describe("scriptLoader Tests.", function () {
    it("should load script", function (done) {
        const scriptTest = 'window.__special_external_script_value = true';
        loadScript(`data:text/plain;base64,${btoa(scriptTest)}`).then(() => {
            expect(window.__special_external_script_value).toBeTruthy();
            delete window.__special_external_script_value;
            done();
        })
    });

    it("should fail script loading", function (done) {
        loadScript('http://localhost/some/script').catch((e) => {
            done();
        })
    });

    it("should fail script loading by timeout", function (done) {
        loadScript('http://localhost/some/script', {timeout: 0}).catch((e) => {
            done();
        })
    });
});