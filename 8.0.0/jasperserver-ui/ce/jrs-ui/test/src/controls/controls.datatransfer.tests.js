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
import Controls from 'src/controls/controls.datatransfer';
import jQuery from 'jquery';
import __jrsConfigs__ from 'js-sdk/src/jrs.configs';

var Mocks = {
    inputControlsStructureResponse : {
        "inputControl": [
            {
                "id": "testControl1",
                "label": "Test Control 1",
                "mandatory": true,
                "readOnly": false,
                "type": "multiSelect",
                "uri": "repo:/reports/samples/TestReport/testControl1",
                "visible": true,
                "masterDependencies": [],
                "slaveDependencies": [
                    "testControl2",
                    "testControl3"
                ],
                "state": {
                    "uri": "/reports/samples/testControl1",
                    "id": "testControl1",
                    "value": null,
                    "error": null,
                    "options": [
                        {
                            "selected": false,
                            "label": "Canada",
                            "value": "Canada"
                        },
                        {
                            "selected": false,
                            "label": "Mexico",
                            "value": "Mexico"
                        },
                        {
                            "selected": true,
                            "label": "USA",
                            "value": "USA"
                        }
                    ]
                }
            },
            {
                "id": "testControl2",
                "label": "Test Control 2",
                "mandatory": true,
                "readOnly": false,
                "type": "multiSelect",
                "uri": "repo:/reports/samples/TestReport/testControl2",
                "visible": true,
                "masterDependencies": [
                    "testControl1"
                ],
                "slaveDependencies": [],
                "state": {
                    "uri": "/reports/samples/testControl2",
                    "id": "testControl2",
                    "value": null,
                    "error": null,
                    "options": [
                        {
                            "selected": true,
                            "label": "WA",
                            "value": "WA"
                        },
                        {
                            "selected": false,
                            "label": "CA",
                            "value": "CA"
                        },
                        {
                            "selected": true,
                            "label": "NY",
                            "value": "NY"
                        }
                    ]
                }
            },
            {
                "id": "testControl3",
                "label": "Test Control 3",
                "mandatory": true,
                "readOnly": false,
                "type": "multiSelect",
                "uri": "repo:/reports/samples/TestReport/testControl3",
                "visible": true,
                "masterDependencies": [
                    "testControl1"
                ],
                "slaveDependencies": [],
                "state": {
                    "uri": "/reports/samples/testControl3",
                    "id": "testControl3",
                    "value": null,
                    "error": null,
                    "options": []
                }
            },
            {
                "id": "testControl4",
                "label": "Test Control 4",
                "mandatory": true,
                "readOnly": false,
                "type": "multiSelect",
                "uri": "repo:/reports/samples/TestReport/testControl4",
                "visible": true,
                "masterDependencies": [
                    "testControl1"
                ],
                "slaveDependencies": [],
                "state": {
                    "uri": "/reports/samples/testControl4",
                    "id": "testControl4",
                    "value": null,
                    "error": null
                }
            }
        ]
    },
    expectedInputControlsStructure :{
        structure : [{
            "id": "testControl1",
            "label": "Test Control 1",
            "mandatory": true,
            "readOnly": false,
            "type": "multiSelect",
            "uri": "/reports/samples/TestReport/testControl1",
            "visible": true,
            "masterDependencies": [],
            "slaveDependencies": [
                "testControl2",
                "testControl3"
            ]
        },{
            "id": "testControl2",
            "label": "Test Control 2",
            "mandatory": true,
            "readOnly": false,
            "type": "multiSelect",
            "uri": "/reports/samples/TestReport/testControl2",
            "visible": true,
            "masterDependencies": [
                "testControl1"
            ],
            "slaveDependencies": []
        },{
            "id": "testControl3",
            "label": "Test Control 3",
            "mandatory": true,
            "readOnly": false,
            "type": "multiSelect",
            "uri": "/reports/samples/TestReport/testControl3",
            "visible": true,
            "masterDependencies": [
                "testControl1"
            ],
            "slaveDependencies": []
        },{
            "id": "testControl4",
            "label": "Test Control 4",
            "mandatory": true,
            "readOnly": false,
            "type": "multiSelect",
            "uri": "/reports/samples/TestReport/testControl4",
            "visible": true,
            "masterDependencies": [
                "testControl1"
            ],
            "slaveDependencies": []
        }],
        state:{
            "testControl1" : {
                error : null,
                values: [
                    {
                        "label":"Canada",
                        "value":"Canada"
                    },
                    {
                        "label":"Mexico",
                        "value":"Mexico"
                    },
                    {
                        "selected":true,
                        "label":"USA",
                        "value":"USA"
                    }
                ]
            },
            "testControl2" : {
                error : null,
                values : [
                    {
                        "selected":true,
                        "label":"WA",
                        "value":"WA"
                    },
                    {
                        "label":"CA",
                        "value":"CA"
                    },
                    {
                        "selected":true,
                        "label":"NY",
                        "value":"NY"
                    }
                ]
            },
            "testControl3" : {
                error : null,
                values : []
            },
            "testControl4" : {
                error : null,
                values : []
            }
        }
    },
    inputControlsStateResponse : {
        "inputControlState": [
            {
                "uri": "/reports/samples/testControl1",
                "id": "testControl1",
                "value": null,
                "error": null,
                "options": [
                    {
                        "selected": false,
                        "label": "Canada",
                        "value": "Canada"
                    },
                    {
                        "selected": false,
                        "label": "Mexico",
                        "value": "Mexico"
                    },
                    {
                        "selected": true,
                        "label": "USA",
                        "value": "USA"
                    }
                ]
            },
            {
                "uri": "/reports/samples/testControl2",
                "id": "testControl2",
                "value": null,
                "error": "Test Error",
                "options": [
                    {
                        "selected":true,
                        "label":"WA",
                        "value":"WA"
                    },
                    {
                        "selected":false,
                        "label":"CA",
                        "value":"CA"
                    },
                    {
                        "selected":true,
                        "label":"NY",
                        "value":"NY"
                    }
                ]
            },
            {
                "uri":"/reports/samples/testControl3",
                "id":"testControl3",
                "value":"123",
                "error":null

            },
            {
                "uri":"/reports/samples/testControl4",
                "id":"testControl4",
                "value":null,
                "error":null,
                "options": []
            },
            {
                "uri":"/reports/samples/testControl5",
                "id":"testControl5",
                "value":null,
                "error":null
            }
        ]
    },
    expectedInputControlsState : {
        state : {

            "testControl1" : {
                error : null,
                values: [
                    {
                        "label":"Canada",
                        "value":"Canada"
                    },
                    {
                        "label":"Mexico",
                        "value":"Mexico"
                    },
                    {
                        "selected":true,
                        "label":"USA",
                        "value":"USA"
                    }
                ]
            },

            "testControl2":{
                error:"Test Error",
                values:[
                    {
                        "selected":true,
                        "label":"WA",
                        "value":"WA"
                    },
                    {
                        "label":"CA",
                        "value":"CA"
                    },
                    {
                        "selected":true,
                        "label":"NY",
                        "value":"NY"
                    }
                ]
            },

            "testControl3":{
                error:null,
                values:"123"
            },

            "testControl4":{
                error:null,
                values:[]
            },

            "testControl5":{
                error:null,
                values:[]
            }
        }
    }
};

describe("DataTransfer", function() {
    var dataTransfer, reportUrl, fakeServer, clock;

    beforeEach(function() {
        dataTransfer = new Controls.DataTransfer({
            dataConverter : new Controls.DataConverter()
        });
        reportUrl = "/TestReport";
        dataTransfer.setReportUri(reportUrl);

        fakeServer = sinon.fakeServer.create();

        clock = sinon.useFakeTimers();
    });

    afterEach(function(){
        clock.restore();
        fakeServer.restore();
    });

    describe("Fetching controls structure",function() {

        it("fetch with selected data", function () {

            fakeServer.respondWith("POST", __jrsConfigs__.contextPath + "/rest_v2/reports/TestReport/inputControls/testControl1;testControl2;testControl3;testControl4",
                [200, { "Content-Type": "application/json" }, JSON.stringify(Mocks.inputControlsStructureResponse)]);

            var doSomething = jasmine.createSpy("doSomething");

            // run the code under test
            dataTransfer.fetchControlsStructure(["testControl1","testControl2","testControl3","testControl4"],{
                "testControl1" : ["USA"],
                "testControl2" : ["WA","NY"],
                "testControl3" : [],
                "testControl4" : []
            }).done(doSomething);

            fakeServer.respond();

            clock.tick(1);

            expect(doSomething.calls.mostRecent().args[0]).toEqual(Mocks.expectedInputControlsStructure);
        });

        it("fetch without selected data", function () {

            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(Mocks.inputControlsStructureResponse)]);

            var doSomething = jasmine.createSpy("doSomething");

            // run the code under test
            dataTransfer.fetchControlsStructure(["testControl1","testControl2","testControl3","testControl4"]).done(doSomething);

            fakeServer.respond();

            clock.tick(1);

            expect(doSomething.calls.mostRecent().args[0]).toEqual(Mocks.expectedInputControlsStructure);
        });
    });

    it("fetch initial values",function(){

        fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(Mocks.inputControlsStateResponse)]);

        var doSomething = jasmine.createSpy("doSomething");

        // run the code under test
        dataTransfer.fetchInitialControlValues(reportUrl).done(doSomething);

        fakeServer.respond();

        clock.tick(1);

        expect(doSomething.calls.mostRecent().args[0]).toEqual(Mocks.expectedInputControlsState);

    });

    it("fetch updated values",function(){

        var ajaxDef;
        var doSomething = jasmine.createSpy("doSomething");

        sinon.stub(jQuery, "ajax").callsFake(function() {
            ajaxDef = new jQuery.Deferred();
            return ajaxDef;
        });

        sinon.stub(Controls.Utils, "wait").callsFake(function() {
            var def = new jQuery.Deferred();
            def.resolve();
            return def;
        });

        // run the code under test
        dataTransfer.fetchControlsUpdatedValues(["testControl1","testControl2","testControl3","testControl4"], {
            "testControl1" : ["USA"],
            "testControl2" : ["WA","NY"],
            "testControl3" : [],
            "testControl4" : []
        }).done(doSomething);

        // now, we now release our deferred object, ie.e it will emulate our Ajax request
        ajaxDef.resolve(Mocks.inputControlsStateResponse);

        clock.tick(1);

        expect(doSomething).toHaveBeenCalledWith(Mocks.expectedInputControlsState);

        jQuery.ajax.restore();
        Controls.Utils.wait.restore();
    });

});