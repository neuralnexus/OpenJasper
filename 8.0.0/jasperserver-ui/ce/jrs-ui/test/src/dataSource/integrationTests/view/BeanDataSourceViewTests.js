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
import i18n from 'src/i18n/all.properties';
import resourceLocator from 'src/resource/resource.locate';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import dataSourceConfig from '../../test/mock/dataSourceConfigMock'
import BeanDataSourceView from 'src/dataSource/view/BeanDataSourceView';
import dataSourceTestingHelper from '../../test/helper/dataSourceTestingHelper'

describe('Testing BeanDataSourceView', function () {
    var beanDataSourceView, fakeServer, root;
    beforeEach(function () {
        jrsConfigs.addDataSource = dataSourceConfig;
        sinon.stub(resourceLocator, 'initialize');
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        resourceLocator.initialize.restore();
    });
    it('BeanDataSourceView should be defined', function () {
        expect(BeanDataSourceView).toBeDefined();
        expect(typeof BeanDataSourceView).toEqual("function");
    });
    describe('BeanDataSourceView\'s work', function () {
        beforeEach(function () {
            dataSourceTestingHelper.beforeEach();    // create an variable which holds the DS in the DOM object
            // create an variable which holds the DS in the DOM object
            root = $('[name=dataSourceTestArea]');    // prepare fake server
            // prepare fake server
            fakeServer = sinon.fakeServer.create();    // init the data source
            // init the data source
            beanDataSourceView = new BeanDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
                dataSourceType: 'beandatasource',
                dataSource: undefined,
                el: root
            }));
            beanDataSourceView.render();
        });
        afterEach(function () {
            // remove the data source from the page
            beanDataSourceView.remove();    // clear the testable area
            // clear the testable area
            root.empty();    // destroy fake XHR service
            // destroy fake XHR service
            fakeServer.restore();
            dataSourceTestingHelper.afterEach();
        });
        it('Check if all fields are visible', function () {
            // check data source specific fields
            expect(root.find('[name=beanName]')).toBeVisible();
            expect(root.find('[name=beanMethod]')).toBeVisible();    // check test connection button
            // check test connection button
            expect(root.find('#testDataSource').length).toBe(1);
        });
        it('Check if everything has proper default value', function () {
            // check page title
            expect($('#display .showingToolBar > .content > .header > .title').text()).toBe(i18n['resource.datasource.bean.page.title.new']);    // check data source specific fields
            // check data source specific fields
            expect(root.find('[name=beanName]').val()).toBe('');
            expect(root.find('[name=beanMethod]').val()).toBe('');
        });
        it('Checking field validation', function () {
            // we need to test specific fields -- we'll remove the value and we'll see if the
            // validation will trigger
            expect(root.find('[name=beanName]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
            expect(root.find('[name=beanMethod]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
        });
    });
});