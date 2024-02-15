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


/**
 * @version: $Id: bundlePluginMock.js 47331 2014-07-18 09:13:06Z kklein $
 */

define({
    load: function (name, req, onload, config) {
        onload({

			// ========================================================================
			// Next goes keys for Data Source module
			"input.password.substitution": "~value~subst~",
			"resource.datasource.bean.page.title.add": "Add Data Source",
			"resource.datasource.bean.page.title.new": "New Data Source",
			"resource.datasource.bean.page.title.edit": "Edit Data Source",
			"resource.datasource.jdbc.page.title.add": "Add Data Source",
			"resource.datasource.jdbc.page.title.new": "New Data Source",
			"resource.datasource.jdbc.page.title.edit": "Edit Data Source",
			"resource.datasource.jndi.page.title.add": "Add Data Source",
			"resource.datasource.jndi.page.title.new": "New Data Source",
			"resource.datasource.jndi.page.title.edit": "Edit Data Source",
			"resource.datasource.aws.page.title.add": "Add Data Source",
			"resource.datasource.aws.page.title.new": "New Data Source",
			"resource.datasource.aws.page.title.edit": "Edit Data Source",
			"resource.datasource.custom.page.title.add": "Add Data Source",
			"resource.datasource.custom.page.title.new": "New Data Source",
			"resource.datasource.custom.page.title.edit": "Edit Data Source",
			"resource.datasource.virtual.page.title.add": "Add Data Source",
			"resource.datasource.virtual.page.title.new": "New Data Source",
			"resource.datasource.virtual.page.title.edit": "Edit Data Source",
			"resource.datasource.hive.page.title.add": "Add Data Source",
			"resource.datasource.hive.page.title.new": "New Data Source",
			"resource.datasource.hive.page.title.edit": "Edit Data Source",
			"resource.datasource.mongo.page.title.add": "Add Data Source",
			"resource.datasource.mongo.page.title.new": "New Data Source",
			"resource.datasource.mongo.page.title.edit": "Edit Data Source",
			"resource.dataSource.dstypeAws": "AWS Data Source",
			"resource.dataSource.dstypeBean": "Bean Data Source",
			"resource.dataSource.dstypeJDBC": "JDBC Data Source",
			"resource.dataSource.dstypeJNDI": "JNDI Data Source",
			"resource.dataSource.dstypeVirtual": "Virtual Data Source",
			"myCustomDataSource.name": "My Custom Data Source",
			"hibernateDataSource.name": "Hibernate Data Source",
			"MongoDbDataSource.name": "MongoDB Data Source",
			"diagnosticCustomDataSource.name": "Internal Diagnostic Data Source",
			"webScraperDataSource.name": "Web Scraper Data Source",
			"HiveDataSource.name": "Hadoop-Hive Data Source",
			"jsp.home.content_title": "Jaspersoft",
			"resource.dataSource.jdbc.driver": "JDBC Driver",
			"resource.dataSource.jdbc.selectDriverTitle": "Select driver",
			"resource.dataSource.jdbc.driverMissing": "NOT INSTALLED:",
			"resource.dataSource.jdbc.otherDriver": "Other...",
			"resource.dataSource.jdbc.upload.addDriverButton": "Add Driver...",
			"resource.dataSource.jdbc.upload.editDriverButton": "Edit Driver...",
			"resource.dataSource.jdbc.upload.driverUploadSuccess": "Driver successfully uploaded and registered",
			"resource.dataSource.jdbc.upload.driverUploadFail": "Driver registration failed. Cancel or select another driver.",
			"resource.dataSource.jdbc.upload.overwriteWarning": "Driver already exists. Upload to overwrite with selected driver.",
			"resource.dataSource.jdbc.upload.wrongExtension": "The file type is invalid for this use. Choose a JAR file.",
			"resource.dataSource.jdbc.hint1": "Hint: org.postgresql.Driver",
			"resource.dataSource.jdbc.url": "URL",
			"resource.dataSource.jdbc.hint2": "Hint: jdbc:postgresql://localhost:5432/mydb",
			"resource.dataSource.jdbc.username": "User Name",
			"resource.dataSource.jdbc.password": "Password",
			"resource.dataSource.jdbc.dbHost": "Host",
			"resource.dataSource.jdbc.dbPort": "Port",
			"resource.dataSource.jdbc.dbName": "Database",
			"resource.dataSource.jdbc.sName": "Service",
			"resource.dataSource.jdbc.schemaName": "Schema Name",
			"resource.dataSource.jdbc.driverType": "Driver Type",
			"resource.dataSource.jdbc.informixServerName": "Informix Server Name",
			"resource.dataSource.jdbc.fieldCantBeEmpty": "{0} field can't be empty.",
			"resource.dataSource.jdbc.invalidField": "{0} has invalid value.",
			"resource.dataSource.jdbc.classNotFound": "The required driver class ({0}) is not found in uploaded files.",
			"resource.dataSource.jdbc.requiredTitle": "Required {0}",
			"resource.dataSource.aws.settings.title": "AWS Settings",
			"resource.dataSource.aws.option.useDefault": "Use EC2 instance credentials.",
			"resource.dataSource.aws.option.useDefault.title": "Use the EC2 instance credentials provided by the IAM Role. Normally this option should be used.",
			"resource.dataSource.aws.option.userDefined": "Use AWS Credentials ().",
			"resource.dataSource.aws.option.userDefined.title": "Use this option if no IAM Role was assigned when the instance was launched. When generating credentials, copy the keys from the 'Outputs' tab after the CloudFormation Stack completes.",
			"resource.dataSource.aws.option.url": "http://www.jaspersoft.com/jrs-create-user",
			"resource.dataSource.aws.setting.accessKey": "AWS Access Key",
			"resource.dataSource.aws.setting.accessKey.title": "Copy the AccessKey value from the 'Outputs' tab after the 'Generate credentials' CloudFormation Stack completes.",
			"resource.dataSource.aws.setting.secretKey": "AWS Secret Key",
			"resource.dataSource.aws.setting.secretKey.title": "Copy the SecretKey value from the 'Outputs' tab after the 'Generate credentials' CloudFormation Stack completes.",
			"resource.dataSource.aws.setting.arn": "ARN",
			"resource.dataSource.aws.setting.arn.title": "Global Amazon Resource Names (ARNs) are needed for cross-account access. Most users should leave this blank.",
			"resource.dataSource.aws.optional.arn": "(Optional) Use for cross-account IAM access.",
			"resource.dataSource.aws.setting.region": "AWS Region",
			"resource.dataSource.aws.setting.region.title": "AWS Region",
			"resource.dataSource.aws.tree": "Select an AWS Data Source",
			"resource.dataSource.aws.updateTree": "Find My AWS Data Sources",
			"resource.dataSource.aws.database.name": "Database Name",
			"resource.dataSource.aws.database.name.title": "Many servers have multiple databases. Specify the database to use.",
			"resource.dataSource.aws.access.denied": "[credentials provided cannot access {0}]",
			"resource.dataSource.aws.empty": "[no {0} data sources found]",
			"resource.dataSource.aws.unknown.host": "[unknown host {0}]",
			"ReportDataSourceValidator.error.not.empty.reportDataSource.parentFolderIsEmpty": "Specify a folder to save the data source",
			"ReportDataSourceValidator.error.folder.not.found": "The folder {0} cannot be found. Please choose another location.",
			"ReportDataSourceValidator.error.not.empty.reportDataSource.driverNotInstalled": "Driver is not installed",
			"fillParameters.error.mandatoryField": "This field is mandatory so you must enter data.",
			"ReportDataSourceValidator.error.sub.datasources.id.duplicates": "Two or more data sources have this ID: {0}. Data source IDs must be unique within a virtual data source. Choose a different ID.",
            "timezone.option":"{0} - {1}"
		});
    }
});