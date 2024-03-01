define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Dialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/Dialog");

var ImportView = require('./ImportView');

var i18n = require("bundle!CommonBundle");

var i18n2 = require("bundle!ImportExportBundle");

var importExportTypesEnum = require('../../export/enum/exportTypesEnum');

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
    this.options = options;
    Dialog.prototype.constructor.call(this, {
      model: this.model,
      modal: true,
      resizable: true,
      additionalCssClasses: 'tenant-import-dialog',
      content: '',
      buttons: [{
        label: i18n['button.import'],
        action: 'import',
        primary: true
      }, {
        label: i18n['button.cancel'],
        action: 'cancel',
        primary: false
      }]
    });
    this.on('button:import', _.bind(this._onImportButtonClick, this));
    this.on('button:cancel', _.bind(this._closeImportDialog, this));
  },
  initialize: function initialize(options) {
    Dialog.prototype.initialize.apply(this, arguments);
    this.importView = new ImportView();
    this.listenTo(this.importView, 'import:finished', function (tenantId) {
      this.close();
      this.trigger('import:finished', tenantId);
    });
    this.listenTo(this.importView.model, 'validated', function (isValid) {
      isValid ? this.buttons.enable('import') : this.buttons.disable('import');
    }, this);
  },
  openDialog: function openDialog(tenant) {
    var title = i18n2['tenant.import.dialog.title'] + ' ' + tenant.name;
    var type = !tenant.isRoot() ? importExportTypesEnum.TENANT : importExportTypesEnum.ROOT_TENANT;
    tenant.isRoot() && this.addCssClasses('tenant');
    this.setContent(this.importView.render({
      type: type,
      tenantId: tenant.id
    }).$el);
    this.setTitle(title);
    Dialog.prototype.open.apply(this, arguments);
    this.importView.applyEpoxyBindings();
    this.importView.delegateEvents();
  },
  // block default validation handlers
  fieldIsValid: function fieldIsValid() {},
  fieldIsInvalid: function fieldIsInvalid(error) {},
  _closeImportDialog: function _closeImportDialog() {
    this.close();
  },
  _onImportButtonClick: function _onImportButtonClick() {
    this.importView.doImport();

    this._closeImportDialog();
  }
});

});