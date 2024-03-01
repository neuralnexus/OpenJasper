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
import CustomDataSourceModel from 'src/dataSource/model/CustomDataSourceModel';
import TextDataSourceModel from 'src/dataSource/fileDataSource/TextDataSourceModel';

describe('Testing TextDataSourceModel', function () {
    var textDataSourceModel, fakeServer, stub = {};
    beforeEach(function () {
        fakeServer = sinon.fakeServer.create();
    });
    afterEach(function () {
        fakeServer.restore();
    });
    it('TextDataSourceModel should be defined', function () {
        expect(TextDataSourceModel).toBeDefined();
        expect(typeof TextDataSourceModel).toEqual("function");
    });
    it('TextDataSourceModel initialize method should call its parent', function () {
        stub = sinon.stub(CustomDataSourceModel.prototype, 'initialize');
        textDataSourceModel = new TextDataSourceModel({}, { dataSourceType: true });
        expect(stub).toHaveBeenCalled();
        CustomDataSourceModel.prototype.initialize.restore();
    });
    it('toJSON static method should remove some attributes', function () {
        var result;
        TextDataSourceModel.prototype.attributes = {
            attr1: 'value1',
            fieldDelimiter: 'comma',
            rowDelimiter: 'comma',
            encodingType: 'utf8',
            attr2: 'value2'
        };
        result = TextDataSourceModel.prototype.toJSON();
        expect(_.isUndefined(result.rowDelimiter)).toBeTruthy();
        expect(_.isUndefined(result.encodingType)).toBeTruthy();
    });
    describe('Testing TextDataSourceModel\'s work', function () {
        beforeEach(function () {
            textDataSourceModel = new TextDataSourceModel({}, { dataSourceType: true });
        });
        it('Checking default values of some attributes', function () {
            expect(textDataSourceModel.attributes.fileSourceType).toBe('repository');
        });
        it('Checking validation entities', function () {
            expect(textDataSourceModel.validation.repositoryFileName).toBeDefined();
            expect(textDataSourceModel.validation.serverFileName).toBeDefined();
            expect(textDataSourceModel.validation.serverAddress).toBeDefined();
            expect(textDataSourceModel.validation.serverPath).toBeDefined();
            expect(textDataSourceModel.validation.ftpsPort).toBeDefined();
        });
    });
});