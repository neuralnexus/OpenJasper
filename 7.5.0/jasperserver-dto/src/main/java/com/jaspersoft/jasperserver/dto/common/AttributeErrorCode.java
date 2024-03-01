/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.dto.common;

import java.util.HashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.ATTRIBUTE_NAME;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.AVAILABLE_CATEGORIES;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.CATEGORY_NAME;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.HIERARCHY_NAME;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.INVALID_ATTRIBUTE_CATEGORY;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.INVALID_ATTRIBUTE_PATTERN;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.NAMED_CAPTURING_GROUPS;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.RESOURCE_FIELD;
import static com.jaspersoft.jasperserver.dto.common.AttributeErrorCode.ParamNames.RESOURCE_URI;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.buildProperties;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.checkPropValues;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.paramNames;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public enum AttributeErrorCode implements ErrorDescriptorTemplate {
    ATTRIBUTE_PATTERNS_INCLUDES_INVALID(Codes.ATTRIBUTE_PATTERNS_INCLUDES_INVALID,
            paramNames(INVALID_ATTRIBUTE_PATTERN, NAMED_CAPTURING_GROUPS)),
    PROFILE_ATTRIBUTE_SUBSTITUTION_NOT_FOUND(Codes.PROFILE_ATTRIBUTE_SUBSTITUTION_NOT_FOUND,
            paramNames(ATTRIBUTE_NAME, HIERARCHY_NAME)),
    PROFILE_ATTRIBUTE_SUBSTITUTION_CATEGORY_INVALID(Codes.PROFILE_ATTRIBUTE_SUBSTITUTION_CATEGORY_INVALID,
            paramNames(INVALID_ATTRIBUTE_CATEGORY, ATTRIBUTE_NAME, AVAILABLE_CATEGORIES)),
    PROFILE_ATTRIBUTE_SUBSTITUTION_NOT_FOUND_IN_RESOURCE(Codes.PROFILE_ATTRIBUTE_SUBSTITUTION_NOT_FOUND_IN_RESOURCE,
            paramNames(RESOURCE_URI, RESOURCE_FIELD, ATTRIBUTE_NAME, CATEGORY_NAME)),
    PROFILE_ATTRIBUTE_SUBSTITUTION_CATEGORY_INVALID_IN_RESOURCE(Codes.PROFILE_ATTRIBUTE_SUBSTITUTION_CATEGORY_INVALID_IN_RESOURCE,
            paramNames(RESOURCE_URI, RESOURCE_FIELD, INVALID_ATTRIBUTE_CATEGORY, ATTRIBUTE_NAME, AVAILABLE_CATEGORIES));

    String code;
    String[] paramNames;

    private static final Map<String, AttributeErrorCode> stringToEnum = new HashMap<String, AttributeErrorCode>();

    static {
        for (AttributeErrorCode code : values())
            stringToEnum.put(code.toString(), code);
    }

    AttributeErrorCode(String code, String[] paramNames) {
        this.code = code;
        this.paramNames = paramNames;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String[] getParamNames() {
        return this.paramNames;
    }

    @Override
    public ErrorDescriptor createDescriptor(Object... propValues) {
        return new ErrorDescriptor().setErrorCode(this.code)
                .setProperties(buildProperties(paramNames, propValues));
    }

    @Override
    public String toString() {
        return this.code;
    }

    public static AttributeErrorCode fromCode(String code) {
        return stringToEnum.get(code);
    }

    public interface Codes {
        String ATTRIBUTE_PATTERNS_INCLUDES_INVALID = "attribute.patterns.contains.invalid";
        String PROFILE_ATTRIBUTE_SUBSTITUTION_NOT_FOUND = "profile.attribute.substitution.not.found";
        String PROFILE_ATTRIBUTE_SUBSTITUTION_CATEGORY_INVALID = "profile.attribute.substitution.category.invalid";
        String PROFILE_ATTRIBUTE_SUBSTITUTION_NOT_FOUND_IN_RESOURCE = "profile.attribute.substitution.not.found.in.resource";
        String PROFILE_ATTRIBUTE_SUBSTITUTION_CATEGORY_INVALID_IN_RESOURCE = "profile.attribute.substitution.category.invalid.in.resource";
    }

    public interface ParamNames {
        String INVALID_ATTRIBUTE_PATTERN = "invalidAttributePattern";
        String NAMED_CAPTURING_GROUPS = "namedCapturingGroups";
        String ATTRIBUTE_NAME = "attributeName";
        String HIERARCHY_NAME = "hierarchyName";
        String INVALID_ATTRIBUTE_CATEGORY = "invalidAttributeCategory";
        String AVAILABLE_CATEGORIES = "availableCategories";
        String RESOURCE_URI = "resourceURI";
        String RESOURCE_FIELD = "resourceField";
        String CATEGORY_NAME = "categoryName";
    }
}