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
 * @version: $Id: create.report.js 47331 2014-07-18 09:13:06Z kklein $
 */

/**
 * Module for creating report from Adhoc Data View
 */
JRS.CreateReport = (function(jQuery, jrsConfigs) {
    var advSelDialog = null;

    //this module needs some scripts from PRO in order to run
    getDialog = function(advUri) {
        if(!advSelDialog) {
            advSelDialog = new JRS.GeneratorPropertiesDialog({
                advUri: advUri,
                okHandler: JRS.CreateReport.showGeneratedReport,
                messages: jrsConfigs
            });
        }
        return advSelDialog;
    };

    return {
        /**
         * Redirects browser page that generates report from advUri Adhoc Data View and displays generated report
         * @param advUri
         */
        showGeneratedReport: function(data) {
            var form = jQuery("#reportGeneratorForm");
            form.find("input[name=advUri]").val(data.sourceURI);
            form.find("input[name=template]").val(data.template || "");
            form.find("input[name=generator]").val(data.generator || "");
            form.submit();
        },

        selectADV: function() {
            getDialog().show();
        },

        selectGenerator: function(advUri) {
            getDialog(advUri).show();
        }
    };
})(jQuery, __jrsConfigs__);

jQuery(function() {
    //make sure ajaxbuffer is available
    if(jQuery("#ajaxbuffer").length == 0) {
        jQuery("body").append(jQuery('<div id="ajaxbuffer" style="display:none"></div>'));
    }
});
