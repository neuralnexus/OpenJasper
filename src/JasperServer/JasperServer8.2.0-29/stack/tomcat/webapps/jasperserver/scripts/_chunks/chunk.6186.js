(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[6186],{6660:(t,e,i)=>{"use strict";i.r(e);var n=i(52499),o=i(33806),s=i(64155),r=i(97836),a=i.n(r),l=i(236),d=i(11129);void 0===window.Administer&&(window.Administer={}),window.Administer=a().extend({_messages:{},urlContext:null,getMessage:function(t,e){var i=this._messages[t];return i?new n.Template(i).evaluate(e||{}):""},menuActions:{"p#navAnalysisOptions":function(){return window.Administer.urlContext+"/flow.html?_flowId=mondrianPropertiesFlow"},"p#navAnalysisOptionsCE":function(){return window.Administer.urlContext+"/olap/properties.html"},"p#navDesignerOptions":function(){return window.Administer.urlContext+"/flow.html?_flowId=designerOptionsFlow"},"p#navDesignerCache":function(){return window.Administer.urlContext+"/flow.html?_flowId=designerCacheFlow"},"p#navAwsSettings":function(){return window.Administer.urlContext+"/flow.html?_flowId=awsSettingsFlow"},"p#navLogSettings":function(){return window.Administer.urlContext+"/log_settings.html"},"p#navGeneralSettings":function(){return window.Administer.urlContext+"/flow.html?_flowId=generalSettingsFlow"},"p#logCollectors":function(){return window.Administer.urlContext+"/logCollectors.html"},"p#navImport":function(){return window.Administer.urlContext+"/adminImport.html"},"p#navExport":function(){return window.Administer.urlContext+"/adminExport.html"},"p#navCustomAttributes":function(){return window.Administer.urlContext+"/customAttributes.html"},"p#navResetSettings":function(){return window.Administer.urlContext+"/resetSettings.html"}},_sendRequest:function(t,e,i){(0,l.mc)(t,{postData:e,callback:i,mode:l.w8.prototype.EVAL_JSON,errorHandler:this._errorHandler})},_errorHandler:function(t){return t.getResponseHeader("LoginRequested")?(window.location="flow.html?_flowId=designerCacheFlow",!0):(0,d.m0)(t)}},window.Administer);const u=window.Administer;var h=i(98223),c=i(72157),p=i.n(c);const m={initialize:function(){o.Z.resizeOnClient("serverSettingsMenu","settings"),h.Z.setCurrentContext("admin"),this.initEvents()},initEvents:function(){p()("#display").on("click",(function(t){var e=t.target,i=(0,s.BwZ)(e,[o.Z.BUTTON_PATTERN],!0);if(i)for(var n in u.menuActions)if(p()(i).is(n)&&!p()(i).parents("li").hasClass("selected"))return void(document.location=u.menuActions[n]())}))}};const g={initialize:function(){m.initialize.call(this)},initEvents:function(){var t=this;m.initEvents(),p()(".js-logSettings select").on("change",(function(e){var i,n=p()(e.target);i=n.hasClass("js-newLogger")?p()("#newLoggerName").val():n.parent().prev().text(),t._setLevel(encodeURIComponent(i),n.val())}))},_setLevel:function(t,e){var i=Object.assign(document.createElement("form"),{method:"post",action:"log_settings.html"});i.appendChild(Object.assign(document.createElement("input"),{type:"hidden",name:"logger",value:t})),i.appendChild(Object.assign(document.createElement("input"),{type:"hidden",name:"level",value:e})),document.body.appendChild(i),i.submit()}};var f=i(71914),w=i(15892),b=i.n(w),v=i(63739),_=i(31371);const E=b().View.extend({events:{"click #exportButton":"doExport"},initialize:function(){this.exportView=new v.Z,this.exportView.render({type:f.Z.isProVersion?_.Z.SERVER_PRO:_.Z.SERVER_CE,tenantId:f.Z.isProVersion?"organizations":null}),this.$el.find(".body").append(this.exportView.el),this.listenTo(this.exportView.model,"validated",(function(t){var e=t?null:"disabled";this.$("#exportButton").attr("disabled",e)}),this)},doExport:function(){this.exportView.doExport()}});var x=i(94277);x.hl.Log=void(window.console||(window.console={log:function(){},debug:function(){},error:function(){}}));x.hl.Log;var I=i(67e3),y=i(52687),C=b().View.extend({rendered:!1,contentSelector:".body",events:{"click .cancel":"hide"},initialize:function(t){if(this.templateId=t&&t.templateId||this.templateId,this.contentSelector=t&&t.contentSelector||this.contentSelector,!this.templateId)throw"Dialog template is not provided";a().bindAll(this,"render","hide","show","setContent","_updateMessage"),this.options=a().extend({},t)},render:function(t){return this.undelegateEvents(),this.$el=p()(I.Z.getTemplateText(this.templateId)).closest("div"),this.el=this.$el[0],p()(t||document.body).append(this.$el),this.delegateEvents(),this.rendered=!0,this},hide:function(t){y.Z.popup.hide(this.el),t&&t.stopPropagation()},show:function(t){this.rendered||this.render(t),y.Z.popup.show(this.el,this.options.modal)},setContent:function(t){this.$el.find(this.contentSelector).html(p()(t))},_updateMessage:function(t){t=a().isString(t)?[t]:t;var e=document.createDocumentFragment();a().each(t||[],(function(t){e.appendChild(p()("<p></p>",{text:t,class:"message"})[0])}),this),this.$el.find(".body").html(e)}}),A=C.extend({templateId:"standardConfirmTemplate",events:{"click button.cancel":"hide","click button.ok":"onOk"},initialize:function(t){C.prototype.initialize.call(this,t),a().extend(this,a().defaults(t||{},{messages:"",ok:function(){}}))},show:function(t){C.prototype.show.call(this),this._updateMessage(t.messages||this.messages),t.ok&&(this.ok=t.ok)},onOk:function(){this.hide(),this.ok()}});x.MP.components.Dialog=C,x.MP.components.ConfirmDialog=A;u.urlContext=f.Z.urlContext,g.initialize(),new E({el:document.getElementById("settings")})},94277:(t,e,i)=>{"use strict";i.d(e,{MP:()=>r,hl:()=>n,YT:()=>s});var n={vars:{element_scrolled:!1,ajax_in_progress:!1,current_flow:null,contextPath:__jrsConfigs__.contextPath},i18n:{}};if(void 0===o)var o=!1;function s(){return __jrsConfigs__.isProVersion}var r={components:{},i18n:{}};void 0===n&&(n={Mocks:{}}),void 0===n.vars&&(n.vars={element_scrolled:!1,ajax_in_progress:!1,current_flow:null}),void 0===n.Export&&(n.Export={i18n:{"file.name.empty":"export.file.name.empty","file.name.too.long":"export.file.name.too.long","file.name.not.valid":"export.file.name.not.valid","export.select.users":"export.select.users","export.select.roles":"export.select.roles","export.session.expired":"export.session.expired","error.timeout":"export.file.name.empty"},configs:{TIMEOUT:12e5,DELAY:3e3}}),void 0===window.localContext&&(window.localContext={}),__jrsConfigs__.calendar&&(n.i18n.bundledCalendarFormat=__jrsConfigs__.calendar.i18n.bundledCalendarFormat,n.i18n.bundledCalendarTimeFormat=__jrsConfigs__.calendar.i18n.bundledCalendarTimeFormat),window.JRS=n,window.jaspersoft=r,window.isProVersion=s},24777:(t,e,i)=>{"use strict";i.d(e,{Z:()=>n});const n={}},89560:(t,e,i)=>{"use strict";i.d(e,{Z:()=>g});var n=i(97836),o=i.n(n),s=i(72157),r=i.n(s),a=i(42994),l=i(10709),d=i(59661),u=i(43891),h=i(35499),c=i(37567),p=(i(68851),i(44768)),m=l.Z.extend({defaultTemplate:h.Z,events:{mousedown:"_focus",touchstart:"_focus",keydown:"_onKeyDown",keyup:"_onKeyboardEvent",keypress:"_onKeyboardEvent",resize:"_onDialogResize"},constructor:function(t){t||(t={}),t.traits||(t.traits=[]),this.resizable=t.resizable||!1,this.modal=t.modal||!1,this.id=t.id,this.titleContainerId=t.titleContainerId,this.bodyContainerId=t.bodyContainerId,this.additionalBodyCssClasses=t.additionalBodyCssClasses||"",this.returnFocusTo=t.returnFocusTo||!1,this.resizable&&-1===o().indexOf(t.traits,u.Z)&&t.traits.push(u.Z),l.Z.call(this,t)},initialize:function(t){this.dialogOptions=o().extend({},t),this.collapsed=!this.collapsed,this.resetSizeOnOpen=!!o().isUndefined(t.resetSizeOnOpen)||t.resetSizeOnOpen,o().isEmpty(t.buttons)||(this.buttons=new d.Z({options:t.buttons,el:this.$(".jr-mDialog-footer")[0]||this.$(".footer")[0],contextName:"button",optionTemplate:t.dialogButtonTemplate||c.Z})),l.Z.prototype.initialize.apply(this,arguments),this.buttons&&this.listenTo(this.buttons,o().map(t.buttons,(function(t){return"button:"+t.action})).join(" "),o().bind((function(t,e){this.trigger("button:"+e.get("action"),t,e)}),this)),this.render()},getTemplateArguments:function(){var t=l.Z.prototype.getTemplateArguments.apply(this,arguments);return o().extend(t,{id:this.id,titleContainerId:this.titleContainerId,bodyContainerId:this.bodyContainerId,additionalBodyCssClasses:this.additionalBodyCssClasses})},setElement:function(t){var e=l.Z.prototype.setElement.apply(this,arguments);return this.buttons&&this.buttons.setElement(this.$(".jr-mDialog-footer")[0]||this.$(".footer")[0]),e},setTitle:function(t){this.$(".jr-mDialog-header > .jr-mDialog-header-title").text(t),this.$el.attr("aria-label",t)},setReturnFocus:function(t){this.returnFocusTo=t},render:function(){return this.$el.hide(),this.modal&&(this.dimmer=new a.Z({zIndex:900})),r()("body").append(this.$el),this.$el.draggable({handle:".mover",addClasses:!1,containment:"document"}),this},open:function(t){if(this.isVisible())return this;this.resetSizeOnOpen&&(this.$el.css({height:"",width:""}),this.$el.find("textarea").css({height:"",width:""})),l.Z.prototype.open.apply(this,arguments),this.modal&&this.dimmer.css({zIndex:++m.highestIndex}).show(),this._setMinSize();var e=this._position(t);this.$el.css({top:e.top,left:e.left,position:"absolute"}),this._increaseZIndex(),this.buttons&&this.buttons.$(".over").removeClass("over"),this.$el.show(),this.lastFocusedElement=this.returnFocusTo||document.activeElement;var i=p.Z.getFirstFocusableElement(this.$el[0]);return i&&o().defer((function(){return i.focus()})),this._onDialogResize(),this.trigger("dialog:visible"),p.Z.beginModalFocus(this.$el[0]),this},close:function(){if(this.isVisible())return p.Z.endModalFocus(this.$el[0]),this.$el.css({zIndex:--m.highestIndex}),this.modal&&this.dimmer.css({zIndex:--m.highestIndex}).hide(),this.$el.hide(),this.lastFocusedElement&&this.lastFocusedElement.focus(),l.Z.prototype.close.apply(this,arguments),this},addCssClasses:function(t){this.$el.addClass(t)},toggleCollapsedState:function(){return this},enableButton:function(t){this.buttons.enable(t)},disableButton:function(t){this.buttons.disable(t)},isVisible:function(){return this.$el.is(":visible")},_setMinSize:function(){this.dialogOptions.minWidth&&this.$el.css({minWidth:this.dialogOptions.minWidth}),this.dialogOptions.minHeight&&this.$el.css({minHeight:this.dialogOptions.minHeight}),this.dialogOptions.setMinSizeAsSize&&this.$el.css({width:this.dialogOptions.minWidth,height:this.dialogOptions.minHeight})},_position:function(t){var e,i,n=r()("body"),o=this.$el.height(),s=this.$el.width();if(t&&void 0!==t.top&&void 0!==t.left){e=t.top,i=t.left;var a=n.height(),l=n.width(),d=a-t.top,u=l-t.left;d<o&&(e=(e=t.top-o)<0?a/2-o/2:e),u<s&&(i=(i=t.left-s)<0?l/2-s/2:i)}else e=r()(window).height()/2-o/2,i=r()(window).width()/2-s/2;return{top:Math.max(0,e),left:Math.max(0,i)}},_focus:function(){!this.modal&&this._increaseZIndex()},_increaseZIndex:function(){this.$el.css({zIndex:++m.highestIndex})},_onKeyDown:function(t){("Escape"===t.key||27===t.keyCode)&&this.buttons&&this.dialogOptions.buttons.length>0&&this.close(),this.buttons&&this.buttons._onKeyDown(t),t.stopPropagation()},_onKeyboardEvent:function(t){t.stopPropagation()},_onDialogResize:function(){},remove:function(){this.buttons&&this.buttons.remove(),this.dimmer&&this.dimmer.remove();try{this.$el.draggable("destroy")}catch(t){}l.Z.prototype.remove.call(this)}},{highestIndex:5e3,resetHighestIndex:function(t){m.highestIndex=t||5e3}});const g=m},59772:(t,e,i)=>{"use strict";i.d(e,{Z:()=>d});var n=i(5887),o=i.n(n),s=i(97836),r=i.n(s),a=new(i(92772).Z),l=o().mixin.validate;o().mixin.validate=function(t,e){e||(e={});var i=this;return l.call(this,t,r().extend({valid:function(t,e){i.trigger("validate:"+e,i,e)},invalid:function(t,e,n){i.trigger("validate:"+e,i,e,n)}},e))},r().extend(o().validators,{doesNotContainSymbols:function(t,e,i){if(new RegExp("["+i+"]","g").test(t))return"Attribute '"+e+"' contains forbidden symbols"},integerNumber:function(t){if(!a.isNumberInt(t))return"Value is not a valid integer number"},type:function(t,e,i){if(r().isArray(i)||(i=[i]),!i.some((function(e){return function(t){var e;return"string"==t?e=r().isString:"number"===t?e=r().isNumber:"object"===t?e=r().isObject:"boolean"===t?e=r().isBoolean:"null"===t?e=r().isNull:"undefined"===t&&(e=r().isUndefined),e}(e)(t)})))return"'{attr}' is not {type}".replace("{attr}",e).replace("'{type}'",i.join(" "))},url:function(t){if(!/(http|https):\/\/.*\..*./.test(t))return"Value is not a valid url"},hexColor:function(t){if(!/^#[0-9a-f]{3,6}$/i.test(t))return"Value is not a valid hex color"},xRegExpPattern:function(t,e,i,n){if(!i.test(t))return"Value does not match pattern"},startsWithLetter:function(t,e,i,n){if(!t.substr(0,1).match(/[A-Za-z]/))return"Value should start with letter"},containsOnlyWordCharacters:function(t,e,i,n){if(t.search(/\W/)>=0)return"Value should contain only word characters (letters, digits and underscore)"},arrayMinLength:function(t,e,i,n){if(r().isArray(t)&&t.length<i)return"Array length is less than "+i}});const d=o()},81180:(t,e,i)=>{"use strict";i.d(e,{Z:()=>d});var n=i(97836),o=i.n(n),s=i(15892);const r={100:"continue",101:"switchingProtocols",200:"ok",201:"created",202:"accepted",203:"nonAuthoritativeInformation",204:"noContent",205:"resetContent",206:"partialContent",300:"multipleChoices",301:"movedPermanently",302:"found",303:"seeOther",304:"notModified",305:"useProxy",307:"temporaryRedirect",400:"badRequest",401:"unauthorized",402:"paymentRequired",403:"forbidden",404:"notFound",405:"methodNotAllowed",406:"notAcceptable",407:"proxyAuthenticationRequired",408:"requestTimeout",409:"conflict",410:"gone",411:"lengthRequired",412:"preconditionFailed",413:"requestEntityTooLarge",414:"requestUriTooLong",415:"unsupportedMediaType",416:"requestedRangeNotSatisfiable",417:"expectationFailed",500:"internalServerError",501:"notImplemented",502:"badGateway",503:"serviceUnavailable",504:"gatewayTimeout",505:"httpVersionNotSupported"},a="unexpected.error";var l=i.n(s)().Model.extend({initialize:function(){this.on("error",l.unifyServerErrors)},serialize:function(){return o().clone(this.attributes)}},{unifyServerErrors:function(t,e){var i=r[e.status],n=l.createServerError(e);t.trigger("error:"+i,t,n,e),t.trigger("error:all",t,n,e)},createServerError:function(t){var e;try{e=JSON.parse(t.responseText)}catch(t){e={message:"Can't parse server response",errorCode:a,parameters:[]}}return e}});const d=l},92772:(t,e,i)=>{"use strict";i.d(e,{Z:()=>l});var n=i(97836),o=i.n(n),s=function(t){function e(t){return"."===t?t="\\.":" "===t&&(t="\\s"),t}t=t||{},this.DECIMAL_SEPARATOR=t.decimalSeparator||".",this.GROUPING_SEPARATOR=t.groupingSeparator||",",this.decimalSeparator=e(this.DECIMAL_SEPARATOR),this.groupingSeparator=e(this.GROUPING_SEPARATOR),this.DECIMAL_NUMBER_PATTERN=new RegExp("^-?([1-9]{1}[0-9]{0,2}("+this.groupingSeparator+"[0-9]{3})*("+this.decimalSeparator+"[0-9]+)?|[1-9]{1}[0-9]{0,}("+this.decimalSeparator+"[0-9]+)?|0("+this.decimalSeparator+"[0-9]+)?)$"),this.INTEGER_NUMBER_PATTERN=new RegExp("^-?([1-9]{1}[0-9]{0,2}("+this.groupingSeparator+"[0-9]{3})*|[1-9]{1}[0-9]{0,}|0)$")},r=Number.MAX_SAFE_INTEGER?Number.MAX_SAFE_INTEGER+1:9007199254740992,a=Number.MIN_SAFE_INTEGER?Number.MIN_SAFE_INTEGER-1:-9007199254740992;s.prototype.number_format=function(t,e,i,n){t=(t+"").replace(/[^0-9+\-Ee.]/g,"");var o,s,r,a=isFinite(+t)?+t:0,l=isFinite(+e)?Math.abs(e):0,d=void 0===n?",":n,u=void 0===i?".":i,h=(l?(o=a,s=l,r=Math.pow(10,s),""+(Math.round(o*r)/r).toFixed(s)):""+Math.round(a)).split(".");return h[0].length>3&&(h[0]=h[0].replace(/\B(?=(?:\d{3})+(?!\d))/g,d)),(h[1]||"").length<l&&(h[1]=h[1]||"",h[1]+=new Array(l-h[1].length+1).join("0")),h.join(u)},s.prototype.isInt=function(t){return this.isNumberInt(t)||this.isStringInt(t)},s.prototype.isNumberInt=function(t){return o().isNumber(t)&&!isNaN(t)&&t!==Number.POSITIVE_INFINITY&&t!==Number.NEGATIVE_INFINITY&&(t===+t&&t===(0|t)||t%1==0)},s.prototype.isStringInt=function(t){return o().isString(t)&&this.INTEGER_NUMBER_PATTERN.test(t)},s.prototype.isInt32=function(t){return this.isInt(t)&&this.parseNumber(t)>=-2147483648&&this.parseNumber(t)<=2147483647},s.prototype.isDecimal=function(t){return o().isNumber(t)&&!isNaN(t)&&t!==Number.POSITIVE_INFINITY&&t!==Number.NEGATIVE_INFINITY||o().isString(t)&&this.DECIMAL_NUMBER_PATTERN.test(t)},s.prototype.parseNumber=function(t){if(o().isNumber(t))return t;if(o().isString(t)&&this.DECIMAL_NUMBER_PATTERN.test(t)){var e=1*(t=t.replace(new RegExp(this.groupingSeparator,"g"),"").replace(new RegExp(this.decimalSeparator,"g"),"."));return e>=r||e<=a?(window.console&&window.console.warn(t+" is out of the ["+(a+1)+", "+(r-1)+"] bounds. Parsing results may be corrupted. Use string representation instead. For more details see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number."),!1):e}},s.prototype.formatNumber=function(t,e){e=e||{};var i=t.toString().split(".")[1],n=i?i.length:0;o().isUndefined(e.decimalPlacesAmount)||(n=e.decimalPlacesAmount);try{return this.number_format(t,n,e.decimalSeparator||this.DECIMAL_SEPARATOR,e.groupingSeparator||this.GROUPING_SEPARATOR)}catch(e){return t.toString()}},s.prototype.MaxRange=Number.MAX_SAFE_INTEGER?Number.MAX_SAFE_INTEGER+1:9007199254740992,s.prototype.MinRange=Number.MIN_SAFE_INTEGER?Number.MIN_SAFE_INTEGER-1:-9007199254740992;const l=s}}]);
//# sourceMappingURL=chunk.6186.js.map