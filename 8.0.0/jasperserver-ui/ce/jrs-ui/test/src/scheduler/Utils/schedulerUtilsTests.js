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
import schedulerUtils from '../../../../src/scheduler/util/schedulerUtils';
import $ from 'jquery';

describe('New serialize parameter', function () {
    var paramsMap = {
        'reportUnitURI': '%2Fpublic%2FSamples%2FReports%2F01._Geographic_Results_by_Segment_Report',
        'resourceType': "DashboardModelResource",
        'parentDashboardUnitURI': '%2Fpublic%2FSamples%2FReports%2F01._Geographic_Results_by_Segment_Report',
        'decorate': 'no'
    };
    it('should call _scheduleDashboard', function () {
        var scheduleStub = sinon.stub(schedulerUtils, '_serializeParams').returns('');
        schedulerUtils._scheduleDashboard(paramsMap, '');
        scheduleStub.restore();
    });
    it('should call _serializeParams', function () {
        expect(typeof schedulerUtils._serializeParams).toBe('function');
        schedulerUtils._serializeParams(paramsMap);
    });
    it('should call _ParamMapping', function () {
        expect(typeof schedulerUtils._ParamMapping).toBe('function');
        var scheduleStub = sinon.stub(schedulerUtils, '_scheduleDashboard').returns('');
        schedulerUtils._ParamMapping();
        expect(scheduleStub).toHaveBeenCalled();
        scheduleStub.restore();
    });
    it('should open scheduler page in overlay', function () {
        var addDimmerSpy = sinon.spy(schedulerUtils, '_addDimmer');
        schedulerUtils._scheduleDashboard(paramsMap, true);
        expect(addDimmerSpy).toHaveBeenCalled();
        expect(typeof schedulerUtils._scheduleDashboard).toBe('function');
        addDimmerSpy.restore();

    });
    it('should resize overlay on window resize', function () {
        const clock = sinon.useFakeTimers();
        let el = $("<div></div>");
        el.appendTo(document.body);
        schedulerUtils._onWindowResize();
        clock.tick(100);
        expect(typeof schedulerUtils._onWindowResize).toBe('function');
        expect("200px", el.css("height"));
        expect("200px", el.css("width"));
        clock.restore();

    });
    it('should detach events on resize and closing overlay', function () {
        schedulerUtils._detachEvents();
        expect(typeof schedulerUtils._detachEvents).toBe('function');
    });
    it('should close overlay on _closeScheduleOverlay', function () {
        let getParamSpy = sinon.spy(schedulerUtils, 'getParamsFromUri');
        schedulerUtils.getParamsFromUri();
        expect(getParamSpy).toHaveBeenCalled();
        expect(schedulerUtils.getParamsFromUri).returned;
        getParamSpy.restore();

    })

});