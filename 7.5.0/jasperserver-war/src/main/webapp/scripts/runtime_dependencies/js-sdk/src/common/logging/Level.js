define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var levels = require("../enum/loggingLevels");

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
 * @author: Pavel Savushchyk
 * @version: $Id$
 */
function Level(level, name) {
  this.level = level;
  this.name = name.toUpperCase();
}

Level.prototype.isGreaterOrEqual = function (globalLevel) {
  var levelNumber = (globalLevel instanceof Level ? globalLevel : Level.getLevel(globalLevel)).level;
  return this.level >= levelNumber;
};

Level.prototype.toString = function () {
  return this.name;
};

Level.getLevel = function (level) {
  return Level[level.toUpperCase()];
};

for (var i in levels) {
  if (levels.hasOwnProperty(i)) {
    Level[i] = new Level(levels[i], i);
  }
}

module.exports = Level;

});