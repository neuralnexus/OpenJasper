define(["require","request","requestSettings","underscore","jrs.configs"],function(e){"use strict";var t=e("request"),r=e("requestSettings"),n=e("underscore"),s=e("jrs.configs"),u=s.contextPath+"/rest_v2/bundles",i=function(e,s){var i;"all"===e?i="?expanded=true":(e=e.split("/"),i="/"+e[e.length-1]);var a=n.extend({},r,{type:"GET",dataType:"json",url:u+i});a.headers["Cache-Control"]="private",delete a.headers.Pragma,t(a).then(function(t){s("all"!==e?t:n(t).reduce(function(e,t){return n.extend(e,t)},{}))})};return i.load=function(e,t,r,n){if(n.isBuild)return void r();i(e,r)},i});