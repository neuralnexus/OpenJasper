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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignField;
import org.junit.Test;

import java.util.List;

import static example.cdspro.CustomDomainMetadataUtils.createCustomDomainMetaData;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class CustomDomainMetadataUtilsTest {

    @Test
    public void testCreateCustomDomainMetaData() {
        final CustomDomainMetaData metaData = createCustomDomainMetaData("MongoDbQuery",
                asList(
                        createJRField("f1", "field 1")
                , createJRField("f1.s1", "field 2")));

        assertNotNull(metaData);
        assertEquals(2, metaData.getJRFieldList().size());
        assertEquals(2, metaData.getFieldMapping().size());
        assertNull( metaData.getQueryText());
    }

    private JRField createJRField(String label, String description) {
        JRDesignField f = new JRDesignField();
        f.setName(label);
        f.setDescription(description);
        f.setValueClassName(String.class.getName());
        return f;
    }

}