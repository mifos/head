-- remove old configuration table
DROP TABLE IF EXISTS LOAN_MONITORING;

-- To allow group loan account with individual monitoring
INSERT INTO CONFIG_KEY_VALUE_INTEGER(CONFIGURATION_KEY, CONFIGURATION_VALUE) VALUES ('loanIndividualMonitoringIsEnabled',0);

-- To allow loan repayment schedules and loan disbursal dates independent of meeting schedules.
INSERT INTO CONFIG_KEY_VALUE_INTEGER(CONFIGURATION_KEY, CONFIGURATION_VALUE) VALUES ('repaymentSchedulesIndependentOfMeetingIsEnabled',0);



UPDATE DATABASE_VERSION SET DATABASE_VERSION = 149 WHERE DATABASE_VERSION = 148;