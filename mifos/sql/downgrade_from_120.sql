delete from report_jasper_map where REPORT_ID = 29;
delete from REPORT where REPORT_ID = 29;

delete from ROLES_ACTIVITY where ACTIVITY_ID = 212;
delete from ACTIVITY where ACTIVITY_ID = 212;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 931;
delete from LOOKUP_VALUE where lookup_id = 586;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 119 WHERE DATABASE_VERSION = 120;
