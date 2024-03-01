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
import HolidayCalView from 'src/scheduler/view/editor/HolidayCalView';

describe('Holiday Calendar View:', () => {

    const collection = new Backbone.Collection();

    let view;
    let renderStub;

    beforeEach(() => {
        renderStub = sinon.spy(HolidayCalView.prototype, 'render');

        view = new HolidayCalView({
            collection
        });
    });

    afterEach(() => {
        view.remove();
        renderStub.restore();
    });

    it('should call render() method when collection gets updated', function () {
        collection.trigger('reset');
        expect(renderStub.callCount).toEqual(1);
    });

    it('should call render() method when collection gets updated', function () {
        collection.trigger('change');
        expect(renderStub.callCount).toEqual(1);
    });
});
