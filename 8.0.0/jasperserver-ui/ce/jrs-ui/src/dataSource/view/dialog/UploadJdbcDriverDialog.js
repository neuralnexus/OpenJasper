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

import BaseDialog from '../../view/dialog/BaseDialog';
import _ from 'underscore';
import $ from 'jquery';
import AjaxFormSubmitter from 'js-sdk/src/common/transport/AjaxFormSubmitter';
import LoadingOverlay from 'js-sdk/src/components/loadingOverlay/LoadingOverlay';
import dialogs from '../../../components/components.dialogs';
import i18n from '../../../i18n/jasperserver_messages.properties';
import uploadJdbcDriverDialogTemplate from '../../template/dialog/uploadJdbcDriverDialogTemplate.htm';
import fileUploadTemplate from '../../template/dialog/fileUploadTemplate.htm';
import '../../../util/utils.common';

export default BaseDialog.extend({
    TITLE: i18n['resource.dataSource.jdbc.selectDriverTitle'],
    PRIMARY_BUTTON_LABEL: i18n['button.upload'],
    SECONDARY_BUTTON_LABEL: i18n['button.cancel'],
    events: function () {
        return _.extend({}, BaseDialog.prototype.events, { 'change input[type=\'file\']': 'onFileChange' });
    },
    initialize: function (options) {
        this.driverClass = options.driverClass;
        this.driverAvailable = options.driverAvailable;
        this.fileIndex = 0;
        this._overlay = new LoadingOverlay({
            delay: 1000
        });
        BaseDialog.prototype.initialize.apply(this, arguments);
        $(this.el).addClass('jr jr-uploadJdbcDriverDialog');
    },
    onFileChange: function (e) {
        const $targetEl = $(e.target),
            $validationEl = $targetEl.next('.message.warning');
        $validationEl.parent().removeClass('error');
        if (!$targetEl.val().match(/.jar$/)) {
            $validationEl.text(i18n['resource.dataSource.jdbc.upload.wrongExtension']).parent().addClass('error');
        } else if ($targetEl.is(this.$('input[type=\'file\']:last-of-type'))) {
            // determine if we need to add one more input to select more files
            let selectedFiles = 0, inputs = this.$('input[type=\'file\']');
            _.each(inputs, function (input, index) {
                selectedFiles += index + 1;
            });
            if (selectedFiles >= inputs.length) {
                this.addFileInput();
            }
            this.$('button.primary').removeClass('disabled').attr('disabled', null);
        }
    },
    render: function () {
        this.$('.body').html(_.template(uploadJdbcDriverDialogTemplate, {
            className: this.driverClass,
            i18n: i18n
        }));
        _.defer(() => {
            this.addFileInput();
            if (this.driverAvailable) {
                this.$('.warningMessageContainer').removeClass('hidden').find('.message').text(i18n['resource.dataSource.jdbc.upload.overwriteWarning']);
            } else {
                this.$('.warningMessageContainer').addClass('hidden').find('.message').text('');
            }
        });
        this.$('button.primary').addClass('disabled').attr('disabled', 'disabled');
        return this;
    },
    onSuccessCallback: function (response) {
        this.trigger('driverUpload', response);
        this.hide();
        dialogs.systemConfirm.show(i18n['resource.dataSource.jdbc.upload.driverUploadSuccess']);
        _.defer(_.bind(this.remove, this));
    },
    onErrorCallback: function (response) {
        let errorMessage;
        response = response.responseJSON ? response.responseJSON : response;
        if ('illegal.parameter.value.error' === response.errorCode && response.parameters && response.parameters.length && 'className' === response.parameters[0]) {
            errorMessage = i18n['resource.dataSource.jdbc.classNotFound'].replace('{0}', this.driverClass);
        } else if (response.message) {
            errorMessage = response.message;
        } else {
            errorMessage = response.errorCode;    // see some notes in JRS-8435
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
    addFileInput: function () {
        this.$('ul').append(_.template(fileUploadTemplate, { fileIndex: this.fileIndex }));
        this.fileIndex++;
    },
    primaryButtonOnClick: function () {
        // clear the error message because we are doing new upload
        this.$('.errorMessageContainer').removeClass('error').find('.message').text('');

        // disable the primary button to block a second click
        this.$('button.primary').addClass('disabled').attr('disabled', 'disabled');
        // add a spinner and overlay with some delay
        $("body").append(this._overlay.$el);
        this._overlay.show();

        const form = this.$('form');
        const submitter = (new AjaxFormSubmitter(form[0])).submit();
        submitter.done((response) => {
            if (response.errorCode) {
                this.onErrorCallback(response);
            } else {
                this.onSuccessCallback(response);
            }
        }).fail((response) => {
            this.onErrorCallback(response);
        }).always(() => {
            // remove overlay with spinner
            this._overlay.hide();
            $("body").detach(this._overlay.$el);
            // and enable the primary button back again
            this.$('button.primary').removeClass('disabled').attr('disabled', null);
        });
    },
    secondaryButtonOnClick: function () {

        // since the overlay start with some delay there is a change that user clicks "Cancel" button
        // and the overlay will trigger. To avoid this we need to stop the overlay as well.
        this._overlay.hide();
        $("body").detach(this._overlay.$el);

        // hide this dialog
        this.hide();
        _.defer(_.bind(this.remove, this));
    }
});
