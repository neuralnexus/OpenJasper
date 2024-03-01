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
import $ from 'jquery';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import resourceLocator from 'src/resource/resource.locate';
import dataSourceConfig from '../test/mock/dataSourceConfigMock'
import dataSourceTestingHelper from '../test/helper/dataSourceTestingHelper'
import DataSourceController from 'src/dataSource/DataSourceController';
import jasperserverConfig from 'src/i18n/jasperserver_config.properties';

describe('Testing DataSourceController', function () {
    var dataSourceController, root, sandbox, ajaxStub;
    beforeEach(function () {
        sandbox = sinon.createSandbox({
            useFakeTimers: true
        });
        ajaxStub = sandbox.stub($, 'ajax');
        ajaxStub.throws(new Error('Some of $.ajax calls has not been stubbed'));

        jrsConfigs.addDataSource = dataSourceConfig;
        sinon.stub(resourceLocator, 'initialize');
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        resourceLocator.initialize.restore();
        sandbox.restore();
    });
    describe('DataSourceController\'s work', function () {
        beforeEach(function () {
            dataSourceTestingHelper.beforeEach();    // create an variable which holds the DS in the DOM object
            // create an variable which holds the DS in the DOM object
            root = $('[name=dataSourceTestArea]');    // prepare fake server

            // prepare the responses
            const customDataSourcesUrl = jrsConfigs.contextPath + '/rest_v2/customDataSources';
            const customDataSourcesPayload = {
                'definition': [
                    'MongoDbDataSource',
                    'diagnosticCustomDataSource',
                    'HiveDataSource',
                    'webScraperDataSource'
                ]
            };
            ajaxStub.withArgs(sinon.match({ type: 'GET', url: customDataSourcesUrl })).callsFake(({success}) => {
                success && success(customDataSourcesPayload);
                return $.Deferred().resolve(customDataSourcesPayload, 'success').promise();
            });

            const jdbcDriversUrl = jrsConfigs.contextPath + '/rest_v2/jdbcDrivers';
            const jdbcDriversPayload = {
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
            };
            ajaxStub.withArgs(sinon.match({ type: 'GET', url: jdbcDriversUrl })).callsFake(({success}) => {
                success && success(jdbcDriversPayload);
                return $.Deferred().resolve(jdbcDriversPayload, 'success').promise();
            });

            // init the data source
            dataSourceController = new DataSourceController(_.extend({}, jrsConfigs.addDataSource.initOptions, { el: root }));    // send the response to the DS
            // send the response to the DS

            sandbox.clock.tick(100);
            dataSourceController.render();
            root.append(dataSourceController.$el);
        });
        afterEach(function () {
            // remove the data source from the page
            dataSourceController.remove();    // clear the testable area
            // clear the testable area
            root.empty();    // destroy fake XHR service
            dataSourceTestingHelper.afterEach();
        });
        it('Check if DS interface has Save anc Cancel buttons', function () {
            // check if Save and Cancel buttons are presented
            expect(root.find('button#saveBtn')).toBeVisible();
            expect(root.find('button#cancelBtn')).toBeVisible();
        });
        it('Check if DS interface has been rendered with initial default state (it means with JBDC)', function () {
            // check if select Data Source has the proper selected value
            expect(root.find('[name=dataSourceType]').val()).toBe('jdbcdatasource');
        });
        it('Checking if Data Source gets re-rendered if we select different DS type', function () {
            // select MongoDB as a custom DS with specific view and check it !
            const mongoDbDataSourceUrl = jrsConfigs.contextPath + '/rest_v2/customDataSources/MongoDbDataSource';
            const mongoDbDataSourcePayload = {
                'name': 'MongoDbDataSource',
                'queryTypes': ['MongoDbQuery'],
                'propertyDefinitions': [
                    {
                        'name': 'mongoURI',
                        'label': 'MongoDbDataSource.properties.mongoURI',
                        'defaultValue': 'mongodb://hostname:27017/database123'
                    },
                    {
                        'name': 'username',
                        'label': 'MongoDbDataSource.properties.username',
                        'defaultValue': 'username123'
                    },
                    {
                        'name': 'password',
                        'label': 'MongoDbDataSource.properties.password',
                        'defaultValue': jasperserverConfig['input.password.substitution']
                    },
                    {
                        'name': 'schema',
                        'label': 'MongoDbDataSource.properties.schema',
                        'defaultValue': ''
                    }
                ],
                'testable': true
            };
            ajaxStub.withArgs(sinon.match({ type: 'GET', url: mongoDbDataSourceUrl })).callsFake(({success}) => {
                success && success(mongoDbDataSourcePayload);
                return $.Deferred().resolve(mongoDbDataSourcePayload, 'success').promise();
            });

            root.find('[name=dataSourceType]').val('MongoDbDataSource').trigger('change');

            sandbox.clock.tick(100);
            // and now make the complete check of MongoDB (since this is part of our PRD we may do this)
            expect(root.find('[name=mongoURI]')).toBeVisible();
            expect(root.find('[name=mongoURI]').val()).toBe('mongodb://hostname:27017/database123');
            expect(root.find('[name=username]')).toBeVisible();
            expect(root.find('[name=username]').val()).toBe('username123');
            expect(root.find('[name=password]')).toBeVisible();
            expect(root.find('[name=password]').val()).toBe(jasperserverConfig['input.password.substitution']);
            expect(root.find('button#saveBtn')).toBeVisible();
            expect(root.find('button#cancelBtn')).toBeVisible();
        });
        it('Checking how we render the \'webScraperDataSource\' custom data source', function () {
            // now, select webScraperDataSource as a custom DS with specific view and check it !
            const webScraperDataSourceUrl = jrsConfigs.contextPath + '/rest_v2/customDataSources/webScraperDataSource';
            const webScraperDataSourcePayload = {
                'name': 'webScraperDataSource',
                'queryTypes': ['webscraper'],
                'propertyDefinitions': [
                    {
                        'name': 'url123',
                        'label': 'webScraperDataSource.properties.url'
                    },
                    {
                        'name': 'path123',
                        'label': 'webScraperDataSource.properties.path',
                        'defaultValue': 'somePath'
                    }
                ],
                'testable': false
            };

            ajaxStub.withArgs(sinon.match({ type: 'GET', url: webScraperDataSourceUrl })).callsFake(({success}) => {
                success && success(webScraperDataSourcePayload);
                return $.Deferred().resolve(webScraperDataSourcePayload, 'success').promise();
            });

            root.find('[name=dataSourceType]').val('webScraperDataSource').trigger('change');
            sandbox.clock.tick(100);

            // and now make the complete check of webScraperDataSource (since this is part of our PRD we may do this)
            expect(root.find('[name=url123]')).toBeVisible();
            expect(root.find('[name=url123]').val()).toBe('');
            expect(root.find('[name=path123]')).toBeVisible();
            expect(root.find('[name=path123]').val()).toBe('somePath');
            expect(root.find('button#saveBtn')).toBeVisible();
            expect(root.find('button#cancelBtn')).toBeVisible();
        });
    });
});