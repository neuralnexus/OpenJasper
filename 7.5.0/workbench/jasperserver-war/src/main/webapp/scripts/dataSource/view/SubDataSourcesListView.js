define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var _ = require('underscore');

var _componentsListBase = require('../../components/list.base');

var dynamicList = _componentsListBase.dynamicList;

var SubDataSourceItemView = require('../view/SubDataSourceItemView');

var featureDetection = require("runtime_dependencies/js-sdk/src/common/util/featureDetection");

var subDataSourcesListTemplate = require("text!../template/subDataSourcesListTemplate.htm");

var _utilUtilsCommon = require('../../util/utils.common');

var disableSelectionWithoutCursorStyle = _utilUtilsCommon.disableSelectionWithoutCursorStyle;
var _enableSelection = _utilUtilsCommon.enableSelection;

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
module.exports = Backbone.View.extend({
  events: {
    'blur input.dataSourceID': 'disableSelection',
    'focus input.dataSourceID': 'enableSelection'
  },
  initialize: function initialize(options) {
    this.options = options;
    this.subviews = [];
    this._list = new dynamicList.UnderscoreTemplatedList('selectedSubDataSourcesList', {
      template: subDataSourcesListTemplate,
      dragPattern: '',
      multiSelect: true,
      selectOnMousedown: !featureDetection.supportsTouch
    });

    this._list.observe('item:unselected', _.bind(this._itemUnselected, this));

    this._list.observe('item:selected', _.bind(this._itemSelected, this));

    this.setElement('#selectedSubDataSourcesList', false);
    this.listenTo(this.collection, 'reset', this.render);
  },
  disableSelection: function disableSelection() {
    disableSelectionWithoutCursorStyle(this._list._getElement());
  },
  enableSelection: function enableSelection() {
    _enableSelection(this._list._getElement());
  },
  _itemUnselected: function _itemUnselected(e) {
    var view = this.getSubviewByListItem(e.memo.item),
        model = view ? view.model : null;
    this.trigger('item:unselected', model);
  },
  _itemSelected: function _itemSelected(e) {
    var view = this.getSubviewByListItem(e.memo.item),
        model = view ? view.model : null;
    this.trigger('item:selected', model);
  },
  render: function render(collection, options) {
    this._list.resetSelected();

    var modelsToAdd = [],
        modelsToRemove = [],
        previousModelUris = [],
        selectAfterAdd = false,
        currentModelUris = this.collection.map(function (model) {
      return model.get('uri');
    }),
        self = this;

    if (options && options.previousModels) {
      previousModelUris = _.map(options.previousModels, function (model) {
        return model.get('uri');
      });

      _.each(options.previousModels, function (model) {
        if (!_.include(currentModelUris, model.get('uri'))) {
          modelsToRemove.push(model);
        }
      });

      selectAfterAdd = true;
    }

    this.collection.forEach(function (model) {
      if (!_.include(previousModelUris, model.get('uri'))) {
        modelsToAdd.push(model);
      }
    });

    if (modelsToAdd.length) {
      _.each(modelsToAdd, function (model) {
        var view = new SubDataSourceItemView({
          model: model
        });
        self.subviews.push(view);

        self._list.addItems([view.getListItem()]);

        self._list.show();
      });

      this._list.show();

      if (selectAfterAdd) {
        var selectedViews = _.compact(_.map(modelsToAdd, _.bind(self.getSubviewByModel, self)));

        _.each(selectedViews, function (view) {
          self._list.selectItem(view.getListItem(), true);
        });
      }

      _.each(this.subviews, function (view) {
        // ListItem._getElement() is pointing to correct element only after we added it to the List
        // so here we wait for DOM of List to be updated and then set root el for subview
        _.defer(_.bind(view.setRootElement, view));
      });
    }

    if (modelsToRemove.length) {
      _.each(modelsToRemove, function (model) {
        self.removeSubview(model);
      });

      this._list.show();
    }

    return this;
  },
  getSubviewByModel: function getSubviewByModel(model) {
    return _.find(this.subviews, function (view) {
      return view.model === model;
    });
  },
  getList: function getList() {
    return this._list;
  },
  getListLength: function getListLength() {
    return this._list._items.length;
  },
  getSelectedModels: function getSelectedModels() {
    var items = this._list.getSelectedItems(),
        subviews = _.compact(_.map(items, _.bind(this.getSubviewByListItem, this)));

    return _.map(subviews, function (view) {
      return view.model;
    });
  },
  getSubviewByListItem: function getSubviewByListItem(listItem) {
    return _.find(this.subviews, function (view) {
      return view.getListItem() === listItem;
    });
  },
  removeSubview: function removeSubview(model) {
    var view = this.getSubviewByModel(model);

    if (view) {
      this._list.removeItems([view.getListItem()]);

      view.remove();

      this._list.show();
    }
  },
  removeSubviews: function removeSubviews() {
    var listItems = _.map(this.subviews, function (view) {
      return view.getListItem();
    });

    this._list.removeItems(listItems);

    _.invoke(this.subviews, 'remove');
  },
  remove: function remove() {
    this.removeSubviews();
    Backbone.View.prototype.remove.apply(this, arguments);
  }
});

});