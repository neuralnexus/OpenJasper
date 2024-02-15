define(["require","backbone","underscore","dataSource/model/JdbcDriverModel"],function(e){"use strict";var t=e("backbone"),r=e("underscore"),i=e("dataSource/model/JdbcDriverModel");return t.Collection.extend({model:i,initialize:function(e,t){this.options=t},getDefaultDriver:function(){var e=this.find(function(e){return!r.isUndefined(e.get("default"))&&!1!==e.get("default")});return e||this.first()},set:function(e,i){return void 0===i&&(i={}),r.extend(i,this.options),t.Collection.prototype.set.call(this,e,i)},getDriverByClass:function(e){var t=this.findWhere({jdbcDriverClass:e});return t||this.findWhere({jdbcDriverClass:i.OTHER_DRIVER})},getAllPossibleCustomAttributes:function(){return r.keys(i.VALIDATION_PATTERNS)}})});