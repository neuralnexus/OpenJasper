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
import logger from 'src/common/logging/logger';
describe('Logging', function () {
    let sandbox;
    beforeEach(() => {
        sandbox = sinon.createSandbox();
    });
    afterEach(() => {
        sandbox.restore();
    });

    it('has exists', function () {
        expect(logger).toBeDefined();
    });
    it('has register method', function () {
        expect(typeof logger.register).toBe('function');
    });
    it('has enable method', function () {
        expect(typeof logger.enable).toBe('function');
    });
    it('has disable method', function () {
        expect(typeof logger.disable).toBe('function');
    });
    it('has setLevel method', function () {
        expect(typeof logger.setLevel).toBe('function');
    });
    describe('register method', function () {
        var log1 = logger.register('testString1');
        var log11 = logger.register('testString1');
        var log2 = logger.register({ id: 'testObject' });
        it('should create log instance with string', function () {
            expect(log1).toBeDefined();
        });
        it('should set id from string argument', function () {
            expect(log1._id).toEqual('testString1');
        });
        it('should return existed log instance', function () {
            expect(log1).toBe(log11);
        });
        it('should create log instance with object', function () {
            expect(log2).toBeDefined();
        });
        it('should set id from object argument', function () {
            expect(log2._id).toEqual('testObject');
        });
    });
    describe('set level, enable and disable logging', function () {
        var log = logger.register('testLog');
        var logSpy;
        beforeEach(function () {
            logger.setLevel('debug');
            logSpy = sandbox.spy(logger, '_appendLogItem');
        });
        afterEach(function () {
            logSpy.restore();
        });
        it('should disable logging', function () {
            logger.disable();
            log.debug('msg');
            expect(logSpy).not.toHaveBeenCalled();
            logger.enable();
        });
        it('should enable logging', function () {
            logger.enable();
            log.debug('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should set level logging when enable logging', function () {
            logger.enable('info');
            log.debug('msg');
            expect(logSpy).not.toHaveBeenCalled();
            log.info('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should set level logging when enable logging', function () {
            logger.enable('error');
            logger.setLevel('info');
            log.debug('msg');
            expect(logSpy).not.toHaveBeenCalled();
            log.info('msg');
            expect(logSpy).toHaveBeenCalled();
        });
    });
    describe('log instance', function () {
        var log = logger.register('testLog');
        var logSpy;
        beforeEach(function () {
            logger.setLevel('debug');
            logSpy = sandbox.spy(logger, '_appendLogItem');
        });
        afterEach(function () {
            logSpy.restore();
        });
        it('should log ERROR logs when level is ERROR', function () {
            logger.setLevel('error');
            log.error('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should not log WARN logs when level is ERROR', function () {
            logger.setLevel('error');
            log.warn('msg');
            expect(logSpy).not.toHaveBeenCalled();
        });
        it('should not log INFO logs when level is ERROR', function () {
            logger.setLevel('error');
            log.info('msg');
            expect(logSpy).not.toHaveBeenCalled();
        });
        it('should not log DEBUG logs when level is ERROR', function () {
            logger.setLevel('error');
            log.debug('msg');
            expect(logSpy).not.toHaveBeenCalled();
        });
        it('should log ERROR logs when level is WARN', function () {
            logger.setLevel('warn');
            log.error('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should log WARN logs when level is WARN', function () {
            logger.setLevel('warn');
            log.warn('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should not log INFO logs when level is WARN', function () {
            logger.setLevel('warn');
            log.info('msg');
            expect(logSpy).not.toHaveBeenCalled();
        });
        it('should not log DEBUG logs when level is WARN', function () {
            logger.setLevel('warn');
            log.debug('msg');
            expect(logSpy).not.toHaveBeenCalled();
        });
        it('should log ERROR logs when level is INFO', function () {
            logger.setLevel('info');
            log.error('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should log WARN logs when level is INFO', function () {
            logger.setLevel('info');
            log.warn('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should log INFO logs when level is INFO', function () {
            logger.setLevel('info');
            log.info('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should not log DEBUG logs when level is INFO', function () {
            logger.setLevel('info');
            log.debug('msg');
            expect(logSpy).not.toHaveBeenCalled();
        });
        it('should log ERROR logs when level is DEBUG', function () {
            logger.setLevel('debug');
            log.error('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should log WARN logs when level is DEBUG', function () {
            logger.setLevel('debug');
            log.warn('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should log INFO logs when level is DEBUG', function () {
            logger.setLevel('debug');
            log.info('msg');
            expect(logSpy).toHaveBeenCalled();
        });
        it('should log DEBUG logs when level is DEBUG', function () {
            logger.setLevel('debug');
            log.debug('msg');
            expect(logSpy).toHaveBeenCalled();
        });
    });
});