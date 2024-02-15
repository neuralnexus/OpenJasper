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
 * @version: $Id: jasmine.ext.js 47331 2014-07-18 09:13:06Z kklein $
 */

if (jasmine) {
    //resets calls sequence automatically after every test
    jasmine.Env.prototype.baseIt = jasmine.Env.prototype.it;

    jasmine.Env.prototype.it = function () {
        jasmine.Env.prototype.baseIt.apply(this, arguments);
        jasmine.callsSequence = [];
        if (!jasmine.callsSequence.indexOf) {
            jasmine.callsSequence.indexOf = function (element) {
                for (var i = 0; i < this.length; i++) {
                    if (this[i] === element) {
                        return i;
                    }
                }
                return -1;
            }
        }
    }

    jasmine.baseCreateSpy = jasmine.createSpy;

    jasmine.createSpy = function (name) {
        if (!jasmine.callsSequence) jasmine.callsSequence = [];
        if (jasmine.callsSequence.indexOf(name) < 0)
            jasmine.callsSequence.push(name);
        return jasmine.baseCreateSpy(name);
    }

    jasmine.isIE = function () {
        return navigator.appName == "Microsoft Internet Explorer";
    }

    jasmine.isOldIE = function () {
        if (this.isIE()) {
            var version = navigator.appVersion.split("MSIE");
            version = parseFloat(version[1]);
            return version < 9;
        }
    }

    // jasmine-jquery.js (initialization)
    var readTempaltes = function () {
        return jasmine.getFixtures().proxyCallTo_('read', arguments);
    };

    var preloadTemplates = function () {
        jasmine.getFixtures().proxyCallTo_('preload', arguments);
    };

    var loadTemplates = function () {
        jasmine.getFixtures().proxyCallTo_('load', arguments);
    };

    var setTemplates = function () {
        if (arguments.length){
            var fixture = "";
            for (var i = 0, l =  arguments.length; i < l; i++){
                fixture += arguments[i]
            }
            jasmine.getFixtures().set(fixture);
        }
    };

    var sandbox = function (attributes) {
        return jasmine.getFixtures().sandbox(attributes);
    };

    var spyOnEvent = function (selector, eventName) {
        jasmine.JQuery.events.spyOn(selector, eventName);
    }

    jasmine.getFixtures = function () {
        return jasmine.currentFixtures_ = jasmine.currentFixtures_ || new jasmine.Fixtures();
    };

    jasmine.Fixtures = function () {
        this.containerId = 'jasmine-fixtures';
        this.fixturesCache_ = {};
        this.fixturesPath = 'spec/javascripts/fixtures';
    };

    jasmine.Fixtures.prototype.set = function (html) {
        this.cleanUp();
        this.createContainer_(html);
    };

    jasmine.Fixtures.prototype.preload = function () {
        this.read.apply(this, arguments);
    };

    jasmine.Fixtures.prototype.load = function () {
        this.cleanUp();
        this.createContainer_(this.read.apply(this, arguments));
    };

    jasmine.Fixtures.prototype.read = function () {
        var htmlChunks = [];

        var fixtureUrls = arguments;
        for (var urlCount = fixtureUrls.length, urlIndex = 0; urlIndex < urlCount; urlIndex++) {
            htmlChunks.push(this.getFixtureHtml_(fixtureUrls[urlIndex]));
        }

        return htmlChunks.join('');
    };

    jasmine.Fixtures.prototype.clearCache = function () {
        this.fixturesCache_ = {};
    };

    jasmine.Fixtures.prototype.cleanUp = function () {
        jQuery('#' + this.containerId).remove();
    };

    jasmine.Fixtures.prototype.sandbox = function (attributes) {
        var attributesToSet = attributes || {};
        return jQuery('<div id="sandbox" />').attr(attributesToSet);
    };

    jasmine.Fixtures.prototype.createContainer_ = function (html) {
        var container;
        if (html instanceof jQuery) {
            container = jQuery('<div id="' + this.containerId + '" />');
            container.html(html);
        } else {
            container = '<div id="' + this.containerId + '">' + html + '</div>'
        }
        jQuery('body').append(container);

    };
    jasmine.Fixtures.prototype.appendToContainer_ = function (html) {
        var container = jQuery("#" + this.containerId);
        var child = jQuery('<div/>');

        if (container.length === 0) {
            container = jQuery('<div id="' + this.containerId + '" />');
            jQuery('body').append(container);
        }
        if (html instanceof jQuery) {
            child.html(html);
        } else {
            child = '<div>' + html + '</div>'
        }
        container.append(container);
    };

    jasmine.Fixtures.prototype.getFixtureHtml_ = function (url) {
        if (typeof this.fixturesCache_[url] == 'undefined') {
            this.loadFixtureIntoCache_(url);
        }
        return this.fixturesCache_[url];
    };

    jasmine.Fixtures.prototype.loadFixtureIntoCache_ = function (relativeUrl) {
        var localDataContainer = jQuery('#_' + relativeUrl.replace(".","-")); // standalone generation specific
        var localData = localDataContainer.html();
        if (localData) {
            this.fixturesCache_[relativeUrl] = localData;
            localDataContainer.remove(); // remove base to allow reapplying
        }
        else {
            var self = this;
            var url = this.fixturesPath.match('/$') ? this.fixturesPath + relativeUrl : this.fixturesPath + '/' + relativeUrl;
            jQuery.ajax({
                async:false, // must be synchronous to guarantee that no tests are run before fixture is loaded
                cache:false,
                dataType:'html',
                url:url,
                success:function (data) {
                    self.fixturesCache_[relativeUrl] = data;
                },
                error:function (jqXHR, status, errorThrown) {
                    throw Error('Fixture could not be loaded: ' + url + ' (status: ' + status + ', message: ' + errorThrown.message + ')');
                }
            });
        }
    };

    jasmine.Fixtures.prototype.proxyCallTo_ = function (methodName, passedArguments) {
        return this[methodName].apply(this, passedArguments);
    };


    jasmine.JQuery = function () {
    };

    jasmine.JQuery.browserTagCaseIndependentHtml = function (html) {
        return jQuery('<div/>').append(html).html();
    };

    jasmine.JQuery.elementToString = function (element) {
        return jQuery('<div />').append(element.clone()).html();
    };

    jasmine.JQuery.matchersClass = {};

    (function (namespace) {
        var data = {
            spiedEvents:{},
            handlers:[]
        };

        namespace.events = {
            spyOn:function (selector, eventName) {
                var handler = function (e) {
                    data.spiedEvents[[selector, eventName]] = e;
                };
                jQuery(selector).bind(eventName, handler);
                data.handlers.push(handler);
            },

            wasTriggered:function (selector, eventName) {
                return !!(data.spiedEvents[[selector, eventName]]);
            },

            cleanUp:function () {
                data.spiedEvents = {};
                data.handlers = [];
            }
        }
    })(jasmine.JQuery);

    (function () {
        var jQueryMatchers = {
            toHaveClass:function (className) {
                return this.actual.hasClass(className);
            },

            toBeVisible:function () {
                return this.actual.is(':visible');
            },

            toBeHidden:function () {
                return this.actual.is(':hidden');
            },

            toBeSelected:function () {
                return this.actual.is(':selected');
            },

            toBeChecked:function () {
                return this.actual.is(':checked');
            },

            toBeEmpty:function () {
                return this.actual.is(':empty');
            },

            toExist:function () {
                return this.actual.size() > 0;
            },

            toHaveAttr:function (attributeName, expectedAttributeValue) {
                return hasProperty(this.actual.attr(attributeName), expectedAttributeValue);
            },

            toHaveId:function (id) {
                return this.actual.attr('id') == id;
            },

            toHaveHtml:function (html) {
                return this.actual.html() == jasmine.JQuery.browserTagCaseIndependentHtml(html);
            },

            toHaveText:function (text) {
                if (text && jQuery.isFunction(text.test)) {
                    return text.test(this.actual.text());
                } else {
                    return this.actual.text() == text;
                }
            },

            toHaveValue:function (value) {
                return this.actual.val() == value;
            },

            toHaveData:function (key, expectedValue) {
                return hasProperty(this.actual.data(key), expectedValue);
            },

            toBe:function (selector) {
                return this.actual.is(selector);
            },

            toContain:function (selector) {
                return this.actual.find(selector).size() > 0;
            },

            toBeDisabled:function (selector) {
                return this.actual.is(':disabled');
            },

            // tests the existence of a specific event binding
            toHandle:function (eventName) {
                var events = this.actual.data("events");
                return events && events[eventName].length > 0;
            },

            // tests the existence of a specific event binding + handler
            toHandleWith:function (eventName, eventHandler) {
                var stack = this.actual.data("events")[eventName];
                var i;
                for (i = 0; i < stack.length; i++) {
                    if (stack[i].handler == eventHandler) {
                        return true;
                    }
                }
                return false;
            }
        };

        var hasProperty = function (actualValue, expectedValue) {
            if (expectedValue === undefined) {
                return actualValue !== undefined;
            }
            return actualValue == expectedValue;
        };

        var bindMatcher = function (methodName) {
            var builtInMatcher = jasmine.Matchers.prototype[methodName];

            jasmine.JQuery.matchersClass[methodName] = function () {
                if (this.actual instanceof jQuery) {
                    var result = jQueryMatchers[methodName].apply(this, arguments);
                    this.actual = jasmine.JQuery.elementToString(this.actual);
                    return result;
                }

                if (builtInMatcher) {
                    return builtInMatcher.apply(this, arguments);
                }

                return false;
            };
        };

        for (var methodName in jQueryMatchers) {
            bindMatcher(methodName);
        }
    })();

    jasmine.getFixtures().fixturesPath = '/test/templates/';
}