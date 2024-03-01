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
import errors from 'src/common/enum/errorCodes';
import BaseModel from 'src/common/model/BaseModel';
describe('BaseModel', function () {
    describe('Create Server Error', function () {
        it('creates default error object if can\'t parse server response', function () {
            var fakeXhr = {
                responseText: '<html><body>Not Found</body></html>',
                status: 404
            };
            var serverError = BaseModel.createServerError(fakeXhr);
            expect(serverError).toEqual({
                message: 'Can\'t parse server response',
                errorCode: errors.UNEXPECTED_ERROR,
                parameters: []
            });
        });
        it('can serialize error from server response', function () {
            var fakeXhr = { responseText: '{"message":"Something 1 wrong 2","errorCode":"something.wrong","parameters":[1,2]}' };
            var serverError = BaseModel.createServerError(fakeXhr);
            expect(serverError).toEqual({
                message: 'Something 1 wrong 2',
                errorCode: 'something.wrong',
                parameters: [
                    1,
                    2
                ]
            });
        });
    });
    it('should serialize server response and issue new \'error:*\' events when \'error\' event is triggered on model', function () {
        var model = new BaseModel(), triggerSpy = sinon.spy(model, 'trigger'), fakeXhr = {
                status: 404,
                statusText: 'not found',
                responseText: '{"message":"Not Found!!!","errorCode":"e123","parameters":[1]}'
            }, errorObj = {
                message: 'Not Found!!!',
                errorCode: 'e123',
                parameters: [1]
            };
        model.trigger('error', model, fakeXhr, {});
        sinon.assert.calledWith(triggerSpy, 'error');
        sinon.assert.calledWith(triggerSpy, 'error:notFound', model, errorObj, fakeXhr);
        sinon.assert.calledWith(triggerSpy, 'error:all', model, errorObj, fakeXhr);
        triggerSpy.restore();
    });
    it('can serialize to plain javascript object', function () {
        var obj = {
            a: '1',
            b: '2',
            c: '3'
        };
        var model = new BaseModel(obj);
        expect(model.serialize()).not.toBe(obj);
        expect(model.serialize()).toEqual(obj);
    });
});