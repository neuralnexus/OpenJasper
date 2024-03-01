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

// generate default config which is suitable for all faf-modules
const constants = require("karma/lib/constants");
const webpackConfigFactory = require("./webpack.config");
const path = require("path");
const isCoverageEnabled = require("./util/coverage/isCoverageEnabled");

module.exports = {
    // base path, that will be used to resolve files and exclude
    //        basePath: '',


    // frameworks to use
    frameworks: ['jasmine', 'webpack'],

    // https://www.npmjs.com/package/karma-jasmine
    client: {
        // args: ['--grep', '<pattern>'],

        // all config options:
        // https://jasmine.github.io/api/3.6/Configuration
        jasmine: {
            random: process.env.JASMINE_RANDOM === 'true',
            // seed: null,
            // oneFailurePerSpec: false,
            // failFast: false,
            // hideDisabled: false
            // specFilter: true
            // failSpecWithNoExpectations: false
            // Promise: undefined
        }
    },

    // list of files / patterns to load in the browser
    files: [
        //tests loader
        { pattern: 'test/test-context.js', watched: false /* use watch in webpack config instead */}
    ],


    // list of files to exclude
    exclude: [

    ],


    // test results reporter to use
    // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
    // junit reporter is necessary for CI
    reporters: ['coverage-istanbul', 'spec' ,'junit'],

    specReporter: {
        suppressFailed: false,
        suppressSkipped: true,
        suppressPassed: true,
        showSpecTiming: false,
        failFast: false
    },

    preprocessors: {
        'test/test-context.js': ['webpack', 'sourcemap']
    },

    webpack: webpackConfigFactory({test: true, coverage: isCoverageEnabled()}),

    junitReporter: {
        outputFile: process.env.KARMA_JUNIT_REPORT,
        useBrowserName: false
    },

    // any of these options are valid: https://github.com/istanbuljs/istanbuljs/blob/aae256fb8b9a3d19414dcf069c592e88712c32c6/packages/istanbul-api/lib/config.js#L33-L39
    coverageIstanbulReporter: {
        includeAllSources: true,

        // reports can be any that are listed here: https://github.com/istanbuljs/istanbuljs/tree/aae256fb8b9a3d19414dcf069c592e88712c32c6/packages/istanbul-reports/lib
        reports: ['html', 'cobertura', 'lcovonly', 'text-summary'],

        // base output directory. If you include %browser% in the path it will be replaced with the karma browser name
        dir: path.join(process.cwd(), process.env.KARMA_COVERAGE_REPORT || 'build/karma-coverage/coverage'),

        'report-config': {
            // all options available at: https://github.com/istanbuljs/istanbuljs/blob/aae256fb8b9a3d19414dcf069c592e88712c32c6/packages/istanbul-reports/lib/html/index.js#L135-L137
            html: {
                // outputs the report in ./coverage/html
                subdir: 'html'
            }
        },

        // Combines coverage information from multiple browsers into one report rather than outputting a report
        // for each browser.
        combineBrowserReports: true,

        // if using webpack and pre-loaders, work around webpack breaking the source path
        fixWebpackSourcePaths: true,

        // Omit files with no statements, no functions and no branches from the report
        skipFilesWithNoCoverage: false,

        verbose: false, // output config used by istanbul for debugging

        thresholds: {
            global: {
                statements: 100,
                branches: 100,
                functions: 100,
                lines: 100
            },
            each: {
                statements: 1,
                lines: 1,
                branches: 1,
                functions: 1
            }
        }
    },

    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: constants.LOG_ERROR,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,


    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - ChromeHeadless
    // - ChromiumHeadless
    // - Firefox
    // - Opera (has to be installed with `npm install karma-opera-launcher`)
    // - Safari (only Mac; has to be installed with `npm install karma-safari-launcher`)
    // - IE (only Windows; has to be installed with `npm install karma-ie-launcher`)
    browsers: ["ChromeHeadless"],

    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 60000,
    browserNoActivityTimeout: 1000000,
    browserDisconnectTimeout: 20000,

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    //singleRun: true,

    plugins: [
        "karma-jasmine",
        "karma-chrome-launcher",
        "karma-coverage",
        'karma-coverage-istanbul-reporter',
        "karma-junit-reporter",
        "karma-spec-reporter",
        "karma-jasmine-html-reporter",
        "karma-sourcemap-loader",
        "karma-webpack"
    ]
};
