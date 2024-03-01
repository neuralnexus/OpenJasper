import sinon from 'sinon';
import attachableComponentPositionUtil from 'src/common/component/base/util/attachableComponentPositionUtil';

describe('attachableComponentPositionUtil Tests.', () => {
    let sandbox: any,
        jQuery: any,
        attachTo: HTMLElement,
        element: HTMLElement;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        element = document.createElement('div');
        attachTo = document.createElement('div');

        jQuery = sandbox.stub();
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should return position for bottom right placement', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(1000),
            width: sandbox.stub().returns(1000)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 300,
                left: 300
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 345,
            left: 300
        });
    });

    it('should return position for bottom left placement', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(1000),
            width: sandbox.stub().returns(1000)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 300,
                left: 950
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 345,
            left: 890
        });
    });

    it('should return position for top right placement', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(1000),
            width: sandbox.stub().returns(1000)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 950,
                left: 300
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 845,
            left: 300
        });
    });

    it('should return position for top left placement', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(1000),
            width: sandbox.stub().returns(1000)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 950,
                left: 950
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 845,
            left: 890
        });
    });

    it('should return position for vertically centered right placement', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(150),
            width: sandbox.stub().returns(1000)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 75,
                left: 200
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 20,
            left: 240
        });
    });

    it('should return position for vertically centered center left placement', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(150),
            width: sandbox.stub().returns(1000)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 75,
                left: 950
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 20,
            left: 850
        });
    });

    it('should return position for horizontally centered placement', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(1000),
            width: sandbox.stub().returns(120)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 300,
                left: 55
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 345,
            left: 25
        });
    });

    it('should return position for left placement when attachTo is out of left boundary', () => {
        jQuery.withArgs('body').returns({
            height: sandbox.stub().returns(1000),
            width: sandbox.stub().returns(150)
        });

        jQuery.withArgs(attachTo).returns({
            height: sandbox.stub().returns(40),
            width: sandbox.stub().returns(40),
            offset: sandbox.stub().returns({
                top: 200,
                left: -10
            }),
            0: {}
        });

        jQuery.withArgs(element).returns({
            innerHeight: sandbox.stub().returns(100),
            innerWidth: sandbox.stub().returns(100)
        });

        const result = attachableComponentPositionUtil.getPosition(
            attachTo,
            {
                top: 5,
                left: 5
            },
            element,
            jQuery
        );

        expect(result).toEqual({
            top: 245,
            left: 0
        });
    });
});
