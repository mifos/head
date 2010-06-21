-- this update add a FOO table
create table foo ( col integer );
insert into foo(col) values(53);
update database_version set database_version = 79 where database_version = 78;
