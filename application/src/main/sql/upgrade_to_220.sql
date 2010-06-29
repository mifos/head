alter table customer_position change column customer_position_id customer_position_id integer not null auto_increment;

update database_version set database_version = 220 where database_version = 219;