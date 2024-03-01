--
--
-- 7.1.0 to 7.2.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

-- Scheduler Upgrade
ALTER TABLE qrtz_fired_triggers ADD COLUMN sched_time BIGINT;
UPDATE qrtz_fired_triggers SET sched_time = fired_time;
ALTER TABLE qrtz_fired_triggers ALTER COLUMN sched_time SET NOT NULL;