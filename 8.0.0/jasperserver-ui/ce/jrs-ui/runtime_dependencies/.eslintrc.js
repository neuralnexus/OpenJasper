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

const ESCAPE_METHODS = ["xssUtil.escape", "xssUtil.hardEscape", "escapeXSS", "_escapeXSS"];

module.exports = {
    "plugins": [
        "no-unsanitized"
    ],
    "env": {
        "es6": true,
        "browser": true,
        "node": false
    },
    "rules": {
        "indent": "off",
        "no-unsanitized/property": [
            "error",
            {},
            {
                "innerHTML": {
                    "escape": {"methods": ESCAPE_METHODS}
                },
                "outerHTML": {
                    "escape": {"methods": ESCAPE_METHODS}
                }
            }
        ],
        "no-unsanitized/method": [
            "error",
            {},
            {
                "insertAdjacentHTML": {
                    "properties": [1],
                    "escape": {"methods": ESCAPE_METHODS}
                },
                "createContextualFragment": {
                    "properties": [0],
                    "escape": {"methods": ESCAPE_METHODS}
                },

                // check first parameter to .write(), as long as the preceeding object matches the regex "document"
                "write": {
                    "objectMatches": [
                        "document"
                    ],
                    "properties": [0],
                    "escape": {"methods": ESCAPE_METHODS}
                },

                // check first parameter to .writeLn(), as long as the preceeding object matches the regex "document"
                "writeln": {
                    "objectMatches": [
                        "document"
                    ],
                    "properties": [0],
                    "escape": {"methods": ESCAPE_METHODS}
                },

                "parseFromString": {
                    // When "objectMatches" is commented out, the rule catches all the method calls
                    // regardless the object.  The object is matched via regex /DOMParser/gi
                    // "objectMatches": [
                    //     "DOMParser"
                    // ],
                    "properties": [0],
                    "escape": {"methods": ESCAPE_METHODS}
                }
            }
        ]
    }
};