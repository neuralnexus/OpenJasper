(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[6582],{49990:(t,e,i)=>{"use strict";i.r(e);var s=i(97836),o=i.n(s),n=i(71914),l=i(17620),r=i(72157),a=i.n(r),h=i(32758),c={CONTENT_REPOSITORY:"CONTENT_REPOSITORY",LOCAL:"LOCAL",NONE:"NONE",FILE_SYSTEM:"FILE_SYSTEM",LOCATE_EVENT:"resource:locate",ALLOWED_FILE_RESOURCE_EXTENSIONS:["css","ttf","jpg","jpeg","gif","bmp","png","jar","jrxml","properties","jrtx","xml","agxml","docx","doc","ppt","pptx","xls","xlsx","ods","odt","odp","pdf","rtf","html"],initialize:function(t){var e=function(t){return o().isObject(t)?t:a()("#"+t)[0]};this.resourceUri=e(t.resourceInput),this.browseButton=e(t.browseButton),this.filePath=e(t.fileUploadInput),this.fakeFilePath=e(t.fakeFileUploadInput),this.fakeFileInput=e(t.fakeFileUploadInputText),this.newResourceLink=e(t.newResourceLink);try{this._initFileSelector(t)}catch(t){}finally{this._initEvents(t)}return this},_initEvents:function(t){a()(document).on("click","#CONTENT_REPOSITORY, #FILE_SYSTEM, #NONE, #LOCAL",this._clickHandler),"fileResourceTreeDataProvider"===t.providerId&&(a()("#next").on("click",c._nextClickHandler),a()("#filePath").on("change",c._uploadChangeHandler))},_nextClickHandler:function(t){a()("#fileUpload").hasClass("error")&&t.preventDefault()},_uploadChangeHandler:function(t){a()("#fileUpload").removeClass("error");var e=!0;if(a()("#filePath")[0].value){var i=a()("#filePath")[0].value.match(/.*\.([^\.]+)$/);if(i){var s=i[1];o().indexOf(c.ALLOWED_FILE_RESOURCE_EXTENSIONS,s)<0&&(e=!1)}else e=!1}else e=!1;if(!e){var n=l.Z.messages["resource.report.unsupportedFileType.error"]+" "+c.ALLOWED_FILE_RESOURCE_EXTENSIONS.join(", ");a()("#fileUpload").addClass("error").find("span.warning").html(n)}},_clickHandler:function(t){c._updateResourceSelectorState(t.target.id)},_updateResourceSelectorState:function(t){l.Z.switchButtonState(this.filePath,t===this.FILE_SYSTEM),l.Z.switchButtonState(this.fakeFilePath,t===this.FILE_SYSTEM),l.Z.switchButtonState(this.fakeFileInput,t===this.FILE_SYSTEM),l.Z.switchButtonState(this.browseButton,t===this.CONTENT_REPOSITORY),l.Z.switchDisableState(this.resourceUri,t!==this.CONTENT_REPOSITORY);var e=t===this.LOCAL?["disabled","launcher"]:[];this._switchElementClasses(this.newResourceLink,e)},_initFileSelector:function(t){this.fileSelector=new h.Z.FileSelector(o().extend({},t,{uriTextboxId:this.resourceUri,browseButtonId:this.browseButton,title:t.dialogTitle}))},remove:function(t){this.fileSelector.remove()},_switchElementClasses:function(t,e){t&&e&&a()(t).removeClass(e[0]).addClass(e[1])}};const d=c;var p={messages:[],initialize:function(){d.initialize({resourceInput:"resourceUri",browseButton:"browser_button",newResourceLink:"newQueryLink",treeId:"queryTreeRepoLocation",providerId:"queryTreeDataProvider",dialogTitle:p.messages["resource.QueryLocate.Title"],selectLeavesOnly:!0});var t=a()("#newQueryLink")[0];t&&t.observe("click",(function(){a()("#LOCAL")[0].checked&&a()("#next").click()}))},jumpTo:function(t){return a()("#jumpToPage")[0].setValue(t),a()("#jumpButton").click(),!1}};const u=p;var m=i(64155);o().extend(u.messages,n.Z.addJasperReport.resourceQueryLocate.messages),u.initialize(),(0,m.zcy)()&&l.Z.initSwipeScroll(),a()("#steps1_2").on("click",(function(){return u.jumpTo("reportNaming")})),a()("#step3").on("click",(function(){return u.jumpTo("resources")})),a()("#step4").on("click",(function(){return u.jumpTo("dataSource")})),a()("#step5").on("click",(function(){return u.jumpTo("query")})),a()("#step6").on("click",(function(){return u.jumpTo("customization")}))},84581:(t,e,i)=>{"use strict";i.d(e,{p:()=>c,k:()=>d});var s=i(97836),o=i.n(s),n=i(64155),l=i(72157),r=i.n(l),a=i(84612),h=i(33806);function c(t,e){if(t){if(this.srcElement=t,e&&(this.label=e.label,this.text=e.text,this.offsets=e.offsets,this.showBelow=!!e.showBelow,this.templateId=e.templateId,this.loadTextCallback=e.loadTextCallback,this.loadTextExecuted=!1),this.disabled=!1,this.removed=!1,this.templateId)this._toAttribute(this.TOOLTIP_TEMPLATE,this.templateId);else{var i=this._fromAttribute(this.TOOLTIP_TEMPLATE);this.templateId=i&&i.length>0?i:this.TOOLTIP_TEMPLATE_ID}this.label?this._toAttribute(this.TOOLTIP_LABEL,this.label):this.label=this._fromAttribute(this.TOOLTIP_LABEL),this.text?this._toAttribute(this.TOOLTIP_TEXT,this.text):this.text=this._fromAttribute(this.TOOLTIP_TEXT),this.label&&(this.label=this._escapeText(this.label)),this.text&&(this.text=this._escapeText(this.text)),this.srcElement.jsTooltip=this,d.tooltips.push(this)}}c.addVar("SEPARATOR","@@"),c.addVar("TOOLTIP_LABEL","tooltiplabel"),c.addVar("TOOLTIP_TEXT","tooltiptext"),c.addVar("TOOLTIP_TEMPLATE","tooltiptemplate"),c.addVar("TOOLTIP_TEMPLATE_ID","jsTooltip"),c.addVar("LABEL_PATTERN",".message.label"),c.addVar("TEXT_PATTERN",".message:not(.label)"),c.addMethod("_toAttribute",(function(t,e){this.srcElement&&(e=a.Z.hardEscape(e),this.srcElement.writeAttribute(t,(0,n.kJL)(e)?e.join(this.SEPARATOR):e))})),c.addMethod("_fromAttribute",(function(t){if(this.srcElement&&this.srcElement.hasAttribute(t)){var e=this.srcElement.readAttribute(t);return e.include(this.SEPARATOR)?e.split(this.SEPARATOR):e}return null})),c.addMethod("_setValues",(function(t,e){t.each((function(t,i){(o().isString(e[i])||o().isNumber(e[i]))&&t.update(a.Z.hardEscape(e[i]))}))})),c.addMethod("_calculateZIndex",(function(t){function e(t){return parseInt(r()(t).css("z-index"))}var i=e(t);o().isNumber(i)&&!o().isNaN(i)||(i=1e3);var s=o().flatten([this.srcElement,r()(this.srcElement).parents().toArray()]);return o().reduce(s,(function(t,i){var s=e(i);return o().isNumber(s)&&!o().isNaN(s)&&(t=Math.max(t,s)),t}),i)+1})),c.addMethod("_escapeText",(function(t){return o().isArray(t)?o().map(t,(function(t){return a.Z.hardEscape(t)})):a.Z.hardEscape(t)})),c.addMethod("show",(function(t){var e;t&&(this.offsets=t),this._element=r()("#"+this.templateId)[0],this.showBelow?(e=(0,n.Qyk)(this.srcElement))[1]+=this.srcElement.clientHeight+5:e=[(0,n.vjD)()+5,(0,n.cxm)()+5],this.offsets&&(e[0]+=this.offsets[0],e[1]+=this.offsets[1]),this._element.setStyle({position:"absolute",left:e[0]+"px",top:e[1]+"px",zIndex:this._calculateZIndex(this._element)});var i=this._element.select(this.LABEL_PATTERN),s=this._element.select(this.TEXT_PATTERN);if(this.label&&this._setValues(i,(0,n.kJL)(this.label)?this.label:[this.label]),this.text&&this._setValues(s,(0,n.kJL)(this.text)?this.text:[this.text]),r()(this._element).removeClass(h.Z.HIDDEN_CLASS),e[0]+this._element.clientWidth>r()(window).width()){var o=e[0]-this._element.clientWidth>0?e[0]-this._element.clientWidth:15;this._element.setStyle({left:o+"px"})}if(e[1]+this._element.clientHeight>r()(window).height()){var l=e[1]-this._element.clientHeight-10;this._element.setStyle({top:l+"px"})}return this.loadTextCallback&&!this.loadTextExecuted&&(this.loadTextExecuted=!0,this.loadTextCallback(this)),this})),c.addMethod("updateText",(function(t){if(this.text=this._escapeText(t),this._element){var e=this._element.select(this.TEXT_PATTERN);this._setValues(e,(0,n.kJL)(this.text)?this.text:[this.text])}})),c.addMethod("hide",(function(){this._element&&r()(this._element).addClass(h.Z.HIDDEN_CLASS)})),c.addMethod("disable",(function(){d.hideJSTooltip(this.srcElement),this.disabled=!0})),c.addMethod("enable",(function(){this.disabled=!1,d.showJSTooltip(this.srcElement,this.offsets)})),c.addMethod("remove",(function(){var t=d.tooltips.indexOf(this.srcElement.jsTooltip);-1!==t&&(d.hideJSTooltip(this.srcElement),d.tooltips.splice(t,1)),this.removed=!0})),c.addMethod("isRemoved",(function(){return this.removed}));var d={TOOLTIP_PATTERN:"*[tooltiptext] > *",ELEMENT_WITH_TOOLTIP_PATTERN:"*[tooltiptext]",tooltips:[],showJSTooltip:function(t,e){if(t.jsTooltip){if(!t.jsTooltip.disabled&&e){var i=e[0],s=e[1];this.cleanUp();var o=r()(t).attr("tooltipappeardelay");o=o?parseInt(o):1e3,t.jsTooltip.timer&&clearTimeout(t.jsTooltip.timer),t.jsTooltip.timer=setTimeout((function(){t.jsTooltip.show([i,s])}),o),r()(t).on("mousemove",(function(t){i=t.clientX,s=t.clientY}))}}else t.jsTooltip=new c(t,{})},enableTooltips:function(){(this.tooltips||[]).forEach((function(t){t.enable()}))},disableTooltips:function(){(this.tooltips||[]).forEach((function(t){t.disable()}))},hideJSTooltip:function(t){t&&t.jsTooltip&&(t.jsTooltip.timer&&clearTimeout(t.jsTooltip.timer),t.jsTooltip.hide(),r()(t).off("mousemove"))},cleanUp:function(){if(this.tooltips&&this.tooltips.length>0){var t=[],e="fuigasuifdughaiadbvguaidbapvuwbev";this.tooltips.each((function(i){i.srcElement.id&&document.getElementById(i.srcElement.id)||(i.srcElement.setAttribute("id",e),document.getElementById(e)||(i.hide(),t.push(i)),i.srcElement.removeAttribute("id"))})),t.length>0&&(this.tooltips=this.tooltips.reject((function(e){return t.include(e)})))}}}},72861:(t,e,i)=>{var s=i(72157),o=i(52499),n=o.$,l=(o.$$,o.$w,o.Prototype),r=o.Position,a=(o.Hash,o.$A,o.Template,o.Class),h=(o.$F,o.Form,o.$break,o.$H,o.Selector,o.Field,o.Enumerable,i(83114)),c=h.Droppables,d=h.Draggables,p=h.Draggable,u=h.Sortable;p.prototype.startDrag=function(t){if(p.isDragging=!0,this.dragging=!0,this.delta||(this.delta=this.currentDelta()),this.options.zindex&&(this.originalZ=parseInt(Element.getStyle(this.element,"z-index")||0),this.element.style.zIndex=this.options.zindex),this.options.ghosting&&(this._clone=this.element.cloneNode(!0),this._originallyAbsolute="absolute"==this.element.getStyle("position"),this._originallyAbsolute||r.absolutize(this.element),this.element.parentNode.insertBefore(this._clone,this.element),"TR"===this.element.parentNode.tagName&&document.body.appendChild(this.element)),this.options.superghosting){r.prepare();var e=[Event.pointerX(t),Event.pointerY(t)],i=document.getElementsByTagName("body")[0],s=this.element;this._clone=s.cloneNode(!0),l.Browser.IE&&(this._clone.clearAttributes(),this._clone.mergeAttributes(s.cloneNode(!1))),s.parentNode.insertBefore(this._clone,s),s.id="clone_"+s.id,s.hide(),r.absolutize(s),s.parentNode.removeChild(s),i.appendChild(s),"0px"!=s.style.width&&"0px"!=s.style.height||(s.style.width=Element.getWidth(this._clone)+"px",s.style.height=Element.getHeight(this._clone)+"px"),this.originalScrollTop=Element.getHeight(this._clone)/2,this.draw(e),s.show()}if(this.options.scroll)if(this.options.scroll==window){var o=this._getWindowScroll(this.options.scroll);this.originalScrollLeft=o.left,this.originalScrollTop=o.top}else this.originalScrollLeft=this.options.scroll.scrollLeft,this.originalScrollTop=this.options.scroll.scrollTop;d.notify("onStart",this,t),this.options.starteffect&&this.options.starteffect(this.element)},p.prototype.draw=function(t){var e=r.cumulativeOffset(this.element);if(this.options.ghosting){var i=r.realOffset(this.element);e[0]+=i[0]-r.deltaX,e[1]+=i[1]-r.deltaY}var s=this.currentDelta();e[0]-=s[0],e[1]-=s[1],this.options.scroll&&(e[0]-=this.options.scroll.scrollLeft,e[1]-=this.options.scroll.scrollTop),this.options.scroll&&this.options.scroll!=window&&this._isScrollChild&&(e[0]-=this.options.scroll.scrollLeft-this.originalScrollLeft,e[1]-=this.options.scroll.scrollTop-this.originalScrollTop);var o=[0,1].map(function(i){return t[i]-e[i]-(this.options.mouseOffset?-2:this.offset[i])}.bind(this));this.options.snap&&(o=Object.isFunction(this.options.snap)?this.options.snap(o[0],o[1],this):Object.isArray(this.options.snap)?o.map(function(t,e){return(t/this.options.snap[e]).round()*this.options.snap[e]}.bind(this)):o.map(function(t){return(t/this.options.snap).round()*this.options.snap}.bind(this))),this.options.superghosting&&("absolute"==this.element.getStyle("position")?o[1]=t[1]-this.originalScrollTop:o[1]-=this.originalScrollTop||10);var n=this.element.style;this.options.constraint&&"horizontal"!=this.options.constraint||(n.left=o[0]+"px"),this.options.constraint&&"vertical"!=this.options.constraint||(n.top=o[1]+"px"),"hidden"==n.visibility&&(n.visibility="")},p.prototype.initDrag=function(t){if((Object.isUndefined(p._dragging[this.element])||!p._dragging[this.element])&&(t.touches&&1==t.touches.length||Event.isLeftClick(t))){var e=Event.element(t).tagName.toUpperCase();if("INPUT"==e||"SELECT"==e||"OPTION"==e||"BUTTON"==e||"TEXTAREA"==e)return;if(s(this.element).parents("#sortDialog").length>0&&"B"==e)return;var i=[Event.pointerX(t),Event.pointerY(t)],o=r.cumulativeOffset(this.element);this.offset=[0,1].map((function(t){return i[t]-o[t]})),d.activate(this),this.countdown=d.DEFAULT_TOLERANCE,Event.stop(t),this.element.fire("drag:mousedown",{targetEvent:t})}},c.isAffected=function(t,e,i){var o=s(i.element),l=o.width(),r=o.height(),a=o.offset(),h=a.left+l,c=a.top+r,d=t[0]>a.left&&t[0]<h&&t[1]>a.top&&t[1]<c;return i.element!=e&&(e.parentNode===n(document.body)||!i._containers||this.isContained(e,i))&&(!i.accept||Element.classNames(e).detect((function(t){return i.accept.include(t)})))&&d},p.prototype.finishDrag=function(t,e){if(p.isDragging=!1,this.dragging=!1,isIE()&&(document.body.onmousemove=function(){}),this.options.quiet){r.prepare();var i=[Event.pointerX(t),Event.pointerY(t)];c.show(i,this.element)}this.options.ghosting&&(this._originallyAbsolute||(r.relativize(this.element),"TR"===this._clone.parentNode.tagName&&this._clone.parentNode.insertBefore(this.element,this._clone)),delete this._originallyAbsolute,Element.remove(this._clone),this._clone=null);var s=!1;e&&((s=c.fire(t,this.element))||(s=!1)),s&&this.options.onDropped&&this.options.onDropped(this.element),d.notify("onEnd",this,t);var o=this.options.revert;o&&Object.isFunction(o)&&(o=o(this.element));var l=this.currentDelta();o&&this.options.reverteffect?0!=s&&"failure"==o||this.options.reverteffect(this.element,l[1]-this.delta[1],l[0]-this.delta[0]):this.delta=l,this.options.zindex&&(this.element.style.zIndex=this.originalZ,this._clone&&(this._clone.style.zIndex=this.originalZ)),this.options.endeffect&&this.options.endeffect(this.element),this.options.superghosting&&(null==this.element.parentNode&&(Element.hide(this.element),n(document.body).appendChild(this.element)),Element.remove(this.element),new p(this._clone,this.options)),d.deactivate(this),c.reset()},u.defaultOnHover=u.onHover,u.onHover=function(t,e,i){t.hasClassName("dialog")||u.defaultOnHover(t,e,i)},u.defaultOnEmptyHover=u.onEmptyHover,u.onEmptyHover=function(t,e,i){t.hasClassName("dialog")||u.defaultOnEmptyHover(t,e,i)};var m=a.create({initialize:function(t,e){this.element=n(t),this.observer=e,this.lastValue=u.serialize(this.element)},onStart:function(){this.lastValue=u.serialize(this.element)},onEnd:function(t,e){u.unmark(),this.lastValue!=u.serialize(this.element)&&this.observer(this.element,e)}});u.getSortableObserverConstructor=function(){return m},c.show=function(t,e){if(this.drops.length){var i,s=[];this.drops.each((function(i){c.isAffected(t,e,i)&&((e.hasClassName("sortDialogAvailable")||e.hasClassName("sortDialogSortFields"))&&"sortDialogAvailable"!=i.element.id&&"sortDialogSortFields"!=i.element.id||s.push(i))})),s.length>0&&(i=c.findDeepestChild(s)),this.last_active&&this.last_active!=i&&this.deactivate(this.last_active),i&&(r.within(i.element,t[0],t[1]),i.onHover&&(e.classNames().include("wrap")&&(e.relativize(),e.classNames().include("measure")?e.classNames().set("draggable dragging measure"+(e.classNames().include("supportsFilter")?" supportsFilter":"")):e.classNames().set("draggable dragging dimension"+(e.classNames().include("supportsFilter")?" supportsFilter":"")),e.style.position="relative",e.style.display="inline-block",e.style.width="",e.style.height=""),i.onHover(e,i.element,r.overlap(i.overlap,i.element))),i!=this.last_active&&c.activate(i))}},e.m9=c,e.Mw=d,e._l=p}}]);
//# sourceMappingURL=chunk.6582.js.map