define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var i18n = require("bundle!all");

var ResourceModel = require("runtime_dependencies/bi-repository/src/bi/repository/model/RepositoryResourceModel");

var DialogWithModelInputValidation = require("runtime_dependencies/js-sdk/src/common/component/dialog/DialogWithModelInputValidation");

var baseSaveDialogTemplate = require("text!./template/baseSaveDialogTemplate.htm");

var settings = require("settings!treeComponent");

var repositoryTreeFactory = require("runtime_dependencies/bi-repository/src/bi/repository/factory/repositoryTreeFactory");

var repositoryResourceTypes = require("runtime_dependencies/bi-repository/src/bi/repository/enum/repositoryResourceTypes");

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
  theDialogIsOpen: false,
  autoUpdateResourceID: true,
  saveDialogTemplate: baseSaveDialogTemplate,
  constructor: function constructor(options) {
    options || (options = {});
    this.options = options;
    var model = this.extendModel(this.options.model);

    var saveButtonLabel = this._getLabelForSaveButton(model);

    var cancelButtonLabel = 'resource.datasource.saveDialog.cancel';
    this.autoUpdateResourceID = !this.options.isEditMode;
    this.preSelectedFolder = options.parentFolderUri;
    DialogWithModelInputValidation.prototype.constructor.call(this, {
      skipLocation: !!options.skipLocation,
      modal: true,
      model: model,
      minHeight: 500,
      minWidth: 440,
      setMinSizeAsSize: true,
      resizable: !options.skipLocation,
      additionalCssClasses: 'dataSourceSaveDialog' + (options.skipLocation ? ' no-minheight' : ''),
      title: i18n['resource.datasource.saveDialog.save'],
      content: _.template(this.saveDialogTemplate, {
        i18n: i18n,
        model: _.extend({}, model.attributes),
        skipLocation: !!this.options.skipLocation,
        isEmbedded: this.options.isEmbedded,
        isEditMode: this.options.isEditMode
      }),
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
    this.on('button:save', _.bind(this._onSaveDialogSaveButtonClick, this));
    this.on('button:cancel', _.bind(this._onSaveDialogCancelButtonClick, this));
  },
  initialize: function initialize(options) {
    DialogWithModelInputValidation.prototype.initialize.apply(this, arguments); // check if this variables has been re-defined by inherited class
    // check if this variables has been re-defined by inherited class

    if (_.isUndefined(this.preSelectedFolder) || !this.preSelectedFolder) {
      this.preSelectedFolder = '/';
    }

    if (!options.skipLocation) {
      this.initializeTree();
    }

    this.listenTo(this.model, 'change:label', this._onDataSourceNameChange);
    this.$contentContainer.find('[name=name]').change(_.bind(this._onResourceIDInputChange, this));
  },
  restoreModel: function restoreModel() {
    if (this.originalModelValidation) {
      this.model.validation = this.originalModelValidation;
    }
  },
  extendModel: function extendModel(model) {
    this.originalModelValidation = model.validation;
    model.validation = _.extend({}, ResourceModel.prototype.validation, {
      label: [{
        required: true,
        msg: i18n['resource.datasource.saveDialog.validation.not.empty.label']
      }, {
        maxLength: ResourceModel.settings.LABEL_MAX_LENGTH,
        msg: i18n['resource.datasource.saveDialog.validation.too.long.label']
      }],
      name: [{
        required: true,
        msg: i18n['resource.datasource.saveDialog.validation.not.empty.name']
      }, {
        maxLength: ResourceModel.settings.NAME_MAX_LENGTH,
        msg: i18n['resource.datasource.saveDialog.validation.too.long.name']
      }, {
        doesNotContainSymbols: ResourceModel.settings.NAME_NOT_SUPPORTED_SYMBOLS,
        msg: i18n['resource.datasource.saveDialog.validation.invalid.chars.name']
      }],
      description: [{
        required: false
      }, {
        maxLength: ResourceModel.settings.DESCRIPTION_MAX_LENGTH,
        msg: i18n['resource.datasource.saveDialog.validation.too.long.description']
      }],
      parentFolderUri: [{
        fn: function fn(value) {
          if (!this.options.skipLocation) {
            if (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '') {
              return i18n['resource.datasource.saveDialog.validation.not.empty.parentFolderIsEmpty'];
            }

            if (value.slice(0, 1) !== '/') {
              return i18n['resource.datasource.saveDialog.validation.folder.not.found'].replace('{0}', value);
            }
          }
        }
      }]
    });
    return model;
  },
  initializeTree: function initializeTree() {
    this.foldersTree = repositoryTreeFactory({
      processors: ['folderTreeProcessor', 'treeNodeProcessor', 'i18nItemProcessor', 'filterPublicFolderProcessor', 'cssClassItemProcessor', 'fakeUriProcessor'],
      treeBufferSize: settings.treeLevelLimit,
      types: [repositoryResourceTypes.FOLDER],
      tooltipOptions: {}
    });
    this.listenTo(this.foldersTree, 'selection:change', function (selection) {
      var parentFolderUri;

      if (selection && _.isArray(selection) && selection[0] && selection[0].uri) {
        parentFolderUri = selection[0].uri;
      }

      if (!parentFolderUri) {
        return;
      }

      this.model.set('parentFolderUri', parentFolderUri);
    });
    this.$el.find('.treeBox .folders').append(this.foldersTree.render().el);
    var $scrollContainer = this.foldersTree.$el.parent().parent().parent();

    this.foldersTree._selectTreeNode(this.preSelectedFolder, $scrollContainer);
  },
  startSaveDialog: function startSaveDialog() {
    this._openDialog();
  },
  _openDialog: function _openDialog() {
    if (this.theDialogIsOpen) {
      return;
    }

    this.bindValidation();
    DialogWithModelInputValidation.prototype.open.apply(this, arguments);
    this.$contentContainer.find('[name=label]').focus();
    this.theDialogIsOpen = true;
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
  _getLabelForSaveButton: function _getLabelForSaveButton() {
    return 'resource.datasource.saveDialog.save';
  },
  _onDialogResize: function _onDialogResize() {
    var self = this;
    var heightReservation = 40;
    var otherElementsHeight = 0;
    var treeBox = this.$contentContainer.find('.control.groupBox.treeBox');
    var dialogBody = this.$contentContainer.closest('.jr-mDialog > .jr-mDialog-body');
    this.$contentContainer.children().not(treeBox).each(function () {
      otherElementsHeight += self.$(this).outerHeight(true);
    });
    treeBox.height(dialogBody.outerHeight(true) - otherElementsHeight - heightReservation);
  },
  _onDataSourceNameChange: function _onDataSourceNameChange() {
    if (this.autoUpdateResourceID) {
      var resourceId = ResourceModel.generateResourceName(this.model.get('label'));
      this.model.set('name', resourceId);
      this.$('input[name=\'name\']').val(resourceId);
    }
  },
  _onResourceIDInputChange: function _onResourceIDInputChange() {
    this.autoUpdateResourceID = false;
  },
  _onSaveDialogCancelButtonClick: function _onSaveDialogCancelButtonClick() {
    this.restoreModel();

    this._closeDialog();
  },
  _onSaveDialogSaveButtonClick: function _onSaveDialogSaveButtonClick() {
    if (!this.model.isValid(true)) {
      return;
    }

    this.performSave();
  },
  performSave: function performSave() {
    if (this.options.saveFn) {
      this.options.saveFn(this.model.attributes, this.model);
      return;
    }

    this.model.save({}, {
      success: _.bind(this._saveSuccessCallback, this),
      error: _.bind(this._saveErrorCallback, this)
    });
  },
  _saveSuccessCallback: function _saveSuccessCallback(model) {
    this._closeDialog();

    if (_.isFunction(this.options.success)) {
      this.options.success(model);
    }
  },
  _saveErrorCallback: function _saveErrorCallback(model, xhr, options) {
    var self = this,
        errors = false;
    var handled = false;

    try {
      errors = JSON.parse(xhr.responseText);
    } catch (e) {}

    if (!_.isArray(errors)) {
      errors = [errors];
    }

    _.each(errors, function (error) {
      var field = false,
          msg = false;

      if (!error) {
        return;
      } // in case of opened dialog, we can highlight some fields with error
      // in case of opened dialog, we can highlight some fields with error


      if (self.theDialogIsOpen) {
        // check if we faced Conflict issue, it's when we are trying to save DS under existing resourceID
        if (error.errorCode === 'version.not.match') {
          field = 'name';
          msg = i18n['resource.dataSource.resource.alreadyInUse'];
        } else if (error.errorCode === 'mandatory.parameter.error') {
          if (error.parameters && error.parameters[0]) {
            msg = i18n['resource.datasource.saveDialog.parameterIsMissing'];
            field = error.parameters[0].substr(error.parameters[0].indexOf('.') + 1);
          }
        } else if (error.errorCode === 'illegal.parameter.value.error') {
          if (error.parameters && error.parameters[0]) {
            field = error.parameters[0].substr(error.parameters[0].indexOf('.') + 1);
            msg = i18n['resource.datasource.saveDialog.parameterIsWrong'];
          }
        } else if (error.errorCode === 'folder.not.found') {
          field = 'parentFolderUri';
          msg = i18n['ReportDataSourceValidator.error.folder.not.found'].replace('{0}', error.parameters[0]);
        } else if (error.errorCode === 'access.denied') {
          field = 'parentFolderUri';
          msg = i18n['jsp.accessDenied.errorMsg'];
        }
      }

      if (msg && field && ['label', 'name', 'description', 'parentFolderUri'].indexOf(field) !== -1) {
        self.invalidField('[name=' + field + ']', msg);
        handled = true;
      }
    }); // otherwise, pass this error to DataSourceController
    // otherwise, pass this error to DataSourceController


    if (handled === false) {
      if (_.isFunction(this.options.error)) {
        this.options.error(model, xhr, options);
      }
    }
  }
});

});