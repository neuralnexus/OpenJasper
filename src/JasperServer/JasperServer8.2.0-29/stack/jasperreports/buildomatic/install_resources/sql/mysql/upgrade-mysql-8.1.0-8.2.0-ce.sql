--
--
-- 8.1.0 to 8.2.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--
-- login/lockout
create table JIExternalUserLoginEvents (
    id                          bigint       not null auto_increment,
    username                    varchar(100) not null,
    enabled                     bit,
    recordCreationDate          datetime,
    recordLastUpdateDate        datetime,
    numberOfFailedLoginAttempts integer default 0,
    tenantId                    varchar(255),
    primary key (id)
) engine=InnoDB;

-- seperated statements for performance
alter table JIUser add column numberOfFailedLoginAttempts integer;
update JIUser set numberOfFailedLoginAttempts = 0;
alter table JIUser alter numberOfFailedLoginAttempts set default 0;
