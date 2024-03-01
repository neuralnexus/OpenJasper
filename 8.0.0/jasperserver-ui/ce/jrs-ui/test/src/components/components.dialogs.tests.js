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
import dialogs from 'src/components/components.dialogs';
import standardAlertText from './test/templates/standardAlert.htm';
import systemConfirmText from './test/templates/systemConfirm.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

var clock;
describe('dialogs', function () {
    var message = 'Test <,& and >', escapedMessage = 'Test &lt;,&amp; and &gt;';
    beforeEach(function () {
        setTemplates(standardAlertText, systemConfirmText);
        clock = sinon.useFakeTimers();
    });
    afterEach(function () {
        clock.restore();
    });

    it('tests are commented out in this module', function() {
        expect(true).toBeTruthy();
    });

    /* TEMPORARY HACK
            describe("system confirmation", function() {

                var systemConfirm = dialogs.systemConfirm, delay = 1000, $ = jQuery;

                beforeEach(function(){
                    sinon.spy(systemConfirm, "hide");
                });

                afterEach(function(){
                    systemConfirm.hide.restore();
                });

                it("should show message", function() {
                    systemConfirm.show(message);

                    var confirm = $("#systemMessageConsole");
                    var actualMessage = confirm.text();

                    expect(confirm).not.toBeHidden();
                    expect(actualMessage.indexOf(message)).toBeGreaterThan(-1);
                });

                it("should hide after default timeout (2s)", function() {
                    systemConfirm.show(message);

                    clock.tick(2000);

                    expect(systemConfirm.hide.calledOnce).toBeTruthy();

                });

                it("should hide after custom timeout (1s)", function() {
                    systemConfirm.show(message, delay);

                    clock.tick(delay);

                    expect(systemConfirm.hide.calledOnce).toBeTruthy();
                });

                it("should show 'test' and  hide immediately after mouse click", function() {
                    var confirm = $("#systemMessageConsole");
                    systemConfirm.show(message);

                    $(confirm).click();

                    expect(confirm).toBeHidden();
                });

            });

            describe("error notification", function() {
                var response = '<html><div id="errorPageContent">' + message + '</div></html>';

                it("should display error dialog", function() {
                    expect(jQuery('#' + dialogs.errorPopup._DOM_ID)).toBeHidden();
                    dialogs.errorPopup.show(response);
                    expect(jQuery('#' + dialogs.errorPopup._DOM_ID)).not.toBeHidden();

                    dialogs.errorPopup._hide();
                });

                it("should display error dialog and search error message in html response data", function() {
                    expect(jQuery('#' + dialogs.errorPopup._DOM_ID)).toBeHidden();

                    dialogs.errorPopup.show(response);

                    expect(jQuery('#' + dialogs.errorPopup._DOM_ID)).not.toBeHidden();

                    var messageParagraph = jQuery('#' + dialogs.errorPopup._CONTENT_ID);
                    expect(messageParagraph.text().strip()).toEqual(message);

                    dialogs.errorPopup._hide();
                });

                it("should display error dialog for ordinary strings", function() {
                    expect(jQuery('#' + dialogs.errorPopup._DOM_ID)).toBeHidden();

                    dialogs.errorPopup.show(message);

                    expect(jQuery('#' + dialogs.errorPopup._DOM_ID)).not.toBeHidden();

                    var messageParagraph = jQuery('#' + dialogs.errorPopup._CONTENT_ID + ' .message');
                    expect(messageParagraph.text().strip()).toEqual(message);
                    expect(messageParagraph[0].innerHTML.strip()).toEqual(escapedMessage);

                    dialogs.errorPopup._hide();
                });

                it("should close on button click", function() {
                    dialogs.errorPopup.show(message);

                    jQuery('#' + dialogs.errorPopup._DOM_ID + ' button').trigger('click');

                    expect(jQuery('#' + dialogs.errorPopup._DOM_ID)).toBeHidden();
                });
            });

            describe("popup", function() {

                it("should show page dimmer if needed", function() {
                    spyOn(pageDimmer, 'show');

                    dialogs.popup.show(dialogs.errorPopup._DOM_ID, true);

                    expect(pageDimmer.show).toHaveBeenCalled();
                });

                it("should be movable", function() {
                    spyOn(layoutModule, 'createMover');

                    dialogs.popup.show(dialogs.errorPopup._DOM_ID);

                    expect(layoutModule.createMover).toHaveBeenCalledWith(jQuery('#' + dialogs.errorPopup._DOM_ID)[0]);
                });

                it("should be sizable", function() {
                    spyOn(layoutModule, 'createSizer');

                    dialogs.popup.show(dialogs.errorPopup._DOM_ID);

                    expect(layoutModule.createSizer).toHaveBeenCalledWith(jQuery('#' + dialogs.errorPopup._DOM_ID)[0]);
                });

                it("should obtain highest z-index after click", function() {
                    var dialog = jQuery('#' + dialogs.errorPopup._DOM_ID);
                    var findMaxZIndex = function(elem) {
                        var max = (elem.style && elem.style.zIndex) ? elem.style.zIndex : 0;
                        if (elem.childNodes && elem.childNodes.length) {
                            for (var i = 0; i < elem.childNodes.length; i++) {
                                max = Math.max(max, findMaxZIndex(elem.childNodes[i]));
                            }
                        }
                        return max;
                    };

                    dialog.css('zIndex', '-1');

                    dialogs.errorPopup.show(message);

                    dialog.trigger('click');

                    expect(+dialog[0].style.zIndex).toEqual(findMaxZIndex(document.body));

                });
            });
            */
});