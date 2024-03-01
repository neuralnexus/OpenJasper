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

import JavaScriptExceptionBiComponentError from '../error/JavaScriptExceptionBiComponentError';
import BiComponentError from '../error/BiComponentError';
import SchemaValidationBiComponentError from '../error/SchemaValidationBiComponentError';
import ContainerNotFoundBiComponentError from '../error/ContainerNotFoundBiComponentError';
import AlreadyDestroyedBiComponentError from '../error/AlreadyDestroyedBiComponentError';
import RequestBiComponentError from '../error/RequestBiComponentError';
import NotYetRenderedBiComponentError from '../error/NotYetRenderedBiComponentError';
import InputControlParameterNotFoundBiComponentError from '../error/InputControlParameterNotFoundBiComponentError';
export default {
    genericError: function (errorCode, message, parameters) {
        return new BiComponentError(errorCode, message, parameters);
    },
    validationError: function (validationError) {
        return new SchemaValidationBiComponentError(validationError);
    },
    javaScriptException: function (ex) {
        return new JavaScriptExceptionBiComponentError(ex);
    },
    requestError: function (xhr, code) {
        return new RequestBiComponentError(xhr, code);
    },
    containerNotFoundError: function (container) {
        return new ContainerNotFoundBiComponentError(container);
    },
    alreadyDestroyedError: function () {
        return new AlreadyDestroyedBiComponentError();
    },
    notYetRenderedError: function () {
        return new NotYetRenderedBiComponentError();
    },
    inputControlParameterNotFound: function (message) {
        return new InputControlParameterNotFoundBiComponentError(message);
    }
};