define(["require","dataSource/model/BaseDataSourceModel","dataSource/enum/connectionTypes","bi/repository/enum/repositoryResourceTypes","underscore","bundle!jasperserver_messages"],function(e){"use strict";var r=e("dataSource/model/BaseDataSourceModel"),o=e("dataSource/enum/connectionTypes"),t=e("bi/repository/enum/repositoryResourceTypes"),a=e("underscore"),n=e("bundle!jasperserver_messages");return r.extend({type:t.BEAN_DATA_SOURCE,defaults:function(){var e={};return a.extend(e,r.prototype.defaults,{beanName:"",beanMethod:"",connectionType:o.BEAN}),e}(),validation:function(){var e={};return a.extend(e,r.prototype.validation,{beanName:[{required:!0,msg:n["ReportDataSourceValidator.error.not.empty.reportDataSource.beanName"]}],beanMethod:[{required:!0,msg:n["ReportDataSourceValidator.error.not.empty.reportDataSource.beanMethod"]}]}),e}()})});