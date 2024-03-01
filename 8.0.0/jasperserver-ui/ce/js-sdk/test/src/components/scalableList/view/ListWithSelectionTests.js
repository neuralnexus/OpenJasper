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

import $ from 'jquery';
import _ from 'underscore';
import ListWithSelection from 'src/components/scalableList/view/ListWithSelection';
import itemsTemplate from 'src/components/scalableList/templates/itemsTemplate.htm';

var list;
var listGetDataFactory = function (options) {
    var MAX_TOTAL = 8500;
    var total = MAX_TOTAL;
    var getDataSegment = function (first, last) {
        last = Math.min(last + 1, total);
        var result = [];
        for (var i = first + 1; i <= last; i++) {
            var val = '' + i;
            result.push({
                label: val,
                value: val
            });
        }
        return result;
    };
    var getData = function (options) {
        var offset = options ? options.offset || 0 : 0;
        var limit = options ? options.limit || total : total;
        var data = getDataSegment(offset, offset + limit);
        var deferred = new $.Deferred();
        deferred.resolve({
            data: data,
            total: total
        });
        return deferred.promise();
    };
    total = options && options.total || MAX_TOTAL;
    return getData;
};
describe('ListWithSelection', function () {
    beforeEach(function () {
    });
    afterEach(function () {
        list && list.remove();
        $('#viewPortPlaceHolder').remove();
    });
    var defaultConstructorParameters = () => ({
        el: $('<div id="viewPortPlaceHolder" class="fakeClass" style="width: 100px; height: 250px; overflow-x: hidden; overflow-y: auto;"></div>'),
        itemsTemplate: itemsTemplate,
        scrollTimeout: -1,
        getData: listGetDataFactory({ total: 1000 }),
        lazy: true
    });
    it('should select value passed as a selection hash in constructor', function () {
        list = new ListWithSelection(_.extend({
            value: {
                0: '1',
                999: '1000'
            }
        }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(1000);
        expect(selection[0]).toEqual('1');
        expect(selection[999]).toEqual('1000');
    });
    it('should select value passed as a string in constructor', function () {
        list = new ListWithSelection(_.extend({ value: '1' }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(1);
        expect(selection[0]).toEqual('1');
    });
    it('should select value passed as an array in constructor', function () {
        list = new ListWithSelection(_.extend({ value: ['2'] }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(2);
        expect(selection[1]).toEqual('2');
    });
    it('should select value passed as a string', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        list.setValue('2');
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(2);
        expect(selection[1]).toEqual('2');
    });
    it('should select value passed as an array', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        list.setValue(['2']);
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(2);
        expect(selection[1]).toEqual('2');
    });
    it('should select value passed as an object hash', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        list.setValue({ 1: '2' });
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(2);
        expect(selection[1]).toEqual('2');
    });
    it('selectAll should select all values', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        list.selectAll();
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(1000);
        expect(selection[0]).toEqual('1');
        expect(selection[50]).toEqual('51');
        expect(selection[999]).toEqual('1000');
    });
    it('selectNone should clear selection', function () {
        list = new ListWithSelection(_.extend({
            value: {
                0: '1',
                999: '1000'
            }
        }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        list.selectNone();
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(0);
    });
    it('invertSelection should invert selection', function () {
        list = new ListWithSelection(_.extend({
            value: {
                0: '1',
                999: '1000'
            }
        }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        list.invertSelection();
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection[0]).toBeUndefined();
        expect(selection[999]).toBeUndefined();
        expect(selection[50]).toEqual('51');
        expect(selection[1]).toEqual('2');
    });
    it('click on item should select it', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        var firstUl = list.$el.find('li:eq(0)');
        var event = $.Event('mousedown');
        event.which = 1;
        firstUl.trigger(event);
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(1);
        expect(selection[0]).toEqual('1');
        expect(firstUl.hasClass('jr-isSelected')).toBeTruthy();
    });
    it('disabled component should have disabled class', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        expect(list.$el.hasClass('disabled')).toBeFalsy();
        list.setDisabled(true);
        expect(list.$el.hasClass('disabled')).toBeTruthy();
    });
    it('click on item of disabled component should not select it', function () {
        list = new ListWithSelection(_.extend({
            value: {
                0: '1',
                999: '1000'
            }
        }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        list.setDisabled(true);
        var firstUl = list.$el.find('li:eq(1)');
        var event = $.Event('mousedown');
        event.which = 1;
        firstUl.trigger(event);
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection[0]).toEqual('1');
        expect(selection[999]).toEqual('1000');
        expect(firstUl.hasClass('jr-isSelected')).toBeFalsy();
    });
    it('click on selected item with CTRL key to deselect all other items and remain that item selected', function () {
        list = new ListWithSelection(_.extend({
            value: {
                0: '1',
                1: '2'
            }
        }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        var firstUl = list.$el.find('li:eq(0)');
        var event = $.Event('mousedown');
        event.which = 1;
        event.ctrlKey = true;
        firstUl.trigger(event);
        var selection = _.compact(list.getValue());
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(1);
        expect(selection[0]).toEqual('1');
        expect(firstUl.hasClass('jr-isSelected')).toBeTruthy();
        expect(list.$el.find('li:eq(1)').hasClass('jr-isSelected')).toBeFalsy();
    });
    it('click on not selected item with CTRL key to deselect all other items and select that item', function () {
        list = new ListWithSelection(_.extend({
            value: {
                0: '1',
                1: '2'
            }
        }, defaultConstructorParameters()));
        $('body').append(list.render().el);
        list.renderData();
        var firstUl = list.$el.find('li:eq(2)');
        var event = $.Event('mousedown');
        event.which = 1;
        event.ctrlKey = true;
        firstUl.trigger(event);
        var selection = _.compact(list.getValue());
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(1);
        expect(selection[0]).toEqual('3');
        expect(firstUl.hasClass('jr-isSelected')).toBeTruthy();
        expect(list.$el.find('li:eq(1)').hasClass('jr-isSelected')).toBeFalsy();
    });
    it('click one element and on other with SHIFT or COMMAND to select range', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        var firstUl = list.$el.find('li:eq(0)');
        var event = $.Event('mousedown');
        event.which = 1;
        firstUl.trigger(event);
        list.$el.scrollTop(29596);
        list.$el.trigger('scroll');
        var fifthUl = list.$el.find('li[data-index=\'999\']');
        event = $.Event('mousedown');
        event.which = 1;
        event.shiftKey = 1;
        fifthUl.trigger(event);
        var selection = list.getValue();
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(1000);
        expect(selection[999]).toEqual('1000');
        expect(list.$el.find('li:eq(0)').hasClass('jr-isSelected')).toBeTruthy();
    });
    it('click one element should toggle this element and do not clear all other selected elements', function () {
        list = new ListWithSelection(defaultConstructorParameters());
        $('body').append(list.render().el);
        list.renderData();
        var firstUl = list.$el.find('li:eq(0)');
        var event = $.Event('mousedown');
        event.which = 1;
        firstUl.trigger(event);
        var fifthUl = list.$el.find('li:eq(50)');
        event = $.Event('mousedown');
        event.which = 1;
        event.shiftKey = 1;
        fifthUl.trigger(event);
        var middleUl = list.$el.find('li:eq(10)');
        event = $.Event('mousedown');
        event.which = 1;
        middleUl.trigger(event);
        var selection = _.compact(list.getValue());
        expect(selection).toBeDefined();
        expect(selection.length).toEqual(50);
        expect(selection[0]).toEqual('1');
        expect(selection[49]).toEqual('51');
        expect(selection[10]).toEqual('12');
        expect(list.$el.find('li:eq(10)').hasClass('jr-isSelected')).toBeFalsy();
        expect(list.$el.find('li:eq(0)').hasClass('jr-isSelected')).toBeTruthy();
        expect(list.$el.find('li:eq(50)').hasClass('jr-isSelected')).toBeTruthy();
    });
    it('set empty value also should fire selection:change event', function () {
        var selectionChanged = false;
        list = new ListWithSelection(defaultConstructorParameters());
        list.once('selection:change', function () {
            selectionChanged = true;
        });
        list.setValue(undefined);
        expect(selectionChanged).toBeTruthy();
        expect(list.getValue().length).toEqual(0);
    });
});