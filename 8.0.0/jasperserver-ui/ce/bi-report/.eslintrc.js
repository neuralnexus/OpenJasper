/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

let airbnbRules = require('js-sdk/util/eslint/airbnb-rules');

module.exports = {
    "parser": "@babel/eslint-parser",
    "plugins": ["@babel"],
    "env": {
        "node": true,
        "es6": true
    },
    "rules": {
        "no-undef": 2,
        "no-eval": 2,
        "no-trailing-spaces": 2,
        "no-new-wrappers": 2,
        "no-new-object": 2,
        "no-array-constructor": 2,
        "indent": ["error", 4]
    },
    "overrides": [
        {
            "files": ["**/*.ts", "**/*.tsx"],
            "parser": "@typescript-eslint/parser",
            "plugins": ["@typescript-eslint", ...airbnbRules.plugins],
            "settings": {...airbnbRules.settings},
            "rules": {
                ...airbnbRules.rules,
                "no-use-before-define": "off",
                "@typescript-eslint/no-use-before-define": ["error"],
                "no-shadow": "off",
                "@typescript-eslint/no-shadow": ["error"],
                "no-unused-vars": "off",
                "@typescript-eslint/no-unused-vars": "error",
                "linebreak-style": "off",
                "max-len": "off",
                "indent": ["error", 4],
                "arrow-body-style": "off",
                "react/jsx-indent": ["error", 4],
                "react/prefer-stateless-function": "off",
                "react/jsx-indent-props": ["error", 4],
                "react/jsx-no-bind": "off",
                "import/no-unresolved": "off",
                "import/no-extraneous-dependencies": "off",
                "comma-dangle": "off",
                "padded-blocks": "off",
                "one-var": "off",
                "semi": "off",
                "no-extra-semi": "off",
                "react/destructuring-assignment": "off",
                "react/jsx-one-expression-per-line": "off",
                "jsx-a11y/click-events-have-key-events": "off",
                "jsx-a11y/no-noninteractive-element-interactions": "off"
            },
            "parserOptions": {
                "ecmaFeatures": {
                    "jsx": true
                },
                "useJSXTextNode": true
            }
        }
    ]
};
