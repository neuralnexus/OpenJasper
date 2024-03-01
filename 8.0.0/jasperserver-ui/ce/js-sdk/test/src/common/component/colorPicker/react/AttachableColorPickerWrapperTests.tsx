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

import sinon from 'sinon';
import AttachableColorPickerWrapper from 'src/common/component/colorPicker/react/AttachableColorPickerWrapper';
import { ColorResult } from 'react-color';
import { createFakeComponent } from '../../../../../mock/fakeComponent';

// @ts-ignore
import invisibleColorPickerSnapshot from './snapshots/invisibleAttachableColorPickerWrapperRenderSnapshot.htm';
// @ts-ignore
import visibleColorPickerSnapshot from './snapshots/visibleAttachableColorPickerWrapperRenderSnapshot.htm';
// @ts-ignore
import newColorSnapshot from './snapshots/newColorAttachableColorPickerWrapperRenderSnapshot.htm';
// @ts-ignore
import hexColorSnapshot from './snapshots/hexColorAttachableColorPickerWrapperRenderSnapshot.htm';
import customMatcher from '../../../../../tools/customMatcher';
import { ColorPickerProps } from '../../../../../../src/common/component/colorPicker/react/AttachableColorPicker';

describe('AttachableColorPickerWrapper Tests', () => {
    let sandbox: any,
        attachTo: any,
        onChangeComplete: any,
        attachableColorPickerWrapper: AttachableColorPickerWrapper;

    const addEventListenerInfo = {
        event: '',
        callback() : void {}
    };

    const removeEventListenerInfo = {
        event: '',
        callback() : void {}
    };

    beforeEach(() => {
        sandbox = sinon.createSandbox();
        jasmine.addMatchers(customMatcher);

        onChangeComplete = sandbox.stub();

        attachTo = {
            addEventListener(event: string, callback: () => void) {
                addEventListenerInfo.event = event;
                addEventListenerInfo.callback = callback;
            },

            removeEventListener(event: string, callback: () => void) {
                removeEventListenerInfo.event = event;
                removeEventListenerInfo.callback = callback;
            }
        };

        attachableColorPickerWrapper = new AttachableColorPickerWrapper(attachTo, {
            padding: {
                top: 5,
                left: 5
            },
            color: 'color',
            disableAlpha: false,
            showTransparentPreset: true,
            onChangeComplete,
            ColorPicker: createFakeComponent('ColorPicker')
        });
    });

    afterEach(() => {
        sandbox.restore();

        attachableColorPickerWrapper.remove();
    });

    it('should render hidden component to dom on init', () => {
        const colorPickerWrapper: any = document.body.querySelector('.jr-jColorPickerWrapper');

        expect(colorPickerWrapper.outerHTML).toEqualSnapshot(invisibleColorPickerSnapshot);
    });

    it('should render visible component to dom on attachTo click', () => {
        const colorPickerWrapper: any = document.body.querySelector('.jr-jColorPickerWrapper');

        addEventListenerInfo.callback();

        expect(colorPickerWrapper.outerHTML).toEqualSnapshot(visibleColorPickerSnapshot);
    });

    it('should set new color', () => {
        attachableColorPickerWrapper.setColor('newColor');

        const colorPickerWrapper: any = document.body.querySelector('.jr-jColorPickerWrapper');

        addEventListenerInfo.callback();

        expect(colorPickerWrapper.outerHTML).toEqualSnapshot(newColorSnapshot);
    });

    it('should save color in wrapper on color change', () => {
        attachableColorPickerWrapper.remove();

        attachableColorPickerWrapper = new AttachableColorPickerWrapper(attachTo, {
            padding: {
                top: 5,
                left: 5
            },
            color: 'color',
            disableAlpha: false,
            showTransparentPreset: false,
            onChangeComplete,
            onHide() {},
            ColorPicker: createFakeComponent<ColorPickerProps>('ColorPicker', {
                componentDidMount: (props) => {
                    props.onChangeComplete({
                        hex: 'hexColor'
                    } as ColorResult);
                }
            })
        });

        const colorPickerWrapper: any = document.body.querySelector('.jr-jColorPickerWrapper');

        addEventListenerInfo.callback();

        expect(onChangeComplete).toHaveBeenCalledWith({
            hex: 'hexColor'
        });

        expect(colorPickerWrapper.outerHTML).toEqualSnapshot(hexColorSnapshot);
    });

    it('should call onHide is color picker should be hidden', (done) => {
        attachableColorPickerWrapper.remove();

        const onHide = sandbox.stub();

        attachableColorPickerWrapper = new AttachableColorPickerWrapper(attachTo, {
            padding: {
                top: 5,
                left: 5
            },
            color: 'color',
            disableAlpha: false,
            showTransparentPreset: true,
            onChangeComplete,
            onHide,
            ColorPicker: createFakeComponent<ColorPickerProps>('ColorPicker', {
                componentDidMount: (props) => {
                    props.onHide();

                    const colorPickerWrapper: any = document.body.querySelector('.jr-jColorPickerWrapper');

                    addEventListenerInfo.callback();

                    expect(onHide).toHaveBeenCalled();

                    expect(colorPickerWrapper.outerHTML).toEqualSnapshot(invisibleColorPickerSnapshot);
                    done()
                }
            })
        });
    });

    it('should remove component', () => {
        sandbox.spy(attachTo, 'removeEventListener');

        attachableColorPickerWrapper.remove();

        const colorPickerWrapper: any = document.body.querySelector('.jr-jColorPickerWrapper');

        expect(colorPickerWrapper).toBeFalsy();

        expect(attachTo.removeEventListener).toHaveBeenCalledWith('click', removeEventListenerInfo.callback);
    });
});
