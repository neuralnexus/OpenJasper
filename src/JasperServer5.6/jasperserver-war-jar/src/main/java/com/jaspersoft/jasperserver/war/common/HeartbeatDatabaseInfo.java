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
package com.jaspersoft.jasperserver.war.common;

import org.apache.commons.httpclient.methods.PostMethod;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HeartbeatDatabaseInfo extends HeartbeatInfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String databaseName = null;
	private String databaseVersion = null;

	/**
	 * @return Returns the databaseName.
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @param databaseName The databaseName to set.
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * @return Returns the databaseVersion.
	 */
	public String getDatabaseVersion() {
		return databaseVersion;
	}

	/**
	 * @param databaseVersion The databaseVersion to set.
	 */
	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public void contributeToHttpCall(PostMethod post)
	{
		post.addParameter("repoDbName[]", getDatabaseName() == null ? "" : getDatabaseName());
		post.addParameter("repoDbVersion[]", getDatabaseVersion() == null ? "" : getDatabaseVersion());
		post.addParameter("repoDbCount[]", String.valueOf(getCount()));
	}

	public String getKey()
	{
		return 
			getDatabaseName() 
			+ "|" + getDatabaseName();
	}

}
