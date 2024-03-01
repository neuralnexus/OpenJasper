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
import ConfirmationDialog from 'src/common/component/dialog/ConfirmationDialog';
import Dialog from 'src/common/component/dialog/Dialog';
describe('Confirmation Dialog', function () {
    var sandbox;

    beforeEach(function () {
        sandbox = sinon.createSandbox();
    });

    afterEach(function () {
        sandbox.restore();
    });

    it('should call Dialog constructor', function () {
        var constructor = Dialog.prototype.constructor;
        var constructorStub = sandbox.stub(Dialog.prototype, 'constructor').callsFake(constructor);
        var dialog = new ConfirmationDialog({
            title: 'title',
            text: 'text'
        });
        expect(constructorStub).toHaveBeenCalled();
        dialog.remove();
    });
    it('should set confirmDialogTemplate when Dialog constructor is called', function () {
        var someTemplate = _.template('some template'),
            dialog = new ConfirmationDialog({confirmDialogTemplate: someTemplate});

        expect(dialog.confirmDialogTemplate).toBe(someTemplate);

        dialog.remove();
    });
    it('should call Dialog initialize', function () {
        var initialize = Dialog.prototype.initialize;
        var initStub = sandbox.stub(Dialog.prototype, 'initialize').callsFake(initialize);
        var dialog = new ConfirmationDialog({
            title: 'title',
            text: 'text'
        });
        expect(initStub).toHaveBeenCalled();
        dialog.remove();
    });
    it('should call \'close\' method on \'button:no\' event', function () {
        var closeStub = sandbox.stub(Dialog.prototype, 'close'), dialog = new ConfirmationDialog({
            title: 'title',
            text: 'text'
        });
        dialog.trigger('button:no');
        expect(closeStub).toHaveBeenCalled();
        dialog.remove();
    });
    it('should call \'close\' method on \'button:yes\' event', function () {
        var closeStub = sandbox.stub(Dialog.prototype, 'close'), dialog = new ConfirmationDialog({
            title: 'title',
            text: 'text'
        });
        dialog.trigger('button:yes');
        expect(closeStub).toHaveBeenCalled();
        dialog.remove();
    });
});