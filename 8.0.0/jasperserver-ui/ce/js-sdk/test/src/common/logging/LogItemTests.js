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

import LogItem from 'src/common/logging/LogItem';
import Level from 'src/common/logging/Level';
describe('LogItem', function () {
    it('has exists', function () {
        expect(LogItem).toBeDefined();
    });
    it('should return logItem instance', function () {
        var options = {
            id: 'id',
            level: Level.getLevel('warn'),
            time: new Date(),
            file: 'testFile.js',
            line: 10,
            args: ['msg']
        };
        var logItem = new LogItem(options);
        expect(logItem instanceof LogItem).toBeTruthy();
        expect(logItem.id).toEqual(options.id);
        expect(logItem.level).toEqual(options.level);
        expect(logItem.time).toEqual(options.time);
        expect(logItem.file).toEqual(options.file);
        expect(logItem.line).toEqual(options.line);
        expect(logItem.args).toEqual(options.args);
    });
    it('should return array of properties', function () {
        var options = {
            id: 'id',
            level: Level.getLevel('warn'),
            time: new Date(),
            file: 'testFile.js',
            line: 10,
            args: ['msg']
        };
        var logItem = new LogItem(options);
        var arr = logItem.toArray();
        expect(arr instanceof Array).toBeTruthy();
        expect(arr[1]).toEqual('[' + options.id + ']');
        expect(arr[2]).toEqual('[' + options.file + ':' + options.line + ']');
        expect(arr[3]).toEqual('[' + options.level.toString() + '] -');
        expect(arr.slice(4)).toEqual(options.args);
    });
});