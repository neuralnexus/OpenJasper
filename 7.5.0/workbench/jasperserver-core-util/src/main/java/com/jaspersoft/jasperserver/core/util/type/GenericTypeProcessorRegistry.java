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
package com.jaspersoft.jasperserver.core.util.type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Component
public class GenericTypeProcessorRegistry implements ApplicationContextAware {
    private final static Log log = LogFactory.getLog(GenericTypeProcessorRegistry.class);

    private ApplicationContext context;

    private volatile Map<Class<?>, Map<Class<?>, Object>> processors = new HashMap<Class<?>, Map<Class<?>, Object>>();

    public <T> T getTypeProcessor(Class<?> typeToProcess, Class<T> processorType) {
        return getTypeProcessor(typeToProcess, processorType, true);
    }

    // casting to processorType is safe, see afterPropertiesSet()
    @SuppressWarnings("unchecked")
    public <T> T getTypeProcessor(String typeToProcessName, Class<T> processorType, Boolean exceptionIfNotConfigured){
        try {
            return getTypeProcessor(Class.forName(typeToProcessName), processorType, exceptionIfNotConfigured);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid typeToProcessName: " + typeToProcessName, e);
        }
    }

    public <T> T getTypeProcessor(Class<?> typeToProcess, Class<T> processorType, Boolean exceptionIfNotConfigured) {
        Map<Class<?>, Object> concreteProcessors = getProcessorMapping(processorType);
        if (exceptionIfNotConfigured && !concreteProcessors.containsKey(typeToProcess))
            throw new IllegalStateException("Processor of type " + processorType.getName() + " for class " + typeToProcess.getName() + " not configured");
        return (T) concreteProcessors.get(typeToProcess);
    }

    public Map<Class<?>, Object> getProcessorMapping(Class<?> processorType){
        Map<Class<?>, Object> concreteProcessors = processors.get(processorType);
        if (concreteProcessors == null) {
            synchronized (processors) {
                concreteProcessors = processors.get(processorType);
                if (concreteProcessors == null) {
                    concreteProcessors = initializeProcessors(processorType);
                    processors.put(processorType, concreteProcessors);
                }
            }
        }
        return concreteProcessors;
    }

    protected Map<Class<?>, Object> initializeProcessors(Class<?> processorType) {
        Map<Class<?>, Object> processors = new HashMap<Class<?>, Object>();
        final String[] typeProcessorNames = context.getBeanNamesForType(processorType);
        if (typeProcessorNames != null && typeProcessorNames.length > 0)
            for (String typeProcessorBeanName : typeProcessorNames) {
                Object currentTypeProcessor = context.getBean(typeProcessorBeanName, processorType);
                final List<Class<?>> processableTypes;
                if(currentTypeProcessor instanceof MultipleTypeProcessor
                        && ((MultipleTypeProcessor) currentTypeProcessor).getProcessableTypes(processorType) != null){
                    processableTypes = ((MultipleTypeProcessor) currentTypeProcessor).getProcessableTypes(processorType);
                } else if(currentTypeProcessor.getClass().getAnnotation(TypesProcessor.class) != null){
                    final TypesProcessor annotation = currentTypeProcessor.getClass().getAnnotation(TypesProcessor.class);
                    processableTypes = Arrays.asList(annotation.value());
                } else {
                    Class<?> valueClass = GenericParametersHelper.getGenericTypeArgument(currentTypeProcessor.getClass(), processorType, 0);
                    if (valueClass == null) {
                        log.warn("Unable to find generic type of bean '" + typeProcessorBeanName + "' (bean class " + currentTypeProcessor.getClass() + ")");
                        processableTypes = new ArrayList<Class<?>>();
                    } else {
                        processableTypes = (List)Arrays.asList(valueClass);
                    }
                }
                for (Class<?> processableType : processableTypes) {
                    if(processors.containsKey(processableType)){
                        log.warn("Multiple processors are available for processable type " + processableType.getName() +
                                ". Processor type " + processorType.getName());
                    }
                    processors.put(processableType, currentTypeProcessor);
                }
            }
        return processors;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
