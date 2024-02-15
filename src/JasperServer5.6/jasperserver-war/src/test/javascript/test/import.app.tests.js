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
 * @version: $Id: import.app.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "import.app"],
    function ($, _, App){

    var components = jaspersoft.components, Import = JRS.Import;

    describe("ImportApp", function(){

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
             })
        });

        describe("Advanced initialization", function(){

            var uiConstructorStub, containerOptions, uiRenderSub, formModelConstructorStub;

            beforeEach(function () {
                var formModel = new Backbone.Model();
                sinon.stub(formModel, "get").returns(new components.State());
                formModelConstructorStub = sinon.stub(Import, "FormModel").returns(formModel);
                var uiView = new Backbone.View();
                uiRenderSub = sinon.stub(uiView, "render");
                uiConstructorStub = sinon.stub(components, "Layout").returns(uiView);
                app = makeApp();
                containerOptions = {type: "TestView", container: "#testSelector"};
                app.initialize(containerOptions);

            });

            afterEach(function(){
                uiConstructorStub.restore();
                uiRenderSub.restore();
                formModelConstructorStub.restore();
            });

            it("should bind layout with formmodel and container's options",function(){
                expect(uiConstructorStub).toHaveBeenCalledWith({
                    model: app.formModel,
                    type:"TestView",
                    container:"#testSelector"
                });
            });

            it("should render after document ready",function(){
                expect(uiRenderSub).toHaveBeenCalledWith(containerOptions);
            });

        });

    });

});