define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var getInitialPos = function getInitialPos(options) {
  return {
    top: options.attachToOffset.top + options.attachToSize.height + options.padding.top,
    left: options.attachToOffset.left
  };
};

var placeElementAboveAttachTo = function placeElementAboveAttachTo(options) {
  return options.attachToOffset.top - options.elementSize.height - options.padding.top;
};

var placeElementOnTheRightSideOfAttachTo = function placeElementOnTheRightSideOfAttachTo(options) {
  return options.attachToOffset.left + options.attachToSize.width;
};

var placeElementOnTheLeftSideOfAttachTo = function placeElementOnTheLeftSideOfAttachTo(options) {
  return options.attachToOffset.left - options.elementSize.width;
};

var alignElementVertically = function alignElementVertically(options) {
  return options.attachToOffset.top - options.elementSize.height / 2 - options.padding.top;
};

var alignElementHorizontally = function alignElementHorizontally(options) {
  return options.attachToOffset.left + options.attachToSize.width / 2 - options.elementSize.width / 2;
};

var alignElementByRightEdgeOfAttachTo = function alignElementByRightEdgeOfAttachTo(options) {
  return options.attachToOffset.left + options.attachToSize.width - options.elementSize.width;
};

module.exports = {
  getPosition: function getPosition(attachTo) {
    var padding = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {
      top: 0,
      left: 0
    };
    var element = arguments.length > 2 ? arguments[2] : undefined;
    var jQuery = arguments.length > 3 ? arguments[3] : undefined;
    var query = jQuery || $;
    var body = query('body');
    var $attachTo = query(attachTo);
    var $el = query(element);
    var attachToOffset = $attachTo.offset() || {
      top: 0,
      left: 0
    };
    var attachToHeight = $attachTo[0].tagName && $attachTo[0].tagName.toLowerCase() === 'input' ? $attachTo.outerHeight() || 0 : $attachTo.height() || 0;
    var attachToWidth = $attachTo.width() || 0;
    var bodyHeight = body.height() || 0;
    var bodyWidth = body.width() || 0;
    var elementWidth = $el.innerWidth() || 0;
    var elementHeight = $el.innerHeight() || 0;
    var options = {
      attachToOffset: attachToOffset,
      attachToSize: {
        width: attachToWidth,
        height: attachToHeight
      },
      elementSize: {
        width: elementWidth,
        height: elementHeight
      },
      padding: padding
    };
    var initialPos = getInitialPos(options);
    var top = initialPos.top,
        left = initialPos.left;
    var verticallyAligned = false;
    var elementIntersectsViewPortBottomBoundary = top + elementHeight > bodyHeight;

    if (elementIntersectsViewPortBottomBoundary) {
      top = placeElementAboveAttachTo(options);
    }

    var elementIntersectsViewportTopBoundary = top < 0;

    if (elementIntersectsViewportTopBoundary) {
      top = alignElementVertically(options);
      left = placeElementOnTheRightSideOfAttachTo(options);
      verticallyAligned = true;
    }

    var elementIntersectsViewportRightBoundary = left + elementWidth > bodyWidth;

    if (elementIntersectsViewportRightBoundary) {
      left = verticallyAligned ? placeElementOnTheLeftSideOfAttachTo(options) : alignElementByRightEdgeOfAttachTo(options);
    }

    var elementIntersectsViewportLeftBoundary = left < 0;

    if (elementIntersectsViewportLeftBoundary) {
      left = alignElementHorizontally(options);
    } // when viewport is still to small to fit the element


    if (top < 0) {
      top = 0;
    }

    if (left < 0) {
      left = 0;
    }

    return {
      top: top,
      left: left
    };
  }
};

});