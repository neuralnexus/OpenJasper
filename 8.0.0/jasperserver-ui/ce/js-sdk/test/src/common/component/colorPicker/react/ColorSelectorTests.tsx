import sinon from 'sinon';
import React from 'react';
import { createColorSampleWithColorPicker } from 'src/common/component/colorPicker/react/ColorSelector';
import { mount } from 'enzyme';
import { createFakeComponent } from '../../../../../mock/fakeComponent';
import customMatcher from '../../../../../tools/customMatcher';

// @ts-ignore
import colorSelectorWithInvisibleColorPickerSnapshot from './snapshots/colorSelector/colorSelectorWithInvisibleColorPickerSnapshot.htm';
// @ts-ignore
import colorSelectorWithVisibleColorPickerSnapshot from './snapshots/colorSelector/colorSelectorWithVisibleColorPickerSnapshot.htm';
import { ColorSampleProps } from '../../../../../../src/common/component/colorPicker/react/ColorSample';

describe('ColorSelector Tests.', () => {
    let sandbox: any;

    beforeEach(() => {
        jasmine.addMatchers(customMatcher);
        sandbox = sinon.createSandbox();
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should render color selector without color picker', () => {
        const ColorSelectorComponent = createColorSampleWithColorPicker(
            createFakeComponent('ColorSample'),
            createFakeComponent('ColorPicker')
        );

        const label = 'label';
        const color = 'color';
        const onColorChange = function onColorChange() {};

        const component = mount(
            <ColorSelectorComponent
                color={color}
                label={label}
                showTransparentPreset={false}
                onColorChange={onColorChange}
            />
        );

        expect(component.html()).toEqualSnapshot(colorSelectorWithInvisibleColorPickerSnapshot);

        expect((document.querySelector('.jr-jColorPickerWrapper') as HTMLElement).innerHTML)
            .toEqual('<div></div>');

        component.unmount();
    });

    it('should render color picker component in color selector via portal', () => {
        let onClick: any;
        const ColorSelectorComponent = createColorSampleWithColorPicker(
            createFakeComponent<ColorSampleProps>('ColorSample', {
                propsConsumer: (props: ColorSampleProps) => {
                    // eslint-disable-next-line prefer-destructuring
                    onClick = props.onClick
                }
            }),
            createFakeComponent('ColorPicker')
        );

        const label = 'label';
        const color = 'color';
        const onColorChange = function onColorChange() {};

        const component = mount(
            <ColorSelectorComponent
                color={color}
                label={label}
                showTransparentPreset={false}
                onColorChange={onColorChange}
            />
        );

        if (onClick) {
            onClick({} as React.MouseEvent);
        }

        expect((document.querySelector('.jr-jColorPickerWrapper') as HTMLElement).innerHTML)
            .toEqualSnapshot(colorSelectorWithVisibleColorPickerSnapshot);

        component.unmount();
    });

    it('should check if show property was changed on click', () => {
        let onClick: any;
        const ColorSelectorComponent = createColorSampleWithColorPicker(
            createFakeComponent<ColorSampleProps>('ColorSample', {
                propsConsumer: (props: ColorSampleProps) => {
                    // eslint-disable-next-line prefer-destructuring
                    onClick = props.onClick
                }
            }),
            createFakeComponent('ColorPicker')
        );

        const label = 'label';
        const color = 'color';
        const onColorChange = function onColorChange() {};

        const component = mount(
            <ColorSelectorComponent
                color={color}
                label={label}
                showTransparentPreset={false}
                onColorChange={onColorChange}
            />
        );

        expect(component.state()).toEqual({ show: false });

        if (onClick) {
            onClick({} as React.MouseEvent);
        }

        expect(component.state()).toEqual({ show: true });

        component.unmount();
    });
});
