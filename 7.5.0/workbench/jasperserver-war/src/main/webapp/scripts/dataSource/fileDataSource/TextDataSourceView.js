define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var i18n = require("bundle!all");

var resourceLocator = require('../../resource/resource.locate');

var CustomDataSourceView = require('../view/CustomDataSourceView');

var TextDataSourceModel = require('./TextDataSourceModel');

var delimitersTextDataSource = require('./enum/delimitersTextDataSource');

var characterEncodings = require('./enum/characterEncodings');

var fileSourceTypes = require('./enum/fileSourceTypes');

var textBackboneTemplate = require("text!./template/backboneTemplate.htm");

var textFileLocationTemplate = require("text!./template/fileLocationTemplate.htm");

var textFilePropertiesTemplate = require("text!./template/filePropertiesTemplate.htm");

var selectDialogTemplate = require("text!runtime_dependencies/js-sdk/src/common/templates/components.pickers.htm");

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
var fileSourceTypeOptions = _.reduce(fileSourceTypes, function (memo, value) {
  if (value.name === 'repository') {
    memo.push(value);
  }

  return memo;
}, []);

module.exports = CustomDataSourceView.extend({
  PAGE_TITLE_NEW_MESSAGE_CODE: 'resource.datasource.text.page.title.new',
  PAGE_TITLE_EDIT_MESSAGE_CODE: 'resource.datasource.text.page.title.edit',
  modelConstructor: TextDataSourceModel,
  browseButton: false,
  events: function events() {
    var events = _.extend({}, CustomDataSourceView.prototype.events);

    events['change [name=fileSourceType]'] = 'changeFileSourceType';
    return events;
  },
  initialize: function initialize(options) {
    CustomDataSourceView.prototype.initialize.apply(this, arguments);
    this.listenTo(this.model, 'change:serverFileName', this.adjustFileSystemConnectButton);
    this.listenTo(this.model, 'change:serverAddress', this.adjustFtpServerConnectButton);
    this.listenTo(this.model, 'change:serverPath', this.adjustFtpServerConnectButton);
    this.listenTo(this.model, 'change:ftpsPort', this.adjustFtpServerConnectButton);
    this.listenTo(this.model, 'change:fieldDelimiter', this.adjustFieldDelimiterSection);
    this.listenTo(this.model, 'change:rowDelimiter', this.adjustRowDelimiterSection);
    this.listenTo(this.model, 'change', this.adjustPreviewButton);
    this.listenTo(this.model, 'sourceFileIsOK', this.sourceFileIsOK);
    this.listenTo(this.model, 'sourceFileCantBeParsed', this.sourceFileCantBeParsed);
  },
  changeFileSourceType: function changeFileSourceType() {
    // since in the BaseDataSourceView there is a default listener, I'll trigger the rendering in the moment
    // right after all event listeners will be run
    _.defer(_.bind(function () {
      this.renderFileLocationSection();
    }, this));
  },
  render: function render() {
    this.$el.empty();
    this.renderTextDataSourceSection();
    return this;
  },
  templateData: function templateData() {
    return _.extend(CustomDataSourceView.prototype.templateData.apply(this, arguments), {
      fileSourceTypeOptions: fileSourceTypeOptions,
      fieldDelimiterOptions: delimitersTextDataSource,
      rowDelimiterOptions: delimitersTextDataSource,
      encodingOptions: characterEncodings
    });
  },
  renderTextDataSourceSection: function renderTextDataSourceSection() {
    this.$el.append(_.template(textBackboneTemplate, this.templateData()));
    this.renderFileLocationSection();
    this.renderFilePropertiesSection();
  },
  renderFileLocationSection: function renderFileLocationSection() {
    this.renderOrAddAnyBlock(this.$el.find('[name=textDataSourceFieldsContainer]'), _.template(textFileLocationTemplate, this.templateData())); // remove the old browse button handler if exists
    // remove the old browse button handler if exists

    if (this.browseButton) {
      this.browseButton.remove();
      this.browseButton = false;
    }

    if (this.model.get('fileSourceType') === 'repository') {
      this.browseButton = resourceLocator.initialize({
        i18n: i18n,
        template: selectDialogTemplate,
        resourceInput: this.$el.find('[name=repositoryFileName]')[0],
        browseButton: this.$el.find('[name=repositoryBrowserButton]')[0],
        providerId: 'contentResourceTreeDataProvider',
        dialogTitle: i18n['resource.Add.Files.Title'],
        selectLeavesOnly: true,
        onChange: _.bind(function (value) {
          this.model.set('repositoryFileName', value);
          this.model.validate({
            'repositoryFileName': value
          });
        }, this)
      });
    }

    this.adjustFileSystemConnectButton();
    this.adjustFtpServerConnectButton();
  },
  adjustFileSystemConnectButton: function adjustFileSystemConnectButton() {
    var flag = this.model.isValid('serverFileName');

    this._adjustButton('serverFileSystemConnectToServer', flag);
  },
  adjustFtpServerConnectButton: function adjustFtpServerConnectButton() {
    var flag = this.model.isValid(['serverAddress', 'serverPath', 'ftpsPort']);

    this._adjustButton('ftpConnectToServer', flag);
  },
  renderFilePropertiesSection: function renderFilePropertiesSection() {
    this.renderOrAddAnyBlock(this.$el.find('[name=textDataSourceFieldsContainer]'), _.template(textFilePropertiesTemplate, this.templateData()));
    this.adjustFieldDelimiterSection();
    this.adjustRowDelimiterSection();
    this.adjustPreviewButton();
  },
  adjustFieldDelimiterSection: function adjustFieldDelimiterSection() {
    this._adjustSection('fieldDelimiter', {
      regex: 'fieldDelimiterRegexInput',
      plugin: 'fieldDelimiterPluginInput',
      other: 'fieldDelimiterOtherInput'
    });
  },
  adjustRowDelimiterSection: function adjustRowDelimiterSection() {
    this._adjustSection('rowDelimiter', {
      regex: 'rowDelimiterRegexInput',
      plugin: 'rowDelimiterPluginInput',
      other: 'rowDelimiterOtherInput'
    });
  },
  adjustPreviewButton: function adjustPreviewButton() {
    var flag1 = this.model.get('fileSourceType') === 'repository' && this.model.isValid('repositoryFileName');
    var flag2 = this.model.get('fileSourceType') === 'serverFileSystem' && this.model.isValid('serverFileName');
    var flag3 = this.model.get('fileSourceType') === 'ftpServer' && this.model.isValid(['serverAddress', 'serverPath', 'ftpsPort']);
    var flag = flag1 || flag2 || flag3;

    this._adjustButton('previewDataSource', flag);
  },
  _adjustButton: function _adjustButton(buttonSelector, enabledState) {
    var button = this.$el.find('[name=' + buttonSelector + ']');

    if (!!enabledState) {
      button.removeAttr('disabled');
    } else {
      button.attr('disabled', 'disabled');
    }
  },
  _adjustSection: function _adjustSection(modelAttrId, elementsInSectionToAdjust) {
    var self = this;

    _.each(elementsInSectionToAdjust, function (elementName, valueItShouldHaveToBeEnabled) {
      var section = self.$el.find('[name=' + elementName + ']');
      section.toggleClass('hidden', self.model.get(modelAttrId) !== valueItShouldHaveToBeEnabled);
    });
  },
  sourceFileIsOK: function sourceFileIsOK() {
    this.fieldIsValid(this, 'repositoryFileName', 'name');
  },
  sourceFileCantBeParsed: function sourceFileCantBeParsed() {
    this.fieldIsInvalid(this, 'repositoryFileName', i18n['resource.file.cantBeProcessed'], 'name');
  }
});

});