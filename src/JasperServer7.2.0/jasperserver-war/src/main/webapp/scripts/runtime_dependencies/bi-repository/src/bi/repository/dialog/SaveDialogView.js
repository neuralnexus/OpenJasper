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


/**
 * @author: Sergey Prilukin
 * @version: $Id$
 * @modified by Dima Gorbenko to work without ResourceID field controlled by user
 *
 *              !! ATTENTION !!
 * This dialog does not allow user control ResourceID field.
 * It works like AdHoc Save dialog but by using REST API instead
 */

define(function (require) {

    "use strict";

    var
        _ = require('underscore'),

        i18n = require('bundle!CommonBundle'),
        RepositoryResourceBundle = require('bundle!RepositoryResourceBundle'),

        DialogWithModelInputValidation = require("common/component/dialog/DialogWithModelInputValidation"),

        ConfirmationDialog = require("common/component/dialog/ConfirmationDialog"),

        repositoryTreeFactory = require("bi/repository/factory/repositoryTreeFactory"),
        repositoryResourceTypes = require("bi/repository/enum/repositoryResourceTypes"),

        dialogTemplate = require('text!./resourceChooser/template/saveDialogTemplate.htm'),

        mapXhrErrorToMessage = require("../util/errorHandling").mapXhrErrorToMessage,
        Notification = require('common/component/notification/Notification');

    var SAVE_AS_DIALOG_MIN_WIDTH = 440;
    var SAVE_AS_DIALOG_MIN_HEIGHT = 520;
    var SAVE_AS_DIALOG_MAX_HEIGHT = 574;
    var _savedOptions = {}; // object used to save options which came to us

    return DialogWithModelInputValidation.extend({

        constructor: function (options) {

            options || (options = {});
            _savedOptions = _.extend({}, options);

            this.isDialogOpened = false;
            this.errorMessages = options.errorMessages || {};

            var additionalCssClasses = options.additionalCssClasses || "",
                title = options.title || RepositoryResourceBundle["dialog.saveDialog.title"],
                label = options.label || RepositoryResourceBundle["dialog.saveDialog.label"],
                description = options.description || RepositoryResourceBundle["dialog.saveDialog.description"];

            this.showNotification = options.showNotification || true;
            this.notificationSuccessMessage = options.notificationSuccessMessage
                || RepositoryResourceBundle["dialog.saveDialog.notification.successMessage"];

            this.confirmationDialogTitle = options.confirmationDialogTitle
                || RepositoryResourceBundle["dialog.saveDialog.confirm.title"];

            this.confirmationDialogText = options.confirmationDialogText
                || RepositoryResourceBundle["dialog.saveDialog.confirm.conflict"];

            var folderTreeOptions = options.folderTreeOptions || {};

            DialogWithModelInputValidation.prototype.constructor.call(this, {
                modal: true,
                resizable: true,
                handles: options.handles,
                minWidth: options.minWidth || SAVE_AS_DIALOG_MIN_WIDTH,
                minHeight: options.minHeight || SAVE_AS_DIALOG_MIN_HEIGHT,
                maxHeight: options.maxHeight || SAVE_AS_DIALOG_MAX_HEIGHT,
                originalModel: options.model,
                defaultSelectedFolder: options.defaultSelectedFolder,
                additionalCssClasses: additionalCssClasses + " saveAs",
                folderTreeOptions: folderTreeOptions,
                title: options.title || RepositoryResourceBundle["dialog.saveDialog.title"],
                template: options.template,
                dialogButtonTemplate: options.dialogButtonTemplate,
                content: _.template(options.dialogContentTemplate || dialogTemplate)({
                    title: title,
                    label: label,
                    description: description
                }),
                buttons: [
                    {label: i18n["button.save"], action: "save", primary: true},
                    {label: i18n["button.cancel"], action: "cancel", primary: false}
                ]
            });

            this.on('button:save', _.bind(this._saveButtonClick, this));
            this.on('button:cancel', _.bind(this._cancelButtonClick, this));
        },

        initialize: function (options) {
            var self = this;

            DialogWithModelInputValidation.prototype.initialize.apply(this, arguments);

            //This is read only model
            //we'll made a clone to do any kind of changes
            this.originalModel = options.originalModel;

            this.mapXhrErrorToMessage = options.mapXhrErrorToMessage || mapXhrErrorToMessage;

            options.tooltipOptions && (options.tooltipOptions.attachTo = this.$el);

            this._defaultSelectedFolder = options.defaultSelectedFolder;

            this.foldersTree = repositoryTreeFactory({
                treeBufferSize: options.folderTreeOptions.treeBufferSize,
                treeItemsTemplate: options.folderTreeOptions.treeItemsTemplate,
                collapserSelector: options.folderTreeOptions.collapserSelector,
                openClass: options.folderTreeOptions.treeNodeIsOpenedClass,
                closedClass: options.folderTreeOptions.treeNodeIsClosedClass,
                processors: [
                    "folderTreeProcessor",
                    "treeNodeProcessor",
                    "i18nItemProcessor",
                    "filterPublicFolderProcessor",
                    "cssClassItemProcessor",
                    "fakeUriProcessor"
                ],
                types: [repositoryResourceTypes.FOLDER],
                tooltipOptions: {
                    attachTo: this.$el
                }
            });

            this.notification = new Notification();

            this.listenTo(this.foldersTree, "selection:change", function (selection) {
                var
                    parentFolderUri,
                    selectionKey = Object.keys(selection)[0];

                if (selection && selectionKey && _.isArray(selection) && selection[selectionKey] && selection[selectionKey].uri) {
                    parentFolderUri = selection[selectionKey].uri;
                }

                self.model.set("parentFolderUri", parentFolderUri);

                self.enableButton("save");
            });

            var foldersTreeContainerClass = options.folderTreeOptions.treeContainerClass || ".control.groupBox .body";

            this.$contentContainer.find(foldersTreeContainerClass).append(this.foldersTree.render().el);

            this.confirmationDialog = new ConfirmationDialog({
                title: this.confirmationDialogTitle,
                text: this.confirmationDialogText
            });

            this.listenTo(this.confirmationDialog, "button:yes", this._yesButtonConfirmationDialogClick);
            this.listenTo(this.confirmationDialog, "button:no", this._noButtonConfirmationDialogClick);
        },

        // ----------------------------------------------------------------------------------
        // Public methods:

        save: function () {
            this.setTitle(_savedOptions.title || RepositoryResourceBundle["dialog.saveDialog.title"]);

            this.model = this.getResourceOnSave(this.originalModel);

            if (this.model.isNew()) {
                this._openDialog();
            } else {
                this._saveModelOnServer();
            }
        },

        saveAs: function () {
            this.setTitle(_savedOptions.titleSaveAs || RepositoryResourceBundle["dialog.saveAsDialog.title"]);

            this._openDialog();
        },

        close: function () {
            this._closeDialog();
        },

        remove: function () {

            this._closeDialog();

            this.notification.remove();
            this.foldersTree.remove();
            this.confirmationDialog.remove();

            DialogWithModelInputValidation.prototype.remove.apply(this, arguments);
        },

        // ----------------------------------------------------------------------------------
        // Common helpful functions:

        _openDialog: function () {

            if (this.isDialogOpened === true) {
                return;
            }

            this.model = this.getResourceOnSaveAs(this.originalModel);

            this.bindValidation();

            if (this.model) {
                _.each(this.model.attributes, function (value, key) {
                    this.$('[name=' + key + ']').val(value);
                }.bind(this));
            }

            this._clearCommonErrorMessageOnDialog();

            DialogWithModelInputValidation.prototype.open.apply(this, arguments);

            this.disableButton("save");

            this._preselectFolder();

            this.isDialogOpened = true;
        },

        _closeDialog: function () {

            if (this.isDialogOpened === false) {
                return;
            }

            if (this.foldersTree) {
                this.foldersTree.collapse("/root", {silent: true});
                this.foldersTree.collapse("/public", {silent: true});
                this.foldersTree.resetSelection();
            }

            this.unbindValidation();
            this.clearValidationErrors();

            DialogWithModelInputValidation.prototype.close.apply(this, arguments);

            this.model = undefined;
            this.isDialogOpened = false;
        },

        _onDialogResize: function () {
            var self = this;
            var heightReservation = 20;
            var otherElementsHeight = 0;
            var treeBox = this.$contentContainer.find(".control.groupBox.treeBox");
            var dialogBody = this.$contentContainer.closest(".jr-mDialog > .jr-mDialog-body");

            this.$contentContainer
                .children()
                .not(treeBox)
                .each(function () {
                    otherElementsHeight += self.$(this).outerHeight(true);
                });

            treeBox.height(dialogBody.outerHeight(true) - otherElementsHeight - heightReservation);
        },

        _preselectFolder: function () {

            // This function should select some folder in the tree once the dialog is opened.
            // There can be several "sources" of folder which we might select:
            // 1) pre-default selection given during dialog initialization
            // 2) last selected folder (user opened dialog, selected folder and clicked "OK")
            // So, the logic would be the next:
            // if we have "last selected folder" then use it, if not:
            // if we have "pre-default selection" the use it, if not: nothing to do

            var folderToSelect = false;
            if (this._lastSelectedFolder) {
                folderToSelect = this._lastSelectedFolder;
            } else if (this._defaultSelectedFolder) {
                folderToSelect = this._defaultSelectedFolder;
            }
            if (!folderToSelect) {
                // nothing to do
                return;
            }

            var scrollArea = this.foldersTree.$el.parent();
            this.foldersTree._selectTreeNode(folderToSelect, scrollArea);
            this.model.set("parentFolderUri", folderToSelect);
        },

        _showNotification: function (options) {
            
            var
                messageType = options.type || "success",
                message = options.message;

            if (!this.showNotification) {
                return;
            }
            if (!message) {
                return;
            }

            this.notification.show({
                message: message,
                type: messageType
            });
        },

        _showCommonErrorMessageOnDialog: function (message) {
            var
                errorHolder = this.$("[name=commonErrorMessage]");

            errorHolder.addClass("error");
            errorHolder.find("span").text(message);
        },

        _clearCommonErrorMessageOnDialog: function () {
            var
                errorHolder = this.$("[name=commonErrorMessage]");

            errorHolder.removeClass("error");
            errorHolder.find("span").text("");
        },

        _findNameOfTheLabel: function (resourcesOnServer) {

            var
                labelWeAreLookingFor = this.model.get("label"),
                resourceWithTheSameLabel;

            resourceWithTheSameLabel = _.find(resourcesOnServer, function(resource) {
                return resource.label === labelWeAreLookingFor;
            });

            if (_.isUndefined(resourceWithTheSameLabel)) {
                return null;
            }

            return resourceWithTheSameLabel;
        },

        // ----------------------------------------------------------------------------------
        // User interaction handlers:

        _saveButtonClick: function () {

            this._clearCommonErrorMessageOnDialog();

            this.model.validate();
            if (!this.model.isValid()) {
                return;
            }

            // Saving selected folder by user. This folder will be pre-selected on next dialog appearance.
            this._lastSelectedFolder = this.model.get("parentFolderUri");

            this.disableButton("save");
            this.disableButton("cancel");

            this._checkIfLabelExistsOnServer();
        },

        _cancelButtonClick: function () {
            this._closeDialog();
        },

        _yesButtonConfirmationDialogClick: function () {
            this._saveModelOnServer({useExistingResource: true});
        },

        _noButtonConfirmationDialogClick: function () {
            this.enableButton("save");
            this.enableButton("cancel");
        },

        // ----------------------------------------------------------------------------------
        // Methods to check if the label entered by user is already in use on Server

        _checkIfLabelExistsOnServer: function () {

            this._resourceOnTheServerWithTheSameLabel = null;

            return this.model.checkLabelExistenceOnServer()
                .done(this._checkIfLabelExistsOnServerDone.bind(this))
                .fail(this._checkIfLabelExistsOnServerFail.bind(this));
        },

        _checkIfLabelExistsOnServerDone: function (result) {

            if (result.foundResources.length === 0) {
                this._saveModelOnServer({createNewResource: true});
                return;
            }

            var resource = this._findNameOfTheLabel(result.foundResources);
            if (resource === null) {
                // Server might return the list of resources which contains in their label our label,
                // like "Label2" and "Label3". They seems similar, but not the same.
                // Yes, this how server seeks: for inclusion and not for equality.
                this._saveModelOnServer({createNewResource: true});
                return;
            }

            if (resource.uri === this.model.get("uri")) {
                // looks like this is the same resource, no need to ask user to override it
                this._saveModelOnServer();
                return;
            }

            this._resourceOnTheServerWithTheSameLabel = resource;
            this.confirmationDialog.open();
        },

        _checkIfLabelExistsOnServerFail: function () {
            this._showCommonErrorMessageOnDialog(RepositoryResourceBundle["error.unknown.error"]);
            this.enableButton("save");
            this.enableButton("cancel");
        },

        // ----------------------------------------------------------------------------------
        // Model saving handler

        _saveModelOnServer: function (options) {

            var
                saveOptions;

            options = options || {
                    // Used to send POST request to create new resource based on label
                    createNewResource: false,

                    // Used to indicate that URI of the resource should be used from other resource
                    useExistingResource: false
            };

            // because RepositoryResourceModel represents our model and it has own logic
            // to decide what HTTP method to use, how to build URL, etc, we need to emulate
            this._clonedModelForSaving = this.model.clone();
            this._clonedModelForSaving.type = this.model.type;

            if (options.useExistingResource === true && this._resourceOnTheServerWithTheSameLabel) {
                // this is the case then we asked user about overriding resource
                // he responded "yes", and we need to override the resource existing on server
                // to do this we need to set the same "uri" of the model
                this._clonedModelForSaving.set({
                    uri: this._resourceOnTheServerWithTheSameLabel.uri,
                    version: this._resourceOnTheServerWithTheSameLabel.version
                }, {silent: true});
            }

            if (options.createNewResource === true) {

                // we have to make model look like a new model for RepositoryResourceModel
                this._clonedModelForSaving.set({
                    version: ""
                }, {silent: true});

                this._clonedModelForSaving.unset("uri", {silent: true});
            }

            saveOptions = this._getSaveOptions(options);

            return this._clonedModelForSaving.save({}, saveOptions);
        },

        _getSaveOptions: function (options) {
            return {
                createFolders: false,
                expanded: true,
                overwrite: true,
                success: this._saveModelOnServerDone.bind(this),
                error: this._saveModelOnServerFail.bind(this)
            };
        },

        _saveModelOnServerDone: function (model, data) {

            // copying back from cloned model into actual all values
            // which might be changed by server
            this.model.set(this._clonedModelForSaving.attributes, {silent: true});
            this._clonedModelForSaving = undefined;

            this._showNotification({
                message: this.notificationSuccessMessage
            });

            this.trigger("save", this, data);
            
            this._closeDialog();
        },

        _saveModelOnServerFail: function (model, xhr, options) {

            this._clonedModelForSaving = undefined;

            // be sure the dialog is opened to show error to user on dialog
            // (because it might be closed due to fast save procedure for existing models)
            this._openDialog();

            this.enableButton("save");
            this.enableButton("cancel");

            var message = this.mapXhrErrorToMessage(xhr, this.errorMessages);
            this._showCommonErrorMessageOnDialog(message);

            this.trigger("error", this, message);
        },

        // ------------------------------------------------------------------------------------
        // Functions to be overridden

        getResourceOnSave: function (model) {
            throw 'This method should be overridden'
        },

        getResourceOnSaveAs: function (model) {
            throw 'This method should be overridden'
        }
    });

});