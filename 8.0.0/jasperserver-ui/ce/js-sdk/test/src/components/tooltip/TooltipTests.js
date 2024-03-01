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
import _ from 'underscore';
import Tooltip from 'src/components/tooltip/Tooltip';
import tooltipTypesEnum from 'src/components/tooltip/enum/tooltipTypesEnum';
describe('Tooltip', function () {
    var tooltip, sandbox;
    beforeEach(function () {
        sandbox = sinon.createSandbox();
    });
    afterEach(function () {
        sandbox.restore();
    });
    describe('Constructor:', function () {
        it('should use provided view and model and initialize them', function () {
            var view = sinon.spy(), model = sinon.spy(), options = {
                    view: view,
                    model: model
                }, tooltip = new Tooltip(options);
            expect(view).toHaveBeenCalled();
            expect(model).toHaveBeenCalled();
            tooltip = null;
        });
        it('should set provided options ', function () {
            var options = {
                    placement: Tooltip.PLACEMENTS.LEFT,
                    content: 'Some text',
                    type: tooltipTypesEnum.ERROR,
                    offset: {
                        top: 5,
                        left: 5
                    }
                }, tooltip = new Tooltip(options);
            expect(tooltip.placement).toEqual(options.placement);
            expect(tooltip.content).toEqual({ text: options.content });
            expect(tooltip.type).toEqual(tooltipTypesEnum.ERROR);
            expect(tooltip.offset).toEqual(options.offset);
            tooltip.remove();
        });
        it('should read \'data-\' attributes if any in \'el\' and set them as options ', function () {
            var elem = $('<div data-jr-placement=\'top\' data-jr-offset=\'{"top": 10, "left":10}\' data-jr-type=\'error\' data-jr-content=\'Test\'></div>')[0], tooltip = new Tooltip({ el: elem });
            expect(tooltip.placement).toEqual(Tooltip.PLACEMENTS.TOP);
            expect(tooltip.content).toEqual({ text: 'Test' });
            expect(tooltip.type).toEqual(tooltipTypesEnum.ERROR);
            expect(tooltip.offset).toEqual({
                top: 10,
                left: 10
            });
            tooltip.remove();
        });
        it('should warn that both \'data-\' attrs and options are specified, prefer \'data-\' attrs over options', function () {
            var elem = $('<div data-jr-placement=\'top\' data-jr-content=\'Test\'></div>')[0], log = { warn: sinon.spy() }, tooltip = new Tooltip({
                log: log,
                el: elem,
                placement: Tooltip.PLACEMENTS.RIGHT,
                content: 'blah'
            });
            expect(log.warn).toHaveBeenCalledWith('The same options found both in constructor and in \'data-\' attrs. Don\'t use both');
            expect(tooltip.placement).toEqual(Tooltip.PLACEMENTS.TOP);
            expect(tooltip.content).toEqual({ text: 'Test' });
        });
    });
    describe('Positioning:', function () {
        describe('Calculation of tooltip tooltip position:', function () {
            var getTooltipPosition = function (extraOptions) {
                var options = {
                    placements: Tooltip.PLACEMENTS,
                    placement: Tooltip.PLACEMENTS.BOTTOM,
                    targetRect: {
                        top: 100,
                        left: 100,
                        width: 50,
                        height: 50
                    },
                    tooltipRect: {
                        width: 140,
                        height: 50
                    },
                    offset: {
                        top: 0,
                        left: 0
                    },
                    tooltipMargins: {
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 0
                    },
                    tooltipPaddings: {
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }
                };
                if (extraOptions) {
                    _.extend(options, extraOptions);
                }
                return Tooltip.getTooltipPosition(options);
            };
            it('Should calculate tooltip position with `bottom` placement by default', function () {
                expect(getTooltipPosition()).toEqual({
                    top: 150,
                    left: 55
                });
            });
            it('Should calculate tooltip position with `bottom` placement', function () {
                expect(getTooltipPosition({ placement: Tooltip.PLACEMENTS.BOTTOM })).toEqual({
                    top: 150,
                    left: 55
                });
            });
            it('Should calculate tooltip position with `top` placement', function () {
                expect(getTooltipPosition({ placement: Tooltip.PLACEMENTS.TOP })).toEqual({
                    top: 50,
                    left: 55
                });
            });
            it('Should calculate tooltip position with `left` placement', function () {
                expect(getTooltipPosition({ placement: Tooltip.PLACEMENTS.LEFT })).toEqual({
                    top: 100,
                    left: -40
                });
            });
            it('Should calculate tooltip position with `right` placement', function () {
                expect(getTooltipPosition({ placement: Tooltip.PLACEMENTS.RIGHT })).toEqual({
                    top: 100,
                    left: 150
                });
            });
            it('Should calculate tooltip position with `bottomLeft` placement', function () {
                expect(getTooltipPosition({ placement: Tooltip.PLACEMENTS.BOTTOM_LEFT })).toEqual({
                    top: 150,
                    left: 100
                });
            });
            it('Should calculate tooltip position with `bottomRight` placement', function () {
                expect(getTooltipPosition({ placement: Tooltip.PLACEMENTS.BOTTOM_RIGHT })).toEqual({
                    top: 150,
                    left: 10
                });
            });
            it('Should calculate tooltip position with `bottomRight` placement and custom offset', function () {
                expect(getTooltipPosition({
                    offset: {
                        top: 10,
                        left: 10
                    },
                    placement: Tooltip.PLACEMENTS.BOTTOM_RIGHT
                })).toEqual({
                    top: 160,
                    left: 20
                });
            });
        });
        it('should call position calculation after being shown', function () {
            var stubs = {};
            stubs._positionTooltip = sandbox.stub(Tooltip.prototype, '_positionTooltip');
            tooltip = new Tooltip({
                placement: Tooltip.PLACEMENTS.BOTTOM,
                content: 'blah-blah'
            });
            tooltip.$el.trigger('mouseenter');
            expect(stubs._positionTooltip).toHaveBeenCalledOnce();
            tooltip.remove();
        });
        it('should read data from target dom elem', function () {
            var elem = $('<div data-jr-placement=\'top\' data-jr-container=\'body\' data-jr-type=\'warning\' data-jr-offset=\'{"top": 10, "left": 10}\' data-jr-content=\'Test\'></div>')[0];
            expect(Tooltip.readTooltipDataFromDomElement(elem)).toEqual({
                content: 'Test',
                placement: 'top',
                offset: {
                    top: 10,
                    left: 10
                },
                type: tooltipTypesEnum.WARNING,
                container: 'body'
            });
        });
        it('should read data from target dom as object', function () {
            var elem = $('<div data-jr-placement=\'top\' data-jr-content=\'{"label": "Aaaa", "text": "Bbbb"}\'></div>')[0];
            expect(Tooltip.readTooltipDataFromDomElement(elem)).toEqual({
                content: {
                    label: 'Aaaa',
                    text: 'Bbbb'
                },
                placement: 'top',
                type: undefined,
                offset: undefined,
                container: undefined
            });
        });
        it('should have placements enum', function () {
            expect(Tooltip.PLACEMENTS).toBeTruthy();
        });
    });
    describe('Setters & Getters:', function () {
        beforeEach(function () {
            tooltip = new Tooltip({
                placement: Tooltip.PLACEMENTS.LEFT,
                content: 'Some text',
                log: { warn: sinon.spy() }
            });
        });
        afterEach(function () {
            if (tooltip) {
                tooltip.remove();
            }
        });
        it('shouldn\'t be shown if no content', function () {
            tooltip = new Tooltip({
                placement: Tooltip.PLACEMENTS.BOTTOM,
                content: ''
            });
            var tooltipPopupModel = tooltip.tooltipModel;
            sandbox.spy(tooltipPopupModel, 'set');
            tooltip.show();
            expect(tooltipPopupModel.set).not.toHaveBeenCalled();
            tooltip.remove();
            tooltip = null;
        });
        it('should set and get \'placement\' to/from tooltip', function () {
            expect(tooltip.placement).toEqual(tooltip.tooltipModel.get('placement'));
            tooltip.placement = Tooltip.PLACEMENTS.RIGHT;
            expect(tooltip.tooltipModel.get('placement')).toEqual(Tooltip.PLACEMENTS.RIGHT);
        });
        it('should set and get \'content\' to/from tooltip', function () {
            expect(tooltip.content).toEqual(tooltip.tooltipModel.get('content'));
            tooltip.content = 'blah';
            expect(tooltip.tooltipModel.get('content')).toEqual({ text: 'blah' });
        });
        it('should set and get \'type\' to/from tooltip', function () {
            tooltip.type = tooltipTypesEnum.ERROR;
            expect(tooltip.tooltipModel.get('type')).toEqual(tooltipTypesEnum.ERROR);
        });
        it('should set and get \'offset\' to/from tooltip', function () {
            tooltip.offset = {
                top: 10,
                left: 10
            };
            expect(tooltip.tooltipModel.get('offset')).toEqual({
                top: 10,
                left: 10
            });
        });
        it('should log a warning if content is an empty string', function () {
            tooltip.content = '';
            expect(tooltip.log.warn).toHaveBeenCalledWith('Can\'t find anything to display in \'content\', tooltip won\'t be shown');
        });
        it('should log a warning if content is an empty object without required fields', function () {
            tooltip.content = {};
            expect(tooltip.log.warn).toHaveBeenCalledWith('Can\'t find anything to display in \'content\', tooltip won\'t be shown');
        });
    });
    describe('Methods:', function () {
        var $element;
        beforeEach(function () {
            tooltip = new Tooltip({
                placement: Tooltip.PLACEMENTS.TOP,
                content: 'Test'
            });
            $element = $('<div></div>').appendTo('body').css({
                position: 'fixed',
                top: 20,
                left: 20,
                width: 100,
                height: 100
            });
        });
        afterEach(function () {
            tooltip.remove();
            $element.detach();
        });
        it('should be hidden by default', function () {
            expect(tooltip.tooltipView.$el).not.toBeVisible();
        });
        it('can \'show\' tooltip', function () {
            tooltip.setElement($element);
            tooltip.show();
            expect(tooltip.tooltipView.$el).toBeVisible();
        });
        it('can \'hide\' tooltip', function () {
            tooltip.setElement($element);
            tooltip.show();
            expect(tooltip.tooltipView.$el).toBeVisible();
            tooltip.hide();
            expect(!tooltip.tooltipView.$el).not.toBeVisible();
        });
    });
    describe('Events:', function () {
        var stubs = {};
        beforeEach(function () {
            stubs.showStub = sandbox.stub(Tooltip.prototype, 'show');
            stubs.hideStub = sandbox.stub(Tooltip.prototype, 'hide');
            tooltip = new Tooltip({
                placement: Tooltip.PLACEMENTS.BOTTOM,
                content: 'blah-blah'
            });
        });
        afterEach(function () {
            tooltip.remove();
        });
        it('should call \'show\' while user mouse over element', function () {
            tooltip.$el.trigger('mouseenter');
            expect(stubs.showStub).toHaveBeenCalledOnce();
            expect(stubs.hideStub).not.toHaveBeenCalled();
        });
        it('should trigger event \'show:tooltip\' while user mouse over element', function () {
            var handlerSpy = sandbox.spy();
            tooltip.on('show:tooltip', handlerSpy);
            tooltip.$el.trigger('mouseenter');
            expect(handlerSpy).toHaveBeenCalled();
        });
        it('can prevent tooltip from showing ', function () {
            tooltip.once('show:tooltip', function (evt) {
                evt.preventDefault();
            });
            tooltip.$el.trigger('mouseenter');
            expect(stubs.showStub).not.toHaveBeenCalled();
        });
        it('should call \'hide\' while user leave mouse from element', function () {
            tooltip.$el.trigger('mouseleave');
            expect(stubs.showStub).not.toHaveBeenCalled();
            expect(stubs.hideStub).toHaveBeenCalledOnce();
        });
        it('should trigger event \'hide:tooltip\' while user leave mouse from element', function () {
            var handlerSpy = sandbox.spy();
            tooltip.on('hide:tooltip', handlerSpy);
            tooltip.$el.trigger('mouseleave');
            expect(handlerSpy).toHaveBeenCalled();
        });
        it('can prevent tooltip from hiding', function () {
            tooltip.once('hide:tooltip', function (evt) {
                evt.preventDefault();
            });
            tooltip.$el.trigger('mouseleave');
            expect(stubs.hideStub).not.toHaveBeenCalled();
        });
    });
});