/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

import _ from 'underscore';

const internalCache = {
    element: null,
    svgElement: null,
    getTextRectPool: {},
    findFontSizePool: {}
};

let debugEnabled = false;
const debug = function () {
    if (!debugEnabled) {
        return;
    }
    // eslint-disable-next-line no-console
    console.log.apply(console, arguments);
};

const buildCacheKey = function (options) {
    let cacheKey = [];
    _.each(options, (value, name) => {
        cacheKey.push(name + ":" + value);
    });
    return cacheKey.join("_");
};

const getTextRect = function (text, fontOptions) {

    const {
        fontSize,
        sizeUnits,
        fontWeight,
        lineHeight
    } = fontOptions;

    const cacheKey = buildCacheKey({
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
        let span = document.createElement("span");
        span.innerText = 'some text';
        span.style.fontFamily = '"Lucida Grande", "Lucida Sans Unicode", Arial, Helvetica, sans-serif';
        span.style.fontSize = '12px';
        span.style.fontWeight = 'normal';
        span.style.lineHeight = 'normal';
        span.style.opacity = '0';
        span.style.position = 'absolute';
        span.style.top = '-9999px';
        span.style.left = '-9999px';
        document.body.appendChild(span);
        internalCache.element = span;
    }

    let span = internalCache.element;

    span.innerText = text;
    span.style.fontSize = fontSize + sizeUnits;
    span.style.fontWeight = fontWeight;
    span.style.lineHeight = lineHeight;

    const result = {
        width: Math.ceil(span.offsetWidth),
        height: Math.ceil(span.offsetHeight)
    };

    internalCache.getTextRectPool[cacheKey] = result;

    return result;
};

const getSVGTextRect = function (linesInTextNode) {

    if (!internalCache.svgElement) {
        let svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('version', '1.1');
        svg.setAttribute('xlink', 'http://www.w3.org/1999/xlink');
        svg.setAttribute('width', '600');
        svg.setAttribute('height', '600');
        svg.setAttribute('viewBox', '0 0 600 600');
        svg.style.opacity = '0';
        svg.style.position = 'absolute';
        svg.style.top = '-99999px';
        svg.style.left = '-99999px';
        svg.style.fontFamily = '"Lucida Grande", "Lucida Sans Unicode", Arial, Helvetica, sans-serif';
        svg.style.fontSize = '12px';
        svg.style.fontWeight = 'normal';
        svg.style.lineHeight = 'normal';

        let text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        text.setAttribute('x', '0');
        text.setAttribute('y', '0');

        svg.appendChild(text);

        document.body.appendChild(svg);

        internalCache.svgElement = svg;
    }

    let svg = internalCache.svgElement;
    let textNode = svg.childNodes[0];

    // remove all child nodes
    while (textNode.childNodes.length) {
        textNode.removeChild(textNode.childNodes[0]);
    }

    _.each(linesInTextNode, (line, index) => {
        let tspan = document.createElementNS('http://www.w3.org/2000/svg', 'tspan');

        tspan.setAttribute('x', '0');

        let vertOffsetAttributeName = index === 0 ? 'y' : 'dy';
        tspan.setAttribute(vertOffsetAttributeName, line.y || '0');

        tspan.style.fontSize = line.fontSize + line.sizeUnits;
        tspan.style.fontWeight = line.fontWeight;
        tspan.style.lineHeight = line.lineHeight;
        tspan.textContent = line.text;

        textNode.appendChild(tspan);
    });

    let rect;

    if (textNode.getBBox) {
        rect = textNode.getBBox();
    } else {
        rect = {
            width: textNode.offsetWidth,
            height: textNode.offsetHeight
        }
    }

    return {
        width: Math.ceil(rect.width),
        height: Math.ceil(rect.height)
    };
};

const parseOptions = function (options) {
    let {
        text,
        sizeUnits,
        fontWeight,
        lineHeight,
        minimalFontSize,
        fontCheckingStrategy,
        widthAvailable,
        heightAvailable
    } = options;

    const result = {
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
        }
        // it can't be less 1
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

    return {
        ...result,
        makesSenseToContinue: true,
        text,
        sizeUnits,
        fontWeight,
        lineHeight,
        minimalFontSize,
        fontCheckingStrategy,
        widthAvailable,
        heightAvailable
    }
};

const findFontSize = function(options) {

    debug('findFontSize(): incoming options:', options);

    let {
        makesSenseToContinue,
        text,
        fontSize,
        fontWeight,
        lineHeight,
        sizeUnits,
        minimalFontSize,
        fontCheckingStrategy,
        widthAvailable,
        heightAvailable
    } = parseOptions(options);

    const result = {
        fontSize,
        sizeUnits
    };

    if (!makesSenseToContinue) {
        return result;
    }

    const cacheKey = buildCacheKey({
        t: text.length,
        w: widthAvailable,
        h: heightAvailable,
        c: fontCheckingStrategy
    });

    if (internalCache.findFontSizePool[cacheKey]) {
        return internalCache.findFontSizePool[cacheKey];
    }

    debug('findFontSize: calculating for key:', cacheKey);

    const testIfFontFits = (text, fontOptions) => {
        let
            fits = true,
            textRect = {
                width: 0,
                height: 0
            };

        // the test can be done in two ways:
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
            fits,
            width: textRect.width,
            height: textRect.height
        };
    };

    const fontOptions = {
        sizeUnits,
        fontWeight,
        lineHeight
    };

    let testResult;
    let testFontSize;
    let biggestSize;
    let smallestSize;
    let i;

    debug('testing font size:', minimalFontSize);
    testResult = testIfFontFits(text, {...fontOptions, fontSize: minimalFontSize});
    debug('testResult:', testResult);
    if (!testResult.fits) {
        debug('The minimum font size '+ minimalFontSize + ' doesnt fit, so nothing to do here...');
        result.fontSize = minimalFontSize;
        internalCache.findFontSizePool[cacheKey] = result;
        return result;
    }

    // first try to find the biggest font which doesn't fit the area
    biggestSize = minimalFontSize + 10;
    smallestSize = minimalFontSize;
    i = 0;
    do {
        debug('---------------- looking for biggest font which doesnt fit: ----------------');
        debug('testing range: [', smallestSize, ',', biggestSize, ']');
        testResult = testIfFontFits(text, {...fontOptions, fontSize: biggestSize});
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

    debug('found range: [', smallestSize, ',', biggestSize, ']');

    // now in that range from smallestSize to biggestSize try to find the one which fits and the biggest
    // the smallestSize is tested or the minimum and potentially can be a return value.
    // the biggestSize is tested and doesn't fit
    // in the next loop we'll set smallest to the font which fits and biggest to the font which doesn't fit
    i = 0;
    do {
        debug('---------------- looking for biggest font which does fit: ----------------');

        if ((biggestSize - smallestSize) === 1) {
            debug('found the biggest font which does fit or is the minimum requested:', smallestSize);
            break;
        }

        testFontSize = Math.ceil((biggestSize + smallestSize) / 2);
        debug('testing range [', smallestSize, ',', biggestSize, '] with font size of', testFontSize);
        testResult = testIfFontFits(text, {...fontOptions, fontSize: testFontSize});
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

const getFontHeight = function (fontSize) {
    // took from Highcharts library
    return fontSize < 24 ? fontSize + 3 : fontSize * 1.2;
};

const getFontBaseline = function (fontSize) {
    // took from Highcharts library
    return Math.round(getFontHeight(fontSize) * 0.8);
};

export default {
    getTextRect,
    getSVGTextRect,
    findFontSize,
    getFontHeight,
    getFontBaseline
};
