/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';
import $ from 'jquery';
import Backbone from 'backbone';
import ColumnGroupModel from './ColumnGroupModel';

var genericProperties = null;
var ColumnGroupCollection = Backbone.Collection.extend({ model: ColumnGroupModel });
export default BaseComponentModel.extend({
    defaults: function () {
        return {
            calendarPatterns: {},
            filterPatterns: {},
            fontSizes: [],
            fonts: {},
            operators: {},
            patterns: {},
            id: null,
            genericProperties: {},
            module: 'jive.table',
            type: jiveTypes.TABLE,
            uimodule: 'jive.interactive.column',
            hasFloatingHeader: null
        };
    },
    constructor: function () {
        this.columnGroups = new ColumnGroupCollection();
        BaseComponentModel.prototype.constructor.apply(this, arguments);
    },
    initialize: function (o) {
        this.config = {
            id: null,
            /**
                 * {"1":{"index":"1","label":"Name","uuid":"ace5fd47-03c8-4d26-b2c0-354ca60560e0","visible":false,"interactive":true},..}
                 */
            allColumnsData: null
        };
        $.extend(this.config, o);
        if (o.genericProperties) {
            genericProperties = o.genericProperties;
        } else {
            this.config.genericProperties = genericProperties;
        }
        this.columns = [];
        this.columnMap = {};
    },
    parse: function (response) {
        var self = this;
        if (response.allColumnGroupsData) {
            this.columnGroups.reset(response.allColumnGroupsData, {
                silent: true,
                parse: true
            });
            this.columnGroups.each(function (group) {
                group.parent = self;
            });
        }
        return response;
    },
    registerPart: function (column) {
        column.parent = this;
        column.trigger('parentTableComponentAttached');
        this.columns[column.get('columnIndex')] = column;
        this.columnMap[column.get('id')] = column;
    },
    getId: function () {
        return this.config.id;
    },
    handleServerError: function(result) {
        this.trigger("serverError", result);
    },
    handleClientError: function(result) {
        this.trigger("serverError", result);
    }
});