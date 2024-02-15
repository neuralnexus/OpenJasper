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
 * @version: $Id: require.config.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

requirejs.config({
    baseUrl: "scripts",

    enforceDefine:true, //allow to catch script load errors in IE

	config: {
		"moment": {
			noGlobal: true
        },

        logger : {
            enabled : true,
            level : "error",
            appenders: ["console"]
		}
	},

	//Define dependencies between jasperserver script files

    paths:{

        // 3rd party libs
        "jquery": "lib/jquery-1.11.0",
        "prototype":"lib/prototype-1.7.1-patched",
        "effects":"lib/effects-1.9.0",
        "builder":"lib/builder-1.9.0",
        "dragdrop":"lib/dragdrop-1.9.0-patched",
        "dragdrop.extra": "lib/dragdropextra-0.2-patched",
        "touchcontroller":"lib/touch.controller",
        "iscroll":"lib/iscroll-4.1.7",
		"async":"lib/async-0.1.1",
        "lodash": "lib/lodash-1.1.1",
		"json2": "lib/json2",
		"json3": "lib/json3-3.2.5",
		"mustache": "lib/mustache-0.4.0",
		"underscore": "common/config/lodashTemplateSettings",
        "underscore.string": "lib/underscore.string-2.3.0",
        "backbone": "common/config/backboneSettings",
        "xregexp": "lib/xregexp.unicode.min-2.0.0",
        "moment": "lib/moment-2.4.0",
        "backbone.validation": "lib/backbone-validation-amd-0.9.1",
        "dateFormatter": "lib/dateFormatter-patched",
        "tv4": "common/config/tv4Settings",

        "xdm": "lib/easyXDM-2.4.19-patched",

        //aliases for plugins
        "bundle" : "common/plugin/bundle",
        "settings" : "common/plugin/settings",
		"text" : "common/plugin/text",
        "requirejs.plugin.text" : "lib/requirejs-text-plugin-2.0.9",
        "domReady" : "lib/requirejs-domReady-2.0.1",
        "csslink": "lib/requirejs-csslink-plugin",

        //aliases for components
		"calendar2": "common/component/calendar2/calendar2",
		"calendar2Folder": "common/component/calendar2", // used to avoid problems with double /calendar2/calendar2/ with folder name
        "export.app": "export.app",
        "components.toolbar" : "components.toolbarButtons.events",
        "components.list" : "list.base",
        "components.dynamicTree" : "dynamicTree.treesupport",
        "component.repository.search" : "repository.search.actions",
        "report.view": "report.view.runtime",

        // jQuery
        "jquery.urldecoder" : "lib/jquery/js/jquery.urldecoder-1.0-patched",
        "jquery.jcryption" : "lib/jquery/js/jquery.jcryption",
        "jquery.jcryption.extensions" : "lib/jquery/js/jquery.jcryption.extensions",
        "jquery.doubletap" : "lib/jquery/js/jquery.doubletap",

        // jQueryUI
        "jquery.ui" : "lib/jquery/ui/jquery-ui-1.10.4.custom-patched",
        "jquery.timepicker" : "lib/jquery/ui/jquery.timepicker.extensions",
        "jquery.timepicker.addon" : "lib/jquery/ui/jquery-ui-timepicker-addon-patched",
        "jquery.datepicker.extensions" : "lib/jquery/ui/jquery.datepicker.extensions",
        "jquery.ui.mouse.touch": "lib/jquery/ui/jquery.ui.touch-punch.min",

        "wcf.scroll" : "../wcf/scroller",
        "datepicker.i18n.en" : "lib/jquery/ui/i18n/jquery.ui.datepicker-en",
        "datepicker.i18n.de" : "lib/jquery/ui/i18n/jquery.ui.datepicker-de",
        "datepicker.i18n.es" : "lib/jquery/ui/i18n/jquery.ui.datepicker-es",
        "datepicker.i18n.fr" : "lib/jquery/ui/i18n/jquery.ui.datepicker-fr",
        "datepicker.i18n.it" : "lib/jquery/ui/i18n/jquery.ui.datepicker-it",
        "datepicker.i18n.ja" : "lib/jquery/ui/i18n/jquery.ui.datepicker-ja",
        "datepicker.i18n.ro" : "lib/jquery/ui/i18n/jquery.ui.datepicker-ro",
        "datepicker.i18n.zh-CN" : "lib/jquery/ui/i18n/jquery.ui.datepicker-zh-CN",
        "datepicker.i18n.zh-TW" : "lib/jquery/ui/i18n/jquery.ui.datepicker-zh-TW",

//      Dynamic
        "csrf.guard": "../JavaScriptServlet?noext",
        "report.global": "../reportresource?resource=net/sf/jasperreports/web/servlets/resources/jasperreports-global.js",
        "ReportRequireJsConfig" : "../getRequirejsConfig.html?noext",

        // Logging
        "logger": "common/logging/logger"
    },

    shim:{

        "underscore.string": {
            deps :["underscore"],
            exports: "_"
        },

        "lib/backbone-1.1.0": {
            deps :["underscore", "json3"],
            exports:'Backbone'
        },

        "mustache":{
            exports:'Mustache'
        },

        "json2":{
            exports:"JSON"
        },

        "json3":{
            exports:"JSON"
        },

        "prototype":{
            exports:"$"
        },

        "builder" : {
            deps: ["prototype"],
            exports: "Builder"
        },

        "effects":{
            deps:["prototype"],
            exports:"Effect"
        },

        "dragdrop": {
            deps: ["prototype", "effects"],
            exports: "Draggable"  // workaround for IE 'enforceDefine'
        },

        "dragdrop.extra": {
            deps: ["dragdrop", "jquery"],
            exports: "Draggable"
        },

        "touchcontroller" : {
            deps: ["jquery"],
            exports: "TouchController"
        },

        "iscroll" : {
            exports: "iScroll"
        },

        "xregexp": {
            exports: "XRegExp"
        },

        "jpivot.jaPro": {
           deps: ["prototype", "utils.common"],
           exports: "bWidth"
        },

        "wcf.scroll": {
            exports: "document"
        },

        //JasperServer Namespaces support

        "namespace":{
            deps: ["jquery.timepicker"],
            exports:"jaspersoft"
        },

        //CE modules

		"csrf.guard": {
            deps: ["core.ajax"],
            exports: "window"
        },

        "ReportRequireJsConfig": {
            exports: "window"
        },

        "utils.common":{
            deps:["prototype", "jquery", "underscore"],
            exports:"jQuery"  // workaround for IE 'enforceDefine'
        },

        "utils.animation":{
            deps:["prototype", "effects"],
            exports:"jQuery" // workaround for IE 'enforceDefine'
        },

        "jquery.ui":{
            deps:["jquery"],
            exports: "jQuery"
        },

        "jquery.timepicker.addon":{
            deps:["jquery", "jquery.datepicker.extensions"],
            exports: "jQuery"
        },

        "jquery.urldecoder":{
            deps:["jquery"],
            exports: "jQuery"
        },

        "tools.truncator":{
            deps:["prototype"],
            exports:"jQuery" // workaround for IE 'enforceDefine'
        },

        "tools.drag": {
            deps: ["jquery", "prototype"],
            exports: "Dragger"
        },

        "actionModel.modelGenerator": {
            deps: ["prototype", "utils.common", "core.events.bis"],
            exports: "actionModel"
        },

        "fakeActionModel.primaryNavigation":{
            exports: "primaryNavModule"
        },

        "actionModel.primaryNavigation": {
            deps: ["actionModel.modelGenerator"],
            exports: "primaryNavModule"
        },

        "core.layout":{
            deps:["jquery", "prototype", "utils.common", "dragdrop.extra", "tools.truncator", "iscroll", "components.webHelp"],
            exports:"layoutModule"
        },

        "home": {
            deps: ["prototype", "components.webHelp"],
            exports: "home"
        },

        "ajax.mock": {
            deps: ["jquery"],
            exports: "fakeResponce"
        },

        "core.ajax":{
            //TODO: it has dependency to 'jive' object in jasperreports-global.js (JasperReport)
            deps:["jquery", "prototype", "utils.common", "builder", "namespace"],
            exports:"ajax"
        },

        "core.accessibility": {
            deps:["prototype", "components.list", "actionModel.modelGenerator", "core.events.bis"],
            exports: "accessibilityModule"
        },

        "core.events.bis": {
            //use primary navigation as dependency !! circle dependency !!
            deps: ["jquery", "prototype" , "utils.common", "core.layout", "components.tooltip"],
            exports: "buttonManager"
        },

        "core.key.events": {
            deps: ["jquery", "prototype" , "utils.common", "core.layout"],
            exports: "keyManager"
        },

        "error.system": {
            deps: ["jquery", "core.layout", "utils.common"],
            exports: "systemError"
        },

        "components.templateengine":{
            deps:["namespace", "jquery", "underscore", "mustache"],
            exports:"jaspersoft.components.templateEngine"
        },

        "components.ajaxdownloader":{
            deps:["namespace", "jquery", "underscore", "backbone"],
            exports:"jaspersoft.components.AjaxDownloader"
        },

        "components.ajaxuploader":{
            deps:["namespace", "jquery", "underscore", "components.templateengine"],
            exports:"jaspersoft.components.AjaxUploader"
        },

        "components.authoritymodel" : {
            deps:["namespace", "jquery", "underscore", "backbone", "components.templateengine"],
            exports: "jaspersoft.components.AuthorityModel"
        },

        "components.authoritypickerview" : {
            deps:["namespace", "jquery", "underscore", "backbone", "components.templateengine"],
            exports: "jaspersoft.components.AuthorityPickerView"
        },

        "components.dialogs":{
            deps: ["jquery", "prototype", "underscore", "utils.common", "utils.animation", "core.layout"],
            exports:"dialogs"
        },

        "components.dialog":{
            deps: ["jquery", "underscore", "components.templateengine", "components.dialogs", "backbone"],
            exports:"jaspersoft.components.Dialog"
        },

        "components.dependent.dialog": {
            deps: ["prototype", "components.dialogs", "jquery", "components.list"],
            exports: "dialogs.dependentResources"
        },

        "components.list": {
            deps: ["jquery", "prototype", "components.layout", "touchcontroller", "utils.common", "dragdrop.extra", "core.events.bis"],
            exports: "dynamicList"
        },

        "components.layout": {
            deps: ["jquery", "underscore", "components.dialog", "components.systemnotificationview"],
            exports: "jaspersoft.components.Layout"
        },

        "components.searchBox": {
            deps: ["prototype", "utils.common", "core.events.bis"],
            exports: "SearchBox"
        },

        "components.servererrorsbackbonetrait":{
            deps:["namespace", "jquery", "underscore"],
            exports:"jaspersoft.components.ServerErrorsBackboneTrait"
        },

        "components.notificationviewtrait":{
            deps:["namespace", "jquery", "underscore", "backbone"],
            exports:"jaspersoft.components.NotificationViewTrait"
        },

        "components.statecontrollertrait":{
            deps:["namespace", "jquery", "underscore", "backbone", "components.state"],
            exports:"jaspersoft.components.StateControllerTrait"
        },

        "components.state":{
            deps:["namespace", "jquery", "underscore", "backbone", "components.servererrorsbackbonetrait"],
            exports:"jaspersoft.components.State"
        },

        "components.stateview":{
            deps:["namespace", "jquery", "underscore", "components.utils", "components.state"],
            exports:"jaspersoft.components.StateView"
        },

        "components.notificationview":{
            deps:["namespace", "jquery", "underscore", "components.notificationviewtrait"],
            exports:"jaspersoft.components.NotificationView"
        },

        "components.systemnotificationview":{
            deps:["namespace", "jquery", "underscore", "components.dialogs", "components.notificationviewtrait"],
            exports:"jaspersoft.components.SystemNotificationView"
        },

        // should not be used directly, load 'components.toolbar' instead
        "components.toolbarButtons" : {
            deps: ["jquery", "prototype"],
            exports : "toolbarButtonModule"
        },

        "messages/list/messageList": {
            deps: ["prototype", "components.list", "components.toolbar", "core.layout"],
            exports: "messageListModule"
        },

        "messages/details/messageDetails": {
            deps: ["prototype", "components.toolbar", "core.layout"],
            exports: "messageDetailModule"
        },

        "components.toolbar" : {
            deps: ["jquery", "prototype", "utils.common", "components.toolbarButtons"],
            exports : "toolbarButtonModule"
        },

        "components.tooltip" : {
            deps: ["jquery", "prototype", "utils.common", "core.layout"],
            exports : "JSTooltip"
        },

        "components.dynamicTree" : {
            deps: ["prototype", "dynamicTree.tree", "dynamicTree.treenode", "dynamicTree.events", "core.ajax"],
            exports: "dynamicTree"
        },

        "components.utils" : {
            deps: ["jquery", "underscore", "mustache", "components.dialogs", "core.ajax"],
            exports: "jaspersoft.components.utils"
        },

        "heartbeat": {
            deps: ["jquery"],
            exports: "checkHeartBeat"
        },

        "components.heartbeat": {
            deps: ["prototype", "core.ajax"],
            exports: "heartbeat"
        },

        "components.customTooltip": {
            deps: [],
            exports: "customTooltip"
        },

        "components.pickers": {
            deps: ["utils.common", "components.dialogs", "core.layout", "core.events.bis", "prototype", "jquery", "dynamicTree.utils"],
            exports: "picker"
        },

        "controls.core":{
            deps:["jquery", "underscore", "mustache" , "components.dialogs", "namespace", "controls.logging"],
            exports:"JRS.Controls"
        },

        "localContext": {
            exports: "window"
        },

        "controls.dataconverter":{
            deps:["underscore", "controls.core"],
            exports:"JRS.Controls"
        },

        "controls.datatransfer":{
            deps:["json3", "jquery", "controls.core", "backbone", "controls.dataconverter"],
            exports:"JRS.Controls"
        },

        "controls.basecontrol":{
            deps:["jquery", "underscore", "controls.core"],
            exports:"JRS.Controls"
        },

        "controls.base":{
            deps:["jquery", "underscore", "utils.common"],
            exports: "ControlsBase"
        },

        "repository.search.globalSearchBoxInit" : {
            deps: ["prototype", "actionModel.primaryNavigation", "components.searchBox"],
            exports: "globalSearchBox"
        },

        "attributes.model":{
            // TODO ZT use separate template engine instead of controls.core's in production
            deps: ["namespace", "underscore", "backbone", "components.templateengine", "controls.core"],
            exports: "jaspersoft.attributes"
        },

        "attributes.view":{
            deps: ["jquery", "underscore", "backbone", "attributes.model", "components.templateengine"],
            exports: "jaspersoft.attributes"
        },

        "export" : {
            deps:["namespace"],
            exports: "JRS.Export"
        },

        "export.statecontroller" : {
            deps:["jquery", "underscore", "backbone", "components.statecontrollertrait", "components.ajaxdownloader"],
            exports: "JRS.Export.StateController"
        },

        "export.servererrortrait" : {
            deps:["underscore", "components.servererrorsbackbonetrait"],
            exports: "JRS.Export.ServerErrorTrait"
        },

        "export.formmodel" : {
            deps:["jquery", "underscore", "backbone", "export.servererrortrait", "components.state"],
            exports: "JRS.Export.FormModel"
        },

        "export.extendedformview" : {
            deps:["jquery", "underscore", "backbone", "components.templateengine", "components.authoritymodel", "components.authoritypickerview", "components.state"],
            exports: "JRS.Export.ExtendedFormView"
        },

        "export.shortformview" : {
            deps:["jquery", "underscore", "backbone", "components.templateengine", "components.state"],
            exports: "JRS.Export.ShortFormView"
        },

        "export.app" : {
            deps: ["jquery", "underscore", "export.formmodel", "components.layout", "export.statecontroller", "components.state"],
            exports: "JRS.Export.App"
        },

        "import":{
            deps:["namespace"],
            exports: "JRS.Import"
        },

        "import.formmodel" : {
            deps: ["jquery", "underscore", "import", "backbone", "components.servererrorsbackbonetrait", "components.state"],
            exports: "JRS.Import.FormModel"
        },

        "import.extendedformview" : {
            deps:["jquery", "underscore", "import", "backbone", "components.templateengine", "components.state", "components.ajaxuploader", "components.stateview"],
            exports: "JRS.Import.ExtendedFormView"
        },

        "import.app" : {
            deps: ["jquery", "underscore", "import.formmodel", "components.layout", "components.state"],
            exports: "JRS.Import.App"
        },

        "report.view.base":{
            deps:["jquery", "underscore", "controls.basecontrol", "controls.base", "core.ajax"],
            exports: "Report"
        },

        "controls.components":{
            deps:[
                "jquery",
                "underscore",
                "controls.basecontrol",
                "jquery.datepicker.extensions",
                "jquery.timepicker",
                "common/component/singleSelect/view/SingleSelect",
                "common/component/multiSelect/view/MultiSelect",
                "common/component/singleSelect/dataprovider/CacheableDataProvider",
                "common/util/parse/date"
            ],
            exports:"JRS.Controls"
        },

        "controls.viewmodel":{
            deps:["jquery", "underscore", "controls.core", "controls.basecontrol"],
            exports:"JRS.Controls"
        },

        "controls.logging": {
            deps:["namespace"],
            exports:"JRS"
        },

        "controls.controller":{
            deps:["jquery", "underscore", "controls.core", "controls.datatransfer", "controls.viewmodel", "controls.components", "report.view.base", "jquery.urldecoder"],
            exports:"JRS.Controls"
        },

        "components.about":{
            deps: ["components.dialogs"],
            exports:"about"
        },

        "dynamicTree.tree" : {
            deps: ["prototype", "dragdrop.extra", "touchcontroller", "utils.common", "core.layout", "json3"],
            exports: "dynamicTree"
        },

        "dynamicTree.treenode" : {
            deps: ["prototype", "dynamicTree.tree"],
            exports: "dynamicTree"
        },

        "dynamicTree.events" : {
            deps: ["prototype", "dynamicTree.tree"],
            exports: "dynamicTree"
        },

        "dynamicTree.utils" : {
            deps: ["components.dynamicTree", "touchcontroller"],
            exports: "dynamicTree"
        },

        "components.webHelp": {
            deps: ["jrs.configs"],
            exports: "webHelpModule"
        },

        "components.loginBox": {
            deps:["prototype", "components.webHelp", "components.dialogs", "components.utils", "core.layout"],
            exports: "loginBox"
        },

        "components.tabs": {
            deps: ["prototype"],
            exports: "tabModule"
        },

        "login": {
            deps: ["jquery", "components.loginBox", "jrs.configs", "encryption.utils"],
            exports: "jQuery"  //workaround
        },

        "jquery.jcryption": {
            deps: ["jquery"],
            exports: "jQuery"
        },

        "tools.infiniteScroll": {
            deps: ["jquery", "prototype", "utils.common"],
            exports: "InfiniteScroll"
        },

        //Manage Common Components

        "mng.common": {
            deps: ["jquery", "prototype", "utils.common", "tools.infiniteScroll", "components.list", "components.dynamicTree", "components.toolbar"],
            exports: "orgModule"
        },

        "mng.main" : {
            deps: ["jquery", "mng.common"],
            exports: "orgModule"
        },

        "mng.common.actions": {
            deps: ["jquery", "prototype", "mng.common"],
            exports: "orgModule"
        },

        //Manage Roles Components

        "org.role.mng.main": {
            deps: ["jquery", "mng.main", "components.webHelp"],
            exports: "orgModule"
        },

        "org.role.mng.actions": {
            deps: ["org.role.mng.main"],
            exports: "orgModule"
        },

        "org.role.mng.components": {
            deps: ["jquery", "org.role.mng.main"],
            exports: "orgModule"
        },

        //Manage Users Components

        "org.user.mng.main": {
            deps: ["jquery", "mng.main"],
            exports: "orgModule"
        },

        "org.user.mng.actions": {
            deps: ["jquery", "org.role.mng.main", "org.user.mng.main", "mng.common.actions"],
            exports: "orgModule"
        },

        "org.user.mng.components": {
            deps: ["jquery", "org.user.mng.main", "mng.common.actions", "encryption.utils"],
            exports: "orgModule"
        },

        "administer.base": {
            deps: ["prototype", "underscore", "core.ajax"],
            exports: "Administer"
        },

        "administer.logging": {
            deps: ["administer.base", "core.layout", "components.webHelp", "utils.common"],
            exports: "logging"
        },

        "administer.options": {
            deps: ["administer.base", "core.layout", "components.webHelp", "utils.common"],
            exports: "Options"
        },

        //Repository, resources wizards

        "repository.search.components":{
            deps: ["repository.search.main", "prototype", "utils.common", "dynamicTree.utils"],
            exports: "GenerateResource"  //TODO: refactor it
        },

        "component.repository.search":{
            deps: ["repository.search.main","repository.search.components", "prototype", "actionModel.modelGenerator", "utils.common", "core.ajax"],
            exports: "repositorySearch"
        },

        //TODO: should go away after moving to AMD
        "repository.search.actions":{
            deps: ["repository.search.main","repository.search.components", "prototype", "actionModel.modelGenerator", "utils.common", "core.ajax"],
            exports: "repositorySearch"
        },

        "repository.search.main":{
            deps:["prototype", "actionModel.modelGenerator", "utils.common"],
            exports: "repositorySearch"
        },

        "dateFormatter": {
            exports: "window"
        },

        "datepicker.i18n.en": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.de": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.es": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.fr": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.it": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.ja": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.ro": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.zh-CN": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "datepicker.i18n.zh-TW": {
            deps: ["jquery.ui"],
            exports: "jQuery"
        },

        "report.global": {
            exports: "jasperreports"
        },

        "report.view": {
            deps: ["report.view.base"],
            exports: "Report"
        },

        "controls.report": {
            deps: ["controls.controller", "report.view.base"],
            exports: "Controls"
        },

        "jquery.ui.mouse.touch": {
            deps: ["jquery", "jquery.ui"],
            exports: "jQuery"
        },

        "resource.base": {
            deps: ["prototype", "utils.common", "core.layout"],
            exports: "resource"
        },

        "resource.locate": {
            deps: ["resource.base", "jquery", "components.pickers"],
            exports: "resourceLocator"
        },

        "resource.dataSource": {
            deps: ["jquery", "underscore", "backbone", "core.ajax", "components.dialogs", "utils.common", "resource.locate"],
            exports: "window.ResourceDataSource"
        },

        "resource.dataSource.jdbc": {
            deps: ["resource.dataSource", "mustache", "components.dialog", "core.events.bis", "xregexp"],
            exports: "window.JdbcDataSourceEditor"
        },

        "resource.dataSource.jndi": {
            deps: ["resource.dataSource", "mustache", "components.dialog", "core.events.bis", "xregexp"],
            exports: "window.JndiResourceDataSource"
        },

        "resource.dataSource.bean": {
            deps: ["resource.dataSource", "mustache", "components.dialog", "core.events.bis", "xregexp"],
            exports: "window.BeanResourceDataSource"
        },

        "resource.dataSource.aws": {
            deps: ["resource.dataSource.jdbc"],
            exports: "window.AwsResourceDataSource"
        },

        "resource.dataSource.virtual": {
            deps: ["resource.dataSource", "mustache", "components.dialog", "core.events.bis", "xregexp",  "components.dependent.dialog"],
            exports: "window.VirtualResourceDataSource"
        },

        "resource.dataType": {
            deps:["resource.base", "prototype", "utils.common"],
            exports: "resourceDataType"
        },

        "resource.dataType.locate": {
            deps:["resource.locate"],
            exports: "resourceDataTypeLocate"
        },

        "resource.listOfValues.locate": {
            deps: ["resource.locate"],
            exports: "resourceListOfValuesLocate"
        },

        "resource.listofvalues": {
            deps: ["resource.base", "utils.common"],
            exports: "resourceListOfValues"
        },

        "resource.inputControl": {
            deps:["resource.base", "prototype", "utils.common"],
            exports: "addInputControl"
        },

        "resource.add.files": {
            deps:["resource.locate", "prototype", "utils.common", "core.events.bis"],
            exports: "addFileResource"
        },

        "resource.add.mondrianxmla": {
            deps:["resource.base", "components.pickers", "prototype", "utils.common"],
            exports: "resourceMondrianXmla"
        },

        "resource.query": {
            deps:["resource.base", "prototype", "utils.common"],
            exports:"resourceQuery"
        },

        "resource.report": {
            deps: ["resource.locate", "prototype", "jquery", "utils.common"],
            exports: "resourceReport"
        },

        "resource.reportResourceNaming": {
            deps: ["resource.base", "components.pickers", "prototype", "utils.common"],
            exports: "resourceReportResourceNaming"
        },

        "resource.inputControl.locate": {
            deps: ["resource.locate", "core.events.bis", "prototype"],
            exports: "inputControl"
        },

        "resource.query.locate": {
            deps: ["resource.locate", "prototype"],
            exports: "resourceQueryLocate"
        },

        "resource.analysisView": {
            deps: ["resource.base", "utils.common", "prototype"],
            exports: "resourceAnalysisView"
        },

        "resource.analysisConnection.mondrian.locate": {
            deps: ["resource.locate", "prototype"],
            exports: "resourceMondrianLocate"
        },

        "resource.analysisConnection.xmla.locate": {
            deps: ["resource.locate", "prototype"],
            exports: "resourceOLAPLocate"
        },

        "resource.analysisConnection": {
            deps: ["resource.base", "prototype", "components.pickers", "utils.common"],
            exports: "resourceAnalysisConnection"
        },

        "resource.analysisConnection.dataSource.locate": {
            deps: ["resource.locate"],
            exports: "resourceDataSourceLocate"
        },

        "addinputcontrol.queryextra": {
            deps: ["prototype", "utils.common", "core.events.bis", "core.layout"],
            exports: "addListOfValues"
        },

        "org.rootObjectModifier": {
            deps: [],
            exports: "rom_init"
        },

        "report.schedule": {
            deps: ["prototype"],
            exports: "Schedule"
        },
        "report.schedule.list": {
            deps: ["prototype"],
            exports: "ScheduleList"
        },
        "report.schedule.setup": {
            deps: ["prototype"],
            exports: "ScheduleSetup"
        },
        "report.schedule.output": {
            deps: ["prototype"],
            exports: "ScheduleOutput"
        },
        "report.schedule.params": {
            deps: ["prototype", "controls.controller", "json3"],
            exports: "ScheduleParams"
        }
    }, // define non

	map: {
		'scheduler/view/editor/parameters': {
			'controls.options': 'controls.base'
		}
	},

    //Wait before giving up on loading a script.
    waitSeconds:60
});