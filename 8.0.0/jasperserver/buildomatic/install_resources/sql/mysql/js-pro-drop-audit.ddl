
    alter table JIAuditEventProperty 
       drop 
       foreign key FK74sb8dic688mlyencffek40iw;

    alter table JIAuditEventPropertyArchive 
       drop 
       foreign key FK1lo2yra6fdwxqxo9769vyf4to;

    drop table if exists JIAccessEvent;

    drop table if exists JIAuditEvent;

    drop table if exists JIAuditEventArchive;

    drop table if exists JIAuditEventProperty;

    drop table if exists JIAuditEventPropertyArchive;

    drop table if exists JIReportMonitoringFact;

    DROP INDEX idx8_audit_event_id_idx ON JIAuditEventPropertyArchive;

    DROP INDEX idx7_audit_event_id_idx ON JIAuditEventProperty;
