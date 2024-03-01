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

const webpack = require("webpack");
const path = require("path");
const ruleTest = require('./webpack/utils/ruleTest');

const webpackModuleExclude = /node_modules/;
const jCryptionPath = 'jCryption/jquery.jcryption';

module.exports = ({test = false, coverage = false, ignoreMomentLocales = true}, {mode} = {mode: 'development'}) => {
    const devtool = process.env.SOURCE_MAP || (test ? 'inline-source-map' : 'source-map');

    const devModules = [path.resolve(process.cwd(), 'node_modules')];
    // added to have shorter paths in test imports
    // so instead of using full relative form:
    // import Dialog from '../../../../src/common/component/dialog/Dialog'
    // in unit tests it is possible to use shorter form:
    // import Dialog from 'src/common/component/dialog/Dialog'
    const testModules = devModules.concat([process.cwd()]);

    return {
        // webpack config name. useful then there are multiple configurations exported from webpack config
        // see https://webpack.js.org/configuration/configuration-types/#exporting-multiple-configurations
        name: 'common',
        mode: mode,
        devtool: devtool === 'false' ? false : devtool,

        resolve: {
            modules: test ? testModules : devModules,
            extensions: ['.ts', '.tsx', '.js', '.jsx'],
            // webpack 5 specific
            fallback: {
                buffer: false,
                stream: false
            },
            alias: {
                // There are few reasons why we need aliases:
                // 1. If we have 2 3'd party libs like jQuery and backbone and backbone uses jQuery inside itself
                //    in this case we can say to backbone from which place jQuery should be loaded in two ways:
                //    - via alias
                //    - via imports-loader
                //    we prefer to use alias since it's cleaner than imports-loader
                // 2. We use non-default script fro this library.
                //    for example for 'moment-timezone' we are going to use the following script:
                //    builds/moment-timezone-with-data
                //    so we either should use this path in each import or we can add an alias
                //    we prefer to add an alias in some cases
                // 3. main entry is missed in libraries package.json

                //this aliases are used for jquery.ui.touch-punch 3rd party library
                'jquery-ui/widget': 'jquery-ui/ui/widget',
                'jquery-ui/widgets/mouse': 'jquery-ui/ui/widgets/mouse',

                'jCryption': jCryptionPath,
                'jquery-ui-touch-punch': 'jquery-ui-touch-punch/jquery.ui.touch-punch',
                'jquery.selection': 'jquery.selection/src/jquery.selection',
                'jquery.urldecoder': 'jquery.urldecoder/jquery.urldecoder',
                'perfect-scrollbar': 'perfect-scrollbar/dist/js/perfect-scrollbar.jquery',
                'xregexp': 'xregexp/xregexp-all',

                //We can not use underscoreExtension.js here because it's es6 module
                //while in backbone which is es5, underscore in this case should be used as _.default instead of just _
                //Thus underscoreExtension should be loaded manually before any extensions are used
                //like in test-context.js
                'underscore': 'underscore/underscore-umd',
                'underscore.string': 'underscore.string/dist/underscore.string'
            }
        },
        module: {
            rules: [
                //jCryption is neither ES6 or AMD module so we should use imports/exports loaders
                //to provide dependencies and export proper global var's
                {
                    test: ruleTest(jCryptionPath),
                    use: [
                        'exports-loader?BigInt,biToHex,biToString',
                        'imports-loader?jQuery=jquery'
                    ]
                },
                //jquery-ui-sliderAccess is neither ES6 or AMD module so we should use imports/exports loaders
                //to provide dependencies and export proper global var's
                {
                    test: ruleTest("jqueryui-timepicker-addon/dist/jquery-ui-sliderAccess"),
                    use: ['imports-loader?jQuery=jquery']
                },
                //jquery.selection is neither ES6 or AMD module so we should use imports/exports loaders
                //to provide dependencies and export proper global var's
                {
                    test: ruleTest("jquery.selection/src/jquery.selection"),
                    use: ['imports-loader?jQuery=jquery']
                },
                //jquery.urldecoder is neither ES6 or AMD module so we should use imports/exports loaders
                //to provide dependencies and export proper global var's
                {
                    test: ruleTest("jquery.urldecoder/jquery.urldecoder"),
                    use: ['imports-loader?jQuery=jquery']
                },
                //use babel loader to transpile sources
                {
                    test: /\.(js|jsx|tsx|ts)$/,
                    enforce: 'pre',
                    exclude: webpackModuleExclude,
                    use: [
                        {
                            loader: 'babel-loader',
                            options: {
                                plugins: test ? [
                                    //babel rewire exports plugin setup
                                    //see https://github.com/asapach/babel-plugin-rewire-exports
                                    ['rewire-exports', {
                                        'unsafeConst': true
                                    }]
                                ] : []
                            }
                        }
                    ]
                },
                //Code coverage setup
                //see https://github.com/webpack-contrib/istanbul-instrumenter-loader
                (test && coverage) ? {
                    test: /\.(js|jsx|tsx|ts)$/,
                    enforce: 'post',
                    include: [
                        path.resolve(process.cwd(), 'src'),
                    ],
                    use: [
                        {
                            loader: 'istanbul-instrumenter-loader',
                            options: {
                                esModules: true
                            }
                        }
                    ]
                } : {},
                //raw loaders for templates and CSS
                {
                    test: /\.htm$/,
                    exclude: webpackModuleExclude,
                    use: {loader: 'raw-loader'}
                },
                // file loader allows to copy file to the build folder and form proper url
                // usually images are used from css files, see css loader below
                {
                    test: /\.png$/,
                    exclude: webpackModuleExclude,
                    use: [
                        {
                            loader: 'url-loader',
                            options: {
                                limit: Number(process.env.WEBPACK_URL_LOADER_LIMIT_KB || 8192) * 1024,
                                fallback: 'file-loader',

                                //file-loader options
                                name: "_assets/[name].[ext]"
                            }
                        }
                    ]
                },
                // css files are processed to copy any dependent resources like images
                // then they copied to the build folder and inserted via link tag
                {
                    test: /\.css$/,
                    exclude: webpackModuleExclude,
                    // for tests we use simplified raw-loader for css files
                    use: test ? {loader: 'raw-loader'} : [
                        {
                            loader: 'style-loader',
                            options: {
                                injectType: 'singletonStyleTag'
                            }
                        },
                        'css-loader'
                    ]
                }
            ]
        },
        plugins: [
            new webpack.EnvironmentPlugin({
                LOGGER_ENABLED: process.env.LOGGER_ENABLED,
                LOGGER_LEVEL: process.env.LOGGER_LEVEL,
                LOGGER_APPENDERS: process.env.LOGGER_APPENDERS,
                TEST_ENV: test,
                // webpack 5 specific
                NODE_DEBUG: ''
            }),
            //Helps to provide jquery to the libs which expect it in global scope
            new webpack.ProvidePlugin({
                jQuery: 'jquery',
                'window.jQuery': 'jquery'
            }),
            ...(
                test ? [
                    //Allows to run only subset of unit tests
                    new webpack.ContextReplacementPlugin(/\.\/src/, (context) => {
                        Object.assign(context, {
                            regExp: new RegExp(process.env.KARMA_TESTS)
                        })
                    })
                ] : []
            ),
            ...(
                !test && ignoreMomentLocales ? [
                    //In test mode we want to bundle all locales so they're available synchronously
                    // while in non test mode they should be loaded dynamically
                    new webpack.IgnorePlugin({
                        resourceRegExp: /^\.\/locale$/,
                        contextRegExp: /moment$/
                    })
                ] : []
            )
        ],
        watch: test
    }
};