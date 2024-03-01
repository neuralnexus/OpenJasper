/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */


/**
 * @author: Narcis Marcu
 * @version: $Id$
 */

import $ from 'jquery';
import Backbone from 'backbone';
import columnResizeMarkerTemplate from './template/columnResizeMarkerTemplate.htm';

import "jquery-ui/ui/widgets/draggable";

export default Backbone.View.extend({

    el: columnResizeMarkerTemplate,

    constructor: function(options) {
        options = options || {};

        this.$parentElement = options.parentElement ? $(options.parentElement) : $("body");

        Backbone.View.apply(this, arguments);
    },

    initialize: function() {
        var self = this;

        Backbone.View.prototype.initialize.apply(this, arguments);

        this.$el.draggable({
            axis: "x",
            start: function(evt, ui) {
                self.trigger("marker:dragStart", evt, ui);
            },
            drag: function(evt, ui){
                self.trigger("marker:drag", evt, ui);
            },
            stop:function(evt, ui) {
                self.trigger("marker:dragStop", evt, ui);
            }
        });

        this.render();
    },

    render: function() {
        this.$parentElement.append(this.$el);
    },

    css: function(options){
        this.$el.css(options);
        return this;
    },

    setPosition: function(options){
        this.$el.position(options);
        return this;
    },

    show: function(){
        this.$el.show();
        this.$el.css("position", "absolute"); // force absolute, because jQuery seems to set it to relative

        return this;
    },

    hide: function(){
        this.$el.hide();

        return this;
    }

});
