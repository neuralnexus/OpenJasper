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
import Dialog from 'src/components/components.dialog';
import dialogs from 'src/components/components.dialogs';
import exportText from './test/templates/export.htm';
import standardConfirmTemplate from './test/templates/standardConfirm.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {jaspersoft} from 'src/namespace/namespace';

describe('Dialog', function () {
    var dialog;
    beforeEach(function () {
        sinon.stub(dialogs.popup, 'hide');
        sinon.stub(dialogs.popup, 'show');
        dialog = new Dialog({ templateId: 'exportsDialogTemplate' });
        setTemplates(exportText, '<div></div>');
    });
    afterEach(function () {
        dialogs.popup.hide.restore();
        dialogs.popup.show.restore();
        $('#exportDialog').remove();
    });
    it('should hide by click on cancel', function () {
        dialog.render($('#' + jasmine.getFixtures().containerId));
        $('#cancelExportButton').trigger('click');
        sinon.assert.calledWith(dialogs.popup.hide, dialog.el);
    });
    it('can be shown', function () {
        dialog.show();
        sinon.assert.calledWith(dialogs.popup.show, dialog.el);
    });
    it('can be hidden', function () {
        dialog.hide();
        sinon.assert.calledWith(dialogs.popup.hide, dialog.el);
    });
    describe('Rendering', function () {
        beforeEach(function () {
            dialog.render($('#' + jasmine.getFixtures().containerId));
        });
        it('chainable render', function () {
            expect(dialog.render()).toEqual(dialog);
        });
        it('should be rendered', function () {
            expect($('#exportDialog')).toExist();
            expect($('#cancelExportButton')).toExist();
            expect($('#dialogExportButton')).toExist();
        });
    });
});    // TODO : finish confirm dialog tests
// TODO : finish confirm dialog tests
describe('Confirm Dialog', function () {
    var dialog, dialogDomMock, messageText = 'confirm dialog message';
    beforeEach(function () {
        dialogDomMock = $(standardConfirmTemplate);
        sinon.stub(jaspersoft.components.templateEngine, 'getTemplateText').returns(dialogDomMock);
        dialog = new jaspersoft.components.ConfirmDialog({ el: dialogDomMock });
        sinon.stub(dialogs.popup, 'hide');
        sinon.stub(dialogs.popup, 'show');
        sinon.spy(dialog, 'render');
        jasmine.getFixtures().set('<div></div>');
    });
    afterEach(function () {
        dialog.render.restore();
        dialogs.popup.hide.restore();
        dialogs.popup.show.restore();
        jaspersoft.components.templateEngine.getTemplateText.restore();
        $('.standardConfirm').remove();
    });
    it('should hide by click on cancel', function () {
        sinon.stub(dialog, 'hide');
        dialog.render($('#' + jasmine.getFixtures().containerId));
        expect(dialog.$el).toContainElement('button.cancel');
        dialog.$('button.cancel').trigger('click');
        sinon.assert.called(dialog.hide);
    });
    it('can be shown', function () {
        dialog.show({});
        sinon.assert.calledWith(dialogs.popup.show, dialog.el);
        expect(dialog.render.calledOnce).toBe(true);
    });
    it('can be shown with default message', function () {
        dialog.show({});
        sinon.assert.calledWith(dialogs.popup.show, dialog.el);
        expect(dialog.messages).toEqual('');
        expect(dialog.render.calledOnce).toBe(true);
    });
    it('can be shown with overridden message', function () {
        dialog.show({ messages: ['message'] });
        sinon.assert.calledWith(dialogs.popup.show, dialog.el);
        expect(dialog.$el.find('.message')).toHaveText('message');
        expect(dialog.render.calledOnce).toBe(true);
    });
    it('can be hidden', function () {
        dialog.hide();
        sinon.assert.calledWith(dialogs.popup.hide, dialog.el);
    });
    describe('Rendering', function () {
        beforeEach(function () {
            dialog.render($('#' + jasmine.getFixtures().containerId));
        });
        it('chainable render', function () {
            expect(dialog.render()).toEqual(dialog);
        });
        it('should be rendered', function () {
            expect($('button.ok')).toExist();
            expect($('button.cancel')).toExist();
        });
    });
});