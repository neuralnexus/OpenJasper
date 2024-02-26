--
--
-- 8.1.0 to 8.2.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--
-- login/lockout changes

create table JIExternalUserLoginEvents (
    id                          int8         not null,
    username                    varchar(100) not null,
    enabled                     boolean,
    recordCreationDate          timestamp,
    recordLastUpdateDate        timestamp,
    numberOfFailedLoginAttempts int4 default 0,
    tenantId                    varchar(100),
    primary key (id)
);

-- seperated statements for performance
alter table JIUser add column numberOfFailedLoginAttempts int4;
update JIUser set numberOfFailedLoginAttempts = 0;
alter table JIUser alter column numberOfFailedLoginAttempts set default 0;