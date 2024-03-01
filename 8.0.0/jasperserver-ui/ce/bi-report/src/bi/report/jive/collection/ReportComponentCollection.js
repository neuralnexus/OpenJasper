/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import jiveTypes from '../enum/jiveTypes';
import logger from "js-sdk/src/common/logging/logger";
import {pageComponentsMapping} from './pageComponentsMapping';

let localLogger = logger.register("ReportComponentCollection");

function processLinkOptions(collection, linkOptions) {
    var processed = _.clone(linkOptions);
    if (linkOptions.events) {
        var newEvents = {};
        _.each(_.keys(linkOptions.events), function (key) {
            newEvents[key] = function (handler, collection) {
                return function (id, event) {
                    handler.call(this, event, _.isObject(id) ? id : _.findWhere(collection.getLinks(), {id: id}));
                };
            }(linkOptions.events[key], collection);
        });
        processed.events = newEvents;
    }
    return processed;
}

export default Backbone.Collection.extend({
    initialize: function (models, options) {
        var self = this;
        this._rootParts = {};
        this.stateModel = options.stateModel;
        this.report = options.report;

        this.pageComponentsMeta = options.pageComponentsMeta;
        this.reportComponentsMeta = options.reportComponentsMeta;

        this.stateModel.get('linkOptions') && (this.linkOptions = processLinkOptions(this, this.stateModel.get('linkOptions')));
        this.listenTo(this.stateModel, 'change:linkOptions', function (model, value) {
            self.linkOptions = processLinkOptions(self, value);
        });
        // very dirty hack for JIVE code
        this.on('change add reset remove', function () {
            _.each(jiveTypes, function (value) {
                self[value] = [];
            });
            self.forEach(function (componentModel) {
                self[componentModel.get('type')] && _.isArray(self[componentModel.get('type')]) && self[componentModel.get('type')].push(componentModel);
            });
        });
        this.on("reset add", function () {
            self.forEach(function (model) {
                if (model && model.get("type")) {
                    if (model.get("type") === "bookmarks") {
                        self.trigger("bookmarksReady", model.get("bookmarks"));
                    }
                    if (model.get("type") === "reportparts") {
                        self.trigger("reportPartsReady", model.get("reportParts"));
                    }
                }
            });
        });
    },
    registerPart: function (part) {
        if (part) {
            const parentId = part.get("parentId");
            if (parentId) {
                const parent = this._rootParts[parentId];
                if (parent && parent.registerPart) {
                    localLogger.debug(`Registering ${part.get("type")}(${part.get("id")}) to parent ${parent.get("type")}(${parentId})!`);
                    parent.registerPart(part);
                } else {
                    localLogger.warn(`Unable to register ${part.get("type")}(${part.get("id")}) to parent ${parent.get("type")}(${parentId})!`);
                }
            } else if (part.has("id")) {
                localLogger.debug(`Part ${part.get("type")}(${part.get("id")}) has no parent!`);
                this._rootParts[part.get("id")] = part;
            }
        }
    },
    fetch: function () {
        const
            self = this,
            modules = [],
            metaModels = [],
            models = [],
            dfd = new $.Deferred();

        this.pageComponentsMeta.forEach(function (model) {
            const type = model.get('type');
            if (!type) {
                return;
            }

            if (!pageComponentsMapping[type]) {
                dfd.reject(`Page component does not registered for type [${type}]`)
            } else {
                modules.push(pageComponentsMapping[type]);

                metaModels.push(model.attributes);
            }
        });

        Promise.all(modules.map(m => m())).then((allModules) => {
            const options = {
                parent: self.report,
                linkOptions: self.linkOptions,
                collection: self,
                parse: true
            };

            _.each(allModules, function ({default: Module}, index) {
                const instance = new Module(metaModels[index], options);
                models.push(instance);
                self.registerPart(instance);
            });

            self.reset(models);
            dfd.resolve();
        }).catch(dfd.reject)

        return dfd;
    },
    fetchReportComponents: function () {
        var self = this,
            modules = [],
            metaModels = [],
            models = [],
            dfd = new $.Deferred();

        const moduleMapping = {
            'bookmarks': () => import("../model/BookmarksComponentModel"),
            'reportparts': () => import("../model/ReportPartsComponentModel")
        };

        this.reportComponentsMeta.forEach(function (model) {
            const type = model.get('type');
            if (!type) {
                return;
            }

            const module = moduleMapping[type];
            module && modules.push(module);

            metaModels.push(model.attributes);
        });

        Promise.all(modules.map(m => m())).then(function (allModules) {
            var options = {
                parent: self.report,
                linkOptions: self.linkOptions,
                collection: self,
                parse: true
            };

            _.each(allModules, function ({default: Module}, index) {
                var instance = new Module(metaModels[index], options);
                models.push(instance);
                self.registerPart(instance);
            });

            self.add(models, {merge: true});
            dfd.resolve();
        }).catch(dfd.reject);

        return dfd;
    },
    add: function (models, options) {
        var allowedModels = [];
        if (this.stateModel.get('isolateDom')) {
            _.each(models, function (model, index, models) {
                if (model.get('type') && (model.get('type').indexOf('fusion') !== -1 || model.get('type').indexOf('tibco-maps') !== -1)) {
                    if (model.get('type').indexOf('fusion') !== -1) {
                        localLogger.info('Fusion components usage deprecated when isolateDom option enabled for report');
                    }
                    if (model.get('type').indexOf('tibco-maps') !== -1) {
                        localLogger.info('Tibco maps components usage deprecated when isolateDom option enabled for report');
                    }
                } else {
                    allowedModels.push(models[index]);
                }
            });
            models = allowedModels;
        }
        return Backbone.Collection.prototype.add.call(this, models, options);
    },
    getComponents: function () {
        var comps = this.reduce(function (memo, model) {
            if (model.toReportComponentObject) {
                var obj = model.toReportComponentObject();
                if (!obj) {
                    return memo;
                }
                if (_.isArray(obj)) {
                    memo = memo.concat(obj);
                } else {
                    memo.push(obj);
                }
            }
            return memo;
        }, []);
        _.forEach(comps, function (component) {
            if (component.name === undefined) {
                delete component.name;
            }
        });
        return comps;
    },
    getLinks: function () {
        return this.reduce(function (memo, model) {
            return memo.concat(model.get('hyperlinks') || []);
        }, []);
    },
    getReportConfig: function () {
        const rc = this.findWhere({ type: "reportConfig" });
        if (rc) {
            return rc.get("reportConfig");
        }
        return null;
    },
    getBookmarks: function () {
        return this.reduce(function (memo, model) {
            return memo.concat(model.get('bookmarks') || []);
        }, []);
    },
    getReportParts: function () {
        return this.reduce(function (memo, model) {
            return memo.concat(model.get('reportParts') || []);
        }, []);
    },
    // hackish way to get changes to components
    updateComponents: function (reportComponents) {
        var self = this, actions = [], collection = new Backbone.Collection(this.map(function (model) {
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
        collection.forEach(function (model) {
            _.each(model.actions, function (func, action) {
                self.listenToOnce(model, action, function (model, property, obj) {
                    actions.push(model.actions[action].call(model, obj));
                });
            });
        });
        _.each(reportComponents, function (component) {
            var model = collection.get(component.id.split('/')[0]);
            if (model) {
                model.updateFromReportComponentObject(component);
            }
        });
        return actions;
    }
});
