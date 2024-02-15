/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: output.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

define('scheduler/view/editor/output', function (require) {

    var $ = require('jquery'),
        _ = require('underscore'),
        Backbone = require('backbone'),
        config = require('jrs.configs'),
        i18n = require('bundle!jasperserver_messages'),
        picker = require('components.pickers'),
        resource = require('resource.base');

    return Backbone.View.extend({

        // tab dom element
        el: '#scheduler_editor .tab[data-tab=output]',

        events: {
            "click .ftp-test": "testFTPConnection"
        },

        // initialize view
        initialize: function () {
            // save link to context
            var t = this;

            // adjust this checkbox into proper state depending on the server-side variable
            t.$("[name=outputToHostFileSystem]").attr("disabled", config.enableSaveToHostFS === "true" ? false : "disabled");

            //
            t.map = [
                { attr: 'baseOutputFilename', control: 'baseOutputFilename' },
                { attr: 'repositoryDestination/outputDescription', control: 'outputDescription' },
                { attr: 'outputTimeZone', control: 'timeZone' },
                { attr: 'outputLocale', control: 'outputLocale' },
                { attr: 'outputFormats/outputFormat', control: 'outputFormats' },
                { attr: 'repositoryDestination/overwriteFiles', control: 'overwriteFiles'},
                { attr: 'repositoryDestination/sequentialFilenames', control: 'sequentialFilenames'},
                { attr: 'repositoryDestination/timestampPattern', control: 'timestampPattern' },
                { attr: 'repositoryDestination', control: 'timestampPattern',
                    depends: 'repositoryDestination/sequentialFilenames' },
                { attr: 'repositoryDestination/saveToRepository', control: 'outputToRepository' },
                { attr: 'repositoryDestination', control: 'outputRepository, outputRepositoryButton',
                    depends: 'repositoryDestination/saveToRepository' },

                { attr: 'repositoryDestination/folderURI', control: 'outputRepository' },
                { attr: 'repositoryDestination', control: 'outputHostFileSystem',
                    depends: 'repositoryDestination/outputLocalFolder', disabled: !(config.enableSaveToHostFS)},
                { attr: 'repositoryDestination/outputLocalFolder', control: 'outputToHostFileSystem',
                    getter: function (value){
                        if (false === value) return null;
                        return '';
                    },
                    setter: function(value){
                        return value === '' || !!value;
                    }},
                { attr: 'repositoryDestination/outputLocalFolder', control: 'outputHostFileSystem' },
                { attr: 'repositoryDestination/outputFTPInfo/enabled', control: 'outputToFTPServer',
                    getter: function (value){
                        if (value === false) {
                            // clear the errors in this block in case when this block has some errors and user decided to disable it
                            t.$("#ftpServerOutput").find('.error').removeClass('error');
                        }
                        return value;
                    }
                },
                { attr: 'repositoryDestination/outputFTPInfo',
                    control: 'ftpAddress, ftpDirectory, ftpUsername, ftpPassword, ftpTestButton, ftpPort, useFTPS',
                    depends: 'repositoryDestination/outputFTPInfo/enabled' },
                { attr: 'repositoryDestination/outputFTPInfo/serverName', control: 'ftpAddress' },
                { attr: 'repositoryDestination/outputFTPInfo/folderPath', control: 'ftpDirectory' },
                { attr: 'repositoryDestination/outputFTPInfo/userName', control: 'ftpUsername',
                    setter: function (value){
                        if (value=="anonymous")
                            return null;
                        return value;
                    }},
                { attr: 'repositoryDestination/outputFTPInfo/password', control: 'ftpPassword' },
                { attr: 'repositoryDestination/outputFTPInfo/port', control: 'ftpPort' }
            ];

            //
            _.each(t.map, function (data) {
                // get control by name
                data.control = data.control.split(',');
                data.control = data.control.map(function (a) {
                    return '[name=' + a + ']'
                });
                data.control = t.$(data.control.join(','));

                //
                if (data.attr) data.attr = data.attr.split('/');

                if (data.depends) {
                    if (data.depends[0] === '!') {
                        data.invert = true;
                        data.depends = data.depends.substr(1);
                    }
                }

                if (data.depends && !data.attr) {
                    data.depends = t.$('[name=' + data.depends + ']');

                    var change = function () {
                        var val = data.setter
                            ? data.setter(data.depends)
                            : data.depends.val();

                        data.control.attr('disabled', data.disabled ? "disabled" : (data.invert ? !!val : !val));
                    };

                    data.depends.on('change', change);

                    return change();
                }

                // handle model change and update element
                t.model.on('change:' + data.attr[0], function (model, value) {
                    value = model.value(data.attr.join('/'));

                    if (data.setter)
                        value = data.setter(value);

                    if (data.depends) {
                        var val = model.value(data.depends);

                        if (data.setter)
                            val = data.setter(val);
                        else if (val === '') val = true;

                        return data.control.attr('disabled', data.disabled ? "disabled" : (data.invert ? !!val : !val));
                    }

                    if (data.control.is('[type=checkbox]') && data.control.length === 1)
                        data.control.prop('checked', value);
                    else if (data.control.is('[type=radio]'))
                        data.control.filter('[value=' + value + ']').prop('checked', true);
                    else
                        data.control.val(value);
                });

                // handle element change and update model
                if (data.control.length && !data.depends)
                    data.control.on('change', function () {
                        var key, value, update, target;

                        value = data.control.is('[type=checkbox]')
                            ? data.control.filter(':checked').map(function () {
                            return $(this).val()
                        }).get()
                            : data.control.val();

                        if (data.control.is('[type=checkbox]') && data.control.length === 1)
                            value = !!value[0];

                        if (data.control.is('[type=radio]'))
                            value = data.control.filter(':checked').val();

                        if (data.getter)
                            value = data.getter(value, _.clone(t.model.value(data.attr.join('/'))));

                        if (data.attr.length > 1) {
                            target = update = _.clone(t.model.get(data.attr[0]));

                            for (var i = 1, l = data.attr.length - 1; i < l; i++) {
                                key = data.attr[i];
                                target[key] = target[key]
                                    ? _.clone(target[key])
                                    : {};
                                target = target[key];
                            }

                            target[data.attr[data.attr.length - 1]] = value;
                        }

                        t.model.update(data.attr[0], update || value);
                    });
            });

            t.$("[name=useFTPS]").on("click", function(){
                var type = "TYPE_FTP", port = "21";
                if ($(this).is(":checked")) {
                    type = "TYPE_FTPS";
                    port = "990";
                }

                var m = $.extend(true, {}, t.model.get("repositoryDestination"));
                m = m || {};
                m.outputFTPInfo = m.outputFTPInfo || {};
                m.outputFTPInfo.type = type;
                m.outputFTPInfo.port = port;

                t.model.update('repositoryDestination', m);
            });

            // check write permissions for output folder
            t.model.on('save', function(model, await){
                if (!t.model.get('repositoryDestination').saveToRepository) return;

                var check = await();
                t.model.permission(function(err, permission){
                    if (err || !(permission === 1 || permission === 30)) {

                        try {
                            err = JSON.parse(err.responseText);
                        } catch(e) {
                            err = {
                                errorCode: ""
                            };
                        }


                        // by default, we think what we can't write to the destination folder
                        var ourErrorCode = 'error.report.job.output.folder.notwriteable';
                        if (err.errorCode === "resource.not.found") {
                            ourErrorCode = 'error.report.job.report.inexistent.output';
                        }

                        t.model.trigger('error', t.model, [{
                            field: 'outputRepository',
                            errorCode: ourErrorCode,
                            errorArguments: [t.model.get('repositoryDestination').folderURI]
                        }], { switchToErrors: true });
                    }
                    else {
                        check();
                    }
                });
            });

            t.model.on('change:repositoryDestination', function (model, value) {
                var rp = model.get('repositoryDestination');
                t.$("[name=ftpPort]").val(rp.outputFTPInfo.port);
                t.$("[name=useFTPS]").prop("checked", rp.outputFTPInfo.type === "TYPE_FTPS");
            });

            //bind 'browse' button with inputs
            new picker.FileSelector({
                treeId: 'repoTree',
                providerId: 'repositoryTreeFoldersProvider',
                treeOptions: {
                    organizationId: "organizationId",
                    publicFolderUri: "publicFolderUri"
                },
                uriTextboxId: "outputRepository",
                browseButtonId: 'browser_button',
                title: i18n['report.scheduling.repository.content'],
                onOk: function () {
                    outputRepository.setValue($("outputRepository").val());
                    $('#outputRepository').trigger('change')
                }
            });
        },

        testFTPConnection: function () {
            $("#ftpTestButton").addClass('disabled');
            this.model.testFTPConnection(function () {
                $("#ftpTestButton").removeClass('disabled');
            });
        }
    });

})
;