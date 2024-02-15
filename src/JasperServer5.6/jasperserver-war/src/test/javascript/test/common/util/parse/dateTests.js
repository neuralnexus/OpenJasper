define(function (require) {
    "use strict";

    var dateUtils = require("common/util/parse/date"),
        jrsConfigs = require("jrs.configs");

    describe("Date Utils", function() {
        it("should have functions", function() {
            expect(dateUtils).toBeDefined();

            expect(dateUtils.isRelativeDate).toBeDefined();
            expect(typeof dateUtils.isRelativeDate).toEqual("function");

            expect(dateUtils.isRelativeTimestamp).toBeDefined();
            expect(typeof dateUtils.isRelativeTimestamp).toEqual("function");

            expect(dateUtils.isDate).toBeDefined();
            expect(typeof dateUtils.isDate).toEqual("function");

            expect(dateUtils.isTimestamp).toBeDefined();
            expect(typeof dateUtils.isTimestamp).toEqual("function");

            expect(dateUtils.isIso8601Timestamp).toBeDefined();
            expect(typeof dateUtils.isIso8601Timestamp).toEqual("function");

            expect(dateUtils.compareDates).toBeDefined();
            expect(typeof dateUtils.compareDates).toEqual("function");

            expect(dateUtils.compareTimestamps).toBeDefined();
            expect(typeof dateUtils.compareTimestamps).toEqual("function");

            expect(dateUtils.dateObjectToIso8601Timestamp).toBeDefined();
            expect(typeof dateUtils.dateObjectToIso8601Timestamp).toEqual("function");

            expect(dateUtils.iso8601TimestampToDateObject).toBeDefined();
            expect(typeof dateUtils.iso8601TimestampToDateObject).toEqual("function");

            expect(dateUtils.localizedTimestampToIsoTimestamp).toBeDefined();
            expect(typeof dateUtils.localizedTimestampToIsoTimestamp).toEqual("function");

            expect(dateUtils.isoTimestampToLocalizedTimestamp).toBeDefined();
            expect(typeof dateUtils.isoTimestampToLocalizedTimestamp).toEqual("function");

            expect(dateUtils.localizedDateToIsoDate).toBeDefined();
            expect(typeof dateUtils.localizedDateToIsoDate).toEqual("function");

            expect(dateUtils.isoDateToLocalizedDate).toBeDefined();
            expect(typeof dateUtils.isoDateToLocalizedDate).toEqual("function");

            expect(dateUtils.localizedTimeToIsoTime).toBeDefined();
            expect(typeof dateUtils.localizedTimeToIsoTime).toEqual("function");

            expect(dateUtils.isoTimeToLocalizedTime).toBeDefined();
            expect(typeof dateUtils.isoTimeToLocalizedTime).toEqual("function");
        });

        describe("isRelativeDate", function() {
            it("should return true in case of correct relative date", function() {
                expect(dateUtils.isRelativeDate("DAY")).toEqual(true);
                expect(dateUtils.isRelativeDate("DAY+1")).toEqual(true);
                expect(dateUtils.isRelativeDate("DAY-1")).toEqual(true);
                expect(dateUtils.isRelativeDate("DAY+0")).toEqual(true);

                expect(dateUtils.isRelativeDate("WEEK")).toEqual(true);
                expect(dateUtils.isRelativeDate("WEEK+1")).toEqual(true);
                expect(dateUtils.isRelativeDate("WEEK-1")).toEqual(true);
                expect(dateUtils.isRelativeDate("WEEK+0")).toEqual(true);

                expect(dateUtils.isRelativeDate("MONTH")).toEqual(true);
                expect(dateUtils.isRelativeDate("MONTH+1")).toEqual(true);
                expect(dateUtils.isRelativeDate("MONTH-1")).toEqual(true);
                expect(dateUtils.isRelativeDate("MONTH+0")).toEqual(true);

                expect(dateUtils.isRelativeDate("QUARTER")).toEqual(true);
                expect(dateUtils.isRelativeDate("QUARTER+1")).toEqual(true);
                expect(dateUtils.isRelativeDate("QUARTER-1")).toEqual(true);
                expect(dateUtils.isRelativeDate("QUARTER+0")).toEqual(true);

                expect(dateUtils.isRelativeDate("SEMI")).toEqual(true);
                expect(dateUtils.isRelativeDate("SEMI+1")).toEqual(true);
                expect(dateUtils.isRelativeDate("SEMI-1")).toEqual(true);
                expect(dateUtils.isRelativeDate("SEMI+0")).toEqual(true);

                expect(dateUtils.isRelativeDate("YEAR")).toEqual(true);
                expect(dateUtils.isRelativeDate("YEAR+1")).toEqual(true);
                expect(dateUtils.isRelativeDate("YEAR-1")).toEqual(true);
                expect(dateUtils.isRelativeDate("YEAR+0")).toEqual(true);
            });

            it("should return false in case of non-string input value", function() {
                expect(dateUtils.isRelativeDate({})).toEqual(false);
            });

            it("should return false in case of incorrect relative date", function() {
                expect(dateUtils.isRelativeDate("DA Y")).toEqual(false);
                expect(dateUtils.isRelativeDate("DAY+a")).toEqual(false);
                expect(dateUtils.isRelativeDate("DAY +1")).toEqual(false);
                expect(dateUtils.isRelativeDate("DAY- 2")).toEqual(false);

                expect(dateUtils.isRelativeDate("WEE K")).toEqual(false);
                expect(dateUtils.isRelativeDate("WErK+1")).toEqual(false);
                expect(dateUtils.isRelativeDate("WEEK --1")).toEqual(false);
                expect(dateUtils.isRelativeDate("WEEK + 1")).toEqual(false);

                expect(dateUtils.isRelativeDate("MOTH")).toEqual(false);
                expect(dateUtils.isRelativeDate("MONTH +1")).toEqual(false);
                expect(dateUtils.isRelativeDate("MONTH -+ 1")).toEqual(false);
                expect(dateUtils.isRelativeDate("MONTH ++1")).toEqual(false);

                expect(dateUtils.isRelativeDate("QUAR TER")).toEqual(false);
                expect(dateUtils.isRelativeDate("QUARTER +1")).toEqual(false);
                expect(dateUtils.isRelativeDate("QUARTER ---1")).toEqual(false);
                expect(dateUtils.isRelativeDate("QUARTER ++sd")).toEqual(false);

                expect(dateUtils.isRelativeDate("SEM I")).toEqual(false);
                expect(dateUtils.isRelativeDate("SEMI+a")).toEqual(false);
                expect(dateUtils.isRelativeDate("SEMI -bb")).toEqual(false);
                expect(dateUtils.isRelativeDate("SEMI +")).toEqual(false);

                expect(dateUtils.isRelativeDate("YE AR")).toEqual(false);
                expect(dateUtils.isRelativeDate("YEAR+a")).toEqual(false);
                expect(dateUtils.isRelativeDate("YEARx-1")).toEqual(false);
                expect(dateUtils.isRelativeDate("YEAR -+ 1")).toEqual(false);
            });
        });

        describe("isRelativeTimestamp", function() {
            it("should return true in case of correct relative date", function() {
                expect(dateUtils.isRelativeTimestamp("DAY")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("DAY+1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("DAY-1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("DAY+0")).toEqual(true);

                expect(dateUtils.isRelativeTimestamp("WEEK")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("WEEK+1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("WEEK-1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("WEEK+0")).toEqual(true);

                expect(dateUtils.isRelativeTimestamp("MONTH")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("MONTH+1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("MONTH-1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("MONTH+0")).toEqual(true);

                expect(dateUtils.isRelativeTimestamp("QUARTER")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("QUARTER+1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("QUARTER-1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("QUARTER+0")).toEqual(true);

                expect(dateUtils.isRelativeTimestamp("SEMI")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("SEMI+1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("SEMI-1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("SEMI+0")).toEqual(true);

                expect(dateUtils.isRelativeTimestamp("YEAR")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("YEAR+1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("YEAR-1")).toEqual(true);
                expect(dateUtils.isRelativeTimestamp("YEAR+0")).toEqual(true);
            });

            it("should return false in case of non-string input value", function() {
                expect(dateUtils.isRelativeTimestamp({})).toEqual(false);
            });

            it("should return false in case of incorrect relative date", function() {
                expect(dateUtils.isRelativeTimestamp("DA Y")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("DAY+a")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("DAY +1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("DAY- 2")).toEqual(false);

                expect(dateUtils.isRelativeTimestamp("WEE K")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("WErK+1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("WEEK --1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("WEEK + 1")).toEqual(false);

                expect(dateUtils.isRelativeTimestamp("MOTH")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("MONTH +1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("MONTH -+ 1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("MONTH ++1")).toEqual(false);

                expect(dateUtils.isRelativeTimestamp("QUAR TER")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("QUARTER +1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("QUARTER ---1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("QUARTER ++sd")).toEqual(false);

                expect(dateUtils.isRelativeTimestamp("SEM I")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("SEMI+a")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("SEMI -bb")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("SEMI +")).toEqual(false);

                expect(dateUtils.isRelativeTimestamp("YE AR")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("YEAR+a")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("YEARx-1")).toEqual(false);
                expect(dateUtils.isRelativeTimestamp("YEAR -+ 1")).toEqual(false);
            });
        });

        describe("isDate", function() {
            it("should return true for valid Date object", function() {
                expect(dateUtils.isDate(new Date())).toEqual(true);
            });

            it("should return false for invalid Date object", function() {
                expect(dateUtils.isDate(new Date("aaa"))).toEqual(false);
            });

            it("should return true in case of valid date string", function() {
                expect(dateUtils.isDate("2013-06-05")).toEqual(true);
                expect(dateUtils.isDate("1999-12-07")).toEqual(true);
            });

            it("should return false in case of invalid date string", function() {
                expect(dateUtils.isDate("2013-28-05")).toEqual(false);
                expect(dateUtils.isDate("1999-12-42")).toEqual(false);
            });

            it("should return false in case of invalidly formatted date string", function() {
                expect(dateUtils.isDate("2013/28/05")).toEqual(false);
                expect(dateUtils.isDate("1999_12_42")).toEqual(false);
            });

            it("should return false in case of timestamp", function() {
                expect(dateUtils.isDate("2013-06-05 11:30:24")).toEqual(false);
            });
        });

        describe("isTimestamp", function() {
            it("should return true for valid Date object", function() {
                expect(dateUtils.isTimestamp(new Date())).toEqual(true);
            });

            it("should return false for invalid Date object", function() {
                expect(dateUtils.isTimestamp(new Date("aaa"))).toEqual(false);
            });

            it("should return true in case of valid timestamp string", function() {
                expect(dateUtils.isTimestamp("2013-06-05 11:30:24")).toEqual(true);
                expect(dateUtils.isTimestamp("1999-12-07 23:30:24")).toEqual(true);
            });

            it("should return false in case of invalid timestamp string", function() {
                expect(dateUtils.isTimestamp("2013-06-05 11:69:24")).toEqual(false);
                expect(dateUtils.isTimestamp("1999-12-07 24:30:24")).toEqual(false);
            });

            it("should return false in case of invalidly formatted timestamp string", function() {
                expect(dateUtils.isTimestamp("2013/28/05   11-69-24")).toEqual(false);
                expect(dateUtils.isTimestamp("1999_12_42/12:30:40")).toEqual(false);
            });

            it("should return false in case of date", function() {
                expect(dateUtils.isTimestamp("2013-06-05")).toEqual(false);
            });
        });

        describe("isIso8601Timestamp", function() {
            it("should return true for valid ISO 8601 UTC timestamp", function() {
                expect(dateUtils.isIso8601Timestamp("2013-06-05T11:30:24")).toEqual(true);
            });

            it("should return false for invalid ISO 8601 UTC timestamp", function() {
                expect(dateUtils.isIso8601Timestamp("2013-06-05Z11:30:24")).toEqual(false);
                expect(dateUtils.isIso8601Timestamp("2013-06-05")).toEqual(false);
            });
        });

        describe("compareDates", function() {
            it("should return undefined if one of the dates is corrupted", function() {
                expect(dateUtils.compareDates("2013-06-05", new Date("aaaa"))).toEqual(undefined);
            });

            it("should return 0 if dates are equal", function() {
                expect(dateUtils.compareDates("2013-06-05", "2013-06-05")).toEqual(0);
                expect(dateUtils.compareDates(new Date(2013,8,5), new Date(2013,8,5))).toEqual(0);
            });

            it("should return -1 if first date is before second", function() {
                expect(dateUtils.compareDates("2013-06-05", "2013-07-05")).toEqual(-1);
                expect(dateUtils.compareDates(new Date(2013,6,5), new Date(2013,7,5))).toEqual(-1);
            });

            it("should return 1 if first date is after second", function() {
                expect(dateUtils.compareDates("2013-08-05", "2013-07-05")).toEqual(1);
                expect(dateUtils.compareDates(new Date(2013,8,5), new Date(2013,7,5))).toEqual(1);
            });
        });

        describe("compareTimestamps", function() {
            it("should return undefined if one of the timestamps is corrupted", function() {
                expect(dateUtils.compareTimestamps("2013-06-05 12:30:40", new Date("aaaa"))).toEqual(undefined);
            });

            it("should return 0 if timestamps are equal", function() {
                expect(dateUtils.compareTimestamps("2013-06-05 12:30:40", "2013-06-05 12:30:40")).toEqual(0);
                expect(dateUtils.compareTimestamps(new Date(2013,8,5,12,30,40), new Date(2013,8,5,12,30,40))).toEqual(0);
            });

            it("should return -1 if first timestamp is before second", function() {
                expect(dateUtils.compareTimestamps("2013-06-05 12:30:40", "2013-06-05 12:30:41")).toEqual(-1);
                expect(dateUtils.compareTimestamps(new Date(2013,6,5,12,30,40), new Date(2013,6,5,12,30,41))).toEqual(-1);
            });

            it("should return 1 if first timestamp is after second", function() {
                expect(dateUtils.compareTimestamps("2013-07-05 12:31:40", "2013-07-05 12:30:40")).toEqual(1);
                expect(dateUtils.compareTimestamps(new Date(2013,6,5,12,30,42), new Date(2013,6,5,12,30,41))).toEqual(1);
            });
        });

        describe("dateObjectToIso8601Timestamp", function() {
            it("should return ISO 8601 string in case of valid Date object", function() {
                expect(dateUtils.dateObjectToIso8601Timestamp(new Date(2013, 5, 5, 11, 30, 24))).toEqual("2013-06-05T11:30:24");
            });

            it("should return undefined in case of invalid Date object", function() {
                expect(dateUtils.dateObjectToIso8601Timestamp("05.06.2013XXX11:30:24")).toBeUndefined();
            });
        });

        describe("iso8601TimestampToDateObject", function() {
            it("should return Date object in case of valid ISO 8601 timestamp", function () {
                var dateObj = dateUtils.iso8601TimestampToDateObject("2013-06-05T11:30:24");

                expect(_.isDate(dateObj)).toBe(true);
                expect(dateObj.getFullYear()).toBe(2013);
                expect(dateObj.getMonth()).toBe(5);
                expect(dateObj.getDate()).toBe(5);
                expect(dateObj.getHours()).toBe(11);
                expect(dateObj.getMinutes()).toBe(30);
                expect(dateObj.getSeconds()).toBe(24);
            });

            it("should return undefined in case of invalid ISO 8601 timestamp", function () {
                expect(dateUtils.iso8601TimestampToDateObject("2013-06-05T11-30-24")).toBeUndefined();
            });
        });

        describe("ISO<->localized format conversion", function(){
            var originalDateFormat = jrsConfigs.localeSettings.dateFormat,
                originalTimeFormat = jrsConfigs.localeSettings.timeFormat,
                originalTimestampSeparator = jrsConfigs.localeSettings.timestampSeparator;
            beforeEach(function(){
                // change format to some unusual
                jrsConfigs.localeSettings.dateFormat = "dd.MM.yyyy";
                jrsConfigs.localeSettings.timeFormat = "HH-mm-ss";
                jrsConfigs.localeSettings.timestampSeparator = "~";
            });
            afterEach(function(){
                // restore original formats
                jrsConfigs.localeSettings.dateFormat = originalDateFormat;
                jrsConfigs.localeSettings.timeFormat = originalTimeFormat;
                jrsConfigs.localeSettings.timestampSeparator = originalTimestampSeparator;
            });
            it("ISO date -> localized date", function(){
                expect(dateUtils.isoDateToLocalizedDate("2014-07-09")).toEqual("09.07.2014");
            });
            it("Invalid ISO date -> localized date returns original invalid", function(){
                expect(dateUtils.isoDateToLocalizedDate("Some Invalid Date")).toEqual("Some Invalid Date");
            });
            it("ISO time -> localized time", function(){
                expect(dateUtils.isoTimeToLocalizedTime("11:01:55")).toEqual("11-01-55");
            });
            it("Invalid ISO time -> localized time returns original invalid", function(){
                expect(dateUtils.isoTimeToLocalizedTime("Some Invalid Time")).toEqual("Some Invalid Time");
            });
            it("ISO timestamp -> localized timestamp", function(){
                expect(dateUtils.isoTimestampToLocalizedTimestamp("2014-07-09T11:01:55")).toEqual("09.07.2014~11-01-55");
            });
            it("Invalid ISO timestamp -> localized timestamp returns original invalid", function(){
                expect(dateUtils.isoTimestampToLocalizedTimestamp("Some Invalid Timestamp")).toEqual("Some Invalid Timestamp");
            });
            it("Localized date -> ISO date", function(){
                expect(dateUtils.localizedDateToIsoDate("09.07.2014")).toEqual("2014-07-09");
            });
            it("Invalid Localized date -> ISO date returns original invalid", function(){
                expect(dateUtils.localizedDateToIsoDate("Some Invalid Date")).toEqual("Some Invalid Date");
            });
            it("Localized time -> ISO time", function(){
                expect(dateUtils.localizedTimeToIsoTime("11-01-55")).toEqual("11:01:55");
            });
            it("Invalid Localized time -> ISO time returns original invalid", function(){
                expect(dateUtils.localizedTimeToIsoTime("Some Invalid Time")).toEqual("Some Invalid Time");
            });
            it("Localized timestamp -> ISO timestamp", function(){
                expect(dateUtils.localizedTimestampToIsoTimestamp("09.07.2014~11-01-55")).toEqual("2014-07-09T11:01:55");
            });
            it("Invalid Localized timestamp -> ISO timestamp returns original invalid", function(){
                expect(dateUtils.localizedTimestampToIsoTimestamp("Some Invalid Timestamp")).toEqual("Some Invalid Timestamp");
            });
        });
    });
});
