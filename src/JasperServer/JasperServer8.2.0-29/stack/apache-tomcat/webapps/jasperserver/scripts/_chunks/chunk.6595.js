(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[6595,7335,5573],{17335:(e,s,r)=>{var t,o,n;
/*!
 * jQuery UI Scroll Parent 1.13.2
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */!function(i){"use strict";o=[r(72157),r(91544)],void 0===(n="function"==typeof(t=function(e){return e.fn.scrollParent=function(s){var r=this.css("position"),t="absolute"===r,o=s?/(auto|scroll|hidden)/:/(auto|scroll)/,n=this.parents().filter((function(){var s=e(this);return(!t||"static"!==s.css("position"))&&o.test(s.css("overflow")+s.css("overflow-y")+s.css("overflow-x"))})).eq(0);return"fixed"!==r&&n.length?n:e(this[0].ownerDocument||document)}})?t.apply(s,o):t)||(e.exports=n)}()},11129:(e,s,r)=>{"use strict";r.d(s,{le:()=>u,m0:()=>c,Po:()=>f,mr:()=>p});var t=r(52687),o=r(94277),n=r(72157),i=r.n(n);function u(e,s){(e&&e.indexOf("sessionAttributeMissingException"))>-1?t.Z.clusterErrorPopup.show(e):t.Z.errorPopup.show(e,!1,s)}function c(e){if(500==e.status)return u(e.responseText),!0;if(e.getResponseHeader("LoginRequested")){return document.location=".",!0}if(e.getResponseHeader("JasperServerError")){if(!e.getResponseHeader("SuppressError"))if(1==i()(".dashboardViewFrame").length)i()(document.body).html(e.responseText),i()("#"+o.hl.fid,window.parent.document).removeClass("hidden").show();else u(e.responseText);return!0}return!1}function f(){}function p(){}}}]);
//# sourceMappingURL=chunk.6595.js.map