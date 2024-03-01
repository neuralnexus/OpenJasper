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
export default {
    doCalcOnVisibleNodeClone: function (options) {
        if (!options || !options.el) {
            throw 'Missing required option: el';
        }
        _.defaults(options, {
            css: {},
            classes: '',
            container: '<div></div>',
            appendTo: 'body',
            callback: function () {
                throw 'no callback was defined';
            },
            alwaysClone: false,
            cloneHandlers: false
        });
        _.defaults(options.css, {
            'position': 'absolute',
            'left': '-9999px'
        });
        if (!options.alwaysClone && $(options.el).is(':visible')) {
            options.callback($(options.el));
        } else {
            var $el = $(options.el).clone();
            var $tmp = $(options.container).css(options.css).addClass(options.classes).appendTo($(options.appendTo)).append($el);
            options.callback($el);
            $tmp.remove();
        }
    }
};