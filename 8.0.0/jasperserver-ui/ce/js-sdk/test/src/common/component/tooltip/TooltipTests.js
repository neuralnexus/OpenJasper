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
import Backbone from 'backbone';
import Tooltip from 'src/common/component/tooltip/Tooltip';
describe('Tooltip component', function () {
    var tooltip, container = $('<div><a data-tooltip="true">test</a></div>'), target = container.find('a');
    beforeEach(function () {
        tooltip = new Tooltip({
            attachTo: container,
            contentTemplate: '<div><a data-tooltip="true">{{- model.test }}</a></div>'
        });
    });
    afterEach(function () {
        tooltip && tooltip.remove();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof Tooltip).toBe('function');
        expect(Tooltip.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should throw exception if required options are not defined', function () {
        expect(function () {
            new Tooltip();
        }).toThrow(new Error('Tooltip should be attached to an element'));
    });
    it('should attach mouseover/mouseout event handlers to element', function () {
        var events = $._data(tooltip.$attachTo[0], 'events');
        expect(events).toBeDefined();
        expect(events.mousemove).toBeDefined();
        expect(events.mouseout).toBeDefined();
    });
    it('should have static functions', function () {
        expect(Tooltip.attachTo).toBeDefined();
        expect(Tooltip.detachFrom).toBeDefined();
    });
    it('should have public functions', function () {
        expect(tooltip.show).toBeDefined();
        expect(tooltip.hide).toBeDefined();
        expect(tooltip.remove).toBeDefined();
    });
    it('should create tooltip element on show', function () {
        tooltip._event = {
            pageY: 0,
            pageX: 0,
            currentTarget: target[0]
        };
        tooltip.show({ test: 'test' }).done(function () {
            expect(tooltip.$el).toBeDefined();
            expect(tooltip.$el.offset().top).toBe(tooltip.offset.x);
            expect(tooltip.$el.offset().left).toBe(tooltip.offset.y);
            expect(tooltip.$el.find('.body').text()).toBe('test');
        });
        var clock = sinon.useFakeTimers();
        clock.tick(1000);
        clock.restore();
    });
    it('should have element with complex structure', function () {
        tooltip._event = {
            pageY: 0,
            pageX: 0,
            currentTarget: target[0]
        };
        tooltip.show();
        expect(tooltip.$el.hasClass('panel')).toBe(true);
        expect(tooltip.$el.hasClass('info')).toBe(true);
        expect(tooltip.$el.hasClass('tooltip')).toBe(true);
        expect(tooltip.$el.find('.content').length).toBe(1);
    });
});