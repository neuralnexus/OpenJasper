define(["require","backbone.epoxy"],function(e){return e("backbone.epoxy").Model.extend({defaults:{id:void 0,name:void 0,value:"",description:""},initialize:function(){this.get("id")||this.setId()},url:function(){var e=encodeURIComponent(this.id).replace("'","%27");return e=e.replace("'","%27"),this.collection.url(this.isNew()?"":e)},setId:function(){var e=this.get("name");e!==this.get("id")&&this.set("id",e)}})});