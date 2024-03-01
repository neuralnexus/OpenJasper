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

/*global spyOn*/

import jQuery from 'jquery';
import orgModule from 'src/org/org.root.user';
import templUsers from './test/templates/users.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('Additional actions', function () {
    beforeEach(function () {
        setTemplates(templUsers);
    });
    it('should provide enable all action', function () {
        expect(orgModule.userActionFactory.enableAll).toBeDefined();
        spyOn(orgModule, 'ServerAction').and.callThrough();
        orgModule.userActionFactory.enableAll({
            users: {
                collect: function () {
                    return [
                        'cat',
                        'dog'
                    ];
                }
            }
        });
        expect(orgModule.ServerAction).toHaveBeenCalledWith('enableAll', { userNames: '["cat","dog"]' });
    });
    it('should provide disable all action', function () {
        expect(orgModule.userActionFactory.disableAll).toBeDefined();
        spyOn(orgModule, 'ServerAction').and.callThrough();
        orgModule.userActionFactory.disableAll({
            users: {
                collect: function () {
                    return [
                        'cat',
                        'dog'
                    ];
                }
            }
        });
        expect(orgModule.ServerAction).toHaveBeenCalledWith('disableAll', { userNames: '["cat","dog"]' });
    });
    it('should provide disable all action', function () {
        expect(orgModule.userActionFactory.disableAll).toBeDefined();
        spyOn(orgModule, 'ServerAction').and.callThrough();
        orgModule.userActionFactory.disableAll({
            users: {
                collect: function () {
                    return [
                        'cat',
                        'dog'
                    ];
                }
            }
        });
        expect(orgModule.ServerAction).toHaveBeenCalledWith('disableAll', { userNames: '["cat","dog"]' });
    });
    it('should provide disable all action', function () {
        expect(orgModule.userActionFactory.disableAll).toBeDefined();
        spyOn(orgModule, 'ServerAction').and.callThrough();
        orgModule.userActionFactory.disableAll({
            users: {
                collect: function () {
                    return [
                        'cat',
                        'dog'
                    ];
                }
            }
        });
        expect(orgModule.ServerAction).toHaveBeenCalledWith('disableAll', { userNames: '["cat","dog"]' });
    });
    it('should provide enableAllUsers factory method', function () {
        expect(orgModule.userManager.actionFactory.enableAllUsers).toBeDefined();
        spyOn(orgModule, 'invokeUserAction');
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [];
        });
        orgModule.userManager.actionFactory.enableAllUsers().invokeAction();
        expect(orgModule.invokeUserAction).toHaveBeenCalledWith('enableAll', { users: [] });
    });
    it('should provide enableAllUsers factory method', function () {
        expect(orgModule.userManager.actionFactory.enableAllUsers).toBeDefined();
        spyOn(orgModule, 'invokeUserAction');
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [];
        });
        orgModule.userManager.actionFactory.enableAllUsers().invokeAction();
        expect(orgModule.invokeUserAction).toHaveBeenCalledWith('enableAll', { users: [] });
    });
    it('should provide disableAllUsers factory method', function () {
        expect(orgModule.userManager.actionFactory.disableAllUsers).toBeDefined();
        spyOn(orgModule, 'invokeUserAction');
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [];
        });
        orgModule.userManager.actionFactory.disableAllUsers().invokeAction();
        expect(orgModule.invokeUserAction).toHaveBeenCalledWith('disableAll', { users: [] });
    });
    it('should login work', function () {
        expect(orgModule.userManager.actionFactory.login).toBeDefined();
        spyOn(jQuery('#loginAsForm')[0], 'submit');
        orgModule.userManager.actionFactory.login({
            user: {
                getNameWithTenant: function () {
                    return 'me';
                }
            }
        }).invokeAction();
        expect(jQuery('#loginAsForm')[0].submit).toHaveBeenCalled();
        expect(jQuery('#j_username').val()).toEqual('me');
    });
});