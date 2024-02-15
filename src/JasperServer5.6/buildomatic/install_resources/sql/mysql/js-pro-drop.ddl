
    alter table JIAccessEvent 
        drop 
        foreign key FK47FB3CD732282198;

    alter table JIAccessEvent 
        drop 
        foreign key FK47FB3CD7F254B53E;

    alter table JIAdhocChartMeasure 
        drop 
        foreign key FK89D1A3FAB0A3C8CB;

    alter table JIAdhocColumn 
        drop 
        foreign key FK9265D330EC885ADB;

    alter table JIAdhocDataView 
        drop 
        foreign key FK200A2AC9A8BF376D;

    alter table JIAdhocDataView 
        drop 
        foreign key FK200A2AC9324CFECB;

    alter table JIAdhocDataView 
        drop 
        foreign key FK200A2AC931211827;

    alter table JIAdhocDataViewInputControl 
        drop 
        foreign key FKA248C79CB22FF3B2;

    alter table JIAdhocDataViewInputControl 
        drop 
        foreign key FKA248C79CE7922149;

    alter table JIAdhocDataViewResource 
        drop 
        foreign key FK98179F7B22FF3B2;

    alter table JIAdhocDataViewResource 
        drop 
        foreign key FK98179F7865B10DA;

    alter table JIAdhocGroup 
        drop 
        foreign key FK704D9365EC885ADB;

    alter table JIAdhocReportUnit 
        drop 
        foreign key FK68AE6BB2981B13F0;

    alter table JIAdhocReportUnit 
        drop 
        foreign key FK68AE6BB231211827;

    alter table JIAdhocStateProperty 
        drop 
        foreign key FK2C7E3C6C298B519D;

    alter table JIAdhocTableSortField 
        drop 
        foreign key FK1AF05FA8EC885ADB;

    alter table JIAdhocXTabColumnGroup 
        drop 
        foreign key FK336E71F262231DA;

    alter table JIAdhocXTabMeasure 
        drop 
        foreign key FK3CF53B0762231DA;

    alter table JIAdhocXTabRowGroup 
        drop 
        foreign key FK9D33843C62231DA;

    alter table JIAuditEventProperty 
        drop 
        foreign key FK3429FE136F667020;

    alter table JIAuditEventPropertyArchive 
        drop 
        foreign key FKD2940F2F637AC28A;

    alter table JIAwsDatasource 
        drop 
        foreign key FK6085542387E4472B;

    alter table JIBeanDatasource 
        drop 
        foreign key FK674BF34A8BF376D;

    alter table JIContentResource 
        drop 
        foreign key FKE466FC68A8BF376D;

    alter table JICustomDatasource 
        drop 
        foreign key FK2BBCEDF5A8BF376D;

    alter table JICustomDatasourceProperty 
        drop 
        foreign key FKB8A66AEA858A89D1;

    alter table JIDashboard 
        drop 
        foreign key FKEC09F815A8BF376D;

    alter table JIDashboard 
        drop 
        foreign key FKEC09F81531211827;

    alter table JIDashboardFrameProperty 
        drop 
        foreign key FK679EF04DFA08F0B4;

    alter table JIDashboardResource 
        drop 
        foreign key FK37B53B43326276AC;

    alter table JIDashboardResource 
        drop 
        foreign key FK37B53B43F254B53E;

    alter table JIDataDefinerUnit 
        drop 
        foreign key FK1EC11AF2981B13F0;

    alter table JIDataSnapshotParameter 
        drop 
        foreign key id_fk_idx;

    alter table JIDataType 
        drop 
        foreign key FK533BCC63A8BF376D;

    alter table JIDomainDatasource 
        drop 
        foreign key FK59F8EB88A8BF376D;

    alter table JIDomainDatasource 
        drop 
        foreign key FK59F8EB88992A3868;

    alter table JIDomainDatasource 
        drop 
        foreign key FK59F8EB8833A6D267;

    alter table JIDomainDatasourceBundle 
        drop 
        foreign key FKE9F0422AE494DFE6;

    alter table JIDomainDatasourceBundle 
        drop 
        foreign key FKE9F0422ACB906E03;

    alter table JIDomainDatasourceDSRef 
        drop 
        foreign key FKFDA42FCCB906E03;

    alter table JIDomainDatasourceDSRef 
        drop 
        foreign key FKFDA42FC7106B699;

    alter table JIFTPInfoProperties 
        drop 
        foreign key FK6BD68B04D5FA3F0A;

    alter table JIFileResource 
        drop 
        foreign key FKF75B5889A8BF376D;

    alter table JIFileResource 
        drop 
        foreign key FKF75B58895A0C539;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A512120E06F7;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A512A8BF376D;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A51262A86F04;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A512B37DB6EB;

    alter table JIInputControlQueryColumn 
        drop 
        foreign key FKE436A5CCE7922149;

    alter table JIJNDIJdbcDatasource 
        drop 
        foreign key FK7F9DA248A8BF376D;

    alter table JIJdbcDatasource 
        drop 
        foreign key FKC8BDFCBFA8BF376D;

    alter table JIListOfValues 
        drop 
        foreign key FK4E86A776A8BF376D;

    alter table JIListOfValuesItem 
        drop 
        foreign key FKD37CEBA993F0E1F6;

    alter table JILogEvent 
        drop 
        foreign key FK5F32081591865AF;

    alter table JIMondrianConnection 
        drop 
        foreign key FK4FF53B191D51BFAD;

    alter table JIMondrianConnection 
        drop 
        foreign key FK4FF53B19324CFECB;

    alter table JIMondrianConnection 
        drop 
        foreign key FK4FF53B19C495A60B;

    alter table JIMondrianConnectionGrant 
        drop 
        foreign key FK3DDE9D8346D80AD2;

    alter table JIMondrianConnectionGrant 
        drop 
        foreign key FK3DDE9D83FFAC5026;

    alter table JIMondrianXMLADefinition 
        drop 
        foreign key FK313B2AB8A8BF376D;

    alter table JIMondrianXMLADefinition 
        drop 
        foreign key FK313B2AB8801D6C37;

    alter table JIOlapClientConnection 
        drop 
        foreign key FK3CA3B7D4A8BF376D;

    alter table JIOlapUnit 
        drop 
        foreign key FKF034DCCFA8BF376D;

    alter table JIOlapUnit 
        drop 
        foreign key FKF034DCCF8F542247;

    alter table JIQuery 
        drop 
        foreign key FKCBCB0EC9A8BF376D;

    alter table JIQuery 
        drop 
        foreign key FKCBCB0EC92B329A97;

    alter table JIReportAlertToAddress 
        drop 
        foreign key FKC4E3713022FA4CBA;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6AE4D73E35;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6AC83ABB38;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6A9EEC902C;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6A74D2696E;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6A4141263C;

    alter table JIReportJobCalendarTrigger 
        drop 
        foreign key FKC374C7D0D2B2EB53;

    alter table JIReportJobMailRecipient 
        drop 
        foreign key FKBB6DB6D880001AAE;

    alter table JIReportJobOutputFormat 
        drop 
        foreign key FKB42A5CE2C3389A8;

    alter table JIReportJobParameter 
        drop 
        foreign key FKEAC52B5F2EC643D;

    alter table JIReportJobSimpleTrigger 
        drop 
        foreign key FKB9337C5CD2B2EB53;

    alter table JIReportOptions 
        drop 
        foreign key resource_id;

    alter table JIReportOptions 
        drop 
        foreign key report_fk;

    alter table JIReportOptionsInput 
        drop 
        foreign key options_fk;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B77A8BF376D;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B778FDA11CC;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B77324CFECB;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B778C8DF21B;

    alter table JIReportUnitInputControl 
        drop 
        foreign key FK5FBE934AE7922149;

    alter table JIReportUnitInputControl 
        drop 
        foreign key FK5FBE934AA6A48880;

    alter table JIReportUnitResource 
        drop 
        foreign key FK8B1C4CA5A6A48880;

    alter table JIReportUnitResource 
        drop 
        foreign key FK8B1C4CA5865B10DA;

    alter table JIRepositoryCache 
        drop 
        foreign key FKE7338B19E7C5A6;

    alter table JIResource 
        drop 
        foreign key FKD444826DA58002DF;

    alter table JIResource 
        drop 
        foreign key FKD444826DA08E2155;

    alter table JIResourceFolder 
        drop 
        foreign key FK7F24453BA08E2155;

    alter table JIRole 
        drop 
        foreign key FK82724655E415AC2D;

    alter table JITenant 
        drop 
        foreign key FKB1D7B2C97803CC2D;

    alter table JIUser 
        drop 
        foreign key FK8273B1AAE415AC2D;

    alter table JIUserRole 
        drop 
        foreign key FKD8B5C1403C31045;

    alter table JIUserRole 
        drop 
        foreign key FKD8B5C14091865AF;

    alter table JIVirtualDataSourceUriMap 
        drop 
        foreign key FK4A6CCE019E600E20;

    alter table JIVirtualDataSourceUriMap 
        drop 
        foreign key FK4A6CCE01F254B53E;

    alter table JIVirtualDatasource 
        drop 
        foreign key FK30E55631A8BF376D;

    alter table JIXMLAConnection 
        drop 
        foreign key FK94C688A71D51BFAD;

    alter table ProfilingRecord 
        drop 
        foreign key FK92D5BBF7DACDD6DA;

    drop table if exists JIAccessEvent;

    drop table if exists JIAdhocChartMeasure;

    drop table if exists JIAdhocColumn;

    drop table if exists JIAdhocDataView;

    drop table if exists JIAdhocDataViewInputControl;

    drop table if exists JIAdhocDataViewResource;

    drop table if exists JIAdhocGroup;

    drop table if exists JIAdhocReportUnit;

    drop table if exists JIAdhocState;

    drop table if exists JIAdhocStateProperty;

    drop table if exists JIAdhocTableSortField;

    drop table if exists JIAdhocXTabColumnGroup;

    drop table if exists JIAdhocXTabMeasure;

    drop table if exists JIAdhocXTabRowGroup;

    drop table if exists JIAuditEvent;

    drop table if exists JIAuditEventArchive;

    drop table if exists JIAuditEventProperty;

    drop table if exists JIAuditEventPropertyArchive;

    drop table if exists JIAwsDatasource;

    drop table if exists JIBeanDatasource;

    drop table if exists JIContentResource;

    drop table if exists JICustomDatasource;

    drop table if exists JICustomDatasourceProperty;

    drop table if exists JIDashboard;

    drop table if exists JIDashboardFrameProperty;

    drop table if exists JIDashboardResource;

    drop table if exists JIDataDefinerUnit;

    drop table if exists JIDataSnapshot;

    drop table if exists JIDataSnapshotContents;

    drop table if exists JIDataSnapshotParameter;

    drop table if exists JIDataType;

    drop table if exists JIDomainDatasource;

    drop table if exists JIDomainDatasourceBundle;

    drop table if exists JIDomainDatasourceDSRef;

    drop table if exists JIFTPInfoProperties;

    drop table if exists JIFileResource;

    drop table if exists JIInputControl;

    drop table if exists JIInputControlQueryColumn;

    drop table if exists JIJNDIJdbcDatasource;

    drop table if exists JIJdbcDatasource;

    drop table if exists JIListOfValues;

    drop table if exists JIListOfValuesItem;

    drop table if exists JILogEvent;

    drop table if exists JIMondrianConnection;

    drop table if exists JIMondrianConnectionGrant;

    drop table if exists JIMondrianXMLADefinition;

    drop table if exists JIObjectPermission;

    drop table if exists JIOlapClientConnection;

    drop table if exists JIOlapUnit;

    drop table if exists JIProfileAttribute;

    drop table if exists JIQuery;

    drop table if exists JIReportAlertToAddress;

    drop table if exists JIReportJob;

    drop table if exists JIReportJobAlert;

    drop table if exists JIReportJobCalendarTrigger;

    drop table if exists JIReportJobMail;

    drop table if exists JIReportJobMailRecipient;

    drop table if exists JIReportJobOutputFormat;

    drop table if exists JIReportJobParameter;

    drop table if exists JIReportJobRepoDest;

    drop table if exists JIReportJobSimpleTrigger;

    drop table if exists JIReportJobTrigger;

    drop table if exists JIReportMonitoringFact;

    drop table if exists JIReportOptions;

    drop table if exists JIReportOptionsInput;

    drop table if exists JIReportUnit;

    drop table if exists JIReportUnitInputControl;

    drop table if exists JIReportUnitResource;

    drop table if exists JIRepositoryCache;

    drop table if exists JIResource;

    drop table if exists JIResourceFolder;

    drop table if exists JIRole;

    drop table if exists JITenant;

    drop table if exists JIUser;

    drop table if exists JIUserRole;

    drop table if exists JIVirtualDataSourceUriMap;

    drop table if exists JIVirtualDatasource;

    drop table if exists JIXMLAConnection;

    drop table if exists ProfilingRecord;
