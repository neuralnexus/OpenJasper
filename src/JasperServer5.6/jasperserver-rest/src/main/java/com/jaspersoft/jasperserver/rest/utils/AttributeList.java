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
package com.jaspersoft.jasperserver.rest.utils;


import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: carbiv
 * Date: 10/8/11
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="ProfileAttributes")
public class AttributeList<ProfileAttributeImpl>{
    protected List<ProfileAttributeImpl> list;

    public AttributeList(){}

    public AttributeList(List<ProfileAttributeImpl> list){
        this.list=list;
    }

    @XmlElement(name="ProfileAttribute")
    public List<ProfileAttributeImpl> getList(){
        return list;
    }

    public void setHostedServices(List<ProfileAttributeImpl> attributes)
    {
        list = attributes;
    }
}

