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

import settingsUtility from 'src/dataSource/util/settingsUtility';

describe('Tests for settingsUtility ', function () {
    it('Check if deepDefaults is working correctly', function () {
        var deepDefaults = settingsUtility.deepDefaults({
            settingsGroup1: {
                group1Setting1: 'group1Setting1OptionValue',
                group1Setting2: 'group1Setting2OptionValue'
            },
            settingsGroup3: { group3Setting1: 'group3Setting1OptionValue' }
        }, {
            settingsGroup1: {
                group1Setting1: 'group1Setting1DefaultValue',
                group1Setting2: 'group1Setting2DefaultValue'
            },
            settingsGroup2: { group2Setting1: 'group2Setting1DefaultValue' },
            settingsGroup3: {
                group3Setting1: 'group3Setting1DefaultValue',
                group3Setting2: 'group3Setting2DefaultValue'
            }
        });
        expect(deepDefaults).not.toBeUndefined();
        expect(deepDefaults).not.toBeNull();
        var settingsGroup1 = deepDefaults.settingsGroup1;
        expect(settingsGroup1).not.toBeUndefined();
        expect(settingsGroup1).not.toBeNull();
        expect(settingsGroup1.group1Setting1).toBe('group1Setting1OptionValue');
        expect(settingsGroup1.group1Setting2).toBe('group1Setting2OptionValue');
        var settingsGroup2 = deepDefaults.settingsGroup2;
        expect(settingsGroup2).not.toBeUndefined();
        expect(settingsGroup2).not.toBeNull();
        expect(settingsGroup2.group2Setting1).toBe('group2Setting1DefaultValue');
        var settingsGroup3 = deepDefaults.settingsGroup3;
        expect(settingsGroup3).not.toBeUndefined();
        expect(settingsGroup3).not.toBeNull();
        expect(settingsGroup3.group3Setting1).toBe('group3Setting1OptionValue');
        expect(settingsGroup3.group3Setting2).toBe('group3Setting2DefaultValue');
    });
});