/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
import sinon from 'sinon';
import CrosstabJiveComponentView from 'src/bi/report/jive/view/CrosstabJiveComponentView';
import JiveOverlayView from 'src/bi/report/jive/view/overlay/JiveOverlayView';
import HeaderToolbarView from 'src/bi/report/jive/view/overlay/HeaderToolbarView';
import $ from 'jquery';
import Backbone from 'backbone';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('CrosstabJiveComponentView Tests', function () {
    var crosstabJiveComponentView;
    var $el = $('<div class=\'reportContainer\' data-reportId=\'report_1\'>' + '<table>' + '<thead>' + '</thead>' + '<tbody>' + '<tr>' + '<td class=\'jrxtcolheader\' data-jrxtid=\'fragment_1\' data-jrxtcolidx=\'1\'>' + '</td>' + '</tr>' + '<tr>' + '<td class=\'jrxtdatacell\' data-jrxtid=\'fragment_1\' data-jrxtcolidx=\'1\'>' + '</td>' + '</tr>' + '<tr>' + '<td class=\'jrxtrowheader\' data-jrxtid=\'fragment_1\' data-jrxtcolidx=\'1\'>' + '</td>' + '</tr>' + '</tbody>' + '</table>' + '</div>');
    var crosstab = {
        id: 'crosstab_1',
        getFragmentId: function () {
            return 'fragment_1';
        },
        isDataColumnSortable: function () {
        },
        sortByDataColumn: function () {
        },
        sortRowGroup: function () {
        },
        getColumnOrder: function () {
            return 'NONE';
        },
        config: { rowGroups: { 1: { order: 'NONE' } } }
    };
    var Model = Backbone.Model.extend({
        id: 'crosstab_1',
        getFragmentId: function () {
            return 'fragment_1';
        },
        isDataColumnSortable: function () {
        },
        sortByDataColumn: function () {
        },
        sortRowGroup: function () {
        },
        getColumnOrder: function () {
            return 'NONE';
        },
        config: { rowGroups: { 1: { order: 'NONE' } } }
    });
    var model = new Model();
    model.set({ type: 'crosstab' });
    var StateModel = Backbone.Model.extend({
        isDefaultJiveUiEnabled: function () {
            return true;
        }
    });
    beforeEach(function () {
        crosstabJiveComponentView = new CrosstabJiveComponentView({
            stateModel: new StateModel(),
            report: {
                id: 'report_1',
                components: { crosstab: [crosstab] }
            },
            model: model
        });
        setTemplates($el[0].outerHTML);
    });
    afterEach(function () {
        $($el).remove();
        crosstabJiveComponentView.remove();
    });
    it('should be properly initialized', function () {
        expect(crosstabJiveComponentView.stateModel).toBeDefined();
        expect(crosstabJiveComponentView.report).toBeDefined();
        expect(crosstabJiveComponentView.model).toBeDefined();
        expect(crosstabJiveComponentView.overlay).not.toBeDefined();
        expect(crosstabJiveComponentView.headerToolbar).not.toBeDefined();
    });
    it('should render and init crosstab jive view components and events', function () {
        var jiveOverlayInitSpy = sinon.spy(JiveOverlayView.prototype, 'initialize');
        var jiveFoobarInitSpy = sinon.spy(HeaderToolbarView.prototype, 'initialize');
        var crosstabJiveComponentViewInitSpy = sinon.spy(crosstabJiveComponentView, 'init');
        var initJiveEventsStub = sinon.stub(crosstabJiveComponentView, 'initJiveEvents');
        crosstabJiveComponentView.render($el);
        expect(jiveOverlayInitSpy).toHaveBeenCalled();
        expect(jiveFoobarInitSpy).toHaveBeenCalled();
        expect(crosstabJiveComponentViewInitSpy).toHaveBeenCalled();
        expect(initJiveEventsStub).toHaveBeenCalledWith(model);
        expect(crosstabJiveComponentView.selected).toBe(null);
        initJiveEventsStub.restore();
        crosstabJiveComponentViewInitSpy.restore();
        jiveFoobarInitSpy.restore();
        jiveOverlayInitSpy.restore();
    });
    it('should call _onClick handler', function () {
        var _onClickSpy = sinon.spy(crosstabJiveComponentView, '_onClick');
        var selectDataColumnStub = sinon.stub(crosstabJiveComponentView, 'selectDataColumn');
        crosstabJiveComponentView.render($el);
        var e = $.Event('click');
        crosstabJiveComponentView.crosstabElement.find('td.jrxtcolheader[data-jrxtid=fragment_1]').trigger(e);
        expect(_onClickSpy).toHaveBeenCalledWith(model, e);
        expect(selectDataColumnStub).toHaveBeenCalledWith(model, $(e.currentTarget));
        selectDataColumnStub.restore();
        _onClickSpy.restore();
    });
    it('should select dataColumn', function () {
        var _onClickSpy = sinon.spy(crosstabJiveComponentView, '_onClick');
        var selectDataColumnSpy = sinon.spy(crosstabJiveComponentView, 'selectDataColumn');
        var resetSelectedSpy = sinon.spy(crosstabJiveComponentView, 'resetSelected');
        var getOverlaySizeStub = sinon.stub(crosstabJiveComponentView, 'getOverlaySize').returns({
            width: 200,
            height: 100
        });
        crosstabJiveComponentView.render($el);
        var overlayCssSpy = sinon.spy(crosstabJiveComponentView.overlay, 'css');
        var headerToolbarShowSpy = sinon.spy(crosstabJiveComponentView.headerToolbar, 'show');
        var setPositionOverlayStub = sinon.stub(crosstabJiveComponentView.overlay, 'setPosition');
        var setPositionFoobarStub = sinon.stub(crosstabJiveComponentView.headerToolbar, 'setPosition');
        var e = $.Event('click');
        crosstabJiveComponentView.crosstabElement.find('td.jrxtcolheader[data-jrxtid=fragment_1]').trigger(e);
        var cell = $(e.currentTarget);
        expect(_onClickSpy).toHaveBeenCalledWith(model, e);
        expect(selectDataColumnSpy).toHaveBeenCalledWith(model, cell);
        expect(overlayCssSpy).toHaveBeenCalledWith({
            width: 200,
            height: 100
        });
        expect(headerToolbarShowSpy).toHaveBeenCalled();
        expect(resetSelectedSpy.args[1][0].crosstab).toBe(model);
        setPositionFoobarStub.restore();
        setPositionOverlayStub.restore();
        headerToolbarShowSpy.restore();
        overlayCssSpy.restore();
        getOverlaySizeStub.restore();
        resetSelectedSpy.restore();
        selectDataColumnSpy.restore();
        _onClickSpy.restore();
    });
    it('should select Row group', function () {
        var _onClickSpy = sinon.spy(crosstabJiveComponentView, '_onClick');
        var selectRowGroupSpy = sinon.spy(crosstabJiveComponentView, 'selectRowGroup');
        var resetSelectedSpy = sinon.spy(crosstabJiveComponentView, 'resetSelected');
        var getOverlaySizeStub = sinon.stub(crosstabJiveComponentView, 'getOverlaySize').returns({
            width: 200,
            height: 100
        });
        crosstabJiveComponentView.render($el);
        var overlayCssSpy = sinon.spy(crosstabJiveComponentView.overlay, 'css');
        var headerToolbarShowSpy = sinon.spy(crosstabJiveComponentView.headerToolbar, 'show');
        var setPositionOverlayStub = sinon.stub(crosstabJiveComponentView.overlay, 'setPosition');
        var setPositionFoobarStub = sinon.stub(crosstabJiveComponentView.headerToolbar, 'setPosition');
        var e = $.Event('click');
        crosstabJiveComponentView.crosstabElement.find('td.jrxtrowheader[data-jrxtid=fragment_1]').trigger(e);
        var cell = $(e.currentTarget);
        expect(_onClickSpy).toHaveBeenCalledWith(model, e);
        expect(selectRowGroupSpy).toHaveBeenCalledWith(model, cell);
        expect(overlayCssSpy).toHaveBeenCalledWith({
            width: 200,
            height: 100
        });
        expect(headerToolbarShowSpy).toHaveBeenCalled();
        expect(resetSelectedSpy.args[1][0].crosstab).toBe(model);
        setPositionFoobarStub.restore();
        setPositionOverlayStub.restore();
        headerToolbarShowSpy.restore();
        overlayCssSpy.restore();
        getOverlaySizeStub.restore();
        resetSelectedSpy.restore();
        selectRowGroupSpy.restore();
        _onClickSpy.restore();
    });
    it('should sort by order ASCENDING', function () {
        var crosstabJiveComponentViewHideSpy = sinon.spy(crosstabJiveComponentView, 'hide');
        var crosstabGetColumnOrderSpy = sinon.spy(model, 'getColumnOrder');
        crosstabJiveComponentView.render($el);
        var e = $.Event('click');
        crosstabJiveComponentView.crosstabElement.find('td.jrxtcolheader[data-jrxtid=fragment_1]').trigger(e);
        var crosstabSortByDataColumnSpy = sinon.spy(crosstabJiveComponentView.selected.crosstab, 'sortByDataColumn');
        crosstabJiveComponentView.sort('ASCENDING');
        expect(crosstabJiveComponentViewHideSpy).toHaveBeenCalled();
        expect(crosstabSortByDataColumnSpy).toHaveBeenCalledWith(1, 'ASCENDING');
        expect(crosstabGetColumnOrderSpy).toHaveBeenCalledWith(1);
        crosstabGetColumnOrderSpy.restore();
        crosstabSortByDataColumnSpy.restore();
        crosstabJiveComponentViewHideSpy.restore();
    });
    it('should sort by order DESCENDING', function () {
        var crosstabJiveComponentViewHideSpy = sinon.spy(crosstabJiveComponentView, 'hide');
        crosstabJiveComponentView.render($el);
        var e = $.Event('click');
        crosstabJiveComponentView.crosstabElement.find('td.jrxtrowheader[data-jrxtid=fragment_1]').trigger(e);
        var crosstabSortRowGroupSpy = sinon.spy(crosstabJiveComponentView.selected.crosstab, 'sortRowGroup');
        crosstabJiveComponentView.sort('DESCENDING');
        expect(crosstabJiveComponentViewHideSpy).toHaveBeenCalled();
        expect(crosstabSortRowGroupSpy).toHaveBeenCalledWith(1, 'DESCENDING');
        crosstabSortRowGroupSpy.restore();
        crosstabJiveComponentViewHideSpy.restore();
    });
    it('should call sort on headerToolbar button click', function () {
        var sortStub = sinon.stub(crosstabJiveComponentView, 'sort');
        crosstabJiveComponentView.render($el);
        var e = $.Event('click');
        $('.jive_button').first().trigger(e);
        expect(sortStub).toHaveBeenCalledWith('ASCENDING');
        sortStub.restore();
    });
    it('should remove', function () {
        crosstabJiveComponentView.render($el);
        var overlayRemoveSpy = sinon.spy(crosstabJiveComponentView.overlay, 'remove');
        var headerToolbarRemoveSpy = sinon.spy(crosstabJiveComponentView.headerToolbar, 'remove');
        crosstabJiveComponentView.remove();
        expect(overlayRemoveSpy).toHaveBeenCalled();
        expect(headerToolbarRemoveSpy).toHaveBeenCalled();
        headerToolbarRemoveSpy.restore();
        overlayRemoveSpy.restore();
    });
});