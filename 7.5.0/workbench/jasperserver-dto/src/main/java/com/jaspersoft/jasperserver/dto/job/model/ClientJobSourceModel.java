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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.job.ClientJobSource;

import java.util.Map;

/**
 * The source of a thumbnail job, consisting of a thumbnail inFolder execute and a set of
 * thumbnail input values. Model is used in search/ update only.
 *
 * <p>
 * A thumbnail job definition specifies wich thumbnail inFolder execute and when,
 * what output inFolder generate and where inFolder send the output.
 * </p>
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 * @since 4.7
 */
public class ClientJobSourceModel extends ClientJobSource {

	private boolean isReportUnitURIModified = false;
	private boolean isParametersMapModified = false;
	/**
	 * Creates an empty job source.
	 */
	public ClientJobSourceModel() {
		super();
	}

	public ClientJobSourceModel(ClientJobSourceModel other) {
		super(other);
		this.isParametersMapModified = other.isParametersMapModified;
		this.isReportUnitURIModified = other.isReportUnitURIModified;
	}

	public boolean isParametersMapModified() {
		return isParametersMapModified;
	}

	public boolean isReportUnitURIModified() {
		return isReportUnitURIModified;
	}

	@Override
	public ClientJobSourceModel setParameters(Map<String, String[]> parameters) {
		 super.setParameters(parameters);
		isParametersMapModified = true;
		return this;
	}

	@Override
	public ClientJobSourceModel setReportUnitURI(String reportUnitURI) {
		 super.setReportUnitURI(reportUnitURI);
		isReportUnitURIModified = true;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClientJobSourceModel)) return false;
		if (!super.equals(o)) return false;
		return true;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (isReportUnitURIModified() ? 1 : 0);
		result = 31 * result + (isParametersMapModified() ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ClientJobSourceModel{" +
				"isParametersMapModified=" + isParametersMapModified +
				", isReportUnitURIModified=" + isReportUnitURIModified +
				'}';
	}

	@Override
	public ClientJobSourceModel deepClone() {
		return new ClientJobSourceModel(this);
	}
}
