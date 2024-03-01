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
import TextDataSourceView from 'src/dataSource/fileDataSource/TextDataSourceView';
import CustomDataSourceView from 'src/dataSource/view/CustomDataSourceView';
import resourceLocator from 'src/resource/resource.locate';

describe('Testing TextDataSourceView', function () {
    var textDataSourceView, fakeServer, stub = {};
    beforeEach(function () {
        fakeServer = sinon.fakeServer.create();
        jrsConfigs.addDataSource = dataSourceConfig;
    });
    afterEach(function () {
        fakeServer.restore();
        delete jrsConfigs.addDataSource;
    });
    it('TextDataSourceView should be defined', function () {
        expect(TextDataSourceView).toBeDefined();
        expect(typeof TextDataSourceView).toEqual("function");
    });
    it('TextDataSourceView should have changeFileSourceType method', function () {
        expect(TextDataSourceView.prototype.changeFileSourceType).toBeDefined();
        expect(typeof TextDataSourceView.prototype.changeFileSourceType).toEqual("function");
    });
    it('TextDataSourceView should have render method', function () {
        expect(TextDataSourceView.prototype.render).toBeDefined();
        expect(typeof TextDataSourceView.prototype.render).toEqual("function");
    });
    it('TextDataSourceView should have templateData method', function () {
        expect(TextDataSourceView.prototype.templateData).toBeDefined();
        expect(typeof TextDataSourceView.prototype.templateData).toEqual("function");
    });
    it('TextDataSourceView should have render renderTextDataSourceSection', function () {
        expect(TextDataSourceView.prototype.renderTextDataSourceSection).toBeDefined();
        expect(typeof TextDataSourceView.prototype.renderTextDataSourceSection).toEqual("function");
    });
    it('TextDataSourceView should have renderFileLocationSection method', function () {
        expect(TextDataSourceView.prototype.renderFileLocationSection).toBeDefined();
        expect(typeof TextDataSourceView.prototype.renderFileLocationSection).toEqual("function");
    });
    it('TextDataSourceView should have renderFilePropertiesSection method', function () {
        expect(TextDataSourceView.prototype.renderFilePropertiesSection).toBeDefined();
        expect(typeof TextDataSourceView.prototype.renderFilePropertiesSection).toEqual("function");
    });
    it('TextDataSourceView: check inheritance from CustomDataSourceView', function () {
        stub.customInit = sinon.stub(CustomDataSourceView.prototype, 'initialize');
        var listenTo = CustomDataSourceView.prototype.listenTo = sinon.stub();
        textDataSourceView = new TextDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
            dataSourceType: undefined,
            dataSource: undefined,
            el: $('[name=dataSourceTestArea]')
        }));
        expect(stub.customInit).toHaveBeenCalled();
        expect(listenTo).toHaveBeenCalled();
        textDataSourceView.remove();
        CustomDataSourceView.prototype.initialize.restore();
        delete CustomDataSourceView.prototype.listenTo;
    });
    describe('Testing TextDataSourceView\'s work', function () {
        beforeEach(function () {
            stub.resourceLocator = sinon.stub(resourceLocator, 'initialize').callsFake(function () {
                return {
                    remove: function () {
                    }
                };
            });
            textDataSourceView = new TextDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
                dataSourceType: undefined,
                dataSource: undefined,
                el: $('[name=dataSourceTestArea]')
            }));
        });
        afterEach(function () {
            textDataSourceView.remove();
            resourceLocator.initialize.restore();
        });
        it('TextDataSourceView: render() method should call certain set of functions', function () {
            stub.renderTextDataSourceSection = sinon.stub(TextDataSourceView.prototype, 'renderTextDataSourceSection');
            textDataSourceView.render();
            expect(stub.renderTextDataSourceSection).toHaveBeenCalled();
            TextDataSourceView.prototype.renderTextDataSourceSection.restore();
        });
        it('TextDataSourceView: templateData() method should supply necessary data', function () {
            var data = textDataSourceView.templateData();
            expect(_.isUndefined(data.fileSourceTypeOptions)).not.toBeTruthy();
            expect(_.isUndefined(data.fieldDelimiterOptions)).not.toBeTruthy();
            expect(_.isUndefined(data.rowDelimiterOptions)).not.toBeTruthy();
            expect(_.isUndefined(data.encodingOptions)).not.toBeTruthy();
        });
        it('TextDataSourceView: renderTextDataSourceSection() method should call certain set of functions', function () {
            stub.append = sinon.stub(textDataSourceView.$el, 'append');
            stub.renderFileLocationSection = sinon.stub(TextDataSourceView.prototype, 'renderFileLocationSection');
            stub.renderFilePropertiesSection = sinon.stub(TextDataSourceView.prototype, 'renderFilePropertiesSection');
            textDataSourceView.renderTextDataSourceSection();
            expect(stub.append).toHaveBeenCalled();
            expect(stub.renderTextDataSourceSection).toHaveBeenCalled();
            expect(stub.renderFilePropertiesSection).toHaveBeenCalled();
            textDataSourceView.$el.append.restore();
            TextDataSourceView.prototype.renderFileLocationSection.restore();
            TextDataSourceView.prototype.renderFilePropertiesSection.restore();
        });
        it('TextDataSourceView: renderFileLocationSection() method should call certain set of functions', function () {
            stub.renderOrAddAnyBlock = sinon.stub(TextDataSourceView.prototype, 'renderOrAddAnyBlock');
            stub.adjustFileSystemConnectButton = sinon.stub(TextDataSourceView.prototype, 'adjustFileSystemConnectButton');
            stub.adjustFtpServerConnectButton = sinon.stub(TextDataSourceView.prototype, 'adjustFtpServerConnectButton');
            textDataSourceView.renderFileLocationSection();
            expect(stub.renderOrAddAnyBlock).toHaveBeenCalled();
            expect(stub.adjustFileSystemConnectButton).toHaveBeenCalled();
            expect(stub.adjustFtpServerConnectButton).toHaveBeenCalled();
            TextDataSourceView.prototype.renderOrAddAnyBlock.restore();
            TextDataSourceView.prototype.adjustFileSystemConnectButton.restore();
            TextDataSourceView.prototype.adjustFtpServerConnectButton.restore();
        });
        it('TextDataSourceView: renderFilePropertiesSection() method should call certain set of functions', function () {
            stub.renderOrAddAnyBlock = sinon.stub(TextDataSourceView.prototype, 'renderOrAddAnyBlock');
            stub.adjustFieldDelimiterSection = sinon.stub(TextDataSourceView.prototype, 'adjustFieldDelimiterSection');
            stub.adjustRowDelimiterSection = sinon.stub(TextDataSourceView.prototype, 'adjustRowDelimiterSection');
            stub.adjustPreviewButton = sinon.stub(TextDataSourceView.prototype, 'adjustPreviewButton');
            textDataSourceView.renderFilePropertiesSection();
            expect(stub.renderOrAddAnyBlock).toHaveBeenCalled();
            expect(stub.adjustFieldDelimiterSection).toHaveBeenCalled();
            expect(stub.adjustRowDelimiterSection).toHaveBeenCalled();
            expect(stub.adjustPreviewButton).toHaveBeenCalled();
            TextDataSourceView.prototype.renderOrAddAnyBlock.restore();
            TextDataSourceView.prototype.adjustFieldDelimiterSection.restore();
            TextDataSourceView.prototype.adjustRowDelimiterSection.restore();
            TextDataSourceView.prototype.adjustPreviewButton.restore();
        });
        it('TextDataSourceView: _adjustButton() should disable button if state is false', function () {
            var state = false, savedFind;
            stub.attr = sinon.stub();
            savedFind = textDataSourceView.$el.find;
            textDataSourceView.$el.find = function () {
                return { attr: stub.attr };
            };
            textDataSourceView._adjustButton('', state);
            expect(stub.attr).toHaveBeenCalled();
            expect(stub.attr.getCall(0).args[0]).toBe('disabled');
            expect(stub.attr.getCall(0).args[1]).toBe('disabled');
            textDataSourceView.$el.find = savedFind;
        });
        it('TextDataSourceView: _adjustButton() should enable button if state is true', function () {
            var state = true, savedFind;
            stub.removeAttr = sinon.stub();
            savedFind = textDataSourceView.$el.find;
            textDataSourceView.$el.find = function () {
                return { removeAttr: stub.removeAttr };
            };
            textDataSourceView._adjustButton('', state);
            expect(stub.removeAttr).toHaveBeenCalled();
            expect(stub.removeAttr.getCall(0).args[0]).toBe('disabled');
            textDataSourceView.$el.find = savedFind;
        });
        it('TextDataSourceView: adjustFileSystemConnectButton() method should call _adjustButton method and check the model', function () {
            stub.isValid = sinon.stub(textDataSourceView.model, 'isValid');
            stub._adjustButton = sinon.stub(TextDataSourceView.prototype, '_adjustButton');
            textDataSourceView.adjustFileSystemConnectButton();
            expect(stub.isValid).toHaveBeenCalled();
            expect(stub._adjustButton).toHaveBeenCalled();
            textDataSourceView.model.isValid.restore();
            TextDataSourceView.prototype._adjustButton.restore();
        });
        it('TextDataSourceView: adjustPreviewButton() method should call _adjustButton method and check the model', function () {
            stub.isValid = sinon.stub(textDataSourceView.model, 'isValid');
            stub._adjustButton = sinon.stub(TextDataSourceView.prototype, '_adjustButton');
            textDataSourceView.adjustPreviewButton();
            expect(stub.isValid).toHaveBeenCalled();
            expect(stub._adjustButton).toHaveBeenCalled();
            textDataSourceView.model.isValid.restore();
            TextDataSourceView.prototype._adjustButton.restore();
        });
        it('TextDataSourceView: adjustFtpServerConnectButton() method should call _adjustButton method and check the model', function () {
            stub.isValid = sinon.stub(textDataSourceView.model, 'isValid');
            stub._adjustButton = sinon.stub(TextDataSourceView.prototype, '_adjustButton');
            textDataSourceView.adjustFtpServerConnectButton();
            expect(stub.isValid).toHaveBeenCalled();
            expect(stub._adjustButton).toHaveBeenCalled();
            textDataSourceView.model.isValid.restore();
            TextDataSourceView.prototype._adjustButton.restore();
        });
        it('TextDataSourceView: adjustFieldDelimiterSection() method should call _adjustSection method', function () {
            stub._adjustSection = sinon.stub(TextDataSourceView.prototype, '_adjustSection');
            textDataSourceView.adjustFieldDelimiterSection();
            expect(stub._adjustSection).toHaveBeenCalled();
            TextDataSourceView.prototype._adjustSection.restore();
        });
        it('TextDataSourceView: adjustRowDelimiterSection() method should call _adjustSection method', function () {
            stub._adjustSection = sinon.stub(TextDataSourceView.prototype, '_adjustSection');
            textDataSourceView.adjustRowDelimiterSection();
            expect(stub._adjustSection).toHaveBeenCalled();
            TextDataSourceView.prototype._adjustSection.restore();
        });
    });
});