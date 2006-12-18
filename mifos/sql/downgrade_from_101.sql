delete from ROLES_ACTIVITY where ACTIVITY_ID = 201;
delete from ACTIVITY where ACTIVITY_ID = 201;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 914;
delete from LOOKUP_VALUE where lookup_id = 569;

update DATABASE_VERSION set DATABASE_VERSION = 100 where DATABASE_VERSION = 101;
