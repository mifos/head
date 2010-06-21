alter table account add column external_id varchar(100);

update database_version set database_version = 218 where database_version = 217;
