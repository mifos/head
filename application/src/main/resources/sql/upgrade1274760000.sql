create table foo (
foo_id integer,
description varchar(25),
primary key(foo_id)

)
engine=innodb character set utf8;

insert into foo values(1, 'BAR');
insert into foo values(2, 'BAZ');
