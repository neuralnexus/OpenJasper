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
import _ from 'underscore';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import resourceLocator from 'src/resource/resource.locate';
import dataSourceConfig from '../../test/mock/dataSourceConfigMock'
import BaseDataSourceView from 'src/dataSource/view/BaseDataSourceView';
import dataSourceTestingHelper from '../../test/helper/dataSourceTestingHelper'

describe('Testing BaseDataSourceView', function () {
    var baseDataSourceView, fakeServer, root;
    beforeEach(function () {
        jrsConfigs.addDataSource = dataSourceConfig;
        sinon.stub(resourceLocator, 'initialize');
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        resourceLocator.initialize.restore();
    });
    it('BaseDataSourceView should be defined', function () {
        expect(BaseDataSourceView).toBeDefined();
        expect(typeof BaseDataSourceView).toEqual("function");
    });
    describe('BaseDataSourceView\'s work', function () {
        beforeEach(function () {
            dataSourceTestingHelper.beforeEach();    // prepare fake server
            // prepare fake server
            fakeServer = sinon.fakeServer.create();    // create an variable which holds the DS in the DOM object
            // create an variable which holds the DS in the DOM object
            root = $('[name=dataSourceTestArea]');    // init the data source
            // init the data source
            baseDataSourceView = new BaseDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
                dataSourceType: undefined,
                dataSource: undefined,
                el: root
            }));
            baseDataSourceView.render();
            baseDataSourceView.renderTimezoneSection();
            baseDataSourceView.renderTestConnectionSection();
        });
        afterEach(function () {
            // remove the data source from the page
            baseDataSourceView.remove();    // clear the testable area
            // clear the testable area
            root.empty();    // destroy fake XHR service
            // destroy fake XHR service
            fakeServer.restore();
            dataSourceTestingHelper.afterEach();
        });
        it('Check if all fields are visible', function () {
            // check timezone
            expect(root.find('[name=timezone]')).toBeVisible();    // check test connection button
            // check test connection button
            expect(root.find('#testDataSource').length).toBe(1);
        });
        it('Check if everything has proper default value', function () {
            // check timezone
            expect(root.find('[name=timezone]').val()).toBe('');
        });
    });
});