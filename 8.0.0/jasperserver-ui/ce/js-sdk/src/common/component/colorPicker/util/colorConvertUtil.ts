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

const RGBA_REGEX = /^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d+(?:\.\d+)?))?\)$/;
const THRESHOLD = 127.5;

export default {
    rgba2NoAlphaHex(color: string): string {
        const rgb = color.match(RGBA_REGEX) || [];
        const hexValue = '#';

        return [rgb[1], rgb[2], rgb[3]].reduce((memo, val) => {
            const hex = (`0${parseInt(val, 10).toString(16)}`).slice(-2);
            return memo + hex;
        }, hexValue).toUpperCase();
    },
    isRgbTransparent: (rgb: string): boolean => {
        return rgb.replace(/\s/g, '').indexOf('0,0,0,0') !== -1;
    },
    isRgba: (rgb: string): boolean => {
        return RGBA_REGEX.test(rgb);
    },
    isColorDark: (color: string): boolean => {
        let colour,
            r,
            g,
            b;

        if (/^rgb/.test(color)) {
            colour = color.match(RGBA_REGEX) || [];

            r = parseInt(colour[1], 10);
            g = parseInt(colour[2], 10);
            b = parseInt(colour[3], 10);
        } else {
            colour = color.substr(1);

            r = parseInt(`${colour[0]}${colour[1]}`, 16);
            g = parseInt(`${colour[2]}${colour[3]}`, 16);
            b = parseInt(`${colour[4]}${colour[5]}`, 16);
        }

        const rgb = Math.sqrt(
            0.299 * (r * r)
            + 0.587 * (g * g)
            + 0.114 * (b * b)
        );

        return rgb < THRESHOLD;
    }
}
