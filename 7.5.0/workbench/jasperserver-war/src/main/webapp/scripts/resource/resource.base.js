define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _prototype = require('prototype');

var $ = _prototype.$;
var $$ = _prototype.$$;
var $break = _prototype.$break;

var TouchController = require('../util/touch.controller');

var _utilUtilsCommon = require("../util/utils.common");

var buildActionUrl = _utilUtilsCommon.buildActionUrl;
var isIPad = _utilUtilsCommon.isIPad;

var buttonManager = require('../core/core.events.bis');

var dynamicTree = require('../dynamicTree/dynamicTree.utils');

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
var resource = {
  messages: {},
  resourceLabelMaxLength: 100,
  resourceIdMaxLength: 100,
  resourceDescriptionMaxLength: 250,
  PROPAGATE_EVENT: 'propagateEvent',
  STEP_DISPLAY_ID: 'stepDisplay',
  FLOW_CONTROLS_ID: 'flowControls',
  initSwipeScroll: function initSwipeScroll() {
    var display = $(resource.STEP_DISPLAY_ID); //        var hasControls = $(resource.FLOW_CONTROLS_ID).childElements().length > 0;
    //
    //        var scrollElement = hasControls ? display.up() : display;
    //        var hasControls = $(resource.FLOW_CONTROLS_ID).childElements().length > 0;
    //
    //        var scrollElement = hasControls ? display.up() : display;

    display && new TouchController(display.up(), display.up(1), {} //            debug: true
    );
  },
  submitForm: function submitForm(formId, params, fillForm) {
    if (!params) {
      return;
    } // prepare action url
    // prepare action url


    var url = buildActionUrl(params);
    fillForm && fillForm(); // write form attributes and submit it
    // write form attributes and submit it

    $(formId).writeAttribute('method', 'post').writeAttribute('action', url);
    $(formId).submit();
  },
  // Setup main event handler for click events on Add Resource pages.
  registerClickHandlers: function registerClickHandlers(handlers, observeElementSelector, addAtBeginning) {
    if (resource._bodyClickEventHandlers) {
      //If we want some handlers to fire before others we should pass addAtBeginning = true
      (addAtBeginning ? Array.prototype.unshift : Array.prototype.push).apply(resource._bodyClickEventHandlers, handlers);
      return;
    }

    resource._bodyClickEventHandlers = handlers;
    var selector = observeElementSelector || 'body';
    $$(selector)[0].observe('click', function (event) {
      resource._bodyClickEventHandlers && resource._bodyClickEventHandlers.each(function (clickEventHandler) {
        var result = clickEventHandler(event); //if handler returns some result
        //this means that we have found necessary handler so
        //do not need to process other
        //if handler returns some result
        //this means that we have found necessary handler so
        //do not need to process other

        if (result) {
          if (result !== resource.PROPAGATE_EVENT) {
            Event.stop(event);
          }

          throw $break;
        }
      });
    });
  },
  TreeWrapper: function TreeWrapper(options) {
    var that = this;
    this._treeId = options.treeId;
    this._resourceUriInput = $(options.resourceUriInput || 'resourceUri');
    this._uri = this._resourceUriInput && this._resourceUriInput.getValue() || options.uri || '/';
    if (!options.providerId) throw 'There is no tree provider set for tree #{id}'.interpolate({
      id: this._treeId
    }); // Setup folders tree
    // Setup folders tree

    var treeOptions = ['providerId', 'rootUri', 'organizationId', 'publicFolderUri', 'urlGetNode', 'urlGetChildren'].inject({}, function (treeOptions, key) {
      options[key] !== null && (treeOptions[key] = options[key]);
      return treeOptions;
    });
    this._tree = new dynamicTree.createRepositoryTree(this._treeId, treeOptions);

    this._tree.observe('tree:loaded', function () {
      that._tree.openAndSelectNode($(that._resourceUriInput).getValue());
    });

    this._tree.observe('leaf:selected', function (event) {
      that._uri = event.memo.node.param.uri;

      that._resourceUriInput.setValue(that._uri);
    });

    this._tree.observe('node:selected', function () {
      that._resourceUriInput.setValue(that._uri = '');
    });

    return {
      getTreeId: function getTreeId() {
        return that._treeId;
      },
      getTree: function getTree() {
        return that._tree;
      },
      selectFolder: function selectFolder(folderUri) {
        that._tree.openAndSelectNode(folderUri);
      },
      getSelectedFolderUri: function getSelectedFolderUri() {
        return that._uri;
      }
    };
  },
  ////////////////////////////////
  // Utility Methods
  ////////////////////////////////
  switchButtonState: function switchButtonState(button, state) {
    buttonManager[state ? 'enable' : 'disable'].call(buttonManager, button);
  },
  switchDisableState: function switchDisableState(element, disable) {
    (element = $(element)) && element[disable ? 'disable' : 'enable'].call(element);
  },
  generateResourceId: function generateResourceId(name) {
    if (window.localContext && window.localContext.initOptions && window.localContext.initOptions.resourceIdNotSupportedSymbols) {
      return name.replace(new RegExp(window.localContext.initOptions.resourceIdNotSupportedSymbols, 'g'), '_');
    } else {
      throw 'There is no resourceIdNotSupportedSymbols property in init options.';
    }
  },
  testResourceId: function testResourceId(resourceId) {
    if (window.localContext && window.localContext.initOptions && window.localContext.initOptions.resourceIdNotSupportedSymbols) {
      return new RegExp(window.localContext.initOptions.resourceIdNotSupportedSymbols, 'g').test(resourceId);
    } else {
      throw 'There is no resourceIdNotSupportedSymbols property in init options.';
    }
  },
  labelValidator: function labelValidator(value) {
    var isValid = true;
    var errorMessage = '';

    if (value.blank()) {
      errorMessage = resource.messages['labelIsEmpty'];
      isValid = false;
    } else if (value.length > resource.resourceLabelMaxLength) {
      errorMessage = resource.messages['labelToLong'];
      isValid = false;
    }

    return {
      isValid: isValid,
      errorMessage: errorMessage
    };
  },
  getLabelValidationEntry: function getLabelValidationEntry(element) {
    return {
      element: element,
      validators: [{
        method: 'mandatory',
        messages: {
          mandatory: resource.messages['labelIsEmpty']
        }
      }, {
        method: 'minMaxLength',
        messages: {
          tooLong: resource.messages['labelToLong']
        },
        options: {
          maxLength: resource.resourceLabelMaxLength
        }
      }]
    };
  },
  getIdValidationEntry: function getIdValidationEntry(element) {
    return {
      element: element,
      validators: [{
        method: 'resourceIdChars',
        messages: resource.messages
      }, {
        method: 'mandatory',
        messages: {
          mandatory: resource.messages['resourceIdIsEmpty']
        }
      }, {
        method: 'minMaxLength',
        messages: {
          tooLong: resource.messages['resourceIdToLong']
        },
        options: {
          maxLength: resource.resourceIdMaxLength
        }
      }]
    };
  },

  /**
   * The context of this method should contain _isEditMode property. Id it is true then validator will not validate
   * the value but will return isValid=true.
   */
  resourceIdValidator: function resourceIdValidator(value) {
    var isValid = true;
    var errorMessage = '';

    if (!this._isEditMode) {
      if (value.blank()) {
        errorMessage = resource.messages['resourceIdIsEmpty'];
        isValid = false;
      } else if (value.length > resource.resourceIdMaxLength) {
        errorMessage = resource.messages['resourceIdToLong'];
        isValid = false;
      } else if (resource.testResourceId(value)) {
        errorMessage = resource.messages['resourceIdInvalidChars'];
        isValid = false;
      }
    }

    return {
      isValid: isValid,
      errorMessage: errorMessage
    };
  },
  getDescriptionValidationEntry: function getDescriptionValidationEntry(element) {
    return {
      element: element,
      validators: [{
        method: 'minMaxLength',
        messages: {
          tooLong: resource.messages['descriptionToLong']
        },
        options: {
          maxLength: resource.resourceDescriptionMaxLength
        }
      }]
    };
  },
  descriptionValidator: function descriptionValidator(value) {
    var isValid = true;
    var errorMessage = '';

    if (value.length > resource.resourceDescriptionMaxLength) {
      errorMessage = resource.messages['descriptionToLong'];
      isValid = false;
    }

    return {
      isValid: isValid,
      errorMessage: errorMessage
    };
  },
  dataSourceValidator: function dataSourceValidator(value) {
    var isValid = true;
    var errorMessage = '';

    if (value.trim() === '') {
      errorMessage = resource.messages['dataSourceInvalid'];
      isValid = false;
    }

    return {
      isValid: isValid,
      errorMessage: errorMessage
    };
  },
  queryValidator: function queryValidator(value) {
    var isValid = true;
    var errorMessage = '';

    if (value.trim() === '') {
      errorMessage = resource.messages['queryInvalid'];
      isValid = false;
    }

    return {
      isValid: isValid,
      errorMessage: errorMessage
    };
  },
  getValidationEntries: function getValidationEntries(elementsToValidate) {
    // To use this method all elements should have validator or validationEntry property set.
    return elementsToValidate.collect(function (element) {
      if (element.validationEntry) {
        return element.validationEntry;
      } else {
        return {
          validator: element.validator,
          element: element
        };
      }
    });
  }
};

if (typeof require === 'undefined') {
  // prevent conflict with domReady plugin in RequireJS environment
  isIPad() && document.observe('dom:loaded', resource.initSwipeScroll.bind(resource));
}

module.exports = resource;

});