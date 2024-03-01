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
import jQuery from 'jquery';
import utils from 'src/components/components.utils';
import dialogs from 'src/components/components.dialogs';

describe("Component's Utils", function () {
    var elem, templateText, templateScriptContent,
        OBJECTS_COUNT = 10000;

    beforeEach(function () {
        elem = jQuery("<select id='orphan' multiple='multiple' >orphan</select>");
        templateText = "{{ for ( var i = 0; i < items.length; i++) { }}<option value='{{=items[i].value}}'>{{=items[i].label}}</option>{{ } }}";
        templateScriptContent =
            "<script id='sandbox' type='mustache'>" +
            templateText +
            "</script>";
    });

    it("check is element in a DOM", function () {
        expect(utils.isElementInDom(elem[0])).toBeFalsy();
        jasmine.getFixtures().set(elem[0].outerHTML);
        expect(jQuery('#orphan')).toExist();
    });

    it("create deferred for wait certain amount of milliseconds ", function () {
        var clock = sinon.useFakeTimers();

        var doSome = sinon.stub();

        utils.wait(1000).done(doSome);

        clock.tick(1200);

        expect(doSome).toHaveBeenCalled();

        clock.restore();
    });

    it("show loading dialog after " +utils.LOADING_DIALOG_DELAY, function () {
        sinon.spy(dialogs.popup, "show");
        sinon.spy(dialogs.popup, "hide");

        var clock = sinon.useFakeTimers();

        var deferred = new jQuery.Deferred();

        utils.showLoadingDialogOn(deferred);

        clock.tick(utils.LOADING_DIALOG_DELAY + 10);

        deferred.resolve();

        clock.tick(utils.LOADING_DIALOG_DELAY);

        expect(dialogs.popup.show).toHaveBeenCalled();
        expect(dialogs.popup.hide).toHaveBeenCalled();

        dialogs.popup.show.restore();
        dialogs.popup.hide.restore();

        clock.restore();
    });
});