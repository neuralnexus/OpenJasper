(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[7926],{67256:(t,e,i)=>{"use strict";i.r(e);var s=i(72157),n=i.n(s),o=i(33806),r=i(52499),a=i(11364),l=i(64155),h=i(43852),c=i(46539),d={messages:{},resourceLabelMaxLength:100,resourceIdMaxLength:100,resourceDescriptionMaxLength:250,PROPAGATE_EVENT:"propagateEvent",STEP_DISPLAY_ID:"stepDisplay",FLOW_CONTROLS_ID:"flowControls",initSwipeScroll:function(){var t=n()("#"+d.STEP_DISPLAY_ID)[0];t&&new a.Z(t.parent(),t.parents().eq(2),{})},submitForm:function(t,e,i){if(e){var s=(0,l.nB6)(e);i&&i(),n()("#"+t).attr("method","post").attr("action",s),n()("#"+t)[0].submit()}},registerClickHandlers:function(t,e,i){if(d._bodyClickEventHandlers)(i?Array.prototype.unshift:Array.prototype.push).apply(d._bodyClickEventHandlers,t);else{d._bodyClickEventHandlers=t;var s=e||"body";n()(s).on("click",(function(t){d._bodyClickEventHandlers&&d._bodyClickEventHandlers.each((function(e){var i=e(t);if(i)throw i!==d.PROPAGATE_EVENT&&Event.stop(t),r.$break}))}))}},TreeWrapper:function(t){var e=this;if(this._treeId=t.treeId,this._resourceUriInput=n()("#"+t.resourceUriInput||0)[0],this._uri=this._resourceUriInput&&this._resourceUriInput.getValue()||t.uri||"/",!t.providerId)throw"There is no tree provider set for tree #{id}".interpolate({id:this._treeId});var i=["providerId","rootUri","organizationId","publicFolderUri","urlGetNode","urlGetChildren"].inject({},(function(e,i){return null!==t[i]&&(e[i]=t[i]),e}));return this._tree=new c.Z.createRepositoryTree(this._treeId,i),this._tree.observe("tree:loaded",(function(){e._tree.openAndSelectNode(n()("#"+e._resourceUriInput)[0].getValue())})),this._tree.observe("leaf:selected",(function(t){e._uri=t.memo.node.param.uri,e._resourceUriInput.setValue(e._uri)})),this._tree.observe("node:selected",(function(){e._resourceUriInput.setValue(e._uri="")})),{getTreeId:function(){return e._treeId},getTree:function(){return e._tree},selectFolder:function(t){e._tree.openAndSelectNode(t)},getSelectedFolderUri:function(){return e._uri}}},switchButtonState:function(t,e){h.Z[e?"enable":"disable"].call(h.Z,t)},switchDisableState:function(t,e){(t=n()(t)[0])&&t[e?"disable":"enable"].call(t)},generateResourceId:function(t){if(window.localContext&&window.localContext.initOptions&&window.localContext.initOptions.resourceIdNotSupportedSymbols)return t.replace(new RegExp(window.localContext.initOptions.resourceIdNotSupportedSymbols,"g"),"_");throw"There is no resourceIdNotSupportedSymbols property in init options."},testResourceId:function(t){if(window.localContext&&window.localContext.initOptions&&window.localContext.initOptions.resourceIdNotSupportedSymbols)return new RegExp(window.localContext.initOptions.resourceIdNotSupportedSymbols,"g").test(t);throw"There is no resourceIdNotSupportedSymbols property in init options."},labelValidator:function(t){var e=!0,i="";return t.blank()?(i=d.messages.labelIsEmpty,e=!1):t.length>d.resourceLabelMaxLength&&(i=d.messages.labelToLong,e=!1),{isValid:e,errorMessage:i}},getLabelValidationEntry:function(t){return{element:t,validators:[{method:"mandatory",messages:{mandatory:d.messages.labelIsEmpty}},{method:"minMaxLength",messages:{tooLong:d.messages.labelToLong},options:{maxLength:d.resourceLabelMaxLength}}]}},getIdValidationEntry:function(t){return{element:t,validators:[{method:"resourceIdChars",messages:d.messages},{method:"mandatory",messages:{mandatory:d.messages.resourceIdIsEmpty}},{method:"minMaxLength",messages:{tooLong:d.messages.resourceIdToLong},options:{maxLength:d.resourceIdMaxLength}}]}},resourceIdValidator:function(t){var e=!0,i="";return this._isEditMode||(t.blank()?(i=d.messages.resourceIdIsEmpty,e=!1):t.length>d.resourceIdMaxLength?(i=d.messages.resourceIdToLong,e=!1):d.testResourceId(t)&&(i=d.messages.resourceIdInvalidChars,e=!1)),{isValid:e,errorMessage:i}},getDescriptionValidationEntry:function(t){return{element:t,validators:[{method:"minMaxLength",messages:{tooLong:d.messages.descriptionToLong},options:{maxLength:d.resourceDescriptionMaxLength}}]}},descriptionValidator:function(t){var e=!0,i="";return t.length>d.resourceDescriptionMaxLength&&(i=d.messages.descriptionToLong,e=!1),{isValid:e,errorMessage:i}},dataSourceValidator:function(t){var e=!0,i="";return""===t.trim()&&(i=d.messages.dataSourceInvalid,e=!1),{isValid:e,errorMessage:i}},queryValidator:function(t){var e=!0,i="";return""===t.trim()&&(i=d.messages.queryInvalid,e=!1),{isValid:e,errorMessage:i}},getValidationEntries:function(t){return t.collect((function(t){return t.validationEntry?t.validationEntry:{validator:t.validator,element:t}}))}};const p=d;const u={PAGE_ID:"addResource_dataType",DATA_TYPE_ID:"dataType\\.dataTypeType",LABEL_ID:"dataType\\.label",RESOURCE_ID_ID:"dataType\\.name",DESCRIPTION_ID:"dataType\\.description",SAVE_BUTTON_ID:"done",CHANGE_TYPE_BUTTON_ID:"changeCombo",_canGenerateId:!0,initialize:function(t){this._form=n()("#"+o.Z.PAGE_BODY_ID).find("form")[0],this._dataType=n()("#"+this.DATA_TYPE_ID)[0],this._label=n()("#"+this.LABEL_ID)[0],this._resourceId=n()("#"+this.RESOURCE_ID_ID)[0],this._description=n()("#"+this.DESCRIPTION_ID)[0],this._saveButton=n()("#"+this.SAVE_BUTTON_ID),this._changeTypeButton=n()("#"+this.CHANGE_TYPE_BUTTON_ID)[0],this._isEditMode=t.isEditMode,this._label.validator=p.labelValidator.bind(this),this._resourceId.validator=p.resourceIdValidator.bind(this),this._description.validator=p.descriptionValidator.bind(this),this._initEvents()},_initEvents:function(){this._dataType.observe("change",function(){this._changeTypeButton.click()}.bindAsEventListener(this)),this._saveButton.on("click",function(t){this._isDataValid()||t.stopPropagation()}.bindAsEventListener(this)),n()(this._form).on("keyup",function(t){var e=t.target;[this._label,this._resourceId,this._description].include(e)&&(l.n4K.validate(p.getValidationEntries([e])),e==this._resourceId&&this._resourceId.getValue()!=p.generateResourceId(this._label.getValue())&&(this._canGenerateId=!1),e==this._label&&!this._isEditMode&&this._canGenerateId&&(this._resourceId.setValue(p.generateResourceId(this._label.getValue())),l.n4K.validate(p.getValidationEntries([this._resourceId]))))}.bind(this))},_isDataValid:function(){var t=[this._label,this._resourceId,this._description];return l.n4K.validate(p.getValidationEntries(t))}};var m=i(97836),f=i.n(m),g=i(15432),T=(i(45837),function(t){this.container=null,void 0!==t.container&&(this.container=void 0===t.container.jquery?n()(t.container):t.container),this.name=t.name,this.id=t.name.replace(".","_"),this.value=t.value,this.onChange=t.onchange||null,this.isReadOnly=void 0!==t.readOnly&&t.readOnly,this.hasDate=void 0!==t.date&&""!==t.date&&"true"===t.date,this.hasTime=void 0!==t.time&&""!==t.time&&"true"===t.time,this.pickerOptions=f().extend({},this.defaultPickerOptions),this.hasDate&&f().extend(this.pickerOptions,g.Z.datepicker),this.hasTime&&f().extend(this.pickerOptions,g.Z.timepicker),void 0!==t.picker&&f().isObject(t.picker)&&f().extend(this.pickerOptions,t.picker),this.field=null});T.prototype.defaultPickerOptions={showOn:"button",buttonText:"",changeYear:!0,changeMonth:!0,showButtonPanel:!0,onChangeMonthYear:null,beforeShow:n().datepicker.movePickerRelativelyToTriggerIcon},T.prototype.create=function(){var t=n()("<input type='text'/>").attr({name:this.name,id:this.id,value:this.value});if(t.on("mousedown",l.rV9),this.onChange&&t.on("change",this.onChange),this.isReadOnly&&t.attr("disabled","disabled"),this.field=t,this.container.append(this.field),!this.isReadOnly){var e=this.hasDate?"date":"";e+=this.hasTime?"time":"",e+="picker",n().fn[e].call(t,this.pickerOptions).next().addClass("button").addClass("picker"),t[0].getValue=function(){return n()(this).val()}}};const _=T;var v=i(71914),b=v.Z.addDataType.localContext.initOptions;if(f().extend(window.localContext,v.Z.addDataType.localContext),f().extend(p.messages,v.Z.addDataType.resource.messages),f().indexOf([3,4,5],v.Z.addDataType.type)>-1){var E=new _(v.Z.addDataType.minValueText);E.container=n()("label.minPicker"),E.create();var y=new _(v.Z.addDataType.maxValueText);y.container=n()("label.maxPicker"),y.create()}u.initialize(b),(0,l.zcy)()&&p.initSwipeScroll(),n()("body").addClass("jr")},84581:(t,e,i)=>{"use strict";i.d(e,{p:()=>c,k:()=>d});var s=i(97836),n=i.n(s),o=i(64155),r=i(72157),a=i.n(r),l=i(84612),h=i(33806);function c(t,e){if(t){if(this.srcElement=t,e&&(this.label=e.label,this.text=e.text,this.offsets=e.offsets,this.showBelow=!!e.showBelow,this.templateId=e.templateId,this.loadTextCallback=e.loadTextCallback,this.loadTextExecuted=!1),this.disabled=!1,this.removed=!1,this.templateId)this._toAttribute(this.TOOLTIP_TEMPLATE,this.templateId);else{var i=this._fromAttribute(this.TOOLTIP_TEMPLATE);this.templateId=i&&i.length>0?i:this.TOOLTIP_TEMPLATE_ID}this.label?this._toAttribute(this.TOOLTIP_LABEL,this.label):this.label=this._fromAttribute(this.TOOLTIP_LABEL),this.text?this._toAttribute(this.TOOLTIP_TEXT,this.text):this.text=this._fromAttribute(this.TOOLTIP_TEXT),this.label&&(this.label=this._escapeText(this.label)),this.text&&(this.text=this._escapeText(this.text)),this.srcElement.jsTooltip=this,d.tooltips.push(this)}}c.addVar("SEPARATOR","@@"),c.addVar("TOOLTIP_LABEL","tooltiplabel"),c.addVar("TOOLTIP_TEXT","tooltiptext"),c.addVar("TOOLTIP_TEMPLATE","tooltiptemplate"),c.addVar("TOOLTIP_TEMPLATE_ID","jsTooltip"),c.addVar("LABEL_PATTERN",".message.label"),c.addVar("TEXT_PATTERN",".message:not(.label)"),c.addMethod("_toAttribute",(function(t,e){this.srcElement&&(e=l.Z.hardEscape(e),this.srcElement.writeAttribute(t,(0,o.kJL)(e)?e.join(this.SEPARATOR):e))})),c.addMethod("_fromAttribute",(function(t){if(this.srcElement&&this.srcElement.hasAttribute(t)){var e=this.srcElement.readAttribute(t);return e.include(this.SEPARATOR)?e.split(this.SEPARATOR):e}return null})),c.addMethod("_setValues",(function(t,e){t.each((function(t,i){(n().isString(e[i])||n().isNumber(e[i]))&&t.update(l.Z.hardEscape(e[i]))}))})),c.addMethod("_calculateZIndex",(function(t){function e(t){return parseInt(a()(t).css("z-index"))}var i=e(t);n().isNumber(i)&&!n().isNaN(i)||(i=1e3);var s=n().flatten([this.srcElement,a()(this.srcElement).parents().toArray()]);return n().reduce(s,(function(t,i){var s=e(i);return n().isNumber(s)&&!n().isNaN(s)&&(t=Math.max(t,s)),t}),i)+1})),c.addMethod("_escapeText",(function(t){return n().isArray(t)?n().map(t,(function(t){return l.Z.hardEscape(t)})):l.Z.hardEscape(t)})),c.addMethod("show",(function(t){var e;t&&(this.offsets=t),this._element=a()("#"+this.templateId)[0],this.showBelow?(e=(0,o.Qyk)(this.srcElement))[1]+=this.srcElement.clientHeight+5:e=[(0,o.vjD)()+5,(0,o.cxm)()+5],this.offsets&&(e[0]+=this.offsets[0],e[1]+=this.offsets[1]),this._element.setStyle({position:"absolute",left:e[0]+"px",top:e[1]+"px",zIndex:this._calculateZIndex(this._element)});var i=this._element.select(this.LABEL_PATTERN),s=this._element.select(this.TEXT_PATTERN);if(this.label&&this._setValues(i,(0,o.kJL)(this.label)?this.label:[this.label]),this.text&&this._setValues(s,(0,o.kJL)(this.text)?this.text:[this.text]),a()(this._element).removeClass(h.Z.HIDDEN_CLASS),e[0]+this._element.clientWidth>a()(window).width()){var n=e[0]-this._element.clientWidth>0?e[0]-this._element.clientWidth:15;this._element.setStyle({left:n+"px"})}if(e[1]+this._element.clientHeight>a()(window).height()){var r=e[1]-this._element.clientHeight-10;this._element.setStyle({top:r+"px"})}return this.loadTextCallback&&!this.loadTextExecuted&&(this.loadTextExecuted=!0,this.loadTextCallback(this)),this})),c.addMethod("updateText",(function(t){if(this.text=this._escapeText(t),this._element){var e=this._element.select(this.TEXT_PATTERN);this._setValues(e,(0,o.kJL)(this.text)?this.text:[this.text])}})),c.addMethod("hide",(function(){this._element&&a()(this._element).addClass(h.Z.HIDDEN_CLASS)})),c.addMethod("disable",(function(){d.hideJSTooltip(this.srcElement),this.disabled=!0})),c.addMethod("enable",(function(){this.disabled=!1,d.showJSTooltip(this.srcElement,this.offsets)})),c.addMethod("remove",(function(){var t=d.tooltips.indexOf(this.srcElement.jsTooltip);-1!==t&&(d.hideJSTooltip(this.srcElement),d.tooltips.splice(t,1)),this.removed=!0})),c.addMethod("isRemoved",(function(){return this.removed}));var d={TOOLTIP_PATTERN:"*[tooltiptext] > *",ELEMENT_WITH_TOOLTIP_PATTERN:"*[tooltiptext]",tooltips:[],showJSTooltip:function(t,e){if(t.jsTooltip){if(!t.jsTooltip.disabled&&e){var i=e[0],s=e[1];this.cleanUp();var n=a()(t).attr("tooltipappeardelay");n=n?parseInt(n):1e3,t.jsTooltip.timer&&clearTimeout(t.jsTooltip.timer),t.jsTooltip.timer=setTimeout((function(){t.jsTooltip.show([i,s])}),n),a()(t).on("mousemove",(function(t){i=t.clientX,s=t.clientY}))}}else t.jsTooltip=new c(t,{})},enableTooltips:function(){(this.tooltips||[]).forEach((function(t){t.enable()}))},disableTooltips:function(){(this.tooltips||[]).forEach((function(t){t.disable()}))},hideJSTooltip:function(t){t&&t.jsTooltip&&(t.jsTooltip.timer&&clearTimeout(t.jsTooltip.timer),t.jsTooltip.hide(),a()(t).off("mousemove"))},cleanUp:function(){if(this.tooltips&&this.tooltips.length>0){var t=[],e="fuigasuifdughaiadbvguaidbapvuwbev";this.tooltips.each((function(i){i.srcElement.id&&document.getElementById(i.srcElement.id)||(i.srcElement.setAttribute("id",e),document.getElementById(e)||(i.hide(),t.push(i)),i.srcElement.removeAttribute("id"))})),t.length>0&&(this.tooltips=this.tooltips.reject((function(e){return t.include(e)})))}}}},45837:(t,e,i)=>{"use strict";var s=i(71914),n=(i(72157),i(97836)),o=i.n(n),r=i(15432),a=i(12197);var l,h,c,d=(l=s.Z.userLocale,h=s.Z.availableLocales,c="en",l&&(o().contains(h,l)?c=l:o().contains(h,l.substring(0,2))&&(c=l.substring(0,2))),c.replace("_","-"));a.Z.setDefaults({locale:d,date:r.Z.datepicker,time:r.Z.timepicker})},24777:(t,e,i)=>{"use strict";i.d(e,{Z:()=>s});const s={}},68453:(t,e,i)=>{"use strict";var s=i(72157),n=i.n(s),o=(i(32314),"jr"),r="jr-"+n().datepicker.dpDiv.attr("id");n().datepicker._mainDivId=r,n().datepicker.dpDiv.attr("id",r),n().datepicker.dpDiv.removeClass();n().datepicker.dpDiv.addClass("jr-jDatepickerPopupContainer ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all jr");var a=n().datepicker._newInst;n().datepicker._newInst=function(){var t=a.apply(n().datepicker,arguments);return t.dpDiv.removeClass(o),t.dpDiv.addClass(o),t};var l=n().datepicker._gotoToday;n().datepicker._gotoToday=function(t){l.call(this,t),this._selectDate(t)};i(267),i(17489);i(57042);var h=n().timepicker._newInst;n().timepicker._newInst=function(t,e){e.onChangeMonthYear||(e.onChangeMonthYear=function(t,e,i,s){i.currentYear=i.selectedYear,i.currentMonth=i.selectedMonth,i.currentDay=i.selectedDay,s._updateDateTime(i)});var i=h.call(n().timepicker,t,e),s=i._onTimeChange;return i._onTimeChange=function(){return this.$timeObj[0].setSelectionRange=null,s.apply(this,arguments)},i}},12197:(t,e,i)=>{"use strict";i.d(e,{Z:()=>u});var s=i(15892),n=i.n(s),o=i(72157),r=i.n(o),a=i(97836),l=i.n(a);var h=i(62287),c=(i(68453),{stepHour:1,stepMinute:1,stepSecond:1,showSecond:!0,changeMonth:!0,changeYear:!0,showButtonPanel:!0,onChangeMonthYear:null,constrainInput:!1}),d={showSecond:!0,constrainInput:!1},p=h.Z.register("DateAndTimePicker");r().timepicker.log=function(t){p.warn(t)};const u=n().View.extend({constructor:function(t){this.pickerOptions=l().clone(t),this.pickerOptions.hasOwnProperty("dateFormat")&&!l().isString(this.pickerOptions.dateFormat)&&delete this.pickerOptions.dateFormat,this.pickerOptions.hasOwnProperty("timeFormat")&&!l().isString(this.pickerOptions.timeFormat)&&delete this.pickerOptions.timeFormat,this.inline=!!this.pickerOptions.el||!1,this.skipMoving=this.pickerOptions.skipMoving||!1,delete this.pickerOptions.el,delete this.pickerOptions.skipMoving,this.pickerType=m(this.pickerOptions),this.pickerOptions=f(this.pickerOptions,this.pickerType),this.pickerOptions=g(this.pickerOptions,this.skipMoving),this.log=t.log?t.log:p,n().View.apply(this,arguments)},initialize:function(){var t=this;this.$el[this.pickerType](this.pickerOptions),this._callPickerAction=function(e){t.$el[t.pickerType](e)}},getDate:function(){return"timepicker"!==this.pickerType?this.$el[this.pickerType].getDate():this.$el[this.pickerType].getTime()},setDate:function(t){if(!t)return this;try{l().isString(t)&&("datetimepicker"===this.pickerType?t=r().datepicker.parseDateTime(this.pickerOptions.dateFormat,this.pickerOptions.timeFormat,t):"datepicker"===this.pickerType&&(t=r().datepicker.parseDate(this.pickerOptions.dateFormat,t))),"datepicker"===this.pickerType?this.$el[this.pickerType]("setDate",t):"timepicker"===this.pickerType?this.$el[this.pickerType]("setTime",t):"datetimepicker"===this.pickerType&&(this.$el[this.pickerType]("setTime",t),this.$el[this.pickerType]("setDate",t))}catch(t){this.log.debug(t)}return this},show:function(){return this._callPickerAction("show"),this},hide:function(){return this._callPickerAction("hide"),this},remove:function(){this._callPickerAction("destroy"),this.inline?(this.$el.empty().off(),this.stopListening()):n().View.prototype.remove.apply(this,arguments)}},{setDefaults:function(t){r().datepicker.regional[t.locale]=t.date,r().datepicker.setDefaults(t.date),r().timepicker.setDefaults(t.time)},Helpers:{fixPopupPositionAndStyling:g,movePickerRelativelyToTriggerIcon:T,stylePopupContainer:_,discoverPickerType:m,provideDefaultPickerOptions:f}});function m(t){var e="datetimepicker";return t.dateFormat&&t.timeFormat||(t.dateFormat?e="datepicker":t.timeFormat&&(e="timepicker")),e}function f(t,e){var i=t;return"datetimepicker"==e||"datepicker"==e?i=l().defaults(t,c):"timepicker"==e&&(i=l().defaults(t,d)),i}function g(t,e){var i=t.beforeShow;return t.beforeShow=function(){_.apply(this,arguments),e||T.apply(this,arguments),i&&i.apply(this,arguments)},t.afterInject=function(){_.apply(this,[this.$input[0],this.inst])},t}function T(t,e){var i=r()(t).offset().left,s=parseFloat(e.dpDiv.css("width").replace("px","")),n=i+t.offsetWidth+s<r()(window).width();e.dpDiv.css({marginLeft:n?t.offsetWidth+"px":0})}function _(t,e){var i,s=e.dpDiv,n=s.parent();n&&n.is("body")&&(i=s.detach(),(s=r()('<div class="jr-jDatepickerPopupContainer jr">\n\n    <js-templateNonce></js-templateNonce>\n</div>\n')).append(i),r()("body").append(s))}},72861:(t,e,i)=>{var s=i(72157),n=i(52499),o=n.$,r=(n.$$,n.$w,n.Prototype),a=n.Position,l=(n.Hash,n.$A,n.Template,n.Class),h=(n.$F,n.Form,n.$break,n.$H,n.Selector,n.Field,n.Enumerable,i(83114)),c=h.Droppables,d=h.Draggables,p=h.Draggable,u=h.Sortable;p.prototype.startDrag=function(t){if(p.isDragging=!0,this.dragging=!0,this.delta||(this.delta=this.currentDelta()),this.options.zindex&&(this.originalZ=parseInt(Element.getStyle(this.element,"z-index")||0),this.element.style.zIndex=this.options.zindex),this.options.ghosting&&(this._clone=this.element.cloneNode(!0),this._originallyAbsolute="absolute"==this.element.getStyle("position"),this._originallyAbsolute||a.absolutize(this.element),this.element.parentNode.insertBefore(this._clone,this.element),"TR"===this.element.parentNode.tagName&&document.body.appendChild(this.element)),this.options.superghosting){a.prepare();var e=[Event.pointerX(t),Event.pointerY(t)],i=document.getElementsByTagName("body")[0],s=this.element;this._clone=s.cloneNode(!0),r.Browser.IE&&(this._clone.clearAttributes(),this._clone.mergeAttributes(s.cloneNode(!1))),s.parentNode.insertBefore(this._clone,s),s.id="clone_"+s.id,s.hide(),a.absolutize(s),s.parentNode.removeChild(s),i.appendChild(s),"0px"!=s.style.width&&"0px"!=s.style.height||(s.style.width=Element.getWidth(this._clone)+"px",s.style.height=Element.getHeight(this._clone)+"px"),this.originalScrollTop=Element.getHeight(this._clone)/2,this.draw(e),s.show()}if(this.options.scroll)if(this.options.scroll==window){var n=this._getWindowScroll(this.options.scroll);this.originalScrollLeft=n.left,this.originalScrollTop=n.top}else this.originalScrollLeft=this.options.scroll.scrollLeft,this.originalScrollTop=this.options.scroll.scrollTop;d.notify("onStart",this,t),this.options.starteffect&&this.options.starteffect(this.element)},p.prototype.draw=function(t){var e=a.cumulativeOffset(this.element);if(this.options.ghosting){var i=a.realOffset(this.element);e[0]+=i[0]-a.deltaX,e[1]+=i[1]-a.deltaY}var s=this.currentDelta();e[0]-=s[0],e[1]-=s[1],this.options.scroll&&(e[0]-=this.options.scroll.scrollLeft,e[1]-=this.options.scroll.scrollTop),this.options.scroll&&this.options.scroll!=window&&this._isScrollChild&&(e[0]-=this.options.scroll.scrollLeft-this.originalScrollLeft,e[1]-=this.options.scroll.scrollTop-this.originalScrollTop);var n=[0,1].map(function(i){return t[i]-e[i]-(this.options.mouseOffset?-2:this.offset[i])}.bind(this));this.options.snap&&(n=Object.isFunction(this.options.snap)?this.options.snap(n[0],n[1],this):Object.isArray(this.options.snap)?n.map(function(t,e){return(t/this.options.snap[e]).round()*this.options.snap[e]}.bind(this)):n.map(function(t){return(t/this.options.snap).round()*this.options.snap}.bind(this))),this.options.superghosting&&("absolute"==this.element.getStyle("position")?n[1]=t[1]-this.originalScrollTop:n[1]-=this.originalScrollTop||10);var o=this.element.style;this.options.constraint&&"horizontal"!=this.options.constraint||(o.left=n[0]+"px"),this.options.constraint&&"vertical"!=this.options.constraint||(o.top=n[1]+"px"),"hidden"==o.visibility&&(o.visibility="")},p.prototype.initDrag=function(t){if((Object.isUndefined(p._dragging[this.element])||!p._dragging[this.element])&&(t.touches&&1==t.touches.length||Event.isLeftClick(t))){var e=Event.element(t).tagName.toUpperCase();if("INPUT"==e||"SELECT"==e||"OPTION"==e||"BUTTON"==e||"TEXTAREA"==e)return;if(s(this.element).parents("#sortDialog").length>0&&"B"==e)return;var i=[Event.pointerX(t),Event.pointerY(t)],n=a.cumulativeOffset(this.element);this.offset=[0,1].map((function(t){return i[t]-n[t]})),d.activate(this),this.countdown=d.DEFAULT_TOLERANCE,Event.stop(t),this.element.fire("drag:mousedown",{targetEvent:t})}},c.isAffected=function(t,e,i){var n=s(i.element),r=n.width(),a=n.height(),l=n.offset(),h=l.left+r,c=l.top+a,d=t[0]>l.left&&t[0]<h&&t[1]>l.top&&t[1]<c;return i.element!=e&&(e.parentNode===o(document.body)||!i._containers||this.isContained(e,i))&&(!i.accept||Element.classNames(e).detect((function(t){return i.accept.include(t)})))&&d},p.prototype.finishDrag=function(t,e){if(p.isDragging=!1,this.dragging=!1,isIE()&&(document.body.onmousemove=function(){}),this.options.quiet){a.prepare();var i=[Event.pointerX(t),Event.pointerY(t)];c.show(i,this.element)}this.options.ghosting&&(this._originallyAbsolute||(a.relativize(this.element),"TR"===this._clone.parentNode.tagName&&this._clone.parentNode.insertBefore(this.element,this._clone)),delete this._originallyAbsolute,Element.remove(this._clone),this._clone=null);var s=!1;e&&((s=c.fire(t,this.element))||(s=!1)),s&&this.options.onDropped&&this.options.onDropped(this.element),d.notify("onEnd",this,t);var n=this.options.revert;n&&Object.isFunction(n)&&(n=n(this.element));var r=this.currentDelta();n&&this.options.reverteffect?0!=s&&"failure"==n||this.options.reverteffect(this.element,r[1]-this.delta[1],r[0]-this.delta[0]):this.delta=r,this.options.zindex&&(this.element.style.zIndex=this.originalZ,this._clone&&(this._clone.style.zIndex=this.originalZ)),this.options.endeffect&&this.options.endeffect(this.element),this.options.superghosting&&(null==this.element.parentNode&&(Element.hide(this.element),o(document.body).appendChild(this.element)),Element.remove(this.element),new p(this._clone,this.options)),d.deactivate(this),c.reset()},u.defaultOnHover=u.onHover,u.onHover=function(t,e,i){t.hasClassName("dialog")||u.defaultOnHover(t,e,i)},u.defaultOnEmptyHover=u.onEmptyHover,u.onEmptyHover=function(t,e,i){t.hasClassName("dialog")||u.defaultOnEmptyHover(t,e,i)};var m=l.create({initialize:function(t,e){this.element=o(t),this.observer=e,this.lastValue=u.serialize(this.element)},onStart:function(){this.lastValue=u.serialize(this.element)},onEnd:function(t,e){u.unmark(),this.lastValue!=u.serialize(this.element)&&this.observer(this.element,e)}});u.getSortableObserverConstructor=function(){return m},c.show=function(t,e){if(this.drops.length){var i,s=[];this.drops.each((function(i){c.isAffected(t,e,i)&&((e.hasClassName("sortDialogAvailable")||e.hasClassName("sortDialogSortFields"))&&"sortDialogAvailable"!=i.element.id&&"sortDialogSortFields"!=i.element.id||s.push(i))})),s.length>0&&(i=c.findDeepestChild(s)),this.last_active&&this.last_active!=i&&this.deactivate(this.last_active),i&&(a.within(i.element,t[0],t[1]),i.onHover&&(e.classNames().include("wrap")&&(e.relativize(),e.classNames().include("measure")?e.classNames().set("draggable dragging measure"+(e.classNames().include("supportsFilter")?" supportsFilter":"")):e.classNames().set("draggable dragging dimension"+(e.classNames().include("supportsFilter")?" supportsFilter":"")),e.style.position="relative",e.style.display="inline-block",e.style.width="",e.style.height=""),i.onHover(e,i.element,a.overlap(i.overlap,i.element))),i!=this.last_active&&c.activate(i))}},e.m9=c,e.Mw=d,e._l=p},15432:(t,e,i)=>{"use strict";i.d(e,{Z:()=>n});var s=i(24777);s.Z.dateTimeSettings=s.Z.dateTimeSettings||{};const n=s.Z.dateTimeSettings}}]);
//# sourceMappingURL=chunk.7926.js.map