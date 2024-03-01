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

import {ControlsBase} from 'src/controls/controls.base';

describe('Controls Base', function () {
    var url = 'http://localhost:8080/jasperserver-pro/flow.html?boolean=true' + '&undefined&undefined1=undefined&normalstring=string&emptystring=&number=2&array=1&array=a&null=null';
    var json = {
        'boolean': ['true'],
        'undefined': [''],
        'undefined1': ['undefined'],
        'normalstring': ['string'],
        'emptystring': [''],
        'number': ['2'],
        'array': [
            '1',
            'a'
        ],
        'null': ['null']
    };
    var paramString = 'boolean=true&undefined=&undefined1=undefined&normalstring=string&emptystring=&number=2&array=1&array=a&null=null';
    var extraParamsJson = {
        'extra1': 'val',
        'extra2': [
            'val1',
            'val2'
        ]
    };
    var extraParamsString = 'extra1=val&extra2=val1&extra2=val2';
    it('Build params url string', function () {
        var actual = ControlsBase.buildParams(json);
        expect(actual).toEqual(paramString);
    });
    it('Build params url string, null argument', function () {
        var actual = ControlsBase.buildParams(null);
        expect(actual).toEqual('');
    });
    it('Build params url string with extra params', function () {
        var actual = ControlsBase.buildSelectedDataUri(json, extraParamsJson);
        var expected = paramString + '&' + extraParamsString;
        expect(actual).toEqual(expected);
    });
    it('Build params url string with extra params not object', function () {
        var actual = ControlsBase.buildSelectedDataUri(json, true);
        expect(actual).toEqual(paramString);
    });
});