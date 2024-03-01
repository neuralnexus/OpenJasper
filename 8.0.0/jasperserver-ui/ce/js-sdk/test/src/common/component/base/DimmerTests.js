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
import $ from 'jquery';
import Dimmer from 'src/common/component/base/Dimmer';
describe('Dialog Dimmer', function () {
    var dimmer;
    it('should have proper API', function () {
        expect(Dimmer.prototype.show).toBeDefined();
        expect(Dimmer.prototype.hide).toBeDefined();
        expect(Dimmer.prototype.css).toBeDefined();
        expect(Dimmer.prototype.setCount).toBeDefined();
        expect(Dimmer.prototype.getCount).toBeDefined();
    });
    it('should be properly initialized', function () {
        dimmer = new Dimmer({
            zIndex: 200,
            position: 'absolute'
        });
        expect($('#dialogDimmer').css('zIndex')).toEqual('200');
        dimmer.remove();
    });
    it('should append only one dom element to the document', function () {
        var d1 = new Dimmer({
            zIndex: 200,
            position: 'absolute'
        });
        var d2 = new Dimmer({
            zIndex: 200,
            position: 'absolute'
        });
        expect($('.dimmer').length).toEqual(1);
        d1.remove();
        d2.remove();
    });
    it('should remove element from the DOM only when last dimmer was removed', function () {
        var d1 = new Dimmer({
                zIndex: 200,
                position: 'absolute'
            }), d2 = new Dimmer({
                zIndex: 200,
                position: 'absolute'
            });
        expect($('.dimmer').length).toEqual(1);
        d1.remove();
        expect($('.dimmer').length).toEqual(1);
        d2.remove();
        expect($('.dimmer').length).toEqual(0);
    });
    it('should set increased count on show and decreased on hide', function () {
        var d = new Dimmer({
                zIndex: 200,
                position: 'absolute'
            }), setCountSpy = sinon.spy(d, 'setCount'), isVisibleSpy = sinon.spy(d, 'isVisible');
        d.show();
        expect(setCountSpy).toHaveBeenCalledWith(1);
        d.hide();
        expect(isVisibleSpy).toHaveBeenCalled();
        expect(setCountSpy).toHaveBeenCalledWith(1);
        isVisibleSpy.restore();
        setCountSpy.restore();
        d.remove();
    });
});