(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[2362],{74238:(r,e,t)=>{"use strict";t.d(e,{Z:()=>s});var n=t(15892),o=t.n(n);const s=o().Model.extend({constructor:function(r,e){e||(e={}),this.parent=e.parent,o().Model.prototype.constructor.call(this,r,e)},_notify:function(r){this.parent._notify(r)},handleServerError:function(r){var e=this.get("uiModuleType");e&&e.handleServerError(r)},handleClientError:function(r){var e=this.get("uiModuleType");e&&e.handleClientError(r)}})},42362:(r,e,t)=>{"use strict";t.r(e),t.d(e,{default:()=>u});var n=t(97836),o=t.n(n),s=t(74238),a=t(19698);const u=s.Z.extend({defaults:function(){return{bookmarks:[],id:void 0,type:a.Z.BOOKMARKS}},constructor:function(r,e){e||(e={}),e.parse||(e=o().extend({},e,{parse:!0})),s.Z.call(this,r,e)},parse:function(r){return r.bookmarks=this._processBookmarks(r.bookmarks),r},_processBookmarks:function(r){if(r){var e=this;return o().map(r,(function(r){return{anchor:r.label,page:r.pageIndex+1,elementAddress:r.elementAddress,bookmarks:e._processBookmarks(r.bookmarks)}}))}return null}})}}]);
//# sourceMappingURL=chunk.2362.js.map