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
import layoutModule from 'src/core/core.layout';
import jQuery from 'jquery';
import pageDimmerText from './test/templates/pageDimmer.htm';
import standardAlertText from './test/templates/standardAlert.htm';
import mainNavigationText from './test/templates/mainNavigation.htm';
import layoutText from './test/templates/layout.htm';
import inputControlsTemp from './test/templates/inputControlsTemp.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {rewire as rewireTruncator, restore as restoreTruncator} from 'src/util/tools.truncator';
import {rewire$JSCookie, restore as restoreJSCookie} from 'src/util/utils.common';
import {
    rewire$isIE7,
    rewire$centerElement,
    restore as restoreUtilsCommon
} from '../../../src/util/utils.common';
import * as iScroll from 'iscroll';
import * as dragndrop from 'dragdropextra';

describe('core layout', function () {
    let sandbox;
    // in webpack 5 sinon can not stub variable which
    // uses star import: import * as ns from 'library'
    const iScroll1 = iScroll.iScroll;
    const Draggable = dragndrop.Draggable;

    beforeEach(function () {
        sandbox = sinon.createSandbox();

        setTemplates(pageDimmerText, standardAlertText, mainNavigationText, inputControlsTemp);
        jQuery('#standardAlert').removeClass(layoutModule.HIDDEN_CLASS);
    });

    afterEach(function () {
        sandbox.restore();
        restoreUtilsCommon();
        restoreJSCookie();
        iScroll.iScroll = iScroll1;
        dragndrop.Draggable = Draggable;
        restoreTruncator();
    });

    describe('common', function () {
        it('should initialize', function () {
            rewireTruncator(this.spy = sandbox.stub())

            let centerElementStub = sandbox.stub();
            rewire$centerElement(centerElementStub);
            sandbox.stub(layoutModule, 'createSizer');
            sandbox.stub(layoutModule, 'createMover');
            layoutModule.initialize();
            expect(layoutModule.createSizer).toHaveBeenCalled();
            expect(layoutModule.createMover).toHaveBeenCalled();
            expect(centerElementStub).toHaveBeenCalled();
            expect(parseFloat(jQuery('#' + layoutModule.DIMMER_ID)[0].style.zIndex)).toEqual(layoutModule.DIMMER_Z_INDEX);
        });
        it('should fix IE7 sizes', function () {
            rewire$isIE7(sandbox.stub().returns(true));
            sandbox.stub(layoutModule, 'fixIE7Sizes');
            layoutModule.initialize();
            expect(layoutModule.fixIE7Sizes).toHaveBeenCalled();
        });
    });
    describe('scroller', function () {
        beforeEach(function () {
            iScroll.iScroll = sandbox.spy(function (a, b) {
                for (var key in b) {
                    if (b.hasOwnProperty(key)) {
                        this[key] = b[key];
                    }
                }
            });
        });
        it('should create scroller for container', function () {
            var mockContainer = {
                identify: function () {
                    return 'container';
                }
            };
            var newlyCreatedScroll = layoutModule.createScroller(mockContainer);
            expect(layoutModule.scrolls.get(mockContainer.identify())).toBe(newlyCreatedScroll);
        });
        it('should disable transform for some containers', function () {
            var container;
            var mockContainer = {
                identify: function () {
                    return container;
                }
            };
            var disableTransformFor = [
                'mainTableContainer',
                'resultsContainer',
                'filtersPanel',
                'foldersPodContent'
            ];
            for (var i = 0; i < disableTransformFor.length; i++) {
                container = disableTransformFor[i];
                layoutModule.createScroller(mockContainer);
                expect(iScroll.iScroll.args[i][1].useTransform).toBeFalsy();
            }
        });
        it('should not disable transform', function () {
            var container;
            var mockContainer = {
                identify: function () {
                    return container;
                }
            };
            var notDisableTransformFor = [
                'sample',
                'sammple2'
            ];
            for (var i = 0; i < notDisableTransformFor.length; i++) {
                container = notDisableTransformFor[i];
                layoutModule.createScroller(mockContainer);
                expect(iScroll.iScroll.args[i][1].useTransform).toBeTruthy();
            }
        });
    });
    describe('moveable', function () {

        it('should create moveable', function () {
            dragndrop.Draggable = sandbox.stub();
            var element = (jQuery('#standardAlert')[0]);
            layoutModule.createMover(element);
            expect(dragndrop.Draggable).toHaveBeenCalled();
            expect(dragndrop.Draggable.args[0][1].handle).toBeDefined();
        });

        it('should create movable using jQuery', () => {
            const jQueryEl = jQuery('#standardAlert');
            const element = (jQueryEl[0]);

            layoutModule.createMover(element, {
                useJQueryForDraggable: true
            });

            expect(jQueryEl.find(layoutModule.MOVER_PATTERN)).toHaveClass('ui-draggable-handle');
            expect(jQueryEl).toHaveClass('ui-draggable');

            jQueryEl.draggable('destroy');
        });

        it('should not became movable if hidden', function () {
            dragndrop.Draggable = sandbox.stub();
            var element = (jQuery('#standardAlert').addClass(layoutModule.HIDDEN_CLASS)[0]);
            layoutModule.createMover(element);
            jQuery('#standardAlert').removeClass(layoutModule.HIDDEN_CLASS);
            expect(dragndrop.Draggable).not.toHaveBeenCalled();
        });
    });
    describe('sizeable', function () {
        it('should create sizeable and be able to resize', function () {
            var $target = jQuery('#standardAlert');
            var startHeight = parseFloat($target.css('height'));
            var startWidth = parseFloat($target.css('width'));
            layoutModule.createSizer(jQuery($target[0]));
            jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {
                dx: 10,
                dy: 10
            });
            var height = parseFloat($target.css('height'));
            var width = parseFloat($target.css('width'));
            expect(height >= startHeight + 9).toBeTruthy();
            expect(width).toEqual(startWidth + 10);
            jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {
                dx: -10,
                dy: -10
            });
            height = parseFloat($target.css('height'));
            width = parseFloat($target.css('width'));
            expect(height >= startHeight - 1).toBeTruthy();
            expect(width).toEqual(startWidth);
        });
        it('should create sizeable and be able to resize width only', function () {
            var $target = jQuery('#standardAlert');
            var $sizer = jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).removeClass('diagonal').addClass('horizontal');
            var startHeight = parseFloat($target.css('height'));
            var startWidth = parseFloat($target.css('width'));
            layoutModule.createSizer(jQuery($target[0]));
            jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {
                dx: 10,
                dy: 10
            });
            var height = parseFloat($target.css('height'));
            var width = parseFloat($target.css('width'));
            expect(height).toEqual(startHeight);
            expect(width).toEqual(startWidth + 10);
            jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {
                dx: -10,
                dy: -10
            });
            height = parseFloat($target.css('height'));
            width = parseFloat($target.css('width'));
            expect(height).toEqual(startHeight);
            expect(width).toEqual(startWidth);
            $sizer.removeClass('horizontal').addClass('diagonal');
        });
        it('should create sizeable and be able to resize height only', function () {
            var $target = jQuery('#standardAlert');
            var $sizer = jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).removeClass('diagonal').addClass('vertical');
            var startHeight = parseFloat($target.css('height'));
            var startWidth = parseFloat($target.css('width'));
            layoutModule.createSizer(($target[0]));
            $sizer.simulate('drag', {
                dx: 10,
                dy: 10
            });
            var height = parseFloat($target.css('height'));
            var width = parseFloat($target.css('width'));
            expect(height >= startHeight + 9).toBeTruthy();
            expect(width).toEqual(startWidth);
            $sizer.simulate('drag', {
                dx: -10,
                dy: -10
            });
            height = parseFloat($target.css('height'));
            width = parseFloat($target.css('width'));
            expect(height >= startHeight - 1).toBeTruthy();
            expect(width).toEqual(startWidth);
            $sizer.removeClass('horizontal').addClass('diagonal');
        });
        it('should fire event after resizing', function () {
            var $target = jQuery('#standardAlert');
            var $sizer = jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN);

            sandbox.stub(document, 'fire');
            layoutModule.createSizer(($target[0]));
            $sizer.simulate('drag', {
                dx: 10,
                dy: 10
            });
            expect(document.fire).toHaveBeenCalled();
            expect(document.fire.lastCall.args[0]).toEqual('dragger:sizer');
        });
    });
    describe('Maximizer/minimizer', function () {
        var templateId = 'maximinizable';
        var expectedWidth = 100;
        beforeEach(function () {
            setTemplates(layoutText);
            window.localStorage && localStorage.setItem(templateId + layoutModule.MINIMIZED, true);
        });
        it('should maximize', function () {
            let el = jQuery('#' + templateId);

            var column = (el[0]);
            window.localStorage && localStorage.setItem(templateId + layoutModule.PANEL_WIDTH, expectedWidth);
            layoutModule.maximize(column, true);
            expect(el.hasClass('maximized')).toBeTruthy();
            expect(column.style.width).toEqual(expectedWidth + 'px');
        });
        it('should minimize', function () {
            let el = jQuery('#' + templateId);
            var column = (el[0]);
            column.style.width = expectedWidth + 'px';
            window.localStorage && localStorage.setItem(templateId + layoutModule.MINIMIZED, false);
            layoutModule.minimize(column, true);
            expect(el.hasClass('minimized')).toBeTruthy();
            expect(el.hasClass('minimized_if_vertical_orientation')).not.toBeTruthy();
        });
        it('should minimize left panel if it should be minimized', function () {
            let el = jQuery('#' + templateId);
            var leftPanel = (el.removeClass('minimized').show()[0]);
            leftPanel.style.width = expectedWidth + 'px';
            window.localStorage && localStorage.setItem(templateId + layoutModule.MINIMIZED, true);
            window.localStorage && localStorage.setItem(templateId + layoutModule.PANEL_WIDTH, expectedWidth);
            spyOn(layoutModule, 'minimize');
            layoutModule.resizeOnClient(templateId, 'main');
            expect(layoutModule.minimize).toHaveBeenCalledWith(leftPanel, true);
        });
        it('cookies should have higher priority than class', function () {
            var leftPanel = (jQuery('#' + templateId).removeClass('minimized')[0]);
            leftPanel.style.width = expectedWidth + 'px';
            window.localStorage && localStorage.setItem(templateId + layoutModule.MINIMIZED, true);
            window.localStorage && localStorage.setItem(templateId + layoutModule.PANEL_WIDTH, expectedWidth);
            spyOn(layoutModule, 'minimize');
            layoutModule.resizeOnClient(templateId, 'main');
            expect(layoutModule.minimize).toHaveBeenCalledWith(leftPanel, true);
        });
        it('should maximize panel if it should be maximized', function () {
            var leftPanel = (jQuery('#' + templateId).removeClass('minimized')[0]);
            spyOn(localStorage, 'getItem').and.callFake(function (name) {
                if (name.indexOf(layoutModule.MINIMIZED) > 0) {
                    return 'false';
                }
                return expectedWidth;
            });
            var mainPanel = (jQuery('#main')[0]);
            layoutModule.resizeOnClient(templateId, 'main');
            expect(leftPanel.style.width).toEqual(expectedWidth + 'px');
            expect(mainPanel.style.left).toEqual(expectedWidth + 'px');
        });
        it('should automaximize(cookies not set, has class)', function () {
            spyOn(localStorage, 'getItem').and.returnValue('false');
            spyOn(layoutModule, 'maximize');
            var leftPanel = (jQuery('#' + templateId).addClass('minimized')[0]);
            layoutModule.autoMaximize(leftPanel);
            expect(layoutModule.maximize).not.toHaveBeenCalled();
        });
        it('should automaximize(cookies not set, class not set)', function () {
            rewire$JSCookie(sandbox.stub().returns({value: 'false'}));
            spyOn(layoutModule, 'maximize');
            var leftPanel = (jQuery('#' + templateId).removeClass('minimized')[0]);
            layoutModule.autoMaximize(leftPanel);
            expect(layoutModule.maximize).not.toHaveBeenCalled();
        });
        it('should automaximize(cookies set, class not set)', function () {
            rewire$JSCookie(sandbox.stub().returns({value: 'true'}));
            spyOn(layoutModule, 'maximize');
            var leftPanel = (jQuery('#' + templateId).removeClass('minimized')[0]);
            layoutModule.autoMaximize(leftPanel);
            expect(layoutModule.maximize).not.toHaveBeenCalled();
        });
        it('should automaximize(cookies not set, class set)', function () {
            rewire$JSCookie(sandbox.stub().returns({value: 'true'}));
            spyOn(layoutModule, 'maximize');
            var leftPanel = (jQuery('#' + templateId).addClass('minimized')[0]);
            layoutModule.autoMaximize(leftPanel);
            expect(layoutModule.maximize).toHaveBeenCalled();
        });
        it('should autominimize(cookies not set)', function () {
            spyOn(localStorage, 'getItem').and.returnValue('false');
            spyOn(layoutModule, 'minimize');
            var leftPanel = (jQuery('#' + templateId).addClass('minimized')[0]);
            layoutModule.autoMinimize(leftPanel);
            expect(layoutModule.minimize).toHaveBeenCalled();
        });
        it('should autominimize(cookies set)', function () {
            rewire$JSCookie(sandbox.stub().returns({value: 'true'}));
            spyOn(layoutModule, 'minimize');
            let el = jQuery('#' + templateId);
            var leftPanel = (el.addClass('minimized')[0]);
            layoutModule.autoMinimize(leftPanel);
            expect(layoutModule.minimize).not.toHaveBeenCalled();
            expect(el.hasClass('minimized')).not.toBeTruthy();
        });
    });
});