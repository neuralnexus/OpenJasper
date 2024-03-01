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
import logger from "../../logging/logger";
import stdnav from '../stdnav';

let localLogger = logger.register("stdnavPluginGrid");

var version = '0.0.1';
var singleton = null;
var gserial = 0;
var stdnavPluginGrid = function () {
    gserial++;
    this.serial = gserial;
};
$.extend(stdnavPluginGrid.prototype, {
    zinit: function (selector) {
        localLogger.debug('stdnavPluginGrid.init(' + selector + ')\n');
        return this;
    },
    activate: function () {
        this.behavior = {
            'focusin': [
                this,
                stdnav.basicFocusIn,
                {
                    'maxdepth': 2,
                    'subfocusclass': 'gridcell'
                }
            ],
            'focusout': [
                this,
                stdnav.ghostFocusOut,
                null
            ],
            'subfocusin': [
                this,
                this._onSubfocusIn,
                null
            ],
            'left': [
                this,
                this._onLeft,
                null
            ],
            'right': [
                this,
                this._onRight,
                null
            ],
            'up': [
                this,
                this._onUp,
                null
            ],
            'down': [
                this,
                this._onDown,
                null
            ],
            'hoverin': [
                this,
                this._onHover,
                null
            ],
            'inherit': true,
            'inheritable': true
        };
        stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
    },
    deactivate: function () {
        stdnav.unregisterNavtype(this.navtype, this.behavior);
    },
    _findSubfocus: function (el) {
        var grid = $(el).closest('.grid');
        var sfels = grid.find('.subfocus');
        if (sfels !== undefined) {
            return $(sfels[0]);
        }
    },
    _fixSubfocus: function (element) {
        var ghosts;
        var newSubfocus;
        if (element.hasClass('grid')) {
            ghosts = element.children('.gridrow').children('.gridcell .ghostfocus');
            if (ghosts.length > 0) {
                newSubfocus = ghosts[0];
            } else {
                var rows = element.children('.gridrow');
                if (rows.length > 0) {
                    var cells = $(rows[0]).children('.gridcell');
                    if (cells.length > 0) {
                        newSubfocus = cells[0];
                    } else {
                        newSubfocus = rows[0];
                    }
                } else {
                    newSubfocus = element;
                }
            }
        } else if (element.hasClass('gridrow')) {
            ghosts = element.children('.gridcell .ghostfocus');
            newSubfocus = ghosts[0];
        } else if (element.hasClass('gridcell')) {
            newSubfocus = element;
        }
        return newSubfocus;
    },
    _onSubfocusIn: function (element) {
        if ($(element).hasClass('gridcell') === false) {
            var subel = this._fixSubfocus($(element));
            stdnav.setSubfocus(subel, false);
        }
    },
    _onLeft: function (element) {
        var sfel = this._findSubfocus(element);
        var newsf = $(false);
        if (sfel.hasClass('gridcell')) {
            newsf = sfel.prev('.gridcell');
        }
        if (newsf.length === 1) {
            stdnav.setSubfocus(newsf);
        }
        return false;
    },
    _onRight: function (element) {
        var sfel = this._findSubfocus(element);
        var newsf = $(false);
        if (sfel.hasClass('gridcell')) {
            newsf = sfel.next('.gridcell');
        }
        if (newsf.length === 1) {
            stdnav.setSubfocus(newsf);
        }
        return false;
    },
    _onUp: function (element) {
        var sfel = this._findSubfocus(element);
        var row = $(false);
        var newrow = $(false);
        var newsf = $(false);
        if (sfel.hasClass('gridcell')) {
            row = sfel.closest('.gridrow');
            if (row.length === 1) {
                newrow = row.prev('.gridrow');
            }
            if (newrow.length === 1) {
                var iter = sfel;
                var col = -1;
                do {
                    col++;
                    iter = $(iter.prev('.gridcell')[0]);
                } while (iter.length > 0);
                newsf = $(newrow.find('.gridcell')[0]);
                while (col > 0) {
                    iter = newsf.next('.gridcell');
                    if (iter.length > 0) {
                        newsf = $(iter[0]);
                    }
                    col--;
                }
            }
        }
        if (newsf.length === 1) {
            stdnav.setSubfocus(newsf);
        }
        return false;
    },
    _onDown: function (element) {
        var sfel = this._findSubfocus(element);
        var row = $(false);
        var newrow = $(false);
        var newsf = $(false);
        if (sfel.hasClass('gridcell')) {
            row = sfel.closest('.gridrow');
            if (row.length > 0) {
                newrow = row.next('.gridrow');
            }
            if (newrow.length > 0) {
                var iter = sfel;
                var col = -1;
                do {
                    col++;
                    iter = $(iter.prev('.gridcell')[0]);
                } while (iter.length > 0);
                newsf = $(newrow.find('.gridcell')[0]);
                while (col > 0) {
                    iter = newsf.next('.gridcell');
                    if (iter.length > 0) {
                        newsf = $(iter[0]);
                    }
                    col--;
                }
            }
        }
        if (newsf.length === 1) {
            stdnav.setSubfocus(newsf);
        }
        return false;
    }
});
$.extend(stdnavPluginGrid.prototype, {
    navtype: 'grid',
    navtype_tags: []
});
var newStdnavPluginGrid = new stdnavPluginGrid();
export default newStdnavPluginGrid;