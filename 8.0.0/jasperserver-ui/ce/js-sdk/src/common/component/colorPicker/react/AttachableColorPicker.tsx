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
import { ColorResult, SketchPicker, SketchPickerProps } from 'react-color';
import positionUtil from '../../base/util/attachableComponentPositionUtil';

import Colors from './enum/colors';

const getPresetColors = (showTransparentPreset: boolean): string[] => {
    let presetColors = [
        '#D0021B',
        '#F5A623',
        '#F8E71C',
        '#8B572A',
        '#7ED321',
        '#417505',
        '#BD10E0',
        '#9013FE',
        '#4A90E2',
        '#50E3C2',
        '#B8E986',
        '#000000',
        '#4A4A4A',
        '#9B9B9B',
        '#FFFFFF'
    ];

    if (showTransparentPreset) {
        presetColors = presetColors.concat([Colors.TRANSPARENT]);
    }

    return presetColors;
};

export interface ColorPickerProps {
    padding: { top: number, left: number },
    show: boolean,
    color: string,
    disableAlpha?: boolean,
    showTransparentPreset?: boolean,
    onChangeComplete(color: ColorResult): void,
    onHide(): void,
    attachTo: HTMLElement
}

interface ColorPickerPropsExtended extends ColorPickerProps {
    ColorPicker: React.ComponentType<SketchPickerProps>,
    doc: Document
}

interface ColorPickerState {
    disableAlpha: boolean,
    showTransparentPreset: boolean
}

class ColorPickerWithAbilityToAttach extends
    React.Component<ColorPickerPropsExtended, ColorPickerState> {
    constructor(props: ColorPickerPropsExtended) {
        super(props);

        const {
            disableAlpha, showTransparentPreset
        } = props;

        this.state = {
            disableAlpha: typeof disableAlpha === 'undefined' ? true : disableAlpha,
            showTransparentPreset: typeof showTransparentPreset === 'undefined'
                ? true
                : showTransparentPreset,
        };

        this.divRef = React.createRef<HTMLDivElement>();

        this.boundOnDocumentMousedown = this.onDocumentMousedown.bind(this);
    }

    componentDidMount(): void {
        this.forceUpdate();
    }

    componentDidUpdate() {
        this.props.doc.removeEventListener('mousedown', this.boundOnDocumentMousedown);

        if (this.props.show && this.divRef.current) {
            this.props.doc.addEventListener('mousedown', this.boundOnDocumentMousedown);
        }
    }

    componentWillUnmount(): void {
        this.props.doc.removeEventListener('mousedown', this.boundOnDocumentMousedown);
    }

    onDocumentMousedown(e: Event) {
        const colorPickerEl = this.divRef.current;
        const mouseDownTarget = e.target as HTMLElement;
        const { attachTo } = this.props;

        if (colorPickerEl) {
            if (!colorPickerEl.contains(mouseDownTarget)
                && !colorPickerEl.isEqualNode(mouseDownTarget)
                && !attachTo.contains(mouseDownTarget)
                && !attachTo.isEqualNode(mouseDownTarget)
            ) {
                this.props.onHide();
            }
        }
    }

    private readonly divRef: React.RefObject<HTMLDivElement>;

    private readonly boundOnDocumentMousedown: (e: Event) => void;

    render() {
        const {
            show, color, attachTo, padding, onChangeComplete, ColorPicker
        } = this.props;

        const {
            disableAlpha, showTransparentPreset
        } = this.state;

        let position = {
            top: 0,
            left: 0
        };

        if (this.divRef.current) {
            position = positionUtil
                .getPosition(attachTo, padding, this.divRef.current);
        }

        const style: React.CSSProperties = {
            position: 'absolute',
            zIndex: 9000,
            top: `${position.top}px`,
            left: `${position.left}px`,
            visibility: show && this.divRef.current ? 'visible' : 'hidden',
        };

        return (
            <div style={style} ref={this.divRef}>
                <ColorPicker
                    color={color}
                    disableAlpha={disableAlpha}
                    onChangeComplete={onChangeComplete}
                    presetColors={getPresetColors(showTransparentPreset)}
                />
            </div>
        );
    }
}

const withAbilityToAttach = (ColorPicker: React.ComponentType<SketchPickerProps>, doc: Document): React.ComponentType<ColorPickerProps> => {
    return (props: ColorPickerProps) => {
        const extendedProps = {
            ...props,
            ColorPicker,
            doc
        };

        return (
            <ColorPickerWithAbilityToAttach {...extendedProps} />
        );
    }
};

const AttachableColorPicker = withAbilityToAttach(SketchPicker, document);

export { withAbilityToAttach, AttachableColorPicker };
