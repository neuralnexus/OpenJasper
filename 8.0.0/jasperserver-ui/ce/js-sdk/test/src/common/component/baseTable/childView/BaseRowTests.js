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
import _ from 'underscore';
import Marionette from 'backbone.marionette';
import BaseRow from 'src/common/component/baseTable/childView/BaseRow';
describe('BaseRow component', function () {
    var baseRow;
    beforeEach(function () {
        baseRow = new BaseRow();
    });
    afterEach(function () {
        baseRow && baseRow.remove();
    });
    it('should be Marionette.ItemView instance', function () {
        expect(typeof BaseRow).toBe('function');
        expect(BaseRow.prototype instanceof Marionette.ItemView).toBeTruthy();
    });
    it('should have public methods', function () {
        expect(baseRow.initialize).toBeDefined();
        expect(baseRow.render).toBeDefined();
        expect(baseRow.remove).toBeDefined();
    });
    it('should call epoxifyView on initialize', function () {
        baseRow && baseRow.remove();
        var epoxifyViewSpy = sinon.spy(BaseRow.prototype, 'epoxifyView');
        baseRow = new BaseRow();
        expect(epoxifyViewSpy).toHaveBeenCalled();
        epoxifyViewSpy.restore();
    });
    it('should call applyEpoxyBindings on render', function () {
        baseRow && baseRow.remove();
        var applyEpoxyBindingsSpy = sinon.spy(BaseRow.prototype, 'applyEpoxyBindings');
        baseRow = new BaseRow({ template: _.template('<div><span></span></div>') });
        baseRow.render();
        expect(applyEpoxyBindingsSpy).toHaveBeenCalled();
        applyEpoxyBindingsSpy.restore();
    });
    it('should call removeEpoxyBindings on remove', function () {
        baseRow && baseRow.remove();
        var removeEpoxyBindingsSpy = sinon.spy(BaseRow.prototype, 'removeEpoxyBindings');
        baseRow = new BaseRow({ template: _.template('<div><span></span></div>') });
        baseRow.render();
        baseRow.remove();
        expect(removeEpoxyBindingsSpy).toHaveBeenCalled();
        removeEpoxyBindingsSpy.restore();
    });
});