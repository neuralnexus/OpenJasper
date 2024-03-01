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

import "sinon";
import "jasmine-sinon";
import "jquery-simulate";
import "jasmine-jquery";
import "./mock/jrsConfigsMock";
import "../src/common/extension/jqueryExtension";
import "../src/common/extension/underscoreExtension";
import "../src/common/extension/momentExtension";
import "../src/common/extension/numeralExtension";
import "../src/common/extension/epoxyExtension";
import "../src/common/extension/tv4Extension";
import "../src/common/extension/jQueryTimepickerExtension";
import "./tools/enzymeConfig";

let context = require.context('./src', true, /(\.t|T)ests\.(js|jsx|ts|tsx)$/);
context.keys().forEach(context);