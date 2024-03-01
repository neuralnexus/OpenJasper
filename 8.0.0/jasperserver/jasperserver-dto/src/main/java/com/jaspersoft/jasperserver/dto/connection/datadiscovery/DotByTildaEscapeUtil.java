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
package com.jaspersoft.jasperserver.dto.connection.datadiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DotByTildaEscapeUtil {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("(?<!~)(?:~~)*\\.");
    private static final Pattern VALIDATE_PATTERN = Pattern.compile("(?<!~)(?:~~)*~(?!(~|\\.))");
    public static final String ESCAPE_CHARACTER = "~";
    public static final String DELIMITER_CHARACTER = ".";
    public static final String DOUBLE_ESCAPE_CHARACTER = ESCAPE_CHARACTER + ESCAPE_CHARACTER;
    public static final String ESCAPED_DELIMITER_CHARACTER = ESCAPE_CHARACTER + DELIMITER_CHARACTER;

    public String escapeDot(String toEscape){
        String result = toEscape;
        if (result.contains(ESCAPE_CHARACTER)) {
            result = result.replace(ESCAPE_CHARACTER, DOUBLE_ESCAPE_CHARACTER);
        }
        if (result.contains(DELIMITER_CHARACTER)) {
            result = result.replace(DELIMITER_CHARACTER, ESCAPED_DELIMITER_CHARACTER);
        }
        return result;
    }
    
    public String unEscapeDot(String toUnEscape){
        return unEscapeDot(toUnEscape, true);
    }

    protected String unEscapeDot(String toUnEscape, boolean validate){
        if(validate) validateBeforeUnEscape(toUnEscape);
        String result = toUnEscape;
        if (result.contains(DOUBLE_ESCAPE_CHARACTER)) {
            result = result.replace(DOUBLE_ESCAPE_CHARACTER, ESCAPE_CHARACTER);
        }
        if (result.contains(ESCAPED_DELIMITER_CHARACTER)) {
            result = result.replace(ESCAPED_DELIMITER_CHARACTER, DELIMITER_CHARACTER);
        }
        return result;
    }
    
    public String[] splitByDotUnEscapeTokens(String dotQualifiedString){
        validateBeforeUnEscape(dotQualifiedString);
        if(dotQualifiedString == null) return null;
        final Matcher matcher = SPLIT_PATTERN.matcher(dotQualifiedString);
        int start = 0;
        List<String> tokens = new ArrayList<String>();
        while (matcher.find()){
            final int end = matcher.end();
            tokens.add(dotQualifiedString.substring(start, end - 1));
            start = end;
        }
        tokens.add(dotQualifiedString.substring(start, dotQualifiedString.length()));
        final String[] split = tokens.toArray(new String[tokens.size()]);
        for(int i = 0; i < split.length; i++){
            split[i] = unEscapeDot(split[i], false);
        }
        return split;
    }

    protected void validateBeforeUnEscape(String string){
        if(string.contains(".") || string.contains("~")) {
            List<Integer> indexes = new ArrayList<Integer>();
            final Matcher matcher = VALIDATE_PATTERN.matcher(string);
            while (matcher.find()) {
                indexes.add(matcher.end() - 1);
            }
            if (!indexes.isEmpty()) {
                throw new UnexpectedEscapeCharacterException(string, indexes);
            }
        }
    }
    
    public String toDotQualifiedString(String... tokens){
        if(tokens == null || tokens.length == 0) return null;
        StringBuilder builder = new StringBuilder();
        for (String token : tokens) {
            if(builder.length() != 0){
                builder.append(DELIMITER_CHARACTER);
            }
            builder.append(escapeDot(token));
        }
        return builder.toString();
    }
}
