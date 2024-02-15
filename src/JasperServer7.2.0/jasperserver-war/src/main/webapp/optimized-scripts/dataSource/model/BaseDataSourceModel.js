define(["require","bi/repository/model/RepositoryResourceModel","underscore","jquery","backbone","jrs.configs"],function(e){"use strict";var t=e("bi/repository/model/RepositoryResourceModel"),i=e("underscore"),o=e("jquery"),r=e("backbone"),n=e("jrs.configs");return t.extend({defaults:function(){var e={};return i.extend(e,t.prototype.defaults,{connectionType:void 0}),e}(),validation:{},initialize:function(e,o){o=i.defaults(o||{},{contextPath:n.contextPath}),this.options=o,this.isNew()&&o.parentFolderUri&&this.set("parentFolderUri",o.parentFolderUri,{silent:!0});var r=o.parentFolderUri?o.parentFolderUri:e.parentFolderUri;e.name&&r&&!e.uri&&!0===o.isEditMode&&this.set("uri",t.constructUri(r,e.name),{silent:!0}),t.prototype.initialize.call(this,e,o)},testConnection:function(){if(this.validate(),this._isValid){var e=o.Deferred(),t=o("#"+ajax.LOADING_ID)[0],i=!1,s=window.setTimeout(function(){i=!0,dialogs.popup.show(t,!0)},AjaxRequester.prototype.MAX_WAIT_TIME),a=this.toJSON();return r.ajax({type:"POST",url:n.contextPath+"/rest_v2/connections",contentType:a.connectionType,headers:{Accept:"application/json"},data:JSON.stringify(a)}).always(function(){window.clearTimeout(s),i&&dialogs.popup.hide(t)}).done(e.resolve).fail(e.reject),e.promise()}}})});