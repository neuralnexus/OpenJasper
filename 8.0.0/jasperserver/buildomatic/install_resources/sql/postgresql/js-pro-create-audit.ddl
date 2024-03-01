    create table JIAccessEvent (
       id int8 not null,
        user_id varchar(255) not null,
        event_date timestamp not null,
        resource_uri varchar(255) not null,
        updating boolean not null,
        resource_type varchar(255) not null,
        hidden boolean not null,
        primary key (id)
    );

    create table JIAuditEvent (
       id int8 not null,
        username varchar(100),
        tenant_id varchar(100),
        event_date timestamp not null,
        resource_uri varchar(250),
        resource_type varchar(250),
        event_type varchar(100) not null,
        request_type varchar(100) not null,
        primary key (id)
    );

    create table JIAuditEventArchive (
       id int8 not null,
        username varchar(100),
        tenant_id varchar(100),
        event_date timestamp not null,
        resource_uri varchar(250),
        resource_type varchar(250),
        event_type varchar(100) not null,
        request_type varchar(100) not null,
        primary key (id)
    );

    create table JIAuditEventProperty (
       id int8 not null,
        property_type varchar(100) not null,
        value varchar(250),
        clob_value text,
        audit_event_id int8 not null,
        primary key (id)
    );

    create table JIAuditEventPropertyArchive (
       id int8 not null,
        property_type varchar(100) not null,
        value varchar(250),
        clob_value text,
        audit_event_id int8 not null,
        primary key (id)
    );

    create table JIReportMonitoringFact (
       id int8 not null,
        date_year int2 not null,
        date_month int2 not null,
        date_day int2 not null,
        time_hour int2 not null,
        time_minute int2 not null,
        event_context varchar(255) not null,
        user_organization varchar(255),
        user_name varchar(255),
        event_type varchar(255) not null,
        report_uri varchar(255),
        editing_action varchar(255),
        query_execution_time int4 not null,
        report_rendering_time int4 not null,
        total_report_execution_time int4 not null,
        time_stamp timestamp not null,
        primary key (id)
    );
create index access_user_index on JIAccessEvent (user_id);
create index access_date_index on JIAccessEvent (event_date);
create index access_res_uri_index on JIAccessEvent (resource_uri);
create index access_upd_index on JIAccessEvent (updating);
create index access_res_type_index on JIAccessEvent (resource_type);
create index access_hid_index on JIAccessEvent (hidden);
create index username_index on JIAuditEvent (username);
create index tenant_id_index on JIAuditEvent (tenant_id);
create index event_date_index on JIAuditEvent (event_date);
create index resource_uri_index on JIAuditEvent (resource_uri);
create index res_type_index on JIAuditEvent (resource_type);
create index event_type_index on JIAuditEvent (event_type);
create index request_type_index on JIAuditEvent (request_type);
create index date_year_index on JIReportMonitoringFact (date_year);
create index date_month_index on JIReportMonitoringFact (date_month);
create index date_day_index on JIReportMonitoringFact (date_day);
create index time_hour_index on JIReportMonitoringFact (time_hour);
create index time_minute_index on JIReportMonitoringFact (time_minute);
create index event_context_index on JIReportMonitoringFact (event_context);
create index user_organization_index on JIReportMonitoringFact (user_organization);
create index user_name_index on JIReportMonitoringFact (user_name);
create index event_type_index_2 on JIReportMonitoringFact (event_type);
create index report_uri_index on JIReportMonitoringFact (report_uri);
create index editing_action_index on JIReportMonitoringFact (editing_action);
create index query_execution_time_index on JIReportMonitoringFact (query_execution_time);
create index report_rendering_time_index on JIReportMonitoringFact (report_rendering_time);
create index total_report_exec_time_index on JIReportMonitoringFact (total_report_execution_time);
create index time_stamp_index on JIReportMonitoringFact (time_stamp);

    alter table JIAuditEventProperty 
       add constraint FK74sb8dic688mlyencffek40iw 
       foreign key (audit_event_id) 
       references JIAuditEvent;

    alter table JIAuditEventPropertyArchive 
       add constraint FK1lo2yra6fdwxqxo9769vyf4to 
       foreign key (audit_event_id) 
       references JIAuditEventArchive;
create index idx7_audit_event_id_idx on JIAuditEventProperty (audit_event_id);
create index idx8_audit_event_id_idx on JIAuditEventPropertyArchive (audit_event_id);
