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
package com.jaspersoft.jasperserver.rest.services;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.services.AttributesRemoteService;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.rest.utils.JAXBList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * @author carbiv
 * @version $Id: RESTAttribute.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("restProfileAttributeService")
public class RESTAttribute extends RESTAbstractService
{
    private final static Log log = LogFactory.getLog(RESTAttribute.class);
    @Resource
    private AttributesRemoteService attributesRemoteService;
    @SuppressWarnings("unused")//used by Spring
    public void setAttributesRemoteService(AttributesRemoteService attributesRemoteServiceImpl) {
        this.attributesRemoteService = attributesRemoteServiceImpl;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp,
                generateProfileAttributeReport(attributesRemoteService
                        .getAttributesOfUser(restUtils.extractResourceName(req.getPathInfo()))));
    }

    private String generateProfileAttributeReport(List<ProfileAttribute> atts)
    {
        try{
            StringWriter sw = new StringWriter();

            JAXBList<ProfileAttribute> lst;
            lst = new JAXBList<ProfileAttribute>(atts);

            Marshaller m = restUtils.getMarshaller(JAXBList.class, ProfileAttributeImpl.class);
            m.marshal(lst, sw);
            if (log.isDebugEnabled()) {
                log.debug("finished marshaling attributes: " + lst.size());
            }

            return sw.toString();
        }
        catch (JAXBException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
     {
         try{
             String userName = restUtils.getFullUserName(restUtils.extractResourceName(req.getPathInfo()));
            @SuppressWarnings("unchecked")
            JAXBList<ProfileAttribute> atts = (JAXBList<ProfileAttribute>) restUtils.unmarshal(req.getInputStream(), JAXBList.class, ProfileAttributeImpl.class);

            for (int i=0 ; i<atts.size() ; i++){
                    attributesRemoteService.putAttribute(userName, atts.get(i));
            }

            restUtils.setStatusAndBody(HttpServletResponse.SC_CREATED, resp, "");
        } catch (IOException e) {
             throw new ServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
         } catch (JAXBException e) {
             throw new ServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
         }
     }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        doPut(req, resp);
    }
}
