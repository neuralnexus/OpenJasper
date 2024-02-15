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
 * @version: $Id: templates.suite.ce.js 47331 2014-07-18 09:13:06Z kklein $
 */

define([
    "jquery",
    "text!templates/addUserDialog.htm",
    "text!templates/ajax.htm",
    "text!templates/attributes.htm",
    "text!templates/components.htm",
    "text!templates/controls.htm",
    "text!templates/export.htm",
    "text!templates/generateResource.htm",
    "text!templates/import.htm",
    "text!templates/pageDimmer.htm",
    "text!templates/pickers.htm",
    "text!templates/users.htm",
    "text!templates/validation.htm"
],function(jQuery){

    console.log("Tests templates for CE loaded (text resources)");

    var title =  jQuery("head title");
    //put all markup to the head to allow old tests work
    for (var i=1; i< arguments.length; i++){
        jQuery(arguments[i]).insertAfter(title);
    }

});