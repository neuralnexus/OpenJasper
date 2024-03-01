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
import React from 'react';
import ReactDOM from 'react-dom';
import { ColorResult } from 'react-color';
import { AttachableColorPicker, ColorPickerProps } from './AttachableColorPicker';

interface Options {
    padding: {
        top: number,
        left: number
    },
    color: string,
    disableAlpha?: boolean,
    showTransparentPreset?: boolean,
    onChangeComplete(color: ColorResult): void,
    onHide?(): void,
    ColorPicker?: React.ComponentType<ColorPickerProps>
}

const defaultOptions = {
    padding: { top: 0, left: 0 },
    disableAlpha: true,
    showTransparentPreset: true,
    color: '',
    onChangeComplete() : void {},
    onHide(): void {},
    ColorPicker: AttachableColorPicker
};

export default class AttachableColorPickerWrapper {
    private readonly attachTo: HTMLElement;

    private readonly options: Options;

    private color: string;

    private readonly colorPickerContainerWrapper: HTMLDivElement;

    private readonly onChangeCompleteWrapper: (color: ColorResult) => void;

    private readonly onHideWrapper: () => void;

    private ColorPicker: React.ComponentType<ColorPickerProps>;

    private readonly boundOnAttachElementClick: () => void;

    constructor(attachTo: HTMLElement, options: Options = defaultOptions) {
        this.options = options;
        this.attachTo = attachTo;

        this.color = this.options.color;

        this.ColorPicker = this.options.ColorPicker || AttachableColorPicker;

        this.colorPickerContainerWrapper = document.createElement('div');
        this.colorPickerContainerWrapper.className = 'jr-jColorPickerWrapper';

        this.boundOnAttachElementClick = this.onAttachElementClick.bind(this);

        const { onChangeComplete, onHide } = this.options;

        this.onChangeCompleteWrapper = (color: ColorResult) => {
            this.color = color.hex;
            onChangeComplete(color);
        };

        this.onHideWrapper = () => {
            const state = this.getColorPickerState(false);

            this.renderColorPicker(state);

            if (onHide) {
                onHide();
            }
        };

        const state = this.getColorPickerState(false);

        this.renderColorPicker(state);
    }

    private renderColorPicker(state: ColorPickerProps) {
        this.remove();

        this.attachTo.addEventListener('click', this.boundOnAttachElementClick);

        document.body.appendChild(this.colorPickerContainerWrapper);

        const { ColorPicker } = this;

        ReactDOM.render(
            <ColorPicker {...state} />,
            this.colorPickerContainerWrapper,
        );
    }

    private onAttachElementClick() {
        const state = this.getColorPickerState(true);

        this.renderColorPicker(state);
    }

    private getColorPickerState(show: boolean): ColorPickerProps {
        return {
            padding: this.options.padding,
            show,
            color: this.color,
            disableAlpha: this.options.disableAlpha,
            showTransparentPreset: this.options.showTransparentPreset,
            onChangeComplete: this.onChangeCompleteWrapper,
            onHide: this.onHideWrapper,
            attachTo: this.attachTo,
        }
    }

    setColor(color: string) {
        this.color = color;
    }

    remove() {
        this.attachTo.removeEventListener('click', this.boundOnAttachElementClick);
        ReactDOM.unmountComponentAtNode(this.colorPickerContainerWrapper);
        $(this.colorPickerContainerWrapper).remove();
    }
}
