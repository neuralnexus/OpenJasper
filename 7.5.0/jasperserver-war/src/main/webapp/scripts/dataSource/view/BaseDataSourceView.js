define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var ResourceModel = require("runtime_dependencies/bi-repository/src/bi/repository/model/RepositoryResourceModel");

var BaseDataSourceModel = require('../model/BaseDataSourceModel');

var i18n = require("bundle!jasperserver_messages");

var timezones = require("settings!userTimeZones");

var Backbone = require('backbone');

var timezoneTemplate = require("text!../template/timezoneTemplate.htm");

var dialogs = require('../../components/components.dialogs');

var testConnectionTemplate = require("text!../template/testConnectionTemplate.htm");

var Validation = require("runtime_dependencies/js-sdk/src/common/extension/backboneValidationExtension");

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
var testConnectionInProgress;
var testConnectionDetailsMessage;
module.exports = Backbone.View.extend({
  PAGE_TITLE_NEW_MESSAGE_CODE: undefined,
  PAGE_TITLE_EDIT_MESSAGE_CODE: undefined,
  modelConstructor: BaseDataSourceModel,
  events: {
    "keyup input[type='text'], input[type='password'], textarea, select": "updateModelProperty",
    "change input[type='text'], input[type='password'], input[type='radio'], input[type='checkbox'], textarea, select": "updateModelProperty",
    "click #testDataSource": "testConnection",
    "click [name=testConnectionMessageDetails]": "showTestConnectionMessageDetails"
  },
  initialize: function initialize(options) {
    this.options = options;
    this.isEditMode = options.isEditMode;
    this.timezones = options.timezones ? options.timezones : timezones;
    var modelAttrs = {};

    if (options.dataSource) {
      modelAttrs = _.extend(modelAttrs, options.dataSource);
    }

    this.model = new this.modelConstructor(modelAttrs, options);
    Validation.bind(this, {
      valid: this.fieldIsValid,
      invalid: this.fieldIsInvalid,
      forceUpdate: true,
      selector: "name"
    });

    if (this.model.initialization) {
      var self = this;
      this.model.initialization.done(function () {
        self.render.apply(self);
      });
    } else {
      this.render();
    }

    this.setPageTitle();
  },
  testConnection: function testConnection() {
    if (testConnectionInProgress === true) return;
    var rc = this.model.testConnection(),
        self = this,
        msg,
        button;
    if (!rc) return; // set flag variable

    testConnectionInProgress = true; // disable the "Test Connection" button

    button = self.$el.find("#testDataSource");
    button.addClass("disabled"); // clear the test connection message near the button

    msg = this.$el.find("[name=testConnectionMessage]"); // next goes magic !

    msg.removeClass("warning success").addClass("hidden");
    msg.parent().addClass("error"); // special class name goes to parent

    msg.find("a").addClass("hidden"); // this hides the "Show Details" link

    rc.done(function () {
      msg.addClass("success").find("span").text(i18n["resource.dataSource.connectionState.passed"]);
    }).fail(function (xhr) {
      // now, compose the message
      var errMsg = self.getTestConnectionErrorMessage(xhr); // and display it !

      msg.addClass("warning").find("span").text(errMsg.text);

      if (errMsg.details) {
        msg.find("a").removeClass("hidden");
        testConnectionDetailsMessage = errMsg.details;
      }
    }).always(function () {
      testConnectionInProgress = false;
      msg.removeClass("hidden"); // this line actually shows the test connection message

      button.removeClass("disabled");
    });
  },
  showTestConnectionMessageDetails: function showTestConnectionMessageDetails() {
    dialogs.errorPopup.show(testConnectionDetailsMessage);
  },
  getTestConnectionErrorMessage: function getTestConnectionErrorMessage(xhr) {
    //parse the response
    var response = false,
        text = i18n["resource.dataSource.connectionState.failed"],
        details = false;

    try {
      response = JSON.parse(xhr.responseText);
    } catch (e) {
      // in this case show at least what was sent ot us
      details = xhr.responseText;
    }

    if (response) {
      if (response.parameters && response.parameters[2]) {
        // if 3rd parameter exist, then it's an exception message and should be shown as connection error text
        text = response.parameters[2];
      }

      if (response.parameters && response.parameters[3]) {
        // if 4th parameter exist, then it's an exception stack trace, which should be shown as error details
        details = response.parameters[3];
      }
    }

    return {
      text: text,
      details: details
    };
  },
  updateModelProperty: function updateModelProperty(e) {
    var $targetEl = $(e.target),
        update = {},
        attr = $targetEl.attr("name"),
        value = "checkbox" === $targetEl.attr("type") ? $targetEl.is(':checked') : $.trim($targetEl.val());
    update[attr] = value;
    this.model.set(update);

    if (!this.isEditMode) {
      if (attr === "name") {
        var generatedId = ResourceModel.generateResourceName(this.model.get("label"));

        if (value !== generatedId) {
          this._idUpdatedManually = true;
        }
      } else if (attr === "label" && !this._idUpdatedManually) {
        var newId = ResourceModel.generateResourceName(value);
        this.model.set("name", newId);
        this.$("input[name='name']").val(newId);
      }
    }

    this.model.validate(update);
  },
  render: function render() {
    this.$el.empty();
    return this;
  },
  renderTimezoneSection: function renderTimezoneSection() {
    this.$el.append(_.template(timezoneTemplate, this.templateData()));
  },
  renderTestConnectionSection: function renderTestConnectionSection() {
    this.$el.append(_.template(testConnectionTemplate, this.templateData()));
  },
  renderOrAddAnyBlock: function renderOrAddAnyBlock(container, html) {
    if (_.isString(html)) {
      try {
        html = $(html);
      } catch (e) {
        html = false;
      }

      if (!html) {
        return false;
      }
    } // check if html has already been rendered in the container
    // (it's possible because each html fragment is covered with "fieldset" tag


    var fragmentName = html.first().attr("name");

    if (!fragmentName) {
      return false;
    }

    if (container.find("[name=" + fragmentName + "]").length > 0) {
      // the render has happened already, so we need to clean the container
      // and put dom fragment inside it
      container.find("[name=" + fragmentName + "]").empty().append(html.children());
    } else {
      // the render happens first time, there is no any custom field elements and there is no custom
      // container, so we can simply put it there
      container.append(html);
    }

    return true;
  },
  templateData: function templateData() {
    return {
      _: _,
      i18n: i18n,
      modelAttributes: _.clone(this.model.attributes),
      timezones: this.timezones,
      isEditMode: this.isEditMode
    };
  },
  setPageTitle: function setPageTitle() {
    var title,
        $pageTitleEl = $("#display .showingToolBar > .content > .header > .title");

    if (this.isEditMode) {
      title = i18n[this.PAGE_TITLE_EDIT_MESSAGE_CODE] + ": " + this.model.get("label");
    } else {
      title = i18n[this.PAGE_TITLE_NEW_MESSAGE_CODE];
    }

    $pageTitleEl.text(title);
  },
  fieldIsValid: function fieldIsValid(view, attr, selector) {
    var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
    $parentEl.removeClass("error");
    $parentEl.find(".message.warning").text("");
  },
  fieldIsInvalid: function fieldIsInvalid(view, attr, error, selector) {
    if (error === true) {
      // don't show anything since this type of error is used when you need to
      // mark model as invalid and at the same time don't show any error message
      return;
    }

    var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
    $parentEl.addClass("error");
    $parentEl.find(".message.warning").text(error);
  },
  // simpler version of 'validField' which works always on the same
  // context ('view' parameter is absent), and selector does not build from two parameters
  validField: function validField(selector) {
    var $parentEl = this.$(selector).parent();
    $parentEl.removeClass("error");
    $parentEl.find(".message.warning").text("");
  },
  // simpler version of 'fieldIsInvalid' which works always on the same
  // context ('view' parameter is absent), and selector does not build from two parameters
  // also, the order of parameters has changed
  invalidField: function invalidField(selector, error) {
    var $parentEl = this.$(selector).parent();
    $parentEl.addClass("error");
    $parentEl.find(".message.warning").text(error.toString());
  },
  remove: function remove() {
    // remove all resource locator DOM elements
    // 1 and 2 are the beginning of timestamp
    $("div[id^='selectFromRepository1'], div[id^='selectFromRepository2']").remove();
    Validation.unbind(this);
    Backbone.View.prototype.remove.call(this);
    return this;
  }
});

});