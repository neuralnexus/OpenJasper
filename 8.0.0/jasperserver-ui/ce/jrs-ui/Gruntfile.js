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

const glob = require('glob');
const merge = require('merge');
const loadGruntTasksOriginal = require('load-grunt-tasks');

function loadGruntTasks(grunt) {
    const loadFrom = `${process.cwd()}/tasks`;

    if (!grunt.file.isDir(loadFrom)) {
        return;
    }

    try {
        grunt.loadTasks(loadFrom);
    } catch (ex) {
        grunt.fail.fatal("Cannot load tasks from '" + loadFrom + "' directory: " + ex);
    }
}

function extendGruntConfig(grunt) {
    let config = {};
    const path = `${process.cwd()}/tasks/options/`;

    try {
        glob.sync('*.js', {
            cwd: path
        }).forEach(function (option) {
            const parts = option.replace(/\.js$/, '').split("-");

            const tasksConfig = parts.reduce((memo, part, index) => {
                if (memo.currentLevel === null) {
                    memo.currentLevel = memo.result;
                }

                memo.currentLevel[part] = {};

                let optionContent;

                if (index === parts.length - 1) {
                    optionContent = require(path + option);

                    if (typeof optionContent === 'function') {
                        optionContent = optionContent(grunt, config);
                    }

                    memo.currentLevel[part] = optionContent;
                }

                return {
                    result: memo.result,
                    currentLevel: memo.currentLevel[part]
                };
            }, {
                result: {},
                currentLevel: null
            }).result;

            config = merge.recursive(true, config, tasksConfig);
        });
    } catch (ex) {
        grunt.log.writeln("Cannot read grunt config options: " + ex);
    }

    return config;
}

module.exports = function (grunt) {
    loadGruntTasksOriginal(grunt);

    loadGruntTasks(grunt);

    const gruntConfig = extendGruntConfig(grunt);

    grunt.initConfig(gruntConfig);
};
