-- Postgresql 8.1 has old syntax for granting privileges on a sequence
-- The syntax varies between 8.1, [8.2, 8.3, 8.4], and 9.0
-- This script is an example for use under postgresql 8.1 (and earlier) only
-- 

GRANT ALL PRIVILEGES ON hibernate_sequence TO jasperdb;

