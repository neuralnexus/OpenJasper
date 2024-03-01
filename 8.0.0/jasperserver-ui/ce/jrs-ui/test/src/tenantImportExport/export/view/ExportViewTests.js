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
import FormModel from 'src/tenantImportExport/export/model/ExportModel';
import ExtendedFormView from 'src/tenantImportExport/export/view/ExportView';
import exportText from 'src/tenantImportExport/export/view/test/template/export.htm';
import exportTypesEnum from 'src/tenantImportExport/export/enum/exportTypesEnum';
import setTemplates from 'js-sdk/test/tools/setTemplates';

import awsSettings from 'src/settings/awsSettings.settings';

describe('Export\'s ExtendedFormView', function () {
    var view, getCustomKeysStub;
    beforeEach(function () {
        view && view.remove();
        sinon.stub($, 'ajax').callsFake(() => $.Deferred());
        setTemplates(exportText, '<div id=\'exportControls\'></div>');
        view = new ExtendedFormView({ model: new FormModel() });
        getCustomKeysStub = sinon.stub(view.customKeyStateModel , 'getCustomKeys').callsFake(function () {
            return $.Deferred().resolve([{alias : 'key1'}]);
        });
    });
    afterEach(function () {
        view.remove();
        $.ajax.restore();
        getCustomKeysStub.restore();
    });
    it('chainable render', function () {


        expect(view.render({ type: exportTypesEnum.SERVER_PRO })).toEqual(view);
    });
    describe('Rendering', function () {
        const renderExportView = () => {
            $('#exportControls').append(view.render({ type: exportTypesEnum.SERVER_PRO }).el);
        };

        beforeEach(function () {
            renderExportView();
        });

        it('render export filename group', function () {
            var $fileName = $('.fileName');
            expect($fileName).toExist();
            expect($fileName).toHaveValue('export.zip');
        });

        describe('should render encryption hint', () => {

            it('when jrs is not on aws', () => {
                const hint = $.trim($('.jr-jEncryptionHint').text());

                expect(hint).toEqual('encryption hint');
            });

            it('when product type is JrsAmi', () => {
                view.remove();

                awsSettings.productTypeIsJrsAmi = true;

                renderExportView();

                const hint = $.trim($('.jr-jEncryptionHint').text());

                expect(hint).toEqual('encryption hint aws');

                awsSettings.productTypeIsJrsAmi = false;
            });

            it('when product type is MpAmi', () => {
                view.remove();

                awsSettings.productTypeIsMpAmi = true;

                renderExportView();

                const hint = $.trim($('.jr-jEncryptionHint').text());

                expect(hint).toEqual('encryption hint aws');

                awsSettings.productTypeIsMpAmi = false;
            });
        });

        it('render export options', function () {
            var $sectionEverything = $('.section.everything'), $sectionFileName = $('.section.file-name'), $sectionResources = $('.section.resources'), $sectionAssets = $('.section.assets'), $sectionRolesUsers = $('.section.roles-users'), $sectionEvents = $('.section.events');
            expect($sectionEverything).toExist();
            expect($sectionFileName).toExist();
            expect($sectionRolesUsers).toExist();
            expect($sectionResources).toExist();
            expect($sectionAssets).toExist();
            expect($sectionEvents).toExist();
            expect($sectionEverything.find('.control').length).toEqual(1);
            expect($sectionFileName.find('.control').length).toEqual(1);
            expect($sectionRolesUsers.find('.control.radio').length).toEqual(3);
            expect($sectionResources.find('.control').length).toEqual(7);
            expect($sectionAssets.find('.control').length).toEqual(6);
            expect($sectionEvents.find('.control').length).toEqual(3);
            expect($sectionEverything.find('input.everything')).toBeChecked();
            expect($sectionResources.find('input.includeReports')).toBeDisabled();
            expect($sectionResources.find('input.includeDomains')).toBeDisabled();
            expect($sectionResources.find('input.includeOtherResourceFiles')).toBeDisabled();
            expect($sectionResources.find('input.includeScheduledReportJobs')).toBeDisabled();
            expect($sectionResources.find('input.includeAdHocViews')).toBeDisabled();
            expect($sectionResources.find('input.includeDashboards')).toBeDisabled();
            expect($sectionResources.find('input.includeDataSources')).toBeDisabled();
            expect($sectionAssets.find('input.includeDependentObjects')).toBeDisabled();
            expect($sectionAssets.find('input.includeAttributes')).toBeDisabled();
            expect($sectionAssets.find('input.includeAttributeValues')).toBeDisabled();
            expect($sectionAssets.find('input.includeRepositoryPermissions')).toBeDisabled();
            expect($sectionAssets.find('input.includeSubOrganizations')).toBeDisabled();
            expect($sectionEvents.find('input.includeAccessEvents')).not.toBeChecked();
            expect($sectionEvents.find('input.includeAuditEvents')).not.toBeChecked();
            expect($sectionEvents.find('input.includeMonitoringEvents')).not.toBeChecked();
            expect($sectionRolesUsers.find('input.selectedUsersRoles')).toBeDisabled();
            expect($sectionRolesUsers.find('input.usersWithSelectedRoles')).toBeDisabled();
            expect($sectionRolesUsers.find('input.rolesWithSelectedUsers')).toBeDisabled();
        });

        it('sets disabled state properly', function () {
            view.model.set('everything', true);
            view.changeEnabledState();
            expect(view.$el.find('.selectedRoles .disabled')).toExist();
            expect(view.$el.find('.selectedUsers .disabled')).toExist();
            expect(view.$el.find('.usersWithSelectedRoles')).toBeDisabled();
            expect(view.$el.find('.includeAccessEvents')).not.toBeDisabled();
            view.model.set('everything', false);
            view.changeEnabledState();
            expect(view.$el.find('.selectedRoles')).not.toBeDisabled();
            expect(view.$el.find('.selectedUsers')).not.toBeDisabled();
            expect(view.$el.find('.usersWithSelectedRoles')).not.toBeDisabled();
            expect(view.$el.find('.includeAccessEvents')).toBeDisabled();
        });

        describe('Wiring with model by dom events', function () {
            var formModel, formSaveStub;
            beforeEach(function () {
                view && view.remove();
                view = new ExtendedFormView({ model: new FormModel(), keyFileDialog : {} });
                getCustomKeysStub = sinon.stub(view.customKeyStateModel , 'getCustomKeys').callsFake(function () {
                    return $.Deferred().resolve([{alias : 'key1'}]);
                });
                formModel = view.model;
                formSaveStub = sinon.stub(formModel, 'save');
                $('#exportControls').append(view.render({ type: exportTypesEnum.SERVER_PRO }).el);
            });
            afterEach(function () {
                view.remove();
                formSaveStub.restore();
            });
            it('can export everything', function () {
                var exportEverything = $('.section.everything input.everything');
                $(exportEverything).prop('checked', false);
                $(exportEverything).trigger('change');
                expect(formModel.get('everything')).toBeFalsy();
            });
            it('can change disabled state on change of export everything', function () {
                sinon.spy(view.rolesList, 'setDisabled');
                sinon.spy(view.usersList, 'setDisabled');
                sinon.spy(view.rolesList, 'selectNone');
                sinon.spy(view.usersList, 'selectNone');
                var exportEverything = $('.section.everything input.everything').first();
                exportEverything.prop('checked', false);
                exportEverything.trigger('change');
                exportEverything.prop('checked', true);
                exportEverything.trigger('change');
                expect(view.rolesList.setDisabled).toHaveBeenCalledWith(false);
                expect(view.usersList.setDisabled).toHaveBeenCalledWith(false);
                expect(view.rolesList.setDisabled).toHaveBeenCalledWith(true);
                expect(view.usersList.setDisabled).toHaveBeenCalledWith(true);
                expect(view.rolesList.selectNone).toHaveBeenCalled();
                expect(view.usersList.selectNone).toHaveBeenCalled();
                view.rolesList.setDisabled.restore();
                view.usersList.setDisabled.restore();
                view.rolesList.selectNone.restore();
                view.usersList.selectNone.restore();
            });
            it('can clear pickers if everything selected', function () {
                var users = sinon.spy(view.usersList, 'selectNone');
                var roles = sinon.spy(view.rolesList, 'selectNone');
                view.model.set('everything', true, { silent: true });
                view.changeEnabledState();
                expect(users.calledOnce).toBeTruthy();
                expect(roles.calledOnce).toBeTruthy();
                users.restore();
                roles.restore();
            });
            it('should not clear pickers if everything deselected', function () {
                var users = sinon.spy(view.usersList, 'selectNone');
                var roles = sinon.spy(view.rolesList, 'selectNone');
                view.model.set('everything', false);
                view.changeEnabledState();
                expect(users.called).toBeFalsy();
                expect(roles.called).toBeFalsy();
                users.restore();
                roles.restore();
            });
            it('can include roles by users', function () {
                var rolesByUsers = $('#exportOptions input[type=\'checkbox\']:eq(1)');
                $(rolesByUsers).prop('disabled', false);
                $(rolesByUsers).prop('checked', false);
                $(rolesByUsers).trigger('change');
                expect(formModel.get('userForRoles')).toBeFalsy();
            });
        });
        describe('Authority pickers', function () {
            var users = ['aaa'];
            var roles = [
                'bbb',
                'ccc'
            ];
            it('should be', function () {
                expect(view.rolesList).toBeTruthy();
                expect(view.usersList).toBeTruthy();
            });
            it('should be bound with model', function () {
                view.bindWithRoles(roles);
                view.bindWithUsers(users);
                expect(view.model.get('users')).toEqual(users);
                expect(view.model.get('roles')).toEqual(roles);
            });
            it('should be aware of component\'s events', function () {
                view.bindWithRoles = sinon.spy();
                view.bindWithUsers = sinon.spy();
                view.rolesList.trigger('change:selection', roles);
                view.usersList.trigger('change:selection', users);
                expect(view.bindWithRoles.calledWith(roles));
                expect(view.bindWithUsers.calledWith(users));
            });
        });
    });
});