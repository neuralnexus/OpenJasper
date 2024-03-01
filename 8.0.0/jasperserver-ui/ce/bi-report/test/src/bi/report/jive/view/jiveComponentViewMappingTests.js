/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import {jiveComponentViewMapping} from 'src/bi/report/jive/view/jiveComponentViewMapping';

const components = [
    'fusionMap',
    'fusionChart',
    'fusionWidget',
    'googlemap',
    'tibco-maps',
    'CVComponent',
    'table',
    'crosstab'
]

describe("jiveComponentViewMapping Tests.", () => {
    it("all jive view components should be functions which returns promises which are resolved into the views itself", function (done) {
        const promises = components.map(componentName => jiveComponentViewMapping[componentName]());

        Promise.all(promises).then((result) => {
            result.map(({default: module}) => {
                expect(module).toBeDefined();
            });
            done();
        })
    });
});