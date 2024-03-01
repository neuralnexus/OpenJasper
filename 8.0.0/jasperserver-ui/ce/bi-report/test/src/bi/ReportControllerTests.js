/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
import sinon from 'sinon';
import ReportController from 'src/bi/report/ReportController';
import $ from 'jquery';
import Backbone from 'backbone'

describe("Report Controller tests", function() {
    var sandbox;

    var successStub = function() { return new $.Deferred().resolve()};

    beforeEach(function() {
        sandbox = sinon.createSandbox();
    });

    afterEach(function() {
        sandbox.restore();
    });

    it('should cancel execution, remove it and remove view on destroy', function(done){
        const controller = new ReportController(new Backbone.Model());
        sandbox.stub(controller, 'cancelReportExecution').callsFake(successStub);
        sandbox.stub(controller.model, 'removeExecution').callsFake(successStub);
        sandbox.spy(controller.view, 'remove');

        controller.destroy().done(() => {
            expect(controller.cancelReportExecution).toHaveBeenCalled();
            expect(controller.model.removeExecution).toHaveBeenCalled();
            expect(controller.view.remove).toHaveBeenCalled();

            done()
        });
    });
});