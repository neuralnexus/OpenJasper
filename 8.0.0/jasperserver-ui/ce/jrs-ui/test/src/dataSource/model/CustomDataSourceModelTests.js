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
import BaseDataSourceModel from 'src/dataSource/model/BaseDataSourceModel';
import CustomDataSourceModel from 'src/dataSource/model/CustomDataSourceModel';

describe('Testing CustomDataSourceModel', function () {
    var model;
    beforeEach(function () {
        model = new CustomDataSourceModel({}, { dataSourceType: 'someType' });
    });
    it('CustomDataSourceModel should be defined', function () {
        expect(CustomDataSourceModel).toBeDefined();
        expect(typeof CustomDataSourceModel).toEqual("function");
    });
    it('toJSON() calls super and this.customFieldsToJSON()', function () {
        var superToJSON = sinon.spy(BaseDataSourceModel.prototype, 'toJSON');
        var customFieldsToJSON = sinon.spy(model, 'customFieldsToJSON');
        model.toJSON();
        expect(superToJSON).toHaveBeenCalled();
        expect(customFieldsToJSON).toHaveBeenCalled();
        superToJSON.restore();
        customFieldsToJSON.restore();
    });
    it('customFieldsToJSON()', function () {
        var data = {
            field1: 'value1',
            field2: 'value2',
            field3: 'value3'
        };
        var customFields = [
            { name: 'field1' },
            { name: 'field2' },
            { name: 'field3' },
            { name: 'field4' }
        ];
        var json = model.customFieldsToJSON(data, customFields);
        expect(json).toBe(data);
        expect(json.field1).toBeUndefined();
        expect(json.field2).toBeUndefined();
        expect(json.field3).toBeUndefined();
        expect(json.properties).toBeDefined();
        expect(json.properties.length).toBe(3);
        expect(json.properties).toEqual([
            {
                key: 'field1',
                value: 'value1'
            },
            {
                key: 'field2',
                value: 'value2'
            },
            {
                key: 'field3',
                value: 'value3'
            }
        ]);
    });
});