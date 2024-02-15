requirejs.config({
  enforceDefine: true,
  config: {
    moment: {
      noGlobal: true
    },
    logger: {
      enabled: true,
      level: 'error',
      appenders: ['console']
    },
    stdnav: {
    
    },
    i18n: {
      paths: {
        'js-sdk': 'runtime_dependencies/js-sdk'
      }
    }
  },
  paths: {
    request: 'transport/request',
    requestSettings: 'config/requestSettings',
    backbone: 'config/Backbone',
    'underscore.string': 'runtime_dependencies/underscore.string/dist/underscore.string',
    'requirejs.plugin.css': 'runtime_dependencies/require-css/css',
    'tv4.original': 'runtime_dependencies/tv4/tv4',
    'backbone.validation.original': 'runtime_dependencies/backbone-validation/dist/backbone-validation-amd',
    jquery: 'runtime_dependencies/jquery/dist/jquery',
    underscore: 'runtime_dependencies/underscore/underscore',
    xregexp: 'runtime_dependencies/xregexp/xregexp-all',
    numeralPackage: 'runtime_dependencies/numeral',
    numeral: 'runtime_dependencies/numeral/numeral',
    localizedNumeral: 'runtime_dependencies/js-sdk/src/common/extension/numeralExtension',
    moment: 'runtime_dependencies/moment/moment',
    momentLocales: 'runtime_dependencies/moment/locale',
    localizedMoment: 'runtime_dependencies/js-sdk/src/common/extension/momentExtension',
    momentTimezone: 'runtime_dependencies/moment-timezone/builds/moment-timezone-with-data',
    domReady: 'runtime_dependencies/requirejs-domready/domReady',
    xdm: 'runtime_dependencies/easyXDM/artifacts/v2.4.20/easyXDM.jasper',
    'backbone.epoxy.original': 'runtime_dependencies/backbone.epoxy/backbone.epoxy',
    'backbone.marionette': 'runtime_dependencies/backbone.marionette/lib/core/backbone.marionette',
    'backbone.wreqr': 'runtime_dependencies/backbone.wreqr/lib/backbone.wreqr',
    'backbone.babysitter': 'runtime_dependencies/backbone.babysitter/lib/backbone.babysitter',
    'jquery-ui': 'runtime_dependencies/jquery-ui/ui',
    jQueryDatepickerExtension: 'runtime_dependencies/js-sdk/src/common/extension/jQueryDatepickerExtension',
    jQueryTimepickerExtension: 'runtime_dependencies/js-sdk/src/common/extension/jQueryTimepickerExtension',
    jQueryUiSliderAccessExtension: 'runtime_dependencies/js-sdk/src/common/extension/jQueryUiSliderAccessExtension',
    'jquery-ui/widgets/timepicker': 'runtime_dependencies/jqueryui-timepicker-addon/dist/jquery-ui-timepicker-addon',
    'jquery-ui-sliderAccess': 'runtime_dependencies/jqueryui-timepicker-addon/dist/jquery-ui-sliderAccess',
    'perfect-scrollbar': 'runtime_dependencies/perfect-scrollbar/dist/js/perfect-scrollbar.jquery',
    'jquery.ui.mouse.touch': 'runtime_dependencies/jquery-ui-touch-punch/jquery.ui.touch-punch',
    'jquery.selection': 'runtime_dependencies/jquery.selection/src/jquery.selection',
    'jquery.urldecoder': 'runtime_dependencies/jquery.urldecoder/jquery.urldecoder',
    'jquery.jcryption': 'runtime_dependencies/jCryption/jquery.jcryption',
    underscoreExtension: 'runtime_dependencies/js-sdk/src/common/extension/underscoreExtension',
    tv4: 'runtime_dependencies/js-sdk/src/common/config/tv4Settings',
    'backbone.validation': 'runtime_dependencies/js-sdk/src/common/extension/backboneValidationExtension',
    'backbone.epoxy': 'runtime_dependencies/js-sdk/src/common/extension/epoxyExtension',
    bundle: 'plugin/bundle',
    text: 'plugin/text',
    css: 'runtime_dependencies/js-sdk/src/common/plugin/css',
    csslink: 'runtime_dependencies/js-sdk/src/common/plugin/csslink',
    vizShim: 'runtime_dependencies/js-sdk/src/common/plugin/vizShim',
    momentLocale: 'runtime_dependencies/js-sdk/src/common/plugin/momentLocale',
    logger: 'runtime_dependencies/js-sdk/src/common/logging/logger',
    stdnav: 'runtime_dependencies/js-sdk/src/common/stdnav/stdnav',
    stdnavDebugger: 'runtime_dependencies/js-sdk/src/common/stdnav/stdnavDebugger',
    stdnavFocusing: 'runtime_dependencies/js-sdk/src/common/stdnav/stdnavFocusing',
    stdnavModalFocusing: 'runtime_dependencies/js-sdk/src/common/stdnav/stdnavModalFocusing',
    stdnavEventHandlers: 'runtime_dependencies/js-sdk/src/common/stdnav/stdnavEventHandlers',
    stdnavPluginAnchor: 'runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginAnchor',
    stdnavPluginButton: 'runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginButton',
    stdnavPluginForms: 'runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginForms',
    stdnavPluginGrid: 'runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginGrid',
    stdnavPluginList: 'runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginList',
    stdnavPluginTable: 'runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginTable',
    common: 'runtime_dependencies/js-sdk/src/common',
    'jquery-ui-locales': 'runtime_dependencies/js-sdk/src/settings/jquery-ui/i18n',
    fakeXhrFactory: 'transport/fakeXhrFactory',
    'backbone.original': 'runtime_dependencies/backbone/backbone',
    'requirejs.plugin.text': 'runtime_dependencies/requirejs-text/text',
    prototype: 'runtime_dependencies/prototype/dist/prototype',
    builder: 'runtime_dependencies/scriptaculous/src/builder',
    effects: 'runtime_dependencies/scriptaculous/src/effects',
    dragdrop: 'runtime_dependencies/scriptaculous/src/dragdrop',
    iscroll: 'runtime_dependencies/iscroll/src/iscroll',
    'dragdrop.extra': 'runtime_dependencies/dragdropextra/dragdropextra',
    touchcontroller: 'touch.controller',
    'components.toolbar': 'components.toolbarButtons.events',
    'components.list': 'list.base',
    'components.dynamicTree': 'dynamicTree.treesupport',
    'component.repository.search': 'repository.search.actions',
    'report.view': 'report.view.runtime',
    stdnavPluginActionMenu: 'stdnav/plugins/stdnavPluginActionMenu',
    stdnavPluginDynamicList: 'stdnav/plugins/stdnavPluginDynamicList',
    stdnavPluginToolbar: 'stdnav/plugins/stdnavPluginToolbar',
    'wcf.scroll': '../wcf/scroller',
    'report.global': '../reportresource?resource=net/sf/jasperreports/web/servlets/resources/jasperreports-global.js',
    ReportRequireJsConfig: '../getRequirejsConfig.html?noext',
    async: 'runtime_dependencies/requirejs-plugins/src/async',
    settings: 'plugin/settings',
    restResource: 'plugin/restResource',
    json: 'plugin/json',
    components: 'runtime_dependencies/js-sdk/src/components',
    'bi/report': 'runtime_dependencies/bi-report/src/bi/report',
    'bi/repository': 'runtime_dependencies/bi-repository/src/bi/repository',
    'jasperreports-loader': 'reportViewer/jasperreports-loader'
  },
  shim: {
    jquery: {
      deps: ['xssUtil'],
      init: function() {
                return this.jQuery.noConflict();
            }
    },
    momentTimezone: {
      deps: ['moment']
    },
    'jquery.selection': {
      deps: ['jquery'],
      exports: 'jQuery'
    },
    'jquery-ui-sliderAccess': {
      deps: ['jquery','jquery-ui/widgets/slider'],
      exports: 'jQuery'
    },
    'jquery.urldecoder': {
      deps: ['jquery'],
      exports: 'jQuery'
    },
    xregexp: {
      exports: 'XRegExp'
    },
    'jquery.jcryption': {
      deps: ['jquery'],
      exports: 'jQuery'
    },
    jasper: {
      exports: 'jasper'
    },
    builder: {
      deps: ['prototype'],
      exports: 'Builder'
    },
    effects: {
      deps: ['prototype'],
      exports: 'Effect'
    },
    prototype: {
      exports: '__dollar_sign__'
    },
    dragdrop: {
      deps: ['prototype','effects'],
      exports: 'Draggable'
    },
    'dragdrop.extra': {
      deps: ['dragdrop','jquery'],
      exports: 'Draggable'
    },
    iscroll: {
      exports: 'iScroll'
    },
    'wcf.scroll': {
      exports: 'document'
    },
    ReportRequireJsConfig: {
      exports: 'window'
    },
    'fakeActionModel.primaryNavigation': {
      exports: 'primaryNavModule'
    },
    namespace: {
      exports: 'jaspersoft'
    },
    touchcontroller: {
      deps: ['jquery'],
      exports: 'TouchController'
    },
    'jpivot.jaPro': {
      deps: ['prototype','utils.common'],
      exports: 'bWidth'
    },
    'utils.common': {
      deps: ['prototype','jquery','underscore'],
      exports: 'jQuery'
    },
    'utils.animation': {
      deps: ['prototype','effects'],
      exports: 'jQuery'
    },
    'tools.truncator': {
      deps: ['prototype'],
      exports: 'jQuery'
    },
    'tools.drag': {
      deps: ['jquery','prototype'],
      exports: 'Dragger'
    },
    'actionModel.modelGenerator': {
      deps: ['prototype','utils.common','core.events.bis'],
      exports: 'actionModel'
    },
    'actionModel.primaryNavigation': {
      deps: ['actionModel.modelGenerator'],
      exports: 'primaryNavModule'
    },
    'core.layout': {
      deps: ['jquery','prototype','utils.common','dragdrop.extra','tools.truncator','iscroll','components.webHelp'],
      exports: 'layoutModule'
    },
    'ajax.mock': {
      deps: ['jquery'],
      exports: 'fakeResponce'
    },
    'core.ajax': {
      deps: ['jquery','prototype','utils.common','builder','namespace'],
      exports: 'ajax'
    },
    'core.accessibility': {
      deps: ['prototype','components.list','actionModel.modelGenerator','core.events.bis'],
      exports: 'accessibilityModule'
    },
    'core.events.bis': {
      deps: ['jquery','prototype','utils.common','core.layout','components.tooltip'],
      exports: 'buttonManager'
    },
    'core.key.events': {
      deps: ['jquery','prototype','utils.common','core.layout'],
      exports: 'keyManager'
    },
    'components.templateengine': {
      deps: ['namespace','jquery','underscore'],
      exports: 'jaspersoft.components.templateEngine'
    },
    'components.dialogs': {
      deps: ['jquery','prototype','underscore','utils.common','utils.animation','core.layout'],
      exports: 'dialogs'
    },
    'components.dependent.dialog': {
      deps: ['prototype','components.dialogs','jquery','components.list'],
      exports: 'dialogs.dependentResources'
    },
    'components.list': {
      deps: ['jquery','prototype','touchcontroller','utils.common','dragdrop.extra','core.events.bis'],
      exports: 'dynamicList'
    },
    'components.searchBox': {
      deps: ['prototype','utils.common','core.events.bis'],
      exports: 'SearchBox'
    },
    'components.toolbarButtons': {
      deps: ['jquery','prototype'],
      exports: 'toolbarButtonModule'
    },
    'messages/list/messageList': {
      deps: ['prototype','components.list','components.toolbar','core.layout'],
      exports: 'messageListModule'
    },
    'messages/details/messageDetails': {
      deps: ['prototype','components.toolbar','core.layout'],
      exports: 'messageDetailModule'
    },
    'components.toolbar': {
      deps: ['jquery','prototype','utils.common','components.toolbarButtons'],
      exports: 'toolbarButtonModule'
    },
    'components.tooltip': {
      deps: ['jquery','prototype','underscore','utils.common','core.layout'],
      exports: 'JSTooltip'
    },
    'components.dynamicTree': {
      deps: ['prototype','dynamicTree.tree','dynamicTree.treenode','dynamicTree.events','core.ajax'],
      exports: 'dynamicTree'
    },
    'components.utils': {
      deps: ['jquery','underscore','components.dialogs','core.ajax'],
      exports: 'jaspersoft.components.utils'
    },
    heartbeat: {
      deps: ['jquery'],
      exports: 'checkHeartBeat'
    },
    'components.heartbeat': {
      deps: ['prototype','core.ajax'],
      exports: 'heartbeat'
    },
    'components.customTooltip': {
      deps: [],
      exports: 'customTooltip'
    },
    'components.pickers': {
      deps: ['utils.common','components.dialogs','core.layout','core.events.bis','prototype','jquery','dynamicTree.utils'],
      exports: 'picker'
    },
    'controls.core': {
      deps: ['jquery','underscore','components.dialogs','namespace','controls.logging'],
      exports: 'JRS.Controls'
    },
    localContext: {
      exports: 'window'
    },
    'controls.dataconverter': {
      deps: ['underscore','controls.core'],
      exports: 'JRS.Controls'
    },
    'controls.datatransfer': {
      deps: ['jquery','controls.core','backbone','controls.dataconverter'],
      exports: 'JRS.Controls'
    },
    'controls.basecontrol': {
      deps: ['jquery','underscore','controls.core'],
      exports: 'JRS.Controls'
    },
    'controls.base': {
      deps: ['jquery','underscore','utils.common'],
      exports: 'ControlsBase'
    },
    'repository.search.globalSearchBoxInit': {
      deps: ['prototype','actionModel.primaryNavigation','components.searchBox'],
      exports: 'globalSearchBox'
    },
    'report.view.base': {
      deps: ['jquery','underscore','controls.basecontrol','controls.base','core.ajax'],
      exports: 'Report'
    },
    'controls.components': {
      deps: ['jquery','underscore','controls.basecontrol','components/singleSelect/view/SingleSelect','components/multiSelect/view/MultiSelect','components/singleSelect/dataprovider/CacheableDataProvider','common/util/parse/date','components/multiSelect/dataprovider/selectedItemsDataProviderSorterFactory'],
      exports: 'JRS.Controls'
    },
    'controls.viewmodel': {
      deps: ['jquery','underscore','controls.core','controls.basecontrol','jquery-ui/widgets/sortable'],
      exports: 'JRS.Controls'
    },
    'controls.logging': {
      deps: ['namespace'],
      exports: 'JRS'
    },
    'controls.controller': {
      deps: ['jquery','underscore','controls.core','controls.datatransfer','controls.viewmodel','controls.components','report.view.base','jquery.urldecoder'],
      exports: 'JRS.Controls'
    },
    'components.about': {
      deps: ['components.dialogs'],
      exports: 'about'
    },
    'dynamicTree.tree': {
      deps: ['prototype','dragdrop.extra','touchcontroller','utils.common','core.layout'],
      exports: 'dynamicTree'
    },
    'dynamicTree.treenode': {
      deps: ['prototype','underscore','dynamicTree.tree'],
      exports: 'dynamicTree'
    },
    'dynamicTree.events': {
      deps: ['prototype','dynamicTree.tree'],
      exports: 'dynamicTree'
    },
    'dynamicTree.utils': {
      deps: ['components.dynamicTree','touchcontroller','dynamicTree.treenode'],
      exports: 'dynamicTree'
    },
    'components.webHelp': {
      deps: ['jrs.configs'],
      exports: 'webHelpModule'
    },
    'components.loginBox': {
      deps: ['prototype','components.webHelp','components.dialogs','components.utils','core.layout'],
      exports: 'loginBox'
    },
    'login.form': {
      deps: ['jquery','components.loginBox','jrs.configs','common/util/encrypter'],
      exports: 'jQuery'
    },
    'tools.infiniteScroll': {
      deps: ['prototype','utils.common'],
      exports: 'InfiniteScroll'
    },
    'mng.common': {
      deps: ['jquery','underscore','prototype','utils.common','tools.infiniteScroll','components.list','components.dynamicTree','components.toolbar','common/component/dialog/ConfirmationDialog'],
      exports: 'orgModule'
    },
    'mng.main': {
      deps: ['jquery','mng.common'],
      exports: 'orgModule'
    },
    'mng.common.actions': {
      deps: ['jquery','prototype','mng.common'],
      exports: 'orgModule'
    },
    'org.role.mng.main': {
      deps: ['jquery','mng.main','components.webHelp'],
      exports: 'orgModule'
    },
    'org.role.mng.actions': {
      deps: ['org.role.mng.main'],
      exports: 'orgModule'
    },
    'org.role.mng.components': {
      deps: ['jquery','org.role.mng.main'],
      exports: 'orgModule'
    },
    'org.user.mng.main': {
      deps: ['jquery','mng.main'],
      exports: 'orgModule'
    },
    'org.user.mng.actions': {
      deps: ['jquery','org.role.mng.main','org.user.mng.main','mng.common.actions'],
      exports: 'orgModule'
    },
    'org.user.mng.components': {
      deps: ['jquery','org.user.mng.main','mng.common.actions','common/util/encrypter'],
      exports: 'orgModule'
    },
    'administer.base': {
      deps: ['prototype','underscore','core.ajax'],
      exports: 'Administer'
    },
    'administer.logging': {
      deps: ['administer.base','core.layout','components.webHelp','utils.common'],
      exports: 'logging'
    },
    'administer.options': {
      deps: ['administer.base','core.layout','components.webHelp','utils.common'],
      exports: 'Options'
    },
    'repository.search.components': {
      deps: ['repository.search.main','prototype','underscore','utils.common','dynamicTree.utils','tools.infiniteScroll','tenantImportExport/export/view/ExportDialogView','tenantImportExport/export/enum/exportTypesEnum'],
      exports: 'GenerateResource'
    },
    'component.repository.search': {
      deps: ['repository.search.main','repository.search.components','prototype','actionModel.modelGenerator','utils.common','core.ajax'],
      exports: 'repositorySearch'
    },
    'repository.search.actions': {
      deps: ['repository.search.main','repository.search.components','prototype','actionModel.modelGenerator','utils.common','core.ajax'],
      exports: 'repositorySearch'
    },
    'repository.search.main': {
      deps: ['prototype','actionModel.modelGenerator','utils.common','common/component/dialog/AlertDialog'],
      exports: 'repositorySearch'
    },
    'report.global': {
      exports: 'jasperreports'
    },
    'report.view': {
      deps: ['report.view.base'],
      exports: 'Report'
    },
    'controls.report': {
      deps: ['controls.controller','report.view.base'],
      exports: 'Controls'
    },
    'resource.base': {
      deps: ['prototype','utils.common','core.layout'],
      exports: 'resource'
    },
    'resource.locate': {
      deps: ['resource.base','jquery','components.pickers'],
      exports: 'resourceLocator'
    },
    'resource.dataSource': {
      deps: ['jquery','underscore','backbone','core.ajax','components.dialogs','utils.common','resource.locate'],
      exports: 'window.ResourceDataSource'
    },
    'resource.dataSource.jdbc': {
      deps: ['resource.dataSource','components.dialog','core.events.bis','xregexp'],
      exports: 'window.JdbcDataSourceEditor'
    },
    'resource.dataSource.jndi': {
      deps: ['resource.dataSource','components.dialog','core.events.bis','xregexp'],
      exports: 'window.JndiResourceDataSource'
    },
    'resource.dataSource.bean': {
      deps: ['resource.dataSource','components.dialog','core.events.bis','xregexp'],
      exports: 'window.BeanResourceDataSource'
    },
    'resource.dataSource.aws': {
      deps: ['resource.dataSource.jdbc'],
      exports: 'window.AwsResourceDataSource'
    },
    'resource.dataSource.virtual': {
      deps: ['resource.dataSource','components.dialog','core.events.bis','xregexp','components.dependent.dialog'],
      exports: 'window.VirtualResourceDataSource'
    },
    'resource.dataType': {
      deps: ['resource.base','prototype','utils.common'],
      exports: 'resourceDataType'
    },
    'resource.dataType.locate': {
      deps: ['resource.locate'],
      exports: 'resourceDataTypeLocate'
    },
    'resource.listOfValues.locate': {
      deps: ['resource.locate'],
      exports: 'resourceListOfValuesLocate'
    },
    'resource.listofvalues': {
      deps: ['resource.base','utils.common'],
      exports: 'resourceListOfValues'
    },
    'resource.inputControl': {
      deps: ['resource.base','prototype','utils.common'],
      exports: 'addInputControl'
    },
    'resource.add.files': {
      deps: ['resource.locate','prototype','utils.common','core.events.bis'],
      exports: 'addFileResource'
    },
    'resource.add.mondrianxmla': {
      deps: ['resource.base','components.pickers','prototype','utils.common'],
      exports: 'resourceMondrianXmla'
    },
    'resource.query': {
      deps: ['resource.base','prototype','utils.common'],
      exports: 'resourceQuery'
    },
    'resource.report': {
      deps: ['resource.locate','prototype','jquery','utils.common'],
      exports: 'resourceReport'
    },
    'resource.reportResourceNaming': {
      deps: ['resource.base','components.pickers','prototype','utils.common'],
      exports: 'resourceReportResourceNaming'
    },
    'resource.inputControl.locate': {
      deps: ['resource.locate','core.events.bis','prototype'],
      exports: 'inputControl'
    },
    'resource.query.locate': {
      deps: ['resource.locate','prototype'],
      exports: 'resourceQueryLocate'
    },
    'resource.analysisView': {
      deps: ['resource.base','utils.common','prototype'],
      exports: 'resourceAnalysisView'
    },
    'resource.analysisConnection.mondrian.locate': {
      deps: ['resource.locate','prototype'],
      exports: 'resourceMondrianLocate'
    },
    'resource.analysisConnection.xmla.locate': {
      deps: ['resource.locate','prototype'],
      exports: 'resourceOLAPLocate'
    },
    'resource.analysisConnection': {
      deps: ['resource.base','prototype','components.pickers','utils.common'],
      exports: 'resourceAnalysisConnection'
    },
    'resource.analysisConnection.dataSource.locate': {
      deps: ['resource.locate'],
      exports: 'resourceDataSourceLocate'
    },
    'addinputcontrol.queryextra': {
      deps: ['prototype','utils.common','core.events.bis','core.layout'],
      exports: 'addListOfValues'
    },
    'org.rootObjectModifier': {
      deps: [],
      exports: 'rom_init'
    },
    'report.schedule': {
      deps: ['prototype'],
      exports: 'Schedule'
    },
    'report.schedule.list': {
      deps: ['prototype'],
      exports: 'ScheduleList'
    },
    'report.schedule.setup': {
      deps: ['prototype'],
      exports: 'ScheduleSetup'
    },
    'report.schedule.output': {
      deps: ['prototype'],
      exports: 'ScheduleOutput'
    },
    'report.schedule.params': {
      deps: ['prototype','controls.controller'],
      exports: 'ScheduleParams'
    }
  },
  map: {
    '*': {
      underscore: 'underscoreExtension',
      'jquery-ui/widgets/datepicker': 'jQueryDatepickerExtension',
      'jquery-ui/widgets/timepicker': 'jQueryTimepickerExtension',
      xssUtil: 'common/util/xssUtil',
      'settings/localeSettings': 'jrs.configs',
      'settings/dateTimeSettings': 'settings!dateTimeSettings',
      'settings/decimalFormatSymbols': 'settings!decimalFormatSymbols',
      'settings/generalSettings': 'jrs.configs'
    },
    underscoreExtension: {
      underscore: 'underscore'
    },
    jQueryDatepickerExtension: {
      'jquery-ui/widgets/datepicker': 'jquery-ui/widgets/datepicker'
    },
    jQueryTimepickerExtension: {
      'jquery-ui/widgets/timepicker': 'jquery-ui/widgets/timepicker'
    },
    'jquery-ui/widgets/timepicker': {
      jquery: 'jQueryUiSliderAccessExtension',
      'jquery-ui': 'jquery-ui/widgets/datepicker'
    },
    'scheduler/view/editor/parametersTabView': {
      'controls.options': 'controls.base'
    }
  },
  waitSeconds: 0
});