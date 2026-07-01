#!/bin/bash

echo "Resetting Smart Recycler development database..."

docker exec -i smart-recycler-mysql mysql -urecycler -precycler smart_recycler <<'SQL'
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE device_commands;
TRUNCATE TABLE sorting_results;
TRUNCATE TABLE classification_logs;
TRUNCATE TABLE error_events;
TRUNCATE TABLE bins;
TRUNCATE TABLE devices;

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE devices MODIFY status VARCHAR(255);

ALTER TABLE error_events MODIFY error_type VARCHAR(255);
ALTER TABLE error_events MODIFY severity VARCHAR(255);
ALTER TABLE error_events MODIFY event_status VARCHAR(255);

ALTER TABLE sorting_results MODIFY action VARCHAR(255);
ALTER TABLE sorting_results MODIFY status VARCHAR(255);

ALTER TABLE device_commands MODIFY command_type VARCHAR(255);
ALTER TABLE device_commands MODIFY command_status VARCHAR(255);
SQL

echo "Database reset completed."
