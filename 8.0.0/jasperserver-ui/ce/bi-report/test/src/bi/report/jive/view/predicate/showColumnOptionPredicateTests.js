/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import showColumnOptionPredicate from 'src/bi/report/jive/view/predicate/showColumnOptionPredicate';

describe("showColumnOptionPredicate Tests.", function () {

    it("should return true if there are some interactive non-visible columns", function () {
        expect(showColumnOptionPredicate(
            [
                {
                    interactive: true,
                    visible: false
                },
                {
                    interactive: true,
                    visible: true
                }
            ]
        )).toEqual(true);
    });

    it("should return false if there no interactive non-visible controls", function () {
        expect(showColumnOptionPredicate(
            [
                {
                    interactive: false,
                    visible: false
                },
                {
                    interactive: true,
                    visible: true
                }
            ]
        )).toEqual(false);
    });
});