define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var i18n = require("bundle!all");

var javaTypeMapper = require("runtime_dependencies/js-sdk/src/common/enum/javaTypeMapper");

var javaTypes = require('./enum/javaTypes');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var SimpleDomainModel = require('./SimpleDomainModel');

var DialogWithModelInputValidation = require("runtime_dependencies/js-sdk/src/common/component/dialog/DialogWithModelInputValidation");

var DomainSaveDialogView = require('../saveDialog/DomainSaveDialogView');

var standardConfirmTemplate = require("text!runtime_dependencies/js-sdk/src/common/templates/standardConfirm.htm");

var simpleDomainDialogTemplate = require("text!./template/simpleDomainDialogTemplate.htm");

var dialogs = require('../../components/components.dialogs');

var _utilUtilsCommon = require("../../util/utils.common");

var redirectToUrl = _utilUtilsCommon.redirectToUrl;

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
module.exports = DialogWithModelInputValidation.extend({
  hasChanges: false,
  modelIsValid: true,
  theDialogIsOpen: false,
  constructor: function constructor(options) {
    options || (options = {});
    this.options = options;
    this.dataSource = options.dataSource ? options.dataSource : {};
    var saveButtonLabel = 'resource.datasource.createDomain.save';
    var cancelButtonLabel = 'resource.datasource.createDomain.cancel';
    this.model = new SimpleDomainModel({}, options);
    DialogWithModelInputValidation.prototype.constructor.call(this, {
      modal: true,
      model: this.model,
      width: 400,
      additionalCssClasses: 'dataSourceCreateNewDomainDialog',
      title: i18n['resource.datasource.createDomain.dialogTitle'],
      content: '',
      buttons: [{
        label: i18n[saveButtonLabel],
        action: 'save',
        primary: true
      }, {
        label: i18n[cancelButtonLabel],
        action: 'cancel',
        primary: false
      }]
    });
    this.on('button:save', _.bind(this._onSaveButtonClick, this));
    this.on('button:cancel', _.bind(this._onCancelButtonClick, this));
    this.model.on('validationPassed', _.bind(this.clearValidationErrors, this));
    this.model.on('validationFailed', _.bind(this.validationFailed, this));
  },
  initialize: function initialize(options) {
    DialogWithModelInputValidation.prototype.initialize.apply(this, arguments);
  },
  updateModelProperty: function updateModelProperty(e) {
    var target = $(e.target),
        type = target.attr('type'),
        name = target.attr('name'),
        fieldId = target.parents('tr').attr('data-fieldId'),
        columns,
        value;

    if (type === 'checkbox' || type === 'radio') {
      value = target.is(':checked');
    } else {
      value = $.trim(target.val());
    }

    columns = this.model.get('columns');
    columns[fieldId][name] = value;
    this.model.set('columns', columns);
    this.model.validate('columns');

    if (this.hasChanges === false) {
      this.changeButtonLabel('save', i18n['resource.datasource.createDomain.applyAndSave']);
    }

    this.hasChanges = true;
  },
  startDialog: function startDialog() {
    var self = this;

    if (this.theDialogIsOpen) {
      return;
    }

    this.bindValidation();
    this.model.fetchMetadata(this.dataSource).done(function () {
      DialogWithModelInputValidation.prototype.open.apply(self, arguments);

      var content = _.template(simpleDomainDialogTemplate, {
        i18n: i18n,
        javaTypes: javaTypes,
        columns: self.model.get('columns'),
        javaTypeMapper: javaTypeMapper
      });

      self.setContent(content);

      self._center();
    });
    this.theDialogIsOpen = true;
  },
  // block default validation handlers
  fieldIsValid: function fieldIsValid() {},
  fieldIsInvalid: function fieldIsInvalid(error) {},
  clearValidationErrors: function clearValidationErrors() {
    this.$el.find('.error').removeClass('error');
    this.modelIsValid = true;
  },
  validationFailed: function validationFailed(errors) {
    var self = this;
    this.clearValidationErrors();
    this.modelIsValid = false;

    _.each(errors, function (error) {
      var errorRow = $(self.$el.find('table tr').get(error.rowId + 1));
      errorRow.find('[name=' + error.name + ']').parent().addClass('error');
      errorRow.find('.message').text(i18n['resource.datasource.createDomain.validation.missing.label']);
    });
  },
  _closeDialog: function _closeDialog() {
    if (!this.theDialogIsOpen) {
      return;
    }

    this.unbindValidation();
    this.clearValidationErrors();
    DialogWithModelInputValidation.prototype.close.apply(this, arguments);
    this.theDialogIsOpen = false;
  },
  _onCancelButtonClick: function _onCancelButtonClick() {
    var self = this,
        msg = i18n['resource.datasource.createDomain.cancelMessage'],
        confirm = $(standardConfirmTemplate);
    confirm.find('.body').html(msg);
    dialogs.popupConfirm.show(confirm.get(0), true, {
      okButtonSelector: '[name=buttonOK]',
      cancelButtonSelector: '[name=buttonCancel]'
    }).done(function () {
      self._closeDialog();

      if (self.options.cancel) {
        self.options.cancel();
      }
    });
  },
  _onSaveButtonClick: function _onSaveButtonClick() {
    if (!this.modelIsValid) {
      return;
    }

    this._closeDialog();

    this.saveDialog = new DomainSaveDialogView(_.extend({}, this.options, {
      model: this.model,
      success: function success() {
        redirectToUrl(jrsConfigs.contextPath + '/flow.html?_flowId=repositoryConfirmFlow&resourceType=dataSource&resourceType=domain');
      }
    }));
    this.saveDialog.startSaveDialog();
  }
});

});