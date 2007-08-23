DROP TABLE IF EXISTS LOAN_MONITORING;

/*
The table LOAN_MONITORING will contain the default behavior of the group loan with individual monitoring on the MFI - Configuration
This information can not be configured through the UI, so it does need to be configured in the script
*/

CREATE TABLE LOAN_MONITORING (
  LOAN_MONITORING_ID INTEGER NOT NULL,
  INDIVIDUAL_MONITORING_FLAG SMALLINT NOT NULL,
  CREATED_BY SMALLINT,
  CREATED_DATE DATE,
  UPDATED_BY SMALLINT,
  UPDATED_DATE DATE,
  VERSION_NO INTEGER NOT NULL,
  PRIMARY KEY(LOAN_MONITORING_ID)
)
ENGINE=InnoDB CHARACTER SET utf8;

-- 0 by default: Don't Allow group loan account with individual monitoring
-- 1: Allow group loan account with individual monitoring

INSERT INTO LOAN_MONITORING (LOAN_MONITORING_ID,INDIVIDUAL_MONITORING_FLAG,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,VERSION_NO)
VALUES(1,0,NULL,NULL,NULL,NULL,1);

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 148 WHERE DATABASE_VERSION = 147;