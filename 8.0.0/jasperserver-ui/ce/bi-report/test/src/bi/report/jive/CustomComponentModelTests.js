/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import CustomComponentModel from 'src/bi/report/jive/model/CustomComponentModel';

describe("CustomComponentModel", function() {
    var attributes,
        options = {
            parse: true,
            parent: {
                get: function(attr) {
                    var attributes = {
                        reportURI: "/public/Samples/Reports/CVC/d3_Circle_Packing"
                    };
                    return attributes[attr];
                },
                contextPath: "http://localhost:8080/jasperserver-pro"
            }
        };

    beforeEach(function() {
        attributes = {
            id: "element51614389",
            instanceData: {
                height: 470,
                id: "element51614389",
                isInteractiveViewer: "true",
                script_uri: "d3_circle_packing.min.js",
                series: [],
                width: 555
            },
            module: "cv-component",
            renderer: "d3_circle_packing",
            type: "CVComponent"
        };
    });


    it("throws error if missed script_uri", function(){
        var attr = _.extend({}, attributes);
        delete attr.instanceData.script_uri;

        expect(function() {
            new CustomComponentModel(attr, options);
        }).toThrow(new Error("Can't initialize without script name"));
    });

    it("parse attributes", function(){
        var attr = _.extend({}, attributes);
        var model = new CustomComponentModel(attr, options);

        expect(model.get("script")).toEqual({
            name: "d3_circle_packing",
            href: "d3_circle_packing.min.js?noext"
        });
    });

    it("parse css attributes", function(){
        var attr = _.extend({}, attributes);
        attr.instanceData.css_uri = "d3_circle_packing.css";

        var model = new CustomComponentModel(attr, options);

        expect(model.get("css")).toEqual({
            name: "element51614389_css",
            href: "d3_circle_packing.css?noext"
        });
    });


});
