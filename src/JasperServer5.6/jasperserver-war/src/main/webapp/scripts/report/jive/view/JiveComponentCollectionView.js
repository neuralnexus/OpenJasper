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
 * @author: Igor Nesterenko
 * @version: $Id: JiveComponentCollectionView.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";


    var Backbone =  require("backbone"),
        jiveTypes = require("../enum/jiveTypes"),
        BaseJiveComponentView = require("./BaseJiveComponentView"),
        ColumnJiveComponentView = require("./ColumnJiveComponentView"),
        CrosstabJiveComponentView = require("./CrosstabJiveComponentView"),
        ChartJiveComponentView = require("./ChartJiveComponentView"),
        _ = require("underscore"),
        $ = require("jquery"),
        log =  require("logger").register("Report");

    var jive;

    function JiveComponentCollectionView(options) {
        options || (options = {});
        this.isDefaultJiveUiEnabled = options.isDefaultJiveUiEnabled;
        this.collection = options.collection;

        this.listenTo(this.collection, "reset", this.initSubviews, this);
    }

    JiveComponentCollectionView.prototype = {
        initSubviews: function(){
            var self = this,
                typeToView = {};

            typeToView[jiveTypes.CHART] = ChartJiveComponentView;
            typeToView[jiveTypes.FUSION_MAP] = BaseJiveComponentView;
            typeToView[jiveTypes.FUSION_CHART] = BaseJiveComponentView;
            typeToView[jiveTypes.FUSION_WIDGET] = BaseJiveComponentView;
            typeToView[jiveTypes.GOOGLEMAP] = BaseJiveComponentView;

            if (this.isDefaultJiveUiEnabled) {
                typeToView[jiveTypes.COLUMN] = ColumnJiveComponentView;
                typeToView[jiveTypes.CROSSTAB] = CrosstabJiveComponentView;
                typeToView[jiveTypes.TABLE] = BaseJiveComponentView;
            }

            _.invoke(this.subviews || [], "remove");

            this.subviews = this.collection
                .map(function(component){
                    var ViewConstructor = typeToView[component.get("type")];
                    return ViewConstructor && new ViewConstructor({
                        model: component,
                        report: self.collection.report,
                        isDefaultJiveUiEnabled: self.isDefaultJiveUiEnabled
                    });
                });

            this.subviews = _.compact(this.subviews);

            log.debug("Create JIVE views ", this.subviews);
        },

        render: function($el) {
            var self = this,
                dfd = new $.Deferred();

            var renderCallback = function() {
                var subViewsRenderDeferreds = _.invoke(self.subviews || [], "render", $el);

                $.when.apply($, subViewsRenderDeferreds).then(function() { dfd.resolve(); });
            };

            if (jive || !this.isDefaultJiveUiEnabled) {
                renderCallback();
            } else {
                require(["jive"], function (jiveReq) {
                    jive = jiveReq;
                    renderCallback();
                });
            }

            return dfd;
        }
    };

    _.extend(JiveComponentCollectionView.prototype, Backbone.Events);

    return JiveComponentCollectionView;

});

