define(["require","exports","module","./BiComponentError","./enum/biComponentErrorCodes","./enum/biComponentErrorMessages"],function(o,r,e){var n=o("./BiComponentError"),t=o("./enum/biComponentErrorCodes"),E=o("./enum/biComponentErrorMessages");e.exports=n.extend({constructor:function(){n.prototype.constructor.call(this,t.NOT_YET_RENDERED_ERROR,E[t.NOT_YET_RENDERED_ERROR])}})});