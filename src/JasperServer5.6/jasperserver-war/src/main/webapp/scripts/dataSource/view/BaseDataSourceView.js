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

define(function(require) {
    "use strict";

    var $ = require("jquery"),
        _ = require("underscore"),
        resourceLocator = require("resource.locate"),
		ResourceModel = require("common/model/RepositoryResourceModel"),
        BaseDataSourceModel = require("dataSource/model/BaseDataSourceModel"),
        i18n = require("bundle!jasperserver_messages"),
        timezones = require("settings!userTimeZones"),
        Backbone = require("backbone"),
        nameAndDescriptionTemplate = require("text!dataSource/template/nameAndDescriptionTemplate.htm"),
        saveLocationTemplate = require("text!dataSource/template/saveLocationTemplate.htm"),
        timezoneTemplate = require("text!dataSource/template/timezoneTemplate.htm"),
        selectFromRepository = require("text!dataSource/template/dialog/selectFromRepository.htm"),
        dialogs = require("components.dialogs"),
        testConnectionTemplate = require("text!dataSource/template/testConnectionTemplate.htm"),
		testConnectionInProgress,
		testConnectionDetailsMessage,
        // tree dialog element ID. Should be removed if new tree component implemented
        TREE_DIALOG_ID="dataSource_selectFromRepository",
        // tree list element ID. Should be removed if new tree component implemented
        TREE_ID="addFileTreeRepoLocation";

    require("dataSource/validation/backboneValidationExtension");

    return Backbone.View.extend({
        PAGE_TITLE_NEW_MESSAGE_CODE: undefined,
        PAGE_TITLE_EDIT_MESSAGE_CODE: undefined,

        modelConstructor: BaseDataSourceModel,

        events: {
			"keyup input[type='text'], input[type='password'], input[type='checkbox'], textarea, select": "updateModelProperty",
            "change input[type='text'], input[type='password'], input[type='checkbox'], textarea, select": "updateModelProperty",
            "click #testDataSource": "testConnection",
			"click [name=testConnectionMessageDetails]" : "showTestConnectionMessageDetails"
        },

        initialize: function(options) {
            this.options = options;
            this.isEditMode = options.isEditMode;
            this.timezones = options.timezones ? options.timezones : timezones;
            this.skipLocation = options.skipLocation ? options.skipLocation : false;

            var modelAttrs = {};
            if(options.dataSource){
                modelAttrs = _.extend(modelAttrs, options.dataSource);
            }
            this.model = new this.modelConstructor(modelAttrs, options);

            Backbone.Validation.bind(this, {
                valid: this.fieldIsValid,
                invalid: this.fieldIsInvalid,
                forceUpdate: true,
                selector: "name"
            });
            if (this.model.initialization) {
                var self = this;
                this.model.initialization.done(function() {
                    self.render.apply(self);
                });
            } else {
                this.render();
            }
            this.setPageTitle();
        },

        testConnection: function() {
			if (testConnectionInProgress === true) return;

            var rc = this.model.testConnection(), self = this, msg, button;
			if (!rc) return;

			// set flag variable
			testConnectionInProgress = true;

			// disable the "Test Connection" button
			button = self.$el.find("#testDataSource");
			button.addClass("disabled");

			// clear the test connection message near the button
			msg = this.$el.find("[name=testConnectionMessage]");

			// next goes magic !
			msg.removeClass("warning success").addClass("hidden");
			msg.parent().addClass("error"); // special class name goes to parent
			msg.find("a").addClass("hidden"); // this hides the "Show Details" link

			rc.done(function() {

				msg.addClass("success").find("span").text(i18n["resource.dataSource.connectionState.passed"]);

            }).fail(function(xhr) {

				//parse the response
				var response = false;
				try { response = $.parseJSON(xhr.responseText) } catch(e){}

				// now, compose the message
				var errMsg = self.getTestConnectionErrorMessage(response);

				// and display it !
				msg.addClass("warning").find("span").text(errMsg.text);
				if (errMsg.details) {
					msg.find("a").removeClass("hidden");
					testConnectionDetailsMessage = errMsg.details;
				}

			}).always(function() {

				testConnectionInProgress = false;
				msg.removeClass("hidden"); // this line actually shows the test connection message
				button.removeClass("disabled");

			});
        },

		showTestConnectionMessageDetails: function() {
			dialogs.errorPopup.show(testConnectionDetailsMessage);
		},

		getTestConnectionErrorMessage: function(response) {
            var text = i18n["resource.dataSource.connectionState.failed"], details = false;

            if (response) {
                if(response.parameters && response.parameters[2]){
                    // if 3d parameter exist, then it's exception message and should be shown as connection error text
                    text = response.parameters[2];
                }
                if(response.parameters && response.parameters[3]){
                    // if 4th parameter exist, then it's exception stack trace, which should be shown as error details
                    details = response.parameters[3];
                }
            }

            return {
                text: text,
                details: details
            };
		},

        updateModelProperty: function(e) {
            var $targetEl = $(e.target),
                valueObj = {},
                attr = $targetEl.attr("name"),
                value = "checkbox" === $targetEl.attr("type") ? $targetEl.is(':checked') : $.trim($targetEl.val());

            valueObj[attr] = value;

            this.model.set(valueObj);

            if (!this.isEditMode) {
                if (attr === "name") {
                    var generatedId = ResourceModel.generateResourceName(this.model.get("label"));
                    if (value !== generatedId) {
                        this._idUpdatedManually = true;
                    }
                } else if (attr === "label" && !this._idUpdatedManually) {
                    var newId = ResourceModel.generateResourceName(value);
                    this.model.set("name", newId);
                    this.$("input[name='name']").val(newId);
                }
            }

			this.model.validate(valueObj);
        },

        render: function() {
            this.$el.empty();
            return this;
        },

        renderNameAndDescriptionSection: function() {
            this.$el.append(_.template(nameAndDescriptionTemplate, this.templateData()));
        },

        renderTimezoneSection: function() {
            this.$el.append(_.template(timezoneTemplate, this.templateData()));
        },

        renderSaveLocationSection: function () {
            if (!this.skipLocation) {
                this.$el.append(_.template(saveLocationTemplate, this.templateData()));

                this.$el.append(_.template(selectFromRepository, {
                    dialogId: TREE_DIALOG_ID,
                    treeId: TREE_ID,
                    i18n: i18n}));

                resourceLocator.initialize({
                    id: TREE_DIALOG_ID,
                    resourceInput: "parentFolderUri",
                    browseButton: "parentFolderUriBrowse",
                    treeId: TREE_ID,
                    providerId: "repositoryExplorerTreeFoldersProvider",
                    dialogTitle: i18n["resource.Add.Files.Title"],
                    onChange: _.bind(function (value) {
                        this.model.set("parentFolderUri", value);
                        this.model.validate({"parentFolderUri": value});
                    }, this)
                });
            }
        },

        renderTestConnectionSection: function() {
            this.$el.append(_.template(testConnectionTemplate, this.templateData()));
        },

        templateData: function() {
            return {
                i18n: i18n,
                modelAttributes: _.clone(this.model.attributes),
                timezones: this.timezones,
                isEditMode: this.isEditMode
            }
        },

        setPageTitle: function() {
            var title = this.isEditMode ? this.model.get("label") : i18n[this.PAGE_TITLE_NEW_MESSAGE_CODE];
            document.title = i18n["jsp.home.content_title"] + ": " + title;
			$("#display .showingToolBar > .content > .header > .title").text(title);
        },

        fieldIsValid: function(view, attr, selector) {
            var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
            $parentEl.removeClass("error");
            $parentEl.find(".message.warning").text("");
        },

        fieldIsInvalid: function(view, attr, error, selector) {
			if (error === true) {
				// don't show anything since this type of error is used when you need to
				// mark model as invalid and at the same time don't show any error message
				return;
			}
            var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
            $parentEl.addClass("error");
            $parentEl.find(".message.warning").text(error);
        },

        remove: function() {
            // remove all resource locator DOM elements
            // 1 and 2 are the beginning of timestamp
            $("div[id^='selectFromRepository1'], div[id^='selectFromRepository2']").remove();
            Backbone.Validation.unbind(this);
            Backbone.View.prototype.remove.call(this);
            return this;
        }
    });
});
