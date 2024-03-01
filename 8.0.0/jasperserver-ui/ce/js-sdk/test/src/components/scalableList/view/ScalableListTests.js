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
import $ from 'jquery';
import ScalableList from 'src/components/scalableList/view/ScalableList';
import ScalableListModel from 'src/components/scalableList/model/ScalableListModel';
import itemsTemplate from 'src/components/scalableList/templates/itemsTemplate.htm';
var clock;
var clearTimeoutSpy;
var setTimeoutSpy;
var list;
var getDataSpy;
var model;
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
describe('ScalableList', function () {
    beforeEach(function () {
        clock = sinon.useFakeTimers();
        clearTimeoutSpy = sinon.spy(window, 'clearTimeout');
        setTimeoutSpy = sinon.spy(window, 'setTimeout');
        getDataSpy = sinon.spy(listGetDataFactory());
        model = new ScalableListModel({ getData: getDataSpy });
        list = new ScalableList({
            scrollTimeout: 0,
            model: model,
            lazy: true
        });
    });
    afterEach(function () {
        clock.restore();
        clearTimeoutSpy.restore();
        setTimeoutSpy.restore();
        list.remove();
    });
    it('should rerender elements on scroll', function () {
        list.remove();
        $('#viewPortPlaceHolder').remove();
        list = new ScalableList({
            el: $('<div id="viewPortPlaceHolder" class="fakeClass" style="width: 100px; height: 250px; overflow-y: auto"></div>'),
            scrollTimeout: 0,
            itemsTemplate: itemsTemplate,
            getData: getDataSpy,
            lazy: true
        });
        $('body').append(list.render().el);
        list.renderData();
        list.$el.scrollTop(16999750);
        list.$el.trigger('scroll');
        var attr = parseInt(list.$el.find('li:last').attr('data-index'), 10);
        expect(attr > 8000).toBeTruthy();
        $('#viewPortPlaceHolder').remove();
    });
    it('should keep scroll position on fetch if keepPosition parameter provided', function () {
        var resetSpy = sinon.spy(list, 'reset'), modelFetchSpy = sinon.spy(model, 'fetch'), renderDataStub = sinon.stub(list, 'renderData'), scrollTopStub = sinon.stub(list.$el, 'scrollTop'), callbackStub = sinon.stub();
        list.fetch(callbackStub, { keepPosition: true });
        sinon.assert.notCalled(resetSpy);
        sinon.assert.calledWith(modelFetchSpy, { force: true });
        sinon.assert.calledOnce(scrollTopStub);
        sinon.assert.calledOnce(callbackStub);
        resetSpy.restore();
        modelFetchSpy.restore();
        renderDataStub.restore();
        scrollTopStub.restore();
    });
    it('should trigger listRenderError on fetch failed', function () {
        var triggerSpy = sinon.stub(list, 'trigger');
        model.trigger('fetchFailed', 'responseStatus', 'error');
        expect(triggerSpy).toHaveBeenCalledWith('listRenderError', 'responseStatus', 'error');
        triggerSpy.restore();
    });
});