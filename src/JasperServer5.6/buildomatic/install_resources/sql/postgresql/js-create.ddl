
    create table JIAccessEvent (
        id int8 not null,
        user_id int8 not null,
        event_date timestamp not null,
        resource_id int8 not null,
        updating bool not null,
        primary key (id)
    );

    create table JIAwsDatasource (
        id int8 not null,
        accessKey varchar(100),
        secretKey varchar(100),
        roleARN varchar(100),
        region varchar(100) not null,
        dbName varchar(100) not null,
        dbInstanceIdentifier varchar(100) not null,
        dbService varchar(100) not null,
        primary key (id)
    );

    create table JIBeanDatasource (
        id int8 not null,
        beanName varchar(100) not null,
        beanMethod varchar(100),
        primary key (id)
    );

    create table JIContentResource (
        id int8 not null,
        data bytea,
        file_type varchar(20),
        primary key (id)
    );

    create table JICustomDatasource (
        id int8 not null,
        serviceClass varchar(250) not null,
        primary key (id)
    );

    create table JICustomDatasourceProperty (
        ds_id int8 not null,
        value varchar(1000),
        name varchar(100) not null,
        primary key (ds_id, name)
    );

    create table JIDataSnapshot (
        id int8 not null,
        version int4 not null,
        snapshot_date timestamp,
        contents_id int8 not null,
        primary key (id)
    );

    create table JIDataSnapshotContents (
        id int8 not null,
        data bytea not null,
        primary key (id)
    );

    create table JIDataSnapshotParameter (
        id int8 not null,
        parameter_value bytea,
        parameter_name varchar(100) not null,
        primary key (id, parameter_name)
    );

    create table JIDataType (
        id int8 not null,
        type int2,
        maxLength int4,
        decimals int4,
        regularExpr varchar(255),
        minValue bytea,
        max_value bytea,
        strictMin bool,
        strictMax bool,
        primary key (id)
    );

    create table JIFTPInfoProperties (
        repodest_id int8 not null,
        property_value varchar(250),
        property_name varchar(100) not null,
        primary key (repodest_id, property_name)
    );

    create table JIFileResource (
        id int8 not null,
        data bytea,
        file_type varchar(20),
        reference int8,
        primary key (id)
    );

    create table JIInputControl (
        id int8 not null,
        type int2,
        mandatory bool,
        readOnly bool,
        visible bool,
        data_type int8,
        list_of_values int8,
        list_query int8,
        query_value_column varchar(200),
        defaultValue bytea,
        primary key (id)
    );

    create table JIInputControlQueryColumn (
        input_control_id int8 not null,
        query_column varchar(200) not null,
        column_index int4 not null,
        primary key (input_control_id, column_index)
    );

    create table JIJNDIJdbcDatasource (
        id int8 not null,
        jndiName varchar(100) not null,
        timezone varchar(100),
        primary key (id)
    );

    create table JIJdbcDatasource (
        id int8 not null,
        driver varchar(100) not null,
        password varchar(100),
        connectionUrl varchar(500),
        username varchar(100),
        timezone varchar(100),
        primary key (id)
    );

    create table JIListOfValues (
        id int8 not null,
        primary key (id)
    );

    create table JIListOfValuesItem (
        id int8 not null,
        label varchar(255),
        value bytea,
        idx int4 not null,
        primary key (id, idx)
    );

    create table JILogEvent (
        id int8 not null,
        occurrence_date timestamp not null,
        event_type int2 not null,
        component varchar(100),
        message varchar(250) not null,
        resource_uri varchar(250),
        event_text text,
        event_data bytea,
        event_state int2,
        userId int8,
        primary key (id)
    );

    create table JIMondrianConnection (
        id int8 not null,
        reportDataSource int8,
        mondrianSchema int8,
        primary key (id)
    );

    create table JIMondrianXMLADefinition (
        id int8 not null,
        catalog varchar(100) not null,
        mondrianConnection int8,
        primary key (id)
    );

    create table JIObjectPermission (
        id int8 not null,
        uri varchar(250) not null,
        recipientobjectclass varchar(250),
        recipientobjectid int8,
        permissionMask int4 not null,
        primary key (id)
    );

    create table JIOlapClientConnection (
        id int8 not null,
        primary key (id)
    );

    create table JIOlapUnit (
        id int8 not null,
        olapClientConnection int8,
        mdx_query text not null,
        view_options bytea,
        primary key (id)
    );

    create table JIProfileAttribute (
        id int8 not null,
        attrName varchar(255) not null,
        attrValue varchar(255) not null,
        principalobjectclass varchar(255) not null,
        principalobjectid int8 not null,
        primary key (id)
    );

    create table JIQuery (
        id int8 not null,
        dataSource int8,
        query_language varchar(40) not null,
        sql_query text not null,
        primary key (id)
    );

    create table JIReportAlertToAddress (
        alert_id int8 not null,
        to_address varchar(100) not null,
        to_address_idx int4 not null,
        primary key (alert_id, to_address_idx)
    );

    create table JIReportJob (
        id int8 not null,
        version int4 not null,
        owner int8 not null,
        label varchar(100) not null,
        description varchar(2000),
        creation_date timestamp,
        report_unit_uri varchar(250) not null,
        job_trigger int8 not null,
        base_output_name varchar(100) not null,
        output_locale varchar(20),
        content_destination int8,
        mail_notification int8,
        alert int8,
        primary key (id)
    );

    create table JIReportJobAlert (
        id int8 not null,
        version int4 not null,
        recipient int2 not null,
        subject varchar(100),
        message_text varchar(2000),
        message_text_when_job_fails varchar(2000),
        job_state int2 not null,
        including_stack_trace bool not null,
        including_report_job_info bool not null,
        primary key (id)
    );

    create table JIReportJobCalendarTrigger (
        id int8 not null,
        minutes varchar(200) not null,
        hours varchar(80) not null,
        days_type int2 not null,
        week_days varchar(20),
        month_days varchar(100),
        months varchar(40) not null,
        primary key (id)
    );

    create table JIReportJobMail (
        id int8 not null,
        version int4 not null,
        subject varchar(100) not null,
        message varchar(2000),
        send_type int2 not null,
        skip_empty bool not null,
        message_text_when_job_fails varchar(2000),
        inc_stktrc_when_job_fails bool not null,
        skip_notif_when_job_fails bool not null,
        primary key (id)
    );

    create table JIReportJobMailRecipient (
        destination_id int8 not null,
        recipient_type int2 not null,
        address varchar(100) not null,
        recipient_idx int4 not null,
        primary key (destination_id, recipient_idx)
    );

    create table JIReportJobOutputFormat (
        report_job_id int8 not null,
        output_format int2 not null,
        primary key (report_job_id, output_format)
    );

    create table JIReportJobParameter (
        job_id int8 not null,
        parameter_value bytea,
        parameter_name varchar(100) not null,
        primary key (job_id, parameter_name)
    );

    create table JIReportJobRepoDest (
        id int8 not null,
        version int4 not null,
        folder_uri varchar(250),
        sequential_filenames bool not null,
        overwrite_files bool not null,
        save_to_repository bool not null,
        output_description varchar(250),
        timestamp_pattern varchar(250),
        using_def_rpt_opt_folder_uri bool not null,
        output_local_folder varchar(250),
        user_name varchar(50),
        password varchar(50),
        server_name varchar(150),
        folder_path varchar(250),
        primary key (id)
    );

    create table JIReportJobSimpleTrigger (
        id int8 not null,
        occurrence_count int4 not null,
        recurrence_interval int4,
        recurrence_interval_unit int2,
        primary key (id)
    );

    create table JIReportJobTrigger (
        id int8 not null,
        version int4 not null,
        timezone varchar(40),
        start_type int2 not null,
        start_date timestamp,
        end_date timestamp,
        calendar_name varchar(50),
        misfire_instruction int4 not null,
        primary key (id)
    );

    create table JIReportUnit (
        id int8 not null,
        reportDataSource int8,
        query int8,
        mainReport int8,
        controlrenderer varchar(100),
        reportrenderer varchar(100),
        promptcontrols bool,
        controlslayout int2,
        data_snapshot_id int8,
        primary key (id)
    );

    create table JIReportUnitInputControl (
        report_unit_id int8 not null,
        input_control_id int8 not null,
        control_index int4 not null,
        primary key (report_unit_id, control_index)
    );

    create table JIReportUnitResource (
        report_unit_id int8 not null,
        resource_id int8 not null,
        resource_index int4 not null,
        primary key (report_unit_id, resource_index)
    );

    create table JIRepositoryCache (
        id int8 not null,
        uri varchar(250) not null,
        cache_name varchar(20) not null,
        data bytea,
        version int4 not null,
        version_date timestamp not null,
        item_reference int8,
        primary key (id),
        unique (uri, cache_name)
    );

    create table JIResource (
        id int8 not null,
        version int4 not null,
        name varchar(100) not null,
        parent_folder int8 not null,
        childrenFolder int8,
        label varchar(100) not null,
        description varchar(250),
        resourceType varchar(255) not null,
        creation_date timestamp not null,
        update_date timestamp not null,
        primary key (id),
        unique (name, parent_folder)
    );

    create table JIResourceFolder (
        id int8 not null,
        version int4 not null,
        uri varchar(250) not null,
        hidden bool,
        name varchar(100) not null,
        label varchar(100) not null,
        description varchar(250),
        parent_folder int8,
        creation_date timestamp not null,
        update_date timestamp not null,
        primary key (id),
        unique (uri)
    );

    create table JIRole (
        id int8 not null,
        rolename varchar(100) not null,
        tenantId int8 not null,
        externallyDefined bool,
        primary key (id),
        unique (rolename, tenantId)
    );

    create table JITenant (
        id int8 not null,
        tenantId varchar(100) not null,
        tenantAlias varchar(100) not null,
        parentId int8,
        tenantName varchar(100) not null,
        tenantDesc varchar(250),
        tenantNote varchar(250),
        tenantUri varchar(250) not null,
        tenantFolderUri varchar(250) not null,
        theme varchar(250),
        primary key (id),
        unique (tenantId)
    );

    create table JIUser (
        id int8 not null,
        username varchar(100) not null,
        tenantId int8 not null,
        fullname varchar(100) not null,
        emailAddress varchar(100),
        password varchar(100),
        externallyDefined bool,
        enabled bool,
        previousPasswordChangeTime timestamp,
        primary key (id),
        unique (username, tenantId)
    );

    create table JIUserRole (
        roleId int8 not null,
        userId int8 not null,
        primary key (userId, roleId)
    );

    create table JIVirtualDataSourceUriMap (
        virtualDS_id int8 not null,
        resource_id int8 not null,
        data_source_name varchar(100) not null,
        primary key (virtualDS_id, data_source_name)
    );

    create table JIVirtualDatasource (
        id int8 not null,
        timezone varchar(100),
        primary key (id)
    );

    create table JIXMLAConnection (
        id int8 not null,
        catalog varchar(100) not null,
        username varchar(100) not null,
        password varchar(100) not null,
        datasource varchar(100) not null,
        uri varchar(100) not null,
        primary key (id)
    );

    create index access_date_index on JIAccessEvent (event_date);

    create index access_upd_index on JIAccessEvent (updating);

    create index access_user_index on JIAccessEvent (user_id);

    create index access_res_index on JIAccessEvent (resource_id);

    alter table JIAccessEvent 
        add constraint FK47FB3CD732282198 
        foreign key (user_id) 
        references JIUser;

    alter table JIAccessEvent 
        add constraint FK47FB3CD7F254B53E 
        foreign key (resource_id) 
        references JIResource 
        on delete cascade;

    alter table JIAwsDatasource 
        add constraint FK6085542387E4472B 
        foreign key (id) 
        references JIJdbcDatasource;

    alter table JIBeanDatasource 
        add constraint FK674BF34A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIContentResource 
        add constraint FKE466FC68A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JICustomDatasource 
        add constraint FK2BBCEDF5A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JICustomDatasourceProperty 
        add constraint FKB8A66AEA858A89D1 
        foreign key (ds_id) 
        references JICustomDatasource;

    alter table JIDataSnapshotParameter 
        add constraint id_fk_idx 
        foreign key (id) 
        references JIDataSnapshot;

    alter table JIDataType 
        add constraint FK533BCC63A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIFTPInfoProperties 
        add constraint FK6BD68B04D5FA3F0A 
        foreign key (repodest_id) 
        references JIReportJobRepoDest;

    alter table JIFileResource 
        add constraint FKF75B5889A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIFileResource 
        add constraint FKF75B58895A0C539 
        foreign key (reference) 
        references JIFileResource;

    alter table JIInputControl 
        add constraint FKCAC6A512120E06F7 
        foreign key (data_type) 
        references JIDataType;

    alter table JIInputControl 
        add constraint FKCAC6A512A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIInputControl 
        add constraint FKCAC6A51262A86F04 
        foreign key (list_of_values) 
        references JIListOfValues;

    alter table JIInputControl 
        add constraint FKCAC6A512B37DB6EB 
        foreign key (list_query) 
        references JIQuery;

    alter table JIInputControlQueryColumn 
        add constraint FKE436A5CCE7922149 
        foreign key (input_control_id) 
        references JIInputControl;

    alter table JIJNDIJdbcDatasource 
        add constraint FK7F9DA248A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIJdbcDatasource 
        add constraint FKC8BDFCBFA8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIListOfValues 
        add constraint FK4E86A776A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIListOfValuesItem 
        add constraint FKD37CEBA993F0E1F6 
        foreign key (id) 
        references JIListOfValues;

    alter table JILogEvent 
        add constraint FK5F32081591865AF 
        foreign key (userId) 
        references JIUser;

    alter table JIMondrianConnection 
        add constraint FK4FF53B191D51BFAD 
        foreign key (id) 
        references JIOlapClientConnection;

    alter table JIMondrianConnection 
        add constraint FK4FF53B19324CFECB 
        foreign key (reportDataSource) 
        references JIResource;

    alter table JIMondrianConnection 
        add constraint FK4FF53B19C495A60B 
        foreign key (mondrianSchema) 
        references JIFileResource;

    alter table JIMondrianXMLADefinition 
        add constraint FK313B2AB8A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIMondrianXMLADefinition 
        add constraint FK313B2AB8DC098B1 
        foreign key (mondrianConnection) 
        references JIMondrianConnection;

    create index uri_index on JIObjectPermission (uri);

    alter table JIOlapClientConnection 
        add constraint FK3CA3B7D4A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIOlapUnit 
        add constraint FKF034DCCFA8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIOlapUnit 
        add constraint FKF034DCCF8F542247 
        foreign key (olapClientConnection) 
        references JIOlapClientConnection;

    alter table JIQuery 
        add constraint FKCBCB0EC9A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIQuery 
        add constraint FKCBCB0EC92B329A97 
        foreign key (dataSource) 
        references JIResource;

    alter table JIReportAlertToAddress 
        add constraint FKC4E3713022FA4CBA 
        foreign key (alert_id) 
        references JIReportJobAlert;

    alter table JIReportJob 
        add constraint FK156F5F6AE4D73E35 
        foreign key (mail_notification) 
        references JIReportJobMail;

    alter table JIReportJob 
        add constraint FK156F5F6AC83ABB38 
        foreign key (alert) 
        references JIReportJobAlert;

    alter table JIReportJob 
        add constraint FK156F5F6A9EEC902C 
        foreign key (content_destination) 
        references JIReportJobRepoDest;

    alter table JIReportJob 
        add constraint FK156F5F6A74D2696E 
        foreign key (job_trigger) 
        references JIReportJobTrigger;

    alter table JIReportJob 
        add constraint FK156F5F6A4141263C 
        foreign key (owner) 
        references JIUser;

    alter table JIReportJobCalendarTrigger 
        add constraint FKC374C7D0D2B2EB53 
        foreign key (id) 
        references JIReportJobTrigger;

    alter table JIReportJobMailRecipient 
        add constraint FKBB6DB6D880001AAE 
        foreign key (destination_id) 
        references JIReportJobMail;

    alter table JIReportJobOutputFormat 
        add constraint FKB42A5CE2C3389A8 
        foreign key (report_job_id) 
        references JIReportJob;

    alter table JIReportJobParameter 
        add constraint FKEAC52B5F2EC643D 
        foreign key (job_id) 
        references JIReportJob;

    alter table JIReportJobSimpleTrigger 
        add constraint FKB9337C5CD2B2EB53 
        foreign key (id) 
        references JIReportJobTrigger;

    alter table JIReportUnit 
        add constraint FK98818B77A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIReportUnit 
        add constraint FK98818B778FDA11CC 
        foreign key (query) 
        references JIQuery;

    alter table JIReportUnit 
        add constraint FK98818B77324CFECB 
        foreign key (reportDataSource) 
        references JIResource;

    alter table JIReportUnit 
        add constraint FK98818B778C8DF21B 
        foreign key (mainReport) 
        references JIFileResource;

    alter table JIReportUnitInputControl 
        add constraint FK5FBE934AE7922149 
        foreign key (input_control_id) 
        references JIInputControl;

    alter table JIReportUnitInputControl 
        add constraint FK5FBE934AA6A48880 
        foreign key (report_unit_id) 
        references JIReportUnit;

    alter table JIReportUnitResource 
        add constraint FK8B1C4CA5A6A48880 
        foreign key (report_unit_id) 
        references JIReportUnit;

    alter table JIReportUnitResource 
        add constraint FK8B1C4CA5865B10DA 
        foreign key (resource_id) 
        references JIFileResource;

    alter table JIRepositoryCache 
        add constraint FKE7338B19E7C5A6 
        foreign key (item_reference) 
        references JIRepositoryCache;

    create index resource_type_index on JIResource (resourceType);

    alter table JIResource 
        add constraint FKD444826DA58002DF 
        foreign key (childrenFolder) 
        references JIResourceFolder;

    alter table JIResource 
        add constraint FKD444826DA08E2155 
        foreign key (parent_folder) 
        references JIResourceFolder;

    alter table JIResourceFolder 
        add constraint FK7F24453BA08E2155 
        foreign key (parent_folder) 
        references JIResourceFolder;

    alter table JIRole 
        add constraint FK82724655E415AC2D 
        foreign key (tenantId) 
        references JITenant;

    alter table JITenant 
        add constraint FKB1D7B2C97803CC2D 
        foreign key (parentId) 
        references JITenant;

    alter table JIUser 
        add constraint FK8273B1AAE415AC2D 
        foreign key (tenantId) 
        references JITenant;

    alter table JIUserRole 
        add constraint FKD8B5C1403C31045 
        foreign key (roleId) 
        references JIRole;

    alter table JIUserRole 
        add constraint FKD8B5C14091865AF 
        foreign key (userId) 
        references JIUser;

    alter table JIVirtualDataSourceUriMap 
        add constraint FK4A6CCE019E600E20 
        foreign key (virtualDS_id) 
        references JIVirtualDatasource;

    alter table JIVirtualDataSourceUriMap 
        add constraint FK4A6CCE01F254B53E 
        foreign key (resource_id) 
        references JIResource;

    alter table JIVirtualDatasource 
        add constraint FK30E55631A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JIXMLAConnection 
        add constraint FK94C688A71D51BFAD 
        foreign key (id) 
        references JIOlapClientConnection;

    create sequence hibernate_sequence;
