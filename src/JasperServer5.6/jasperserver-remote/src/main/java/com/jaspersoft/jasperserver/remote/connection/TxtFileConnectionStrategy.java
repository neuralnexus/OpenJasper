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

import com.jaspersoft.jasperserver.dto.connection.TxtFileConnection;
import com.jaspersoft.jasperserver.dto.connection.TxtFileDelimiterParser;
import com.jaspersoft.jasperserver.dto.connection.TxtFileParser;
import com.jaspersoft.jasperserver.dto.connection.TxtFileRegularExpressionParser;
import com.jaspersoft.jasperserver.dto.connection.metadata.TxtFileMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: TxtFileConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class TxtFileConnectionStrategy extends AbstractFileConnectionStrategy<TxtFileConnection> implements ConnectionMetadataBuilder<TxtFileConnection> {

    @Override
    protected TxtFileConnection cloneConnection(TxtFileConnection connectionDescription) {
        return new TxtFileConnection(connectionDescription);
    }

    @Override
    protected Object internalBuildMetadata(TxtFileConnection connection, InputStream stream) throws IOException {
        InputStreamReader isReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isReader);
        String line = reader.readLine();
        final TxtFileParser parser = connection.getParser();
        String delimiter;
        if(parser instanceof TxtFileDelimiterParser){
            delimiter = Pattern.quote(((TxtFileDelimiterParser) parser).getDelimiter());
        } else if(parser instanceof TxtFileRegularExpressionParser){
            delimiter = ((TxtFileRegularExpressionParser) parser).getRegularExpression();
        } else {
            // shouldn't happen
            throw new IllegalStateException("Unsupported parser type: " + parser.getClass().getName());
        }
        String[] tokens = line.split(delimiter);
        List<String> columns = new ArrayList<String>(tokens.length);
        for(int i = 0; i < tokens.length; i++){
            final String currentColumnName = connection.hasHeaderLine() ? tokens[i].trim() : buildColumnName (i);
            columns.add(currentColumnName);
        }
        return new TxtFileMetadata().setColumns(columns);
    }
}
