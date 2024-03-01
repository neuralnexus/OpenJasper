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
import ConsoleAppender from 'src/common/logging/appender/ConsoleAppender';
import LogItem from 'src/common/logging/LogItem';
import Level from 'src/common/logging/Level';
describe('ConsoleAppender', function () {
    it('has exists', function () {
        expect(ConsoleAppender).toBeDefined();
    });
    it('should create stub for console object', function () {
        var consoleAppender = new ConsoleAppender();
        expect(typeof consoleAppender.console).toBe('object');
    });
    it('should create instance of console appender', function () {
        var consoleAppender = new ConsoleAppender();
        expect(consoleAppender instanceof ConsoleAppender).toBeTruthy();
    });
    describe('ConsoleAppender instance', function () {
        var consoleAppender = new ConsoleAppender(), logItem = new LogItem({
            id: 'id',
            level: Level.getLevel('info'),
            time: new Date(),
            file: 'testFile.js',
            line: 10,
            args: ['msg']
        });
        it('has method write', function () {
            expect(typeof consoleAppender.write).toEqual('function');
        });
        it('should call console.debug', function () {
            var consoleDebugSpy = sinon.spy(ConsoleAppender.prototype.console, 'debug');
            logItem.level = Level.getLevel('debug');
            consoleAppender.write(logItem);
            expect(consoleDebugSpy).toHaveBeenCalled();
        });
        it('should call console.info', function () {
            var consoleInfoSpy = sinon.spy(ConsoleAppender.prototype.console, 'info');
            logItem.level = Level.getLevel('info');
            consoleAppender.write(logItem);
            expect(consoleInfoSpy).toHaveBeenCalled();
        });
        it('should call console.warn', function () {
            var consoleWarnSpy = sinon.spy(ConsoleAppender.prototype.console, 'warn');
            logItem.level = Level.getLevel('warn');
            consoleAppender.write(logItem);
            expect(consoleWarnSpy).toHaveBeenCalled();
        });
        it('should call console.error', function () {
            var consoleErrorSpy = sinon.spy(ConsoleAppender.prototype.console, 'error');
            logItem.level = Level.getLevel('error');
            consoleAppender.write(logItem);
            expect(consoleErrorSpy).toHaveBeenCalled();
        });
    });
});