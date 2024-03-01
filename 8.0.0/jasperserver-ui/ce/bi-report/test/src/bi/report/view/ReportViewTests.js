/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
import sinon from 'sinon';
import ReportView from 'src/bi/report/view/ReportView';
import scaleStrategies from 'src/bi/report/enum/scaleStrategies';
import reportEvents from 'src/bi/report/enum/reportEvents';
import domUtil from 'js-sdk/src/common/util/domUtil';
import $ from 'jquery';
import Backbone from 'backbone';
import renderHighchartsReportMock from 'test/src/bi/report/view/mock/renderHighchartsReport.mock.htm';

describe("ReportView tests", function() {
    var view,
        sandbox;

    beforeEach(function() {
        sandbox = sinon.createSandbox({
            useFakeTimers: true
        });

        view = new ReportView({
            stateModel: new Backbone.Model({ chart: {} }),
            collection: new Backbone.Collection()
        });
        view.collection.trigger("reset");
    });

    afterEach(function() {
        view && view.remove();
        sandbox.restore();
    });

    it("should be able to calculate scale factor", function() {
        expect(view.calculateScaleFactor()).toBe(1);

        view.stateModel.set("scale", 1.5);

        expect(view.calculateScaleFactor()).toBe(1.5);

        view.stateModel.set("scale", "245%");

        expect(view.calculateScaleFactor()).toBe(2.45);

        view.stateModel.set("scale", "asc%");

        expect(view.calculateScaleFactor()).toBeUndefined();

        view.$jrTables = {
            width: sandbox.stub().returns(400),
            height: sandbox.stub().returns(300)
        };

        view.$reportContainer = {
            width: sandbox.stub().returns(700),
            height: sandbox.stub().returns(450)
        };

        var isScrollableStub = sandbox.stub(domUtil, "isScrollable").returns(true),
            getScrollbarWidthStub = sandbox.stub(domUtil, "getScrollbarWidth").returns(20);

        view.stateModel.set("scale", scaleStrategies.CONTAINER);

        expect(view.calculateScaleFactor()).toBe(1.5);

        view.stateModel.set("scale", scaleStrategies.WIDTH);

        expect(view.calculateScaleFactor()).toBe(1.6975);

        view.stateModel.set("scale", scaleStrategies.HEIGHT);

        expect(view.calculateScaleFactor()).toBe(1.5);

        isScrollableStub.restore();
        getScrollbarWidthStub.restore();
    });

    it("should be able to scroll to anchor while rendering report", function() {
        view.model = {
            getExport: sandbox.stub().returns({
                getHTMLOutput: sandbox.stub().returns("<div class='_jr_report_container_'><table><tr><td>TEST</td></tr><tr><td><a name='summary'></a></td></tr></table></div>")
            })
        };

        view.$reportContainer = $("<div></div>").append(view.$el);

        view.stateModel.set("pages", { pages: 2, anchor: "summary" });

        var isElasticChartStub = sandbox.stub(view, "isElasticChart").returns(false),
            triggerStub = sandbox.stub(view, "trigger"),
            applyScaleStub = sandbox.stub(view, "applyScale").returns(1.5),
            scrollTopStub = sandbox.stub(),
            scrollParent = { scrollTop: scrollTopStub },
            scrollParentDomNode = {},
            getElementOffsetStub = sandbox.stub(domUtil, "getElementOffset"),
            scrollParentStub = sandbox.stub(view.$el, "scrollParent").returns(scrollParent);

        scrollParent[0] = scrollParentDomNode;

        getElementOffsetStub.onCall(0).returns({ top: 100, left: 40 });
        getElementOffsetStub.onCall(1).returns({ top: 20, left: 40 });

        view.renderReport();

        expect(view.$el.find("table").length).toBe(1);
        expect(isElasticChartStub).toHaveBeenCalled();
        expect(triggerStub).toHaveBeenCalledWith(reportEvents.BEFORE_RENDER, view.$el[0]);
        expect(applyScaleStub).toHaveBeenCalled();
        expect(view.$jrTables.length).toBe(1);
        expect(getElementOffsetStub.getCall(0).args[0]).toBe(view.$el.find("[name=summary]")[0]);
        expect(getElementOffsetStub.getCall(1).args[0]).toBe(scrollParentDomNode);
        expect(scrollParentStub).toHaveBeenCalled();
        expect(scrollTopStub).toHaveBeenCalledWith(120);

        isElasticChartStub.restore();
        triggerStub.restore();
        applyScaleStub.restore();
        getElementOffsetStub.restore();
        scrollParentStub.restore();
    });

    it("should extract report title and pass it when rendering jive component collection", function(done) {
        view.model = {
            getExport: sandbox.stub().returns({
                getHTMLOutput: sandbox.stub().returns(renderHighchartsReportMock)
            })
        };

        view.$reportContainer = $("<div></div>").append(view.$el);

        view.stateModel.set("pages", { pages: 2, anchor: "summary" });

        var scrollTopStub = sandbox.stub(),
            scrollParent = { scrollTop: scrollTopStub },
            scrollParentDomNode = {},
            getElementOffsetStub = sandbox.stub(domUtil, "getElementOffset");

        var dfd = new $.Deferred();

        sandbox.stub(view, "isElasticChart").returns(true);
        sandbox.stub(view, "trigger");
        sandbox.stub(view, "applyScale");
        sandbox.stub(view, "calculateScaleFactor").returns(1.5);
        sandbox.stub(view.$el, "scrollParent").returns(scrollParent);
        sandbox.stub(view, "showOverlay");
        sandbox.stub(view, "hideOverlay");
        sandbox.stub(view.jiveComponentCollectionView, "render").returns(dfd);

        sandbox.spy(view.$el, "html");

        scrollParent[0] = scrollParentDomNode;

        getElementOffsetStub.onCall(0).returns({ top: 100, left: 40 });
        getElementOffsetStub.onCall(1).returns({ top: 20, left: 40 });

        view.renderReport();

        view.renderJive().then(function() {
            expect(view.highchartsReportTitle).toEqual("Title");

            expect(view.$el.find(".highcharts_parent_container").length).toEqual(1);
            expect(view.$el.find(".content").length).toEqual(1);

            expect(view.jiveComponentCollectionView.render).toHaveBeenCalledWith(view.$el, {
                highchartsReportTitle: "Title"
            });
        }).always(function() {
            expect(view.showOverlay).toHaveBeenCalled();
            expect(view.hideOverlay).toHaveBeenCalled();

            done();
        });

        sandbox.clock.tick(1100);

        dfd.resolve();

        sandbox.clock.tick(100);
    });

    it("should scroll element to top 0 in case if anchor is not specified", function() {
        view.model = {
            getExport: sandbox.stub().returns({
                getHTMLOutput: sandbox.stub().returns("<div class='_jr_report_container_'><table><tr><td>TEST</td></tr><tr><td><a name='summary'></a></td></tr></table></div>")
            })
        };

        view.$reportContainer = $("<div></div>").append(view.$el);
        var scrollTopStub = sandbox.stub(),
            scrollParentStub = sandbox.stub(view.$el, "scrollParent").returns({ scrollTop: scrollTopStub });

        view.renderReport();

        expect(scrollParentStub).toHaveBeenCalled();
        expect(scrollTopStub).toHaveBeenCalledWith(0);

        scrollParentStub.restore();
    });

    it("should set height of localFrameView based on jrTables when isolateDom is set to true", function() {
        view.$jrTables = $("<div class='div.visualizejs._jr_report_container_.jr.jiveDisabled'></div>");
        view.$jrTables.height(400);
        view.$jrTables.length = 1;
        view.localFrameView = $("<iframe></iframe>", { scrolling: "no" }).css({ border: "none", width: "100%"});
        view.localFrameView.$el = $("<iframe></iframe>", { scrolling: "no" }).css({ border: "none", width: "100%"});

        view.model = {
            getExport: sandbox.stub().returns({
                getHTMLOutput: sandbox.stub().returns("<div class='_jr_report_container_'><table><tr><td>TEST</td></tr><tr><td><a name='summary'></a></td></tr></table></div>")
            })
        };

        view.$reportContainer = $("<div></div>").append(view.$el);
        view.stateModel.set("isolateDom", true);

        view._applyScaleTransform(1);

        sandbox.clock.tick(50);

        expect(view.localFrameView.$el.height()).toBe(400);
    });

    it("should call or not call applyScale function depending on autoresize property", function() {
        view && view.remove();

        var applyScaleStub = sandbox.stub(ReportView.prototype, "applyScale");

        view = new ReportView({
            stateModel: new Backbone.Model({ autoresize: true }),
            collection: new Backbone.Collection()
        });

        window.dispatchEvent(new Event("resize"));

        var callCountBefore = applyScaleStub.callCount;

        expect(callCountBefore).toBeGreaterThan(0);

        view.stateModel.set("autoresize", false);

        window.dispatchEvent(new Event("resize"));

        expect(applyScaleStub.callCount).toBe(callCountBefore);
    });

    it("should be able to apply scaling to Ad Hoc Chart report", function() {
        var $div = $("<div style='width: 200px; height: 100px;'><table></table></div>"),
            sizableViews = [{
                setSize: sandbox.stub()
            }],
            calculateScaleFactorStub = sandbox.stub(view, "calculateScaleFactor").returns(1),
            applyScaleTransformStub = sandbox.stub(view, "_applyScaleTransform"),
            isElasticChartStub = sandbox.stub(view, "isElasticChart").returns(true),
            autoScaleFontsEnabledStub = sandbox.stub(view, "_autoScaleFontsEnabled").returns(true),
            setAutoScaleFontsStub = sandbox.stub(view, "_setAutoScaleFonts"),
            sizableSubviewsStub = sandbox.stub(view.jiveComponentCollectionView, "getSizableSubviews").returns((new $.Deferred()).resolve(sizableViews)),
            cssSpy = sandbox.spy($.fn, "css");

        view.$reportContainer = $("<div></div>").append(view.$el);

        view.$jrTables = $div.find("table");

        view.applyScale();

        sandbox.clock.tick(50);

        expect(calculateScaleFactorStub).toHaveBeenCalled();
        expect(applyScaleTransformStub).toHaveBeenCalledWith(1);
        expect(isElasticChartStub).toHaveBeenCalled();
        expect(autoScaleFontsEnabledStub).toHaveBeenCalled();
        expect(setAutoScaleFontsStub).toHaveBeenCalled();
        expect(sizableSubviewsStub).toHaveBeenCalled();
        expect(sizableViews[0].setSize).toHaveBeenCalledWith(200, 100);
        expect(cssSpy.getCall(0).args).toEqual(["overflow", "hidden"]);
        expect(cssSpy.getCall(1).args).toEqual(["overflow", "visible"]);

        calculateScaleFactorStub.restore();
        applyScaleTransformStub.restore();
        isElasticChartStub.restore();
        autoScaleFontsEnabledStub.restore();
        setAutoScaleFontsStub.restore();
        sizableSubviewsStub.restore();
        cssSpy.restore();
    });
});
