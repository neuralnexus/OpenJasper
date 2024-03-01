--
--
-- 7.9.0 to 8.0.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

-- Change table name from "JIAccessEvent" to "JIAuditAccessEvent"
/*ALTER TABLE JIAccessEvent
    RENAME TO JIAuditAccessEvent;*/

--Delete Access events for deleted resources
DELETE FROM JIAccessEvent j
    WHERE j.resource_id NOT IN (SELECT j2.id FROM JIResource j2);

ALTER TABLE JIAccessEvent
    ADD COLUMN userid varchar(255);

UPDATE JIAccessEvent
    SET userid= case when t3.tenantid = 'organizations' then t2.username else concat(t2.username, '|', t3.tenantid) end
    FROM JIUser t2 JOIN JITenant t3 ON t2.tenantid=t3.id
    WHERE user_id=t2.id;

ALTER TABLE JIAccessEvent
    ALTER COLUMN userid SET NOT NULL;

ALTER TABLE JIAccessEvent DROP COLUMN user_id;

ALTER TABLE JIAccessEvent RENAME COLUMN userid TO user_id;

create index access_user_index on JIAccessEvent (user_id);

ALTER TABLE JIAccessEvent
    ADD COLUMN resource_uri varchar(255),
    ADD COLUMN resource_type varchar(255),
    ADD COLUMN hidden boolean;

UPDATE JIAccessEvent
    SET resource_type=t2.resourcetype,
        resource_uri=case when t3.uri='/'
            then concat(t3.uri,t2.name)
            else concat(t3.uri,'/',t2.name) end,
        hidden=t3.hidden
    FROM JIResource t2 JOIN JIResourceFolder t3 ON t2.parent_folder=t3.id
    WHERE resource_id=t2.id;

ALTER TABLE JIAccessEvent DROP COLUMN resource_id;

ALTER TABLE JIAccessEvent
    ALTER COLUMN resource_uri SET NOT NULL;

ALTER TABLE JIAccessEvent
    ALTER COLUMN resource_type SET NOT NULL;

ALTER TABLE JIAccessEvent
    ALTER COLUMN hidden SET NOT NULL;

create index access_res_type_index on JIAccessEvent (resource_type);

create index access_res_uri_index on JIAccessEvent (resource_uri);

create index access_hid_index on JIAccessEvent (hidden);

create index idx_keyStore_id on JIAzureSqlDatasource (keyStore_id);

create index idx_scheduled_res on JIReportJob (scheduledResource);

create index idx_ssh_private_key on JIReportJobRepoDest (ssh_private_key);
