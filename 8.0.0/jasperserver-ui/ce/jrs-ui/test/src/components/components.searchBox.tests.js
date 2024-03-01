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

import SearchBox from 'src/components/components.searchBox';
import jQuery from 'jquery';
import searchBoxText from './test/templates/searchBox.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {rewire$ajaxTargettedUpdate, restore} from 'src/core/core.ajax';
import {Keys} from 'src/util/utils.common';

describe('SearchBox Component', function () {
    var searchBox, searchButton, clearButton, searchBoxInput;
    beforeEach(function () {
        setTemplates(searchBoxText);
        searchBox = new SearchBox({ id: 'searchBox' });
        searchButton = jQuery('.button.search');
        clearButton = jQuery('.button.searchClear');
        searchBoxInput = jQuery('#searchBoxInput');
        rewire$ajaxTargettedUpdate(function () {
            return false;
        });
    });
    afterEach(function () {
        restore();
    });
    it('should get empty string text by default', function () {
        expect(searchBox.getText()).toEqual('');
    });
    it('should set \'MyReport\' text and get it', function () {
        searchBox.setText('MyReport');
        expect(searchBox.getText()).toEqual('MyReport');
    });
    it('should invoke onSearch method when pressing enter in search input', function () {
        spyOn(searchBox, 'onSearch').and.callThrough();
        searchBoxInput.simulate('keypress', { keyCode: Keys.DOM_VK_ENTER });
        expect(searchBox.onSearch).toHaveBeenCalled();
        searchBox.setText('Some Report');
        searchBoxInput.simulate('keypress', { keyCode: Keys.DOM_VK_ENTER });
        expect(searchBox.onSearch).toHaveBeenCalledWith('Some Report');
    });
    it('should invoke onSearch method when clicking search button', function () {
        spyOn(searchBox, 'onSearch').and.callThrough();
        searchButton.simulate('click');
        expect(searchBox.onSearch).toHaveBeenCalled();
        searchBox.setText('Some Report');
        searchButton.simulate('click');
        expect(searchBox.onSearch).toHaveBeenCalledWith('Some Report');
    });
    it('should clear input and invoke onSearch method when clear button is clicked', function () {
        spyOn(searchBox, 'onSearch').and.callThrough();
        searchBox.setText('Text to clean');
        expect(searchBox.getText()).toEqual('Text to clean');
        clearButton.simulate('click');
        expect(searchBox.getText()).toEqual('');
        expect(searchBox.onSearch).toHaveBeenCalledWith('');
    });
    it('should not invoke onSearch method when search box is disabled', function () {
        spyOn(searchBox, 'onSearch').and.callThrough();
        searchBox.disable();
        searchButton.simulate('click');
        expect(searchBox.onSearch).not.toHaveBeenCalled();
        clearButton.simulate('click');
        expect(searchBox.onSearch).not.toHaveBeenCalled();
    });
    it('should not clear search input when search box is disabled', function () {
        spyOn(searchBox, 'onSearch').and.callThrough();
        searchBox.setText('Text to not be cleaned');
        searchBox.disable();
        expect(searchBox.getText()).toEqual('Text to not be cleaned');
        clearButton.simulate('click');
        expect(searchBox.getText()).toEqual('Text to not be cleaned');
    });
    it('should invoke onSearch method on enter and search button click when search box is enabled after it was disabled', function () {
        spyOn(searchBox, 'onSearch').and.callThrough();
        searchBox.disable();
        searchBox.enable();
        searchButton.simulate('click');
        expect(searchBox.onSearch).toHaveBeenCalled();
        searchBox.setText('Some Report');
        searchButton.simulate('click');
        expect(searchBox.onSearch).toHaveBeenCalledWith('Some Report');
        searchBoxInput.simulate('keypress', { keyCode: Keys.DOM_VK_ENTER });
        expect(searchBox.onSearch).toHaveBeenCalled();
        searchBox.setText('Some Report');
        searchBoxInput.simulate('keypress', { keyCode: Keys.DOM_VK_ENTER });
        expect(searchBox.onSearch).toHaveBeenCalledWith('Some Report');
    });
});