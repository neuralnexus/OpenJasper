/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
import sinon from 'sinon';
import $ from 'jquery';
import TableJiveComponentView from 'src/bi/report/jive/view/TableJiveComponentView';
import jiveTypes from 'src/bi/report/jive/enum/jiveTypes';
import _ from 'underscore';
import jiveActions from 'src/bi/report/jive/enum/jiveActions';
import testReportMarkup from './mock/testReportMarkup.htm';

describe("TableJiveComponentView Tests.", function() {
    var tableJiveComponentView,
        buttonModel,
        buttonView,
        stateModel,
        reportEl,
        columns,
        sandbox,
        report,
        model;

    beforeEach(function() {
        sandbox = sinon.createSandbox();

        report = {
            id: "reportId"
        };

        reportEl = $(testReportMarkup);

        $("body").append(reportEl);

        buttonModel = {
            get: sandbox.stub()
        };

        buttonView = {
            $el: {
                offset: sandbox.stub().returns({
                    top: 5,
                    left: 5
                })
            }
        };

        columns = [
            {
                index: 0,
                label: "label0",
                uuid: "column1",
                interactive: true,
                get: sandbox.stub().withArgs("id").returns("column1")
            },
            {
                index: 1,
                label: "label1",
                uuid: "column2",
                interactive: true,
                get: sandbox.stub().withArgs("id").returns("column2")
            }
        ];

        model = {
            get: sandbox.stub(),
            config: {
                genericProperties: {},
                allColumnsData: []
            },
            columns: [],
            on: function() {},
            off: function() {}
        };

        model.get.withArgs("id").returns("tableId");
        model.get.withArgs("type").returns(jiveTypes.TABLE);

        stateModel = {
            get: sandbox.stub(),
            isFloatingTableHeaderEnabled: sandbox.stub().returns(true)
        };

        stateModel.get.withArgs("container").returns($("<div></div>"));

        tableJiveComponentView = new TableJiveComponentView({
            model: model,
            stateModel: stateModel,
            report: report
        });

        tableJiveComponentView.init();
    });

    afterEach(function() {
        sandbox.restore();
        reportEl.remove();

        tableJiveComponentView.remove();
    });

    it("should open filter dialog", function() {
        buttonModel.get.withArgs("message").returns("filter");

        sandbox.stub(tableJiveComponentView.filterDialog, "open");

        tableJiveComponentView.headerToolbar.trigger("select", buttonView, buttonModel);

        expect(tableJiveComponentView.filterDialog.open).toHaveBeenCalled();
    });

    it("should open format dialog", function() {
        buttonModel.get.withArgs("message").returns("format");

        sandbox.stub(tableJiveComponentView.formatDialog, "open");

        tableJiveComponentView.headerToolbar.trigger("select", buttonView, buttonModel);

        expect(tableJiveComponentView.formatDialog.open).toHaveBeenCalled();
    });

    describe("selectColumn", function() {

        beforeEach(function() {
            model.columns = [].concat(columns);
        });

        it("should select column on header click", function() {
            sandbox.stub(tableJiveComponentView, "selectColumn");

            var columnData = {
                $header: {
                    data: sandbox.stub().withArgs("coluuid").returns("column1")
                }
            };

            sandbox.stub(tableJiveComponentView, "getColumnData").returns(columnData);

            var header = reportEl.find("td#firstHeader.jrcolHeader");

            header.click();

            var target = tableJiveComponentView.getColumnData.args[0][0];

            expect(target[0]).toEqual(header[0]);
            expect(tableJiveComponentView.selectColumn).toHaveBeenCalledWith("column1", columnData);
        });

        it("should select column on cell click", function() {
            sandbox.stub(tableJiveComponentView, "selectColumn");

            var columnData = {
                $header: {
                    data: sandbox.stub().withArgs("coluuid").returns("column1")
                }
            };

            sandbox.stub(tableJiveComponentView, "getColumnData").returns(columnData);

            var cell = reportEl.find("td#firstCell.jrcel");

            cell.click();

            expect(tableJiveComponentView.selectColumn).toHaveBeenCalledWith("column1", columnData);
        });

        it("should not select column on header click if it's not in the model", function() {
            sandbox.stub(tableJiveComponentView, "selectColumn");

            var columnData = {
                $header: {
                    data: sandbox.stub().withArgs("coluuid").returns("column3")
                }
            };

            sandbox.stub(tableJiveComponentView, "getColumnData").returns(columnData);

            var header = reportEl.find("td#firstHeader.jrcolHeader");

            header.click();

            expect(tableJiveComponentView.selectColumn).not.toHaveBeenCalled();
        });

        it("should not select column on cell click if it's not in the model", function() {
            sandbox.stub(tableJiveComponentView, "selectColumn");

            var columnData = {
                $header: {
                    data: sandbox.stub().withArgs("coluuid").returns("column3")
                }
            };

            sandbox.stub(tableJiveComponentView, "getColumnData").returns(columnData);

            var cell = reportEl.find("td#firstCell.jrcel");

            cell.click();

            expect(tableJiveComponentView.selectColumn).not.toHaveBeenCalled();
        });
    });

    describe("getHoverMenuChildren", function() {

        beforeEach(function() {
            model.config.allColumnsData = [
                {
                    interactive: false
                }
            ].concat(columns);

            model.columns = [].concat(columns);
        });

        it("should get hover menu children", function() {
            var hoverMenuOptions = tableJiveComponentView.getHoverMenuChildren(),
                hoverMenuOptionsWithoutTestFn = hoverMenuOptions.map(function(option) {
                    return _.omit(option, "test");
                });

            expect(hoverMenuOptionsWithoutTestFn).toEqual([
                {
                    "label": undefined,
                    "action": "select",
                    "message": jiveActions.SHOW_COLUMN,
                    "index": [
                        0,
                        1
                    ]
                },
                {
                    "label": "label0",
                    "id": "column1",
                    "message": jiveActions.SHOW_COLUMN,
                    "action": "select",
                    "index": 0
                },
                {
                    "label": "label1",
                    "id": "column2",
                    "message": jiveActions.SHOW_COLUMN,
                    "action": "select",
                    "index": 1
                }
            ]);
        });

        describe("hoverMenuOption test fn", function() {

            it("should return true if column is invisible", function() {
                var hoverMenuOption = tableJiveComponentView.getHoverMenuChildren()[1];

                model.columns = [columns[1]];

                expect(hoverMenuOption.test()).toEqual(true);
            });

            it("should return false if column is visible", function() {
                var hoverMenuOption = tableJiveComponentView.getHoverMenuChildren()[1];

                expect(hoverMenuOption.test()).toEqual(false);
            });
        });
    });
});