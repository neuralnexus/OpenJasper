(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[4797,4026,8223,9389,7202,4887,2446,8323,1933,4564,3028,7335,5573,7409,5266,6595,553,2724,9174],{17335:(e,o,n)=>{var t,r,s;
/*!
 * jQuery UI Scroll Parent 1.13.2
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */!function(i){"use strict";r=[n(72157),n(91544)],void 0===(s="function"==typeof(t=function(e){return e.fn.scrollParent=function(o){var n=this.css("position"),t="absolute"===n,r=o?/(auto|scroll|hidden)/:/(auto|scroll)/,s=this.parents().filter((function(){var o=e(this);return(!t||"static"!==o.css("position"))&&r.test(o.css("overflow")+o.css("overflow-y")+o.css("overflow-x"))})).eq(0);return"fixed"!==n&&s.length?s:e(this[0].ownerDocument||document)}})?t.apply(o,r):t)||(e.exports=s)}()},98223:(e,o,n)=>{"use strict";n.d(o,{Z:()=>s});var t=n(71914),r={currentContext:"default",displayWebHelp:function(){var e=t.Z.webHelpModuleState;if(e){var o=e.contextMap[r.currentContext],n="".concat(e.hostURL,"/").concat(e.pagePrefix).concat(o);window.name="";var s=window.open(n,"MCWebHelp");s&&s.focus()}},setCurrentContext:function(e){r.currentContext=e}};const s=r},11129:(e,o,n)=>{"use strict";n.d(o,{m0:()=>l,Po:()=>c,mr:()=>u});var t=n(52687),r=n(94277),s=n(72157),i=n.n(s);function a(e,o){(e&&e.indexOf("sessionAttributeMissingException"))>-1?t.Z.clusterErrorPopup.show(e):t.Z.errorPopup.show(e,!1,o)}function l(e){if(500==e.status)return a(e.responseText),!0;if(e.getResponseHeader("LoginRequested")){return document.location=".",!0}if(e.getResponseHeader("JasperServerError")){if(!e.getResponseHeader("SuppressError"))if(1==i()(".dashboardViewFrame").length)i()(document.body).html(e.responseText),i()("#"+r.hl.fid,window.parent.document).removeClass("hidden").show();else a(e.responseText);return!0}return!1}function c(){}function u(){}},94277:(e,o,n)=>{"use strict";n.d(o,{hl:()=>t,YT:()=>s});var t={vars:{element_scrolled:!1,ajax_in_progress:!1,current_flow:null,contextPath:__jrsConfigs__.contextPath},i18n:{}};if(void 0===r)var r=!1;function s(){return __jrsConfigs__.isProVersion}void 0===t&&(t={Mocks:{}}),void 0===t.vars&&(t.vars={element_scrolled:!1,ajax_in_progress:!1,current_flow:null}),void 0===t.Export&&(t.Export={i18n:{"file.name.empty":"export.file.name.empty","file.name.too.long":"export.file.name.too.long","file.name.not.valid":"export.file.name.not.valid","export.select.users":"export.select.users","export.select.roles":"export.select.roles","export.session.expired":"export.session.expired","error.timeout":"export.file.name.empty"},configs:{TIMEOUT:12e5,DELAY:3e3}}),void 0===window.localContext&&(window.localContext={}),__jrsConfigs__.calendar&&(t.i18n.bundledCalendarFormat=__jrsConfigs__.calendar.i18n.bundledCalendarFormat,t.i18n.bundledCalendarTimeFormat=__jrsConfigs__.calendar.i18n.bundledCalendarTimeFormat),window.JRS=t,window.jaspersoft={components:{},i18n:{}},window.isProVersion=s}}]);
//# sourceMappingURL=chunk.4797.js.map