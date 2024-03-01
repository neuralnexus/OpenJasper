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

/**
 * @author dlitvak
 */

import $ from "jquery";
import jrsConfigs from 'src/jrs.configs';

describe("jQuery_xss_escape", function() {

    it("should not escape 'div' html string passed to $().  Div element should be returned.", function() {
        var testStr = '<div>Hello</div>';
        expect($(testStr)[0] instanceof HTMLDivElement).toEqual(true);
    });

    it("should escape 'script' html string passed to $(). Text should be returned.", function() {
        var testStr = '<script>alert(1)</script>';
        expect($(testStr)[0] instanceof Text).toEqual(true);
    });

    it("should escape 'script' html string inserted via $(el).html().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<script>alert(1)</script>';
        divEl.html(testStr);
        expect(divEl[0].childNodes[0] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[0].data).toEqual(testStr);
    });

    it("should NOT escape 'script' html string inserted via $(el).html() when a nonce is present.", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<script>alert(1)</script><js-templateNonce></js-templateNonce>'.replace('<js-templateNonce></js-templateNonce>', jrsConfigs.xssNonce);
        divEl.html(testStr);
        expect(divEl[0].childNodes[0] instanceof HTMLScriptElement).toEqual(true);
    });

    it("should escape 'div' html string inserted via $(el).html().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<div>Div2</div>';
        divEl.html(testStr);
        expect(divEl[0].childNodes[0] instanceof HTMLDivElement).toEqual(true);
    });

    it("should escape 'onerror' img attribute in html().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<img src="x" onerror="alert(2)" test="alert(2)">';
        divEl.html(testStr);

        expect(divEl[0].childNodes[0].getAttribute('test')).toEqual("alert(2)");
        expect(divEl[0].childNodes[0].getAttribute('onerror')).toEqual(null);
    });

    it("should escape 'onerror' img attribute in parseHTML().", function() {
        var testEl = $.parseHTML('<img src="x" onerror="alert(2)" test="alert(2)">');

        expect(testEl[0] instanceof HTMLImageElement).toEqual(true);
        expect(testEl[0].getAttribute('test')).toEqual("alert(2)");
        expect(testEl[0].getAttribute('onerror')).toEqual(null);
    });

    it("should remove 'javascript:' in an attribute inserted via $(el).html().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<img src="javascript:x">';
        divEl.html(testStr);

        expect(divEl[0].childNodes[0].getAttribute('src')).toEqual("x");
    });

    it("should NOT remove 'javascript:' in an attribute inserted via $(el).html() when a nonce is present.", function() {
        var divEl = $('<div>Hello</div>');
        var imgSrcStr = "javascript:x";  // jshint ignore: line
        var testStr = '<img src="@src@"><js-templateNonce></js-templateNonce>'
            .replace('@src@', imgSrcStr)
            .replace('<js-templateNonce></js-templateNonce>', jrsConfigs.xssNonce);
        divEl.html(testStr);

        expect(divEl[0].childNodes[0].getAttribute('src')).toEqual(imgSrcStr);
    });

    it("should remove encoded 'javascript:' in an attribute inserted via $(el).html().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<img src="java&#115;Crip&#84;:x">';
        divEl.html(testStr);

        expect(divEl[0].childNodes[0].getAttribute('src')).toEqual("x");
    });

    it("should append 'div' element via $(el).append().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<div>Hi</div>';

        divEl.append(testStr);

        expect(divEl[0].childNodes.length).toEqual(2);
        expect(divEl[0].childNodes[0] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[1] instanceof HTMLDivElement).toEqual(true);
    });

    it("should escape 'script' element inserted via $(el).append().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<script>alert(1)</script>';

        divEl.append(testStr);

        expect(divEl[0].childNodes.length).toEqual(2);
        expect(divEl[0].childNodes[0] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[1] instanceof Text).toEqual(true);
    });

    it("should prepend 'div' element via $(el).prepend().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<div>Hi</div>';

        divEl.prepend(testStr);

        expect(divEl[0].childNodes.length).toEqual(2);
        expect(divEl[0].childNodes[1] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[0] instanceof HTMLDivElement).toEqual(true);
    });

    it("should escape 'script' element prepended via $(el).prepend().", function() {
        var divEl = $('<div>Hello</div>');
        var testStr = '<script>alert(1)</script>';

        divEl.prepend(testStr);

        expect(divEl[0].childNodes.length).toEqual(2);
        expect(divEl[0].childNodes[0] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[0].data).toEqual(testStr);
        expect(divEl[0].childNodes[1] instanceof Text).toEqual(true);
    });

    it("should escape 'script' element prepended via $(el).before().", function() {
        var divEl = $('<div>Hello<div id="hi">Hi</div></div>');
        var testStr = '<script>alert(1)</script>';

        divEl.find("#hi").before(testStr);

        expect(divEl[0].childNodes.length).toEqual(3);
        expect(divEl[0].childNodes[0] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[1] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[1].data).toEqual(testStr);
        expect(divEl[0].childNodes[2] instanceof HTMLDivElement).toEqual(true);
    });

    it("should escape 'script' element appended via $(el).after().", function() {
        var divEl = $('<div>Hello<div id="hi">Hi</div></div>');
        var testStr = '<script>alert(1)</script>';

        divEl.find("#hi").after(testStr);

        expect(divEl[0].childNodes.length).toEqual(3);
        expect(divEl[0].childNodes[0] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[1] instanceof HTMLDivElement).toEqual(true);
        expect(divEl[0].childNodes[2] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[2].data).toEqual(testStr);
    });

    it("should escape 'script' element appended via $(el).replaceWith().", function() {
        var divEl = $('<div>Hello<div id="hi">Hi</div></div>');
        var testStr = '<script>alert(1)</script>';

        divEl.find("#hi").replaceWith(testStr);

        expect(divEl[0].childNodes.length).toEqual(2);
        expect(divEl[0].childNodes[0] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[1] instanceof Text).toEqual(true);
        expect(divEl[0].childNodes[1].data).toEqual(testStr);
        expect(divEl.find("#hi").length).toEqual(0);
    });
});