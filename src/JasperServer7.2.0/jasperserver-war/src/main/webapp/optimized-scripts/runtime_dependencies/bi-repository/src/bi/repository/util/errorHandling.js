define(["require","underscore","logger","bundle!RepositoryResourceBundle"],function(r){function e(r,e){var o=e[r.status]||e.unknown;return n.isString(o)?e[r.status]:n.isFunction(o)?o(r):void 0}var n=r("underscore"),o=r("logger").register("ResourceErrors"),s=r("bundle!RepositoryResourceBundle");return{mapXhrErrorToMessage:function(r,n){var u,t=r&&r.responseJSON&&r.responseJSON.message,i=r&&r.responseJSON&&r.responseJSON.errorCode;return u=e(r,n)||t||s["error.unknown.error"],o.warn(r.status,i,t),u}}});