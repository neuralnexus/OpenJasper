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
 * @version: $Id: components.ajaxdownloader.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "components.ajaxdownloader"],
    function ($, _, AjaxDownloader) {

    xdescribe("Downloader", function(){

        var downloader;

        beforeEach(function(){
           jasmine.getFixtures().set("<div id='mysandbox'></div>");
           downloader = new AjaxDownloader();
        });

        it("chainable renderer", function(){
            expect(downloader.render()).toEqual(downloader);
        });

        it("should render hidden iframe with uuid", function(){
            $("#mysandbox").append(downloader.render().el);
            expect($("#mysandbox iframe")).toExist();
            expect($("#mysandbox iframe")).toHaveAttr("data-downloader-id", downloader.cid);
            expect($("#mysandbox iframe")).toBeHidden();
        });

        it("should render to body by default", function(){
            downloader.defaultLayout();
            var downloadFrame = $("body [data-downloader-id='"+downloader.cid+"']");
            expect(downloadFrame).toExist();
            downloadFrame.remove();
        });

        it("should target iframe to url by starting downloading", function(){
            downloader.start("test/url/blabla");
            expect($("[data-downloader-id='"+downloader.cid+"']")).toHaveAttr("src", "test/url/blabla");
        });

        describe("Initialization", function(){

            var defaultLayoutStub;

            beforeEach(function(){
                defaultLayoutStub = sinon.stub(AjaxDownloader.prototype, "defaultLayout");
            });

            it("should layout by initialization", function(){
                new AjaxDownloader;
                expect(defaultLayoutStub).toHaveBeenCalled();
            });

            afterEach(function(){
                defaultLayoutStub.restore();
            });
        });

    });

});
