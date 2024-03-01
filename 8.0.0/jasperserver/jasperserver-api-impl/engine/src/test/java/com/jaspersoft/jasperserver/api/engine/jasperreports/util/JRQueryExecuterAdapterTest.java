/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import org.junit.Test;

import static com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRQueryExecuterAdapter.JASPER_QL;
import static org.mockito.Mockito.*;

public class JRQueryExecuterAdapterTest {
    @Test
    public void setExecuterRefereneceDS_checkIfTypeCastHappens() throws JRException {
        Query query = mock(Query.class);
        ReportDataSource dataSource = mock(ReportDataSource.class);
        JRQueryExecuter executer = spy(new ExecutorTest());
        when(query.getLanguage()).thenReturn(JASPER_QL);
        JRQueryExecuterAdapter.setExecuterRefereneceDS(query, dataSource, executer);
        verify((JRJaperQLExecuter)executer, times(1)).setDataSource(any(ReportDataSource.class));
    }


    class ExecutorTest implements JRJaperQLExecuter, JRQueryExecuter {
        @Override
        public void setDataSource(Resource dataSource) {
            //nothing
        }

        @Override
        public void close() {

        }

        public boolean cancelQuery() throws JRException {
            return false;
        }

        @Override
        public JRDataSource createDatasource() throws JRException {
            return null;
        }

        @Override
        public Resource getDataSource() {
            return null;
        }
    }
}
