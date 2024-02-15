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
 * @author: Pavel Savushchyk
 * @version: $Id$
 */

define(function(require) {
    "use strict"

    var LoggerManager = require("common/logging/LoggerManager"),
        Level = require("common/logging/Level"),
        loggingLevels = require("common/enum/loggingLevels"),
        ConsoleAppender = require("common/logging/appender/ConsoleAppender");

    describe("LoggerManager", function() {

        it("has exists", function() {
            expect(LoggerManager).toBeDefined();
        });

        it("should create logger instance", function(){
            var logger = new LoggerManager({});

            expect(logger instanceof LoggerManager).toBeTruthy();
        });

        describe("create default logger instance", function() {

            var logger = new LoggerManager({});

            it("should be disabled", function(){
                expect(logger.get("enabled")).toBeFalsy();
            });
            xit("should create Level instance with error logging level", function(){
                expect(logger.get("level") instanceof Level).toBeTruthy();
                expect(logger.get("level").toString()).toBe("ERROR");
            });
            it("should create empty object for appenders", function(){
                expect(logger.get("appenders")).toEqual({});
            });
            it("should create empty object for loggers", function(){
                expect(logger.get("loggers")).toEqual({});
            });

        });

        describe("process options", function() {

            // Enable / disable
            it("should set enabled property to true", function(){
                var options = {
                    enabled: true
                };
                var logger = new LoggerManager(options);

                expect(logger.get("enabled")).toBeTruthy();
            });
            it("should set enabled property to false", function(){
                var options = {
                    enabled: false
                };
                var logger = new LoggerManager(options);

                expect(logger.get("enabled")).toBeFalsy();
            });

            // levels
            for (var i in loggingLevels) {
                if (loggingLevels.hasOwnProperty(i)) {

                    (function(levelName){
                        it("should set level to " + levelName, function(){
                            var options = {
                                level: levelName.toLowerCase()
                            };
                            var logger = new LoggerManager(options);

                            expect(logger.get("level").toLowerCase()).toBe(levelName.toLowerCase());
                        });
                    })(i);

                }
            }

            // appenders
            it("should add console appender instance", function(){
                var options = {
                    appenders: ["console"]
                };
                var logger = new LoggerManager(options);

                expect(logger.get("appenderInstances").console instanceof ConsoleAppender).toBeTruthy();
            });

        });

    });

});