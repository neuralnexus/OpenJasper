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

import base64 from 'src/common/util/base64';

describe("base64 Tests.", function() {

    it("should encode/decode string", function() {
        expect(base64.encode("Hello, world")).toEqual("SGVsbG8sIHdvcmxk");
        expect(base64.decode("SGVsbG8sIHdvcmxk")).toEqual("Hello, world");
    });

    it("should encode/decode utf string", function() {
        expect(base64.encode("✓ à la mode")).toEqual("4pyTIMOgIGxhIG1vZGU=");
        expect(base64.decode("4pyTIMOgIGxhIG1vZGU=")).toEqual("✓ à la mode");
    });
});