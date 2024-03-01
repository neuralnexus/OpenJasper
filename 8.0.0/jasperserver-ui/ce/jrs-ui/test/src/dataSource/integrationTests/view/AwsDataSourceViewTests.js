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
import jrsConfigs from 'js-sdk/src/jrs.configs';
import resourceLocator from 'src/resource/resource.locate';
import dataSourceConfig from '../../test/mock/dataSourceConfigMock'
import AwsDataSourceView from 'src/dataSource/view/AwsDataSourceView';
import dataSourceTestingHelper from '../../test/helper/dataSourceTestingHelper'

describe('Testing AwsDataSourceView', function () {
    var awsDataSourceView, fakeServer, root, stub = {};
    beforeEach(function () {
        jrsConfigs.addDataSource = dataSourceConfig;
        sinon.stub(resourceLocator, 'initialize');
        stub.initDataSourceTree = sinon.stub(AwsDataSourceView.prototype, 'initDataSourceTree');
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        resourceLocator.initialize.restore();
        AwsDataSourceView.prototype.initDataSourceTree.restore();
    });
    it('AwsDataSourceView should be defined', function () {
        expect(AwsDataSourceView).toBeDefined();
        expect(typeof AwsDataSourceView).toEqual("function");
    });
    describe('AwsDataSourceView\'s work', function () {
        beforeEach(function () {
            dataSourceTestingHelper.beforeEach();    // create an variable which holds the DS in the DOM object
            // create an variable which holds the DS in the DOM object
            root = $('[name=dataSourceTestArea]');    // prepare fake server
            // prepare fake server
            fakeServer = sinon.fakeServer.create();    // prepare the response
            // prepare the response
            fakeServer.respondWith('GET', jrsConfigs.contextPath + '/rest_v2/jdbcDrivers', [
                200,
                { 'Content-Type': 'application/json' },
                JSON.stringify({
                    'jdbcDrivers': [
                        {
                            'name': 'mysql',
                            'label': 'MySQL',
                            'available': true,
                            'jdbcUrl': 'jdbc:mysql://$[dbHost]:$[dbPort]/$[dbName]',
                            'jdbcDriverClass': 'org.mariadb.jdbc.Driver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '3306'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                }
                            ]
                        },
                        {
                            'name': 'mysql_oracle',
                            'label': 'MySQL',
                            'available': false,
                            'jdbcUrl': 'jdbc:mysql://$[dbHost]:$[dbPort]/$[dbName]',
                            'jdbcDriverClass': 'com.mysql.jdbc.Driver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '3306'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                }
                            ]
                        },
                        {
                            'name': 'postgresql',
                            'label': 'PostgreSQL',
                            'available': true,
                            'jdbcUrl': 'jdbc:postgresql://$[dbHost]:$[dbPort]/$[dbName]',
                            'jdbcDriverClass': 'org.postgresql.Driver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '5432'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                }
                            ],
                            'isDefault': true
                        },
                        {
                            'name': 'ingres',
                            'label': 'Ingres',
                            'available': true,
                            'jdbcUrl': 'jdbc:ingres://$[dbHost]:$[dbPort]/$[dbName]',
                            'jdbcDriverClass': 'com.ingres.jdbc.IngresDriver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '117'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                }
                            ]
                        },
                        {
                            'name': 'oracle',
                            'label': 'Oracle',
                            'available': true,
                            'jdbcUrl': 'jdbc:oracle:thin:@$[dbHost]:$[dbPort]:$[sName]',
                            'jdbcDriverClass': 'oracle.jdbc.OracleDriver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '1521'
                                },
                                {
                                    'key': 'sName',
                                    'value': 'orcl'
                                }
                            ]
                        },
                        {
                            'name': 'sqlserver',
                            'label': 'MS SQL Server 2005',
                            'available': true,
                            'jdbcUrl': 'jdbc:sqlserver://$[dbHost]:$[dbPort];databaseName=$[dbName]',
                            'jdbcDriverClass': 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '1433'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                }
                            ]
                        },
                        {
                            'name': 'sqlserver2000',
                            'label': 'MS SQL Server 2000',
                            'available': false,
                            'jdbcUrl': 'jdbc:microsoft:sqlserver://$[dbHost]:$[dbPort];DatabaseName=$[dbName]',
                            'jdbcDriverClass': 'com.microsoft.jdbc.sqlserver.SQLServerDriver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '1433'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                }
                            ]
                        },
                        {
                            'name': 'db2',
                            'label': 'IBM DB2',
                            'available': true,
                            'jdbcUrl': 'jdbc:db2://$[dbHost]:$[dbPort]/$[dbName]:driverType=$[driverType];currentSchema=$[schemaName];',
                            'jdbcDriverClass': 'com.ibm.db2.jcc.DB2Driver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '50000'
                                },
                                {
                                    'key': 'driverType',
                                    'value': '4'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                },
                                {
                                    'key': 'schemaName',
                                    'value': 'schemaname'
                                }
                            ]
                        },
                        {
                            'name': 'vertica',
                            'label': 'Vertica',
                            'available': true,
                            'jdbcUrl': 'jdbc:vertica://$[dbHost]:$[dbPort]/$[dbName]',
                            'jdbcDriverClass': 'com.vertica.Driver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '5433'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                }
                            ]
                        },
                        {
                            'name': 'informix',
                            'label': 'Informix',
                            'available': false,
                            'jdbcUrl': 'jdbc:informix-sqli://$[dbHost]:$[dbPort]/$[dbName]:INFORMIXSERVER=$[informixServerName]',
                            'jdbcDriverClass': 'com.informix.jdbc.IfxDriver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '1526'
                                },
                                {
                                    'key': 'dbName',
                                    'value': 'dbname'
                                },
                                {
                                    'key': 'informixServerName',
                                    'value': 'informixServerName'
                                }
                            ]
                        },
                        {
                            'name': 'SYBASE',
                            'label': 'Sybase jConnect',
                            'available': false,
                            'jdbcUrl': 'jdbc:sybase:Tds:$[dbHost]:$[dbPort]?ServiceName=$[sName]',
                            'jdbcDriverClass': 'com.sybase.jdbc4.jdbc.SybDriver',
                            'defaultValues': [
                                {
                                    'key': 'dbHost',
                                    'value': 'localhost'
                                },
                                {
                                    'key': 'dbPort',
                                    'value': '5433'
                                },
                                {
                                    'key': 'sName',
                                    'value': 'serviceName'
                                }
                            ]
                        }
                    ],
                    '_links': {
                        'create': {
                            'profile': 'POST',
                            'relation': 'create'
                        },
                        'edit': {
                            'profile': 'PUT',
                            'relation': 'edit'
                        }
                    },
                    '_embedded': {}
                })
            ]);    // init the data source
            // init the data source
            awsDataSourceView = new AwsDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
                dataSourceType: 'awsdatasource',
                dataSource: undefined,
                el: root
            }));    // respond to data source
            // respond to data source
            fakeServer.respond();
            awsDataSourceView.render();
        });
        afterEach(function () {
            // remove the data source from the page
            awsDataSourceView.remove();    // clear the testable area
            // clear the testable area
            root.empty();    // destroy fake XHR service
            // destroy fake XHR service
            fakeServer.restore();
            dataSourceTestingHelper.afterEach();
        });
        it('Check if all fields are visible', function () {
            // check data source specific fields
            expect(root.find('[name=credentialsType]').length).toBe(2);
            expect(root.find('[name=accessKey]')).toBeVisible();
            expect(root.find('[name=secretKey]')).toBeVisible();
            expect(root.find('[name=roleArn]')).toBeVisible();
            expect(root.find('[name=region]')).toBeVisible();
            expect(root.find('#findAwsDataSources')).toBeVisible();
            expect(root.find('#awsDataSourceTree')).toBeVisible();
            expect(root.find('[name=username]')).toBeVisible();
            expect(root.find('[name=password]')).toBeVisible();
            expect(root.find('[name=dbName]')).toBeVisible();
            expect(root.find('[name=driverClass]')).toBeVisible();
            expect(root.find('#driverUploadButton')).toBeVisible();
            expect(root.find('[name=connectionUrl]')).toBeVisible();    // check timezone
            // check timezone
            expect(root.find('[name=timezone]')).toBeVisible();    // check test connection button
            // check test connection button
            expect(root.find('#testDataSource').length).toBe(1);
        });
        it('Check if everything has proper default value', function () {
            // check page title
            expect($('#display .showingToolBar > .content > .header > .title').text()).toBe(i18n['resource.datasource.aws.page.title.new']);    // check data source specific fields
            // check data source specific fields
            expect(root.find('[name=credentialsType]:checked').val()).toBe('aws');
            expect(root.find('[name=accessKey]').val()).toBe('');
            expect(root.find('[name=secretKey]').val()).toBe('');
            expect(root.find('[name=roleArn]').val()).toBe('');
            expect(root.find('[name=region]').val()).toBe('us-east-1.amazonaws.com');
            expect(root.find('[name=username]').val()).toBe('');
            expect(root.find('[name=password]').val()).toBe('');
            expect(root.find('[name=dbName]').val()).toBe('');
            expect(root.find('[name=driverClass]').val()).toBe('');
            expect(root.find('[name=connectionUrl]').val()).toBe('');    // check timezone
            // check timezone
            expect(root.find('[name=timezone]').val()).toBe('');
        });
        it('Checking field validation', function () {
            // we need to test specific fields -- we'll remove the value and we'll see if the
            // validation will trigger
            expect(root.find('[name=accessKey]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
            expect(root.find('[name=secretKey]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
            expect(root.find('[name=username]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
            expect(root.find('[name=dbName]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
            expect(root.find('[name=driverClass]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
            expect(root.find('[name=connectionUrl]').val('').trigger('change').parent().hasClass('error')).toBeTruthy();
        });
        it('Checking JDBC Driver class field behavior', function () {
            root.find('[name=driverClass]').val('').trigger('change');
            expect(root.find('#driverUploadButton span').text()).toBe(i18n['resource.dataSource.jdbc.upload.addDriverButton']);
            expect(root.find('#driverUploadButton').attr('disabled')).toBe('disabled');
            root.find('[name=driverClass]').val('org.postgresql.Driver').trigger('change');
            expect(root.find('#driverUploadButton span').text()).toBe(i18n['resource.dataSource.jdbc.upload.editDriverButton']);
            expect(root.find('#driverUploadButton').attr('disabled')).toBe(undefined);
            root.find('[name=driverClass]').val('org.postgresql').trigger('change');
            expect(root.find('#driverUploadButton span').text()).toBe(i18n['resource.dataSource.jdbc.upload.addDriverButton']);
            expect(root.find('#driverUploadButton').attr('disabled')).toBe(undefined);
        });
    });
});