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
import jQuery from 'jquery';
import actionModel from 'src/actionModel/actionModel.modelGenerator';
import primaryNavModule from 'src/actionModel/actionModel.primaryNavigation';
import menuText from './test/templates/menu.htm';
import mainNavigationText from './test/templates/mainNavigation.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('Primary navigation', function () {
    // in webpack 5 sinon can not stub variable which
    // uses star import: import * as ns from 'library'should create muttons on initialization

    beforeEach(function () {
        setTemplates(menuText, mainNavigationText);
    })

    var navActionModel = {
        'main_home_mutton': [],
        'main_view_mutton': [{
            'type': 'selectAction',
            'text': 'View',
            'children': [{
                'type': 'optionAction',
                'text': 'Search Results',
                'action': 'primaryNavModule.navigationOption',
                'actionArgs': ['search']
            }]
        }]
    };

    it('should create muttons on initialization', function () {
        spyOn(primaryNavModule, 'createMutton');

        primaryNavModule.initializeNavigation();
        var args = primaryNavModule.createMutton.calls.allArgs();
        var expectedArgs = [];
        expect(args).toEqual(expectedArgs);
    });
    it('should create mutton elements', function () {
        jQuery('#navigationOptions .mutton').remove();
        primaryNavModule.createMutton('test_id', 'test');
        var mutton = jQuery('#navigationOptions #test_id');
        expect(mutton.length).toEqual(1);
        expect(mutton).toHaveClass('mutton');
        expect(mutton.text().strip()).toEqual('test');
    });
    it('should open menu on mutton', function () {
        jQuery('#navigationOptions .mutton').remove();
        spyOn(actionModel, 'showDynamicMenu');

        primaryNavModule.initializeNavigation();
        var left = 0, top = 0, height = 100;
        //primaryNavModule.showNavButtonMenu({}, prototype.$(jQuery('#main_view')[0]));
        //expect(actionModel.showDynamicMenu).toHaveBeenCalled();
    });
    it('should make test before navigation (positive)', function () {
        spyOn(primaryNavModule, 'setNewLocation');

        var stub;
        if (window.designerBase) {
            stub = sinon.stub(window.designerBase, 'confirmAndLeave').callsFake(function () {
                return true;
            });
        } else {
            window.designerBase = {
                confirmAndLeave: function () {
                    return true;
                }
            };
        }
        primaryNavModule.navigationOption();
        expect(primaryNavModule.setNewLocation).toHaveBeenCalled();
        stub && stub.restore();
    });
    it('should make test before navigation (negative)', function () {
        spyOn(primaryNavModule, 'setNewLocation');

        var stub;
        if (window.designerBase) {
            stub = sinon.stub(window.designerBase, 'confirmAndLeave').callsFake(function () {
                return false;
            });
        } else {
            window.designerBase = {
                confirmAndLeave: function () {
                    return false;
                }
            };
        }
        //primaryNavModule.navigationOption();
        //expect(primaryNavModule.setNewLocation).not.toHaveBeenCalled();
        stub && stub.restore();
    });
    it('should call setNewLocation method when moving out of dashboard ', function () {
        window.DashboardReportNavigation = {
            confirmAndLeave: function () {
                return true;
            }
        };
        spyOn(primaryNavModule, 'setNewLocation');

        primaryNavModule.navigationOption();
        expect(primaryNavModule.setNewLocation).toHaveBeenCalled();
    });
});
