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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.ServerInit;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.FakeWorkManager;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.SimpleMock;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TeiidEmbeddedServer;
import org.teiid.runtime.EmbeddedServer;
import javax.resource.spi.XATerminator;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: TeiidServerInitImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class TeiidServerInitImpl implements ServerInit {

    public void init(EmbeddedServer embeddedServer) {
        if (embeddedServer instanceof TeiidEmbeddedServer) {
            System.out.println("INIT - TEIID SERVER INIT IMPL");
            TeiidEmbeddedServer teiidEmbeddedServer = (TeiidEmbeddedServer) embeddedServer;
            teiidEmbeddedServer.setXaTerminator(SimpleMock.createSimpleMock(XATerminator.class));
    //        teiidEmbeddedServer.setWorkManager(new FakeWorkManager());
    //        teiidEmbeddedServer.setDetectTransactions(false);
        }
    }

}
