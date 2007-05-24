delete from ROLES_ACTIVITY where ACTIVITY_ID = 211;
delete from ACTIVITY where ACTIVITY_ID = 211;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 930;
delete from LOOKUP_VALUE where lookup_id = 585;

delete from ROLES_ACTIVITY where ACTIVITY_ID = 210;
delete from ACTIVITY where ACTIVITY_ID = 210;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 929;
delete from LOOKUP_VALUE where lookup_id = 584;

delete from ROLES_ACTIVITY where ACTIVITY_ID = 209;
delete from ACTIVITY where ACTIVITY_ID = 209;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 928;
delete from LOOKUP_VALUE where lookup_id = 583;

update DATABASE_VERSION set DATABASE_VERSION = 118 where DATABASE_VERSION = 119;
