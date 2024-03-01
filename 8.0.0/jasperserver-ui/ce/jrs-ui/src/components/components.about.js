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
/**
 * @author: Yuriy Plakosh
 * @version: $Id$
 */
import jQuery from 'jquery';
import dialogs from './components.dialogs';

var about = {
    /**
     * Initialize about module.
     */
    initialize: function () {
        about.aboutBox.registerListeners();
    }
};
///////////////////////////
// About box component
///////////////////////////
about.aboutBox = {
    /**
     * Shows about box.
     */
    show: function () {
        var dom = jQuery('#aboutBox');
        if (dom.hasClass('hidden')) {
            dialogs.popup.show(dom[0], true);
        }
    },
    _hide: function () {
        var dom = jQuery('#aboutBox');
        if (!dom.hasClass('hidden')) {
            dialogs.popup.hide(dom[0]);
        }
    },

    registerListeners : function() {
        var $about = jQuery("#about"),
            $aboutBoxCloseButton = jQuery("#aboutBox button");

        $about.on('click', function(e) {
            e.preventDefault();
            about.aboutBox.show();
        });

        $aboutBoxCloseButton.on('click', function(e) {
            about.aboutBox._hide();
            e.stopPropagation();
        });

    }
};

export default about;