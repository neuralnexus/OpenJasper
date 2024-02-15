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
 * @version: $Id: JdbcDataSourceViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		sinon = require("sinon"),
		jrsConfigs = require('jrs.configs'),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		UploadJdbcDriverDialog = require("dataSource/view/dialog/UploadJdbcDriverDialog"),
		JdbcDataSourceView = require("dataSource/view/JdbcDataSourceView"),
		BaseDataSourceView = require("dataSource/view/BaseDataSourceView"),
		dataSourceTestingHelper = require("test/dataSource/dataSourceTestingHelper.js");


	describe("Testing JdbcDataSourceView", function() {

		var jdbcDataSourceView, fakeServer, stub = {};

		beforeEach(function() {
			fakeServer = sinon.fakeServer.create();
			jrsConfigs.addDataSource = dataSourceConfig;
		});

		afterEach(function(){
			fakeServer.restore();
			delete jrsConfigs.addDataSource;
		});

		it("JdbcDataSourceView should be defined", function() {
			expect(JdbcDataSourceView).toBeDefined();
			expect(JdbcDataSourceView).toBeFunction();
		});

		it("JdbcDataSourceView: check inheritance from BaseDataSourceView", function() {

			spyOn(BaseDataSourceView.prototype, "initialize").andCallThrough();

			jdbcDataSourceView = new JdbcDataSourceView(
				_.extend(jrsConfigs.addDataSource.initOptions, {
					dataSourceType: undefined,
					dataSource: undefined,
					el: $("[name=dataSourceTestArea]")
				})
			);

			expect(BaseDataSourceView.prototype.initialize).toHaveBeenCalled();

			jdbcDataSourceView.remove();
		});

		describe("JdbcDataSourceView's work", function() {

			beforeEach(function(){

				jdbcDataSourceView = new JdbcDataSourceView(
					_.extend(jrsConfigs.addDataSource.initOptions, {
						dataSourceType: undefined,
						dataSource: undefined,
						el: $("[name=dataSourceTestArea]")
					})
				);

			});

			afterEach(function(){
				jdbcDataSourceView.remove();
			});

			it("JdbcDataSourceView: manuallySetDriverClass() method should call changeUploadDriverButtonState", function() {

				stub.change = sinon.stub(JdbcDataSourceView.prototype, "changeUploadDriverButtonState");
				stub.modelSet = sinon.stub(jdbcDataSourceView.model, "set");
				stub.modelValidate = sinon.stub(jdbcDataSourceView.model, "validate");

				jdbcDataSourceView.manuallySetDriverClass();

				expect(stub.change).toHaveBeenCalled();
				expect(stub.modelSet).toHaveBeenCalled();
				expect(stub.modelValidate).toHaveBeenCalled();

				JdbcDataSourceView.prototype.changeUploadDriverButtonState.restore();
				jdbcDataSourceView.model.set.restore();
				jdbcDataSourceView.model.validate.restore();
			});

			it("JdbcDataSourceView: updateConnectionUrl() method should call validate only in case of other or uploaded driver", function() {

				stub.driver = sinon.stub(jdbcDataSourceView.model, "getCurrentDriver", function(){
					return {
						isOtherDriver: function(){
							return false;
						},
						isUploadedDriver: function(){
							return false;
						}
					};
				});
				stub.modelValidate = sinon.stub(jdbcDataSourceView.model, "validate");

				jdbcDataSourceView.updateConnectionUrl();

				expect(stub.driver).toHaveBeenCalled();
				expect(stub.modelValidate).toHaveBeenCalled();

				jdbcDataSourceView.model.validate.restore();
				jdbcDataSourceView.model.getCurrentDriver.restore();
			});

			it("JdbcDataSourceView: changeDriver() method should call render method and change the label of the button", function() {

				stub.render = sinon.stub(jdbcDataSourceView, "renderDriverCustomAttributeFields");
				stub.change = sinon.stub(jdbcDataSourceView, "changeUploadDriverButtonState");

				jdbcDataSourceView.changeDriver();

				expect(stub.render).toHaveBeenCalled();
				expect(stub.change).toHaveBeenCalled();

				jdbcDataSourceView.renderDriverCustomAttributeFields.restore();
				jdbcDataSourceView.changeUploadDriverButtonState.restore();
			});

			it("JdbcDataSourceView: changeUploadDriverButtonState() method should call render method and change the label of the button", function() {

				stub.render = sinon.stub(jdbcDataSourceView, "renderDriverCustomAttributeFields");
				stub.change = sinon.stub(jdbcDataSourceView, "changeUploadDriverButtonState");

				jdbcDataSourceView.changeDriver();

				expect(stub.render).toHaveBeenCalled();
				expect(stub.change).toHaveBeenCalled();

				jdbcDataSourceView.renderDriverCustomAttributeFields.restore();
				jdbcDataSourceView.changeUploadDriverButtonState.restore();
			});

			it("JdbcDataSourceView: uploadDriver() method should show driver upload dialog", function() {

				stub.show = sinon.stub();
				stub.dialog = sinon.stub(jdbcDataSourceView, "initDriverUploadDialog", function() {
					jdbcDataSourceView.driverUploadDialog = {
						show: stub.show
					}
				});

				jdbcDataSourceView.driverUploadDialog = false;
				jdbcDataSourceView.model.attributes.driverClass = "driverClass";
                jdbcDataSourceView.model.drivers.driverUploadEnabled = true;

				jdbcDataSourceView.uploadDriver();

				expect(stub.show).toHaveBeenCalled();
				expect(stub.dialog).toHaveBeenCalled();

				jdbcDataSourceView.driverUploadDialog = false;
				jdbcDataSourceView.initDriverUploadDialog.restore();
			});

			it("JdbcDataSourceView: initDriverUploadDialog() method should create upload dialog", function() {

				stub.init = sinon.stub(UploadJdbcDriverDialog.prototype, "initialize");

				jdbcDataSourceView.model.attributes.isOtherDriver = true;
				jdbcDataSourceView.model.attributes.driverClass = "driverClass";
				jdbcDataSourceView.model.getCurrentDriver = function() {
					return {
						get: function(){
							return "aaa";
						}
					};
				};

				jdbcDataSourceView.initDriverUploadDialog();

				expect(stub.init).toHaveBeenCalled();

				UploadJdbcDriverDialog.prototype.initialize.restore();
			});

			it("JdbcDataSourceView: render() method should call certain set of functions", function() {

				stub.empty = sinon.stub(jdbcDataSourceView.$el, "empty");
				stub.renderNameAndDescriptionSection = sinon.stub(BaseDataSourceView.prototype, "renderNameAndDescriptionSection");
				stub.renderJdbcSpecificSection = sinon.stub(JdbcDataSourceView.prototype, "renderJdbcSpecificSection");
				stub.renderTimezoneSection = sinon.stub(BaseDataSourceView.prototype, "renderTimezoneSection");
				stub.renderSaveLocationSection = sinon.stub(BaseDataSourceView.prototype, "renderSaveLocationSection");
				stub.renderTestConnectionSection = sinon.stub(BaseDataSourceView.prototype, "renderTestConnectionSection");

				jdbcDataSourceView.render();

				expect(stub.empty).toHaveBeenCalled();
				expect(stub.renderNameAndDescriptionSection).toHaveBeenCalled();
				expect(stub.renderJdbcSpecificSection).toHaveBeenCalled();
				expect(stub.renderTimezoneSection).toHaveBeenCalled();
				expect(stub.renderSaveLocationSection).toHaveBeenCalled();
				expect(stub.renderTestConnectionSection).toHaveBeenCalled();

				jdbcDataSourceView.$el.empty.restore();
				BaseDataSourceView.prototype.renderNameAndDescriptionSection.restore();
				JdbcDataSourceView.prototype.renderJdbcSpecificSection.restore();
				BaseDataSourceView.prototype.renderTimezoneSection.restore();
				BaseDataSourceView.prototype.renderSaveLocationSection.restore();
				BaseDataSourceView.prototype.renderTestConnectionSection.restore();
			});

			it("JdbcDataSourceView: testing templateData method", function(){

				var res = jdbcDataSourceView.templateData();

				expect(_.isUndefined(res.drivers)).toBeFalsy();
				expect(_.isUndefined(res.otherDriverValue)).toBeFalsy();
				expect(_.isUndefined(res.driverUploadEnabled)).toBeFalsy();
			});

			it("JdbcDataSourceView: renderJdbcSpecificSection() method should call certain set of functions", function() {

				stub.append = sinon.stub(jdbcDataSourceView.$el, "append");
				stub.renderDriverCustomAttributeFields = sinon.stub(JdbcDataSourceView.prototype, "renderDriverCustomAttributeFields");
				stub.changeUploadDriverButtonState = sinon.stub(JdbcDataSourceView.prototype, "changeUploadDriverButtonState");

				jdbcDataSourceView.renderJdbcSpecificSection();

				expect(stub.append).toHaveBeenCalled();
				expect(stub.renderDriverCustomAttributeFields).toHaveBeenCalled();
				expect(stub.changeUploadDriverButtonState).toHaveBeenCalled();

				jdbcDataSourceView.$el.append.restore();
				JdbcDataSourceView.prototype.renderDriverCustomAttributeFields.restore();
				JdbcDataSourceView.prototype.changeUploadDriverButtonState.restore();
			});

			it("JdbcDataSourceView: renderDriverCustomAttributeFields() should call template function and append in case of other driver", function(){

				jdbcDataSourceView.model.attributes.isOtherDriver = true;
				stub.template = sinon.stub(_, "template");
				stub.$ = jdbcDataSourceView.$ = sinon.stub().returns({
					html: function(){}
				});

				jdbcDataSourceView.renderDriverCustomAttributeFields();

				expect(stub.template).toHaveBeenCalled();
				expect(stub.$).toHaveBeenCalled();

				_.template.restore();
			});

			it("JdbcDataSourceView: renderDriverCustomAttributeFields() should call template function and append in case of non other driver", function(){

				jdbcDataSourceView.model.attributes.isOtherDriver = false;
				stub.driver = sinon.stub(jdbcDataSourceView.model, "getCurrentDriver", function(){
					return {
						getCustomAttributes: function(){
							return ["driver"];
						}
					};
				});
				stub.template = sinon.stub(_, "template");
				stub.$ = jdbcDataSourceView.$ = sinon.stub().returns({
					html: function(){}
				});

				jdbcDataSourceView.renderDriverCustomAttributeFields();

				expect(stub.driver).toHaveBeenCalled();
				expect(stub.template).toHaveBeenCalled();
				expect(stub.$).toHaveBeenCalled();

				_.template.restore();
			});

			it("JdbcDataSourceView: remove() should remove driver upload dialog and call parent's remove function", function(){

				stub.remove = sinon.stub(BaseDataSourceView.prototype, "remove");
				stub.dialog = sinon.stub();
				jdbcDataSourceView.driverUploadDialog = {
					remove: stub.dialog
				};

				jdbcDataSourceView.remove();

				expect(stub.dialog).toHaveBeenCalled();
				expect(stub.remove).toHaveBeenCalled();

				BaseDataSourceView.prototype.remove.restore();
			});
		});
	});
});