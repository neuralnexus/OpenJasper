define(["require","underscore","common/component/baseTable/childView/BaseRow","serverSettingsCommon/enum/confirmDialogTypesEnum","text!../templates/itemViewTemplate.htm","serverSettingsCommon/behaviors/DeleteConfirmBehavior"],function(e){var t=e("underscore"),o=e("common/component/baseTable/childView/BaseRow"),m=(e("serverSettingsCommon/enum/confirmDialogTypesEnum"),e("text!../templates/itemViewTemplate.htm")),n=e("serverSettingsCommon/behaviors/DeleteConfirmBehavior");return o.extend({tagName:"div",className:"table-row",template:t.template(m),behaviors:t.extend(o.prototype.behaviors,{DeleteConfirm:{behaviorClass:n}})})});