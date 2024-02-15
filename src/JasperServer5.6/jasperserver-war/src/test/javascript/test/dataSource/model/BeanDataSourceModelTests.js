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
 * @version: $Id: BeanDataSourceModelTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		sinon = require("sinon"),
		BaseDataSourceModel = require("dataSource/model/BaseDataSourceModel"),
		BeanDataSourceModel = require("dataSource/model/BeanDataSourceModel");

	describe("Testing BaseDataSourceModel", function() {

		var beanDataSourceModel, stub = {};

		it("BeanDataSourceModel should be defined", function() {
			expect(BeanDataSourceModel).toBeDefined();
			expect(BeanDataSourceModel).toBeFunction();
		});

		it("BeanDataSourceModel initialize method should call its parent", function() {

			stub = sinon.stub(BaseDataSourceModel.prototype, "initialize");

			beanDataSourceModel = new BeanDataSourceModel();

			expect(stub).toHaveBeenCalled();

			BaseDataSourceModel.prototype.initialize.restore();
		});

		it("BeanDataSourceModel has all necessary model attributes", function() {

			beanDataSourceModel = new BeanDataSourceModel({
				parentFolderUri: "/aaa/bbb/ccc"
			});

			expect(!_.isUndefined(beanDataSourceModel.attributes.beanName)).toBeTruthy();
			expect(!_.isUndefined(beanDataSourceModel.attributes.beanMethod)).toBeTruthy();
		});
	});
});

