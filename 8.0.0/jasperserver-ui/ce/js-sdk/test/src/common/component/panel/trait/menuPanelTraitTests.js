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
import menuPanelTrait from 'src/common/component/panel/trait/menuPanelTrait';
import ClickMenu from 'src/common/component/menu/ClickMenu';
import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
describe('menuPanelTrait', function () {
    let sandbox;
    beforeEach(() => {
        sandbox = sinon.createSandbox();
    });

    afterEach(() => {
        sandbox.restore();
    })

    it('should read menu options', function () {
        var obj = {};
        menuPanelTrait.onConstructor.call(obj, {
            menuOptions: [],
            menuOptionSelectable: true
        });
        expect(obj.menuOptions).toEqual([]);
        expect(obj.menuOptionSelectable).toEqual(true);
    });
    it('should init ClickMenu instance and re-throw all option events', function () {
        var obj = {
            $el: $('<div><div class=\'title\'></div></div>'),
            menuOptionSelectable: true,
            menuPadding: {
                top: 10,
                left: 5
            },
            menuToggleMode: true,
            menuOptions: [{
                label: 'Test',
                action: 'test'
            }]
        };
        _.extend(obj, Backbone.Events);
        menuPanelTrait.afterSetElement.call(obj);
        expect(obj.$menuEl).toBeDefined();
        expect(obj.$el.find(obj.$menuEl).length).toBe(1);
        expect(obj.filterMenu).toBeDefined();
        expect(obj.filterMenu instanceof ClickMenu).toBe(true);
        expect(obj.filterMenu.collection.length).toBe(1);
        expect(obj.filterMenu.collection.at(0).get('action')).toBe(obj.menuOptions[0].action);
        expect(obj.filterMenu.collection.at(0).get('label')).toBe(obj.menuOptions[0].label);
        expect(obj.filterMenu.$attachTo[0]).toBe(obj.$menuEl[0]);
        expect(obj.filterMenu.toggle).toBe(obj.menuOptionSelectable);
        expect(obj.filterMenu.toggleClass).toBe('active');
        expect(obj.filterMenu.padding).toEqual({ top: 10,  left: 5 });

        expect(obj.filterMenu.additionalSettings.toggle).toEqual(true);
        expect(obj.filterMenu.additionalSettings.toggleClass).toEqual('active');
        expect(obj.filterMenu.additionalSettings.padding).toEqual({ top: 10, left: 5 });
        expect(obj.filterMenu.additionalSettings.toggleMode).toEqual(true);


        var triggerSpy = sandbox.spy(obj, 'trigger');
        var filterMenuHideSpy = sandbox.spy(ClickMenu.prototype, 'hide');
        obj.filterMenu.trigger(obj.filterMenu.contextName + ':' + obj.menuOptions[0].action, obj.filterMenu.options[0], obj.filterMenu.options[0].model);
        expect(filterMenuHideSpy).toHaveBeenCalled();
        expect(triggerSpy).toHaveBeenCalledWith(obj.filterMenu.contextName + ':' + obj.menuOptions[0].action, obj.filterMenu.options[0], obj.filterMenu.options[0].model);
        obj.filterMenu.remove();
    });
    it('should remove created menu component instance', function () {
        var obj = { filterMenu: { remove: sandbox.spy() } };
        menuPanelTrait.onRemove.call(obj);
        expect(obj.filterMenu.remove.called).toBeTruthy();
    });
});