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
import buttonManager from 'src/core/core.events.bis';
import layoutModule from 'src/core/core.layout';
import jQuery from 'jquery';
import primaryNavModule from 'src/actionModel/actionModel.primaryNavigation';
import actionModel from 'src/actionModel/actionModel.modelGenerator';
import mainNavigationText from './test/templates/mainNavigation.htm';
import layoutText from './test/templates/layout.htm';
import eventsText from './test/templates/events.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {
    rewire$isIPad,
    rewire$isSupportsTouch,
    restore as utilsCommonRestore
} from '../../../src/util/utils.common';

describe('Core events', function () {
    let sandbox;

    beforeEach(function () {
        sandbox = sinon.createSandbox();

        setTemplates(eventsText, layoutText, mainNavigationText);
        document.fire('dom:loaded');
    });

    afterEach(function () {
        sandbox.restore();
        utilsCommonRestore();
    });

    describe('events', function () {
        beforeEach(function () {
            actionModel.menuDom = jQuery('<div></div>')[0];
            sandbox.stub(actionModel, 'resetMenu');
            sandbox.stub(actionModel, 'assembleMenuFromActionModel');
            sandbox.stub(actionModel, 'makeMenuVisible');
            sandbox.stub(actionModel, 'adjustMenuPosition');
        });
        afterEach(function () {
        });
        describe('mousedown', function () {
            it('should initialize global mousedown events(layoutModule.MINIMIZER_PATTERN)', function () {
                sandbox.spy(layoutModule, 'maximize');
                var element = jQuery('#minimized').removeClass('maximized').simulate('mouseup')[0];
                expect(layoutModule.maximize).toHaveBeenCalledWith(element);
            });
            it('should initialize global mousedown events(layoutModule.MINIMIZED_PATTERN)', function () {
                sandbox.spy(layoutModule, 'minimize');
                var element = jQuery('#minimized').parent().addClass('maximized').find('#minimized').simulate('mouseup')[0];
                expect(layoutModule.minimize).toHaveBeenCalledWith(element);
            });
            it('should initialize global mousedown events(layoutModule.META_LINKS_PATTERN)', function () {
                sandbox.stub(primaryNavModule, 'navigationOption');
                jQuery('#main_logOut_link').simulate('mousedown');
                expect(primaryNavModule.navigationOption).toHaveBeenCalledWith('logOut');
            });
            it('should initialize global mousedown events(layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN )', function () {
                sandbox.spy(buttonManager, 'down');

                rewire$isSupportsTouch(sandbox.stub().returns(false));

                var element = jQuery('#run').simulate('mousedown')[0];
                expect(buttonManager.down).toHaveBeenCalledWith(element);
            });
            it('should initialize global mousedown events(pressed layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN)', function () {
                sandbox.spy(buttonManager, 'down');
                rewire$isSupportsTouch(sandbox.stub().returns(false));
                jQuery('#run').attr('disabled', true).simulate('mousedown');
                expect(buttonManager.down).not.toHaveBeenCalled();
            });
            it('should initialize global mousedown events(not support touch, layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN)', function () {
                sandbox.spy(buttonManager, 'down');
                rewire$isSupportsTouch(sandbox.stub().returns(true));
                jQuery('#run').attr('disabled', true).simulate('mousedown');
                expect(buttonManager.down).not.toHaveBeenCalled();
            });
        });
        describe('mouseover', function () {
            it('should initialize global mouseover events(layoutModule.NAVIGATION_MUTTON_PATTERN)', function () {
                sandbox.spy(primaryNavModule, 'showNavButtonMenu');
                var element = jQuery('#main_manage').simulate('mouseover')[0];
                expect(primaryNavModule.showNavButtonMenu).toHaveBeenCalled();
                expect(primaryNavModule.showNavButtonMenu.lastCall.args[1]).toEqual(element);
            });
        });
        describe('mouseup', function () {
            it('should initialize global mouseup events(home, layoutModule.NAVIGATION_PATTERN)', function () {
                sandbox.stub(primaryNavModule, 'navigationOption');
                jQuery('#' + layoutModule.MAIN_NAVIGATION_HOME_ITEM_ID).find(layoutModule.BUTTON_PATTERN).simulate('mouseup');
                expect(primaryNavModule.navigationOption).toHaveBeenCalledWith('home');
            });
            it('should initialize global mouseup events(library, layoutModule.NAVIGATION_PATTERN)', function () {
                sandbox.stub(primaryNavModule, 'navigationOption');
                jQuery('#' + layoutModule.MAIN_NAVIGATION_LIBRARY_ITEM_ID).find(layoutModule.BUTTON_PATTERN).simulate('mouseup');
                expect(primaryNavModule.navigationOption).toHaveBeenCalledWith('library');
            });
            it('should initialize global mouseup events(other, layoutModule.NAVIGATION_PATTERN)', function () {
                sandbox.stub(primaryNavModule, 'navigationOption');
                jQuery('#main_view').find(layoutModule.BUTTON_PATTERN).simulate('mouseup');
                expect(primaryNavModule.navigationOption).not.toHaveBeenCalled();
            });
            it('should initialize global mouseup events(not selected, layoutModule.TABSET_TAB_PATTERN)', function () {
                let el = jQuery("[tabId='#attributesTab']");
                el.simulate('mouseup');
                expect(el.hasClass(layoutModule.SELECTED_CLASS)).toBeTruthy();
                expect(jQuery('#attributesTab').hasClass(layoutModule.HIDDEN_CLASS)).toBeFalsy();
                expect(jQuery('#propertiesTab').hasClass(layoutModule.HIDDEN_CLASS)).toBeTruthy();
            });
            it('should initialize global mouseup events(not selected, disabled, layoutModule.TABSET_TAB_PATTERN)', function () {
                jQuery("[tabId='#attributesTab']").attr('disabled', true).simulate('mouseup');
                expect(jQuery("[tabId='#attributesTab']").hasClass(layoutModule.SELECTED_CLASS)).not.toBeTruthy();
                expect(jQuery('#attributesTab').hasClass(layoutModule.HIDDEN_CLASS)).toBeTruthy();
                expect(jQuery('#propertiesTab').hasClass(layoutModule.HIDDEN_CLASS)).toBeFalsy();
            });
            it('should initialize global mouseup events(not capsule, layoutModule.BUTTON_PATTERN)', function () {
                sandbox.spy(buttonManager, 'up');
                var element = jQuery('#run').removeClass('capsule').simulate('mouseup')[0];
                expect(buttonManager.up).toHaveBeenCalledWith(element);
            });
        });
    });
    describe('button manager', function () {
        it('should know, if item is selected(selected)', function () {
            expect(buttonManager.isSelected((jQuery('#anchor')[0]))).toBeTruthy();
        });
        it('should know, if item is selected(not selected)', function () {
            expect(buttonManager.isSelected((jQuery('#anchor').parent().removeClass('selected')[0]))).toBeFalsy();
        });
        it('should know, if item is selected(not item)', function () {
            expect(buttonManager.isSelected((jQuery('#minimized')[0]))).toBeFalsy();
        });
        it('should know, if item is selected(select by function)', function () {
            expect(buttonManager.isSelected(true, function () {
                return (jQuery('#anchor')[0]);
            })).toBeTruthy();
        });
        it('should know, if item is selected(nothing selected)', function () {
            expect(buttonManager.isSelected()).toBeFalsy();
        });
        it('should set layoutModule.HOVERED_CLASS', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            buttonManager.over(jQuery('#menuMutton')[0]);
            expect(jQuery('#menuMutton').hasClass(layoutModule.HOVERED_CLASS)).toBeTruthy();
        });
        it('should not set layoutModule.HOVERED_CLASS if item is selected', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(true);
            buttonManager.over(jQuery('#menuMutton')[0]);
            expect(jQuery('#menuMutton').hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
        });
        it('should set layoutModule.HOVERED_CLASS for element, got by function', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            buttonManager.over(true, function () {
                return (jQuery('#menuMutton')[0]);
            });
            expect(jQuery('#menuMutton').hasClass(layoutModule.HOVERED_CLASS)).toBeTruthy();
        });
        it('should remove classes on out', function () {
            var element = jQuery('#menuMutton').addClass(layoutModule.HOVERED_CLASS).addClass(layoutModule.PRESSED_CLASS);
            buttonManager.out(element[0]);
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).not.toBeTruthy();
        });
        it('should remove classes on out for element, got by function', function () {
            var element = jQuery('#menuMutton').addClass(layoutModule.HOVERED_CLASS).addClass(layoutModule.PRESSED_CLASS);
            buttonManager.out(true, function () {
                return element[0];
            });
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).not.toBeTruthy();
        });
        it('should set layoutModule.PRESSED_CLASS', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            var element = jQuery('#menuMutton').addClass(layoutModule.HOVERED_CLASS);
            buttonManager.down(element[0]);
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).toBeTruthy();
        });
        it('should not set layoutModule.PRESSED_CLASS if item is selected', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            var element = jQuery('#menuMutton').addClass(layoutModule.HOVERED_CLASS);
            buttonManager.down(element[0]);
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).toBeTruthy();
        });
        it('should set layoutModule.PRESSED_CLASS for element, got by function', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            var element = jQuery('#menuMutton').addClass(layoutModule.HOVERED_CLASS);
            buttonManager.down(true, function () {
                return element[0];
            });
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).toBeTruthy();
        });
        it('should set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            var element = jQuery('#menuMutton').addClass(layoutModule.PRESSED_CLASS).removeClass('selected');
            buttonManager.up(element[0]);
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).not.toBeTruthy();
        });
        it('should not set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS if is ipad', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            var element = jQuery('#menuMutton').addClass(layoutModule.PRESSED_CLASS);

            rewire$isIPad(sandbox.stub().returns(true));
            buttonManager.up(element[0]);
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).not.toBeTruthy();
        });
        it('should not set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS if item is selected', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(true);
            var element = jQuery('#menuMutton').addClass(layoutModule.PRESSED_CLASS);
            buttonManager.up(element[0]);
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).not.toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).toBeTruthy();
        });
        it('should set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS for element, got by function', function () {
            sandbox.stub(buttonManager, 'isSelected').returns(false);
            var element = jQuery('#menuMutton').removeClass('selected').addClass(layoutModule.PRESSED_CLASS);
            buttonManager.up(true, function () {
                return element[0];
            });
            expect(element.hasClass(layoutModule.HOVERED_CLASS)).toBeTruthy();
            expect(element.hasClass(layoutModule.PRESSED_CLASS)).not.toBeTruthy();
        });
        it('should enable element', function () {
            spyOn(buttonManager, 'out');
            var element = jQuery('#run').attr('disabled', true)[0];
            buttonManager.enable(element);
            expect(element).not.toBeDisabled();
            expect(buttonManager.out).toHaveBeenCalledWith(element);
        });
        it('should disable element', function () {
            sandbox.spy(buttonManager, 'out');
            var element = jQuery('#run')[0];
            buttonManager.disable(element);
            expect(element).toBeDisabled();
            expect(buttonManager.out).toHaveBeenCalledWith(element);
        });
        it('should select element', function () {
            var element = jQuery('#run');
            buttonManager.select(element[0]);
            expect(element.hasClass(layoutModule.SELECTED_CLASS)).toBeTruthy();
        });
        it('should deselect element', function () {
            var element = jQuery('#run').addClass(layoutModule.SELECTED_CLASS);
            buttonManager.unSelect(element[0]);
            expect(element.hasClass(layoutModule.SELECTED_CLASS)).not.toBeTruthy();
        });
    });
});