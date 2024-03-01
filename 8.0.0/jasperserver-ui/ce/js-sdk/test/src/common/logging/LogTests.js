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
import Log from 'src/common/logging/Log';
describe('Log', function () {
    var emptyFn = function () {
    };
    it('has exists', function () {
        expect(Log).toBeDefined();
    });
    it('should create log instance', function () {
        var log = new Log({ id: 'id' }, emptyFn);
        expect(typeof log).toEqual('object');
    });
    describe('log instance creation', function () {
        var log = new Log('id', emptyFn);
        it('has debug method', function () {
            expect(typeof log.debug).toEqual('function');
        });
        it('has info method', function () {
            expect(typeof log.info).toEqual('function');
        });
        it('has warn method', function () {
            expect(typeof log.warn).toEqual('function');
        });
        it('has error method', function () {
            expect(typeof log.error).toEqual('function');
        });
        it('should call callback', function () {
            var callback = sinon.spy();
            var log = new Log('id', callback);
            log.error('test error');
            expect(callback).toHaveBeenCalled();
        });
        it('should return logItem object', function () {
            var log = new Log('id', emptyFn);
            var logError = log.error('test error');
            expect(typeof logError).toEqual('object');
        });
        it('should pass logItem object as parameter to callback', function () {
            var callback = sinon.spy();
            var log = new Log('id', callback);
            var logError = log.error('test error');
            expect(callback).toHaveBeenCalledWith(logError);
        });
    });
    describe('log instance working', function () {
        it('should create logItem with id passed from logger', function () {
            var log = new Log({ id: 'testId' }, emptyFn);
            var logItem = log.error('msg');
            expect(logItem.id).toEqual('testId');
        });
        it('should create logItem with arguments', function () {
            var log = new Log({ id: 'testId' }, emptyFn);
            var arr = [
                1,
                2,
                3
            ];
            var logItem = log.error('msg', arr);
            expect(logItem.args[0]).toEqual('msg');
            expect(logItem.args[1]).toBe(arr);
        });
        it('should create logItem with arguments', function () {
            var log = new Log({ id: 'id' }, emptyFn);
            var logItem = log.error('msg');
            expect(logItem.time instanceof Date).toBeTruthy();
        });
        it('should create logItem with level DEBUG', function () {
            var log = new Log({ id: 'id' }, emptyFn);
            var logItem = log.debug('msg');
            expect(logItem.level.toString()).toEqual('DEBUG');
        });
        it('should create logItem with level INFO', function () {
            var log = new Log({ id: 'id' }, emptyFn);
            var logItem = log.info('msg');
            expect(logItem.level.toString()).toEqual('INFO');
        });
        it('should create logItem with level WARN', function () {
            var log = new Log({ id: 'id' }, emptyFn);
            var logItem = log.warn('msg');
            expect(logItem.level.toString()).toEqual('WARN');
        });
        it('should create logItem with level ERROR', function () {
            var log = new Log({ id: 'id' }, emptyFn);
            var logItem = log.error('msg');
            expect(logItem.level.toString()).toEqual('ERROR');
        });
    });
});