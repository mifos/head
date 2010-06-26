create table imported_transactions_files 
( file_name varchar(100) not null, 
  submitted_on timestamp not null, 
  submitted_by smallint not null,
  primary key (file_name),
  foreign key (submitted_by) 
  references personnel(personnel_id) 
  on delete no action 
  on update no action
)engine=innodb character set utf8;

update database_version set database_version = 221 where database_version = 220;