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
import ResourceModel from 'bi-repository/src/bi/repository/model/RepositoryResourceModel';
import SimpleDomainModel from 'src/dataSource/fileDataSource/SimpleDomainModel';

describe('Testing SimpleDomainModel', function () {
    var simpleDomainModel, fakeServer, stub = {};
    beforeEach(function () {
        fakeServer = sinon.fakeServer.create();
    });
    afterEach(function () {
        fakeServer.restore();
    });
    it('SimpleDomainModel should be defined', function () {
        expect(SimpleDomainModel).toBeDefined();
        expect(typeof SimpleDomainModel).toEqual("function");
    });
    it('Checking validation entities', function () {
        expect(SimpleDomainModel.prototype.validation.columns).toBeDefined();
    });
    it('SimpleDomainModel initialize method should call its parent', function () {
        stub = sinon.stub(ResourceModel.prototype, 'initialize');
        simpleDomainModel = new SimpleDomainModel({}, {});
        expect(stub).toHaveBeenCalled();
        ResourceModel.prototype.initialize.restore();
    });
    it('parse method should convert response from the server in a special way', function () {
        stub = sinon.stub(ResourceModel.prototype, 'parse').callsFake(function (data) {
            return data;
        });
        var response = {
            dataSource: { dataSourceReference: 'abc' },
            metadata: { columns: 'def' }
        };
        var expectingData = {
            dataSourceUri: 'abc',
            columns: 'def'
        };
        var data = SimpleDomainModel.prototype.parse(response);
        expect(data).toEqual(expectingData);
        ResourceModel.prototype.parse.restore();
    });
    it('toJSON method should convert model to the server\'s presentation in a special way', function () {
        stub = sinon.stub(ResourceModel.prototype, 'toJSON').callsFake(function (data) {
            return data;
        });
        var model = {
            dataSourceUri: 'abc',
            columns: [
                {
                    name: 'column1',
                    show: true
                },
                {
                    name: 'column2',
                    show: false
                },
                {
                    name: 'column3',
                    show: true
                },
                {
                    name: 'column4',
                    show: false
                }
            ]
        };
        var convertedModel = {
            dataSource: { dataSourceReference: 'abc' },
            metadata: {
                columns: [
                    {
                        name: 'column1',
                        show: true
                    },
                    {
                        name: 'column3',
                        show: true
                    }
                ],
                queryLanguage: 'csv'
            }
        };
        var data = SimpleDomainModel.prototype.toJSON(model);
        expect(data).toEqual(convertedModel);
        ResourceModel.prototype.toJSON.restore();
    });
    it('parseMetadata method should add \'show\' attributes', function () {
        var data = {
            columns: [
                { label: 'abc' },
                { label: 'def' },
                { label: 'ghi' }
            ]
        };
        var expectingData = {
            columns: [
                {
                    label: 'abc',
                    show: true
                },
                {
                    label: 'def',
                    show: true
                },
                {
                    label: 'ghi',
                    show: true
                }
            ]
        };
        data = SimpleDomainModel.prototype.parseMetadata(data);
        expect(data).toEqual(expectingData);
    });
});