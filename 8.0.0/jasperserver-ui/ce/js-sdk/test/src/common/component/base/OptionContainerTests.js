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
import _ from 'underscore';
import Container from 'src/common/component/base/OptionContainer';

describe('Options Container component', function () {
    var container, mainTemplate = '<div><div class=\'content\'><ul></ul></div></div>', optionTemplate = '<li>{{- label }}</li>';
    beforeEach(function () {
        container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a'
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate
        });
        $('body').append(container.el);
    });
    afterEach(function () {
        container && container.remove();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof Container).toBe('function');
        expect(Container.prototype instanceof Backbone.View).toBeTruthy();
    });
    describe('function validateOptions ', function () {
        it('should throw exception if required options are not defined', function () {
            expect(function () {
                new Container();
            }).toThrow(new Error('Init options must be specified'));
        });
        it('should throw exception if required options are empty object', function () {
            expect(function () {
                new Container({});
            }).toThrow(new Error('Init options must be specified'));
        });
        it('should throw exception if required options are empty array', function () {
            expect(function () {
                new Container([]);
            }).toThrow(new Error('Init options must be specified'));
        });
        it('should throw exception if required options has property \'options\' as empty array', function () {
            expect(function () {
                new Container({ options: [] });
            }).toThrow(new Error('Option views descriptors must be specified'));
        });
        it('should throw exception if required options has property \'options\' as array with element as empty object', function () {
            expect(function () {
                new Container({ options: [{}] });
            }).toThrow(new Error('Option container must have a template'));
        });
        it('should throw exception if required options has property \'options\' as array with element as empty object, ' + 'and define property\'mainTemplate\'', function () {
            expect(function () {
                new Container({
                    options: [{}],
                    mainTemplate: 'asffas'
                });
            }).toThrow(new Error('Option container must have an option template'));
        });
    });
    it('should have public functions', function () {
        expect(container.render).toBeDefined();
        expect(container.show).toBeDefined();
        expect(container.hide).toBeDefined();
        expect(container.remove).toBeDefined();
    });
    it('should define content root expression', function () {
        expect(container.contentContainer).toBe('.content > ul');
        expect(container.$contentContainer).toBeDefined();
    });
    it('should have default values for "toggle" and "toggleClass" params', function () {
        expect(container.toggle).toBeFalsy();
        expect(container.toggleClass).toBe('active');
    });
    it('should have passed values for "toggle" and "toggleClass" params', function () {
        container && container.remove();
        container = new Container({
            options: [{
                label: 'a',
                action: 'a'
            }],
            toggle: true,
            toggleClass: 'someClass',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate
        });
        expect(container.toggle).toBeTruthy();
        expect(container.toggleClass).toBe('someClass');
    });
    it('should not define content root expression if el passed and use el as content root', function () {
        container && container.remove();

        $('body').append("<div class='qwertyuiop'></div>");

        var el = $(".qwertyuiop");

        container = new Container({
            options: [{
                label: 'Save Dashboard',
                action: 'save'
            }],
            el: el[0],
            optionTemplate: optionTemplate
        });

        expect(container.contentContainer).toEqual(el[0]);
        expect(container.$contentContainer).toBeDefined();
        expect(container.$contentContainer).toEqual(container.$el);

        el.remove();
    });
    it('should be able to override content root expression', function () {
        container && container.remove();
        var contentC = 'input';
        container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a'
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: contentC
        });
        expect(container.contentContainer).toBe(contentC);
        expect(container.$contentContainer).toBeDefined();
    });
    it('should render options', function () {
        expect(container.$('li').length).toBe(3);
    });
    it('should transform options into Backbone.Collection', function () {
        expect(container.collection instanceof Backbone.Collection).toBe(true);
        expect(container.collection.length).toEqual(3);
        expect(container.collection.at(0).get('label')).toEqual('a');
        expect(container.collection.at(0).get('action')).toEqual('a');
        expect(container.collection.at(1).get('label')).toEqual('b');
        expect(container.collection.at(1).get('action')).toEqual('b');
        expect(container.collection.at(2).get('label')).toEqual('c');
        expect(container.collection.at(2).get('action')).toEqual('c');
    });
    it('should toggle class on default option if toggle is enabled', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    'default': true
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'sel',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        expect(container.getOptionView('a').$el.hasClass('sel')).toBe(true);
    });
    it('should show container', function () {
        container.show();
        expect(container.$el.is(':visible')).toBe(true);
    });
    it('should trigger \'show\' event on show', function () {
        var triggerSpy = sinon.spy(container, 'trigger');
        container.show();
        sinon.assert.calledWith(triggerSpy, 'show', container);
        triggerSpy.restore();
    });
    it('should hide container', function () {
        container.show();
        container.hide();
        expect(container.$el.is(':visible')).toBe(false);
    });
    it('should trigger \'hide\' event on hide', function () {
        container.show();
        var triggerSpy = sinon.spy(container, 'trigger');
        container.hide();
        sinon.assert.calledWith(triggerSpy, 'hide', container);
        triggerSpy.restore();
    });
    it('should trigger \'option\' event when container option is clicked by default', function () {
        container.show();
        var triggerSpy = sinon.spy(container, 'trigger');
        container.$('li:eq(0)').trigger('click');
        sinon.assert.calledWith(triggerSpy, 'option:a');
        triggerSpy.restore();
    });
    it('should trigger \'option\' event when container option is clicked by default', function () {
        container.show();
        var triggerSpy = sinon.spy(container, 'trigger');
        container.$el.trigger('mouseout');
        sinon.assert.calledWith(triggerSpy, 'container:mouseout');
        triggerSpy.restore();
    });
    it('should trigger \'option\' event when container option is clicked by default', function () {
        container.show();
        var triggerSpy = sinon.spy(container, 'trigger');
        container.$el.trigger('mouseover');
        sinon.assert.calledWith(triggerSpy, 'container:mouseover');
        triggerSpy.restore();
    });
    it('should mark selected option as active when container option is clicked', function () {
        container && container.remove();
        container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a'
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate
        });
        $('body').append(container.el);
        container.show();
        container.$('li:eq(0)').trigger('click');
        expect(container.$('li:eq(0)').hasClass('active')).toBe(true);
        expect(container.$('li:eq(1)').hasClass('active')).toBe(false);
        expect(container.$('li:eq(2)').hasClass('active')).toBe(false);
    });
    it('should trigger passed context event event when container option is clicked ', function () {
        container && container.remove();
        var context = 'button';
        container = new Container({
            options: [{
                label: 'Save Dashboard',
                action: 'save'
            }],
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contextName: context
        });
        $('body').append(container.el);
        container.show();
        var triggerSpy = sinon.spy(container, 'trigger');
        container.$('li:eq(0)').trigger('click');
        sinon.assert.calledWith(triggerSpy, context + ':save');
        triggerSpy.restore();
    });
    it('should remove subviews first when remove() is called', function () {
        var optionRemoveSpy = sinon.spy(container.options[0], 'remove');
        container.remove();
        sinon.assert.calledWith(optionRemoveSpy);
        optionRemoveSpy.restore();
    });
    it('should call base Backbone.View \'remove\' method', function () {
        var removeSpy = sinon.spy(Backbone.View.prototype, 'remove');
        container.remove();
        sinon.assert.calledWith(removeSpy);
        removeSpy.restore();
    });
    it('should disable all options on disable call', function () {
        _.each(container.options, function (option) {
            sinon.spy(option, 'disable');
        });
        container.disable();
        var spies = _.pluck(container.options, 'disable');
        _.each(spies, function (spy) {
            sinon.assert.called(spy);
        });
    });
    it('should enable all options on enable call', function () {
        container.disable();
        _.each(container.options, function (option) {
            sinon.spy(option, 'enable');
        });
        container.enable();
        var spies = _.pluck(container.options, 'enable');
        _.each(spies, function (spy) {
            sinon.assert.called(spy);
        });
    });
    it('should disable certain id', function () {
        var res = {};
        _.each(container.options, function (option) {
            option.disable = function () {
                res[this.model.get('action')] = true;
            };
        });
        container.disable('a');
        expect(res.a).toBeTruthy();
        expect(res.b).toBeFalsy();
        expect(res.c).toBeFalsy();
    });
    it('should disable certain ids (2)', function () {
        var res = {};
        _.each(container.options, function (option) {
            option.disable = function () {
                res[this.model.get('action')] = true;
            };
        });
        container.disable('a', 'b');
        expect(res.a).toBeTruthy();
        expect(res.b).toBeTruthy();
        expect(res.c).toBeFalsy();
    });
    it('should enable certain id', function () {
        container.disable();
        var res = {};
        _.each(container.options, function (option) {
            option.enable = function () {
                res[this.model.get('action')] = true;
            };
        });
        container.enable('b');
        expect(res.a).toBeFalsy();
        expect(res.b).toBeTruthy();
        expect(res.c).toBeFalsy();
    });
    it('should enable certain ids (2)', function () {
        container.disable();
        var res = {};
        _.each(container.options, function (option) {
            option.enable = function () {
                res[this.model.get('action')] = true;
            };
        });
        container.enable('c', 'a');
        expect(res.a).toBeTruthy();
        expect(res.b).toBeFalsy();
        expect(res.c).toBeTruthy();
    });
    it('should return all selected option on getSelection', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    'default': true
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        expect(container.getSelection()).toEqual(['a']);
        container.select('b', 'a');
        expect(container.getSelection()).toEqual([
            'a',
            'b'
        ]);
        container.select();
        expect(container.getSelection()).toEqual([
            'a',
            'b',
            'c'
        ]);
        container.deselect('b');
        expect(container.getSelection()).toEqual([
            'a',
            'c'
        ]);
        container.deselect();
        expect(container.getSelection()).toEqual([]);
    });
    it('should select options and reset to default', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    'default': true
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        container.select('b', 'a');
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
        container.select();
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(true);
    });
    it('should deselect options and reset to default', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    'default': true
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        container.select('b', 'a');
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
        container.deselect('b');
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
        container.deselect();
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
    });
    it('should reset option', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    'default': true
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        container.resetSelection(['b']);
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
        container.resetSelection(['c']);
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(true);
        container.resetSelection();
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
        container.resetSelection([
            'a',
            'b',
            'c'
        ]);
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(true);
    });
    it('should reset correctly if no default option specified', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a'
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        container.resetSelection(['b']);
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(true);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
        container.resetSelection();
        expect(container.getOptionView('a').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('b').$el.hasClass('active')).toBe(false);
        expect(container.getOptionView('c').$el.hasClass('active')).toBe(false);
    });
    it('should get option by another field and default by action', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    customField: 'customKeyA'
                },
                {
                    label: 'b',
                    action: 'b',
                    customField: 'customKeyB'
                },
                {
                    label: 'c',
                    action: 'c',
                    customField: 'customKeyC'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        expect(container.getOptionView('a').model.get('action')).toBe('a');
        expect(container.getOptionView('b').model.get('action')).toBe('b');
        expect(container.getOptionView('c').model.get('action')).toBe('c');
        expect(container.getOptionView('customKeyA', 'customField').model.get('action')).toBe('a');
        expect(container.getOptionView('customKeyB', 'customField').model.get('action')).toBe('b');
        expect(container.getOptionView('customKeyC', 'customField').model.get('action')).toBe('c');
    });
    it('should call select option on \'_onKeyDown\' call', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    triggerOnKeyCode: 13
                },
                {
                    label: 'b',
                    action: 'b',
                    customField: 'customKeyB'
                },
                {
                    label: 'c',
                    action: 'c',
                    customField: 'customKeyC'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'input'
        });
        var e = $.Event('keydown', { keyCode: 13 });
        var optionView = container.getOptionView('a');
        var optionViewSpy = sinon.spy(optionView, 'select');
        container._onKeyDown(e);
        expect(optionViewSpy).toHaveBeenCalled();
        optionViewSpy.restore();
    });
    it('should be able to add options to the container', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    'default': true
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'ul'
        });
        container.addOptions([
            {
                label: 'e',
                action: 'e'
            },
            {
                label: 'f',
                action: 'f'
            }
        ]);
        expect(container.options.length).toEqual(5);
    });
    it('should be able to remove options from the container', function () {
        var container = new Container({
            options: [
                {
                    label: 'a',
                    action: 'a',
                    'default': true
                },
                {
                    label: 'b',
                    action: 'b'
                },
                {
                    label: 'c',
                    action: 'c'
                }
            ],
            toggle: true,
            toggleClass: 'active',
            mainTemplate: mainTemplate,
            optionTemplate: optionTemplate,
            contentContainer: 'ul'
        });
        container.removeOptions(['b']);
        expect(container.options.length).toEqual(2);
    });
});