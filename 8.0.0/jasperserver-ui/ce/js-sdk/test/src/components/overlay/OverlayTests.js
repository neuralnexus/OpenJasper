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
import Overlay from 'src/components/overlay/Overlay';

describe('Overlay component', function(){
    var overlay;

    beforeEach(function() {
        overlay = new Overlay();
    });

    afterEach(function () {
        overlay && overlay.remove();
    });

    it('should be Backbone.View instance', function(){
        expect(typeof Overlay).toBe('function');
        expect(Overlay.prototype instanceof Backbone.View).toBeTruthy();
    });

    it('should have public functions', function() {
        expect(overlay.show).toBeDefined();
        expect(overlay.hide).toBeDefined();
    });

    it('should have element with complex structure', function(){
        expect(overlay.$el.hasClass("jr-mOverlay")).toBe(true);
    });

    it('method "show" should remove class "jr-isHidden" from template', function(){
        expect(overlay.$el.hasClass("jr-isHidden")).toBe(true);

        overlay.show();

        expect(overlay.$el.hasClass("jr-isHidden")).toBe(false);
    });

    it('method "hide" should add class "jr-isHidden" to template', function(){
        overlay.show();
        expect(overlay.$el.hasClass("jr-isHidden")).toBe(false);

        overlay.hide();
        expect(overlay.$el.hasClass("jr-isHidden")).toBe(true);
    });

});