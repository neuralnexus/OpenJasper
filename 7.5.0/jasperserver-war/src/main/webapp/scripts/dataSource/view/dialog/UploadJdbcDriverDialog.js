define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var BaseDialog = require('../../view/dialog/BaseDialog');

var _ = require('underscore');

var $ = require('jquery');

var AjaxFormSubmitter = require("runtime_dependencies/js-sdk/src/common/transport/AjaxFormSubmitter");

var LoadingOverlay = require("runtime_dependencies/js-sdk/src/components/loadingOverlay/LoadingOverlay");

var dialogs = require('../../../components/components.dialogs');

var i18n = require("bundle!jasperserver_messages");

var uploadJdbcDriverDialogTemplate = require("text!../../template/dialog/uploadJdbcDriverDialogTemplate.htm");

var fileUploadTemplate = require("text!../../template/dialog/fileUploadTemplate.htm");

require('../../../util/utils.common');

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
module.exports = BaseDialog.extend({
  TITLE: i18n['resource.dataSource.jdbc.selectDriverTitle'],
  PRIMARY_BUTTON_LABEL: i18n['button.upload'],
  SECONDARY_BUTTON_LABEL: i18n['button.cancel'],
  events: function events() {
    return _.extend({}, BaseDialog.prototype.events, {
      'change input[type=\'file\']': 'onFileChange'
    });
  },
  initialize: function initialize(options) {
    this.driverClass = options.driverClass;
    this.driverAvailable = options.driverAvailable;
    this.fileIndex = 0;
    this._overlay = new LoadingOverlay({
      delay: 1000
    });
    BaseDialog.prototype.initialize.apply(this, arguments);
    $(this.el).addClass('jr jr-uploadJdbcDriverDialog');
  },
  onFileChange: function onFileChange(e) {
    var $targetEl = $(e.target),
        $validationEl = $targetEl.next('.message.warning');
    $validationEl.parent().removeClass('error');

    if (!$targetEl.val().match(/.jar$/)) {
      $validationEl.text(i18n['resource.dataSource.jdbc.upload.wrongExtension']).parent().addClass('error');
    } else if ($targetEl.is(this.$('input[type=\'file\']:last-of-type'))) {
      // determine if we need to add one more input to select more files
      var selectedFiles = 0,
          inputs = this.$('input[type=\'file\']');

      _.each(inputs, function (input, index) {
        selectedFiles += index + 1;
      });

      if (selectedFiles >= inputs.length) {
        this.addFileInput();
      }

      this.$('button.primary').removeClass('disabled').attr('disabled', null);
    }
  },
  render: function render() {
    var _this = this;

    this.$('.body').html(_.template(uploadJdbcDriverDialogTemplate, {
      className: this.driverClass,
      i18n: i18n
    }));

    _.defer(function () {
      _this.addFileInput();

      if (_this.driverAvailable) {
        _this.$('.warningMessageContainer').removeClass('hidden').find('.message').text(i18n['resource.dataSource.jdbc.upload.overwriteWarning']);
      } else {
        _this.$('.warningMessageContainer').addClass('hidden').find('.message').text('');
      }
    });

    this.$('button.primary').addClass('disabled').attr('disabled', 'disabled');
    return this;
  },
  onSuccessCallback: function onSuccessCallback(response) {
    this.trigger('driverUpload', response);
    this.hide();
    dialogs.systemConfirm.show(i18n['resource.dataSource.jdbc.upload.driverUploadSuccess']);

    _.defer(_.bind(this.remove, this));
  },
  onErrorCallback: function onErrorCallback(response) {
    var errorMessage;
    response = response.responseJSON ? response.responseJSON : response;

    if ('illegal.parameter.value.error' === response.errorCode && response.parameters && response.parameters.length && 'className' === response.parameters[0]) {
      errorMessage = i18n['resource.dataSource.jdbc.classNotFound'].replace('{0}', this.driverClass);
    } else if (response.message) {
      errorMessage = response.message;
    } else {
      errorMessage = response.errorCode; // see some notes in JRS-8435
      // backend responses with 400 error code and http header "Content-Type: application/errorDescriptor+xml"
      // this makes all browsers crazy.
      // Shortly, browsers don't have access to http response body...
      // Server is considering this issue, but for now we may close the issue by adding next lines on code
      // see some notes in JRS-8435
      // backend responses with 400 error code and http header "Content-Type: application/errorDescriptor+xml"
      // this makes all browsers crazy.
      // Shortly, browsers don't have access to http response body...
      // Server is considering this issue, but for now we may close the issue by adding next lines on code

      if (errorMessage === 'error.invalid.response') {
        errorMessage = 'The required driver class (' + this.driverClass + ') is not found in uploaded files';
      }
    }

    this.$('.errorMessageContainer').addClass('error').find('.message').text(errorMessage);
  },
  addFileInput: function addFileInput() {
    this.$('ul').append(_.template(fileUploadTemplate, {
      fileIndex: this.fileIndex
    }));
    this.fileIndex++;
  },
  primaryButtonOnClick: function primaryButtonOnClick() {
    var _this2 = this;

    // clear the error message because we are doing new upload
    this.$('.errorMessageContainer').removeClass('error').find('.message').text(''); // disable the primary button to block a second click

    this.$('button.primary').addClass('disabled').attr('disabled', 'disabled'); // add a spinner and overlay with some delay

    $("body").append(this._overlay.$el);

    this._overlay.show();

    var form = this.$('form');
    var submitter = new AjaxFormSubmitter(form[0]).submit();
    submitter.done(function (response) {
      if (response.errorCode) {
        _this2.onErrorCallback(response);
      } else {
        _this2.onSuccessCallback(response);
      }
    }).fail(function (response) {
      _this2.onErrorCallback(response);
    }).always(function () {
      // remove overlay with spinner
      _this2._overlay.hide();

      $("body").detach(_this2._overlay.$el); // and enable the primary button back again

      _this2.$('button.primary').removeClass('disabled').attr('disabled', null);
    });
  },
  secondaryButtonOnClick: function secondaryButtonOnClick() {
    // since the overlay start with some delay there is a change that user clicks "Cancel" button
    // and the overlay will trigger. To avoid this we need to stop the overlay as well.
    this._overlay.hide();

    $("body").detach(this._overlay.$el); // hide this dialog

    this.hide();

    _.defer(_.bind(this.remove, this));
  }
});

});