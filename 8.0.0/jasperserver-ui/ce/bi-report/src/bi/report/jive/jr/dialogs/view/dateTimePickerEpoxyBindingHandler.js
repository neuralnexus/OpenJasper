/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import DateAndTimePicker from 'js-sdk/src/components/dateAndTime/DateAndTimePicker';

function cleanUpFromDateTimePicker(inputId) {
    this.view.pickers.forEach(function (picker) {
        if (picker.$el.prop('id') == inputId) {
            picker.remove();
        }
    });
    this.view.pickers = this.view.pickers.filter(function (picker) {
        return inputId != picker.$el.prop('id');
    });
}
export default function ($element, dataType) {
    var previousDataType = this.view.viewModel.previous('dataType'), patterns = this.view.viewModel.get('calendarPatterns');
    if (!this.view.pickers) {
        this.view.pickers = [];
    }
    if (previousDataType != dataType) {
        if (previousDataType === 'date' || previousDataType === 'time') {
            cleanUpFromDateTimePicker.call(this, $element.prop('id'));
        }
        if (dataType === 'date') {
            this.view.pickers.push(new DateAndTimePicker({
                el: $element[0],
                dateFormat: patterns.date,
                timeFormat: patterns.time,
                skipMoving: true
            }));
        } else if (dataType === 'time') {
            this.view.pickers.push(new DateAndTimePicker({
                el: $element[0],
                timeFormat: patterns.time,
                skipMoving: true
            }));
        }
    }
}