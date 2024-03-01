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

import LoggerManager from 'src/common/logging/LoggerManager';
import loggingLevels from 'src/common/enum/loggingLevels';
import ConsoleAppender from 'src/common/logging/appender/ConsoleAppender';
describe('LoggerManager', function () {
    it('has exists', function () {
        expect(LoggerManager).toBeDefined();
    });
    it('should create logger instance', function () {
        var logger = new LoggerManager({});
        expect(logger instanceof LoggerManager).toBeTruthy();
    });
    describe('create default logger instance', function () {
        var logger;
        beforeEach(function () {
            logger = new LoggerManager({});
        });
        it('should be disabled', function () {
            expect(logger.get('enabled')).toBeFalsy();
        });
        it('should create Level instance with error logging level', function () {
            expect(logger.get('level')).toBe('error');
        });
        it('should create empty object for appenders', function () {
            expect(logger.get('appenders')).toEqual({});
        });
        it('should create empty object for loggers', function () {
            expect(logger.get('loggers')).toEqual({});
        });
    });
    describe('process options', function () {
        it('should set enabled property to true', function () {
            var options = { enabled: true };
            var logger = new LoggerManager(options);
            expect(logger.get('enabled')).toBeTruthy();
        });
        it('should set enabled property to false', function () {
            var options = { enabled: false };
            var logger = new LoggerManager(options);
            expect(logger.get('enabled')).toBeFalsy();
        });
        for (var i in loggingLevels) {
            if (loggingLevels.hasOwnProperty(i)) {
                (function (levelName) {
                    it('should set level to ' + levelName, function () {
                        var options = { level: levelName.toLowerCase() };
                        var logger = new LoggerManager(options);
                        expect(logger.get('level').toLowerCase()).toBe(levelName.toLowerCase());
                    });
                }(i));
            }
        }
        it('should add console appender instance', function () {
            var options = { appenders: ['console'] };
            var logger = new LoggerManager(options);
            expect(logger.get('appenderInstances').console instanceof ConsoleAppender).toBeTruthy();
        });
    });
});