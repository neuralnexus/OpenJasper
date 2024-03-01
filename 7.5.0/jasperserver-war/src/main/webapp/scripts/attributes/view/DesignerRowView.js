define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var RowView = require('../../attributes/view/RowView');

var DeleteConfirm = require('../../serverSettingsCommon/behaviors/DeleteConfirmBehavior');

var confirmDialogTypesEnum = require('../../serverSettingsCommon/enum/confirmDialogTypesEnum');

var permissionMasksEnum = require('../../attributes/enum/permissionMasksEnum');

var rowTemplatesFactory = require('../../attributes/factory/rowTemplatesFactory');

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
var SECURE_VALUE_SUBSTITUTION = '~secure~';
var SLIDE_UP_ROW_HEIGHT = '28px';
var ROW_HEIGHT = '100%';
var DesignerRowView = RowView.extend({
  tagName: 'div',
  className: 'table-row',
  ui: {
    passwordInput: 'input[type=\'password\']'
  },
  events: _.extend({}, RowView.prototype.events, {
    'click .edit': 'toggleActive',
    'click .cancel': 'cancel',
    'click .ok': 'runValidation',
    'click @ui.passwordInput': 'toggleValue',
    'blur @ui.passwordInput': 'toggleValue'
  }),
  modelEvents: {
    'change': '_onModelChange'
  },
  behaviors: _.extend(RowView.prototype.behaviors, {
    DeleteConfirm: {
      behaviorClass: DeleteConfirm
    }
  }),
  computeds: _.extend(RowView.prototype.computeds, {
    readOnly: {
      deps: ['inherited', 'permissionMask'],
      get: function get(inherited, permissionMask) {
        var isReadOnly = permissionMask === permissionMasksEnum.READ_ONLY;
        return isReadOnly || inherited && isReadOnly;
      }
    },
    disableRemove: {
      deps: ['inherited', 'permissionMask'],
      get: function get(inherited, permissionMask) {
        return inherited || permissionMask === permissionMasksEnum.READ_ONLY;
      }
    }
  }),
  initialize: function initialize(options) {
    this.editMode = false;
    this._onViewInitialize && this._onViewInitialize();
    RowView.prototype.initialize.apply(this, arguments);
  },
  render: function render() {
    RowView.prototype.render.apply(this, arguments);
    this._onViewRender && this._onViewRender();
    return this;
  },
  getTemplate: function getTemplate() {
    var showRowViewTemplate = !this.editMode && this.model.get('name');
    return _.template(rowTemplatesFactory({
      editMode: !showRowViewTemplate
    }));
  },
  toggleActive: function toggleActive() {
    var dfd = new $.Deferred(),
        self = this;
    this.toggleMode().done(function () {
      self.trigger('active', self.editMode, dfd);
    });
    return dfd;
  },
  toggleMode: function toggleMode() {
    var dfd = new $.Deferred();
    this.editMode = !this.editMode;
    this.editMode ? this.slideDown(dfd) : this.slideUp(dfd);
    return dfd;
  },
  toggleIfModelIsValid: function toggleIfModelIsValid() {
    if (this.validateModel()) {
      this.model.setState('confirmedState');
      this.model.isNew() && this.confirmState();
      this.modelChanged = null;
      return this.toggleActive();
    }

    return new $.Deferred();
  },
  slideDown: function slideDown(dfd) {
    this.render().hide().slideDown({
      done: function done() {
        dfd.resolve();
      }
    });
    return this;
  },
  slideUp: function slideUp(dfd, height) {
    height = height || SLIDE_UP_ROW_HEIGHT;
    var self = this;
    this.$el.animate({
      height: height
    }, {
      done: function done() {
        self.render().$el.css({
          height: ROW_HEIGHT
        }).show();
        dfd.resolve();
      }
    });
    return this;
  },
  hide: function hide() {
    return this.$el.hide();
  },
  show: function show() {
    return this.$el.show();
  },
  cancel: function cancel() {
    this.model.set(this.model.getState('confirmedState'));
    this.toggleActive();
  },
  runValidation: function runValidation(event, options) {
    this.validateModel(options) && this.triggerConfirmOpening(options);
  },
  triggerConfirmOpening: function triggerConfirmOpening(options) {
    options = options || {};
    var dfd = new $.Deferred(),
        self = this;

    var openPermissionConfirmOrValidate = function openPermissionConfirmOrValidate() {
      self.permissionConfirmShouldBeShown ? self._openPermissionConfirm && _.bind(self._openPermissionConfirm, self, options)() : self.triggerModelValidation(options);
    };

    if (!this.model.isNew()) {
      if (this.modelChanged) {
        this.modelChanged.name ? this.trigger('open:confirm', confirmDialogTypesEnum.NAME_CONFIRM, {
          dfd: dfd
        }) : dfd.resolve();
        dfd.done(function () {
          self.modelChanged._embedded ? openPermissionConfirmOrValidate() : self.triggerModelValidation(options);
        });
      } else {
        this.toggleActive();
      }
    } else {
      openPermissionConfirmOrValidate();
    }
  },
  triggerModelValidation: function triggerModelValidation(options) {
    this.model.trimAttrs(['name', 'value', 'description']);
    this.trigger('validate', options);
  },
  validateModel: function validateModel(options) {
    options = options || {};
    var modelIsValid = this.model.isValid(true);
    !modelIsValid && options.dfd && options.dfd.reject();
    return modelIsValid;
  },
  confirmState: function confirmState(confirm) {
    this.model.confirmState(confirm);
  },
  isStateConfirmed: function isStateConfirmed() {
    return this.model.isStateConfirmed();
  },
  isInherited: function isInherited() {
    return this.model.getState('originalState').inherited;
  },
  getChangedProperties: function getChangedProperties(property) {
    if (this.modelChanged) {
      return property ? this.modelChanged[property] : this.modelChanged;
    }
  },
  toggleValue: function toggleValue(e) {
    if (this.editMode) {
      var $input = $(e.target),
          inputValue = $input.val(),
          isValueSecured = inputValue === SECURE_VALUE_SUBSTITUTION,
          isOkButton = $(e.relatedTarget).hasClass('ok'),
          isClick = e.type === 'click';

      if (inputValue) {
        if (isClick && isValueSecured) {
          $input.val('');
          this.model.resetField('value');
        }

        if (!isClick && !isOkButton) {
          $input.val(SECURE_VALUE_SUBSTITUTION);
        }
      }
    }
  },
  invokeFiltration: function invokeFiltration() {
    var model = this.model;

    if (model.hasChanged('secure') || model.hasChanged('_embedded') && !this._isPermissionLimited()) {
      return !this.editMode && model.hasChanged('inherited');
    }
  },
  validateIfSecure: function validateIfSecure() {
    var nameChanged = this.getChangedProperties('name');
    this.model.validateIfSecure = this.model.validateSecureValue() && nameChanged || false;
  },
  _onSaveSuccess: function _onSaveSuccess() {
    this.modelChanged = null;
    this.model.setId();
    this.model.get('secure') && this.model.resetField('value');
    this.model.setState();
    this.model.setState('confirmedState');
  },
  _onModelChange: function _onModelChange(model) {
    this.modelChanged = this.modelChanged || {};
    var changedAttributes = model.changedAttributes(),
        originalModelAttributes = this.model.getState(),
        isInherited = this.isInherited(),
        modelIsNew = model.isNew(),
        modelIsChanged;

    _.each(changedAttributes, function (val, prop) {
      if (prop !== 'inherited') {
        var originalAttribute = originalModelAttributes[prop],
            equal = !_.isObject(val) ? originalAttribute === val : _.isEqual(originalAttribute, val);

        if (!equal) {
          this.modelChanged[prop] = val;
        } else {
          delete this.modelChanged[prop];
        }
      }
    }, this);

    modelIsChanged = !_.isEmpty(this.modelChanged);
    isInherited && model.set('inherited', !modelIsChanged);
    !this.editMode && changedAttributes.secure && model.setState('confirmedState');
    this.trigger('changed', modelIsChanged || modelIsNew);
    this.model.validate(this.modelChanged);
  }
});
module.exports = DesignerRowView;

});