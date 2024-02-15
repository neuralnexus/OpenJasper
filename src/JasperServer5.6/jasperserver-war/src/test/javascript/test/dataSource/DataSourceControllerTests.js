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
 * @version: $Id$
 */

define(function(require) {

	"use strict";

	var
		$ = require("jquery"),
		_ = require("underscore"),
		sinon = require("sinon"),
		jrsConfigs = require('jrs.configs'),
		history = require("common/util/historyHelper"),
		resourceLocator = require("resource.locate"),
		BaseDataSourceModel = require("dataSource/model/BaseDataSourceModel"),
		dataSourceViewFactory = require("dataSource/factory/dataSourceViewFactory"),
		dataSourceConfig = require("test/dataSource/dataSourceConfig.js"),
		DataSourceController = require("dataSource/DataSourceController"),
		featureDetection = require("common/util/featureDetection"),
		RepositoryResourceModel = require("common/model/RepositoryResourceModel"),
		CustomDataSourcesCollection = require("dataSource/collection/CustomDataSourceCollection");


	describe("Testing DataSourceController", function() {

		var dataSourceController, stub = {};

		beforeEach(function() {
			jrsConfigs.addDataSource = dataSourceConfig;
			stub.resource = sinon.stub(resourceLocator, "initialize");
		});

		afterEach(function(){
			delete jrsConfigs.addDataSource;
			resourceLocator.initialize.restore();
		});

		it("DataSourceController should be defined", function() {
			expect(DataSourceController).toBeDefined();
			expect(DataSourceController).toBeFunction();
		});

		it("DataSourceController should have constructor method", function() {
			expect(DataSourceController.prototype.constructor).toBeDefined();
			expect(DataSourceController.prototype.constructor).toBeFunction();
		});

		it("DataSourceController should have initialize method", function() {
			expect(DataSourceController.prototype.initialize).toBeDefined();
			expect(DataSourceController.prototype.initialize).toBeFunction();
		});

		it("DataSourceController should have render method", function() {
			expect(DataSourceController.prototype.render).toBeDefined();
			expect(DataSourceController.prototype.render).toBeFunction();
		});

		it("DataSourceController should have _render method", function() {
			expect(DataSourceController.prototype._render).toBeDefined();
			expect(DataSourceController.prototype._render).toBeFunction();
		});

		it("DataSourceController should have initSwipeScroll method", function() {
			expect(DataSourceController.prototype.initSwipeScroll).toBeDefined();
			expect(DataSourceController.prototype.initSwipeScroll).toBeFunction();
		});

		it("DataSourceController should have onSaveClick method", function() {
			expect(DataSourceController.prototype.onSaveClick).toBeDefined();
			expect(DataSourceController.prototype.onSaveClick).toBeFunction();
		});

		it("DataSourceController should have onCancelClick method", function() {
			expect(DataSourceController.prototype.onCancelClick).toBeDefined();
			expect(DataSourceController.prototype.onCancelClick).toBeFunction();
		});

		it("DataSourceController should have onDataSourceTypeChange method", function() {
			expect(DataSourceController.prototype.onDataSourceTypeChange).toBeDefined();
			expect(DataSourceController.prototype.onDataSourceTypeChange).toBeFunction();
		});

		it("DataSourceController should have renderDataSourceContainer method", function() {
			expect(DataSourceController.prototype.renderDataSourceContainer).toBeDefined();
			expect(DataSourceController.prototype.renderDataSourceContainer).toBeFunction();
		});

		it("DataSourceController constructor should call the BackboneView method", function() {
			stub.view = sinon.stub(Backbone, "View");

			dataSourceController = new DataSourceController({});

			expect(stub.view).toHaveBeenCalled();
			Backbone.View.restore();
		});

		it("DataSourceController constructor should create a copy of options passed to him", function() {
			var testOptions = {isEditMode: true, someKey: "someValue"};

			stub.view = sinon.stub(Backbone, "View");

			dataSourceController = new DataSourceController(testOptions);

			expect(stub.view.getCall(0).args[0]).not.toBe(testOptions);
			expect(stub.view.getCall(0).args[0].someKey).toBe("someValue");
			Backbone.View.restore();
		});

		it("DataSourceController initialize method should be called on creating the object", function() {

			stub.init = sinon.stub(DataSourceController.prototype, "initialize");

			dataSourceController = new DataSourceController({});

			expect(stub.init).toHaveBeenCalled();
			DataSourceController.prototype.initialize.restore();
			dataSourceController.remove();
		});

		xdescribe("Checking dataSourceController initialize method", function() {

			beforeEach(function(){
				stub.render = sinon.stub(DataSourceController.prototype, "render");
				stub.initSwipeScroll = sinon.stub(DataSourceController.prototype, "initSwipeScroll");
				stub.featureDetection = featureDetection.supportsTouch; featureDetection.supportsTouch = true;
				stub.customDataSourcesCollectionFetch = sinon.stub(CustomDataSourcesCollection.prototype, "fetch", function(){
					return $.Deferred();
				});
				stub.baseDataSourceModelFetch = sinon.stub(BaseDataSourceModel.prototype, "fetch", function(){
					return $.Deferred().resolve("data", "textStatus", {
						getResponseHeader: function() {
							return "myValue";
						}
					});
				});
				stub.dataSourceViewFactoryGetViewType = sinon.stub(dataSourceViewFactory, "getViewType");
			});

			afterEach(function(){
				DataSourceController.prototype.render.restore();
				DataSourceController.prototype.initSwipeScroll.restore();
				CustomDataSourcesCollection.prototype.fetch.restore();
				featureDetection.supportsTouch = stub.featureDetection;
				BaseDataSourceModel.prototype.fetch.restore();
				dataSourceViewFactory.getViewType.restore();
				dataSourceController.remove();
			});

			it("DataSourceController initialize method should call some methods in editing mode", function() {
				dataSourceController = new DataSourceController({
					resourceUri: "/aaa/bbb/ccc"
				});

				expect(stub.initSwipeScroll).toHaveBeenCalled();
				expect(stub.customDataSourcesCollectionFetch).toHaveBeenCalled();
				expect(stub.dataSourceViewFactoryGetViewType).toHaveBeenCalled();
				expect(stub.dataSourceViewFactoryGetViewType.getCall(0).args[0]).toBe("myValue");
				expect(stub.baseDataSourceModelFetch).toHaveBeenCalled();
			});

			it("DataSourceController initialize method should call some methods in creating mode", function() {
				dataSourceController = new DataSourceController({
					resourceUri: false,
					dataSource: "abc",
					dataSourceClientType: "def"
				});

				expect(stub.initSwipeScroll).toHaveBeenCalled();
				expect(stub.customDataSourcesCollectionFetch).toHaveBeenCalled();
				expect(stub.dataSourceViewFactoryGetViewType).toHaveBeenCalledWith("def", "abc");
			});
		});

		xdescribe("Checking dataSourceController internal methods", function() {
			beforeEach(function(){
				stub.render = sinon.stub(DataSourceController.prototype, "render");
				sinon.stub(CustomDataSourcesCollection.prototype, "fetch", function(){
					return $.Deferred();
				});
				sinon.stub(BaseDataSourceModel.prototype, "fetch", function(){
					return $.Deferred();
				});
				stub.dataSourceViewFactoryGetView = sinon.stub(dataSourceViewFactory, "getView");
				stub.dataSourceViewFactoryGetViewType = sinon.stub(dataSourceViewFactory, "getViewType");

				dataSourceController = new DataSourceController({
					resourceUri: "/aaa/bbb/ccc"
				});
			});

			afterEach(function(){
				DataSourceController.prototype.render.restore();
				CustomDataSourcesCollection.prototype.fetch.restore();
				BaseDataSourceModel.prototype.fetch.restore();
				dataSourceViewFactory.getView.restore();
				dataSourceViewFactory.getViewType.restore();
				dataSourceController.remove();
			});

			it("DataSourceController renderDataSourceContainer method should call a template functions", function() {

				stub.template = sinon.stub(_, "template");

				dataSourceController.renderDataSourceContainer();

				expect(stub.template).toHaveBeenCalled();

				_.template.restore();
			});

			it("DataSourceController _render method should create a View", function() {

				dataSourceController._render();

				expect(stub.dataSourceViewFactoryGetView).toHaveBeenCalled();
			});

			it("DataSourceController onDataSourceTypeChange() method should call render in case of change", function() {

				dataSourceController.onDataSourceTypeChange({target: $("body")});

				expect(stub.render).toHaveBeenCalled();
			});
		});

		xdescribe("render should call _render() method only when two deferred object are resolved", function() {

			beforeEach(function(){
				sinon.stub(DataSourceController.prototype, "initialize");
				stub.render = sinon.stub(DataSourceController.prototype, "_render");
				sinon.stub(BaseDataSourceModel.prototype, "fetch", function(){
					return $.Deferred();
				});

				dataSourceController = new DataSourceController({});
			});

			afterEach(function(){
				DataSourceController.prototype.initialize.restore();
				DataSourceController.prototype._render.restore();
				BaseDataSourceModel.prototype.fetch.restore();
				dataSourceController.remove();
			});

			it("_render() should not be called", function(){
				dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred();
				dataSourceController.fetchingTheModelDeferred = $.Deferred();
				dataSourceController.render();

				expect(stub.render).not.toHaveBeenCalled();
			});

			it("_render() should not be called with only one resolved: case 1", function(){
				dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred().resolve();
				dataSourceController.fetchingTheModelDeferred = $.Deferred();
				dataSourceController.render();

				expect(stub.render).not.toHaveBeenCalled();
			});

			it("_render() should not be called with only one resolved: case 2", function(){
				dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred();
				dataSourceController.fetchingTheModelDeferred = $.Deferred().resolve();
				dataSourceController.render();

				expect(stub.render).not.toHaveBeenCalled();
			});

			it("_render() should be called", function(){
				dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred().resolve();
				dataSourceController.fetchingTheModelDeferred = $.Deferred().resolve();
				dataSourceController.render();

				expect(stub.render).toHaveBeenCalled();
			});
		});


		describe("Checking dataSourceController save and cancel methods", function() {

			var fakeServer, doCallCallbacks = false, whichCallbackToCall = false;

			beforeEach(function(){

				stub.render = sinon.stub(DataSourceController.prototype, "render");
				stub._onSaveDone = sinon.stub(DataSourceController.prototype, "_onSaveDone");
				stub._onSaveFail = sinon.stub(DataSourceController.prototype, "_onSaveFail");
				sinon.stub(CustomDataSourcesCollection.prototype, "fetch", function(){
					return $.Deferred();
				});
				sinon.stub(BaseDataSourceModel.prototype, "fetch", function(){
					return $.Deferred();
				});

				stub.save = sinon.stub(RepositoryResourceModel.prototype, "save", function(_tmp, options) {
					if (!options || !doCallCallbacks) {
						return;
					}
					if (whichCallbackToCall) {
						if (options.success) { options.success(); }
					} else {
						if (options.error) { options.error(); }
					}
				});

				stub.cancelFn = sinon.stub();

				fakeServer = sinon.fakeServer.create();

				dataSourceController = new DataSourceController({
					resourceUri: "/aaa/bbb/ccc",
					cancelFn: stub.cancelFn
				});

				dataSourceController._render();

				stub.isValid = sinon.stub(dataSourceController.dataSourceView.model, "isValid", function(){
					return true;
				});
			});

			afterEach(function(){
				DataSourceController.prototype.render.restore();
				DataSourceController.prototype._onSaveDone.restore();
				DataSourceController.prototype._onSaveFail.restore();
				CustomDataSourcesCollection.prototype.fetch.restore();
				BaseDataSourceModel.prototype.fetch.restore();
				RepositoryResourceModel.prototype.save.restore();
				dataSourceController.dataSourceView.model.isValid.restore();
				dataSourceController.remove();
				fakeServer.respond();
			});

			it("DataSourceController onSaveClick() method should call validation", function() {

				dataSourceController.onSaveClick();

				expect(stub.isValid).toHaveBeenCalled();
			});

			it("DataSourceController onSaveClick() method should call save method in case of valid model", function() {

				dataSourceController.onSaveClick();

				expect(stub.save).toHaveBeenCalled();
			});

			it("DataSourceController onSaveClick() method should able to call done handler", function() {

				doCallCallbacks = true;
				whichCallbackToCall = true;
				dataSourceController.onSaveClick();

				expect(stub._onSaveDone).toHaveBeenCalled();
			});

			it("DataSourceController onSaveClick() method should able to call fail handler", function() {

				doCallCallbacks = true;
				whichCallbackToCall = false;
				dataSourceController.onSaveClick();

				expect(stub._onSaveFail).toHaveBeenCalled();
			});

			it("DataSourceController onCancelClick() method should call cancelFn function", function() {

				dataSourceController.onCancelClick();

				expect(stub.cancelFn).toHaveBeenCalled();
			});

			it("DataSourceController onCancelClick() method should able to call history.restore() method", function() {

				var historyStub = sinon.stub(history, "restore");

				// re-init the DS
				dataSourceController.remove();
				dataSourceController = new DataSourceController({
					resourceUri: "/aaa/bbb/ccc"
				});
				dataSourceController._render();
				stub.isValid = sinon.stub(dataSourceController.dataSourceView.model, "isValid", function(){
					return true;
				});
				dataSourceController.onCancelClick();

				expect(historyStub).toHaveBeenCalled();

				history.restore.restore();
			});
		});
	});
});