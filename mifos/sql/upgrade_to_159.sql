
-- Add the ability to configure a min and max interval of days
-- between loan disbursal and loan schedule start date if the 
-- MFI requires this. For ENDA, this is a range of 28-31 days. 

INSERT INTO CONFIG_KEY_VALUE_INTEGER(CONFIGURATION_KEY, CONFIGURATION_VALUE) 
VALUES ('minDaysBetweenDisbursalAndFirstRepaymentDay',1);

INSERT INTO CONFIG_KEY_VALUE_INTEGER(CONFIGURATION_KEY, CONFIGURATION_VALUE) 
VALUES ('maxDaysBetweenDisbursalAndFirstRepaymentDay',31);

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 159 WHERE DATABASE_VERSION = 158;