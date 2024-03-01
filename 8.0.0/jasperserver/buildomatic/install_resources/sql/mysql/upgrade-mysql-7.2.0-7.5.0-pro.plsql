--
--
-- 7.2.0 to 7.5.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

-- Change column type from "varchar(100)" to "varchar(150)"
ALTER TABLE JIAwsDatasource MODIFY COLUMN accessKey varchar(150)
/

-- Change column type from "varchar(100)" to "varchar(255)"
ALTER TABLE JIAwsDatasource MODIFY COLUMN secretKey varchar(255)
/
-- drop foreign key constraints from JIAccessEvent to JIResource and JIUser
-- there's no "drop if exists" in mysql; for other db's I was able to work with cursors
-- but MySQL cursor capabilities are a mess; best way is to create a procedure

DROP PROCEDURE IF EXISTS PROC_DROP_FOREIGN_KEY
/
CREATE PROCEDURE PROC_DROP_FOREIGN_KEY(IN tableName VARCHAR(64), IN constraintName VARCHAR(64))
BEGIN
   IF EXISTS(
       SELECT * FROM information_schema.table_constraints
       WHERE
           table_schema    = DATABASE()     AND
           table_name      = tableName      AND
           constraint_name = constraintName AND
           constraint_type = 'FOREIGN KEY')
   THEN
       -- the DDL to create the constraint also creates an index with the same name
       -- so we are assuming that if the constraint is present, the index will be as well
       SET @query = CONCAT('ALTER TABLE ', tableName, ' DROP FOREIGN KEY ', constraintName);
       PREPARE stmt FROM @query;
       EXECUTE stmt;
       DEALLOCATE PREPARE stmt;
       SET @query2 = CONCAT('ALTER TABLE ', tableName, ' DROP INDEX ', constraintName);
       PREPARE stmt2 FROM @query2;
       EXECUTE stmt2;
       DEALLOCATE PREPARE stmt2;
   END IF;
END
/

call PROC_DROP_FOREIGN_KEY('JIAccessEvent', 'FK7caj87u72rymu6805gtek03y8')
/
call PROC_DROP_FOREIGN_KEY('JIAccessEvent', 'FK8lqavxfshc29dnw97io0t6wbf')
/
call PROC_DROP_FOREIGN_KEY('JIAccessEvent', 'FK47FB3CD732282198')
/
call PROC_DROP_FOREIGN_KEY('JIAccessEvent', 'FK47FB3CD7F254B53E')
/

