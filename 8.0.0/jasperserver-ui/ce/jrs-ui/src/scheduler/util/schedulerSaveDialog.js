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

import $ from "jquery";
import i18n from "../../i18n/all.properties";
import dialogs from "../../components/components.dialogs";
import schedulerUtils from '../util/schedulerUtils';
import standardConfirmTemplate from 'js-sdk/src/common/templates/standardConfirm.htm';

export default {
    saveDialogBox: {
        SAVE_CONFIRMATION: {
            DIALOG_ID: 'saveConfirmation',
            OK_BUTTON_ID: 'saveConfirmationOK',
            CANCEL_BUTTON_ID: 'saveConfirmationCancel',
            IGNORE_BUTTON_ID: 'saveConfirmationIgnore'
        }
    },
    saveScheduleDialog: function (paramsMap) {
        let text = paramsMap.resourceType === "DashboardModelResource" ? i18n["dashboard.schedule.overlay.save.content"] : i18n["dashboard.schedule.overlay.save.content.report"],
            confirmDialog = $(standardConfirmTemplate);

        confirmDialog.attr('id', this.saveDialogBox.SAVE_CONFIRMATION.DIALOG_ID);
        confirmDialog.find('.title').text(i18n["dashboard.schedule.overlay.dialog"]);
        confirmDialog.find('.body').text(text);
        confirmDialog.find('.button.action.up.cancel').attr('id', this.saveDialogBox.SAVE_CONFIRMATION.CANCEL_BUTTON_ID);
        confirmDialog.find('.button.action.primary.up').attr('id', this.saveDialogBox.SAVE_CONFIRMATION.OK_BUTTON_ID);
        confirmDialog.find('.button.action.up.ignore').attr('id', this.saveDialogBox.SAVE_CONFIRMATION.IGNORE_BUTTON_ID);
        confirmDialog.find('.button.action.primary.up .wrap').text(i18n["dashboard.schedule.overlay.save"]);
        confirmDialog.find('.button.action.up.cancel .wrap').text(i18n["dashboard.schedule.overlay.cancel"]);
        confirmDialog.find('.button.action.up.ignore .wrap').text(i18n["dashboard.schedule.overlay.ignore"]);
        confirmDialog.appendTo($('#frame .content:eq(0)'));
    },
    saveDialogConfirmationBox: function () {
        let saveDialog = $('#' + this.saveDialogBox.SAVE_CONFIRMATION.DIALOG_ID);
        dialogs.popupConfirm.show(saveDialog[0], false, {
            okButtonSelector: '#' + this.saveDialogBox.SAVE_CONFIRMATION.OK_BUTTON_ID,
            cancelButtonSelector: '#' + this.saveDialogBox.SAVE_CONFIRMATION.CANCEL_BUTTON_ID,
            ignoreButtonSelector: '#' + this.saveDialogBox.SAVE_CONFIRMATION.IGNORE_BUTTON_ID
        }).done();
    },
    scheduleDialogBox: function(previousState,paramsMap,save){
        if (previousState) {
            !$('#saveConfirmation').length && this.saveScheduleDialog(paramsMap);
            this.saveDialogConfirmationBox();
            let saveDialogBox = $('#saveConfirmation'),
                cancelSave = $('#saveConfirmationCancel'),
                ignoreSave = $('#saveConfirmationIgnore'),
                saveContent = $('#saveConfirmationOK'),
                scheduleBtn = $('#schedule');

            ignoreSave.removeClass('hidden');
            saveDialogBox.show();
            cancelSave.on('click', (e) => {
                e.stopImmediatePropagation();
                scheduleBtn.hasClass('over') && scheduleBtn.removeClass('over');
                saveDialogBox.remove();
            });
            saveContent.on('click', (e) => {
                e.stopImmediatePropagation();
                save();
                paramsMap && schedulerUtils._ParamMapping(paramsMap);
                saveDialogBox.hide();
                ignoreSave.addClass('hidden');
            });
            ignoreSave.on('click', (e)=> {
                e.stopImmediatePropagation();
                paramsMap && schedulerUtils._ParamMapping(paramsMap);
                saveDialogBox.remove();
            });
        } else {
            paramsMap && schedulerUtils._ParamMapping(paramsMap);
        }
    }
}