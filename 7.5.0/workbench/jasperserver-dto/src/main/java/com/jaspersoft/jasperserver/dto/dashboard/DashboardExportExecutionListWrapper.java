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

package com.jaspersoft.jasperserver.dto.dashboard;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * <p>DTO for dashboard executions.</p>
 *
 * @author Zakhar Tomchenko
 * @version $Id: $
 */
@XmlRootElement(name = "dashboardExportExecutions")
public class DashboardExportExecutionListWrapper implements DeepCloneable<DashboardExportExecutionListWrapper> {
    private List<DashboardExportExecution> executions;

    public DashboardExportExecutionListWrapper() {}

    public DashboardExportExecutionListWrapper(DashboardExportExecutionListWrapper other) {
        checkNotNull(other);

        executions = copyOf(other.executions);
    }

    @XmlElement(name = "dashboardExportExecution")
    public List<DashboardExportExecution> getExecutions() {
        return executions;
    }

    public DashboardExportExecutionListWrapper setExecutions(List<DashboardExportExecution> executions) {
        this.executions = executions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashboardExportExecutionListWrapper that = (DashboardExportExecutionListWrapper) o;

        return !(executions != null ? !executions.equals(that.executions) : that.executions != null);
    }

    @Override
    public int hashCode() {
        return executions != null ? executions.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DashboardExportExecutionListWrapper{" +
                "executions=" + executions +
                '}';
    }

    /*
     * DeepCloneable
     */

    @Override
    public DashboardExportExecutionListWrapper deepClone() {
        return new DashboardExportExecutionListWrapper(this);
    }
}

