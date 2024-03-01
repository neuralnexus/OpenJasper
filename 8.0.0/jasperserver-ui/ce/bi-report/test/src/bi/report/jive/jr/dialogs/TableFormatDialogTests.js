/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import sinon from 'sinon';
import $ from 'jquery';
import TableFormatDialog from 'src/bi/report/jive/jr/dialogs/TableFormatDialog';
import Dialog from 'js-sdk/src/common/component/dialog/Dialog';
import {getFilterOperatorKey} from "../../../../../../../src/bi/report/jive/jr/dialogs/util/filterLabelResolver";

describe("TableFormatDialog Tests.", function() {
    var tableFormatDialog,
        sandbox,
        $el;

    beforeEach(function() {
        sandbox = sinon.createSandbox();

        sandbox.stub(Dialog.prototype, "_position");

        tableFormatDialog = new TableFormatDialog({
            i18n: {}
        });

        $el = $("<div></div>");

        tableFormatDialog.$el.height(100);
        tableFormatDialog.$el.width(400);

        $("body").append($el).height(2000);
    });

    afterEach(function() {
        tableFormatDialog.remove();
        $el.remove();
        sandbox.restore();
    });

    it("should open tableFormatDialog with centered position", function() {
        sandbox.spy(Dialog.prototype, "open");

        Dialog.prototype._position.returns({top:100, left: 250});

        var columnComponentModel = {
            get: sandbox.stub(),
            detailsRowFormat: {
                toJSON: sandbox.stub().returns({}),
                toJiveFormat: sandbox.stub().returns('')
            },
            conditions: {
                toJSON: sandbox.stub().returns({})
            },
            parent: {
                columnGroups: {
                    each: function() {}
                },
                columns: [],
                config: {
                    genericProperties: {
                        fonts: 1,
                        fontSizes: 2,
                        patterns: {
                            "datatype": "datatype"
                        }
                    }
                }
            }
        };

        columnComponentModel.parent.columns.push(columnComponentModel);
        columnComponentModel.get.withArgs("columnLabel").returns("label");
        columnComponentModel.get.withArgs("columnIndex").returns(0);
        columnComponentModel.get.withArgs("dataType").returns("datatype");
        columnComponentModel.get.withArgs("conditionalFormattingData").returns({
            conditionPattern: ""
        });

        $(window).scrollTop(700);

        tableFormatDialog.open(columnComponentModel);

        expect(tableFormatDialog.$el.css("top")).toEqual("800px");

        expect(tableFormatDialog.$el.css("left")).toEqual("250px");
    });

    it("should correctly provide filter operator i18n key", function() {
        const operatorType = "boolean";
        const operatorValueKey = "IS_TRUE";
        const expectedResult = "net.sf.jasperreports.components.sort.FilterTypeBooleanOperatorsEnum.IS_TRUE";
        expect(getFilterOperatorKey(operatorType, operatorValueKey)).toEqual(expectedResult);
    });
});