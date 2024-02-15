define(["require","underscore","backbone","actionModel.primaryNavigation","tenantImportExport/model/PermissionModel"],function(t){var e=t("underscore"),n=t("backbone"),a=t("actionModel.primaryNavigation"),i=t("tenantImportExport/model/PermissionModel");return n.Model.extend({initialize:function(t){t&&(this.roleName=t.roleName,this.external=t.external,this.tenantId=t.tenantId),t.permissionToDisplay&&(this.permission=new i(t.permissionToDisplay))},getDisplayName:function(){return this.roleName},getNameWithTenant:function(){return this.tenantId&&!this.tenantId.blank()?this.roleName+"|"+this.tenantId:this.roleName},getManagerURL:function(){return"flow.html?"+Object.toQueryString({_flowId:"roleListFlow",text:void 0!==this.roleName?encodeURIComponent(this.roleName):this.roleName,tenantId:void 0!==this.tenantId?encodeURIComponent(this.tenantId):this.tenantId})},navigateToManager:function(){a.navigationPaths.tempNavigateToManager=e.cloneDeep(a.navigationPaths.role),a.navigationPaths.tempNavigateToManager.params+="&"+Object.toQueryString({text:this.roleName,tenantId:this.tenantId}),a.navigationOption("tempNavigateToManager")},equals:function(t){return t&&this.roleName==t.roleName&&this.tenantId==t.tenantId},toPermissionData:function(t){return{roleName:this.roleName,tenantId:this.tenantId,permissionToDisplay:this.permission.toData()}}})});