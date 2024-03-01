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
import CustomDataSourceView from 'src/dataSource/view/CustomDataSourceView';
import TextDataSourceView from 'src/dataSource/fileDataSource/TextDataSourceView';

describe('Testing TextDataSourceView', function () {
    var textDataSourceView, fakeServer, stub = {};
    beforeEach(function () {
        fakeServer = sinon.fakeServer.create();
    });
    afterEach(function () {
        fakeServer.restore();
    });
    it('TextDataSourceView should be defined', function () {
        expect(TextDataSourceView).toBeDefined();
        expect(typeof TextDataSourceView).toEqual("function");
    });
    it('TextDataSourceView initialize method should call its parent', function () {
        stub.initialize = sinon.stub(CustomDataSourceView.prototype, 'initialize');
        stub.listenTo = sinon.stub(CustomDataSourceView.prototype, 'listenTo');
        textDataSourceView = new TextDataSourceView({}, { dataSourceType: true });
        expect(stub.initialize).toHaveBeenCalled();
        CustomDataSourceView.prototype.initialize.restore();
        CustomDataSourceView.prototype.listenTo.restore();
    });
    describe('Testing TextDataSourceView\'s work', function () {
        beforeEach(function () {
            textDataSourceView = new TextDataSourceView({}, { dataSourceType: true });
        });
        it('Checking what render method calls rendering of text data source section and clears the content of the $el', function () {
            textDataSourceView.$el = { empty: sinon.stub() };
            stub.renderTextDataSourceSection = sinon.stub(TextDataSourceView.prototype, 'renderTextDataSourceSection');
            textDataSourceView.render();
            expect(textDataSourceView.$el.empty).toHaveBeenCalled();
            expect(stub.renderTextDataSourceSection).toHaveBeenCalled();
            TextDataSourceView.prototype.renderTextDataSourceSection.restore();
        });
        it('Checking what templateData method calls templateData of upper class', function () {
            stub.templateData = sinon.stub(CustomDataSourceView.prototype, 'templateData');
            textDataSourceView.templateData();
            expect(stub.templateData).toHaveBeenCalled();
            CustomDataSourceView.prototype.templateData.restore();
        });
        it('Checking what renderFilePropertiesSection method calls rendering of subsequent sections', function () {
            textDataSourceView.$el = { append: sinon.stub() };
            stub.renderFileLocationSection = sinon.stub(TextDataSourceView.prototype, 'renderFileLocationSection');
            stub.renderFilePropertiesSection = sinon.stub(TextDataSourceView.prototype, 'renderFilePropertiesSection');
            textDataSourceView.renderTextDataSourceSection();
            expect(textDataSourceView.$el.append).toHaveBeenCalled();
            expect(stub.renderFileLocationSection).toHaveBeenCalled();
            expect(stub.renderFilePropertiesSection).toHaveBeenCalled();
            TextDataSourceView.prototype.renderFileLocationSection.restore();
            TextDataSourceView.prototype.renderFilePropertiesSection.restore();
        });
    });
});