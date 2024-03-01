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
import AuthorityModel from 'src/tenantImportExport/export/model/AuthorityPickerModel';
import AuthorityPickerView from 'src/tenantImportExport/export/view/AuthorityPickerView';
import mocks from 'src/tenantImportExport/export/model/data/authority.data';
import authorityPickerTmpl from 'src/tenantImportExport/export/view/test/template/export.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('Authority picker view', function () {
    var view, server, model;
    beforeEach(function () {
        setTemplates(authorityPickerTmpl);
        sinon.stub($, 'ajax').callsFake(() => $.Deferred());
        model = AuthorityModel.instance('rest_v2/roles');
        model.attributes.items = mocks.rolesRest.role;
        view = new AuthorityPickerView({
            model: model,
            customClass: 'selectedRoles',
            title: 'Select role',
            selectLabel: 'Select All'
        });
    });
    afterEach(function () {
        $.ajax.restore();
    });
    it('should initialize and do basic render', function () {
        expect(view.render().el).toBeTruthy();
        expect(view.render().subEl).toBeTruthy();
    });
    it('should be notified if server error occurs', function () {
        var callback = sinon.spy();
        view.on('error:server', callback);
        model.trigger('error:server');
        expect(callback.calledOnce).toBeTruthy();
    });
    it('should render options', function () {
        view.render();
        expect(view.$el.find('li').length).toEqual(0);
        model.trigger('change');
        expect(view.$el.find('li').length).toEqual(mocks.rolesRest.role.length);
    });
    it('should return list of selected elements', function () {
        view.render();
        model.trigger('change');
        $(view.$el.find('li')[3]).addClass('selected');
        expect(view.getSelected().length).toEqual(1);
    });
    it('should empty array if no selected elements', function () {
        view.render();
        model.trigger('change');
        expect(view.getSelected().length).toEqual(0);
    });
    it('should trigger event that selection was changed after render', function () {
        var callback = sinon.spy();
        view.on('change:selection', callback);
        view.render();
        model.trigger('change');
        expect(callback.calledOnce).toBeTruthy();
    });
    it('should trigger event is selection was changed', function () {
        var callback = sinon.spy();
        view.on('change:selection', callback);
        var stub = sinon.stub(view, 'getSelected').callsFake(function () {
            return [];
        });
        view.selectionFinished();
        stub.restore();
        expect(callback.calledOnce).toBeTruthy();
    });
    it('should select all', function () {
        view.render();
        model.trigger('change');
        view.selectAll();
        expect(view.getSelected().length).toEqual(mocks.rolesRest.role.length);
    });
    it('should clear all', function () {
        view.render();
        model.trigger('change');
        view.selectAll();
        view.selectNone();
        expect(view.getSelected().length).toEqual(0);
    });
    it('should select range', function () {
        view.render();
        model.trigger('change');
        view.selectRange(2, 3);
        var selected = view.getSelected();
        expect(selected.length).toEqual(2);
        expect(selected[0]).toEqual(mocks.rolesRest.role[2].name);
        expect(selected[1]).toEqual(mocks.rolesRest.role[3].name + '|' + mocks.rolesRest.role[3].tenantId);
    });
    it('should select item', function () {
        view.render();
        model.trigger('change');
        view.selectItem(0);
        var selected = view.getSelected();
        expect(selected.length).toEqual(1);
        expect(selected[0]).toEqual(mocks.rolesRest.role[0].name);
    });
    it('should deselect item', function () {
        view.render();
        model.trigger('change');
        view.selectItem(0);
        view.unSelectItem(0);
        var selected = view.getSelected();
        expect(selected.length).toEqual(0);
    });
});