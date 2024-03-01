define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var i18n = require("bundle!all");

var Dialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/Dialog");

var dialogTemplate = require("text!../template/repositoryFolderChooserDialogTemplate.htm");

var repositoryTreeFactory = require('../../../factory/repositoryTreeFactory');

var repositoryResourceTypes = require('../../../enum/repositoryResourceTypes');

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
module.exports = Dialog.extend({
  constructor: function constructor(options) {
    options || (options = {});
    var additionalCssClasses = 'selectFolder';

    if (options.additionalCssClasses) {
      additionalCssClasses += ' ' + options.additionalCssClasses;
    }

    this.foldersTree = repositoryTreeFactory({
      processors: ['folderTreeProcessor', 'treeNodeProcessor', 'i18nItemProcessor', 'filterPublicFolderProcessor', 'cssClassItemProcessor', 'fakeUriProcessor'],
      treeBufferSize: options.treeBufferSize,
      types: [repositoryResourceTypes.FOLDER],
      tooltipOptions: {}
    });
    Dialog.prototype.constructor.call(this, {
      modal: true,
      resizable: true,
      minWidth: 400,
      minHeight: 400,
      setMinSizeAsSize: options.setMinSizeAsSize,
      additionalCssClasses: additionalCssClasses,
      title: i18n['repository.content'],
      content: _.template(dialogTemplate)({
        i18n: i18n
      }),
      buttons: [{
        label: i18n['button.select'],
        action: 'select',
        primary: true
      }, {
        label: i18n['button.cancel'],
        action: 'cancel',
        primary: false
      }]
    });
    var $bodyOfDialog = this.$contentContainer.find('.control.groupBox .body');
    $bodyOfDialog.append(this.foldersTree.render().el);
  },
  initialize: function initialize(options) {
    this.listenTo(this.foldersTree, 'selection:change', this._selectionListener);
    this.listenTo(this, 'button:select', this._onOkButtonClick);
    this.listenTo(this, 'button:cancel', this._onCancelButtonClick);
    Dialog.prototype.initialize.apply(this, arguments);
    this._isOpened = false;
    this.selectedResource = null;
    this._lastSelectedResource = null;
  },
  open: function open() {
    this._openDialog();
  },
  close: function close() {
    this._closeDialog();
  },
  remove: function remove() {
    this._closeDialog(); // remove internal components
    // remove internal components


    this.foldersTree.remove();
    Dialog.prototype.remove.apply(this, arguments);
  },
  setDefaultSelectedItem: function setDefaultSelectedItem(defaultSelectedItem) {
    this._defaultSelectedItem = defaultSelectedItem;
  },
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Next go private methods
  _onOkButtonClick: function _onOkButtonClick() {
    if (this.selectedResource) {
      this._closeDialog();

      this._lastSelectedResource = this.selectedResource;
    }
  },
  _onCancelButtonClick: function _onCancelButtonClick() {
    // removing the selection if some was made
    this.selectedResource = null;

    if (this._lastSelectedResource) {
      this.selectedResource = this._lastSelectedResource;
    }

    this._closeDialog();
  },
  _openDialog: function _openDialog() {
    if (this._isOpened) {
      return;
    }

    Dialog.prototype.open.apply(this, arguments); // reset the tree itself !
    // reset the tree itself !

    this._resetTree();

    this.disableButton('select'); // now, select the default item in the tree
    // now, select the default item in the tree

    this._preselectItem();

    this._isOpened = true;
  },
  _closeDialog: function _closeDialog() {
    if (!this._isOpened) {
      return;
    }

    Dialog.prototype.close.apply(this, arguments);
    this._isOpened = false;
  },
  _onDialogResize: function _onDialogResize() {
    var widthReservation = 55;
    var heightReservation = 50;
    var treeBox = this.$contentContainer.find('.treeBox > .content > .body');
    var dialogBody = this.$contentContainer.closest('.jr-mDialog > .jr-mDialog-body');
    treeBox.width(dialogBody.outerWidth(true) - widthReservation);
    treeBox.height(dialogBody.outerHeight(true) - heightReservation);
  },
  _selectionListener: function _selectionListener(selection) {
    var selectionKeys = Object.keys(selection);
    var itemSelected = selection && (_.isArray(selection) || _.isObject(selection)) && selection[selectionKeys[0]];
    var resourceUri = itemSelected ? itemSelected.uri : undefined;
    var resourceType = itemSelected ? itemSelected.resourceType : undefined;

    if (!(itemSelected || resourceUri || resourceType)) {
      this.disableButton('select');
      return;
    } // save selected resource information.
    // this could be used by anyone to get information about what has been selected
    // save selected resource information.
    // this could be used by anyone to get information about what has been selected


    this.selectedResource = {
      resourceUri: resourceUri
    };
    this.enableButton('select');
  },
  _resetTree: function _resetTree() {
    // reset Tree
    this.foldersTree.collapse('/root', {
      silent: true
    });
    this.foldersTree.collapse('/public', {
      silent: true
    });
    this.foldersTree.resetSelection();
  },
  _preselectItem: function _preselectItem() {
    // This function should select some item in the tree once the dialog is opened.
    // There can be several "sources" of items which we might select:
    // 1) pre-default selection given during dialog initialization
    // 2) last selected item (user opened dialog, selected item and clicked "OK")
    // So, the logic would be the next:
    // we look if we have "last selected item", if yes -- then use it, if not --
    // then look if we have "pre-default selection", if yes -- the use it, if not -- nothing to do
    var itemToSelect = false;

    if (this._lastSelectedResource) {
      itemToSelect = this._lastSelectedResource.resourceUri;
    } else if (this._defaultSelectedItem) {
      itemToSelect = this._defaultSelectedItem;
    }

    if (!itemToSelect) {
      // nothing to do
      return;
    }

    var scrollArea = this.foldersTree.$el.parent();

    this.foldersTree._selectTreeNode(itemToSelect, scrollArea);
  }
});

});