(self.webpackChunkjrs_ui=self.webpackChunkjrs_ui||[]).push([[2265],{42265:function(t,e,_){var n,o,r,s;function d(t){return(d="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol&&t!==Symbol.prototype?"symbol":typeof t})(t)}
//! moment.js locale configuration
//! locale : Georgian [ka]
//! author : Irakli Janiashvili : https://github.com/IrakliJani
s=function(t){"use strict";//! moment.js locale configuration
return t.defineLocale("ka",{months:"იანვარი_თებერვალი_მარტი_აპრილი_მაისი_ივნისი_ივლისი_აგვისტო_სექტემბერი_ოქტომბერი_ნოემბერი_დეკემბერი".split("_"),monthsShort:"იან_თებ_მარ_აპრ_მაი_ივნ_ივლ_აგვ_სექ_ოქტ_ნოე_დეკ".split("_"),weekdays:{standalone:"კვირა_ორშაბათი_სამშაბათი_ოთხშაბათი_ხუთშაბათი_პარასკევი_შაბათი".split("_"),format:"კვირას_ორშაბათს_სამშაბათს_ოთხშაბათს_ხუთშაბათს_პარასკევს_შაბათს".split("_"),isFormat:/(წინა|შემდეგ)/},weekdaysShort:"კვი_ორშ_სამ_ოთხ_ხუთ_პარ_შაბ".split("_"),weekdaysMin:"კვ_ორ_სა_ოთ_ხუ_პა_შა".split("_"),longDateFormat:{LT:"HH:mm",LTS:"HH:mm:ss",L:"DD/MM/YYYY",LL:"D MMMM YYYY",LLL:"D MMMM YYYY HH:mm",LLLL:"dddd, D MMMM YYYY HH:mm"},calendar:{sameDay:"[დღეს] LT[-ზე]",nextDay:"[ხვალ] LT[-ზე]",lastDay:"[გუშინ] LT[-ზე]",nextWeek:"[შემდეგ] dddd LT[-ზე]",lastWeek:"[წინა] dddd LT-ზე",sameElse:"L"},relativeTime:{future:function(t){return t.replace(/(წამ|წუთ|საათ|წელ|დღ|თვ)(ი|ე)/,(function(t,e,_){return"ი"===_?e+"ში":e+_+"ში"}))},past:function(t){return/(წამი|წუთი|საათი|დღე|თვე)/.test(t)?t.replace(/(ი|ე)$/,"ის წინ"):/წელი/.test(t)?t.replace(/წელი$/,"წლის წინ"):t},s:"რამდენიმე წამი",ss:"%d წამი",m:"წუთი",mm:"%d წუთი",h:"საათი",hh:"%d საათი",d:"დღე",dd:"%d დღე",M:"თვე",MM:"%d თვე",y:"წელი",yy:"%d წელი"},dayOfMonthOrdinalParse:/0|1-ლი|მე-\d{1,2}|\d{1,2}-ე/,ordinal:function(t){return 0===t?t:1===t?t+"-ლი":t<20||t<=100&&t%20==0||t%100==0?"მე-"+t:t+"-ე"},week:{dow:1,doy:7}})},"object"===d(e)?s(_(12354)):(o=[_(12354)],void 0===(r="function"==typeof(n=s)?n.apply(e,o):n)||(t.exports=r))}}]);
//# sourceMappingURL=chunk.2265.js.map