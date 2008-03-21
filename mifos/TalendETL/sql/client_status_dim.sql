create table client_status_dim (
  status_id smallint(4) not null auto_increment,
  status_description varchar(50) not null default '',
  primary key  (status_id)
) engine=innodb default charset=utf8;