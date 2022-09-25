insert into job_lock(id, name, status, used_by, created_at, updated_at) values
(1000, 'SYNC_JOB_LOCK', 'NEW', '',current_timestamp,current_timestamp);

insert into sync_position(id, block_number, last_updated_date) values
(1000, 0, current_timestamp);

insert into account(id, name, address, balance, created_date, last_updated_date) values
(101, 'Owner', '0xfb7952df13212e47e33ae961610c4b19da895fa2',20000, current_timestamp,current_timestamp);