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

/**
 * @author: Pavel Savushchik
 * @version: $Id$
 */

define(function(require) {
    "use strict";

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        dialogTemplate = require("text!./template/dialogTemplate.htm"),
        dialogButtonTemplate = require("text!./template/dialogButtonTemplate.htm"),
        Event = require("components/utils/Event"),
        Overlay = require("components/overlay/Overlay"),
        log = require("logger").register('Dialog');

    require("jquery-ui/widgets/draggable");
    require("jquery-ui/widgets/resizable");

    var Dialog = Backbone.View.extend({

        defaults: {
            title: "",
            modal: true,
            zIndex: 4000,
            resetSizeOnOpen: true
        },
        template: _.template(dialogTemplate),
        events: {
            "click .jr-jDialogClose": "_onClose"
        },

        el: function () {
            return this.template({ options: this.props });
        },

        constructor: function (options) {
            this.props = _.defaults(options || {}, this.defaults);
            this.log = this.props.log || log;
            this.$overlay = this.props.$overlay ? this.props.$overlay : new Overlay({
                zIndex: this.props.zIndex
            });
            Backbone.View.apply(this, arguments);
        },

        initialize: function() {
            this.$window =$(window);
            this.$body = $("body");

            this.$body.append(this.$overlay.$el);
            this.$body.append(this.$el);
        },

        _onClose: function() {
            var event = new Event({name: "dialog:close"});
            this.trigger(event.name, event);
            if (!event.isDefaultPrevented()) {
                this.close();
            }
        },

        open: function(coordinates) {
            this.$el.removeClass("jr-isHidden");
            this.$el.css({
                position: "absolute"
            });

            if (this.props.resetSizeOnOpen) {
                this.$el.css({
                    height: "",
                    width: ""
                });

                this.$el.find("textarea").css({
                    height: "",
                    width: ""
                });
            }

            this._position(coordinates);

            if (this.props.modal) {
                this.$overlay.show();
                this.$el.addClass('jr-mDialogModal');
            }

            return this;
        },

        _position: function(coordinates) {

            var position,
                elHeight = this.$el.height(),
                elWidth = this.$el.width();
            var elemRect = {
                height: elHeight,
                width: elWidth
            };

            if (coordinates && typeof coordinates.top !== "undefined" && typeof coordinates.left !== "undefined") {
                position = Dialog.fitInProvidedCoordinates({
                    coordinates: coordinates,
                    outerRect: {
                        height: this.$body.height(),
                        width: this.$body.width()
                    },
                    elemRect: elemRect
                });
            } else {
                position = Dialog.calculateCenterPosition({
                    outerRect: {
                        width: this.$window.width(),
                        height: this.$window.height()
                    },
                    innerRect: elemRect,
                    scrollCorrection: {
                        width: this.$window.scrollLeft(),
                        height: this.$window.scrollTop()
                    }
                })
            }

            this.$el.css({
                top: parseInt(position.top),
                left: parseInt(position.left),
                zIndex: this.props.zIndex
            });

            return this;
        },

        close: function() {
            this.$el.addClass("jr-isHidden");
            if (this.props.modal) {
                this.$overlay.hide();
            }
            return this;
        },

        delegateEvents: function() {
            var self = this;

            Backbone.View.prototype.delegateEvents.apply(this, arguments);

            this.$el.draggable({
                handle: ".jr-jDialogDraggable",
                addClasses: false,
                containment: "document"
            });

            var $resizer = this.$(".jr-jDialogResizer");

            if ($resizer.length > 0) {
                this.$el.resizable({
                    handles: {
                        "se": this.$(".jr-jDialogResizer")
                    },
                    start: function (event, ui) {
                        self.trigger("dialog:resize:started", ui);
                    },
                    stop: function (event, ui) {
                        self.trigger("dialog:resize:stopped", ui);
                    },
                    resize: function (event, ui) {
                        self.trigger("dialog:resize", ui);
                    }
                });
            }

            return this;
        },

        undelegateEvents: function() {
            try {
                this.$el.draggable("destroy");
                this.$el.resizable("destroy");
            } catch (err) {
                // just swallow warning
            }
            return Backbone.View.prototype.undelegateEvents.apply(this, arguments);
        },
        remove: function() {
            this.stopListening();
            this.$overlay.remove();
            Backbone.View.prototype.remove.call(this);
            return this;
        }

    }, {
        calculateCenterPosition: function(options) {
            options = options || {};

            var outerRect = options.outerRect;
            var innerRect = options.innerRect;
            var scrollCorrection = options.scrollCorrection;

            if (!scrollCorrection){
                scrollCorrection = {
                    width: 0,
                    height: 0
                }
            }

            if (outerRect && innerRect) {
                var left = Math.max(0, scrollCorrection.width + outerRect.width / 2 - innerRect.width / 2);
                var  top = Math.max(0, scrollCorrection.height + outerRect.height / 2 - innerRect.height / 2);

                if (_.isNaN(left) || _.isNaN(top)) {
                    throw new TypeError("Can't calculate position. Make sure that you pass dimension as integer values")
                }

                return {
                    left: left,
                    top: top
                }
            } else {
                throw new Error("Illegal arguments")
            }
        },

        fitInProvidedCoordinates: function(options) {
            options = options || {};

            var coordinates = options.coordinates;
            var outerRect = options.outerRect;
            var elemRect = options.elemRect;

            var topPoint = coordinates.topPoint || 0;
            var leftPoint = coordinates.leftPoint || 0;

            var top = coordinates.top - topPoint * elemRect.height;
            var left = coordinates.left - leftPoint * elemRect.width;

            var fitByHeight = outerRect.height - coordinates.top;
            var fitByWidth = outerRect.width - coordinates.left;

            if (fitByHeight < elemRect.height) {
                top = coordinates.top - elemRect.height;
                top = (top < 0) ? (outerRect.height / 2 - elemRect.height / 2) : top
            }
            if (fitByWidth < elemRect.width) {
                left = coordinates.left - elemRect.width;
                left = (left < 0) ? (outerRect.width / 2 - elemRect.width / 2) : left
            }

            return {
                top: top,
                left: left
            }
        }
    });

    Dialog.prototype = _.extend({

        get title() {
            return this.props.title;
        },

        set title(value) {

            if (!_.isString(value)) {
                throw new TypeError("'Title' should be string");
            }

            this.props.title = value;
            this.$el.find(".jr-jDialogTitle").text(this.props.title);
        },

        get modal() {
            return this.props.modal;
        },

        set modal(value) {

            if (!_.isBoolean(value)) {
                throw new TypeError("'Modal' should be boolean");
            }

            this.props.modal = value;
        }

    }, Dialog.prototype);

    return Dialog;
});
