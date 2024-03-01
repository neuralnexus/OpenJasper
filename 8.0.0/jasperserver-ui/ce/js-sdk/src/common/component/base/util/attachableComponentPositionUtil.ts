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

import $ from 'jquery';

interface PosFunctionOptions {
    attachToOffset: {
        top: number,
        left: number
    },
    attachToSize: {
        width: number,
        height: number
    },
    elementSize: {
        width: number,
        height: number
    },
    padding: {
        top: number,
        left: number
    }
}

const getInitialPos = (options: PosFunctionOptions) => {
    return {
        top: options.attachToOffset.top + options.attachToSize.height + options.padding.top,
        left: options.attachToOffset.left
    }
};

const placeElementAboveAttachTo = (options: PosFunctionOptions) => {
    return options.attachToOffset.top - options.elementSize.height - options.padding.top;
};

const placeElementOnTheRightSideOfAttachTo = (options: PosFunctionOptions) => {
    return options.attachToOffset.left + options.attachToSize.width;
};

const placeElementOnTheLeftSideOfAttachTo = (options: PosFunctionOptions) => {
    return options.attachToOffset.left - options.elementSize.width;
};

const alignElementVertically = (options: PosFunctionOptions) => {
    return options.attachToOffset.top - options.elementSize.height / 2 - options.padding.top;
};

const alignElementHorizontally = (options: PosFunctionOptions) => {
    return options.attachToOffset.left + options.attachToSize.width / 2 - options.elementSize.width / 2;
};

const alignElementByRightEdgeOfAttachTo = (options: PosFunctionOptions) => {
    return options.attachToOffset.left + options.attachToSize.width - options.elementSize.width;
};

export default {
    getPosition(
        attachTo: HTMLElement,
        padding: {top: number, left: number} = { top: 0, left: 0 },
        element: HTMLElement,
        jQuery?: (e: HTMLElement|string) => any
    ): {top: number, left: number} {
        const query = jQuery || $;

        const body = query('body');
        const $attachTo = query(attachTo);
        const $el = query(element);

        const attachToOffset = $attachTo.offset() || { top: 0, left: 0 };
        const attachToHeight = $attachTo[0].tagName && $attachTo[0].tagName.toLowerCase() === 'input'
            ? $attachTo.outerHeight() || 0 : $attachTo.height() || 0;
        const attachToWidth = $attachTo.width() || 0;

        const bodyHeight = body.height() || 0;
        const bodyWidth = body.width() || 0;
        const elementWidth = $el.innerWidth() || 0;
        const elementHeight = $el.innerHeight() || 0;

        const options = {
            attachToOffset,
            attachToSize: {
                width: attachToWidth,
                height: attachToHeight
            },
            elementSize: {
                width: elementWidth,
                height: elementHeight
            },
            padding
        };

        const initialPos = getInitialPos(options);

        let { top, left } = initialPos;

        let verticallyAligned = false;

        const elementIntersectsViewPortBottomBoundary = top + elementHeight > bodyHeight;

        if (elementIntersectsViewPortBottomBoundary) {
            top = placeElementAboveAttachTo(options);
        }

        const elementIntersectsViewportTopBoundary = top < 0;

        if (elementIntersectsViewportTopBoundary) {
            top = alignElementVertically(options);
            left = placeElementOnTheRightSideOfAttachTo(options);

            verticallyAligned = true;
        }

        const elementIntersectsViewportRightBoundary = left + elementWidth > bodyWidth;

        if (elementIntersectsViewportRightBoundary) {
            left = verticallyAligned
                ? placeElementOnTheLeftSideOfAttachTo(options)
                : alignElementByRightEdgeOfAttachTo(options);
        }

        const elementIntersectsViewportLeftBoundary = left < 0;

        if (elementIntersectsViewportLeftBoundary) {
            left = alignElementHorizontally(options);
        }

        // when viewport is still to small to fit the element
        if (top < 0) {
            top = 0;
        }

        if (left < 0) {
            left = 0;
        }

        return {
            top,
            left,
        };
    },
};
