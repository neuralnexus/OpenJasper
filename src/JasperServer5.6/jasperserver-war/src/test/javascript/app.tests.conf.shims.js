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
 * @version: $Id: app.tests.conf.shims.js 47331 2014-07-18 09:13:06Z kklein $
 */

define({

    "jasmine":{
        exports:"jasmine"
    },

    "jasmine.ext":{
        deps:["jasmine"],
        exports:"jasmine"
    },

    "custom.matchers":{
        deps:["jasmine.ext"],
        exports:"jasmine"
    },

    "jasmine-sinon":{
        deps:["jasmine", "sinon"],
        exports:"jasmine"
    },

    "sinon":{
        exports:"sinon"
    },

    "sinon-ie":{
        deps:["sinon"],
        exports:"sinon"
    },

    "sinon-timers-ie":{
        deps:["sinon"],
        exports:"sinon"
        },

    "jquery.simulate" : {
         deps: ["jquery"],
         exports: "jQuery"
    },

    "jquery.cookies" : {
         deps: ["jquery"],
         exports: "jQuery"
    },

    //Reporters

    "html.jasmine.reporter":{
        deps:["jasmine"],
        exports:"jasmine.HtmlReporter"
    },

    "console.jasmine.reporter":{
        deps:["jasmine"],
        exports:"jasmine.ConsoleReporter"
    },

    "junitxml.jasmine.reporter":{
        deps:["jasmine"],
        exports:"jasmine.JUnitXmlReporter"
    },

    //Need for tests

    "lib/ns.mocks":{
        exports:"jaspersoft"
    }
});