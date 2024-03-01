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
import TooltipModel from 'src/components/tooltip/model/TooltipModel';
import tooltipTypesEnum from 'src/components/tooltip/enum/tooltipTypesEnum';
describe('TooltipModel', function () {
    it('should have predefined defaults', function () {
        var model = new TooltipModel();
        expect(model.get('content')).toEqual({
            title: undefined,
            text: undefined
        });
        expect(model.get('visible')).toEqual(false);
        expect(model.get('offset')).toEqual({
            top: 0,
            left: 0
        });
        expect(model.get('type')).toEqual(tooltipTypesEnum.INFO);
        expect(model.get('defaultType')).toEqual(tooltipTypesEnum.INFO);
    });
    describe('Validation', function () {
        beforeEach(function () {
            this.model = new TooltipModel({ log: { error: sinon.spy() } });
        });
        it('should be valid if \'type\' is a string', function () {
            this.model.set({ type: tooltipTypesEnum.ERROR }, { validate: true });
            expect(this.model.isValid()).toBeTruthy();
        });
        it('should be invalid if \'type\' is not a string', function () {
            this.model.set({ type: 1 }, { validate: true });
            expect(this.model.isValid()).toBeFalsy();
        });
        it('should be valid if \'offset\' is an object', function () {
            this.model.set({
                offset: {
                    top: 10,
                    left: 10
                }
            }, { validate: true });
            expect(this.model.isValid()).toBeTruthy();
        });
        it('should be invalid if \'offset\' is not an object', function () {
            this.model.set({ offset: 1 }, { validate: true });
            expect(this.model.isValid()).toBeFalsy();
        });
        it('should be valid if \'visible\' is boolean', function () {
            this.model.set({ visible: true }, { validate: true });
            expect(this.model.isValid()).toBeTruthy();
        });
        it('should be invalid if \'visible\' isn\'t a boolean', function () {
            this.model.set({ visible: 44 }, { validate: true });
            expect(this.model.isValid()).toBeFalsy();
        });
        it('should be valid if \'content\' is object ', function () {
            this.model.set({ content: {} }, { validate: true });
            expect(this.model.isValid()).toBeTruthy();
        });
        it('should be valid if \'content\' isn`t object or string ', function () {
            this.model.set({ content: true }, { validate: true });
            expect(this.model.isValid()).toBeFalsy();
        });
        it('should log error for failed validation', function () {
            this.model.trigger('invalid', this.model, 'blah');
            expect(this.model.log.error).toHaveBeenCalledWith('blah');
        });
    });
});