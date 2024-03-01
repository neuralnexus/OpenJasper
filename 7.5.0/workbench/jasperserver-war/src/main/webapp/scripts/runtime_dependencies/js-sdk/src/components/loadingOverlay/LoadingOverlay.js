define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

var overlayLayout = require("text!./template/overlayLayout.htm");

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
module.exports = Backbone.View.extend({
  template: _.template(overlayLayout),
  initialize: function initialize(options) {
    this.delay = options.delay;
    this.render();
  },
  render: function render() {
    this.$el.append(this.template());
    this.$elSpinner = this.$('.jr-mSpinnerDatatable');
    this.$elOverlay = this.$('.jr-mOverlay');
    return this;
  },
  show: function show(delay) {
    var self = this,
        show = function show() {
      self.$elSpinner.show();
      self.$elOverlay.show();
    };

    if (this.delay || delay) {
      if (!this._timer) {
        this._timer = setTimeout(show, this.delay || delay);
      }
    } else show();
  },
  hide: function hide() {
    if (this._timer) {
      clearTimeout(this._timer);
      this._timer = null;
    }

    this.$elSpinner.hide();
    this.$elOverlay.hide();
  },
  remove: function remove() {
    this.$elSpinner.remove();
    this.$elOverlay.remove();
    this.stopListening();
    return this;
  }
});

});