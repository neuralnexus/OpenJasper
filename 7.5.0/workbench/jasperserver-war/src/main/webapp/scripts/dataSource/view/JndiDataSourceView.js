define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var BaseDataSourceView = require('../view/BaseDataSourceView');

var JndiDataSourceModel = require('../model/JndiDataSourceModel');

var jndiSpecificTemplate = require("text!../template/jndiSpecificTemplate.htm");

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
module.exports = BaseDataSourceView.extend({
  PAGE_TITLE_NEW_MESSAGE_CODE: 'resource.datasource.jndi.page.title.new',
  PAGE_TITLE_EDIT_MESSAGE_CODE: 'resource.datasource.jndi.page.title.edit',
  modelConstructor: JndiDataSourceModel,
  render: function render() {
    this.$el.empty();
    this.renderJndiSpecificSection();
    this.renderTimezoneSection();
    this.renderTestConnectionSection();
    return this;
  },
  renderJndiSpecificSection: function renderJndiSpecificSection() {
    this.$el.append(_.template(jndiSpecificTemplate, this.templateData()));
  }
});

});