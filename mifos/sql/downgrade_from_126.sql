delete from ROLES_ACTIVITY where ACTIVITY_ID = 215;
delete from ACTIVITY where ACTIVITY_ID = 215;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 934;
delete from LOOKUP_VALUE where lookup_id = 589;

delete from ROLES_ACTIVITY where ACTIVITY_ID = 216;
delete from ACTIVITY where ACTIVITY_ID = 216;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 935;
delete from LOOKUP_VALUE where lookup_id = 590;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 125 WHERE DATABASE_VERSION = 126;