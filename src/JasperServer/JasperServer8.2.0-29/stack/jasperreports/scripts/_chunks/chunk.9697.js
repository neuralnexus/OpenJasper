(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[9697,7335,5573,6595,8522,236],{17335:(e,t,a)=>{var _,n,r;
/*!
 * jQuery UI Scroll Parent 1.13.2
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */!function(s){"use strict";n=[a(72157),a(91544)],void 0===(r="function"==typeof(_=function(e){return e.fn.scrollParent=function(t){var a=this.css("position"),_="absolute"===a,n=t?/(auto|scroll|hidden)/:/(auto|scroll)/,r=this.parents().filter((function(){var t=e(this);return(!_||"static"!==t.css("position"))&&n.test(t.css("overflow")+t.css("overflow-y")+t.css("overflow-x"))})).eq(0);return"fixed"!==a&&r.length?r:e(this[0].ownerDocument||document)}})?_.apply(t,n):_)||(e.exports=r)}()},236:(__unused_webpack_module,__webpack_exports__,__webpack_require__)=>{"use strict";__webpack_require__.d(__webpack_exports__,{mc:()=>ajaxTargettedUpdate,Pw:()=>ajaxNonReturningUpdate,Z8:()=>appendPostData});var jquery__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(72157),jquery__WEBPACK_IMPORTED_MODULE_0___default=__webpack_require__.n(jquery__WEBPACK_IMPORTED_MODULE_0__),_components_components_dialogs__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(52687),_util_utils_common__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(64155),_core_ajax_utils__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(11129),_namespace_namespace__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(94277),js_sdk_src_common_util_xssUtil__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(84612),scriptaculous_src_builder__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(9026),scriptaculous_src_builder__WEBPACK_IMPORTED_MODULE_6___default=__webpack_require__.n(scriptaculous_src_builder__WEBPACK_IMPORTED_MODULE_6__),ajax={};function AjaxRequester(e,t,a,_){this.url=e||document.location.toString(),this.params=t,this.xmlhttp=getXMLHTTP();var n=this.processResponse(this);this.xmlhttp.onreadystatechange=n,this.postData=a,this.async=!_,this.requestTime=+new Date}function handleResponse(e){checkForErrors(e)||e.responseHandler(e),ajaxRequestEnded(e),_namespace_namespace__WEBPACK_IMPORTED_MODULE_4__.hl&&_namespace_namespace__WEBPACK_IMPORTED_MODULE_4__.hl.vars&&(_namespace_namespace__WEBPACK_IMPORTED_MODULE_4__.hl.vars.ajax_in_progress=!1),document.getElementById("mainTableContainerOverlay")&&(document.getElementById("mainTableContainerOverlay").className="hidden")}function targettedResponseHandler(e){var t=e.xmlhttp,a=e.params[0],_=e.params[1],n=e.params[2],r=jquery__WEBPACK_IMPORTED_MODULE_0___default()("#"+a)[0];if(_)jquery__WEBPACK_IMPORTED_MODULE_0___default()(r).html(""),updateUsingResponseSubset(t,_,r);else{var s=t.responseText.trim();if(0===s.indexOf("<div")){var o=s.substring(0,s.indexOf(">")+1),i=s.substring(s.indexOf(">")+1,s.lastIndexOf("</")),u=s.substring(s.lastIndexOf("</")),p=jquery__WEBPACK_IMPORTED_MODULE_0___default()(o+u);i=i.replace(/&quot;/g,"&amp;quot;"),p.text(js_sdk_src_common_util_xssUtil__WEBPACK_IMPORTED_MODULE_5__.Z.hardEscape(i)),jquery__WEBPACK_IMPORTED_MODULE_0___default()(r).html(p)}else jquery__WEBPACK_IMPORTED_MODULE_0___default()(r).html(s)}invokeCallbacks(n,r)}function cumulativeResponseHandler(e){var t=e.xmlhttp,a=e.params[0],_=e.params[1],n=e.params[2],r=jquery__WEBPACK_IMPORTED_MODULE_0___default()("#"+a)[0];_?updateUsingResponseSubset(t,_,r):r.insert(t.responseText,{position:"after"}),invokeCallbacks(n)}function rowCopyResponseHandler(e){var t=e.xmlhttp,a=e.params[0],_=e.params[2],n=jquery__WEBPACK_IMPORTED_MODULE_0___default()("#"+a)[0];if("TABLE"===n.tagName){var r=scriptaculous_src_builder__WEBPACK_IMPORTED_MODULE_6___default().node("DIV");jquery__WEBPACK_IMPORTED_MODULE_0___default()(r).html(t.responseText),(0,_util_utils_common__WEBPACK_IMPORTED_MODULE_2__.SG5)(r.getElementsByTagName("table")[0],n,!1,!1),invokeCallbacks(_)}else alert("Ajax Exception: rowCopyResponseHandler will not work for container "+n.tagName)}ajax.cancelRequestsBefore,ajax.LOADING_ID="loading",AjaxRequester.addVar("CUMULATIVE_UPDATE","c").addVar("ROW_COPY_UPDATE","r").addVar("TARGETTED_REPLACE_UPDATE","t").addVar("EVAL_JSON","j").addVar("DUMMY_POST_PARAM","dummyPostData").addVar("MAX_WAIT_TIME",2e3).addVar("errorHandler",(function(){return!1})).addMethod("doGet",(function(){return!!this.xmlhttp&&(this.xmlhttp.open("GET",this.url,this.async),this.xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded"),this.xmlhttp.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000 00:00:00 GMT"),this.xmlhttp.setRequestHeader("x-requested-with","AJAXRequest"),this.xmlhttp.send(null),!0)})).addMethod("doPost",(function(){return!!this.xmlhttp&&(this.postData===AjaxRequester.prototype.DUMMY_POST_PARAM&&(this.postData=null),this.xmlhttp.open("POST",this.url,this.async),this.xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded"),this.xmlhttp.setRequestHeader("x-requested-with","AJAXRequest"),this.xmlhttp.send(this.postData),!0)})).addMethod("processResponse",(function(e){return function(){if(4==e.xmlhttp.readyState){if(ajax.cancelRequestsBefore&&ajax.cancelRequestsBefore>e.requestTime)return void ajaxRequestEnded(e);handleResponse(e)}}})).addMethod("setErrorHandler",(function(e){this.errorHandler=_core_ajax_utils__WEBPACK_IMPORTED_MODULE_3__.Po})).addMethod("verifyAjaxResponse",(function(){return this.xmlhttp.getResponseHeader("Server")||this.confirmContinue()})).addMethod("startResponseTimer",(function(){this.responseTimer=window.setTimeout((function(){_components_components_dialogs__WEBPACK_IMPORTED_MODULE_1__.Z.popup.show(jquery__WEBPACK_IMPORTED_MODULE_0___default()("#"+ajax.LOADING_ID)[0],!0)}),this.MAX_WAIT_TIME)})).addMethod("cancelResponseTimer",(function(){window.clearTimeout(this.responseTimer)})).addMethod("confirmContinue",(function(){return confirm(window.serverIsNotResponding)}));var clobberingResponseHandler=function(e){var t=e.params[2];jquery__WEBPACK_IMPORTED_MODULE_0___default()(document.body).html(e.xmlhttp.responseText),document.fire("dom:loaded"),invokeCallbacks(t)},evalJSONResponseHandler=function(e){var t=null;try{t=e.xmlhttp.responseText.evalJSON()}catch(e){window.console&&console.log(e)}invokeCallbacks(e.params[2],t)};function updateUsingResponseSubset(e,t,a){var _=jquery__WEBPACK_IMPORTED_MODULE_0___default()(e.responseText),n=_.filter("#"+t);if("TABLE"==a.tagName&&"TABLE"==jquery__WEBPACK_IMPORTED_MODULE_0___default()("#"+t)[0].tagName?(0,_util_utils_common__WEBPACK_IMPORTED_MODULE_2__.M$o)(n,a,!0):jquery__WEBPACK_IMPORTED_MODULE_0___default()(a).append(n.html()),void 0!==jquery__WEBPACK_IMPORTED_MODULE_0___default()){var r=_.filter("script.jasperreports"),s=r.size(),o=0;!function e(){if(!(o>=s)){var t=jquery__WEBPACK_IMPORTED_MODULE_0___default()(r.get(o));t.attr("src")?(o++,a=t.attr("data-custname"),_=t.attr("src"),i=(n=e)||!1,u=document.createElement("script"),window.jr_scripts||(window.jr_scripts={}),window.jr_scripts[a]||"jr_jq_min"===a?i&&n():(u.setAttribute("type","text/javascript"),u.readyState?u.onreadystatechange=function(){"loaded"!==u.readyState&&"complete"!==u.readyState||(u.onreadystatechange=null,i&&n())}:u.onload=function(){i&&n()},u.src=_,document.getElementsByTagName("head")[0].appendChild(u),window.jr_scripts[a]=_)):(o++,function(e,t){var a=t||!1;if(e){var _=e.match(/^.*((\r\n|\n|\r)|$)/gm);window.eval(_.join("\n")),a&&t()}}(t.html(),e))}var a,_,n,i,u}()}}function invokeCallbacks(callback,customArg){callback&&("string"==typeof callback?eval(callback):callback(customArg))}function ajaxClobberredUpdate(e,t){t.responseHandler=clobberingResponseHandler,ajaxUpdate(e,t)}function ajaxTargettedUpdate(url,options){var responseHandler;responseHandler=options.mode==AjaxRequester.prototype.CUMULATIVE_UPDATE?cumulativeResponseHandler:options.mode==AjaxRequester.prototype.ROW_COPY_UPDATE?rowCopyResponseHandler:options.mode==AjaxRequester.prototype.EVAL_JSON?evalJSONResponseHandler:targettedResponseHandler,options.responseHandler=function(requester,params){options.preFillAction?"string"==typeof options.preFillAction?eval(options.preFillAction):options.preFillAction(responseHandler(requester,params)):responseHandler(requester,options.params)},ajaxUpdate(url,options)}function ajaxNonReturningUpdate(e,t){t.responseHandler=null,ajaxUpdate(e,t)}function ajaxClobberedFormSubmit(e,t,a){var _=getPostData(e,window.extraPostData);a.postData=appendPostData(_,window.extraPostData),a.responseHandler=clobberingResponseHandler,ajaxUpdate(t,a)}function ajaxTargettedFormSubmit(e,t,a){var _=getPostData(e,a.extraPostData);a.postData=appendPostData(_,a.extraPostData),a.responseHandler=targettedResponseHandler,ajaxUpdate(t,a)}function ajaxErrorHandler(){(0,_core_ajax_utils__WEBPACK_IMPORTED_MODULE_3__.mr)(window.ajaxError,window.ajaxErrorHeader)}function doNothing(){}function ajaxUpdate(e,t){var a=new AjaxRequester(e,[t.fillLocation,t.fromLocation,t.callback,t.isAutomaticRefresh],t.postData,t.synchronous);(0,_util_utils_common__WEBPACK_IMPORTED_MODULE_2__.zcy)()&&"adhoc"==_namespace_namespace__WEBPACK_IMPORTED_MODULE_4__.hl.vars.current_flow&&(_namespace_namespace__WEBPACK_IMPORTED_MODULE_4__.hl.vars.ajax_in_progress||(_namespace_namespace__WEBPACK_IMPORTED_MODULE_4__.hl.vars.ajax_in_progress=!0)),a.busyCursor=!t.silent,a.showLoading=!t.silent&&!t.hideLoader,a.responseHandler=t.responseHandler||doNothing,a.responseHandler!=doNothing&&ajaxRequestStarted(a),t.errorHandler&&(a.errorHandler=t.errorHandler),a.xmlhttp&&(t.postData?a.doPost():a.doGet())}function checkForErrors(e){return(0,e.errorHandler)(e.xmlhttp)}function getPostData(e,t){"string"==typeof e&&(e=document.forms[e]);for(var a="",_=0;_<e.elements.length;++_){var n=e.elements[_];!n.name||t&&t[n.name]||(a=appendFormInput(a,n))}return a}function appendPostData(e,t){for(var a in t)e=appendFormValue(e,a,t[a]);return e}function appendFormInput(e,t){if(t.name){var a,_=!1;switch(t.type){case"checkbox":case"radio":_=t.checked,a=t.value;break;case"hidden":case"password":case"text":case"Textarea":_=!0,a=t.value;break;case"select-one":case"select-multiple":a=[];for(var n=0;n<t.options.length;++n){var r=t.options[n];r.selected&&(_=!0,a.push(r.value))}}if(_)if(a.shift)for(;a.length>0;)e=appendFormValue(e,t.name,a.shift());else e=appendFormValue(e,t.name,a)}return e}function appendFormValue(e,t,a){return e.length>0&&(e+="&"),e+=t+"="+encodeURIComponent(a)}var ERROR_POPUP_DIV="jsErrorPopup",ERROR_POPUP_CONTENTS="errorPopupContents",ERROR_POPUP_BACK_BUTTON="errorBack",ERROR_POPUP_CLOSE_BUTTON="errorPopupCloseButton";function hideErrorPopup(){(0,_util_utils_common__WEBPACK_IMPORTED_MODULE_2__.JHn)(),document.getElementById(ERROR_POPUP_DIV).style.display="none"}function ajaxRequestStarted(e){++ajax.ajaxRequestCount,e.busyCursor&&(document.body.style.cursor="wait"),!(0,_util_utils_common__WEBPACK_IMPORTED_MODULE_2__.zcy)()&&e.showLoading&&e.startResponseTimer()}function ajaxRequestEnded(e){e.cancelResponseTimer(),ajax.ajaxRequestCount<=1?(document.body.style.cursor="auto",ajax.ajaxRequestCount=0,_components_components_dialogs__WEBPACK_IMPORTED_MODULE_1__.Z.popup.hide(jquery__WEBPACK_IMPORTED_MODULE_0___default()("#"+ajax.LOADING_ID)[0])):ajax.ajaxRequestCount--}function isValidJsonResponse(e){var t=e.getResponseHeader("Content-Type");return 200==e.status&&null!=t&&t.indexOf("application/json")>=0}function getXMLHTTP(){var e;
/*@cc_on @*/
/*@cc_on @*/
if(!e)try{e=new XMLHttpRequest}catch(e){alert("You need a browser which supports an XMLHttpRequest Object.\nMozilla build 0.9.5 has this Object and IE5 and above, others may do, I don't know, any info jim@jibbering.com")}return e}ajax.ajaxRequestCount=0,window.ajaxNonReturningUpdate=ajaxNonReturningUpdate},11129:(e,t,a)=>{"use strict";a.d(t,{m0:()=>i,Po:()=>u,mr:()=>p});var _=a(52687),n=a(94277),r=a(72157),s=a.n(r);function o(e,t){(e&&e.indexOf("sessionAttributeMissingException"))>-1?_.Z.clusterErrorPopup.show(e):_.Z.errorPopup.show(e,!1,t)}function i(e){if(500==e.status)return o(e.responseText),!0;if(e.getResponseHeader("LoginRequested")){return document.location=".",!0}if(e.getResponseHeader("JasperServerError")){if(!e.getResponseHeader("SuppressError"))if(1==s()(".dashboardViewFrame").length)s()(document.body).html(e.responseText),s()("#"+n.hl.fid,window.parent.document).removeClass("hidden").show();else o(e.responseText);return!0}return!1}function u(){}function p(){}}}]);
//# sourceMappingURL=chunk.9697.js.map