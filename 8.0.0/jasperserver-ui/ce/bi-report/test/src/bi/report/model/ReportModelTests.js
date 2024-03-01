/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import sinon from 'sinon';
import $ from "jquery";
import ReportModel from "../../../../../src/bi/report/model/ReportModel";

describe("ReportModel tests", function() {
    var model,
        sandbox;

    beforeEach(function () {
        sandbox = sinon.createSandbox();

        model = new ReportModel();
    });

    afterEach(function () {
        sandbox.restore();
        model = null;

    });

    it("should model status be changed when cancel method is called", function() {
        var executeReportStub = sinon.stub(model.execution, "remove").callsFake(function() {
        });
        var exeModelCancelStub = sinon.stub(model.execution, "cancel").callsFake(function() {
            return (new $.Deferred()).resolve({value:'canceled'});
        });
        expect(model.get('status')).toBeUndefined();
        model.cancel();
        expect(model.get('status')).toBe('canceled');
        executeReportStub.restore();
        exeModelCancelStub.restore();
    });
});