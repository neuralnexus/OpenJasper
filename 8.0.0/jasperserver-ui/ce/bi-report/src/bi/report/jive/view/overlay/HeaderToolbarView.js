/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import $ from 'jquery';
import _ from 'underscore';
import OptionContainer from 'js-sdk/src/common/component/base/OptionContainer';
import HoverMenu from 'js-sdk/src/common/component/menu/HoverMenu';
import cascadingMenuTrait from 'js-sdk/src/common/component/menu/cascadingMenuTrait';
import headerToolbarTemplate from './template/headerToolbarTemplate.htm';
import headerToolbarButtonTemplate from './template/headerToolbarButtonTemplate.htm';
import dropdownMenuTemplate from './template/dropdownMenuTemplate.htm';
import dropdownOptionTemplate from './template/dropdownOptionTemplate.htm';

var CascadingHoverMenu = HoverMenu.extend(cascadingMenuTrait);
var HeaderToolbarView = Backbone.View.extend({
    events: {
        'mousedown button': '_onMouseTouchDown',
        'touchstart button': '_onMouseTouchDown',
        'mouseup button': '_onMouseTouchUp',
        'touchend button': '_onMouseTouchUp'
    },
    initialize: function (options) {
        options = options || {};
        this.parentElement = options.parentElement ? $(options.parentElement) : $('body');
        var buttons = options.buttons || [];
        this.buttons = new OptionContainer({
            contextName: 'button',
            mainTemplate: headerToolbarTemplate,
            contentContainer: '.jive_button_bar_options',
            optionTemplate: headerToolbarButtonTemplate,
            options: buttons
        });
        this.resetCurrentButton();
        this.initEvents();
        this.initHoverMenus();
        this.setElement(this.buttons.$el);
        this.rendered = false;
        Backbone.View.prototype.initialize.apply(this, arguments);
    },
    initEvents: function () {
        this.listenTo(this.buttons, 'mouseover', this._onMouseOver);
        this.listenTo(this.buttons, 'mouseout', this._onMouseOut);
        this.listenTo(this.buttons, 'button:select button:sortAsc button:sortDesc button:filter', this._onSelect);
    },
    initHoverMenus: function () {
        this.cascadingMenus = [];
        _.each(this.buttons.options, function (option) {
            var hoverMenuOptions = option.model.get('hoverMenuOptions');
            if (hoverMenuOptions) {
                var cascadingMenu = new CascadingHoverMenu(hoverMenuOptions, option.$el, null, {
                    menuContainerTemplate: dropdownMenuTemplate,
                    menuOptionTemplate: dropdownOptionTemplate
                });
                this.listenTo(cascadingMenu, 'option:select', this._onSelect);
                this.cascadingMenus.push(cascadingMenu);
            }
        }, this);
    },
    _onSelect: function (buttonView, buttonModel, e) {
        this.trigger('select', buttonView, buttonModel, e);
    },
    _onMouseTouchDown: function (e) {
        if (this.currentButton) {
            var buttonView = this.currentButton;
            !buttonView.$el.hasClass('disabled') && buttonView.$el.addClass('pressed');
        }
        return false;
    },
    _onMouseTouchUp: function (e) {
        if (this.currentButton) {
            this.currentButton.$el.removeClass('pressed');
        }
        return false;
    },
    _onMouseOver: function (buttonView, buttons, model) {
        this.resetCurrentButton(buttonView);
        !buttonView.$el.hasClass('disabled') && buttonView.$el.addClass('over');
    },
    _onMouseOut: function (buttonView, buttons, model) {
        this.resetCurrentButton();
        buttonView.$el.removeClass('over pressed');
    },
    resetCurrentButton: function (buttonView) {
        this.currentButton = buttonView ? buttonView : null;
    },
    resetButtonsClasses: function () {
        this.$el.find('button').removeClass('over pressed disabled');
    },
    disableButtons: function () {
        this.$el.find('button').addClass('disabled');
    },
    setPosition: function (options) {
        this.$el.position(options);
        return this;
    },
    show: function (disable) {
        this.resetButtonsClasses();
        !disable && this.disableButtons();
        this.$el.show();
        return this;
    },
    hide: function () {
        this.$el.hide();
        return this;
    },
    render: function () {
        this.parentElement.append(this.$el);
        this.rendered = true;
        return this;
    },
    remove: function () {
        Backbone.View.prototype.remove.apply(this, arguments);
        this.rendered = false;
        this.buttons && this.buttons.remove();
        _.invoke(this.cascadingMenus, 'remove');
    }
});
export default HeaderToolbarView;