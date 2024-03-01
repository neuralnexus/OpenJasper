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
import HolidayCalsCollection from 'src/scheduler/collection/HolidayCalsCollection';

describe('Holiday Calendars Collection:', function () {
    const model = new Backbone.Model();

    let collection;

    beforeEach(() => {
        collection = new HolidayCalsCollection({
            model
        });
    });

    afterEach(() => {
        collection.remove();
    });

    it('parse() method should return models', function () {
        let models = collection.parse({
            calendarName: [
                'calendar_a',
                'calendar_b'
            ]
        });
        expect(models.length).toEqual(2);
        expect(models[1].id).toEqual('calendar_b');
    });
});
