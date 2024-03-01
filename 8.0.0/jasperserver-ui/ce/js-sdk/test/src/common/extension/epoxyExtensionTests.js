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
import epoxy from 'src/common/extension/epoxyExtension';
import _ from 'underscore';
import Backbone from 'backbone';

import {
    rewire as attachableColorPickerWrapperRewire,
    restore as attachableColorPickerWrapperRestore
} from 'src/common/component/colorPicker/react/AttachableColorPickerWrapper';

import {
    rewire as colorSelectorWrapperRewire,
    restore as colorSelectorWrapperRestore
} from 'src/common/component/colorPicker/react/ColorSelectorWrapper';

import colorConvertUtil from 'src/common/component/colorPicker/util/colorConvertUtil';

var Model = Backbone.Model.extend({});

const getView = (options = {
    value: true,
    name: 'Name',
    alignment: 'left',
    bold: true,
    italic: true,
    color: 'rgb(0,0,0)'
}) => {
    const View = Backbone.View.extend({
        el: '<div>' + '<div data-bind=\'validationErrorClass:value\' data-model-attr=\'value\' class=\'errorClass\'></div>' + '<input data-bind=\'value:escapeCharacters(value)\'/>' + '<div class=\'errorText\' data-bind=\'validationErrorText:value\' data-model-attr=\'value\'></div>' + '<div class=\'textInputButton checked left\'  data-bind=\'radioDiv:alignment\' data-value=\'left\'>' + '<span class=\'textIcon textALeft checked radioChild\'></span>' + '</div>' + '<div class=\'textInputButton center\' data-bind=\'radioDiv:alignment\' data-value=\'center\'>' + '<span class=\'textIcon textACenter radioChild\'></span>' + '</div>' + '<div class=\'textInputButton right\' data-bind=\'radioDiv:alignment\' data-value=\'right\'>' + '<span class=\'textIcon textARight radioChild\'></span>' + '</div>' + '<div class=\'textInputButton bold\' data-bind=\'checkboxDiv:bold\'>' + '<span class=\'textIcon textBold checkboxChild\'></span>' + '</div>' + '<div class=\'textInputButton italic\' data-bind=\'checkboxDiv:italic\'>' + '<span class=\'textIcon textItalic checkboxChild\'></span>' + '</div>' + '<div class=\'textInputButton textColorButtons\' data-bind=\'colorpicker:color\' data-label=\'label\'>' + '<div class=\'colorIndicator\'></div>' + '<span class=\'textIcon textColor\'></span>' + '</div>' + '<span class=\'prependText\' data-bind=\'text:prependText(name, "My")\'></span>' + '</div>',
        model: new Model(options)
    });

    epoxy.View.mixin(View.prototype);

    return View;
};

describe('epoxyExtension Tests', function () {
    var testView;

    let sandbox;

    let ColorPickerStub,
        colorPickerInstanceStub;

    let ColorSampleStub,
        ColorSelectorStub,
        colorSampleInstanceStub,
        colorSelectorInstanceStub;

    beforeEach(function () {
        sandbox = sinon.createSandbox();

        colorPickerInstanceStub = {
            setColor: sandbox.stub(),
            remove: sandbox.stub()
        };

        colorSampleInstanceStub = {
            setState: sandbox.stub(),
            remove: sandbox.stub()
        };

        colorSelectorInstanceStub = {
            setState: sandbox.stub(),
            remove: sandbox.stub()
        };

        ColorPickerStub = sandbox.stub().returns(colorPickerInstanceStub);
        ColorSampleStub = sandbox.stub().returns(colorSampleInstanceStub);
        ColorSelectorStub = sandbox.stub().returns(colorSelectorInstanceStub);

        attachableColorPickerWrapperRewire(ColorPickerStub);
        colorSelectorWrapperRewire(ColorSampleStub);

        const View = getView();

        testView = new View();
        testView.applyBindings();
    });

    afterEach(function () {
        sandbox.restore();
        attachableColorPickerWrapperRestore();
        colorSelectorWrapperRestore();
        testView.removeBindings();
        testView.remove();
    });

    it('should have validationErrorClass and validationErrorText handler', function () {
        testView.model.trigger('validate:value', testView.model, 'value', 'This is error');
        expect(testView.$el.find('.errorClass')).toHaveClass('error');
        expect(testView.$el.find('.errorText').text()).toBe('This is error');
    });
    it('should have escapeCharacters filter', function () {
        var value = '<script>alert("Hello");</script>';
        var escapedCharacters = '&lt;script&gt;alert(&quot;Hello&quot;);&lt;/script&gt;';
        testView.model.set('value', value);
        var input = testView.$el.find('input');
        expect(input.val()).toEqual(escapedCharacters);
        testView.model.set('value', 'valid');
        input.val(value);
        input.trigger('change');
        expect(testView.model.get('value')).toEqual(value);
    });
    it('should have radioDiv handler', function () {
        testView.$el.find('.center').trigger('click');
        expect(testView.model.get('alignment')).toEqual('center');
        expect(testView.$el.find('.center')).toHaveClass('checked');
        testView.$el.find('.right').trigger('click');
        expect(testView.model.get('alignment')).toEqual('right');
        expect(testView.$el.find('.right')).toHaveClass('checked');
        testView.$el.find('.left').trigger('click');
        expect(testView.model.get('alignment')).toEqual('left');
        expect(testView.$el.find('.left')).toHaveClass('checked');
    });
    it('should have checkboxDiv handler', function () {
        testView.$el.find('.bold').trigger('click');
        expect(testView.model.get('bold')).toBeFalsy();
        expect(testView.$el.find('.bold')).not.toHaveClass('checked');
        testView.$el.find('.bold').trigger('click');
        expect(testView.model.get('bold')).toBeTruthy();
        expect(testView.$el.find('.bold')).toHaveClass('checked');
        testView.$el.find('.italic').trigger('click');
        expect(testView.model.get('italic')).toBeFalsy();
        expect(testView.$el.find('.italic')).not.toHaveClass('checked');
        testView.$el.find('.italic').trigger('click');
        expect(testView.model.get('italic')).toBeTruthy();
        expect(testView.$el.find('.italic')).toHaveClass('checked');
    });

    describe('colorpicker handler', () => {

        describe('with opaque color', () => {
            let colorpickerTestView;

            beforeEach(() => {
                ColorPickerStub = sandbox.stub().returns(colorPickerInstanceStub);
                ColorSampleStub = sandbox.stub().returns(colorSampleInstanceStub);
                ColorSelectorStub = sandbox.stub().returns(colorSelectorInstanceStub);

                attachableColorPickerWrapperRewire(ColorPickerStub);
                colorSelectorWrapperRewire(ColorSelectorStub);

                sandbox.stub(colorConvertUtil, 'isRgbTransparent').returns(false);
                sandbox.stub(colorConvertUtil, 'rgba2NoAlphaHex').returns('hex');

                const View = getView();

                colorpickerTestView = new View();
                colorpickerTestView.applyBindings();
            });

            afterEach(() => {
                colorpickerTestView.removeBindings();
                colorpickerTestView.remove();
            });

            it('should init attachableColorPicker', () => {
                const binding = colorpickerTestView.b();

                const colorpicker = _.find(binding, function (item) {
                    return !_.isUndefined(item.colorPicker);
                }).colorPicker;

                expect(colorpicker).toEqual(colorSelectorInstanceStub);

                const colorPickerOptions = ColorSelectorStub.args[0][1];

                colorPickerOptions.onColorChange({
                    rgb: {
                        r: 1,
                        g: 1,
                        b: 1,
                        a: 0
                    }
                });

                expect(colorpickerTestView.model.get('color')).toEqual('rgba(1, 1, 1, 0)');
                expect(colorConvertUtil.rgba2NoAlphaHex).toHaveBeenCalledWith('rgba(1, 1, 1, 0)');
                expect(colorConvertUtil.isRgbTransparent).toHaveBeenCalledWith('rgba(1, 1, 1, 0)');

                expect(colorSelectorInstanceStub.setState).toHaveBeenCalledWith({color: 'hex', label: 'hex'});

                colorpickerTestView.removeBindings();
                colorpickerTestView.remove();

                expect(colorSelectorInstanceStub.remove).toHaveBeenCalled();
            });

            it('should init color sample', function () {
                const binding = colorpickerTestView.b();

                const colorSample = _.find(binding, function (item) {
                    return !_.isUndefined(item.colorPicker);
                }).colorPicker;

                expect(colorSample).toEqual(colorSelectorInstanceStub);

                const colorSampleOptions = ColorSelectorStub.args[0][1];

                expect(colorSampleOptions.color).toEqual('hex');
                expect(colorSampleOptions.label).toEqual('hex');

                expect(colorSelectorInstanceStub.setState).toHaveBeenCalledWith({
                    color: 'hex',
                    label: 'hex'
                });

                colorpickerTestView.removeBindings();
                colorpickerTestView.remove();

                expect(colorSelectorInstanceStub.remove).toHaveBeenCalled();
            });
        });

        describe('with transparent color', () => {
            let colorpickerTestView;

            beforeEach(() => {
                ColorPickerStub = sandbox.stub().returns(colorPickerInstanceStub);
                ColorSampleStub = sandbox.stub().returns(colorSampleInstanceStub);

                attachableColorPickerWrapperRewire(ColorPickerStub);
                colorSelectorWrapperRewire(ColorSampleStub);

                sandbox.stub(colorConvertUtil, 'isRgbTransparent').returns(true);
                sandbox.stub(colorConvertUtil, 'rgba2NoAlphaHex');

                const View = getView();

                colorpickerTestView = new View();
                colorpickerTestView.applyBindings();
            });

            afterEach(() => {
                colorpickerTestView.removeBindings();
                colorpickerTestView.remove();
            });

            it('should init color sample', function () {
                const binding = colorpickerTestView.b();

                const colorSample = _.find(binding, function (item) {
                    return !_.isUndefined(item.colorPicker);
                }).colorPicker;

                expect(colorSample).toEqual(colorSampleInstanceStub);

                const colorSampleOptions = ColorSampleStub.args[0][1];

                expect(colorSampleOptions.color).toEqual('transparent');
                expect(colorSampleOptions.label).toEqual('TRANSP');

                expect(colorSampleInstanceStub.setState).toHaveBeenCalledWith({
                    color: 'transparent',
                    label: 'TRANSP'
                });
            });
        });
    });

    it('should have prependText filter', function () {
        var text = testView.$el.find('.prependText').text();
        expect(text).toEqual('My Name');
    });
});