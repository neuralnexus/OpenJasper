define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var i18n = require("bundle!AttributesBundle");

var AttributesCollection = require('../../attributes/collection/AttributesCollection');

var DesignerRowView = require('../../attributes/view/DesignerRowView');

var DesignerEmptyView = require('../../attributes/view/DesignerEmptyView');

var viewWithPermissionTrait = require('../../attributes/view/viewWithPermissionTrait');

var RowView = require('../../attributes/view/RowView');

var attributesTypesEnum = require('../../attributes/enum/attributesTypesEnum');

var i18n2 = require("bundle!CommonBundle");

var AttributeModel = require('../../attributes/model/AttributeModel');

var modelWithPermissionTrait = require('../../attributes/model/modelWithPermissionTrait');

var tooltipTemplate = require("text!../../attributes/templates/tooltipTemplate.htm");

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
module.exports = function (options) {
  var collection = new AttributesCollection([], {
    context: options.context,
    model: options.type === attributesTypesEnum.USER ? AttributeModel : AttributeModel.extend(modelWithPermissionTrait)
  }),
      commonOptions = {
    type: options.type,
    collection: collection,
    childViewContainer: '.tbody',
    tooltip: {
      template: tooltipTemplate,
      i18n: i18n
    }
  };
  return _.extend(commonOptions, options.type !== attributesTypesEnum.SERVER ? {
    $container: options.container,
    designer: {
      childView: options.type === attributesTypesEnum.USER ? DesignerRowView : DesignerRowView.extend(viewWithPermissionTrait),
      emptyView: DesignerEmptyView,
      filters: [{
        title: i18n['attributes.table.filters.all'],
        value: 'true',
        field: 'defaultFilter',
        selected: true
      }, {
        title: i18n['attributes.table.filters.true'],
        value: 'true',
        field: 'inherited'
      }, {
        title: i18n['attributes.table.filters.false'],
        value: 'false',
        field: 'inherited'
      }]
    },
    viewer: {
      childView: RowView
    }
  } : {
    el: options.el,
    childView: DesignerRowView.extend(viewWithPermissionTrait),
    emptyView: DesignerEmptyView,
    buttons: [{
      label: i18n2['button.save'],
      action: 'save',
      primary: true
    }, {
      label: i18n2['button.cancel'],
      action: 'cancel',
      primary: false
    }],
    buttonsContainer: '.buttonsContainer'
  });
};

});