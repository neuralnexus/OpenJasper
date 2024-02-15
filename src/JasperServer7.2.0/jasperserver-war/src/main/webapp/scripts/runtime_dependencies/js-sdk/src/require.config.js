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
    
    }
  },
  paths: {
    request: 'common/transport/request',
    requestSettings: 'common/config/requestSettings',
    backbone: 'runtime_dependencies/backbone/backbone',
    'underscore.string': 'runtime_dependencies/underscore.string/dist/underscore.string',
    'requirejs.plugin.css': 'runtime_dependencies/require-css/css',
    'tv4.original': 'runtime_dependencies/tv4/tv4',
    'backbone.validation.original': 'runtime_dependencies/backbone-validation/dist/backbone-validation-amd',
    jquery: 'runtime_dependencies/jquery/dist/jquery',
    underscore: 'runtime_dependencies/underscore/underscore',
    xregexp: 'runtime_dependencies/xregexp/xregexp-all',
    numeralPackage: 'runtime_dependencies/numeral',
    numeral: 'runtime_dependencies/numeral/numeral',
    localizedNumeral: 'common/extension/numeralExtension',
    moment: 'runtime_dependencies/moment/moment',
    momentLocales: 'runtime_dependencies/moment/locale',
    localizedMoment: 'common/extension/momentExtension',
    momentTimezone: 'runtime_dependencies/moment-timezone/builds/moment-timezone-with-data',
    domReady: 'runtime_dependencies/requirejs-domready/domReady',
    xdm: 'runtime_dependencies/easyXDM/artifacts/v2.4.20/easyXDM.jasper',
    'backbone.epoxy.original': 'runtime_dependencies/backbone.epoxy/backbone.epoxy',
    'backbone.marionette': 'runtime_dependencies/backbone.marionette/lib/core/backbone.marionette',
    'backbone.wreqr': 'runtime_dependencies/backbone.wreqr/lib/backbone.wreqr',
    'backbone.babysitter': 'runtime_dependencies/backbone.babysitter/lib/backbone.babysitter',
    'jquery-ui': 'runtime_dependencies/jquery-ui/ui',
    jQueryDatepickerExtension: 'common/extension/jQueryDatepickerExtension',
    jQueryTimepickerExtension: 'common/extension/jQueryTimepickerExtension',
    jQueryUiSliderAccessExtension: 'common/extension/jQueryUiSliderAccessExtension',
    'jquery-ui/widgets/timepicker': 'runtime_dependencies/jqueryui-timepicker-addon/dist/jquery-ui-timepicker-addon',
    'jquery-ui-sliderAccess': 'runtime_dependencies/jqueryui-timepicker-addon/dist/jquery-ui-sliderAccess',
    'perfect-scrollbar': 'runtime_dependencies/perfect-scrollbar/dist/js/perfect-scrollbar.jquery',
    'jquery.ui.mouse.touch': 'runtime_dependencies/jquery-ui-touch-punch/jquery.ui.touch-punch',
    'jquery.selection': 'runtime_dependencies/jquery.selection/src/jquery.selection',
    'jquery.urldecoder': 'runtime_dependencies/jquery.urldecoder/jquery.urldecoder',
    'jquery.jcryption': 'runtime_dependencies/jCryption/jquery.jcryption',
    underscoreExtension: 'common/extension/underscoreExtension',
    tv4: 'common/config/tv4Settings',
    'backbone.validation': 'common/extension/backboneValidationExtension',
    'backbone.epoxy': 'common/extension/epoxyExtension',
    bundle: 'common/plugin/bundle',
    text: 'runtime_dependencies/requirejs-text/text',
    css: 'common/plugin/css',
    csslink: 'common/plugin/csslink',
    vizShim: 'common/plugin/vizShim',
    momentLocale: 'common/plugin/momentLocale',
    logger: 'common/logging/logger',
    stdnav: 'common/stdnav/stdnav',
    stdnavDebugger: 'common/stdnav/stdnavDebugger',
    stdnavFocusing: 'common/stdnav/stdnavFocusing',
    stdnavModalFocusing: 'common/stdnav/stdnavModalFocusing',
    stdnavEventHandlers: 'common/stdnav/stdnavEventHandlers',
    stdnavPluginAnchor: 'common/stdnav/plugins/stdnavPluginAnchor',
    stdnavPluginButton: 'common/stdnav/plugins/stdnavPluginButton',
    stdnavPluginForms: 'common/stdnav/plugins/stdnavPluginForms',
    stdnavPluginGrid: 'common/stdnav/plugins/stdnavPluginGrid',
    stdnavPluginList: 'common/stdnav/plugins/stdnavPluginList',
    stdnavPluginTable: 'common/stdnav/plugins/stdnavPluginTable'
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
    }
  },
  map: {
    '*': {
      underscore: 'underscoreExtension',
      'jquery-ui/widgets/datepicker': 'jQueryDatepickerExtension',
      'jquery-ui/widgets/timepicker': 'jQueryTimepickerExtension',
      xssUtil: 'common/util/xssUtil'
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
    }
  },
  waitSeconds: 60
});