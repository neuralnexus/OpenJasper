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
 * @version: $Id: jasmine.env.factory.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require){

    require("tests/bdd.toolkit");

    var jasmine = require("jasmine"),
        HtmlReporter = require("html.jasmine.reporter"),
        ConsoleReporter = require("console.jasmine.reporter"),
        JUnitReporter = require("junitxml.jasmine.reporter");

   return {

       createWithAllReporters : function (){
           var jasmineEnv = jasmine.getEnv();
           jasmineEnv.updateInterval = 1000;

           jasmineEnv.addReporter(new JUnitReporter());
           var htmlReporter = new HtmlReporter();
           jasmineEnv.addReporter(htmlReporter);
           jasmineEnv.specFilter = function (spec) {
               return htmlReporter.specFilter(spec);
           };

           //keep as last reporter because phantomjs rely on it
           if (document.cookie.indexOf("phantomJS=true") !== -1){
               jasmineEnv.addReporter(new ConsoleReporter());
           }

           return jasmineEnv;
       }
   }

});