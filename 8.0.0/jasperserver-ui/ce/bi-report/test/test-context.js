/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import "sinon";
import "jasmine-sinon";
import "jquery-simulate";
import "jasmine-jquery";
import "./mock/jrsConfigsMock"
import "js-sdk/src/common/extension/jqueryExtension";
import "js-sdk/src/common/extension/underscoreExtension";
import "js-sdk/src/common/extension/momentExtension";
import "js-sdk/src/common/extension/numeralExtension";
import "js-sdk/src/common/extension/epoxyExtension";
import "js-sdk/src/common/extension/tv4Extension";
import "js-sdk/src/common/extension/jQueryTimepickerExtension";
import "js-sdk/test/tools/enzymeConfig";

let context = require.context('./src', true, /(\.t|T)ests\.(js|jsx|ts|tsx)$/);

context.keys().forEach(context);