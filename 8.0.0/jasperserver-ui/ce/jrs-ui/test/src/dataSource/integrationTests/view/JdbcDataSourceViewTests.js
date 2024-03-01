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

import sinon from 'sinon';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import resourceLocator from 'src/resource/resource.locate';
import dataSourceConfig from '../../test/mock/dataSourceConfigMock'
import JdbcDataSourceView from 'src/dataSource/view/JdbcDataSourceView';

describe('Testing JdbcDataSourceView', function () {
    var jdbcDataSourceView, fakeServer, root, clock;
    beforeEach(function () {
        jrsConfigs.addDataSource = dataSourceConfig;
        sinon.stub(resourceLocator, 'initialize');
    });
    afterEach(function () {
        delete jrsConfigs.addDataSource;
        resourceLocator.initialize.restore();
    });
    it('JdbcDataSourceView should be defined', function () {
        expect(JdbcDataSourceView).toBeDefined();
        expect(typeof JdbcDataSourceView).toEqual("function");
    });    /* TEMPORARY HACK
		describe("JdbcDataSourceView's work", function() {

			beforeEach(function(){

				dataSourceTestingHelper.beforeEach();

				// create an variable which holds the DS in the DOM object
				root = $("[name=dataSourceTestArea]");

				// prepare fake server
				fakeServer = sinon.fakeServer.create();

				// prepare the response
				fakeServer.respondWith(
					"GET", jrsConfigs.contextPath + "/rest_v2/jdbcDrivers",
					[200, { "Content-Type": "application/json" }, JSON.stringify(
                        {"jdbcDrivers":
						    [
							    {"name":"mysql","label":"MySQL","available":true,"jdbcUrl":"jdbc:mysql://$[dbHost]:$[dbPort]/$[dbName]","jdbcDriverClass":"org.mariadb.jdbc.Driver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"3306"},{"key":"dbName","value":"dbname"}]},
							    {"name":"mysql_oracle","label":"MySQL","available":false,"jdbcUrl":"jdbc:mysql://$[dbHost]:$[dbPort]/$[dbName]","jdbcDriverClass":"com.mysql.jdbc.Driver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"3306"},{"key":"dbName","value":"dbname"}]},
							    {"name":"postgresql","label":"PostgreSQL","available":true,"jdbcUrl":"jdbc:postgresql://$[dbHost]:$[dbPort]/$[dbName]","jdbcDriverClass":"org.postgresql.Driver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"5432"},{"key":"dbName","value":"dbname"}],"isDefault":true},
							    {"name":"ingres","label":"Ingres","available":true,"jdbcUrl":"jdbc:ingres://$[dbHost]:$[dbPort]/$[dbName]","jdbcDriverClass":"com.ingres.jdbc.IngresDriver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"117"},{"key":"dbName","value":"dbname"}]},
							    {"name":"oracle","label":"Oracle","available":true,"jdbcUrl":"jdbc:oracle:thin:@$[dbHost]:$[dbPort]:$[sName]","jdbcDriverClass":"oracle.jdbc.OracleDriver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"1521"},{"key":"sName","value":"orcl"}]},
							    {"name":"sqlserver","label":"MS SQL Server 2005","available":true,"jdbcUrl":"jdbc:sqlserver://$[dbHost]:$[dbPort];databaseName=$[dbName]","jdbcDriverClass":"com.microsoft.sqlserver.jdbc.SQLServerDriver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"1433"},{"key":"dbName","value":"dbname"}]},
							    {"name":"sqlserver2000","label":"MS SQL Server 2000","available":false,"jdbcUrl":"jdbc:microsoft:sqlserver://$[dbHost]:$[dbPort];DatabaseName=$[dbName]","jdbcDriverClass":"com.microsoft.jdbc.sqlserver.SQLServerDriver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"1433"},{"key":"dbName","value":"dbname"}]},
							    {"name":"db2","label":"IBM DB2","available":true,"jdbcUrl":"jdbc:db2://$[dbHost]:$[dbPort]/$[dbName]:driverType=$[driverType];currentSchema=$[schemaName];","jdbcDriverClass":"com.ibm.db2.jcc.DB2Driver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"50000"},{"key":"driverType","value":"4"},{"key":"dbName","value":"dbname"},{"key":"schemaName","value":"schemaname"}]},
							    {"name":"vertica","label":"Vertica","available":true,"jdbcUrl":"jdbc:vertica://$[dbHost]:$[dbPort]/$[dbName]","jdbcDriverClass":"com.vertica.Driver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"5433"},{"key":"dbName","value":"dbname"}]},
							    {"name":"informix","label":"Informix","available":false,"jdbcUrl":"jdbc:informix-sqli://$[dbHost]:$[dbPort]/$[dbName]:INFORMIXSERVER=$[informixServerName]","jdbcDriverClass":"com.informix.jdbc.IfxDriver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"1526"},{"key":"dbName","value":"dbname"},{"key":"informixServerName","value":"informixServerName"}]},
							    {"name":"SYBASE","label":"Sybase jConnect","available":false,"jdbcUrl":"jdbc:sybase:Tds:$[dbHost]:$[dbPort]?ServiceName=$[sName]","jdbcDriverClass":"com.sybase.jdbc4.jdbc.SybDriver","defaultValues":[{"key":"dbHost","value":"localhost"},{"key":"dbPort","value":"5433"},{"key":"sName","value":"serviceName"}]}
					    	],
                            "_links":{"create":{"profile":"POST","relation":"create"},"edit":{"profile":"PUT","relation":"edit"}},"_embedded":{}
                        }
					)]);

				// init the data source
				jdbcDataSourceView = new JdbcDataSourceView(
					_.extend(jrsConfigs.addDataSource.initOptions, {
						dataSourceType: "jdbcdatasource",
						dataSource: undefined,
						el: root
					})
				);

				// respond to data source
				fakeServer.respond();

				jdbcDataSourceView.render();
			});

			afterEach(function(){

				// remove the data source from the page
				jdbcDataSourceView.remove();

				// clear the testable area
				root.empty();

				// destroy fake XHR service
				fakeServer.restore();

				dataSourceTestingHelper.afterEach();
			});

			it("Check if all fields are visible", function() {

				// check data source specific fields
				expect(root.find("[name=selectedDriverClass]")).toBeVisible();
				expect(root.find("[name=dbHost]")).toBeVisible();
				expect(root.find("[name=dbPort]")).toBeVisible();
				expect(root.find("[name=dbName]")).toBeVisible();
				expect(root.find("[name=connectionUrl]")).toBeVisible();
				expect(root.find("[name=username]")).toBeVisible();
				expect(root.find("[name=password]")).toBeVisible();

				// check timezone
				expect(root.find("[name=timezone]")).toBeVisible();

				// check test connection button
				expect(root.find("#testDataSource").length).toBe(1);
			});

			it("Check if everything has proper default value", function() {

				// check page title
				expect(document.title).toBe(i18n["jsp.home.content_title"] + ": " + i18n["resource.datasource.jdbc.page.title.new"]);

				// check data source specific fields
				expect(root.find("[name=selectedDriverClass]").val()).toBe("org.postgresql.Driver");
				expect(root.find("[name=dbHost]").val()).toBe("localhost");
				expect(root.find("[name=dbPort]").val()).toBe("5432");
				expect(root.find("[name=dbName]").val()).toBe("dbname");
				expect(root.find("[name=connectionUrl]").val()).toBe("jdbc:postgresql://localhost:5432/dbname");
				expect(root.find("[name=username]").val()).toBe("");
				expect(root.find("[name=password]").val()).toBe("");

				// check what timezone is present and has proper value
				expect(root.find("[name=timezone]").val()).toBe("");
			});

			it("Checking field validation", function() {

				// we need to test specific fields -- we'll remove the value and we'll see if the
				// validation will trigger
				expect(root.find("[name=dbHost]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
				expect(root.find("[name=dbPort]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
				expect(root.find("[name=dbName]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
				expect(root.find("[name=connectionUrl]").val("").trigger("change").parent().hasClass("error")).toBeTruthy();
				expect(root.find("[name=username]").val("").trigger("change").parent().hasClass("error")).toBeFalsy();
				expect(root.find("[name=password]").val("").trigger("change").parent().hasClass("error")).toBeFalsy();
			});

			it("Checking if specific fields of JDBC are getting re-rendered after selecting different JDBC driver", function() {

				// we are going to change the driver to IBM DB2
				// this driver has an extra fields, so by existing of these fields we can assume everything works fine

				// check these fields -- they should not be presented
				expect(root.find("[name=driverType]").length).toBe(0);
				expect(root.find("[name=schemaName]").length).toBe(0);

				// change the driver to com.ibm.db2.jcc.DB2Driver
				root.find("[name=selectedDriverClass]").val("com.ibm.db2.jcc.DB2Driver").trigger("change");

				// now, see if they have appeared
				expect(root.find("[name=driverType]")).toBeVisible();
				expect(root.find("[name=schemaName]")).toBeVisible();
			});

			describe("Checking Add/Edit Driver button", function() {

				it("Checking button state on different driver selections", function() {

					var driversAndExpectedState = {
						"org.mariadb.jdbc.Driver" : "edit",
						"com.mysql.jdbc.Driver" : "add",
						"org.postgresql.Driver" : "edit",
						"com.ingres.jdbc.IngresDriver" : "edit",
						"oracle.jdbc.OracleDriver" : "edit",
						"com.microsoft.sqlserver.jdbc.SQLServerDriver" : "edit",
						"com.microsoft.jdbc.sqlserver.SQLServerDriver" : "add",
						"com.ibm.db2.jcc.DB2Driver" : "edit",
						"com.vertica.Driver" : "edit",
						"com.informix.jdbc.IfxDriver" : "add",
						"com.sybase.jdbc4.jdbc.SybDriver" : "add"
					};

					// now, check what all of these drivers changes button state and makes the buttom enabled.
					_.each(driversAndExpectedState, function(state, driver) {
						root.find("[name=selectedDriverClass]").val(driver).trigger("change");
						expect(root.find("#driverUploadButton span").text()).toBe(state === "add" ? i18n["resource.dataSource.jdbc.upload.addDriverButton"] : i18n["resource.dataSource.jdbc.upload.editDriverButton"]);
						expect(root.find("#driverUploadButton").attr("disabled")).toBe(undefined);
					});

					// now, check what select has "other driver" option, and if we choose it, it makes the button disabled
					root.find("[name=selectedDriverClass]").val("other").trigger("change");
					expect(root.find("#driverUploadButton span").text()).toBe(i18n["resource.dataSource.jdbc.upload.addDriverButton"]);
					expect(root.find("#driverUploadButton").attr("disabled")).toBe("disabled");
				});

				it("Checking if Add/Edit Driver dialog works", function() {

					var dialog;

					// click on button and see if dialog appears
					// but because this dialogs appears after 500ms, we need to emulate timer

					clock = sinon.useFakeTimers();

					// check if dialog is not yet appeared
					dialog = $("#select");
					expect(dialog).not.toBeVisible();

					// now, click
					root.find("#driverUploadButton").click();

					// increase time
					clock.tick(1000);

					// OK, now we can expect what dialog has appeared. Check it !
					// grab dialog once again because it should be accessible not
					dialog = $("#select");
					expect(dialog).toBeVisible();

					// ok, close the dialog (click on Cancel)
					$(dialog.find("button")[1]).trigger("click");

					// check if dialog is absent
					expect(dialog).not.toBeVisible();

					// and restore timer
					clock.restore();
				});
			});
		});
        */
});