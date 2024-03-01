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

import PaginationModel from 'src/common/component/pagination/model/PaginationModel';
import Backbone from 'backbone';
describe('Pagination Model', function () {
    var paginationModel;
    beforeEach(function () {
        paginationModel = new PaginationModel({
            current: 2,
            total: 20
        });
    });
    it('should be Backbone.Model instance', function () {
        expect(typeof PaginationModel).toBe('function');
        expect(PaginationModel.prototype instanceof Backbone.Model).toBeTruthy();
    });
    it('should not pass validation', function () {
        paginationModel.set('current', 'string', { validate: true });
        expect(paginationModel.validationError.current.code).toEqual('error.pagination.property.integer.value');
        paginationModel.set('current', NaN, { validate: true });
        expect(paginationModel.validationError.current.code).toEqual('error.pagination.property.integer.value');
        paginationModel.set('current', -1, { validate: true });
        expect(paginationModel.validationError.current.code).toEqual('error.pagination.property.min.value');
        paginationModel.set('current', 200, { validate: true });
        expect(paginationModel.validationError.current.code).toEqual('error.pagination.property.max.value');
    });
});