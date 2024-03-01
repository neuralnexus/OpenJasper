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

package example.cdspro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import net.sf.jasperreports.engine.JRField;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class JsonDataSourceUtilsTest {

    @Test
    public void createField() {
        List<JRField> acc = new ArrayList<JRField>();
        Map.Entry<String, JsonNode> json = new AbstractMap.SimpleEntry<>("f1", BooleanNode.getTrue());
        final ArrayList<String> parent = new ArrayList<>();
        JsonDataSourceUtils.createField(acc, parent, json);

        assertEquals(1, acc.size());

    }

}