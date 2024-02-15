/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

define(function (require) {
    "use strict";

    var
		ResourceModel = require("common/model/RepositoryResourceModel"),
        _ = require("underscore"),
        $ = require("jquery"),
        i18n = require("bundle!jasperserver_messages"),
        jrsConfigs = require("jrs.configs");

    return ResourceModel.extend({
        defaults: (function (){
            var defaults = {};

            _.extend(defaults, ResourceModel.prototype.defaults, {
                parentFolderUri: undefined,
                name: undefined,
                connectionType: undefined
            });

            return defaults;
        })(),

        validation: {
            label: [
                {
                    required: true,
                    msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.label"]
                },
                {
                    maxLength: ResourceModel.LABEL_MAX_LENGTH,
                    msg: i18n["ReportDataSourceValidator.error.too.long.reportDataSource.label"]
                }
            ],
            name: [
                {
                    required: true,
                    msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.name"]
                },
                {
                    maxLength: ResourceModel.NAME_MAX_LENGTH,
                    msg: i18n["ReportDataSourceValidator.error.too.long.reportDataSource.name"]
                },
                {
                    doesNotContainCharacters: ResourceModel.NAME_NOT_SUPPORTED_SYMBOLS,
                    msg: i18n["ReportDataSourceValidator.error.invalid.chars.reportDataSource.name"]
                }
            ],
            description: [
                {
                    required: false
                },
                {
                    maxLength: ResourceModel.DESCRIPTION_MAX_LENGTH,
                    msg: i18n["ReportDataSourceValidator.error.too.long.reportDataSource.description"]
                }
            ],
            parentFolderUri: [
                {
                    fn: function(value){
                        if(!this.skipLocation) {
                            if (_.isNull(value) || _.isUndefined(value) || (_.isString(value) && value === '')) {
                                return i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.parentFolderIsEmpty"];
                            }
                            if (value.slice(0, 1) !== '/') {
                                return i18n["ReportDataSourceValidator.error.folder.not.found"].replace("{0}", value);
                            }
                        }
                    }
                }
            ]
        },

        initialize: function(attributes, options) {
            this.options = options;
            this.skipLocation = options.skipLocation ? options.skipLocation : false;

			if (this.isNew()) {
				options.parentFolderUri && this.set("parentFolderUri", options.parentFolderUri, { silent: true });
			}

            var parentFolderUri = options.parentFolderUri ? options.parentFolderUri : attributes.parentFolderUri;

            if(attributes.name && parentFolderUri && !attributes.uri && options.isEditMode === true) {
                // if resource name and parent folder URI is given, but resource URI isn't set,
                // then let's generate it and set to the model for consistency purpose
	            // but we'll do it only in case of when we are in the editing mode
                this.set("uri", ResourceModel.constructUri(parentFolderUri, attributes.name), { silent: true });
            }

			ResourceModel.prototype.initialize.apply(this, arguments);
        },

		testConnection: function() {
			this.validate();
			if (!this._isValid) return;

			// launch the loading timer and create a deferred object to encapsulate this timer
			var dfr = $.Deferred(),
				loadingDialog = $("#" + ajax.LOADING_ID)[0],
				loadingDialogOpened = false,
				responseTimer = window.setTimeout(function() {
						loadingDialogOpened = true;
						dialogs.popup.show(loadingDialog, true);
					}, AjaxRequester.prototype.MAX_WAIT_TIME);

			var data = this.toJSON();
			$.ajax({
				type: "POST",
				url: jrsConfigs.contextPath + "/rest_v2/connections",
				contentType: data.connectionType,
				data: JSON.stringify(data)

			}).always(function(){

				// remove that timer and close the dialog if it was open
				window.clearTimeout(responseTimer);
				loadingDialogOpened && dialogs.popup.hide(loadingDialog);

			}).done(dfr.resolve).fail(dfr.reject);

			return dfr.promise();
		}
    });
});