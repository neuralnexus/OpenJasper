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
package com.jaspersoft.jasperserver.search.mode;

import java.io.Serializable;

import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.state.InitialStateResolver;
import com.jaspersoft.jasperserver.search.util.JSONConverter;

/**
 * <p>Settings for SearchMode</p>
 *
 * @author Yuriy Plakosh
 * @version $Id: SearchModeSettings.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class SearchModeSettings  implements Serializable{
    private RepositorySearchConfiguration repositorySearchConfiguration;
    private JSONConverter jsonConverter;
    private InitialStateResolver initialStateResolver;

    public RepositorySearchConfiguration getRepositorySearchConfiguration() {
        return repositorySearchConfiguration;
    }

    public void setRepositorySearchConfiguration(RepositorySearchConfiguration repositorySearchConfiguration) {
        this.repositorySearchConfiguration = repositorySearchConfiguration;
    }

    public JSONConverter getJsonConverter() {
        return jsonConverter;
    }

    public void setJsonConverter(JSONConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public InitialStateResolver getInitialStateResolver() {
        return initialStateResolver;
    }

    public void setInitialStateResolver(InitialStateResolver initialStateResolver) {
        this.initialStateResolver = initialStateResolver;
    }
}
