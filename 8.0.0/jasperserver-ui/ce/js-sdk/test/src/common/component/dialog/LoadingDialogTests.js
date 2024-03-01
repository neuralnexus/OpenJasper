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
import LoadingDialog from 'src/common/component/dialog/LoadingDialog';
import Dialog from 'src/common/component/dialog/Dialog';
describe('Confirmation Dialog', function () {
    it('should call Dialog constructor', function () {
        var constructor = Dialog.prototype.constructor;
        var constructorSpy = sinon.stub(Dialog.prototype, 'constructor').callsFake(constructor)
        var dialog = new LoadingDialog();
        expect(constructorSpy).toHaveBeenCalled();
        dialog.remove();
        constructorSpy.restore();
    });
    it('should call Dialog initialize', function () {
        var initSpy = sinon.spy(Dialog.prototype, 'initialize'), dialog = new LoadingDialog();
        expect(initSpy).toHaveBeenCalled();
        dialog.remove();
        initSpy.restore();
    });
    it('should call \'close\' method on \'button:cancel\' event', function () {
        var closeStub = sinon.stub(Dialog.prototype, 'close'), dialog = new LoadingDialog({ cancellable: true });
        dialog.trigger('button:cancel');
        expect(closeStub).toHaveBeenCalled();
        closeStub.restore();
        dialog.remove();
    });
    it('should exporse progress setter', function () {
        var dialog = new LoadingDialog({ showProgress: true });
        expect(dialog.progress).toBeDefined();
        dialog.remove();
    });
});