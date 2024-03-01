/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';
import biComponentUtil from 'js-sdk/src/common/bi/component/util/biComponentUtil';
import BiComponent from 'js-sdk/src/common/bi/component/BiComponent';
import biComponentErrorCodes from 'js-sdk/src/common/bi/error/enum/biComponentErrorCodes';
import biComponentErrorFactoryReportProxy from './error/biComponentErrorFactoryReportProxy';
import ReportController from './ReportController';
import ReportPropertiesModel from './model/ReportPropertiesModel';
import reportOutputFormats from './enum/reportOutputFormats';
import interactiveComponentTypes from './jive/enum/interactiveComponentTypes';
import reportEvents from './enum/reportEvents';

import logger from "js-sdk/src/common/logging/logger";

import reportSchema from './schema/Report.json';
import reportExportSchema from './schema/ReportExport.json';
import reportSearchSchema from './schema/ReportSearch.json';
import reportSaveSchema from './schema/ReportSave.json';
import reportSaveAsSchema from './schema/ReportSaveAs.json';
import chartSchema from './schema/Chart.json';
import crosstabColumnSchema from './schema/CrosstabDataColumn.json';
import crosstabRowSchema from './schema/CrosstabRowGroup.json';
import tableColumnSchema from './schema/TableColumn.json';

let localLogger = logger.register("Report");

var propertyNames = _.keys(reportSchema.properties);
var fieldNames = ['properties'];
var readOnlyFieldNames = ['data'];
var reportBiComponentEvents = {
    CHANGE_TOTAL_PAGES: 'changeTotalPages',
    CHANGE_PAGES_STATE: 'changePagesState',
    CHANGE_LAST_PARTIAL_PAGE: 'changeLastPartialPage',
    CHANGE_SNAPSHOT_SAVE_STATUS: 'changeSnapshotSaveStatus',
    CAN_UNDO: 'canUndo',
    CAN_REDO: 'canRedo',
    CAN_SAVE: 'canSave',
    BEFORE_ACTION: 'beforeAction',
    AFTER_ACTION: 'afterAction',
    BEFORE_RENDER: 'beforeRender',
    AFTER_RENDER: 'afterRender',
    RESPONSIVE_BREAKPOINT_CHANGED: 'responsiveBreakpointChanged',
    PAGE_FINAL: 'pageFinal',
    PAGE_META_CHANGED: 'changePageMeta',
    REPORT_COMPLETED: 'reportCompleted',
    BOOKMARKS_READY: 'bookmarksReady',
    REPORTPARTS_READY: 'reportPartsReady',
    HTML_READY: 'htmlReady'
};
function reportParameters(params) {
    var parameters = null;
    if (params && _.keys(params).length) {
        parameters = {
            reportParameter: _.map(params, function (value, key) {
                return {
                    name: key,
                    value: value
                };
            })
        };
    }
    return parameters;
}
var componentTypeToSchema = {};
componentTypeToSchema[interactiveComponentTypes.CHART] = chartSchema;
componentTypeToSchema[interactiveComponentTypes.CROSSTAB_COLUMN] = crosstabColumnSchema;
componentTypeToSchema[interactiveComponentTypes.CROSSTAB_ROW] = crosstabRowSchema;
componentTypeToSchema[interactiveComponentTypes.TABLE_COLUMN] = tableColumnSchema;
function run(dfd, instanceData, controller, refresh, stateModel) {
    var validationResult = this.validate(), prevPages, prevAnchor, prevParameters, self = this, err;
    if (validationResult) {
        err = biComponentErrorFactoryReportProxy.validationError(validationResult);
        localLogger.error(err.toString());
        dfd.reject(err);
        return;
    }
    if (instanceData.properties.isolateDom && (_.isUndefined(instanceData.properties.defaultJiveUi) || instanceData.properties.defaultJiveUi.enabled !== false)) {
        err = biComponentErrorFactoryReportProxy.genericError(biComponentErrorCodes.UNSUPPORTED_CONFIGURATION_ERROR, 'Default JIVE UI should be disabled when isolateDom option is true');
        localLogger.error(err.toString());
        dfd.reject(err);
        return;
    }    // check if we have correct container (only in case it is provided)
    // check if we have correct container (only in case it is provided)
    if (instanceData.properties.container) {
        var $container = $(instanceData.properties.container);
        if (!($container.length && $container[0].nodeType == '1')) {
            err = biComponentErrorFactoryReportProxy.containerNotFoundError(instanceData.properties.container);
            localLogger.error(err.toString());
            dfd.reject(err);
            return;
        }    // set the report container
        // set the report container
        controller.view.setContainer($container);
    }
    // we assume that this cannot be changed and are set only once
    if (!_.isUndefined(instanceData.properties.resource.executionId)) {
        controller.model.set('executionId', instanceData.properties.resource.executionId);
    } else {
        controller.model.set('reportURI', instanceData.properties.resource);
    }
    controller.model.contextPath = instanceData.properties.server;    // so make it read-only
    // so make it read-only
    biComponentUtil.createReadOnlyProperty(this, 'server', instanceData, true, stateModel);
    biComponentUtil.createReadOnlyProperty(this, 'resource', instanceData, true, stateModel);    // for now "ignorePagination" property can be only set before report execution, so make it read-only
    // for now "ignorePagination" property can be only set before report execution, so make it read-only
    biComponentUtil.createReadOnlyProperty(this, 'ignorePagination', instanceData, true, stateModel);
    biComponentUtil.createReadOnlyProperty(this, "reportLocale", instanceData, true, stateModel);
    biComponentUtil.createReadOnlyProperty(this, "reportTimeZone", instanceData, true, stateModel);

    prevParameters = controller.model.execution.get('parameters');
    prevPages = controller.model.execution.get('pages');
    prevAnchor = controller.model.execution.get('anchor');
    var pages = _.isObject(instanceData.properties.pages) ? instanceData.properties.pages.pages : instanceData.properties.pages, anchor = _.isObject(instanceData.properties.pages) ? instanceData.properties.pages.anchor : undefined;
    if (!_.isUndefined(pages)) {
        _.extend(controller.model.getExport(reportOutputFormats.HTML).get('options'), {
            pages: pages,
            anchor: anchor
        });
        controller.model.execution.set({
            pages: pages,
            anchor: anchor,
            ignorePagination: instanceData.properties.ignorePagination,
            reportContainerWidth: instanceData.properties.reportContainerWidth,
            parameters: reportParameters(instanceData.properties.params)
        });
    } else {
        _.extend(controller.model.getExport(reportOutputFormats.HTML).get('options'), {
            pages: undefined,
            anchor: anchor
        });
        controller.model.execution.set({
            pages: undefined,
            anchor: anchor,
            ignorePagination: instanceData.properties.ignorePagination,
            reportContainerWidth: instanceData.properties.reportContainerWidth,
            parameters: reportParameters(instanceData.properties.params)
        });
    }

    if (instanceData.properties.reportLocale) {
        controller.model.execution.set({reportLocale: instanceData.properties.reportLocale});
    }
    if (instanceData.properties.reportTimeZone) {
        controller.model.execution.set({reportTimeZone: instanceData.properties.reportTimeZone});
    }

    var changedAttributes = controller.model.execution.changedAttributes(),
        parametersChanged = changedAttributes && "parameters" in changedAttributes,
        locationChanged = changedAttributes && ("pages" in changedAttributes || "anchor" in changedAttributes);

    var searchActionExecutedSuccessfuly = instanceData.searchActionExecutedSuccessfuly;

    instanceData.searchActionExecutedSuccessfuly = false;

    var tryUpdateComponents = function () {
        var dfd = $.Deferred(), err;
        if (!_.isObject(controller.updateComponent)) {
            return dfd.resolve();
        }
        var componentId = controller.updateComponent && controller.updateComponent.componentId, componentProps = controller.updateComponent && controller.updateComponent.componentProps, componentToUpdate = !_.isUndefined(componentId) && _.findWhere(controller.components.getComponents(), { name: componentId }) || _.findWhere(controller.components.getComponents(), { id: componentId });
        if (!componentToUpdate) {
            throw new Error('Component with such name or id \'' + componentId + '\' was not found');
        }
        var updatedComponent = _.extend(componentToUpdate, componentProps), componentSchema = componentTypeToSchema[updatedComponent.componentType];
        if (!componentSchema) {
            throw new Error('Cannot validate component - unknown component type \'' + updatedComponent.componentType + '\'');
        }
        var validationResult = biComponentUtil.validateObject(componentSchema, componentToUpdate);
        if (validationResult) {
            err = biComponentErrorFactoryReportProxy.validationError(validationResult);
            localLogger.error(err.toString());
            dfd.reject(err);
        } else {
            var actions = controller.components.updateComponents([updatedComponent]);
            if (actions && _.isArray(actions)) {
                actions = _.compact(actions);
            }
            if (actions && _.isArray(actions) && actions.length) {
                actions.silent = true;
                controller.runReportAction(actions).done(function () {
                    dfd.resolve(_.findWhere(controller.components.getComponents(), { name: componentId }));
                }).fail(function (error) {
                    if (error.source === 'export' || error.source === 'execution') {
                        err = biComponentErrorFactoryReportProxy.reportStatus(error);
                        if (_.include([
                            biComponentErrorCodes.REPORT_EXECUTION_FAILED,
                            biComponentErrorCodes.REPORT_EXECUTION_CANCELLED,
                            biComponentErrorCodes.REPORT_EXPORT_FAILED,
                            biComponentErrorCodes.REPORT_EXPORT_CANCELLED
                        ], err)) {
                            localLogger.error(err.toString());
                        } else {
                            localLogger.error('Report ' + error.source + (error.source === 'export' ? ' to format \'' + error.format + '\'' : '') + ' ' + error.status + ': ' + err.toString());
                        }
                        dfd.reject(err);
                    } else if (error.type === 'highchartsInternalError') {
                        err = biComponentErrorFactoryReportProxy.reportRender(error);
                        localLogger.error(err.toString());
                        dfd.reject(err);
                    } else {
                        err = biComponentErrorFactoryReportProxy.requestError(error);
                        localLogger.error(err.toString());
                        dfd.reject(err);
                    }
                });
            } else {
                dfd.resolve(_.findWhere(controller.components.getComponents(), { name: componentId }));
            }
        }
        return dfd;
    };
    var tryRenderReport = function () {
        var renderDfd = new $.Deferred();
        localLogger.debug('Starting trying to render report');
        if (!instanceData.properties.container) {
            renderDfd.resolve(self.data());
        } else {
            controller.renderReport().done(function () {
                try {
                    var data = self.data();
                    stateModel.set('_rendered', true);
                    renderDfd.resolve(data);
                } catch (ex) {
                    localLogger.error(ex.toString());
                    renderDfd.reject(ex);
                }
            }).fail(function (ex) {
                var err = biComponentErrorFactoryReportProxy.reportRender(ex);
                var stack = err.data && err.data.error && err.data.error.stack ? '\n' + err.data.error.stack : '';
                if (stack) {
                    localLogger.error(err.toString() + stack);
                } else {
                    localLogger.debug(err);
                }
                renderDfd.reject(err);
            });
        }
        return renderDfd;
    };
    var onSuccessfulRun = function () {
        tryRenderReport().done(function () {
            instanceData.data.totalPages = controller.model.get('totalPages');
            instanceData.data.components = controller.components.getComponents();
            instanceData.data.links = controller.components.getLinks();
            instanceData.data.bookmarks = controller.components.getBookmarks();
            instanceData.data.reportParts = controller.components.getReportParts();
            dfd.resolve(self.data());
        }).fail(dfd.reject);
    };
    var onFailedRun = function (error) {
        var err;
        controller.view.hideOverlay();
        parametersChanged && controller.model.execution.set({ parameters: prevParameters }, { silent: true });
        if (locationChanged) {
            controller.model.execution.set({
                pages: prevPages,
                anchor: prevAnchor
            }, { silent: true });
            if (!_.isUndefined(prevAnchor)) {
                instanceData.properties.pages = !_.isUndefined(prevPages) ? {
                    pages: prevPages,
                    anchor: prevAnchor
                } : { anchor: prevAnchor };
            } else {
                instanceData.properties.pages = prevPages;
            }
        }    // reject with specific error if we have one
        // reject with specific error if we have one
        if (error.errorDescriptor && error.errorDescriptor.errorCode) {
            err = biComponentErrorFactoryReportProxy.genericError(error.errorDescriptor.errorCode, error.errorDescriptor.message, error.errorDescriptor.parameters);
            localLogger.error(err.toString());
            dfd.reject(err);
        } else if (error.source === 'export' || error.source === 'execution') {
            err = biComponentErrorFactoryReportProxy.reportStatus(error);
            if (_.include([
                biComponentErrorCodes.REPORT_EXECUTION_FAILED,
                biComponentErrorCodes.REPORT_EXECUTION_CANCELLED,
                biComponentErrorCodes.REPORT_EXPORT_FAILED,
                biComponentErrorCodes.REPORT_EXPORT_CANCELLED
            ], err)) {
                localLogger.error(err.toString());
            } else {
                localLogger.error('Report ' + error.source + (error.source === 'export' ? ' to format \'' + error.format + '\'' : '') + ' ' + error.status + ': ' + err.toString());
            }
            dfd.reject(err);
        } else if (error.type === 'highchartsInternalError') {
            err = biComponentErrorFactoryReportProxy.reportRender(error);
            localLogger.error(err.toString());
            dfd.reject(err);
        } else {
            err = biComponentErrorFactoryReportProxy.requestError(error);
            localLogger.error(err.toString());
            dfd.reject(err);
        }
    };
    controller.view.showOverlay();
    if (controller.model.isNew()) {
        controller.executeReport(refresh).then(onSuccessfulRun, onFailedRun);
    } else {
        tryUpdateComponents().done(function () {
            if (parametersChanged || refresh) {
                controller.applyReportParameters(refresh).then(onSuccessfulRun, onFailedRun);
            } else {
                if (locationChanged || searchActionExecutedSuccessfuly || controller.updateComponent) {
                    controller
                        .fetchPageHtmlExportAndJiveComponents({
                            ignoreCancelledReportExecution: locationChanged // pagination for cancelled reports should still work
                        })
                        .then(onSuccessfulRun, onFailedRun);
                } else {
                    tryRenderReport().then(dfd.resolve, dfd.reject);
                }
            }
        }).fail(dfd.reject);
    }
    $("#embedvizcode #embedDrawer .embed button").length && $("#embedvizcode #embedDrawer .embed button").removeAttr('disabled');
}
function render(dfd, instanceData, controller, stateModel) {
    if (controller.view.setContainer(instanceData.properties.container) === false) {
        var err = biComponentErrorFactoryReportProxy.containerNotFoundError(instanceData.properties.container);
        localLogger.error(err.toString());
        dfd.reject(err);
        return;
    }
    controller.renderReport().done(function () {
        stateModel.set('_rendered', true);
        dfd.resolve(controller.view.$el[0]);
    }).fail(function (ex) {
        var err = biComponentErrorFactoryReportProxy.reportRender(ex);
        localLogger.error(err.toString());
        dfd.reject(err);
    });
}
function resize(dfd, instanceData, controller, stateModel) {
    if (!stateModel.get('_rendered')) {
        var err = biComponentErrorFactoryReportProxy.notYetRenderedError();
        localLogger.error(err.toString());
        dfd.reject(err);
    } else {
        var scaleFactor = controller.view.applyScale();
        dfd.resolve(scaleFactor);
    }
}
function cancel(dfd, controller) {
    controller.cancelReportExecution().done(dfd.resolve).fail(function (error) {
        var err = biComponentErrorFactoryReportProxy.requestError(error);
        localLogger.error(err.toString());
        dfd.reject(err);
    });
}
function cancelAsync(dfd, controller) {
    controller.cancelReportExecution(true).done(dfd.resolve).fail(function (error) {
        var err = biComponentErrorFactoryReportProxy.requestError(error);
        localLogger.error(err.toString());
        dfd.reject(err);
    });
}
function undo(dfd, controller) {
    controller.undoReportAction().done(dfd.resolve).fail(function (error) {
        var err = biComponentErrorFactoryReportProxy.requestError(error);
        localLogger.error(err.toString());
        dfd.reject(err);
    });
}
function undoAll(dfd, controller) {
    controller.undoAllReportAction().done(dfd.resolve).fail(function (error) {
        var err = biComponentErrorFactoryReportProxy.requestError(error);
        localLogger.error(err.toString());
        dfd.reject(err);
    });
}
function redo(dfd, controller) {
    controller.redoReportAction().done(dfd.resolve).fail(function (error) {
        var err = biComponentErrorFactoryReportProxy.requestError(error);
        localLogger.error(err.toString());
        dfd.reject(err);
    });
}
function destroy(dfd, controller, stateModel) {
    controller.destroy().done(function () {
        stateModel.set('_destroyed', true);
        dfd.resolve();
    }).fail(function (error) {
        var err = biComponentErrorFactoryReportProxy.requestError(error);
        localLogger.error(err.toString());
        dfd.reject(err);
    });
}
function createSaveAction(controller) {
    return function (inputOptions, success, error, complete) {
        var dfd = new $.Deferred();
        var successCallback = success,
            errorCallback = error,
            completeCallback = complete,
            options = inputOptions,
            schema,
            validationResult,
            keys;

        if (_.isFunction(inputOptions)) {
            // no options, only callbacks. Let's shift arguments.
            options = undefined;
            successCallback = inputOptions;
            errorCallback = success;
            completeCallback = error;
        }
        successCallback && _.isFunction(successCallback) && dfd.done(successCallback);
        errorCallback && _.isFunction(errorCallback) && dfd.fail(errorCallback);
        completeCallback && _.isFunction(completeCallback) && dfd.always(completeCallback);

        if (options) {
            if (_.isObject(options) && Object.keys(options).length > 0){
                keys = Object.keys(options);
                if (keys.length === 1 && keys[0] === "inputControlsOrder") {
                    schema = reportSaveSchema;
                } else {
                    schema = reportSaveAsSchema;
                }
            } else {
                schema = _.omit(reportSaveAsSchema, "required");
            }
            validationResult = biComponentUtil.validateObject(schema, options);
        }

        if (validationResult) {
            var err = biComponentErrorFactoryReportProxy.validationError(validationResult);
            localLogger.error(err.toString());
            dfd.reject(err);
        } else {
            if (controller.model.get("canSave") !== true) {
                var message = "Report cannot be saved!"
                localLogger.warn(message);
                dfd.reject(new Error(message));
            } else {
                controller.save(options)
                    .done(function (actionResponse) {
                        dfd.resolve(actionResponse);
                    })
                    .fail(function (error) {
                        var responseJson = error && error.responseText ? JSON.parse(error.responseText) : null, err;
                        if (responseJson && responseJson.result && responseJson.result.code && responseJson.result.msg) {
                            err = biComponentErrorFactoryReportProxy.genericError(responseJson.result.code, responseJson.result.msg);
                        } else {
                            err = biComponentErrorFactoryReportProxy.requestError(error);
                        }
                        localLogger.error(err.toString());
                        dfd.reject(err);
                    });
            }
        }
        return dfd;
    };
}
function createRunZoomAction(controller) {
    return function (inputOptions, success, error, complete) {
        var dfd = new $.Deferred();
        var successCallback = success, errorCallback = error, completeCallback = complete, options = inputOptions;
        if (_.isFunction(inputOptions)) {
            // no options, only callbacks. Let's shift arguments.
            options = undefined;
            successCallback = inputOptions;
            errorCallback = success;
            completeCallback = error;
        }
        successCallback && _.isFunction(successCallback) && dfd.done(successCallback);
        errorCallback && _.isFunction(errorCallback) && dfd.fail(errorCallback);
        completeCallback && _.isFunction(completeCallback) && dfd.always(completeCallback);
        // TODO: add validation for running zoom action?
        // var validationResult = options ? biComponentUtil.validateObject(reportSaveSchema, options) : undefined;
        // if (validationResult) {
        //     var err = biComponentErrorFactoryReportProxy.validationError(validationResult);
        //     localLogger.error(err.toString());
        //     dfd.reject(err);
        // } else {
        controller
            .runZoomAction(options)
            .done(function(actionResponse) {
                dfd.resolve(actionResponse);
            })
            .fail(function (error) {
                var responseJson = error && error.responseText ? JSON.parse(error.responseText) : null, err;
                if (responseJson && responseJson.result && responseJson.result.code && responseJson.result.msg) {
                    err = biComponentErrorFactoryReportProxy.genericError(responseJson.result.code, responseJson.result.msg);
                } else {
                    err = biComponentErrorFactoryReportProxy.requestError(error);
                }
                localLogger.error(err.toString());
                dfd.reject(err);
            });
        // }
        return dfd;
    };
}
function createStatusAction(controller) {
    return function() {
        return controller.model.get("status");
    }
}
function createHtmlExportFinalAction(controller) {
    return function() {
        var htmlExport = controller.model.getExport(reportOutputFormats.HTML);
        if (htmlExport) {
            return htmlExport.get("outputFinal");
        }
        return null;
    }
}
function createClearExportContextCacheAction(controller) {
    return function() {
        var htmlExport = controller.model.getExport(reportOutputFormats.HTML);
        if (htmlExport) {
            // This ensures that a flag for clearing report context is passed when requesting an export execution
            controller.model.getExport(reportOutputFormats.HTML).resetFirstRun();
            return true;
        }

        return false;
    }
}

function createGetStackStateAction(controller) {
    return function() {
        return controller.stateStack.currentState();
    }
}

function createResetStackStateAction(controller) {
    return function() {
        controller.stateStack.reset();
    }
}

function createReportExecutionIdAction(controller) {
    return function() {
        return controller.model.get("requestId");
    }
}

function createExportAction(controller, stateModel) {
    return function (options, success, error, always) {
        var dfd, validationResult, err;
        if (stateModel.get('_destroyed')) {
            err = biComponentErrorFactoryReportProxy.alreadyDestroyedError();
            localLogger.error(err.toString());
            dfd = new $.Deferred().reject(err);
        } else {
            try {
                validationResult = biComponentUtil.validateObject(reportExportSchema, options);
                if (validationResult) {
                    dfd = new $.Deferred();
                    err = biComponentErrorFactoryReportProxy.validationError(validationResult);
                    localLogger.error(err.toString());
                    dfd.reject(err);
                } else {
                    dfd = controller.exportReport(options);
                }
            } catch (ex) {
                dfd = new $.Deferred();
                err = biComponentErrorFactoryReportProxy.javaScriptException(ex);
                localLogger.error(err.toString());
                dfd.reject(err);
            }
        }
        dfd.done(success).fail(error).always(always);
        return dfd;
    };
}

function createSearchAction(controller, instanceData, stateModel) {
    return function (options) {
        var dfd, validationResult, err;
        if (stateModel.get('_destroyed')) {
            err = biComponentErrorFactoryReportProxy.alreadyDestroyedError();
            localLogger.error(err.toString());
            dfd = new $.Deferred().reject(err);
        } else {
            try {
                validationResult = biComponentUtil.validateObject(reportSearchSchema, options);
                if (validationResult) {
                    dfd = new $.Deferred();
                    err = biComponentErrorFactoryReportProxy.validationError(validationResult);
                    localLogger.error(err.toString());
                    dfd.reject(err);
                } else {
                    dfd = controller.searchReportAction(options).then(function (result) {
                        var responseJson = result;

                        instanceData.searchActionExecutedSuccessfuly = true;

                        if (responseJson.actionResult && responseJson.actionResult.searchResults) {
                            return responseJson.actionResult.searchResults;
                        } else {
                            return [];
                        }
                    });
                }
            } catch (ex) {
                dfd = new $.Deferred();
                err = biComponentErrorFactoryReportProxy.javaScriptException(ex);
                localLogger.error(err.toString());
                dfd.reject(err);
            }
        }
        return dfd;
    };
}
function createUpdateComponentAction(controller, stateModel) {
    return function () {
        var self = this, dfd = $.Deferred(), componentProps, successCallback, errorCallback, completeCallback, componentId, err;
        if (_.isString(arguments[0])) {
            componentId = arguments[0];
            componentProps = arguments[1];
            successCallback = arguments[2];
            errorCallback = arguments[3];
            completeCallback = arguments[4];
        } else {
            componentProps = arguments[0];
            componentId = componentProps.id;
            successCallback = arguments[1];
            errorCallback = arguments[2];
            completeCallback = arguments[3];
        }
        successCallback && _.isFunction(successCallback) && dfd.done(successCallback);
        errorCallback && _.isFunction(errorCallback) && dfd.fail(errorCallback);
        completeCallback && _.isFunction(completeCallback) && dfd.always(completeCallback);
        if (!stateModel.get('_destroyed')) {
            try {
                controller.updateComponent = {
                    componentId: componentId,
                    componentProps: componentProps
                };
                self.run().always(function () {
                    controller.updateComponent = null;
                }).fail(function (err) {
                    controller.view.hideOverlay();
                    dfd.reject(err);
                }).done(function () {
                    dfd.resolve(!_.isUndefined(componentId) && _.findWhere(controller.components.getComponents(), { name: componentId }) || _.findWhere(controller.components.getComponents(), { id: componentId }));
                });
            } catch (ex) {
                err = biComponentErrorFactoryReportProxy.javaScriptException(ex);
                localLogger.error(err.toString());
                dfd.reject(biComponentErrorFactoryReportProxy.javaScriptException(ex));
            }
        } else {
            err = biComponentErrorFactoryReportProxy.alreadyDestroyedError();
            localLogger.error(err.toString());
            dfd.reject(err);
        }
        return dfd;
    };
}
function createEventsFunction(instanceData, eventManager, controller, stateModel) {
    return function (events) {
        if (stateModel.get('_destroyed')) {
            throw biComponentErrorFactoryReportProxy.alreadyDestroyedError();
        }
        var self = this;
        if (!events || !_.isObject(events) || !_.keys(events).length) {
            return self;
        }
        _.each(instanceData.events, function (value, key) {
            if (_.isFunction(value)) {
                if (key === reportBiComponentEvents.CHANGE_TOTAL_PAGES) {
                    eventManager.stopListening(controller.model, 'change:totalPages', value);
                } else if (key === reportBiComponentEvents.CHANGE_LAST_PARTIAL_PAGE) {
                    eventManager.stopListening(controller.model, 'change:lastPartialPage', value);
                } else if (key === reportBiComponentEvents.CHANGE_SNAPSHOT_SAVE_STATUS) {
                    eventManager.stopListening(controller.model, 'change:snapshotSaveStatus', value);
                } else if (key === reportBiComponentEvents.CAN_REDO || key === reportBiComponentEvents.CAN_UNDO) {
                    eventManager.stopListening(controller.stateStack, 'change:position', value);
                } else if (key === reportBiComponentEvents.CAN_SAVE) {
                    eventManager.stopListening(controller.model, 'change:canSave', value);
                } else if (key === reportBiComponentEvents.REPORT_COMPLETED) {
                    eventManager.stopListening(controller, reportEvents.REPORT_COMPLETED, value);
                } else if (key === reportBiComponentEvents.PAGE_FINAL) {
                    eventManager.stopListening(controller, reportEvents.PAGE_FINAL, value);
                } else if (key === reportBiComponentEvents.PAGE_META_CHANGED) {
                    eventManager.stopListening(controller, reportEvents.PAGE_META_CHANGED, value);
                } else if (key === reportBiComponentEvents.BEFORE_ACTION) {
                    eventManager.stopListening(controller, reportEvents.BEFORE_ACTION, value);
                } else if (key === reportBiComponentEvents.AFTER_ACTION) {
                    eventManager.stopListening(controller, reportEvents.AFTER_ACTION, value);
                } else if (key === reportBiComponentEvents.BEFORE_RENDER) {
                    eventManager.stopListening(controller.view, reportEvents.BEFORE_RENDER, value);
                } else if (key === reportBiComponentEvents.AFTER_RENDER) {
                    eventManager.stopListening(controller.view, reportEvents.AFTER_RENDER, value);
                } else if (key === reportBiComponentEvents.RESPONSIVE_BREAKPOINT_CHANGED) {
                    eventManager.stopListening(controller.view, reportEvents.RESPONSIVE_BREAKPOINT_CHANGED, value);
                } else if (key === reportBiComponentEvents.CHANGE_PAGES_STATE) {
                    eventManager.stopListening(controller, reportEvents.CURRENT_PAGE_CHANGED, value);
                } else if (key == reportBiComponentEvents.BOOKMARKS_READY) {
                    eventManager.stopListening(controller.components, reportBiComponentEvents.BOOKMARKS_READY, value);
                } else if (key == reportBiComponentEvents.REPORTPARTS_READY) {
                    eventManager.stopListening(controller.components, reportBiComponentEvents.REPORTPARTS_READY, value);
                } else if (key == reportBiComponentEvents.HTML_READY) {
                    eventManager.stopListening(controller, reportEvents.REPORT_HTML_READY, value);
                }
            }
        });
        _.each(events, function (value, key) {
            if (_.isFunction(value)) {
                if (key === reportBiComponentEvents.CHANGE_TOTAL_PAGES) {
                    instanceData.events[key] = function () {
                        value.call(self, controller.model.get('totalPages'));
                    };
                } else if (key === reportBiComponentEvents.CHANGE_LAST_PARTIAL_PAGE) {
                    instanceData.events[key] = function () {
                        value.call(self, controller.model.get('lastPartialPage'));
                    };
                } else if (key === reportBiComponentEvents.CHANGE_SNAPSHOT_SAVE_STATUS) {
                    instanceData.events[key] = function () {
                        value.call(self, controller.model.get('snapshotSaveStatus'));
                    };
                } else if (key === reportBiComponentEvents.CAN_UNDO) {
                    instanceData.events[key] = function () {
                        value.call(self, controller.stateStack.get('canUndo'));
                    };
                } else if (key === reportBiComponentEvents.CAN_REDO) {
                    instanceData.events[key] = function () {
                        value.call(self, controller.stateStack.get('canRedo'));
                    };
                } else if (key === reportBiComponentEvents.CAN_SAVE) {
                    instanceData.events[key] = function () {
                        value.call(self, controller.model.get('canSave'));
                    };
                } else if (key === reportBiComponentEvents.PAGE_FINAL) {
                    instanceData.events[key] = function (markup) {
                        value.call(self, markup);
                    };
                } else if (key === reportBiComponentEvents.PAGE_META_CHANGED) {
                    instanceData.events[key] = function (pageMeta) {
                        value.call(self, pageMeta);
                    };
                } else if (key === reportBiComponentEvents.REPORT_COMPLETED) {
                    instanceData.events[key] = function (status, error) {
                        if (error) {
                            try {
                                if (error.source === 'export' || error.source === 'execution') {
                                    error = biComponentErrorFactoryReportProxy.reportStatus(error);
                                } else {
                                    error = biComponentErrorFactoryReportProxy.requestError(error);
                                }
                            } catch (ex) {
                                error = biComponentErrorFactoryReportProxy.javaScriptException(ex);
                            }
                        }
                        value.call(self, status, error);
                    };
                } else if (key === reportBiComponentEvents.BEFORE_ACTION) {
                    instanceData.events[key] = function(actions, shouldCancel) {
                        value.call(self, actions, shouldCancel);
                    }
                } else if (key === reportBiComponentEvents.AFTER_ACTION) {
                    instanceData.events[key] = function(actions) {
                        value.call(self, actions);
                    }
                } else if (key === reportBiComponentEvents.BEFORE_RENDER) {
                    instanceData.events[key] = _.bind(value, self);
                } else if (key === reportBiComponentEvents.AFTER_RENDER) {
                    instanceData.events[key] = _.bind(value, self);
                } else if (key === reportBiComponentEvents.RESPONSIVE_BREAKPOINT_CHANGED) {
                    instanceData.events[key] = function({ width, previousWidth }) {
                        try {
                            var changed = controller.responsiveBreakpointChanged({ width, previousWidth });
                            if (changed) {
                                value.call(self);
                            }
                        } catch(e) {
                            value.call(self, e);
                        }
                    };
                } else if (key === reportBiComponentEvents.CHANGE_PAGES_STATE) {
                    instanceData.events[key] = _.bind(value, self);
                } else if (key == reportBiComponentEvents.BOOKMARKS_READY) {
                    instanceData.events[key] = function (bookmarks) {
                        bookmarks.length && value.call(self, bookmarks);
                    };
                } else if (key == reportBiComponentEvents.REPORTPARTS_READY) {
                    instanceData.events[key] = function (reportParts) {
                        reportParts.length && value.call(self, reportParts);
                    };
                } else if (key == reportBiComponentEvents.HTML_READY) {
                    instanceData.events[key] = function () {
                        value.call(self);
                    };
                }
            }
        });
        _.each(instanceData.events, function (value, key) {
            if (_.isFunction(value)) {
                if (key === reportBiComponentEvents.CHANGE_TOTAL_PAGES) {
                    eventManager.listenTo(controller.model, 'change:totalPages', value);
                } else if (key === reportBiComponentEvents.CHANGE_LAST_PARTIAL_PAGE) {
                    eventManager.listenTo(controller.model, 'change:lastPartialPage', value);
                } else if (key === reportBiComponentEvents.CHANGE_SNAPSHOT_SAVE_STATUS) {
                    eventManager.listenTo(controller.model, 'change:snapshotSaveStatus', value);
                } else if (key === reportBiComponentEvents.CAN_REDO) {
                    eventManager.listenTo(controller.stateStack, 'change:canRedo', value);
                } else if (key === reportBiComponentEvents.CAN_UNDO) {
                    eventManager.listenTo(controller.stateStack, 'change:canUndo', value);
                } else if (key === reportBiComponentEvents.CAN_SAVE) {
                    eventManager.listenTo(controller.model, 'change:canSave', value);
                } else if (key === reportBiComponentEvents.PAGE_FINAL) {
                    eventManager.listenTo(controller, reportEvents.PAGE_FINAL, value);
                } else if (key === reportBiComponentEvents.PAGE_META_CHANGED) {
                    eventManager.listenTo(controller, reportEvents.PAGE_META_CHANGED, value);
                } else if (key === reportBiComponentEvents.REPORT_COMPLETED) {
                    eventManager.listenTo(controller, reportEvents.REPORT_COMPLETED, value);
                } else if (key === reportBiComponentEvents.BEFORE_ACTION) {
                    eventManager.listenTo(controller, reportEvents.BEFORE_ACTION, value);
                } else if (key === reportBiComponentEvents.AFTER_ACTION) {
                    eventManager.listenTo(controller, reportEvents.AFTER_ACTION, value);
                } else if (key === reportBiComponentEvents.BEFORE_RENDER) {
                    eventManager.listenTo(controller.view, reportEvents.BEFORE_RENDER, value);
                } else if (key === reportBiComponentEvents.AFTER_RENDER) {
                    eventManager.listenTo(controller.view, reportEvents.AFTER_RENDER, value);
                } else if (key === reportBiComponentEvents.RESPONSIVE_BREAKPOINT_CHANGED) {
                    eventManager.listenTo(controller.view, reportEvents.RESPONSIVE_BREAKPOINT_CHANGED, value);
                } else if (key === reportBiComponentEvents.CHANGE_PAGES_STATE) {
                    eventManager.listenTo(controller, reportEvents.CURRENT_PAGE_CHANGED, value);
                } else if (key == reportBiComponentEvents.BOOKMARKS_READY) {
                    eventManager.listenTo(controller.components, reportBiComponentEvents.BOOKMARKS_READY, value);
                } else if (key == reportBiComponentEvents.REPORTPARTS_READY) {
                    eventManager.listenTo(controller.components, reportBiComponentEvents.REPORTPARTS_READY, value);
                } else if (key == reportBiComponentEvents.HTML_READY) {
                    eventManager.listenTo(controller, reportEvents.REPORT_HTML_READY, value);
                }
            }
        });
        return self;
    };
}
var Report = function (properties) {
    properties || (properties = {});
    var events = properties.events, instanceData = {
        properties: _.extend({
            pages: 1,
            autoresize: true,
            centerReport: false,
            useReportZoom: false,
            modalDialogs: true,
            chart: {},
            loadingOverlay: true
        }, properties),
        data: {
            totalPages: undefined,
            components: [],
            links: [],
            bookmarks: [],
            reportParts: []
        },
        searchActionExecutedSuccessfuly: false,
        events: {}
    };
    delete instanceData.properties.events;
    var stateModel = new ReportPropertiesModel(biComponentUtil.cloneDeep(instanceData.properties));
    biComponentUtil.createInstancePropertiesAndFields(this, instanceData, propertyNames, fieldNames, readOnlyFieldNames, stateModel);
    var controller = new ReportController(stateModel), eventManager = _.extend({}, Backbone.Events);    // hack to prevent CSS overrides
    // hack to prevent CSS overrides
    controller.view.$el.addClass('visualizejs _jr_report_container_ jr');
    eventManager.listenTo(controller.model, 'change:totalPages', function () {
        instanceData.data.totalPages = controller.model.get('totalPages');
    });
    eventManager.listenTo(controller.components, 'change add reset remove', function () {
        instanceData.data.components = controller.components.getComponents();
        instanceData.data.links = controller.components.getLinks();
        instanceData.data.bookmarks = controller.components.getBookmarks();
        instanceData.data.reportParts = controller.components.getReportParts();
    });
    _.extend(this, {
        validate: biComponentUtil.createValidateAction(instanceData, reportSchema, stateModel),
        run: biComponentUtil.createDeferredAction(run, stateModel, instanceData, controller, false, stateModel),
        refresh: biComponentUtil.createDeferredAction(run, stateModel, instanceData, controller, true, stateModel),
        render: biComponentUtil.createDeferredAction(render, stateModel, instanceData, controller, stateModel),
        resize: biComponentUtil.createDeferredAction(resize, stateModel, instanceData, controller, stateModel),
        cancel: biComponentUtil.createDeferredAction(cancel, stateModel, controller),
        cancelAsync: biComponentUtil.createDeferredAction(cancelAsync, stateModel, controller),
        status: createStatusAction(controller),
        htmlExportFinal: createHtmlExportFinalAction(controller),
        undo: biComponentUtil.createDeferredAction(undo, stateModel, controller),
        undoAll: biComponentUtil.createDeferredAction(undoAll, stateModel, controller),
        redo: biComponentUtil.createDeferredAction(redo, stateModel, controller),
        search: createSearchAction(controller, instanceData, stateModel),
        save: createSaveAction(controller),
        runZoomAction: createRunZoomAction(controller),
        destroy: biComponentUtil.createDeferredAction(destroy, stateModel, controller, stateModel),
        'export': createExportAction(controller, stateModel),
        updateComponent: createUpdateComponentAction(controller, stateModel),
        events: createEventsFunction(instanceData, eventManager, controller, stateModel),
        resetUndoRedoStack: createResetStackStateAction(controller),

        // internal API
        _clearExportContextCache: createClearExportContextCacheAction(controller),
        _getStackState: createGetStackStateAction(controller),
        _getExecutionId: createReportExecutionIdAction(controller)
    });    // init events
    // init events
    this.events(events);
};
Report.prototype = new BiComponent();
_.extend(Report, {
    exportFormats: [
        'pdf',
        'xlsx',
        'xls',
        'rtf',
        'csv',
        'xml',
        'odt',
        'ods',
        'docx',
        'pptx',
        'json',
        'data_csv'
    ],
    chart: {
        componentTypes: ['chart'],
        types: chartSchema.properties.chartType['enum']
    },
    table: {
        componentTypes: ['tableColumn'],
        column: {
            types: [
                'numeric',
                'boolean',
                'datetime',
                'string',
                'time'
            ]
        }
    },
    crosstab: {
        componentTypes: [
            'crosstabDataColumn',
            'crosstabRowGroup'
        ]
    }
});
export default Report;
