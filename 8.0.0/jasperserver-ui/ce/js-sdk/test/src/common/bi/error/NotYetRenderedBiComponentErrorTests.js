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

import sinon from 'sinon'
import BiComponentError from 'src/common/bi/error/BiComponentError';
import errorCodes from 'src/common/bi/error/enum/biComponentErrorCodes';
import NotYetRenderedBiComponentError from 'src/common/bi/error/NotYetRenderedBiComponentError';
describe('NotYetRenderedBiComponentError tests', function () {
    it('should BiComponentError instance', function () {
        expect(typeof NotYetRenderedBiComponentError).toBe('function');
        expect(NotYetRenderedBiComponentError.prototype instanceof BiComponentError).toBeTruthy();
    });
    it('should call base constructor with specific arguments', function () {
        var constructor = BiComponentError.prototype.constructor;
        var constructorSpy = sinon.stub(BiComponentError.prototype, 'constructor').callsFake(constructor);
        var error = new NotYetRenderedBiComponentError();
        expect(error.parameters).toBeUndefined();
        expect(error.errorCode).toBe(errorCodes.NOT_YET_RENDERED_ERROR);
        constructorSpy.restore();
    });
});