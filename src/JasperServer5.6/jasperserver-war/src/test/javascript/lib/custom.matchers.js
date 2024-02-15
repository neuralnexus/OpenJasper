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
 * @version: $Id: custom.matchers.js 47331 2014-07-18 09:13:06Z kklein $
 */

beforeEach(function() {
    this.addMatchers(jasmine.JQuery.matchersClass);
    this.addMatchers({
        toHasClass: function(className) {
            var act = (this.actual && this.actual.hasClass) ? this.actual : jQuery(this.actual);
            return !!(this.actual && act.hasClass(className));
        },
        toBeDisabled: function() {
           var act = (this.actual && this.actual.attr) ? this.actual : jQuery(this.actual);
           return  !!act.attr('disabled');
        },
        toBeFunction: function(){
            return !!(this.actual && typeof(this.actual) === 'function');
        },
        toHaveBeenCalledNTimes: function(n){
          return !!(this.actual && this.actual.isSpy && this.actual.callCount  === n);
        },
        toArrayEquals:function(array){
            if (!this.actual) this.actual = [];

            function equals(e1, e2) {
                if (e1 === e2) return true;
                var res = false;
                if (e1.length != undefined && e2.length != undefined){
                    if (e1.length == e2.length){
                        res = true;
                        for (var i = 0; i<e1.length; i++){
                            res = res && equals(e1[i], e2[i]);
                        }
                    }
                }
                return res;
            }

            return equals(this.actual, array);
        },
        // need jasmine.ext.js
        toHaveBeenCalledBefore: function(spy){
            var res = false;
            if (this.actual.isSpy && spy.isSpy){
                 res = jasmine.callsSequence.indexOf(this.actual.identity) < jasmine.callsSequence.indexOf(spy.identity);
            }
            return res;
        },
        toHaveBeenTriggeredOn: function(selector) {
               this.message = function() {
                 return [
                   "Expected event " + this.actual + " to have been triggered on" + selector,
                   "Expected event " + this.actual + " not to have been triggered on" + selector
                 ];
               };
               return jasmine.JQuery.events.wasTriggered(selector, this.actual);
             }
    });
});

 afterEach(function() {
   jasmine.getFixtures().cleanUp();
   jasmine.JQuery.events.cleanUp();
 });