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
import JobsView from 'src/scheduler/view/JobsView';
import jobStateEnum from 'src/scheduler/enum/jobStateEnum';

describe('Scheduler Report Jobs View', function () {
    var jobsView, jobBaseMock = {
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
    beforeEach(function () {
        jobsView = new JobsView({
            runInBackgroundMode: false,
            masterViewMode: false,
            reportUri: '/public/scheduler/SalesByMonthReport',
            parentReportURI: null
        });
    });
    afterEach(function () {
        jobsView && jobsView.remove();
    });
    it('should be properly initialized', function () {
        expect(jobsView.jobsViewCollection).toBeDefined();
    });
    it('should have events object mapping', function () {
        expect(jobsView.events).toEqual({
            'click [name=backButton]': 'backButtonClick',
            'click [name=scheduleJob]': 'createButtonClick',
            'click [name=runJob]': 'runButtonClick',
            'click [name=refreshList]': 'refreshButtonClick',
            // search listeners
            'keydown [name=jobSearchInput]': 'jobSearchInputKeyDown',
            'click [name=jobSearchIcon]': 'jobSearchIconClick',
            'click [name=jobSearchClear]': 'clearJobSearch'
        });
    });
    it('should properly render an empty Report Jobs page', function () {
        jobsView.render();
        expect(jobsView.$el).toBeMatchedBy('.singleReportView');
        expect(jobsView.$el).toContainElement('[name=jobsContainer]');
        expect(jobsView.$el).toContainElement('#jobsSummaryContainer');
        expect(jobsView.$el).toContainElement('#nothingToDisplay');
        expect(jobsView.$el).not.toContainElement('#listOfJobs');
    });
    it('should properly render jobs list', function () {
        var renderCollectionSpy = sinon.spy(jobsView, 'renderCollection');
        var createJobSpy = sinon.spy(jobsView, 'createAndAddJobView');
        var initInfiniteScrollSpy = sinon.spy(jobsView, 'initInfiniteScroll');
        jobsView.render();
        expect(renderCollectionSpy).toHaveBeenCalled();
        expect(createJobSpy).not.toHaveBeenCalled();
        expect(initInfiniteScrollSpy).not.toHaveBeenCalled();
        expect(jobsView.$el).not.toContainElement('#listOfJobs');
        jobsView.jobsViewCollection.reset(jobsMock);
        expect(renderCollectionSpy).toHaveBeenCalled();
        expect(createJobSpy).toHaveBeenCalled();
        expect(initInfiniteScrollSpy).toHaveBeenCalled();
        expect(jobsView.$el).toContainElement('#listOfJobs');
        expect(jobsView.$el).toContainElement('.jobID');
        renderCollectionSpy.restore();
        createJobSpy.restore();
        initInfiniteScrollSpy.restore();
    });
});
describe('Scheduler Master View', function () {
    var jobsView;
    beforeEach(function () {
        jobsView = new JobsView({
            runInBackgroundMode: false,
            masterViewMode: true,
            reportUri: '/public/scheduler/SalesByMonthReport',
            parentReportURI: null
        });
    });
    afterEach(function () {
        jobsView && jobsView.remove();
    });
    it('should be properly rendered', function () {
        jobsView.render();
        expect(jobsView.$el).toBeMatchedBy('.masterView');
        expect(jobsView.$el).toContainElement('#secondarySearchBox');
        expect(jobsView.$el).toContainElement('#jobsSummaryContainer');
    });
    it('should search the jobs', function () {
        var searchJobsSpy = sinon.spy(jobsView, 'searchJobs');
        var setSearchingTermSpy = sinon.spy(jobsView.jobsViewCollection, 'setSearchingTerm');
        var fetchJobsSpy = sinon.spy(jobsView.jobsViewCollection, 'fetch');
        jobsView.render();
        jobsView.$('[name=jobSearchInput]').val('sales');
        jobsView.$('[name=jobSearchIcon]').trigger('click');
        expect(searchJobsSpy).toHaveBeenCalled();
        expect(setSearchingTermSpy).toHaveBeenCalledWith('sales');
        expect(fetchJobsSpy).toHaveBeenCalled();
        searchJobsSpy.restore();
        setSearchingTermSpy.restore();
        fetchJobsSpy.restore();
    });
});
