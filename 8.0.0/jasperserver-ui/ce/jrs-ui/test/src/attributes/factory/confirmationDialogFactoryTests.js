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

import confirmationDialogFactory from 'src/attributes/factory/confirmationDialogFactory';
import confirmDialogTypesEnum from 'src/serverSettingsCommon/enum/confirmDialogTypesEnum';
import ConfirmationDialog from 'js-sdk/src/common/component/dialog/ConfirmationDialog';

describe('confirmationDialogFactory Tests', function () {
    it('should be a function', function () {
        expect(typeof confirmationDialogFactory).toEqual('function');
    });
    it('should have deleteConfirm', function () {
        var confirmDialog = confirmationDialogFactory(confirmDialogTypesEnum.DELETE_CONFIRM);
        expect(confirmDialog instanceof ConfirmationDialog).toBe(true);
    });
    it('should have nameConfirm', function () {
        var confirmDialog = confirmationDialogFactory(confirmDialogTypesEnum.NAME_CONFIRM);
        expect(confirmDialog instanceof ConfirmationDialog).toBe(true);
    });
    it('should be undefined if there no dialog of some type', function () {
        var confirmDialog = confirmationDialogFactory('someType');
        expect(confirmDialog).toBe(undefined);
    });
});