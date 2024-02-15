/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: inesterenko
 * @version: $Id: compare.jasmine.sinon.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jasmine-sinon"],function () {

    describe("Make Jasmine and Sinon matchers work together", function () {

        var counter1, obj1;

        beforeEach(function () {
            counter1 = 0;

            obj1 = {
                start:function () {
                    counter1++;
                },

                stop:function () {
                    counter1 = 0;
                }
            };
        });

        it("has to detect call on jasmine spy", function (){
            var jasmineStartSpy = spyOn(obj1, "start");
            obj1.start();
            expect(jasmineStartSpy).toHaveBeenCalled();
            expect(counter1).toEqual(0);
        });

        it("has to detect that no calls on jasmine spy", function (){
            var jasmineStopSpy = spyOn(obj1, "stop");
            expect(jasmineStopSpy).not.toHaveBeenCalled();
        });

        it("has to detect call on sinon spy", function (){
            var sinonStartSpy = sinon.spy(obj1, "start");
            obj1.start();
            expect(sinonStartSpy).toHaveBeenCalled();
            expect(counter1).toEqual(1);
        });

        it("has to detect that no calls on sinon spy", function (){
            var sinonStopSpy = sinon.spy(obj1, "stop");
            expect(sinonStopSpy).not.toHaveBeenCalled();
        });

    });

    return jQuery;
});