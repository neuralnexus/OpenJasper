define(["require","exports","module","./BiComponentError","./enum/biComponentErrorCodes","./enum/biComponentErrorMessages"],function(o,r,e){var n=o("./BiComponentError"),t=o("./enum/biComponentErrorCodes"),E=o("./enum/biComponentErrorMessages");e.exports=n.extend({constructor:function(o){n.prototype.constructor.call(this,t.ALREADY_DESTROYED_ERROR,E[t.ALREADY_DESTROYED_ERROR])}})});