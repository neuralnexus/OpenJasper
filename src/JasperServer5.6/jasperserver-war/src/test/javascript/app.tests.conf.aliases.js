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
 * @version: $Id: app.tests.conf.aliases.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery"], function (jQuery) {
    //merge paths and aliases with base configs
    return jQuery.extend(window.paths, {

        "tests": jrsConfigs.testPath,
        "templates": jrsConfigs.testTemplatePath,
        "lib": jrsConfigs.testLibPath,

        //Plugins aliases
        "text": jrsConfigs.testLibPath + "/text",
        "domReady": jrsConfigs.testLibPath + "/domReady",

		// because 3rd party libs is located in the "/lib" folder and there is an alias for 'lib' (see above)
		// we need to alias each 3rd party library we need for tests
		// otherwise, the default link for 3rd party library will go to wrong location
		"lib/backbone-1.1.0": jrsConfigs.srcRequireJSPath + "/lib/backbone-1.1.0",
        "lib/tv4-1.0.16-patched": jrsConfigs.srcRequireJSPath + "/lib/tv4-1.0.16-patched",
		"prototype": jrsConfigs.srcRequireJSPath + "/lib/prototype-1.7.1-patched",

        //Tests aliases
        "main": jrsConfigs.testPath + "../main",
        "jasmine": jrsConfigs.testLibPath + "/jasmine",
        "html.jasmine.reporter": jrsConfigs.testLibPath + "/html.jasmine.reporter",
        "custom.matchers": jrsConfigs.testLibPath + "/custom.matchers",
        "jasmine.ext": jrsConfigs.testLibPath + "/jasmine.ext",
        "junitxml.jasmine.reporter": jrsConfigs.testLibPath + "/junitxml.jasmine.reporter",
        "console.jasmine.reporter": jrsConfigs.testLibPath + "/console.jasmine.reporter",
        "jasmine-sinon": jrsConfigs.testLibPath + "/jasmine-sinon",
        "jquery.simulate" :  jrsConfigs.testLibPath + "/jquery/jquery.simulate",
        "jquery.cookies" :  jrsConfigs.testLibPath + "/jquery/jquery.cookies",

        "sinon":jrsConfigs.testLibPath + "/sinon-1.7.3",
        "sinon-ie":jrsConfigs.testLibPath + "/sinon-ie-1.7.1",
        "sinon-timers-ie":jrsConfigs.testLibPath + "/sinon-timers-ie-1.7.1",

        "ajax.mock": jrsConfigs.testLibPath + "/ajax.mock"
    });
});