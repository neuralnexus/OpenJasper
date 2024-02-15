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
 * @author: inesterenko, ztomchenco
 * @version: $Id: import.extendedformview.js 47331 2014-07-18 09:13:06Z kklein $
 */

JRS.Import.ExtendedFormView = (function (importz, jQuery, _, Backbone, State, templateEngine, AjaxUploader) {

    return Backbone.View.extend({

        events:{
            "change #importOptions input[type='checkbox']":"setValue",
            "change #importDataFile input[type='file']":"validateFile",
            "click #importDataFile .checkBox label":"clickOnCheckbox",
            "click #importButton":"performImport"
        },

        initialize:function () {
            _.bindAll(this);
            this.mainTemplateId = "importMainTemplate";
            this.footerTemplateId = "importFooterTemplate";
        },

        render:function (options) {
            var mainTemplate = templateEngine.createTemplate(this.mainTemplateId);
            var footerTemplate = templateEngine.createTemplate(this.footerTemplateId);

            var mainHtml = mainTemplate(this.model.attributes);

            var footerHtml = footerTemplate();

            if (options && options.container) {
                this.undelegateEvents();
                this.$el = jQuery(options.container);
                this.el = this.$el[0];
                this.$el.find(".body").append(mainHtml);
                this.$el.find(".footer").prepend(footerHtml);
                this.delegateEvents();
            } else {
                jQuery(this.el).html(mainHtml + footerHtml);
            }

            this.form = this.$el.find("form")[0];
            this.uploader = new AjaxUploader(this.form, this.setResponseData, importz.configs.TIMEOUT);
            return this;
        },

        changeEnabledState:function (element, disabled) {
            var subList = element.parents(".checkBox").next();
            if (subList.length) {
                subList.first().find('input[type="checkbox"]').attr("disabled", disabled);
            }
        },

        clickOnCheckbox:function (evt) {
            var checkbox = jQuery(evt.target).next();
            if (!checkbox[0].disabled) {
                checkbox[0].checked = !checkbox[0].checked;
                checkbox.trigger("change");
            }
        },

        setValue:function (evt) {
            var checkbox = jQuery(evt.target);
            var value = checkbox[0].checked;
            this.model.set(checkbox[0].id, value);
            this.changeEnabledState(checkbox, !value);
        },

        validateFile:function (evt) {
            var file = jQuery(evt.target);
            if (/\.zip$/.test(file.val())) {
                this.$el.find("#importButton").attr("disabled", false);
                this.valid = true;
                file.parent().removeClass("error");
            } else {
                this.$el.find("#importButton").attr("disabled", true);
                file.parent().addClass("error");
                this.valid = false;
            }
        },

        performImport:function (evt) {
            if (this.isValid()) {
                (new (Backbone.Model.extend({
                    url:"rest_v2/import"
                }))).fetch({
                    success: _.bind(function(){
                        this.model.get("state").reset();
                        this.model.get("state").set({phase:State.INPROGRESS});
                        this.form.submit();
                        this.uploader.startTimeoutLookup(this.setResponseData);
                    }, this),
                    error: this.model.defaultErrorDelegator
                });
            }
        },

        setResponseData:function (data) {
            if (data.errorCode) {
                data.phase = State.FAILED;
                if (importz.i18n[data.errorCode]) {
                    data.message = importz.i18n[data.errorCode];
                }
            }
            var state = this.model.get("state");
            state.reset();
            state.set(data);
        },

        isValid:function () {
            return this.valid;
        }
    })

})(
    JRS.Import,
    jQuery,
    _,
    Backbone,
    jaspersoft.components.State,
    jaspersoft.components.templateEngine,
    jaspersoft.components.AjaxUploader
);