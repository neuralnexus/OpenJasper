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

package com.jaspersoft.jasperserver.export.service.impl;

import com.jaspersoft.jasperserver.api.common.util.spring.ApplicationContextProvider;
import com.jaspersoft.jasperserver.export.Exporter;
import com.jaspersoft.jasperserver.export.Importer;
import com.jaspersoft.jasperserver.export.ProfileAttributeServiceMock;
import com.jaspersoft.jasperserver.export.UserAuthorityServiceMock;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
    @author Zakhar.Tomchenco
*/
@ContextConfiguration(locations={"classpath:applicationContext-export-import-test.xml","classpath:applicationContext-export-import-web.xml"})
public class ImportExportServiceImplTest extends AbstractTestNGSpringContextTests {

    @Resource(name = "importExportService")
    ImportExportServiceImpl service;

    @Resource(name = "profileAttributeService")
    ProfileAttributeServiceMock profileAttributeService;

    @Resource(name = "userAuthorityService")
    UserAuthorityServiceMock authorityService;

	//needed for import/export beans using password decryption;
	// without this resource init, UserBean's importExportCipher is
	// not initialized correctly because StaticApplicationContext.getApplicationContext()
	// is null.
    @Resource(name = "applicationContextProvider")
	ApplicationContextProvider applicationContextProvider;

    private Set<Importer> importerInstances = Collections.synchronizedSet(new HashSet<Importer>());
    private Set<Exporter> exporterInstances = Collections.synchronizedSet(new HashSet<Exporter>());

    private Locale enUsLocale = new Locale("en,", "US");

    @Test(threadPoolSize = 20, invocationCount = 20, enabled = true)
    public void testInjectExporterPrototype1(){
        exporterInstances.add(service.getExporter());
    }

    @Test(dependsOnMethods = {"testInjectExporterPrototype1"}, enabled = true)
    public void testInjectExporterPrototype2(){
        assertEquals(exporterInstances.size(), 20);
    }

    @Test(threadPoolSize = 20, invocationCount = 20, enabled = true)
    public void testInjectImporterPrototype1(){
        importerInstances.add(service.getImporter());
    }

    @Test(dependsOnMethods = {"testInjectImporterPrototype1"}, enabled = true)
    public void testInjectImporterPrototype2(){
        assertEquals(importerInstances.size(), 20);
    }

    @Test(threadPoolSize = 10, invocationCount = 200, enabled = true)
    public void testExportRoles() throws Exception {
        Random r = new Random(new Date().getTime());

        List<String> expected = new ArrayList<String>();
        if (r.nextBoolean()) expected.add("ROLE_USER");
        if (r.nextBoolean()) expected.add("ROLE_DEMO");
        if (r.nextBoolean()) expected.add("ROLE_SUPERUSER");
        if (r.nextBoolean()) expected.add("ROLE_ADMINISTRATOR");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

//        try {
            service.doExport(outputStream, null, null, null, expected, null, enUsLocale);
//        }
//        catch (Exception e){
//            fail(e.getMessage());
//        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        ZipInputStream zip = new ZipInputStream(inputStream);

        Set<String> entries = new HashSet<String>();

        try {
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                entries.add(entry.getName());
                entry = zip.getNextEntry();
            }
        } catch (Exception er) {
            fail(er.getMessage());
        }

        if (expected.size() > 0) {
            assertEquals(entries.size(), expected.size() + 2, "Quantity of items is different");
        }
        else {
            assertEquals(entries.size(), 6, "Quantity of items is different"); // all roles were exported
        }
        assertTrue(entries.contains("roles/"));
        assertTrue(entries.contains("index.xml"));

        for (String s : expected) {
            if (!entries.contains("roles/" + s + ".xml"))
                fail("roles/" + s + ".xml not found");
        }
    }

    @Test(threadPoolSize = 20, invocationCount = 200, enabled = true)
    public void testExportRolesWithUsers() {
        List<String> expected = new ArrayList<String>();
        expected.add("ROLE_USER");
        expected.add("ROLE_DEMO");

        Map<String, Boolean> params = new HashMap<String, Boolean>();
        params.put("role-users", true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            service.doExport(outputStream, params, null, null, expected, null, enUsLocale);
        }
        catch (Exception e){
            fail(e.getMessage());
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        ZipInputStream zip = new ZipInputStream(inputStream);

        Set<String> entries = new HashSet<String>();

        try {
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                entries.add(entry.getName());
                entry = zip.getNextEntry();
            }
        } catch (Exception er) {
            fail(er.getMessage());
        }

        assertEquals(entries.size(), expected.size() + 4);
        assertTrue(entries.contains("roles/"));
        assertTrue(entries.contains("index.xml"));
        assertTrue(entries.contains("users/"));
        assertTrue(entries.contains("users/demo.xml"));

        for (String s : expected) {
            if (!entries.contains("roles/" + s + ".xml"))
                fail("roles/" + s + ".xml not found");
        }
    }

    @Test(enabled = true)
    public void testInputUsersAndRoles(){
        InputStream stream = this.getClass().getResourceAsStream("/testRolesAndUsers.zip");

        assertNull(authorityService.getUser(null, "superuser"));
        assertNull(authorityService.getRole(null, "ROLE_SUPERMART_MANAGER"));
        assertNull(authorityService.getRole(null, "ROLE_PORTLET"));
        assertNull(profileAttributeService.getProfileAttribute(null,null));

        try {
            service.doImport(stream,null, enUsLocale);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertNotNull(authorityService.getUser(null, "superuser"));
        assertNotNull(authorityService.getRole(null, "ROLE_SUPERMART_MANAGER"));
        assertNotNull(authorityService.getRole(null, "ROLE_PORTLET"));
        assertNotNull(profileAttributeService.getProfileAttribute(null,null));
    }

    @Test(enabled = true)
    public void tesJapaneseMessageForInvalidContentShouldBeLocalized(){
        InputStream stream = this.getClass().getResourceAsStream("/invalidContentStructure.zip");

        String key = "exception.remote.import.failed.content.error";
        Locale jaJpLocale = new Locale("ja");
        try {
            service.doImport(stream, null, jaJpLocale);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "インポートに失敗しました。理由：指定された zip ファイルが、有効な JasperReports Server エクスポートファイルではありません。");
        }
    }

    @Test(enabled = true)
    public void testSpanishMessageForInvalidContentShouldBeLocalized(){
        InputStream stream = this.getClass().getResourceAsStream("/invalidContentStructure.zip");

        String key = "exception.remote.import.failed.content.error";
        Locale esEsLocale = new Locale("es");
        try {
            service.doImport(stream, null, esEsLocale);
        } catch (Exception e) {
            assertEquals(e.getLocalizedMessage(), "Error de importación. Motivo: el archivo zip proporcionado no es un archivo de exportación de JasperReports Server válido.");
        }
    }

    @Test(enabled = true)
    public void testGermanMessageForInvalidZipShouldBeLocalized(){
        InputStream stream = this.getClass().getResourceAsStream("/invalidZipFile.zip");

        String key = "exception.remote.import.failed.zip.error";
        Locale esEsLocale = new Locale("de");
        try {
            service.doImport(stream, null, esEsLocale);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Der Import ist fehlgeschlagen. Grund: Die ZIP-Datei kann nicht gelesen werden.");
        }
    }

    @Test(enabled = true)
    public void testChineseMessageForInvalidZipShouldBeLocalized(){
        InputStream stream = this.getClass().getResourceAsStream("/invalidZipFile.zip");

        String key = "exception.remote.import.failed.zip.error";
        Locale esEsLocale = new Locale("zh", "CN");
        try {
            service.doImport(stream, null, esEsLocale);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "导入失败。原因: 无法读取 zip 文件。");
        }
    }
}
