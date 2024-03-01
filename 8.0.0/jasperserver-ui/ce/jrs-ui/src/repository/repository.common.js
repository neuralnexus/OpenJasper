/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

import repositorySearch from '../repository/repository.search.root';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import {dynamicList} from '../components/list.base';
import _ from 'underscore';

import 'backbone';

import '../components/components.dependent.dialog';
import '../components/components.toolbarButtons.events';
import '../components/components.tooltip';

import '../util/tools.infiniteScroll';
import '../manage/mng.common';

_.extend(repositorySearch.messages, jrsConfigs.repositorySearch.i18n);
_.extend(dynamicList.messages, jrsConfigs.dynamicList.i18n);
_.extend(window.localContext, jrsConfigs.repositorySearch.localContext);
repositorySearch.initialize(window.localContext);
