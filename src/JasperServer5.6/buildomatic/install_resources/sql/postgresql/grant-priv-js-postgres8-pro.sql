-- sql script to set privileges on each table and sequence
-- Required for PostgreSQL 8
--

GRANT ALL PRIVILEGES ON SEQUENCE hibernate_sequence TO jasperdb;
 
GRANT ALL PRIVILEGES ON TABLE jiaccessevent TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocchartmeasure TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhoccolumn TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocdataview TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocdataviewinputcontrol TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocdataviewresource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocgroup TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocreportunit TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocstate TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocstateproperty TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhoctablesortfield TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocxtabcolumngroup TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocxtabmeasure TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiadhocxtabrowgroup TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiauditevent TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiauditeventarchive TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiauditeventproperty TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiauditeventpropertyarchive TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jibeandatasource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jicontentresource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jicustomdatasource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jicustomdatasourceproperty TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidashboard TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidashboardframeproperty TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidashboardresource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidatadefinerunit TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidatasnapshot TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidatasnapshotcontents TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidatasnapshotparameter TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidatatype TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidomaindatasource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidomaindatasourcebundle TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jidomaindatasourcedsref TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jifileresource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiftpinfoproperties TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiinputcontrol TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiinputcontrolquerycolumn TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jijdbcdatasource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jijndijdbcdatasource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jilistofvalues TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jilistofvaluesitem TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jilogevent TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jimondrianconnection TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jimondrianconnectiongrant TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jimondrianxmladefinition TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiobjectpermission TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiolapclientconnection TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiolapunit TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiprofileattribute TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiquery TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportalerttoaddress TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjob TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobalert TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobcalendartrigger TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobmail TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobmailrecipient TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjoboutputformat TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobparameter TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobrepodest TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobsimpletrigger TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportjobtrigger TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportoptions TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportoptionsinput TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportunit TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportunitinputcontrol TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jireportunitresource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jirepositorycache TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiresource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiresourcefolder TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jirole TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jitenant TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiuser TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jiuserrole TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jivirtualdatasourceurimap TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jivirtualdatasource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE jixmlaconnection TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE profilingrecord TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_blob_triggers TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_calendars TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_cron_triggers TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_fired_triggers TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_job_details TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_locks TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_paused_trigger_grps TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_scheduler_state TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_simple_triggers TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_simprop_triggers TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_triggers TO jasperdb;

--
-- 2012-05-08: tonyk: add new tables for 4.7 release
--

GRANT ALL PRIVILEGES ON TABLE JIFTPInfoProperties TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIReportAlertToAddress TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIReportJobAlert TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIDataSnapshot TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIDataSnapshotContents TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIDataSnapshotParameter TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIAdhocDataView TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIAdhocDataViewInputControl TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIAdhocDataViewResource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE qrtz_simprop_triggers TO jasperdb;

--
-- 2012-08-07: ichan: add new tables for 5.0 multi-data source project
--             tonyk: add new table for 5.0 for monitoring
--

GRANT ALL PRIVILEGES ON TABLE JIVirtualDataSourceUriMap TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIVirtualDatasource TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE JIReportMonitoringFact TO jasperdb;

--
-- 2013-03-18: tonyk add new table for amazon support
--

GRANT ALL PRIVILEGES ON TABLE JIAwsDatasource TO jasperdb;

