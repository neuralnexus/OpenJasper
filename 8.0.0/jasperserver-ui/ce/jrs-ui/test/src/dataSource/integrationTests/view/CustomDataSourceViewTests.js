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
import resourceLocator from 'src/resource/resource.locate';
import i18n from 'src/i18n/all.properties';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import dataSourceConfig from '../../test/mock/dataSourceConfigMock'
import CustomDataSourceView from 'src/dataSource/view/CustomDataSourceView';
import dataSourceTestingHelper from '../../test/helper/dataSourceTestingHelper'

describe('Testing CustomDataSourceView', function () {
    var customDataSourceView, sandbox, root, ajaxStub;
    beforeEach(function () {
        sandbox = sinon.createSandbox({
            useFakeTimers: true
        })
        jrsConfigs.addDataSource = dataSourceConfig;
        sandbox.stub(resourceLocator, 'initialize');

        ajaxStub = sandbox.stub($, 'ajax');
        ajaxStub.throws(new Error('Some of $.ajax calls has not been stubbed'));
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        resourceLocator.initialize.restore();
        sandbox.restore();
    });
    it('CustomDataSourceView should be defined', function () {
        expect(CustomDataSourceView).toBeDefined();
        expect(typeof CustomDataSourceView).toEqual("function");
    });
    describe('CustomDataSourceView\'s work', function () {
        beforeEach(function () {
            dataSourceTestingHelper.beforeEach();    // create an variable which holds the DS in the DOM object
            // create an variable which holds the DS in the DOM object
            root = $('[name=dataSourceTestArea]');    // prepare fake server

            const customDataSourceUrl = jrsConfigs.contextPath + '/rest_v2/customDataSources/CustomDataSource';
            const customDataSourcePayload = {
                'name': 'CustomDbDataSource',
                'queryTypes': ['CustomDbQuery'],
                'propertyDefinitions': [
                    {
                        'name': 'customURI',
                        'label': 'CustomDbDataSource.properties.customURI',
                        'defaultValue': 'customdb://hostname:27017/database'
                    },
                    {
                        'name': 'username',
                        'label': 'CustomDbDataSource.properties.username',
                        'defaultValue': 'username',
                        'properties': [{
                            'key': 'mandatory',
                            'value': true
                        }]
                    },
                    {
                        'name': 'password',
                        'label': 'CustomDbDataSource.properties.password',
                        'defaultValue': 'password'
                    }
                ],
                'testable': true
            };
            ajaxStub.withArgs(sinon.match({ type: 'GET', url: customDataSourceUrl })).callsFake(({success}) => {
                success && success(customDataSourcePayload);
                return $.Deferred().resolve(customDataSourcePayload, 'success').promise();
            });


            // init the data source
            customDataSourceView = new CustomDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
                dataSourceType: 'CustomDataSource',
                dataSource: undefined,
                el: root
            }));

            sandbox.clock.tick(100);

            customDataSourceView.render();
        });
        afterEach(function () {
            // remove the data source from the page
            customDataSourceView.remove();    // clear the testable area
            // clear the testable area
            root.empty();    // destroy fake XHR service
            dataSourceTestingHelper.afterEach();
        });
        it('Check if all fields are visible', function () {
            // check data source specific fields
            expect(root.find('[name=customURI]')).toBeVisible();
            expect(root.find('[name=username]')).toBeVisible();
            expect(root.find('[name=password]')).toBeVisible();    // check test connection button
            // check test connection button
            expect(root.find('#testDataSource').length).toBe(1);
        });
        it('Check if everything has proper default value', function () {
            // check page title
            expect($('#display .showingToolBar > .content > .header > .title').text()).toBe(i18n['resource.datasource.custom.page.title.new']);    // check data source specific fields
            // check data source specific fields
            expect(root.find('[name=customURI]').val()).toBe('customdb://hostname:27017/database');
            expect(root.find('[name=username]').val()).toBe('username');
            expect(root.find('[name=password]').val()).toBe(i18n['input.password.substitution']);
        });
        it('Checking field validation', function () {
            // we need to test specific fields -- we'll remove the value and we'll see if the
            // validation will trigger
            expect(root.find('[name=username]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
        });
    });
});