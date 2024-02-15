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
		jrsConfigs = require('jrs.configs'),
		sinon = require("sinon"),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		CustomDataSourceView = require("dataSource/view/CustomDataSourceView"),
		BaseDataSourceView = require("dataSource/view/BaseDataSourceView");

	describe("Testing CustomDataSourceView", function() {

		var customDataSourceView, stub = {};

		beforeEach(function() {

			jrsConfigs.addDataSource = dataSourceConfig;

			stub.customInit = sinon.stub(BaseDataSourceView.prototype, "initialize");
			stub.templateData = sinon.stub(BaseDataSourceView.prototype, "templateData");

			customDataSourceView = new CustomDataSourceView(
				_.extend(jrsConfigs.addDataSource.initOptions, {
					dataSourceType: undefined,
					dataSource: undefined,
					el: $("[name=dataSourceTestArea]")
				})
			);
		});

		afterEach(function(){

			customDataSourceView.remove();

			BaseDataSourceView.prototype.initialize.restore();
			BaseDataSourceView.prototype.templateData.restore();
			delete jrsConfigs.addDataSource;
		});

		it("CustomDataSourceView should be defined", function() {
			expect(CustomDataSourceView).toBeDefined();
			expect(CustomDataSourceView).toBeFunction();
		});

		it("CustomDataSourceView should have render method", function() {
			expect(CustomDataSourceView.prototype.render).toBeDefined();
			expect(CustomDataSourceView.prototype.render).toBeFunction();
		});

		it("CustomDataSourceView: check inheritance from BaseDataSourceView", function() {

			expect(stub.customInit).toHaveBeenCalled();
		});

		it("CustomDataSourceView: testing render method", function() {

			// stubbing...
			stub.renderNameAndDescriptionSection = sinon.stub(BaseDataSourceView.prototype, "renderNameAndDescriptionSection");
			stub.renderCustomFieldsSection = sinon.stub(CustomDataSourceView.prototype, "renderCustomFieldsSection");
			stub.renderSaveLocationSection = sinon.stub(BaseDataSourceView.prototype, "renderSaveLocationSection");
			stub.renderTestConnectionSection = sinon.stub(BaseDataSourceView.prototype, "renderTestConnectionSection");

			customDataSourceView.model = {testable: true};
			customDataSourceView.render();

			// testing...
			expect(stub.customInit).toHaveBeenCalled();
			expect(stub.renderNameAndDescriptionSection).toHaveBeenCalled();
			expect(stub.renderCustomFieldsSection).toHaveBeenCalled();
			expect(stub.renderSaveLocationSection).toHaveBeenCalled();
			expect(stub.renderTestConnectionSection).toHaveBeenCalled();

			// cleaning...
			BaseDataSourceView.prototype.renderNameAndDescriptionSection.restore();
			CustomDataSourceView.prototype.renderCustomFieldsSection.restore();
			BaseDataSourceView.prototype.renderSaveLocationSection.restore();
			BaseDataSourceView.prototype.renderTestConnectionSection.restore();
		});

		it("CustomDataSourceView: check inheritance of templateData() from BaseDataSourceView", function() {

			customDataSourceView.model = {customFields: {}};
			customDataSourceView.templateData();

			expect(stub.templateData).toHaveBeenCalled();
		});

		it("CustomDataSourceView: renderJndiSpecificSection calls append method on $el", function() {

			sinon.stub(_, "template", function(){
				return "";
			});
			stub.append = sinon.stub(customDataSourceView.$el, "append");

			customDataSourceView.model = {customFields: {}};
			customDataSourceView.renderCustomFieldsSection();

			expect(stub.append).toHaveBeenCalled();
			expect(_.isString(stub.append.getCall(0).args[0])).toBeTruthy();

			customDataSourceView.$el.append.restore();
			_.template.restore();
		});
	});
});