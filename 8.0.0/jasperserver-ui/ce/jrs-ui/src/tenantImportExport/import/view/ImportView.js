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


/**
 * @author: Zakhar Tomchenko
 * @version:
 */

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import ImportModel from '../model/ImportModel';
import ImportStateModel from '../model/ImportStateModel';
import ImportPendingStateEnum from '../enum/importPendingStatesEnum';
import BrokenDependencyStrategyEnum from '../enum/brokenDependencyStrategyEnum';
import LoadingDialog from '../../export/view/LoadingDialog';
import MergeTenantDialogView from './MergeTenantDialogView';
import ImportDependentResourcesDialogView from './ImportDependentResourcesDialogView';
import ImportWarningsDialogView from './ImportWarningsDialogView';
import warningsFactory from '../factory/warningsFactory';
import Notification from 'js-sdk/src/common/component/notification/Notification';
import epoxyViewMixin from 'js-sdk/src/common/view/mixin/epoxyViewMixin';
import importTemplate from '../template/importTemplate.htm';
import i18n from '../../../i18n/ImportExportBundle.properties';
import i18n2 from 'js-sdk/src/i18n/CommonBundle.properties';
import secretKeyTemplate from '../../templates/secretKeyTemplate.htm' ;
import CustomKeyModel from '../../model/CustomKeyModel'
import RepositoryChooserDialogFactory from 'bi-repository/src/bi/repository/dialog/resourceChooser/RepositoryChooserDialogFactory';
import settings from "../../../settings/treeComponent.settings";
import repositoryResourceTypes from "bi-repository/src/bi/repository/enum/repositoryResourceTypes";
import secureKeyTypeEnum from '../enum/secureKeyTypeEnum';
import importRestErrorCodesEnum from '../enum/importRestErrorCodesEnum';

import importErrorMessageFactory from '../factory/importErrorMessageFactory';

var ImportView = Backbone.View.extend({
    tagName: "form",
    className: "import-view",
    id: "importDataFile",

    events: {
        "change input[type='file']": "validateFile",
        "change input.jr-jDefaultKey, input.jr-jKeyValue, input.jr-jKeyFile , input.jr-juniversalKeyValue,  input.jr-jCustomKey": "_onKeyTypeChange",
        "input input.jr-jSecretKey": "_onSecretKeyInput",
        "input input.jr-jSecretUri": "_onSecretFileInput",
        "click button.jr-jRepositoryBrowserButton": "_onRepositoryBrowserButtonClick",
        "click .checkBox label": "_clickOnCheckbox",
        "change .control.select.inline": "_onCustomKeyInput"
    },

    computeds: {
        'isKeyUseUniversal': {
            deps: ['keyType'],
            get: function(keyType) {
                return keyType === secureKeyTypeEnum.UNIVERSALKEY;
            }
        },
        'isKeyUseCustom': {
            deps: ['keyType'],
            get: function get(keyType) {
                return keyType === secureKeyTypeEnum.CUSTOMKEY ;
            }
        },
        'isKeyUseValue': {
            deps: ['keyType'],
            get: function(keyType) {
                return keyType === secureKeyTypeEnum.VALUE;
            }
        },
        'isKeyUseFile': {
            deps: ['keyType'],
            get: function(keyType) {
                return keyType === secureKeyTypeEnum.FILE;
            }
        }
    },

    initialize:function (options = {}) {
        this.model || (this.model = new ImportModel(null, {form: this.el}));

        this.stateModel = new ImportStateModel();

        this.customKeyStateModel = new CustomKeyModel();;

        this.loadingDialog = new LoadingDialog({
            content: i18n2["dialog.overlay.loading"]
        });

        const Dialog = RepositoryChooserDialogFactory.getDialog('item');

        this.keyFileDialog = options.keyFileDialog || new Dialog({
            disableListTab: true,
            treeBufferSize: parseInt(settings.treeLevelLimit, 10),
            resourcesTypeToSelect: [repositoryResourceTypes.SECURE_FILE]
        });

        this.keyFileDialog.on('close', () => {
            if (this.keyFileDialog.selectedResource && this.keyFileDialog.selectedResource.resourceUri) {
                const resourceUri = this.keyFileDialog.selectedResource.resourceUri;

                this.model.set('secretUri', resourceUri);
                this._clearSecureKeyErrors();
            }
        });

        this.mergeTenantDialogView = new MergeTenantDialogView();
        this.dependentResourcesDialogView = new ImportDependentResourcesDialogView();
        this.warningsDialogView = new ImportWarningsDialogView();

        this.notification = new Notification();

        this.listenTo(this.stateModel, "change:phase", this._handleImportPhase, this);

        var notFoundNotification = {
                delay: false,
                message: i18n["import.error.cancelled"]
            },
            unexpectedErrorNotification = {
                delay: false,
                message: i18n["import.error.unexpected"]
            };

        this.listenTo(this.model, "change", this._onModelChange);

        this.listenTo(this.model, "error:notFound", _.bind(this.notification.show, this.notification, notFoundNotification));
        this.listenTo(this.stateModel, "error:notFound", _.bind(this.notification.show, this.notification, notFoundNotification));

        this.listenTo(this.model, "error:internalServerError", _.bind(this.notification.show, this.notification, unexpectedErrorNotification));
        this.listenTo(this.stateModel, "error:internalServerError", _.bind(this.notification.show, this.notification, unexpectedErrorNotification));

        this.listenTo(this.model, "error", _.bind(this.loadingDialog.close, this.loadingDialog));
        this.listenTo(this.stateModel, "error", _.bind(this.loadingDialog.close, this.loadingDialog));

        this.listenTo(this.mergeTenantDialogView, "button:import", function() {
            this.model.set("mergeOrganization", true);
            if (this.model.get('keyType') === '') {
                this.model.set('keyAlias', secureKeyTypeEnum.DEFAULTKEY);
            } else if (this.model.get('keyType') === secureKeyTypeEnum.UNIVERSALKEY) {
                this.model.set('keyAlias', secureKeyTypeEnum.UNIVERSALKEY);
            }
            this.doImport();
        }, this);

        this.listenTo(this.mergeTenantDialogView, "button:cancel", _.bind(cancelImportTask, this, ImportStateModel.STATE.CANCELLED));

        this.listenTo(this.dependentResourcesDialogView, "button:skip", applyBrokenDependencyStrategy(this, BrokenDependencyStrategyEnum.SKIP));
        this.listenTo(this.dependentResourcesDialogView, "button:include", applyBrokenDependencyStrategy(this, BrokenDependencyStrategyEnum.INCLUDE));
        this.listenTo(this.dependentResourcesDialogView, "button:cancel", _.bind(cancelImportTask, this, ImportStateModel.STATE.CANCELLED));

        this.epoxifyView();
    },

    render: function (options) {
        var self = this;
        this.type = options.type;

        this.model.reset(this.type, {organization: options.tenantId});

        this.$el.html(_.template(importTemplate)({
            i18n: i18n,
            i18n2: i18n2,
            model: _.extend(this.model.toJSON(), {
                secureKeyTypes: secureKeyTypeEnum
            })
        }));
        this.customKeyStateModel.getCustomKeys().done(function(response) {
            if( response && response.length === 0){
                renderSecTemplate(self, false);
            }else if(response && response.responseJSON && response.responseJSON.error){
                customKeyFailure(self, response);
            } else {
                self.customKeyElements = response;
                self.model.set('keyAlias',response[0].alias);
                renderSecTemplate(self, true);
                for (var i = 0; i < response.length; ++i) {
                    var element = $("<option />").attr("value", response[i].alias);
                    element.text(response[i].label ? response[i].label : response[i].alias);
                    self.$el.find('#importCustomKey').append(element);
                }
            }
            self.applyEpoxyBindings();
        }).fail(function(response) {
            customKeyFailure(self, response);
        });
        return this;
    },

    validateFile: function (evt) {
        this.model.set("fileName", $(evt.target).val());

        var $file = $(evt.target),
            $parent = $file.parent();

        if (this.model.isValid(true)) {
            $parent.removeClass("error");
        } else {
            $parent.addClass("error");
        }
    },

    doImport: function () {
        let dfd = new $.Deferred();

        var self = this;

        this.loadingDialog.open();

        if (this.model.isValid(true)) {
            dfd = this.model.save().fail((res) => {
                this._onImportFail(res);
            }).always(function (res) {
                self.stateModel.set(res);
            });
        } else {
            dfd.reject();
        }

        return dfd;
    },

    _onImportFail: function(res) {
        const errorCode = res.errorCode;

        if (errorCode === importRestErrorCodesEnum.INVALID_SECRET_KEY) {
            this.model.set('invalidKeyError', i18n['import.invalid.secretKey']);
        } else if (errorCode === importRestErrorCodesEnum.INVALID_SECRET_FILE_CONTENT) {
            this.model.set('invalidSecureFileContentError', i18n['import.invalid.secretUri.secretFile']);
        } else if (errorCode === importRestErrorCodesEnum.INVALID_SECRET_FILE) {
            this.model.set('invalidSecureFileContentError', i18n['import.invalid.secretUri']);
        } else if (errorCode === importRestErrorCodesEnum.INVALID_SECRET_KEY_LENGTH) {
            this.model.set('invalidKeyError', i18n['import.invalid.secretKey.length']);
        }
    },

    _handleImportPhase: function() {
        var phase = this.stateModel.get("phase");

        if (phase !== ImportStateModel.STATE.INPROGRESS) {
            this.loadingDialog.close();
        }

        if (phase === ImportStateModel.STATE.READY) {
            finishImport.call(this, "import.finished", "success");
        }
        else if (phase === ImportStateModel.STATE.FAILED) {
            if (this.stateModel.get("error") && this.stateModel.get("error").errorCode) {
                finishImport.call(this, this.stateModel.get("error").errorCode);
            } else if (this.stateModel.get("message")) {
                finishImport.call(this, this.stateModel.get("message"));
            }
        } else if (phase === ImportStateModel.STATE.CANCELLED) {
            finishImport.call(this, "import.error.cancelled");
        }
        else if (phase === ImportStateModel.STATE.PENDING) {
            finishPendingImport.call(this);
        }
    },

    _onRepositoryBrowserButtonClick: function() {
        this.keyFileDialog.open();
    },

    _clearSecureKeyErrors: function() {
        this.model.set({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    },

    _onKeyTypeChange: function(event) {
        let radioValue = event.target.value;
        let element = this.$el.find('input[name ="key-alias"]')[0];
        let index = this.$el.find('#importCustomKey')[0] && this.$el.find('#importCustomKey')[0].selectedIndex;
        if(radioValue == secureKeyTypeEnum.CUSTOMKEY) {
            element.value = $(".control.select.inline").find('option')[index].value;
        }else if(radioValue == secureKeyTypeEnum.UNIVERSALKEY ){
            element.value = radioValue;
        }else if(radioValue == ''){
            element.value = secureKeyTypeEnum.DEFAULTKEY;
        }else{
            element.value ='' ;
        }
        this.model.set('keyType', radioValue);

        this._clearSecureKeyErrors();
    },

    _onSecretKeyInput: function(event) {
        const value = event.target.value;

        this.model.set('secretKey', value, {silent: true});

        this._clearSecureKeyErrors();
    },

    _onSecretFileInput: function(event) {
        const value = event.target.value;

        this.model.set('secretUri', value, {silent: true});

        this._clearSecureKeyErrors();
    },

    _clickOnCheckbox:function (evt) {
        var checkbox = $(evt.target).next();
        if (!checkbox[0].disabled) {
            checkbox[0].checked = !checkbox[0].checked;
            checkbox.trigger("change");
        }
    },

    _onCustomKeyInput: function(evt){
        const index = evt.target.selectedIndex;
        const  element = $(".control.select.inline").find('option')[index];
        this.model.set('keyAlias', element.value, {silent: true});
        this.$el.find('input[name ="key-alias"]')[0].value = element.value;
    },

    _onModelChange: function() {
        this.model.isValid(true);
    }
});

_.extend(ImportView.prototype, epoxyViewMixin);

function customKeyFailure(self, response) {
    renderSecTemplate(self, false);
    self.applyEpoxyBindings();
    self.notification.show({
        delay: false,
        message: response && response.responseJSON && response.responseJSON.message
    });
    self.trigger('error', self, response);
}

function renderSecTemplate(self, showCustomKey) {
    $(_.template(secretKeyTemplate, {
        i18n: i18n,
        i18n2: i18n2,
        model: _.extend(self.model.toJSON(), {
            secureKeyTypes: secureKeyTypeEnum
        }),
        showCustomKey: showCustomKey,
        showKeyValue: true,
        exportMode: false
    })).insertAfter(self.$el.find('fieldset:first'));
}
function getWarnings() {
    var warnings = this.stateModel.get("warnings");

    return _.map(warnings, function(value) {
        return warningsFactory(value);
    });
}

function finishImport(code, notificationType) {
    notificationType = notificationType || "warning";

    var warnings = getWarnings.call(this);

    if (code === "import.finished" && !_.isEmpty(warnings)) {
        this.warningsDialogView.open({items: warnings});
    }

    this.notification.show({
        delay: false,
        message: importErrorMessageFactory.create(code),
        type: notificationType
    });

    this.trigger("import:finished", this.model.get("organization"));

    this.model.reset(this.type, {}, this.customKeyElements);
}

function finishPendingImport(){
    var error = this.stateModel.get("error");

    if (error.errorCode === ImportPendingStateEnum.BROKEN_DEPS){
        this.dependentResourcesDialogView.open({items: error.parameters});

    } else if (error.errorCode === ImportPendingStateEnum.TENANT_MISMATCH){
        this.mergeTenantDialogView.open({fileTenantId: error.parameters[0], selectedTenantId: this.model.get("organization")});
    }
}

function applyBrokenDependencyStrategy(self, strategy){
    return function(str){
        self.model.set("brokenDependencies", strategy);
        self.doImport();
    };
}

function cancelImportTask(phase) {
    this.model.cancel();
    this.stateModel.set("phase", phase);
}

export default ImportView;