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
 * @version: $Id: editor.js 47331 2014-07-18 09:13:06Z kklein $
 */

define('scheduler/view/editor', function(require){

    var $ = require('jquery'),
        Backbone = require('backbone'),
        jobModel = require('scheduler/model/job'),
        i18n = require('bundle!jasperserver_messages'),
        editorScheduleView = require('scheduler/view/editor/schedule'),
        editorParametersView = require('scheduler/view/editor/parameters'),
        editorOutputView = require('scheduler/view/editor/output'),
        editorNotificationsView = require('scheduler/view/editor/notifications'),
        jobUtil = require("scheduler/util/jobUtil"),
        _ = require('underscore');

    return Backbone.View.extend({

        // editor page
        el: '#scheduler_editor',
        editMode: false, // When editing we need to have different validations - for example we should skip the isPastDate

        // initialize view
        initialize: function(options){
            // private variables
            var t = this, params;
            var saveAsFormValidation;

            // initialize view app
            options.app && (t.app = options.app);

            // prepare model
            t.model = new jobModel();

            // get tab headers and bodies
            t.tabs = t.$('.tabs li');
            t.pages = t.$('div.tab');

            // prepare params
            params = { app: t.app, model: t.model };

            // create views for each tab
            t.tabs.schedule = new editorScheduleView(params);
            t.tabs.parameters = new editorParametersView(params);
            t.tabs.output = new editorOutputView(params);
            t.tabs.notifications = new editorNotificationsView(params);

            // editor title element
            t.header = t.$('.schedule_for');
            t.title = t.$('.header .title');
            t.location = t.header.find('.path');

            // configure Save As dialog
            t.dialog = $('#saveAs');

            t.dialog.find('#saveAsInputName').attr('maxlength', 100);

            t.dialog.find('.groupBox').remove();

            t.dialog.find('[for=saveAsInputName] > span:first').text(i18n["report.scheduling.job.edit.specify.scheduledjobname.required"]);
            t.dialog.find('[for=saveAsInputDescription] > span:first').text(i18n["report.scheduling.job.edit.specify.schedule.description"]);

            // handle tab changes
            t.tabs.on('touchend mouseup', function(tab, page){

                // forbid changing tab if model is not valid
                if (!t.model.isValid(t.editMode)) {
                    t.select('error');
                    return false;
                }

                // select needed tab
                t.select($(this).data('tab'));

                // stop bubbling
                return false;
            });

            // handle any type of validation started
            t.model.on('clearAllErrors', function(){
                t.$('.error').removeClass('error');
            });

            // model is valid, show success messages
            t.model.on('valid', function(model, messages){
                _.each(messages, function(message){
                    t.message('success', message);
                });
            });

            // model is invalid, show error messages
            t.model.on('invalid error', function(model, errors, options){
                _.each(errors, function(error){
                    t.message('error', error);
                });

                if (options && options.switchToErrors) {
                    t.select('error');
                }
            });

            // disable/enable parameters due to model state
            t.model.on('change:source', function(model, value){
                var empty;

                if (value.parameters){
                    // check parameters hash length
                    empty = _.keys(value.parameters.parameterValues).length === 1;

                    // parameters are empty if length is 1 and it is timezone
                    empty &= !!value.parameters.parameterValues.REPORT_TIME_ZONE;
                }

                // disable tab if needed
                t.state('parameters', value.parameters && !empty);
                t.location.text(value.reportUnitURI);
            });

            // saving handler
            t.save = {
				validate: false,
                success: _.bind(t.back, t),
                error: function(model, response){
                    try {
                        response = JSON.parse(response.responseText);
                    } catch(e) {
                        return false;
                    }
                    if (response.message && response.message.indexOf("will never fire") !== -1) {
                        t.model.trigger('error', t.model, [{
                            field: "triggerWillNeverFire",
                            errorCode: "error.report.job.trigger.no.fire"
                        }]);
                    } else {
                        t.model.trigger('error', t.model, response.error);
                    }
                    t.select('error');
                }
            };

            // handle save button
            t.$('[data-action=save]').click(function(){
                t.submitOrSaveAction();
            });

            // handle submit button
            t.$('[data-action=submit]').click(function(){
                // update model from dialog inputs
                t.model.set('label', 'Immediate Execution');
                t.submitOrSaveAction();
            });

            // handle save action from 'save as' dialog
            t.dialog.on('click', '#saveAsBtnSave', function() {

                if (t.saveAsFormValidation === false) {
                    // validation is not passes -- wait until it will pass
                    return;
                }

                // update model from dialog inputs
                t.model.set('label', t.dialog.find('#saveAsInputName').val());
                t.model.set('description', t.dialog.find('#saveAsInputDescription').val());

                // save model
                t.model.save({}, t.save);
            });

            // handle save button
            t.$('#cancel').click(_.bind(t.back, t));
        },

        back: function() {
            jobUtil.back(('fast' === this.option && this.app._runNowClick === false) ? jobUtil.SCHEDULER_BACK_COOKIE_NAME : jobUtil.SCHEDULER_LIST_BACK_COOKIE_NAME);
        },

        submitOrSaveAction: function(){
            var t = this;
        
            if (!t.model.isValid(t.editMode)) {
                t.select('error');
                return;
            }

            if (t.model.id || t.model.get('label')) {
                // set the name and the description if we have them
                t.dialog.find('#saveAsInputName').val(t.model.get('label'));
                t.dialog.find('#saveAsInputDescription').val(t.model.get('description'));

            } else {
                // cleanup 'save as' inputs
                t.dialog.find('#saveAsInputName').val('');
                t.dialog.find('#saveAsInputDescription').val('');
            }

            // handle creating new job

            t.saveAsFormValidation = false; // set to invalid state because there is an required field
            dialogs.popupConfirm.show(t.dialog.get(0), false, {
                okButtonSelector: '#saveAsBtnSave',
                cancelButtonSelector: '#saveAsBtnCancel',
                validateFunc: function(){
                    // check the name
                    var name = t.dialog.find('#saveAsInputName').val();
                    t.dialog.find('#saveAsInputName').parent().removeClass("error");
                    t.dialog.find('#saveAsInputName').next().text("");
                    if (name == "") {
                        t.dialog.find('#saveAsInputName').parent().addClass("error");
                        t.dialog.find('#saveAsInputName').next().text(i18n["report.scheduling.job.edit.specify.scheduledjobname"]);
                        t.saveAsFormValidation = false;
                        return false;
                    }
                    t.saveAsFormValidation = true;
                    return true;
                }
            });

            // set focus to first input in dialog
            t.dialog.find('input:first').focus();
        },

        // select tab
        select: function(name){
            // get name of first tab with error
            if ('error' === name)
                name = this.$('.error').first().parents('.tab').data('tab');

            // break if no name provided
            if (!name) return;

            if (this.tabs.current){
                // break if tab already selected
                if (name === this.tabs.current.data('tab')) return;

                // look for errors on current tab
                var errors = this.pages.current.find('input.error, select.error');

                // don't switch tabs if error
                if (errors.length) return;
            }

            // get tab header and body
            var tab = this.tabs.filter('[data-tab=' + name+ ']'),
                page = this.pages.filter('[data-tab=' + name+ ']');

            // ignore disabled tabs
            if (tab.hasClass('disabled') || tab.attr('disabled')) return;

            // update url to show tab opened
            if (name) this.app.router.navigate(
                location.hash.substr(1).replace(/(\$[^@\/]*)?(@.*)?$/, '$1@' + name),
                // don't store tab switching in browser history
                { replace: true }
            );

            this.header.attr('data-tab-current', name);

            // make needed header selected
            tab.siblings().removeClass('selected');
            this.tabs.current = tab.addClass('selected');

            // show proper tab body
            page.siblings('.tab').addClass('hidden');
            this.pages.current = page.removeClass('hidden');
        },

        // show validation message
        message: function(type, data){
            // skip errors we can't handle
            if (!data.field) return;

            // it happened what server sends an error message with different field names.
            // correct it !
            if (data.field === "contentRepositoryDestination.folderURI") {
                data.field = "contentRepositoryDestination.outputRepository";
            }

            // remove prefixes
            data.field = data.field.replace('trigger.', '');
            data.field = data.field.replace('mailNotification.', '');
            data.field = data.field.replace('contentRepositoryDestination.', '');

            // get controls list
            var message = this.$('.warning[data-field=' + data.field + ']');

            // set the "error" class into parent, because this is requirement of JRS CSS structure
            message.parent().addClass("error");

            // norw, adjust class type of the message field
            message.removeClass("success").removeClass("error"); // remove all types of notifications
            message.addClass(type); // now, add the class name which represents the current notification

            if (data.errorCode) {
                // get error text from i18n
                var text = i18n[data.errorCode];
                if (!text) return;

                // insert error arguments
                if (data.errorArguments)
                    for(var i=0, l=data.errorArguments.length; i<l; i++)
                        text = text.replace('{' + i + '}', data.errorArguments[i]);

                // show warning box
                message.text(text);
            }
        },

        // set tab state
        state: function(name, state){
            // get tab
            var tab = this.tabs.filter('[data-tab=' + name + ']');

            // add disabled attr
            tab.attr('disabled', !state);

            // toggle class
            tab.toggleClass('disabled', !state);

            // select nearest enabled tab
            if (!state && tab.hasClass('selected'))
                this.select(tab.nextAll(':not(.disabled):first').data('tab'));
        },

        // edit job by job id
        edit: function(id, tab){
            this.editMode = true;
            // private variables
            var t = this;

            // open editor page
            t.app.page(t);

            // hide submit button
            t.$('#submit').addClass('hidden');

            // enable schedule tab
            t.state('schedule', true);

            // select first tab
            t.select(tab || 'schedule');

            // set model id
            t.model.id = id;

            // clear title
            t.title.text('');
            t.$('#save').removeClass('hidden');

            // fetch model from server
            t.model.fetch({
                success: function(){
                    // set proper title
                    t.title.text(t.model.get('label'));
                }
            });
        },

        // start creating a new job
        create: function(uri, tab, option){
            var t = this;
            t.editMode = false;
            t.option = option;
            // set title of editor
            t.title.text(i18n["report.scheduling.new.schedule"]);

            // open editor page
            t.app.page(t);

            // handle schedule tab state
            t.state('schedule', 'fast' !== t.option);

            // select first tab
            t.select(tab || 'schedule');

            // create new model from uri
            t.model.createFromUri(uri);

            // load model parameters
            t.model.parameters(this.parentReportURI || false, function(err, data){
                if (data && data.inputControl){
                    var parameters = {},
                        controls = data.inputControl;

                    for(var i=0, l=controls.length; i<l; i++)
                        parameters[controls[i].id] = null;

                    t.model.update('source', { parameters: { parameterValues: parameters }});
                }
            });

            // change buttons state
            t.$('.footer #save')['fast' === t.option ? 'addClass' : 'removeClass']('hidden');
            t.$('.footer #submit')['fast' !== t.option ? 'addClass' : 'removeClass']('hidden');
        }
    });

});