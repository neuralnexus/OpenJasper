define(["require","./BiComponentError","./enum/biComponentErrorCodes","./enum/biComponentErrorMessages"],function(r){"use strict";var o=r("./BiComponentError"),e=r("./enum/biComponentErrorCodes"),n=r("./enum/biComponentErrorMessages");return o.extend({constructor:function(r){o.prototype.constructor.call(this,e.CONTAINER_NOT_FOUND_ERROR,n[e.CONTAINER_NOT_FOUND_ERROR],[r])}})});