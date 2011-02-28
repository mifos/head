
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
INSERT INTO BATCH_JOB_EXECUTION (job_execution_id, version, job_instance_id, create_time, start_time, end_time, status, exit_code, exit_message, last_updated) VALUES (1,2,1,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32'),(2,2,2,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32'),(3,2,3,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32'),(4,2,4,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32'),(5,2,5,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED','COMPLETED','','2011-02-22 10:46:32'),(6,2,6,'2011-02-22 10:46:32','2011-02-22 10:46:32','2011-02-22 10:46:33','COMPLETED','COMPLETED','','2011-02-22 10:46:33'),(7,2,7,'2011-02-22 10:46:33','2011-02-22 10:46:33','2011-02-22 10:46:34','COMPLETED','COMPLETED','','2011-02-22 10:46:34'),(8,2,8,'2011-02-22 10:46:34','2011-02-22 10:46:34','2011-02-22 10:46:34','COMPLETED','COMPLETED','','2011-02-22 10:46:34');
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
INSERT INTO BATCH_JOB_EXECUTION_CONTEXT (job_execution_id, short_context, serialized_context) VALUES (1,'{\"map\":[\"\"]}',NULL),(2,'{\"map\":[\"\"]}',NULL),(3,'{\"map\":[\"\"]}',NULL),(4,'{\"map\":[\"\"]}',NULL),(5,'{\"map\":[\"\"]}',NULL),(6,'{\"map\":[\"\"]}',NULL),(7,'{\"map\":[\"\"]}',NULL),(8,'{\"map\":[\"\"]}',NULL);
/*!40000 ALTER TABLE BATCH_JOB_EXECUTION_CONTEXT ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_SEQ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_EXECUTION_SEQ (
  id bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_EXECUTION_SEQ DISABLE KEYS */;
INSERT INTO BATCH_JOB_EXECUTION_SEQ (id) VALUES (8);
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
INSERT INTO BATCH_JOB_INSTANCE (job_instance_id, version, job_name, job_key) VALUES (1,0,'ApplyCustomerFeeChangesTaskJob','8e2ed1aee6f87f171f9836be205e7133'),(2,0,'ApplyHolidayChangesTaskJob','8e2ed1aee6f87f171f9836be205e7133'),(3,0,'GenerateMeetingsForCustomerAndSavingsTaskJob','8e2ed1aee6f87f171f9836be205e7133'),(4,0,'LoanArrearsAgingTaskJob','8e2ed1aee6f87f171f9836be205e7133'),(5,0,'LoanArrearsAndPortfolioAtRiskTaskJob','8e2ed1aee6f87f171f9836be205e7133'),(6,0,'BranchReportTaskJob','a1b211bdafbdc1031d867bf1fb40e2ee'),(7,0,'ProductStatusJob','a1b211bdafbdc1031d867bf1fb40e2ee'),(8,0,'SavingsIntPostingTaskJob','a1b211bdafbdc1031d867bf1fb40e2ee');
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
INSERT INTO BATCH_JOB_PARAMS (job_instance_id, type_cd, key_name, string_val, date_val, long_val, double_val) VALUES (1,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0),(2,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0),(3,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0),(4,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0),(5,'LONG','executionTime','','1970-01-01 05:30:00',1298351792031,0),(6,'LONG','executionTime','','1970-01-01 05:30:00',1298351792032,0),(7,'LONG','executionTime','','1970-01-01 05:30:00',1298351792032,0),(8,'LONG','executionTime','','1970-01-01 05:30:00',1298351792032,0);
/*!40000 ALTER TABLE BATCH_JOB_PARAMS ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_JOB_SEQ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_JOB_SEQ (
  id bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_JOB_SEQ DISABLE KEYS */;
INSERT INTO BATCH_JOB_SEQ (id) VALUES (8);
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
INSERT INTO BATCH_STEP_EXECUTION (step_execution_id, version, step_name, job_execution_id, start_time, end_time, status, commit_count, read_count, filter_count, write_count, read_skip_count, write_skip_count, process_skip_count, rollback_count, exit_code, exit_message, last_updated) VALUES (1,3,'ApplyCustomerFeeChangesTask-step-1',1,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32'),(2,3,'ApplyHolidayChangesTask-step-1',2,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32'),(3,3,'GenerateMeetingsForCustomerAndSavingsTask-step-1',3,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32'),(4,3,'LoanArrearsAgingTask-step-1',4,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32'),(5,3,'LoanArrearsAndPortfolioAtRiskTask-step-1',5,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32'),(6,3,'LoanArrearsAndPortfolioAtRiskTask-step-2',5,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32'),(7,3,'BranchReportTask-step-1',6,'2011-02-22 10:46:32','2011-02-22 10:46:32','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:32'),(8,3,'ProductStatus-step-1',7,'2011-02-22 10:46:33','2011-02-22 10:46:34','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:34'),(9,3,'SavingsIntPostingTask-step-1',8,'2011-02-22 10:46:34','2011-02-22 10:46:34','COMPLETED',1,0,0,0,0,0,0,0,'COMPLETED','','2011-02-22 10:46:34');
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
INSERT INTO BATCH_STEP_EXECUTION_CONTEXT (step_execution_id, short_context, serialized_context) VALUES (1,'{\"map\":[\"\"]}',NULL),(2,'{\"map\":[\"\"]}',NULL),(3,'{\"map\":[\"\"]}',NULL),(4,'{\"map\":[\"\"]}',NULL),(5,'{\"map\":[\"\"]}',NULL),(6,'{\"map\":[\"\"]}',NULL),(7,'{\"map\":[\"\"]}',NULL),(8,'{\"map\":[\"\"]}',NULL),(9,'{\"map\":[\"\"]}',NULL);
/*!40000 ALTER TABLE BATCH_STEP_EXECUTION_CONTEXT ENABLE KEYS */;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_SEQ;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE BATCH_STEP_EXECUTION_SEQ (
  id bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE BATCH_STEP_EXECUTION_SEQ DISABLE KEYS */;
INSERT INTO BATCH_STEP_EXECUTION_SEQ (id) VALUES (9);
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
INSERT INTO DATABASECHANGELOGLOCK (ID, LOCKED, LOCKGRANTED, LOCKEDBY) VALUES (1,0,NULL,NULL);
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
INSERT INTO QRTZ_LOCKS (lock_name) VALUES ('CALENDAR_ACCESS'),('JOB_ACCESS'),('MISFIRE_ACCESS'),('STATE_ACCESS'),('TRIGGER_ACCESS');
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
INSERT INTO accepted_payment_type (accepted_payment_type_id, transaction_id, payment_type_id) VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,1),(5,5,1),(7,1,3),(9,2,3),(11,3,3),(13,4,3),(15,5,3),(16,1,2),(17,2,2),(18,3,2),(19,4,2),(20,5,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account DISABLE KEYS */;
INSERT INTO account (account_id, global_account_num, customer_id, account_state_id, account_type_id, office_id, personnel_id, created_by, created_date, updated_by, updated_date, closed_date, version_no, offsetting_allowable, external_id) VALUES (1,'000100000000001',1,11,3,2,2,1,'2011-02-18',NULL,NULL,NULL,1,1,NULL),(2,'000100000000002',1,16,2,2,2,1,'2011-02-18',1,'2020-01-01',NULL,8,1,NULL),(3,'000100000000003',2,11,3,2,2,1,'2011-02-21',NULL,NULL,NULL,2,1,NULL),(4,'000100000000004',3,11,3,2,2,1,'2011-02-21',NULL,NULL,NULL,2,1,NULL),(5,'000100000000005',4,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL),(6,'000100000000006',5,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL),(7,'000100000000007',6,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL),(8,'000100000000008',7,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL),(9,'000100000000009',8,11,3,2,2,1,'2010-01-22',NULL,NULL,NULL,3,1,NULL),(10,'000100000000010',5,5,1,2,2,1,'2011-02-21',1,'2011-02-21',NULL,3,1,''),(11,'000100000000011',4,3,1,2,2,1,'2011-02-21',1,'2011-02-21',NULL,2,1,''),(12,'000100000000012',6,9,1,2,2,1,'2011-02-21',1,'2011-02-22',NULL,5,1,''),(13,'000100000000013',9,11,3,2,2,1,'2011-02-18',NULL,NULL,NULL,2,1,NULL),(14,'000100000000014',10,11,3,2,2,1,'2011-02-18',NULL,NULL,NULL,2,1,NULL),(15,'000100000000015',10,5,1,2,2,1,'2011-02-18',1,'2011-02-18',NULL,3,1,''),(16,'000100000000016',11,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL),(17,'000100000000017',12,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL),(18,'000100000000018',13,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL),(19,'000100000000019',14,11,3,2,2,1,'2011-02-22',NULL,NULL,NULL,2,1,NULL),(20,'000100000000020',3,3,1,2,2,1,'2011-02-22',1,'2011-02-22',NULL,2,1,''),(21,'000100000000021',15,11,3,2,2,1,'2011-02-24',NULL,NULL,NULL,1,1,NULL),(22,'000100000000022',16,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL),(23,'000100000000023',17,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL),(24,'000100000000024',18,11,3,2,2,1,'2010-10-11',NULL,NULL,NULL,2,1,NULL),(25,'000100000000025',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,''),(26,'000100000000026',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,''),(27,'000100000000027',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,''),(28,'000100000000028',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,''),(29,'000100000000029',18,5,1,2,2,1,'2010-10-11',1,'2010-10-11',NULL,4,1,''),(30,'000100000000030',19,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,1,1,NULL),(31,'000100000000031',20,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL),(32,'000100000000032',21,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,1,1,NULL),(33,'000100000000033',22,11,3,2,2,1,'2011-02-25',NULL,NULL,NULL,2,1,NULL),(34,'000100000000034',23,11,3,2,2,1,'2020-01-01',NULL,NULL,NULL,2,1,NULL),(35,'000100000000035',23,3,1,2,2,1,'2020-01-01',1,'2020-01-01',NULL,2,1,''),(36,'000100000000036',24,11,3,2,2,1,'2011-02-28',NULL,NULL,NULL,2,1,NULL),(37,'000100000000037',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,5,1,''),(38,'000100000000038',24,3,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,2,1,''),(39,'000100000000039',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,''),(40,'000100000000040',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,''),(41,'000100000000041',24,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,''),(42,'000100000000042',4,5,1,2,2,1,'2011-02-28',1,'2011-02-28',NULL,3,1,'');
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
INSERT INTO account_action (account_action_id, lookup_id) VALUES (1,167),(2,168),(3,169),(4,170),(5,171),(6,172),(7,173),(8,191),(9,192),(10,193),(11,214),(12,362),(13,364),(14,366),(15,547),(16,548),(17,549),(18,572),(19,573),(20,610);
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
INSERT INTO account_fees (account_fee_id, account_id, fee_id, fee_frequency, status, inherited_flag, start_date, end_date, account_fee_amnt, account_fee_amnt_currency_id, fee_amnt, fee_status, status_change_date, version_no, last_applied_date) VALUES (1,20,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL),(2,25,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09'),(3,26,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09'),(4,27,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09'),(5,28,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-09'),(6,29,3,NULL,NULL,NULL,NULL,NULL,'100.0000',2,'100.0000',1,NULL,0,'2010-11-16'),(7,35,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL),(8,37,1,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',1,'2011-02-28',0,'2011-03-07'),(9,38,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL),(10,39,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL),(11,40,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL),(12,41,2,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL),(13,42,1,NULL,NULL,NULL,NULL,NULL,'10.0000',2,'10.0000',NULL,NULL,0,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_notes DISABLE KEYS */;
INSERT INTO account_notes (account_notes_id, account_id, note, comment_date, officer_id) VALUES (1,2,'Active','2011-02-18',1),(2,10,'asdfasdf','2011-02-21',1),(3,11,'Application Approved','2011-02-21',1),(4,12,'Application Approved','2011-02-21',1),(5,15,'Application Approved','2011-02-18',1),(6,20,'Application Approved','2011-02-22',1),(7,25,'Application Approved','2010-10-11',1),(8,26,'Application Approved','2010-10-11',1),(9,27,'Approved','2010-10-11',1),(10,28,'Approved','2010-10-11',1),(11,29,'Approved','2010-10-11',1),(12,35,'Application Approved','2020-01-01',1),(13,37,'Approved','2011-02-28',1),(14,38,'Application','2011-02-28',1),(15,39,'Approved','2011-02-28',1),(16,40,'Approved','2011-02-28',1),(17,41,'Approved','2011-02-28',1),(18,42,'Approved','2011-02-28',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_payment DISABLE KEYS */;
INSERT INTO account_payment (payment_id, account_id, payment_type_id, currency_id, amount, receipt_number, voucher_number, check_number, payment_date, receipt_date, bank_name, comment) VALUES (1,2,1,2,'1000.0000','',NULL,NULL,'2011-02-18',NULL,NULL,NULL),(2,10,1,2,'1000.0000','',NULL,NULL,'2011-02-21','2011-02-21',NULL,NULL),(3,15,1,2,'1000.0000','',NULL,NULL,'2011-02-18','2011-02-18',NULL,NULL),(4,12,1,2,'100000.0000','',NULL,NULL,'2010-02-22','2010-02-22',NULL,NULL),(5,25,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL),(6,26,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL),(7,27,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL),(8,28,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL),(9,29,1,2,'1000.0000','',NULL,NULL,'2010-10-11','2010-10-11',NULL,NULL),(10,37,1,2,'1000.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL),(11,39,1,2,'99990.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL),(12,40,1,2,'99990.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL),(13,41,1,2,'99990.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL),(14,42,1,2,'1000.0000','',NULL,NULL,'2011-02-28','2011-02-28',NULL,NULL);
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
INSERT INTO account_state (account_state_id, lookup_id, prd_type_id, currently_in_use, status_description) VALUES (1,17,1,1,'Partial application'),(2,18,1,1,'Application pending approval'),(3,19,1,1,'Application approved'),(4,20,1,0,'Disbursed to loan officer'),(5,21,1,1,'Active in good standing'),(6,22,1,1,'Closed - obligation met'),(7,23,1,1,'Closed - written off'),(8,24,1,1,'Closed - rescheduled'),(9,25,1,1,'Active in bad standing'),(10,141,1,1,'Canceled'),(11,142,1,1,'Customer Account Active'),(12,143,1,1,'Customer Account Inactive'),(13,181,2,1,'Partial application'),(14,182,2,1,'Application pending approval'),(15,183,2,1,'Canceled'),(16,184,2,1,'Active'),(17,185,2,1,'Closed'),(18,210,2,1,'Inactive');
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
INSERT INTO account_state_flag (flag_id, lookup_id, status_id, flag_description, retain_flag) VALUES (1,174,10,'Withdraw',0),(2,175,10,'Rejected',0),(3,176,10,'Other',0),(4,211,15,'Withdraw',0),(5,212,15,'Rejected',0),(6,213,15,'Blacklisted',1),(7,571,10,'Loan reversal',0);
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_status_change_history DISABLE KEYS */;
INSERT INTO account_status_change_history (account_status_change_id, account_id, old_status, new_status, changed_by, changed_date) VALUES (1,2,NULL,14,1,'2011-02-18'),(2,2,14,16,1,'2011-02-18'),(3,10,2,2,1,'2011-02-21'),(4,10,2,3,1,'2011-02-21'),(5,11,2,2,1,'2011-02-21'),(6,11,2,3,1,'2011-02-21'),(7,12,2,2,1,'2011-02-21'),(8,12,2,3,1,'2011-02-21'),(9,10,3,5,1,'2011-02-21'),(10,15,2,2,1,'2011-02-18'),(11,15,2,3,1,'2011-02-18'),(12,15,3,5,1,'2011-02-18'),(13,12,3,5,1,'2010-02-22'),(14,12,5,9,2,'2011-02-22'),(15,20,2,2,1,'2011-02-22'),(16,20,2,3,1,'2011-02-22'),(17,25,2,2,1,'2010-10-11'),(18,25,2,3,1,'2010-10-11'),(19,25,3,5,1,'2010-10-11'),(20,26,2,2,1,'2010-10-11'),(21,26,2,3,1,'2010-10-11'),(22,26,3,5,1,'2010-10-11'),(23,27,2,2,1,'2010-10-11'),(24,27,2,3,1,'2010-10-11'),(25,27,3,5,1,'2010-10-11'),(26,28,2,2,1,'2010-10-11'),(27,28,2,3,1,'2010-10-11'),(28,28,3,5,1,'2010-10-11'),(29,29,2,2,1,'2010-10-11'),(30,29,2,3,1,'2010-10-11'),(31,29,3,5,1,'2010-10-11'),(32,35,2,2,1,'2020-01-01'),(33,35,2,3,1,'2020-01-01'),(34,37,2,2,1,'2011-02-28'),(35,37,2,3,1,'2011-02-28'),(36,37,3,5,1,'2011-02-28'),(37,38,2,2,1,'2011-02-28'),(38,38,2,3,1,'2011-02-28'),(39,39,2,2,1,'2011-02-28'),(40,39,2,3,1,'2011-02-28'),(41,39,3,5,1,'2011-02-28'),(42,40,2,2,1,'2011-02-28'),(43,40,2,3,1,'2011-02-28'),(44,40,3,5,1,'2011-02-28'),(45,41,2,2,1,'2011-02-28'),(46,41,2,3,1,'2011-02-28'),(47,41,3,5,1,'2011-02-28'),(48,42,2,2,1,'2011-02-28'),(49,42,2,3,1,'2011-02-28'),(50,42,3,5,1,'2011-02-28');
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE account_trxn DISABLE KEYS */;
INSERT INTO account_trxn (account_trxn_id, account_id, payment_id, personnel_id, account_action_id, currency_id, amount_currency_id, amount, due_date, comments, action_date, created_date, customer_id, installment_id, related_trxn_id) VALUES (1,2,1,1,6,NULL,2,'1000.0000','2011-02-18','','2011-02-18','2011-02-17 18:30:00',1,NULL,NULL),(2,10,2,1,10,NULL,2,'1000.0000','2011-02-21','-','2011-02-21','2011-02-21 10:32:16',5,0,NULL),(3,15,3,1,10,NULL,2,'1000.0000','2011-02-18','-','2011-02-18','2011-02-18 12:42:57',10,0,NULL),(4,12,4,1,10,NULL,2,'100000.0000','2010-02-22','-','2010-02-22','2010-02-22 05:15:52',6,0,NULL),(5,25,5,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:41:38',18,0,NULL),(6,26,6,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:43:13',18,0,NULL),(7,27,7,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:44:34',18,0,NULL),(8,28,8,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:45:48',18,0,NULL),(9,29,9,1,10,NULL,2,'1000.0000','2010-10-11','-','2010-10-11','2010-10-11 07:47:23',18,0,NULL),(10,37,10,1,10,NULL,2,'1000.0000','2011-02-28','-','2011-02-28','2011-02-28 07:23:06',24,0,NULL),(11,39,11,1,4,NULL,2,'10.0000','2011-02-28','-','2011-02-28','2011-02-28 08:51:51',24,0,NULL),(12,39,11,1,10,NULL,2,'100000.0000','2011-02-28','-','2011-02-28','2011-02-28 08:51:51',24,0,NULL),(13,40,12,1,4,NULL,2,'10.0000','2011-02-28','-','2011-02-28','2011-02-28 11:44:39',24,0,NULL),(14,40,12,1,10,NULL,2,'100000.0000','2011-02-28','-','2011-02-28','2011-02-28 11:44:39',24,0,NULL),(15,41,13,1,4,NULL,2,'10.0000','2011-02-28','-','2011-02-28','2011-02-28 11:45:06',24,0,NULL),(16,41,13,1,10,NULL,2,'100000.0000','2011-02-28','-','2011-02-28','2011-02-28 11:45:06',24,0,NULL),(17,42,14,1,10,NULL,2,'1000.0000','2011-02-28','-','2011-02-28','2011-02-28 11:46:24',4,0,NULL);
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
INSERT INTO account_type (account_type_id, lookup_id, description) VALUES (1,126,'Loan Account'),(2,127,'Savings Account'),(3,140,'Customer Account'),(4,126,'Individual Loan Account');
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
INSERT INTO activity (activity_id, parent_id, activity_name_lookup_id, description_lookup_id) VALUES (1,NULL,371,371),(2,1,372,372),(3,2,373,373),(4,2,374,374),(5,1,375,375),(6,5,376,376),(7,5,377,377),(8,1,378,378),(9,8,379,379),(10,8,380,380),(13,NULL,381,381),(14,13,382,382),(15,14,383,383),(16,14,384,384),(17,NULL,385,385),(18,17,386,386),(19,18,387,387),(20,18,388,388),(21,18,389,389),(22,17,390,390),(23,22,391,391),(24,22,392,392),(25,22,393,393),(33,NULL,394,394),(34,33,395,395),(35,34,396,396),(36,34,397,397),(37,34,398,398),(38,34,399,399),(39,34,400,400),(40,34,401,401),(41,34,402,402),(42,34,403,403),(43,34,404,404),(44,34,405,405),(46,34,407,407),(47,34,408,408),(48,34,409,409),(49,34,410,410),(50,34,411,411),(51,34,412,412),(52,34,413,413),(53,34,414,414),(54,34,415,415),(55,34,416,416),(56,33,417,417),(57,56,418,418),(58,56,419,419),(59,56,420,420),(60,56,421,421),(61,56,422,422),(62,56,423,423),(63,56,424,424),(64,56,425,425),(65,56,426,426),(66,56,427,427),(68,56,429,429),(69,56,430,430),(70,56,431,431),(71,56,432,432),(72,56,433,433),(73,56,434,434),(74,56,435,435),(75,56,436,436),(76,56,437,437),(77,56,438,438),(78,33,439,439),(79,78,440,440),(80,78,441,441),(81,78,442,442),(82,78,443,443),(83,78,444,444),(85,78,446,446),(86,78,447,447),(87,78,448,438),(88,78,449,449),(89,NULL,450,450),(90,89,451,451),(91,90,452,452),(92,90,453,453),(93,89,454,454),(94,93,455,455),(95,93,456,456),(96,89,457,457),(97,96,458,458),(98,96,459,459),(99,NULL,460,460),(100,99,461,461),(101,100,462,462),(102,100,463,463),(103,100,464,464),(104,100,465,465),(105,100,466,466),(106,100,467,467),(108,100,469,469),(109,100,470,470),(110,100,471,471),(113,99,474,474),(115,113,475,475),(116,113,476,476),(118,113,478,478),(119,113,479,479),(120,113,480,480),(121,34,481,481),(122,56,482,482),(126,34,483,483),(127,78,484,484),(128,78,485,485),(129,100,486,486),(131,113,487,487),(135,18,488,488),(136,NULL,489,489),(137,136,490,490),(138,136,491,491),(139,136,492,492),(140,136,493,493),(141,NULL,494,494),(145,141,498,498),(146,141,499,499),(147,141,500,500),(148,141,501,501),(149,141,502,502),(150,141,503,503),(151,141,504,504),(178,113,531,531),(179,100,532,532),(180,136,533,533),(181,136,534,534),(182,136,535,535),(183,136,536,536),(184,136,537,537),(185,136,538,538),(186,136,546,546),(187,136,551,551),(188,136,552,552),(189,113,553,553),(190,136,554,554),(191,136,555,555),(192,196,560,560),(193,13,562,562),(194,18,563,563),(195,90,561,561),(196,NULL,564,564),(197,196,565,565),(198,34,566,566),(199,56,567,567),(200,78,568,568),(201,196,569,569),(202,99,570,570),(203,NULL,574,574),(204,203,575,575),(205,203,579,579),(206,34,580,580),(208,34,582,582),(209,89,583,583),(210,209,584,584),(211,209,585,585),(213,203,587,587),(214,141,588,588),(215,141,589,589),(216,141,590,590),(217,113,591,591),(218,99,592,592),(219,1,593,593),(220,141,594,594),(221,141,595,595),(222,141,596,596),(223,141,597,597),(224,203,598,598),(225,141,602,602),(226,141,603,603),(227,NULL,605,605),(228,227,606,606),(229,145,607,607),(230,203,608,608),(231,150,611,611),(232,150,612,612),(233,196,619,619),(234,227,625,625),(235,1,627,627),(236,150,628,628),(237,150,629,629),(238,227,630,630),(239,227,631,631),(240,203,632,632),(241,227,633,633),(242,227,634,634),(243,203,635,635),(244,113,637,637),(245,34,638,638),(246,196,639,639);
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
INSERT INTO applied_upgrades (upgrade_id) VALUES (1277565300),(1277565388),(1277565389),(1277567194),(1277567768),(1277567885),(1277567949),(1277568944),(1277569001),(1277571296),(1277571560),(1277571792),(1277571837),(1277586926),(1277587117),(1277587199),(1277587465),(1277587818),(1277587878),(1277587947),(1277588038),(1277588072),(1277588240),(1277588373),(1277588885),(1277588973),(1277589055),(1277589236),(1277589321),(1277589383),(1278540763),(1278540832),(1278542100),(1278542119),(1278542138),(1278542152),(1278542171),(1279140399),(1279272090),(1280719328),(1280719447),(1280719676),(1280721170),(1280793109),(1282247229),(1282389745),(1282814250),(1283237728),(1283320210),(1283416834),(1283765911),(1284365506),(1284977483),(1284986654),(1285046834),(1285177663),(1285651956),(1285812348),(1286195484),(1286529235),(1286780611),(1287934290),(1288013750),(1288349766),(1288869198),(1289125815),(1289541994),(1289994929),(1290720085),(1291245955),(1292234934),(1292241366),(1294738016),(1294927843),(1295985566),(1296137314),(1298198335),(1299279218);
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
INSERT INTO batch_branch_report (branch_report_id, branch_id, run_date) VALUES (1,2,'2011-02-22');
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
INSERT INTO batch_client_summary (client_summary_id, branch_report_id, field_name, total, vpoor_total) VALUES (1,1,'active.savers.count','0','0'),(2,1,'replacements.count','0','0'),(3,1,'loan.account.dormant.count','0','0'),(4,1,'active.borrowers.count','3','3'),(5,1,'onholds.count','0','0'),(6,1,'saving.account.dormant.count','0','0'),(7,1,'portfolio.at.risk','0.00',NULL),(8,1,'group.count','2',''),(9,1,'center.count','1',''),(10,1,'dropouts.count','0','0'),(11,1,'dropout.rate','0.00','0.00'),(12,1,'active.members.count','7','7');
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
INSERT INTO batch_loan_arrears_aging (loan_arrears_aging_id, aging_period_id, branch_report_id, clients_aging, loans_aging, amount_aging, amount_aging_currency_id, amount_outstanding_aging, amount_outstanding_aging_currency_id, interest_aging, interest_aging_currency_id) VALUES (1,6,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2),(2,1,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2),(3,2,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2),(4,5,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2),(5,3,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2),(6,4,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2),(7,0,1,0,0,'0.0000',2,'0.0000',2,'0.0000',2);
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
INSERT INTO batch_loan_arrears_profile (loan_arrears_profile_id, branch_report_id, loans_in_arrears, clients_in_arrears, overdue_balance, overdue_balance_currency_id, unpaid_balance, unpaid_balance_currency_id, loans_at_risk, outstanding_amount_at_risk, outstanding_amount_at_risk_currency_id, overdue_amount_at_risk, overdue_amount_at_risk_currency_id, clients_at_risk) VALUES (1,1,0,0,'0.0000',2,'0.0000',2,0,'0.0000',2,'0.0000',2,0);
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
INSERT INTO batch_loan_details (loan_details_id, branch_report_id, product_name, number_of_loans_issued, loan_amount_issued, loan_amount_issued_currency_id, loan_interest_issued, loan_interest_issued_currency_id, number_of_loans_outstanding, loan_outstanding_amount, loan_outstanding_amount_currency_id, loan_outstanding_interest, loan_outstanding_interest_currency_id) VALUES (1,1,'GroupEmergencyLoan',0,'0.0000',2,'0.0000',2,0,'0.0000',2,'0.0000',2),(2,1,'WeeklyFlatLoanWithOneTimeFees',1,'100000.0000',2,'4603.0000',2,1,'100000.0000',2,'4603.0000',2),(3,1,'ClientEmergencyLoan',2,'2000.0000',2,'0.0000',2,2,'2000.0000',2,'0.0000',2);
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
INSERT INTO batch_staff_summary (staff_summary_id, branch_report_id, personnel_id, personnel_name, joining_date, active_borrowers, active_loans, center_count, client_count, loan_amount_outstanding, loan_amount_outstanding_currency_id, interest_fees_outstanding, interest_fees_outstanding_currency_id, portfolio_at_risk, total_clients_enrolled, clients_enrolled_this_month, loan_arrears_amount, loan_arrears_amount_currency_id) VALUES (1,1,2,'loan officer','2005-02-18',3,3,1,7,'2000.0000',2,'0.0000',2,'0.0000',7,3,'0.0000',2);
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
INSERT INTO batch_staffing_level_summary (staffing_level_summary_id, branch_report_id, role_id, role_name, personnel_count) VALUES (1,1,-1,'Total Staff',1),(2,1,1,'No Title',1);
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
INSERT INTO category_type (category_id, category_lookup_id) VALUES (1,81),(2,82),(3,83),(4,84),(5,86);
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
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE change_log DISABLE KEYS */;
INSERT INTO change_log (change_log_id, changed_by, modifier_name, entity_id, entity_type, changed_date, fields_changed) VALUES (1,1,'mifos',1,17,'2011-02-18',NULL),(2,1,'mifos',2,21,'2011-02-18',NULL),(3,1,'mifos',2,21,'2011-02-18',NULL),(4,1,'mifos',1,17,'2011-02-21',NULL),(5,1,'mifos',2,1,'2011-02-21',NULL),(6,1,'mifos',3,1,'2011-02-21',NULL),(7,1,'mifos',1,17,'2010-01-22',NULL),(8,1,'mifos',4,12,'2010-01-22',NULL),(9,1,'mifos',5,1,'2010-01-22',NULL),(10,1,'mifos',6,1,'2010-01-22',NULL),(11,1,'mifos',7,1,'2010-01-22',NULL),(12,1,'mifos',4,2,'2010-01-22',NULL),(13,1,'mifos',8,1,'2010-01-22',NULL),(14,1,'mifos',5,2,'2011-02-21',NULL),(15,1,'mifos',10,22,'2011-02-21',NULL),(16,1,'mifos',11,22,'2011-02-21',NULL),(17,1,'mifos',1,17,'2011-02-21',NULL),(18,1,'mifos',12,22,'2011-02-21',NULL),(19,1,'mifos',9,12,'2011-02-18',NULL),(20,1,'mifos',10,1,'2011-02-18',NULL),(21,1,'mifos',15,22,'2011-02-18',NULL),(22,1,'mifos',1,17,'2010-02-22',NULL),(23,1,'mifos',1,17,'2011-02-22',NULL),(24,1,'mifos',7,2,'2011-02-22',NULL),(25,1,'mifos',12,1,'2011-02-22',NULL),(26,1,'mifos',13,1,'2011-02-22',NULL),(27,1,'mifos',14,1,'2011-02-22',NULL),(28,1,'mifos',11,12,'2011-02-22',NULL),(29,1,'mifos',12,1,'2011-02-22',NULL),(30,1,'mifos',13,1,'2011-02-22',NULL),(31,1,'mifos',14,1,'2011-02-22',NULL),(32,1,'mifos',2,2,'2011-02-22',NULL),(33,1,'mifos',20,22,'2011-02-22',NULL),(34,1,'mifos',1,17,'2011-02-23',NULL),(35,1,'mifos',1,17,'2011-03-04',NULL),(36,1,'mifos',7,2,'2011-03-04',NULL),(37,1,'mifos',16,12,'2011-02-25',NULL),(38,1,'mifos',17,1,'2011-02-25',NULL),(39,1,'mifos',8,3,'2011-02-25',NULL),(40,1,'mifos',1,17,'2010-10-11',NULL),(41,1,'mifos',18,1,'2010-10-11',NULL),(42,1,'mifos',25,22,'2010-10-11',NULL),(43,1,'mifos',26,22,'2010-10-11',NULL),(44,1,'mifos',27,22,'2010-10-11',NULL),(45,1,'mifos',28,22,'2010-10-11',NULL),(46,1,'mifos',29,22,'2010-10-11',NULL),(47,1,'mifos',1,17,'2011-02-25',NULL),(48,1,'mifos',20,12,'2011-02-25',NULL),(49,1,'mifos',22,12,'2011-02-25',NULL),(50,1,'mifos',1,17,'2020-01-01',NULL),(51,1,'mifos',23,1,'2020-01-01',NULL),(52,1,'mifos',35,22,'2020-01-01',NULL),(53,1,'mifos',1,17,'2011-02-28',NULL),(54,1,'mifos',24,1,'2011-02-28',NULL),(55,1,'mifos',37,22,'2011-02-28',NULL),(56,1,'mifos',38,22,'2011-02-28',NULL),(57,1,'mifos',39,22,'2011-02-28',NULL),(58,1,'mifos',40,22,'2011-02-28',NULL),(59,1,'mifos',41,22,'2011-02-28',NULL),(60,1,'mifos',42,22,'2011-02-28',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE change_log_detail DISABLE KEYS */;
INSERT INTO change_log_detail (sequence_num, change_log_id, field_name, old_value, new_value) VALUES (1,1,'lastLogin','-','18/02/2011'),(2,2,'activationDate','-','18/02/2011'),(3,2,'Status','Application Pending Approval','Active'),(4,3,'savingsBalance','-','1000.0'),(5,4,'lastLogin','18/02/2011','21/02/2011'),(6,5,'Status','Application Pending Approval','Active'),(7,6,'Status','Application Pending Approval','Active'),(8,7,'lastLogin','21/02/2011','22/01/2010'),(9,8,'Status','Application Pending Approval','Active'),(10,9,'Status','Application Pending Approval','Active'),(11,10,'Status','Application Pending Approval','Active'),(12,11,'Status','Application Pending Approval','Active'),(13,12,'Frequency Of Installments','Week(s)','Month(s)'),(14,13,'Status','Application Pending Approval','Active'),(15,14,'Product Instance Name','EmergencyLoan','ClientEmergencyLoan'),(16,15,'Status','Application Pending Approval','Application Approved'),(17,16,'Status','Application Pending Approval','Application Approved'),(18,17,'lastLogin','22/01/2010','21/02/2011'),(19,18,'Status','Application Pending Approval','Application Approved'),(20,19,'Status','Application Pending Approval','Active'),(21,20,'Status','Application Pending Approval','Active'),(22,21,'Status','Application Pending Approval','Application Approved'),(23,22,'lastLogin','21/02/2011','22/02/2010'),(24,23,'lastLogin','22/02/2010','22/02/2011'),(25,24,'Fee Types','-','oneTimeFee'),(26,25,'Status','Application Pending Approval','Active'),(27,26,'Status','Application Pending Approval','Active'),(28,27,'Status','Application Pending Approval','Active'),(29,28,'Status','Application Pending Approval','Active'),(30,29,'Name','-','Default Group'),(31,29,'groupFlag','0','1'),(32,30,'groupFlag','0','1'),(33,30,'Name','-','Default Group'),(34,31,'Name','-','Default Group'),(35,31,'groupFlag','0','1'),(36,32,'Fee Types','-','disbursementFee'),(37,33,'Status','Application Pending Approval','Application Approved'),(38,34,'lastLogin','22/02/2011','23/02/2011'),(39,35,'lastLogin','23/02/2011','04/03/2011'),(40,36,'Min  Rate','24.0','1.0'),(41,36,'Max  Rate','24.0','99.0'),(42,37,'Status','Application Pending Approval','Active'),(43,38,'Status','Application Pending Approval','Active'),(44,39,'Applicable For','Centers','Clients'),(45,40,'lastLogin','04/03/2011','11/10/2010'),(46,41,'Status','Application Pending Approval','Active'),(47,42,'Status','Application Pending Approval','Application Approved'),(48,43,'Status','Application Pending Approval','Application Approved'),(49,44,'Status','Application Pending Approval','Application Approved'),(50,45,'Status','Application Pending Approval','Application Approved'),(51,46,'Status','Application Pending Approval','Application Approved'),(52,47,'lastLogin','11/10/2010','25/02/2011'),(53,48,'Status','Application Pending Approval','Active'),(54,49,'Status','Application Pending Approval','Active'),(55,50,'lastLogin','25/02/2011','01/01/2020'),(56,51,'Status','Application Pending Approval','Active'),(57,52,'Status','Application Pending Approval','Application Approved'),(58,53,'lastLogin','01/01/2020','28/02/2011'),(59,54,'Status','Application Pending Approval','Active'),(60,55,'Status','Application Pending Approval','Application Approved'),(61,56,'Status','Application Pending Approval','Application Approved'),(62,57,'Status','Application Pending Approval','Application Approved'),(63,58,'Status','Application Pending Approval','Application Approved'),(64,59,'Status','Application Pending Approval','Application Approved'),(65,60,'Status','Application Pending Approval','Application Approved');
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE client_perf_history DISABLE KEYS */;
INSERT INTO client_perf_history (id, customer_id, last_loan_amnt, last_loan_amnt_currency_id, total_active_loans, total_savings_amnt, total_savings_amnt_currency_id, delinquint_portfolio, delinquint_portfolio_currency_id) VALUES (1,2,'0.0000',2,0,'0.0000',2,'0.0000',2),(2,3,'0.0000',2,0,'0.0000',2,'0.0000',2),(3,5,'0.0000',2,1,'0.0000',2,'0.0000',2),(4,6,'0.0000',2,1,'0.0000',2,'0.0000',2),(5,7,'0.0000',2,0,'0.0000',2,'0.0000',2),(6,8,'0.0000',2,0,'0.0000',2,'0.0000',2),(7,10,'0.0000',2,1,'0.0000',2,'0.0000',2),(8,12,'0.0000',2,0,'0.0000',2,'0.0000',2),(9,13,'0.0000',2,0,'0.0000',2,'0.0000',2),(10,14,'0.0000',2,0,'0.0000',2,'0.0000',2),(11,17,'0.0000',2,0,'0.0000',2,'0.0000',2),(12,18,'0.0000',2,5,'0.0000',2,'0.0000',2),(13,23,'0.0000',2,0,'0.0000',2,'0.0000',2),(14,24,'0.0000',2,4,'0.0000',2,'0.0000',2);
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
INSERT INTO coa (coa_id, coa_name, glcode_id, category_type) VALUES (1,'ASSETS',1,'ASSET'),(2,'Cash and bank balances',2,NULL),(3,'Petty Cash Accounts',3,NULL),(4,'Cash 1',4,NULL),(5,'Cash 2',5,NULL),(6,'Bank Balances',6,NULL),(7,'Bank Account 1',7,NULL),(8,'Bank Account 2',8,NULL),(9,'Loan Portfolio',9,NULL),(10,'Loans and Advances',10,NULL),(11,'IGLoan',11,NULL),(12,'ManagedICICI-IGLoan',12,NULL),(13,'SPLoan',13,NULL),(14,'ManagedICICI-SPLoan',14,NULL),(15,'WFLoan',15,NULL),(16,'Managed WFLoan',16,NULL),(17,'Emergency Loans',17,NULL),(18,'Special Loans',18,NULL),(19,'Micro Enterprises Loans',19,NULL),(20,'Loans to clients',20,NULL),(21,'Loan Loss Provisions',21,NULL),(22,'Write-offs',22,NULL),(23,'LIABILITIES',23,'LIABILITY'),(24,'Interest Payable',24,NULL),(25,'Interest payable on clients savings',25,NULL),(26,'Interest on mandatory savings',26,NULL),(27,'Clients Deposits',27,NULL),(28,'Clients Deposits',28,NULL),(29,'Emergency Fund',29,NULL),(30,'Margin Money-1',30,NULL),(31,'Margin Money-2',31,NULL),(32,'Village Development Fund',32,NULL),(33,'Savings accounts',33,NULL),(34,'Mandatory Savings',34,NULL),(35,'Mandatory Savings',35,NULL),(36,'Mandatory Savings Accounts',36,NULL),(37,'INCOME',37,'INCOME'),(38,'Direct Income',38,NULL),(39,'Interest income from loans',39,NULL),(40,'Interest',40,NULL),(41,'Interest on loans',41,NULL),(42,'Penalty',42,NULL),(43,'Income from micro credit & lending activities',43,NULL),(44,'Processing Fees',44,NULL),(45,'Annual Subscription Fee',45,NULL),(46,'Emergency Loan Documentation Fee',46,NULL),(47,'Sale of Publication',47,NULL),(48,'Fines & Penalties',48,NULL),(49,'Miscelleneous Income',49,NULL),(50,'Fees',50,NULL),(51,'Income from 999 Account',51,NULL),(52,'EXPENDITURE',52,'EXPENDITURE'),(53,'Direct Expenditure',53,NULL),(54,'Cost of Funds',54,NULL),(55,'Interest on clients voluntary savings',55,NULL),(56,'Interest on clients mandatory savings',56,NULL);
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
INSERT INTO coahierarchy (coa_id, parent_coaid) VALUES (1,NULL),(2,1),(3,2),(4,3),(5,3),(6,2),(7,6),(8,6),(9,1),(10,9),(11,10),(12,10),(13,10),(14,10),(15,10),(16,10),(17,10),(18,10),(19,10),(20,10),(21,9),(22,21),(23,NULL),(24,23),(25,24),(26,25),(27,23),(28,27),(29,28),(30,28),(31,28),(32,28),(33,28),(34,23),(35,34),(36,35),(37,NULL),(38,37),(39,38),(40,39),(41,39),(42,39),(43,38),(44,43),(45,43),(46,43),(47,43),(48,43),(49,43),(50,43),(51,37),(52,NULL),(53,52),(54,53),(55,54),(56,54);
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
INSERT INTO config_key_value_integer (configuration_id, configuration_key, configuration_value) VALUES (1,'x',0),(2,' ',0),(3,'jasperReportIsHidden',1),(4,'loanIndividualMonitoringIsEnabled',0),(5,'repaymentSchedulesIndependentOfMeetingIsEnabled',0),(6,'CenterHierarchyExists',1),(7,'ClientCanExistOutsideGroup',1),(8,'GroupCanApplyLoans',1),(9,'minDaysBetweenDisbursalAndFirstRepaymentDay',1),(10,'maxDaysBetweenDisbursalAndFirstRepaymentDay',365),(11,'AdministrativeDocumentsIsEnabled',1);
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
INSERT INTO country (country_id, country_name, country_short_name) VALUES (1,'America','US'),(2,'India','IN'),(3,'Spain','ES'),(4,'England','ENG'),(5,'South Africa','SA'),(6,'United Kingdom','GB'),(7,'Iceland','IS'),(8,'Spain','ES'),(9,'France','FR'),(10,'China','CN'),(11,'Kenya','KE'),(12,'Tanzania','TZ'),(13,'Uganda','UG'),(14,'Algeria','DZ'),(15,'Bahrain','BH'),(16,'Comoros','KM'),(17,'Chad','TD'),(18,'Djibouti','DJ'),(19,'Egypt','EG'),(20,'Eritrea','ER'),(21,'Iraq','IQ'),(22,'Israel','IL'),(23,'Jordan','JO'),(24,'Kuwait','KW'),(25,'Lebanon','LB'),(26,'Libyan Arab Rebublic','LY'),(27,'Mauritania','MR'),(28,'Morocco','MA'),(29,'Oman','OM'),(30,'Qatar','QA'),(31,'Saudi Arabia','SA'),(32,'Somalia','SO'),(33,'Sudan','SD'),(34,'Syrian Arab Republic','SY'),(35,'Tunisia','TN'),(36,'United Arab Emirates','AE'),(37,'Yemen','YE'),(38,'Palestinian Territory, Occupied','PS'),(39,'Western Sahara','EH'),(40,'Angola','AO'),(41,'Brazil','BR'),(42,'Cape Verde','CV'),(43,'Guinea-Bissau','GW'),(44,'Equatorial Guinea','GQ'),(45,'Macau','MO'),(46,'Mozambique','MZ'),(47,'Portugal','PT'),(48,'Sao Tome and Principe','ST'),(49,'Cambodia','KH'),(50,'Laos','LA'),(51,'Hungary','HU');
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
INSERT INTO currency (currency_id, currency_name, rounding_amount, currency_code) VALUES (1,'US Dollar','1.000','USD'),(2,'Indian Rupee','1.000','INR'),(3,'Euro','1.000','EUR'),(4,'Pound Sterling','1.000','GBP'),(5,'United Arab Emirates dirham','1.000','AED'),(6,'Afghani','1.000','AFN'),(7,'Lek','1.000','ALL'),(8,'Armenian dram','1.000','AMD'),(9,'Netherlands Antillean guilder','1.000','ANG'),(10,'Kwanza','1.000','AOA'),(11,'Argentine peso','1.000','ARS'),(12,'Australian dollar','1.000','AUD'),(13,'Aruban guilder','1.000','AWG'),(14,'Azerbaijanian manat','1.000','AZN'),(15,'Convertible marks','1.000','BAM'),(16,'Barbados dollar','1.000','BBD'),(17,'Bangladeshi taka','1.000','BDT'),(18,'Bulgarian lev','1.000','BGN'),(19,'Bahraini dinar','1.000','BHD'),(20,'Burundian franc','1.000','BIF'),(21,'Bermudian dollar','1.000','BMD'),(22,'Brunei dollar','1.000','BND'),(23,'Boliviano','1.000','BOB'),(24,'Bolivian Mvdol','1.000','BOV'),(25,'Brazilian real','1.000','BRL'),(26,'Bahamian dollar','1.000','BSD'),(27,'Ngultrum','1.000','BTN'),(28,'Pula','1.000','BWP'),(29,'Belarussian ruble','1.000','BYR'),(30,'Belize dollar','1.000','BZD'),(31,'Canadian dollar','1.000','CAD'),(32,'Franc Congolais','1.000','CDF'),(33,'WIR euro','1.000','CHE'),(34,'Swiss franc','1.000','CHF'),(35,'WIR franc','1.000','CHW'),(36,'Unidad de Fomento','1.000','CLF'),(37,'Chilean peso','1.000','CLP'),(38,'Renminbi','1.000','CNY'),(39,'Colombian peso','1.000','COP'),(40,'Unidad de Valor Real','1.000','COU'),(41,'Costa Rican colon','1.000','CRC'),(42,'Cuban peso','1.000','CUP'),(43,'Cape Verde escudo','1.000','CVE'),(44,'Czech koruna','1.000','CZK'),(45,'Djibouti franc','1.000','DJF'),(46,'Danish krone','1.000','DKK'),(47,'Dominican peso','1.000','DOP'),(48,'Algerian dinar','1.000','DZD'),(49,'Kroon','1.000','EEK'),(50,'Egyptian pound','1.000','EGP'),(51,'Nakfa','1.000','ERN'),(52,'Ethiopian birr','1.000','ETB'),(53,'Fiji dollar','1.000','FJD'),(54,'Falkland Islands pound','1.000','FKP'),(55,'Lari','1.000','GEL'),(56,'Cedi','1.000','GHS'),(57,'Gibraltar pound','1.000','GIP'),(58,'Dalasi','1.000','GMD'),(59,'Guinea franc','1.000','GNF'),(60,'Quetzal','1.000','GTQ'),(61,'Guyana dollar','1.000','GYD'),(62,'Hong Kong dollar','1.000','HKD'),(63,'Lempira','1.000','HNL'),(64,'Croatian kuna','1.000','HRK'),(65,'Haiti gourde','1.000','HTG'),(66,'Forint','1.000','HUF'),(67,'Rupiah','1.000','IDR'),(68,'Israeli new sheqel','1.000','ILS'),(69,'Iraqi dinar','1.000','IQD'),(70,'Iranian rial','1.000','IRR'),(71,'Iceland krona','1.000','ISK'),(72,'Jamaican dollar','1.000','JMD'),(73,'Jordanian dinar','1.000','JOD'),(74,'Japanese yen','1.000','JPY'),(75,'Kenyan shilling','1.000','KES'),(76,'Som','1.000','KGS'),(77,'Riel','1.000','KHR'),(78,'Comoro franc','1.000','KMF'),(79,'North Korean won','1.000','KPW'),(80,'South Korean won','1.000','KRW'),(81,'Kuwaiti dinar','1.000','KWD'),(82,'Cayman Islands dollar','1.000','KYD'),(83,'Tenge','1.000','KZT'),(84,'Kip','1.000','LAK'),(85,'Lebanese pound','1.000','LBP'),(86,'Sri Lanka rupee','1.000','LKR'),(87,'Liberian dollar','1.000','LRD'),(88,'Loti','1.000','LSL'),(89,'Lithuanian litas','1.000','LTL'),(90,'Latvian lats','1.000','LVL'),(91,'Libyan dinar','1.000','LYD'),(92,'Moroccan dirham','1.000','MAD'),(93,'Moldovan leu','1.000','MDL'),(94,'Malagasy ariary','1.000','MGA'),(95,'Denar','1.000','MKD'),(96,'Kyat','1.000','MMK'),(97,'Tugrik','1.000','MNT'),(98,'Pataca','1.000','MOP'),(99,'Ouguiya','1.000','MRO'),(100,'Mauritius rupee','1.000','MUR'),(101,'Rufiyaa','1.000','MVR'),(102,'Kwacha','1.000','MWK'),(103,'Mexican peso','1.000','MXN'),(104,'Mexican Unidad de Inversion','1.000','MXV'),(105,'Malaysian ringgit','1.000','MYR'),(106,'Metical','1.000','MZN'),(107,'Namibian dollar','1.000','NAD'),(108,'Naira','1.000','NGN'),(109,'Cordoba oro','1.000','NIO'),(110,'Norwegian krone','1.000','NOK'),(111,'Nepalese rupee','1.000','NPR'),(112,'New Zealand dollar','1.000','NZD'),(113,'Rial Omani','1.000','OMR'),(114,'Balboa','1.000','PAB'),(115,'Nuevo sol','1.000','PEN'),(116,'Kina','1.000','PGK'),(117,'Philippine peso','1.000','PHP'),(118,'Pakistan rupee','1.000','PKR'),(119,'Zloty','1.000','PLN'),(120,'Guarani','1.000','PYG'),(121,'Qatari rial','1.000','QAR'),(122,'Romanian new leu','1.000','RON'),(123,'Serbian dinar','1.000','RSD'),(124,'Russian rouble','1.000','RUB'),(125,'Rwanda franc','1.000','RWF'),(126,'Saudi riyal','1.000','SAR'),(127,'Solomon Islands dollar','1.000','SBD'),(128,'Seychelles rupee','1.000','SCR'),(129,'Sudanese pound','1.000','SDG'),(130,'Swedish krona','1.000','SEK'),(131,'Singapore dollar','1.000','SGD'),(132,'Saint Helena pound','1.000','SHP'),(133,'Slovak koruna','1.000','SKK'),(134,'Leone','1.000','SLL'),(135,'Somali shilling','1.000','SOS'),(136,'Surinam dollar','1.000','SRD'),(137,'Dobra','1.000','STD'),(138,'Syrian pound','1.000','SYP'),(139,'Lilangeni','1.000','SZL'),(140,'Baht','1.000','THB'),(141,'Somoni','1.000','TJS'),(142,'Manat','1.000','TMM'),(143,'Tunisian dinar','1.000','TND'),(144,'Pa\'anga','1.000','TOP'),(145,'New Turkish lira','1.000','TRY'),(146,'Trinidad and Tobago dollar','1.000','TTD'),(147,'New Taiwan dollar','1.000','TWD'),(148,'Tanzanian shilling','1.000','TZS'),(149,'Hryvnia','1.000','UAH'),(150,'Uganda shilling','1.000','UGX'),(151,'US dollar (next day)','1.000','USN'),(152,'US dollar (same day)','1.000','USS'),(153,'Peso Uruguayo','1.000','UYU'),(154,'Uzbekistan som','1.000','UZS'),(155,'Venezuelan Bolivares Fuertes','1.000','VEF'),(156,'Vietnamese Dong','1.000','VND'),(157,'Vatu','1.000','VUV'),(158,'Samoan tala','1.000','WST'),(159,'CFA franc BEAC','1.000','XAF'),(160,'Silver','1.000','XAG'),(161,'Gold','1.000','XAU'),(162,'European Composite Unit','1.000','XBA'),(163,'European Monetary Unit','1.000','XBB'),(164,'European Unit of Account 9','1.000','XBC'),(165,'European Unit of Account 17','1.000','XBD'),(166,'East Caribbean dollar','1.000','XCD'),(167,'Special Drawing Rights','1.000','XDR'),(168,'UIC franc','1.000','XFU'),(169,'CFA Franc BCEAO','1.000','XOF'),(170,'Palladium','1.000','XPD'),(171,'CFP franc','1.000','XPF'),(172,'Platinum','1.000','XPT'),(173,'Code reserved for testing purposes','1.000','XTS'),(174,'No currency','1.000','XXX'),(175,'Yemeni rial','1.000','YER'),(176,'South African rand','1.000','ZAR'),(177,'Kwacha','1.000','ZMK'),(178,'Zimbabwe dollar','1.000','ZWD');
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer DISABLE KEYS */;
INSERT INTO customer (customer_id, customer_level_id, global_cust_num, loan_officer_id, customer_formedby_id, status_id, branch_id, display_name, first_name, last_name, second_last_name, display_address, external_id, date_of_birth, group_flag, trained, trained_date, parent_customer_id, created_date, updated_date, search_id, max_child_count, ho_updated, client_confidential, mfi_joining_date, government_id, customer_activation_date, created_by, updated_by, blacklisted, discriminator, version_no) VALUES (1,3,'0002-000000001',2,NULL,13,2,'Default Center',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-18',NULL,'1.1',4,NULL,NULL,'2011-02-18',NULL,'2011-02-18',1,NULL,0,'CENTER',10),(2,1,'0002-000000002',2,2,3,2,'Client - Mary Monthly','Client - Mary','Monthly','',NULL,'','2000-01-01',0,0,NULL,NULL,'2011-02-21','2011-02-21','1.2',0,NULL,NULL,'2011-02-21','','2011-02-21',1,1,0,'CLIENT',3),(3,1,'0002-000000003',2,2,3,2,'Stu1233266063395 Client1233266063395','Stu1233266063395','Client1233266063395','',NULL,'','2000-01-01',0,0,NULL,NULL,'2011-02-21','2011-02-21','1.3',0,NULL,NULL,'2011-02-21','','2011-02-21',1,1,0,'CLIENT',3),(4,2,'0002-000000004',2,2,9,2,'group1',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2010-01-22','2010-01-22','1.1.1',1,NULL,NULL,'2010-01-22',NULL,'2010-01-22',1,1,0,'GROUP',5),(5,1,'0002-000000005',2,2,3,2,'client1 lastname','client1','lastname','',NULL,'','1990-01-01',1,0,NULL,4,'2010-01-22','2010-01-22','1.1.1.1',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',4),(6,1,'0002-000000006',2,2,3,2,'Stu1233171716380 Client1233171716380','Stu1233171716380','Client1233171716380','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-01-22','2010-01-22','1.4',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',3),(7,1,'0002-000000007',2,2,3,2,'Client - Mary Monthly1','Client - Mary','Monthly1','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-01-22','2010-01-22','1.5',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',3),(8,1,'0002-000000008',2,2,3,2,'Client - Mia Monthly3rdFriday','Client - Mia','Monthly3rdFriday','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-01-22','2010-01-22','1.6',0,NULL,NULL,'2010-01-22','','2010-01-22',1,1,0,'CLIENT',3),(9,2,'0002-000000009',2,2,9,2,'groupWithoutLoan',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2011-02-18','2011-02-18','1.1.2',1,NULL,NULL,'2011-02-18',NULL,'2011-02-18',1,1,0,'GROUP',5),(10,1,'0002-000000010',2,2,3,2,'ClientWithLoan 20110221','ClientWithLoan','20110221','',NULL,'','1985-04-04',1,0,NULL,9,'2011-02-18','2011-02-18','1.1.2.1',0,NULL,NULL,'2011-02-18','','2011-02-18',1,1,0,'CLIENT',4),(11,2,'0002-000000011',2,2,9,2,'Default Group',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2011-02-22','2011-02-22','1.1.3',4,NULL,NULL,'2011-02-22',NULL,'2011-02-22',1,1,0,'GROUP',8),(12,1,'0002-000000012',2,2,3,2,'Stu1233266299995 Client1233266299995','Stu1233266299995','Client1233266299995','',NULL,'','1990-01-01',1,0,NULL,11,'2011-02-22','2011-02-22','1.1.3.1',0,NULL,NULL,'2011-02-22','','2011-02-22',1,1,0,'CLIENT',4),(13,1,'0002-000000013',2,2,3,2,'Stu1233266309851 Client1233266309851','Stu1233266309851','Client1233266309851','',NULL,'','1990-01-01',1,0,NULL,11,'2011-02-22','2011-02-22','1.1.3.2',0,NULL,NULL,'2011-02-22','','2011-02-22',1,1,0,'CLIENT',4),(14,1,'0002-000000014',2,2,3,2,'Stu1233266319760 Client1233266319760','Stu1233266319760','Client1233266319760','',NULL,'','1990-01-01',1,0,NULL,11,'2011-02-22','2011-02-22','1.1.3.3',0,NULL,NULL,'2011-02-22','','2011-02-22',1,1,0,'CLIENT',4),(15,3,'0002-000000015',2,NULL,13,2,'WeeklyMeetingCenter',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-24',NULL,'1.7',0,NULL,NULL,'2011-02-24',NULL,'2011-02-24',1,NULL,0,'CENTER',2),(16,2,'0002-000000016',2,2,9,2,'UpdateCustomPropertiesTestGroup',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,1,'2011-02-25','2011-02-25','1.1.4',1,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,1,0,'GROUP',5),(17,1,'0002-000000017',2,2,3,2,'UpdateCustomProperties TestClient','UpdateCustomProperties','TestClient','',NULL,'','1984-09-02',1,0,NULL,16,'2011-02-25','2011-02-25','1.1.4.1',0,NULL,NULL,'2011-02-25','','2011-02-25',1,1,0,'CLIENT',4),(18,1,'0002-000000018',2,2,3,2,'Client WeeklyTue','Client','WeeklyTue','',NULL,'','1990-01-01',0,0,NULL,NULL,'2010-10-11','2010-10-11','1.8',0,NULL,NULL,'2010-10-11','','2010-10-11',1,1,0,'CLIENT',3),(19,3,'0002-000000019',2,NULL,13,2,'CenterForMeetsOn3rdFriday',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-25',NULL,'1.9',1,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,NULL,0,'CENTER',4),(20,2,'0002-000000020',2,2,9,2,'GroupMeetsOn3rdFriday',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,19,'2011-02-25','2011-02-25','1.9.1',0,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,1,0,'GROUP',3),(21,3,'0002-000000021',2,NULL,13,2,'MonthlyMeetingCenter',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,NULL,'2011-02-25',NULL,'1.10',1,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,NULL,0,'CENTER',4),(22,2,'0002-000000022',2,2,9,2,'MonthlyGroup',NULL,NULL,NULL,NULL,'',NULL,NULL,0,NULL,21,'2011-02-25','2011-02-25','1.10.1',0,NULL,NULL,'2011-02-25',NULL,'2011-02-25',1,1,0,'GROUP',3),(23,1,'0002-000000023',2,2,3,2,'Holiday TestClient','Holiday','TestClient','',NULL,'','1984-09-02',1,0,NULL,11,'2020-01-01','2020-01-01','1.1.3.4',0,NULL,NULL,'2020-01-01','','2020-01-01',1,1,0,'CLIENT',4),(24,1,'0002-000000024',2,2,3,2,'WeeklyClient Monday','WeeklyClient','Monday','',NULL,'','1990-01-01',0,0,NULL,NULL,'2011-02-28','2011-02-28','1.11',0,NULL,NULL,'2011-02-28','','2011-02-28',1,1,0,'CLIENT',3);
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
INSERT INTO customer_account (account_id) VALUES (1),(3),(4),(5),(6),(7),(8),(9),(13),(14),(16),(17),(18),(19),(21),(22),(23),(24),(30),(31),(32),(33),(34),(36);
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_address_detail DISABLE KEYS */;
INSERT INTO customer_address_detail (customer_address_id, customer_id, locale_id, address_name, line_1, line_2, line_3, city, state, country, zip, address_status, phone_number, phone_number_stripped) VALUES (1,1,NULL,NULL,'','','','','','','',NULL,'',''),(2,2,NULL,NULL,'','','','','','','',NULL,'',''),(3,3,NULL,NULL,'','','','','','','',NULL,'',''),(4,4,NULL,NULL,'','','','','','','',NULL,'',''),(5,5,NULL,NULL,'','','','','','','',NULL,'',''),(6,6,NULL,NULL,'','','','','','','',NULL,'',''),(7,7,NULL,NULL,'','','','','','','',NULL,'',''),(8,8,NULL,NULL,'','','','','','','',NULL,'',''),(9,9,NULL,NULL,'','','','','','','',NULL,'',''),(10,10,NULL,NULL,'','','','','','','',NULL,'',''),(11,11,NULL,NULL,'','','','','','','',NULL,'',''),(12,12,NULL,NULL,'','','','','','','',NULL,'',''),(13,13,NULL,NULL,'','','','','','','',NULL,'',''),(14,14,NULL,NULL,'','','','','','','',NULL,'',''),(15,15,NULL,NULL,'','','','','','','',NULL,'',''),(16,16,NULL,NULL,'','','','','','','',NULL,'',''),(17,17,NULL,NULL,'','','','','','','',NULL,'',''),(18,18,NULL,NULL,'','','','','','','',NULL,'',''),(19,19,NULL,NULL,'','','','','','','',NULL,'',''),(20,20,NULL,NULL,'','','','','','','',NULL,'',''),(21,21,NULL,NULL,'','','','','','','',NULL,'',''),(22,22,NULL,NULL,'','','','','','','',NULL,'',''),(23,23,NULL,NULL,'','','','','','','',NULL,'',''),(24,24,NULL,NULL,'','','','','','','',NULL,'','');
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
INSERT INTO customer_attendance_types (attendance_id, attendance_lookup_id, description) VALUES (1,194,'Present'),(2,195,'Absent'),(3,196,'Approved leave'),(4,197,'Late');
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
INSERT INTO customer_detail (customer_id, ethinicity, citizenship, handicapped, business_activities, marital_status, education_level, num_children, gender, date_started, handicapped_details, poverty_status, poverty_lhood_pct) VALUES (2,NULL,NULL,NULL,NULL,66,NULL,NULL,50,NULL,NULL,41,NULL),(3,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL),(5,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL),(6,NULL,NULL,NULL,NULL,66,NULL,NULL,49,NULL,NULL,41,NULL),(7,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(8,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,41,NULL),(10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(13,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(14,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(17,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,43,NULL),(18,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(23,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,42,NULL),(24,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_hierarchy DISABLE KEYS */;
INSERT INTO customer_hierarchy (hierarchy_id, parent_id, customer_id, status, start_date, end_date, updated_by, updated_date) VALUES (1,1,4,1,'2010-01-22',NULL,NULL,NULL),(2,4,5,1,'2010-01-22',NULL,NULL,NULL),(3,1,9,1,'2011-02-18',NULL,NULL,NULL),(4,9,10,1,'2011-02-18',NULL,NULL,NULL),(5,1,11,1,'2011-02-22',NULL,NULL,NULL),(6,11,12,1,'2011-02-22',NULL,NULL,NULL),(7,11,13,1,'2011-02-22',NULL,NULL,NULL),(8,11,14,1,'2011-02-22',NULL,NULL,NULL),(9,1,16,1,'2011-02-25',NULL,NULL,NULL),(10,16,17,1,'2011-02-25',NULL,NULL,NULL),(11,19,20,1,'2011-02-25',NULL,NULL,NULL),(12,21,22,1,'2011-02-25',NULL,NULL,NULL),(13,11,23,1,'2020-01-01',NULL,NULL,NULL);
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
INSERT INTO customer_level (level_id, parent_level_id, level_name_id, interaction_flag, max_child_count, max_instance_count) VALUES (1,2,13,NULL,1,30),(2,3,12,NULL,2,12),(3,NULL,11,NULL,4,10);
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_meeting DISABLE KEYS */;
INSERT INTO customer_meeting (customer_meeting_id, meeting_id, customer_id) VALUES (1,1,1),(2,4,2),(3,5,3),(4,1,4),(5,1,5),(6,7,6),(7,9,7),(8,12,8),(9,1,9),(10,1,10),(11,1,11),(12,1,12),(13,1,13),(14,1,14),(15,25,15),(16,1,16),(17,1,17),(18,30,18),(19,37,19),(20,37,20),(21,38,21),(22,38,22),(23,1,23),(24,42,24);
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
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_name_detail DISABLE KEYS */;
INSERT INTO customer_name_detail (customer_name_id, customer_id, name_type, locale_id, salutation, first_name, middle_name, last_name, second_last_name, second_middle_name, display_name) VALUES (1,2,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395'),(2,2,3,NULL,228,'Client - Mary','','Monthly','',NULL,'Client - Mary Monthly'),(3,3,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395'),(4,3,3,NULL,47,'Stu1233266063395','','Client1233266063395','',NULL,'Stu1233266063395 Client1233266063395'),(5,5,3,NULL,47,'client1','','lastname','',NULL,'client1 lastname'),(6,5,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395'),(7,6,3,NULL,47,'Stu1233171716380','','Client1233171716380','',NULL,'Stu1233171716380 Client1233171716380'),(8,6,1,NULL,NULL,'Client1233266063395','','Client1233266063395','',NULL,'Client1233266063395 Client1233266063395'),(9,7,NULL,NULL,NULL,'','','','',NULL,''),(10,7,3,NULL,47,'Client - Mary','','Monthly1','',NULL,'Client - Mary Monthly1'),(11,8,3,NULL,228,'Client - Mia','','Monthly3rdFriday','',NULL,'Client - Mia Monthly3rdFriday'),(12,8,NULL,NULL,NULL,'','','','',NULL,''),(13,10,3,NULL,47,'ClientWithLoan','','20110221','',NULL,'ClientWithLoan 20110221'),(14,10,NULL,NULL,NULL,'','','','',NULL,''),(15,12,NULL,NULL,NULL,'','','','',NULL,''),(16,12,3,NULL,47,'Stu1233266299995','','Client1233266299995','',NULL,'Stu1233266299995 Client1233266299995'),(17,13,3,NULL,47,'Stu1233266309851','','Client1233266309851','',NULL,'Stu1233266309851 Client1233266309851'),(18,13,NULL,NULL,NULL,'','','','',NULL,''),(19,14,3,NULL,47,'Stu1233266319760','','Client1233266319760','',NULL,'Stu1233266319760 Client1233266319760'),(20,14,NULL,NULL,NULL,'','','','',NULL,''),(21,17,NULL,NULL,NULL,'','','','',NULL,''),(22,17,3,NULL,47,'UpdateCustomProperties','','TestClient','',NULL,'UpdateCustomProperties TestClient'),(23,18,3,NULL,47,'Client','','WeeklyTue','',NULL,'Client WeeklyTue'),(24,18,NULL,NULL,NULL,'','','','',NULL,''),(25,23,NULL,NULL,NULL,'','','','',NULL,''),(26,23,3,NULL,47,'Holiday','','TestClient','',NULL,'Holiday TestClient'),(27,24,NULL,NULL,NULL,'','','','',NULL,''),(28,24,3,NULL,47,'WeeklyClient','','Monday','',NULL,'WeeklyClient Monday');
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_note DISABLE KEYS */;
INSERT INTO customer_note (comment_id, customer_id, field_officer_id, comment_date, comment) VALUES (1,2,1,'2011-02-21','Active'),(2,3,1,'2011-02-21','Active\r\n	'),(3,4,1,'2010-01-22','Active'),(4,5,1,'2010-01-22','Active'),(5,6,1,'2010-01-22','Active'),(6,7,1,'2010-01-22','Active'),(7,8,1,'2010-01-22','Active'),(8,9,1,'2011-02-18','Active'),(9,10,1,'2011-02-18','Active'),(10,12,1,'2011-02-22','Active'),(11,13,1,'2011-02-22','Active'),(12,14,1,'2011-02-22','Active'),(13,11,1,'2011-02-22','Active'),(14,16,1,'2011-02-25','UpdateCustomPropertiesTest'),(15,17,1,'2011-02-25','TestClient'),(16,18,1,'2010-10-11','Active'),(17,20,1,'2011-02-25','activating the group'),(18,22,1,'2011-02-25','activating the group'),(19,23,1,'2020-01-01','Active'),(20,24,1,'2011-02-28','Active');
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_picture DISABLE KEYS */;
INSERT INTO customer_picture (picture_id, customer_id, picture) VALUES (1,2,NULL),(2,3,NULL),(3,5,NULL),(4,6,NULL),(5,7,NULL),(6,8,NULL),(7,10,NULL),(8,12,NULL),(9,13,NULL),(10,14,NULL),(11,17,NULL),(12,18,NULL),(13,23,NULL),(14,24,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=291 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE customer_schedule DISABLE KEYS */;
INSERT INTO customer_schedule (id, account_id, customer_id, currency_id, action_date, misc_fees, misc_fees_currency_id, misc_fees_paid, misc_fees_paid_currency_id, misc_penalty, misc_penalty_currency_id, misc_penalty_paid, misc_penalty_paid_currency_id, payment_status, installment_id, payment_date, parent_flag, version_no) VALUES (1,1,1,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1),(2,1,1,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1),(3,1,1,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1),(4,1,1,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1),(5,1,1,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1),(6,1,1,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1),(7,1,1,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1),(8,1,1,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1),(9,1,1,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1),(10,1,1,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1),(11,3,2,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(12,3,2,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(13,3,2,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(14,3,2,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(15,3,2,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(16,3,2,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(17,3,2,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(18,3,2,NULL,'2011-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(19,3,2,NULL,'2011-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(20,3,2,NULL,'2011-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(21,4,3,NULL,'2011-02-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(22,4,3,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(23,4,3,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(24,4,3,NULL,'2011-03-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(25,4,3,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(26,4,3,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(27,4,3,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(28,4,3,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(29,4,3,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(30,4,3,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(31,5,4,NULL,'2010-01-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(32,5,4,NULL,'2010-01-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(33,5,4,NULL,'2010-02-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(34,5,4,NULL,'2010-02-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(35,5,4,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(36,5,4,NULL,'2010-02-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(37,5,4,NULL,'2010-03-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(38,5,4,NULL,'2010-03-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(39,5,4,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(40,5,4,NULL,'2010-03-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(41,6,5,NULL,'2010-01-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(42,6,5,NULL,'2010-01-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(43,6,5,NULL,'2010-02-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(44,6,5,NULL,'2010-02-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(45,6,5,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(46,6,5,NULL,'2010-02-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(47,6,5,NULL,'2010-03-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(48,6,5,NULL,'2010-03-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(49,6,5,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(50,6,5,NULL,'2010-03-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(51,7,6,NULL,'2010-01-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(52,7,6,NULL,'2010-01-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(53,7,6,NULL,'2010-02-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(54,7,6,NULL,'2010-02-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(55,7,6,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(56,7,6,NULL,'2010-02-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(57,7,6,NULL,'2010-03-05','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(58,7,6,NULL,'2010-03-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(59,7,6,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(60,7,6,NULL,'2010-03-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(61,8,7,NULL,'2010-02-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(62,8,7,NULL,'2010-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(63,8,7,NULL,'2010-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(64,8,7,NULL,'2010-05-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(65,8,7,NULL,'2010-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(66,8,7,NULL,'2010-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(67,8,7,NULL,'2010-08-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(68,8,7,NULL,'2010-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(69,8,7,NULL,'2010-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(70,8,7,NULL,'2010-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(71,9,8,NULL,'2010-02-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(72,9,8,NULL,'2010-03-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(73,9,8,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(74,9,8,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(75,9,8,NULL,'2010-06-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(76,9,8,NULL,'2010-07-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(77,9,8,NULL,'2010-08-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(78,9,8,NULL,'2010-09-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(79,9,8,NULL,'2010-10-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(80,9,8,NULL,'2010-11-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(81,13,9,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(82,13,9,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(83,13,9,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(84,13,9,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(85,13,9,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(86,13,9,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(87,13,9,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(88,13,9,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(89,13,9,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(90,13,9,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(91,14,10,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(92,14,10,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(93,14,10,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(94,14,10,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(95,14,10,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(96,14,10,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(97,14,10,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(98,14,10,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(99,14,10,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(100,14,10,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(101,5,4,NULL,'2010-04-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0),(102,5,4,NULL,'2010-04-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0),(103,5,4,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0),(104,5,4,NULL,'2010-04-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0),(105,5,4,NULL,'2010-04-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0),(106,5,4,NULL,'2010-05-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0),(107,5,4,NULL,'2010-05-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0),(108,5,4,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0),(109,5,4,NULL,'2010-05-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0),(110,5,4,NULL,'2010-06-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0),(111,6,5,NULL,'2010-04-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0),(112,6,5,NULL,'2010-04-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0),(113,6,5,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0),(114,6,5,NULL,'2010-04-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0),(115,6,5,NULL,'2010-04-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0),(116,6,5,NULL,'2010-05-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0),(117,6,5,NULL,'2010-05-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0),(118,6,5,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0),(119,6,5,NULL,'2010-05-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0),(120,6,5,NULL,'2010-06-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0),(121,7,6,NULL,'2010-04-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0),(122,7,6,NULL,'2010-04-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0),(123,7,6,NULL,'2010-04-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0),(124,7,6,NULL,'2010-04-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0),(125,7,6,NULL,'2010-04-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0),(126,7,6,NULL,'2010-05-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0),(127,7,6,NULL,'2010-05-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0),(128,7,6,NULL,'2010-05-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0),(129,7,6,NULL,'2010-05-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0),(130,7,6,NULL,'2010-06-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0),(131,8,7,NULL,'2010-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0),(132,8,7,NULL,'2011-01-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0),(133,8,7,NULL,'2011-02-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0),(134,8,7,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0),(135,8,7,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0),(136,8,7,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0),(137,8,7,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0),(138,8,7,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0),(139,8,7,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0),(140,8,7,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0),(141,9,8,NULL,'2010-12-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,11,NULL,NULL,0),(142,9,8,NULL,'2011-01-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,12,NULL,NULL,0),(143,9,8,NULL,'2011-02-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,13,NULL,NULL,0),(144,9,8,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,14,NULL,NULL,0),(145,9,8,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,15,NULL,NULL,0),(146,9,8,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,16,NULL,NULL,0),(147,9,8,NULL,'2011-06-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,17,NULL,NULL,0),(148,9,8,NULL,'2011-07-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,18,NULL,NULL,0),(149,9,8,NULL,'2011-08-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,19,NULL,NULL,0),(150,9,8,NULL,'2011-09-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,20,NULL,NULL,0),(151,17,12,NULL,'2011-02-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(152,17,12,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1),(153,17,12,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1),(154,17,12,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1),(155,17,12,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1),(156,17,12,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1),(157,17,12,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1),(158,17,12,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1),(159,17,12,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1),(160,17,12,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1),(161,18,13,NULL,'2011-02-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(162,18,13,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1),(163,18,13,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1),(164,18,13,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1),(165,18,13,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1),(166,18,13,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1),(167,18,13,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1),(168,18,13,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1),(169,18,13,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1),(170,18,13,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1),(171,19,14,NULL,'2011-02-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(172,19,14,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1),(173,19,14,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1),(174,19,14,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1),(175,19,14,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1),(176,19,14,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1),(177,19,14,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1),(178,19,14,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1),(179,19,14,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1),(180,19,14,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1),(181,16,11,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(182,16,11,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(183,16,11,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(184,16,11,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(185,16,11,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(186,16,11,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(187,16,11,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(188,16,11,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(189,16,11,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(190,16,11,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(191,21,15,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1),(192,21,15,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1),(193,21,15,NULL,'2011-03-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1),(194,21,15,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1),(195,21,15,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1),(196,21,15,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1),(197,21,15,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1),(198,21,15,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1),(199,21,15,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1),(200,21,15,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1),(201,22,16,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(202,22,16,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(203,22,16,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(204,22,16,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(205,22,16,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(206,22,16,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(207,22,16,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(208,22,16,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(209,22,16,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(210,22,16,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(211,23,17,NULL,'2011-02-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(212,23,17,NULL,'2011-03-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(213,23,17,NULL,'2011-03-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(214,23,17,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(215,23,17,NULL,'2011-03-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(216,23,17,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(217,23,17,NULL,'2011-04-08','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(218,23,17,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(219,23,17,NULL,'2011-04-22','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(220,23,17,NULL,'2011-04-29','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(221,24,18,NULL,'2010-10-12','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(222,24,18,NULL,'2010-10-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(223,24,18,NULL,'2010-10-26','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(224,24,18,NULL,'2010-11-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(225,24,18,NULL,'2010-11-09','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(226,24,18,NULL,'2010-11-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(227,24,18,NULL,'2010-11-23','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(228,24,18,NULL,'2010-11-30','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(229,24,18,NULL,'2010-12-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(230,24,18,NULL,'2010-12-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(231,30,19,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1),(232,30,19,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1),(233,30,19,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1),(234,30,19,NULL,'2011-06-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1),(235,30,19,NULL,'2011-07-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1),(236,30,19,NULL,'2011-08-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1),(237,30,19,NULL,'2011-09-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1),(238,30,19,NULL,'2011-10-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1),(239,30,19,NULL,'2011-11-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1),(240,30,19,NULL,'2011-12-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1),(241,31,20,NULL,'2011-03-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(242,31,20,NULL,'2011-04-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(243,31,20,NULL,'2011-05-20','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(244,31,20,NULL,'2011-06-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(245,31,20,NULL,'2011-07-15','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(246,31,20,NULL,'2011-08-19','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(247,31,20,NULL,'2011-09-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(248,31,20,NULL,'2011-10-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(249,31,20,NULL,'2011-11-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(250,31,20,NULL,'2011-12-16','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(251,32,21,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1),(252,32,21,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1),(253,32,21,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1),(254,32,21,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1),(255,32,21,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1),(256,32,21,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1),(257,32,21,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1),(258,32,21,NULL,'2011-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1),(259,32,21,NULL,'2011-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1),(260,32,21,NULL,'2011-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1),(261,33,22,NULL,'2011-03-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(262,33,22,NULL,'2011-04-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(263,33,22,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(264,33,22,NULL,'2011-06-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(265,33,22,NULL,'2011-07-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(266,33,22,NULL,'2011-08-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(267,33,22,NULL,'2011-09-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(268,33,22,NULL,'2011-10-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(269,33,22,NULL,'2011-11-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(270,33,22,NULL,'2011-12-01','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(271,34,23,NULL,'2020-01-03','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(272,34,23,NULL,'2020-01-10','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(273,34,23,NULL,'2020-01-17','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(274,34,23,NULL,'2020-01-24','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(275,34,23,NULL,'2020-01-31','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(276,34,23,NULL,'2020-02-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(277,34,23,NULL,'2020-02-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(278,34,23,NULL,'2020-02-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(279,34,23,NULL,'2020-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(280,34,23,NULL,'2020-03-06','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0),(281,36,24,NULL,'2011-02-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0),(282,36,24,NULL,'2011-03-07','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0),(283,36,24,NULL,'2011-03-14','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0),(284,36,24,NULL,'2011-03-21','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0),(285,36,24,NULL,'2011-03-28','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0),(286,36,24,NULL,'2011-04-04','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0),(287,36,24,NULL,'2011-04-11','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0),(288,36,24,NULL,'2011-04-18','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0),(289,36,24,NULL,'2011-04-25','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0),(290,36,24,NULL,'2011-05-02','0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0);
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
INSERT INTO customer_state (status_id, status_lookup_id, level_id, description, currently_in_use) VALUES (1,1,1,'Customer Was Partial',1),(2,2,1,'Customer Was Pending',1),(3,3,1,'Customer Was Active',1),(4,4,1,'Customer Was Hold',1),(5,5,1,'Customer Was Cancel',1),(6,6,1,'Customer Was Close',1),(7,7,2,'Customer Was Partial',1),(8,8,2,'Customer Was Pending',1),(9,9,2,'Customer Was Active',1),(10,10,2,'Customer Was Hold',1),(11,11,2,'Customer Was Cancel',1),(12,12,2,'Customer Was Close',1),(13,13,3,'Customer Was Active',1),(14,14,3,'Customer Was Inactive',1);
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
INSERT INTO customer_state_flag (flag_id, flag_lookup_id, status_id, flag_description, isblacklisted) VALUES (1,28,5,'Withdraw',0),(2,29,5,'Rejected',0),(3,30,5,'Blacklisted',1),(4,31,5,'Duplicate',0),(5,34,5,'Other',0),(6,32,6,'Transferred',0),(7,31,6,'Duplicate',0),(8,30,6,'Blacklisted',1),(9,33,6,'Left program',0),(10,34,6,'Other',0),(11,28,11,'Withdraw',0),(12,29,11,'Rejected',0),(13,30,11,'Blacklisted',1),(14,31,11,'Duplicate',0),(15,34,11,'Other',0),(16,32,12,'Transferred',0),(17,31,12,'Duplicate',0),(18,30,12,'Blacklisted',1),(19,33,12,'Left program',0),(20,34,12,'Other',0);
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
INSERT INTO entity_master (entity_type_id, entity_type) VALUES (1,'Client'),(2,'LoanProduct'),(3,'SavingsProduct'),(4,'ProductCategory'),(5,'ProductConfiguration'),(6,'Fees'),(7,'Accounts'),(8,'Admin'),(9,'Checklist'),(10,'Configuration'),(11,'Customer'),(12,'Group'),(13,'Login'),(14,'Meeting'),(15,'Office'),(16,'Penalty'),(17,'Personnel'),(19,'Roleandpermission'),(20,'Center'),(21,'Savings'),(22,'Loan'),(23,'BulkEntry');
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
INSERT INTO event_sources (id, entity_type_id, event_id, description) VALUES (1,1,1,'Create Client'),(2,22,1,'Create Loan'),(3,1,2,'View Client'),(4,12,1,'Create Group'),(5,22,3,'Approve Loan'),(6,1,4,'Close Client'),(7,22,2,'View Loan'),(8,12,2,'View Group'),(9,20,1,'Create Center'),(10,20,2,'View Center'),(11,22,5,'Disburse Loan'),(12,21,1,'Create Savings'),(13,21,2,'View Savings'),(14,15,1,'Create Office'),(15,17,1,'Create Personnel');
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
INSERT INTO events (id, name) VALUES (1,'Create'),(2,'View'),(3,'Approve'),(4,'Close'),(5,'Disburse');
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
INSERT INTO fee_formula_master (formulaid, forumla_lookup_id) VALUES (1,149),(2,150),(3,151);
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
INSERT INTO fee_frequency (fee_frequency_id, fee_id, fee_frequencytype_id, frequency_payment_id, frequency_meeting_id) VALUES (1,1,2,1,NULL),(2,2,2,2,NULL),(3,3,1,NULL,24),(4,4,2,1,NULL),(5,5,2,1,NULL);
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
INSERT INTO fee_frequency_type (fee_frequency_id, lookup_id) VALUES (1,558),(2,559);
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
INSERT INTO fee_payment (fee_payment_id, fee_payment_lookup_id) VALUES (1,146),(2,147),(3,148);
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
INSERT INTO fee_payments_categories_type (fee_payments_category_type_id, fee_payment_id, category_id, fee_type_id) VALUES (1,1,1,1),(2,1,1,1),(3,1,1,1);
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
INSERT INTO fee_status (status_id, status_lookup_id) VALUES (1,165),(2,166);
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
INSERT INTO fee_trxn_detail (fee_trxn_detail_id, account_trxn_id, account_fee_id, fee_amount_currency_id, fee_amount) VALUES (1,11,10,2,'10.0000'),(2,13,11,2,'10.0000'),(3,15,12,2,'10.0000');
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
INSERT INTO fee_type (fee_type_id, fee_lookup_id, flat_or_rate, formula) VALUES (1,1,NULL,NULL),(2,1,NULL,NULL),(3,2,NULL,NULL),(4,3,NULL,NULL),(5,3,NULL,NULL);
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
INSERT INTO fee_update_type (fee_update_type_id, lookup_id) VALUES (1,556),(2,557);
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
INSERT INTO fees (fee_id, global_fee_num, fee_name, fee_payments_category_type_id, office_id, glcode_id, status, category_id, rate_or_amount, rate_or_amount_currency_id, rate_flat_falg, created_date, created_by, updated_date, updated_by, update_flag, formula_id, default_admin_fee, fee_amount, fee_amount_currency_id, rate, version_no, discriminator) VALUES (1,NULL,'oneTimeFee',NULL,1,50,1,5,NULL,NULL,NULL,'2011-02-22',1,NULL,NULL,0,NULL,NULL,'10.0000',2,NULL,0,'AMOUNT'),(2,NULL,'disbursementFee',NULL,1,50,1,5,NULL,NULL,NULL,'2011-02-22',1,NULL,NULL,0,NULL,NULL,'10.0000',2,NULL,0,'AMOUNT'),(3,NULL,'loanWeeklyFee',NULL,1,50,1,5,NULL,NULL,NULL,'2011-03-04',1,NULL,NULL,0,NULL,NULL,'100.0000',2,NULL,0,'AMOUNT'),(4,NULL,'fixedFeePerAmountAndInterest',NULL,1,50,1,5,NULL,NULL,NULL,'2011-03-04',1,NULL,NULL,0,2,NULL,NULL,NULL,'100.00000',0,'RATE'),(5,NULL,'fixedFeePerInterest',NULL,1,50,1,5,NULL,NULL,NULL,'2011-03-04',1,NULL,NULL,0,3,NULL,NULL,NULL,'20.00000',0,'RATE');
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
INSERT INTO field_configuration (field_config_id, field_name, entity_id, mandatory_flag, hidden_flag, parent_field_config_id) VALUES (1,'MiddleName',1,0,0,NULL),(2,'MiddleName',17,0,0,NULL),(3,'SecondLastName',1,0,0,NULL),(4,'SecondLastName',17,0,0,NULL),(5,'GovernmentId',1,0,0,NULL),(6,'GovernmentId',17,0,0,NULL),(7,'ExternalId',1,0,0,NULL),(8,'ExternalId',12,0,0,NULL),(9,'ExternalId',20,0,0,NULL),(10,'Ethinicity',1,0,0,NULL),(11,'Citizenship',1,0,0,NULL),(12,'Handicapped',1,0,0,NULL),(13,'BusinessActivities',1,0,0,NULL),(14,'EducationLevel',1,0,0,NULL),(15,'Photo',1,0,0,NULL),(16,'SpouseFatherMiddleName',1,0,0,NULL),(17,'SpouseFatherSecondLastName',1,0,0,NULL),(18,'Trained',1,0,0,NULL),(19,'Trained',12,0,0,NULL),(20,'TrainedDate',1,0,0,NULL),(21,'TrainedDate',12,0,0,NULL),(22,'Address',1,0,0,NULL),(23,'Address',12,0,0,NULL),(24,'Address',20,0,0,NULL),(25,'Address1',1,0,0,22),(26,'Address1',12,0,0,23),(27,'Address1',20,0,0,24),(28,'Address2',1,0,0,22),(29,'Address2',12,0,0,23),(30,'Address2',20,0,0,24),(31,'Address3',1,0,0,22),(32,'Address3',12,0,0,23),(33,'Address3',20,0,0,24),(34,'Address3',15,0,0,NULL),(35,'Address3',17,0,0,NULL),(36,'City',1,0,0,22),(37,'City',12,0,0,23),(38,'City',20,0,0,24),(39,'State',1,0,0,22),(40,'State',12,0,0,23),(41,'State',20,0,0,24),(42,'State',15,0,0,NULL),(43,'State',17,0,0,NULL),(44,'Country',1,0,0,22),(45,'Country',12,0,0,23),(46,'Country',20,0,0,24),(47,'Country',15,0,0,NULL),(48,'Country',17,0,0,NULL),(49,'PostalCode',1,0,0,22),(50,'PostalCode',12,0,0,23),(51,'PostalCode',20,0,0,24),(52,'PostalCode',15,0,0,NULL),(53,'PostalCode',17,0,0,NULL),(54,'PhoneNumber',1,0,0,NULL),(55,'PhoneNumber',12,0,0,NULL),(56,'PhoneNumber',20,0,0,NULL),(57,'PhoneNumber',17,0,0,NULL),(58,'PurposeOfLoan',22,0,0,NULL),(59,'CollateralType',22,0,0,NULL),(60,'CollateralNotes',22,0,0,NULL),(61,'ReceiptId',1,0,0,NULL),(62,'ReceiptId',12,0,0,NULL),(63,'ReceiptId',20,0,0,NULL),(64,'ReceiptId',21,0,0,NULL),(65,'ReceiptId',22,0,0,NULL),(66,'ReceiptId',23,0,0,NULL),(67,'ReceiptDate',1,0,0,NULL),(68,'ReceiptDate',12,0,0,NULL),(69,'ReceiptDate',20,0,0,NULL),(70,'ReceiptDate',21,0,0,NULL),(71,'ReceiptDate',22,0,0,NULL),(72,'ReceiptDate',23,0,0,NULL),(73,'PovertyStatus',1,1,0,NULL),(74,'AssignClients',1,0,0,NULL),(75,'Address2',15,0,0,NULL),(76,'Address2',17,0,0,NULL),(77,'Address1',15,0,0,NULL),(78,'Address1',17,0,0,NULL),(79,'City',15,0,0,NULL),(80,'SourceOfFund',22,0,0,NULL),(81,'MaritalStatus',1,0,0,NULL),(82,'NumberOfChildren',1,0,0,NULL),(83,'ExternalId',22,0,0,NULL),(84,'SpouseFatherInformation',1,0,0,NULL),(85,'FamilyDetails',1,1,0,NULL);
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
INSERT INTO financial_action (fin_action_id, lookup_id) VALUES (1,198),(2,199),(3,200),(5,201),(8,202),(9,203),(10,204),(11,205),(12,206),(13,207),(14,208),(15,209),(7,215),(4,229),(6,361),(16,363),(17,365),(18,367),(19,368),(20,369),(21,370),(22,550),(23,609);
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
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE financial_trxn DISABLE KEYS */;
INSERT INTO financial_trxn (trxn_id, account_trxn_id, related_fin_trxn, currency_id, fin_action_id, glcode_id, posted_amount, posted_amount_currency_id, balance_amount, balance_amount_currency_id, action_date, posted_date, posted_by, accounting_updated, notes, debit_credit_flag) VALUES (1,1,NULL,NULL,10,36,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'',1),(2,1,NULL,NULL,10,7,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'',0),(3,2,NULL,NULL,7,14,'1000.0000',2,'1000.0000',2,'2011-02-21','2011-02-21',1,1,'-',0),(4,2,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-21','2011-02-21',1,1,'-',1),(5,3,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'-',1),(6,3,NULL,NULL,7,14,'1000.0000',2,'1000.0000',2,'2011-02-18','2011-02-18',1,1,'-',0),(7,4,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2010-02-22','2010-02-22',1,1,'-',1),(8,4,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2010-02-22','2010-02-22',1,1,'-',0),(9,5,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0),(10,5,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1),(11,6,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1),(12,6,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0),(13,7,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1),(14,7,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0),(15,8,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0),(16,8,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1),(17,9,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',0),(18,9,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2010-10-11','2010-10-11',1,1,'-',1),(19,10,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1),(20,10,NULL,NULL,7,16,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(21,11,NULL,NULL,3,7,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(22,11,NULL,NULL,3,50,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',1),(23,12,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1),(24,12,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(25,13,NULL,NULL,3,7,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(26,13,NULL,NULL,3,50,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',1),(27,14,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(28,14,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1),(29,15,NULL,NULL,3,7,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(30,15,NULL,NULL,3,50,'10.0000',2,'10.0000',2,'2011-02-28','2011-02-28',1,1,'-',1),(31,16,NULL,NULL,7,7,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1),(32,16,NULL,NULL,7,20,'100000.0000',2,'100000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(33,17,NULL,NULL,7,20,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',0),(34,17,NULL,NULL,7,7,'1000.0000',2,'1000.0000',2,'2011-02-28','2011-02-28',1,1,'-',1);
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
INSERT INTO fund (fund_id, fund_name, version_no, fundcode_id) VALUES (1,'Non Donor',0,1),(2,'Funding Org A',0,1),(3,'Funding Org B',0,1),(4,'Funding Org C',0,1),(5,'Funding Org D',0,1);
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
INSERT INTO fund_code (fundcode_id, fundcode_value) VALUES (1,'00'),(2,'01'),(3,'02'),(4,'03'),(5,'04');
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
INSERT INTO gl_code (glcode_id, glcode_value) VALUES (1,'10000'),(2,'11000'),(3,'11100'),(4,'11101'),(5,'11102'),(6,'11200'),(7,'11201'),(8,'11202'),(9,'13000'),(10,'13100'),(20,'13101'),(21,'13200'),(22,'13201'),(11,'1501'),(12,'1502'),(13,'1503'),(14,'1504'),(15,'1505'),(16,'1506'),(17,'1507'),(18,'1508'),(19,'1509'),(23,'20000'),(24,'22000'),(25,'22100'),(26,'22101'),(27,'23000'),(28,'23100'),(33,'23101'),(34,'24000'),(35,'24100'),(36,'24101'),(37,'30000'),(38,'31000'),(39,'31100'),(41,'31101'),(42,'31102'),(43,'31300'),(50,'31301'),(51,'31401'),(52,'40000'),(53,'41000'),(54,'41100'),(55,'41101'),(56,'41102'),(29,'4601'),(30,'4602'),(31,'4603'),(32,'4606'),(40,'5001'),(44,'5201'),(45,'5202'),(46,'5203'),(47,'5204'),(48,'5205'),(49,'6201');
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
INSERT INTO grace_period_type (grace_period_type_id, lookup_id) VALUES (1,96),(2,97),(3,98);
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
INSERT INTO group_loan_counter (group_loan_counter_id, group_perf_id, loan_offering_id, loan_cycle_counter) VALUES (1,1,7,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE group_perf_history DISABLE KEYS */;
INSERT INTO group_perf_history (id, customer_id, no_of_clients, last_group_loan_amnt_disb, last_group_loan_amnt_disb_currency_id, avg_loan_size, avg_loan_size_currency_id, total_outstand_loan_amnt, total_outstand_loan_amnt_currency_id, portfolio_at_risk, portfolio_at_risk_currency_id, total_savings_amnt, total_savings_amnt_currency_id) VALUES (1,4,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(2,9,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(3,11,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(4,16,0,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(5,20,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,22,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
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
INSERT INTO interest_calc_rule (interest_calc_rule_id, lookup_id) VALUES (1,88),(2,89);
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
INSERT INTO interest_calculation_types (interest_calculation_type_id, interest_calculation_lookup_id, description) VALUES (1,122,NULL),(2,123,NULL);
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
INSERT INTO interest_types (interest_type_id, lookup_id, category_id, descripton) VALUES (1,79,1,'Flat'),(2,80,1,'Declining'),(4,604,1,'Declining Balance-Equal Principal Installment'),(5,636,1,'InterestTypes-DecliningPrincipalBalance');
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
INSERT INTO language (lang_id, lang_name, lang_short_name, lookup_id) VALUES (1,'English','EN',189),(2,'Icelandic','is',599),(3,'Spanish','es',600),(4,'French','fr',601),(5,'Chinese','zh',613),(6,'Swahili','sw',614),(7,'Arabic','ar',615),(8,'Portuguese','pt',616),(9,'Khmer','km',617),(10,'Lao','lo',618),(11,'Hungarian','hu',624);
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
INSERT INTO loan_account (account_id, business_activities_id, collateral_type_id, grace_period_type_id, group_flag, loan_amount, loan_amount_currency_id, loan_balance, loan_balance_currency_id, interest_type_id, interest_rate, fund_id, meeting_id, currency_id, no_of_installments, disbursement_date, collateral_note, grace_period_duration, interest_at_disb, grace_period_penalty, prd_offering_id, redone, parent_account_id, month_rank, month_week, recur_month) VALUES (10,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'0.0000000000',NULL,15,NULL,10,'2011-02-21',NULL,0,0,0,5,0,NULL,NULL,NULL,NULL),(11,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'0.0000000000',NULL,16,NULL,10,'2011-02-25',NULL,0,0,0,6,0,NULL,NULL,NULL,NULL),(12,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,17,NULL,10,'2010-02-22',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL),(15,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'0.0000000000',NULL,18,NULL,10,'2011-02-18',NULL,0,0,0,5,0,NULL,NULL,NULL,NULL),(20,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,23,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL),(25,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,32,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL),(26,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,33,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL),(27,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,34,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL),(28,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,35,NULL,4,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL),(29,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,5,'24.0000000000',NULL,36,NULL,5,'2010-10-11',NULL,0,0,0,9,0,NULL,NULL,NULL,NULL),(35,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,40,NULL,10,'2020-01-03',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL),(37,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'24.0000000000',NULL,43,NULL,4,'2011-02-28',NULL,0,0,0,11,0,NULL,NULL,NULL,NULL),(38,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,44,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL),(39,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,45,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL),(40,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,48,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL),(41,NULL,NULL,1,NULL,'100000.0000',2,'100000.0000',2,1,'24.0000000000',NULL,49,NULL,10,'2011-02-28',NULL,0,0,0,2,0,NULL,NULL,NULL,NULL),(42,NULL,NULL,1,NULL,'1000.0000',2,'1000.0000',2,1,'24.0000000000',NULL,50,NULL,4,'2011-02-28',NULL,0,0,0,7,0,NULL,NULL,NULL,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_activity_details DISABLE KEYS */;
INSERT INTO loan_activity_details (id, created_by, account_id, created_date, comments, principal_amount, principal_amount_currency_id, interest_amount, interest_amount_currency_id, penalty_amount, penalty_amount_currency_id, fee_amount, fee_amount_currency_id, balance_principal_amount, balance_principal_amount_currency_id, balance_interest_amount, balance_interest_amount_currency_id, balance_penalty_amount, balance_penalty_amount_currency_id, balance_fee_amount, balance_fee_amount_currency_id) VALUES (1,1,10,'2011-02-20 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(2,1,15,'2011-02-17 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(3,1,12,'2010-02-21 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2),(4,1,12,'2010-02-22 05:16:04','Misc fees applied','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2),(5,1,25,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2),(6,1,25,'2010-10-11 07:41:46','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2),(7,1,26,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2),(8,1,26,'2010-10-11 07:43:21','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2),(9,1,27,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2),(10,1,27,'2010-10-11 07:44:40','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2),(11,1,28,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'11.6000',2,'0.0000',2,'0.0000',2),(12,1,28,'2010-10-11 07:46:20','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'400.0000',2,'1000.0000',2,'12.1967',2,'0.0000',2,'400.0000',2),(13,1,29,'2010-10-10 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'999.9000',2,'13.9000',2,'0.0000',2,'0.0000',2),(14,1,29,'2010-10-11 07:47:31','loanWeeklyFee applied','0.0000',2,'0.0000',2,'0.0000',2,'500.0000',2,'1000.0000',2,'14.5096',2,'0.0000',2,'500.0000',2),(15,1,37,'2011-02-27 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'19.0000',2,'0.0000',2,'0.0000',2),(16,1,37,'2011-02-28 07:41:02','oneTimeFee applied','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'1000.0000',2,'19.0000',2,'0.0000',2,'10.0000',2),(17,1,37,'2011-02-28 07:41:09','Misc penalty applied','0.0000',2,'0.0000',2,'5.0000',2,'0.0000',2,'1000.0000',2,'19.0000',2,'5.0000',2,'10.0000',2),(18,1,39,'2011-02-27 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2),(19,1,39,'2011-02-27 18:30:00','Payment rcvd.','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2),(20,1,40,'2011-02-27 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2),(21,1,40,'2011-02-27 18:30:00','Payment rcvd.','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2),(22,1,41,'2011-02-27 18:30:00','Loan Disbursal','100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'10.0000',2),(23,1,41,'2011-02-27 18:30:00','Payment rcvd.','0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'100000.0000',2,'4603.0000',2,'0.0000',2,'0.0000',2),(24,1,42,'2011-02-27 18:30:00','Loan Disbursal','1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2,'19.0000',2,'0.0000',2,'10.0000',2);
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_amount_same_for_all_loan DISABLE KEYS */;
INSERT INTO loan_amount_same_for_all_loan (loan_amount_same_for_all_loan_id, prd_offering_id, min_loan_amount, max_loan_amnt, default_loan_amount) VALUES (2,3,'1000.0000','100000.0000','100000.0000'),(4,4,'1000.0000','100000.0000','100000.0000'),(6,5,'1000.0000','10000.0000','1000.0000'),(7,6,'1000.0000','10000.0000','1000.0000'),(12,2,'1000.0000','100000.0000','100000.0000'),(13,7,'100.0000','1000000.0000','1000.0000'),(14,9,'100.0000','10000000.0000','1000.0000'),(15,10,'1000.0000','10000.0000','10000.0000'),(16,11,'100.0000','10000000.0000','1000.0000'),(17,12,'1000.0000','10000.0000','10000.0000'),(18,13,'1000.0000','10000.0000','10000.0000');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_counter DISABLE KEYS */;
INSERT INTO loan_counter (loan_counter_id, client_perf_id, loan_offering_id, loan_cycle_counter) VALUES (1,3,5,1),(2,7,5,1),(3,4,2,1),(4,12,9,5),(5,14,11,1),(6,14,2,3);
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
INSERT INTO loan_fee_schedule (account_fees_detail_id, id, installment_id, fee_id, account_fee_id, amount, amount_currency_id, amount_paid, amount_paid_currency_id, version_no) VALUES (1,75,1,3,2,'100.0000',2,'0.0000',2,0),(2,76,2,3,2,'100.0000',2,'0.0000',2,0),(3,77,3,3,2,'100.0000',2,'0.0000',2,0),(4,78,4,3,2,'100.0000',2,'0.0000',2,0),(5,83,1,3,3,'100.0000',2,'0.0000',2,0),(6,84,2,3,3,'100.0000',2,'0.0000',2,0),(7,85,3,3,3,'100.0000',2,'0.0000',2,0),(8,86,4,3,3,'100.0000',2,'0.0000',2,0),(9,91,1,3,4,'100.0000',2,'0.0000',2,0),(10,92,2,3,4,'100.0000',2,'0.0000',2,0),(11,93,3,3,4,'100.0000',2,'0.0000',2,0),(12,94,4,3,4,'100.0000',2,'0.0000',2,0),(13,99,1,3,5,'100.0000',2,'0.0000',2,0),(14,100,2,3,5,'100.0000',2,'0.0000',2,0),(15,101,3,3,5,'100.0000',2,'0.0000',2,0),(16,102,4,3,5,'100.0000',2,'0.0000',2,0),(17,108,1,3,6,'100.0000',2,'0.0000',2,0),(18,109,2,3,6,'100.0000',2,'0.0000',2,0),(19,110,3,3,6,'100.0000',2,'0.0000',2,0),(20,111,4,3,6,'100.0000',2,'0.0000',2,0),(21,112,5,3,6,'100.0000',2,'0.0000',2,0),(22,123,1,1,8,'10.0000',2,'0.0000',2,0),(24,171,1,1,13,'10.0000',2,'0.0000',2,0);
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
INSERT INTO loan_offering (prd_offering_id, interest_type_id, interest_calc_rule_id, penalty_id, min_loan_amount, min_loan_amount_currency_id, max_loan_amnt, max_loan_amnt_currency_id, default_loan_amount, default_loan_amount_currency_id, graceperiod_type_id, max_interest_rate, min_interest_rate, def_interest_rate, max_no_installments, min_no_installments, def_no_installments, penalty_grace, loan_counter_flag, int_ded_disbursement_flag, prin_due_last_inst_flag, penalty_rate, grace_period_duration, principal_glcode_id, interest_glcode_id, penalties_glcode_id, interest_waiver_flag, variable_installment_flag, variable_installment_details_id, cash_flow_comparison_flag, cash_flow_detail_id) VALUES (2,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,20,40,NULL,0,0,NULL,0,NULL),(3,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL),(4,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,12,40,NULL,0,0,NULL,0,NULL),(5,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'0.0000000000','0.0000000000','0.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,14,42,NULL,0,0,NULL,0,NULL),(6,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'0.0000000000','0.0000000000','0.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,14,40,NULL,0,0,NULL,0,NULL),(7,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'99.0000000000','1.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,20,40,NULL,0,0,NULL,0,NULL),(9,5,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'30.0000000000','10.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,16,42,NULL,0,0,NULL,0,NULL),(10,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL),(11,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'99.0000000000','1.0000000000','24.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,16,40,NULL,0,0,NULL,0,NULL),(12,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,1,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL),(13,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'24.0000000000','24.0000000000','24.0000000000',NULL,NULL,NULL,NULL,1,0,0,NULL,0,15,40,NULL,0,0,NULL,0,NULL);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_offering_fund DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_perf_history DISABLE KEYS */;
INSERT INTO loan_perf_history (id, account_id, no_of_payments, no_of_missed_payments, days_in_arrears, loan_maturity_date) VALUES (1,10,0,NULL,NULL,'2011-04-29'),(2,11,0,NULL,NULL,NULL),(3,12,0,NULL,NULL,'2010-04-30'),(4,15,0,NULL,NULL,'2011-04-29'),(5,20,0,NULL,NULL,NULL),(6,25,0,NULL,NULL,'2010-11-09'),(7,26,0,NULL,NULL,'2010-11-09'),(8,27,0,NULL,NULL,'2010-11-09'),(9,28,0,NULL,NULL,'2010-11-09'),(10,29,0,NULL,NULL,'2010-11-16'),(11,35,0,NULL,NULL,NULL),(12,37,0,NULL,NULL,'2011-03-28'),(13,38,0,NULL,NULL,NULL),(14,39,0,NULL,NULL,'2011-05-09'),(15,40,0,NULL,NULL,'2011-05-09'),(16,41,0,NULL,NULL,'2011-05-09'),(17,42,0,NULL,NULL,'2011-03-25');
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
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE loan_schedule DISABLE KEYS */;
INSERT INTO loan_schedule (id, account_id, customer_id, currency_id, action_date, principal, principal_currency_id, interest, interest_currency_id, penalty, penalty_currency_id, misc_fees, misc_fees_currency_id, misc_fees_paid, misc_fees_paid_currency_id, misc_penalty, misc_penalty_currency_id, misc_penalty_paid, misc_penalty_paid_currency_id, principal_paid, principal_paid_currency_id, interest_paid, interest_paid_currency_id, penalty_paid, penalty_paid_currency_id, payment_status, installment_id, payment_date, parent_flag, version_no, extra_interest, extra_interest_currency_id, extra_interest_paid, extra_interest_paid_currency_id) VALUES (11,11,4,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2),(12,11,4,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2),(13,11,4,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2),(14,11,4,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2),(15,11,4,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2),(16,11,4,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2),(17,11,4,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2),(18,11,4,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2),(19,11,4,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2),(20,11,4,NULL,'2011-05-06','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2),(31,10,5,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2),(32,10,5,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2),(33,10,5,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2),(34,10,5,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2),(35,10,5,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2),(36,10,5,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2),(37,10,5,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2),(38,10,5,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2),(39,10,5,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2),(40,10,5,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2),(41,15,10,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2),(42,15,10,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2),(43,15,10,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2),(44,15,10,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2),(45,15,10,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2),(46,15,10,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2),(47,15,10,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2),(48,15,10,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2),(49,15,10,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2),(50,15,10,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2),(51,12,6,NULL,'2010-02-26','9999.7000',2,'460.3000',2,'0.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2),(52,12,6,NULL,'2010-03-05','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2),(53,12,6,NULL,'2010-03-12','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2),(54,12,6,NULL,'2010-03-19','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2),(55,12,6,NULL,'2010-03-26','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,2,'0.0000',2,'0.0000',2),(56,12,6,NULL,'2010-04-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,2,'0.0000',2,'0.0000',2),(57,12,6,NULL,'2010-04-09','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,2,'0.0000',2,'0.0000',2),(58,12,6,NULL,'2010-04-16','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,2,'0.0000',2,'0.0000',2),(59,12,6,NULL,'2010-04-23','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,2,'0.0000',2,'0.0000',2),(60,12,6,NULL,'2010-04-30','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,2,'0.0000',2,'0.0000',2),(61,20,3,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2),(62,20,3,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2),(63,20,3,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2),(64,20,3,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2),(65,20,3,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2),(66,20,3,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2),(67,20,3,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2),(68,20,3,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2),(69,20,3,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2),(70,20,3,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2),(75,25,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2),(76,25,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2),(77,25,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2),(78,25,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2),(83,26,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2),(84,26,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2),(85,26,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2),(86,26,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2),(91,27,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2),(92,27,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2),(93,27,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2),(94,27,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2),(99,28,18,NULL,'2010-10-19','247.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2),(100,28,18,NULL,'2010-10-26','249.5000',2,'3.5000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2),(101,28,18,NULL,'2010-11-02','250.7000',2,'2.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2),(102,28,18,NULL,'2010-11-09','252.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2),(108,29,18,NULL,'2010-10-19','197.7000',2,'5.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,2,'0.0000',2,'0.0000',2),(109,29,18,NULL,'2010-10-26','199.3000',2,'3.7000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,2,'0.0000',2,'0.0000',2),(110,29,18,NULL,'2010-11-02','200.2000',2,'2.8000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,2,'0.0000',2,'0.0000',2),(111,29,18,NULL,'2010-11-09','201.1000',2,'1.9000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,2,'0.0000',2,'0.0000',2),(112,29,18,NULL,'2010-11-16','201.7000',2,'1.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,2,'0.0000',2,'0.0000',2),(113,35,23,NULL,'2020-01-10','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2),(114,35,23,NULL,'2020-01-17','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2),(115,35,23,NULL,'2020-01-24','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2),(116,35,23,NULL,'2020-01-31','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2),(117,35,23,NULL,'2020-02-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2),(118,35,23,NULL,'2020-02-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2),(119,35,23,NULL,'2020-02-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2),(120,35,23,NULL,'2020-02-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2),(121,35,23,NULL,'2020-03-06','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2),(122,35,23,NULL,'2020-03-13','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2),(123,37,24,NULL,'2011-03-07','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'5.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,3,'0.0000',2,'0.0000',2),(124,37,24,NULL,'2011-03-14','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,3,'0.0000',2,'0.0000',2),(125,37,24,NULL,'2011-03-21','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,3,'0.0000',2,'0.0000',2),(126,37,24,NULL,'2011-03-28','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,3,'0.0000',2,'0.0000',2),(127,38,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,'0.0000',2,'0.0000',2),(128,38,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,'0.0000',2,'0.0000',2),(129,38,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,'0.0000',2,'0.0000',2),(130,38,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,'0.0000',2,'0.0000',2),(131,38,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,'0.0000',2,'0.0000',2),(132,38,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,'0.0000',2,'0.0000',2),(133,38,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,'0.0000',2,'0.0000',2),(134,38,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,'0.0000',2,'0.0000',2),(135,38,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,'0.0000',2,'0.0000',2),(136,38,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,'0.0000',2,'0.0000',2),(137,39,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2),(138,39,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2),(139,39,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2),(140,39,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2),(141,39,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2),(142,39,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2),(143,39,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2),(144,39,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2),(145,39,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2),(146,39,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2),(147,40,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2),(148,40,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2),(149,40,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2),(150,40,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2),(151,40,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2),(152,40,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2),(153,40,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2),(154,40,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2),(155,40,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2),(156,40,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2),(157,41,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2),(158,41,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2),(159,41,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2),(160,41,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2),(161,41,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,1,'0.0000',2,'0.0000',2),(162,41,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,1,'0.0000',2,'0.0000',2),(163,41,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,1,'0.0000',2,'0.0000',2),(164,41,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,1,'0.0000',2,'0.0000',2),(165,41,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,1,'0.0000',2,'0.0000',2),(166,41,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,1,'0.0000',2,'0.0000',2),(171,42,4,NULL,'2011-03-04','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,1,'0.0000',2,'0.0000',2),(172,42,4,NULL,'2011-03-11','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,1,'0.0000',2,'0.0000',2),(173,42,4,NULL,'2011-03-18','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,1,'0.0000',2,'0.0000',2),(174,42,4,NULL,'2011-03-25','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,1,'0.0000',2,'0.0000',2);
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
INSERT INTO loan_summary (account_id, orig_principal, orig_principal_currency_id, orig_interest, orig_interest_currency_id, orig_fees, orig_fees_currency_id, orig_penalty, orig_penalty_currency_id, principal_paid, principal_paid_currency_id, interest_paid, interest_paid_currency_id, fees_paid, fees_paid_currency_id, penalty_paid, penalty_paid_currency_id, raw_amount_total, raw_amount_total_currency_id) VALUES (10,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(11,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(12,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2),(15,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(20,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2),(25,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2),(26,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2),(27,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2),(28,'1000.0000',2,'12.1967',2,'400.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'411.5000',2),(29,'1000.0000',2,'14.5096',2,'500.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'513.9000',2),(35,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2),(37,'1000.0000',2,'19.0000',2,'10.0000',2,'5.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'28.4000',2),(38,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'4612.7000',2),(39,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'0.0000',2,'4612.7000',2),(40,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'0.0000',2,'4612.7000',2),(41,'100000.0000',2,'4603.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'10.0000',2,'0.0000',2,'4612.7000',2),(42,'1000.0000',2,'19.0000',2,'10.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'28.4000',2);
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
INSERT INTO loan_trxn_detail (account_trxn_id, principal_amount, principal_amount_currency_id, interest_amount, interest_amount_currency_id, penalty_amount, penalty_amount_currency_id, misc_fee_amount, misc_fee_amount_currency_id, misc_penalty_amount, misc_penalty_amount_currency_id) VALUES (2,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(3,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(4,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(5,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(6,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(7,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(8,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(9,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(10,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(11,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(12,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(13,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(14,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(15,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(16,'100000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2),(17,'1000.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2);
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
INSERT INTO lookup_entity (entity_id, entity_name, description) VALUES (1,'ClientStatus','Client Status'),(2,'GroupStatus','Group Status'),(3,'CenterStatus','Center Status'),(4,'OfficeStatus','Office Status'),(5,'AccountState','Account States'),(6,'PersonnelStatusUnused','Personnel Status (Unused)'),(7,'GroupFlag','Group Flag'),(8,'FeeType','Fee Type'),(9,'Titles','Customer Position'),(10,'PovertyStatus','Poverty Status For Client'),(11,'Center','Center VALUES'),(12,'Group','Group VALUES'),(13,'Client','Client VALUES'),(14,'Office','Office'),(15,'Salutation','Mr/Mrs'),(16,'Gender','Male/Female'),(17,'MaritalStatus','Married/UnMarried'),(18,'Citizenship','Citizenship'),(19,'Ethinicity','Ethnicity'),(20,'EducationLevel','EducationLevel'),(21,'BusinessActivities','BusinessActivities'),(22,'Handicapped','Handicaped'),(23,'ClientFormedBy','CustomField ClientFormedBy for client'),(24,'PostalCode','ZipCode'),(25,'ProductState','Product State'),(26,'Loan','Loan'),(27,'Savings','Savings'),(29,'PersonnelTitles','CFO/Accountant'),(30,'PersonnelLevels','LoanOfficer/NonLoanOfficer'),(34,'OfficeLevels','Head Office/Regional Office/Sub Regional Office/Area Office/BranchOffice'),(35,'PrdApplicableMaster','Ceratin product categories applicable to certain types of clients'),(36,'WeekDays','Week Days List'),(37,'InterestTypes','Interest Types for PrdOfferings and Accounts'),(38,'CategoryType','This is mainly used in fees to show the categories where this fee is applicable'),(39,'InterestCalcRule','Interest calculation rule for loan prd offerings'),(41,'GracePeriodTypes','Grace Period Types for loan products'),(42,'DayRank','Day Rank'),(43,'CollateralTypes','Collateral Types for loan accounts'),(44,'OfficeCode','Office Code'),(45,'ProductCategoryStatus','ProductCategoryStatus'),(46,'ProductStatus','ProductStatus'),(47,'SavingsType','SavingsType'),(48,'RecommendedAmtUnit','RecommendedAmtUnit'),(49,'IntCalTypes','IntCalTypes'),(50,'YESNO','YESNO'),(51,'AccountType','AccountType'),(52,'SpouseFather','SpouseFather'),(53,'CustomerStatus','CustomerStatus'),(54,'FeePayment','FeePayment'),(55,'FeeFormulaMaster','FeeFormulaMaster'),(56,'PersonnelStatus','PersonnelStatus'),(57,'Personnel','Personnel'),(62,'ExternalId','External ID'),(68,'FeeStatus','FeeStatus'),(69,'AccountAction','AccountAction'),(70,'AccountFlags','AccountFlags'),(71,'PaymentType','PaymentType'),(72,'SavingsStatus','Saving Status'),(73,'Position','Position'),(74,'Language','Language'),(75,'CustomerAttendanceType','CustomerAttendanceType'),(76,'FinancialAction','Financial Action'),(77,'BulkEntry','BulkEntry'),(78,'SavingsAccountFlag','SavingsAccountFlag'),(79,'Address3','Address3'),(80,'City','City'),(81,'Interest','Interest'),(82,'LoanPurposes','Loan Purposes'),(83,'State','State'),(84,'Address1','Address1'),(85,'Address2','Address2'),(86,'GovernmentId','GovernmentId'),(87,'Permissions','Permissions'),(88,'ServiceCharge','Interest'),(89,'feeUpdationType',' fee updation can to applied to existing accounts or future accounts'),(90,'FeeFrequency','Fee Frequency'),(91,'RepaymentRule','Repayment Rule Types'),(92,'LivingStatus','This entity is used to track whether the family member is living together with the client or not');
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
INSERT INTO lookup_label (label_id, entity_id, locale_id, entity_name) VALUES (1,1,1,NULL),(3,2,1,NULL),(5,3,1,NULL),(7,4,1,NULL),(9,5,1,NULL),(11,6,1,NULL),(13,7,1,NULL),(15,8,1,NULL),(17,9,1,NULL),(19,10,1,NULL),(21,11,1,NULL),(23,12,1,NULL),(25,13,1,NULL),(27,14,1,NULL),(29,15,1,NULL),(31,16,1,NULL),(33,17,1,NULL),(35,18,1,NULL),(37,19,1,NULL),(39,20,1,NULL),(41,21,1,NULL),(43,22,1,NULL),(47,24,1,NULL),(49,25,1,NULL),(51,26,1,NULL),(53,27,1,NULL),(57,29,1,NULL),(59,30,1,NULL),(67,34,1,NULL),(69,35,1,NULL),(71,36,1,NULL),(73,42,1,NULL),(75,37,1,NULL),(76,38,1,NULL),(77,39,1,NULL),(79,41,1,NULL),(80,43,1,NULL),(81,44,1,NULL),(83,45,1,NULL),(85,46,1,NULL),(87,47,1,NULL),(89,48,1,NULL),(91,49,1,NULL),(93,50,1,NULL),(95,51,1,NULL),(97,52,1,NULL),(99,53,1,NULL),(100,54,1,NULL),(102,55,1,NULL),(104,56,1,NULL),(106,57,1,NULL),(116,62,1,NULL),(128,68,1,NULL),(130,69,1,NULL),(132,70,1,NULL),(134,71,1,NULL),(136,72,1,NULL),(151,74,1,NULL),(154,75,1,NULL),(156,76,1,NULL),(158,77,1,NULL),(160,79,1,NULL),(162,80,1,NULL),(164,81,1,NULL),(166,82,1,NULL),(167,83,1,NULL),(168,84,1,NULL),(169,85,1,NULL),(170,86,1,NULL),(171,87,1,NULL),(172,88,1,NULL);
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
INSERT INTO lookup_value (lookup_id, entity_id, lookup_name) VALUES (1,1,'ClientStatus-PartialApplication'),(2,1,'ClientStatus-ApplicationPendingApproval'),(3,1,'ClientStatus-Active'),(4,1,'ClientStatus-OnHold'),(5,1,'ClientStatus-Cancelled'),(6,1,'ClientStatus-Closed'),(7,2,'GroupStatus-PartialApplication'),(8,2,'GroupStatus-ApplicationPendingApproval'),(9,2,'GroupStatus-Active'),(10,2,'GroupStatus-OnHold'),(11,2,'GroupStatus-Cancelled'),(12,2,'GroupStatus-Closed'),(13,3,'CenterStatus-Active'),(14,3,'CenterStatus-Inactive'),(15,4,'OfficeStatus-Active'),(16,4,'OfficeStatus-Inactive'),(17,5,'AccountState-PartialApplication'),(18,5,'AccountState-ApplicationPendingApproval'),(19,5,'AccountState-ApplicationApproved'),(20,5,'AccountState-DisbursedToLo'),(21,5,'AccountState-ActiveInGoodStanding'),(22,5,'AccountState-ClosedObligationMet'),(23,5,'AccountState-ClosedWrittenOff'),(24,5,'AccountState-ClosedRescheduled'),(25,5,'AccountState-ActiveInBadStanding'),(26,6,'PersonnelStatusUnused-Active'),(27,6,'PersonnelStatusUnused-Inactive'),(28,7,'GroupFlag-Withdraw'),(29,7,'GroupFlag-Rejected'),(30,7,'GroupFlag-Blacklisted'),(31,7,'GroupFlag-Duplicate'),(32,7,'GroupFlag-Transferred'),(33,7,'GroupFlag-LeftProgram'),(34,7,'GroupFlag-Other'),(35,8,'FeeType-MaintenanceFee'),(36,8,'FeeType-ConsultancyFee'),(37,8,'FeeType-TrainingFee'),(38,8,'FeeType-MeetingCharges'),(39,9,'Titles-President'),(40,9,'Titles-VicePresident'),(41,10,'PovertyStatus-VeryPoor'),(42,10,'PovertyStatus-Poor'),(43,10,'PovertyStatus-NonPoor'),(47,15,'Salutation-Mr'),(48,15,'Salutation-Mrs'),(49,16,'Gender-Male'),(50,16,'Gender-Female'),(51,25,'ProductState-Active'),(52,25,'ProductState-Inactive'),(53,25,'ProductState-Close'),(54,26,'Loan-Loan'),(55,27,'Savings-Savings'),(57,29,'PersonnelTitles-Cashier'),(58,29,'PersonnelTitles-CenterManager'),(59,29,'PersonnelTitles-BranchManager'),(60,30,'PersonnelLevels-LoanOfficer'),(61,30,'PersonnelLevels-NonLoanOfficer'),(65,34,'DBUpgrade.OfficeLevels.Unsued'),(66,17,'MaritalStatus-Married'),(67,17,'MaritalStatus-Unmarried'),(68,35,'PrdApplicableMaster-Clients'),(69,35,'PrdApplicableMaster-Groups'),(70,35,'PrdApplicableMaster-Centers'),(71,35,'DBUpgrade.PrdApplicableMaster.Unused'),(79,37,'InterestTypes-Flat'),(80,37,'InterestTypes-DecliningBalance'),(81,38,'CategoryType-AllCustomers'),(82,38,'CategoryType-Client'),(83,38,'CategoryType-Group'),(84,38,'CategoryType-Center'),(85,38,'CategoryType-AllProductTypes'),(86,38,'CategoryType-Loans'),(87,38,'CategoryType-Savings'),(88,39,'InterestCalcRule-AlwaysRecalculate'),(89,39,'InterestCalcRule-NeverRecalculate'),(90,39,'DBUpgrade.InterestCalcRule.Unused'),(96,41,'GracePeriodTypes-None'),(97,41,'GracePeriodTypes-GraceOnAllRepayments'),(98,41,'GracePeriodTypes-PrincipalOnlyGrace'),(104,34,'OfficeLevels-HeadOffice'),(105,34,'OfficeLevels-RegionalOffice'),(106,34,'OfficeLevels-DivisionalOffice'),(107,34,'OfficeLevels-AreaOffice'),(108,34,'OfficeLevels-BranchOffice'),(109,43,'CollateralTypes-Type1'),(110,43,'CollateralTypes-Type2'),(111,44,'OfficeCode-Code1'),(112,44,'OfficeCode-Code2'),(113,45,'ProductCategoryStatus-Inactive'),(114,45,'ProductCategoryStatus-Active'),(115,46,'ProductStatus-Active'),(116,46,'ProductStatus-Inactive'),(117,46,'ProductStatus-Closed'),(118,47,'SavingsType-Mandatory'),(119,47,'SavingsType-Voluntary'),(120,48,'RecommendedAmtUnit-PerIndividual'),(121,48,'RecommendedAmtUnit-CompleteGroup'),(122,49,'IntCalTypes-MinimumBalance'),(123,49,'IntCalTypes-AverageBalance'),(124,50,'YESNO-Yes'),(125,50,'YESNO-No'),(126,51,'AccountType-Loan'),(127,51,'AccountType-Saving'),(128,52,'SpouseFather-Spouse'),(129,52,'SpouseFather-Father'),(130,18,'Citizenship-Hindu'),(131,18,'Citizenship-Muslim'),(132,19,'Ethinicity-Sc'),(133,19,'Ethinicity-Bc'),(134,20,'EducationLevel-OnlyClient'),(135,20,'EducationLevel-OnlyHusband'),(136,21,'BusinessActivities-DailyLabour'),(137,21,'BusinessActivities-Agriculture'),(138,22,'Handicapped-Yes'),(139,22,'Handicapped-No'),(140,51,'AccountType-Customer'),(141,5,'AccountState-Cancel'),(142,53,'CustomerStatus-CustomerAccountActive'),(143,53,'CustomerStatus-CustomerAccountInactive'),(144,21,'BusinessActivities-AnimalHusbandry'),(145,21,'BusinessActivities-MicroEnterprise'),(146,54,'FeePayment-Upfront'),(147,54,'FeePayment-TimeOfDisburstment'),(148,54,'FeePayment-TimeOfFirstLoanRepayment'),(149,55,'FeeFormulaMaster-LoanAmount'),(150,55,'FeeFormulaMaster-LoanAmountInterest'),(151,55,'FeeFormulaMaster-Interest'),(152,56,'PersonnelStatus-Active'),(153,56,'PersonnelStatus-Inactive'),(154,57,'Personnel-Personnel'),(165,68,'FeeStatus-Active'),(166,68,'FeeStatus-Inactive'),(167,69,'AccountAction-LoanRepayment'),(168,69,'AccountAction-Penalty'),(169,69,'AccountAction-MiscellenousPenalty'),(170,69,'AccountAction-Fee'),(171,69,'AccountAction-MiscellenousFee'),(172,69,'AccountAction-Deposit'),(173,69,'AccountAction-Withdrawal'),(174,70,'AccountFlags-Withdraw'),(175,70,'AccountFlags-Rejected'),(176,70,'AccountFlags-Other'),(177,71,'PaymentType-Cash'),(179,71,'PaymentType-Voucher'),(180,71,'PaymentType-Cheque'),(181,72,'SavingsStatus-PartialApplication'),(182,72,'SavingsStatus-ApplicationPendingApproval'),(183,72,'SavingsStatus-Cancelled'),(184,72,'SavingsStatus-Active'),(185,72,'SavingsStatus-Closed'),(186,73,'Position-CenterLeader'),(187,73,'Position-CenterSecretary'),(188,73,'Position-GroupLeader'),(189,74,'Language-English'),(191,69,'AccountAction-Payment'),(192,69,'AccountAction-Adjustment'),(193,69,'AccountAction-Disbursal'),(194,75,'CustomerAttendance-P'),(195,75,'CustomerAttendance-Ab'),(196,75,'CustomerAttendance-Al'),(197,75,'CustomerAttendance-L'),(198,76,'FinancialAction-Principal'),(199,76,'FinancialAction-Interest'),(200,76,'FinancialAction-Fees'),(201,76,'FinancialAction-Penalty'),(202,76,'FinancialAction-RoundingAdjustments'),(203,76,'FinancialAction-MandatoryDeposit'),(204,76,'FinancialAction-VoluntoryDeposit'),(205,76,'FinancialAction-MandatoryWithdrawal'),(206,76,'FinancialAction-VoluntoryWithdrawal'),(207,76,'FinancialAction-ReversalAdjustment'),(208,76,'FinancialAction-SavingsInterestPosting'),(209,76,'FinancialAction-Interest_posting'),(210,72,'SavingsStatus-Inactive'),(211,78,'SavingsAccountFlag-Withdraw'),(212,78,'SavingsAccountFlag-Rejected'),(213,78,'SavingsAccountFlag-Blacklisted'),(214,69,'AccountAction-Interest_posting'),(215,76,'FinancialAction-LoanDisbursement'),(216,73,'Position-GroupSecretary'),(217,19,'Ethinicity-St'),(218,19,'Ethinicity-Obc'),(219,19,'Ethinicity-Fc'),(220,17,'MaritalStatus-Widowed'),(221,18,'Citizenship-Christian'),(222,21,'BusinessActivities-Production'),(223,79,'DBUpgrade.Address3.Unused'),(224,80,'DBUpgrade.City.Unused'),(225,21,'BusinessActivities-Trading'),(226,20,'EducationLevel-BothLiterate'),(227,20,'EducationLevel-BothIlliterate'),(228,15,'Salutation-Ms'),(229,76,'FinancialAction-MiscFee'),(230,82,'LoanPurposes-0000AnimalHusbandry'),(231,82,'LoanPurposes-0001CowPurchase'),(232,82,'LoanPurposes-0002BuffaloPurchase'),(233,82,'LoanPurposes-0003GoatPurchase'),(234,82,'LoanPurposes-0004OxBuffalo'),(235,82,'LoanPurposes-0005PigRaising'),(236,82,'LoanPurposes-0006ChickenRaising'),(237,82,'LoanPurposes-0007DonkeyRaising'),(238,82,'LoanPurposes-0008AnimalTrading'),(239,82,'LoanPurposes-0009Horse'),(240,82,'LoanPurposes-0010Camel'),(241,82,'LoanPurposes-0011Bear'),(242,82,'LoanPurposes-0012SheepPurchase'),(243,82,'LoanPurposes-0013HybridCow'),(244,82,'LoanPurposes-0014PhotoFrameWork'),(245,82,'LoanPurposes-0021Fishery'),(246,82,'LoanPurposes-0100Trading'),(247,82,'LoanPurposes-0101PaddyBagBusiness'),(248,82,'LoanPurposes-0102VegetableSelling'),(249,82,'LoanPurposes-0103FruitSelling'),(250,82,'LoanPurposes-0104BanglesTrading'),(251,82,'LoanPurposes-0105TeaShop'),(252,82,'LoanPurposes-0106CosmeticsSelling'),(253,82,'LoanPurposes-0107GeneralStores'),(254,82,'LoanPurposes-0108FlourMill'),(255,82,'LoanPurposes-0109HotelTrading'),(256,82,'LoanPurposes-0110ToddyBusiness'),(257,82,'LoanPurposes-0111PanShop'),(258,82,'LoanPurposes-0112PanleafTrading'),(259,82,'DBUpgrade.LoanPurposes1.Unused'),(260,82,'LoanPurposes-0113MadicalStors'),(261,82,'LoanPurposes-0114MeatSelling'),(262,82,'LoanPurposes-0115OilSelling'),(263,82,'DBUpgrade.LoanPurposes2.Unused'),(264,82,'LoanPurposes-0116ChatShop'),(265,82,'LoanPurposes-0117PaintShop'),(266,82,'LoanPurposes-0118EggShop'),(267,82,'LoanPurposes-0119ShoeMaker'),(268,82,'LoanPurposes-0120PettyShop'),(269,82,'LoanPurposes-0121FlowerBusiness'),(270,82,'LoanPurposes-0122Bakery'),(271,82,'LoanPurposes-0123CoconutBusiness'),(272,82,'LoanPurposes-0124Electricals'),(273,82,'LoanPurposes-0125GroundnutBusiness'),(274,82,'LoanPurposes-0126ScrapBusiness'),(275,82,'LoanPurposes-0127BroomStickBusiness'),(276,82,'LoanPurposes-0128PlasticBusiness'),(277,82,'LoanPurposes-0129PetrolBusiness'),(278,82,'LoanPurposes-0130SteelBusiness'),(279,82,'LoanPurposes-0131BananaLeafBusiness'),(280,82,'LoanPurposes-0132StationaryShop'),(281,82,'LoanPurposes-0200Production'),(282,82,'LoanPurposes-0201BrickMaking'),(283,82,'LoanPurposes-0202MatWeaving'),(284,82,'LoanPurposes-0203ClothSelling'),(285,82,'LoanPurposes-0204SewingMachine'),(286,82,'LoanPurposes-0205WoodSelling'),(287,82,'LoanPurposes-0206BediMaking'),(288,82,'LoanPurposes-0207CarpetWeaving'),(289,82,'LoanPurposes-0208MotorBodyMaking'),(290,82,'LoanPurposes-0209BuildingMaterial'),(291,82,'LoanPurposes-0210ChainPulley'),(292,82,'LoanPurposes-0211ZigZagMachine'),(293,82,'LoanPurposes-0212PapadBusiness'),(294,82,'LoanPurposes-0213TilesBusiness'),(295,82,'LoanPurposes-0214WeldingShop'),(296,82,'LoanPurposes-0215BedBusiness'),(297,82,'LoanPurposes-0216RopeBusiness'),(298,82,'LoanPurposes-0217AgarbattiBusiness'),(299,82,'LoanPurposes-0300Transportation'),(300,82,'LoanPurposes-0301PushCartSagari'),(301,82,'LoanPurposes-0302CycleRickshaw'),(302,82,'LoanPurposes-0303BicycleRepairing'),(303,82,'LoanPurposes-0304AutoRepairing'),(304,82,'LoanPurposes-0305BullockCarts'),(305,82,'LoanPurposes-0306ThresarMachine'),(306,82,'LoanPurposes-0307VideoSet'),(307,82,'LoanPurposes-0308MujackMachine'),(308,82,'LoanPurposes-0309BiskutFery'),(309,82,'LoanPurposes-0310HorseCart'),(310,82,'LoanPurposes-0311AutoPurchase'),(311,82,'LoanPurposes-0400Agriculture'),(312,82,'LoanPurposes-0401Sharecropping'),(313,82,'LoanPurposes-0402TreeLeasing'),(314,82,'LoanPurposes-0403LandReleasing'),(315,82,'LoanPurposes-0404LandLeasing'),(316,82,'LoanPurposes-0405FoodGrainTrading'),(317,82,'LoanPurposes-0406MotorPurchasing'),(318,82,'LoanPurposes-0500Emergency'),(319,82,'LoanPurposes-0501Consumption'),(320,82,'LoanPurposes-0600TraditionalWorks'),(321,82,'LoanPurposes-0601Carpentry'),(322,82,'LoanPurposes-0602StoneCutting'),(323,82,'LoanPurposes-0603Poultry'),(324,82,'LoanPurposes-0604ClothWeaving'),(325,82,'LoanPurposes-0605LeatherSelling'),(326,82,'LoanPurposes-0606BarberShop'),(327,82,'LoanPurposes-0607Blanketweaving'),(328,82,'LoanPurposes-0608WatchShop'),(329,82,'LoanPurposes-0609Blacksmith'),(330,82,'LoanPurposes-0610IronBusiness'),(331,82,'LoanPurposes-0611SoundSystem'),(332,82,'LoanPurposes-0612PotBusiness'),(333,82,'LoanPurposes-0613CookingContract'),(334,82,'LoanPurposes-0614DhobiBusiness'),(335,82,'LoanPurposes-0615StoneBusiness'),(336,82,'LoanPurposes-0616BeautyParlour'),(337,82,'LoanPurposes-0700Marriage'),(338,82,'LoanPurposes-0999CharakhaMachnies'),(339,82,'LoanPurposes-1000Generator'),(340,82,'LoanPurposes-1001BandBaha'),(341,82,'LoanPurposes-1002TentHouse'),(342,82,'LoanPurposes-1003ToiletConstructions'),(343,82,'LoanPurposes-1004HouseConstructions'),(344,82,'LoanPurposes-1005HouseRepairs'),(345,82,'LoanPurposes-1006Education'),(346,82,'LoanPurposes-1007GoldPurchase'),(347,82,'LoanPurposes-1008Hospital'),(348,82,'LoanPurposes-1009Ration'),(349,82,'LoanPurposes-1010Education'),(350,82,'LoanPurposes-1011IgActivity'),(351,82,'LoanPurposes-1012Agriculture'),(352,82,'LoanPurposes-1013AssetsCreations'),(353,82,'LoanPurposes-1014Festivals'),(354,82,'LoanPurposes-1015LoanRepayment'),(355,82,'LoanPurposes-1016CurrentBill'),(356,82,'LoanPurposes-1017Rent'),(357,82,'LoanPurposes-1018Tour'),(358,82,'LoanPurposes-1019FerBusiness'),(359,82,'LoanPurposes-1019FerBusiness2'),(360,82,'LoanPurposes-1020SesionalBusiness'),(361,76,'FinancialAction-MiscPenalty'),(362,69,'AccountAction-CustomerAccountRepayment'),(363,76,'FinancialAction-CustomerAccountFeesPosting'),(364,69,'AccountAction-CustomerAdjustment'),(365,76,'FinancialAction-CustomerAdjustment'),(366,69,'AccountAction-SavingsAdjustment'),(367,76,'FinancialAction-MandatoryDepositAdjustment'),(368,76,'FinancialAction-VoluntoryDepositAdjustment'),(369,76,'FinancialAction-MandatoryWithdrawalAdjustment'),(370,76,'FinancialAction-VoluntoryWithdrawalAdjustment'),(371,87,'Permissions-OrganizationManagement'),(372,87,'Permissions-Funds'),(373,87,'Permissions-CanCreateFunds'),(374,87,'Permissions-CanModifyFunds'),(375,87,'Permissions-Fees'),(376,87,'Permissions-CanDefineNewFeeType'),(377,87,'Permissions-CanModifyFeeInformation'),(378,87,'Permissions-Checklists'),(379,87,'Permissions-CanDefineNewChecklistType'),(380,87,'Permissions-CanModifyChecklistInformation'),(381,87,'Permissions-OfficeManagement'),(382,87,'Permissions-Offices'),(383,87,'Permissions-CanCreateNewOffice'),(384,87,'Permissions-CanModifyOfficeInformation'),(385,87,'Permissions-UserManagement'),(386,87,'Permissions-Personnel'),(387,87,'Permissions-CanCreateNewSystemUsers'),(388,87,'Permissions-CanModifyUserInformation'),(389,87,'Permissions-CanUnlockAUser'),(390,87,'Permissions-Roles'),(391,87,'Permissions-CanCreateNewRole'),(392,87,'Permissions-CanModifyARole'),(393,87,'Permissions-CanDeleteARole'),(394,87,'Permissions-ClientManagement'),(395,87,'Permissions-Clients'),(396,87,'Permissions-Clients-CanCreateNewClientInSaveForLaterState'),(397,87,'Permissions-Clients-CanCreateNewClientInSubmitForApprovalState'),(398,87,'Permissions-Clients-CanChangeStateToPartialApplication'),(399,87,'Permissions-Clients-CanChangeStateToActive'),(400,87,'Permissions-Clients-CanChangeStateToCancelled'),(401,87,'Permissions-Clients-CanChangeStateToOnHold'),(402,87,'Permissions-Clients-CanChangeStateToClosed'),(403,87,'Permissions-Clients-CanChangeStateToApplicationPendingApproval'),(404,87,'Permissions-Clients-CanMakePaymentsToClientAccounts'),(405,87,'Permissions-Clients-CanMakeAdjustmentEntriesToClientAccount'),(407,87,'Permissions-Clients-CanWaiveADueAmount'),(408,87,'Permissions-Clients-CanRemoveFeeTypesFromClientAccount'),(409,87,'Permissions-Clients-CanAddNotesToClient'),(410,87,'Permissions-Clients-CanEditMfiInformation'),(411,87,'Permissions-Clients-CanEditGroupMembership'),(412,87,'Permissions-Clients-CanEditOfficeMembership'),(413,87,'Permissions-Clients-CanEditMeetingSchedule'),(414,87,'Permissions-Clients-CanAddEditHistoricalData'),(415,87,'Permissions-Clients-CanEditFeeAmountAttachedToTheAccount'),(416,87,'Permissions-Clients-CanBlacklistAClient'),(417,87,'Permissions-Groups'),(418,87,'Permissions-Groups-CanCreateNewGroupInSaveForLaterState'),(419,87,'Permissions-Groups-CanCreateNewGroupInSubmitForApprovalState'),(420,87,'Permissions-Groups-CanChangeStateToPartialApplication'),(421,87,'Permissions-Groups-CanChangeStateToActive'),(422,87,'Permissions-Groups-CanChangeStateToCancelled'),(423,87,'Permissions-Groups-CanChangeStateToOnHold'),(424,87,'Permissions-Groups-CanChangeStateToClosed'),(425,87,'Permissions-Groups-CanChangeStateToApplicationPendingApproval'),(426,87,'Permissions-Groups-CanMakePaymentsToGroupAccounts'),(427,87,'Permissions-Groups-CanMakeAdjustmentEntriesToGroupAccount'),(429,87,'Permissions-Groups-CanWaiveADueAmount'),(430,87,'Permissions-Groups-CanRemoveFeeTypesFromGroupAccount'),(431,87,'Permissions-Groups-CanAddNotesToGroup'),(432,87,'Permissions-Groups-CanEditGroupInformation'),(433,87,'Permissions-Groups-CanEditCenterMembership'),(434,87,'Permissions-Groups-CanEditOfficeMembership'),(435,87,'Permissions-Groups-CanEditMeetingSchedule'),(436,87,'Permissions-Groups-CanAddEditHistoricalData'),(437,87,'Permissions-Groups-CanEditFeeAmountAttachedToTheAccount'),(438,87,'Permissions-Groups-CanBlacklistAGroup'),(439,87,'Permissions-Centers'),(440,87,'Permissions-Centers-CanCreateNewCenter'),(441,87,'Permissions-Centers-CanModifyCenterInformation'),(442,87,'Permissions-Centers-CanEditCenterStatus'),(443,87,'Permissions-Centers-CanMakePaymentsToCenterAccounts'),(444,87,'Permissions-Centers-CanMakeAdjustmentEntriesToCenterAccount'),(446,87,'Permissions-Centers-CanWaiveADueAmount'),(447,87,'Permissions-Centers-CanRemoveFeeTypesFromCenterAccount'),(448,87,'Permissions-Centers-CanAddNotesToCenterRecords'),(449,87,'Permissions-Centers-CanEditFeeAmountAttachedToTheAccount'),(450,87,'Permissions-ProductDefinition'),(451,87,'Permissions-ProductCategories'),(452,87,'Permissions-CanDefineNewProductCategories'),(453,87,'Permissions-CanEditProductCategoryInformation'),(454,87,'Permissions-LoanProducts'),(455,87,'Permissions-CanDefineNewLoanProductInstance'),(456,87,'Permissions-CanEditLoanProductInstances'),(457,87,'Permissions-SavingsProducts'),(458,87,'Permissions-CanDefineNewSavingsProductInstance'),(459,87,'Permissions-CanEditSavingsProductInstances'),(460,87,'Permissions-LoanManagement'),(461,87,'Permissions-LoanProcessing'),(462,87,'Permissions-CanCreateNewLoanAccountInSaveForLaterState'),(463,87,'Permissions-CanCreateNewLoanAccountInSubmitForApprovalState'),(464,87,'Permissions-LoanProcessing-CanChangeStateToPartialApplication'),(465,87,'Permissions-LoanProcessing-CanChangeStateToApproved'),(466,87,'Permissions-LoanProcessing-CanChangeStateToCancelled'),(467,87,'Permissions-LoanProcessing-CanChangeStateToDisbursedToLo'),(469,87,'Permissions-LoanProcessing-CanChangeStateToPendingApproval'),(470,87,'Permissions-LoanProcessing-CanChangeStateToClosedWrittenOff'),(471,87,'Permissions-LoanProcessing-CanChangeStateToClosedRescheduled'),(474,87,'Permissions-LoanTransactions'),(475,87,'Permissions-CanMakePaymentToTheAccount'),(476,87,'Permissions-CanMakeAdjustmentEntryToTheAccount'),(478,87,'Permissions-CanWaivePenalty'),(479,87,'Permissions-CanWaiveAFeeInstallment'),(480,87,'Permissions-CanRemoveFeeTypesAttachedToTheAccount'),(481,87,'Permissions-Clients-CanSpecifyMeetingSchedule'),(482,87,'Permissions-Groups-CanSpecifyMeetingSchedule'),(483,87,'Permissions-Clients-CanEditPersonalInformation'),(484,87,'Permissions-Centers-CanEditMeetingSchedule'),(485,87,'Permissions-Centers-CanSpecifyMeetingSchedule'),(486,87,'Permissions-CanEditLoanAccountInformation'),(487,87,'Permissions-CanApplyChargesToLoans'),(488,87,'Permissions-CanEditSelfInformation'),(489,87,'Permissions-SavingsManagement'),(490,87,'Permissions-CanCreateNewSavingsAccountInSaveForLaterState'),(491,87,'Permissions-CanUpdateSavingsAccount'),(492,87,'Permissions-CanCloseSavingsAccount'),(493,87,'Permissions-SavingsManagement-CanChangeStateToPartialApplication'),(494,87,'Permissions-ReportsManagement'),(495,87,'Permissions-CanAdministerReports'),(496,87,'Permissions-CanPreviewReports'),(497,87,'Permissions-CanUploadNewReports'),(498,87,'Permissions-ClientDetail'),(499,87,'Permissions-Center'),(500,87,'Permissions-Status'),(501,87,'Permissions-Performance'),(502,87,'Permissions-LoanProductDetail'),(503,87,'Permissions-Analysis'),(504,87,'Permissions-Miscellaneous'),(531,87,'Permissions-CanRepayLoan'),(532,87,'Permissions-CanAddNotesToLoanAccount'),(533,87,'Permissions-SavingsManagement-CanChangeStateToPendingApproval'),(534,87,'Permissions-SavingsManagement-CanChangeStateToCancel'),(535,87,'Permissions-SavingsManagement-CanChangeStateToActive'),(536,87,'Permissions-SavingsManagement-CanChangeStateToInactive'),(537,87,'Permissions-CanBlacklistSavingsAccount'),(538,87,'Permissions-CanCreateNewSavingsAccountInSubmitForApprovalState'),(539,87,'Permissions-NotImplemented'),(540,29,'PersonnelTitles-AreaManager'),(541,29,'PersonnelTitles-DivisionalManager'),(542,29,'PersonnelTitles-RegionalManager'),(543,29,'PersonnelTitles-Coo'),(544,29,'PersonnelTitles-MisTeam'),(545,29,'PersonnelTitles-ItTeam'),(546,87,'Permissions-CanDoAdjustmentsForSavingsAccount'),(547,69,'AccountAction-LoanWrittenOff'),(548,69,'AccountAction-WaiveOffDue'),(549,69,'AccountAction-WaiveOffOverDue'),(550,76,'FinancialAction-LoanWrittenOff'),(551,87,'Permissions-CanWaiveDueDepositsForSavingsAccount'),(552,87,'Permissions-CanWaiveOverDueDepositsForSavingsAccount'),(553,87,'Permissions-CanDisburseLoan'),(554,87,'Permissions-CanMakeDepositWithdrawalToSavingsAccount'),(555,87,'Permissions-CanAddNotesToSavingsAccount'),(556,89,'feeUpdationType-AppliesToExistingFutureAccounts'),(557,89,'feeUpdationType-AppliesToFutureAccounts'),(558,90,'FeeFrequency-Periodic'),(559,90,'FeeFrequency-OneTime'),(560,87,'Permissions-CanApproveLoansInBulk'),(561,87,'Permissions-CanModifyLatenessDormancyDefinition'),(562,87,'Permissions-CanModifyOfficeHierarchy'),(563,87,'Permissions-CanAddNotesToPersonnel'),(564,87,'Permissions-Bulk'),(565,87,'Permissions-CanEnterCollectionSheetData'),(566,87,'Permissions-Clients-CanApplyChargesToClientAccounts'),(567,87,'Permissions-Groups-CanApplyChargesToGroupAccounts'),(568,87,'Permissions-Centers-CanApplyChargesToCenterAccounts'),(569,87,'Permissions-CanCreateMultipleLoanAccounts'),(570,87,'Permissions-CanReverseLoanDisbursals'),(571,70,'AccountFlags-LoanReversal'),(572,69,'AccountAction-LoanReversal'),(573,69,'AccountAction-DisrbursalAmountReversal'),(574,87,'Permissions-ConfigurationManagement'),(575,87,'Permissions-CanDefineLabels'),(576,91,'RepaymentRule-SameDay'),(577,91,'RepaymentRule-NextMeetingRepayment'),(578,91,'RepaymentRule-NextWorkingDay'),(579,87,'Permissions-CanDefineHiddenMandatoryFields'),(580,87,'Permissions-Clients-CanRemoveClientsFromGroups'),(581,87,'Permissions-CanViewDetailedAgingOfPortfolioAtRisk'),(582,87,'Permissions-Clients-CanAddAnExistingClientToAGroup'),(583,87,'Permissions-ProductMix'),(584,87,'Permissions-CanDefineProductMix'),(585,87,'Permissions-CanEditProductMix'),(586,87,'Permissions-CanViewActiveLoansByLoanOfficer'),(587,87,'Permissions-CanDefineLookupValues'),(588,87,'Permissions-CanUploadReportTemplate'),(589,87,'Permissions-CanViewReports'),(590,87,'Permissions-CanEditReportInformation'),(591,87,'Permissions-CanAdjustPaymentWhenAccountStatusIsClosedObligationMet'),(592,87,'Permissions-CanRedoLoanDisbursals'),(593,87,'Permissions-CanDefineAcceptedPaymentType'),(594,87,'Permissions-CanDefineNewReportCategory'),(595,87,'Permissions-CanViewReportCategory'),(596,87,'Permissions-CanDeleteReportCategory'),(597,87,'Permissions-CanDownloadReportTemplate'),(598,87,'Permissions-CanDefineCustomFields'),(599,74,'Language-Icelandic'),(600,74,'Language-Spanish'),(601,74,'Language-French'),(602,87,'Permissions-CanUploadAdminDocuments'),(603,87,'Permissions-CanViewAdminDocuments'),(604,37,'InterestTypes-DecliningBalance-EqualPrincipalInstallment'),(605,87,'Permissions-SystemInformation'),(606,87,'Permissions-CanViewSystemInformation'),(607,87,'Permissions-CanViewCollectionSheetReport'),(608,87,'Permissions-CanViewOrganizationSettings'),(609,76,'FinancialAction-LoanRescheduled'),(610,69,'AccountAction-LoanRescheduled'),(611,87,'Permissions-CanViewBranchCashConfirmationReport'),(612,87,'Permissions-CanViewBranchProgressReport'),(613,74,'Language-Chinese'),(614,74,'Language-Swahili'),(615,74,'Language-Arabic'),(616,74,'Language-Portuguese'),(617,74,'Language-Khmer'),(618,74,'Language-Lao'),(619,87,'Permissions-CanImportTransactions'),(620,92,'Together'),(621,92,'NotTogether'),(622,52,'Mother'),(623,52,'Child'),(624,74,'Language-Hungarian'),(625,87,'Permissions-CanShutdownMifos'),(626,91,'RepaymentRule-RepaymentMoratorium'),(627,87,'Permissions-CanDefineHoliday'),(628,87,'Permissions.CanViewDetailedAgingOfPortfolioAtRiskReport'),(629,87,'Permissions.CanViewGeneralLedgerReport'),(630,87,'Permissions-CanViewActiveSessions'),(631,87,'Permissions-CanStartMifosShutDown'),(632,87,'Permissions-CanManageQuestionGroups'),(633,87,'Permissions-CanRunBatchJobsOnDemand'),(634,87,'Permissions-CanUpdateBatchJobsConfiguration'),(635,87,'Permissions-CanActivateQuestionGroups'),(636,37,'InterestTypes-DecliningPrincipalBalance'),(637,87,'Permissions-CanAdjustBackDatedTransactions'),(638,87,'Permissions-Clients-CanEditPhoneNumber'),(639,87,'Permissions-CanUseAccountingIntegration');
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
INSERT INTO lookup_value_locale (lookup_value_id, locale_id, lookup_id, lookup_value) VALUES (1,1,1,NULL),(3,1,2,NULL),(5,1,3,NULL),(7,1,4,NULL),(9,1,5,NULL),(11,1,6,NULL),(13,1,7,NULL),(15,1,8,NULL),(17,1,9,NULL),(19,1,10,NULL),(21,1,11,NULL),(23,1,12,NULL),(25,1,13,NULL),(27,1,14,NULL),(29,1,15,NULL),(31,1,16,NULL),(33,1,17,NULL),(35,1,18,NULL),(37,1,19,NULL),(39,1,20,NULL),(41,1,21,NULL),(43,1,22,NULL),(45,1,23,NULL),(47,1,24,NULL),(49,1,25,NULL),(51,1,26,NULL),(53,1,27,NULL),(55,1,28,NULL),(57,1,29,NULL),(59,1,30,NULL),(61,1,31,NULL),(63,1,32,NULL),(65,1,33,NULL),(67,1,34,NULL),(69,1,35,NULL),(71,1,36,NULL),(73,1,37,NULL),(75,1,38,NULL),(77,1,39,'President'),(79,1,40,'Vice President'),(81,1,41,NULL),(83,1,42,NULL),(85,1,43,NULL),(93,1,47,'Mr'),(95,1,48,'Mrs'),(97,1,49,NULL),(99,1,50,NULL),(101,1,51,NULL),(103,1,52,NULL),(105,1,53,NULL),(107,1,54,NULL),(109,1,55,NULL),(113,1,57,'Cashier'),(114,1,58,'Center Manager'),(115,1,59,'Branch Manager'),(119,1,60,NULL),(120,1,61,NULL),(131,1,66,'Married'),(133,1,67,'UnMarried'),(135,1,68,NULL),(136,1,69,NULL),(137,1,70,NULL),(157,1,79,NULL),(158,1,80,NULL),(161,1,81,NULL),(162,1,82,NULL),(163,1,83,NULL),(164,1,84,NULL),(165,1,85,NULL),(166,1,86,NULL),(167,1,87,NULL),(168,1,88,NULL),(169,1,89,NULL),(176,1,96,NULL),(177,1,97,NULL),(178,1,98,NULL),(189,1,104,NULL),(191,1,105,NULL),(193,1,106,NULL),(195,1,107,NULL),(197,1,108,NULL),(199,1,109,NULL),(200,1,110,NULL),(201,1,111,NULL),(203,1,112,NULL),(205,1,113,NULL),(207,1,114,NULL),(209,1,115,NULL),(211,1,116,NULL),(213,1,117,NULL),(215,1,118,NULL),(217,1,119,NULL),(219,1,120,NULL),(221,1,121,NULL),(223,1,122,NULL),(225,1,123,NULL),(227,1,124,NULL),(229,1,125,NULL),(231,1,126,NULL),(233,1,127,NULL),(235,1,128,NULL),(237,1,129,NULL),(239,1,130,'Hindu'),(241,1,131,'Muslim'),(243,1,132,'SC'),(245,1,133,'BC'),(247,1,134,'Only Client'),(249,1,135,'Only Husband'),(251,1,136,NULL),(253,1,137,NULL),(255,1,138,'Yes'),(257,1,139,'No'),(259,1,140,NULL),(261,1,141,NULL),(263,1,142,NULL),(264,1,143,NULL),(265,1,144,NULL),(266,1,145,NULL),(267,1,146,NULL),(269,1,147,NULL),(271,1,148,NULL),(273,1,149,NULL),(275,1,150,NULL),(277,1,151,NULL),(279,1,152,NULL),(281,1,153,NULL),(283,1,154,NULL),(305,1,165,NULL),(307,1,166,NULL),(309,1,167,NULL),(311,1,168,NULL),(313,1,169,NULL),(315,1,170,NULL),(317,1,171,NULL),(319,1,172,NULL),(321,1,173,NULL),(323,1,174,NULL),(325,1,175,NULL),(327,1,176,NULL),(329,1,177,NULL),(337,1,181,NULL),(339,1,182,NULL),(341,1,183,NULL),(343,1,184,NULL),(345,1,185,NULL),(347,1,186,NULL),(349,1,187,NULL),(351,1,188,NULL),(376,1,189,NULL),(380,1,191,NULL),(382,1,192,NULL),(384,1,193,NULL),(386,1,194,NULL),(388,1,195,NULL),(390,1,196,NULL),(392,1,197,NULL),(394,1,198,NULL),(395,1,199,NULL),(396,1,200,NULL),(397,1,201,NULL),(398,1,202,NULL),(399,1,203,NULL),(400,1,204,NULL),(401,1,205,NULL),(402,1,206,NULL),(403,1,207,NULL),(404,1,208,NULL),(405,1,209,NULL),(407,1,210,NULL),(409,1,211,NULL),(411,1,212,NULL),(413,1,213,NULL),(415,1,214,NULL),(417,1,215,NULL),(418,1,216,NULL),(420,1,217,'ST'),(422,1,218,'OBC'),(424,1,219,'FC'),(426,1,220,'Widowed'),(428,1,221,'Christian'),(430,1,222,NULL),(432,1,225,NULL),(434,1,226,'Both Literate'),(436,1,227,'Both Illiterate'),(438,1,228,'Ms'),(440,1,229,NULL),(441,1,230,'0000-Animal Husbandry'),(443,1,231,'0001-Cow Purchase'),(445,1,232,'0002-Buffalo Purchase'),(447,1,233,'0003-Goat Purchase'),(449,1,234,'0004-Ox/Buffalo'),(451,1,235,'0005-Pig Raising'),(453,1,236,'0006-Chicken Raising'),(455,1,237,'0007-Donkey Raising'),(457,1,238,'0008-Animal Trading'),(459,1,239,'0009-Horse'),(461,1,240,'0010-Camel'),(463,1,241,'0011-Bear'),(465,1,242,'0012-Sheep Purchase'),(467,1,243,'0013-Hybrid Cow'),(469,1,244,'0014-Photo Frame Work'),(471,1,245,'0021-Fishery'),(473,1,246,'0100-Trading'),(475,1,247,'0101-Paddy Bag Business'),(477,1,248,'0102-Vegetable Selling'),(479,1,249,'0103-Fruit Selling'),(481,1,250,'0104-Bangles Trading'),(483,1,251,'0105-Tea Shop'),(485,1,252,'0106-Cosmetics Selling'),(487,1,253,'0107-General Stores'),(489,1,254,'0108-Flour Mill'),(491,1,255,'0109-Hotel Trading'),(493,1,256,'0110-Toddy Business'),(495,1,257,'0111-Pan Shop'),(497,1,258,'0112-PanLeaf Trading'),(499,1,260,'0113-Madical Stors'),(501,1,261,'0114-Meat Selling'),(503,1,262,'0115-Oil Selling'),(505,1,264,'0116-Chat Shop'),(507,1,265,'0117-Paint Shop'),(509,1,266,'0118-Egg Shop'),(511,1,267,'0119-Shoe Maker'),(513,1,268,'0120-Petty Shop'),(515,1,269,'0121-Flower Business'),(517,1,270,'0122-Bakery'),(519,1,271,'0123-Coconut Business'),(521,1,272,'0124-Electricals'),(523,1,273,'0125-Groundnut Business'),(525,1,274,'0126-Scrap Business'),(527,1,275,'0127-Broom Stick Business'),(529,1,276,'0128-Plastic Business'),(531,1,277,'0129-Petrol Business'),(533,1,278,'0130-Steel Business'),(535,1,279,'0131-Banana leaf Business'),(537,1,280,'0132-Stationary Shop'),(539,1,281,'0200-Production'),(541,1,282,'0201-Brick Making'),(543,1,283,'0202-Mat Weaving'),(545,1,284,'0203-Cloth Selling'),(547,1,285,'0204-Sewing Machine'),(549,1,286,'0205-Wood Selling'),(551,1,287,'0206-Bedi Making'),(553,1,288,'0207-Carpet Weaving'),(555,1,289,'0208-Motor Body Making'),(557,1,290,'0209-Building Material'),(559,1,291,'0210-Chain Pulley'),(561,1,292,'0211-Zig-zag Machine'),(563,1,293,'0212-Papad Business'),(565,1,294,'0213-Tiles Business'),(567,1,295,'0214-Welding Shop'),(569,1,296,'0215-Bed Business'),(571,1,297,'0216-Rope Business'),(573,1,298,'0217-Agarbatti Business'),(575,1,299,'0300-Transportation'),(577,1,300,'0301-Push Cart/Sagari'),(579,1,301,'0302-Cycle Rickshaw'),(581,1,302,'0303-Bicycle Repairing'),(583,1,303,'0304-Auto Repairing'),(585,1,304,'0305-Bullock Carts'),(587,1,305,'0306-Thresar Machine'),(589,1,306,'0307-Video Set'),(591,1,307,'0308-Mujack Machine'),(593,1,308,'0309-Biskut Fery'),(595,1,309,'0310-Horse Cart'),(597,1,310,'0311-Auto Purchase'),(599,1,311,'0400-Agriculture'),(601,1,312,'0401-Sharecropping'),(603,1,313,'0402-Tree Leasing'),(605,1,314,'0403-Land Releasing'),(607,1,315,'0404-Land Leasing'),(609,1,316,'0405-Food Grain Trading'),(611,1,317,'0406-Motor Purchasing'),(613,1,318,'0500-Emergency'),(615,1,319,'0501-Consumption'),(617,1,320,'0600-Traditional Works'),(619,1,321,'0601-Carpentry'),(621,1,322,'0602-Stone Cutting'),(623,1,323,'0603-Poultry'),(625,1,324,'0604-Cloth Weaving'),(627,1,325,'0605-Leather Selling'),(629,1,326,'0606-Barber Shop'),(631,1,327,'0607-BlanketWeaving'),(633,1,328,'0608-Watch Shop'),(635,1,329,'0609-Blacksmith'),(637,1,330,'0610-Iron Business'),(639,1,331,'0611-Sound System'),(641,1,332,'0612-Pot Business'),(643,1,333,'0613-Cooking Contract'),(645,1,334,'0614-Dhobi Business'),(647,1,335,'0615-Stone Business'),(649,1,336,'0616-Beauty Parlour'),(651,1,337,'0700-Marriage'),(653,1,338,'0999-Charakha Machnies'),(655,1,339,'1000-Generator'),(657,1,340,'1001-Band Baha'),(659,1,341,'1002-Tent House'),(661,1,342,'1003-Toilet Constructions'),(663,1,343,'1004-House Constructions'),(665,1,344,'1005-House Repairs'),(667,1,345,'1006-Education'),(669,1,346,'1007-Gold Purchase'),(671,1,347,'1008-Hospital'),(673,1,348,'1009-Ration'),(675,1,349,'1010-Education'),(677,1,350,'1011-IG Activity'),(679,1,351,'1012-Agriculture'),(681,1,352,'1013-Assets Creations'),(683,1,353,'1014-Festivals'),(685,1,354,'1015-Loan Repayment'),(687,1,355,'1016-Current Bill'),(689,1,356,'1017-Rent'),(691,1,357,'1018-Tour'),(693,1,358,'1019-Fer Business'),(695,1,359,'1019-Fer Business2'),(697,1,360,'1020-Sesional Business'),(699,1,361,NULL),(700,1,362,NULL),(701,1,363,NULL),(702,1,364,NULL),(703,1,365,NULL),(704,1,366,NULL),(705,1,367,NULL),(706,1,368,NULL),(707,1,369,NULL),(708,1,370,NULL),(709,1,371,NULL),(710,1,372,NULL),(711,1,373,NULL),(712,1,374,NULL),(713,1,375,NULL),(714,1,376,NULL),(715,1,377,NULL),(716,1,378,NULL),(717,1,379,NULL),(718,1,380,NULL),(719,1,381,NULL),(720,1,382,NULL),(721,1,383,NULL),(722,1,384,NULL),(723,1,385,NULL),(724,1,386,NULL),(725,1,387,NULL),(726,1,388,NULL),(727,1,389,NULL),(728,1,390,NULL),(729,1,391,NULL),(730,1,392,NULL),(731,1,393,NULL),(732,1,394,NULL),(733,1,395,NULL),(734,1,396,NULL),(735,1,397,NULL),(736,1,398,NULL),(737,1,399,NULL),(738,1,400,NULL),(739,1,401,NULL),(740,1,402,NULL),(741,1,403,NULL),(742,1,404,NULL),(743,1,405,NULL),(745,1,407,NULL),(746,1,408,NULL),(747,1,409,NULL),(748,1,410,NULL),(749,1,411,NULL),(750,1,412,NULL),(751,1,413,NULL),(752,1,414,NULL),(753,1,415,NULL),(754,1,416,NULL),(755,1,417,NULL),(756,1,418,NULL),(757,1,419,NULL),(758,1,420,NULL),(759,1,421,NULL),(760,1,422,NULL),(761,1,423,NULL),(762,1,424,NULL),(763,1,425,NULL),(764,1,426,NULL),(765,1,427,NULL),(767,1,429,NULL),(768,1,430,NULL),(769,1,431,NULL),(770,1,432,NULL),(771,1,433,NULL),(772,1,434,NULL),(773,1,435,NULL),(774,1,436,NULL),(775,1,437,NULL),(776,1,438,NULL),(777,1,439,NULL),(778,1,440,NULL),(779,1,441,NULL),(780,1,442,NULL),(781,1,443,NULL),(782,1,444,NULL),(784,1,446,NULL),(785,1,447,NULL),(786,1,448,NULL),(787,1,449,NULL),(788,1,450,NULL),(789,1,451,NULL),(790,1,452,NULL),(791,1,453,NULL),(792,1,454,NULL),(793,1,455,NULL),(794,1,456,NULL),(795,1,457,NULL),(796,1,458,NULL),(797,1,459,NULL),(798,1,460,NULL),(799,1,461,NULL),(800,1,462,NULL),(801,1,463,NULL),(802,1,464,NULL),(803,1,465,NULL),(804,1,466,NULL),(805,1,467,NULL),(807,1,469,NULL),(808,1,470,NULL),(809,1,471,NULL),(812,1,474,NULL),(813,1,475,NULL),(814,1,476,NULL),(816,1,478,NULL),(817,1,479,NULL),(818,1,480,NULL),(819,1,481,NULL),(820,1,482,NULL),(821,1,483,NULL),(822,1,484,NULL),(823,1,485,NULL),(824,1,486,NULL),(825,1,487,NULL),(826,1,488,NULL),(827,1,489,NULL),(828,1,490,NULL),(829,1,491,NULL),(830,1,492,NULL),(831,1,493,NULL),(832,1,494,NULL),(833,1,495,NULL),(834,1,496,NULL),(835,1,497,NULL),(836,1,498,NULL),(837,1,499,NULL),(838,1,500,NULL),(839,1,501,NULL),(840,1,502,NULL),(841,1,503,NULL),(842,1,504,NULL),(869,1,531,NULL),(870,1,532,NULL),(871,1,533,NULL),(872,1,534,NULL),(873,1,535,NULL),(874,1,536,NULL),(875,1,537,NULL),(876,1,538,NULL),(877,1,539,NULL),(878,1,540,'Area Manager'),(879,1,541,'Divisional Manager'),(880,1,542,'Regional Manager'),(881,1,543,'COO'),(882,1,544,'MIS Team'),(883,1,545,'IT Team'),(884,1,546,NULL),(885,1,547,NULL),(886,1,548,NULL),(887,1,549,NULL),(888,1,550,NULL),(889,1,551,NULL),(890,1,552,NULL),(891,1,553,NULL),(892,1,554,NULL),(893,1,555,NULL),(894,1,556,NULL),(895,1,557,NULL),(896,1,558,NULL),(897,1,559,NULL),(898,1,560,NULL),(899,1,561,NULL),(900,1,562,NULL),(901,1,563,NULL),(902,1,564,NULL),(903,1,565,NULL),(904,1,566,NULL),(905,1,567,NULL),(906,1,568,NULL),(912,1,179,NULL),(913,1,180,NULL),(914,1,569,NULL),(915,1,570,NULL),(916,1,571,NULL),(917,1,572,NULL),(918,1,573,NULL),(919,1,574,NULL),(920,1,575,NULL),(921,1,576,NULL),(922,1,577,NULL),(923,1,578,NULL),(924,1,579,NULL),(925,1,580,NULL),(926,1,581,NULL),(927,1,582,NULL),(928,1,583,NULL),(929,1,584,NULL),(930,1,585,NULL),(931,1,586,NULL),(932,1,587,NULL),(933,1,588,NULL),(934,1,589,NULL),(935,1,590,NULL),(936,1,591,NULL),(937,1,592,NULL),(938,1,593,NULL),(939,1,594,NULL),(940,1,595,NULL),(941,1,596,NULL),(942,1,597,NULL),(943,1,598,NULL),(944,1,602,NULL),(945,1,603,NULL),(946,1,604,NULL),(947,1,605,NULL),(948,1,606,NULL),(949,1,607,NULL),(950,1,608,NULL),(951,1,609,NULL),(952,1,610,NULL),(953,1,611,NULL),(954,1,612,NULL),(955,1,619,NULL),(956,1,625,NULL),(957,1,627,NULL),(958,1,628,'Can View Detailed Aging Of Portfolio At Risk Report'),(959,1,629,'Can View General Ledger Report'),(960,1,630,NULL),(961,1,631,NULL),(962,1,632,NULL),(963,1,633,NULL),(964,1,634,NULL),(965,1,635,NULL),(966,1,637,NULL),(967,1,638,NULL),(968,1,639,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE max_min_interest_rate DISABLE KEYS */;
INSERT INTO max_min_interest_rate (account_id, min_interest_rate, max_interest_rate) VALUES (10,'0.0000','0.0000'),(11,'0.0000','0.0000'),(12,'24.0000','24.0000'),(15,'0.0000','0.0000'),(20,'24.0000','24.0000'),(25,'10.0000','30.0000'),(26,'10.0000','30.0000'),(27,'10.0000','30.0000'),(28,'10.0000','30.0000'),(29,'10.0000','30.0000'),(35,'24.0000','24.0000'),(37,'1.0000','99.0000'),(38,'24.0000','24.0000'),(39,'24.0000','24.0000'),(40,'24.0000','24.0000'),(41,'24.0000','24.0000'),(42,'1.0000','99.0000');
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
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE max_min_loan_amount DISABLE KEYS */;
INSERT INTO max_min_loan_amount (account_id, min_loan_amount, max_loan_amount) VALUES (10,'1000.0000','10000.0000'),(11,'1000.0000','10000.0000'),(12,'1000.0000','100000.0000'),(15,'1000.0000','10000.0000'),(20,'1000.0000','100000.0000'),(25,'100.0000','10000000.0000'),(26,'100.0000','10000000.0000'),(27,'100.0000','10000000.0000'),(28,'100.0000','10000000.0000'),(29,'100.0000','10000000.0000'),(35,'1000.0000','100000.0000'),(37,'100.0000','10000000.0000'),(38,'1000.0000','100000.0000'),(39,'1000.0000','100000.0000'),(40,'1000.0000','100000.0000'),(41,'1000.0000','100000.0000'),(42,'100.0000','1000000.0000');
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
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE max_min_no_of_install DISABLE KEYS */;
INSERT INTO max_min_no_of_install (account_id, min_no_install, max_no_install) VALUES (10,'1.0000','10.0000'),(11,'1.0000','10.0000'),(12,'1.0000','10.0000'),(15,'1.0000','10.0000'),(20,'1.0000','10.0000'),(25,'1.0000','52.0000'),(26,'1.0000','52.0000'),(27,'1.0000','52.0000'),(28,'1.0000','52.0000'),(29,'1.0000','52.0000'),(35,'1.0000','10.0000'),(37,'1.0000','100.0000'),(38,'1.0000','10.0000'),(39,'1.0000','10.0000'),(40,'1.0000','10.0000'),(41,'1.0000','10.0000'),(42,'1.0000','100.0000');
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE meeting DISABLE KEYS */;
INSERT INTO meeting (meeting_id, meeting_type_id, meeting_place, start_date, end_date, start_time, end_time, version_no) VALUES (1,4,'test','2011-02-18',NULL,NULL,NULL,9),(2,3,'meetingPlace','2011-02-18',NULL,NULL,NULL,0),(3,2,'meetingPlace','2011-02-18',NULL,NULL,NULL,0),(4,4,'test','2011-02-21',NULL,NULL,NULL,1),(5,4,'test','2011-02-21',NULL,NULL,NULL,1),(6,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0),(7,4,'test','2010-01-22',NULL,NULL,NULL,1),(8,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0),(9,4,'test','2010-01-22',NULL,NULL,NULL,1),(10,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0),(11,1,'meetingPlace','2010-01-22',NULL,NULL,NULL,0),(12,4,'test','2010-01-22',NULL,NULL,NULL,1),(13,1,'meetingPlace','2011-02-21',NULL,NULL,NULL,0),(14,1,'meetingPlace','2011-02-21',NULL,NULL,NULL,0),(15,4,'test','2011-02-21',NULL,NULL,NULL,1),(16,4,'test','2011-02-25',NULL,NULL,NULL,0),(17,4,'test','2010-02-22',NULL,NULL,NULL,1),(18,4,'test','2011-02-18',NULL,NULL,NULL,0),(19,1,'meetingPlace','2011-02-22',NULL,NULL,NULL,0),(20,4,'test','2011-02-22',NULL,NULL,NULL,1),(21,4,'test','2011-02-22',NULL,NULL,NULL,1),(22,4,'test','2011-02-22',NULL,NULL,NULL,1),(23,4,'test','2011-02-28',NULL,NULL,NULL,0),(24,5,'meetingPlace','2011-03-04',NULL,NULL,NULL,0),(25,4,'weeklyMeetingPlace','2011-02-24',NULL,NULL,NULL,1),(28,3,'meetingPlace','2011-02-25',NULL,NULL,NULL,0),(29,2,'meetingPlace','2011-02-25',NULL,NULL,NULL,0),(30,4,'test','2010-10-11',NULL,NULL,NULL,1),(31,1,'meetingPlace','2010-10-11',NULL,NULL,NULL,0),(32,1,'test','2010-10-13',NULL,NULL,NULL,1),(33,1,'test','2010-10-13',NULL,NULL,NULL,1),(34,1,'test','2010-10-13',NULL,NULL,NULL,1),(35,1,'test','2010-10-13',NULL,NULL,NULL,1),(36,1,'test','2010-10-13',NULL,NULL,NULL,1),(37,4,'MonthlyMeetingLocation','2011-02-25',NULL,NULL,NULL,2),(38,4,'MonthlyMeetingLocation','2011-02-25',NULL,NULL,NULL,2),(39,1,'meetingPlace','2011-02-25',NULL,NULL,NULL,0),(40,4,'test','2020-01-03',NULL,NULL,NULL,0),(41,1,'meetingPlace','2011-02-28',NULL,NULL,NULL,0),(42,4,'test','2011-02-28',NULL,NULL,NULL,1),(43,4,'test','2011-02-28',NULL,NULL,NULL,0),(44,4,'test','2011-02-28',NULL,NULL,NULL,0),(45,4,'test','2011-02-28',NULL,NULL,NULL,0),(46,1,'meetingPlace','2011-02-22',NULL,NULL,NULL,0),(47,1,'meetingPlace','2011-02-22',NULL,NULL,NULL,0),(48,4,'test','2011-02-28',NULL,NULL,NULL,0),(49,4,'test','2011-02-28',NULL,NULL,NULL,0),(50,4,'test','2011-02-28',NULL,NULL,NULL,1);
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
INSERT INTO meeting_type (meeting_type_id, meeting_purpose, description) VALUES (1,'LOANFREQUENCYOFINSTALLMENTS','Loan Frequency of istalments'),(2,'SAVINGSTIMEPERFORINTCALC','Savings Time Period for Interest Calculation'),(3,'SAVINGSFRQINTPOSTACC','Savings Frequency of Interest Posting to Accounts'),(4,'CUSTOMERMEETING','Customer Meeting'),(5,'FEEMEETING','Fees Meetings');
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
INSERT INTO mfi_attribute (attribute_id, office_id, attribute_name, attribute_value) VALUES (1,1,'CENTER','GROUP'),(2,1,'CENTER','GROUP'),(3,1,'CENTER','GROUP'),(4,1,'CENTER','GROUP'),(5,1,'CENTER','GROUP');
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE no_of_install_same_for_all_loan DISABLE KEYS */;
INSERT INTO no_of_install_same_for_all_loan (no_of_install_same_for_all_loan_id, prd_offering_id, min_no_install, max_no_install, default_no_install) VALUES (2,3,'1.0000','10.0000','10.0000'),(4,4,'1.0000','10.0000','10.0000'),(6,5,'1.0000','10.0000','10.0000'),(7,6,'1.0000','10.0000','10.0000'),(12,2,'1.0000','10.0000','10.0000'),(13,7,'1.0000','100.0000','4.0000'),(14,9,'1.0000','52.0000','4.0000'),(15,10,'1.0000','10.0000','10.0000'),(16,11,'1.0000','100.0000','4.0000'),(17,12,'1.0000','10.0000','10.0000'),(18,13,'1.0000','10.0000','10.0000');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office DISABLE KEYS */;
INSERT INTO office (office_id, global_office_num, office_level_id, search_id, max_child_count, local_remote_flag, display_name, created_by, created_date, updated_by, updated_date, office_short_name, parent_office_id, status_id, version_no, office_code_id) VALUES (1,'0001',1,'1.1.',2,1,'Mifos HO ',NULL,NULL,NULL,NULL,'MIF1',NULL,1,1,NULL),(2,'0002',5,'1.1.1.',0,1,'MyOfficeDHMFT',1,'2011-02-18',NULL,NULL,'DHM',1,1,0,NULL);
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
INSERT INTO office_action_payment_type (office_id, prd_type_id, account_action_id, payment_type_id) VALUES (NULL,NULL,1,NULL),(NULL,NULL,2,NULL),(NULL,NULL,3,NULL),(NULL,NULL,4,NULL),(NULL,NULL,5,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE office_address DISABLE KEYS */;
INSERT INTO office_address (office_address_id, office_id, address_1, address_2, address_3, city, state, country, zip, telephone) VALUES (1,2,'','','','','','','','');
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
INSERT INTO office_code (code_id, lookup_id) VALUES (1,111),(2,112);
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
INSERT INTO office_level (level_id, parent_level_id, level_name_id, interaction_flag, configured, version_no) VALUES (1,NULL,104,0,1,NULL),(2,1,105,0,1,NULL),(3,2,106,0,1,NULL),(4,3,107,0,1,NULL),(5,4,108,1,1,NULL);
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
INSERT INTO office_status (status_id, lookup_id) VALUES (1,15),(2,16);
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
INSERT INTO original_loan_fee_schedule (account_fees_detail_id, id, installment_id, fee_id, account_fee_id, amount, amount_currency_id, amount_paid, amount_paid_currency_id, version_no) VALUES (1,86,1,1,13,'10.0000',2,'0.0000',2,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE original_loan_schedule DISABLE KEYS */;
INSERT INTO original_loan_schedule (id, account_id, customer_id, currency_id, action_date, principal, principal_currency_id, interest, interest_currency_id, penalty, penalty_currency_id, misc_fees, misc_fees_currency_id, misc_fees_paid, misc_fees_paid_currency_id, misc_penalty, misc_penalty_currency_id, misc_penalty_paid, misc_penalty_paid_currency_id, principal_paid, principal_paid_currency_id, interest_paid, interest_paid_currency_id, penalty_paid, penalty_paid_currency_id, payment_status, installment_id, payment_date, parent_flag, version_no, extra_interest, extra_interest_currency_id, extra_interest_paid, extra_interest_paid_currency_id) VALUES (1,10,5,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(2,10,5,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(3,10,5,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(4,10,5,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(5,10,5,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL),(6,10,5,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL),(7,10,5,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL),(8,10,5,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL),(9,10,5,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL),(10,10,5,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL),(11,15,10,NULL,'2011-02-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(12,15,10,NULL,'2011-03-04','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(13,15,10,NULL,'2011-03-11','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(14,15,10,NULL,'2011-03-18','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(15,15,10,NULL,'2011-03-25','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL),(16,15,10,NULL,'2011-04-01','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL),(17,15,10,NULL,'2011-04-08','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL),(18,15,10,NULL,'2011-04-15','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL),(19,15,10,NULL,'2011-04-22','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL),(20,15,10,NULL,'2011-04-29','100.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL),(21,12,6,NULL,'2010-02-26','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(22,12,6,NULL,'2010-03-05','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(23,12,6,NULL,'2010-03-12','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(24,12,6,NULL,'2010-03-19','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(25,12,6,NULL,'2010-03-26','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL),(26,12,6,NULL,'2010-04-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL),(27,12,6,NULL,'2010-04-09','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL),(28,12,6,NULL,'2010-04-16','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL),(29,12,6,NULL,'2010-04-23','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL),(30,12,6,NULL,'2010-04-30','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL),(31,25,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(32,25,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(33,25,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(34,25,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(35,26,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(36,26,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(37,26,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(38,26,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(39,27,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(40,27,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(41,27,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(42,27,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(43,28,18,NULL,'2010-10-19','247.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(44,28,18,NULL,'2010-10-26','249.5375',2,'3.4625',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(45,28,18,NULL,'2010-11-02','250.6861',2,'2.3139',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(46,28,18,NULL,'2010-11-09','252.0366',2,'1.1601',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(47,29,18,NULL,'2010-10-19','197.7397',2,'5.2603',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(48,29,18,NULL,'2010-10-26','199.3074',2,'3.6926',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(49,29,18,NULL,'2010-11-02','200.2248',2,'2.7752',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(50,29,18,NULL,'2010-11-09','201.1463',2,'1.8537',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(51,29,18,NULL,'2010-11-16','201.5818',2,'0.9278',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL),(52,37,24,NULL,'2011-03-07','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(53,37,24,NULL,'2011-03-14','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(54,37,24,NULL,'2011-03-21','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(55,37,24,NULL,'2011-03-28','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(56,39,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(57,39,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(58,39,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(59,39,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(60,39,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL),(61,39,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL),(62,39,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL),(63,39,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL),(64,39,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL),(65,39,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL),(66,40,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(67,40,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(68,40,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(69,40,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(70,40,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL),(71,40,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL),(72,40,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL),(73,40,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL),(74,40,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL),(75,40,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL),(76,41,24,NULL,'2011-03-07','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(77,41,24,NULL,'2011-03-14','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(78,41,24,NULL,'2011-03-21','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(79,41,24,NULL,'2011-03-28','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL),(80,41,24,NULL,'2011-04-04','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,5,NULL,NULL,0,NULL,NULL,NULL,NULL),(81,41,24,NULL,'2011-04-11','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,6,NULL,NULL,0,NULL,NULL,NULL,NULL),(82,41,24,NULL,'2011-04-18','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,7,NULL,NULL,0,NULL,NULL,NULL,NULL),(83,41,24,NULL,'2011-04-25','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,8,NULL,NULL,0,NULL,NULL,NULL,NULL),(84,41,24,NULL,'2011-05-02','9999.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,9,NULL,NULL,0,NULL,NULL,NULL,NULL),(85,41,24,NULL,'2011-05-09','10002.7000',2,'460.3000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,10,NULL,NULL,0,NULL,NULL,NULL,NULL),(86,42,4,NULL,'2011-03-04','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,1,NULL,NULL,0,NULL,NULL,NULL,NULL),(87,42,4,NULL,'2011-03-11','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,2,NULL,NULL,0,NULL,NULL,NULL,NULL),(88,42,4,NULL,'2011-03-18','250.4000',2,'4.6000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,3,NULL,NULL,0,NULL,NULL,NULL,NULL),(89,42,4,NULL,'2011-03-25','248.8000',2,'5.2000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,'0.0000',2,0,4,NULL,NULL,0,NULL,NULL,NULL,NULL);
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
INSERT INTO payment_type (payment_type_id, payment_type_lookup_id) VALUES (1,177),(2,179),(3,180);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE personnel DISABLE KEYS */;
INSERT INTO personnel (personnel_id, level_id, global_personnel_num, office_id, title, personnel_status, preferred_locale, search_id, max_child_count, password, login_name, email_id, password_changed, display_name, created_by, created_date, updated_by, updated_date, last_login, locked, no_of_tries, version_no) VALUES (1,2,'1',1,1,1,1,NULL,1,'\"d��#0�a�M�2�Nj����d�QS8�','mifos',NULL,1,'mifos',1,NULL,1,'2011-02-28','2011-02-28',0,0,12),(2,1,'0002-00002',2,NULL,1,1,NULL,NULL,'ï¿œ;}ï¿œï¿œï¿œÇï¿œ1d&ï¿œDï¿œï¿œï¿œï¿œ1sIï¿œ@}ï¿œ','loanofficer','',0,'loan officer',1,'2011-02-18',NULL,NULL,NULL,0,0,1);
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
INSERT INTO personnel_details (personnel_id, first_name, middle_name, second_last_name, last_name, government_id_number, dob, marital_status, gender, date_of_joining_mfi, date_of_joining_branch, date_of_leaving_branch, address_1, address_2, address_3, city, state, country, postal_code, telephone) VALUES (1,'Mifos',NULL,NULL,'MFI','123','1979-12-12',NULL,50,NULL,NULL,NULL,'Bangalore',NULL,NULL,'Bangalore','Bangalore','Bangalore',NULL,NULL),(2,'loan','','','officer','','1990-01-01',NULL,49,'2005-02-18','2011-02-18',NULL,'','','','','','','','');
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
INSERT INTO personnel_level (level_id, level_name_id, parent_level_id, interaction_flag) VALUES (1,60,1,0),(2,61,1,0);
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
INSERT INTO personnel_role (personnel_role_id, role_id, personnel_id) VALUES (1,1,1);
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
INSERT INTO personnel_status (personnel_status_id, lookup_id) VALUES (1,152),(2,153);
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
INSERT INTO position (position_id, lookup_id) VALUES (1,186),(2,187),(3,188),(4,216);
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
INSERT INTO prd_applicable_master (prd_applicable_master_id, lookup_id) VALUES (1,68),(2,69),(3,70);
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
INSERT INTO prd_category (prd_category_id, prd_type_id, global_prd_offering_num, prd_category_name, created_date, created_by, office_id, updated_by, udpated_date, state, description, version_no) VALUES (1,1,'1-1','Other',NULL,NULL,NULL,NULL,NULL,1,NULL,1),(2,2,'1-2','Other',NULL,NULL,NULL,NULL,NULL,1,NULL,1);
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
INSERT INTO prd_category_status (prd_category_status_id, lookup_id) VALUES (2,113),(1,114);
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering DISABLE KEYS */;
INSERT INTO prd_offering (prd_offering_id, prd_applicable_master_id, global_prd_offering_num, prd_category_id, prd_type_id, office_id, start_date, end_date, glcode_id, prd_offering_name, prd_offering_short_name, offering_status_id, description, created_date, created_by, updated_date, updated_by, version_no, prd_mix_flag, currency_id) VALUES (1,1,'1-001',2,2,NULL,'2009-01-01',NULL,NULL,'MonthlyClientSavingsAccount','s1',2,'','2009-01-01',1,NULL,NULL,0,NULL,2),(2,1,'1-002',1,1,1,'2010-01-22',NULL,NULL,'WeeklyFlatLoanWithOneTimeFees','flwf',1,'','2010-01-22',1,'2011-02-22',1,1,NULL,2),(3,1,'1-003',1,1,1,'2010-01-22',NULL,NULL,'MonthlyClientFlatLoan1stOfMonth','mcf1',1,'','2010-01-22',1,NULL,NULL,0,NULL,2),(4,1,'1-004',1,1,1,'2010-01-22',NULL,NULL,'MonthlyClientFlatLoanThirdFridayOfMonth','mcf3',1,'','2010-01-22',1,'2010-01-22',1,1,NULL,2),(5,1,'1-005',1,1,1,'2011-02-21',NULL,NULL,'ClientEmergencyLoan','EL',1,'','2011-02-21',1,'2011-02-21',1,1,NULL,2),(6,2,'1-006',1,1,1,'2011-02-21',NULL,NULL,'GroupEmergencyLoan','GEL',1,'','2011-02-21',1,NULL,NULL,0,NULL,2),(7,2,'1-007',1,1,1,'2011-02-22',NULL,NULL,'WeeklyGroupFlatLoanWithOnetimeFee','wgff',1,'','2011-02-22',1,'2011-02-22',1,3,NULL,2),(8,1,'1-008',2,2,NULL,'2011-02-25',NULL,NULL,'MandatorySavingsAccount','MSA',2,'','2011-02-25',1,'2011-02-25',1,1,NULL,2),(9,1,'1-009',1,1,1,'2010-10-11',NULL,NULL,'WeeklyPawdepLoan','wpl',1,'','2010-10-11',1,NULL,NULL,0,NULL,2),(10,2,'1-010',1,1,1,'2011-02-25',NULL,NULL,'MonthlyGroupFlatLoan1stOfMonth','mgf1',1,'','2011-02-25',1,NULL,NULL,0,NULL,2),(11,1,'1-011',1,1,1,'2011-02-28',NULL,NULL,'InterestWaiverLoan','iwl',1,'','2011-02-28',1,NULL,NULL,0,NULL,2),(12,1,'1-012',1,1,1,'2011-02-22',NULL,NULL,'WeeklyClientFlatLoanWithNoFee','wcl1',1,'','2011-02-22',1,NULL,NULL,0,NULL,2),(13,1,'1-013',1,1,1,'2011-02-22',NULL,NULL,'AnotherWeeklyClientFlatLoanWithNoFee','wcl2',1,'','2011-02-22',1,NULL,NULL,0,NULL,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering_fees DISABLE KEYS */;
INSERT INTO prd_offering_fees (prd_offering_fee_id, fee_id, prd_offering_id) VALUES (4,2,2),(5,1,7);
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE prd_offering_meeting DISABLE KEYS */;
INSERT INTO prd_offering_meeting (prd_offering_meeting_id, prd_offering_id, prd_meeting_id, prd_offering_meeting_type_id) VALUES (1,1,2,3),(2,1,3,2),(3,2,6,1),(4,3,8,1),(5,4,11,1),(6,5,13,1),(7,6,14,1),(8,7,19,1),(11,8,28,3),(12,8,29,2),(13,9,31,1),(14,10,39,1),(15,11,41,1),(16,12,46,1),(17,13,47,1);
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
INSERT INTO prd_state (prd_state_id, prd_state_lookup_id) VALUES (1,115),(2,116);
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
INSERT INTO prd_status (offering_status_id, prd_state_id, prd_type_id, currently_in_use, version_no) VALUES (1,1,1,1,1),(2,1,2,1,1),(4,2,1,1,1),(5,2,2,1,1);
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
INSERT INTO prd_type (prd_type_id, prd_type_lookup_id, lateness_days, dormancy_days, version_no) VALUES (1,54,10,1,1),(2,55,12,30,1);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_choices DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_group DISABLE KEYS */;
INSERT INTO question_group (id, title, date_of_creation, state, is_editable, is_ppi) VALUES (1,'QGForCreateSavingsAccount','2011-02-24',0,0,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_group_event_sources DISABLE KEYS */;
INSERT INTO question_group_event_sources (id, question_group_id, event_source_id) VALUES (1,1,12);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE question_group_instance DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE questions DISABLE KEYS */;
INSERT INTO questions (question_id, answer_type, question_state, question_text, numeric_min, numeric_max, nickname) VALUES (1,2,1,'Question1',NULL,NULL,'e5c1d1ea399a2e46aca161ed737cfee3');
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
INSERT INTO recommended_amnt_unit (recommended_amnt_unit_id, lookup_id) VALUES (1,120),(2,121);
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE recur_on_day DISABLE KEYS */;
INSERT INTO recur_on_day (recur_on_day_id, details_id, days, rank_of_days, day_number, version_no) VALUES (1,1,6,NULL,NULL,9),(2,2,NULL,NULL,1,0),(3,3,NULL,NULL,NULL,0),(4,4,NULL,NULL,1,1),(5,5,2,NULL,NULL,1),(6,6,2,NULL,NULL,0),(7,7,6,NULL,NULL,1),(8,8,NULL,NULL,1,0),(9,9,NULL,NULL,1,1),(10,10,2,NULL,NULL,0),(11,11,NULL,NULL,1,0),(12,12,6,3,NULL,1),(13,13,2,NULL,NULL,0),(14,14,2,NULL,NULL,0),(15,15,6,NULL,NULL,0),(16,16,6,NULL,NULL,0),(17,17,6,NULL,NULL,0),(18,18,6,NULL,NULL,0),(19,19,2,NULL,NULL,0),(20,20,3,NULL,NULL,1),(21,21,3,NULL,NULL,1),(22,22,3,NULL,NULL,1),(23,23,2,NULL,NULL,0),(24,24,2,NULL,NULL,0),(25,25,2,NULL,NULL,1),(28,28,NULL,NULL,1,0),(29,29,NULL,NULL,1,0),(30,30,3,NULL,NULL,1),(31,31,2,NULL,NULL,0),(32,32,3,NULL,NULL,0),(33,33,3,NULL,NULL,0),(34,34,3,NULL,NULL,0),(35,35,3,NULL,NULL,0),(36,36,3,NULL,NULL,0),(37,37,6,3,NULL,2),(38,38,NULL,NULL,1,2),(39,39,NULL,NULL,1,0),(40,40,6,NULL,NULL,0),(41,41,2,NULL,NULL,0),(42,42,2,NULL,NULL,1),(43,43,2,NULL,NULL,0),(44,44,2,NULL,NULL,0),(45,45,2,NULL,NULL,0),(46,46,2,NULL,NULL,0),(47,47,2,NULL,NULL,0),(48,48,2,NULL,NULL,0),(49,49,2,NULL,NULL,0),(50,50,6,NULL,NULL,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE recurrence_detail DISABLE KEYS */;
INSERT INTO recurrence_detail (details_id, meeting_id, recurrence_id, recur_after, version_no) VALUES (1,1,1,1,9),(2,2,2,1,0),(3,3,3,7,0),(4,4,2,1,1),(5,5,1,1,1),(6,6,1,1,0),(7,7,1,1,1),(8,8,2,1,0),(9,9,2,1,1),(10,10,1,1,0),(11,11,2,1,0),(12,12,2,1,1),(13,13,1,1,0),(14,14,1,1,0),(15,15,1,1,0),(16,16,1,1,0),(17,17,1,1,0),(18,18,1,1,0),(19,19,1,1,0),(20,20,1,1,1),(21,21,1,1,1),(22,22,1,1,1),(23,23,1,1,0),(24,24,1,1,0),(25,25,1,1,1),(28,28,2,1,0),(29,29,2,1,0),(30,30,1,1,1),(31,31,1,1,0),(32,32,1,1,0),(33,33,1,1,0),(34,34,1,1,0),(35,35,1,1,0),(36,36,1,1,0),(37,37,2,1,2),(38,38,2,1,2),(39,39,2,1,0),(40,40,1,1,0),(41,41,1,1,0),(42,42,1,1,1),(43,43,1,1,0),(44,44,1,1,0),(45,45,1,1,0),(46,46,1,1,0),(47,47,1,1,0),(48,48,1,1,0),(49,49,1,1,0),(50,50,1,1,0);
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
INSERT INTO recurrence_type (recurrence_id, recurrence_name, description) VALUES (1,'Week(s)','Weekly Recurrence'),(2,'Month(s)','Monthly Recurrence'),(3,'Day(s)','Daily Recurrence');
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
INSERT INTO repayment_rule (repayment_rule_id, repayment_rule_lookup_id) VALUES (1,576),(2,577),(3,578),(4,626);
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
INSERT INTO report (report_id, report_category_id, report_name, report_identifier, activity_id, report_active) VALUES (1,1,'Collection Sheet Report','collection_sheet_report',229,1),(2,6,'Branch Cash Confirmation Report','branch_cash_confirmation_report',231,1),(3,6,'Branch Progress Report','branch_progress_report',232,1),(4,6,'Detailed Aging Of Portfolio At Risk Report','detailed_aging_of_portfolio_at_risk_report',236,1),(5,6,'General Ledger Report','general_ledger_report',237,1);
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
INSERT INTO report_category (report_category_id, report_category_value, activity_id) VALUES (1,'Client Detail',145),(2,'Performance',148),(3,'Center',146),(4,'Loan Product Detail',149),(5,'Status',147),(6,'Analysis',150),(7,'Miscellaneous',151);
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
INSERT INTO report_jasper_map (report_id, report_category_id, report_name, report_identifier, report_jasper) VALUES (1,1,'Collection Sheet Report','collection_sheet_report','CollectionSheetReport.rptdesign'),(2,6,'Branch Cash Confirmation Report','branch_cash_confirmation_report','BranchCashConfirmationReport.rptdesign'),(3,6,'Branch Progress Report','branch_progress_report','ProgressReport.rptdesign'),(4,6,'Detailed Aging Of Portfolio At Risk Report','detailed_aging_of_portfolio_at_risk_report','DetailedAgingPortfolioAtRiskReport.rptdesign'),(5,6,'General Ledger Report','general_ledger_report','GeneralLedgerReport.rptdesign');
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
INSERT INTO role (role_id, role_name, version_no, created_by, created_date, updated_by, updated_date) VALUES (1,'Admin',3,NULL,NULL,1,'2011-02-18');
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
INSERT INTO roles_activity (activity_id, role_id) VALUES (3,1),(4,1),(6,1),(7,1),(9,1),(10,1),(15,1),(16,1),(19,1),(20,1),(21,1),(23,1),(24,1),(25,1),(35,1),(36,1),(37,1),(38,1),(39,1),(40,1),(41,1),(42,1),(43,1),(44,1),(46,1),(47,1),(48,1),(49,1),(50,1),(51,1),(52,1),(53,1),(54,1),(55,1),(57,1),(58,1),(59,1),(60,1),(61,1),(62,1),(63,1),(64,1),(65,1),(66,1),(68,1),(69,1),(70,1),(71,1),(72,1),(73,1),(74,1),(75,1),(76,1),(77,1),(79,1),(80,1),(81,1),(82,1),(83,1),(85,1),(86,1),(87,1),(88,1),(91,1),(92,1),(94,1),(95,1),(97,1),(98,1),(101,1),(102,1),(103,1),(104,1),(105,1),(106,1),(108,1),(109,1),(110,1),(115,1),(116,1),(118,1),(119,1),(120,1),(121,1),(122,1),(126,1),(127,1),(128,1),(129,1),(131,1),(135,1),(137,1),(138,1),(139,1),(140,1),(146,1),(147,1),(148,1),(149,1),(151,1),(178,1),(179,1),(180,1),(181,1),(182,1),(183,1),(184,1),(185,1),(186,1),(187,1),(188,1),(189,1),(190,1),(191,1),(192,1),(193,1),(194,1),(195,1),(197,1),(198,1),(199,1),(200,1),(201,1),(202,1),(204,1),(205,1),(206,1),(208,1),(210,1),(211,1),(213,1),(214,1),(215,1),(216,1),(217,1),(218,1),(219,1),(220,1),(221,1),(222,1),(223,1),(224,1),(225,1),(226,1),(228,1),(229,1),(231,1),(232,1),(233,1),(234,1),(235,1),(236,1),(237,1),(238,1),(239,1),(240,1),(241,1),(242,1),(243,1),(244,1),(245,1),(246,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE saving_schedule DISABLE KEYS */;
INSERT INTO saving_schedule (id, account_id, customer_id, currency_id, action_date, deposit, deposit_currency_id, deposit_paid, deposit_paid_currency_id, payment_status, installment_id, payment_date, parent_flag, version_no) VALUES (1,2,5,NULL,'2010-01-22','10.0000',2,'0.0000',2,0,1,NULL,NULL,0),(2,2,5,NULL,'2010-01-29','10.0000',2,'0.0000',2,0,2,NULL,NULL,0),(3,2,5,NULL,'2010-02-05','10.0000',2,'0.0000',2,0,3,NULL,NULL,0),(4,2,5,NULL,'2010-02-12','10.0000',2,'0.0000',2,0,4,NULL,NULL,0),(5,2,5,NULL,'2010-02-19','10.0000',2,'0.0000',2,0,5,NULL,NULL,0),(6,2,5,NULL,'2010-02-26','10.0000',2,'0.0000',2,0,6,NULL,NULL,0),(7,2,5,NULL,'2010-03-05','10.0000',2,'0.0000',2,0,7,NULL,NULL,0),(8,2,5,NULL,'2010-03-12','10.0000',2,'0.0000',2,0,8,NULL,NULL,0),(9,2,5,NULL,'2010-03-19','10.0000',2,'0.0000',2,0,9,NULL,NULL,0),(10,2,5,NULL,'2010-03-26','10.0000',2,'0.0000',2,0,10,NULL,NULL,0),(11,2,10,NULL,'2011-02-18','10.0000',2,'0.0000',2,0,1,NULL,NULL,0),(12,2,10,NULL,'2011-02-25','10.0000',2,'0.0000',2,0,2,NULL,NULL,0),(13,2,10,NULL,'2011-03-04','10.0000',2,'0.0000',2,0,3,NULL,NULL,0),(14,2,10,NULL,'2011-03-11','10.0000',2,'0.0000',2,0,4,NULL,NULL,0),(15,2,10,NULL,'2011-03-18','10.0000',2,'0.0000',2,0,5,NULL,NULL,0),(16,2,10,NULL,'2011-03-25','10.0000',2,'0.0000',2,0,6,NULL,NULL,0),(17,2,10,NULL,'2011-04-01','10.0000',2,'0.0000',2,0,7,NULL,NULL,0),(18,2,10,NULL,'2011-04-08','10.0000',2,'0.0000',2,0,8,NULL,NULL,0),(19,2,10,NULL,'2011-04-15','10.0000',2,'0.0000',2,0,9,NULL,NULL,0),(20,2,10,NULL,'2011-04-22','10.0000',2,'0.0000',2,0,10,NULL,NULL,0),(21,2,5,NULL,'2010-04-02','10.0000',2,'0.0000',2,0,11,NULL,NULL,0),(22,2,5,NULL,'2010-04-09','10.0000',2,'0.0000',2,0,12,NULL,NULL,0),(23,2,5,NULL,'2010-04-16','10.0000',2,'0.0000',2,0,13,NULL,NULL,0),(24,2,5,NULL,'2010-04-23','10.0000',2,'0.0000',2,0,14,NULL,NULL,0),(25,2,5,NULL,'2010-04-30','10.0000',2,'0.0000',2,0,15,NULL,NULL,0),(26,2,5,NULL,'2010-05-07','10.0000',2,'0.0000',2,0,16,NULL,NULL,0),(27,2,5,NULL,'2010-05-14','10.0000',2,'0.0000',2,0,17,NULL,NULL,0),(28,2,5,NULL,'2010-05-21','10.0000',2,'0.0000',2,0,18,NULL,NULL,0),(29,2,5,NULL,'2010-05-28','10.0000',2,'0.0000',2,0,19,NULL,NULL,0),(30,2,5,NULL,'2010-06-04','10.0000',2,'0.0000',2,0,20,NULL,NULL,0),(31,2,10,NULL,'2010-04-02','10.0000',2,'0.0000',2,0,11,NULL,NULL,0),(32,2,10,NULL,'2010-04-09','10.0000',2,'0.0000',2,0,12,NULL,NULL,0),(33,2,10,NULL,'2010-04-16','10.0000',2,'0.0000',2,0,13,NULL,NULL,0),(34,2,10,NULL,'2010-04-23','10.0000',2,'0.0000',2,0,14,NULL,NULL,0),(35,2,10,NULL,'2010-04-30','10.0000',2,'0.0000',2,0,15,NULL,NULL,0),(36,2,10,NULL,'2010-05-07','10.0000',2,'0.0000',2,0,16,NULL,NULL,0),(37,2,10,NULL,'2010-05-14','10.0000',2,'0.0000',2,0,17,NULL,NULL,0),(38,2,10,NULL,'2010-05-21','10.0000',2,'0.0000',2,0,18,NULL,NULL,0),(39,2,10,NULL,'2010-05-28','10.0000',2,'0.0000',2,0,19,NULL,NULL,0),(40,2,10,NULL,'2010-06-04','10.0000',2,'0.0000',2,0,20,NULL,NULL,0),(41,2,17,NULL,'2011-02-25','10.0000',2,'0.0000',2,0,1,NULL,NULL,0),(42,2,17,NULL,'2011-03-04','10.0000',2,'0.0000',2,0,2,NULL,NULL,0),(43,2,17,NULL,'2011-03-11','10.0000',2,'0.0000',2,0,3,NULL,NULL,0),(44,2,17,NULL,'2011-03-18','10.0000',2,'0.0000',2,0,4,NULL,NULL,0),(45,2,17,NULL,'2011-03-25','10.0000',2,'0.0000',2,0,5,NULL,NULL,0),(46,2,17,NULL,'2011-04-01','10.0000',2,'0.0000',2,0,6,NULL,NULL,0),(47,2,17,NULL,'2011-04-08','10.0000',2,'0.0000',2,0,7,NULL,NULL,0),(48,2,17,NULL,'2011-04-15','10.0000',2,'0.0000',2,0,8,NULL,NULL,0),(49,2,17,NULL,'2011-04-22','10.0000',2,'0.0000',2,0,9,NULL,NULL,0),(50,2,17,NULL,'2011-04-29','10.0000',2,'0.0000',2,0,10,NULL,NULL,0),(51,2,23,NULL,'2020-01-03','10.0000',2,'0.0000',2,0,1,NULL,NULL,0),(52,2,23,NULL,'2020-01-10','10.0000',2,'0.0000',2,0,2,NULL,NULL,0),(53,2,23,NULL,'2020-01-17','10.0000',2,'0.0000',2,0,3,NULL,NULL,0),(54,2,23,NULL,'2020-01-24','10.0000',2,'0.0000',2,0,4,NULL,NULL,0),(55,2,23,NULL,'2020-01-31','10.0000',2,'0.0000',2,0,5,NULL,NULL,0),(56,2,23,NULL,'2020-02-07','10.0000',2,'0.0000',2,0,6,NULL,NULL,0),(57,2,23,NULL,'2020-02-14','10.0000',2,'0.0000',2,0,7,NULL,NULL,0),(58,2,23,NULL,'2020-02-21','10.0000',2,'0.0000',2,0,8,NULL,NULL,0),(59,2,23,NULL,'2020-02-28','10.0000',2,'0.0000',2,0,9,NULL,NULL,0),(60,2,23,NULL,'2020-03-06','10.0000',2,'0.0000',2,0,10,NULL,NULL,0);
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
INSERT INTO savings_account (account_id, activation_date, savings_balance, savings_balance_currency_id, recommended_amount, recommended_amount_currency_id, recommended_amnt_unit_id, savings_type_id, int_to_be_posted, int_to_be_posted_currency_id, last_int_calc_date, last_int_post_date, next_int_calc_date, next_int_post_date, inter_int_calc_date, prd_offering_id, interest_rate, interest_calculation_type_id, time_per_for_int_calc, min_amnt_for_int, min_amnt_for_int_currency_id) VALUES (2,'2009-01-01','1000.0000',2,'10.0000',2,1,2,'0.0000',2,NULL,NULL,NULL,'2009-08-31',NULL,1,'10.0000000000',1,NULL,NULL,NULL);
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
INSERT INTO savings_activity_details (id, created_by, account_id, created_date, account_action_id, amount, amount_currency_id, balance_amount, balance_amount_currency_id) VALUES (1,1,2,'2011-02-17 18:30:00',6,'1000.0000',2,'1000.0000',2);
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
INSERT INTO savings_offering (prd_offering_id, interest_calculation_type_id, savings_type_id, recommended_amnt_unit_id, recommended_amount, recommended_amount_currency_id, interest_rate, max_amnt_withdrawl, max_amnt_withdrawl_currency_id, min_amnt_for_int, min_amnt_for_int_currency_id, deposit_glcode_id, interest_glcode_id) VALUES (1,1,2,NULL,'0.0000',2,'10.0000000000','0.0000',2,'0.0000',2,36,56),(8,1,1,NULL,'10.0000',2,'1.0000000000','0.0000',2,'0.0000',2,32,56);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE savings_performance DISABLE KEYS */;
INSERT INTO savings_performance (id, account_id, total_deposits, total_deposits_currency_id, total_withdrawals, total_withdrawals_currency_id, total_interest_earned, total_interest_earned_currency_id, missed_deposits) VALUES (1,2,'1000.0000',2,'0.0000',2,'0.0000',2,NULL);
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
INSERT INTO savings_trxn_detail (account_trxn_id, deposit_amount, deposit_amount_currency_id, withdrawal_amount, withdrawal_amount_currency_id, interest_amount, interest_amount_currency_id, balance, balance_currency_id) VALUES (1,'1000.0000',2,'0.0000',2,'0.0000',2,'1000.0000',2);
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
INSERT INTO savings_type (savings_type_id, lookup_id) VALUES (1,118),(2,119);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE sections DISABLE KEYS */;
INSERT INTO sections (id, question_group_id, name, sequence_number) VALUES (1,1,'Misc',0);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE sections_questions DISABLE KEYS */;
INSERT INTO sections_questions (id, section_id, question_id, is_required, sequence_number) VALUES (1,1,1,0,0);
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
INSERT INTO spouse_father_lookup (spouse_father_id, lookup_id) VALUES (1,128),(2,129),(4,622),(5,623);
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
INSERT INTO supported_locale (locale_id, country_id, lang_id, locale_name, default_locale) VALUES (1,6,1,'EN',1),(2,7,2,'Icelandic',0),(3,8,3,'Spanish',0),(4,9,4,'French',0),(5,10,5,'Chinese-China',0),(6,11,6,'Swahili-Kenya',0),(7,12,6,'Swahili-Tanzania',0),(8,13,6,'Swahili-Uganda',0),(9,14,7,'Arabic-Algeria',0),(10,15,7,'Arabic-Bahrain',0),(11,16,7,'Arabic-Comoros',0),(12,17,7,'Arabic-Chad',0),(13,18,7,'Arabic-Djibouti',0),(14,19,7,'Arabic-Egypt',0),(15,20,7,'Arabic-Eritrea',0),(16,21,7,'Arabic-Iraq',0),(17,22,7,'Arabic-Israel',0),(18,23,7,'Arabic-Jordan',0),(19,24,7,'Arabic-Kuwait',0),(20,25,7,'Arabic-Lebanon',0),(21,26,7,'Arabic-Libyan Arab Rebublic',0),(22,27,7,'Arabic-Mauritania',0),(23,28,7,'Arabic-Morocco',0),(24,29,7,'Arabic-Oman',0),(25,30,7,'Arabic-Qatar',0),(26,31,7,'Arabic-Saudi Arabia',0),(27,32,7,'Arabic-Somalia',0),(28,33,7,'Arabic-Sudan',0),(29,34,7,'Arabic-Syrian Arab Republic',0),(30,35,7,'Arabic-Tunisia',0),(31,36,7,'Arabic-United Arab Emirates',0),(32,37,7,'Arabic-Yemen',0),(33,38,7,'Arabic-Palestinian Territory, Occupied',0),(34,39,7,'Arabic-Western Sahara',0),(35,40,8,'Portuguese-Angola',0),(36,41,8,'Portuguese-Brazil',0),(37,42,8,'Portuguese-Cape Verde',0),(38,43,8,'Portuguese-Guinea-Bissau',0),(39,44,8,'Portuguese-Equatorial Guinea',0),(40,45,8,'Portuguese-Macau',0),(41,46,8,'Portuguese-Mozambique',0),(42,47,8,'Portuguese-Portugal',0),(43,48,8,'Portuguese-Sao Tome and Principe',0),(44,49,9,'Khmer-Cambodia',0),(45,50,10,'Lao-Laos',0),(46,25,1,'English-Lebanon-AlMajmoua',0),(47,51,11,'Hungarian-Hungary',0);
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
INSERT INTO transaction_type (transaction_id, transaction_name) VALUES (1,'Loan Disbursement'),(2,'Loan Repayment'),(3,'Savings Deposit'),(4,'Savings Withdrawals'),(5,'Client Fees/penalty payments');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE variable_installment_details DISABLE KEYS */;
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
INSERT INTO yes_no_master (yes_no_master_id, lookup_id) VALUES (1,124),(2,125);
/*!40000 ALTER TABLE yes_no_master ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

