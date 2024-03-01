
    alter table JIAuditEventProperty 
       drop constraint FK74sb8dic688mlyencffek40iw;

    alter table JIAuditEventPropertyArchive 
       drop constraint FK1lo2yra6fdwxqxo9769vyf4to;

    drop table if exists JIAccessEvent cascade;

    drop table if exists JIAuditEvent cascade;

    drop table if exists JIAuditEventArchive cascade;

    drop table if exists JIAuditEventProperty cascade;

    drop table if exists JIAuditEventPropertyArchive cascade;

    drop table if exists JIReportMonitoringFact cascade;

    drop sequence if exists hibernate_sequence;

    DROP INDEX idx8_audit_event_id_idx ON JIAuditEventPropertyArchive;

    DROP INDEX idx7_audit_event_id_idx ON JIAuditEventProperty;
