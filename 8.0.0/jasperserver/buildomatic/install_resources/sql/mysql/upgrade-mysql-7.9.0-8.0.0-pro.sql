--
--
-- 7.9.0 to 8.0.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

--Delete Access events for deleted resources
DELETE FROM JIAccessEvent 
WHERE resource_id NOT IN (SELECT j2.id FROM JIResource j2);

--Drop Foreign Key constraints
ALTER TABLE JIAccessEvent DROP FOREIGN KEY FK7caj87u72rymu6805gtek03y8;

ALTER TABLE JIAccessEvent DROP FOREIGN KEY FK8lqavxfshc29dnw97io0t6wbf;

ALTER TABLE JIAccessEvent
    ADD COLUMN userid varchar(255) not null;

UPDATE JIAccessEvent t1 JOIN JIUser t2 ON t1.user_id=t2.id
    JOIN JITenant t3 ON t2.tenantid=t3.id
    SET userid= case when t3.tenantid = 'organizations' then t2.username
        else concat(t2.username, '|', t3.tenantid) end;

ALTER TABLE JIAccessEvent DROP COLUMN user_id;

ALTER TABLE JIAccessEvent CHANGE COLUMN userid user_id varchar(255) not null;

create index access_user_index on JIAccessEvent (user_id);

ALTER TABLE JIAccessEvent
    ADD (resource_uri varchar(255) not null, resource_type varchar(255) not null, hidden bit not null);

UPDATE JIAccessEvent t1 JOIN JIResource t2 ON t1.resource_id=t2.id
    JOIN JIResourceFolder t3 ON t2.parent_folder=t3.id
    SET t1.resource_type=t2.resourcetype,
        t1.resource_uri=case when t3.uri='/'
        then concat(t3.uri,t2.name)
        else concat(t3.uri,'/',t2.name) end,
    t1.hidden=t3.hidden;

ALTER TABLE JIAccessEvent DROP COLUMN resource_id;

create index access_res_type_index on JIAccessEvent (resource_type);

create index access_res_uri_index on JIAccessEvent (resource_uri);

create index access_hid_index on JIAccessEvent (hidden);

create index idx_keyStore_id on JIAzureSqlDatasource (keyStore_id);

create index idx_domain_schema_file on JIDomainDatasource (schema_id);

create index idx_domain_security_file on JIDomainDatasource (security_id);

create index idx_scheduled_res on JIReportJob (scheduledResource);

create index idx_ssh_private_key on JIReportJobRepoDest (ssh_private_key);
