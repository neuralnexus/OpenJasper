/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.dto.resources.domain;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JoinInfo {

    private Boolean includeAllJoinsForQueryFieldTables;
    private Boolean includeAllDataIslandJoins;
    private Boolean suppressCircularJoins;
    @Valid
    private List<Join> joins;
    private List<String> mandatoryTables;

    public JoinInfo(){}

    public JoinInfo(JoinInfo source){
        includeAllJoinsForQueryFieldTables = source.getIncludeAllJoinsForQueryFieldTables();
        includeAllDataIslandJoins = source.getIncludeAllDataIslandJoins();
        suppressCircularJoins = source.getSuppressCircularJoins();
        final List<Join> sourceJoins = source.getJoins();
        if(sourceJoins != null){
            joins = new ArrayList<Join>(sourceJoins.size()){{
                for(Join join : sourceJoins){
                    add(new Join(join));
                }
            }};
        }
        final List<String> sourceMandatoryTables = source.getMandatoryTables();
        if(sourceMandatoryTables != null){
            mandatoryTables = new ArrayList<String>();
            mandatoryTables.addAll(sourceMandatoryTables);
        }
    }

    public Boolean getIncludeAllJoinsForQueryFieldTables() {
        return includeAllJoinsForQueryFieldTables;
    }

    public JoinInfo setIncludeAllJoinsForQueryFieldTables(Boolean includeAllJoinsForQueryFieldTables) {
        this.includeAllJoinsForQueryFieldTables = includeAllJoinsForQueryFieldTables;
        return this;
    }

    public Boolean getIncludeAllDataIslandJoins() {
        return includeAllDataIslandJoins;
    }

    public JoinInfo setIncludeAllDataIslandJoins(Boolean includeAllDataIslandJoins) {
        this.includeAllDataIslandJoins = includeAllDataIslandJoins;
        return this;
    }

    public Boolean getSuppressCircularJoins() {
        return suppressCircularJoins;
    }

    public JoinInfo setSuppressCircularJoins(Boolean suppressCircularJoins) {
        this.suppressCircularJoins = suppressCircularJoins;
        return this;
    }

    @XmlElementWrapper(name = "joins")
    @XmlElement(name = "join")
    public List<Join> getJoins() {
        return joins;
    }

    public JoinInfo setJoins(List<Join> joins) {
        this.joins = joins;
        return this;
    }


    @XmlElementWrapper(name = "mandatoryTables")
    @XmlElement(name = "mandatoryTable")
    public List<String> getMandatoryTables() {
        return mandatoryTables;
    }

    public JoinInfo setMandatoryTables(List<String> mandatoryTables) {
        this.mandatoryTables = mandatoryTables;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoinInfo)) return false;

        JoinInfo joinInfo = (JoinInfo) o;

        if (includeAllJoinsForQueryFieldTables != null ?
                !includeAllJoinsForQueryFieldTables.equals(joinInfo.includeAllJoinsForQueryFieldTables)
                : joinInfo.includeAllJoinsForQueryFieldTables != null)
            return false;
        if (includeAllDataIslandJoins != null ? !includeAllDataIslandJoins.equals(joinInfo.includeAllDataIslandJoins)
                : joinInfo.includeAllDataIslandJoins != null)
            return false;
        if (suppressCircularJoins != null ? !suppressCircularJoins.equals(joinInfo.suppressCircularJoins)
                : joinInfo.suppressCircularJoins != null)
            return false;
        if (joins != null ? !joins.equals(joinInfo.joins) : joinInfo.joins != null) return false;
        if (mandatoryTables != null ? !mandatoryTables.equals(joinInfo.mandatoryTables)
                : joinInfo.mandatoryTables != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = includeAllJoinsForQueryFieldTables != null ? includeAllJoinsForQueryFieldTables.hashCode() : 0;
        result = 31 * result + (includeAllDataIslandJoins != null ? includeAllDataIslandJoins.hashCode() : 0);
        result = 31 * result + (suppressCircularJoins != null ? suppressCircularJoins.hashCode() : 0);
        result = 31 * result + (joins != null ? joins.hashCode() : 0);
        result = 31 * result + (mandatoryTables != null ? mandatoryTables.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JoinInfo{" +
                "includeAllJoinsForQueryFieldTables=" + includeAllJoinsForQueryFieldTables +
                ", includeAllDataIslandJoins=" + includeAllDataIslandJoins +
                ", suppressCircularJoins=" + suppressCircularJoins +
                ", joins=" + joins +
                ", mandatoryTables=" + mandatoryTables +
                '}';
    }
}
