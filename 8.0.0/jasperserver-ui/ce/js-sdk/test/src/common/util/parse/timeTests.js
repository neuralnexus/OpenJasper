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

import timeUtils from 'src/common/util/parse/time';
import Time from 'src/common/util/datetime/Time';
describe('Time Utils', function () {
    it('should have functions', function () {
        expect(timeUtils).toBeDefined();
        expect(timeUtils.isRelativeTime).toBeDefined();
        expect(typeof timeUtils.isRelativeTime).toEqual('function');
        expect(timeUtils.isTime).toBeDefined();
        expect(typeof timeUtils.isTime).toEqual('function');
        expect(timeUtils.isIso8601Time).toBeDefined();
        expect(typeof timeUtils.isIso8601Time).toEqual('function');
        expect(timeUtils.compareTimes).toBeDefined();
        expect(typeof timeUtils.compareTimes).toEqual('function');
        expect(timeUtils.timeToIso8061Time).toBeDefined();
        expect(typeof timeUtils.timeToIso8061Time).toEqual('function');
        expect(timeUtils.iso8601TimeToTimeObject).toBeDefined();
        expect(typeof timeUtils.iso8601TimeToTimeObject).toEqual('function');
    });
    describe('isRelativeTime', function () {
        it('should return true in case of correct relative time', function () {
            expect(timeUtils.isRelativeTime('HOUR')).toEqual(true);
            expect(timeUtils.isRelativeTime('HOUR+1')).toEqual(true);
            expect(timeUtils.isRelativeTime('HOUR-1')).toEqual(true);
            expect(timeUtils.isRelativeTime('HOUR+0')).toEqual(true);
            expect(timeUtils.isRelativeTime('MINUTE')).toEqual(true);
            expect(timeUtils.isRelativeTime('MINUTE+1')).toEqual(true);
            expect(timeUtils.isRelativeTime('MINUTE-1')).toEqual(true);
            expect(timeUtils.isRelativeTime('MINUTE+0')).toEqual(true);
        });
        it('should return false in case of non-string input value', function () {
            expect(timeUtils.isRelativeTime({})).toEqual(false);
        });
        it('should return false in case of incorrect relative time', function () {
            expect(timeUtils.isRelativeTime('HO UR')).toEqual(false);
            expect(timeUtils.isRelativeTime('HOUR1+1')).toEqual(false);
            expect(timeUtils.isRelativeTime('HOUR -1')).toEqual(false);
            expect(timeUtils.isRelativeTime('HOUR3')).toEqual(false);
            expect(timeUtils.isRelativeTime('MINUTsE')).toEqual(false);
            expect(timeUtils.isRelativeTime('MINUTE1+1')).toEqual(false);
            expect(timeUtils.isRelativeTime('MINUTE--1')).toEqual(false);
            expect(timeUtils.isRelativeTime('MINUTE+ 1')).toEqual(false);
        });
    });
    describe('isTime', function () {
        it('should return false in case of non-string input', function () {
            expect(timeUtils.isTime({})).toEqual(false);
        });
        it('should return true in case of valid time string', function () {
            expect(timeUtils.isTime('11:23:48')).toEqual(true);
            expect(timeUtils.isTime('23:56:01')).toEqual(true);
        });
        it('should return false in case of invalid time string', function () {
            expect(timeUtils.isTime('11:83:48')).toEqual(false);
            expect(timeUtils.isTime('26:56:01')).toEqual(false);
            expect(timeUtils.isTime('11:56:91')).toEqual(false);
        });
        it('should return false in case of invalidly formatted time string', function () {
            expect(timeUtils.isTime('11-83-48')).toEqual(false);
            expect(timeUtils.isTime('11:83')).toEqual(false);
        });
        it('should allow passing time format', function () {
            expect(timeUtils.isTime('11-83-48', 'hh-mm-ss')).toEqual(false);
            expect(timeUtils.isTime('118348', 'hhmmss')).toEqual(false);
            expect(timeUtils.isTime('11:83', 'hh:mm')).toEqual(false);
        });
    });
    describe('isIso8601Time', function () {
        it('should return true in case of valid ISO 8601 time string', function () {
            expect(timeUtils.isTime('11:23:48')).toEqual(true);
            expect(timeUtils.isTime('23:56:01')).toEqual(true);
        });
        it('should return false in case of invalid ISO 8601 time string', function () {
            expect(timeUtils.isTime('11-56-31')).toEqual(false);
            expect(timeUtils.isTime('115631')).toEqual(false);
        });
    });
    describe('compareTimes', function () {
        it('should return undefined if one of the times is corrupted', function () {
            expect(timeUtils.compareTimes('11:53:48', '11:8348')).toEqual(undefined);
        });
        it('should return 0 if times are equal', function () {
            expect(timeUtils.compareTimes('11:53:48', '11:53:48')).toEqual(0);
        });
        it('should return -1 if first time is before second', function () {
            expect(timeUtils.compareTimes('11:53:48', '11:53:49')).toEqual(-1);
            expect(timeUtils.compareTimes('11:53:48', '11:54:48')).toEqual(-1);
            expect(timeUtils.compareTimes('11:53:48', '12:53:48')).toEqual(-1);
        });
        it('should return 1 if first time is after second', function () {
            expect(timeUtils.compareTimes('11:53:49', '11:53:48')).toEqual(1);
            expect(timeUtils.compareTimes('11:54:48', '11:53:48')).toEqual(1);
            expect(timeUtils.compareTimes('12:53:48', '11:53:48')).toEqual(1);
        });
    });
    describe('timeToIso8061Time', function () {
        it('should convert time parts to ISO 8601 time format', function () {
            expect(timeUtils.timeToIso8061Time(4, 5, 3)).toBe('04:05:03');
        });
        it('should return undefined in case of invalid time', function () {
            expect(timeUtils.timeToIso8061Time(24, 20, 3)).toBeUndefined();
        });
    });
    describe('iso8601TimeToTimeObject', function () {
        it('should return instance of Time object valid ISO 8061 time', function () {
            var time = timeUtils.iso8601TimeToTimeObject('13:24:03');
            expect(time instanceof Time).toBeTruthy();
            expect(time.hours).toBe(13);
            expect(time.minutes).toBe(24);
            expect(time.seconds).toBe(3);
        });
    });
});