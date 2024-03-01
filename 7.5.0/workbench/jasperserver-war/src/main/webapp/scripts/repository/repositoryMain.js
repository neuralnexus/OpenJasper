define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var domReady = require('requirejs-domready');

var repositorySearch = require('../repository/repository.search.root');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var _componentsListBase = require('../components/list.base');

var dynamicList = _componentsListBase.dynamicList;

var _ = require('underscore');

require('backbone');

require('../components/components.dependent.dialog');

require('../components/components.toolbarButtons.events');

require('../components/components.tooltip');

require('../util/tools.infiniteScroll');

require('../manage/mng.common');

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
domReady(function () {
  _.extend(repositorySearch.messages, jrsConfigs.repositorySearch.i18n);

  _.extend(dynamicList.messages, jrsConfigs.dynamicList.i18n);

  _.extend(window.localContext, jrsConfigs.repositorySearch.localContext);

  repositorySearch.initialize(window.localContext);
});

});