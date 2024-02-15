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
 * @author: Zakhar Tomchenko
 * @version: $Id$
 */

define(function(require) {
    "use strict";

    var _ = require("underscore"),

        Report = require("bi/component/Report"),
        ResourcesSearch = require("bi/component/ResourcesSearch"),
        InputControls = require("bi/component/InputControls");

    function BiComponentsFactory(properties){
        // TODO: deep clone
        var instanceData = _.clone(properties);

        this.report = createBiComponentFunction(instanceData, Report);
        this.inputControls  = createBiComponentFunction(instanceData, InputControls);
        this.resourcesSearch  = createBiComponentFunction(instanceData, ResourcesSearch);

        _.extend(this.report, Report);
        _.extend(this.inputControls, InputControls);
        _.extend(this.resourcesSearch, ResourcesSearch);
    }

    function createBiComponentFunction(instanceData, constructor) {
        return function(settings){
            var properties = _.extend({runImmediately: true}, instanceData, settings),
                res = new constructor(clean(properties));

            properties.runImmediately && res.run(properties.success, properties.error, properties.always);

            return res;
        }
    }

    function clean(properties){
        var props = _.clone(properties);

        delete props.success;
        delete props.error;
        delete props.always;
        delete props.runImmediately;

        return props;
    }

    return BiComponentsFactory;

});

