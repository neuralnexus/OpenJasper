--
--
-- 7.2.0 to 7.5.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

-- Change column type from "varchar(100)" to "varchar(150)"
ALTER TABLE JIAwsDatasource MODIFY COLUMN accessKey varchar(150);

-- Change column type from "varchar(100)" to "varchar(255)"
ALTER TABLE JIAwsDatasource MODIFY COLUMN secretKey varchar(255);
-- drop foreign key constraints from JIAccessEvent to JIResource and JIUser
    alter table JIAccessEvent
       drop
       foreign key FK7caj87u72rymu6805gtek03y8;

    alter table JIAccessEvent
       drop
       foreign key FK8lqavxfshc29dnw97io0t6wbf;

