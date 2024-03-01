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
import BackboneValidation from 'js-sdk/src/common/extension/backboneValidationExtension';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import i18n from '../../../i18n/ImportExportValidationBundle.properties';
import BaseModel from 'js-sdk/src/common/model/BaseModel';
import ResourcesModel from '../model/ResourcesModel';
import exportModelAttributesFactory from '../factory/exportModelAttributesFactory';
import exportTypesEnum from '../enum/exportTypesEnum';
import i18nMessageUtil from 'js-sdk/src/common/util/i18nMessage';
import secureKeyTypeEnum from '../../import/enum/secureKeyTypeEnum';

var i18nMessage = i18nMessageUtil.extend({ bundle: i18n });

var MAX_LENGTH = 256,
    typeMap = {
        includeReports: "report",
        includeDomains: "domain",
        includeAdHocViews: "adhocDataView",
        includeDataSources: "dataSource",
        includeDashboards: "dashboard",
        includeOtherResourceFiles: "others" // TODO WAT?
    },

    ExportModel = BaseModel.extend({

        defaults: {
            "uris": ["/"],
            "fileName": "export.zip",
            "encryptFile": false,
            "includeRepositoryPermissions": true,
            "includeScheduledReportJobs": true,
            "includeDependencies": true,
            "includeFullResourcePath": true,
            'keyType': '',
            'keyAlias' :'',
            'invalidKeyError': '',
            'invalidSecureFileContentError': '',
            'customKeyElements' : '',
            'showCustomKey' : true,
            'key-Alias': ''
        },

        validation: {
            fileName: [
                {
                    required: true,
                    msg: new i18nMessage("export.file.name.empty")
                },
                {
                    maxLength: MAX_LENGTH,
                    msg: new i18nMessage("export.file.name.too.long", MAX_LENGTH)
                },
                {
                    fn: function(fileName) {
                        if (/[\\/?*%:|"<>]/g.test(fileName)) {
                            return new i18nMessage("export.file.name.contains.not.supported.characters");
                        }
                        if (/^[\.]{1,2}$/g.test(fileName)) {
                            return new i18nMessage("export.file.name.not.valid");
                        }
                    }
                }
            ],
            uris: [
                {
                    fn: function() {
                        var state = this.toExportTask();

                        return !(state.uris || state.roles || state.users || _.filter(state.parameters, function (param) {
                            return param !== "role-users" && param !== "users-roles";
                        }).length);
                    }
                }
            ]
        },

        initialize: function () {
            BaseModel.prototype.initialize.call(this);
            _.bindAll(this, "url", "save", "cancel", "toExportTask", "reset");

            this.resources = new ResourcesModel();
        },

        url: function () {
            return "rest_v2/export"
        },

        save: function () {
            var dfd = new $.Deferred(),
                tmpModel = new Backbone.Model(),
                self = this;

            tmpModel.url = this.url;

            var parameters = {};

            getResourceTypes(this).done(function(resourceTypes) {
                resourceTypes && (parameters.resourceTypes = resourceTypes);

                tmpModel.save(_.extend(self.toExportTask(), parameters)).done(function(response) {
                    dfd.resolve(response);
                }).fail(function(response) {
                    dfd.reject(response);
                    self.trigger("error", self, response);
                })
            });

            return dfd;
        },

        cancel: function() {
            var url = this.url() + "/" + this.id;

            this.destroy({url: url});
        },


        toExportTask: function () {
            var everything = this.get("everything"),
                roles = this.get("roles"),
                users = this.get("users"),
                uris = _.clone(this.get("uris")),
                includeReportJobs = everything || (this.get("includeReports") && this.get("includeScheduledReportJobs"));

            var repositoryExportOptions = {
                uris: uris,
                scheduledJobs: this.get("includeScheduledReportJobs") ? uris : null,
                parameters: []
            };
            _.extend(repositoryExportOptions, this.ExportOptions());
            this.get("includeRepositoryPermissions") && repositoryExportOptions.parameters.push("repository-permissions");

            !this.get("includeDependencies") && repositoryExportOptions.parameters.push("skip-dependent-resources");

            // drop parent org prefix from paths, add required attributes if exporting from sub-org and includeFullResourcePath is unchecked
            var matches = subOrgLevelMatches(uris && uris[0]);
            if (matches && !this.get("includeFullResourcePath")) {
                repositoryExportOptions.organization = matches.last();
                repositoryExportOptions.parameters.push("skip-suborganizations", "skip-attribute-values");
                repositoryExportOptions.uris = _.map(uris, function(uri) {
                    return uri.replace(matches.first(),"");
                });
                repositoryExportOptions.scheduledJobs = this.get("includeScheduledReportJobs") ? repositoryExportOptions.uris : null;
            }

            var tenantExportOptions = {
                uris: someResourceIsChecked(this) ? uris : null,
                roles: !_.isEmpty(roles) ? roles : null,
                users: !_.isEmpty(users) ? users : null,
                scheduledJobs: includeReportJobs ? uris : null,
                organization: this.get("organization"),
                parameters: getConvertedParameters(this)
            };
            _.extend(tenantExportOptions, this.ExportOptions());

            return this.type !== exportTypesEnum.REPOSITORY
                ? tenantExportOptions
                : repositoryExportOptions;
        },

        ExportOptions: function() {
            if ( this.get("keyType") ===secureKeyTypeEnum.CUSTOMKEY ) {
                return {'keyAlias' : this.get("keyAlias")};
            } else if( this.get("keyType") ===secureKeyTypeEnum.UNIVERSALKEY ) {
                return {'keyAlias' : this.get("keyType")};
            } else{
                return {'keyAlias' : secureKeyTypeEnum.DEFAULTKEY};
            }
        },

        reset: function(opts, type) {
            this.type = type;

            var defaults = _.extend({}, this.defaults, exportModelAttributesFactory(type));

            this.clear().set(_.extend({}, defaults, opts));
        }
    });

_.extend(ExportModel.prototype, BackboneValidation.mixin);

function getConvertedParameters(exportModel) {
    var res = [],
        includeRepositoryPermissions,
        skipProfileAttributesValues;

    if (exportModel.get("everything")) {
        res.push("everything");

        exportModel.get("includeAccessEvents") && res.push("include-access-events");
    } else {
        includeRepositoryPermissions = someResourceIsChecked(exportModel) && exportModel.get("includeRepositoryPermissions") && !_.isEmpty(exportModel.get("uris"));
        skipProfileAttributesValues = !exportModel.get("includeAttributeValues") || !exportModel.get("includeAttributes");

        exportModel.get("userForRoles") && res.push("role-users");
        exportModel.get("rolesForUser") && res.push("users-roles");

        includeRepositoryPermissions && res.push("repository-permissions");
        !exportModel.get("includeSubOrganizations") && res.push("skip-suborganizations");

        exportModel.get("includeAttributes") && res.push("include-attributes");
        skipProfileAttributesValues && res.push("skip-attribute-values");

        exportModel.get("includeServerSettings") && res.push("include-server-settings");
        (!someResourceIsChecked(exportModel) || !exportModel.get("includeDependentObjects")) && res.push("skip-dependent-resources");
    }

    exportModel.get("includeAuditEvents") && res.push("include-audit-events");

    exportModel.get("includeMonitoringEvents") && res.push("include-monitoring-events");

    exportModel.get("encryptFile") && res.push("encrypted");

    return res;
}

function getResourceTypes(exportModel) {
    var res = [],
        dfd = new $.Deferred();

    !exportModel.get("everything") && someResourceIsChecked(exportModel)
        ? exportModel.resources.fetch().done(function(resources) {
            for (var key in typeMap) {
                exportModel.get(key) && res.push(resources[typeMap[key]]);
            }

            dfd.resolve(_.flatten(res));
        })
        : dfd.resolve(null);

    return dfd;
}

function someResourceIsChecked(exportModel) {
    var attributeNames = _.keys(typeMap);

    return _.filter(attributeNames, function(value) {
        return exportModel.get(value);
    }).length;
}

function subOrgLevelMatches(uri) {
    var organizationsFolderUri = jrsConfigs.organizationsFolderUri || "/organizations";
    var orgTemplateFolderUri = jrsConfigs.orgTemplateFolderUri || "/org_template";
    var regExp = new RegExp("^(" + organizationsFolderUri + "(?!" + orgTemplateFolderUri + ")/([^/]+))+");
    return regExp.exec(uri);
}

export default ExportModel;