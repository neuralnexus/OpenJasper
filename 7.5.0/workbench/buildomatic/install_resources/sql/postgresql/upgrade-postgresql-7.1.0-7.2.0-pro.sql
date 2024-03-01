--
--
-- 7.1.0 to 7.2.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

-- Legacy Dashboards Removal
DROP TABLE JIDashboardFrameProperty;
DROP TABLE JIDashboardResource;
DROP TABLE JIDashboard;

DELETE FROM JIObjectPermission
WHERE uri IN (
  SELECT concat('repo:', f.uri, '/', r.name) AS fulluri
  FROM JIResource r
  JOIN JIResourceFolder f ON r.parent_folder = f.id
  WHERE resourcetype = 'com.jaspersoft.ji.adhoc.DashboardResource'
);

DELETE FROM JIResource WHERE resourcetype = 'com.jaspersoft.ji.adhoc.DashboardResource';

-- Scheduler Upgrade
ALTER TABLE qrtz_fired_triggers ADD COLUMN sched_time BIGINT;
UPDATE qrtz_fired_triggers SET sched_time = fired_time;
ALTER TABLE qrtz_fired_triggers ALTER COLUMN sched_time SET NOT NULL;