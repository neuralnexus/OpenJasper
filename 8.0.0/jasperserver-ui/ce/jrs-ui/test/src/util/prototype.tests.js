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
 *
 * Note: Only <img> won't be escaped by xssUtil due to jrsConfigsMock.js prop xssHtmlTagWhiteList: 'img'
 * In the browser/prod, xssHtmlTagWhiteList is configured via security-config.properties
 *
 * @author dlitvak
 */

import jrsConfigs from "js-sdk/src/jrs.configs";

describe("prototypejs_xss_escape", function() {

    it("should escape 'onerror' attribute inserted via $(el).insert().", function() {
        var divEl = new Element('div');
        var testStr = '<img onerror="(function() {})()" alt="x">';
        divEl.insert(testStr);

        expect(divEl.childNodes[0] instanceof HTMLImageElement).toEqual(true);
        expect(divEl.childNodes[0].getAttribute("alt")).toEqual('x');
        expect(divEl.childNodes[0].getAttribute("onerror")).toEqual(null);
    });

    it("should NOT escape 'onerror' attribute inserted via $(el).insert() when a nonce is present.", function() {
        var divEl = new Element('div');
        var testStr = '<img onerror="(function() {})()" alt="x"><js-templateNonce></js-templateNonce>'.replace('<js-templateNonce></js-templateNonce>', jrsConfigs.xssNonce);
        divEl.insert(testStr);

        expect(divEl.childNodes[0] instanceof HTMLImageElement).toEqual(true);
        expect(divEl.childNodes[0].getAttribute("alt")).toEqual('x');
        expect(divEl.childNodes[0].getAttribute("onerror")).toEqual('(function() {})()');
    });

    it("should escape 'onfocus' attribute inserted via $(el).update().", function() {
        var divEl = new Element('div');
        var testStr = '<img onfocus="(function() {})()" alt="x">';
        divEl.update(testStr);

        expect(divEl.childNodes[0] instanceof HTMLImageElement).toEqual(true);
        expect(divEl.childNodes[0].getAttribute("alt")).toEqual('x');
        expect(divEl.childNodes[0].getAttribute("onfocus")).toEqual(null);
    });

    it("should NOT escape 'onfocus' attribute inserted via $(el).update() when a nonce is present.", function() {
        var divEl = new Element('div');
        var testStr = '<img onfocus="(function() {})()" alt="x"><js-templateNonce></js-templateNonce>'.replace('<js-templateNonce></js-templateNonce>', jrsConfigs.xssNonce);
        divEl.update(testStr);

        expect(divEl.childNodes[0] instanceof HTMLImageElement).toEqual(true);
        expect(divEl.childNodes[0].getAttribute("alt")).toEqual('x');
        expect(divEl.childNodes[0].getAttribute("onfocus")).toEqual('(function() {})()');
    });

    it("should escape 'onfocus' attribute inserted via $(el).replace().", function() {
        var divElParent = new Element('div');
        var divEl = new Element('div');
        divElParent.appendChild(divEl);
        var testStr = '<img onfocus="(function() {})()" alt="x">';
        divEl.replace(testStr);

        expect(divElParent.childNodes[0] instanceof HTMLImageElement).toEqual(true);
        expect(divElParent.childNodes[0].getAttribute("alt")).toEqual('x');
        expect(divElParent.childNodes[0].getAttribute("onfocus")).toEqual(null);
    });

    it("should NOT escape 'onfocus' attribute inserted via $(el).replace() when a nonce is present.", function() {
        var divElParent = new Element('div');
        var divEl = new Element('div');
        divElParent.appendChild(divEl);
        var testStr = '<img onfocus="(function() {})()" alt="x"><js-templateNonce></js-templateNonce>'.replace('<js-templateNonce></js-templateNonce>', jrsConfigs.xssNonce);
        divEl.replace(testStr);

        expect(divElParent.childNodes[0] instanceof HTMLImageElement).toEqual(true);
        expect(divElParent.childNodes[0].getAttribute("alt")).toEqual('x');
        expect(divElParent.childNodes[0].getAttribute("onfocus")).toEqual('(function() {})()');
    });
});

