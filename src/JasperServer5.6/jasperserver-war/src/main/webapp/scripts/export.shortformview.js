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
 * @author: inesterenko, ztomchenko
 * @version: $Id: export.shortformview.js 47331 2014-07-18 09:13:06Z kklein $
 */

JRS.Export.ShortFormView = (function (Export, jQuery, _, Backbone, TemplateEngine) {
    var isIE = navigator.userAgent.toLowerCase().indexOf("msie") > -1;

    return Backbone.View.extend({

        events:{
            "input input[type='text']":"editFileName",
            "change input[type='checkbox']":"setValue",
            "click .checkBox label":"clickOnCheckbox",
            "click #exportButton":"sendParameters"
        },

        initialize:function () {
            _.bindAll(this);

            this.model.set({everything: false});

            this.fileTemplateId = "exportDataFileTemplate";
            this.optionsTemplateId = "exportOptionsTemplatesShort";
            this.controlsTemplateId = "controlButtonsTemplate";

            this.fileTemplate = TemplateEngine.createTemplate(this.fileTemplateId);
            this.optionsTemplate = TemplateEngine.createTemplate(this.optionsTemplateId);
            this.controlsTemplate = TemplateEngine.createTemplate(this.controlsTemplateId);
        },

        render:function (options) {
            var fileControlHtml = this.fileTemplate({defaultFileName:this.model.get('fileName')});
            var optionsHtml = this.optionsTemplate({
                includeReportJobs:this.model.get("includeReportJobs"),
                includeRepositoryPermissions:this.model.get("includeRepositoryPermissions")
            });

            var controlsHtml = this.controlsTemplate();

            if (options && options.container) {
                this.undelegateEvents();
                this.$el = jQuery(options.container);
                this.el = this.$el[0];
                this.$el.find(".body").append(fileControlHtml + optionsHtml);
                this.$el.find(".footer").prepend(controlsHtml);
                this.delegateEvents();
            } else {
                this.$el.html(fileControlHtml + optionsHtml + controlsHtml);
            }

            isIE && (this.$el.find("input[type='text']").on("propertychange input",this.editFileName));

            return this;
        },

        setValue: function(evt){
            var checkbox = jQuery(evt.target)[0];
            var data = {};

            data[checkbox.id] = checkbox.checked;

            this.model.set(data);
        },

        editFileName:function (evt) {
            var edit = jQuery(evt.target);
            edit.parent().removeClass("error");
            var button = this.$el.find("#exportButton").prop("disabled", false);
            this.model.once("invalid", function (model, error) {
                edit.next().html(error);
                edit.parent().addClass("error");
                button.prop("disabled", true);
            });
            this.model.set("fileName", edit.val(), { validate : true });
        },

        sendParameters:function (evt) {
            if (this.model.isValid() && this.isValid()) {
                this.model.save();
            }
        },

        clickOnCheckbox: function(evt){
            var checkbox = jQuery(evt.target).next();
            if (!checkbox[0].disabled) {
                checkbox[0].checked = !checkbox[0].checked;
                checkbox.trigger("change");
            }
        },

        isValid: function(){
            return !this.$el.find(".error").length;
        },

        prepareToShow: function() {
            this.$el.find("input[type='text']").val(this.model.defaults.fileName).parent().removeClass("error");
            this.$el.find("#exportButton").prop("disabled", false);
            this.$el.find("#includeReportJobs").prop("disabled", !this.model.get("includeReportJobs")).prop("checked", !!this.model.get("includeReportJobs"));
            this.model.set({fileName : this.model.defaults.fileName});
        }

    })

})

    (
        JRS.Export,
        jQuery,
        _,
        Backbone,
        jaspersoft.components.templateEngine
    );