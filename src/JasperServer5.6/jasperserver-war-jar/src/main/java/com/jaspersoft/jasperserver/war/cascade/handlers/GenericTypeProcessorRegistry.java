/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.war.helper.GenericParametersHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: GenericTypeProcessorRegistry.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class GenericTypeProcessorRegistry {
    private final static Log log = LogFactory.getLog(GenericTypeProcessorRegistry.class);
    @Autowired
    private ApplicationContext context;

    private volatile Map<Class<?>, Map<String, Object>> processors = new HashMap<Class<?>, Map<String, Object>>();

    public <T> T getTypeProcessor(Class<?> typeToProcess, Class<T> processorType) {
        return getTypeProcessor(typeToProcess, processorType, true);
    }

    // casting to processorType is safe, see afterPropertiesSet()
    @SuppressWarnings("unchecked")
    public <T> T getTypeProcessor(String typeToProcessName, Class<T> processorType, Boolean exceptionIfNotConfigured){
        Map<String, Object> concreteProcessors = processors.get(processorType);
        if (concreteProcessors == null)
            synchronized (processors) {
                concreteProcessors = processors.get(processorType);
                if (concreteProcessors == null) {
                    concreteProcessors = initializeProcessors(processorType);
                    processors.put(processorType, concreteProcessors);
                }
            }
        if (exceptionIfNotConfigured && !concreteProcessors.containsKey(typeToProcessName))
            throw new IllegalStateException("Processor of type " + processorType.getName() + " for class " + typeToProcessName + " not configured");
        return (T) concreteProcessors.get(typeToProcessName);
    }

    public <T> T getTypeProcessor(Class<?> typeToProcess, Class<T> processorType, Boolean exceptionIfNotConfigured) {
        return getTypeProcessor(typeToProcess.getName(), processorType, exceptionIfNotConfigured);
    }

    protected Map<String, Object> initializeProcessors(Class<?> processorType) {
        Map<String, Object> processors = new HashMap<String, Object>();
        final String[] typeProcessorNames = context.getBeanNamesForType(processorType);
        if (typeProcessorNames != null && typeProcessorNames.length > 0)
            for (String typeProcessorBeanName : typeProcessorNames) {
                Object currentTypeProcessor = context.getBean(typeProcessorBeanName, processorType);
                Class<?> valueClass = GenericParametersHelper.getGenericTypeArgument(currentTypeProcessor.getClass(), processorType, 0);
                if (valueClass == null){
                    log.warn("Unable to find generic type of bean '" + typeProcessorBeanName + "' (bean class " + currentTypeProcessor.getClass() + ")");
                    continue;
                }
                processors.put(valueClass.getName(), currentTypeProcessor);
            }
        return processors;
    }
}
