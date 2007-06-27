delete from ROLES_ACTIVITY where ACTIVITY_ID = 217;
delete from ACTIVITY where ACTIVITY_ID = 217;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 936;
delete from LOOKUP_VALUE where lookup_id = 591;

update DATABASE_VERSION set DATABASE_VERSION = 129 where DATABASE_VERSION = 130;