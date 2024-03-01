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
import epoxyViewMixin from 'src/common/view/mixin/epoxyViewMixin';
import _ from 'underscore';
describe('epoxyViewMixin tests', function () {
    it('should be an object', function () {
        expect(_.isObject(epoxyViewMixin)).toBe(true);
    });
    it('should have proper methods', function () {
        expect(epoxyViewMixin.epoxifyView).toBeDefined();
        expect(epoxyViewMixin.applyEpoxyBindings).toBeDefined();
        expect(epoxyViewMixin.removeEpoxyBindings).toBeDefined();
    });
    it('should mixin itself to view', function () {
        var View = Backbone.View.extend({});
        _.extend(View.prototype, epoxyViewMixin);
        expect(View.prototype.epoxifyView).toBeDefined();
        expect(View.prototype.applyEpoxyBindings).toBeDefined();
        expect(View.prototype.removeEpoxyBindings).toBeDefined();
    });
    it('should not override remove method', function () {
        var removeMethod = function () {
            return 'Remove!';
        };
        var View = Backbone.View.extend({ remove: removeMethod });
        _.extend(View.prototype, epoxyViewMixin);
        expect(View.prototype.remove).toEqual(removeMethod);
    });
    it('should not override bindings and filters object', function () {
        var bindingHandlers = {
            handler: {
                get: function () {
                },
                set: function () {
                }
            }
        };
        var bindingFilters = {
            filter: {
                get: function () {
                },
                set: function () {
                }
            }
        };
        var View = Backbone.View.extend({
            bindingHandlers: bindingHandlers,
            bindingFilters: bindingFilters
        });
        _.extend(View.prototype, epoxyViewMixin);
        expect(_.isEqual(View.prototype.bindingFilters, bindingFilters)).toBeTruthy();
        expect(_.isEqual(View.prototype.bindingHandlers, bindingHandlers)).toBeTruthy();
    });
});