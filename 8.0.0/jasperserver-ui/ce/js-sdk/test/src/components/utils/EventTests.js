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

import Event from 'src/components/utils/Event';
describe('Event', function () {
    describe('Setters & Getters', function () {
        it('should throw an error on non-string name', function () {
            expect(function () {
                new Event();
            }).toThrow(new TypeError('\'name\' must be a \'string\''));
        });
        it('should throw an error on non-empty string name', function () {
            expect(function () {
                new Event({ name: '' });
            }).toThrow(new Error('\'name\' should\'t be an empty string'));
        });
        it('should throw an error on non-object \'data\'', function () {
            expect(function () {
                new Event({
                    name: 'blabla',
                    data: 4
                });
            }).toThrow(new Error('\'data\' must be an \'object\''));
        });
        it('should return as \'name\' as \'data\'', function () {
            var options = {
                    name: 'test',
                    data: { value: 'test' }
                }, event = new Event(options);
            expect(event.name).toEqual(options.name);
            expect(event.data).toEqual(options.data);
        });
    });
    describe('Prevent Defaults', function () {
        beforeEach(function () {
            this.event = new Event({ name: 'test' });
        });
        it('should be \'false\' by default', function () {
            expect(this.event.isDefaultPrevented()).toBeFalsy();
        });
        it('should change to \'true\'', function () {
            this.event.preventDefault();
            expect(this.event.isDefaultPrevented()).toBeTruthy();
        });
        it('should change to \'true\', no mater how many times it called', function () {
            this.event.preventDefault();
            this.event.preventDefault();
            expect(this.event.isDefaultPrevented()).toBeTruthy();
        });
    });
});