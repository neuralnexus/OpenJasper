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
 * @version: $Id: CustomDataSourceViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		sinon = require("sinon"),
		resourceLocator = require("resource.locate"),
		i18n = require("bundle!all"),
		jrsConfigs = require('jrs.configs'),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		CustomDataSourceView = require("dataSource/view/CustomDataSourceView"),
		dataSourceTestingHelper = require("test/dataSource/dataSourceTestingHelper.js");


	describe("Testing CustomDataSourceView", function() {

		var customDataSourceView, fakeServer, root;

		beforeEach(function() {
			jrsConfigs.addDataSource = dataSourceConfig;
			sinon.stub(resourceLocator, "initialize");
		});

		afterEach(function(){
			delete jrsConfigs.addDataSource;
			resourceLocator.initialize.restore();
		});

		it("CustomDataSourceView should be defined", function() {
			expect(CustomDataSourceView).toBeDefined();
			expect(CustomDataSourceView).toBeFunction();
		});

		describe("CustomDataSourceView's work", function() {

			beforeEach(function(){

				dataSourceTestingHelper.beforeEach();

				// create an variable which holds the DS in the DOM object
				root = $("[name=dataSourceTestArea]");

				// prepare fake server
				fakeServer = sinon.fakeServer.create();

				// prepare the response
				fakeServer.respondWith(
					"GET", jrsConfigs.contextPath + "/rest_v2/customDataSources/CustomDataSource",
					[200, { "Content-Type": "application/json" }, JSON.stringify(
						{
							"name":"CustomDbDataSource",
							"queryTypes":["CustomDbQuery"],
							"propertyDefinitions":[
								{"name":"customURI","label":"CustomDbDataSource.properties.customURI","defaultValue":"customdb://hostname:27017/database"},
								{"name":"username","label":"CustomDbDataSource.properties.username","defaultValue":"username", "properties": [{"key": "mandatory", "value": true}]},
								{"name":"password","label":"CustomDbDataSource.properties.password","defaultValue":"password"}
							],
							"testable":true
						}
					)]);

				// init the data source
				customDataSourceView = new CustomDataSourceView(
					_.extend(jrsConfigs.addDataSource.initOptions, {
						dataSourceType: "CustomDataSource",
						dataSource: undefined,
						el: root
					})
				);

				// respond to data source
				fakeServer.respond();

				customDataSourceView.render();
			});

			afterEach(function(){

				// remove the data source from the page
				customDataSourceView.remove();

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

				// check data source specific fields
				expect(root.find("[name=customURI]")).toBeVisible();
				expect(root.find("[name=username]")).toBeVisible();
				expect(root.find("[name=password]")).toBeVisible();

				// check save path
				expect(root.find("[name=parentFolderUri]")).toBeVisible();

				// check test connection button
				expect(root.find("#testDataSource").length).toBe(1);
			});

			it("Check if everything has proper default value", function() {

				// check page title
				expect(document.title).toBe(i18n["jsp.home.content_title"] + ": " + i18n["resource.datasource.custom.page.title.new"]);

				// check save section
				expect(root.find("[name=label]").val()).toBe("");
				expect(root.find("[name=name]").val()).toBe("");
				expect(root.find("[name=description]").val()).toBe("");

				// check data source specific fields
				expect(root.find("[name=customURI]").val()).toBe("customdb://hostname:27017/database");
				expect(root.find("[name=username]").val()).toBe("username");
				expect(root.find("[name=password]").val()).toBe(i18n['input.password.substitution']);

				// check save path
				expect(root.find("[name=parentFolderUri]").val()).toBe("");
			});

			it("Checking field validation", function() {

				// we need to test specific fields -- we'll remove the value and we'll see if the
				// validation will trigger
				expect(root.find("[name=username]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
			});
		});
	});
});

