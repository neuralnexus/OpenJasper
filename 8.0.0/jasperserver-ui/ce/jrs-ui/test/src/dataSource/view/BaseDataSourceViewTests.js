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
import $ from 'jquery';
import _ from 'underscore';
import i18n from 'src/i18n/all.properties';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import Backbone from 'backbone';
import dataSourceConfig from '../test/mock/dataSourceConfigMock';
import BaseDataSourceView from 'src/dataSource/view/BaseDataSourceView';
import dataSourceTestingHelper from '../test/helper/dataSourceTestingHelper';
import resourceLocator from 'src/resource/resource.locate';
import ResourceModel from 'bi-repository/src/bi/repository/model/RepositoryResourceModel';
import dialogs from 'src/components/components.dialogs';

describe('Testing BaseDataSourceView', function () {
    var baseDataSourceView, fakeServer, root, stub = {};
    beforeEach(function () {
        jrsConfigs.addDataSource = dataSourceConfig;
        stub.resource = sinon.stub(resourceLocator, 'initialize');
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        resourceLocator.initialize.restore();
    });
    it('BaseDataSourceView should be defined', function () {
        expect(BaseDataSourceView).toBeDefined();
        expect(typeof BaseDataSourceView).toEqual("function");
    });
    it('BaseDataSourceView should have constructor method', function () {
        expect(BaseDataSourceView.prototype.constructor).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.constructor).toEqual("function");
    });
    it('BaseDataSourceView should have initialize method', function () {
        expect(BaseDataSourceView.prototype.initialize).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.initialize).toEqual("function");
    });
    it('BaseDataSourceView should have testConnection method', function () {
        expect(BaseDataSourceView.prototype.testConnection).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.testConnection).toEqual("function");
    });
    it('BaseDataSourceView should have showTestConnectionMessageDetails method', function () {
        expect(BaseDataSourceView.prototype.showTestConnectionMessageDetails).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.showTestConnectionMessageDetails).toEqual("function");
    });
    it('BaseDataSourceView should have getTestConnectionErrorMessage method', function () {
        expect(BaseDataSourceView.prototype.getTestConnectionErrorMessage).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.getTestConnectionErrorMessage).toEqual("function");
    });
    it('BaseDataSourceView should have updateModelProperty method', function () {
        expect(BaseDataSourceView.prototype.updateModelProperty).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.updateModelProperty).toEqual("function");
    });
    it('BaseDataSourceView should have render method', function () {
        expect(BaseDataSourceView.prototype.render).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.render).toEqual("function");
    });
    it('BaseDataSourceView should have renderTimezoneSection method', function () {
        expect(BaseDataSourceView.prototype.renderTimezoneSection).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.renderTimezoneSection).toEqual("function");
    });
    it('BaseDataSourceView should have renderTestConnectionSection method', function () {
        expect(BaseDataSourceView.prototype.renderTestConnectionSection).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.renderTestConnectionSection).toEqual("function");
    });
    it('BaseDataSourceView should have templateData method', function () {
        expect(BaseDataSourceView.prototype.templateData).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.templateData).toEqual("function");
    });
    it('BaseDataSourceView should have setPageTitle method', function () {
        expect(BaseDataSourceView.prototype.setPageTitle).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.setPageTitle).toEqual("function");
    });
    it('BaseDataSourceView should have fieldIsValid method', function () {
        expect(BaseDataSourceView.prototype.fieldIsValid).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.fieldIsValid).toEqual("function");
    });
    it('BaseDataSourceView should have fieldIsInvalid method', function () {
        expect(BaseDataSourceView.prototype.fieldIsInvalid).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.fieldIsInvalid).toEqual("function");
    });
    it('BaseDataSourceView should have remove method', function () {
        expect(BaseDataSourceView.prototype.remove).toBeDefined();
        expect(typeof BaseDataSourceView.prototype.remove).toEqual("function");
    });
    it('BaseDataSourceView: initialize should call modelConstructor', function () {
        // preparations...
        fakeServer = sinon.fakeServer.create();
        dataSourceTestingHelper.beforeEach();
        stub.render = sinon.stub(BaseDataSourceView.prototype, 'render');
        sinon.stub(BaseDataSourceView.prototype, 'setPageTitle');
        stub.bind = sinon.stub(Backbone.Validation, 'bind');
        baseDataSourceView = new BaseDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
            dataSourceType: undefined,
            dataSource: undefined,
            el: $('[name=dataSourceTestArea]')
        }));    // testing...
        // testing...
        expect(_.isObject(baseDataSourceView.model)).toBeTruthy();
        expect(stub.bind).toHaveBeenCalled();
        expect(stub.render).toHaveBeenCalled();    // cleaning...
        // cleaning...
        Backbone.Validation.bind.restore();
        BaseDataSourceView.prototype.render.restore();
        BaseDataSourceView.prototype.setPageTitle.restore();    // removing...
        // removing...
        baseDataSourceView.remove();
        fakeServer.restore();
        dataSourceTestingHelper.afterEach();
    });
    describe('BaseDataSourceView\'s work', function () {
        beforeEach(function () {
            dataSourceTestingHelper.beforeEach();    // prepare fake server
            // prepare fake server
            fakeServer = sinon.fakeServer.create();    // create an variable which holds the DS in the DOM object
            // create an variable which holds the DS in the DOM object
            root = $('[name=dataSourceTestArea]');    // init the data source
            // init the data source
            baseDataSourceView = new BaseDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
                dataSourceType: undefined,
                dataSource: undefined,
                el: root
            }));
            baseDataSourceView.render();
        });
        afterEach(function () {
            // remove the data source from the page
            baseDataSourceView.remove();    // clear the testable area
            // clear the testable area
            root.empty();    // destroy fake XHR service
            // destroy fake XHR service
            fakeServer.restore();
            dataSourceTestingHelper.afterEach();
        });
        it('BaseDataSourceView: testing setPageTitle method', function () {
            baseDataSourceView.isEditMode = false;
            baseDataSourceView.PAGE_TITLE_NEW_MESSAGE_CODE = 'resource.datasource.aws.page.title.new';
            baseDataSourceView.setPageTitle();
            expect($('#display .showingToolBar > .content > .header > .title').text()).toBe(i18n['resource.datasource.aws.page.title.new']);
            baseDataSourceView.isEditMode = true;
            baseDataSourceView.PAGE_TITLE_EDIT_MESSAGE_CODE = 'resource.datasource.aws.page.title.edit';
            baseDataSourceView.model.attributes.label = 'someValue';
            baseDataSourceView.setPageTitle();
            expect($('#display .showingToolBar > .content > .header > .title').text()).toBe(i18n['resource.datasource.aws.page.title.edit'] + ': someValue');
        });
        it('BaseDataSourceView: testing templateData method', function () {
            var res = baseDataSourceView.templateData();
            expect(_.isUndefined(res.i18n)).toBeFalsy();
            expect(_.isUndefined(res.modelAttributes)).toBeFalsy();
            expect(_.isUndefined(res.timezones)).toBeFalsy();
            expect(_.isUndefined(res.isEditMode)).toBeFalsy();
        });
        it('BaseDataSourceView: renderTestConnectionSection calls append method on $el', function () {
            stub.append = sinon.stub(baseDataSourceView.$el, 'append');
            baseDataSourceView.renderTestConnectionSection();
            expect(stub.append).toHaveBeenCalled();
            expect(_.isString(stub.append.getCall(0).args[0])).toBeTruthy();
            baseDataSourceView.$el.append.restore();
        });
        it('BaseDataSourceView: renderTimezoneSection calls append method on $el', function () {
            stub.append = sinon.stub(baseDataSourceView.$el, 'append');
            baseDataSourceView.renderTimezoneSection();
            expect(stub.append).toHaveBeenCalled();
            expect(_.isString(stub.append.getCall(0).args[0])).toBeTruthy();
            baseDataSourceView.$el.append.restore();
        });
        it('BaseDataSourceView: render should clear $el', function () {
            stub.empty = sinon.stub(baseDataSourceView.$el, 'empty');
            baseDataSourceView.render();
            expect(stub.empty).toHaveBeenCalled();
            baseDataSourceView.$el.empty.restore();
        });
        it('BaseDataSourceView: updateModelProperty should call set and validate methods witht he same parameters', function () {
            stub.set = sinon.stub(baseDataSourceView.model, 'set');
            stub.validate = sinon.stub(baseDataSourceView.model, 'validate');
            baseDataSourceView.updateModelProperty({ target: false });
            expect(stub.set).toHaveBeenCalled();
            expect(stub.validate).toHaveBeenCalled();
            expect(stub.set.getCall(0).args[0]).toBe(stub.validate.getCall(0).args[0]);
            baseDataSourceView.model.set.restore();
            baseDataSourceView.model.validate.restore();
        });
        it('BaseDataSourceView: updateModelProperty should auto-generate resource name in case of change on name', function () {
            sinon.stub(baseDataSourceView.model, 'set');
            sinon.stub(baseDataSourceView.model, 'validate');
            stub.generate = sinon.stub(ResourceModel, 'generateResourceName');
            baseDataSourceView.$el.append('<div name=\'name\'></div>');
            baseDataSourceView.updateModelProperty({ target: baseDataSourceView.$el.find('[name=name]') });
            expect(stub.generate).toHaveBeenCalled();
            baseDataSourceView.model.set.restore();
            baseDataSourceView.model.validate.restore();
            ResourceModel.generateResourceName.restore();
        });
        it('BaseDataSourceView: updateModelProperty should auto-generate resource name in case of change on label', function () {
            sinon.stub(baseDataSourceView.model, 'set');
            sinon.stub(baseDataSourceView.model, 'validate');
            stub.generate = sinon.stub(ResourceModel, 'generateResourceName');
            baseDataSourceView.$el.append('<div name=\'label\'></div>');
            baseDataSourceView.updateModelProperty({ target: baseDataSourceView.$el.find('[name=label]') });
            expect(stub.generate).toHaveBeenCalled();
            baseDataSourceView.model.set.restore();
            baseDataSourceView.model.validate.restore();
            ResourceModel.generateResourceName.restore();
        });
        it('BaseDataSourceView: getTestConnectionErrorMessage should return proper object', function () {
            var res;
            res = baseDataSourceView.getTestConnectionErrorMessage(false);
            expect(res.text).toBe(i18n['resource.dataSource.connectionState.failed']);
            expect(res.details).toBeFalsy();
            res = baseDataSourceView.getTestConnectionErrorMessage({ responseText: '{"parameters":["aa","bb","cc"]}' });
            expect(res.text).toBe('cc');
            expect(res.details).toBeFalsy();
            res = baseDataSourceView.getTestConnectionErrorMessage({ responseText: '{"parameters":["aa","bb","cc","dd"]}' });
            expect(res.text).toBe('cc');
            expect(res.details).toBe('dd');
        });
        it('BaseDataSourceView: showTestConnectionMessageDetails should call dialog', function () {
            stub.dialog = sinon.stub(dialogs.errorPopup, 'show');
            baseDataSourceView.showTestConnectionMessageDetails();
            expect(stub.dialog).toHaveBeenCalled();
            dialogs.errorPopup.show.restore();
        });
        it('BaseDataSourceView: testConnection should call the model and create the message', function () {
            var drd = $.Deferred();
            stub.modelTest = sinon.stub(baseDataSourceView.model, 'testConnection').callsFake(function () {
                return drd;
            });
            stub.message = sinon.stub(baseDataSourceView, 'getTestConnectionErrorMessage').callsFake(function () {
                return {
                    text: 'aaa',
                    details: 'bbb'
                };
            });
            baseDataSourceView.testConnection();
            expect(stub.modelTest).toHaveBeenCalled();
            drd.reject();
            expect(stub.message).toHaveBeenCalled();
            baseDataSourceView.getTestConnectionErrorMessage.restore();
            baseDataSourceView.model.testConnection.restore();
        });
    });
});