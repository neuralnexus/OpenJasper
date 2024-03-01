/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import { ColorResult } from 'react-color';
import AttachableColorPickerWrapper from 'js-sdk/src/common/component/colorPicker/react/AttachableColorPickerWrapper';
import Colors from 'js-sdk/src/common/component/colorPicker/react/enum/colors';

const getColor = (value: string|null): string => {
    if (value) {
        return value === Colors.TRANSPARENT ? value : `#${value}`;
    }

    return Colors.TRANSPARENT;
};

const convertColorForModel = (color: ColorResult): string => {
    const { hex } = color;

    if (hex === Colors.TRANSPARENT) {
        return Colors.TRANSPARENT;
    }

    return hex.slice(1);
};

export default function getColorPickerBinding() {
    return {
        attachableColorPickerWrapper: {
            remove: () => {},
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            setColor: (color: string) => {}
        } as AttachableColorPickerWrapper,

        init($element: JQuery, value: string, bindings: {
            [propName: string]: (value: string) => void
        }) {
            const showTransparentInput = !!$element.data('showTransparentInput');
            const modelBinding = $element.data('model-attr');

            this.attachableColorPickerWrapper = new AttachableColorPickerWrapper($element[0], {
                padding: {
                    top: 0,
                    left: 0,
                },
                color: getColor(value),
                showTransparentPreset: showTransparentInput,
                onChangeComplete: (color: ColorResult) => {
                    bindings[modelBinding](convertColorForModel(color));
                },
            });
        },

        set($element: JQuery, value: string|null) {
            const color = getColor(value);

            if (value) {
                $element.removeClass('unchanged');
            } else {
                $element.addClass('unchanged');
            }

            $element.find('div.colorpick').css('background-color', color);

            this.attachableColorPickerWrapper.setColor(color);
        },

        clean() {
            this.attachableColorPickerWrapper.remove();
        },
    }
}
