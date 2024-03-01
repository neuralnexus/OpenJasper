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
import DesignerRowView from 'src/attributes/view/DesignerRowView';
import modelWithPermissionTrait from 'src/attributes/model/modelWithPermissionTrait';
import AttributeModel from 'src/attributes/model/AttributeModel';
import DeleteConfirmBehavior from 'src/serverSettingsCommon/behaviors/DeleteConfirmBehavior';
import confirmDialogTypesEnum from 'src/serverSettingsCommon/enum/confirmDialogTypesEnum';

var AttributeModelExtended = AttributeModel.extend(modelWithPermissionTrait);

describe('DesignerRowView Tests', function () {
    var rowView;
    var SECURE_VALUE_SUBSTITUTION = '~secure~';
    var ConfirmationDialog = Backbone.View.extend({
        setContent: function () {
        },
        open: function () {
        }
    });
    beforeEach(function () {
        rowView = new DesignerRowView({
            model: new AttributeModelExtended(),
            collection: new Backbone.Collection(),
            confirmationDialogs: {
                'saveConfirm': new ConfirmationDialog(),
                'deleteConfirm': new ConfirmationDialog()
            }
        });
    });
    afterEach(function () {
        rowView && rowView.remove();
    });
    it('should be properly initialized', function () {
        expect(rowView.editMode).toBeDefined();
    });
    it('should toggle to edit mode', function () {
        var renderSpy = sinon.spy(rowView, 'render');
        var getTemplateSpy = sinon.spy(rowView, 'getTemplate');
        expect(rowView.editMode).toBe(false);
        rowView.toggleMode();
        expect(rowView.editMode).toBe(true);
        expect(getTemplateSpy).toHaveBeenCalled();
        getTemplateSpy.restore();
        renderSpy.restore();
    });
    it('should trigger validate event', function () {
        var triggerSpy = sinon.spy(rowView, 'trigger');
        rowView.model.set({
            'value': 'someValidValue',
            'name': 'someValidName',
            'description': 'someDescription'
        });
        rowView.runValidation();
        expect(triggerSpy).toHaveBeenCalledWith('validate');
        triggerSpy.restore();
    });
    it('should trigger delete event', function () {
        var triggerSpy = sinon.spy(rowView, 'trigger');
        DeleteConfirmBehavior.prototype._onDeleteClick.call({view: rowView});
        expect(triggerSpy).toHaveBeenCalledWith('open:confirm', confirmDialogTypesEnum.DELETE_CONFIRM, rowView, rowView.model);
        triggerSpy.restore();
    });
    it('should cancel editing', function () {
        var getStateSpy = sinon.spy(rowView.model, 'getState');
        var toggleActiveSpy = sinon.spy(rowView, 'toggleActive');
        rowView.model.set({
            'value': 'someValidValue',
            'name': 'someValidName',
            'description': 'someDescription',
            'id': 1
        });
        rowView.cancel();
        expect(getStateSpy).toHaveBeenCalled();
        expect(toggleActiveSpy).toHaveBeenCalled();
        toggleActiveSpy.restore();
        getStateSpy.restore();
    });
    it('should change encrypt properly', function () {
        rowView.model.set({
            'value': 'someValidValue',
            'name': 'someValidName',
            'description': 'someDescription',
            'id': 1,
            'secure': true
        });
        rowView.toggleMode();
        var input = rowView.$el.find('.value.editMode input');
        expect(input.val()).toBe(SECURE_VALUE_SUBSTITUTION);
        input.val('someOtherValue');
        input.trigger('click');
        expect(input.val()).toBe('someOtherValue');
        input.trigger('blur');
        expect(input.val()).toBe(SECURE_VALUE_SUBSTITUTION);
    });
});