define(["require","./Menu","common/component/base/AttachableComponent"],function(t){"use strict";var n=t("./Menu"),e=t("common/component/base/AttachableComponent");return n.extend(e.extend({constructor:function(t,o,p,s){this.padding=p||{top:0,left:0},e.call(this,o,this.padding),n.call(this,t,s)},show:function(){return e.prototype.show.apply(this,arguments),n.prototype.show.apply(this,arguments)}}).prototype)});