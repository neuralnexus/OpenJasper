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
 * @author: Igor Nesterenko, Kostiantyn Tsaregradskyi
 * @version: $Id: ReportView.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var _ = require("underscore"),
        Backbone = require("backbone"),
        jiveTypes = require("../jive/enum/jiveTypes"),
        reportCreators = require("../enum/reportCreators"),
        ReportComponentCollection = require("../jive/collection/ReportComponentCollection"),
        JiveComponentCollectionView = require("../jive/view/JiveComponentCollectionView"),
        reportOutputFormats = require("../enum/reportOutputFormats"),
        $ = require("jquery"),
        reportEvents = require("../enum/reportEvents"),
        domReady = require("domReady"),
        LocalFrameView = require("./LocalFrameView");

    return Backbone.View.extend({
        el: "<div style='height: 100%;'></div>",

        initialize: function(options) {
            this.jiveComponentCollectionView = new JiveComponentCollectionView({
                collection: this.collection,
                isDefaultJiveUiEnabled: options.isDefaultJiveUiEnabled
            });
            this.isDefaultJiveUiEnabled = options.isDefaultJiveUiEnabled;
            this.isolateDom = options.isolateDom;

            if (this.isolateDom) {
                this.localFrameView = new LocalFrameView();
            }
        },

        render: function(selector) {
            var self = this,
                dfd = new $.Deferred();

            domReady(function() {
                var $reportContainer = self.$reportContainer = $(selector);
                var containerHeight = $reportContainer.height();

                if ($reportContainer.length && $reportContainer[0].nodeType == "1") {

                    if (self.isolateDom) {
                        $($reportContainer[0]).html(self.localFrameView.$el);
                        self.renderIsolatedDom(dfd, $reportContainer, containerHeight);
                    } else {
                        $($reportContainer[0]).html(self.$el);
                        self.showOverlay(true);
                        self.renderMarkup();
                        self.renderJive().done(function() { dfd.resolve(); });
                    }
                } else {
                    dfd.reject(new Error("Can't find container to render"));
                }
            });

            return dfd;
        },
        renderIsolatedDom: function(dfd, $reportContainer, containerHeight) {
            var self = this;

            self.localFrameView.add(self.$el, function(){

                if (self.isElasticChart()) {
                    self.localFrameView.el.width = "100%";
                    // if $reportContainer.height is 100%, when we attach iframe $reportContainer.height sets to ~150px,
                    // so we have to know whether user defined the height or not before iframe was attached.
                    // save it to containerHeight variable.
                    if (containerHeight === 0) {
                        self.localFrameView.el.height = 400;
                    } else {
                        self.localFrameView.el.height = containerHeight;
                    }
                    if (window) {
                        $(window).resize(function(){
                            self.localFrameView.el.height = $reportContainer.height();
                        });
                    }
                }

                self.renderMarkup();
                self.renderJive().done(function() { dfd.resolve(); });

                if (!self.isElasticChart()) {
                    self.localFrameView.el.width = "100%";
                    if (self.localFrameView.$el.width() < self.$el.children().width()) {
                        self.localFrameView.el.width = self.$el.children().width();
                    }
                    self.localFrameView.el.height = self.$el.children().height();
                }
            });
        },

        isElasticChart: function() {
            return this.collection.some(function (component) {
                return  reportCreators.AD_HOC_DESIGNER === component.get("creator") &&
                        jiveTypes.CHART === component.get("type");
            });
        },
        renderMarkup: function() {
            var $markup = $(this.model.getExport(reportOutputFormats.HTML).get("output"));

            this.linkOptions && processLinks(this, $markup);

            if (this.isElasticChart()) {
                $markup = $markup
                    .find(".highcharts_parent_container")
                    .clone()
                    .css({
                        "margin": "0 auto",
                        "height": "100%"
                    });

                // Dirty hack to make pie chart rendering to work. Is done because of adhocHighchartsSettingService.js
                $markup = $("<div></div>")
                    .append($markup)
                    .append("<table cellpadding='0' cellspacing='0' style='widht: 100%; height: 100%;' class='jrPage'></table>")
                    .html();
            }
            this.trigger(reportEvents.BEFORE_RENDER,$markup[0]);
            this.$el.html($markup);
            return this;
        },

        renderJive: function() {
            var self = this,
                dfd = this.jiveComponentCollectionView.render(this.$el);
            dfd.then(function(){
                self.hideOverlay();
            });
            return dfd;
        },

        showOverlay: function(redoOverlay) {
            if (!this.isDefaultJiveUiEnabled || (this.$overlay && this.$overlay.is(":visible"))) {
                return;
            }

            this.$overlay && redoOverlay && this.$overlay.remove() && (delete this.$overlay);
            this.$overlay || this._drawOverlay();

            var width, height;
            // if container smaller than report
            var smallerX = this.$reportContainer.width() < this.$el.children().width();
            var smallerY = this.$reportContainer.height() < this.$el.children().height();
            // if container can have scroll
            var overflowX = this.$reportContainer.css("overflow-x") === "visible";
            var overflowY = this.$reportContainer.css("overflow-y") === "visible";

            if (smallerX && overflowX) {
                width = this.$el.children().width();
            } else {
                width = this.$reportContainer.width();
            }
            if (smallerY && overflowY) {
                height = this.$el.children().height();
            } else {
                height = this.$reportContainer.height();
            }

            this.$overlay.css({
                    width: width,
                    height: height,
                    "line-height": height + "px"
                }).show().position({
                    my: "left top",
                    at: "left top",
                    of: this.$reportContainer
                });
        },
        hideOverlay: function() {
            if (!this.isDefaultJiveUiEnabled) {
                return;
            }
            this.$overlay && this.$overlay.hide();
        },
        _drawOverlay: function() {
            if (!this.isDefaultJiveUiEnabled) {
                return;
            }
            this.$overlay = $("<div>Loading...</div>")
                .prependTo(this.$reportContainer)
                .css({
                    color: "#fff",
                    "text-align": "center",
                    "z-index": 495,
                    "background-color": "#000",
                    filter: "alpha(opacity=60)",
                    opacity: ".6",
                    position: "absolute",
                    display: "none"
                });

        }

    });

    function processLinks(view, $markup) {
        var links = view.model.components.getLinks();

        if (view.linkOptions.beforeRender && links.length) {
            view.linkOptions.beforeRender(_.map(links, function(hyperlinkData) {
                return {
                    element: $markup.find("[data-id='" + hyperlinkData.id + "']")[0],
                    data: hyperlinkData
                }
            }));
        }

        if (view.linkOptions.events && links.length) {
            view.events = _.reduce(_.keys(view.linkOptions.events), function(events, eventName) {
                events[eventName + " ._jrHyperLink"] = processLinkEventHandler(links, view.linkOptions.events[eventName]);
                return events;
            }, {});

            view.delegateEvents();
        }
    }

    function processLinkEventHandler(links, handler){
        return function(event){
            handler.call(this, event, _.findWhere(links, {id: event.currentTarget.getAttribute("data-id")}));
        }

    }
});