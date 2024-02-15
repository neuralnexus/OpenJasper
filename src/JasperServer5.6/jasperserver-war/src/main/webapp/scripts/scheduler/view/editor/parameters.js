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
 * @version: $Id: parameters.js 47331 2014-07-18 09:13:06Z kklein $
 */

define('scheduler/view/editor/parameters', function(require){

    require('controls.options');
    require('controls.controller');

    // dependencies
    var $ = require('jquery'),
        config = require('jrs.configs'),
        Backbone = require('backbone'),
        parameters = require('text!scheduler/template/parameters.htm');

    _.extend(ControlsBase, config.inputControlsConstants);

    // return backbone view
    return Backbone.View.extend({

        // tab dom element
        el: '#scheduler_editor div.tab[data-tab=parameters]',

        // initialize view
        initialize: function(){
            // private variables
            var t = this;

            // pro version
            if (config.isProVersion){
                var header,
                    i18n = require('bundle!jasperserver_messages'),
                    template = _.template(parameters)

                // render header
                t.header = $(template({
                    i18n: i18n,
                    config: config
                }))

                t.$el.parent().find('.schedule_for').append(t.header);

                // create report options instance
                t.reportOptions = new JRS.Controls.ReportOptions();

                // construct save values dialog
                t.reportOptionsDialog = new OptionsDialog({

                    'button#saveAsBtnSave': function(){
                        var optionName = t.reportOptionsDialog.input.getValue(),
                            selectedData = t.controlsController.getViewModel().get('selection'),
                            overwrite = optionName === t.reportOptionsDialog.optionNameToOverwrite;

                        $.when(t.reportOptions.add(t.reportOptions.optionsUrl || t.reportOptions.url, optionName, selectedData, overwrite)).
                            done(function () {
                                t.reportOptionsDialog.hideWarning();
                                var container = t.reportOptions.getElem().parent();
                                if (container.length == 0) {
                                   container = $('.sub.header');
                                   container.prepend(t.reportOptions.getElem());
                                }
                                t.reportOptionsDialog.hide();
                                delete t.reportOptionsDialog.optionNameToOverwrite;
                            }).fail(function(err){
                                try {
                                    var response = $.parseJSON(err.responseText);
                                    if (response.errorCode === "report.options.dialog.confirm.message"){
                                       !overwrite && (t.reportOptionsDialog.optionNameToOverwrite = optionName);
                                    }
                                    t.reportOptionsDialog.showWarning(response.message);
                                } catch (e) {
                                    // In this scenario security error is handled earlier, in errorHandler, so we can ignore exception here.
                                    // Comment this because it will not work in IE, but can be uncommented for debug purpose.
                                    // console.error("Can't parse server response: %s", "controls.core", err.responseText);
                                }
                            });
                    },

                    'button#saveAsBtnCancel': function(){
                        t.reportOptionsDialog.hide();
                    }

                });

                // handle save options click
                t.header.find('button#save').click(function(){
                    t.reportOptionsDialog.show();
                });
            }

            //
            t.model.on('save', function(model, await){
                if (!t.model.get('source').parameters) return;

                var check = await();
                t.controlsController.validate().then(function(areControlsValid){
                    check(!areControlsValid);
                    if (!areControlsValid) {
                         t.model.trigger('invalid', t, [{
                            field: 'parametersErrorNotifierStub',
                            errorCode: 'report.scheduling.list.state.5'
                         }], {
                            switchToErrors: true
                         });
                    }
                });
            });

            // handle model changes
            t.model.on('change:source', function(model, value){
                // get url for report
                var url = value && value.reportUnitURI;

                // prepare parameters
                var source = t.model.get('source').parameters;

                // check for parameters source
                if (!source) return;

                // go deeper in structure
                source = source.parameterValues;

                for(var item in source) if (source.hasOwnProperty(item))
                    if (source[item] === null) delete source[item];

                if (t.reportOptions)
                    t.reportOptions.url = undefined;

                // create controls controller
                if (!t.controlsController || url !== t.controlsController.reportUri)
                    t.controlsController = new JRS.Controls.Controller({
                        reportUri : url,
                        reportOptionUri: '',
                        preSelectedData: source
                    });

                t.controlsController.fetchControlsStructure(source);
            });


            t.model.on('change:source', function(model, value){
                // get url for report
                var url = value && value.reportUnitURI;

                // if url changed, fetch new data
                if (t.reportOptions && url !== t.reportOptions.url){
                    // fetch new data
                    t.reportOptions.optionsUrl = undefined;
                    t.model.resource('reportOptions', function(err, data){
                        if (err) return;
                        t.reportOptions.optionsUrl = data.reportUri;
                        $.when(t.reportOptions.fetch(data.reportUri || url, '')).done(function(){
                            t.header.prepend(t.reportOptions.getElem());
                            $(document).trigger('viewmodel:selection:changed');
                        });
                    });

                    // 
                    t.model.permission(function(err, permission){
                        var canWrite = !!err || !(permission === 1 || permission === 30);
                        t.header.find('button#save').attr('disabled', canWrite);
                    });

                    // save new url
                    t.reportOptions.url = url;
                }
            });

            // handle values change
            $(document).on('viewmodel:values:changed', function(){
                // update model
                t.model.set('source', {
                    reportUnitURI: t.model.get('source').reportUnitURI,
                    parameters: {
                        parameterValues: t.controlsController.getViewModel().get('selection')
                    }
                }, { validate: false, silent: true });
            });

            // handle selection change
            if (t.reportOptions) $(document).on('viewmodel:selection:changed', function(){
                // update options
                var option = t.reportOptions.find({uri:t.reportOptions.url });
                t.reportOptions.set({selection:option}, true);
            });
        }

    });

});