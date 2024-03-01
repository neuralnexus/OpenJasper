/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import $ from 'jquery';
import overlayTemplate from './template/overlayTemplate.htm';

var JiveOverlayView = Backbone.View.extend({
    el: overlayTemplate,
    events: { 'click': '_overlayClicked' },
    initialize: function (options) {
        var options = options || {};
        this.parentElement = options.parentElement ? $(options.parentElement) : $('body');
        this.rendered = false;
        Backbone.View.prototype.initialize.apply(this, arguments);
    },
    render: function () {
        this.parentElement.append(this.$el);
        this.rendered = true;
        return this;
    },
    css: function (options) {
        this.$el.css(options);
        return this;
    },
    setPosition: function (options) {
        this.$el.position(options);
        return this;
    },
    show: function () {
        this.$el.show();
        return this;
    },
    hide: function () {
        this.$el.hide();
        return this;
    },
    _overlayClicked: function () {
        this.trigger('overlayClicked');
    }
});
export default JiveOverlayView;