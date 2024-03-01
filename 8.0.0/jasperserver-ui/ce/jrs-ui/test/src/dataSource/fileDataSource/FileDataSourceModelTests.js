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
import FileDataSourceModel from 'src/dataSource/fileDataSource/FileDataSourceModel';

describe('Testing BaseDataSourceModel', function () {
    var fileDataSourceModel, fakeServer, stub = {};
    beforeEach(function () {
        fakeServer = sinon.fakeServer.create();
    });
    afterEach(function () {
        fakeServer.restore();
    });
    it('FileDataSourceModel should be defined', function () {
        expect(FileDataSourceModel).toBeDefined();
        expect(typeof FileDataSourceModel).toEqual("function");
    });
    it('FileDataSourceModel initialize method should call its parent', function () {
        stub = sinon.stub(CustomDataSourceModel.prototype, 'initialize');
        fileDataSourceModel = new FileDataSourceModel({}, { dataSourceType: true });
        expect(stub).toHaveBeenCalled();
        CustomDataSourceModel.prototype.initialize.restore();
    });
    it('toJSON static method should remove some attributes', function () {
        var result;
        FileDataSourceModel.prototype.attributes = {
            attr1: 'value1',
            fileSourceType: '',
            useFirstRowAsHeader: true,
            repositoryFileName: 'repo',
            attr2: 'value2'
        };
        result = FileDataSourceModel.prototype.toJSON();
        expect(_.isUndefined(result.rowDelimiter)).toBeTruthy();
        expect(_.isUndefined(result.encodingType)).toBeTruthy();
    });
    it('parse static method should convert some attributes', function () {
        stub = sinon.stub(CustomDataSourceModel.prototype, 'parse').callsFake(function () {
            return { fileName: 'repo:/abc/def' };
        });
        var result = FileDataSourceModel.prototype.parse();
        expect(result.fileSourceType).toEqual('repository');
        expect(result.repositoryFileName).toEqual('/abc/def');
        CustomDataSourceModel.prototype.parse.restore();
    });
    describe('Testing FileDataSourceModel\'s work', function () {
        beforeEach(function () {
            fileDataSourceModel = new FileDataSourceModel({}, { dataSourceType: true });
        });
        it('Checking validation entities', function () {
            expect(fileDataSourceModel.validation.repositoryFileName).toBeDefined();
            expect(fileDataSourceModel.validation.serverFileName).toBeDefined();
            expect(fileDataSourceModel.validation.serverAddress).toBeDefined();
            expect(fileDataSourceModel.validation.serverPath).toBeDefined();
            expect(fileDataSourceModel.validation.ftpsPort).toBeDefined();
        });
    });
});