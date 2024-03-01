/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import {loadJsonp} from 'src/bi/report/loader/jsonpLoader';

describe("jsonpLoader Tests.", function () {
    it("should load jsonp script", function (done) {
        const scriptUrl = 'http://localhost/test/somescript';
        loadJsonp(scriptUrl).catch(() => {
            const allScripts = document.getElementsByTagName('script')
            let urlStr;
            for (const script of allScripts) {
                if (script.src && script.src.startsWith(scriptUrl)) {
                    urlStr = script.src;
                    break;
                }
            }

            const url = new URL(urlStr);
            expect(url.searchParams.has('callback'));
            done();
        })
    });

    it("should load jsonp script with custom callback", function (done) {
        const scriptUrl = 'http://localhost/test/somescript2';
        loadJsonp(scriptUrl, 'customCallback').catch(() => {
            const allScripts = document.getElementsByTagName('script')
            let urlStr;
            for (const script of allScripts) {
                if (script.src && script.src.startsWith(scriptUrl)) {
                    urlStr = script.src;
                    break;
                }
            }

            const url = new URL(urlStr);
            expect(url.searchParams.has('customCallback'));
            done();
        })
    });
});