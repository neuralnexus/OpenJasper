define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var domReady = require('requirejs-domready');

var $ = require('jquery');

var i18n = require("bundle!CommonBundle");

var i18n2 = require("bundle!EditSettingsBundle");

var logging = require('../../administer/administer.logging');

var Administer = require('../../administer/administer.base');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var buttonsTrait = require('../../serverSettingsCommon/view/traits/buttonsTrait');

var ResetSettingsCollectionView = require('./view/ResetSettingsCollectionView');

var ResetSettingsItemView = require('./view/ResetSettingsItemView');

var ResetSettingsEmptyView = require('./view/ResetSettingsEmptyView');

var ResetSettingsCollection = require('./collection/ResetSettingsCollection');

var ResetSettingsModel = require('./model/ResetSettingsModel');

var tooltipTemplate = require("text!./templates/tooltipTemplate.htm");

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
var ResetSettingsCollectionViewExtended = ResetSettingsCollectionView.extend(buttonsTrait);
domReady(function () {
  Administer.urlContext = jrsConfigs.urlContext;
  logging.initialize();
  var collection = new ResetSettingsCollection([], {
    model: ResetSettingsModel
  }),
      resetSettingsView = new ResetSettingsCollectionViewExtended({
    el: $('.resetSettings'),
    tooltip: {
      template: tooltipTemplate,
      i18n: i18n2
    },
    collection: collection,
    childViewContainer: '.tbody',
    childView: ResetSettingsItemView,
    emptyView: ResetSettingsEmptyView,
    buttons: [{
      label: i18n['button.save'],
      action: 'save',
      primary: true
    }, {
      label: i18n['button.cancel'],
      action: 'cancel',
      primary: false
    }],
    buttonsContainer: '.buttonsContainer'
  });
  resetSettingsView.fetchData().done(resetSettingsView.render);
});

});