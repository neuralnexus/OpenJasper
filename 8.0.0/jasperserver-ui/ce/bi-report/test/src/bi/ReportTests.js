/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
import sinon from 'sinon';
import logger from "js-sdk/src/common/logging/logger";
import Report from 'src/bi/report/Report';
import ReportController from 'src/bi/report/ReportController';
import ReportView from 'src/bi/report/view/ReportView';
import $ from 'jquery';

describe("Report BiComponent tests", function() {
    var clock;

    beforeEach(function() {
        clock = sinon.useFakeTimers();
    });

    afterEach(function() {
        clock.restore();
        // need to stop listening forwindow resize event
        // because ReportView does sign up for listening
        // while there are no clean way for tests for destroy transitive ReportView
        // (report.destroy() does not working for some reason);
        $(window).off("resize");
    });

    it("should have have default value for 'pages' property", function() {
        var report = new Report();

        expect(report.pages()).toBe(1);
    });

    it("setter 'pages' should return current report instance", function() {
        var report = new Report();

        expect(report.pages(2)).toBe(report);
    });

    it("should be possible to initialize instance with 'pages' property", function() {
        var report = new Report({ pages: 2 });

        expect(report.pages()).toBe(2);

        report = new Report({ pages: "3-5" });
        expect(report.pages()).toBe("3-5");

        report = new Report({
            pages: {
                pages: 4,
                anchor: "summary"
            }
        });
        expect(report.pages()).toEqual({
            pages: 4,
            anchor: "summary"
        });

        report = new Report({
            pages: {
                anchor: "summary"
            }
        });
        expect(report.pages()).toEqual({
            anchor: "summary"
        });

        report = new Report({
            pages: {
                pages: "4-5"
            }
        });
        expect(report.pages()).toEqual({
            pages: "4-5"
        });
    });

    it("should be possible to set 'pages' property to instance", function() {
        var report = new Report();
        report.pages(2);

        expect(report.pages()).toBe(2);

        report.pages("3-5");
        expect(report.pages()).toBe("3-5");

        report.pages({
            pages: 4,
            anchor: "summary"
        });
        expect(report.pages()).toEqual({
            pages: 4,
            anchor: "summary"
        });

        report.pages({
            anchor: "summary"
        });
        expect(report.pages()).toEqual({
            anchor: "summary"
        });

        report.pages({
            pages: "4-5"
        });
        expect(report.pages()).toEqual({
            pages: "4-5"
        });
    });

    it("should be possible to set 'pages' property to instance via 'properties' method", function() {
        var report = new Report();

        report.properties({ pages: 2 });
        expect(report.pages()).toBe(2);
        expect(report.properties()).toEqual({ pages: 2, autoresize: true, centerReport: false, useReportZoom:false, modalDialogs: true, chart: {}, loadingOverlay: true });

        report.properties({ pages: "3-5" });
        expect(report.pages()).toBe("3-5");
        expect(report.properties()).toEqual({ pages: "3-5", autoresize: true, centerReport: false, useReportZoom:false, modalDialogs: true, chart: {}, loadingOverlay: true });

        report.properties({
            pages: {
                pages: 4,
                anchor: "summary"
            }
        });
        expect(report.pages()).toEqual({
            pages: 4,
            anchor: "summary"
        });
        expect(report.properties()).toEqual({
            pages: {
                pages: 4,
                anchor: "summary"
            },
            autoresize: true,
            centerReport: false,
            useReportZoom:false,
            modalDialogs: true,
            chart: {},
            loadingOverlay: true
        });

        report.properties({
            pages: {
                anchor: "summary"
            }
        });
        expect(report.pages()).toEqual({
            anchor: "summary"
        });
        expect(report.properties()).toEqual({
            pages: {
                anchor: "summary"
            },
            autoresize: true,
            centerReport: false,
            useReportZoom:false,
            modalDialogs: true,
            chart: {},
            loadingOverlay: true
        });

        report.properties({
            pages: {
                pages: "4-5"
            }
        });
        expect(report.pages()).toEqual({
            pages: "4-5"
        });
        expect(report.properties()).toEqual({
            pages: {
                pages: "4-5"
            },
            autoresize: true,
            centerReport: false,
            useReportZoom:false,
            modalDialogs: true,
            chart: {},
            loadingOverlay: true
        });
    });

    it("should restore original value for 'pages' object with anchor if 'run' method failed", function() {
        var executeReportStub = sinon.stub(ReportController.prototype, "executeReport").callsFake(function() {
                this.model.set("requestId", "test");
                return (new $.Deferred()).resolve();
            }),
            fetchPageHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchPageHtmlExportAndJiveComponents")
                .returns((new $.Deferred()).reject({})),
            report = new Report({
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/test",
                pages: {
                    pages: "3-5",
                    anchor: "summary"
                }
            });

        report.run();

        clock.tick(1);

        expect(report.pages()).toEqual({
            pages: "3-5",
            anchor: "summary"
        });

        report.pages({ anchor: "test" }).run();

        clock.tick(1);

        expect(report.pages()).toEqual({
            pages: "3-5",
            anchor: "summary"
        });

        executeReportStub.restore();
        fetchPageHtmlExportAndJiveComponentsStub.restore();
    });

    it("should restore original value for 'pages' property without anchor if 'run' method failed", function() {
        var executeReportStub = sinon.stub(ReportController.prototype, "executeReport").callsFake(function() {
                this.model.set("requestId", "test");
                return (new $.Deferred()).resolve();
            }),
            fetchPageHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchPageHtmlExportAndJiveComponents")
                .returns((new $.Deferred()).reject({})),
            report = new Report({
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/test",
                pages: {
                    pages: "3-5"
                }
            });

        report.run();

        clock.tick(1);

        expect(report.pages()).toEqual({
            pages: "3-5"
        });

        report.pages({ anchor: "test" }).run();

        clock.tick(1);

        expect(report.pages()).toEqual("3-5");

        executeReportStub.restore();
        fetchPageHtmlExportAndJiveComponentsStub.restore();
    });

    it("should rerun report after search action if results were found on the same page as before the search", function(done) {
        clock.restore();

        var searchReportDeferred = new $.Deferred();

        var executeReportStub = sinon.stub(ReportController.prototype, "executeReport").callsFake(function() {
                this.model.set("requestId", "test");
                return (new $.Deferred()).resolve();
            }),
            searchReportActionStub = sinon.stub(ReportController.prototype, "searchReportAction").callsFake(function() {
                return searchReportDeferred;
            }),
            fetchPageHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchPageHtmlExportAndJiveComponents")
                .returns((new $.Deferred()).resolve({})),
            report = new Report({
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/test",
                pages: 1
            });

        report.pages(1).run().then(function() {
            report.search({
                text: "text"
            }).then(function(data) {
                report.pages(data[0].page).run().then(function() {
                    expect(searchReportActionStub).toHaveBeenCalledWith({text: "text"});

                    executeReportStub.restore();
                    fetchPageHtmlExportAndJiveComponentsStub.restore();
                    searchReportActionStub.restore();

                    done();
                });
            });

            searchReportDeferred.resolve({
                actionResult: {
                    searchResults: [
                        {
                            "page": 1,
                            "hitCount": 1
                        },
                        {
                            "page": 37,
                            "hitCount": 1
                        }
                    ]
                }
            });
        });
    });

    it("should not rerun report if the page didn't change", function(done) {
        clock.restore();

        var executeReportStub = sinon.stub(ReportController.prototype, "executeReport").callsFake(function() {
                this.model.set("requestId", "test");
                return (new $.Deferred()).resolve();
            }),
            fetchPageHtmlExportAndJiveComponentsStub = sinon.stub(ReportController.prototype, "fetchPageHtmlExportAndJiveComponents")
                .returns((new $.Deferred()).resolve({})),
            report = new Report({
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/test",
                pages: 1
            });

        report.pages(2).run().then(function() {
            report.pages(2).run().then(function() {
                expect(fetchPageHtmlExportAndJiveComponentsStub).not.toHaveBeenCalled();

                executeReportStub.restore();
                fetchPageHtmlExportAndJiveComponentsStub.restore();

                done();
            });
        });
    });

    it("should be able to set 'autoresize' property", function() {
        var report = new Report();

        expect(report.autoresize()).toBe(true);
        expect(report.autoresize(false)).toBe(report);
        expect(report.autoresize()).toBe(false);
    });

    it("should be able to resize report", function() {
        var container = $("<div class='reportContainer'></div>");

        $("body").append(container);

        var applyScaleStub = sinon.stub(ReportView.prototype, "applyScale"),
            renderReportStub = sinon.stub(ReportController.prototype, "renderReport").returns((new $.Deferred()).resolve()),
            report = new Report({ container: ".reportContainer" });

        report.render();
        report.resize();

        expect(applyScaleStub).toHaveBeenCalled();

        applyScaleStub.restore();
        renderReportStub.restore();

        container.remove();
    });

    it("should return not.yet.rendered.error when calling resize before rendering report", function() {
        var container = $("<div class='reportContainer'></div>");

        $("body").append(container);

        var report = new Report({ container: ".reportContainer" }),
            failStub = sinon.stub();

        report.resize().fail(failStub);

        expect(failStub.getCall(0).args[0].errorCode).toEqual('not.yet.rendered.error');

        container.remove();
    });

    it("should have 'chart' property", function() {
        var report = new Report();

        expect(report.chart()).toEqual({});
        expect(report.chart({ animation: false, zoom: "x" })).toBe(report);
        expect(report.chart()).toEqual({ animation: false, zoom: "x" });
    });

    it("should log renderReport failure to debug level", function() {
        let localLogger = logger.register("Report");
        var container = $("<div class='reportContainer'></div>");
        $("body").append(container);
        var executeReportStub = sinon.stub(ReportController.prototype, "executeReport").callsFake(function() {
                this.model.set("requestId", "test");
                return (new $.Deferred()).resolve();
            }),
            renderReportStub = sinon.stub(ReportController.prototype, "renderReport")
                .returns((new $.Deferred()).reject({
                    message: "Map container not found.",
                    name: "error",
                    stack: "testStack"
                })),
            debugStub = sinon.stub(localLogger, "debug"),
            report = new Report({
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/test",
                container: ".reportContainer"
            });

        report.run();
        clock.tick(1);

        expect(executeReportStub).toHaveBeenCalled();
        expect(renderReportStub).toHaveBeenCalled();
        expect(debugStub).toHaveBeenCalled();

        executeReportStub.restore();
        renderReportStub.restore();
        debugStub.restore();
    });

    it("should log renderReport failure to error level", function() {
        let localLogger = logger.register("Report");
        var container = $("<div class='reportContainer'></div>");
        $("body").append(container);
        var executeReportStub = sinon.stub(ReportController.prototype, "executeReport").callsFake(function() {
                this.model.set("requestId", "test");
                return (new $.Deferred()).resolve();
            }),
            renderReportStub = sinon.stub(ReportController.prototype, "renderReport")
                .returns((new $.Deferred()).reject({
                    data: {
                        error: {
                            stack: "testStack"
                        }
                    }
                })),
            errorStub = sinon.stub(localLogger, "error"),
            report = new Report({
                server: "http://localhost:8080/jasperserver-pro",
                resource: "/public/test",
                container: ".reportContainer"
            });

        report.run();
        clock.tick(1);

        expect(executeReportStub).toHaveBeenCalled();
        expect(renderReportStub).toHaveBeenCalled();
        expect(errorStub).toHaveBeenCalled();

        executeReportStub.restore();
        renderReportStub.restore();
        errorStub.restore();
    });
});