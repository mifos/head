alter table sections_questions add column sequence_number integer not null;
update database_version set database_version = 261 where database_version = 260;