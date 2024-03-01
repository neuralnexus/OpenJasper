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
package com.jaspersoft.jasperserver.jaxrs.report;

/**
 * @author Sanda Zaharia
 */

public interface ResponseExample {
	
	static final String REPORT_RUN_EXPORT_OUTPUT = "Response body:\n\n" +
			"Contains the PDF file to be downloaded\n\n" + 
			"Response headers:\n\n" +
			"content-disposition: attachment; filename=\"AllAccounts.pdf\"\n" +
			"content-type: application/pdf\n" +
			"output-final: true ";
	
	static final String REPORT_EXECUTION_STATUS_JSON = "{\n" + 
			"    \"value\": \"failed\",\n" + 
			"    \"errorDescriptor\": {\n" + 
			"        \"message\": \"Input controls validation failure\",\n" + 
			"        \"errorCode\": \"input.controls.validation.error\",\n" + 
			"        \"parameters\": [\"Specify a valid value for type Integer.\"]\n" + 
			"    }\n" + 
			"}";
	
	static final String REPORT_EXECUTION_STATUS_XML = "<status>\n" + 
			"    <errorDescriptor>\n" + 
			"        <errorCode>input.controls.validation.error</errorCode>\n" + 
			"        <message>Input controls validation failure</message>\n" + 
			"        <parameters>\n" + 
			"            <parameter>Specify a valid value for type Integer.\n" + 
			"            </parameter>\n" + 
			"        </parameters>\n" + 
			"    </errorDescriptor>\n" + 
			"    <value>failed</value>\n" + 
			"</status>";

	static final String REPORT_EXECUTION_INFO_JSON = "{\n" +
			"  \"bookmarks\": {\n" +
			"    \"id\": \"bkmrk_1058907116\",\n" +
			"    \"type\": \"bookmarks\",\n" +
			"    \"bookmarks\": [\n" +
			"      {\n" +
			"        \"label\": \"USA shipments\",\n" +
			"        \"pageIndex\": 22,\n" +
			"        \"elementAddress\": \"0\",\n" +
			"        \"bookmarks\": [\n" +
			"          {\n" +
			"            \"label\": \"Albuquerque\",\n" +
			"            \"pageIndex\": 22,\n" +
			"            \"elementAddress\": \"4\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Anchorage\",\n" +
			"            \"pageIndex\": 22,\n" +
			"            \"elementAddress\": \"116\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Boise\",\n" +
			"            \"pageIndex\": 23,\n" +
			"            \"elementAddress\": \"33\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Butte\",\n" +
			"            \"pageIndex\": 23,\n" +
			"            \"elementAddress\": \"223\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Elgin\",\n" +
			"            \"pageIndex\": 23,\n" +
			"            \"elementAddress\": \"245\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Eugene\",\n" +
			"            \"pageIndex\": 23,\n" +
			"            \"elementAddress\": \"279\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Kirkland\",\n" +
			"            \"pageIndex\": 24,\n" +
			"            \"elementAddress\": \"57\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Lander\",\n" +
			"            \"pageIndex\": 24,\n" +
			"            \"elementAddress\": \"79\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Portland\",\n" +
			"            \"pageIndex\": 24,\n" +
			"            \"elementAddress\": \"137\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"San Francisco\",\n" +
			"            \"pageIndex\": 24,\n" +
			"            \"elementAddress\": \"213\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Seattle\",\n" +
			"            \"pageIndex\": 24,\n" +
			"            \"elementAddress\": \"241\",\n" +
			"            \"bookmarks\": null\n" +
			"          },\n" +
			"          {\n" +
			"            \"label\": \"Walla Walla\",\n" +
			"            \"pageIndex\": 25,\n" +
			"            \"elementAddress\": \"45\",\n" +
			"            \"bookmarks\": null\n" +
			"          }\n" +
			"        ]\n" +
			"      }\n" +
			"    ]\n" +
			"  },\n" +
			"  \"parts\": {\n" +
			"    \"id\": \"parts_533304192\",\n" +
			"    \"type\": \"reportparts\",\n" +
			"    \"parts\": [\n" +
			"      {\n" +
			"        \"idx\": 0,\n" +
			"        \"name\": \"Table of Contents\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"idx\": 3,\n" +
			"        \"name\": \"Overview\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"idx\": 22,\n" +
			"        \"name\": \"USA shipments\"\n" +
			"      }\n" +
			"    ]\n" +
			"  }\n" +
			"}";

	static final String REPORT_EXECUTION_INFO_XML = "<reportInfo>\n" +
			"    <bookmarks>\n" +
			"        <bookmarks>\n" +
			"            <bookmark>\n" +
			"                <elementAddress>0</elementAddress>\n" +
			"                <label>Distribution by Country</label>\n" +
			"                <pageIndex>3</pageIndex>\n" +
			"            </bookmark>\n" +
			"            <bookmark>\n" +
			"                <bookmarks>\n" +
			"                    <bookmark>\n" +
			"                        <bookmarks>\n" +
			"                            <bookmark>\n" +
			"                                <elementAddress>1__5_0</elementAddress>\n" +
			"                                <label>Burnaby</label>\n" +
			"                                <pageIndex>9</pageIndex>\n" +
			"                            </bookmark>\n" +
			"                            <bookmark>\n" +
			"                                <elementAddress>0__64_0</elementAddress>\n" +
			"                                <label>Cliffside</label>\n" +
			"                                <pageIndex>11</pageIndex>\n" +
			"                            </bookmark>\n" +
			"                        </bookmarks>\n" +
			"                        <elementAddress>1__4_0</elementAddress>\n" +
			"                        <label>Canada</label>\n" +
			"                        <pageIndex>9</pageIndex>\n" +
			"                    </bookmark>\n" +
			"                </bookmarks>\n" +
			"                <elementAddress>0</elementAddress>\n" +
			"                <label>Customers List</label>\n" +
			"                <pageIndex>9</pageIndex>\n" +
			"            </bookmark>\n" +
			"        </bookmarks>\n" +
			"        <id>bkmrk_1991387153</id>\n" +
			"        <type>bookmarks</type>\n" +
			"    </bookmarks>\n" +
			"    <parts>\n" +
			"        <id>parts_756330337</id>\n" +
			"        <parts>\n" +
			"            <part>\n" +
			"                <idx>0</idx>\n" +
			"                <name>TocReport</name>\n" +
			"            </part>\n" +
			"            <part>\n" +
			"                <idx>3</idx>\n" +
			"                <name>Chart</name>\n" +
			"            </part>\n" +
			"        </parts>\n" +
			"        <type>reportparts</type>\n" +
			"    </parts>\n" +
			"</reportInfo>";

	static final String REPORT_EXECUTION_PAGE_STATUS = "{\n" +
			"  \"reportStatus\": \"ready\",\n" +
			"  \"pageTimestamp\": \"0\",\n" +
			"  \"pageFinal\": \"true\"\n" +
			"}";
	
	static final String CANCEL_REPORT_EXECUTION_JSON = "{\n  \"value\": \"cancelled\"\n}";
	
	static final String CANCEL_REPORT_EXECUTION_XML = "<status>cancelled</status>"; 

		
}
