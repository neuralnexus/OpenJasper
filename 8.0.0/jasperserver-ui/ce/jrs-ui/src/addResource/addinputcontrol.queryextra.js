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
 * @version: $Id$
 */
import jQuery from 'jquery'
import {matchAny} from "../util/utils.common";
import buttonManager from '../core/core.events.bis';
import layoutModule from '../core/core.layout';

var STEP_DISPLAY_ID = 'stepDisplay';
var WIZARD_NAV_ID = 'wizardNav';
var FRAME_ID = 'frame';
var VALUE_ID_PATTERN = '#value';
var LABEL_ID_PATTERN = '#labelID';
var ADD_PATTERN = '#add';
var REMOVE_PATTERN = '#remove';
var CONTROLS_PATTERN = '#controls';
var addListOfValues = {
    flowExecutionKey: '',
    messages: [],
    initialize: function () {
        this._stepDisplay = jQuery('#' + STEP_DISPLAY_ID)[0];
        this._wizardNav = jQuery('#' + WIZARD_NAV_ID)[0];
        this.initEvents();
    },
    initEvents: function () {
        jQuery('#' + FRAME_ID).on('click', function (event) {
            var elem = event.target;
            if (matchAny(elem, [ADD_PATTERN])) {
                jQuery('#ar').attr('name', '_eventId_addItem');
                jQuery('#extra')[0].submit();
            }
            if (elem.nodeName == 'A' && elem.identify() != 'add') {
                var id = elem.identify();
                jQuery('#itemToDelete')[0].setValue(id);
                jQuery('#ar').attr('name', '_eventId_removeItem');
                jQuery('#extra')[0].submit();
            }
        }.bindAsEventListener(this));
        jQuery('#' + FRAME_ID).on('keyup', function (event) {
            var elem = event.target;
            if (matchAny(elem, [
                VALUE_ID_PATTERN,
                LABEL_ID_PATTERN
            ])) {
                this.allowSubmit();
            }
        }.bindAsEventListener(this));
    },
    allowSubmit: function () {
        if (!jQuery('#labelID')[0].getValue().blank()) {
            buttonManager.enable('save');
            jQuery('#save').attr(layoutModule.DISABLED_ATTR_NAME, null);
        } else {
            buttonManager.disable('save');
            jQuery('#save').attr(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
        if (!jQuery('#value')[0].getValue().blank()) {
            buttonManager.enable('add');
            jQuery('#add').attr(layoutModule.DISABLED_ATTR_NAME, null);
        } else {
            buttonManager.disable('add');
            jQuery('#add').attr(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
    }
};

export default addListOfValues;