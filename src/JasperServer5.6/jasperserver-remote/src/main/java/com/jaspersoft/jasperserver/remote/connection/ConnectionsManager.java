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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.UnsupportedOperationRemoteException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.war.helper.GenericParametersHelper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ConnectionsManager.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class ConnectionsManager implements InitializingBean {
    @Resource
    private List<ConnectionManagementStrategy<?>> strategies;
    private Map<String, Class<?>> typeToClassMapping;
    private Map<Class<?>, ConnectionManagementStrategy<?>> classToStrategyMapping;
    @Resource(name = "connectionsCache")
    private Cache cache;
    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    public Class<?> getConnectionDescriptionClass(String connectionType) {
        return typeToClassMapping.get(connectionType != null ? connectionType.toLowerCase() : null);
    }

    public UUID createConnection(Object connectionDescription) throws IllegalParameterValueException {
        final Map<String, Object> data = new HashMap<String, Object>();
        // generic type processor registry assures safety of unchecked assignment
        @SuppressWarnings("unchecked")
        final ConnectionValidator<Object> validator = genericTypeProcessorRegistry.getTypeProcessor(connectionDescription.getClass(), ConnectionValidator.class, false);
        if (validator != null) {
            validator.validate(connectionDescription);
        }
        final ConnectionManagementStrategy<Object> strategy = getStrategy(connectionDescription);
        final Object connection = strategy.createConnection(connectionDescription, data);
        return cacheItem(new ConnectionDataPair(connection, data));
    }

    public Object getConnection(UUID uuid) throws ResourceNotFoundException {
        final ConnectionDataPair pair = getItemFromCache(uuid);
        return pair != null ? getStrategy(pair.connection).secureGetConnection(pair.connection, pair.data) : null;
    }

    public void removeConnection(UUID uuid) throws ResourceNotFoundException {
        final ConnectionDataPair pair = getItemFromCache(uuid);
        if (pair != null) {
            getStrategy(pair.connection).deleteConnection(pair.connection, pair.data);
            uncacheItem(uuid);
        }
    }

    public Object modifyConnection(UUID uuid, Object newConnectionDescription) throws IllegalParameterValueException, ResourceNotFoundException {
        final ConnectionDataPair pair = getItemFromCache(uuid);
        if (pair != null && pair.connection != null && pair.connection.getClass() != newConnectionDescription.getClass()) {
            throw new IllegalParameterValueException("connectionType", ClientTypeHelper.extractClientType(newConnectionDescription.getClass()));
        }
        // generic type processor registry assures safety of unchecked assignment
        @SuppressWarnings("unchecked")
        final ConnectionValidator<Object> validator = genericTypeProcessorRegistry.getTypeProcessor(newConnectionDescription.getClass(), ConnectionValidator.class, false);
        if (validator != null) {
            validator.validate(newConnectionDescription);
        }
        final ConnectionManagementStrategy<Object> strategy = getStrategy(newConnectionDescription);
        pair.connection = strategy.modifyConnection(newConnectionDescription, pair.connection, pair.data);
        replaceItem(uuid, pair);
        return strategy.secureGetConnection(pair.connection, pair.data);
    }

    // generic processor registry assures safety of call
    @SuppressWarnings("unchecked")
    public Object getConnectionMetadata(UUID uuid) throws ResourceNotFoundException, UnsupportedOperationRemoteException {
        final ConnectionDataPair pair = getItemFromCache(uuid);
        final ConnectionMetadataBuilder typeProcessor = genericTypeProcessorRegistry.getTypeProcessor(pair.connection.getClass(), ConnectionMetadataBuilder.class, false);
        if (typeProcessor == null) {
            throw new UnsupportedOperationRemoteException(ClientTypeHelper.extractClientType(pair.connection.getClass()) + "/metadata");
        }
        return typeProcessor.build(pair.connection);
    }

    // initialization code in afterPropertiesSet() ensures cast safety in this case.
    @SuppressWarnings("unchecked")
    protected <T> ConnectionManagementStrategy<T> getStrategy(T object) {
        return (ConnectionManagementStrategy<T>) classToStrategyMapping.get(object != null ? object.getClass() : null);
    }

    protected UUID cacheItem(ConnectionDataPair item) {
        final UUID uuid = UUID.randomUUID();
        cache.put(new Element(uuid, item));
        return uuid;
    }

    protected ConnectionDataPair getItemFromCache(UUID uuid) throws ResourceNotFoundException {
        final Element element = cache.get(uuid);
        if (element == null) throw new ResourceNotFoundException(uuid.toString());
        return (ConnectionDataPair) element.getObjectValue();
    }

    protected void uncacheItem(UUID uuid) {
        cache.remove(uuid);
    }

    protected void replaceItem(UUID uuid, ConnectionDataPair item) {
        cache.replace(new Element(uuid, item));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Class<?>> typeToClassMap = new HashMap<String, Class<?>>();
        Map<Class<?>, ConnectionManagementStrategy<?>> classToStrategyMap = new HashMap<Class<?>, ConnectionManagementStrategy<?>>();
        if (strategies != null) {
            for (ConnectionManagementStrategy<?> currentStrategy : strategies) {
                final Class<?> connectionDescriptionClass = GenericParametersHelper.getGenericTypeArgument(
                        currentStrategy.getClass(), ConnectionManagementStrategy.class, 0);
                final String clientType = ClientTypeHelper.extractClientType(connectionDescriptionClass).toLowerCase();
                if (typeToClassMap.containsKey(clientType)) {
                    throw new IllegalStateException("Connection client type '" + clientType
                            + "' is duplicated. Conflicting classes: " + typeToClassMap.get(clientType).getName()
                            + ", " + connectionDescriptionClass.getName());
                }
                typeToClassMap.put(clientType, connectionDescriptionClass);
                classToStrategyMap.put(connectionDescriptionClass, currentStrategy);
            }
        }
        typeToClassMapping = Collections.unmodifiableMap(typeToClassMap);
        classToStrategyMapping = Collections.unmodifiableMap(classToStrategyMap);
    }

    private class ConnectionDataPair {
        private ConnectionDataPair(Object connection, Map<String, Object> data) {
            this.connection = connection;
            this.data = data;
        }

        private Object connection;
        private Map<String, Object> data;
    }
}
