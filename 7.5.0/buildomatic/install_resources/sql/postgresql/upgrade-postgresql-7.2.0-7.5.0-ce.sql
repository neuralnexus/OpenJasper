--
--
-- 7.2.0 to 7.5.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

-- Change column type from "varchar(100)" to "varchar(150)"
ALTER TABLE JIAwsDatasource ALTER COLUMN accessKey TYPE varchar(150);

-- Change column type from "varchar(100)" to "varchar(255)"
ALTER TABLE JIAwsDatasource ALTER COLUMN secretKey TYPE varchar(255);
-- drop foreign key from JIAccessEvent to JIResource and JIUser

    alter table JIAccessEvent
       drop constraint FK7caj87u72rymu6805gtek03y8;

    alter table JIAccessEvent
       drop constraint FK8lqavxfshc29dnw97io0t6wbf;

