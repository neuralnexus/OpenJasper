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
import { ColorResult } from 'react-color';
import { ColorSample as ColorSampleComponent, ColorSampleProps } from './ColorSample';
import { AttachableColorPicker as AttachableColorPickerComponent, ColorPickerProps } from './AttachableColorPicker';

export interface ColorSelectorProps {
    color: string,
    label: string,
    showTransparentPreset?: boolean,
    onColorChange: (color: ColorResult) => void
}

interface ColorSelectorState {
    show: boolean
}

const createColorSampleWithColorPicker = (
    ColorSample: React.ComponentType<ColorSampleProps>,
    AttachableColorPicker: React.ComponentType<ColorPickerProps>
): React.ComponentType<ColorSelectorProps> => {
    return class ColorSelector extends React.Component<ColorSelectorProps, ColorSelectorState> {

        constructor(props: ColorSelectorProps) {
            super(props);

            this.state = {
                show: false
            };

            this.colorSampleRef = React.createRef<HTMLDivElement>();
            this.colorPickerContainerWrapper = null;
        }

        // eslint-disable-next-line camelcase
        UNSAFE_componentWillMount(): void {
            this.colorPickerContainerWrapper = document.createElement('div');
            this.colorPickerContainerWrapper.className = 'jr-jColorPickerWrapper';

            document.body.appendChild(this.colorPickerContainerWrapper);
        }

        componentWillUnmount(): void {
            if (this.colorPickerContainerWrapper) {
                this.colorPickerContainerWrapper.remove();
            }
        }

        private onClick() {
            const isSetState = this.state.show;
            this.setState({
                show: !isSetState
            });
        }

        private onColorPickerHide() {
            this.setState({
                show: false
            });
        }

        private readonly colorSampleRef: React.RefObject<HTMLDivElement>;

        private colorPickerContainerWrapper: HTMLDivElement|null;

        render() {
            const { show } = this.state;
            const { color, label } = this.props;
            const colorSampleEl = this.colorSampleRef.current;

            const showTransparentPreset = typeof this.props.showTransparentPreset === 'undefined'
                ? true
                : this.props.showTransparentPreset;

            let colorPicker: React.ReactElement;

            if (colorSampleEl) {
                colorPicker = (
                    <AttachableColorPicker
                        padding={{ top: 0, left: 0 }}
                        show={show}
                        color={color}
                        showTransparentPreset={showTransparentPreset}
                        onChangeComplete={this.props.onColorChange}
                        onHide={() => { this.onColorPickerHide() }}
                        attachTo={colorSampleEl}
                    />
                )
            } else {
                colorPicker = <div />;
            }

            return (
                <>
                    <div className="jr-jColorSample" ref={this.colorSampleRef}>
                        <ColorSample
                            color={color}
                            label={label}
                            onClick={() => { this.onClick() }}
                        />
                    </div>
                    {
                        ReactDOM.createPortal(colorPicker,
                            this.colorPickerContainerWrapper as HTMLDivElement)
                    }
                </>
            )
        }
    }
};

const ColorSelector = createColorSampleWithColorPicker(
    ColorSampleComponent,
    AttachableColorPickerComponent
);

export { createColorSampleWithColorPicker, ColorSelector };
