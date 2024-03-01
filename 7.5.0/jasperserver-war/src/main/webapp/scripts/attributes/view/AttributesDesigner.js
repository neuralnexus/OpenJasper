define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var Backbone = require('backbone');

var i18n = require("bundle!AttributesBundle");

var confirmDialogTypesEnum = require('../../serverSettingsCommon/enum/confirmDialogTypesEnum');

var attributesTypesEnum = require('../../attributes/enum/attributesTypesEnum');

var confirmationDialogFactory = require('../../attributes/factory/confirmationDialogFactory');

var tableTemplatesFactory = require('../../attributes/factory/tableTemplatesFactory');

var errorFactory = require('../../attributes/factory/errorFactory');

var AttributesViewer = require('../../attributes/view/AttributesViewer');

var Notification = require("runtime_dependencies/js-sdk/src/common/component/notification/Notification");

var AlertDialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/AlertDialog");

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
var AttributesDesigner = AttributesViewer.extend({
  className: 'attributesDesigner',
  ui: {
    addNewBtn: '.addNewItem'
  },
  events: {
    'click @ui.addNewBtn': '_addNewChildView',
    'mousedown .filterItems, .actions.table-column button, .permission.table-column select, .secure.table-column input': '_checkCurrentAttribute',
    'focus .filterItems, .actions.table-column button, .permission.table-column select, .secure.table-column input': '_checkCurrentAttribute'
  },
  childEvents: {
    'active': '_activeChildView',
    'changed': '_saveChildViewToChangedList',
    'open:confirm': '_openConfirm',
    'validate': '_validateChildView'
  },
  initialize: function initialize(options) {
    options = options || {};
    var type = {
      type: options.type
    };
    this.notification = new Notification();
    this.alertDialog = new AlertDialog();
    this.model = new Backbone.Model();
    this.changedModels = [];
    this.overriddenInheritedModels = [];
    AttributesViewer.prototype.initialize.apply(this, arguments);
    this.childViewOptions = _.extend({}, options.childViewOptions, type);
    this.emptyViewOptions = _.extend({}, options.emptyViewOptions, type);

    this._initConfirmationDialogs();

    this._initFilters && this._initFilters(options);
    !_.isEmpty(options.buttons) && options.buttonsContainer && this._initButtons && this._initButtons(options);

    this._initEvents();
  },
  render: function render(hideFilters) {
    AttributesViewer.prototype.render.apply(this, arguments);
    this._renderFilters && this._renderFilters(hideFilters);
    return this;
  },
  hide: function hide() {
    this._resetFilters && this._resetFilters();
    AttributesViewer.prototype.hide.apply(this, arguments);
  },
  saveChildren: function saveChildren() {
    var self = this,
        deletedModels = this._filterChangeList('isDeleted'),
        updatedModels = _.difference(this.changedModels, deletedModels);

    this.saveDfD = new $.Deferred();

    this._validateNotAcceptedChildView().done(function () {
      self.containsUnsavedItems() ? self.collection.save(self.changedModels, updatedModels).done(_.bind(self._successAjaxCallback, self)).fail(_.bind(self._errorAjaxCallback, self)) : self.saveDfD.resolve();
    }).fail(function () {
      self.saveDfD.reject();
    });

    return this.saveDfD;
  },
  _validateNotAcceptedChildView: function _validateNotAcceptedChildView() {
    var validationDfD = new $.Deferred();
    this.currentChildView ? this.currentChildView.runValidation(null, {
      dfd: validationDfD
    }) : validationDfD.resolve();
    return validationDfD;
  },
  revertChanges: function revertChanges() {
    this.revertDfd = new $.Deferred();
    var self = this,
        dfd = new $.Deferred(),
        model;
    this.currentChildView ? this.currentChildView.toggleActive().done(dfd.resolve) : dfd.resolve();
    dfd.done(function () {
      var length = self.changedModels.length;

      for (var i = length - 1; i >= 0; i--) {
        model = self.changedModels[i];

        if (!model.isDeleted) {
          !model.isNew() ? model.reset().setState('confirmedState', model.getState()) : self._removeModel(model);
        } else {
          self.revertViewRemoval(model);
        }
      }

      self._resetChangedList();

      self.revertDfd.resolve();
    });
    return this.revertDfd;
  },
  getTemplate: function getTemplate() {
    return _.template(tableTemplatesFactory());
  },
  containsUnsavedItems: function containsUnsavedItems() {
    return !!this.changedModels.length;
  },
  removeView: function removeView(model) {
    var view = this._findChildrenByModel(model);

    model.isDeleted = {
      index: this.collection.indexOf(model)
    };
    !model.get('inherited') ? this._saveChildViewToChangedList(view, !model.isNew()) : this._saveRemovedOverriddenInheritedModelToList(model);

    this._removeModel(model);
  },
  revertViewRemoval: function revertViewRemoval(model) {
    var index = model.isDeleted && model.isDeleted.index,
        indexInCollection = _.isNumber(index) ? index : this.collection.models.length; //TODO: Check if this row could be removed
    //TODO: Check if this row could be removed

    this._deleteViewFromChangedList(model);

    model.reset();
    this.collection.add(model, {
      at: indexInCollection
    });
    delete model.isDeleted;
  },
  remove: function remove() {
    $(window).off('beforeunload', this._onPageLeave);

    this._removeConfirmationDialogs();

    this.notification && this.notification.remove();
    this.alertDialog && this.alertDialog.remove();
    AttributesViewer.prototype.remove.apply(this, arguments);
  },

  /*******************/

  /* Private methods */

  /*******************/
  _initEvents: function _initEvents() {
    $(window).on('beforeunload', _.bind(this._onPageLeave, this));
  },
  _initConfirmationDialogs: function _initConfirmationDialogs() {
    this.confirmationDialogs = {};

    _.each(confirmDialogTypesEnum, function (type) {
      this.confirmationDialogs[type] = confirmationDialogFactory(type);
    }, this);

    this.listenTo(this.confirmationDialogs[confirmDialogTypesEnum.DELETE_CONFIRM], 'button:yes', this._onDeleteConfirm);
    this.listenTo(this.confirmationDialogs[confirmDialogTypesEnum.NAME_CONFIRM], 'button:yes', this._onNameConfirm);
    this.listenTo(this.confirmationDialogs[confirmDialogTypesEnum.NAME_CONFIRM], 'button:no', _.bind(this._revertChangedModelProperty, this, 'name'));
    this.listenTo(this.confirmationDialogs[confirmDialogTypesEnum.CANCEL_CONFIRM], 'button:yes', this.revertChanges);
    this.listenTo(this.confirmationDialogs[confirmDialogTypesEnum.EDIT_CONFIRM], 'button:yes', this._onEditConfirm);
    this._initPermissionConfirmEvents && this._initPermissionConfirmEvents();
  },
  _removeConfirmationDialogs: function _removeConfirmationDialogs() {
    _.each(this.confirmationDialogs, function (confirmationDialog) {
      confirmationDialog.remove();
    }, this);
  },
  _successAjaxCallback: function _successAjaxCallback(data) {
    this.notification.show({
      message: i18n['attributes.notification.message.saved'],
      type: 'success'
    });
    this._sendSearchRequest() ? this._searchForInherited(_.union(this.deletedModels, this.renamedModels)).then(this.saveDfD.resolve, this.saveDfD.reject) : this.saveDfD.resolve();
    data && this._postProcessItems(data);

    this._resetChangedList();
  },
  _sendSearchRequest: function _sendSearchRequest() {
    return !this._isServerLevel() && this._searchForInherited && this._deletedRenamedModels();
  },
  _deletedRenamedModels: function _deletedRenamedModels() {
    this.deletedModels = this._filterChangeList('isDeleted');
    this.renamedModels = this._filterChangeList('isRenamed');
    return this.deletedModels.length || this.renamedModels.length;
  },
  _filterChangeList: function _filterChangeList(propertyName) {
    var property;
    return _.compact(_.filter(this.changedModels, function (model) {
      property = model[propertyName];

      if (!model.get('inherited')) {
        return !_.isFunction(property) ? property : model[propertyName]();
      }
    }));
  },
  _postProcessItems: function _postProcessItems(data) {
    var attributes = data.attribute,
        view;

    _.each(attributes, function (attribute) {
      view = this._findChildrenByModel(attribute.name);
      view && view._onSaveSuccess();
    }, this);
  },
  _errorAjaxCallback: function _errorAjaxCallback(response) {
    this.alertDialog.setMessage(errorFactory(response));
    this.alertDialog.open();
    this.saveDfD.reject();
  },
  _toggleAddNewItemButton: function _toggleAddNewItemButton(show) {
    var $addNewItem = $(this.ui.addNewBtn);
    show ? $addNewItem.show() : $addNewItem.hide();
  },
  _checkCurrentAttribute: function _checkCurrentAttribute(e) {
    if (this.currentChildView) {
      e.preventDefault();
      this.confirmationDialogs[confirmDialogTypesEnum.EDIT_CONFIRM].open();
    }
  },
  _onPageLeave: function _onPageLeave(e) {
    if (this.containsUnsavedItems()) {
      (e || window.event).returnValue = i18n['attributes.dialog.unsaved.changes'];
      return i18n['attributes.dialog.unsaved.changes'];
    }
  },
  _onEditConfirm: function _onEditConfirm() {
    this.currentChildView.cancel();
  },
  _onNameConfirm: function _onNameConfirm() {
    this.validateDfD && this.validateDfD.resolve();
  },
  _onDeleteConfirm: function _onDeleteConfirm() {
    var childView = this.model.get('changedChildView'),
        model = childView.model,
        modelName = model.get('name');

    if (childView.isInherited() && !model.isRenamed()) {
      model.reset();
    } else {
      this.removeView(model);
      this._revertInheritedRemoval && this._revertInheritedRemoval(modelName);
    }
  },
  _isServerLevel: function _isServerLevel() {
    return this.type === attributesTypesEnum.SERVER || this.collection.getContext().id === null;
  },

  /***********************/

  /* Child views methods */

  /***********************/
  _revertChangedModelProperty: function _revertChangedModelProperty(property) {
    var childView = this.model.get('changedChildView') || this.currentChildView;
    childView.model.reset(property, 'confirmedState');
  },
  _addNewChildView: function _addNewChildView() {
    var model = this.collection.addNew();

    var view = this._findChildrenByModel(model);

    this._saveChildViewToChangedList(view, true);

    view.toggleActive();
  },
  _scrollToChildView: function _scrollToChildView(childView) {
    var $parentElement = this.$el.closest('.body'),
        parentElementHeight = $parentElement.height(),
        $childViewEl = childView.$el,
        childViewElHeight = $childViewEl.height(),
        childViewPosition = $childViewEl.position(),
        scrollTo = parentElementHeight < childViewPosition.top + childViewElHeight && {
      scrollTop: $parentElement.scrollTop() + (childViewPosition.top + childViewElHeight - parentElementHeight)
    };
    scrollTo && $parentElement.animate(scrollTo, 900);
  },
  _successValidationCallback: function _successValidationCallback(childView, dfd, data) {
    var self = this,
        model = childView.model;
    this._filterInheritedViews && this._filterInheritedViews(data);
    childView.validateIfSecure();
    childView.toggleIfModelIsValid().done(function () {
      self._removeInheritedView && self._removeInheritedView(model);
      self._addInheritedView && self._addInheritedView(model);
      self._showPermissionConfirm && self._showPermissionConfirm(childView);
      self._resetFilters && self._resetFilters();
      dfd.resolve();
    });
  },
  _deleteViewFromChangedList: function _deleteViewFromChangedList(model) {
    var index = _.indexOf(this.changedModels, model);

    index !== -1 && this.changedModels.splice(index, 1);
  },
  _removeModel: function _removeModel(model) {
    this.collection.remove(model);
  },
  _findModelsWhere: function _findModelsWhere(attributes) {
    return this.collection.findWhere(attributes);
  },
  _findChildrenByModel: function _findChildrenByModel(model) {
    model = _.isString(model) ? this._findModelsWhere({
      name: model
    }) : model;
    return model && this.children.findByModel(model);
  },
  _resetChangedList: function _resetChangedList() {
    this.changedModels.length = 0;
    this.overriddenInheritedModels.length = 0;

    this._triggerChangeEvent();
  },
  _triggerChangeEvent: function _triggerChangeEvent() {
    this.toggleButtons && this.toggleButtons();
    this.trigger('change');
  },

  /******************************/

  /*  Child view event handlers */

  /******************************/
  _activeChildView: function _activeChildView(childView, active, dfd) {
    this._setCurrentChildView(active ? childView : null);

    this._scrollToChildView(childView);

    dfd && dfd.resolve();
  },
  _saveChildViewToChangedList: function _saveChildViewToChangedList(childView, modelChanged) {
    var model = childView.model;
    _.contains(this.changedModels, model) ? !modelChanged && this._deleteViewFromChangedList(model) : modelChanged && this.changedModels.push(model);
    childView.invokeFiltration() && this._resetFilters && this._resetFilters();

    this._triggerChangeEvent();
  },
  _saveRemovedOverriddenInheritedModelToList: function _saveRemovedOverriddenInheritedModelToList(model) {
    !_.contains(this.overriddenInheritedModels, model) && this.overriddenInheritedModels.push(model);
  },
  _openConfirm: function _openConfirm(childView, type, options) {
    this.validateDfD = options.dfd;
    this.model.set('changedChildView', childView);
    var confirmDialog = this.confirmationDialogs[type],
        openConfirm = type !== confirmDialogTypesEnum.CANCEL_CONFIRM ? true : this.containsUnsavedItems();

    switch (type) {
      case confirmDialogTypesEnum.DELETE_CONFIRM:
        confirmDialog.setContent(_.template(i18n['attributes.confirm.delete.dialog.text'], {
          name: childView.model.get('name')
        }));
        break;

      case confirmDialogTypesEnum.PERMISSION_CONFIRM:
        confirmDialog.setContent(i18n['attributes.confirm.permission.dialog.text']);
        break;
    }

    openConfirm && confirmDialog.open();
  },
  _validateChildView: function _validateChildView(childView, options) {
    options = options || {};

    var model = childView.model,
        dfd = options.dfd ? options.dfd : new $.Deferred(),
        modelIsOriginallyInherited = !model.isOriginallyInherited(),
        successCallback = _.bind(this._successValidationCallback, this, childView, dfd),
        groupSearch = this._isServerLevel();

    childView.getChangedProperties('name') && !this._sendValidateSearchRequest(model) ? this.collection.validateSearch(model, this.changedModels, modelIsOriginallyInherited, groupSearch).then(successCallback) : successCallback();
    return dfd;
  },
  _sendValidateSearchRequest: function _sendValidateSearchRequest(model) {
    var deletedModels = this._filterChangeList('isDeleted'),
        modelWithSameNameWasDeleted = _.filter(deletedModels, function (deletedModel) {
      return model.get('name') === deletedModel.get('name');
    }).length,
        changedModelsWithSameName = _.filter(this.changedModels, function (changedModel) {
      return model.get('name') === changedModel.get('name');
    }).length;

    return modelWithSameNameWasDeleted && !model.isDeleted && changedModelsWithSameName <= 2;
  },
  _setCurrentChildView: function _setCurrentChildView(nextCurrentChildView) {
    this._toggleAddNewItemButton(!nextCurrentChildView);

    var currentChildView = this.currentChildView;

    if (currentChildView) {
      var currentChildViewModel = currentChildView.model,
          removeModel = currentChildViewModel.isNew() && !currentChildView.isStateConfirmed();
      removeModel && this.removeView(currentChildViewModel);
    }

    this.currentChildView = nextCurrentChildView;
  }
});
module.exports = AttributesDesigner;

});