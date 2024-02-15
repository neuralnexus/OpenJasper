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
package com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import javax.resource.spi.XATerminator;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
@JasperServerAPI
public class ServerConfig {

    String transactionManagerJNDILookup;
    ServerInit serverInit;
    XATerminator xaTerminator;

    /*
	 * Set Transaction Manager Lookup for JNDI
	 * eg, java:/TransactionManager, java:/comp/env/TransactionManager, java:jboss/TransactionManager
	 */
    public String getTransactionManagerJNDILookup() {
        return transactionManagerJNDILookup;
    }

    /*
	 * Get Transaction Manager Lookup for JNDI
	 * eg, java:/TransactionManager, java:/comp/env/TransactionManager, java:jboss/TransactionManager
	 */
    public void setTransactionManagerJNDILookup(String transactionManagerJNDILookup) {
        this.transactionManagerJNDILookup = transactionManagerJNDILookup;
    }

    public ServerInit getServerInit() {
        return serverInit;
    }

    public void setServerInit(ServerInit serverInit) {
        this.serverInit = serverInit;
    }

    public XATerminator getXaTerminator() {
        return xaTerminator;
    }

    public void setXaTerminator(XATerminator xaTerminator) {
        this.xaTerminator = xaTerminator;
    }
}
