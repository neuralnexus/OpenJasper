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

import Marionette from 'backbone.marionette';
import BaseTable from 'src/common/component/baseTable/BaseTable';
describe('BaseTable component', function () {
    var baseTable;
    beforeEach(function () {
        baseTable = new BaseTable();
    });
    afterEach(function () {
        baseTable && baseTable.remove();
    });
    it('should be Marionette.CompositeView instance', function () {
        expect(typeof BaseTable).toBe('function');
        expect(BaseTable.prototype instanceof Marionette.CompositeView).toBeTruthy();
    });
    it('should have public methods', function () {
        expect(baseTable.initialize).toBeDefined();
        expect(baseTable.remove).toBeDefined();
    });
    it('should initialize tooltip', function () {
        baseTable && baseTable.remove();
        var options = {
            tooltip: {
                i18n: {},
                template: '<div></div>'
            }
        };
        baseTable = new BaseTable(options);
        expect(baseTable.tooltip).toBeDefined();
    });
    it('shouldn\'t initialize tooltip', function () {
        baseTable && baseTable.remove();
        var options = { tooltip: { i18n: {} } };
        baseTable = new BaseTable(options);
        expect(baseTable.tooltip).toBeUndefined();
    });
});