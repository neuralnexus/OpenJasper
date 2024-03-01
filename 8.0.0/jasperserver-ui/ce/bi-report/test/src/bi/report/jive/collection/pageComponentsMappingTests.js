/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import {pageComponentsMapping} from 'src/bi/report/jive/collection/pageComponentsMapping';

const components = [
    'table',
    'column',
    'fusionChart',
    'fusionMap',
    'fusionWidget',
    'googlemap',
    'tibco-maps',
    'crosstab',
    'webfonts',
    'hyperlinks',
    'bookmarks',
    'reportparts',
    'CVComponent'
]

describe("pageComponentsMapping Tests.", () => {
    it("all page components should be functions which returns promises which are resolved into the views itself", function (done) {
        const promises = components.map(componentName => pageComponentsMapping[componentName]());

        Promise.all(promises).then((result) => {
            result.map(({default: module}) => {
                expect(module).toBeDefined();
            });
            done();
        })
    });
});