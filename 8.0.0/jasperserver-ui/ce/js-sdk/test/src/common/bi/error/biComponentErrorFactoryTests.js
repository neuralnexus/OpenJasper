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

import JavaScriptExceptionBiComponentError from 'src/common/bi/error/JavaScriptExceptionBiComponentError';
import ContainerNotFoundBiComponentError from 'src/common/bi/error/ContainerNotFoundBiComponentError';
import SchemaValidationBiComponentError from 'src/common/bi/error/SchemaValidationBiComponentError';
import AlreadyDestroyedBiComponentError from 'src/common/bi/error/AlreadyDestroyedBiComponentError';
import NotYetRenderedBiComponentError from 'src/common/bi/error/NotYetRenderedBiComponentError';
import RequestBiComponentError from 'src/common/bi/error/RequestBiComponentError';
import biComponentErrorFactory from 'src/common/bi/error/biComponentErrorFactory';
describe('biComponentErrorFactory tests', function () {
    it('should have \'validationError\' method', function () {
        expect(typeof biComponentErrorFactory.validationError).toBe('function');
        var error = biComponentErrorFactory.validationError({
            dataPath: '/0',
            message: '1'
        });
        expect(error instanceof SchemaValidationBiComponentError).toBe(true);
    });
    it('should have \'javaScriptException\' method', function () {
        expect(typeof biComponentErrorFactory.javaScriptException).toBe('function');
        var error = biComponentErrorFactory.javaScriptException(new Error('test'));
        expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
    });
    it('should have \'requestError\' method', function () {
        expect(typeof biComponentErrorFactory.requestError).toBe('function');
        var error = biComponentErrorFactory.requestError({ status: 400 });
        expect(error instanceof RequestBiComponentError).toBe(true);
    });
    it('should have \'containerNotFoundError\' method', function () {
        expect(typeof biComponentErrorFactory.containerNotFoundError).toBe('function');
        var error = biComponentErrorFactory.containerNotFoundError('#main');
        expect(error instanceof ContainerNotFoundBiComponentError).toBe(true);
    });
    it('should have \'alreadyDestroyedError\' method', function () {
        expect(typeof biComponentErrorFactory.alreadyDestroyedError).toBe('function');
        var error = biComponentErrorFactory.alreadyDestroyedError();
        expect(error instanceof AlreadyDestroyedBiComponentError).toBe(true);
    });
    it('should have \'notYetRenderedError\' method', function () {
        expect(typeof biComponentErrorFactory.notYetRenderedError).toBe('function');
        var error = biComponentErrorFactory.notYetRenderedError();
        expect(error instanceof NotYetRenderedBiComponentError).toBe(true);
    });
});