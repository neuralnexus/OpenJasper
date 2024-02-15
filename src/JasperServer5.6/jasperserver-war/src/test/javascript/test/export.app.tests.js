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
 * @author: inesterenko
 * @version: $Id: export.app.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "backbone","export.app"],
    function ($, _, Backbone,  App){

    var components = jaspersoft.components, Export = JRS.Export;

    describe("ExportApp", function(){

        var app, makeApp = function(){
            return _.clone(App);
        };

        describe("Base Initialization", function(){
              beforeEach(function(){
                  app = makeApp();
              });

             it("should create all components expect formview", function(){
                 app.initialize();
                 expect(app.formModel).toBeDefined();
                 expect(app.ui).toBeDefined();
                 expect(app.stateController).toBeDefined();
             })
        });

        describe("Advanced initialization", function(){

            var stateControllerConstructorStub, uiConstructorStub, containerOptions, uiRenderSub, formModelConstructorStub,stateModel;

            beforeEach(function () {
                var formModel = new Backbone.Model();
                stateModel = new Backbone.Model();
                sinon.stub(formModel, "get").returns(stateModel);
                formModelConstructorStub = sinon.stub(Export, "FormModel").returns(formModel);
                var uiView = new Backbone.View();
                uiRenderSub = sinon.stub(uiView, "render");
                uiConstructorStub = sinon.stub(components, "Layout").returns(uiView);
                stateControllerConstructorStub = sinon.stub(Export, "StateController");
                app = makeApp();
                containerOptions = {type: "TestView", container: "#testSelector"};
                app.initialize(containerOptions);

            });

            afterEach(function(){
                stateControllerConstructorStub.restore();
                uiConstructorStub.restore();
                uiRenderSub.restore();
                formModelConstructorStub.restore();
            });

            it("should bind layout with formmodel and container's options",function(){
                expect(uiConstructorStub).toHaveBeenCalledWith({
                    model: app.formModel,
                    type:"TestView",
                    container:"#testSelector",
                    namespace: Export
                });
            });

            it("should bind statecontroller with state model",function(){
                expect(stateControllerConstructorStub).toHaveBeenCalledWith({
                        model: stateModel,
                        timeout: 1200000,
                        delay: 3000
                });
            });

            it("should render after document ready",function(){
                expect(uiRenderSub).toHaveBeenCalledWith(containerOptions);
            });

        });

        describe("Base functions", function(){

            var formSetStub, uiShowStub;

            beforeEach(function(){
                app = makeApp();
                app.initialize();
                formSetStub = sinon.stub(app.formModel, "set");
                uiShowStub = sinon.stub(app.ui, "showDialog");
            });

            afterEach(function(){
                formSetStub.restore();
                uiShowStub.restore();
            });

            it("can show dialog to specific resources", function(){
                var mockDataForDialog = {};
                var mockUris = ["a","b","b"];
                var mockReports = "asf";
                var parseDataStub = sinon.stub(app, "parseRepoData").returns(mockUris);
                var hasReportsStub = sinon.stub(app, "hasReports").returns(mockReports);
                app.showDialogFor(mockDataForDialog);
                expect(parseDataStub).toHaveBeenCalledWith(mockDataForDialog);
                expect(formSetStub).toHaveBeenCalledWith({uris: mockUris, includeReportJobs:mockReports});
                expect(uiShowStub).toHaveBeenCalled();
                parseDataStub.restore();
            });

            describe("RepoData parsing", function(){

                it("can parse repodata as single object", function(){
                    var results = app.parseRepoData({URIString: "test", key1:"val1", key2:"val2"});
                    expect(results[0]).toEqual("test");
                });

                it("can parse repodata as array of objects", function(){
                    var results = app.parseRepoData([
                        {URIString: "test1", key1:"val1", key2:"val2"},
                        {URIString: "test2", key1:"val1", key2:"val2"},
                        {URIString: "test3", key1:"val1", key2:"val2"}
                    ]);
                    expect(results).toEqual(["test1", "test2", "test3"]);
                });

            });

        });

    });

});