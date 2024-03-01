/*
 * Copyright (C) 2005 - 2021 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import { isValidColorFn } from 'src/common/util/colorPicker/colorValidator';

describe('Color Pikcer Tests ', () => {

    it('should validate hex values', () => {
        expect(isValidColorFn('#90ffff')).toBeTruthy();
        expect(isValidColorFn('#FFFFFF')).toBeTruthy();
        expect(isValidColorFn('#FfFfFa')).toBeTruthy();
        expect(isValidColorFn('#ffffff')).toBeTruthy();
        expect(isValidColorFn('#fffff')).toBeFalsy();
        expect(isValidColorFn(' #fffff ')).toBeFalsy();
        expect(isValidColorFn('#fffffg')).toBeFalsy();
    });

    it('should validate rgb values', () => {
        expect(isValidColorFn('rgb(100, 100, 100)')).toBeTruthy();
        expect(isValidColorFn('rgb( 100,  100,  100 )')).toBeTruthy();
        expect(isValidColorFn('rgb(100, 100, 100,)')).toBeFalsy();
        expect(isValidColorFn(' rgb(100, 100, 100) ')).toBeFalsy();
        expect(isValidColorFn('RGB(100, 100, 100)')).toBeFalsy();
        expect(isValidColorFn('rgB(100, 100, 100)')).toBeFalsy();
        expect(isValidColorFn('rgb(256, 100, 100)')).toBeFalsy();
        expect(isValidColorFn('rgb(100, 256, 100)')).toBeFalsy();
        expect(isValidColorFn('rgb(100, 100, 256)')).toBeFalsy();
        expect(isValidColorFn('rgb(-1, 100, 100)')).toBeFalsy();
        expect(isValidColorFn('rgb(100, -1, 100)')).toBeFalsy();
        expect(isValidColorFn('rgb(100, 100, -1)')).toBeFalsy();
    });

    it('should validate rgba values', () => {
        expect(isValidColorFn('rgba(100, 100, 100, 0)')).toBeTruthy();
        expect(isValidColorFn('rgba(100, 100, 100, 0.5)')).toBeTruthy();
        expect(isValidColorFn('rgba(100, 100, 100, 1)')).toBeTruthy();
        expect(isValidColorFn('rgba( 100,  100,  100,  0 )')).toBeTruthy();

        expect(isValidColorFn('rgba(100, 100, 100, 0,)')).toBeFalsy();
        expect(isValidColorFn(' rgba(100, 100, 100, 0) ')).toBeFalsy();
        expect(isValidColorFn('RGBA(100, 100, 100, 0)')).toBeFalsy();
        expect(isValidColorFn('rgbA(100, 100, 100)')).toBeFalsy();
        expect(isValidColorFn('rgba(256, 100, 100, 0)')).toBeFalsy();
        expect(isValidColorFn('rgba(100, 256, 100, 0)')).toBeFalsy();
        expect(isValidColorFn('rgba(100, 100, 256, 0)')).toBeFalsy();
        expect(isValidColorFn('rgba(100, 100, 100, 10 )')).toBeFalsy();
        expect(isValidColorFn('rgba(-1, 100, 100, 0)')).toBeFalsy();
        expect(isValidColorFn('rgba(100, -1, 100, 0)')).toBeFalsy();
        expect(isValidColorFn('rgba(100, 100, -1, 0)')).toBeFalsy();
        expect(isValidColorFn('rgba(100, 100, 100, -1)')).toBeFalsy();
    });
});
