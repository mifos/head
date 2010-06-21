drop table if exists logmessages;

update database_version set database_version = 212 where database_version = 211;
