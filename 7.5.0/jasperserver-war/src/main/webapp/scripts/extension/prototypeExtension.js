/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

/*global $, $$, $w, Prototype, Position, Hash, $A, Template, Class, $F, Form, $break, $H, Selector, Field*/
define(function (require) {

    var prototypeExtension = require('util/prototypeExtension');

    // just to make sure jQuery.noConflict() is called
    // before we're extending prototype in global scope
    require('./jqueryExtension');

    //we will use global $ to reference prototype,
    //but in not optimized mode global $ is undefined until we
    //explicitly load prototype via require("prototype")
    require("prototype");

    // do not extend during build
    if (typeof $ !== 'string') {
        //Should use prototype from the global scope to work in
        //optimized and non optimized mode
        prototypeExtension.extend($);

        return {
            $: $,
            $$: $$,
            $w: $w,
            Prototype: Prototype,
            Position: Position,
            Hash: Hash,
            $A: $A,
            Template: Template,
            Class: Class,
            $F: $F,
            Form: Form,
            $break: $break,
            $H: $H,
            Selector: Selector,
            Field: Field
        };
    } else {
        return {};
    }
});