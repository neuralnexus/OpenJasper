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
import _ from 'underscore';
import SimpleDomainView from 'src/dataSource/fileDataSource/SimpleDomainView';
import DomainSaveDialogView from 'src/dataSource/saveDialog/DomainSaveDialogView';
import DialogWithModelInputValidation from 'js-sdk/src/common/component/dialog/DialogWithModelInputValidation';

describe('Testing SimpleDomainView', function () {
    var simpleDomainView, fakeServer, stub = {};
    beforeEach(function () {
        fakeServer = sinon.fakeServer.create();
    });
    afterEach(function () {
        fakeServer.restore();
    });
    it('SimpleDomainView should be defined', function () {
        expect(SimpleDomainView).toBeDefined();
        expect(typeof SimpleDomainView).toEqual("function");
    });
    it('SimpleDomainView initialize method should call its parent', function () {
        stub.initialize = sinon.stub(DialogWithModelInputValidation.prototype, 'initialize');
        stub.listenTo = sinon.stub(DialogWithModelInputValidation.prototype, 'listenTo');
        simpleDomainView = new SimpleDomainView(_.extend({}, this.options, { dataSource: {} }));
        expect(stub.initialize).toHaveBeenCalled();
        DialogWithModelInputValidation.prototype.initialize.restore();
        DialogWithModelInputValidation.prototype.listenTo.restore();
    });
    describe('Testing SimpleDomainView', function () {
        it('_onSaveButtonClick start a next dialog', function () {
            simpleDomainView = new SimpleDomainView(_.extend({}, this.options, { dataSource: {} }));
            simpleDomainView._closeDialog = sinon.spy();
            var startDialogStub = sinon.stub(DomainSaveDialogView.prototype, 'startSaveDialog');
            var initializeTreeStub = sinon.stub(DomainSaveDialogView.prototype, 'initializeTree');
            simpleDomainView._onSaveButtonClick();
            expect(simpleDomainView._closeDialog).toHaveBeenCalled();
            expect(startDialogStub).toHaveBeenCalled();
            startDialogStub.restore();
            initializeTreeStub.restore();
        });
    });
});