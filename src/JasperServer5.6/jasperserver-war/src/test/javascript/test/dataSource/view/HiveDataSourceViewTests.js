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
 * @version: $Id: HiveDataSourceViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		jrsConfigs = require('jrs.configs'),
		sinon = require("sinon"),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		HiveDataSourceView = require("dataSource/view/HiveDataSourceView"),
		BaseDataSourceView = require("dataSource/view/BaseDataSourceView"),
		CustomDataSourceView = require("dataSource/view/CustomDataSourceView");

	describe("Testing HiveDataSourceView", function() {

		beforeEach(function() {
			jrsConfigs.addDataSource = dataSourceConfig;
		});

		afterEach(function(){
			delete jrsConfigs.addDataSource;
		});

		it("HiveDataSourceView should be defined", function() {
			expect(HiveDataSourceView).toBeDefined();
			expect(HiveDataSourceView).toBeFunction();
		});

		it("HiveDataSourceView should have render method", function() {
			expect(HiveDataSourceView.prototype.render).toBeDefined();
			expect(HiveDataSourceView.prototype.render).toBeFunction();
		});

		it("HiveDataSourceView: check inheritance from CustomDataSourceView", function() {

			var hiveDataSourceView, stub = {};

			// stubbing...
			stub.customInit = sinon.stub(CustomDataSourceView.prototype, "initialize");

			hiveDataSourceView = new HiveDataSourceView(
				_.extend(jrsConfigs.addDataSource.initOptions, {
					dataSourceType: undefined,
					dataSource: undefined,
					el: $("[name=dataSourceTestArea]")
				})
			);

			// testing...
			expect(stub.customInit).toHaveBeenCalled();

			// removing...
			hiveDataSourceView.remove();

			// cleaning...
			CustomDataSourceView.prototype.initialize.restore();
		});
	});
});