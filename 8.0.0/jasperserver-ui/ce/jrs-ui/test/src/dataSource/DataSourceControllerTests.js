/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import sinon from 'sinon';
import Backbone from 'backbone';
import $ from 'jquery';
import _ from 'underscore';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import history from 'src/util/historyHelper';
import resourceLocator from 'src/resource/resource.locate';
import BaseDataSourceModel from 'src/dataSource/model/BaseDataSourceModel';
import dataSourceViewFactory from 'src/dataSource/factory/dataSourceViewFactory';
import dataSourceConfig from './test/mock/dataSourceConfigMock'
import DataSourceController from 'src/dataSource/DataSourceController';
import featureDetection from 'js-sdk/src/common/util/featureDetection';
import RepositoryResourceModel from 'bi-repository/src/bi/repository/model/RepositoryResourceModel';
import BaseSaveDialogView from 'src/dataSource/saveDialog/BaseSaveDialogView';
import CustomDataSourcesCollection from 'src/dataSource/collection/CustomDataSourceCollection';
import redirectToUrl from 'src/util/redirectToUrlUtil';

describe('Testing DataSourceController', function () {
    var dataSourceController, stub = {};

    beforeEach(function () {
        sinon.stub(redirectToUrl, 'redirect');

        jrsConfigs.addDataSource = dataSourceConfig;
        stub.resource = sinon.stub(resourceLocator, 'initialize');
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        redirectToUrl.redirect.restore();
        resourceLocator.initialize.restore();
    });
    it('DataSourceController should be defined', function () {
        expect(DataSourceController).toBeDefined();
        expect(typeof DataSourceController).toEqual("function");
    });
    it('DataSourceController should have constructor method', function () {
        expect(DataSourceController.prototype.constructor).toBeDefined();
        expect(typeof DataSourceController.prototype.constructor).toEqual("function");
    });
    it('DataSourceController should have initialize method', function () {
        expect(DataSourceController.prototype.initialize).toBeDefined();
        expect(typeof DataSourceController.prototype.initialize).toEqual("function");
    });
    it('DataSourceController should have render method', function () {
        expect(DataSourceController.prototype.render).toBeDefined();
        expect(typeof DataSourceController.prototype.render).toEqual("function");
    });
    it('DataSourceController should have _render method', function () {
        expect(DataSourceController.prototype._render).toBeDefined();
        expect(typeof DataSourceController.prototype._render).toEqual("function");
    });
    it('DataSourceController should have initSwipeScroll method', function () {
        expect(DataSourceController.prototype.initSwipeScroll).toBeDefined();
        expect(typeof DataSourceController.prototype.initSwipeScroll).toEqual("function");
    });
    it('DataSourceController should have onSaveClick method', function () {
        expect(DataSourceController.prototype.onSaveClick).toBeDefined();
        expect(typeof DataSourceController.prototype.onSaveClick).toEqual("function");
    });
    it('DataSourceController should have onCancelClick method', function () {
        expect(DataSourceController.prototype.onCancelClick).toBeDefined();
        expect(typeof DataSourceController.prototype.onCancelClick).toEqual("function");
    });
    it('DataSourceController should have onDataSourceTypeChange method', function () {
        expect(DataSourceController.prototype.onDataSourceTypeChange).toBeDefined();
        expect(typeof DataSourceController.prototype.onDataSourceTypeChange).toEqual("function");
    });
    it('DataSourceController should have renderDataSourceContainer method', function () {
        expect(DataSourceController.prototype.renderDataSourceContainer).toBeDefined();
        expect(typeof DataSourceController.prototype.renderDataSourceContainer).toEqual("function");
    });
    it('DataSourceController constructor should call the BackboneView method', function () {
        stub.view = sinon.stub(Backbone, 'View');
        dataSourceController = new DataSourceController({});
        expect(stub.view).toHaveBeenCalled();
        Backbone.View.restore();
    });
    it('DataSourceController constructor should create a copy of options passed to him', function () {
        var testOptions = {
            isEditMode: true,
            someKey: 'someValue'
        };
        stub.view = sinon.stub(Backbone, 'View');
        dataSourceController = new DataSourceController(testOptions);
        expect(stub.view.getCall(0).args[0]).not.toBe(testOptions);
        expect(stub.view.getCall(0).args[0].someKey).toBe('someValue');
        Backbone.View.restore();
    });
    it('DataSourceController initialize method should be called on creating the object', function () {
        stub.init = sinon.stub(DataSourceController.prototype, 'initialize');
        dataSourceController = new DataSourceController({});
        expect(stub.init).toHaveBeenCalled();
        DataSourceController.prototype.initialize.restore();
        dataSourceController.remove();
    });
    describe('Checking dataSourceController initialize method', function () {
        beforeEach(function () {
            stub.render = sinon.stub(DataSourceController.prototype, 'render');
            stub.initSwipeScroll = sinon.stub(DataSourceController.prototype, 'initSwipeScroll');
            stub.featureDetection = featureDetection.supportsTouch;
            featureDetection.supportsTouch = true;
            stub.customDataSourcesCollectionFetch = sinon.stub(CustomDataSourcesCollection.prototype, 'fetch').callsFake(function () {
                return $.Deferred();
            });
            stub.baseDataSourceModelFetch = sinon.stub(BaseDataSourceModel.prototype, 'fetch').callsFake(function () {
                return $.Deferred().resolve('data', 'textStatus', {
                    getResponseHeader: function () {
                        return 'myValue';
                    }
                });
            });
            stub.dataSourceViewFactoryGetViewType = sinon.stub(dataSourceViewFactory, 'getViewType');
        });
        afterEach(function () {
            DataSourceController.prototype.render.restore();
            DataSourceController.prototype.initSwipeScroll.restore();
            CustomDataSourcesCollection.prototype.fetch.restore();
            featureDetection.supportsTouch = stub.featureDetection;
            BaseDataSourceModel.prototype.fetch.restore();
            dataSourceViewFactory.getViewType.restore();
            dataSourceController.remove();
        });

        it("DataSourceController initialize method should call some methods in editing mode", function() {
            var clock = sinon.useFakeTimers();

            dataSourceController = new DataSourceController({
                resourceUri: "/aaa/bbb/ccc"
            });

            clock.tick(1);

            expect(stub.initSwipeScroll).toHaveBeenCalled();
            expect(stub.customDataSourcesCollectionFetch).toHaveBeenCalled();
            expect(stub.dataSourceViewFactoryGetViewType).toHaveBeenCalled();
            expect(stub.dataSourceViewFactoryGetViewType.getCall(0).args[0]).toBe('myValue');
            expect(stub.baseDataSourceModelFetch).toHaveBeenCalled();
        });
        it('DataSourceController initialize method should call some methods in creating mode', function () {
            dataSourceController = new DataSourceController({
                resourceUri: false,
                dataSource: 'abc',
                dataSourceClientType: 'def'
            });
            expect(stub.initSwipeScroll).toHaveBeenCalled();
            expect(stub.customDataSourcesCollectionFetch).toHaveBeenCalled();
            expect(stub.dataSourceViewFactoryGetViewType).toHaveBeenCalledWith('def', 'abc');
        });
    });
    describe('Checking dataSourceController internal methods', function () {
        beforeEach(function () {
            stub.render = sinon.stub(DataSourceController.prototype, 'render');
            sinon.stub(CustomDataSourcesCollection.prototype, 'fetch').callsFake(function () {
                return $.Deferred();
            });
            sinon.stub(BaseDataSourceModel.prototype, 'fetch').callsFake(function () {
                return $.Deferred();
            });
            stub.dataSourceViewFactoryGetView = sinon.stub(dataSourceViewFactory, 'getView');
            stub.dataSourceViewFactoryGetViewType = sinon.stub(dataSourceViewFactory, 'getViewType');
            dataSourceController = new DataSourceController({ resourceUri: '/aaa/bbb/ccc' });
        });
        afterEach(function () {
            DataSourceController.prototype.render.restore();
            CustomDataSourcesCollection.prototype.fetch.restore();
            BaseDataSourceModel.prototype.fetch.restore();
            dataSourceViewFactory.getView.restore();
            dataSourceViewFactory.getViewType.restore();
            dataSourceController.remove();
        });
        it('DataSourceController renderDataSourceContainer method should call a template functions', function () {
            stub.template = sinon.stub(_, 'template');
            dataSourceController.renderDataSourceContainer();
            expect(stub.template).toHaveBeenCalled();
            _.template.restore();
        });
        it('DataSourceController _render method should create a View', function () {
            dataSourceController._render();
            expect(stub.dataSourceViewFactoryGetView).toHaveBeenCalled();
        });
        it('DataSourceController onDataSourceTypeChange() method should call render in case of change', function () {
            dataSourceController.onDataSourceTypeChange({ target: $('body') });
            expect(stub.render).toHaveBeenCalled();
        });
    });
    describe('render should call _render() method only when two deferred object are resolved', function () {
        beforeEach(function () {
            sinon.stub(DataSourceController.prototype, 'initialize');
            stub.render = sinon.stub(DataSourceController.prototype, '_render');
            sinon.stub(BaseDataSourceModel.prototype, 'fetch').callsFake(function () {
                return $.Deferred();
            });
            dataSourceController = new DataSourceController({});
        });
        afterEach(function () {
            DataSourceController.prototype.initialize.restore();
            DataSourceController.prototype._render.restore();
            BaseDataSourceModel.prototype.fetch.restore();
            dataSourceController.remove();
        });
        it('_render() should not be called', function () {
            dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred();
            dataSourceController.fetchingTheModelDeferred = $.Deferred();
            dataSourceController.render();
            expect(stub.render).not.toHaveBeenCalled();
        });
        it('_render() should not be called with only one resolved: case 1', function () {
            dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred().resolve();
            dataSourceController.fetchingTheModelDeferred = $.Deferred();
            dataSourceController.render();
            expect(stub.render).not.toHaveBeenCalled();
        });
        it('_render() should not be called with only one resolved: case 2', function () {
            dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred();
            dataSourceController.fetchingTheModelDeferred = $.Deferred().resolve();
            dataSourceController.render();
            expect(stub.render).not.toHaveBeenCalled();
        });
        it('_render() should be called', function () {
            dataSourceController.fetchingCustomDataSourcesDeferred = $.Deferred().resolve();
            dataSourceController.fetchingTheModelDeferred = $.Deferred().resolve();
            dataSourceController.render();
            expect(stub.render).toHaveBeenCalled();
        });
    });
    describe('Checking dataSourceController save and cancel methods', function () {
        var fakeServer, doCallCallbacks = false, whichCallbackToCall = false;
        beforeEach(function () {
            sinon.stub(BaseSaveDialogView.prototype, 'initializeTree');
            stub.render = sinon.stub(DataSourceController.prototype, 'render');
            stub._onSaveDone = sinon.stub(DataSourceController.prototype, '_onSaveDone');
            stub._onSaveAndCreateDomainDone = sinon.stub(DataSourceController.prototype, '_onSaveAndCreateDomainDone');
            stub._onSaveFail = sinon.stub(DataSourceController.prototype, '_onSaveFail');
            sinon.stub(CustomDataSourcesCollection.prototype, 'fetch').callsFake(function () {
                return $.Deferred();
            });
            sinon.stub(BaseDataSourceModel.prototype, 'fetch').callsFake(function () {
                return $.Deferred();
            });
            stub.save = sinon.stub(RepositoryResourceModel.prototype, 'save').callsFake(function (_tmp, options) {
                if (!options || !doCallCallbacks) {
                    return;
                }
                if (whichCallbackToCall) {
                    if (options.success) {
                        options.success();
                    }
                } else {
                    if (options.error) {
                        options.error();
                    }
                }
            });
            stub.cancelFn = sinon.stub();
            fakeServer = sinon.fakeServer.create();
            dataSourceController = new DataSourceController({
                resourceUri: '/aaa/bbb/ccc',
                cancelFn: stub.cancelFn
            });
            dataSourceController._render();
            stub.isValid = sinon.stub(dataSourceController.dataSourceView.model, 'isValid').callsFake(function () {
                return true;
            });
        });
        afterEach(function () {
            DataSourceController.prototype.render.restore();
            DataSourceController.prototype._onSaveDone.restore();
            DataSourceController.prototype._onSaveAndCreateDomainDone.restore();
            DataSourceController.prototype._onSaveFail.restore();
            CustomDataSourcesCollection.prototype.fetch.restore();
            BaseDataSourceModel.prototype.fetch.restore();
            RepositoryResourceModel.prototype.save.restore();
            BaseSaveDialogView.prototype.initializeTree.restore();
            dataSourceController.remove();
            fakeServer.respond();
            stub.isValid.restore && stub.isValid.restore();
        });
        it('DataSourceController onSaveClick() method should call validation', function () {
            dataSourceController.onSaveClick();
            expect(stub.isValid).toHaveBeenCalled();
        });
        it('DataSourceController onSaveClick() method should call save method in case of valid model', function () {
            dataSourceController.onSaveClick();
            dataSourceController.saveDialog.performSave();
            expect(stub.save).toHaveBeenCalled();
        });
        it('DataSourceController onSaveClick() method should able to call done handler', function () {
            doCallCallbacks = true;
            whichCallbackToCall = true;
            dataSourceController.onSaveClick();
            dataSourceController.saveDialog.performSave();
            expect(stub._onSaveDone).toHaveBeenCalled();
        });
        it('DataSourceController onSaveClick() method should able to call fail handler', function () {
            doCallCallbacks = true;
            whichCallbackToCall = false;
            dataSourceController.onSaveClick();
            dataSourceController.saveDialog.performSave();
            expect(stub._onSaveFail).toHaveBeenCalled();
        });
        it('DataSourceController onCancelClick() method should call cancelFn function', function () {
            dataSourceController.onCancelClick();
            expect(stub.cancelFn).toHaveBeenCalled();
        });
        it('DataSourceController onSaveClick() method should able to call create and save callback', function () {
            var e = $.Event('click');
            e.currentTarget = $('<div>').attr('id', 'createDomainBtn')[0];
            doCallCallbacks = true;
            whichCallbackToCall = true;
            dataSourceController.onSaveClick(e);
            dataSourceController.saveDialog.performSave();
            expect(stub._onSaveAndCreateDomainDone).toHaveBeenCalled();
        });
        it('DataSourceController onCancelClick() method should able to call history.restore() method', function () {
            var historyStub = sinon.stub(history, 'restore');    // re-init the DS
            // re-init the DS
            dataSourceController.remove();
            dataSourceController = new DataSourceController({ resourceUri: '/aaa/bbb/ccc' });
            dataSourceController._render();
            var validStub = sinon.stub(dataSourceController.dataSourceView.model, 'isValid').callsFake(function () {
                return true;
            });
            dataSourceController.onCancelClick();
            expect(historyStub).toHaveBeenCalled();
            history.restore.restore();
            validStub.restore();
        });
    });
});