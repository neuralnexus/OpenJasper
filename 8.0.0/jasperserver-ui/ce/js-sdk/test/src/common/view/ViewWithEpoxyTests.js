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
import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import ViewWithEpoxy from 'src/common/view/ViewWithEpoxy';
describe('ViewWithEpoxy tests', function () {
    var model = new Backbone.Model({ id: 1 }), template = '<input type=\'text\' value=\'{{- model.id }}\' class=\'{{- options.className }}\' title=\'{{- i18n.title }}\'>', i18n = { title: 'ID' }, view;
    function createView() {
        view = new ViewWithEpoxy({
            template: template,
            model: model,
            i18n: i18n,
            className: 'someClass'
        });
    }
    beforeEach(function () {
        createView();
    });
    afterEach(function () {
        view && view.remove();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof ViewWithEpoxy).toBe('function');
        expect(ViewWithEpoxy.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should throw exception if model or template are not passed to constructor', function () {
        expect(function () {
            new ViewWithEpoxy();
        }).toThrow(new Error('Template must be defined'));
        expect(function () {
            new ViewWithEpoxy({ template: '<div></div>' });
        }).toThrow(new Error('Model must be defined'));
    });
    it('should save template, model, i18n and options as view properties', function () {
        expect(_.isFunction(view.template)).toBe(true);
        expect(view.model).toBe(model);
        expect(view.i18n).toBe(i18n);
        expect(view.options).toEqual({ className: 'someClass' });
    });
    it('should have root element created from template', function () {
        var resultingHtml = $('<div></div>').html(view.$el).html();
        expect(resultingHtml).toBe('<input type="text" value="1" class="someClass" title="ID">');
    });
    it('should apply epoxy bindings in render method', function () {
        var applyEpoxyBindingsSpy = sinon.spy(view, 'applyEpoxyBindings');
        expect(view.render()).toBe(view);
        expect(applyEpoxyBindingsSpy).toHaveBeenCalled();
        applyEpoxyBindingsSpy.restore();
    });
    it('should remove epoxy bindings in remove method and call base remove', function () {
        view && view.remove();
        var baseRemoveSpy = sinon.spy(Backbone.View.prototype, 'remove');
        createView();
        var removeEpoxyBindingsSpy = sinon.spy(view, 'removeEpoxyBindings');
        view.remove();
        expect(removeEpoxyBindingsSpy).toHaveBeenCalled();
        expect(baseRemoveSpy).toHaveBeenCalled();
        removeEpoxyBindingsSpy.restore();
        baseRemoveSpy.restore();
    });
});