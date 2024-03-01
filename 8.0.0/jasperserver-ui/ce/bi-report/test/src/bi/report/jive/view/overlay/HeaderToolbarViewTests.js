/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
import sinon from 'sinon';
import HeaderToolbarView from 'src/bi/report/jive/view/overlay/HeaderToolbarView';
import $ from 'jquery';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('HeaderToolbarView Tests', function () {
    var headerToolbarView;
    beforeEach(function () {
        setTemplates('<div class=\'toolbar\'></div>');
        headerToolbarView = new HeaderToolbarView({
            parentElement: '.toolbar',
            buttons: [
                {
                    title: 'First',
                    icon: 'fire',
                    thirdArg: 'test'
                },
                {
                    title: 'Second',
                    icon: 'explode',
                    thirdArg: 'test'
                }
            ]
        });
    });
    afterEach(function () {
        headerToolbarView && headerToolbarView.remove();
        $('.toolbar').remove();
    });
    it('should be properly initialized', function () {
        var resetCurrentButtonSpy = sinon.spy(HeaderToolbarView.prototype, 'resetCurrentButton'), initEventsSpy = sinon.spy(HeaderToolbarView.prototype, 'initEvents'), setElementSpy = sinon.spy(HeaderToolbarView.prototype, 'setElement');
        var headerToolbarView = new HeaderToolbarView({
            buttons: [
                {
                    title: 'First',
                    icon: 'fire',
                    thirdArg: 'test'
                },
                {
                    title: 'Second',
                    icon: 'explode',
                    thirdArg: 'test'
                }
            ]
        });
        expect(resetCurrentButtonSpy).toHaveBeenCalled();
        expect(headerToolbarView.currentButton).toBe(null);
        expect(initEventsSpy).toHaveBeenCalled();
        expect(setElementSpy).toHaveBeenCalledWith(headerToolbarView.buttons.$el);
        expect(headerToolbarView.buttons).toBeDefined();
        expect(headerToolbarView.buttons.options.length).toEqual(2);
        headerToolbarView.remove();
        setElementSpy.restore();
        initEventsSpy.restore();
        resetCurrentButtonSpy.restore();
    });
    it('should render HeaderToolbarView', function () {
        expect(headerToolbarView.rendered).toBeFalsy();
        headerToolbarView.render();
        expect(headerToolbarView.rendered).toBeTruthy();
        expect($('.toolbar').find('button').length).toEqual(2);
    });
    it('should set currentButton on mouseOver event', function () {
        headerToolbarView.render();
        var $button = $('.toolbar').find('button').first();
        var buttonView = headerToolbarView.buttons.options[0];
        $button.trigger('mouseover');
        expect(headerToolbarView.currentButton).toEqual(buttonView);
        expect(buttonView.$el).toHaveClass('over');
    });
    it('should reset currentButton on mouseOut event', function () {
        headerToolbarView.render();
        var $button = $('.toolbar').find('button').first();
        var buttonView = headerToolbarView.buttons.options[0];
        $button.trigger('mouseover');
        expect(headerToolbarView.currentButton).toEqual(buttonView);
        expect(buttonView.$el).toHaveClass('over');
        $button.trigger('mouseout');
        expect(headerToolbarView.currentButton).toEqual(null);
        expect(buttonView.$el).not.toHaveClass('over');
    });
    it('should trigger select event', function () {
        headerToolbarView.render();
        var buttonSelectEvent = sinon.spy(headerToolbarView, 'trigger');
        var buttonView = headerToolbarView.buttons.options[0];
        headerToolbarView._onSelect('select', buttonView, buttonView.model);
        expect(buttonSelectEvent).toHaveBeenCalled();
        buttonSelectEvent.restore();
    });
    it('should add class pressed on mousedown event', function () {
        headerToolbarView.render();
        var $button = $('.toolbar').find('button').first();
        var buttonView = headerToolbarView.buttons.options[0];
        $button.trigger('mouseover');
        $button.trigger('mousedown');
        expect(buttonView.$el).toHaveClass('pressed');
    });
    it('should remove class pressed on mouseup event', function () {
        headerToolbarView.render();
        var $button = $('.toolbar').find('button').first();
        var buttonView = headerToolbarView.buttons.options[0];
        $button.trigger('mouseover');
        $button.trigger('mousedown');
        expect(buttonView.$el).toHaveClass('pressed');
        $button.trigger('mouseup');
        expect(buttonView.$el).not.toHaveClass('pressed');
    });
    it('should reset classes', function () {
        headerToolbarView.render();
        var $button = $('.toolbar').find('button').first();
        var buttonView = headerToolbarView.buttons.options[0];
        $button.trigger('mouseover');
        $button.trigger('mousedown');
        expect(buttonView.$el).toHaveClass('pressed');
        expect(buttonView.$el).toHaveClass('over');
        headerToolbarView.resetButtonsClasses();
        expect(buttonView.$el).not.toHaveClass('pressed');
        expect(buttonView.$el).not.toHaveClass('over');
    });
    it('should disable buttons', function () {
        headerToolbarView.render();
        var $button = $('.toolbar').find('button').first();
        var buttonView = headerToolbarView.buttons.options[0];
        $button.trigger('mouseover');
        $button.trigger('mousedown');
        expect(buttonView.$el).toHaveClass('pressed');
        expect(buttonView.$el).toHaveClass('over');
        headerToolbarView.disableButtons();
        expect(buttonView.$el).toHaveClass('disabled');
    });
    it('should show toolbar', function () {
        var resetButtonsClassesSpy = sinon.spy(headerToolbarView, 'resetButtonsClasses');
        headerToolbarView.render();
        headerToolbarView.hide();
        expect(headerToolbarView.$el.is(':visible')).toBeFalsy();
        headerToolbarView.show();
        expect(headerToolbarView.$el.is(':visible')).toBeTruthy();
        expect(resetButtonsClassesSpy).toHaveBeenCalled();
        resetButtonsClassesSpy.restore();
    });
});