define(["require","underscore","bundle!AttributesBundle","bundle!CommonBundle","common/component/baseTable/childView/BaseRow","attributes/enum/attributesTypesEnum","attributes/factory/rowTemplatesFactory"],function(e){var t=e("underscore"),n=e("bundle!AttributesBundle"),i=e("bundle!CommonBundle"),r=e("common/component/baseTable/childView/BaseRow"),u=e("attributes/enum/attributesTypesEnum"),o=e("attributes/factory/rowTemplatesFactory");return r.extend({className:"table-row",template:t.template(o({readOnly:!0})),templateHelpers:function(){return{i18n:n,i18n2:i,type:this.type,types:u,encrypted:"~secure~"}},computeds:{encrypt:{get:function(){return"~secure~"},set:function(e){this.setBinding("value",e)}}},initialize:function(e){this.type=e.type,r.prototype.initialize.apply(this,arguments)}})});