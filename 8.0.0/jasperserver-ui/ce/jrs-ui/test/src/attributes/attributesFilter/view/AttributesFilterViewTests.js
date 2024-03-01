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
import AttributesFilterView from 'src/attributes/attributesFilter/view/AttributesFilterView';
import Backbone from 'backbone';

describe('AttributesFilterView Tests', function () {
    var attributesFilterView, model = new Backbone.Model({
        field: 'inherited',
        value: 'true',
        title: 'Inherited Only'
    });
    beforeEach(function () {
        attributesFilterView = new AttributesFilterView({model: model});
    });
    afterEach(function () {
        attributesFilterView && attributesFilterView.remove();
    });
    it('should be properly initialized', function () {
        expect(typeof attributesFilterView.template).toEqual("function");
        expect(typeof attributesFilterView.onRender).toEqual("function");
    });
    it('should be properly rendered', function () {
        var onRenderSpy = sinon.spy(attributesFilterView, 'onRender');
        attributesFilterView.render();
        expect(attributesFilterView.$el).toEqual('option');
        expect(attributesFilterView.$el.text()).toEqual('Inherited Only');
        expect(attributesFilterView.$el.val()).toEqual('inherited::true');
        expect(onRenderSpy).toHaveBeenCalled();
        onRenderSpy.restore();
    });
});