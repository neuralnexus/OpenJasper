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
import Backbone from 'backbone';
import $ from 'jquery';
import OptionView from 'src/common/component/base/OptionView';
describe('OptionView', function () {
    var optionView;
    beforeEach(function () {
        optionView = new OptionView({
            model: new Backbone.Model({ label: 'test' }),
            template: '<div>{{- label }}</div>',
            disabledClass: 'disabled',
            hiddenClass: 'hidden',
            toggleClass: 'active'
        });
        $('body').append(optionView.$el);
    });
    afterEach(function () {
        optionView && optionView.remove();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof OptionView).toBe('function');
        expect(OptionView.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should throw exception if "template" or "model" options are not defined', function () {
        expect(function () {
            new OptionView();
        }).toThrow(new Error('Option should have defined template'));
        expect(function () {
            new OptionView({});
        }).toThrow(new Error('Option should have defined template'));
        expect(function () {
            new OptionView({ template: '<div></div>' });
        }).toThrow(new Error('Option should have associated Backbone.Model'));
        expect(function () {
            new OptionView({
                template: '<div></div>',
                model: {}
            });
        }).toThrow(new Error('Option should have associated Backbone.Model'));
    });
    it('should have \'events\' object', function () {
        expect(OptionView.prototype.events).toEqual({
            'click': 'select',
            'mouseover': 'mouseover',
            'mouseout': 'mouseout'
        });
    });
    it('should have \'disabledClass\', \'hiddenClass\' and \'toggleClass\' from options', function () {
        expect(optionView.hiddenClass).toBe('hidden');
        expect(optionView.disabledClass).toBe('disabled');
        expect(optionView.toggleClass).toBe('active');
    });
    it('should be rendered from passed template', function () {
        expect(optionView.$el.text()).toEqual('test');
    });
    it('should trigger "select" event on model in "select" method', function () {
        var modelTriggerSpy = sinon.spy(optionView.model, 'trigger'), ev = {};
        optionView.select(ev);
        sinon.assert.calledWith(modelTriggerSpy, 'select', optionView, optionView.model, ev);
        modelTriggerSpy.restore();
    });
    it('should trigger "mouseover" event on view in "mouseover" method', function () {
        var triggerSpy = sinon.spy(optionView, 'trigger'), ev = {};
        optionView.mouseover(ev);
        sinon.assert.calledWith(triggerSpy, 'mouseover', optionView, optionView.model, ev);
        triggerSpy.restore();
    });
    it('should trigger "mouseout" event on view in "mouseout" method', function () {
        var triggerSpy = sinon.spy(optionView, 'trigger'), ev = {};
        optionView.mouseout(ev);
        sinon.assert.calledWith(triggerSpy, 'mouseout', optionView, optionView.model, ev);
        triggerSpy.restore();
    });
    it('should be enabled by default', function () {
        expect(optionView.$el.is(':disabled')).toBeFalsy();
        expect(optionView.$el.hasClass('disabled')).toBeFalsy();
    });
    it('should be able to disable', function () {
        optionView.disable();
        expect(optionView.$el.attr('disabled')).toBe('disabled');
        expect(optionView.$el.hasClass('disabled')).toBeTruthy();
    });
    it('should be able to enable', function () {
        optionView.disable();
        optionView.enable();
        expect(optionView.$el.attr('disabled')).toBeUndefined();
        expect(optionView.$el.hasClass('disabled')).toBeFalsy();
    });
    it('should be able to hide', function () {
        optionView.hide();
        expect(optionView.$el.is(':visible')).toBeFalsy();
        expect(optionView.$el.hasClass('hidden')).toBeTruthy();
    });
    it('should be able to show', function () {
        optionView.hide();
        optionView.show();
        expect(optionView.$el.is(':visible')).toBeTruthy();
        expect(optionView.$el.hasClass('hidden')).toBeFalsy();
    });
    it('should have isVisible method', function () {
        optionView.$el.hide();
        expect(optionView.isVisible()).toBeFalsy();
        optionView.$el.show();
        expect(optionView.isVisible()).toBeTruthy();
    });
    it('should call \'disable\' and \'hide\' methods on initialize if model has corresponding props', function () {
        optionView.remove();
        var hideSpy = sinon.spy(OptionView.prototype, 'hide'), disableSpy = sinon.spy(OptionView.prototype, 'disable');
        optionView = new OptionView({
            model: new Backbone.Model({
                label: 'test',
                disabled: true,
                hidden: true
            }),
            template: '<div>{{- label }}</div>'
        });
        expect(hideSpy).toHaveBeenCalled();
        expect(disableSpy).toHaveBeenCalled();
        hideSpy.restore();
        disableSpy.restore();
    });
    it('should have addSelection/removeSelection method', function () {
        optionView.addSelection();
        expect(optionView.$el.hasClass('active')).toBeTruthy();
        expect(optionView.model.get('selected')).toBeTruthy();
        optionView.removeSelection();
        expect(optionView.$el.hasClass('active')).toBeFalsy();
        expect(optionView.model.get('selected')).toBeFalsy();
    });
});