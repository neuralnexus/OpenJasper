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

import com.jaspersoft.jasperserver.dto.common.ResourceLocation;
import com.jaspersoft.jasperserver.dto.connection.AbstractFileConnection;
import com.jaspersoft.jasperserver.dto.connection.FtpConnection;
import com.jaspersoft.jasperserver.dto.connection.TxtFileConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: AbstractFileConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class AbstractFileConnectionStrategy<FileTypeConnection extends AbstractFileConnection>
        implements ConnectionManagementStrategy<FileTypeConnection>, ConnectionMetadataBuilder<FileTypeConnection>, ConnectionValidator<FileTypeConnection> {
    private static final Log log = LogFactory.getLog(AbstractFileConnectionStrategy.class);
    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    @Override
    public FileTypeConnection createConnection(FileTypeConnection connectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        return connectionDescription;
    }

    @Override
    public void deleteConnection(FileTypeConnection connectionDescription, Map<String, Object> data) {
        // nothing to do for now
    }

    @Override
    public FileTypeConnection modifyConnection(FileTypeConnection newConnectionDescription, FileTypeConnection oldConnectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        return newConnectionDescription;
    }

    @Override
    public FileTypeConnection secureGetConnection(FileTypeConnection connectionDescription, Map<String, Object> data) {
        FileTypeConnection copy = cloneConnection(connectionDescription);
        if(copy.getLocation() instanceof FtpConnection){
            ((FtpConnection) copy.getLocation()).setPassword(null);
        }
        return copy;
    }

    @Override
    public Object build(FileTypeConnection connection) {
        InputStream inputStream = null;
        Object metadata = null;
        try {
            // Type safety is assured by generic processor registry. Assignment is safe.
            @SuppressWarnings("unchecked")
            final FileAccessor<ResourceLocation> typeProcessor = genericTypeProcessorRegistry.getTypeProcessor(connection.getLocation().getClass(), FileAccessor.class);
            inputStream = typeProcessor.openStream(connection.getLocation());
            metadata = internalBuildMetadata(connection, inputStream);
        } catch (IOException e) {
            // let container handle this case
            throw new RuntimeException(e);
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Error occure by closing of file input stream. File location: " + connection.getLocation().toString(), e);
                }
            }
        }
        return metadata;
    }

    @Override
    public void validate(FileTypeConnection connection) throws RemoteException {
        final ResourceLocation location = connection.getLocation();
        if(location == null){
            throw new MandatoryParameterNotFoundException("location");
        }
        // Type safety is assured by generic processor registry. Assignment is safe.
        @SuppressWarnings("unchecked")
        final FileAccessor<ResourceLocation> fileAccessor = genericTypeProcessorRegistry
                .getTypeProcessor(location.getClass(), FileAccessor.class);
        if(!fileAccessor.exist(location)){
            throw new ResourceNotFoundException(location.toString());
        }
    }

    protected String buildColumnName(int columnIndex){
        return "Field" + columnIndex;
    }

    protected abstract FileTypeConnection cloneConnection(FileTypeConnection connectionDescription);
    protected abstract Object internalBuildMetadata(FileTypeConnection connection, InputStream inputStream) throws IOException;
}
