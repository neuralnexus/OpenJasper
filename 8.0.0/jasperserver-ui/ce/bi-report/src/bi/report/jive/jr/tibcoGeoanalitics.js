/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

/* eslint no-console: "off", no-eval: "off" */
/* global require, global */

import $ from 'jquery';
var T = T || {};
T.settings = { geoWebServices: window.location.protocol != 'https:' ? 'http://geowebservices.maporama.com/' : 'https://geowebservices.maporama.com/' };    //window.T = T;
// ## Document extesnion functions
//window.T = T;
// ## Document extesnion functions
if (!document.querySelectorAll) {
    document.querySelectorAll = function (r, c, i, j, a) {
        var s = document.createStyleSheet();
        a = document.all, c = [], r = r.replace(/\[for\b/gi, '[htmlFor').split(',');
        for (i = r.length; i--;) {
            s.addRule(r[i], 'k:v');
            for (j = a.length; j--;)
                a[j].currentStyle.k && c.push(a[j]);
            s.removeRule(0);
        }
        return c;
    };
}    // ##Array extension functions
// ##Array extension functions
if (!Array.prototype.reduce) {
    Array.prototype.reduce = function (callback, opt_initialValue) {
        if (null === this || 'undefined' === typeof this) {
            // At the moment all modern browsers, that support strict mode, have
            // native implementation of Array.prototype.reduce. For instance, IE8
            // does not support strict mode, so this check is actually useless.
            throw new TypeError('Array.prototype.reduce called on null or undefined');
        }
        if ('function' !== typeof callback) {
            throw new TypeError(callback + ' is not a function');
        }
        var index, value, length = this.length >>> 0, isValueSet = false;
        if (1 < arguments.length) {
            value = opt_initialValue;
            isValueSet = true;
        }
        for (index = 0; length > index; ++index) {
            if (this.hasOwnProperty(index)) {
                if (isValueSet) {
                    value = callback(value, this[index], index, this);
                } else {
                    value = this[index];
                    isValueSet = true;
                }
            }
        }
        if (!isValueSet) {
            throw new TypeError('Reduce of empty array with no initial value');
        }
        return value;
    };
}
if (!Array.prototype.map) {
    Array.prototype.map = function (fun) {
        var len = this.length;
        if (typeof fun !== 'function') {
            throw new TypeError('func is not a function!');
        }
        var res = [], i;
        var thisArg = arguments.length >= 2 ? arguments[1] : void 0;
        for (i = 0; i < len; i++) {
            // NOTE: Absolute correctness would demand Object.defineProperty
            //       be used.  But this method is fairly new, and failure is
            //       possible only if Object.prototype or Array.prototype
            //       has a property |i| (very unlikely), so use a less-correct
            //       but more portable alternative.
            if (i in this) {
                res[i] = fun.call(thisArg, this[i], i, this);
            }
        }
        return res;
    };
}
if (!Array.prototype.filter) {
    Array.prototype.filter = function (fun) {
        var len = this.length;
        if (typeof fun !== 'function') {
            throw new TypeError();
        }
        var res = [], i;
        var thisArg = arguments.length >= 2 ? arguments[1] : void 0;
        for (i = 0; i < len; i++) {
            if (i in this) {
                var val = this[i];    // NOTE: Technically this should Object.defineProperty at
                //       the next index, as push can be affected by
                //       properties on Object.prototype and Array.prototype.
                //       But that method's new, and collisions should be
                //       rare, so use the more-compatible alternative.
                // NOTE: Technically this should Object.defineProperty at
                //       the next index, as push can be affected by
                //       properties on Object.prototype and Array.prototype.
                //       But that method's new, and collisions should be
                //       rare, so use the more-compatible alternative.
                if (fun.call(thisArg, val, i, this)) {
                    res.push(val);
                }
            }
        }
        return res;
    };
}
if (!Array.prototype.forEach) {
    Array.prototype.forEach = function (fun) {
        var t = this;
        var len = t.length;
        if (typeof fun !== 'function') {
            throw new TypeError();
        }
        var thisArg = arguments.length >= 2 ? arguments[1] : void 0, i = 0;
        for (i = 0; i < len; i++) {
            if (i in t) {
                fun.call(thisArg, t[i], i, t);
            }
        }
    };
}
if (!Array.isArray) {
    Array.isArray = function (vArg) {
        return Object.prototype.toString.call(vArg) === '[object Array]';
    };
}
if (!String.prototype.trim) {
    String.prototype.trim = function () {
        return this.replace(/^\s+|\s+$/g, '');
    };
}    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
(function (exports) {
    /**
         * EventDispatcher class is used to catch and dispatch custom events
         *
         * @summary EventDispatcher's constructor
         * @constructor true
         */
    var EventDispatcher = function () {
        this.events = {};
        this.eventsContext = {};
    };
    EventDispatcher.prototype = {
        /**
             * Adds a listener to an event
             *
             * @param {String} eventName The name of the event for which to add the listener
             * @param {Function} fn The callback function that will be called when the event is fired
             * @param {Object} context The context in which the callback will be called
             */
        on: function (eventName, fn, context) {
            var f = context ? this.bind(fn, context) : fn, events = this.events[eventName] = this.events[eventName] || {}, eventsContext = this.eventsContext[eventName] = this.eventsContext[eventName] || {};
            fn.key = 'fn' + this.now() + '_' + Math.floor(Math.random() * 100);
            fn.proxy = f;
            events[fn.key] = f;
            eventsContext[fn.key] = context;
        },
        /**
             * Fires an event
             *
             * @param {String} eventName The name of the event that will be fired
             * @param {Object} params The parameters that will be sent to the callback functions of the listeners
             */
        fire: function (eventName, params) {
            var functions = this.events[eventName], key;
            for (key in functions) {
                if (functions.hasOwnProperty(key)) {
                    if (typeof functions[key] === 'function') {
                        var fn = functions[key];
                        fn.apply(this, [params]);
                    }
                }
            }
        },
        /**
             * Removes an event listener
             *
             * @param {String} eventName The name of the event
             * @param {Function} fn The function associated with the given event name that will be removed from list of listeners
             */
        detach: function (eventName, fn) {
            var events = this.events[eventName];
            if (events.hasOwnProperty(fn.key)) {
                delete events[fn.key];
            }
            return;
        },
        /**
             * Detaches all functions from all events
             */
        detachAll: function () {
            this.events = {};
        },
        bind: function (fn, c) {
            return function () {
                return fn.apply(c || fn, arguments);
            };
        },
        now: function () {
            return new Date().getTime();
        }
    };    // Exports
    // Exports
    exports.EventDispatcher = EventDispatcher;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
(function (exports) {
    /**
         * Point class is used to define a geometric point
         *
         * @summary Point's constructor
         * @constructor true
         *
         * @param {Number} x The x property of the point
         * @param {Number} y The y property of the point
         */
    var Point = function (x, y) {
        this.x = x;
        this.y = y;
    };
    Point.prototype = {
        /**
             * Converts the current instance to a string
             * Ex.: `Point(12.05,15.17)`
             * @return {String} The converted string
             */
        toString: function () {
            return 'Point(' + this.x + ',' + this.y + ')';
        },
        /**
             * Creates a copy of the current instance
             * @return {[Point](#top)} point The copy of the current instance
             */
        clone: function (point) {
            return new Point(this.x, this.y);
        },
        /**
             * Adds the given point's values to the current instance without modifying the current instance
             * @param {[Point](#top)} point The point whose values will be added
             * @return {[Point](#top)} The new point
             */
        add: function (point) {
            var result = this.clone();
            result.x += point.x;
            result.y += point.y;
            return result;
        },
        /**
             * Substracts the given point's values from the current instance without modifying the current instance
             * @param {[Point](#top)} point The point whose values will be substracted
             * @return {[Point](#top)} The new point
             */
        substract: function (point) {
            var result = this.clone();
            result.x -= point.x;
            result.y -= point.y;
            return result;
        },
        /**
             * Multiplies the values of the current instance by the given point's values or by just a number without modifying the current instance
             * @param {[Point](#top)|Number} val The point or the number whose values will be multiplied
             * @return {[Point](#top)} The new point
             */
        multiplyBy: function (val) {
            var result = this.clone();
            if (val instanceof T.Point) {
                result.x *= val.x;
                result.y *= val.y;
            } else {
                result.x *= val;
                result.y *= val;
            }
            return result;
        },
        /**
             * Divides the values of the current instance by the given point's values or by just a number without modifying the current instance
             * @param {[Point](#top)|Number} val The point or the number whose value will be the divisor
             * @return {[Point](#top)} The new point
             */
        divideBy: function (val) {
            var result = this.clone();
            if (val instanceof T.Point) {
                result.x /= val.x;
                result.y /= val.y;
            } else {
                result.x /= val;
                result.y /= val;
            }
            return result;
        },
        /**
             * Rounds the values of the current instance to the closest integer without modifying the current instance
             * @return {[Point](#top)} The new point
             */
        round: function () {
            var result = this.clone();
            result.x = Math.round(this.x);
            result.y = Math.round(this.y);
            return result;
        },
        /**
             * Rounds the values of the current instance to the largest previous integer without modifying the current instance
             * @return {[Point](#top)} The new point
             */
        floor: function () {
            var result = this.clone();
            result.x = Math.floor(result.x);
            result.y = Math.floor(result.y);
            return result;
        },
        /**
             * Compares the values of the current instance with another's
             * @param {[Point](#top)} other The point with which the comparison is done
             * @return {Boolean} If `true`, the values are equal
             */
        isEqual: function (other) {
            return this.x === other.x && this.y === other.y;
        },
        /**
             * Calculates the distance between the current point and another
             * @param {[Point](#top)} other The point to which the distance will be calculated
             * @return {Number} The calculated distance
             */
        distanceTo: function (point) {
            var x = point.x - this.x, y = point.y - this.y;
            return Math.sqrt(x * x + y * y);
        }
    };    // Exports
    // Exports
    exports.Point = Point;
}(T));    /*global T, window */
/*global T, window */
(function (exports) {
    var Transform = function (a, b, c, d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    };
    Transform.prototype = {
        transform: function (point, scale) {
            var result = point.clone();
            scale = scale || 1;
            result.x = scale * (this.a * result.x + this.b);
            result.y = scale * (this.c * result.y + this.d);
            return result;
        },
        untransform: function (point, scale) {
            scale = scale || 1;
            return new T.Point((point.x / scale - this.b) / this.a, (point.y / scale - this.d) / this.c);
        }
    };    // Exports
    // Exports
    exports.Transform = Transform;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
(function (exports) {
    /**
         * LatLng class is used to define a geographic point which contains a latitutude and a longitude
         *
         * @summary LatLng's constructor
         * @constructor true
         *
         * @param {Number} lat The latitude
         * @param {Number} lng The longitude
         */
    var LatLng = function (lat, lng) {
        this.lat = lat;
        this.lng = lng;
    };    /**
         * Converts the two given parameters to a LatLng object
         *
         * @static true
         *
         * @param {[LatLng](#top)|Array|Number} p1 The first parameter
         * @param {Number} p2 If `p1` is a number, `p2` will be used to convert to a LatLng object
         *
         * @return {[LatLng](#top)} The converted LatLng object
         */
    /**
         * Converts the two given parameters to a LatLng object
         *
         * @static true
         *
         * @param {[LatLng](#top)|Array|Number} p1 The first parameter
         * @param {Number} p2 If `p1` is a number, `p2` will be used to convert to a LatLng object
         *
         * @return {[LatLng](#top)} The converted LatLng object
         */
    LatLng.from = function (p1, p2) {
        if (p1 === undefined || p1 === null) {
            return null;
        }
        if (p1 instanceof T.LatLng) {
            return p1;
        }
        if (T.Util.isArray(p1)) {
            return new T.LatLng(p1[0], p1[1]);
        }
        return new T.LatLng(p1, p2);
    };
    LatLng.prototype = {
        /**
             * Converts a LatLng instance to a string.
             * Ex.: `'LatLng(43.15,22.17)'`
             *
             * @return {String} The converted string
             */
        toString: function () {
            return 'LatLng(' + this.lat + ',' + this.lng + ')';
        },
        /**
             * Returns a copy of the current instance
             *
             * @return {[LatLng](#top)} The copy of the current instance
             */
        clone: function () {
            return new LatLng(this.lat, this.lng);
        },
        /**
             * Checks if the current instance's values is equal to another's
             *
             * @param {[LatLng](#top)} other The other instance
             *
             * @return {Boolean} If `true`, the instances' values are equal
             */
        isEqual: function (other) {
            return this.lat === other.lat && this.lng === other.lng;
        },
        /**
             * Returns a copy of the current instance with the `lat` and `lng` values rounded to nearest integer
             * @return {[LatLng](#top)} The new rounded instance
             */
        round: function () {
            var result = this.clone();
            result.lat = Math.round(this.lat);
            result.lng = Math.round(this.lng);
            return result;
        }
    };    // Export
    // Export
    exports.LatLng = LatLng;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
(function (exports) {
    /**
         * LatLngBounds class is used to define a boundary based on south-west and north-east coordinates
         *
         * @summary LatLngBounds's constructor
         * @constructor true
         *
         * @param {[LatLng](LatLng.js.html)} southWest The south-west coordinates of the boundary
         * @param {[LatLng](LatLng.js.html)} northEast The north-east coordinates of the boundary
         */
    var LatLngBounds = function (southWest, northEast) {
        if (!southWest) {
            return;
        }
        this.extend(southWest);
        this.extend(northEast);
    };    /**
         * Converts the two given parameters to a LatLngBounds instance
         *
         * @static true
         * @param {[LatLngBounds](#top)|Array} p1 The first parameter, it can be an array containing two other arrays with latitude and longitude values, or it can be an array containing two [LatLng](LatLng.js.html) instances
         * @param {Array} p2 If `p1` is not a LatLngBounds instance, `p2` is used as the north-east coordinates of the boundary
         * @return {[LatLngBounds](#top)} The converted LatLngBounds instance
         */
    /**
         * Converts the two given parameters to a LatLngBounds instance
         *
         * @static true
         * @param {[LatLngBounds](#top)|Array} p1 The first parameter, it can be an array containing two other arrays with latitude and longitude values, or it can be an array containing two [LatLng](LatLng.js.html) instances
         * @param {Array} p2 If `p1` is not a LatLngBounds instance, `p2` is used as the north-east coordinates of the boundary
         * @return {[LatLngBounds](#top)} The converted LatLngBounds instance
         */
    LatLngBounds.from = function (p1, p2) {
        if (p1 === undefined || p1 === null) {
            return null;
        }
        if (p1 instanceof T.LatLngBounds) {
            return p1;
        }
        if (T.Util.isArray(p1) && T.Util.isArray(p2)) {
            return new T.LatLngBounds(T.LatLng.from(p1), T.LatLng.from(p2));
        }
        if (T.Util.isArray(p1) && p1.length === 2) {
            return new T.LatLngBounds(T.LatLng.from(p1[0]), T.LatLng.from(p1[1]));
        }
    };
    LatLngBounds.prototype = {
        /**
             * Extends the current instance bounds so that it contains the given coordinates or bounds
             * @param {[LatLng](LatLng.js.html)|[LatLngBounds](#top)} obj The coordinates or the bounds to which to extend
             * @return {[LatLngBounds](#top)} The current instance
             */
        extend: function (obj) {
            if (!obj) {
                return this;
            }
            if (obj instanceof T.LatLng) {
                if (!this.southWest && !this.northEast) {
                    this.southWest = new T.LatLng(obj.lat, obj.lng);
                    this.northEast = new T.LatLng(obj.lat, obj.lng);
                } else {
                    this.southWest.lat = Math.min(obj.lat, this.southWest.lat);
                    this.southWest.lng = Math.min(obj.lng, this.southWest.lng);
                    this.northEast.lat = Math.max(obj.lat, this.northEast.lat);
                    this.northEast.lng = Math.max(obj.lng, this.northEast.lng);
                }
            } else if (obj instanceof T.LatLngBounds) {
                this.extend(obj.southWest);
                this.extend(obj.northEast);
            }
            return this;
        },
        /**
             * Returns the north-east coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The north-east coordinates
             */
        getNorthEast: function () {
            return new T.LatLng(this.northEast.lat, this.northEast.lng);
        },
        /**
             * Returns the north-west coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The north-west coordinates
             */
        getNorthWest: function () {
            return new T.LatLng(this.northEast.lat, this.southWest.lng);
        },
        /**
             * Returns the south-east coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The south-east coordinates
             */
        getSouthEast: function () {
            return new T.LatLng(this.southWest.lat, this.northEast.lng);
        },
        /**
             * Returns the south-west coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The south-west coordinates
             */
        getSouthWest: function () {
            return new T.LatLng(this.southWest.lat, this.southWest.lng);
        },
        /**
             * Returns the coordinates of the center of the current instance
             * @return {[LatLng](LatLng.js.html)} The coordinates of the center
             */
        center: function () {
            return new T.LatLng((this.southWest.lat + this.northEast.lat) / 2, (this.southWest.lng + this.northEast.lng) / 2);
        },
        /**
             * Returns the west coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The west coordinates
             */
        getWest: function () {
            return this.southWest.lng;
        },
        /**
             * Returns the south coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The south coordinates
             */
        getSouth: function () {
            return this.southWest.lat;
        },
        /**
             * Returns the east coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The east coordinates
             */
        getEast: function () {
            return this.northEast.lng;
        },
        /**
             * Returns the north coordinates of the instance
             * @return {[LatLng](LatLng.js.html)} The north coordinates
             */
        getNorth: function () {
            return this.northEast.lat;
        }
    };    // Export
    // Export
    exports.LatLngBounds = LatLngBounds;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
(function (exports) {
    /**
         * Bounds class is used to define a boundary based on bottom left and top right points
         *
         * @summary Bounds's constructor
         * @constructor true
         *
         * @param {[Point](Point.js.html)} a The bottom left point
         * @param {[Point](Point.js.html)} b The top right point
         */
    var Bounds = function (a, b) {
        if (!a) {
            a = new T.Point(0, 0);
        }
        if (!b) {
            b = new T.Point(0, 0);
        }
        this.extend(a);
        this.extend(b);
    };
    Bounds.prototype = {
        /**
             * Extends the current bounds with the given point
             *
             * @param {[Point](Point.js.html)} point The point used to extend the bounds
             * @return {[Bounds](#top)} The modified bounds
             */
        extend: function (point) {
            if (!point) {
                return;
            }
            if (!this.min && !this.max) {
                this.min = point.clone();
                this.max = point.clone();
            } else {
                this.min.x = Math.min(point.x, this.min.x);
                this.max.x = Math.max(point.x, this.max.x);
                this.min.y = Math.min(point.y, this.min.y);
                this.max.y = Math.max(point.y, this.max.y);
            }
            return this;
        },
        /**
             * Intersects the current bounds with the given bounds without modifying the current bounds
             *
             * @param {[Bounds](#top)} bounds The bounds used to intersect the current bounds
             * @return {[Bounds](#top)} The intersection of the two bounds
             */
        intersect: function (bounds) {
            var b = new T.Bounds();
            b.min.x = Math.max(this.min.x, bounds.min.x);
            b.min.y = Math.max(this.min.y, bounds.min.y);
            b.max.x = Math.min(this.max.x, bounds.max.x);
            b.max.y = Math.min(this.max.y, bounds.max.y);
            return b;
        },
        /**
             * Returns the center of the bounds
             * @return {[Point](Point.js.html)} The center of the bounds
             */
        center: function () {
            return new T.Point((this.min.x + this.max.x) / 2, (this.min.y + this.max.y) / 2);
        },
        /**
             * Returns the top left point of the bounds
             * @return {[Point](Point.js.html)} The top left point of the bounds
             */
        topLeft: function () {
            return new T.Point(this.min.x, this.max.y);
        },
        /**
             * Returns the top right point of the bounds
             * @return {[Point](Point.js.html)} The top right point of the bounds
             */
        topRight: function () {
            return new T.Point(this.max.x, this.max.y);
        },
        /**
             * Returns the bottom left point of the bounds
             * @return {[Point](Point.js.html)} The bottom left point of the bounds
             */
        bottomLeft: function () {
            return new T.Point(this.min.x, this.min.y);
        },
        /**
             * Returns the bottom right point of the bounds
             * @return {[Point](Point.js.html)} The bottom right point of the bounds
             */
        bottomRight: function () {
            return new T.Point(this.max.x, this.min.y);
        },
        /**
             * Returns the width of the bounds
             * @return {Number} The width of the bounds
             */
        width: function () {
            return this.max.x - this.min.x;
        },
        /**
             * Returns the height of the bounds
             * @return {Number} The height of the bounds
             */
        height: function () {
            return this.max.y - this.min.y;
        }
    };    // Export
    // Export
    exports.Bounds = Bounds;
}(T));    /*jslint evil: true, regexp: true */
/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
     call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
     getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
     lastIndex, length, parse, prototype, push, replace, slice, stringify,
     test, toJSON, toString, valueOf
     */
// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.
/*jslint evil: true, regexp: true */
/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
     call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
     getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
     lastIndex, length, parse, prototype, push, replace, slice, stringify,
     test, toJSON, toString, valueOf
     */
// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.
if (typeof JSON !== 'object') {
    JSON = {};
}
(function () {
    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }
    if (typeof Date.prototype.toJSON !== 'function') {
        Date.prototype.toJSON = function () {
            return isFinite(this.valueOf()) ? this.getUTCFullYear() + '-' + f(this.getUTCMonth() + 1) + '-' + f(this.getUTCDate()) + 'T' + f(this.getUTCHours()) + ':' + f(this.getUTCMinutes()) + ':' + f(this.getUTCSeconds()) + 'Z' : null;
        };
        String.prototype.toJSON = Number.prototype.toJSON = Boolean.prototype.toJSON = function () {
            return this.valueOf();
        };
    }
    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, gap, indent, meta = {
            // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"': '\\"',
            '\\': '\\\\'
        }, rep;
    function quote(string) {
        // If the string contains no control characters, no quote characters, and no
        // backslash characters, then we can safely slap some quotes around it.
        // Otherwise we must also replace the offending characters with safe escape
        // sequences.
        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string' ? c : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }
    function str(key, holder) {
        // Produce a string from holder[key].
        var i,
            // The loop counter.
            k,
            // The member key.
            v,
            // The member value.
            length, mind = gap, partial, value = holder[key];    // If the value has a toJSON method, call it to obtain a replacement value.
        // If the value has a toJSON method, call it to obtain a replacement value.
        if (value && typeof value === 'object' && typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }    // If we were called with a replacer function, then call the replacer to
        // obtain a replacement value.
        // If we were called with a replacer function, then call the replacer to
        // obtain a replacement value.
        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }    // What happens next depends on the value's type.
        // What happens next depends on the value's type.
        switch (typeof value) {
        case 'string':
            return quote(value);
        case 'number':
            // JSON numbers must be finite. Encode non-finite numbers as null.
            return isFinite(value) ? String(value) : 'null';
        case 'boolean':
        case 'null':
            // If the value is a boolean or null, convert it to a string. Note:
            // typeof null does not produce 'null'. The case is included here in
            // the remote chance that this gets fixed someday.
            return String(value);    // If the type is 'object', we might be dealing with an object or an array or
            // null.
        // If the type is 'object', we might be dealing with an object or an array or
        // null.
        case 'object':
            // Due to a specification blunder in ECMAScript, typeof null is 'object',
            // so watch out for that case.
            if (!value) {
                return 'null';
            }    // Make an array to hold the partial results of stringifying this object value.
            // Make an array to hold the partial results of stringifying this object value.
            gap += indent;
            partial = [];    // Is the value an array?
            // Is the value an array?
            if (Object.prototype.toString.apply(value) === '[object Array]') {
                // The value is an array. Stringify every element. Use null as a placeholder
                // for non-JSON values.
                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }    // Join all of the elements together, separated with commas, and wrap them in
                // brackets.
                // Join all of the elements together, separated with commas, and wrap them in
                // brackets.
                v = partial.length === 0 ? '[]' : gap ? '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']' : '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }    // If the replacer is an array, use it to select the members to be stringified.
            // If the replacer is an array, use it to select the members to be stringified.
            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === 'string') {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {
                // Otherwise, iterate through all of the keys in the object.
                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }    // Join all of the member texts together, separated with commas,
            // and wrap them in braces.
            // Join all of the member texts together, separated with commas,
            // and wrap them in braces.
            v = partial.length === 0 ? '{}' : gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}' : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }    // If the JSON object does not yet have a stringify method, give it one.
    // If the JSON object does not yet have a stringify method, give it one.
    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {
            // The stringify method takes a value and an optional replacer, and an optional
            // space parameter, and returns a JSON text. The replacer can be a function
            // that can replace values, or an array of strings that will select the keys.
            // A default replacer method can be provided. Use of the space parameter can
            // produce text that is more easily readable.
            var i;
            gap = '';
            indent = '';    // If the space parameter is a number, make an indent string containing that
            // many spaces.
            // If the space parameter is a number, make an indent string containing that
            // many spaces.
            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }    // If the space parameter is a string, it will be used as the indent string.
            } else if (typeof space === 'string') {
                indent = space;
            }    // If there is a replacer, it must be a function or an array.
            // Otherwise, throw an error.
            // If there is a replacer, it must be a function or an array.
            // Otherwise, throw an error.
            rep = replacer;
            if (replacer && typeof replacer !== 'function' && (typeof replacer !== 'object' || typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }    // Make a fake root object containing our value under the key of ''.
            // Return the result of stringifying the value.
            // Make a fake root object containing our value under the key of ''.
            // Return the result of stringifying the value.
            return str('', { '': value });
        };
    }    // If the JSON object does not yet have a parse method, give it one.
    // If the JSON object does not yet have a parse method, give it one.
    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {
            // The parse method takes a text and an optional reviver function, and returns
            // a JavaScript value if the text is a valid JSON text.
            var j;
            function walk(holder, key) {
                // The walk method is used to recursively walk the resulting structure so
                // that modifications can be made.
                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }    // Parsing happens in four stages. In the first stage, we replace certain
            // Unicode characters with escape sequences. JavaScript handles many characters
            // incorrectly, either silently deleting them, or treating them as line endings.
            // Parsing happens in four stages. In the first stage, we replace certain
            // Unicode characters with escape sequences. JavaScript handles many characters
            // incorrectly, either silently deleting them, or treating them as line endings.
            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }    // In the second stage, we run the text against regular expressions that look
            // for non-JSON patterns. We are especially concerned with '()' and 'new'
            // because they can cause invocation, and '=' because it can cause mutation.
            // But just to be safe, we want to reject all unexpected forms.
            // We split the second stage into 4 regexp operations in order to work around
            // crippling inefficiencies in IE's and Safari's regexp engines. First we
            // replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
            // replace all simple value tokens with ']' characters. Third, we delete all
            // open brackets that follow a colon or comma or that begin the text. Finally,
            // we look to see that the remaining characters are only whitespace or ']' or
            // ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.
            // In the second stage, we run the text against regular expressions that look
            // for non-JSON patterns. We are especially concerned with '()' and 'new'
            // because they can cause invocation, and '=' because it can cause mutation.
            // But just to be safe, we want to reject all unexpected forms.
            // We split the second stage into 4 regexp operations in order to work around
            // crippling inefficiencies in IE's and Safari's regexp engines. First we
            // replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
            // replace all simple value tokens with ']' characters. Third, we delete all
            // open brackets that follow a colon or comma or that begin the text. Finally,
            // we look to see that the remaining characters are only whitespace or ']' or
            // ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.
            if (/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
                // In the third stage we use the eval function to compile the text into a
                // JavaScript structure. The '{' operator is subject to a syntactic ambiguity
                // in JavaScript: it can begin a block or an object literal. We wrap the text
                // in parens to eliminate the ambiguity.
                j = eval('(' + text + ')');    // In the optional fourth stage, we recursively walk the new structure, passing
                // each name/value pair to a reviver function for possible transformation.
                // In the optional fourth stage, we recursively walk the new structure, passing
                // each name/value pair to a reviver function for possible transformation.
                return typeof reviver === 'function' ? walk({ '': j }, '') : j;
            }    // If the text is not JSON parseable, then a SyntaxError is thrown.
            // If the text is not JSON parseable, then a SyntaxError is thrown.
            throw new SyntaxError('JSON.parse');
        };
    }
}());    /*global T, window */
/*global T, window */
(function (exports) {
    var Matrix2D = function (a, b, c, d, e, f) {
        var ln = 0;
        var cl = 0;
        for (var i = 0; i < 9; i++) {
            ln = (i / 3 | 0) + 1;
            cl = i % 3 + 1;
            this['m' + ln + cl] = ln === cl ? 1 : 0;
        }
        if (typeof a === 'string') {
            var vals = a.match(/(-|\+)?\d*\.?\d+(?=,|\))/g);
            this.m11 = Number(vals[0]);
            this.m21 = Number(vals[1]);
            this.m12 = Number(vals[2]);
            this.m22 = Number(vals[3]);
            this.m13 = Number(vals[4]);
            this.m23 = Number(vals[5]);
        } else {
            this.m11 = a || this.m11;
            this.m21 = b || this.m21;
            this.m12 = c || this.m12;
            this.m22 = d || this.m22;
            this.m13 = e || this.m13;
            this.m23 = f || this.m23;
        }
    };
    Matrix2D.prototype = {
        multiplyBy: function (b) {
            var a = this;
            var c = new Matrix2D();
            c.m11 = a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31;
            c.m12 = a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32;
            c.m13 = a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33;
            c.m21 = a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31;
            c.m22 = a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32;
            c.m23 = a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33;
            c.m31 = a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31;
            c.m32 = a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32;
            c.m33 = a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33;
            return c;
        },
        clone: function () {
            var m = new Matrix2D();
            var l = 0;
            var c = 0;
            for (var i = 0; i < 9; i++) {
                l = (i / 3 | 0) + 1;
                c = i % 3 + 1;
                m['m' + l + c] = this['m' + l + c];
            }
        },
        toString: function () {
            var str = this.m11.toFixed(6);
            str += ', ' + this.m21.toFixed(6);
            str += ', ' + this.m12.toFixed(6);
            str += ', ' + this.m22.toFixed(6);
            str += ', ' + this.m13.toFixed(6);
            str += ', ' + this.m23.toFixed(6);
            return 'matrix(' + str + ')';
        }
    };    // Export
    // Export
    exports.Matrix2D = Matrix2D;
}(T));    /*global T, window, XDomainRequest, navigator */
/*global T, window, XDomainRequest, navigator */
(function (exports) {
    /**
         * Ajax class is used to make XHR requests.
         */
    function Ajax() {
    }
    Ajax.prototype = {
        /**
             * Creates a new XHR request
             * depending on the browser.
             *
             * @method createXhr
             * @param options {Object}
             */
        createXhr: function (options) {
            if (options) {
                this.options = options;
            }
            var ajax = this;    //Don't create XDR object if the url points to the origin
            //IE doesn't like to this
            //Don't create XDR object if the url points to the origin
            //IE doesn't like to this
            if (this.isIE() && !this.isSameWithOrigin(this.url)) {
                this.xhr = new XDomainRequest();
                this.xhr.onload = function () {
                    ajax.onComplete();
                };
                this.xhr.onerror = function () {
                    ajax.onError();
                };
                this.xhr.onprogress = function () {
                };
                this.xhr.ontimeout = function () {
                };
            } else {
                if (!!window.XMLHttpRequest) {
                    this.xhr = new window.XMLHttpRequest();    // Most browsers
                } else if (!!window.ActiveXObject) {
                    this.xhr = new window.ActiveXObject('Microsoft.XMLHTTP');    // Some IE
                }
            }
            if (!this.xhr) {
                throw new Error('Unable to create XHR object!');
            }
            this.xhr.onreadystatechange = function () {
                if (this.readyState === 4) {
                    //check Session Expired status code (501)
                    if (this.status === 501) {
                        ajax.onSessionTimeout();
                    } else {
                        if (this.status >= 400 || this.status === 0) {
                            ajax.onError();
                        } else {
                            ajax.onComplete();
                        }
                    }
                }
            };
            this.xhr.onprogress = function () {
            };
            this.xhr.ontimeout = function () {
            };
            return;
        },
        abort: function () {
            this.xhr.abort();
        },
        /**
             * Helper function for IE check.
             *
             * @method isIE
             */
        isIE: function () {
            if (navigator.appVersion.indexOf('MSIE') !== -1) {
                return true;
            }
            return false;
        },
        /**
             * Helper function to compare the domains of two URLs
             *
             * @method isSameWithOrigin
             */
        isSameWithOrigin: function (url) {
            var reg = new RegExp('^http(s)?://.*?(/|$)', 'i');
            var newHost = reg.exec(url)[0];
            var originHost = reg.exec(window.location.href)[0];
            return originHost === newHost;
        },
        attachIsAjaxParameter: function (url) {
            var isQueryString = false, i = 0;
            for (i = 0; i < url.length; i++) {
                if (url.charAt(i) === '?') {
                    url = url + '&isajax=true';
                    isQueryString = true;
                    break;
                }
            }
            if (!isQueryString) {
                url = url + '?isajax=true';
            }
            return url;
        },
        /**
             * Send XHR request to server.
             *
             * @param url
             * @param options
             */
        send: function (url, options) {
            this.options = options;
            this.url = this.attachIsAjaxParameter(url);
            if (!this.xhr) {
                this.createXhr();
            }
            this.xhr.open(options.method, this.url, true);    // IE needs timeout set after xhr.open()
            // IE needs timeout set after xhr.open()
            this.xhr.timeout = 2000000;    //Use credentials
            //Use credentials
            if (this.xhr.withCredentials !== undefined) {
                if (options.withCredentials) {
                    this.xhr.withCredentials = options.withCredentials;
                }
            }    // IE XDR doesn't support request headers
            // IE XDR doesn't support request headers
            var i = 0;
            if (this.xhr.setRequestHeader && options.headers) {
                for (i = 0; i < options.headers.length; i++) {
                    this.xhr.setRequestHeader(options.headers[i].name, options.headers[i].value);
                }
            }    //Data payload
            //Data payload
            if (options.data) {
                this.xhr.send(options.data);
            } else {
                this.xhr.send(null);
            }
            function timeoutHandler() {
                throw new Error('Loading timeout: ' + url);
            }
            return this;
        },
        onComplete: function () {
            if (this.options.on.success) {
                this.options.on.success(this.xhr.responseText);
            }
        },
        onError: function () {
            if (this.options.on.error) {
                this.options.on.error(this.xhr.responseText);
            }
        },
        onSessionTimeout: function () {
        }
    };    // Publish
    // Publish
    exports.Ajax = Ajax;
}(T));    /*global window*/
/*global window*/
(function (exports) {
    var Promise = function () {
        this.state = 'pending';
        this.value = null;
        this.deferred = null;
        this.rejected = null;
    };
    Promise.prototype = {
        then: function (deferred, rejected) {
            this._handle(deferred, rejected);
        },
        resolve: function (value) {
            this.value = value;
            this.state = 'resolved';
            if (this.deferred) {
                this._handle(this.deferred);
            }
        },
        reject: function (value) {
            if (value) {
                value = JSON.parse(value);
            }
            this.rejected(value);
        },
        _handle: function (deffered, rejected) {
            if (this.state === 'pending') {
                this.deferred = deffered;
                this.rejected = rejected;
                return;
            }
            deffered(this.value);
        }
    };
    exports.Promise = Promise;
}(T));    /*global T, document, window, navigator, setTimeout */
/*global T, document, window, navigator, setTimeout */
(function (exports) {
    var Util = {
        extend: function (dest) {
            var sources = Array.prototype.slice.call(arguments, 1), len = sources.length;
            var i, j, src;
            for (j = 0; j < len; j++) {
                src = sources[j] || {};
                for (i in src) {
                    if (src.hasOwnProperty(i)) {
                        dest[i] = src[i];
                    }
                }
            }
            return dest;
        },
        clone: function (dest) {
            var i, result = {};
            for (i in dest) {
                if (dest.hasOwnProperty(i)) {
                    result[i] = dest[i];
                }
            }
            return result;
        },
        cloneFunctions: function (dest) {
            var i, result = {};
            for (i in dest) {
                //if (dest.hasOwnProperty(i)) {
                if (typeof dest[i] === 'function') {
                    result[i] = function () {
                        var func = dest[i];
                        return function () {
                            return func.apply(this, arguments);
                        };
                    }();
                } else {
                    result[i] = dest[i];
                }    //}
            }
            return result;
        },
        inherit: function (cls, superCls) {
            var Construct = function () {
            };
            Construct.prototype = this.cloneFunctions(superCls.prototype);
            var proto = new Construct();
            cls.prototype = Util.extend(proto, cls.prototype);
            cls.prototype.constructor = cls;
            cls.base = superCls.prototype;
        },
        bind: function (fn, c) {
            return function () {
                return fn.apply(c || fn, arguments);
            };
        },
        template: function (str, data) {
            return str.replace(/\{ *([\w_]+) *\}/g, function (str, key) {
                var value = data[key];
                if (value === undefined) {
                    throw new Error('No value provided for variable ' + str);
                } else if (typeof value === 'function') {
                    value = value(data);
                }
                return value;
            });
        },
        trim: function (str) {
            return str.trim ? str.trim() : str.replace(/^\s+|\s+$/g, '');
        },
        defaults: function (obj, source) {
            if (!obj) {
                return source;
            }
            if (source) {
                var prop;
                for (prop in source) {
                    if (source.hasOwnProperty(prop)) {
                        if (obj[prop] === null || obj[prop] === undefined) {
                            obj[prop] = source[prop];
                        }
                    }
                }
            }
            return obj;
        },
        limitExecByInterval: function (fn, time, context) {
            var lock, execOnUnlock;
            return function wrapperFn() {
                var args = arguments;
                if (lock) {
                    execOnUnlock = true;
                    return;
                }
                lock = true;
                setTimeout(function () {
                    lock = false;
                    if (execOnUnlock) {
                        wrapperFn.apply(context, args);
                        execOnUnlock = false;
                    }
                }, time);
                fn.apply(context, args);
            };
        },
        isArray: function (obj) {
            return Object.prototype.toString.call(obj) === '[object Array]';
        },
        minMax: function (v1, v2, v3) {
            return Math.max(v1, Math.min(v2, v3));
        },
        maxMin: function (v1, v2, v3) {
            return Math.min(v1, Math.max(v2, v3));
        },
        floatEqual: function (v1, v2, epsilon) {
            epsilon = epsilon || 0.00001;
            return Math.abs(v1 - v2) <= epsilon;
        },
        stamp: function () {
            var lastId = 0, key = 'tibcoId';
            return function (obj) {
                obj[key] = obj[key] || ++lastId;
                return obj[key];
            };
        }(),
        hexToRgba: function (hex, alpha) {
            var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
            var rgba = result ? {
                r: parseInt(result[1], 16),
                g: parseInt(result[2], 16),
                b: parseInt(result[3], 16),
                a: alpha || 1
            } : null;
            if (rgba) {
                return 'rgba(' + rgba.r + ',' + rgba.g + ',' + rgba.b + ',' + rgba.a + ')';
            }
            return '';
        },
        // ## Get object class
        // Returns the class name of the argument or undefined if it's not a valid JavaScript object.
        getObjectClass: function (obj) {
            if (obj && obj.constructor && obj.constructor.toString) {
                var arr = obj.constructor.toString().match(/function\s*(\w+)/);
                if (arr && arr.length === 2) {
                    return arr[1];
                }
            }
            return undefined;
        },
        browserSupportsHistoryApi: function () {
            return !!(window.history && history.pushState);
        },
        handleBrowserNavigation: function (callback) {
            window.onpopstate = callback;
        },
        setBrowserHistory: function (content, noHistory) {
            var newUrl = window.location.href;
            if (!content || history.state && content === history.state.content || window.location.href.indexOf(content) !== -1) {
                return;
            }
            console.log('content: ', content);
            if (window.location.href.indexOf(content.charAt(0)) !== -1) {
                newUrl = window.location.href.substring(0, window.location.href.indexOf(content.charAt(0)));
            }
            if (noHistory) {
                history.replaceState({ content: content }, null, newUrl + content);
            } else {
                history.pushState({ content: content }, null, newUrl + content);
            }
        },
        setBrowserUrlHash: function (content) {
            if (!content) {
                return;
            }
            window.location.hash = content;
        },
        getBrowserUrlHash: function (content) {
            return window.location.hash;
        },
        geolocate: function (onSuccess, onError) {
            if (!navigator || !navigator.geolocation) {
                onError.call(this, null);
            }
            navigator.geolocation.getCurrentPosition(T.Util.bind(function (position) {
                onSuccess.call(this, position.coords.latitude, position.coords.longitude);
            }, this), T.Util.bind(function (error) {
                onError.call(this, error);
            }, this));
        },
        browserSupportsGeolocation: function () {
            return navigator && navigator.geolocation;
        },
        browserVersion: function () {
            var ua = navigator.userAgent.toLowerCase();
            return {
                ielt9: !!window.ActiveXObject && !document.addEventListener,
                webkit: ua.indexOf('webkit') !== -1,
                chrome: ua.indexOf('chrome') !== -1,
                android: ua.indexOf('android') !== -1
            };
        }(),
        // Extension methods
        initOldIE: function () {
            if (!Array.prototype.indexOf) {
                Array.prototype.indexOf = function (obj) {
                    var i = 0;
                    for (i = 0; i < this.length; i++) {
                        if (this[i] === obj) {
                            return i;
                        }
                    }
                    return -1;
                };
            }
        }(),
        numberToRad: function () {
            if (typeof Number.prototype.toRad === 'undefined') {
                Number.prototype.toRad = function () {
                    return this * Math.PI / 180;
                };
            }
        }()
    };    // Exports
    // Exports
    exports.Util = Util;
}(T));    /*global T, document, window */
/*global T, document, window */
(function (exports) {
    var DomUtil = {
        SVG_NS: 'http://www.w3.org/2000/svg',
        get: function (id) {
            return document.getElementById(id);
        },
        getNodesByClass: function (className) {
            return document.querySelectorAll('.' + className);
        },
        create: function (tagName, className, parent, id) {
            var element = document.createElement(tagName);
            element.className = className || '';
            if (id) {
                element.id = id;
            }
            if (parent) {
                parent.appendChild(element);
            }
            return element;
        },
        remove: function (el) {
            if (!el) {
                return;
            }
            el.parentNode.removeChild(el);
        },
        html: function (el, text) {
            if (!el) {
                return;
            }
            $(el).html(text);
        },
        hasClass: function (el, className) {
            return el.className.length > 0 && new RegExp('(^|\\s)' + className + '(\\s|$)').test(el.className);
        },
        addClass: function (el, className) {
            if (!DomUtil.hasClass(el, className)) {
                el.className += (el.className ? ' ' : '') + className;
            }
        },
        removeClass: function (el, className) {
            el.className = T.Util.trim((' ' + el.className + ' ').replace(' ' + className + ' ', ' '));
        },
        getStyle: function (el, style) {
            var value = el.style[style];
            if (!value && el.currentStyle) {
                value = el.currentStyle[style];
            }
            if ((!value || value === 'auto') && document.defaultView) {
                var css = document.defaultView.getComputedStyle(el, null);
                value = css ? css[style] : null;
            }
            return value === 'auto' ? null : value;
        },
        setOpacity: function (el, value) {
            el.style.msFilter = 'progid:DXImageTransform.Microsoft.Alpha(Opacity=' + value * 100 + ')';
            if (!T.Util.browserVersion.ielt9) {
                el.style.filter = 'alpha(opacity=' + value * 100 + ')';
            }
            el.style.opacity = value;
            el.style.mozOpacity = value;
        },
        position: function (el, point) {
            if (point) {
                el.style.left = point.x + 'px';
                el.style.top = point.y + 'px';
                el.tibcoPosition = point;
            }
            return el.tibcoPosition;
        },
        size: function (el, size) {
            if (size) {
                el.style.width = size.x + 'px';
                el.style.height = size.y + 'px';
            }
            return new T.Point(parseInt(el.style.width, 10), parseInt(el.style.height, 10));
        },
        calculateElementClientOffset: function (element) {
            var offsetLeft = 0;
            var offsetTop = 0;
            var bounds = element.getBoundingClientRect();
            offsetLeft = bounds.left;
            offsetTop = bounds.top;
            return {
                left: offsetLeft,
                top: offsetTop
            };
        },
        createSvgElement: function (name) {
            return document.createElementNS(DomUtil.SVG_NS, name);
        },
        createVmlElement: function (name) {
            var x = document.createElement(name);
            x.setAttribute('xmlns', 'urn:schemas-microsoft.com:vml');
            return x;
        },
        getNodeIndex: function (node, parent) {
            var nodes = parent ? parent.childNodes : node.parentNode.childNodes, i;
            for (i = 0; i < nodes.length; i++) {
                if (node === nodes[i]) {
                    return i;
                }
            }
            return -1;
        },
        addStyleNode: function (css) {
            var head = document.getElementsByTagName('head')[0];
            var style = document.createElement('style');
            style.type = 'text/css';
            if (style.styleSheet) {
                style.styleSheet.cssText = css;
            } else {
                style.appendChild(document.createTextNode(css));
            }
            head.appendChild(style);
        },
        hide: function (el) {
            el.style.display = 'none';
        },
        show: function (el, display) {
            el.style.display = display || 'block';
        },
        getElementInsideContainer: function (parentId, childId) {
            var elm = document.getElementById(childId);
            var parent = elm ? elm.parentNode : {};
            return parent.id && parent.id === parentId ? elm : {};
        },
        disableSelection: function (element) {
            element.style.mozUserSelect = 'none';
            element.style.khtmlUserSelect = 'none';
            element.style.webkitUserSelect = 'none';
            element.style.msUserSelect = 'none';
            element.style.userSelect = 'none';
            element.dtraggable = false;
            element.onmousedown = function (event) {
                if (event) {
                    if (event.preventDefault) {
                        event.preventDefault();
                    } else {
                        event.returnValue = false;
                    }
                }
                return false;
            };
        },
        nodeCollectionToArray: function (collection) {
            var a = [], i = 0;
            try {
                a = Array.prototype.slice.call(collection);
            } catch (e) {
                for (i = 0; i < collection.length; i++) {
                    a.push(collection[i]);
                }
            }
            return a;
        }
    };    // Exports
    // Exports
    exports.DomUtil = DomUtil;
}(T));    /*global T, document, window */
/*global T, document, window */
(function (exports) {
    var DomEventUtil = {
        on: function (obj, type, fn, context) {
            var id = T.Util.stamp(fn), key = 'tibco_' + type + id, handler, originalHandler, newType;
            if (obj[key]) {
                return this;
            }
            handler = function (e) {
                return fn.call(context || obj, e || DomEventUtil.getEvent());
            };
            if (obj.addEventListener !== undefined) {
                if (type === 'mousewheel') {
                    obj.addEventListener('DOMMouseScroll', handler, false);
                    obj.addEventListener(type, handler, false);
                } else if (type === 'mouseenter' || type === 'mouseleave') {
                    originalHandler = handler;
                    newType = type === 'mouseenter' ? 'mouseover' : 'mouseout';
                    handler = function (e) {
                        if (!DomEventUtil.checkMouse(obj, e)) {
                            return;
                        }
                        return originalHandler(e);
                    };
                    obj.addEventListener(newType, handler, false);
                } else {
                    obj.addEventListener(type, handler, false);
                }
            } else if (obj.attachEvent !== undefined) {
                obj.attachEvent('on' + type, handler);
            }
            obj[key] = handler;
            return this;
        },
        off: function (obj, type, fn) {
            // (HTMLElement, String, Function)
            var id = T.Util.stamp(fn), key = 'tibco_' + type + id, handler = obj[key];
            if (!handler) {
                return this;
            }
            if (obj.removeEventListener !== undefined) {
                if (type === 'mousewheel') {
                    obj.removeEventListener('DOMMouseScroll', handler, false);
                    obj.removeEventListener(type, handler, false);
                } else if (type === 'mouseenter' || type === 'mouseleave') {
                    obj.removeEventListener(type === 'mouseenter' ? 'mouseover' : 'mouseout', handler, false);
                } else {
                    obj.removeEventListener(type, handler, false);
                }
            } else if (obj.detachEvent !== undefined) {
                obj.detachEvent('on' + type, handler);
            }
            obj[key] = null;
            return this;
        },
        stopPropagation: function (e) {
            if (e.stopPropagation) {
                e.stopPropagation();
            } else {
                e.cancelBubble = true;
            }
            return this;
        },
        preventDefault: function (e) {
            if (e.preventDefault) {
                e.preventDefault();
            } else {
                e.returnValue = false;
            }
            return this;
        },
        stop: function (e) {
            return DomEventUtil.preventDefault(e).stopPropagation(e);
        },
        getWheelDelta: function (e) {
            var delta = 0;
            if (e.wheelDelta) {
                delta = e.wheelDelta / 120;
            }
            if (e.detail) {
                delta = -e.detail / 3;
            }
            return delta;
        },
        checkMouse: function (el, e) {
            var related = e.relatedTarget;
            if (!related) {
                return true;
            }
            try {
                while (related && related !== el) {
                    related = related.parentNode;
                }
            } catch (err) {
                return false;
            }
            return related !== el;
        },
        getEvent: function () {
            var e = window.event;
            if (!e) {
                var caller = arguments.callee.caller;
                while (caller) {
                    e = caller['arguments'][0];
                    if (e && window.Event === e.constructor) {
                        break;
                    }
                    caller = caller.caller;
                }
            }
            return e;
        }
    };    // Exports
    // Exports
    exports.DomEventUtil = DomEventUtil;
}(T));    /*global T, document, window */
/*global T, document, window */
(function (exports) {
    /**
         * DragUtil Class
         *
         * Events:
         *  - drag-start
         *  - drag
         *  - drag-end
         *
         * @param element
         * @param dragTarget
         */
    var DragUtil = function (element, dragTarget, options) {
        this.element = element;
        this.events = new T.EventDispatcher();
        this.dragTarget = dragTarget || element;
        this.options = T.Util.defaults(options, this.options);
    };
    DragUtil.prototype = {
        options: {},
        enable: function () {
            if (this.isEnable) {
                return;
            }
            T.DomEventUtil.on(this.dragTarget, 'mousedown', this.onDown, this);
            this.isEnable = true;
        },
        disable: function () {
            if (!this.isEnable) {
                return;
            }
            T.DomEventUtil.off(this.dragTarget, 'mousedown', this.onDown, this);
            this.isEnable = false;
        },
        onDown: function (evt) {
            T.DomEventUtil.stop(evt);
            this.moved = false;
            this.startPoint = new T.Point(evt.clientX, evt.clientY);
            this.startPos = this.newPos = T.DomUtil.position(this.element);
            T.DomEventUtil.on(document, 'mousemove', this.onMove, this);
            T.DomEventUtil.on(document, 'mouseup', this.onUp, this);
            this.events.fire('before-drag');
        },
        onMove: function (evt) {
            var newPoint = new T.Point(evt.clientX, evt.clientY), offset = newPoint.substract(this.startPoint);
            T.DomEventUtil.preventDefault(evt);
            if (!this.moved) {
                this.events.fire('drag-start');
                this.moved = true;
                this.startPos = T.DomUtil.position(this.element).substract(offset);
            }
            this.newPos = this.startPos.add(offset);
            this.moving = true;
            if (this.options.limit) {
                if (this.options.limit.minX !== undefined && this.options.limit.minX > this.newPos.x) {
                    this.newPos.x = this.options.limit.minX;
                }
                if (this.options.limit.maxX !== undefined && this.options.limit.maxX < this.newPos.x) {
                    this.newPos.x = this.options.limit.maxX;
                }
                if (this.options.limit.minY !== undefined && this.options.limit.minY > this.newPos.y) {
                    this.newPos.y = this.options.limit.minY;
                }
                if (this.options.limit.maxY !== undefined && this.options.limit.maxY < this.newPos.y) {
                    this.newPos.y = this.options.limit.maxY;
                }
            }
            if (this.options.skipInterval) {
                if (this.options.skipInterval.y) {
                    this.newPos.y = this.newPos.y - this.newPos.y % this.options.skipInterval.y;
                }
                if (this.options.skipInterval.x) {
                    this.newPos.x = this.newPos.x - this.newPos.x % this.options.skipInterval.x;
                }
            }    // Apply positions
            // Apply positions
            T.DomUtil.position(this.element, this.newPos);    //            T.AnimUtil.transition(this.element, 0);
            //            T.AnimUtil.translate(this.element, this.newPos.x, this.newPos.y);
            // Fire drag
            //            T.AnimUtil.transition(this.element, 0);
            //            T.AnimUtil.translate(this.element, this.newPos.x, this.newPos.y);
            // Fire drag
            this.events.fire('drag', { offset: offset });
        },
        onUp: function (evt) {
            T.DomEventUtil.off(document, 'mousemove', this.onMove, this);
            T.DomEventUtil.off(document, 'mouseup', this.onUp, this);
            this.events.fire('drag-end');
            this.moving = false;    // why true? changed to false
            // why true? changed to false
            this.moved = false;
        }
    };    // Exports
    // Exports
    exports.DragUtil = DragUtil;
}(T));    /*global window, T, document */
/*global window, T, document */
(function (exports) {
    var AnimUtil = {
        getTransform: function (element) {
            var style = getComputedStyle(element);
            var transform = style.msTransform || style.webkitTransform || style.transform || '';
            if (transform.search('matrix') !== -1) {
                return transform;
            }
            return null;
        },
        setMatrix: function (element, matrixStr) {
            if (element.style.msTransform) {
                element.style.msTransform = matrixStr;
            } else {
                element.style.webkitTransform = matrixStr;
                element.style.transform = matrixStr;
            }
        },
        onTransitionEnd: function (element, callback) {
            element.removeEventListener('webkitTransitionEnd', element.lastCallback);
            element.addEventListener('webkitTransitionEnd', callback, false);
            element.removeEventListener('transitionend', element.lastCallback);
            element.addEventListener('transitionend', callback, false);
            element.removeEventListener('otransitionend', element.lastCallback);
            element.addEventListener('otransitionend', callback, false);
            element.removeEventListener('MSTransitionEnd', element.lastCallback);
            element.addEventListener('MSTransitionEnd', callback, false);
            element.lastCallback = callback;
        },
        transition: function (element, time, pos, type) {
            if (type) {
                element.style.webkitTransition = type + ' ' + time + 's';
                element.style.transition = type + ' ' + time + 's';
                return;
            }
            pos = pos || new T.Point(0, 0);
            element.style.webkitTransition = '-webkit-transform ' + time + 's cubic-bezier(.17,.67,1,.84)';
            element.style.transition = 'transform ' + time + 's cubic-bezier(.17,.67,1,.84)';    // element.style.webkitTransition = '-webkit-transform ' + time + 's';
            // element.style.transition = 'transform ' + time + 's';
            // element.style.webkitTransition = '-webkit-transform ' + time + 's';
            // element.style.transition = 'transform ' + time + 's';
            element.style.webkitTransformOrigin = pos.x + 'px ' + pos.y + 'px';
            element.style.transformOrigin = pos.x + 'px ' + pos.y + 'px';
            element.style.msTransformOrigin = pos.x + 'px ' + pos.y + 'px';
        },
        translate: function (element, x, y) {
            if (element.style.msTransform) {
                element.style.msTransform = 'translate(' + x + 'px,' + y + 'px)' + element.style.msTransform;
            } else {
                element.style.webkitTransform = 'translate(' + x + 'px,' + y + 'px)' + element.style.webkitTransform;
                element.style.transform = 'translate(' + x + 'px,' + y + 'px)' + element.style.transform;
            }
        },
        scale: function (element, x, y) {
            if (element.style.msTransform) {
                element.style.msTransform = 'scale(' + x + ',' + y + ')' + element.style.msTransform;
            } else {
                element.style.webkitTransform = 'scale(' + x + ',' + y + ')' + element.style.webkitTransform;
                element.style.transform = 'scale(' + x + ',' + y + ')' + element.style.transform;
            }
        },
        reset: function (element) {
            element.style.webkitTransition = '-webkit-transform 0s';
            element.style.transition = 'transform 0s';
            if (element.style.msTransform) {
                element.style.msTransform = 'translate(0,0)';
            } else {
                element.style.webkitTransform = 'translate(0,0)';
                element.style.transform = 'translate(0,0)';
            }
            if (element.style.msTransform) {
                element.style.msTransform += 'scale(1,1)';
            } else {
                element.style.webkitTransform += 'scale(1,1)';
                element.style.transform += 'scale(1,1)';
            }
        },
        canAnim: function () {
            var s = document.createElement('p').style, supportsTransitions = 'transition' in s || 'WebkitTransition' in s || 'MozTransition' in s || 'msTransition' in s || 'OTransition' in s;
            return supportsTransitions;
        }
    };    // Exports
    // Exports
    exports.AnimUtil = AnimUtil;
}(T));
!function (e) {
    var f;
    'undefined' != typeof window ? f = window : 'undefined' != typeof global ? f = global : 'undefined' != typeof self && (f = self), f.proj4 = e();
}(function () {
    var define, module, exports;
    return function e(t, n, r) {
        function s(o, u) {
            if (!n[o]) {
                if (!t[o]) {
                    var a = typeof require == 'function' && require;
                    if (!u && a)
                        return a(o, !0);
                    if (i)
                        return i(o, !0);
                    throw new Error('Cannot find module \'' + o + '\'');
                }
                var f = n[o] = { exports: {} };
                t[o][0].call(f.exports, function (e) {
                    var n = t[o][1][e];
                    return s(n ? n : e);
                }, f, f.exports, e, t, n, r);
            }
            return n[o].exports;
        }
        var i = typeof require == 'function' && require;
        for (var o = 0; o < r.length; o++)
            s(r[o]);
        return s;
    }({
        1: [
            function (_dereq_, module, exports) {
                var mgrs = _dereq_('mgrs');
                function Point(x, y, z) {
                    if (!(this instanceof Point)) {
                        return new Point(x, y, z);
                    }
                    if (Array.isArray(x)) {
                        this.x = x[0];
                        this.y = x[1];
                        this.z = x[2] || 0;
                    } else if (typeof x === 'object') {
                        this.x = x.x;
                        this.y = x.y;
                        this.z = x.z || 0;
                    } else if (typeof x === 'string' && typeof y === 'undefined') {
                        var coords = x.split(',');
                        this.x = parseFloat(coords[0], 10);
                        this.y = parseFloat(coords[1], 10);
                        this.z = parseFloat(coords[2], 10) || 0;
                    } else {
                        this.x = x;
                        this.y = y;
                        this.z = z || 0;
                    }
                    console.warn('proj4.Point will be removed in version 3, use proj4.toPoint');
                }
                Point.fromMGRS = function (mgrsStr) {
                    return new Point(mgrs.toPoint(mgrsStr));
                };
                Point.prototype.toMGRS = function (accuracy) {
                    return mgrs.forward([
                        this.x,
                        this.y
                    ], accuracy);
                };
                module.exports = Point;
            },
            { 'mgrs': 66 }
        ],
        2: [
            function (_dereq_, module, exports) {
                var parseCode = _dereq_('./parseCode');
                var extend = _dereq_('./extend');
                var projections = _dereq_('./projections');
                var deriveConstants = _dereq_('./deriveConstants');
                function Projection(srsCode, callback) {
                    if (!(this instanceof Projection)) {
                        return new Projection(srsCode);
                    }
                    callback = callback || function (error) {
                        if (error) {
                            throw error;
                        }
                    };
                    var json = parseCode(srsCode);
                    if (typeof json !== 'object') {
                        callback(srsCode);
                        return;
                    }
                    var modifiedJSON = deriveConstants(json);
                    var ourProj = Projection.projections.get(modifiedJSON.projName);
                    if (ourProj) {
                        extend(this, modifiedJSON);
                        extend(this, ourProj);
                        this.init();
                        callback(null, this);
                    } else {
                        callback(srsCode);
                    }
                }
                Projection.projections = projections;
                Projection.projections.start();
                module.exports = Projection;
            },
            {
                './deriveConstants': 32,
                './extend': 33,
                './parseCode': 36,
                './projections': 38
            }
        ],
        3: [
            function (_dereq_, module, exports) {
                module.exports = function (crs, denorm, point) {
                    var xin = point.x, yin = point.y, zin = point.z || 0;
                    var v, t, i;
                    for (i = 0; i < 3; i++) {
                        if (denorm && i === 2 && point.z === undefined) {
                            continue;
                        }
                        if (i === 0) {
                            v = xin;
                            t = 'x';
                        } else if (i === 1) {
                            v = yin;
                            t = 'y';
                        } else {
                            v = zin;
                            t = 'z';
                        }
                        switch (crs.axis[i]) {
                        case 'e':
                            point[t] = v;
                            break;
                        case 'w':
                            point[t] = -v;
                            break;
                        case 'n':
                            point[t] = v;
                            break;
                        case 's':
                            point[t] = -v;
                            break;
                        case 'u':
                            if (point[t] !== undefined) {
                                point.z = v;
                            }
                            break;
                        case 'd':
                            if (point[t] !== undefined) {
                                point.z = -v;
                            }
                            break;
                        default:
                            //console.log("ERROR: unknow axis ("+crs.axis[i]+") - check definition of "+crs.projName);
                            return null;
                        }
                    }
                    return point;
                };
            },
            {}
        ],
        4: [
            function (_dereq_, module, exports) {
                var HALF_PI = Math.PI / 2;
                var sign = _dereq_('./sign');
                module.exports = function (x) {
                    return Math.abs(x) < HALF_PI ? x : x - sign(x) * Math.PI;
                };
            },
            { './sign': 21 }
        ],
        5: [
            function (_dereq_, module, exports) {
                var TWO_PI = Math.PI * 2;
                var sign = _dereq_('./sign');
                module.exports = function (x) {
                    return Math.abs(x) < Math.PI ? x : x - sign(x) * TWO_PI;
                };
            },
            { './sign': 21 }
        ],
        6: [
            function (_dereq_, module, exports) {
                module.exports = function (x) {
                    if (Math.abs(x) > 1) {
                        x = x > 1 ? 1 : -1;
                    }
                    return Math.asin(x);
                };
            },
            {}
        ],
        7: [
            function (_dereq_, module, exports) {
                module.exports = function (x) {
                    return 1 - 0.25 * x * (1 + x / 16 * (3 + 1.25 * x));
                };
            },
            {}
        ],
        8: [
            function (_dereq_, module, exports) {
                module.exports = function (x) {
                    return 0.375 * x * (1 + 0.25 * x * (1 + 0.46875 * x));
                };
            },
            {}
        ],
        9: [
            function (_dereq_, module, exports) {
                module.exports = function (x) {
                    return 0.05859375 * x * x * (1 + 0.75 * x);
                };
            },
            {}
        ],
        10: [
            function (_dereq_, module, exports) {
                module.exports = function (x) {
                    return x * x * x * (35 / 3072);
                };
            },
            {}
        ],
        11: [
            function (_dereq_, module, exports) {
                module.exports = function (a, e, sinphi) {
                    var temp = e * sinphi;
                    return a / Math.sqrt(1 - temp * temp);
                };
            },
            {}
        ],
        12: [
            function (_dereq_, module, exports) {
                module.exports = function (ml, e0, e1, e2, e3) {
                    var phi;
                    var dphi;
                    phi = ml / e0;
                    for (var i = 0; i < 15; i++) {
                        dphi = (ml - (e0 * phi - e1 * Math.sin(2 * phi) + e2 * Math.sin(4 * phi) - e3 * Math.sin(6 * phi))) / (e0 - 2 * e1 * Math.cos(2 * phi) + 4 * e2 * Math.cos(4 * phi) - 6 * e3 * Math.cos(6 * phi));
                        phi += dphi;
                        if (Math.abs(dphi) <= 1e-10) {
                            return phi;
                        }
                    }    //..reportError("IMLFN-CONV:Latitude failed to converge after 15 iterations");
                    //..reportError("IMLFN-CONV:Latitude failed to converge after 15 iterations");
                    return NaN;
                };
            },
            {}
        ],
        13: [
            function (_dereq_, module, exports) {
                var HALF_PI = Math.PI / 2;
                module.exports = function (eccent, q) {
                    var temp = 1 - (1 - eccent * eccent) / (2 * eccent) * Math.log((1 - eccent) / (1 + eccent));
                    if (Math.abs(Math.abs(q) - temp) < 0.000001) {
                        if (q < 0) {
                            return -1 * HALF_PI;
                        } else {
                            return HALF_PI;
                        }
                    }    //var phi = 0.5* q/(1-eccent*eccent);
                    //var phi = 0.5* q/(1-eccent*eccent);
                    var phi = Math.asin(0.5 * q);
                    var dphi;
                    var sin_phi;
                    var cos_phi;
                    var con;
                    for (var i = 0; i < 30; i++) {
                        sin_phi = Math.sin(phi);
                        cos_phi = Math.cos(phi);
                        con = eccent * sin_phi;
                        dphi = Math.pow(1 - con * con, 2) / (2 * cos_phi) * (q / (1 - eccent * eccent) - sin_phi / (1 - con * con) + 0.5 / eccent * Math.log((1 - con) / (1 + con)));
                        phi += dphi;
                        if (Math.abs(dphi) <= 1e-10) {
                            return phi;
                        }
                    }    //console.log("IQSFN-CONV:Latitude failed to converge after 30 iterations");
                    //console.log("IQSFN-CONV:Latitude failed to converge after 30 iterations");
                    return NaN;
                };
            },
            {}
        ],
        14: [
            function (_dereq_, module, exports) {
                module.exports = function (e0, e1, e2, e3, phi) {
                    return e0 * phi - e1 * Math.sin(2 * phi) + e2 * Math.sin(4 * phi) - e3 * Math.sin(6 * phi);
                };
            },
            {}
        ],
        15: [
            function (_dereq_, module, exports) {
                module.exports = function (eccent, sinphi, cosphi) {
                    var con = eccent * sinphi;
                    return cosphi / Math.sqrt(1 - con * con);
                };
            },
            {}
        ],
        16: [
            function (_dereq_, module, exports) {
                var HALF_PI = Math.PI / 2;
                module.exports = function (eccent, ts) {
                    var eccnth = 0.5 * eccent;
                    var con, dphi;
                    var phi = HALF_PI - 2 * Math.atan(ts);
                    for (var i = 0; i <= 15; i++) {
                        con = eccent * Math.sin(phi);
                        dphi = HALF_PI - 2 * Math.atan(ts * Math.pow((1 - con) / (1 + con), eccnth)) - phi;
                        phi += dphi;
                        if (Math.abs(dphi) <= 1e-10) {
                            return phi;
                        }
                    }    //console.log("phi2z has NoConvergence");
                    //console.log("phi2z has NoConvergence");
                    return -9999;
                };
            },
            {}
        ],
        17: [
            function (_dereq_, module, exports) {
                var C00 = 1;
                var C02 = 0.25;
                var C04 = 0.046875;
                var C06 = 0.01953125;
                var C08 = 0.01068115234375;
                var C22 = 0.75;
                var C44 = 0.46875;
                var C46 = 0.013020833333333334;
                var C48 = 0.007120768229166667;
                var C66 = 0.3645833333333333;
                var C68 = 0.005696614583333333;
                var C88 = 0.3076171875;
                module.exports = function (es) {
                    var en = [];
                    en[0] = C00 - es * (C02 + es * (C04 + es * (C06 + es * C08)));
                    en[1] = es * (C22 - es * (C04 + es * (C06 + es * C08)));
                    var t = es * es;
                    en[2] = t * (C44 - es * (C46 + es * C48));
                    t *= es;
                    en[3] = t * (C66 - es * C68);
                    en[4] = t * es * C88;
                    return en;
                };
            },
            {}
        ],
        18: [
            function (_dereq_, module, exports) {
                var pj_mlfn = _dereq_('./pj_mlfn');
                var EPSLN = 1e-10;
                var MAX_ITER = 20;
                module.exports = function (arg, es, en) {
                    var k = 1 / (1 - es);
                    var phi = arg;
                    for (var i = MAX_ITER; i; --i) {
                        /* rarely goes over 2 iterations */
                        var s = Math.sin(phi);
                        var t = 1 - es * s * s;    //t = this.pj_mlfn(phi, s, Math.cos(phi), en) - arg;
                        //phi -= t * (t * Math.sqrt(t)) * k;
                        //t = this.pj_mlfn(phi, s, Math.cos(phi), en) - arg;
                        //phi -= t * (t * Math.sqrt(t)) * k;
                        t = (pj_mlfn(phi, s, Math.cos(phi), en) - arg) * (t * Math.sqrt(t)) * k;
                        phi -= t;
                        if (Math.abs(t) < EPSLN) {
                            return phi;
                        }
                    }    //..reportError("cass:pj_inv_mlfn: Convergence error");
                    //..reportError("cass:pj_inv_mlfn: Convergence error");
                    return phi;
                };
            },
            { './pj_mlfn': 19 }
        ],
        19: [
            function (_dereq_, module, exports) {
                module.exports = function (phi, sphi, cphi, en) {
                    cphi *= sphi;
                    sphi *= sphi;
                    return en[0] * phi - cphi * (en[1] + sphi * (en[2] + sphi * (en[3] + sphi * en[4])));
                };
            },
            {}
        ],
        20: [
            function (_dereq_, module, exports) {
                module.exports = function (eccent, sinphi) {
                    var con;
                    if (eccent > 1e-7) {
                        con = eccent * sinphi;
                        return (1 - eccent * eccent) * (sinphi / (1 - con * con) - 0.5 / eccent * Math.log((1 - con) / (1 + con)));
                    } else {
                        return 2 * sinphi;
                    }
                };
            },
            {}
        ],
        21: [
            function (_dereq_, module, exports) {
                module.exports = function (x) {
                    return x < 0 ? -1 : 1;
                };
            },
            {}
        ],
        22: [
            function (_dereq_, module, exports) {
                module.exports = function (esinp, exp) {
                    return Math.pow((1 - esinp) / (1 + esinp), exp);
                };
            },
            {}
        ],
        23: [
            function (_dereq_, module, exports) {
                module.exports = function (array) {
                    var out = {
                        x: array[0],
                        y: array[1]
                    };
                    if (array.length > 2) {
                        out.z = array[2];
                    }
                    if (array.length > 3) {
                        out.m = array[3];
                    }
                    return out;
                };
            },
            {}
        ],
        24: [
            function (_dereq_, module, exports) {
                var HALF_PI = Math.PI / 2;
                module.exports = function (eccent, phi, sinphi) {
                    var con = eccent * sinphi;
                    var com = 0.5 * eccent;
                    con = Math.pow((1 - con) / (1 + con), com);
                    return Math.tan(0.5 * (HALF_PI - phi)) / con;
                };
            },
            {}
        ],
        25: [
            function (_dereq_, module, exports) {
                exports.wgs84 = {
                    towgs84: '0,0,0',
                    ellipse: 'WGS84',
                    datumName: 'WGS84'
                };
                exports.ch1903 = {
                    towgs84: '674.374,15.056,405.346',
                    ellipse: 'bessel',
                    datumName: 'swiss'
                };
                exports.ggrs87 = {
                    towgs84: '-199.87,74.79,246.62',
                    ellipse: 'GRS80',
                    datumName: 'Greek_Geodetic_Reference_System_1987'
                };
                exports.nad83 = {
                    towgs84: '0,0,0',
                    ellipse: 'GRS80',
                    datumName: 'North_American_Datum_1983'
                };
                exports.nad27 = {
                    nadgrids: '@conus,@alaska,@ntv2_0.gsb,@ntv1_can.dat',
                    ellipse: 'clrk66',
                    datumName: 'North_American_Datum_1927'
                };
                exports.potsdam = {
                    towgs84: '606.0,23.0,413.0',
                    ellipse: 'bessel',
                    datumName: 'Potsdam Rauenberg 1950 DHDN'
                };
                exports.carthage = {
                    towgs84: '-263.0,6.0,431.0',
                    ellipse: 'clark80',
                    datumName: 'Carthage 1934 Tunisia'
                };
                exports.hermannskogel = {
                    towgs84: '653.0,-212.0,449.0',
                    ellipse: 'bessel',
                    datumName: 'Hermannskogel'
                };
                exports.ire65 = {
                    towgs84: '482.530,-130.596,564.557,-1.042,-0.214,-0.631,8.15',
                    ellipse: 'mod_airy',
                    datumName: 'Ireland 1965'
                };
                exports.rassadiran = {
                    towgs84: '-133.63,-157.5,-158.62',
                    ellipse: 'intl',
                    datumName: 'Rassadiran'
                };
                exports.nzgd49 = {
                    towgs84: '59.47,-5.04,187.44,0.47,-0.1,1.024,-4.5993',
                    ellipse: 'intl',
                    datumName: 'New Zealand Geodetic Datum 1949'
                };
                exports.osgb36 = {
                    towgs84: '446.448,-125.157,542.060,0.1502,0.2470,0.8421,-20.4894',
                    ellipse: 'airy',
                    datumName: 'Airy 1830'
                };
                exports.s_jtsk = {
                    towgs84: '589,76,480',
                    ellipse: 'bessel',
                    datumName: 'S-JTSK (Ferro)'
                };
                exports.beduaram = {
                    towgs84: '-106,-87,188',
                    ellipse: 'clrk80',
                    datumName: 'Beduaram'
                };
                exports.gunung_segara = {
                    towgs84: '-403,684,41',
                    ellipse: 'bessel',
                    datumName: 'Gunung Segara Jakarta'
                };
                exports.rnb72 = {
                    towgs84: '106.869,-52.2978,103.724,-0.33657,0.456955,-1.84218,1',
                    ellipse: 'intl',
                    datumName: 'Reseau National Belge 1972'
                };
            },
            {}
        ],
        26: [
            function (_dereq_, module, exports) {
                exports.MERIT = {
                    a: 6378137,
                    rf: 298.257,
                    ellipseName: 'MERIT 1983'
                };
                exports.SGS85 = {
                    a: 6378136,
                    rf: 298.257,
                    ellipseName: 'Soviet Geodetic System 85'
                };
                exports.GRS80 = {
                    a: 6378137,
                    rf: 298.257222101,
                    ellipseName: 'GRS 1980(IUGG, 1980)'
                };
                exports.IAU76 = {
                    a: 6378140,
                    rf: 298.257,
                    ellipseName: 'IAU 1976'
                };
                exports.airy = {
                    a: 6377563.396,
                    b: 6356256.91,
                    ellipseName: 'Airy 1830'
                };
                exports.APL4 = {
                    a: 6378137,
                    rf: 298.25,
                    ellipseName: 'Appl. Physics. 1965'
                };
                exports.NWL9D = {
                    a: 6378145,
                    rf: 298.25,
                    ellipseName: 'Naval Weapons Lab., 1965'
                };
                exports.mod_airy = {
                    a: 6377340.189,
                    b: 6356034.446,
                    ellipseName: 'Modified Airy'
                };
                exports.andrae = {
                    a: 6377104.43,
                    rf: 300,
                    ellipseName: 'Andrae 1876 (Den., Iclnd.)'
                };
                exports.aust_SA = {
                    a: 6378160,
                    rf: 298.25,
                    ellipseName: 'Australian Natl & S. Amer. 1969'
                };
                exports.GRS67 = {
                    a: 6378160,
                    rf: 298.247167427,
                    ellipseName: 'GRS 67(IUGG 1967)'
                };
                exports.bessel = {
                    a: 6377397.155,
                    rf: 299.1528128,
                    ellipseName: 'Bessel 1841'
                };
                exports.bess_nam = {
                    a: 6377483.865,
                    rf: 299.1528128,
                    ellipseName: 'Bessel 1841 (Namibia)'
                };
                exports.clrk66 = {
                    a: 6378206.4,
                    b: 6356583.8,
                    ellipseName: 'Clarke 1866'
                };
                exports.clrk80 = {
                    a: 6378249.145,
                    rf: 293.4663,
                    ellipseName: 'Clarke 1880 mod.'
                };
                exports.clrk58 = {
                    a: 6378293.645208759,
                    rf: 294.2606763692654,
                    ellipseName: 'Clarke 1858'
                };
                exports.CPM = {
                    a: 6375738.7,
                    rf: 334.29,
                    ellipseName: 'Comm. des Poids et Mesures 1799'
                };
                exports.delmbr = {
                    a: 6376428,
                    rf: 311.5,
                    ellipseName: 'Delambre 1810 (Belgium)'
                };
                exports.engelis = {
                    a: 6378136.05,
                    rf: 298.2566,
                    ellipseName: 'Engelis 1985'
                };
                exports.evrst30 = {
                    a: 6377276.345,
                    rf: 300.8017,
                    ellipseName: 'Everest 1830'
                };
                exports.evrst48 = {
                    a: 6377304.063,
                    rf: 300.8017,
                    ellipseName: 'Everest 1948'
                };
                exports.evrst56 = {
                    a: 6377301.243,
                    rf: 300.8017,
                    ellipseName: 'Everest 1956'
                };
                exports.evrst69 = {
                    a: 6377295.664,
                    rf: 300.8017,
                    ellipseName: 'Everest 1969'
                };
                exports.evrstSS = {
                    a: 6377298.556,
                    rf: 300.8017,
                    ellipseName: 'Everest (Sabah & Sarawak)'
                };
                exports.fschr60 = {
                    a: 6378166,
                    rf: 298.3,
                    ellipseName: 'Fischer (Mercury Datum) 1960'
                };
                exports.fschr60m = {
                    a: 6378155,
                    rf: 298.3,
                    ellipseName: 'Fischer 1960'
                };
                exports.fschr68 = {
                    a: 6378150,
                    rf: 298.3,
                    ellipseName: 'Fischer 1968'
                };
                exports.helmert = {
                    a: 6378200,
                    rf: 298.3,
                    ellipseName: 'Helmert 1906'
                };
                exports.hough = {
                    a: 6378270,
                    rf: 297,
                    ellipseName: 'Hough'
                };
                exports.intl = {
                    a: 6378388,
                    rf: 297,
                    ellipseName: 'International 1909 (Hayford)'
                };
                exports.kaula = {
                    a: 6378163,
                    rf: 298.24,
                    ellipseName: 'Kaula 1961'
                };
                exports.lerch = {
                    a: 6378139,
                    rf: 298.257,
                    ellipseName: 'Lerch 1979'
                };
                exports.mprts = {
                    a: 6397300,
                    rf: 191,
                    ellipseName: 'Maupertius 1738'
                };
                exports.new_intl = {
                    a: 6378157.5,
                    b: 6356772.2,
                    ellipseName: 'New International 1967'
                };
                exports.plessis = {
                    a: 6376523,
                    rf: 6355863,
                    ellipseName: 'Plessis 1817 (France)'
                };
                exports.krass = {
                    a: 6378245,
                    rf: 298.3,
                    ellipseName: 'Krassovsky, 1942'
                };
                exports.SEasia = {
                    a: 6378155,
                    b: 6356773.3205,
                    ellipseName: 'Southeast Asia'
                };
                exports.walbeck = {
                    a: 6376896,
                    b: 6355834.8467,
                    ellipseName: 'Walbeck'
                };
                exports.WGS60 = {
                    a: 6378165,
                    rf: 298.3,
                    ellipseName: 'WGS 60'
                };
                exports.WGS66 = {
                    a: 6378145,
                    rf: 298.25,
                    ellipseName: 'WGS 66'
                };
                exports.WGS7 = {
                    a: 6378135,
                    rf: 298.26,
                    ellipseName: 'WGS 72'
                };
                exports.WGS84 = {
                    a: 6378137,
                    rf: 298.257223563,
                    ellipseName: 'WGS 84'
                };
                exports.sphere = {
                    a: 6370997,
                    b: 6370997,
                    ellipseName: 'Normal Sphere (r=6370997)'
                };
            },
            {}
        ],
        27: [
            function (_dereq_, module, exports) {
                exports.greenwich = 0;    //"0dE",
                //"0dE",
                exports.lisbon = -9.131906111111;    //"9d07'54.862\"W",
                //"9d07'54.862\"W",
                exports.paris = 2.337229166667;    //"2d20'14.025\"E",
                //"2d20'14.025\"E",
                exports.bogota = -74.080916666667;    //"74d04'51.3\"W",
                //"74d04'51.3\"W",
                exports.madrid = -3.687938888889;    //"3d41'16.58\"W",
                //"3d41'16.58\"W",
                exports.rome = 12.452333333333;    //"12d27'8.4\"E",
                //"12d27'8.4\"E",
                exports.bern = 7.439583333333;    //"7d26'22.5\"E",
                //"7d26'22.5\"E",
                exports.jakarta = 106.807719444444;    //"106d48'27.79\"E",
                //"106d48'27.79\"E",
                exports.ferro = -17.666666666667;    //"17d40'W",
                //"17d40'W",
                exports.brussels = 4.367975;    //"4d22'4.71\"E",
                //"4d22'4.71\"E",
                exports.stockholm = 18.058277777778;    //"18d3'29.8\"E",
                //"18d3'29.8\"E",
                exports.athens = 23.7163375;    //"23d42'58.815\"E",
                //"23d42'58.815\"E",
                exports.oslo = 10.722916666667;    //"10d43'22.5\"E"
            },
            {}
        ],
        28: [
            function (_dereq_, module, exports) {
                var proj = _dereq_('./Proj');
                var transform = _dereq_('./transform');
                var wgs84 = proj('WGS84');
                function transformer(from, to, coords) {
                    var transformedArray;
                    if (Array.isArray(coords)) {
                        transformedArray = transform(from, to, coords);
                        if (coords.length === 3) {
                            return [
                                transformedArray.x,
                                transformedArray.y,
                                transformedArray.z
                            ];
                        } else {
                            return [
                                transformedArray.x,
                                transformedArray.y
                            ];
                        }
                    } else {
                        return transform(from, to, coords);
                    }
                }
                function checkProj(item) {
                    if (item instanceof proj) {
                        return item;
                    }
                    if (item.oProj) {
                        return item.oProj;
                    }
                    return proj(item);
                }
                function proj4(fromProj, toProj, coord) {
                    fromProj = checkProj(fromProj);
                    var single = false;
                    var obj;
                    if (typeof toProj === 'undefined') {
                        toProj = fromProj;
                        fromProj = wgs84;
                        single = true;
                    } else if (typeof toProj.x !== 'undefined' || Array.isArray(toProj)) {
                        coord = toProj;
                        toProj = fromProj;
                        fromProj = wgs84;
                        single = true;
                    }
                    toProj = checkProj(toProj);
                    if (coord) {
                        return transformer(fromProj, toProj, coord);
                    } else {
                        obj = {
                            forward: function (coords) {
                                return transformer(fromProj, toProj, coords);
                            },
                            inverse: function (coords) {
                                return transformer(toProj, fromProj, coords);
                            }
                        };
                        if (single) {
                            obj.oProj = toProj;
                        }
                        return obj;
                    }
                }
                module.exports = proj4;
            },
            {
                './Proj': 2,
                './transform': 64
            }
        ],
        29: [
            function (_dereq_, module, exports) {
                var HALF_PI = Math.PI / 2;
                var PJD_3PARAM = 1;
                var PJD_7PARAM = 2;
                var PJD_GRIDSHIFT = 3;
                var PJD_WGS84 = 4;    // WGS84 or equivalent
                // WGS84 or equivalent
                var PJD_NODATUM = 5;    // WGS84 or equivalent
                // WGS84 or equivalent
                var SEC_TO_RAD = 0.00000484813681109536;
                var AD_C = 1.0026;
                var COS_67P5 = 0.3826834323650898;
                var datum = function (proj) {
                    if (!(this instanceof datum)) {
                        return new datum(proj);
                    }
                    this.datum_type = PJD_WGS84;    //default setting
                    //default setting
                    if (!proj) {
                        return;
                    }
                    if (proj.datumCode && proj.datumCode === 'none') {
                        this.datum_type = PJD_NODATUM;
                    }
                    if (proj.datum_params) {
                        for (var i = 0; i < proj.datum_params.length; i++) {
                            proj.datum_params[i] = parseFloat(proj.datum_params[i]);
                        }
                        if (proj.datum_params[0] !== 0 || proj.datum_params[1] !== 0 || proj.datum_params[2] !== 0) {
                            this.datum_type = PJD_3PARAM;
                        }
                        if (proj.datum_params.length > 3) {
                            if (proj.datum_params[3] !== 0 || proj.datum_params[4] !== 0 || proj.datum_params[5] !== 0 || proj.datum_params[6] !== 0) {
                                this.datum_type = PJD_7PARAM;
                                proj.datum_params[3] *= SEC_TO_RAD;
                                proj.datum_params[4] *= SEC_TO_RAD;
                                proj.datum_params[5] *= SEC_TO_RAD;
                                proj.datum_params[6] = proj.datum_params[6] / 1000000 + 1;
                            }
                        }
                    }    // DGR 2011-03-21 : nadgrids support
                    // DGR 2011-03-21 : nadgrids support
                    this.datum_type = proj.grids ? PJD_GRIDSHIFT : this.datum_type;
                    this.a = proj.a;    //datum object also uses these values
                    //datum object also uses these values
                    this.b = proj.b;
                    this.es = proj.es;
                    this.ep2 = proj.ep2;
                    this.datum_params = proj.datum_params;
                    if (this.datum_type === PJD_GRIDSHIFT) {
                        this.grids = proj.grids;
                    }
                };
                datum.prototype = {
                    /****************************************************************/
                    // cs_compare_datums()
                    //   Returns TRUE if the two datums match, otherwise FALSE.
                    compare_datums: function (dest) {
                        if (this.datum_type !== dest.datum_type) {
                            return false;    // false, datums are not equal
                        } else if (this.a !== dest.a || Math.abs(this.es - dest.es) > 5e-11) {
                            // the tolerence for es is to ensure that GRS80 and WGS84
                            // are considered identical
                            return false;
                        } else if (this.datum_type === PJD_3PARAM) {
                            return this.datum_params[0] === dest.datum_params[0] && this.datum_params[1] === dest.datum_params[1] && this.datum_params[2] === dest.datum_params[2];
                        } else if (this.datum_type === PJD_7PARAM) {
                            return this.datum_params[0] === dest.datum_params[0] && this.datum_params[1] === dest.datum_params[1] && this.datum_params[2] === dest.datum_params[2] && this.datum_params[3] === dest.datum_params[3] && this.datum_params[4] === dest.datum_params[4] && this.datum_params[5] === dest.datum_params[5] && this.datum_params[6] === dest.datum_params[6];
                        } else if (this.datum_type === PJD_GRIDSHIFT || dest.datum_type === PJD_GRIDSHIFT) {
                            //alert("ERROR: Grid shift transformations are not implemented.");
                            //return false
                            //DGR 2012-07-29 lazy ...
                            return this.nadgrids === dest.nadgrids;
                        } else {
                            return true;    // datums are equal
                        }
                    },
                    // cs_compare_datums()
                    /*
                     * The function Convert_Geodetic_To_Geocentric converts geodetic coordinates
                     * (latitude, longitude, and height) to geocentric coordinates (X, Y, Z),
                     * according to the current ellipsoid parameters.
                     *
                     *    Latitude  : Geodetic latitude in radians                     (input)
                     *    Longitude : Geodetic longitude in radians                    (input)
                     *    Height    : Geodetic height, in meters                       (input)
                     *    X         : Calculated Geocentric X coordinate, in meters    (output)
                     *    Y         : Calculated Geocentric Y coordinate, in meters    (output)
                     *    Z         : Calculated Geocentric Z coordinate, in meters    (output)
                     *
                     */
                    geodetic_to_geocentric: function (p) {
                        var Longitude = p.x;
                        var Latitude = p.y;
                        var Height = p.z ? p.z : 0;    //Z value not always supplied
                        //Z value not always supplied
                        var X;    // output
                        // output
                        var Y;
                        var Z;
                        var Error_Code = 0;    //  GEOCENT_NO_ERROR;
                        //  GEOCENT_NO_ERROR;
                        var Rn;    /*  Earth radius at location  */
                        /*  Earth radius at location  */
                        var Sin_Lat;    /*  Math.sin(Latitude)  */
                        /*  Math.sin(Latitude)  */
                        var Sin2_Lat;    /*  Square of Math.sin(Latitude)  */
                        /*  Square of Math.sin(Latitude)  */
                        var Cos_Lat;    /*  Math.cos(Latitude)  */
                        /*
                         ** Don't blow up if Latitude is just a little out of the value
                         ** range as it may just be a rounding issue.  Also removed longitude
                         ** test, it should be wrapped by Math.cos() and Math.sin().  NFW for PROJ.4, Sep/2001.
                         */
                        /*  Math.cos(Latitude)  */
                        /*
                         ** Don't blow up if Latitude is just a little out of the value
                         ** range as it may just be a rounding issue.  Also removed longitude
                         ** test, it should be wrapped by Math.cos() and Math.sin().  NFW for PROJ.4, Sep/2001.
                         */
                        if (Latitude < -HALF_PI && Latitude > -1.001 * HALF_PI) {
                            Latitude = -HALF_PI;
                        } else if (Latitude > HALF_PI && Latitude < 1.001 * HALF_PI) {
                            Latitude = HALF_PI;
                        } else if (Latitude < -HALF_PI || Latitude > HALF_PI) {
                            /* Latitude out of range */
                            //..reportError('geocent:lat out of range:' + Latitude);
                            return null;
                        }
                        if (Longitude > Math.PI) {
                            Longitude -= 2 * Math.PI;
                        }
                        Sin_Lat = Math.sin(Latitude);
                        Cos_Lat = Math.cos(Latitude);
                        Sin2_Lat = Sin_Lat * Sin_Lat;
                        Rn = this.a / Math.sqrt(1 - this.es * Sin2_Lat);
                        X = (Rn + Height) * Cos_Lat * Math.cos(Longitude);
                        Y = (Rn + Height) * Cos_Lat * Math.sin(Longitude);
                        Z = (Rn * (1 - this.es) + Height) * Sin_Lat;
                        p.x = X;
                        p.y = Y;
                        p.z = Z;
                        return Error_Code;
                    },
                    // cs_geodetic_to_geocentric()
                    geocentric_to_geodetic: function (p) {
                        /* local defintions and variables */
                        /* end-criterium of loop, accuracy of sin(Latitude) */
                        var genau = 1e-12;
                        var genau2 = genau * genau;
                        var maxiter = 30;
                        var P;    /* distance between semi-minor axis and location */
                        /* distance between semi-minor axis and location */
                        var RR;    /* distance between center and location */
                        /* distance between center and location */
                        var CT;    /* sin of geocentric latitude */
                        /* sin of geocentric latitude */
                        var ST;    /* cos of geocentric latitude */
                        /* cos of geocentric latitude */
                        var RX;
                        var RK;
                        var RN;    /* Earth radius at location */
                        /* Earth radius at location */
                        var CPHI0;    /* cos of start or old geodetic latitude in iterations */
                        /* cos of start or old geodetic latitude in iterations */
                        var SPHI0;    /* sin of start or old geodetic latitude in iterations */
                        /* sin of start or old geodetic latitude in iterations */
                        var CPHI;    /* cos of searched geodetic latitude */
                        /* cos of searched geodetic latitude */
                        var SPHI;    /* sin of searched geodetic latitude */
                        /* sin of searched geodetic latitude */
                        var SDPHI;    /* end-criterium: addition-theorem of sin(Latitude(iter)-Latitude(iter-1)) */
                        /* end-criterium: addition-theorem of sin(Latitude(iter)-Latitude(iter-1)) */
                        var At_Pole;    /* indicates location is in polar region */
                        /* indicates location is in polar region */
                        var iter;    /* # of continous iteration, max. 30 is always enough (s.a.) */
                        /* # of continous iteration, max. 30 is always enough (s.a.) */
                        var X = p.x;
                        var Y = p.y;
                        var Z = p.z ? p.z : 0;    //Z value not always supplied
                        //Z value not always supplied
                        var Longitude;
                        var Latitude;
                        var Height;
                        At_Pole = false;
                        P = Math.sqrt(X * X + Y * Y);
                        RR = Math.sqrt(X * X + Y * Y + Z * Z);    /*      special cases for latitude and longitude */
                        /*      special cases for latitude and longitude */
                        if (P / this.a < genau) {
                            /*  special case, if P=0. (X=0., Y=0.) */
                            At_Pole = true;
                            Longitude = 0;    /*  if (X,Y,Z)=(0.,0.,0.) then Height becomes semi-minor axis
                             *  of ellipsoid (=center of mass), Latitude becomes PI/2 */
                            /*  if (X,Y,Z)=(0.,0.,0.) then Height becomes semi-minor axis
                             *  of ellipsoid (=center of mass), Latitude becomes PI/2 */
                            if (RR / this.a < genau) {
                                Latitude = HALF_PI;
                                Height = -this.b;
                                return;
                            }
                        } else {
                            /*  ellipsoidal (geodetic) longitude
                             *  interval: -PI < Longitude <= +PI */
                            Longitude = Math.atan2(Y, X);
                        }    /* --------------------------------------------------------------
                         * Following iterative algorithm was developped by
                         * "Institut for Erdmessung", University of Hannover, July 1988.
                         * Internet: www.ife.uni-hannover.de
                         * Iterative computation of CPHI,SPHI and Height.
                         * Iteration of CPHI and SPHI to 10**-12 radian resp.
                         * 2*10**-7 arcsec.
                         * --------------------------------------------------------------
                         */
                        /* --------------------------------------------------------------
                         * Following iterative algorithm was developped by
                         * "Institut for Erdmessung", University of Hannover, July 1988.
                         * Internet: www.ife.uni-hannover.de
                         * Iterative computation of CPHI,SPHI and Height.
                         * Iteration of CPHI and SPHI to 10**-12 radian resp.
                         * 2*10**-7 arcsec.
                         * --------------------------------------------------------------
                         */
                        CT = Z / RR;
                        ST = P / RR;
                        RX = 1 / Math.sqrt(1 - this.es * (2 - this.es) * ST * ST);
                        CPHI0 = ST * (1 - this.es) * RX;
                        SPHI0 = CT * RX;
                        iter = 0;    /* loop to find sin(Latitude) resp. Latitude
                         * until |sin(Latitude(iter)-Latitude(iter-1))| < genau */
                        /* loop to find sin(Latitude) resp. Latitude
                         * until |sin(Latitude(iter)-Latitude(iter-1))| < genau */
                        do {
                            iter++;
                            RN = this.a / Math.sqrt(1 - this.es * SPHI0 * SPHI0);    /*  ellipsoidal (geodetic) height */
                            /*  ellipsoidal (geodetic) height */
                            Height = P * CPHI0 + Z * SPHI0 - RN * (1 - this.es * SPHI0 * SPHI0);
                            RK = this.es * RN / (RN + Height);
                            RX = 1 / Math.sqrt(1 - RK * (2 - RK) * ST * ST);
                            CPHI = ST * (1 - RK) * RX;
                            SPHI = CT * RX;
                            SDPHI = SPHI * CPHI0 - CPHI * SPHI0;
                            CPHI0 = CPHI;
                            SPHI0 = SPHI;
                        } while (SDPHI * SDPHI > genau2 && iter < maxiter);    /*      ellipsoidal (geodetic) latitude */
                        /*      ellipsoidal (geodetic) latitude */
                        Latitude = Math.atan(SPHI / Math.abs(CPHI));
                        p.x = Longitude;
                        p.y = Latitude;
                        p.z = Height;
                        return p;
                    },
                    // cs_geocentric_to_geodetic()
                    /** Convert_Geocentric_To_Geodetic
                     * The method used here is derived from 'An Improved Algorithm for
                     * Geocentric to Geodetic Coordinate Conversion', by Ralph Toms, Feb 1996
                     */
                    geocentric_to_geodetic_noniter: function (p) {
                        var X = p.x;
                        var Y = p.y;
                        var Z = p.z ? p.z : 0;    //Z value not always supplied
                        //Z value not always supplied
                        var Longitude;
                        var Latitude;
                        var Height;
                        var W;    /* distance from Z axis */
                        /* distance from Z axis */
                        var W2;    /* square of distance from Z axis */
                        /* square of distance from Z axis */
                        var T0;    /* initial estimate of vertical component */
                        /* initial estimate of vertical component */
                        var T1;    /* corrected estimate of vertical component */
                        /* corrected estimate of vertical component */
                        var S0;    /* initial estimate of horizontal component */
                        /* initial estimate of horizontal component */
                        var S1;    /* corrected estimate of horizontal component */
                        /* corrected estimate of horizontal component */
                        var Sin_B0;    /* Math.sin(B0), B0 is estimate of Bowring aux variable */
                        /* Math.sin(B0), B0 is estimate of Bowring aux variable */
                        var Sin3_B0;    /* cube of Math.sin(B0) */
                        /* cube of Math.sin(B0) */
                        var Cos_B0;    /* Math.cos(B0) */
                        /* Math.cos(B0) */
                        var Sin_p1;    /* Math.sin(phi1), phi1 is estimated latitude */
                        /* Math.sin(phi1), phi1 is estimated latitude */
                        var Cos_p1;    /* Math.cos(phi1) */
                        /* Math.cos(phi1) */
                        var Rn;    /* Earth radius at location */
                        /* Earth radius at location */
                        var Sum;    /* numerator of Math.cos(phi1) */
                        /* numerator of Math.cos(phi1) */
                        var At_Pole;    /* indicates location is in polar region */
                        /* indicates location is in polar region */
                        X = parseFloat(X);    // cast from string to float
                        // cast from string to float
                        Y = parseFloat(Y);
                        Z = parseFloat(Z);
                        At_Pole = false;
                        if (X !== 0) {
                            Longitude = Math.atan2(Y, X);
                        } else {
                            if (Y > 0) {
                                Longitude = HALF_PI;
                            } else if (Y < 0) {
                                Longitude = -HALF_PI;
                            } else {
                                At_Pole = true;
                                Longitude = 0;
                                if (Z > 0) {
                                    /* north pole */
                                    Latitude = HALF_PI;
                                } else if (Z < 0) {
                                    /* south pole */
                                    Latitude = -HALF_PI;
                                } else {
                                    /* center of earth */
                                    Latitude = HALF_PI;
                                    Height = -this.b;
                                    return;
                                }
                            }
                        }
                        W2 = X * X + Y * Y;
                        W = Math.sqrt(W2);
                        T0 = Z * AD_C;
                        S0 = Math.sqrt(T0 * T0 + W2);
                        Sin_B0 = T0 / S0;
                        Cos_B0 = W / S0;
                        Sin3_B0 = Sin_B0 * Sin_B0 * Sin_B0;
                        T1 = Z + this.b * this.ep2 * Sin3_B0;
                        Sum = W - this.a * this.es * Cos_B0 * Cos_B0 * Cos_B0;
                        S1 = Math.sqrt(T1 * T1 + Sum * Sum);
                        Sin_p1 = T1 / S1;
                        Cos_p1 = Sum / S1;
                        Rn = this.a / Math.sqrt(1 - this.es * Sin_p1 * Sin_p1);
                        if (Cos_p1 >= COS_67P5) {
                            Height = W / Cos_p1 - Rn;
                        } else if (Cos_p1 <= -COS_67P5) {
                            Height = W / -Cos_p1 - Rn;
                        } else {
                            Height = Z / Sin_p1 + Rn * (this.es - 1);
                        }
                        if (At_Pole === false) {
                            Latitude = Math.atan(Sin_p1 / Cos_p1);
                        }
                        p.x = Longitude;
                        p.y = Latitude;
                        p.z = Height;
                        return p;
                    },
                    // geocentric_to_geodetic_noniter()
                    /****************************************************************/
                    // pj_geocentic_to_wgs84( p )
                    //  p = point to transform in geocentric coordinates (x,y,z)
                    geocentric_to_wgs84: function (p) {
                        if (this.datum_type === PJD_3PARAM) {
                            // if( x[io] === HUGE_VAL )
                            //    continue;
                            p.x += this.datum_params[0];
                            p.y += this.datum_params[1];
                            p.z += this.datum_params[2];
                        } else if (this.datum_type === PJD_7PARAM) {
                            var Dx_BF = this.datum_params[0];
                            var Dy_BF = this.datum_params[1];
                            var Dz_BF = this.datum_params[2];
                            var Rx_BF = this.datum_params[3];
                            var Ry_BF = this.datum_params[4];
                            var Rz_BF = this.datum_params[5];
                            var M_BF = this.datum_params[6];    // if( x[io] === HUGE_VAL )
                            //    continue;
                            // if( x[io] === HUGE_VAL )
                            //    continue;
                            var x_out = M_BF * (p.x - Rz_BF * p.y + Ry_BF * p.z) + Dx_BF;
                            var y_out = M_BF * (Rz_BF * p.x + p.y - Rx_BF * p.z) + Dy_BF;
                            var z_out = M_BF * (-Ry_BF * p.x + Rx_BF * p.y + p.z) + Dz_BF;
                            p.x = x_out;
                            p.y = y_out;
                            p.z = z_out;
                        }
                    },
                    // cs_geocentric_to_wgs84
                    /****************************************************************/
                    // pj_geocentic_from_wgs84()
                    //  coordinate system definition,
                    //  point to transform in geocentric coordinates (x,y,z)
                    geocentric_from_wgs84: function (p) {
                        if (this.datum_type === PJD_3PARAM) {
                            //if( x[io] === HUGE_VAL )
                            //    continue;
                            p.x -= this.datum_params[0];
                            p.y -= this.datum_params[1];
                            p.z -= this.datum_params[2];
                        } else if (this.datum_type === PJD_7PARAM) {
                            var Dx_BF = this.datum_params[0];
                            var Dy_BF = this.datum_params[1];
                            var Dz_BF = this.datum_params[2];
                            var Rx_BF = this.datum_params[3];
                            var Ry_BF = this.datum_params[4];
                            var Rz_BF = this.datum_params[5];
                            var M_BF = this.datum_params[6];
                            var x_tmp = (p.x - Dx_BF) / M_BF;
                            var y_tmp = (p.y - Dy_BF) / M_BF;
                            var z_tmp = (p.z - Dz_BF) / M_BF;    //if( x[io] === HUGE_VAL )
                            //    continue;
                            //if( x[io] === HUGE_VAL )
                            //    continue;
                            p.x = x_tmp + Rz_BF * y_tmp - Ry_BF * z_tmp;
                            p.y = -Rz_BF * x_tmp + y_tmp + Rx_BF * z_tmp;
                            p.z = Ry_BF * x_tmp - Rx_BF * y_tmp + z_tmp;
                        }    //cs_geocentric_from_wgs84()
                    }
                };    /** point object, nothing fancy, just allows values to be
                 passed back and forth by reference rather than by value.
                 Other point classes may be used as long as they have
                 x and y properties, which will get modified in the transform method.
                 */
                /** point object, nothing fancy, just allows values to be
                 passed back and forth by reference rather than by value.
                 Other point classes may be used as long as they have
                 x and y properties, which will get modified in the transform method.
                 */
                module.exports = datum;
            },
            {}
        ],
        30: [
            function (_dereq_, module, exports) {
                var PJD_3PARAM = 1;
                var PJD_7PARAM = 2;
                var PJD_GRIDSHIFT = 3;
                var PJD_NODATUM = 5;    // WGS84 or equivalent
                // WGS84 or equivalent
                var SRS_WGS84_SEMIMAJOR = 6378137;    // only used in grid shift transforms
                // only used in grid shift transforms
                var SRS_WGS84_ESQUARED = 0.006694379990141316;    //DGR: 2012-07-29
                //DGR: 2012-07-29
                module.exports = function (source, dest, point) {
                    var wp, i, l;
                    function checkParams(fallback) {
                        return fallback === PJD_3PARAM || fallback === PJD_7PARAM;
                    }    // Short cut if the datums are identical.
                    // Short cut if the datums are identical.
                    if (source.compare_datums(dest)) {
                        return point;    // in this case, zero is sucess,
                        // whereas cs_compare_datums returns 1 to indicate TRUE
                        // confusing, should fix this
                    }    // Explicitly skip datum transform by setting 'datum=none' as parameter for either source or dest
                    // Explicitly skip datum transform by setting 'datum=none' as parameter for either source or dest
                    if (source.datum_type === PJD_NODATUM || dest.datum_type === PJD_NODATUM) {
                        return point;
                    }    //DGR: 2012-07-29 : add nadgrids support (begin)
                    //DGR: 2012-07-29 : add nadgrids support (begin)
                    var src_a = source.a;
                    var src_es = source.es;
                    var dst_a = dest.a;
                    var dst_es = dest.es;
                    var fallback = source.datum_type;    // If this datum requires grid shifts, then apply it to geodetic coordinates.
                    // If this datum requires grid shifts, then apply it to geodetic coordinates.
                    if (fallback === PJD_GRIDSHIFT) {
                        if (this.apply_gridshift(source, 0, point) === 0) {
                            source.a = SRS_WGS84_SEMIMAJOR;
                            source.es = SRS_WGS84_ESQUARED;
                        } else {
                            // try 3 or 7 params transformation or nothing ?
                            if (!source.datum_params) {
                                source.a = src_a;
                                source.es = source.es;
                                return point;
                            }
                            wp = 1;
                            for (i = 0, l = source.datum_params.length; i < l; i++) {
                                wp *= source.datum_params[i];
                            }
                            if (wp === 0) {
                                source.a = src_a;
                                source.es = source.es;
                                return point;
                            }
                            if (source.datum_params.length > 3) {
                                fallback = PJD_7PARAM;
                            } else {
                                fallback = PJD_3PARAM;
                            }
                        }
                    }
                    if (dest.datum_type === PJD_GRIDSHIFT) {
                        dest.a = SRS_WGS84_SEMIMAJOR;
                        dest.es = SRS_WGS84_ESQUARED;
                    }    // Do we need to go through geocentric coordinates?
                    // Do we need to go through geocentric coordinates?
                    if (source.es !== dest.es || source.a !== dest.a || checkParams(fallback) || checkParams(dest.datum_type)) {
                        //DGR: 2012-07-29 : add nadgrids support (end)
                        // Convert to geocentric coordinates.
                        source.geodetic_to_geocentric(point);    // CHECK_RETURN;
                        // Convert between datums
                        // CHECK_RETURN;
                        // Convert between datums
                        if (checkParams(source.datum_type)) {
                            source.geocentric_to_wgs84(point);    // CHECK_RETURN;
                        }
                        if (checkParams(dest.datum_type)) {
                            dest.geocentric_from_wgs84(point);    // CHECK_RETURN;
                        }    // Convert back to geodetic coordinates
                        // Convert back to geodetic coordinates
                        dest.geocentric_to_geodetic(point);    // CHECK_RETURN;
                    }    // Apply grid shift to destination if required
                    // Apply grid shift to destination if required
                    if (dest.datum_type === PJD_GRIDSHIFT) {
                        this.apply_gridshift(dest, 1, point);    // CHECK_RETURN;
                    }
                    source.a = src_a;
                    source.es = src_es;
                    dest.a = dst_a;
                    dest.es = dst_es;
                    return point;
                };
            },
            {}
        ],
        31: [
            function (_dereq_, module, exports) {
                var globals = _dereq_('./global');
                var parseProj = _dereq_('./projString');
                var wkt = _dereq_('./wkt');
                function defs(name) {
                    /*global console*/
                    var that = this;
                    if (arguments.length === 2) {
                        var def = arguments[1];
                        if (typeof def === 'string') {
                            if (def[0] === '+') {
                                defs[name] = parseProj(arguments[1]);
                            } else {
                                defs[name] = wkt(arguments[1]);
                            }
                        } else {
                            defs[name] = def;
                        }
                    } else if (arguments.length === 1) {
                        if (Array.isArray(name)) {
                            return name.map(function (v) {
                                if (Array.isArray(v)) {
                                    defs.apply(that, v);
                                } else {
                                    defs(v);
                                }
                            });
                        } else if (typeof name === 'string') {
                            if (name in defs) {
                                return defs[name];
                            }
                        } else if ('EPSG' in name) {
                            defs['EPSG:' + name.EPSG] = name;
                        } else if ('ESRI' in name) {
                            defs['ESRI:' + name.ESRI] = name;
                        } else if ('IAU2000' in name) {
                            defs['IAU2000:' + name.IAU2000] = name;
                        } else {
                            console.log(name);
                        }
                        return;
                    }
                }
                globals(defs);
                module.exports = defs;
            },
            {
                './global': 34,
                './projString': 37,
                './wkt': 65
            }
        ],
        32: [
            function (_dereq_, module, exports) {
                var Datum = _dereq_('./constants/Datum');
                var Ellipsoid = _dereq_('./constants/Ellipsoid');
                var extend = _dereq_('./extend');
                var datum = _dereq_('./datum');
                var EPSLN = 1e-10;    // ellipoid pj_set_ell.c
                // ellipoid pj_set_ell.c
                var SIXTH = 0.16666666666666666;    /* 1/6 */
                /* 1/6 */
                var RA4 = 0.04722222222222222;    /* 17/360 */
                /* 17/360 */
                var RA6 = 0.022156084656084655;
                module.exports = function (json) {
                    // DGR 2011-03-20 : nagrids -> nadgrids
                    if (json.datumCode && json.datumCode !== 'none') {
                        var datumDef = Datum[json.datumCode];
                        if (datumDef) {
                            json.datum_params = datumDef.towgs84 ? datumDef.towgs84.split(',') : null;
                            json.ellps = datumDef.ellipse;
                            json.datumName = datumDef.datumName ? datumDef.datumName : json.datumCode;
                        }
                    }
                    if (!json.a) {
                        // do we have an ellipsoid?
                        var ellipse = Ellipsoid[json.ellps] ? Ellipsoid[json.ellps] : Ellipsoid.WGS84;
                        extend(json, ellipse);
                    }
                    if (json.rf && !json.b) {
                        json.b = (1 - 1 / json.rf) * json.a;
                    }
                    if (json.rf === 0 || Math.abs(json.a - json.b) < EPSLN) {
                        json.sphere = true;
                        json.b = json.a;
                    }
                    json.a2 = json.a * json.a;    // used in geocentric
                    // used in geocentric
                    json.b2 = json.b * json.b;    // used in geocentric
                    // used in geocentric
                    json.es = (json.a2 - json.b2) / json.a2;    // e ^ 2
                    // e ^ 2
                    json.e = Math.sqrt(json.es);    // eccentricity
                    // eccentricity
                    if (json.R_A) {
                        json.a *= 1 - json.es * (SIXTH + json.es * (RA4 + json.es * RA6));
                        json.a2 = json.a * json.a;
                        json.b2 = json.b * json.b;
                        json.es = 0;
                    }
                    json.ep2 = (json.a2 - json.b2) / json.b2;    // used in geocentric
                    // used in geocentric
                    if (!json.k0) {
                        json.k0 = 1;    //default value
                    }    //DGR 2010-11-12: axis
                    //DGR 2010-11-12: axis
                    if (!json.axis) {
                        json.axis = 'enu';
                    }
                    json.datum = datum(json);
                    return json;
                };
            },
            {
                './constants/Datum': 25,
                './constants/Ellipsoid': 26,
                './datum': 29,
                './extend': 33
            }
        ],
        33: [
            function (_dereq_, module, exports) {
                module.exports = function (destination, source) {
                    destination = destination || {};
                    var value, property;
                    if (!source) {
                        return destination;
                    }
                    for (property in source) {
                        value = source[property];
                        if (value !== undefined) {
                            destination[property] = value;
                        }
                    }
                    return destination;
                };
            },
            {}
        ],
        34: [
            function (_dereq_, module, exports) {
                module.exports = function (defs) {
                    defs('EPSG:4326', '+title=WGS 84 (long/lat) +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees');
                    defs('EPSG:4269', '+title=NAD83 (long/lat) +proj=longlat +a=6378137.0 +b=6356752.31414036 +ellps=GRS80 +datum=NAD83 +units=degrees');
                    defs('EPSG:3857', '+title=WGS 84 / Pseudo-Mercator +proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs');
                    defs.WGS84 = defs['EPSG:4326'];
                    defs['EPSG:3785'] = defs['EPSG:3857'];    // maintain backward compat, official code is 3857
                    // maintain backward compat, official code is 3857
                    defs.GOOGLE = defs['EPSG:3857'];
                    defs['EPSG:900913'] = defs['EPSG:3857'];
                    defs['EPSG:102113'] = defs['EPSG:3857'];
                };
            },
            {}
        ],
        35: [
            function (_dereq_, module, exports) {
                var proj4 = _dereq_('./core');
                proj4.defaultDatum = 'WGS84';    //default datum
                //default datum
                proj4.Proj = _dereq_('./Proj');
                proj4.WGS84 = new proj4.Proj('WGS84');
                proj4.Point = _dereq_('./Point');
                proj4.toPoint = _dereq_('./common/toPoint');
                proj4.defs = _dereq_('./defs');
                proj4.transform = _dereq_('./transform');
                proj4.mgrs = _dereq_('mgrs');
                proj4.version = _dereq_('../package.json').version;
                _dereq_('./includedProjections')(proj4);
                module.exports = proj4;
            },
            {
                '../package.json': 67,
                './Point': 1,
                './Proj': 2,
                './common/toPoint': 23,
                './core': 28,
                './defs': 31,
                './includedProjections': 'Jf/0WF',
                './transform': 64,
                'mgrs': 66
            }
        ],
        36: [
            function (_dereq_, module, exports) {
                var defs = _dereq_('./defs');
                var wkt = _dereq_('./wkt');
                var projStr = _dereq_('./projString');
                function testObj(code) {
                    return typeof code === 'string';
                }
                function testDef(code) {
                    return code in defs;
                }
                function testWKT(code) {
                    var codeWords = [
                        'GEOGCS',
                        'GEOCCS',
                        'PROJCS',
                        'LOCAL_CS'
                    ];
                    return codeWords.reduce(function (a, b) {
                        return a + 1 + code.indexOf(b);
                    }, 0);
                }
                function testProj(code) {
                    return code[0] === '+';
                }
                function parse(code) {
                    if (testObj(code)) {
                        //check to see if this is a WKT string
                        if (testDef(code)) {
                            return defs[code];
                        } else if (testWKT(code)) {
                            return wkt(code);
                        } else if (testProj(code)) {
                            return projStr(code);
                        }
                    } else {
                        return code;
                    }
                }
                module.exports = parse;
            },
            {
                './defs': 31,
                './projString': 37,
                './wkt': 65
            }
        ],
        37: [
            function (_dereq_, module, exports) {
                var D2R = 0.017453292519943295;
                var PrimeMeridian = _dereq_('./constants/PrimeMeridian');
                module.exports = function (defData) {
                    var self = {};
                    var paramObj = {};
                    defData.split('+').map(function (v) {
                        return v.trim();
                    }).filter(function (a) {
                        return a;
                    }).forEach(function (a) {
                        var split = a.split('=');
                        split.push(true);
                        paramObj[split[0].toLowerCase()] = split[1];
                    });
                    var paramName, paramVal, paramOutname;
                    var params = {
                        proj: 'projName',
                        datum: 'datumCode',
                        rf: function (v) {
                            self.rf = parseFloat(v, 10);
                        },
                        lat_0: function (v) {
                            self.lat0 = v * D2R;
                        },
                        lat_1: function (v) {
                            self.lat1 = v * D2R;
                        },
                        lat_2: function (v) {
                            self.lat2 = v * D2R;
                        },
                        lat_ts: function (v) {
                            self.lat_ts = v * D2R;
                        },
                        lon_0: function (v) {
                            self.long0 = v * D2R;
                        },
                        lon_1: function (v) {
                            self.long1 = v * D2R;
                        },
                        lon_2: function (v) {
                            self.long2 = v * D2R;
                        },
                        alpha: function (v) {
                            self.alpha = parseFloat(v) * D2R;
                        },
                        lonc: function (v) {
                            self.longc = v * D2R;
                        },
                        x_0: function (v) {
                            self.x0 = parseFloat(v, 10);
                        },
                        y_0: function (v) {
                            self.y0 = parseFloat(v, 10);
                        },
                        k_0: function (v) {
                            self.k0 = parseFloat(v, 10);
                        },
                        k: function (v) {
                            self.k0 = parseFloat(v, 10);
                        },
                        r_a: function () {
                            self.R_A = true;
                        },
                        zone: function (v) {
                            self.zone = parseInt(v, 10);
                        },
                        south: function () {
                            self.utmSouth = true;
                        },
                        towgs84: function (v) {
                            self.datum_params = v.split(',').map(function (a) {
                                return parseFloat(a, 10);
                            });
                        },
                        to_meter: function (v) {
                            self.to_meter = parseFloat(v, 10);
                        },
                        from_greenwich: function (v) {
                            self.from_greenwich = v * D2R;
                        },
                        pm: function (v) {
                            self.from_greenwich = (PrimeMeridian[v] ? PrimeMeridian[v] : parseFloat(v, 10)) * D2R;
                        },
                        nadgrids: function (v) {
                            if (v === '@null') {
                                self.datumCode = 'none';
                            } else {
                                self.nadgrids = v;
                            }
                        },
                        axis: function (v) {
                            var legalAxis = 'ewnsud';
                            if (v.length === 3 && legalAxis.indexOf(v.substr(0, 1)) !== -1 && legalAxis.indexOf(v.substr(1, 1)) !== -1 && legalAxis.indexOf(v.substr(2, 1)) !== -1) {
                                self.axis = v;
                            }
                        }
                    };
                    for (paramName in paramObj) {
                        paramVal = paramObj[paramName];
                        if (paramName in params) {
                            paramOutname = params[paramName];
                            if (typeof paramOutname === 'function') {
                                paramOutname(paramVal);
                            } else {
                                self[paramOutname] = paramVal;
                            }
                        } else {
                            self[paramName] = paramVal;
                        }
                    }
                    if (typeof self.datumCode === 'string' && self.datumCode !== 'WGS84') {
                        self.datumCode = self.datumCode.toLowerCase();
                    }
                    return self;
                };
            },
            { './constants/PrimeMeridian': 27 }
        ],
        38: [
            function (_dereq_, module, exports) {
                var projs = [
                    _dereq_('./projections/merc'),
                    _dereq_('./projections/longlat')
                ];
                var names = {};
                var projStore = [];
                function add(proj, i) {
                    var len = projStore.length;
                    if (!proj.names) {
                        console.log(i);
                        return true;
                    }
                    projStore[len] = proj;
                    proj.names.forEach(function (n) {
                        names[n.toLowerCase()] = len;
                    });
                    return this;
                }
                exports.add = add;
                exports.get = function (name) {
                    if (!name) {
                        return false;
                    }
                    var n = name.toLowerCase();
                    if (typeof names[n] !== 'undefined' && projStore[names[n]]) {
                        return projStore[names[n]];
                    }
                };
                exports.start = function () {
                    projs.forEach(add);
                };
            },
            {
                './projections/longlat': 50,
                './projections/merc': 51
            }
        ],
        39: [
            function (_dereq_, module, exports) {
                var EPSLN = 1e-10;
                var msfnz = _dereq_('../common/msfnz');
                var qsfnz = _dereq_('../common/qsfnz');
                var adjust_lon = _dereq_('../common/adjust_lon');
                var asinz = _dereq_('../common/asinz');
                exports.init = function () {
                    if (Math.abs(this.lat1 + this.lat2) < EPSLN) {
                        return;
                    }
                    this.temp = this.b / this.a;
                    this.es = 1 - Math.pow(this.temp, 2);
                    this.e3 = Math.sqrt(this.es);
                    this.sin_po = Math.sin(this.lat1);
                    this.cos_po = Math.cos(this.lat1);
                    this.t1 = this.sin_po;
                    this.con = this.sin_po;
                    this.ms1 = msfnz(this.e3, this.sin_po, this.cos_po);
                    this.qs1 = qsfnz(this.e3, this.sin_po, this.cos_po);
                    this.sin_po = Math.sin(this.lat2);
                    this.cos_po = Math.cos(this.lat2);
                    this.t2 = this.sin_po;
                    this.ms2 = msfnz(this.e3, this.sin_po, this.cos_po);
                    this.qs2 = qsfnz(this.e3, this.sin_po, this.cos_po);
                    this.sin_po = Math.sin(this.lat0);
                    this.cos_po = Math.cos(this.lat0);
                    this.t3 = this.sin_po;
                    this.qs0 = qsfnz(this.e3, this.sin_po, this.cos_po);
                    if (Math.abs(this.lat1 - this.lat2) > EPSLN) {
                        this.ns0 = (this.ms1 * this.ms1 - this.ms2 * this.ms2) / (this.qs2 - this.qs1);
                    } else {
                        this.ns0 = this.con;
                    }
                    this.c = this.ms1 * this.ms1 + this.ns0 * this.qs1;
                    this.rh = this.a * Math.sqrt(this.c - this.ns0 * this.qs0) / this.ns0;
                };    /* Albers Conical Equal Area forward equations--mapping lat,long to x,y
                 -------------------------------------------------------------------*/
                /* Albers Conical Equal Area forward equations--mapping lat,long to x,y
                 -------------------------------------------------------------------*/
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    this.sin_phi = Math.sin(lat);
                    this.cos_phi = Math.cos(lat);
                    var qs = qsfnz(this.e3, this.sin_phi, this.cos_phi);
                    var rh1 = this.a * Math.sqrt(this.c - this.ns0 * qs) / this.ns0;
                    var theta = this.ns0 * adjust_lon(lon - this.long0);
                    var x = rh1 * Math.sin(theta) + this.x0;
                    var y = this.rh - rh1 * Math.cos(theta) + this.y0;
                    p.x = x;
                    p.y = y;
                    return p;
                };
                exports.inverse = function (p) {
                    var rh1, qs, con, theta, lon, lat;
                    p.x -= this.x0;
                    p.y = this.rh - p.y + this.y0;
                    if (this.ns0 >= 0) {
                        rh1 = Math.sqrt(p.x * p.x + p.y * p.y);
                        con = 1;
                    } else {
                        rh1 = -Math.sqrt(p.x * p.x + p.y * p.y);
                        con = -1;
                    }
                    theta = 0;
                    if (rh1 !== 0) {
                        theta = Math.atan2(con * p.x, con * p.y);
                    }
                    con = rh1 * this.ns0 / this.a;
                    if (this.sphere) {
                        lat = Math.asin((this.c - con * con) / (2 * this.ns0));
                    } else {
                        qs = (this.c - con * con) / this.ns0;
                        lat = this.phi1z(this.e3, qs);
                    }
                    lon = adjust_lon(theta / this.ns0 + this.long0);
                    p.x = lon;
                    p.y = lat;
                    return p;
                };    /* Function to compute phi1, the latitude for the inverse of the
                 Albers Conical Equal-Area projection.
                 -------------------------------------------*/
                /* Function to compute phi1, the latitude for the inverse of the
                 Albers Conical Equal-Area projection.
                 -------------------------------------------*/
                exports.phi1z = function (eccent, qs) {
                    var sinphi, cosphi, con, com, dphi;
                    var phi = asinz(0.5 * qs);
                    if (eccent < EPSLN) {
                        return phi;
                    }
                    var eccnts = eccent * eccent;
                    for (var i = 1; i <= 25; i++) {
                        sinphi = Math.sin(phi);
                        cosphi = Math.cos(phi);
                        con = eccent * sinphi;
                        com = 1 - con * con;
                        dphi = 0.5 * com * com / cosphi * (qs / (1 - eccnts) - sinphi / com + 0.5 / eccent * Math.log((1 - con) / (1 + con)));
                        phi = phi + dphi;
                        if (Math.abs(dphi) <= 1e-7) {
                            return phi;
                        }
                    }
                    return null;
                };
                exports.names = [
                    'Albers_Conic_Equal_Area',
                    'Albers',
                    'aea'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/asinz': 6,
                '../common/msfnz': 15,
                '../common/qsfnz': 20
            }
        ],
        40: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                var HALF_PI = Math.PI / 2;
                var EPSLN = 1e-10;
                var mlfn = _dereq_('../common/mlfn');
                var e0fn = _dereq_('../common/e0fn');
                var e1fn = _dereq_('../common/e1fn');
                var e2fn = _dereq_('../common/e2fn');
                var e3fn = _dereq_('../common/e3fn');
                var gN = _dereq_('../common/gN');
                var asinz = _dereq_('../common/asinz');
                var imlfn = _dereq_('../common/imlfn');
                exports.init = function () {
                    this.sin_p12 = Math.sin(this.lat0);
                    this.cos_p12 = Math.cos(this.lat0);
                };
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var sinphi = Math.sin(p.y);
                    var cosphi = Math.cos(p.y);
                    var dlon = adjust_lon(lon - this.long0);
                    var e0, e1, e2, e3, Mlp, Ml, tanphi, Nl1, Nl, psi, Az, G, H, GH, Hs, c, kp, cos_c, s, s2, s3, s4, s5;
                    if (this.sphere) {
                        if (Math.abs(this.sin_p12 - 1) <= EPSLN) {
                            //North Pole case
                            p.x = this.x0 + this.a * (HALF_PI - lat) * Math.sin(dlon);
                            p.y = this.y0 - this.a * (HALF_PI - lat) * Math.cos(dlon);
                            return p;
                        } else if (Math.abs(this.sin_p12 + 1) <= EPSLN) {
                            //South Pole case
                            p.x = this.x0 + this.a * (HALF_PI + lat) * Math.sin(dlon);
                            p.y = this.y0 + this.a * (HALF_PI + lat) * Math.cos(dlon);
                            return p;
                        } else {
                            //default case
                            cos_c = this.sin_p12 * sinphi + this.cos_p12 * cosphi * Math.cos(dlon);
                            c = Math.acos(cos_c);
                            kp = c / Math.sin(c);
                            p.x = this.x0 + this.a * kp * cosphi * Math.sin(dlon);
                            p.y = this.y0 + this.a * kp * (this.cos_p12 * sinphi - this.sin_p12 * cosphi * Math.cos(dlon));
                            return p;
                        }
                    } else {
                        e0 = e0fn(this.es);
                        e1 = e1fn(this.es);
                        e2 = e2fn(this.es);
                        e3 = e3fn(this.es);
                        if (Math.abs(this.sin_p12 - 1) <= EPSLN) {
                            //North Pole case
                            Mlp = this.a * mlfn(e0, e1, e2, e3, HALF_PI);
                            Ml = this.a * mlfn(e0, e1, e2, e3, lat);
                            p.x = this.x0 + (Mlp - Ml) * Math.sin(dlon);
                            p.y = this.y0 - (Mlp - Ml) * Math.cos(dlon);
                            return p;
                        } else if (Math.abs(this.sin_p12 + 1) <= EPSLN) {
                            //South Pole case
                            Mlp = this.a * mlfn(e0, e1, e2, e3, HALF_PI);
                            Ml = this.a * mlfn(e0, e1, e2, e3, lat);
                            p.x = this.x0 + (Mlp + Ml) * Math.sin(dlon);
                            p.y = this.y0 + (Mlp + Ml) * Math.cos(dlon);
                            return p;
                        } else {
                            //Default case
                            tanphi = sinphi / cosphi;
                            Nl1 = gN(this.a, this.e, this.sin_p12);
                            Nl = gN(this.a, this.e, sinphi);
                            psi = Math.atan((1 - this.es) * tanphi + this.es * Nl1 * this.sin_p12 / (Nl * cosphi));
                            Az = Math.atan2(Math.sin(dlon), this.cos_p12 * Math.tan(psi) - this.sin_p12 * Math.cos(dlon));
                            if (Az === 0) {
                                s = Math.asin(this.cos_p12 * Math.sin(psi) - this.sin_p12 * Math.cos(psi));
                            } else if (Math.abs(Math.abs(Az) - Math.PI) <= EPSLN) {
                                s = -Math.asin(this.cos_p12 * Math.sin(psi) - this.sin_p12 * Math.cos(psi));
                            } else {
                                s = Math.asin(Math.sin(dlon) * Math.cos(psi) / Math.sin(Az));
                            }
                            G = this.e * this.sin_p12 / Math.sqrt(1 - this.es);
                            H = this.e * this.cos_p12 * Math.cos(Az) / Math.sqrt(1 - this.es);
                            GH = G * H;
                            Hs = H * H;
                            s2 = s * s;
                            s3 = s2 * s;
                            s4 = s3 * s;
                            s5 = s4 * s;
                            c = Nl1 * s * (1 - s2 * Hs * (1 - Hs) / 6 + s3 / 8 * GH * (1 - 2 * Hs) + s4 / 120 * (Hs * (4 - 7 * Hs) - 3 * G * G * (1 - 7 * Hs)) - s5 / 48 * GH);
                            p.x = this.x0 + c * Math.sin(Az);
                            p.y = this.y0 + c * Math.cos(Az);
                            return p;
                        }
                    }
                };
                exports.inverse = function (p) {
                    p.x -= this.x0;
                    p.y -= this.y0;
                    var rh, z, sinz, cosz, lon, lat, con, e0, e1, e2, e3, Mlp, M, N1, psi, Az, cosAz, tmp, A, B, D, Ee, F;
                    if (this.sphere) {
                        rh = Math.sqrt(p.x * p.x + p.y * p.y);
                        if (rh > 2 * HALF_PI * this.a) {
                            return;
                        }
                        z = rh / this.a;
                        sinz = Math.sin(z);
                        cosz = Math.cos(z);
                        lon = this.long0;
                        if (Math.abs(rh) <= EPSLN) {
                            lat = this.lat0;
                        } else {
                            lat = asinz(cosz * this.sin_p12 + p.y * sinz * this.cos_p12 / rh);
                            con = Math.abs(this.lat0) - HALF_PI;
                            if (Math.abs(con) <= EPSLN) {
                                if (this.lat0 >= 0) {
                                    lon = adjust_lon(this.long0 + Math.atan2(p.x, -p.y));
                                } else {
                                    lon = adjust_lon(this.long0 - Math.atan2(-p.x, p.y));
                                }
                            } else {
                                /*con = cosz - this.sin_p12 * Math.sin(lat);
                                 if ((Math.abs(con) < EPSLN) && (Math.abs(p.x) < EPSLN)) {
                                 //no-op, just keep the lon value as is
                                 } else {
                                 var temp = Math.atan2((p.x * sinz * this.cos_p12), (con * rh));
                                 lon = adjust_lon(this.long0 + Math.atan2((p.x * sinz * this.cos_p12), (con * rh)));
                                 }*/
                                lon = adjust_lon(this.long0 + Math.atan2(p.x * sinz, rh * this.cos_p12 * cosz - p.y * this.sin_p12 * sinz));
                            }
                        }
                        p.x = lon;
                        p.y = lat;
                        return p;
                    } else {
                        e0 = e0fn(this.es);
                        e1 = e1fn(this.es);
                        e2 = e2fn(this.es);
                        e3 = e3fn(this.es);
                        if (Math.abs(this.sin_p12 - 1) <= EPSLN) {
                            //North pole case
                            Mlp = this.a * mlfn(e0, e1, e2, e3, HALF_PI);
                            rh = Math.sqrt(p.x * p.x + p.y * p.y);
                            M = Mlp - rh;
                            lat = imlfn(M / this.a, e0, e1, e2, e3);
                            lon = adjust_lon(this.long0 + Math.atan2(p.x, -1 * p.y));
                            p.x = lon;
                            p.y = lat;
                            return p;
                        } else if (Math.abs(this.sin_p12 + 1) <= EPSLN) {
                            //South pole case
                            Mlp = this.a * mlfn(e0, e1, e2, e3, HALF_PI);
                            rh = Math.sqrt(p.x * p.x + p.y * p.y);
                            M = rh - Mlp;
                            lat = imlfn(M / this.a, e0, e1, e2, e3);
                            lon = adjust_lon(this.long0 + Math.atan2(p.x, p.y));
                            p.x = lon;
                            p.y = lat;
                            return p;
                        } else {
                            //default case
                            rh = Math.sqrt(p.x * p.x + p.y * p.y);
                            Az = Math.atan2(p.x, p.y);
                            N1 = gN(this.a, this.e, this.sin_p12);
                            cosAz = Math.cos(Az);
                            tmp = this.e * this.cos_p12 * cosAz;
                            A = -tmp * tmp / (1 - this.es);
                            B = 3 * this.es * (1 - A) * this.sin_p12 * this.cos_p12 * cosAz / (1 - this.es);
                            D = rh / N1;
                            Ee = D - A * (1 + A) * Math.pow(D, 3) / 6 - B * (1 + 3 * A) * Math.pow(D, 4) / 24;
                            F = 1 - A * Ee * Ee / 2 - D * Ee * Ee * Ee / 6;
                            psi = Math.asin(this.sin_p12 * Math.cos(Ee) + this.cos_p12 * Math.sin(Ee) * cosAz);
                            lon = adjust_lon(this.long0 + Math.asin(Math.sin(Az) * Math.sin(Ee) / Math.cos(psi)));
                            lat = Math.atan((1 - this.es * F * this.sin_p12 / Math.sin(psi)) * Math.tan(psi) / (1 - this.es));
                            p.x = lon;
                            p.y = lat;
                            return p;
                        }
                    }
                };
                exports.names = [
                    'Azimuthal_Equidistant',
                    'aeqd'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/asinz': 6,
                '../common/e0fn': 7,
                '../common/e1fn': 8,
                '../common/e2fn': 9,
                '../common/e3fn': 10,
                '../common/gN': 11,
                '../common/imlfn': 12,
                '../common/mlfn': 14
            }
        ],
        41: [
            function (_dereq_, module, exports) {
                var mlfn = _dereq_('../common/mlfn');
                var e0fn = _dereq_('../common/e0fn');
                var e1fn = _dereq_('../common/e1fn');
                var e2fn = _dereq_('../common/e2fn');
                var e3fn = _dereq_('../common/e3fn');
                var gN = _dereq_('../common/gN');
                var adjust_lon = _dereq_('../common/adjust_lon');
                var adjust_lat = _dereq_('../common/adjust_lat');
                var imlfn = _dereq_('../common/imlfn');
                var HALF_PI = Math.PI / 2;
                var EPSLN = 1e-10;
                exports.init = function () {
                    if (!this.sphere) {
                        this.e0 = e0fn(this.es);
                        this.e1 = e1fn(this.es);
                        this.e2 = e2fn(this.es);
                        this.e3 = e3fn(this.es);
                        this.ml0 = this.a * mlfn(this.e0, this.e1, this.e2, this.e3, this.lat0);
                    }
                };    /* Cassini forward equations--mapping lat,long to x,y
                 -----------------------------------------------------------------------*/
                /* Cassini forward equations--mapping lat,long to x,y
                 -----------------------------------------------------------------------*/
                exports.forward = function (p) {
                    /* Forward equations
                     -----------------*/
                    var x, y;
                    var lam = p.x;
                    var phi = p.y;
                    lam = adjust_lon(lam - this.long0);
                    if (this.sphere) {
                        x = this.a * Math.asin(Math.cos(phi) * Math.sin(lam));
                        y = this.a * (Math.atan2(Math.tan(phi), Math.cos(lam)) - this.lat0);
                    } else {
                        //ellipsoid
                        var sinphi = Math.sin(phi);
                        var cosphi = Math.cos(phi);
                        var nl = gN(this.a, this.e, sinphi);
                        var tl = Math.tan(phi) * Math.tan(phi);
                        var al = lam * Math.cos(phi);
                        var asq = al * al;
                        var cl = this.es * cosphi * cosphi / (1 - this.es);
                        var ml = this.a * mlfn(this.e0, this.e1, this.e2, this.e3, phi);
                        x = nl * al * (1 - asq * tl * (1 / 6 - (8 - tl + 8 * cl) * asq / 120));
                        y = ml - this.ml0 + nl * sinphi / cosphi * asq * (0.5 + (5 - tl + 6 * cl) * asq / 24);
                    }
                    p.x = x + this.x0;
                    p.y = y + this.y0;
                    return p;
                };    /* Inverse equations
                 -----------------*/
                /* Inverse equations
                 -----------------*/
                exports.inverse = function (p) {
                    p.x -= this.x0;
                    p.y -= this.y0;
                    var x = p.x / this.a;
                    var y = p.y / this.a;
                    var phi, lam;
                    if (this.sphere) {
                        var dd = y + this.lat0;
                        phi = Math.asin(Math.sin(dd) * Math.cos(x));
                        lam = Math.atan2(Math.tan(x), Math.cos(dd));
                    } else {
                        /* ellipsoid */
                        var ml1 = this.ml0 / this.a + y;
                        var phi1 = imlfn(ml1, this.e0, this.e1, this.e2, this.e3);
                        if (Math.abs(Math.abs(phi1) - HALF_PI) <= EPSLN) {
                            p.x = this.long0;
                            p.y = HALF_PI;
                            if (y < 0) {
                                p.y *= -1;
                            }
                            return p;
                        }
                        var nl1 = gN(this.a, this.e, Math.sin(phi1));
                        var rl1 = nl1 * nl1 * nl1 / this.a / this.a * (1 - this.es);
                        var tl1 = Math.pow(Math.tan(phi1), 2);
                        var dl = x * this.a / nl1;
                        var dsq = dl * dl;
                        phi = phi1 - nl1 * Math.tan(phi1) / rl1 * dl * dl * (0.5 - (1 + 3 * tl1) * dl * dl / 24);
                        lam = dl * (1 - dsq * (tl1 / 3 + (1 + 3 * tl1) * tl1 * dsq / 15)) / Math.cos(phi1);
                    }
                    p.x = adjust_lon(lam + this.long0);
                    p.y = adjust_lat(phi);
                    return p;
                };
                exports.names = [
                    'Cassini',
                    'Cassini_Soldner',
                    'cass'
                ];
            },
            {
                '../common/adjust_lat': 4,
                '../common/adjust_lon': 5,
                '../common/e0fn': 7,
                '../common/e1fn': 8,
                '../common/e2fn': 9,
                '../common/e3fn': 10,
                '../common/gN': 11,
                '../common/imlfn': 12,
                '../common/mlfn': 14
            }
        ],
        42: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                var qsfnz = _dereq_('../common/qsfnz');
                var msfnz = _dereq_('../common/msfnz');
                var iqsfnz = _dereq_('../common/iqsfnz');    /*
                 reference:
                 "Cartographic Projection Procedures for the UNIX Environment-
                 A User's Manual" by Gerald I. Evenden,
                 USGS Open File Report 90-284and Release 4 Interim Reports (2003)
                 */
                /*
                 reference:
                 "Cartographic Projection Procedures for the UNIX Environment-
                 A User's Manual" by Gerald I. Evenden,
                 USGS Open File Report 90-284and Release 4 Interim Reports (2003)
                 */
                exports.init = function () {
                    //no-op
                    if (!this.sphere) {
                        this.k0 = msfnz(this.e, Math.sin(this.lat_ts), Math.cos(this.lat_ts));
                    }
                };    /* Cylindrical Equal Area forward equations--mapping lat,long to x,y
                 ------------------------------------------------------------*/
                /* Cylindrical Equal Area forward equations--mapping lat,long to x,y
                 ------------------------------------------------------------*/
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var x, y;    /* Forward equations
                     -----------------*/
                    /* Forward equations
                     -----------------*/
                    var dlon = adjust_lon(lon - this.long0);
                    if (this.sphere) {
                        x = this.x0 + this.a * dlon * Math.cos(this.lat_ts);
                        y = this.y0 + this.a * Math.sin(lat) / Math.cos(this.lat_ts);
                    } else {
                        var qs = qsfnz(this.e, Math.sin(lat));
                        x = this.x0 + this.a * this.k0 * dlon;
                        y = this.y0 + this.a * qs * 0.5 / this.k0;
                    }
                    p.x = x;
                    p.y = y;
                    return p;
                };    /* Cylindrical Equal Area inverse equations--mapping x,y to lat/long
                 ------------------------------------------------------------*/
                /* Cylindrical Equal Area inverse equations--mapping x,y to lat/long
                 ------------------------------------------------------------*/
                exports.inverse = function (p) {
                    p.x -= this.x0;
                    p.y -= this.y0;
                    var lon, lat;
                    if (this.sphere) {
                        lon = adjust_lon(this.long0 + p.x / this.a / Math.cos(this.lat_ts));
                        lat = Math.asin(p.y / this.a * Math.cos(this.lat_ts));
                    } else {
                        lat = iqsfnz(this.e, 2 * p.y * this.k0 / this.a);
                        lon = adjust_lon(this.long0 + p.x / (this.a * this.k0));
                    }
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = ['cea'];
            },
            {
                '../common/adjust_lon': 5,
                '../common/iqsfnz': 13,
                '../common/msfnz': 15,
                '../common/qsfnz': 20
            }
        ],
        43: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                var adjust_lat = _dereq_('../common/adjust_lat');
                exports.init = function () {
                    this.x0 = this.x0 || 0;
                    this.y0 = this.y0 || 0;
                    this.lat0 = this.lat0 || 0;
                    this.long0 = this.long0 || 0;
                    this.lat_ts = this.lat_ts || 0;
                    this.title = this.title || 'Equidistant Cylindrical (Plate Carre)';
                    this.rc = Math.cos(this.lat_ts);
                };    // forward equations--mapping lat,long to x,y
                // -----------------------------------------------------------------
                // forward equations--mapping lat,long to x,y
                // -----------------------------------------------------------------
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var dlon = adjust_lon(lon - this.long0);
                    var dlat = adjust_lat(lat - this.lat0);
                    p.x = this.x0 + this.a * dlon * this.rc;
                    p.y = this.y0 + this.a * dlat;
                    return p;
                };    // inverse equations--mapping x,y to lat/long
                // -----------------------------------------------------------------
                // inverse equations--mapping x,y to lat/long
                // -----------------------------------------------------------------
                exports.inverse = function (p) {
                    var x = p.x;
                    var y = p.y;
                    p.x = adjust_lon(this.long0 + (x - this.x0) / (this.a * this.rc));
                    p.y = adjust_lat(this.lat0 + (y - this.y0) / this.a);
                    return p;
                };
                exports.names = [
                    'Equirectangular',
                    'Equidistant_Cylindrical',
                    'eqc'
                ];
            },
            {
                '../common/adjust_lat': 4,
                '../common/adjust_lon': 5
            }
        ],
        44: [
            function (_dereq_, module, exports) {
                var e0fn = _dereq_('../common/e0fn');
                var e1fn = _dereq_('../common/e1fn');
                var e2fn = _dereq_('../common/e2fn');
                var e3fn = _dereq_('../common/e3fn');
                var msfnz = _dereq_('../common/msfnz');
                var mlfn = _dereq_('../common/mlfn');
                var adjust_lon = _dereq_('../common/adjust_lon');
                var adjust_lat = _dereq_('../common/adjust_lat');
                var imlfn = _dereq_('../common/imlfn');
                var EPSLN = 1e-10;
                exports.init = function () {
                    /* Place parameters in static storage for common use
                     -------------------------------------------------*/
                    // Standard Parallels cannot be equal and on opposite sides of the equator
                    if (Math.abs(this.lat1 + this.lat2) < EPSLN) {
                        return;
                    }
                    this.lat2 = this.lat2 || this.lat1;
                    this.temp = this.b / this.a;
                    this.es = 1 - Math.pow(this.temp, 2);
                    this.e = Math.sqrt(this.es);
                    this.e0 = e0fn(this.es);
                    this.e1 = e1fn(this.es);
                    this.e2 = e2fn(this.es);
                    this.e3 = e3fn(this.es);
                    this.sinphi = Math.sin(this.lat1);
                    this.cosphi = Math.cos(this.lat1);
                    this.ms1 = msfnz(this.e, this.sinphi, this.cosphi);
                    this.ml1 = mlfn(this.e0, this.e1, this.e2, this.e3, this.lat1);
                    if (Math.abs(this.lat1 - this.lat2) < EPSLN) {
                        this.ns = this.sinphi;
                    } else {
                        this.sinphi = Math.sin(this.lat2);
                        this.cosphi = Math.cos(this.lat2);
                        this.ms2 = msfnz(this.e, this.sinphi, this.cosphi);
                        this.ml2 = mlfn(this.e0, this.e1, this.e2, this.e3, this.lat2);
                        this.ns = (this.ms1 - this.ms2) / (this.ml2 - this.ml1);
                    }
                    this.g = this.ml1 + this.ms1 / this.ns;
                    this.ml0 = mlfn(this.e0, this.e1, this.e2, this.e3, this.lat0);
                    this.rh = this.a * (this.g - this.ml0);
                };    /* Equidistant Conic forward equations--mapping lat,long to x,y
                 -----------------------------------------------------------*/
                /* Equidistant Conic forward equations--mapping lat,long to x,y
                 -----------------------------------------------------------*/
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var rh1;    /* Forward equations
                     -----------------*/
                    /* Forward equations
                     -----------------*/
                    if (this.sphere) {
                        rh1 = this.a * (this.g - lat);
                    } else {
                        var ml = mlfn(this.e0, this.e1, this.e2, this.e3, lat);
                        rh1 = this.a * (this.g - ml);
                    }
                    var theta = this.ns * adjust_lon(lon - this.long0);
                    var x = this.x0 + rh1 * Math.sin(theta);
                    var y = this.y0 + this.rh - rh1 * Math.cos(theta);
                    p.x = x;
                    p.y = y;
                    return p;
                };    /* Inverse equations
                 -----------------*/
                /* Inverse equations
                 -----------------*/
                exports.inverse = function (p) {
                    p.x -= this.x0;
                    p.y = this.rh - p.y + this.y0;
                    var con, rh1, lat, lon;
                    if (this.ns >= 0) {
                        rh1 = Math.sqrt(p.x * p.x + p.y * p.y);
                        con = 1;
                    } else {
                        rh1 = -Math.sqrt(p.x * p.x + p.y * p.y);
                        con = -1;
                    }
                    var theta = 0;
                    if (rh1 !== 0) {
                        theta = Math.atan2(con * p.x, con * p.y);
                    }
                    if (this.sphere) {
                        lon = adjust_lon(this.long0 + theta / this.ns);
                        lat = adjust_lat(this.g - rh1 / this.a);
                        p.x = lon;
                        p.y = lat;
                        return p;
                    } else {
                        var ml = this.g - rh1 / this.a;
                        lat = imlfn(ml, this.e0, this.e1, this.e2, this.e3);
                        lon = adjust_lon(this.long0 + theta / this.ns);
                        p.x = lon;
                        p.y = lat;
                        return p;
                    }
                };
                exports.names = [
                    'Equidistant_Conic',
                    'eqdc'
                ];
            },
            {
                '../common/adjust_lat': 4,
                '../common/adjust_lon': 5,
                '../common/e0fn': 7,
                '../common/e1fn': 8,
                '../common/e2fn': 9,
                '../common/e3fn': 10,
                '../common/imlfn': 12,
                '../common/mlfn': 14,
                '../common/msfnz': 15
            }
        ],
        45: [
            function (_dereq_, module, exports) {
                var FORTPI = Math.PI / 4;
                var srat = _dereq_('../common/srat');
                var HALF_PI = Math.PI / 2;
                var MAX_ITER = 20;
                exports.init = function () {
                    var sphi = Math.sin(this.lat0);
                    var cphi = Math.cos(this.lat0);
                    cphi *= cphi;
                    this.rc = Math.sqrt(1 - this.es) / (1 - this.es * sphi * sphi);
                    this.C = Math.sqrt(1 + this.es * cphi * cphi / (1 - this.es));
                    this.phic0 = Math.asin(sphi / this.C);
                    this.ratexp = 0.5 * this.C * this.e;
                    this.K = Math.tan(0.5 * this.phic0 + FORTPI) / (Math.pow(Math.tan(0.5 * this.lat0 + FORTPI), this.C) * srat(this.e * sphi, this.ratexp));
                };
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    p.y = 2 * Math.atan(this.K * Math.pow(Math.tan(0.5 * lat + FORTPI), this.C) * srat(this.e * Math.sin(lat), this.ratexp)) - HALF_PI;
                    p.x = this.C * lon;
                    return p;
                };
                exports.inverse = function (p) {
                    var DEL_TOL = 1e-14;
                    var lon = p.x / this.C;
                    var lat = p.y;
                    var num = Math.pow(Math.tan(0.5 * lat + FORTPI) / this.K, 1 / this.C);
                    for (var i = MAX_ITER; i > 0; --i) {
                        lat = 2 * Math.atan(num * srat(this.e * Math.sin(p.y), -0.5 * this.e)) - HALF_PI;
                        if (Math.abs(lat - p.y) < DEL_TOL) {
                            break;
                        }
                        p.y = lat;
                    }    /* convergence failed */
                    /* convergence failed */
                    if (!i) {
                        return null;
                    }
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = ['gauss'];
            },
            { '../common/srat': 22 }
        ],
        46: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                var EPSLN = 1e-10;
                var asinz = _dereq_('../common/asinz');    /*
                 reference:
                 Wolfram Mathworld "Gnomonic Projection"
                 http://mathworld.wolfram.com/GnomonicProjection.html
                 Accessed: 12th November 2009
                 */
                /*
                 reference:
                 Wolfram Mathworld "Gnomonic Projection"
                 http://mathworld.wolfram.com/GnomonicProjection.html
                 Accessed: 12th November 2009
                 */
                exports.init = function () {
                    /* Place parameters in static storage for common use
                     -------------------------------------------------*/
                    this.sin_p14 = Math.sin(this.lat0);
                    this.cos_p14 = Math.cos(this.lat0);    // Approximation for projecting points to the horizon (infinity)
                    // Approximation for projecting points to the horizon (infinity)
                    this.infinity_dist = 1000 * this.a;
                    this.rc = 1;
                };    /* Gnomonic forward equations--mapping lat,long to x,y
                 ---------------------------------------------------*/
                /* Gnomonic forward equations--mapping lat,long to x,y
                 ---------------------------------------------------*/
                exports.forward = function (p) {
                    var sinphi, cosphi;    /* sin and cos value        */
                    /* sin and cos value        */
                    var dlon;    /* delta longitude value      */
                    /* delta longitude value      */
                    var coslon;    /* cos of longitude        */
                    /* cos of longitude        */
                    var ksp;    /* scale factor          */
                    /* scale factor          */
                    var g;
                    var x, y;
                    var lon = p.x;
                    var lat = p.y;    /* Forward equations
                     -----------------*/
                    /* Forward equations
                     -----------------*/
                    dlon = adjust_lon(lon - this.long0);
                    sinphi = Math.sin(lat);
                    cosphi = Math.cos(lat);
                    coslon = Math.cos(dlon);
                    g = this.sin_p14 * sinphi + this.cos_p14 * cosphi * coslon;
                    ksp = 1;
                    if (g > 0 || Math.abs(g) <= EPSLN) {
                        x = this.x0 + this.a * ksp * cosphi * Math.sin(dlon) / g;
                        y = this.y0 + this.a * ksp * (this.cos_p14 * sinphi - this.sin_p14 * cosphi * coslon) / g;
                    } else {
                        // Point is in the opposing hemisphere and is unprojectable
                        // We still need to return a reasonable point, so we project
                        // to infinity, on a bearing
                        // equivalent to the northern hemisphere equivalent
                        // This is a reasonable approximation for short shapes and lines that
                        // straddle the horizon.
                        x = this.x0 + this.infinity_dist * cosphi * Math.sin(dlon);
                        y = this.y0 + this.infinity_dist * (this.cos_p14 * sinphi - this.sin_p14 * cosphi * coslon);
                    }
                    p.x = x;
                    p.y = y;
                    return p;
                };
                exports.inverse = function (p) {
                    var rh;    /* Rho */
                    /* Rho */
                    var sinc, cosc;
                    var c;
                    var lon, lat;    /* Inverse equations
                     -----------------*/
                    /* Inverse equations
                     -----------------*/
                    p.x = (p.x - this.x0) / this.a;
                    p.y = (p.y - this.y0) / this.a;
                    p.x /= this.k0;
                    p.y /= this.k0;
                    if (rh = Math.sqrt(p.x * p.x + p.y * p.y)) {
                        c = Math.atan2(rh, this.rc);
                        sinc = Math.sin(c);
                        cosc = Math.cos(c);
                        lat = asinz(cosc * this.sin_p14 + p.y * sinc * this.cos_p14 / rh);
                        lon = Math.atan2(p.x * sinc, rh * this.cos_p14 * cosc - p.y * this.sin_p14 * sinc);
                        lon = adjust_lon(this.long0 + lon);
                    } else {
                        lat = this.phic0;
                        lon = 0;
                    }
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = ['gnom'];
            },
            {
                '../common/adjust_lon': 5,
                '../common/asinz': 6
            }
        ],
        47: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                exports.init = function () {
                    this.a = 6377397.155;
                    this.es = 0.006674372230614;
                    this.e = Math.sqrt(this.es);
                    if (!this.lat0) {
                        this.lat0 = 0.863937979737193;
                    }
                    if (!this.long0) {
                        this.long0 = 0.7417649320975901 - 0.308341501185665;
                    }    /* if scale not set default to 0.9999 */
                    /* if scale not set default to 0.9999 */
                    if (!this.k0) {
                        this.k0 = 0.9999;
                    }
                    this.s45 = 0.785398163397448;    /* 45 */
                    /* 45 */
                    this.s90 = 2 * this.s45;
                    this.fi0 = this.lat0;
                    this.e2 = this.es;
                    this.e = Math.sqrt(this.e2);
                    this.alfa = Math.sqrt(1 + this.e2 * Math.pow(Math.cos(this.fi0), 4) / (1 - this.e2));
                    this.uq = 1.04216856380474;
                    this.u0 = Math.asin(Math.sin(this.fi0) / this.alfa);
                    this.g = Math.pow((1 + this.e * Math.sin(this.fi0)) / (1 - this.e * Math.sin(this.fi0)), this.alfa * this.e / 2);
                    this.k = Math.tan(this.u0 / 2 + this.s45) / Math.pow(Math.tan(this.fi0 / 2 + this.s45), this.alfa) * this.g;
                    this.k1 = this.k0;
                    this.n0 = this.a * Math.sqrt(1 - this.e2) / (1 - this.e2 * Math.pow(Math.sin(this.fi0), 2));
                    this.s0 = 1.37008346281555;
                    this.n = Math.sin(this.s0);
                    this.ro0 = this.k1 * this.n0 / Math.tan(this.s0);
                    this.ad = this.s90 - this.uq;
                };    /* ellipsoid */
                /* calculate xy from lat/lon */
                /* Constants, identical to inverse transform function */
                /* ellipsoid */
                /* calculate xy from lat/lon */
                /* Constants, identical to inverse transform function */
                exports.forward = function (p) {
                    var gfi, u, deltav, s, d, eps, ro;
                    var lon = p.x;
                    var lat = p.y;
                    var delta_lon = adjust_lon(lon - this.long0);    /* Transformation */
                    /* Transformation */
                    gfi = Math.pow((1 + this.e * Math.sin(lat)) / (1 - this.e * Math.sin(lat)), this.alfa * this.e / 2);
                    u = 2 * (Math.atan(this.k * Math.pow(Math.tan(lat / 2 + this.s45), this.alfa) / gfi) - this.s45);
                    deltav = -delta_lon * this.alfa;
                    s = Math.asin(Math.cos(this.ad) * Math.sin(u) + Math.sin(this.ad) * Math.cos(u) * Math.cos(deltav));
                    d = Math.asin(Math.cos(u) * Math.sin(deltav) / Math.cos(s));
                    eps = this.n * d;
                    ro = this.ro0 * Math.pow(Math.tan(this.s0 / 2 + this.s45), this.n) / Math.pow(Math.tan(s / 2 + this.s45), this.n);
                    p.y = ro * Math.cos(eps) / 1;
                    p.x = ro * Math.sin(eps) / 1;
                    if (!this.czech) {
                        p.y *= -1;
                        p.x *= -1;
                    }
                    return p;
                };    /* calculate lat/lon from xy */
                /* calculate lat/lon from xy */
                exports.inverse = function (p) {
                    var u, deltav, s, d, eps, ro, fi1;
                    var ok;    /* Transformation */
                    /* revert y, x*/
                    /* Transformation */
                    /* revert y, x*/
                    var tmp = p.x;
                    p.x = p.y;
                    p.y = tmp;
                    if (!this.czech) {
                        p.y *= -1;
                        p.x *= -1;
                    }
                    ro = Math.sqrt(p.x * p.x + p.y * p.y);
                    eps = Math.atan2(p.y, p.x);
                    d = eps / Math.sin(this.s0);
                    s = 2 * (Math.atan(Math.pow(this.ro0 / ro, 1 / this.n) * Math.tan(this.s0 / 2 + this.s45)) - this.s45);
                    u = Math.asin(Math.cos(this.ad) * Math.sin(s) - Math.sin(this.ad) * Math.cos(s) * Math.cos(d));
                    deltav = Math.asin(Math.cos(s) * Math.sin(d) / Math.cos(u));
                    p.x = this.long0 - deltav / this.alfa;
                    fi1 = u;
                    ok = 0;
                    var iter = 0;
                    do {
                        p.y = 2 * (Math.atan(Math.pow(this.k, -1 / this.alfa) * Math.pow(Math.tan(u / 2 + this.s45), 1 / this.alfa) * Math.pow((1 + this.e * Math.sin(fi1)) / (1 - this.e * Math.sin(fi1)), this.e / 2)) - this.s45);
                        if (Math.abs(fi1 - p.y) < 1e-10) {
                            ok = 1;
                        }
                        fi1 = p.y;
                        iter += 1;
                    } while (ok === 0 && iter < 15);
                    if (iter >= 15) {
                        return null;
                    }
                    return p;
                };
                exports.names = [
                    'Krovak',
                    'krovak'
                ];
            },
            { '../common/adjust_lon': 5 }
        ],
        48: [
            function (_dereq_, module, exports) {
                var HALF_PI = Math.PI / 2;
                var FORTPI = Math.PI / 4;
                var EPSLN = 1e-10;
                var qsfnz = _dereq_('../common/qsfnz');
                var adjust_lon = _dereq_('../common/adjust_lon');    /*
                 reference
                 "New Equal-Area Map Projections for Noncircular Regions", John P. Snyder,
                 The American Cartographer, Vol 15, No. 4, October 1988, pp. 341-355.
                 */
                /*
                 reference
                 "New Equal-Area Map Projections for Noncircular Regions", John P. Snyder,
                 The American Cartographer, Vol 15, No. 4, October 1988, pp. 341-355.
                 */
                exports.S_POLE = 1;
                exports.N_POLE = 2;
                exports.EQUIT = 3;
                exports.OBLIQ = 4;    /* Initialize the Lambert Azimuthal Equal Area projection
                 ------------------------------------------------------*/
                /* Initialize the Lambert Azimuthal Equal Area projection
                 ------------------------------------------------------*/
                exports.init = function () {
                    var t = Math.abs(this.lat0);
                    if (Math.abs(t - HALF_PI) < EPSLN) {
                        this.mode = this.lat0 < 0 ? this.S_POLE : this.N_POLE;
                    } else if (Math.abs(t) < EPSLN) {
                        this.mode = this.EQUIT;
                    } else {
                        this.mode = this.OBLIQ;
                    }
                    if (this.es > 0) {
                        var sinphi;
                        this.qp = qsfnz(this.e, 1);
                        this.mmf = 0.5 / (1 - this.es);
                        this.apa = this.authset(this.es);
                        switch (this.mode) {
                        case this.N_POLE:
                            this.dd = 1;
                            break;
                        case this.S_POLE:
                            this.dd = 1;
                            break;
                        case this.EQUIT:
                            this.rq = Math.sqrt(0.5 * this.qp);
                            this.dd = 1 / this.rq;
                            this.xmf = 1;
                            this.ymf = 0.5 * this.qp;
                            break;
                        case this.OBLIQ:
                            this.rq = Math.sqrt(0.5 * this.qp);
                            sinphi = Math.sin(this.lat0);
                            this.sinb1 = qsfnz(this.e, sinphi) / this.qp;
                            this.cosb1 = Math.sqrt(1 - this.sinb1 * this.sinb1);
                            this.dd = Math.cos(this.lat0) / (Math.sqrt(1 - this.es * sinphi * sinphi) * this.rq * this.cosb1);
                            this.ymf = (this.xmf = this.rq) / this.dd;
                            this.xmf *= this.dd;
                            break;
                        }
                    } else {
                        if (this.mode === this.OBLIQ) {
                            this.sinph0 = Math.sin(this.lat0);
                            this.cosph0 = Math.cos(this.lat0);
                        }
                    }
                };    /* Lambert Azimuthal Equal Area forward equations--mapping lat,long to x,y
                 -----------------------------------------------------------------------*/
                /* Lambert Azimuthal Equal Area forward equations--mapping lat,long to x,y
                 -----------------------------------------------------------------------*/
                exports.forward = function (p) {
                    /* Forward equations
                     -----------------*/
                    var x, y, coslam, sinlam, sinphi, q, sinb, cosb, b, cosphi;
                    var lam = p.x;
                    var phi = p.y;
                    lam = adjust_lon(lam - this.long0);
                    if (this.sphere) {
                        sinphi = Math.sin(phi);
                        cosphi = Math.cos(phi);
                        coslam = Math.cos(lam);
                        if (this.mode === this.OBLIQ || this.mode === this.EQUIT) {
                            y = this.mode === this.EQUIT ? 1 + cosphi * coslam : 1 + this.sinph0 * sinphi + this.cosph0 * cosphi * coslam;
                            if (y <= EPSLN) {
                                return null;
                            }
                            y = Math.sqrt(2 / y);
                            x = y * cosphi * Math.sin(lam);
                            y *= this.mode === this.EQUIT ? sinphi : this.cosph0 * sinphi - this.sinph0 * cosphi * coslam;
                        } else if (this.mode === this.N_POLE || this.mode === this.S_POLE) {
                            if (this.mode === this.N_POLE) {
                                coslam = -coslam;
                            }
                            if (Math.abs(phi + this.phi0) < EPSLN) {
                                return null;
                            }
                            y = FORTPI - phi * 0.5;
                            y = 2 * (this.mode === this.S_POLE ? Math.cos(y) : Math.sin(y));
                            x = y * Math.sin(lam);
                            y *= coslam;
                        }
                    } else {
                        sinb = 0;
                        cosb = 0;
                        b = 0;
                        coslam = Math.cos(lam);
                        sinlam = Math.sin(lam);
                        sinphi = Math.sin(phi);
                        q = qsfnz(this.e, sinphi);
                        if (this.mode === this.OBLIQ || this.mode === this.EQUIT) {
                            sinb = q / this.qp;
                            cosb = Math.sqrt(1 - sinb * sinb);
                        }
                        switch (this.mode) {
                        case this.OBLIQ:
                            b = 1 + this.sinb1 * sinb + this.cosb1 * cosb * coslam;
                            break;
                        case this.EQUIT:
                            b = 1 + cosb * coslam;
                            break;
                        case this.N_POLE:
                            b = HALF_PI + phi;
                            q = this.qp - q;
                            break;
                        case this.S_POLE:
                            b = phi - HALF_PI;
                            q = this.qp + q;
                            break;
                        }
                        if (Math.abs(b) < EPSLN) {
                            return null;
                        }
                        switch (this.mode) {
                        case this.OBLIQ:
                        case this.EQUIT:
                            b = Math.sqrt(2 / b);
                            if (this.mode === this.OBLIQ) {
                                y = this.ymf * b * (this.cosb1 * sinb - this.sinb1 * cosb * coslam);
                            } else {
                                y = (b = Math.sqrt(2 / (1 + cosb * coslam))) * sinb * this.ymf;
                            }
                            x = this.xmf * b * cosb * sinlam;
                            break;
                        case this.N_POLE:
                        case this.S_POLE:
                            if (q >= 0) {
                                x = (b = Math.sqrt(q)) * sinlam;
                                y = coslam * (this.mode === this.S_POLE ? b : -b);
                            } else {
                                x = y = 0;
                            }
                            break;
                        }
                    }
                    p.x = this.a * x + this.x0;
                    p.y = this.a * y + this.y0;
                    return p;
                };    /* Inverse equations
                 -----------------*/
                /* Inverse equations
                 -----------------*/
                exports.inverse = function (p) {
                    p.x -= this.x0;
                    p.y -= this.y0;
                    var x = p.x / this.a;
                    var y = p.y / this.a;
                    var lam, phi, cCe, sCe, q, rho, ab;
                    if (this.sphere) {
                        var cosz = 0, rh, sinz = 0;
                        rh = Math.sqrt(x * x + y * y);
                        phi = rh * 0.5;
                        if (phi > 1) {
                            return null;
                        }
                        phi = 2 * Math.asin(phi);
                        if (this.mode === this.OBLIQ || this.mode === this.EQUIT) {
                            sinz = Math.sin(phi);
                            cosz = Math.cos(phi);
                        }
                        switch (this.mode) {
                        case this.EQUIT:
                            phi = Math.abs(rh) <= EPSLN ? 0 : Math.asin(y * sinz / rh);
                            x *= sinz;
                            y = cosz * rh;
                            break;
                        case this.OBLIQ:
                            phi = Math.abs(rh) <= EPSLN ? this.phi0 : Math.asin(cosz * this.sinph0 + y * sinz * this.cosph0 / rh);
                            x *= sinz * this.cosph0;
                            y = (cosz - Math.sin(phi) * this.sinph0) * rh;
                            break;
                        case this.N_POLE:
                            y = -y;
                            phi = HALF_PI - phi;
                            break;
                        case this.S_POLE:
                            phi -= HALF_PI;
                            break;
                        }
                        lam = y === 0 && (this.mode === this.EQUIT || this.mode === this.OBLIQ) ? 0 : Math.atan2(x, y);
                    } else {
                        ab = 0;
                        if (this.mode === this.OBLIQ || this.mode === this.EQUIT) {
                            x /= this.dd;
                            y *= this.dd;
                            rho = Math.sqrt(x * x + y * y);
                            if (rho < EPSLN) {
                                p.x = 0;
                                p.y = this.phi0;
                                return p;
                            }
                            sCe = 2 * Math.asin(0.5 * rho / this.rq);
                            cCe = Math.cos(sCe);
                            x *= sCe = Math.sin(sCe);
                            if (this.mode === this.OBLIQ) {
                                ab = cCe * this.sinb1 + y * sCe * this.cosb1 / rho;
                                q = this.qp * ab;
                                y = rho * this.cosb1 * cCe - y * this.sinb1 * sCe;
                            } else {
                                ab = y * sCe / rho;
                                q = this.qp * ab;
                                y = rho * cCe;
                            }
                        } else if (this.mode === this.N_POLE || this.mode === this.S_POLE) {
                            if (this.mode === this.N_POLE) {
                                y = -y;
                            }
                            q = x * x + y * y;
                            if (!q) {
                                p.x = 0;
                                p.y = this.phi0;
                                return p;
                            }
                            ab = 1 - q / this.qp;
                            if (this.mode === this.S_POLE) {
                                ab = -ab;
                            }
                        }
                        lam = Math.atan2(x, y);
                        phi = this.authlat(Math.asin(ab), this.apa);
                    }
                    p.x = adjust_lon(this.long0 + lam);
                    p.y = phi;
                    return p;
                };    /* determine latitude from authalic latitude */
                /* determine latitude from authalic latitude */
                exports.P00 = 0.3333333333333333;
                exports.P01 = 0.17222222222222222;
                exports.P02 = 0.10257936507936508;
                exports.P10 = 0.06388888888888888;
                exports.P11 = 0.0664021164021164;
                exports.P20 = 0.016415012942191543;
                exports.authset = function (es) {
                    var t;
                    var APA = [];
                    APA[0] = es * this.P00;
                    t = es * es;
                    APA[0] += t * this.P01;
                    APA[1] = t * this.P10;
                    t *= es;
                    APA[0] += t * this.P02;
                    APA[1] += t * this.P11;
                    APA[2] = t * this.P20;
                    return APA;
                };
                exports.authlat = function (beta, APA) {
                    var t = beta + beta;
                    return beta + APA[0] * Math.sin(t) + APA[1] * Math.sin(t + t) + APA[2] * Math.sin(t + t + t);
                };
                exports.names = [
                    'Lambert Azimuthal Equal Area',
                    'Lambert_Azimuthal_Equal_Area',
                    'laea'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/qsfnz': 20
            }
        ],
        49: [
            function (_dereq_, module, exports) {
                var EPSLN = 1e-10;
                var msfnz = _dereq_('../common/msfnz');
                var tsfnz = _dereq_('../common/tsfnz');
                var HALF_PI = Math.PI / 2;
                var sign = _dereq_('../common/sign');
                var adjust_lon = _dereq_('../common/adjust_lon');
                var phi2z = _dereq_('../common/phi2z');
                exports.init = function () {
                    // array of:  r_maj,r_min,lat1,lat2,c_lon,c_lat,false_east,false_north
                    //double c_lat;                   /* center latitude                      */
                    //double c_lon;                   /* center longitude                     */
                    //double lat1;                    /* first standard parallel              */
                    //double lat2;                    /* second standard parallel             */
                    //double r_maj;                   /* major axis                           */
                    //double r_min;                   /* minor axis                           */
                    //double false_east;              /* x offset in meters                   */
                    //double false_north;             /* y offset in meters                   */
                    if (!this.lat2) {
                        this.lat2 = this.lat1;
                    }    //if lat2 is not defined
                    //if lat2 is not defined
                    if (!this.k0) {
                        this.k0 = 1;
                    }
                    this.x0 = this.x0 || 0;
                    this.y0 = this.y0 || 0;    // Standard Parallels cannot be equal and on opposite sides of the equator
                    // Standard Parallels cannot be equal and on opposite sides of the equator
                    if (Math.abs(this.lat1 + this.lat2) < EPSLN) {
                        return;
                    }
                    var temp = this.b / this.a;
                    this.e = Math.sqrt(1 - temp * temp);
                    var sin1 = Math.sin(this.lat1);
                    var cos1 = Math.cos(this.lat1);
                    var ms1 = msfnz(this.e, sin1, cos1);
                    var ts1 = tsfnz(this.e, this.lat1, sin1);
                    var sin2 = Math.sin(this.lat2);
                    var cos2 = Math.cos(this.lat2);
                    var ms2 = msfnz(this.e, sin2, cos2);
                    var ts2 = tsfnz(this.e, this.lat2, sin2);
                    var ts0 = tsfnz(this.e, this.lat0, Math.sin(this.lat0));
                    if (Math.abs(this.lat1 - this.lat2) > EPSLN) {
                        this.ns = Math.log(ms1 / ms2) / Math.log(ts1 / ts2);
                    } else {
                        this.ns = sin1;
                    }
                    if (isNaN(this.ns)) {
                        this.ns = sin1;
                    }
                    this.f0 = ms1 / (this.ns * Math.pow(ts1, this.ns));
                    this.rh = this.a * this.f0 * Math.pow(ts0, this.ns);
                    if (!this.title) {
                        this.title = 'Lambert Conformal Conic';
                    }
                };    // Lambert Conformal conic forward equations--mapping lat,long to x,y
                // -----------------------------------------------------------------
                // Lambert Conformal conic forward equations--mapping lat,long to x,y
                // -----------------------------------------------------------------
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;    // singular cases :
                    // singular cases :
                    if (Math.abs(2 * Math.abs(lat) - Math.PI) <= EPSLN) {
                        lat = sign(lat) * (HALF_PI - 2 * EPSLN);
                    }
                    var con = Math.abs(Math.abs(lat) - HALF_PI);
                    var ts, rh1;
                    if (con > EPSLN) {
                        ts = tsfnz(this.e, lat, Math.sin(lat));
                        rh1 = this.a * this.f0 * Math.pow(ts, this.ns);
                    } else {
                        con = lat * this.ns;
                        if (con <= 0) {
                            return null;
                        }
                        rh1 = 0;
                    }
                    var theta = this.ns * adjust_lon(lon - this.long0);
                    p.x = this.k0 * (rh1 * Math.sin(theta)) + this.x0;
                    p.y = this.k0 * (this.rh - rh1 * Math.cos(theta)) + this.y0;
                    return p;
                };    // Lambert Conformal Conic inverse equations--mapping x,y to lat/long
                // -----------------------------------------------------------------
                // Lambert Conformal Conic inverse equations--mapping x,y to lat/long
                // -----------------------------------------------------------------
                exports.inverse = function (p) {
                    var rh1, con, ts;
                    var lat, lon;
                    var x = (p.x - this.x0) / this.k0;
                    var y = this.rh - (p.y - this.y0) / this.k0;
                    if (this.ns > 0) {
                        rh1 = Math.sqrt(x * x + y * y);
                        con = 1;
                    } else {
                        rh1 = -Math.sqrt(x * x + y * y);
                        con = -1;
                    }
                    var theta = 0;
                    if (rh1 !== 0) {
                        theta = Math.atan2(con * x, con * y);
                    }
                    if (rh1 !== 0 || this.ns > 0) {
                        con = 1 / this.ns;
                        ts = Math.pow(rh1 / (this.a * this.f0), con);
                        lat = phi2z(this.e, ts);
                        if (lat === -9999) {
                            return null;
                        }
                    } else {
                        lat = -HALF_PI;
                    }
                    lon = adjust_lon(theta / this.ns + this.long0);
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Lambert Tangential Conformal Conic Projection',
                    'Lambert_Conformal_Conic',
                    'Lambert_Conformal_Conic_2SP',
                    'lcc'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/msfnz': 15,
                '../common/phi2z': 16,
                '../common/sign': 21,
                '../common/tsfnz': 24
            }
        ],
        50: [
            function (_dereq_, module, exports) {
                exports.init = function () {
                }    //no-op for longlat
                ;
                function identity(pt) {
                    return pt;
                }
                exports.forward = identity;
                exports.inverse = identity;
                exports.names = [
                    'longlat',
                    'identity'
                ];
            },
            {}
        ],
        51: [
            function (_dereq_, module, exports) {
                var msfnz = _dereq_('../common/msfnz');
                var HALF_PI = Math.PI / 2;
                var EPSLN = 1e-10;
                var R2D = 57.29577951308232;
                var adjust_lon = _dereq_('../common/adjust_lon');
                var FORTPI = Math.PI / 4;
                var tsfnz = _dereq_('../common/tsfnz');
                var phi2z = _dereq_('../common/phi2z');
                exports.init = function () {
                    var con = this.b / this.a;
                    this.es = 1 - con * con;
                    if (!('x0' in this)) {
                        this.x0 = 0;
                    }
                    if (!('y0' in this)) {
                        this.y0 = 0;
                    }
                    this.e = Math.sqrt(this.es);
                    if (this.lat_ts) {
                        if (this.sphere) {
                            this.k0 = Math.cos(this.lat_ts);
                        } else {
                            this.k0 = msfnz(this.e, Math.sin(this.lat_ts), Math.cos(this.lat_ts));
                        }
                    } else {
                        if (!this.k0) {
                            if (this.k) {
                                this.k0 = this.k;
                            } else {
                                this.k0 = 1;
                            }
                        }
                    }
                };    /* Mercator forward equations--mapping lat,long to x,y
                 --------------------------------------------------*/
                /* Mercator forward equations--mapping lat,long to x,y
                 --------------------------------------------------*/
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;    // convert to radians
                    // convert to radians
                    if (lat * R2D > 90 && lat * R2D < -90 && lon * R2D > 180 && lon * R2D < -180) {
                        return null;
                    }
                    var x, y;
                    if (Math.abs(Math.abs(lat) - HALF_PI) <= EPSLN) {
                        return null;
                    } else {
                        if (this.sphere) {
                            x = this.x0 + this.a * this.k0 * adjust_lon(lon - this.long0);
                            y = this.y0 + this.a * this.k0 * Math.log(Math.tan(FORTPI + 0.5 * lat));
                        } else {
                            var sinphi = Math.sin(lat);
                            var ts = tsfnz(this.e, lat, sinphi);
                            x = this.x0 + this.a * this.k0 * adjust_lon(lon - this.long0);
                            y = this.y0 - this.a * this.k0 * Math.log(ts);
                        }
                        p.x = x;
                        p.y = y;
                        return p;
                    }
                };    /* Mercator inverse equations--mapping x,y to lat/long
                 --------------------------------------------------*/
                /* Mercator inverse equations--mapping x,y to lat/long
                 --------------------------------------------------*/
                exports.inverse = function (p) {
                    var x = p.x - this.x0;
                    var y = p.y - this.y0;
                    var lon, lat;
                    if (this.sphere) {
                        lat = HALF_PI - 2 * Math.atan(Math.exp(-y / (this.a * this.k0)));
                    } else {
                        var ts = Math.exp(-y / (this.a * this.k0));
                        lat = phi2z(this.e, ts);
                        if (lat === -9999) {
                            return null;
                        }
                    }
                    lon = adjust_lon(this.long0 + x / (this.a * this.k0));
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Mercator',
                    'Popular Visualisation Pseudo Mercator',
                    'Mercator_1SP',
                    'Mercator_Auxiliary_Sphere',
                    'merc'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/msfnz': 15,
                '../common/phi2z': 16,
                '../common/tsfnz': 24
            }
        ],
        52: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');    /*
                 reference
                 "New Equal-Area Map Projections for Noncircular Regions", John P. Snyder,
                 The American Cartographer, Vol 15, No. 4, October 1988, pp. 341-355.
                 */
                /* Initialize the Miller Cylindrical projection
                 -------------------------------------------*/
                /*
                 reference
                 "New Equal-Area Map Projections for Noncircular Regions", John P. Snyder,
                 The American Cartographer, Vol 15, No. 4, October 1988, pp. 341-355.
                 */
                /* Initialize the Miller Cylindrical projection
                 -------------------------------------------*/
                exports.init = function () {
                }    //no-op
                ;    /* Miller Cylindrical forward equations--mapping lat,long to x,y
                 ------------------------------------------------------------*/
                /* Miller Cylindrical forward equations--mapping lat,long to x,y
                 ------------------------------------------------------------*/
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;    /* Forward equations
                     -----------------*/
                    /* Forward equations
                     -----------------*/
                    var dlon = adjust_lon(lon - this.long0);
                    var x = this.x0 + this.a * dlon;
                    var y = this.y0 + this.a * Math.log(Math.tan(Math.PI / 4 + lat / 2.5)) * 1.25;
                    p.x = x;
                    p.y = y;
                    return p;
                };    /* Miller Cylindrical inverse equations--mapping x,y to lat/long
                 ------------------------------------------------------------*/
                /* Miller Cylindrical inverse equations--mapping x,y to lat/long
                 ------------------------------------------------------------*/
                exports.inverse = function (p) {
                    p.x -= this.x0;
                    p.y -= this.y0;
                    var lon = adjust_lon(this.long0 + p.x / this.a);
                    var lat = 2.5 * (Math.atan(Math.exp(0.8 * p.y / this.a)) - Math.PI / 4);
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Miller_Cylindrical',
                    'mill'
                ];
            },
            { '../common/adjust_lon': 5 }
        ],
        53: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                var EPSLN = 1e-10;
                exports.init = function () {
                };    /* Mollweide forward equations--mapping lat,long to x,y
                 ----------------------------------------------------*/
                /* Mollweide forward equations--mapping lat,long to x,y
                 ----------------------------------------------------*/
                exports.forward = function (p) {
                    /* Forward equations
                     -----------------*/
                    var lon = p.x;
                    var lat = p.y;
                    var delta_lon = adjust_lon(lon - this.long0);
                    var theta = lat;
                    var con = Math.PI * Math.sin(lat);    /* Iterate using the Newton-Raphson method to find theta
                     -----------------------------------------------------*/
                    /* Iterate using the Newton-Raphson method to find theta
                     -----------------------------------------------------*/
                    for (var i = 0; true; i++) {
                        var delta_theta = -(theta + Math.sin(theta) - con) / (1 + Math.cos(theta));
                        theta += delta_theta;
                        if (Math.abs(delta_theta) < EPSLN) {
                            break;
                        }
                    }
                    theta /= 2;    /* If the latitude is 90 deg, force the x coordinate to be "0 + false easting"
                     this is done here because of precision problems with "cos(theta)"
                     --------------------------------------------------------------------------*/
                    /* If the latitude is 90 deg, force the x coordinate to be "0 + false easting"
                     this is done here because of precision problems with "cos(theta)"
                     --------------------------------------------------------------------------*/
                    if (Math.PI / 2 - Math.abs(lat) < EPSLN) {
                        delta_lon = 0;
                    }
                    var x = 0.900316316158 * this.a * delta_lon * Math.cos(theta) + this.x0;
                    var y = 1.4142135623731 * this.a * Math.sin(theta) + this.y0;
                    p.x = x;
                    p.y = y;
                    return p;
                };
                exports.inverse = function (p) {
                    var theta;
                    var arg;    /* Inverse equations
                     -----------------*/
                    /* Inverse equations
                     -----------------*/
                    p.x -= this.x0;
                    p.y -= this.y0;
                    arg = p.y / (1.4142135623731 * this.a);    /* Because of division by zero problems, 'arg' can not be 1.  Therefore
                     a number very close to one is used instead.
                     -------------------------------------------------------------------*/
                    /* Because of division by zero problems, 'arg' can not be 1.  Therefore
                     a number very close to one is used instead.
                     -------------------------------------------------------------------*/
                    if (Math.abs(arg) > 0.999999999999) {
                        arg = 0.999999999999;
                    }
                    theta = Math.asin(arg);
                    var lon = adjust_lon(this.long0 + p.x / (0.900316316158 * this.a * Math.cos(theta)));
                    if (lon < -Math.PI) {
                        lon = -Math.PI;
                    }
                    if (lon > Math.PI) {
                        lon = Math.PI;
                    }
                    arg = (2 * theta + Math.sin(2 * theta)) / Math.PI;
                    if (Math.abs(arg) > 1) {
                        arg = 1;
                    }
                    var lat = Math.asin(arg);
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Mollweide',
                    'moll'
                ];
            },
            { '../common/adjust_lon': 5 }
        ],
        54: [
            function (_dereq_, module, exports) {
                var SEC_TO_RAD = 0.00000484813681109536;    /*
                 reference
                 Department of Land and Survey Technical Circular 1973/32
                 http://www.linz.govt.nz/docs/miscellaneous/nz-map-definition.pdf
                 OSG Technical Report 4.1
                 http://www.linz.govt.nz/docs/miscellaneous/nzmg.pdf
                 */
                /**
                 * iterations: Number of iterations to refine inverse transform.
                 *     0 -> km accuracy
                 *     1 -> m accuracy -- suitable for most mapping applications
                 *     2 -> mm accuracy
                 */
                /*
                 reference
                 Department of Land and Survey Technical Circular 1973/32
                 http://www.linz.govt.nz/docs/miscellaneous/nz-map-definition.pdf
                 OSG Technical Report 4.1
                 http://www.linz.govt.nz/docs/miscellaneous/nzmg.pdf
                 */
                /**
                 * iterations: Number of iterations to refine inverse transform.
                 *     0 -> km accuracy
                 *     1 -> m accuracy -- suitable for most mapping applications
                 *     2 -> mm accuracy
                 */
                exports.iterations = 1;
                exports.init = function () {
                    this.A = [];
                    this.A[1] = 0.6399175073;
                    this.A[2] = -0.1358797613;
                    this.A[3] = 0.063294409;
                    this.A[4] = -0.02526853;
                    this.A[5] = 0.0117879;
                    this.A[6] = -0.0055161;
                    this.A[7] = 0.0026906;
                    this.A[8] = -0.001333;
                    this.A[9] = 0.00067;
                    this.A[10] = -0.00034;
                    this.B_re = [];
                    this.B_im = [];
                    this.B_re[1] = 0.7557853228;
                    this.B_im[1] = 0;
                    this.B_re[2] = 0.249204646;
                    this.B_im[2] = 0.003371507;
                    this.B_re[3] = -0.001541739;
                    this.B_im[3] = 0.04105856;
                    this.B_re[4] = -0.10162907;
                    this.B_im[4] = 0.01727609;
                    this.B_re[5] = -0.26623489;
                    this.B_im[5] = -0.36249218;
                    this.B_re[6] = -0.6870983;
                    this.B_im[6] = -1.1651967;
                    this.C_re = [];
                    this.C_im = [];
                    this.C_re[1] = 1.3231270439;
                    this.C_im[1] = 0;
                    this.C_re[2] = -0.577245789;
                    this.C_im[2] = -0.007809598;
                    this.C_re[3] = 0.508307513;
                    this.C_im[3] = -0.112208952;
                    this.C_re[4] = -0.15094762;
                    this.C_im[4] = 0.18200602;
                    this.C_re[5] = 1.01418179;
                    this.C_im[5] = 1.64497696;
                    this.C_re[6] = 1.9660549;
                    this.C_im[6] = 2.5127645;
                    this.D = [];
                    this.D[1] = 1.5627014243;
                    this.D[2] = 0.5185406398;
                    this.D[3] = -0.03333098;
                    this.D[4] = -0.1052906;
                    this.D[5] = -0.0368594;
                    this.D[6] = 0.007317;
                    this.D[7] = 0.0122;
                    this.D[8] = 0.00394;
                    this.D[9] = -0.0013;
                };    /**
                 New Zealand Map Grid Forward  - long/lat to x/y
                 long/lat in radians
                 */
                /**
                 New Zealand Map Grid Forward  - long/lat to x/y
                 long/lat in radians
                 */
                exports.forward = function (p) {
                    var n;
                    var lon = p.x;
                    var lat = p.y;
                    var delta_lat = lat - this.lat0;
                    var delta_lon = lon - this.long0;    // 1. Calculate d_phi and d_psi    ...                          // and d_lambda
                    // For this algorithm, delta_latitude is in seconds of arc x 10-5, so we need to scale to those units. Longitude is radians.
                    // 1. Calculate d_phi and d_psi    ...                          // and d_lambda
                    // For this algorithm, delta_latitude is in seconds of arc x 10-5, so we need to scale to those units. Longitude is radians.
                    var d_phi = delta_lat / SEC_TO_RAD * 0.00001;
                    var d_lambda = delta_lon;
                    var d_phi_n = 1;    // d_phi^0
                    // d_phi^0
                    var d_psi = 0;
                    for (n = 1; n <= 10; n++) {
                        d_phi_n = d_phi_n * d_phi;
                        d_psi = d_psi + this.A[n] * d_phi_n;
                    }    // 2. Calculate theta
                    // 2. Calculate theta
                    var th_re = d_psi;
                    var th_im = d_lambda;    // 3. Calculate z
                    // 3. Calculate z
                    var th_n_re = 1;
                    var th_n_im = 0;    // theta^0
                    // theta^0
                    var th_n_re1;
                    var th_n_im1;
                    var z_re = 0;
                    var z_im = 0;
                    for (n = 1; n <= 6; n++) {
                        th_n_re1 = th_n_re * th_re - th_n_im * th_im;
                        th_n_im1 = th_n_im * th_re + th_n_re * th_im;
                        th_n_re = th_n_re1;
                        th_n_im = th_n_im1;
                        z_re = z_re + this.B_re[n] * th_n_re - this.B_im[n] * th_n_im;
                        z_im = z_im + this.B_im[n] * th_n_re + this.B_re[n] * th_n_im;
                    }    // 4. Calculate easting and northing
                    // 4. Calculate easting and northing
                    p.x = z_im * this.a + this.x0;
                    p.y = z_re * this.a + this.y0;
                    return p;
                };    /**
                 New Zealand Map Grid Inverse  -  x/y to long/lat
                 */
                /**
                 New Zealand Map Grid Inverse  -  x/y to long/lat
                 */
                exports.inverse = function (p) {
                    var n;
                    var x = p.x;
                    var y = p.y;
                    var delta_x = x - this.x0;
                    var delta_y = y - this.y0;    // 1. Calculate z
                    // 1. Calculate z
                    var z_re = delta_y / this.a;
                    var z_im = delta_x / this.a;    // 2a. Calculate theta - first approximation gives km accuracy
                    // 2a. Calculate theta - first approximation gives km accuracy
                    var z_n_re = 1;
                    var z_n_im = 0;    // z^0
                    // z^0
                    var z_n_re1;
                    var z_n_im1;
                    var th_re = 0;
                    var th_im = 0;
                    for (n = 1; n <= 6; n++) {
                        z_n_re1 = z_n_re * z_re - z_n_im * z_im;
                        z_n_im1 = z_n_im * z_re + z_n_re * z_im;
                        z_n_re = z_n_re1;
                        z_n_im = z_n_im1;
                        th_re = th_re + this.C_re[n] * z_n_re - this.C_im[n] * z_n_im;
                        th_im = th_im + this.C_im[n] * z_n_re + this.C_re[n] * z_n_im;
                    }    // 2b. Iterate to refine the accuracy of the calculation
                    //        0 iterations gives km accuracy
                    //        1 iteration gives m accuracy -- good enough for most mapping applications
                    //        2 iterations bives mm accuracy
                    // 2b. Iterate to refine the accuracy of the calculation
                    //        0 iterations gives km accuracy
                    //        1 iteration gives m accuracy -- good enough for most mapping applications
                    //        2 iterations bives mm accuracy
                    for (var i = 0; i < this.iterations; i++) {
                        var th_n_re = th_re;
                        var th_n_im = th_im;
                        var th_n_re1;
                        var th_n_im1;
                        var num_re = z_re;
                        var num_im = z_im;
                        for (n = 2; n <= 6; n++) {
                            th_n_re1 = th_n_re * th_re - th_n_im * th_im;
                            th_n_im1 = th_n_im * th_re + th_n_re * th_im;
                            th_n_re = th_n_re1;
                            th_n_im = th_n_im1;
                            num_re = num_re + (n - 1) * (this.B_re[n] * th_n_re - this.B_im[n] * th_n_im);
                            num_im = num_im + (n - 1) * (this.B_im[n] * th_n_re + this.B_re[n] * th_n_im);
                        }
                        th_n_re = 1;
                        th_n_im = 0;
                        var den_re = this.B_re[1];
                        var den_im = this.B_im[1];
                        for (n = 2; n <= 6; n++) {
                            th_n_re1 = th_n_re * th_re - th_n_im * th_im;
                            th_n_im1 = th_n_im * th_re + th_n_re * th_im;
                            th_n_re = th_n_re1;
                            th_n_im = th_n_im1;
                            den_re = den_re + n * (this.B_re[n] * th_n_re - this.B_im[n] * th_n_im);
                            den_im = den_im + n * (this.B_im[n] * th_n_re + this.B_re[n] * th_n_im);
                        }    // Complex division
                        // Complex division
                        var den2 = den_re * den_re + den_im * den_im;
                        th_re = (num_re * den_re + num_im * den_im) / den2;
                        th_im = (num_im * den_re - num_re * den_im) / den2;
                    }    // 3. Calculate d_phi              ...                                    // and d_lambda
                    // 3. Calculate d_phi              ...                                    // and d_lambda
                    var d_psi = th_re;
                    var d_lambda = th_im;
                    var d_psi_n = 1;    // d_psi^0
                    // d_psi^0
                    var d_phi = 0;
                    for (n = 1; n <= 9; n++) {
                        d_psi_n = d_psi_n * d_psi;
                        d_phi = d_phi + this.D[n] * d_psi_n;
                    }    // 4. Calculate latitude and longitude
                    // d_phi is calcuated in second of arc * 10^-5, so we need to scale back to radians. d_lambda is in radians.
                    // 4. Calculate latitude and longitude
                    // d_phi is calcuated in second of arc * 10^-5, so we need to scale back to radians. d_lambda is in radians.
                    var lat = this.lat0 + d_phi * SEC_TO_RAD * 100000;
                    var lon = this.long0 + d_lambda;
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'New_Zealand_Map_Grid',
                    'nzmg'
                ];
            },
            {}
        ],
        55: [
            function (_dereq_, module, exports) {
                var tsfnz = _dereq_('../common/tsfnz');
                var adjust_lon = _dereq_('../common/adjust_lon');
                var phi2z = _dereq_('../common/phi2z');
                var HALF_PI = Math.PI / 2;
                var FORTPI = Math.PI / 4;
                var EPSLN = 1e-10;    /* Initialize the Oblique Mercator  projection
                 ------------------------------------------*/
                /* Initialize the Oblique Mercator  projection
                 ------------------------------------------*/
                exports.init = function () {
                    this.no_off = this.no_off || false;
                    this.no_rot = this.no_rot || false;
                    if (isNaN(this.k0)) {
                        this.k0 = 1;
                    }
                    var sinlat = Math.sin(this.lat0);
                    var coslat = Math.cos(this.lat0);
                    var con = this.e * sinlat;
                    this.bl = Math.sqrt(1 + this.es / (1 - this.es) * Math.pow(coslat, 4));
                    this.al = this.a * this.bl * this.k0 * Math.sqrt(1 - this.es) / (1 - con * con);
                    var t0 = tsfnz(this.e, this.lat0, sinlat);
                    var dl = this.bl / coslat * Math.sqrt((1 - this.es) / (1 - con * con));
                    if (dl * dl < 1) {
                        dl = 1;
                    }
                    var fl;
                    var gl;
                    if (!isNaN(this.longc)) {
                        //Central point and azimuth method
                        if (this.lat0 >= 0) {
                            fl = dl + Math.sqrt(dl * dl - 1);
                        } else {
                            fl = dl - Math.sqrt(dl * dl - 1);
                        }
                        this.el = fl * Math.pow(t0, this.bl);
                        gl = 0.5 * (fl - 1 / fl);
                        this.gamma0 = Math.asin(Math.sin(this.alpha) / dl);
                        this.long0 = this.longc - Math.asin(gl * Math.tan(this.gamma0)) / this.bl;
                    } else {
                        //2 points method
                        var t1 = tsfnz(this.e, this.lat1, Math.sin(this.lat1));
                        var t2 = tsfnz(this.e, this.lat2, Math.sin(this.lat2));
                        if (this.lat0 >= 0) {
                            this.el = (dl + Math.sqrt(dl * dl - 1)) * Math.pow(t0, this.bl);
                        } else {
                            this.el = (dl - Math.sqrt(dl * dl - 1)) * Math.pow(t0, this.bl);
                        }
                        var hl = Math.pow(t1, this.bl);
                        var ll = Math.pow(t2, this.bl);
                        fl = this.el / hl;
                        gl = 0.5 * (fl - 1 / fl);
                        var jl = (this.el * this.el - ll * hl) / (this.el * this.el + ll * hl);
                        var pl = (ll - hl) / (ll + hl);
                        var dlon12 = adjust_lon(this.long1 - this.long2);
                        this.long0 = 0.5 * (this.long1 + this.long2) - Math.atan(jl * Math.tan(0.5 * this.bl * dlon12) / pl) / this.bl;
                        this.long0 = adjust_lon(this.long0);
                        var dlon10 = adjust_lon(this.long1 - this.long0);
                        this.gamma0 = Math.atan(Math.sin(this.bl * dlon10) / gl);
                        this.alpha = Math.asin(dl * Math.sin(this.gamma0));
                    }
                    if (this.no_off) {
                        this.uc = 0;
                    } else {
                        if (this.lat0 >= 0) {
                            this.uc = this.al / this.bl * Math.atan2(Math.sqrt(dl * dl - 1), Math.cos(this.alpha));
                        } else {
                            this.uc = -1 * this.al / this.bl * Math.atan2(Math.sqrt(dl * dl - 1), Math.cos(this.alpha));
                        }
                    }
                };    /* Oblique Mercator forward equations--mapping lat,long to x,y
                 ----------------------------------------------------------*/
                /* Oblique Mercator forward equations--mapping lat,long to x,y
                 ----------------------------------------------------------*/
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var dlon = adjust_lon(lon - this.long0);
                    var us, vs;
                    var con;
                    if (Math.abs(Math.abs(lat) - HALF_PI) <= EPSLN) {
                        if (lat > 0) {
                            con = -1;
                        } else {
                            con = 1;
                        }
                        vs = this.al / this.bl * Math.log(Math.tan(FORTPI + con * this.gamma0 * 0.5));
                        us = -1 * con * HALF_PI * this.al / this.bl;
                    } else {
                        var t = tsfnz(this.e, lat, Math.sin(lat));
                        var ql = this.el / Math.pow(t, this.bl);
                        var sl = 0.5 * (ql - 1 / ql);
                        var tl = 0.5 * (ql + 1 / ql);
                        var vl = Math.sin(this.bl * dlon);
                        var ul = (sl * Math.sin(this.gamma0) - vl * Math.cos(this.gamma0)) / tl;
                        if (Math.abs(Math.abs(ul) - 1) <= EPSLN) {
                            vs = Number.POSITIVE_INFINITY;
                        } else {
                            vs = 0.5 * this.al * Math.log((1 - ul) / (1 + ul)) / this.bl;
                        }
                        if (Math.abs(Math.cos(this.bl * dlon)) <= EPSLN) {
                            us = this.al * this.bl * dlon;
                        } else {
                            us = this.al * Math.atan2(sl * Math.cos(this.gamma0) + vl * Math.sin(this.gamma0), Math.cos(this.bl * dlon)) / this.bl;
                        }
                    }
                    if (this.no_rot) {
                        p.x = this.x0 + us;
                        p.y = this.y0 + vs;
                    } else {
                        us -= this.uc;
                        p.x = this.x0 + vs * Math.cos(this.alpha) + us * Math.sin(this.alpha);
                        p.y = this.y0 + us * Math.cos(this.alpha) - vs * Math.sin(this.alpha);
                    }
                    return p;
                };
                exports.inverse = function (p) {
                    var us, vs;
                    if (this.no_rot) {
                        vs = p.y - this.y0;
                        us = p.x - this.x0;
                    } else {
                        vs = (p.x - this.x0) * Math.cos(this.alpha) - (p.y - this.y0) * Math.sin(this.alpha);
                        us = (p.y - this.y0) * Math.cos(this.alpha) + (p.x - this.x0) * Math.sin(this.alpha);
                        us += this.uc;
                    }
                    var qp = Math.exp(-1 * this.bl * vs / this.al);
                    var sp = 0.5 * (qp - 1 / qp);
                    var tp = 0.5 * (qp + 1 / qp);
                    var vp = Math.sin(this.bl * us / this.al);
                    var up = (vp * Math.cos(this.gamma0) + sp * Math.sin(this.gamma0)) / tp;
                    var ts = Math.pow(this.el / Math.sqrt((1 + up) / (1 - up)), 1 / this.bl);
                    if (Math.abs(up - 1) < EPSLN) {
                        p.x = this.long0;
                        p.y = HALF_PI;
                    } else if (Math.abs(up + 1) < EPSLN) {
                        p.x = this.long0;
                        p.y = -1 * HALF_PI;
                    } else {
                        p.y = phi2z(this.e, ts);
                        p.x = adjust_lon(this.long0 - Math.atan2(sp * Math.cos(this.gamma0) - vp * Math.sin(this.gamma0), Math.cos(this.bl * us / this.al)) / this.bl);
                    }
                    return p;
                };
                exports.names = [
                    'Hotine_Oblique_Mercator',
                    'Hotine Oblique Mercator',
                    'Hotine_Oblique_Mercator_Azimuth_Natural_Origin',
                    'Hotine_Oblique_Mercator_Azimuth_Center',
                    'omerc'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/phi2z': 16,
                '../common/tsfnz': 24
            }
        ],
        56: [
            function (_dereq_, module, exports) {
                var e0fn = _dereq_('../common/e0fn');
                var e1fn = _dereq_('../common/e1fn');
                var e2fn = _dereq_('../common/e2fn');
                var e3fn = _dereq_('../common/e3fn');
                var adjust_lon = _dereq_('../common/adjust_lon');
                var adjust_lat = _dereq_('../common/adjust_lat');
                var mlfn = _dereq_('../common/mlfn');
                var EPSLN = 1e-10;
                var gN = _dereq_('../common/gN');
                var MAX_ITER = 20;
                exports.init = function () {
                    /* Place parameters in static storage for common use
                     -------------------------------------------------*/
                    this.temp = this.b / this.a;
                    this.es = 1 - Math.pow(this.temp, 2);    // devait etre dans tmerc.js mais n y est pas donc je commente sinon retour de valeurs nulles
                    // devait etre dans tmerc.js mais n y est pas donc je commente sinon retour de valeurs nulles
                    this.e = Math.sqrt(this.es);
                    this.e0 = e0fn(this.es);
                    this.e1 = e1fn(this.es);
                    this.e2 = e2fn(this.es);
                    this.e3 = e3fn(this.es);
                    this.ml0 = this.a * mlfn(this.e0, this.e1, this.e2, this.e3, this.lat0);    //si que des zeros le calcul ne se fait pas
                };    /* Polyconic forward equations--mapping lat,long to x,y
                 ---------------------------------------------------*/
                /* Polyconic forward equations--mapping lat,long to x,y
                 ---------------------------------------------------*/
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var x, y, el;
                    var dlon = adjust_lon(lon - this.long0);
                    el = dlon * Math.sin(lat);
                    if (this.sphere) {
                        if (Math.abs(lat) <= EPSLN) {
                            x = this.a * dlon;
                            y = -1 * this.a * this.lat0;
                        } else {
                            x = this.a * Math.sin(el) / Math.tan(lat);
                            y = this.a * (adjust_lat(lat - this.lat0) + (1 - Math.cos(el)) / Math.tan(lat));
                        }
                    } else {
                        if (Math.abs(lat) <= EPSLN) {
                            x = this.a * dlon;
                            y = -1 * this.ml0;
                        } else {
                            var nl = gN(this.a, this.e, Math.sin(lat)) / Math.tan(lat);
                            x = nl * Math.sin(el);
                            y = this.a * mlfn(this.e0, this.e1, this.e2, this.e3, lat) - this.ml0 + nl * (1 - Math.cos(el));
                        }
                    }
                    p.x = x + this.x0;
                    p.y = y + this.y0;
                    return p;
                };    /* Inverse equations
                 -----------------*/
                /* Inverse equations
                 -----------------*/
                exports.inverse = function (p) {
                    var lon, lat, x, y, i;
                    var al, bl;
                    var phi, dphi;
                    x = p.x - this.x0;
                    y = p.y - this.y0;
                    if (this.sphere) {
                        if (Math.abs(y + this.a * this.lat0) <= EPSLN) {
                            lon = adjust_lon(x / this.a + this.long0);
                            lat = 0;
                        } else {
                            al = this.lat0 + y / this.a;
                            bl = x * x / this.a / this.a + al * al;
                            phi = al;
                            var tanphi;
                            for (i = MAX_ITER; i; --i) {
                                tanphi = Math.tan(phi);
                                dphi = -1 * (al * (phi * tanphi + 1) - phi - 0.5 * (phi * phi + bl) * tanphi) / ((phi - al) / tanphi - 1);
                                phi += dphi;
                                if (Math.abs(dphi) <= EPSLN) {
                                    lat = phi;
                                    break;
                                }
                            }
                            lon = adjust_lon(this.long0 + Math.asin(x * Math.tan(phi) / this.a) / Math.sin(lat));
                        }
                    } else {
                        if (Math.abs(y + this.ml0) <= EPSLN) {
                            lat = 0;
                            lon = adjust_lon(this.long0 + x / this.a);
                        } else {
                            al = (this.ml0 + y) / this.a;
                            bl = x * x / this.a / this.a + al * al;
                            phi = al;
                            var cl, mln, mlnp, ma;
                            var con;
                            for (i = MAX_ITER; i; --i) {
                                con = this.e * Math.sin(phi);
                                cl = Math.sqrt(1 - con * con) * Math.tan(phi);
                                mln = this.a * mlfn(this.e0, this.e1, this.e2, this.e3, phi);
                                mlnp = this.e0 - 2 * this.e1 * Math.cos(2 * phi) + 4 * this.e2 * Math.cos(4 * phi) - 6 * this.e3 * Math.cos(6 * phi);
                                ma = mln / this.a;
                                dphi = (al * (cl * ma + 1) - ma - 0.5 * cl * (ma * ma + bl)) / (this.es * Math.sin(2 * phi) * (ma * ma + bl - 2 * al * ma) / (4 * cl) + (al - ma) * (cl * mlnp - 2 / Math.sin(2 * phi)) - mlnp);
                                phi -= dphi;
                                if (Math.abs(dphi) <= EPSLN) {
                                    lat = phi;
                                    break;
                                }
                            }    //lat=phi4z(this.e,this.e0,this.e1,this.e2,this.e3,al,bl,0,0);
                            //lat=phi4z(this.e,this.e0,this.e1,this.e2,this.e3,al,bl,0,0);
                            cl = Math.sqrt(1 - this.es * Math.pow(Math.sin(lat), 2)) * Math.tan(lat);
                            lon = adjust_lon(this.long0 + Math.asin(x * cl / this.a) / Math.sin(lat));
                        }
                    }
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Polyconic',
                    'poly'
                ];
            },
            {
                '../common/adjust_lat': 4,
                '../common/adjust_lon': 5,
                '../common/e0fn': 7,
                '../common/e1fn': 8,
                '../common/e2fn': 9,
                '../common/e3fn': 10,
                '../common/gN': 11,
                '../common/mlfn': 14
            }
        ],
        57: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                var adjust_lat = _dereq_('../common/adjust_lat');
                var pj_enfn = _dereq_('../common/pj_enfn');
                var MAX_ITER = 20;
                var pj_mlfn = _dereq_('../common/pj_mlfn');
                var pj_inv_mlfn = _dereq_('../common/pj_inv_mlfn');
                var HALF_PI = Math.PI / 2;
                var EPSLN = 1e-10;
                var asinz = _dereq_('../common/asinz');
                exports.init = function () {
                    /* Place parameters in static storage for common use
                     -------------------------------------------------*/
                    if (!this.sphere) {
                        this.en = pj_enfn(this.es);
                    } else {
                        this.n = 1;
                        this.m = 0;
                        this.es = 0;
                        this.C_y = Math.sqrt((this.m + 1) / this.n);
                        this.C_x = this.C_y / (this.m + 1);
                    }
                };    /* Sinusoidal forward equations--mapping lat,long to x,y
                 -----------------------------------------------------*/
                /* Sinusoidal forward equations--mapping lat,long to x,y
                 -----------------------------------------------------*/
                exports.forward = function (p) {
                    var x, y;
                    var lon = p.x;
                    var lat = p.y;    /* Forward equations
                     -----------------*/
                    /* Forward equations
                     -----------------*/
                    lon = adjust_lon(lon - this.long0);
                    if (this.sphere) {
                        if (!this.m) {
                            lat = this.n !== 1 ? Math.asin(this.n * Math.sin(lat)) : lat;
                        } else {
                            var k = this.n * Math.sin(lat);
                            for (var i = MAX_ITER; i; --i) {
                                var V = (this.m * lat + Math.sin(lat) - k) / (this.m + Math.cos(lat));
                                lat -= V;
                                if (Math.abs(V) < EPSLN) {
                                    break;
                                }
                            }
                        }
                        x = this.a * this.C_x * lon * (this.m + Math.cos(lat));
                        y = this.a * this.C_y * lat;
                    } else {
                        var s = Math.sin(lat);
                        var c = Math.cos(lat);
                        y = this.a * pj_mlfn(lat, s, c, this.en);
                        x = this.a * lon * c / Math.sqrt(1 - this.es * s * s);
                    }
                    p.x = x;
                    p.y = y;
                    return p;
                };
                exports.inverse = function (p) {
                    var lat, temp, lon, s;
                    p.x -= this.x0;
                    lon = p.x / this.a;
                    p.y -= this.y0;
                    lat = p.y / this.a;
                    if (this.sphere) {
                        lat /= this.C_y;
                        lon = lon / (this.C_x * (this.m + Math.cos(lat)));
                        if (this.m) {
                            lat = asinz((this.m * lat + Math.sin(lat)) / this.n);
                        } else if (this.n !== 1) {
                            lat = asinz(Math.sin(lat) / this.n);
                        }
                        lon = adjust_lon(lon + this.long0);
                        lat = adjust_lat(lat);
                    } else {
                        lat = pj_inv_mlfn(p.y / this.a, this.es, this.en);
                        s = Math.abs(lat);
                        if (s < HALF_PI) {
                            s = Math.sin(lat);
                            temp = this.long0 + p.x * Math.sqrt(1 - this.es * s * s) / (this.a * Math.cos(lat));    //temp = this.long0 + p.x / (this.a * Math.cos(lat));
                            //temp = this.long0 + p.x / (this.a * Math.cos(lat));
                            lon = adjust_lon(temp);
                        } else if (s - EPSLN < HALF_PI) {
                            lon = this.long0;
                        }
                    }
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Sinusoidal',
                    'sinu'
                ];
            },
            {
                '../common/adjust_lat': 4,
                '../common/adjust_lon': 5,
                '../common/asinz': 6,
                '../common/pj_enfn': 17,
                '../common/pj_inv_mlfn': 18,
                '../common/pj_mlfn': 19
            }
        ],
        58: [
            function (_dereq_, module, exports) {
                /*
                 references:
                 Formules et constantes pour le Calcul pour la
                 projection cylindrique conforme à axe oblique et pour la transformation entre
                 des systèmes de référence.
                 http://www.swisstopo.admin.ch/internet/swisstopo/fr/home/topics/survey/sys/refsys/switzerland.parsysrelated1.31216.downloadList.77004.DownloadFile.tmp/swissprojectionfr.pdf
                 */
                exports.init = function () {
                    var phy0 = this.lat0;
                    this.lambda0 = this.long0;
                    var sinPhy0 = Math.sin(phy0);
                    var semiMajorAxis = this.a;
                    var invF = this.rf;
                    var flattening = 1 / invF;
                    var e2 = 2 * flattening - Math.pow(flattening, 2);
                    var e = this.e = Math.sqrt(e2);
                    this.R = this.k0 * semiMajorAxis * Math.sqrt(1 - e2) / (1 - e2 * Math.pow(sinPhy0, 2));
                    this.alpha = Math.sqrt(1 + e2 / (1 - e2) * Math.pow(Math.cos(phy0), 4));
                    this.b0 = Math.asin(sinPhy0 / this.alpha);
                    var k1 = Math.log(Math.tan(Math.PI / 4 + this.b0 / 2));
                    var k2 = Math.log(Math.tan(Math.PI / 4 + phy0 / 2));
                    var k3 = Math.log((1 + e * sinPhy0) / (1 - e * sinPhy0));
                    this.K = k1 - this.alpha * k2 + this.alpha * e / 2 * k3;
                };
                exports.forward = function (p) {
                    var Sa1 = Math.log(Math.tan(Math.PI / 4 - p.y / 2));
                    var Sa2 = this.e / 2 * Math.log((1 + this.e * Math.sin(p.y)) / (1 - this.e * Math.sin(p.y)));
                    var S = -this.alpha * (Sa1 + Sa2) + this.K;    // spheric latitude
                    // spheric latitude
                    var b = 2 * (Math.atan(Math.exp(S)) - Math.PI / 4);    // spheric longitude
                    // spheric longitude
                    var I = this.alpha * (p.x - this.lambda0);    // psoeudo equatorial rotation
                    // psoeudo equatorial rotation
                    var rotI = Math.atan(Math.sin(I) / (Math.sin(this.b0) * Math.tan(b) + Math.cos(this.b0) * Math.cos(I)));
                    var rotB = Math.asin(Math.cos(this.b0) * Math.sin(b) - Math.sin(this.b0) * Math.cos(b) * Math.cos(I));
                    p.y = this.R / 2 * Math.log((1 + Math.sin(rotB)) / (1 - Math.sin(rotB))) + this.y0;
                    p.x = this.R * rotI + this.x0;
                    return p;
                };
                exports.inverse = function (p) {
                    var Y = p.x - this.x0;
                    var X = p.y - this.y0;
                    var rotI = Y / this.R;
                    var rotB = 2 * (Math.atan(Math.exp(X / this.R)) - Math.PI / 4);
                    var b = Math.asin(Math.cos(this.b0) * Math.sin(rotB) + Math.sin(this.b0) * Math.cos(rotB) * Math.cos(rotI));
                    var I = Math.atan(Math.sin(rotI) / (Math.cos(this.b0) * Math.cos(rotI) - Math.sin(this.b0) * Math.tan(rotB)));
                    var lambda = this.lambda0 + I / this.alpha;
                    var S = 0;
                    var phy = b;
                    var prevPhy = -1000;
                    var iteration = 0;
                    while (Math.abs(phy - prevPhy) > 1e-7) {
                        if (++iteration > 20) {
                            //...reportError("omercFwdInfinity");
                            return;
                        }    //S = Math.log(Math.tan(Math.PI / 4 + phy / 2));
                        //S = Math.log(Math.tan(Math.PI / 4 + phy / 2));
                        S = 1 / this.alpha * (Math.log(Math.tan(Math.PI / 4 + b / 2)) - this.K) + this.e * Math.log(Math.tan(Math.PI / 4 + Math.asin(this.e * Math.sin(phy)) / 2));
                        prevPhy = phy;
                        phy = 2 * Math.atan(Math.exp(S)) - Math.PI / 2;
                    }
                    p.x = lambda;
                    p.y = phy;
                    return p;
                };
                exports.names = ['somerc'];
            },
            {}
        ],
        59: [
            function (_dereq_, module, exports) {
                var HALF_PI = Math.PI / 2;
                var EPSLN = 1e-10;
                var sign = _dereq_('../common/sign');
                var msfnz = _dereq_('../common/msfnz');
                var tsfnz = _dereq_('../common/tsfnz');
                var phi2z = _dereq_('../common/phi2z');
                var adjust_lon = _dereq_('../common/adjust_lon');
                exports.ssfn_ = function (phit, sinphi, eccen) {
                    sinphi *= eccen;
                    return Math.tan(0.5 * (HALF_PI + phit)) * Math.pow((1 - sinphi) / (1 + sinphi), 0.5 * eccen);
                };
                exports.init = function () {
                    this.coslat0 = Math.cos(this.lat0);
                    this.sinlat0 = Math.sin(this.lat0);
                    if (this.sphere) {
                        if (this.k0 === 1 && !isNaN(this.lat_ts) && Math.abs(this.coslat0) <= EPSLN) {
                            this.k0 = 0.5 * (1 + sign(this.lat0) * Math.sin(this.lat_ts));
                        }
                    } else {
                        if (Math.abs(this.coslat0) <= EPSLN) {
                            if (this.lat0 > 0) {
                                //North pole
                                //trace('stere:north pole');
                                this.con = 1;
                            } else {
                                //South pole
                                //trace('stere:south pole');
                                this.con = -1;
                            }
                        }
                        this.cons = Math.sqrt(Math.pow(1 + this.e, 1 + this.e) * Math.pow(1 - this.e, 1 - this.e));
                        if (this.k0 === 1 && !isNaN(this.lat_ts) && Math.abs(this.coslat0) <= EPSLN) {
                            this.k0 = 0.5 * this.cons * msfnz(this.e, Math.sin(this.lat_ts), Math.cos(this.lat_ts)) / tsfnz(this.e, this.con * this.lat_ts, this.con * Math.sin(this.lat_ts));
                        }
                        this.ms1 = msfnz(this.e, this.sinlat0, this.coslat0);
                        this.X0 = 2 * Math.atan(this.ssfn_(this.lat0, this.sinlat0, this.e)) - HALF_PI;
                        this.cosX0 = Math.cos(this.X0);
                        this.sinX0 = Math.sin(this.X0);
                    }
                };    // Stereographic forward equations--mapping lat,long to x,y
                // Stereographic forward equations--mapping lat,long to x,y
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var sinlat = Math.sin(lat);
                    var coslat = Math.cos(lat);
                    var A, X, sinX, cosX, ts, rh;
                    var dlon = adjust_lon(lon - this.long0);
                    if (Math.abs(Math.abs(lon - this.long0) - Math.PI) <= EPSLN && Math.abs(lat + this.lat0) <= EPSLN) {
                        //case of the origine point
                        //trace('stere:this is the origin point');
                        p.x = NaN;
                        p.y = NaN;
                        return p;
                    }
                    if (this.sphere) {
                        //trace('stere:sphere case');
                        A = 2 * this.k0 / (1 + this.sinlat0 * sinlat + this.coslat0 * coslat * Math.cos(dlon));
                        p.x = this.a * A * coslat * Math.sin(dlon) + this.x0;
                        p.y = this.a * A * (this.coslat0 * sinlat - this.sinlat0 * coslat * Math.cos(dlon)) + this.y0;
                        return p;
                    } else {
                        X = 2 * Math.atan(this.ssfn_(lat, sinlat, this.e)) - HALF_PI;
                        cosX = Math.cos(X);
                        sinX = Math.sin(X);
                        if (Math.abs(this.coslat0) <= EPSLN) {
                            ts = tsfnz(this.e, lat * this.con, this.con * sinlat);
                            rh = 2 * this.a * this.k0 * ts / this.cons;
                            p.x = this.x0 + rh * Math.sin(lon - this.long0);
                            p.y = this.y0 - this.con * rh * Math.cos(lon - this.long0);    //trace(p.toString());
                            //trace(p.toString());
                            return p;
                        } else if (Math.abs(this.sinlat0) < EPSLN) {
                            //Eq
                            //trace('stere:equateur');
                            A = 2 * this.a * this.k0 / (1 + cosX * Math.cos(dlon));
                            p.y = A * sinX;
                        } else {
                            //other case
                            //trace('stere:normal case');
                            A = 2 * this.a * this.k0 * this.ms1 / (this.cosX0 * (1 + this.sinX0 * sinX + this.cosX0 * cosX * Math.cos(dlon)));
                            p.y = A * (this.cosX0 * sinX - this.sinX0 * cosX * Math.cos(dlon)) + this.y0;
                        }
                        p.x = A * cosX * Math.sin(dlon) + this.x0;
                    }    //trace(p.toString());
                    //trace(p.toString());
                    return p;
                };    //* Stereographic inverse equations--mapping x,y to lat/long
                //* Stereographic inverse equations--mapping x,y to lat/long
                exports.inverse = function (p) {
                    p.x -= this.x0;
                    p.y -= this.y0;
                    var lon, lat, ts, ce, Chi;
                    var rh = Math.sqrt(p.x * p.x + p.y * p.y);
                    if (this.sphere) {
                        var c = 2 * Math.atan(rh / (0.5 * this.a * this.k0));
                        lon = this.long0;
                        lat = this.lat0;
                        if (rh <= EPSLN) {
                            p.x = lon;
                            p.y = lat;
                            return p;
                        }
                        lat = Math.asin(Math.cos(c) * this.sinlat0 + p.y * Math.sin(c) * this.coslat0 / rh);
                        if (Math.abs(this.coslat0) < EPSLN) {
                            if (this.lat0 > 0) {
                                lon = adjust_lon(this.long0 + Math.atan2(p.x, -1 * p.y));
                            } else {
                                lon = adjust_lon(this.long0 + Math.atan2(p.x, p.y));
                            }
                        } else {
                            lon = adjust_lon(this.long0 + Math.atan2(p.x * Math.sin(c), rh * this.coslat0 * Math.cos(c) - p.y * this.sinlat0 * Math.sin(c)));
                        }
                        p.x = lon;
                        p.y = lat;
                        return p;
                    } else {
                        if (Math.abs(this.coslat0) <= EPSLN) {
                            if (rh <= EPSLN) {
                                lat = this.lat0;
                                lon = this.long0;
                                p.x = lon;
                                p.y = lat;    //trace(p.toString());
                                //trace(p.toString());
                                return p;
                            }
                            p.x *= this.con;
                            p.y *= this.con;
                            ts = rh * this.cons / (2 * this.a * this.k0);
                            lat = this.con * phi2z(this.e, ts);
                            lon = this.con * adjust_lon(this.con * this.long0 + Math.atan2(p.x, -1 * p.y));
                        } else {
                            ce = 2 * Math.atan(rh * this.cosX0 / (2 * this.a * this.k0 * this.ms1));
                            lon = this.long0;
                            if (rh <= EPSLN) {
                                Chi = this.X0;
                            } else {
                                Chi = Math.asin(Math.cos(ce) * this.sinX0 + p.y * Math.sin(ce) * this.cosX0 / rh);
                                lon = adjust_lon(this.long0 + Math.atan2(p.x * Math.sin(ce), rh * this.cosX0 * Math.cos(ce) - p.y * this.sinX0 * Math.sin(ce)));
                            }
                            lat = -1 * phi2z(this.e, Math.tan(0.5 * (HALF_PI + Chi)));
                        }
                    }
                    p.x = lon;
                    p.y = lat;    //trace(p.toString());
                    //trace(p.toString());
                    return p;
                };
                exports.names = ['stere'];
            },
            {
                '../common/adjust_lon': 5,
                '../common/msfnz': 15,
                '../common/phi2z': 16,
                '../common/sign': 21,
                '../common/tsfnz': 24
            }
        ],
        60: [
            function (_dereq_, module, exports) {
                var gauss = _dereq_('./gauss');
                var adjust_lon = _dereq_('../common/adjust_lon');
                exports.init = function () {
                    gauss.init.apply(this);
                    if (!this.rc) {
                        return;
                    }
                    this.sinc0 = Math.sin(this.phic0);
                    this.cosc0 = Math.cos(this.phic0);
                    this.R2 = 2 * this.rc;
                    if (!this.title) {
                        this.title = 'Oblique Stereographic Alternative';
                    }
                };
                exports.forward = function (p) {
                    var sinc, cosc, cosl, k;
                    p.x = adjust_lon(p.x - this.long0);
                    gauss.forward.apply(this, [p]);
                    sinc = Math.sin(p.y);
                    cosc = Math.cos(p.y);
                    cosl = Math.cos(p.x);
                    k = this.k0 * this.R2 / (1 + this.sinc0 * sinc + this.cosc0 * cosc * cosl);
                    p.x = k * cosc * Math.sin(p.x);
                    p.y = k * (this.cosc0 * sinc - this.sinc0 * cosc * cosl);
                    p.x = this.a * p.x + this.x0;
                    p.y = this.a * p.y + this.y0;
                    return p;
                };
                exports.inverse = function (p) {
                    var sinc, cosc, lon, lat, rho;
                    p.x = (p.x - this.x0) / this.a;
                    p.y = (p.y - this.y0) / this.a;
                    p.x /= this.k0;
                    p.y /= this.k0;
                    if (rho = Math.sqrt(p.x * p.x + p.y * p.y)) {
                        var c = 2 * Math.atan2(rho, this.R2);
                        sinc = Math.sin(c);
                        cosc = Math.cos(c);
                        lat = Math.asin(cosc * this.sinc0 + p.y * sinc * this.cosc0 / rho);
                        lon = Math.atan2(p.x * sinc, rho * this.cosc0 * cosc - p.y * this.sinc0 * sinc);
                    } else {
                        lat = this.phic0;
                        lon = 0;
                    }
                    p.x = lon;
                    p.y = lat;
                    gauss.inverse.apply(this, [p]);
                    p.x = adjust_lon(p.x + this.long0);
                    return p;
                };
                exports.names = [
                    'Stereographic_North_Pole',
                    'Oblique_Stereographic',
                    'Polar_Stereographic',
                    'sterea',
                    'Oblique Stereographic Alternative'
                ];
            },
            {
                '../common/adjust_lon': 5,
                './gauss': 45
            }
        ],
        61: [
            function (_dereq_, module, exports) {
                var e0fn = _dereq_('../common/e0fn');
                var e1fn = _dereq_('../common/e1fn');
                var e2fn = _dereq_('../common/e2fn');
                var e3fn = _dereq_('../common/e3fn');
                var mlfn = _dereq_('../common/mlfn');
                var adjust_lon = _dereq_('../common/adjust_lon');
                var HALF_PI = Math.PI / 2;
                var EPSLN = 1e-10;
                var sign = _dereq_('../common/sign');
                var asinz = _dereq_('../common/asinz');
                exports.init = function () {
                    this.e0 = e0fn(this.es);
                    this.e1 = e1fn(this.es);
                    this.e2 = e2fn(this.es);
                    this.e3 = e3fn(this.es);
                    this.ml0 = this.a * mlfn(this.e0, this.e1, this.e2, this.e3, this.lat0);
                };    /**
                 Transverse Mercator Forward  - long/lat to x/y
                 long/lat in radians
                 */
                /**
                 Transverse Mercator Forward  - long/lat to x/y
                 long/lat in radians
                 */
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;
                    var delta_lon = adjust_lon(lon - this.long0);
                    var con;
                    var x, y;
                    var sin_phi = Math.sin(lat);
                    var cos_phi = Math.cos(lat);
                    if (this.sphere) {
                        var b = cos_phi * Math.sin(delta_lon);
                        if (Math.abs(Math.abs(b) - 1) < 1e-10) {
                            return 93;
                        } else {
                            x = 0.5 * this.a * this.k0 * Math.log((1 + b) / (1 - b));
                            con = Math.acos(cos_phi * Math.cos(delta_lon) / Math.sqrt(1 - b * b));
                            if (lat < 0) {
                                con = -con;
                            }
                            y = this.a * this.k0 * (con - this.lat0);
                        }
                    } else {
                        var al = cos_phi * delta_lon;
                        var als = Math.pow(al, 2);
                        var c = this.ep2 * Math.pow(cos_phi, 2);
                        var tq = Math.tan(lat);
                        var t = Math.pow(tq, 2);
                        con = 1 - this.es * Math.pow(sin_phi, 2);
                        var n = this.a / Math.sqrt(con);
                        var ml = this.a * mlfn(this.e0, this.e1, this.e2, this.e3, lat);
                        x = this.k0 * n * al * (1 + als / 6 * (1 - t + c + als / 20 * (5 - 18 * t + Math.pow(t, 2) + 72 * c - 58 * this.ep2))) + this.x0;
                        y = this.k0 * (ml - this.ml0 + n * tq * (als * (0.5 + als / 24 * (5 - t + 9 * c + 4 * Math.pow(c, 2) + als / 30 * (61 - 58 * t + Math.pow(t, 2) + 600 * c - 330 * this.ep2))))) + this.y0;
                    }
                    p.x = x;
                    p.y = y;
                    return p;
                };    /**
                 Transverse Mercator Inverse  -  x/y to long/lat
                 */
                /**
                 Transverse Mercator Inverse  -  x/y to long/lat
                 */
                exports.inverse = function (p) {
                    var con, phi;
                    var delta_phi;
                    var i;
                    var max_iter = 6;
                    var lat, lon;
                    if (this.sphere) {
                        var f = Math.exp(p.x / (this.a * this.k0));
                        var g = 0.5 * (f - 1 / f);
                        var temp = this.lat0 + p.y / (this.a * this.k0);
                        var h = Math.cos(temp);
                        con = Math.sqrt((1 - h * h) / (1 + g * g));
                        lat = asinz(con);
                        if (temp < 0) {
                            lat = -lat;
                        }
                        if (g === 0 && h === 0) {
                            lon = this.long0;
                        } else {
                            lon = adjust_lon(Math.atan2(g, h) + this.long0);
                        }
                    } else {
                        // ellipsoidal form
                        var x = p.x - this.x0;
                        var y = p.y - this.y0;
                        con = (this.ml0 + y / this.k0) / this.a;
                        phi = con;
                        for (i = 0; true; i++) {
                            delta_phi = (con + this.e1 * Math.sin(2 * phi) - this.e2 * Math.sin(4 * phi) + this.e3 * Math.sin(6 * phi)) / this.e0 - phi;
                            phi += delta_phi;
                            if (Math.abs(delta_phi) <= EPSLN) {
                                break;
                            }
                            if (i >= max_iter) {
                                return 95;
                            }
                        }    // for()
                        // for()
                        if (Math.abs(phi) < HALF_PI) {
                            var sin_phi = Math.sin(phi);
                            var cos_phi = Math.cos(phi);
                            var tan_phi = Math.tan(phi);
                            var c = this.ep2 * Math.pow(cos_phi, 2);
                            var cs = Math.pow(c, 2);
                            var t = Math.pow(tan_phi, 2);
                            var ts = Math.pow(t, 2);
                            con = 1 - this.es * Math.pow(sin_phi, 2);
                            var n = this.a / Math.sqrt(con);
                            var r = n * (1 - this.es) / con;
                            var d = x / (n * this.k0);
                            var ds = Math.pow(d, 2);
                            lat = phi - n * tan_phi * ds / r * (0.5 - ds / 24 * (5 + 3 * t + 10 * c - 4 * cs - 9 * this.ep2 - ds / 30 * (61 + 90 * t + 298 * c + 45 * ts - 252 * this.ep2 - 3 * cs)));
                            lon = adjust_lon(this.long0 + d * (1 - ds / 6 * (1 + 2 * t + c - ds / 20 * (5 - 2 * c + 28 * t - 3 * cs + 8 * this.ep2 + 24 * ts))) / cos_phi);
                        } else {
                            lat = HALF_PI * sign(y);
                            lon = this.long0;
                        }
                    }
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Transverse_Mercator',
                    'Transverse Mercator',
                    'tmerc'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/asinz': 6,
                '../common/e0fn': 7,
                '../common/e1fn': 8,
                '../common/e2fn': 9,
                '../common/e3fn': 10,
                '../common/mlfn': 14,
                '../common/sign': 21
            }
        ],
        62: [
            function (_dereq_, module, exports) {
                var D2R = 0.017453292519943295;
                var tmerc = _dereq_('./tmerc');
                exports.dependsOn = 'tmerc';
                exports.init = function () {
                    if (!this.zone) {
                        return;
                    }
                    this.lat0 = 0;
                    this.long0 = (6 * Math.abs(this.zone) - 183) * D2R;
                    this.x0 = 500000;
                    this.y0 = this.utmSouth ? 10000000 : 0;
                    this.k0 = 0.9996;
                    tmerc.init.apply(this);
                    this.forward = tmerc.forward;
                    this.inverse = tmerc.inverse;
                };
                exports.names = [
                    'Universal Transverse Mercator System',
                    'utm'
                ];
            },
            { './tmerc': 61 }
        ],
        63: [
            function (_dereq_, module, exports) {
                var adjust_lon = _dereq_('../common/adjust_lon');
                var HALF_PI = Math.PI / 2;
                var EPSLN = 1e-10;
                var asinz = _dereq_('../common/asinz');    /* Initialize the Van Der Grinten projection
                 ----------------------------------------*/
                /* Initialize the Van Der Grinten projection
                 ----------------------------------------*/
                exports.init = function () {
                    //this.R = 6370997; //Radius of earth
                    this.R = this.a;
                };
                exports.forward = function (p) {
                    var lon = p.x;
                    var lat = p.y;    /* Forward equations
                     -----------------*/
                    /* Forward equations
                     -----------------*/
                    var dlon = adjust_lon(lon - this.long0);
                    var x, y;
                    if (Math.abs(lat) <= EPSLN) {
                        x = this.x0 + this.R * dlon;
                        y = this.y0;
                    }
                    var theta = asinz(2 * Math.abs(lat / Math.PI));
                    if (Math.abs(dlon) <= EPSLN || Math.abs(Math.abs(lat) - HALF_PI) <= EPSLN) {
                        x = this.x0;
                        if (lat >= 0) {
                            y = this.y0 + Math.PI * this.R * Math.tan(0.5 * theta);
                        } else {
                            y = this.y0 + Math.PI * this.R * -Math.tan(0.5 * theta);
                        }    //  return(OK);
                    }
                    var al = 0.5 * Math.abs(Math.PI / dlon - dlon / Math.PI);
                    var asq = al * al;
                    var sinth = Math.sin(theta);
                    var costh = Math.cos(theta);
                    var g = costh / (sinth + costh - 1);
                    var gsq = g * g;
                    var m = g * (2 / sinth - 1);
                    var msq = m * m;
                    var con = Math.PI * this.R * (al * (g - msq) + Math.sqrt(asq * (g - msq) * (g - msq) - (msq + asq) * (gsq - msq))) / (msq + asq);
                    if (dlon < 0) {
                        con = -con;
                    }
                    x = this.x0 + con;    //con = Math.abs(con / (Math.PI * this.R));
                    //con = Math.abs(con / (Math.PI * this.R));
                    var q = asq + g;
                    con = Math.PI * this.R * (m * q - al * Math.sqrt((msq + asq) * (asq + 1) - q * q)) / (msq + asq);
                    if (lat >= 0) {
                        //y = this.y0 + Math.PI * this.R * Math.sqrt(1 - con * con - 2 * al * con);
                        y = this.y0 + con;
                    } else {
                        //y = this.y0 - Math.PI * this.R * Math.sqrt(1 - con * con - 2 * al * con);
                        y = this.y0 - con;
                    }
                    p.x = x;
                    p.y = y;
                    return p;
                };    /* Van Der Grinten inverse equations--mapping x,y to lat/long
                 ---------------------------------------------------------*/
                /* Van Der Grinten inverse equations--mapping x,y to lat/long
                 ---------------------------------------------------------*/
                exports.inverse = function (p) {
                    var lon, lat;
                    var xx, yy, xys, c1, c2, c3;
                    var a1;
                    var m1;
                    var con;
                    var th1;
                    var d;    /* inverse equations
                     -----------------*/
                    /* inverse equations
                     -----------------*/
                    p.x -= this.x0;
                    p.y -= this.y0;
                    con = Math.PI * this.R;
                    xx = p.x / con;
                    yy = p.y / con;
                    xys = xx * xx + yy * yy;
                    c1 = -Math.abs(yy) * (1 + xys);
                    c2 = c1 - 2 * yy * yy + xx * xx;
                    c3 = -2 * c1 + 1 + 2 * yy * yy + xys * xys;
                    d = yy * yy / c3 + (2 * c2 * c2 * c2 / c3 / c3 / c3 - 9 * c1 * c2 / c3 / c3) / 27;
                    a1 = (c1 - c2 * c2 / 3 / c3) / c3;
                    m1 = 2 * Math.sqrt(-a1 / 3);
                    con = 3 * d / a1 / m1;
                    if (Math.abs(con) > 1) {
                        if (con >= 0) {
                            con = 1;
                        } else {
                            con = -1;
                        }
                    }
                    th1 = Math.acos(con) / 3;
                    if (p.y >= 0) {
                        lat = (-m1 * Math.cos(th1 + Math.PI / 3) - c2 / 3 / c3) * Math.PI;
                    } else {
                        lat = -(-m1 * Math.cos(th1 + Math.PI / 3) - c2 / 3 / c3) * Math.PI;
                    }
                    if (Math.abs(xx) < EPSLN) {
                        lon = this.long0;
                    } else {
                        lon = adjust_lon(this.long0 + Math.PI * (xys - 1 + Math.sqrt(1 + 2 * (xx * xx - yy * yy) + xys * xys)) / 2 / xx);
                    }
                    p.x = lon;
                    p.y = lat;
                    return p;
                };
                exports.names = [
                    'Van_der_Grinten_I',
                    'VanDerGrinten',
                    'vandg'
                ];
            },
            {
                '../common/adjust_lon': 5,
                '../common/asinz': 6
            }
        ],
        64: [
            function (_dereq_, module, exports) {
                var D2R = 0.017453292519943295;
                var R2D = 57.29577951308232;
                var PJD_3PARAM = 1;
                var PJD_7PARAM = 2;
                var datum_transform = _dereq_('./datum_transform');
                var adjust_axis = _dereq_('./adjust_axis');
                var proj = _dereq_('./Proj');
                var toPoint = _dereq_('./common/toPoint');
                module.exports = function transform(source, dest, point) {
                    var wgs84;
                    if (Array.isArray(point)) {
                        point = toPoint(point);
                    }
                    function checkNotWGS(source, dest) {
                        return (source.datum.datum_type === PJD_3PARAM || source.datum.datum_type === PJD_7PARAM) && dest.datumCode !== 'WGS84';
                    }    // Workaround for datum shifts towgs84, if either source or destination projection is not wgs84
                    // Workaround for datum shifts towgs84, if either source or destination projection is not wgs84
                    if (source.datum && dest.datum && (checkNotWGS(source, dest) || checkNotWGS(dest, source))) {
                        wgs84 = new proj('WGS84');
                        transform(source, wgs84, point);
                        source = wgs84;
                    }    // DGR, 2010/11/12
                    // DGR, 2010/11/12
                    if (source.axis !== 'enu') {
                        adjust_axis(source, false, point);
                    }    // Transform source points to long/lat, if they aren't already.
                    // Transform source points to long/lat, if they aren't already.
                    if (source.projName === 'longlat') {
                        point.x *= D2R;    // convert degrees to radians
                        // convert degrees to radians
                        point.y *= D2R;
                    } else {
                        if (source.to_meter) {
                            point.x *= source.to_meter;
                            point.y *= source.to_meter;
                        }
                        source.inverse(point);    // Convert Cartesian to longlat
                    }    // Adjust for the prime meridian if necessary
                    // Adjust for the prime meridian if necessary
                    if (source.from_greenwich) {
                        point.x += source.from_greenwich;
                    }    // Convert datums if needed, and if possible.
                    // Convert datums if needed, and if possible.
                    point = datum_transform(source.datum, dest.datum, point);    // Adjust for the prime meridian if necessary
                    // Adjust for the prime meridian if necessary
                    if (dest.from_greenwich) {
                        point.x -= dest.from_greenwich;
                    }
                    if (dest.projName === 'longlat') {
                        // convert radians to decimal degrees
                        point.x *= R2D;
                        point.y *= R2D;
                    } else {
                        // else project
                        dest.forward(point);
                        if (dest.to_meter) {
                            point.x /= dest.to_meter;
                            point.y /= dest.to_meter;
                        }
                    }    // DGR, 2010/11/12
                    // DGR, 2010/11/12
                    if (dest.axis !== 'enu') {
                        adjust_axis(dest, true, point);
                    }
                    return point;
                };
            },
            {
                './Proj': 2,
                './adjust_axis': 3,
                './common/toPoint': 23,
                './datum_transform': 30
            }
        ],
        65: [
            function (_dereq_, module, exports) {
                var D2R = 0.017453292519943295;
                var extend = _dereq_('./extend');
                function mapit(obj, key, v) {
                    obj[key] = v.map(function (aa) {
                        var o = {};
                        sExpr(aa, o);
                        return o;
                    }).reduce(function (a, b) {
                        return extend(a, b);
                    }, {});
                }
                function sExpr(v, obj) {
                    var key;
                    if (!Array.isArray(v)) {
                        obj[v] = true;
                        return;
                    } else {
                        key = v.shift();
                        if (key === 'PARAMETER') {
                            key = v.shift();
                        }
                        if (v.length === 1) {
                            if (Array.isArray(v[0])) {
                                obj[key] = {};
                                sExpr(v[0], obj[key]);
                            } else {
                                obj[key] = v[0];
                            }
                        } else if (!v.length) {
                            obj[key] = true;
                        } else if (key === 'TOWGS84') {
                            obj[key] = v;
                        } else {
                            obj[key] = {};
                            if ([
                                'UNIT',
                                'PRIMEM',
                                'VERT_DATUM'
                            ].indexOf(key) > -1) {
                                obj[key] = {
                                    name: v[0].toLowerCase(),
                                    convert: v[1]
                                };
                                if (v.length === 3) {
                                    obj[key].auth = v[2];
                                }
                            } else if (key === 'SPHEROID') {
                                obj[key] = {
                                    name: v[0],
                                    a: v[1],
                                    rf: v[2]
                                };
                                if (v.length === 4) {
                                    obj[key].auth = v[3];
                                }
                            } else if ([
                                'GEOGCS',
                                'GEOCCS',
                                'DATUM',
                                'VERT_CS',
                                'COMPD_CS',
                                'LOCAL_CS',
                                'FITTED_CS',
                                'LOCAL_DATUM'
                            ].indexOf(key) > -1) {
                                v[0] = [
                                    'name',
                                    v[0]
                                ];
                                mapit(obj, key, v);
                            } else if (v.every(function (aa) {
                                return Array.isArray(aa);
                            })) {
                                mapit(obj, key, v);
                            } else {
                                sExpr(v, obj[key]);
                            }
                        }
                    }
                }
                function rename(obj, params) {
                    var outName = params[0];
                    var inName = params[1];
                    if (!(outName in obj) && inName in obj) {
                        obj[outName] = obj[inName];
                        if (params.length === 3) {
                            obj[outName] = params[2](obj[outName]);
                        }
                    }
                }
                function d2r(input) {
                    return input * D2R;
                }
                function cleanWKT(wkt) {
                    if (wkt.type === 'GEOGCS') {
                        wkt.projName = 'longlat';
                    } else if (wkt.type === 'LOCAL_CS') {
                        wkt.projName = 'identity';
                        wkt.local = true;
                    } else {
                        if (typeof wkt.PROJECTION === 'object') {
                            wkt.projName = Object.keys(wkt.PROJECTION)[0];
                        } else {
                            wkt.projName = wkt.PROJECTION;
                        }
                    }
                    if (wkt.UNIT) {
                        wkt.units = wkt.UNIT.name.toLowerCase();
                        if (wkt.units === 'metre') {
                            wkt.units = 'meter';
                        }
                        if (wkt.UNIT.convert) {
                            wkt.to_meter = parseFloat(wkt.UNIT.convert, 10);
                        }
                    }
                    if (wkt.GEOGCS) {
                        //if(wkt.GEOGCS.PRIMEM&&wkt.GEOGCS.PRIMEM.convert){
                        //  wkt.from_greenwich=wkt.GEOGCS.PRIMEM.convert*D2R;
                        //}
                        if (wkt.GEOGCS.DATUM) {
                            wkt.datumCode = wkt.GEOGCS.DATUM.name.toLowerCase();
                        } else {
                            wkt.datumCode = wkt.GEOGCS.name.toLowerCase();
                        }
                        if (wkt.datumCode.slice(0, 2) === 'd_') {
                            wkt.datumCode = wkt.datumCode.slice(2);
                        }
                        if (wkt.datumCode === 'new_zealand_geodetic_datum_1949' || wkt.datumCode === 'new_zealand_1949') {
                            wkt.datumCode = 'nzgd49';
                        }
                        if (wkt.datumCode === 'wgs_1984') {
                            if (wkt.PROJECTION === 'Mercator_Auxiliary_Sphere') {
                                wkt.sphere = true;
                            }
                            wkt.datumCode = 'wgs84';
                        }
                        if (wkt.datumCode.slice(-6) === '_ferro') {
                            wkt.datumCode = wkt.datumCode.slice(0, -6);
                        }
                        if (wkt.datumCode.slice(-8) === '_jakarta') {
                            wkt.datumCode = wkt.datumCode.slice(0, -8);
                        }
                        if (~wkt.datumCode.indexOf('belge')) {
                            wkt.datumCode = 'rnb72';
                        }
                        if (wkt.GEOGCS.DATUM && wkt.GEOGCS.DATUM.SPHEROID) {
                            wkt.ellps = wkt.GEOGCS.DATUM.SPHEROID.name.replace('_19', '').replace(/[Cc]larke\_18/, 'clrk');
                            if (wkt.ellps.toLowerCase().slice(0, 13) === 'international') {
                                wkt.ellps = 'intl';
                            }
                            wkt.a = wkt.GEOGCS.DATUM.SPHEROID.a;
                            wkt.rf = parseFloat(wkt.GEOGCS.DATUM.SPHEROID.rf, 10);
                        }
                        if (~wkt.datumCode.indexOf('osgb_1936')) {
                            wkt.datumCode = 'osgb36';
                        }
                    }
                    if (wkt.b && !isFinite(wkt.b)) {
                        wkt.b = wkt.a;
                    }
                    function toMeter(input) {
                        var ratio = wkt.to_meter || 1;
                        return parseFloat(input, 10) * ratio;
                    }
                    var renamer = function (a) {
                        return rename(wkt, a);
                    };
                    var list = [
                        [
                            'standard_parallel_1',
                            'Standard_Parallel_1'
                        ],
                        [
                            'standard_parallel_2',
                            'Standard_Parallel_2'
                        ],
                        [
                            'false_easting',
                            'False_Easting'
                        ],
                        [
                            'false_northing',
                            'False_Northing'
                        ],
                        [
                            'central_meridian',
                            'Central_Meridian'
                        ],
                        [
                            'latitude_of_origin',
                            'Latitude_Of_Origin'
                        ],
                        [
                            'scale_factor',
                            'Scale_Factor'
                        ],
                        [
                            'k0',
                            'scale_factor'
                        ],
                        [
                            'latitude_of_center',
                            'Latitude_of_center'
                        ],
                        [
                            'lat0',
                            'latitude_of_center',
                            d2r
                        ],
                        [
                            'longitude_of_center',
                            'Longitude_Of_Center'
                        ],
                        [
                            'longc',
                            'longitude_of_center',
                            d2r
                        ],
                        [
                            'x0',
                            'false_easting',
                            toMeter
                        ],
                        [
                            'y0',
                            'false_northing',
                            toMeter
                        ],
                        [
                            'long0',
                            'central_meridian',
                            d2r
                        ],
                        [
                            'lat0',
                            'latitude_of_origin',
                            d2r
                        ],
                        [
                            'lat0',
                            'standard_parallel_1',
                            d2r
                        ],
                        [
                            'lat1',
                            'standard_parallel_1',
                            d2r
                        ],
                        [
                            'lat2',
                            'standard_parallel_2',
                            d2r
                        ],
                        [
                            'alpha',
                            'azimuth',
                            d2r
                        ],
                        [
                            'srsCode',
                            'name'
                        ]
                    ];
                    list.forEach(renamer);
                    if (!wkt.long0 && wkt.longc && (wkt.PROJECTION === 'Albers_Conic_Equal_Area' || wkt.PROJECTION === 'Lambert_Azimuthal_Equal_Area')) {
                        wkt.long0 = wkt.longc;
                    }
                }
                module.exports = function (wkt, self) {
                    var lisp = JSON.parse((',' + wkt).replace(/\s*\,\s*([A-Z_0-9]+?)(\[)/g, ',["$1",').slice(1).replace(/\s*\,\s*([A-Z_0-9]+?)\]/g, ',"$1"]'));
                    var type = lisp.shift();
                    var name = lisp.shift();
                    lisp.unshift([
                        'name',
                        name
                    ]);
                    lisp.unshift([
                        'type',
                        type
                    ]);
                    lisp.unshift('output');
                    var obj = {};
                    sExpr(lisp, obj);
                    cleanWKT(obj.output);
                    return extend(self, obj.output);
                };
            },
            { './extend': 33 }
        ],
        66: [
            function (_dereq_, module, exports) {
                /**
                 * UTM zones are grouped, and assigned to one of a group of 6
                 * sets.
                 *
                 * {int} @private
                 */
                var NUM_100K_SETS = 6;    /**
                 * The column letters (for easting) of the lower left value, per
                 * set.
                 *
                 * {string} @private
                 */
                /**
                 * The column letters (for easting) of the lower left value, per
                 * set.
                 *
                 * {string} @private
                 */
                var SET_ORIGIN_COLUMN_LETTERS = 'AJSAJS';    /**
                 * The row letters (for northing) of the lower left value, per
                 * set.
                 *
                 * {string} @private
                 */
                /**
                 * The row letters (for northing) of the lower left value, per
                 * set.
                 *
                 * {string} @private
                 */
                var SET_ORIGIN_ROW_LETTERS = 'AFAFAF';
                var A = 65;    // A
                // A
                var I = 73;    // I
                // I
                var O = 79;    // O
                // O
                var V = 86;    // V
                // V
                var Z = 90;    // Z
                /**
                 * Conversion of lat/lon to MGRS.
                 *
                 * @param {object} ll Object literal with lat and lon properties on a
                 *     WGS84 ellipsoid.
                 * @param {int} accuracy Accuracy in digits (5 for 1 m, 4 for 10 m, 3 for
                 *      100 m, 4 for 1000 m or 5 for 10000 m). Optional, default is 5.
                 * @return {string} the MGRS string for the given location and accuracy.
                 */
                // Z
                /**
                 * Conversion of lat/lon to MGRS.
                 *
                 * @param {object} ll Object literal with lat and lon properties on a
                 *     WGS84 ellipsoid.
                 * @param {int} accuracy Accuracy in digits (5 for 1 m, 4 for 10 m, 3 for
                 *      100 m, 4 for 1000 m or 5 for 10000 m). Optional, default is 5.
                 * @return {string} the MGRS string for the given location and accuracy.
                 */
                exports.forward = function (ll, accuracy) {
                    accuracy = accuracy || 5;    // default accuracy 1m
                    // default accuracy 1m
                    return encode(LLtoUTM({
                        lat: ll[1],
                        lon: ll[0]
                    }), accuracy);
                };    /**
                 * Conversion of MGRS to lat/lon.
                 *
                 * @param {string} mgrs MGRS string.
                 * @return {array} An array with left (longitude), bottom (latitude), right
                 *     (longitude) and top (latitude) values in WGS84, representing the
                 *     bounding box for the provided MGRS reference.
                 */
                /**
                 * Conversion of MGRS to lat/lon.
                 *
                 * @param {string} mgrs MGRS string.
                 * @return {array} An array with left (longitude), bottom (latitude), right
                 *     (longitude) and top (latitude) values in WGS84, representing the
                 *     bounding box for the provided MGRS reference.
                 */
                exports.inverse = function (mgrs) {
                    var bbox = UTMtoLL(decode(mgrs.toUpperCase()));
                    return [
                        bbox.left,
                        bbox.bottom,
                        bbox.right,
                        bbox.top
                    ];
                };
                exports.toPoint = function (mgrsStr) {
                    var llbbox = exports.inverse(mgrsStr);
                    return [
                        (llbbox[2] + llbbox[0]) / 2,
                        (llbbox[3] + llbbox[1]) / 2
                    ];
                };    /**
                 * Conversion from degrees to radians.
                 *
                 * @private
                 * @param {number} deg the angle in degrees.
                 * @return {number} the angle in radians.
                 */
                /**
                 * Conversion from degrees to radians.
                 *
                 * @private
                 * @param {number} deg the angle in degrees.
                 * @return {number} the angle in radians.
                 */
                function degToRad(deg) {
                    return deg * (Math.PI / 180);
                }    /**
                 * Conversion from radians to degrees.
                 *
                 * @private
                 * @param {number} rad the angle in radians.
                 * @return {number} the angle in degrees.
                 */
                /**
                 * Conversion from radians to degrees.
                 *
                 * @private
                 * @param {number} rad the angle in radians.
                 * @return {number} the angle in degrees.
                 */
                function radToDeg(rad) {
                    return 180 * (rad / Math.PI);
                }    /**
                 * Converts a set of Longitude and Latitude co-ordinates to UTM
                 * using the WGS84 ellipsoid.
                 *
                 * @private
                 * @param {object} ll Object literal with lat and lon properties
                 *     representing the WGS84 coordinate to be converted.
                 * @return {object} Object literal containing the UTM value with easting,
                 *     northing, zoneNumber and zoneLetter properties, and an optional
                 *     accuracy property in digits. Returns null if the conversion failed.
                 */
                /**
                 * Converts a set of Longitude and Latitude co-ordinates to UTM
                 * using the WGS84 ellipsoid.
                 *
                 * @private
                 * @param {object} ll Object literal with lat and lon properties
                 *     representing the WGS84 coordinate to be converted.
                 * @return {object} Object literal containing the UTM value with easting,
                 *     northing, zoneNumber and zoneLetter properties, and an optional
                 *     accuracy property in digits. Returns null if the conversion failed.
                 */
                function LLtoUTM(ll) {
                    var Lat = ll.lat;
                    var Long = ll.lon;
                    var a = 6378137;    //ellip.radius;
                    //ellip.radius;
                    var eccSquared = 0.00669438;    //ellip.eccsq;
                    //ellip.eccsq;
                    var k0 = 0.9996;
                    var LongOrigin;
                    var eccPrimeSquared;
                    var N, T, C, A, M;
                    var LatRad = degToRad(Lat);
                    var LongRad = degToRad(Long);
                    var LongOriginRad;
                    var ZoneNumber;    // (int)
                    // (int)
                    ZoneNumber = Math.floor((Long + 180) / 6) + 1;    //Make sure the longitude 180.00 is in Zone 60
                    //Make sure the longitude 180.00 is in Zone 60
                    if (Long === 180) {
                        ZoneNumber = 60;
                    }    // Special zone for Norway
                    // Special zone for Norway
                    if (Lat >= 56 && Lat < 64 && Long >= 3 && Long < 12) {
                        ZoneNumber = 32;
                    }    // Special zones for Svalbard
                    // Special zones for Svalbard
                    if (Lat >= 72 && Lat < 84) {
                        if (Long >= 0 && Long < 9) {
                            ZoneNumber = 31;
                        } else if (Long >= 9 && Long < 21) {
                            ZoneNumber = 33;
                        } else if (Long >= 21 && Long < 33) {
                            ZoneNumber = 35;
                        } else if (Long >= 33 && Long < 42) {
                            ZoneNumber = 37;
                        }
                    }
                    LongOrigin = (ZoneNumber - 1) * 6 - 180 + 3;    //+3 puts origin
                    // in middle of
                    // zone
                    //+3 puts origin
                    // in middle of
                    // zone
                    LongOriginRad = degToRad(LongOrigin);
                    eccPrimeSquared = eccSquared / (1 - eccSquared);
                    N = a / Math.sqrt(1 - eccSquared * Math.sin(LatRad) * Math.sin(LatRad));
                    T = Math.tan(LatRad) * Math.tan(LatRad);
                    C = eccPrimeSquared * Math.cos(LatRad) * Math.cos(LatRad);
                    A = Math.cos(LatRad) * (LongRad - LongOriginRad);
                    M = a * ((1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5 * eccSquared * eccSquared * eccSquared / 256) * LatRad - (3 * eccSquared / 8 + 3 * eccSquared * eccSquared / 32 + 45 * eccSquared * eccSquared * eccSquared / 1024) * Math.sin(2 * LatRad) + (15 * eccSquared * eccSquared / 256 + 45 * eccSquared * eccSquared * eccSquared / 1024) * Math.sin(4 * LatRad) - 35 * eccSquared * eccSquared * eccSquared / 3072 * Math.sin(6 * LatRad));
                    var UTMEasting = k0 * N * (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72 * C - 58 * eccPrimeSquared) * A * A * A * A * A / 120) + 500000;
                    var UTMNorthing = k0 * (M + N * Math.tan(LatRad) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61 - 58 * T + T * T + 600 * C - 330 * eccPrimeSquared) * A * A * A * A * A * A / 720));
                    if (Lat < 0) {
                        UTMNorthing += 10000000;    //10000000 meter offset for
                        // southern hemisphere
                    }
                    return {
                        northing: Math.round(UTMNorthing),
                        easting: Math.round(UTMEasting),
                        zoneNumber: ZoneNumber,
                        zoneLetter: getLetterDesignator(Lat)
                    };
                }    /**
                 * Converts UTM coords to lat/long, using the WGS84 ellipsoid. This is a convenience
                 * class where the Zone can be specified as a single string eg."60N" which
                 * is then broken down into the ZoneNumber and ZoneLetter.
                 *
                 * @private
                 * @param {object} utm An object literal with northing, easting, zoneNumber
                 *     and zoneLetter properties. If an optional accuracy property is
                 *     provided (in meters), a bounding box will be returned instead of
                 *     latitude and longitude.
                 * @return {object} An object literal containing either lat and lon values
                 *     (if no accuracy was provided), or top, right, bottom and left values
                 *     for the bounding box calculated according to the provided accuracy.
                 *     Returns null if the conversion failed.
                 */
                /**
                 * Converts UTM coords to lat/long, using the WGS84 ellipsoid. This is a convenience
                 * class where the Zone can be specified as a single string eg."60N" which
                 * is then broken down into the ZoneNumber and ZoneLetter.
                 *
                 * @private
                 * @param {object} utm An object literal with northing, easting, zoneNumber
                 *     and zoneLetter properties. If an optional accuracy property is
                 *     provided (in meters), a bounding box will be returned instead of
                 *     latitude and longitude.
                 * @return {object} An object literal containing either lat and lon values
                 *     (if no accuracy was provided), or top, right, bottom and left values
                 *     for the bounding box calculated according to the provided accuracy.
                 *     Returns null if the conversion failed.
                 */
                function UTMtoLL(utm) {
                    var UTMNorthing = utm.northing;
                    var UTMEasting = utm.easting;
                    var zoneLetter = utm.zoneLetter;
                    var zoneNumber = utm.zoneNumber;    // check the ZoneNummber is valid
                    // check the ZoneNummber is valid
                    if (zoneNumber < 0 || zoneNumber > 60) {
                        return null;
                    }
                    var k0 = 0.9996;
                    var a = 6378137;    //ellip.radius;
                    //ellip.radius;
                    var eccSquared = 0.00669438;    //ellip.eccsq;
                    //ellip.eccsq;
                    var eccPrimeSquared;
                    var e1 = (1 - Math.sqrt(1 - eccSquared)) / (1 + Math.sqrt(1 - eccSquared));
                    var N1, T1, C1, R1, D, M;
                    var LongOrigin;
                    var mu, phi1Rad;    // remove 500,000 meter offset for longitude
                    // remove 500,000 meter offset for longitude
                    var x = UTMEasting - 500000;
                    var y = UTMNorthing;    // We must know somehow if we are in the Northern or Southern
                    // hemisphere, this is the only time we use the letter So even
                    // if the Zone letter isn't exactly correct it should indicate
                    // the hemisphere correctly
                    // We must know somehow if we are in the Northern or Southern
                    // hemisphere, this is the only time we use the letter So even
                    // if the Zone letter isn't exactly correct it should indicate
                    // the hemisphere correctly
                    if (zoneLetter < 'N') {
                        y -= 10000000;    // remove 10,000,000 meter offset used
                        // for southern hemisphere
                    }    // There are 60 zones with zone 1 being at West -180 to -174
                    // There are 60 zones with zone 1 being at West -180 to -174
                    LongOrigin = (zoneNumber - 1) * 6 - 180 + 3;    // +3 puts origin
                    // in middle of
                    // zone
                    // +3 puts origin
                    // in middle of
                    // zone
                    eccPrimeSquared = eccSquared / (1 - eccSquared);
                    M = y / k0;
                    mu = M / (a * (1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5 * eccSquared * eccSquared * eccSquared / 256));
                    phi1Rad = mu + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * mu) + (21 * e1 * e1 / 16 - 55 * e1 * e1 * e1 * e1 / 32) * Math.sin(4 * mu) + 151 * e1 * e1 * e1 / 96 * Math.sin(6 * mu);    // double phi1 = ProjMath.radToDeg(phi1Rad);
                    // double phi1 = ProjMath.radToDeg(phi1Rad);
                    N1 = a / Math.sqrt(1 - eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad));
                    T1 = Math.tan(phi1Rad) * Math.tan(phi1Rad);
                    C1 = eccPrimeSquared * Math.cos(phi1Rad) * Math.cos(phi1Rad);
                    R1 = a * (1 - eccSquared) / Math.pow(1 - eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad), 1.5);
                    D = x / (N1 * k0);
                    var lat = phi1Rad - N1 * Math.tan(phi1Rad) / R1 * (D * D / 2 - (5 + 3 * T1 + 10 * C1 - 4 * C1 * C1 - 9 * eccPrimeSquared) * D * D * D * D / 24 + (61 + 90 * T1 + 298 * C1 + 45 * T1 * T1 - 252 * eccPrimeSquared - 3 * C1 * C1) * D * D * D * D * D * D / 720);
                    lat = radToDeg(lat);
                    var lon = (D - (1 + 2 * T1 + C1) * D * D * D / 6 + (5 - 2 * C1 + 28 * T1 - 3 * C1 * C1 + 8 * eccPrimeSquared + 24 * T1 * T1) * D * D * D * D * D / 120) / Math.cos(phi1Rad);
                    lon = LongOrigin + radToDeg(lon);
                    var result;
                    if (utm.accuracy) {
                        var topRight = UTMtoLL({
                            northing: utm.northing + utm.accuracy,
                            easting: utm.easting + utm.accuracy,
                            zoneLetter: utm.zoneLetter,
                            zoneNumber: utm.zoneNumber
                        });
                        result = {
                            top: topRight.lat,
                            right: topRight.lon,
                            bottom: lat,
                            left: lon
                        };
                    } else {
                        result = {
                            lat: lat,
                            lon: lon
                        };
                    }
                    return result;
                }    /**
                 * Calculates the MGRS letter designator for the given latitude.
                 *
                 * @private
                 * @param {number} lat The latitude in WGS84 to get the letter designator
                 *     for.
                 * @return {char} The letter designator.
                 */
                /**
                 * Calculates the MGRS letter designator for the given latitude.
                 *
                 * @private
                 * @param {number} lat The latitude in WGS84 to get the letter designator
                 *     for.
                 * @return {char} The letter designator.
                 */
                function getLetterDesignator(lat) {
                    //This is here as an error flag to show that the Latitude is
                    //outside MGRS limits
                    var LetterDesignator = 'Z';
                    if (84 >= lat && lat >= 72) {
                        LetterDesignator = 'X';
                    } else if (72 > lat && lat >= 64) {
                        LetterDesignator = 'W';
                    } else if (64 > lat && lat >= 56) {
                        LetterDesignator = 'V';
                    } else if (56 > lat && lat >= 48) {
                        LetterDesignator = 'U';
                    } else if (48 > lat && lat >= 40) {
                        LetterDesignator = 'T';
                    } else if (40 > lat && lat >= 32) {
                        LetterDesignator = 'S';
                    } else if (32 > lat && lat >= 24) {
                        LetterDesignator = 'R';
                    } else if (24 > lat && lat >= 16) {
                        LetterDesignator = 'Q';
                    } else if (16 > lat && lat >= 8) {
                        LetterDesignator = 'P';
                    } else if (8 > lat && lat >= 0) {
                        LetterDesignator = 'N';
                    } else if (0 > lat && lat >= -8) {
                        LetterDesignator = 'M';
                    } else if (-8 > lat && lat >= -16) {
                        LetterDesignator = 'L';
                    } else if (-16 > lat && lat >= -24) {
                        LetterDesignator = 'K';
                    } else if (-24 > lat && lat >= -32) {
                        LetterDesignator = 'J';
                    } else if (-32 > lat && lat >= -40) {
                        LetterDesignator = 'H';
                    } else if (-40 > lat && lat >= -48) {
                        LetterDesignator = 'G';
                    } else if (-48 > lat && lat >= -56) {
                        LetterDesignator = 'F';
                    } else if (-56 > lat && lat >= -64) {
                        LetterDesignator = 'E';
                    } else if (-64 > lat && lat >= -72) {
                        LetterDesignator = 'D';
                    } else if (-72 > lat && lat >= -80) {
                        LetterDesignator = 'C';
                    }
                    return LetterDesignator;
                }    /**
                 * Encodes a UTM location as MGRS string.
                 *
                 * @private
                 * @param {object} utm An object literal with easting, northing,
                 *     zoneLetter, zoneNumber
                 * @param {number} accuracy Accuracy in digits (1-5).
                 * @return {string} MGRS string for the given UTM location.
                 */
                /**
                 * Encodes a UTM location as MGRS string.
                 *
                 * @private
                 * @param {object} utm An object literal with easting, northing,
                 *     zoneLetter, zoneNumber
                 * @param {number} accuracy Accuracy in digits (1-5).
                 * @return {string} MGRS string for the given UTM location.
                 */
                function encode(utm, accuracy) {
                    var seasting = '' + utm.easting, snorthing = '' + utm.northing;
                    return utm.zoneNumber + utm.zoneLetter + get100kID(utm.easting, utm.northing, utm.zoneNumber) + seasting.substr(seasting.length - 5, accuracy) + snorthing.substr(snorthing.length - 5, accuracy);
                }    /**
                 * Get the two letter 100k designator for a given UTM easting,
                 * northing and zone number value.
                 *
                 * @private
                 * @param {number} easting
                 * @param {number} northing
                 * @param {number} zoneNumber
                 * @return the two letter 100k designator for the given UTM location.
                 */
                /**
                 * Get the two letter 100k designator for a given UTM easting,
                 * northing and zone number value.
                 *
                 * @private
                 * @param {number} easting
                 * @param {number} northing
                 * @param {number} zoneNumber
                 * @return the two letter 100k designator for the given UTM location.
                 */
                function get100kID(easting, northing, zoneNumber) {
                    var setParm = get100kSetForZone(zoneNumber);
                    var setColumn = Math.floor(easting / 100000);
                    var setRow = Math.floor(northing / 100000) % 20;
                    return getLetter100kID(setColumn, setRow, setParm);
                }    /**
                 * Given a UTM zone number, figure out the MGRS 100K set it is in.
                 *
                 * @private
                 * @param {number} i An UTM zone number.
                 * @return {number} the 100k set the UTM zone is in.
                 */
                /**
                 * Given a UTM zone number, figure out the MGRS 100K set it is in.
                 *
                 * @private
                 * @param {number} i An UTM zone number.
                 * @return {number} the 100k set the UTM zone is in.
                 */
                function get100kSetForZone(i) {
                    var setParm = i % NUM_100K_SETS;
                    if (setParm === 0) {
                        setParm = NUM_100K_SETS;
                    }
                    return setParm;
                }    /**
                 * Get the two-letter MGRS 100k designator given information
                 * translated from the UTM northing, easting and zone number.
                 *
                 * @private
                 * @param {number} column the column index as it relates to the MGRS
                 *        100k set spreadsheet, created from the UTM easting.
                 *        Values are 1-8.
                 * @param {number} row the row index as it relates to the MGRS 100k set
                 *        spreadsheet, created from the UTM northing value. Values
                 *        are from 0-19.
                 * @param {number} parm the set block, as it relates to the MGRS 100k set
                 *        spreadsheet, created from the UTM zone. Values are from
                 *        1-60.
                 * @return two letter MGRS 100k code.
                 */
                /**
                 * Get the two-letter MGRS 100k designator given information
                 * translated from the UTM northing, easting and zone number.
                 *
                 * @private
                 * @param {number} column the column index as it relates to the MGRS
                 *        100k set spreadsheet, created from the UTM easting.
                 *        Values are 1-8.
                 * @param {number} row the row index as it relates to the MGRS 100k set
                 *        spreadsheet, created from the UTM northing value. Values
                 *        are from 0-19.
                 * @param {number} parm the set block, as it relates to the MGRS 100k set
                 *        spreadsheet, created from the UTM zone. Values are from
                 *        1-60.
                 * @return two letter MGRS 100k code.
                 */
                function getLetter100kID(column, row, parm) {
                    // colOrigin and rowOrigin are the letters at the origin of the set
                    var index = parm - 1;
                    var colOrigin = SET_ORIGIN_COLUMN_LETTERS.charCodeAt(index);
                    var rowOrigin = SET_ORIGIN_ROW_LETTERS.charCodeAt(index);    // colInt and rowInt are the letters to build to return
                    // colInt and rowInt are the letters to build to return
                    var colInt = colOrigin + column - 1;
                    var rowInt = rowOrigin + row;
                    var rollover = false;
                    if (colInt > Z) {
                        colInt = colInt - Z + A - 1;
                        rollover = true;
                    }
                    if (colInt === I || colOrigin < I && colInt > I || (colInt > I || colOrigin < I) && rollover) {
                        colInt++;
                    }
                    if (colInt === O || colOrigin < O && colInt > O || (colInt > O || colOrigin < O) && rollover) {
                        colInt++;
                        if (colInt === I) {
                            colInt++;
                        }
                    }
                    if (colInt > Z) {
                        colInt = colInt - Z + A - 1;
                    }
                    if (rowInt > V) {
                        rowInt = rowInt - V + A - 1;
                        rollover = true;
                    } else {
                        rollover = false;
                    }
                    if (rowInt === I || rowOrigin < I && rowInt > I || (rowInt > I || rowOrigin < I) && rollover) {
                        rowInt++;
                    }
                    if (rowInt === O || rowOrigin < O && rowInt > O || (rowInt > O || rowOrigin < O) && rollover) {
                        rowInt++;
                        if (rowInt === I) {
                            rowInt++;
                        }
                    }
                    if (rowInt > V) {
                        rowInt = rowInt - V + A - 1;
                    }
                    var twoLetter = String.fromCharCode(colInt) + String.fromCharCode(rowInt);
                    return twoLetter;
                }    /**
                 * Decode the UTM parameters from a MGRS string.
                 *
                 * @private
                 * @param {string} mgrsString an UPPERCASE coordinate string is expected.
                 * @return {object} An object literal with easting, northing, zoneLetter,
                 *     zoneNumber and accuracy (in meters) properties.
                 */
                /**
                 * Decode the UTM parameters from a MGRS string.
                 *
                 * @private
                 * @param {string} mgrsString an UPPERCASE coordinate string is expected.
                 * @return {object} An object literal with easting, northing, zoneLetter,
                 *     zoneNumber and accuracy (in meters) properties.
                 */
                function decode(mgrsString) {
                    if (mgrsString && mgrsString.length === 0) {
                        throw 'MGRSPoint coverting from nothing';
                    }
                    var length = mgrsString.length;
                    var hunK = null;
                    var sb = '';
                    var testChar;
                    var i = 0;    // get Zone number
                    // get Zone number
                    while (!/[A-Z]/.test(testChar = mgrsString.charAt(i))) {
                        if (i >= 2) {
                            throw 'MGRSPoint bad conversion from: ' + mgrsString;
                        }
                        sb += testChar;
                        i++;
                    }
                    var zoneNumber = parseInt(sb, 10);
                    if (i === 0 || i + 3 > length) {
                        // A good MGRS string has to be 4-5 digits long,
                        // ##AAA/#AAA at least.
                        throw 'MGRSPoint bad conversion from: ' + mgrsString;
                    }
                    var zoneLetter = mgrsString.charAt(i++);    // Should we check the zone letter here? Why not.
                    // Should we check the zone letter here? Why not.
                    if (zoneLetter <= 'A' || zoneLetter === 'B' || zoneLetter === 'Y' || zoneLetter >= 'Z' || zoneLetter === 'I' || zoneLetter === 'O') {
                        throw 'MGRSPoint zone letter ' + zoneLetter + ' not handled: ' + mgrsString;
                    }
                    hunK = mgrsString.substring(i, i += 2);
                    var set = get100kSetForZone(zoneNumber);
                    var east100k = getEastingFromChar(hunK.charAt(0), set);
                    var north100k = getNorthingFromChar(hunK.charAt(1), set);    // We have a bug where the northing may be 2000000 too low.
                    // How
                    // do we know when to roll over?
                    // We have a bug where the northing may be 2000000 too low.
                    // How
                    // do we know when to roll over?
                    while (north100k < getMinNorthing(zoneLetter)) {
                        north100k += 2000000;
                    }    // calculate the char index for easting/northing separator
                    // calculate the char index for easting/northing separator
                    var remainder = length - i;
                    if (remainder % 2 !== 0) {
                        throw 'MGRSPoint has to have an even number \nof digits after the zone letter and two 100km letters - front \nhalf for easting meters, second half for \nnorthing meters' + mgrsString;
                    }
                    var sep = remainder / 2;
                    var sepEasting = 0;
                    var sepNorthing = 0;
                    var accuracyBonus, sepEastingString, sepNorthingString, easting, northing;
                    if (sep > 0) {
                        accuracyBonus = 100000 / Math.pow(10, sep);
                        sepEastingString = mgrsString.substring(i, i + sep);
                        sepEasting = parseFloat(sepEastingString) * accuracyBonus;
                        sepNorthingString = mgrsString.substring(i + sep);
                        sepNorthing = parseFloat(sepNorthingString) * accuracyBonus;
                    }
                    easting = sepEasting + east100k;
                    northing = sepNorthing + north100k;
                    return {
                        easting: easting,
                        northing: northing,
                        zoneLetter: zoneLetter,
                        zoneNumber: zoneNumber,
                        accuracy: accuracyBonus
                    };
                }    /**
                 * Given the first letter from a two-letter MGRS 100k zone, and given the
                 * MGRS table set for the zone number, figure out the easting value that
                 * should be added to the other, secondary easting value.
                 *
                 * @private
                 * @param {char} e The first letter from a two-letter MGRS 100´k zone.
                 * @param {number} set The MGRS table set for the zone number.
                 * @return {number} The easting value for the given letter and set.
                 */
                /**
                 * Given the first letter from a two-letter MGRS 100k zone, and given the
                 * MGRS table set for the zone number, figure out the easting value that
                 * should be added to the other, secondary easting value.
                 *
                 * @private
                 * @param {char} e The first letter from a two-letter MGRS 100´k zone.
                 * @param {number} set The MGRS table set for the zone number.
                 * @return {number} The easting value for the given letter and set.
                 */
                function getEastingFromChar(e, set) {
                    // colOrigin is the letter at the origin of the set for the
                    // column
                    var curCol = SET_ORIGIN_COLUMN_LETTERS.charCodeAt(set - 1);
                    var eastingValue = 100000;
                    var rewindMarker = false;
                    while (curCol !== e.charCodeAt(0)) {
                        curCol++;
                        if (curCol === I) {
                            curCol++;
                        }
                        if (curCol === O) {
                            curCol++;
                        }
                        if (curCol > Z) {
                            if (rewindMarker) {
                                throw 'Bad character: ' + e;
                            }
                            curCol = A;
                            rewindMarker = true;
                        }
                        eastingValue += 100000;
                    }
                    return eastingValue;
                }    /**
                 * Given the second letter from a two-letter MGRS 100k zone, and given the
                 * MGRS table set for the zone number, figure out the northing value that
                 * should be added to the other, secondary northing value. You have to
                 * remember that Northings are determined from the equator, and the vertical
                 * cycle of letters mean a 2000000 additional northing meters. This happens
                 * approx. every 18 degrees of latitude. This method does *NOT* count any
                 * additional northings. You have to figure out how many 2000000 meters need
                 * to be added for the zone letter of the MGRS coordinate.
                 *
                 * @private
                 * @param {char} n Second letter of the MGRS 100k zone
                 * @param {number} set The MGRS table set number, which is dependent on the
                 *     UTM zone number.
                 * @return {number} The northing value for the given letter and set.
                 */
                /**
                 * Given the second letter from a two-letter MGRS 100k zone, and given the
                 * MGRS table set for the zone number, figure out the northing value that
                 * should be added to the other, secondary northing value. You have to
                 * remember that Northings are determined from the equator, and the vertical
                 * cycle of letters mean a 2000000 additional northing meters. This happens
                 * approx. every 18 degrees of latitude. This method does *NOT* count any
                 * additional northings. You have to figure out how many 2000000 meters need
                 * to be added for the zone letter of the MGRS coordinate.
                 *
                 * @private
                 * @param {char} n Second letter of the MGRS 100k zone
                 * @param {number} set The MGRS table set number, which is dependent on the
                 *     UTM zone number.
                 * @return {number} The northing value for the given letter and set.
                 */
                function getNorthingFromChar(n, set) {
                    if (n > 'V') {
                        throw 'MGRSPoint given invalid Northing ' + n;
                    }    // rowOrigin is the letter at the origin of the set for the
                    // column
                    // rowOrigin is the letter at the origin of the set for the
                    // column
                    var curRow = SET_ORIGIN_ROW_LETTERS.charCodeAt(set - 1);
                    var northingValue = 0;
                    var rewindMarker = false;
                    while (curRow !== n.charCodeAt(0)) {
                        curRow++;
                        if (curRow === I) {
                            curRow++;
                        }
                        if (curRow === O) {
                            curRow++;
                        }    // fixing a bug making whole application hang in this loop
                        // when 'n' is a wrong character
                        // fixing a bug making whole application hang in this loop
                        // when 'n' is a wrong character
                        if (curRow > V) {
                            if (rewindMarker) {
                                // making sure that this loop ends
                                throw 'Bad character: ' + n;
                            }
                            curRow = A;
                            rewindMarker = true;
                        }
                        northingValue += 100000;
                    }
                    return northingValue;
                }    /**
                 * The function getMinNorthing returns the minimum northing value of a MGRS
                 * zone.
                 *
                 * Ported from Geotrans' c Lattitude_Band_Value structure table.
                 *
                 * @private
                 * @param {char} zoneLetter The MGRS zone to get the min northing for.
                 * @return {number}
                 */
                /**
                 * The function getMinNorthing returns the minimum northing value of a MGRS
                 * zone.
                 *
                 * Ported from Geotrans' c Lattitude_Band_Value structure table.
                 *
                 * @private
                 * @param {char} zoneLetter The MGRS zone to get the min northing for.
                 * @return {number}
                 */
                function getMinNorthing(zoneLetter) {
                    var northing;
                    switch (zoneLetter) {
                    case 'C':
                        northing = 1100000;
                        break;
                    case 'D':
                        northing = 2000000;
                        break;
                    case 'E':
                        northing = 2800000;
                        break;
                    case 'F':
                        northing = 3700000;
                        break;
                    case 'G':
                        northing = 4600000;
                        break;
                    case 'H':
                        northing = 5500000;
                        break;
                    case 'J':
                        northing = 6400000;
                        break;
                    case 'K':
                        northing = 7300000;
                        break;
                    case 'L':
                        northing = 8200000;
                        break;
                    case 'M':
                        northing = 9100000;
                        break;
                    case 'N':
                        northing = 0;
                        break;
                    case 'P':
                        northing = 800000;
                        break;
                    case 'Q':
                        northing = 1700000;
                        break;
                    case 'R':
                        northing = 2600000;
                        break;
                    case 'S':
                        northing = 3500000;
                        break;
                    case 'T':
                        northing = 4400000;
                        break;
                    case 'U':
                        northing = 5300000;
                        break;
                    case 'V':
                        northing = 6200000;
                        break;
                    case 'W':
                        northing = 7000000;
                        break;
                    case 'X':
                        northing = 7900000;
                        break;
                    default:
                        northing = -1;
                    }
                    if (northing >= 0) {
                        return northing;
                    } else {
                        throw 'Invalid zone letter: ' + zoneLetter;
                    }
                }
            },
            {}
        ],
        67: [
            function (_dereq_, module, exports) {
                module.exports = {
                    'name': 'proj4',
                    'version': '2.2.2-alpha',
                    'description': 'Proj4js is a JavaScript library to transform point coordinates from one coordinate system to another, including datum transformations.',
                    'main': 'lib/index.js',
                    'directories': {
                        'test': 'test',
                        'doc': 'docs'
                    },
                    'scripts': { 'test': './node_modules/istanbul/lib/cli.js test ./node_modules/mocha/bin/_mocha test/test.js' },
                    'repository': {
                        'type': 'git',
                        'url': 'git://github.com/proj4js/proj4js.git'
                    },
                    'author': '',
                    'license': 'MIT',
                    'jam': {
                        'main': 'dist/proj4.js',
                        'include': [
                            'dist/proj4.js',
                            'README.md',
                            'AUTHORS',
                            'LICENSE.md'
                        ]
                    },
                    'devDependencies': {
                        'grunt-cli': '~0.1.13',
                        'grunt': '~0.4.2',
                        'grunt-contrib-connect': '~0.6.0',
                        'grunt-contrib-jshint': '~0.8.0',
                        'chai': '~1.8.1',
                        'mocha': '~1.17.1',
                        'grunt-mocha-phantomjs': '~0.4.0',
                        'browserify': '~3.24.5',
                        'grunt-browserify': '~1.3.0',
                        'grunt-contrib-uglify': '~0.3.2',
                        'curl': 'git://github.com/cujojs/curl.git',
                        'istanbul': '~0.2.4',
                        'tin': '~0.4.0'
                    },
                    'dependencies': { 'mgrs': '0.0.0' }
                };
            },
            {}
        ],
        './includedProjections': [
            function (_dereq_, module, exports) {
                module.exports = _dereq_('Jf/0WF');
            },
            {}
        ],
        'Jf/0WF': [
            function (_dereq_, module, exports) {
                var projs = [
                    _dereq_('./lib/projections/tmerc'),
                    _dereq_('./lib/projections/utm'),
                    _dereq_('./lib/projections/sterea'),
                    _dereq_('./lib/projections/stere'),
                    _dereq_('./lib/projections/somerc'),
                    _dereq_('./lib/projections/omerc'),
                    _dereq_('./lib/projections/lcc'),
                    _dereq_('./lib/projections/krovak'),
                    _dereq_('./lib/projections/cass'),
                    _dereq_('./lib/projections/laea'),
                    _dereq_('./lib/projections/aea'),
                    _dereq_('./lib/projections/gnom'),
                    _dereq_('./lib/projections/cea'),
                    _dereq_('./lib/projections/eqc'),
                    _dereq_('./lib/projections/poly'),
                    _dereq_('./lib/projections/nzmg'),
                    _dereq_('./lib/projections/mill'),
                    _dereq_('./lib/projections/sinu'),
                    _dereq_('./lib/projections/moll'),
                    _dereq_('./lib/projections/eqdc'),
                    _dereq_('./lib/projections/vandg'),
                    _dereq_('./lib/projections/aeqd')
                ];
                module.exports = function (proj4) {
                    projs.forEach(function (proj) {
                        proj4.Proj.projections.add(proj);
                    });
                };
            },
            {
                './lib/projections/aea': 39,
                './lib/projections/aeqd': 40,
                './lib/projections/cass': 41,
                './lib/projections/cea': 42,
                './lib/projections/eqc': 43,
                './lib/projections/eqdc': 44,
                './lib/projections/gnom': 46,
                './lib/projections/krovak': 47,
                './lib/projections/laea': 48,
                './lib/projections/lcc': 49,
                './lib/projections/mill': 52,
                './lib/projections/moll': 53,
                './lib/projections/nzmg': 54,
                './lib/projections/omerc': 55,
                './lib/projections/poly': 56,
                './lib/projections/sinu': 57,
                './lib/projections/somerc': 58,
                './lib/projections/stere': 59,
                './lib/projections/sterea': 60,
                './lib/projections/tmerc': 61,
                './lib/projections/utm': 62,
                './lib/projections/vandg': 63
            }
        ]
    }, {}, [35])(35);
});    /*global T, window, proj4*/
/*global T, window, proj4*/
(function (exports) {
    var Projection = function (proj) {
        this.proj = new proj4.Proj(proj);
    };
    Projection.prototype = {
        project: function (latlng) {
            var projectedPoint = proj4(this.proj).forward([
                latlng.lng,
                latlng.lat
            ]);
            return new T.Point(projectedPoint[0], projectedPoint[1]);
        },
        unproject: function (point) {
            var unprojectedPoint = proj4(this.proj).inverse([
                point.x,
                point.y
            ]);
            return new T.LatLng(unprojectedPoint[1], unprojectedPoint[0]);
        }
    };    // Export
    // Export
    exports.Projection = Projection;
}(T));    /*global T, window */
/*global T, window */
(function (exports) {
    var SphericalMercator = function () {
        this.MAX_LATITUDE = 85.0511287798 * 200;
    };
    SphericalMercator.prototype = {
        project: function (latlng) {
            var d = Math.PI / 180, max = this.MAX_LATITUDE, lat = Math.max(Math.min(max, latlng.lat), -max), x = latlng.lng * d, y = lat * d;
            y = Math.log(Math.tan(Math.PI / 4 + y / 2));
            return new T.Point(x, y);
        },
        unproject: function (point) {
            var d = 180 / Math.PI, lng = point.x * d, lat = (2 * Math.atan(Math.exp(point.y)) - Math.PI / 2) * d;
            return new T.LatLng(lat, lng);
        }
    };    // Export
    // Export
    exports.SphericalMercator = SphericalMercator;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50, bitwise: true  */
/*global T, document, window, proj4 */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50, bitwise: true  */
/*global T, document, window, proj4 */
(function (exports) {
    /**
         * Crs class is used to define the coordinate system of the map
         *
         * @summary Crs's constructor
         * @constructor true
         *
         * @param {Object} [options](#options) The options of the instance
         *
         * @option {String} code CRS code (default: `'EPSG:3857'`)
         * @option {Transform} transformation CRS transformation (default: `new T.Transform(1, 0, -1, 0)`)
         * @option {Array} resolutions CRS resolutions
         * @option {Number} maxResolution CRS max resolution
         * @option {String} proj CRS projection text. If `proj` is not defined, `code` value will be used to create the projection instance
         */
    var Crs = function (options) {
        var i;
        this.code = options.code || 'EPSG:3857';
        this.transformation = options.transform || new T.Transform(1, 0, -1, 0);
        this.resolutions = options.resolutions;
        this.maxResolution = options.maxResolution;
        this.projText = options.proj;
        this.projection = new T.Projection(options.proj || options.code);
        this.maxProjected = this.projection.project(new T.LatLng(0, 179.9999));    // Generate resolutions
        // Generate resolutions
        if (!this.resolutions) {
            this.resolutions = [];
            if (!this.maxResolution) {
                this.maxResolution = 2 * Math.PI * this.projection.proj.a / 256;
            }
            for (i = 0; i < 19; i++) {
                this.resolutions.push(this.maxResolution / (1 << i));
            }
        }    // Compute scales
        // Compute scales
        this.scales = [];
        for (i = 0; i < this.resolutions.length; i++) {
            this.scales.push(1 / this.resolutions[i]);
        }
    };
    Crs.prototype = {
        /**
             * Projects coordinates using the CRS projection
             * @param {LatLng} latLng The coordinates that will be projected
             * @return {Point} The projected point
             */
        project: function (latlng) {
            var point, MAX, factor, latlngClone;    // Hack to ignore coordinate wrapping for Maporama tiles
            // Hack to ignore coordinate wrapping for Maporama tiles
            if (this.code === 'EPSG:3857') {
                MAX = 20037508.342789233;
                latlngClone = latlng.clone();
                this.transformation = new T.Transform(1, MAX, -1, MAX);
                factor = this.prepareLatLng(latlngClone);
                point = this.projection.project(latlngClone);
                point.x += this.maxProjected.x * factor;
            } else {
                point = this.projection.project(latlng);
            }
            return point;
        },
        /**
             * Reverses the projection of a point to its original coordinates
             * @param {Point} point The point that will be unprojected
             * @return {LatLng} The original coordinates
             */
        unproject: function (point) {
            var latlng, factor, MAX, pointClone;    // Hack to ingore coordinate wrapping for Maporama tiles
            // Hack to ingore coordinate wrapping for Maporama tiles
            if (this.code === 'EPSG:3857') {
                MAX = 20037508.342789233;
                pointClone = point.clone();
                this.transformation = new T.Transform(1, MAX, -1, MAX);
                factor = this.preparePoint(pointClone);
                latlng = this.projection.unproject(pointClone);
                latlng.lng += 180 * factor;
            } else {
                latlng = this.projection.unproject(point);
            }
            return latlng;
        },
        /**
             * Projects the coordinates using the CRS projection and applies transformation (see [transform()](#transform)) using the zoom level
             * @param {LatLng} latLng The coordinates that will be projected
             * @param {Number} zoom The zoom level for the transformation
             * @return {Point} The projected point
             */
        latLngToPoint: function (latlng, zoom) {
            var projectedPoint = this.project(latlng), point = this.transform(projectedPoint, zoom);
            return point;
        },
        /**
             * Reverses the projection of a point to its original coordinates and unstranforms it (see [untransform()](#untransform)) using the zoom level
             * @param {Point} point The point that will be unprojected
             * @param {Number} zoom The zoom level for the transformation
             * @return {LatLng} The original coordinates
             */
        pointToLatLng: function (point, zoom) {
            var untransformedPoint = this.untransform(point, zoom), latlng = this.unproject(untransformedPoint);
            return latlng;
        },
        /**
             * Transforms the projected point to pixels
             * @param {Point} point The projected point
             * @param {Number} zoom The zoom level that will be scaled (see [scale()](#scale)) for the transformation
             * @return {Point} The transformed point
             */
        transform: function (point, zoom) {
            var scale = this.scale(zoom), transformedPoint = this.transformation.transform(point, scale);
            return transformedPoint;
        },
        // Pixels to projected units
        /**
             * Pixels are transformed back to projected point
             * @param {Point} point The pixel point that will be transformed back
             * @param {Number} zoom The zoom level that will be scaled  (see [scale()](#scale)) for the transformation
             * @return {Point} The untransformed point
             */
        untransform: function (point, zoom) {
            var scale = this.scale(zoom), untransformedPoint = this.transformation.untransform(point, scale);
            return untransformedPoint;
        },
        /**
             * Returns the associated scale for the given zoom level from the list of scales (calculated as `1 / resolution`)
             * @param {Number} zoom The zoom level
             * @return {Number} The associated scale value
             */
        scale: function (zoom) {
            return this.scales[zoom];
        },
        // projected/px
        /**
             * Returns the associated resolution for the given zoom level from the list of resolutions
             * @param {Number} zoom The zoom level
             * @return {Number} The associated resolution
             */
        resolution: function (zoom) {
            return this.resolutions[zoom];
        },
        //Used for horizontal continuity of the PseudoMercator map
        preparePoint: function (point) {
            if (!point.x || point.x === 0) {
                return 0;
            }
            var max = this.maxProjected;
            var sgn = Math.abs(point.x) / point.x;
            var factor = Math.floor(Math.abs(point.x / max.x));
            if (factor > 0) {
                point.x = point.x % max.x;
            }
            return factor * sgn;
        },
        //Used for horizontal continuity of the PseudoMercator map
        prepareLatLng: function (latlng) {
            if (!latlng.lng || latlng.lng === 0) {
                return 0;
            }
            var max = 180;
            var sgn = Math.abs(latlng.lng) / latlng.lng;
            var factor = Math.floor(Math.abs(latlng.lng / max));
            if (factor > 0) {
                latlng.lng = latlng.lng % max;
            }
            return factor * sgn;
        }
    };    // Exports
    // Exports
    exports.Crs = Crs;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
(function (exports) {
    /**
         * DragHandler class is responsible for enabling dragging actions on the map
         *
         * @summary DragHandler's constructor
         * @constructor true
         */
    var DragHandler = function (options) {
    };
    DragHandler.prototype = {
        /**
             * Enables the drag handler for the given map instance
             * @param {Map} map The map for which the handler will be enabled
             */
        init: function (map) {
            this.map = map;
            this.drag = new T.DragUtil(map.$container.layers, map.$container.layers);
            this.drag.events.on('drag-start', this.onDragStart, this);
            this.drag.events.on('drag', this.onDrag, this);
            this.drag.events.on('drag-end', this.onDragEnd, this);
            this.drag.enable();
        },
        onDragStart: function (evt) {
            this.map.events.fire('move-start');
        },
        onDrag: function (evt) {
            this.map.events.fire('move', { offset: evt.offset });
        },
        onDragEnd: function (evt) {
            if (this.drag.moved) {
                this.map.events.fire('move-end');
            }
        },
        /**
             * Disables the handler
             */
        disable: function () {
            this.drag.disable();
        },
        /**
             * Enables the handler
             */
        enable: function () {
            this.drag.enable();
        },
        remove: function () {
            this.drag.disable();
            this.drag.events.detach('drag-start', this.onDragStart);
            this.drag.events.detach('drag', this.onDrag);
            this.drag.events.detach('drag-end', this.onDragEnd);
            this.map = null;
            this.drag = null;
        }
    };    // Exports
    // Exports
    exports.DragHandler = DragHandler;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window */
(function (exports) {
    /**
         * MarkerDragHandler class is responsible for enabling dragging actions for the markers on the map
         *
         * @summary MarkerDragHandler's constructor
         * @constructor true
         */
    var MarkerDragHandler = function (options) {
    };
    MarkerDragHandler.prototype = {
        /**
             * Enables the drag handler for the given marker instance
             * @param {HtmlMarker|ImageMarker} marker The marker for which the handler will be enabled
             */
        init: function (marker) {
            this.marker = marker;
            this.drag = new T.DragUtil(marker.$container, marker.$container);
            this.drag.events.on('before-drag', this.onBeforeStart, this);
            this.drag.events.on('drag-start', this.onDragStart, this);
            this.drag.events.on('drag', this.onDrag, this);
            this.drag.events.on('drag-end', this.onDragEnd, this);
            this.drag.enable();    //this.marker.$container.style.cursor = 'move';
        },
        onBeforeStart: function (evt) {
            if (this.marker.options.popOnClick) {
                var p = T.DomUtil.position(this.marker.$container);
                p.y -= 2;
                T.DomUtil.position(this.marker.$container, p);
            }
        },
        onDragStart: function (evt) {
            if (this.marker.layer) {
                this.marker.layer.events.fire('marker-drag-start', this.marker);
            }
        },
        onDrag: function (evt) {
            if (this.marker.layer) {
                this.marker.layer.events.fire('marker-drag', this.marker);
            }
        },
        onDragEnd: function (evt) {
            if (this.marker.options.popOnClick) {
                var p = T.DomUtil.position(this.marker.$container);
                p.y += 2;
                T.DomUtil.position(this.marker.$container, p);
            }
            if (this.marker.options.draggableUsingOffset) {
                this.marker.resetOffset();
            } else {
                this.marker.resetFromPx();
            }
            if (this.marker.layer) {
                this.marker.layer.events.fire('marker-drag-end', this.marker);
            }
        },
        remove: function () {
            this.drag.disable();
            this.drag.events.detach('before-drag', this.onBeforeStart);
            this.drag.events.detach('drag-start', this.onDragStart);
            this.drag.events.detach('drag', this.onDrag);
            this.drag.events.detach('drag-end', this.onDragEnd);
            this.marker = null;
            this.drag = null;
        }
    };    // Exports
    // Exports
    exports.MarkerDragHandler = MarkerDragHandler;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window, clearTimeout, setTimeout */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, window, clearTimeout, setTimeout */
(function (exports) {
    /**
         * MouseWheelHandler class is responsible for enabling the use of the mouse wheel to zoom the map
         *
         * @summary MouseWheelHandler's constructor
         * @constructor true
         */
    var MouseWheelHandler = function (options) {
    };
    MouseWheelHandler.prototype = {
        /**
             * Enables the mouse wheel handler for the given map instance
             * @param {Map} map The map instance for which the handler will be enabled
             */
        init: function (map) {
            this.map = map;
            this.delta = 0;
            T.DomEventUtil.on(this.map.$container, 'mousewheel', this.onMouseWheel, this);
        },
        onMouseWheel: function (evt) {
            var util = T.DomEventUtil, delta = util.getWheelDelta(evt), mousePosition = this.map.computeMousePosition(evt), centerInPixels = this.calculateCenterInPixels(mousePosition), center = this.map.unproject(centerInPixels), zoom, self;
            this.delta += delta;
            util.stop(evt);
            clearTimeout(this.tm);
            self = this;
            this.tm = setTimeout(function () {
                zoom = self.map.getZoom() + Math.floor(self.delta);
                self.map.zoomAround(center, zoom, true);
                self.delta = 0;
            }, 30);
        },
        zoom: function (evt) {
        },
        calculateCenterInPixels: function (mousePosition) {
            var pos = mousePosition.substract(this.map.getLayersPos()).add(this.map.getTopLeftInPixels());
            return pos;
        },
        remove: function () {
            T.DomEventUtil.off(this.map.$container, 'mousewheel', this.onMouseWheel);
        }
    };    // Exports
    // Exports
    exports.MouseWheelHandler = MouseWheelHandler;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window,T,document, setTimeout,clearTimeout */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window,T,document, setTimeout,clearTimeout */
(function (exports) {
    /**
         * The map object is responsible for rendering a map in a container with a set of options.
         *
         * @constructor true
         * @summary The map's constructor
         *
         * @option {Number} zoom The initial zoom (default: `5`)
         * @option {Number} minZoom The minimum zoom level (default: `0`)
         * @option {Number} maxZoom The maximum zoom level (default: `18`)
         * @option {LatLng} center The initial map location coordinates (default: `new T.LatLng(46.0, 2.0)`)
         * @option {CRS} crs The CRS option for the map (default: `new T.Crs({code: "EPSG:3857"})`)
         * @option {LatLngBounds} maxExtent The bounds that will constrain the map movement
         * @option {Boolean} repeatX If `true`, will repeat the tiles when exceeding the world's bounds horizontally (default: `true`)
         * @option {Boolean} urlLocation If `true`, will append the center of the map (latitude, longitude) and the map zoom in the URL hash (default: `true`)
         * @option {Boolean} noHistory If `true`, will not insert URL hash changes in history when `urlLocation` option is active (default: `true`)
         * @option {Boolean} geolocate If `true`, will automatically geolocate the user by using the browser's built-in geolocation system (default: `false`)
         * @option {Object} controls The controls to enable on the map. Available options are:
         *
         * - `attribution`, if this option has a property `addToMap: true` (ex.: `attribution: {addToMap: true}`), {AttributionControl} will be added to the map
         * - `scale`, if this option has a property `addToMap: true` (ex.: `scale: {addToMap: true}`), {ScaleControl} will be added to the map
         *
         * @event view-reset The map was moved at a specified location and zoom level
         * @event move-start The map will be moved by panning
         * @event move The map is being moved by panning
         * @event move-end Panning of the map has stopped
         * @event layer-add A new layer was added to the map
         * @event layer-remove A layer was removed from the map
         * @event layer-moved A layer's position (z-index) on the map was changed
         * @event zoom-interval-change The available maximum and minimum zoom levels were changed
         *
         * @param {HTMLDiv} container The html div where the map will be rendered
         * @param {Object} [options](#options) The options to set for the map's initial rendering
         * @example examples/docs/map/map 100% 500
         */
    var Map = function (container, options) {
        if (!container) {
            throw new Error('Map container not found.');
        }
        this.$layersArray = [];
        this.options = T.Util.defaults(options, this.options);
        this.zoom = this.options.zoom;
        this.center = this.options.center;
        this.events = new T.EventDispatcher();
        this.lastTransform = { matrix: new T.Matrix2D() };
        this.initContainer(container);
        if (!this.options.skipDefaultHandlers) {
            this.initHandlers();    // Add default handlers
        }
        this.initControls();    // Add default controls
        // Add default controls
        this.initPosition();    // Initialize map with correct position
        // Initialize map with correct position
        this.resetView(this.center, this.zoom);
    };
    Map.prototype = {
        options: {
            zoom: 5,
            minZoom: 0,
            maxZoom: 18,
            maxExtent: null,
            center: new T.LatLng(46, 2),
            crs: new T.Crs({ code: 'EPSG:3857' }),
            repeatX: true,
            repeatY: false,
            // obsolete,
            urlLocation: true,
            noHistory: true,
            geolocate: false,
            controls: { attribution: { prefix: '<a target=\'_blank\' rel=\'noopener noreferrer\' href=\'http://www.tibco.com/assets/blt59b361e6644c8d3a/tibco-end-user-license-agreement_tcm8-9378.pdf\'>\xA9TIBCO GeoAnalytics</a>' } }
        },
        initContainer: function (container) {
            this.$container = container;
            this.$container.map = this;
            this.$container.style.overflow = 'hidden';
            this.$container.style.position = 'relative';
            T.DomUtil.addClass(this.$container, 'tibco-map');
            var layers = this.$container.layers = T.DomUtil.create('div', 'layers-container', this.$container, 'layers-container');
            var controls = this.$container.controls = T.DomUtil.create('div', 'controls-container', this.$container);
            layers.style.position = 'absolute';    //T.DomUtil.size(layers, this.getSize());
            //T.DomUtil.size(layers, this.getSize());
            T.DomUtil.position(layers, new T.Point(0, 0));
            T.DomEventUtil.on(window, 'resize', this.reset, this);    //TODO for zoom test;
            //TODO for zoom test;
            this.$layers = layers;
        },
        initHandlers: function () {
            this.dragHandler = new T.DragHandler();
            this.mouseWheelHandler = new T.MouseWheelHandler();
            this.addHandler(this.dragHandler);
            this.addHandler(this.mouseWheelHandler);
        },
        initControls: function () {
            if (!(this.options.controls.attribution && this.options.controls.attribution.addToMap === false)) {
                this.addControl(new T.AttributionControl(this.options.controls.attribution));
            }
            if (!(this.options.controls.scale && this.options.controls.scale.addToMap === false)) {
                this.addControl(new T.ScaleControl(this.options.controls.scale));
            }
        },
        /*
             * Initializes position options.
             * First checks if position options (center & zoom) are specified in the URL.
             * If they are, use them, if not, get them from options if specified.
             */
        initPosition: function () {
            if (this.options.geolocate && T.Util.browserSupportsGeolocation()) {
                this.setPositionFromGeolocation();
            } else {
                this.setPositionFromUrl();
            }
        },
        /**
             * Adds a handler to the map
             * Ex. `map.addHandler(new T.DragHandler())`
             *
             * By default the map contains two handlers: {DragHandler} and {MouseWheelHandler}
             *
             * @param {DragHandler|MouseWheelHandler|MarkerDragHandler} handler The new handler to add to the map
             * @summary Adds a handler to the map <br />
             * Ex. `map.addHandler(new T.DragHandler())`
             */
        addHandler: function (handler) {
            handler.init(this);
        },
        /**
             * Adds a UI control to the map
             * Ex. `map.addControl(new T.RulerControl({...}))`
             *
             * @param {AttributionControl|LayersControl|NavigationControl|RulerControl|ScaleControl} control The new control to add to the map
             * @example examples/docs/map/add-control 100% 500
             */
        addControl: function (control) {
            control.onAdd(this);
            return this;
        },
        reset: function () {
            this.resetView(this.getCenter(), this.zoom);
        },
        /**
             * Resets the map to the specified center and zoom
             *
             * @param {LatLng} center The desired map center position.
             * @param {Number} zoom The desired map zoom level.
             * @param {Boolean} hard If true, will not use the back buffer.
             *
             * @example examples/docs/map/reset-view 100% 500
             */
        resetView: function (center, zoom, hard) {
            this.sizeChanged = true;    // Hold the last transform operation in case other layers need it (e.g. back-buffer)
            // Hold the last transform operation in case other layers need it (e.g. back-buffer)
            if (this.lastTransform.matrix) {
                var matrix = this.lastTransform.matrix;
                if (matrix.m11 === 1 && matrix.m22 === 1) {
                    matrix = this.computeTransformMatrix(this.calculateCenterInPixel(), zoom);
                }
                matrix.m13 += this.getLayersPos().x;
                matrix.m23 += this.getLayersPos().y;
            }    // Reset center and zoom
            // Reset center and zoom
            this.zoom = this.limitZoom(zoom);
            this.center = center;    // Reset transformations
            // Reset transformations
            T.DomUtil.position(this.$container.layers, new T.Point(0, 0));
            T.AnimUtil.reset(this.$layers);
            if (hard) {
                this.events.fire('hard-reset');
            }
            this.events.fire('view-reset');
        },
        /**
             * Adds a layer to the map
             * Ex. `map.addLayer(new T.MarkersLayer())`
             *
             * @param {BaseLayer|TileLayer|VectorLayer|MarkersLayer|ImageLayer|PopupsLayer|WmsLayer} layer The new layer to add to the map
             * @return {[Map](#top)} The map object
             * @example examples/docs/map/add-layer 100% 500
             */
        addLayer: function (layer) {
            this.$layersArray.push(layer);
            layer.onAdd(this);
            this.events.fire('layer-add', layer);
            return this;
        },
        /**
             * Removes a layer from the map
             *
             * @param {BaseLayer|TileLayer|VectorLayer|MarkersLayer|ImageLayer|PopupsLayer|WmsLayer} layer The layer on the map to remove
             * @return {[Map](#top)} The map object
             */
        removeLayer: function (layer) {
            var layerIndex = this.getLayerIndex(layer);
            if (layerIndex === -1) {
                return;
            }
            this.$layersArray.splice(layerIndex, 1);
            layer.onRemove();
            this.events.fire('layer-remove', layer);
            return this;
        },
        getSize: function () {
            if (!this.size || this.sizeChanged) {
                this.size = new T.Point(this.$container.clientWidth, this.$container.clientHeight);
                this.sizeChanged = false;
            }
            return this.size.clone();
        },
        setDefaults: function (defaults) {
            this.options = T.Util.defaults(defaults, this.options);
        },
        setCrs: function (crs) {
            this.options.crs = crs;
            this.options.minZoom = 0;
            this.options.maxZoom = crs.resolutions.length - 1;
            this.events.fire('zoom-interval-change');
        },
        /**
             * Sets the map center to a given location
             *
             * @param {LatLng} center The coordinates of the new map center
             */
        setCenter: function (center) {
            this.resetView(center, this.getZoom());
        },
        /**
             * Sets the map to a new zoom level
             *
             * @param {Number} zoom The value of the new zoom level
             */
        setZoom: function (zoom) {
            this.zoomAround(this.calculateCenter(), zoom);
        },
        /*
             * Sets the URL hash with the specified location information formatted like: '@'latitude,longitude,zoom'z'
             */
        setUrlLocation: function () {
            var urlLocationString = null;
            if (this.options.urlLocation) {
                urlLocationString = '@' + this.getCenter().lat.toFixed(7) + ',' + this.getCenter().lng.toFixed(7) + ',' + this.getZoom() + 'z';
                if (T.Util.browserSupportsHistoryApi()) {
                    T.Util.setBrowserHistory('#' + urlLocationString, this.options.noHistory);
                    T.Util.handleBrowserNavigation(T.Util.bind(function () {
                    }    //this.setPositionFromUrl();
                    , this));
                } else {
                    T.Util.setBrowserUrlHash(urlLocationString);
                }
            }
        },
        /*
             * Sets the map center & zoom by reading them from the URL hash.
             * If this information is not in the URL hash, the map center & zoom values are taken from the map options.
             */
        setPositionFromUrl: function () {
            var urlPosition = T.Util.getBrowserUrlHash(), urlContent = null, urlItems = [], latitude = null, longitude = null, zoom = null;
            if (!this.options.urlLocation) {
                return;
            }
            if (urlPosition && urlPosition.length > 1) {
                urlContent = urlPosition.substring(1, urlPosition.length);    // eliminate # as first char
                // eliminate # as first char
                urlItems = urlContent.split(',');    // => ['@'latitude, longitude, zoom'z']
                // Check that all options exist: center (lat + lon) & zoom
                // => ['@'latitude, longitude, zoom'z']
                // Check that all options exist: center (lat + lon) & zoom
                if (urlItems.length === 3) {
                    latitude = parseFloat(urlItems[0].substring(1, urlItems[0].length));
                    longitude = parseFloat(urlItems[1]);
                    zoom = parseInt(urlItems[2]);
                    this.center = new T.LatLng(latitude, longitude);
                    this.zoom = zoom;    // Reset map with updated position
                    // Reset map with updated position
                    this.resetView(this.center, this.zoom);
                } else {
                    // If no position in URL hash => use position from options
                    this.zoom = this.options.zoom;
                    this.center = this.options.center;    // Reset map with updated position
                    // Reset map with updated position
                    this.resetView(this.center, this.zoom);
                }
            }
        },
        /*
             * Sets the map center & zoom by attempting to geolocate the user using the browser's buil-in geolocation system.
             * If the browser does not have the geolocation system or it has been disabled by the user, the position is set from the URL hash or the map options.
             */
        setPositionFromGeolocation: function () {
            T.Util.geolocate(T.Util.bind(function (latitude, longitude) {
                if (latitude && longitude) {
                    this.center = new T.LatLng(latitude, longitude);
                    this.zoom = 14;    // Reset map with updated position
                    // Reset map with updated position
                    this.resetView(this.center, this.zoom);
                } else {
                    this.setPositionFromUrl();
                }
            }, this), T.Util.bind(function (error) {
                this.setPositionFromUrl();
            }, this));
        },
        /**
             * Returns the map's current zoom level
             *
             * @return {Number} The current zoom level
             */
        getZoom: function () {
            return this.zoom;
        },
        /**
             * Returns the map's current center coordinates
             *
             * @return {LatLng} The current center coordinates
             */
        getCenter: function () {
            return this.calculateCenter();
        },
        getViewportCenter: function () {
            return this.center.clone();
        },
        getCenterInPixels: function () {
            return this.project(this.getViewportCenter(), this.getZoom());
        },
        getTopLeftInPixels: function () {
            return this.getCenterInPixels().substract(this.getSize().divideBy(2));
        },
        getResolution: function () {
            return this.options.crs.resolution(this.zoom);
        },
        /**
             * Converts map coordinates to a point representing the pixels on the map from the top left corner
             *
             * @param {LatLng} latLng The coordinates that will be converted
             * @return {Point} The point representing the converted coordinates
             */
        latLngToPoint: function (latlng) {
            latlng = T.LatLng.from(latlng);
            var projectedPoint = this.project(latlng).round();
            return projectedPoint.substract(this.getTopLeftInPixels());
        },
        /**
             * Converts a point representing the pixels on the map from the top left corner to map coordinates
             *
             * @param {Point} The point that will be converted
             * @return {LatLng} latLng The coordinates converted from the given point
             */
        pointToLatLng: function (point) {
            var projectedPoint = point.add(this.getTopLeftInPixels());
            return this.unproject(projectedPoint);
        },
        getLayersCenter: function () {
            var layerPoint = this.getSize().divideBy(2).substract(this.getLayersPos());
            return layerPoint;
        },
        getPixelBounds: function () {
            var topLeft = this.calculateTopLeft();
            return new T.Bounds(topLeft, topLeft.add(this.getSize()));
        },
        getLayersPos: function () {
            return T.DomUtil.position(this.$container.layers);
        },
        getLayers: function () {
            return this.$layersArray;
        },
        calculateCenter: function () {
            return this.unproject(this.calculateCenterInPixel());
        },
        calculateCenterInPixel: function () {
            var projectedCenter = this.getLayersCenter().add(this.getTopLeftInPixels());
            return projectedCenter;
        },
        /* calculates the center taking into consideration map panning */
        calculateTopLeft: function () {
            var half = this.getSize().divideBy(2);
            var topLeft = this.getCenterInPixels().substract(half).substract(this.getLayersPos());
            return topLeft;
        },
        latLngToContainerPoint: function (latlng) {
            latlng = T.LatLng.from(latlng);
            var projectedPoint = this.project(latlng).round();
            return projectedPoint.substract(this.calculateTopLeft());
        },
        /* transform a map container point into LatLng coordinate  */
        containerPointToLatLng: function (point) {
            return this.unproject(this.calculateTopLeft().add(point));
        },
        /* returns  the map resolution (meters/pixel)  at  the center of the viewport */
        getMapResolution: function () {
            var point = this.getCenter();
            var zoom = this.getZoom();
            var tileSize = 256;
            var earthRadius = 6378137;
            var res = Math.cos(point.lat * Math.PI / 180) * 2 * Math.PI * earthRadius / (tileSize * Math.pow(2, zoom));
            return res;
        },
        /* returns horizontal arc angle in degrees for given width in meters at a specific coordinate */
        metersToArcAngle: function (meters, latlng) {
            var equatorialArc = meters * 360 / 40075016;    // the nasty number is the earth circumference at the equator
            // the nasty number is the earth circumference at the equator
            return equatorialArc / Math.cos(latlng.lat * Math.PI / 180);
        },
        /**
             * Pans the map by the given offset value.
             * Ex. `map.panBy(new T.Point(-100, 0))`, pans the map to the right by 100 pixels
             *
             * @param {Point} offset The value by which to pan the map
             * @param {Boolean} skipRefresh If `true`, the `move-end` event is not fired
             * @return {[Map](#top)} The map instance
             */
        panBy: function (offset, skipRefresh) {
            if (this.animatingZoom) {
                return;
            }
            this.events.fire('move-start');
            T.DomUtil.position(this.$container.layers, this.getLayersPos().add(offset));
            this.events.fire('move');
            if (!skipRefresh) {
                this.events.fire('move-end');
            }
            return this;
        },
        /**
             * Sets the map location to new coordinates and zooms the map to a new zoom level
             *
             * @param {LatLng} latLng The coordinates of the new map location
             * @param {Number} zoom The value of the new zoom level
             * @param {Boolean} withAnim If `true`, animation effects will be used if possible to transit to the new location
             */
        zoomAround: function (latlng, zoom, withAnim) {
            zoom = this.limitZoom(zoom);
            withAnim = withAnim || false;
            if (zoom === this.zoom) {
                return;
            }
            var point = this.project(latlng);
            var newPoint = this.project(latlng, zoom);
            var diff = newPoint.substract(point);
            var pixelCenter = this.calculateCenterInPixel().add(diff);
            var latLngCenter = this.unproject(pixelCenter, zoom);
            var matrix = this.computeTransformMatrix(point, zoom);
            if (T.AnimUtil.canAnim() && withAnim) {
                // Don't do anything while zooming
                if (this.animatingZoom) {
                    return;
                }
                this.animatingZoom = true;
                if (this.dragHandler) {
                    this.dragHandler.disable();
                }
                var self = this;
                T.AnimUtil.onTransitionEnd(this.$layers, function () {
                    if (self.dragHandler) {
                        self.dragHandler.enable();
                    }
                    self.animatingZoom = null;
                    self.lastTransform.matrix = new T.Matrix2D(T.AnimUtil.getTransform(self.$layers));
                    self.resetView(latLngCenter, zoom);
                });
                T.AnimUtil.transition(this.$layers, 0.1);
                T.AnimUtil.setMatrix(this.$layers, matrix.toString());
            } else {
                this.lastTransform.matrix = matrix;
                this.resetView(latLngCenter, zoom);
            }
        },
        // ##Limits map bounds ##
        //
        // Limits map bounds based on map maxExtent and current bounds
        limitBounds: function () {
            if (!this.options.maxExtent) {
                return;
            }
            var currentBounds = this.getBounds(), maxBounds = this.options.maxExtent, newBounds = new T.LatLngBounds(currentBounds.getSouthWest(), currentBounds.getNorthEast()), boundsChanged = false;
            if (!T.Util.floatEqual(currentBounds.northEast.lat, maxBounds.northEast.lat) && currentBounds.northEast.lat > maxBounds.northEast.lat) {
                boundsChanged = true;
                newBounds.northEast.lat = maxBounds.northEast.lat;
                newBounds.southWest.lat -= currentBounds.northEast.lat - maxBounds.northEast.lat;
            } else if (!T.Util.floatEqual(currentBounds.southWest.lat, maxBounds.southWest.lat) && currentBounds.southWest.lat < maxBounds.southWest.lat) {
                boundsChanged = true;
                newBounds.southWest.lat = maxBounds.southWest.lat;
                newBounds.northEast.lat -= currentBounds.southWest.lat - maxBounds.southWest.lat;
            }
            if (!T.Util.floatEqual(currentBounds.northEast.lng, maxBounds.northEast.lng) && currentBounds.northEast.lng > maxBounds.northEast.lng) {
                boundsChanged = true;
                newBounds.northEast.lng = maxBounds.northEast.lng;
                newBounds.southWest.lng -= currentBounds.northEast.lng - maxBounds.northEast.lng;
            } else if (!T.Util.floatEqual(currentBounds.southWest.lng, maxBounds.southWest.lng) && currentBounds.southWest.lng < maxBounds.southWest.lng) {
                boundsChanged = true;
                newBounds.southWest.lng = maxBounds.southWest.lng;
                newBounds.northEast.lng -= currentBounds.southWest.lng - maxBounds.southWest.lng;
            }
            if (boundsChanged) {
                this.fitToBounds(newBounds, 20);
            }
        },
        // ##Limits map zoom ##
        //
        // Limits map zoom based on min&max map zoom and maxExtent zoom
        limitZoom: function (zoom) {
            var maxExtentZoom = this.options.maxExtent ? this.getZoomForBounds(this.options.maxExtent) + 1 : zoom;
            return T.Util.maxMin(this.options.maxZoom, maxExtentZoom, T.Util.minMax(this.options.minZoom, this.options.maxZoom, zoom));
        },
        project: function (latlng, zoom) {
            zoom = zoom === undefined ? this.zoom : zoom;
            return this.options.crs.latLngToPoint(latlng, zoom);
        },
        unproject: function (point, zoom) {
            zoom = zoom === undefined ? this.zoom : zoom;
            return this.options.crs.pointToLatLng(point, zoom);
        },
        //projected to pixel
        transform: function (point, zoom) {
            zoom = zoom === undefined ? this.zoom : zoom;
            return this.options.crs.transform(point, zoom);
        },
        //pixel to projected
        untransform: function (point, zoom) {
            zoom = zoom === undefined ? this.zoom : zoom;
            return this.options.crs.untransform(point, zoom);
        },
        /**
             * Sets the map location and zoom level to the specified bounds
             *
             * @param {LatLngBounds} bounds The new bounds of the map
             * @param {Number} tolerance The new bounds' tolerance (margin) in pixels
             */
        fitToBounds: function (bounds, tolerance) {
            var zoom = this.getZoomForBounds(bounds, tolerance);
            var ne = this.project(bounds.getNorthEast(), zoom);
            var sw = this.project(bounds.getSouthWest(), zoom);
            var center = this.unproject(sw.add(ne).divideBy(2), zoom);
            this.resetView(center, zoom);
        },
        /**
             * Returns the current bounds of the map
             *
             * @return {LatLngBounds} The current bounds of the map
             */
        getBounds: function () {
            var pixelBounds = this.getPixelBounds();
            var bottomLeft = this.unproject(pixelBounds.bottomLeft());
            var topRight = this.unproject(pixelBounds.topRight());
            var bounds = new T.LatLngBounds(bottomLeft, topRight);
            return bounds;
        },
        getProjectedBounds: function () {
            var key, queryParams = this.options.queryParams, image = this.image, crs = this.options.crs, size = this.getSize(), bottomLeft = new T.Point(0, 0), topRight = new T.Point(0, 0), projCenter = this.untransform(this.calculateCenterInPixel()), half = size.divideBy(2).multiplyBy(crs.resolutions[this.getZoom()]);    //map.untransform(size.divideBy(2));
            //Compute bounds in projected coordinates
            //map.untransform(size.divideBy(2));
            //Compute bounds in projected coordinates
            bottomLeft.x = projCenter.x - half.x;
            bottomLeft.y = projCenter.y - half.y;
            topRight.x = projCenter.x + half.x;
            topRight.y = projCenter.y + half.y;
            var bounds = new T.Bounds(bottomLeft, topRight);
            return bounds;
        },
        getZoomForBounds: function (bounds, tolerance) {
            var zoom = this.options.minZoom, viewportSize = this.getSize(), i = 0;
            tolerance = tolerance || 0;
            for (i = this.options.minZoom; i <= this.options.maxZoom; i++) {
                var topRight = this.project(bounds.getNorthEast(), i);
                var bottomLeft = this.project(bounds.getSouthWest(), i);
                var bWidth = topRight.x - bottomLeft.x;
                var bHeight = bottomLeft.y - topRight.y;
                if (bWidth - tolerance <= viewportSize.x && bHeight - tolerance <= viewportSize.y) {
                    zoom = i;
                }
            }
            return zoom;
        },
        getLayerIndex: function (layer) {
            return this.$layersArray.indexOf(layer);
        },
        /* Returns the mouse position inside map container given a mouse event */
        computeMousePosition: function (evt) {
            var offset = T.DomUtil.calculateElementClientOffset(this.$container);
            var containerOffset = new T.Point(offset.left, offset.top);
            var mousePosition = new T.Point(evt.clientX, evt.clientY).substract(containerOffset);
            return mousePosition;
        },
        /**
             * Moves a layer to a new index (zIndex) in the stack of layers
             *
             * @param {BaseLayer|TileLayer|VectorLayer|MarkersLayer|ImageLayer|PopupsLayer|WmsLayer} layer The layer for which a new position will be set
             * @param {Number} newLayerIndex The new position of the layer
             */
        moveLayer: function (layer, newLayerIndex) {
            if (!layer || layer.getIndex() === newLayerIndex || newLayerIndex < 0 || newLayerIndex >= this.$layersArray.length) {
                return;
            }    //1. Move layers in DOM
            //1. Move layers in DOM
            var referenceLayerElement = this.$layers.children.item(newLayerIndex);
            layer.getContainer().parentNode.removeChild(layer.getContainer());    //remove layer from DOM
            //remove layer from DOM
            if (layer.getIndex() > newLayerIndex) {
                //When moving back insert target before reference
                this.$layers.insertBefore(layer.getContainer(), referenceLayerElement);
            } else {
                //When moving to front insert target after reference
                referenceLayerElement.parentNode.insertBefore(layer.getContainer(), referenceLayerElement.nextSibling);
            }    //2. Move in map layers array
            //2. Move in map layers array
            this.$layersArray.splice(layer.getIndex(), 1);    //remove layer from array
            //remove layer from array
            this.$layersArray.splice(newLayerIndex, 0, layer);    //add layer at new position
            //3. Fire layer move event
            //add layer at new position
            //3. Fire layer move event
            this.events.fire('layer-moved', layer);
        },
        /**
             * Moves a layer up (increase its position) in the stack of layers
             * @param {BaseLayer|TileLayer|VectorLayer|MarkersLayer|ImageLayer|PopupsLayer|WmsLayer} layer The layer for which a new position will be set
             */
        moveLayerUp: function (layer) {
            //don't make a loop
            if (layer.getIndex() !== -1 && layer.getIndex() !== this.$layersArray.length - 1) {
                this.moveLayer(layer, layer.getIndex() + 1);
            }
        },
        /**
             * Moves a layer down (decrease its position) in the stack of layers
             * @param {BaseLayer|TileLayer|VectorLayer|MarkersLayer|ImageLayer|PopupsLayer|WmsLayer} layer The layer for which a new position will be set
             */
        moveLayerDown: function (layer) {
            //don't make a loop
            if (layer.getIndex() !== -1 && layer.getIndex() !== 0) {
                this.moveLayer(layer, layer.getIndex() - 1);
            }
        },
        computeTransformMatrix: function (point, zoom) {
            var zoomDiff = zoom - this.getZoom();
            var ratio = Math.pow(2, zoomDiff);
            var posDiff = this.getLayersPos().substract(point.substract(this.calculateTopLeft()));
            var newPosition = posDiff.multiplyBy(ratio).substract(posDiff);
            var matrix = new T.Matrix2D(ratio, 0, 0, ratio, newPosition.x, newPosition.y);
            return matrix;
        }
    };
    exports.Map = Map;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window*/
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window*/
(function (exports) {
    /**
         * MapTypes object holds all possible map tiles types.
         *
         * - `STANDARD`
         * - `STANDARD_LIGHT`
         * - `BASIC`
         * - `BASIC_LIGHT`
         * - `BORDERS`
         * - `LABELS`
         * - `ROADS`
         *
         * @summary MapTypes object holds all possible map tiles types.
         */
    var MapTypes = {
        STANDARD: 'Standard map',
        STANDARD_LIGHT: 'Standard map - light',
        BASIC: 'Basic map',
        BASIC_LIGHT: 'Basic map - light',
        BORDERS: 'Borders',
        LABELS: 'Labels',
        ROADS: 'Roads'
    };    // Export
    // Export
    exports.MapTypes = MapTypes;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window*/
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window*/
(function (exports) {
    /**
         * MapStyles object holds all possible map tiles styles.
         *
         * - `LIGHT`
         * - `DARK`
         *
         * @summary MapStyles object holds all possible map tiles styles.
         */
    var MapStyles = {
        LIGHT: 'Light',
        DARK: 'Dark'
    };    // Export
    // Export
    exports.MapStyles = MapStyles;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * The class from which all geometry classes inherit
         *
         * @abstract true
         * @constructor true
         * @summary The Path's constructor
         *
         * @option {String} path The path data of the geometry (default: `''`)
         * @option {Boolean} clickable If `true`, the geometry handles click events (default: `true`)
         * @option {Boolean} reverseCoordinates If `true`, the coordinates will be handled as reversed (default: `false`)
         *
         * @param {Object} [options](#options) The options of the geometry
         */
    var Path = function (options) {
        this.type = 'path';
        this.options = T.Util.defaults(options, this.options);
        this.style = T.Util.defaults({}, this.options.style);
    };
    Path.prototype = {
        options: {
            path: '',
            clickable: true,
            reverseCoordinates: false
        },
        style: null,
        initContainer: function () {
            if (this.context) {
                this.context.initContainer(this);    //throw new Error("Geometry context not available.");
            }    //this.context.initContainer(this);
        },
        /**
             * Sets the style of the geometry
             *
             * @param {Object} style An object containing the style properties like whether to use `stroke` (Boolean), whether to use `fill` (Boolean), stroke's `color`, `opacity`, `fillColor`, `fillOpacity`
             */
        setStyle: function (style) {
            T.Util.extend(this.style, style);
            this.draw();
        },
        /**
             * Returns the current style
             *
             * @return {Object} The current style
             */
        getStyle: function () {
            return this.context.options.style;
        },
        /* Override */
        projectCoords: function () {
        },
        reset: function () {
            if (this.context) {
                this.projectCoords();
                this.draw();
            }
        },
        draw: function () {
            if (this.context) {
                this.context.applyStyle(this);
                this.context.draw(this);
            }
        },
        onAdd: function (layer) {
            var defaultStyle = T.Util.clone(layer.options.defaultStyle);
            this.layer = layer;
            this.context = layer.context;
            this.initContainer();
            this.setStyle(T.Util.extend(defaultStyle, this.style));
            this.reset();
        },
        onRemove: function (layer) {
            this.context.dispose(this);
            this.context = null;
            this.layer = null;
        }
    };    // Export
    // Export
    exports.Path = Path;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * Polyline class is responsible for drawing polyline geometry on the map using the {VectorLayer}
         *
         * @inherits {Path}
         * @summary Polyline's constructor
         * @constructor true
         *
         * @param {Array} coords The list of coordinates used to draw the polyline
         * @param {Object} options Path's options
         */
    var Polyline = function (coords, options) {
        // Call super class constructor
        T.Path.call(this, options);
        this.type = 'polyline';
        this.coords = coords;
        this.projectedCoords = [];
        this.noneFillGeometry = true;
    };
    Polyline.prototype = {
        /* Override */
        projectCoords: function () {
            var i, j;
            this.projectedCoords = [];
            for (i = 0; i < this.coords.length; i++) {
                this.projectedCoords.push(this.layer.map.latLngToContainerPoint(this.options.reverseCoordinates ? this.coords[i].concat().reverse() : this.coords[i]).floor());
            }
        }
    };    //Extend class
    //Extend class
    T.Util.inherit(Polyline, T.Path);    // Export
    // Export
    exports.Polyline = Polyline;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * Polygon class is responsible for drawing polygon geometry on the map using the {VectorLayer}
         *
         * @inherits {Path}
         * @summary Polygon's constructor
         * @constructor true
         *
         * @param {Array} coords The list of coordinates used to draw the polygon
         * @param {Object} options Path's options
         */
    var Polygon = function (coords, options) {
        // Call super class constructor
        T.Path.call(this, options);
        this.type = 'polygon';
        this.coords = coords;
        this.projectedCoords = [];
    };
    Polygon.prototype = {
        /* Override */
        projectCoords: function () {
            var i, j, k, line;
            this.projectedCoords = [];
            for (i = 0; i < this.coords.length; i++) {
                line = [];
                for (j = 0; j < this.coords[i].length; j++) {
                    line.push(this.layer.map.latLngToContainerPoint(this.options.reverseCoordinates ? this.coords[i][j].concat().reverse() : this.coords[i][j]).floor());
                }
                this.projectedCoords.push(line);
            }
        }
    };    //Extend class
    //Extend class
    T.Util.inherit(Polygon, T.Path);    // Export
    // Export
    exports.Polygon = Polygon;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * MultiPolyline class is responsible for drawing multi-polyline geometry on the map using the {VectorLayer}
         *
         * @inherits {Path}
         * @summary MultiPolyline's constructor
         * @constructor true
         *
         * @param {Array} coords The list of coordinates used to draw the multi-polyline
         * @param {Object} options Path's options
         */
    var MultiPolyline = function (coords, options) {
        // Call super class constructor
        T.Path.call(this, options);
        this.type = 'multipolyline';
        this.coords = coords;
        this.projectedCoords = [];
        this.noneFillGeometry = true;
    };
    MultiPolyline.prototype = {
        /* Override */
        projectCoords: function () {
            var i, j, k, line = [];
            this.projectedCoords = [];
            for (i = 0; i < this.coords.length; i++) {
                line = [];
                for (j = 0; j < this.coords[i].length; j++) {
                    line.push(this.layer.map.latLngToContainerPoint(this.options.reverseCoordinates ? this.coords[i][j].concat().reverse() : this.coords[i][j]).floor());
                }
                this.projectedCoords.push(line);
            }
        }
    };    //Extend class
    //Extend class
    T.Util.inherit(MultiPolyline, T.Path);    // Export
    // Export
    exports.MultiPolyline = MultiPolyline;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * MultiPolygon class is responsible for drawing multi-polygon geometry on the map using the {VectorLayer}
         *
         * @inherits {Path}
         * @summary MultiPolygon's constructor
         * @constructor true
         *
         * @param {Array} coords The list of coordinates used to draw the multi-polygon
         * @param {Object} options Path's options
         */
    var MultiPolygon = function (coords, options) {
        // Call super class constructor
        T.Path.call(this, options);
        this.type = 'multipolygon';
        this.coords = coords;
        this.projectedCoords = [];
    };
    MultiPolygon.prototype = {
        /* Override */
        projectCoords: function () {
            var i, j, k, line = [], poly = [];
            this.projectedCoords = [];
            for (k = 0; k < this.coords.length; k++) {
                poly = [];
                for (i = 0; i < this.coords[k].length; i++) {
                    line = [];
                    for (j = 0; j < this.coords[k][i].length; j++) {
                        line.push(this.layer.map.latLngToContainerPoint(this.options.reverseCoordinates ? this.coords[k][i][j].concat().reverse() : this.coords[k][i][j]).floor());
                    }
                    poly.push(line);
                }
                this.projectedCoords.push(poly);
            }
        }
    };    //Extend class
    //Extend class
    T.Util.inherit(MultiPolygon, T.Path);    // Export
    // Export
    exports.MultiPolygon = MultiPolygon;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * Rectangle class is responsible for drawing rectangle geometry on the map using the {VectorLayer}
         *
         * @inherits {Polygon}
         * @summary Rectangle's constructor
         * @constructor true
         *
         * @param {Array} bounds The list of coordinates used to draw the rectangle
         * @param {Object} options Polygon's options
         */
    var Rectangle = function (bounds, options) {
        // Call super class constructor
        T.Polygon.call(this, this.boundsToCoordinates(bounds), options);
        this.type = 'rectangle';
        this.projectedCoords = [];
    };
    Rectangle.prototype = {
        /**
             * Sets new bounds to the rectangle
             *
             * @param {Array} bounds The list of coordinates used to draw the rectangle
             */
        setBounds: function (bounds) {
            this.coords = this.boundsToCoordinates(bounds);
            this.reset();
        },
        boundsToCoordinates: function (bounds) {
            bounds = T.LatLngBounds.from(bounds);
            return [[
                bounds.getSouthWest(),
                bounds.getSouthEast(),
                bounds.getNorthEast(),
                bounds.getNorthWest()
            ]];
        }
    };    //Extend class
    //Extend class
    T.Util.inherit(Rectangle, T.Polygon);    // Export
    // Export
    exports.Rectangle = Rectangle;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * Circle class is responsible for drawing circles on the map using the {VectorLayer}
         *
         * @inherits {Path}
         * @summary Circle's constructor
         * @constructor true
         *
         * @option {String} radiusMeasure The radius' unit of measurement: `pixel` or `meter` (default: `pixel`)
         *
         * @param {Array} center The circle's center coordinates
         * @param {Number} radius The radius of the circle expressed in the radius unit of measurement (default: `100`)
         * @param {Object} [options](#options) Circle's options
         */
    var Circle = function (center, radius, options) {
        // Call super class constructor
        T.Path.call(this, options);
        this.type = 'circle';
        this.radius = radius || 100;
        this.center = T.LatLng.from(center);
        this.radiusMeasure = options.radiusMeasure === 'meter' ? options.radiusMeasure : 'pixel';
    };
    Circle.prototype = {
        /**
             * Sets a new radius of the circle
             *
             * @param {Number} radius The new radius
             */
        setRadius: function (radius) {
            this.radius = radius;
            this.reset();
        },
        /**
             * Sets a new center of the circle
             *
             * @param {Array} center The circle's new center coordinates
             */
        setCenter: function (center) {
            this.center = T.LatLng.from(center);
            this.reset();
        },
        /* Override */
        projectCoords: function () {
            this.projectedCenter = this.layer.map.latLngToContainerPoint(this.options.reverseCoordinates ? this.center.concat().reverse() : this.center).floor();
            if (this.radiusMeasure === 'meter') {
                var diff = this.layer.map.metersToArcAngle(this.radius, this.center);
                this.porjectedStartPoint = this.layer.map.latLngToContainerPoint(this.options.reverseCoordinates ? [
                    this.center.lng,
                    this.center.lat - diff
                ] : [
                    this.center.lat,
                    this.center.lng - diff
                ]).floor();
            } else {
                this.porjectedStartPoint = this.projectedCenter.clone().substract(new T.Point(this.radius, 0));
            }
        }
    };    //Extend class
    //Extend class
    T.Util.inherit(Circle, T.Path);    // Export
    // Export
    exports.Circle = Circle;
}(T));    // > SvgContext.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > SvgContext class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50, continue: true */
/*global window, T, document */
// > SvgContext.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > SvgContext class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50, continue: true */
/*global window, T, document */
(function (exports) {
    var SvgContext = function (svg, options) {
        this.options = T.Util.defaults(options, this.options);
        this.$svg = svg;
    };
    SvgContext.prototype = {
        initContainer: function (geometry) {
            if (!geometry.$container) {
                geometry.$container = T.DomUtil.createSvgElement('g');
                geometry.$path = T.DomUtil.createSvgElement('path');
                geometry.$path.setAttributeNS(null, 'fill-rule', 'evenodd');
                geometry.$container.appendChild(geometry.$path);    //if (this.options.clickable) {
                //if (this.options.clickable) {
                T.DomEventUtil.on(geometry.$container, 'click', function (e) {
                    if (geometry.layer !== null) {
                        geometry.layer.onGeometryClick(e, geometry);
                    }
                }, this);    //}
            }
            this.$svg.$g.appendChild(geometry.$container);
        },
        applyStyle: function (geometry) {
            var style = geometry.style;
            if (style.stroke) {
                geometry.$path.setAttributeNS(null, 'stroke', style.color);
                geometry.$path.setAttributeNS(null, 'stroke-opacity', style.opacity);
                geometry.$path.setAttributeNS(null, 'stroke-width', style.weight);
                geometry.$path.setAttributeNS(null, 'stroke-linecap', style.lineCap);
                geometry.$path.setAttributeNS(null, 'stroke-linejoin', style.lineJoin);
                geometry.$path.setAttributeNS(null, 'stroke-miterlimit', style.miterLimit);
            } else {
                geometry.$path.setAttribute('stroke', 'none');
            }
            if (style.fill && !geometry.noneFillGeometry) {
                geometry.$path.setAttributeNS(null, 'fill', style.fillColor || style.color);
                geometry.$path.setAttributeNS(null, 'fill-opacity', style.fillOpacity);
            } else {
                geometry.$path.setAttributeNS(null, 'fill', 'none');
            }
        },
        getPathString: function (geometry) {
            var coords = geometry.projectedCoords;
            var i, j, k, path = '';
            if (geometry.type !== 'circle' && coords.length === 0) {
                return;
            }
            switch (geometry.type) {
            case 'polyline':
                path = 'M ' + coords[0].x + ' ' + coords[0].y;
                for (i = 1; i < coords.length; i++) {
                    path += 'L ' + coords[i].x + ' ' + coords[i].y;
                }
                return path;
            case 'polygon':
            case 'rectangle':
                for (i = 0; i < coords.length; i++) {
                    for (j = 0; j < coords[i].length; j++) {
                        if (j === 0) {
                            path += 'M ' + coords[i][0].x + ' ' + coords[i][0].y;
                            continue;
                        }
                        path += 'L ' + coords[i][j].x + ' ' + coords[i][j].y;
                    }
                    path += ' Z ';
                }
                return path;
            case 'multipolyline':
                for (i = 0; i < coords.length; i++) {
                    for (j = 0; j < coords[i].length; j++) {
                        if (j === 0) {
                            path += 'M ' + coords[i][0].x + ' ' + coords[i][0].y;
                            continue;
                        }
                        path += 'L ' + coords[i][j].x + ' ' + coords[i][j].y;
                    }
                }
                return path;
            case 'multipolygon':
                for (k = 0; k < coords.length; k++) {
                    for (i = 0; i < coords[k].length; i++) {
                        for (j = 0; j < coords[k][i].length; j++) {
                            if (j === 0) {
                                path += 'M ' + coords[k][i][0].x + ' ' + coords[k][i][0].y;
                                continue;
                            }
                            path += 'L ' + coords[k][i][j].x + ' ' + coords[k][i][j].y;
                        }
                        path += ' Z ';
                    }
                }
                return path;
            case 'circle':
                if (!geometry.projectedCenter) {
                    return;
                }
                var radius = geometry.projectedCenter.x - geometry.porjectedStartPoint.x;
                path = 'M ' + geometry.porjectedStartPoint.x + ' ' + geometry.porjectedStartPoint.y;
                path += ' A ' + radius + ' ' + radius;
                path += ',0,1,1, ' + geometry.porjectedStartPoint.x + ' ' + (geometry.porjectedStartPoint.y + 0.01) + ' Z';
                return path;
            }
        },
        draw: function (geometry) {
            var path = this.getPathString(geometry);
            if (path) {
                geometry.$path.setAttributeNS(null, 'd', path);
            } else {
                geometry.$path.setAttributeNS(null, 'd', 'M 0 0');    /* WebKit throws ' Error: Problem parsing d="" ' when having empty d attribute */
                /* courrent workaraound it to set path to the 0,0 point */
                //this.$path.removeAttributeNS(null, 'd');
            }
        },
        dispose: function (geometry) {
            this.$svg.$g.removeChild(geometry.$container);
        }
    };    // Export
    // Export
    exports.SvgContext = SvgContext;
}(T));    // > CanvasContext.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > CanvasContext class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
// > CanvasContext.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > CanvasContext class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var CanvasContext = function (canvas, options) {
        this.options = T.Util.defaults(options, this.options);
        this.$canvas = canvas;
        this.ctx = this.$canvas.getContext('2d');
    };
    CanvasContext.prototype = {
        initContainer: function (geometry) {
        },
        applyStyle: function (geometry) {
            var style = geometry.style;
            if (style.stroke) {
                this.ctx.strokeStyle = T.Util.hexToRgba(style.color, style.opacity);
                this.ctx.lineWidth = style.weight;
                this.ctx.lineCap = style.lineCap;
                this.ctx.lineJoin = style.lineJoin;
                this.ctx.miterLimit = style.miterLimit;
            }
            if (style.fill) {
                this.ctx.fillStyle = T.Util.hexToRgba(style.fillColor || style.color, style.fillOpacity);
            }
        },
        draw: function (geometry) {
            var coords = geometry.projectedCoords, style = geometry.style;
            var i, j, k, polygonPoints;
            this.ctx.beginPath();
            switch (geometry.type) {
            case 'polyline':
                if (coords.length > 0) {
                    this.ctx.moveTo(coords[0].x, coords[0].y);
                    for (i = 1; i < coords.length; i++) {
                        this.ctx.lineTo(coords[i].x, coords[i].y);
                    }
                }
                break;
            case 'polygon':
            case 'rectangle':
                if (coords.length > 0) {
                    for (i = 0; i < coords.length; i++) {
                        polygonPoints = coords[i].concat();
                        if (i > 0) {
                            polygonPoints.reverse();
                        }
                        this.ctx.moveTo(polygonPoints[0].x, polygonPoints[0].y);
                        for (j = 1; j < polygonPoints.length; j++) {
                            this.ctx.lineTo(polygonPoints[j].x, polygonPoints[j].y);
                        }
                        this.ctx.closePath();
                    }
                }
                break;
            case 'multipolyline':
                if (coords.length > 0) {
                    for (i = 0; i < coords.length; i++) {
                        polygonPoints = coords[i].concat();
                        this.ctx.moveTo(polygonPoints[0].x, polygonPoints[0].y);
                        for (j = 1; j < polygonPoints.length; j++) {
                            this.ctx.lineTo(polygonPoints[j].x, polygonPoints[j].y);
                        }
                    }
                }
                break;
            case 'multipolygon':
                if (coords.length > 0) {
                    for (k = 0; k < coords.length; k++) {
                        for (i = 0; i < coords[k].length; i++) {
                            polygonPoints = coords[k][i].concat();
                            if (i > 0) {
                                polygonPoints.reverse();
                            }
                            this.ctx.moveTo(polygonPoints[0].x, polygonPoints[0].y);
                            for (j = 1; j < polygonPoints.length; j++) {
                                this.ctx.lineTo(polygonPoints[j].x, polygonPoints[j].y);
                            }
                            this.ctx.closePath();
                        }
                    }
                }
                break;
            case 'circle':
                if (!geometry.projectedCenter) {
                    return;
                }
                var radius = geometry.projectedCenter.x - geometry.porjectedStartPoint.x;
                this.ctx.arc(geometry.projectedCenter.x, geometry.projectedCenter.y, radius, 0, 2 * Math.PI);
                this.ctx.stroke();
                break;
            }
            if (style.fill && !geometry.noneFillGeometry) {
                this.ctx.fill();
            }
            if (style.stroke) {
                this.ctx.stroke();
            }
        },
        dispose: function (geometry) {
            this.draw(geometry);
        }
    };    // Export
    // Export
    exports.CanvasContext = CanvasContext;
}(T));    // > VmlContext.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > VmlContext class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50, continue: true */
/*global window, T, document */
// > VmlContext.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > VmlContext class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50, continue: true */
/*global window, T, document */
(function (exports) {
    var VmlContext = function (vml, options) {
        this.options = T.Util.defaults(options, this.options);
        this.$vml = vml;
    };
    VmlContext.prototype = {
        initContainer: function (geometry) {
            if (!geometry.$container) {
                geometry.$container = T.DomUtil.createVmlElement('shape');
                var path = T.DomUtil.createVmlElement('path');
                geometry.$container.appendChild(path);
                geometry.$container.$path = path;
                geometry.$container.style.width = '1px';
                geometry.$container.style.height = '1px';
                geometry.$container.className = 'tibco-vml';
                geometry.$container.$path.className = 'tibco-vml';
            }
            geometry.$container.coordsize = '1 1';
            T.DomEventUtil.on(geometry.$container, 'click', function (e) {
                if (geometry.layer !== null) {
                    geometry.layer.onGeometryClick(e, geometry);
                }
            }, this);
            this.$vml.appendChild(geometry.$container);
        },
        applyStyle: function (geometry) {
            var style = geometry.style;
            if (style.stroke) {
                if (!geometry.$stroke) {
                    geometry.$stroke = T.DomUtil.createVmlElement('stroke');
                    geometry.$stroke.className = 'tibco-vml';
                    geometry.$container.appendChild(geometry.$stroke);
                }
                geometry.$stroke.color = style.color;
                geometry.$stroke.weight = style.weight;
                geometry.$stroke.opacity = style.opacity;
                geometry.$container.stroked = 't';
            } else {
                if (geometry.$stroke) {
                    geometry.$container.removeChild(geometry.$stroke);
                }
                geometry.$container.stroked = 'f';
            }
            if (style.fill && !geometry.noneFillGeometry) {
                if (!geometry.$fill) {
                    geometry.$fill = T.DomUtil.createVmlElement('fill');
                    geometry.$fill.className = 'tibco-vml';
                    geometry.$container.appendChild(geometry.$fill);
                }
                geometry.$fill.color = style.fillColor || style.color;
                geometry.$fill.opacity = style.fillOpacity;
                geometry.$container.filled = 't';
            } else {
                if (geometry.$fill) {
                    geometry.$container.removeChild(geometry.$fill);
                }
                geometry.$container.filled = 'f';
            }
        },
        getPathString: function (geometry) {
            var coords = geometry.projectedCoords;
            var i, j, k, path;
            if (geometry.type !== 'circle' && coords.length === 0) {
                return;
            }
            switch (geometry.type) {
            case 'polyline':
                path = 'M ' + Math.round(coords[0].x) + ' ' + Math.round(coords[0].y);
                for (i = 1; i < coords.length; i++) {
                    path += 'L ' + Math.round(coords[i].x) + ' ' + Math.round(coords[i].y);
                }
                return path;
            case 'polygon':
            case 'rectangle':
                for (i = 0; i < coords.length; i++) {
                    for (j = 0; j < coords[i].length; j++) {
                        if (j === 0) {
                            path += 'M ' + coords[i][0].x + ' ' + coords[i][0].y;
                            continue;
                        }
                        path += 'L ' + coords[i][j].x + ' ' + coords[i][j].y;
                    }
                    path += ' X ';
                }
                return path;
            case 'multipolyline':
                for (i = 0; i < coords.length; i++) {
                    for (j = 0; j < coords[i].length; j++) {
                        if (j === 0) {
                            path += 'M ' + coords[i][0].x + ' ' + coords[i][0].y;
                            continue;
                        }
                        path += 'L ' + coords[i][j].x + ' ' + coords[i][j].y;
                    }
                }
                return path;
            case 'multipolygon':
                for (k = 0; k < coords.length; k++) {
                    for (i = 0; i < coords[k].length; i++) {
                        for (j = 0; j < coords[k][i].length; j++) {
                            if (j === 0) {
                                path += 'M ' + coords[k][i][0].x + ' ' + coords[k][i][0].y;
                                continue;
                            }
                            path += 'L ' + coords[k][i][j].x + ' ' + coords[k][i][j].y;
                        }
                        path += ' X ';
                    }
                }
                return path;
            case 'circle':
                if (!geometry.projectedCenter) {
                    return;
                }
                var radius = geometry.projectedCenter.x - geometry.porjectedStartPoint.x;
                path = 'AL ' + Math.round(geometry.projectedCenter.x) + ',' + Math.round(geometry.projectedCenter.y) + ' ' + Math.round(radius) + ',' + Math.round(radius) + ' 0,' + 65535 * 360;
                return path;
            }
        },
        draw: function (geometry) {
            geometry.$container.style.display = 'none';
            geometry.$container.$path.v = this.getPathString(geometry) + ' ';
            geometry.$container.style.display = '';
        },
        dispose: function (geometry) {
            this.$vml.removeChild(geometry.$container);
        }
    };    // Export
    // Export
    exports.VmlContext = VmlContext;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * The class from which all layer classes inherit
         *
         * @abstract true
         * @constructor true
         * @summary The BaseLayer constructor
         *
         * @event layer-show The layer's visibility was changed to visible
         * @event layer-hide The layer's visibility was changed to hidden
         *
         * @option {String} name The name of the layer (default: `''`)
         *
         * @param {Object} [options](#options) The options of the layer
         */
    var BaseLayer = function (options) {
        this.options = T.Util.defaults(options, this.options);
        this.name = this.options.name || '';
        this.events = new T.EventDispatcher();
    };
    BaseLayer.prototype = {
        /* Override */
        onAdd: function () {
        },
        /* Override */
        onRemove: function () {
            T.DomUtil.remove(this.getContainer());
            this.events.detachAll();
            this.$container = null;
        },
        /* Override */
        reset: function () {
        },
        /* Override */
        /**
             * Returns the layer's index in the stack of layers
             *
             * @return {Number} The layer's index
             */
        getIndex: function () {
            if (!this.map) {
                return -1;
            }
            return this.map.getLayerIndex(this);
        },
        /* Override */
        getContainer: function () {
            return this.$container;
        },
        /* Override */
        /**
             * Sets the visibility of the layer to visible or hidden
             *
             * @param {Boolean} visible If `true`, the the layer will be visible; if `false`, it will be hidden
             */
        setVisible: function (visible) {
            if (visible === true) {
                //this.getContainer().style.visibility = 'visible';
                this.getContainer().style.display = 'block';
                this.map.events.fire('layer-show');
            } else if (visible === false) {
                //this.getContainer().style.visibility = 'hidden';
                this.getContainer().style.display = 'none';
                this.map.events.fire('layer-hide');
            }
        },
        /* Override */
        /**
             * Returns true if the layer is visible and false otherwise
             *
             * @return {Boolean} If `true`, the layer is visible; if `false`, the layer is hidden
             */
        isVisible: function () {
            //return (this.getContainer().style.visibility !== 'hidden' ? true : false);
            return window.getComputedStyle(this.getContainer()).display !== 'none' ? true : false;
        }
    };    // Export
    // Export
    exports.BaseLayer = BaseLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var base = T.BaseLayer;    /**
         * The class responsible for adding tiles from a tile server on the map.
         *
         * @constructor true
         * @summary TileLayer's constructor
         * @inherits {BaseLayer}
         *
         * @param {String} url The URL to the tile server containing z, x, y coordinates
         * @param {Object} [options](#options) The tile options
         *
         * @event tile-load A tile was loaded
         * @event tiles-loaded All tiles were loaded
         * @event tile-error A tile could not be loaded
         *
         * @option {Number} tileSize The width or height of the tiles in pixels (default: `256`)
         * @option {Number} errorTileAttemptsThreshold The number of times to try to load a tile before firing `tile-error` (default: `1`)
         * @option {Boolean} reuseTiles If `true`, reuses the cached tiles (default: `true`)
         * @option {Number} opacity The opacity of the layer with values from `0` to `1` (default: `1`)
         * @option {Boolean} autoLoadTiles If `true`, reloads the tiles when `update` method is called (default: `true`)
         * @option {String} tile404 The path to the `404 error` tile (default: `'../assets/404-tile.png'`)
         */
    /**
         * The class responsible for adding tiles from a tile server on the map.
         *
         * @constructor true
         * @summary TileLayer's constructor
         * @inherits {BaseLayer}
         *
         * @param {String} url The URL to the tile server containing z, x, y coordinates
         * @param {Object} [options](#options) The tile options
         *
         * @event tile-load A tile was loaded
         * @event tiles-loaded All tiles were loaded
         * @event tile-error A tile could not be loaded
         *
         * @option {Number} tileSize The width or height of the tiles in pixels (default: `256`)
         * @option {Number} errorTileAttemptsThreshold The number of times to try to load a tile before firing `tile-error` (default: `1`)
         * @option {Boolean} reuseTiles If `true`, reuses the cached tiles (default: `true`)
         * @option {Number} opacity The opacity of the layer with values from `0` to `1` (default: `1`)
         * @option {Boolean} autoLoadTiles If `true`, reloads the tiles when `update` method is called (default: `true`)
         * @option {String} tile404 The path to the `404 error` tile (default: `'../assets/404-tile.png'`)
         */
    var TileLayer = function (url, options) {
        if (!url) {
            console.log('TileLayer must have an URL');
        }    // Call super class constructor
        // Call super class constructor
        T.BaseLayer.call(this, options);
        this.url = url;
        this.tilesToLoad = 0;
        this.delayedUpdate = T.Util.limitExecByInterval(this.move, 200, this);
    };
    TileLayer.prototype = {
        options: {
            tileSize: 256,
            errorTileAttemptsThreshold: 1,
            reuseTiles: true,
            opacity: 1,
            autoLoadTiles: true,
            tile404: 'https://geoanalytics.tibco.com/404.png',
            attribution: null
        },
        initContainer: function () {
            if (!this.$container) {
                this.$layerContainer = T.DomUtil.create('div', 'tibco-layer tile-layer', this.map.$container.layers);
                this.$container = T.DomUtil.create('div', 'tibco-layer tile-layer-1', this.$layerContainer);
                this.$bufferContainer = T.DomUtil.create('div', 'tibco-layer tile-layer-2', this.$layerContainer);
                this.$layerContainer.style.position = 'relative';
                this.$container.style.position = 'absolute';
                this.$bufferContainer.style.position = 'absolute';
                this.$container.style.visibility = 'visible';
                this.$bufferContainer.style.visibility = 'visible';
                this.$container.layer = this;
                this.$bufferContainer.layer = this;
            }
        },
        // ## Reset layer ##
        // This will remove all cached tiles and remove all DOM elements.
        reset: function () {
            this.tiles = {};
            this.tilesToLoad = 0;
            this.unusedTiles = [];
            this.handleBackBuffer();
            this.update();    // Set location in URL
            // Set location in URL
            this.map.setUrlLocation();
        },
        move: function () {
            if (this.$container.style.visibility === 'hidden') {
                this.$container.style.visibility = 'visible';
                this.$container.style.zIndex = 0;
                this.$bufferContainer.style.zIndex = -1;
            }
            this.update();
        },
        moveEnd: function () {
            this.move();    // Set location in URL
            // Set location in URL
            this.map.setUrlLocation();
        },
        // ## Add a tile
        //
        // The process of adding a tile is:
        // > - Set tile position
        // > - Cache tile
        // > - Load tile if necessary
        // > - Add tile to main container
        addTile: function (point, $container) {
            var position = this.getTilePosition(point);
            var tile = this.getTile();
            T.DomUtil.position(tile, position);
            this.storeTile(tile, point);
            this.loadTile(tile, point);
            $container.appendChild(tile);
        },
        // ## Update layer
        // Updates layer
        update: function () {
            if (!this.map) {
                return;
            }
            if (this.options.autoLoadTiles === true) {
                this.reloadTiles();
            }
        },
        reloadTiles: function () {
            var zoom = this.map.getZoom(), bounds = this.map.getPixelBounds(), tileSize = this.options.tileSize;
            var tileBounds = new T.Bounds(bounds.min.divideBy(tileSize).floor(), bounds.max.divideBy(tileSize).floor());
            this.tilesLoaded = 0;    //tileBounds.extend(tileBounds.min.add(new T.Point(-1, -1)));
            //tileBounds.extend(tileBounds.max.add(new T.Point(1, 1)));
            //tileBounds.extend(tileBounds.min.add(new T.Point(-1, -1)));
            //tileBounds.extend(tileBounds.max.add(new T.Point(1, 1)));
            this.addTileFromBounds(tileBounds);
            this.removeInvisibleTiles(tileBounds);
        },
        handleBackBuffer: function () {
            if (!this.IsBackbufferEnabled()) {
                T.DomUtil.html(this.$container, '');
                return;
            }
            var frontPercent = this.getLoadedTilesPercent(this.$container);
            var backPercent = this.getLoadedTilesPercent(this.$bufferContainer);    // Switch  buffers
            // Switch  buffers
            if (frontPercent > 0.5 && backPercent < 0.5) {
                var temp = this.$bufferContainer;
                this.$bufferContainer = this.$container;
                this.$container = temp;
                this.$container.style.visibility = 'hidden';
            }    // Prepare container for loading tiles
            //this.stopLoadingTiles(this.$bufferContainer);
            // Prepare container for loading tiles
            //this.stopLoadingTiles(this.$bufferContainer);
            this.stopLoadingTiles(this.$container);
            T.DomUtil.html(this.$container, '');
            this.$bufferContainer.style.visibility = 'visible';    // Apply temporary transformation to visible container
            // Apply temporary transformation to visible container
            var matrix = this.map.lastTransform.matrix;
            if (!this.$bufferContainer.matrix) {
                this.$bufferContainer.matrix = new T.Matrix2D();
            }
            this.$bufferContainer.matrix = matrix.multiplyBy(this.$bufferContainer.matrix);
            T.DomUtil.position(this.$bufferContainer, new T.Point(0, 0));
            T.AnimUtil.setMatrix(this.$bufferContainer, this.$bufferContainer.matrix.toString());
            T.AnimUtil.setMatrix(this.$container, new T.Matrix2D());
        },
        /**
             * Sets a new layer opacity
             *
             * @param {Number} opacity The new opacity of the layer. Values can be from `0` to `1`
             */
        setOpacity: function (opacity) {
            this.options.opacity = opacity;
            var key, tiles = this.tiles;
            for (key in tiles) {
                if (tiles.hasOwnProperty(key)) {
                    T.DomUtil.setOpacity(tiles[key], this.options.opacity);
                }
            }
        },
        // ## Add tiles from center
        addTileFromBounds: function (tileBounds) {
            var queue = [];
            var j, i, point;
            var center = tileBounds.center();
            for (j = tileBounds.min.y; j <= tileBounds.max.y; j++) {
                for (i = tileBounds.min.x; i <= tileBounds.max.x; i++) {
                    point = new T.Point(i, j);
                    if (this.shouldTileBeLoaded(point)) {
                        queue.push(point);
                    }
                }
            }
            if (queue.length === 0) {
                return;
            }
            queue.sort(function (a, b) {
                return a.distanceTo(center) - b.distanceTo(center);
            });
            this.tilesToLoad = queue.length;
            var fragment = document.createDocumentFragment();
            for (i = 0; i < queue.length; i++) {
                this.addTile(queue[i], fragment);
            }
            this.$container.appendChild(fragment);
        },
        // ## Remove invisible tiles
        // Remove all tiles that are no longer in the map viewport
        removeInvisibleTiles: function (tileBounds) {
            var x, y, key;
            for (key in this.tiles) {
                if (this.tiles.hasOwnProperty(key)) {
                    var tmp = key.split(':');
                    x = parseInt(tmp[0], 10);
                    y = parseInt(tmp[1], 10);
                    if (x < tileBounds.min.x || x > tileBounds.max.x || y < tileBounds.min.y || y > tileBounds.max.y) {
                        this.removeTile(key);
                    }
                }
            }
        },
        // ## Remove tile
        // This function removes a tile from cache
        removeTile: function (key) {
            var tile = this.tiles[key];
            if (this.options.reuseTiles) {
                T.DomUtil.removeClass(tile, 'tibco-tile-loaded');
                this.unusedTiles.push(tile);
            }
            if (!this.options.reuseTiles) {
                tile.parentNode.removeChild(tile);
            } else {
                if (!T.Util.browserVersion.ielt9) {
                    tile.onerror = null;
                    tile.onload = null;    //tile.src = "";
                    //tile.src = "";
                    tile.removeAttribute('src');    //tile.style.visibility = "hidden";
                }
            }
            delete this.tiles[key];
        },
        // ## Get Tile ##
        // Get unused tile or create a new tile.
        getTile: function () {
            if (this.options.reuseTiles && this.unusedTiles.length > 0) {
                var tile = this.unusedTiles.pop();
                return tile;
            }
            return this.createTile();
        },
        getTileUrl: function (point) {
            /* If the tiles position exceeds boundaries for specific zoom recalculate it */
            var tileCount = Math.pow(2, this.map.getZoom());
            var x = point.x;
            var y = point.y;
            if (this.map.options.repeatX) {
                x = x >= 0 ? x % tileCount : (tileCount + x % tileCount) % tileCount;
            }
            if (x >= 0 && x < tileCount && y >= 0 && y < tileCount) {
                return T.Util.template(this.url, {
                    x: x,
                    y: y,
                    z: point.z
                });
            }
            return '';
        },
        getTilePosition: function (point) {
            var tileSize = this.options.tileSize, topLeft = this.map.getCenterInPixels().substract(this.map.getSize().divideBy(2)).floor();
            return point.multiplyBy(tileSize).substract(topLeft);
        },
        // ## Create tile ##
        // Creates a new IMG dom element
        createTile: function () {
            var tile = T.DomUtil.create('img', 'tibco-tile');
            tile.style.width = tile.style.height = this.options.tileSize + 'px';
            tile.style.position = 'absolute';
            tile.errorAttempts = 0;
            T.DomUtil.setOpacity(tile, this.options.opacity);
            T.DomUtil.disableSelection(tile);
            T.DomUtil.show(tile);
            return tile;
        },
        storeTile: function (tile, point) {
            this.tiles[point.x + ':' + point.y + ':' + point.z] = tile;
        },
        // ## Load a tile
        // This functions loads a tile for an x, y, z
        loadTile: function (tile, point) {
            point.z = this.map.getZoom();
            tile.layer = this;
            tile.point = point;
            var src = this.getTileUrl(point);
            if (src && src !== '') {
                if (!tile.onload) {
                    tile.onload = this.onTileLoad;
                }
                if (!tile.onerror) {
                    tile.onerror = this.onTileError;
                }
                tile.src = src;    //+ "&" + Math.random();
                //+ "&" + Math.random();
                if (T.Util.browserVersion.ielt9) {
                    T.DomUtil.show(tile);
                }
            } else {
                if (T.Util.browserVersion.ielt9) {
                    T.DomUtil.hide(tile);
                }
                this.tilesToLoad--;
            }
            if (!T.Util.browserVersion.ielt9) {
                T.DomUtil.hide(tile);
            }
        },
        shouldTileBeLoaded: function (point) {
            var tileKey = point.x + ':' + point.y + ':' + point.z;
            if (typeof this.tiles[tileKey] !== 'undefined') {
                return false;
            }
            return true;
        },
        /* This function is obsolete since BaseLayer already implements this */
        getIndex: function () {
            return this.map.getLayerIndex(this);
        },
        getContainer: function () {
            return this.$layerContainer;
        },
        /* Override */
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('hard-reset', function () {
                this.hardReset = true;
            }, this);
            this.map.events.on('move-end', this.moveEnd, this);
            this.map.events.on('move', this.delayedUpdate, this);
            this.map.events.on('view-reset', this.reset, this);
            this.initContainer();
            this.reset();    //this.update();
        },
        // Needs more work
        onRemove: function () {
            base.prototype.onRemove.call(this);
            this.map.events.detach('move-end', this.moveEnd);
            this.map.events.detach('view-reset', this.reset);
            this.map.events.detach('move', this.delayedUpdate);
        },
        onTileLoad: function () {
            var layer = this.layer;
            layer.events.fire('tile-load', { tile: this });
            T.DomUtil.addClass(this, 'tibco-tile-loaded');
            layer.tilesLoaded++;
            if (this.src && this.src !== '') {
                T.DomUtil.show(this);
            }
            if (layer.tilesToLoad === layer.tilesLoaded) {
                layer.onAllTilesLoaded();
                layer.events.fire('tiles-loaded', { nrTiles: layer.tilesLoaded });
            }
        },
        onTileError: function (e) {
            var layer = this.layer;
            if (this.errorAttempts < this.layer.options.errorTileAttemptsThreshold) {
                this.errorAttempts++;
                this.layer.loadTile(this, this.point);
            } else {
                this.layer.tilesLoaded++;
                this.layer.events.fire('tile-error', { tile: this });    // Add 404 tile
                // Add 404 tile
                this.onerror = null;
                this.src = this.layer.options.tile404;
            }
            if (layer.tilesToLoad === layer.tilesLoaded) {
                layer.onAllTilesLoaded();
                layer.events.fire('tiles-loaded', { nrTiles: layer.tilesLoaded });
            }
        },
        IsBackbufferEnabled: function () {
            if (T.AnimUtil.canAnim()) {
                if (this.hardReset) {
                    this.hardReset = false;
                    return false;
                }
            }
            return true;
        },
        onAllTilesLoaded: function () {
            if (!this.IsBackbufferEnabled()) {
                return;
            }
            var self = this;
            self.$container.style.zIndex = 0;
            self.$bufferContainer.style.zIndex = -1;
            self.$container.style.visibility = 'visible';
            self.$bufferContainer.style.visibility = 'hidden';
            T.DomUtil.html(self.$bufferContainer, '');
            self.$bufferContainer.matrix = new T.Matrix2D();
        },
        stopLoadingTiles: function (container) {
            var tiles = T.DomUtil.nodeCollectionToArray(container.getElementsByTagName('img')),
                // Array.prototype.slice.call(container.getElementsByTagName('img')),
                i, len, tile;
            for (i = 0, len = tiles.length; i < len; i++) {
                tile = tiles[i];
                if (!tile.complete) {
                    tile.onload = null;
                    tile.onerror = null;
                    tile.removeAttribute('src');
                    tile.parentNode.removeChild(tile);    //tile.src = null;
                }
            }
        },
        getLoadedTilesPercent: function (container) {
            var tiles = container.getElementsByTagName('img'), i, j = 0, len = tiles.length, tile;
            if (len === 0) {
                return 0;
            }
            for (i = 0; i < len; i++) {
                if (tiles[i].complete) {
                    j++;
                }
            }
            return j / len;
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(TileLayer, base);    // Export
    // Export
    exports.TileLayer = TileLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * ImageLayer class is responsible for adding images on the map
         *
         * @inherits {BaseLayer}
         * @summary ImageLayer's constructor
         * @constructor true
         *
         * @option {Number} opacity The opacity of the layer. Values from `0` to `1` (Default: `1`)
         *
         * @event image-load The image was loaded
         * @event image-loading The image is currently loading
         * @event image-error The image couldn't be loaded
         *
         * @param {String} url The URL to the image
         * @param {LatLngBounds} bounds The bounds on which the image will be displayed
         * @param {Object} [options](#options) The ImageLayer's options
         */
    var ImageLayer = function (url, bounds, options) {
        // if (!url) {
        // throw new Error('ImageLayer must have an URL');
        // }
        this.options = T.Util.defaults(options, this.options);
        this.url = url;
        this.bounds = bounds;    // Call super class constructor
        // Call super class constructor
        T.BaseLayer.call(this, options);
    };
    ImageLayer.prototype = {
        options: { opacity: 1 },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-layer', this.map.$container.layers);
                this.$container.layer = this;
            }
        },
        initImage: function () {
            var image = this.image = T.DomUtil.create('img', 'tibco-image-1');
            var backImage = this.backImage = T.DomUtil.create('img', 'tibco-image-2');
            image.onload = this.onImageLoad;
            image.onerror = this.onImageError;
            if (this.url) {
                image.src = this.url;
            }
            image.layer = this;
            image.style.position = 'absolute';
            image.style.opacity = this.options.opacity;
            image.matrix = new T.Matrix2D();
            backImage.layer = this;
            backImage.style.position = 'absolute';
            backImage.style.opacity = this.options.opacity;
            backImage.matrix = new T.Matrix2D();
            backImage.style.visibility = 'hidden';
            T.DomUtil.disableSelection(image);
            T.DomUtil.disableSelection(backImage);
        },
        reset: function () {
            var image = this.image, topLeft, size;
            if (this.bounds instanceof T.LatLngBounds) {
                topLeft = this.map.latLngToPoint(this.bounds.getNorthWest()).floor();
                size = this.map.latLngToPoint(this.bounds.getSouthEast()).substract(topLeft).floor();
            } else {
                topLeft = this.map.transform(this.bounds.topLeft()).substract(this.map.getTopLeftInPixels()).round();    // To round or to floor that is the question !
                // To round or to floor that is the question !
                size = this.map.transform(this.bounds.bottomRight()).substract(this.map.getTopLeftInPixels()).substract(topLeft).round();    // To round or to floor that is the question !
            }
            T.DomUtil.position(image, topLeft);
            image.style.width = size.x + 'px';
            image.style.height = size.y + 'px';
        },
        /**
             * Changes the layer's image and bounds
             *
             * @param {String} url The new URL of the image
             * @param {LatLngBounds} bounds The bounds on which the image will be displayed
             */
        reload: function (url, bounds) {
            this.url = url;
            this.bounds = bounds;
            if (!this.image) {
                this.initImage();
            }
            this.events.fire('image-loading', { tile: this.image });
            this.image.src = this.url;
            this.reset();
        },
        /**
             * Sets the layers opacity
             *
             * @param {Number} opacity The new opacity of the layer. Values from `0` to `1`
             */
        setOpacity: function (opacity) {
            if (!this.image) {
                this.initImage();
            }
            this.options.opacity = opacity;
            this.image.style.opacity = this.options.opacity;
        },
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.reset, this);
            this.initContainer();
            if (!this.image) {
                this.initImage();
            }
            this.$container.appendChild(this.image);
            this.$container.appendChild(this.backImage);
            this.reset();
        },
        onRemove: function (map) {
            T.ImageLayer.base.onRemove.call(this);
            this.map.events.detach('view-reset', this.reset);
        },
        onImageLoad: function () {
            var layer = this.layer;
            layer.events.fire('image-load', { tile: this });
        },
        onImageError: function (e) {
            var layer = this.layer;
            layer.events.fire('image-error', { tile: this });
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(ImageLayer, T.BaseLayer);    // Export
    // Export
    exports.ImageLayer = ImageLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var base = T.BaseLayer;    /**
         * MarkersLayer class is responsible for adding markers on the map
         *
         * @inherits {BaseLayer}
         * @summary MarkersLayer's constructor
         * @constructor true
         *
         * @option {Number} opacity The opacity of the layer. Values from `0` to `1` (Default: `1`)
         *
         * @event marker-click A marker was clicked
         * @event marker-drag-start The dragging of a marker has started
         * @event marker-drag A marker is currently dragged
         * @event marker-drag-end The dragging of a marker has stopped
         *
         * @param {Object} [options](#options) The MarkerLayer's options
         */
    /**
         * MarkersLayer class is responsible for adding markers on the map
         *
         * @inherits {BaseLayer}
         * @summary MarkersLayer's constructor
         * @constructor true
         *
         * @option {Number} opacity The opacity of the layer. Values from `0` to `1` (Default: `1`)
         *
         * @event marker-click A marker was clicked
         * @event marker-drag-start The dragging of a marker has started
         * @event marker-drag A marker is currently dragged
         * @event marker-drag-end The dragging of a marker has stopped
         *
         * @param {Object} [options](#options) The MarkerLayer's options
         */
    var MarkersLayer = function (options) {
        // Call super class constructor
        base.call(this, options);
        this.markers = [];
    };
    MarkersLayer.prototype = {
        options: { opacity: 1 },
        initContainer: function () {
            var i;
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-layer markers-layer', this.map.$container.layers);
                this.$container.layer = this;
                for (i = 0; i < this.markers.length; i++) {
                    this.$container.appendChild(this.markers[i].$container);
                }
            }
        },
        reset: function () {
            var i = 0;
            for (i = 0; i < this.markers.length; i++) {
                this.markers[i].reset();
            }
        },
        /**
             * Adds a marker to the markers layer
             *
             * @param {HtmlMarker|ImageMarker} marker The marker that will be added to the map
             */
        addMarker: function (marker) {
            marker.layer = this;
            marker.onAdd(this);
            marker.reset();
            if (this.$container) {
                this.$container.appendChild(marker.$container);
            }
            this.markers.push(marker);    // Add events
            // Add events
            T.DomEventUtil.on(marker.$container, 'mousedown', this.onMarkerClick, this);
        },
        /**
             * Removes a marker from the markers layer
             *
             * @param {HtmlMarker|ImageMarker} marker The marker that will be removed from the map
             */
        removeMarker: function (marker) {
            var index = this.markers.indexOf(marker);
            if (index > -1) {
                this.markers.splice(index, 1);
                marker.layer = null;
                marker.onRemove(this);
                if (this.$container) {
                    this.$container.removeChild(marker.$container);
                    T.DomEventUtil.off(marker.$container, 'mousedown', this.onMarkerClick, this);
                }
            }
        },
        /**
             * Removes all markers from the markers layer
             */
        removeAllMarkers: function () {
            var i = 0;
            for (i = 0; i < this.markers.length; i++) {
                this.markers[i].layer = null;
                this.markers[i].onRemove(this);
            }
            this.markers = [];
            T.DomUtil.html(this.$container, '');
        },
        /**
             * Sets the layers opacity
             *
             * @param {Number} opacity The new opacity of the layer. Values from `0` to `1`
             */
        setOpacity: function (opacity) {
            this.options.opacity = opacity;
            this.$container.style.opacity = this.options.opacity;
        },
        /* Override */
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.reset, this);
            this.initContainer();
            this.reset();
        },
        /* Override */
        onRemove: function () {
            base.prototype.onRemove.call(this);
            this.map.events.detach('view-reset', this.reset);
        },
        onMarkerClick: function (event) {
            var target = event.target || event.srcElement;
            this.events.fire('marker-click', target.marker);
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(MarkersLayer, base);    // Export
    // Export
    exports.MarkersLayer = MarkersLayer;
}(T));    // > SvgLayer.js
// > https://github.com/Maporama/TibcoMaps
// >
// > SvgLayer layer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
// > SvgLayer.js
// > https://github.com/Maporama/TibcoMaps
// >
// > SvgLayer layer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var SvgLayer = function (options) {
        // Call super class constructor
        T.BaseLayer.call(this, options);
        this.geometries = [];
        this.type = 'svg';
    };
    SvgLayer.prototype = {
        initContainer: function () {
            var i;
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-layer svg-layer', this.map.$container.layers);
                this.$svg = T.DomUtil.createSvgElement('svg');
                this.$svg.$g = T.DomUtil.createSvgElement('g');
                this.$svg.style.position = 'absolute';
                this.$container.appendChild(this.$svg);
                this.$svg.appendChild(this.$svg.$g);
                this.context = new T.SvgContext(this.$svg);    // Init geometires added before the layer was added to a map instance
                // Init geometires added before the layer was added to a map instance
                for (i = 0; i < this.geometries.length; i++) {
                    this.geometries[i].context = this.context;
                    this.geometries[i].initContainer();
                }
            }
        },
        reset: function () {
            var i;
            for (i = 0; i < this.geometries.length; i++) {
                this.geometries[i].reset();
            }
        },
        addGeometry: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (index > -1) {
                return;
            }
            geometry.onAdd(this);
            this.geometries.push(geometry);
            return this;
        },
        removeGeometry: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (index > -1) {
                this.geometries.splice(index, 1);
                geometry.onRemove(this);
            }
        },
        updateViewPort: function () {
            var padding = new T.Point(this.options.clipOffset, this.options.clipOffset);
            var delta = this.map.getLayersPos();
            var pos = delta.add(padding);
            var size = this.map.getSize().add(padding.multiplyBy(2));
            T.DomUtil.position(this.$svg, pos.multiplyBy(-1));
            this.$svg.setAttributeNS(null, 'width', size.x);
            this.$svg.setAttributeNS(null, 'height', size.y);
            this.$svg.setAttributeNS(null, 'viewBox', [
                -padding.x,
                -padding.y,
                size.x,
                size.y
            ].join(' '));
            this.$svg.$g.setAttributeNS(null, 'transform', 'matrix(' + [
                1,
                0,
                0,
                1,
                0,
                0
            ].join(' ') + ')');
            this.reset();
        },
        bringForward: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$svg.$g);
            if (index < 0) {
                return;
            }
            var nextNode = this.$svg.$g.childNodes[index + 2];
            if (nextNode) {
                this.$svg.$g.insertBefore(geometry.$container, nextNode);
            } else {
                this.$svg.$g.appendChild(geometry.$container);
            }
        },
        sendBackward: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$svg.$g);
            if (index < 0) {
                return;
            }
            var nextNode = this.$svg.$g.childNodes[index - 1];
            if (nextNode) {
                this.$svg.$g.insertBefore(geometry.$container, nextNode);
            }
        },
        bringToFront: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$svg.$g);
            if (index < 0) {
                return;
            }
            this.$svg.$g.appendChild(geometry.$container);
        },
        sendToBack: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$svg.$g);
            if (index < 0 || this.$svg.$g.firstChild === geometry.$container) {
                return;
            }
            this.$svg.$g.insertBefore(geometry.$container, this.$svg.$g.firstChild);
        },
        getContainer: function () {
            return this.$container;
        },
        /* Override */
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.updateViewPort, this);
            this.map.events.on('move-end', this.updateViewPort, this);
            this.initContainer();
            this.reset();
            this.updateViewPort();
        },
        /* Override */
        onRemove: function () {
            this.map.events.detach('view-reset', this.reset);
            this.map.events.detach('move-end', this.updateViewPort);
            this.map = null;
        },
        onGeometryClick: function (e, geometry) {
            this.events.fire('geometry-click', geometry);
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(SvgLayer, T.BaseLayer);    // Export
    // Export
    exports.SvgLayer = SvgLayer;
}(T));    // > CanvasLayer.js
// > https://github.com/Maporama/TibcoMaps
// >
// > CanvasLayer layer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
// > CanvasLayer.js
// > https://github.com/Maporama/TibcoMaps
// >
// > CanvasLayer layer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var CanvasLayer = function (options) {
        // Call super class constructor
        T.BaseLayer.call(this, options);
        this.type = 'canvas';
        this.geometries = [];
    };
    CanvasLayer.prototype = {
        initContainer: function () {
            var i;
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-layer canvas-layer', this.map.$container.layers);
                this.$canvas = T.DomUtil.create('canvas', 'canvas');
                this.$canvas.style.position = 'absolute';
                this.$container.appendChild(this.$canvas);    //Store the canvas context
                //Store the canvas context
                this.canvasContext = this.$canvas.getContext('2d');    //Store the  context specific for this layer ( a HTML 5 canvas drawing  context)
                //Store the  context specific for this layer ( a HTML 5 canvas drawing  context)
                this.context = new T.CanvasContext(this.$canvas);    //Handle canvas click events
                //Handle canvas click events
                T.DomEventUtil.on(this.$canvas, 'click', function (e) {
                    this.onGeometryClick(e);
                }, this);    // Init geometires added before the layer was added to a map instance
                // Init geometires added before the layer was added to a map instance
                for (i = 0; i < this.geometries.length; i++) {
                    this.geometries[i].context = this.context;
                    this.geometries[i].initContainer();
                }
            }
        },
        reset: function () {
            var i;
            for (i = 0; i < this.geometries.length; i++) {
                this.geometries[i].reset();
            }
        },
        addGeometry: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (index > -1) {
                return;
            }
            geometry.onAdd(this);
            this.geometries.push(geometry);
            return this;
        },
        removeGeometry: function (geometry) {
            var index = this.geometries.indexOf(geometry), i = 0;
            if (index > -1) {
                this.geometries.splice(index, 1);
                this.clear();
                for (i = 0; i < this.geometries.length; i++) {
                    this.geometries[i].draw(this.geometries[i]);
                }
            }
        },
        clear: function () {
            this.canvasContext.clearRect(0, 0, this.$canvas.width, this.$canvas.height);
        },
        bringForward: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (this.geometries.length < 2 || index === this.geometries.length - 1) {
                return;
            }
            var temp = this.geometries[index + 1];
            this.geometries[index + 1] = geometry;
            this.geometries[index] = temp;
            this.updateViewPort();
        },
        sendBackward: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (this.geometries.length < 2 || index === 0) {
                return;
            }
            var temp = this.geometries[index - 1];
            this.geometries[index - 1] = geometry;
            this.geometries[index] = temp;
            this.updateViewPort();
        },
        bringToFront: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (this.geometries.length < 2 || index === this.geometries.length - 1) {
                return;
            }
            this.geometries.splice(index, 1);
            this.geometries.push(geometry);
            this.updateViewPort();
        },
        sendToBack: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (this.geometries.length < 2 || index === 0) {
                return;
            }
            this.geometries.splice(index, 1);
            this.geometries.splice(0, 0, geometry);
            this.updateViewPort();
        },
        updateViewPort: function () {
            var padding = new T.Point(this.options.clipOffset, this.options.clipOffset);
            var pos = this.map.getLayersPos().add(padding);
            var size = this.map.getSize().add(padding.multiplyBy(2));
            T.DomUtil.position(this.$canvas, pos.multiplyBy(-1));
            this.$canvas.width = size.x;
            this.$canvas.height = size.y;    //Adjust padding
            //Adjust padding
            this.canvasContext.setTransform(1, 0, 0, 1, padding.x, padding.y);    // Clipping if necessary
            // Clipping if necessary
            this.reset();
        },
        /* Override */
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.updateViewPort, this);
            this.map.events.on('move-end', this.updateViewPort, this);
            this.initContainer();
            this.updateViewPort();
        },
        /* Override */
        onRemove: function () {
            this.map.events.detach('view-reset', this.updateViewPort);
            this.map.events.detach('move-end', this.updateViewPort);
            this.map = null;
        },
        onGeometryClick: function (e) {
            var i, geometry;
            var point = this.map.computeMousePosition(e);
            var geoms = this.geometries.concat().reverse();
            var padding = new T.Point(this.options.clipOffset, this.options.clipOffset);
            point = point.add(padding);
            for (i = 0; i < geoms.length; i++) {
                this.clear();
                geoms[i].reset();
                var img = this.canvasContext.getImageData(point.x, point.y, 1, 1).data;
                if (img[0] !== 0 || img[1] !== 0 || img[2] !== 0) {
                    geometry = geoms[i];
                    break;
                }
            }
            this.updateViewPort();
            if (geometry) {
                this.events.fire('geometry-click', geometry);
            }
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(CanvasLayer, T.BaseLayer);    // Export
    // Export
    exports.CanvasLayer = CanvasLayer;
}(T));    // > ImageLayer.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > SVG layer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
// > ImageLayer.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > SVG layer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var VmlLayer = function (options) {
        // Call super class constructor
        T.BaseLayer.call(this, options);
        this.type = 'vml';
        this.geometries = [];
    };
    VmlLayer.prototype = {
        initContainer: function () {
            var i;
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-layer vml-layer', this.map.$container.layers);
                this.$container.style.position = 'absolute';    //Add vml namespace
                //Add vml namespace
                document.namespaces.add('v', 'urn:schemas-microsoft-com:vml');    //Add vml css
                //Add vml css
                T.DomUtil.addStyleNode('.tibco-vml\t {\tdisplay:inline-block; behavior:url(#default#VML); position: absolute; }');
                this.$vml = this.$container;
                this.context = new T.VmlContext(this.$vml);    // Init geometires added before the layer was added to a map instance
                // Init geometires added before the layer was added to a map instance
                for (i = 0; i < this.geometries.length; i++) {
                    this.geometries[i].context = this.context;
                    this.geometries[i].initContainer();
                }
            }
        },
        reset: function () {
            var i;
            for (i = 0; i < this.geometries.length; i++) {
                this.geometries[i].reset();
            }
        },
        addGeometry: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (index > -1) {
                return;
            }
            geometry.onAdd(this);
            this.geometries.push(geometry);
            return this;
        },
        removeGeometry: function (geometry) {
            var index = this.geometries.indexOf(geometry);
            if (index > -1) {
                this.geometries.splice(index, 1);
                geometry.onRemove(this);
            }
        },
        updateViewPort: function () {
            var pos = this.map.getLayersPos();
            T.DomUtil.position(this.$vml, pos.multiplyBy(-1));
            this.reset();
        },
        bringForward: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$vml);
            if (index < 0) {
                return;
            }
            var nextNode = this.$vml.childNodes[index + 2];
            if (nextNode) {
                this.$vml.insertBefore(geometry.$container, nextNode);
            } else {
                this.$vml.appendChild(geometry.$container);
            }
        },
        sendBackward: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$vml);
            if (index < 0) {
                return;
            }
            var nextNode = this.$vml.childNodes[index - 1];
            if (nextNode) {
                this.$vml.insertBefore(geometry.$container, nextNode);
            }
        },
        bringToFront: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$vml);
            if (index < 0) {
                return;
            }
            this.$vml.appendChild(geometry.$container);
        },
        sendToBack: function (geometry) {
            var index = T.DomUtil.getNodeIndex(geometry.$container, this.$vml);
            if (index < 0 || this.$vml.firstChild === geometry.$container) {
                return;
            }
            this.$vml.insertBefore(geometry.$container, this.$vml.firstChild);
        },
        getContainer: function () {
            return this.$container;
        },
        /* Override */
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.reset, this);
            this.map.events.on('move-end', this.updateViewPort, this);
            if (T.Util.browserVersion.ielt9) {
                this.map.events.on('layer-moved', this.updateViewPort, this);
            }
            this.initContainer();
            this.updateViewPort();
        },
        /* Override */
        onRemove: function () {
            T.VmlLayer.base.onRemove.call(this);
            this.map.events.detach('view-reset', this.reset);
            this.map.events.detach('move-end', this.updateViewPort);
            if (T.Util.browserVersion.ielt9) {
                this.map.events.detach('layer-moved', this.updateViewPort);
            }
        },
        onGeometryClick: function (e, geometry) {
            this.events.fire('geometry-click', geometry);
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(VmlLayer, T.BaseLayer);    // Export
    // Export
    exports.VmlLayer = VmlLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var base = T.BaseLayer;    /**
         * The layer class responsible for adding vector shapes on the map
         *
         * @summary VectorLayer's constructor
         * @constructor true
         * @inherits {BaseLayer}
         *
         * @option {Number} clipOffset The clip offset (Default: `100`)
         * @option {Object} defaultStyle An object with different style properties used to draw the shapes on the map. Available properties are:
         *
         * - `stroke` (Boolean) If `true`, uses stroke when drawing shapes (Default: `true`)
         * - `color` (CSSColor) The color of the shape (Default: `'#0033ff'`)
         * - `dashArray` (SVGDashArray) The dash array pattern of the stroke of the shape (Default: `null`)
         * - `lineCap` The line cap to use when joining elements: `butt|round|square` (Default: `null`)
         * - `lineJoin` The line join to use for elements: `bevel|round|miter` (Default: `null`)
         * - `miterLimit`  Maximum miter length when using `miter` in `lineJoin`
         * - `weight` (Number) The weight of the stroke (Default: `2`)
         * - `opacity` (Number) The opacity of the shape (Default: `0.5`)
         * - `fill` (Bollean) If `true`, fills the shape (Default: `true`)
         * - `fillColor` (CSSColor) The color of the fill (Default: `null`)
         * - `fillOpacity` (CSSColor) The opacity of the fill (Default: `0.2`)
         *
         *
         * @param {Object} [options](#options) The VectorLayer options
         */
    /**
         * The layer class responsible for adding vector shapes on the map
         *
         * @summary VectorLayer's constructor
         * @constructor true
         * @inherits {BaseLayer}
         *
         * @option {Number} clipOffset The clip offset (Default: `100`)
         * @option {Object} defaultStyle An object with different style properties used to draw the shapes on the map. Available properties are:
         *
         * - `stroke` (Boolean) If `true`, uses stroke when drawing shapes (Default: `true`)
         * - `color` (CSSColor) The color of the shape (Default: `'#0033ff'`)
         * - `dashArray` (SVGDashArray) The dash array pattern of the stroke of the shape (Default: `null`)
         * - `lineCap` The line cap to use when joining elements: `butt|round|square` (Default: `null`)
         * - `lineJoin` The line join to use for elements: `bevel|round|miter` (Default: `null`)
         * - `miterLimit`  Maximum miter length when using `miter` in `lineJoin`
         * - `weight` (Number) The weight of the stroke (Default: `2`)
         * - `opacity` (Number) The opacity of the shape (Default: `0.5`)
         * - `fill` (Bollean) If `true`, fills the shape (Default: `true`)
         * - `fillColor` (CSSColor) The color of the fill (Default: `null`)
         * - `fillOpacity` (CSSColor) The opacity of the fill (Default: `0.2`)
         *
         *
         * @param {Object} [options](#options) The VectorLayer options
         */
    var VectorLayer = function (options) {
        var i;    // Call super class constructor
        // Call super class constructor
        base.call(this, options);
        if (T.Util.browserVersion.ielt9) {
            this.vectorDrawer = new T.VmlLayer(this.options);
        } else {
            if (options && options.useCanvas === true) {
                this.vectorDrawer = new T.CanvasLayer(this.options);
            } else {
                this.vectorDrawer = new T.SvgLayer(this.options);
            }
        }
        this.vectorDrawer.events = this.events;
    };
    VectorLayer.prototype = {
        options: {
            defaultStyle: {
                stroke: true,
                color: '#0033ff',
                dashArray: null,
                lineCap: null,
                lineJoin: null,
                weight: 2,
                opacity: 0.5,
                fill: true,
                fillColor: null,
                fillOpacity: 0.2
            },
            clipOffset: 100
        },
        reset: function () {
            this.vectorDrawer.reset();
        },
        /**
             * Adds a geometry to the vector layer
             *
             * @param {Circle|MultiPolygon|MultiPolyline|Path|Polygon|Polyline|Rectangle} geometry The geometry that will be added to the layer
             *
             * @return {CanvasLayer|SvgLayer|VmlLayer} The vector layer used to draw the shapes
             */
        addGeometry: function (geometry) {
            return this.vectorDrawer.addGeometry(geometry);
        },
        /**
             * Removes a geometry from the layer
             *
             * @param {Circle|MultiPolygon|MultiPolyline|Path|Polygon|Polyline|Rectangle} geometry The geometry that will be removed from the layer
             *
             * @return {CanvasLayer|SvgLayer|VmlLayer} The vector layer used to draw the shapes
             */
        removeGeometry: function (geometry) {
            return this.vectorDrawer.removeGeometry(geometry);
        },
        /**
             * Increases the geometry position in the stack of geometries by one
             *
             * @param {Circle|MultiPolygon|MultiPolyline|Path|Polygon|Polyline|Rectangle} geometry The geometry that will be moved
             */
        bringForward: function (geometry) {
            this.vectorDrawer.bringForward(geometry);
        },
        /**
             * Decreases the geometry position in the stack of geometries by one
             *
             * @param {Circle|MultiPolygon|MultiPolyline|Path|Polygon|Polyline|Rectangle} geometry The geometry that will be moved
             */
        sendBackward: function (geometry) {
            this.vectorDrawer.sendBackward(geometry);
        },
        /**
             * Brings the geometry to the front of all the other geometries
             *
             * @param {Circle|MultiPolygon|MultiPolyline|Path|Polygon|Polyline|Rectangle} geometry The geometry that will be moved
             */
        bringToFront: function (geometry) {
            this.vectorDrawer.bringToFront(geometry);
        },
        /**
             * Sends the geometry to the back of all the other geometries
             *
             * @param {Circle|MultiPolygon|MultiPolyline|Path|Polygon|Polyline|Rectangle} geometry The geometry that will be moved
             */
        sendToBack: function (geometry) {
            this.vectorDrawer.sendToBack(geometry);
        },
        updateViewPort: function () {
            this.vectorDrawer.updateViewPort();
        },
        /* Override */
        getIndex: function () {
            return this.vectorDrawer.map.getLayerIndex(this);
        },
        /* Override */
        getContainer: function () {
            return this.vectorDrawer.$container;
        },
        /* Override */
        setVisible: function (visible) {
            var container = this.getContainer();
            if (visible === true) {
                container.style.display = 'block';
            } else if (visible === false) {
                container.style.display = 'none';
            }
        },
        /* Override */
        isVisible: function () {
            var container = this.getContainer();
            return container.style.display === 'none' ? false : true;
        },
        /* Override */
        onAdd: function (map) {
            this.vectorDrawer.onAdd(map);
        },
        /* Override */
        onRemove: function (map) {
            base.prototype.onRemove.call(this);
            this.vectorDrawer.onRemove(map);
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(VectorLayer, base);    // Export
    // Export
    exports.VectorLayer = VectorLayer;
}(T));    // > GoogleLayer.js 0.1.2
// > https://github.com/Maporama/TibcoMaps
// >
// > GoogleLayer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document, google */
// > GoogleLayer.js 0.1.2
// > https://github.com/Maporama/TibcoMaps
// >
// > GoogleLayer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document, google */
(function (exports) {
    // ## Constructor
    //
    // The constructor takes 1 parameters, the options for layer.
    //
    // Simple example:
    //
    //		var layer1 = new T.GoogleLayer({opacity: 0.5});
    //
    var GoogleLayer = function (options) {
        // Call super class constructor
        T.BaseLayer.call(this, options);
    };
    GoogleLayer.prototype = {
        // The layer options are:
        // > - **opacity** - Layer opacity
        options: {
            opacity: 1,
            attribution: null
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-layer google-layer', this.map.$container.layers);
                this.$container.layer = this;
                T.DomUtil.size(this.$container, this.map.getSize());
                this.mapObject = new google.maps.Map(this.$container, {
                    center: this.toGoogle(this.map.getCenter()),
                    zoom: this.map.getZoom(),
                    type: google.maps.MapTypeId.ROADMAP,
                    disableDefaultUI: true,
                    keyboardShortcuts: false,
                    draggable: false,
                    disableDoubleClickZoom: false,
                    scrollwheel: false,
                    streetViewControl: false
                });
            }
        },
        // ## Reset layer ##
        reset: function () {
            var container = this.map.$layers;
            google.maps.event.addListenerOnce(this.mapObject, 'idle', function () {
                container.style.visibility = '';
            });
            container.style.visibility = 'hidden';
            this.mapObject.setOptions({
                animatedZoom: false,
                center: this.toGoogle(this.map.getCenter()),
                zoom: this.map.getZoom()
            });    // Set location in URL
            // Set location in URL
            this.map.setUrlLocation();
        },
        // ## Update layer
        // Updates layer
        update: function () {
            if (!this.map) {
                return;
            }    // Set location in URL
            // Set location in URL
            this.map.setUrlLocation();
        },
        updatePosition: function () {
            if (!this.map) {
                return;
            }
            var layerPosition = this.map.getLayersPos().multiplyBy(-1);
            T.DomUtil.position(this.$container, layerPosition);
            this.mapObject.setCenter(this.toGoogle(this.map.getCenter()));
            this.resizeMap();
        },
        resizeMap: function () {
            google.maps.event.trigger(this.mapObject, 'resize');
        },
        toGoogle: function (latLng) {
            return new google.maps.LatLng(latLng.lat, latLng.lng);
        },
        // ## Set layer opacity
        // Change layer opacity. The value can be from 0 to 1.
        setOpacity: function (opacity) {
            this.options.opacity = opacity;
        },
        /* Override */
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('move-end', this.update, this);
            this.map.events.on('move', this.updatePosition, this);
            this.map.events.on('view-reset', this.reset, this);
            this.initContainer();
            this.reset();
            this.update();
        },
        /* Override */
        onRemove: function () {
            T.GoogleLayer.base.onRemove.call(this);
            this.map.events.detach('move-end', this.update);
            this.map.events.detach('move', this.updatePosition);
            this.map.events.detach('view-reset', this.reset);
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(GoogleLayer, T.BaseLayer);    // Export
    // Export
    exports.GoogleLayer = GoogleLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T */
(function (exports) {
    var base = T.TileLayer;    /**
         * The class responsible for adding tiles of a specific type and style to the map. Adds the TIBCO copyright prefix to the attribution control by default.
         *
         * @constructor true
         * @summary TibcoLayer's constructor
         * @inherits {TileLayer}
         *
         * @param {Object} [options](#options) The layer options
         *
         * @option {MapTypes} mapType The map tiles type (default: STANDARD)
         * @option {MapStyles} mapStyle The map tiles style (default: LIGHT)
         *
         * @event attribution-update Updates the attribution control options with layer attribution options.
         * @event layer-attribution-reset Resets the attribution control.
         *
         * @example examples/docs/layers/tibco-layer 100% 500
         */
    /**
         * The class responsible for adding tiles of a specific type and style to the map. Adds the TIBCO copyright prefix to the attribution control by default.
         *
         * @constructor true
         * @summary TibcoLayer's constructor
         * @inherits {TileLayer}
         *
         * @param {Object} [options](#options) The layer options
         *
         * @option {MapTypes} mapType The map tiles type (default: STANDARD)
         * @option {MapStyles} mapStyle The map tiles style (default: LIGHT)
         *
         * @event attribution-update Updates the attribution control options with layer attribution options.
         * @event layer-attribution-reset Resets the attribution control.
         *
         * @example examples/docs/layers/tibco-layer 100% 500
         */
    var TibcoLayer = function (options) {
        // Init
        if (window.location.protocol != 'https:') {
            this.mapServicesUrl = 'http://geoanalytics.tibco.com/service/services-list.json';    // Default, HARDCODED!
        } else {
            this.mapServicesUrl = 'https://geoanalytics.tibco.com/service/ssl/services-list.json';    // Default, HARDCODED!
        }
        this.mapServices = null;    // all available map services for all map types
        // all available map services for all map types
        this.mapTypeService = null;    // map type specific service data
        // map type specific service data
        this.mapTypeServiceData = null;    // tile data (inlcuding the tiles url) for the specific map type
        // tile data (inlcuding the tiles url) for the specific map type
        this.options = T.Util.defaults(this.options, base.prototype.options);
        T.TileLayer.call(this, '', options);    // Init layer
        // Init layer
        this.init(options);
    };
    TibcoLayer.prototype = {
        options: {
            mapType: T.MapTypes.STANDARD,
            mapStyle: T.MapStyles.LIGHT,
            attribution: {
                prefix: '<a target=\'_blank\' rel=\'noopener noreferrer\' href=\'http://www.tibco.com/assets/blt59b361e6644c8d3a/tibco-end-user-license-agreement_tcm8-9378.pdf\'>\xA9TIBCO GeoAnalytics</a>',
                text: ''
            }
        },
        /*
             * Initialize layer: fetch all map services and set map type and map style
             */
        init: function () {
            var self = this;    // Get map services data
            // Get map services data
            this.makeRequest(this.mapServicesUrl).then(function (data) {
                self.mapServices = self.getMapServices(data);
                self.setMapType(self.options.mapType);
            }, function () {
                throw new Error('Map services error!');
            });
        },
        /*
             * Makes an Ajax call to the specified URL
             */
        makeRequest: function (url) {
            var promise = new T.Promise();
            var cfg = {
                method: 'GET',
                withCredentials: false,
                on: {
                    success: function (data) {
                        var json = JSON.parse(data);
                        promise.resolve(json);
                    },
                    error: function (error) {
                        promise.reject(error);
                    }
                }
            };    // Execute xhr
            // Execute xhr
            new T.Ajax().send(url, cfg);
            return promise;
        },
        /*
             * Sets specified map type and fetches map type - related styles and sets the layer's specified style
             */
        setMapType: function (mapType) {
            var self = this, promise = new T.Promise();
            if (!mapType) {
                throw new Error('Please specify a map type!');
            }    // Save map type
            // Save map type
            this.options.mapType = mapType;    // Get corresponding map service based on the map type
            // Get corresponding map service based on the map type
            this.mapTypeService = this.getMapServiceByMapType(this.options.mapType);
            if (!this.mapTypeService) {
                throw new Error('Map services error!');
            }    // Get data for the specfic map type service
            // Get data for the specfic map type service
            this.makeRequest(this.mapTypeService.href).then(function (data) {
                if (!data || !data['tile url']) {
                    throw new Error('Map tiles error!');
                }    // Set layer attribution
                // Set layer attribution
                self.options.attribution.text = '<a target="_blank" rel="noopener noreferrer" href=' + data['copyright href'] + '>' + data.copyright + '</a>';    // Trigger attribution control changes
                // Trigger attribution control changes
                self.map.events.fire('attribution-update', self.options.attribution);
                self.map.events.fire('layer-attribution-reset', self);    // Save map type - specific service data
                // Save map type - specific service data
                self.mapTypeServiceData = data;    // Set map style
                // Set map style
                self.setMapStyle(self.options.mapStyle);
                promise.resolve();
            }, function () {
                promise.reject();
                throw new Error('Map tiles error!');
            });
            return promise;
        },
        /*
             * Sets specified map style.
             * If no style is supported by the current configuration (map service/map type) the default tile URL is used.
             */
        setMapStyle: function (mapStyle) {
            var availableMapStyles = this.mapTypeServiceData['tile styles'], styleFound = false, i = 0;    // No styles available for this map type
            // No styles available for this map type
            if (!availableMapStyles) {
                // Set tiles url
                this.setTilesUrl(this.mapTypeServiceData['tile url']);
            } else {
                for (i = 0; i < availableMapStyles.length; i++) {
                    if (availableMapStyles[i].id === mapStyle) {
                        styleFound = true;    // Save map style
                        // Save map style
                        this.options.mapStyle = availableMapStyles[i].id;    // Set tiles url
                        // Set tiles url
                        this.setTilesUrl(availableMapStyles[i].url);
                        break;
                    }
                }
                if (!styleFound) {
                    throw new Error('Style \'' + mapStyle + '\' is not a supported map style!');
                }
            }
        },
        /*
             * Sets the layer's tile URL and calls base reset in order to redraw tile layer.
             */
        setTilesUrl: function (tilesUrl) {
            if (!tilesUrl) {
                throw new Error('Map services error!');
            }    // Set tiles URL based on the style
            // Set tiles URL based on the style
            this.url = tilesUrl + '{z}/{x}/{y}.png';    // Reset TileLayer
            // Reset TileLayer
            if (this.map) {
                base.prototype.reset.call(this);
            }
        },
        /*
             * Gets all available map types based on the previously fetched map services.
             */
        getAvailableMapTypes: function () {
            var availableMapTypes = [], i = 0;
            if (!this.mapServices || this.mapServices.length === 0) {
                throw new Error('Map services error!');
            }
            for (i = 0; i < this.mapServices.length; i++) {
                availableMapTypes.push(this.mapServices[i].name);
            }
            return availableMapTypes;
        },
        /*
             * Gets all available map styles for the current configutaion (map type/map service)
             */
        getAvailableMapStyles: function () {
            var availableMapStyles = [], i = 0;
            if (!this.mapTypeServiceData) {
                throw new Error('Map services error!');
            }
            if (!this.mapTypeServiceData['tile styles']) {
                return availableMapStyles;
            }
            for (i = 0; i < this.mapTypeServiceData['tile styles'].length; i++) {
                availableMapStyles.push(this.mapTypeServiceData['tile styles'][i].id);
            }
            return availableMapStyles;
        },
        /*
             * Gets the layer's latest version map services.
             */
        getMapServices: function (data) {
            var latestMapData = null;
            if (!data || !data.maps) {
                throw new Error('Map services error!');
            }    // Get latest map data based on version
            // Get latest map data based on version
            latestMapData = this.getLastVersionOfMapData(data.maps);
            if (!latestMapData || !latestMapData.services || latestMapData.services.length === 0) {
                throw new Error('Map services error!');
            }    // Return latest map data services
            // Return latest map data services
            return latestMapData.services;
        },
        /*
             * Gets the layers map service by specifying the map type.
             */
        getMapServiceByMapType: function (mapType) {
            var i = 0;
            if (!mapType || !this.mapServices || this.mapServices.length === 0) {
                throw new Error('Map services error!');
            }
            for (i = 0; i < this.mapServices.length; i++) {
                if (this.mapServices[i].name === mapType) {
                    return this.mapServices[i];
                }
            }
            return null;
        },
        /*
             * Gets the latest version of map services.
             */
        getLastVersionOfMapData: function (mapData) {
            var i = 0, latestMapData = null;
            if (!mapData || mapData.length === 0) {
                throw new Error('Map services error!');
            }
            latestMapData = mapData[0];
            for (i = 1; i < mapData.length; i++) {
                if (mapData[i].version > latestMapData.version) {
                    latestMapData = mapData[i];
                }
            }
            return latestMapData;
        }    // endregion
    };    // Extend class
    // Extend class
    T.Util.inherit(TibcoLayer, T.TileLayer);    // Export
    // Export
    exports.TibcoLayer = TibcoLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document, proj4, clearTimeout, setTimeout */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document, proj4, clearTimeout, setTimeout */
(function (exports) {
    /**
         * WmsLayer class is responsible for adding WMS images on the map
         *
         * @inherits {ImageLayer}
         * @summary WmsLayer's constructor
         * @constructor true
         *
         * @event image-load The WMS image was loaded
         * @event image-loading The WMS image is currently loading
         * @event wms-error There was an error loading WMS image
         *
         * @option {Boolean} transparent If `true`, transparent image is expected (default: `true`)
         * @option {String} version The version of the WMS (default: `'1.1.1'`)
         * @option {String} format The image format (default: `'image/png'`)
         * @option {Array} styles The styles array that will be sent to the server (default: `[]`)
         * @option {Array} queryParams The query parameters array that will be sent to the server (default: `[]`)
         *
         * @param {String} url The WMS URL
         * @param {Object} [options](#options) The WmsLayer's options
         */
    var WmsLayer = function (url, options) {
        options.transparent = options.transparent || 'true';
        options.version = options.version || '1.1.1';
        options.format = options.format || 'image/png';
        options.styles = options.styles || [];
        options.queryParams = options.queryParams || [];    // Baseclass constructor
        // Baseclass constructor
        T.ImageLayer.call(this, null, null, options);
        this.wmsUrl = url;
    };
    WmsLayer.prototype = {
        computeParameters: function (bounds) {
            var param = {};
            if (this.options.maxBounds) {
                param.bounds = bounds.intersect(this.options.maxBounds);
                var topLeftPx = this.map.transform(param.bounds.topLeft());
                var bottomRightPx = this.map.transform(param.bounds.bottomRight());
                param.topLeftPixel = topLeftPx.substract(this.map.calculateTopLeft()).substract(this.map.getLayersPos());
                param.size = bottomRightPx.substract(topLeftPx);
            } else {
                param.topLeftPixel = new T.Point(0, 0).substract(this.map.getLayersPos());
                param.size = this.map.getSize();
                param.bounds = bounds;
            }    // float to int
            // float to int
            param.size.x = param.size.x | 0;
            param.size.y = param.size.y | 0;
            return param;
        },
        handleBackbuffer: function (noTransform) {
            // Switch  buffers
            if (this.image.completed) {
                var temp = this.backImage;
                this.backImage = this.image;
                this.image = temp;    // Prepare new image
                // Prepare new image
                this.image.style.visibility = 'hidden';
                this.image.completed = false;
                if (T.AnimUtil.canAnim()) {
                    T.AnimUtil.setMatrix(this.image, new T.Matrix2D());
                } else {
                    this.image.style.width = '';
                    this.image.style.height = '';
                    this.image.style.left = '';
                    this.image.style.top = '';
                }
            } else {
                this.image.onload = null;
                this.image.onerror = null;
                this.image.removeAttribute('src');
            }    // Apply temporary transformation to visible container
            // Apply temporary transformation to visible container
            if (!noTransform) {
                var matrix = this.map.lastTransform.matrix;
                this.backImage.matrix = matrix.multiplyBy(this.backImage.matrix);
                T.DomUtil.position(this.backImage, new T.Point(0, 0));
                if (T.AnimUtil.canAnim()) {
                    T.AnimUtil.transition(this.backImage, 0);
                    T.AnimUtil.setMatrix(this.backImage, this.backImage.matrix.toString());
                } else {
                    this.backImage.style.width = (this.backImage.originalWidth || 0) * this.backImage.matrix.m11 + 'px';
                    this.backImage.style.height = (this.backImage.originalHeight || 0) * this.backImage.matrix.m11 + 'px';
                    this.backImage.style.left = this.backImage.matrix.m13 + 'px';
                    this.backImage.style.top = this.backImage.matrix.m23 + 'px';
                }
            }
        },
        /* Overload */
        reset: function (noTransform) {
            this.handleBackbuffer(noTransform);
            var key, queryParams = this.options.queryParams, image = this.image, crs = this.map.options.crs, url = this.wmsUrl, map = this.map, bounds = map.getProjectedBounds(), param = this.computeParameters(bounds), size = param.size, topLeftPixel = param.topLeftPixel, bottomLeft = param.bounds.bottomLeft(), topRight = param.bounds.topRight();
            if (size.x <= 0 || size.y <= 0) {
                this.events.fire('image-load', { tile: null });
                return;
            }    //Build URL
            //Build URL
            url += '?';
            url += 'service=WMS';
            url += '&version=' + this.options.version;
            url += '&request=GetMap';
            url += '&layers=' + this.options.layers.join(',');
            url += '&styles=' + this.options.styles.join(',');
            url += '&bbox=' + bottomLeft.x + ',' + bottomLeft.y + ',' + topRight.x + ',' + topRight.y;
            url += '&format=' + this.options.format;
            url += '&SRS=' + crs.code;
            url += '&width=' + size.x;
            url += '&height=' + size.y;
            url += '&transparent=' + this.options.transparent;    // Additional WMS parameters
            // Additional WMS parameters
            for (key in queryParams) {
                if (queryParams.hasOwnProperty(key)) {
                    url += '&' + key + '=' + queryParams[key];
                }
            }
            this.events.fire('image-loading', { tile: this.image });    //Setup image
            //Setup image
            T.DomUtil.position(image, topLeftPixel);
            image.onload = this.onImageLoad;
            image.onerror = this.onImageError;
            image.style.width = size.x + 'px';
            image.style.height = size.y + 'px';
            image.src = url;
        },
        onMoveEnd: function () {
            this.reset(true);
        },
        /* Overload */
        reload: function (url, bounds) {
        },
        /* Overload */
        onAdd: function (map) {
            WmsLayer.base.onAdd.call(this, map);
            map.events.on('move-end', this.onMoveEnd, this);
        },
        /* Overload */
        onRemove: function () {
            WmsLayer.base.onRemove.call(this);
            this.map.events.detach('move-end', this.reset);
        },
        /* Overload */
        onImageLoad: function () {
            var self = this.layer;
            var img;
            this.completed = true;
            self.image.style.zIndex = 0;
            self.backImage.style.zIndex = -1;
            self.image.style.visibility = 'visible';
            self.backImage.style.visibility = 'hidden';    //Store the last position of the image;
            //Store the last position of the image;
            self.image.matrix = new T.Matrix2D();
            self.image.matrix.m13 += T.DomUtil.position(this).x;
            self.image.matrix.m23 += T.DomUtil.position(this).y;
            if (!T.AnimUtil.canAnim()) {
                self.image.originalWidth = parseInt(this.style.width, 10);
                self.image.originalHeight = parseInt(this.style.height, 10);
            }
            WmsLayer.base.onImageLoad.call(this);
        },
        /* Overload */
        onImageError: function (e) {
            this.layer.events.fire('wms-error', 'Error loading WMS image');
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(WmsLayer, T.ImageLayer);    // Export
    // Export
    exports.WmsLayer = WmsLayer;
}(T));    // > TileLayer.js 0.0.2
// > https://github.com/Maporama/TibcoMaps
// >
// > TileLayer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
//BETA// not ready for production, only works with SphericalMercator
// > TileLayer.js 0.0.2
// > https://github.com/Maporama/TibcoMaps
// >
// > TileLayer class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
//BETA// not ready for production, only works with SphericalMercator
(function (exports) {
    // ## Constructor
    //
    // The constructor takes 2 parameters, the URL to
    // a tile server and tile options.
    //
    // Simple example:
    //
    //		var layer1 = new T.TileLayer("http://raster.maporama.com/maporama/{z}/{x}/{y}.png");
    //
    var WmsTileLayer = function (url, options) {
        if (!url) {
            throw new Error('TileLayer must have an URL');
        }    // Call super class constructor
        // Call super class constructor
        T.TileLayer.call(this, url, options);
        this.url = url;
        options.transparent = options.transparent || 'true';
        options.version = options.version || '1.1.1';
        options.format = options.format || 'image/png';
        options.styles = options.styles || [];
        options.queryParams = options.queryParams || [];
        options.errorTileAttemptsThreshold = 0;    //geoserver has behaves wierd on requerying images
    };
    WmsTileLayer.prototype = {
        /* Override */
        getTileUrl: function (point) {
            /* If the tiles position exceeds boundaries for specific zoom recalculate it */
            var size, tilePos, crs = this.map.options.crs, url = this.url, tileSize = this.options.tileSize, queryParams = this.options.queryParams, tileSizePoint = new T.Point(tileSize, tileSize), topLeft, topRight, bottomLeft, bottomRight, topLeftLatLng, bottomRightLatLng, key;    // Compute position and size
            // Compute position and size
            tilePos = point.multiplyBy(tileSize);
            topLeft = crs.untransform(tilePos, this.map.getZoom());
            size = tileSizePoint.multiplyBy(this.map.getResolution()).multiplyBy(new T.Point(1, -1));
            bottomRight = topLeft.add(size);    // Build URL
            // Build URL
            url += '?';
            url += 'service=WMS';
            url += '&version=' + this.options.version;
            url += '&request=GetMap';
            url += '&layers=' + this.options.layers.join(',');
            url += '&styles=' + this.options.styles.join(',');
            url += '&bbox=' + topLeft.x + ',' + bottomRight.y + ',' + bottomRight.x + ',' + topLeft.y;    //Left, Bottom, Right, Top
            //Left, Bottom, Right, Top
            url += '&format=' + this.options.format;
            url += '&SRS=' + crs.code;
            url += '&width=' + tileSize;
            url += '&height=' + tileSize;
            url += '&transparent=' + this.options.transparent;    // Additional parameters
            // Additional parameters
            for (key in queryParams) {
                if (queryParams.hasOwnProperty(key)) {
                    url += '&' + key + '=' + queryParams[key];
                }
            }
            return url;
        },
        /* Override */
        getTilePosition: function (point) {
            var tileSize = this.options.tileSize, topLeft = this.map.getCenterInPixels().substract(this.map.getSize().divideBy(2)).floor();
            return point.multiplyBy(tileSize).substract(topLeft);
        },
        /* Override */
        onRemove: function () {
            WmsTileLayer.base.onRemove.call(this);    //this.map.events.detach('view-reset', this.reset);
            //this.map.events.detach('move-end', this.reset);
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(WmsTileLayer, T.TileLayer);    // Export
    // Export
    exports.WmsTileLayer = WmsTileLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * PopupsLayer class is responsible for adding popups on the map
         *
         * @inherits {BaseLayer}
         * @summary PopupsLayer's constructor
         * @constructor true
         *
         * @option {Number} opacity The opacity of the layer. Values from `0` to `1` (Default: `1`)
         *
         * @param {Object} [options](#options) The PopupsLayer's options
         */
    var PopupsLayer = function (options) {
        // Call super class constructor
        T.BaseLayer.call(this, options);
        this.popups = [];
    };
    PopupsLayer.prototype = {
        options: { opacity: 1 },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-layer popup-layer', this.map.$container.layers);
                this.$container.layer = this;
            }
        },
        reset: function () {
            var i = 0;
            for (i = 0; i < this.popups.length; i++) {
                this.popups[i].reset();
            }
        },
        /**
             * Add a popup to the layer
             *
             * @param {Popup} popup The popup that will be added to the layer
             */
        addPopup: function (popup) {
            this.$container.appendChild(popup.$container);
            popup.layer = this;
            popup.onAdd(this);
            this.popups.push(popup);    // Add click event on popup close button
            // Add click event on popup close button
            if (popup.$closeButton) {
                T.DomEventUtil.on(popup.$closeButton, 'mousedown', this.onPopupCloseButtonClick, this);
            }
        },
        /**
             * Removes a popup from the layer
             *
             * @param {Popup} popup The popup that will be removed from the layer
             */
        removePopup: function (popup) {
            var index = this.popups.indexOf(popup);
            if (index > -1) {
                this.popups.splice(index, 1);
                popup.layer = null;
                popup.onRemove(this);
                this.$container.removeChild(popup.$container);    // Remove click event from popup close button
                // Remove click event from popup close button
                if (popup.$closeButton) {
                    T.DomEventUtil.off(popup.$closeButton, 'mousedown', this.onPopupCloseButtonClick, this);
                }
            }
        },
        /**
             * Removes all popups from the layer
             */
        removeAllPopups: function () {
            var i = 0;
            for (i = 0; i < this.popups.length; i++) {
                this.popups[i].layer = null;
                this.popups[i].onRemove(this);
            }
            this.popups = [];
            T.DomUtil.html(this.$container, '');
        },
        /**
             * Sets the layers' opacity
             *
             * @param {Number} opacity The new opacity of the layer. Values from `0` to `1`
             */
        setOpacity: function (opacity) {
            this.options.opacity = opacity;
            this.$container.style.opacity = this.options.opacity;
        },
        /* Override */
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.reset, this);
            this.initContainer();
            this.reset();
        },
        /* Override */
        onRemove: function () {
            T.PopupsLayer.base.onRemove.call(this);
            this.map.events.detach('view-reset', this.reset);
        },
        onPopupCloseButtonClick: function (event) {
            var target = event.target || event.srcElement;
            target.popup.close();
        }
    };    // Extend class
    // Extend class
    T.Util.inherit(PopupsLayer, T.BaseLayer);    // Export
    // Export
    exports.PopupsLayer = PopupsLayer;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, document, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, document, window */
(function (exports) {
    /**
         * Popup class is responsible for displaying HTML content in a popup on the map. Popups should be added using {PopupsLayer}
         *
         * @summary Popup's constructor
         * @constructor true
         *
         * @option {String} closeButtonUrl The URL to the close button image (default: `''`)
         * @option {Point} offset The pixels offset of the popup from its original position (default: `{x: 0, y: 0}`)
         * @option {Boolean} panMap If `true`, pans the map if popup is not completely visible
         * @option {Point} panMapExtraOffset The extra pixels that the map should be panned when popup is not completely in view
         *
         * @param {String} html The HTML content that will be added to the popup's container
         * @param {Object} [options](#options) The options of the popup instance
         */
    var Popup = function (html, options) {
        this.html = html;
        this.options = T.Util.defaults(options, this.options);
        this.initContainer();
    };
    Popup.prototype = {
        options: {
            closeButtonUrl: '',
            offset: {
                x: 0,
                y: 0
            },
            panMap: false,
            panMapExtraOffset: {
                x: 0,
                y: 0
            }
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-popup');
                if (this.options.closeButtonUrl) {
                    // Create element
                    this.$closeButton = T.DomUtil.create('img', 'tibco-popup-close', this.$container);    // Add img src
                    // Add img src
                    this.$closeButton.src = this.options.closeButtonUrl;    // Save popup so that we can access it in event handlers
                    // Save popup so that we can access it in event handlers
                    this.$closeButton.popup = this;
                }
                this.$htmlContent = T.DomUtil.create('div', 'tibco-popup-content', this.$container);
                this.$tail = T.DomUtil.create('div', 'tibco-popup-tail', this.$container);
                this.$container.style.position = 'absolute';
                if (this.html) {
                    T.DomUtil.html(this.$htmlContent, this.html);
                }    // Popup must be manually opened
                // Popup must be manually opened
                this.close();
            }
        },
        addHandler: function (handler) {
            handler.init(this);
        },
        // ## Set Html
        // Sets new HTML content for the popup with an option to specify template data object in the form {{property}}
        setHtml: function (template, templateData) {
            var key = null, keyInTemplate = null, regExp = null;
            if (!template) {
                return;
            }    // Make replacements in the template if template data is specified
            // Make replacements in the template if template data is specified
            if (templateData) {
                for (key in templateData) {
                    keyInTemplate = '{{' + key + '}}';
                    if (template.indexOf(keyInTemplate) == -1) {
                        throw 'Invalid object for popup template!';
                    } else {
                        regExp = new RegExp(keyInTemplate, 'g');
                        template = template.replace(regExp, templateData[key]);
                    }
                }
            }    // Save
            // Save
            this.html = template;    // Set content
            // Set content
            T.DomUtil.html(this.$htmlContent, this.html);
        },
        // ## Get position
        // Returns the position (in pixels) considering the latlng and the offset
        getPosition: function (latlng, offset) {
            // Get position of coordinates
            var position = this.layer.map.latLngToPoint(latlng).floor();    // Get new position by taking into consideration the popup size (including the tail) and the popup offset
            // Get new position by taking into consideration the popup size (including the tail) and the popup offset
            position = position.substract(new T.Point(this.$container.clientWidth / 2, this.$container.offsetHeight + this.$tail.offsetHeight / 2)).add(new T.Point(this.options.offset.x, this.options.offset.y));
            return position;
        },
        // ## Get layers container pan offset
        // Return the top & left offset of the layers container
        // Layers container offset is changed when panning the map
        getLayersContainerPanOffset: function () {
            return {
                top: parseInt(T.DomUtil.getStyle(this.layer.map.$container.layers, 'top'), 10),
                left: parseInt(T.DomUtil.getStyle(this.layer.map.$container.layers, 'left'), 10)
            };
        },
        // ## Get pan offset
        // Returns the offset used for panning the map so that the popup is visible in the map viewport
        getPanOffset: function () {
            var offsetX = 0, offsetY = 0, popupPanPosition = this.getPositionWithPan();
            if (popupPanPosition.left < 0) {
                offsetX = Math.abs(popupPanPosition.left) + this.options.panMapExtraOffset.x;
            } else if (!this.isHorizontallyVisible()) {
                offsetX = this.layer.map.$container.offsetWidth - this.options.panMapExtraOffset.x - (popupPanPosition.left + this.$container.offsetWidth);
            } else {
                offsetX = -this.options.panMapExtraOffset.x;
            }
            if (popupPanPosition.top < 0) {
                offsetY = Math.abs(popupPanPosition.top) + this.options.panMapExtraOffset.y;
            } else if (!this.isVerticallyVisible()) {
                offsetY = this.layer.map.$container.offsetHeight - this.options.panMapExtraOffset.y - (popupPanPosition.top + this.$container.offsetHeight + this.$tail.offsetHeight);
            } else {
                offsetY = -this.options.panMapExtraOffset.y;
            }
            return {
                x: offsetX,
                y: offsetY
            };
        },
        // ## Get position with pan
        // Returns the popup top & left position considering how much the layers container have been panned
        // This is used to correctly consider the actual position of the tooltip when the map is panned
        getPositionWithPan: function () {
            var layersContainerPanOffset = this.getLayersContainerPanOffset();
            if (!this.$container.tibcoPosition) {
                return null;
            }
            return {
                top: this.$container.tibcoPosition.y + layersContainerPanOffset.top,
                left: this.$container.tibcoPosition.x + layersContainerPanOffset.left
            };
        },
        /**
             * Checks if the popup is entirely visible in the map viewport
             * @return {Boolean} If `true`, the popup is visible
             */
        isInViewport: function () {
            return this.isHorizontallyVisible() && this.isVerticallyVisible();
        },
        /**
             * Checks if popup is entirely visible on X axis
             * @return {Boolean} If `true`, the popup is visible
             */
        isHorizontallyVisible: function () {
            var popupPanPosition = this.getPositionWithPan();
            return popupPanPosition.left > 0 && popupPanPosition.left + this.$container.offsetWidth <= this.layer.map.$container.offsetWidth;
        },
        /**
             * Checks if popup is entirely visible on Y axis
             * @return {Boolean} If `true`, the popup is visible
             */
        isVerticallyVisible: function () {
            var popupPanPosition = this.getPositionWithPan();
            return popupPanPosition.top > 0 && popupPanPosition.top + this.$container.offsetHeight <= this.layer.map.$container.offsetHeight;
        },
        // ## Pan map if necessary
        // Pans the map if the 'panMap' option is true and if this popup is not in the map viewport
        panMapIfNecessary: function () {
            var panOffset;
            if (this.options.panMap === true && !this.isInViewport()) {
                panOffset = this.getPanOffset();
                this.layer.map.panBy(new T.Point(panOffset.x, panOffset.y));
            }
        },
        reset: function () {
            var position;
            if (!this.reference) {
                return;
            }
            position = this.getPosition(this.reference, this.options.offset);
            T.DomUtil.position(this.$container, position);
        },
        /**
             * Opens the popup and sets its position
             *
             * @param {LatLng} reference The coordinates on the map where to show the popup
             */
        open: function (reference) {
            if (!reference) {
                return;
            }
            this.reference = reference;
            T.DomUtil.show(this.$container);
            this.reset();
            this.panMapIfNecessary();
        },
        /**
             * Closes the popup
             */
        close: function () {
            T.DomUtil.hide(this.$container);
        },
        onAdd: function (layer) {
        },
        onRemove: function (layer) {
        }
    };    // Exports
    // Exports
    exports.Popup = Popup;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, document, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, document, window */
(function (exports) {
    /**
         * ImageMarker class is responsible for displaying an image in a marker on the map. Markers should be added using {MarkersLayer}
         *
         * @summary ImageMarker's constructor
         * @constructor true
         *
         * @param {LatLng} latLng The coordinates of the marker
         * @param {String} url The URL to the image
         * @param {Object} [options](#options) The options of the marker
         *
         * @option {Boolean} draggable If `true`, a {MarkerDragHandler} will be enabled for the marker (default: `false`)
         * @option {Point} offset The pixels offset of the marker from its original position (default: `new T.Point(0, 0)`)
         * @option {String} anchor The point of anchor on the marker (default: `'bottom-center'`). Available options are:
         *
         * - `'bottom-left'`
         * - `'bottom-center'`
         * - `'bottom-right'`
         */
    var ImageMarker = function (latlng, url, options) {
        this.latlng = latlng;
        this.url = url;
        this.options = T.Util.defaults(options, this.options);
        this.imageSize = new T.Point(0, 0);
        this.initContainer();
        if (this.options.draggable) {
            this.addHandler(new T.MarkerDragHandler());
        }
        this.tooltip = null;
    };
    ImageMarker.prototype = {
        // > - **anchor** - bottom-left, bottom-center, bottom-right
        options: {
            draggable: false,
            draggableUsingOffset: false,
            popOnClick: true,
            offset: new T.Point(0, 0),
            anchor: 'bottom-center'
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-marker');
                this.$container.marker = this;
                this.$container.style.position = 'absolute';
            }
        },
        addHandler: function (handler) {
            handler.init(this);
        },
        reset: function () {
            if (!this.layer || !this.layer.map) {
                return;
            }
            var pxPosition = this.layer.map.latLngToPoint(this.latlng);
            switch (this.options.anchor) {
            case 'bottom-left':
                pxPosition.y = pxPosition.y - this.imageSize.y;
                break;
            case 'bottom-center':
                pxPosition.x = pxPosition.x - this.imageSize.x / 2;
                pxPosition.y = pxPosition.y - this.imageSize.y;
                break;
            case 'bottom-right':
                pxPosition.x = pxPosition.x - this.imageSize.x;
                pxPosition.y = pxPosition.y - this.imageSize.y;
                break;
            }
            pxPosition = pxPosition.substract(this.options.offset).floor();
            T.DomUtil.position(this.$container, pxPosition);
        },
        resetFromPx: function () {
            var p = T.DomUtil.position(this.$container).add(new T.Point(this.imageSize.x / 2, this.imageSize.y));
            this.latlng = this.layer.map.pointToLatLng(p);
        },
        initImage: function () {
            var image = this.image = T.DomUtil.create('img', 'tibco-marker-image');
            image.marker = this;
            image.onload = this.onImageLoad;
            image.onerror = this.onImageError;
            image.src = this.url;
            if (this.tooltip != null && this.tooltip.length) {
                image.title = this.tooltip;
            }
            this.$container.appendChild(image);
        },
        onAdd: function (layer) {
            if (!this.image) {
                this.initImage();
            }
        },
        onRemove: function (layer) {
        },
        onImageLoad: function () {
            this.marker.imageSize = new T.Point(this.width, this.height);
            this.marker.reset();
        },
        onImageError: function () {
        }
    };    // Exports
    // Exports
    exports.ImageMarker = ImageMarker;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, document, window */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T, document, window */
(function (exports) {
    /**
         * HtmlMarker class is responsible for displaying HTML content in a marker on the map. Markers should be added using {MarkersLayer}
         *
         * @summary HtmlMarker's constructor
         * @constructor true
         *
         * @param {LatLng} latLng The coordinates of the marker
         * @param {String} html The HTML content of the marker
         * @param {Object} [options](#options) The options of the marker
         *
         * @option {Boolean} draggable If `true`, a {MarkerDragHandler} will be enabled for the marker (default: `false`)
         * @option {Point} offset The pixels offset of the marker from its original position (default: `new T.Point(0, 0)`)
         */
    var HtmlMarker = function (latlng, html, options) {
        this.latlng = latlng;
        this.html = html;
        this.options = T.Util.defaults(options, this.options);
        this.dragOffset = new T.Point(0, 0);
        this.initContainer();
        if (this.options.draggable) {
            this.addHandler(new T.MarkerDragHandler());
        }
    };
    HtmlMarker.prototype = {
        options: {
            draggable: false,
            draggableUsingOffset: false,
            popOnClick: false,
            offset: new T.Point(0, 0)
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-marker');
                this.$container.marker = this;
                this.$container.style.position = 'absolute';
                if (this.html) {
                    T.DomUtil.html(this.$container, this.html);
                }
            }
        },
        addHandler: function (handler) {
            handler.init(this);
        },
        reset: function () {
            if (!this.layer || !this.layer.map) {
                return;
            }
            var pxPosition = this.layer.map.latLngToPoint(this.latlng).floor();
            pxPosition = pxPosition.substract(this.options.offset).add(this.dragOffset);
            T.DomUtil.position(this.$container, pxPosition);
        },
        resetFromPx: function () {
            var p = T.DomUtil.position(this.$container).add(this.options.offset);
            this.latlng = this.layer.map.pointToLatLng(p);
        },
        resetOffset: function () {
            var viewportPos = this.layer.map.latLngToPoint(this.latlng);
            var currentPos = T.DomUtil.position(this.$container);
            this.dragOffset = currentPos.substract(viewportPos);
        },
        onAdd: function (layer) {
        },
        onRemove: function (layer) {
        }
    };    // Exports
    // Exports
    exports.HtmlMarker = HtmlMarker;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * ScaleControl class is responsible for displaying the scale of the map. Controls should be added using {Map}
         *
         * @summary ScaleControl's constructor
         * @constructor true
         *
         * @param {Object} [options](#options) The options of the control
         *
         * @option {Array} offset The offset in pixels from the control's original position (default: `[0, 0]`)
         * @option {Number} fontSize The size of control's font (default: `10`)
         * @option {String} fontFamily The family of control's font (default: `'Tahoma'`)
         * @option {String} fontStyle The style of control's font (default: `'normal'`)
         * @option {String} kmLabel The text to display for kilometers (default: `'km'`)
         * @option {String} mLabel The text to display for meters (default: `'m'`)
         * @option {String} miLabel The text to display for miles (default: `'mi'`)
         * @option {String} ftLabel The text to display for feet (default: `'ft'`)
         */
    var ScaleControl = function (options) {
        this.options = T.Util.defaults(options, this.options);
    };
    ScaleControl.prototype = {
        options: {
            offset: [
                0,
                0
            ],
            fontSize: 10,
            fontStyle: 'normal',
            fontFamily: 'Tahoma',
            kmLabel: 'km',
            mLabel: 'm',
            miLabel: 'mi',
            ftLabel: 'ft'
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'scale-control', this.map.$container.controls);
                this.$container.style.position = 'absolute';
                this.$container.style.left = this.options.offset[0] + 5 + 'px';
                this.$container.style.bottom = this.options.offset[1] + 5 + 'px';
                this.$container.style.fontSize = this.options.fontSize + 'pt';
                this.$container.style.fontFamily = this.options.fontFamily;
                this.$container.style.fontStyle = this.options.fontStyle;    //this.$container.controls = this;
            }
        },
        getPresetDistance: function (resolution) {
            var resStr = parseInt(resolution, 10).toString();
            var multiplier = Math.pow(10, resStr.length);
            var res = resolution / multiplier;
            if (res < 0.03) {
                return multiplier;
            } else if (res < 0.1) {
                return 5 * multiplier;
            } else if (res < 0.2) {
                return 10 * multiplier;
            } else if (res < 0.4) {
                return 20 * multiplier;
            } else {
                return 50 * multiplier;
            }
        },
        getPresetMileDistance: function (resolution) {
            var resStr = parseInt(resolution, 10).toString();
            var multiplier = Math.pow(10, resStr.length);
            var res = resolution / multiplier;
            if (res < 0.015) {
                return 5;
            } else if (res < 0.02) {
                return 10;
            } else if (res < 0.03) {
                return multiplier;
            } else if (res < 0.1) {
                return 5 * multiplier;
            } else if (res < 0.2) {
                return 10 * multiplier;
            } else if (res < 0.4) {
                return 20 * multiplier;
            } else {
                return 50 * multiplier;
            }
        },
        getPresetFeetDistance: function (resolution) {
            var resStr = parseInt(resolution, 10).toString();
            var multiplier = Math.pow(10, resStr.length);
            var res = resolution / multiplier;
            if (res < 0.03) {
                return multiplier;
            } else if (res < 0.15) {
                return 5 * multiplier;
            } else if (res < 0.25) {
                return 10 * multiplier;
            } else if (res < 0.45) {
                return 20 * multiplier;
            } else {
                return 50 * multiplier;
            }
        },
        reset: function () {
            var resolution = this.map.getMapResolution();    // meters
            // meters
            var meterDistance = this.getPresetDistance(resolution);
            var meterWidth = meterDistance / resolution;
            var meterDispLabel = this.options.mLabel;
            if (meterDistance / 1000 >= 1) {
                meterDistance = meterDistance / 1000;
                meterDispLabel = this.options.kmLabel;
            }    // imperial
            // imperial
            var feetResolution = resolution * 3.28084;
            var mileDispLabel = this.options.ftLabel;
            var miles = false;
            var mileDistance = this.getPresetFeetDistance(feetResolution);
            if (mileDistance / 5280 >= 1) {
                var mileResolution = resolution * 0.000621371;
                mileDistance = this.getPresetMileDistance(mileResolution);
                miles = true;
                mileDispLabel = this.options.miLabel;
            }
            var mileWidth = miles === false ? mileDistance * 0.3048 / resolution : mileDistance * 1609.34 / resolution;
            var html = '';
            html += '<div style="padding-left: 6px;"><span>' + meterDistance + ' ' + meterDispLabel + '</span></div>';
            html += '<div style="width: ' + Math.max(meterWidth, mileWidth) + 'px; height: 0px; border: 1px solid;"></div>';
            html += '<div style="padding-left: 6px;"><span>' + mileDistance + ' ' + mileDispLabel + '</span></div>';
            html += '<div style="position: absolute; border: 1px solid;height: 26px;top: 50%;margin-top: -13px;"></div>';
            html += '<div style="position: absolute; border: 1px solid;height: 5px;top: 50%;left:' + meterWidth + 'px;margin-top: -6px;"></div>';
            html += '<div style="position: absolute; border: 1px solid;height: 5px;top: 50%;left:' + mileWidth + 'px;"></div>';
            T.DomUtil.html(this.$container, html);
        },
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.reset, this);
            this.map.events.on('move', this.reset, this);
            this.initContainer();
            this.reset();
        },
        onRemove: function () {
            this.map.events.detach('view-reset', this.reset);
            this.map.events.detach('move', this.reset);
            T.DomUtil.remove(this.$container);
            this.map = null;
        }
    };    // Export
    // Export
    exports.ScaleControl = ScaleControl;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * AttributionControl class is responsible for displaying attribution information on the map. Controls should be added using {Map}
         *
         * @summary AttributionControl's constructor
         * @constructor true
         *
         * @param {Object} [options](#options) The options of the control
         *
         * @option {String} separator The separator between attribution texts (default: `' | '`)
         * @option {String} prefix The prefix that will be added before the attribution texts (default: `''`)
         * @option {Array} offset The offset in pixels from the control's original position (default: `[0, 0]`)
         * @option {Number} fontSize The size of attribution texts' font (default: `10`)
         * @option {String} fontFamily The family of the attribution texts' font (default: `'Tahoma'`)
         * @option {Number} opacity The opacity of the control. Values from `0` to `1` (default: `0.5`)
         *
         */
    var AttributionControl = function (options) {
        this.options = T.Util.defaults(options, this.options);
        this.attributions = [];
    };
    AttributionControl.prototype = {
        options: {
            separator: ' | ',
            prefix: '',
            offset: [
                0,
                0
            ],
            fontSize: 9,
            fontFamily: 'Tahoma',
            opacity: 0.5
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', 'tibco-control tibco-attribution', this.map.$container.controls);
                this.$container.style.background = '#FFFFFF';
                this.$container.style.position = 'absolute';
                this.$container.style.margin = 0;
                this.$container.style.right = this.options.offset[0] + 'px';
                this.$container.style.bottom = this.options.offset[1] + 'px';
                this.$container.style.padding = '5px';
                this.$container.style.fontSize = this.options.fontSize + 'pt';
                this.$container.style.fontFamily = this.options.fontFamily;    // IE < 9 style for opacity
                // IE < 9 style for opacity
                this.$container.style.filter = 'progid:DXImageTransform.Microsoft.Alpha(Opacity=' + this.options.opacity * 100 + ')';
                T.DomUtil.setOpacity(this.$container, this.options.opacity);
            }
        },
        reset: function () {
            var attribution = '';
            var separator = this.options.separator;
            if (this.options.prefix === '' || this.attributions.length === 0) {
                separator = '';
            }
            attribution = this.options.prefix + separator + this.attributions.join(this.options.separator);
            if (!attribution) {
                this.$container.style.display = 'none';
            } else {
                this.$container.style.display = 'block';
                T.DomUtil.html(this.$container, attribution);
            }
        },
        /**
             * Adds a text to attribution text collection
             * @param {String} text The text to add to the attribution text collection
             * @return {[AttributionControl](#top)} The control instance
             */
        addAttribution: function (text) {
            if (!text || this.getAttributionIndex(text) > -1) {
                return;
            }
            this.attributions.push(text);
            this.reset();
            return this;
        },
        /**
             * Removes a text from attribution text collection
             * @param {String} text The text to remove from the attribution text collection
             * @return {[AttributionControl](#top)} The control instance
             */
        removeAttribution: function (text) {
            if (!text) {
                return;
            }
            var index = this.getAttributionIndex(text);
            if (index > -1) {
                this.attributions = this.attributions.slice(index, 1);
            }
            return this;
        },
        /**
             * Returns the index of the text in the attribution text collection
             * @param {String} text The text to search for
             * @return {Number} The index of the text in the attribution text collection
             */
        getAttributionIndex: function (text) {
            var i = 0;
            for (i = 0; i < this.attributions.length; i++) {
                if (text === this.attributions[i]) {
                    return i;
                }
            }
            return -1;
        },
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('layer-add', this.onLayerAdd, this);
            this.map.events.on('layer-remove', this.onLayerRemove, this);
            this.map.events.on('layer-attribution-reset', this.onLayerAttributionReset, this);
            this.map.events.on('attribution-update', this.onAttributionUpdate, this);
            this.initContainer();
            this.reset();
        },
        onRemove: function () {
            this.map.events.detach('layer-add', this.onLayerAdd);
            this.map.events.detach('layer-remove', this.onLayerRemove);
            T.DomUtil.remove(this.$container);
            this.map = null;
        },
        onLayerAdd: function (layer) {
            if (layer.options.attribution) {
                if (typeof layer.options.attribution === 'object') {
                    this.addAttribution(layer.options.attribution.text);
                } else {
                    this.addAttribution(layer.options.attribution);
                }
            }
        },
        onLayerRemove: function (layer) {
            if (layer.options.attribution) {
                if (typeof layer.options.attribution === 'object') {
                    this.removeAttribution(layer.options.attribution.text);
                } else {
                    this.removeAttribution(layer.options.attribution);
                }
            }
        },
        onAttributionUpdate: function (options) {
            if (options) {
                T.Util.defaults(this.options, options);
            }
        },
        onLayerAttributionReset: function (layer) {
            if (layer.options.attribution) {
                if (typeof layer.options.attribution === 'object') {
                    this.addAttribution(layer.options.attribution.text);
                } else {
                    this.addAttribution(layer.options.attribution);
                }
            }
        }
    };    // Export
    // Export
    exports.AttributionControl = AttributionControl;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * RulerControl class is responsible for displaying map navigation UI. Controls should be added using {Map}
         *
         * @summary RulerControl's constructor
         * @constructor true
         *
         * @param {Object} [options](#options) The options of the control
         *
         * @option {Boolean} disableMouse If `true`, mouse clicks on the map will be ignored (default: `false`)
         * @option {Boolean} useCanvas If `true`, canvas will be used to draw the polyline (default: `false`)
         * @option {String} startFlag The URL to the image representing the start of the ruler (default: `''`)
         * @option {String} endFlag The URL to the image representing the end of the ruler (default: `''`)
         */
    var RulerControl = function (options) {
        this.options = T.Util.defaults(options, this.options);
        this.disbaled = false;
        this.vectorLayer = null;
        this.markerLayer = null;
        this.coordinates = [];
        this.distance = 0;
        this.polyline = new T.Polyline(this.coordinates);
        this.polyline.setStyle({
            color: '#FF0000',
            stroke: true
        });
        this.events = new T.EventDispatcher();
    };
    RulerControl.prototype = {
        options: {
            disableMouse: false,
            useCanvas: false,
            startFlag: '',
            endFlag: ''
        },
        initContainer: function () {
            if (!this.vectorLayer) {
                this.vectorLayer = new T.VectorLayer({ useCanvas: this.options.useCanvas });
                this.map.addLayer(this.vectorLayer);
                this.vectorLayer.addGeometry(this.polyline);
            }
            if (!this.markersLayer) {
                this.markerLayer = new T.MarkersLayer();
                this.map.addLayer(this.markerLayer);
            }
        },
        calculateDisance: function () {
            var i;
            this.distance = 0;
            if (this.coordinates.length > 1) {
                for (i = 1; i < this.coordinates.length; i++) {
                    this.distance += this.computeDistanceBetween(T.LatLng.from(this.coordinates[i - 1]), T.LatLng.from(this.coordinates[i]));
                }
            }
            this.events.fire('distance-changed', this);
        },
        computeDistanceBetween: function (a, b) {
            var R = 6371;
            var dLat = (b.lat - a.lat).toRad();
            var dLon = (b.lng - a.lng).toRad();
            var lat1 = a.lat.toRad();
            var lat2 = b.lat.toRad();
            var x = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
            var y = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));
            var d = R * y;
            return d;
        },
        /**
             * Enables the control
             */
        enable: function () {
            if (!this.options.disableMouse) {
                T.DomEventUtil.on(this.map.$container, 'click', this.onMapClick, this);
            }
            this.disabled = false;
        },
        /**
             * Disables the control
             */
        disable: function () {
            T.DomEventUtil.off(this.map.$container, 'click', this.onMapClick);
            this.disabled = true;
        },
        /**
             * Adds a new coordinate to the ruler
             * @param {LatLng} coord The new coordinate
             */
        addCoordinate: function (coord) {
            this.coordinates.push([
                coord.lat,
                coord.lng
            ]);
            this.reset();
        },
        /**
             * Adds a new coordinate at a specific index in the list of coordinates
             * @param {LatLng} coord The coordinate to add
             * @param {Number} index The index at which to insert the coordinate
             */
        addCoordinateAt: function (coord, index) {
            this.coordinates.splice(index, 0, [
                coord.lat,
                coord.lng
            ]);
            this.reset();
        },
        reset: function () {
            this.vectorLayer.updateViewPort();    //this.polyline.reset();
            //this.polyline.reset();
            var startMarker = null;
            if (this.coordinates.length === 1) {
                startMarker = new T.ImageMarker(T.LatLng.from(this.coordinates[0]), this.options.startFlag, {
                    anchor: 'bottom-center',
                    offset: new T.Point(0, -5)
                });
                this.markerLayer.removeAllMarkers();
                this.markerLayer.addMarker(startMarker);
            } else if (this.coordinates.length > 1) {
                startMarker = new T.ImageMarker(T.LatLng.from(this.coordinates[0]), this.options.startFlag, {
                    anchor: 'bottom-center',
                    offset: new T.Point(0, -5)
                });
                var stopMarker = new T.ImageMarker(T.LatLng.from(this.coordinates[this.coordinates.length - 1]), this.options.endFlag, {
                    anchor: 'bottom-center',
                    offset: new T.Point(0, -5)
                });
                this.markerLayer.removeAllMarkers();
                this.markerLayer.addMarker(startMarker);
                this.markerLayer.addMarker(stopMarker);
            }
            this.calculateDisance();
        },
        /**
             * Clears the coordinates list and removes the ruler from the map
             */
        clear: function () {
            this.coordinates = [];
            this.polyline.coords = this.coordinates;
            this.markerLayer.removeAllMarkers();
            this.reset();
        },
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('view-reset', this.reset, this);
            this.initContainer();
            this.reset();
        },
        onRemove: function () {
            this.map.events.detach('view-reset', this.reset);
            T.DomUtil.remove(this.$container);
            this.map = null;
        },
        onMapClick: function (e) {
            var mousePosition = this.map.computeMousePosition(e);
            var coord = this.map.containerPointToLatLng(mousePosition);
            this.coordinates.push([
                coord.lat,
                coord.lng
            ]);
            this.reset();
        }
    };    // Export
    // Export
    exports.RulerControl = RulerControl;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * LayersControl class is responsible for displaying all the layers on the map in a user interface. Controls should be added using {Map}
         *
         * @summary LayersControl's constructor
         * @constructor true
         *
         * @param {Object} [options](#options) The options of the control
         *
         * @option {Array} offset The offset in pixels from the control's original position (default: `[0, 0]`)
         * @option {Number} fontSize The size of control's font (default: `10`)
         * @option {String} fontFamily The family of control's font (default: `'Tahoma'`)
         */
    var LayersControl = function (options) {
        this.options = T.Util.defaults(options, this.options);
        this.attributions = [];
    };
    LayersControl.prototype = {
        // Constants
        CONTROL_LAYERS_CLASS: 'tibco-control-layers',
        CONTROL_LAYERS_HEADER_CLASS: 'header',
        CONTROL_LAYERS_CONTAINER_CLASS: 'layers',
        HEADER_LABEL_CLASS: 'label',
        HEADER_COLLAPSE_CLASS: 'collapse',
        HEADER_COLLAPSE_TRUE_CLASS: 'is-collapsed',
        HEADER_COLLAPSE_FALSE_CLASS: 'is-not-collapsed',
        LAYER_CONTAINER_CLASS: 'layer',
        LAYER_RADIO_CLASS: 'radio',
        LAYER_VISIBLE_CLASS: 'visibility',
        LAYER_LABEL_CLASS: 'label',
        LAYER_SELECTED_CLASS: 'selected',
        options: {
            offset: [
                0,
                0
            ],
            fontSize: 10,
            fontFamily: 'Tahoma'
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', this.CONTROL_LAYERS_CLASS, this.map.$container.controls);
                this.$container.style.top = this.options.offset[0] + 'px';
                this.$container.style.left = this.options.offset[1] + 'px';
                this.$container.style.fontSize = this.options.fontSize + 'pt';
                this.$container.style.fontFamily = this.options.fontFamily;
                this.createHeader();    // Create header
                // Create header
                this.createLayersContainer();    //Create layers container
                //Create layers container
                this.selectedLayerData = this.map.getLayers()[this.map.getLayers().length - 1];    // Initially select last layer by default
            }
        },
        reset: function () {
            var layers = this.map.getLayers(), i = 0;
            this.clearLayers();
            for (i = 0; i < layers.length; i++) {
                this.createLayer(layers[i]);
            }
            this.bindKeyEvents();
        },
        // ## Creates header
        // Creates control header
        createHeader: function () {
            var label = null, collapse = null;
            this.$headerContainer = T.DomUtil.create('div', this.CONTROL_LAYERS_HEADER_CLASS, this.$container);    //create container
            //create container
            this.$headerContainer.style.fontSize = this.options.fontSize + 'pt';
            this.$headerContainer.style.fontFamily = this.options.fontFamily;
            label = T.DomUtil.create('div', this.HEADER_LABEL_CLASS, this.$headerContainer);    //create label
            //create label
            T.DomUtil.html(label, 'Layers');
            collapse = T.DomUtil.create('div', this.HEADER_COLLAPSE_CLASS, this.$headerContainer);    //create collapse element
            // Add events
            //create collapse element
            // Add events
            T.DomEventUtil.on(this.$headerContainer, 'click', this.toggleLayers, this);
        },
        // ## Creates layers container
        // Creates layers container where all layers will be shown
        createLayersContainer: function () {
            this.$layersContainer = T.DomUtil.create('div', this.CONTROL_LAYERS_CONTAINER_CLASS, this.$container);
            this.$layersContainer.tabIndex = '1';
            this.$layersContainer.style.display = 'none';
        },
        /**
             * Creates and adds to DOM a layer UI for a given layer
             * @param {BaseLayer|TileLayer|VectorLayer|MarkersLayer|ImageLayer|PopupsLayer|WmsLayer} layer The layer for which to create the UI
             */
        createLayer: function (layer) {
            var layerContainer = null, radio = null, checkbox = null, label = null;
            layerContainer = T.DomUtil.create('div', this.LAYER_CONTAINER_CLASS, this.$layersContainer);    //Create radio - used for showing which layer is active
            //Create radio - used for showing which layer is active
            radio = T.DomUtil.create('input', this.LAYER_RADIO_CLASS);
            radio.type = 'radio';
            radio.name = 'active-layer';
            layerContainer.appendChild(radio);
            radio.checked = layer === this.selectedLayerData ? 'checked' : '';    //Create checkbox - used for layer visibility
            //Create checkbox - used for layer visibility
            checkbox = T.DomUtil.create('input', this.LAYER_VISIBLE_CLASS);
            checkbox.type = 'checkbox';
            layerContainer.appendChild(checkbox);
            checkbox.checked = layer.isVisible() === true ? 'checked' : '';    //Create layer label
            //Create layer label
            label = T.DomUtil.create('div', this.LAYER_LABEL_CLASS);
            T.DomUtil.html(label, layer.name);
            layerContainer.appendChild(label);    //Add events
            //Add events
            T.DomEventUtil.on(radio, 'click', this.onSelectLayer, this);
            T.DomEventUtil.on(checkbox, 'click', this.onSetLayerVisibility, this);
        },
        /**
             * Show / Hides the list of layers and adds collapse class to header element
             */
        toggleLayers: function () {
            var collapse = T.DomUtil.getNodesByClass(this.HEADER_COLLAPSE_CLASS)[0];
            if (!collapse) {
                return;
            }
            if (this.areLayersVisible()) {
                collapse.className = this.HEADER_COLLAPSE_CLASS + ' ' + this.HEADER_COLLAPSE_TRUE_CLASS;    // add correct class to collapse element
                // add correct class to collapse element
                T.DomEventUtil.off(this.$layersContainer, 'keydown', this.onKeydownHandler, this);    // unbind keyboard events
                // unbind keyboard events
                this.hideLayers();    // hide layers
            } else {
                collapse.className = this.HEADER_COLLAPSE_CLASS + ' ' + this.HEADER_COLLAPSE_FALSE_CLASS;    // add correct class to collapse element
                // add correct class to collapse element
                T.DomEventUtil.on(this.$layersContainer, 'keydown', this.onKeydownHandler, this);    // unbind keyboard events
                // unbind keyboard events
                this.showLayers();    //Focus Layers control
                //Focus Layers control
                this.$layersContainer.focus();
            }
        },
        /**
             * Displays the layers UI on the map
             */
        showLayers: function () {
            this.$layersContainer.style.display = 'block';
        },
        /**
             * Hides the layers UI
             */
        hideLayers: function () {
            this.$layersContainer.style.display = 'none';
        },
        areLayersVisible: function () {
            return this.$layersContainer.style.display !== 'none' ? true : false;
        },
        // ## Remove all layers
        // Removes all layers from layers control
        clearLayers: function () {
            while (this.$layersContainer.hasChildNodes()) {
                if (T.DomUtil.hasClass(this.$layersContainer.lastChild, this.LAYER_CONTAINER_CLASS)) {
                    this.$layersContainer.removeChild(this.$layersContainer.lastChild);
                }
            }
        },
        // ## Bind keyboard arrow events
        // Moves layer up or down depending on the specified arrow key
        bindKeyEvents: function () {
            T.DomEventUtil.off(document, 'keydown', this.onKeydownHandler, this);
            T.DomEventUtil.on(document, 'keydown', this.onKeydownHandler, this);
        },
        // ## Keyboard events handler
        // Handles UP/DOWN arrow keyboard events
        onKeydownHandler: function (evt) {
            if (evt.stopPropagation !== undefined) {
                evt.stopPropagation();
            } else {
                evt.cancelBubble = true;
            }
            if (!this.selectedLayerData) {
                return;
            }
            if (evt.keyCode === 38) {
                this.map.moveLayerDown(this.selectedLayerData);
            } else if (evt.keyCode === 40) {
                this.map.moveLayerUp(this.selectedLayerData);
            }
        },
        // ## Select layer
        // Save layer data and focus layers container
        onSelectLayer: function (evt) {
            // Save layer data
            var targetEelement = evt.srcElement === undefined ? evt.target : evt.srcElement;
            this.selectedLayerIndex = T.DomUtil.getNodeIndex(targetEelement.parentNode, this.$layersContainer);
            this.selectedLayerData = this.map.getLayers()[this.selectedLayerIndex];    //Focus Layers control
            //Focus Layers control
            this.$layersContainer.focus();
        },
        // ## Set layer visibility
        // Sets layer visibility
        onSetLayerVisibility: function (evt) {
            var layer = null, layerIndex = -1, targetEelement = evt.srcElement === undefined ? evt.target : evt.srcElement, layerVisible = targetEelement.checked;
            layerIndex = T.DomUtil.getNodeIndex(targetEelement.parentNode, this.$layersContainer);
            layer = this.map.getLayers()[layerIndex];
            layer.setVisible(layerVisible);
        },
        onAdd: function (map) {
            this.map = map;
            this.map.events.on('layer-add', this.reset, this);
            this.map.events.on('layer-remove', this.reset, this);
            this.map.events.on('layer-moved', this.reset, this);
            this.map.events.on('layer-hide', this.reset, this);
            this.map.events.on('layer-show', this.reset, this);
            this.initContainer();
            this.reset();
        },
        onRemove: function () {
            this.map.events.detach('layer-add', this.reset);
            this.map.events.detach('layer-remove', this.reset);
            this.map.events.detach('layer-moved', this.reset);
            T.DomUtil.remove(this.$container);
            this.map = null;
        }
    };    // Export
    // Export
    exports.LayersControl = LayersControl;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T */
(function (exports) {
    /**
         * TibcoMapControl class is responsible for displaying all the {TibcoLayer} type layers available in the map. Controls should be added using {Map}.
         *
         * @summary TibcoMapControl's constructor
         * @constructor true
         *
         * @param {Object} [options](#options) The options of the control
         *
         * @option {Array} offset The offset in pixels from the control's original position (default: `[0, 0]`)
         * @option {Number} fontSize The size of control's font (default: `10`)
         * @option {String} fontFamily The family of control's font (default: `'Tahoma'`)
         */
    var TibcoMapControl = function (options) {
        this.options = T.Util.defaults(options, this.options);
        this.attributions = [];
    };
    TibcoMapControl.prototype = {
        // Constants
        CONTROL_LAYERS_CLASS: 'tibco-control-tibco-map',
        CONTROL_LAYERS_HEADER_CLASS: 'header',
        CONTROL_LAYERS_CONTAINER_CLASS: 'layers',
        HEADER_LABEL_CLASS: 'label',
        HEADER_COLLAPSE_CLASS: 'collapse',
        HEADER_COLLAPSE_TRUE_CLASS: 'is-collapsed',
        HEADER_COLLAPSE_FALSE_CLASS: 'is-not-collapsed',
        LAYER_CONTAINER_CLASS: 'layer',
        LAYER_RADIO_CLASS: 'radio',
        LAYER_VISIBLE_CLASS: 'visibility',
        LAYER_LABEL_CLASS: 'label',
        LAYER_SELECTED_CLASS: 'selected',
        LAYER_OPTIONS_CONTAINER_CLASS: 'layer-options',
        LAYER_OPTIONS_GROUP_CLASS: 'layer-options-group',
        LAYER_OPTIONS_SELECT_CLASS: 'layer-options-select',
        LAYER_OPTION_CLASS: 'layer-option',
        options: {
            offset: [
                0,
                0
            ],
            fontSize: 10,
            fontFamily: 'Tahoma'
        },
        /*
             * Initialize control container.
             */
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', this.CONTROL_LAYERS_CLASS, this.map.$container.controls);
                this.$container.style.top = this.options.offset[0] + 'px';
                this.$container.style.right = this.options.offset[1] + 'px';
                this.$container.style.fontSize = this.options.fontSize + 'pt';
                this.$container.style.fontFamily = this.options.fontFamily;
                this.createHeader();
                this.createLayersContainer();
                this.createOptionsContainer();
            }
        },
        /*
             * Resets control.
             */
        reset: function () {
            var layers = this.getMapTibcoLayers(), i = 0;    // Remove layers
            // Remove layers
            this.clearContainer(this.$layersContainer, this.LAYER_CONTAINER_CLASS);
            for (i = 0; i < layers.length; i++) {
                this.createLayer(layers[i]);
            }
        },
        /*
             * Creates control header
             */
        createHeader: function () {
            var label = null, collapse = null;
            this.$headerContainer = T.DomUtil.create('div', this.CONTROL_LAYERS_HEADER_CLASS, this.$container);    //create container
            //create container
            this.$headerContainer.style.fontSize = this.options.fontSize + 'pt';
            this.$headerContainer.style.fontFamily = this.options.fontFamily;
            label = T.DomUtil.create('div', this.HEADER_LABEL_CLASS, this.$headerContainer);    //create label
            //create label
            T.DomUtil.html(label, 'Tibco Layers');
            collapse = T.DomUtil.create('div', this.HEADER_COLLAPSE_CLASS, this.$headerContainer);    //create collapse element
            // Add events
            //create collapse element
            // Add events
            T.DomEventUtil.on(this.$headerContainer, 'click', this.toggleContent, this);
        },
        /*
             * Creates layers container where all layers will be shown
             */
        createLayersContainer: function () {
            this.$layersContainer = T.DomUtil.create('div', this.CONTROL_LAYERS_CONTAINER_CLASS, this.$container);
            this.$layersContainer.tabIndex = '1';
            this.$layersContainer.style.display = 'none';
        },
        /*
             * Creates layer options container where all layer options will be shown
             */
        createOptionsContainer: function () {
            this.$layerOptionsContainer = T.DomUtil.create('div', this.LAYER_OPTIONS_CONTAINER_CLASS, this.$container);
            this.$layerOptionsContainer.tabIndex = '1';
            this.$layerOptionsContainer.style.display = 'none';
        },
        /*
             * Creates and adds to DOM a layer UI for a given layer
             */
        createLayer: function (layer) {
            var layerContainer = null, radio = null, label = null;
            layerContainer = T.DomUtil.create('div', this.LAYER_CONTAINER_CLASS, this.$layersContainer);    //Create radio - used for showing which layer is active
            //Create radio - used for showing which layer is active
            radio = T.DomUtil.create('input', this.LAYER_RADIO_CLASS);
            radio.type = 'radio';
            radio.name = 'active-layer';
            layerContainer.appendChild(radio);
            radio.checked = '';    //Create layer label
            //Create layer label
            label = T.DomUtil.create('div', this.LAYER_LABEL_CLASS);
            T.DomUtil.html(label, layer.options.name);
            layerContainer.appendChild(label);    //Add events
            //Add events
            T.DomEventUtil.on(radio, 'click', this.onSelectLayer, this);
        },
        /*
             * Creates the options lists (map type & map style) for the current layer.
             */
        createLayerOptions: function () {
            var i = 0, mapTypesContainer = null, mapStylesContainer = null, mapTypesOptionContainer = null, mapStylesOptionContainer = null, availableMapTypes = null, availableMapStyles = null, mapTypeLabelContainer = null, mapStyleLabelContainer = null, groupContainer = null;
            if (!this.selectedLayerData) {
                throw new Error('No layer selected!');
            }    // Remove all options
            // Remove all options
            this.clearContainer(this.$layerOptionsContainer, this.LAYER_OPTIONS_GROUP_CLASS);    // Make options container visible
            // Make options container visible
            this.$layerOptionsContainer.style.display = 'block';    // region MAP TYPE
            // Get all available map types
            // region MAP TYPE
            // Get all available map types
            availableMapTypes = this.selectedLayerData.getAvailableMapTypes();    // Create map type option group
            // Create map type option group
            groupContainer = T.DomUtil.create('div', this.LAYER_OPTIONS_GROUP_CLASS, this.$layerOptionsContainer);    // Create map type label
            // Create map type label
            mapTypeLabelContainer = T.DomUtil.create('span', this.LAYER_OPTIONS_SELECT_CLASS, groupContainer);
            T.DomUtil.html(mapTypeLabelContainer, 'Map type:');    // Create map types select
            // Create map types select
            mapTypesContainer = T.DomUtil.create('select', this.LAYER_OPTIONS_SELECT_CLASS, groupContainer);
            for (i = 0; i < availableMapTypes.length; i++) {
                mapTypesOptionContainer = T.DomUtil.create('option', this.LAYER_OPTION_CLASS);
                T.DomUtil.html(mapTypesOptionContainer, availableMapTypes[i]);
                mapTypesContainer.appendChild(mapTypesOptionContainer);    //Add events
                //Add events
                T.DomEventUtil.on(mapTypesContainer, 'change', this.onMapTypeChanged, this);
            }    // Set selected map type
            // Set selected map type
            mapTypesContainer.value = this.selectedLayerData.options.mapType;    // endregion
            // region MAP STYLE
            // Get all available map styles
            // endregion
            // region MAP STYLE
            // Get all available map styles
            availableMapStyles = this.selectedLayerData.getAvailableMapStyles();
            if (!availableMapStyles || availableMapStyles.length === 0) {
                return;
            }    // Create map style option group
            // Create map style option group
            groupContainer = T.DomUtil.create('div', this.LAYER_OPTIONS_GROUP_CLASS, this.$layerOptionsContainer);    // Create map style label
            // Create map style label
            mapStyleLabelContainer = T.DomUtil.create('span', this.LAYER_OPTIONS_SELECT_CLASS, groupContainer);
            T.DomUtil.html(mapStyleLabelContainer, 'Map style:');    // Create map types select
            // Create map types select
            mapStylesContainer = T.DomUtil.create('select', this.LAYER_OPTIONS_SELECT_CLASS, groupContainer);
            for (i = 0; i < availableMapStyles.length; i++) {
                mapStylesOptionContainer = T.DomUtil.create('option', this.LAYER_OPTION_CLASS);
                T.DomUtil.html(mapStylesOptionContainer, availableMapStyles[i]);
                mapStylesContainer.appendChild(mapStylesOptionContainer);    //Add events
                //Add events
                T.DomEventUtil.on(mapStylesContainer, 'change', this.onMapStyleChanged, this);
            }    // Set selected map type
            // Set selected map type
            mapStylesContainer.value = this.selectedLayerData.options.mapStyle;    // endregion
        },
        /*
             * Gets all the TibcoLayer type layers in the map.
             */
        getMapTibcoLayers: function () {
            var layers = this.map.getLayers(), tibcoLayers = [], i = 0;
            for (i = 0; i < layers.length; i++) {
                if (layers[i] instanceof T.TibcoLayer) {
                    tibcoLayers.push(layers[i]);
                }
            }
            return tibcoLayers;
        },
        /*
             * Show / Hides the list of layers and adds collapse class to header element
             */
        toggleContent: function () {
            var collapse = T.DomUtil.getNodesByClass(this.HEADER_COLLAPSE_CLASS)[0];
            if (!collapse) {
                return;
            }
            if (this.areLayersVisible()) {
                collapse.className = this.HEADER_COLLAPSE_CLASS + ' ' + this.HEADER_COLLAPSE_TRUE_CLASS;    // add correct class to collapse element
                // add correct class to collapse element
                this.hideContent();    // hide layers
            } else {
                collapse.className = this.HEADER_COLLAPSE_CLASS + ' ' + this.HEADER_COLLAPSE_FALSE_CLASS;    // add correct class to collapse element
                // add correct class to collapse element
                this.showContent();    //Focus Layers control
                //Focus Layers control
                this.$layersContainer.focus();
            }
        },
        /**
             * Displays the layers UI on the map
             */
        showContent: function () {
            this.$layersContainer.style.display = 'block';
            if (this.selectedLayerData) {
                this.$layerOptionsContainer.style.display = 'block';
            }
        },
        /**
             * Hides the layers UI
             */
        hideContent: function () {
            this.$layersContainer.style.display = 'none';
            this.$layerOptionsContainer.style.display = 'none';
        },
        areLayersVisible: function () {
            return this.$layersContainer.style.display !== 'none' ? true : false;
        },
        /*
             * Remove all elements in the specified container
             */
        clearContainer: function ($container, containerClass) {
            while ($container.hasChildNodes()) {
                if (T.DomUtil.hasClass($container.lastChild, containerClass)) {
                    $container.removeChild($container.lastChild);
                }
            }
        },
        onAdd: function (map) {
            this.map = map;
            this.tibcoLayers = this.getMapTibcoLayers();
            if (this.tibcoLayers.length === 0) {
                throw new Error('Map does not contain TibcoLayers. Cannot initialize this control!');
            }
            this.map.events.on('layer-add', this.reset, this);
            this.map.events.on('layer-remove', this.reset, this);
            this.map.events.on('layer-moved', this.reset, this);
            this.map.events.on('layer-hide', this.reset, this);
            this.map.events.on('layer-show', this.reset, this);
            this.initContainer();
            this.reset();
        },
        onRemove: function () {
            this.map.events.detach('layer-add', this.reset);
            this.map.events.detach('layer-remove', this.reset);
            this.map.events.detach('layer-moved', this.reset);
            T.DomUtil.remove(this.$container);
            this.map = null;
        },
        /*
             * Save layer data and create current layer's options
             */
        onSelectLayer: function (evt) {
            // Save layer data
            var targetEelement = evt.srcElement === undefined ? evt.target : evt.srcElement;
            this.selectedLayerIndex = T.DomUtil.getNodeIndex(targetEelement.parentNode, this.$layersContainer);
            this.selectedLayerData = this.tibcoLayers[this.selectedLayerIndex];
            this.createLayerOptions();    //Focus Layers control
            //Focus Layers control
            this.$layersContainer.focus();
        },
        /*
             * Sets the layer's map type and waits for the promise to be resolved in order to show the style options for the new map type.
             */
        onMapTypeChanged: function (evt) {
            var targetEelement = evt.srcElement === undefined ? evt.target : evt.srcElement, self = this;
            this.selectedLayerData.setMapType(targetEelement.value).then(function () {
                self.createLayerOptions();
            }, function () {
            });
        },
        /*
             * Sets the layer's new style and create sthe layer options.
             */
        onMapStyleChanged: function (evt) {
            var targetEelement = evt.srcElement === undefined ? evt.target : evt.srcElement;
            this.selectedLayerData.setMapStyle(targetEelement.value);
            this.createLayerOptions();
        }
    };    // Export
    // Export
    exports.TibcoMapControl = TibcoMapControl;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    /**
         * NavigationControl class is responsible for displaying map navigation UI. Controls should be added using {Map}
         *
         * @summary NavigationControl's constructor
         * @constructor true
         *
         * @param {Object} [options](#options) The options of the control
         *
         * @option {Array} offset The offset in pixels from the control's original position (default: `[0, 0]`)
         * @option {Number} panOffset The number of pixels to move the map when panning (default: `100`)
         * @option {Boolean} panControl If `true`, the pan control will be displayed (default: `true`)
         * @option {Boolean} zoomControl If `true`, the zoom control will be displayed (default: `true`)
         */
    var NavigationControl = function (options) {
        this.minHeight = 100;
        this.options = T.Util.defaults(options, this.options);
        this.height = !this.options.zoomRailHeight || this.options.zoomRailHeight < this.minHeight ? this.minHeight : this.options.zoomRailHeight;
    };
    NavigationControl.prototype = {
        CONTROL_NAVIGATION_CLASS: 'tibco-control-navigation',
        PAN_CONTROL_CLASS: 'pan-control',
        RESET_CLASS: 'reset',
        PAN_UP_CLASS: 'pan-up',
        PAN_DOWN_CLASS: 'pan-down',
        PAN_LEFT_CLASS: 'pan-left',
        PAN_RIGHT_CLASS: 'pan-right',
        ZOOM_CONTROL_CLASS: 'zoom-control',
        ZOOM_IN_CLASS: 'zoom-in',
        ZOOM_OUT_CLASS: 'zoom-out',
        ZOOM_SLIDER_CLASS: 'zoom-slider',
        ZOOM_RAIL_CLASS: 'zoom-rail',
        ZOOM_THUMB_CLASS: 'zoom-thumb',
        options: {
            offset: [
                0,
                0
            ],
            panOffset: 100,
            panControl: true,
            zoomControl: true
        },
        initContainer: function () {
            if (!this.$container) {
                this.$container = T.DomUtil.create('div', this.CONTROL_NAVIGATION_CLASS, this.map.$container.controls);
                this.$container.style.top = this.options.offset[0] + 'px';
                this.$container.style.left = this.options.offset[1] + 'px';
                this.initPanControl();
                this.initZoomControl();
            }
        },
        initPanControl: function () {
            if (!this.options.panControl) {
                return;
            }
            this.createPanControl();
        },
        initZoomControl: function () {
            if (!this.options.zoomControl) {
                return;
            }
            this.createZoomControl();
            this.setPositionForZoom(this.map.zoom);
            this.drag = new T.DragUtil(this.$zoomThumbContainer, this.$zoomThumbContainer, {
                limit: {
                    minX: 0,
                    maxX: 0,
                    minY: this.minY,
                    maxY: this.maxY
                }
            });
            this.drag.events.on('drag-end', this.onZoomThumbDragEnd, this);
            this.drag.enable();
        },
        // ## Creates DOM elements for pan control
        createPanControl: function () {
            this.$panContainer = T.DomUtil.create('div', this.PAN_CONTROL_CLASS, this.$container);    // UP
            // UP
            this.$panUpContainer = T.DomUtil.create('div', this.PAN_UP_CLASS, this.$panContainer);
            this.$panUpContainer.title = this.options.titles && this.options.titles.panUp ? this.options.titles.panUp : '';    // DOWN
            // DOWN
            this.$panDownContainer = T.DomUtil.create('div', this.PAN_DOWN_CLASS, this.$panContainer);
            this.$panDownContainer.title = this.options.titles && this.options.titles.panDown ? this.options.titles.panDown : '';    // LEFT
            // LEFT
            this.$panLeftContainer = T.DomUtil.create('div', this.PAN_LEFT_CLASS, this.$panContainer);
            this.$panLeftContainer.title = this.options.titles && this.options.titles.panLeft ? this.options.titles.panLeft : '';    // RIGHT
            // RIGHT
            this.$panRightContainer = T.DomUtil.create('div', this.PAN_RIGHT_CLASS, this.$panContainer);
            this.$panRightContainer.title = this.options.titles && this.options.titles.panRight ? this.options.titles.panRight : '';    // RESET
            // RESET
            if (!this.options.disableMapReset) {
                this.$resetContainer = T.DomUtil.create('div', this.RESET_CLASS, this.$panContainer);
                this.$resetContainer.title = this.options.titles && this.options.titles.reset ? this.options.titles.reset : '';
                T.DomEventUtil.on(this.$resetContainer, 'click', this.onReset, this);
            }    // Add listeners
            // Add listeners
            T.DomEventUtil.on(this.$panUpContainer, 'click', this.onPanUp, this);
            T.DomEventUtil.on(this.$panDownContainer, 'click', this.onPanDown, this);
            T.DomEventUtil.on(this.$panLeftContainer, 'click', this.onPanLeft, this);
            T.DomEventUtil.on(this.$panRightContainer, 'click', this.onPanRight, this);
        },
        // ## Creates DOM elements for zoom control
        createZoomControl: function () {
            this.$zoomContainer = T.DomUtil.create('div', this.ZOOM_CONTROL_CLASS, this.$container);    // ZOOM IN
            // ZOOM IN
            this.$zoomInContainer = T.DomUtil.create('div', this.ZOOM_IN_CLASS, this.$zoomContainer);
            this.$zoomInContainer.title = this.options.titles && this.options.titles.zoomIn ? this.options.titles.zoomIn : '';    // ZOOM OUT
            // ZOOM OUT
            this.$zoomOutContainer = T.DomUtil.create('div', this.ZOOM_OUT_CLASS, this.$zoomContainer);
            this.$zoomOutContainer.title = this.options.titles && this.options.titles.zoomOut ? this.options.titles.zoomOut : '';    //ZOOM THUMB & RAIL
            //ZOOM THUMB & RAIL
            this.$zoomSliderContainer = T.DomUtil.create('div', this.ZOOM_SLIDER_CLASS, this.$zoomContainer);
            this.$zoomRailContainer = T.DomUtil.create('div', this.ZOOM_RAIL_CLASS, this.$zoomSliderContainer);
            this.$zoomThumbContainer = T.DomUtil.create('div', this.ZOOM_THUMB_CLASS, this.$zoomRailContainer);    // Adjust heights
            // Adjust heights
            this.$zoomSliderContainer.style.height = this.height + 'px';
            if (this.minZoom === 0) {
                this.$zoomSliderContainer.style.height = this.height + this.$zoomThumbContainer.clientHeight + 'px';
            } else {
                this.$zoomSliderContainer.style.height = this.height + this.$zoomThumbContainer.clientHeight - this.zoomSkipInterval + 'px';
            }
            this.$zoomContainer.style.height = this.$zoomSliderContainer.clientHeight + this.$zoomInContainer.clientHeight + this.$zoomOutContainer.clientHeight + 'px';    //Set vertical bounderies
            //Set vertical bounderies
            this.minY = 0;
            if (this.minZoom === 0) {
                this.maxY = this.height;
            } else {
                this.maxY = this.height - this.zoomSkipInterval;
            }    // Add listeners
            // Add listeners
            T.DomEventUtil.on(this.$zoomInContainer, 'click', this.onZoomIn, this);
            T.DomEventUtil.on(this.$zoomOutContainer, 'click', this.onZoomOut, this);
            T.DomEventUtil.on(this.$zoomRailContainer, 'click', this.onRailClick, this);
        },
        getBottomPosition: function (topPosition) {
            return this.height - topPosition;
        },
        setPositionForZoom: function (zoom) {
            var position = null, snapPosition = null;
            zoom = zoom !== undefined ? zoom : this.map.zoom;
            position = this.getBottomPosition(zoom * this.zoomSkipInterval);
            T.DomUtil.position(this.$zoomThumbContainer, new T.Point(0, position));
        },
        setZoomForPosition: function (position) {
            var newZoom = null;
            position = this.getBottomPosition(position);
            if (position === this.minY) {
                newZoom = this.minZoom;
            } else {
                newZoom = Math.round(position / this.zoomSkipInterval);
            }
            this.map.setZoom(newZoom);
        },
        reset: function () {
            if (this.$container) {
                T.DomUtil.remove(this.$container);
                this.$container = null;
            }    // Save map's min/max zoom & number of zoom levels
            // Save map's min/max zoom & number of zoom levels
            this.minZoom = this.map.options.minZoom;
            this.maxZoom = this.map.options.maxZoom;
            if (this.minZoom === 0) {
                this.zoomSkipInterval = this.height / this.maxZoom;
            } else {
                this.zoomSkipInterval = this.height / (this.maxZoom - this.minZoom + 1);
            }
            this.initContainer();
        },
        // HANDLERS
        onPanUp: function () {
            var p = new T.Point(0, this.options.panOffset);
            this.map.panBy(p);
        },
        onPanDown: function () {
            var p = new T.Point(0, -this.options.panOffset);
            this.map.panBy(p);
        },
        onPanLeft: function () {
            var p = new T.Point(this.options.panOffset, 0);
            this.map.panBy(p);
        },
        onPanRight: function () {
            var p = new T.Point(-this.options.panOffset, 0);
            this.map.panBy(p);
        },
        onReset: function () {
            this.map.resetView(this.map.options.center, this.map.options.zoom);
        },
        onZoomIn: function () {
            this.map.setZoom(this.map.zoom + 1);
        },
        onZoomOut: function () {
            this.map.setZoom(this.map.zoom - 1);
        },
        onZoomThumbDragEnd: function () {
            var thumbPosition = T.DomUtil.position(this.$zoomThumbContainer).y;
            this.setZoomForPosition(thumbPosition);
        },
        onRailClick: function (evt) {
            var position = null, targetEelement = null;
            targetEelement = evt.srcElement === undefined ? evt.target : evt.srcElement;
            if (targetEelement.className === this.ZOOM_RAIL_CLASS) {
                position = evt.offsetY === undefined ? evt.layerY : evt.offsetY;
                this.setZoomForPosition(position);
            }
        },
        onAdd: function (map) {
            this.map = map;    // Add handlers
            // Add handlers
            this.map.events.on('view-reset', this.setPositionForZoom, this);
            this.map.events.on('zoom-interval-change', this.reset, this);
            this.reset();
        },
        onRemove: function () {
            this.map.events.detach('view-reset', this.setPositionForZoom);
            this.map.events.detach('zoom-interval-change', this.reset);
            T.DomUtil.remove(this.$container);
            this.map = null;
        }
    };    // Export
    // Export
    exports.NavigationControl = NavigationControl;
}(T));    // > SvgCircle.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > GeoJson to SVG  class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
// > SvgCircle.js 0.0.1
// > https://github.com/Maporama/TibcoMaps
// >
// > GeoJson to SVG  class for TibcoMap engine.
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global window, T, document */
(function (exports) {
    var GeoJsonImporter = function (geoJson) {
        this.geoJson = geoJson;
        this.geometries = [];
        this.readPart(geoJson);
    };
    GeoJsonImporter.prototype = {
        readPart: function (obj) {
            var i, point;
            switch (obj.type) {
            case 'Point':
                point = new T.Circle(obj.coordinates, 3, {
                    radiusMeasure: 'pixel',
                    style: {
                        color: '#ff0000',
                        weight: 1
                    },
                    reverseCoordinates: true
                });
                this.geometries.push(point);
                break;
            case 'MultiPoint':
                for (i = 0; i < obj.coordinates.length; i++) {
                    point = new T.Circle(obj.coordinates[i], 3, {
                        radiusMeasure: 'pixel',
                        style: {
                            color: '#ff0000',
                            weight: 1
                        },
                        reverseCoordinates: true
                    });
                    this.geometries.push(point);
                }
                break;
            case 'LineString':
                var lineString = new T.Polyline(obj.coordinates, { reverseCoordinates: true });
                this.geometries.push(lineString);
                break;
            case 'MultiLineString':
                var multiLineString = new T.MultiPolyline(obj.coordinates, { reverseCoordinates: true });
                this.geometries.push(multiLineString);
                break;
            case 'Polygon':
                var polygon = new T.Polygon(obj.coordinates, { reverseCoordinates: true });
                this.geometries.push(polygon);
                break;
            case 'MultiPolygon':
                var multiPolygon = new T.MultiPolygon(obj.coordinates, { reverseCoordinates: true });
                this.geometries.push(multiPolygon);
                break;
            case 'GeometryCollection':
                for (i = 0; i < obj.geometries.length; i++) {
                    this.readPart(obj.geometries[i]);
                }
                break;
            case 'Feature':
                this.readPart(obj.geometry);
                break;
            case 'FeatureCollection':
                for (i = 0; i < obj.features.length; i++) {
                    this.readPart(obj.features[i]);
                }
                break;
            }
        },
        addTo: function (layer) {
            var i;
            for (i = 0; i < this.geometries.length; i++) {
                layer.addGeometry(this.geometries[i]);
            }
        }
    };    // Export
    // Export
    exports.GeoJsonImporter = GeoJsonImporter;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T */
(function (exports) {
    var GeocoderResult = function (resultObject) {
        if (!resultObject) {
            throw new Error('Please specify the geocoder result!');
        }    // Set location
        // Set location
        if (resultObject.Location && resultObject.Location.Latitude && resultObject.Location.Longitude) {
            this.location = new T.LatLng(resultObject.Location.Latitude, resultObject.Location.Longitude);
        }    // Set bounding box
        // Set bounding box
        if (resultObject.BoundingBox && resultObject.BoundingBox.NorthEast && resultObject.BoundingBox.NorthEast.Latitude && resultObject.BoundingBox.NorthEast.Longitude && resultObject.BoundingBox.SouthWest && resultObject.BoundingBox.SouthWest.Latitude && resultObject.BoundingBox.SouthWest.Longitude) {
            this.bounds = new T.LatLngBounds(new T.LatLng(resultObject.BoundingBox.SouthWest.Latitude, resultObject.BoundingBox.SouthWest.Longitude), new T.LatLng(resultObject.BoundingBox.NorthEast.Latitude, resultObject.BoundingBox.NorthEast.Longitude));
        }    // Set city
        // Set city
        if (resultObject.City) {
            this.city = resultObject.City;
        }    // Set country
        // Set country
        if (resultObject.Country) {
            this.country = resultObject.Country;
        }    // Set entire address
        // Set entire address
        if (resultObject.EntireAddress) {
            this.address = resultObject.EntireAddress;
        }    // Set state
        // Set state
        if (resultObject.State) {
            this.state = resultObject.State;
        }    // Set street
        // Set street
        if (resultObject.Street) {
            this.street = resultObject.Street;
        }    // Set street name
        // Set street name
        if (resultObject.StreetName) {
            this.streetName = resultObject.StreetName;
        }    // Set street number
        // Set street number
        if (resultObject.StreetNb) {
            this.streetNumber = resultObject.StreetNb;
        }    // Set street type
        // Set street type
        if (resultObject.StreetType) {
            this.streetType = resultObject.StreetType;
        }    // Set zip
        // Set zip
        if (resultObject.Zip) {
            this.zip = resultObject.Zip;
        }    // Set geocoding level
        // Set geocoding level
        if (resultObject.Level) {
            this.level = resultObject.Level;
        }    // Set geocoding level info
        // Set geocoding level info
        if (resultObject.LevelInfo) {
            this.levelInfo = resultObject.LevelInfo;
        }    // Set geocoding score
        // Set geocoding score
        if (resultObject.Score) {
            this.score = resultObject.Score;
        }
    };
    GeocoderResult.prototype = {
        /**
             * {LatLng} - The location.
             */
        location: null,
        /**
             * {LatLngBounds} - The bounds.
             */
        bounds: null,
        /**
             * {String} - The country.
             */
        country: null,
        /**
             * {String} - The city.
             */
        city: null,
        /**
             * {String} - The entire address.
             */
        address: null,
        /**
             * {String} - The state.
             */
        state: null,
        /**
             * {String} - The street.
             */
        street: null,
        /**
             * {String} - The street name.
             */
        streetName: null,
        /**
             * {String} - The street number.
             */
        streetNumber: null,
        /**
             * {String} - The street type.
             */
        streetType: null,
        /**
             * {String} - The zip.
             */
        zip: null,
        /**
             * {String} - The geocoding level.*The higher the more accurate.*
             */
        level: null,
        /**
             * {String} - The Geocoding information.
             */
        levelInfo: null,
        /**
             * {String} - The geocoding score. *(percentage, 100 means 100% accuracy).*
             */
        score: null
    };
    exports.GeocoderResult = GeocoderResult;
}(T));    /*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T */
/*jslint vars: true, plusplus: true, devel: true, nomen: true, indent: 4, maxerr: 50 */
/*global T */
(function (exports) {
    /**
         * The geocoder object is responsible for geocoding and reverse geocoding data.
         *
         * @constructor true
         * @summary The geocoder's constructor
         *
         * @param {String} customer The customer name.
         * @param {String} key The authorization key necessary for using the [Geocoder].
         */
    var Geocoder = function (customer, key, url) {
        if (!customer || !key) {
            throw new Error('Please set the geocoder customer and key!');
        }
        this.customer = customer;
        this.key = key;
        this.url = url ? url : T.settings.geoWebServices;
    };
    Geocoder.prototype = {
        /**
             * Geocodes data.
             *
             * @option {String} country The country name or country code. *Mandatory!*
             * @option {String} city The city. *'city' or 'zip' must be set!*
             * @option {String} zip The zip. *'city' or 'zip' must be set!*
             * @option {String} state The state. *Can be set for better accuracy.*
             * @option {String} street The street. *Can be set for better accuracy.*
             *
             * @param {Object} [options](#options) The options to set for geocoding.
             * @return {GeocoderResult} Array of geocoder results.
             * @example examples/docs/api/geocoder/geocode 100% 500
             */
        geocode: function (options) {
            var url = null, promise = new T.Promise();    // Check mandatory customer & authorization key
            // Check mandatory customer & authorization key
            if (!this.customer || !this.key) {
                throw new Error('Please set the geocoder customer and key!');
            }    // Check country option, mandatory for geocoding
            // Check country option, mandatory for geocoding
            if (!options || !options.country) {
                throw new Error('Please set the country!');
            }    // Check that at least one of the following options are specified: city or zip
            // Check that at least one of the following options are specified: city or zip
            if (!options.city && !options.zip) {
                throw new Error('Please set one of the following options, necessary in order to geocode: city or zip!');
            }    // Set URL
            // Set URL
            url = this.url;    // Setup request
            // Setup request
            var cfg = {
                method: 'GET',
                withCredentials: false,
                on: {
                    success: function (result) {
                        var resultObject = JSON.parse(result), geocoderResults = [], i = 0;    // Loop & convert to proper format
                        // Loop & convert to proper format
                        for (i = 0; i < resultObject.length; i++) {
                            geocoderResults.push(new T.GeocoderResult(resultObject[i]));
                        }    // Resolve promise
                        // Resolve promise
                        promise.resolve(geocoderResults);
                    },
                    error: function (errorObject) {
                        promise.reject(errorObject);
                    }
                }
            };    // Add customer
            // Add customer
            url += this.customer;    // Add coder path
            // Add coder path
            url += '/coder.json';    // Add geocoder
            // Add geocoder
            url += '?geocoder=maporama';    // Add authorization key
            // Add authorization key
            url += '&maporamakey=' + this.key;    // Add country
            // Add country
            url += '&country=' + options.country;    // Add city
            // Add city
            if (options.city) {
                url += '&city=' + options.city;
            }    // Add zip
            // Add zip
            if (options.zip) {
                url += '&zip=' + options.zip;
            }    // Add state
            // Add state
            if (options.state) {
                url += '&state=' + options.state;
            }    // Add street
            // Add street
            if (options.street) {
                url += '&street=' + options.street;
            }    // Execute xhr
            // Execute xhr
            new T.Ajax().send(url, cfg);
            return promise;
        },
        /**
             * Reverse geocodes data.
             *
             * @param {LatLng} latLngObject The latitude and longitude to reverse geocode.
             * @return {GeocoderResult} Array of geocoder results.
             * @example examples/docs/api/geocoder/reverse-geocode 100% 500
             */
        reverseGeocode: function (latLngObject) {
            var url = null, promise = new T.Promise();    // Check mandatory customer & authorization key
            // Check mandatory customer & authorization key
            if (!this.customer || !this.key) {
                throw new Error('Please set the geocoder customer and key!');
            }
            if (!latLngObject || !latLngObject.lat || !latLngObject.lng) {
                throw new Error('Please specify the location!');
            }    // Set URL
            // Set URL
            url = this.url;    // Setup request
            // Setup request
            var cfg = {
                method: 'GET',
                withCredentials: false,
                on: {
                    success: function (result) {
                        var resultObject = JSON.parse(result), geocoderResults = [], i = 0;    // Loop & convert to proper format
                        // Loop & convert to proper format
                        for (i = 0; i < resultObject.length; i++) {
                            geocoderResults.push(new T.GeocoderResult(resultObject[i]));
                        }    // Resolve promise
                        // Resolve promise
                        promise.resolve(geocoderResults);
                    },
                    error: function (errorObject) {
                        promise.reject(errorObject);
                    }
                }
            };    // Add customer
            // Add customer
            url += this.customer;    // Add coder path
            // Add coder path
            url += '/coder.json';    // Add geocoder
            // Add geocoder
            url += '?geocoder=maporama';    // Add authorization key
            // Add authorization key
            url += '&maporamakey=' + this.key;    // Add latitude
            // Add latitude
            url += '&latitude=' + latLngObject.lat;    // Add longitude
            // Add longitude
            url += '&longitude=' + latLngObject.lng;    // Execute xhr
            // Execute xhr
            new T.Ajax().send(url, cfg);
            return promise;
        }
    };
    exports.Geocoder = Geocoder;
}(T));
export default T;