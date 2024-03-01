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

import Epoxy from 'backbone.epoxy';
import Backbone from 'backbone';
import ColorSelectorWrapper from '../component/colorPicker/react/ColorSelectorWrapper';
import _ from 'underscore';
import colorConvertUtil from '../component/colorPicker/util/colorConvertUtil';

function getAttrName($el, handlerName) {
    var selector = $el.data('bind'), pattern = new RegExp(handlerName + ':(\\w*)\\(?(\\w*)?\\)?');
    if (selector) {
        return _.last(_.compact(selector.match(pattern)));
    } else {
        return $el.data('bindAttribute');
    }
}
var bindingSettings = {
    optionText: 'label',
    optionValue: 'value',
    optionClass: 'className'
};
var isCollection = function (obj) {
    return obj instanceof Backbone.Collection;
};
var isModel = function (obj) {
    return obj instanceof Backbone.Model;
};
var isFunction = _.isFunction;
var isObject = _.isObject;

const TRANSPARENT = 'transparent';
const TRANSPARENT_LABEL = 'TRANSP';

Epoxy.binding.addHandler('optionsWithAdditionalProperties', {
    init: function ($element, value, context, bindings) {
        this.e = bindings.optionsEmpty;
        this.d = bindings.optionsDefault;
        this.v = bindings.value;
    },
    set: function ($element, value) {
        var self = this;
        var optionsEmpty = readAccessor(self.e);
        var optionsDefault = readAccessor(self.d);
        var currentValue = readAccessor(self.v);
        var options = isCollection(value) ? value.models : value;
        var numOptions = options.length;
        var enabled = true;
        var html = '';
        if (!numOptions && !optionsDefault && optionsEmpty) {
            html += self.opt(optionsEmpty, numOptions);
            enabled = false;
        } else {
            if (optionsDefault) {
                options = [optionsDefault].concat(options);
            }
            _.each(options, function (option, index) {
                html += self.opt(option, numOptions);
            });
        }
        $element.html(html).prop('disabled', !enabled).val(currentValue);
        if ($element[0].selectedIndex < 0 && $element.children().length) {
            $element[0].selectedIndex = 0;
        }
        var revisedValue = $element.val();
        if (self.v && !_.isEqual(currentValue, revisedValue)) {
            self.v(revisedValue);
        }
    },
    opt: function (option, numOptions) {
        var label = option;
        var value = option;
        var className = option;
        var textAttr = bindingSettings.optionText;
        var valueAttr = bindingSettings.optionValue;
        var classAttr = bindingSettings.optionClass;
        var optionClass;
        if (isObject(option)) {
            label = isModel(option) ? option.get(textAttr) : option[textAttr];
            value = isModel(option) ? option.get(valueAttr) : option[valueAttr];
            className = isModel(option) ? option.get(classAttr) : option[classAttr];
        }
        if (className) {
            optionClass = 'class="' + className + '"';
        } else {
            optionClass = '';
        }
        return [
            '<option value="',
            value,
            '"',
            optionClass,
            '>',
            label,
            '</option>'
        ].join('');
    }
});
Epoxy.binding.addHandler('validationErrorClass', {
    init: function ($element, value, bindings, context) {
        this.attr = getAttrName($element, 'validationErrorClass');
        var model = this.view.model;
        this._onAttrValidated = function (model, attr, error) {
            $element[error ? 'addClass' : 'removeClass']('error');
        };
        model.on('validate:' + this.attr, this._onAttrValidated);
    },
    get: function ($element, value, event) {
        return $element.val();
    },
    set: function ($element, value) {
        $element.val(value);
    },
    clean: function () {
        this.view.model.off('validate:' + this.attr, this._onAttrValidated);
    }
});
Epoxy.binding.addHandler('validationErrorText', {
    init: function ($element, value, bindings, context) {
        this.attr = getAttrName($element, 'validationErrorText');
        var model = this.view.model;
        this._onAttrValidated = function (model, attr, error) {
            $element.text(error || '');
        };
        model.on('validate:' + this.attr, this._onAttrValidated);
    },
    get: function ($element, value, event) {
        return $element.val();
    },
    set: function ($element, value) {
        $element.val(value);
    },
    clean: function () {
        this.view.model.off('validate:' + this.attr, this._onAttrValidated);
    }
});
Epoxy.binding.addFilter('escapeCharacters', {
    get: function (value) {
        return _.escape(value);
    },
    set: function (value) {
        return _.unescape(value);
    }
});
Epoxy.binding.addHandler('colorpicker', {
    init: function ($element, value, bindings, context) {
        var self = this,
            showTransparentInput = !!$element.data('showTransparentInput'),
            attr = getAttrName($element, 'colorpicker'),
            colorSelectorState = this._getColorSelectorState(value);
        this.colorPicker = new ColorSelectorWrapper($element[0], {
            showTransparentPreset: showTransparentInput,
            color: colorSelectorState.color,
            label: colorSelectorState.label,
            onColorChange: function(color){
                bindings[attr](self._convertColorForModel(color));
            }
        });
    },
    get: function ($element, value, event) {
        return $element.val();
    },
    set: function ($element, value) {
        let state = this._getColorSelectorState(value);
        this.colorPicker.setState(state);
    },
    clean: function () {
        this.colorPicker.remove();
    },
    _getColorSelectorState: function(value) {
        let label = colorConvertUtil.rgba2NoAlphaHex(value),
            color = label;

        if (colorConvertUtil.isRgbTransparent(value)) {
            label = TRANSPARENT_LABEL;
            color = TRANSPARENT;
        }

        return {
            label,
            color
        };
    },
    _convertColorForModel: function(color) {
        var rgb = color.rgb;

        return (`rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${rgb.a})`);
    }
});
Epoxy.binding.addHandler('radioDiv', {
    init: function ($element, value, bindings, context) {
        var attr = getAttrName($element, 'radioDiv');
        this.callback = function () {
            var value = $element.data('value');
            bindings[attr](value);
        };
        $element.on('click', _.bind(this.callback, this));
    },
    get: function ($element, value, event) {
        return $element.data('value');
    },
    set: function ($element, value) {
        var radioDivs = $element.siblings('div[data-bind*=\'radioDiv:\']');
        if ($element.data('value') === value) {
            $element.addClass('checked');
            $element.children('.radioChild').addClass('checked');
            radioDivs.removeClass('checked');
            radioDivs.children('.radioChild').removeClass('checked');
        }
    }
});
Epoxy.binding.addHandler('checkboxDiv', {
    init: function ($element, value, bindings, context) {
        var attr = getAttrName($element, 'checkboxDiv');
        this.callback = function () {
            bindings[attr](!bindings[attr]());
        };
        $element.on('click', _.bind(this.callback, this));
    },
    get: function ($element, value, event) {
        return $element.data('value');
    },
    set: function ($element, value) {
        if (value) {
            $element.addClass('checked');
            $element.children('.checkboxChild').addClass('checked');
        } else {
            $element.removeClass('checked');
            $element.children('.checkboxChild').removeClass('checked');
        }
    }
});
Epoxy.binding.addHandler('slide', function ($element, value) {
    $element[value ? 'slideDown' : 'slideUp']({
        complete: function () {
            !value && $element.hide();
        }
    });
});
Epoxy.binding.addHandler('selectionRange', {
    get: function ($element, value, event) {
        return {
            selectionRange: {
                start: $element[0].selectionStart,
                end: $element[0].selectionEnd
            }
        };
    },
    set: function ($element, value) {
        if ($element.is(':visible')) {
            $element[0].setSelectionRange(value.start, value.end);
            $element.focus();
        }
    }
});
Epoxy.binding.addFilter('prependText', function (value, text) {
    if (text.charAt(0) === '\'' && text.charAt(text.length - 1) === '\'') {
        text = text.slice(1, text.length - 1);
    }
    return text + ' ' + (_.isUndefined(value) ? '' : value);
});
function readAccessor(accessor) {
    if (isFunction(accessor)) {
        return accessor();
    } else if (isObject(accessor)) {
        accessor = _.clone(accessor);
        _.each(accessor, function (value, key) {
            accessor[key] = readAccessor(value);
        });
    }
    return accessor;
}
export default Epoxy;