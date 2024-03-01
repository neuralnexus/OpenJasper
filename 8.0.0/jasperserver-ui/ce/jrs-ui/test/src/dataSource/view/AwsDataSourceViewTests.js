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
import jrsConfigs from 'js-sdk/src/jrs.configs';
import dataSourceConfig from '../test/mock/dataSourceConfigMock'
import buttonManager from 'src/core/core.events.bis';
import UploadJdbcDriverDialog from 'src/dataSource/view/dialog/UploadJdbcDriverDialog';
import AwsDataSourceModel from 'src/dataSource/model/AwsDataSourceModel';
import AwsDataSourceView from 'src/dataSource/view/AwsDataSourceView';
import BaseDataSourceView from 'src/dataSource/view/BaseDataSourceView';

describe('Testing AwsDataSourceView', function () {
    var awsDataSourceView, fakeServer, stub = {};
    beforeEach(function () {
        fakeServer = sinon.fakeServer.create();
        jrsConfigs.addDataSource = dataSourceConfig;
        stub.initDataSourceTree = sinon.stub(AwsDataSourceView.prototype, 'initDataSourceTree');
    });
    afterEach(function () {
        fakeServer.restore();
        AwsDataSourceView.prototype.initDataSourceTree.restore();
        delete jrsConfigs.addDataSource;
    });
    it('AwsDataSourceView should be defined', function () {
        expect(AwsDataSourceView).toBeDefined();
        expect(typeof AwsDataSourceView).toEqual("function");
    });
    it('AwsDataSourceView: check inheritance from JdbcDataSourceView', function () {
        var spy = sinon.spy(BaseDataSourceView.prototype, 'initialize');
        awsDataSourceView = new AwsDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
            dataSourceType: undefined,
            dataSource: undefined,
            el: $('[name=dataSourceTestArea]')
        }));
        expect(spy).toHaveBeenCalled();
        spy.restore();
        awsDataSourceView.remove();
    });
    it('JdbcDataSourceView: initialize() method should call set listeners', function () {
        stub.listen = sinon.stub(AwsDataSourceView.prototype, 'listenTo');
        awsDataSourceView = new AwsDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
            dataSourceType: undefined,
            dataSource: undefined,
            el: $('[name=dataSourceTestArea]')
        }));
        expect(stub.listen).toHaveBeenCalled();
        awsDataSourceView.remove();
        AwsDataSourceView.prototype.listenTo.restore();
    });
    describe('Checking AwsDataSourceView\'s work', function () {
        beforeEach(function () {
            awsDataSourceView = new AwsDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
                dataSourceType: undefined,
                dataSource: undefined,
                el: $('[name=dataSourceTestArea]')
            }));
        });
        afterEach(function () {
            awsDataSourceView.remove();
        });
        it('AwsDataSourceView: changeCredentialsType() should update model attribute', function () {
            stub.set = sinon.stub(awsDataSourceView.model, 'set');
            awsDataSourceView.changeCredentialsType();
            expect(stub.set).toHaveBeenCalled();
            awsDataSourceView.model.set.restore();
        });
        it('AwsDataSourceView: recheckDriver() should validate model and change upload driver', function () {
            stub.validate = sinon.stub(awsDataSourceView.model, 'validate');
            stub.changeUploadDriverButtonState = sinon.stub(awsDataSourceView, 'changeUploadDriverButtonState');
            awsDataSourceView.recheckDriver();
            expect(stub.validate).toHaveBeenCalled();
            expect(stub.changeUploadDriverButtonState).toHaveBeenCalled();
            awsDataSourceView.model.validate.restore();
            awsDataSourceView.changeUploadDriverButtonState.restore();
        });
        it('AwsDataSourceView: changeUploadDriverButtonState() should disable button manager in case of empty driverClass', function () {
            stub.disable = sinon.stub(buttonManager, 'disable');
            awsDataSourceView.model.attributes.driverClass = '';
            awsDataSourceView.changeUploadDriverButtonState();
            expect(stub.disable).toHaveBeenCalled();
            buttonManager.disable.restore();
        });
        it('AwsDataSourceView: changeUploadDriverButtonState() should enable button manager in case of non-empty driverClass', function () {
            stub.enable = sinon.stub(buttonManager, 'enable');
            awsDataSourceView.model.attributes.driverClass = 'aaa';
            awsDataSourceView.changeUploadDriverButtonState();
            expect(stub.enable).toHaveBeenCalled();
            buttonManager.enable.restore();
        });
        it('AwsDataSourceView: initDriverUploadDialog() should init driver upload dialog', function () {
            stub.init = sinon.stub(UploadJdbcDriverDialog.prototype, 'initialize');
            awsDataSourceView.model.attributes.driverClass = 'aaa';
            awsDataSourceView.model.drivers = {
                getDriverByClass: function () {
                }
            };
            awsDataSourceView.initDriverUploadDialog();
            expect(stub.init).toHaveBeenCalled();
            UploadJdbcDriverDialog.prototype.initialize.restore();
        });
        it('AwsDataSourceView: onCredentialsTypeChange() should show tree', function () {
            stub.showAwsDsTree = sinon.stub(awsDataSourceView, 'showAwsDsTree');
            awsDataSourceView.model.attributes.credentialsType = AwsDataSourceModel.credentialsType.EC2;
            awsDataSourceView.onCredentialsTypeChange();
            expect(stub.showAwsDsTree).toHaveBeenCalled();
            awsDataSourceView.showAwsDsTree.restore();
        });
        it('AwsDataSourceView: refreshAwsDataSourceTree() should refresh the tree', function () {
            stub.showAwsDsTree = sinon.stub(awsDataSourceView, 'showAwsDsTree');
            awsDataSourceView.refreshAwsDataSourceTree({
                preventDefault: function () {
                }
            });
            expect(stub.showAwsDsTree).toHaveBeenCalled();
            awsDataSourceView.showAwsDsTree.restore();
        });
        it('AwsDataSourceView: render() should call certain set of functions', function () {
            stub.empty = sinon.stub(awsDataSourceView.$el, 'empty');
            stub.renderTimezoneSection = sinon.stub(BaseDataSourceView.prototype, 'renderTimezoneSection');
            stub.renderAwsSpecificSection = sinon.stub(AwsDataSourceView.prototype, 'renderAwsSpecificSection');
            stub.renderTestConnectionSection = sinon.stub(BaseDataSourceView.prototype, 'renderTestConnectionSection');
            stub.showAwsDsTree = sinon.stub(AwsDataSourceView.prototype, 'showAwsDsTree');
            awsDataSourceView.options.isEditMode = false;
            awsDataSourceView.model.attributes.credentialsType = AwsDataSourceModel.credentialsType.EC2;
            var res = 'abc';
            stub.getFullDbTreePath = sinon.stub().returns(res);
            awsDataSourceView.model.getFullDbTreePath = stub.getFullDbTreePath;
            awsDataSourceView.render();
            expect(stub.empty).toHaveBeenCalled();
            expect(stub.renderTimezoneSection).toHaveBeenCalled();
            expect(stub.renderAwsSpecificSection).toHaveBeenCalled();
            expect(stub.renderTestConnectionSection).toHaveBeenCalled();
            expect(stub.initDataSourceTree).toHaveBeenCalled();
            expect(stub.showAwsDsTree).toHaveBeenCalled();
            expect(stub.getFullDbTreePath).toHaveBeenCalled();
            expect(stub.showAwsDsTree.getCall(0).args[0]).toBe(res);
            awsDataSourceView.$el.empty.restore();
            BaseDataSourceView.prototype.renderTimezoneSection.restore();
            AwsDataSourceView.prototype.renderAwsSpecificSection.restore();
            BaseDataSourceView.prototype.renderTestConnectionSection.restore();
            AwsDataSourceView.prototype.showAwsDsTree.restore();
        });
        it('AwsDataSourceView: showAwsDsTree() should show validate accessKey and secretKey', function () {
            var accessKey = 'accessKey', secretKey = 'secretKey', path = '/path';
            stub.validate = sinon.stub(awsDataSourceView.model, 'validate');
            awsDataSourceView.model.attributes.accessKey = accessKey;
            awsDataSourceView.model.attributes.secretKey = secretKey;
            stub.isValid = sinon.stub(awsDataSourceView.model, 'isValid').callsFake(function () {
                return true;
            });
            stub.showTreePrefetchNodes = sinon.stub();
            awsDataSourceView.awsDataSourceTree = { showTreePrefetchNodes: stub.showTreePrefetchNodes };
            awsDataSourceView.showAwsDsTree(path);
            expect(stub.validate).toHaveBeenCalled();
            expect(stub.validate.getCall(0).args[0].accessKey).toBe(accessKey);
            expect(stub.validate.getCall(0).args[0].secretKey).toBe(secretKey);
            expect(stub.isValid).toHaveBeenCalled();
            expect(stub.isValid.getCall(0).args[0]).toBe(secretKey);
            expect(stub.isValid.getCall(1).args[0]).toBe(accessKey);
            expect(stub.showTreePrefetchNodes).toHaveBeenCalled();
            expect(stub.showTreePrefetchNodes.getCall(0).args[0]).toBe(path);
            awsDataSourceView.model.validate.restore();
            awsDataSourceView.awsDataSourceTree = false;
        });
        it('AwsDataSourceView: renderAwsSpecificSection() should extend html and change the driver button', function () {
            stub.append = sinon.stub(awsDataSourceView.$el, 'append');
            stub.changeUploadDriverButtonState = sinon.stub(awsDataSourceView, 'changeUploadDriverButtonState');
            awsDataSourceView.renderAwsSpecificSection();
            expect(stub.append).toHaveBeenCalled();
            expect(stub.changeUploadDriverButtonState).toHaveBeenCalled();
            awsDataSourceView.$el.append.restore();
            awsDataSourceView.changeUploadDriverButtonState.restore();
        });
    });
});