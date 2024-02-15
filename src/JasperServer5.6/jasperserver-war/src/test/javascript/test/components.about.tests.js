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
 * @version: $Id: components.about.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["components.about",
        "text!templates/aboutBox.htm",
        "text!templates/about.htm"],
    function(about, aboutBoxText, aboutText) {

        describe("AboutBox Component", function() {
            var aboutBoxDom, closeButton, aboutLink;

            beforeEach(function() {
                about.initialize();

                setTemplates(aboutBoxText, aboutText);

                aboutBoxDom = jQuery("#aboutBox");
                closeButton = jQuery("button");

                aboutLink = jQuery("#about");
            });

            it("should be defined", function() {
                expect(aboutBoxDom).toBeDefined();
            });

            it("should be hidden by default", function() {
                expect(aboutBoxDom).toBeHidden();
            });

            it("should be shown when show() method is called", function() {
                about.aboutBox.show();

                expect(aboutBoxDom).not.toBeHidden();
            });

            it("should be shown 'about' link clicked", function() {
                sinon.spy(about.aboutBox, "show");

                aboutLink.trigger("click");

                expect(about.aboutBox.show.called).toBeTruthy();

                about.aboutBox.show.restore();
            });

            it("should be hidden when user click close button", function() {
                about.aboutBox.show();

                expect(aboutBoxDom).not.toBeHidden();

                closeButton.trigger('click');

                expect(aboutBoxDom).toBeHidden();
            });
        });
    });

