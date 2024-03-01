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

import Level from 'src/common/logging/Level';
describe('Level', function () {
    it('has exists', function () {
        expect(Level).toBeDefined();
    });
    it('should return name of level', function () {
        var levelName = Level.getLevel('info').toString();
        expect(levelName).toEqual('INFO');
    });
    it('should return debug level instance', function () {
        var level = Level.getLevel('debug');
        expect(level.level).toEqual(100);
        expect(level.name).toEqual('DEBUG');
    });
    it('should return info level instance', function () {
        var level = Level.getLevel('info');
        expect(level.level).toEqual(200);
        expect(level.name).toEqual('INFO');
    });
    it('should return warn level instance', function () {
        var level = Level.getLevel('warn');
        expect(level.level).toEqual(300);
        expect(level.name).toEqual('WARN');
    });
    it('should return error level instance', function () {
        var level = Level.getLevel('error');
        expect(level.level).toEqual(400);
        expect(level.name).toEqual('ERROR');
    });
    describe('comparison levels', function () {
        var levels = {
            debug: Level.getLevel('debug'),
            info: Level.getLevel('info'),
            warn: Level.getLevel('warn'),
            error: Level.getLevel('error')
        };
        it('should error level be greater than all', function () {
            expect(levels.error.isGreaterOrEqual(levels.debug)).toBeTruthy();
            expect(levels.error.isGreaterOrEqual(levels.info)).toBeTruthy();
            expect(levels.error.isGreaterOrEqual(levels.warn)).toBeTruthy();
            expect(levels.error.isGreaterOrEqual(levels.error)).toBeTruthy();
        });
        it('should warn level be greater than all except error', function () {
            expect(levels.warn.isGreaterOrEqual(levels.debug)).toBeTruthy();
            expect(levels.warn.isGreaterOrEqual(levels.info)).toBeTruthy();
            expect(levels.warn.isGreaterOrEqual(levels.warn)).toBeTruthy();
            expect(levels.warn.isGreaterOrEqual(levels.error)).toBeFalsy();
        });
        it('should info level be greater than debug and info, but lesser than warn and error', function () {
            expect(levels.info.isGreaterOrEqual(levels.debug)).toBeTruthy();
            expect(levels.info.isGreaterOrEqual(levels.info)).toBeTruthy();
            expect(levels.info.isGreaterOrEqual(levels.warn)).toBeFalsy();
            expect(levels.info.isGreaterOrEqual(levels.error)).toBeFalsy();
        });
        it('should error level be lesser than exept debug', function () {
            expect(levels.debug.isGreaterOrEqual(levels.debug)).toBeTruthy();
            expect(levels.debug.isGreaterOrEqual(levels.info)).toBeFalsy();
            expect(levels.debug.isGreaterOrEqual(levels.warn)).toBeFalsy();
            expect(levels.debug.isGreaterOrEqual(levels.error)).toBeFalsy();
        });
    });
});