define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _utilUtilsCommon = require("../util/utils.common");

var fitObjectIntoScreen = _utilUtilsCommon.fitObjectIntoScreen;
var getScrollLeft = _utilUtilsCommon.getScrollLeft;
var getBoxOffsets = _utilUtilsCommon.getBoxOffsets;
var getParentDiv = _utilUtilsCommon.getParentDiv;
var getScrollTop = _utilUtilsCommon.getScrollTop;

var jQuery = require('jquery');

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
var customTooltip = {};
customTooltip.TOOLTIP_ID = "custTooltip";

function makeCustomTooltip(evt, tipText) {
  //cleanup old one just in case
  hideCustomTooltip(evt); //make a new one

  var evt = evt ? evt : window.event;
  var myTooltip = document.getElementById('customTooltipTemplate').cloneNode(true);

  if (!tipText) {
    myTooltip.style.display = "none";
  } else {
    myTooltip.style.display = "block";
  }

  myTooltip.setAttribute('id', customTooltip.TOOLTIP_ID);
  jQuery(myTooltip).html(tipText);

  myTooltip.onmouseover = function (event) {
    hideCustomTooltip(event);
  };

  return myTooltip;
}

function addCustomTooltip(myTooltip) {
  document.body.appendChild(myTooltip);
  fitObjectIntoScreen(myTooltip, myTooltip.style.left, myTooltip.style.top, myTooltip.clientWidth, myTooltip.clientWidth);
}

function showCustomTooltip(evt, tipText, tipWidth, tipColor, tipBgColor, topOffset) {
  var myTooltip = makeCustomTooltip(evt, tipText);

  if (tipWidth) {
    myTooltip.style.maxWidth = tipWidth;
  }

  if (tipBgColor) {
    myTooltip.style.backgroundColor = tipBgColor;
  }

  if (tipColor) {
    myTooltip.style.color = tipColor;
  }

  myTooltip.style.left = evt.clientX + getScrollLeft();
  myTooltip.style.top = evt.clientY + getScrollTop() + 5 + (topOffset ? topOffset : 0);
  addCustomTooltip(myTooltip);
  return myTooltip;
}

function showCustomTooltipBelowObject(evt, tipText, tipWidth, tipClassName, theObject) {
  if (!theObject.parentNode) {
    //looks like whatever we were hovering over has now been dereferenced (page reload?)
    //do nothing
    return;
  }

  var myTooltip = makeCustomTooltip(evt, tipText);

  if (tipWidth) {
    myTooltip.style.maxWidth = tipWidth;
  }

  if (tipClassName) {
    myTooltip.className = tipClassName;
  }

  var objOffsets = getBoxOffsets(theObject);
  var tipTop = objOffsets[1] + theObject.clientHeight + 5;
  myTooltip.style.left = objOffsets[0];
  myTooltip.style.top = tipTop;
  addCustomTooltip(myTooltip);
  return myTooltip;
}

function updateCustomTooltip(text) {
  var myTooltip = document.getElementById(customTooltip.TOOLTIP_ID);

  if (myTooltip) {
    var myTooltipCell = myTooltip.getElementsByTagName('TD')[0];
    jQuery(myTooltipCell).html(text);
    myTooltip.style.display = "block";
    fitObjectIntoScreen(myTooltip, myTooltip.style.left, myTooltip.style.top, myTooltipCell.offsetWidth, myTooltipCell.offsetHeight);
  }
}

function hideCustomTooltip(evt) {
  var evt = evt ? evt : window.event;
  var newTarget = evt.explicitOriginalTarget ? getParentDiv(evt.explicitOriginalTarget) : null; //not if we are hovering over tooltip itself (FF only)

  if (newTarget && newTarget.getAttribute("id") == customTooltip.TOOLTIP_ID) {
    return;
  }

  var myTooltip = document.getElementById(customTooltip.TOOLTIP_ID);

  if (myTooltip) {
    if (myTooltip.parentNode) {
      myTooltip.parentNode.removeChild(myTooltip);
    }

    myTooltip = null;
  }
}

exports.customTooltip = customTooltip;
exports.showCustomTooltip = showCustomTooltip;
exports.hideCustomTooltip = hideCustomTooltip;
exports.showCustomTooltipBelowObject = showCustomTooltipBelowObject;

});