/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: repository.search.components.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define([
    "jquery",
    "text!templates/list.htm",
    "text!templates/tree.htm",
    "text!templates/generateResource.htm",
    "repository.search.components"
], function (jQuery) {

    function isProVersion() {
        return false;
    }

    var dialogs = {popup:{show:function () {
    }, hide:function () {
    }}};

//dummy
    if (!Droppables) {
        var Droppables = {
            add:function () {
            }
        };
    }

    if (!Draggables) {
        var Draggables = {
            add:function () {
            }
        };
    }


    if (!TouchController) {
        var TouchController = {
            element_scrolled:false
        };
    }

    var localContext = {};

    localContext.rsInitOptions = {
        organizationId:"/organizations",
        publicFolderUri:"/public"
    };

    var resource = {
        label:"Resource",
        description:"Resource description"
    };

//TODO clean it out, GenerateResource was made obsolete by new create report workflow
    describe("GenerateResource Dialog", function () {

        beforeEach(function () {
            loadTemplates("list.htm", "tree.htm", 'generateResource.htm');
        });

        it('should be shown', function () {
            var dialog = new GenerateResource(resource, {});
            expect(dialog).not.toBeNull();

            spyOn(dialogs.popup, 'show');

            dialog.show();

            expect(dialogs.popup.show).toHaveBeenCalled();

        });

        it('should fail to show dialog', function () {
            var e;

            if (isIE()) {
                e = new Error("Cannot read property 'okCallback' of undefined");
            } else if (isFirefox()) {
                e = new TypeError("options is undefined");
            } else {
                e = new Error("Cannot read property 'okCallback' of undefined");
            }

            expect(function () {
                new GenerateResource();
            }).toThrow(e);
        });

        it('should have updated title', function () {
            var dialog = new GenerateResource(resource, {});

            dialog.show();

            var title = jQuery.trim(jQuery('#generateResource .title').text());
            expect(title).toEqual('Generate Report from: ' + resource.label);
        });

        it('should have default name and description', function () {
            var dialog = new GenerateResource(resource, {});
            dialog.show();

            expect(jQuery("#generateResourceInputName").val()).toEqual(resource.label);
            expect(jQuery("#generateResourceInputDescription").val()).toEqual(resource.description);
        });

        it('should call callback on OK button', function () {
            window.theCallback = function () {
            };
            var dialog = new GenerateResource(resource, {okCallback:theCallback});

            dialog._enableSave(true);
            dialog.show();

            spyOn(window, 'theCallback');

            jQuery("#generateResourceBtnSave").simulate('click', {bubbles:true});

            expect(window.theCallback).toHaveBeenCalledWith({});
        });

        it('should be hidden', function () {
            var dialog = new GenerateResource(resource, {});
            expect(dialog).not.toBeNull();

            dialog.show();

            spyOn(dialogs.popup, 'hide');

            jQuery("#generateResourceBtnCancel").simulate('click');

            expect(dialogs.popup.hide).toHaveBeenCalled();
        });
    });

});
