import sinon from 'sinon';
import React from 'react';
import { ColorSample } from 'src/common/component/colorPicker/react/ColorSample';
import { mount } from 'enzyme';

// @ts-ignore
import visibleColorSampleSnapshot from './snapshots/visibleColorSampleSnapshot.htm';
// @ts-ignore
import transparentColorSampleSnapshot from './snapshots/transparentColorSampleSnapshot.htm';

describe('ColorSample Tests', () => {
    let sandbox: any;

    beforeEach(() => {
        sandbox = sinon.createSandbox();
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should render color sample component', () => {
        const color = '#ffffff';
        const label = '#ffffff';
        const onClick = function onClick() {};

        const component = mount(
            <ColorSample
                color={color}
                label={label}
                onClick={onClick}
            />
        );

        expect(component.html()).toEqual(visibleColorSampleSnapshot);

        component.unmount();
    });

    it('should render color sample component with color set to transparent', () => {
        const color = 'transparent';
        const label = '#ffffff';
        let flag = false;
        const onClick = function onClick() { flag = true; };

        const component = mount(
            <ColorSample
                color={color}
                label={label}
                onClick={onClick}
            />
        );

        expect(component.html()).toEqual(transparentColorSampleSnapshot);

        component.props().onClick();

        expect(flag).toBeTruthy();

        component.unmount();
    });
});
