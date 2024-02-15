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

define(function () {

    /**
     * @param {Object} properties - bi component properties
     * @constructor
     */

    function BiComponent(properties){}

    //Setters/Getters

    /**
     * Get/Set server settings
     * @param {String} server  - url to server
     * @returns this if 'arguments' send to the method,
     *          otherwise returns server settings
     */

    BiComponent.prototype.server = function(server){};

    /**
     * Get/Set bi component properties
     * @param {Object} properties  - url to server
     * @returns this if 'arguments' send to the method,
     *          otherwise returns bi properties
     */

    BiComponent.prototype.properties = function(properties){};

    /**
     * Get any result after invoking run action, null by default
     * @returns any data which supported by this bi component
     */
    BiComponent.prototype.data = function(){};

    //Actions

    /**
     * Perform main action for bi component
     * Callbacks will be attached to  deferred object.
     *
     * @param {Function} [callback] - optional, invoked in case of successful run
     * @param {Function} [errorback] - optional, invoked in case of failed run
     * @param {Function} [always] - optional, invoked always
     * @return {Deferred} dfd
     */
    BiComponent.prototype.run = function(callback, errorback, always){};

    /**
     *  Validate bi component properties,
     *  in generally, should covers client-side validation issues
     *  @return {Error} err - if properties is not valid state, null otherwise
     */

    BiComponent.prototype.validate = function(){};



    return BiComponent;
});