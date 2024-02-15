define(["require","jquery","underscore","bundle!all","backbone","utils.common","common/util/domUtil","touchcontroller","tools.infiniteScroll","scheduler/view/jobView","scheduler/model/jobModel","scheduler/collection/jobsCollection","scheduler/util/schedulerUtils","text!scheduler/template/jobsViewTemplate.htm","text!scheduler/template/masterViewTemplate.htm","text!scheduler/template/list/listOfJobsTemplate.htm","text!scheduler/template/list/nothingToDisplayTemplate.htm"],function(e){"use strict";var t=e("jquery"),i=e("underscore"),o=e("bundle!all"),n=e("backbone"),l=(e("utils.common"),e("common/util/domUtil")),s=e("touchcontroller"),r=e("tools.infiniteScroll"),c=e("scheduler/view/jobView"),h=(e("scheduler/model/jobModel"),e("scheduler/collection/jobsCollection")),a=(e("scheduler/util/schedulerUtils"),e("text!scheduler/template/jobsViewTemplate.htm")),d=e("text!scheduler/template/masterViewTemplate.htm"),u=e("text!scheduler/template/list/listOfJobsTemplate.htm"),m=e("text!scheduler/template/list/nothingToDisplayTemplate.htm");return n.View.extend({events:{"click [name=backButton]":"backButtonClick","click [name=scheduleJob]":"createButtonClick","click [name=runJob]":"runButtonClick","click [name=refreshList]":"refreshButtonClick","keydown [name=jobSearchInput]":"jobSearchInputKeyDown","click [name=jobSearchIcon]":"jobSearchIconClick","click [name=jobSearchClear]":"clearJobSearch"},initialize:function(e){this.options=i.extend({},e),this.options=i.omit(this.options,"model"),this.jobsViewCollection=new h(null,this.options),this.listenTo(this.jobsViewCollection,"add",this.createAndAddJobView),this.listenTo(this.jobsViewCollection,"reset destroy",this.renderCollection)},refresh:function(){this.jobsViewCollection.fetch()},searchJobs:function(){var e=this.$el.find("[name=jobSearchInput]").val();e&&(this.$el.find("[name=jobSearchClear]").addClass("up"),this.jobsViewCollection.setSearchingTerm(e),this.jobsViewCollection.fetch())},clearJobSearch:function(){this.$el.find("[name=jobSearchClear]").removeClass("up"),this.$el.find("[name=jobSearchInput]").val(""),this.jobsViewCollection.setSearchingTerm(""),this.jobsViewCollection.fetch()},render:function(){var e=this.options.masterViewMode?d:a,n={i18n:o,reportUri:this.options.reportUri,searchTerm:this.jobsViewCollection.getSearchingTerm()};this.setElement(t(i.template(e,n))),this.renderCollection()},renderCollection:function(){var e=this.$el.find("[name=jobsContainer]").empty();this.jobsViewCollection.length?(e.append(t(i.template(u,{}))),this.jobsViewCollection.each(i.bind(this.createAndAddJobView,this)),this.initInfiniteScroll(),this.adjustTableHeaderWidth()):e.append(t(i.template(m,{message:this.options.masterViewMode?o["report.scheduling.list.no.jobs"]:o["report.scheduling.list.no.jobs.for.report"]})))},initInfiniteScroll:function(){var e=this.$el.find("#listOfJobs")[0],o=this.$el.find("#jobsSummaryContainer")[0];this.options.masterViewMode&&e&&(isSupportsTouch()&&(this._touchController||(this._touchController=new s(e,e.up(),{forceLayout:!0})),this.listenTo(e,"layout_update orientationchange",function(){t(e).css("min-width",t(o).width()+"px"),e.width(t(o).width())})),isIPad()?this._infiniteScroll=new r({control:o,content:e,scroll:this._touchController||void 0}):this._infiniteScroll=new r({control:o,content:e}),this._infiniteScroll.onLoad=i.bind(function(){this._infiniteScroll.wait(),this.jobsViewCollection.loadMoreJobs(),this._infiniteScroll.stopWaiting()},this))},adjustTableHeaderWidth:function(){var e=this.$el.find(".content > .subheader .wrap"),t=this.$el.find(".content > .body"),i=l.hasScrollBar(t[0],"vertical");e.css("margin-right",(i?15:0)+"px")},jobSearchInputKeyDown:function(e){27===e.keyCode&&(e.preventDefault(),e.stopPropagation(),this.clearJobSearch()),13===e.keyCode&&(e.preventDefault(),e.stopPropagation(),this.searchJobs())},jobSearchIconClick:function(){this.searchJobs()},createAndAddJobView:function(e){var t=new c({model:e,masterViewMode:this.options.masterViewMode});this.listenTo(t,"editJobPressed",this.editJobPressed);var i=t.render().$el;this.$el.find("[name=listOfJobs]").append(i)},editJobPressed:function(e){this.trigger("editJobPressed",e)},backButtonClick:function(){this.trigger("backButtonPressed")},createButtonClick:function(){this.trigger("createNewJobRequest")},runButtonClick:function(){this.trigger("runNowRequest")},refreshButtonClick:function(){this.refresh()}})});