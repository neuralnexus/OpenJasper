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

package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * Used to indicate any non-supported resources.
 * Such resources will be skipped during import-export task.
 *
 * To add a new old / legacy resource define it in <b>castorschema.xsd</b> file
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
public class LegacyResourceBean extends ResourceBean {
    @Override
    protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isSupported() {
        return false;
    }
}
