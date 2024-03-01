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
import ReactDOM from 'react-dom';
import { ColorSelector, ColorSelectorProps } from './ColorSelector';

interface Options {
    color: string,
    label: string,
    showTransparentPreset: boolean,
    onColorChange(): void,
    ColorSelector?: React.ComponentType<ColorSelectorProps>
}

const defaultOptions = {
    color: '',
    label: '',
    showTransparentPreset: true,
    onColorChange(): void {},
    ColorSelector
};

export default class ColorSelectorWrapper {
    private readonly element: HTMLElement;

    private readonly onColorChange: () => void;

    constructor(element: HTMLElement, options: Options = defaultOptions) {
        this.element = element;

        this.onColorChange = options.onColorChange;

        this.renderColorSelector({
            color: options.color,
            label: options.label,
            showTransparentPreset: options.showTransparentPreset,
            onColorChange: this.onColorChange
        });
    }

    private renderColorSelector(state: Options) {
        ReactDOM.render(
            <ColorSelector {...state} />,
            this.element
        );
    }

    setState(state: { color: string, label: string, showTransparentPreset: boolean}) {
        this.renderColorSelector({
            ...state, onColorChange: this.onColorChange
        });
    }

    remove() {
        ReactDOM.unmountComponentAtNode(this.element);
    }
}
