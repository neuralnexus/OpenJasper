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
 * @version: $Id: MongoDataSourceViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		i18n = require("bundle!all"),
		jrsConfigs = require('jrs.configs'),
		sinon = require("sinon"),
		resourceLocator = require("resource.locate"),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		MongoDataSourceView = require("dataSource/view/MongoDbDataSourceView"),
		dataSourceTestingHelper = require("test/dataSource/dataSourceTestingHelper.js"),
		jasperserverConfig = require("bundle!jasperserver_config");


	describe("Testing MongoDataSourceView", function() {

		var mongoDataSourceView, fakeServer, root;

		beforeEach(function() {
			jrsConfigs.addDataSource = dataSourceConfig;
			sinon.stub(resourceLocator, "initialize");
		});

		afterEach(function(){
			delete jrsConfigs.addDataSource;
			resourceLocator.initialize.restore();
		});

		it("MongoDataSourceView should be defined", function() {
			expect(MongoDataSourceView).toBeDefined();
			expect(MongoDataSourceView).toBeFunction();
		});

		describe("MongoDataSourceView's work", function() {

			beforeEach(function(){

				dataSourceTestingHelper.beforeEach();

				// create an variable which holds the DS in the DOM object
				root = $("[name=dataSourceTestArea]");

				// prepare fake server
				fakeServer = sinon.fakeServer.create();

				// prepare the response
				fakeServer.respondWith(
					"GET", jrsConfigs.contextPath + "/rest_v2/customDataSources/MongoDataSource",
					[200, { "Content-Type": "application/json" }, JSON.stringify(
						{
							"name":"MongoDbDataSource",
							"queryTypes":["MongoDbQuery"],
							"propertyDefinitions":[
								{"name":"mongoURI","label":"MongoDbDataSource.properties.mongoURI","defaultValue":"mongodb://hostname:27017/database"},
								{"name":"username","label":"MongoDbDataSource.properties.username","defaultValue":"username"},
								{"name":"password","label":"MongoDbDataSource.properties.password","defaultValue": jasperserverConfig["input.password.substitution"]},
								{"name":"schema","label":"MongoDbDataSource.properties.password","defaultValue": ""}
							],
							"testable":true
						}
					)]);

				// init the data source
				mongoDataSourceView = new MongoDataSourceView(
					_.extend(jrsConfigs.addDataSource.initOptions, {
						dataSourceType: "MongoDataSource",
						dataSource: undefined,
						el: root
					})
				);

				// respond to data source
				fakeServer.respond();

				mongoDataSourceView.render();
			});

			afterEach(function(){

				// remove the data source from the page
				mongoDataSourceView.remove();

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
				expect(root.find("[name=mongoURI]")).toBeVisible();
				expect(root.find("[name=username]")).toBeVisible();
				expect(root.find("[name=password]")).toBeVisible();
				expect(root.find("[name=schema]")).toBeVisible();

				// check save path
				expect(root.find("[name=parentFolderUri]")).toBeVisible();

				// check test connection button
				expect(root.find("#testDataSource").length).toBe(1);
			});

			it("Check if everything has proper default value", function() {

				// check page title
				expect(document.title).toBe(i18n["jsp.home.content_title"] + ": " + i18n["resource.datasource.mongo.page.title.new"]);

				// check save section
				expect(root.find("[name=label]").val()).toBe("");
				expect(root.find("[name=name]").val()).toBe("");
				expect(root.find("[name=description]").val()).toBe("");

				// check data source specific fields
				expect(root.find("[name=mongoURI]").val()).toBe("mongodb://hostname:27017/database");
				expect(root.find("[name=username]").val()).toBe("username");
				expect(root.find("[name=password]").val()).toBe(jasperserverConfig["input.password.substitution"]);
				expect(root.find("[name=schema]").val()).toBe("");

				// check save path
				expect(root.find("[name=parentFolderUri]").val()).toBe("");
			});

			it("Checking field validation", function() {

				// we need to test specific fields -- we'll remove the value and we'll see if the
				// validation will trigger
				expect(root.find("[name=mongoURI]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
			});
		});
	});
});

