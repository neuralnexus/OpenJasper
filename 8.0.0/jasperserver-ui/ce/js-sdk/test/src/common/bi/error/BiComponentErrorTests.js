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

import BiComponentError from 'src/common/bi/error/BiComponentError';
describe('BiComponentError tests', function () {
    it('should accept \'errorCode\' and \'message\' as arguments in constructor', function () {
        var error = new BiComponentError(100, 'test');
        expect(error.errorCode).toBe(100);
        expect(error.message).toBe('test');
    });
    it('should have overridden \'toString\' method', function () {
        var error = new BiComponentError(100, 'test');
        expect(error.toString()).toBe('100 : test');
    });
    it('should be instanceof Error', function () {
        var error = new BiComponentError(100, 'test');
        expect(error instanceof Error).toBe(true);
    });
});