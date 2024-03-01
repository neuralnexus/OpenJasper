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

import $ from 'jquery';
import dataSourceTemplate from '../templates/dataSource.htm';
import treeTemplate from '../templates/tree.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

var selectFromRepository = {
    block: null,
    parent: null
};

export default {
    beforeEach: function () {
        // before installing template, we need to remove the '#selectFromRepository' element and then get it back
        selectFromRepository.block = $('#selectFromRepository');
        selectFromRepository.parent = selectFromRepository.block.parent();
        selectFromRepository.block.detach();    // install template
        // install template
        setTemplates(dataSourceTemplate, treeTemplate, '<div id=\'display\'><div class=\'showingToolBar\'><div class=\'content\'><div class=\'header\'><div class=\'title\'></div></div></div></div></div>');
    },
    afterEach: function () {
        // get back the "#selectFromRepository" element
        selectFromRepository.parent.append(selectFromRepository.block);
    }
};