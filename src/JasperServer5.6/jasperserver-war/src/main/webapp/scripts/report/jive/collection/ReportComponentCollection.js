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


/**
 * @author: Igor Nesterenko
 * @version: $Id: ReportComponentCollection.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

define(function (require) {

    var Backbone = require("backbone"),
        _ = require("underscore"),
        //TODO: provide lazy loading if need
        BaseComponentModel = require("../model/BaseComponentModel"),
        ColumnComponentModel = require("../model/ColumnComponentModel"),
        TableComponentModel = require("../model/TableComponentModel"),
        CrosstabComponentModel = require("../model/CrosstabComponentModel"),
        ChartComponentModel = require("../model/ChartComponentModel"),
        FusionComponentModel = require("../model/FusionComponentModel"),
        GooglemapComponentModel = require("../model/GooglemapComponentModel"),
        WebfontsComponentModel = require("../model/WebfontsComponentModel"),
        HyperlinksComponentModel = require("../model/HyperlinksComponentModel"),
        jiveTypes = require("../enum/jiveTypes"),
        log = require("logger").register("ReportComponentCollection");

    var pagesRegexp = new RegExp("^(\\d+)(\\-\\d+)?$");

    function getPageIndex(pages) {
        var pageIndex = 1;

        if (pages) {
            if (_.isNumber(pages)) {
                pageIndex = pages;
            } else {
                try {
                    pageIndex = parseInt(pagesRegexp.exec(pages)[1], 10);
                } catch(ex) {}
            }
        }

        pageIndex--;

        return pageIndex;
    }

    function processLinkOptions(collection, linkOptions){
        var processed = _.clone(linkOptions);

        if (linkOptions.events){
            var newEvents = {};
            _.each(_.keys(linkOptions.events), function(key) {
                newEvents[key] = (function(handler, collection) {
                    return function(id, event) {
                        handler.call(this, event, _.findWhere(collection.getLinks(), {id: id}));
                    }
                })(linkOptions.events[key], collection);
            });

            processed.events = newEvents;
        }

        return processed;
    }

    return Backbone.Collection.extend({
        initialize: function(models, options){
            var self = this;

            this._parts = [];
            this._root = null;

            this.report = options.report;
            this.isolateDom = options.isolateDom;

            options.linkOptions && (this.linkOptions = processLinkOptions(this, options.linkOptions));

            // very dirty hack for JIVE code
            this.on("change add reset remove", function() {
                _.each(jiveTypes, function(value) {
                    self[value] = [];
                });

                self.forEach(function(componentModel) {
                    self[componentModel.get("type")].push(componentModel);
                });
            });
        },

        registerPart: function(part){
            var self = this;

            if (part) {
                if (!part.get("parentId")) {
                    self._root = part;

                    _.each(this._parts, function (part) {
                        self._root.registerPart(part);
                    });
                } else {
                    if (self._root) {
                        self._root.registerPart(part);
                    } else {
                        self._parts.push(part);
                    }
                }
            }
        },

        model : function(attrs, options) {
            var type = attrs["type"],
                result;

            options || (options = {});

            _.extend(options, { parent: options.collection.report, linkOptions: options.collection.linkOptions });

            if (jiveTypes.TABLE == type){
                result = new TableComponentModel(attrs, options);
            } else if(jiveTypes.CROSSTAB == type){
                result = new CrosstabComponentModel(attrs, options);
            } else if(jiveTypes.COLUMN == type){
               result = new ColumnComponentModel(attrs, options);
            } else if (jiveTypes.CHART == type){
                result = new ChartComponentModel(attrs, options);
            } else if(jiveTypes.GOOGLEMAP == type){
                result = new GooglemapComponentModel(attrs, options);
            } else if (jiveTypes.FUSION_CHART == type ||
                       jiveTypes.FUSION_MAP == type ||
                       jiveTypes.FUSION_WIDGET == type){
                result = new FusionComponentModel(attrs, options);
            } else if (jiveTypes.WEBFONTS == type){
                result = new WebfontsComponentModel(attrs, options);
            }else if (jiveTypes.HYPERLINKS == type){
                result = new HyperlinksComponentModel(attrs, options);
            } else {
                // create a generic model for unsupported component types for now
                result = new BaseComponentModel(attrs, options);
            }

            //group components by parent id
            options.collection.registerPart(result);

            return result;
        },

        url: function() {
            var url = this.report.contextPath;

            if (url[url.length-1] !== "/") {
                url += "/";
            }

            url += "getReportComponents.html";

            return url;
        },

        fetch: function() {
            if (!this.report.has("requestId")) {
                throw new Error("You must run report first before fetching components.");
            }

            return Backbone.Collection.prototype.fetch.call(this, {
                type: "POST",
                reset: true,
                headers: {
                    "Accept": "application/json",
                    "x-jrs-base-url" : this.report.contextPath
                },
                data : {
                    jasperPrintName : this.report.get("requestId"),
                    pageIndex: getPageIndex(this.report.execution.get("pages"))
                }
            });
        },
        add: function(models, options) {
            if (this.isolateDom) {
                _.each(models, function(model, index, models) {
                    if (model.type.indexOf("fusion") !== -1) {
                        log.info("Fusion components usage deprecated when isolateDom option enabled for report");
                        delete models[index];
                    }
                });
            }
            return Backbone.Collection.prototype.add.call(this, models, options);
        },

        parse: function(response) {
            return _.values(response);
        },

        getComponents: function() {

            var comps = _.chain(this.models)
                .map(function(model) {
                    if (model.toReportComponentObject){
                        return model.toReportComponentObject();
                    } else {
                        return null;
                    }
                })
                .flatten()
                .compact()
                .value();

            _.forEach(comps, function(component) {
                if (component.name === undefined) {
                    delete component.name;
                }
            });

            return comps;
        },

        getLinks: function() {
            return this.reduce(function(memo, model) {
                return model.get("type") === jiveTypes.HYPERLINKS ? memo.concat(model.get("hyperlinks")) : memo;
            },[]);
        },

        // hackish way to get changes to components
        updateComponents: function(reportComponents) {
            var self = this,
                actions = [],
                collection = new Backbone.Collection(this.map(function(model) {
                    var newModel = new Backbone.Model(model.attributes);
                    _.extend(newModel, {
                        updateFromReportComponentObject: model.updateFromReportComponentObject,
                        actions: model.actions,
                        parent: model.parent,
                        headingFormat: model.headingFormat,
                        detailsRowFormat: model.detailsRowFormat,
                        conditions: model.conditions
                    });

                    if (model.attachEvents) {
                        model.attachEvents.call(newModel);
                    }

                    return newModel;
                }));

            collection.forEach(function(model) {
                _.each(model.actions, function(func, action) {
                    self.listenToOnce(model, action, function(model, property, obj) {
                        actions.push(model.actions[action].call(model, obj));
                    });
                });
            });

            _.each(reportComponents, function(component) {
                var model = collection.get(component.id.split("/")[0]);

                if (model) {
                    model.updateFromReportComponentObject(component);
                }
            });

            return actions;
        }
    });
});

