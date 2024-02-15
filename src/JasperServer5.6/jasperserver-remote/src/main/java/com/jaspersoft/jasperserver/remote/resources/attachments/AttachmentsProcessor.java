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
package com.jaspersoft.jasperserver.remote.resources.attachments;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;

import java.io.InputStream;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: AttachmentsProcessor.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface AttachmentsProcessor<ServerType extends Resource> {
    /**
     * This method places binary data to corresponding file resources of server object.
     *
     * @param serverObject - the server object to process. Not null.
     * @param parts - the map with attachments input streams. Not null.
     * @return processed server object instance.
     * @throws MandatoryParameterNotFoundException if server object contains local file resource but corresponding attachment is missing.
     * @throws IllegalParameterValueException if corresponding resource reference isn't local or local, but FileResource is reference.
     */
    ServerType processAttachments(ServerType serverObject, Map<String, InputStream> parts) throws MandatoryParameterNotFoundException, IllegalParameterValueException;
}
