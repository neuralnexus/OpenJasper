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
import $ from 'jquery';
import ConfirmationDialog from 'js-sdk/src/common/component/dialog/ConfirmationDialog';
import JobModel from 'src/scheduler/model/JobModel';
import JobView from 'src/scheduler/view/JobView';
import jobStateEnum from 'src/scheduler/enum/jobStateEnum';
import date from 'js-sdk/src/common/util/parse/date';

describe("Scheduler Job View", function() {
    var jobModel,
        jobView,
        sandbox;

    beforeEach(function() {
        sandbox = sinon.createSandbox();

        sandbox.stub(date, "isoTimestampToLocalizedTimestampByTimezone").returns("localizedDate");

        jobModel = new JobModel({
            id: 22,
            label: "testJob_1",
            owner: "superuser",
            reportLabel: "11. Sales By Month Report",
            reportUnitURI: "/public/scheduler/SalesByMonthReport",
            state: {"nextFireTime": "2016-09-14T00:00:00+03:00", "value": jobStateEnum.NORMAL}
        });

        jobView = new JobView({
            model: jobModel,
            masterViewMode: false
        });
    });

    afterEach(function() {
        sandbox.restore();

        jobView && jobView.remove();
    });

    it("should be properly initialized", function() {
        expect(typeof jobView.template === "function").toBeTruthy();
    });

    it("should have events object mapping", function() {
        expect(jobView.events).toEqual({
            'click [name=editJob]': 'edit',
            'click [name=deleteJob]': 'remove',
            'change [name=enableJob]': 'enable'
        })
    });

    it("should be properly rendered", function() {
        jobView.render();
        expect(jobView.$el).toContainElement(".jobID");
        expect(jobView.$(".jobID").text()).toEqual("22");
    });

    it("should should change the job state", function() {
        jobView.render();

        expect(jobView.model.get("state").value).toEqual(jobStateEnum.NORMAL);

        // emulate checkbox click
        jobView.$("input[name=enableJob]")[0].checked = false;
        jobView.$("input[name=enableJob]").trigger("change");

        expect(jobView.model.get("state").value).toEqual("PAUSED");
    });

    it("should open the confirmation dialog on job remove", function() {
        var openConfirmStub = sinon.stub(ConfirmationDialog.prototype, "open");
        jobView.render();

        // open confirm dialog
        jobView.remove();

        expect(openConfirmStub).toHaveBeenCalled();

        openConfirmStub.restore();
    });

    it("should destroy model on remove confirm", function() {
        var destroyModelStub = sinon.stub(jobView.model, "destroy");

        jobView.render();

        // open confirm dialog
        jobView.remove();

        // click YES
        $(".schedulerJobRemoveDialog button.primary").trigger("click");

        expect(destroyModelStub).toHaveBeenCalled();

        destroyModelStub.restore();
    });

    it("should correctly render last run date and next run date", function() {
        jobView.remove();

        jobModel = new JobModel({
            id: 22,
            label: "testJob_1",
            owner: "superuser",
            reportLabel: "11. Sales By Month Report",
            reportUnitURI: "/public/scheduler/SalesByMonthReport",
            state: {
                "previousFireTime": "2016-09-13T00:00:00+03:00",
                "nextFireTime": "2016-09-14T00:00:00+03:00",
                "value": jobStateEnum.NORMAL
            }
        });

        jobView = new JobView({
            model: jobModel,
            masterViewMode: false
        });

        jobView.render();

        var lastRunDate = $.trim(jobView.$(".lastRanDate").first().text()),
            nextRunDate = $.trim(jobView.$(".nextRunDate").first().text());

        expect(date.isoTimestampToLocalizedTimestampByTimezone).toHaveBeenCalledWith("2016-09-13T00:00:00+03:00");
        expect(date.isoTimestampToLocalizedTimestampByTimezone).toHaveBeenCalledWith("2016-09-14T00:00:00+03:00");

        expect(lastRunDate).toEqual("localizedDate");
        expect(nextRunDate).toEqual("localizedDate");
    });
});
