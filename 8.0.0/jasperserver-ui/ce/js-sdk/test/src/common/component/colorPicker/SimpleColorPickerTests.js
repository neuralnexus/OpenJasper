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
import ColorPicker from 'src/common/component/colorPicker/SimpleColorPicker';
describe('SimpleColorPicker component', function () {
    var colorPicker;
    beforeEach(function () {
        colorPicker = new ColorPicker();
    });
    afterEach(function () {
        colorPicker && colorPicker.remove();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof ColorPicker).toBe('function');
        expect(ColorPicker.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should have public API', function () {
        expect(colorPicker.show).toBeDefined();
        expect(colorPicker.hide).toBeDefined();
    });
    it('should trigger \'color:selected\' event on color click', function () {
        var eventSpy = sinon.spy(colorPicker, 'trigger');
        var color = colorPicker.$el.find('.color').first();
        color.trigger('click');
        expect(eventSpy).toHaveBeenCalledWith('color:selected', color.css('background-color'));
    });
    it('should highlight color', function () {
        colorPicker.highlightColor('rgb(152, 0, 0)');
        var colorBox = colorPicker.$el.find('.colorWrapper.selected');
        expect(colorBox.length).toEqual(1);
    });
    it('should highlight transparent color', function () {
        var colorPicker = new ColorPicker({ showTransparentInput: true });
        colorPicker.highlightColor('rgba(0, 0, 0, 0)');
        var colorBox = colorPicker.$el.find('.color.transparent.selected');
        expect(colorBox.length).toEqual(1);
    });
    it('should remove highlighting from previous color', function () {
        colorPicker.highlightColor('rgb(152, 0, 0)');
        var colorBox = colorPicker.$el.find('.colorWrapper.selected');
        expect(colorBox.length).toEqual(1);
        colorPicker.highlightColor('rgb(255, 0, 0)');
        var colorBox = colorPicker.$el.find('.colorWrapper.selected');
        expect(colorBox.length).toEqual(1);
    });
});