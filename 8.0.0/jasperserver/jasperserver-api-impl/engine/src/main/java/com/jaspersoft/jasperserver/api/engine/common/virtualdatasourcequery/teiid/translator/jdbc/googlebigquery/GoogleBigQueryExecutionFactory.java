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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.translator.jdbc.googlebigquery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.teiid.metadata.Column;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.Table;
import org.teiid.translator.MetadataProcessor;
import org.teiid.translator.Translator;
import org.teiid.translator.TranslatorException;
import org.teiid.translator.jdbc.JDBCExecutionFactory;
import org.teiid.translator.jdbc.JDBCMetdataProcessor;

@Translator(name="googlebigquery-jdbc", deprecated = "Google Big Query JDBC-compatible translator")
public class GoogleBigQueryExecutionFactory extends JDBCExecutionFactory {

  @Override
  public MetadataProcessor<Connection> getMetadataProcessor() {
    return new JDBCMetdataProcessor(){
      @Override
      protected Column addColumn(ResultSet columns, Table table, MetadataFactory metadataFactory,
          int rsColumns) throws SQLException {
        Column column = super.addColumn(columns, table, metadataFactory, rsColumns);

        return column;
      }
    };
  }

  @Override
  public String translateLiteralBoolean(Boolean booleanValue) {
    return booleanValue ? "true" : "false";
  }
}
