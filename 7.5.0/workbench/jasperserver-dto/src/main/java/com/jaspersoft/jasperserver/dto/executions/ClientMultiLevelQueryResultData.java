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
package com.jaspersoft.jasperserver.dto.executions;

import com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientMultiLevelDataset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 11.02.2016
 */
@XmlRootElement(name = "resultData")
public class ClientMultiLevelQueryResultData extends ClientQueryResultData<ClientMultiLevelQueryResultData,
        ClientMultiLevelDataset, Integer> {

    public ClientMultiLevelQueryResultData() {
    }

    public ClientMultiLevelQueryResultData(ClientMultiLevelQueryResultData source) {
        super(source);
    }

    @Override
    protected Integer deepCopyOfTotalCounts(Integer totalCounts) {
        return totalCounts;
    }

    @Override
    protected ClientMultiLevelDataset deepCopyOfDataSet(ClientMultiLevelDataset dataSet) {
        return copyOf(dataSet);
    }

    @Override
    public ClientMultiLevelQueryResultData deepClone() {
        return new ClientMultiLevelQueryResultData(this);
    }

    public ClientMultiLevelQueryResultData(ClientMultiLevelDataset dataSet) {
        setDataSet(dataSet);
    }

    @Override
    @XmlElement(name = "dataset")
    public ClientMultiLevelDataset getDataSet() {
        return dataSet;
    }

    @Override
    public ClientMultiLevelQueryResultData setDataSet(ClientMultiLevelDataset dataset) {
        this.dataSet = dataset;
        return this;
    }

    @Override
    @XmlElement(name = "totalCounts")
    public Integer getTotalCounts() {
        return totalCounts;
    }

    @Override
    public ClientMultiLevelQueryResultData setTotalCounts(Integer totalCounts) {
        this.totalCounts = totalCounts;
        return this;
    }

    @Override
    public String toString() {
        return "ClientMultiLevelQueryResultData{} " + super.toString();
    }
}