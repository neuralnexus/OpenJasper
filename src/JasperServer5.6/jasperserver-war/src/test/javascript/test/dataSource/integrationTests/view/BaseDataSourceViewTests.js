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
 * @author: Dima Gorbenko <dgorbenko@jaspersoft.com>
 * @version: $Id: BaseDataSourceViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		i18n = require("bundle!all"),
		sinon = require("sinon"),
		jrsConfigs = require('jrs.configs'),
		resourceLocator = require("resource.locate"),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		BaseDataSourceView = require("dataSource/view/BaseDataSourceView"),
		dataSourceTestingHelper = require("test/dataSource/dataSourceTestingHelper.js");


	describe("Testing BaseDataSourceView", function() {

		var baseDataSourceView, fakeServer, root;

		beforeEach(function() {
			jrsConfigs.addDataSource = dataSourceConfig;
			sinon.stub(resourceLocator, "initialize");
		});

		afterEach(function(){
			delete jrsConfigs.addDataSource;
			resourceLocator.initialize.restore();
		});

		it("BaseDataSourceView should be defined", function() {
			expect(BaseDataSourceView).toBeDefined();
			expect(BaseDataSourceView).toBeFunction();
		});

		describe("BaseDataSourceView's work", function() {

			beforeEach(function(){

				dataSourceTestingHelper.beforeEach();

				// prepare fake server
				fakeServer = sinon.fakeServer.create();

				// create an variable which holds the DS in the DOM object
				root = $("[name=dataSourceTestArea]");

				// init the data source
				baseDataSourceView = new BaseDataSourceView(
					_.extend(jrsConfigs.addDataSource.initOptions, {
						dataSourceType: undefined,
						dataSource: undefined,
						el: root
					})
				);

				baseDataSourceView.render();
				baseDataSourceView.renderNameAndDescriptionSection();
				baseDataSourceView.renderTimezoneSection();
				baseDataSourceView.renderSaveLocationSection();
				baseDataSourceView.renderTestConnectionSection();
			});

			afterEach(function(){

				// remove the data source from the page
				baseDataSourceView.remove();

				// clear the testable area
				root.empty();

				// destroy fake XHR service
				fakeServer.restore();

				dataSourceTestingHelper.afterEach();
			})

			it("Check if all fields are visible", function() {

				// check save section
				expect(root.find("[name=label]")).toBeVisible();
				expect(root.find("[name=name]")).toBeVisible();
				expect(root.find("[name=description]")).toBeVisible();

				// check timezone
				expect(root.find("[name=timezone]")).toBeVisible();

				// check save path
				expect(root.find("[name=parentFolderUri]")).toBeVisible();

				// check test connection button
				expect(root.find("#testDataSource").length).toBe(1);
			});

			it("Check if everything has proper default value", function() {

				// check page title
				expect(document.title).toBe(i18n["jsp.home.content_title"] + ": undefined");

				// check save section
				expect(root.find("[name=label]").val()).toBe("");
				expect(root.find("[name=name]").val()).toBe("");
				expect(root.find("[name=description]").val()).toBe("");

				// check timezone
				expect(root.find("[name=timezone]").val()).toBe("");

				// check save path
				expect(root.find("[name=parentFolderUri]").val()).toBe("");
			});

			it("Checking if backbone model listens for the changes in the view and gets field's values", function() {

				// set the label into some value
				root.find("[name=label]").val("some value").trigger("change");

				// now, expect what dependent field "id" will have the auto-generated value
				// thus, we'll check the model's works and the logic for generating the ID

				expect(root.find("[name=name]").val()).toBe("some_value");
			});

			it("Checking basic field validation of any data source", function() {

				// at first, check what fields are not marked with error
				expect(root.find("[name=label]").parent().hasClass("error")).toBeFalsy();
				expect(root.find("[name=name]").parent().hasClass("error")).toBeFalsy();
				expect(root.find("[name=parentFolderUri]").parent().hasClass("error")).toBeFalsy();

				// now, check if fields have the validation after they have been changed
				expect(root.find("[name=label]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
				expect(root.find("[name=name]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
				expect(root.find("[name=parentFolderUri]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();

				//---------------------------------
				// Now, check what native validation on key-pressing and change works

				// set the parentFolderUri to "abc" value and check if the validation will happen.
				// "abc" if a wrong path because it should start with "/" symbol
				root.find("[name=parentFolderUri]").val("abc").trigger("change");
				// now, check the error
				expect(root.find("[name=parentFolderUri]").parent().hasClass("error")).toBeTruthy();


				// set the parentFolderUri to "/abc" value and check if the validation will happen -- it shouldn't
				root.find("[name=parentFolderUri]").val("/abc").trigger("change");
				// now, check the error
				expect(root.find("[name=parentFolderUri]").parent().hasClass("error")).toBeFalsy();

				// set the parentFolderUri back to "abc" value and check if the validation will happen
				// with the keyup event
				root.find("[name=parentFolderUri]").val("abc").trigger("keyup");
				// now, check the error
				expect(root.find("[name=parentFolderUri]").parent().hasClass("error")).toBeTruthy();
			});
		});
	});
});

