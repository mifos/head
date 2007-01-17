delete from ROLES_ACTIVITY where ACTIVITY_ID = 204;
delete from ACTIVITY where ACTIVITY_ID = 204;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 920;
delete from LOOKUP_VALUE where lookup_id = 575;

delete from ROLES_ACTIVITY where ACTIVITY_ID = 203;
delete from ACTIVITY where ACTIVITY_ID = 203;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 919;
delete from LOOKUP_VALUE where lookup_id = 574;

update DATABASE_VERSION set DATABASE_VERSION = 102 where DATABASE_VERSION = 103;