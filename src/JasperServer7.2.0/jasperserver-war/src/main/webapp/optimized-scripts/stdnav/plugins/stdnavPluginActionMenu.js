define(["require","exports","module","jquery","underscore","logger","stdnav","actionModel.modelGenerator","core.events.bis","core.layout","actionModel.primaryNavigation"],function(t,e,n){"use strict";var s=t("jquery"),i=t("underscore"),o=t("logger").register(n),r=t("stdnav"),l=t("actionModel.modelGenerator"),u=t("core.events.bis"),a=t("core.layout"),c=t("actionModel.primaryNavigation"),h=0,_=function(){h++,this.serial=h,this.menu_item_callbacks={click:{}}};return i.extend(_.prototype,{zinit:function(t){return this},activate:function(){this.behavior={down:[this,this._onDown,null],enter:null,exit:[this,this._onExit,null],fixfocus:[this,this._fixFocus,null],fixsubfocus:[this,this._fixFocus,null],fixsuperfocus:[this,this._fixSuperfocus,null],focusin:[this,this._onFocusIn,null],focusout:[this,this._onFocusOut,null],subfocusin:[this,this._onSubfocusIn,null],left:[this,this._onLeft,null],right:[this,this._onRight,null],superfocusin:[this,this._onSuperfocusIn,null],superfocusout:[this,this._onSuperfocusOut,null],up:[this,this._onUp,null],inherit:!1,inheritable:!0},r.registerNavtype(this.navtype,this.behavior,this.navtype_tags)},deactivate:function(){r.unregisterNavtype(this.navtype,this)},_fixFocus:function(t){var e,n=s(t);if(n.is("div,ul,ol")){var i=n.find(".pressed");i.length>0?e=i[0]:(i=n.find("li"),e=i.length>0?i[0]:t)}else if(n.is("li"))e=t;else{var o=n.closest("li");o.length>0?e=!1===s(o[0]).prop["js-navigable"]?n.closest("ul,ol"):o[0]:(e=n.closest("ul,ol"),e=n.find("li"),e.length>0&&(e=e[0]))}return e},_fixSuperfocus:function(t){var e=s(t).closest(".menuRoot,.dropDown,.context");return e.length>0?e[0]:null},_onSuperfocusIn:function(t){var e=s(t),n=s(this.lastMenuBarItem).closest(".menuRoot"),i=e.closest(".menu").length&&e;return i&&n.attr("tabindex")>-1&&(this._parentTabindex=n.attr("tabindex"),this._parentTabindex>-1?(i.attr("js-suspended-tabindex",this._parentTabindex),i.find("li:first").attr("tabindex",this._parentTabindex)):(e.attr("js-suspended-tabindex","none"),i.find("li:first").attr("tabindex",-1)),i.attr("tabindex","-1"),n.attr("tabindex","-1")),t},_onFocusIn:function(t){var e,n,i=(s(t),s(t));return i.length>0&&(e=i.closest(a.NAVIGATION_PATTERN),e.length>0?(u.over(e.find(a.BUTTON_PATTERN)[0]),n=e.closest(a.NAVIGATION_MUTTON_PATTERN),n.length>0?c.showNavButtonMenu(null,n[0]):l.hideMenu()):(e=i.closest(a.MENU_LIST_PATTERN),e.length>0&&(u.over(e.find(a.BUTTON_PATTERN)[0]),n=e.closest(a.NAVIGATION_MUTTON_PATTERN)))),t},_onFocusOut:function(t){var e=s(t).closest(a.NAVIGATION_PATTERN);return e.length>0?this.lastMenuBarItem!==t&&(u.out(e.find(a.BUTTON_PATTERN)[0]),e.removeAttr("tabindex")):(e=s(t).closest(a.MENU_LIST_PATTERN),e.length>0&&(u.out(e.find(a.BUTTON_PATTERN)[0]),e.removeAttr("tabindex"))),null},_onSuperfocusOut:function(t){var e=s("#"+a.MAIN_NAVIGATION_ID);if(e.length<1)return t;if(s(document.activeElement).closest(".dropDown,.context").length<1){var n=e.find("."+a.HOVERED_CLASS);n.length>0&&u.out(n[0]),l.hideMenu(),s(this.lastMenuBarItem).closest(".menuRoot").attr("tabindex",this._parentTabindex),this.lastMenuBarItem=null}},_focus_prev_menu_entry:function(t){var e;if(t.hasClass("node")&&!t.children(".menu").hasClass("is-closed")&&!0,e=t.prev(),0===e.length)for(e=t;e.next().length>0;)e=e.next();o.debug("Granting focus to "+e.attr("id")),r.setSubfocus(e)},_focus_next_menu_entry:function(t){var e;if(t.hasClass("node")&&!t.children(".menu").hasClass("is-closed")&&!0,e=t.next(),0===e.length)for(e=t;e.prev().length>0;)e=e.prev();o.debug("Granting focus to "+e.attr("id")),r.setSubfocus(e)},_onSubfocusIn:function(t){var e=s(t).closest(".menuRoot");if(e.attr("js-suspended-tabindex")>-1?s(t).attr("tabindex",e.attr("js-suspended-tabindex")):e.attr("tabindex")>-1&&(s(t).attr("tabindex",e.attr("tabindex")),e.attr("js-suspended-tabindex",e.attr("tabindex"))),"li"===s(t).prop("nodeName")==!1){var n=this._fixFocus(t);r.setSubfocus(n,!1)}},_onExit:function(t){var e=s(t);return t=!e.closest("#"+a.MAIN_NAVIGATION_ID).length&&e.find("p").length>0?this._onExitHandler(t):s("#"+a.MAIN_SEARCH_INPUT_ID)[0]},_onLeft:function(t){var e=s(t).closest(a.NAVIGATION_PATTERN),n=s(t);return!e.length&&s(t).closest(".menu").length&&(e=s(this._onExitHandler(t))),e.length>0?n=e.prev(a.NAVIGATION_PATTERN):(e=s(t).closest(a.MENU_LIST_PATTERN),e.length>0&&(n=s(this.lastMenuBarItem).prev(a.NAVIGATION_PATTERN))),n.length>0?n[0]:t},_onRight:function(t){var e=s(t).closest(a.NAVIGATION_PATTERN),n=s(t);return!e.length&&s(t).closest(".menu").length&&(e=s(this._onExitHandler(t))),e.length>0?n=e.next(a.NAVIGATION_PATTERN):(e=s(t).closest(a.MENU_LIST_PATTERN),e.length>0&&(n=s(this.lastMenuBarItem).next(a.NAVIGATION_PATTERN))),n.length>0?n[0]:t},_onUp:function(t){var e=s(t);if(!(s(document.activeElement).closest("."+l.DROP_DOWN_MENU_CLASS).length>0))return t;var n=s(document.activeElement).closest(a.MENU_LIST_PATTERN);for(e=n.prev(a.MENU_LIST_PATTERN);e.is(a.SEPARATOR_PATTERN);)e=e.prev(a.MENU_LIST_PATTERN);return e.length<1&&(e=s(this.lastMenuBarItem)),e.length>0?e[0]:this._onExitHandler(t)},_onDown:function(t){var e=s(t),n=s(document.activeElement).closest("."+l.DROP_DOWN_MENU_CLASS);if(n.length>0){var i=s(document.activeElement).closest(a.MENU_LIST_PATTERN);for(e=i.next(a.MENU_LIST_PATTERN);e.is(a.SEPARATOR_PATTERN);)e=e.next(a.MENU_LIST_PATTERN)}else{if(n=s(document.activeElement).closest("."+a.MENU_ROOT_CLASS),n.length<1)return t;l.isMenuShowing()&&(this.lastMenuBarItem=t,s(t).addClass("isParent"),e=s(a.MENU_LIST_PATTERN),n=e.closest(".menuRoot"),n.attr("tabindex",s(t).attr("tabindex")))}return e.length>0?e[0]:t},_onExitHandler:function(t){return t=this.lastMenuBarItem,t||(t=s(".isParent")[0]),s(t).removeClass("isParent"),t}}),s.extend(_.prototype,{navtype:"actionmenu",navtype_tags:[]}),new _});