define(["require","./jquery.ui.core"],function(t){var e=t("./jquery.ui.core"),i=0,n=Array.prototype.slice,s=e.cleanData;return e.cleanData=function(t){for(var i,n=0;null!=(i=t[n]);n++)try{e(i).triggerHandler("remove")}catch(o){}s(t)},e.widget=function(t,i,n){var s,o,r,a,u={},d=t.split(".")[0];t=t.split(".")[1],s=d+"-"+t,n||(n=i,i=e.Widget),e.expr[":"][s.toLowerCase()]=function(t){return!!e.data(t,s)},e[d]=e[d]||{},o=e[d][t],r=e[d][t]=function(t,e){return this._createWidget?void(arguments.length&&this._createWidget(t,e)):new r(t,e)},e.extend(r,o,{version:n.version,_proto:e.extend({},n),_childConstructors:[]}),a=new i,a.options=e.widget.extend({},a.options),e.each(n,function(t,n){return e.isFunction(n)?void(u[t]=function(){var e=function(){return i.prototype[t].apply(this,arguments)},s=function(e){return i.prototype[t].apply(this,e)};return function(){var t,i=this._super,o=this._superApply;return this._super=e,this._superApply=s,t=n.apply(this,arguments),this._super=i,this._superApply=o,t}}()):void(u[t]=n)}),r.prototype=e.widget.extend(a,{widgetEventPrefix:o?a.widgetEventPrefix||t:t},u,{constructor:r,namespace:d,widgetName:t,widgetFullName:s}),o?(e.each(o._childConstructors,function(t,i){var n=i.prototype;e.widget(n.namespace+"."+n.widgetName,r,i._proto)}),delete o._childConstructors):i._childConstructors.push(r),e.widget.bridge(t,r)},e.widget.extend=function(t){for(var i,s,o=n.call(arguments,1),r=0,a=o.length;a>r;r++)for(i in o[r])s=o[r][i],o[r].hasOwnProperty(i)&&void 0!==s&&(e.isPlainObject(s)?t[i]=e.isPlainObject(t[i])?e.widget.extend({},t[i],s):e.widget.extend({},s):t[i]=s);return t},e.widget.bridge=function(t,i){var s=i.prototype.widgetFullName||t;e.fn[t]=function(o){var r="string"==typeof o,a=n.call(arguments,1),u=this;return o=!r&&a.length?e.widget.extend.apply(null,[o].concat(a)):o,r?this.each(function(){var i,n=e.data(this,s);return n?e.isFunction(n[o])&&"_"!==o.charAt(0)?(i=n[o].apply(n,a),i!==n&&void 0!==i?(u=i&&i.jquery?u.pushStack(i.get()):i,!1):void 0):e.error("no such method '"+o+"' for "+t+" widget instance"):e.error("cannot call methods on "+t+" prior to initialization; attempted to call method '"+o+"'")}):this.each(function(){var t=e.data(this,s);t?t.option(o||{})._init():e.data(this,s,new i(o,this))}),u}},e.Widget=function(){},e.Widget._childConstructors=[],e.Widget.prototype={widgetName:"widget",widgetEventPrefix:"",defaultElement:"<div>",options:{disabled:!1,create:null},_createWidget:function(t,n){n=e(n||this.defaultElement||this)[0],this.element=e(n),this.uuid=i++,this.eventNamespace="."+this.widgetName+this.uuid,this.options=e.widget.extend({},this.options,this._getCreateOptions(),t),this.bindings=e(),this.hoverable=e(),this.focusable=e(),n!==this&&(e.data(n,this.widgetFullName,this),this._on(!0,this.element,{remove:function(t){t.target===n&&this.destroy()}}),this.document=e(n.style?n.ownerDocument:n.document||n),this.window=e(this.document[0].defaultView||this.document[0].parentWindow)),this._create(),this._trigger("create",null,this._getCreateEventData()),this._init()},_getCreateOptions:e.noop,_getCreateEventData:e.noop,_create:e.noop,_init:e.noop,destroy:function(){this._destroy(),this.element.unbind(this.eventNamespace).removeData(this.widgetName).removeData(this.widgetFullName).removeData(e.camelCase(this.widgetFullName)),this.widget().unbind(this.eventNamespace).removeAttr("aria-disabled").removeClass(this.widgetFullName+"-disabled ui-state-disabled"),this.bindings.unbind(this.eventNamespace),this.hoverable.removeClass("ui-state-hover"),this.focusable.removeClass("ui-state-focus")},_destroy:e.noop,widget:function(){return this.element},option:function(t,i){var n,s,o,r=t;if(0===arguments.length)return e.widget.extend({},this.options);if("string"==typeof t)if(r={},n=t.split("."),t=n.shift(),n.length){for(s=r[t]=e.widget.extend({},this.options[t]),o=0;o<n.length-1;o++)s[n[o]]=s[n[o]]||{},s=s[n[o]];if(t=n.pop(),1===arguments.length)return void 0===s[t]?null:s[t];s[t]=i}else{if(1===arguments.length)return void 0===this.options[t]?null:this.options[t];r[t]=i}return this._setOptions(r),this},_setOptions:function(t){var e;for(e in t)this._setOption(e,t[e]);return this},_setOption:function(t,e){return this.options[t]=e,"disabled"===t&&(this.widget().toggleClass(this.widgetFullName+"-disabled ui-state-disabled",!!e).attr("aria-disabled",e),this.hoverable.removeClass("ui-state-hover"),this.focusable.removeClass("ui-state-focus")),this},enable:function(){return this._setOption("disabled",!1)},disable:function(){return this._setOption("disabled",!0)},_on:function(t,i,n){var s,o=this;"boolean"!=typeof t&&(n=i,i=t,t=!1),n?(i=s=e(i),this.bindings=this.bindings.add(i)):(n=i,i=this.element,s=this.widget()),e.each(n,function(n,r){function a(){return t||o.options.disabled!==!0&&!e(this).hasClass("ui-state-disabled")?("string"==typeof r?o[r]:r).apply(o,arguments):void 0}"string"!=typeof r&&(a.guid=r.guid=r.guid||a.guid||e.guid++);var u=n.match(/^(\w+)\s*(.*)$/),d=u[1]+o.eventNamespace,h=u[2];h?s.delegate(h,d,a):i.bind(d,a)})},_off:function(t,e){e=(e||"").split(" ").join(this.eventNamespace+" ")+this.eventNamespace,t.unbind(e).undelegate(e)},_delay:function(t,e){function i(){return("string"==typeof t?n[t]:t).apply(n,arguments)}var n=this;return setTimeout(i,e||0)},_hoverable:function(t){this.hoverable=this.hoverable.add(t),this._on(t,{mouseenter:function(t){e(t.currentTarget).addClass("ui-state-hover")},mouseleave:function(t){e(t.currentTarget).removeClass("ui-state-hover")}})},_focusable:function(t){this.focusable=this.focusable.add(t),this._on(t,{focusin:function(t){e(t.currentTarget).addClass("ui-state-focus")},focusout:function(t){e(t.currentTarget).removeClass("ui-state-focus")}})},_trigger:function(t,i,n){var s,o,r=this.options[t];if(n=n||{},i=e.Event(i),i.type=(t===this.widgetEventPrefix?t:this.widgetEventPrefix+t).toLowerCase(),i.target=this.element[0],o=i.originalEvent)for(s in o)s in i||(i[s]=o[s]);return this.element.trigger(i,n),!(e.isFunction(r)&&r.apply(this.element[0],[i].concat(n))===!1||i.isDefaultPrevented())}},e.each({show:"fadeIn",hide:"fadeOut"},function(t,i){e.Widget.prototype["_"+t]=function(n,s,o){"string"==typeof s&&(s={effect:s});var r,a=s?s===!0||"number"==typeof s?i:s.effect||i:t;s=s||{},"number"==typeof s&&(s={duration:s}),r=!e.isEmptyObject(s),s.complete=o,s.delay&&n.delay(s.delay),r&&e.effects&&e.effects.effect[a]?n[t](s):a!==t&&n[a]?n[a](s.duration,s.easing,o):n.queue(function(i){e(this)[t](),o&&o.call(n[0]),i()})}}),e});