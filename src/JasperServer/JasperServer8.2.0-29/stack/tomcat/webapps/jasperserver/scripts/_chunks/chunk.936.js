(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[936],{68453:(t,e,i)=>{"use strict";var r=i(72157),n=i.n(r),a=(i(32314),"jr"),o="jr-"+n().datepicker.dpDiv.attr("id");n().datepicker._mainDivId=o,n().datepicker.dpDiv.attr("id",o),n().datepicker.dpDiv.removeClass();n().datepicker.dpDiv.addClass("jr-jDatepickerPopupContainer ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all jr");var s=n().datepicker._newInst;n().datepicker._newInst=function(){var t=s.apply(n().datepicker,arguments);return t.dpDiv.removeClass(a),t.dpDiv.addClass(a),t};var u=n().datepicker._gotoToday;n().datepicker._gotoToday=function(t){u.call(this,t),this._selectDate(t)};i(267),i(17489);i(57042);var c=n().timepicker._newInst;n().timepicker._newInst=function(t,e){e.onChangeMonthYear||(e.onChangeMonthYear=function(t,e,i,r){i.currentYear=i.selectedYear,i.currentMonth=i.selectedMonth,i.currentDay=i.selectedDay,r._updateDateTime(i)});var i=c.call(n().timepicker,t,e),r=i._onTimeChange;return i._onTimeChange=function(){return this.$timeObj[0].setSelectionRange=null,r.apply(this,arguments)},i}},98354:(t,e,i)=>{"use strict";i.d(e,{Z:()=>o});var r=i(97836),n=i.n(r),a=function(t,e,i){this.setKeyword(t),this.setSign(e),this.setNumber(i)};a.prototype.setKeyword=function(t){this.keyword=t.toUpperCase()},a.prototype.setSign=function(t){this.sign=t},a.prototype.setNumber=function(t){if(n().isNumber(t))this.number=t;else{var e=parseInt(t,10);isNaN(e)?this.number=0:this.number=e}},a.prototype.toString=function(){return n().isNumber(this.number)&&!isNaN(this.number)&&this.number>0&&("+"===this.sign||"-"===this.sign)&&this.keyword in a.PATTERNS?this.keyword+this.sign+this.number.toString():this.keyword in a.PATTERNS?this.keyword:""},a.PATTERNS={DAY:/^(DAY)(([+|\-])(\d{1,9}))?$/i,WEEK:/^(WEEK)(([+|\-])(\d{1,9}))?$/i,MONTH:/^(MONTH)(([+|\-])(\d{1,9}))?$/i,QUARTER:/^(QUARTER)(([+|\-])(\d{1,9}))?$/i,SEMI:/^(SEMI)(([+|\-])(\d{1,9}))?$/i,YEAR:/^(YEAR)(([+|\-])(\d{1,9}))?$/i},a.parse=function(t){if(a.isValid(t))for(var e in a.PATTERNS){var i=a.PATTERNS[e].exec(t);if(null!==i&&n().isArray(i)&&5===i.length)return new a(i[1],i[3],i[4])}},a.isValid=function(t){if(t instanceof a)return""!==t.toString();if(n().isString(t))for(var e in a.PATTERNS)if(a.PATTERNS[e].test(t))return!0;return!1};const o=a},55149:(t,e,i)=>{"use strict";i.d(e,{Z:()=>O});var r=i(12354),n=i.n(r),a=i(71914),o=i(97836),s=i.n(o),u=i(98354),c=function(){u.Z.apply(this,arguments)},m=function(){};m.prototype=u.Z.prototype,(c.prototype=new m).constructor=c,c.PATTERNS={DAY:/^(DAY)(([+|\-])(\d{1,9}))?$/i,WEEK:/^(WEEK)(([+|\-])(\d{1,9}))?$/i,MONTH:/^(MONTH)(([+|\-])(\d{1,9}))?$/i,QUARTER:/^(QUARTER)(([+|\-])(\d{1,9}))?$/i,SEMI:/^(SEMI)(([+|\-])(\d{1,9}))?$/i,YEAR:/^(YEAR)(([+|\-])(\d{1,9}))?$/i},c.parse=function(t){if(c.isValid(t))for(var e in c.PATTERNS){var i=c.PATTERNS[e].exec(t);if(null!==i&&s().isArray(i)&&5===i.length)return new c(i[1],i[3],i[4])}},c.isValid=function(t){if(t instanceof c)return""!==t.toString();if(s().isString(t))for(var e in c.PATTERNS)if(c.PATTERNS[e].test(t))return!0;return!1};const d=c;var f=a.Z.localeSettings,p=a.Z.userTimezone,l="YYYY-MM-DD[T]HH:mm:ss",T="YYYY-MM-DD",D="HH:mm:ss";function h(t){var e=t;return e=t.indexOf("yy")>-1?e.replace("yy","YYYY"):e.replace("y","YY"),t.indexOf("mm")>-1?e=e.replace("mm","MM"):t.indexOf("MM")>-1?e=e.replace("MM","MMMM"):t.indexOf("m")>-1?e=e.replace("m","M"):t.indexOf("M")>-1&&(e=e.replace("M","MMM")),t.indexOf("dd")>-1?e=e.replace("dd","DD"):t.indexOf("DD")>-1?e=e.replace("DD","dddd"):t.indexOf("d")>-1?e=e.replace("d","D"):t.indexOf("D")>-1&&(e=e.replace("D","ddd")),e}function v(t){return t.toUpperCase()}function y(t){return t.toLowerCase().replace(/h/g,"H")}function z(t){return t.replace("aaa","a")}function M(t,e,i){return h(t)+(null!=i?i:" ")+y(e)}function E(t){return n()(t,h(f.dateFormat),!0)}function S(t){return n()(t,M(f.dateFormat,f.timeFormat,f.timestampSeparator),!0)}function g(t){return s().isDate(t)&&n()(t).isValid()||s().isString(t)&&""!==t&&E(t).isValid()}function A(t){return s().isDate(t)&&n()(t).isValid()||s().isString(t)&&""!==t&&S(t).isValid()}function R(t,e){return t.isBefore(e)?-1:t.isAfter(e)?1:0}function Y(t){return N(t,M(f.dateFormat,f.timeFormat,f.timestampSeparator),l)}function k(t){return N(t,h(f.dateFormat),T)}function N(t,e,i){if(u.Z.isValid(t))return t;var r=n()(t,e,!0);return r.isValid()?r.format(i):t}const O={isRelativeDate:function(t){return u.Z.isValid(t)},isRelativeTimestamp:function(t){return d.isValid(t)},isDate:g,isTimestamp:A,isIso8601Timestamp:function(t){return n()(t,l,!0).isValid()},compareDates:function(t,e){if(g(t)&&g(e))return R(s().isDate(t)?n()(t):E(t),s().isDate(e)?n()(e):E(e))},compareTimestamps:function(t,e){if(A(t)&&A(e))return R(s().isDate(t)?n()(t):S(t),s().isDate(e)?n()(e):S(e))},iso8601DateToMoment:function(t){return n()(t,T,!0)},momentToIso8601Date:function(t){return t.format(T)},momentToLocalizedDate:function(t){return t.format(h(f.dateFormat))},dateObjectToIso8601Timestamp:function(t){var e=n()(t);if(s().isDate(t)&&e.isValid())return e.format(l)},iso8601TimestampToDateObject:function(t){var e=n()(t,l,!0);if(e.isValid())return e.toDate()},localizedTimestampToIsoTimestamp:Y,isoTimestampToLocalizedTimestamp:function(t){return N(t,l,M(f.dateFormat,f.timeFormat,f.timestampSeparator))},localizedDateToIsoDate:k,isoDateToLocalizedDate:function(t){return N(t,T,h(f.dateFormat))},localizedTimeToIsoTime:function(t){return N(t,y(f.timeFormat),D)},isoTimeToLocalizedTime:function(t){return N(t,D,y(f.timeFormat))},toMomentDateOrTimeOrTimestampPattern:function(t,e,i){i=void 0!==i&&i,e=e||" ";var r=t.split(e),n=r[0],a=r.length>1?r[1]:null,o=i?v:h,s=i?z:y;(n.indexOf("h")>=0||n.indexOf("H")>=0||n.indexOf("s")>=0)&&(a=n,n=null);var u="";return n&&(u+=o(n)),a&&(u&&(u+=e),u+=s(a)),u},isoDateToLocalizedDateByTimezone:function(t,e){return(e?n().tz(t,e).tz(p):n().utc(t).tz(p)).format(h(f.dateFormat))},localizedDateToIsoDateByTimezone:function(t,e){t=k(t);var i=n().tz(t,p);return(i=e?i.tz(e):i.utc()).format(T)},isoTimeToLocalizedTimeByTimezone:function(t,e){var i=n().duration(t),r=n().utc();return e&&r.tz(e),r.hours(i.hours()),r.minutes(i.minutes()),r.seconds(i.seconds()),r.tz(p).format(y(f.timeFormat))},localizedTimeToIsoTimeByTimezone:function(t,e){var i=n().duration(t),r=n().utc().tz(p);return r.hours(i.hours()),r.minutes(i.minutes()),r.seconds(i.seconds()),(e?r.tz(e):r.utc()).format(D)},isoTimestampToLocalizedTimestampByTimezone:function(t,e){return(e?n().tz(t,e).tz(p):n().utc(t).tz(p)).format(M(f.dateFormat,f.timeFormat,f.timestampSeparator))},localizedTimestampToIsoTimestampByTimezone:function(t,e){t=Y(t);var i=n().tz(t,p);return(i=e?i.tz(e):i.utc()).format(l)}}}}]);
//# sourceMappingURL=chunk.936.js.map