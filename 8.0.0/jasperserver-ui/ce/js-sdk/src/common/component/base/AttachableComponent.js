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
import ClassUtil from '../../util/classUtil';
import positionUtil from './util/attachableComponentPositionUtil';

export default ClassUtil.extend({
    constructor: function (attachTo, padding) {
        this.padding = padding ? padding : {
            top: 5,
            left: 0
        };
        this.setAttachTo(attachTo);
    },
    setAttachTo: function (attachTo) {
        if (attachTo && $(attachTo).length > 0) {
            this.$attachTo = $(attachTo);
        } else {
            this.$attachTo = $('<div></div>');
        }
    },
    show: function () {
        const position = positionUtil.getPosition(this.$attachTo[0], this.padding, this.$el[0]);

        _.extend(this, {
            top: position.top,
            left: position.left
        });
        this.$el.css({
            top: this.top,
            left: this.left
        });
        this.$el.show();
        this.trigger('show', this);
    },
    hide: function () {
        this.$el.hide();
        this.trigger('hide', this);
        return this;
    }
});
