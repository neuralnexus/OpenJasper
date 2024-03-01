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
import _ from 'underscore';
import JobsCollection from 'src/scheduler/collection/JobsCollection';
import jobStateEnum from 'src/scheduler/enum/jobStateEnum';
import config from 'js-sdk/src/jrs.configs';

describe('Scheduler Jobs Collection', function () {
    var jobsCollection, initOptions = {
            runInBackgroundMode: false,
            masterViewMode: false,
            reportUri: '/public/scheduler/SalesByMonthReport',
            parentReportURI: null
        }, jobBaseMock = {
            owner: 'superuser',
            reportLabel: '11. Sales By Month Report',
            reportUnitURI: '/public/scheduler/SalesByMonthReport',
            state: {
                'nextFireTime': '2016-09-14T00:00:00+03:00',
                'value': jobStateEnum.NORMAL
            }
        }, jobsMock = [
            _.extend({
                id: 22,
                label: 'testJob_1'
            }, jobBaseMock),
            _.extend({
                id: 26,
                label: 'testJob_2'
            }, jobBaseMock),
            _.extend({
                id: 4,
                label: 'testJob_3'
            }, jobBaseMock),
            _.extend({
                id: 12,
                label: 'testJob_4'
            }, jobBaseMock)
        ];
    it('should be properly initialized', function () {
        jobsCollection = new JobsCollection(null, initOptions);
        expect(jobsCollection.initialAmountOfJobsToLoad).toBeDefined();
        expect(jobsCollection.amountOfJobsToLoad).toBeDefined();
        expect(jobsCollection.loadMoreJobs).toBeDefined();
        expect(jobsCollection.length).toBe(0);
    });
    it('should fetch the report jobs from server', function () {
        jobsCollection = new JobsCollection(null, initOptions);
        var getAllJobsSpy = sinon.spy(jobsCollection, 'getJobsOfAllReportsWeHave');
        var getJobSpy = sinon.spy(jobsCollection, 'getJobOfReport');
        var resetSpy = sinon.spy(jobsCollection, 'reset');
        var fakeServer = sinon.fakeServer.create();
        fakeServer.respondWith('GET', new RegExp(config.contextPath + '/rest_v2/reports/public/scheduler/SalesByMonthReport/options\\?_=\\d*'), [
            200,
            { 'Content-Type': 'application/json' },
            'No options found'
        ]);
        fakeServer.respondWith('GET', new RegExp(config.contextPath + '/rest_v2/jobs\\?reportUnitURI=/public/scheduler/SalesByMonthReport\\&_=\\d*'), [
            200,
            { 'Content-Type': 'application/json' },
            JSON.stringify({ jobsummary: jobsMock })
        ]);
        expect(jobsCollection.length).toBe(0);
        jobsCollection.fetch();
        fakeServer.respond();
        fakeServer.respond();
        expect(getAllJobsSpy).toHaveBeenCalled();
        expect(getJobSpy).toHaveBeenCalled();
        expect(resetSpy).toHaveBeenCalled();
        expect(jobsCollection.length).toBe(jobsMock.length);
        getAllJobsSpy.restore();
        getJobSpy.restore();
        resetSpy.restore();
        fakeServer.restore();
    });
    it('should fetch all jobs from server for Master View', function () {
        jobsCollection = new JobsCollection(null, initOptions);
        jobsCollection.options.masterViewMode = true;
        expect(jobsCollection.length).toBe(0);
        var getJobsPaginationSpy = sinon.spy(jobsCollection, 'getJobsWithPagination');
        var getJobsFromBackendSpy = sinon.spy(jobsCollection, 'getJobsFromBackend');
        var resetSpy = sinon.spy(jobsCollection, 'reset');
        var fakeServer = sinon.fakeServer.create();
        fakeServer.respondWith('GET', new RegExp(config.contextPath + '/rest_v2/jobs\\?sortType=SORTBY_REPORTURI\\&numberOfRows=100\\&_=\\d*'), [
            200,
            { 'Content-Type': 'application/json' },
            JSON.stringify({ jobsummary: jobsMock })
        ]);
        jobsCollection.fetch();
        fakeServer.respond();
        expect(getJobsPaginationSpy).toHaveBeenCalled();
        expect(getJobsFromBackendSpy).toHaveBeenCalled();
        expect(resetSpy).toHaveBeenCalled();
        expect(jobsCollection.length).toBe(jobsMock.length);
        getJobsPaginationSpy.restore();
        getJobsFromBackendSpy.restore();
        resetSpy.restore();
        fakeServer.restore();
    });
});
