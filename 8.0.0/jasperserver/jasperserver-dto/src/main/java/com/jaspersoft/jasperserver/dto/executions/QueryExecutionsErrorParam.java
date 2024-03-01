/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.dto.executions;

/**
 * @author Volodya Sabadosh
 */
public interface QueryExecutionsErrorParam {
    String EXECUTION_ID = "executionID";
    String DATASOURCE_URI = "dataSourceURI";
    String DATASOURCE_TYPE = "dataSourceType";
    String PROPERTY_PATH = "propertyPath";
    String INVALID_FILTER_EXPRESSION = "invalidFilterExpression";
    String FIELD_EXPRESSION = "fieldExpression";
    String FIELD_NAME = "fieldName";
    String FIELDS_NOT_FOUND = "fieldsNotFound";
    String DETAIL_MESSAGE = "detailMessage";
    String QUERY_FIELD_IDS = "queryFieldIDs";
    String QUERY_FIELD_ID = "queryFieldID";
    String FIRST_FIELD_ID = "firstFieldID";
    String SECOND_FIELD_ID = "secondFieldID";
    String MIN_SIZE = "minSize";
    String MAX_SIZE = "maxSize";
    String EXPRESSION_TYPE = "expressionType";
    String EXPRESSION_NAME = "expressionName";
    String AGGREGATION_FUNCTION ="aggregationFunction";
    String INVALID_CATEGORIZER_NAME = "invalidCategorizerName";
    String INVALID_LIMIT_VALUE = "invalidLimitValue";
    String INVALID_TYPE = "invalidType";
    String AGGREGATE_FIELD_ID = "aggregationFieldID";
    String FIELD_ID = "fieldID";
    String INVALID_AGGREGATION_FUNCTION = "invalidAggregationFunction";
    String INVALID_AGGREGATION_EXPRESSION = "invalidAggregationExpression";
    String INVALID_FIELD_EXPRESSION = "invalidFieldExpression";
    String ERROR_MESSAGE = "errorMessage";
    String MIN_QUERY_LIMIT = "minQueryLimit";
    String MAX_QUERY_LIMIT = "maxQueryLimit";
    String START_RANGE = "startRange";
    String END_RANGE = "endRange";
    String MIN_PAGE_SIZE = "minPageSize";
    String MEASURE_NAME = "measureName";
    String FIELD_NUMBER = "fieldNumber";
    String FIELD_COUNT = "fieldCount";
    String MEASURE_INDEX = "measureIndex";
    String MEASURE_NUMBER = "measureNumber";
    String MEASURE_COUNT = "measureCount";
    String MEASURE_ID = "measureID";
    String EXPRESSION = "expression";
    String VAR_NAME = "expression";
    String AXIS_NAME = "axisName";
    String AXIS_TYPE = "axisType";
    String DIMENSION_NUMBER = "dimensionNumber";
    String DIMENSION_COUNT = "dimensionCount";
    String DIMENSION_LEVEL_COUNT = "dimensionLevelCount";
    String FIELD_UNIQUE_NAME = "fieldUniqueName";
    String FIELD_TYPE = "fieldType";
    String METADATA_FIELD_NAMES_COLLISION_PARAMS = "metadataFieldNamesCollisionParams";
    String INVALID_TRANSFORMATION_PATH = "invalidTransformationPath";
    String AVAILABLE_TRANSFORMATION_PATHS = "availableTransformationPaths";
    String INVALID_PARAMETER_NAMES = "invalidParameterNames";
    String PARAMETER_TYPES = "parameterTypes";
    String OPERATOR = "operator";
    String AMBIGUOUS_REFS = "ambiguousRefs";
    String REFERENCE = "reference";
    String DUPLICATED_IDS = "duplicatedIds";
    String UNSUPPORTED_TYPE = "unsupportedType";
}
