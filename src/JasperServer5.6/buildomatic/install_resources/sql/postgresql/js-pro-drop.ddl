
    alter table JIAccessEvent 
        drop constraint FK47FB3CD732282198;

    alter table JIAccessEvent 
        drop constraint FK47FB3CD7F254B53E;

    alter table JIAdhocDataView 
        drop constraint FK200A2AC9A8BF376D;

    alter table JIAdhocDataView 
        drop constraint FK200A2AC9324CFECB;

    alter table JIAdhocDataView 
        drop constraint FK200A2AC931211827;

    alter table JIAdhocDataViewInputControl 
        drop constraint FKA248C79CB22FF3B2;

    alter table JIAdhocDataViewInputControl 
        drop constraint FKA248C79CE7922149;

    alter table JIAdhocDataViewResource 
        drop constraint FK98179F7B22FF3B2;

    alter table JIAdhocDataViewResource 
        drop constraint FK98179F7865B10DA;

    alter table JIAdhocReportUnit 
        drop constraint FK68AE6BB2981B13F0;

    alter table JIAdhocReportUnit 
        drop constraint FK68AE6BB231211827;

    alter table JIAdhocStateProperty 
        drop constraint FK2C7E3C6C298B519D;

    alter table JIAuditEventProperty 
        drop constraint FK3429FE136F667020;

    alter table JIAuditEventPropertyArchive 
        drop constraint FKD2940F2F637AC28A;

    alter table JIAwsDatasource 
        drop constraint FK6085542387E4472B;

    alter table JIBeanDatasource 
        drop constraint FK674BF34A8BF376D;

    alter table JIContentResource 
        drop constraint FKE466FC68A8BF376D;

    alter table JICustomDatasource 
        drop constraint FK2BBCEDF5A8BF376D;

    alter table JICustomDatasourceProperty 
        drop constraint FKB8A66AEA858A89D1;

    alter table JIDashboard 
        drop constraint FKEC09F815A8BF376D;

    alter table JIDashboard 
        drop constraint FKEC09F81531211827;

    alter table JIDashboardFrameProperty 
        drop constraint FK679EF04DFA08F0B4;

    alter table JIDashboardResource 
        drop constraint FK37B53B43326276AC;

    alter table JIDashboardResource 
        drop constraint FK37B53B43F254B53E;

    alter table JIDataDefinerUnit 
        drop constraint FK1EC11AF2981B13F0;

    alter table JIDataSnapshotParameter 
        drop constraint id_fk_idx;

    alter table JIDataType 
        drop constraint FK533BCC63A8BF376D;

    alter table JIDomainDatasource 
        drop constraint FK59F8EB88A8BF376D;

    alter table JIDomainDatasource 
        drop constraint FK59F8EB88992A3868;

    alter table JIDomainDatasource 
        drop constraint FK59F8EB8833A6D267;

    alter table JIDomainDatasourceBundle 
        drop constraint FKE9F0422AE494DFE6;

    alter table JIDomainDatasourceBundle 
        drop constraint FKE9F0422ACB906E03;

    alter table JIDomainDatasourceDSRef 
        drop constraint FKFDA42FCCB906E03;

    alter table JIDomainDatasourceDSRef 
        drop constraint FKFDA42FC7106B699;

    alter table JIFTPInfoProperties 
        drop constraint FK6BD68B04D5FA3F0A;

    alter table JIFileResource 
        drop constraint FKF75B5889A8BF376D;

    alter table JIFileResource 
        drop constraint FKF75B58895A0C539;

    alter table JIInputControl 
        drop constraint FKCAC6A512120E06F7;

    alter table JIInputControl 
        drop constraint FKCAC6A512A8BF376D;

    alter table JIInputControl 
        drop constraint FKCAC6A51262A86F04;

    alter table JIInputControl 
        drop constraint FKCAC6A512B37DB6EB;

    alter table JIInputControlQueryColumn 
        drop constraint FKE436A5CCE7922149;

    alter table JIJNDIJdbcDatasource 
        drop constraint FK7F9DA248A8BF376D;

    alter table JIJdbcDatasource 
        drop constraint FKC8BDFCBFA8BF376D;

    alter table JIListOfValues 
        drop constraint FK4E86A776A8BF376D;

    alter table JIListOfValuesItem 
        drop constraint FKD37CEBA993F0E1F6;

    alter table JILogEvent 
        drop constraint FK5F32081591865AF;

    alter table JIMondrianConnection 
        drop constraint FK4FF53B191D51BFAD;

    alter table JIMondrianConnection 
        drop constraint FK4FF53B19324CFECB;

    alter table JIMondrianConnection 
        drop constraint FK4FF53B19C495A60B;

    alter table JIMondrianConnectionGrant 
        drop constraint FK3DDE9D8346D80AD2;

    alter table JIMondrianConnectionGrant 
        drop constraint FK3DDE9D83FFAC5026;

    alter table JIMondrianXMLADefinition 
        drop constraint FK313B2AB8A8BF376D;

    alter table JIMondrianXMLADefinition 
        drop constraint FK313B2AB8801D6C37;

    alter table JIOlapClientConnection 
        drop constraint FK3CA3B7D4A8BF376D;

    alter table JIOlapUnit 
        drop constraint FKF034DCCFA8BF376D;

    alter table JIOlapUnit 
        drop constraint FKF034DCCF8F542247;

    alter table JIQuery 
        drop constraint FKCBCB0EC9A8BF376D;

    alter table JIQuery 
        drop constraint FKCBCB0EC92B329A97;

    alter table JIReportAlertToAddress 
        drop constraint FKC4E3713022FA4CBA;

    alter table JIReportJob 
        drop constraint FK156F5F6AE4D73E35;

    alter table JIReportJob 
        drop constraint FK156F5F6AC83ABB38;

    alter table JIReportJob 
        drop constraint FK156F5F6A9EEC902C;

    alter table JIReportJob 
        drop constraint FK156F5F6A74D2696E;

    alter table JIReportJob 
        drop constraint FK156F5F6A4141263C;

    alter table JIReportJobCalendarTrigger 
        drop constraint FKC374C7D0D2B2EB53;

    alter table JIReportJobMailRecipient 
        drop constraint FKBB6DB6D880001AAE;

    alter table JIReportJobOutputFormat 
        drop constraint FKB42A5CE2C3389A8;

    alter table JIReportJobParameter 
        drop constraint FKEAC52B5F2EC643D;

    alter table JIReportJobSimpleTrigger 
        drop constraint FKB9337C5CD2B2EB53;

    alter table JIReportOptions 
        drop constraint resource_id;

    alter table JIReportOptions 
        drop constraint report_fk;

    alter table JIReportOptionsInput 
        drop constraint options_fk;

    alter table JIReportUnit 
        drop constraint FK98818B77A8BF376D;

    alter table JIReportUnit 
        drop constraint FK98818B778FDA11CC;

    alter table JIReportUnit 
        drop constraint FK98818B77324CFECB;

    alter table JIReportUnit 
        drop constraint FK98818B778C8DF21B;

    alter table JIReportUnitInputControl 
        drop constraint FK5FBE934AE7922149;

    alter table JIReportUnitInputControl 
        drop constraint FK5FBE934AA6A48880;

    alter table JIReportUnitResource 
        drop constraint FK8B1C4CA5A6A48880;

    alter table JIReportUnitResource 
        drop constraint FK8B1C4CA5865B10DA;

    alter table JIRepositoryCache 
        drop constraint FKE7338B19E7C5A6;

    alter table JIResource 
        drop constraint FKD444826DA58002DF;

    alter table JIResource 
        drop constraint FKD444826DA08E2155;

    alter table JIResourceFolder 
        drop constraint FK7F24453BA08E2155;

    alter table JIRole 
        drop constraint FK82724655E415AC2D;

    alter table JITenant 
        drop constraint FKB1D7B2C97803CC2D;

    alter table JIUser 
        drop constraint FK8273B1AAE415AC2D;

    alter table JIUserRole 
        drop constraint FKD8B5C1403C31045;

    alter table JIUserRole 
        drop constraint FKD8B5C14091865AF;

    alter table JIVirtualDataSourceUriMap 
        drop constraint FK4A6CCE019E600E20;

    alter table JIVirtualDataSourceUriMap 
        drop constraint FK4A6CCE01F254B53E;

    alter table JIVirtualDatasource 
        drop constraint FK30E55631A8BF376D;

    alter table JIXMLAConnection 
        drop constraint FK94C688A71D51BFAD;

    alter table ProfilingRecord 
        drop constraint FK92D5BBF7DACDD6DA;

    drop table JIAccessEvent;

    drop table JIAdhocDataView;

    drop table JIAdhocDataViewInputControl;

    drop table JIAdhocDataViewResource;

    drop table JIAdhocReportUnit;

    drop table JIAdhocState;

    drop table JIAdhocStateProperty;

    drop table JIAuditEvent;

    drop table JIAuditEventArchive;

    drop table JIAuditEventProperty;

    drop table JIAuditEventPropertyArchive;

    drop table JIAwsDatasource;

    drop table JIBeanDatasource;

    drop table JIContentResource;

    drop table JICustomDatasource;

    drop table JICustomDatasourceProperty;

    drop table JIDashboard;

    drop table JIDashboardFrameProperty;

    drop table JIDashboardResource;

    drop table JIDataDefinerUnit;

    drop table JIDataSnapshot;

    drop table JIDataSnapshotContents;

    drop table JIDataSnapshotParameter;

    drop table JIDataType;

    drop table JIDomainDatasource;

    drop table JIDomainDatasourceBundle;

    drop table JIDomainDatasourceDSRef;

    drop table JIFTPInfoProperties;

    drop table JIFileResource;

    drop table JIInputControl;

    drop table JIInputControlQueryColumn;

    drop table JIJNDIJdbcDatasource;

    drop table JIJdbcDatasource;

    drop table JIListOfValues;

    drop table JIListOfValuesItem;

    drop table JILogEvent;

    drop table JIMondrianConnection;

    drop table JIMondrianConnectionGrant;

    drop table JIMondrianXMLADefinition;

    drop table JIObjectPermission;

    drop table JIOlapClientConnection;

    drop table JIOlapUnit;

    drop table JIProfileAttribute;

    drop table JIQuery;

    drop table JIReportAlertToAddress;

    drop table JIReportJob;

    drop table JIReportJobAlert;

    drop table JIReportJobCalendarTrigger;

    drop table JIReportJobMail;

    drop table JIReportJobMailRecipient;

    drop table JIReportJobOutputFormat;

    drop table JIReportJobParameter;

    drop table JIReportJobRepoDest;

    drop table JIReportJobSimpleTrigger;

    drop table JIReportJobTrigger;

    drop table JIReportMonitoringFact;

    drop table JIReportOptions;

    drop table JIReportOptionsInput;

    drop table JIReportUnit;

    drop table JIReportUnitInputControl;

    drop table JIReportUnitResource;

    drop table JIRepositoryCache;

    drop table JIResource;

    drop table JIResourceFolder;

    drop table JIRole;

    drop table JITenant;

    drop table JIUser;

    drop table JIUserRole;

    drop table JIVirtualDataSourceUriMap;

    drop table JIVirtualDatasource;

    drop table JIXMLAConnection;

    drop table ProfilingRecord;

    drop sequence hibernate_sequence;
