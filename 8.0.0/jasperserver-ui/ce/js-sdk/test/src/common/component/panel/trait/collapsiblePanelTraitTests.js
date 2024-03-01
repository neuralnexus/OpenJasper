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
import collapsiblePanelTrait from 'src/common/component/panel/trait/collapsiblePanelTrait';
import Backbone from 'backbone';
import $ from 'jquery';
describe('collapsiblePanelTrait', function () {
    it('should assign default collapserClass, collapserSelector and collapsiblePanelClass in \'onConstructor\' method', function () {
        var obj = {};
        collapsiblePanelTrait.onConstructor.call(obj, {});
        expect(obj.collapserClass).toBeDefined();
        expect(obj.collapserSelector).toBeDefined();
        expect(obj.collapsiblePanelClass).toBeDefined();
    });
    it('should assign passed through options collapserClass, collapserSelector and collapsiblePanelClass in \'onConstructor\' method', function () {
        var obj = {};
        collapsiblePanelTrait.onConstructor.call(obj, {
            collapserClass: 'collapser',
            collapserSelector: '.collapser',
            collapsiblePanelClass: 'panel'
        });
        expect(obj.collapserClass).toBe('collapser');
        expect(obj.collapserSelector).toBe('.collapser');
        expect(obj.collapsiblePanelClass).toBe('panel');
    });
    it('should stop listening to \'mousedown\' event on $collapser in \'beforeSetElement\' method', function () {
        var obj = {};
        obj.$collapser = $('<div></div>');
        var offSpy = sinon.spy(obj.$collapser, 'off');
        collapsiblePanelTrait.beforeSetElement.call(obj, {});
        expect(offSpy).toHaveBeenCalledWith('mousedown');
        offSpy.restore();
    });
    it('should stop listening to \'mousedown\' event on $collapser in \'onRemove\' method', function () {
        var obj = {};
        obj.$collapser = $('<div></div>');
        var offSpy = sinon.spy(obj.$collapser, 'off');
        collapsiblePanelTrait.onRemove.call(obj, {});
        expect(offSpy).toHaveBeenCalledWith('mousedown');
        offSpy.restore();
    });
    it('should add class to element, create $collapser and attach \'mousedown\' event in \'afterSetElement\' method', function () {
        var obj = new Backbone.View({
            el: function () {
                return $('<div><div class=\'header\'></div></div>');
            }
        });
        obj.collapsiblePanelClass = 'panel';
        obj.collapserClass = 'collapser';
        obj.collapserSelector = '.collapser';
        collapsiblePanelTrait.afterSetElement.call(obj, {});
        expect(obj.$el.hasClass(obj.collapsiblePanelClass)).toBe(true);
        expect(obj.$collapser).toBeDefined();
        expect(obj.$collapser.length).toBe(1);
        expect(obj.$collapser.hasClass(obj.collapserClass)).toBeDefined();
        expect(obj.$('> .header > .' + obj.collapserClass).length).toBe(1);
        var events = $._data(obj.$collapser[0], 'events');
        expect(events).toBeDefined();
        expect(events.mousedown).toBeDefined();
        obj.remove();
    });
    it('should create $collapser from existing element by selector in \'afterSetElement\' method', function () {
        var obj = new Backbone.View({
            el: function () {
                return $('<div><div class=\'.myCollapserClass\'></div></div>');
            }
        });
        obj.collapserSelector = '.myCollapserClass';
        collapsiblePanelTrait.afterSetElement.call(obj, {});
        expect(obj.$collapser).toBeDefined();
        expect(obj.$collapser.hasClass('myCollapserClass')).toBeDefined();
        expect(obj.$collapser.length).toBe(1);
        obj.remove();
    });
    it('should accept optional \'onCollapseControlPressed\' callback function and call it instead of default when collapse control is pressed', function () {
        var obj = new Backbone.View({
            el: function () {
                return $('<div><div class=\'header\'></div></div>');
            }
        });
        collapsiblePanelTrait.onConstructor.call(obj, {
            collapserClass: 'collapser',
            collapserSelector: '.collapser',
            collapsiblePanelClass: 'panel',
            onCollapseControlPressed: function () {
            }
        });
        var spy = sinon.spy(obj, 'onCollapseControlPressed');
        collapsiblePanelTrait.afterSetElement.call(obj, {});
        obj.$collapser.trigger('mousedown');
        expect(spy).toHaveBeenCalled();
        spy.restore();
    });
});