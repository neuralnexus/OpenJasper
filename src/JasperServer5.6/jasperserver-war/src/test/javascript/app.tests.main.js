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
 * @author: inesterenko
 * @version: $Id: app.tests.main.js 47331 2014-07-18 09:13:06Z kklein $
 */

function loadApplicationShims () {

    var drd = jQuery.Deferred(),
        counter = 0,
        toBeLoaded = 0,
        onShimLoad = function(){
            if (++counter == toBeLoaded) drd.resolve();
        },
        getShim = function(code){
            var orig = requirejs.config,
                shim;

            requirejs.config = function(cfg) {
                shim = cfg.shim;
            };

            eval(code);
            requirejs.config = orig;
            return shim;
        },
        getPaths = function (code) {
            var orig = requirejs.config,
                paths;

            requirejs.config = function(cfg) {
                paths = cfg.paths;
            };

            eval(code);

            requirejs.config = orig;

            return paths;
        };

    jQuery.ajax({
        url: jrsConfigs.srcPath + "/require.config.js",
        cache: false,
        type: "GET",
        dataType: "text"
    }).done(function(code){
        console.log("Loaded content of the file require.config.js");

        window.shim = getShim(code);
        window.paths = getPaths(code);
    }).fail(function(){
        console.log(" !!! Failed to load file require.config.js");
    }).then(onShimLoad);

    toBeLoaded++;

    return drd;
}



loadApplicationShims().done(function(){
    console.log("All application shims were loaded, continue execution....");

    require([
        "app.tests.conf.aliases",
        "app.tests.conf.shims",
        "app.main"
    ], function (testPathAndAliases, testsShims) {

        var jasperTests = requirejs.config({

            //force paths merging
            paths:testPathAndAliases,

            //Non amd scripts

            shim:testsShims,

            //Wait before giving up on loading a script.
            waitSeconds:15
        });

        //Setup tests environment (Load all specs and create reports)

        jasperTests([
            "tests/jasmine.env.factory",
            "common/util/featureDetection",
            "prototype"
        ], function (envFactory, featureDetection) {

            console.log("Prepare tests environment...");

            var jasmineEnv = envFactory.createWithAllReporters();

            var deps = [
                "prototype",
                "domReady!"
            ];

            if (window.__test__) {
                console.log("Single test override: " + window.__test__);
                var tests = window.__test__.split(/,\s*|\s/);
                [].unshift.apply(deps, tests);
            } else {
                deps.unshift("tests/ce.specs");
                if (window.__edition__ == "pro") {
                    deps.push("tests/pro.specs");
                }
            }

            // By default PhantomJS "supports" touch events. However, our tests do not support touch devices.
            // This probably should be fixed, but for now we will make such workaround.
            window.Prototype.BrowserFeatures.SupportsTouch = featureDetection.supportsTouch;

            jasperTests(deps, function () {

                // Now, call the "dom:loaded" event to run all events which are associated with objects involved into tests
                document.fire("dom:loaded");

                console.log("Start tests execution");

                jasmineEnv.execute();
            },function (err) {

                if (err.message) {
                    console.error(err.message);
                    if (err.requireModules){
                        for (var i in err.requireModules) {
                            if (!err.requireModules.hasOwnProperty(i)) continue;
                            console.log(" !!! Failed to load module: " + err.requireModules[i]);
                        }
                    }
                }
            });
        });
    });
});
