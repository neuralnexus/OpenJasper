define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var BaseDataSourceView = require('../view/BaseDataSourceView');

var JdbcDataSourceModel = require('../model/JdbcDataSourceModel');

var JdbcDriverModel = require('../model/JdbcDriverModel');

var UploadJdbcDriverDialog = require('../view/dialog/UploadJdbcDriverDialog');

var jdbcSpecificTemplate = require("text!../template/jdbcSpecificTemplate.htm");

var jdbcCustomFieldTemplate = require("text!../template/jdbcCustomFieldTemplate.htm");

var buttonManager = require('../../core/core.events.bis');

var i18n = require("bundle!jasperserver_messages");

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
module.exports = BaseDataSourceView.extend({
  PAGE_TITLE_NEW_MESSAGE_CODE: 'resource.datasource.jdbc.page.title.new',
  PAGE_TITLE_EDIT_MESSAGE_CODE: 'resource.datasource.jdbc.page.title.edit',
  modelConstructor: JdbcDataSourceModel,
  events: function () {
    var events = {};

    _.extend(events, BaseDataSourceView.prototype.events, {
      'keyup input[type=\'text\'][name!=\'driverClass\'], input[type=\'password\'], textarea': 'updateModelProperty',
      'change input[type=\'text\'][name!=\'driverClass\'], input[type=\'password\'], textarea, select': 'updateModelProperty',
      'keyup input[type=\'text\'][name=\'driverClass\']': 'manuallySetDriverClass',
      'change input[type=\'text\'][name=\'driverClass\']': 'manuallySetDriverClass',
      'click #driverUploadButton': 'uploadDriver'
    });

    return events;
  }(),
  initialize: function initialize(options) {
    BaseDataSourceView.prototype.initialize.apply(this, arguments);
    this.listenTo(this.model, 'connectionUrlUpdate', this.updateConnectionUrl);
    this.listenTo(this.model, 'customAttributesUpdate', this.updateCustomAttributes);
    this.listenTo(this.model, 'driverClassChange', this.changeDriver);
    this.listenTo(this.model.drivers, 'change', this.updateDriverOption);
    this.listenTo(this.model.drivers, 'add', this.addDriverOption);
  },
  updateDriverOption: function updateDriverOption(driver) {
    var $option = this.$('select[name=\'selectedDriverClass\'] option[value=\'' + driver.get('jdbcDriverClass') + '\']'),
        optionText = driver.get('label') + ' (' + driver.get('jdbcDriverClass') + ')';

    if (!driver.get('available')) {
      optionText = i18n['resource.dataSource.jdbc.driverMissing'] + ' ' + optionText;
    }

    $option.text(optionText);

    if (this.model.getCurrentDriver() === driver) {
      this.changeUploadDriverButtonState();
    }
  },
  addDriverOption: function addDriverOption(driver) {
    // ignore adding of "other driver" option.
    if (driver.isOtherDriver) return; // in case of normal driver is added to the drivers collection let's add it to the drivers select as option.
    // in case of normal driver is added to the drivers collection let's add it to the drivers select as option.

    var $otherDriverOption = this.$('select[name=\'selectedDriverClass\'] option[value=\'' + JdbcDriverModel.OTHER_DRIVER + '\']'),
        optionText = driver.get('jdbcDriverClass'),
        self = this;

    if (!driver.get('available')) {
      optionText = i18n['resource.dataSource.jdbc.driverMissing'] + ' ' + optionText;
    }

    $otherDriverOption.before('<option value=\'' + driver.get('jdbcDriverClass') + '\'>' + optionText + '</option>');

    _.defer(function () {
      self.$('select[name=\'selectedDriverClass\']').val(driver.get('jdbcDriverClass')); // manually trigger change event as set won't work here, as driverClass is already the same
      // manually trigger change event as set won't work here, as driverClass is already the same

      self.model.trigger('change:driverClass');
    });
  },
  manuallySetDriverClass: function manuallySetDriverClass() {
    var value = this.$('input[type=\'text\'][name=\'driverClass\']').val(),
        valueObj = {
      driverClass: value
    };
    this.model.set(valueObj, {
      silent: true
    });
    this.model.validate(valueObj);
    this.changeUploadDriverButtonState();
  },
  updateConnectionUrl: function updateConnectionUrl() {
    this.$('input[name=\'connectionUrl\']').val(this.model.get('connectionUrl')); // do not trigger validation for connectionUrl of "other" driver or "uploaded" driver as it's empty by default
    // do not trigger validation for connectionUrl of "other" driver or "uploaded" driver as it's empty by default

    var currentDriver = this.model.getCurrentDriver();

    if (!currentDriver.isOtherDriver() && !currentDriver.isUploadedDriver()) {
      this.model.validate({
        connectionUrl: this.model.get('connectionUrl')
      });
    }
  },
  updateCustomAttributes: function updateCustomAttributes() {
    var self = this,
        driverCustomAttributes = this.model.getCurrentDriver().getCustomAttributes();

    _.each(driverCustomAttributes, function (attr) {
      self.$('input[name=\'' + attr + '\']').val(self.model.get(attr));
    });

    var customAttributes = this.model.pick(driverCustomAttributes);
    this.model.validate(customAttributes);
  },
  changeDriver: function changeDriver() {
    this.renderDriverCustomAttributeFields();
    this.changeUploadDriverButtonState();
  },
  changeUploadDriverButtonState: function changeUploadDriverButtonState() {
    var currentDriver = this.model.getCurrentDriver(),
        $driverUploadButton = this.$('#driverUploadButton');
    buttonManager.enable($driverUploadButton[0]);
    var buttonLabel = currentDriver.get('available') ? i18n['resource.dataSource.jdbc.upload.editDriverButton'] : i18n['resource.dataSource.jdbc.upload.addDriverButton'];
    $driverUploadButton.find('.wrap').text(buttonLabel);
  },
  uploadDriver: function uploadDriver() {
    if (this.model.drivers.driverUploadEnabled && this.model.get('driverClass')) {
      this.fieldIsValid(this, 'driverClass', 'name');
      this.driverUploadDialog && this.stopListening(this.driverUploadDialog);
      delete this.driverUploadDialog;
      this.initDriverUploadDialog();
      this.driverUploadDialog.show();
    } else {
      this.fieldIsInvalid(this, 'driverClass', i18n['ReportDataSourceValidator.error.not.empty.reportDataSource.driverClass'], 'name');
    }
  },
  initDriverUploadDialog: function initDriverUploadDialog() {
    this.driverUploadDialog = new UploadJdbcDriverDialog({
      driverAvailable: this.model.getCurrentDriver().get('available'),
      driverClass: this.model.get('isOtherDriver') ? this.model.get('driverClass') : this.model.getCurrentDriver().get('jdbcDriverClass')
    });
    this.listenTo(this.driverUploadDialog, 'driverUpload', this._onDriverUploadFinished);
  },
  _onDriverUploadFinished: function _onDriverUploadFinished(driver) {
    var self = this;
    this.model.fetchDrivers().then(function () {
      self.model.drivers.markDriverAsAvailable(driver.jdbcDriverClass);

      _.defer(function () {
        self.model.validate();
        self.render();
      });
    });
  },
  render: function render() {
    this.$el.empty();
    this.renderJdbcSpecificSection();
    this.renderTimezoneSection();
    this.renderTestConnectionSection();
    return this;
  },
  templateData: function templateData() {
    var data = BaseDataSourceView.prototype.templateData.apply(this, arguments);

    _.extend(data, {
      drivers: this.model.drivers.toJSON(),
      otherDriverValue: JdbcDriverModel.OTHER_DRIVER,
      driverUploadEnabled: this.model.drivers.driverUploadEnabled
    });

    return data;
  },
  renderJdbcSpecificSection: function renderJdbcSpecificSection() {
    this.$el.append(_.template(jdbcSpecificTemplate, this.templateData()));
    this.renderDriverCustomAttributeFields();
    this.changeUploadDriverButtonState();
  },
  renderDriverCustomAttributeFields: function renderDriverCustomAttributeFields() {
    var self = this,
        resultingHtml = '';

    if (this.model.get('isOtherDriver')) {
      resultingHtml += _.template(jdbcCustomFieldTemplate, {
        hint: i18n['resource.dataSource.jdbc.hint1'],
        label: i18n['resource.dataSource.jdbc.driver'],
        name: 'driverClass',
        title: i18n['resource.analysisConnection.driver'],
        value: this.model.get('driverClass'),
        i18n: i18n
      });
    } else {
      var driverSpecificFields = this.model.getCurrentDriver().getCustomAttributes();

      _.each(driverSpecificFields, function (field) {
        resultingHtml += _.template(jdbcCustomFieldTemplate, {
          hint: '',
          label: i18n['resource.dataSource.jdbc.' + field],
          name: field,
          title: i18n['resource.dataSource.jdbc.requiredTitle'].replace('{0}', i18n['resource.dataSource.jdbc.' + field].toLowerCase()),
          value: self.model.get(field),
          i18n: i18n
        });
      });
    }

    this.$('[name=jdbcSpecificFieldsContainer]').html(resultingHtml);
  },
  remove: function remove() {
    this.driverUploadDialog && this.driverUploadDialog.remove();
    BaseDataSourceView.prototype.remove.apply(this, arguments);
  }
});

});