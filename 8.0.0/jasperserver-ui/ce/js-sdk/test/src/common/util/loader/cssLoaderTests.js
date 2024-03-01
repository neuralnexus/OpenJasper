/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import {loadCss} from 'src/common/util/loader/cssLoader';

describe("cssLoader Tests.", function () {
    it("should load css", function () {
        const link = loadCss('http://localhost/test/css');
        expect(link).toBeDefined();
        const allLinks = document.getElementsByTagName('link');

        let isLinkInDocument = false;
        for (const documentLink of allLinks) {
            if (documentLink === link) {
                isLinkInDocument = true;
                break;
            }
        }

        expect(isLinkInDocument).toBeTruthy();
    });

    it("should load css from cache", function () {
        const link1 = loadCss('http://localhost/test/css');
        const link2 = loadCss('http://localhost/test/css');
        expect(link2).toEqual(link1);
    });

    it("should load css and not use cache", function () {
        const link1 = loadCss('http://localhost/test/css');
        link1.remove();
        const link2 = loadCss('http://localhost/test/css', false);

        expect(link2).not.toEqual(link1);
    });
});