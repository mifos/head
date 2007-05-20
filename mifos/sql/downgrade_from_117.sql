delete from report_jasper_map where REPORT_ID = 28;
delete from REPORT where REPORT_ID = 28;

delete from ROLES_ACTIVITY where ACTIVITY_ID = 207;
delete from ACTIVITY where ACTIVITY_ID = 207;
delete from LOOKUP_VALUE_LOCALE where LOOKUP_VALUE_ID = 926;
delete from LOOKUP_VALUE where lookup_id = 581;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 116 WHERE DATABASE_VERSION = 117;