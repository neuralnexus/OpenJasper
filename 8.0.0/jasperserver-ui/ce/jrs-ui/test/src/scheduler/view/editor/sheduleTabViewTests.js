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
import Backbone from "backbone";
import moment from 'moment';

import HolidayCalsCollection from 'src/scheduler/collection/HolidayCalsCollection';
import ScheduleTabView from 'src/scheduler/view/editor/ScheduleTabView';

describe("Scheduler Tab View:", function() {

    ScheduleTabView.prototype.model = new Backbone.Model({trigger: {timezone:null}});

    let app;
    let fetchStub;

    beforeEach(() => {
        fetchStub = sinon.stub(HolidayCalsCollection.prototype, 'fetch');
        app = new ScheduleTabView();
        app.render();
    });

    afterEach(() => {
        app.remove();
        fetchStub.restore();
    });

    describe("method setupDatepickersOn:", function () {
        it("should update timepicker zone if model timezone is changed", function () {
            app.model.set('trigger', {timezone: "America/Denver"});
            app.model.trigger("change:trigger");

            const calendar = app.$el.find(".datepicker")[0];
            const actual = $.datepicker._getInst(calendar).settings.timepicker.timezone;

            const expected = moment.tz("America/Denver").utcOffset();

            expect(actual).toBe(expected);
        });
    });
});
