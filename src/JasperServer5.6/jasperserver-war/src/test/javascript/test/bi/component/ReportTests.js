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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: ReportTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var  _ = require("underscore"),
        $ = require("jquery"),
        json3 = require("json3"),
        BiComponent = require("bi/component/BiComponent"),
        Report = require("bi/component/Report"),
        ReportModel = require("report/model/ReportModel"),
        ReportView = require("report/view/ReportView"),
        ReportStateStack = require("report/model/ReportStateStack"),
        ReportController = require("report/ReportController"),
        reportStatuses = require("report/enum/reportStatuses"),
        reportEvents = require("report/enum/reportEvents"),
        ReportExecutionModel = require("report/model/ReportExecutionModel"),
        ReportComponentCollection = require("report/jive/collection/ReportComponentCollection"),
        ReportExportModel = require("report/model/ReportExportModel"),
        JavaScriptExceptionBiComponentError = require("bi/error/JavaScriptExceptionBiComponentError"),
        ContainerNotFoundBiComponentError = require("bi/error/ContainerNotFoundBiComponentError"),
        SchemaValidationBiComponentError = require("bi/error/SchemaValidationBiComponentError"),
        RequestBiComponentError = require("bi/error/RequestBiComponentError"),
        ReportStatusError = require("bi/error/ReportStatusError"),
        biComponentErrorFactory = require("bi/error/biComponentErrorFactory"),
        interactiveComponentTypes = require("report/jive/enum/interactiveComponentTypes");

    var propertyNames = ['server', 'resource', 'params', 'pages'];

    describe('Report BI component', function(){

        describe('instance creation', function(){

            it("should create instance", function(){
                expect(new Report()).toBeDefined();
            });

            it("should be an BiComponent", function(){
                expect(new Report() instanceof BiComponent).toBeTruthy();
            });

            it("should create simple properties", function(){
                var inst = new Report(),
                    instancePropertyNames = _.functions(inst);

                _.each(propertyNames, function(property){
                    expect(_.indexOf(instancePropertyNames, property) < 0).toBeFalsy();
                });
            });

            it("should create common properties", function(){
                var inst = new Report(),
                    fieldNames = ['properties', 'data'],
                    instancePropertyNames = _.functions(inst);

                _.each(fieldNames, function(property){
                    expect(_.indexOf(instancePropertyNames, property) > -1).toBeTruthy();
                });
            });

            it("should set values to simple properties", function(){
                var inst = new Report(),
                    value = "sapi";

                _.each(propertyNames, function(property){
                    inst[property](value);
                    expect(inst[property]()).toEqual(value);
                });
            });

            it("should set values to simple properties and return instance", function(){
                var inst = new Report(),
                    value = "sapi";

                _.each(propertyNames, function(property){
                    expect(inst[property](value)).toBe(inst);
                });
            });

            it("should set values to common properties", function(){
                var inst = new Report(),
                    value = {resource: "9", pages: "2"};


                inst.properties(value);
                expect(inst.properties()).toEqual(value);
            });

            it("should set values to common properties and return instance", function(){
                var inst = new Report(),
                    value = {resource: "9", pages: "5"};

                expect(inst.properties(value)).toBe(inst);
            });

            it("should set values to proper instance", function(){
                var inst = new Report(),
                    inst2 = new Report(),
                    value = {resource: "9", pages: "2"},
                    value2 = {resource: "5", pages: "3"};

                inst.properties(value);
                inst2.properties(value2);

                expect(inst.properties()).toEqual(value);
                expect(inst2.properties()).toEqual(value2);
                expect(inst2.properties()).not.toEqual(inst.properties());
            });

            it("should set simple values to proper instance", function(){
                var inst = new Report(),
                    inst2 = new Report(),
                    value = "sapi",
                    value2 = "urg";

                _.each(propertyNames, function(property){
                    inst[property](value);
                    inst2[property](value2);

                    expect(inst[property]()).toEqual(value);
                    expect(inst2[property]()).toEqual(value2);
                    expect(inst2[property]()).not.toEqual(inst[property]());
                });
            });

            it("should set simple values via properties method", function() {
                var inst = new Report();

                _.each(propertyNames, function(property) {
                    var options = {};
                    options[property] = property;

                    inst.properties(options);

                    expect(inst.properties()[property]).toEqual(property);

                    options[property] = undefined;
                    inst.properties(options);

                    expect(inst.properties()[property]).not.toBeDefined();
                });
            });

            it("should set simple values and properties together", function() {
                var inst = new Report();

                inst.properties({resource:"q"});
                inst.server("server");
                inst.params({
                    "Country_multi_select":["Mexico"],
                    "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                });

                expect(inst.properties().resource).toEqual("q");
                expect(inst.properties().server).toEqual("server");
                expect(inst.properties().params).toEqual({
                    "Country_multi_select":["Mexico"],
                    "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                });
            });

            it("should have default data()", function() {
                var report = new Report();

                expect(report.data()).toEqual({
                    totalPages: undefined,
                    components: [],
                    links: []
                });
            });

            it("should have default 'pages' property", function() {
                var report = new Report();

                expect(report.pages()).toBe(1);
            });
        });

        describe("action 'run'", function(){
            var defaultSettings = {
                    server: "http://localhost:8080/jasperserver-pro",
                    resource: "/public/resource",
                    params: {
                        "Country_multi_select":["Mexico"],
                        "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                    },
                    pages: "2-4"
                },
                defaultData = { totalPages: 10, markup: "<table></table>" },
                server;

            beforeEach(function(){
                server = sinon.fakeServer.create();
            });

            afterEach(function(){
                server.restore();
            });

            it("should have run method", function(){
                var inst = new Report(defaultSettings);

                expect(inst.run).toBeDefined();
                expect(_.isFunction(inst.run)).toBeTruthy();

            });

            it("should return deferred", function(){
                var inst = new Report(defaultSettings),
                    res = inst.run();

                expect(_.isFunction(res.done)).toBeTruthy();
                expect(_.isFunction(res.fail)).toBeTruthy();
                expect(_.isFunction(res.always)).toBeTruthy();
            });

            it("should take callback and run it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.run(sucsess, failure);

                res.resolve();

                expect(sucsess.called).toBeTruthy();
                expect(failure.called).toBeFalsy();
            });

            it("should take errback and run it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.run(sucsess, failure);

                res.reject();

                expect(sucsess.called).toBeFalsy();
                expect(failure.called).toBeTruthy();
            });

            it("should take complete handler and run it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    complete = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.run(sucsess, failure, complete);

                res.resolve();

                expect(complete.called).toBeTruthy();
            });

            it("should take complete handler and run it on fail", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    complete = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.run(sucsess, failure, complete);

                res.reject();

                expect(complete.called).toBeTruthy();
            });

            it("should pass SchemaValidationBiComponentError instance to error callback if validation fails in 'run' method", function() {
                var failure = sinon.spy(),
                    validationErrorSpy = sinon.spy(biComponentErrorFactory, "validationError"),
                    inst = new Report({});

                inst.run(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof SchemaValidationBiComponentError).toBe(true);
                expect(validationErrorSpy).toHaveBeenCalled();

                validationErrorSpy.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback if exception happened in 'run' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.run(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                fetchStub.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of exception in 'run' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.run(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                fetchStub.restore();
            });

            it("should pass RequestBiComponentError instance to error callback in case of request error in 'run' method", function() {
                var failure = sinon.spy(),
                    server = sinon.fakeServer.create(),
                    requestErrorSpy = sinon.spy(biComponentErrorFactory, "requestError"),
                    inst = new Report(defaultSettings);

                server.respondWith([400, {}, json3.stringify({ message: "error" })]);

                inst.run(function() {}, failure);

                server.respond();

                var error = failure.getCall(0).args[0];

                expect(error instanceof RequestBiComponentError).toBe(true);
                expect(requestErrorSpy).toHaveBeenCalled();

                requestErrorSpy.restore();
                server.restore();
            });

            it("should set 'server', 'resource', 'params' and 'pages' to model", function() {
                var reportModelSetSpy = sinon.spy(ReportModel.prototype, "set"),
                    reportExecutionModelSetSpy = sinon.spy(ReportExecutionModel.prototype, "set"),
                    report = new Report(defaultSettings);

                report.run();

                expect(reportModelSetSpy).toHaveBeenCalledWith("reportURI", defaultSettings.resource);
                expect(reportExecutionModelSetSpy).toHaveBeenCalledWith({
                    "pages": defaultSettings.pages,
                    "parameters": {
                        "reportParameter": [
                            {
                                name: "Country_multi_select",
                                value: ["Mexico"]
                            },
                            {
                                name: "Cascading_state_multi_select",
                                value: ["Guerrero", "Sinaloa"]
                            }
                        ]
                    }
                });

                reportModelSetSpy.restore();
                reportExecutionModelSetSpy.restore();
            });

            it("should call 'executeReport' method of ReportController if model is new", function() {
                var runSpy = sinon.spy(ReportController.prototype, "executeReport"),
                    report = new Report(defaultSettings);

                report.run();

                expect(runSpy).toHaveBeenCalled();

                runSpy.restore();
            });

            it("should set totalPages, components and links to data() in case of success", function() {
                var successCallback = sinon.spy(),
                    components = [
                        {
                            id: "bla",
                            chartType: "StackedColumn",
                            componentType: "chart"
                        }
                    ],
                    links = [
                        {
                            "id":"106035432",
                            "selector":"._jrHyperLink.ReportExecution",
                            "type":"ReportExecution",
                            "typeValue":"Custom",
                            "params":{
                                "_report":"/AdditionalResourcesForTesting/Drill_Reports_with_Controls/drill_report",
                                "country":["USA"],
                                "state":[],
                                "city":"La Mesa"
                            }
                        }
                    ],
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        this.model.set("totalPages", 10, { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents").returns(components),
                    getLinksStub = sinon.stub(ReportComponentCollection.prototype, "getLinks").returns(links),
                    report = new Report(defaultSettings);

                report.run(successCallback);

                expect(report.data()).toEqual({ totalPages: 10, components: components, links: links });
                expect(successCallback).toHaveBeenCalledWith({ totalPages: 10, components: components, links: links });

                fetchStub.restore();
                getComponentsStub.restore();
                getLinksStub.restore();
            });

            it("should call ReportController 'renderReport' method and resolve deferred after render", function() {
                var successCallback = sinon.spy(),
                    renderStub = sinon.stub(ReportController.prototype, "renderReport", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        this.model.exports.reset([new ReportExportModel({ "output": defaultData.markup })], { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.run(successCallback);

                expect(report.data()).toEqual({ totalPages: undefined, components: [], links: [] });
                expect(successCallback).toHaveBeenCalledWith({ totalPages: undefined, components: [], links: [] });

                fetchStub.restore();
                renderStub.restore();
            });

            it("should call ReportController 'renderReport' method and resolve deferred even if render threw an Error", function() {
                var successCallback = sinon.spy(),
                    renderStub = sinon.stub(ReportController.prototype, "renderReport").throws(new Error("Error in render")),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        this.model.exports.reset([new ReportExportModel({ "output": defaultData.markup })], { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.run(successCallback);

                expect(report.data()).toEqual({ totalPages: undefined, components: [], links: [] });
                expect(successCallback).toHaveBeenCalledWith({ totalPages: undefined, components: [], links: [] });

                fetchStub.restore();
                renderStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
            });

            it("should call 'applyReportParameters' if model is not new and parameters were changed", function() {
                var successCallback = sinon.spy(),
                    changedAttributesStub = sinon.stub(ReportExecutionModel.prototype, "changedAttributes")
                        .returns({ parameters: { reportParameter: [ { name: "Country", value: "USA" } ]}}),
                    isNewStub = sinon.stub(ReportModel.prototype, "isNew").returns(false),
                    showViewStub = sinon.stub(ReportView.prototype, "showOverlay").returns(false),
                    applyParametersStub = sinon.stub(ReportController.prototype, "applyReportParameters", function() {
                        this.model.set("status", reportStatuses.EXECUTION, { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.run(successCallback);

                expect(changedAttributesStub).toHaveBeenCalled();
                expect(isNewStub).toHaveBeenCalled();
                expect(showViewStub).toHaveBeenCalled();
                expect(applyParametersStub).toHaveBeenCalled();
                expect(successCallback).toHaveBeenCalled();

                isNewStub.restore();
                showViewStub.restore();
                applyParametersStub.restore();
                changedAttributesStub.restore();
            });

            it("should not call 'applyReportParameters', but call 'fetchReportHtmlExportAndJiveComponents' if model is not new and parameters were not changed, but pages were changed", function() {
                var successCallback = sinon.spy(),
                    changedAttributesStub = sinon.stub(ReportExecutionModel.prototype, "changedAttributes").returns({ pages: "1" }),
                    isNewStub = sinon.stub(ReportModel.prototype, "isNew").returns(false),
                    showViewStub = sinon.stub(ReportView.prototype, "showOverlay").returns(false),
                    applyParametersSpy = sinon.spy(ReportController.prototype, "applyReportParameters"),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.run(successCallback);

                expect(changedAttributesStub).toHaveBeenCalled();
                expect(isNewStub).toHaveBeenCalled();
                expect(showViewStub).toHaveBeenCalled();
                expect(applyParametersSpy).not.toHaveBeenCalled();
                expect(fetchHtmlExportAndJiveComponentsStub).toHaveBeenCalled();
                expect(successCallback).toHaveBeenCalled();

                isNewStub.restore();
                showViewStub.restore();
                applyParametersSpy.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
                changedAttributesStub.restore();
            });

            it("should not do any requests to server, but re-render report if model is not new and parameters and pages were not changed", function() {
                var successCallback = sinon.spy(),
                    changedAttributesStub = sinon.stub(ReportExecutionModel.prototype, "changedAttributes").returns({}),
                    isNewStub = sinon.stub(ReportModel.prototype, "isNew").returns(false),
                    showViewStub = sinon.stub(ReportView.prototype, "showOverlay").returns(false),
                    applyParametersStub = sinon.stub(ReportController.prototype, "applyReportParameters", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    updateStatusStub =  sinon.stub(ReportModel.prototype, "updateStatus", function() {
                        this.set("status", reportStatuses.EXECUTION, { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    renderReportStub = sinon.stub(ReportController.prototype, "renderReport", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                var $div = $("<div id='container_for_repor'></div>");
                $("body").append($div);

                report.container("#container_for_repor");

                report.run(successCallback);

                expect(changedAttributesStub).toHaveBeenCalled();
                expect(isNewStub).toHaveBeenCalled();
                expect(showViewStub).toHaveBeenCalled();
                expect(applyParametersStub).not.toHaveBeenCalled();
                expect(updateStatusStub).not.toHaveBeenCalled();
                expect(fetchHtmlExportAndJiveComponentsStub).not.toHaveBeenCalled();
                expect(renderReportStub).toHaveBeenCalled();
                expect(successCallback).toHaveBeenCalled();

                $div.remove();
                isNewStub.restore();
                showViewStub.restore();
                applyParametersStub.restore();
                updateStatusStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
                changedAttributesStub.restore();
                renderReportStub.restore();
            });

            it("should reset pages() to 1 after report execution", function() {
                var controller,
                    executeReportStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report({
                        server: "http://localhost:8080/jasperserver-pro",
                        resource: "/public/resource",
                        pages: 20
                    });

                report.run();

                controller.trigger(reportEvents.AFTER_REPORT_EXECUTION);

                expect(report.pages()).toEqual(1);

                executeReportStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
            });
        });

        describe("action 'refresh'", function(){
            var defaultSettings = {
                    server: "http://localhost:8080/jasperserver-pro",
                    resource: "/public/resource",
                    params: {
                        "Country_multi_select":["Mexico"],
                        "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                    },
                    pages: "2-4"
                },
                defaultData = { totalPages: 10, markup: "<table></table>" },
                server;

            beforeEach(function(){
                server = sinon.fakeServer.create();
            });

            afterEach(function(){
                server.restore();
            });

            it("should have refresh method", function(){
                var inst = new Report(defaultSettings);

                expect(inst.refresh).toBeDefined();
                expect(_.isFunction(inst.refresh)).toBeTruthy();

            });

            it("should return deferred", function(){
                var inst = new Report(defaultSettings),
                    res = inst.refresh();

                expect(_.isFunction(res.done)).toBeTruthy();
                expect(_.isFunction(res.fail)).toBeTruthy();
                expect(_.isFunction(res.always)).toBeTruthy();
            });

            it("should take callback and refresh it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.refresh(sucsess, failure);

                res.resolve();

                expect(sucsess.called).toBeTruthy();
                expect(failure.called).toBeFalsy();
            });

            it("should take errback and refresh it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.refresh(sucsess, failure);

                res.reject();

                expect(sucsess.called).toBeFalsy();
                expect(failure.called).toBeTruthy();
            });

            it("should take complete handler and run it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    complete = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.refresh(sucsess, failure, complete);

                res.resolve();

                expect(complete.called).toBeTruthy();
            });

            it("should take complete handler and run it on fail", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    complete = sinon.spy(),
                    inst = new Report(defaultSettings),
                    res = inst.refresh(sucsess, failure, complete);

                res.reject();

                expect(complete.called).toBeTruthy();
            });

            it("should pass SchemaValidationBiComponentError instance to error callback if validation fails in 'refresh' method", function() {
                var failure = sinon.spy(),
                    validationErrorSpy = sinon.spy(biComponentErrorFactory, "validationError"),
                    inst = new Report({});

                inst.refresh(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof SchemaValidationBiComponentError).toBe(true);
                expect(validationErrorSpy).toHaveBeenCalled();

                validationErrorSpy.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback if exception happened in 'refresh' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.refresh(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                fetchStub.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of exception in 'refresh' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.refresh(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                fetchStub.restore();
            });

            it("should pass RequestBiComponentError instance to error callback in case of request error in 'refresh' method", function() {
                var failure = sinon.spy(),
                    server = sinon.fakeServer.create(),
                    requestErrorSpy = sinon.spy(biComponentErrorFactory, "requestError"),
                    inst = new Report(defaultSettings);

                server.respondWith([400, {}, json3.stringify({ message: "error" })]);

                inst.refresh(function() {}, failure);

                server.respond();

                var error = failure.getCall(0).args[0];

                expect(error instanceof RequestBiComponentError).toBe(true);
                expect(requestErrorSpy).toHaveBeenCalled();

                requestErrorSpy.restore();
                server.restore();
            });

            it("should set 'server', 'resource', 'params' and 'pages' to model", function() {
                var reportModelSetSpy = sinon.spy(ReportModel.prototype, "set"),
                    reportExecutionModelSetSpy = sinon.spy(ReportExecutionModel.prototype, "set"),
                    report = new Report(defaultSettings);

                report.refresh();

                expect(reportModelSetSpy).toHaveBeenCalledWith("reportURI", defaultSettings.resource);
                expect(reportExecutionModelSetSpy).toHaveBeenCalledWith({
                    "pages": defaultSettings.pages,
                    "parameters": {
                        "reportParameter": [
                            {
                                name: "Country_multi_select",
                                value: ["Mexico"]
                            },
                            {
                                name: "Cascading_state_multi_select",
                                value: ["Guerrero", "Sinaloa"]
                            }
                        ]
                    }
                });

                reportModelSetSpy.restore();
                reportExecutionModelSetSpy.restore();
            });

            it("should call 'executeReport' method of ReportController if model is new", function() {
                var runSpy = sinon.spy(ReportController.prototype, "executeReport"),
                    report = new Report(defaultSettings);

                report.refresh();

                expect(runSpy).toHaveBeenCalled();
                expect(runSpy.calledWith(true)).toBeTruthy();

                runSpy.restore();
            });

            it("should set totalPages, components and links to data() in case of success", function() {
                var successCallback = sinon.spy(),
                    components = [
                        {
                            id: "bla",
                            chartType: "StackedColumn",
                            componentType: "chart"
                        }
                    ],
                    links = [
                        {
                            "id":"106035432",
                            "selector":"._jrHyperLink.ReportExecution",
                            "type":"ReportExecution",
                            "typeValue":"Custom",
                            "params":{
                                "_report":"/AdditionalResourcesForTesting/Drill_Reports_with_Controls/drill_report",
                                "country":["USA"],
                                "state":[],
                                "city":"La Mesa"
                            }
                        }
                    ],
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        this.model.set("totalPages", 10, { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents").returns(components),
                    getLinksStub = sinon.stub(ReportComponentCollection.prototype, "getLinks").returns(links),
                    report = new Report(defaultSettings);

                report.refresh(successCallback);

                expect(report.data()).toEqual({ totalPages: 10, components: components, links: links });
                expect(successCallback).toHaveBeenCalledWith({ totalPages: 10, components: components, links: links });

                fetchStub.restore();
                getComponentsStub.restore();
                getLinksStub.restore();
            });

            it("should call ReportController 'renderReport' method and resolve deferred after render", function() {
                var successCallback = sinon.spy(),
                    renderStub = sinon.stub(ReportController.prototype, "renderReport", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        this.model.exports.reset([new ReportExportModel({ "output": defaultData.markup })], { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.refresh(successCallback);

                expect(report.data()).toEqual({ totalPages: undefined, components: [], links: [] });
                expect(successCallback).toHaveBeenCalledWith({ totalPages: undefined, components: [], links: [] });

                fetchStub.restore();
                renderStub.restore();
            });

            it("should call ReportController 'renderReport' method and resolve deferred even if render threw an Error", function() {
                var successCallback = sinon.spy(),
                    renderStub = sinon.stub(ReportController.prototype, "renderReport").throws(new Error("Error in render")),
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        this.model.exports.reset([new ReportExportModel({ "output": defaultData.markup })], { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.refresh(successCallback);

                expect(report.data()).toEqual({ totalPages: undefined, components: [], links: [] });
                expect(successCallback).toHaveBeenCalledWith({ totalPages: undefined, components: [], links: [] });

                fetchStub.restore();
                renderStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
            });

            it("should call 'applyReportParameters' if model is not new and parameters were changed", function() {
                var successCallback = sinon.spy(),
                    changedAttributesStub = sinon.stub(ReportExecutionModel.prototype, "changedAttributes")
                        .returns({ parameters: { reportParameter: [ { name: "Country", value: "USA" } ]}}),
                    isNewStub = sinon.stub(ReportModel.prototype, "isNew").returns(false),
                    showViewStub = sinon.stub(ReportView.prototype, "showOverlay").returns(false),
                    applyParametersStub = sinon.stub(ReportController.prototype, "applyReportParameters", function() {
                        this.model.set("status", reportStatuses.EXECUTION, { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.refresh(successCallback);

                expect(changedAttributesStub).toHaveBeenCalled();
                expect(isNewStub).toHaveBeenCalled();
                expect(showViewStub).toHaveBeenCalled();
                expect(applyParametersStub).toHaveBeenCalled();
                expect(successCallback).toHaveBeenCalled();

                expect(applyParametersStub.calledWith(true)).toBeTruthy();

                isNewStub.restore();
                showViewStub.restore();
                applyParametersStub.restore();
                changedAttributesStub.restore();
            });

            it("should call 'applyReportParameters' even if model is not new and parameters were not changed, but pages were changed", function() {
                var successCallback = sinon.spy(),
                    changedAttributesStub = sinon.stub(ReportExecutionModel.prototype, "changedAttributes").returns({ pages: "1" }),
                    isNewStub = sinon.stub(ReportModel.prototype, "isNew").returns(false),
                    showViewStub = sinon.stub(ReportView.prototype, "showOverlay").returns(false),
                    applyParametersSpy = sinon.spy(ReportController.prototype, "applyReportParameters"),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.refresh(successCallback);

                expect(changedAttributesStub).toHaveBeenCalled();
                expect(isNewStub).toHaveBeenCalled();
                expect(showViewStub).toHaveBeenCalled();
                expect(applyParametersSpy).toHaveBeenCalled();

                isNewStub.restore();
                showViewStub.restore();
                applyParametersSpy.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
                changedAttributesStub.restore();
            });

            it("should refresh data even if model is not new and parameters and pages were not changed", function() {
                var successCallback = sinon.spy(),
                    changedAttributesStub = sinon.stub(ReportExecutionModel.prototype, "changedAttributes").returns({}),
                    isNewStub = sinon.stub(ReportModel.prototype, "isNew").returns(false),
                    showViewStub = sinon.stub(ReportView.prototype, "showOverlay").returns(false),
                    applyParametersStub = sinon.stub(ReportController.prototype, "applyReportParameters", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    updateStatusStub =  sinon.stub(ReportModel.prototype, "updateStatus", function() {
                        this.set("status", reportStatuses.EXECUTION, { silent: true });
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    renderReportStub = sinon.stub(ReportController.prototype, "renderReport", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.refresh(successCallback);

                expect(changedAttributesStub).toHaveBeenCalled();
                expect(isNewStub).toHaveBeenCalled();
                expect(showViewStub).toHaveBeenCalled();
                expect(applyParametersStub).toHaveBeenCalled();

                isNewStub.restore();
                showViewStub.restore();
                applyParametersStub.restore();
                updateStatusStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
                changedAttributesStub.restore();
                renderReportStub.restore();
            });

            it("should reset pages() to 1 after report execution", function() {
                var controller,
                    executeReportStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report({
                        server: "http://localhost:8080/jasperserver-pro",
                        resource: "/public/resource",
                        pages: 20
                    });

                report.refresh();

                controller.trigger(reportEvents.AFTER_REPORT_EXECUTION);

                expect(report.pages()).toEqual(1);

                executeReportStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
            });
        });

        describe("action 'render'", function(){
            var defaultSettings = {
                    server: "http://localhost:8080/jasperserver-pro",
                    resource: "/public/resource",
                    params: {
                        "Country_multi_select":["Mexico"],
                        "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                    }
                };

            it("should have 'render' method", function(){
                var inst = new Report(defaultSettings);

                expect(inst.render).toBeDefined();
                expect(_.isFunction(inst.render)).toBeTruthy();

            });

            it("should return deferred", function(){
                var inst = new Report(defaultSettings),
                    res = inst.render();

                expect(_.isFunction(res.done)).toBeTruthy();
                expect(_.isFunction(res.fail)).toBeTruthy();
                expect(_.isFunction(res.always)).toBeTruthy();
            });

            it("should pass ContainerNotFoundBiComponentError instance to error callback if container was not found in DOM when calling 'render' method", function() {
                var failure = sinon.spy(),
                    containerNotFoundSpy = sinon.spy(biComponentErrorFactory, "containerNotFoundError"),
                    fetchStub = sinon.stub(ReportController.prototype, "renderReport"),
                    inst = new Report(defaultSettings);

                inst.render(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof ContainerNotFoundBiComponentError).toBe(true);
                expect(containerNotFoundSpy).toHaveBeenCalled();

                containerNotFoundSpy.restore();
                fetchStub.restore();
            });

            it("should call ReportView 'render' method and resolve deferred after render with HTMLElement", function() {
                var successCallback = sinon.spy(),
                    renderStub = sinon.stub(ReportController.prototype, "renderReport", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                var $div = $("<div id='container_for_report'></div>");
                $("body").append($div);

                report.container("#container_for_report");

                report.render(successCallback);

                expect(successCallback).toHaveBeenCalled();
                expect(successCallback.getCall(0).args[0].tagName.toLowerCase()).toEqual("div");

                $div.remove();
                renderStub.restore();
            });
        });

        describe("action 'export'", function(){
            var defaultSettings = {
                    server: "http://localhost:8080/jasperserver-pro",
                    resource: "/public/resource",
                    params: {
                        "Country_multi_select":["Mexico"],
                        "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                    }
                };

            it("should have 'export' method", function(){
                var inst = new Report(defaultSettings);

                expect(inst.export).toBeDefined();
                expect(_.isFunction(inst.export)).toBeTruthy();

            });

            it("should return deferred", function(){
                var inst = new Report(defaultSettings),
                    res = inst.export({});

                expect(_.isFunction(res.done)).toBeTruthy();
                expect(_.isFunction(res.fail)).toBeTruthy();
                expect(_.isFunction(res.always)).toBeTruthy();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of exception in 'export' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    fetchStub = sinon.stub(ReportController.prototype, "exportReport").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.export({outputFormat: "pdf"}, function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                fetchStub.restore();
            });

            it("should call 'exportReport' of controller", function() {
                var successCallback = sinon.spy(),
                    exportStub = sinon.stub(ReportController.prototype, "exportReport", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    object = {outputFormat: "pdf"},
                    report = new Report(defaultSettings);

                report.export(object, successCallback);

                expect(exportStub).toHaveBeenCalled();
                expect(exportStub.getCall(0).args[0]).toBe(object);

                exportStub.restore();
            });
        });

        describe("action 'validate'", function(){
            it ("should have the method", function(){
                var inst = new Report();

                expect(inst.validate).toBeDefined();
                expect(_.isFunction(inst.validate)).toBeTruthy();
            });

            it ("should validate properties", function(){
                var inst = new Report();

                expect(inst.validate()).toBeDefined();
            });

            it ("should validate valid properties", function(){
                var inst = new Report({
                    server: "http://localhost:8080/jasperserver-pro",
                    resource: "/public/report",
                    params: {
                        "Country_multi_select":["Mexico"],
                        "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                    },
                    pages: "2-5"
                });

                expect(inst.validate()).not.toBeDefined();
            });

            it ("should validate on run and pass validation error to errback", function(){
                var inst = new Report({ server: "http://localhost:8080/jasperserver-pro" }),
                    spy = sinon.spy();

                inst.run(null, spy);

                expect(spy.called).toBeTruthy();
            });
        });

        describe("action 'cancel'", function(){
            var defaultSettings = {
                    server: "http://localhost:8080/jasperserver-pro",
                    resource: "/public/resource"
                };

            it("should have 'cancel' method", function(){
                var inst = new Report(defaultSettings);

                expect(inst.cancel).toBeDefined();
                expect(_.isFunction(inst.cancel)).toBeTruthy();

            });

            it("should return deferred", function(){
                var inst = new Report(defaultSettings),
                    res = inst.cancel();

                expect(_.isFunction(res.done)).toBeTruthy();
                expect(_.isFunction(res.fail)).toBeTruthy();
                expect(_.isFunction(res.always)).toBeTruthy();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of exception in 'cancel' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    cancelStub = sinon.stub(ReportController.prototype, "cancelReportExecution").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.cancel(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                cancelStub.restore();
            });

            it("should call ReportController 'cancelReportExecution' method and resolve deferred", function() {
                var successCallback = sinon.spy(),
                    cancelStub = sinon.stub(ReportController.prototype, "cancelReportExecution", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.cancel(successCallback);

                expect(cancelStub).toHaveBeenCalled();
                expect(successCallback).toHaveBeenCalled();

                cancelStub.restore();
            });
        });

        describe("actions 'undo/undoAll/redo'", function(){
            var defaultSettings = {
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/resource"
            };

            it("should have 'undo', 'undoAll' and 'redo' methods", function(){
                var inst = new Report(defaultSettings);

                expect(inst.undo).toBeDefined();
                expect(_.isFunction(inst.undo)).toBeTruthy();

                expect(inst.undoAll).toBeDefined();
                expect(_.isFunction(inst.undoAll)).toBeTruthy();

                expect(inst.redo).toBeDefined();
                expect(_.isFunction(inst.redo)).toBeTruthy();

            });

            it("should return deferred", function(){
                var inst = new Report(defaultSettings),
                    undoAction = inst.undo(),
                    redoAction = inst.redo(),
                    undoAllAction = inst.undoAll();

                expect(_.isFunction(undoAction.done)).toBeTruthy();
                expect(_.isFunction(undoAction.fail)).toBeTruthy();
                expect(_.isFunction(undoAction.always)).toBeTruthy();

                expect(_.isFunction(undoAllAction.done)).toBeTruthy();
                expect(_.isFunction(undoAllAction.fail)).toBeTruthy();
                expect(_.isFunction(undoAllAction.always)).toBeTruthy();

                expect(_.isFunction(redoAction.done)).toBeTruthy();
                expect(_.isFunction(redoAction.fail)).toBeTruthy();
                expect(_.isFunction(redoAction.always)).toBeTruthy();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of exception in 'undo/undoAll/redo' actions", function() {
                var failure1 = sinon.spy(),
                    failure2 = sinon.spy(),
                    failure3 = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    undoStub = sinon.stub(ReportController.prototype, "undoReportAction").throws(new Error("stub exception")),
                    redoStub = sinon.stub(ReportController.prototype, "redoReportAction").throws(new Error("stub exception")),
                    undoAllStub = sinon.stub(ReportController.prototype, "undoAllReportAction").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.undo(function() {}, failure1);
                inst.redo(function() {}, failure2);
                inst.undoAll(function() {}, failure3);

                expect(failure1.getCall(0).args[0] instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(failure2.getCall(0).args[0] instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(failure3.getCall(0).args[0] instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                undoStub.restore();
                redoStub.restore();
                undoAllStub.restore();
            });

            it("should call ReportController corresponding method and resolve deferred", function() {
                var successCallback1 = sinon.spy(),
                    successCallback2 = sinon.spy(),
                    successCallback3 = sinon.spy(),
                    undoStub = sinon.stub(ReportController.prototype, "undoReportAction", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    undoAllStub = sinon.stub(ReportController.prototype, "undoAllReportAction", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    redoStub = sinon.stub(ReportController.prototype, "redoReportAction", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings);

                report.undo(successCallback1);
                expect(undoStub).toHaveBeenCalled();
                expect(successCallback1).toHaveBeenCalled();

                report.undoAll(successCallback2);
                expect(undoAllStub).toHaveBeenCalled();
                expect(successCallback2).toHaveBeenCalled();

                report.redo(successCallback3);
                expect(redoStub).toHaveBeenCalled();
                expect(successCallback3).toHaveBeenCalled();

                undoStub.restore();
                undoAllStub.restore();
                redoStub.restore();
            });
        });

        describe("action 'updateComponent'", function(){
            var defaultSettings = {
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/resource"
            };

            it("should have 'updateComponent' method", function(){
                var inst = new Report(defaultSettings);

                expect(inst.updateComponent).toBeDefined();
                expect(_.isFunction(inst.updateComponent)).toBeTruthy();
            });

            it("should return deferred", function(){
                var inst = new Report(defaultSettings),
                    res = inst.updateComponent();

                expect(_.isFunction(res.done)).toBeTruthy();
                expect(_.isFunction(res.fail)).toBeTruthy();
                expect(_.isFunction(res.always)).toBeTruthy();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of component cannot be found", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents").returns([{ id: "bla" }]),
                    inst = new Report(defaultSettings);

                inst.updateComponent({ id: "foo" }, function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(error.exception.message).toBe("Component with such name or id 'foo' was not found");
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                getComponentsStub.restore();
                javaScriptExceptionSpy.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of exception in 'updateComponent' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents").throws(new Error("stub exception")),
                    inst = new Report(defaultSettings);

                inst.updateComponent({}, function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                getComponentsStub.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback in case of unknown 'componentType'", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents").returns([{ id: "foo" }]),
                    inst = new Report(defaultSettings);

                inst.updateComponent({ id: "foo", componentType: "baz" }, function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(error.exception.message).toBe("Cannot validate component - unknown component type '" + "baz" + "'");
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                getComponentsStub.restore();
                javaScriptExceptionSpy.restore();
            });

            it("should pass SchemaValidationBiComponentError instance to error callback when component validation fails", function() {
                var failure = sinon.spy(),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents").returns([{ id: "foo", componentType: "chart", chartType: "Column" }]),
                    validationErrorSpy = sinon.spy(biComponentErrorFactory, "validationError"),
                    inst = new Report(defaultSettings);

                inst.updateComponent({ id: "foo", chartType: "SomeNotExistingChartType" }, function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof SchemaValidationBiComponentError).toBe(true);
                expect(validationErrorSpy).toHaveBeenCalled();

                getComponentsStub.restore();
                validationErrorSpy.restore();
            });

            it("should call ReportComponentCollection 'updateComponents' method if component id is passed as first argument", function() {
                var successSpy = sinon.spy(),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents")
                        .returns([
                            {
                                id: "bla",
                                chartType: "StackedColumn",
                                componentType: "chart"
                            }
                        ]),
                    updateComponentsSpy = sinon.spy(ReportComponentCollection.prototype, "updateComponents"),
                    inst = new Report(defaultSettings);

                inst.updateComponent("bla", { chartType: "Bar" }, successSpy);

                expect(updateComponentsSpy).toHaveBeenCalledWith([
                    {
                        id: "bla",
                        chartType: "Bar",
                        componentType: "chart"
                    }
                ]);
                expect(successSpy).toHaveBeenCalledWith({
                    id: "bla",
                    chartType: "Bar",
                    componentType: "chart"
                });

                getComponentsStub.restore();
                updateComponentsSpy.restore();
            });

            it("should call ReportComponentCollection 'updateComponents' method if component properties are passed as first argument", function() {
                var successSpy = sinon.spy(),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents")
                        .returns([
                            {
                                id: "bla",
                                chartType: "StackedColumn",
                                componentType: "chart"
                            }
                        ]),
                    updateComponentsSpy = sinon.spy(ReportComponentCollection.prototype, "updateComponents"),
                    inst = new Report(defaultSettings);

                inst.updateComponent({
                    id: "bla",
                    chartType: "Bar"
                }, successSpy);

                expect(updateComponentsSpy).toHaveBeenCalledWith([
                    {
                        id: "bla",
                        chartType: "Bar",
                        componentType: "chart"
                    }
                ]);
                expect(successSpy).toHaveBeenCalledWith({
                    id: "bla",
                    chartType: "Bar",
                    componentType: "chart"
                });

                getComponentsStub.restore();
                updateComponentsSpy.restore();
            });

            it("should call ReportController 'runReportAction' if there are actions to perform and successfully resolve deferred", function() {
                var successSpy = sinon.spy(),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents")
                        .returns([
                            {
                                id: "bla",
                                chartType: "StackedColumn",
                                componentType: "chart"
                            }
                        ]),
                    updateComponentsStub = sinon.stub(ReportComponentCollection.prototype, "updateComponents")
                        .returns([
                            {
                                actionName: 'changeChartType',
                                changeChartTypeData: {
                                    chartComponentUuid: "bla",
                                    chartType: "Bar"
                                }
                            }
                        ]),
                    runActionStub = sinon.stub(ReportController.prototype, "runReportAction", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    inst = new Report(defaultSettings);

                inst.updateComponent({
                    id: "bla",
                    chartType: "Bar"
                }, successSpy);

                expect(runActionStub).toHaveBeenCalledWith([ {
                    actionName: 'changeChartType',
                    changeChartTypeData: {
                        chartComponentUuid: "bla",
                        chartType: "Bar"
                    }
                } ]);

                expect(successSpy).toHaveBeenCalledWith({
                    id: "bla",
                    chartType: "Bar",
                    componentType: "chart"
                });

                runActionStub.restore();
                getComponentsStub.restore();
                updateComponentsStub.restore();
            });

            it("should call ReportController 'runReportAction' if there are actions to perform and reject deferred if it fails", function() {
                var successSpy = sinon.spy(),
                    errorSpy = sinon.spy(),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents")
                        .returns([
                            {
                                id: "bla",
                                chartType: "StackedColumn",
                                componentType: "chart"
                            }
                        ]),
                    updateComponentsStub = sinon.stub(ReportComponentCollection.prototype, "updateComponents")
                        .returns([
                            {
                                actionName: 'changeChartType',
                                changeChartTypeData: {
                                    chartComponentUuid: "bla",
                                    chartType: "Bar"
                                }
                            }
                        ]),
                    runActionStub = sinon.stub(ReportController.prototype, "runReportAction", function() {
                        var dfd = new $.Deferred();
                        dfd.reject({
                            source: "execution",
                            status: "failed",
                            errorDescriptor: { message: "error" }
                        });
                        return dfd;
                    }),
                    reportStatusSpy = sinon.spy(biComponentErrorFactory, "reportStatus"),
                    inst = new Report(defaultSettings);

                inst.updateComponent({
                    id: "bla",
                    chartType: "Bar"
                }, successSpy, errorSpy);

                expect(runActionStub).toHaveBeenCalledWith([ {
                    actionName: 'changeChartType',
                    changeChartTypeData: {
                        chartComponentUuid: "bla",
                        chartType: "Bar"
                    }
                } ]);

                expect(reportStatusSpy).toHaveBeenCalledWith({
                    source: "execution",
                    status: "failed",
                    errorDescriptor: { message: "error" }
                });

                expect(errorSpy).toHaveBeenCalled();
                expect(errorSpy.getCall(0).args[0] instanceof ReportStatusError).toBe(true);

                runActionStub.restore();
                reportStatusSpy.restore();
                getComponentsStub.restore();
                updateComponentsStub.restore();
            });
        });

        describe("method 'events'", function() {
            var defaultSettings = {
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/resource",
                params: {
                    "Country_multi_select": ["Mexico"],
                    "Cascading_state_multi_select": ["Guerrero", "Sinaloa"]
                },
                pages: "2-4"
            };

            it ("should have the method", function(){
                var report = new Report();

                expect(report.events).toBeDefined();
                expect(_.isFunction(report.events)).toBeTruthy();
            });

            it("should return report instance to allow chaining", function() {
                var report = new Report();

                expect(report.events({ changeTotalPages: function() {} })).toBe(report);
            });

            it("should call assigned event handler for 'changeTotalPages' event", function() {
                var model,
                    controller,
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        model = this.model;
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings),
                    changeTotalPagesSpy = sinon.spy();

                report.events({ changeTotalPages: changeTotalPagesSpy });
                report.run();

                controller.stopListening(model, "change:totalPages");
                model.set("totalPages", 10);

                expect(changeTotalPagesSpy).toHaveBeenCalledWith(10);

                fetchStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
            });

            it("should overwrite event handler and clean memory", function() {
                var model,
                    controller,
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        model = this.model;
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings),
                    changeTotalPagesSpy1 = sinon.spy(),
                    changeTotalPagesSpy2 = sinon.spy();

                report.events({ changeTotalPages: changeTotalPagesSpy1 });
                report.run();
                report.events({ changeTotalPages: changeTotalPagesSpy2 });

                controller.stopListening(model, "change:totalPages");
                model.set("totalPages", 10);

                expect(changeTotalPagesSpy2).toHaveBeenCalledWith(10);
                expect(changeTotalPagesSpy1).not.toHaveBeenCalled();

                fetchStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
            });

            it("should call assigned event handlers for 'canUndo' and 'canRedo' events", function() {
                var controller,
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings),
                    canUndoSpy = sinon.spy(),
                    canRedoSpy = sinon.spy();

                report.events({
                    canUndo: canUndoSpy,
                    canRedo: canRedoSpy
                });

                report.run();

                controller.stateStack.set({
                    "canUndo": true,
                    "canRedo": true
                });

                expect(canUndoSpy).toHaveBeenCalledWith(true);
                expect(canRedoSpy).toHaveBeenCalledWith(true);

                fetchStub.restore();
            });

            it("should call assigned event handlers for 'reportCompleted' event", function() {
                var controller,
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report(defaultSettings),
                    reportCompletedSpy = sinon.spy();

                report.events({ reportCompleted: reportCompletedSpy });

                report.run();

                controller.trigger(reportEvents.REPORT_COMPLETED, reportStatuses.FAILED, {
                    source: "export",
                    format: "html",
                    status: "failed",
                    errorDescriptor: { message: "error" }
                });

                expect(reportCompletedSpy).toHaveBeenCalled();
                expect(reportCompletedSpy.getCall(0).args[0]).toBe(reportStatuses.FAILED);
                expect(reportCompletedSpy.getCall(0).args[1] instanceof ReportStatusError).toBe(true);

                fetchStub.restore();
            });
        });

        describe("properties validation", function(){
            it("should require 'server' property", function(){
                var inst = new Report({ resource: "/public/report", server: "http://localhost:8080/jasperserver-pro" });

                expect(inst.validate()).toBeFalsy();

                inst.properties({ resource: "/public/report", server: undefined });

                expect(inst.validate()).toBeTruthy();
            });

            it("should require 'resource' property", function(){
                var inst = new Report({ resource: "/public/report", server: "http://localhost:8080/jasperserver-pro" });

                expect(inst.validate()).toBeFalsy();

                inst.properties({ server: "http://localhost:8080/jasperserver-pro", resource: undefined });

                expect(inst.validate()).toBeTruthy();
            });

            it("should correctly validate 'params' property", function() {
                var inst = new Report({
                    resource: "/public/report",
                    server: "http://localhost:8080/jasperserver-pro",
                    params: {}
                });

                expect(inst.validate()).toBeFalsy();

                inst.params({
                    "Country_multi_select":["Mexico"],
                    "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                });

                expect(inst.validate()).toBeFalsy();

                inst.params({
                    "Country_multi_select": []
                });

                expect(inst.validate()).toBeFalsy();

                inst.params({
                    "Country_multi_select": "Mexico"
                });

                expect(inst.validate()).toBeTruthy();
            });

            it("should correctly validate 'pages' property", function() {
                var inst = new Report({
                    resource: "/public/report",
                    server: "http://localhost:8080/jasperserver-pro",
                    pages: 1
                });

                expect(inst.validate()).toBeFalsy();

                inst.pages("1");

                expect(inst.validate()).toBeFalsy();

                inst.pages("2-5");

                expect(inst.validate()).toBeFalsy();

                inst.pages("[2-5]");

                expect(inst.validate()).toBeTruthy();

                inst.pages([1, 5]);

                expect(inst.validate()).toBeTruthy();
            });
        });

        describe("auto update of data()", function() {
            it("should automatically update 'totalPages' in data() when it changes in model", function() {
                var model,
                    controller,
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        model = this.model;
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    report = new Report({
                        server: "http://localhost:8080/jasperserver-pro",
                        resource: "/public/resource"
                    });

                report.run();

                controller.stopListening(model, "change:totalPages");
                model.set("totalPages", 10);

                expect(report.data().totalPages).toBe(10);

                fetchStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
            });

            it("should automatically update 'components' in data() when they change", function() {
                var model,
                    controller,
                    components = [
                        {
                            id: "bla",
                            chartType: "StackedColumn",
                            componentType: "chart"
                        }
                    ],
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        model = this.model;
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getComponents").returns(components),
                    report = new Report({
                        server: "http://localhost:8080/jasperserver-pro",
                        resource: "/public/resource"
                    });

                report.run();

                controller.components.off("add remove reset change");
                controller.components.trigger("reset");

                expect(report.data().components).toEqual(components);

                fetchStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
                getComponentsStub.restore();
            });

            it("should automatically update 'links' in data() when they change", function() {
                var model,
                    controller,
                    links = [
                        {
                            id: "bla",
                            chartType: "StackedColumn",
                            componentType: "chart"
                        }
                    ],
                    fetchStub = sinon.stub(ReportController.prototype, "executeReport", function() {
                        model = this.model;
                        controller = this;
                        return (new $.Deferred()).resolve();
                    }),
                    fetchHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchReportHtmlExportAndJiveComponents", function() {
                        return (new $.Deferred()).resolve();
                    }),
                    getComponentsStub = sinon.stub(ReportComponentCollection.prototype, "getLinks").returns(links),
                    report = new Report({
                        server: "http://localhost:8080/jasperserver-pro",
                        resource: "/public/resource"
                    });

                report.run();

                controller.components.off("add remove reset change");
                controller.components.trigger("reset");

                expect(report.data().links).toEqual(links);

                fetchStub.restore();
                fetchHtmlExportAndJiveComponentsStub.restore();
                getComponentsStub.restore();
            });
        });
    });
});
