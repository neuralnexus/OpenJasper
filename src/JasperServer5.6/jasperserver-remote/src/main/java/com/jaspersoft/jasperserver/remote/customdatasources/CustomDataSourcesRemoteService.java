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
package com.jaspersoft.jasperserver.remote.customdatasources;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.dto.customdatasources.ClientCustomDataSourceDefinition;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: CustomDataSourcesRemoteService.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class CustomDataSourcesRemoteService implements InitializingBean {
    @Resource
    private List<CustomDataSourceDefinition> definitions;
    @Resource
    private CustomDataSourceDefinitionToClientConverter converter;

    private Map<String, CustomDataSourceDefinition> definitionMap = new HashMap<String, CustomDataSourceDefinition>();

    public List<String> getCustomDataSourceDefinitions(){
        return new ArrayList<String>(definitionMap.keySet());
    }

    public ClientCustomDataSourceDefinition getCustomDataSourceDefinition(String name){
        final CustomDataSourceDefinition customDataSourceDefinition = definitionMap.get(name);
        if(customDataSourceDefinition == null) throw new ResourceNotFoundException(name);
        return converter.toClient(customDataSourceDefinition, null);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for(CustomDataSourceDefinition currentDefinition : definitions){
            definitionMap.put(currentDefinition.getName(), currentDefinition);
        }
    }
}
