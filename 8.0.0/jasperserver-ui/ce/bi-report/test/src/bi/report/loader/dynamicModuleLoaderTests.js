/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
import sinon from 'sinon';
import {loadDynamicModule} from 'src/bi/report/loader/dynamicModuleLoader';
import $ from 'jquery';

describe("dynamicModuleLoader Tests.", () => {
    let sandbox;
    const testModuleContent = "define('test', function() { return 'testResult' })";

    beforeEach(() => {
        sandbox = sinon.createSandbox();
        sandbox.stub($, 'ajax');
    });

    afterEach(() => {
        sandbox.restore();
    });

    it("should load module dynamically", (done) => {
        $.ajax.returns(Promise.resolve(testModuleContent))

        loadDynamicModule('test', 'http://localhost/testModule').then((module) => {
            expect(module).toEqual('testResult');
            done();
        });
    });

    it("should load module dynamically from cache", (done) => {
        $.ajax.returns(Promise.resolve(testModuleContent))

        const promise = loadDynamicModule('test', 'http://localhost/testModule');
        promise.then(() => {
            return loadDynamicModule('test', 'http://localhost/testModule')
        }).then((module) => {
            expect(module).toEqual('testResult');
            done();
        });
    });
});