/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import jiveDataConverter from '../util/jiveDataConverter';

export default Backbone.Model.extend({
    defaults: function () {
        return {
            'operator': null,
            'value': null,
            'backgroundColor': null,
            'font': {
                'bold': null,
                'italic': null,
                'underline': null,
                'color': null
            }
        };
    },
    parse: function (jiveObj) {
        var valueAndOperator = jiveDataConverter.operatorAndValueToSchemaFormat.call(this.collection.parent, jiveObj.conditionTypeOperator, this.collection.dataType, jiveObj.conditionStart, jiveObj.conditionEnd, this.collection.conditionPattern);
        return {
            operator: valueAndOperator.operator,
            value: valueAndOperator.value,
            backgroundColor: jiveObj.conditionMode === 'Transparent' ? 'transparent' : jiveObj.conditionFontBackColor,
            font: {
                'italic': jiveObj.conditionFontItalic,
                'bold': jiveObj.conditionFontBold,
                'underline': jiveObj.conditionFontUnderline,
                'color': jiveObj.conditionFontColor
            }
        };
    },
    toJiveFormat: function (genericProperties) {
        var conditionPattern = this.collection.conditionPattern;
        var dataType = this.collection.dataType;
        var operator = this.get('operator');
        var value = this.get('value');
        var conditionStart = jiveDataConverter.filterStartValue(operator, value, dataType, genericProperties, conditionPattern);
        var conditionEnd = jiveDataConverter.filterEndValue(operator, value, dataType, genericProperties, conditionPattern);
        var conditionTypeOperator = jiveDataConverter.schemaFormatOperatorToFilterOperator(operator, value, dataType);
        var backgroundColor = this.get('backgroundColor');
        var isTransparent = backgroundColor === 'transparent';
        var conditionMode = isTransparent ? 'Transparent' : backgroundColor === null ? null : 'Opaque';
        var conditionFontBackColor = isTransparent ? null : backgroundColor;
        var font = this.get('font');
        return {
            'conditionStart': conditionStart,
            'conditionEnd': conditionEnd,
            'conditionTypeOperator': conditionTypeOperator,
            'conditionFontBold': font.bold,
            'conditionFontItalic': font.italic,
            'conditionFontUnderline': font.underline,
            'conditionFontColor': font.color,
            'conditionFontBackColor': conditionFontBackColor,
            'conditionMode': conditionMode
        };
    }
});