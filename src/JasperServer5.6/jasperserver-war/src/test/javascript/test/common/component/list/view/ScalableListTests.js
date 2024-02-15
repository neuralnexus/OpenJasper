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
 * @version: $Id: ScalableListTests.js 47864 2014-08-06 13:47:07Z sergey.prilukin $
 */

define([
    "jquery",
    "underscore",
    "core.layout",
    "common/component/list/view/ScalableList",
    "text!common/component/list/templates/itemsTemplate.htm"],

    function ($, _, layoutModule, ScalableList, itemsTemplate) {

        var clock, clearTimeoutSpy, setTimeoutSpy, list;


        var listGetDataFactory = function(options) {
            var MAX_TOTAL = 8500;

            var total = MAX_TOTAL;

            var getDataSegment = function(first, last) {
                last = Math.min(last + 1, total);

                var result = [];

                for (var i = first + 1; i <= last; i++) {
                    var val = "" + i;
                    result.push({label: val, value: val})
                }

                return result;
            };

            var getData = function(options) {
                var offset = options ? options.offset || 0 : 0;
                var limit = options ? options.limit || total : total;

                var data = getDataSegment(offset, offset + limit);

                var deferred = new $.Deferred();
                deferred.resolve({data: data, total: total});

                return deferred.promise();
            };

            total = (options && options.total) || MAX_TOTAL;

            return getData;
        };

        describe("ScalableList", function () {

            beforeEach(function(){
                clock = sinon.useFakeTimers();
                clearTimeoutSpy = sinon.spy(window, "clearTimeout");
                setTimeoutSpy = sinon.spy(window, "setTimeout");

                list = new ScalableList({
                    el: $("<div id=\"viewPortPlaceHolder\" class=\"fakeClass\" style=\"width: 100px; height: 250px; overflow-y: auto\"></div>"),
                    //chunksTemplate: chunksTemplate,
                    itemsTemplate: itemsTemplate,
                    getData: listGetDataFactory(),
                    lazy: true
                });

                $("body").append(list.render().el);
                list.renderData();
            });

            afterEach(function(){
                clock.restore();
                clearTimeoutSpy.restore();
                setTimeoutSpy.restore();

                list.remove();
                $("#viewPortPlaceHolder").remove();
            });

            it("should rerender elements on scroll", function(){
                list.$el.scrollTop(16999750);
                list.$el.trigger("scroll");

                clock.tick(60);

                var attr = parseInt(list.$el.find("li:last").attr("data-index"), 10);
                expect(attr > 8000).toBeTruthy();
            });
        });
});
