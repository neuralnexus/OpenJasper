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

import _ from 'underscore';
import Backbone from 'backbone';
import Marionette from 'backbone.marionette';
import $ from 'jquery';
import attributesDesignerFactory from 'src/attributes/factory/attributesDesignerFactory';
import attributesTypesEnum from 'src/attributes/enum/attributesTypesEnum';

describe('AttributesDesginerFactory Tests', function () {
    var model = Backbone.Model.extend({});
    var options = {
        collection: new Backbone.Collection([
            {first: 'first'},
            {second: 'second'}
        ], {model: model}),
        childView: Marionette.ItemView.extend({
            template: _.template('<div><span></span></div>'),
            tagName: 'p',
            slideDown: function () {
            },
            toggleMode: function () {
                return new $.Deferred();
            },
            toggleActive: function () {
                return new $.Deferred();
            },
            validateModel: function () {
                return this.model.isValid(true);
            },
            isStateConfirmed: function () {
            }
        }),
        childViewContainer: '.tbody',
        emptyView: Marionette.ItemView.extend({
            template: _.template('<div><span></span></div>'),
            tagName: 'p'
        })
    };
    it('should be a function', function () {
        expect(typeof attributesDesignerFactory).toEqual('function');
    });
    it('should have server attributes designer', function () {
        var attributesDesigner = attributesDesignerFactory(attributesTypesEnum.SERVER, options);
        expect(typeof attributesDesigner._initButtons).toEqual("function");
        expect(typeof attributesDesigner._initPermissionConfirmEvents).toEqual("function");
        expect(typeof attributesDesigner._onPermissionConfirm).toEqual("function");
        expect(typeof attributesDesigner._onPermissionCancel).toEqual("function");
    });
    it('should have tenant attributes designer', function () {
        var attributesDesigner = attributesDesignerFactory(attributesTypesEnum.TENANT, options);
        expect(attributesDesigner._initButtons).not.toBeDefined();
        expect(typeof attributesDesigner._initPermissionConfirmEvents).toEqual("function");
        expect(typeof attributesDesigner._onPermissionConfirm).toEqual("function");
        expect(typeof attributesDesigner._onPermissionCancel).toEqual("function");
        expect(typeof attributesDesigner._initFilters).toEqual("function");
        expect(typeof attributesDesigner._renderFilters).toEqual("function");
        expect(typeof attributesDesigner._resetFilters).toEqual("function");
        expect(typeof attributesDesigner._findOriginallyInheritedModelByName).toEqual("function");
        expect(typeof attributesDesigner._filterInheritedViews).toEqual("function");
        expect(typeof attributesDesigner._findInheriteds).toEqual("function");
        expect(typeof attributesDesigner._revertInheritedRemoval).toEqual("function");
        expect(typeof attributesDesigner._searchForInherited).toEqual("function");
        expect(typeof attributesDesigner._removeInheritedView).toEqual("function");
        expect(typeof attributesDesigner._addInheritedView).toEqual("function");
    });
    it('should have user attributes designer', function () {
        var attributesDesigner = attributesDesignerFactory(attributesTypesEnum.USER, options);
        expect(attributesDesigner._initPermissionConfirmEvents).not.toBeDefined();
        expect(attributesDesigner._onPermissionConfirm).not.toBeDefined();
        expect(attributesDesigner._onPermissionCancel).not.toBeDefined();
        expect(attributesDesigner._getPermissionConfirmContent).not.toBeDefined();
        expect(typeof attributesDesigner._initFilters).toEqual("function");
        expect(typeof attributesDesigner._renderFilters).toEqual("function");
        expect(typeof attributesDesigner._resetFilters).toEqual("function");
        expect(typeof attributesDesigner._findOriginallyInheritedModelByName).toEqual("function");
        expect(typeof attributesDesigner._filterInheritedViews).toEqual("function");
        expect(typeof attributesDesigner._findInheriteds).toEqual("function");
        expect(typeof attributesDesigner._revertInheritedRemoval).toEqual("function");
        expect(typeof attributesDesigner._searchForInherited).toEqual("function");
        expect(typeof attributesDesigner._removeInheritedView).toEqual("function");
        expect(typeof attributesDesigner._addInheritedView).toEqual("function");
    });
});