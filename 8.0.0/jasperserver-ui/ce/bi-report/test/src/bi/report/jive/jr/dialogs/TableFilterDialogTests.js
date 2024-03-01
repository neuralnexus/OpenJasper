/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import sinon from 'sinon';
import $ from 'jquery';
import TableFilterDialog from 'src/bi/report/jive/jr/dialogs/TableFilterDialog';
import Dialog from 'js-sdk/src/common/component/dialog/Dialog';

describe("TableFilterDialog Tests.", function() {
    var tableFilterDialog,
        sandbox,
        $el;

    beforeEach(function() {
        sandbox = sinon.createSandbox();

        sandbox.stub(Dialog.prototype, "_position");

        tableFilterDialog = new TableFilterDialog({
            i18n: {}
        });

        $el = $("<div></div>");

        tableFilterDialog.$el.height(222);
        tableFilterDialog.$el.width(433);

        $("body").append($el).height(2000);
    });

    afterEach(function() {
        tableFilterDialog.remove();
        $el.remove();
        sandbox.restore();
    });

    it("should open tableFilterDialog with centered position", function() {
        sandbox.spy(Dialog.prototype, "open");

        Dialog.prototype._position.returns({top:100, left: 250});

        var columnComponentModel = {
            get: sandbox.stub(),
            toReportComponentObject: function() {
                return {
                    label: "label",
                    filter: {
                        operator: null
                    },
                    dataType: "datatype"
                }
            },
            parent: {
                config: {
                    genericProperties: {
                        operators: {
                            "datatype": {}
                        }
                    },
                    calendarPatterns: ""
                }
            }
        };

        columnComponentModel.get.withArgs("dataType").returns("datatype");

        $(window).scrollTop(777);

        tableFilterDialog.open(columnComponentModel);

        expect(tableFilterDialog.$el.css("top")).toEqual("877px");

        expect(tableFilterDialog.$el.css("left")).toEqual("250px");
    });
});