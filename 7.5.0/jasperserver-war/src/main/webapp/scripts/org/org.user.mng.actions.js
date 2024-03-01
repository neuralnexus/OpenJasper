define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _prototype = require('prototype');

var $ = _prototype.$;

var orgModule = require('./org.user.mng.main');

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
 * @version: $Id$
 */
orgModule.userActionFactory = {
  'enableAll': function enableAll(options) {
    var users = options.users;
    var data = {
      'userNames': Object.toJSON(users.collect(function (user) {
        return user.getNameWithTenant();
      }))
    };
    var action = new orgModule.ServerAction("enableAll", data);

    action.onSuccess = function (data) {
      orgModule.fire(orgModule.userManager.Event.USERS_ENABLED, {
        inputData: options,
        responseData: data
      });
    };

    action.onError = function (data) {
      orgModule.fire(orgModule.Event.SERVER_ERROR, {
        inputData: options,
        responseData: data
      });
    };

    action.beforeInvoke = function () {
      return orgModule.invokeClientAction("cancelIfEdit", {
        entity: users[0]
      });
    };

    return action;
  },
  'disableAll': function disableAll(options) {
    var users = options.users;
    var data = {
      'userNames': Object.toJSON(users.collect(function (user) {
        return user.getNameWithTenant();
      }))
    };
    var action = new orgModule.ServerAction("disableAll", data);

    action.onSuccess = function (data) {
      orgModule.fire(orgModule.userManager.Event.USERS_DISABLED, {
        inputData: options,
        responseData: data
      });
    };

    action.onError = function (data) {
      orgModule.fire(orgModule.Event.SERVER_ERROR, {
        inputData: options,
        responseData: data
      });
    };

    action.beforeInvoke = function () {
      return orgModule.invokeClientAction("cancelIfEdit", {
        entity: users[0]
      });
    };

    return action;
  }
};
orgModule.userManager.actionFactory = {
  'enableAllUsers': function enableAllUsers() {
    var users = orgModule.entityList.getSelectedEntities();
    return new orgModule.Action(function () {
      orgModule.invokeUserAction(orgModule.userManager.Action.ENABLE_ALL, {
        users: users
      });
    });
  },
  'disableAllUsers': function disableAllUsers() {
    var users = orgModule.entityList.getSelectedEntities();
    return new orgModule.Action(function () {
      orgModule.invokeUserAction(orgModule.userManager.Action.DISABLE_ALL, {
        users: users
      });
    });
  },
  'login': function login(options) {
    var user = options.user;
    return new orgModule.Action(function () {
      $('j_username').setValue(user.getNameWithTenant());
      $('loginAsForm').submit();
    });
  }
};
module.exports = orgModule;

});