/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import Epoxy from 'backbone.epoxy';
import getResetColorBinding from './bindigs/resetColorBinding';
import getColorPickerBinding from './bindigs/colorPickerBinding';
import datePickerBindingHandler from './dateTimePickerEpoxyBindingHandler';

export default Epoxy.View.extend({
    constructor: function (options) {
        this.i18n = options.i18n;
        Epoxy.View.prototype.constructor.call(this, options);
    },
    bindingHandlers: {
        radioDiv: {
            init: function ($element, value, bindings, context) {
                var modelBinding = $element.data('model-attr');

                this.$el = $element;

                let callback = function (evt) {
                    bindings[modelBinding]($element.data('value'));
                };

                this.callback = _.bind(callback, this);

                this.$el.on('click', this.callback);
            },
            set: function ($element, value) {
                var radioDivs = $element.siblings('div[data-bind*=\'radioDiv\']');
                if ($element.data('value') === value) {
                    $element.addClass('selected');
                    radioDivs.removeClass('selected');
                }
            },
            clean: function () {
                this.$el.off('click', this.callback);
            }
        },
        checkboxDiv: {
            init: function ($element, value, bindings, context) {
                var modelBinding = $element.data('model-attr');

                this.$el = $element;
                this.isTrippleState = !!this.$el.data('tripplestate');

                let callback = function (evt) {
                    bindings[modelBinding](this._get($element));
                };

                this.callback = _.bind(callback, this);

                this.$el.on('click', this.callback);
            },
            set: function ($element, value) {
                if (value === true) {
                    $element.removeClass('unchanged').addClass('selected');
                } else if (value === false) {
                    $element.removeClass('unchanged').removeClass('selected');
                } else {
                    $element.removeClass('selected').addClass('unchanged');
                }
            },
            _get: function ($element) {
                if (this.isTrippleState) {
                    if ($element.is('.unchanged')) {
                        return true;
                    } else if ($element.is('.selected')) {
                        return false;
                    } else {
                        return null;
                    }
                } else {
                    return !$element.is('.selected');
                }
            },
            clean: function () {
                this.$el.off('click', this.callback);
            }
        },
        resetColor: getResetColorBinding(),
        colorpicker: getColorPickerBinding(),
        dateTimePicker: datePickerBindingHandler
    }
});