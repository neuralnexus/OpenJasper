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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: FormatModel.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone");

    var POSITIVE_NUMBER_FORMAT = "###0";

    var currencyMap = {
        "LOCALE_SPECIFIC": "\u00A4",
        "USD": "\u0024",
        "GBP": "\u00A3",
        "EUR": "\u20AC",
        "YEN": "\u00A5"
    };

    return Backbone.Model.extend({
        defaults: function() {
            return {
                "backgroundColor": undefined,
                "align": undefined,
                "font": {
                    "name": undefined,
                    "size": 11,
                    "bold": false,
                    "italic": false,
                    "underline": false,
                    "color": "000000"
                }
            }
        },

        initialize: function(attrs, options) {
            options || (options = {});

            this.dataType = options.dataType;
        },

        parse: function(jiveObj) {
            var res = {
                align: jiveObj.fontHAlign.toLowerCase(),
                backgroundColor: jiveObj.mode === "Opaque" ? jiveObj.fontBackColor : "transparent",
                font: {
                    "name": jiveObj.fontName,
                    "size": parseFloat(jiveObj.fontSize),
                    "italic": jiveObj.fontItalic,
                    "bold": jiveObj.fontBold,
                    "underline": jiveObj.fontUnderline,
                    "color": jiveObj.fontColor
                }
            };

            if (jiveObj.formatPattern) {
                if (this.dataType === "numeric") {
                    var pattern = jiveObj.formatPattern,
                        precisionRegexp = new RegExp("0\\.(0+)"),
                        grouping = false,
                        percentage = false,
                        precision = 0,
                        negativeFormat = "-" + POSITIVE_NUMBER_FORMAT,
                        currency = null;

                    if (pattern.indexOf(",") > -1) {
                        grouping = true;
                        pattern = pattern.replace(/,/g, "");
                    }

                    if (pattern.indexOf("%") > -1) {
                        percentage = true;
                        pattern = pattern.replace(/%/g, "");
                    }

                    if (pattern.indexOf("\u00A4") > -1) {
                        currency = "LOCALE_SPECIFIC";
                        pattern = pattern.replace(/\u00A4/g, "");
                    } else if (pattern.indexOf("\u0024") > -1) {
                        currency = "USD";
                        pattern = pattern.replace(/\u0024/g, "");
                    } else if (pattern.indexOf("\u00A3") > -1) {
                        currency = "GBP";
                        pattern = pattern.replace(/\u00A3/g, "");
                    } else if (pattern.indexOf("\u20AC") > -1) {
                        currency = "EUR";
                        pattern = pattern.replace(/\u20AC/g, "");
                    } else if (pattern.indexOf("\u00A5") > -1) {
                        currency = "YEN";
                        pattern = pattern.replace(/\u00A5/g, "");
                    }

                    pattern = pattern.replace(/\s/g, "");

                    var precisionTestResult = precisionRegexp.exec(pattern);

                    if (precisionTestResult && precisionTestResult.length == 2) {
                        precision = precisionTestResult[1].length;
                        pattern = pattern.replace(new RegExp("\\." + Array(precision+1).join("0"), "g"), "");
                    }

                    var parts = pattern.split(";");

                    if (parts && parts.length === 2) {
                        negativeFormat = parts[1];
                    }

                    res.pattern = {
                        negativeFormat: negativeFormat,
                        currency: currency,
                        precision: precision,
                        grouping: grouping,
                        percentage: percentage
                    }
                }else{
                    res.pattern = jiveObj.formatPattern;
                    if (res.pattern && this.dataType === "datetime" || this.dataType === "time") {
                        if (res.pattern.indexOf("hide") !== -1 || res.pattern.indexOf("medium") !== -1) {
                            //workaround for http://jira.jaspersoft.com/browse/JRS-1617 and http://jira.jaspersoft.com/browse/JRS-1626
                            delete res.pattern;
                        }
                    }
                }
            }

            return res;
        },

        toJiveFormat: function() {
            if (!this.has("pattern")) {
                return "";
            } else {
                if (this.dataType !== "numeric") {
                    return this.get("pattern");
                } else {
                    var patternObj = this.get("pattern"),
                        positivePart = POSITIVE_NUMBER_FORMAT,
                        negativePart = patternObj.negativeFormat || ("-" + POSITIVE_NUMBER_FORMAT);

                    if (patternObj.percentage) {
                        positivePart += " %";
                        negativePart += " %";
                    }

                    if (patternObj.currency && currencyMap[patternObj.currency]) {
                        positivePart = currencyMap[patternObj.currency] + " " + positivePart;
                        negativePart = currencyMap[patternObj.currency] + " " + negativePart;
                    }

                    if (patternObj.grouping) {
                        var index = positivePart.indexOf("#");
                        positivePart = positivePart.slice(0, index) + "#," + positivePart.slice(index+1);
                        index = negativePart.indexOf("#");
                        negativePart = negativePart.slice(0, index) + "#," + negativePart.slice(index+1);
                    }

                    if(patternObj.precision) {
                        var index = positivePart.indexOf("##0");
                        positivePart = positivePart.slice(0, index) + "##0" + ("." + Array(patternObj.precision+1).join("0")) + positivePart.slice(index+3);
                        index = negativePart.indexOf("##0");
                        negativePart = negativePart.slice(0, index) + "##0" + ("." + Array(patternObj.precision+1).join("0")) + negativePart.slice(index+3);
                    }

                    return (positivePart + ";" + negativePart);
                }
            }
        }
    });
});