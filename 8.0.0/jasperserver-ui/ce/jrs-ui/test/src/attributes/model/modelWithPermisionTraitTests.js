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
import AttributeModel from 'src/attributes/model/AttributeModel';
import modelWithPermissionTrait from 'src/attributes/model/modelWithPermissionTrait';
import roleEnum from 'src/attributes/enum/roleEnum';

describe('modelWithPermissionTrait Tests', function () {
    it('should be object and have correct functions', function () {
        expect(typeof modelWithPermissionTrait).toBe('object');
        expect(modelWithPermissionTrait._initModelWithPermissionDefaults).toBeDefined();
        expect(modelWithPermissionTrait.computeds.permissionEmbedded).toBeDefined();
        expect(typeof modelWithPermissionTrait.computeds.permissionEmbedded.get).toEqual("function");
        expect(typeof modelWithPermissionTrait.computeds.permissionEmbedded.set).toEqual("function");
        expect(modelWithPermissionTrait.computeds.permissionEmbedded.deps).toEqual([
            '_embedded',
            'permissionMask'
        ]);
    });
    it('should mix defaults into model', function () {
        var defaults = {
            id: undefined,
            name: undefined,
            value: '',
            description: '',
            inherited: false,
            permissionMask: 1,
            secure: false
        };
        var embedded = {
            _embedded: {
                'permission': [{
                    'recipient': roleEnum.ROLE_ADMINISTRATOR,
                    'mask': '1'
                }]
            }
        };
        var ModelWithTrait = AttributeModel.extend(modelWithPermissionTrait);
        var model = new ModelWithTrait();
        expect(model.defaults).toEqual(_.extend({}, defaults, embedded));
    });
});