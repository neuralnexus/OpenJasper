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

import templateEngine from 'src/components/components.templateengine';

describe("Common TempateEngine", function () {

    var templateText, templateText2;

    beforeEach(function() {
        templateText = "please, read this '{{=message}}'";
        templateText2 = "<!--#items-->{{ for (var i=0; i<items.length; i++){ }}<li>{{=items[i].label}}</li>{{  } }}<!--/items-->";
        jasmine.getFixtures().set("<script id='sandbox' type='template/mustache'>"+templateText+"</script><js-templateNonce></js-templateNonce>" + "<script id='sandbox2' type='template/mustache'>"+templateText2+"</script>");
    });

    it("can render default templates", function () {
        var result = templateEngine.render(
            templateText,
            {message:"bla-bla"}
        );
        expect(result).toEqual("please, read this 'bla-bla'");
    });

    it("can render placeholders like {0} with simple array ", function () {

        var templateText = "dfsdfsdf sdfsdfsdf {0}, afdfsdfsdfsd {1} dffsdfsdf, dfsdfsdfsd {2}",
            parameters = ["a", "bb","ccc"];

        var result = templateEngine.render(
            templateText,
            parameters,
            templateEngine.STD_PLACEHOLDERS
        );

        expect(result).toEqual("dfsdfsdf sdfsdfsdf a, afdfsdfsdfsd bb dffsdfsdf, dfsdfsdfsd ccc");
    });

    it("URL is encoded if param set", function(){
        var template = "/path/to/service/entity{{- entityUri }}/";
        var entityUri = "/абв/abc/qфg";

        var result = templateEngine.renderUrl(template, {entityUri: entityUri}, true);

        expect(result).toEqual(encodeURI("/path/to/service/entity/абв/abc/qфg/"));
    });

    it("can get template's  content", function () {
        var content = templateEngine.getTemplateText("sandbox");
        if (navigator.userAgent.toLowerCase().indexOf("msie") > -1){
            //workaroud for IE7-8
            content =  content.replace("\r\n", "");
        }
        expect(content).toEqual(templateText);
    });

    it("create template function", function () {
        var template = templateEngine.createTemplate("sandbox");
        expect(typeof template).toEqual('function');

        var result = template({message:"bla-bla"});
        if (navigator.userAgent.toLowerCase().indexOf("msie") > -1){
            //workaroud for IE7-8
            result =  result.replace("\r\n", "");
        }
        expect(result).toEqual("please, read this 'bla-bla'");
    });

    it("get template section as function", function(){
        var template = templateEngine.createTemplateSection("items", "sandbox2");
        expect(typeof template).toEqual('function');

        var result = template({
            items: [
                {label :"1"},
                {label :"2"},
                {label :"3"}
            ]
        });

        expect(result).toEqual("<!--#items--><li>1</li><li>2</li><li>3</li><!--/items-->");
    });
})