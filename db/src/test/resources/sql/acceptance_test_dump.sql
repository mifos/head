
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_EXECUTION (
  job_execution_id bigint(20) NOT NULL,
  version bigint(20) DEFAULT NULL,
  job_instance_id bigint(20) NOT NULL,
  create_time datetime NOT NULL,
  start_time datetime DEFAULT NULL,
  end_time datetime DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  exit_code varchar(20) DEFAULT NULL,
  exit_message varchar(2500) DEFAULT NULL,
  last_updated datetime DEFAULT NULL,
  PRIMARY KEY (job_execution_id),
  KEY job_inst_exec_fk (job_instance_id),
  CONSTRAINT job_inst_exec_fk FOREIGN KEY (job_instance_id) REFERENCES BATCH_JOB_INSTANCE (job_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_EXECUTION DISABLE KEYS */;
INSERT INTO BATCH_JOB_EXECUTION VALUES (1,2,1,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_JOB_EXECUTION VALUES (2,2,2,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_JOB_EXECUTION VALUES (3,2,3,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_JOB_EXECUTION VALUES (4,2,4,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_JOB_EXECUTION VALUES (5,2,5,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_JOB_EXECUTION VALUES (6,2,6,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:33','COMPLETED','COMPLETED','','2011-02-22 10:46:33');
INSERT INTO BATCH_JOB_EXECUTION VALUES (7,2,7,'2011-02-22 10:46:33','2011-02-22 10:46:33','2011-02-22 10:46:34','COMPLETED','COMPLETED','','2011-02-22 10:46:34');
INSERT INTO BATCH_JOB_EXECUTION VALUES (8,2,8,'2011-02-22 10:46:34','2011-02-22 10:46:34','2011-02-22 10:46:34','COMPLETED','COMPLETED','','2011-02-22 10:46:34');
/*!40000 ALTER TABLE BATCH_JOB_EXECUTION ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_CONTEXT;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
  job_execution_id bigint(20) NOT NULL,
  short_context varchar(2500) NOT NULL,
  serialized_context text,
  PRIMARY KEY (job_execution_id),
  CONSTRAINT job_exec_ctx_fk FOREIGN KEY (job_execution_id) REFERENCES BATCH_JOB_EXECUTION (job_execution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_EXECUTION_CONTEXT DISABLE KEYS */;
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (1,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (2,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (3,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (4,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (5,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (6,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (7,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT VALUES (8,'{\"map\":[\"\"]}',NULL);
/*!40000 ALTER TABLE BATCH_JOB_EXECUTION_CONTEXT ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_SEQ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_EXECUTION_SEQ (
  id bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_EXECUTION_SEQ DISABLE KEYS */;
INSERT INTO BATCH_JOB_EXECUTION_SEQ VALUES (8);
/*!40000 ALTER TABLE BATCH_JOB_EXECUTION_SEQ ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_JOB_INSTANCE;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_INSTANCE (
  job_instance_id bigint(20) NOT NULL,
  version bigint(20) DEFAULT NULL,
  job_name varchar(100) NOT NULL,
  job_key varchar(32) NOT NULL,
  PRIMARY KEY (job_instance_id),
  UNIQUE KEY job_inst_un (job_name,job_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_INSTANCE DISABLE KEYS */;
INSERT INTO BATCH_JOB_INSTANCE VALUES (1,0,'ApplyCustomerFeeChangesTaskJob','8e2ed1aee6f87f171f9836be205e7133');
INSERT INTO BATCH_JOB_INSTANCE VALUES (2,0,'ApplyHolidayChangesTaskJob','8e2ed1aee6f87f171f9836be205e7133');
INSERT INTO BATCH_JOB_INSTANCE VALUES (3,0,'GenerateMeetingsForCustomerAndSavingsTaskJob','8e2ed1aee6f87f171f9836be205e7133');
INSERT INTO BATCH_JOB_INSTANCE VALUES (4,0,'LoanArrearsAgingTaskJob','8e2ed1aee6f87f171f9836be205e7133');
INSERT INTO BATCH_JOB_INSTANCE VALUES (5,0,'LoanArrearsAndPortfolioAtRiskTaskJob','8e2ed1aee6f87f171f9836be205e7133');
INSERT INTO BATCH_JOB_INSTANCE VALUES (6,0,'BranchReportTaskJob','a1b211bdafbdc1031d867bf1fb40e2ee');
INSERT INTO BATCH_JOB_INSTANCE VALUES (7,0,'ProductStatusJob','a1b211bdafbdc1031d867bf1fb40e2ee');
INSERT INTO BATCH_JOB_INSTANCE VALUES (8,0,'SavingsIntPostingTaskJob','a1b211bdafbdc1031d867bf1fb40e2ee');
/*!40000 ALTER TABLE BATCH_JOB_INSTANCE ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_JOB_PARAMS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_PARAMS (
  job_instance_id bigint(20) NOT NULL,
  type_cd varchar(6) NOT NULL,
  key_name varchar(100) NOT NULL,
  string_val varchar(250) DEFAULT NULL,
  date_val datetime DEFAULT NULL,
  long_val bigint(20) DEFAULT NULL,
  double_val double DEFAULT NULL,
  KEY job_inst_params_fk (job_instance_id),
  CONSTRAINT job_inst_params_fk FOREIGN KEY (job_instance_id) REFERENCES BATCH_JOB_INSTANCE (job_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_PARAMS DISABLE KEYS */;
INSERT INTO BATCH_JOB_PARAMS VALUES (1,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0);
INSERT INTO BATCH_JOB_PARAMS VALUES (2,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0);
INSERT INTO BATCH_JOB_PARAMS VALUES (3,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0);
INSERT INTO BATCH_JOB_PARAMS VALUES (4,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0);
INSERT INTO BATCH_JOB_PARAMS VALUES (5,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0);
INSERT INTO BATCH_JOB_PARAMS VALUES (6,'LONG','executionTime','','1970-01-01 05:30:00',1298351792032,0);
INSERT INTO BATCH_JOB_PARAMS VALUES (7,'LONG','executionTime','','1970-01-01 05:30:00',1298351792032,0);
INSERT INTO BATCH_JOB_PARAMS VALUES (8,'LONG','executionTime','','1970-01-01 05:30:00',1298351792032,0);
/*!40000 ALTER TABLE BATCH_JOB_PARAMS ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_JOB_SEQ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_SEQ (
  id bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_SEQ DISABLE KEYS */;
INSERT INTO BATCH_JOB_SEQ VALUES (8);
/*!40000 ALTER TABLE BATCH_JOB_SEQ ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_STEP_EXECUTION (
  step_execution_id bigint(20) NOT NULL,
  version bigint(20) NOT NULL,
  step_name varchar(100) NOT NULL,
  job_execution_id bigint(20) NOT NULL,
  start_time datetime NOT NULL,
  end_time datetime DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  commit_count bigint(20) DEFAULT NULL,
  read_count bigint(20) DEFAULT NULL,
  filter_count bigint(20) DEFAULT NULL,
  write_count bigint(20) DEFAULT NULL,
  read_skip_count bigint(20) DEFAULT NULL,
  write_skip_count bigint(20) DEFAULT NULL,
  process_skip_count bigint(20) DEFAULT NULL,
  rollback_count bigint(20) DEFAULT NULL,
  exit_code varchar(20) DEFAULT NULL,
  exit_message varchar(2500) DEFAULT NULL,
  last_updated datetime DEFAULT NULL,
  PRIMARY KEY (step_execution_id),
  KEY job_exec_step_fk (job_execution_id),
  CONSTRAINT job_exec_step_fk FOREIGN KEY (job_execution_id) REFERENCES BATCH_JOB_EXECUTION (job_execution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_STEP_EXECUTION DISABLE KEYS */;
INSERT INTO BATCH_STEP_EXECUTION VALUES (1,3,'ApplyCustomerFeeChangesTask-step-1',1,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_STEP_EXECUTION VALUES (2,3,'ApplyHolidayChangesTask-step-1',2,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_STEP_EXECUTION VALUES (3,3,'GenerateMeetingsForCustomerAndSavingsTask-step-1',3,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_STEP_EXECUTION VALUES (4,3,'LoanArrearsAgingTask-step-1',4,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_STEP_EXECUTION VALUES (5,3,'LoanArrearsAndPortfolioAtRiskTask-step-1',5,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_STEP_EXECUTION VALUES (6,3,'LoanArrearsAndPortfolioAtRiskTask-step-2',5,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_STEP_EXECUTION VALUES (7,3,'BranchReportTask-step-1',6,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32');
INSERT INTO BATCH_STEP_EXECUTION VALUES (8,3,'ProductStatus-step-1',7,'2011-02-22 10:46:33','2011-02-22 10:46:34','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:34');
INSERT INTO BATCH_STEP_EXECUTION VALUES (9,3,'SavingsIntPostingTask-step-1',8,'2011-02-22 10:46:34','2011-02-22 10:46:34','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:34');
/*!40000 ALTER TABLE BATCH_STEP_EXECUTION ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_CONTEXT;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
  step_execution_id bigint(20) NOT NULL,
  short_context varchar(2500) NOT NULL,
  serialized_context text,
  PRIMARY KEY (step_execution_id),
  CONSTRAINT step_exec_ctx_fk FOREIGN KEY (step_execution_id) REFERENCES BATCH_STEP_EXECUTION (step_execution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_STEP_EXECUTION_CONTEXT DISABLE KEYS */;
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (1,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (2,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (3,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (4,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (5,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (6,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (7,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (8,'{\"map\":[\"\"]}',NULL);
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT VALUES (9,'{\"map\":[\"\"]}',NULL);
/*!40000 ALTER TABLE BATCH_STEP_EXECUTION_CONTEXT ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_SEQ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_STEP_EXECUTION_SEQ (
  id bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_STEP_EXECUTION_SEQ DISABLE KEYS */;
INSERT INTO BATCH_STEP_EXECUTION_SEQ VALUES (9);
/*!40000 ALTER TABLE BATCH_STEP_EXECUTION_SEQ ENABLE KEYS */;
DROP TABLE IF EXISTS DATABASECHANGELOG;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE DATABASECHANGELOG (
  ID varchar(63) NOT NULL,
  AUTHOR varchar(63) NOT NULL,
  FILENAME varchar(200) NOT NULL,
  DATEEXECUTED datetime NOT NULL,
  ORDEREXECUTED int(11) NOT NULL,
  EXECTYPE varchar(10) NOT NULL,
  MD5SUM varchar(35) DEFAULT NULL,
  DESCRIPTION varchar(255) DEFAULT NULL,
  COMMENTS varchar(255) DEFAULT NULL,
  TAG varchar(255) DEFAULT NULL,
  LIQUIBASE varchar(20) DEFAULT NULL,
  PRIMARY KEY (ID,AUTHOR,FILENAME)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE DATABASECHANGELOG DISABLE KEYS */;
INSERT INTO DATABASECHANGELOG VALUES ('MIFOS-4633_1','Van Mittal-Henkle','changesets/changelog-Release_G.xml','2011-03-16 16:00:35',1,'EXECUTED','3:b47549abaeb58b3bde90195d5e8e65b8','Create Table','',NULL,'2.0.0');
INSERT INTO DATABASECHANGELOG VALUES ('MIFOS-4633_2','Van Mittal-Henkle','changesets/changelog-Release_G.xml','2011-03-16 16:00:35',2,'EXECUTED','3:0a7e597a8de1dba32d857ba4f29bac59','Custom SQL','',NULL,'2.0.0');
/*!40000 ALTER TABLE DATABASECHANGELOG ENABLE KEYS */;
DROP TABLE IF EXISTS DATABASECHANGELOGLOCK;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE DATABASECHANGELOGLOCK (
  ID int(11) NOT NULL,
  LOCKED tinyint(1) NOT NULL,
  LOCKGRANTED datetime DEFAULT NULL,
  LOCKEDBY varchar(255) DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE DATABASECHANGELOGLOCK DISABLE KEYS */;
INSERT INTO DATABASECHANGELOGLOCK VALUES (1,0,NULL,NULL);
/*!40000 ALTER TABLE DATABASECHANGELOGLOCK ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_BLOB_TRIGGERS (
  trigger_name varchar(200) NOT NULL,
  trigger_group varchar(200) NOT NULL,
  blob_data blob,
  PRIMARY KEY (trigger_name,trigger_group),
  CONSTRAINT QRTZ_BLOB_TRIGGERS_ibfk_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS (trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_BLOB_TRIGGERS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_BLOB_TRIGGERS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_CALENDARS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_CALENDARS (
  calendar_name varchar(200) NOT NULL,
  calendar blob NOT NULL,
  PRIMARY KEY (calendar_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_CALENDARS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_CALENDARS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_CRON_TRIGGERS (
  trigger_name varchar(200) NOT NULL,
  trigger_group varchar(200) NOT NULL,
  cron_expression varchar(200) NOT NULL,
  time_zone_id varchar(80) DEFAULT NULL,
  PRIMARY KEY (trigger_name,trigger_group),
  CONSTRAINT QRTZ_CRON_TRIGGERS_ibfk_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS (trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_CRON_TRIGGERS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_CRON_TRIGGERS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_FIRED_TRIGGERS (
  entry_id varchar(95) NOT NULL,
  trigger_name varchar(200) NOT NULL,
  trigger_group varchar(200) NOT NULL,
  is_volatile varchar(1) NOT NULL,
  instance_name varchar(200) NOT NULL,
  fired_time bigint(13) NOT NULL,
  priority int(11) NOT NULL,
  state varchar(16) NOT NULL,
  job_name varchar(200) DEFAULT NULL,
  job_group varchar(200) DEFAULT NULL,
  is_stateful varchar(1) DEFAULT NULL,
  requests_recovery varchar(1) DEFAULT NULL,
  PRIMARY KEY (entry_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_FIRED_TRIGGERS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_FIRED_TRIGGERS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_JOB_DETAILS (
  job_name varchar(200) NOT NULL,
  job_group varchar(200) NOT NULL,
  description varchar(250) DEFAULT NULL,
  job_class_name varchar(250) NOT NULL,
  is_durable varchar(1) NOT NULL,
  is_volatile varchar(1) NOT NULL,
  is_stateful varchar(1) NOT NULL,
  requests_recovery varchar(1) NOT NULL,
  job_data blob,
  PRIMARY KEY (job_name,job_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_JOB_DETAILS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_JOB_DETAILS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_JOB_LISTENERS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_JOB_LISTENERS (
  job_name varchar(200) NOT NULL,
  job_group varchar(200) NOT NULL,
  job_listener varchar(200) NOT NULL,
  PRIMARY KEY (job_name,job_group,job_listener),
  CONSTRAINT QRTZ_JOB_LISTENERS_ibfk_1 FOREIGN KEY (job_name, job_group) REFERENCES QRTZ_JOB_DETAILS (job_name, job_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_JOB_LISTENERS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_JOB_LISTENERS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_LOCKS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_LOCKS (
  lock_name varchar(40) NOT NULL,
  PRIMARY KEY (lock_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_LOCKS DISABLE KEYS */;
INSERT INTO QRTZ_LOCKS VALUES ('CALENDAR_ACCESS');
INSERT INTO QRTZ_LOCKS VALUES ('JOB_ACCESS');
INSERT INTO QRTZ_LOCKS VALUES ('MISFIRE_ACCESS');
INSERT INTO QRTZ_LOCKS VALUES ('STATE_ACCESS');
INSERT INTO QRTZ_LOCKS VALUES ('TRIGGER_ACCESS');
/*!40000 ALTER TABLE QRTZ_LOCKS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
  trigger_group varchar(200) NOT NULL,
  PRIMARY KEY (trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_SCHEDULER_STATE (
  instance_name varchar(200) NOT NULL,
  last_checkin_time bigint(13) NOT NULL,
  checkin_interval bigint(13) NOT NULL,
  PRIMARY KEY (instance_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_SCHEDULER_STATE DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_SCHEDULER_STATE ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
  trigger_name varchar(200) NOT NULL,
  trigger_group varchar(200) NOT NULL,
  repeat_count bigint(7) NOT NULL,
  repeat_interval bigint(12) NOT NULL,
  times_triggered bigint(10) NOT NULL,
  PRIMARY KEY (trigger_name,trigger_group),
  CONSTRAINT QRTZ_SIMPLE_TRIGGERS_ibfk_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS (trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_SIMPLE_TRIGGERS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_SIMPLE_TRIGGERS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_TRIGGERS (
  trigger_name varchar(200) NOT NULL,
  trigger_group varchar(200) NOT NULL,
  job_name varchar(200) NOT NULL,
  job_group varchar(200) NOT NULL,
  is_volatile varchar(1) NOT NULL,
  description varchar(250) DEFAULT NULL,
  next_fire_time bigint(13) DEFAULT NULL,
  prev_fire_time bigint(13) DEFAULT NULL,
  priority int(11) DEFAULT NULL,
  trigger_state varchar(16) NOT NULL,
  trigger_type varchar(8) NOT NULL,
  start_time bigint(13) NOT NULL,
  end_time bigint(13) DEFAULT NULL,
  calendar_name varchar(200) DEFAULT NULL,
  misfire_instr smallint(2) DEFAULT NULL,
  job_data blob,
  PRIMARY KEY (trigger_name,trigger_group),
  KEY job_name (job_name,job_group),
  CONSTRAINT QRTZ_TRIGGERS_ibfk_1 FOREIGN KEY (job_name, job_group) REFERENCES QRTZ_JOB_DETAILS (job_name, job_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_TRIGGERS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_TRIGGERS ENABLE KEYS */;
DROP TABLE IF EXISTS QRTZ_TRIGGER_LISTENERS;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE QRTZ_TRIGGER_LISTENERS (
  trigger_name varchar(200) NOT NULL,
  trigger_group varchar(200) NOT NULL,
  trigger_listener varchar(200) NOT NULL,
  PRIMARY KEY (trigger_name,trigger_group,trigger_listener),
  CONSTRAINT QRTZ_TRIGGER_LISTENERS_ibfk_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS (trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE QRTZ_TRIGGER_LISTENERS DISABLE KEYS */;
/*!40000 ALTER TABLE QRTZ_TRIGGER_LISTENERS ENABLE KEYS */;
DROP TABLE IF EXISTS accepted_payment_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE accepted_payment_type (
  accepted_payment_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  transaction_id smallint(6) NOT NULL,
  payment_type_id smallint(6) NOT NULL,
  PRIMARY KEY (accepted_payment_type_id),
  KEY transaction_id (transaction_id),
  KEY payment_type_id (payment_type_id),
  CONSTRAINT accepted_payment_type_ibfk_1 FOREIGN KEY (transaction_id) REFERENCES transaction_type (transaction_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT accepted_payment_type_ibfk_2 FOREIGN KEY (payment_type_id) REFERENCES payment_type (payment_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE accepted_payment_type DISABLE KEYS */;
INSERT INTO accepted_payment_type VALUES (1,1,1);
INSERT INTO accepted_payment_type VALUES (2,2,1);
INSERT INTO accepted_payment_type VALUES (3,3,1);
INSERT INTO accepted_payment_type VALUES (4,4,1);
INSERT INTO accepted_payment_type VALUES (5,5,1);
INSERT INTO accepted_payment_type VALUES (7,1,3);
INSERT INTO accepted_payment_type VALUES (9,2,3);
INSERT INTO accepted_payment_type VALUES (11,3,3);
INSERT INTO accepted_payment_type VALUES (13,4,3);
INSERT INTO accepted_payment_type VALUES (15,5,3);
INSERT INTO accepted_payment_type VALUES (16,1,2);
INSERT INTO accepted_payment_type VALUES (17,2,2);
INSERT INTO accepted_payment_type VALUES (18,3,2);
INSERT INTO accepted_payment_type VALUES (19,4,2);
INSERT INTO accepted_payment_type VALUES (20,5,2);
/*!40000 ALTER TABLE accepted_payment_type ENABLE KEYS */;
DROP TABLE IF EXISTS account;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account (
  account_id int(11) NOT NULL AUTO_INCREMENT,
  global_account_num varchar(100) DEFAULT NULL,
  customer_id int(11) DEFAULT NULL,
  account_state_id smallint(6) DEFAULT NULL,
  account_type_id smallint(6) NOT NULL,
  office_id smallint(6) DEFAULT NULL,
  personnel_id smallint(6) DEFAULT NULL,
  created_by smallint(6) NOT NULL,
  created_date date NOT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  closed_date date DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  offsetting_allowable smallint(6) NOT NULL,
  external_id varchar(100) DEFAULT NULL,
  PRIMARY KEY (account_id),
  UNIQUE KEY account_global_idx (global_account_num),
  KEY account_state_id (account_state_id),
  KEY account_type_id (account_type_id),
  KEY personnel_id (personnel_id),
  KEY office_id (office_id),
  KEY customer_id_account_idx (customer_id),
  CONSTRAINT account_ibfk_1 FOREIGN KEY (account_state_id) REFERENCES account_state (account_state_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_ibfk_2 FOREIGN KEY (account_type_id) REFERENCES account_type (account_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_ibfk_3 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_ibfk_4 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_ibfk_5 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account DISABLE KEYS */;
INSERT INTO account VALUES (1,'000100000000001',1,11,3,2,2,1,'2011-02-18',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (2,'000100000000002',1,16,2,2,2,1,'2011-02-18',1,'2020-01-01',NULL,8,1,NULL);
INSERT INTO account VALUES (3,'000100000000003',2,11,3,2,2,1,'2011-02-21',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (4,'000100000000004',3,11,3,2,2,1,'2011-02-21',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (5,'000100000000005',4,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL);
INSERT INTO account VALUES (6,'000100000000006',5,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL);
INSERT INTO account VALUES (7,'000100000000007',6,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL);
INSERT INTO account VALUES (8,'000100000000008',7,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL);
INSERT INTO account VALUES (9,'000100000000009',8,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL);
INSERT INTO account VALUES (10,'000100000000010',5,5,1,2,2,1,'2011-02-21',1,'2011-02-21',NULL,3,1,'');
INSERT INTO account VALUES (11,'000100000000011',4,3,1,2,2,1,'2011-02-21',1,'2011-02-21',NULL,2,1,'');
INSERT INTO account VALUES (12,'000100000000012',6,9,1,2,2,1,'2011-02-21',1,'2011-02-22',NULL,5,1,'');
INSERT INTO account VALUES (13,'000100000000013',9,11,3,2,2,1,'2011-02-18',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (14,'000100000000014',10,11,3,2,2,1,'2011-02-18',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (15,'000100000000015',10,5,1,2,2,1,'2011-02-18',1,'2011-02-18',NULL,3,1,'');
INSERT INTO account VALUES (16,'000100000000016',11,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (17,'000100000000017',12,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (18,'000100000000018',13,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (19,'000100000000019',14,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (20,'000100000000020',3,3,1,2,2,1,'2011-02-22',1,'2011-02-22',NULL,2,1,'');
INSERT INTO account VALUES (21,'000100000000021',15,11,3,2,2,1,'2011-02-24',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (22,'000100000000022',16,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (23,'000100000000023',17,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (24,'000100000000024',18,11,3,2,2,1,'2010-10-11',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (25,'000100000000025',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,'');
INSERT INTO account VALUES (26,'000100000000026',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,'');
INSERT INTO account VALUES (27,'000100000000027',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,'');
INSERT INTO account VALUES (28,'000100000000028',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,'');
INSERT INTO account VALUES (29,'000100000000029',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,'');
INSERT INTO account VALUES (30,'000100000000030',19,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (31,'000100000000031',20,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (32,'000100000000032',21,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (33,'000100000000033',22,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (34,'000100000000034',23,11,3,2,2,1,'2020-01-01',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (35,'000100000000035',23,3,1,2,2,1,'2020-01-01',1,'2020-01-01',NULL,2,1,'');
INSERT INTO account VALUES (36,'000100000000036',24,11,3,2,2,1,'2011-02-28',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (37,'000100000000037',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,5,1,'');
INSERT INTO account VALUES (38,'000100000000038',24,3,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,2,1,'');
INSERT INTO account VALUES (39,'000100000000039',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,'');
INSERT INTO account VALUES (40,'000100000000040',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,'');
INSERT INTO account VALUES (41,'000100000000041',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,'');
INSERT INTO account VALUES (42,'000100000000042',4,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,'');
INSERT INTO account VALUES (43,'000100000000043',24,2,1,2,2,1,'2011-02-28',NULL,NULL,NULL,1,1,'');
INSERT INTO account VALUES (44,'000100000000044',24,1,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,2,1,'');
INSERT INTO account VALUES (45,'000100000000045',24,3,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,2,1,'');
INSERT INTO account VALUES (46,'000100000000046',24,3,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,2,1,'');
INSERT INTO account VALUES (47,'000100000000047',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,'');
INSERT INTO account VALUES (48,'000100000000048',25,11,3,2,2,1,'2011-01-01',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (49,'000100000000049',26,11,3,2,2,1,'2011-01-01',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (50,'000100000000050',6,3,1,2,2,1,'2011-03-03',1,'2011-03-03',NULL,2,1,'');
INSERT INTO account VALUES (51,'000100000000051',27,11,3,3,3,1,'2011-03-03',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (52,'000100000000052',6,3,1,2,2,1,'2011-03-04',1,'2011-03-04',NULL,2,1,'');
INSERT INTO account VALUES (53,'000100000000053',6,2,1,2,2,1,'2011-03-04',NULL,NULL,NULL,1,1,'');
INSERT INTO account VALUES (54,'000100000000054',6,2,1,2,2,1,'2011-03-04',NULL,NULL,NULL,1,1,'');
INSERT INTO account VALUES (55,'000100000000055',6,2,1,2,2,1,'2011-03-04',NULL,NULL,NULL,1,1,'');
INSERT INTO account VALUES (56,'000100000000056',28,11,3,2,2,1,'2010-10-11',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (57,'000100000000057',29,11,3,2,2,1,'2010-10-11',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (58,'000100000000058',30,11,3,2,2,1,'2011-01-01',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (59,'000100000000059',10,16,2,2,2,1,'2011-03-08',1,'2011-03-08',NULL,2,1,NULL);
INSERT INTO account VALUES (60,'000100000000060',31,11,3,2,2,1,'2011-03-09',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (61,'000100000000061',32,11,3,3,3,1,'2011-03-10',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (62,'000100000000062',33,11,3,3,3,1,'2011-03-10',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (63,'000100000000063',34,11,3,4,4,1,'2011-03-14',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (64,'000100000000064',35,11,3,2,2,1,'2011-03-14',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (65,'000100000000065',36,11,3,2,2,1,'2011-03-14',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (66,'000100000000066',37,11,3,2,2,1,'2011-03-14',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (67,'000100000000067',37,16,2,2,2,1,'2011-03-14',1,'2011-03-14',NULL,2,1,NULL);
INSERT INTO account VALUES (68,'000100000000068',38,11,3,2,2,1,'2011-03-15',NULL,NULL,NULL,1,1,NULL);
INSERT INTO account VALUES (69,'000100000000069',39,11,3,2,2,1,'2011-03-15',NULL,NULL,NULL,2,1,NULL);
INSERT INTO account VALUES (70,'000100000000070',40,11,3,2,2,1,'2011-03-15',NULL,NULL,NULL,2,1,NULL);
/*!40000 ALTER TABLE account ENABLE KEYS */;
DROP TABLE IF EXISTS account_action;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_action (
  account_action_id smallint(6) NOT NULL,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (account_action_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT account_action_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_action DISABLE KEYS */;
INSERT INTO account_action VALUES (1,167);
INSERT INTO account_action VALUES (2,168);
INSERT INTO account_action VALUES (3,169);
INSERT INTO account_action VALUES (4,170);
INSERT INTO account_action VALUES (5,171);
INSERT INTO account_action VALUES (6,172);
INSERT INTO account_action VALUES (7,173);
INSERT INTO account_action VALUES (8,191);
INSERT INTO account_action VALUES (9,192);
INSERT INTO account_action VALUES (10,193);
INSERT INTO account_action VALUES (11,214);
INSERT INTO account_action VALUES (12,362);
INSERT INTO account_action VALUES (13,364);
INSERT INTO account_action VALUES (14,366);
INSERT INTO account_action VALUES (15,547);
INSERT INTO account_action VALUES (16,548);
INSERT INTO account_action VALUES (17,549);
INSERT INTO account_action VALUES (18,572);
INSERT INTO account_action VALUES (19,573);
INSERT INTO account_action VALUES (20,610);
/*!40000 ALTER TABLE account_action ENABLE KEYS */;
DROP TABLE IF EXISTS account_activity;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_activity (
  activity_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  personnel_id smallint(6) NOT NULL,
  activity_name varchar(50) NOT NULL,
  principal decimal(21,4) DEFAULT NULL,
  principal_currency_id smallint(6) DEFAULT NULL,
  principal_outstanding decimal(21,4) DEFAULT NULL,
  principal_outstanding_currency_id smallint(6) DEFAULT NULL,
  interest decimal(13,10) DEFAULT NULL,
  interest_currency_id smallint(6) DEFAULT NULL,
  interest_outstanding decimal(13,10) DEFAULT NULL,
  interest_outstanding_currency_id smallint(6) DEFAULT NULL,
  fee decimal(13,2) DEFAULT NULL,
  fee_currency_id smallint(6) DEFAULT NULL,
  fee_outstanding decimal(13,2) DEFAULT NULL,
  fee_outstanding_currency_id smallint(6) DEFAULT NULL,
  penalty decimal(13,10) DEFAULT NULL,
  penalty_currency_id smallint(6) DEFAULT NULL,
  penalty_outstanding decimal(13,10) DEFAULT NULL,
  penalty_outstanding_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (activity_id),
  KEY account_id (account_id),
  KEY principal_currency_id (principal_currency_id),
  KEY principal_outstanding_currency_id (principal_outstanding_currency_id),
  KEY interest_currency_id (interest_currency_id),
  KEY interest_outstanding_currency_id (interest_outstanding_currency_id),
  KEY fee_currency_id (fee_currency_id),
  KEY fee_outstanding_currency_id (fee_outstanding_currency_id),
  KEY penalty_currency_id (penalty_currency_id),
  KEY penalty_outstanding_currency_id (penalty_outstanding_currency_id),
  CONSTRAINT account_activity_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_2 FOREIGN KEY (principal_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_3 FOREIGN KEY (principal_outstanding_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_4 FOREIGN KEY (interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_5 FOREIGN KEY (interest_outstanding_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_6 FOREIGN KEY (fee_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_7 FOREIGN KEY (fee_outstanding_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_8 FOREIGN KEY (penalty_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_activity_ibfk_9 FOREIGN KEY (penalty_outstanding_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_activity DISABLE KEYS */;
/*!40000 ALTER TABLE account_activity ENABLE KEYS */;
DROP TABLE IF EXISTS account_custom_field;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_custom_field (
  account_custom_field_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  field_id smallint(6) NOT NULL,
  field_value varchar(200) DEFAULT NULL,
  PRIMARY KEY (account_custom_field_id),
  KEY field_id (field_id),
  KEY account_id (account_id),
  CONSTRAINT account_custom_field_ibfk_1 FOREIGN KEY (field_id) REFERENCES custom_field_definition (field_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_custom_field_ibfk_2 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_custom_field DISABLE KEYS */;
/*!40000 ALTER TABLE account_custom_field ENABLE KEYS */;
DROP TABLE IF EXISTS account_fees;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_fees (
  account_fee_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  fee_id smallint(6) NOT NULL,
  fee_frequency int(11) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  inherited_flag smallint(6) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  account_fee_amnt decimal(21,4) NOT NULL,
  account_fee_amnt_currency_id smallint(6) DEFAULT NULL,
  fee_amnt decimal(21,4) NOT NULL,
  fee_status smallint(6) DEFAULT NULL,
  status_change_date date DEFAULT NULL,
  version_no int(11) NOT NULL,
  last_applied_date date DEFAULT NULL,
  PRIMARY KEY (account_fee_id),
  KEY fee_id (fee_id),
  KEY account_fee_amnt_currency_id (account_fee_amnt_currency_id),
  KEY fee_frequency (fee_frequency),
  KEY account_fees_id_idx (account_id,fee_id),
  CONSTRAINT account_fees_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_fees_ibfk_2 FOREIGN KEY (fee_id) REFERENCES fees (fee_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_fees_ibfk_3 FOREIGN KEY (account_fee_amnt_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_fees_ibfk_4 FOREIGN KEY (fee_frequency) REFERENCES recurrence_detail (details_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_fees DISABLE KEYS */;
INSERT INTO account_fees VALUES (1,20,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
INSERT INTO account_fees VALUES (2,25,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09');
INSERT INTO account_fees VALUES (3,26,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09');
INSERT INTO account_fees VALUES (4,27,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09');
INSERT INTO account_fees VALUES (5,28,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09');
INSERT INTO account_fees VALUES (6,29,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-16');
INSERT INTO account_fees VALUES (7,35,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
INSERT INTO account_fees VALUES (8,37,1,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',1,'2011-02-28',0,'2011-03-07');
INSERT INTO account_fees VALUES (9,38,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
INSERT INTO account_fees VALUES (10,39,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
INSERT INTO account_fees VALUES (11,40,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
INSERT INTO account_fees VALUES (12,41,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
INSERT INTO account_fees VALUES (13,42,1,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
/*!40000 ALTER TABLE account_fees ENABLE KEYS */;
DROP TABLE IF EXISTS account_flag_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_flag_detail (
  account_flag_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  flag_id smallint(6) NOT NULL,
  created_by smallint(6) NOT NULL,
  created_date date NOT NULL,
  PRIMARY KEY (account_flag_id),
  KEY account_id (account_id),
  KEY flag_id (flag_id),
  KEY created_by (created_by),
  CONSTRAINT account_flag_detail_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_flag_detail_ibfk_2 FOREIGN KEY (flag_id) REFERENCES account_state_flag (flag_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_flag_detail_ibfk_3 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_flag_detail DISABLE KEYS */;
/*!40000 ALTER TABLE account_flag_detail ENABLE KEYS */;
DROP TABLE IF EXISTS account_notes;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_notes (
  account_notes_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  note varchar(500) NOT NULL,
  comment_date date NOT NULL,
  officer_id smallint(6) NOT NULL,
  PRIMARY KEY (account_notes_id),
  KEY account_id (account_id),
  KEY officer_id (officer_id),
  CONSTRAINT account_notes_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_notes_ibfk_2 FOREIGN KEY (officer_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_notes DISABLE KEYS */;
INSERT INTO account_notes VALUES (1,2,'Active','2011-02-18',1);
INSERT INTO account_notes VALUES (2,10,'asdfasdf','2011-02-21',1);
INSERT INTO account_notes VALUES (3,11,'Application Approved','2011-02-21',1);
INSERT INTO account_notes VALUES (4,12,'Application Approved','2011-02-21',1);
INSERT INTO account_notes VALUES (5,15,'Application Approved','2011-02-18',1);
INSERT INTO account_notes VALUES (6,20,'Application Approved','2011-02-22',1);
INSERT INTO account_notes VALUES (7,25,'Application Approved','2010-10-11',1);
INSERT INTO account_notes VALUES (8,26,'Application Approved','2010-10-11',1);
INSERT INTO account_notes VALUES (9,27,'Approved','2010-10-11',1);
INSERT INTO account_notes VALUES (10,28,'Approved','2010-10-11',1);
INSERT INTO account_notes VALUES (11,29,'Approved','2010-10-11',1);
INSERT INTO account_notes VALUES (12,35,'Application Approved','2020-01-01',1);
INSERT INTO account_notes VALUES (13,37,'Approved','2011-02-28',1);
INSERT INTO account_notes VALUES (14,38,'Application','2011-02-28',1);
INSERT INTO account_notes VALUES (15,39,'Approved','2011-02-28',1);
INSERT INTO account_notes VALUES (16,40,'Approved','2011-02-28',1);
INSERT INTO account_notes VALUES (17,41,'Approved','2011-02-28',1);
INSERT INTO account_notes VALUES (18,42,'Approved','2011-02-28',1);
INSERT INTO account_notes VALUES (19,44,'Partial','2011-02-28',1);
INSERT INTO account_notes VALUES (20,45,'Application','2011-02-28',1);
INSERT INTO account_notes VALUES (21,46,'Approved','2011-02-28',1);
INSERT INTO account_notes VALUES (22,47,'	Application Approved','2011-02-28',1);
INSERT INTO account_notes VALUES (23,50,'Approved','2011-03-03',1);
INSERT INTO account_notes VALUES (24,52,'Approved','2011-03-04',1);
INSERT INTO account_notes VALUES (25,59,'Ok','2011-03-08',1);
INSERT INTO account_notes VALUES (26,67,'Ok','2011-03-14',1);
/*!40000 ALTER TABLE account_notes ENABLE KEYS */;
DROP TABLE IF EXISTS account_payment;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_payment (
  payment_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  payment_type_id smallint(6) NOT NULL,
  currency_id smallint(6) DEFAULT NULL,
  amount decimal(21,4) NOT NULL,
  receipt_number varchar(60) DEFAULT NULL,
  voucher_number varchar(50) DEFAULT NULL,
  check_number varchar(50) DEFAULT NULL,
  payment_date date NOT NULL,
  receipt_date date DEFAULT NULL,
  bank_name varchar(50) DEFAULT NULL,
  `comment` varchar(80) DEFAULT NULL,
  PRIMARY KEY (payment_id),
  KEY currency_id (currency_id),
  KEY payment_type_id (payment_type_id),
  KEY account_id_account_payment_idx (account_id),
  CONSTRAINT account_payment_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_payment_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_payment_ibfk_3 FOREIGN KEY (payment_type_id) REFERENCES payment_type (payment_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_payment DISABLE KEYS */;
INSERT INTO account_payment VALUES (1,2,1,2,'1000.0000','',NULL,NULL,'2011-02-18',NULL,NULL,NULL);
INSERT INTO account_payment VALUES (2,10,1,2,'1000.0000','',NULL,NULL,'2011-02-21','2011-02-21',NULL,NULL);
INSERT INTO account_payment VALUES (3,15,1,2,'1000.0000','',NULL,NULL,'2011-02-18','2011-02-18',NULL,NULL);
INSERT INTO account_payment VALUES (4,12,1,2,'100000.0000','',NULL,NULL,'2010-02-22','2010-02-22',NULL,NULL);
INSERT INTO account_payment VALUES (5,25,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL);
INSERT INTO account_payment VALUES (6,26,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL);
INSERT INTO account_payment VALUES (7,27,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL);
INSERT INTO account_payment VALUES (8,28,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL);
INSERT INTO account_payment VALUES (9,29,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL);
INSERT INTO account_payment VALUES (10,37,1,2,'1000.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL);
INSERT INTO account_payment VALUES (11,39,1,2,'99990.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL);
INSERT INTO account_payment VALUES (12,40,1,2,'99990.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL);
INSERT INTO account_payment VALUES (13,41,1,2,'99990.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL);
INSERT INTO account_payment VALUES (14,42,1,2,'1000.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL);
INSERT INTO account_payment VALUES (15,47,1,2,'10000.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL);
/*!40000 ALTER TABLE account_payment ENABLE KEYS */;
DROP TABLE IF EXISTS account_state;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_state (
  account_state_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  prd_type_id smallint(6) NOT NULL,
  currently_in_use smallint(6) NOT NULL,
  status_description varchar(200) DEFAULT NULL,
  PRIMARY KEY (account_state_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT account_state_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_state DISABLE KEYS */;
INSERT INTO account_state VALUES (1,17,1,1,'Partial application');
INSERT INTO account_state VALUES (2,18,1,1,'Application pending approval');
INSERT INTO account_state VALUES (3,19,1,1,'Application approved');
INSERT INTO account_state VALUES (4,20,1,0,'Disbursed to loan officer');
INSERT INTO account_state VALUES (5,21,1,1,'Active in good standing');
INSERT INTO account_state VALUES (6,22,1,1,'Closed - obligation met');
INSERT INTO account_state VALUES (7,23,1,1,'Closed - written off');
INSERT INTO account_state VALUES (8,24,1,1,'Closed - rescheduled');
INSERT INTO account_state VALUES (9,25,1,1,'Active in bad standing');
INSERT INTO account_state VALUES (10,141,1,1,'Canceled');
INSERT INTO account_state VALUES (11,142,1,1,'Customer Account Active');
INSERT INTO account_state VALUES (12,143,1,1,'Customer Account Inactive');
INSERT INTO account_state VALUES (13,181,2,1,'Partial application');
INSERT INTO account_state VALUES (14,182,2,1,'Application pending approval');
INSERT INTO account_state VALUES (15,183,2,1,'Canceled');
INSERT INTO account_state VALUES (16,184,2,1,'Active');
INSERT INTO account_state VALUES (17,185,2,1,'Closed');
INSERT INTO account_state VALUES (18,210,2,1,'Inactive');
/*!40000 ALTER TABLE account_state ENABLE KEYS */;
DROP TABLE IF EXISTS account_state_flag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_state_flag (
  flag_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  status_id smallint(6) NOT NULL,
  flag_description varchar(200) DEFAULT NULL,
  retain_flag smallint(6) NOT NULL,
  PRIMARY KEY (flag_id),
  KEY status_id (status_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT account_state_flag_ibfk_1 FOREIGN KEY (status_id) REFERENCES account_state (account_state_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_state_flag_ibfk_2 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_state_flag DISABLE KEYS */;
INSERT INTO account_state_flag VALUES (1,174,10,'Withdraw',0);
INSERT INTO account_state_flag VALUES (2,175,10,'Rejected',0);
INSERT INTO account_state_flag VALUES (3,176,10,'Other',0);
INSERT INTO account_state_flag VALUES (4,211,15,'Withdraw',0);
INSERT INTO account_state_flag VALUES (5,212,15,'Rejected',0);
INSERT INTO account_state_flag VALUES (6,213,15,'Blacklisted',1);
INSERT INTO account_state_flag VALUES (7,571,10,'Loan reversal',0);
/*!40000 ALTER TABLE account_state_flag ENABLE KEYS */;
DROP TABLE IF EXISTS account_status_change_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_status_change_history (
  account_status_change_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  old_status smallint(6) DEFAULT NULL,
  new_status smallint(6) NOT NULL,
  changed_by smallint(6) NOT NULL,
  changed_date date NOT NULL,
  PRIMARY KEY (account_status_change_id),
  KEY account_id (account_id),
  KEY old_status (old_status),
  KEY new_status (new_status),
  KEY changed_by (changed_by),
  CONSTRAINT account_status_change_history_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_status_change_history_ibfk_2 FOREIGN KEY (old_status) REFERENCES account_state (account_state_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_status_change_history_ibfk_3 FOREIGN KEY (new_status) REFERENCES account_state (account_state_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_status_change_history_ibfk_4 FOREIGN KEY (changed_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_status_change_history DISABLE KEYS */;
INSERT INTO account_status_change_history VALUES (1,2,NULL,14,1,'2011-02-18');
INSERT INTO account_status_change_history VALUES (2,2,14,16,1,'2011-02-18');
INSERT INTO account_status_change_history VALUES (3,10,2,2,1,'2011-02-21');
INSERT INTO account_status_change_history VALUES (4,10,2,3,1,'2011-02-21');
INSERT INTO account_status_change_history VALUES (5,11,2,2,1,'2011-02-21');
INSERT INTO account_status_change_history VALUES (6,11,2,3,1,'2011-02-21');
INSERT INTO account_status_change_history VALUES (7,12,2,2,1,'2011-02-21');
INSERT INTO account_status_change_history VALUES (8,12,2,3,1,'2011-02-21');
INSERT INTO account_status_change_history VALUES (9,10,3,5,1,'2011-02-21');
INSERT INTO account_status_change_history VALUES (10,15,2,2,1,'2011-02-18');
INSERT INTO account_status_change_history VALUES (11,15,2,3,1,'2011-02-18');
INSERT INTO account_status_change_history VALUES (12,15,3,5,1,'2011-02-18');
INSERT INTO account_status_change_history VALUES (13,12,3,5,1,'2010-02-22');
INSERT INTO account_status_change_history VALUES (14,12,5,9,2,'2011-02-22');
INSERT INTO account_status_change_history VALUES (15,20,2,2,1,'2011-02-22');
INSERT INTO account_status_change_history VALUES (16,20,2,3,1,'2011-02-22');
INSERT INTO account_status_change_history VALUES (17,25,2,2,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (18,25,2,3,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (19,25,3,5,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (20,26,2,2,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (21,26,2,3,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (22,26,3,5,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (23,27,2,2,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (24,27,2,3,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (25,27,3,5,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (26,28,2,2,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (27,28,2,3,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (28,28,3,5,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (29,29,2,2,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (30,29,2,3,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (31,29,3,5,1,'2010-10-11');
INSERT INTO account_status_change_history VALUES (32,35,2,2,1,'2020-01-01');
INSERT INTO account_status_change_history VALUES (33,35,2,3,1,'2020-01-01');
INSERT INTO account_status_change_history VALUES (34,37,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (35,37,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (36,37,3,5,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (37,38,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (38,38,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (39,39,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (40,39,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (41,39,3,5,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (42,40,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (43,40,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (44,40,3,5,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (45,41,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (46,41,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (47,41,3,5,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (48,42,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (49,42,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (50,42,3,5,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (51,43,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (52,44,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (53,44,2,1,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (54,45,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (55,45,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (56,46,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (57,46,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (58,47,2,2,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (59,47,2,3,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (60,47,3,5,1,'2011-02-28');
INSERT INTO account_status_change_history VALUES (61,50,2,2,1,'2011-03-03');
INSERT INTO account_status_change_history VALUES (62,50,2,3,1,'2011-03-03');
INSERT INTO account_status_change_history VALUES (63,52,2,2,1,'2011-03-04');
INSERT INTO account_status_change_history VALUES (64,52,2,3,1,'2011-03-04');
INSERT INTO account_status_change_history VALUES (65,53,2,2,1,'2011-03-04');
INSERT INTO account_status_change_history VALUES (66,54,2,2,1,'2011-03-04');
INSERT INTO account_status_change_history VALUES (67,55,2,2,1,'2011-03-04');
INSERT INTO account_status_change_history VALUES (68,59,NULL,14,1,'2011-03-08');
INSERT INTO account_status_change_history VALUES (69,59,14,16,1,'2011-03-08');
INSERT INTO account_status_change_history VALUES (70,67,NULL,14,1,'2011-03-14');
INSERT INTO account_status_change_history VALUES (71,67,14,16,1,'2011-03-14');
/*!40000 ALTER TABLE account_status_change_history ENABLE KEYS */;
DROP TABLE IF EXISTS account_trxn;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_trxn (
  account_trxn_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  payment_id int(11) NOT NULL,
  personnel_id int(11) DEFAULT NULL,
  account_action_id smallint(6) NOT NULL,
  currency_id smallint(6) DEFAULT NULL,
  amount_currency_id smallint(6) DEFAULT NULL,
  amount decimal(21,4) NOT NULL,
  due_date date DEFAULT NULL,
  comments varchar(200) DEFAULT NULL,
  action_date date NOT NULL,
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  customer_id int(11) DEFAULT NULL,
  installment_id smallint(6) DEFAULT NULL,
  related_trxn_id int(11) DEFAULT NULL,
  PRIMARY KEY (account_trxn_id),
  KEY account_action_id (account_action_id),
  KEY payment_id (payment_id),
  KEY currency_id (currency_id),
  KEY amount_currency_id (amount_currency_id),
  KEY customer_id (customer_id),
  KEY account_id_account_trxn_idx (account_id),
  CONSTRAINT account_trxn_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_trxn_ibfk_2 FOREIGN KEY (account_action_id) REFERENCES account_action (account_action_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_trxn_ibfk_3 FOREIGN KEY (payment_id) REFERENCES account_payment (payment_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_trxn_ibfk_4 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_trxn_ibfk_5 FOREIGN KEY (amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT account_trxn_ibfk_6 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_trxn DISABLE KEYS */;
INSERT INTO account_trxn VALUES (1,2,1,1,6,NULL,2,'1000.0000','2011-02-18','','2011-02-18','2011-02-17 18:30:00',1,NULL,NULL);
INSERT INTO account_trxn VALUES (2,10,2,1,10,NULL,2,'1000.0000','2011-02-21','-','2011-02-21','2011-02-21 10:32:16',5,0,NULL);
INSERT INTO account_trxn VALUES (3,15,3,1,10,NULL,2,'1000.0000','2011-02-18','-','2011-02-18','2011-02-18 12:42:57',10,0,NULL);
INSERT INTO account_trxn VALUES (4,12,4,1,10,NULL,2,'100000.0000','2010-02-22','-','2010-02-22','2010-02-22 05:15:52',6,0,NULL);
INSERT INTO account_trxn VALUES (5,25,5,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:41:38',18,0,NULL);
INSERT INTO account_trxn VALUES (6,26,6,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:43:13',18,0,NULL);
INSERT INTO account_trxn VALUES (7,27,7,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:44:34',18,0,NULL);
INSERT INTO account_trxn VALUES (8,28,8,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:45:48',18,0,NULL);
INSERT INTO account_trxn VALUES (9,29,9,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:47:23',18,0,NULL);
INSERT INTO account_trxn VALUES (10,37,10,1,10,NULL,2,'1000.0000','2011-02-28','-','2011-02-28','2011-02-28 07:23:06',24,0,NULL);
INSERT INTO account_trxn VALUES (11,39,11,1,4,NULL,2,'10.0000','2011-02-28','-','2011-02-28','2011-02-28 08:51:51',24,0,NULL);
INSERT INTO account_trxn VALUES (12,39,11,1,10,NULL,2,'100000.0000','2011-02-28','-','2011-02-28','2011-02-28 08:51:51',24,0,NULL);
INSERT INTO account_trxn VALUES (13,40,12,1,4,NULL,2,'10.0000','2011-02-28','-','2011-02-28','2011-02-28 11:44:39',24,0,NULL);
INSERT INTO account_trxn VALUES (14,40,12,1,10,NULL,2,'100000.0000','2011-02-28','-','2011-02-28','2011-02-28 11:44:39',24,0,NULL);
INSERT INTO account_trxn VALUES (15,41,13,1,4,NULL,2,'10.0000','2011-02-28','-','2011-02-28','2011-02-28 11:45:06',24,0,NULL);
INSERT INTO account_trxn VALUES (16,41,13,1,10,NULL,2,'100000.0000','2011-02-28','-','2011-02-28','2011-02-28 11:45:06',24,0,NULL);
INSERT INTO account_trxn VALUES (17,42,14,1,10,NULL,2,'1000.0000','2011-02-28','-','2011-02-28','2011-02-28 11:46:24',4,0,NULL);
INSERT INTO account_trxn VALUES (18,47,15,1,10,NULL,2,'10000.0000','2011-02-28','-','2011-02-28','2011-02-28 05:21:01',24,0,NULL);
/*!40000 ALTER TABLE account_trxn ENABLE KEYS */;
DROP TABLE IF EXISTS account_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_type (
  account_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  description varchar(50) DEFAULT NULL,
  PRIMARY KEY (account_type_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT account_type_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_type DISABLE KEYS */;
INSERT INTO account_type VALUES (1,126,'Loan Account');
INSERT INTO account_type VALUES (2,127,'Savings Account');
INSERT INTO account_type VALUES (3,140,'Customer Account');
INSERT INTO account_type VALUES (4,126,'Individual Loan Account');
/*!40000 ALTER TABLE account_type ENABLE KEYS */;
DROP TABLE IF EXISTS activity;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE activity (
  activity_id smallint(6) NOT NULL AUTO_INCREMENT,
  parent_id smallint(6) DEFAULT NULL,
  activity_name_lookup_id int(11) NOT NULL,
  description_lookup_id int(11) NOT NULL,
  PRIMARY KEY (activity_id),
  KEY parent_id (parent_id),
  KEY activity_name_lookup_id (activity_name_lookup_id),
  KEY description_lookup_id (description_lookup_id),
  CONSTRAINT activity_ibfk_1 FOREIGN KEY (parent_id) REFERENCES activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT activity_ibfk_2 FOREIGN KEY (activity_name_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT activity_ibfk_3 FOREIGN KEY (description_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=247 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE activity DISABLE KEYS */;
INSERT INTO activity VALUES (1,NULL,371,371);
INSERT INTO activity VALUES (2,1,372,372);
INSERT INTO activity VALUES (3,2,373,373);
INSERT INTO activity VALUES (4,2,374,374);
INSERT INTO activity VALUES (5,1,375,375);
INSERT INTO activity VALUES (6,5,376,376);
INSERT INTO activity VALUES (7,5,377,377);
INSERT INTO activity VALUES (8,1,378,378);
INSERT INTO activity VALUES (9,8,379,379);
INSERT INTO activity VALUES (10,8,380,380);
INSERT INTO activity VALUES (13,NULL,381,381);
INSERT INTO activity VALUES (14,13,382,382);
INSERT INTO activity VALUES (15,14,383,383);
INSERT INTO activity VALUES (16,14,384,384);
INSERT INTO activity VALUES (17,NULL,385,385);
INSERT INTO activity VALUES (18,17,386,386);
INSERT INTO activity VALUES (19,18,387,387);
INSERT INTO activity VALUES (20,18,388,388);
INSERT INTO activity VALUES (21,18,389,389);
INSERT INTO activity VALUES (22,17,390,390);
INSERT INTO activity VALUES (23,22,391,391);
INSERT INTO activity VALUES (24,22,392,392);
INSERT INTO activity VALUES (25,22,393,393);
INSERT INTO activity VALUES (33,NULL,394,394);
INSERT INTO activity VALUES (34,33,395,395);
INSERT INTO activity VALUES (35,34,396,396);
INSERT INTO activity VALUES (36,34,397,397);
INSERT INTO activity VALUES (37,34,398,398);
INSERT INTO activity VALUES (38,34,399,399);
INSERT INTO activity VALUES (39,34,400,400);
INSERT INTO activity VALUES (40,34,401,401);
INSERT INTO activity VALUES (41,34,402,402);
INSERT INTO activity VALUES (42,34,403,403);
INSERT INTO activity VALUES (43,34,404,404);
INSERT INTO activity VALUES (44,34,405,405);
INSERT INTO activity VALUES (46,34,407,407);
INSERT INTO activity VALUES (47,34,408,408);
INSERT INTO activity VALUES (48,34,409,409);
INSERT INTO activity VALUES (49,34,410,410);
INSERT INTO activity VALUES (50,34,411,411);
INSERT INTO activity VALUES (51,34,412,412);
INSERT INTO activity VALUES (52,34,413,413);
INSERT INTO activity VALUES (53,34,414,414);
INSERT INTO activity VALUES (54,34,415,415);
INSERT INTO activity VALUES (55,34,416,416);
INSERT INTO activity VALUES (56,33,417,417);
INSERT INTO activity VALUES (57,56,418,418);
INSERT INTO activity VALUES (58,56,419,419);
INSERT INTO activity VALUES (59,56,420,420);
INSERT INTO activity VALUES (60,56,421,421);
INSERT INTO activity VALUES (61,56,422,422);
INSERT INTO activity VALUES (62,56,423,423);
INSERT INTO activity VALUES (63,56,424,424);
INSERT INTO activity VALUES (64,56,425,425);
INSERT INTO activity VALUES (65,56,426,426);
INSERT INTO activity VALUES (66,56,427,427);
INSERT INTO activity VALUES (68,56,429,429);
INSERT INTO activity VALUES (69,56,430,430);
INSERT INTO activity VALUES (70,56,431,431);
INSERT INTO activity VALUES (71,56,432,432);
INSERT INTO activity VALUES (72,56,433,433);
INSERT INTO activity VALUES (73,56,434,434);
INSERT INTO activity VALUES (74,56,435,435);
INSERT INTO activity VALUES (75,56,436,436);
INSERT INTO activity VALUES (76,56,437,437);
INSERT INTO activity VALUES (77,56,438,438);
INSERT INTO activity VALUES (78,33,439,439);
INSERT INTO activity VALUES (79,78,440,440);
INSERT INTO activity VALUES (80,78,441,441);
INSERT INTO activity VALUES (81,78,442,442);
INSERT INTO activity VALUES (82,78,443,443);
INSERT INTO activity VALUES (83,78,444,444);
INSERT INTO activity VALUES (85,78,446,446);
INSERT INTO activity VALUES (86,78,447,447);
INSERT INTO activity VALUES (87,78,448,438);
INSERT INTO activity VALUES (88,78,449,449);
INSERT INTO activity VALUES (89,NULL,450,450);
INSERT INTO activity VALUES (90,89,451,451);
INSERT INTO activity VALUES (91,90,452,452);
INSERT INTO activity VALUES (92,90,453,453);
INSERT INTO activity VALUES (93,89,454,454);
INSERT INTO activity VALUES (94,93,455,455);
INSERT INTO activity VALUES (95,93,456,456);
INSERT INTO activity VALUES (96,89,457,457);
INSERT INTO activity VALUES (97,96,458,458);
INSERT INTO activity VALUES (98,96,459,459);
INSERT INTO activity VALUES (99,NULL,460,460);
INSERT INTO activity VALUES (100,99,461,461);
INSERT INTO activity VALUES (101,100,462,462);
INSERT INTO activity VALUES (102,100,463,463);
INSERT INTO activity VALUES (103,100,464,464);
INSERT INTO activity VALUES (104,100,465,465);
INSERT INTO activity VALUES (105,100,466,466);
INSERT INTO activity VALUES (106,100,467,467);
INSERT INTO activity VALUES (108,100,469,469);
INSERT INTO activity VALUES (109,100,470,470);
INSERT INTO activity VALUES (110,100,471,471);
INSERT INTO activity VALUES (113,99,474,474);
INSERT INTO activity VALUES (115,113,475,475);
INSERT INTO activity VALUES (116,113,476,476);
INSERT INTO activity VALUES (118,113,478,478);
INSERT INTO activity VALUES (119,113,479,479);
INSERT INTO activity VALUES (120,113,480,480);
INSERT INTO activity VALUES (121,34,481,481);
INSERT INTO activity VALUES (122,56,482,482);
INSERT INTO activity VALUES (126,34,483,483);
INSERT INTO activity VALUES (127,78,484,484);
INSERT INTO activity VALUES (128,78,485,485);
INSERT INTO activity VALUES (129,100,486,486);
INSERT INTO activity VALUES (131,113,487,487);
INSERT INTO activity VALUES (135,18,488,488);
INSERT INTO activity VALUES (136,NULL,489,489);
INSERT INTO activity VALUES (137,136,490,490);
INSERT INTO activity VALUES (138,136,491,491);
INSERT INTO activity VALUES (139,136,492,492);
INSERT INTO activity VALUES (140,136,493,493);
INSERT INTO activity VALUES (141,NULL,494,494);
INSERT INTO activity VALUES (145,141,498,498);
INSERT INTO activity VALUES (146,141,499,499);
INSERT INTO activity VALUES (147,141,500,500);
INSERT INTO activity VALUES (148,141,501,501);
INSERT INTO activity VALUES (149,141,502,502);
INSERT INTO activity VALUES (150,141,503,503);
INSERT INTO activity VALUES (151,141,504,504);
INSERT INTO activity VALUES (178,113,531,531);
INSERT INTO activity VALUES (179,100,532,532);
INSERT INTO activity VALUES (180,136,533,533);
INSERT INTO activity VALUES (181,136,534,534);
INSERT INTO activity VALUES (182,136,535,535);
INSERT INTO activity VALUES (183,136,536,536);
INSERT INTO activity VALUES (184,136,537,537);
INSERT INTO activity VALUES (185,136,538,538);
INSERT INTO activity VALUES (186,136,546,546);
INSERT INTO activity VALUES (187,136,551,551);
INSERT INTO activity VALUES (188,136,552,552);
INSERT INTO activity VALUES (189,113,553,553);
INSERT INTO activity VALUES (190,136,554,554);
INSERT INTO activity VALUES (191,136,555,555);
INSERT INTO activity VALUES (192,196,560,560);
INSERT INTO activity VALUES (193,13,562,562);
INSERT INTO activity VALUES (194,18,563,563);
INSERT INTO activity VALUES (195,90,561,561);
INSERT INTO activity VALUES (196,NULL,564,564);
INSERT INTO activity VALUES (197,196,565,565);
INSERT INTO activity VALUES (198,34,566,566);
INSERT INTO activity VALUES (199,56,567,567);
INSERT INTO activity VALUES (200,78,568,568);
INSERT INTO activity VALUES (201,196,569,569);
INSERT INTO activity VALUES (202,99,570,570);
INSERT INTO activity VALUES (203,NULL,574,574);
INSERT INTO activity VALUES (204,203,575,575);
INSERT INTO activity VALUES (205,203,579,579);
INSERT INTO activity VALUES (206,34,580,580);
INSERT INTO activity VALUES (208,34,582,582);
INSERT INTO activity VALUES (209,89,583,583);
INSERT INTO activity VALUES (210,209,584,584);
INSERT INTO activity VALUES (211,209,585,585);
INSERT INTO activity VALUES (213,203,587,587);
INSERT INTO activity VALUES (214,141,588,588);
INSERT INTO activity VALUES (215,141,589,589);
INSERT INTO activity VALUES (216,141,590,590);
INSERT INTO activity VALUES (217,113,591,591);
INSERT INTO activity VALUES (218,99,592,592);
INSERT INTO activity VALUES (219,1,593,593);
INSERT INTO activity VALUES (220,141,594,594);
INSERT INTO activity VALUES (221,141,595,595);
INSERT INTO activity VALUES (222,141,596,596);
INSERT INTO activity VALUES (223,141,597,597);
INSERT INTO activity VALUES (224,203,598,598);
INSERT INTO activity VALUES (225,141,602,602);
INSERT INTO activity VALUES (226,141,603,603);
INSERT INTO activity VALUES (227,NULL,605,605);
INSERT INTO activity VALUES (228,227,606,606);
INSERT INTO activity VALUES (229,145,607,607);
INSERT INTO activity VALUES (230,203,608,608);
INSERT INTO activity VALUES (231,150,611,611);
INSERT INTO activity VALUES (232,150,612,612);
INSERT INTO activity VALUES (233,196,619,619);
INSERT INTO activity VALUES (234,227,625,625);
INSERT INTO activity VALUES (235,1,627,627);
INSERT INTO activity VALUES (236,150,628,628);
INSERT INTO activity VALUES (237,150,629,629);
INSERT INTO activity VALUES (238,227,630,630);
INSERT INTO activity VALUES (239,227,631,631);
INSERT INTO activity VALUES (240,203,632,632);
INSERT INTO activity VALUES (241,227,633,633);
INSERT INTO activity VALUES (242,227,634,634);
INSERT INTO activity VALUES (243,203,635,635);
INSERT INTO activity VALUES (244,113,637,637);
INSERT INTO activity VALUES (245,34,638,638);
INSERT INTO activity VALUES (246,196,639,639);
/*!40000 ALTER TABLE activity ENABLE KEYS */;
DROP TABLE IF EXISTS admin_document;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE admin_document (
  admin_document_id int(11) NOT NULL AUTO_INCREMENT,
  admin_document_name varchar(200) DEFAULT NULL,
  admin_document_identifier varchar(100) DEFAULT NULL,
  admin_document_active smallint(6) DEFAULT NULL,
  created_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (admin_document_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE admin_document DISABLE KEYS */;
/*!40000 ALTER TABLE admin_document ENABLE KEYS */;
DROP TABLE IF EXISTS admin_document_acc_state_mix;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE admin_document_acc_state_mix (
  admin_doc_acc_state_mix_id int(11) NOT NULL AUTO_INCREMENT,
  account_state_id smallint(6) NOT NULL,
  admin_document_id int(11) NOT NULL,
  created_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (admin_doc_acc_state_mix_id),
  KEY admin_document_acc_state_mix_fk (account_state_id),
  KEY admin_document_acc_state_mix_fk1 (admin_document_id),
  CONSTRAINT admin_document_acc_state_mix_fk FOREIGN KEY (account_state_id) REFERENCES account_state (account_state_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT admin_document_acc_state_mix_fk1 FOREIGN KEY (admin_document_id) REFERENCES admin_document (admin_document_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE admin_document_acc_state_mix DISABLE KEYS */;
/*!40000 ALTER TABLE admin_document_acc_state_mix ENABLE KEYS */;
DROP TABLE IF EXISTS applied_upgrades;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE applied_upgrades (
  upgrade_id int(11) NOT NULL,
  PRIMARY KEY (upgrade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE applied_upgrades DISABLE KEYS */;
INSERT INTO applied_upgrades VALUES (1277565300);
INSERT INTO applied_upgrades VALUES (1277565388);
INSERT INTO applied_upgrades VALUES (1277565389);
INSERT INTO applied_upgrades VALUES (1277567194);
INSERT INTO applied_upgrades VALUES (1277567768);
INSERT INTO applied_upgrades VALUES (1277567885);
INSERT INTO applied_upgrades VALUES (1277567949);
INSERT INTO applied_upgrades VALUES (1277568944);
INSERT INTO applied_upgrades VALUES (1277569001);
INSERT INTO applied_upgrades VALUES (1277571296);
INSERT INTO applied_upgrades VALUES (1277571560);
INSERT INTO applied_upgrades VALUES (1277571792);
INSERT INTO applied_upgrades VALUES (1277571837);
INSERT INTO applied_upgrades VALUES (1277586926);
INSERT INTO applied_upgrades VALUES (1277587117);
INSERT INTO applied_upgrades VALUES (1277587199);
INSERT INTO applied_upgrades VALUES (1277587465);
INSERT INTO applied_upgrades VALUES (1277587818);
INSERT INTO applied_upgrades VALUES (1277587878);
INSERT INTO applied_upgrades VALUES (1277587947);
INSERT INTO applied_upgrades VALUES (1277588038);
INSERT INTO applied_upgrades VALUES (1277588072);
INSERT INTO applied_upgrades VALUES (1277588240);
INSERT INTO applied_upgrades VALUES (1277588373);
INSERT INTO applied_upgrades VALUES (1277588885);
INSERT INTO applied_upgrades VALUES (1277588973);
INSERT INTO applied_upgrades VALUES (1277589055);
INSERT INTO applied_upgrades VALUES (1277589236);
INSERT INTO applied_upgrades VALUES (1277589321);
INSERT INTO applied_upgrades VALUES (1277589383);
INSERT INTO applied_upgrades VALUES (1278540763);
INSERT INTO applied_upgrades VALUES (1278540832);
INSERT INTO applied_upgrades VALUES (1278542100);
INSERT INTO applied_upgrades VALUES (1278542119);
INSERT INTO applied_upgrades VALUES (1278542138);
INSERT INTO applied_upgrades VALUES (1278542152);
INSERT INTO applied_upgrades VALUES (1278542171);
INSERT INTO applied_upgrades VALUES (1279140399);
INSERT INTO applied_upgrades VALUES (1279272090);
INSERT INTO applied_upgrades VALUES (1280719328);
INSERT INTO applied_upgrades VALUES (1280719447);
INSERT INTO applied_upgrades VALUES (1280719676);
INSERT INTO applied_upgrades VALUES (1280721170);
INSERT INTO applied_upgrades VALUES (1280793109);
INSERT INTO applied_upgrades VALUES (1282247229);
INSERT INTO applied_upgrades VALUES (1282389745);
INSERT INTO applied_upgrades VALUES (1282814250);
INSERT INTO applied_upgrades VALUES (1283237728);
INSERT INTO applied_upgrades VALUES (1283320210);
INSERT INTO applied_upgrades VALUES (1283416834);
INSERT INTO applied_upgrades VALUES (1283765911);
INSERT INTO applied_upgrades VALUES (1284365506);
INSERT INTO applied_upgrades VALUES (1284977483);
INSERT INTO applied_upgrades VALUES (1284986654);
INSERT INTO applied_upgrades VALUES (1285046834);
INSERT INTO applied_upgrades VALUES (1285177663);
INSERT INTO applied_upgrades VALUES (1285651956);
INSERT INTO applied_upgrades VALUES (1285812348);
INSERT INTO applied_upgrades VALUES (1286195484);
INSERT INTO applied_upgrades VALUES (1286529235);
INSERT INTO applied_upgrades VALUES (1286780611);
INSERT INTO applied_upgrades VALUES (1287934290);
INSERT INTO applied_upgrades VALUES (1288013750);
INSERT INTO applied_upgrades VALUES (1288349766);
INSERT INTO applied_upgrades VALUES (1288869198);
INSERT INTO applied_upgrades VALUES (1289125815);
INSERT INTO applied_upgrades VALUES (1289541994);
INSERT INTO applied_upgrades VALUES (1289994929);
INSERT INTO applied_upgrades VALUES (1290720085);
INSERT INTO applied_upgrades VALUES (1291245955);
INSERT INTO applied_upgrades VALUES (1292234934);
INSERT INTO applied_upgrades VALUES (1292241366);
INSERT INTO applied_upgrades VALUES (1294738016);
INSERT INTO applied_upgrades VALUES (1294927843);
INSERT INTO applied_upgrades VALUES (1295985566);
INSERT INTO applied_upgrades VALUES (1296137314);
INSERT INTO applied_upgrades VALUES (1298198335);
INSERT INTO applied_upgrades VALUES (1299279218);
/*!40000 ALTER TABLE applied_upgrades ENABLE KEYS */;
DROP TABLE IF EXISTS attendance;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE attendance (
  meeting_id int(11) NOT NULL,
  meeting_date date NOT NULL,
  attendance smallint(6) DEFAULT NULL,
  notes varchar(200) NOT NULL,
  PRIMARY KEY (meeting_id,meeting_date),
  CONSTRAINT attendance_ibfk_1 FOREIGN KEY (meeting_id) REFERENCES customer_meeting (customer_meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE attendance DISABLE KEYS */;
/*!40000 ALTER TABLE attendance ENABLE KEYS */;
DROP TABLE IF EXISTS batch_branch_cash_confirmation_report;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_branch_cash_confirmation_report (
  branch_cash_confirmation_report_id int(11) NOT NULL AUTO_INCREMENT,
  branch_id smallint(6) NOT NULL,
  run_date date NOT NULL,
  PRIMARY KEY (branch_cash_confirmation_report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_branch_cash_confirmation_report DISABLE KEYS */;
/*!40000 ALTER TABLE batch_branch_cash_confirmation_report ENABLE KEYS */;
DROP TABLE IF EXISTS batch_branch_confirmation_disbursement;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_branch_confirmation_disbursement (
  id int(11) NOT NULL AUTO_INCREMENT,
  branch_cash_confirmation_report_id int(11) NOT NULL,
  product_name varchar(50) NOT NULL,
  actual decimal(21,4) NOT NULL,
  actual_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (id),
  KEY branch_cash_confirmation_report_id (branch_cash_confirmation_report_id),
  CONSTRAINT batch_branch_confirmation_disbursement_ibfk_1 FOREIGN KEY (branch_cash_confirmation_report_id) REFERENCES batch_branch_cash_confirmation_report (branch_cash_confirmation_report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_branch_confirmation_disbursement DISABLE KEYS */;
/*!40000 ALTER TABLE batch_branch_confirmation_disbursement ENABLE KEYS */;
DROP TABLE IF EXISTS batch_branch_confirmation_issue;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_branch_confirmation_issue (
  id int(11) NOT NULL AUTO_INCREMENT,
  branch_cash_confirmation_report_id int(11) NOT NULL,
  product_name varchar(50) NOT NULL,
  actual decimal(21,4) NOT NULL,
  actual_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (id),
  KEY branch_cash_confirmation_report_id (branch_cash_confirmation_report_id),
  CONSTRAINT batch_branch_confirmation_issue_ibfk_1 FOREIGN KEY (branch_cash_confirmation_report_id) REFERENCES batch_branch_cash_confirmation_report (branch_cash_confirmation_report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_branch_confirmation_issue DISABLE KEYS */;
/*!40000 ALTER TABLE batch_branch_confirmation_issue ENABLE KEYS */;
DROP TABLE IF EXISTS batch_branch_confirmation_recovery;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_branch_confirmation_recovery (
  recovery_id int(11) NOT NULL AUTO_INCREMENT,
  branch_cash_confirmation_report_id int(11) NOT NULL,
  product_name varchar(50) NOT NULL,
  due decimal(21,4) NOT NULL,
  due_currency_id smallint(6) NOT NULL,
  actual decimal(21,4) NOT NULL,
  actual_currency_id smallint(6) NOT NULL,
  arrears decimal(21,4) NOT NULL,
  arrears_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (recovery_id),
  KEY branch_cash_confirmation_report_id (branch_cash_confirmation_report_id),
  CONSTRAINT batch_branch_confirmation_recovery_ibfk_1 FOREIGN KEY (branch_cash_confirmation_report_id) REFERENCES batch_branch_cash_confirmation_report (branch_cash_confirmation_report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_branch_confirmation_recovery DISABLE KEYS */;
/*!40000 ALTER TABLE batch_branch_confirmation_recovery ENABLE KEYS */;
DROP TABLE IF EXISTS batch_branch_report;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_branch_report (
  branch_report_id int(11) NOT NULL AUTO_INCREMENT,
  branch_id smallint(6) NOT NULL,
  run_date date NOT NULL,
  PRIMARY KEY (branch_report_id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_branch_report DISABLE KEYS */;
INSERT INTO batch_branch_report VALUES (1,2,'2011-02-22');
/*!40000 ALTER TABLE batch_branch_report ENABLE KEYS */;
DROP TABLE IF EXISTS batch_client_summary;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_client_summary (
  client_summary_id int(11) NOT NULL AUTO_INCREMENT,
  branch_report_id int(11) NOT NULL,
  field_name varchar(50) NOT NULL,
  total varchar(50) DEFAULT NULL,
  vpoor_total varchar(50) DEFAULT NULL,
  PRIMARY KEY (client_summary_id),
  KEY branch_report_id (branch_report_id),
  CONSTRAINT batch_client_summary_ibfk_1 FOREIGN KEY (branch_report_id) REFERENCES batch_branch_report (branch_report_id)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_client_summary DISABLE KEYS */;
INSERT INTO batch_client_summary VALUES (1,1,'active.savers.count','0','0');
INSERT INTO batch_client_summary VALUES (2,1,'replacements.count','0','0');
INSERT INTO batch_client_summary VALUES (3,1,'loan.account.dormant.count','0','0');
INSERT INTO batch_client_summary VALUES (4,1,'active.borrowers.count','3','3');
INSERT INTO batch_client_summary VALUES (5,1,'onholds.count','0','0');
INSERT INTO batch_client_summary VALUES (6,1,'saving.account.dormant.count','0','0');
INSERT INTO batch_client_summary VALUES (7,1,'portfolio.at.risk','0.00',NULL);
INSERT INTO batch_client_summary VALUES (8,1,'group.count','2','');
INSERT INTO batch_client_summary VALUES (9,1,'center.count','1','');
INSERT INTO batch_client_summary VALUES (10,1,'dropouts.count','0','0');
INSERT INTO batch_client_summary VALUES (11,1,'dropout.rate','0.00','0.00');
INSERT INTO batch_client_summary VALUES (12,1,'active.members.count','7','7');
/*!40000 ALTER TABLE batch_client_summary ENABLE KEYS */;
DROP TABLE IF EXISTS batch_loan_arrears_aging;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_loan_arrears_aging (
  loan_arrears_aging_id int(11) NOT NULL AUTO_INCREMENT,
  aging_period_id int(11) NOT NULL,
  branch_report_id int(11) NOT NULL,
  clients_aging int(11) NOT NULL,
  loans_aging int(11) NOT NULL,
  amount_aging decimal(21,4) NOT NULL,
  amount_aging_currency_id smallint(6) NOT NULL,
  amount_outstanding_aging decimal(21,4) NOT NULL,
  amount_outstanding_aging_currency_id smallint(6) NOT NULL,
  interest_aging decimal(21,4) NOT NULL,
  interest_aging_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (loan_arrears_aging_id),
  KEY branch_report_id (branch_report_id),
  CONSTRAINT batch_loan_arrears_aging_ibfk_1 FOREIGN KEY (branch_report_id) REFERENCES batch_branch_report (branch_report_id)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_loan_arrears_aging DISABLE KEYS */;
INSERT INTO batch_loan_arrears_aging VALUES (1,6,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO batch_loan_arrears_aging VALUES (2,1,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO batch_loan_arrears_aging VALUES (3,2,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO batch_loan_arrears_aging VALUES (4,5,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO batch_loan_arrears_aging VALUES (5,3,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO batch_loan_arrears_aging VALUES (6,4,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO batch_loan_arrears_aging VALUES (7,0,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
/*!40000 ALTER TABLE batch_loan_arrears_aging ENABLE KEYS */;
DROP TABLE IF EXISTS batch_loan_arrears_profile;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_loan_arrears_profile (
  loan_arrears_profile_id int(11) NOT NULL AUTO_INCREMENT,
  branch_report_id int(11) NOT NULL,
  loans_in_arrears int(11) NOT NULL,
  clients_in_arrears int(11) NOT NULL,
  overdue_balance decimal(21,4) NOT NULL,
  overdue_balance_currency_id smallint(6) NOT NULL,
  unpaid_balance decimal(21,4) NOT NULL,
  unpaid_balance_currency_id smallint(6) NOT NULL,
  loans_at_risk int(11) NOT NULL,
  outstanding_amount_at_risk decimal(21,4) NOT NULL,
  outstanding_amount_at_risk_currency_id smallint(6) NOT NULL,
  overdue_amount_at_risk decimal(21,4) NOT NULL,
  overdue_amount_at_risk_currency_id smallint(6) NOT NULL,
  clients_at_risk int(11) NOT NULL,
  PRIMARY KEY (loan_arrears_profile_id),
  KEY branch_report_id (branch_report_id),
  CONSTRAINT batch_loan_arrears_profile_ibfk_1 FOREIGN KEY (branch_report_id) REFERENCES batch_branch_report (branch_report_id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_loan_arrears_profile DISABLE KEYS */;
INSERT INTO batch_loan_arrears_profile VALUES (1,1,0,0,'0.0000',2,'0.0000',2,0,'0.0000',2,'0.0000',2,0);
/*!40000 ALTER TABLE batch_loan_arrears_profile ENABLE KEYS */;
DROP TABLE IF EXISTS batch_loan_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_loan_details (
  loan_details_id int(11) NOT NULL AUTO_INCREMENT,
  branch_report_id int(11) NOT NULL,
  product_name varchar(50) NOT NULL,
  number_of_loans_issued int(11) NOT NULL,
  loan_amount_issued decimal(21,4) NOT NULL,
  loan_amount_issued_currency_id smallint(6) NOT NULL,
  loan_interest_issued decimal(21,4) NOT NULL,
  loan_interest_issued_currency_id smallint(6) NOT NULL,
  number_of_loans_outstanding int(11) NOT NULL,
  loan_outstanding_amount decimal(21,4) NOT NULL,
  loan_outstanding_amount_currency_id smallint(6) NOT NULL,
  loan_outstanding_interest decimal(21,4) NOT NULL,
  loan_outstanding_interest_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (loan_details_id),
  KEY branch_report_id (branch_report_id),
  CONSTRAINT batch_loan_details_ibfk_1 FOREIGN KEY (branch_report_id) REFERENCES batch_branch_report (branch_report_id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_loan_details DISABLE KEYS */;
INSERT INTO batch_loan_details VALUES (1,1,'GroupEmergencyLoan',0,'0.0000',2,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO batch_loan_details VALUES (2,1,'WeeklyFlatLoanWithOneTimeFees',1,'100000.0000',2,'4603.0000',2,1,'100000.0000',2,'4603.0000',2);
INSERT INTO batch_loan_details VALUES (3,1,'ClientEmergencyLoan',2,'2000.0000',2,'0.0000',2,2,'2000.0000',2,'0.0000',2);
/*!40000 ALTER TABLE batch_loan_details ENABLE KEYS */;
DROP TABLE IF EXISTS batch_staff_summary;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_staff_summary (
  staff_summary_id int(11) NOT NULL AUTO_INCREMENT,
  branch_report_id int(11) NOT NULL,
  personnel_id smallint(6) NOT NULL,
  personnel_name varchar(50) NOT NULL,
  joining_date date DEFAULT NULL,
  active_borrowers int(11) NOT NULL,
  active_loans int(11) NOT NULL,
  center_count int(11) NOT NULL,
  client_count int(11) NOT NULL,
  loan_amount_outstanding decimal(21,4) NOT NULL,
  loan_amount_outstanding_currency_id smallint(6) NOT NULL,
  interest_fees_outstanding decimal(21,4) NOT NULL,
  interest_fees_outstanding_currency_id smallint(6) NOT NULL,
  portfolio_at_risk decimal(21,4) NOT NULL,
  total_clients_enrolled int(11) NOT NULL,
  clients_enrolled_this_month int(11) NOT NULL,
  loan_arrears_amount decimal(21,4) NOT NULL,
  loan_arrears_amount_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (staff_summary_id),
  KEY branch_report_id (branch_report_id),
  CONSTRAINT batch_staff_summary_ibfk_1 FOREIGN KEY (branch_report_id) REFERENCES batch_branch_report (branch_report_id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_staff_summary DISABLE KEYS */;
INSERT INTO batch_staff_summary VALUES (1,1,2,'loan officer','2005-02-18',3,3,1,7,'2000.0000',2,'0.0000',2,'0.0000',7,3,'0.0000',2);
/*!40000 ALTER TABLE batch_staff_summary ENABLE KEYS */;
DROP TABLE IF EXISTS batch_staffing_level_summary;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE batch_staffing_level_summary (
  staffing_level_summary_id int(11) NOT NULL AUTO_INCREMENT,
  branch_report_id int(11) NOT NULL,
  role_id int(11) NOT NULL,
  role_name varchar(50) NOT NULL,
  personnel_count int(11) NOT NULL,
  PRIMARY KEY (staffing_level_summary_id),
  KEY branch_report_id (branch_report_id),
  CONSTRAINT batch_staffing_level_summary_ibfk_1 FOREIGN KEY (branch_report_id) REFERENCES batch_branch_report (branch_report_id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE batch_staffing_level_summary DISABLE KEYS */;
INSERT INTO batch_staffing_level_summary VALUES (1,1,-1,'Total Staff',1);
INSERT INTO batch_staffing_level_summary VALUES (2,1,1,'No Title',1);
/*!40000 ALTER TABLE batch_staffing_level_summary ENABLE KEYS */;
DROP TABLE IF EXISTS branch_ho_update;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE branch_ho_update (
  office_id smallint(6) NOT NULL,
  last_updated_date date DEFAULT NULL,
  PRIMARY KEY (office_id),
  CONSTRAINT branch_ho_update_ibfk_1 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE branch_ho_update DISABLE KEYS */;
/*!40000 ALTER TABLE branch_ho_update ENABLE KEYS */;
DROP TABLE IF EXISTS calculated_interest_on_payment;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE calculated_interest_on_payment (
  loan_account_trxn_id int(11) NOT NULL,
  original_interest decimal(21,4) NOT NULL,
  original_interest_currency_id smallint(6) DEFAULT NULL,
  extra_interest_paid decimal(21,4) DEFAULT NULL,
  extra_interest_paid_currency_id smallint(6) DEFAULT NULL,
  interest_due_till_paid decimal(21,4) DEFAULT NULL,
  interest_due_till_paid_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (loan_account_trxn_id),
  KEY original_interest_currency_id (original_interest_currency_id),
  KEY extra_interest_paid_currency_id (extra_interest_paid_currency_id),
  KEY interest_due_till_paid_currency_id (interest_due_till_paid_currency_id),
  CONSTRAINT calculated_interest_on_payment_ibfk_1 FOREIGN KEY (loan_account_trxn_id) REFERENCES loan_trxn_detail (account_trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT calculated_interest_on_payment_ibfk_2 FOREIGN KEY (original_interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT calculated_interest_on_payment_ibfk_3 FOREIGN KEY (extra_interest_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT calculated_interest_on_payment_ibfk_4 FOREIGN KEY (interest_due_till_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE calculated_interest_on_payment DISABLE KEYS */;
/*!40000 ALTER TABLE calculated_interest_on_payment ENABLE KEYS */;
DROP TABLE IF EXISTS cash_flow;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE cash_flow (
  id int(11) NOT NULL AUTO_INCREMENT,
  capital decimal(21,4) DEFAULT NULL,
  liability decimal(21,4) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE cash_flow DISABLE KEYS */;
/*!40000 ALTER TABLE cash_flow ENABLE KEYS */;
DROP TABLE IF EXISTS cash_flow_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE cash_flow_detail (
  id smallint(6) NOT NULL AUTO_INCREMENT,
  cash_flow_threshold decimal(13,10) DEFAULT NULL,
  indebtedness_ratio decimal(13,10) DEFAULT NULL,
  repayment_capacity decimal(13,10) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE cash_flow_detail DISABLE KEYS */;
/*!40000 ALTER TABLE cash_flow_detail ENABLE KEYS */;
DROP TABLE IF EXISTS category_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE category_type (
  category_id smallint(6) NOT NULL,
  category_lookup_id int(11) NOT NULL,
  PRIMARY KEY (category_id),
  KEY category_lookup_id (category_lookup_id),
  CONSTRAINT category_type_ibfk_1 FOREIGN KEY (category_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE category_type DISABLE KEYS */;
INSERT INTO category_type VALUES (1,81);
INSERT INTO category_type VALUES (2,82);
INSERT INTO category_type VALUES (3,83);
INSERT INTO category_type VALUES (4,84);
INSERT INTO category_type VALUES (5,86);
/*!40000 ALTER TABLE category_type ENABLE KEYS */;
DROP TABLE IF EXISTS change_log;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE change_log (
  change_log_id int(11) NOT NULL AUTO_INCREMENT,
  changed_by smallint(6) NOT NULL,
  modifier_name varchar(50) NOT NULL,
  entity_id int(11) DEFAULT NULL,
  entity_type smallint(6) DEFAULT NULL,
  changed_date date DEFAULT NULL,
  fields_changed varchar(250) DEFAULT NULL,
  PRIMARY KEY (change_log_id),
  KEY changed_by (changed_by),
  KEY change_log_idx (entity_type,entity_id,changed_date),
  CONSTRAINT change_log_ibfk_1 FOREIGN KEY (changed_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE change_log DISABLE KEYS */;
INSERT INTO change_log VALUES (1,1,'mifos',1,17,'2011-02-18',NULL);
INSERT INTO change_log VALUES (2,1,'mifos',2,21,'2011-02-18',NULL);
INSERT INTO change_log VALUES (3,1,'mifos',2,21,'2011-02-18',NULL);
INSERT INTO change_log VALUES (4,1,'mifos',1,17,'2011-02-21',NULL);
INSERT INTO change_log VALUES (5,1,'mifos',2,1,'2011-02-21',NULL);
INSERT INTO change_log VALUES (6,1,'mifos',3,1,'2011-02-21',NULL);
INSERT INTO change_log VALUES (7,1,'mifos',1,17,'2010-01-22',NULL);
INSERT INTO change_log VALUES (8,1,'mifos',4,12,'2010-01-22',NULL);
INSERT INTO change_log VALUES (9,1,'mifos',5,1,'2010-01-22',NULL);
INSERT INTO change_log VALUES (10,1,'mifos',6,1,'2010-01-22',NULL);
INSERT INTO change_log VALUES (11,1,'mifos',7,1,'2010-01-22',NULL);
INSERT INTO change_log VALUES (12,1,'mifos',4,2,'2010-01-22',NULL);
INSERT INTO change_log VALUES (13,1,'mifos',8,1,'2010-01-22',NULL);
INSERT INTO change_log VALUES (14,1,'mifos',5,2,'2011-02-21',NULL);
INSERT INTO change_log VALUES (15,1,'mifos',10,22,'2011-02-21',NULL);
INSERT INTO change_log VALUES (16,1,'mifos',11,22,'2011-02-21',NULL);
INSERT INTO change_log VALUES (17,1,'mifos',1,17,'2011-02-21',NULL);
INSERT INTO change_log VALUES (18,1,'mifos',12,22,'2011-02-21',NULL);
INSERT INTO change_log VALUES (19,1,'mifos',9,12,'2011-02-18',NULL);
INSERT INTO change_log VALUES (20,1,'mifos',10,1,'2011-02-18',NULL);
INSERT INTO change_log VALUES (21,1,'mifos',15,22,'2011-02-18',NULL);
INSERT INTO change_log VALUES (22,1,'mifos',1,17,'2010-02-22',NULL);
INSERT INTO change_log VALUES (23,1,'mifos',1,17,'2011-02-22',NULL);
INSERT INTO change_log VALUES (24,1,'mifos',7,2,'2011-02-22',NULL);
INSERT INTO change_log VALUES (25,1,'mifos',12,1,'2011-02-22',NULL);
INSERT INTO change_log VALUES (26,1,'mifos',13,1,'2011-02-22',NULL);
INSERT INTO change_log VALUES (27,1,'mifos',14,1,'2011-02-22',NULL);
INSERT INTO change_log VALUES (28,1,'mifos',11,12,'2011-02-22',NULL);
INSERT INTO change_log VALUES (29,1,'mifos',12,1,'2011-02-22',NULL);
INSERT INTO change_log VALUES (30,1,'mifos',13,1,'2011-02-22',NULL);
INSERT INTO change_log VALUES (31,1,'mifos',14,1,'2011-02-22',NULL);
INSERT INTO change_log VALUES (32,1,'mifos',2,2,'2011-02-22',NULL);
INSERT INTO change_log VALUES (33,1,'mifos',20,22,'2011-02-22',NULL);
INSERT INTO change_log VALUES (34,1,'mifos',1,17,'2011-02-23',NULL);
INSERT INTO change_log VALUES (35,1,'mifos',1,17,'2011-03-04',NULL);
INSERT INTO change_log VALUES (36,1,'mifos',7,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (37,1,'mifos',16,12,'2011-02-25',NULL);
INSERT INTO change_log VALUES (38,1,'mifos',17,1,'2011-02-25',NULL);
INSERT INTO change_log VALUES (39,1,'mifos',8,3,'2011-02-25',NULL);
INSERT INTO change_log VALUES (40,1,'mifos',1,17,'2010-10-11',NULL);
INSERT INTO change_log VALUES (41,1,'mifos',18,1,'2010-10-11',NULL);
INSERT INTO change_log VALUES (42,1,'mifos',25,22,'2010-10-11',NULL);
INSERT INTO change_log VALUES (43,1,'mifos',26,22,'2010-10-11',NULL);
INSERT INTO change_log VALUES (44,1,'mifos',27,22,'2010-10-11',NULL);
INSERT INTO change_log VALUES (45,1,'mifos',28,22,'2010-10-11',NULL);
INSERT INTO change_log VALUES (46,1,'mifos',29,22,'2010-10-11',NULL);
INSERT INTO change_log VALUES (47,1,'mifos',1,17,'2011-02-25',NULL);
INSERT INTO change_log VALUES (48,1,'mifos',20,12,'2011-02-25',NULL);
INSERT INTO change_log VALUES (49,1,'mifos',22,12,'2011-02-25',NULL);
INSERT INTO change_log VALUES (50,1,'mifos',1,17,'2020-01-01',NULL);
INSERT INTO change_log VALUES (51,1,'mifos',23,1,'2020-01-01',NULL);
INSERT INTO change_log VALUES (52,1,'mifos',35,22,'2020-01-01',NULL);
INSERT INTO change_log VALUES (53,1,'mifos',1,17,'2011-02-28',NULL);
INSERT INTO change_log VALUES (54,1,'mifos',24,1,'2011-02-28',NULL);
INSERT INTO change_log VALUES (55,1,'mifos',37,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (56,1,'mifos',38,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (57,1,'mifos',39,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (58,1,'mifos',40,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (59,1,'mifos',41,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (60,1,'mifos',42,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (61,1,'mifos',1,17,'2010-10-11',NULL);
INSERT INTO change_log VALUES (62,1,'mifos',1,17,'2011-02-28',NULL);
INSERT INTO change_log VALUES (63,1,'mifos',44,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (64,1,'mifos',45,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (65,1,'mifos',46,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (66,1,'mifos',47,22,'2011-02-28',NULL);
INSERT INTO change_log VALUES (67,1,'mifos',1,17,'2011-03-03',NULL);
INSERT INTO change_log VALUES (68,1,'mifos',25,12,'2011-01-01',NULL);
INSERT INTO change_log VALUES (69,1,'mifos',26,1,'2011-01-01',NULL);
INSERT INTO change_log VALUES (70,1,'mifos',50,22,'2011-03-03',NULL);
INSERT INTO change_log VALUES (71,1,'mifos',1,17,'2011-03-04',NULL);
INSERT INTO change_log VALUES (72,1,'mifos',13,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (73,1,'mifos',5,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (74,1,'mifos',16,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (75,1,'mifos',15,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (76,1,'mifos',6,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (77,1,'mifos',11,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (78,1,'mifos',4,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (79,1,'mifos',10,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (80,1,'mifos',12,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (81,1,'mifos',14,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (82,1,'mifos',2,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (83,1,'mifos',17,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (84,1,'mifos',7,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (85,1,'mifos',9,2,'2011-03-04',NULL);
INSERT INTO change_log VALUES (86,1,'mifos',1,17,'2010-10-11',NULL);
INSERT INTO change_log VALUES (87,1,'mifos',14,2,'2010-10-11',NULL);
INSERT INTO change_log VALUES (88,1,'mifos',1,17,'2011-03-04',NULL);
INSERT INTO change_log VALUES (89,1,'mifos',52,22,'2011-03-04',NULL);
INSERT INTO change_log VALUES (90,1,'mifos',1,17,'2011-03-07',NULL);
INSERT INTO change_log VALUES (91,1,'mifos',1,17,'2010-10-11',NULL);
INSERT INTO change_log VALUES (92,1,'mifos',28,1,'2010-10-11',NULL);
INSERT INTO change_log VALUES (93,1,'mifos',29,1,'2010-10-11',NULL);
INSERT INTO change_log VALUES (94,1,'mifos',1,17,'2011-03-08',NULL);
INSERT INTO change_log VALUES (95,1,'mifos',15,2,'2011-01-01',NULL);
INSERT INTO change_log VALUES (96,1,'mifos',30,1,'2011-01-01',NULL);
INSERT INTO change_log VALUES (97,1,'mifos',59,21,'2011-03-08',NULL);
INSERT INTO change_log VALUES (98,1,'mifos',1,17,'2011-03-09',NULL);
INSERT INTO change_log VALUES (99,1,'mifos',32,12,'2011-03-10',NULL);
INSERT INTO change_log VALUES (100,1,'mifos',33,1,'2011-03-10',NULL);
INSERT INTO change_log VALUES (101,1,'mifos',1,17,'2011-03-14',NULL);
INSERT INTO change_log VALUES (102,1,'mifos',36,12,'2011-03-14',NULL);
INSERT INTO change_log VALUES (103,1,'mifos',37,1,'2011-03-14',NULL);
INSERT INTO change_log VALUES (104,1,'mifos',67,21,'2011-03-14',NULL);
INSERT INTO change_log VALUES (105,1,'mifos',39,12,'2011-03-15',NULL);
INSERT INTO change_log VALUES (106,1,'mifos',40,1,'2011-03-15',NULL);
/*!40000 ALTER TABLE change_log ENABLE KEYS */;
DROP TABLE IF EXISTS change_log_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE change_log_detail (
  sequence_num int(11) NOT NULL AUTO_INCREMENT,
  change_log_id int(11) NOT NULL,
  field_name varchar(100) DEFAULT NULL,
  old_value varchar(200) DEFAULT NULL,
  new_value varchar(200) DEFAULT NULL,
  PRIMARY KEY (sequence_num),
  KEY change_log_id (change_log_id),
  CONSTRAINT change_log_detail_ibfk_1 FOREIGN KEY (change_log_id) REFERENCES change_log (change_log_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE change_log_detail DISABLE KEYS */;
INSERT INTO change_log_detail VALUES (1,1,'lastLogin','-','18/02/2011');
INSERT INTO change_log_detail VALUES (2,2,'activationDate','-','18/02/2011');
INSERT INTO change_log_detail VALUES (3,2,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (4,3,'savingsBalance','-','1000.0');
INSERT INTO change_log_detail VALUES (5,4,'lastLogin','18/02/2011','21/02/2011');
INSERT INTO change_log_detail VALUES (6,5,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (7,6,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (8,7,'lastLogin','21/02/2011','22/01/2010');
INSERT INTO change_log_detail VALUES (9,8,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (10,9,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (11,10,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (12,11,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (13,12,'Frequency Of Installments','Week(s)','Month(s)');
INSERT INTO change_log_detail VALUES (14,13,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (15,14,'Product Instance Name','EmergencyLoan','ClientEmergencyLoan');
INSERT INTO change_log_detail VALUES (16,15,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (17,16,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (18,17,'lastLogin','22/01/2010','21/02/2011');
INSERT INTO change_log_detail VALUES (19,18,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (20,19,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (21,20,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (22,21,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (23,22,'lastLogin','21/02/2011','22/02/2010');
INSERT INTO change_log_detail VALUES (24,23,'lastLogin','22/02/2010','22/02/2011');
INSERT INTO change_log_detail VALUES (25,24,'Fee Types','-','oneTimeFee');
INSERT INTO change_log_detail VALUES (26,25,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (27,26,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (28,27,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (29,28,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (30,29,'Name','-','Default Group');
INSERT INTO change_log_detail VALUES (31,29,'groupFlag','0','1');
INSERT INTO change_log_detail VALUES (32,30,'groupFlag','0','1');
INSERT INTO change_log_detail VALUES (33,30,'Name','-','Default Group');
INSERT INTO change_log_detail VALUES (34,31,'Name','-','Default Group');
INSERT INTO change_log_detail VALUES (35,31,'groupFlag','0','1');
INSERT INTO change_log_detail VALUES (36,32,'Fee Types','-','disbursementFee');
INSERT INTO change_log_detail VALUES (37,33,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (38,34,'lastLogin','22/02/2011','23/02/2011');
INSERT INTO change_log_detail VALUES (39,35,'lastLogin','23/02/2011','04/03/2011');
INSERT INTO change_log_detail VALUES (40,36,'Min  Rate','24.0','1.0');
INSERT INTO change_log_detail VALUES (41,36,'Max  Rate','24.0','99.0');
INSERT INTO change_log_detail VALUES (42,37,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (43,38,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (44,39,'Applicable For','Centers','Clients');
INSERT INTO change_log_detail VALUES (45,40,'lastLogin','04/03/2011','11/10/2010');
INSERT INTO change_log_detail VALUES (46,41,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (47,42,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (48,43,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (49,44,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (50,45,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (51,46,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (52,47,'lastLogin','11/10/2010','25/02/2011');
INSERT INTO change_log_detail VALUES (53,48,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (54,49,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (55,50,'lastLogin','25/02/2011','01/01/2020');
INSERT INTO change_log_detail VALUES (56,51,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (57,52,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (58,53,'lastLogin','01/01/2020','28/02/2011');
INSERT INTO change_log_detail VALUES (59,54,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (60,55,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (61,56,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (62,57,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (63,58,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (64,59,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (65,60,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (66,61,'lastLogin','28/02/2011','11/10/2010');
INSERT INTO change_log_detail VALUES (67,62,'lastLogin','11/10/2010','28/02/2011');
INSERT INTO change_log_detail VALUES (68,63,'Status','Application Pending Approval','Partial Application');
INSERT INTO change_log_detail VALUES (69,64,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (70,65,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (71,66,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (72,67,'lastLogin','28/02/2011','03/03/2011');
INSERT INTO change_log_detail VALUES (73,68,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (74,69,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (75,70,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (76,71,'lastLogin','03/03/2011','04/03/2011');
INSERT INTO change_log_detail VALUES (77,72,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (78,73,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (79,74,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (80,75,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (81,76,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (82,77,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (83,78,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (84,79,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (85,80,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (86,81,'variableInstallmentsAllowed','1','0');
INSERT INTO change_log_detail VALUES (87,81,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (88,82,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (89,83,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (90,84,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (91,85,'Sources of funds','-','Funding Org A');
INSERT INTO change_log_detail VALUES (92,86,'lastLogin','04/03/2011','11/10/2010');
INSERT INTO change_log_detail VALUES (93,87,'variableInstallmentsAllowed','0','1');
INSERT INTO change_log_detail VALUES (94,88,'lastLogin','11/10/2010','04/03/2011');
INSERT INTO change_log_detail VALUES (95,89,'Status','Application Pending Approval','Application Approved');
INSERT INTO change_log_detail VALUES (96,90,'lastLogin','04/03/2011','07/03/2011');
INSERT INTO change_log_detail VALUES (97,91,'lastLogin','07/03/2011','11/10/2010');
INSERT INTO change_log_detail VALUES (98,92,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (99,93,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (100,94,'lastLogin','11/10/2010','08/03/2011');
INSERT INTO change_log_detail VALUES (101,95,'Grace Period Type','None','Grace on all repayments');
INSERT INTO change_log_detail VALUES (102,95,'Grace Period Duration','0','10');
INSERT INTO change_log_detail VALUES (103,96,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (104,97,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (105,97,'activationDate','-','08/03/2011');
INSERT INTO change_log_detail VALUES (106,98,'lastLogin','08/03/2011','09/03/2011');
INSERT INTO change_log_detail VALUES (107,99,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (108,100,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (109,101,'lastLogin','09/03/2011','14/03/2011');
INSERT INTO change_log_detail VALUES (110,102,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (111,103,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (112,104,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (113,104,'activationDate','-','14/03/2011');
INSERT INTO change_log_detail VALUES (114,105,'Status','Application Pending Approval','Active');
INSERT INTO change_log_detail VALUES (115,106,'Status','Application Pending Approval','Active');
/*!40000 ALTER TABLE change_log_detail ENABLE KEYS */;
DROP TABLE IF EXISTS checklist;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE checklist (
  checklist_id smallint(6) NOT NULL AUTO_INCREMENT,
  checklist_name varchar(100) DEFAULT NULL,
  checklist_status smallint(6) NOT NULL DEFAULT '1',
  locale_id smallint(6) NOT NULL,
  created_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  PRIMARY KEY (checklist_id),
  KEY locale_id (locale_id),
  KEY created_by (created_by),
  KEY updated_by (updated_by),
  CONSTRAINT checklist_ibfk_1 FOREIGN KEY (locale_id) REFERENCES supported_locale (locale_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT checklist_ibfk_2 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT checklist_ibfk_3 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE checklist DISABLE KEYS */;
/*!40000 ALTER TABLE checklist ENABLE KEYS */;
DROP TABLE IF EXISTS checklist_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE checklist_detail (
  detail_id int(11) NOT NULL AUTO_INCREMENT,
  checklist_id smallint(6) DEFAULT NULL,
  locale_id smallint(6) DEFAULT NULL,
  detail_text varchar(250) DEFAULT NULL,
  answer_type smallint(6) NOT NULL,
  PRIMARY KEY (detail_id),
  KEY locale_id (locale_id),
  KEY chk_detail_idx (checklist_id,locale_id),
  CONSTRAINT checklist_detail_ibfk_1 FOREIGN KEY (checklist_id) REFERENCES checklist (checklist_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT checklist_detail_ibfk_2 FOREIGN KEY (locale_id) REFERENCES supported_locale (locale_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE checklist_detail DISABLE KEYS */;
/*!40000 ALTER TABLE checklist_detail ENABLE KEYS */;
DROP TABLE IF EXISTS client_initial_savings_offering;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE client_initial_savings_offering (
  client_offering_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  prd_offering_id smallint(6) NOT NULL,
  PRIMARY KEY (client_offering_id),
  KEY customer_id (customer_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT client_initial_savings_offering_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT client_initial_savings_offering_ibfk_2 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE client_initial_savings_offering DISABLE KEYS */;
/*!40000 ALTER TABLE client_initial_savings_offering ENABLE KEYS */;
DROP TABLE IF EXISTS client_perf_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE client_perf_history (
  id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  last_loan_amnt decimal(21,4) DEFAULT NULL,
  last_loan_amnt_currency_id smallint(6) DEFAULT NULL,
  total_active_loans smallint(6) DEFAULT NULL,
  total_savings_amnt decimal(21,4) DEFAULT NULL,
  total_savings_amnt_currency_id smallint(6) DEFAULT NULL,
  delinquint_portfolio decimal(21,4) DEFAULT NULL,
  delinquint_portfolio_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY customer_id (customer_id),
  CONSTRAINT client_perf_history_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE client_perf_history DISABLE KEYS */;
INSERT INTO client_perf_history VALUES (1,2,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (2,3,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (3,5,'0.0000',2,1,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (4,6,'0.0000',2,1,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (5,7,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (6,8,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (7,10,'0.0000',2,1,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (8,12,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (9,13,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (10,14,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (11,17,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (12,18,'0.0000',2,5,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (13,23,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (14,24,'0.0000',2,5,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (15,26,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (16,28,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (17,29,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (18,30,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (19,31,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (20,33,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (21,37,'0.0000',2,0,'0.0000',2,'0.0000',2);
INSERT INTO client_perf_history VALUES (22,40,'0.0000',2,0,'0.0000',2,'0.0000',2);
/*!40000 ALTER TABLE client_perf_history ENABLE KEYS */;
DROP TABLE IF EXISTS coa;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE coa (
  coa_id smallint(6) NOT NULL AUTO_INCREMENT,
  coa_name varchar(150) NOT NULL,
  glcode_id smallint(6) NOT NULL,
  category_type varchar(20) DEFAULT NULL,
  PRIMARY KEY (coa_id),
  KEY glcode_id (glcode_id),
  CONSTRAINT coa_ibfk_1 FOREIGN KEY (glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE coa DISABLE KEYS */;
INSERT INTO coa VALUES (1,'ASSETS',1,'ASSET');
INSERT INTO coa VALUES (2,'Cash and bank balances',2,NULL);
INSERT INTO coa VALUES (3,'Petty Cash Accounts',3,NULL);
INSERT INTO coa VALUES (4,'Cash 1',4,NULL);
INSERT INTO coa VALUES (5,'Cash 2',5,NULL);
INSERT INTO coa VALUES (6,'Bank Balances',6,NULL);
INSERT INTO coa VALUES (7,'Bank Account 1',7,NULL);
INSERT INTO coa VALUES (8,'Bank Account 2',8,NULL);
INSERT INTO coa VALUES (9,'Loan Portfolio',9,NULL);
INSERT INTO coa VALUES (10,'Loans and Advances',10,NULL);
INSERT INTO coa VALUES (11,'IGLoan',11,NULL);
INSERT INTO coa VALUES (12,'ManagedICICI-IGLoan',12,NULL);
INSERT INTO coa VALUES (13,'SPLoan',13,NULL);
INSERT INTO coa VALUES (14,'ManagedICICI-SPLoan',14,NULL);
INSERT INTO coa VALUES (15,'WFLoan',15,NULL);
INSERT INTO coa VALUES (16,'Managed WFLoan',16,NULL);
INSERT INTO coa VALUES (17,'Emergency Loans',17,NULL);
INSERT INTO coa VALUES (18,'Special Loans',18,NULL);
INSERT INTO coa VALUES (19,'Micro Enterprises Loans',19,NULL);
INSERT INTO coa VALUES (20,'Loans to clients',20,NULL);
INSERT INTO coa VALUES (21,'Loan Loss Provisions',21,NULL);
INSERT INTO coa VALUES (22,'Write-offs',22,NULL);
INSERT INTO coa VALUES (23,'LIABILITIES',23,'LIABILITY');
INSERT INTO coa VALUES (24,'Interest Payable',24,NULL);
INSERT INTO coa VALUES (25,'Interest payable on clients savings',25,NULL);
INSERT INTO coa VALUES (26,'Interest on mandatory savings',26,NULL);
INSERT INTO coa VALUES (27,'Clients Deposits',27,NULL);
INSERT INTO coa VALUES (28,'Clients Deposits',28,NULL);
INSERT INTO coa VALUES (29,'Emergency Fund',29,NULL);
INSERT INTO coa VALUES (30,'Margin Money-1',30,NULL);
INSERT INTO coa VALUES (31,'Margin Money-2',31,NULL);
INSERT INTO coa VALUES (32,'Village Development Fund',32,NULL);
INSERT INTO coa VALUES (33,'Savings accounts',33,NULL);
INSERT INTO coa VALUES (34,'Mandatory Savings',34,NULL);
INSERT INTO coa VALUES (35,'Mandatory Savings',35,NULL);
INSERT INTO coa VALUES (36,'Mandatory Savings Accounts',36,NULL);
INSERT INTO coa VALUES (37,'INCOME',37,'INCOME');
INSERT INTO coa VALUES (38,'Direct Income',38,NULL);
INSERT INTO coa VALUES (39,'Interest income from loans',39,NULL);
INSERT INTO coa VALUES (40,'Interest',40,NULL);
INSERT INTO coa VALUES (41,'Interest on loans',41,NULL);
INSERT INTO coa VALUES (42,'Penalty',42,NULL);
INSERT INTO coa VALUES (43,'Income from micro credit & lending activities',43,NULL);
INSERT INTO coa VALUES (44,'Processing Fees',44,NULL);
INSERT INTO coa VALUES (45,'Annual Subscription Fee',45,NULL);
INSERT INTO coa VALUES (46,'Emergency Loan Documentation Fee',46,NULL);
INSERT INTO coa VALUES (47,'Sale of Publication',47,NULL);
INSERT INTO coa VALUES (48,'Fines & Penalties',48,NULL);
INSERT INTO coa VALUES (49,'Miscelleneous Income',49,NULL);
INSERT INTO coa VALUES (50,'Fees',50,NULL);
INSERT INTO coa VALUES (51,'Income from 999 Account',51,NULL);
INSERT INTO coa VALUES (52,'EXPENDITURE',52,'EXPENDITURE');
INSERT INTO coa VALUES (53,'Direct Expenditure',53,NULL);
INSERT INTO coa VALUES (54,'Cost of Funds',54,NULL);
INSERT INTO coa VALUES (55,'Interest on clients voluntary savings',55,NULL);
INSERT INTO coa VALUES (56,'Interest on clients mandatory savings',56,NULL);
/*!40000 ALTER TABLE coa ENABLE KEYS */;
DROP TABLE IF EXISTS coa_idmapper;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE coa_idmapper (
  constant_id smallint(6) NOT NULL,
  coa_id smallint(6) NOT NULL,
  description varchar(50) DEFAULT NULL,
  PRIMARY KEY (constant_id),
  KEY coa_id (coa_id),
  CONSTRAINT coa_idmapper_ibfk_1 FOREIGN KEY (coa_id) REFERENCES coa (coa_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE coa_idmapper DISABLE KEYS */;
/*!40000 ALTER TABLE coa_idmapper ENABLE KEYS */;
DROP TABLE IF EXISTS coahierarchy;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE coahierarchy (
  coa_id smallint(6) NOT NULL,
  parent_coaid smallint(6) DEFAULT NULL,
  KEY coa_id (coa_id),
  KEY parent_coaid (parent_coaid),
  CONSTRAINT coahierarchy_ibfk_1 FOREIGN KEY (coa_id) REFERENCES coa (coa_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT coahierarchy_ibfk_2 FOREIGN KEY (parent_coaid) REFERENCES coa (coa_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE coahierarchy DISABLE KEYS */;
INSERT INTO coahierarchy VALUES (1,NULL);
INSERT INTO coahierarchy VALUES (2,1);
INSERT INTO coahierarchy VALUES (3,2);
INSERT INTO coahierarchy VALUES (4,3);
INSERT INTO coahierarchy VALUES (5,3);
INSERT INTO coahierarchy VALUES (6,2);
INSERT INTO coahierarchy VALUES (7,6);
INSERT INTO coahierarchy VALUES (8,6);
INSERT INTO coahierarchy VALUES (9,1);
INSERT INTO coahierarchy VALUES (10,9);
INSERT INTO coahierarchy VALUES (11,10);
INSERT INTO coahierarchy VALUES (12,10);
INSERT INTO coahierarchy VALUES (13,10);
INSERT INTO coahierarchy VALUES (14,10);
INSERT INTO coahierarchy VALUES (15,10);
INSERT INTO coahierarchy VALUES (16,10);
INSERT INTO coahierarchy VALUES (17,10);
INSERT INTO coahierarchy VALUES (18,10);
INSERT INTO coahierarchy VALUES (19,10);
INSERT INTO coahierarchy VALUES (20,10);
INSERT INTO coahierarchy VALUES (21,9);
INSERT INTO coahierarchy VALUES (22,21);
INSERT INTO coahierarchy VALUES (23,NULL);
INSERT INTO coahierarchy VALUES (24,23);
INSERT INTO coahierarchy VALUES (25,24);
INSERT INTO coahierarchy VALUES (26,25);
INSERT INTO coahierarchy VALUES (27,23);
INSERT INTO coahierarchy VALUES (28,27);
INSERT INTO coahierarchy VALUES (29,28);
INSERT INTO coahierarchy VALUES (30,28);
INSERT INTO coahierarchy VALUES (31,28);
INSERT INTO coahierarchy VALUES (32,28);
INSERT INTO coahierarchy VALUES (33,28);
INSERT INTO coahierarchy VALUES (34,23);
INSERT INTO coahierarchy VALUES (35,34);
INSERT INTO coahierarchy VALUES (36,35);
INSERT INTO coahierarchy VALUES (37,NULL);
INSERT INTO coahierarchy VALUES (38,37);
INSERT INTO coahierarchy VALUES (39,38);
INSERT INTO coahierarchy VALUES (40,39);
INSERT INTO coahierarchy VALUES (41,39);
INSERT INTO coahierarchy VALUES (42,39);
INSERT INTO coahierarchy VALUES (43,38);
INSERT INTO coahierarchy VALUES (44,43);
INSERT INTO coahierarchy VALUES (45,43);
INSERT INTO coahierarchy VALUES (46,43);
INSERT INTO coahierarchy VALUES (47,43);
INSERT INTO coahierarchy VALUES (48,43);
INSERT INTO coahierarchy VALUES (49,43);
INSERT INTO coahierarchy VALUES (50,43);
INSERT INTO coahierarchy VALUES (51,37);
INSERT INTO coahierarchy VALUES (52,NULL);
INSERT INTO coahierarchy VALUES (53,52);
INSERT INTO coahierarchy VALUES (54,53);
INSERT INTO coahierarchy VALUES (55,54);
INSERT INTO coahierarchy VALUES (56,54);
/*!40000 ALTER TABLE coahierarchy ENABLE KEYS */;
DROP TABLE IF EXISTS config_key_value_integer;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE config_key_value_integer (
  configuration_id int(11) NOT NULL AUTO_INCREMENT,
  configuration_key varchar(100) NOT NULL,
  configuration_value int(11) NOT NULL,
  PRIMARY KEY (configuration_id),
  UNIQUE KEY configuration_key (configuration_key)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE config_key_value_integer DISABLE KEYS */;
INSERT INTO config_key_value_integer VALUES (1,'x',0);
INSERT INTO config_key_value_integer VALUES (2,' ',0);
INSERT INTO config_key_value_integer VALUES (3,'jasperReportIsHidden',1);
INSERT INTO config_key_value_integer VALUES (4,'loanIndividualMonitoringIsEnabled',0);
INSERT INTO config_key_value_integer VALUES (5,'repaymentSchedulesIndependentOfMeetingIsEnabled',0);
INSERT INTO config_key_value_integer VALUES (6,'CenterHierarchyExists',1);
INSERT INTO config_key_value_integer VALUES (7,'ClientCanExistOutsideGroup',1);
INSERT INTO config_key_value_integer VALUES (8,'GroupCanApplyLoans',1);
INSERT INTO config_key_value_integer VALUES (9,'minDaysBetweenDisbursalAndFirstRepaymentDay',1);
INSERT INTO config_key_value_integer VALUES (10,'maxDaysBetweenDisbursalAndFirstRepaymentDay',365);
INSERT INTO config_key_value_integer VALUES (11,'AdministrativeDocumentsIsEnabled',1);
/*!40000 ALTER TABLE config_key_value_integer ENABLE KEYS */;
DROP TABLE IF EXISTS country;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE country (
  country_id smallint(6) NOT NULL,
  country_name varchar(100) DEFAULT NULL,
  country_short_name varchar(10) DEFAULT NULL,
  PRIMARY KEY (country_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE country DISABLE KEYS */;
INSERT INTO country VALUES (1,'America','US');
INSERT INTO country VALUES (2,'India','IN');
INSERT INTO country VALUES (3,'Spain','ES');
INSERT INTO country VALUES (4,'England','ENG');
INSERT INTO country VALUES (5,'South Africa','SA');
INSERT INTO country VALUES (6,'United Kingdom','GB');
INSERT INTO country VALUES (7,'Iceland','IS');
INSERT INTO country VALUES (8,'Spain','ES');
INSERT INTO country VALUES (9,'France','FR');
INSERT INTO country VALUES (10,'China','CN');
INSERT INTO country VALUES (11,'Kenya','KE');
INSERT INTO country VALUES (12,'Tanzania','TZ');
INSERT INTO country VALUES (13,'Uganda','UG');
INSERT INTO country VALUES (14,'Algeria','DZ');
INSERT INTO country VALUES (15,'Bahrain','BH');
INSERT INTO country VALUES (16,'Comoros','KM');
INSERT INTO country VALUES (17,'Chad','TD');
INSERT INTO country VALUES (18,'Djibouti','DJ');
INSERT INTO country VALUES (19,'Egypt','EG');
INSERT INTO country VALUES (20,'Eritrea','ER');
INSERT INTO country VALUES (21,'Iraq','IQ');
INSERT INTO country VALUES (22,'Israel','IL');
INSERT INTO country VALUES (23,'Jordan','JO');
INSERT INTO country VALUES (24,'Kuwait','KW');
INSERT INTO country VALUES (25,'Lebanon','LB');
INSERT INTO country VALUES (26,'Libyan Arab Rebublic','LY');
INSERT INTO country VALUES (27,'Mauritania','MR');
INSERT INTO country VALUES (28,'Morocco','MA');
INSERT INTO country VALUES (29,'Oman','OM');
INSERT INTO country VALUES (30,'Qatar','QA');
INSERT INTO country VALUES (31,'Saudi Arabia','SA');
INSERT INTO country VALUES (32,'Somalia','SO');
INSERT INTO country VALUES (33,'Sudan','SD');
INSERT INTO country VALUES (34,'Syrian Arab Republic','SY');
INSERT INTO country VALUES (35,'Tunisia','TN');
INSERT INTO country VALUES (36,'United Arab Emirates','AE');
INSERT INTO country VALUES (37,'Yemen','YE');
INSERT INTO country VALUES (38,'Palestinian Territory, Occupied','PS');
INSERT INTO country VALUES (39,'Western Sahara','EH');
INSERT INTO country VALUES (40,'Angola','AO');
INSERT INTO country VALUES (41,'Brazil','BR');
INSERT INTO country VALUES (42,'Cape Verde','CV');
INSERT INTO country VALUES (43,'Guinea-Bissau','GW');
INSERT INTO country VALUES (44,'Equatorial Guinea','GQ');
INSERT INTO country VALUES (45,'Macau','MO');
INSERT INTO country VALUES (46,'Mozambique','MZ');
INSERT INTO country VALUES (47,'Portugal','PT');
INSERT INTO country VALUES (48,'Sao Tome and Principe','ST');
INSERT INTO country VALUES (49,'Cambodia','KH');
INSERT INTO country VALUES (50,'Laos','LA');
INSERT INTO country VALUES (51,'Hungary','HU');
/*!40000 ALTER TABLE country ENABLE KEYS */;
DROP TABLE IF EXISTS currency;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE currency (
  currency_id smallint(6) NOT NULL AUTO_INCREMENT,
  currency_name varchar(50) DEFAULT NULL,
  rounding_amount decimal(6,3) DEFAULT NULL,
  currency_code varchar(3) DEFAULT NULL,
  PRIMARY KEY (currency_id)
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE currency DISABLE KEYS */;
INSERT INTO currency VALUES (1,'US Dollar','1.000','USD');
INSERT INTO currency VALUES (2,'Indian Rupee','1.000','INR');
INSERT INTO currency VALUES (3,'Euro','1.000','EUR');
INSERT INTO currency VALUES (4,'Pound Sterling','1.000','GBP');
INSERT INTO currency VALUES (5,'United Arab Emirates dirham','1.000','AED');
INSERT INTO currency VALUES (6,'Afghani','1.000','AFN');
INSERT INTO currency VALUES (7,'Lek','1.000','ALL');
INSERT INTO currency VALUES (8,'Armenian dram','1.000','AMD');
INSERT INTO currency VALUES (9,'Netherlands Antillean guilder','1.000','ANG');
INSERT INTO currency VALUES (10,'Kwanza','1.000','AOA');
INSERT INTO currency VALUES (11,'Argentine peso','1.000','ARS');
INSERT INTO currency VALUES (12,'Australian dollar','1.000','AUD');
INSERT INTO currency VALUES (13,'Aruban guilder','1.000','AWG');
INSERT INTO currency VALUES (14,'Azerbaijanian manat','1.000','AZN');
INSERT INTO currency VALUES (15,'Convertible marks','1.000','BAM');
INSERT INTO currency VALUES (16,'Barbados dollar','1.000','BBD');
INSERT INTO currency VALUES (17,'Bangladeshi taka','1.000','BDT');
INSERT INTO currency VALUES (18,'Bulgarian lev','1.000','BGN');
INSERT INTO currency VALUES (19,'Bahraini dinar','1.000','BHD');
INSERT INTO currency VALUES (20,'Burundian franc','1.000','BIF');
INSERT INTO currency VALUES (21,'Bermudian dollar','1.000','BMD');
INSERT INTO currency VALUES (22,'Brunei dollar','1.000','BND');
INSERT INTO currency VALUES (23,'Boliviano','1.000','BOB');
INSERT INTO currency VALUES (24,'Bolivian Mvdol','1.000','BOV');
INSERT INTO currency VALUES (25,'Brazilian real','1.000','BRL');
INSERT INTO currency VALUES (26,'Bahamian dollar','1.000','BSD');
INSERT INTO currency VALUES (27,'Ngultrum','1.000','BTN');
INSERT INTO currency VALUES (28,'Pula','1.000','BWP');
INSERT INTO currency VALUES (29,'Belarussian ruble','1.000','BYR');
INSERT INTO currency VALUES (30,'Belize dollar','1.000','BZD');
INSERT INTO currency VALUES (31,'Canadian dollar','1.000','CAD');
INSERT INTO currency VALUES (32,'Franc Congolais','1.000','CDF');
INSERT INTO currency VALUES (33,'WIR euro','1.000','CHE');
INSERT INTO currency VALUES (34,'Swiss franc','1.000','CHF');
INSERT INTO currency VALUES (35,'WIR franc','1.000','CHW');
INSERT INTO currency VALUES (36,'Unidad de Fomento','1.000','CLF');
INSERT INTO currency VALUES (37,'Chilean peso','1.000','CLP');
INSERT INTO currency VALUES (38,'Renminbi','1.000','CNY');
INSERT INTO currency VALUES (39,'Colombian peso','1.000','COP');
INSERT INTO currency VALUES (40,'Unidad de Valor Real','1.000','COU');
INSERT INTO currency VALUES (41,'Costa Rican colon','1.000','CRC');
INSERT INTO currency VALUES (42,'Cuban peso','1.000','CUP');
INSERT INTO currency VALUES (43,'Cape Verde escudo','1.000','CVE');
INSERT INTO currency VALUES (44,'Czech koruna','1.000','CZK');
INSERT INTO currency VALUES (45,'Djibouti franc','1.000','DJF');
INSERT INTO currency VALUES (46,'Danish krone','1.000','DKK');
INSERT INTO currency VALUES (47,'Dominican peso','1.000','DOP');
INSERT INTO currency VALUES (48,'Algerian dinar','1.000','DZD');
INSERT INTO currency VALUES (49,'Kroon','1.000','EEK');
INSERT INTO currency VALUES (50,'Egyptian pound','1.000','EGP');
INSERT INTO currency VALUES (51,'Nakfa','1.000','ERN');
INSERT INTO currency VALUES (52,'Ethiopian birr','1.000','ETB');
INSERT INTO currency VALUES (53,'Fiji dollar','1.000','FJD');
INSERT INTO currency VALUES (54,'Falkland Islands pound','1.000','FKP');
INSERT INTO currency VALUES (55,'Lari','1.000','GEL');
INSERT INTO currency VALUES (56,'Cedi','1.000','GHS');
INSERT INTO currency VALUES (57,'Gibraltar pound','1.000','GIP');
INSERT INTO currency VALUES (58,'Dalasi','1.000','GMD');
INSERT INTO currency VALUES (59,'Guinea franc','1.000','GNF');
INSERT INTO currency VALUES (60,'Quetzal','1.000','GTQ');
INSERT INTO currency VALUES (61,'Guyana dollar','1.000','GYD');
INSERT INTO currency VALUES (62,'Hong Kong dollar','1.000','HKD');
INSERT INTO currency VALUES (63,'Lempira','1.000','HNL');
INSERT INTO currency VALUES (64,'Croatian kuna','1.000','HRK');
INSERT INTO currency VALUES (65,'Haiti gourde','1.000','HTG');
INSERT INTO currency VALUES (66,'Forint','1.000','HUF');
INSERT INTO currency VALUES (67,'Rupiah','1.000','IDR');
INSERT INTO currency VALUES (68,'Israeli new sheqel','1.000','ILS');
INSERT INTO currency VALUES (69,'Iraqi dinar','1.000','IQD');
INSERT INTO currency VALUES (70,'Iranian rial','1.000','IRR');
INSERT INTO currency VALUES (71,'Iceland krona','1.000','ISK');
INSERT INTO currency VALUES (72,'Jamaican dollar','1.000','JMD');
INSERT INTO currency VALUES (73,'Jordanian dinar','1.000','JOD');
INSERT INTO currency VALUES (74,'Japanese yen','1.000','JPY');
INSERT INTO currency VALUES (75,'Kenyan shilling','1.000','KES');
INSERT INTO currency VALUES (76,'Som','1.000','KGS');
INSERT INTO currency VALUES (77,'Riel','1.000','KHR');
INSERT INTO currency VALUES (78,'Comoro franc','1.000','KMF');
INSERT INTO currency VALUES (79,'North Korean won','1.000','KPW');
INSERT INTO currency VALUES (80,'South Korean won','1.000','KRW');
INSERT INTO currency VALUES (81,'Kuwaiti dinar','1.000','KWD');
INSERT INTO currency VALUES (82,'Cayman Islands dollar','1.000','KYD');
INSERT INTO currency VALUES (83,'Tenge','1.000','KZT');
INSERT INTO currency VALUES (84,'Kip','1.000','LAK');
INSERT INTO currency VALUES (85,'Lebanese pound','1.000','LBP');
INSERT INTO currency VALUES (86,'Sri Lanka rupee','1.000','LKR');
INSERT INTO currency VALUES (87,'Liberian dollar','1.000','LRD');
INSERT INTO currency VALUES (88,'Loti','1.000','LSL');
INSERT INTO currency VALUES (89,'Lithuanian litas','1.000','LTL');
INSERT INTO currency VALUES (90,'Latvian lats','1.000','LVL');
INSERT INTO currency VALUES (91,'Libyan dinar','1.000','LYD');
INSERT INTO currency VALUES (92,'Moroccan dirham','1.000','MAD');
INSERT INTO currency VALUES (93,'Moldovan leu','1.000','MDL');
INSERT INTO currency VALUES (94,'Malagasy ariary','1.000','MGA');
INSERT INTO currency VALUES (95,'Denar','1.000','MKD');
INSERT INTO currency VALUES (96,'Kyat','1.000','MMK');
INSERT INTO currency VALUES (97,'Tugrik','1.000','MNT');
INSERT INTO currency VALUES (98,'Pataca','1.000','MOP');
INSERT INTO currency VALUES (99,'Ouguiya','1.000','MRO');
INSERT INTO currency VALUES (100,'Mauritius rupee','1.000','MUR');
INSERT INTO currency VALUES (101,'Rufiyaa','1.000','MVR');
INSERT INTO currency VALUES (102,'Kwacha','1.000','MWK');
INSERT INTO currency VALUES (103,'Mexican peso','1.000','MXN');
INSERT INTO currency VALUES (104,'Mexican Unidad de Inversion','1.000','MXV');
INSERT INTO currency VALUES (105,'Malaysian ringgit','1.000','MYR');
INSERT INTO currency VALUES (106,'Metical','1.000','MZN');
INSERT INTO currency VALUES (107,'Namibian dollar','1.000','NAD');
INSERT INTO currency VALUES (108,'Naira','1.000','NGN');
INSERT INTO currency VALUES (109,'Cordoba oro','1.000','NIO');
INSERT INTO currency VALUES (110,'Norwegian krone','1.000','NOK');
INSERT INTO currency VALUES (111,'Nepalese rupee','1.000','NPR');
INSERT INTO currency VALUES (112,'New Zealand dollar','1.000','NZD');
INSERT INTO currency VALUES (113,'Rial Omani','1.000','OMR');
INSERT INTO currency VALUES (114,'Balboa','1.000','PAB');
INSERT INTO currency VALUES (115,'Nuevo sol','1.000','PEN');
INSERT INTO currency VALUES (116,'Kina','1.000','PGK');
INSERT INTO currency VALUES (117,'Philippine peso','1.000','PHP');
INSERT INTO currency VALUES (118,'Pakistan rupee','1.000','PKR');
INSERT INTO currency VALUES (119,'Zloty','1.000','PLN');
INSERT INTO currency VALUES (120,'Guarani','1.000','PYG');
INSERT INTO currency VALUES (121,'Qatari rial','1.000','QAR');
INSERT INTO currency VALUES (122,'Romanian new leu','1.000','RON');
INSERT INTO currency VALUES (123,'Serbian dinar','1.000','RSD');
INSERT INTO currency VALUES (124,'Russian rouble','1.000','RUB');
INSERT INTO currency VALUES (125,'Rwanda franc','1.000','RWF');
INSERT INTO currency VALUES (126,'Saudi riyal','1.000','SAR');
INSERT INTO currency VALUES (127,'Solomon Islands dollar','1.000','SBD');
INSERT INTO currency VALUES (128,'Seychelles rupee','1.000','SCR');
INSERT INTO currency VALUES (129,'Sudanese pound','1.000','SDG');
INSERT INTO currency VALUES (130,'Swedish krona','1.000','SEK');
INSERT INTO currency VALUES (131,'Singapore dollar','1.000','SGD');
INSERT INTO currency VALUES (132,'Saint Helena pound','1.000','SHP');
INSERT INTO currency VALUES (133,'Slovak koruna','1.000','SKK');
INSERT INTO currency VALUES (134,'Leone','1.000','SLL');
INSERT INTO currency VALUES (135,'Somali shilling','1.000','SOS');
INSERT INTO currency VALUES (136,'Surinam dollar','1.000','SRD');
INSERT INTO currency VALUES (137,'Dobra','1.000','STD');
INSERT INTO currency VALUES (138,'Syrian pound','1.000','SYP');
INSERT INTO currency VALUES (139,'Lilangeni','1.000','SZL');
INSERT INTO currency VALUES (140,'Baht','1.000','THB');
INSERT INTO currency VALUES (141,'Somoni','1.000','TJS');
INSERT INTO currency VALUES (142,'Manat','1.000','TMM');
INSERT INTO currency VALUES (143,'Tunisian dinar','1.000','TND');
INSERT INTO currency VALUES (144,'Pa\'anga','1.000','TOP');
INSERT INTO currency VALUES (145,'New Turkish lira','1.000','TRY');
INSERT INTO currency VALUES (146,'Trinidad and Tobago dollar','1.000','TTD');
INSERT INTO currency VALUES (147,'New Taiwan dollar','1.000','TWD');
INSERT INTO currency VALUES (148,'Tanzanian shilling','1.000','TZS');
INSERT INTO currency VALUES (149,'Hryvnia','1.000','UAH');
INSERT INTO currency VALUES (150,'Uganda shilling','1.000','UGX');
INSERT INTO currency VALUES (151,'US dollar (next day)','1.000','USN');
INSERT INTO currency VALUES (152,'US dollar (same day)','1.000','USS');
INSERT INTO currency VALUES (153,'Peso Uruguayo','1.000','UYU');
INSERT INTO currency VALUES (154,'Uzbekistan som','1.000','UZS');
INSERT INTO currency VALUES (155,'Venezuelan Bolivares Fuertes','1.000','VEF');
INSERT INTO currency VALUES (156,'Vietnamese Dong','1.000','VND');
INSERT INTO currency VALUES (157,'Vatu','1.000','VUV');
INSERT INTO currency VALUES (158,'Samoan tala','1.000','WST');
INSERT INTO currency VALUES (159,'CFA franc BEAC','1.000','XAF');
INSERT INTO currency VALUES (160,'Silver','1.000','XAG');
INSERT INTO currency VALUES (161,'Gold','1.000','XAU');
INSERT INTO currency VALUES (162,'European Composite Unit','1.000','XBA');
INSERT INTO currency VALUES (163,'European Monetary Unit','1.000','XBB');
INSERT INTO currency VALUES (164,'European Unit of Account 9','1.000','XBC');
INSERT INTO currency VALUES (165,'European Unit of Account 17','1.000','XBD');
INSERT INTO currency VALUES (166,'East Caribbean dollar','1.000','XCD');
INSERT INTO currency VALUES (167,'Special Drawing Rights','1.000','XDR');
INSERT INTO currency VALUES (168,'UIC franc','1.000','XFU');
INSERT INTO currency VALUES (169,'CFA Franc BCEAO','1.000','XOF');
INSERT INTO currency VALUES (170,'Palladium','1.000','XPD');
INSERT INTO currency VALUES (171,'CFP franc','1.000','XPF');
INSERT INTO currency VALUES (172,'Platinum','1.000','XPT');
INSERT INTO currency VALUES (173,'Code reserved for testing purposes','1.000','XTS');
INSERT INTO currency VALUES (174,'No currency','1.000','XXX');
INSERT INTO currency VALUES (175,'Yemeni rial','1.000','YER');
INSERT INTO currency VALUES (176,'South African rand','1.000','ZAR');
INSERT INTO currency VALUES (177,'Kwacha','1.000','ZMK');
INSERT INTO currency VALUES (178,'Zimbabwe dollar','1.000','ZWD');
/*!40000 ALTER TABLE currency ENABLE KEYS */;
DROP TABLE IF EXISTS cust_perf_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE cust_perf_history (
  customer_id int(11) NOT NULL,
  loan_cycle_counter smallint(6) DEFAULT NULL,
  last_loan_amnt decimal(21,4) DEFAULT NULL,
  active_loans_count smallint(6) DEFAULT NULL,
  total_savings_amnt decimal(21,4) DEFAULT NULL,
  delinquint_portfolio decimal(21,4) DEFAULT NULL,
  PRIMARY KEY (customer_id),
  CONSTRAINT cust_perf_history_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE cust_perf_history DISABLE KEYS */;
/*!40000 ALTER TABLE cust_perf_history ENABLE KEYS */;
DROP TABLE IF EXISTS custom_field_definition;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE custom_field_definition (
  field_id smallint(6) NOT NULL AUTO_INCREMENT,
  entity_id smallint(6) NOT NULL,
  level_id smallint(6) DEFAULT NULL,
  field_type smallint(6) DEFAULT NULL,
  entity_type smallint(6) NOT NULL,
  mandatory_flag smallint(6) NOT NULL,
  default_value varchar(200) DEFAULT NULL,
  PRIMARY KEY (field_id),
  KEY level_id (level_id),
  KEY entity_id (entity_id),
  KEY entity_type (entity_type),
  CONSTRAINT custom_field_definition_ibfk_1 FOREIGN KEY (level_id) REFERENCES customer_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT custom_field_definition_ibfk_2 FOREIGN KEY (entity_id) REFERENCES lookup_entity (entity_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT custom_field_definition_ibfk_3 FOREIGN KEY (entity_type) REFERENCES entity_master (entity_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE custom_field_definition DISABLE KEYS */;
/*!40000 ALTER TABLE custom_field_definition ENABLE KEYS */;
DROP TABLE IF EXISTS customer;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer (
  customer_id int(11) NOT NULL AUTO_INCREMENT,
  customer_level_id smallint(6) NOT NULL,
  global_cust_num varchar(100) DEFAULT NULL,
  loan_officer_id smallint(6) DEFAULT NULL,
  customer_formedby_id smallint(6) DEFAULT NULL,
  status_id smallint(6) DEFAULT NULL,
  branch_id smallint(6) DEFAULT NULL,
  display_name varchar(200) DEFAULT NULL,
  first_name varchar(200) DEFAULT NULL,
  last_name varchar(200) DEFAULT NULL,
  second_last_name varchar(200) DEFAULT NULL,
  display_address varchar(500) DEFAULT NULL,
  external_id varchar(50) DEFAULT NULL,
  date_of_birth date DEFAULT NULL,
  group_flag smallint(6) DEFAULT NULL,
  trained smallint(6) DEFAULT NULL,
  trained_date date DEFAULT NULL,
  parent_customer_id int(11) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_date date DEFAULT NULL,
  search_id varchar(100) DEFAULT NULL,
  max_child_count int(11) DEFAULT NULL,
  ho_updated smallint(6) DEFAULT NULL,
  client_confidential smallint(6) DEFAULT NULL,
  mfi_joining_date date DEFAULT NULL,
  government_id varchar(50) DEFAULT NULL,
  customer_activation_date date DEFAULT NULL,
  created_by smallint(6) DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  blacklisted smallint(6) DEFAULT NULL,
  discriminator varchar(20) DEFAULT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (customer_id),
  UNIQUE KEY cust_global_idx (global_cust_num),
  KEY status_id (status_id),
  KEY customer_formedby_id (customer_formedby_id),
  KEY parent_customer_id (parent_customer_id),
  KEY cust_search_idx (search_id),
  KEY cust_government_idx (government_id),
  KEY cust_lo_idx (loan_officer_id,branch_id),
  KEY customer_lo_name_idx (loan_officer_id,customer_level_id,display_name(15),first_name(15),last_name(15),second_last_name(15)),
  KEY customer_name_idx (customer_level_id,first_name(15),last_name(15),second_last_name(15)),
  KEY customer_branch_search_idx (branch_id,search_id),
  KEY customer_dob_status_idx (date_of_birth,status_id),
  CONSTRAINT customer_ibfk_1 FOREIGN KEY (customer_level_id) REFERENCES customer_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_ibfk_2 FOREIGN KEY (status_id) REFERENCES customer_state (status_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_ibfk_3 FOREIGN KEY (branch_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_ibfk_4 FOREIGN KEY (loan_officer_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_ibfk_5 FOREIGN KEY (customer_formedby_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_ibfk_6 FOREIGN KEY (parent_customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer DISABLE KEYS */;
INSERT INTO customer VALUES (1,3,'0002-000000001',2,NULL,13,2,'Default Center',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-18',NULL,'1.1',4,NULL,NULL,'2011-02-18',NULL,'2011-02-18',1,NULL,0,'CENTER',10);
INSERT INTO customer VALUES (2,1,'0002-000000002',2,2,3,2,'Client - Mary Monthly','Client - Mary','Monthly','',NULL,'','1970-01-01',0,0,NULL,NULL,'2001-02-21','2001-02-21','1.2',0,NULL,NULL,'2001-02-21','','2001-02-21',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (3,1,'0002-000000003',2,2,3,2,'Stu1233266063395 Client1233266063395','Stu1233266063395','Client1233266063395','',NULL,'','2000-01-01',0,0,NULL,NULL,'2011-02-21','2011-02-21','1.3',0,NULL,NULL,'2011-02-21','','2011-02-21',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (4,2,'0002-000000004',2,2,9,2,'group1',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2010-01-22','2010-01-22','1.1.1',1,NULL,NULL,'2010-01-22',NULL,'2010-01-22',1,1,0,'GROUP',5);
INSERT INTO customer VALUES (5,1,'0002-000000005',2,2,3,2,'client1 lastname','client1','lastname','',NULL,'','1990-01-01',1,0,NULL,4,'2010-01-22','2010-01-22','1.1.1.1',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (6,1,'0002-000000006',2,2,3,2,'Stu1233171716380 Client1233171716380','Stu1233171716380','Client1233171716380','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-01-22','2010-01-22','1.4',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (7,1,'0002-000000007',2,2,3,2,'Client - Mary Monthly1','Client - Mary','Monthly1','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-01-22','2010-01-22','1.5',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (8,1,'0002-000000008',2,2,3,2,'Client - Mia Monthly3rdFriday','Client - Mia','Monthly3rdFriday','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-01-22','2010-01-22','1.6',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (9,2,'0002-000000009',2,2,9,2,'groupWithoutLoan',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2011-02-18','2011-02-18','1.1.2',1,NULL,NULL,'2011-02-18',NULL,'2011-02-18',1,1,0,'GROUP',5);
INSERT INTO customer VALUES (10,1,'0002-000000010',2,2,3,2,'ClientWithLoan 20110221','ClientWithLoan','20110221','',NULL,'','1985-04-04',1,0,NULL,9,'2011-02-18','2011-02-18','1.1.2.1',0,NULL,NULL,'2011-02-18','','2011-02-18',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (11,2,'0002-000000011',2,2,9,2,'Default Group',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2011-02-22','2011-02-22','1.1.3',4,NULL,NULL,'2011-02-22',NULL,'2011-02-22',1,1,0,'GROUP',8);
INSERT INTO customer VALUES (12,1,'0002-000000012',2,2,3,2,'Stu1233266299995 Client1233266299995','Stu1233266299995','Client1233266299995','',NULL,'','1990-01-01',1,0,NULL,11,'2011-02-22','2011-02-22','1.1.3.1',0,NULL,NULL,'2011-02-22','','2011-02-22',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (13,1,'0002-000000013',2,2,3,2,'Stu1233266309851 Client1233266309851','Stu1233266309851','Client1233266309851','',NULL,'','1990-01-01',1,0,NULL,11,'2011-02-22','2011-02-22','1.1.3.2',0,NULL,NULL,'2011-02-22','','2011-02-22',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (14,1,'0002-000000014',2,2,3,2,'Stu1233266319760 Client1233266319760','Stu1233266319760','Client1233266319760','',NULL,'','1990-01-01',1,0,NULL,11,'2011-02-22','2011-02-22','1.1.3.3',0,NULL,NULL,'2011-02-22','','2011-02-22',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (15,3,'0002-000000015',2,NULL,13,2,'WeeklyMeetingCenter',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-24',NULL,'1.7',1,NULL,NULL,'2011-02-24',NULL,'2011-02-24',1,NULL,0,'CENTER',4);
INSERT INTO customer VALUES (16,2,'0002-000000016',2,2,9,2,'UpdateCustomPropertiesTestGroup',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2011-02-25','2011-02-25','1.1.4',1,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,1,0,'GROUP',5);
INSERT INTO customer VALUES (17,1,'0002-000000017',2,2,3,2,'UpdateCustomProperties TestClient','UpdateCustomProperties','TestClient','',NULL,'','1984-09-02',1,0,NULL,16,'2011-02-25','2011-02-25','1.1.4.1',0,NULL,NULL,'2011-02-25','','2011-02-25',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (18,1,'0002-000000018',2,2,3,2,'Client WeeklyTue','Client','WeeklyTue','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-10-11','2010-10-11','1.8',0,NULL,NULL,'2010-10-11','','2010-10-11',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (19,3,'0002-000000019',2,NULL,13,2,'CenterForMeetsOn3rdFriday',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-25',NULL,'1.9',1,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,NULL,0,'CENTER',4);
INSERT INTO customer VALUES (20,2,'0002-000000020',2,2,9,2,'GroupMeetsOn3rdFriday',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,19,'2011-02-25','2011-02-25','1.9.1',0,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,1,0,'GROUP',3);
INSERT INTO customer VALUES (21,3,'0002-000000021',2,NULL,13,2,'MonthlyMeetingCenter',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-25',NULL,'1.10',1,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,NULL,0,'CENTER',4);
INSERT INTO customer VALUES (22,2,'0002-000000022',2,2,9,2,'MonthlyGroup',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,21,'2011-02-25','2011-02-25','1.10.1',0,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,1,0,'GROUP',3);
INSERT INTO customer VALUES (23,1,'0002-000000023',2,2,3,2,'Holiday TestClient','Holiday','TestClient','',NULL,'','1984-09-02',1,0,NULL,11,'2020-01-01','2020-01-01','1.1.3.4',0,NULL,NULL,'2020-01-01','','2020-01-01',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (24,1,'0002-000000024',2,2,3,2,'WeeklyClient Monday','WeeklyClient','Monday','',NULL,'','1990-01-01',0,0,NULL,NULL,'2011-02-28','2011-02-28','1.11',0,NULL,NULL,'2011-02-28','','2011-02-28',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (25,2,'0002-000000025',2,2,9,2,'GroupWeekly',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,15,'2011-01-01','2011-03-03','1.7.1',1,NULL,NULL,'2011-01-01',NULL,'2011-01-01',1,1,0,'GROUP',5);
INSERT INTO customer VALUES (26,1,'0002-000000026',2,2,3,2,'MemberWeekly Group','MemberWeekly','Group','',NULL,'','1980-01-01',1,0,NULL,25,'2011-01-01','2011-03-03','1.7.1.1',0,NULL,NULL,'2011-01-01','','2011-01-01',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (27,3,'0003-000000027',3,NULL,13,3,'branch1 center',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-03-03',NULL,'1.1',1,NULL,NULL,'2011-03-03',NULL,'2011-03-03',1,NULL,0,'CENTER',4);
INSERT INTO customer VALUES (28,1,'0002-000000028',2,2,3,2,'WeeklyOld Monday','WeeklyOld','Monday','',NULL,'','2010-01-01',0,0,NULL,NULL,'2010-10-11','2010-10-11','1.12',0,NULL,NULL,'2010-10-11','','2010-10-11',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (29,1,'0002-000000029',2,2,3,2,'WeeklyOld Tuesday','WeeklyOld','Tuesday','',NULL,'','1999-01-01',0,0,NULL,NULL,'2010-10-11','2010-10-11','1.13',0,NULL,NULL,'2010-10-11','','2010-10-11',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (30,1,'0002-000000030',2,2,3,2,'Stu12332659912419 Client12332659912419','Stu12332659912419','Client12332659912419','',NULL,'','1980-01-01',0,0,NULL,NULL,'2011-01-01','2011-03-08','1.14',0,NULL,NULL,'2011-01-01','','2011-01-01',1,1,0,'CLIENT',3);
INSERT INTO customer VALUES (31,1,'0002-000000031',2,2,2,2,'WeeklyClient Wednesday','WeeklyClient','Wednesday','',NULL,'','1990-01-01',0,0,NULL,NULL,'2011-03-09',NULL,'1.15',0,NULL,NULL,'2011-03-09','',NULL,1,NULL,0,'CLIENT',2);
INSERT INTO customer VALUES (32,2,'0003-000000032',3,3,9,3,'GroupInBranch1',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,27,'2011-03-10','2011-03-10','1.1.1',1,NULL,NULL,'2011-03-10',NULL,'2011-03-10',1,1,0,'GROUP',5);
INSERT INTO customer VALUES (33,1,'0003-000000033',3,3,3,3,'ClientInBranch1 ClientInBranch1','ClientInBranch1','ClientInBranch1','',NULL,'','1975-01-01',1,0,NULL,32,'2011-03-10','2011-03-10','1.1.1.1',0,NULL,NULL,'2011-03-10','','2011-03-10',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (34,3,'0004-000000034',4,NULL,13,4,'branch2 center',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-03-14',NULL,'1.1',0,NULL,NULL,'2011-03-14',NULL,'2011-03-14',1,NULL,0,'CENTER',2);
INSERT INTO customer VALUES (35,3,'0002-000000035',2,NULL,13,2,'SavingsAccountPerformanceHistoryTestCenter',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-03-14',NULL,'1.16',1,NULL,NULL,'2011-03-14',NULL,'2011-03-14',1,NULL,0,'CENTER',4);
INSERT INTO customer VALUES (36,2,'0002-000000036',2,2,9,2,'SavingsAccountPerformanceHistoryTestGroup',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,35,'2011-03-14','2011-03-14','1.16.1',1,NULL,NULL,'2011-03-14',NULL,'2011-03-14',1,1,0,'GROUP',5);
INSERT INTO customer VALUES (37,1,'0002-000000037',2,2,3,2,'SavingsAccountPerformanceHistoryTestClient SavingsAccountPerformanceHistoryTestClient','SavingsAccountPerformanceHistoryTestClient','SavingsAccountPerformanceHistoryTestClient','',NULL,'','1942-12-24',1,0,NULL,36,'2011-03-14','2011-03-14','1.16.1.1',0,NULL,NULL,'2011-03-14','','2011-03-14',1,1,0,'CLIENT',4);
INSERT INTO customer VALUES (38,3,'0002-000000038',2,NULL,13,2,'DefineNewSavingsProductTestCenter',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-03-15',NULL,'1.17',1,NULL,NULL,'2011-03-15',NULL,'2011-03-15',1,NULL,0,'CENTER',4);
INSERT INTO customer VALUES (39,2,'0002-000000039',2,2,9,2,'DefineNewSavingsProductTestGroup',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,38,'2011-03-15','2011-03-15','1.17.1',1,NULL,NULL,'2011-03-15',NULL,'2011-03-15',1,1,0,'GROUP',5);
INSERT INTO customer VALUES (40,1,'0002-000000040',2,2,3,2,'DefineNewSavingsProduct TestClient','DefineNewSavingsProduct','TestClient','',NULL,'','1986-06-05',1,0,NULL,39,'2011-03-15','2011-03-15','1.17.1.1',0,NULL,NULL,'2011-03-15','','2011-03-15',1,1,0,'CLIENT',4);
/*!40000 ALTER TABLE customer ENABLE KEYS */;
DROP TABLE IF EXISTS customer_account;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_account (
  account_id int(11) NOT NULL,
  PRIMARY KEY (account_id),
  CONSTRAINT customer_account_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_account DISABLE KEYS */;
INSERT INTO customer_account VALUES (1);
INSERT INTO customer_account VALUES (3);
INSERT INTO customer_account VALUES (4);
INSERT INTO customer_account VALUES (5);
INSERT INTO customer_account VALUES (6);
INSERT INTO customer_account VALUES (7);
INSERT INTO customer_account VALUES (8);
INSERT INTO customer_account VALUES (9);
INSERT INTO customer_account VALUES (13);
INSERT INTO customer_account VALUES (14);
INSERT INTO customer_account VALUES (16);
INSERT INTO customer_account VALUES (17);
INSERT INTO customer_account VALUES (18);
INSERT INTO customer_account VALUES (19);
INSERT INTO customer_account VALUES (21);
INSERT INTO customer_account VALUES (22);
INSERT INTO customer_account VALUES (23);
INSERT INTO customer_account VALUES (24);
INSERT INTO customer_account VALUES (30);
INSERT INTO customer_account VALUES (31);
INSERT INTO customer_account VALUES (32);
INSERT INTO customer_account VALUES (33);
INSERT INTO customer_account VALUES (34);
INSERT INTO customer_account VALUES (36);
INSERT INTO customer_account VALUES (48);
INSERT INTO customer_account VALUES (49);
INSERT INTO customer_account VALUES (51);
INSERT INTO customer_account VALUES (56);
INSERT INTO customer_account VALUES (57);
INSERT INTO customer_account VALUES (58);
INSERT INTO customer_account VALUES (60);
INSERT INTO customer_account VALUES (61);
INSERT INTO customer_account VALUES (62);
INSERT INTO customer_account VALUES (63);
INSERT INTO customer_account VALUES (64);
INSERT INTO customer_account VALUES (65);
INSERT INTO customer_account VALUES (66);
INSERT INTO customer_account VALUES (68);
INSERT INTO customer_account VALUES (69);
INSERT INTO customer_account VALUES (70);
/*!40000 ALTER TABLE customer_account ENABLE KEYS */;
DROP TABLE IF EXISTS customer_account_activity;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_account_activity (
  customer_account_activity_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  description varchar(200) NOT NULL,
  amount decimal(21,4) DEFAULT NULL,
  fee_amount_currency_id smallint(6) DEFAULT NULL,
  created_date date NOT NULL,
  created_by smallint(6) DEFAULT NULL,
  PRIMARY KEY (customer_account_activity_id),
  KEY account_id (account_id),
  KEY fee_amount_currency_id (fee_amount_currency_id),
  KEY created_by (created_by),
  CONSTRAINT customer_account_activity_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_account_activity_ibfk_2 FOREIGN KEY (fee_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_account_activity_ibfk_3 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_account_activity DISABLE KEYS */;
/*!40000 ALTER TABLE customer_account_activity ENABLE KEYS */;
DROP TABLE IF EXISTS customer_address_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_address_detail (
  customer_address_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) DEFAULT NULL,
  locale_id smallint(6) DEFAULT NULL,
  address_name varchar(100) DEFAULT NULL,
  line_1 varchar(200) DEFAULT NULL,
  line_2 varchar(200) DEFAULT NULL,
  line_3 varchar(200) DEFAULT NULL,
  city varchar(100) DEFAULT NULL,
  state varchar(100) DEFAULT NULL,
  country varchar(100) DEFAULT NULL,
  zip varchar(20) DEFAULT NULL,
  address_status smallint(6) DEFAULT NULL,
  phone_number varchar(20) DEFAULT NULL,
  phone_number_stripped varchar(20) DEFAULT NULL,
  PRIMARY KEY (customer_address_id),
  KEY locale_id (locale_id),
  KEY cust_address_idx (customer_id),
  KEY customer_address_detail_phone_number_stripped_idx (phone_number_stripped),
  CONSTRAINT customer_address_detail_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_address_detail_ibfk_2 FOREIGN KEY (locale_id) REFERENCES supported_locale (locale_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_address_detail DISABLE KEYS */;
INSERT INTO customer_address_detail VALUES (1,1,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (2,2,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (3,3,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (4,4,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (5,5,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (6,6,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (7,7,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (8,8,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (9,9,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (10,10,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (11,11,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (12,12,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (13,13,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (14,14,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (15,15,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (16,16,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (17,17,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (18,18,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (19,19,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (20,20,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (21,21,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (22,22,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (23,23,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (24,24,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (25,25,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (26,26,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (27,27,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (28,28,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (29,29,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (30,30,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (31,31,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (32,32,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (33,33,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (34,34,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (35,35,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (36,36,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (37,37,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (38,38,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (39,39,NULL,NULL,'','','','','','','',NULL,'','');
INSERT INTO customer_address_detail VALUES (40,40,NULL,NULL,'','','','','','','',NULL,'','');
/*!40000 ALTER TABLE customer_address_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customer_attendance;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_attendance (
  id int(11) NOT NULL AUTO_INCREMENT,
  meeting_date date NOT NULL,
  customer_id int(11) NOT NULL,
  attendance smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY customer_id (customer_id),
  KEY customer_attendance_meeting_date_idx (meeting_date,customer_id),
  CONSTRAINT customer_attendance_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_attendance DISABLE KEYS */;
/*!40000 ALTER TABLE customer_attendance ENABLE KEYS */;
DROP TABLE IF EXISTS customer_attendance_types;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_attendance_types (
  attendance_id smallint(6) NOT NULL AUTO_INCREMENT,
  attendance_lookup_id int(11) NOT NULL,
  description varchar(50) DEFAULT NULL,
  PRIMARY KEY (attendance_id),
  KEY attendance_lookup_id (attendance_lookup_id),
  CONSTRAINT customer_attendance_types_ibfk_1 FOREIGN KEY (attendance_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_attendance_types DISABLE KEYS */;
INSERT INTO customer_attendance_types VALUES (1,194,'Present');
INSERT INTO customer_attendance_types VALUES (2,195,'Absent');
INSERT INTO customer_attendance_types VALUES (3,196,'Approved leave');
INSERT INTO customer_attendance_types VALUES (4,197,'Late');
/*!40000 ALTER TABLE customer_attendance_types ENABLE KEYS */;
DROP TABLE IF EXISTS customer_checklist;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_checklist (
  checklist_id smallint(6) NOT NULL,
  level_id smallint(6) NOT NULL,
  customer_status_id smallint(6) NOT NULL,
  PRIMARY KEY (checklist_id),
  KEY level_id (level_id),
  KEY customer_status_id (customer_status_id),
  CONSTRAINT customer_checklist_ibfk_1 FOREIGN KEY (checklist_id) REFERENCES checklist (checklist_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_checklist_ibfk_2 FOREIGN KEY (level_id) REFERENCES customer_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_checklist_ibfk_3 FOREIGN KEY (customer_status_id) REFERENCES customer_state (status_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_checklist DISABLE KEYS */;
/*!40000 ALTER TABLE customer_checklist ENABLE KEYS */;
DROP TABLE IF EXISTS customer_custom_field;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_custom_field (
  customer_customfield_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  field_id smallint(6) NOT NULL,
  field_value varchar(200) DEFAULT NULL,
  PRIMARY KEY (customer_customfield_id),
  KEY field_id (field_id),
  KEY customer_id (customer_id),
  CONSTRAINT customer_custom_field_ibfk_1 FOREIGN KEY (field_id) REFERENCES custom_field_definition (field_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_custom_field_ibfk_2 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_custom_field DISABLE KEYS */;
/*!40000 ALTER TABLE customer_custom_field ENABLE KEYS */;
DROP TABLE IF EXISTS customer_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_detail (
  customer_id int(11) NOT NULL,
  ethinicity int(11) DEFAULT NULL,
  citizenship int(11) DEFAULT NULL,
  handicapped int(11) DEFAULT NULL,
  business_activities int(11) DEFAULT NULL,
  marital_status int(11) DEFAULT NULL,
  education_level int(11) DEFAULT NULL,
  num_children smallint(6) DEFAULT NULL,
  gender smallint(6) DEFAULT NULL,
  date_started date DEFAULT NULL,
  handicapped_details varchar(200) DEFAULT NULL,
  poverty_status int(11) DEFAULT NULL,
  poverty_lhood_pct decimal(21,4) DEFAULT NULL,
  PRIMARY KEY (customer_id),
  KEY citizenship (citizenship),
  KEY education_level (education_level),
  KEY ethinicity (ethinicity),
  KEY handicapped (handicapped),
  KEY marital_status (marital_status),
  KEY poverty_status (poverty_status),
  CONSTRAINT customer_detail_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_detail_ibfk_2 FOREIGN KEY (citizenship) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_detail_ibfk_3 FOREIGN KEY (education_level) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_detail_ibfk_4 FOREIGN KEY (ethinicity) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_detail_ibfk_5 FOREIGN KEY (handicapped) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_detail_ibfk_6 FOREIGN KEY (marital_status) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_detail_ibfk_7 FOREIGN KEY (poverty_status) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_detail DISABLE KEYS */;
INSERT INTO customer_detail VALUES (2,NULL,NULL,NULL,NULL,66,NULL,NULL,50,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (3,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (5,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (6,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (7,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (8,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (13,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (14,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (17,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,43,NULL);
INSERT INTO customer_detail VALUES (18,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (23,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,42,NULL);
INSERT INTO customer_detail VALUES (24,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (26,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (28,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (29,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (30,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (31,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
INSERT INTO customer_detail VALUES (33,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,42,NULL);
INSERT INTO customer_detail VALUES (37,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,42,NULL);
INSERT INTO customer_detail VALUES (40,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL);
/*!40000 ALTER TABLE customer_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customer_family_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_family_detail (
  customer_family_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) DEFAULT NULL,
  customer_name_id int(11) DEFAULT NULL,
  relationship smallint(6) DEFAULT NULL,
  gender smallint(6) DEFAULT NULL,
  date_of_birth date DEFAULT NULL,
  living_status smallint(6) DEFAULT NULL,
  PRIMARY KEY (customer_family_id),
  KEY customer_id (customer_id),
  KEY customer_name_id (customer_name_id),
  CONSTRAINT customer_family_detail_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_family_detail_ibfk_2 FOREIGN KEY (customer_name_id) REFERENCES customer_name_detail (customer_name_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_family_detail DISABLE KEYS */;
/*!40000 ALTER TABLE customer_family_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customer_fee_schedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_fee_schedule (
  account_fees_detail_id int(11) NOT NULL AUTO_INCREMENT,
  id int(11) NOT NULL,
  installment_id int(11) NOT NULL,
  fee_id smallint(6) NOT NULL,
  account_fee_id int(11) NOT NULL,
  amount decimal(21,4) DEFAULT NULL,
  amount_currency_id smallint(6) DEFAULT NULL,
  amount_paid decimal(21,4) DEFAULT NULL,
  amount_paid_currency_id smallint(6) DEFAULT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (account_fees_detail_id),
  KEY id (id),
  KEY amount_currency_id (amount_currency_id),
  KEY amount_paid_currency_id (amount_paid_currency_id),
  KEY fee_id (fee_id),
  KEY account_fee_id (account_fee_id),
  CONSTRAINT customer_fee_schedule_ibfk_1 FOREIGN KEY (id) REFERENCES customer_schedule (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_fee_schedule_ibfk_2 FOREIGN KEY (amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_fee_schedule_ibfk_3 FOREIGN KEY (amount_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_fee_schedule_ibfk_4 FOREIGN KEY (fee_id) REFERENCES fees (fee_id),
  CONSTRAINT customer_fee_schedule_ibfk_5 FOREIGN KEY (account_fee_id) REFERENCES account_fees (account_fee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_fee_schedule DISABLE KEYS */;
/*!40000 ALTER TABLE customer_fee_schedule ENABLE KEYS */;
DROP TABLE IF EXISTS customer_flag_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_flag_detail (
  customer_flag_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  flag_id smallint(6) NOT NULL,
  created_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (customer_flag_id),
  KEY customer_id (customer_id),
  KEY flag_id (flag_id),
  KEY created_by (created_by),
  CONSTRAINT customer_flag_detail_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_flag_detail_ibfk_2 FOREIGN KEY (flag_id) REFERENCES customer_state_flag (flag_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_flag_detail_ibfk_3 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_flag_detail DISABLE KEYS */;
/*!40000 ALTER TABLE customer_flag_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customer_hierarchy;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_hierarchy (
  hierarchy_id int(11) NOT NULL AUTO_INCREMENT,
  parent_id int(11) NOT NULL,
  customer_id int(11) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  PRIMARY KEY (hierarchy_id),
  KEY parent_id (parent_id),
  KEY updated_by (updated_by),
  KEY cust_hierarchy_idx (customer_id,`status`),
  CONSTRAINT customer_hierarchy_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_hierarchy_ibfk_2 FOREIGN KEY (parent_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_hierarchy_ibfk_3 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_hierarchy DISABLE KEYS */;
INSERT INTO customer_hierarchy VALUES (1,1,4,1,'2010-01-22',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (2,4,5,1,'2010-01-22',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (3,1,9,1,'2011-02-18',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (4,9,10,1,'2011-02-18',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (5,1,11,1,'2011-02-22',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (6,11,12,1,'2011-02-22',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (7,11,13,1,'2011-02-22',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (8,11,14,1,'2011-02-22',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (9,1,16,1,'2011-02-25',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (10,16,17,1,'2011-02-25',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (11,19,20,1,'2011-02-25',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (12,21,22,1,'2011-02-25',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (13,11,23,1,'2020-01-01',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (14,15,25,1,'2011-01-01',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (15,25,26,1,'2011-01-01',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (16,27,32,1,'2011-03-10',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (17,32,33,1,'2011-03-10',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (18,35,36,1,'2011-03-14',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (19,36,37,1,'2011-03-14',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (20,38,39,1,'2011-03-15',NULL,NULL,NULL);
INSERT INTO customer_hierarchy VALUES (21,39,40,1,'2011-03-15',NULL,NULL,NULL);
/*!40000 ALTER TABLE customer_hierarchy ENABLE KEYS */;
DROP TABLE IF EXISTS customer_historical_data;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_historical_data (
  historical_id smallint(6) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  product_name varchar(100) DEFAULT NULL,
  loan_amount decimal(21,4) DEFAULT NULL,
  loan_amount_currency_id smallint(6) DEFAULT NULL,
  total_amount_paid decimal(21,4) DEFAULT NULL,
  total_amount_paid_currency_id smallint(6) DEFAULT NULL,
  interest_paid decimal(21,4) DEFAULT NULL,
  interest_paid_currency_id smallint(6) DEFAULT NULL,
  missed_payments_count int(11) DEFAULT NULL,
  total_payments_count int(11) DEFAULT NULL,
  notes varchar(500) DEFAULT NULL,
  loan_cycle_number int(11) DEFAULT NULL,
  created_by smallint(6) DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_date date DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (historical_id),
  KEY customer_id (customer_id),
  CONSTRAINT customer_historical_data_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_historical_data DISABLE KEYS */;
/*!40000 ALTER TABLE customer_historical_data ENABLE KEYS */;
DROP TABLE IF EXISTS customer_level;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_level (
  level_id smallint(6) NOT NULL,
  parent_level_id smallint(6) DEFAULT NULL,
  level_name_id smallint(6) NOT NULL,
  interaction_flag smallint(6) DEFAULT NULL,
  max_child_count smallint(6) NOT NULL,
  max_instance_count smallint(6) NOT NULL,
  PRIMARY KEY (level_id),
  KEY parent_level_id (parent_level_id),
  KEY level_name_id (level_name_id),
  CONSTRAINT customer_level_ibfk_1 FOREIGN KEY (parent_level_id) REFERENCES customer_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_level_ibfk_2 FOREIGN KEY (level_name_id) REFERENCES lookup_entity (entity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_level DISABLE KEYS */;
INSERT INTO customer_level VALUES (1,2,13,NULL,1,30);
INSERT INTO customer_level VALUES (2,3,12,NULL,2,12);
INSERT INTO customer_level VALUES (3,NULL,11,NULL,4,10);
/*!40000 ALTER TABLE customer_level ENABLE KEYS */;
DROP TABLE IF EXISTS customer_loan_account_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_loan_account_detail (
  account_trxn_id int(11) NOT NULL,
  account_id int(11) NOT NULL,
  currency_id smallint(6) DEFAULT NULL,
  installment_number smallint(6) NOT NULL,
  due_date date NOT NULL,
  principal decimal(21,4) NOT NULL,
  principal_currency_id smallint(6) NOT NULL,
  interest decimal(21,4) NOT NULL,
  interest_currency_id smallint(6) NOT NULL,
  penalty decimal(21,4) NOT NULL,
  penalty_currency_id smallint(6) NOT NULL,
  KEY account_trxn_id (account_trxn_id),
  KEY currency_id (currency_id),
  KEY principal_currency_id (principal_currency_id),
  KEY interest_currency_id (interest_currency_id),
  KEY penalty_currency_id (penalty_currency_id),
  KEY account_id (account_id),
  CONSTRAINT customer_loan_account_detail_ibfk_1 FOREIGN KEY (account_trxn_id) REFERENCES account_trxn (account_trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_loan_account_detail_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_loan_account_detail_ibfk_3 FOREIGN KEY (principal_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_loan_account_detail_ibfk_4 FOREIGN KEY (interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_loan_account_detail_ibfk_5 FOREIGN KEY (penalty_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_loan_account_detail_ibfk_6 FOREIGN KEY (account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_loan_account_detail DISABLE KEYS */;
/*!40000 ALTER TABLE customer_loan_account_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customer_meeting;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_meeting (
  customer_meeting_id int(11) NOT NULL AUTO_INCREMENT,
  meeting_id int(11) NOT NULL,
  customer_id int(11) NOT NULL,
  PRIMARY KEY (customer_meeting_id),
  KEY meeting_id (meeting_id),
  KEY customer_meeting_idx (customer_id),
  CONSTRAINT customer_meeting_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_meeting_ibfk_2 FOREIGN KEY (meeting_id) REFERENCES meeting (meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_meeting DISABLE KEYS */;
INSERT INTO customer_meeting VALUES (1,1,1);
INSERT INTO customer_meeting VALUES (2,4,2);
INSERT INTO customer_meeting VALUES (3,5,3);
INSERT INTO customer_meeting VALUES (4,1,4);
INSERT INTO customer_meeting VALUES (5,1,5);
INSERT INTO customer_meeting VALUES (6,7,6);
INSERT INTO customer_meeting VALUES (7,9,7);
INSERT INTO customer_meeting VALUES (8,12,8);
INSERT INTO customer_meeting VALUES (9,1,9);
INSERT INTO customer_meeting VALUES (10,1,10);
INSERT INTO customer_meeting VALUES (11,1,11);
INSERT INTO customer_meeting VALUES (12,1,12);
INSERT INTO customer_meeting VALUES (13,1,13);
INSERT INTO customer_meeting VALUES (14,1,14);
INSERT INTO customer_meeting VALUES (15,25,15);
INSERT INTO customer_meeting VALUES (16,1,16);
INSERT INTO customer_meeting VALUES (17,1,17);
INSERT INTO customer_meeting VALUES (18,30,18);
INSERT INTO customer_meeting VALUES (19,37,19);
INSERT INTO customer_meeting VALUES (20,37,20);
INSERT INTO customer_meeting VALUES (21,38,21);
INSERT INTO customer_meeting VALUES (22,38,22);
INSERT INTO customer_meeting VALUES (23,1,23);
INSERT INTO customer_meeting VALUES (24,42,24);
INSERT INTO customer_meeting VALUES (25,25,25);
INSERT INTO customer_meeting VALUES (26,25,26);
INSERT INTO customer_meeting VALUES (27,61,27);
INSERT INTO customer_meeting VALUES (28,66,28);
INSERT INTO customer_meeting VALUES (29,67,29);
INSERT INTO customer_meeting VALUES (30,68,30);
INSERT INTO customer_meeting VALUES (31,69,31);
INSERT INTO customer_meeting VALUES (32,61,32);
INSERT INTO customer_meeting VALUES (33,61,33);
INSERT INTO customer_meeting VALUES (34,72,34);
INSERT INTO customer_meeting VALUES (35,73,35);
INSERT INTO customer_meeting VALUES (36,73,36);
INSERT INTO customer_meeting VALUES (37,73,37);
INSERT INTO customer_meeting VALUES (38,76,38);
INSERT INTO customer_meeting VALUES (39,76,39);
INSERT INTO customer_meeting VALUES (40,76,40);
/*!40000 ALTER TABLE customer_meeting ENABLE KEYS */;
DROP TABLE IF EXISTS customer_meeting_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_meeting_detail (
  meeting_id int(11) NOT NULL,
  details_id int(11) NOT NULL,
  PRIMARY KEY (meeting_id,details_id),
  KEY details_id (details_id),
  CONSTRAINT customer_meeting_detail_ibfk_1 FOREIGN KEY (meeting_id) REFERENCES customer_meeting (customer_meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_meeting_detail_ibfk_2 FOREIGN KEY (details_id) REFERENCES recurrence_detail (details_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_meeting_detail DISABLE KEYS */;
/*!40000 ALTER TABLE customer_meeting_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customer_movement;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_movement (
  customer_movement_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) DEFAULT NULL,
  personnel_id smallint(6) DEFAULT NULL,
  office_id smallint(6) NOT NULL,
  `status` smallint(6) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  PRIMARY KEY (customer_movement_id),
  KEY office_id (office_id),
  KEY personnel_id (personnel_id),
  KEY updated_by (updated_by),
  KEY cust_movement_idx (customer_id,`status`),
  CONSTRAINT customer_movement_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_movement_ibfk_2 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_movement_ibfk_3 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_movement_ibfk_4 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_movement DISABLE KEYS */;
/*!40000 ALTER TABLE customer_movement ENABLE KEYS */;
DROP TABLE IF EXISTS customer_name_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_name_detail (
  customer_name_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) DEFAULT NULL,
  name_type smallint(6) DEFAULT NULL,
  locale_id smallint(6) DEFAULT NULL,
  salutation int(11) DEFAULT NULL,
  first_name varchar(100) NOT NULL,
  middle_name varchar(100) DEFAULT NULL,
  last_name varchar(100) NOT NULL,
  second_last_name varchar(100) DEFAULT NULL,
  second_middle_name varchar(100) DEFAULT NULL,
  display_name varchar(200) DEFAULT NULL,
  PRIMARY KEY (customer_name_id),
  KEY salutation (salutation),
  KEY locale_id (locale_id),
  KEY cust_name_idx (customer_id),
  CONSTRAINT customer_name_detail_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_name_detail_ibfk_2 FOREIGN KEY (salutation) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_name_detail_ibfk_3 FOREIGN KEY (locale_id) REFERENCES supported_locale (locale_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_name_detail DISABLE KEYS */;
INSERT INTO customer_name_detail VALUES (1,2,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395');
INSERT INTO customer_name_detail VALUES (2,2,3,NULL,228,'Client - Mary','','Monthly','',NULL,'Client - Mary Monthly');
INSERT INTO customer_name_detail VALUES (3,3,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395');
INSERT INTO customer_name_detail VALUES (4,3,3,NULL,47,'Stu1233266063395','','Client1233266063395','',NULL,'Stu1233266063395 Client1233266063395');
INSERT INTO customer_name_detail VALUES (5,5,3,NULL,47,'client1','','lastname','',NULL,'client1 lastname');
INSERT INTO customer_name_detail VALUES (6,5,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395');
INSERT INTO customer_name_detail VALUES (7,6,3,NULL,47,'Stu1233171716380','','Client1233171716380','',NULL,'Stu1233171716380 Client1233171716380');
INSERT INTO customer_name_detail VALUES (8,6,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395');
INSERT INTO customer_name_detail VALUES (9,7,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (10,7,3,NULL,47,'Client - Mary','','Monthly1','',NULL,'Client - Mary Monthly1');
INSERT INTO customer_name_detail VALUES (11,8,3,NULL,228,'Client - Mia','','Monthly3rdFriday','',NULL,'Client - Mia Monthly3rdFriday');
INSERT INTO customer_name_detail VALUES (12,8,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (13,10,3,NULL,47,'ClientWithLoan','','20110221','',NULL,'ClientWithLoan 20110221');
INSERT INTO customer_name_detail VALUES (14,10,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (15,12,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (16,12,3,NULL,47,'Stu1233266299995','','Client1233266299995','',NULL,'Stu1233266299995 Client1233266299995');
INSERT INTO customer_name_detail VALUES (17,13,3,NULL,47,'Stu1233266309851','','Client1233266309851','',NULL,'Stu1233266309851 Client1233266309851');
INSERT INTO customer_name_detail VALUES (18,13,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (19,14,3,NULL,47,'Stu1233266319760','','Client1233266319760','',NULL,'Stu1233266319760 Client1233266319760');
INSERT INTO customer_name_detail VALUES (20,14,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (21,17,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (22,17,3,NULL,47,'UpdateCustomProperties','','TestClient','',NULL,'UpdateCustomProperties TestClient');
INSERT INTO customer_name_detail VALUES (23,18,3,NULL,47,'Client','','WeeklyTue','',NULL,'Client WeeklyTue');
INSERT INTO customer_name_detail VALUES (24,18,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (25,23,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (26,23,3,NULL,47,'Holiday','','TestClient','',NULL,'Holiday TestClient');
INSERT INTO customer_name_detail VALUES (27,24,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (28,24,3,NULL,47,'WeeklyClient','','Monday','',NULL,'WeeklyClient Monday');
INSERT INTO customer_name_detail VALUES (29,26,3,NULL,47,'MemberWeekly','','Group','',NULL,'MemberWeekly Group');
INSERT INTO customer_name_detail VALUES (30,26,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (31,28,3,NULL,47,'WeeklyOld','','Monday','',NULL,'WeeklyOld Monday');
INSERT INTO customer_name_detail VALUES (32,28,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (33,29,3,NULL,47,'WeeklyOld','','Tuesday','',NULL,'WeeklyOld Tuesday');
INSERT INTO customer_name_detail VALUES (34,29,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (35,30,3,NULL,47,'Stu12332659912419','','Client12332659912419','',NULL,'Stu12332659912419 Client12332659912419');
INSERT INTO customer_name_detail VALUES (36,30,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (37,31,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (38,31,3,NULL,47,'WeeklyClient','','Wednesday','',NULL,'WeeklyClient Wednesday');
INSERT INTO customer_name_detail VALUES (39,33,3,NULL,47,'ClientInBranch1','','ClientInBranch1','',NULL,'ClientInBranch1 ClientInBranch1');
INSERT INTO customer_name_detail VALUES (40,33,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (41,37,3,NULL,47,'SavingsAccountPerformanceHistoryTestClient','','SavingsAccountPerformanceHistoryTestClient','',NULL,'SavingsAccountPerformanceHistoryTestClient SavingsAccountPerformanceHistoryTestClient');
INSERT INTO customer_name_detail VALUES (42,37,NULL,NULL,NULL,'','','','',NULL,'');
INSERT INTO customer_name_detail VALUES (43,40,1,NULL,NULL,'asdfdsaf','','asdfdsaf','',NULL,'asdfdsaf asdfdsaf');
INSERT INTO customer_name_detail VALUES (44,40,3,NULL,47,'DefineNewSavingsProduct','','TestClient','',NULL,'DefineNewSavingsProduct TestClient');
/*!40000 ALTER TABLE customer_name_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customer_note;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_note (
  comment_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  field_officer_id smallint(6) NOT NULL,
  comment_date date NOT NULL,
  `comment` varchar(500) NOT NULL,
  PRIMARY KEY (comment_id),
  KEY field_officer_id (field_officer_id),
  KEY cust_note_idx (customer_id),
  CONSTRAINT customer_note_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_note_ibfk_2 FOREIGN KEY (field_officer_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_note DISABLE KEYS */;
INSERT INTO customer_note VALUES (1,2,1,'2011-02-21','Active');
INSERT INTO customer_note VALUES (2,3,1,'2011-02-21','Active\r\n	');
INSERT INTO customer_note VALUES (3,4,1,'2010-01-22','Active');
INSERT INTO customer_note VALUES (4,5,1,'2010-01-22','Active');
INSERT INTO customer_note VALUES (5,6,1,'2010-01-22','Active');
INSERT INTO customer_note VALUES (6,7,1,'2010-01-22','Active');
INSERT INTO customer_note VALUES (7,8,1,'2010-01-22','Active');
INSERT INTO customer_note VALUES (8,9,1,'2011-02-18','Active');
INSERT INTO customer_note VALUES (9,10,1,'2011-02-18','Active');
INSERT INTO customer_note VALUES (10,12,1,'2011-02-22','Active');
INSERT INTO customer_note VALUES (11,13,1,'2011-02-22','Active');
INSERT INTO customer_note VALUES (12,14,1,'2011-02-22','Active');
INSERT INTO customer_note VALUES (13,11,1,'2011-02-22','Active');
INSERT INTO customer_note VALUES (14,16,1,'2011-02-25','UpdateCustomPropertiesTest');
INSERT INTO customer_note VALUES (15,17,1,'2011-02-25','TestClient');
INSERT INTO customer_note VALUES (16,18,1,'2010-10-11','Active');
INSERT INTO customer_note VALUES (17,20,1,'2011-02-25','activating the group');
INSERT INTO customer_note VALUES (18,22,1,'2011-02-25','activating the group');
INSERT INTO customer_note VALUES (19,23,1,'2020-01-01','Active');
INSERT INTO customer_note VALUES (20,24,1,'2011-02-28','Active');
INSERT INTO customer_note VALUES (21,25,1,'2011-03-03','a');
INSERT INTO customer_note VALUES (22,26,1,'2011-03-03','a');
INSERT INTO customer_note VALUES (23,28,1,'2010-10-11','Active');
INSERT INTO customer_note VALUES (24,29,1,'2010-10-11','Active');
INSERT INTO customer_note VALUES (25,30,1,'2011-03-08','a');
INSERT INTO customer_note VALUES (26,32,1,'2011-03-10','Ok');
INSERT INTO customer_note VALUES (27,33,1,'2011-03-10','Ok');
INSERT INTO customer_note VALUES (28,36,1,'2011-03-14','Ok');
INSERT INTO customer_note VALUES (29,37,1,'2011-03-14','Ok');
INSERT INTO customer_note VALUES (30,39,1,'2011-03-15','asfdas');
INSERT INTO customer_note VALUES (31,40,1,'2011-03-15','asdf');
/*!40000 ALTER TABLE customer_note ENABLE KEYS */;
DROP TABLE IF EXISTS customer_picture;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_picture (
  picture_id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  picture blob,
  PRIMARY KEY (picture_id),
  KEY customer_id (customer_id),
  CONSTRAINT customer_picture_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_picture DISABLE KEYS */;
INSERT INTO customer_picture VALUES (1,2,NULL);
INSERT INTO customer_picture VALUES (2,3,NULL);
INSERT INTO customer_picture VALUES (3,5,NULL);
INSERT INTO customer_picture VALUES (4,6,NULL);
INSERT INTO customer_picture VALUES (5,7,NULL);
INSERT INTO customer_picture VALUES (6,8,NULL);
INSERT INTO customer_picture VALUES (7,10,NULL);
INSERT INTO customer_picture VALUES (8,12,NULL);
INSERT INTO customer_picture VALUES (9,13,NULL);
INSERT INTO customer_picture VALUES (10,14,NULL);
INSERT INTO customer_picture VALUES (11,17,NULL);
INSERT INTO customer_picture VALUES (12,18,NULL);
INSERT INTO customer_picture VALUES (13,23,NULL);
INSERT INTO customer_picture VALUES (14,24,NULL);
INSERT INTO customer_picture VALUES (15,26,NULL);
INSERT INTO customer_picture VALUES (16,28,NULL);
INSERT INTO customer_picture VALUES (17,29,NULL);
INSERT INTO customer_picture VALUES (18,30,NULL);
INSERT INTO customer_picture VALUES (19,31,NULL);
INSERT INTO customer_picture VALUES (20,33,NULL);
INSERT INTO customer_picture VALUES (21,37,NULL);
INSERT INTO customer_picture VALUES (22,40,NULL);
/*!40000 ALTER TABLE customer_picture ENABLE KEYS */;
DROP TABLE IF EXISTS customer_position;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_position (
  customer_position_id int(11) NOT NULL AUTO_INCREMENT,
  position_id int(11) NOT NULL,
  customer_id int(11) DEFAULT NULL,
  parent_customer_id int(11) DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (customer_position_id),
  UNIQUE KEY cust_position_idx (customer_id,position_id),
  KEY parent_customer_id (parent_customer_id),
  CONSTRAINT customer_position_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_position_ibfk_2 FOREIGN KEY (parent_customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_position DISABLE KEYS */;
/*!40000 ALTER TABLE customer_position ENABLE KEYS */;
DROP TABLE IF EXISTS customer_program;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_program (
  program_id int(11) NOT NULL,
  customer_id int(11) NOT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (program_id,customer_id),
  KEY customer_id (customer_id),
  CONSTRAINT customer_program_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_program_ibfk_2 FOREIGN KEY (program_id) REFERENCES program (program_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_program DISABLE KEYS */;
/*!40000 ALTER TABLE customer_program ENABLE KEYS */;
DROP TABLE IF EXISTS customer_schedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_schedule (
  id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  customer_id int(11) NOT NULL,
  currency_id smallint(6) DEFAULT NULL,
  action_date date DEFAULT NULL,
  misc_fees decimal(21,4) DEFAULT NULL,
  misc_fees_currency_id smallint(6) DEFAULT NULL,
  misc_fees_paid decimal(21,4) DEFAULT NULL,
  misc_fees_paid_currency_id smallint(6) DEFAULT NULL,
  misc_penalty decimal(21,4) DEFAULT NULL,
  misc_penalty_currency_id smallint(6) DEFAULT NULL,
  misc_penalty_paid decimal(21,4) DEFAULT NULL,
  misc_penalty_paid_currency_id smallint(6) DEFAULT NULL,
  payment_status smallint(6) NOT NULL,
  installment_id smallint(6) NOT NULL,
  payment_date date DEFAULT NULL,
  parent_flag smallint(6) DEFAULT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (id),
  KEY account_id (account_id),
  KEY currency_id (currency_id),
  KEY customer_schedule_action_date_idx (customer_id,action_date,payment_status),
  CONSTRAINT customer_schedule_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_schedule_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_schedule_ibfk_3 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=442 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_schedule DISABLE KEYS */;
INSERT INTO customer_schedule VALUES (1,1,1,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (2,1,1,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (3,1,1,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (4,1,1,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (5,1,1,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (6,1,1,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (7,1,1,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (8,1,1,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (9,1,1,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (10,1,1,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (11,3,2,NULL,'2011-02-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (12,3,2,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (13,3,2,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (14,3,2,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (15,3,2,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (16,3,2,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (17,3,2,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (18,3,2,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (19,3,2,NULL,'2011-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (20,3,2,NULL,'2011-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (21,3,2,NULL,'2011-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (22,4,3,NULL,'2011-02-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (23,4,3,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (24,4,3,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (25,4,3,NULL,'2011-03-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (26,4,3,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (27,4,3,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (28,4,3,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (29,4,3,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (30,4,3,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (31,4,3,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (32,5,4,NULL,'2010-01-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (33,5,4,NULL,'2010-01-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (34,5,4,NULL,'2010-02-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (35,5,4,NULL,'2010-02-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (36,5,4,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (37,5,4,NULL,'2010-02-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (38,5,4,NULL,'2010-03-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (39,5,4,NULL,'2010-03-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (40,5,4,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (41,5,4,NULL,'2010-03-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (42,6,5,NULL,'2010-01-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (43,6,5,NULL,'2010-01-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (44,6,5,NULL,'2010-02-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (45,6,5,NULL,'2010-02-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (46,6,5,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (47,6,5,NULL,'2010-02-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (48,6,5,NULL,'2010-03-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (49,6,5,NULL,'2010-03-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (50,6,5,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (51,6,5,NULL,'2010-03-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (52,7,6,NULL,'2010-01-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (53,7,6,NULL,'2010-01-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (54,7,6,NULL,'2010-02-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (55,7,6,NULL,'2010-02-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (56,7,6,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (57,7,6,NULL,'2010-02-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (58,7,6,NULL,'2010-03-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (59,7,6,NULL,'2010-03-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (60,7,6,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (61,7,6,NULL,'2010-03-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (62,8,7,NULL,'2010-02-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (63,8,7,NULL,'2010-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (64,8,7,NULL,'2010-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (65,8,7,NULL,'2010-05-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (66,8,7,NULL,'2010-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (67,8,7,NULL,'2010-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (68,8,7,NULL,'2010-08-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (69,8,7,NULL,'2010-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (70,8,7,NULL,'2010-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (71,8,7,NULL,'2010-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (72,9,8,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (73,9,8,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (74,9,8,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (75,9,8,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (76,9,8,NULL,'2010-06-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (77,9,8,NULL,'2010-07-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (78,9,8,NULL,'2010-08-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (79,9,8,NULL,'2010-09-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (80,9,8,NULL,'2010-10-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (81,9,8,NULL,'2010-11-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (82,13,9,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (83,13,9,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (84,13,9,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (85,13,9,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (86,13,9,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (87,13,9,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (88,13,9,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (89,13,9,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (90,13,9,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (91,13,9,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (92,14,10,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (93,14,10,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (94,14,10,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (95,14,10,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (96,14,10,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (97,14,10,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (98,14,10,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (99,14,10,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (100,14,10,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (101,14,10,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (102,5,4,NULL,'2010-04-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (103,5,4,NULL,'2010-04-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (104,5,4,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (105,5,4,NULL,'2010-04-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (106,5,4,NULL,'2010-04-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (107,5,4,NULL,'2010-05-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (108,5,4,NULL,'2010-05-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (109,5,4,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (110,5,4,NULL,'2010-05-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (111,5,4,NULL,'2010-06-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (112,6,5,NULL,'2010-04-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (113,6,5,NULL,'2010-04-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (114,6,5,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (115,6,5,NULL,'2010-04-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (116,6,5,NULL,'2010-04-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (117,6,5,NULL,'2010-05-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (118,6,5,NULL,'2010-05-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (119,6,5,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (120,6,5,NULL,'2010-05-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (121,6,5,NULL,'2010-06-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (122,7,6,NULL,'2010-04-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (123,7,6,NULL,'2010-04-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (124,7,6,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (125,7,6,NULL,'2010-04-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (126,7,6,NULL,'2010-04-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (127,7,6,NULL,'2010-05-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (128,7,6,NULL,'2010-05-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (129,7,6,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (130,7,6,NULL,'2010-05-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (131,7,6,NULL,'2010-06-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (132,8,7,NULL,'2010-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (133,8,7,NULL,'2011-01-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (134,8,7,NULL,'2011-02-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (135,8,7,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (136,8,7,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (137,8,7,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (138,8,7,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (139,8,7,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (140,8,7,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (141,8,7,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (142,9,8,NULL,'2010-12-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (143,9,8,NULL,'2011-01-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (144,9,8,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (145,9,8,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (146,9,8,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (147,9,8,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (148,9,8,NULL,'2011-06-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (149,9,8,NULL,'2011-07-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (150,9,8,NULL,'2011-08-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (151,9,8,NULL,'2011-09-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (152,17,12,NULL,'2011-02-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (153,17,12,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (154,17,12,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (155,17,12,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (156,17,12,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (157,17,12,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (158,17,12,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (159,17,12,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (160,17,12,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (161,17,12,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (162,18,13,NULL,'2011-02-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (163,18,13,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (164,18,13,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (165,18,13,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (166,18,13,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (167,18,13,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (168,18,13,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (169,18,13,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (170,18,13,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (171,18,13,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (172,19,14,NULL,'2011-02-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (173,19,14,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (174,19,14,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (175,19,14,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (176,19,14,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (177,19,14,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (178,19,14,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (179,19,14,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (180,19,14,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (181,19,14,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (182,16,11,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (183,16,11,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (184,16,11,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (185,16,11,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (186,16,11,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (187,16,11,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (188,16,11,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (189,16,11,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (190,16,11,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (191,16,11,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (192,21,15,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (193,21,15,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (194,21,15,NULL,'2011-03-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (195,21,15,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (196,21,15,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (197,21,15,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (198,21,15,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (199,21,15,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (200,21,15,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (201,21,15,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (202,22,16,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (203,22,16,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (204,22,16,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (205,22,16,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (206,22,16,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (207,22,16,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (208,22,16,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (209,22,16,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (210,22,16,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (211,22,16,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (212,23,17,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (213,23,17,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (214,23,17,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (215,23,17,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (216,23,17,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (217,23,17,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (218,23,17,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (219,23,17,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (220,23,17,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (221,23,17,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (222,24,18,NULL,'2010-10-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (223,24,18,NULL,'2010-10-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (224,24,18,NULL,'2010-10-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (225,24,18,NULL,'2010-11-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (226,24,18,NULL,'2010-11-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (227,24,18,NULL,'2010-11-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (228,24,18,NULL,'2010-11-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (229,24,18,NULL,'2010-11-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (230,24,18,NULL,'2010-12-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (231,24,18,NULL,'2010-12-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (232,30,19,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (233,30,19,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (234,30,19,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (235,30,19,NULL,'2011-06-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (236,30,19,NULL,'2011-07-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (237,30,19,NULL,'2011-08-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (238,30,19,NULL,'2011-09-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (239,30,19,NULL,'2011-10-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (240,30,19,NULL,'2011-11-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (241,30,19,NULL,'2011-12-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (242,31,20,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (243,31,20,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (244,31,20,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (245,31,20,NULL,'2011-06-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (246,31,20,NULL,'2011-07-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (247,31,20,NULL,'2011-08-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (248,31,20,NULL,'2011-09-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (249,31,20,NULL,'2011-10-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (250,31,20,NULL,'2011-11-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (251,31,20,NULL,'2011-12-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (252,32,21,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (253,32,21,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (254,32,21,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (255,32,21,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (256,32,21,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (257,32,21,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (258,32,21,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (259,32,21,NULL,'2011-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (260,32,21,NULL,'2011-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (261,32,21,NULL,'2011-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (262,33,22,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (263,33,22,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (264,33,22,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (265,33,22,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (266,33,22,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (267,33,22,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (268,33,22,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (269,33,22,NULL,'2011-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (270,33,22,NULL,'2011-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (271,33,22,NULL,'2011-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (272,34,23,NULL,'2020-01-03','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (273,34,23,NULL,'2020-01-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (274,34,23,NULL,'2020-01-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (275,34,23,NULL,'2020-01-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (276,34,23,NULL,'2020-01-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (277,34,23,NULL,'2020-02-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (278,34,23,NULL,'2020-02-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (279,34,23,NULL,'2020-02-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (280,34,23,NULL,'2020-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (281,34,23,NULL,'2020-03-06','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (282,36,24,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (283,36,24,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (284,36,24,NULL,'2011-03-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (285,36,24,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (286,36,24,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (287,36,24,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (288,36,24,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (289,36,24,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (290,36,24,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (291,36,24,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (292,48,25,NULL,'2011-01-03','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (293,48,25,NULL,'2011-01-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (294,48,25,NULL,'2011-01-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (295,48,25,NULL,'2011-01-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (296,48,25,NULL,'2011-01-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (297,48,25,NULL,'2011-02-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (298,48,25,NULL,'2011-02-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (299,48,25,NULL,'2011-02-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (300,48,25,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (301,48,25,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (302,49,26,NULL,'2011-01-03','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (303,49,26,NULL,'2011-01-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (304,49,26,NULL,'2011-01-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (305,49,26,NULL,'2011-01-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (306,49,26,NULL,'2011-01-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (307,49,26,NULL,'2011-02-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (308,49,26,NULL,'2011-02-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (309,49,26,NULL,'2011-02-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (310,49,26,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (311,49,26,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (312,51,27,NULL,'2011-03-03','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (313,51,27,NULL,'2011-03-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (314,51,27,NULL,'2011-03-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (315,51,27,NULL,'2011-03-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (316,51,27,NULL,'2011-03-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (317,51,27,NULL,'2011-04-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (318,51,27,NULL,'2011-04-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (319,51,27,NULL,'2011-04-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (320,51,27,NULL,'2011-04-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (321,51,27,NULL,'2011-05-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (322,56,28,NULL,'2010-10-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (323,56,28,NULL,'2010-10-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (324,56,28,NULL,'2010-10-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (325,56,28,NULL,'2010-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (326,56,28,NULL,'2010-11-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (327,56,28,NULL,'2010-11-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (328,56,28,NULL,'2010-11-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (329,56,28,NULL,'2010-11-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (330,56,28,NULL,'2010-12-06','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (331,56,28,NULL,'2010-12-13','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (332,57,29,NULL,'2010-10-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (333,57,29,NULL,'2010-10-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (334,57,29,NULL,'2010-10-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (335,57,29,NULL,'2010-11-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (336,57,29,NULL,'2010-11-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (337,57,29,NULL,'2010-11-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (338,57,29,NULL,'2010-11-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (339,57,29,NULL,'2010-11-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (340,57,29,NULL,'2010-12-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (341,57,29,NULL,'2010-12-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (342,58,30,NULL,'2011-01-03','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (343,58,30,NULL,'2011-01-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (344,58,30,NULL,'2011-01-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (345,58,30,NULL,'2011-01-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (346,58,30,NULL,'2011-01-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (347,58,30,NULL,'2011-02-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (348,58,30,NULL,'2011-02-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (349,58,30,NULL,'2011-02-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (350,58,30,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (351,58,30,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (352,61,32,NULL,'2011-03-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (353,61,32,NULL,'2011-03-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (354,61,32,NULL,'2011-03-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (355,61,32,NULL,'2011-03-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (356,61,32,NULL,'2011-04-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (357,61,32,NULL,'2011-04-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (358,61,32,NULL,'2011-04-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (359,61,32,NULL,'2011-04-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (360,61,32,NULL,'2011-05-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (361,61,32,NULL,'2011-05-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (362,62,33,NULL,'2011-03-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (363,62,33,NULL,'2011-03-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (364,62,33,NULL,'2011-03-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (365,62,33,NULL,'2011-03-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (366,62,33,NULL,'2011-04-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (367,62,33,NULL,'2011-04-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (368,62,33,NULL,'2011-04-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (369,62,33,NULL,'2011-04-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (370,62,33,NULL,'2011-05-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (371,62,33,NULL,'2011-05-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (372,63,34,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (373,63,34,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (374,63,34,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (375,63,34,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (376,63,34,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (377,63,34,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (378,63,34,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (379,63,34,NULL,'2011-05-06','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (380,63,34,NULL,'2011-05-13','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (381,63,34,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (382,64,35,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (383,64,35,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (384,64,35,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (385,64,35,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (386,64,35,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (387,64,35,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (388,64,35,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (389,64,35,NULL,'2011-05-06','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (390,64,35,NULL,'2011-05-13','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (391,64,35,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (392,65,36,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (393,65,36,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (394,65,36,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (395,65,36,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (396,65,36,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (397,65,36,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (398,65,36,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (399,65,36,NULL,'2011-05-06','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (400,65,36,NULL,'2011-05-13','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (401,65,36,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (402,66,37,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (403,66,37,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (404,66,37,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (405,66,37,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (406,66,37,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (407,66,37,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (408,66,37,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (409,66,37,NULL,'2011-05-06','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (410,66,37,NULL,'2011-05-13','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (411,66,37,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (412,68,38,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (413,68,38,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (414,68,38,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (415,68,38,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (416,68,38,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (417,68,38,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (418,68,38,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (419,68,38,NULL,'2011-05-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (420,68,38,NULL,'2011-05-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (421,68,38,NULL,'2011-05-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1);
INSERT INTO customer_schedule VALUES (422,69,39,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (423,69,39,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (424,69,39,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (425,69,39,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (426,69,39,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (427,69,39,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (428,69,39,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (429,69,39,NULL,'2011-05-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (430,69,39,NULL,'2011-05-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (431,69,39,NULL,'2011-05-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (432,70,40,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (433,70,40,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (434,70,40,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (435,70,40,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (436,70,40,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (437,70,40,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (438,70,40,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (439,70,40,NULL,'2011-05-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (440,70,40,NULL,'2011-05-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO customer_schedule VALUES (441,70,40,NULL,'2011-05-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
/*!40000 ALTER TABLE customer_schedule ENABLE KEYS */;
DROP TABLE IF EXISTS customer_state;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_state (
  status_id smallint(6) NOT NULL AUTO_INCREMENT,
  status_lookup_id int(11) NOT NULL,
  level_id smallint(6) NOT NULL,
  description varchar(200) DEFAULT NULL,
  currently_in_use smallint(6) NOT NULL,
  PRIMARY KEY (status_id),
  KEY level_id (level_id),
  KEY status_lookup_id (status_lookup_id),
  CONSTRAINT customer_state_ibfk_1 FOREIGN KEY (level_id) REFERENCES customer_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_state_ibfk_2 FOREIGN KEY (status_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_state DISABLE KEYS */;
INSERT INTO customer_state VALUES (1,1,1,'Customer Was Partial',1);
INSERT INTO customer_state VALUES (2,2,1,'Customer Was Pending',1);
INSERT INTO customer_state VALUES (3,3,1,'Customer Was Active',1);
INSERT INTO customer_state VALUES (4,4,1,'Customer Was Hold',1);
INSERT INTO customer_state VALUES (5,5,1,'Customer Was Cancel',1);
INSERT INTO customer_state VALUES (6,6,1,'Customer Was Close',1);
INSERT INTO customer_state VALUES (7,7,2,'Customer Was Partial',1);
INSERT INTO customer_state VALUES (8,8,2,'Customer Was Pending',1);
INSERT INTO customer_state VALUES (9,9,2,'Customer Was Active',1);
INSERT INTO customer_state VALUES (10,10,2,'Customer Was Hold',1);
INSERT INTO customer_state VALUES (11,11,2,'Customer Was Cancel',1);
INSERT INTO customer_state VALUES (12,12,2,'Customer Was Close',1);
INSERT INTO customer_state VALUES (13,13,3,'Customer Was Active',1);
INSERT INTO customer_state VALUES (14,14,3,'Customer Was Inactive',1);
/*!40000 ALTER TABLE customer_state ENABLE KEYS */;
DROP TABLE IF EXISTS customer_state_flag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_state_flag (
  flag_id smallint(6) NOT NULL,
  flag_lookup_id int(11) NOT NULL,
  status_id smallint(6) NOT NULL,
  flag_description varchar(200) NOT NULL,
  isblacklisted smallint(6) DEFAULT NULL,
  PRIMARY KEY (flag_id),
  KEY status_id (status_id),
  KEY flag_lookup_id (flag_lookup_id),
  CONSTRAINT customer_state_flag_ibfk_1 FOREIGN KEY (status_id) REFERENCES customer_state (status_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_state_flag_ibfk_2 FOREIGN KEY (flag_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_state_flag DISABLE KEYS */;
INSERT INTO customer_state_flag VALUES (1,28,5,'Withdraw',0);
INSERT INTO customer_state_flag VALUES (2,29,5,'Rejected',0);
INSERT INTO customer_state_flag VALUES (3,30,5,'Blacklisted',1);
INSERT INTO customer_state_flag VALUES (4,31,5,'Duplicate',0);
INSERT INTO customer_state_flag VALUES (5,34,5,'Other',0);
INSERT INTO customer_state_flag VALUES (6,32,6,'Transferred',0);
INSERT INTO customer_state_flag VALUES (7,31,6,'Duplicate',0);
INSERT INTO customer_state_flag VALUES (8,30,6,'Blacklisted',1);
INSERT INTO customer_state_flag VALUES (9,33,6,'Left program',0);
INSERT INTO customer_state_flag VALUES (10,34,6,'Other',0);
INSERT INTO customer_state_flag VALUES (11,28,11,'Withdraw',0);
INSERT INTO customer_state_flag VALUES (12,29,11,'Rejected',0);
INSERT INTO customer_state_flag VALUES (13,30,11,'Blacklisted',1);
INSERT INTO customer_state_flag VALUES (14,31,11,'Duplicate',0);
INSERT INTO customer_state_flag VALUES (15,34,11,'Other',0);
INSERT INTO customer_state_flag VALUES (16,32,12,'Transferred',0);
INSERT INTO customer_state_flag VALUES (17,31,12,'Duplicate',0);
INSERT INTO customer_state_flag VALUES (18,30,12,'Blacklisted',1);
INSERT INTO customer_state_flag VALUES (19,33,12,'Left program',0);
INSERT INTO customer_state_flag VALUES (20,34,12,'Other',0);
/*!40000 ALTER TABLE customer_state_flag ENABLE KEYS */;
DROP TABLE IF EXISTS customer_trxn_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customer_trxn_detail (
  account_trxn_id int(11) NOT NULL,
  total_amount decimal(21,4) DEFAULT NULL,
  total_amount_currency_id smallint(6) DEFAULT NULL,
  misc_fee_amount decimal(21,4) DEFAULT NULL,
  misc_fee_amount_currency_id smallint(6) DEFAULT NULL,
  misc_penalty_amount decimal(21,4) DEFAULT NULL,
  misc_penalty_amount_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (account_trxn_id),
  KEY total_amount_currency_id (total_amount_currency_id),
  KEY misc_penalty_amount_currency_id (misc_penalty_amount_currency_id),
  KEY misc_fee_amount_currency_id (misc_fee_amount_currency_id),
  CONSTRAINT customer_trxn_detail_ibfk_1 FOREIGN KEY (total_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_trxn_detail_ibfk_2 FOREIGN KEY (misc_penalty_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_trxn_detail_ibfk_3 FOREIGN KEY (misc_fee_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT customer_trxn_detail_ibfk_4 FOREIGN KEY (account_trxn_id) REFERENCES account_trxn (account_trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_trxn_detail DISABLE KEYS */;
/*!40000 ALTER TABLE customer_trxn_detail ENABLE KEYS */;
DROP TABLE IF EXISTS customized_text;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE customized_text (
  original_text varchar(50) COLLATE utf8_bin NOT NULL,
  custom_text varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (original_text)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customized_text DISABLE KEYS */;
/*!40000 ALTER TABLE customized_text ENABLE KEYS */;
DROP TABLE IF EXISTS entity_master;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE entity_master (
  entity_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  entity_type varchar(100) NOT NULL,
  PRIMARY KEY (entity_type_id)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE entity_master DISABLE KEYS */;
INSERT INTO entity_master VALUES (1,'Client');
INSERT INTO entity_master VALUES (2,'LoanProduct');
INSERT INTO entity_master VALUES (3,'SavingsProduct');
INSERT INTO entity_master VALUES (4,'ProductCategory');
INSERT INTO entity_master VALUES (5,'ProductConfiguration');
INSERT INTO entity_master VALUES (6,'Fees');
INSERT INTO entity_master VALUES (7,'Accounts');
INSERT INTO entity_master VALUES (8,'Admin');
INSERT INTO entity_master VALUES (9,'Checklist');
INSERT INTO entity_master VALUES (10,'Configuration');
INSERT INTO entity_master VALUES (11,'Customer');
INSERT INTO entity_master VALUES (12,'Group');
INSERT INTO entity_master VALUES (13,'Login');
INSERT INTO entity_master VALUES (14,'Meeting');
INSERT INTO entity_master VALUES (15,'Office');
INSERT INTO entity_master VALUES (16,'Penalty');
INSERT INTO entity_master VALUES (17,'Personnel');
INSERT INTO entity_master VALUES (19,'Roleandpermission');
INSERT INTO entity_master VALUES (20,'Center');
INSERT INTO entity_master VALUES (21,'Savings');
INSERT INTO entity_master VALUES (22,'Loan');
INSERT INTO entity_master VALUES (23,'BulkEntry');
/*!40000 ALTER TABLE entity_master ENABLE KEYS */;
DROP TABLE IF EXISTS event_sources;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE event_sources (
  id int(11) NOT NULL,
  entity_type_id smallint(6) NOT NULL,
  event_id int(11) NOT NULL,
  description varchar(200) NOT NULL,
  PRIMARY KEY (id),
  KEY entity_type_id (entity_type_id),
  KEY event_id (event_id),
  CONSTRAINT event_sources_ibfk_1 FOREIGN KEY (entity_type_id) REFERENCES entity_master (entity_type_id),
  CONSTRAINT event_sources_ibfk_2 FOREIGN KEY (event_id) REFERENCES `events` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE event_sources DISABLE KEYS */;
INSERT INTO event_sources VALUES (1,1,1,'Create Client');
INSERT INTO event_sources VALUES (2,22,1,'Create Loan');
INSERT INTO event_sources VALUES (3,1,2,'View Client');
INSERT INTO event_sources VALUES (4,12,1,'Create Group');
INSERT INTO event_sources VALUES (5,22,3,'Approve Loan');
INSERT INTO event_sources VALUES (6,1,4,'Close Client');
INSERT INTO event_sources VALUES (7,22,2,'View Loan');
INSERT INTO event_sources VALUES (8,12,2,'View Group');
INSERT INTO event_sources VALUES (9,20,1,'Create Center');
INSERT INTO event_sources VALUES (10,20,2,'View Center');
INSERT INTO event_sources VALUES (11,22,5,'Disburse Loan');
INSERT INTO event_sources VALUES (12,21,1,'Create Savings');
INSERT INTO event_sources VALUES (13,21,2,'View Savings');
INSERT INTO event_sources VALUES (14,15,1,'Create Office');
INSERT INTO event_sources VALUES (15,17,1,'Create Personnel');
/*!40000 ALTER TABLE event_sources ENABLE KEYS */;
DROP TABLE IF EXISTS events;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
  id int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE events DISABLE KEYS */;
INSERT INTO events VALUES (1,'Create');
INSERT INTO events VALUES (2,'View');
INSERT INTO events VALUES (3,'Approve');
INSERT INTO events VALUES (4,'Close');
INSERT INTO events VALUES (5,'Disburse');
/*!40000 ALTER TABLE events ENABLE KEYS */;
DROP TABLE IF EXISTS fee_formula_master;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_formula_master (
  formulaid smallint(6) NOT NULL AUTO_INCREMENT,
  forumla_lookup_id int(11) NOT NULL,
  PRIMARY KEY (formulaid),
  KEY forumla_lookup_id (forumla_lookup_id),
  CONSTRAINT fee_formula_master_ibfk_1 FOREIGN KEY (forumla_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_formula_master DISABLE KEYS */;
INSERT INTO fee_formula_master VALUES (1,149);
INSERT INTO fee_formula_master VALUES (2,150);
INSERT INTO fee_formula_master VALUES (3,151);
/*!40000 ALTER TABLE fee_formula_master ENABLE KEYS */;
DROP TABLE IF EXISTS fee_frequency;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_frequency (
  fee_frequency_id smallint(6) NOT NULL AUTO_INCREMENT,
  fee_id smallint(6) NOT NULL,
  fee_frequencytype_id smallint(6) NOT NULL,
  frequency_payment_id smallint(6) DEFAULT NULL,
  frequency_meeting_id int(11) DEFAULT NULL,
  PRIMARY KEY (fee_frequency_id),
  KEY fee_id (fee_id),
  KEY fee_frequencytype_id (fee_frequencytype_id),
  KEY frequency_payment_id (frequency_payment_id),
  KEY frequency_meeting_id (frequency_meeting_id),
  CONSTRAINT fee_frequency_ibfk_1 FOREIGN KEY (fee_id) REFERENCES fees (fee_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fee_frequency_ibfk_2 FOREIGN KEY (fee_frequencytype_id) REFERENCES fee_frequency_type (fee_frequency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fee_frequency_ibfk_3 FOREIGN KEY (frequency_payment_id) REFERENCES fee_payment (fee_payment_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fee_frequency_ibfk_4 FOREIGN KEY (frequency_meeting_id) REFERENCES meeting (meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_frequency DISABLE KEYS */;
INSERT INTO fee_frequency VALUES (1,1,2,1,NULL);
INSERT INTO fee_frequency VALUES (2,2,2,2,NULL);
INSERT INTO fee_frequency VALUES (3,3,1,NULL,24);
INSERT INTO fee_frequency VALUES (4,4,2,1,NULL);
INSERT INTO fee_frequency VALUES (5,5,2,1,NULL);
/*!40000 ALTER TABLE fee_frequency ENABLE KEYS */;
DROP TABLE IF EXISTS fee_frequency_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_frequency_type (
  fee_frequency_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (fee_frequency_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT fee_frequency_type_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_frequency_type DISABLE KEYS */;
INSERT INTO fee_frequency_type VALUES (1,558);
INSERT INTO fee_frequency_type VALUES (2,559);
/*!40000 ALTER TABLE fee_frequency_type ENABLE KEYS */;
DROP TABLE IF EXISTS fee_payment;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_payment (
  fee_payment_id smallint(6) NOT NULL AUTO_INCREMENT,
  fee_payment_lookup_id int(11) DEFAULT NULL,
  PRIMARY KEY (fee_payment_id),
  KEY fee_payment_lookup_id (fee_payment_lookup_id),
  CONSTRAINT fee_payment_ibfk_1 FOREIGN KEY (fee_payment_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_payment DISABLE KEYS */;
INSERT INTO fee_payment VALUES (1,146);
INSERT INTO fee_payment VALUES (2,147);
INSERT INTO fee_payment VALUES (3,148);
/*!40000 ALTER TABLE fee_payment ENABLE KEYS */;
DROP TABLE IF EXISTS fee_payments_categories_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_payments_categories_type (
  fee_payments_category_type_id smallint(6) NOT NULL,
  fee_payment_id smallint(6) DEFAULT NULL,
  category_id smallint(6) DEFAULT NULL,
  fee_type_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (fee_payments_category_type_id),
  KEY category_id (category_id),
  KEY fee_payment_id (fee_payment_id),
  KEY fee_type_id (fee_type_id),
  CONSTRAINT fee_payments_categories_type_ibfk_1 FOREIGN KEY (category_id) REFERENCES category_type (category_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fee_payments_categories_type_ibfk_2 FOREIGN KEY (fee_payment_id) REFERENCES fee_payment (fee_payment_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fee_payments_categories_type_ibfk_3 FOREIGN KEY (fee_type_id) REFERENCES fee_type (fee_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_payments_categories_type DISABLE KEYS */;
INSERT INTO fee_payments_categories_type VALUES (1,1,1,1);
INSERT INTO fee_payments_categories_type VALUES (2,1,1,1);
INSERT INTO fee_payments_categories_type VALUES (3,1,1,1);
/*!40000 ALTER TABLE fee_payments_categories_type ENABLE KEYS */;
DROP TABLE IF EXISTS fee_status;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_status (
  status_id smallint(6) NOT NULL AUTO_INCREMENT,
  status_lookup_id int(11) NOT NULL,
  PRIMARY KEY (status_id),
  KEY status_lookup_id (status_lookup_id),
  CONSTRAINT fee_status_ibfk_1 FOREIGN KEY (status_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_status DISABLE KEYS */;
INSERT INTO fee_status VALUES (1,165);
INSERT INTO fee_status VALUES (2,166);
/*!40000 ALTER TABLE fee_status ENABLE KEYS */;
DROP TABLE IF EXISTS fee_trxn_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_trxn_detail (
  fee_trxn_detail_id int(11) NOT NULL AUTO_INCREMENT,
  account_trxn_id int(11) NOT NULL,
  account_fee_id int(11) DEFAULT NULL,
  fee_amount_currency_id smallint(6) DEFAULT NULL,
  fee_amount decimal(21,4) NOT NULL,
  PRIMARY KEY (fee_trxn_detail_id),
  KEY account_fee_id (account_fee_id),
  KEY fee_amount_currency_id (fee_amount_currency_id),
  KEY fee_account_trxn_idx (account_trxn_id),
  CONSTRAINT fee_trxn_detail_ibfk_1 FOREIGN KEY (account_fee_id) REFERENCES account_fees (account_fee_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fee_trxn_detail_ibfk_2 FOREIGN KEY (fee_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fee_trxn_detail_ibfk_3 FOREIGN KEY (account_trxn_id) REFERENCES account_trxn (account_trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_trxn_detail DISABLE KEYS */;
INSERT INTO fee_trxn_detail VALUES (1,11,10,2,'10.0000');
INSERT INTO fee_trxn_detail VALUES (2,13,11,2,'10.0000');
INSERT INTO fee_trxn_detail VALUES (3,15,12,2,'10.0000');
/*!40000 ALTER TABLE fee_trxn_detail ENABLE KEYS */;
DROP TABLE IF EXISTS fee_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_type (
  fee_type_id smallint(6) NOT NULL,
  fee_lookup_id smallint(6) DEFAULT NULL,
  flat_or_rate smallint(6) DEFAULT NULL,
  formula varchar(100) DEFAULT NULL,
  PRIMARY KEY (fee_type_id),
  KEY fee_lookup_id (fee_lookup_id),
  CONSTRAINT fee_type_ibfk_1 FOREIGN KEY (fee_lookup_id) REFERENCES lookup_entity (entity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_type DISABLE KEYS */;
INSERT INTO fee_type VALUES (1,1,NULL,NULL);
INSERT INTO fee_type VALUES (2,1,NULL,NULL);
INSERT INTO fee_type VALUES (3,2,NULL,NULL);
INSERT INTO fee_type VALUES (4,3,NULL,NULL);
INSERT INTO fee_type VALUES (5,3,NULL,NULL);
/*!40000 ALTER TABLE fee_type ENABLE KEYS */;
DROP TABLE IF EXISTS fee_update_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fee_update_type (
  fee_update_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (fee_update_type_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT fee_update_type_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fee_update_type DISABLE KEYS */;
INSERT INTO fee_update_type VALUES (1,556);
INSERT INTO fee_update_type VALUES (2,557);
/*!40000 ALTER TABLE fee_update_type ENABLE KEYS */;
DROP TABLE IF EXISTS feelevel;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE feelevel (
  feelevel_id smallint(6) NOT NULL AUTO_INCREMENT,
  fee_id smallint(6) NOT NULL,
  level_id smallint(6) NOT NULL,
  PRIMARY KEY (feelevel_id),
  KEY fee_id (fee_id),
  CONSTRAINT feelevel_ibfk_1 FOREIGN KEY (fee_id) REFERENCES fees (fee_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE feelevel DISABLE KEYS */;
/*!40000 ALTER TABLE feelevel ENABLE KEYS */;
DROP TABLE IF EXISTS fees;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fees (
  fee_id smallint(6) NOT NULL AUTO_INCREMENT,
  global_fee_num varchar(50) DEFAULT NULL,
  fee_name varchar(50) NOT NULL,
  fee_payments_category_type_id smallint(6) DEFAULT NULL,
  office_id smallint(6) NOT NULL,
  glcode_id smallint(6) NOT NULL,
  `status` smallint(6) NOT NULL,
  category_id smallint(6) NOT NULL,
  rate_or_amount decimal(16,5) DEFAULT NULL,
  rate_or_amount_currency_id smallint(6) DEFAULT NULL,
  rate_flat_falg smallint(6) DEFAULT NULL,
  created_date date NOT NULL,
  created_by smallint(6) NOT NULL,
  updated_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  update_flag smallint(6) DEFAULT NULL,
  formula_id smallint(6) DEFAULT NULL,
  default_admin_fee varchar(10) DEFAULT NULL,
  fee_amount decimal(21,4) DEFAULT NULL,
  fee_amount_currency_id smallint(6) DEFAULT NULL,
  rate decimal(16,5) DEFAULT NULL,
  version_no int(11) NOT NULL,
  discriminator varchar(20) DEFAULT NULL,
  PRIMARY KEY (fee_id),
  UNIQUE KEY fee_global_idx (global_fee_num),
  KEY glcode_id (glcode_id),
  KEY category_id (category_id),
  KEY `status` (`status`),
  KEY created_by (created_by),
  KEY updated_by (updated_by),
  KEY formula_id (formula_id),
  KEY rate_or_amount_currency_id (rate_or_amount_currency_id),
  KEY fee_amount_currency_id (fee_amount_currency_id),
  KEY fee_pmnt_catg_idx (fee_payments_category_type_id),
  KEY fee_office_idx (office_id),
  CONSTRAINT fees_ibfk_1 FOREIGN KEY (glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_2 FOREIGN KEY (category_id) REFERENCES category_type (category_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_3 FOREIGN KEY (`status`) REFERENCES fee_status (status_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_4 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_5 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_6 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_7 FOREIGN KEY (formula_id) REFERENCES fee_formula_master (formulaid) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_8 FOREIGN KEY (rate_or_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fees_ibfk_9 FOREIGN KEY (fee_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fees DISABLE KEYS */;
INSERT INTO fees VALUES (1,NULL,'oneTimeFee',NULL,1,50,1,5,NULL,NULL,NULL,'2011-02-22',1,NULL,NULL,0,NULL,NULL,'10.0000',2,NULL,0,'AMOUNT');
INSERT INTO fees VALUES (2,NULL,'disbursementFee',NULL,1,50,1,5,NULL,NULL,NULL,'2011-02-22',1,NULL,NULL,0,NULL,NULL,'10.0000',2,NULL,0,'AMOUNT');
INSERT INTO fees VALUES (3,NULL,'loanWeeklyFee',NULL,1,50,1,5,NULL,NULL,NULL,'2011-03-04',1,NULL,NULL,0,NULL,NULL,'100.0000',2,NULL,0,'AMOUNT');
INSERT INTO fees VALUES (4,NULL,'fixedFeePerAmountAndInterest',NULL,1,50,1,5,NULL,NULL,NULL,'2011-03-04',1,NULL,NULL,0,2,NULL,NULL,NULL,'100.00000',0,'RATE');
INSERT INTO fees VALUES (5,NULL,'fixedFeePerInterest',NULL,1,50,1,5,NULL,NULL,NULL,'2011-03-04',1,NULL,NULL,0,3,NULL,NULL,NULL,'20.00000',0,'RATE');
/*!40000 ALTER TABLE fees ENABLE KEYS */;
DROP TABLE IF EXISTS field_configuration;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE field_configuration (
  field_config_id int(11) NOT NULL AUTO_INCREMENT,
  field_name varchar(100) NOT NULL,
  entity_id smallint(6) NOT NULL,
  mandatory_flag smallint(6) NOT NULL,
  hidden_flag smallint(6) NOT NULL,
  parent_field_config_id int(11) DEFAULT NULL,
  PRIMARY KEY (field_config_id),
  KEY entity_id (entity_id),
  KEY parent_field_config_id (parent_field_config_id),
  CONSTRAINT field_configuration_ibfk_1 FOREIGN KEY (entity_id) REFERENCES entity_master (entity_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT field_configuration_ibfk_2 FOREIGN KEY (parent_field_config_id) REFERENCES field_configuration (field_config_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE field_configuration DISABLE KEYS */;
INSERT INTO field_configuration VALUES (1,'MiddleName',1,0,0,NULL);
INSERT INTO field_configuration VALUES (2,'MiddleName',17,0,0,NULL);
INSERT INTO field_configuration VALUES (3,'SecondLastName',1,0,0,NULL);
INSERT INTO field_configuration VALUES (4,'SecondLastName',17,0,0,NULL);
INSERT INTO field_configuration VALUES (5,'GovernmentId',1,0,0,NULL);
INSERT INTO field_configuration VALUES (6,'GovernmentId',17,0,0,NULL);
INSERT INTO field_configuration VALUES (7,'ExternalId',1,0,0,NULL);
INSERT INTO field_configuration VALUES (8,'ExternalId',12,0,0,NULL);
INSERT INTO field_configuration VALUES (9,'ExternalId',20,0,0,NULL);
INSERT INTO field_configuration VALUES (10,'Ethinicity',1,0,0,NULL);
INSERT INTO field_configuration VALUES (11,'Citizenship',1,0,0,NULL);
INSERT INTO field_configuration VALUES (12,'Handicapped',1,0,0,NULL);
INSERT INTO field_configuration VALUES (13,'BusinessActivities',1,0,0,NULL);
INSERT INTO field_configuration VALUES (14,'EducationLevel',1,0,0,NULL);
INSERT INTO field_configuration VALUES (15,'Photo',1,0,0,NULL);
INSERT INTO field_configuration VALUES (16,'SpouseFatherMiddleName',1,0,0,NULL);
INSERT INTO field_configuration VALUES (17,'SpouseFatherSecondLastName',1,0,0,NULL);
INSERT INTO field_configuration VALUES (18,'Trained',1,0,0,NULL);
INSERT INTO field_configuration VALUES (19,'Trained',12,0,0,NULL);
INSERT INTO field_configuration VALUES (20,'TrainedDate',1,0,0,NULL);
INSERT INTO field_configuration VALUES (21,'TrainedDate',12,0,0,NULL);
INSERT INTO field_configuration VALUES (22,'Address',1,0,0,NULL);
INSERT INTO field_configuration VALUES (23,'Address',12,0,0,NULL);
INSERT INTO field_configuration VALUES (24,'Address',20,0,0,NULL);
INSERT INTO field_configuration VALUES (25,'Address1',1,0,0,22);
INSERT INTO field_configuration VALUES (26,'Address1',12,0,0,23);
INSERT INTO field_configuration VALUES (27,'Address1',20,0,0,24);
INSERT INTO field_configuration VALUES (28,'Address2',1,0,0,22);
INSERT INTO field_configuration VALUES (29,'Address2',12,0,0,23);
INSERT INTO field_configuration VALUES (30,'Address2',20,0,0,24);
INSERT INTO field_configuration VALUES (31,'Address3',1,0,0,22);
INSERT INTO field_configuration VALUES (32,'Address3',12,0,0,23);
INSERT INTO field_configuration VALUES (33,'Address3',20,0,0,24);
INSERT INTO field_configuration VALUES (34,'Address3',15,0,0,NULL);
INSERT INTO field_configuration VALUES (35,'Address3',17,0,0,NULL);
INSERT INTO field_configuration VALUES (36,'City',1,0,0,22);
INSERT INTO field_configuration VALUES (37,'City',12,0,0,23);
INSERT INTO field_configuration VALUES (38,'City',20,0,0,24);
INSERT INTO field_configuration VALUES (39,'State',1,0,0,22);
INSERT INTO field_configuration VALUES (40,'State',12,0,0,23);
INSERT INTO field_configuration VALUES (41,'State',20,0,0,24);
INSERT INTO field_configuration VALUES (42,'State',15,0,0,NULL);
INSERT INTO field_configuration VALUES (43,'State',17,0,0,NULL);
INSERT INTO field_configuration VALUES (44,'Country',1,0,0,22);
INSERT INTO field_configuration VALUES (45,'Country',12,0,0,23);
INSERT INTO field_configuration VALUES (46,'Country',20,0,0,24);
INSERT INTO field_configuration VALUES (47,'Country',15,0,0,NULL);
INSERT INTO field_configuration VALUES (48,'Country',17,0,0,NULL);
INSERT INTO field_configuration VALUES (49,'PostalCode',1,0,0,22);
INSERT INTO field_configuration VALUES (50,'PostalCode',12,0,0,23);
INSERT INTO field_configuration VALUES (51,'PostalCode',20,0,0,24);
INSERT INTO field_configuration VALUES (52,'PostalCode',15,0,0,NULL);
INSERT INTO field_configuration VALUES (53,'PostalCode',17,0,0,NULL);
INSERT INTO field_configuration VALUES (54,'PhoneNumber',1,0,0,NULL);
INSERT INTO field_configuration VALUES (55,'PhoneNumber',12,0,0,NULL);
INSERT INTO field_configuration VALUES (56,'PhoneNumber',20,0,0,NULL);
INSERT INTO field_configuration VALUES (57,'PhoneNumber',17,0,0,NULL);
INSERT INTO field_configuration VALUES (58,'PurposeOfLoan',22,0,0,NULL);
INSERT INTO field_configuration VALUES (59,'CollateralType',22,0,0,NULL);
INSERT INTO field_configuration VALUES (60,'CollateralNotes',22,0,0,NULL);
INSERT INTO field_configuration VALUES (61,'ReceiptId',1,0,0,NULL);
INSERT INTO field_configuration VALUES (62,'ReceiptId',12,0,0,NULL);
INSERT INTO field_configuration VALUES (63,'ReceiptId',20,0,0,NULL);
INSERT INTO field_configuration VALUES (64,'ReceiptId',21,0,0,NULL);
INSERT INTO field_configuration VALUES (65,'ReceiptId',22,0,0,NULL);
INSERT INTO field_configuration VALUES (66,'ReceiptId',23,0,0,NULL);
INSERT INTO field_configuration VALUES (67,'ReceiptDate',1,0,0,NULL);
INSERT INTO field_configuration VALUES (68,'ReceiptDate',12,0,0,NULL);
INSERT INTO field_configuration VALUES (69,'ReceiptDate',20,0,0,NULL);
INSERT INTO field_configuration VALUES (70,'ReceiptDate',21,0,0,NULL);
INSERT INTO field_configuration VALUES (71,'ReceiptDate',22,0,0,NULL);
INSERT INTO field_configuration VALUES (72,'ReceiptDate',23,0,0,NULL);
INSERT INTO field_configuration VALUES (73,'PovertyStatus',1,1,0,NULL);
INSERT INTO field_configuration VALUES (74,'AssignClients',1,0,0,NULL);
INSERT INTO field_configuration VALUES (75,'Address2',15,0,0,NULL);
INSERT INTO field_configuration VALUES (76,'Address2',17,0,0,NULL);
INSERT INTO field_configuration VALUES (77,'Address1',15,0,0,NULL);
INSERT INTO field_configuration VALUES (78,'Address1',17,0,0,NULL);
INSERT INTO field_configuration VALUES (79,'City',15,0,0,NULL);
INSERT INTO field_configuration VALUES (80,'SourceOfFund',22,0,0,NULL);
INSERT INTO field_configuration VALUES (81,'MaritalStatus',1,0,0,NULL);
INSERT INTO field_configuration VALUES (82,'NumberOfChildren',1,0,0,NULL);
INSERT INTO field_configuration VALUES (83,'ExternalId',22,0,0,NULL);
INSERT INTO field_configuration VALUES (84,'SpouseFatherInformation',1,0,0,NULL);
INSERT INTO field_configuration VALUES (85,'FamilyDetails',1,1,0,NULL);
/*!40000 ALTER TABLE field_configuration ENABLE KEYS */;
DROP TABLE IF EXISTS financial_action;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE financial_action (
  fin_action_id smallint(6) NOT NULL,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (fin_action_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT financial_action_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value_locale (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE financial_action DISABLE KEYS */;
INSERT INTO financial_action VALUES (1,198);
INSERT INTO financial_action VALUES (2,199);
INSERT INTO financial_action VALUES (3,200);
INSERT INTO financial_action VALUES (5,201);
INSERT INTO financial_action VALUES (8,202);
INSERT INTO financial_action VALUES (9,203);
INSERT INTO financial_action VALUES (10,204);
INSERT INTO financial_action VALUES (11,205);
INSERT INTO financial_action VALUES (12,206);
INSERT INTO financial_action VALUES (13,207);
INSERT INTO financial_action VALUES (14,208);
INSERT INTO financial_action VALUES (15,209);
INSERT INTO financial_action VALUES (7,215);
INSERT INTO financial_action VALUES (4,229);
INSERT INTO financial_action VALUES (6,361);
INSERT INTO financial_action VALUES (16,363);
INSERT INTO financial_action VALUES (17,365);
INSERT INTO financial_action VALUES (18,367);
INSERT INTO financial_action VALUES (19,368);
INSERT INTO financial_action VALUES (20,369);
INSERT INTO financial_action VALUES (21,370);
INSERT INTO financial_action VALUES (22,550);
INSERT INTO financial_action VALUES (23,609);
/*!40000 ALTER TABLE financial_action ENABLE KEYS */;
DROP TABLE IF EXISTS financial_trxn;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE financial_trxn (
  trxn_id int(11) NOT NULL AUTO_INCREMENT,
  account_trxn_id int(11) NOT NULL,
  related_fin_trxn int(11) DEFAULT NULL,
  currency_id smallint(6) DEFAULT NULL,
  fin_action_id smallint(6) DEFAULT NULL,
  glcode_id smallint(6) NOT NULL,
  posted_amount decimal(21,4) NOT NULL,
  posted_amount_currency_id smallint(6) DEFAULT NULL,
  balance_amount decimal(21,4) NOT NULL,
  balance_amount_currency_id smallint(6) DEFAULT NULL,
  action_date date NOT NULL,
  posted_date date NOT NULL,
  posted_by smallint(6) DEFAULT NULL,
  accounting_updated smallint(6) DEFAULT NULL,
  notes varchar(200) DEFAULT NULL,
  debit_credit_flag smallint(6) NOT NULL,
  PRIMARY KEY (trxn_id),
  KEY account_trxn_id (account_trxn_id),
  KEY posted_amount_currency_id (posted_amount_currency_id),
  KEY balance_amount_currency_id (balance_amount_currency_id),
  KEY currency_id (currency_id),
  KEY related_fin_trxn (related_fin_trxn),
  KEY fin_action_id (fin_action_id),
  KEY posted_by (posted_by),
  KEY glcode_id (glcode_id),
  CONSTRAINT financial_trxn_ibfk_1 FOREIGN KEY (account_trxn_id) REFERENCES account_trxn (account_trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_3 FOREIGN KEY (posted_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_4 FOREIGN KEY (balance_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_5 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_6 FOREIGN KEY (related_fin_trxn) REFERENCES financial_trxn (trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_7 FOREIGN KEY (fin_action_id) REFERENCES financial_action (fin_action_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_8 FOREIGN KEY (posted_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT financial_trxn_ibfk_9 FOREIGN KEY (glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE financial_trxn DISABLE KEYS */;
INSERT INTO financial_trxn VALUES (1,1,NULL,NULL,10,36,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'',1);
INSERT INTO financial_trxn VALUES (2,1,NULL,NULL,10,7,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'',0);
INSERT INTO financial_trxn VALUES (3,2,NULL,NULL,7,14,'1000.0000',2,'1000.0000',2,'2011-02-21','2011-02-21',1,1,'-',0);
INSERT INTO financial_trxn VALUES (4,2,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-21','2011-02-21',1,1,'-',1);
INSERT INTO financial_trxn VALUES (5,3,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'-',1);
INSERT INTO financial_trxn VALUES (6,3,NULL,NULL,7,14,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'-',0);
INSERT INTO financial_trxn VALUES (7,4,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2010-02-22','2010-02-22',1,1,'-',1);
INSERT INTO financial_trxn VALUES (8,4,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2010-02-22','2010-02-22',1,1,'-',0);
INSERT INTO financial_trxn VALUES (9,5,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0);
INSERT INTO financial_trxn VALUES (10,5,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1);
INSERT INTO financial_trxn VALUES (11,6,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1);
INSERT INTO financial_trxn VALUES (12,6,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0);
INSERT INTO financial_trxn VALUES (13,7,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1);
INSERT INTO financial_trxn VALUES (14,7,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0);
INSERT INTO financial_trxn VALUES (15,8,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0);
INSERT INTO financial_trxn VALUES (16,8,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1);
INSERT INTO financial_trxn VALUES (17,9,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0);
INSERT INTO financial_trxn VALUES (18,9,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1);
INSERT INTO financial_trxn VALUES (19,10,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (20,10,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (21,11,NULL,NULL,3,7,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (22,11,NULL,NULL,3,50,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (23,12,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (24,12,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (25,13,NULL,NULL,3,7,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (26,13,NULL,NULL,3,50,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (27,14,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (28,14,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (29,15,NULL,NULL,3,7,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (30,15,NULL,NULL,3,50,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (31,16,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (32,16,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (33,17,NULL,NULL,7,20,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (34,17,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
INSERT INTO financial_trxn VALUES (35,18,NULL,NULL,7,15,'10000.0000',2,'10000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0);
INSERT INTO financial_trxn VALUES (36,18,NULL,NULL,7,7,'10000.0000',2,'10000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
/*!40000 ALTER TABLE financial_trxn ENABLE KEYS */;
DROP TABLE IF EXISTS freq_of_deposits;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE freq_of_deposits (
  freq_of_deposits_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (freq_of_deposits_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT freq_of_deposits_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE freq_of_deposits DISABLE KEYS */;
/*!40000 ALTER TABLE freq_of_deposits ENABLE KEYS */;
DROP TABLE IF EXISTS fund;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fund (
  fund_id smallint(6) NOT NULL AUTO_INCREMENT,
  fund_name varchar(100) DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  fundcode_id smallint(6) NOT NULL,
  PRIMARY KEY (fund_id),
  KEY fundcode_id (fundcode_id),
  CONSTRAINT fund_ibfk_1 FOREIGN KEY (fundcode_id) REFERENCES fund_code (fundcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fund DISABLE KEYS */;
INSERT INTO fund VALUES (1,'Non Donor',0,1);
INSERT INTO fund VALUES (2,'Funding Org A',0,1);
INSERT INTO fund VALUES (3,'Funding Org B',0,1);
INSERT INTO fund VALUES (4,'Funding Org C',0,1);
INSERT INTO fund VALUES (5,'Funding Org D',0,1);
/*!40000 ALTER TABLE fund ENABLE KEYS */;
DROP TABLE IF EXISTS fund_code;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE fund_code (
  fundcode_id smallint(6) NOT NULL AUTO_INCREMENT,
  fundcode_value varchar(50) NOT NULL,
  PRIMARY KEY (fundcode_id)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE fund_code DISABLE KEYS */;
INSERT INTO fund_code VALUES (1,'00');
INSERT INTO fund_code VALUES (2,'01');
INSERT INTO fund_code VALUES (3,'02');
INSERT INTO fund_code VALUES (4,'03');
INSERT INTO fund_code VALUES (5,'04');
/*!40000 ALTER TABLE fund_code ENABLE KEYS */;
DROP TABLE IF EXISTS gl_code;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE gl_code (
  glcode_id smallint(6) NOT NULL AUTO_INCREMENT,
  glcode_value varchar(50) NOT NULL,
  PRIMARY KEY (glcode_id),
  UNIQUE KEY glcode_value_idx (glcode_value)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE gl_code DISABLE KEYS */;
INSERT INTO gl_code VALUES (1,'10000');
INSERT INTO gl_code VALUES (2,'11000');
INSERT INTO gl_code VALUES (3,'11100');
INSERT INTO gl_code VALUES (4,'11101');
INSERT INTO gl_code VALUES (5,'11102');
INSERT INTO gl_code VALUES (6,'11200');
INSERT INTO gl_code VALUES (7,'11201');
INSERT INTO gl_code VALUES (8,'11202');
INSERT INTO gl_code VALUES (9,'13000');
INSERT INTO gl_code VALUES (10,'13100');
INSERT INTO gl_code VALUES (20,'13101');
INSERT INTO gl_code VALUES (21,'13200');
INSERT INTO gl_code VALUES (22,'13201');
INSERT INTO gl_code VALUES (11,'1501');
INSERT INTO gl_code VALUES (12,'1502');
INSERT INTO gl_code VALUES (13,'1503');
INSERT INTO gl_code VALUES (14,'1504');
INSERT INTO gl_code VALUES (15,'1505');
INSERT INTO gl_code VALUES (16,'1506');
INSERT INTO gl_code VALUES (17,'1507');
INSERT INTO gl_code VALUES (18,'1508');
INSERT INTO gl_code VALUES (19,'1509');
INSERT INTO gl_code VALUES (23,'20000');
INSERT INTO gl_code VALUES (24,'22000');
INSERT INTO gl_code VALUES (25,'22100');
INSERT INTO gl_code VALUES (26,'22101');
INSERT INTO gl_code VALUES (27,'23000');
INSERT INTO gl_code VALUES (28,'23100');
INSERT INTO gl_code VALUES (33,'23101');
INSERT INTO gl_code VALUES (34,'24000');
INSERT INTO gl_code VALUES (35,'24100');
INSERT INTO gl_code VALUES (36,'24101');
INSERT INTO gl_code VALUES (37,'30000');
INSERT INTO gl_code VALUES (38,'31000');
INSERT INTO gl_code VALUES (39,'31100');
INSERT INTO gl_code VALUES (41,'31101');
INSERT INTO gl_code VALUES (42,'31102');
INSERT INTO gl_code VALUES (43,'31300');
INSERT INTO gl_code VALUES (50,'31301');
INSERT INTO gl_code VALUES (51,'31401');
INSERT INTO gl_code VALUES (52,'40000');
INSERT INTO gl_code VALUES (53,'41000');
INSERT INTO gl_code VALUES (54,'41100');
INSERT INTO gl_code VALUES (55,'41101');
INSERT INTO gl_code VALUES (56,'41102');
INSERT INTO gl_code VALUES (29,'4601');
INSERT INTO gl_code VALUES (30,'4602');
INSERT INTO gl_code VALUES (31,'4603');
INSERT INTO gl_code VALUES (32,'4606');
INSERT INTO gl_code VALUES (40,'5001');
INSERT INTO gl_code VALUES (44,'5201');
INSERT INTO gl_code VALUES (45,'5202');
INSERT INTO gl_code VALUES (46,'5203');
INSERT INTO gl_code VALUES (47,'5204');
INSERT INTO gl_code VALUES (48,'5205');
INSERT INTO gl_code VALUES (49,'6201');
/*!40000 ALTER TABLE gl_code ENABLE KEYS */;
DROP TABLE IF EXISTS grace_period_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE grace_period_type (
  grace_period_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (grace_period_type_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT grace_period_type_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE grace_period_type DISABLE KEYS */;
INSERT INTO grace_period_type VALUES (1,96);
INSERT INTO grace_period_type VALUES (2,97);
INSERT INTO grace_period_type VALUES (3,98);
/*!40000 ALTER TABLE grace_period_type ENABLE KEYS */;
DROP TABLE IF EXISTS group_loan_counter;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE group_loan_counter (
  group_loan_counter_id int(11) NOT NULL AUTO_INCREMENT,
  group_perf_id int(11) NOT NULL,
  loan_offering_id smallint(6) NOT NULL,
  loan_cycle_counter smallint(6) DEFAULT NULL,
  PRIMARY KEY (group_loan_counter_id),
  KEY group_perf_id (group_perf_id),
  CONSTRAINT group_loan_counter_ibfk_1 FOREIGN KEY (group_perf_id) REFERENCES group_perf_history (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE group_loan_counter DISABLE KEYS */;
INSERT INTO group_loan_counter VALUES (1,1,7,1);
/*!40000 ALTER TABLE group_loan_counter ENABLE KEYS */;
DROP TABLE IF EXISTS group_perf_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE group_perf_history (
  id int(11) NOT NULL AUTO_INCREMENT,
  customer_id int(11) NOT NULL,
  no_of_clients smallint(6) DEFAULT NULL,
  last_group_loan_amnt_disb decimal(21,4) DEFAULT NULL,
  last_group_loan_amnt_disb_currency_id smallint(6) DEFAULT NULL,
  avg_loan_size decimal(21,4) DEFAULT NULL,
  avg_loan_size_currency_id smallint(6) DEFAULT NULL,
  total_outstand_loan_amnt decimal(21,4) DEFAULT NULL,
  total_outstand_loan_amnt_currency_id smallint(6) DEFAULT NULL,
  portfolio_at_risk decimal(21,4) DEFAULT NULL,
  portfolio_at_risk_currency_id smallint(6) DEFAULT NULL,
  total_savings_amnt decimal(21,4) DEFAULT NULL,
  total_savings_amnt_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY customer_id (customer_id),
  CONSTRAINT group_perf_history_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE group_perf_history DISABLE KEYS */;
INSERT INTO group_perf_history VALUES (1,4,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO group_perf_history VALUES (2,9,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO group_perf_history VALUES (3,11,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO group_perf_history VALUES (4,16,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO group_perf_history VALUES (5,20,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO group_perf_history VALUES (6,22,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO group_perf_history VALUES (7,25,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO group_perf_history VALUES (8,32,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO group_perf_history VALUES (9,36,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO group_perf_history VALUES (10,39,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
/*!40000 ALTER TABLE group_perf_history ENABLE KEYS */;
DROP TABLE IF EXISTS holiday;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE holiday (
  holiday_from_date date NOT NULL,
  holiday_thru_date date DEFAULT NULL,
  holiday_name varchar(100) DEFAULT NULL,
  repayment_rule_id smallint(6) NOT NULL,
  holiday_changes_applied_flag smallint(6) DEFAULT '1',
  holiday_id int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (holiday_id),
  KEY repayment_rule_id (repayment_rule_id),
  CONSTRAINT holiday_ibfk_2 FOREIGN KEY (repayment_rule_id) REFERENCES repayment_rule (repayment_rule_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE holiday DISABLE KEYS */;
/*!40000 ALTER TABLE holiday ENABLE KEYS */;
DROP TABLE IF EXISTS imported_transactions_files;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE imported_transactions_files (
  file_name varchar(100) NOT NULL,
  submitted_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  submitted_by smallint(6) NOT NULL,
  PRIMARY KEY (file_name),
  KEY submitted_by (submitted_by),
  CONSTRAINT imported_transactions_files_ibfk_1 FOREIGN KEY (submitted_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE imported_transactions_files DISABLE KEYS */;
/*!40000 ALTER TABLE imported_transactions_files ENABLE KEYS */;
DROP TABLE IF EXISTS inherited_meeting;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE inherited_meeting (
  meeting_id int(11) DEFAULT NULL,
  customer_id int(11) DEFAULT NULL,
  KEY customer_id (customer_id),
  KEY meeting_id (meeting_id),
  CONSTRAINT inherited_meeting_ibfk_1 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT inherited_meeting_ibfk_2 FOREIGN KEY (meeting_id) REFERENCES customer_meeting (customer_meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE inherited_meeting DISABLE KEYS */;
/*!40000 ALTER TABLE inherited_meeting ENABLE KEYS */;
DROP TABLE IF EXISTS insurance_offering;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE insurance_offering (
  prd_offering_id smallint(6) NOT NULL,
  PRIMARY KEY (prd_offering_id),
  CONSTRAINT insurance_offering_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE insurance_offering DISABLE KEYS */;
/*!40000 ALTER TABLE insurance_offering ENABLE KEYS */;
DROP TABLE IF EXISTS interest_calc_rule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE interest_calc_rule (
  interest_calc_rule_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (interest_calc_rule_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT interest_calc_rule_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE interest_calc_rule DISABLE KEYS */;
INSERT INTO interest_calc_rule VALUES (1,88);
INSERT INTO interest_calc_rule VALUES (2,89);
/*!40000 ALTER TABLE interest_calc_rule ENABLE KEYS */;
DROP TABLE IF EXISTS interest_calculation_types;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE interest_calculation_types (
  interest_calculation_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  interest_calculation_lookup_id int(11) NOT NULL,
  description varchar(100) DEFAULT NULL,
  PRIMARY KEY (interest_calculation_type_id),
  KEY interest_calculation_lookup_id (interest_calculation_lookup_id),
  CONSTRAINT interest_calculation_types_ibfk_1 FOREIGN KEY (interest_calculation_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE interest_calculation_types DISABLE KEYS */;
INSERT INTO interest_calculation_types VALUES (1,122,NULL);
INSERT INTO interest_calculation_types VALUES (2,123,NULL);
/*!40000 ALTER TABLE interest_calculation_types ENABLE KEYS */;
DROP TABLE IF EXISTS interest_types;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE interest_types (
  interest_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  category_id smallint(6) NOT NULL,
  descripton varchar(50) DEFAULT NULL,
  PRIMARY KEY (interest_type_id),
  KEY category_id (category_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT interest_types_ibfk_1 FOREIGN KEY (category_id) REFERENCES prd_type (prd_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT interest_types_ibfk_2 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE interest_types DISABLE KEYS */;
INSERT INTO interest_types VALUES (1,79,1,'Flat');
INSERT INTO interest_types VALUES (2,80,1,'Declining');
INSERT INTO interest_types VALUES (4,604,1,'Declining Balance-Equal Principal Installment');
INSERT INTO interest_types VALUES (5,636,1,'InterestTypes-DecliningPrincipalBalance');
/*!40000 ALTER TABLE interest_types ENABLE KEYS */;
DROP TABLE IF EXISTS language;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  lang_id smallint(6) NOT NULL,
  lang_name varchar(100) DEFAULT NULL,
  lang_short_name varchar(10) DEFAULT NULL,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (lang_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT language_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE language DISABLE KEYS */;
INSERT INTO language VALUES (1,'English','EN',189);
INSERT INTO language VALUES (2,'Icelandic','is',599);
INSERT INTO language VALUES (3,'Spanish','es',600);
INSERT INTO language VALUES (4,'French','fr',601);
INSERT INTO language VALUES (5,'Chinese','zh',613);
INSERT INTO language VALUES (6,'Swahili','sw',614);
INSERT INTO language VALUES (7,'Arabic','ar',615);
INSERT INTO language VALUES (8,'Portuguese','pt',616);
INSERT INTO language VALUES (9,'Khmer','km',617);
INSERT INTO language VALUES (10,'Lao','lo',618);
INSERT INTO language VALUES (11,'Hungarian','hu',624);
/*!40000 ALTER TABLE language ENABLE KEYS */;
DROP TABLE IF EXISTS loan_account;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_account (
  account_id int(11) NOT NULL,
  business_activities_id int(11) DEFAULT NULL,
  collateral_type_id int(11) DEFAULT NULL,
  grace_period_type_id smallint(6) NOT NULL,
  group_flag smallint(6) DEFAULT NULL,
  loan_amount decimal(21,4) DEFAULT NULL,
  loan_amount_currency_id smallint(6) DEFAULT NULL,
  loan_balance decimal(21,4) DEFAULT NULL,
  loan_balance_currency_id smallint(6) DEFAULT NULL,
  interest_type_id smallint(6) DEFAULT NULL,
  interest_rate decimal(13,10) DEFAULT NULL,
  fund_id smallint(6) DEFAULT NULL,
  meeting_id int(11) DEFAULT NULL,
  currency_id smallint(6) DEFAULT NULL,
  no_of_installments smallint(6) NOT NULL,
  disbursement_date date DEFAULT NULL,
  collateral_note varchar(500) DEFAULT NULL,
  grace_period_duration smallint(6) DEFAULT NULL,
  interest_at_disb smallint(6) DEFAULT NULL,
  grace_period_penalty smallint(6) DEFAULT NULL,
  prd_offering_id smallint(6) DEFAULT NULL,
  redone smallint(6) NOT NULL,
  parent_account_id int(11) DEFAULT NULL,
  month_rank smallint(6) DEFAULT NULL,
  month_week smallint(6) DEFAULT NULL,
  recur_month smallint(6) DEFAULT NULL,
  PRIMARY KEY (account_id),
  KEY currency_id (currency_id),
  KEY loan_amount_currency_id (loan_amount_currency_id),
  KEY loan_balance_currency_id (loan_balance_currency_id),
  KEY fund_id (fund_id),
  KEY grace_period_type_id (grace_period_type_id),
  KEY interest_type_id (interest_type_id),
  KEY meeting_id (meeting_id),
  KEY fk_loan_col_type_id (collateral_type_id),
  KEY fk_loan_bus_act_id (business_activities_id),
  KEY fk_loan_prd_off_id (prd_offering_id),
  KEY fk_loan_account (parent_account_id),
  CONSTRAINT loan_account_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_account_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_account_ibfk_3 FOREIGN KEY (loan_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_account_ibfk_4 FOREIGN KEY (loan_balance_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_account_ibfk_5 FOREIGN KEY (fund_id) REFERENCES fund (fund_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_account_ibfk_6 FOREIGN KEY (grace_period_type_id) REFERENCES grace_period_type (grace_period_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_account_ibfk_7 FOREIGN KEY (interest_type_id) REFERENCES interest_types (interest_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_account_ibfk_8 FOREIGN KEY (meeting_id) REFERENCES meeting (meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_loan_col_type_id FOREIGN KEY (collateral_type_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_loan_bus_act_id FOREIGN KEY (business_activities_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_loan_prd_off_id FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_loan_acc_id FOREIGN KEY (parent_account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_loan_account FOREIGN KEY (parent_account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_account DISABLE KEYS */;
INSERT INTO loan_account VALUES (10,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'0.0000000000',NULL,15,NULL,10,'2011-02-21',NULL,0,0,0,5,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (11,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'0.0000000000',NULL,16,NULL,10,'2011-02-25',NULL,0,0,0,6,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (12,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,17,NULL,10,'2010-02-22',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (15,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'0.0000000000',NULL,18,NULL,10,'2011-02-18',NULL,0,0,0,5,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (20,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,23,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (25,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,32,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (26,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,33,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (27,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,34,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (28,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,35,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (29,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,36,NULL,5,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (35,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,40,NULL,10,'2020-01-03',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (37,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'24.0000000000',NULL,43,NULL,4,'2011-02-28',NULL,0,0,0,11,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (38,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,44,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (39,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,45,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (40,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,48,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (41,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,49,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (42,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'24.0000000000',NULL,50,NULL,4,'2011-02-28',NULL,0,0,0,7,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (43,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,52,NULL,10,'2011-02-28',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (44,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,53,NULL,10,'2011-02-28',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (45,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,54,NULL,10,'2011-02-28',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (46,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,55,NULL,10,'2011-02-28',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (47,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,56,NULL,10,'2011-02-28',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (50,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,60,NULL,10,'2011-03-04',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (52,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,62,NULL,10,'2011-03-04',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (53,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,63,NULL,10,'2011-03-04',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (54,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,64,NULL,10,'2011-03-04',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
INSERT INTO loan_account VALUES (55,NULL,NULL,1,NULL,'10000.0000',2,'10000.0000',2,1,'24.0000000000',NULL,65,NULL,10,'2011-03-04',NULL,0,0,0,12,0,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE loan_account ENABLE KEYS */;
DROP TABLE IF EXISTS loan_activity_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_activity_details (
  id int(11) NOT NULL AUTO_INCREMENT,
  created_by smallint(6) NOT NULL,
  account_id int(11) NOT NULL,
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  comments varchar(100) NOT NULL,
  principal_amount decimal(21,4) DEFAULT NULL,
  principal_amount_currency_id smallint(6) DEFAULT NULL,
  interest_amount decimal(21,4) DEFAULT NULL,
  interest_amount_currency_id smallint(6) DEFAULT NULL,
  penalty_amount decimal(21,4) DEFAULT NULL,
  penalty_amount_currency_id smallint(6) DEFAULT NULL,
  fee_amount decimal(21,4) DEFAULT NULL,
  fee_amount_currency_id smallint(6) DEFAULT NULL,
  balance_principal_amount decimal(21,4) DEFAULT NULL,
  balance_principal_amount_currency_id smallint(6) DEFAULT NULL,
  balance_interest_amount decimal(21,4) DEFAULT NULL,
  balance_interest_amount_currency_id smallint(6) DEFAULT NULL,
  balance_penalty_amount decimal(21,4) DEFAULT NULL,
  balance_penalty_amount_currency_id smallint(6) DEFAULT NULL,
  balance_fee_amount decimal(21,4) DEFAULT NULL,
  balance_fee_amount_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY created_by (created_by),
  KEY account_id (account_id),
  KEY principal_amount_currency_id (principal_amount_currency_id),
  KEY interest_amount_currency_id (interest_amount_currency_id),
  KEY fee_amount_currency_id (fee_amount_currency_id),
  KEY penalty_amount_currency_id (penalty_amount_currency_id),
  KEY balance_principal_amount_currency_id (balance_principal_amount_currency_id),
  KEY balance_interest_amount_currency_id (balance_interest_amount_currency_id),
  KEY balance_penalty_amount_currency_id (balance_penalty_amount_currency_id),
  KEY balance_fee_amount_currency_id (balance_fee_amount_currency_id),
  CONSTRAINT loan_activity_details_ibfk_1 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_2 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_3 FOREIGN KEY (principal_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_4 FOREIGN KEY (interest_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_5 FOREIGN KEY (fee_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_6 FOREIGN KEY (penalty_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_7 FOREIGN KEY (balance_principal_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_8 FOREIGN KEY (balance_interest_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_9 FOREIGN KEY (balance_penalty_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_activity_details_ibfk_10 FOREIGN KEY (balance_fee_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_activity_details DISABLE KEYS */;
INSERT INTO loan_activity_details VALUES (1,1,10,'2011-02-20 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (2,1,15,'2011-02-17 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (3,1,12,'2010-02-21 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (4,1,12,'2010-02-22 05:16:04','Misc fees applied','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2);
INSERT INTO loan_activity_details VALUES (5,1,25,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (6,1,25,'2010-10-11 07:41:46','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2);
INSERT INTO loan_activity_details VALUES (7,1,26,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (8,1,26,'2010-10-11 07:43:21','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2);
INSERT INTO loan_activity_details VALUES (9,1,27,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (10,1,27,'2010-10-11 07:44:40','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2);
INSERT INTO loan_activity_details VALUES (11,1,28,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (12,1,28,'2010-10-11 07:46:20','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2);
INSERT INTO loan_activity_details VALUES (13,1,29,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'999.9000',2,'13.9000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (14,1,29,'2010-10-11 07:47:31','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'500.0000',2,'1000.0000',2,'14.5096',2,'0.0000',2,'500.0000',2);
INSERT INTO loan_activity_details VALUES (15,1,37,'2011-02-27 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'19.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (16,1,37,'2011-02-28 07:41:02','oneTimeFee applied','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'1000.0000',2,'19.0000',2,'0.0000',2,'10.0000',2);
INSERT INTO loan_activity_details VALUES (17,1,37,'2011-02-28 07:41:09','Misc penalty applied','0.0000',2,'0.0000',2,'5.0000',2,'0.0000',2,'1000.0000',2,'19.0000',2,'5.0000',2,'10.0000',2);
INSERT INTO loan_activity_details VALUES (18,1,39,'2011-02-27 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2);
INSERT INTO loan_activity_details VALUES (19,1,39,'2011-02-27 18:30:00','Payment rcvd.','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (20,1,40,'2011-02-27 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2);
INSERT INTO loan_activity_details VALUES (21,1,40,'2011-02-27 18:30:00','Payment rcvd.','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (22,1,41,'2011-02-27 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2);
INSERT INTO loan_activity_details VALUES (23,1,41,'2011-02-27 18:30:00','Payment rcvd.','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_activity_details VALUES (24,1,42,'2011-02-27 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'19.0000',2,'0.0000',2,'10.0000',2);
INSERT INTO loan_activity_details VALUES (25,1,47,'2011-02-27 18:30:00','Loan Disbursal','10000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2);
/*!40000 ALTER TABLE loan_activity_details ENABLE KEYS */;
DROP TABLE IF EXISTS loan_amount_from_last_loan;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_amount_from_last_loan (
  loan_amount_from_last_loan_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  start_range decimal(21,4) NOT NULL,
  end_range decimal(21,4) NOT NULL,
  min_loan_amount decimal(21,4) NOT NULL,
  max_loan_amnt decimal(21,4) NOT NULL,
  default_loan_amount decimal(21,4) NOT NULL,
  PRIMARY KEY (loan_amount_from_last_loan_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT loan_amount_from_last_loan_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES loan_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_amount_from_last_loan DISABLE KEYS */;
/*!40000 ALTER TABLE loan_amount_from_last_loan ENABLE KEYS */;
DROP TABLE IF EXISTS loan_amount_from_loan_cycle;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_amount_from_loan_cycle (
  loan_amount_from_loan_cycle_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  min_loan_amount decimal(21,4) NOT NULL,
  max_loan_amnt decimal(21,4) NOT NULL,
  default_loan_amount decimal(21,4) NOT NULL,
  range_index smallint(6) NOT NULL,
  PRIMARY KEY (loan_amount_from_loan_cycle_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT loan_amount_from_loan_cycle_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES loan_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_amount_from_loan_cycle DISABLE KEYS */;
/*!40000 ALTER TABLE loan_amount_from_loan_cycle ENABLE KEYS */;
DROP TABLE IF EXISTS loan_amount_same_for_all_loan;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_amount_same_for_all_loan (
  loan_amount_same_for_all_loan_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  min_loan_amount decimal(21,4) NOT NULL,
  max_loan_amnt decimal(21,4) NOT NULL,
  default_loan_amount decimal(21,4) NOT NULL,
  PRIMARY KEY (loan_amount_same_for_all_loan_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT loan_amount_same_for_all_loan_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES loan_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_amount_same_for_all_loan DISABLE KEYS */;
INSERT INTO loan_amount_same_for_all_loan VALUES (23,13,'1000.0000','10000.0000','10000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (24,5,'1000.0000','10000.0000','1000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (25,16,'1.0000','100000.0000','10000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (27,6,'1000.0000','10000.0000','1000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (28,11,'100.0000','10000000.0000','1000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (29,3,'1000.0000','100000.0000','100000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (30,4,'1000.0000','100000.0000','100000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (31,10,'1000.0000','10000.0000','10000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (32,12,'1000.0000','10000.0000','10000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (34,2,'1000.0000','100000.0000','100000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (35,17,'1.0000','100000.0000','10000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (36,7,'100.0000','1000000.0000','1000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (37,9,'100.0000','10000000.0000','1000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (38,14,'100.0000','190000.0000','1000.0000');
INSERT INTO loan_amount_same_for_all_loan VALUES (39,15,'1.0000','10000.0000','10000.0000');
/*!40000 ALTER TABLE loan_amount_same_for_all_loan ENABLE KEYS */;
DROP TABLE IF EXISTS loan_arrears_aging;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_arrears_aging (
  id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  customer_id int(11) NOT NULL,
  customer_name varchar(200) DEFAULT NULL,
  parent_customer_id int(11) DEFAULT NULL,
  office_id smallint(6) NOT NULL,
  days_in_arrears smallint(6) NOT NULL,
  overdue_principal decimal(21,4) DEFAULT NULL,
  overdue_principal_currency_id smallint(6) DEFAULT NULL,
  overdue_interest decimal(21,4) DEFAULT NULL,
  overdue_interest_currency_id smallint(6) DEFAULT NULL,
  overdue_balance decimal(21,4) DEFAULT NULL,
  overdue_balance_currency_id smallint(6) DEFAULT NULL,
  unpaid_principal decimal(21,4) DEFAULT NULL,
  unpaid_principal_currency_id smallint(6) DEFAULT NULL,
  unpaid_interest decimal(21,4) DEFAULT NULL,
  unpaid_interest_currency_id smallint(6) DEFAULT NULL,
  unpaid_balance decimal(21,4) DEFAULT NULL,
  unpaid_balance_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY account_id (account_id),
  KEY customer_id (customer_id),
  KEY parent_customer_id (parent_customer_id),
  KEY office_id (office_id),
  KEY overdue_principal_currency_id (overdue_principal_currency_id),
  KEY overdue_interest_currency_id (overdue_interest_currency_id),
  KEY overdue_balance_currency_id (overdue_balance_currency_id),
  KEY unpaid_principal_currency_id (unpaid_principal_currency_id),
  KEY unpaid_interest_currency_id (unpaid_interest_currency_id),
  KEY unpaid_balance_currency_id (unpaid_balance_currency_id),
  CONSTRAINT loan_arrears_aging_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_2 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_3 FOREIGN KEY (parent_customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_4 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_5 FOREIGN KEY (overdue_principal_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_6 FOREIGN KEY (overdue_interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_7 FOREIGN KEY (overdue_balance_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_8 FOREIGN KEY (unpaid_principal_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_9 FOREIGN KEY (unpaid_interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_arrears_aging_ibfk_10 FOREIGN KEY (unpaid_balance_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_arrears_aging DISABLE KEYS */;
/*!40000 ALTER TABLE loan_arrears_aging ENABLE KEYS */;
DROP TABLE IF EXISTS loan_cash_flow;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_cash_flow (
  loan_id int(11) NOT NULL,
  cash_flow_id int(11) NOT NULL,
  KEY loan_id (loan_id),
  KEY cash_flow_id (cash_flow_id),
  CONSTRAINT loan_cash_flow_ibfk_1 FOREIGN KEY (loan_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_cash_flow_ibfk_2 FOREIGN KEY (cash_flow_id) REFERENCES cash_flow (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_cash_flow DISABLE KEYS */;
/*!40000 ALTER TABLE loan_cash_flow ENABLE KEYS */;
DROP TABLE IF EXISTS loan_counter;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_counter (
  loan_counter_id int(11) NOT NULL AUTO_INCREMENT,
  client_perf_id int(11) NOT NULL,
  loan_offering_id smallint(6) NOT NULL,
  loan_cycle_counter smallint(6) DEFAULT NULL,
  PRIMARY KEY (loan_counter_id),
  KEY loan_counter_client_perf_idx (client_perf_id)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_counter DISABLE KEYS */;
INSERT INTO loan_counter VALUES (1,3,5,1);
INSERT INTO loan_counter VALUES (2,7,5,1);
INSERT INTO loan_counter VALUES (3,4,2,1);
INSERT INTO loan_counter VALUES (4,12,9,5);
INSERT INTO loan_counter VALUES (5,14,11,1);
INSERT INTO loan_counter VALUES (6,14,2,3);
INSERT INTO loan_counter VALUES (7,14,12,1);
/*!40000 ALTER TABLE loan_counter ENABLE KEYS */;
DROP TABLE IF EXISTS loan_fee_schedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_fee_schedule (
  account_fees_detail_id int(11) NOT NULL AUTO_INCREMENT,
  id int(11) NOT NULL,
  installment_id int(11) NOT NULL,
  fee_id smallint(6) NOT NULL,
  account_fee_id int(11) NOT NULL,
  amount decimal(21,4) DEFAULT NULL,
  amount_currency_id smallint(6) DEFAULT NULL,
  amount_paid decimal(21,4) NOT NULL,
  amount_paid_currency_id smallint(6) NOT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (account_fees_detail_id),
  KEY id (id),
  KEY amount_currency_id (amount_currency_id),
  KEY amount_paid_currency_id (amount_paid_currency_id),
  KEY fee_id (fee_id),
  KEY account_fee_id (account_fee_id),
  CONSTRAINT loan_fee_schedule_ibfk_1 FOREIGN KEY (id) REFERENCES loan_schedule (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_fee_schedule_ibfk_2 FOREIGN KEY (amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_fee_schedule_ibfk_3 FOREIGN KEY (amount_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_fee_schedule_ibfk_4 FOREIGN KEY (fee_id) REFERENCES fees (fee_id),
  CONSTRAINT loan_fee_schedule_ibfk_5 FOREIGN KEY (account_fee_id) REFERENCES account_fees (account_fee_id)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_fee_schedule DISABLE KEYS */;
INSERT INTO loan_fee_schedule VALUES (1,75,1,3,2,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (2,76,2,3,2,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (3,77,3,3,2,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (4,78,4,3,2,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (5,83,1,3,3,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (6,84,2,3,3,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (7,85,3,3,3,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (8,86,4,3,3,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (9,91,1,3,4,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (10,92,2,3,4,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (11,93,3,3,4,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (12,94,4,3,4,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (13,99,1,3,5,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (14,100,2,3,5,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (15,101,3,3,5,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (16,102,4,3,5,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (17,108,1,3,6,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (18,109,2,3,6,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (19,110,3,3,6,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (20,111,4,3,6,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (21,112,5,3,6,'100.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (22,123,1,1,8,'10.0000',2,'0.0000',2,0);
INSERT INTO loan_fee_schedule VALUES (24,171,1,1,13,'10.0000',2,'0.0000',2,0);
/*!40000 ALTER TABLE loan_fee_schedule ENABLE KEYS */;
DROP TABLE IF EXISTS loan_offering;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_offering (
  prd_offering_id smallint(6) NOT NULL,
  interest_type_id smallint(6) NOT NULL,
  interest_calc_rule_id smallint(6) DEFAULT NULL,
  penalty_id smallint(6) DEFAULT NULL,
  min_loan_amount decimal(21,4) DEFAULT NULL,
  min_loan_amount_currency_id smallint(6) DEFAULT NULL,
  max_loan_amnt decimal(21,4) DEFAULT NULL,
  max_loan_amnt_currency_id smallint(6) DEFAULT NULL,
  default_loan_amount decimal(21,4) DEFAULT NULL,
  default_loan_amount_currency_id smallint(6) DEFAULT NULL,
  graceperiod_type_id smallint(6) DEFAULT NULL,
  max_interest_rate decimal(13,10) NOT NULL,
  min_interest_rate decimal(13,10) NOT NULL,
  def_interest_rate decimal(13,10) NOT NULL,
  max_no_installments smallint(6) DEFAULT NULL,
  min_no_installments smallint(6) DEFAULT NULL,
  def_no_installments smallint(6) DEFAULT NULL,
  penalty_grace smallint(6) DEFAULT NULL,
  loan_counter_flag smallint(6) DEFAULT NULL,
  int_ded_disbursement_flag smallint(6) NOT NULL,
  prin_due_last_inst_flag smallint(6) NOT NULL,
  penalty_rate decimal(13,10) DEFAULT NULL,
  grace_period_duration smallint(6) DEFAULT NULL,
  principal_glcode_id smallint(6) NOT NULL,
  interest_glcode_id smallint(6) NOT NULL,
  penalties_glcode_id smallint(6) DEFAULT NULL,
  interest_waiver_flag smallint(6) DEFAULT '0',
  variable_installment_flag smallint(6) DEFAULT '0',
  variable_installment_details_id smallint(6) DEFAULT NULL,
  cash_flow_comparison_flag smallint(6) DEFAULT '0',
  cash_flow_detail_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (prd_offering_id),
  KEY principal_glcode_id (principal_glcode_id),
  KEY interest_glcode_id (interest_glcode_id),
  KEY loan_offering_penalty_glcode (penalties_glcode_id),
  KEY graceperiod_type_id (graceperiod_type_id),
  KEY loan_offering_penalty (penalty_id),
  KEY loan_offering_interest_calc_rule (interest_calc_rule_id),
  KEY interest_type_id (interest_type_id),
  KEY min_loan_amount_currency_id (min_loan_amount_currency_id),
  KEY max_loan_amnt_currency_id (max_loan_amnt_currency_id),
  KEY default_loan_amount_currency_id (default_loan_amount_currency_id),
  KEY variable_installment_details_id (variable_installment_details_id),
  KEY cash_flow_detail_id (cash_flow_detail_id),
  CONSTRAINT loan_offering_ibfk_1 FOREIGN KEY (principal_glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_2 FOREIGN KEY (interest_glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_penalty_glcode FOREIGN KEY (penalties_glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_3 FOREIGN KEY (graceperiod_type_id) REFERENCES grace_period_type (grace_period_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_4 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_penalty FOREIGN KEY (penalty_id) REFERENCES penalty (penalty_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_interest_calc_rule FOREIGN KEY (interest_calc_rule_id) REFERENCES interest_calc_rule (interest_calc_rule_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_5 FOREIGN KEY (interest_type_id) REFERENCES interest_types (interest_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_6 FOREIGN KEY (min_loan_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_7 FOREIGN KEY (max_loan_amnt_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_8 FOREIGN KEY (default_loan_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_9 FOREIGN KEY (variable_installment_details_id) REFERENCES variable_installment_details (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_ibfk_10 FOREIGN KEY (cash_flow_detail_id) REFERENCES cash_flow_detail (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_offering DISABLE KEYS */;
INSERT INTO loan_offering VALUES (2,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,20,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (3,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (4,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,12,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (5,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'0.0000000000','0.0000000000','0.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,14,42,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (6,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'0.0000000000','0.0000000000','0.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,14,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (7,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'99.0000000000','1.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,20,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (9,5,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'30.0000000000','10.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,16,42,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (10,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (11,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'99.0000000000','1.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,16,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (12,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,1,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (13,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,1,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (14,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'30.0000000000','10.0000000000','20.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,15,40,NULL,0,1,2,0,NULL);
INSERT INTO loan_offering VALUES (15,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,'100.0000000000','1.0000000000','10.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,10,20,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (16,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'10.0000000000','0.0000000000','0.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,20,40,NULL,0,0,NULL,0,NULL);
INSERT INTO loan_offering VALUES (17,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'100.0000000000','1.0000000000','10.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,20,40,NULL,0,0,NULL,0,NULL);
/*!40000 ALTER TABLE loan_offering ENABLE KEYS */;
DROP TABLE IF EXISTS loan_offering_fund;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_offering_fund (
  loan_offering_fund_id smallint(6) NOT NULL AUTO_INCREMENT,
  fund_id smallint(6) NOT NULL,
  prd_offering_id smallint(6) NOT NULL,
  PRIMARY KEY (loan_offering_fund_id),
  KEY prd_offering_id (prd_offering_id),
  KEY fund_id (fund_id),
  CONSTRAINT loan_offering_fund_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES loan_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_offering_fund_ibfk_2 FOREIGN KEY (fund_id) REFERENCES fund (fund_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_offering_fund DISABLE KEYS */;
INSERT INTO loan_offering_fund VALUES (2,2,13);
INSERT INTO loan_offering_fund VALUES (3,2,5);
INSERT INTO loan_offering_fund VALUES (4,2,16);
INSERT INTO loan_offering_fund VALUES (6,2,6);
INSERT INTO loan_offering_fund VALUES (7,2,11);
INSERT INTO loan_offering_fund VALUES (8,2,3);
INSERT INTO loan_offering_fund VALUES (9,2,4);
INSERT INTO loan_offering_fund VALUES (10,2,10);
INSERT INTO loan_offering_fund VALUES (11,2,12);
INSERT INTO loan_offering_fund VALUES (13,2,2);
INSERT INTO loan_offering_fund VALUES (14,2,17);
INSERT INTO loan_offering_fund VALUES (15,2,7);
INSERT INTO loan_offering_fund VALUES (16,2,9);
INSERT INTO loan_offering_fund VALUES (17,2,14);
INSERT INTO loan_offering_fund VALUES (18,2,15);
/*!40000 ALTER TABLE loan_offering_fund ENABLE KEYS */;
DROP TABLE IF EXISTS loan_penalty;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_penalty (
  loan_penalty_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) DEFAULT NULL,
  penalty_id smallint(6) NOT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  penalty_type varchar(200) DEFAULT NULL,
  penalty_rate decimal(13,10) DEFAULT NULL,
  PRIMARY KEY (loan_penalty_id),
  KEY account_id (account_id),
  KEY penalty_id (penalty_id),
  CONSTRAINT loan_penalty_ibfk_1 FOREIGN KEY (account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_penalty_ibfk_2 FOREIGN KEY (penalty_id) REFERENCES penalty (penalty_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_penalty DISABLE KEYS */;
/*!40000 ALTER TABLE loan_penalty ENABLE KEYS */;
DROP TABLE IF EXISTS loan_perf_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_perf_history (
  id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  no_of_payments smallint(6) DEFAULT NULL,
  no_of_missed_payments smallint(6) DEFAULT NULL,
  days_in_arrears smallint(6) DEFAULT NULL,
  loan_maturity_date date DEFAULT NULL,
  PRIMARY KEY (id),
  KEY account_id (account_id),
  CONSTRAINT loan_perf_history_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_perf_history DISABLE KEYS */;
INSERT INTO loan_perf_history VALUES (1,10,0,NULL,NULL,'2011-04-29');
INSERT INTO loan_perf_history VALUES (2,11,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (3,12,0,NULL,NULL,'2010-04-30');
INSERT INTO loan_perf_history VALUES (4,15,0,NULL,NULL,'2011-04-29');
INSERT INTO loan_perf_history VALUES (5,20,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (6,25,0,NULL,NULL,'2010-11-09');
INSERT INTO loan_perf_history VALUES (7,26,0,NULL,NULL,'2010-11-09');
INSERT INTO loan_perf_history VALUES (8,27,0,NULL,NULL,'2010-11-09');
INSERT INTO loan_perf_history VALUES (9,28,0,NULL,NULL,'2010-11-09');
INSERT INTO loan_perf_history VALUES (10,29,0,NULL,NULL,'2010-11-16');
INSERT INTO loan_perf_history VALUES (11,35,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (12,37,0,NULL,NULL,'2011-03-28');
INSERT INTO loan_perf_history VALUES (13,38,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (14,39,0,NULL,NULL,'2011-05-09');
INSERT INTO loan_perf_history VALUES (15,40,0,NULL,NULL,'2011-05-09');
INSERT INTO loan_perf_history VALUES (16,41,0,NULL,NULL,'2011-05-09');
INSERT INTO loan_perf_history VALUES (17,42,0,NULL,NULL,'2011-03-25');
INSERT INTO loan_perf_history VALUES (18,43,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (19,44,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (20,45,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (21,46,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (22,47,0,NULL,NULL,'2011-05-09');
INSERT INTO loan_perf_history VALUES (23,50,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (24,52,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (25,53,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (26,54,0,NULL,NULL,NULL);
INSERT INTO loan_perf_history VALUES (27,55,0,NULL,NULL,NULL);
/*!40000 ALTER TABLE loan_perf_history ENABLE KEYS */;
DROP TABLE IF EXISTS loan_schedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_schedule (
  id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  customer_id int(11) NOT NULL,
  currency_id smallint(6) DEFAULT NULL,
  action_date date DEFAULT NULL,
  principal decimal(21,4) NOT NULL,
  principal_currency_id smallint(6) DEFAULT NULL,
  interest decimal(21,4) NOT NULL,
  interest_currency_id smallint(6) DEFAULT NULL,
  penalty decimal(21,4) NOT NULL,
  penalty_currency_id smallint(6) DEFAULT NULL,
  misc_fees decimal(21,4) DEFAULT NULL,
  misc_fees_currency_id smallint(6) DEFAULT NULL,
  misc_fees_paid decimal(21,4) DEFAULT NULL,
  misc_fees_paid_currency_id smallint(6) DEFAULT NULL,
  misc_penalty decimal(21,4) DEFAULT NULL,
  misc_penalty_currency_id smallint(6) DEFAULT NULL,
  misc_penalty_paid decimal(21,4) DEFAULT NULL,
  misc_penalty_paid_currency_id smallint(6) DEFAULT NULL,
  principal_paid decimal(21,4) DEFAULT NULL,
  principal_paid_currency_id smallint(6) DEFAULT NULL,
  interest_paid decimal(21,4) DEFAULT NULL,
  interest_paid_currency_id smallint(6) DEFAULT NULL,
  penalty_paid decimal(21,4) DEFAULT NULL,
  penalty_paid_currency_id smallint(6) DEFAULT NULL,
  payment_status smallint(6) NOT NULL,
  installment_id smallint(6) NOT NULL,
  payment_date date DEFAULT NULL,
  parent_flag smallint(6) DEFAULT NULL,
  version_no int(11) NOT NULL,
  extra_interest decimal(21,4) DEFAULT NULL,
  extra_interest_currency_id smallint(6) DEFAULT NULL,
  extra_interest_paid decimal(21,4) DEFAULT NULL,
  extra_interest_paid_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY account_id (account_id),
  KEY currency_id (currency_id),
  KEY principal_currency_id (principal_currency_id),
  KEY interest_currency_id (interest_currency_id),
  KEY penalty_currency_id (penalty_currency_id),
  KEY misc_fees_currency_id (misc_fees_currency_id),
  KEY misc_fees_paid_currency_id (misc_fees_paid_currency_id),
  KEY misc_penalty_currency_id (misc_penalty_currency_id),
  KEY principal_paid_currency_id (principal_paid_currency_id),
  KEY interest_paid_currency_id (interest_paid_currency_id),
  KEY penalty_paid_currency_id (penalty_paid_currency_id),
  KEY misc_penalty_paid_currency_id (misc_penalty_paid_currency_id),
  KEY customer_id (customer_id),
  KEY extra_interest_currency_id (extra_interest_currency_id),
  KEY extra_interest_paid_currency_id (extra_interest_paid_currency_id),
  CONSTRAINT loan_schedule_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_3 FOREIGN KEY (principal_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_4 FOREIGN KEY (interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_5 FOREIGN KEY (penalty_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_6 FOREIGN KEY (misc_fees_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_7 FOREIGN KEY (misc_fees_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_8 FOREIGN KEY (misc_penalty_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_9 FOREIGN KEY (principal_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_10 FOREIGN KEY (interest_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_11 FOREIGN KEY (penalty_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_12 FOREIGN KEY (misc_penalty_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_13 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_14 FOREIGN KEY (extra_interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_schedule_ibfk_15 FOREIGN KEY (extra_interest_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=275 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_schedule DISABLE KEYS */;
INSERT INTO loan_schedule VALUES (11,11,4,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (12,11,4,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (13,11,4,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (14,11,4,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (15,11,4,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (16,11,4,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (17,11,4,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (18,11,4,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (19,11,4,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (20,11,4,NULL,'2011-05-06','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (31,10,5,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (32,10,5,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (33,10,5,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (34,10,5,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (35,10,5,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (36,10,5,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (37,10,5,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (38,10,5,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (39,10,5,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (40,10,5,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (41,15,10,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (42,15,10,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (43,15,10,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (44,15,10,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (45,15,10,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (46,15,10,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (47,15,10,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (48,15,10,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (49,15,10,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (50,15,10,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (51,12,6,NULL,'2010-02-26','9999.7000',2,'460.3000',2,'0.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (52,12,6,NULL,'2010-03-05','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (53,12,6,NULL,'2010-03-12','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (54,12,6,NULL,'2010-03-19','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (55,12,6,NULL,'2010-03-26','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (56,12,6,NULL,'2010-04-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (57,12,6,NULL,'2010-04-09','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (58,12,6,NULL,'2010-04-16','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (59,12,6,NULL,'2010-04-23','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (60,12,6,NULL,'2010-04-30','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (61,20,3,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (62,20,3,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (63,20,3,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (64,20,3,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (65,20,3,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (66,20,3,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (67,20,3,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (68,20,3,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (69,20,3,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (70,20,3,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (75,25,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (76,25,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (77,25,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (78,25,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (83,26,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (84,26,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (85,26,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (86,26,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (91,27,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (92,27,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (93,27,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (94,27,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (99,28,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (100,28,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (101,28,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (102,28,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (108,29,18,NULL,'2010-10-19','197.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (109,29,18,NULL,'2010-10-26','199.3000',2,'3.7000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (110,29,18,NULL,'2010-11-02','200.2000',2,'2.8000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (111,29,18,NULL,'2010-11-09','201.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (112,29,18,NULL,'2010-11-16','201.7000',2,'1.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,2,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (113,35,23,NULL,'2020-01-10','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (114,35,23,NULL,'2020-01-17','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (115,35,23,NULL,'2020-01-24','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (116,35,23,NULL,'2020-01-31','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (117,35,23,NULL,'2020-02-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (118,35,23,NULL,'2020-02-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (119,35,23,NULL,'2020-02-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (120,35,23,NULL,'2020-02-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (121,35,23,NULL,'2020-03-06','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (122,35,23,NULL,'2020-03-13','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (123,37,24,NULL,'2011-03-07','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'5.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,3,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (124,37,24,NULL,'2011-03-14','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,3,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (125,37,24,NULL,'2011-03-21','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,3,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (126,37,24,NULL,'2011-03-28','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,3,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (127,38,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (128,38,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (129,38,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (130,38,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (131,38,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (132,38,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (133,38,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (134,38,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (135,38,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (136,38,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (137,39,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (138,39,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (139,39,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (140,39,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (141,39,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (142,39,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (143,39,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (144,39,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (145,39,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (146,39,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (147,40,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (148,40,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (149,40,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (150,40,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (151,40,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (152,40,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (153,40,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (154,40,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (155,40,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (156,40,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (157,41,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (158,41,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (159,41,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (160,41,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (161,41,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (162,41,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (163,41,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (164,41,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (165,41,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (166,41,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (171,42,4,NULL,'2011-03-04','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (172,42,4,NULL,'2011-03-11','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (173,42,4,NULL,'2011-03-18','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (174,42,4,NULL,'2011-03-25','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (175,43,24,NULL,'2011-03-07','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (176,43,24,NULL,'2011-03-14','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (177,43,24,NULL,'2011-03-21','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (178,43,24,NULL,'2011-03-28','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (179,43,24,NULL,'2011-04-04','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (180,43,24,NULL,'2011-04-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (181,43,24,NULL,'2011-04-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (182,43,24,NULL,'2011-04-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (183,43,24,NULL,'2011-05-02','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (184,43,24,NULL,'2011-05-09','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (185,44,24,NULL,'2011-03-07','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (186,44,24,NULL,'2011-03-14','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (187,44,24,NULL,'2011-03-21','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (188,44,24,NULL,'2011-03-28','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (189,44,24,NULL,'2011-04-04','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (190,44,24,NULL,'2011-04-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (191,44,24,NULL,'2011-04-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (192,44,24,NULL,'2011-04-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (193,44,24,NULL,'2011-05-02','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (194,44,24,NULL,'2011-05-09','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (195,45,24,NULL,'2011-03-07','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (196,45,24,NULL,'2011-03-14','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (197,45,24,NULL,'2011-03-21','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (198,45,24,NULL,'2011-03-28','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (199,45,24,NULL,'2011-04-04','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (200,45,24,NULL,'2011-04-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (201,45,24,NULL,'2011-04-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (202,45,24,NULL,'2011-04-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (203,45,24,NULL,'2011-05-02','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (204,45,24,NULL,'2011-05-09','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (205,46,24,NULL,'2011-03-07','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (206,46,24,NULL,'2011-03-14','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (207,46,24,NULL,'2011-03-21','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (208,46,24,NULL,'2011-03-28','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (209,46,24,NULL,'2011-04-04','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (210,46,24,NULL,'2011-04-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (211,46,24,NULL,'2011-04-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (212,46,24,NULL,'2011-04-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (213,46,24,NULL,'2011-05-02','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (214,46,24,NULL,'2011-05-09','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (215,47,24,NULL,'2011-03-07','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (216,47,24,NULL,'2011-03-14','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (217,47,24,NULL,'2011-03-21','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (218,47,24,NULL,'2011-03-28','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (219,47,24,NULL,'2011-04-04','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (220,47,24,NULL,'2011-04-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (221,47,24,NULL,'2011-04-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (222,47,24,NULL,'2011-04-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (223,47,24,NULL,'2011-05-02','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (224,47,24,NULL,'2011-05-09','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (225,50,6,NULL,'2011-03-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (226,50,6,NULL,'2011-03-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (227,50,6,NULL,'2011-03-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (228,50,6,NULL,'2011-04-01','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (229,50,6,NULL,'2011-04-08','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (230,50,6,NULL,'2011-04-15','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (231,50,6,NULL,'2011-04-22','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (232,50,6,NULL,'2011-04-29','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (233,50,6,NULL,'2011-05-06','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (234,50,6,NULL,'2011-05-13','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (235,52,6,NULL,'2011-03-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (236,52,6,NULL,'2011-03-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (237,52,6,NULL,'2011-03-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (238,52,6,NULL,'2011-04-01','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (239,52,6,NULL,'2011-04-08','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (240,52,6,NULL,'2011-04-15','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (241,52,6,NULL,'2011-04-22','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (242,52,6,NULL,'2011-04-29','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (243,52,6,NULL,'2011-05-06','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (244,52,6,NULL,'2011-05-13','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (245,53,6,NULL,'2011-03-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (246,53,6,NULL,'2011-03-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (247,53,6,NULL,'2011-03-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (248,53,6,NULL,'2011-04-01','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (249,53,6,NULL,'2011-04-08','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (250,53,6,NULL,'2011-04-15','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (251,53,6,NULL,'2011-04-22','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (252,53,6,NULL,'2011-04-29','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (253,53,6,NULL,'2011-05-06','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (254,53,6,NULL,'2011-05-13','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (255,54,6,NULL,'2011-03-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (256,54,6,NULL,'2011-03-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (257,54,6,NULL,'2011-03-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (258,54,6,NULL,'2011-04-01','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (259,54,6,NULL,'2011-04-08','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (260,54,6,NULL,'2011-04-15','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (261,54,6,NULL,'2011-04-22','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (262,54,6,NULL,'2011-04-29','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (263,54,6,NULL,'2011-05-06','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (264,54,6,NULL,'2011-05-13','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (265,55,6,NULL,'2011-03-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (266,55,6,NULL,'2011-03-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (267,55,6,NULL,'2011-03-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (268,55,6,NULL,'2011-04-01','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (269,55,6,NULL,'2011-04-08','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (270,55,6,NULL,'2011-04-15','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (271,55,6,NULL,'2011-04-22','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (272,55,6,NULL,'2011-04-29','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (273,55,6,NULL,'2011-05-06','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2);
INSERT INTO loan_schedule VALUES (274,55,6,NULL,'2011-05-13','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2);
/*!40000 ALTER TABLE loan_schedule ENABLE KEYS */;
DROP TABLE IF EXISTS loan_summary;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_summary (
  account_id int(11) NOT NULL,
  orig_principal decimal(21,4) DEFAULT NULL,
  orig_principal_currency_id smallint(6) DEFAULT NULL,
  orig_interest decimal(21,4) DEFAULT NULL,
  orig_interest_currency_id smallint(6) DEFAULT NULL,
  orig_fees decimal(21,4) DEFAULT NULL,
  orig_fees_currency_id smallint(6) DEFAULT NULL,
  orig_penalty decimal(21,4) DEFAULT NULL,
  orig_penalty_currency_id smallint(6) DEFAULT NULL,
  principal_paid decimal(21,4) DEFAULT NULL,
  principal_paid_currency_id smallint(6) DEFAULT NULL,
  interest_paid decimal(21,4) DEFAULT NULL,
  interest_paid_currency_id smallint(6) DEFAULT NULL,
  fees_paid decimal(21,4) DEFAULT NULL,
  fees_paid_currency_id smallint(6) DEFAULT NULL,
  penalty_paid decimal(21,4) DEFAULT NULL,
  penalty_paid_currency_id smallint(6) DEFAULT NULL,
  raw_amount_total decimal(21,4) DEFAULT NULL,
  raw_amount_total_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (account_id),
  KEY orig_principal_currency_id (orig_principal_currency_id),
  KEY orig_interest_currency_id (orig_interest_currency_id),
  KEY orig_fees_currency_id (orig_fees_currency_id),
  KEY orig_penalty_currency_id (orig_penalty_currency_id),
  KEY principal_paid_currency_id (principal_paid_currency_id),
  KEY interest_paid_currency_id (interest_paid_currency_id),
  KEY fees_paid_currency_id (fees_paid_currency_id),
  KEY penalty_paid_currency_id (penalty_paid_currency_id),
  KEY fk_loan_summary_raw_amount_total (raw_amount_total_currency_id),
  CONSTRAINT loan_summary_ibfk_1 FOREIGN KEY (orig_principal_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_2 FOREIGN KEY (orig_interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_3 FOREIGN KEY (orig_fees_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_4 FOREIGN KEY (orig_penalty_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_5 FOREIGN KEY (principal_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_6 FOREIGN KEY (interest_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_7 FOREIGN KEY (fees_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_8 FOREIGN KEY (penalty_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_summary_ibfk_9 FOREIGN KEY (account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_loan_summary_raw_amount_total FOREIGN KEY (raw_amount_total_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_summary DISABLE KEYS */;
INSERT INTO loan_summary VALUES (10,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_summary VALUES (11,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_summary VALUES (12,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2);
INSERT INTO loan_summary VALUES (15,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_summary VALUES (20,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2);
INSERT INTO loan_summary VALUES (25,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2);
INSERT INTO loan_summary VALUES (26,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2);
INSERT INTO loan_summary VALUES (27,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2);
INSERT INTO loan_summary VALUES (28,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2);
INSERT INTO loan_summary VALUES (29,'1000.0000',2,'14.5096',2,'500.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'513.9000',2);
INSERT INTO loan_summary VALUES (35,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2);
INSERT INTO loan_summary VALUES (37,'1000.0000',2,'19.0000',2,'10.0000',2,'5.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'28.4000',2);
INSERT INTO loan_summary VALUES (38,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2);
INSERT INTO loan_summary VALUES (39,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'0.0000',2,'4612.7000',2);
INSERT INTO loan_summary VALUES (40,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'0.0000',2,'4612.7000',2);
INSERT INTO loan_summary VALUES (41,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'0.0000',2,'4612.7000',2);
INSERT INTO loan_summary VALUES (42,'1000.0000',2,'19.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'28.4000',2);
INSERT INTO loan_summary VALUES (43,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (44,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (45,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (46,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (47,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (50,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (52,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (53,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (54,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
INSERT INTO loan_summary VALUES (55,'10000.0000',2,'461.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'460.3000',2);
/*!40000 ALTER TABLE loan_summary ENABLE KEYS */;
DROP TABLE IF EXISTS loan_trxn_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE loan_trxn_detail (
  account_trxn_id int(11) NOT NULL,
  principal_amount decimal(21,4) DEFAULT NULL,
  principal_amount_currency_id smallint(6) DEFAULT NULL,
  interest_amount decimal(21,4) DEFAULT NULL,
  interest_amount_currency_id smallint(6) DEFAULT NULL,
  penalty_amount decimal(21,4) DEFAULT NULL,
  penalty_amount_currency_id smallint(6) DEFAULT NULL,
  misc_fee_amount decimal(21,4) DEFAULT NULL,
  misc_fee_amount_currency_id smallint(6) DEFAULT NULL,
  misc_penalty_amount decimal(21,4) DEFAULT NULL,
  misc_penalty_amount_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (account_trxn_id),
  KEY principal_amount_currency_id (principal_amount_currency_id),
  KEY interest_amount_currency_id (interest_amount_currency_id),
  KEY penalty_amount_currency_id (penalty_amount_currency_id),
  KEY misc_penalty_amount_currency_id (misc_penalty_amount_currency_id),
  KEY misc_fee_amount_currency_id (misc_fee_amount_currency_id),
  KEY loan_account_trxn_idx (account_trxn_id),
  CONSTRAINT loan_trxn_detail_ibfk_1 FOREIGN KEY (principal_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_trxn_detail_ibfk_2 FOREIGN KEY (interest_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_trxn_detail_ibfk_3 FOREIGN KEY (penalty_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_trxn_detail_ibfk_4 FOREIGN KEY (misc_penalty_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_trxn_detail_ibfk_5 FOREIGN KEY (misc_fee_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT loan_trxn_detail_ibfk_6 FOREIGN KEY (account_trxn_id) REFERENCES account_trxn (account_trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_trxn_detail DISABLE KEYS */;
INSERT INTO loan_trxn_detail VALUES (2,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (3,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (4,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (5,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (6,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (7,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (8,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (9,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (10,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (11,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (12,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (13,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (14,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (15,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (16,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (17,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
INSERT INTO loan_trxn_detail VALUES (18,'10000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
/*!40000 ALTER TABLE loan_trxn_detail ENABLE KEYS */;
DROP TABLE IF EXISTS lookup_entity;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE lookup_entity (
  entity_id smallint(6) NOT NULL AUTO_INCREMENT,
  entity_name varchar(100) NOT NULL,
  description varchar(200) DEFAULT NULL,
  PRIMARY KEY (entity_id),
  KEY lookup_entityname_idx (entity_name)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE lookup_entity DISABLE KEYS */;
INSERT INTO lookup_entity VALUES (1,'ClientStatus','Client Status');
INSERT INTO lookup_entity VALUES (2,'GroupStatus','Group Status');
INSERT INTO lookup_entity VALUES (3,'CenterStatus','Center Status');
INSERT INTO lookup_entity VALUES (4,'OfficeStatus','Office Status');
INSERT INTO lookup_entity VALUES (5,'AccountState','Account States');
INSERT INTO lookup_entity VALUES (6,'PersonnelStatusUnused','Personnel Status (Unused)');
INSERT INTO lookup_entity VALUES (7,'GroupFlag','Group Flag');
INSERT INTO lookup_entity VALUES (8,'FeeType','Fee Type');
INSERT INTO lookup_entity VALUES (9,'Titles','Customer Position');
INSERT INTO lookup_entity VALUES (10,'PovertyStatus','Poverty Status For Client');
INSERT INTO lookup_entity VALUES (11,'Center','Center VALUES');
INSERT INTO lookup_entity VALUES (12,'Group','Group VALUES');
INSERT INTO lookup_entity VALUES (13,'Client','Client VALUES');
INSERT INTO lookup_entity VALUES (14,'Office','Office');
INSERT INTO lookup_entity VALUES (15,'Salutation','Mr/Mrs');
INSERT INTO lookup_entity VALUES (16,'Gender','Male/Female');
INSERT INTO lookup_entity VALUES (17,'MaritalStatus','Married/UnMarried');
INSERT INTO lookup_entity VALUES (18,'Citizenship','Citizenship');
INSERT INTO lookup_entity VALUES (19,'Ethinicity','Ethnicity');
INSERT INTO lookup_entity VALUES (20,'EducationLevel','EducationLevel');
INSERT INTO lookup_entity VALUES (21,'BusinessActivities','BusinessActivities');
INSERT INTO lookup_entity VALUES (22,'Handicapped','Handicaped');
INSERT INTO lookup_entity VALUES (23,'ClientFormedBy','CustomField ClientFormedBy for client');
INSERT INTO lookup_entity VALUES (24,'PostalCode','ZipCode');
INSERT INTO lookup_entity VALUES (25,'ProductState','Product State');
INSERT INTO lookup_entity VALUES (26,'Loan','Loan');
INSERT INTO lookup_entity VALUES (27,'Savings','Savings');
INSERT INTO lookup_entity VALUES (29,'PersonnelTitles','CFO/Accountant');
INSERT INTO lookup_entity VALUES (30,'PersonnelLevels','LoanOfficer/NonLoanOfficer');
INSERT INTO lookup_entity VALUES (34,'OfficeLevels','Head Office/Regional Office/Sub Regional Office/Area Office/BranchOffice');
INSERT INTO lookup_entity VALUES (35,'PrdApplicableMaster','Ceratin product categories applicable to certain types of clients');
INSERT INTO lookup_entity VALUES (36,'WeekDays','Week Days List');
INSERT INTO lookup_entity VALUES (37,'InterestTypes','Interest Types for PrdOfferings and Accounts');
INSERT INTO lookup_entity VALUES (38,'CategoryType','This is mainly used in fees to show the categories where this fee is applicable');
INSERT INTO lookup_entity VALUES (39,'InterestCalcRule','Interest calculation rule for loan prd offerings');
INSERT INTO lookup_entity VALUES (41,'GracePeriodTypes','Grace Period Types for loan products');
INSERT INTO lookup_entity VALUES (42,'DayRank','Day Rank');
INSERT INTO lookup_entity VALUES (43,'CollateralTypes','Collateral Types for loan accounts');
INSERT INTO lookup_entity VALUES (44,'OfficeCode','Office Code');
INSERT INTO lookup_entity VALUES (45,'ProductCategoryStatus','ProductCategoryStatus');
INSERT INTO lookup_entity VALUES (46,'ProductStatus','ProductStatus');
INSERT INTO lookup_entity VALUES (47,'SavingsType','SavingsType');
INSERT INTO lookup_entity VALUES (48,'RecommendedAmtUnit','RecommendedAmtUnit');
INSERT INTO lookup_entity VALUES (49,'IntCalTypes','IntCalTypes');
INSERT INTO lookup_entity VALUES (50,'YESNO','YESNO');
INSERT INTO lookup_entity VALUES (51,'AccountType','AccountType');
INSERT INTO lookup_entity VALUES (52,'SpouseFather','SpouseFather');
INSERT INTO lookup_entity VALUES (53,'CustomerStatus','CustomerStatus');
INSERT INTO lookup_entity VALUES (54,'FeePayment','FeePayment');
INSERT INTO lookup_entity VALUES (55,'FeeFormulaMaster','FeeFormulaMaster');
INSERT INTO lookup_entity VALUES (56,'PersonnelStatus','PersonnelStatus');
INSERT INTO lookup_entity VALUES (57,'Personnel','Personnel');
INSERT INTO lookup_entity VALUES (62,'ExternalId','External ID');
INSERT INTO lookup_entity VALUES (68,'FeeStatus','FeeStatus');
INSERT INTO lookup_entity VALUES (69,'AccountAction','AccountAction');
INSERT INTO lookup_entity VALUES (70,'AccountFlags','AccountFlags');
INSERT INTO lookup_entity VALUES (71,'PaymentType','PaymentType');
INSERT INTO lookup_entity VALUES (72,'SavingsStatus','Saving Status');
INSERT INTO lookup_entity VALUES (73,'Position','Position');
INSERT INTO lookup_entity VALUES (74,'Language','Language');
INSERT INTO lookup_entity VALUES (75,'CustomerAttendanceType','CustomerAttendanceType');
INSERT INTO lookup_entity VALUES (76,'FinancialAction','Financial Action');
INSERT INTO lookup_entity VALUES (77,'BulkEntry','BulkEntry');
INSERT INTO lookup_entity VALUES (78,'SavingsAccountFlag','SavingsAccountFlag');
INSERT INTO lookup_entity VALUES (79,'Address3','Address3');
INSERT INTO lookup_entity VALUES (80,'City','City');
INSERT INTO lookup_entity VALUES (81,'Interest','Interest');
INSERT INTO lookup_entity VALUES (82,'LoanPurposes','Loan Purposes');
INSERT INTO lookup_entity VALUES (83,'State','State');
INSERT INTO lookup_entity VALUES (84,'Address1','Address1');
INSERT INTO lookup_entity VALUES (85,'Address2','Address2');
INSERT INTO lookup_entity VALUES (86,'GovernmentId','GovernmentId');
INSERT INTO lookup_entity VALUES (87,'Permissions','Permissions');
INSERT INTO lookup_entity VALUES (88,'ServiceCharge','Interest');
INSERT INTO lookup_entity VALUES (89,'feeUpdationType',' fee updation can to applied to existing accounts or future accounts');
INSERT INTO lookup_entity VALUES (90,'FeeFrequency','Fee Frequency');
INSERT INTO lookup_entity VALUES (91,'RepaymentRule','Repayment Rule Types');
INSERT INTO lookup_entity VALUES (92,'LivingStatus','This entity is used to track whether the family member is living together with the client or not');
/*!40000 ALTER TABLE lookup_entity ENABLE KEYS */;
DROP TABLE IF EXISTS lookup_label;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE lookup_label (
  label_id int(11) NOT NULL AUTO_INCREMENT,
  entity_id smallint(6) DEFAULT NULL,
  locale_id smallint(6) DEFAULT NULL,
  entity_name varchar(200) DEFAULT NULL,
  PRIMARY KEY (label_id),
  KEY entity_id (entity_id),
  KEY locale_id (locale_id),
  CONSTRAINT lookup_label_ibfk_1 FOREIGN KEY (entity_id) REFERENCES lookup_entity (entity_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT lookup_label_ibfk_2 FOREIGN KEY (locale_id) REFERENCES supported_locale (locale_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=173 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE lookup_label DISABLE KEYS */;
INSERT INTO lookup_label VALUES (1,1,1,NULL);
INSERT INTO lookup_label VALUES (3,2,1,NULL);
INSERT INTO lookup_label VALUES (5,3,1,NULL);
INSERT INTO lookup_label VALUES (7,4,1,NULL);
INSERT INTO lookup_label VALUES (9,5,1,NULL);
INSERT INTO lookup_label VALUES (11,6,1,NULL);
INSERT INTO lookup_label VALUES (13,7,1,NULL);
INSERT INTO lookup_label VALUES (15,8,1,NULL);
INSERT INTO lookup_label VALUES (17,9,1,NULL);
INSERT INTO lookup_label VALUES (19,10,1,NULL);
INSERT INTO lookup_label VALUES (21,11,1,NULL);
INSERT INTO lookup_label VALUES (23,12,1,NULL);
INSERT INTO lookup_label VALUES (25,13,1,NULL);
INSERT INTO lookup_label VALUES (27,14,1,NULL);
INSERT INTO lookup_label VALUES (29,15,1,NULL);
INSERT INTO lookup_label VALUES (31,16,1,NULL);
INSERT INTO lookup_label VALUES (33,17,1,NULL);
INSERT INTO lookup_label VALUES (35,18,1,NULL);
INSERT INTO lookup_label VALUES (37,19,1,NULL);
INSERT INTO lookup_label VALUES (39,20,1,NULL);
INSERT INTO lookup_label VALUES (41,21,1,NULL);
INSERT INTO lookup_label VALUES (43,22,1,NULL);
INSERT INTO lookup_label VALUES (47,24,1,NULL);
INSERT INTO lookup_label VALUES (49,25,1,NULL);
INSERT INTO lookup_label VALUES (51,26,1,NULL);
INSERT INTO lookup_label VALUES (53,27,1,NULL);
INSERT INTO lookup_label VALUES (57,29,1,NULL);
INSERT INTO lookup_label VALUES (59,30,1,NULL);
INSERT INTO lookup_label VALUES (67,34,1,NULL);
INSERT INTO lookup_label VALUES (69,35,1,NULL);
INSERT INTO lookup_label VALUES (71,36,1,NULL);
INSERT INTO lookup_label VALUES (73,42,1,NULL);
INSERT INTO lookup_label VALUES (75,37,1,NULL);
INSERT INTO lookup_label VALUES (76,38,1,NULL);
INSERT INTO lookup_label VALUES (77,39,1,NULL);
INSERT INTO lookup_label VALUES (79,41,1,NULL);
INSERT INTO lookup_label VALUES (80,43,1,NULL);
INSERT INTO lookup_label VALUES (81,44,1,NULL);
INSERT INTO lookup_label VALUES (83,45,1,NULL);
INSERT INTO lookup_label VALUES (85,46,1,NULL);
INSERT INTO lookup_label VALUES (87,47,1,NULL);
INSERT INTO lookup_label VALUES (89,48,1,NULL);
INSERT INTO lookup_label VALUES (91,49,1,NULL);
INSERT INTO lookup_label VALUES (93,50,1,NULL);
INSERT INTO lookup_label VALUES (95,51,1,NULL);
INSERT INTO lookup_label VALUES (97,52,1,NULL);
INSERT INTO lookup_label VALUES (99,53,1,NULL);
INSERT INTO lookup_label VALUES (100,54,1,NULL);
INSERT INTO lookup_label VALUES (102,55,1,NULL);
INSERT INTO lookup_label VALUES (104,56,1,NULL);
INSERT INTO lookup_label VALUES (106,57,1,NULL);
INSERT INTO lookup_label VALUES (116,62,1,NULL);
INSERT INTO lookup_label VALUES (128,68,1,NULL);
INSERT INTO lookup_label VALUES (130,69,1,NULL);
INSERT INTO lookup_label VALUES (132,70,1,NULL);
INSERT INTO lookup_label VALUES (134,71,1,NULL);
INSERT INTO lookup_label VALUES (136,72,1,NULL);
INSERT INTO lookup_label VALUES (151,74,1,NULL);
INSERT INTO lookup_label VALUES (154,75,1,NULL);
INSERT INTO lookup_label VALUES (156,76,1,NULL);
INSERT INTO lookup_label VALUES (158,77,1,NULL);
INSERT INTO lookup_label VALUES (160,79,1,NULL);
INSERT INTO lookup_label VALUES (162,80,1,NULL);
INSERT INTO lookup_label VALUES (164,81,1,NULL);
INSERT INTO lookup_label VALUES (166,82,1,NULL);
INSERT INTO lookup_label VALUES (167,83,1,NULL);
INSERT INTO lookup_label VALUES (168,84,1,NULL);
INSERT INTO lookup_label VALUES (169,85,1,NULL);
INSERT INTO lookup_label VALUES (170,86,1,NULL);
INSERT INTO lookup_label VALUES (171,87,1,NULL);
INSERT INTO lookup_label VALUES (172,88,1,NULL);
/*!40000 ALTER TABLE lookup_label ENABLE KEYS */;
DROP TABLE IF EXISTS lookup_value;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE lookup_value (
  lookup_id int(11) NOT NULL AUTO_INCREMENT,
  entity_id smallint(6) DEFAULT NULL,
  lookup_name varchar(100) DEFAULT NULL,
  PRIMARY KEY (lookup_id),
  UNIQUE KEY lookup_name_idx (lookup_name),
  KEY lookup_value_idx (entity_id),
  CONSTRAINT lookup_value_ibfk_1 FOREIGN KEY (entity_id) REFERENCES lookup_entity (entity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=640 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE lookup_value DISABLE KEYS */;
INSERT INTO lookup_value VALUES (1,1,'ClientStatus-PartialApplication');
INSERT INTO lookup_value VALUES (2,1,'ClientStatus-ApplicationPendingApproval');
INSERT INTO lookup_value VALUES (3,1,'ClientStatus-Active');
INSERT INTO lookup_value VALUES (4,1,'ClientStatus-OnHold');
INSERT INTO lookup_value VALUES (5,1,'ClientStatus-Cancelled');
INSERT INTO lookup_value VALUES (6,1,'ClientStatus-Closed');
INSERT INTO lookup_value VALUES (7,2,'GroupStatus-PartialApplication');
INSERT INTO lookup_value VALUES (8,2,'GroupStatus-ApplicationPendingApproval');
INSERT INTO lookup_value VALUES (9,2,'GroupStatus-Active');
INSERT INTO lookup_value VALUES (10,2,'GroupStatus-OnHold');
INSERT INTO lookup_value VALUES (11,2,'GroupStatus-Cancelled');
INSERT INTO lookup_value VALUES (12,2,'GroupStatus-Closed');
INSERT INTO lookup_value VALUES (13,3,'CenterStatus-Active');
INSERT INTO lookup_value VALUES (14,3,'CenterStatus-Inactive');
INSERT INTO lookup_value VALUES (15,4,'OfficeStatus-Active');
INSERT INTO lookup_value VALUES (16,4,'OfficeStatus-Inactive');
INSERT INTO lookup_value VALUES (17,5,'AccountState-PartialApplication');
INSERT INTO lookup_value VALUES (18,5,'AccountState-ApplicationPendingApproval');
INSERT INTO lookup_value VALUES (19,5,'AccountState-ApplicationApproved');
INSERT INTO lookup_value VALUES (20,5,'AccountState-DisbursedToLo');
INSERT INTO lookup_value VALUES (21,5,'AccountState-ActiveInGoodStanding');
INSERT INTO lookup_value VALUES (22,5,'AccountState-ClosedObligationMet');
INSERT INTO lookup_value VALUES (23,5,'AccountState-ClosedWrittenOff');
INSERT INTO lookup_value VALUES (24,5,'AccountState-ClosedRescheduled');
INSERT INTO lookup_value VALUES (25,5,'AccountState-ActiveInBadStanding');
INSERT INTO lookup_value VALUES (26,6,'PersonnelStatusUnused-Active');
INSERT INTO lookup_value VALUES (27,6,'PersonnelStatusUnused-Inactive');
INSERT INTO lookup_value VALUES (28,7,'GroupFlag-Withdraw');
INSERT INTO lookup_value VALUES (29,7,'GroupFlag-Rejected');
INSERT INTO lookup_value VALUES (30,7,'GroupFlag-Blacklisted');
INSERT INTO lookup_value VALUES (31,7,'GroupFlag-Duplicate');
INSERT INTO lookup_value VALUES (32,7,'GroupFlag-Transferred');
INSERT INTO lookup_value VALUES (33,7,'GroupFlag-LeftProgram');
INSERT INTO lookup_value VALUES (34,7,'GroupFlag-Other');
INSERT INTO lookup_value VALUES (35,8,'FeeType-MaintenanceFee');
INSERT INTO lookup_value VALUES (36,8,'FeeType-ConsultancyFee');
INSERT INTO lookup_value VALUES (37,8,'FeeType-TrainingFee');
INSERT INTO lookup_value VALUES (38,8,'FeeType-MeetingCharges');
INSERT INTO lookup_value VALUES (39,9,'Titles-President');
INSERT INTO lookup_value VALUES (40,9,'Titles-VicePresident');
INSERT INTO lookup_value VALUES (41,10,'PovertyStatus-VeryPoor');
INSERT INTO lookup_value VALUES (42,10,'PovertyStatus-Poor');
INSERT INTO lookup_value VALUES (43,10,'PovertyStatus-NonPoor');
INSERT INTO lookup_value VALUES (47,15,'Salutation-Mr');
INSERT INTO lookup_value VALUES (48,15,'Salutation-Mrs');
INSERT INTO lookup_value VALUES (49,16,'Gender-Male');
INSERT INTO lookup_value VALUES (50,16,'Gender-Female');
INSERT INTO lookup_value VALUES (51,25,'ProductState-Active');
INSERT INTO lookup_value VALUES (52,25,'ProductState-Inactive');
INSERT INTO lookup_value VALUES (53,25,'ProductState-Close');
INSERT INTO lookup_value VALUES (54,26,'Loan-Loan');
INSERT INTO lookup_value VALUES (55,27,'Savings-Savings');
INSERT INTO lookup_value VALUES (57,29,'PersonnelTitles-Cashier');
INSERT INTO lookup_value VALUES (58,29,'PersonnelTitles-CenterManager');
INSERT INTO lookup_value VALUES (59,29,'PersonnelTitles-BranchManager');
INSERT INTO lookup_value VALUES (60,30,'PersonnelLevels-LoanOfficer');
INSERT INTO lookup_value VALUES (61,30,'PersonnelLevels-NonLoanOfficer');
INSERT INTO lookup_value VALUES (65,34,'DBUpgrade.OfficeLevels.Unsued');
INSERT INTO lookup_value VALUES (66,17,'MaritalStatus-Married');
INSERT INTO lookup_value VALUES (67,17,'MaritalStatus-Unmarried');
INSERT INTO lookup_value VALUES (68,35,'PrdApplicableMaster-Clients');
INSERT INTO lookup_value VALUES (69,35,'PrdApplicableMaster-Groups');
INSERT INTO lookup_value VALUES (70,35,'PrdApplicableMaster-Centers');
INSERT INTO lookup_value VALUES (71,35,'DBUpgrade.PrdApplicableMaster.Unused');
INSERT INTO lookup_value VALUES (79,37,'InterestTypes-Flat');
INSERT INTO lookup_value VALUES (80,37,'InterestTypes-DecliningBalance');
INSERT INTO lookup_value VALUES (81,38,'CategoryType-AllCustomers');
INSERT INTO lookup_value VALUES (82,38,'CategoryType-Client');
INSERT INTO lookup_value VALUES (83,38,'CategoryType-Group');
INSERT INTO lookup_value VALUES (84,38,'CategoryType-Center');
INSERT INTO lookup_value VALUES (85,38,'CategoryType-AllProductTypes');
INSERT INTO lookup_value VALUES (86,38,'CategoryType-Loans');
INSERT INTO lookup_value VALUES (87,38,'CategoryType-Savings');
INSERT INTO lookup_value VALUES (88,39,'InterestCalcRule-AlwaysRecalculate');
INSERT INTO lookup_value VALUES (89,39,'InterestCalcRule-NeverRecalculate');
INSERT INTO lookup_value VALUES (90,39,'DBUpgrade.InterestCalcRule.Unused');
INSERT INTO lookup_value VALUES (96,41,'GracePeriodTypes-None');
INSERT INTO lookup_value VALUES (97,41,'GracePeriodTypes-GraceOnAllRepayments');
INSERT INTO lookup_value VALUES (98,41,'GracePeriodTypes-PrincipalOnlyGrace');
INSERT INTO lookup_value VALUES (104,34,'OfficeLevels-HeadOffice');
INSERT INTO lookup_value VALUES (105,34,'OfficeLevels-RegionalOffice');
INSERT INTO lookup_value VALUES (106,34,'OfficeLevels-DivisionalOffice');
INSERT INTO lookup_value VALUES (107,34,'OfficeLevels-AreaOffice');
INSERT INTO lookup_value VALUES (108,34,'OfficeLevels-BranchOffice');
INSERT INTO lookup_value VALUES (109,43,'CollateralTypes-Type1');
INSERT INTO lookup_value VALUES (110,43,'CollateralTypes-Type2');
INSERT INTO lookup_value VALUES (111,44,'OfficeCode-Code1');
INSERT INTO lookup_value VALUES (112,44,'OfficeCode-Code2');
INSERT INTO lookup_value VALUES (113,45,'ProductCategoryStatus-Inactive');
INSERT INTO lookup_value VALUES (114,45,'ProductCategoryStatus-Active');
INSERT INTO lookup_value VALUES (115,46,'ProductStatus-Active');
INSERT INTO lookup_value VALUES (116,46,'ProductStatus-Inactive');
INSERT INTO lookup_value VALUES (117,46,'ProductStatus-Closed');
INSERT INTO lookup_value VALUES (118,47,'SavingsType-Mandatory');
INSERT INTO lookup_value VALUES (119,47,'SavingsType-Voluntary');
INSERT INTO lookup_value VALUES (120,48,'RecommendedAmtUnit-PerIndividual');
INSERT INTO lookup_value VALUES (121,48,'RecommendedAmtUnit-CompleteGroup');
INSERT INTO lookup_value VALUES (122,49,'IntCalTypes-MinimumBalance');
INSERT INTO lookup_value VALUES (123,49,'IntCalTypes-AverageBalance');
INSERT INTO lookup_value VALUES (124,50,'YESNO-Yes');
INSERT INTO lookup_value VALUES (125,50,'YESNO-No');
INSERT INTO lookup_value VALUES (126,51,'AccountType-Loan');
INSERT INTO lookup_value VALUES (127,51,'AccountType-Saving');
INSERT INTO lookup_value VALUES (128,52,'SpouseFather-Spouse');
INSERT INTO lookup_value VALUES (129,52,'SpouseFather-Father');
INSERT INTO lookup_value VALUES (130,18,'Citizenship-Hindu');
INSERT INTO lookup_value VALUES (131,18,'Citizenship-Muslim');
INSERT INTO lookup_value VALUES (132,19,'Ethinicity-Sc');
INSERT INTO lookup_value VALUES (133,19,'Ethinicity-Bc');
INSERT INTO lookup_value VALUES (134,20,'EducationLevel-OnlyClient');
INSERT INTO lookup_value VALUES (135,20,'EducationLevel-OnlyHusband');
INSERT INTO lookup_value VALUES (136,21,'BusinessActivities-DailyLabour');
INSERT INTO lookup_value VALUES (137,21,'BusinessActivities-Agriculture');
INSERT INTO lookup_value VALUES (138,22,'Handicapped-Yes');
INSERT INTO lookup_value VALUES (139,22,'Handicapped-No');
INSERT INTO lookup_value VALUES (140,51,'AccountType-Customer');
INSERT INTO lookup_value VALUES (141,5,'AccountState-Cancel');
INSERT INTO lookup_value VALUES (142,53,'CustomerStatus-CustomerAccountActive');
INSERT INTO lookup_value VALUES (143,53,'CustomerStatus-CustomerAccountInactive');
INSERT INTO lookup_value VALUES (144,21,'BusinessActivities-AnimalHusbandry');
INSERT INTO lookup_value VALUES (145,21,'BusinessActivities-MicroEnterprise');
INSERT INTO lookup_value VALUES (146,54,'FeePayment-Upfront');
INSERT INTO lookup_value VALUES (147,54,'FeePayment-TimeOfDisburstment');
INSERT INTO lookup_value VALUES (148,54,'FeePayment-TimeOfFirstLoanRepayment');
INSERT INTO lookup_value VALUES (149,55,'FeeFormulaMaster-LoanAmount');
INSERT INTO lookup_value VALUES (150,55,'FeeFormulaMaster-LoanAmountInterest');
INSERT INTO lookup_value VALUES (151,55,'FeeFormulaMaster-Interest');
INSERT INTO lookup_value VALUES (152,56,'PersonnelStatus-Active');
INSERT INTO lookup_value VALUES (153,56,'PersonnelStatus-Inactive');
INSERT INTO lookup_value VALUES (154,57,'Personnel-Personnel');
INSERT INTO lookup_value VALUES (165,68,'FeeStatus-Active');
INSERT INTO lookup_value VALUES (166,68,'FeeStatus-Inactive');
INSERT INTO lookup_value VALUES (167,69,'AccountAction-LoanRepayment');
INSERT INTO lookup_value VALUES (168,69,'AccountAction-Penalty');
INSERT INTO lookup_value VALUES (169,69,'AccountAction-MiscellenousPenalty');
INSERT INTO lookup_value VALUES (170,69,'AccountAction-Fee');
INSERT INTO lookup_value VALUES (171,69,'AccountAction-MiscellenousFee');
INSERT INTO lookup_value VALUES (172,69,'AccountAction-Deposit');
INSERT INTO lookup_value VALUES (173,69,'AccountAction-Withdrawal');
INSERT INTO lookup_value VALUES (174,70,'AccountFlags-Withdraw');
INSERT INTO lookup_value VALUES (175,70,'AccountFlags-Rejected');
INSERT INTO lookup_value VALUES (176,70,'AccountFlags-Other');
INSERT INTO lookup_value VALUES (177,71,'PaymentType-Cash');
INSERT INTO lookup_value VALUES (179,71,'PaymentType-Voucher');
INSERT INTO lookup_value VALUES (180,71,'PaymentType-Cheque');
INSERT INTO lookup_value VALUES (181,72,'SavingsStatus-PartialApplication');
INSERT INTO lookup_value VALUES (182,72,'SavingsStatus-ApplicationPendingApproval');
INSERT INTO lookup_value VALUES (183,72,'SavingsStatus-Cancelled');
INSERT INTO lookup_value VALUES (184,72,'SavingsStatus-Active');
INSERT INTO lookup_value VALUES (185,72,'SavingsStatus-Closed');
INSERT INTO lookup_value VALUES (186,73,'Position-CenterLeader');
INSERT INTO lookup_value VALUES (187,73,'Position-CenterSecretary');
INSERT INTO lookup_value VALUES (188,73,'Position-GroupLeader');
INSERT INTO lookup_value VALUES (189,74,'Language-English');
INSERT INTO lookup_value VALUES (191,69,'AccountAction-Payment');
INSERT INTO lookup_value VALUES (192,69,'AccountAction-Adjustment');
INSERT INTO lookup_value VALUES (193,69,'AccountAction-Disbursal');
INSERT INTO lookup_value VALUES (194,75,'CustomerAttendance-P');
INSERT INTO lookup_value VALUES (195,75,'CustomerAttendance-Ab');
INSERT INTO lookup_value VALUES (196,75,'CustomerAttendance-Al');
INSERT INTO lookup_value VALUES (197,75,'CustomerAttendance-L');
INSERT INTO lookup_value VALUES (198,76,'FinancialAction-Principal');
INSERT INTO lookup_value VALUES (199,76,'FinancialAction-Interest');
INSERT INTO lookup_value VALUES (200,76,'FinancialAction-Fees');
INSERT INTO lookup_value VALUES (201,76,'FinancialAction-Penalty');
INSERT INTO lookup_value VALUES (202,76,'FinancialAction-RoundingAdjustments');
INSERT INTO lookup_value VALUES (203,76,'FinancialAction-MandatoryDeposit');
INSERT INTO lookup_value VALUES (204,76,'FinancialAction-VoluntoryDeposit');
INSERT INTO lookup_value VALUES (205,76,'FinancialAction-MandatoryWithdrawal');
INSERT INTO lookup_value VALUES (206,76,'FinancialAction-VoluntoryWithdrawal');
INSERT INTO lookup_value VALUES (207,76,'FinancialAction-ReversalAdjustment');
INSERT INTO lookup_value VALUES (208,76,'FinancialAction-SavingsInterestPosting');
INSERT INTO lookup_value VALUES (209,76,'FinancialAction-Interest_posting');
INSERT INTO lookup_value VALUES (210,72,'SavingsStatus-Inactive');
INSERT INTO lookup_value VALUES (211,78,'SavingsAccountFlag-Withdraw');
INSERT INTO lookup_value VALUES (212,78,'SavingsAccountFlag-Rejected');
INSERT INTO lookup_value VALUES (213,78,'SavingsAccountFlag-Blacklisted');
INSERT INTO lookup_value VALUES (214,69,'AccountAction-Interest_posting');
INSERT INTO lookup_value VALUES (215,76,'FinancialAction-LoanDisbursement');
INSERT INTO lookup_value VALUES (216,73,'Position-GroupSecretary');
INSERT INTO lookup_value VALUES (217,19,'Ethinicity-St');
INSERT INTO lookup_value VALUES (218,19,'Ethinicity-Obc');
INSERT INTO lookup_value VALUES (219,19,'Ethinicity-Fc');
INSERT INTO lookup_value VALUES (220,17,'MaritalStatus-Widowed');
INSERT INTO lookup_value VALUES (221,18,'Citizenship-Christian');
INSERT INTO lookup_value VALUES (222,21,'BusinessActivities-Production');
INSERT INTO lookup_value VALUES (223,79,'DBUpgrade.Address3.Unused');
INSERT INTO lookup_value VALUES (224,80,'DBUpgrade.City.Unused');
INSERT INTO lookup_value VALUES (225,21,'BusinessActivities-Trading');
INSERT INTO lookup_value VALUES (226,20,'EducationLevel-BothLiterate');
INSERT INTO lookup_value VALUES (227,20,'EducationLevel-BothIlliterate');
INSERT INTO lookup_value VALUES (228,15,'Salutation-Ms');
INSERT INTO lookup_value VALUES (229,76,'FinancialAction-MiscFee');
INSERT INTO lookup_value VALUES (230,82,'LoanPurposes-0000AnimalHusbandry');
INSERT INTO lookup_value VALUES (231,82,'LoanPurposes-0001CowPurchase');
INSERT INTO lookup_value VALUES (232,82,'LoanPurposes-0002BuffaloPurchase');
INSERT INTO lookup_value VALUES (233,82,'LoanPurposes-0003GoatPurchase');
INSERT INTO lookup_value VALUES (234,82,'LoanPurposes-0004OxBuffalo');
INSERT INTO lookup_value VALUES (235,82,'LoanPurposes-0005PigRaising');
INSERT INTO lookup_value VALUES (236,82,'LoanPurposes-0006ChickenRaising');
INSERT INTO lookup_value VALUES (237,82,'LoanPurposes-0007DonkeyRaising');
INSERT INTO lookup_value VALUES (238,82,'LoanPurposes-0008AnimalTrading');
INSERT INTO lookup_value VALUES (239,82,'LoanPurposes-0009Horse');
INSERT INTO lookup_value VALUES (240,82,'LoanPurposes-0010Camel');
INSERT INTO lookup_value VALUES (241,82,'LoanPurposes-0011Bear');
INSERT INTO lookup_value VALUES (242,82,'LoanPurposes-0012SheepPurchase');
INSERT INTO lookup_value VALUES (243,82,'LoanPurposes-0013HybridCow');
INSERT INTO lookup_value VALUES (244,82,'LoanPurposes-0014PhotoFrameWork');
INSERT INTO lookup_value VALUES (245,82,'LoanPurposes-0021Fishery');
INSERT INTO lookup_value VALUES (246,82,'LoanPurposes-0100Trading');
INSERT INTO lookup_value VALUES (247,82,'LoanPurposes-0101PaddyBagBusiness');
INSERT INTO lookup_value VALUES (248,82,'LoanPurposes-0102VegetableSelling');
INSERT INTO lookup_value VALUES (249,82,'LoanPurposes-0103FruitSelling');
INSERT INTO lookup_value VALUES (250,82,'LoanPurposes-0104BanglesTrading');
INSERT INTO lookup_value VALUES (251,82,'LoanPurposes-0105TeaShop');
INSERT INTO lookup_value VALUES (252,82,'LoanPurposes-0106CosmeticsSelling');
INSERT INTO lookup_value VALUES (253,82,'LoanPurposes-0107GeneralStores');
INSERT INTO lookup_value VALUES (254,82,'LoanPurposes-0108FlourMill');
INSERT INTO lookup_value VALUES (255,82,'LoanPurposes-0109HotelTrading');
INSERT INTO lookup_value VALUES (256,82,'LoanPurposes-0110ToddyBusiness');
INSERT INTO lookup_value VALUES (257,82,'LoanPurposes-0111PanShop');
INSERT INTO lookup_value VALUES (258,82,'LoanPurposes-0112PanleafTrading');
INSERT INTO lookup_value VALUES (259,82,'DBUpgrade.LoanPurposes1.Unused');
INSERT INTO lookup_value VALUES (260,82,'LoanPurposes-0113MadicalStors');
INSERT INTO lookup_value VALUES (261,82,'LoanPurposes-0114MeatSelling');
INSERT INTO lookup_value VALUES (262,82,'LoanPurposes-0115OilSelling');
INSERT INTO lookup_value VALUES (263,82,'DBUpgrade.LoanPurposes2.Unused');
INSERT INTO lookup_value VALUES (264,82,'LoanPurposes-0116ChatShop');
INSERT INTO lookup_value VALUES (265,82,'LoanPurposes-0117PaintShop');
INSERT INTO lookup_value VALUES (266,82,'LoanPurposes-0118EggShop');
INSERT INTO lookup_value VALUES (267,82,'LoanPurposes-0119ShoeMaker');
INSERT INTO lookup_value VALUES (268,82,'LoanPurposes-0120PettyShop');
INSERT INTO lookup_value VALUES (269,82,'LoanPurposes-0121FlowerBusiness');
INSERT INTO lookup_value VALUES (270,82,'LoanPurposes-0122Bakery');
INSERT INTO lookup_value VALUES (271,82,'LoanPurposes-0123CoconutBusiness');
INSERT INTO lookup_value VALUES (272,82,'LoanPurposes-0124Electricals');
INSERT INTO lookup_value VALUES (273,82,'LoanPurposes-0125GroundnutBusiness');
INSERT INTO lookup_value VALUES (274,82,'LoanPurposes-0126ScrapBusiness');
INSERT INTO lookup_value VALUES (275,82,'LoanPurposes-0127BroomStickBusiness');
INSERT INTO lookup_value VALUES (276,82,'LoanPurposes-0128PlasticBusiness');
INSERT INTO lookup_value VALUES (277,82,'LoanPurposes-0129PetrolBusiness');
INSERT INTO lookup_value VALUES (278,82,'LoanPurposes-0130SteelBusiness');
INSERT INTO lookup_value VALUES (279,82,'LoanPurposes-0131BananaLeafBusiness');
INSERT INTO lookup_value VALUES (280,82,'LoanPurposes-0132StationaryShop');
INSERT INTO lookup_value VALUES (281,82,'LoanPurposes-0200Production');
INSERT INTO lookup_value VALUES (282,82,'LoanPurposes-0201BrickMaking');
INSERT INTO lookup_value VALUES (283,82,'LoanPurposes-0202MatWeaving');
INSERT INTO lookup_value VALUES (284,82,'LoanPurposes-0203ClothSelling');
INSERT INTO lookup_value VALUES (285,82,'LoanPurposes-0204SewingMachine');
INSERT INTO lookup_value VALUES (286,82,'LoanPurposes-0205WoodSelling');
INSERT INTO lookup_value VALUES (287,82,'LoanPurposes-0206BediMaking');
INSERT INTO lookup_value VALUES (288,82,'LoanPurposes-0207CarpetWeaving');
INSERT INTO lookup_value VALUES (289,82,'LoanPurposes-0208MotorBodyMaking');
INSERT INTO lookup_value VALUES (290,82,'LoanPurposes-0209BuildingMaterial');
INSERT INTO lookup_value VALUES (291,82,'LoanPurposes-0210ChainPulley');
INSERT INTO lookup_value VALUES (292,82,'LoanPurposes-0211ZigZagMachine');
INSERT INTO lookup_value VALUES (293,82,'LoanPurposes-0212PapadBusiness');
INSERT INTO lookup_value VALUES (294,82,'LoanPurposes-0213TilesBusiness');
INSERT INTO lookup_value VALUES (295,82,'LoanPurposes-0214WeldingShop');
INSERT INTO lookup_value VALUES (296,82,'LoanPurposes-0215BedBusiness');
INSERT INTO lookup_value VALUES (297,82,'LoanPurposes-0216RopeBusiness');
INSERT INTO lookup_value VALUES (298,82,'LoanPurposes-0217AgarbattiBusiness');
INSERT INTO lookup_value VALUES (299,82,'LoanPurposes-0300Transportation');
INSERT INTO lookup_value VALUES (300,82,'LoanPurposes-0301PushCartSagari');
INSERT INTO lookup_value VALUES (301,82,'LoanPurposes-0302CycleRickshaw');
INSERT INTO lookup_value VALUES (302,82,'LoanPurposes-0303BicycleRepairing');
INSERT INTO lookup_value VALUES (303,82,'LoanPurposes-0304AutoRepairing');
INSERT INTO lookup_value VALUES (304,82,'LoanPurposes-0305BullockCarts');
INSERT INTO lookup_value VALUES (305,82,'LoanPurposes-0306ThresarMachine');
INSERT INTO lookup_value VALUES (306,82,'LoanPurposes-0307VideoSet');
INSERT INTO lookup_value VALUES (307,82,'LoanPurposes-0308MujackMachine');
INSERT INTO lookup_value VALUES (308,82,'LoanPurposes-0309BiskutFery');
INSERT INTO lookup_value VALUES (309,82,'LoanPurposes-0310HorseCart');
INSERT INTO lookup_value VALUES (310,82,'LoanPurposes-0311AutoPurchase');
INSERT INTO lookup_value VALUES (311,82,'LoanPurposes-0400Agriculture');
INSERT INTO lookup_value VALUES (312,82,'LoanPurposes-0401Sharecropping');
INSERT INTO lookup_value VALUES (313,82,'LoanPurposes-0402TreeLeasing');
INSERT INTO lookup_value VALUES (314,82,'LoanPurposes-0403LandReleasing');
INSERT INTO lookup_value VALUES (315,82,'LoanPurposes-0404LandLeasing');
INSERT INTO lookup_value VALUES (316,82,'LoanPurposes-0405FoodGrainTrading');
INSERT INTO lookup_value VALUES (317,82,'LoanPurposes-0406MotorPurchasing');
INSERT INTO lookup_value VALUES (318,82,'LoanPurposes-0500Emergency');
INSERT INTO lookup_value VALUES (319,82,'LoanPurposes-0501Consumption');
INSERT INTO lookup_value VALUES (320,82,'LoanPurposes-0600TraditionalWorks');
INSERT INTO lookup_value VALUES (321,82,'LoanPurposes-0601Carpentry');
INSERT INTO lookup_value VALUES (322,82,'LoanPurposes-0602StoneCutting');
INSERT INTO lookup_value VALUES (323,82,'LoanPurposes-0603Poultry');
INSERT INTO lookup_value VALUES (324,82,'LoanPurposes-0604ClothWeaving');
INSERT INTO lookup_value VALUES (325,82,'LoanPurposes-0605LeatherSelling');
INSERT INTO lookup_value VALUES (326,82,'LoanPurposes-0606BarberShop');
INSERT INTO lookup_value VALUES (327,82,'LoanPurposes-0607Blanketweaving');
INSERT INTO lookup_value VALUES (328,82,'LoanPurposes-0608WatchShop');
INSERT INTO lookup_value VALUES (329,82,'LoanPurposes-0609Blacksmith');
INSERT INTO lookup_value VALUES (330,82,'LoanPurposes-0610IronBusiness');
INSERT INTO lookup_value VALUES (331,82,'LoanPurposes-0611SoundSystem');
INSERT INTO lookup_value VALUES (332,82,'LoanPurposes-0612PotBusiness');
INSERT INTO lookup_value VALUES (333,82,'LoanPurposes-0613CookingContract');
INSERT INTO lookup_value VALUES (334,82,'LoanPurposes-0614DhobiBusiness');
INSERT INTO lookup_value VALUES (335,82,'LoanPurposes-0615StoneBusiness');
INSERT INTO lookup_value VALUES (336,82,'LoanPurposes-0616BeautyParlour');
INSERT INTO lookup_value VALUES (337,82,'LoanPurposes-0700Marriage');
INSERT INTO lookup_value VALUES (338,82,'LoanPurposes-0999CharakhaMachnies');
INSERT INTO lookup_value VALUES (339,82,'LoanPurposes-1000Generator');
INSERT INTO lookup_value VALUES (340,82,'LoanPurposes-1001BandBaha');
INSERT INTO lookup_value VALUES (341,82,'LoanPurposes-1002TentHouse');
INSERT INTO lookup_value VALUES (342,82,'LoanPurposes-1003ToiletConstructions');
INSERT INTO lookup_value VALUES (343,82,'LoanPurposes-1004HouseConstructions');
INSERT INTO lookup_value VALUES (344,82,'LoanPurposes-1005HouseRepairs');
INSERT INTO lookup_value VALUES (345,82,'LoanPurposes-1006Education');
INSERT INTO lookup_value VALUES (346,82,'LoanPurposes-1007GoldPurchase');
INSERT INTO lookup_value VALUES (347,82,'LoanPurposes-1008Hospital');
INSERT INTO lookup_value VALUES (348,82,'LoanPurposes-1009Ration');
INSERT INTO lookup_value VALUES (349,82,'LoanPurposes-1010Education');
INSERT INTO lookup_value VALUES (350,82,'LoanPurposes-1011IgActivity');
INSERT INTO lookup_value VALUES (351,82,'LoanPurposes-1012Agriculture');
INSERT INTO lookup_value VALUES (352,82,'LoanPurposes-1013AssetsCreations');
INSERT INTO lookup_value VALUES (353,82,'LoanPurposes-1014Festivals');
INSERT INTO lookup_value VALUES (354,82,'LoanPurposes-1015LoanRepayment');
INSERT INTO lookup_value VALUES (355,82,'LoanPurposes-1016CurrentBill');
INSERT INTO lookup_value VALUES (356,82,'LoanPurposes-1017Rent');
INSERT INTO lookup_value VALUES (357,82,'LoanPurposes-1018Tour');
INSERT INTO lookup_value VALUES (358,82,'LoanPurposes-1019FerBusiness');
INSERT INTO lookup_value VALUES (359,82,'LoanPurposes-1019FerBusiness2');
INSERT INTO lookup_value VALUES (360,82,'LoanPurposes-1020SesionalBusiness');
INSERT INTO lookup_value VALUES (361,76,'FinancialAction-MiscPenalty');
INSERT INTO lookup_value VALUES (362,69,'AccountAction-CustomerAccountRepayment');
INSERT INTO lookup_value VALUES (363,76,'FinancialAction-CustomerAccountFeesPosting');
INSERT INTO lookup_value VALUES (364,69,'AccountAction-CustomerAdjustment');
INSERT INTO lookup_value VALUES (365,76,'FinancialAction-CustomerAdjustment');
INSERT INTO lookup_value VALUES (366,69,'AccountAction-SavingsAdjustment');
INSERT INTO lookup_value VALUES (367,76,'FinancialAction-MandatoryDepositAdjustment');
INSERT INTO lookup_value VALUES (368,76,'FinancialAction-VoluntoryDepositAdjustment');
INSERT INTO lookup_value VALUES (369,76,'FinancialAction-MandatoryWithdrawalAdjustment');
INSERT INTO lookup_value VALUES (370,76,'FinancialAction-VoluntoryWithdrawalAdjustment');
INSERT INTO lookup_value VALUES (371,87,'Permissions-OrganizationManagement');
INSERT INTO lookup_value VALUES (372,87,'Permissions-Funds');
INSERT INTO lookup_value VALUES (373,87,'Permissions-CanCreateFunds');
INSERT INTO lookup_value VALUES (374,87,'Permissions-CanModifyFunds');
INSERT INTO lookup_value VALUES (375,87,'Permissions-Fees');
INSERT INTO lookup_value VALUES (376,87,'Permissions-CanDefineNewFeeType');
INSERT INTO lookup_value VALUES (377,87,'Permissions-CanModifyFeeInformation');
INSERT INTO lookup_value VALUES (378,87,'Permissions-Checklists');
INSERT INTO lookup_value VALUES (379,87,'Permissions-CanDefineNewChecklistType');
INSERT INTO lookup_value VALUES (380,87,'Permissions-CanModifyChecklistInformation');
INSERT INTO lookup_value VALUES (381,87,'Permissions-OfficeManagement');
INSERT INTO lookup_value VALUES (382,87,'Permissions-Offices');
INSERT INTO lookup_value VALUES (383,87,'Permissions-CanCreateNewOffice');
INSERT INTO lookup_value VALUES (384,87,'Permissions-CanModifyOfficeInformation');
INSERT INTO lookup_value VALUES (385,87,'Permissions-UserManagement');
INSERT INTO lookup_value VALUES (386,87,'Permissions-Personnel');
INSERT INTO lookup_value VALUES (387,87,'Permissions-CanCreateNewSystemUsers');
INSERT INTO lookup_value VALUES (388,87,'Permissions-CanModifyUserInformation');
INSERT INTO lookup_value VALUES (389,87,'Permissions-CanUnlockAUser');
INSERT INTO lookup_value VALUES (390,87,'Permissions-Roles');
INSERT INTO lookup_value VALUES (391,87,'Permissions-CanCreateNewRole');
INSERT INTO lookup_value VALUES (392,87,'Permissions-CanModifyARole');
INSERT INTO lookup_value VALUES (393,87,'Permissions-CanDeleteARole');
INSERT INTO lookup_value VALUES (394,87,'Permissions-ClientManagement');
INSERT INTO lookup_value VALUES (395,87,'Permissions-Clients');
INSERT INTO lookup_value VALUES (396,87,'Permissions-Clients-CanCreateNewClientInSaveForLaterState');
INSERT INTO lookup_value VALUES (397,87,'Permissions-Clients-CanCreateNewClientInSubmitForApprovalState');
INSERT INTO lookup_value VALUES (398,87,'Permissions-Clients-CanChangeStateToPartialApplication');
INSERT INTO lookup_value VALUES (399,87,'Permissions-Clients-CanChangeStateToActive');
INSERT INTO lookup_value VALUES (400,87,'Permissions-Clients-CanChangeStateToCancelled');
INSERT INTO lookup_value VALUES (401,87,'Permissions-Clients-CanChangeStateToOnHold');
INSERT INTO lookup_value VALUES (402,87,'Permissions-Clients-CanChangeStateToClosed');
INSERT INTO lookup_value VALUES (403,87,'Permissions-Clients-CanChangeStateToApplicationPendingApproval');
INSERT INTO lookup_value VALUES (404,87,'Permissions-Clients-CanMakePaymentsToClientAccounts');
INSERT INTO lookup_value VALUES (405,87,'Permissions-Clients-CanMakeAdjustmentEntriesToClientAccount');
INSERT INTO lookup_value VALUES (407,87,'Permissions-Clients-CanWaiveADueAmount');
INSERT INTO lookup_value VALUES (408,87,'Permissions-Clients-CanRemoveFeeTypesFromClientAccount');
INSERT INTO lookup_value VALUES (409,87,'Permissions-Clients-CanAddNotesToClient');
INSERT INTO lookup_value VALUES (410,87,'Permissions-Clients-CanEditMfiInformation');
INSERT INTO lookup_value VALUES (411,87,'Permissions-Clients-CanEditGroupMembership');
INSERT INTO lookup_value VALUES (412,87,'Permissions-Clients-CanEditOfficeMembership');
INSERT INTO lookup_value VALUES (413,87,'Permissions-Clients-CanEditMeetingSchedule');
INSERT INTO lookup_value VALUES (414,87,'Permissions-Clients-CanAddEditHistoricalData');
INSERT INTO lookup_value VALUES (415,87,'Permissions-Clients-CanEditFeeAmountAttachedToTheAccount');
INSERT INTO lookup_value VALUES (416,87,'Permissions-Clients-CanBlacklistAClient');
INSERT INTO lookup_value VALUES (417,87,'Permissions-Groups');
INSERT INTO lookup_value VALUES (418,87,'Permissions-Groups-CanCreateNewGroupInSaveForLaterState');
INSERT INTO lookup_value VALUES (419,87,'Permissions-Groups-CanCreateNewGroupInSubmitForApprovalState');
INSERT INTO lookup_value VALUES (420,87,'Permissions-Groups-CanChangeStateToPartialApplication');
INSERT INTO lookup_value VALUES (421,87,'Permissions-Groups-CanChangeStateToActive');
INSERT INTO lookup_value VALUES (422,87,'Permissions-Groups-CanChangeStateToCancelled');
INSERT INTO lookup_value VALUES (423,87,'Permissions-Groups-CanChangeStateToOnHold');
INSERT INTO lookup_value VALUES (424,87,'Permissions-Groups-CanChangeStateToClosed');
INSERT INTO lookup_value VALUES (425,87,'Permissions-Groups-CanChangeStateToApplicationPendingApproval');
INSERT INTO lookup_value VALUES (426,87,'Permissions-Groups-CanMakePaymentsToGroupAccounts');
INSERT INTO lookup_value VALUES (427,87,'Permissions-Groups-CanMakeAdjustmentEntriesToGroupAccount');
INSERT INTO lookup_value VALUES (429,87,'Permissions-Groups-CanWaiveADueAmount');
INSERT INTO lookup_value VALUES (430,87,'Permissions-Groups-CanRemoveFeeTypesFromGroupAccount');
INSERT INTO lookup_value VALUES (431,87,'Permissions-Groups-CanAddNotesToGroup');
INSERT INTO lookup_value VALUES (432,87,'Permissions-Groups-CanEditGroupInformation');
INSERT INTO lookup_value VALUES (433,87,'Permissions-Groups-CanEditCenterMembership');
INSERT INTO lookup_value VALUES (434,87,'Permissions-Groups-CanEditOfficeMembership');
INSERT INTO lookup_value VALUES (435,87,'Permissions-Groups-CanEditMeetingSchedule');
INSERT INTO lookup_value VALUES (436,87,'Permissions-Groups-CanAddEditHistoricalData');
INSERT INTO lookup_value VALUES (437,87,'Permissions-Groups-CanEditFeeAmountAttachedToTheAccount');
INSERT INTO lookup_value VALUES (438,87,'Permissions-Groups-CanBlacklistAGroup');
INSERT INTO lookup_value VALUES (439,87,'Permissions-Centers');
INSERT INTO lookup_value VALUES (440,87,'Permissions-Centers-CanCreateNewCenter');
INSERT INTO lookup_value VALUES (441,87,'Permissions-Centers-CanModifyCenterInformation');
INSERT INTO lookup_value VALUES (442,87,'Permissions-Centers-CanEditCenterStatus');
INSERT INTO lookup_value VALUES (443,87,'Permissions-Centers-CanMakePaymentsToCenterAccounts');
INSERT INTO lookup_value VALUES (444,87,'Permissions-Centers-CanMakeAdjustmentEntriesToCenterAccount');
INSERT INTO lookup_value VALUES (446,87,'Permissions-Centers-CanWaiveADueAmount');
INSERT INTO lookup_value VALUES (447,87,'Permissions-Centers-CanRemoveFeeTypesFromCenterAccount');
INSERT INTO lookup_value VALUES (448,87,'Permissions-Centers-CanAddNotesToCenterRecords');
INSERT INTO lookup_value VALUES (449,87,'Permissions-Centers-CanEditFeeAmountAttachedToTheAccount');
INSERT INTO lookup_value VALUES (450,87,'Permissions-ProductDefinition');
INSERT INTO lookup_value VALUES (451,87,'Permissions-ProductCategories');
INSERT INTO lookup_value VALUES (452,87,'Permissions-CanDefineNewProductCategories');
INSERT INTO lookup_value VALUES (453,87,'Permissions-CanEditProductCategoryInformation');
INSERT INTO lookup_value VALUES (454,87,'Permissions-LoanProducts');
INSERT INTO lookup_value VALUES (455,87,'Permissions-CanDefineNewLoanProductInstance');
INSERT INTO lookup_value VALUES (456,87,'Permissions-CanEditLoanProductInstances');
INSERT INTO lookup_value VALUES (457,87,'Permissions-SavingsProducts');
INSERT INTO lookup_value VALUES (458,87,'Permissions-CanDefineNewSavingsProductInstance');
INSERT INTO lookup_value VALUES (459,87,'Permissions-CanEditSavingsProductInstances');
INSERT INTO lookup_value VALUES (460,87,'Permissions-LoanManagement');
INSERT INTO lookup_value VALUES (461,87,'Permissions-LoanProcessing');
INSERT INTO lookup_value VALUES (462,87,'Permissions-CanCreateNewLoanAccountInSaveForLaterState');
INSERT INTO lookup_value VALUES (463,87,'Permissions-CanCreateNewLoanAccountInSubmitForApprovalState');
INSERT INTO lookup_value VALUES (464,87,'Permissions-LoanProcessing-CanChangeStateToPartialApplication');
INSERT INTO lookup_value VALUES (465,87,'Permissions-LoanProcessing-CanChangeStateToApproved');
INSERT INTO lookup_value VALUES (466,87,'Permissions-LoanProcessing-CanChangeStateToCancelled');
INSERT INTO lookup_value VALUES (467,87,'Permissions-LoanProcessing-CanChangeStateToDisbursedToLo');
INSERT INTO lookup_value VALUES (469,87,'Permissions-LoanProcessing-CanChangeStateToPendingApproval');
INSERT INTO lookup_value VALUES (470,87,'Permissions-LoanProcessing-CanChangeStateToClosedWrittenOff');
INSERT INTO lookup_value VALUES (471,87,'Permissions-LoanProcessing-CanChangeStateToClosedRescheduled');
INSERT INTO lookup_value VALUES (474,87,'Permissions-LoanTransactions');
INSERT INTO lookup_value VALUES (475,87,'Permissions-CanMakePaymentToTheAccount');
INSERT INTO lookup_value VALUES (476,87,'Permissions-CanMakeAdjustmentEntryToTheAccount');
INSERT INTO lookup_value VALUES (478,87,'Permissions-CanWaivePenalty');
INSERT INTO lookup_value VALUES (479,87,'Permissions-CanWaiveAFeeInstallment');
INSERT INTO lookup_value VALUES (480,87,'Permissions-CanRemoveFeeTypesAttachedToTheAccount');
INSERT INTO lookup_value VALUES (481,87,'Permissions-Clients-CanSpecifyMeetingSchedule');
INSERT INTO lookup_value VALUES (482,87,'Permissions-Groups-CanSpecifyMeetingSchedule');
INSERT INTO lookup_value VALUES (483,87,'Permissions-Clients-CanEditPersonalInformation');
INSERT INTO lookup_value VALUES (484,87,'Permissions-Centers-CanEditMeetingSchedule');
INSERT INTO lookup_value VALUES (485,87,'Permissions-Centers-CanSpecifyMeetingSchedule');
INSERT INTO lookup_value VALUES (486,87,'Permissions-CanEditLoanAccountInformation');
INSERT INTO lookup_value VALUES (487,87,'Permissions-CanApplyChargesToLoans');
INSERT INTO lookup_value VALUES (488,87,'Permissions-CanEditSelfInformation');
INSERT INTO lookup_value VALUES (489,87,'Permissions-SavingsManagement');
INSERT INTO lookup_value VALUES (490,87,'Permissions-CanCreateNewSavingsAccountInSaveForLaterState');
INSERT INTO lookup_value VALUES (491,87,'Permissions-CanUpdateSavingsAccount');
INSERT INTO lookup_value VALUES (492,87,'Permissions-CanCloseSavingsAccount');
INSERT INTO lookup_value VALUES (493,87,'Permissions-SavingsManagement-CanChangeStateToPartialApplication');
INSERT INTO lookup_value VALUES (494,87,'Permissions-ReportsManagement');
INSERT INTO lookup_value VALUES (495,87,'Permissions-CanAdministerReports');
INSERT INTO lookup_value VALUES (496,87,'Permissions-CanPreviewReports');
INSERT INTO lookup_value VALUES (497,87,'Permissions-CanUploadNewReports');
INSERT INTO lookup_value VALUES (498,87,'Permissions-ClientDetail');
INSERT INTO lookup_value VALUES (499,87,'Permissions-Center');
INSERT INTO lookup_value VALUES (500,87,'Permissions-Status');
INSERT INTO lookup_value VALUES (501,87,'Permissions-Performance');
INSERT INTO lookup_value VALUES (502,87,'Permissions-LoanProductDetail');
INSERT INTO lookup_value VALUES (503,87,'Permissions-Analysis');
INSERT INTO lookup_value VALUES (504,87,'Permissions-Miscellaneous');
INSERT INTO lookup_value VALUES (531,87,'Permissions-CanRepayLoan');
INSERT INTO lookup_value VALUES (532,87,'Permissions-CanAddNotesToLoanAccount');
INSERT INTO lookup_value VALUES (533,87,'Permissions-SavingsManagement-CanChangeStateToPendingApproval');
INSERT INTO lookup_value VALUES (534,87,'Permissions-SavingsManagement-CanChangeStateToCancel');
INSERT INTO lookup_value VALUES (535,87,'Permissions-SavingsManagement-CanChangeStateToActive');
INSERT INTO lookup_value VALUES (536,87,'Permissions-SavingsManagement-CanChangeStateToInactive');
INSERT INTO lookup_value VALUES (537,87,'Permissions-CanBlacklistSavingsAccount');
INSERT INTO lookup_value VALUES (538,87,'Permissions-CanCreateNewSavingsAccountInSubmitForApprovalState');
INSERT INTO lookup_value VALUES (539,87,'Permissions-NotImplemented');
INSERT INTO lookup_value VALUES (540,29,'PersonnelTitles-AreaManager');
INSERT INTO lookup_value VALUES (541,29,'PersonnelTitles-DivisionalManager');
INSERT INTO lookup_value VALUES (542,29,'PersonnelTitles-RegionalManager');
INSERT INTO lookup_value VALUES (543,29,'PersonnelTitles-Coo');
INSERT INTO lookup_value VALUES (544,29,'PersonnelTitles-MisTeam');
INSERT INTO lookup_value VALUES (545,29,'PersonnelTitles-ItTeam');
INSERT INTO lookup_value VALUES (546,87,'Permissions-CanDoAdjustmentsForSavingsAccount');
INSERT INTO lookup_value VALUES (547,69,'AccountAction-LoanWrittenOff');
INSERT INTO lookup_value VALUES (548,69,'AccountAction-WaiveOffDue');
INSERT INTO lookup_value VALUES (549,69,'AccountAction-WaiveOffOverDue');
INSERT INTO lookup_value VALUES (550,76,'FinancialAction-LoanWrittenOff');
INSERT INTO lookup_value VALUES (551,87,'Permissions-CanWaiveDueDepositsForSavingsAccount');
INSERT INTO lookup_value VALUES (552,87,'Permissions-CanWaiveOverDueDepositsForSavingsAccount');
INSERT INTO lookup_value VALUES (553,87,'Permissions-CanDisburseLoan');
INSERT INTO lookup_value VALUES (554,87,'Permissions-CanMakeDepositWithdrawalToSavingsAccount');
INSERT INTO lookup_value VALUES (555,87,'Permissions-CanAddNotesToSavingsAccount');
INSERT INTO lookup_value VALUES (556,89,'feeUpdationType-AppliesToExistingFutureAccounts');
INSERT INTO lookup_value VALUES (557,89,'feeUpdationType-AppliesToFutureAccounts');
INSERT INTO lookup_value VALUES (558,90,'FeeFrequency-Periodic');
INSERT INTO lookup_value VALUES (559,90,'FeeFrequency-OneTime');
INSERT INTO lookup_value VALUES (560,87,'Permissions-CanApproveLoansInBulk');
INSERT INTO lookup_value VALUES (561,87,'Permissions-CanModifyLatenessDormancyDefinition');
INSERT INTO lookup_value VALUES (562,87,'Permissions-CanModifyOfficeHierarchy');
INSERT INTO lookup_value VALUES (563,87,'Permissions-CanAddNotesToPersonnel');
INSERT INTO lookup_value VALUES (564,87,'Permissions-Bulk');
INSERT INTO lookup_value VALUES (565,87,'Permissions-CanEnterCollectionSheetData');
INSERT INTO lookup_value VALUES (566,87,'Permissions-Clients-CanApplyChargesToClientAccounts');
INSERT INTO lookup_value VALUES (567,87,'Permissions-Groups-CanApplyChargesToGroupAccounts');
INSERT INTO lookup_value VALUES (568,87,'Permissions-Centers-CanApplyChargesToCenterAccounts');
INSERT INTO lookup_value VALUES (569,87,'Permissions-CanCreateMultipleLoanAccounts');
INSERT INTO lookup_value VALUES (570,87,'Permissions-CanReverseLoanDisbursals');
INSERT INTO lookup_value VALUES (571,70,'AccountFlags-LoanReversal');
INSERT INTO lookup_value VALUES (572,69,'AccountAction-LoanReversal');
INSERT INTO lookup_value VALUES (573,69,'AccountAction-DisrbursalAmountReversal');
INSERT INTO lookup_value VALUES (574,87,'Permissions-ConfigurationManagement');
INSERT INTO lookup_value VALUES (575,87,'Permissions-CanDefineLabels');
INSERT INTO lookup_value VALUES (576,91,'RepaymentRule-SameDay');
INSERT INTO lookup_value VALUES (577,91,'RepaymentRule-NextMeetingRepayment');
INSERT INTO lookup_value VALUES (578,91,'RepaymentRule-NextWorkingDay');
INSERT INTO lookup_value VALUES (579,87,'Permissions-CanDefineHiddenMandatoryFields');
INSERT INTO lookup_value VALUES (580,87,'Permissions-Clients-CanRemoveClientsFromGroups');
INSERT INTO lookup_value VALUES (581,87,'Permissions-CanViewDetailedAgingOfPortfolioAtRisk');
INSERT INTO lookup_value VALUES (582,87,'Permissions-Clients-CanAddAnExistingClientToAGroup');
INSERT INTO lookup_value VALUES (583,87,'Permissions-ProductMix');
INSERT INTO lookup_value VALUES (584,87,'Permissions-CanDefineProductMix');
INSERT INTO lookup_value VALUES (585,87,'Permissions-CanEditProductMix');
INSERT INTO lookup_value VALUES (586,87,'Permissions-CanViewActiveLoansByLoanOfficer');
INSERT INTO lookup_value VALUES (587,87,'Permissions-CanDefineLookupValues');
INSERT INTO lookup_value VALUES (588,87,'Permissions-CanUploadReportTemplate');
INSERT INTO lookup_value VALUES (589,87,'Permissions-CanViewReports');
INSERT INTO lookup_value VALUES (590,87,'Permissions-CanEditReportInformation');
INSERT INTO lookup_value VALUES (591,87,'Permissions-CanAdjustPaymentWhenAccountStatusIsClosedObligationMet');
INSERT INTO lookup_value VALUES (592,87,'Permissions-CanRedoLoanDisbursals');
INSERT INTO lookup_value VALUES (593,87,'Permissions-CanDefineAcceptedPaymentType');
INSERT INTO lookup_value VALUES (594,87,'Permissions-CanDefineNewReportCategory');
INSERT INTO lookup_value VALUES (595,87,'Permissions-CanViewReportCategory');
INSERT INTO lookup_value VALUES (596,87,'Permissions-CanDeleteReportCategory');
INSERT INTO lookup_value VALUES (597,87,'Permissions-CanDownloadReportTemplate');
INSERT INTO lookup_value VALUES (598,87,'Permissions-CanDefineCustomFields');
INSERT INTO lookup_value VALUES (599,74,'Language-Icelandic');
INSERT INTO lookup_value VALUES (600,74,'Language-Spanish');
INSERT INTO lookup_value VALUES (601,74,'Language-French');
INSERT INTO lookup_value VALUES (602,87,'Permissions-CanUploadAdminDocuments');
INSERT INTO lookup_value VALUES (603,87,'Permissions-CanViewAdminDocuments');
INSERT INTO lookup_value VALUES (604,37,'InterestTypes-DecliningBalance-EqualPrincipalInstallment');
INSERT INTO lookup_value VALUES (605,87,'Permissions-SystemInformation');
INSERT INTO lookup_value VALUES (606,87,'Permissions-CanViewSystemInformation');
INSERT INTO lookup_value VALUES (607,87,'Permissions-CanViewCollectionSheetReport');
INSERT INTO lookup_value VALUES (608,87,'Permissions-CanViewOrganizationSettings');
INSERT INTO lookup_value VALUES (609,76,'FinancialAction-LoanRescheduled');
INSERT INTO lookup_value VALUES (610,69,'AccountAction-LoanRescheduled');
INSERT INTO lookup_value VALUES (611,87,'Permissions-CanViewBranchCashConfirmationReport');
INSERT INTO lookup_value VALUES (612,87,'Permissions-CanViewBranchProgressReport');
INSERT INTO lookup_value VALUES (613,74,'Language-Chinese');
INSERT INTO lookup_value VALUES (614,74,'Language-Swahili');
INSERT INTO lookup_value VALUES (615,74,'Language-Arabic');
INSERT INTO lookup_value VALUES (616,74,'Language-Portuguese');
INSERT INTO lookup_value VALUES (617,74,'Language-Khmer');
INSERT INTO lookup_value VALUES (618,74,'Language-Lao');
INSERT INTO lookup_value VALUES (619,87,'Permissions-CanImportTransactions');
INSERT INTO lookup_value VALUES (620,92,'Together');
INSERT INTO lookup_value VALUES (621,92,'NotTogether');
INSERT INTO lookup_value VALUES (622,52,'Mother');
INSERT INTO lookup_value VALUES (623,52,'Child');
INSERT INTO lookup_value VALUES (624,74,'Language-Hungarian');
INSERT INTO lookup_value VALUES (625,87,'Permissions-CanShutdownMifos');
INSERT INTO lookup_value VALUES (626,91,'RepaymentRule-RepaymentMoratorium');
INSERT INTO lookup_value VALUES (627,87,'Permissions-CanDefineHoliday');
INSERT INTO lookup_value VALUES (628,87,'Permissions.CanViewDetailedAgingOfPortfolioAtRiskReport');
INSERT INTO lookup_value VALUES (629,87,'Permissions.CanViewGeneralLedgerReport');
INSERT INTO lookup_value VALUES (630,87,'Permissions-CanViewActiveSessions');
INSERT INTO lookup_value VALUES (631,87,'Permissions-CanStartMifosShutDown');
INSERT INTO lookup_value VALUES (632,87,'Permissions-CanManageQuestionGroups');
INSERT INTO lookup_value VALUES (633,87,'Permissions-CanRunBatchJobsOnDemand');
INSERT INTO lookup_value VALUES (634,87,'Permissions-CanUpdateBatchJobsConfiguration');
INSERT INTO lookup_value VALUES (635,87,'Permissions-CanActivateQuestionGroups');
INSERT INTO lookup_value VALUES (636,37,'InterestTypes-DecliningPrincipalBalance');
INSERT INTO lookup_value VALUES (637,87,'Permissions-CanAdjustBackDatedTransactions');
INSERT INTO lookup_value VALUES (638,87,'Permissions-Clients-CanEditPhoneNumber');
INSERT INTO lookup_value VALUES (639,87,'Permissions-CanUseAccountingIntegration');
/*!40000 ALTER TABLE lookup_value ENABLE KEYS */;
DROP TABLE IF EXISTS lookup_value_locale;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE lookup_value_locale (
  lookup_value_id int(11) NOT NULL AUTO_INCREMENT,
  locale_id smallint(6) NOT NULL,
  lookup_id int(11) NOT NULL,
  lookup_value varchar(300) DEFAULT NULL,
  PRIMARY KEY (lookup_value_id),
  KEY lookup_id (lookup_id),
  KEY locale_id (locale_id),
  CONSTRAINT lookup_value_locale_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT lookup_value_locale_ibfk_2 FOREIGN KEY (locale_id) REFERENCES supported_locale (locale_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=969 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE lookup_value_locale DISABLE KEYS */;
INSERT INTO lookup_value_locale VALUES (1,1,1,NULL);
INSERT INTO lookup_value_locale VALUES (3,1,2,NULL);
INSERT INTO lookup_value_locale VALUES (5,1,3,NULL);
INSERT INTO lookup_value_locale VALUES (7,1,4,NULL);
INSERT INTO lookup_value_locale VALUES (9,1,5,NULL);
INSERT INTO lookup_value_locale VALUES (11,1,6,NULL);
INSERT INTO lookup_value_locale VALUES (13,1,7,NULL);
INSERT INTO lookup_value_locale VALUES (15,1,8,NULL);
INSERT INTO lookup_value_locale VALUES (17,1,9,NULL);
INSERT INTO lookup_value_locale VALUES (19,1,10,NULL);
INSERT INTO lookup_value_locale VALUES (21,1,11,NULL);
INSERT INTO lookup_value_locale VALUES (23,1,12,NULL);
INSERT INTO lookup_value_locale VALUES (25,1,13,NULL);
INSERT INTO lookup_value_locale VALUES (27,1,14,NULL);
INSERT INTO lookup_value_locale VALUES (29,1,15,NULL);
INSERT INTO lookup_value_locale VALUES (31,1,16,NULL);
INSERT INTO lookup_value_locale VALUES (33,1,17,NULL);
INSERT INTO lookup_value_locale VALUES (35,1,18,NULL);
INSERT INTO lookup_value_locale VALUES (37,1,19,NULL);
INSERT INTO lookup_value_locale VALUES (39,1,20,NULL);
INSERT INTO lookup_value_locale VALUES (41,1,21,NULL);
INSERT INTO lookup_value_locale VALUES (43,1,22,NULL);
INSERT INTO lookup_value_locale VALUES (45,1,23,NULL);
INSERT INTO lookup_value_locale VALUES (47,1,24,NULL);
INSERT INTO lookup_value_locale VALUES (49,1,25,NULL);
INSERT INTO lookup_value_locale VALUES (51,1,26,NULL);
INSERT INTO lookup_value_locale VALUES (53,1,27,NULL);
INSERT INTO lookup_value_locale VALUES (55,1,28,NULL);
INSERT INTO lookup_value_locale VALUES (57,1,29,NULL);
INSERT INTO lookup_value_locale VALUES (59,1,30,NULL);
INSERT INTO lookup_value_locale VALUES (61,1,31,NULL);
INSERT INTO lookup_value_locale VALUES (63,1,32,NULL);
INSERT INTO lookup_value_locale VALUES (65,1,33,NULL);
INSERT INTO lookup_value_locale VALUES (67,1,34,NULL);
INSERT INTO lookup_value_locale VALUES (69,1,35,NULL);
INSERT INTO lookup_value_locale VALUES (71,1,36,NULL);
INSERT INTO lookup_value_locale VALUES (73,1,37,NULL);
INSERT INTO lookup_value_locale VALUES (75,1,38,NULL);
INSERT INTO lookup_value_locale VALUES (77,1,39,'President');
INSERT INTO lookup_value_locale VALUES (79,1,40,'Vice President');
INSERT INTO lookup_value_locale VALUES (81,1,41,NULL);
INSERT INTO lookup_value_locale VALUES (83,1,42,NULL);
INSERT INTO lookup_value_locale VALUES (85,1,43,NULL);
INSERT INTO lookup_value_locale VALUES (93,1,47,'Mr');
INSERT INTO lookup_value_locale VALUES (95,1,48,'Mrs');
INSERT INTO lookup_value_locale VALUES (97,1,49,NULL);
INSERT INTO lookup_value_locale VALUES (99,1,50,NULL);
INSERT INTO lookup_value_locale VALUES (101,1,51,NULL);
INSERT INTO lookup_value_locale VALUES (103,1,52,NULL);
INSERT INTO lookup_value_locale VALUES (105,1,53,NULL);
INSERT INTO lookup_value_locale VALUES (107,1,54,NULL);
INSERT INTO lookup_value_locale VALUES (109,1,55,NULL);
INSERT INTO lookup_value_locale VALUES (113,1,57,'Cashier');
INSERT INTO lookup_value_locale VALUES (114,1,58,'Center Manager');
INSERT INTO lookup_value_locale VALUES (115,1,59,'Branch Manager');
INSERT INTO lookup_value_locale VALUES (119,1,60,NULL);
INSERT INTO lookup_value_locale VALUES (120,1,61,NULL);
INSERT INTO lookup_value_locale VALUES (131,1,66,'Married');
INSERT INTO lookup_value_locale VALUES (133,1,67,'UnMarried');
INSERT INTO lookup_value_locale VALUES (135,1,68,NULL);
INSERT INTO lookup_value_locale VALUES (136,1,69,NULL);
INSERT INTO lookup_value_locale VALUES (137,1,70,NULL);
INSERT INTO lookup_value_locale VALUES (157,1,79,NULL);
INSERT INTO lookup_value_locale VALUES (158,1,80,NULL);
INSERT INTO lookup_value_locale VALUES (161,1,81,NULL);
INSERT INTO lookup_value_locale VALUES (162,1,82,NULL);
INSERT INTO lookup_value_locale VALUES (163,1,83,NULL);
INSERT INTO lookup_value_locale VALUES (164,1,84,NULL);
INSERT INTO lookup_value_locale VALUES (165,1,85,NULL);
INSERT INTO lookup_value_locale VALUES (166,1,86,NULL);
INSERT INTO lookup_value_locale VALUES (167,1,87,NULL);
INSERT INTO lookup_value_locale VALUES (168,1,88,NULL);
INSERT INTO lookup_value_locale VALUES (169,1,89,NULL);
INSERT INTO lookup_value_locale VALUES (176,1,96,NULL);
INSERT INTO lookup_value_locale VALUES (177,1,97,NULL);
INSERT INTO lookup_value_locale VALUES (178,1,98,NULL);
INSERT INTO lookup_value_locale VALUES (189,1,104,NULL);
INSERT INTO lookup_value_locale VALUES (191,1,105,NULL);
INSERT INTO lookup_value_locale VALUES (193,1,106,NULL);
INSERT INTO lookup_value_locale VALUES (195,1,107,NULL);
INSERT INTO lookup_value_locale VALUES (197,1,108,NULL);
INSERT INTO lookup_value_locale VALUES (199,1,109,NULL);
INSERT INTO lookup_value_locale VALUES (200,1,110,NULL);
INSERT INTO lookup_value_locale VALUES (201,1,111,NULL);
INSERT INTO lookup_value_locale VALUES (203,1,112,NULL);
INSERT INTO lookup_value_locale VALUES (205,1,113,NULL);
INSERT INTO lookup_value_locale VALUES (207,1,114,NULL);
INSERT INTO lookup_value_locale VALUES (209,1,115,NULL);
INSERT INTO lookup_value_locale VALUES (211,1,116,NULL);
INSERT INTO lookup_value_locale VALUES (213,1,117,NULL);
INSERT INTO lookup_value_locale VALUES (215,1,118,NULL);
INSERT INTO lookup_value_locale VALUES (217,1,119,NULL);
INSERT INTO lookup_value_locale VALUES (219,1,120,NULL);
INSERT INTO lookup_value_locale VALUES (221,1,121,NULL);
INSERT INTO lookup_value_locale VALUES (223,1,122,NULL);
INSERT INTO lookup_value_locale VALUES (225,1,123,NULL);
INSERT INTO lookup_value_locale VALUES (227,1,124,NULL);
INSERT INTO lookup_value_locale VALUES (229,1,125,NULL);
INSERT INTO lookup_value_locale VALUES (231,1,126,NULL);
INSERT INTO lookup_value_locale VALUES (233,1,127,NULL);
INSERT INTO lookup_value_locale VALUES (235,1,128,NULL);
INSERT INTO lookup_value_locale VALUES (237,1,129,NULL);
INSERT INTO lookup_value_locale VALUES (239,1,130,'Hindu');
INSERT INTO lookup_value_locale VALUES (241,1,131,'Muslim');
INSERT INTO lookup_value_locale VALUES (243,1,132,'SC');
INSERT INTO lookup_value_locale VALUES (245,1,133,'BC');
INSERT INTO lookup_value_locale VALUES (247,1,134,'Only Client');
INSERT INTO lookup_value_locale VALUES (249,1,135,'Only Husband');
INSERT INTO lookup_value_locale VALUES (251,1,136,NULL);
INSERT INTO lookup_value_locale VALUES (253,1,137,NULL);
INSERT INTO lookup_value_locale VALUES (255,1,138,'Yes');
INSERT INTO lookup_value_locale VALUES (257,1,139,'No');
INSERT INTO lookup_value_locale VALUES (259,1,140,NULL);
INSERT INTO lookup_value_locale VALUES (261,1,141,NULL);
INSERT INTO lookup_value_locale VALUES (263,1,142,NULL);
INSERT INTO lookup_value_locale VALUES (264,1,143,NULL);
INSERT INTO lookup_value_locale VALUES (265,1,144,NULL);
INSERT INTO lookup_value_locale VALUES (266,1,145,NULL);
INSERT INTO lookup_value_locale VALUES (267,1,146,NULL);
INSERT INTO lookup_value_locale VALUES (269,1,147,NULL);
INSERT INTO lookup_value_locale VALUES (271,1,148,NULL);
INSERT INTO lookup_value_locale VALUES (273,1,149,NULL);
INSERT INTO lookup_value_locale VALUES (275,1,150,NULL);
INSERT INTO lookup_value_locale VALUES (277,1,151,NULL);
INSERT INTO lookup_value_locale VALUES (279,1,152,NULL);
INSERT INTO lookup_value_locale VALUES (281,1,153,NULL);
INSERT INTO lookup_value_locale VALUES (283,1,154,NULL);
INSERT INTO lookup_value_locale VALUES (305,1,165,NULL);
INSERT INTO lookup_value_locale VALUES (307,1,166,NULL);
INSERT INTO lookup_value_locale VALUES (309,1,167,NULL);
INSERT INTO lookup_value_locale VALUES (311,1,168,NULL);
INSERT INTO lookup_value_locale VALUES (313,1,169,NULL);
INSERT INTO lookup_value_locale VALUES (315,1,170,NULL);
INSERT INTO lookup_value_locale VALUES (317,1,171,NULL);
INSERT INTO lookup_value_locale VALUES (319,1,172,NULL);
INSERT INTO lookup_value_locale VALUES (321,1,173,NULL);
INSERT INTO lookup_value_locale VALUES (323,1,174,NULL);
INSERT INTO lookup_value_locale VALUES (325,1,175,NULL);
INSERT INTO lookup_value_locale VALUES (327,1,176,NULL);
INSERT INTO lookup_value_locale VALUES (329,1,177,NULL);
INSERT INTO lookup_value_locale VALUES (337,1,181,NULL);
INSERT INTO lookup_value_locale VALUES (339,1,182,NULL);
INSERT INTO lookup_value_locale VALUES (341,1,183,NULL);
INSERT INTO lookup_value_locale VALUES (343,1,184,NULL);
INSERT INTO lookup_value_locale VALUES (345,1,185,NULL);
INSERT INTO lookup_value_locale VALUES (347,1,186,NULL);
INSERT INTO lookup_value_locale VALUES (349,1,187,NULL);
INSERT INTO lookup_value_locale VALUES (351,1,188,NULL);
INSERT INTO lookup_value_locale VALUES (376,1,189,NULL);
INSERT INTO lookup_value_locale VALUES (380,1,191,NULL);
INSERT INTO lookup_value_locale VALUES (382,1,192,NULL);
INSERT INTO lookup_value_locale VALUES (384,1,193,NULL);
INSERT INTO lookup_value_locale VALUES (386,1,194,NULL);
INSERT INTO lookup_value_locale VALUES (388,1,195,NULL);
INSERT INTO lookup_value_locale VALUES (390,1,196,NULL);
INSERT INTO lookup_value_locale VALUES (392,1,197,NULL);
INSERT INTO lookup_value_locale VALUES (394,1,198,NULL);
INSERT INTO lookup_value_locale VALUES (395,1,199,NULL);
INSERT INTO lookup_value_locale VALUES (396,1,200,NULL);
INSERT INTO lookup_value_locale VALUES (397,1,201,NULL);
INSERT INTO lookup_value_locale VALUES (398,1,202,NULL);
INSERT INTO lookup_value_locale VALUES (399,1,203,NULL);
INSERT INTO lookup_value_locale VALUES (400,1,204,NULL);
INSERT INTO lookup_value_locale VALUES (401,1,205,NULL);
INSERT INTO lookup_value_locale VALUES (402,1,206,NULL);
INSERT INTO lookup_value_locale VALUES (403,1,207,NULL);
INSERT INTO lookup_value_locale VALUES (404,1,208,NULL);
INSERT INTO lookup_value_locale VALUES (405,1,209,NULL);
INSERT INTO lookup_value_locale VALUES (407,1,210,NULL);
INSERT INTO lookup_value_locale VALUES (409,1,211,NULL);
INSERT INTO lookup_value_locale VALUES (411,1,212,NULL);
INSERT INTO lookup_value_locale VALUES (413,1,213,NULL);
INSERT INTO lookup_value_locale VALUES (415,1,214,NULL);
INSERT INTO lookup_value_locale VALUES (417,1,215,NULL);
INSERT INTO lookup_value_locale VALUES (418,1,216,NULL);
INSERT INTO lookup_value_locale VALUES (420,1,217,'ST');
INSERT INTO lookup_value_locale VALUES (422,1,218,'OBC');
INSERT INTO lookup_value_locale VALUES (424,1,219,'FC');
INSERT INTO lookup_value_locale VALUES (426,1,220,'Widowed');
INSERT INTO lookup_value_locale VALUES (428,1,221,'Christian');
INSERT INTO lookup_value_locale VALUES (430,1,222,NULL);
INSERT INTO lookup_value_locale VALUES (432,1,225,NULL);
INSERT INTO lookup_value_locale VALUES (434,1,226,'Both Literate');
INSERT INTO lookup_value_locale VALUES (436,1,227,'Both Illiterate');
INSERT INTO lookup_value_locale VALUES (438,1,228,'Ms');
INSERT INTO lookup_value_locale VALUES (440,1,229,NULL);
INSERT INTO lookup_value_locale VALUES (441,1,230,'0000-Animal Husbandry');
INSERT INTO lookup_value_locale VALUES (443,1,231,'0001-Cow Purchase');
INSERT INTO lookup_value_locale VALUES (445,1,232,'0002-Buffalo Purchase');
INSERT INTO lookup_value_locale VALUES (447,1,233,'0003-Goat Purchase');
INSERT INTO lookup_value_locale VALUES (449,1,234,'0004-Ox/Buffalo');
INSERT INTO lookup_value_locale VALUES (451,1,235,'0005-Pig Raising');
INSERT INTO lookup_value_locale VALUES (453,1,236,'0006-Chicken Raising');
INSERT INTO lookup_value_locale VALUES (455,1,237,'0007-Donkey Raising');
INSERT INTO lookup_value_locale VALUES (457,1,238,'0008-Animal Trading');
INSERT INTO lookup_value_locale VALUES (459,1,239,'0009-Horse');
INSERT INTO lookup_value_locale VALUES (461,1,240,'0010-Camel');
INSERT INTO lookup_value_locale VALUES (463,1,241,'0011-Bear');
INSERT INTO lookup_value_locale VALUES (465,1,242,'0012-Sheep Purchase');
INSERT INTO lookup_value_locale VALUES (467,1,243,'0013-Hybrid Cow');
INSERT INTO lookup_value_locale VALUES (469,1,244,'0014-Photo Frame Work');
INSERT INTO lookup_value_locale VALUES (471,1,245,'0021-Fishery');
INSERT INTO lookup_value_locale VALUES (473,1,246,'0100-Trading');
INSERT INTO lookup_value_locale VALUES (475,1,247,'0101-Paddy Bag Business');
INSERT INTO lookup_value_locale VALUES (477,1,248,'0102-Vegetable Selling');
INSERT INTO lookup_value_locale VALUES (479,1,249,'0103-Fruit Selling');
INSERT INTO lookup_value_locale VALUES (481,1,250,'0104-Bangles Trading');
INSERT INTO lookup_value_locale VALUES (483,1,251,'0105-Tea Shop');
INSERT INTO lookup_value_locale VALUES (485,1,252,'0106-Cosmetics Selling');
INSERT INTO lookup_value_locale VALUES (487,1,253,'0107-General Stores');
INSERT INTO lookup_value_locale VALUES (489,1,254,'0108-Flour Mill');
INSERT INTO lookup_value_locale VALUES (491,1,255,'0109-Hotel Trading');
INSERT INTO lookup_value_locale VALUES (493,1,256,'0110-Toddy Business');
INSERT INTO lookup_value_locale VALUES (495,1,257,'0111-Pan Shop');
INSERT INTO lookup_value_locale VALUES (497,1,258,'0112-PanLeaf Trading');
INSERT INTO lookup_value_locale VALUES (499,1,260,'0113-Madical Stors');
INSERT INTO lookup_value_locale VALUES (501,1,261,'0114-Meat Selling');
INSERT INTO lookup_value_locale VALUES (503,1,262,'0115-Oil Selling');
INSERT INTO lookup_value_locale VALUES (505,1,264,'0116-Chat Shop');
INSERT INTO lookup_value_locale VALUES (507,1,265,'0117-Paint Shop');
INSERT INTO lookup_value_locale VALUES (509,1,266,'0118-Egg Shop');
INSERT INTO lookup_value_locale VALUES (511,1,267,'0119-Shoe Maker');
INSERT INTO lookup_value_locale VALUES (513,1,268,'0120-Petty Shop');
INSERT INTO lookup_value_locale VALUES (515,1,269,'0121-Flower Business');
INSERT INTO lookup_value_locale VALUES (517,1,270,'0122-Bakery');
INSERT INTO lookup_value_locale VALUES (519,1,271,'0123-Coconut Business');
INSERT INTO lookup_value_locale VALUES (521,1,272,'0124-Electricals');
INSERT INTO lookup_value_locale VALUES (523,1,273,'0125-Groundnut Business');
INSERT INTO lookup_value_locale VALUES (525,1,274,'0126-Scrap Business');
INSERT INTO lookup_value_locale VALUES (527,1,275,'0127-Broom Stick Business');
INSERT INTO lookup_value_locale VALUES (529,1,276,'0128-Plastic Business');
INSERT INTO lookup_value_locale VALUES (531,1,277,'0129-Petrol Business');
INSERT INTO lookup_value_locale VALUES (533,1,278,'0130-Steel Business');
INSERT INTO lookup_value_locale VALUES (535,1,279,'0131-Banana leaf Business');
INSERT INTO lookup_value_locale VALUES (537,1,280,'0132-Stationary Shop');
INSERT INTO lookup_value_locale VALUES (539,1,281,'0200-Production');
INSERT INTO lookup_value_locale VALUES (541,1,282,'0201-Brick Making');
INSERT INTO lookup_value_locale VALUES (543,1,283,'0202-Mat Weaving');
INSERT INTO lookup_value_locale VALUES (545,1,284,'0203-Cloth Selling');
INSERT INTO lookup_value_locale VALUES (547,1,285,'0204-Sewing Machine');
INSERT INTO lookup_value_locale VALUES (549,1,286,'0205-Wood Selling');
INSERT INTO lookup_value_locale VALUES (551,1,287,'0206-Bedi Making');
INSERT INTO lookup_value_locale VALUES (553,1,288,'0207-Carpet Weaving');
INSERT INTO lookup_value_locale VALUES (555,1,289,'0208-Motor Body Making');
INSERT INTO lookup_value_locale VALUES (557,1,290,'0209-Building Material');
INSERT INTO lookup_value_locale VALUES (559,1,291,'0210-Chain Pulley');
INSERT INTO lookup_value_locale VALUES (561,1,292,'0211-Zig-zag Machine');
INSERT INTO lookup_value_locale VALUES (563,1,293,'0212-Papad Business');
INSERT INTO lookup_value_locale VALUES (565,1,294,'0213-Tiles Business');
INSERT INTO lookup_value_locale VALUES (567,1,295,'0214-Welding Shop');
INSERT INTO lookup_value_locale VALUES (569,1,296,'0215-Bed Business');
INSERT INTO lookup_value_locale VALUES (571,1,297,'0216-Rope Business');
INSERT INTO lookup_value_locale VALUES (573,1,298,'0217-Agarbatti Business');
INSERT INTO lookup_value_locale VALUES (575,1,299,'0300-Transportation');
INSERT INTO lookup_value_locale VALUES (577,1,300,'0301-Push Cart/Sagari');
INSERT INTO lookup_value_locale VALUES (579,1,301,'0302-Cycle Rickshaw');
INSERT INTO lookup_value_locale VALUES (581,1,302,'0303-Bicycle Repairing');
INSERT INTO lookup_value_locale VALUES (583,1,303,'0304-Auto Repairing');
INSERT INTO lookup_value_locale VALUES (585,1,304,'0305-Bullock Carts');
INSERT INTO lookup_value_locale VALUES (587,1,305,'0306-Thresar Machine');
INSERT INTO lookup_value_locale VALUES (589,1,306,'0307-Video Set');
INSERT INTO lookup_value_locale VALUES (591,1,307,'0308-Mujack Machine');
INSERT INTO lookup_value_locale VALUES (593,1,308,'0309-Biskut Fery');
INSERT INTO lookup_value_locale VALUES (595,1,309,'0310-Horse Cart');
INSERT INTO lookup_value_locale VALUES (597,1,310,'0311-Auto Purchase');
INSERT INTO lookup_value_locale VALUES (599,1,311,'0400-Agriculture');
INSERT INTO lookup_value_locale VALUES (601,1,312,'0401-Sharecropping');
INSERT INTO lookup_value_locale VALUES (603,1,313,'0402-Tree Leasing');
INSERT INTO lookup_value_locale VALUES (605,1,314,'0403-Land Releasing');
INSERT INTO lookup_value_locale VALUES (607,1,315,'0404-Land Leasing');
INSERT INTO lookup_value_locale VALUES (609,1,316,'0405-Food Grain Trading');
INSERT INTO lookup_value_locale VALUES (611,1,317,'0406-Motor Purchasing');
INSERT INTO lookup_value_locale VALUES (613,1,318,'0500-Emergency');
INSERT INTO lookup_value_locale VALUES (615,1,319,'0501-Consumption');
INSERT INTO lookup_value_locale VALUES (617,1,320,'0600-Traditional Works');
INSERT INTO lookup_value_locale VALUES (619,1,321,'0601-Carpentry');
INSERT INTO lookup_value_locale VALUES (621,1,322,'0602-Stone Cutting');
INSERT INTO lookup_value_locale VALUES (623,1,323,'0603-Poultry');
INSERT INTO lookup_value_locale VALUES (625,1,324,'0604-Cloth Weaving');
INSERT INTO lookup_value_locale VALUES (627,1,325,'0605-Leather Selling');
INSERT INTO lookup_value_locale VALUES (629,1,326,'0606-Barber Shop');
INSERT INTO lookup_value_locale VALUES (631,1,327,'0607-BlanketWeaving');
INSERT INTO lookup_value_locale VALUES (633,1,328,'0608-Watch Shop');
INSERT INTO lookup_value_locale VALUES (635,1,329,'0609-Blacksmith');
INSERT INTO lookup_value_locale VALUES (637,1,330,'0610-Iron Business');
INSERT INTO lookup_value_locale VALUES (639,1,331,'0611-Sound System');
INSERT INTO lookup_value_locale VALUES (641,1,332,'0612-Pot Business');
INSERT INTO lookup_value_locale VALUES (643,1,333,'0613-Cooking Contract');
INSERT INTO lookup_value_locale VALUES (645,1,334,'0614-Dhobi Business');
INSERT INTO lookup_value_locale VALUES (647,1,335,'0615-Stone Business');
INSERT INTO lookup_value_locale VALUES (649,1,336,'0616-Beauty Parlour');
INSERT INTO lookup_value_locale VALUES (651,1,337,'0700-Marriage');
INSERT INTO lookup_value_locale VALUES (653,1,338,'0999-Charakha Machnies');
INSERT INTO lookup_value_locale VALUES (655,1,339,'1000-Generator');
INSERT INTO lookup_value_locale VALUES (657,1,340,'1001-Band Baha');
INSERT INTO lookup_value_locale VALUES (659,1,341,'1002-Tent House');
INSERT INTO lookup_value_locale VALUES (661,1,342,'1003-Toilet Constructions');
INSERT INTO lookup_value_locale VALUES (663,1,343,'1004-House Constructions');
INSERT INTO lookup_value_locale VALUES (665,1,344,'1005-House Repairs');
INSERT INTO lookup_value_locale VALUES (667,1,345,'1006-Education');
INSERT INTO lookup_value_locale VALUES (669,1,346,'1007-Gold Purchase');
INSERT INTO lookup_value_locale VALUES (671,1,347,'1008-Hospital');
INSERT INTO lookup_value_locale VALUES (673,1,348,'1009-Ration');
INSERT INTO lookup_value_locale VALUES (675,1,349,'1010-Education');
INSERT INTO lookup_value_locale VALUES (677,1,350,'1011-IG Activity');
INSERT INTO lookup_value_locale VALUES (679,1,351,'1012-Agriculture');
INSERT INTO lookup_value_locale VALUES (681,1,352,'1013-Assets Creations');
INSERT INTO lookup_value_locale VALUES (683,1,353,'1014-Festivals');
INSERT INTO lookup_value_locale VALUES (685,1,354,'1015-Loan Repayment');
INSERT INTO lookup_value_locale VALUES (687,1,355,'1016-Current Bill');
INSERT INTO lookup_value_locale VALUES (689,1,356,'1017-Rent');
INSERT INTO lookup_value_locale VALUES (691,1,357,'1018-Tour');
INSERT INTO lookup_value_locale VALUES (693,1,358,'1019-Fer Business');
INSERT INTO lookup_value_locale VALUES (695,1,359,'1019-Fer Business2');
INSERT INTO lookup_value_locale VALUES (697,1,360,'1020-Sesional Business');
INSERT INTO lookup_value_locale VALUES (699,1,361,NULL);
INSERT INTO lookup_value_locale VALUES (700,1,362,NULL);
INSERT INTO lookup_value_locale VALUES (701,1,363,NULL);
INSERT INTO lookup_value_locale VALUES (702,1,364,NULL);
INSERT INTO lookup_value_locale VALUES (703,1,365,NULL);
INSERT INTO lookup_value_locale VALUES (704,1,366,NULL);
INSERT INTO lookup_value_locale VALUES (705,1,367,NULL);
INSERT INTO lookup_value_locale VALUES (706,1,368,NULL);
INSERT INTO lookup_value_locale VALUES (707,1,369,NULL);
INSERT INTO lookup_value_locale VALUES (708,1,370,NULL);
INSERT INTO lookup_value_locale VALUES (709,1,371,NULL);
INSERT INTO lookup_value_locale VALUES (710,1,372,NULL);
INSERT INTO lookup_value_locale VALUES (711,1,373,NULL);
INSERT INTO lookup_value_locale VALUES (712,1,374,NULL);
INSERT INTO lookup_value_locale VALUES (713,1,375,NULL);
INSERT INTO lookup_value_locale VALUES (714,1,376,NULL);
INSERT INTO lookup_value_locale VALUES (715,1,377,NULL);
INSERT INTO lookup_value_locale VALUES (716,1,378,NULL);
INSERT INTO lookup_value_locale VALUES (717,1,379,NULL);
INSERT INTO lookup_value_locale VALUES (718,1,380,NULL);
INSERT INTO lookup_value_locale VALUES (719,1,381,NULL);
INSERT INTO lookup_value_locale VALUES (720,1,382,NULL);
INSERT INTO lookup_value_locale VALUES (721,1,383,NULL);
INSERT INTO lookup_value_locale VALUES (722,1,384,NULL);
INSERT INTO lookup_value_locale VALUES (723,1,385,NULL);
INSERT INTO lookup_value_locale VALUES (724,1,386,NULL);
INSERT INTO lookup_value_locale VALUES (725,1,387,NULL);
INSERT INTO lookup_value_locale VALUES (726,1,388,NULL);
INSERT INTO lookup_value_locale VALUES (727,1,389,NULL);
INSERT INTO lookup_value_locale VALUES (728,1,390,NULL);
INSERT INTO lookup_value_locale VALUES (729,1,391,NULL);
INSERT INTO lookup_value_locale VALUES (730,1,392,NULL);
INSERT INTO lookup_value_locale VALUES (731,1,393,NULL);
INSERT INTO lookup_value_locale VALUES (732,1,394,NULL);
INSERT INTO lookup_value_locale VALUES (733,1,395,NULL);
INSERT INTO lookup_value_locale VALUES (734,1,396,NULL);
INSERT INTO lookup_value_locale VALUES (735,1,397,NULL);
INSERT INTO lookup_value_locale VALUES (736,1,398,NULL);
INSERT INTO lookup_value_locale VALUES (737,1,399,NULL);
INSERT INTO lookup_value_locale VALUES (738,1,400,NULL);
INSERT INTO lookup_value_locale VALUES (739,1,401,NULL);
INSERT INTO lookup_value_locale VALUES (740,1,402,NULL);
INSERT INTO lookup_value_locale VALUES (741,1,403,NULL);
INSERT INTO lookup_value_locale VALUES (742,1,404,NULL);
INSERT INTO lookup_value_locale VALUES (743,1,405,NULL);
INSERT INTO lookup_value_locale VALUES (745,1,407,NULL);
INSERT INTO lookup_value_locale VALUES (746,1,408,NULL);
INSERT INTO lookup_value_locale VALUES (747,1,409,NULL);
INSERT INTO lookup_value_locale VALUES (748,1,410,NULL);
INSERT INTO lookup_value_locale VALUES (749,1,411,NULL);
INSERT INTO lookup_value_locale VALUES (750,1,412,NULL);
INSERT INTO lookup_value_locale VALUES (751,1,413,NULL);
INSERT INTO lookup_value_locale VALUES (752,1,414,NULL);
INSERT INTO lookup_value_locale VALUES (753,1,415,NULL);
INSERT INTO lookup_value_locale VALUES (754,1,416,NULL);
INSERT INTO lookup_value_locale VALUES (755,1,417,NULL);
INSERT INTO lookup_value_locale VALUES (756,1,418,NULL);
INSERT INTO lookup_value_locale VALUES (757,1,419,NULL);
INSERT INTO lookup_value_locale VALUES (758,1,420,NULL);
INSERT INTO lookup_value_locale VALUES (759,1,421,NULL);
INSERT INTO lookup_value_locale VALUES (760,1,422,NULL);
INSERT INTO lookup_value_locale VALUES (761,1,423,NULL);
INSERT INTO lookup_value_locale VALUES (762,1,424,NULL);
INSERT INTO lookup_value_locale VALUES (763,1,425,NULL);
INSERT INTO lookup_value_locale VALUES (764,1,426,NULL);
INSERT INTO lookup_value_locale VALUES (765,1,427,NULL);
INSERT INTO lookup_value_locale VALUES (767,1,429,NULL);
INSERT INTO lookup_value_locale VALUES (768,1,430,NULL);
INSERT INTO lookup_value_locale VALUES (769,1,431,NULL);
INSERT INTO lookup_value_locale VALUES (770,1,432,NULL);
INSERT INTO lookup_value_locale VALUES (771,1,433,NULL);
INSERT INTO lookup_value_locale VALUES (772,1,434,NULL);
INSERT INTO lookup_value_locale VALUES (773,1,435,NULL);
INSERT INTO lookup_value_locale VALUES (774,1,436,NULL);
INSERT INTO lookup_value_locale VALUES (775,1,437,NULL);
INSERT INTO lookup_value_locale VALUES (776,1,438,NULL);
INSERT INTO lookup_value_locale VALUES (777,1,439,NULL);
INSERT INTO lookup_value_locale VALUES (778,1,440,NULL);
INSERT INTO lookup_value_locale VALUES (779,1,441,NULL);
INSERT INTO lookup_value_locale VALUES (780,1,442,NULL);
INSERT INTO lookup_value_locale VALUES (781,1,443,NULL);
INSERT INTO lookup_value_locale VALUES (782,1,444,NULL);
INSERT INTO lookup_value_locale VALUES (784,1,446,NULL);
INSERT INTO lookup_value_locale VALUES (785,1,447,NULL);
INSERT INTO lookup_value_locale VALUES (786,1,448,NULL);
INSERT INTO lookup_value_locale VALUES (787,1,449,NULL);
INSERT INTO lookup_value_locale VALUES (788,1,450,NULL);
INSERT INTO lookup_value_locale VALUES (789,1,451,NULL);
INSERT INTO lookup_value_locale VALUES (790,1,452,NULL);
INSERT INTO lookup_value_locale VALUES (791,1,453,NULL);
INSERT INTO lookup_value_locale VALUES (792,1,454,NULL);
INSERT INTO lookup_value_locale VALUES (793,1,455,NULL);
INSERT INTO lookup_value_locale VALUES (794,1,456,NULL);
INSERT INTO lookup_value_locale VALUES (795,1,457,NULL);
INSERT INTO lookup_value_locale VALUES (796,1,458,NULL);
INSERT INTO lookup_value_locale VALUES (797,1,459,NULL);
INSERT INTO lookup_value_locale VALUES (798,1,460,NULL);
INSERT INTO lookup_value_locale VALUES (799,1,461,NULL);
INSERT INTO lookup_value_locale VALUES (800,1,462,NULL);
INSERT INTO lookup_value_locale VALUES (801,1,463,NULL);
INSERT INTO lookup_value_locale VALUES (802,1,464,NULL);
INSERT INTO lookup_value_locale VALUES (803,1,465,NULL);
INSERT INTO lookup_value_locale VALUES (804,1,466,NULL);
INSERT INTO lookup_value_locale VALUES (805,1,467,NULL);
INSERT INTO lookup_value_locale VALUES (807,1,469,NULL);
INSERT INTO lookup_value_locale VALUES (808,1,470,NULL);
INSERT INTO lookup_value_locale VALUES (809,1,471,NULL);
INSERT INTO lookup_value_locale VALUES (812,1,474,NULL);
INSERT INTO lookup_value_locale VALUES (813,1,475,NULL);
INSERT INTO lookup_value_locale VALUES (814,1,476,NULL);
INSERT INTO lookup_value_locale VALUES (816,1,478,NULL);
INSERT INTO lookup_value_locale VALUES (817,1,479,NULL);
INSERT INTO lookup_value_locale VALUES (818,1,480,NULL);
INSERT INTO lookup_value_locale VALUES (819,1,481,NULL);
INSERT INTO lookup_value_locale VALUES (820,1,482,NULL);
INSERT INTO lookup_value_locale VALUES (821,1,483,NULL);
INSERT INTO lookup_value_locale VALUES (822,1,484,NULL);
INSERT INTO lookup_value_locale VALUES (823,1,485,NULL);
INSERT INTO lookup_value_locale VALUES (824,1,486,NULL);
INSERT INTO lookup_value_locale VALUES (825,1,487,NULL);
INSERT INTO lookup_value_locale VALUES (826,1,488,NULL);
INSERT INTO lookup_value_locale VALUES (827,1,489,NULL);
INSERT INTO lookup_value_locale VALUES (828,1,490,NULL);
INSERT INTO lookup_value_locale VALUES (829,1,491,NULL);
INSERT INTO lookup_value_locale VALUES (830,1,492,NULL);
INSERT INTO lookup_value_locale VALUES (831,1,493,NULL);
INSERT INTO lookup_value_locale VALUES (832,1,494,NULL);
INSERT INTO lookup_value_locale VALUES (833,1,495,NULL);
INSERT INTO lookup_value_locale VALUES (834,1,496,NULL);
INSERT INTO lookup_value_locale VALUES (835,1,497,NULL);
INSERT INTO lookup_value_locale VALUES (836,1,498,NULL);
INSERT INTO lookup_value_locale VALUES (837,1,499,NULL);
INSERT INTO lookup_value_locale VALUES (838,1,500,NULL);
INSERT INTO lookup_value_locale VALUES (839,1,501,NULL);
INSERT INTO lookup_value_locale VALUES (840,1,502,NULL);
INSERT INTO lookup_value_locale VALUES (841,1,503,NULL);
INSERT INTO lookup_value_locale VALUES (842,1,504,NULL);
INSERT INTO lookup_value_locale VALUES (869,1,531,NULL);
INSERT INTO lookup_value_locale VALUES (870,1,532,NULL);
INSERT INTO lookup_value_locale VALUES (871,1,533,NULL);
INSERT INTO lookup_value_locale VALUES (872,1,534,NULL);
INSERT INTO lookup_value_locale VALUES (873,1,535,NULL);
INSERT INTO lookup_value_locale VALUES (874,1,536,NULL);
INSERT INTO lookup_value_locale VALUES (875,1,537,NULL);
INSERT INTO lookup_value_locale VALUES (876,1,538,NULL);
INSERT INTO lookup_value_locale VALUES (877,1,539,NULL);
INSERT INTO lookup_value_locale VALUES (878,1,540,'Area Manager');
INSERT INTO lookup_value_locale VALUES (879,1,541,'Divisional Manager');
INSERT INTO lookup_value_locale VALUES (880,1,542,'Regional Manager');
INSERT INTO lookup_value_locale VALUES (881,1,543,'COO');
INSERT INTO lookup_value_locale VALUES (882,1,544,'MIS Team');
INSERT INTO lookup_value_locale VALUES (883,1,545,'IT Team');
INSERT INTO lookup_value_locale VALUES (884,1,546,NULL);
INSERT INTO lookup_value_locale VALUES (885,1,547,NULL);
INSERT INTO lookup_value_locale VALUES (886,1,548,NULL);
INSERT INTO lookup_value_locale VALUES (887,1,549,NULL);
INSERT INTO lookup_value_locale VALUES (888,1,550,NULL);
INSERT INTO lookup_value_locale VALUES (889,1,551,NULL);
INSERT INTO lookup_value_locale VALUES (890,1,552,NULL);
INSERT INTO lookup_value_locale VALUES (891,1,553,NULL);
INSERT INTO lookup_value_locale VALUES (892,1,554,NULL);
INSERT INTO lookup_value_locale VALUES (893,1,555,NULL);
INSERT INTO lookup_value_locale VALUES (894,1,556,NULL);
INSERT INTO lookup_value_locale VALUES (895,1,557,NULL);
INSERT INTO lookup_value_locale VALUES (896,1,558,NULL);
INSERT INTO lookup_value_locale VALUES (897,1,559,NULL);
INSERT INTO lookup_value_locale VALUES (898,1,560,NULL);
INSERT INTO lookup_value_locale VALUES (899,1,561,NULL);
INSERT INTO lookup_value_locale VALUES (900,1,562,NULL);
INSERT INTO lookup_value_locale VALUES (901,1,563,NULL);
INSERT INTO lookup_value_locale VALUES (902,1,564,NULL);
INSERT INTO lookup_value_locale VALUES (903,1,565,NULL);
INSERT INTO lookup_value_locale VALUES (904,1,566,NULL);
INSERT INTO lookup_value_locale VALUES (905,1,567,NULL);
INSERT INTO lookup_value_locale VALUES (906,1,568,NULL);
INSERT INTO lookup_value_locale VALUES (912,1,179,NULL);
INSERT INTO lookup_value_locale VALUES (913,1,180,NULL);
INSERT INTO lookup_value_locale VALUES (914,1,569,NULL);
INSERT INTO lookup_value_locale VALUES (915,1,570,NULL);
INSERT INTO lookup_value_locale VALUES (916,1,571,NULL);
INSERT INTO lookup_value_locale VALUES (917,1,572,NULL);
INSERT INTO lookup_value_locale VALUES (918,1,573,NULL);
INSERT INTO lookup_value_locale VALUES (919,1,574,NULL);
INSERT INTO lookup_value_locale VALUES (920,1,575,NULL);
INSERT INTO lookup_value_locale VALUES (921,1,576,NULL);
INSERT INTO lookup_value_locale VALUES (922,1,577,NULL);
INSERT INTO lookup_value_locale VALUES (923,1,578,NULL);
INSERT INTO lookup_value_locale VALUES (924,1,579,NULL);
INSERT INTO lookup_value_locale VALUES (925,1,580,NULL);
INSERT INTO lookup_value_locale VALUES (926,1,581,NULL);
INSERT INTO lookup_value_locale VALUES (927,1,582,NULL);
INSERT INTO lookup_value_locale VALUES (928,1,583,NULL);
INSERT INTO lookup_value_locale VALUES (929,1,584,NULL);
INSERT INTO lookup_value_locale VALUES (930,1,585,NULL);
INSERT INTO lookup_value_locale VALUES (931,1,586,NULL);
INSERT INTO lookup_value_locale VALUES (932,1,587,NULL);
INSERT INTO lookup_value_locale VALUES (933,1,588,NULL);
INSERT INTO lookup_value_locale VALUES (934,1,589,NULL);
INSERT INTO lookup_value_locale VALUES (935,1,590,NULL);
INSERT INTO lookup_value_locale VALUES (936,1,591,NULL);
INSERT INTO lookup_value_locale VALUES (937,1,592,NULL);
INSERT INTO lookup_value_locale VALUES (938,1,593,NULL);
INSERT INTO lookup_value_locale VALUES (939,1,594,NULL);
INSERT INTO lookup_value_locale VALUES (940,1,595,NULL);
INSERT INTO lookup_value_locale VALUES (941,1,596,NULL);
INSERT INTO lookup_value_locale VALUES (942,1,597,NULL);
INSERT INTO lookup_value_locale VALUES (943,1,598,NULL);
INSERT INTO lookup_value_locale VALUES (944,1,602,NULL);
INSERT INTO lookup_value_locale VALUES (945,1,603,NULL);
INSERT INTO lookup_value_locale VALUES (946,1,604,NULL);
INSERT INTO lookup_value_locale VALUES (947,1,605,NULL);
INSERT INTO lookup_value_locale VALUES (948,1,606,NULL);
INSERT INTO lookup_value_locale VALUES (949,1,607,NULL);
INSERT INTO lookup_value_locale VALUES (950,1,608,NULL);
INSERT INTO lookup_value_locale VALUES (951,1,609,NULL);
INSERT INTO lookup_value_locale VALUES (952,1,610,NULL);
INSERT INTO lookup_value_locale VALUES (953,1,611,NULL);
INSERT INTO lookup_value_locale VALUES (954,1,612,NULL);
INSERT INTO lookup_value_locale VALUES (955,1,619,NULL);
INSERT INTO lookup_value_locale VALUES (956,1,625,NULL);
INSERT INTO lookup_value_locale VALUES (957,1,627,NULL);
INSERT INTO lookup_value_locale VALUES (958,1,628,'Can View Detailed Aging Of Portfolio At Risk Report');
INSERT INTO lookup_value_locale VALUES (959,1,629,'Can View General Ledger Report');
INSERT INTO lookup_value_locale VALUES (960,1,630,NULL);
INSERT INTO lookup_value_locale VALUES (961,1,631,NULL);
INSERT INTO lookup_value_locale VALUES (962,1,632,NULL);
INSERT INTO lookup_value_locale VALUES (963,1,633,NULL);
INSERT INTO lookup_value_locale VALUES (964,1,634,NULL);
INSERT INTO lookup_value_locale VALUES (965,1,635,NULL);
INSERT INTO lookup_value_locale VALUES (966,1,637,NULL);
INSERT INTO lookup_value_locale VALUES (967,1,638,NULL);
INSERT INTO lookup_value_locale VALUES (968,1,639,NULL);
/*!40000 ALTER TABLE lookup_value_locale ENABLE KEYS */;
DROP TABLE IF EXISTS max_min_interest_rate;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE max_min_interest_rate (
  account_id int(11) NOT NULL AUTO_INCREMENT,
  min_interest_rate decimal(21,4) NOT NULL,
  max_interest_rate decimal(21,4) NOT NULL,
  PRIMARY KEY (account_id),
  CONSTRAINT max_min_interest_rate_ibfk_1 FOREIGN KEY (account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE max_min_interest_rate DISABLE KEYS */;
INSERT INTO max_min_interest_rate VALUES (10,'0.0000','0.0000');
INSERT INTO max_min_interest_rate VALUES (11,'0.0000','0.0000');
INSERT INTO max_min_interest_rate VALUES (12,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (15,'0.0000','0.0000');
INSERT INTO max_min_interest_rate VALUES (20,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (25,'10.0000','30.0000');
INSERT INTO max_min_interest_rate VALUES (26,'10.0000','30.0000');
INSERT INTO max_min_interest_rate VALUES (27,'10.0000','30.0000');
INSERT INTO max_min_interest_rate VALUES (28,'10.0000','30.0000');
INSERT INTO max_min_interest_rate VALUES (29,'10.0000','30.0000');
INSERT INTO max_min_interest_rate VALUES (35,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (37,'1.0000','99.0000');
INSERT INTO max_min_interest_rate VALUES (38,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (39,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (40,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (41,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (42,'1.0000','99.0000');
INSERT INTO max_min_interest_rate VALUES (43,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (44,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (45,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (46,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (47,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (50,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (52,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (53,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (54,'24.0000','24.0000');
INSERT INTO max_min_interest_rate VALUES (55,'24.0000','24.0000');
/*!40000 ALTER TABLE max_min_interest_rate ENABLE KEYS */;
DROP TABLE IF EXISTS max_min_loan_amount;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE max_min_loan_amount (
  account_id int(11) NOT NULL AUTO_INCREMENT,
  min_loan_amount decimal(21,4) NOT NULL,
  max_loan_amount decimal(21,4) NOT NULL,
  PRIMARY KEY (account_id),
  CONSTRAINT max_min_loan_amount_ibfk_1 FOREIGN KEY (account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE max_min_loan_amount DISABLE KEYS */;
INSERT INTO max_min_loan_amount VALUES (10,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (11,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (12,'1000.0000','100000.0000');
INSERT INTO max_min_loan_amount VALUES (15,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (20,'1000.0000','100000.0000');
INSERT INTO max_min_loan_amount VALUES (25,'100.0000','10000000.0000');
INSERT INTO max_min_loan_amount VALUES (26,'100.0000','10000000.0000');
INSERT INTO max_min_loan_amount VALUES (27,'100.0000','10000000.0000');
INSERT INTO max_min_loan_amount VALUES (28,'100.0000','10000000.0000');
INSERT INTO max_min_loan_amount VALUES (29,'100.0000','10000000.0000');
INSERT INTO max_min_loan_amount VALUES (35,'1000.0000','100000.0000');
INSERT INTO max_min_loan_amount VALUES (37,'100.0000','10000000.0000');
INSERT INTO max_min_loan_amount VALUES (38,'1000.0000','100000.0000');
INSERT INTO max_min_loan_amount VALUES (39,'1000.0000','100000.0000');
INSERT INTO max_min_loan_amount VALUES (40,'1000.0000','100000.0000');
INSERT INTO max_min_loan_amount VALUES (41,'1000.0000','100000.0000');
INSERT INTO max_min_loan_amount VALUES (42,'100.0000','1000000.0000');
INSERT INTO max_min_loan_amount VALUES (43,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (44,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (45,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (46,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (47,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (50,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (52,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (53,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (54,'1000.0000','10000.0000');
INSERT INTO max_min_loan_amount VALUES (55,'1000.0000','10000.0000');
/*!40000 ALTER TABLE max_min_loan_amount ENABLE KEYS */;
DROP TABLE IF EXISTS max_min_no_of_install;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE max_min_no_of_install (
  account_id int(11) NOT NULL AUTO_INCREMENT,
  min_no_install decimal(21,4) NOT NULL,
  max_no_install decimal(21,4) NOT NULL,
  PRIMARY KEY (account_id),
  CONSTRAINT max_min_no_of_install_ibfk_1 FOREIGN KEY (account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE max_min_no_of_install DISABLE KEYS */;
INSERT INTO max_min_no_of_install VALUES (10,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (11,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (12,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (15,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (20,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (25,'1.0000','52.0000');
INSERT INTO max_min_no_of_install VALUES (26,'1.0000','52.0000');
INSERT INTO max_min_no_of_install VALUES (27,'1.0000','52.0000');
INSERT INTO max_min_no_of_install VALUES (28,'1.0000','52.0000');
INSERT INTO max_min_no_of_install VALUES (29,'1.0000','52.0000');
INSERT INTO max_min_no_of_install VALUES (35,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (37,'1.0000','100.0000');
INSERT INTO max_min_no_of_install VALUES (38,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (39,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (40,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (41,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (42,'1.0000','100.0000');
INSERT INTO max_min_no_of_install VALUES (43,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (44,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (45,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (46,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (47,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (50,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (52,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (53,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (54,'1.0000','10.0000');
INSERT INTO max_min_no_of_install VALUES (55,'1.0000','10.0000');
/*!40000 ALTER TABLE max_min_no_of_install ENABLE KEYS */;
DROP TABLE IF EXISTS meeting;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE meeting (
  meeting_id int(11) NOT NULL AUTO_INCREMENT,
  meeting_type_id smallint(6) NOT NULL,
  meeting_place varchar(200) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  start_time date DEFAULT NULL,
  end_time date DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (meeting_id),
  KEY meeting_type_id (meeting_type_id),
  CONSTRAINT meeting_ibfk_1 FOREIGN KEY (meeting_type_id) REFERENCES meeting_type (meeting_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE meeting DISABLE KEYS */;
INSERT INTO meeting VALUES (1,4,'test','2011-02-01',NULL,NULL,NULL,9);
INSERT INTO meeting VALUES (2,3,'meetingPlace','2011-02-18',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (3,2,'meetingPlace','2011-02-18',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (4,4,'test','2001-03-01',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (5,4,'test','2011-02-21',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (6,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (7,4,'test','2010-01-22',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (8,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (9,4,'test','2010-01-22',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (10,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (11,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (12,4,'test','2010-01-22',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (13,1,'meetingPlace','2011-02-21',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (14,1,'meetingPlace','2011-02-21',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (15,4,'test','2011-02-21',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (16,4,'test','2011-02-25',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (17,4,'test','2010-02-22',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (18,4,'test','2011-02-18',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (19,1,'meetingPlace','2011-02-22',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (20,4,'test','2011-02-22',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (21,4,'test','2011-02-22',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (22,4,'test','2011-02-22',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (23,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (24,5,'meetingPlace','2011-03-04',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (25,4,'weeklyMeetingPlace','2011-02-24',NULL,NULL,NULL,3);
INSERT INTO meeting VALUES (28,3,'meetingPlace','2011-02-25',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (29,2,'meetingPlace','2011-02-25',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (30,4,'test','2010-10-11',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (31,1,'meetingPlace','2010-10-11',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (32,1,'test','2010-10-13',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (33,1,'test','2010-10-13',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (34,1,'test','2010-10-13',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (35,1,'test','2010-10-13',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (36,1,'test','2010-10-13',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (37,4,'MonthlyMeetingLocation','2011-02-25',NULL,NULL,NULL,2);
INSERT INTO meeting VALUES (38,4,'MonthlyMeetingLocation','2011-02-25',NULL,NULL,NULL,2);
INSERT INTO meeting VALUES (39,1,'meetingPlace','2011-02-25',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (40,4,'test','2020-01-03',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (41,1,'meetingPlace','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (42,4,'test','2011-02-28',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (43,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (44,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (45,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (46,1,'meetingPlace','2011-02-22',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (47,1,'meetingPlace','2011-02-22',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (48,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (49,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (50,4,'test','2011-02-28',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (51,1,'meetingPlace','2010-10-11',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (52,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (53,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (54,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (55,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (56,4,'test','2011-02-28',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (57,1,'meetingPlace','2011-01-01',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (58,1,'meetingPlace','2011-01-01',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (59,1,'meetingPlace','2011-01-01',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (60,4,'test','2011-03-04',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (61,4,'test','2011-03-03',NULL,NULL,NULL,3);
INSERT INTO meeting VALUES (62,4,'test','2011-03-04',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (63,4,'test','2011-03-04',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (64,4,'test','2011-03-04',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (65,4,'test','2011-03-04',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (66,4,'test','2010-10-11',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (67,4,'test','2010-10-11',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (68,4,'a','2011-01-01',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (69,4,'test','2011-03-09',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (70,2,'meetingPlace','2009-01-29',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (71,3,'meetingPlace','2009-01-29',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (72,4,'center','2011-03-14',NULL,NULL,NULL,1);
INSERT INTO meeting VALUES (73,4,'center','2011-03-14',NULL,NULL,NULL,3);
INSERT INTO meeting VALUES (74,3,'meetingPlace','2011-03-14',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (75,2,'meetingPlace','2011-03-14',NULL,NULL,NULL,0);
INSERT INTO meeting VALUES (76,4,'DefineNewSavingsProductTestPlace','2011-03-15',NULL,NULL,NULL,3);
/*!40000 ALTER TABLE meeting ENABLE KEYS */;
DROP TABLE IF EXISTS meeting_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE meeting_type (
  meeting_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  meeting_purpose varchar(50) DEFAULT NULL,
  description varchar(200) NOT NULL,
  PRIMARY KEY (meeting_type_id)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE meeting_type DISABLE KEYS */;
INSERT INTO meeting_type VALUES (1,'LOANFREQUENCYOFINSTALLMENTS','Loan Frequency of istalments');
INSERT INTO meeting_type VALUES (2,'SAVINGSTIMEPERFORINTCALC','Savings Time Period for Interest Calculation');
INSERT INTO meeting_type VALUES (3,'SAVINGSFRQINTPOSTACC','Savings Frequency of Interest Posting to Accounts');
INSERT INTO meeting_type VALUES (4,'CUSTOMERMEETING','Customer Meeting');
INSERT INTO meeting_type VALUES (5,'FEEMEETING','Fees Meetings');
/*!40000 ALTER TABLE meeting_type ENABLE KEYS */;
DROP TABLE IF EXISTS mfi_attribute;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE mfi_attribute (
  attribute_id smallint(6) NOT NULL,
  office_id smallint(6) NOT NULL,
  attribute_name varchar(100) NOT NULL,
  attribute_value varchar(200) NOT NULL,
  PRIMARY KEY (attribute_id),
  KEY office_id (office_id),
  CONSTRAINT mfi_attribute_ibfk_1 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE mfi_attribute DISABLE KEYS */;
INSERT INTO mfi_attribute VALUES (1,1,'CENTER','GROUP');
INSERT INTO mfi_attribute VALUES (2,1,'CENTER','GROUP');
INSERT INTO mfi_attribute VALUES (3,1,'CENTER','GROUP');
INSERT INTO mfi_attribute VALUES (4,1,'CENTER','GROUP');
INSERT INTO mfi_attribute VALUES (5,1,'CENTER','GROUP');
/*!40000 ALTER TABLE mfi_attribute ENABLE KEYS */;
DROP TABLE IF EXISTS monthly_cash_flow_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE monthly_cash_flow_details (
  id int(11) NOT NULL AUTO_INCREMENT,
  cash_flow_id int(11) DEFAULT NULL,
  month_year date DEFAULT NULL,
  revenue decimal(21,4) NOT NULL,
  expenses decimal(21,4) NOT NULL,
  notes varchar(300) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY cash_flow_id (cash_flow_id),
  CONSTRAINT monthly_cash_flow_details_ibfk_1 FOREIGN KEY (cash_flow_id) REFERENCES cash_flow (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE monthly_cash_flow_details DISABLE KEYS */;
/*!40000 ALTER TABLE monthly_cash_flow_details ENABLE KEYS */;
DROP TABLE IF EXISTS no_of_install_from_last_loan;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE no_of_install_from_last_loan (
  no_of_install_from_last_loan_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  start_range decimal(21,4) NOT NULL,
  end_range decimal(21,4) NOT NULL,
  min_no_install decimal(21,4) NOT NULL,
  max_no_install decimal(21,4) NOT NULL,
  default_no_install decimal(21,4) NOT NULL,
  PRIMARY KEY (no_of_install_from_last_loan_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT no_of_install_from_last_loan_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES loan_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE no_of_install_from_last_loan DISABLE KEYS */;
/*!40000 ALTER TABLE no_of_install_from_last_loan ENABLE KEYS */;
DROP TABLE IF EXISTS no_of_install_from_loan_cycle;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE no_of_install_from_loan_cycle (
  no_of_install_from_loan_cycle_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  min_no_install decimal(21,4) NOT NULL,
  max_no_install decimal(21,4) NOT NULL,
  default_no_install decimal(21,4) NOT NULL,
  range_index decimal(21,4) NOT NULL,
  PRIMARY KEY (no_of_install_from_loan_cycle_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT no_of_install_from_loan_cycle_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES loan_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE no_of_install_from_loan_cycle DISABLE KEYS */;
/*!40000 ALTER TABLE no_of_install_from_loan_cycle ENABLE KEYS */;
DROP TABLE IF EXISTS no_of_install_same_for_all_loan;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE no_of_install_same_for_all_loan (
  no_of_install_same_for_all_loan_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  min_no_install decimal(21,4) NOT NULL,
  max_no_install decimal(21,4) NOT NULL,
  default_no_install decimal(21,4) NOT NULL,
  PRIMARY KEY (no_of_install_same_for_all_loan_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT no_of_install_same_for_all_loan_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES loan_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE no_of_install_same_for_all_loan DISABLE KEYS */;
INSERT INTO no_of_install_same_for_all_loan VALUES (23,13,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (24,5,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (25,16,'1.0000','100.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (27,6,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (28,11,'1.0000','100.0000','4.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (29,3,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (30,4,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (31,10,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (32,12,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (34,2,'1.0000','10.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (35,17,'1.0000','100.0000','10.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (36,7,'1.0000','100.0000','4.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (37,9,'1.0000','52.0000','4.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (38,14,'1.0000','52.0000','5.0000');
INSERT INTO no_of_install_same_for_all_loan VALUES (39,15,'1.0000','100.0000','12.0000');
/*!40000 ALTER TABLE no_of_install_same_for_all_loan ENABLE KEYS */;
DROP TABLE IF EXISTS offering_fund;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE offering_fund (
  offering_fund_id smallint(6) NOT NULL,
  fund_id smallint(6) DEFAULT NULL,
  prd_offering_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (offering_fund_id),
  KEY fund_id (fund_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT offering_fund_ibfk_1 FOREIGN KEY (fund_id) REFERENCES fund (fund_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT offering_fund_ibfk_2 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE offering_fund DISABLE KEYS */;
/*!40000 ALTER TABLE offering_fund ENABLE KEYS */;
DROP TABLE IF EXISTS office;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office (
  office_id smallint(6) NOT NULL AUTO_INCREMENT,
  global_office_num varchar(100) NOT NULL,
  office_level_id smallint(6) NOT NULL,
  search_id varchar(100) NOT NULL,
  max_child_count int(11) NOT NULL,
  local_remote_flag smallint(6) NOT NULL,
  display_name varchar(200) NOT NULL,
  created_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  office_short_name varchar(4) NOT NULL,
  parent_office_id smallint(6) DEFAULT NULL,
  status_id smallint(6) NOT NULL,
  version_no int(11) NOT NULL,
  office_code_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (office_id),
  UNIQUE KEY global_office_num (global_office_num),
  UNIQUE KEY office_global_idx (global_office_num),
  KEY office_level_id (office_level_id),
  KEY parent_office_id (parent_office_id),
  KEY status_id (status_id),
  KEY office_code_id (office_code_id),
  CONSTRAINT office_ibfk_1 FOREIGN KEY (office_level_id) REFERENCES office_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_ibfk_2 FOREIGN KEY (parent_office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_ibfk_3 FOREIGN KEY (status_id) REFERENCES office_status (status_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_ibfk_4 FOREIGN KEY (office_code_id) REFERENCES office_code (code_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office DISABLE KEYS */;
INSERT INTO office VALUES (1,'0001',1,'1.1.',2,1,'Mifos HO ',NULL,NULL,NULL,NULL,'MIF1',NULL,1,1,NULL);
INSERT INTO office VALUES (2,'0002',5,'1.1.1.',0,1,'MyOfficeDHMFT',1,'2011-02-18',NULL,NULL,'DHM',1,1,0,NULL);
INSERT INTO office VALUES (3,'0003',5,'1.1.2.',0,1,'branch1',1,'2011-03-03',NULL,NULL,'b1',1,1,0,NULL);
INSERT INTO office VALUES (4,'0004',5,'1.1.3.',0,1,'branch2',1,'2011-03-14',NULL,NULL,'b2',1,1,0,NULL);
/*!40000 ALTER TABLE office ENABLE KEYS */;
DROP TABLE IF EXISTS office_action_payment_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_action_payment_type (
  office_id smallint(6) DEFAULT NULL,
  prd_type_id smallint(6) DEFAULT NULL,
  account_action_id smallint(6) NOT NULL,
  payment_type_id smallint(6) DEFAULT NULL,
  KEY account_action_id (account_action_id),
  KEY office_id (office_id),
  KEY payment_type_id (payment_type_id),
  KEY prd_type_id (prd_type_id),
  CONSTRAINT office_action_payment_type_ibfk_1 FOREIGN KEY (account_action_id) REFERENCES account_action (account_action_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_action_payment_type_ibfk_2 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_action_payment_type_ibfk_3 FOREIGN KEY (payment_type_id) REFERENCES payment_type (payment_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_action_payment_type_ibfk_4 FOREIGN KEY (prd_type_id) REFERENCES prd_type (prd_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_action_payment_type DISABLE KEYS */;
INSERT INTO office_action_payment_type VALUES (NULL,NULL,1,NULL);
INSERT INTO office_action_payment_type VALUES (NULL,NULL,2,NULL);
INSERT INTO office_action_payment_type VALUES (NULL,NULL,3,NULL);
INSERT INTO office_action_payment_type VALUES (NULL,NULL,4,NULL);
INSERT INTO office_action_payment_type VALUES (NULL,NULL,5,NULL);
/*!40000 ALTER TABLE office_action_payment_type ENABLE KEYS */;
DROP TABLE IF EXISTS office_address;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_address (
  office_address_id smallint(6) NOT NULL AUTO_INCREMENT,
  office_id smallint(6) NOT NULL,
  address_1 varchar(200) DEFAULT NULL,
  address_2 varchar(200) DEFAULT NULL,
  address_3 varchar(200) DEFAULT NULL,
  city varchar(100) DEFAULT NULL,
  state varchar(100) DEFAULT NULL,
  country varchar(100) DEFAULT NULL,
  zip varchar(20) DEFAULT NULL,
  telephone varchar(20) DEFAULT NULL,
  PRIMARY KEY (office_address_id),
  KEY office_id (office_id),
  CONSTRAINT office_address_ibfk_1 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_address DISABLE KEYS */;
INSERT INTO office_address VALUES (1,2,'','','','','','','','');
INSERT INTO office_address VALUES (2,3,'','','','','','','','');
INSERT INTO office_address VALUES (3,4,'','','','','','','','');
/*!40000 ALTER TABLE office_address ENABLE KEYS */;
DROP TABLE IF EXISTS office_code;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_code (
  code_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (code_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT office_code_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_code DISABLE KEYS */;
INSERT INTO office_code VALUES (1,111);
INSERT INTO office_code VALUES (2,112);
/*!40000 ALTER TABLE office_code ENABLE KEYS */;
DROP TABLE IF EXISTS office_custom_field;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_custom_field (
  office_custom_field_id int(11) NOT NULL AUTO_INCREMENT,
  office_id smallint(6) NOT NULL,
  field_id smallint(6) NOT NULL,
  field_value varchar(200) DEFAULT NULL,
  PRIMARY KEY (office_custom_field_id),
  KEY office_id (office_id),
  CONSTRAINT office_custom_field_ibfk_1 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_custom_field DISABLE KEYS */;
/*!40000 ALTER TABLE office_custom_field ENABLE KEYS */;
DROP TABLE IF EXISTS office_hierarchy;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_hierarchy (
  hierarchy_id int(11) NOT NULL AUTO_INCREMENT,
  parent_id smallint(6) NOT NULL,
  office_id smallint(6) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  PRIMARY KEY (hierarchy_id),
  KEY parent_id (parent_id),
  KEY updated_by (updated_by),
  KEY office_hierarchy_idx (office_id,`status`),
  CONSTRAINT office_hierarchy_ibfk_1 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_hierarchy_ibfk_2 FOREIGN KEY (parent_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_hierarchy_ibfk_3 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_hierarchy DISABLE KEYS */;
/*!40000 ALTER TABLE office_hierarchy ENABLE KEYS */;
DROP TABLE IF EXISTS office_holiday;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_holiday (
  office_id smallint(6) NOT NULL DEFAULT '0',
  holiday_id int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (office_id,holiday_id),
  KEY holiday_id (holiday_id),
  CONSTRAINT office_holiday_ibfk_1 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT office_holiday_ibfk_2 FOREIGN KEY (holiday_id) REFERENCES holiday (holiday_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_holiday DISABLE KEYS */;
/*!40000 ALTER TABLE office_holiday ENABLE KEYS */;
DROP TABLE IF EXISTS office_level;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_level (
  level_id smallint(6) NOT NULL,
  parent_level_id smallint(6) DEFAULT NULL,
  level_name_id smallint(6) DEFAULT NULL,
  interaction_flag smallint(6) DEFAULT NULL,
  configured smallint(6) NOT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (level_id),
  KEY parent_level_id (parent_level_id),
  CONSTRAINT office_level_ibfk_1 FOREIGN KEY (parent_level_id) REFERENCES office_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_level DISABLE KEYS */;
INSERT INTO office_level VALUES (1,NULL,104,0,1,NULL);
INSERT INTO office_level VALUES (2,1,105,0,1,NULL);
INSERT INTO office_level VALUES (3,2,106,0,1,NULL);
INSERT INTO office_level VALUES (4,3,107,0,1,NULL);
INSERT INTO office_level VALUES (5,4,108,1,1,NULL);
/*!40000 ALTER TABLE office_level ENABLE KEYS */;
DROP TABLE IF EXISTS office_status;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE office_status (
  status_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (status_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT office_status_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_status DISABLE KEYS */;
INSERT INTO office_status VALUES (1,15);
INSERT INTO office_status VALUES (2,16);
/*!40000 ALTER TABLE office_status ENABLE KEYS */;
DROP TABLE IF EXISTS original_loan_fee_schedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE original_loan_fee_schedule (
  account_fees_detail_id int(11) NOT NULL AUTO_INCREMENT,
  id int(11) NOT NULL,
  installment_id int(11) NOT NULL,
  fee_id smallint(6) NOT NULL,
  account_fee_id int(11) NOT NULL,
  amount decimal(21,4) DEFAULT NULL,
  amount_currency_id smallint(6) DEFAULT NULL,
  amount_paid decimal(21,4) NOT NULL,
  amount_paid_currency_id smallint(6) NOT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (account_fees_detail_id),
  KEY id (id),
  KEY amount_currency_id (amount_currency_id),
  KEY amount_paid_currency_id (amount_paid_currency_id),
  KEY fee_id (fee_id),
  KEY account_fee_id (account_fee_id),
  CONSTRAINT original_loan_fee_schedule_ibfk_1 FOREIGN KEY (id) REFERENCES original_loan_schedule (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_fee_schedule_ibfk_2 FOREIGN KEY (amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_fee_schedule_ibfk_3 FOREIGN KEY (amount_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_fee_schedule_ibfk_4 FOREIGN KEY (fee_id) REFERENCES fees (fee_id),
  CONSTRAINT original_loan_fee_schedule_ibfk_5 FOREIGN KEY (account_fee_id) REFERENCES account_fees (account_fee_id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE original_loan_fee_schedule DISABLE KEYS */;
INSERT INTO original_loan_fee_schedule VALUES (1,86,1,1,13,'10.0000',2,'0.0000',2,0);
/*!40000 ALTER TABLE original_loan_fee_schedule ENABLE KEYS */;
DROP TABLE IF EXISTS original_loan_schedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE original_loan_schedule (
  id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  customer_id int(11) NOT NULL,
  currency_id smallint(6) DEFAULT NULL,
  action_date date DEFAULT NULL,
  principal decimal(21,4) NOT NULL,
  principal_currency_id smallint(6) DEFAULT NULL,
  interest decimal(21,4) NOT NULL,
  interest_currency_id smallint(6) DEFAULT NULL,
  penalty decimal(21,4) NOT NULL,
  penalty_currency_id smallint(6) DEFAULT NULL,
  misc_fees decimal(21,4) DEFAULT NULL,
  misc_fees_currency_id smallint(6) DEFAULT NULL,
  misc_fees_paid decimal(21,4) DEFAULT NULL,
  misc_fees_paid_currency_id smallint(6) DEFAULT NULL,
  misc_penalty decimal(21,4) DEFAULT NULL,
  misc_penalty_currency_id smallint(6) DEFAULT NULL,
  misc_penalty_paid decimal(21,4) DEFAULT NULL,
  misc_penalty_paid_currency_id smallint(6) DEFAULT NULL,
  principal_paid decimal(21,4) DEFAULT NULL,
  principal_paid_currency_id smallint(6) DEFAULT NULL,
  interest_paid decimal(21,4) DEFAULT NULL,
  interest_paid_currency_id smallint(6) DEFAULT NULL,
  penalty_paid decimal(21,4) DEFAULT NULL,
  penalty_paid_currency_id smallint(6) DEFAULT NULL,
  payment_status smallint(6) NOT NULL,
  installment_id smallint(6) NOT NULL,
  payment_date date DEFAULT NULL,
  parent_flag smallint(6) DEFAULT NULL,
  version_no int(11) NOT NULL,
  extra_interest decimal(21,4) DEFAULT NULL,
  extra_interest_currency_id smallint(6) DEFAULT NULL,
  extra_interest_paid decimal(21,4) DEFAULT NULL,
  extra_interest_paid_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY account_id (account_id),
  KEY currency_id (currency_id),
  KEY principal_currency_id (principal_currency_id),
  KEY interest_currency_id (interest_currency_id),
  KEY penalty_currency_id (penalty_currency_id),
  KEY misc_fees_currency_id (misc_fees_currency_id),
  KEY misc_fees_paid_currency_id (misc_fees_paid_currency_id),
  KEY misc_penalty_currency_id (misc_penalty_currency_id),
  KEY principal_paid_currency_id (principal_paid_currency_id),
  KEY interest_paid_currency_id (interest_paid_currency_id),
  KEY penalty_paid_currency_id (penalty_paid_currency_id),
  KEY misc_penalty_paid_currency_id (misc_penalty_paid_currency_id),
  KEY customer_id (customer_id),
  KEY extra_interest_currency_id (extra_interest_currency_id),
  KEY extra_interest_paid_currency_id (extra_interest_paid_currency_id),
  CONSTRAINT original_loan_schedule_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_3 FOREIGN KEY (principal_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_4 FOREIGN KEY (interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_5 FOREIGN KEY (penalty_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_6 FOREIGN KEY (misc_fees_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_7 FOREIGN KEY (misc_fees_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_8 FOREIGN KEY (misc_penalty_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_9 FOREIGN KEY (principal_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_10 FOREIGN KEY (interest_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_11 FOREIGN KEY (penalty_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_12 FOREIGN KEY (misc_penalty_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_13 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_14 FOREIGN KEY (extra_interest_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT original_loan_schedule_ibfk_15 FOREIGN KEY (extra_interest_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE original_loan_schedule DISABLE KEYS */;
INSERT INTO original_loan_schedule VALUES (1,10,5,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (2,10,5,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (3,10,5,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (4,10,5,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (5,10,5,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (6,10,5,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (7,10,5,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (8,10,5,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (9,10,5,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (10,10,5,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (11,15,10,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (12,15,10,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (13,15,10,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (14,15,10,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (15,15,10,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (16,15,10,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (17,15,10,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (18,15,10,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (19,15,10,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (20,15,10,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (21,12,6,NULL,'2010-02-26','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (22,12,6,NULL,'2010-03-05','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (23,12,6,NULL,'2010-03-12','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (24,12,6,NULL,'2010-03-19','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (25,12,6,NULL,'2010-03-26','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (26,12,6,NULL,'2010-04-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (27,12,6,NULL,'2010-04-09','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (28,12,6,NULL,'2010-04-16','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (29,12,6,NULL,'2010-04-23','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (30,12,6,NULL,'2010-04-30','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (31,25,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (32,25,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (33,25,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (34,25,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (35,26,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (36,26,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (37,26,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (38,26,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (39,27,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (40,27,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (41,27,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (42,27,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (43,28,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (44,28,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (45,28,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (46,28,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (47,29,18,NULL,'2010-10-19','197.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (48,29,18,NULL,'2010-10-26','199.3074',2,'3.6926',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (49,29,18,NULL,'2010-11-02','200.2248',2,'2.7752',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (50,29,18,NULL,'2010-11-09','201.1463',2,'1.8537',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (51,29,18,NULL,'2010-11-16','201.5818',2,'0.9278',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (52,37,24,NULL,'2011-03-07','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (53,37,24,NULL,'2011-03-14','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (54,37,24,NULL,'2011-03-21','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (55,37,24,NULL,'2011-03-28','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (56,39,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (57,39,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (58,39,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (59,39,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (60,39,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (61,39,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (62,39,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (63,39,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (64,39,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (65,39,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (66,40,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (67,40,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (68,40,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (69,40,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (70,40,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (71,40,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (72,40,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (73,40,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (74,40,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (75,40,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (76,41,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (77,41,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (78,41,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (79,41,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (80,41,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (81,41,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (82,41,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (83,41,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (84,41,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (85,41,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (86,42,4,NULL,'2011-03-04','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (87,42,4,NULL,'2011-03-11','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (88,42,4,NULL,'2011-03-18','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (89,42,4,NULL,'2011-03-25','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (90,47,24,NULL,'2011-03-07','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (91,47,24,NULL,'2011-03-14','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (92,47,24,NULL,'2011-03-21','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (93,47,24,NULL,'2011-03-28','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (94,47,24,NULL,'2011-04-04','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (95,47,24,NULL,'2011-04-11','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (96,47,24,NULL,'2011-04-18','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (97,47,24,NULL,'2011-04-25','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (98,47,24,NULL,'2011-05-02','1000.0000',2,'46.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO original_loan_schedule VALUES (99,47,24,NULL,'2011-05-09','1000.0000',2,'47.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE original_loan_schedule ENABLE KEYS */;
DROP TABLE IF EXISTS payment_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE payment_type (
  payment_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  payment_type_lookup_id int(11) DEFAULT NULL,
  PRIMARY KEY (payment_type_id),
  KEY payment_type_lookup_id (payment_type_lookup_id),
  CONSTRAINT payment_type_ibfk_1 FOREIGN KEY (payment_type_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE payment_type DISABLE KEYS */;
INSERT INTO payment_type VALUES (1,177);
INSERT INTO payment_type VALUES (2,179);
INSERT INTO payment_type VALUES (3,180);
/*!40000 ALTER TABLE payment_type ENABLE KEYS */;
DROP TABLE IF EXISTS penalty;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE penalty (
  penalty_id smallint(6) NOT NULL,
  global_penalty_num varchar(100) DEFAULT NULL,
  penalty_type varchar(100) DEFAULT NULL,
  office_id smallint(6) DEFAULT NULL,
  category_id smallint(6) DEFAULT NULL,
  glcode_id smallint(6) NOT NULL,
  lookup_id int(11) DEFAULT NULL,
  rate decimal(13,10) NOT NULL,
  formula varchar(100) DEFAULT NULL,
  PRIMARY KEY (penalty_id),
  KEY category_id (category_id),
  KEY glcode_id (glcode_id),
  KEY lookup_id (lookup_id),
  KEY office_id (office_id),
  CONSTRAINT penalty_ibfk_1 FOREIGN KEY (category_id) REFERENCES category_type (category_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT penalty_ibfk_2 FOREIGN KEY (glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT penalty_ibfk_3 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT penalty_ibfk_4 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE penalty DISABLE KEYS */;
/*!40000 ALTER TABLE penalty ENABLE KEYS */;
DROP TABLE IF EXISTS personnel;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel (
  personnel_id smallint(6) NOT NULL AUTO_INCREMENT,
  level_id smallint(6) NOT NULL,
  global_personnel_num varchar(100) DEFAULT NULL,
  office_id smallint(6) DEFAULT NULL,
  title int(11) DEFAULT NULL,
  personnel_status smallint(6) DEFAULT NULL,
  preferred_locale smallint(6) DEFAULT NULL,
  search_id varchar(100) DEFAULT NULL,
  max_child_count int(11) DEFAULT NULL,
  `password` tinyblob,
  login_name varchar(200) DEFAULT NULL,
  email_id varchar(255) DEFAULT NULL,
  password_changed smallint(6) NOT NULL,
  display_name varchar(200) DEFAULT NULL,
  created_by smallint(6) NOT NULL,
  created_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  last_login date DEFAULT NULL,
  locked smallint(6) NOT NULL,
  no_of_tries smallint(6) NOT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (personnel_id),
  UNIQUE KEY personnel_global_idx (global_personnel_num),
  UNIQUE KEY personnel_search_idx (search_id),
  UNIQUE KEY personnel_login_idx (login_name),
  KEY created_by (created_by),
  KEY level_id (level_id),
  KEY preferred_locale (preferred_locale),
  KEY title (title),
  KEY updated_by (updated_by),
  KEY personnel_office_idx (office_id),
  CONSTRAINT personnel_ibfk_1 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_ibfk_2 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_ibfk_3 FOREIGN KEY (level_id) REFERENCES personnel_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_ibfk_4 FOREIGN KEY (preferred_locale) REFERENCES supported_locale (locale_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_ibfk_5 FOREIGN KEY (title) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_ibfk_6 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel DISABLE KEYS */;
INSERT INTO personnel VALUES (1,2,'1',1,1,1,1,NULL,1,'\"d��#0�a�M�2�Nj����d�QS8�','mifos',NULL,1,'mifos',1,NULL,1,'2011-03-14','2011-03-14',0,0,23);
INSERT INTO personnel VALUES (2,1,'0002-00002',2,NULL,1,1,NULL,NULL,0x22648ca42330a561964dbe32f54e6a04a49d03f203ca64b2515338d9,'loanofficer','',1,'loan officer',1,'2011-02-18',2,'2011-11-25','2011-11-25',0,0,2);
INSERT INTO personnel VALUES (3,1,'0003-00003',3,NULL,1,1,NULL,NULL,0x22648ca42330a561964dbe32f54e6a04a49d03f203ca64b2515338d9,'loanofficerbranch1','',1,'loanofficer branch1',1,'2011-03-03',3,'2011-11-25','2011-11-25',0,0,4);
INSERT INTO personnel VALUES (4,1,'0004-00004',4,NULL,1,1,NULL,NULL,'ͱ~�y9��I�(�P\\bc4&I�','loanofficerbranch2','',0,'loanofficerbranch2 loanofficerbranch2',1,'2011-03-14',NULL,NULL,NULL,0,0,1);
/*!40000 ALTER TABLE personnel ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_custom_field;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_custom_field (
  personnel_custom_field_id int(11) NOT NULL AUTO_INCREMENT,
  field_id smallint(6) NOT NULL,
  personnel_id smallint(6) NOT NULL,
  field_value varchar(100) DEFAULT NULL,
  PRIMARY KEY (personnel_custom_field_id),
  KEY personnel_id (personnel_id),
  KEY field_id (field_id),
  CONSTRAINT personnel_custom_field_ibfk_1 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_custom_field_ibfk_2 FOREIGN KEY (field_id) REFERENCES custom_field_definition (field_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_custom_field DISABLE KEYS */;
/*!40000 ALTER TABLE personnel_custom_field ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_details (
  personnel_id smallint(6) NOT NULL,
  first_name varchar(100) NOT NULL,
  middle_name varchar(100) DEFAULT NULL,
  second_last_name varchar(100) DEFAULT NULL,
  last_name varchar(100) DEFAULT NULL,
  government_id_number varchar(50) DEFAULT NULL,
  dob date NOT NULL,
  marital_status int(11) DEFAULT NULL,
  gender int(11) NOT NULL,
  date_of_joining_mfi date DEFAULT NULL,
  date_of_joining_branch date DEFAULT NULL,
  date_of_leaving_branch date DEFAULT NULL,
  address_1 varchar(200) DEFAULT NULL,
  address_2 varchar(200) DEFAULT NULL,
  address_3 varchar(200) DEFAULT NULL,
  city varchar(100) DEFAULT NULL,
  state varchar(100) DEFAULT NULL,
  country varchar(100) DEFAULT NULL,
  postal_code varchar(100) DEFAULT NULL,
  telephone varchar(20) DEFAULT NULL,
  KEY personnel_id (personnel_id),
  KEY gender (gender),
  KEY marital_status (marital_status),
  CONSTRAINT personnel_details_ibfk_1 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_details_ibfk_2 FOREIGN KEY (gender) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_details_ibfk_3 FOREIGN KEY (marital_status) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_details DISABLE KEYS */;
INSERT INTO personnel_details VALUES (1,'Mifos',NULL,NULL,'MFI','123','1979-12-12',NULL,50,NULL,NULL,NULL,'Bangalore',NULL,NULL,'Bangalore','Bangalore','Bangalore',NULL,NULL);
INSERT INTO personnel_details VALUES (2,'loan','','','officer','','1990-01-01',NULL,49,'2005-02-18','2011-02-18',NULL,'','','','','','','','');
INSERT INTO personnel_details VALUES (3,'loanofficer','','','branch1','','1990-01-01',NULL,49,'2011-03-03','2011-03-03',NULL,'','','','','','','','');
INSERT INTO personnel_details VALUES (4,'loanofficerbranch2','','','loanofficerbranch2','','1942-12-21',NULL,49,'2011-03-14','2011-03-14',NULL,'','','','','','','','');
/*!40000 ALTER TABLE personnel_details ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_hierarchy;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_hierarchy (
  hierarchy_id int(11) NOT NULL,
  parent_id smallint(6) NOT NULL,
  personnel_id smallint(6) DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date NOT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  PRIMARY KEY (hierarchy_id),
  KEY parent_id (parent_id),
  KEY updated_by (updated_by),
  KEY personnel_hierarchy_idx (personnel_id,`status`),
  CONSTRAINT personnel_hierarchy_ibfk_1 FOREIGN KEY (parent_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_hierarchy_ibfk_2 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_hierarchy_ibfk_3 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_hierarchy DISABLE KEYS */;
/*!40000 ALTER TABLE personnel_hierarchy ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_level;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_level (
  level_id smallint(6) NOT NULL,
  level_name_id int(11) NOT NULL,
  parent_level_id smallint(6) DEFAULT NULL,
  interaction_flag smallint(6) DEFAULT NULL,
  PRIMARY KEY (level_id),
  KEY parent_level_id (parent_level_id),
  KEY level_name_id (level_name_id),
  CONSTRAINT personnel_level_ibfk_1 FOREIGN KEY (parent_level_id) REFERENCES personnel_level (level_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_level_ibfk_2 FOREIGN KEY (level_name_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_level DISABLE KEYS */;
INSERT INTO personnel_level VALUES (1,60,1,0);
INSERT INTO personnel_level VALUES (2,61,1,0);
/*!40000 ALTER TABLE personnel_level ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_movement;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_movement (
  personnel_movement_id smallint(6) NOT NULL AUTO_INCREMENT,
  personnel_id smallint(6) DEFAULT NULL,
  office_id smallint(6) NOT NULL,
  `status` smallint(6) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  PRIMARY KEY (personnel_movement_id),
  KEY updated_by (updated_by),
  KEY office_id (office_id),
  KEY personnel_movement_idx (personnel_id),
  CONSTRAINT personnel_movement_ibfk_1 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_movement_ibfk_2 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_movement_ibfk_3 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_movement DISABLE KEYS */;
/*!40000 ALTER TABLE personnel_movement ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_notes;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_notes (
  comment_id int(11) NOT NULL AUTO_INCREMENT,
  personnel_id smallint(6) NOT NULL,
  comment_date date NOT NULL,
  comments varchar(500) NOT NULL,
  officer_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (comment_id),
  KEY personnel_id (personnel_id),
  KEY officer_id (officer_id),
  CONSTRAINT personnel_notes_ibfk_1 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_notes_ibfk_2 FOREIGN KEY (officer_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_notes DISABLE KEYS */;
/*!40000 ALTER TABLE personnel_notes ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_role;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_role (
  personnel_role_id int(11) NOT NULL AUTO_INCREMENT,
  role_id smallint(6) NOT NULL,
  personnel_id smallint(6) NOT NULL,
  PRIMARY KEY (personnel_role_id),
  KEY personnel_id (personnel_id),
  KEY role_id (role_id),
  CONSTRAINT personnel_role_ibfk_1 FOREIGN KEY (personnel_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT personnel_role_ibfk_2 FOREIGN KEY (role_id) REFERENCES role (role_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_role DISABLE KEYS */;
INSERT INTO personnel_role VALUES (1,1,1);
/*!40000 ALTER TABLE personnel_role ENABLE KEYS */;
DROP TABLE IF EXISTS personnel_status;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE personnel_status (
  personnel_status_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (personnel_status_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT personnel_status_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel_status DISABLE KEYS */;
INSERT INTO personnel_status VALUES (1,152);
INSERT INTO personnel_status VALUES (2,153);
/*!40000 ALTER TABLE personnel_status ENABLE KEYS */;
DROP TABLE IF EXISTS position;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE position (
  position_id int(11) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (position_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT position_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE position DISABLE KEYS */;
INSERT INTO position VALUES (1,186);
INSERT INTO position VALUES (2,187);
INSERT INTO position VALUES (3,188);
INSERT INTO position VALUES (4,216);
/*!40000 ALTER TABLE position ENABLE KEYS */;
DROP TABLE IF EXISTS ppi_likelihoods;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ppi_likelihoods (
  likelihood_id int(11) NOT NULL AUTO_INCREMENT,
  survey_id int(11) NOT NULL,
  score_from int(11) NOT NULL,
  score_to int(11) NOT NULL,
  bottom_half_below decimal(21,4) NOT NULL,
  top_half_below decimal(21,4) NOT NULL,
  likelihood_order int(11) NOT NULL,
  PRIMARY KEY (likelihood_id),
  KEY survey_id (survey_id),
  CONSTRAINT ppi_likelihoods_ibfk_1 FOREIGN KEY (survey_id) REFERENCES survey (survey_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE ppi_likelihoods DISABLE KEYS */;
/*!40000 ALTER TABLE ppi_likelihoods ENABLE KEYS */;
DROP TABLE IF EXISTS ppi_survey;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ppi_survey (
  country_id int(11) NOT NULL,
  survey_id int(11) NOT NULL,
  very_poor_min int(11) NOT NULL,
  very_poor_max int(11) NOT NULL,
  poor_min int(11) NOT NULL,
  poor_max int(11) NOT NULL,
  at_risk_min int(11) NOT NULL,
  at_risk_max int(11) NOT NULL,
  non_poor_min int(11) NOT NULL,
  non_poor_max int(11) NOT NULL,
  PRIMARY KEY (country_id),
  KEY survey_id (survey_id),
  CONSTRAINT ppi_survey_ibfk_1 FOREIGN KEY (survey_id) REFERENCES survey (survey_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE ppi_survey DISABLE KEYS */;
/*!40000 ALTER TABLE ppi_survey ENABLE KEYS */;
DROP TABLE IF EXISTS ppi_survey_instance;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ppi_survey_instance (
  instance_id int(11) NOT NULL,
  bottom_half_below decimal(21,4) DEFAULT NULL,
  top_half_below decimal(21,4) DEFAULT NULL,
  PRIMARY KEY (instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE ppi_survey_instance DISABLE KEYS */;
/*!40000 ALTER TABLE ppi_survey_instance ENABLE KEYS */;
DROP TABLE IF EXISTS prd_applicable_master;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_applicable_master (
  prd_applicable_master_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (prd_applicable_master_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT prd_applicable_master_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_applicable_master DISABLE KEYS */;
INSERT INTO prd_applicable_master VALUES (1,68);
INSERT INTO prd_applicable_master VALUES (2,69);
INSERT INTO prd_applicable_master VALUES (3,70);
/*!40000 ALTER TABLE prd_applicable_master ENABLE KEYS */;
DROP TABLE IF EXISTS prd_category;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_category (
  prd_category_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_type_id smallint(6) NOT NULL,
  global_prd_offering_num varchar(50) NOT NULL,
  prd_category_name varchar(100) NOT NULL,
  created_date date DEFAULT NULL,
  created_by int(11) DEFAULT NULL,
  office_id smallint(6) DEFAULT NULL,
  updated_by int(11) DEFAULT NULL,
  udpated_date date DEFAULT NULL,
  state smallint(6) NOT NULL,
  description varchar(500) DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (prd_category_id),
  KEY prd_type_id (prd_type_id),
  CONSTRAINT prd_category_ibfk_1 FOREIGN KEY (prd_type_id) REFERENCES prd_type (prd_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_category DISABLE KEYS */;
INSERT INTO prd_category VALUES (1,1,'1-1','Other',NULL,NULL,NULL,NULL,NULL,1,NULL,1);
INSERT INTO prd_category VALUES (2,2,'1-2','Other',NULL,NULL,NULL,NULL,NULL,1,NULL,1);
/*!40000 ALTER TABLE prd_category ENABLE KEYS */;
DROP TABLE IF EXISTS prd_category_status;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_category_status (
  prd_category_status_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (prd_category_status_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT prd_category_status_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_category_status DISABLE KEYS */;
INSERT INTO prd_category_status VALUES (2,113);
INSERT INTO prd_category_status VALUES (1,114);
/*!40000 ALTER TABLE prd_category_status ENABLE KEYS */;
DROP TABLE IF EXISTS prd_checklist;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_checklist (
  checklist_id smallint(6) NOT NULL,
  prd_type_id smallint(6) DEFAULT NULL,
  account_status smallint(6) NOT NULL,
  PRIMARY KEY (checklist_id),
  KEY account_status (account_status),
  KEY prd_type_id (prd_type_id),
  CONSTRAINT prd_checklist_ibfk_1 FOREIGN KEY (account_status) REFERENCES account_state (account_state_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_checklist_ibfk_2 FOREIGN KEY (checklist_id) REFERENCES checklist (checklist_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_checklist_ibfk_3 FOREIGN KEY (prd_type_id) REFERENCES prd_type (prd_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_checklist DISABLE KEYS */;
/*!40000 ALTER TABLE prd_checklist ENABLE KEYS */;
DROP TABLE IF EXISTS prd_fee_frequency;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_fee_frequency (
  prdoffering_fee_id smallint(6) NOT NULL,
  fee_id smallint(6) DEFAULT NULL,
  frequency_id smallint(6) NOT NULL,
  PRIMARY KEY (prdoffering_fee_id),
  KEY fee_id (fee_id),
  KEY frequency_id (frequency_id),
  CONSTRAINT prd_fee_frequency_ibfk_1 FOREIGN KEY (fee_id) REFERENCES fees (fee_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_fee_frequency_ibfk_2 FOREIGN KEY (prdoffering_fee_id) REFERENCES prd_offering_fees (prd_offering_fee_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_fee_frequency_ibfk_3 FOREIGN KEY (frequency_id) REFERENCES recurrence_type (recurrence_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_fee_frequency DISABLE KEYS */;
/*!40000 ALTER TABLE prd_fee_frequency ENABLE KEYS */;
DROP TABLE IF EXISTS prd_offering;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_offering (
  prd_offering_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_applicable_master_id smallint(6) NOT NULL,
  global_prd_offering_num varchar(50) NOT NULL,
  prd_category_id smallint(6) NOT NULL,
  prd_type_id smallint(6) DEFAULT NULL,
  office_id smallint(6) DEFAULT NULL,
  start_date date NOT NULL,
  end_date date DEFAULT NULL,
  glcode_id smallint(6) DEFAULT NULL,
  prd_offering_name varchar(50) NOT NULL,
  prd_offering_short_name varchar(50) NOT NULL,
  offering_status_id smallint(6) DEFAULT NULL,
  description varchar(200) DEFAULT NULL,
  created_date date NOT NULL,
  created_by int(11) NOT NULL,
  updated_date date DEFAULT NULL,
  updated_by int(11) DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  prd_mix_flag smallint(6) DEFAULT NULL,
  currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (prd_offering_id),
  UNIQUE KEY prd_offering_global_idx (global_prd_offering_num),
  UNIQUE KEY prd_offering_name_idx (prd_offering_name),
  UNIQUE KEY prd_offering_short_name_idx (prd_offering_short_name),
  KEY glcode_id (glcode_id),
  KEY prd_category_id (prd_category_id),
  KEY offering_status_id (offering_status_id),
  KEY prd_applicable_master_id (prd_applicable_master_id),
  KEY currency_id (currency_id),
  KEY prd_offering_office_idx (office_id),
  KEY prd_type_idx (prd_type_id),
  CONSTRAINT prd_offering_ibfk_1 FOREIGN KEY (glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_ibfk_2 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_ibfk_3 FOREIGN KEY (prd_category_id) REFERENCES prd_category (prd_category_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_ibfk_4 FOREIGN KEY (offering_status_id) REFERENCES prd_status (offering_status_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_ibfk_5 FOREIGN KEY (prd_type_id) REFERENCES prd_type (prd_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_ibfk_6 FOREIGN KEY (prd_applicable_master_id) REFERENCES prd_applicable_master (prd_applicable_master_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_ibfk_7 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering DISABLE KEYS */;
INSERT INTO prd_offering VALUES (1,1,'1-001',2,2,NULL,'2009-01-01',NULL,NULL,'MonthlyClientSavingsAccount','s1',2,'','2009-01-01',1,NULL,NULL,0,NULL,2);
INSERT INTO prd_offering VALUES (2,1,'1-002',1,1,1,'2010-01-22',NULL,NULL,'WeeklyFlatLoanWithOneTimeFees','flwf',1,'','2010-01-22',1,'2011-03-04',1,4,NULL,2);
INSERT INTO prd_offering VALUES (3,1,'1-003',1,1,1,'2010-01-22',NULL,NULL,'MonthlyClientFlatLoan1stOfMonth','mcf1',1,'','2010-01-22',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (4,1,'1-004',1,1,1,'2010-01-22',NULL,NULL,'MonthlyClientFlatLoanThirdFridayOfMonth','mcf3',1,'','2010-01-22',1,'2011-03-04',1,4,NULL,2);
INSERT INTO prd_offering VALUES (5,1,'1-005',1,1,1,'2011-02-21',NULL,NULL,'ClientEmergencyLoan','EL',1,'','2011-02-21',1,'2011-03-04',1,4,NULL,2);
INSERT INTO prd_offering VALUES (6,2,'1-006',1,1,1,'2011-02-21',NULL,NULL,'GroupEmergencyLoan','GEL',1,'','2011-02-21',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (7,2,'1-007',1,1,1,'2011-02-22',NULL,NULL,'WeeklyGroupFlatLoanWithOnetimeFee','wgff',1,'','2011-02-22',1,'2011-03-04',1,6,NULL,2);
INSERT INTO prd_offering VALUES (8,1,'1-008',2,2,NULL,'2011-02-25',NULL,NULL,'MandatorySavingsAccount','MSA',2,'','2011-02-25',1,'2011-02-25',1,1,NULL,2);
INSERT INTO prd_offering VALUES (9,1,'1-009',1,1,1,'2010-10-11',NULL,NULL,'WeeklyPawdepLoan','wpl',1,'','2010-10-11',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (10,2,'1-010',1,1,1,'2011-02-25',NULL,NULL,'MonthlyGroupFlatLoan1stOfMonth','mgf1',1,'','2011-02-25',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (11,1,'1-011',1,1,1,'2011-02-28',NULL,NULL,'InterestWaiverLoan','iwl',1,'','2011-02-28',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (12,1,'1-012',1,1,1,'2011-02-22',NULL,NULL,'WeeklyClientFlatLoanWithNoFee','wcl1',1,'','2011-02-22',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (13,1,'1-013',1,1,1,'2011-02-22',NULL,NULL,'AnotherWeeklyClientFlatLoanWithNoFee','wcl2',1,'','2011-02-22',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (14,1,'1-014',1,1,1,'2010-10-11',NULL,NULL,'WeeklyClientVariableInstallmentsLoan','wcvi',1,'','2010-10-11',1,'2010-10-11',1,4,NULL,2);
INSERT INTO prd_offering VALUES (15,1,'1-015',1,1,1,'2011-01-01',NULL,NULL,'Flat Interest Loan Product With Fee','lpwf',1,'','2011-01-01',1,'2011-01-01',1,4,NULL,2);
INSERT INTO prd_offering VALUES (16,1,'1-016',1,1,1,'2011-01-01',NULL,NULL,'EmergencyLoanWithZeroInterest','elzi',1,'','2011-01-01',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (17,2,'1-017',1,1,1,'2011-01-01',NULL,NULL,'WeeklyGroupDeclineLoanWithPeriodicFee','wgdp',1,'','2011-01-01',1,'2011-03-04',1,3,NULL,2);
INSERT INTO prd_offering VALUES (18,1,'1-018',2,2,NULL,'2009-01-29',NULL,NULL,'SavingsProductWithInterestOnMonthlyAvgBalance','spam',2,'','2009-01-29',1,NULL,NULL,0,NULL,2);
INSERT INTO prd_offering VALUES (19,1,'1-019',2,2,NULL,'2011-03-14',NULL,NULL,'NewMonthlyClientSavingsAccount','nmcs',2,'','2011-03-14',1,NULL,NULL,0,NULL,2);
/*!40000 ALTER TABLE prd_offering ENABLE KEYS */;
DROP TABLE IF EXISTS prd_offering_fees;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_offering_fees (
  prd_offering_fee_id smallint(6) NOT NULL AUTO_INCREMENT,
  fee_id smallint(6) DEFAULT NULL,
  prd_offering_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (prd_offering_fee_id),
  KEY fee_id (fee_id),
  KEY prd_offering_fee_idx (prd_offering_id,fee_id),
  CONSTRAINT prd_offering_fees_ibfk_1 FOREIGN KEY (fee_id) REFERENCES fees (fee_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_fees_ibfk_2 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering_fees DISABLE KEYS */;
INSERT INTO prd_offering_fees VALUES (6,2,2);
INSERT INTO prd_offering_fees VALUES (7,1,7);
/*!40000 ALTER TABLE prd_offering_fees ENABLE KEYS */;
DROP TABLE IF EXISTS prd_offering_meeting;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_offering_meeting (
  prd_offering_meeting_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  prd_meeting_id int(11) DEFAULT NULL,
  prd_offering_meeting_type_id smallint(6) NOT NULL,
  PRIMARY KEY (prd_offering_meeting_id),
  KEY prd_offering_id (prd_offering_id),
  KEY prd_meeting_id (prd_meeting_id),
  KEY prd_offering_meeting_type_id (prd_offering_meeting_type_id),
  CONSTRAINT prd_offering_meeting_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id),
  CONSTRAINT prd_offering_meeting_ibfk_2 FOREIGN KEY (prd_meeting_id) REFERENCES meeting (meeting_id),
  CONSTRAINT prd_offering_meeting_ibfk_3 FOREIGN KEY (prd_offering_meeting_type_id) REFERENCES meeting_type (meeting_type_id)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering_meeting DISABLE KEYS */;
INSERT INTO prd_offering_meeting VALUES (1,1,2,3);
INSERT INTO prd_offering_meeting VALUES (2,1,3,2);
INSERT INTO prd_offering_meeting VALUES (3,2,6,1);
INSERT INTO prd_offering_meeting VALUES (4,3,8,1);
INSERT INTO prd_offering_meeting VALUES (5,4,11,1);
INSERT INTO prd_offering_meeting VALUES (6,5,13,1);
INSERT INTO prd_offering_meeting VALUES (7,6,14,1);
INSERT INTO prd_offering_meeting VALUES (8,7,19,1);
INSERT INTO prd_offering_meeting VALUES (11,8,28,3);
INSERT INTO prd_offering_meeting VALUES (12,8,29,2);
INSERT INTO prd_offering_meeting VALUES (13,9,31,1);
INSERT INTO prd_offering_meeting VALUES (14,10,39,1);
INSERT INTO prd_offering_meeting VALUES (15,11,41,1);
INSERT INTO prd_offering_meeting VALUES (16,12,46,1);
INSERT INTO prd_offering_meeting VALUES (17,13,47,1);
INSERT INTO prd_offering_meeting VALUES (18,14,51,1);
INSERT INTO prd_offering_meeting VALUES (19,15,57,1);
INSERT INTO prd_offering_meeting VALUES (20,16,58,1);
INSERT INTO prd_offering_meeting VALUES (21,17,59,1);
INSERT INTO prd_offering_meeting VALUES (22,18,70,2);
INSERT INTO prd_offering_meeting VALUES (23,18,71,3);
INSERT INTO prd_offering_meeting VALUES (24,19,74,3);
INSERT INTO prd_offering_meeting VALUES (25,19,75,2);
/*!40000 ALTER TABLE prd_offering_meeting ENABLE KEYS */;
DROP TABLE IF EXISTS prd_offering_mix;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_offering_mix (
  prd_offering_mix_id int(11) NOT NULL AUTO_INCREMENT,
  prd_offering_id smallint(6) NOT NULL,
  prd_offering_not_allowed_id smallint(6) NOT NULL,
  created_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (prd_offering_mix_id),
  KEY prd_offering_mix_prd_offering_id_1 (prd_offering_id),
  KEY prd_offering_mix_prd_offering_id_2 (prd_offering_not_allowed_id),
  CONSTRAINT prd_offering_mix_prd_offering_id_1 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_offering_mix_prd_offering_id_2 FOREIGN KEY (prd_offering_not_allowed_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering_mix DISABLE KEYS */;
/*!40000 ALTER TABLE prd_offering_mix ENABLE KEYS */;
DROP TABLE IF EXISTS prd_offering_question_group;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_offering_question_group (
  prd_offering_id smallint(6) NOT NULL,
  question_group_id int(11) NOT NULL,
  KEY prd_offering_id (prd_offering_id),
  KEY question_group_id (question_group_id),
  CONSTRAINT prd_offering_question_group_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id),
  CONSTRAINT prd_offering_question_group_ibfk_2 FOREIGN KEY (question_group_id) REFERENCES question_group (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering_question_group DISABLE KEYS */;
INSERT INTO prd_offering_question_group VALUES (13,9);
INSERT INTO prd_offering_question_group VALUES (5,9);
INSERT INTO prd_offering_question_group VALUES (16,9);
INSERT INTO prd_offering_question_group VALUES (6,9);
INSERT INTO prd_offering_question_group VALUES (11,9);
INSERT INTO prd_offering_question_group VALUES (3,9);
INSERT INTO prd_offering_question_group VALUES (4,9);
INSERT INTO prd_offering_question_group VALUES (10,9);
INSERT INTO prd_offering_question_group VALUES (12,9);
INSERT INTO prd_offering_question_group VALUES (14,9);
INSERT INTO prd_offering_question_group VALUES (2,9);
INSERT INTO prd_offering_question_group VALUES (17,9);
INSERT INTO prd_offering_question_group VALUES (7,9);
INSERT INTO prd_offering_question_group VALUES (9,9);
INSERT INTO prd_offering_question_group VALUES (13,10);
INSERT INTO prd_offering_question_group VALUES (5,10);
INSERT INTO prd_offering_question_group VALUES (16,10);
INSERT INTO prd_offering_question_group VALUES (6,10);
INSERT INTO prd_offering_question_group VALUES (11,10);
INSERT INTO prd_offering_question_group VALUES (3,10);
INSERT INTO prd_offering_question_group VALUES (4,10);
INSERT INTO prd_offering_question_group VALUES (10,10);
INSERT INTO prd_offering_question_group VALUES (12,10);
INSERT INTO prd_offering_question_group VALUES (14,10);
INSERT INTO prd_offering_question_group VALUES (2,10);
INSERT INTO prd_offering_question_group VALUES (17,10);
INSERT INTO prd_offering_question_group VALUES (7,10);
INSERT INTO prd_offering_question_group VALUES (9,10);
/*!40000 ALTER TABLE prd_offering_question_group ENABLE KEYS */;
DROP TABLE IF EXISTS prd_state;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_state (
  prd_state_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_state_lookup_id int(11) DEFAULT NULL,
  PRIMARY KEY (prd_state_id),
  KEY prd_state_lookup_id (prd_state_lookup_id),
  CONSTRAINT prd_state_ibfk_1 FOREIGN KEY (prd_state_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_state DISABLE KEYS */;
INSERT INTO prd_state VALUES (1,115);
INSERT INTO prd_state VALUES (2,116);
/*!40000 ALTER TABLE prd_state ENABLE KEYS */;
DROP TABLE IF EXISTS prd_status;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_status (
  offering_status_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_state_id smallint(6) NOT NULL,
  prd_type_id smallint(6) NOT NULL,
  currently_in_use smallint(6) NOT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (offering_status_id),
  KEY prd_type_id (prd_type_id),
  KEY prd_state_id (prd_state_id),
  CONSTRAINT prd_status_ibfk_1 FOREIGN KEY (prd_type_id) REFERENCES prd_type (prd_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT prd_status_ibfk_2 FOREIGN KEY (prd_state_id) REFERENCES prd_state (prd_state_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_status DISABLE KEYS */;
INSERT INTO prd_status VALUES (1,1,1,1,1);
INSERT INTO prd_status VALUES (2,1,2,1,1);
INSERT INTO prd_status VALUES (4,2,1,1,1);
INSERT INTO prd_status VALUES (5,2,2,1,1);
/*!40000 ALTER TABLE prd_status ENABLE KEYS */;
DROP TABLE IF EXISTS prd_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE prd_type (
  prd_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  prd_type_lookup_id int(11) NOT NULL,
  lateness_days smallint(6) DEFAULT NULL,
  dormancy_days smallint(6) DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (prd_type_id),
  KEY prd_type_lookup_id (prd_type_lookup_id),
  CONSTRAINT prd_type_ibfk_1 FOREIGN KEY (prd_type_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_type DISABLE KEYS */;
INSERT INTO prd_type VALUES (1,54,10,1,1);
INSERT INTO prd_type VALUES (2,55,12,30,1);
/*!40000 ALTER TABLE prd_type ENABLE KEYS */;
DROP TABLE IF EXISTS product_offering_mandatory_savings;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE product_offering_mandatory_savings (
  product_offering_mandatory_savings_id smallint(6) NOT NULL,
  product_offering_mandatory_savings_type smallint(6) DEFAULT NULL,
  prd_offering_id smallint(6) DEFAULT NULL,
  product_offering_mandatory_savings_value smallint(6) DEFAULT NULL,
  product_offering_mandatory_savings_range smallint(6) DEFAULT NULL,
  PRIMARY KEY (product_offering_mandatory_savings_id),
  KEY prd_offering_id (prd_offering_id),
  CONSTRAINT product_offering_mandatory_savings_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE product_offering_mandatory_savings DISABLE KEYS */;
/*!40000 ALTER TABLE product_offering_mandatory_savings ENABLE KEYS */;
DROP TABLE IF EXISTS program;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE program (
  program_id int(11) NOT NULL AUTO_INCREMENT,
  office_id smallint(6) NOT NULL,
  lookup_id int(11) NOT NULL,
  glcode_id smallint(6) DEFAULT NULL,
  program_name varchar(100) DEFAULT NULL,
  start_date date NOT NULL,
  end_date date DEFAULT NULL,
  confidentiality smallint(6) DEFAULT NULL,
  PRIMARY KEY (program_id),
  KEY glcode_id (glcode_id),
  KEY lookup_id (lookup_id),
  KEY office_id (office_id),
  CONSTRAINT program_ibfk_1 FOREIGN KEY (glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT program_ibfk_2 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT program_ibfk_3 FOREIGN KEY (office_id) REFERENCES office (office_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE program DISABLE KEYS */;
/*!40000 ALTER TABLE program ENABLE KEYS */;
DROP TABLE IF EXISTS program_fund;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE program_fund (
  program_fund_id smallint(6) NOT NULL,
  fund_id smallint(6) DEFAULT NULL,
  program_id int(11) DEFAULT NULL,
  PRIMARY KEY (program_fund_id),
  KEY fund_id (fund_id),
  KEY program_id (program_id),
  CONSTRAINT program_fund_ibfk_1 FOREIGN KEY (fund_id) REFERENCES fund (fund_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT program_fund_ibfk_2 FOREIGN KEY (program_id) REFERENCES program (program_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE program_fund DISABLE KEYS */;
/*!40000 ALTER TABLE program_fund ENABLE KEYS */;
DROP TABLE IF EXISTS question_choice_tags;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE question_choice_tags (
  id int(11) NOT NULL AUTO_INCREMENT,
  choice_id int(11) NOT NULL,
  tag_text varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY choice_id (choice_id,tag_text),
  CONSTRAINT question_choice_tags_ibfk_1 FOREIGN KEY (choice_id) REFERENCES question_choices (choice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_choice_tags DISABLE KEYS */;
/*!40000 ALTER TABLE question_choice_tags ENABLE KEYS */;
DROP TABLE IF EXISTS question_choices;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE question_choices (
  choice_id int(11) NOT NULL AUTO_INCREMENT,
  question_id int(11) NOT NULL,
  choice_text varchar(500) NOT NULL,
  choice_order int(11) NOT NULL,
  ppi varchar(1) NOT NULL,
  ppi_points int(11) DEFAULT NULL,
  PRIMARY KEY (choice_id),
  KEY question_id (question_id),
  CONSTRAINT question_choices_ibfk_1 FOREIGN KEY (question_id) REFERENCES questions (question_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_choices DISABLE KEYS */;
INSERT INTO question_choices VALUES (1,4,'yes',0,'N',NULL);
INSERT INTO question_choices VALUES (2,4,'no',1,'N',NULL);
INSERT INTO question_choices VALUES (3,9,'one',0,'N',NULL);
INSERT INTO question_choices VALUES (4,9,'two',1,'N',NULL);
INSERT INTO question_choices VALUES (5,9,'three',2,'N',NULL);
INSERT INTO question_choices VALUES (6,9,'four',3,'N',NULL);
INSERT INTO question_choices VALUES (7,10,'red',0,'N',NULL);
INSERT INTO question_choices VALUES (8,10,'blue',1,'N',NULL);
INSERT INTO question_choices VALUES (9,10,'green',2,'N',NULL);
INSERT INTO question_choices VALUES (10,10,'yellow',3,'N',NULL);
INSERT INTO question_choices VALUES (11,10,'red',0,'N',NULL);
INSERT INTO question_choices VALUES (12,10,'blue',1,'N',NULL);
INSERT INTO question_choices VALUES (13,10,'green',2,'N',NULL);
INSERT INTO question_choices VALUES (14,10,'yellow',3,'N',NULL);
INSERT INTO question_choices VALUES (15,9,'one',0,'N',NULL);
INSERT INTO question_choices VALUES (16,9,'two',1,'N',NULL);
INSERT INTO question_choices VALUES (17,9,'three',2,'N',NULL);
INSERT INTO question_choices VALUES (18,9,'four',3,'N',NULL);
INSERT INTO question_choices VALUES (19,10,'red',0,'N',NULL);
INSERT INTO question_choices VALUES (20,10,'blue',1,'N',NULL);
INSERT INTO question_choices VALUES (21,10,'green',2,'N',NULL);
INSERT INTO question_choices VALUES (22,10,'yellow',3,'N',NULL);
INSERT INTO question_choices VALUES (23,9,'one',0,'N',NULL);
INSERT INTO question_choices VALUES (24,9,'two',1,'N',NULL);
INSERT INTO question_choices VALUES (25,9,'three',2,'N',NULL);
INSERT INTO question_choices VALUES (26,9,'four',3,'N',NULL);
INSERT INTO question_choices VALUES (27,10,'red',0,'N',NULL);
INSERT INTO question_choices VALUES (28,10,'blue',1,'N',NULL);
INSERT INTO question_choices VALUES (29,10,'green',2,'N',NULL);
INSERT INTO question_choices VALUES (30,10,'yellow',3,'N',NULL);
INSERT INTO question_choices VALUES (31,9,'one',0,'N',NULL);
INSERT INTO question_choices VALUES (32,9,'two',1,'N',NULL);
INSERT INTO question_choices VALUES (33,9,'three',2,'N',NULL);
INSERT INTO question_choices VALUES (34,9,'four',3,'N',NULL);
INSERT INTO question_choices VALUES (35,10,'red',0,'N',NULL);
INSERT INTO question_choices VALUES (36,10,'blue',1,'N',NULL);
INSERT INTO question_choices VALUES (37,10,'green',2,'N',NULL);
INSERT INTO question_choices VALUES (38,10,'yellow',3,'N',NULL);
INSERT INTO question_choices VALUES (39,9,'one',0,'N',NULL);
INSERT INTO question_choices VALUES (40,9,'two',1,'N',NULL);
INSERT INTO question_choices VALUES (41,9,'three',2,'N',NULL);
INSERT INTO question_choices VALUES (42,9,'four',3,'N',NULL);
INSERT INTO question_choices VALUES (43,9,'one',0,'N',NULL);
INSERT INTO question_choices VALUES (44,9,'two',1,'N',NULL);
INSERT INTO question_choices VALUES (45,9,'three',2,'N',NULL);
INSERT INTO question_choices VALUES (46,9,'four',3,'N',NULL);
/*!40000 ALTER TABLE question_choices ENABLE KEYS */;
DROP TABLE IF EXISTS question_group;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE question_group (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(200) NOT NULL,
  date_of_creation date NOT NULL,
  state int(11) NOT NULL,
  is_editable tinyint(4) NOT NULL DEFAULT '0',
  is_ppi tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_group DISABLE KEYS */;
INSERT INTO question_group VALUES (1,'QGForCreateSavingsAccount','2011-02-24',0,0,0);
INSERT INTO question_group VALUES (2,'QGForLoanApproval','2011-03-04',0,0,0);
INSERT INTO question_group VALUES (3,'QGForViewClientCentreGroupLoan','2011-03-07',0,0,0);
INSERT INTO question_group VALUES (4,'ViewCenterQG','2011-03-07',1,1,0);
INSERT INTO question_group VALUES (5,'QGForDisburseLoan1','2011-03-08',0,1,0);
INSERT INTO question_group VALUES (6,'QGForDisburseLoan2','2011-03-07',0,1,0);
INSERT INTO question_group VALUES (7,'QGForApproveLoan1','2011-03-08',0,1,0);
INSERT INTO question_group VALUES (8,'QGForApproveLoan2','2011-03-08',0,1,0);
INSERT INTO question_group VALUES (9,'QGForCreateLoan1','2011-03-08',0,1,0);
INSERT INTO question_group VALUES (10,'QGForCreateLoan2','2011-03-08',0,1,0);
INSERT INTO question_group VALUES (11,'QGForViewSavings','2011-03-08',0,1,0);
INSERT INTO question_group VALUES (12,'CreateOffice','2011-03-09',0,1,0);
INSERT INTO question_group VALUES (13,'CreateClientQG-1','2011-03-09',0,1,0);
INSERT INTO question_group VALUES (14,'QGForCloseLoan','2011-11-17',0,1,0);
INSERT INTO question_group VALUES (15,'QGForCloseSavings','2011-11-17',0,1,0);
/*!40000 ALTER TABLE question_group ENABLE KEYS */;
DROP TABLE IF EXISTS question_group_event_sources;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE question_group_event_sources (
  id int(11) NOT NULL AUTO_INCREMENT,
  question_group_id int(11) NOT NULL,
  event_source_id int(11) NOT NULL,
  PRIMARY KEY (id),
  KEY question_group_id (question_group_id),
  KEY event_source_id (event_source_id),
  CONSTRAINT question_group_event_sources_ibfk_1 FOREIGN KEY (question_group_id) REFERENCES question_group (id),
  CONSTRAINT question_group_event_sources_ibfk_2 FOREIGN KEY (event_source_id) REFERENCES event_sources (id)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_group_event_sources DISABLE KEYS */;
INSERT INTO question_group_event_sources VALUES (1,1,12);
INSERT INTO question_group_event_sources VALUES (3,2,5);
INSERT INTO question_group_event_sources VALUES (8,3,3);
INSERT INTO question_group_event_sources VALUES (9,3,7);
INSERT INTO question_group_event_sources VALUES (10,3,8);
INSERT INTO question_group_event_sources VALUES (11,3,10);
INSERT INTO question_group_event_sources VALUES (13,4,10);
INSERT INTO question_group_event_sources VALUES (17,6,11);
INSERT INTO question_group_event_sources VALUES (20,5,11);
INSERT INTO question_group_event_sources VALUES (21,7,5);
INSERT INTO question_group_event_sources VALUES (22,8,5);
INSERT INTO question_group_event_sources VALUES (25,9,2);
INSERT INTO question_group_event_sources VALUES (26,10,2);
INSERT INTO question_group_event_sources VALUES (28,11,13);
INSERT INTO question_group_event_sources VALUES (30,12,6);
INSERT INTO question_group_event_sources VALUES (33,13,1);
INSERT INTO question_group_event_sources VALUES (35,14,16);
INSERT INTO question_group_event_sources VALUES (36,15,17);
/*!40000 ALTER TABLE question_group_event_sources ENABLE KEYS */;
DROP TABLE IF EXISTS question_group_instance;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE question_group_instance (
  id int(11) NOT NULL AUTO_INCREMENT,
  question_group_id int(11) NOT NULL,
  entity_id int(11) NOT NULL,
  date_conducted date NOT NULL,
  completed_status smallint(6) NOT NULL,
  created_by int(11) NOT NULL,
  version_id int(11) NOT NULL,
  event_source_id int(11) NOT NULL,
  PRIMARY KEY (id),
  KEY question_group_id (question_group_id),
  CONSTRAINT question_group_instance_ibfk_1 FOREIGN KEY (question_group_id) REFERENCES question_group (id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_group_instance DISABLE KEYS */;
INSERT INTO question_group_instance VALUES (1,13,31,'2011-03-09',1,1,0,1);
/*!40000 ALTER TABLE question_group_instance ENABLE KEYS */;
DROP TABLE IF EXISTS question_group_response;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE question_group_response (
  id int(11) NOT NULL AUTO_INCREMENT,
  question_group_instance_id int(11) NOT NULL,
  sections_questions_id int(11) NOT NULL,
  response varchar(500) NOT NULL,
  tag varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY sections_questions_id (sections_questions_id),
  KEY question_group_instance_id (question_group_instance_id),
  CONSTRAINT question_group_response_ibfk_1 FOREIGN KEY (sections_questions_id) REFERENCES sections_questions (id),
  CONSTRAINT question_group_response_ibfk_2 FOREIGN KEY (question_group_instance_id) REFERENCES question_group_instance (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_group_response DISABLE KEYS */;
/*!40000 ALTER TABLE question_group_response ENABLE KEYS */;
DROP TABLE IF EXISTS questions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE questions (
  question_id int(11) NOT NULL AUTO_INCREMENT,
  answer_type int(11) NOT NULL,
  question_state int(11) NOT NULL,
  question_text varchar(1000) NOT NULL,
  numeric_min int(11) DEFAULT NULL,
  numeric_max int(11) DEFAULT NULL,
  nickname varchar(64) NOT NULL,
  PRIMARY KEY (question_id),
  UNIQUE KEY nickname (nickname)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE questions DISABLE KEYS */;
INSERT INTO questions VALUES (1,2,1,'Question1',NULL,NULL,'e5c1d1ea399a2e46aca161ed737cfee3');
INSERT INTO questions VALUES (2,5,1,'Date',NULL,NULL,'44749712dbec183e983dcd78a7736c41');
INSERT INTO questions VALUES (3,5,1,'question 3',NULL,NULL,'4527a3a6804e6f368aa32a73ca30da95');
INSERT INTO questions VALUES (4,6,1,'question 4',NULL,NULL,'8b1921a8385c31f50aa2ee992b820f91');
INSERT INTO questions VALUES (5,5,1,'DateQuestion',NULL,NULL,'d578923d0af9bbf6ec05ca0978a72aac');
INSERT INTO questions VALUES (6,3,1,'Number',NULL,NULL,'b2ee912b91d69b435159c7c3f6df7f5f');
INSERT INTO questions VALUES (7,2,1,'question 1',NULL,NULL,'4fdd288d632ab655534cee798129f2e3');
INSERT INTO questions VALUES (8,2,1,'Text',NULL,NULL,'9dffbf69ffba8bc38bc4e01abf4b1675');
INSERT INTO questions VALUES (9,1,1,'MultiSelect',NULL,NULL,'0cba84730c2881b781171ae3640a6421');
INSERT INTO questions VALUES (10,6,1,'SingleSelect',NULL,NULL,'46fb49833801b679f5da12e40a7e07a5');
INSERT INTO questions VALUES (11,2,1,'FreeText',NULL,NULL,'19635fdd73f0ff31933414280bee07c6');
INSERT INTO questions VALUES (12,2,1,'ToBeDisabled',NULL,NULL,'ea06497c775eff59ee969da7252bd761');
INSERT INTO questions VALUES (13,3,1,'NumberBetween5And10',5,10,'afc3e20340e11a0cef7f479fa6984d87');
/*!40000 ALTER TABLE questions ENABLE KEYS */;
DROP TABLE IF EXISTS recommended_amnt_unit;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE recommended_amnt_unit (
  recommended_amnt_unit_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (recommended_amnt_unit_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT recommended_amnt_unit_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE recommended_amnt_unit DISABLE KEYS */;
INSERT INTO recommended_amnt_unit VALUES (1,120);
INSERT INTO recommended_amnt_unit VALUES (2,121);
/*!40000 ALTER TABLE recommended_amnt_unit ENABLE KEYS */;
DROP TABLE IF EXISTS recur_on_day;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE recur_on_day (
  recur_on_day_id int(11) NOT NULL AUTO_INCREMENT,
  details_id int(11) NOT NULL,
  days smallint(6) DEFAULT NULL,
  rank_of_days smallint(6) DEFAULT NULL,
  day_number smallint(6) DEFAULT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (recur_on_day_id),
  KEY details_id (details_id),
  CONSTRAINT recur_on_day_ibfk_1 FOREIGN KEY (details_id) REFERENCES recurrence_detail (details_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE recur_on_day DISABLE KEYS */;
INSERT INTO recur_on_day VALUES (1,1,6,NULL,NULL,9);
INSERT INTO recur_on_day VALUES (2,2,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (3,3,NULL,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (4,4,NULL,NULL,1,1);
INSERT INTO recur_on_day VALUES (5,5,2,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (6,6,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (7,7,6,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (8,8,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (9,9,NULL,NULL,1,1);
INSERT INTO recur_on_day VALUES (10,10,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (11,11,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (12,12,6,3,NULL,1);
INSERT INTO recur_on_day VALUES (13,13,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (14,14,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (15,15,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (16,16,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (17,17,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (18,18,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (19,19,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (20,20,3,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (21,21,3,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (22,22,3,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (23,23,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (24,24,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (25,25,2,NULL,NULL,3);
INSERT INTO recur_on_day VALUES (28,28,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (29,29,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (30,30,3,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (31,31,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (32,32,3,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (33,33,3,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (34,34,3,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (35,35,3,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (36,36,3,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (37,37,6,3,NULL,2);
INSERT INTO recur_on_day VALUES (38,38,NULL,NULL,1,2);
INSERT INTO recur_on_day VALUES (39,39,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (40,40,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (41,41,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (42,42,2,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (43,43,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (44,44,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (45,45,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (46,46,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (47,47,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (48,48,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (49,49,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (50,50,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (51,51,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (52,52,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (53,53,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (54,54,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (55,55,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (56,56,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (57,57,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (58,58,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (59,59,2,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (60,60,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (61,61,5,NULL,NULL,3);
INSERT INTO recur_on_day VALUES (62,62,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (63,63,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (64,64,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (65,65,6,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (66,66,2,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (67,67,3,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (68,68,2,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (69,69,4,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (70,70,NULL,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (71,71,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (72,72,6,NULL,NULL,1);
INSERT INTO recur_on_day VALUES (73,73,6,NULL,NULL,3);
INSERT INTO recur_on_day VALUES (74,74,NULL,NULL,1,0);
INSERT INTO recur_on_day VALUES (75,75,NULL,NULL,NULL,0);
INSERT INTO recur_on_day VALUES (76,76,2,NULL,NULL,3);
/*!40000 ALTER TABLE recur_on_day ENABLE KEYS */;
DROP TABLE IF EXISTS recurrence_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE recurrence_detail (
  details_id int(11) NOT NULL AUTO_INCREMENT,
  meeting_id int(11) NOT NULL,
  recurrence_id smallint(6) DEFAULT NULL,
  recur_after smallint(6) NOT NULL,
  version_no int(11) DEFAULT NULL,
  PRIMARY KEY (details_id),
  KEY recurrence_id (recurrence_id),
  KEY meeting_id (meeting_id),
  CONSTRAINT recurrence_detail_ibfk_1 FOREIGN KEY (recurrence_id) REFERENCES recurrence_type (recurrence_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT recurrence_detail_ibfk_2 FOREIGN KEY (meeting_id) REFERENCES meeting (meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE recurrence_detail DISABLE KEYS */;
INSERT INTO recurrence_detail VALUES (1,1,1,1,9);
INSERT INTO recurrence_detail VALUES (2,2,2,1,0);
INSERT INTO recurrence_detail VALUES (3,3,3,7,0);
INSERT INTO recurrence_detail VALUES (4,4,2,1,1);
INSERT INTO recurrence_detail VALUES (5,5,1,1,1);
INSERT INTO recurrence_detail VALUES (6,6,1,1,0);
INSERT INTO recurrence_detail VALUES (7,7,1,1,1);
INSERT INTO recurrence_detail VALUES (8,8,2,1,0);
INSERT INTO recurrence_detail VALUES (9,9,2,1,1);
INSERT INTO recurrence_detail VALUES (10,10,1,1,0);
INSERT INTO recurrence_detail VALUES (11,11,2,1,0);
INSERT INTO recurrence_detail VALUES (12,12,2,1,1);
INSERT INTO recurrence_detail VALUES (13,13,1,1,0);
INSERT INTO recurrence_detail VALUES (14,14,1,1,0);
INSERT INTO recurrence_detail VALUES (15,15,1,1,0);
INSERT INTO recurrence_detail VALUES (16,16,1,1,0);
INSERT INTO recurrence_detail VALUES (17,17,1,1,0);
INSERT INTO recurrence_detail VALUES (18,18,1,1,0);
INSERT INTO recurrence_detail VALUES (19,19,1,1,0);
INSERT INTO recurrence_detail VALUES (20,20,1,1,1);
INSERT INTO recurrence_detail VALUES (21,21,1,1,1);
INSERT INTO recurrence_detail VALUES (22,22,1,1,1);
INSERT INTO recurrence_detail VALUES (23,23,1,1,0);
INSERT INTO recurrence_detail VALUES (24,24,1,1,0);
INSERT INTO recurrence_detail VALUES (25,25,1,1,3);
INSERT INTO recurrence_detail VALUES (28,28,2,1,0);
INSERT INTO recurrence_detail VALUES (29,29,2,1,0);
INSERT INTO recurrence_detail VALUES (30,30,1,1,1);
INSERT INTO recurrence_detail VALUES (31,31,1,1,0);
INSERT INTO recurrence_detail VALUES (32,32,1,1,0);
INSERT INTO recurrence_detail VALUES (33,33,1,1,0);
INSERT INTO recurrence_detail VALUES (34,34,1,1,0);
INSERT INTO recurrence_detail VALUES (35,35,1,1,0);
INSERT INTO recurrence_detail VALUES (36,36,1,1,0);
INSERT INTO recurrence_detail VALUES (37,37,2,1,2);
INSERT INTO recurrence_detail VALUES (38,38,2,1,2);
INSERT INTO recurrence_detail VALUES (39,39,2,1,0);
INSERT INTO recurrence_detail VALUES (40,40,1,1,0);
INSERT INTO recurrence_detail VALUES (41,41,1,1,0);
INSERT INTO recurrence_detail VALUES (42,42,1,1,1);
INSERT INTO recurrence_detail VALUES (43,43,1,1,0);
INSERT INTO recurrence_detail VALUES (44,44,1,1,0);
INSERT INTO recurrence_detail VALUES (45,45,1,1,0);
INSERT INTO recurrence_detail VALUES (46,46,1,1,0);
INSERT INTO recurrence_detail VALUES (47,47,1,1,0);
INSERT INTO recurrence_detail VALUES (48,48,1,1,0);
INSERT INTO recurrence_detail VALUES (49,49,1,1,0);
INSERT INTO recurrence_detail VALUES (50,50,1,1,0);
INSERT INTO recurrence_detail VALUES (51,51,1,1,0);
INSERT INTO recurrence_detail VALUES (52,52,1,1,0);
INSERT INTO recurrence_detail VALUES (53,53,1,1,0);
INSERT INTO recurrence_detail VALUES (54,54,1,1,0);
INSERT INTO recurrence_detail VALUES (55,55,1,1,0);
INSERT INTO recurrence_detail VALUES (56,56,1,1,0);
INSERT INTO recurrence_detail VALUES (57,57,1,1,0);
INSERT INTO recurrence_detail VALUES (58,58,2,1,0);
INSERT INTO recurrence_detail VALUES (59,59,1,1,0);
INSERT INTO recurrence_detail VALUES (60,60,1,1,0);
INSERT INTO recurrence_detail VALUES (61,61,1,1,3);
INSERT INTO recurrence_detail VALUES (62,62,1,1,0);
INSERT INTO recurrence_detail VALUES (63,63,1,1,0);
INSERT INTO recurrence_detail VALUES (64,64,1,1,0);
INSERT INTO recurrence_detail VALUES (65,65,1,1,0);
INSERT INTO recurrence_detail VALUES (66,66,1,1,1);
INSERT INTO recurrence_detail VALUES (67,67,1,1,1);
INSERT INTO recurrence_detail VALUES (68,68,1,1,1);
INSERT INTO recurrence_detail VALUES (69,69,1,1,1);
INSERT INTO recurrence_detail VALUES (70,70,3,30,0);
INSERT INTO recurrence_detail VALUES (71,71,2,12,0);
INSERT INTO recurrence_detail VALUES (72,72,1,1,1);
INSERT INTO recurrence_detail VALUES (73,73,1,1,3);
INSERT INTO recurrence_detail VALUES (74,74,2,1,0);
INSERT INTO recurrence_detail VALUES (75,75,3,7,0);
INSERT INTO recurrence_detail VALUES (76,76,1,1,3);
/*!40000 ALTER TABLE recurrence_detail ENABLE KEYS */;
DROP TABLE IF EXISTS recurrence_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE recurrence_type (
  recurrence_id smallint(6) NOT NULL AUTO_INCREMENT,
  recurrence_name varchar(50) DEFAULT NULL,
  description varchar(200) NOT NULL,
  PRIMARY KEY (recurrence_id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE recurrence_type DISABLE KEYS */;
INSERT INTO recurrence_type VALUES (1,'Week(s)','Weekly Recurrence');
INSERT INTO recurrence_type VALUES (2,'Month(s)','Monthly Recurrence');
INSERT INTO recurrence_type VALUES (3,'Day(s)','Daily Recurrence');
/*!40000 ALTER TABLE recurrence_type ENABLE KEYS */;
DROP TABLE IF EXISTS repayment_rule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE repayment_rule (
  repayment_rule_id smallint(6) NOT NULL AUTO_INCREMENT,
  repayment_rule_lookup_id int(11) DEFAULT NULL,
  PRIMARY KEY (repayment_rule_id),
  KEY repayment_rule_lookup_id (repayment_rule_lookup_id),
  CONSTRAINT repayment_rule_ibfk_1 FOREIGN KEY (repayment_rule_lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE repayment_rule DISABLE KEYS */;
INSERT INTO repayment_rule VALUES (1,576);
INSERT INTO repayment_rule VALUES (2,577);
INSERT INTO repayment_rule VALUES (3,578);
INSERT INTO repayment_rule VALUES (4,626);
/*!40000 ALTER TABLE repayment_rule ENABLE KEYS */;
DROP TABLE IF EXISTS report;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report (
  report_id smallint(6) NOT NULL AUTO_INCREMENT,
  report_category_id smallint(6) DEFAULT NULL,
  report_name varchar(100) DEFAULT NULL,
  report_identifier varchar(100) DEFAULT NULL,
  activity_id smallint(6) DEFAULT NULL,
  report_active smallint(6) DEFAULT NULL,
  PRIMARY KEY (report_id),
  KEY report_category_id (report_category_id),
  KEY activity_id (activity_id),
  CONSTRAINT report_ibfk_1 FOREIGN KEY (report_category_id) REFERENCES report_category (report_category_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT report_ibfk_2 FOREIGN KEY (activity_id) REFERENCES activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE report DISABLE KEYS */;
INSERT INTO report VALUES (1,1,'Collection Sheet Report','collection_sheet_report',229,1);
INSERT INTO report VALUES (2,6,'Branch Cash Confirmation Report','branch_cash_confirmation_report',231,1);
INSERT INTO report VALUES (3,6,'Branch Progress Report','branch_progress_report',232,1);
INSERT INTO report VALUES (4,6,'Detailed Aging Of Portfolio At Risk Report','detailed_aging_of_portfolio_at_risk_report',236,1);
INSERT INTO report VALUES (5,6,'General Ledger Report','general_ledger_report',237,1);
/*!40000 ALTER TABLE report ENABLE KEYS */;
DROP TABLE IF EXISTS report_category;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_category (
  report_category_id smallint(6) NOT NULL AUTO_INCREMENT,
  report_category_value varchar(100) DEFAULT NULL,
  activity_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (report_category_id),
  KEY activity_id (activity_id),
  CONSTRAINT report_category_ibfk_1 FOREIGN KEY (activity_id) REFERENCES activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE report_category DISABLE KEYS */;
INSERT INTO report_category VALUES (1,'Client Detail',145);
INSERT INTO report_category VALUES (2,'Performance',148);
INSERT INTO report_category VALUES (3,'Center',146);
INSERT INTO report_category VALUES (4,'Loan Product Detail',149);
INSERT INTO report_category VALUES (5,'Status',147);
INSERT INTO report_category VALUES (6,'Analysis',150);
INSERT INTO report_category VALUES (7,'Miscellaneous',151);
/*!40000 ALTER TABLE report_category ENABLE KEYS */;
DROP TABLE IF EXISTS report_datasource;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_datasource (
  datasource_id int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  driver varchar(255) DEFAULT NULL,
  url varchar(255) NOT NULL DEFAULT '',
  username varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  max_idle int(11) DEFAULT NULL,
  max_active int(11) DEFAULT NULL,
  max_wait bigint(20) DEFAULT NULL,
  validation_query varchar(255) DEFAULT NULL,
  jndi tinyint(4) DEFAULT NULL,
  PRIMARY KEY (datasource_id),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY name_2 (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE report_datasource DISABLE KEYS */;
/*!40000 ALTER TABLE report_datasource ENABLE KEYS */;
DROP TABLE IF EXISTS report_jasper_map;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_jasper_map (
  report_id smallint(6) NOT NULL AUTO_INCREMENT,
  report_category_id smallint(6) DEFAULT NULL,
  report_name varchar(100) DEFAULT NULL,
  report_identifier varchar(100) DEFAULT NULL,
  report_jasper varchar(100) DEFAULT NULL,
  PRIMARY KEY (report_id),
  KEY report_category_id (report_category_id),
  CONSTRAINT report_jasper_map_ibfk_1 FOREIGN KEY (report_category_id) REFERENCES report_category (report_category_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE report_jasper_map DISABLE KEYS */;
INSERT INTO report_jasper_map VALUES (1,1,'Collection Sheet Report','collection_sheet_report','CollectionSheetReport.rptdesign');
INSERT INTO report_jasper_map VALUES (2,6,'Branch Cash Confirmation Report','branch_cash_confirmation_report','BranchCashConfirmationReport.rptdesign');
INSERT INTO report_jasper_map VALUES (3,6,'Branch Progress Report','branch_progress_report','ProgressReport.rptdesign');
INSERT INTO report_jasper_map VALUES (4,6,'Detailed Aging Of Portfolio At Risk Report','detailed_aging_of_portfolio_at_risk_report','DetailedAgingPortfolioAtRiskReport.rptdesign');
INSERT INTO report_jasper_map VALUES (5,6,'General Ledger Report','general_ledger_report','GeneralLedgerReport.rptdesign');
/*!40000 ALTER TABLE report_jasper_map ENABLE KEYS */;
DROP TABLE IF EXISTS report_parameter;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_parameter (
  parameter_id int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `type` varchar(255) NOT NULL DEFAULT '',
  classname varchar(255) NOT NULL DEFAULT '',
  `data` text,
  datasource_id int(11) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  PRIMARY KEY (parameter_id),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY name_2 (`name`),
  KEY datasource_id (datasource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE report_parameter DISABLE KEYS */;
/*!40000 ALTER TABLE report_parameter ENABLE KEYS */;
DROP TABLE IF EXISTS report_parameter_map;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_parameter_map (
  report_id int(11) NOT NULL DEFAULT '0',
  parameter_id int(11) DEFAULT NULL,
  required tinyint(4) DEFAULT NULL,
  sort_order int(11) DEFAULT NULL,
  step int(11) DEFAULT NULL,
  map_id int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (map_id),
  KEY report_id (report_id),
  KEY parameter_id (parameter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE report_parameter_map DISABLE KEYS */;
/*!40000 ALTER TABLE report_parameter_map ENABLE KEYS */;
DROP TABLE IF EXISTS role;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE role (
  role_id smallint(6) NOT NULL,
  role_name varchar(50) NOT NULL,
  version_no int(11) NOT NULL,
  created_by smallint(6) DEFAULT NULL,
  created_date date DEFAULT NULL,
  updated_by smallint(6) DEFAULT NULL,
  updated_date date DEFAULT NULL,
  PRIMARY KEY (role_id),
  KEY created_by (created_by),
  KEY updated_by (updated_by),
  CONSTRAINT role_ibfk_1 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT role_ibfk_2 FOREIGN KEY (updated_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE role DISABLE KEYS */;
INSERT INTO role VALUES (1,'Admin',3,NULL,NULL,1,'2011-02-18');
/*!40000 ALTER TABLE role ENABLE KEYS */;
DROP TABLE IF EXISTS roles_activity;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE roles_activity (
  activity_id smallint(6) NOT NULL,
  role_id smallint(6) NOT NULL,
  PRIMARY KEY (activity_id,role_id),
  KEY role_id (role_id),
  CONSTRAINT roles_activity_ibfk_1 FOREIGN KEY (activity_id) REFERENCES activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT roles_activity_ibfk_2 FOREIGN KEY (role_id) REFERENCES role (role_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE roles_activity DISABLE KEYS */;
INSERT INTO roles_activity VALUES (3,1);
INSERT INTO roles_activity VALUES (4,1);
INSERT INTO roles_activity VALUES (6,1);
INSERT INTO roles_activity VALUES (7,1);
INSERT INTO roles_activity VALUES (9,1);
INSERT INTO roles_activity VALUES (10,1);
INSERT INTO roles_activity VALUES (15,1);
INSERT INTO roles_activity VALUES (16,1);
INSERT INTO roles_activity VALUES (19,1);
INSERT INTO roles_activity VALUES (20,1);
INSERT INTO roles_activity VALUES (21,1);
INSERT INTO roles_activity VALUES (23,1);
INSERT INTO roles_activity VALUES (24,1);
INSERT INTO roles_activity VALUES (25,1);
INSERT INTO roles_activity VALUES (35,1);
INSERT INTO roles_activity VALUES (36,1);
INSERT INTO roles_activity VALUES (37,1);
INSERT INTO roles_activity VALUES (38,1);
INSERT INTO roles_activity VALUES (39,1);
INSERT INTO roles_activity VALUES (40,1);
INSERT INTO roles_activity VALUES (41,1);
INSERT INTO roles_activity VALUES (42,1);
INSERT INTO roles_activity VALUES (43,1);
INSERT INTO roles_activity VALUES (44,1);
INSERT INTO roles_activity VALUES (46,1);
INSERT INTO roles_activity VALUES (47,1);
INSERT INTO roles_activity VALUES (48,1);
INSERT INTO roles_activity VALUES (49,1);
INSERT INTO roles_activity VALUES (50,1);
INSERT INTO roles_activity VALUES (51,1);
INSERT INTO roles_activity VALUES (52,1);
INSERT INTO roles_activity VALUES (53,1);
INSERT INTO roles_activity VALUES (54,1);
INSERT INTO roles_activity VALUES (55,1);
INSERT INTO roles_activity VALUES (57,1);
INSERT INTO roles_activity VALUES (58,1);
INSERT INTO roles_activity VALUES (59,1);
INSERT INTO roles_activity VALUES (60,1);
INSERT INTO roles_activity VALUES (61,1);
INSERT INTO roles_activity VALUES (62,1);
INSERT INTO roles_activity VALUES (63,1);
INSERT INTO roles_activity VALUES (64,1);
INSERT INTO roles_activity VALUES (65,1);
INSERT INTO roles_activity VALUES (66,1);
INSERT INTO roles_activity VALUES (68,1);
INSERT INTO roles_activity VALUES (69,1);
INSERT INTO roles_activity VALUES (70,1);
INSERT INTO roles_activity VALUES (71,1);
INSERT INTO roles_activity VALUES (72,1);
INSERT INTO roles_activity VALUES (73,1);
INSERT INTO roles_activity VALUES (74,1);
INSERT INTO roles_activity VALUES (75,1);
INSERT INTO roles_activity VALUES (76,1);
INSERT INTO roles_activity VALUES (77,1);
INSERT INTO roles_activity VALUES (79,1);
INSERT INTO roles_activity VALUES (80,1);
INSERT INTO roles_activity VALUES (81,1);
INSERT INTO roles_activity VALUES (82,1);
INSERT INTO roles_activity VALUES (83,1);
INSERT INTO roles_activity VALUES (85,1);
INSERT INTO roles_activity VALUES (86,1);
INSERT INTO roles_activity VALUES (87,1);
INSERT INTO roles_activity VALUES (88,1);
INSERT INTO roles_activity VALUES (91,1);
INSERT INTO roles_activity VALUES (92,1);
INSERT INTO roles_activity VALUES (94,1);
INSERT INTO roles_activity VALUES (95,1);
INSERT INTO roles_activity VALUES (97,1);
INSERT INTO roles_activity VALUES (98,1);
INSERT INTO roles_activity VALUES (101,1);
INSERT INTO roles_activity VALUES (102,1);
INSERT INTO roles_activity VALUES (103,1);
INSERT INTO roles_activity VALUES (104,1);
INSERT INTO roles_activity VALUES (105,1);
INSERT INTO roles_activity VALUES (106,1);
INSERT INTO roles_activity VALUES (108,1);
INSERT INTO roles_activity VALUES (109,1);
INSERT INTO roles_activity VALUES (110,1);
INSERT INTO roles_activity VALUES (115,1);
INSERT INTO roles_activity VALUES (116,1);
INSERT INTO roles_activity VALUES (118,1);
INSERT INTO roles_activity VALUES (119,1);
INSERT INTO roles_activity VALUES (120,1);
INSERT INTO roles_activity VALUES (121,1);
INSERT INTO roles_activity VALUES (122,1);
INSERT INTO roles_activity VALUES (126,1);
INSERT INTO roles_activity VALUES (127,1);
INSERT INTO roles_activity VALUES (128,1);
INSERT INTO roles_activity VALUES (129,1);
INSERT INTO roles_activity VALUES (131,1);
INSERT INTO roles_activity VALUES (135,1);
INSERT INTO roles_activity VALUES (137,1);
INSERT INTO roles_activity VALUES (138,1);
INSERT INTO roles_activity VALUES (139,1);
INSERT INTO roles_activity VALUES (140,1);
INSERT INTO roles_activity VALUES (146,1);
INSERT INTO roles_activity VALUES (147,1);
INSERT INTO roles_activity VALUES (148,1);
INSERT INTO roles_activity VALUES (149,1);
INSERT INTO roles_activity VALUES (151,1);
INSERT INTO roles_activity VALUES (178,1);
INSERT INTO roles_activity VALUES (179,1);
INSERT INTO roles_activity VALUES (180,1);
INSERT INTO roles_activity VALUES (181,1);
INSERT INTO roles_activity VALUES (182,1);
INSERT INTO roles_activity VALUES (183,1);
INSERT INTO roles_activity VALUES (184,1);
INSERT INTO roles_activity VALUES (185,1);
INSERT INTO roles_activity VALUES (186,1);
INSERT INTO roles_activity VALUES (187,1);
INSERT INTO roles_activity VALUES (188,1);
INSERT INTO roles_activity VALUES (189,1);
INSERT INTO roles_activity VALUES (190,1);
INSERT INTO roles_activity VALUES (191,1);
INSERT INTO roles_activity VALUES (192,1);
INSERT INTO roles_activity VALUES (193,1);
INSERT INTO roles_activity VALUES (194,1);
INSERT INTO roles_activity VALUES (195,1);
INSERT INTO roles_activity VALUES (197,1);
INSERT INTO roles_activity VALUES (198,1);
INSERT INTO roles_activity VALUES (199,1);
INSERT INTO roles_activity VALUES (200,1);
INSERT INTO roles_activity VALUES (201,1);
INSERT INTO roles_activity VALUES (202,1);
INSERT INTO roles_activity VALUES (204,1);
INSERT INTO roles_activity VALUES (205,1);
INSERT INTO roles_activity VALUES (206,1);
INSERT INTO roles_activity VALUES (208,1);
INSERT INTO roles_activity VALUES (210,1);
INSERT INTO roles_activity VALUES (211,1);
INSERT INTO roles_activity VALUES (213,1);
INSERT INTO roles_activity VALUES (214,1);
INSERT INTO roles_activity VALUES (215,1);
INSERT INTO roles_activity VALUES (216,1);
INSERT INTO roles_activity VALUES (217,1);
INSERT INTO roles_activity VALUES (218,1);
INSERT INTO roles_activity VALUES (219,1);
INSERT INTO roles_activity VALUES (220,1);
INSERT INTO roles_activity VALUES (221,1);
INSERT INTO roles_activity VALUES (222,1);
INSERT INTO roles_activity VALUES (223,1);
INSERT INTO roles_activity VALUES (224,1);
INSERT INTO roles_activity VALUES (225,1);
INSERT INTO roles_activity VALUES (226,1);
INSERT INTO roles_activity VALUES (228,1);
INSERT INTO roles_activity VALUES (229,1);
INSERT INTO roles_activity VALUES (231,1);
INSERT INTO roles_activity VALUES (232,1);
INSERT INTO roles_activity VALUES (233,1);
INSERT INTO roles_activity VALUES (234,1);
INSERT INTO roles_activity VALUES (235,1);
INSERT INTO roles_activity VALUES (236,1);
INSERT INTO roles_activity VALUES (237,1);
INSERT INTO roles_activity VALUES (238,1);
INSERT INTO roles_activity VALUES (239,1);
INSERT INTO roles_activity VALUES (240,1);
INSERT INTO roles_activity VALUES (241,1);
INSERT INTO roles_activity VALUES (242,1);
INSERT INTO roles_activity VALUES (243,1);
INSERT INTO roles_activity VALUES (244,1);
INSERT INTO roles_activity VALUES (245,1);
INSERT INTO roles_activity VALUES (246,1);
/*!40000 ALTER TABLE roles_activity ENABLE KEYS */;
DROP TABLE IF EXISTS saving_schedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE saving_schedule (
  id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  customer_id int(11) NOT NULL,
  currency_id smallint(6) DEFAULT NULL,
  action_date date DEFAULT NULL,
  deposit decimal(21,4) NOT NULL,
  deposit_currency_id smallint(6) DEFAULT NULL,
  deposit_paid decimal(21,4) DEFAULT NULL,
  deposit_paid_currency_id smallint(6) DEFAULT NULL,
  payment_status smallint(6) NOT NULL,
  installment_id smallint(6) NOT NULL,
  payment_date date DEFAULT NULL,
  parent_flag smallint(6) DEFAULT NULL,
  version_no int(11) NOT NULL,
  PRIMARY KEY (id),
  KEY account_id (account_id),
  KEY currency_id (currency_id),
  KEY deposit_currency_id (deposit_currency_id),
  KEY deposit_paid_currency_id (deposit_paid_currency_id),
  KEY customer_id (customer_id),
  CONSTRAINT saving_schedule_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT saving_schedule_ibfk_2 FOREIGN KEY (currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT saving_schedule_ibfk_3 FOREIGN KEY (deposit_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT saving_schedule_ibfk_4 FOREIGN KEY (deposit_paid_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT saving_schedule_ibfk_5 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE saving_schedule DISABLE KEYS */;
INSERT INTO saving_schedule VALUES (1,2,5,NULL,'2010-01-22','10.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (2,2,5,NULL,'2010-01-29','10.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (3,2,5,NULL,'2010-02-05','10.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (4,2,5,NULL,'2010-02-12','10.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (5,2,5,NULL,'2010-02-19','10.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (6,2,5,NULL,'2010-02-26','10.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (7,2,5,NULL,'2010-03-05','10.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (8,2,5,NULL,'2010-03-12','10.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (9,2,5,NULL,'2010-03-19','10.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (10,2,5,NULL,'2010-03-26','10.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (11,2,10,NULL,'2011-02-18','10.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (12,2,10,NULL,'2011-02-25','10.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (13,2,10,NULL,'2011-03-04','10.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (14,2,10,NULL,'2011-03-11','10.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (15,2,10,NULL,'2011-03-18','10.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (16,2,10,NULL,'2011-03-25','10.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (17,2,10,NULL,'2011-04-01','10.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (18,2,10,NULL,'2011-04-08','10.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (19,2,10,NULL,'2011-04-15','10.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (20,2,10,NULL,'2011-04-22','10.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (21,2,5,NULL,'2010-04-02','10.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (22,2,5,NULL,'2010-04-09','10.0000',2,'0.0000',2,0,12,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (23,2,5,NULL,'2010-04-16','10.0000',2,'0.0000',2,0,13,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (24,2,5,NULL,'2010-04-23','10.0000',2,'0.0000',2,0,14,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (25,2,5,NULL,'2010-04-30','10.0000',2,'0.0000',2,0,15,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (26,2,5,NULL,'2010-05-07','10.0000',2,'0.0000',2,0,16,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (27,2,5,NULL,'2010-05-14','10.0000',2,'0.0000',2,0,17,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (28,2,5,NULL,'2010-05-21','10.0000',2,'0.0000',2,0,18,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (29,2,5,NULL,'2010-05-28','10.0000',2,'0.0000',2,0,19,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (30,2,5,NULL,'2010-06-04','10.0000',2,'0.0000',2,0,20,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (31,2,10,NULL,'2010-04-02','10.0000',2,'0.0000',2,0,11,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (32,2,10,NULL,'2010-04-09','10.0000',2,'0.0000',2,0,12,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (33,2,10,NULL,'2010-04-16','10.0000',2,'0.0000',2,0,13,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (34,2,10,NULL,'2010-04-23','10.0000',2,'0.0000',2,0,14,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (35,2,10,NULL,'2010-04-30','10.0000',2,'0.0000',2,0,15,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (36,2,10,NULL,'2010-05-07','10.0000',2,'0.0000',2,0,16,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (37,2,10,NULL,'2010-05-14','10.0000',2,'0.0000',2,0,17,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (38,2,10,NULL,'2010-05-21','10.0000',2,'0.0000',2,0,18,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (39,2,10,NULL,'2010-05-28','10.0000',2,'0.0000',2,0,19,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (40,2,10,NULL,'2010-06-04','10.0000',2,'0.0000',2,0,20,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (41,2,17,NULL,'2011-02-25','10.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (42,2,17,NULL,'2011-03-04','10.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (43,2,17,NULL,'2011-03-11','10.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (44,2,17,NULL,'2011-03-18','10.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (45,2,17,NULL,'2011-03-25','10.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (46,2,17,NULL,'2011-04-01','10.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (47,2,17,NULL,'2011-04-08','10.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (48,2,17,NULL,'2011-04-15','10.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (49,2,17,NULL,'2011-04-22','10.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (50,2,17,NULL,'2011-04-29','10.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (51,2,23,NULL,'2020-01-03','10.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (52,2,23,NULL,'2020-01-10','10.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (53,2,23,NULL,'2020-01-17','10.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (54,2,23,NULL,'2020-01-24','10.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (55,2,23,NULL,'2020-01-31','10.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (56,2,23,NULL,'2020-02-07','10.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (57,2,23,NULL,'2020-02-14','10.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (58,2,23,NULL,'2020-02-21','10.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (59,2,23,NULL,'2020-02-28','10.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (60,2,23,NULL,'2020-03-06','10.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (61,59,10,NULL,'2011-03-11','100.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (62,59,10,NULL,'2011-03-18','100.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (63,59,10,NULL,'2011-03-25','100.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (64,59,10,NULL,'2011-04-01','100.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (65,59,10,NULL,'2011-04-08','100.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (66,59,10,NULL,'2011-04-15','100.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (67,59,10,NULL,'2011-04-22','100.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (68,59,10,NULL,'2011-04-29','100.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (69,59,10,NULL,'2011-05-06','100.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (70,59,10,NULL,'2011-05-13','100.0000',2,'0.0000',2,0,10,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (71,67,37,NULL,'2011-03-18','10.0000',2,'0.0000',2,0,1,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (72,67,37,NULL,'2011-03-25','10.0000',2,'0.0000',2,0,2,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (73,67,37,NULL,'2011-04-01','10.0000',2,'0.0000',2,0,3,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (74,67,37,NULL,'2011-04-08','10.0000',2,'0.0000',2,0,4,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (75,67,37,NULL,'2011-04-15','10.0000',2,'0.0000',2,0,5,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (76,67,37,NULL,'2011-04-22','10.0000',2,'0.0000',2,0,6,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (77,67,37,NULL,'2011-04-29','10.0000',2,'0.0000',2,0,7,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (78,67,37,NULL,'2011-05-06','10.0000',2,'0.0000',2,0,8,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (79,67,37,NULL,'2011-05-13','10.0000',2,'0.0000',2,0,9,NULL,NULL,0);
INSERT INTO saving_schedule VALUES (80,67,37,NULL,'2011-05-20','10.0000',2,'0.0000',2,0,10,NULL,NULL,0);
/*!40000 ALTER TABLE saving_schedule ENABLE KEYS */;
DROP TABLE IF EXISTS savings_account;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE savings_account (
  account_id int(11) NOT NULL,
  activation_date date DEFAULT NULL,
  savings_balance decimal(21,4) DEFAULT NULL,
  savings_balance_currency_id smallint(6) DEFAULT NULL,
  recommended_amount decimal(21,4) DEFAULT NULL,
  recommended_amount_currency_id smallint(6) DEFAULT NULL,
  recommended_amnt_unit_id smallint(6) DEFAULT NULL,
  savings_type_id smallint(6) NOT NULL,
  int_to_be_posted decimal(21,4) DEFAULT NULL,
  int_to_be_posted_currency_id smallint(6) DEFAULT NULL,
  last_int_calc_date date DEFAULT NULL,
  last_int_post_date date DEFAULT NULL,
  next_int_calc_date date DEFAULT NULL,
  next_int_post_date date DEFAULT NULL,
  inter_int_calc_date date DEFAULT NULL,
  prd_offering_id smallint(6) NOT NULL,
  interest_rate decimal(13,10) NOT NULL,
  interest_calculation_type_id smallint(6) NOT NULL,
  time_per_for_int_calc int(11) DEFAULT NULL,
  min_amnt_for_int decimal(21,4) DEFAULT NULL,
  min_amnt_for_int_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (account_id),
  KEY savings_balance_currency_id (savings_balance_currency_id),
  KEY recommended_amount_currency_id (recommended_amount_currency_id),
  KEY int_to_be_posted_currency_id (int_to_be_posted_currency_id),
  KEY recommended_amnt_unit_id (recommended_amnt_unit_id),
  KEY savings_type_id (savings_type_id),
  KEY prd_offering_id (prd_offering_id),
  KEY interest_calculation_type_id (interest_calculation_type_id),
  KEY time_per_for_int_calc (time_per_for_int_calc),
  KEY min_amnt_for_int_currency_id (min_amnt_for_int_currency_id),
  CONSTRAINT savings_account_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_2 FOREIGN KEY (savings_balance_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_3 FOREIGN KEY (recommended_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_4 FOREIGN KEY (int_to_be_posted_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_5 FOREIGN KEY (recommended_amnt_unit_id) REFERENCES recommended_amnt_unit (recommended_amnt_unit_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_6 FOREIGN KEY (savings_type_id) REFERENCES savings_type (savings_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_7 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_8 FOREIGN KEY (interest_calculation_type_id) REFERENCES interest_calculation_types (interest_calculation_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_9 FOREIGN KEY (time_per_for_int_calc) REFERENCES meeting (meeting_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_account_ibfk_10 FOREIGN KEY (min_amnt_for_int_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_account DISABLE KEYS */;
INSERT INTO savings_account VALUES (2,'2009-01-01','1000.0000',2,'10.0000',2,1,2,'0.0000',2,NULL,NULL,NULL,'2009-08-31',NULL,1,'10.0000000000',1,NULL,NULL,NULL);
INSERT INTO savings_account VALUES (59,'2011-03-08','0.0000',2,'100.0000',2,2,2,'0.0000',2,NULL,NULL,NULL,'2011-03-31',NULL,1,'10.0000000000',1,NULL,NULL,NULL);
INSERT INTO savings_account VALUES (67,'2011-03-14','0.0000',2,'10.0000',2,2,2,'0.0000',2,NULL,NULL,NULL,'2011-03-31',NULL,19,'10.0000000000',1,NULL,NULL,NULL);
/*!40000 ALTER TABLE savings_account ENABLE KEYS */;
DROP TABLE IF EXISTS savings_activity_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE savings_activity_details (
  id int(11) NOT NULL AUTO_INCREMENT,
  created_by smallint(6) DEFAULT NULL,
  account_id int(11) NOT NULL,
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  account_action_id smallint(6) NOT NULL,
  amount decimal(21,4) NOT NULL,
  amount_currency_id smallint(6) NOT NULL,
  balance_amount decimal(21,4) NOT NULL,
  balance_amount_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (id),
  KEY created_by (created_by),
  KEY account_id (account_id),
  KEY account_action_id (account_action_id),
  KEY amount_currency_id (amount_currency_id),
  KEY balance_amount_currency_id (balance_amount_currency_id),
  CONSTRAINT savings_activity_details_ibfk_1 FOREIGN KEY (created_by) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_activity_details_ibfk_2 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_activity_details_ibfk_3 FOREIGN KEY (account_action_id) REFERENCES account_action (account_action_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_activity_details_ibfk_4 FOREIGN KEY (amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_activity_details_ibfk_5 FOREIGN KEY (balance_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_activity_details DISABLE KEYS */;
INSERT INTO savings_activity_details VALUES (1,1,2,'2011-02-17 18:30:00',6,'1000.0000',2,'1000.0000',2);
/*!40000 ALTER TABLE savings_activity_details ENABLE KEYS */;
DROP TABLE IF EXISTS savings_offering;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE savings_offering (
  prd_offering_id smallint(6) NOT NULL,
  interest_calculation_type_id smallint(6) NOT NULL,
  savings_type_id smallint(6) NOT NULL,
  recommended_amnt_unit_id smallint(6) DEFAULT NULL,
  recommended_amount decimal(21,4) DEFAULT NULL,
  recommended_amount_currency_id smallint(6) DEFAULT NULL,
  interest_rate decimal(13,10) NOT NULL,
  max_amnt_withdrawl decimal(21,4) DEFAULT NULL,
  max_amnt_withdrawl_currency_id smallint(6) DEFAULT NULL,
  min_amnt_for_int decimal(21,4) DEFAULT NULL,
  min_amnt_for_int_currency_id smallint(6) DEFAULT NULL,
  deposit_glcode_id smallint(6) NOT NULL,
  interest_glcode_id smallint(6) NOT NULL,
  PRIMARY KEY (prd_offering_id),
  KEY recommended_amnt_unit_id (recommended_amnt_unit_id),
  KEY savings_type_id (savings_type_id),
  KEY interest_calculation_type_id (interest_calculation_type_id),
  KEY recommended_amount_currency_id (recommended_amount_currency_id),
  KEY max_amnt_withdrawl_currency_id (max_amnt_withdrawl_currency_id),
  KEY min_amnt_for_int_currency_id (min_amnt_for_int_currency_id),
  KEY deposit_glcode_id (deposit_glcode_id),
  KEY interest_glcode_id (interest_glcode_id),
  CONSTRAINT savings_offering_ibfk_1 FOREIGN KEY (prd_offering_id) REFERENCES prd_offering (prd_offering_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_2 FOREIGN KEY (recommended_amnt_unit_id) REFERENCES recommended_amnt_unit (recommended_amnt_unit_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_3 FOREIGN KEY (savings_type_id) REFERENCES savings_type (savings_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_4 FOREIGN KEY (interest_calculation_type_id) REFERENCES interest_calculation_types (interest_calculation_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_5 FOREIGN KEY (recommended_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_6 FOREIGN KEY (max_amnt_withdrawl_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_7 FOREIGN KEY (min_amnt_for_int_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_8 FOREIGN KEY (deposit_glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_offering_ibfk_9 FOREIGN KEY (interest_glcode_id) REFERENCES gl_code (glcode_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_offering DISABLE KEYS */;
INSERT INTO savings_offering VALUES (1,1,2,NULL,'0.0000',2,'10.0000000000','0.0000',2,'0.0000',2,36,56);
INSERT INTO savings_offering VALUES (8,1,1,NULL,'10.0000',2,'1.0000000000','0.0000',2,'0.0000',2,32,56);
INSERT INTO savings_offering VALUES (18,2,2,NULL,'0.0000',2,'8.0000000000','0.0000',2,'100.0000',2,36,55);
INSERT INTO savings_offering VALUES (19,1,2,NULL,'0.0000',2,'10.0000000000','0.0000',2,'0.0000',2,36,56);
/*!40000 ALTER TABLE savings_offering ENABLE KEYS */;
DROP TABLE IF EXISTS savings_offering_historical_interest_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE savings_offering_historical_interest_detail (
  id int(11) NOT NULL AUTO_INCREMENT,
  period_start_date date NOT NULL,
  period_end_date date NOT NULL,
  interest_rate decimal(13,10) NOT NULL,
  min_amnt_for_int decimal(21,4) NOT NULL,
  min_amnt_for_int_currency_id smallint(6) NOT NULL,
  product_id smallint(6) NOT NULL,
  PRIMARY KEY (id),
  KEY product_id (product_id),
  CONSTRAINT savings_offering_historical_interest_detail_ibfk_1 FOREIGN KEY (product_id) REFERENCES savings_offering (prd_offering_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_offering_historical_interest_detail DISABLE KEYS */;
/*!40000 ALTER TABLE savings_offering_historical_interest_detail ENABLE KEYS */;
DROP TABLE IF EXISTS savings_performance;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE savings_performance (
  id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  total_deposits decimal(21,4) DEFAULT NULL,
  total_deposits_currency_id smallint(6) DEFAULT NULL,
  total_withdrawals decimal(21,4) DEFAULT NULL,
  total_withdrawals_currency_id smallint(6) DEFAULT NULL,
  total_interest_earned decimal(21,4) DEFAULT NULL,
  total_interest_earned_currency_id smallint(6) DEFAULT NULL,
  missed_deposits smallint(6) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY account_id (account_id),
  KEY total_deposits_currency_id (total_deposits_currency_id),
  KEY total_withdrawals_currency_id (total_withdrawals_currency_id),
  KEY total_interest_earned_currency_id (total_interest_earned_currency_id),
  CONSTRAINT savings_performance_ibfk_1 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_performance_ibfk_2 FOREIGN KEY (total_deposits_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_performance_ibfk_3 FOREIGN KEY (total_withdrawals_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_performance_ibfk_4 FOREIGN KEY (total_interest_earned_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_performance DISABLE KEYS */;
INSERT INTO savings_performance VALUES (1,2,'1000.0000',2,'0.0000',2,'0.0000',2,NULL);
INSERT INTO savings_performance VALUES (2,59,'0.0000',2,'0.0000',2,'0.0000',2,NULL);
INSERT INTO savings_performance VALUES (3,67,'0.0000',2,'0.0000',2,'0.0000',2,NULL);
/*!40000 ALTER TABLE savings_performance ENABLE KEYS */;
DROP TABLE IF EXISTS savings_trxn_detail;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE savings_trxn_detail (
  account_trxn_id int(11) NOT NULL,
  deposit_amount decimal(21,4) DEFAULT NULL,
  deposit_amount_currency_id smallint(6) DEFAULT NULL,
  withdrawal_amount decimal(21,4) DEFAULT NULL,
  withdrawal_amount_currency_id smallint(6) DEFAULT NULL,
  interest_amount decimal(21,4) DEFAULT NULL,
  interest_amount_currency_id smallint(6) DEFAULT NULL,
  balance decimal(21,4) DEFAULT NULL,
  balance_currency_id smallint(6) NOT NULL,
  PRIMARY KEY (account_trxn_id),
  KEY deposit_amount_currency_id (deposit_amount_currency_id),
  KEY withdrawal_amount_currency_id (withdrawal_amount_currency_id),
  KEY interest_amount_currency_id (interest_amount_currency_id),
  KEY balance_currency_id (balance_currency_id),
  CONSTRAINT savings_trxn_detail_ibfk_1 FOREIGN KEY (account_trxn_id) REFERENCES account_trxn (account_trxn_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_trxn_detail_ibfk_2 FOREIGN KEY (deposit_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_trxn_detail_ibfk_3 FOREIGN KEY (withdrawal_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_trxn_detail_ibfk_4 FOREIGN KEY (interest_amount_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT savings_trxn_detail_ibfk_5 FOREIGN KEY (balance_currency_id) REFERENCES currency (currency_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_trxn_detail DISABLE KEYS */;
INSERT INTO savings_trxn_detail VALUES (1,'1000.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2);
/*!40000 ALTER TABLE savings_trxn_detail ENABLE KEYS */;
DROP TABLE IF EXISTS savings_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE savings_type (
  savings_type_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (savings_type_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT savings_type_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_type DISABLE KEYS */;
INSERT INTO savings_type VALUES (1,118);
INSERT INTO savings_type VALUES (2,119);
/*!40000 ALTER TABLE savings_type ENABLE KEYS */;
DROP TABLE IF EXISTS sections;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE sections (
  id int(11) NOT NULL AUTO_INCREMENT,
  question_group_id int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  sequence_number int(11) NOT NULL,
  PRIMARY KEY (id),
  KEY question_group_id (question_group_id),
  CONSTRAINT sections_ibfk_1 FOREIGN KEY (question_group_id) REFERENCES question_group (id)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE sections DISABLE KEYS */;
INSERT INTO sections VALUES (1,1,'Misc',0);
INSERT INTO sections VALUES (2,2,'Misc',0);
INSERT INTO sections VALUES (3,3,'Misc',0);
INSERT INTO sections VALUES (4,4,'Sec 1',0);
INSERT INTO sections VALUES (5,4,'Sec 2',1);
INSERT INTO sections VALUES (6,5,'Sec 1',0);
INSERT INTO sections VALUES (7,5,'Sec 2',1);
INSERT INTO sections VALUES (8,6,'Sec 1',0);
INSERT INTO sections VALUES (9,6,'Sec 2',1);
INSERT INTO sections VALUES (10,7,'Sec 1',0);
INSERT INTO sections VALUES (11,7,'Sec 2',1);
INSERT INTO sections VALUES (12,8,'Sec 1',0);
INSERT INTO sections VALUES (13,8,'Sec 2',1);
INSERT INTO sections VALUES (14,9,'Sec 1',0);
INSERT INTO sections VALUES (15,9,'Sec 2',1);
INSERT INTO sections VALUES (16,10,'Sec 1',0);
INSERT INTO sections VALUES (17,10,'Sec 2',1);
INSERT INTO sections VALUES (18,11,'Sec 1',0);
INSERT INTO sections VALUES (19,12,'Default',0);
INSERT INTO sections VALUES (20,12,'Misc',1);
INSERT INTO sections VALUES (21,13,'Misc',0);
INSERT INTO sections VALUES (22,14,'Sec1',0);
INSERT INTO sections VALUES (23,15,'Sec1',0);
/*!40000 ALTER TABLE sections ENABLE KEYS */;
DROP TABLE IF EXISTS sections_questions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE sections_questions (
  id int(11) NOT NULL AUTO_INCREMENT,
  section_id int(11) NOT NULL,
  question_id int(11) NOT NULL,
  is_required tinyint(4) DEFAULT '0',
  sequence_number int(11) NOT NULL,
  PRIMARY KEY (id),
  KEY section_id (section_id),
  KEY question_id (question_id),
  CONSTRAINT sections_questions_ibfk_1 FOREIGN KEY (section_id) REFERENCES sections (id),
  CONSTRAINT sections_questions_ibfk_2 FOREIGN KEY (question_id) REFERENCES questions (question_id)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE sections_questions DISABLE KEYS */;
INSERT INTO sections_questions VALUES (1,1,1,0,0);
INSERT INTO sections_questions VALUES (2,2,1,0,0);
INSERT INTO sections_questions VALUES (3,3,1,0,0);
INSERT INTO sections_questions VALUES (4,4,2,1,0);
INSERT INTO sections_questions VALUES (5,4,3,0,1);
INSERT INTO sections_questions VALUES (6,4,4,0,2);
INSERT INTO sections_questions VALUES (7,5,5,0,0);
INSERT INTO sections_questions VALUES (8,5,6,0,1);
INSERT INTO sections_questions VALUES (9,5,7,0,2);
INSERT INTO sections_questions VALUES (10,5,8,1,3);
INSERT INTO sections_questions VALUES (11,6,2,0,0);
INSERT INTO sections_questions VALUES (12,6,12,0,1);
INSERT INTO sections_questions VALUES (13,7,11,0,0);
INSERT INTO sections_questions VALUES (14,7,10,0,1);
INSERT INTO sections_questions VALUES (15,8,5,0,0);
INSERT INTO sections_questions VALUES (16,8,6,0,1);
INSERT INTO sections_questions VALUES (17,9,9,0,0);
INSERT INTO sections_questions VALUES (18,9,8,0,1);
INSERT INTO sections_questions VALUES (19,10,2,0,0);
INSERT INTO sections_questions VALUES (20,10,12,0,1);
INSERT INTO sections_questions VALUES (21,11,11,0,0);
INSERT INTO sections_questions VALUES (22,11,10,0,1);
INSERT INTO sections_questions VALUES (23,12,5,0,0);
INSERT INTO sections_questions VALUES (24,12,6,0,1);
INSERT INTO sections_questions VALUES (25,13,9,0,0);
INSERT INTO sections_questions VALUES (26,13,8,0,1);
INSERT INTO sections_questions VALUES (27,14,2,0,0);
INSERT INTO sections_questions VALUES (28,14,12,0,1);
INSERT INTO sections_questions VALUES (29,15,11,0,0);
INSERT INTO sections_questions VALUES (30,15,10,0,1);
INSERT INTO sections_questions VALUES (31,16,5,0,0);
INSERT INTO sections_questions VALUES (32,16,6,0,1);
INSERT INTO sections_questions VALUES (33,17,9,0,0);
INSERT INTO sections_questions VALUES (34,17,8,0,1);
INSERT INTO sections_questions VALUES (35,18,5,1,0);
INSERT INTO sections_questions VALUES (36,18,6,0,1);
INSERT INTO sections_questions VALUES (37,18,13,0,2);
INSERT INTO sections_questions VALUES (38,19,6,0,0);
INSERT INTO sections_questions VALUES (39,19,10,0,1);
INSERT INTO sections_questions VALUES (40,20,2,0,0);
INSERT INTO sections_questions VALUES (41,20,11,1,1);
INSERT INTO sections_questions VALUES (42,20,9,0,2);
INSERT INTO sections_questions VALUES (43,21,11,0,0);
INSERT INTO sections_questions VALUES (44,22,2,0,0);
INSERT INTO sections_questions VALUES (45,22,11,0,1);
INSERT INTO sections_questions VALUES (46,23,9,0,0);
INSERT INTO sections_questions VALUES (47,23,6,0,1);
/*!40000 ALTER TABLE sections_questions ENABLE KEYS */;
DROP TABLE IF EXISTS spouse_father_lookup;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE spouse_father_lookup (
  spouse_father_id int(11) NOT NULL,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (spouse_father_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT spouse_father_lookup_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE spouse_father_lookup DISABLE KEYS */;
INSERT INTO spouse_father_lookup VALUES (1,128);
INSERT INTO spouse_father_lookup VALUES (2,129);
INSERT INTO spouse_father_lookup VALUES (4,622);
INSERT INTO spouse_father_lookup VALUES (5,623);
/*!40000 ALTER TABLE spouse_father_lookup ENABLE KEYS */;
DROP TABLE IF EXISTS supported_locale;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE supported_locale (
  locale_id smallint(6) NOT NULL,
  country_id smallint(6) DEFAULT NULL,
  lang_id smallint(6) DEFAULT NULL,
  locale_name varchar(50) DEFAULT NULL,
  default_locale smallint(6) DEFAULT NULL,
  PRIMARY KEY (locale_id),
  KEY country_id (country_id),
  KEY lang_id (lang_id),
  CONSTRAINT supported_locale_ibfk_1 FOREIGN KEY (country_id) REFERENCES country (country_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT supported_locale_ibfk_2 FOREIGN KEY (lang_id) REFERENCES `language` (lang_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE supported_locale DISABLE KEYS */;
INSERT INTO supported_locale VALUES (1,6,1,'EN',1);
INSERT INTO supported_locale VALUES (2,7,2,'Icelandic',0);
INSERT INTO supported_locale VALUES (3,8,3,'Spanish',0);
INSERT INTO supported_locale VALUES (4,9,4,'French',0);
INSERT INTO supported_locale VALUES (5,10,5,'Chinese-China',0);
INSERT INTO supported_locale VALUES (6,11,6,'Swahili-Kenya',0);
INSERT INTO supported_locale VALUES (7,12,6,'Swahili-Tanzania',0);
INSERT INTO supported_locale VALUES (8,13,6,'Swahili-Uganda',0);
INSERT INTO supported_locale VALUES (9,14,7,'Arabic-Algeria',0);
INSERT INTO supported_locale VALUES (10,15,7,'Arabic-Bahrain',0);
INSERT INTO supported_locale VALUES (11,16,7,'Arabic-Comoros',0);
INSERT INTO supported_locale VALUES (12,17,7,'Arabic-Chad',0);
INSERT INTO supported_locale VALUES (13,18,7,'Arabic-Djibouti',0);
INSERT INTO supported_locale VALUES (14,19,7,'Arabic-Egypt',0);
INSERT INTO supported_locale VALUES (15,20,7,'Arabic-Eritrea',0);
INSERT INTO supported_locale VALUES (16,21,7,'Arabic-Iraq',0);
INSERT INTO supported_locale VALUES (17,22,7,'Arabic-Israel',0);
INSERT INTO supported_locale VALUES (18,23,7,'Arabic-Jordan',0);
INSERT INTO supported_locale VALUES (19,24,7,'Arabic-Kuwait',0);
INSERT INTO supported_locale VALUES (20,25,7,'Arabic-Lebanon',0);
INSERT INTO supported_locale VALUES (21,26,7,'Arabic-Libyan Arab Rebublic',0);
INSERT INTO supported_locale VALUES (22,27,7,'Arabic-Mauritania',0);
INSERT INTO supported_locale VALUES (23,28,7,'Arabic-Morocco',0);
INSERT INTO supported_locale VALUES (24,29,7,'Arabic-Oman',0);
INSERT INTO supported_locale VALUES (25,30,7,'Arabic-Qatar',0);
INSERT INTO supported_locale VALUES (26,31,7,'Arabic-Saudi Arabia',0);
INSERT INTO supported_locale VALUES (27,32,7,'Arabic-Somalia',0);
INSERT INTO supported_locale VALUES (28,33,7,'Arabic-Sudan',0);
INSERT INTO supported_locale VALUES (29,34,7,'Arabic-Syrian Arab Republic',0);
INSERT INTO supported_locale VALUES (30,35,7,'Arabic-Tunisia',0);
INSERT INTO supported_locale VALUES (31,36,7,'Arabic-United Arab Emirates',0);
INSERT INTO supported_locale VALUES (32,37,7,'Arabic-Yemen',0);
INSERT INTO supported_locale VALUES (33,38,7,'Arabic-Palestinian Territory, Occupied',0);
INSERT INTO supported_locale VALUES (34,39,7,'Arabic-Western Sahara',0);
INSERT INTO supported_locale VALUES (35,40,8,'Portuguese-Angola',0);
INSERT INTO supported_locale VALUES (36,41,8,'Portuguese-Brazil',0);
INSERT INTO supported_locale VALUES (37,42,8,'Portuguese-Cape Verde',0);
INSERT INTO supported_locale VALUES (38,43,8,'Portuguese-Guinea-Bissau',0);
INSERT INTO supported_locale VALUES (39,44,8,'Portuguese-Equatorial Guinea',0);
INSERT INTO supported_locale VALUES (40,45,8,'Portuguese-Macau',0);
INSERT INTO supported_locale VALUES (41,46,8,'Portuguese-Mozambique',0);
INSERT INTO supported_locale VALUES (42,47,8,'Portuguese-Portugal',0);
INSERT INTO supported_locale VALUES (43,48,8,'Portuguese-Sao Tome and Principe',0);
INSERT INTO supported_locale VALUES (44,49,9,'Khmer-Cambodia',0);
INSERT INTO supported_locale VALUES (45,50,10,'Lao-Laos',0);
INSERT INTO supported_locale VALUES (46,25,1,'English-Lebanon-AlMajmoua',0);
INSERT INTO supported_locale VALUES (47,51,11,'Hungarian-Hungary',0);
/*!40000 ALTER TABLE supported_locale ENABLE KEYS */;
DROP TABLE IF EXISTS survey;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE survey (
  survey_id int(11) NOT NULL AUTO_INCREMENT,
  survey_name varchar(200) NOT NULL,
  survey_applies_to varchar(200) NOT NULL,
  date_of_creation date NOT NULL,
  state int(11) NOT NULL,
  PRIMARY KEY (survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE survey DISABLE KEYS */;
/*!40000 ALTER TABLE survey ENABLE KEYS */;
DROP TABLE IF EXISTS survey_instance;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE survey_instance (
  instance_id int(11) NOT NULL AUTO_INCREMENT,
  survey_id int(11) NOT NULL,
  customer_id int(11) DEFAULT NULL,
  officer_id smallint(6) DEFAULT NULL,
  date_conducted date NOT NULL,
  completed_status int(11) NOT NULL,
  account_id int(11) DEFAULT NULL,
  creating_officer_id smallint(6) NOT NULL,
  PRIMARY KEY (instance_id),
  KEY survey_id (survey_id),
  KEY customer_id (customer_id),
  KEY officer_id (officer_id),
  KEY account_id (account_id),
  KEY creating_officer_id (creating_officer_id),
  CONSTRAINT survey_instance_ibfk_1 FOREIGN KEY (survey_id) REFERENCES survey (survey_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT survey_instance_ibfk_2 FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT survey_instance_ibfk_3 FOREIGN KEY (officer_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT survey_instance_ibfk_4 FOREIGN KEY (account_id) REFERENCES account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT survey_instance_ibfk_5 FOREIGN KEY (creating_officer_id) REFERENCES personnel (personnel_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE survey_instance DISABLE KEYS */;
/*!40000 ALTER TABLE survey_instance ENABLE KEYS */;
DROP TABLE IF EXISTS survey_questions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE survey_questions (
  surveyquestion_id int(11) NOT NULL AUTO_INCREMENT,
  survey_id int(11) NOT NULL,
  question_id int(11) NOT NULL,
  question_order int(11) NOT NULL,
  mandatory smallint(6) NOT NULL DEFAULT '1',
  PRIMARY KEY (surveyquestion_id),
  KEY question_id (question_id),
  KEY survey_id (survey_id),
  CONSTRAINT survey_questions_ibfk_1 FOREIGN KEY (question_id) REFERENCES questions (question_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT survey_questions_ibfk_2 FOREIGN KEY (survey_id) REFERENCES survey (survey_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE survey_questions DISABLE KEYS */;
/*!40000 ALTER TABLE survey_questions ENABLE KEYS */;
DROP TABLE IF EXISTS survey_response;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE survey_response (
  response_id int(11) NOT NULL AUTO_INCREMENT,
  instance_id int(11) NOT NULL,
  survey_question_id int(11) NOT NULL,
  freetext_value text,
  choice_value int(11) DEFAULT NULL,
  date_value date DEFAULT NULL,
  number_value decimal(16,5) DEFAULT NULL,
  multi_select_value text,
  PRIMARY KEY (response_id),
  UNIQUE KEY instance_id (instance_id,survey_question_id),
  KEY survey_question_id (survey_question_id),
  KEY choice_value (choice_value),
  CONSTRAINT survey_response_ibfk_1 FOREIGN KEY (survey_question_id) REFERENCES survey_questions (surveyquestion_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT survey_response_ibfk_2 FOREIGN KEY (instance_id) REFERENCES survey_instance (instance_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT survey_response_ibfk_3 FOREIGN KEY (choice_value) REFERENCES question_choices (choice_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE survey_response DISABLE KEYS */;
/*!40000 ALTER TABLE survey_response ENABLE KEYS */;
DROP TABLE IF EXISTS temp_id;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE temp_id (
  id smallint(6) NOT NULL AUTO_INCREMENT,
  tempid smallint(6) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE temp_id DISABLE KEYS */;
/*!40000 ALTER TABLE temp_id ENABLE KEYS */;
DROP TABLE IF EXISTS transaction_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE transaction_type (
  transaction_id smallint(6) NOT NULL,
  transaction_name varchar(100) NOT NULL,
  PRIMARY KEY (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE transaction_type DISABLE KEYS */;
INSERT INTO transaction_type VALUES (1,'Loan Disbursement');
INSERT INTO transaction_type VALUES (2,'Loan Repayment');
INSERT INTO transaction_type VALUES (3,'Savings Deposit');
INSERT INTO transaction_type VALUES (4,'Savings Withdrawals');
INSERT INTO transaction_type VALUES (5,'Client Fees/penalty payments');
/*!40000 ALTER TABLE transaction_type ENABLE KEYS */;
DROP TABLE IF EXISTS variable_installment_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE variable_installment_details (
  id smallint(6) NOT NULL AUTO_INCREMENT,
  min_gap_in_days smallint(6) DEFAULT NULL,
  max_gap_in_days smallint(6) DEFAULT NULL,
  min_loan_amount decimal(21,4) DEFAULT NULL,
  min_loan_amount_currency_id smallint(6) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE variable_installment_details DISABLE KEYS */;
INSERT INTO variable_installment_details VALUES (1,1,10,'100.0000',2);
INSERT INTO variable_installment_details VALUES (2,1,10,'100.0000',2);
/*!40000 ALTER TABLE variable_installment_details ENABLE KEYS */;
DROP TABLE IF EXISTS waive_off_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE waive_off_history (
  waive_off_id int(11) NOT NULL AUTO_INCREMENT,
  account_id int(11) NOT NULL,
  waive_off_date date NOT NULL,
  waive_off_type varchar(20) NOT NULL,
  PRIMARY KEY (waive_off_id),
  KEY account_id (account_id),
  CONSTRAINT waive_off_history_ibfk_1 FOREIGN KEY (account_id) REFERENCES loan_account (account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE waive_off_history DISABLE KEYS */;
/*!40000 ALTER TABLE waive_off_history ENABLE KEYS */;
DROP TABLE IF EXISTS yes_no_master;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE yes_no_master (
  yes_no_master_id smallint(6) NOT NULL AUTO_INCREMENT,
  lookup_id int(11) NOT NULL,
  PRIMARY KEY (yes_no_master_id),
  KEY lookup_id (lookup_id),
  CONSTRAINT yes_no_master_ibfk_1 FOREIGN KEY (lookup_id) REFERENCES lookup_value (lookup_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE yes_no_master DISABLE KEYS */;
INSERT INTO yes_no_master VALUES (1,124);
INSERT INTO yes_no_master VALUES (2,125);
/*!40000 ALTER TABLE yes_no_master ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

