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

/*global spyOn*/

import jQuery from 'jquery';
import actionModel from 'src/actionModel/actionModel.modelGenerator';
import toolbuttonText from './test/templates/toolbutton.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import toolbarButtonModule from 'src/components/components.toolbarButtons.events';

describe('components toolbar buttons', function () {
    var actionMap = jasmine.createSpyObj('toolbarActionMap', [
        'normal',
        'disabled'
    ]);
    beforeEach(function () {
        setTemplates(toolbuttonText);
        spyOn(actionModel, 'showDynamicMenu');
        spyOn(actionModel, 'hideMenu');
        actionMap.normal.calls.reset();
        actionMap.disabled.calls.reset();
        toolbarButtonModule.initialize(actionMap);
    });
    it('should show menu on mouse enter', function () {
        jQuery('#withMenu').simulate('mouseover');
        expect(actionModel.showDynamicMenu).toHaveBeenCalled();
    });
    it('should not show menu on mouse enter if button disabled', function () {
        jQuery('#withMenuDisabled').simulate('mouseover');
        expect(actionModel.showDynamicMenu).not.toHaveBeenCalled();
    });
    it('should not show menu on mouse enter if button has not mutton pattern', function () {
        jQuery('#normal').simulate('mouseover');
        expect(actionModel.showDynamicMenu).not.toHaveBeenCalled();
    });
    it('should run corresponding action', function () {
        jQuery('#normal').simulate('mouseup');
        expect(actionMap.normal).toHaveBeenCalled();
    });
    it('should not run corresponding action if button is disabled', function () {
        jQuery('#disabled').attr('disabled', true);
        jQuery('#normal').simulate('mouseup');
        expect(actionMap.disabled).not.toHaveBeenCalled();
    });
    it('should determine, if element is tool button or not', function () {
        expect(toolbarButtonModule.isToolBarButton(jQuery('#normal')[0])).toBeTruthy();
        expect(toolbarButtonModule.isToolBarButton(jQuery('#disabled')[0])).toBeTruthy();
        expect(toolbarButtonModule.isToolBarButton(jQuery('#withMenu')[0])).toBeTruthy();
        expect(toolbarButtonModule.isToolBarButton(jQuery('#withMenuDisabled')[0])).toBeTruthy();
        expect(toolbarButtonModule.isToolBarButton(jQuery('#frame')[0])).toBeFalsy();
        expect(toolbarButtonModule.isToolBarButton(jQuery('#menu')[0])).toBeFalsy();
    });
    it('should be able to enable and disable', function () {
        var enabledButton = jQuery('#normal')[0];
        toolbarButtonModule.disable(enabledButton);
        expect(enabledButton).toBeDisabled();
        toolbarButtonModule.enable(enabledButton);
        expect(enabledButton).not.toBeDisabled();
    });
    it('should be able to set button state', function () {
        var enabledButton = jQuery('#normal')[0];
        toolbarButtonModule.setButtonState(enabledButton, false);
        expect(enabledButton).toBeDisabled();
        toolbarButtonModule.setButtonState(enabledButton, true);
        expect(enabledButton).not.toBeDisabled();
    });
});