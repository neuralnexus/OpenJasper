/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import hideColumnOptionPredicate from 'src/bi/report/jive/view/predicate/hideColumnOptionPredicate';

describe("hideColumnOptionPredicate Tests.", function () {

    it("should return true if there are more than 1 visible interactive column", function () {
        expect(hideColumnOptionPredicate(
            [
                {
                    interactive: true,
                    visible: true
                },
                {
                    interactive: true,
                    visible: true
                }
            ]
        )).toEqual(true);
    });

    it("should return false if there are less than 1 visible interactive column", function () {
        expect(hideColumnOptionPredicate(
            [
                {
                    interactive: false,
                    visible: true
                },
                {
                    interactive: true,
                    visible: false
                }
            ]
        )).toEqual(false);
    });
});
