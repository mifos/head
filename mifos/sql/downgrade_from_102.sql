delete from ROLES_ACTIVITY where ACTIVITY_ID = 202;
delete from ACTIVITY where ACTIVITY_ID = 202;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 915;
delete from LOOKUP_VALUE where lookup_id = 570;

delete from ACCOUNT_STATE_FLAG where FLAG_ID = 7;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 916;
delete from LOOKUP_VALUE where lookup_id = 571;

delete from ACCOUNT_ACTION where ACCOUNT_ACTION_ID=18;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 917;
delete from LOOKUP_VALUE where lookup_id = 572;

delete from ACCOUNT_ACTION where ACCOUNT_ACTION_ID=19;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 918;
delete from LOOKUP_VALUE where lookup_id = 573;

update DATABASE_VERSION set DATABASE_VERSION = 101 where DATABASE_VERSION = 102;