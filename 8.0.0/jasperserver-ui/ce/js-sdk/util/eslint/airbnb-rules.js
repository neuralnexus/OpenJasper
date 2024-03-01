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

// airbnb rules are expected to be used in "extend" part of the .eslintrc
// but eslint does not allow to use extend in overrides part of the .eslintrc.js
// This is known issue. See here: https://github.com/eslint/eslint/issues/8813
// so we are extending airbnb configs manually

let airbnbBase = require('eslint-config-airbnb-base');
let airbnbReact = require('eslint-config-airbnb/rules/react');
let airbnbReactA11Y = require('eslint-config-airbnb/rules/react-a11y');
let airbnbReactHooks = require('eslint-config-airbnb/rules/react-hooks');
let airbnbTypescript = require('eslint-config-airbnb-typescript');
let [airbnbTypescriptShared] = require('eslint-config-airbnb-typescript/lib/shared').overrides;

function addPlugins(source, target) {
    if (source) {
        source.forEach((plugin) => {
            if (target.indexOf(plugin) === -1) {
                target.push(plugin);
            }
        });
    }
}

let airbnbRules = airbnbBase.extends.reduce((memo, element) => {
    element = require(element);

    Object.assign(memo.rules, element.rules);
    Object.assign(memo.settings, element.settings);

    addPlugins(element.plugins, memo.plugins);

    return memo;
}, {
    rules: {},
    settings: {},
    plugins: []
});

Object.assign(
    airbnbRules.rules,
    airbnbReact.rules,
    airbnbReactA11Y.rules,
    airbnbReactHooks.rules,
    airbnbTypescriptShared.rules,
    airbnbTypescript.rules
);

Object.assign(
    airbnbRules.settings,
    airbnbReact.settings,
    airbnbReactA11Y.settings,
    airbnbReactHooks.settings,
    airbnbTypescriptShared.settings,
    airbnbTypescript.settings
);

addPlugins(airbnbReact.plugins, airbnbRules.plugins);
addPlugins(airbnbReactA11Y.plugins, airbnbRules.plugins);
addPlugins(airbnbReactHooks.plugins, airbnbRules.plugins);

module.exports = airbnbRules;