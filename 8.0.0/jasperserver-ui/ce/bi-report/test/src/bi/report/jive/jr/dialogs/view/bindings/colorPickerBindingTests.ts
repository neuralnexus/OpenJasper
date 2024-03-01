import sinon, { SinonStub, SinonSandbox } from 'sinon';
import getColorPickerBinding from 'src/bi/report/jive/jr/dialogs/view/bindigs/colorPickerBinding';
import {
    // @ts-ignore
    rewire as attachableColorPickerWrapperRewire,
    // @ts-ignore
    restore as attachableColorPickerWrapperRestore,
} from 'js-sdk/src/common/component/colorPicker/react/AttachableColorPickerWrapper';

import Colors from 'js-sdk/src/common/component/colorPicker/react/enum/colors';

describe('colorPickerBinding Tests.', () => {
    let binding: {
        init: (element: JQuery, value: string, binding: {
            [propName: string]: (value: string) => void;
        }) => void,
        set: (element: JQuery, value: string|null) => void,
        clean: () => void
    };
    let sandbox: SinonSandbox;
    let AttachableColorPickerWrapperConstr: SinonStub;
    let attachableColorPickerWrapperInstance: {
        setColor: SinonStub,
        remove: SinonStub,
    };

    let el: unknown;
    let $el: JQuery;
    let dataStub: SinonStub;
    let attrCallback: SinonStub;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        binding = getColorPickerBinding();

        attachableColorPickerWrapperInstance = {
            setColor: sandbox.stub(),
            remove: sandbox.stub(),
        };

        AttachableColorPickerWrapperConstr = sandbox.stub()
            .returns(attachableColorPickerWrapperInstance);

        attachableColorPickerWrapperRewire(AttachableColorPickerWrapperConstr);

        el = {
            addEventListener: sandbox.stub(),
            removeEventListener: sandbox.stub(),
        } as unknown as HTMLElement;

        $el = [
            el,
        ] as unknown as JQuery;

        dataStub = sandbox.stub();
        $el.data = dataStub;

        dataStub.withArgs('model-attr').returns('attr');
        dataStub.withArgs('showTransparentInput').returns(true);

        attrCallback = sandbox.stub();
    });

    afterEach(() => {
        sandbox.restore();
        attachableColorPickerWrapperRestore();
    });

    describe('init', () => {

        it('should init binding with opaque color', () => {
            binding.init($el, 'value', {
                attr: attrCallback,
            });

            const colorPickerOptions = AttachableColorPickerWrapperConstr.args[0][1];

            expect(AttachableColorPickerWrapperConstr.args[0][0]).toEqual(el);

            expect(colorPickerOptions.padding).toEqual({
                top: 0,
                left: 0,
            });

            expect(colorPickerOptions.color).toEqual('#value');
            expect(colorPickerOptions.showTransparentPreset).toBe(true);

            const callback = colorPickerOptions.onChangeComplete;

            callback({
                hex: '#value'
            });

            expect(attrCallback).toHaveBeenCalledWith('value');
        });

        it('should init binding with transparent color', () => {
            binding.init($el, Colors.TRANSPARENT, {
                attr: attrCallback,
            });

            const colorPickerOptions = AttachableColorPickerWrapperConstr.args[0][1];

            expect(AttachableColorPickerWrapperConstr.args[0][0]).toEqual(el);

            expect(colorPickerOptions.padding).toEqual({
                top: 0,
                left: 0,
            });

            expect(colorPickerOptions.color).toEqual(Colors.TRANSPARENT);
            expect(colorPickerOptions.showTransparentPreset).toBe(true);

            const callback = colorPickerOptions.onChangeComplete;

            callback({
                hex: Colors.TRANSPARENT
            });

            expect(attrCallback).toHaveBeenCalledWith(Colors.TRANSPARENT);
        });
    });

    describe('set', () => {

        it('should set opaque color', () => {
            binding.init($el, 'value', {
                attr: attrCallback,
            });

            const cssStub = {
                css: sandbox.stub()
            };

            const $element = {
                removeClass: sandbox.stub(),
                addClass: sandbox.stub(),
                find: sandbox.stub().withArgs('div.colorpick').returns(cssStub)
            } as unknown as JQuery;

            binding.set($element, 'value');

            expect($element.removeClass).toHaveBeenCalledWith('unchanged');
            expect(cssStub.css).toHaveBeenCalledWith('background-color', '#value');

            expect(attachableColorPickerWrapperInstance.setColor).toHaveBeenCalledWith('#value');
        });

        it('should set transparent color', () => {
            binding.init($el, 'value', {
                attr: attrCallback,
            });

            const cssStub = {
                css: sandbox.stub()
            };

            const $element = {
                removeClass: sandbox.stub(),
                addClass: sandbox.stub(),
                find: sandbox.stub().withArgs('div.colorpick').returns(cssStub)
            } as unknown as JQuery;

            binding.set($element, Colors.TRANSPARENT);

            expect($element.removeClass).toHaveBeenCalledWith('unchanged');
            expect(cssStub.css).toHaveBeenCalledWith('background-color', Colors.TRANSPARENT);

            expect(attachableColorPickerWrapperInstance.setColor).toHaveBeenCalledWith(Colors.TRANSPARENT);
        });

        it('should set no color', () => {
            binding.init($el, 'value', {
                attr: attrCallback,
            });

            const cssStub = {
                css: sandbox.stub()
            };

            const $element = {
                removeClass: sandbox.stub(),
                addClass: sandbox.stub(),
                find: sandbox.stub().withArgs('div.colorpick').returns(cssStub)
            } as unknown as JQuery;

            binding.set($element, null);

            expect($element.addClass).toHaveBeenCalledWith('unchanged');
            expect(cssStub.css).toHaveBeenCalledWith('background-color', Colors.TRANSPARENT);

            expect(attachableColorPickerWrapperInstance.setColor).toHaveBeenCalledWith(Colors.TRANSPARENT);
        });
    });

    it('should clean binding', () => {
        binding.init($el, 'value', {
            attr: attrCallback,
        });

        binding.clean();

        expect(attachableColorPickerWrapperInstance.remove).toHaveBeenCalled();
    });
});
