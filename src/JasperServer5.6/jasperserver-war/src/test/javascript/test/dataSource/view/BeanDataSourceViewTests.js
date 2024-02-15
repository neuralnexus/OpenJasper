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
 * @version: $Id: BeanDataSourceViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		jrsConfigs = require('jrs.configs'),
		sinon = require("sinon"),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		BeanDataSourceView = require("dataSource/view/BeanDataSourceView"),
		BaseDataSourceView = require("dataSource/view/BaseDataSourceView");

	describe("Testing BeanDataSourceView", function() {

		var beanDataSourceView, stub = {};

		beforeEach(function() {

			jrsConfigs.addDataSource = dataSourceConfig;

			stub.customInit = sinon.stub(BaseDataSourceView.prototype, "initialize");

			beanDataSourceView = new BeanDataSourceView(
				_.extend(jrsConfigs.addDataSource.initOptions, {
					dataSourceType: undefined,
					dataSource: undefined,
					el: $("[name=dataSourceTestArea]")
				})
			);
		});

		afterEach(function(){

			beanDataSourceView.remove();

			BaseDataSourceView.prototype.initialize.restore();
			delete jrsConfigs.addDataSource;
		});

		it("BeanDataSourceView should be defined", function() {
			expect(BeanDataSourceView).toBeDefined();
			expect(BeanDataSourceView).toBeFunction();
		});

		it("BeanDataSourceView should have render method", function() {
			expect(BeanDataSourceView.prototype.render).toBeDefined();
			expect(BeanDataSourceView.prototype.render).toBeFunction();
		});

		it("BeanDataSourceView: check inheritance from BaseDataSourceView", function() {

			expect(stub.customInit).toHaveBeenCalled();
		});


		it("BeanDataSourceView: testing render method", function() {

			// stubbing...
			stub.renderNameAndDescriptionSection = sinon.stub(BaseDataSourceView.prototype, "renderNameAndDescriptionSection");
			stub.renderBeanSpecificSection = sinon.stub(BeanDataSourceView.prototype, "renderBeanSpecificSection");
			stub.renderSaveLocationSection = sinon.stub(BaseDataSourceView.prototype, "renderSaveLocationSection");
			stub.renderTestConnectionSection = sinon.stub(BaseDataSourceView.prototype, "renderTestConnectionSection");

			beanDataSourceView.render();

			// testing...
			expect(stub.customInit).toHaveBeenCalled();
			expect(stub.renderNameAndDescriptionSection).toHaveBeenCalled();
			expect(stub.renderBeanSpecificSection).toHaveBeenCalled();
			expect(stub.renderSaveLocationSection).toHaveBeenCalled();
			expect(stub.renderTestConnectionSection).toHaveBeenCalled();

			// cleaning...
			BaseDataSourceView.prototype.renderNameAndDescriptionSection.restore();
			BeanDataSourceView.prototype.renderBeanSpecificSection.restore();
			BaseDataSourceView.prototype.renderSaveLocationSection.restore();
			BaseDataSourceView.prototype.renderTestConnectionSection.restore();
		});

		it("BeanDataSourceView: renderBeanSpecificSection calls append method on $el", function() {

			sinon.stub(BaseDataSourceView.prototype, "templateData");
			sinon.stub(_, "template", function(){
				return "";
			});
			stub.append = sinon.stub(beanDataSourceView.$el, "append");

			beanDataSourceView.renderBeanSpecificSection();

			expect(stub.append).toHaveBeenCalled();
			expect(_.isString(stub.append.getCall(0).args[0])).toBeTruthy();

			beanDataSourceView.$el.append.restore();
			_.template.restore();
			BaseDataSourceView.prototype.templateData.restore();
		});
	});
});