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
import {Form} from 'prototype';
import resourceLocator from './resource.locate';
import buttonManager from '../core/core.events.bis';
import jQuery from 'jquery';

var inputControl = {
    messages: [],
    initialize: function () {
        this.form = jQuery('input[name=_flowExecutionKey]').parent('form');
        this.defineRadio = jQuery('#LOCAL')[0];
        this.resourceUriInput = jQuery('#resourceUri')[0];
        try {
            this.resourcePicker();
            this.updateButtonsState();
        } finally {
            this.initEvents();
        }
    },
    resourcePicker: function () {
        resourceLocator.initialize({
            resourceInput: 'resourceUri',
            browseButton: 'browser_button',
            treeId: 'inputControlTreeRepoLocation',
            providerId: 'inputControlResourceTreeDataProvider',
            dialogTitle: inputControl.messages['InputControlLocate.Title'],
            selectLeavesOnly: true
        });
    },
    initEvents: function () {
        this.form.length && new Form.Observer(this.form[0], 0.3, function () {
            this.updateButtonsState();
        }.bind(this));
    },
    updateButtonsState: function () {
        if (!this.resourceUriInput.getValue().blank() || this.defineRadio.getValue() === 'LOCAL') {
            buttonManager.enable('next');
        } else {
            buttonManager.disable('next');
        }
    }
};

export default inputControl;
