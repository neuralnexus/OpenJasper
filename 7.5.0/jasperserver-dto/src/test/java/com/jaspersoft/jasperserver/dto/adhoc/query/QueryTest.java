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

package com.jaspersoft.jasperserver.dto.adhoc.query;

import com.jaspersoft.jasperserver.dto.adhoc.filters.FilterTest;
import com.jaspersoft.jasperserver.dto.executions.ClientMultiLevelQueryExecution;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 2/2/16 8:16AM
 */
public class QueryTest extends FilterTest {

    @Override
    protected String xml(Object value) throws Exception {
        StringWriter w = new StringWriter();
        getMarshaller(ClientMultiLevelQuery.class, ClientMultiAxisQuery.class).marshal(value, w);
        return w.toString().replace("\r\n", "\n").replace("\r", "\n");
    }

    @Override
    protected <T> T dto(String xml) throws IOException, JAXBException {
        return (T) getUnmarshaller(ClientMultiLevelQuery.class, ClientMultiAxisQuery.class).unmarshal(IOUtils.toInputStream(xml));
    }

    protected <T> T dtoForEntity(String xml, Class<T> entity) throws IOException, JAXBException {
        return (T) getUnmarshaller(entity).unmarshal(IOUtils.toInputStream(xml));
    }

    protected ClientMultiLevelQueryExecution request(ClientMultiLevelQuery cq) {
        return new ClientMultiLevelQueryExecution(cq, new ClientReference("/uri"));
    }
}