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

import biComponentUtil from 'src/common/bi/component/util/biComponentUtil';
import $ from 'jquery';

describe('biComponentUtil', function () {
    it('should be able to deep clone ignoring jQuery objects', function () {
        var jqObj = $('<div></div>');
        expect(biComponentUtil.cloneDeep(jqObj)).toBe(jqObj);
        var domObj = $(document.getElementsByTagName('body')[0]);
        expect(biComponentUtil.cloneDeep(domObj)).toBe(domObj);
    });
});