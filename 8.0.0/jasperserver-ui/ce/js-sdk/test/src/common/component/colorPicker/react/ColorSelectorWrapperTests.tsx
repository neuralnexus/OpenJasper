import sinon from 'sinon';
import ColorSelectorWrapper from 'src/common/component/colorPicker/react/ColorSelectorWrapper';

// @ts-ignore
import colorSelectorWrapperSnapshot from './snapshots/colorSelectorWrapperSnapshot.htm';
// @ts-ignore
import initialColorSelectorWrapperSnapshot from './snapshots/initialColorSelectorWrapperSnapshot.htm';

describe('ColorSampleWrapper Tests', () => {
    let sandbox: any,
        onColorChange: any,
        element: HTMLElement,
        colorSelectorWrapper: ColorSelectorWrapper;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        onColorChange = sandbox.stub();

        element = document.createElement('div');

        colorSelectorWrapper = new ColorSelectorWrapper(element, {
            color: '#ffffff',
            label: '#ffffff',
            showTransparentPreset: false,
            onColorChange
        });
    });

    afterEach(() => {
        sandbox.restore();

        colorSelectorWrapper.remove();
    });

    it('should render color selector on init', () => {
        expect(element.outerHTML).toEqual(initialColorSelectorWrapperSnapshot);
    });

    it('should render color selector', () => {
        const state = {
            color: '#000000',
            label: '#000000',
            showTransparentPreset: false,
            onColorChange
        };
        colorSelectorWrapper.setState(state);

        expect(element.outerHTML).toEqual(colorSelectorWrapperSnapshot);
    });
});
