/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: core.layout.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["core.layout",
        "text!templates/pageDimmer.htm",
        "text!templates/standardAlert.htm",
        "text!templates/mainNavigation.htm",
        "text!templates/layout.htm"],
        function(layoutModule, pageDimmerText, standardAlertText, mainNavigationText, layoutText) {

        describe('core layout', function() {

            beforeEach(function() {
                setTemplates(pageDimmerText, standardAlertText, mainNavigationText);
                jQuery('#standardAlert').removeClass(layoutModule.HIDDEN_CLASS);
            });

            describe("common", function() {
                it("should initialize", function() {
                    spyOn(window, 'Truncator');
                    spyOn(window, 'centerElement');
                    spyOn(layoutModule, 'createSizer');
                    spyOn(layoutModule, 'createMover');

                    layoutModule.initialize();

                    expect(layoutModule.createSizer).toHaveBeenCalled();
                    expect(layoutModule.createMover).toHaveBeenCalled();
                    expect(centerElement).toHaveBeenCalled();
                    expect(parseFloat(jQuery('#' + layoutModule.DIMMER_ID)[0].style.zIndex)).toEqual(layoutModule.DIMMER_Z_INDEX);

                });

                it("should fix IE7 sizes", function() {
                    spyOn(window, 'isIE7').andReturn(true);
                    spyOn(layoutModule, 'fixIE7Sizes');

                    layoutModule.initialize();

                    expect(layoutModule.fixIE7Sizes).toHaveBeenCalled();
                });

            });
            describe("scroller", function() {
                beforeEach(function(){
                    sinon.stub(window, "iScroll", function(a, b){
                        for (var key in b){
                            if (b.hasOwnProperty(key)){
                                this[key] = b[key];
                            }
                        }
                    });
                });

                afterEach(function(){
                    window.iScroll.restore();
                });

                it("should create scroller for container", function() {
                    var mockContainer = {
                        identify: function() {
                            return 'container'
                        }
                    };

                    var newlyCreatedScroll = layoutModule.createScroller(mockContainer);

                    expect(layoutModule.scrolls.get(mockContainer.identify())).toBe(newlyCreatedScroll);
                });

                it("should disable transform for some containers", function() {
                    var container;
                    var mockContainer = {
                        identify: function() {
                            return container
                        }
                    };

                    var disableTransformFor = ["mainTableContainer", "resultsContainer", "filtersPanel", "foldersPodContent"];

                    for (var i = 0; i < disableTransformFor.length; i++) {
                        container = disableTransformFor[i];
                        layoutModule.createScroller(mockContainer);
                        expect(iScroll.args[i][1].useTransform).toBeFalsy();
                    }
                });

                it("should not disable transform", function() {
                    var container;
                    var mockContainer = {
                        identify: function() {
                            return container
                        }
                    };

                    var notDisableTransformFor = ["sample", "sammple2"];

                    for (var i = 0; i < notDisableTransformFor.length; i++) {
                        container = notDisableTransformFor[i];
                        layoutModule.createScroller(mockContainer);
                        expect(iScroll.args[i][1].useTransform).toBeTruthy();
                    }
                });
            });

            describe("moveable", function() {
                it("should create moveable", function() {
                    spyOn(window, 'Draggable');

                    var element = $(jQuery('#standardAlert')[0]);
                    layoutModule.createMover(element);

                    expect(Draggable).toHaveBeenCalled();
                    expect(Draggable.mostRecentCall.args[1].handle).toBeDefined();
                });

                it("should not became movable if hidden", function() {
                    spyOn(window, 'Draggable');

                    var element = $(jQuery('#standardAlert').addClass(layoutModule.HIDDEN_CLASS)[0]);
                    layoutModule.createMover(element);
                    jQuery('#standardAlert').removeClass(layoutModule.HIDDEN_CLASS);

                    expect(Draggable).not.toHaveBeenCalled();

                });
            });

            describe('sizeable', function() {
                it("should create sizeable and be able to resize", function() {
                    var $target = jQuery('#standardAlert');
                    var startHeight = parseFloat($target.css('height'));
                    var startWidth = parseFloat($target.css('width'));

                    layoutModule.createSizer($($target[0]));

                    jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {dx: 10, dy: 10});

                    var height = parseFloat($target.css('height'));
                    var width = parseFloat($target.css('width'));

                    expect(height >= (startHeight + 9) ).toBeTruthy();
                    expect(width).toEqual(startWidth + 10);

                    jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {dx: -10, dy: -10});

                    height = parseFloat($target.css('height'));
                    width = parseFloat($target.css('width'));

                    expect(height >= (startHeight - 1)).toBeTruthy();
                    expect(width).toEqual(startWidth);
                });

                it("should create sizeable and be able to resize width only", function() {
                    var $target = jQuery('#standardAlert');
                    var $sizer = jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).removeClass('diagonal').addClass('horizontal');
                    var startHeight = parseFloat($target.css('height'));
                    var startWidth = parseFloat($target.css('width'));

                    layoutModule.createSizer($($target[0]));

                    jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {dx: 10, dy: 10});

                    var height = parseFloat($target.css('height'));
                    var width = parseFloat($target.css('width'));

                    expect(height).toEqual(startHeight);
                    expect(width).toEqual(startWidth + 10);

                    jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).simulate('drag', {dx: -10, dy: -10});

                    height = parseFloat($target.css('height'));
                    width = parseFloat($target.css('width'));

                    expect(height).toEqual(startHeight);
                    expect(width).toEqual(startWidth);

                    $sizer.removeClass('horizontal').addClass('diagonal');
                });

                it("should create sizeable and be able to resize height only", function() {
                    var $target = jQuery('#standardAlert');
                    var $sizer = jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN).removeClass('diagonal').addClass('vertical');
                    var startHeight = parseFloat($target.css('height'));
                    var startWidth = parseFloat($target.css('width'));

                    layoutModule.createSizer($($target[0]));

                    $sizer.simulate('drag', {dx: 10, dy: 10});

                    var height = parseFloat($target.css('height'));
                    var width = parseFloat($target.css('width'));

                    expect(height >= (startHeight + 9)).toBeTruthy();
                    expect(width).toEqual(startWidth);

                    $sizer.simulate('drag', {dx: -10, dy: -10});

                    height = parseFloat($target.css('height'));
                    width = parseFloat($target.css('width'));

                    expect(height >= (startHeight - 1)).toBeTruthy();
                    expect(width).toEqual(startWidth);

                    $sizer.removeClass('horizontal').addClass('diagonal');
                });

                it("should fire event after resizing", function() {
                    var $target = jQuery('#standardAlert');
                    var $sizer = jQuery('#standardAlert ' + layoutModule.SIZER_PATTERN);

                    spyOn(document, 'fire');
                    layoutModule.createSizer($($target[0]));

                    $sizer.simulate('drag', {dx: 10, dy: 10});

                    expect(document.fire).toHaveBeenCalled();
                    expect(document.fire.mostRecentCall.args[0]).toEqual('dragger:sizer');
                });
            });

            describe("Maximizer/minimizer", function() {
                var templateId = "maximinizable";
                var expectedWidth = 100;

                beforeEach(function() {
                    setTemplates(layoutText);
                    jQuery.cookie(templateId + layoutModule.MINIMIZED, true);
                });

                it("should maximize", function() {
                    var column = $(jQuery("#" + templateId)[0]);
                    jQuery.cookie(templateId + layoutModule.PANEL_WIDTH, expectedWidth);

                    layoutModule.maximize(column, true);

                    expect(column).toHasClass("maximized");

                    expect(column.style.width).toEqual(expectedWidth+'px');
                });

                // TODO this feature was disabled for some reason
                xit("should maximize with effects", function() {
                    var column = $(jQuery("#" + templateId)[0]);
                    jQuery.cookie(templateId + layoutModule.PANEL_WIDTH, expectedWidth);
                    spyOn(Effect, "Morph").andCallThrough();
                    spyOn(Effect, "Opacity").andCallThrough();

                    layoutModule.maximize(column, false);

                    expect(column.style.width).toEqual(expectedWidth + 'px');
                    expect(Effect.Morph).toHaveBeenCalled();
                    expect(Effect.Opacity).toHaveBeenCalled();
                });

                it("should minimize", function() {
                    var column = $(jQuery("#" + templateId)[0]);
                    column.style.width = expectedWidth + 'px';
                    jQuery.cookie(templateId + layoutModule.MINIMIZED, false);

                    layoutModule.minimize(column, true);

                    expect(column).toHasClass("minimized");
                    expect(column).not.toHasClass("minimized_if_vertical_orientation");
                });

                // TODO this feature was disabled for some reason
                xit("should minimize with effects", function() {
                    var column = $(jQuery("#" + templateId)[0]);
                    column.style.width = expectedWidth + 'px';
                    jQuery.cookie(templateId + layoutModule.MINIMIZED, false);

                    spyOn(Effect, "Morph").andCallThrough();
                    spyOn(Effect, "Opacity").andCallThrough();

                    layoutModule.minimize(column, false);

                    expect(column.style.width).toEqual(expectedWidth + 'px');
                    expect(Effect.Morph).toHaveBeenCalled();
                    expect(Effect.Opacity).toHaveBeenCalled();
                    expect(jQuery.cookie(templateId + layoutModule.MINIMIZED)).toBeTruthy();
                    expect(+jQuery.cookie(templateId + layoutModule.PANEL_WIDTH)).toEqual(expectedWidth);
                });

                it("should minimize left panel if it should be minimized", function() {
                    var leftPanel = $(jQuery("#" + templateId).removeClass("minimized").show()[0]);
                    leftPanel.style.width = expectedWidth + 'px';

                    jQuery.cookie(templateId + layoutModule.MINIMIZED, true);
                    jQuery.cookie(templateId + layoutModule.PANEL_WIDTH, expectedWidth);

                    spyOn(layoutModule, "minimize");

                    layoutModule.resizeOnClient(templateId, "main");

                    expect(layoutModule.minimize).toHaveBeenCalledWith(leftPanel, true);
                });

                it("cookies should have higher priority than class", function() {
                    var leftPanel = $(jQuery("#" + templateId).removeClass("minimized")[0]);
                    leftPanel.style.width = expectedWidth + 'px';

                    jQuery.cookie(templateId + layoutModule.MINIMIZED, true);
                    jQuery.cookie(templateId + layoutModule.PANEL_WIDTH, expectedWidth);

                    spyOn(layoutModule, "minimize");

                    layoutModule.resizeOnClient(templateId, "main");

                    expect(layoutModule.minimize).toHaveBeenCalledWith(leftPanel, true);
                });

                it("should maximize panel if it should be maximized", function() {
                    var leftPanel = $(jQuery("#" + templateId).removeClass("minimized")[0]);

                    spyOn(window, "JSCookie").andCallFake(function(name) {
                        if (name.indexOf(layoutModule.MINIMIZED) > 0) {
                            return {value: "false"};
                        }
                        return {value: expectedWidth};
                    });

                    var mainPanel = $(jQuery("#main")[0]);

                    layoutModule.resizeOnClient(templateId, "main");

                    expect(leftPanel.style.width).toEqual(expectedWidth + 'px');
                    expect(mainPanel.style.left).toEqual(expectedWidth + 'px');

                });

                it("should automaximize(cookies not set, has class)", function() {
                    spyOn(window, "JSCookie").andReturn({value: "false"});
                    spyOn(layoutModule, "maximize");

                    var leftPanel = $(jQuery("#" + templateId).addClass("minimized")[0]);

                    layoutModule.autoMaximize(leftPanel);

                    expect(layoutModule.maximize).not.toHaveBeenCalled();
                });

                it("should automaximize(cookies not set, class not set)", function() {
                    spyOn(window, "JSCookie").andReturn({value: "false"});
                    spyOn(layoutModule, "maximize");

                    var leftPanel = $(jQuery("#" + templateId).removeClass("minimized")[0]);

                    layoutModule.autoMaximize(leftPanel);

                    expect(layoutModule.maximize).not.toHaveBeenCalled();
                });

                it("should automaximize(cookies set, class not set)", function() {
                    spyOn(window, "JSCookie").andReturn({value: "true"});
                    spyOn(layoutModule, "maximize");

                    var leftPanel = $(jQuery("#" + templateId).removeClass("minimized")[0]);

                    layoutModule.autoMaximize(leftPanel);

                    expect(layoutModule.maximize).not.toHaveBeenCalled();
                });

                it("should automaximize(cookies not set, class set)", function() {
                    spyOn(window, "JSCookie").andReturn({value: "true"});
                    spyOn(layoutModule, "maximize");

                    var leftPanel = $(jQuery("#" + templateId).addClass("minimized")[0]);

                    layoutModule.autoMaximize(leftPanel);

                    expect(layoutModule.maximize).toHaveBeenCalled();
                });

                it("should autominimize(cookies not set)", function() {
                    spyOn(window, "JSCookie").andReturn({value: "false"});
                    spyOn(layoutModule, "minimize");

                    var leftPanel = $(jQuery("#" + templateId).addClass("minimized")[0]);

                    layoutModule.autoMinimize(leftPanel);

                    expect(layoutModule.minimize).toHaveBeenCalled();
                });

                it("should autominimize(cookies set)", function() {
                    spyOn(window, "JSCookie").andReturn({value: "true"});
                    spyOn(layoutModule, "minimize");

                    var leftPanel = $(jQuery("#" + templateId).addClass("minimized")[0]);

                    layoutModule.autoMinimize(leftPanel);

                    expect(layoutModule.minimize).not.toHaveBeenCalled();
                    expect(leftPanel).not.toHasClass('minimized');
                });

            });

        });

    });

