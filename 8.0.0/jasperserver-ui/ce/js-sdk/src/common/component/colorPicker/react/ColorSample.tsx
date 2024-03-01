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

import React from 'react';
import colorConvertUtil from '../util/colorConvertUtil';
import Colors from './enum/colors';

const SWATCH_LIGHT_CLASS = 'jr-mControl-launcher-swatchLight';
const TRANSPARENT_CLASS = `${SWATCH_LIGHT_CLASS} jr-mControl-launcher-swatchTransparent`;

export interface ColorSampleProps {
    color: string,
    label: string,
    onClick(): void
}

const getSwatchLightClass = (color : string): string => {
    if (color === Colors.TRANSPARENT) {
        return TRANSPARENT_CLASS;
    }
    if (!colorConvertUtil.isColorDark(color)) {
        return SWATCH_LIGHT_CLASS;
    }
    return '';
};

const ColorSample = (props: ColorSampleProps) => {
    const style: React.CSSProperties = { backgroundColor: props.color };
    const className = `jr-mControl-launcher-swatch ${getSwatchLightClass(props.color)} jr`;

    return (
        // eslint-disable-next-line jsx-a11y/no-static-element-interactions
        <div className="jr-mControl-launcher jr" onClick={props.onClick}>
            <div className={className} style={style} />
            <div className="jr-mControl-launcher-hex jr">{ props.label }</div>
        </div>
    );
};

export { ColorSample };
