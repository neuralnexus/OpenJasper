define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var roleEnum = require('../../attributes/enum/roleEnum');

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
module.exports = {
  computeds: {
    permissionEmbedded: {
      deps: ['_embedded', 'permissionMask'],
      get: function get(_embedded, permissionMask) {
        var permission = this.getPermission(_embedded);
        return permission ? permission.mask : permissionMask;
      },
      set: function set(value) {
        var _embedded = _.cloneDeep(this.get('_embedded')),
            permission = this.getPermission(_embedded);

        if (permission) {
          permission.mask = value;
        } else {
          _embedded.permission.push({
            mask: value,
            recipient: roleEnum.ROLE_ADMINISTRATOR
          });
        }

        return {
          _embedded: _embedded
        };
      }
    }
  },
  _initModelWithPermissionDefaults: function _initModelWithPermissionDefaults() {
    this.defaults = _.extend({}, this.defaults, {
      _embedded: {
        'permission': [{
          'recipient': roleEnum.ROLE_ADMINISTRATOR,
          'mask': '1'
        }]
      }
    });
  },
  getPermission: function getPermission(_embedded) {
    _embedded = _embedded || this.get('_embedded');

    if (_embedded) {
      return _.find(_embedded.permission, function (permission) {
        return permission.recipient === roleEnum.ROLE_ADMINISTRATOR;
      });
    }
  }
};

});