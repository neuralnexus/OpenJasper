(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[1432,7335,5573],{17335:(t,i,e)=>{var s,n,o;
/*!
 * jQuery UI Scroll Parent 1.13.2
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */!function(r){"use strict";n=[e(72157),e(91544)],void 0===(o="function"==typeof(s=function(t){return t.fn.scrollParent=function(i){var e=this.css("position"),s="absolute"===e,n=i?/(auto|scroll|hidden)/:/(auto|scroll)/,o=this.parents().filter((function(){var i=t(this);return(!s||"static"!==i.css("position"))&&n.test(i.css("overflow")+i.css("overflow-y")+i.css("overflow-x"))})).eq(0);return"fixed"!==e&&o.length?o:t(this[0].ownerDocument||document)}})?s.apply(i,n):s)||(t.exports=o)}()},13609:(t,e,s)=>{var n,o=s(72157);(n=o).jCryption=function(t,i){var e=this;e.$el=n(t),e.el=t,e.$el.data("jCryption",e),e.init=function(){if(e.options=n.extend({},n.jCryption.defaultOptions,i),$encryptedElement=n("<input />",{type:"hidden",name:e.options.postVariable}),!1!==e.options.submitElement)var t=e.options.submitElement;else t=e.$el.find(":input:submit");t.bind(e.options.submitEvent,(function(){return n(this).attr("disabled",!0),e.options.beforeEncryption()&&n.jCryption.getKeys(e.options.getKeysURL,(function(t){n.jCryption.encrypt(e.$el.serialize(),t,(function(t){$encryptedElement.val(t),n(e.$el).find(e.options.formFieldSelector).attr("disabled",!0).end().append($encryptedElement).submit()}))})),!1}))},e.init()},n.jCryption.getKeys=function(t,i){var e=function(t,i,e){!function(t){r=t,l=new Array(r);for(var i=0;i<l.length;i++)l[i]=0;a=new v,(h=new v).digits[0]=1}(parseInt(e,10)),this.e=A(t),this.m=A(i),this.chunkSize=2*T(this.m),this.radix=16,this.barrett=new U(this.m)};n.getJSON(t,(function(t){var s=new e(t.e,t.n,t.maxdigits);n.isFunction(i)&&i.call(this,s)}))},n.jCryption.encrypt=function(t,i,e){for(var s=0,o=0;o<t.length;o++)s+=t.charCodeAt(o);for(var r="0123456789abcdef",l="",a=(l+=r.charAt((240&s)>>4)+r.charAt(15&s))+t,h=[],g=0;g<a.length;)h[g]=a.charCodeAt(g),g++;for(;h.length%i.chunkSize!=0;)h[g++]=0;!function(t){var s,o,r=0,l="";setTimeout((function a(){o=new v,s=0;for(var h=r;h<r+i.chunkSize;++s)o.digits[s]=t[h++],o.digits[s]+=t[h++]<<8;var g=i.barrett.powMod(o,i.e),d=16==i.radix?E(g):b(g,i.radix);if(l+=d+" ",(r+=i.chunkSize)<t.length)setTimeout(a,1);else{var u=l.substring(0,l.length-1);if(!n.isFunction(e))return u;e(u)}}),1)}(h)},n.jCryption.defaultOptions={submitElement:!1,submitEvent:"click",getKeysURL:"main.php?generateKeypair=true",beforeEncryption:function(){return!0},postVariable:"jCryption",formFieldSelector:":input"},n.fn.jCryption=function(t){return this.each((function(){new n.jCryption(this,t)}))};var r,l,a,h,g=16,d=65536,u=65535,c=new Array(0,32768,49152,57344,61440,63488,64512,65024,65280,65408,65472,65504,65520,65528,65532,65534,65535),p=new Array("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"),f=new Array("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"),m=new Array(0,1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,32767,65535);function v(t){this.digits="boolean"==typeof t&&1==t?null:l.slice(0),this.isNeg=!1}function N(t){var i=new v(!0);return i.digits=t.digits.slice(0),i.isNeg=t.isNeg,i}function y(t){for(var i="",e=t.length-1;e>-1;--e)i+=t.charAt(e);return i}function b(t,i){var e=new v;e.digits[0]=i;for(var s=B(t,e),n=p[s[1].digits[0]];1==I(s[0],a);)s=B(s[0],e),digit=s[1].digits[0],n+=p[s[1].digits[0]];return(t.isNeg?"-":"")+y(n)}function w(t){var e="";for(i=0;i<4;++i)e+=f[15&t],t>>>=4;return y(e)}function E(t){for(var i="",e=(T(t),T(t));e>-1;--e)i+=w(t.digits[e]);return i}function _(t){return t>=48&&t<=57?t-48:t>=65&&t<=90?10+t-65:t>=97&&t<=122?10+t-97:0}function C(t){for(var i=0,e=Math.min(t.length,4),s=0;s<e;++s)i<<=4,i|=_(t.charCodeAt(s));return i}function A(t){for(var i=new v,e=t.length,s=0;e>0;e-=4,++s)i.digits[s]=C(t.substr(Math.max(e-4,0),Math.min(e,4)));return i}function S(t,i){var e;if(t.isNeg!=i.isNeg)i.isNeg=!i.isNeg,e=k(t,i),i.isNeg=!i.isNeg;else{e=new v;for(var s,n=0,o=0;o<t.digits.length;++o)s=t.digits[o]+i.digits[o]+n,e.digits[o]=65535&s,n=Number(s>=d);e.isNeg=t.isNeg}return e}function k(t,i){var e;if(t.isNeg!=i.isNeg)i.isNeg=!i.isNeg,e=S(t,i),i.isNeg=!i.isNeg;else{var s,n;e=new v,n=0;for(var o=0;o<t.digits.length;++o)s=t.digits[o]-i.digits[o]+n,e.digits[o]=65535&s,e.digits[o]<0&&(e.digits[o]+=d),n=0-Number(s<0);if(-1==n){n=0;for(o=0;o<t.digits.length;++o)s=0-e.digits[o]+n,e.digits[o]=65535&s,e.digits[o]<0&&(e.digits[o]+=d),n=0-Number(s<0);e.isNeg=!t.isNeg}else e.isNeg=t.isNeg}return e}function T(t){for(var i=t.digits.length-1;i>0&&0==t.digits[i];)--i;return i}function x(t){var i,e=T(t),s=t.digits[e],n=(e+1)*g;for(i=n;i>n-g&&0==(32768&s);--i)s<<=1;return i}function z(t,i){for(var e,s,n,o=new v,r=T(t),l=T(i),a=0;a<=l;++a){for(e=0,n=a,j=0;j<=r;++j,++n)s=o.digits[n]+t.digits[j]*i.digits[a]+e,o.digits[n]=s&u,e=s>>>16;o.digits[a+r+1]=e}return o.isNeg=t.isNeg!=i.isNeg,o}function D(t,i){var e,s,n,o=new v;e=T(t),s=0;for(var r=0;r<=e;++r)n=o.digits[r]+t.digits[r]*i+s,o.digits[r]=n&u,s=n>>>16;return o.digits[1+e]=s,o}function O(t,i,e,s,n){for(var o=Math.min(i+n,t.length),r=i,l=s;r<o;++r,++l)e[l]=t[r]}function F(t,i){var e=Math.floor(i/g),s=new v;O(t.digits,0,s.digits,e,s.digits.length-e);for(var n=i%g,o=g-n,r=s.digits.length-1,l=r-1;r>0;--r,--l)s.digits[r]=s.digits[r]<<n&u|(s.digits[l]&c[n])>>>o;return s.digits[0]=s.digits[r]<<n&u,s.isNeg=t.isNeg,s}function M(t,i){var e=Math.floor(i/g),s=new v;O(t.digits,e,s.digits,0,t.digits.length-e);for(var n=i%g,o=g-n,r=0,l=r+1;r<s.digits.length-1;++r,++l)s.digits[r]=s.digits[r]>>>n|(s.digits[l]&m[n])<<o;return s.digits[s.digits.length-1]>>>=n,s.isNeg=t.isNeg,s}function $(t,i){var e=new v;return O(t.digits,0,e.digits,i,e.digits.length-i),e}function H(t,i){var e=new v;return O(t.digits,i,e.digits,0,e.digits.length-i),e}function L(t,i){var e=new v;return O(t.digits,0,e.digits,0,i),e}function I(t,i){if(t.isNeg!=i.isNeg)return 1-2*Number(t.isNeg);for(var e=t.digits.length-1;e>=0;--e)if(t.digits[e]!=i.digits[e])return t.isNeg?1-2*Number(t.digits[e]>i.digits[e]):1-2*Number(t.digits[e]<i.digits[e]);return 0}function B(t,i){var e,s,n=x(t),o=x(i),r=i.isNeg;if(n<o)return t.isNeg?((e=N(h)).isNeg=!i.isNeg,t.isNeg=!1,i.isNeg=!1,s=k(i,t),t.isNeg=!0,i.isNeg=r):(e=new v,s=N(t)),new Array(e,s);e=new v,s=t;for(var l=Math.ceil(o/g)-1,a=0;i.digits[l]<32768;)i=F(i,1),++a,++o,l=Math.ceil(o/g)-1;s=F(s,a),n+=a;for(var c=Math.ceil(n/g)-1,p=$(i,c-l);-1!=I(s,p);)++e.digits[c-l],s=k(s,p);for(var f=c;f>l;--f){var m=f>=s.digits.length?0:s.digits[f],y=f-1>=s.digits.length?0:s.digits[f-1],b=f-2>=s.digits.length?0:s.digits[f-2],w=l>=i.digits.length?0:i.digits[l],E=l-1>=i.digits.length?0:i.digits[l-1];e.digits[f-l-1]=m==w?u:Math.floor((m*d+y)/w);for(var _=e.digits[f-l-1]*(w*d+E),C=4294967296*m+(y*d+b);_>C;)--e.digits[f-l-1],_=e.digits[f-l-1]*(w*d|E),C=m*d*d+(y*d+b);(s=k(s,D(p=$(i,f-l-1),e.digits[f-l-1]))).isNeg&&(s=S(s,p),--e.digits[f-l-1])}return s=M(s,a),e.isNeg=t.isNeg!=r,t.isNeg&&(e=r?S(e,h):k(e,h),s=k(i=M(i,a),s)),0==s.digits[0]&&0==T(s)&&(s.isNeg=!1),new Array(e,s)}function U(t){this.modulus=N(t),this.k=T(this.modulus)+1;var i,e,s=new v;s.digits[2*this.k]=1,this.mu=(i=s,e=this.modulus,B(i,e)[0]),this.bkplus1=new v,this.bkplus1.digits[this.k+1]=1,this.modulo=R,this.multiplyMod=K,this.powMod=P}function R(t){var i=H(t,this.k-1),e=H(z(i,this.mu),this.k+1),s=k(L(t,this.k+1),L(z(e,this.modulus),this.k+1));s.isNeg&&(s=S(s,this.bkplus1));for(var n=I(s,this.modulus)>=0;n;)n=I(s=k(s,this.modulus),this.modulus)>=0;return s}function K(t,i){var e=z(t,i);return this.modulo(e)}function P(t,i){var e=new v;for(e.digits[0]=1;0!=(1&i.digits[0])&&(e=this.multiplyMod(e,t)),0!=(i=M(i,1)).digits[0]||0!=T(i);)t=this.multiplyMod(t,t);return e}e.tk=v,e.Iy=E,e.sB=b},72861:(t,i,e)=>{var s=e(72157),n=e(52499),o=n.$,r=(n.$$,n.$w,n.Prototype),l=n.Position,a=(n.Hash,n.$A,n.Template,n.Class),h=(n.$F,n.Form,n.$break,n.$H,n.Selector,n.Field,n.Enumerable,e(83114)),g=h.Droppables,d=h.Draggables,u=h.Draggable,c=h.Sortable;u.prototype.startDrag=function(t){if(u.isDragging=!0,this.dragging=!0,this.delta||(this.delta=this.currentDelta()),this.options.zindex&&(this.originalZ=parseInt(Element.getStyle(this.element,"z-index")||0),this.element.style.zIndex=this.options.zindex),this.options.ghosting&&(this._clone=this.element.cloneNode(!0),this._originallyAbsolute="absolute"==this.element.getStyle("position"),this._originallyAbsolute||l.absolutize(this.element),this.element.parentNode.insertBefore(this._clone,this.element),"TR"===this.element.parentNode.tagName&&document.body.appendChild(this.element)),this.options.superghosting){l.prepare();var i=[Event.pointerX(t),Event.pointerY(t)],e=document.getElementsByTagName("body")[0],s=this.element;this._clone=s.cloneNode(!0),r.Browser.IE&&(this._clone.clearAttributes(),this._clone.mergeAttributes(s.cloneNode(!1))),s.parentNode.insertBefore(this._clone,s),s.id="clone_"+s.id,s.hide(),l.absolutize(s),s.parentNode.removeChild(s),e.appendChild(s),"0px"!=s.style.width&&"0px"!=s.style.height||(s.style.width=Element.getWidth(this._clone)+"px",s.style.height=Element.getHeight(this._clone)+"px"),this.originalScrollTop=Element.getHeight(this._clone)/2,this.draw(i),s.show()}if(this.options.scroll)if(this.options.scroll==window){var n=this._getWindowScroll(this.options.scroll);this.originalScrollLeft=n.left,this.originalScrollTop=n.top}else this.originalScrollLeft=this.options.scroll.scrollLeft,this.originalScrollTop=this.options.scroll.scrollTop;d.notify("onStart",this,t),this.options.starteffect&&this.options.starteffect(this.element)},u.prototype.draw=function(t){var i=l.cumulativeOffset(this.element);if(this.options.ghosting){var e=l.realOffset(this.element);i[0]+=e[0]-l.deltaX,i[1]+=e[1]-l.deltaY}var s=this.currentDelta();i[0]-=s[0],i[1]-=s[1],this.options.scroll&&(i[0]-=this.options.scroll.scrollLeft,i[1]-=this.options.scroll.scrollTop),this.options.scroll&&this.options.scroll!=window&&this._isScrollChild&&(i[0]-=this.options.scroll.scrollLeft-this.originalScrollLeft,i[1]-=this.options.scroll.scrollTop-this.originalScrollTop);var n=[0,1].map(function(e){return t[e]-i[e]-(this.options.mouseOffset?-2:this.offset[e])}.bind(this));this.options.snap&&(n=Object.isFunction(this.options.snap)?this.options.snap(n[0],n[1],this):Object.isArray(this.options.snap)?n.map(function(t,i){return(t/this.options.snap[i]).round()*this.options.snap[i]}.bind(this)):n.map(function(t){return(t/this.options.snap).round()*this.options.snap}.bind(this))),this.options.superghosting&&("absolute"==this.element.getStyle("position")?n[1]=t[1]-this.originalScrollTop:n[1]-=this.originalScrollTop||10);var o=this.element.style;this.options.constraint&&"horizontal"!=this.options.constraint||(o.left=n[0]+"px"),this.options.constraint&&"vertical"!=this.options.constraint||(o.top=n[1]+"px"),"hidden"==o.visibility&&(o.visibility="")},u.prototype.initDrag=function(t){if((Object.isUndefined(u._dragging[this.element])||!u._dragging[this.element])&&(t.touches&&1==t.touches.length||Event.isLeftClick(t))){var i=Event.element(t).tagName.toUpperCase();if("INPUT"==i||"SELECT"==i||"OPTION"==i||"BUTTON"==i||"TEXTAREA"==i)return;if(s(this.element).parents("#sortDialog").length>0&&"B"==i)return;var e=[Event.pointerX(t),Event.pointerY(t)],n=l.cumulativeOffset(this.element);this.offset=[0,1].map((function(t){return e[t]-n[t]})),d.activate(this),this.countdown=d.DEFAULT_TOLERANCE,Event.stop(t),this.element.fire("drag:mousedown",{targetEvent:t})}},g.isAffected=function(t,i,e){var n=s(e.element),r=n.width(),l=n.height(),a=n.offset(),h=a.left+r,g=a.top+l,d=t[0]>a.left&&t[0]<h&&t[1]>a.top&&t[1]<g;return e.element!=i&&(i.parentNode===o(document.body)||!e._containers||this.isContained(i,e))&&(!e.accept||Element.classNames(i).detect((function(t){return e.accept.include(t)})))&&d},u.prototype.finishDrag=function(t,i){if(u.isDragging=!1,this.dragging=!1,isIE()&&(document.body.onmousemove=function(){}),this.options.quiet){l.prepare();var e=[Event.pointerX(t),Event.pointerY(t)];g.show(e,this.element)}this.options.ghosting&&(this._originallyAbsolute||(l.relativize(this.element),"TR"===this._clone.parentNode.tagName&&this._clone.parentNode.insertBefore(this.element,this._clone)),delete this._originallyAbsolute,Element.remove(this._clone),this._clone=null);var s=!1;i&&((s=g.fire(t,this.element))||(s=!1)),s&&this.options.onDropped&&this.options.onDropped(this.element),d.notify("onEnd",this,t);var n=this.options.revert;n&&Object.isFunction(n)&&(n=n(this.element));var r=this.currentDelta();n&&this.options.reverteffect?0!=s&&"failure"==n||this.options.reverteffect(this.element,r[1]-this.delta[1],r[0]-this.delta[0]):this.delta=r,this.options.zindex&&(this.element.style.zIndex=this.originalZ,this._clone&&(this._clone.style.zIndex=this.originalZ)),this.options.endeffect&&this.options.endeffect(this.element),this.options.superghosting&&(null==this.element.parentNode&&(Element.hide(this.element),o(document.body).appendChild(this.element)),Element.remove(this.element),new u(this._clone,this.options)),d.deactivate(this),g.reset()},c.defaultOnHover=c.onHover,c.onHover=function(t,i,e){t.hasClassName("dialog")||c.defaultOnHover(t,i,e)},c.defaultOnEmptyHover=c.onEmptyHover,c.onEmptyHover=function(t,i,e){t.hasClassName("dialog")||c.defaultOnEmptyHover(t,i,e)};var p=a.create({initialize:function(t,i){this.element=o(t),this.observer=i,this.lastValue=c.serialize(this.element)},onStart:function(){this.lastValue=c.serialize(this.element)},onEnd:function(t,i){c.unmark(),this.lastValue!=c.serialize(this.element)&&this.observer(this.element,i)}});c.getSortableObserverConstructor=function(){return p},g.show=function(t,i){if(this.drops.length){var e,s=[];this.drops.each((function(e){g.isAffected(t,i,e)&&((i.hasClassName("sortDialogAvailable")||i.hasClassName("sortDialogSortFields"))&&"sortDialogAvailable"!=e.element.id&&"sortDialogSortFields"!=e.element.id||s.push(e))})),s.length>0&&(e=g.findDeepestChild(s)),this.last_active&&this.last_active!=e&&this.deactivate(this.last_active),e&&(l.within(e.element,t[0],t[1]),e.onHover&&(i.classNames().include("wrap")&&(i.relativize(),i.classNames().include("measure")?i.classNames().set("draggable dragging measure"+(i.classNames().include("supportsFilter")?" supportsFilter":"")):i.classNames().set("draggable dragging dimension"+(i.classNames().include("supportsFilter")?" supportsFilter":"")),i.style.position="relative",i.style.display="inline-block",i.style.width="",i.style.height=""),e.onHover(i,e.element,l.overlap(e.overlap,e.element))),e!=this.last_active&&g.activate(e))}},i.Mw=d,i._l=u}}]);
//# sourceMappingURL=chunk.1432.js.map