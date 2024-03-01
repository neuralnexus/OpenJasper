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

import $ from 'jquery';
import _ from 'underscore';
import domUtil from '../../common/util/domUtil';
import Backbone from 'backbone';

import log from "../../common/logging/logger";

import Event from '../utils/Event';
import TooltipView from './view/TooltipView';
import TooltipModel from './model/TooltipModel';
import placements from './enum/tooltipPlacements';

let logger = log.register("tooltip");

var Tooltip = Backbone.View.extend({
    events: {
        'mouseenter': '_onShow',
        'mouseleave': '_onHide'
    },
    initialize: function (options) {
        options = options || {};
        this.options = options;
        this.log = options.log || logger;
        var dataOptions = Tooltip.readTooltipDataFromDomElement(this.el);
        if (!_.isEmpty(dataOptions)) {
            if (Tooltip.areSomeKeysEqual(dataOptions, options)) {
                this.log.warn('The same options found both in constructor and in \'data-\' attrs. Don\'t use both');
            }
            options = _.extend(options, dataOptions);
        }
        this.$container = null;
        if (options.container) {
            this.$container = $(options.container);
        }
        var modelConstructor = options.model || TooltipModel;
        this.tooltipModel = new modelConstructor({
            offset: options.offset,
            placement: options.placement,
            type: options.type,
            content: Tooltip.convertContentToObject({
                value: options.content,
                log: this.log
            })
        });
        var viewConstructor = options.view || TooltipView;
        this.tooltipView = new viewConstructor({ model: this.tooltipModel });
    },
    remove: function () {
        this.stopListening();
        this.tooltipView.remove();
        return this;
    },
    show: function () {
        this._updateTooltipData();
        if (!this.tooltipModel.get('content') || _.isEmpty(this.tooltipModel.get('content'))) {
            return;
        }
        $('body').append(this.tooltipView.$el);
        this.tooltipModel.set({ visible: true });
        this._positionTooltip();
        if (this.$container && this.$container.length > 0) {
            this.$container.append(this.tooltipView.$el);
        } else {
            this.tooltipView.$el.insertAfter(this.$el);
        }
        this.tooltipView.position();
        return this;
    },
    hide: function () {
        this.tooltipModel.set({ visible: false });
        this.tooltipView.$el.detach();
        return this;
    },
    _updateTooltipData: function () {
        var attrData = Tooltip.readTooltipDataFromDomElement(this.el);
        if (attrData.container) {
            this.$container = $(attrData.container);
        }
        var updates = {};
        if (!_.isEmpty(attrData)) {
            if (attrData.placement) {
                updates.placement = attrData.placement;
            }
            if (attrData.content) {
                updates.content = Tooltip.convertContentToObject({
                    value: attrData.content,
                    log: this.log
                });
            }
            if (attrData.type) {
                updates.type = attrData.type;
            }
            if (attrData.offset) {
                updates.offset = attrData.offset;
            }
        }
        if (!_.isEmpty(updates)) {
            this.tooltipModel.set(updates);
        }
    },
    _onShow: function () {
        var event = new Event({ name: 'show:tooltip' });
        this.trigger(event.name, event);
        if (!event.isDefaultPrevented()) {
            this.show();
        }
    },
    _onHide: function () {
        var event = new Event({ name: 'hide:tooltip' });
        this.trigger(event.name, event);
        if (!event.isDefaultPrevented()) {
            this.hide();
        }
    },
    _positionTooltip: function () {
        var position, options = {
            placements: placements,
            placement: this.tooltipModel.get('placement'),
            offset: this.tooltipModel.get('offset'),
            targetRect: this._getPosition(this.$el),
            tooltipRect: this._getPosition(this.tooltipView.$el),
            tooltipMargins: domUtil.getMargins(this.tooltipView.$el),
            tooltipPaddings: domUtil.getPaddings(this.tooltipView.$el)
        };
        position = Tooltip.getTooltipPosition(options);
        this.tooltipModel.set('position', position);
    },
    _getPosition: function ($element) {
        var el = $element[0];
        var isBody = el.tagName === 'BODY';
        var elRect = el.getBoundingClientRect();
        var isSvg = window.SVGElement && el instanceof window.SVGElement;
        var elOffset = isBody ? {
            top: 0,
            left: 0
        } : isSvg ? null : $element.offset();
        var scroll = { scroll: isBody ? document.documentElement.scrollTop || document.body.scrollTop : $element.scrollTop() };
        var outerDims = isBody ? {
            width: $(window).width(),
            height: $(window).height()
        } : null;
        return _.extend({}, elRect, scroll, outerDims, elOffset);
    }
}, {
    PLACEMENTS: placements,
    getTooltipPosition: function (options) {
        var top = 0, left = 0, placements = options.placements, placement = options.placement, targetRect = options.targetRect, tooltipRect = options.tooltipRect, offset = options.offset, tooltipMargins = options.tooltipMargins, tooltipPaddings = options.tooltipPaddings;
        switch (placement) {
        case placements.TOP:
            top = targetRect.top - tooltipRect.height;
            left = targetRect.left + targetRect.width / 2 - tooltipRect.width / 2;
            break;
        case placements.LEFT:
            top = targetRect.top + targetRect.height / 2 - tooltipRect.height / 2;
            left = targetRect.left - tooltipRect.width;
            break;
        case placements.RIGHT:
            top = targetRect.top + targetRect.height / 2 - tooltipRect.height / 2;
            left = targetRect.left + targetRect.width;
            break;
        case placements.BOTTOM:
            top = targetRect.top + targetRect.height;
            left = targetRect.left + targetRect.width / 2 - tooltipRect.width / 2;
            break;
        case placements.BOTTOM_LEFT:
            top = targetRect.top + targetRect.height;
            left = targetRect.left - tooltipPaddings.left;
            break;
        case placements.BOTTOM_RIGHT:
            top = targetRect.top + targetRect.height;
            left = targetRect.left + targetRect.width - tooltipRect.width + tooltipPaddings.right;
            break;
        }
        top += tooltipMargins.top;
        left += tooltipMargins.left;
        top += offset.top;
        left += offset.left;
        top = Math.floor(top);
        left = Math.floor(left);
        return {
            top: top,
            left: left
        };
    },
    readTooltipDataFromDomElement: function (elem) {
        var $element = $(elem);
        $element.removeData('jrContent');
        $element.removeData('jrPlacement');
        $element.removeData('jrType');
        $element.removeData('jrOffset');
        $element.removeData('jrContainer');
        var content = $element.data('jrContent'), placement = $element.data('jrPlacement'), type = $element.data('jrType'), offset = $element.data('jrOffset'), container = $element.data('jrContainer'), result;
        if (content || placement || type || offset || container) {
            result = {
                placement: placement,
                content: content,
                type: type,
                offset: offset,
                container: container
            };
        } else {
            result = {};
        }
        return result;
    },
    areSomeKeysEqual: function (obj1, obj2) {
        return _.intersection(Object.keys(obj1), Object.keys(obj2)).length > 0;
    },
    convertContentToObject: function (options) {
        options = options || {};
        var value = options.value || {}, log = options.log, isEmptyString = _.isString(value) && value.length === 0, hasOneOfNessesaryProperties = !_.isUndefined(value.label) || !_.isUndefined(value.text), isObjectWithoutNessesaryProperties = _.isObject(value) && !hasOneOfNessesaryProperties;
        if (_.isUndefined(value) || isEmptyString || isObjectWithoutNessesaryProperties) {
            log && log.warn('Can\'t find anything to display in \'content\', tooltip won\'t be shown');
        } else {
            if (!_.isObject(value) && _.isString(value)) {
                value = { text: value };
            }
        }
        return value;
    }
});
Tooltip.prototype = _.extend({
    get placement() {
        return this.tooltipModel.get('placement');
    },
    set placement(value) {
        this.tooltipModel.set('placement', value);
    },
    get content() {
        return this.tooltipModel.get('content');
    },
    set content(value) {
        this.tooltipModel.set('content', Tooltip.convertContentToObject({
            value: value,
            log: this.log
        }), { validate: true });
    },
    get type() {
        return this.tooltipModel.get('type');
    },
    set type(value) {
        this.tooltipModel.set('type', value);
    },
    get offset() {
        return this.tooltipModel.get('offset');
    },
    set offset(offset) {
        var currentOffset = this.tooltipModel.get('offset');
        offset = _.extend({}, currentOffset, offset);
        this.tooltipModel.set('offset', offset, { validate: true });
    }
}, Tooltip.prototype);
export default Tooltip;