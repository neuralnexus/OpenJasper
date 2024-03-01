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
 * @author: Sergey Prilukin
 * @version: $Id$
 */
import buttonManager from '../core/core.events.bis';
import _ from 'underscore';
import layoutModule from '../core/core.layout';
import dynamicTree from '../dynamicTree/dynamicTree.utils';
import dialogs from './components.dialogs';
import {matchAny} from '../util/utils.common';
import jQuery from 'jquery';

var picker = {};    /*
 * File/Folder Selector box.
 *
 * @param options {JSON Object} - Set of options for folder selector:
 * <ul>
 *       <li>id - Dialog template id, by default 'selectFromRepository'</li>
 *       <li>disabled - Wether file picker is disabled. By defailt is false</li>
 *       <li>suffix - Unique suffix to be able to use multiple dialogs, by default unique number - current time</li>
 *       <li>treeId - Dom id of tree container. The option is required</li>
 *       <li>selectLeavesOnly - Wether only tree leaves should be selectable.
 *                          Set to true for file selection and false for folder selection by default false</li>
 *       <li>title - Title of dialog. Not required. If not setted - title will not be changed</li>
 *       <li>onOk - If setted will be called after select button is clicked. Not required</li>
 *       <li>onCancel - If setted will be called after cancel button clicked. not required</li>
 *       <li>uriTextboxId - Dom id of textbox where uri is placed. Required.</li>
 *       <li>browseButtonId - Dom id of button which should opens file selector dialog. Required.</li>
 *       <li>providerId - Tree data provider id. The option is required.</li>
 *       <li>treeOptions - Additional tree params like rootUri, bShowRoot etc. Not required.</li>
 * </ul>
 *
 * Minimal usage example:
 * <pre>
 *  new picker.FileSelector({
 *       treeId: treeId,
 *       providerId: providerId,
 *       uriTextboxId: uriTextboxId,
 *       browseButtonId: browseButtonId
 *   })
 * </pre>
 *
 */
/*
 * File/Folder Selector box.
 *
 * @param options {JSON Object} - Set of options for folder selector:
 * <ul>
 *       <li>id - Dialog template id, by default 'selectFromRepository'</li>
 *       <li>disabled - Wether file picker is disabled. By defailt is false</li>
 *       <li>suffix - Unique suffix to be able to use multiple dialogs, by default unique number - current time</li>
 *       <li>treeId - Dom id of tree container. The option is required</li>
 *       <li>selectLeavesOnly - Wether only tree leaves should be selectable.
 *                          Set to true for file selection and false for folder selection by default false</li>
 *       <li>title - Title of dialog. Not required. If not setted - title will not be changed</li>
 *       <li>onOk - If setted will be called after select button is clicked. Not required</li>
 *       <li>onCancel - If setted will be called after cancel button clicked. not required</li>
 *       <li>uriTextboxId - Dom id of textbox where uri is placed. Required.</li>
 *       <li>browseButtonId - Dom id of button which should opens file selector dialog. Required.</li>
 *       <li>providerId - Tree data provider id. The option is required.</li>
 *       <li>treeOptions - Additional tree params like rootUri, bShowRoot etc. Not required.</li>
 * </ul>
 *
 * Minimal usage example:
 * <pre>
 *  new picker.FileSelector({
 *       treeId: treeId,
 *       providerId: providerId,
 *       uriTextboxId: uriTextboxId,
 *       browseButtonId: browseButtonId
 *   })
 * </pre>
 *
 */
picker.FileSelector = function (options) {
    this._disabled = options.disabled !== undefined ? options.disabled : false;
    this._uriTextbox = typeof options.uriTextboxId === 'string' ? jQuery('#' + options.uriTextboxId)[0]: options.uriTextboxId;
    this._browseButtonId = typeof options.uriTextboxId === 'string' ? jQuery('#' + options.browseButtonId)[0]: options.browseButtonId;
    this._onChange = options.onChange || false;
    this._options = options;
    if (!this._disabled) {
        this._id = options.id;    //There can be multiple fileSelectors so made them all unique using id suffix
        //There can be multiple fileSelectors so made them all unique using id suffix
        this._suffix = options.suffix ? options.suffix : new Date().getTime();
        this._treeDomId = options.treeId;
        this._selectLeavesOnly = options.selectLeavesOnly !== undefined ? options.selectLeavesOnly : false;
        this._selectedUri = jQuery(this._uriTextbox).val();
        this._process(options);
        this._assignHandlers();
        this._refreshButtonsState();
    } else {
        buttonManager.disable(this._uriTextbox);
        buttonManager.disable(this._browseButtonId);
    }
};
picker.FileSelector.addVar('DEFAULT_TEMPLATE_DOM_ID', 'selectFromRepository');
picker.FileSelector.addVar('DEFAULT_TREE_ID', 'selectFromRepoTree');
picker.FileSelector.addVar('OK_BUTTON_ID', 'selectFromRepoBtnSelect');
picker.FileSelector.addVar('CANCEL_BUTTON_ID', 'selectFromRepoBtnCancel');
picker.FileSelector.addVar('TITLE_PATTERN', 'div.title');
picker.FileSelector.addMethod('_process', function (options) {
    !this._id && (this._id = this.DEFAULT_TEMPLATE_DOM_ID);
    if (!jQuery('#' + this._id)[0] && this._options.template && this._options.i18n) {
        this._dom = jQuery(_.template(this._options.template, { i18n: this._options.i18n }));
        this._dom = this._dom[0];
    } else {
        this._dom = jQuery('#' + this._id).clone(true);
    }
    jQuery(this._dom).attr('id', this._id + this._suffix);
    this._okButton = jQuery(this._dom).find('#' + this.OK_BUTTON_ID)[0];
    jQuery(this._okButton).attr('id', this.OK_BUTTON_ID + this._suffix);
    this._cancelButton = jQuery(this._dom).find('#' + this.CANCEL_BUTTON_ID)[0];
    jQuery(this._cancelButton).attr('id', this.CANCEL_BUTTON_ID + this._suffix);
    !this._treeDomId && (this._treeDomId = this.DEFAULT_TREE_ID);
    this._treeDom = jQuery(this._dom).find('#' + this._treeDomId)[0];
    jQuery(this._treeDom).attr('id', this._treeDomId + this._suffix);
    this._visible = false;
    options.title && jQuery(this._dom).find(this.TITLE_PATTERN)[0].update(options.title);
    this._onOk = options.onOk;
    this._onCancel = options.onCancel;
    jQuery(document.body).append(this._dom);
    var scroll;
    var scrollWrapper = jQuery(this._dom)[0].next(layoutModule.SWIPE_SCROLL_PATTERN);
    scrollWrapper && (scroll = layoutModule.createScroller(scrollWrapper));
    var treeOptions = Object.extend({
        providerId: options.providerId,
        scroll: scroll
    }, options.treeOptions);
    this._tree = dynamicTree.createRepositoryTree(this._treeDomId + this._suffix, treeOptions);
    this._selectedUri && this._selectedUri.length > 0 ? this._tree.showTreePrefetchNodes(this._selectedUri) : this._tree.showTree(1);
});
picker.FileSelector.addMethod('_assignHandlers', function () {
    jQuery(this._dom).on('click', this._dialogClickHandler.bindAsEventListener(this));
    [
        'node:dblclick',
        'leaf:dblclick'
    ].each(function (event) {
        this._tree.observe(event, this._treeClickHandler.bindAsEventListener(this));
    }, this);
    [
        'node:click',
        'leaf:click',
        'node:selected',
        'leaf:selected'
    ].each(function (event) {
        this._tree.observe(event, this._refreshButtonsState.bindAsEventListener(this));
    }, this);
    [
        'childredPrefetched:loaded',
        'tree:loaded'
    ].each(function (event) {
        this._tree.observe(event, this._treeLoadHandler.bindAsEventListener(this));
    }, this);
    jQuery(this._browseButtonId).on('click', this._browseClickHandler.bindAsEventListener(this));
});
picker.FileSelector.addMethod('_canClickOk', function (event) {
    return this._tree.getSelectedNode() && (this._selectLeavesOnly ? this._tree.getSelectedNode().param.type !== this._tree.getSelectedNode().FOLDER_TYPE_NAME : true);
});
picker.FileSelector.addMethod('_dialogClickHandler', function (event) {
    var element = event.target;
    if (matchAny(element, ['#' + this.OK_BUTTON_ID + this._suffix], true)) {
        event.stopPropagation();
        var value = this._tree.getSelectedNode().param.uri;
        this._uriTextbox.setValue(value);
        this._onChange && this._onChange(value);
        this._hide();
        this._onOk && this._onOk();
    } else if (matchAny(element, ['#' + this.CANCEL_BUTTON_ID + this._suffix], true)) {
        event.stopPropagation();
        this._hide();
        this._onCancel && this._onCancel();
    }
});
picker.FileSelector.addMethod('_treeClickHandler', function (event) {
    if (this._canClickOk()) {
        var value = this._tree.getSelectedNode().param.uri;
        this._uriTextbox.setValue(value);
        this._onChange && this._onChange(value);
        this._hide();
        this._onOk && this._onOk();
    }
});
picker.FileSelector.addMethod('_treeLoadHandler', function (event) {
    this._visible && this._selectedUri && this._tree.openAndSelectNode(this._selectedUri);
});
picker.FileSelector.addMethod('_browseClickHandler', function (event) {
    event.stopPropagation();
    this._show();
});
picker.FileSelector.addMethod('_refreshButtonsState', function () {
    this._canClickOk() ? buttonManager.enable(this._okButton) : buttonManager.disable(this._okButton);
});
picker.FileSelector.addMethod('_hide', function () {
    dialogs.popup.hide(this._dom);
    this._visible = false;
});
picker.FileSelector.addMethod('_show', function () {
    this._selectedUri = jQuery(this._uriTextbox).val();
    this._selectedUri && this._selectedUri.length > 0 ? this._tree.showTreePrefetchNodes(this._selectedUri) : this._tree.showTree(1);
    dialogs.popup.show(this._dom, true);
    this._visible = true;
    this._selectedUri && this._tree.openAndSelectNode(this._selectedUri);
    this._refreshButtonsState();
});
picker.FileSelector.addMethod('remove', function () {
    jQuery(this._dom).remove();
});

export default picker;