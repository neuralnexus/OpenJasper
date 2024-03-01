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
import Backbone from 'backbone';
import tooltipTemplate from './template/tooltipTemplate.htm';
export default Backbone.View.extend({
    template: _.template(tooltipTemplate),
    delay: 500,
    offset: {
        x: 10,
        y: 10
    },
    attribute: 'data-tooltip',
    cssClasses: 'panel info tooltip',
    contentSelector: '.body',
    triggerEvents: false,
    el: function () {
        return this.template({ cssClasses: this.cssClasses });
    },
    constructor: function (options) {
        options || (options = {});
        if (!options.attachTo || $(options.attachTo).length === 0) {
            throw new Error('Tooltip should be attached to an element');
        }
        if (!options.contentTemplate) {
            throw new Error('Tooltip should have contentTemplate');
        }
        this._shown = false;
        this.$attachTo = $(options.attachTo);
        this.$attachTo[0].tooltip = this;
        options.delay && (this.delay = options.delay);
        options.offset && (this.offset = options.offset);
        options.attribute && (this.attribute = options.attribute);
        options.cssClasses && (this.cssClasses = options.cssClasses);
        this.contentTemplate = options.contentTemplate;
        this.i18n = options.i18n || {};
        !_.isUndefined(options.triggerEvents) && (this.triggerEvents = options.triggerEvents);
        this.selector = '[' + this.attribute + ']';
        _.bindAll(this, 'show', '_onMouseMove', '_onMouseLeave');
        this.$attachTo.on('mousemove touchmove', this.selector, this._onMouseMove);
        this.$attachTo.on('mouseleave touchend', this.selector, this._onMouseLeave);
        Backbone.View.prototype.constructor.apply(this, arguments);
    },
    _render: function (model, options, dfd) {
        if (this._event) {
            model = model instanceof Backbone.Model ? model.toJSON() : model;
            var content = _.template(this.contentTemplate)({
                model: model,
                i18n: this.i18n,
                options: options || {}
            });
            this.$el.find('.body').empty().append(content);
            this._showTooltip();
        }
        dfd.resolve();
    },
    show: function (model, options) {
        var dfd = new $.Deferred();
        this._timer = setTimeout(_.bind(this._render, this, model, options, dfd), this.delay);
        return dfd;
    },
    _showTooltip: function () {
        $('body').append(this.$el);
        this._updatePosition();
        this._shown = true;
        this.triggerEvents && this.trigger('show', this);
    },
    hide: function () {
        this.contentTemplate && this._timer && clearTimeout(this._timer);
        this.$el.detach();
        this._shown = false;
        this.triggerEvents && this.trigger('hide', this);
    },
    _updatePosition: function () {
        var tooltipHeight = this.$el.innerHeight(), tooltipWidth = this.$el.innerWidth(), windowHeight = $(window).height(), windowWidth = $(window).width(), shiftY = this.offset.y, shiftX = this.offset.x;
        if (tooltipHeight + this._event.pageY + this.offset.y + 3 >= windowHeight) {
            shiftY = -this.offset.y - tooltipHeight;
        }
        if (tooltipWidth + this._event.pageX + this.offset.x + 3 >= windowWidth) {
            shiftX = -this.offset.x - tooltipWidth;
        }
        this.$el.css({
            top: this._event.pageY + shiftY,
            left: this._event.pageX + shiftX
        });
    },
    _onMouseMove: function (event) {
        this._event = event;
    },
    _onMouseLeave: function () {
        this._shown && this.hide();
        this._timer && clearTimeout(this._timer);
        this._event = null;
    },
    remove: function () {
        this._shown && this.hide();
        this._timer && clearTimeout(this._timer);
        this.$attachTo.off('mousemove touchmove', this.selector, this._onMouseMove);
        this.$attachTo.off('mouseleave touchend', this.selector, this._onMouseLeave);
        Backbone.View.prototype.remove.apply(this, arguments);
    }
}, {
    attachTo: function (container, options) {
        var Tooltip = this;
        options || (options = {});
        options.attachTo = container || 'body';
        return new Tooltip(options);
    },
    detachFrom: function (container) {
        container = $(container)[0];
        container.tooltip && container.tooltip.remove();
    }
});