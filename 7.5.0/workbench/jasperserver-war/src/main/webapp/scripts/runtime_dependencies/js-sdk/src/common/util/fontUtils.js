define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(source, true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(source).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

var internalCache = {
  element: null,
  svgElement: null,
  getTextRectPool: {},
  findFontSizePool: {}
};
var debugEnabled = false;

var debug = function debug() {
  if (!debugEnabled) {
    return;
  } // eslint-disable-next-line no-console


  console.log.apply(console, arguments);
};

var buildCacheKey = function buildCacheKey(options) {
  var cacheKey = [];

  _.each(options, function (value, name) {
    cacheKey.push(name + ":" + value);
  });

  return cacheKey.join("_");
};

var getTextRect = function getTextRect(text, fontOptions) {
  var fontSize = fontOptions.fontSize,
      sizeUnits = fontOptions.sizeUnits,
      fontWeight = fontOptions.fontWeight,
      lineHeight = fontOptions.lineHeight;
  var cacheKey = buildCacheKey({
    t: text,
    s: fontSize,
    u: sizeUnits,
    w: fontWeight,
    h: lineHeight
  });

  if (internalCache.getTextRectPool[cacheKey]) {
    return internalCache.getTextRectPool[cacheKey];
  }

  if (!internalCache.element) {
    var _span = document.createElement("span");

    _span.innerText = 'some text';
    _span.style.fontFamily = '"Lucida Grande", "Lucida Sans Unicode", Arial, Helvetica, sans-serif';
    _span.style.fontSize = '12px';
    _span.style.fontWeight = 'normal';
    _span.style.lineHeight = 'normal';
    _span.style.opacity = '0';
    _span.style.position = 'absolute';
    _span.style.top = '-9999px';
    _span.style.left = '-9999px';
    document.body.appendChild(_span);
    internalCache.element = _span;
  }

  var span = internalCache.element;
  span.innerText = text;
  span.style.fontSize = fontSize + sizeUnits;
  span.style.fontWeight = fontWeight;
  span.style.lineHeight = lineHeight;
  var result = {
    width: Math.ceil(span.offsetWidth),
    height: Math.ceil(span.offsetHeight)
  };
  internalCache.getTextRectPool[cacheKey] = result;
  return result;
};

var getSVGTextRect = function getSVGTextRect(linesInTextNode) {
  if (!internalCache.svgElement) {
    var _svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');

    _svg.setAttribute('version', '1.1');

    _svg.setAttribute('xlink', 'http://www.w3.org/1999/xlink');

    _svg.setAttribute('width', '600');

    _svg.setAttribute('height', '600');

    _svg.setAttribute('viewBox', '0 0 600 600');

    _svg.style.opacity = '0';
    _svg.style.position = 'absolute';
    _svg.style.top = '-99999px';
    _svg.style.left = '-99999px';
    _svg.style.fontFamily = '"Lucida Grande", "Lucida Sans Unicode", Arial, Helvetica, sans-serif';
    _svg.style.fontSize = '12px';
    _svg.style.fontWeight = 'normal';
    _svg.style.lineHeight = 'normal';
    var text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    text.setAttribute('x', '0');
    text.setAttribute('y', '0');

    _svg.appendChild(text);

    document.body.appendChild(_svg);
    internalCache.svgElement = _svg;
  }

  var svg = internalCache.svgElement;
  var textNode = svg.childNodes[0]; // remove all child nodes

  while (textNode.childNodes.length) {
    textNode.removeChild(textNode.childNodes[0]);
  }

  _.each(linesInTextNode, function (line, index) {
    var tspan = document.createElementNS('http://www.w3.org/2000/svg', 'tspan');
    tspan.setAttribute('x', '0');
    var vertOffsetAttributeName = index === 0 ? 'y' : 'dy';
    tspan.setAttribute(vertOffsetAttributeName, line.y || '0');
    tspan.style.fontSize = line.fontSize + line.sizeUnits;
    tspan.style.fontWeight = line.fontWeight;
    tspan.style.lineHeight = line.lineHeight;
    tspan.textContent = line.text;
    textNode.appendChild(tspan);
  });

  var rect;

  if (textNode.getBBox) {
    rect = textNode.getBBox();
  } else {
    rect = {
      width: textNode.offsetWidth,
      height: textNode.offsetHeight
    };
  }

  return {
    width: Math.ceil(rect.width),
    height: Math.ceil(rect.height)
  };
};

var parseOptions = function parseOptions(options) {
  var text = options.text,
      sizeUnits = options.sizeUnits,
      fontWeight = options.fontWeight,
      lineHeight = options.lineHeight,
      minimalFontSize = options.minimalFontSize,
      fontCheckingStrategy = options.fontCheckingStrategy,
      widthAvailable = options.widthAvailable,
      heightAvailable = options.heightAvailable;
  var result = {
    makesSenseToContinue: false,
    fontSize: 12,
    lineHeight: getFontHeight(12),
    sizeUnits: "px"
  };

  if (_.isUndefined(text)) {
    // no text ? ok, nothing to do here
    return result;
  }

  text = text.toString();

  if (_.isUndefined(heightAvailable) && _.isUndefined(widthAvailable)) {
    // user hasn't specified the area to test, nothing to do here
    return result;
  }

  if (_.isUndefined(heightAvailable)) {
    heightAvailable = -1;
  }

  if (_.isUndefined(widthAvailable)) {
    widthAvailable = -1;
  }

  if (_.isUndefined(minimalFontSize)) {
    minimalFontSize = 1;
  } else {
    minimalFontSize = parseInt(minimalFontSize);

    if (_.isNaN(minimalFontSize)) {
      minimalFontSize = 1;
    } // it can't be less 1


    minimalFontSize = Math.max(1, minimalFontSize);
  }

  if (_.isUndefined(fontCheckingStrategy)) {
    fontCheckingStrategy = 'basedOnActualSize';
  }

  if (_.isUndefined(sizeUnits)) {
    sizeUnits = "px";
  }

  if (_.isUndefined(fontWeight)) {
    fontWeight = "normal";
  }

  if (_.isUndefined(lineHeight)) {
    lineHeight = "normal";
  }

  return _objectSpread({}, result, {
    makesSenseToContinue: true,
    text: text,
    sizeUnits: sizeUnits,
    fontWeight: fontWeight,
    lineHeight: lineHeight,
    minimalFontSize: minimalFontSize,
    fontCheckingStrategy: fontCheckingStrategy,
    widthAvailable: widthAvailable,
    heightAvailable: heightAvailable
  });
};

var findFontSize = function findFontSize(options) {
  debug('findFontSize(): incoming options:', options);

  var _parseOptions = parseOptions(options),
      makesSenseToContinue = _parseOptions.makesSenseToContinue,
      text = _parseOptions.text,
      fontSize = _parseOptions.fontSize,
      fontWeight = _parseOptions.fontWeight,
      lineHeight = _parseOptions.lineHeight,
      sizeUnits = _parseOptions.sizeUnits,
      minimalFontSize = _parseOptions.minimalFontSize,
      fontCheckingStrategy = _parseOptions.fontCheckingStrategy,
      widthAvailable = _parseOptions.widthAvailable,
      heightAvailable = _parseOptions.heightAvailable;

  var result = {
    fontSize: fontSize,
    sizeUnits: sizeUnits
  };

  if (!makesSenseToContinue) {
    return result;
  }

  var cacheKey = buildCacheKey({
    t: text.length,
    w: widthAvailable,
    h: heightAvailable,
    c: fontCheckingStrategy
  });

  if (internalCache.findFontSizePool[cacheKey]) {
    return internalCache.findFontSizePool[cacheKey];
  }

  debug('findFontSize: calculating for key:', cacheKey);

  var testIfFontFits = function testIfFontFits(text, fontOptions) {
    var fits = true,
        textRect = {
      width: 0,
      height: 0
    }; // the test can be done in two ways:
    // 1) based on how much the browser takes space to render the font
    // 2) based on fontHeight which is just a result of fontSize
    // let's see if we need to get actual size for case 1 OR do we need to know the width
    // for case 2 (widthAvailable !== -1 means we need to check width)

    if (fontCheckingStrategy === 'basedOnActualSize' || widthAvailable !== -1) {
      textRect = getTextRect(text, fontOptions);
    }

    if (fontCheckingStrategy === 'basedOnFontHeight') {
      if (getFontHeight(fontOptions.fontSize) > heightAvailable) {
        fits = false;
      }

      if (textRect.width > widthAvailable) {
        fits = false;
      }
    } else {
      if (heightAvailable !== -1) {
        if (textRect.height > heightAvailable) {
          fits = false;
        }
      }

      if (widthAvailable !== -1) {
        if (textRect.width > widthAvailable) {
          fits = false;
        }
      }
    }

    return {
      fits: fits,
      width: textRect.width,
      height: textRect.height
    };
  };

  var fontOptions = {
    sizeUnits: sizeUnits,
    fontWeight: fontWeight,
    lineHeight: lineHeight
  };
  var testResult;
  var testFontSize;
  var biggestSize;
  var smallestSize;
  var i;
  debug('testing font size:', minimalFontSize);
  testResult = testIfFontFits(text, _objectSpread({}, fontOptions, {
    fontSize: minimalFontSize
  }));
  debug('testResult:', testResult);

  if (!testResult.fits) {
    debug('The minimum font size ' + minimalFontSize + ' doesnt fit, so nothing to do here...');
    result.fontSize = minimalFontSize;
    internalCache.findFontSizePool[cacheKey] = result;
    return result;
  } // first try to find the biggest font which doesn't fit the area


  biggestSize = minimalFontSize + 10;
  smallestSize = minimalFontSize;
  i = 0;

  do {
    debug('---------------- looking for biggest font which doesnt fit: ----------------');
    debug('testing range: [', smallestSize, ',', biggestSize, ']');
    testResult = testIfFontFits(text, _objectSpread({}, fontOptions, {
      fontSize: biggestSize
    }));
    debug('testResult:', testResult);

    if (testResult.fits) {
      smallestSize = biggestSize;
      biggestSize = Math.ceil(biggestSize + (biggestSize - minimalFontSize) / 2);
      debug('fits, increasing range... the new range is: [', smallestSize, ',', biggestSize, ']');
    } else {
      debug('doesnt fit, stopping');
    }

    if (i++ > 100) {
      debug('emergency stop');
      internalCache.findFontSizePool[cacheKey] = result;
      return result;
    }
  } while (testResult.fits);

  debug('found range: [', smallestSize, ',', biggestSize, ']'); // now in that range from smallestSize to biggestSize try to find the one which fits and the biggest
  // the smallestSize is tested or the minimum and potentially can be a return value.
  // the biggestSize is tested and doesn't fit
  // in the next loop we'll set smallest to the font which fits and biggest to the font which doesn't fit

  i = 0;

  do {
    debug('---------------- looking for biggest font which does fit: ----------------');

    if (biggestSize - smallestSize === 1) {
      debug('found the biggest font which does fit or is the minimum requested:', smallestSize);
      break;
    }

    testFontSize = Math.ceil((biggestSize + smallestSize) / 2);
    debug('testing range [', smallestSize, ',', biggestSize, '] with font size of', testFontSize);
    testResult = testIfFontFits(text, _objectSpread({}, fontOptions, {
      fontSize: testFontSize
    }));
    debug('testResult:', testResult);

    if (testResult.fits) {
      smallestSize = testFontSize;
      debug('fits, changing font range to: [', smallestSize, ',', biggestSize, ']');
    } else {
      biggestSize = testFontSize;
      debug('doesnt fit, changing font range to: [', smallestSize, ',', biggestSize, ']');
    }

    if (i++ > 100) {
      debug('emergency stop');
      internalCache.findFontSizePool[cacheKey] = result;
      return result;
    }
  } while (biggestSize > smallestSize);

  result.fontSize = smallestSize;
  internalCache.findFontSizePool[cacheKey] = result;
  return result;
};

var getFontHeight = function getFontHeight(fontSize) {
  // took from Highcharts library
  return fontSize < 24 ? fontSize + 3 : fontSize * 1.2;
};

var getFontBaseline = function getFontBaseline(fontSize) {
  // took from Highcharts library
  return Math.round(getFontHeight(fontSize) * 0.8);
};

module.exports = {
  getTextRect: getTextRect,
  getSVGTextRect: getSVGTextRect,
  findFontSize: findFontSize,
  getFontHeight: getFontHeight,
  getFontBaseline: getFontBaseline
};

});