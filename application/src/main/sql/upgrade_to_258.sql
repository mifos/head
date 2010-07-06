create table question_group(
  id integer auto_increment not null,
  title varchar(200) not null,
  date_of_creation date not null,
  state integer not null,
  primary key (id)
)engine=innodb character set utf8;

update database_version set database_version = 258 where database_version = 257;
