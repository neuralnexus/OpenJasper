import sinon from 'sinon';
import colorConvertUtil from 'src/common/component/colorPicker/util/colorConvertUtil';

describe('colorConvertUtil Tests.', () => {
    let sandbox: any,
        color: string;

    beforeEach(() => {
        sandbox = sinon.createSandbox();
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should return converted color for rgba', () => {
        const result = '#101520';
        color = 'rgb(16, 21, 32, 0)';

        expect(colorConvertUtil.rgba2NoAlphaHex(color)).toEqual(result);
    });

    it('should return converted color for rgb', () => {
        let result = '#C4C5D6';
        color = 'rgb(196, 197, 214)';

        expect(colorConvertUtil.rgba2NoAlphaHex(color)).toEqual(result);

        color = 'rgb(36,195,0)';
        result = '#24C300';

        expect(colorConvertUtil.rgba2NoAlphaHex(color)).toEqual(result);
    });

    it('should check if current rgb color is transparent', () => {
        color = 'rgba(0, 0, 0, 0)';

        expect(colorConvertUtil.isRgbTransparent(color)).toBeTruthy();

        color = 'rgba(0,0,0,0)';

        expect(colorConvertUtil.isRgbTransparent(color)).toBeTruthy();

        color = 'rgb(0, 0, 0)';

        expect(colorConvertUtil.isRgbTransparent(color)).toBeFalsy();
    });

    it('should check if current color is rgba', () => {
        color = 'rgba(0, 0, 0, 0)';

        expect(colorConvertUtil.isRgba(color)).toBeTruthy();

        color = 'rgb(25, 36, 47)';

        expect(colorConvertUtil.isRgbTransparent(color)).toBeFalsy();

        color = 'rgba(25, 36, 47)';

        expect(colorConvertUtil.isRgbTransparent(color)).toBeFalsy();
    });

    it('should determine if rgb color is either dark or light', () => {
        color = 'rgba(0, 0, 0, 0)';

        expect(colorConvertUtil.isColorDark(color)).toBeTruthy();

        color = 'rgba(25, 36, 47, 1)';

        expect(colorConvertUtil.isColorDark(color)).toBeTruthy();

        color = 'rgb(250, 200, 150)';

        expect(colorConvertUtil.isRgbTransparent(color)).toBeFalsy();
    });

    it('should determine if hex color is either dark or light', () => {
        color = '#FFFFFF';

        expect(colorConvertUtil.isColorDark(color)).toBeFalsy();

        color = '#000000)';

        expect(colorConvertUtil.isColorDark(color)).toBeTruthy();

        color = '#cecece)';

        expect(colorConvertUtil.isColorDark(color)).toBeFalsy();
    });
});
