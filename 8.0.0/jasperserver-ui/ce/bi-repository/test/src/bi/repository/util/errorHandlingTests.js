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
import errorHandling from 'src/bi/repository/util/errorHandling';
import i18n from 'src/i18n/RepositoryResourceBundle.properties';
describe('errorHandling BI component', function () {
    describe('ErrorHandling Tests', function () {
        var sandbox, errorMessages, xhr;
        beforeEach(function () {
            sandbox = sinon.createSandbox();
            errorMessages = {
                404: 'error 404',
                403: 'error 403',
                401: 'error 401',
                400: sandbox.stub().returns('errorMessage400'),
                unknown: 'unknown error'
            };
            i18n['error.unknown.error'] = 'unknownErrorMessage';
        });
        afterEach(function () {
            sandbox.restore();
        });
        it('should fallback to unknown error handler if it\'s a function', function () {
            errorMessages = { unknown: sandbox.stub().returns('errorMessage400') };
            xhr = {
                responseJSON: {
                    message: 'errorMessage',
                    errorCode: 'errorCode'
                },
                status: 400
            };
            expect(errorHandling.mapXhrErrorToMessage(xhr, errorMessages)).toEqual('errorMessage400');
        });
        it('should return error message for status 400', function () {
            xhr = {
                responseJSON: {
                    message: 'errorMessage',
                    errorCode: 'errorCode'
                },
                status: 400
            };
            expect(errorHandling.mapXhrErrorToMessage(xhr, errorMessages)).toEqual('errorMessage400');
        });
        it('should return error message for status 404', function () {
            xhr = {
                responseJSON: {
                    message: 'errorMessage404',
                    errorCode: 'errorCode'
                },
                status: 404
            };
            expect(errorHandling.mapXhrErrorToMessage(xhr, errorMessages)).toEqual('error 404');
        });
        it('should return error message for status 403', function () {
            xhr = {
                responseJSON: {
                    message: 'errorMessage403',
                    errorCode: 'errorCode'
                },
                status: 403
            };
            expect(errorHandling.mapXhrErrorToMessage(xhr, errorMessages)).toEqual('error 403');
        });
        it('should return error message for status 401', function () {
            xhr = {
                responseJSON: {
                    message: 'errorMessage401',
                    errorCode: 'errorCode'
                },
                status: 401
            };
            expect(errorHandling.mapXhrErrorToMessage(xhr, errorMessages)).toEqual('error 401');
        });
        it('should return error message for invalid status', function () {
            xhr = {
                responseJSON: {
                    message: 'errorMessage4895',
                    errorCode: 'errorCode'
                },
                status: 4895
            };
            expect(errorHandling.mapXhrErrorToMessage(xhr, errorMessages)).toEqual('errorMessage4895');
        });
        it('should return error message for status unknown with empty xhr message', function () {
            xhr = {
                responseJSON: {
                    message: '',
                    errorCode: 'errorCode'
                },
                status: 7.65
            };
            expect(errorHandling.mapXhrErrorToMessage(xhr, errorMessages)).toEqual('unknownErrorMessage');
        });
    });
});