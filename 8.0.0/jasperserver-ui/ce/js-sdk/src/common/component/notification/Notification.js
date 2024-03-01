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

import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';
import i18n from '../../../i18n/CommonBundle.properties';
import notificationTemplate from './template/notificationTemplate.htm';
var instance;

var NOTIFICATION_TYPES = {
    SUCCESS: 'success',
    WARNING: 'warning'
};
var NOTIFICATION_DEFAULT_DELAY = 2000;
var notificationTypeToCssClassMap = {};
notificationTypeToCssClassMap[NOTIFICATION_TYPES.WARNING] = NOTIFICATION_TYPES.WARNING;

const Notification = Backbone.View.extend({
    template: _.template(notificationTemplate),
    events: { 'click .close a': 'hide' },
    el: function () {
        return this.template({
            message: this.message,
            i18n: i18n
        });
    },
    initialize: function () {
        this.render();
    },
    render: function () {
        $('body').append(this.$el);
        this.$el.hide();
        this.$messageContainer = this.$('.notificationMessage > span:first-child');
        return this;
    },
    show: function (options) {
        options = _.extend({
            type: NOTIFICATION_TYPES.WARNING,
            delay: NOTIFICATION_DEFAULT_DELAY
        }, options);
        this.$messageContainer.text(options.message);
        this.$messageContainer.removeClass().attr({ 'class': notificationTypeToCssClassMap[options.type] });
        this.$el.slideDown();
        options.delay && _.delay(_.bind(this.hide, this), options.delay);
        return this;
    },
    hide: function () {
        arguments.length && arguments[0].preventDefault && arguments[0].preventDefault();
        this.$el.slideUp();
        return this;
    },
    remove: function () {
        Backbone.View.prototype.remove.apply(this, arguments);
    }
}, {
    show: function () {
        instance || (instance = new Notification());
        return instance.show.apply(instance, arguments);
    },
    hide: function () {
        instance || (instance = new Notification());
        return instance.hide.apply(instance, arguments);
    }
});

export default Notification;