/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

import BaseJiveComponentView from './BaseJiveComponentView';
import _ from 'underscore';
import headerToolbarViewFactory from '../factory/headerToolbarViewFactory';
import JiveOverlayView from './overlay/JiveOverlayView';
import $ from 'jquery';

import 'jquery-ui/ui/position';

export default BaseJiveComponentView.extend({

    init: function(){
        this.reportContainer = this.getReportContainer(this.getReportId());

        this.initJiveComponents(this.reportContainer);
        this.resetSelected();

        this.initJiveEvents(this.model);
    },

    initJiveEvents: function(crosstab){
        this.headerToolbar && this.listenTo(this.headerToolbar, "select", this._onHeaderToolbarSelect);

        this.overlay && this.listenTo(this.overlay, "overlayClicked", this._overlayClicked);

        this.crosstabElement = this.getReportContainer(this.getReportId());

        var fragmentId = crosstab.getFragmentId();

        this.crosstabElement.on('click touchend', 'td.jrxtcolheader[data-jrxtid=\'' +
            fragmentId + '\']', _.bind(this._onClick, this, crosstab));
        this.crosstabElement.on('click touchend', 'td.jrxtdatacell[data-jrxtid=\'' +
            fragmentId + '\']', _.bind(this._onClick, this, crosstab));
        this.crosstabElement.on('click touchend', 'td.jrxtrowheader[data-jrxtid=\'' +
            fragmentId + '\']', _.bind(this._onClick, this, crosstab));

        if (this.model.get("hasFloatingHeaders") && this.stateModel.isFloatingCrosstabHeaderEnabled()) {
            this._initFloatingHeaders();

            this.listenTo(this.model, "change:scaleFactor", function() {
                // force rescaling the floating header
                if (this.$scrollContainer.find("table.jr_floating_cross_header").is(":visible") ||
                    this.$scrollContainer.find("table.jr_floating_row_header").is(":visible") ||
                    this.$scrollContainer.find("table.jr_floating_column_header").is(":visible")) {
                    this._scrollHeaders(null, true);
                }

                this.floatingColumnHeader = null;
                this.floatingRowHeader = null;
                this.floatingCrossHeader = null;

                this.firstCol = null;
                this.lastCell = null;
            });
        }

        // hide visual selection elements when scaling
        this.listenTo(this.model, "change:scaleFactor", function() {
            this.hide();
            this.resetSelected();
            this.isSelectionActive = false;
        });
    },

    initJiveComponents: function(reportContainer){
        //TODO i18n
        this.overlay = new JiveOverlayView({
            parentElement: reportContainer
        });

        this.headerToolbar = headerToolbarViewFactory(this.model.get("type"), {
            parentElement: reportContainer
        });

        this.headerToolbar.$el.addClass("jr_xtab");
    },

    _onClick: function(crosstab, e){
        if(!$(e.target).parent().is('._jrHyperLink')) {
            var axis = $(e.currentTarget).hasClass("jrxtrowheader") ? "RowGroup" : "DataColumn";
            this["select" + axis](crosstab, $(e.currentTarget));
            return false;
        }
    },

    _onHeaderToolbarSelect: function(buttonView, buttonModel, e){
        var order = buttonModel.get("order");
        buttonView.$el.hasClass("disabled") ? this.hide() : this.sort(order);
    },

    selectDataColumn: function(crosstab, cell) {
        var columnIdx = cell.data('jrxtcolidx');
        var fragmentId = cell.data('jrxtid');
        var parentTable = cell.parents("table:first");
        var firstHeader = $('td.jrxtcolheader[data-jrxtid=\'' + fragmentId + '\'][data-jrxtcolidx=\'' + columnIdx + '\']:first', parentTable);
        var lastCell = $('td.jrxtdatacell[data-jrxtid=\'' + fragmentId + '\'][data-jrxtcolidx=\'' + columnIdx + '\']:last', parentTable);

        var selected = {crosstab: crosstab, header: firstHeader, cell:cell, isColumn: true};
        this.resetSelected(selected);

        var overlaySize = this.getOverlaySize(lastCell, firstHeader, this.model.get("scaleFactor"));

        this.overlay.css({
            width: overlaySize.width,
            height: overlaySize.height
        }).show().setPosition({
            of: firstHeader,
            my: 'left top',
            at:'left top',
            collision:'none'
        });

        var firstHeaderColumnIdx = firstHeader.data('jrxtcolidx');
        var sortingEnabled = crosstab.isDataColumnSortable(firstHeaderColumnIdx);

        this.headerToolbar.show(sortingEnabled);
        this._setToolbarAndOverlayPosition();

        this.isSelectionActive = true;
    },
    selectRowGroup: function(crosstab, cell) {
        var columnIdx = cell.data('jrxtcolidx');
        var fragmentId = cell.data('jrxtid');
        var headers = $('td.jrxtrowheader[data-jrxtid=\'' + fragmentId + '\'][data-jrxtcolidx=\'' + columnIdx + '\']', cell.parents("table:first"));
        var firstHeader = $(headers[0]);
        var lastHeader = $(headers[headers.length - 1]);

        var selected = {crosstab: crosstab, header: firstHeader, cell: cell, isColumn: false};
        this.resetSelected(selected);

        var overlaySize = this.getOverlaySize(lastHeader, firstHeader, this.model.get("scaleFactor"));

        this.overlay.css({
            width: overlaySize.width,
            height: overlaySize.height
        }).show();
        this.headerToolbar.show(true);
        this._setToolbarAndOverlayPosition();

        this.isSelectionActive = true;
    },

    getOverlaySize: function(lastEl, firstEl, scaleFactor){
        var lastElOffset = lastEl.offset(),
            firstElOffset = firstEl.offset(),
            scaleFactor = scaleFactor || 1;

        return {
            width: (lastElOffset.left + lastEl.outerWidth() - firstElOffset.left) * scaleFactor,
            height: lastElOffset.top + (lastEl.outerHeight() * scaleFactor)  - firstElOffset.top
        }
    },

    sort: function(order){
        this.hide();

        var selected = this.selected;
        var crosstab = selected.crosstab;

        if (this.selected.header.hasClass('jrxtcolheader')) {
            var columnIdx = selected.header.data('jrxtcolidx');
            var sortOrder = order;
            if (sortOrder == crosstab.getColumnOrder(columnIdx)) {
                sortOrder = 'NONE';
            }
            crosstab.sortByDataColumn(columnIdx, sortOrder);

        } else if (selected.header.hasClass('jrxtrowheader')) {
            var rowGroupIdx = selected.header.data('jrxtcolidx');
            var sortOrder = order;
            if (sortOrder == crosstab.config.rowGroups[rowGroupIdx].order) {
                sortOrder = 'NONE';
            }
            crosstab.sortRowGroup(rowGroupIdx, sortOrder);
        }
    },

    hide: function(){
        this.overlay && this.overlay.hide();
        this.headerToolbar && this.headerToolbar.hide();
        this.$el.hide();
    },

    show: function(){
        this.overlay && this.overlay.show();
        this.headerToolbar && this.headerToolbar.$el.show();
        this.$el.show();
    },

    render: function($el){
        var dfd = new $.Deferred();

        this.setDataReportId($el, this.report.id);

        if (!this.stateModel.isDefaultJiveUiEnabled()) {
            dfd.resolve();
            return dfd;
        }

        this.init();

        this.overlay && this.overlay.render();
        this.headerToolbar && this.headerToolbar.render();

        dfd.resolve();

        this.canFloat = true;

        return dfd;
    },

    resetSelected: function(selected){
        this.selected = selected ? selected : null;
    },

    _getModulesToLoad: function() {
        var modules = BaseJiveComponentView.prototype._getModulesToLoad.call(this);

        //TODO: find out why it's not in a themes folder
        modules.push("csslink!jr.jive.crosstab.templates.styles.css");

        return modules;
    },

    detachEvents: function(){
        this.crosstabElement && this.crosstabElement.off();
    },

    remove: function() {
        this.overlay && this.overlay.remove();
        this.headerToolbar && this.headerToolbar.remove();

        if (this.$scrollContainer) {
            this.floatingColumnHeader && this.floatingColumnHeader.remove();
            this.floatingRowHeader && this.floatingRowHeader.remove();
            this.floatingCrossHeader && this.floatingCrossHeader.remove();

            this.floatingColumnHeader = null;
            this.floatingRowHeader = null;
            this.floatingCrossHeader = null;

            this.firstCol = null;
            this.lastCell = null;

            this.$scrollContainer.off("scroll");
        }

        BaseJiveComponentView.prototype.remove.call(this, arguments);
    },

    _overlayClicked: function() {
        this.hide();
        this.resetSelected();
        this.isSelectionActive = false;
    },

    _initFloatingHeaders: function() {
        this.$scrollContainer = $(this.stateModel.get("container"));

        // scrollContainer must have position!=static in order for the floating headers to behave properly
        if (this.$scrollContainer.css("position") === "static") {
            this.$scrollContainer.css("position", "relative");
        }

        this.canFloat = false;

        // relax the scroll event triggering a bit by throttling it
        this.$scrollContainer.on("scroll", _.throttle(_.bind(this._scrollHeaders, this), 100, { leading: true, trailing: true }));

        this.scrollData = {
            bColMoved: false,
            bRowMoved: false
        };

        this.on("columnHeader:scroll", this._onColumnHeaderScroll);
        this.on("columnHeader:scrollStop", this._onColumnHeaderScrollStop);

        this.on("rowHeader:scroll", this._onRowHeaderScroll);
        this.on("rowHeader:scrollStop", this._onRowHeaderScrollStop);
    },

    _scrollHeaders: function(evt, forceScroll) {
        if (!this.canFloat || !this._isCrosstabVisible()) {
            return;
        }

        var scrollContainer = this.$scrollContainer,
            scrolledTop = false,
            scrolledLeft = false,
            scrollData = this.scrollData;

        // Determine scroll direction
        if (scrollData.scrollTop != null) { // check previous scrollTop
            if (scrollContainer.scrollTop() != scrollData.scrollTop) {
                scrollData.scrollTop = scrollContainer.scrollTop();
                scrolledTop = true;
            }
        } else if (scrollContainer.scrollTop() !== 0) {
            scrollData.scrollTop = scrollContainer.scrollTop();
            scrolledTop = true;
        }

        if (scrollData.scrollLeft != null) { // check previous scrollLeft
            if (scrollContainer.scrollLeft() != scrollData.scrollLeft) {
                scrollData.scrollLeft = scrollContainer.scrollLeft();
                scrolledLeft = true;
            }
        } else if (scrollContainer.scrollLeft() !== 0) {
            scrollData.scrollLeft = scrollContainer.scrollLeft();
            scrolledLeft = true;
        }

        if (!scrolledLeft && !scrolledTop && !forceScroll) {
            return;
        }

        this._scrollColumnHeader();
        this._scrollRowHeader();
        this._scrollCrossSection();
    },

    /**
     * Checks if the crosstab is in the visible area of the report container
     * @returns {boolean}
     * @private
     */
    _isCrosstabVisible: function() {
        var scrollContainer = this.$scrollContainer,
            containerTop = scrollContainer.offset().top,
            crosstabId = this.model.getFragmentId(),
            firstCol, lastCell, isNotVisible;

        if (!this.firstCol) {
            this.firstCol = scrollContainer.find("td.jrxtcolfloating[data-jrxtid='" + crosstabId + "']").first();
        }
        firstCol = this.firstCol;

        if (!this.lastCell) {
            this.lastCell = firstCol.closest("table").find("td.jrxtdatacell[data-jrxtid='" + crosstabId + "']").last();
        }
        lastCell = this.lastCell;

        isNotVisible = lastCell.offset().top < containerTop || firstCol.offset().top > (containerTop + scrollContainer.outerHeight());
        return !isNotVisible;
    },

    _scrollColumnHeader: function() {
        var scrollContainer = this.$scrollContainer,
            scrollData = this.scrollData,
            crosstabId = this.model.getFragmentId(),
            firstHeader = scrollContainer.find("td.jrxtcolfloating[data-jrxtid='" + crosstabId + "']").first();

        if (!firstHeader.length) {
            return;
        }

        if (!this.floatingColumnHeader) {
            this.floatingColumnHeader = this._getColumnHeaderFloatingTable(firstHeader, crosstabId);
        }

        var floatingTbl = this.floatingColumnHeader,
            containerTop = scrollContainer.offset().top,
            headerTop = firstHeader.closest('tr').offset().top,
            firstHeaderCel = scrollContainer.find("td.jrxtcolfloating[data-jrxtid='" + crosstabId + "']").first(),
            firstRowHeaderCel = scrollContainer.find("td.jrxtrowheader[data-jrxtid='" + crosstabId + "']").first(),
            lastTableCel = firstHeaderCel.closest("table").find("td.jrxtdatacell[data-jrxtid='" + crosstabId + "']").last(),
            diff = lastTableCel.length ? lastTableCel.offset().top - floatingTbl.outerHeight() - containerTop: -1, // if last cell is not visible, hide the floating header
            scaleFactor = this.model.get("scaleFactor"),
            floatCondition = headerTop - containerTop < 0 && diff > 0;

        if ((!scrollData.bColMoved && floatCondition) || (scrollData.bColMoved && floatCondition)) {
            floatingTbl.show();

            if (scaleFactor) {
                this._applyScaleTransform(floatingTbl, scaleFactor);
            }

            floatingTbl.offset({
                top: containerTop,
                left: firstRowHeaderCel.offset().left
            });

            scrollData.bColMoved = true;
            this.trigger("columnHeader:scroll");
        } else if (scrollData.bColMoved) {
            floatingTbl.hide();
            scrollData.bColMoved = false;
            this.trigger("columnHeader:scrollStop");
        }
    },

    _scrollRowHeader: function() {
        var scrollContainer = this.$scrollContainer,
            scrollData = this.scrollData,
            crosstabId = this.model.getFragmentId();

        var firstHeader = scrollContainer.find("td.jrxtrowfloating[data-jrxtid='" + crosstabId + "']").first();
        if (!firstHeader.length) {
            return;
        }

        if (!this.floatingRowHeader) {
            this.floatingRowHeader = this._getFloatingTable(firstHeader, crosstabId, "jr_floating_row_header", "jrxtrowfloating");
        }

        var floatingTbl = this.floatingRowHeader,
            containerLeft = scrollContainer.offset().left,
            headerLeft = firstHeader.offset().left,
            lastTableCel = scrollContainer.find("table.jrPage td.jrxtdatacell[data-jrxtid='" + crosstabId + "']").last(),
            diff = lastTableCel.length ? lastTableCel.offset().left - floatingTbl.width() - containerLeft: -1, // if last cell is not visible, hide the floating header
            scaleFactor = this.model.get("scaleFactor"),
            floatCondition = headerLeft - containerLeft < 0 && diff > 0;

        if (((!scrollData.bRowMoved && floatCondition) || (scrollData.bRowMoved && floatCondition)) &&
            0.8 * scrollContainer.outerWidth() > floatingTbl.outerWidth() * scaleFactor) {

            floatingTbl.show();

            if (scaleFactor) {
                this._applyScaleTransform(floatingTbl, scaleFactor);
            }

            floatingTbl.offset({
                top: firstHeader.offset().top,
                left: containerLeft
            });

            scrollData.bRowMoved = true;
            this.trigger("rowHeader:scroll");
        } else if (scrollData.bRowMoved) {
            floatingTbl.hide();
            scrollData.bRowMoved = false;
            this.trigger("rowHeader:scrollStop");
        }
    },

    _scrollCrossSection: function() {
        var scrollContainer = this.$scrollContainer,
            scrollData = this.scrollData,
            crosstabId = this.model.getFragmentId();

        if (!scrollContainer.find("table.jr_floating_cross_header[data-jrxtid='" + crosstabId + "']").length) {
            this._markCrossHeaderElements(crosstabId);
        }

        var firstHeader = scrollContainer.find("td.jrxtcrossheader[data-jrxtid='" + crosstabId + "']").first();
        if (!firstHeader.length) {
            return;
        }

        if (!this.floatingCrossHeader) {
            this.floatingCrossHeader = this._getFloatingTable(firstHeader, crosstabId, "jr_floating_cross_header", "jrxtcrossheader");
        }

        var floatingCrossTbl = this.floatingCrossHeader,
            floatingColumnTbl = this.floatingColumnHeader || this._getFloatingTable(firstHeader, crosstabId, "jr_floating_column_header"),
            floatingRowTbl = this.floatingRowHeader || this._getFloatingTable(firstHeader, crosstabId, "jr_floating_row_header"),
            scaleFactor = this.model.get("scaleFactor");

        if ((scrollData.bColMoved || scrollData.bRowMoved) &&
            0.8 * scrollContainer.outerWidth() > floatingRowTbl.outerWidth() * scaleFactor) {

            floatingCrossTbl.show();

            if (scaleFactor) {
                this._applyScaleTransform(floatingCrossTbl, scaleFactor);
            }

            floatingCrossTbl.offset({
                top: scrollData.bColMoved ? floatingColumnTbl.offset().top : firstHeader.offset().top,
                left: scrollData.bRowMoved ? floatingRowTbl.offset().left : firstHeader.offset().left
            });

            scrollData.bCrossMoved = true;

        } else if (scrollData.bCrossMoved) {
            floatingCrossTbl.hide();
            scrollData.bCrossMoved = false;
        }
    },

    _getFloatingTable: function(firstHeader, crosstabId, tableClass, elementClass, altElementClass) {
        var tbl = this.$scrollContainer.find("table." + tableClass + "[data-jrxtid='" + crosstabId + "']");

        if (firstHeader && tbl.length === 0) {
            tbl = $("<table class='" + tableClass + "' data-jrxtid='" + crosstabId + "' style='display:none'></table>").appendTo(this.$scrollContainer);

            if (elementClass == "jrxtrowfloating") {
                tbl.on("click", ".jrxtrowfloating", function(evt){
                    // keep links functional
                    if(!$(evt.target).parent().is("._jrHyperLink")) {
                        var jo = $(this),
                            crosstabId = jo.attr("data-jrxtid"),
                            colIdx = jo.attr("data-jrxtcolidx"),
                            altJo = tbl.parent()
                                .find("table.jrPage td.jrxtrowfloating[data-jrxtid='" + crosstabId + "']")
                                .filter("td[data-jrxtcolidx='" + colIdx + "']").eq(0);

                        altJo.length && altJo.trigger("click");
                        return false;
                    }
                });
            } else if (elementClass == "jrxtcrossheader") {
                tbl.on("click", ".jrxtcrossheader", function(evt){
                    // keep links functional
                    if(!$(evt.target).parent().is("._jrHyperLink")) {
                        var jo = $(this),
                            crosstabId = jo.attr("data-jrxtid"),
                            colIdx = jo.attr("data-jrxtcolidx"),
                            altJo;

                        altJo = tbl.parent()
                            .find("table.jrPage td.jrxtrowfloating[data-jrxtid='" + crosstabId + "']")
                            .filter("td[data-jrxtcolidx='" + colIdx + "']").eq(0);

                        altJo.length && altJo.trigger("click");
                        return false;
                    }
                });
            }

            var parentTable = firstHeader.closest("table"),
                lastHeader = parentTable.find("td." + elementClass + "[data-jrxtid='" + crosstabId + "']").last(),
                rows = [], clone, cloneWidth = [],
                row, $row, lastRow, cloneTD, rowTD, rowTDs, i, j, k,
                tblJrPage, parentTableRows,
                startIndex = 0, colSpanLength = 0, finishedCalculatedStartIndex = false;

            if (firstHeader.length > 0) {
                row = firstHeader.closest("tr");
                lastRow = lastHeader.closest("tr");
                tblJrPage = firstHeader.closest("table");
                parentTableRows = parentTable.find("tr");

                if (row.get(0) === lastRow.get(0)) {    // need to compare the actual dom nodes
                    rows.push(row);
                } else {
                    i = parentTableRows.index(row);
                    j = parentTableRows.index(lastRow);

                    for (k = i; k <= j; k++) {
                        rows.push(parentTableRows.get(k));
                    }
                }

                /*
                 * Need to compensate the cross section with rows due to rowspan variations
                 */
                if (elementClass === "jrxtcrossheader") {
                    lastRow = $(rows[rows.length - 1]);

                    var lastRowIndex = parentTableRows.index(lastRow),
                        allRowSpans = $.map(lastRow.find("td.jrxtcrossheader"), function(td) {
                            return $(td).prop("rowspan");
                        }),
                        maxSpan = Math.max.apply(Math, allRowSpans);

                    k = 1;
                    if (maxSpan > 1) {
                        for (k; k < maxSpan; k++) {
                            rows.push(parentTableRows.get(lastRowIndex + k));
                        }
                    }
                }

                $.each(rows, function(idx, row) {
                    $row = $(row);
                    rowTDs = $row.find("td");
                    clone = $("<tr></tr>");
                    cloneWidth[idx] = 0;
                    clone.attr("style", $row.attr("style"));
                    clone.css("height", $row.css("height"));
                    clone.attr("valign", $row.attr("valign"));

                    // add only the tds with elementClass class
                    for (i = 0; i < rowTDs.length; i++) {
                        rowTD = $(rowTDs.get(i));
                        !finishedCalculatedStartIndex && (startIndex += rowTD.prop("colspan"));
                        if (rowTD.data("jrxtid") === crosstabId && (rowTD.is("." + elementClass) || (altElementClass && rowTD.is("." + altElementClass)))) {
                            if (elementClass !== "jrxtrowfloating") {
                                if (idx === 0 && !finishedCalculatedStartIndex) {
                                    startIndex -= rowTD.prop("colspan");
                                    finishedCalculatedStartIndex = true;
                                }
                                colSpanLength += rowTD.prop("colspan");
                            }

                            // put empty TDs for the rowheader to prevent rowspans from interfering
                            if (elementClass == "jrxtrowfloating" && rowTD.is(".jrxtinteractive")) {
                                cloneTD = $("<td class='jrxtrowfloating jrxtinteractive'></td>");
                                cloneTD.attr("data-jrxtid", rowTD.attr("data-jrxtid"));
                                cloneTD.attr("data-jrxtcolidx", rowTD.attr("data-jrxtcolidx"));
                            } else {
                                cloneTD = rowTD.clone();

                                // Fix for bug #41786 - set width/height with css method to take box-sizing into account
                                cloneTD.css("width", rowTD.css("width"));
                                cloneTD.css("height", rowTD.css("height"));

                                cloneWidth[idx] = cloneWidth[idx] + rowTD.outerWidth();
                            }
                            clone.append(cloneTD);
                        }
                    }

                    /* First row of table.jrPage contains all the columns(with colspan = 1) with their respective
                     * size, so we must copy all the columns from it, across which the floating column header
                     * expands so that future columns with colspan will expand properly
                     */
                    if (idx === 0 && startIndex !== undefined && elementClass !== "jrxtrowfloating") {
                        var firstRow = tblJrPage.find("tr").first(),
                            firstRowTDs = firstRow.find("td"),
                            firstRowClone = $("<tr></tr>"),
                            j = startIndex;

                        for (j; j < startIndex + colSpanLength; j ++) {
                            firstRowClone.append($(firstRowTDs.get(j)).clone());
                        }

                        tbl.append(firstRowClone);
                    }

                    tbl.append(clone);
                });

                tbl.css({
                    position: "absolute",
                    width: Math.max.apply(Math, cloneWidth),
                    "empty-cells": tblJrPage.css("empty-cells"),
                    "border-collapse": tblJrPage.css("border-collapse"),
                    "background-color": tblJrPage.css("background-color")
                });
                tbl.attr("cellpadding", tblJrPage.attr("cellpadding"));
                tbl.attr("cellspacing", tblJrPage.attr("cellspacing"));
                tbl.attr("border", tblJrPage.attr("border"));
            }
        }

        return tbl;
    },

    _getColumnHeaderFloatingTable: function(firstHeader, crosstabId) {
        var tableClass = "jr_floating_column_header",
            elementClass = "jrxtcolfloating",
            tbl = this.$scrollContainer.find("table." + tableClass + "[data-jrxtid='" + crosstabId + "']");

        if (firstHeader && tbl.length === 0) {
            tbl = $("<table class='" + tableClass + "' data-jrxtid='" + crosstabId + "' style='display:none'></table>").appendTo(this.$scrollContainer);

            tbl.on("click", ".jrxtcolfloating", function(evt){
                // keep links functional
                if(!$(evt.target).parent().is("._jrHyperLink")) {
                    var jo = $(this),
                        crosstabId = jo.attr("data-jrxtid"),
                        colIdx = jo.attr("data-jrxtcolidx"),
                        crosstabFloatingHeader = tbl.parent()
                            .find("table.jrPage td.jrxtcolheader[data-jrxtid='" + crosstabId + "']")
                            .filter("td[data-jrxtcolidx='" + colIdx + "']").eq(0);

                    crosstabFloatingHeader.length && crosstabFloatingHeader.trigger("click");
                    return false;
                }
            });

            var parentTable = firstHeader.closest("table"),
                lastHeader = parentTable.find("td." + elementClass + "[data-jrxtid='" + crosstabId + "']").last(),
                lastInteractiveRowHeader = parentTable.find("td.jrxtrowheader.jrxtinteractive[data-jrxtid='" + crosstabId + "']").last(),
                rows = [], clone, cloneWidth = [],
                row, $row, lastRow, lastInteractiveRowHeaderRow, cloneTD, rowTD, rowTDs, i, j, k,
                tblJrPage, parentTableRows,
                startIndex = 0, colSpanLength = 0, finishedCalculatedStartIndex = false;

            if (firstHeader.length > 0) {
                row = firstHeader.closest("tr");
                lastRow = lastHeader.closest("tr");
                lastInteractiveRowHeaderRow = lastInteractiveRowHeader.closest("tr");
                tblJrPage = parentTable;
                parentTableRows = parentTable.find("tr");

                if (lastInteractiveRowHeaderRow.length && parentTableRows.index(lastInteractiveRowHeaderRow) > parentTableRows.index(lastRow)) {
                    lastRow = lastInteractiveRowHeaderRow;
                }

                if (row.get(0) === lastRow.get(0)) {    // need to compare the actual dom nodes
                    rows.push(row);
                } else {
                    i = parentTableRows.index(row);
                    j = parentTableRows.index(lastRow);

                    for (k = i; k <= j; k++) {
                        rows.push(parentTableRows.get(k));
                    }
                }

                $.each(rows, function(idx, row) {
                    $row = $(row);
                    rowTDs = $row.find("td");
                    clone = $("<tr></tr>");
                    cloneWidth[idx] = 0;

                    clone.attr("style", $row.attr("style"));
                    clone.css("height", $row.css("height"));
                    clone.attr("valign", $row.attr("valign"));

                    for (i = 0; i < rowTDs.length; i++) {
                        rowTD = $(rowTDs.get(i));

                        !finishedCalculatedStartIndex && (startIndex += rowTD.prop("colspan"));

                        if (rowTD.data("jrxtid") === crosstabId) {
                            if (idx === 0 && !finishedCalculatedStartIndex) {
                                startIndex -= rowTD.prop("colspan");
                                finishedCalculatedStartIndex = true;
                            }

                            colSpanLength += rowTD.prop("colspan");
                            cloneTD = rowTD.clone();

                            // Fix for bug #41786 - set width/height with css method to take box-sizing into account
                            cloneTD.css("width", rowTD.css("width"));
                            cloneTD.css("height", rowTD.css("height"));

                            cloneWidth[idx] = cloneWidth[idx] + rowTD.outerWidth();

                            clone.append(cloneTD);
                        }
                    }

                    /* First row of table.jrPage contains all the columns(with colspan = 1) with their respective
                     * size, so we must copy all the columns from it, across which the floating column header
                     * expands so that future columns with colspan will expand properly
                     */
                    if (idx === 0 && startIndex !== undefined) {
                        var firstRow = tblJrPage.find("tr").first(),
                            firstRowTDs = firstRow.find("td"),
                            firstRowClone = $("<tr></tr>"),
                            j = startIndex;

                        for (j; j < startIndex + colSpanLength; j ++) {
                            firstRowClone.append($(firstRowTDs.get(j)).clone());
                        }

                        tbl.append(firstRowClone);
                    }

                    tbl.append(clone);
                });

                tbl.css({
                    position: "absolute",
                    width: Math.max.apply(Math, cloneWidth),
                    "empty-cells": tblJrPage.css("empty-cells"),
                    "border-collapse": tblJrPage.css("border-collapse"),
                    "background-color": tblJrPage.css("background-color")
                });
                tbl.attr("cellpadding", tblJrPage.attr("cellpadding"));
                tbl.attr("cellspacing", tblJrPage.attr("cellspacing"));
                tbl.attr("border", tblJrPage.attr("border"));
            }
        }

        return tbl;
    },

    _markCrossHeaderElements: function(crosstabId) {
        var scrollContainer = this.$scrollContainer;
        // Prepare the crosssection in case it doesn't exist
        if (!scrollContainer.find("td.jrxtcrossheader[data-jrxtid='" + crosstabId + "']").length) {
            var firstColHeader = scrollContainer.find("td.jrxtcolfloating[data-jrxtid='" + crosstabId + "']").first(),
                firstRow, lastRow, parentTable, parentTableRows, i,
                firstRowIndex, lastRowIndex, rows = [],
                maxSpan = 1, row, currentRowMaxSpan, currentRowSpans,
                colSpanLength, colSpanStop, bFoundColHeader;

            if (firstColHeader.length) {
                parentTable = firstColHeader.closest("table");
                firstRow = firstColHeader.closest("tr");
                parentTableRows = parentTable.find("tr");

                lastRow = parentTable.find("td.jrxtrowheader.jrxtinteractive[data-jrxtid='" + crosstabId + "']").last().closest("tr");

                if (!lastRow.length) {
                    lastRow = parentTable.find("td.jrxtcolfloating[data-jrxtid='" + crosstabId + "']").last().closest("tr");
                }

                firstRowIndex = parentTableRows.index(firstRow);
                lastRowIndex = parentTableRows.index(lastRow);

                for (i = firstRowIndex; i <= lastRowIndex; i++) {
                    rows.push(parentTableRows.get(i));

                    row = $(parentTableRows.get(i));
                    currentRowSpans = $.map(row.find("td"), function(td) {
                        return $(td).prop("rowspan");
                    });
                    currentRowMaxSpan = Math.max.apply(Math, currentRowSpans);

                    if (currentRowMaxSpan > maxSpan) {
                        maxSpan = currentRowMaxSpan;
                    }

                    maxSpan--;
                }

                i = 1;
                if (maxSpan > 1) {
                    for (i; i < maxSpan; i++) {
                        rows.push(parentTableRows.get(lastRowIndex + i));
                    }
                }

                $.each(rows, function(idx, row) {
                    colSpanLength = 0;

                    $(row).find("td").each(function(tdIdx, td) {
                        var $td = $(td);

                        colSpanLength += $td.prop("colspan");

                        if ($td.data("jrxtid") === crosstabId) {
                            if (idx === 0 && !bFoundColHeader && $td.is(".jrxtcolfloating")) {
                                bFoundColHeader = true;
                                colSpanStop = colSpanLength - $td.prop("colspan");
                            }

                            if (!bFoundColHeader || (colSpanLength <= colSpanStop && !$td.is(".jrxtcolfloating"))) {
                                $td.addClass("jrxtcrossheader");
                            }
                        }
                    });
                });
            }
        }
    },

    _onColumnHeaderScroll: function() {
        if (this.headerToolbar.$el.is(":visible")) {
            var floatingTable, floatingHeader, my;

            // data column is selected
            if (this.selected.isColumn) {
                floatingTable = this.floatingColumnHeader;
                floatingHeader = floatingTable.find("td.jrxtcolheader[data-jrxtcolidx='" + this.selected.header.data("jrxtcolidx") + "']").first();
            }
            // row group column is selected
            else {
                floatingTable = this.floatingCrossHeader;

                // the row header is floating and no cross-header table present
                if (this.scrollData.bRowMoved && !floatingTable.length) {
                    floatingTable = this.floatingRowHeader;
                }

                floatingHeader = floatingTable.find("td.jrxtrowheader[data-jrxtcolidx='" + this.selected.header.data("jrxtcolidx") + "']").first();
            }

            if (floatingHeader && floatingHeader.length) {
                if (floatingHeader.offset().top > floatingTable.offset().top) {
                    var diff = floatingHeader.offset().top - floatingTable.offset().top - this.headerToolbar.$el.outerHeight();
                    my = "left bottom";
                    diff < 0 && (my += "+" + Math.abs(diff));
                } else {
                    my = "left top";
                }

                this.headerToolbar.setPosition({
                    my: my,
                    at: "left top",
                    of: floatingHeader,
                    collision: "none"
                });
            }
            /* this happens when the row group is selected & the row header is not floating
             * & there is no floating cross-section(usually for non-adhoc generated cross-tabs)
            */
            else {
                var containerTop = this.$scrollContainer.offset().top,
                    diff = this.selected.header.offset().top - this.headerToolbar.$el.outerHeight() - containerTop,
                    newTop;

                if (diff <= 0) {
                    newTop = containerTop;
                } else {
                    newTop = this.selected.header.offset().top - this.headerToolbar.$el.outerHeight() + 1;
                }

                this.headerToolbar.$el.offset({ top: newTop, left: this.headerToolbar.$el.offset().left });
            }

        }
    },

    _onColumnHeaderScrollStop: function() {
        if (this.headerToolbar.$el.is(":visible")) {
            this._setToolbarAndOverlayPosition();
        }
    },

    _onRowHeaderScroll: function() {
        if (!this.isSelectionActive) {
            return;
        }

        var floatingRowTable = this.floatingRowHeader,
            scaleFactor = this.model.get("scaleFactor"),
            floatingRowTableRight = floatingRowTable.offset().left + floatingRowTable.width() * scaleFactor;

        // toolbar is visible and selected header goes under the floating row header
        if (this.headerToolbar.$el.is(":visible") && this.selected.header.offset().left < floatingRowTableRight) {
            this.hide();
        }
        // toolbar is not visible(because of actions above) and selected header is no longer under the floating row header
        else if (!this.headerToolbar.$el.is(":visible") && this.selected.header.offset().left >= floatingRowTableRight) {
            this.show();
        }
    },

    _onRowHeaderScrollStop: function() {
        if (!this.isSelectionActive) {
            return;
        }

        if (!this.headerToolbar.$el.is(":visible")) {
            this.show();
            this._setToolbarAndOverlayPosition();
        }
    },

    _setToolbarAndOverlayPosition: function() {
        var scrollData = this.scrollData || {},
            tableHeader, floatingTable, my, diff, overlayOffset,
            crosstabId = this.model.getFragmentId(),
            bHeaderToolbarPositioned = false;

        if ((!scrollData.bColMoved && !scrollData.bRowMoved) || (scrollData.bRowMoved && !scrollData.bColMoved)) {
            tableHeader = this.selected.header;
        }

        this.overlay.setPosition({
            my: 'left top',
            at: 'left top',
            of: this.selected.header,
            collision: 'none'
        });

        if (scrollData.bColMoved) {
            // data column is selected
            if (this.selected.isColumn) {
                floatingTable = this.floatingColumnHeader || this._getFloatingTable(null, crosstabId, "jr_floating_column_header");
                tableHeader = floatingTable.find("td.jrxtcolheader[data-jrxtcolidx='" + this.selected.header.data("jrxtcolidx") + "']").first();
            }
            // row group column is selected
            else {
                floatingTable = this.floatingCrossHeader || this._getFloatingTable(null, crosstabId, "jr_floating_cross_header");

                // the row header is floating and (no cross-header table present or present but with no row headers)
                if (this.scrollData.bRowMoved && (!floatingTable.length || !floatingTable.find("td.jrxtrowheader").length)) {
                    floatingTable = this.floatingRowHeader || this._getFloatingTable(null, crosstabId, "jr_floating_row_header");
                }

                tableHeader = floatingTable.find("td.jrxtrowheader[data-jrxtcolidx='" + this.selected.header.data("jrxtcolidx") + "']").first();
            }

            if (tableHeader && tableHeader.length) {
                if (tableHeader.offset().top > floatingTable.offset().top) {
                    diff = tableHeader.offset().top - floatingTable.offset().top - this.headerToolbar.$el.outerHeight();
                    my = "left bottom";
                    diff < 0 && (my += "+" + Math.abs(diff));
                } else {
                    my = "left top";
                }

                /* the floatingRowHeader floats only horizontally, so the header toolbar position must be calculated
                 * relatively to the container's top offset
                 */
                if (floatingTable.is(".jr_floating_row_header")) {
                    if (tableHeader.offset().top < this.$scrollContainer.offset().top) {
                        my = "left top+" + (this.$scrollContainer.offset().top - tableHeader.offset().top);
                    } else {
                        my = "left bottom";
                        diff =  tableHeader.offset().top - this.$scrollContainer.offset().top - this.headerToolbar.$el.outerHeight();
                        diff < 0 && (my += "+" + Math.abs(diff));
                    }
                }

                this.headerToolbar.setPosition({
                    my: my,
                    at: "left top",
                    of: tableHeader,
                    collision: "none"
                });

                bHeaderToolbarPositioned = true;
            }
            /* this happens when the row group is selected & the row header is not floating
             * & there is no floating cross-section(usually for non-adhoc generated cross-tabs)
             */
            else {
                var containerTop = this.$scrollContainer.offset().top,
                    diff = this.selected.header.offset().top - this.headerToolbar.$el.outerHeight() - containerTop,
                    newTop;

                if (diff <= 0) {
                    newTop = containerTop;
                } else {
                    newTop = this.selected.header.offset().top - this.headerToolbar.$el.outerHeight() + 1;
                }

                this.headerToolbar.$el.offset({ top: newTop, left: this.selected.header.offset().left });

                bHeaderToolbarPositioned = true;
            }
        }

        if (scrollData.bRowMoved) {
            if (!this.selected.isColumn) {
                floatingTable = this.floatingCrossHeader || this._getFloatingTable(null, crosstabId, "jr_floating_cross_header");

                if (!floatingTable.length || !floatingTable.find("td.jrxtrowheader").length) {
                    floatingTable = this.floatingRowHeader || this._getFloatingTable(null, crosstabId, "jr_floating_row_header");
                }

                tableHeader = floatingTable.find("td.jrxtrowheader[data-jrxtcolidx='" + this.selected.header.data("jrxtcolidx") + "']").first();

                overlayOffset = this.overlay.$el.offset();
                this.overlay.$el.offset({
                    left: tableHeader.offset().left,
                    top: overlayOffset.top
                });
            }
        }

        !bHeaderToolbarPositioned && this.headerToolbar.setPosition({
            my: my ? my: "left bottom+1",
            at: "left top",
            of: tableHeader,
            collision: "none"
        });
    }
});

