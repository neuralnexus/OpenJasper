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

import Backbone from 'backbone';
import $ from 'jquery';
import AttributesFilterCollectionView from 'src/attributes/attributesFilter/AttributesFilterCollectionView';
import AttributesFilterView from 'src/attributes/attributesFilter/view/AttributesFilterView';
import _ from 'underscore';

var attributesFilterCollectionViewTemplate = '<span>FilterBy:</span><select class=\'filterItems\'></select>';

describe('AttributesFilterCollectionView Tests', function () {
    var attributesFilterCollection;
    beforeEach(function () {
        attributesFilterCollection = new AttributesFilterCollectionView({
            collection: new Backbone.Collection([
                {
                    title: 'Filter1',
                    value: 'true',
                    field: 'defaultFilter',
                    selected: true
                },
                {
                    title: 'Filter2',
                    value: 'false',
                    field: 'inherited'
                },
                {
                    title: 'Filter3',
                    value: 'true',
                    field: 'inherited'
                }
            ]),
            childView: AttributesFilterView,
            childViewContainer: 'select',
            template: _.template(attributesFilterCollectionViewTemplate),
            model: new Backbone.Model({currentFilter: null}),
            targetCollection: new Backbone.Collection([
                {inherited: true},
                {inherited: false},
                {inherited: false},
                {inherited: false},
                {inherited: true}
            ])
        });
        attributesFilterCollection.targetCollection.trigger('sync');
        $('body').append(attributesFilterCollection.render().$el);
    });
    afterEach(function () {
        attributesFilterCollection.remove();
    });
    it('should be properly initialized', function () {
        expect(attributesFilterCollection.filterCollection).toBeDefined();
        expect(attributesFilterCollection.targetCollection).toBeDefined();
        expect(attributesFilterCollection.filterCollection.length).toEqual(5);
        expect(attributesFilterCollection.filterCollection.length).toEqual(attributesFilterCollection.targetCollection.length);
    });
    it('should add model to filterCollection on add to targetCollection', function () {
        attributesFilterCollection.targetCollection.add({inherited: false});
        expect(attributesFilterCollection.targetCollection.length).toEqual(6);
        expect(attributesFilterCollection.filterCollection.length).toEqual(attributesFilterCollection.targetCollection.length);
    });
    it('should remove model to filterCollection on remove from targetCollection', function () {
        var model = attributesFilterCollection.targetCollection.at(0);
        attributesFilterCollection.targetCollection.remove(model);
        expect(attributesFilterCollection.targetCollection.length).toEqual(4);
        expect(attributesFilterCollection.filterCollection.length).toEqual(attributesFilterCollection.targetCollection.length);
    });
    it('should filter models', function () {
        attributesFilterCollection.filter({inherited: false});
        expect(attributesFilterCollection.targetCollection.length).toEqual(3);
    });
    it('should reset filter', function () {
        var select = attributesFilterCollection.$el.find('select');
        select.val('inherited::true');
        var e = $.Event('change');
        e.target = select[0];
        select.trigger(e);
        expect(attributesFilterCollection.currentCriteria).toEqual({inherited: true});
        expect(attributesFilterCollection.targetCollection.length).toEqual(2);
        attributesFilterCollection.reset();
        expect(attributesFilterCollection.currentCriteria).toEqual({defaultFilter: true});
        expect(attributesFilterCollection.targetCollection.length).toEqual(5);
    });
});