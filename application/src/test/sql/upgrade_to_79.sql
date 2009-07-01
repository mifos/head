-- this update add a FOO table
create table FOO ( COL INTEGER );
insert into FOO(COL) VALUES(53);
update DATABASE_VERSION set DATABASE_VERSION = 79 where DATABASE_VERSION = 78;
