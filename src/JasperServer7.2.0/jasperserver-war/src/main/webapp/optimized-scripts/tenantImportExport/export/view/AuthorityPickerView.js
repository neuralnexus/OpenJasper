define(["require","jquery","backbone","text!tenantImportExport/export/template/authorityPickerTemplate.htm","text!tenantImportExport/export/template/authorityPickerOptionsTemplate.htm","underscore"],function(e){"use strict";function t(e,t,s){return s<=Math.max(e,t)&&s>=Math.min(e,t)}var s=e("jquery"),i=e("backbone"),n=e("text!tenantImportExport/export/template/authorityPickerTemplate.htm"),l=e("text!tenantImportExport/export/template/authorityPickerOptionsTemplate.htm"),r=e("underscore");return i.View.extend({events:{"click .button.search":"search","click .button.searchClear":"clear","click .button-select-all":"_selectAllHandler","keydown input[type=text]":"keyHandler","mousedown li":"selectionStarted","mouseenter li":"mouseEntered","mouseleave li":"mouseLeave","mouseup ul":"selectionFinished","mouseenter ul":"stopScroll","mouseleave ul":"startScroll"},cursor:-1,baseCursor:-1,selecting:!1,scrollSpeed:30,initialize:function(e){this.model.on("change",r.bind(this.renderList,this)),this.model.on("error:server",r.bind(function(){this.trigger("error:server",arguments)},this)),this.upHandler=r.bind(function(e){this.stopScroll(e),this.selecting=!1},this),this.el=!1,this.initOptions=e},render:function(){return this.mainTemplate||(this.mainTemplate=r.template(n)),this.optionsTemplate||(this.optionsTemplate=r.template(l)),this.el||(this.$el=s("<div/>").html(this.mainTemplate(this.initOptions)).children(),this.el=this.$el[0],this.subEl=this.$el.find(".authorityPicker ul"),this.delegateEvents()),this.model.context||this.model.setContext(),this},renderList:function(){return this.subEl.html(this.optionsTemplate(this.model.attributes)).find("li").each(function(e,t){s(t).attr("index",e)}),this.cursor=-1,this.baseCursor=-1,this.scrollSpeed=this.subEl.find("li:eq(0)").height(),this.subEl.scrollTop(0),this.trigger("change:selection",[]),this},getSelected:function(){return r.reduce(this.subEl.find(".selected"),function(e,t){var i=r.map(s(t).find("span"),function(e){return s(e).html()});return e.push(i.join("|")),e},[])},search:function(){var e=this.$el.find("input[type=text]").val();this.model.setContext({searchString:e}),e?this.$el.find(".button.searchClear").addClass("up"):this.$el.find(".button.searchClear").removeClass("up")},_selectAllHandler:function(){this.selectAll()},clear:function(){this.$el.find("input[type=text]").val(""),this.search()},keyHandler:function(e){13===e.which&&this.search()},setDisabled:function(e){var t=this.$el.find(".button-select-all").children();e?(this.subEl.addClass("disabled"),t.addClass("disabled"),this.undelegateEvents()):(this.subEl.removeClass("disabled"),t.removeClass("disabled"),this.delegateEvents()),this.$el.find("input[type=text]").attr("disabled",e)},highlightSet:function(e){this.subEl.find("li").each(function(t,i){i=s(i);var n=r.map(i.find("span"),function(e){return s(e).html()});i.toggleClass("highlighted",r.contains(e,n.join("|")))})},selectAll:function(e){this.subEl.find("li").each(function(e,t){s(t).addClass("selected")}),e||this.trigger("change:selection",this.getSelected())},selectNone:function(e){this.subEl.find("li").each(function(e,t){s(t).removeClass("selected")}),e||this.trigger("change:selection",this.getSelected())},selectInverse:function(e){this.subEl.find("li").each(function(e,t){s(t).toggleClass("selected")}),e||this.trigger("change:selection",this.getSelected())},selectItem:function(e,t){this.subEl.find("li[index="+e+"]").addClass("selected"),t||this.trigger("change:selection",this.getSelected())},unSelectItem:function(e,t){this.subEl.find("li[index="+e+"]").removeClass("selected"),t||this.trigger("change:selection",this.getSelected())},selectRange:function(e,t,i){var n=Math.min(e,t),l=Math.max(e,t);this.subEl.find("li:lt("+(l+1)+")").each(function(e,t){e>=n&&s(t).addClass("selected")}),i||this.trigger("change:selection",this.getSelected())},selectionStarted:function(e){this.selecting=!0,e.ctrlKey||e.metaKey||this.selectNone(!0);var t=+s(e.target).parents("li").toggleClass("selected").attr("index");e.shiftKey?-1!=this.cursor&&this.selectRange(this.cursor,t,!0):(this.cursor=t,this.baseCursor=this.cursor)},mouseEntered:function(e){this.selecting&&(this.cursor=+s(e.target).parents("li").addClass("selected").attr("index"))},mouseLeave:function(e){if(this.selecting){var i=s(e.target).parents("li"),n=s(e.relatedTarget).parents("li");t(this.cursor,this.baseCursor,+n.attr("index"))&&i.removeClass("selected")}},selectionFinished:function(e){this.selecting=!1,this.trigger("change:selection",this.getSelected())},startScroll:function(e){if(this.selecting){if(s(e.relatedTarget).hasClass("upper"))this.direction=-1;else{if(!s(e.relatedTarget).hasClass("lower"))return void(this.selecting=!1);this.direction=1}s(document.body).on("mouseup",this.upHandler),this.scrollTimer=setInterval(r.bind(this.scrollList,this),200)}},stopScroll:function(e){this.selecting&&(clearInterval(this.scrollTimer),this.direction=0,s(document.body).off("mouseup",this.upHandler))},scrollList:function(){this.subEl.scrollTop(+this.subEl.scrollTop()+this.scrollSpeed*this.direction),t(this.cursor,this.baseCursor,this.cursor+this.direction)?this.unSelectItem(this.cursor,!0):this.selectItem(this.cursor+this.direction,!0),this.cursor=this.cursor+this.direction}})});