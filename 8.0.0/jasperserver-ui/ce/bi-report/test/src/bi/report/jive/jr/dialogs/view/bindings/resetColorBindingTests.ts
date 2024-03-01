import sinon, { SinonStub, SinonSandbox } from 'sinon';
import getResetColorBindig from 'src/bi/report/jive/jr/dialogs/view/bindigs/resetColorBinding';

describe('resetColorBinding Tests', () => {
    let binding: {
        init: (element: JQuery, value: string, binding: {
            [propName: string]: (value: null) => void;
        }) => void,
        set: (element: JQuery, value: string) => void,
        clean: () => void
    };
    let sandbox: SinonSandbox;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        binding = getResetColorBindig();
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should init binding', () => {
        const el = {
            addEventListener: sandbox.stub(),
            removeEventListener: sandbox.stub(),
        } as unknown as HTMLElement;

        const $el = {
            get(index: number): HTMLElement|null {
                if (index === 0) {
                    return el;
                }

                return null;
            }
        } as unknown as JQuery;

        const attrStub: SinonStub = sandbox.stub();
        $el.attr = attrStub;

        const dataStub: SinonStub = sandbox.stub();
        $el.data = dataStub;

        dataStub.withArgs('model-attr').returns('attr');

        const attrCallback = sandbox.stub();

        binding.init($el, 'value', {
            attr: attrCallback,
        });

        expect(attrStub).toHaveBeenCalledWith('disabled', 'disabled');
        // @ts-ignore
        expect(el.addEventListener).toHaveBeenCalledWith('click');

        const callback = (el.addEventListener as SinonStub).args[0][1];

        const event = {
            stopPropagation: sandbox.stub(),
        };

        callback(event);

        expect(event.stopPropagation).toHaveBeenCalled();
        expect(attrCallback).toHaveBeenCalledWith(null);
        expect(attrCallback).toHaveBeenCalledWith(null);

        expect(attrStub.callCount).toEqual(2);
    });

    it('should set value', () => {
        const $el = {
            removeAttr: sandbox.stub(),
            prop: sandbox.stub(),
        } as unknown as JQuery;

        binding.set($el, 'value');

        expect($el.removeAttr).toHaveBeenCalledWith('disabled');
        // @ts-ignore
        expect($el.prop).toHaveBeenCalledWith('checked', true);
    });

    it('should clean event listeners', () => {
        const addEventListener: SinonStub = sandbox.stub();
        const removeEventListener: SinonStub = sandbox.stub();

        const el = {
            addEventListener,
            removeEventListener,
        } as unknown as HTMLElement;

        const $el = {
            get(index: number): HTMLElement|null {
                if (index === 0) {
                    return el;
                }

                return null;
            }
        } as unknown as JQuery;

        $el.attr = sandbox.stub() as SinonStub;
        $el.data = sandbox.stub() as SinonStub;

        binding.init($el, 'value', {
            attr: sandbox.stub(),
        });

        binding.clean();

        expect(removeEventListener).toHaveBeenCalledWith('click');
        expect(addEventListener.args[0][1]).toEqual(removeEventListener.args[0][1]);
    });
});
