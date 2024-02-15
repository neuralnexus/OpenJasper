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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.dto.connection.XlsFileConnection;
import com.jaspersoft.jasperserver.dto.connection.metadata.XlsFileMetadata;
import com.jaspersoft.jasperserver.dto.connection.metadata.XlsSheet;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: XlsFileConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class XlsFileConnectionStrategy extends AbstractFileConnectionStrategy<XlsFileConnection> {
    @Override
    protected XlsFileConnection cloneConnection(XlsFileConnection connectionDescription) {
        return new XlsFileConnection(connectionDescription);
    }

    @Override
    protected Object internalBuildMetadata(XlsFileConnection connection, InputStream inputStream) throws IOException {
        List<XlsSheet> sheets = new ArrayList<XlsSheet>();
        try {
            final Workbook workbook = Workbook.getWorkbook(inputStream);
            for (Sheet currentSheet : workbook.getSheets()) {
                List<String> columns = new ArrayList<String>();
                final XlsSheet clientSheet = new XlsSheet().setName(currentSheet.getName()).setColumns(columns);
                sheets.add(clientSheet);
                for (int i = 0; i < currentSheet.getColumns(); i++) {
                    if (connection.hasHeaderLine()) {
                        columns.add(currentSheet.getColumn(i)[0].getContents());
                    } else {
                        columns.add(buildColumnName (i));
                    }
                }
            }
        } catch (BiffException e) {
            throw new IOException("Unable to parse input stream to a workbook", e);
        }
        return new XlsFileMetadata().setSheets(sheets);
    }
}
