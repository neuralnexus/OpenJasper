define(["require","jquery","underscore","common/util/classUtil"],function(t){"use strict";var i=t("jquery"),h=t("underscore"),e=t("common/util/classUtil");return e.extend({constructor:function(t,i){this.padding=i?i:{top:5,left:0},this.setAttachTo(t)},setAttachTo:function(t){t&&i(t).length>0?this.$attachTo=i(t):this.$attachTo=i("<div></div>")},show:function(){var t=this.$attachTo.offset(),e=this.$attachTo[0].tagName&&"input"===this.$attachTo[0].tagName.toLowerCase()?this.$attachTo.outerHeight():this.$attachTo.height(),s=this.$attachTo.width(),o=i("body"),a=o.height(),n=o.width(),c=this.$el.innerWidth(),r=this.$el.innerHeight(),d=t.top+e+this.padding.top,l=t.left,p=t.top+e+this.padding.top,u=t.left+s;r+d>a&&(p=t.top-r-this.padding.top),0>p&&(p=t.top-r/2-this.padding.top),0>p&&(p=0),c+l>n&&(u=t.left-c),0>u&&(u=t.left+s/2-c/2),0>u&&(u=0),p>t.top&&n>c+l&&(u-=s),h.extend(this,{top:p,left:u}),this.$el.css({top:this.top,left:this.left}),this.$el.show(),this.trigger("show",this)},hide:function(){return this.$el.hide(),this.trigger("hide",this),this}})});