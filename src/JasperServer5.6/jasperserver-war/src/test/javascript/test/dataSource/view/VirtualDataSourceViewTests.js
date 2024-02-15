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
 * @version: $Id: VirtualDataSourceViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		i18n = require("bundle!all"),
		jrsConfigs = require('jrs.configs'),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		VirtualDataSourceView = require("dataSource/view/VirtualDataSourceView"),
		dataSourceTestingHelper = require("test/dataSource/dataSourceTestingHelper.js");


	describe("Testing VirtualDataSourceView", function() {

		beforeEach(function() {
			jrsConfigs.addDataSource = dataSourceConfig;
		});

		afterEach(function(){
			delete jrsConfigs.addDataSource;
		});

		it("VirtualDataSourceView should be defined", function() {
			expect(VirtualDataSourceView).toBeDefined();
			expect(VirtualDataSourceView).toBeFunction();
		});

		describe("VirtualDataSourceView's work", function() {

			var virtualDataSourceView, fakeServer, root, clock;

			beforeEach(function(){

				dataSourceTestingHelper.beforeEach();

				// create an variable which holds the DS in the DOM object
				root = $("[name=dataSourceTestArea]");

				// prepare fake server
				fakeServer = sinon.fakeServer.create();

				// init the data source
				virtualDataSourceView = new VirtualDataSourceView(
					_.extend(jrsConfigs.addDataSource.initOptions, {
                        dataSourceType: "virtualdatasource",
						dataSource: undefined,
						el: root
					})
				);

				virtualDataSourceView.render();
			});

			afterEach(function(){

				// remove the data source from the page
				virtualDataSourceView.remove();

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
				expect(root.find("#subDataSourcesTree")).toBeVisible();
				expect(root.find("#moveButtons")).toBeVisible();
				expect(root.find("#moveButtons").children().length).toBe(3);
				expect(root.find("#selectedDataSourcesHeader")).toBeVisible();
				expect(root.find("#selectedSubDataSourcesListContainer")).toBeVisible();

				// check save path
				expect(root.find("[name=parentFolderUri]")).toBeVisible();
			});

			it("Check if everything has proper default value", function() {

				// check page title
				expect(document.title).toBe(i18n["jsp.home.content_title"] + ": " + i18n["resource.datasource.virtual.page.title.new"]);

				// check save section
				expect(root.find("[name=label]").val()).toBe("");
				expect(root.find("[name=name]").val()).toBe("");
				expect(root.find("[name=description]").val()).toBe("");

				// check save path
				expect(root.find("[name=parentFolderUri]").val()).toBe("");
			});
		});
	});
});

