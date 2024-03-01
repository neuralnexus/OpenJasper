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
package com.jaspersoft.jasperserver.dto.resources.domain.validation;

import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.dto.resources.domain.GroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 * @since
 */
public class DomainJsonPathHelper {
    private static Pattern  ROOT_RESOURCES_ELEMENT_PATTERN = Pattern.compile("schema\\.resources\\[(\\d+)\\]");
    private static Pattern  ROOT_PRESENTATION_ELEMENT_PATTERN = Pattern.compile("schema\\.presentation\\[(\\d+)\\]");
    private static Pattern  ELEMENT_INDEX_PATTERN = Pattern.compile(".elements\\[(\\d+)\\]");
    public static String getResourcePath(ClientDomain domain, String jsonPath){
        List<ResourceElement> resources = domain.getSchema() != null && domain.getSchema().getResources() != null ?
                domain.getSchema().getResources() : new ArrayList<ResourceElement>();
        final StringBuilder resourcePath = new StringBuilder();
        final Matcher matcher = ROOT_RESOURCES_ELEMENT_PATTERN.matcher(jsonPath);
        if(matcher.find()){
            Integer rootElementIndex = Integer.valueOf(matcher.group(1));
            final SchemaElement rootElement = resources.get(rootElementIndex);
            List<SchemaElement> elements;
            if(rootElement instanceof GroupElement){
                elements = ((GroupElement) rootElement).getElements();
            } else {
                elements = new ArrayList<SchemaElement>();
            }
            final String elementsPath = getElementsPath(elements, jsonPath);
            resourcePath.append(rootElement.getName());
            if (!elementsPath.isEmpty()) {
                resourcePath.append(".").append(elementsPath);
            }
        } else {
            throw new IllegalArgumentException("JSON Path " + jsonPath +
                    " should start from schema.resources[{rootResourceIndex}]. Where {rootResourceIndex} is a number");
        }
        return resourcePath.toString();
    }

    private static String getElementsPath(List<SchemaElement> schemaElements, String propertyPath){
        StringBuilder result = new StringBuilder();
        SchemaElement currentElement;
        List<SchemaElement> currentElements = schemaElements;
        final Matcher elementIndexMatcher = ELEMENT_INDEX_PATTERN.matcher(propertyPath);
        while (elementIndexMatcher.find()){
            Integer currentIndex = Integer.valueOf(elementIndexMatcher.group(1));
            currentElement = currentElements.get(currentIndex);
            if(result.length() > 0){
                result.append(".");
            }
            result.append(currentElement.getName());
            if(currentElement instanceof GroupElement){
                currentElements = ((GroupElement) currentElement).getElements();
            }
        }
        return result.toString();
    }

    public static PresentationPath getPresentationPath(ClientDomain domain, String propertyPath){
        final Matcher matcher = ROOT_PRESENTATION_ELEMENT_PATTERN.matcher(propertyPath);
        if(!matcher.find()){
            throw new IllegalArgumentException("JSON Path " + propertyPath +
                    " should start from schema.presentation[{dataIslandIndex}]. Where {dataIslandIndex} is a number");
        }
        final List<PresentationGroupElement> presentation = domain.getSchema() != null && domain.getSchema().getPresentation() != null ?
                domain.getSchema().getPresentation() : new ArrayList<PresentationGroupElement>();
        Integer dataIslandIndex = Integer.valueOf(matcher.group(1));
        final PresentationGroupElement dataIsland = presentation.get(dataIslandIndex);
        return new PresentationPath(dataIsland.getName(), getElementsPath((List) dataIsland.getElements(), propertyPath));
    }

    public static class PresentationPath{
        private String dataIslandName;
        private String hierarchicalName;
        public PresentationPath(String dataIslandName, String hierarchicalName){
            this.dataIslandName = dataIslandName;
            this.hierarchicalName = hierarchicalName;
        }

        public String getDataIslandName() {
            return dataIslandName;
        }

        public PresentationPath setDataIslandName(String dataIslandName) {
            this.dataIslandName = dataIslandName;
            return this;
        }

        public String getHierarchicalName() {
            return hierarchicalName;
        }

        public PresentationPath setHierarchicalName(String hierarchicalName) {
            this.hierarchicalName = hierarchicalName;
            return this;
        }
    }
}
