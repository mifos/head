-- MySQL dump 10.11
--
-- Host: localhost    Database: mifos
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt-log

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

--
-- Table structure for table `accepted_payment_type`
--

DROP TABLE IF EXISTS `accepted_payment_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `accepted_payment_type` (
  `ACCEPTED_PAYMENT_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `TRANSACTION_ID` smallint(6) NOT NULL,
  `PAYMENT_TYPE_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`ACCEPTED_PAYMENT_TYPE_ID`),
  KEY `TRANSACTION_ID` (`TRANSACTION_ID`),
  KEY `PAYMENT_TYPE_ID` (`PAYMENT_TYPE_ID`),
  CONSTRAINT `accepted_payment_type_ibfk_1` FOREIGN KEY (`TRANSACTION_ID`) REFERENCES `transaction_type` (`TRANSACTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `accepted_payment_type_ibfk_2` FOREIGN KEY (`PAYMENT_TYPE_ID`) REFERENCES `payment_type` (`PAYMENT_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `accepted_payment_type`
--

LOCK TABLES `accepted_payment_type` WRITE;
/*!40000 ALTER TABLE `accepted_payment_type` DISABLE KEYS */;
INSERT INTO `accepted_payment_type` VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,1),(5,5,1);
/*!40000 ALTER TABLE `accepted_payment_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account` (
  `ACCOUNT_ID` int(11) NOT NULL auto_increment,
  `GLOBAL_ACCOUNT_NUM` varchar(100) default NULL,
  `CUSTOMER_ID` int(11) default NULL,
  `ACCOUNT_STATE_ID` smallint(6) default NULL,
  `ACCOUNT_TYPE_ID` smallint(6) NOT NULL,
  `OFFICE_ID` smallint(6) default NULL,
  `PERSONNEL_ID` smallint(6) default NULL,
  `CREATED_BY` smallint(6) NOT NULL,
  `CREATED_DATE` date NOT NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  `CLOSED_DATE` date default NULL,
  `VERSION_NO` int(11) default NULL,
  `OFFSETTING_ALLOWABLE` smallint(6) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_ID`),
  UNIQUE KEY `ACCOUNT_GLOBAL_IDX` (`GLOBAL_ACCOUNT_NUM`),
  KEY `ACCOUNT_STATE_ID` (`ACCOUNT_STATE_ID`),
  KEY `ACCOUNT_TYPE_ID` (`ACCOUNT_TYPE_ID`),
  KEY `PERSONNEL_ID` (`PERSONNEL_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  KEY `CUSTOMER_ID_ACCOUNT_IDX` (`CUSTOMER_ID`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`ACCOUNT_STATE_ID`) REFERENCES `account_state` (`ACCOUNT_STATE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_ibfk_2` FOREIGN KEY (`ACCOUNT_TYPE_ID`) REFERENCES `account_type` (`ACCOUNT_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_ibfk_3` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_ibfk_4` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_ibfk_5` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'000100000000001',1,11,3,2,2,1,'2008-12-04',NULL,NULL,NULL,1,1),(2,'000100000000002',2,11,3,2,2,1,'2008-12-04',NULL,NULL,NULL,2,1),(3,'000100000000003',3,11,3,2,2,1,'2008-12-04',NULL,NULL,NULL,2,1),(4,'000100000000004',3,5,1,2,2,1,'2008-12-04',1,'2008-12-04',NULL,3,1),(5,'000100000000005',4,11,3,2,2,1,'2008-12-04',NULL,NULL,NULL,2,1),(6,'000100000000006',4,3,1,2,2,1,'2008-12-04',1,'2008-12-04',NULL,2,1),(7,'000100000000007',5,11,3,2,2,1,'2008-12-04',NULL,NULL,NULL,2,1),(8,'000100000000008',5,5,1,2,2,1,'2008-12-04',1,'2008-12-04',NULL,3,1),(9,'000100000000009',6,11,3,3,3,1,'2008-12-04',NULL,NULL,NULL,1,1),(10,'000100000000010',7,11,3,3,3,1,'2008-12-04',NULL,NULL,NULL,2,1),(11,'000100000000011',8,11,3,3,3,1,'2008-12-04',NULL,NULL,NULL,2,1),(12,'000100000000012',8,5,1,3,3,1,'2008-12-04',1,'2008-12-04',NULL,3,1),(13,'000100000000013',9,11,3,3,3,1,'2008-12-04',NULL,NULL,NULL,2,1),(14,'000100000000014',9,3,1,3,3,1,'2008-12-04',1,'2008-12-04',NULL,2,1),(15,'000100000000015',10,11,3,3,3,1,'2008-12-04',NULL,NULL,NULL,2,1),(16,'000100000000016',10,5,1,3,3,1,'2008-12-04',1,'2008-12-04',NULL,3,1),(17,'000100000000017',11,11,3,3,3,1,'2008-12-04',NULL,NULL,NULL,2,1),(18,'000100000000018',11,3,1,3,3,1,'2008-12-04',1,'2008-12-04',NULL,2,1),(19,'000100000000019',12,11,3,4,4,1,'2008-12-04',NULL,NULL,NULL,1,1),(20,'000100000000020',13,11,3,4,4,1,'2008-12-04',NULL,NULL,NULL,2,1),(21,'000100000000021',14,11,3,4,4,1,'2008-12-04',NULL,NULL,NULL,2,1),(22,'000100000000022',14,5,1,4,4,1,'2008-12-04',1,'2008-12-04',NULL,3,1),(23,'000100000000023',15,11,3,4,4,1,'2008-12-04',NULL,NULL,NULL,2,1),(24,'000100000000024',15,3,1,4,4,1,'2008-12-04',1,'2008-12-04',NULL,2,1),(25,'000100000000025',16,11,3,4,4,1,'2008-12-04',NULL,NULL,NULL,2,1),(26,'000100000000026',16,5,1,4,4,1,'2008-12-04',1,'2008-12-04',NULL,3,1),(27,'000100000000027',17,11,3,4,4,1,'2008-12-04',NULL,NULL,NULL,2,1),(28,'000100000000028',17,3,1,4,4,1,'2008-12-04',1,'2008-12-04',NULL,2,1);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_action`
--

DROP TABLE IF EXISTS `account_action`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_action` (
  `ACCOUNT_ACTION_ID` smallint(6) NOT NULL,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_ACTION_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `account_action_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_action`
--

LOCK TABLES `account_action` WRITE;
/*!40000 ALTER TABLE `account_action` DISABLE KEYS */;
INSERT INTO `account_action` VALUES (1,167),(2,168),(3,169),(4,170),(5,171),(6,172),(7,173),(8,191),(9,192),(10,193),(11,214),(12,362),(13,364),(14,366),(15,547),(16,548),(17,549),(18,572),(19,573),(20,610);
/*!40000 ALTER TABLE `account_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_activity`
--

DROP TABLE IF EXISTS `account_activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_activity` (
  `ACTIVITY_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `PERSONNEL_ID` smallint(6) NOT NULL,
  `ACTIVITY_NAME` varchar(50) NOT NULL,
  `PRINCIPAL` decimal(10,3) default NULL,
  `PRINCIPAL_CURRENCY_ID` smallint(6) default NULL,
  `PRINCIPAL_OUTSTANDING` decimal(10,3) default NULL,
  `PRINCIPAL_OUTSTANDING_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST` decimal(13,10) default NULL,
  `INTEREST_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_OUTSTANDING` decimal(13,10) default NULL,
  `INTEREST_OUTSTANDING_CURRENCY_ID` smallint(6) default NULL,
  `FEE` decimal(13,2) default NULL,
  `FEE_CURRENCY_ID` smallint(6) default NULL,
  `FEE_OUTSTANDING` decimal(13,2) default NULL,
  `FEE_OUTSTANDING_CURRENCY_ID` smallint(6) default NULL,
  `PENALTY` decimal(13,10) default NULL,
  `PENALTY_CURRENCY_ID` smallint(6) default NULL,
  `PENALTY_OUTSTANDING` decimal(13,10) default NULL,
  `PENALTY_OUTSTANDING_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ACTIVITY_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `PRINCIPAL_CURRENCY_ID` (`PRINCIPAL_CURRENCY_ID`),
  KEY `PRINCIPAL_OUTSTANDING_CURRENCY_ID` (`PRINCIPAL_OUTSTANDING_CURRENCY_ID`),
  KEY `INTEREST_CURRENCY_ID` (`INTEREST_CURRENCY_ID`),
  KEY `INTEREST_OUTSTANDING_CURRENCY_ID` (`INTEREST_OUTSTANDING_CURRENCY_ID`),
  KEY `FEE_CURRENCY_ID` (`FEE_CURRENCY_ID`),
  KEY `FEE_OUTSTANDING_CURRENCY_ID` (`FEE_OUTSTANDING_CURRENCY_ID`),
  KEY `PENALTY_CURRENCY_ID` (`PENALTY_CURRENCY_ID`),
  KEY `PENALTY_OUTSTANDING_CURRENCY_ID` (`PENALTY_OUTSTANDING_CURRENCY_ID`),
  CONSTRAINT `account_activity_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_2` FOREIGN KEY (`PRINCIPAL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_3` FOREIGN KEY (`PRINCIPAL_OUTSTANDING_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_4` FOREIGN KEY (`INTEREST_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_5` FOREIGN KEY (`INTEREST_OUTSTANDING_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_6` FOREIGN KEY (`FEE_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_7` FOREIGN KEY (`FEE_OUTSTANDING_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_8` FOREIGN KEY (`PENALTY_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_activity_ibfk_9` FOREIGN KEY (`PENALTY_OUTSTANDING_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_activity`
--

LOCK TABLES `account_activity` WRITE;
/*!40000 ALTER TABLE `account_activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_custom_field`
--

DROP TABLE IF EXISTS `account_custom_field`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_custom_field` (
  `ACCOUNT_CUSTOM_FIELD_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `FIELD_ID` smallint(6) NOT NULL,
  `FIELD_VALUE` varchar(200) default NULL,
  PRIMARY KEY  (`ACCOUNT_CUSTOM_FIELD_ID`),
  KEY `FIELD_ID` (`FIELD_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  CONSTRAINT `account_custom_field_ibfk_1` FOREIGN KEY (`FIELD_ID`) REFERENCES `custom_field_definition` (`FIELD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_custom_field_ibfk_2` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_custom_field`
--

LOCK TABLES `account_custom_field` WRITE;
/*!40000 ALTER TABLE `account_custom_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_custom_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_fees`
--

DROP TABLE IF EXISTS `account_fees`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_fees` (
  `ACCOUNT_FEE_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `FEE_ID` smallint(6) NOT NULL,
  `FEE_FREQUENCY` int(11) default NULL,
  `STATUS` smallint(6) default NULL,
  `INHERITED_FLAG` smallint(6) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `ACCOUNT_FEE_AMNT` decimal(10,3) NOT NULL,
  `ACCOUNT_FEE_AMNT_CURRENCY_ID` smallint(6) default NULL,
  `FEE_AMNT` decimal(10,3) NOT NULL,
  `FEE_STATUS` smallint(6) default NULL,
  `STATUS_CHANGE_DATE` date default NULL,
  `VERSION_NO` int(11) NOT NULL,
  `LAST_APPLIED_DATE` date default NULL,
  PRIMARY KEY  (`ACCOUNT_FEE_ID`),
  KEY `FEE_ID` (`FEE_ID`),
  KEY `ACCOUNT_FEE_AMNT_CURRENCY_ID` (`ACCOUNT_FEE_AMNT_CURRENCY_ID`),
  KEY `FEE_FREQUENCY` (`FEE_FREQUENCY`),
  KEY `ACCOUNT_FEES_ID_IDX` (`ACCOUNT_ID`,`FEE_ID`),
  CONSTRAINT `account_fees_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_fees_ibfk_2` FOREIGN KEY (`FEE_ID`) REFERENCES `fees` (`FEE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_fees_ibfk_3` FOREIGN KEY (`ACCOUNT_FEE_AMNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_fees_ibfk_4` FOREIGN KEY (`FEE_FREQUENCY`) REFERENCES `recurrence_detail` (`DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_fees`
--

LOCK TABLES `account_fees` WRITE;
/*!40000 ALTER TABLE `account_fees` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_fees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_flag_detail`
--

DROP TABLE IF EXISTS `account_flag_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_flag_detail` (
  `ACCOUNT_FLAG_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `FLAG_ID` smallint(6) NOT NULL,
  `CREATED_BY` smallint(6) NOT NULL,
  `CREATED_DATE` date NOT NULL,
  PRIMARY KEY  (`ACCOUNT_FLAG_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `FLAG_ID` (`FLAG_ID`),
  KEY `CREATED_BY` (`CREATED_BY`),
  CONSTRAINT `account_flag_detail_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_flag_detail_ibfk_2` FOREIGN KEY (`FLAG_ID`) REFERENCES `account_state_flag` (`FLAG_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_flag_detail_ibfk_3` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_flag_detail`
--

LOCK TABLES `account_flag_detail` WRITE;
/*!40000 ALTER TABLE `account_flag_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_flag_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_notes`
--

DROP TABLE IF EXISTS `account_notes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_notes` (
  `ACCOUNT_NOTES_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `NOTE` varchar(500) NOT NULL,
  `COMMENT_DATE` date NOT NULL,
  `OFFICER_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_NOTES_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `OFFICER_ID` (`OFFICER_ID`),
  CONSTRAINT `account_notes_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_notes_ibfk_2` FOREIGN KEY (`OFFICER_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_notes`
--

LOCK TABLES `account_notes` WRITE;
/*!40000 ALTER TABLE `account_notes` DISABLE KEYS */;
INSERT INTO `account_notes` VALUES (1,4,'Approved.','2008-12-04',1),(2,6,'approved','2008-12-04',1),(3,8,'approved','2008-12-04',1),(4,12,'Approved.','2008-12-04',1),(5,14,'approved','2008-12-04',1),(6,16,'approved','2008-12-04',1),(7,18,'approved','2008-12-04',1),(8,22,'Approved.','2008-12-04',1),(9,24,'approved','2008-12-04',1),(10,26,'approved','2008-12-04',1),(11,28,'approved','2008-12-04',1);
/*!40000 ALTER TABLE `account_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_payment`
--

DROP TABLE IF EXISTS `account_payment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_payment` (
  `PAYMENT_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `PAYMENT_TYPE_ID` smallint(6) NOT NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `AMOUNT` decimal(10,3) NOT NULL,
  `RECEIPT_NUMBER` varchar(25) default NULL,
  `VOUCHER_NUMBER` varchar(50) default NULL,
  `CHECK_NUMBER` varchar(50) default NULL,
  `PAYMENT_DATE` date NOT NULL,
  `RECEIPT_DATE` date default NULL,
  `BANK_NAME` varchar(50) default NULL,
  PRIMARY KEY  (`PAYMENT_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `PAYMENT_TYPE_ID` (`PAYMENT_TYPE_ID`),
  KEY `ACCOUNT_ID_ACCOUNT_PAYMENT_IDX` (`ACCOUNT_ID`),
  CONSTRAINT `account_payment_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_payment_ibfk_2` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_payment_ibfk_3` FOREIGN KEY (`PAYMENT_TYPE_ID`) REFERENCES `payment_type` (`PAYMENT_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_payment`
--

LOCK TABLES `account_payment` WRITE;
/*!40000 ALTER TABLE `account_payment` DISABLE KEYS */;
INSERT INTO `account_payment` VALUES (1,4,1,2,'2000.000','',NULL,NULL,'2008-12-04','2008-12-04',NULL),(2,8,1,2,'10000.000','',NULL,NULL,'2008-12-04','2008-12-04',NULL),(3,12,1,2,'2000.000','',NULL,NULL,'2008-12-04','2008-12-04',NULL),(4,16,1,2,'10000.000','',NULL,NULL,'2008-12-04','2008-12-04',NULL),(5,22,1,2,'2000.000','',NULL,NULL,'2008-12-04','2008-12-04',NULL),(6,26,1,2,'10000.000','',NULL,NULL,'2008-12-04','2008-12-04',NULL);
/*!40000 ALTER TABLE `account_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_state`
--

DROP TABLE IF EXISTS `account_state`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_state` (
  `ACCOUNT_STATE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  `PRD_TYPE_ID` smallint(6) NOT NULL,
  `CURRENTLY_IN_USE` smallint(6) NOT NULL,
  `STATUS_DESCRIPTION` varchar(200) default NULL,
  PRIMARY KEY  (`ACCOUNT_STATE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `account_state_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_state`
--

LOCK TABLES `account_state` WRITE;
/*!40000 ALTER TABLE `account_state` DISABLE KEYS */;
INSERT INTO `account_state` VALUES (1,17,1,1,'Partial application'),(2,18,1,1,'Application pending approval'),(3,19,1,1,'Application approved'),(4,20,1,0,'Disbursed to loan officer'),(5,21,1,1,'Active in good standing'),(6,22,1,1,'Closed - obligation met'),(7,23,1,1,'Closed - written off'),(8,24,1,1,'Closed - rescheduled'),(9,25,1,1,'Active in bad standing'),(10,141,1,1,'Canceled'),(11,142,1,1,'Customer Account Active'),(12,143,1,1,'Customer Account Inactive'),(13,181,2,1,'Partial application'),(14,182,2,1,'Application pending approval'),(15,183,2,1,'Canceled'),(16,184,2,1,'Active'),(17,185,2,1,'Closed'),(18,210,2,1,'Inactive');
/*!40000 ALTER TABLE `account_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_state_flag`
--

DROP TABLE IF EXISTS `account_state_flag`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_state_flag` (
  `FLAG_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  `STATUS_ID` smallint(6) NOT NULL,
  `FLAG_DESCRIPTION` varchar(200) default NULL,
  `RETAIN_FLAG` smallint(6) NOT NULL,
  PRIMARY KEY  (`FLAG_ID`),
  KEY `STATUS_ID` (`STATUS_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `account_state_flag_ibfk_1` FOREIGN KEY (`STATUS_ID`) REFERENCES `account_state` (`ACCOUNT_STATE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_state_flag_ibfk_2` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_state_flag`
--

LOCK TABLES `account_state_flag` WRITE;
/*!40000 ALTER TABLE `account_state_flag` DISABLE KEYS */;
INSERT INTO `account_state_flag` VALUES (1,174,10,'Withdraw',0),(2,175,10,'Rejected',0),(3,176,10,'Other',0),(4,211,15,'Withdraw',0),(5,212,15,'Rejected',0),(6,213,15,'Blacklisted',1),(7,571,10,'Loan reversal',0);
/*!40000 ALTER TABLE `account_state_flag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_status_change_history`
--

DROP TABLE IF EXISTS `account_status_change_history`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_status_change_history` (
  `ACCOUNT_STATUS_CHANGE_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `OLD_STATUS` smallint(6) default NULL,
  `NEW_STATUS` smallint(6) NOT NULL,
  `CHANGED_BY` smallint(6) NOT NULL,
  `CHANGED_DATE` date NOT NULL,
  PRIMARY KEY  (`ACCOUNT_STATUS_CHANGE_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `OLD_STATUS` (`OLD_STATUS`),
  KEY `NEW_STATUS` (`NEW_STATUS`),
  KEY `CHANGED_BY` (`CHANGED_BY`),
  CONSTRAINT `account_status_change_history_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_status_change_history_ibfk_2` FOREIGN KEY (`OLD_STATUS`) REFERENCES `account_state` (`ACCOUNT_STATE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_status_change_history_ibfk_3` FOREIGN KEY (`NEW_STATUS`) REFERENCES `account_state` (`ACCOUNT_STATE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_status_change_history_ibfk_4` FOREIGN KEY (`CHANGED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_status_change_history`
--

LOCK TABLES `account_status_change_history` WRITE;
/*!40000 ALTER TABLE `account_status_change_history` DISABLE KEYS */;
INSERT INTO `account_status_change_history` VALUES (1,4,2,2,1,'2008-12-04'),(2,4,2,3,1,'2008-12-04'),(3,4,3,5,1,'2008-12-04'),(4,6,2,2,1,'2008-12-04'),(5,6,2,3,1,'2008-12-04'),(6,8,2,2,1,'2008-12-04'),(7,8,2,3,1,'2008-12-04'),(8,8,3,5,1,'2008-12-04'),(9,12,2,2,1,'2008-12-04'),(10,12,2,3,1,'2008-12-04'),(11,12,3,5,1,'2008-12-04'),(12,14,2,2,1,'2008-12-04'),(13,14,2,3,1,'2008-12-04'),(14,16,2,2,1,'2008-12-04'),(15,16,2,3,1,'2008-12-04'),(16,16,3,5,1,'2008-12-04'),(17,18,2,2,1,'2008-12-04'),(18,18,2,3,1,'2008-12-04'),(19,22,2,2,1,'2008-12-04'),(20,22,2,3,1,'2008-12-04'),(21,22,3,5,1,'2008-12-04'),(22,24,2,2,1,'2008-12-04'),(23,24,2,3,1,'2008-12-04'),(24,26,2,2,1,'2008-12-04'),(25,26,2,3,1,'2008-12-04'),(26,26,3,5,1,'2008-12-04'),(27,28,2,2,1,'2008-12-04'),(28,28,2,3,1,'2008-12-04');
/*!40000 ALTER TABLE `account_status_change_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_trxn`
--

DROP TABLE IF EXISTS `account_trxn`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_trxn` (
  `ACCOUNT_TRXN_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `PAYMENT_ID` int(11) NOT NULL,
  `PERSONNEL_ID` int(11) default NULL,
  `ACCOUNT_ACTION_ID` smallint(6) NOT NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `AMOUNT` decimal(10,3) NOT NULL,
  `DUE_DATE` date default NULL,
  `COMMENTS` varchar(200) default NULL,
  `ACTION_DATE` date NOT NULL,
  `CREATED_DATE` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `CUSTOMER_ID` int(11) default NULL,
  `INSTALLMENT_ID` smallint(6) default NULL,
  `RELATED_TRXN_ID` int(11) default NULL,
  PRIMARY KEY  (`ACCOUNT_TRXN_ID`),
  KEY `ACCOUNT_ACTION_ID` (`ACCOUNT_ACTION_ID`),
  KEY `PAYMENT_ID` (`PAYMENT_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `AMOUNT_CURRENCY_ID` (`AMOUNT_CURRENCY_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  KEY `ACCOUNT_ID_ACCOUNT_TRXN_IDX` (`ACCOUNT_ID`),
  CONSTRAINT `account_trxn_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_trxn_ibfk_2` FOREIGN KEY (`ACCOUNT_ACTION_ID`) REFERENCES `account_action` (`ACCOUNT_ACTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_trxn_ibfk_3` FOREIGN KEY (`PAYMENT_ID`) REFERENCES `account_payment` (`PAYMENT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_trxn_ibfk_4` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_trxn_ibfk_5` FOREIGN KEY (`AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `account_trxn_ibfk_6` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_trxn`
--

LOCK TABLES `account_trxn` WRITE;
/*!40000 ALTER TABLE `account_trxn` DISABLE KEYS */;
INSERT INTO `account_trxn` VALUES (1,4,1,1,10,NULL,2,'2000.000','2008-12-04','-','2008-12-04','2008-12-04 20:45:51',3,0,NULL),(2,8,2,1,10,NULL,2,'10000.000','2008-12-04','-','2008-12-04','2008-12-04 20:48:41',5,0,NULL),(3,12,3,1,10,NULL,2,'2000.000','2008-12-04','-','2008-12-04','2008-12-04 20:52:30',8,0,NULL),(4,16,4,1,10,NULL,2,'10000.000','2008-12-04','-','2008-12-04','2008-12-04 20:55:19',10,0,NULL),(5,22,5,1,10,NULL,2,'2000.000','2008-12-04','-','2008-12-04','2008-12-04 21:01:50',14,0,NULL),(6,26,6,1,10,NULL,2,'10000.000','2008-12-04','-','2008-12-04','2008-12-04 21:04:40',16,0,NULL);
/*!40000 ALTER TABLE `account_trxn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_type`
--

DROP TABLE IF EXISTS `account_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `account_type` (
  `ACCOUNT_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  `DESCRIPTION` varchar(50) default NULL,
  PRIMARY KEY  (`ACCOUNT_TYPE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `account_type_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `account_type`
--

LOCK TABLES `account_type` WRITE;
/*!40000 ALTER TABLE `account_type` DISABLE KEYS */;
INSERT INTO `account_type` VALUES (1,126,'Loan Account'),(2,127,'Savings Account'),(3,140,'Customer Account'),(4,126,'Individual Loan Account');
/*!40000 ALTER TABLE `account_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `activity` (
  `ACTIVITY_ID` smallint(6) NOT NULL auto_increment,
  `PARENT_ID` smallint(6) default NULL,
  `ACTIVITY_NAME_LOOKUP_ID` int(11) NOT NULL,
  `DESCRIPTION_LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ACTIVITY_ID`),
  KEY `PARENT_ID` (`PARENT_ID`),
  KEY `ACTIVITY_NAME_LOOKUP_ID` (`ACTIVITY_NAME_LOOKUP_ID`),
  KEY `DESCRIPTION_LOOKUP_ID` (`DESCRIPTION_LOOKUP_ID`),
  CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`PARENT_ID`) REFERENCES `activity` (`ACTIVITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `activity_ibfk_2` FOREIGN KEY (`ACTIVITY_NAME_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `activity_ibfk_3` FOREIGN KEY (`DESCRIPTION_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=233 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
INSERT INTO `activity` VALUES (1,NULL,371,371),(2,1,372,372),(3,2,373,373),(4,2,374,374),(5,1,375,375),(6,5,376,376),(7,5,377,377),(8,1,378,378),(9,8,379,379),(10,8,380,380),(13,NULL,381,381),(14,13,382,382),(15,14,383,383),(16,14,384,384),(17,NULL,385,385),(18,17,386,386),(19,18,387,387),(20,18,388,388),(21,18,389,389),(22,17,390,390),(23,22,391,391),(24,22,392,392),(25,22,393,393),(33,NULL,394,394),(34,33,395,395),(35,34,396,396),(36,34,397,397),(37,34,398,398),(38,34,399,399),(39,34,400,400),(40,34,401,401),(41,34,402,402),(42,34,403,403),(43,34,404,404),(44,34,405,405),(46,34,407,407),(47,34,408,408),(48,34,409,409),(49,34,410,410),(50,34,411,411),(51,34,412,412),(52,34,413,413),(53,34,414,414),(54,34,415,415),(55,34,416,416),(56,33,417,417),(57,56,418,418),(58,56,419,419),(59,56,420,420),(60,56,421,421),(61,56,422,422),(62,56,423,423),(63,56,424,424),(64,56,425,425),(65,56,426,426),(66,56,427,427),(68,56,429,429),(69,56,430,430),(70,56,431,431),(71,56,432,432),(72,56,433,433),(73,56,434,434),(74,56,435,435),(75,56,436,436),(76,56,437,437),(77,56,438,438),(78,33,439,439),(79,78,440,440),(80,78,441,441),(81,78,442,442),(82,78,443,443),(83,78,444,444),(85,78,446,446),(86,78,447,447),(87,78,448,438),(88,78,449,449),(89,NULL,450,450),(90,89,451,451),(91,90,452,452),(92,90,453,453),(93,89,454,454),(94,93,455,455),(95,93,456,456),(96,89,457,457),(97,96,458,458),(98,96,459,459),(99,NULL,460,460),(100,99,461,461),(101,100,462,462),(102,100,463,463),(103,100,464,464),(104,100,465,465),(105,100,466,466),(106,100,467,467),(108,100,469,469),(109,100,470,470),(110,100,471,471),(113,99,474,474),(115,113,475,475),(116,113,476,476),(118,113,478,478),(119,113,479,479),(120,113,480,480),(121,34,481,481),(122,56,482,482),(126,34,483,483),(127,78,484,484),(128,78,485,485),(129,100,486,486),(131,113,487,487),(135,18,488,488),(136,NULL,489,489),(137,136,490,490),(138,136,491,491),(139,136,492,492),(140,136,493,493),(141,NULL,494,494),(145,141,498,498),(146,141,499,499),(147,141,500,500),(148,141,501,501),(149,141,502,502),(150,141,503,503),(151,141,504,504),(178,113,531,531),(179,100,532,532),(180,136,533,533),(181,136,534,534),(182,136,535,535),(183,136,536,536),(184,136,537,537),(185,136,538,538),(186,136,546,546),(187,136,551,551),(188,136,552,552),(189,113,553,553),(190,136,554,554),(191,136,555,555),(192,196,560,560),(193,13,562,562),(194,18,563,563),(195,90,561,561),(196,NULL,564,564),(197,196,565,565),(198,34,566,566),(199,56,567,567),(200,78,568,568),(201,196,569,569),(202,99,570,570),(203,NULL,574,574),(204,203,575,575),(205,203,579,579),(206,34,580,580),(208,34,582,582),(209,89,583,583),(210,209,584,584),(211,209,585,585),(213,203,587,587),(214,141,588,588),(215,141,589,589),(216,141,590,590),(217,113,591,591),(218,99,592,592),(219,1,593,593),(220,141,594,594),(221,141,595,595),(222,141,596,596),(223,141,597,597),(224,203,598,598),(225,141,602,602),(226,141,603,603),(227,NULL,605,605),(228,227,606,606),(229,145,607,607),(230,203,608,608),(231,150,611,611),(232,150,612,612);
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_document`
--

DROP TABLE IF EXISTS `admin_document`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `admin_document` (
  `ADMIN_DOCUMENT_ID` int(11) NOT NULL auto_increment,
  `ADMIN_DOCUMENT_NAME` varchar(200) default NULL,
  `ADMIN_DOCUMENT_IDENTIFIER` varchar(100) default NULL,
  `ADMIN_DOCUMENT_ACTIVE` smallint(6) default NULL,
  `CREATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`ADMIN_DOCUMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `admin_document`
--

LOCK TABLES `admin_document` WRITE;
/*!40000 ALTER TABLE `admin_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_document_acc_state_mix`
--

DROP TABLE IF EXISTS `admin_document_acc_state_mix`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `admin_document_acc_state_mix` (
  `ADMIN_DOC_ACC_STATE_MIX_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_STATE_ID` smallint(6) NOT NULL,
  `ADMIN_DOCUMENT_ID` int(11) NOT NULL,
  `CREATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`ADMIN_DOC_ACC_STATE_MIX_ID`),
  KEY `admin_document_acc_state_mix_fk` (`ACCOUNT_STATE_ID`),
  KEY `admin_document_acc_state_mix_fk1` (`ADMIN_DOCUMENT_ID`),
  CONSTRAINT `admin_document_acc_state_mix_fk` FOREIGN KEY (`ACCOUNT_STATE_ID`) REFERENCES `account_state` (`ACCOUNT_STATE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `admin_document_acc_state_mix_fk1` FOREIGN KEY (`ADMIN_DOCUMENT_ID`) REFERENCES `admin_document` (`ADMIN_DOCUMENT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `admin_document_acc_state_mix`
--

LOCK TABLES `admin_document_acc_state_mix` WRITE;
/*!40000 ALTER TABLE `admin_document_acc_state_mix` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_document_acc_state_mix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `attendance` (
  `MEETING_ID` int(11) NOT NULL,
  `MEETING_DATE` date NOT NULL,
  `ATTENDANCE` smallint(6) default NULL,
  `NOTES` varchar(200) NOT NULL,
  PRIMARY KEY  (`MEETING_ID`,`MEETING_DATE`),
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`MEETING_ID`) REFERENCES `customer_meeting` (`CUSTOMER_MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_branch_cash_confirmation_report`
--

DROP TABLE IF EXISTS `batch_branch_cash_confirmation_report`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_branch_cash_confirmation_report` (
  `BRANCH_CASH_CONFIRMATION_REPORT_ID` int(11) NOT NULL auto_increment,
  `BRANCH_ID` smallint(6) NOT NULL,
  `RUN_DATE` date NOT NULL,
  PRIMARY KEY  (`BRANCH_CASH_CONFIRMATION_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_branch_cash_confirmation_report`
--

LOCK TABLES `batch_branch_cash_confirmation_report` WRITE;
/*!40000 ALTER TABLE `batch_branch_cash_confirmation_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_branch_cash_confirmation_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_branch_confirmation_disbursement`
--

DROP TABLE IF EXISTS `batch_branch_confirmation_disbursement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_branch_confirmation_disbursement` (
  `ID` int(11) NOT NULL auto_increment,
  `BRANCH_CASH_CONFIRMATION_REPORT_ID` int(11) NOT NULL,
  `PRODUCT_NAME` varchar(50) NOT NULL,
  `ACTUAL` decimal(20,3) NOT NULL,
  `ACTUAL_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `BRANCH_CASH_CONFIRMATION_REPORT_ID` (`BRANCH_CASH_CONFIRMATION_REPORT_ID`),
  CONSTRAINT `batch_branch_confirmation_disbursement_ibfk_1` FOREIGN KEY (`BRANCH_CASH_CONFIRMATION_REPORT_ID`) REFERENCES `batch_branch_cash_confirmation_report` (`BRANCH_CASH_CONFIRMATION_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_branch_confirmation_disbursement`
--

LOCK TABLES `batch_branch_confirmation_disbursement` WRITE;
/*!40000 ALTER TABLE `batch_branch_confirmation_disbursement` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_branch_confirmation_disbursement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_branch_confirmation_issue`
--

DROP TABLE IF EXISTS `batch_branch_confirmation_issue`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_branch_confirmation_issue` (
  `ID` int(11) NOT NULL auto_increment,
  `BRANCH_CASH_CONFIRMATION_REPORT_ID` int(11) NOT NULL,
  `PRODUCT_NAME` varchar(50) NOT NULL,
  `ACTUAL` decimal(20,3) NOT NULL,
  `ACTUAL_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `BRANCH_CASH_CONFIRMATION_REPORT_ID` (`BRANCH_CASH_CONFIRMATION_REPORT_ID`),
  CONSTRAINT `batch_branch_confirmation_issue_ibfk_1` FOREIGN KEY (`BRANCH_CASH_CONFIRMATION_REPORT_ID`) REFERENCES `batch_branch_cash_confirmation_report` (`BRANCH_CASH_CONFIRMATION_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_branch_confirmation_issue`
--

LOCK TABLES `batch_branch_confirmation_issue` WRITE;
/*!40000 ALTER TABLE `batch_branch_confirmation_issue` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_branch_confirmation_issue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_branch_confirmation_recovery`
--

DROP TABLE IF EXISTS `batch_branch_confirmation_recovery`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_branch_confirmation_recovery` (
  `RECOVERY_ID` int(11) NOT NULL auto_increment,
  `BRANCH_CASH_CONFIRMATION_REPORT_ID` int(11) NOT NULL,
  `PRODUCT_NAME` varchar(50) NOT NULL,
  `DUE` decimal(20,3) NOT NULL,
  `DUE_CURRENCY_ID` smallint(6) NOT NULL,
  `ACTUAL` decimal(20,3) NOT NULL,
  `ACTUAL_CURRENCY_ID` smallint(6) NOT NULL,
  `ARREARS` decimal(20,3) NOT NULL,
  `ARREARS_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`RECOVERY_ID`),
  KEY `BRANCH_CASH_CONFIRMATION_REPORT_ID` (`BRANCH_CASH_CONFIRMATION_REPORT_ID`),
  CONSTRAINT `batch_branch_confirmation_recovery_ibfk_1` FOREIGN KEY (`BRANCH_CASH_CONFIRMATION_REPORT_ID`) REFERENCES `batch_branch_cash_confirmation_report` (`BRANCH_CASH_CONFIRMATION_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_branch_confirmation_recovery`
--

LOCK TABLES `batch_branch_confirmation_recovery` WRITE;
/*!40000 ALTER TABLE `batch_branch_confirmation_recovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_branch_confirmation_recovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_branch_report`
--

DROP TABLE IF EXISTS `batch_branch_report`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_branch_report` (
  `BRANCH_REPORT_ID` int(11) NOT NULL auto_increment,
  `BRANCH_ID` smallint(6) NOT NULL,
  `RUN_DATE` date NOT NULL,
  PRIMARY KEY  (`BRANCH_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_branch_report`
--

LOCK TABLES `batch_branch_report` WRITE;
/*!40000 ALTER TABLE `batch_branch_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_branch_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_client_summary`
--

DROP TABLE IF EXISTS `batch_client_summary`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_client_summary` (
  `CLIENT_SUMMARY_ID` int(11) NOT NULL auto_increment,
  `BRANCH_REPORT_ID` int(11) NOT NULL,
  `FIELD_NAME` varchar(50) NOT NULL,
  `TOTAL` varchar(50) default NULL,
  `VPOOR_TOTAL` varchar(50) default NULL,
  PRIMARY KEY  (`CLIENT_SUMMARY_ID`),
  KEY `BRANCH_REPORT_ID` (`BRANCH_REPORT_ID`),
  CONSTRAINT `batch_client_summary_ibfk_1` FOREIGN KEY (`BRANCH_REPORT_ID`) REFERENCES `batch_branch_report` (`BRANCH_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_client_summary`
--

LOCK TABLES `batch_client_summary` WRITE;
/*!40000 ALTER TABLE `batch_client_summary` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_client_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_loan_arrears_aging`
--

DROP TABLE IF EXISTS `batch_loan_arrears_aging`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_loan_arrears_aging` (
  `LOAN_ARREARS_AGING_ID` int(11) NOT NULL auto_increment,
  `AGING_PERIOD_ID` int(11) NOT NULL,
  `BRANCH_REPORT_ID` int(11) NOT NULL,
  `CLIENTS_AGING` int(11) NOT NULL,
  `LOANS_AGING` int(11) NOT NULL,
  `AMOUNT_AGING` decimal(20,3) NOT NULL,
  `AMOUNT_AGING_CURRENCY_ID` smallint(6) NOT NULL,
  `AMOUNT_OUTSTANDING_AGING` decimal(20,3) NOT NULL,
  `AMOUNT_OUTSTANDING_AGING_CURRENCY_ID` smallint(6) NOT NULL,
  `INTEREST_AGING` decimal(20,3) NOT NULL,
  `INTEREST_AGING_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`LOAN_ARREARS_AGING_ID`),
  KEY `BRANCH_REPORT_ID` (`BRANCH_REPORT_ID`),
  CONSTRAINT `batch_loan_arrears_aging_ibfk_1` FOREIGN KEY (`BRANCH_REPORT_ID`) REFERENCES `batch_branch_report` (`BRANCH_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_loan_arrears_aging`
--

LOCK TABLES `batch_loan_arrears_aging` WRITE;
/*!40000 ALTER TABLE `batch_loan_arrears_aging` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_loan_arrears_aging` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_loan_arrears_profile`
--

DROP TABLE IF EXISTS `batch_loan_arrears_profile`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_loan_arrears_profile` (
  `LOAN_ARREARS_PROFILE_ID` int(11) NOT NULL auto_increment,
  `BRANCH_REPORT_ID` int(11) NOT NULL,
  `LOANS_IN_ARREARS` int(11) NOT NULL,
  `CLIENTS_IN_ARREARS` int(11) NOT NULL,
  `OVERDUE_BALANCE` decimal(20,3) NOT NULL,
  `OVERDUE_BALANCE_CURRENCY_ID` smallint(6) NOT NULL,
  `UNPAID_BALANCE` decimal(20,3) NOT NULL,
  `UNPAID_BALANCE_CURRENCY_ID` smallint(6) NOT NULL,
  `LOANS_AT_RISK` int(11) NOT NULL,
  `OUTSTANDING_AMOUNT_AT_RISK` decimal(20,3) NOT NULL,
  `OUTSTANDING_AMOUNT_AT_RISK_CURRENCY_ID` smallint(6) NOT NULL,
  `OVERDUE_AMOUNT_AT_RISK` decimal(20,3) NOT NULL,
  `OVERDUE_AMOUNT_AT_RISK_CURRENCY_ID` smallint(6) NOT NULL,
  `CLIENTS_AT_RISK` int(11) NOT NULL,
  PRIMARY KEY  (`LOAN_ARREARS_PROFILE_ID`),
  KEY `BRANCH_REPORT_ID` (`BRANCH_REPORT_ID`),
  CONSTRAINT `batch_loan_arrears_profile_ibfk_1` FOREIGN KEY (`BRANCH_REPORT_ID`) REFERENCES `batch_branch_report` (`BRANCH_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_loan_arrears_profile`
--

LOCK TABLES `batch_loan_arrears_profile` WRITE;
/*!40000 ALTER TABLE `batch_loan_arrears_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_loan_arrears_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_loan_details`
--

DROP TABLE IF EXISTS `batch_loan_details`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_loan_details` (
  `LOAN_DETAILS_ID` int(11) NOT NULL auto_increment,
  `BRANCH_REPORT_ID` int(11) NOT NULL,
  `PRODUCT_NAME` varchar(50) NOT NULL,
  `NUMBER_OF_LOANS_ISSUED` int(11) NOT NULL,
  `LOAN_AMOUNT_ISSUED` decimal(20,3) NOT NULL,
  `LOAN_AMOUNT_ISSUED_CURRENCY_ID` smallint(6) NOT NULL,
  `LOAN_INTEREST_ISSUED` decimal(20,3) NOT NULL,
  `LOAN_INTEREST_ISSUED_CURRENCY_ID` smallint(6) NOT NULL,
  `NUMBER_OF_LOANS_OUTSTANDING` int(11) NOT NULL,
  `LOAN_OUTSTANDING_AMOUNT` decimal(20,3) NOT NULL,
  `LOAN_OUTSTANDING_AMOUNT_CURRENCY_ID` smallint(6) NOT NULL,
  `LOAN_OUTSTANDING_INTEREST` decimal(20,3) NOT NULL,
  `LOAN_OUTSTANDING_INTEREST_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`LOAN_DETAILS_ID`),
  KEY `BRANCH_REPORT_ID` (`BRANCH_REPORT_ID`),
  CONSTRAINT `batch_loan_details_ibfk_1` FOREIGN KEY (`BRANCH_REPORT_ID`) REFERENCES `batch_branch_report` (`BRANCH_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_loan_details`
--

LOCK TABLES `batch_loan_details` WRITE;
/*!40000 ALTER TABLE `batch_loan_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_loan_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_staff_summary`
--

DROP TABLE IF EXISTS `batch_staff_summary`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_staff_summary` (
  `STAFF_SUMMARY_ID` int(11) NOT NULL auto_increment,
  `BRANCH_REPORT_ID` int(11) NOT NULL,
  `PERSONNEL_ID` smallint(6) NOT NULL,
  `PERSONNEL_NAME` varchar(50) NOT NULL,
  `JOINING_DATE` date default NULL,
  `ACTIVE_BORROWERS` int(11) NOT NULL,
  `ACTIVE_LOANS` int(11) NOT NULL,
  `CENTER_COUNT` int(11) NOT NULL,
  `CLIENT_COUNT` int(11) NOT NULL,
  `LOAN_AMOUNT_OUTSTANDING` decimal(20,3) NOT NULL,
  `LOAN_AMOUNT_OUTSTANDING_CURRENCY_ID` smallint(6) NOT NULL,
  `INTEREST_FEES_OUTSTANDING` decimal(20,3) NOT NULL,
  `INTEREST_FEES_OUTSTANDING_CURRENCY_ID` smallint(6) NOT NULL,
  `PORTFOLIO_AT_RISK` decimal(20,3) NOT NULL,
  `TOTAL_CLIENTS_ENROLLED` int(11) NOT NULL,
  `CLIENTS_ENROLLED_THIS_MONTH` int(11) NOT NULL,
  `LOAN_ARREARS_AMOUNT` decimal(20,3) NOT NULL,
  `LOAN_ARREARS_AMOUNT_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`STAFF_SUMMARY_ID`),
  KEY `BRANCH_REPORT_ID` (`BRANCH_REPORT_ID`),
  CONSTRAINT `batch_staff_summary_ibfk_1` FOREIGN KEY (`BRANCH_REPORT_ID`) REFERENCES `batch_branch_report` (`BRANCH_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_staff_summary`
--

LOCK TABLES `batch_staff_summary` WRITE;
/*!40000 ALTER TABLE `batch_staff_summary` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_staff_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_staffing_level_summary`
--

DROP TABLE IF EXISTS `batch_staffing_level_summary`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `batch_staffing_level_summary` (
  `STAFFING_LEVEL_SUMMARY_ID` int(11) NOT NULL auto_increment,
  `BRANCH_REPORT_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL,
  `ROLE_NAME` varchar(50) NOT NULL,
  `PERSONNEL_COUNT` int(11) NOT NULL,
  PRIMARY KEY  (`STAFFING_LEVEL_SUMMARY_ID`),
  KEY `BRANCH_REPORT_ID` (`BRANCH_REPORT_ID`),
  CONSTRAINT `batch_staffing_level_summary_ibfk_1` FOREIGN KEY (`BRANCH_REPORT_ID`) REFERENCES `batch_branch_report` (`BRANCH_REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `batch_staffing_level_summary`
--

LOCK TABLES `batch_staffing_level_summary` WRITE;
/*!40000 ALTER TABLE `batch_staffing_level_summary` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_staffing_level_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `branch_ho_update`
--

DROP TABLE IF EXISTS `branch_ho_update`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `branch_ho_update` (
  `OFFICE_ID` smallint(6) NOT NULL,
  `LAST_UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`OFFICE_ID`),
  CONSTRAINT `branch_ho_update_ibfk_1` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `branch_ho_update`
--

LOCK TABLES `branch_ho_update` WRITE;
/*!40000 ALTER TABLE `branch_ho_update` DISABLE KEYS */;
/*!40000 ALTER TABLE `branch_ho_update` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_type`
--

DROP TABLE IF EXISTS `category_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `category_type` (
  `CATEGORY_ID` smallint(6) NOT NULL,
  `CATEGORY_LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`CATEGORY_ID`),
  KEY `CATEGORY_LOOKUP_ID` (`CATEGORY_LOOKUP_ID`),
  CONSTRAINT `category_type_ibfk_1` FOREIGN KEY (`CATEGORY_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `category_type`
--

LOCK TABLES `category_type` WRITE;
/*!40000 ALTER TABLE `category_type` DISABLE KEYS */;
INSERT INTO `category_type` VALUES (1,81),(2,82),(3,83),(4,84),(5,86);
/*!40000 ALTER TABLE `category_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `change_log`
--

DROP TABLE IF EXISTS `change_log`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `change_log` (
  `CHANGE_LOG_ID` int(11) NOT NULL auto_increment,
  `CHANGED_BY` smallint(6) NOT NULL,
  `MODIFIER_NAME` varchar(50) NOT NULL,
  `ENTITY_ID` int(11) default NULL,
  `ENTITY_TYPE` smallint(6) default NULL,
  `CHANGED_DATE` date default NULL,
  `FIELDS_CHANGED` varchar(250) default NULL,
  PRIMARY KEY  (`CHANGE_LOG_ID`),
  KEY `CHANGED_BY` (`CHANGED_BY`),
  KEY `CHANGE_LOG_IDX` (`ENTITY_TYPE`,`ENTITY_ID`,`CHANGED_DATE`),
  CONSTRAINT `change_log_ibfk_1` FOREIGN KEY (`CHANGED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `change_log`
--

LOCK TABLES `change_log` WRITE;
/*!40000 ALTER TABLE `change_log` DISABLE KEYS */;
INSERT INTO `change_log` VALUES (1,1,'mifos',2,12,'2008-12-04',NULL),(2,1,'mifos',3,1,'2008-12-04',NULL),(3,1,'mifos',4,22,'2008-12-04',NULL),(4,1,'mifos',4,1,'2008-12-04',NULL),(5,1,'mifos',6,22,'2008-12-04',NULL),(6,1,'mifos',5,1,'2008-12-04',NULL),(7,1,'mifos',8,22,'2008-12-04',NULL),(8,1,'mifos',7,12,'2008-12-04',NULL),(9,1,'mifos',8,1,'2008-12-04',NULL),(10,1,'mifos',12,22,'2008-12-04',NULL),(11,1,'mifos',9,1,'2008-12-04',NULL),(12,1,'mifos',14,22,'2008-12-04',NULL),(13,1,'mifos',10,1,'2008-12-04',NULL),(14,1,'mifos',16,22,'2008-12-04',NULL),(15,1,'mifos',11,1,'2008-12-04',NULL),(16,1,'mifos',18,22,'2008-12-04',NULL),(17,1,'mifos',13,12,'2008-12-04',NULL),(18,1,'mifos',14,1,'2008-12-04',NULL),(19,1,'mifos',22,22,'2008-12-04',NULL),(20,1,'mifos',15,1,'2008-12-04',NULL),(21,1,'mifos',24,22,'2008-12-04',NULL),(22,1,'mifos',16,1,'2008-12-04',NULL),(23,1,'mifos',26,22,'2008-12-04',NULL),(24,1,'mifos',17,1,'2008-12-04',NULL),(25,1,'mifos',28,22,'2008-12-04',NULL);
/*!40000 ALTER TABLE `change_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `change_log_detail`
--

DROP TABLE IF EXISTS `change_log_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `change_log_detail` (
  `SEQUENCE_NUM` int(11) NOT NULL auto_increment,
  `CHANGE_LOG_ID` int(11) NOT NULL,
  `FIELD_NAME` varchar(100) default NULL,
  `OLD_VALUE` varchar(200) default NULL,
  `NEW_VALUE` varchar(200) default NULL,
  PRIMARY KEY  (`SEQUENCE_NUM`),
  KEY `CHANGE_LOG_ID` (`CHANGE_LOG_ID`),
  CONSTRAINT `change_log_detail_ibfk_1` FOREIGN KEY (`CHANGE_LOG_ID`) REFERENCES `change_log` (`CHANGE_LOG_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `change_log_detail`
--

LOCK TABLES `change_log_detail` WRITE;
/*!40000 ALTER TABLE `change_log_detail` DISABLE KEYS */;
INSERT INTO `change_log_detail` VALUES (1,1,'Status','Application Pending Approval','Active'),(2,2,'Status','Application Pending Approval','Active'),(3,3,'Status','Application Pending Approval','Application Approved'),(4,4,'Status','Application Pending Approval','Active'),(5,5,'Status','Application Pending Approval','Application Approved'),(6,6,'Status','Application Pending Approval','Active'),(7,7,'Status','Application Pending Approval','Application Approved'),(8,8,'Status','Application Pending Approval','Active'),(9,9,'Status','Application Pending Approval','Active'),(10,10,'Status','Application Pending Approval','Application Approved'),(11,11,'Status','Application Pending Approval','Active'),(12,12,'Status','Application Pending Approval','Application Approved'),(13,13,'Status','Application Pending Approval','Active'),(14,14,'Status','Application Pending Approval','Application Approved'),(15,15,'Status','Application Pending Approval','Active'),(16,16,'Status','Application Pending Approval','Application Approved'),(17,17,'Status','Application Pending Approval','Active'),(18,18,'Status','Application Pending Approval','Active'),(19,19,'Status','Application Pending Approval','Application Approved'),(20,20,'Status','Application Pending Approval','Active'),(21,21,'Status','Application Pending Approval','Application Approved'),(22,22,'Status','Application Pending Approval','Active'),(23,23,'Status','Application Pending Approval','Application Approved'),(24,24,'Status','Application Pending Approval','Active'),(25,25,'Status','Application Pending Approval','Application Approved');
/*!40000 ALTER TABLE `change_log_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checklist`
--

DROP TABLE IF EXISTS `checklist`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `checklist` (
  `CHECKLIST_ID` smallint(6) NOT NULL auto_increment,
  `CHECKLIST_NAME` varchar(100) default NULL,
  `CHECKLIST_STATUS` smallint(6) NOT NULL default '1',
  `LOCALE_ID` smallint(6) NOT NULL,
  `CREATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`CHECKLIST_ID`),
  KEY `LOCALE_ID` (`LOCALE_ID`),
  KEY `CREATED_BY` (`CREATED_BY`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  CONSTRAINT `checklist_ibfk_1` FOREIGN KEY (`LOCALE_ID`) REFERENCES `supported_locale` (`LOCALE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `checklist_ibfk_2` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `checklist_ibfk_3` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `checklist`
--

LOCK TABLES `checklist` WRITE;
/*!40000 ALTER TABLE `checklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `checklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checklist_detail`
--

DROP TABLE IF EXISTS `checklist_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `checklist_detail` (
  `DETAIL_ID` int(11) NOT NULL auto_increment,
  `CHECKLIST_ID` smallint(6) default NULL,
  `LOCALE_ID` smallint(6) default NULL,
  `DETAIL_TEXT` varchar(250) default NULL,
  `ANSWER_TYPE` smallint(6) NOT NULL,
  PRIMARY KEY  (`DETAIL_ID`),
  KEY `LOCALE_ID` (`LOCALE_ID`),
  KEY `CHK_DETAIL_IDX` (`CHECKLIST_ID`,`LOCALE_ID`),
  CONSTRAINT `checklist_detail_ibfk_1` FOREIGN KEY (`CHECKLIST_ID`) REFERENCES `checklist` (`CHECKLIST_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `checklist_detail_ibfk_2` FOREIGN KEY (`LOCALE_ID`) REFERENCES `supported_locale` (`LOCALE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `checklist_detail`
--

LOCK TABLES `checklist_detail` WRITE;
/*!40000 ALTER TABLE `checklist_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `checklist_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_initial_savings_offering`
--

DROP TABLE IF EXISTS `client_initial_savings_offering`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `client_initial_savings_offering` (
  `CLIENT_OFFERING_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`CLIENT_OFFERING_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `client_initial_savings_offering_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `client_initial_savings_offering_ibfk_2` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `client_initial_savings_offering`
--

LOCK TABLES `client_initial_savings_offering` WRITE;
/*!40000 ALTER TABLE `client_initial_savings_offering` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_initial_savings_offering` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_perf_history`
--

DROP TABLE IF EXISTS `client_perf_history`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `client_perf_history` (
  `ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `LAST_LOAN_AMNT` decimal(10,3) default NULL,
  `LAST_LOAN_AMNT_CURRENCY_ID` smallint(6) default NULL,
  `TOTAL_ACTIVE_LOANS` smallint(6) default NULL,
  `TOTAL_SAVINGS_AMNT` decimal(10,3) default NULL,
  `TOTAL_SAVINGS_AMNT_CURRENCY_ID` smallint(6) default NULL,
  `DELINQUINT_PORTFOLIO` decimal(10,3) default NULL,
  `DELINQUINT_PORTFOLIO_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `client_perf_history_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `client_perf_history`
--

LOCK TABLES `client_perf_history` WRITE;
/*!40000 ALTER TABLE `client_perf_history` DISABLE KEYS */;
INSERT INTO `client_perf_history` VALUES (1,3,'0.000',2,1,'0.000',2,'0.000',2),(2,4,'0.000',2,0,'0.000',2,'0.000',2),(3,5,'0.000',2,1,'0.000',2,'0.000',2),(4,8,'0.000',2,1,'0.000',2,'0.000',2),(5,9,'0.000',2,0,'0.000',2,'0.000',2),(6,10,'0.000',2,1,'0.000',2,'0.000',2),(7,11,'0.000',2,0,'0.000',2,'0.000',2),(8,14,'0.000',2,1,'0.000',2,'0.000',2),(9,15,'0.000',2,0,'0.000',2,'0.000',2),(10,16,'0.000',2,1,'0.000',2,'0.000',2),(11,17,'0.000',2,0,'0.000',2,'0.000',2);
/*!40000 ALTER TABLE `client_perf_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coa`
--

DROP TABLE IF EXISTS `coa`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `coa` (
  `COA_ID` smallint(6) NOT NULL auto_increment,
  `COA_Name` varchar(150) NOT NULL,
  `GLCODE_ID` smallint(6) NOT NULL,
  `CATEGORY_TYPE` varchar(20) default NULL,
  PRIMARY KEY  (`COA_ID`),
  KEY `GLCODE_ID` (`GLCODE_ID`),
  CONSTRAINT `coa_ibfk_1` FOREIGN KEY (`GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `coa`
--

LOCK TABLES `coa` WRITE;
/*!40000 ALTER TABLE `coa` DISABLE KEYS */;
INSERT INTO `coa` VALUES (1,'ASSETS',1,'ASSET'),(2,'Cash and bank balances',2,NULL),(3,'Petty Cash Accounts',3,NULL),(4,'Cash 1',4,NULL),(5,'Cash 2',5,NULL),(6,'Bank Balances',6,NULL),(7,'Bank Account 1',7,NULL),(8,'Bank Account 2',8,NULL),(9,'Loan Portfolio',9,NULL),(10,'Loans and Advances',10,NULL),(11,'IGLoan',11,NULL),(12,'ManagedICICI-IGLoan',12,NULL),(13,'SPLoan',13,NULL),(14,'ManagedICICI-SPLoan',14,NULL),(15,'WFLoan',15,NULL),(16,'Managed WFLoan',16,NULL),(17,'Emergency Loans',17,NULL),(18,'Special Loans',18,NULL),(19,'Micro Enterprises Loans',19,NULL),(20,'Loans to clients',20,NULL),(21,'Loan Loss Provisions',21,NULL),(22,'Write-offs',22,NULL),(23,'LIABILITIES',23,'LIABILITY'),(24,'Interest Payable',24,NULL),(25,'Interest payable on clients savings',25,NULL),(26,'Interest on mandatory savings',26,NULL),(27,'Clients Deposits',27,NULL),(28,'Clients Deposits',28,NULL),(29,'Emergency Fund',29,NULL),(30,'Margin Money-1',30,NULL),(31,'Margin Money-2',31,NULL),(32,'Village Development Fund',32,NULL),(33,'Savings accounts',33,NULL),(34,'Mandatory Savings',34,NULL),(35,'Mandatory Savings',35,NULL),(36,'Mandatory Savings Accounts',36,NULL),(37,'INCOME',37,'INCOME'),(38,'Direct Income',38,NULL),(39,'Interest income from loans',39,NULL),(40,'Interest',40,NULL),(41,'Interest on loans',41,NULL),(42,'Penalty',42,NULL),(43,'Income from micro credit & lending activities',43,NULL),(44,'Processing Fees',44,NULL),(45,'Annual Subscription Fee',45,NULL),(46,'Emergency Loan Documentation Fee',46,NULL),(47,'Sale of Publication',47,NULL),(48,'Fines & Penalties',48,NULL),(49,'Miscelleneous Income',49,NULL),(50,'Fees',50,NULL),(51,'Income from 999 Account',51,NULL),(52,'EXPENDITURE',52,'EXPENDITURE'),(53,'Direct Expenditure',53,NULL),(54,'Cost of Funds',54,NULL),(55,'Interest on clients voluntary savings',55,NULL),(56,'Interest on clients mandatory savings',56,NULL);
/*!40000 ALTER TABLE `coa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coa_idmapper`
--

DROP TABLE IF EXISTS `coa_idmapper`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `coa_idmapper` (
  `CONSTANT_ID` smallint(6) NOT NULL,
  `COA_ID` smallint(6) NOT NULL,
  `DESCRIPTION` varchar(50) default NULL,
  PRIMARY KEY  (`CONSTANT_ID`),
  KEY `COA_ID` (`COA_ID`),
  CONSTRAINT `coa_idmapper_ibfk_1` FOREIGN KEY (`COA_ID`) REFERENCES `coa` (`COA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `coa_idmapper`
--

LOCK TABLES `coa_idmapper` WRITE;
/*!40000 ALTER TABLE `coa_idmapper` DISABLE KEYS */;
/*!40000 ALTER TABLE `coa_idmapper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coahierarchy`
--

DROP TABLE IF EXISTS `coahierarchy`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `coahierarchy` (
  `COA_ID` smallint(6) NOT NULL,
  `PARENT_COAID` smallint(6) default NULL,
  KEY `COA_ID` (`COA_ID`),
  KEY `PARENT_COAID` (`PARENT_COAID`),
  CONSTRAINT `coahierarchy_ibfk_1` FOREIGN KEY (`COA_ID`) REFERENCES `coa` (`COA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coahierarchy_ibfk_2` FOREIGN KEY (`PARENT_COAID`) REFERENCES `coa` (`COA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `coahierarchy`
--

LOCK TABLES `coahierarchy` WRITE;
/*!40000 ALTER TABLE `coahierarchy` DISABLE KEYS */;
INSERT INTO `coahierarchy` VALUES (1,NULL),(2,1),(3,2),(4,3),(5,3),(6,2),(7,6),(8,6),(9,1),(10,9),(11,10),(12,10),(13,10),(14,10),(15,10),(16,10),(17,10),(18,10),(19,10),(20,10),(21,9),(22,21),(23,NULL),(24,23),(25,24),(26,25),(27,23),(28,27),(29,28),(30,28),(31,28),(32,28),(33,28),(34,23),(35,34),(36,35),(37,NULL),(38,37),(39,38),(40,39),(41,39),(42,39),(43,38),(44,43),(45,43),(46,43),(47,43),(48,43),(49,43),(50,43),(51,37),(52,NULL),(53,52),(54,53),(55,54),(56,54);
/*!40000 ALTER TABLE `coahierarchy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coll_sheet`
--

DROP TABLE IF EXISTS `coll_sheet`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `coll_sheet` (
  `COLL_SHEET_ID` int(11) NOT NULL auto_increment,
  `COLL_SHEET_DATE` date NOT NULL,
  `STATUS_FLAG` smallint(6) NOT NULL,
  `RUN_DATE` date NOT NULL,
  PRIMARY KEY  (`COLL_SHEET_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `coll_sheet`
--

LOCK TABLES `coll_sheet` WRITE;
/*!40000 ALTER TABLE `coll_sheet` DISABLE KEYS */;
/*!40000 ALTER TABLE `coll_sheet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coll_sheet_customer`
--

DROP TABLE IF EXISTS `coll_sheet_customer`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `coll_sheet_customer` (
  `COLL_SHEET_CUST_ID` bigint(20) NOT NULL auto_increment,
  `COLL_SHEET_ID` int(11) NOT NULL,
  `CUST_ID` int(11) NOT NULL,
  `CUST_DISPLAY_NAME` varchar(200) NOT NULL,
  `TOTAL_DUE_SAVINGS_LOAN` decimal(10,3) default NULL,
  `TOTAL_DUE_SAVINGS_LOAN_CURRENCY` smallint(6) default NULL,
  `CUST_ACCNT_FEE` decimal(10,3) default NULL,
  `CUST_ACCNT_FEE_CURRENCY` smallint(6) default NULL,
  `CUST__ACCNT_PENALTY` decimal(10,3) default NULL,
  `CUST__ACCNT_PENALTY_CURRENCY` smallint(6) default NULL,
  `CUST_LEVEL` smallint(6) NOT NULL,
  `CUST_ACCNT_ID` int(11) default NULL,
  `CUST_OFFICE_ID` smallint(6) NOT NULL,
  `SEARCH_ID` varchar(100) NOT NULL,
  `PARENT_CUSTOMER_ID` int(11) default NULL,
  `COLLECTIVE_LN_AMNT_DUE` decimal(10,3) default NULL,
  `COLLECTIVE_LN_AMNT_DUE_CURRENCY` smallint(6) default NULL,
  `COLLECTIVE_LN_DISBURSAL` decimal(10,3) default NULL,
  `COLLECTIVE_LN_DISBURSAL_CURRENCY` smallint(6) default NULL,
  `COLLECTIVE_SAVINGS_DUE` decimal(10,3) default NULL,
  `COLLECTIVE_SAVINGS_DUE_CURRENCY` smallint(6) default NULL,
  `COLLECTIVE_ACCNT_CHARGES` decimal(10,3) default NULL,
  `COLLECTIVE_ACCNT_CHARGES_CURRENCY` smallint(6) default NULL,
  `COLLECTIVE_TOTAL_CHARGES` decimal(10,3) default NULL,
  `COLLECTIVE_TOTAL_CHARGES_CURRENCY` smallint(6) default NULL,
  `COLLECTIVE_NET_CASH_IN` decimal(10,3) default NULL,
  `COLLECTIVE_NET_CASH_IN_CURRENCY` smallint(6) default NULL,
  `LOAN_OFFICER_ID` smallint(6) default NULL,
  PRIMARY KEY  (`COLL_SHEET_CUST_ID`),
  KEY `TOTAL_DUE_SAVINGS_LOAN_CURRENCY` (`TOTAL_DUE_SAVINGS_LOAN_CURRENCY`),
  KEY `CUST_ACCNT_FEE_CURRENCY` (`CUST_ACCNT_FEE_CURRENCY`),
  KEY `CUST__ACCNT_PENALTY_CURRENCY` (`CUST__ACCNT_PENALTY_CURRENCY`),
  KEY `COLLECTIVE_LN_AMNT_DUE_CURRENCY` (`COLLECTIVE_LN_AMNT_DUE_CURRENCY`),
  KEY `COLLECTIVE_LN_DISBURSAL_CURRENCY` (`COLLECTIVE_LN_DISBURSAL_CURRENCY`),
  KEY `COLLECTIVE_SAVINGS_DUE_CURRENCY` (`COLLECTIVE_SAVINGS_DUE_CURRENCY`),
  KEY `COLLECTIVE_ACCNT_CHARGES_CURRENCY` (`COLLECTIVE_ACCNT_CHARGES_CURRENCY`),
  KEY `COLLECTIVE_TOTAL_CHARGES_CURRENCY` (`COLLECTIVE_TOTAL_CHARGES_CURRENCY`),
  KEY `COLLECTIVE_NET_CASH_IN_CURRENCY` (`COLLECTIVE_NET_CASH_IN_CURRENCY`),
  CONSTRAINT `coll_sheet_customer_ibfk_1` FOREIGN KEY (`TOTAL_DUE_SAVINGS_LOAN_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_2` FOREIGN KEY (`CUST_ACCNT_FEE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_3` FOREIGN KEY (`CUST__ACCNT_PENALTY_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_4` FOREIGN KEY (`COLLECTIVE_LN_AMNT_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_5` FOREIGN KEY (`COLLECTIVE_LN_DISBURSAL_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_6` FOREIGN KEY (`COLLECTIVE_SAVINGS_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_7` FOREIGN KEY (`COLLECTIVE_ACCNT_CHARGES_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_8` FOREIGN KEY (`COLLECTIVE_TOTAL_CHARGES_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_customer_ibfk_9` FOREIGN KEY (`COLLECTIVE_NET_CASH_IN_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `coll_sheet_customer`
--

LOCK TABLES `coll_sheet_customer` WRITE;
/*!40000 ALTER TABLE `coll_sheet_customer` DISABLE KEYS */;
/*!40000 ALTER TABLE `coll_sheet_customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coll_sheet_loan_details`
--

DROP TABLE IF EXISTS `coll_sheet_loan_details`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `coll_sheet_loan_details` (
  `LOAN_DETAILS_ID` bigint(20) NOT NULL auto_increment,
  `COLL_SHEET_CUST_ID` bigint(20) NOT NULL,
  `ACCNT_ID` int(11) NOT NULL,
  `TOTAL_PRIN_DUE` decimal(10,3) NOT NULL,
  `TOTAL_PRIN_DUE_CURRENCY` smallint(6) default NULL,
  `ORIG_LOAN_AMNT` decimal(10,3) NOT NULL,
  `ORIG_LOAN_AMNT_CURRENCY` smallint(6) default NULL,
  `AMNT_TO_CLOSE_LOAN` decimal(10,3) NOT NULL,
  `AMNT_TO_CLOSE_LOAN_CURRENCY` smallint(6) default NULL,
  `TOTAL_NO_OF_INSTALLMENTS` smallint(6) NOT NULL,
  `CURRENT_INSTALLMENT_NO` smallint(6) default NULL,
  `PRINCIPAL_DUE` decimal(10,3) default NULL,
  `PRINCIPAL_DUE_CURRENCY` smallint(6) default NULL,
  `INTEREST_DUE` decimal(10,3) default NULL,
  `INTEREST_DUE_CURRENCY` smallint(6) default NULL,
  `FEES_DUE` decimal(10,3) default NULL,
  `FEES_DUE_CURRENCY` smallint(6) default NULL,
  `PENALTY_DUE` decimal(10,3) default NULL,
  `PENALTY_DUE_CURRENCY` smallint(6) default NULL,
  `TOTAL_SCHEDULED_AMNT_DUE` decimal(10,3) default NULL,
  `TOTAL_SCHEDULED_AMNT_DUE_CURRENCY` smallint(6) default NULL,
  `PRINCIPAL_OVERDUE` decimal(10,3) default NULL,
  `PRINCIPAL_OVERDUE_CURRENCY` smallint(6) default NULL,
  `INTEREST_OVERDUE` decimal(10,3) default NULL,
  `INTEREST_OVERDUE_CURRENCY` smallint(6) default NULL,
  `FEES_OVERDUE` decimal(10,3) default NULL,
  `FEES_OVERDUE_CURRENCY` smallint(6) default NULL,
  `PENALTY_OVERDUE` decimal(10,3) default NULL,
  `PENALTY_OVERDUE_CURRENCY` smallint(6) default NULL,
  `TOTAL_AMNT_OVERDUE` decimal(10,3) default NULL,
  `TOTAL_AMNT_OVERDUE_CURRENCY` smallint(6) default NULL,
  `TOTAL_AMNT_DUE` decimal(10,3) default NULL,
  `TOTAL_AMNT_DUE_CURRENCY` smallint(6) default NULL,
  `AMNT_TOBE_DISBURSED` decimal(10,3) default NULL,
  `AMNT_TOBE_DISBURSED_CURRENCY` smallint(6) default NULL,
  PRIMARY KEY  (`LOAN_DETAILS_ID`),
  KEY `ORIG_LOAN_AMNT_CURRENCY` (`ORIG_LOAN_AMNT_CURRENCY`),
  KEY `AMNT_TO_CLOSE_LOAN_CURRENCY` (`AMNT_TO_CLOSE_LOAN_CURRENCY`),
  KEY `PRINCIPAL_DUE_CURRENCY` (`PRINCIPAL_DUE_CURRENCY`),
  KEY `INTEREST_DUE_CURRENCY` (`INTEREST_DUE_CURRENCY`),
  KEY `FEES_DUE_CURRENCY` (`FEES_DUE_CURRENCY`),
  KEY `PENALTY_DUE_CURRENCY` (`PENALTY_DUE_CURRENCY`),
  KEY `TOTAL_SCHEDULED_AMNT_DUE_CURRENCY` (`TOTAL_SCHEDULED_AMNT_DUE_CURRENCY`),
  KEY `PRINCIPAL_OVERDUE_CURRENCY` (`PRINCIPAL_OVERDUE_CURRENCY`),
  KEY `INTEREST_OVERDUE_CURRENCY` (`INTEREST_OVERDUE_CURRENCY`),
  KEY `FEES_OVERDUE_CURRENCY` (`FEES_OVERDUE_CURRENCY`),
  KEY `PENALTY_OVERDUE_CURRENCY` (`PENALTY_OVERDUE_CURRENCY`),
  KEY `TOTAL_AMNT_OVERDUE_CURRENCY` (`TOTAL_AMNT_OVERDUE_CURRENCY`),
  KEY `TOTAL_AMNT_DUE_CURRENCY` (`TOTAL_AMNT_DUE_CURRENCY`),
  KEY `AMNT_TOBE_DISBURSED_CURRENCY` (`AMNT_TOBE_DISBURSED_CURRENCY`),
  KEY `TOTAL_PRIN_DUE_CURRENCY` (`TOTAL_PRIN_DUE_CURRENCY`),
  CONSTRAINT `coll_sheet_loan_details_ibfk_1` FOREIGN KEY (`ORIG_LOAN_AMNT_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_10` FOREIGN KEY (`FEES_OVERDUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_11` FOREIGN KEY (`PENALTY_OVERDUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_12` FOREIGN KEY (`TOTAL_AMNT_OVERDUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_13` FOREIGN KEY (`TOTAL_AMNT_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_14` FOREIGN KEY (`AMNT_TOBE_DISBURSED_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_15` FOREIGN KEY (`TOTAL_PRIN_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_2` FOREIGN KEY (`AMNT_TO_CLOSE_LOAN_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_3` FOREIGN KEY (`PRINCIPAL_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_4` FOREIGN KEY (`INTEREST_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_5` FOREIGN KEY (`FEES_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_6` FOREIGN KEY (`PENALTY_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_7` FOREIGN KEY (`TOTAL_SCHEDULED_AMNT_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_8` FOREIGN KEY (`PRINCIPAL_OVERDUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_loan_details_ibfk_9` FOREIGN KEY (`INTEREST_OVERDUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `coll_sheet_loan_details`
--

LOCK TABLES `coll_sheet_loan_details` WRITE;
/*!40000 ALTER TABLE `coll_sheet_loan_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `coll_sheet_loan_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coll_sheet_savings_details`
--

DROP TABLE IF EXISTS `coll_sheet_savings_details`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `coll_sheet_savings_details` (
  `SAVINGS_DETAILS_ID` bigint(20) NOT NULL auto_increment,
  `COLL_SHEET_CUST_ID` int(11) NOT NULL,
  `ACCNT_ID` int(11) NOT NULL,
  `ACCNT_BALANCE` decimal(10,3) default NULL,
  `ACCNT_BALANCE_CURRENCY` smallint(6) default NULL,
  `RECOMMENDED_AMNT_DUE` decimal(10,3) default NULL,
  `RECOMMENDED_AMNT_DUE_CURRENCY` smallint(6) default NULL,
  `AMNT_OVERDUE` decimal(10,3) default NULL,
  `AMNT_OVERDUE_CURRENCY` smallint(6) default NULL,
  `INSTALLMENT_ID` smallint(6) NOT NULL,
  `TOTAL_SAVINGS_AMNT_DUE` decimal(10,3) default NULL,
  `TOTAL_SAVINGS_AMNT_DUE_CURRENCY` smallint(6) default NULL,
  PRIMARY KEY  (`SAVINGS_DETAILS_ID`),
  KEY `ACCNT_BALANCE_CURRENCY` (`ACCNT_BALANCE_CURRENCY`),
  KEY `RECOMMENDED_AMNT_DUE_CURRENCY` (`RECOMMENDED_AMNT_DUE_CURRENCY`),
  KEY `AMNT_OVERDUE_CURRENCY` (`AMNT_OVERDUE_CURRENCY`),
  KEY `TOTAL_SAVINGS_AMNT_DUE_CURRENCY` (`TOTAL_SAVINGS_AMNT_DUE_CURRENCY`),
  CONSTRAINT `coll_sheet_savings_details_ibfk_1` FOREIGN KEY (`ACCNT_BALANCE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_savings_details_ibfk_2` FOREIGN KEY (`RECOMMENDED_AMNT_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_savings_details_ibfk_3` FOREIGN KEY (`AMNT_OVERDUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `coll_sheet_savings_details_ibfk_4` FOREIGN KEY (`TOTAL_SAVINGS_AMNT_DUE_CURRENCY`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `coll_sheet_savings_details`
--

LOCK TABLES `coll_sheet_savings_details` WRITE;
/*!40000 ALTER TABLE `coll_sheet_savings_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `coll_sheet_savings_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_key_value_integer`
--

DROP TABLE IF EXISTS `config_key_value_integer`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `config_key_value_integer` (
  `CONFIGURATION_ID` int(11) NOT NULL auto_increment,
  `CONFIGURATION_KEY` varchar(100) NOT NULL,
  `CONFIGURATION_VALUE` int(11) NOT NULL,
  PRIMARY KEY  (`CONFIGURATION_ID`),
  UNIQUE KEY `CONFIGURATION_KEY` (`CONFIGURATION_KEY`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `config_key_value_integer`
--

LOCK TABLES `config_key_value_integer` WRITE;
/*!40000 ALTER TABLE `config_key_value_integer` DISABLE KEYS */;
INSERT INTO `config_key_value_integer` VALUES (1,'x',0),(2,' ',0),(3,'jasperReportIsHidden',1),(4,'loanIndividualMonitoringIsEnabled',0),(5,'repaymentSchedulesIndependentOfMeetingIsEnabled',0),(6,'CenterHierarchyExists',1),(7,'ClientCanExistOutsideGroup',1),(8,'GroupCanApplyLoans',1),(9,'minDaysBetweenDisbursalAndFirstRepaymentDay',1),(10,'maxDaysBetweenDisbursalAndFirstRepaymentDay',365),(11,'AdministrativeDocumentsIsEnabled',1);
/*!40000 ALTER TABLE `config_key_value_integer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `country` (
  `COUNTRY_ID` smallint(6) NOT NULL,
  `COUNTRY_NAME` varchar(100) default NULL,
  `COUNTRY_SHORT_NAME` varchar(10) default NULL,
  PRIMARY KEY  (`COUNTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES (1,'America','US'),(2,'India','IN'),(3,'Spain','ES'),(4,'England','ENG'),(5,'South Africa','SA'),(6,'United Kingdom','GB'),(7,'Iceland','IS'),(8,'Spain','ES'),(9,'France','FR');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `currency` (
  `CURRENCY_ID` smallint(6) NOT NULL auto_increment,
  `CURRENCY_NAME` varchar(50) default NULL,
  `DISPLAY_SYMBOL` varchar(50) default NULL,
  `ROUNDING_MODE` smallint(6) default NULL,
  `ROUNDING_AMOUNT` decimal(6,3) default NULL,
  `DEFAULT_CURRENCY` smallint(6) default NULL,
  `DEFAULT_DIGITS_AFTER_DECIMAL` smallint(6) NOT NULL,
  `CURRENCY_CODE` varchar(3) default NULL,
  PRIMARY KEY  (`CURRENCY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `currency`
--

LOCK TABLES `currency` WRITE;
/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
INSERT INTO `currency` VALUES (1,'US Dollar','$',1,'0.500',0,1,'USD'),(2,'Indian Rupee','Rs',1,'1.000',1,1,'INR'),(3,'Euro','',2,'0.500',0,1,'EUR'),(4,'Pound Sterling','',1,'0.500',0,1,'GBP'),(5,'United Arab Emirates dirham','',NULL,NULL,0,2,'AED'),(6,'Afghani','',NULL,NULL,0,2,'AFN'),(7,'Lek','',NULL,NULL,0,2,'ALL'),(8,'Armenian dram','',NULL,NULL,0,2,'AMD'),(9,'Netherlands Antillean guilder','',NULL,NULL,0,2,'ANG'),(10,'Kwanza','',NULL,NULL,0,2,'AOA'),(11,'Argentine peso','',NULL,NULL,0,2,'ARS'),(12,'Australian dollar','',NULL,NULL,0,2,'AUD'),(13,'Aruban guilder','',NULL,NULL,0,2,'AWG'),(14,'Azerbaijanian manat','',NULL,NULL,0,2,'AZN'),(15,'Convertible marks','',NULL,NULL,0,2,'BAM'),(16,'Barbados dollar','',NULL,NULL,0,2,'BBD'),(17,'Bangladeshi taka','',NULL,NULL,0,2,'BDT'),(18,'Bulgarian lev','',NULL,NULL,0,2,'BGN'),(19,'Bahraini dinar','',NULL,NULL,0,3,'BHD'),(20,'Burundian franc','',NULL,NULL,0,0,'BIF'),(21,'Bermudian dollar','',NULL,NULL,0,2,'BMD'),(22,'Brunei dollar','',NULL,NULL,0,2,'BND'),(23,'Boliviano','',NULL,NULL,0,2,'BOB'),(24,'Bolivian Mvdol','',NULL,NULL,0,2,'BOV'),(25,'Brazilian real','',NULL,NULL,0,2,'BRL'),(26,'Bahamian dollar','',NULL,NULL,0,2,'BSD'),(27,'Ngultrum','',NULL,NULL,0,2,'BTN'),(28,'Pula','',NULL,NULL,0,2,'BWP'),(29,'Belarussian ruble','',NULL,NULL,0,0,'BYR'),(30,'Belize dollar','',NULL,NULL,0,2,'BZD'),(31,'Canadian dollar','',NULL,NULL,0,2,'CAD'),(32,'Franc Congolais','',NULL,NULL,0,2,'CDF'),(33,'WIR euro','',NULL,NULL,0,2,'CHE'),(34,'Swiss franc','',NULL,NULL,0,2,'CHF'),(35,'WIR franc','',NULL,NULL,0,2,'CHW'),(36,'Unidad de Fomento','',NULL,NULL,0,0,'CLF'),(37,'Chilean peso','',NULL,NULL,0,0,'CLP'),(38,'Renminbi','',NULL,NULL,0,2,'CNY'),(39,'Colombian peso','',NULL,NULL,0,2,'COP'),(40,'Unidad de Valor Real','',NULL,NULL,0,2,'COU'),(41,'Costa Rican colon','',NULL,NULL,0,2,'CRC'),(42,'Cuban peso','',NULL,NULL,0,2,'CUP'),(43,'Cape Verde escudo','',NULL,NULL,0,2,'CVE'),(44,'Czech koruna','',NULL,NULL,0,2,'CZK'),(45,'Djibouti franc','',NULL,NULL,0,0,'DJF'),(46,'Danish krone','',NULL,NULL,0,2,'DKK'),(47,'Dominican peso','',NULL,NULL,0,2,'DOP'),(48,'Algerian dinar','',NULL,NULL,0,2,'DZD'),(49,'Kroon','',NULL,NULL,0,2,'EEK'),(50,'Egyptian pound','',NULL,NULL,0,2,'EGP'),(51,'Nakfa','',NULL,NULL,0,2,'ERN'),(52,'Ethiopian birr','',NULL,NULL,0,2,'ETB'),(53,'Fiji dollar','',NULL,NULL,0,2,'FJD'),(54,'Falkland Islands pound','',NULL,NULL,0,2,'FKP'),(55,'Lari','',NULL,NULL,0,2,'GEL'),(56,'Cedi','',NULL,NULL,0,2,'GHS'),(57,'Gibraltar pound','',NULL,NULL,0,2,'GIP'),(58,'Dalasi','',NULL,NULL,0,2,'GMD'),(59,'Guinea franc','',NULL,NULL,0,0,'GNF'),(60,'Quetzal','',NULL,NULL,0,2,'GTQ'),(61,'Guyana dollar','',NULL,NULL,0,2,'GYD'),(62,'Hong Kong dollar','',NULL,NULL,0,2,'HKD'),(63,'Lempira','',NULL,NULL,0,2,'HNL'),(64,'Croatian kuna','',NULL,NULL,0,2,'HRK'),(65,'Haiti gourde','',NULL,NULL,0,2,'HTG'),(66,'Forint','',NULL,NULL,0,2,'HUF'),(67,'Rupiah','',NULL,NULL,0,2,'IDR'),(68,'Israeli new sheqel','',NULL,NULL,0,2,'ILS'),(69,'Iraqi dinar','',NULL,NULL,0,3,'IQD'),(70,'Iranian rial','',NULL,NULL,0,2,'IRR'),(71,'Iceland krona','',NULL,NULL,0,0,'ISK'),(72,'Jamaican dollar','',NULL,NULL,0,2,'JMD'),(73,'Jordanian dinar','',NULL,NULL,0,3,'JOD'),(74,'Japanese yen','',NULL,NULL,0,0,'JPY'),(75,'Kenyan shilling','',NULL,NULL,0,2,'KES'),(76,'Som','',NULL,NULL,0,2,'KGS'),(77,'Riel','',NULL,NULL,0,2,'KHR'),(78,'Comoro franc','',NULL,NULL,0,0,'KMF'),(79,'North Korean won','',NULL,NULL,0,2,'KPW'),(80,'South Korean won','',NULL,NULL,0,0,'KRW'),(81,'Kuwaiti dinar','',NULL,NULL,0,3,'KWD'),(82,'Cayman Islands dollar','',NULL,NULL,0,2,'KYD'),(83,'Tenge','',NULL,NULL,0,2,'KZT'),(84,'Kip','',NULL,NULL,0,2,'LAK'),(85,'Lebanese pound','',NULL,NULL,0,2,'LBP'),(86,'Sri Lanka rupee','',NULL,NULL,0,2,'LKR'),(87,'Liberian dollar','',NULL,NULL,0,2,'LRD'),(88,'Loti','',NULL,NULL,0,2,'LSL'),(89,'Lithuanian litas','',NULL,NULL,0,2,'LTL'),(90,'Latvian lats','',NULL,NULL,0,2,'LVL'),(91,'Libyan dinar','',NULL,NULL,0,3,'LYD'),(92,'Moroccan dirham','',NULL,NULL,0,2,'MAD'),(93,'Moldovan leu','',NULL,NULL,0,2,'MDL'),(94,'Malagasy ariary','',NULL,NULL,0,0,'MGA'),(95,'Denar','',NULL,NULL,0,2,'MKD'),(96,'Kyat','',NULL,NULL,0,2,'MMK'),(97,'Tugrik','',NULL,NULL,0,2,'MNT'),(98,'Pataca','',NULL,NULL,0,2,'MOP'),(99,'Ouguiya','',NULL,NULL,0,0,'MRO'),(100,'Mauritius rupee','',NULL,NULL,0,2,'MUR'),(101,'Rufiyaa','',NULL,NULL,0,2,'MVR'),(102,'Kwacha','',NULL,NULL,0,2,'MWK'),(103,'Mexican peso','',NULL,NULL,0,2,'MXN'),(104,'Mexican Unidad de Inversion','',NULL,NULL,0,2,'MXV'),(105,'Malaysian ringgit','',NULL,NULL,0,2,'MYR'),(106,'Metical','',NULL,NULL,0,2,'MZN'),(107,'Namibian dollar','',NULL,NULL,0,2,'NAD'),(108,'Naira','',NULL,NULL,0,2,'NGN'),(109,'Cordoba oro','',NULL,NULL,0,2,'NIO'),(110,'Norwegian krone','',NULL,NULL,0,2,'NOK'),(111,'Nepalese rupee','',NULL,NULL,0,2,'NPR'),(112,'New Zealand dollar','',NULL,NULL,0,2,'NZD'),(113,'Rial Omani','',NULL,NULL,0,3,'OMR'),(114,'Balboa','',NULL,NULL,0,2,'PAB'),(115,'Nuevo sol','',NULL,NULL,0,2,'PEN'),(116,'Kina','',NULL,NULL,0,2,'PGK'),(117,'Philippine peso','',NULL,NULL,0,2,'PHP'),(118,'Pakistan rupee','',NULL,NULL,0,2,'PKR'),(119,'Zloty','',NULL,NULL,0,2,'PLN'),(120,'Guarani','',NULL,NULL,0,0,'PYG'),(121,'Qatari rial','',NULL,NULL,0,2,'QAR'),(122,'Romanian new leu','',NULL,NULL,0,2,'RON'),(123,'Serbian dinar','',NULL,NULL,0,2,'RSD'),(124,'Russian rouble','',NULL,NULL,0,2,'RUB'),(125,'Rwanda franc','',NULL,NULL,0,0,'RWF'),(126,'Saudi riyal','',NULL,NULL,0,2,'SAR'),(127,'Solomon Islands dollar','',NULL,NULL,0,2,'SBD'),(128,'Seychelles rupee','',NULL,NULL,0,2,'SCR'),(129,'Sudanese pound','',NULL,NULL,0,2,'SDG'),(130,'Swedish krona','',NULL,NULL,0,2,'SEK'),(131,'Singapore dollar','',NULL,NULL,0,2,'SGD'),(132,'Saint Helena pound','',NULL,NULL,0,2,'SHP'),(133,'Slovak koruna','',NULL,NULL,0,2,'SKK'),(134,'Leone','',NULL,NULL,0,2,'SLL'),(135,'Somali shilling','',NULL,NULL,0,2,'SOS'),(136,'Surinam dollar','',NULL,NULL,0,2,'SRD'),(137,'Dobra','',NULL,NULL,0,2,'STD'),(138,'Syrian pound','',NULL,NULL,0,2,'SYP'),(139,'Lilangeni','',NULL,NULL,0,2,'SZL'),(140,'Baht','',NULL,NULL,0,2,'THB'),(141,'Somoni','',NULL,NULL,0,2,'TJS'),(142,'Manat','',NULL,NULL,0,2,'TMM'),(143,'Tunisian dinar','',NULL,NULL,0,3,'TND'),(144,'Pa\'anga','',NULL,NULL,0,2,'TOP'),(145,'New Turkish lira','',NULL,NULL,0,2,'TRY'),(146,'Trinidad and Tobago dollar','',NULL,NULL,0,2,'TTD'),(147,'New Taiwan dollar','',NULL,NULL,0,2,'TWD'),(148,'Tanzanian shilling','',NULL,NULL,0,2,'TZS'),(149,'Hryvnia','',NULL,NULL,0,2,'UAH'),(150,'Uganda shilling','',NULL,NULL,0,2,'UGX'),(151,'US dollar (next day)','',NULL,NULL,0,2,'USN'),(152,'US dollar (same day)','',NULL,NULL,0,2,'USS'),(153,'Peso Uruguayo','',NULL,NULL,0,2,'UYU'),(154,'Uzbekistan som','',NULL,NULL,0,2,'UZS'),(155,'Venezuelan Bolivares Fuertes','',NULL,NULL,0,2,'VEF'),(156,'Vietnamese Dong','',NULL,NULL,0,2,'VND'),(157,'Vatu','',NULL,NULL,0,0,'VUV'),(158,'Samoan tala','',NULL,NULL,0,2,'WST'),(159,'CFA franc BEAC','',NULL,NULL,0,0,'XAF'),(160,'Silver','',NULL,NULL,0,0,'XAG'),(161,'Gold','',NULL,NULL,0,0,'XAU'),(162,'European Composite Unit','',NULL,NULL,0,0,'XBA'),(163,'European Monetary Unit','',NULL,NULL,0,0,'XBB'),(164,'European Unit of Account 9','',NULL,NULL,0,0,'XBC'),(165,'European Unit of Account 17','',NULL,NULL,0,0,'XBD'),(166,'East Caribbean dollar','',NULL,NULL,0,2,'XCD'),(167,'Special Drawing Rights','',NULL,NULL,0,0,'XDR'),(168,'UIC franc','',NULL,NULL,0,0,'XFU'),(169,'CFA Franc BCEAO','',NULL,NULL,0,0,'XOF'),(170,'Palladium','',NULL,NULL,0,0,'XPD'),(171,'CFP franc','',NULL,NULL,0,0,'XPF'),(172,'Platinum','',NULL,NULL,0,0,'XPT'),(173,'Code reserved for testing purposes','',NULL,NULL,0,0,'XTS'),(174,'No currency','',NULL,NULL,0,0,'XXX'),(175,'Yemeni rial','',NULL,NULL,0,2,'YER'),(176,'South African rand','',NULL,NULL,0,2,'ZAR'),(177,'Kwacha','',NULL,NULL,0,2,'ZMK'),(178,'Zimbabwe dollar','',NULL,NULL,0,2,'ZWD');
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cust_perf_history`
--

DROP TABLE IF EXISTS `cust_perf_history`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cust_perf_history` (
  `CUSTOMER_ID` int(11) NOT NULL,
  `LOAN_CYCLE_COUNTER` smallint(6) default NULL,
  `LAST_LOAN_AMNT` decimal(10,3) default NULL,
  `ACTIVE_LOANS_COUNT` smallint(6) default NULL,
  `TOTAL_SAVINGS_AMNT` decimal(10,3) default NULL,
  `DELINQUINT_PORTFOLIO` decimal(10,3) default NULL,
  PRIMARY KEY  (`CUSTOMER_ID`),
  CONSTRAINT `cust_perf_history_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cust_perf_history`
--

LOCK TABLES `cust_perf_history` WRITE;
/*!40000 ALTER TABLE `cust_perf_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `cust_perf_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_field_definition`
--

DROP TABLE IF EXISTS `custom_field_definition`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `custom_field_definition` (
  `FIELD_ID` smallint(6) NOT NULL auto_increment,
  `ENTITY_ID` smallint(6) NOT NULL,
  `LEVEL_ID` smallint(6) default NULL,
  `FIELD_TYPE` smallint(6) default NULL,
  `ENTITY_TYPE` smallint(6) NOT NULL,
  `MANDATORY_FLAG` smallint(6) NOT NULL,
  `DEFAULT_VALUE` varchar(200) default NULL,
  PRIMARY KEY  (`FIELD_ID`),
  KEY `LEVEL_ID` (`LEVEL_ID`),
  KEY `ENTITY_ID` (`ENTITY_ID`),
  KEY `ENTITY_TYPE` (`ENTITY_TYPE`),
  CONSTRAINT `custom_field_definition_ibfk_1` FOREIGN KEY (`LEVEL_ID`) REFERENCES `customer_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `custom_field_definition_ibfk_2` FOREIGN KEY (`ENTITY_ID`) REFERENCES `lookup_entity` (`ENTITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `custom_field_definition_ibfk_3` FOREIGN KEY (`ENTITY_TYPE`) REFERENCES `entity_master` (`ENTITY_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `custom_field_definition`
--

LOCK TABLES `custom_field_definition` WRITE;
/*!40000 ALTER TABLE `custom_field_definition` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_field_definition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer` (
  `CUSTOMER_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_LEVEL_ID` smallint(6) NOT NULL,
  `GLOBAL_CUST_NUM` varchar(100) default NULL,
  `LOAN_OFFICER_ID` smallint(6) default NULL,
  `CUSTOMER_FORMEDBY_ID` smallint(6) default NULL,
  `STATUS_ID` smallint(6) default NULL,
  `BRANCH_ID` smallint(6) default NULL,
  `DISPLAY_NAME` varchar(200) default NULL,
  `FIRST_NAME` varchar(200) default NULL,
  `LAST_NAME` varchar(200) default NULL,
  `SECOND_LAST_NAME` varchar(200) default NULL,
  `DISPLAY_ADDRESS` varchar(500) default NULL,
  `EXTERNAL_ID` varchar(50) default NULL,
  `DATE_OF_BIRTH` date default NULL,
  `GROUP_FLAG` smallint(6) default NULL,
  `TRAINED` smallint(6) default NULL,
  `TRAINED_DATE` date default NULL,
  `PARENT_CUSTOMER_ID` int(11) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_DATE` date default NULL,
  `SEARCH_ID` varchar(100) default NULL,
  `MAX_CHILD_COUNT` int(11) default NULL,
  `HO_UPDATED` smallint(6) default NULL,
  `CLIENT_CONFIDENTIAL` smallint(6) default NULL,
  `MFI_JOINING_DATE` date default NULL,
  `GOVERNMENT_ID` varchar(50) default NULL,
  `CUSTOMER_ACTIVATION_DATE` date default NULL,
  `CREATED_BY` smallint(6) default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `BLACKLISTED` smallint(6) default NULL,
  `DISCRIMINATOR` varchar(20) default NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`CUSTOMER_ID`),
  UNIQUE KEY `CUST_GLOBAL_IDX` (`GLOBAL_CUST_NUM`),
  KEY `STATUS_ID` (`STATUS_ID`),
  KEY `BRANCH_ID` (`BRANCH_ID`),
  KEY `CUSTOMER_FORMEDBY_ID` (`CUSTOMER_FORMEDBY_ID`),
  KEY `CUST_SEARCH_IDX` (`SEARCH_ID`),
  KEY `CUST_LO_IDX` (`LOAN_OFFICER_ID`,`BRANCH_ID`),
  KEY `CUSTOMER_LO_NAME_IDX` (`LOAN_OFFICER_ID`,`CUSTOMER_LEVEL_ID`,`DISPLAY_NAME`(15),`FIRST_NAME`(15),`LAST_NAME`(15),`SECOND_LAST_NAME`(15)),
  KEY `CUSTOMER_NAME_IDX` (`CUSTOMER_LEVEL_ID`,`FIRST_NAME`(15),`LAST_NAME`(15),`SECOND_LAST_NAME`(15)),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`CUSTOMER_LEVEL_ID`) REFERENCES `customer_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_ibfk_2` FOREIGN KEY (`STATUS_ID`) REFERENCES `customer_state` (`STATUS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_ibfk_3` FOREIGN KEY (`BRANCH_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_ibfk_4` FOREIGN KEY (`LOAN_OFFICER_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_ibfk_5` FOREIGN KEY (`CUSTOMER_FORMEDBY_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,3,'0002-000000001',2,NULL,13,2,'Center1',NULL,NULL,NULL,'','',NULL,NULL,NULL,NULL,NULL,'2008-12-04',NULL,'1.1',1,NULL,NULL,'2008-12-04',NULL,'2008-12-04',1,NULL,0,'CENTER',2),(2,2,'0002-000000002',2,2,9,2,'Group1',NULL,NULL,NULL,'','',NULL,NULL,0,NULL,1,'2008-12-04','2008-12-04','1.1.1',3,NULL,NULL,NULL,NULL,'2008-12-04',1,1,0,'GROUP',5),(3,1,'0002-000000003',2,2,3,2,'Client - Veronica Abisya','Client - Veronica','Abisya','','','','1949-07-03',1,0,NULL,2,'2008-12-04','2008-12-04','1.1.1.1',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(4,1,'0002-000000004',2,2,3,2,'Client - Dauda Mayowa','Client - Dauda','Mayowa','','','','1973-10-14',1,0,NULL,2,'2008-12-04','2008-12-04','1.1.1.2',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(5,1,'0002-000000005',2,2,3,2,'Client - Polly Gikonyo','Client - Polly','Gikonyo','','','','1965-09-12',1,0,NULL,2,'2008-12-04','2008-12-04','1.1.1.3',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(6,3,'0003-000000006',3,NULL,13,3,'Center2',NULL,NULL,NULL,'','',NULL,NULL,NULL,NULL,NULL,'2008-12-04',NULL,'1.1',1,NULL,NULL,'2008-12-04',NULL,'2008-12-04',1,NULL,0,'CENTER',2),(7,2,'0003-000000007',3,3,9,3,'Group2',NULL,NULL,NULL,'','',NULL,NULL,0,NULL,6,'2008-12-04','2008-12-04','1.1.1',4,NULL,NULL,NULL,NULL,'2008-12-04',1,1,0,'GROUP',6),(8,1,'0003-000000008',3,3,3,3,'Client - Teja Kakarla','Client - Teja','Kakarla','','','','1976-07-22',1,0,NULL,7,'2008-12-04','2008-12-04','1.1.1.1',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(9,1,'0003-000000009',3,3,3,3,'Client - Aarif Mawani','Client - Aarif','Mawani','','','','1973-10-17',1,0,NULL,7,'2008-12-04','2008-12-04','1.1.1.2',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(10,1,'0003-000000010',3,3,3,3,'Client - Mutu Juma','Client - Mutu','Juma','','','','1965-09-14',1,0,NULL,7,'2008-12-04','2008-12-04','1.1.1.3',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(11,1,'0003-000000011',3,3,3,3,'Client - Anna Martin','Client - Anna','Martin','','','','1972-12-31',1,0,NULL,7,'2008-12-04','2008-12-04','1.1.1.4',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(12,3,'0004-000000012',4,NULL,13,4,'Center3',NULL,NULL,NULL,'','',NULL,NULL,NULL,NULL,NULL,'2008-12-04',NULL,'1.1',1,NULL,NULL,'2008-12-04',NULL,'2008-12-04',1,NULL,0,'CENTER',2),(13,2,'0004-000000013',4,4,9,4,'Group3',NULL,NULL,NULL,'','',NULL,NULL,0,NULL,12,'2008-12-04','2008-12-04','1.1.1',4,NULL,NULL,NULL,NULL,'2008-12-04',1,1,0,'GROUP',6),(14,1,'0004-000000014',4,4,3,4,'Client - Tesa Mendez','Client - Tesa','Mendez','','','','1976-01-27',1,0,NULL,13,'2008-12-04','2008-12-04','1.1.1.1',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(15,1,'0004-000000015',4,4,3,4,'Client - Megana Martin','Client - Megana','Martin','','','','1973-11-17',1,0,NULL,13,'2008-12-04','2008-12-04','1.1.1.2',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(16,1,'0004-000000016',4,4,3,4,'Client - Reyna Tabilin','Client - Reyna','Tabilin','','','','1961-09-14',1,0,NULL,13,'2008-12-04','2008-12-04','1.1.1.3',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2),(17,1,'0004-000000017',4,4,3,4,'Client - Mary Asanti','Client - Mary','Asanti','','','','1972-12-31',1,0,NULL,13,'2008-12-04','2008-12-04','1.1.1.4',0,NULL,NULL,NULL,'','2008-12-04',1,1,0,'CLIENT',2);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_account`
--

DROP TABLE IF EXISTS `customer_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_account` (
  `ACCOUNT_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_ID`),
  CONSTRAINT `customer_account_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_account`
--

LOCK TABLES `customer_account` WRITE;
/*!40000 ALTER TABLE `customer_account` DISABLE KEYS */;
INSERT INTO `customer_account` VALUES (1),(2),(3),(5),(7),(9),(10),(11),(13),(15),(17),(19),(20),(21),(23),(25),(27);
/*!40000 ALTER TABLE `customer_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_account_activity`
--

DROP TABLE IF EXISTS `customer_account_activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_account_activity` (
  `CUSTOMER_ACCOUNT_ACTIVITY_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `DESCRIPTION` varchar(200) NOT NULL,
  `AMOUNT` decimal(10,3) default NULL,
  `FEE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `CREATED_DATE` date NOT NULL,
  `CREATED_BY` smallint(6) default NULL,
  PRIMARY KEY  (`CUSTOMER_ACCOUNT_ACTIVITY_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `FEE_AMOUNT_CURRENCY_ID` (`FEE_AMOUNT_CURRENCY_ID`),
  KEY `CREATED_BY` (`CREATED_BY`),
  CONSTRAINT `customer_account_activity_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_account_activity_ibfk_2` FOREIGN KEY (`FEE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_account_activity_ibfk_3` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_account_activity`
--

LOCK TABLES `customer_account_activity` WRITE;
/*!40000 ALTER TABLE `customer_account_activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_account_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_address_detail`
--

DROP TABLE IF EXISTS `customer_address_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_address_detail` (
  `CUSTOMER_ADDRESS_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) default NULL,
  `LOCALE_ID` smallint(6) default NULL,
  `ADDRESS_NAME` varchar(100) default NULL,
  `LINE_1` varchar(200) default NULL,
  `LINE_2` varchar(200) default NULL,
  `LINE_3` varchar(200) default NULL,
  `CITY` varchar(100) default NULL,
  `STATE` varchar(100) default NULL,
  `COUNTRY` varchar(100) default NULL,
  `ZIP` varchar(20) default NULL,
  `ADDRESS_STATUS` smallint(6) default NULL,
  `PHONE_NUMBER` varchar(20) default NULL,
  PRIMARY KEY  (`CUSTOMER_ADDRESS_ID`),
  KEY `LOCALE_ID` (`LOCALE_ID`),
  KEY `CUST_ADDRESS_IDX` (`CUSTOMER_ID`),
  CONSTRAINT `customer_address_detail_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_address_detail_ibfk_2` FOREIGN KEY (`LOCALE_ID`) REFERENCES `supported_locale` (`LOCALE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_address_detail`
--

LOCK TABLES `customer_address_detail` WRITE;
/*!40000 ALTER TABLE `customer_address_detail` DISABLE KEYS */;
INSERT INTO `customer_address_detail` VALUES (1,1,NULL,NULL,'','','','','','','',NULL,''),(2,2,NULL,NULL,'','','','','','','',NULL,''),(3,3,NULL,NULL,'','','','','','','',NULL,''),(4,4,NULL,NULL,'','','','','','','',NULL,''),(5,5,NULL,NULL,'','','','','','','',NULL,''),(6,6,NULL,NULL,'','','','','','','',NULL,''),(7,7,NULL,NULL,'','','','','','','',NULL,''),(8,8,NULL,NULL,'','','','','','','',NULL,''),(9,9,NULL,NULL,'','','','','','','',NULL,''),(10,10,NULL,NULL,'','','','','','','',NULL,''),(11,11,NULL,NULL,'','','','','','','',NULL,''),(12,12,NULL,NULL,'','','','','','','',NULL,''),(13,13,NULL,NULL,'','','','','','','',NULL,''),(14,14,NULL,NULL,'','','','','','','',NULL,''),(15,15,NULL,NULL,'','','','','','','',NULL,''),(16,16,NULL,NULL,'','','','','','','',NULL,''),(17,17,NULL,NULL,'','','','','','','',NULL,'');
/*!40000 ALTER TABLE `customer_address_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_attendance`
--

DROP TABLE IF EXISTS `customer_attendance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_attendance` (
  `ID` int(11) NOT NULL auto_increment,
  `MEETING_DATE` date NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `ATTENDANCE` smallint(6) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `customer_attendance_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_attendance`
--

LOCK TABLES `customer_attendance` WRITE;
/*!40000 ALTER TABLE `customer_attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_attendance_types`
--

DROP TABLE IF EXISTS `customer_attendance_types`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_attendance_types` (
  `ATTENDANCE_ID` smallint(6) NOT NULL auto_increment,
  `ATTENDANCE_LOOKUP_ID` int(11) NOT NULL,
  `DESCRIPTION` varchar(50) default NULL,
  PRIMARY KEY  (`ATTENDANCE_ID`),
  KEY `ATTENDANCE_LOOKUP_ID` (`ATTENDANCE_LOOKUP_ID`),
  CONSTRAINT `customer_attendance_types_ibfk_1` FOREIGN KEY (`ATTENDANCE_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_attendance_types`
--

LOCK TABLES `customer_attendance_types` WRITE;
/*!40000 ALTER TABLE `customer_attendance_types` DISABLE KEYS */;
INSERT INTO `customer_attendance_types` VALUES (1,194,'Present'),(2,195,'Absent'),(3,196,'Approved leave'),(4,197,'Late');
/*!40000 ALTER TABLE `customer_attendance_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_checklist`
--

DROP TABLE IF EXISTS `customer_checklist`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_checklist` (
  `CHECKLIST_ID` smallint(6) NOT NULL,
  `LEVEL_ID` smallint(6) NOT NULL,
  `CUSTOMER_STATUS_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`CHECKLIST_ID`),
  KEY `LEVEL_ID` (`LEVEL_ID`),
  KEY `CUSTOMER_STATUS_ID` (`CUSTOMER_STATUS_ID`),
  CONSTRAINT `customer_checklist_ibfk_1` FOREIGN KEY (`CHECKLIST_ID`) REFERENCES `checklist` (`CHECKLIST_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_checklist_ibfk_2` FOREIGN KEY (`LEVEL_ID`) REFERENCES `customer_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_checklist_ibfk_3` FOREIGN KEY (`CUSTOMER_STATUS_ID`) REFERENCES `customer_state` (`STATUS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_checklist`
--

LOCK TABLES `customer_checklist` WRITE;
/*!40000 ALTER TABLE `customer_checklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_checklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_custom_field`
--

DROP TABLE IF EXISTS `customer_custom_field`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_custom_field` (
  `CUSTOMER_CUSTOMFIELD_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `FIELD_ID` smallint(6) NOT NULL,
  `FIELD_VALUE` varchar(200) default NULL,
  PRIMARY KEY  (`CUSTOMER_CUSTOMFIELD_ID`),
  KEY `FIELD_ID` (`FIELD_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `customer_custom_field_ibfk_1` FOREIGN KEY (`FIELD_ID`) REFERENCES `custom_field_definition` (`FIELD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_custom_field_ibfk_2` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_custom_field`
--

LOCK TABLES `customer_custom_field` WRITE;
/*!40000 ALTER TABLE `customer_custom_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_custom_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_detail`
--

DROP TABLE IF EXISTS `customer_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_detail` (
  `CUSTOMER_ID` int(11) NOT NULL,
  `ETHINICITY` int(11) default NULL,
  `CITIZENSHIP` int(11) default NULL,
  `HANDICAPPED` int(11) default NULL,
  `BUSINESS_ACTIVITIES` int(11) default NULL,
  `MARITAL_STATUS` int(11) default NULL,
  `EDUCATION_LEVEL` int(11) default NULL,
  `NUM_CHILDREN` smallint(6) default NULL,
  `GENDER` smallint(6) default NULL,
  `DATE_STARTED` date default NULL,
  `HANDICAPPED_DETAILS` varchar(200) default NULL,
  `POVERTY_STATUS` int(11) default NULL,
  `POVERTY_LHOOD_PCT` decimal(10,3) default NULL,
  PRIMARY KEY  (`CUSTOMER_ID`),
  KEY `CITIZENSHIP` (`CITIZENSHIP`),
  KEY `EDUCATION_LEVEL` (`EDUCATION_LEVEL`),
  KEY `ETHINICITY` (`ETHINICITY`),
  KEY `HANDICAPPED` (`HANDICAPPED`),
  KEY `MARITAL_STATUS` (`MARITAL_STATUS`),
  KEY `POVERTY_STATUS` (`POVERTY_STATUS`),
  CONSTRAINT `customer_detail_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_detail_ibfk_2` FOREIGN KEY (`CITIZENSHIP`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_detail_ibfk_3` FOREIGN KEY (`EDUCATION_LEVEL`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_detail_ibfk_4` FOREIGN KEY (`ETHINICITY`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_detail_ibfk_5` FOREIGN KEY (`HANDICAPPED`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_detail_ibfk_6` FOREIGN KEY (`MARITAL_STATUS`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_detail_ibfk_7` FOREIGN KEY (`POVERTY_STATUS`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_detail`
--

LOCK TABLES `customer_detail` WRITE;
/*!40000 ALTER TABLE `customer_detail` DISABLE KEYS */;
INSERT INTO `customer_detail` VALUES (3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(4,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,42,NULL),(5,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,41,NULL),(8,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(9,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,42,NULL),(10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,41,NULL),(11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(14,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL),(15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,42,NULL),(16,NULL,NULL,NULL,NULL,NULL,NULL,NULL,50,NULL,NULL,41,NULL),(17,NULL,NULL,NULL,NULL,NULL,NULL,NULL,49,NULL,NULL,41,NULL);
/*!40000 ALTER TABLE `customer_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_fee_schedule`
--

DROP TABLE IF EXISTS `customer_fee_schedule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_fee_schedule` (
  `ACCOUNT_FEES_DETAIL_ID` int(11) NOT NULL auto_increment,
  `ID` int(11) NOT NULL,
  `INSTALLMENT_ID` int(11) NOT NULL,
  `FEE_ID` smallint(6) NOT NULL,
  `ACCOUNT_FEE_ID` int(11) NOT NULL,
  `AMOUNT` decimal(10,3) default NULL,
  `AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `AMOUNT_PAID` decimal(10,3) default NULL,
  `AMOUNT_PAID_CURRENCY_ID` smallint(6) default NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_FEES_DETAIL_ID`),
  KEY `ID` (`ID`),
  KEY `AMOUNT_CURRENCY_ID` (`AMOUNT_CURRENCY_ID`),
  KEY `AMOUNT_PAID_CURRENCY_ID` (`AMOUNT_PAID_CURRENCY_ID`),
  KEY `FEE_ID` (`FEE_ID`),
  KEY `ACCOUNT_FEE_ID` (`ACCOUNT_FEE_ID`),
  CONSTRAINT `customer_fee_schedule_ibfk_1` FOREIGN KEY (`ID`) REFERENCES `customer_schedule` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_fee_schedule_ibfk_2` FOREIGN KEY (`AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_fee_schedule_ibfk_3` FOREIGN KEY (`AMOUNT_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_fee_schedule_ibfk_4` FOREIGN KEY (`FEE_ID`) REFERENCES `fees` (`FEE_ID`),
  CONSTRAINT `customer_fee_schedule_ibfk_5` FOREIGN KEY (`ACCOUNT_FEE_ID`) REFERENCES `account_fees` (`ACCOUNT_FEE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_fee_schedule`
--

LOCK TABLES `customer_fee_schedule` WRITE;
/*!40000 ALTER TABLE `customer_fee_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_fee_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_flag_detail`
--

DROP TABLE IF EXISTS `customer_flag_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_flag_detail` (
  `CUSTOMER_FLAG_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `FLAG_ID` smallint(6) NOT NULL,
  `CREATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`CUSTOMER_FLAG_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  KEY `FLAG_ID` (`FLAG_ID`),
  KEY `CREATED_BY` (`CREATED_BY`),
  CONSTRAINT `customer_flag_detail_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_flag_detail_ibfk_2` FOREIGN KEY (`FLAG_ID`) REFERENCES `customer_state_flag` (`FLAG_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_flag_detail_ibfk_3` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_flag_detail`
--

LOCK TABLES `customer_flag_detail` WRITE;
/*!40000 ALTER TABLE `customer_flag_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_flag_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_hierarchy`
--

DROP TABLE IF EXISTS `customer_hierarchy`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_hierarchy` (
  `HIERARCHY_ID` int(11) NOT NULL auto_increment,
  `PARENT_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) default NULL,
  `STATUS` smallint(6) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`HIERARCHY_ID`),
  KEY `PARENT_ID` (`PARENT_ID`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  KEY `CUST_HIERARCHY_IDX` (`CUSTOMER_ID`,`STATUS`),
  CONSTRAINT `customer_hierarchy_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_hierarchy_ibfk_2` FOREIGN KEY (`PARENT_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_hierarchy_ibfk_3` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_hierarchy`
--

LOCK TABLES `customer_hierarchy` WRITE;
/*!40000 ALTER TABLE `customer_hierarchy` DISABLE KEYS */;
INSERT INTO `customer_hierarchy` VALUES (1,1,2,1,'2008-12-04',NULL,NULL,NULL),(2,2,3,1,'2008-12-04',NULL,NULL,NULL),(3,2,4,1,'2008-12-04',NULL,NULL,NULL),(4,2,5,1,'2008-12-04',NULL,NULL,NULL),(5,6,7,1,'2008-12-04',NULL,NULL,NULL),(6,7,8,1,'2008-12-04',NULL,NULL,NULL),(7,7,9,1,'2008-12-04',NULL,NULL,NULL),(8,7,10,1,'2008-12-04',NULL,NULL,NULL),(9,7,11,1,'2008-12-04',NULL,NULL,NULL),(10,12,13,1,'2008-12-04',NULL,NULL,NULL),(11,13,14,1,'2008-12-04',NULL,NULL,NULL),(12,13,15,1,'2008-12-04',NULL,NULL,NULL),(13,13,16,1,'2008-12-04',NULL,NULL,NULL),(14,13,17,1,'2008-12-04',NULL,NULL,NULL);
/*!40000 ALTER TABLE `customer_hierarchy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_historical_data`
--

DROP TABLE IF EXISTS `customer_historical_data`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_historical_data` (
  `HISTORICAL_ID` smallint(6) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `PRODUCT_NAME` varchar(100) default NULL,
  `LOAN_AMOUNT` decimal(10,3) default NULL,
  `LOAN_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `TOTAL_AMOUNT_PAID` decimal(10,3) default NULL,
  `TOTAL_AMOUNT_PAID_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_PAID` decimal(10,3) default NULL,
  `INTEREST_PAID_CURRENCY_ID` smallint(6) default NULL,
  `MISSED_PAYMENTS_COUNT` int(11) default NULL,
  `TOTAL_PAYMENTS_COUNT` int(11) default NULL,
  `NOTES` varchar(500) default NULL,
  `LOAN_CYCLE_NUMBER` int(11) default NULL,
  `CREATED_BY` smallint(6) default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_DATE` date default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`HISTORICAL_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `customer_historical_data_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_historical_data`
--

LOCK TABLES `customer_historical_data` WRITE;
/*!40000 ALTER TABLE `customer_historical_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_historical_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_level`
--

DROP TABLE IF EXISTS `customer_level`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_level` (
  `LEVEL_ID` smallint(6) NOT NULL,
  `PARENT_LEVEL_ID` smallint(6) default NULL,
  `LEVEL_NAME_ID` smallint(6) NOT NULL,
  `INTERACTION_FLAG` smallint(6) default NULL,
  `MAX_CHILD_COUNT` smallint(6) NOT NULL,
  `MAX_INSTANCE_COUNT` smallint(6) NOT NULL,
  PRIMARY KEY  (`LEVEL_ID`),
  KEY `PARENT_LEVEL_ID` (`PARENT_LEVEL_ID`),
  KEY `LEVEL_NAME_ID` (`LEVEL_NAME_ID`),
  CONSTRAINT `customer_level_ibfk_1` FOREIGN KEY (`PARENT_LEVEL_ID`) REFERENCES `customer_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_level_ibfk_2` FOREIGN KEY (`LEVEL_NAME_ID`) REFERENCES `lookup_entity` (`ENTITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_level`
--

LOCK TABLES `customer_level` WRITE;
/*!40000 ALTER TABLE `customer_level` DISABLE KEYS */;
INSERT INTO `customer_level` VALUES (1,2,13,NULL,1,30),(2,3,12,NULL,2,12),(3,NULL,11,NULL,4,10);
/*!40000 ALTER TABLE `customer_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_loan_account_detail`
--

DROP TABLE IF EXISTS `customer_loan_account_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_loan_account_detail` (
  `ACCOUNT_TRXN_ID` int(11) NOT NULL,
  `ACCOUNT_ID` int(11) NOT NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `INSTALLMENT_NUMBER` smallint(6) NOT NULL,
  `DUE_DATE` date NOT NULL,
  `PRINCIPAL` decimal(10,3) NOT NULL,
  `PRINCIPAL_CURRENCY_ID` smallint(6) NOT NULL,
  `INTEREST` decimal(10,3) NOT NULL,
  `INTEREST_CURRENCY_ID` smallint(6) NOT NULL,
  `PENALTY` decimal(10,3) NOT NULL,
  `PENALTY_CURRENCY_ID` smallint(6) NOT NULL,
  KEY `ACCOUNT_TRXN_ID` (`ACCOUNT_TRXN_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `PRINCIPAL_CURRENCY_ID` (`PRINCIPAL_CURRENCY_ID`),
  KEY `INTEREST_CURRENCY_ID` (`INTEREST_CURRENCY_ID`),
  KEY `PENALTY_CURRENCY_ID` (`PENALTY_CURRENCY_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  CONSTRAINT `customer_loan_account_detail_ibfk_1` FOREIGN KEY (`ACCOUNT_TRXN_ID`) REFERENCES `account_trxn` (`ACCOUNT_TRXN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_loan_account_detail_ibfk_2` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_loan_account_detail_ibfk_3` FOREIGN KEY (`PRINCIPAL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_loan_account_detail_ibfk_4` FOREIGN KEY (`INTEREST_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_loan_account_detail_ibfk_5` FOREIGN KEY (`PENALTY_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_loan_account_detail_ibfk_6` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `loan_account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_loan_account_detail`
--

LOCK TABLES `customer_loan_account_detail` WRITE;
/*!40000 ALTER TABLE `customer_loan_account_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_loan_account_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_meeting`
--

DROP TABLE IF EXISTS `customer_meeting`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_meeting` (
  `CUSTOMER_MEETING_ID` int(11) NOT NULL auto_increment,
  `MEETING_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `UPDATED_FLAG` smallint(6) NOT NULL,
  `UPDATED_MEETING_ID` int(11) default NULL,
  PRIMARY KEY  (`CUSTOMER_MEETING_ID`),
  KEY `MEETING_ID` (`MEETING_ID`),
  KEY `UPDATED_MEETING_ID` (`UPDATED_MEETING_ID`),
  KEY `CUSTOMER_MEETING_IDX` (`CUSTOMER_ID`),
  KEY `CUST_INHERITED_MEETING_IDX` (`CUSTOMER_ID`),
  CONSTRAINT `customer_meeting_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_meeting_ibfk_2` FOREIGN KEY (`MEETING_ID`) REFERENCES `meeting` (`MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_meeting_ibfk_3` FOREIGN KEY (`UPDATED_MEETING_ID`) REFERENCES `meeting` (`MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_meeting`
--

LOCK TABLES `customer_meeting` WRITE;
/*!40000 ALTER TABLE `customer_meeting` DISABLE KEYS */;
INSERT INTO `customer_meeting` VALUES (1,2,1,0,NULL),(2,2,2,0,NULL),(3,2,3,0,NULL),(4,2,4,0,NULL),(5,2,5,0,NULL),(6,7,6,0,NULL),(7,7,7,0,NULL),(8,7,8,0,NULL),(9,7,9,0,NULL),(10,7,10,0,NULL),(11,7,11,0,NULL),(12,13,12,0,NULL),(13,13,13,0,NULL),(14,13,14,0,NULL),(15,13,15,0,NULL),(16,13,16,0,NULL),(17,13,17,0,NULL);
/*!40000 ALTER TABLE `customer_meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_meeting_detail`
--

DROP TABLE IF EXISTS `customer_meeting_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_meeting_detail` (
  `MEETING_ID` int(11) NOT NULL,
  `DETAILS_ID` int(11) NOT NULL,
  PRIMARY KEY  (`MEETING_ID`,`DETAILS_ID`),
  KEY `DETAILS_ID` (`DETAILS_ID`),
  CONSTRAINT `customer_meeting_detail_ibfk_1` FOREIGN KEY (`MEETING_ID`) REFERENCES `customer_meeting` (`CUSTOMER_MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_meeting_detail_ibfk_2` FOREIGN KEY (`DETAILS_ID`) REFERENCES `recurrence_detail` (`DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_meeting_detail`
--

LOCK TABLES `customer_meeting_detail` WRITE;
/*!40000 ALTER TABLE `customer_meeting_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_meeting_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_movement`
--

DROP TABLE IF EXISTS `customer_movement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_movement` (
  `CUSTOMER_MOVEMENT_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) default NULL,
  `PERSONNEL_ID` smallint(6) default NULL,
  `OFFICE_ID` smallint(6) NOT NULL,
  `STATUS` smallint(6) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`CUSTOMER_MOVEMENT_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  KEY `PERSONNEL_ID` (`PERSONNEL_ID`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  KEY `CUST_MOVEMENT_IDX` (`CUSTOMER_ID`,`STATUS`),
  CONSTRAINT `customer_movement_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_movement_ibfk_2` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_movement_ibfk_3` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_movement_ibfk_4` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_movement`
--

LOCK TABLES `customer_movement` WRITE;
/*!40000 ALTER TABLE `customer_movement` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_movement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_name_detail`
--

DROP TABLE IF EXISTS `customer_name_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_name_detail` (
  `CUSTOMER_NAME_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) default NULL,
  `NAME_TYPE` smallint(6) default NULL,
  `LOCALE_ID` smallint(6) default NULL,
  `SALUTATION` int(11) default NULL,
  `FIRST_NAME` varchar(100) NOT NULL,
  `MIDDLE_NAME` varchar(100) default NULL,
  `LAST_NAME` varchar(100) NOT NULL,
  `SECOND_LAST_NAME` varchar(100) default NULL,
  `SECOND_MIDDLE_NAME` varchar(100) default NULL,
  `DISPLAY_NAME` varchar(200) default NULL,
  PRIMARY KEY  (`CUSTOMER_NAME_ID`),
  KEY `SALUTATION` (`SALUTATION`),
  KEY `LOCALE_ID` (`LOCALE_ID`),
  KEY `CUST_NAME_IDX` (`CUSTOMER_ID`),
  CONSTRAINT `customer_name_detail_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_name_detail_ibfk_2` FOREIGN KEY (`SALUTATION`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_name_detail_ibfk_3` FOREIGN KEY (`LOCALE_ID`) REFERENCES `supported_locale` (`LOCALE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_name_detail`
--

LOCK TABLES `customer_name_detail` WRITE;
/*!40000 ALTER TABLE `customer_name_detail` DISABLE KEYS */;
INSERT INTO `customer_name_detail` VALUES (1,3,1,NULL,NULL,'Sis1228423328625','','Last1228423328625','',NULL,'Sis1228423328625 Last1228423328625'),(2,3,3,NULL,613,'Client - Veronica','','Abisya','',NULL,'Client - Veronica Abisya'),(3,4,3,NULL,613,'Client - Dauda','','Mayowa','',NULL,'Client - Dauda Mayowa'),(4,4,2,NULL,NULL,'J','','Mayowa','',NULL,'J Mayowa'),(5,5,3,NULL,613,'Client - Polly','','Gikonyo','',NULL,'Client - Polly Gikonyo'),(6,5,1,NULL,NULL,'A','','Gikonyo','',NULL,'A Gikonyo'),(7,8,1,NULL,NULL,'Sis1228423740015','','Last1228423740015','',NULL,'Sis1228423740015 Last1228423740015'),(8,8,3,NULL,613,'Client - Teja','','Kakarla','',NULL,'Client - Teja Kakarla'),(9,9,2,NULL,NULL,'J','','Mayowa','',NULL,'J Mayowa'),(10,9,3,NULL,613,'Client - Aarif','','Mawani','',NULL,'Client - Aarif Mawani'),(11,10,1,NULL,NULL,'A','','Juma','',NULL,'A Juma'),(12,10,3,NULL,613,'Client - Mutu','','Juma','',NULL,'Client - Mutu Juma'),(13,11,1,NULL,NULL,'A','','Martin','',NULL,'A Martin'),(14,11,3,NULL,613,'Client - Anna','','Martin','',NULL,'Client - Anna Martin'),(15,14,1,NULL,NULL,'Sis1228424295906','','Last1228424295906','',NULL,'Sis1228424295906 Last1228424295906'),(16,14,3,NULL,613,'Client - Tesa','','Mendez','',NULL,'Client - Tesa Mendez'),(17,15,3,NULL,613,'Client - Megana','','Martin','',NULL,'Client - Megana Martin'),(18,15,2,NULL,NULL,'J','','Martin','',NULL,'J Martin'),(19,16,1,NULL,NULL,'A','','Tabilin','',NULL,'A Tabilin'),(20,16,3,NULL,613,'Client - Reyna','','Tabilin','',NULL,'Client - Reyna Tabilin'),(21,17,3,NULL,613,'Client - Mary','','Asanti','',NULL,'Client - Mary Asanti'),(22,17,1,NULL,NULL,'A','','Asanti','',NULL,'A Asanti');
/*!40000 ALTER TABLE `customer_name_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_note`
--

DROP TABLE IF EXISTS `customer_note`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_note` (
  `COMMENT_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `FIELD_OFFICER_ID` smallint(6) NOT NULL,
  `COMMENT_DATE` date NOT NULL,
  `COMMENT` varchar(500) NOT NULL,
  PRIMARY KEY  (`COMMENT_ID`),
  KEY `FIELD_OFFICER_ID` (`FIELD_OFFICER_ID`),
  KEY `CUST_NOTE_IDX` (`CUSTOMER_ID`),
  CONSTRAINT `customer_note_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_note_ibfk_2` FOREIGN KEY (`FIELD_OFFICER_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_note`
--

LOCK TABLES `customer_note` WRITE;
/*!40000 ALTER TABLE `customer_note` DISABLE KEYS */;
INSERT INTO `customer_note` VALUES (1,2,1,'2008-12-04','good to go.'),(2,3,1,'2008-12-04','ready and set.'),(3,4,1,'2008-12-04','active'),(4,5,1,'2008-12-04','active'),(5,7,1,'2008-12-04','good to go.'),(6,8,1,'2008-12-04','ready and set.'),(7,9,1,'2008-12-04','active'),(8,10,1,'2008-12-04','active'),(9,11,1,'2008-12-04','note'),(10,13,1,'2008-12-04','good to go.'),(11,14,1,'2008-12-04','ready and set.'),(12,15,1,'2008-12-04','active'),(13,16,1,'2008-12-04','active'),(14,17,1,'2008-12-04','active');
/*!40000 ALTER TABLE `customer_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_picture`
--

DROP TABLE IF EXISTS `customer_picture`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_picture` (
  `PICTURE_ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `PICTURE` blob,
  PRIMARY KEY  (`PICTURE_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `customer_picture_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_picture`
--

LOCK TABLES `customer_picture` WRITE;
/*!40000 ALTER TABLE `customer_picture` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_picture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_position`
--

DROP TABLE IF EXISTS `customer_position`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_position` (
  `CUSTOMER_POSITION_ID` smallint(6) NOT NULL auto_increment,
  `POSITION_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) default NULL,
  `PARENT_CUSTOMER_ID` int(11) default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`CUSTOMER_POSITION_ID`),
  UNIQUE KEY `CUST_POSITION_IDX` (`CUSTOMER_ID`,`POSITION_ID`),
  CONSTRAINT `customer_position_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_position`
--

LOCK TABLES `customer_position` WRITE;
/*!40000 ALTER TABLE `customer_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_program`
--

DROP TABLE IF EXISTS `customer_program`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_program` (
  `PROGRAM_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`PROGRAM_ID`,`CUSTOMER_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `customer_program_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_program_ibfk_2` FOREIGN KEY (`PROGRAM_ID`) REFERENCES `program` (`PROGRAM_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_program`
--

LOCK TABLES `customer_program` WRITE;
/*!40000 ALTER TABLE `customer_program` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_program` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_schedule`
--

DROP TABLE IF EXISTS `customer_schedule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_schedule` (
  `ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `ACTION_DATE` date default NULL,
  `MISC_FEES` decimal(10,3) default NULL,
  `MISC_FEES_CURRENCY_ID` smallint(6) default NULL,
  `MISC_FEES_PAID` decimal(10,3) default NULL,
  `MISC_FEES_PAID_CURRENCY_ID` smallint(6) default NULL,
  `MISC_PENALTY` decimal(10,3) default NULL,
  `MISC_PENALTY_CURRENCY_ID` smallint(6) default NULL,
  `MISC_PENALTY_PAID` decimal(10,3) default NULL,
  `MISC_PENALTY_PAID_CURRENCY_ID` smallint(6) default NULL,
  `PAYMENT_STATUS` smallint(6) NOT NULL,
  `INSTALLMENT_ID` smallint(6) NOT NULL,
  `PAYMENT_DATE` date default NULL,
  `PARENT_FLAG` smallint(6) default NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `CUSTOMER_SCHEDULE_ACTION_DATE_IDX` (`CUSTOMER_ID`,`ACTION_DATE`,`PAYMENT_STATUS`),
  CONSTRAINT `customer_schedule_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_schedule_ibfk_2` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_schedule_ibfk_3` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_schedule`
--

LOCK TABLES `customer_schedule` WRITE;
/*!40000 ALTER TABLE `customer_schedule` DISABLE KEYS */;
INSERT INTO `customer_schedule` VALUES (1,1,1,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(2,1,1,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(3,1,1,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(4,1,1,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(5,1,1,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(6,1,1,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(7,1,1,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(8,1,1,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(9,1,1,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(10,1,1,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(11,2,2,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(12,2,2,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(13,2,2,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(14,2,2,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(15,2,2,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(16,2,2,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(17,2,2,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(18,2,2,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(19,2,2,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(20,2,2,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(21,3,3,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(22,3,3,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(23,3,3,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(24,3,3,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(25,3,3,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(26,3,3,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(27,3,3,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(28,3,3,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(29,3,3,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(30,3,3,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(31,5,4,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(32,5,4,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(33,5,4,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(34,5,4,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(35,5,4,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(36,5,4,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(37,5,4,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(38,5,4,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(39,5,4,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(40,5,4,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(41,7,5,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(42,7,5,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(43,7,5,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(44,7,5,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(45,7,5,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(46,7,5,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(47,7,5,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(48,7,5,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(49,7,5,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(50,7,5,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(51,9,6,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(52,9,6,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(53,9,6,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(54,9,6,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(55,9,6,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(56,9,6,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(57,9,6,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(58,9,6,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(59,9,6,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(60,9,6,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(61,10,7,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(62,10,7,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(63,10,7,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(64,10,7,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(65,10,7,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(66,10,7,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(67,10,7,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(68,10,7,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(69,10,7,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(70,10,7,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(71,11,8,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(72,11,8,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(73,11,8,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(74,11,8,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(75,11,8,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(76,11,8,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(77,11,8,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(78,11,8,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(79,11,8,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(80,11,8,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(81,13,9,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(82,13,9,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(83,13,9,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(84,13,9,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(85,13,9,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(86,13,9,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(87,13,9,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(88,13,9,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(89,13,9,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(90,13,9,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(91,15,10,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(92,15,10,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(93,15,10,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(94,15,10,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(95,15,10,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(96,15,10,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(97,15,10,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(98,15,10,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(99,15,10,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(100,15,10,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(101,17,11,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(102,17,11,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(103,17,11,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(104,17,11,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(105,17,11,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(106,17,11,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(107,17,11,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(108,17,11,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(109,17,11,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(110,17,11,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(111,19,12,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(112,19,12,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(113,19,12,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(114,19,12,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(115,19,12,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(116,19,12,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(117,19,12,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(118,19,12,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(119,19,12,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(120,19,12,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(121,20,13,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(122,20,13,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(123,20,13,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(124,20,13,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(125,20,13,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(126,20,13,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(127,20,13,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(128,20,13,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(129,20,13,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(130,20,13,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(131,21,14,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(132,21,14,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(133,21,14,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(134,21,14,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(135,21,14,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(136,21,14,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(137,21,14,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(138,21,14,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(139,21,14,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(140,21,14,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(141,23,15,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(142,23,15,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(143,23,15,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(144,23,15,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(145,23,15,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(146,23,15,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(147,23,15,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(148,23,15,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(149,23,15,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(150,23,15,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(151,25,16,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(152,25,16,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(153,25,16,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(154,25,16,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(155,25,16,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(156,25,16,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(157,25,16,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(158,25,16,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(159,25,16,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(160,25,16,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(161,27,17,NULL,'2008-12-04','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(162,27,17,NULL,'2008-12-11','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(163,27,17,NULL,'2008-12-18','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(164,27,17,NULL,'2008-12-25','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(165,27,17,NULL,'2009-01-01','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(166,27,17,NULL,'2009-01-08','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(167,27,17,NULL,'2009-01-15','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(168,27,17,NULL,'2009-01-22','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(169,27,17,NULL,'2009-01-29','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(170,27,17,NULL,'2009-02-05','0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0);
/*!40000 ALTER TABLE `customer_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_state`
--

DROP TABLE IF EXISTS `customer_state`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_state` (
  `STATUS_ID` smallint(6) NOT NULL auto_increment,
  `STATUS_LOOKUP_ID` int(11) NOT NULL,
  `LEVEL_ID` smallint(6) NOT NULL,
  `DESCRIPTION` varchar(200) default NULL,
  `CURRENTLY_IN_USE` smallint(6) NOT NULL,
  PRIMARY KEY  (`STATUS_ID`),
  KEY `LEVEL_ID` (`LEVEL_ID`),
  KEY `STATUS_LOOKUP_ID` (`STATUS_LOOKUP_ID`),
  CONSTRAINT `customer_state_ibfk_1` FOREIGN KEY (`LEVEL_ID`) REFERENCES `customer_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_state_ibfk_2` FOREIGN KEY (`STATUS_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_state`
--

LOCK TABLES `customer_state` WRITE;
/*!40000 ALTER TABLE `customer_state` DISABLE KEYS */;
INSERT INTO `customer_state` VALUES (1,1,1,'Customer Was Partial',1),(2,2,1,'Customer Was Pending',1),(3,3,1,'Customer Was Active',1),(4,4,1,'Customer Was Hold',1),(5,5,1,'Customer Was Cancel',1),(6,6,1,'Customer Was Close',1),(7,7,2,'Customer Was Partial',1),(8,8,2,'Customer Was Pending',1),(9,9,2,'Customer Was Active',1),(10,10,2,'Customer Was Hold',1),(11,11,2,'Customer Was Cancel',1),(12,12,2,'Customer Was Close',1),(13,13,3,'Customer Was Active',1),(14,14,3,'Customer Was Inactive',1);
/*!40000 ALTER TABLE `customer_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_state_flag`
--

DROP TABLE IF EXISTS `customer_state_flag`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_state_flag` (
  `FLAG_ID` smallint(6) NOT NULL,
  `FLAG_LOOKUP_ID` int(11) NOT NULL,
  `STATUS_ID` smallint(6) NOT NULL,
  `FLAG_DESCRIPTION` varchar(200) NOT NULL,
  `ISBLACKLISTED` smallint(6) default NULL,
  PRIMARY KEY  (`FLAG_ID`),
  KEY `STATUS_ID` (`STATUS_ID`),
  KEY `FLAG_LOOKUP_ID` (`FLAG_LOOKUP_ID`),
  CONSTRAINT `customer_state_flag_ibfk_1` FOREIGN KEY (`STATUS_ID`) REFERENCES `customer_state` (`STATUS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_state_flag_ibfk_2` FOREIGN KEY (`FLAG_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_state_flag`
--

LOCK TABLES `customer_state_flag` WRITE;
/*!40000 ALTER TABLE `customer_state_flag` DISABLE KEYS */;
INSERT INTO `customer_state_flag` VALUES (1,28,5,'Withdraw',0),(2,29,5,'Rejected',0),(3,30,5,'Blacklisted',1),(4,31,5,'Duplicate',0),(5,34,5,'Other',0),(6,32,6,'Transferred',0),(7,31,6,'Duplicate',0),(8,30,6,'Blacklisted',1),(9,33,6,'Left program',0),(10,34,6,'Other',0),(11,28,11,'Withdraw',0),(12,29,11,'Rejected',0),(13,30,11,'Blacklisted',1),(14,31,11,'Duplicate',0),(15,34,11,'Other',0),(16,32,12,'Transferred',0),(17,31,12,'Duplicate',0),(18,30,12,'Blacklisted',1),(19,33,12,'Left program',0),(20,34,12,'Other',0);
/*!40000 ALTER TABLE `customer_state_flag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_trxn_detail`
--

DROP TABLE IF EXISTS `customer_trxn_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customer_trxn_detail` (
  `ACCOUNT_TRXN_ID` int(11) NOT NULL,
  `TOTAL_AMOUNT` decimal(10,3) default NULL,
  `TOTAL_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `MISC_FEE_AMOUNT` decimal(10,3) default NULL,
  `MISC_FEE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `MISC_PENALTY_AMOUNT` decimal(10,3) default NULL,
  `MISC_PENALTY_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ACCOUNT_TRXN_ID`),
  KEY `TOTAL_AMOUNT_CURRENCY_ID` (`TOTAL_AMOUNT_CURRENCY_ID`),
  KEY `MISC_PENALTY_AMOUNT_CURRENCY_ID` (`MISC_PENALTY_AMOUNT_CURRENCY_ID`),
  KEY `MISC_FEE_AMOUNT_CURRENCY_ID` (`MISC_FEE_AMOUNT_CURRENCY_ID`),
  CONSTRAINT `customer_trxn_detail_ibfk_1` FOREIGN KEY (`TOTAL_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_trxn_detail_ibfk_2` FOREIGN KEY (`MISC_PENALTY_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_trxn_detail_ibfk_3` FOREIGN KEY (`MISC_FEE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `customer_trxn_detail_ibfk_4` FOREIGN KEY (`ACCOUNT_TRXN_ID`) REFERENCES `account_trxn` (`ACCOUNT_TRXN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `customer_trxn_detail`
--

LOCK TABLES `customer_trxn_detail` WRITE;
/*!40000 ALTER TABLE `customer_trxn_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_trxn_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `database_version`
--

DROP TABLE IF EXISTS `database_version`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `database_version` (
  `DATABASE_VERSION` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `database_version`
--

LOCK TABLES `database_version` WRITE;
/*!40000 ALTER TABLE `database_version` DISABLE KEYS */;
INSERT INTO `database_version` VALUES (207);
/*!40000 ALTER TABLE `database_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_master`
--

DROP TABLE IF EXISTS `entity_master`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `entity_master` (
  `ENTITY_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `ENTITY_TYPE` varchar(100) NOT NULL,
  PRIMARY KEY  (`ENTITY_TYPE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `entity_master`
--

LOCK TABLES `entity_master` WRITE;
/*!40000 ALTER TABLE `entity_master` DISABLE KEYS */;
INSERT INTO `entity_master` VALUES (1,'Client'),(2,'LoanProduct'),(3,'SavingsProduct'),(4,'ProductCategory'),(5,'ProductConfiguration'),(6,'Fees'),(7,'Accounts'),(8,'Admin'),(9,'Checklist'),(10,'Configuration'),(11,'Customer'),(12,'Group'),(13,'Login'),(14,'Meeting'),(15,'Office'),(16,'Penalty'),(17,'Personnel'),(19,'Roleandpermission'),(20,'Center'),(21,'Savings'),(22,'Loan'),(23,'BulkEntry');
/*!40000 ALTER TABLE `entity_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_formula_master`
--

DROP TABLE IF EXISTS `fee_formula_master`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_formula_master` (
  `FORMULAID` smallint(6) NOT NULL auto_increment,
  `FORUMLA_LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`FORMULAID`),
  KEY `FORUMLA_LOOKUP_ID` (`FORUMLA_LOOKUP_ID`),
  CONSTRAINT `fee_formula_master_ibfk_1` FOREIGN KEY (`FORUMLA_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_formula_master`
--

LOCK TABLES `fee_formula_master` WRITE;
/*!40000 ALTER TABLE `fee_formula_master` DISABLE KEYS */;
INSERT INTO `fee_formula_master` VALUES (1,149),(2,150),(3,151);
/*!40000 ALTER TABLE `fee_formula_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_frequency`
--

DROP TABLE IF EXISTS `fee_frequency`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_frequency` (
  `FEE_FREQUENCY_ID` smallint(6) NOT NULL auto_increment,
  `FEE_ID` smallint(6) NOT NULL,
  `FEE_FREQUENCYTYPE_ID` smallint(6) NOT NULL,
  `FREQUENCY_PAYMENT_ID` smallint(6) default NULL,
  `FREQUENCY_MEETING_ID` int(11) default NULL,
  PRIMARY KEY  (`FEE_FREQUENCY_ID`),
  KEY `FEE_ID` (`FEE_ID`),
  KEY `FEE_FREQUENCYTYPE_ID` (`FEE_FREQUENCYTYPE_ID`),
  KEY `FREQUENCY_PAYMENT_ID` (`FREQUENCY_PAYMENT_ID`),
  KEY `FREQUENCY_MEETING_ID` (`FREQUENCY_MEETING_ID`),
  CONSTRAINT `fee_frequency_ibfk_1` FOREIGN KEY (`FEE_ID`) REFERENCES `fees` (`FEE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fee_frequency_ibfk_2` FOREIGN KEY (`FEE_FREQUENCYTYPE_ID`) REFERENCES `fee_frequency_type` (`FEE_FREQUENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fee_frequency_ibfk_3` FOREIGN KEY (`FREQUENCY_PAYMENT_ID`) REFERENCES `fee_payment` (`FEE_PAYMENT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fee_frequency_ibfk_4` FOREIGN KEY (`FREQUENCY_MEETING_ID`) REFERENCES `meeting` (`MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_frequency`
--

LOCK TABLES `fee_frequency` WRITE;
/*!40000 ALTER TABLE `fee_frequency` DISABLE KEYS */;
/*!40000 ALTER TABLE `fee_frequency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_frequency_type`
--

DROP TABLE IF EXISTS `fee_frequency_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_frequency_type` (
  `FEE_FREQUENCY_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`FEE_FREQUENCY_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `fee_frequency_type_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_frequency_type`
--

LOCK TABLES `fee_frequency_type` WRITE;
/*!40000 ALTER TABLE `fee_frequency_type` DISABLE KEYS */;
INSERT INTO `fee_frequency_type` VALUES (1,558),(2,559);
/*!40000 ALTER TABLE `fee_frequency_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_payment`
--

DROP TABLE IF EXISTS `fee_payment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_payment` (
  `FEE_PAYMENT_ID` smallint(6) NOT NULL auto_increment,
  `FEE_PAYMENT_LOOKUP_ID` int(11) default NULL,
  PRIMARY KEY  (`FEE_PAYMENT_ID`),
  KEY `FEE_PAYMENT_LOOKUP_ID` (`FEE_PAYMENT_LOOKUP_ID`),
  CONSTRAINT `fee_payment_ibfk_1` FOREIGN KEY (`FEE_PAYMENT_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_payment`
--

LOCK TABLES `fee_payment` WRITE;
/*!40000 ALTER TABLE `fee_payment` DISABLE KEYS */;
INSERT INTO `fee_payment` VALUES (1,146),(2,147),(3,148);
/*!40000 ALTER TABLE `fee_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_payments_categories_type`
--

DROP TABLE IF EXISTS `fee_payments_categories_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_payments_categories_type` (
  `FEE_PAYMENTS_CATEGORY_TYPE_ID` smallint(6) NOT NULL,
  `FEE_PAYMENT_ID` smallint(6) default NULL,
  `CATEGORY_ID` smallint(6) default NULL,
  `FEE_TYPE_ID` smallint(6) default NULL,
  PRIMARY KEY  (`FEE_PAYMENTS_CATEGORY_TYPE_ID`),
  KEY `CATEGORY_ID` (`CATEGORY_ID`),
  KEY `FEE_PAYMENT_ID` (`FEE_PAYMENT_ID`),
  KEY `FEE_TYPE_ID` (`FEE_TYPE_ID`),
  CONSTRAINT `fee_payments_categories_type_ibfk_1` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `category_type` (`CATEGORY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fee_payments_categories_type_ibfk_2` FOREIGN KEY (`FEE_PAYMENT_ID`) REFERENCES `fee_payment` (`FEE_PAYMENT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fee_payments_categories_type_ibfk_3` FOREIGN KEY (`FEE_TYPE_ID`) REFERENCES `fee_type` (`FEE_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_payments_categories_type`
--

LOCK TABLES `fee_payments_categories_type` WRITE;
/*!40000 ALTER TABLE `fee_payments_categories_type` DISABLE KEYS */;
INSERT INTO `fee_payments_categories_type` VALUES (1,1,1,1),(2,1,1,1),(3,1,1,1);
/*!40000 ALTER TABLE `fee_payments_categories_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_status`
--

DROP TABLE IF EXISTS `fee_status`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_status` (
  `STATUS_ID` smallint(6) NOT NULL auto_increment,
  `STATUS_LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`STATUS_ID`),
  KEY `STATUS_LOOKUP_ID` (`STATUS_LOOKUP_ID`),
  CONSTRAINT `fee_status_ibfk_1` FOREIGN KEY (`STATUS_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_status`
--

LOCK TABLES `fee_status` WRITE;
/*!40000 ALTER TABLE `fee_status` DISABLE KEYS */;
INSERT INTO `fee_status` VALUES (1,165),(2,166);
/*!40000 ALTER TABLE `fee_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_trxn_detail`
--

DROP TABLE IF EXISTS `fee_trxn_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_trxn_detail` (
  `FEE_TRXN_DETAIL_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_TRXN_ID` int(11) NOT NULL,
  `ACCOUNT_FEE_ID` int(11) default NULL,
  `FEE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `FEE_AMOUNT` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`FEE_TRXN_DETAIL_ID`),
  KEY `ACCOUNT_FEE_ID` (`ACCOUNT_FEE_ID`),
  KEY `FEE_AMOUNT_CURRENCY_ID` (`FEE_AMOUNT_CURRENCY_ID`),
  KEY `FEE_ACCOUNT_TRXN_IDX` (`ACCOUNT_TRXN_ID`),
  CONSTRAINT `fee_trxn_detail_ibfk_1` FOREIGN KEY (`ACCOUNT_FEE_ID`) REFERENCES `account_fees` (`ACCOUNT_FEE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fee_trxn_detail_ibfk_2` FOREIGN KEY (`FEE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fee_trxn_detail_ibfk_3` FOREIGN KEY (`ACCOUNT_TRXN_ID`) REFERENCES `account_trxn` (`ACCOUNT_TRXN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_trxn_detail`
--

LOCK TABLES `fee_trxn_detail` WRITE;
/*!40000 ALTER TABLE `fee_trxn_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `fee_trxn_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_type`
--

DROP TABLE IF EXISTS `fee_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_type` (
  `FEE_TYPE_ID` smallint(6) NOT NULL,
  `FEE_LOOKUP_ID` smallint(6) default NULL,
  `FLAT_OR_RATE` smallint(6) default NULL,
  `FORMULA` varchar(100) default NULL,
  PRIMARY KEY  (`FEE_TYPE_ID`),
  KEY `FEE_LOOKUP_ID` (`FEE_LOOKUP_ID`),
  CONSTRAINT `fee_type_ibfk_1` FOREIGN KEY (`FEE_LOOKUP_ID`) REFERENCES `lookup_entity` (`ENTITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_type`
--

LOCK TABLES `fee_type` WRITE;
/*!40000 ALTER TABLE `fee_type` DISABLE KEYS */;
INSERT INTO `fee_type` VALUES (1,1,NULL,NULL),(2,1,NULL,NULL),(3,2,NULL,NULL),(4,3,NULL,NULL),(5,3,NULL,NULL);
/*!40000 ALTER TABLE `fee_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_update_type`
--

DROP TABLE IF EXISTS `fee_update_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fee_update_type` (
  `FEE_UPDATE_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`FEE_UPDATE_TYPE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `fee_update_type_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fee_update_type`
--

LOCK TABLES `fee_update_type` WRITE;
/*!40000 ALTER TABLE `fee_update_type` DISABLE KEYS */;
INSERT INTO `fee_update_type` VALUES (1,556),(2,557);
/*!40000 ALTER TABLE `fee_update_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feelevel`
--

DROP TABLE IF EXISTS `feelevel`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `feelevel` (
  `FEELEVEL_ID` smallint(6) NOT NULL auto_increment,
  `FEE_ID` smallint(6) NOT NULL,
  `LEVEL_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`FEELEVEL_ID`),
  KEY `FEE_ID` (`FEE_ID`),
  CONSTRAINT `feelevel_ibfk_1` FOREIGN KEY (`FEE_ID`) REFERENCES `fees` (`FEE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `feelevel`
--

LOCK TABLES `feelevel` WRITE;
/*!40000 ALTER TABLE `feelevel` DISABLE KEYS */;
/*!40000 ALTER TABLE `feelevel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fees`
--

DROP TABLE IF EXISTS `fees`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fees` (
  `FEE_ID` smallint(6) NOT NULL auto_increment,
  `GLOBAL_FEE_NUM` varchar(50) default NULL,
  `FEE_NAME` varchar(50) NOT NULL,
  `FEE_PAYMENTS_CATEGORY_TYPE_ID` smallint(6) default NULL,
  `OFFICE_ID` smallint(6) NOT NULL,
  `GLCODE_ID` smallint(6) NOT NULL,
  `STATUS` smallint(6) NOT NULL,
  `CATEGORY_ID` smallint(6) NOT NULL,
  `RATE_OR_AMOUNT` decimal(16,5) default NULL,
  `RATE_OR_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `RATE_FLAT_FALG` smallint(6) default NULL,
  `CREATED_DATE` date NOT NULL,
  `CREATED_BY` smallint(6) NOT NULL,
  `UPDATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATE_FLAG` smallint(6) default NULL,
  `FORMULA_ID` smallint(6) default NULL,
  `DEFAULT_ADMIN_FEE` varchar(10) default NULL,
  `FEE_AMOUNT` decimal(10,3) default NULL,
  `FEE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `RATE` decimal(16,5) default NULL,
  `VERSION_NO` int(11) NOT NULL,
  `DISCRIMINATOR` varchar(20) default NULL,
  PRIMARY KEY  (`FEE_ID`),
  UNIQUE KEY `FEE_GLOBAL_IDX` (`GLOBAL_FEE_NUM`),
  KEY `GLCODE_ID` (`GLCODE_ID`),
  KEY `CATEGORY_ID` (`CATEGORY_ID`),
  KEY `STATUS` (`STATUS`),
  KEY `CREATED_BY` (`CREATED_BY`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  KEY `FORMULA_ID` (`FORMULA_ID`),
  KEY `RATE_OR_AMOUNT_CURRENCY_ID` (`RATE_OR_AMOUNT_CURRENCY_ID`),
  KEY `FEE_AMOUNT_CURRENCY_ID` (`FEE_AMOUNT_CURRENCY_ID`),
  KEY `FEE_PMNT_CATG_IDX` (`FEE_PAYMENTS_CATEGORY_TYPE_ID`),
  KEY `FEE_OFFICE_IDX` (`OFFICE_ID`),
  CONSTRAINT `fees_ibfk_1` FOREIGN KEY (`GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_2` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `category_type` (`CATEGORY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_3` FOREIGN KEY (`STATUS`) REFERENCES `fee_status` (`STATUS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_4` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_5` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_6` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_7` FOREIGN KEY (`FORMULA_ID`) REFERENCES `fee_formula_master` (`FORMULAID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_8` FOREIGN KEY (`RATE_OR_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fees_ibfk_9` FOREIGN KEY (`FEE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fees`
--

LOCK TABLES `fees` WRITE;
/*!40000 ALTER TABLE `fees` DISABLE KEYS */;
/*!40000 ALTER TABLE `fees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `field_configuration`
--

DROP TABLE IF EXISTS `field_configuration`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `field_configuration` (
  `FIELD_CONFIG_ID` int(11) NOT NULL auto_increment,
  `FIELD_NAME` varchar(100) NOT NULL,
  `ENTITY_ID` smallint(6) NOT NULL,
  `MANDATORY_FLAG` smallint(6) NOT NULL,
  `HIDDEN_FLAG` smallint(6) NOT NULL,
  `PARENT_FIELD_CONFIG_ID` int(11) default NULL,
  PRIMARY KEY  (`FIELD_CONFIG_ID`),
  KEY `ENTITY_ID` (`ENTITY_ID`),
  KEY `PARENT_FIELD_CONFIG_ID` (`PARENT_FIELD_CONFIG_ID`),
  CONSTRAINT `field_configuration_ibfk_1` FOREIGN KEY (`ENTITY_ID`) REFERENCES `entity_master` (`ENTITY_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `field_configuration_ibfk_2` FOREIGN KEY (`PARENT_FIELD_CONFIG_ID`) REFERENCES `field_configuration` (`FIELD_CONFIG_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `field_configuration`
--

LOCK TABLES `field_configuration` WRITE;
/*!40000 ALTER TABLE `field_configuration` DISABLE KEYS */;
INSERT INTO `field_configuration` VALUES (1,'MiddleName',1,0,0,NULL),(2,'MiddleName',17,0,0,NULL),(3,'SecondLastName',1,0,0,NULL),(4,'SecondLastName',17,0,0,NULL),(5,'GovernmentId',1,0,0,NULL),(6,'GovernmentId',17,0,0,NULL),(7,'ExternalId',1,0,0,NULL),(8,'ExternalId',12,0,0,NULL),(9,'ExternalId',20,0,0,NULL),(10,'Ethinicity',1,0,0,NULL),(11,'Citizenship',1,0,0,NULL),(12,'Handicapped',1,0,0,NULL),(13,'BusinessActivities',1,0,0,NULL),(14,'EducationLevel',1,0,0,NULL),(15,'Photo',1,0,0,NULL),(16,'SpouseFatherMiddleName',1,0,0,NULL),(17,'SpouseFatherSecondLastName',1,0,0,NULL),(18,'Trained',1,0,0,NULL),(19,'Trained',12,0,0,NULL),(20,'TrainedDate',1,0,0,NULL),(21,'TrainedDate',12,0,0,NULL),(22,'Address',1,0,0,NULL),(23,'Address',12,0,0,NULL),(24,'Address',20,0,0,NULL),(25,'Address1',1,0,0,22),(26,'Address1',12,0,0,23),(27,'Address1',20,0,0,24),(28,'Address2',1,0,0,22),(29,'Address2',12,0,0,23),(30,'Address2',20,0,0,24),(31,'Address3',1,0,0,22),(32,'Address3',12,0,0,23),(33,'Address3',20,0,0,24),(34,'Address3',15,0,0,NULL),(35,'Address3',17,0,0,NULL),(36,'City',1,0,0,22),(37,'City',12,0,0,23),(38,'City',20,0,0,24),(39,'State',1,0,0,22),(40,'State',12,0,0,23),(41,'State',20,0,0,24),(42,'State',15,0,0,NULL),(43,'State',17,0,0,NULL),(44,'Country',1,0,0,22),(45,'Country',12,0,0,23),(46,'Country',20,0,0,24),(47,'Country',15,0,0,NULL),(48,'Country',17,0,0,NULL),(49,'PostalCode',1,0,0,22),(50,'PostalCode',12,0,0,23),(51,'PostalCode',20,0,0,24),(52,'PostalCode',15,0,0,NULL),(53,'PostalCode',17,0,0,NULL),(54,'PhoneNumber',1,0,0,NULL),(55,'PhoneNumber',12,0,0,NULL),(56,'PhoneNumber',20,0,0,NULL),(57,'PhoneNumber',17,0,0,NULL),(58,'PurposeOfLoan',22,0,0,NULL),(59,'CollateralType',22,0,0,NULL),(60,'CollateralNotes',22,0,0,NULL),(61,'ReceiptId',1,0,0,NULL),(62,'ReceiptId',12,0,0,NULL),(63,'ReceiptId',20,0,0,NULL),(64,'ReceiptId',21,0,0,NULL),(65,'ReceiptId',22,0,0,NULL),(66,'ReceiptId',23,0,0,NULL),(67,'ReceiptDate',1,0,0,NULL),(68,'ReceiptDate',12,0,0,NULL),(69,'ReceiptDate',20,0,0,NULL),(70,'ReceiptDate',21,0,0,NULL),(71,'ReceiptDate',22,0,0,NULL),(72,'ReceiptDate',23,0,0,NULL),(73,'PovertyStatus',1,1,0,NULL),(74,'AssignClients',1,0,0,NULL),(75,'Address2',15,0,0,NULL),(76,'Address2',17,0,0,NULL),(77,'Address1',15,0,0,NULL),(78,'Address1',17,0,0,NULL);
/*!40000 ALTER TABLE `field_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financial_action`
--

DROP TABLE IF EXISTS `financial_action`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `financial_action` (
  `FIN_ACTION_ID` smallint(6) NOT NULL,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`FIN_ACTION_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `financial_action_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value_locale` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `financial_action`
--

LOCK TABLES `financial_action` WRITE;
/*!40000 ALTER TABLE `financial_action` DISABLE KEYS */;
INSERT INTO `financial_action` VALUES (1,198),(2,199),(3,200),(5,201),(8,202),(9,203),(10,204),(11,205),(12,206),(13,207),(14,208),(15,209),(7,215),(4,229),(6,361),(16,363),(17,365),(18,367),(19,368),(20,369),(21,370),(22,550),(23,609);
/*!40000 ALTER TABLE `financial_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financial_trxn`
--

DROP TABLE IF EXISTS `financial_trxn`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `financial_trxn` (
  `TRXN_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_TRXN_ID` int(11) NOT NULL,
  `RELATED_FIN_TRXN` int(11) default NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `FIN_ACTION_ID` smallint(6) default NULL,
  `GLCODE_ID` smallint(6) NOT NULL,
  `POSTED_AMOUNT` decimal(10,3) NOT NULL,
  `POSTED_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `BALANCE_AMOUNT` decimal(10,3) NOT NULL,
  `BALANCE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `ACTION_DATE` date NOT NULL,
  `POSTED_DATE` date NOT NULL,
  `POSTED_BY` smallint(6) default NULL,
  `ACCOUNTING_UPDATED` smallint(6) default NULL,
  `NOTES` varchar(200) default NULL,
  `DEBIT_CREDIT_FLAG` smallint(6) NOT NULL,
  PRIMARY KEY  (`TRXN_ID`),
  KEY `ACCOUNT_TRXN_ID` (`ACCOUNT_TRXN_ID`),
  KEY `POSTED_AMOUNT_CURRENCY_ID` (`POSTED_AMOUNT_CURRENCY_ID`),
  KEY `BALANCE_AMOUNT_CURRENCY_ID` (`BALANCE_AMOUNT_CURRENCY_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `RELATED_FIN_TRXN` (`RELATED_FIN_TRXN`),
  KEY `FIN_ACTION_ID` (`FIN_ACTION_ID`),
  KEY `POSTED_BY` (`POSTED_BY`),
  KEY `GLCODE_ID` (`GLCODE_ID`),
  CONSTRAINT `financial_trxn_ibfk_1` FOREIGN KEY (`ACCOUNT_TRXN_ID`) REFERENCES `account_trxn` (`ACCOUNT_TRXN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_2` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_3` FOREIGN KEY (`POSTED_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_4` FOREIGN KEY (`BALANCE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_5` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_6` FOREIGN KEY (`RELATED_FIN_TRXN`) REFERENCES `financial_trxn` (`TRXN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_7` FOREIGN KEY (`FIN_ACTION_ID`) REFERENCES `financial_action` (`FIN_ACTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_8` FOREIGN KEY (`POSTED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `financial_trxn_ibfk_9` FOREIGN KEY (`GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `financial_trxn`
--

LOCK TABLES `financial_trxn` WRITE;
/*!40000 ALTER TABLE `financial_trxn` DISABLE KEYS */;
INSERT INTO `financial_trxn` VALUES (1,1,NULL,NULL,7,7,'-2000.000',2,'-2000.000',2,'2008-12-04','2008-12-04',1,1,'-',1),(2,1,NULL,NULL,7,13,'2000.000',2,'2000.000',2,'2008-12-04','2008-12-04',1,1,'-',0),(3,2,NULL,NULL,7,13,'10000.000',2,'10000.000',2,'2008-12-04','2008-12-04',1,1,'-',0),(4,2,NULL,NULL,7,7,'-10000.000',2,'-10000.000',2,'2008-12-04','2008-12-04',1,1,'-',1),(5,3,NULL,NULL,7,13,'2000.000',2,'2000.000',2,'2008-12-04','2008-12-04',1,1,'-',0),(6,3,NULL,NULL,7,7,'-2000.000',2,'-2000.000',2,'2008-12-04','2008-12-04',1,1,'-',1),(7,4,NULL,NULL,7,13,'10000.000',2,'10000.000',2,'2008-12-04','2008-12-04',1,1,'-',0),(8,4,NULL,NULL,7,7,'-10000.000',2,'-10000.000',2,'2008-12-04','2008-12-04',1,1,'-',1),(9,5,NULL,NULL,7,13,'2000.000',2,'2000.000',2,'2008-12-04','2008-12-04',1,1,'-',0),(10,5,NULL,NULL,7,7,'-2000.000',2,'-2000.000',2,'2008-12-04','2008-12-04',1,1,'-',1),(11,6,NULL,NULL,7,7,'-10000.000',2,'-10000.000',2,'2008-12-04','2008-12-04',1,1,'-',1),(12,6,NULL,NULL,7,13,'10000.000',2,'10000.000',2,'2008-12-04','2008-12-04',1,1,'-',0);
/*!40000 ALTER TABLE `financial_trxn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `freq_of_deposits`
--

DROP TABLE IF EXISTS `freq_of_deposits`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `freq_of_deposits` (
  `FREQ_OF_DEPOSITS_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`FREQ_OF_DEPOSITS_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `freq_of_deposits_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `freq_of_deposits`
--

LOCK TABLES `freq_of_deposits` WRITE;
/*!40000 ALTER TABLE `freq_of_deposits` DISABLE KEYS */;
/*!40000 ALTER TABLE `freq_of_deposits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fund`
--

DROP TABLE IF EXISTS `fund`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fund` (
  `FUND_ID` smallint(6) NOT NULL auto_increment,
  `FUND_NAME` varchar(100) default NULL,
  `VERSION_NO` int(11) default NULL,
  `FUNDCODE_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`FUND_ID`),
  KEY `FUNDCODE_ID` (`FUNDCODE_ID`),
  CONSTRAINT `fund_ibfk_1` FOREIGN KEY (`FUNDCODE_ID`) REFERENCES `fund_code` (`FUNDCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fund`
--

LOCK TABLES `fund` WRITE;
/*!40000 ALTER TABLE `fund` DISABLE KEYS */;
INSERT INTO `fund` VALUES (1,'Non Donor',0,1),(2,'Funding Org A',0,1),(3,'Funding Org B',0,1),(4,'Funding Org C',0,1),(5,'Funding Org D',0,1);
/*!40000 ALTER TABLE `fund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fund_code`
--

DROP TABLE IF EXISTS `fund_code`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `fund_code` (
  `FUNDCODE_ID` smallint(6) NOT NULL auto_increment,
  `FUNDCODE_VALUE` varchar(50) NOT NULL,
  PRIMARY KEY  (`FUNDCODE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `fund_code`
--

LOCK TABLES `fund_code` WRITE;
/*!40000 ALTER TABLE `fund_code` DISABLE KEYS */;
INSERT INTO `fund_code` VALUES (1,'00'),(2,'01'),(3,'02'),(4,'03'),(5,'04');
/*!40000 ALTER TABLE `fund_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gl_code`
--

DROP TABLE IF EXISTS `gl_code`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `gl_code` (
  `GLCODE_ID` smallint(6) NOT NULL auto_increment,
  `GLCODE_VALUE` varchar(50) NOT NULL,
  PRIMARY KEY  (`GLCODE_ID`),
  UNIQUE KEY `GLCODE_VALUE_IDX` (`GLCODE_VALUE`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `gl_code`
--

LOCK TABLES `gl_code` WRITE;
/*!40000 ALTER TABLE `gl_code` DISABLE KEYS */;
INSERT INTO `gl_code` VALUES (1,'10000'),(2,'11000'),(3,'11100'),(4,'11101'),(5,'11102'),(6,'11200'),(7,'11201'),(8,'11202'),(9,'13000'),(10,'13100'),(20,'13101'),(21,'13200'),(22,'13201'),(11,'1501'),(12,'1502'),(13,'1503'),(14,'1504'),(15,'1505'),(16,'1506'),(17,'1507'),(18,'1508'),(19,'1509'),(23,'20000'),(24,'22000'),(25,'22100'),(26,'22101'),(27,'23000'),(28,'23100'),(33,'23101'),(34,'24000'),(35,'24100'),(36,'24101'),(37,'30000'),(38,'31000'),(39,'31100'),(41,'31101'),(42,'31102'),(43,'31300'),(50,'31301'),(51,'31401'),(52,'40000'),(53,'41000'),(54,'41100'),(55,'41101'),(56,'41102'),(29,'4601'),(30,'4602'),(31,'4603'),(32,'4606'),(40,'5001'),(44,'5201'),(45,'5202'),(46,'5203'),(47,'5204'),(48,'5205'),(49,'6201');
/*!40000 ALTER TABLE `gl_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grace_period_type`
--

DROP TABLE IF EXISTS `grace_period_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `grace_period_type` (
  `GRACE_PERIOD_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`GRACE_PERIOD_TYPE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `grace_period_type_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `grace_period_type`
--

LOCK TABLES `grace_period_type` WRITE;
/*!40000 ALTER TABLE `grace_period_type` DISABLE KEYS */;
INSERT INTO `grace_period_type` VALUES (1,96),(2,97),(3,98);
/*!40000 ALTER TABLE `grace_period_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_loan_counter`
--

DROP TABLE IF EXISTS `group_loan_counter`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `group_loan_counter` (
  `GROUP_LOAN_COUNTER_ID` int(11) NOT NULL auto_increment,
  `GROUP_PERF_ID` int(11) NOT NULL,
  `LOAN_OFFERING_ID` smallint(6) NOT NULL,
  `LOAN_CYCLE_COUNTER` smallint(6) default NULL,
  PRIMARY KEY  (`GROUP_LOAN_COUNTER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `group_loan_counter`
--

LOCK TABLES `group_loan_counter` WRITE;
/*!40000 ALTER TABLE `group_loan_counter` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_loan_counter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_perf_history`
--

DROP TABLE IF EXISTS `group_perf_history`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `group_perf_history` (
  `ID` int(11) NOT NULL auto_increment,
  `CUSTOMER_ID` int(11) NOT NULL,
  `NO_OF_CLIENTS` smallint(6) default NULL,
  `LAST_GROUP_LOAN_AMNT_DISB` decimal(10,3) default NULL,
  `LAST_GROUP_LOAN_AMNT_DISB_CURRENCY_ID` smallint(6) default NULL,
  `AVG_LOAN_SIZE` decimal(10,3) default NULL,
  `AVG_LOAN_SIZE_CURRENCY_ID` smallint(6) default NULL,
  `TOTAL_OUTSTAND_LOAN_AMNT` decimal(10,3) default NULL,
  `TOTAL_OUTSTAND_LOAN_AMNT_CURRENCY_ID` smallint(6) default NULL,
  `PORTFOLIO_AT_RISK` decimal(10,3) default NULL,
  `PORTFOLIO_AT_RISK_CURRENCY_ID` smallint(6) default NULL,
  `TOTAL_SAVINGS_AMNT` decimal(10,3) default NULL,
  `TOTAL_SAVINGS_AMNT_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `group_perf_history_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `group_perf_history`
--

LOCK TABLES `group_perf_history` WRITE;
/*!40000 ALTER TABLE `group_perf_history` DISABLE KEYS */;
INSERT INTO `group_perf_history` VALUES (1,2,0,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2),(2,7,0,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2),(3,13,0,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2);
/*!40000 ALTER TABLE `group_perf_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `holiday`
--

DROP TABLE IF EXISTS `holiday`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `holiday` (
  `OFFICE_ID` smallint(6) NOT NULL,
  `HOLIDAY_FROM_DATE` date NOT NULL,
  `HOLIDAY_THRU_DATE` date default NULL,
  `HOLIDAY_NAME` varchar(100) default NULL,
  `REPAYMENT_RULE_ID` smallint(6) NOT NULL,
  `HOLIDAY_CHANGES_APPLIED_FLAG` smallint(6) default '1',
  PRIMARY KEY  (`OFFICE_ID`,`HOLIDAY_FROM_DATE`),
  KEY `REPAYMENT_RULE_ID` (`REPAYMENT_RULE_ID`),
  CONSTRAINT `holiday_ibfk_1` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `holiday_ibfk_2` FOREIGN KEY (`REPAYMENT_RULE_ID`) REFERENCES `repayment_rule` (`REPAYMENT_RULE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `holiday`
--

LOCK TABLES `holiday` WRITE;
/*!40000 ALTER TABLE `holiday` DISABLE KEYS */;
/*!40000 ALTER TABLE `holiday` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inherited_meeting`
--

DROP TABLE IF EXISTS `inherited_meeting`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `inherited_meeting` (
  `MEETING_ID` int(11) default NULL,
  `CUSTOMER_ID` int(11) default NULL,
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  KEY `MEETING_ID` (`MEETING_ID`),
  CONSTRAINT `inherited_meeting_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `inherited_meeting_ibfk_2` FOREIGN KEY (`MEETING_ID`) REFERENCES `customer_meeting` (`CUSTOMER_MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `inherited_meeting`
--

LOCK TABLES `inherited_meeting` WRITE;
/*!40000 ALTER TABLE `inherited_meeting` DISABLE KEYS */;
/*!40000 ALTER TABLE `inherited_meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `insurance_offering`
--

DROP TABLE IF EXISTS `insurance_offering`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `insurance_offering` (
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`PRD_OFFERING_ID`),
  CONSTRAINT `insurance_offering_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `insurance_offering`
--

LOCK TABLES `insurance_offering` WRITE;
/*!40000 ALTER TABLE `insurance_offering` DISABLE KEYS */;
/*!40000 ALTER TABLE `insurance_offering` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interest_calc_rule`
--

DROP TABLE IF EXISTS `interest_calc_rule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `interest_calc_rule` (
  `INTEREST_CALC_RULE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`INTEREST_CALC_RULE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `interest_calc_rule_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `interest_calc_rule`
--

LOCK TABLES `interest_calc_rule` WRITE;
/*!40000 ALTER TABLE `interest_calc_rule` DISABLE KEYS */;
INSERT INTO `interest_calc_rule` VALUES (1,88),(2,89);
/*!40000 ALTER TABLE `interest_calc_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interest_calculation_types`
--

DROP TABLE IF EXISTS `interest_calculation_types`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `interest_calculation_types` (
  `INTEREST_CALCULATION_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `INTEREST_CALCULATION_LOOKUP_ID` int(11) NOT NULL,
  `DESCRIPTION` varchar(100) default NULL,
  PRIMARY KEY  (`INTEREST_CALCULATION_TYPE_ID`),
  KEY `INTEREST_CALCULATION_LOOKUP_ID` (`INTEREST_CALCULATION_LOOKUP_ID`),
  CONSTRAINT `interest_calculation_types_ibfk_1` FOREIGN KEY (`INTEREST_CALCULATION_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `interest_calculation_types`
--

LOCK TABLES `interest_calculation_types` WRITE;
/*!40000 ALTER TABLE `interest_calculation_types` DISABLE KEYS */;
INSERT INTO `interest_calculation_types` VALUES (1,122,NULL),(2,123,NULL);
/*!40000 ALTER TABLE `interest_calculation_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interest_types`
--

DROP TABLE IF EXISTS `interest_types`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `interest_types` (
  `INTEREST_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  `CATEGORY_ID` smallint(6) NOT NULL,
  `DESCRIPTON` varchar(50) default NULL,
  PRIMARY KEY  (`INTEREST_TYPE_ID`),
  KEY `CATEGORY_ID` (`CATEGORY_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `interest_types_ibfk_1` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `prd_type` (`PRD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `interest_types_ibfk_2` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `interest_types`
--

LOCK TABLES `interest_types` WRITE;
/*!40000 ALTER TABLE `interest_types` DISABLE KEYS */;
INSERT INTO `interest_types` VALUES (1,79,1,'Flat'),(2,80,1,'Declining'),(4,604,1,'Declining Balance-Equal Principal Installment');
/*!40000 ALTER TABLE `interest_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `language` (
  `LANG_ID` smallint(6) NOT NULL,
  `LANG_NAME` varchar(100) default NULL,
  `LANG_SHORT_NAME` varchar(10) default NULL,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`LANG_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `language_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `language`
--

LOCK TABLES `language` WRITE;
/*!40000 ALTER TABLE `language` DISABLE KEYS */;
INSERT INTO `language` VALUES (1,'English','EN',189),(2,'Icelandic','IS',599),(3,'Spanish','es',600),(4,'French','fr',601);
/*!40000 ALTER TABLE `language` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_account`
--

DROP TABLE IF EXISTS `loan_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_account` (
  `ACCOUNT_ID` int(11) NOT NULL,
  `BUSINESS_ACTIVITIES_ID` int(11) default NULL,
  `COLLATERAL_TYPE_ID` int(11) default NULL,
  `GRACE_PERIOD_TYPE_ID` smallint(6) NOT NULL,
  `GROUP_FLAG` smallint(6) default NULL,
  `LOAN_AMOUNT` decimal(10,3) default NULL,
  `LOAN_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `LOAN_BALANCE` decimal(10,3) default NULL,
  `LOAN_BALANCE_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_TYPE_ID` smallint(6) default NULL,
  `INTEREST_RATE` decimal(13,10) default NULL,
  `FUND_ID` smallint(6) default NULL,
  `MEETING_ID` int(11) default NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `NO_OF_INSTALLMENTS` smallint(6) NOT NULL,
  `DISBURSEMENT_DATE` date default NULL,
  `COLLATERAL_NOTE` text,
  `GRACE_PERIOD_DURATION` smallint(6) default NULL,
  `INTEREST_AT_DISB` smallint(6) default NULL,
  `GRACE_PERIOD_PENALTY` smallint(6) default NULL,
  `PRD_OFFERING_ID` smallint(6) default NULL,
  `REDONE` smallint(6) NOT NULL,
  `PARENT_ACCOUNT_ID` int(11) default NULL,
  `MONTH_RANK` smallint(6) default NULL,
  `MONTH_WEEK` smallint(6) default NULL,
  `RECUR_MONTH` smallint(6) default NULL,
  PRIMARY KEY  (`ACCOUNT_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `LOAN_AMOUNT_CURRENCY_ID` (`LOAN_AMOUNT_CURRENCY_ID`),
  KEY `LOAN_BALANCE_CURRENCY_ID` (`LOAN_BALANCE_CURRENCY_ID`),
  KEY `FUND_ID` (`FUND_ID`),
  KEY `GRACE_PERIOD_TYPE_ID` (`GRACE_PERIOD_TYPE_ID`),
  KEY `INTEREST_TYPE_ID` (`INTEREST_TYPE_ID`),
  KEY `MEETING_ID` (`MEETING_ID`),
  KEY `fk_loan_col_type_id` (`COLLATERAL_TYPE_ID`),
  KEY `fk_loan_bus_act_id` (`BUSINESS_ACTIVITIES_ID`),
  KEY `fk_loan_prd_off_id` (`PRD_OFFERING_ID`),
  KEY `fk_loan_account` (`PARENT_ACCOUNT_ID`),
  KEY `fk_loan_rankday` (`MONTH_RANK`),
  KEY `fk_loan_monthweek` (`MONTH_WEEK`),
  CONSTRAINT `fk_loan_account` FOREIGN KEY (`PARENT_ACCOUNT_ID`) REFERENCES `loan_account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_loan_acc_id` FOREIGN KEY (`PARENT_ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_loan_bus_act_id` FOREIGN KEY (`BUSINESS_ACTIVITIES_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_loan_col_type_id` FOREIGN KEY (`COLLATERAL_TYPE_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_loan_monthweek` FOREIGN KEY (`MONTH_WEEK`) REFERENCES `week_days_master` (`WEEK_DAYS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_loan_prd_off_id` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_loan_rankday` FOREIGN KEY (`MONTH_RANK`) REFERENCES `rank_days_master` (`RANK_DAYS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_2` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_3` FOREIGN KEY (`LOAN_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_4` FOREIGN KEY (`LOAN_BALANCE_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_5` FOREIGN KEY (`FUND_ID`) REFERENCES `fund` (`FUND_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_6` FOREIGN KEY (`GRACE_PERIOD_TYPE_ID`) REFERENCES `grace_period_type` (`GRACE_PERIOD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_7` FOREIGN KEY (`INTEREST_TYPE_ID`) REFERENCES `interest_types` (`INTEREST_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_account_ibfk_8` FOREIGN KEY (`MEETING_ID`) REFERENCES `meeting` (`MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_account`
--

LOCK TABLES `loan_account` WRITE;
/*!40000 ALTER TABLE `loan_account` DISABLE KEYS */;
INSERT INTO `loan_account` VALUES (4,NULL,NULL,1,NULL,'2000.000',2,'2000.000',2,1,'3.0000000000',NULL,3,NULL,11,'2008-12-04','',0,0,0,1,0,NULL,NULL,NULL,NULL),(6,NULL,NULL,1,NULL,'10000.000',2,'10000.000',2,1,'13.0000000000',NULL,4,NULL,26,'2008-12-04','',0,0,0,1,0,NULL,NULL,NULL,NULL),(8,NULL,NULL,1,NULL,'10000.000',2,'10000.000',2,1,'15.0000000000',NULL,5,NULL,52,'2008-12-04','',0,0,0,1,0,NULL,NULL,NULL,NULL),(12,NULL,NULL,1,NULL,'2000.000',2,'2000.000',2,1,'3.0000000000',NULL,8,NULL,11,'2008-12-04','',0,0,0,2,0,NULL,NULL,NULL,NULL),(14,NULL,NULL,1,NULL,'10000.000',2,'10000.000',2,1,'13.0000000000',NULL,9,NULL,26,'2008-12-04','',0,0,0,2,0,NULL,NULL,NULL,NULL),(16,NULL,NULL,1,NULL,'10000.000',2,'10000.000',2,1,'15.0000000000',NULL,10,NULL,52,'2008-12-04','',0,0,0,2,0,NULL,NULL,NULL,NULL),(18,NULL,NULL,1,NULL,'2000.000',2,'2000.000',2,1,'9.0000000000',NULL,11,NULL,52,'2008-12-04','',0,0,0,2,0,NULL,NULL,NULL,NULL),(22,NULL,NULL,1,NULL,'2000.000',2,'2000.000',2,2,'3.0000000000',NULL,14,NULL,11,'2008-12-04','',0,0,0,3,0,NULL,NULL,NULL,NULL),(24,NULL,NULL,1,NULL,'10000.000',2,'10000.000',2,2,'23.0000000000',NULL,15,NULL,26,'2008-12-04','',0,0,0,3,0,NULL,NULL,NULL,NULL),(26,NULL,NULL,1,NULL,'10000.000',2,'10000.000',2,2,'15.0000000000',NULL,16,NULL,52,'2008-12-04','',0,0,0,3,0,NULL,NULL,NULL,NULL),(28,NULL,NULL,1,NULL,'2000.000',2,'2000.000',2,2,'9.0000000000',NULL,17,NULL,52,'2008-12-04','',0,0,0,3,0,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `loan_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_activity_details`
--

DROP TABLE IF EXISTS `loan_activity_details`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_activity_details` (
  `ID` int(11) NOT NULL auto_increment,
  `CREATED_BY` smallint(6) NOT NULL,
  `ACCOUNT_ID` int(11) NOT NULL,
  `CREATED_DATE` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `COMMENTS` varchar(100) NOT NULL,
  `PRINCIPAL_AMOUNT` decimal(10,3) default NULL,
  `PRINCIPAL_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_AMOUNT` decimal(10,3) default NULL,
  `INTEREST_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `PENALTY_AMOUNT` decimal(10,3) default NULL,
  `PENALTY_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `FEE_AMOUNT` decimal(10,3) default NULL,
  `FEE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `BALANCE_PRINCIPAL_AMOUNT` decimal(10,3) default NULL,
  `BALANCE_PRINCIPAL_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `BALANCE_INTEREST_AMOUNT` decimal(10,3) default NULL,
  `BALANCE_INTEREST_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `BALANCE_PENALTY_AMOUNT` decimal(10,3) default NULL,
  `BALANCE_PENALTY_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `BALANCE_FEE_AMOUNT` decimal(10,3) default NULL,
  `BALANCE_FEE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `CREATED_BY` (`CREATED_BY`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `PRINCIPAL_AMOUNT_CURRENCY_ID` (`PRINCIPAL_AMOUNT_CURRENCY_ID`),
  KEY `INTEREST_AMOUNT_CURRENCY_ID` (`INTEREST_AMOUNT_CURRENCY_ID`),
  KEY `FEE_AMOUNT_CURRENCY_ID` (`FEE_AMOUNT_CURRENCY_ID`),
  KEY `PENALTY_AMOUNT_CURRENCY_ID` (`PENALTY_AMOUNT_CURRENCY_ID`),
  KEY `BALANCE_PRINCIPAL_AMOUNT_CURRENCY_ID` (`BALANCE_PRINCIPAL_AMOUNT_CURRENCY_ID`),
  KEY `BALANCE_INTEREST_AMOUNT_CURRENCY_ID` (`BALANCE_INTEREST_AMOUNT_CURRENCY_ID`),
  KEY `BALANCE_PENALTY_AMOUNT_CURRENCY_ID` (`BALANCE_PENALTY_AMOUNT_CURRENCY_ID`),
  KEY `BALANCE_FEE_AMOUNT_CURRENCY_ID` (`BALANCE_FEE_AMOUNT_CURRENCY_ID`),
  CONSTRAINT `loan_activity_details_ibfk_1` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_10` FOREIGN KEY (`BALANCE_FEE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_2` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_3` FOREIGN KEY (`PRINCIPAL_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_4` FOREIGN KEY (`INTEREST_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_5` FOREIGN KEY (`FEE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_6` FOREIGN KEY (`PENALTY_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_7` FOREIGN KEY (`BALANCE_PRINCIPAL_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_8` FOREIGN KEY (`BALANCE_INTEREST_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_activity_details_ibfk_9` FOREIGN KEY (`BALANCE_PENALTY_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_activity_details`
--

LOCK TABLES `loan_activity_details` WRITE;
/*!40000 ALTER TABLE `loan_activity_details` DISABLE KEYS */;
INSERT INTO `loan_activity_details` VALUES (1,1,4,'2008-12-04 08:00:00','Loan Disbursal','2000.000',2,'0.000',2,'0.000',2,'0.000',2,'2000.000',2,'13.000',2,'0.000',2,'0.000',2),(2,1,8,'2008-12-04 08:00:00','Loan Disbursal','10000.000',2,'0.000',2,'0.000',2,'0.000',2,'10000.000',2,'1496.000',2,'0.000',2,'0.000',2),(3,1,12,'2008-12-04 08:00:00','Loan Disbursal','2000.000',2,'0.000',2,'0.000',2,'0.000',2,'2000.000',2,'13.000',2,'0.000',2,'0.000',2),(4,1,16,'2008-12-04 08:00:00','Loan Disbursal','10000.000',2,'0.000',2,'0.000',2,'0.000',2,'10000.000',2,'1496.000',2,'0.000',2,'0.000',2),(5,1,22,'2008-12-04 08:00:00','Loan Disbursal','2000.000',2,'0.000',2,'0.000',2,'0.000',2,'2000.000',2,'7.000',2,'0.000',2,'0.000',2),(6,1,26,'2008-12-04 08:00:00','Loan Disbursal','10000.000',2,'0.000',2,'0.000',2,'0.000',2,'10000.000',2,'781.000',2,'0.000',2,'0.000',2);
/*!40000 ALTER TABLE `loan_activity_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_amount_from_last_loan`
--

DROP TABLE IF EXISTS `loan_amount_from_last_loan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_amount_from_last_loan` (
  `LOAN_AMOUNT_FROM_LAST_LOAN_ID` smallint(6) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `START_RANGE` decimal(10,3) NOT NULL,
  `END_RANGE` decimal(10,3) NOT NULL,
  `MIN_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  `MAX_LOAN_AMNT` decimal(10,3) NOT NULL,
  `DEFAULT_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`LOAN_AMOUNT_FROM_LAST_LOAN_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `loan_amount_from_last_loan_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `loan_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_amount_from_last_loan`
--

LOCK TABLES `loan_amount_from_last_loan` WRITE;
/*!40000 ALTER TABLE `loan_amount_from_last_loan` DISABLE KEYS */;
/*!40000 ALTER TABLE `loan_amount_from_last_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_amount_from_loan_cycle`
--

DROP TABLE IF EXISTS `loan_amount_from_loan_cycle`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_amount_from_loan_cycle` (
  `LOAN_AMOUNT_FROM_LOAN_CYCLE_ID` smallint(6) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `MIN_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  `MAX_LOAN_AMNT` decimal(10,3) NOT NULL,
  `DEFAULT_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  `RANGE_INDEX` smallint(6) NOT NULL,
  PRIMARY KEY  (`LOAN_AMOUNT_FROM_LOAN_CYCLE_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `loan_amount_from_loan_cycle_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `loan_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_amount_from_loan_cycle`
--

LOCK TABLES `loan_amount_from_loan_cycle` WRITE;
/*!40000 ALTER TABLE `loan_amount_from_loan_cycle` DISABLE KEYS */;
/*!40000 ALTER TABLE `loan_amount_from_loan_cycle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_amount_same_for_all_loan`
--

DROP TABLE IF EXISTS `loan_amount_same_for_all_loan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_amount_same_for_all_loan` (
  `LOAN_AMOUNT_SAME_FOR_ALL_LOAN_ID` smallint(6) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `MIN_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  `MAX_LOAN_AMNT` decimal(10,3) NOT NULL,
  `DEFAULT_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`LOAN_AMOUNT_SAME_FOR_ALL_LOAN_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `loan_amount_same_for_all_loan_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `loan_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_amount_same_for_all_loan`
--

LOCK TABLES `loan_amount_same_for_all_loan` WRITE;
/*!40000 ALTER TABLE `loan_amount_same_for_all_loan` DISABLE KEYS */;
INSERT INTO `loan_amount_same_for_all_loan` VALUES (1,1,'100.000','10000.000','2000.000'),(2,2,'100.000','10000.000','2000.000'),(3,3,'100.000','10000.000','2000.000');
/*!40000 ALTER TABLE `loan_amount_same_for_all_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_arrears_aging`
--

DROP TABLE IF EXISTS `loan_arrears_aging`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_arrears_aging` (
  `ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `CUSTOMER_NAME` varchar(200) default NULL,
  `PARENT_CUSTOMER_ID` int(11) default NULL,
  `OFFICE_ID` smallint(6) NOT NULL,
  `DAYS_IN_ARREARS` smallint(6) NOT NULL,
  `OVERDUE_PRINCIPAL` decimal(10,3) default NULL,
  `OVERDUE_PRINCIPAL_CURRENCY_ID` smallint(6) default NULL,
  `OVERDUE_INTEREST` decimal(10,3) default NULL,
  `OVERDUE_INTEREST_CURRENCY_ID` smallint(6) default NULL,
  `OVERDUE_BALANCE` decimal(10,3) default NULL,
  `OVERDUE_BALANCE_CURRENCY_ID` smallint(6) default NULL,
  `UNPAID_PRINCIPAL` decimal(10,3) default NULL,
  `UNPAID_PRINCIPAL_CURRENCY_ID` smallint(6) default NULL,
  `UNPAID_INTEREST` decimal(10,3) default NULL,
  `UNPAID_INTEREST_CURRENCY_ID` smallint(6) default NULL,
  `UNPAID_BALANCE` decimal(10,3) default NULL,
  `UNPAID_BALANCE_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  KEY `PARENT_CUSTOMER_ID` (`PARENT_CUSTOMER_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  KEY `OVERDUE_PRINCIPAL_CURRENCY_ID` (`OVERDUE_PRINCIPAL_CURRENCY_ID`),
  KEY `OVERDUE_INTEREST_CURRENCY_ID` (`OVERDUE_INTEREST_CURRENCY_ID`),
  KEY `OVERDUE_BALANCE_CURRENCY_ID` (`OVERDUE_BALANCE_CURRENCY_ID`),
  KEY `UNPAID_PRINCIPAL_CURRENCY_ID` (`UNPAID_PRINCIPAL_CURRENCY_ID`),
  KEY `UNPAID_INTEREST_CURRENCY_ID` (`UNPAID_INTEREST_CURRENCY_ID`),
  KEY `UNPAID_BALANCE_CURRENCY_ID` (`UNPAID_BALANCE_CURRENCY_ID`),
  CONSTRAINT `loan_arrears_aging_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_10` FOREIGN KEY (`UNPAID_BALANCE_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_2` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_3` FOREIGN KEY (`PARENT_CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_4` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_5` FOREIGN KEY (`OVERDUE_PRINCIPAL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_6` FOREIGN KEY (`OVERDUE_INTEREST_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_7` FOREIGN KEY (`OVERDUE_BALANCE_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_8` FOREIGN KEY (`UNPAID_PRINCIPAL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_arrears_aging_ibfk_9` FOREIGN KEY (`UNPAID_INTEREST_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_arrears_aging`
--

LOCK TABLES `loan_arrears_aging` WRITE;
/*!40000 ALTER TABLE `loan_arrears_aging` DISABLE KEYS */;
/*!40000 ALTER TABLE `loan_arrears_aging` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_counter`
--

DROP TABLE IF EXISTS `loan_counter`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_counter` (
  `LOAN_COUNTER_ID` int(11) NOT NULL auto_increment,
  `CLIENT_PERF_ID` int(11) NOT NULL,
  `LOAN_OFFERING_ID` smallint(6) NOT NULL,
  `LOAN_CYCLE_COUNTER` smallint(6) default NULL,
  PRIMARY KEY  (`LOAN_COUNTER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_counter`
--

LOCK TABLES `loan_counter` WRITE;
/*!40000 ALTER TABLE `loan_counter` DISABLE KEYS */;
INSERT INTO `loan_counter` VALUES (1,1,1,1),(2,3,1,1),(3,4,2,1),(4,6,2,1),(5,8,3,1),(6,10,3,1);
/*!40000 ALTER TABLE `loan_counter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_fee_schedule`
--

DROP TABLE IF EXISTS `loan_fee_schedule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_fee_schedule` (
  `ACCOUNT_FEES_DETAIL_ID` int(11) NOT NULL auto_increment,
  `ID` int(11) NOT NULL,
  `INSTALLMENT_ID` int(11) NOT NULL,
  `FEE_ID` smallint(6) NOT NULL,
  `ACCOUNT_FEE_ID` int(11) NOT NULL,
  `AMOUNT` decimal(10,3) default NULL,
  `AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `AMOUNT_PAID` decimal(10,3) default NULL,
  `AMOUNT_PAID_CURRENCY_ID` smallint(6) default NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_FEES_DETAIL_ID`),
  KEY `ID` (`ID`),
  KEY `AMOUNT_CURRENCY_ID` (`AMOUNT_CURRENCY_ID`),
  KEY `AMOUNT_PAID_CURRENCY_ID` (`AMOUNT_PAID_CURRENCY_ID`),
  KEY `FEE_ID` (`FEE_ID`),
  KEY `ACCOUNT_FEE_ID` (`ACCOUNT_FEE_ID`),
  CONSTRAINT `loan_fee_schedule_ibfk_1` FOREIGN KEY (`ID`) REFERENCES `loan_schedule` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_fee_schedule_ibfk_2` FOREIGN KEY (`AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_fee_schedule_ibfk_3` FOREIGN KEY (`AMOUNT_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_fee_schedule_ibfk_4` FOREIGN KEY (`FEE_ID`) REFERENCES `fees` (`FEE_ID`),
  CONSTRAINT `loan_fee_schedule_ibfk_5` FOREIGN KEY (`ACCOUNT_FEE_ID`) REFERENCES `account_fees` (`ACCOUNT_FEE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_fee_schedule`
--

LOCK TABLES `loan_fee_schedule` WRITE;
/*!40000 ALTER TABLE `loan_fee_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `loan_fee_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_offering`
--

DROP TABLE IF EXISTS `loan_offering`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_offering` (
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `INTEREST_TYPE_ID` smallint(6) NOT NULL,
  `INTEREST_CALC_RULE_ID` smallint(6) default NULL,
  `PENALTY_ID` smallint(6) default NULL,
  `MIN_LOAN_AMOUNT` decimal(10,3) default NULL,
  `MIN_LOAN_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `MAX_LOAN_AMNT` decimal(10,3) default NULL,
  `MAX_LOAN_AMNT_CURRENCY_ID` smallint(6) default NULL,
  `DEFAULT_LOAN_AMOUNT` decimal(10,3) default NULL,
  `DEFAULT_LOAN_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `GRACEPERIOD_TYPE_ID` smallint(6) default NULL,
  `MAX_INTEREST_RATE` decimal(13,10) NOT NULL,
  `MIN_INTEREST_RATE` decimal(13,10) NOT NULL,
  `DEF_INTEREST_RATE` decimal(13,10) NOT NULL,
  `MAX_NO_INSTALLMENTS` smallint(6) default NULL,
  `MIN_NO_INSTALLMENTS` smallint(6) default NULL,
  `DEF_NO_INSTALLMENTS` smallint(6) default NULL,
  `PENALTY_GRACE` smallint(6) default NULL,
  `LOAN_COUNTER_FLAG` smallint(6) default NULL,
  `INT_DED_DISBURSEMENT_FLAG` smallint(6) NOT NULL,
  `PRIN_DUE_LAST_INST_FLAG` smallint(6) NOT NULL,
  `PENALTY_RATE` decimal(13,10) default NULL,
  `GRACE_PERIOD_DURATION` smallint(6) default NULL,
  `PRINCIPAL_GLCODE_ID` smallint(6) NOT NULL,
  `INTEREST_GLCODE_ID` smallint(6) NOT NULL,
  `PENALTIES_GLCODE_ID` smallint(6) default NULL,
  PRIMARY KEY  (`PRD_OFFERING_ID`),
  KEY `PRINCIPAL_GLCODE_ID` (`PRINCIPAL_GLCODE_ID`),
  KEY `INTEREST_GLCODE_ID` (`INTEREST_GLCODE_ID`),
  KEY `LOAN_OFFERING_PENALTY_GLCODE` (`PENALTIES_GLCODE_ID`),
  KEY `GRACEPERIOD_TYPE_ID` (`GRACEPERIOD_TYPE_ID`),
  KEY `LOAN_OFFERING_PENALTY` (`PENALTY_ID`),
  KEY `LOAN_OFFERING_INTEREST_CALC_RULE` (`INTEREST_CALC_RULE_ID`),
  KEY `INTEREST_TYPE_ID` (`INTEREST_TYPE_ID`),
  KEY `MIN_LOAN_AMOUNT_CURRENCY_ID` (`MIN_LOAN_AMOUNT_CURRENCY_ID`),
  KEY `MAX_LOAN_AMNT_CURRENCY_ID` (`MAX_LOAN_AMNT_CURRENCY_ID`),
  KEY `DEFAULT_LOAN_AMOUNT_CURRENCY_ID` (`DEFAULT_LOAN_AMOUNT_CURRENCY_ID`),
  CONSTRAINT `loan_offering_ibfk_1` FOREIGN KEY (`PRINCIPAL_GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_ibfk_2` FOREIGN KEY (`INTEREST_GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_ibfk_3` FOREIGN KEY (`GRACEPERIOD_TYPE_ID`) REFERENCES `grace_period_type` (`GRACE_PERIOD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_ibfk_4` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_ibfk_5` FOREIGN KEY (`INTEREST_TYPE_ID`) REFERENCES `interest_types` (`INTEREST_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_ibfk_6` FOREIGN KEY (`MIN_LOAN_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_ibfk_7` FOREIGN KEY (`MAX_LOAN_AMNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_ibfk_8` FOREIGN KEY (`DEFAULT_LOAN_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `LOAN_OFFERING_INTEREST_CALC_RULE` FOREIGN KEY (`INTEREST_CALC_RULE_ID`) REFERENCES `interest_calc_rule` (`INTEREST_CALC_RULE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `LOAN_OFFERING_PENALTY` FOREIGN KEY (`PENALTY_ID`) REFERENCES `penalty` (`PENALTY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `LOAN_OFFERING_PENALTY_GLCODE` FOREIGN KEY (`PENALTIES_GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_offering`
--

LOCK TABLES `loan_offering` WRITE;
/*!40000 ALTER TABLE `loan_offering` DISABLE KEYS */;
INSERT INTO `loan_offering` VALUES (1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'30.0000000000','3.0000000000','3.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,13,41,NULL),(2,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'30.0000000000','3.0000000000','3.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,13,41,NULL),(3,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'30.0000000000','3.0000000000','3.0000000000',NULL,NULL,NULL,NULL,0,0,0,NULL,0,13,41,NULL);
/*!40000 ALTER TABLE `loan_offering` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_offering_fund`
--

DROP TABLE IF EXISTS `loan_offering_fund`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_offering_fund` (
  `LOAN_OFFERING_FUND_ID` smallint(6) NOT NULL auto_increment,
  `FUND_ID` smallint(6) NOT NULL,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`LOAN_OFFERING_FUND_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  KEY `FUND_ID` (`FUND_ID`),
  CONSTRAINT `loan_offering_fund_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `loan_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_offering_fund_ibfk_2` FOREIGN KEY (`FUND_ID`) REFERENCES `fund` (`FUND_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_offering_fund`
--

LOCK TABLES `loan_offering_fund` WRITE;
/*!40000 ALTER TABLE `loan_offering_fund` DISABLE KEYS */;
/*!40000 ALTER TABLE `loan_offering_fund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_penalty`
--

DROP TABLE IF EXISTS `loan_penalty`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_penalty` (
  `LOAN_PENALTY_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) default NULL,
  `PENALTY_ID` smallint(6) NOT NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `PENALTY_TYPE` varchar(200) default NULL,
  `PENALTY_RATE` decimal(13,10) default NULL,
  PRIMARY KEY  (`LOAN_PENALTY_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `PENALTY_ID` (`PENALTY_ID`),
  CONSTRAINT `loan_penalty_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `loan_account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_penalty_ibfk_2` FOREIGN KEY (`PENALTY_ID`) REFERENCES `penalty` (`PENALTY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_penalty`
--

LOCK TABLES `loan_penalty` WRITE;
/*!40000 ALTER TABLE `loan_penalty` DISABLE KEYS */;
/*!40000 ALTER TABLE `loan_penalty` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_perf_history`
--

DROP TABLE IF EXISTS `loan_perf_history`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_perf_history` (
  `ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `NO_OF_PAYMENTS` smallint(6) default NULL,
  `NO_OF_MISSED_PAYMENTS` smallint(6) default NULL,
  `DAYS_IN_ARREARS` smallint(6) default NULL,
  `LOAN_MATURITY_DATE` date default NULL,
  PRIMARY KEY  (`ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  CONSTRAINT `loan_perf_history_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_perf_history`
--

LOCK TABLES `loan_perf_history` WRITE;
/*!40000 ALTER TABLE `loan_perf_history` DISABLE KEYS */;
INSERT INTO `loan_perf_history` VALUES (1,4,0,NULL,NULL,'2009-02-19'),(2,6,0,NULL,NULL,NULL),(3,8,0,NULL,NULL,'2009-12-03'),(4,12,0,NULL,NULL,'2009-02-19'),(5,14,0,NULL,NULL,NULL),(6,16,0,NULL,NULL,'2009-12-03'),(7,18,0,NULL,NULL,NULL),(8,22,0,NULL,NULL,'2009-02-19'),(9,24,0,NULL,NULL,NULL),(10,26,0,NULL,NULL,'2009-12-03'),(11,28,0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `loan_perf_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_schedule`
--

DROP TABLE IF EXISTS `loan_schedule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_schedule` (
  `ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `ACTION_DATE` date default NULL,
  `PRINCIPAL` decimal(10,3) NOT NULL,
  `PRINCIPAL_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST` decimal(10,3) NOT NULL,
  `INTEREST_CURRENCY_ID` smallint(6) default NULL,
  `PENALTY` decimal(10,3) NOT NULL,
  `PENALTY_CURRENCY_ID` smallint(6) default NULL,
  `MISC_FEES` decimal(10,3) default NULL,
  `MISC_FEES_CURRENCY_ID` smallint(6) default NULL,
  `MISC_FEES_PAID` decimal(10,3) default NULL,
  `MISC_FEES_PAID_CURRENCY_ID` smallint(6) default NULL,
  `MISC_PENALTY` decimal(10,3) default NULL,
  `MISC_PENALTY_CURRENCY_ID` smallint(6) default NULL,
  `MISC_PENALTY_PAID` decimal(10,3) default NULL,
  `MISC_PENALTY_PAID_CURRENCY_ID` smallint(6) default NULL,
  `PRINCIPAL_PAID` decimal(10,3) default NULL,
  `PRINCIPAL_PAID_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_PAID` decimal(10,3) default NULL,
  `INTEREST_PAID_CURRENCY_ID` smallint(6) default NULL,
  `PENALTY_PAID` decimal(10,3) default NULL,
  `PENALTY_PAID_CURRENCY_ID` smallint(6) default NULL,
  `PAYMENT_STATUS` smallint(6) NOT NULL,
  `INSTALLMENT_ID` smallint(6) NOT NULL,
  `PAYMENT_DATE` date default NULL,
  `PARENT_FLAG` smallint(6) default NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `PRINCIPAL_CURRENCY_ID` (`PRINCIPAL_CURRENCY_ID`),
  KEY `INTEREST_CURRENCY_ID` (`INTEREST_CURRENCY_ID`),
  KEY `PENALTY_CURRENCY_ID` (`PENALTY_CURRENCY_ID`),
  KEY `MISC_FEES_CURRENCY_ID` (`MISC_FEES_CURRENCY_ID`),
  KEY `MISC_FEES_PAID_CURRENCY_ID` (`MISC_FEES_PAID_CURRENCY_ID`),
  KEY `MISC_PENALTY_CURRENCY_ID` (`MISC_PENALTY_CURRENCY_ID`),
  KEY `PRINCIPAL_PAID_CURRENCY_ID` (`PRINCIPAL_PAID_CURRENCY_ID`),
  KEY `INTEREST_PAID_CURRENCY_ID` (`INTEREST_PAID_CURRENCY_ID`),
  KEY `PENALTY_PAID_CURRENCY_ID` (`PENALTY_PAID_CURRENCY_ID`),
  KEY `MISC_PENALTY_PAID_CURRENCY_ID` (`MISC_PENALTY_PAID_CURRENCY_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `loan_schedule_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_10` FOREIGN KEY (`INTEREST_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_11` FOREIGN KEY (`PENALTY_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_12` FOREIGN KEY (`MISC_PENALTY_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_13` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_2` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_3` FOREIGN KEY (`PRINCIPAL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_4` FOREIGN KEY (`INTEREST_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_5` FOREIGN KEY (`PENALTY_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_6` FOREIGN KEY (`MISC_FEES_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_7` FOREIGN KEY (`MISC_FEES_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_8` FOREIGN KEY (`MISC_PENALTY_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_schedule_ibfk_9` FOREIGN KEY (`PRINCIPAL_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=372 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_schedule`
--

LOCK TABLES `loan_schedule` WRITE;
/*!40000 ALTER TABLE `loan_schedule` DISABLE KEYS */;
INSERT INTO `loan_schedule` VALUES (1,4,3,NULL,'2008-12-11','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(2,4,3,NULL,'2008-12-18','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(3,4,3,NULL,'2008-12-25','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(4,4,3,NULL,'2009-01-01','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(5,4,3,NULL,'2009-01-08','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(6,4,3,NULL,'2009-01-15','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(7,4,3,NULL,'2009-01-22','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(8,4,3,NULL,'2009-01-29','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(9,4,3,NULL,'2009-02-05','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(10,4,3,NULL,'2009-02-12','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(11,4,3,NULL,'2009-02-19','182.000',2,'1.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(12,6,4,NULL,'2008-12-11','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(13,6,4,NULL,'2008-12-18','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(14,6,4,NULL,'2008-12-25','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(15,6,4,NULL,'2009-01-01','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(16,6,4,NULL,'2009-01-08','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(17,6,4,NULL,'2009-01-15','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(18,6,4,NULL,'2009-01-22','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(19,6,4,NULL,'2009-01-29','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(20,6,4,NULL,'2009-02-05','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(21,6,4,NULL,'2009-02-12','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(22,6,4,NULL,'2009-02-19','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(23,6,4,NULL,'2009-02-26','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(24,6,4,NULL,'2009-03-05','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(25,6,4,NULL,'2009-03-12','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(26,6,4,NULL,'2009-03-19','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(27,6,4,NULL,'2009-03-26','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(28,6,4,NULL,'2009-04-02','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(29,6,4,NULL,'2009-04-09','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(30,6,4,NULL,'2009-04-16','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(31,6,4,NULL,'2009-04-23','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(32,6,4,NULL,'2009-04-30','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(33,6,4,NULL,'2009-05-07','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(34,6,4,NULL,'2009-05-14','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(35,6,4,NULL,'2009-05-21','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(36,6,4,NULL,'2009-05-28','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(37,6,4,NULL,'2009-06-04','372.500',2,'26.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(38,8,5,NULL,'2008-12-11','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(39,8,5,NULL,'2008-12-18','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(40,8,5,NULL,'2008-12-25','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(41,8,5,NULL,'2009-01-01','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(42,8,5,NULL,'2009-01-08','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(43,8,5,NULL,'2009-01-15','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(44,8,5,NULL,'2009-01-22','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(45,8,5,NULL,'2009-01-29','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(46,8,5,NULL,'2009-02-05','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(47,8,5,NULL,'2009-02-12','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(48,8,5,NULL,'2009-02-19','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(49,8,5,NULL,'2009-02-26','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(50,8,5,NULL,'2009-03-05','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(51,8,5,NULL,'2009-03-12','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(52,8,5,NULL,'2009-03-19','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(53,8,5,NULL,'2009-03-26','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(54,8,5,NULL,'2009-04-02','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(55,8,5,NULL,'2009-04-09','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(56,8,5,NULL,'2009-04-16','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(57,8,5,NULL,'2009-04-23','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(58,8,5,NULL,'2009-04-30','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(59,8,5,NULL,'2009-05-07','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(60,8,5,NULL,'2009-05-14','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(61,8,5,NULL,'2009-05-21','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(62,8,5,NULL,'2009-05-28','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(63,8,5,NULL,'2009-06-04','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(64,8,5,NULL,'2009-06-11','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,27,NULL,NULL,0),(65,8,5,NULL,'2009-06-18','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,28,NULL,NULL,0),(66,8,5,NULL,'2009-06-25','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,29,NULL,NULL,0),(67,8,5,NULL,'2009-07-02','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,30,NULL,NULL,0),(68,8,5,NULL,'2009-07-09','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,31,NULL,NULL,0),(69,8,5,NULL,'2009-07-16','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,32,NULL,NULL,0),(70,8,5,NULL,'2009-07-23','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,33,NULL,NULL,0),(71,8,5,NULL,'2009-07-30','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,34,NULL,NULL,0),(72,8,5,NULL,'2009-08-06','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,35,NULL,NULL,0),(73,8,5,NULL,'2009-08-13','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,36,NULL,NULL,0),(74,8,5,NULL,'2009-08-20','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,37,NULL,NULL,0),(75,8,5,NULL,'2009-08-27','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,38,NULL,NULL,0),(76,8,5,NULL,'2009-09-03','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,39,NULL,NULL,0),(77,8,5,NULL,'2009-09-10','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,40,NULL,NULL,0),(78,8,5,NULL,'2009-09-17','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,41,NULL,NULL,0),(79,8,5,NULL,'2009-09-24','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,42,NULL,NULL,0),(80,8,5,NULL,'2009-10-01','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,43,NULL,NULL,0),(81,8,5,NULL,'2009-10-08','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,44,NULL,NULL,0),(82,8,5,NULL,'2009-10-15','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,45,NULL,NULL,0),(83,8,5,NULL,'2009-10-22','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,46,NULL,NULL,0),(84,8,5,NULL,'2009-10-29','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,47,NULL,NULL,0),(85,8,5,NULL,'2009-11-05','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,48,NULL,NULL,0),(86,8,5,NULL,'2009-11-12','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,49,NULL,NULL,0),(87,8,5,NULL,'2009-11-19','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,50,NULL,NULL,0),(88,8,5,NULL,'2009-11-26','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,51,NULL,NULL,0),(89,8,5,NULL,'2009-12-03','197.800',2,'27.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,52,NULL,NULL,0),(90,12,8,NULL,'2008-12-11','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(91,12,8,NULL,'2008-12-18','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(92,12,8,NULL,'2008-12-25','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(93,12,8,NULL,'2009-01-01','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(94,12,8,NULL,'2009-01-08','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(95,12,8,NULL,'2009-01-15','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(96,12,8,NULL,'2009-01-22','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(97,12,8,NULL,'2009-01-29','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(98,12,8,NULL,'2009-02-05','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(99,12,8,NULL,'2009-02-12','181.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(100,12,8,NULL,'2009-02-19','182.000',2,'1.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(101,14,9,NULL,'2008-12-11','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(102,14,9,NULL,'2008-12-18','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(103,14,9,NULL,'2008-12-25','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(104,14,9,NULL,'2009-01-01','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(105,14,9,NULL,'2009-01-08','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(106,14,9,NULL,'2009-01-15','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(107,14,9,NULL,'2009-01-22','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(108,14,9,NULL,'2009-01-29','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(109,14,9,NULL,'2009-02-05','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(110,14,9,NULL,'2009-02-12','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(111,14,9,NULL,'2009-02-19','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(112,14,9,NULL,'2009-02-26','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(113,14,9,NULL,'2009-03-05','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(114,14,9,NULL,'2009-03-12','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(115,14,9,NULL,'2009-03-19','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(116,14,9,NULL,'2009-03-26','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(117,14,9,NULL,'2009-04-02','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(118,14,9,NULL,'2009-04-09','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(119,14,9,NULL,'2009-04-16','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(120,14,9,NULL,'2009-04-23','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(121,14,9,NULL,'2009-04-30','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(122,14,9,NULL,'2009-05-07','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(123,14,9,NULL,'2009-05-14','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(124,14,9,NULL,'2009-05-21','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(125,14,9,NULL,'2009-05-28','385.100',2,'24.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(126,14,9,NULL,'2009-06-04','372.500',2,'26.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(127,16,10,NULL,'2008-12-11','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(128,16,10,NULL,'2008-12-18','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(129,16,10,NULL,'2008-12-25','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(130,16,10,NULL,'2009-01-01','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(131,16,10,NULL,'2009-01-08','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(132,16,10,NULL,'2009-01-15','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(133,16,10,NULL,'2009-01-22','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(134,16,10,NULL,'2009-01-29','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(135,16,10,NULL,'2009-02-05','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(136,16,10,NULL,'2009-02-12','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(137,16,10,NULL,'2009-02-19','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(138,16,10,NULL,'2009-02-26','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(139,16,10,NULL,'2009-03-05','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(140,16,10,NULL,'2009-03-12','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(141,16,10,NULL,'2009-03-19','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(142,16,10,NULL,'2009-03-26','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(143,16,10,NULL,'2009-04-02','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(144,16,10,NULL,'2009-04-09','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(145,16,10,NULL,'2009-04-16','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(146,16,10,NULL,'2009-04-23','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(147,16,10,NULL,'2009-04-30','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(148,16,10,NULL,'2009-05-07','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(149,16,10,NULL,'2009-05-14','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(150,16,10,NULL,'2009-05-21','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(151,16,10,NULL,'2009-05-28','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(152,16,10,NULL,'2009-06-04','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(153,16,10,NULL,'2009-06-11','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,27,NULL,NULL,0),(154,16,10,NULL,'2009-06-18','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,28,NULL,NULL,0),(155,16,10,NULL,'2009-06-25','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,29,NULL,NULL,0),(156,16,10,NULL,'2009-07-02','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,30,NULL,NULL,0),(157,16,10,NULL,'2009-07-09','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,31,NULL,NULL,0),(158,16,10,NULL,'2009-07-16','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,32,NULL,NULL,0),(159,16,10,NULL,'2009-07-23','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,33,NULL,NULL,0),(160,16,10,NULL,'2009-07-30','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,34,NULL,NULL,0),(161,16,10,NULL,'2009-08-06','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,35,NULL,NULL,0),(162,16,10,NULL,'2009-08-13','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,36,NULL,NULL,0),(163,16,10,NULL,'2009-08-20','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,37,NULL,NULL,0),(164,16,10,NULL,'2009-08-27','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,38,NULL,NULL,0),(165,16,10,NULL,'2009-09-03','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,39,NULL,NULL,0),(166,16,10,NULL,'2009-09-10','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,40,NULL,NULL,0),(167,16,10,NULL,'2009-09-17','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,41,NULL,NULL,0),(168,16,10,NULL,'2009-09-24','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,42,NULL,NULL,0),(169,16,10,NULL,'2009-10-01','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,43,NULL,NULL,0),(170,16,10,NULL,'2009-10-08','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,44,NULL,NULL,0),(171,16,10,NULL,'2009-10-15','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,45,NULL,NULL,0),(172,16,10,NULL,'2009-10-22','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,46,NULL,NULL,0),(173,16,10,NULL,'2009-10-29','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,47,NULL,NULL,0),(174,16,10,NULL,'2009-11-05','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,48,NULL,NULL,0),(175,16,10,NULL,'2009-11-12','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,49,NULL,NULL,0),(176,16,10,NULL,'2009-11-19','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,50,NULL,NULL,0),(177,16,10,NULL,'2009-11-26','192.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,51,NULL,NULL,0),(178,16,10,NULL,'2009-12-03','197.800',2,'27.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,52,NULL,NULL,0),(179,18,11,NULL,'2008-12-11','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(180,18,11,NULL,'2008-12-18','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(181,18,11,NULL,'2008-12-25','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(182,18,11,NULL,'2009-01-01','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(183,18,11,NULL,'2009-01-08','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(184,18,11,NULL,'2009-01-15','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(185,18,11,NULL,'2009-01-22','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(186,18,11,NULL,'2009-01-29','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(187,18,11,NULL,'2009-02-05','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(188,18,11,NULL,'2009-02-12','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(189,18,11,NULL,'2009-02-19','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(190,18,11,NULL,'2009-02-26','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(191,18,11,NULL,'2009-03-05','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(192,18,11,NULL,'2009-03-12','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(193,18,11,NULL,'2009-03-19','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(194,18,11,NULL,'2009-03-26','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(195,18,11,NULL,'2009-04-02','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(196,18,11,NULL,'2009-04-09','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(197,18,11,NULL,'2009-04-16','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(198,18,11,NULL,'2009-04-23','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(199,18,11,NULL,'2009-04-30','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(200,18,11,NULL,'2009-05-07','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(201,18,11,NULL,'2009-05-14','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(202,18,11,NULL,'2009-05-21','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(203,18,11,NULL,'2009-05-28','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(204,18,11,NULL,'2009-06-04','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(205,18,11,NULL,'2009-06-11','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,27,NULL,NULL,0),(206,18,11,NULL,'2009-06-18','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,28,NULL,NULL,0),(207,18,11,NULL,'2009-06-25','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,29,NULL,NULL,0),(208,18,11,NULL,'2009-07-02','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,30,NULL,NULL,0),(209,18,11,NULL,'2009-07-09','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,31,NULL,NULL,0),(210,18,11,NULL,'2009-07-16','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,32,NULL,NULL,0),(211,18,11,NULL,'2009-07-23','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,33,NULL,NULL,0),(212,18,11,NULL,'2009-07-30','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,34,NULL,NULL,0),(213,18,11,NULL,'2009-08-06','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,35,NULL,NULL,0),(214,18,11,NULL,'2009-08-13','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,36,NULL,NULL,0),(215,18,11,NULL,'2009-08-20','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,37,NULL,NULL,0),(216,18,11,NULL,'2009-08-27','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,38,NULL,NULL,0),(217,18,11,NULL,'2009-09-03','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,39,NULL,NULL,0),(218,18,11,NULL,'2009-09-10','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,40,NULL,NULL,0),(219,18,11,NULL,'2009-09-17','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,41,NULL,NULL,0),(220,18,11,NULL,'2009-09-24','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,42,NULL,NULL,0),(221,18,11,NULL,'2009-10-01','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,43,NULL,NULL,0),(222,18,11,NULL,'2009-10-08','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,44,NULL,NULL,0),(223,18,11,NULL,'2009-10-15','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,45,NULL,NULL,0),(224,18,11,NULL,'2009-10-22','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,46,NULL,NULL,0),(225,18,11,NULL,'2009-10-29','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,47,NULL,NULL,0),(226,18,11,NULL,'2009-11-05','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,48,NULL,NULL,0),(227,18,11,NULL,'2009-11-12','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,49,NULL,NULL,0),(228,18,11,NULL,'2009-11-19','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,50,NULL,NULL,0),(229,18,11,NULL,'2009-11-26','38.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,51,NULL,NULL,0),(230,18,11,NULL,'2009-12-03','36.500',2,'1.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,52,NULL,NULL,0),(231,22,14,NULL,'2008-12-11','180.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(232,22,14,NULL,'2008-12-18','181.000',2,'1.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(233,22,14,NULL,'2008-12-25','181.100',2,'0.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(234,22,14,NULL,'2009-01-01','181.200',2,'0.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(235,22,14,NULL,'2009-01-08','181.300',2,'0.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(236,22,14,NULL,'2009-01-15','181.400',2,'0.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(237,22,14,NULL,'2009-01-22','181.500',2,'0.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(238,22,14,NULL,'2009-01-29','181.600',2,'0.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(239,22,14,NULL,'2009-02-05','181.700',2,'0.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(240,22,14,NULL,'2009-02-12','181.800',2,'0.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(241,22,14,NULL,'2009-02-19','186.600',2,'0.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(242,24,15,NULL,'2008-12-11','363.900',2,'44.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(243,24,15,NULL,'2008-12-18','365.500',2,'42.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(244,24,15,NULL,'2008-12-25','367.100',2,'40.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(245,24,15,NULL,'2009-01-01','368.700',2,'39.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(246,24,15,NULL,'2009-01-08','370.400',2,'37.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(247,24,15,NULL,'2009-01-15','372.000',2,'36.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(248,24,15,NULL,'2009-01-22','373.600',2,'34.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(249,24,15,NULL,'2009-01-29','375.300',2,'32.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(250,24,15,NULL,'2009-02-05','376.900',2,'31.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(251,24,15,NULL,'2009-02-12','378.600',2,'29.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(252,24,15,NULL,'2009-02-19','380.300',2,'27.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(253,24,15,NULL,'2009-02-26','381.900',2,'26.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(254,24,15,NULL,'2009-03-05','383.600',2,'24.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(255,24,15,NULL,'2009-03-12','385.300',2,'22.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(256,24,15,NULL,'2009-03-19','387.000',2,'21.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(257,24,15,NULL,'2009-03-26','388.700',2,'19.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(258,24,15,NULL,'2009-04-02','390.400',2,'17.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(259,24,15,NULL,'2009-04-09','392.200',2,'15.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(260,24,15,NULL,'2009-04-16','393.900',2,'14.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(261,24,15,NULL,'2009-04-23','395.600',2,'12.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(262,24,15,NULL,'2009-04-30','397.400',2,'10.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(263,24,15,NULL,'2009-05-07','399.100',2,'8.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(264,24,15,NULL,'2009-05-14','400.900',2,'7.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(265,24,15,NULL,'2009-05-21','402.600',2,'5.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(266,24,15,NULL,'2009-05-28','404.400',2,'3.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(267,24,15,NULL,'2009-06-04','404.700',2,'2.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(268,26,16,NULL,'2008-12-11','178.200',2,'28.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(269,26,16,NULL,'2008-12-18','178.700',2,'28.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(270,26,16,NULL,'2008-12-25','179.300',2,'27.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(271,26,16,NULL,'2009-01-01','179.800',2,'27.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(272,26,16,NULL,'2009-01-08','180.300',2,'26.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(273,26,16,NULL,'2009-01-15','180.800',2,'26.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(274,26,16,NULL,'2009-01-22','181.300',2,'25.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(275,26,16,NULL,'2009-01-29','181.900',2,'25.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(276,26,16,NULL,'2009-02-05','182.400',2,'24.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(277,26,16,NULL,'2009-02-12','182.900',2,'24.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(278,26,16,NULL,'2009-02-19','183.400',2,'23.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(279,26,16,NULL,'2009-02-26','184.000',2,'23.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(280,26,16,NULL,'2009-03-05','184.500',2,'22.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(281,26,16,NULL,'2009-03-12','185.000',2,'22.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(282,26,16,NULL,'2009-03-19','185.600',2,'21.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(283,26,16,NULL,'2009-03-26','186.100',2,'20.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(284,26,16,NULL,'2009-04-02','186.600',2,'20.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(285,26,16,NULL,'2009-04-09','187.200',2,'19.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(286,26,16,NULL,'2009-04-16','187.700',2,'19.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(287,26,16,NULL,'2009-04-23','188.200',2,'18.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(288,26,16,NULL,'2009-04-30','188.800',2,'18.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(289,26,16,NULL,'2009-05-07','189.300',2,'17.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(290,26,16,NULL,'2009-05-14','189.900',2,'17.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(291,26,16,NULL,'2009-05-21','190.400',2,'16.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(292,26,16,NULL,'2009-05-28','191.000',2,'16.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(293,26,16,NULL,'2009-06-04','191.500',2,'15.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(294,26,16,NULL,'2009-06-11','192.100',2,'14.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,27,NULL,NULL,0),(295,26,16,NULL,'2009-06-18','192.600',2,'14.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,28,NULL,NULL,0),(296,26,16,NULL,'2009-06-25','193.200',2,'13.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,29,NULL,NULL,0),(297,26,16,NULL,'2009-07-02','193.700',2,'13.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,30,NULL,NULL,0),(298,26,16,NULL,'2009-07-09','194.300',2,'12.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,31,NULL,NULL,0),(299,26,16,NULL,'2009-07-16','194.900',2,'12.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,32,NULL,NULL,0),(300,26,16,NULL,'2009-07-23','195.400',2,'11.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,33,NULL,NULL,0),(301,26,16,NULL,'2009-07-30','196.000',2,'11.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,34,NULL,NULL,0),(302,26,16,NULL,'2009-08-06','196.600',2,'10.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,35,NULL,NULL,0),(303,26,16,NULL,'2009-08-13','197.100',2,'9.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,36,NULL,NULL,0),(304,26,16,NULL,'2009-08-20','197.700',2,'9.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,37,NULL,NULL,0),(305,26,16,NULL,'2009-08-27','198.300',2,'8.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,38,NULL,NULL,0),(306,26,16,NULL,'2009-09-03','198.800',2,'8.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,39,NULL,NULL,0),(307,26,16,NULL,'2009-09-10','199.400',2,'7.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,40,NULL,NULL,0),(308,26,16,NULL,'2009-09-17','200.000',2,'7.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,41,NULL,NULL,0),(309,26,16,NULL,'2009-09-24','200.600',2,'6.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,42,NULL,NULL,0),(310,26,16,NULL,'2009-10-01','201.100',2,'5.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,43,NULL,NULL,0),(311,26,16,NULL,'2009-10-08','201.700',2,'5.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,44,NULL,NULL,0),(312,26,16,NULL,'2009-10-15','202.300',2,'4.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,45,NULL,NULL,0),(313,26,16,NULL,'2009-10-22','202.900',2,'4.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,46,NULL,NULL,0),(314,26,16,NULL,'2009-10-29','203.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,47,NULL,NULL,0),(315,26,16,NULL,'2009-11-05','204.000',2,'3.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,48,NULL,NULL,0),(316,26,16,NULL,'2009-11-12','204.600',2,'2.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,49,NULL,NULL,0),(317,26,16,NULL,'2009-11-19','205.200',2,'1.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,50,NULL,NULL,0),(318,26,16,NULL,'2009-11-26','205.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,51,NULL,NULL,0),(319,26,16,NULL,'2009-12-03','223.400',2,'0.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,52,NULL,NULL,0),(320,28,17,NULL,'2008-12-11','36.500',2,'3.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,1,NULL,NULL,0),(321,28,17,NULL,'2008-12-18','36.600',2,'3.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,2,NULL,NULL,0),(322,28,17,NULL,'2008-12-25','36.700',2,'3.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,3,NULL,NULL,0),(323,28,17,NULL,'2009-01-01','36.700',2,'3.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,4,NULL,NULL,0),(324,28,17,NULL,'2009-01-08','36.800',2,'3.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,5,NULL,NULL,0),(325,28,17,NULL,'2009-01-15','36.900',2,'3.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,6,NULL,NULL,0),(326,28,17,NULL,'2009-01-22','36.900',2,'3.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,7,NULL,NULL,0),(327,28,17,NULL,'2009-01-29','37.000',2,'3.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,8,NULL,NULL,0),(328,28,17,NULL,'2009-02-05','37.100',2,'2.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,9,NULL,NULL,0),(329,28,17,NULL,'2009-02-12','37.100',2,'2.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,10,NULL,NULL,0),(330,28,17,NULL,'2009-02-19','37.200',2,'2.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,11,NULL,NULL,0),(331,28,17,NULL,'2009-02-26','37.300',2,'2.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,12,NULL,NULL,0),(332,28,17,NULL,'2009-03-05','37.300',2,'2.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,13,NULL,NULL,0),(333,28,17,NULL,'2009-03-12','37.400',2,'2.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,14,NULL,NULL,0),(334,28,17,NULL,'2009-03-19','37.400',2,'2.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,15,NULL,NULL,0),(335,28,17,NULL,'2009-03-26','37.500',2,'2.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,16,NULL,NULL,0),(336,28,17,NULL,'2009-04-02','37.600',2,'2.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,17,NULL,NULL,0),(337,28,17,NULL,'2009-04-09','37.600',2,'2.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,18,NULL,NULL,0),(338,28,17,NULL,'2009-04-16','37.700',2,'2.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,19,NULL,NULL,0),(339,28,17,NULL,'2009-04-23','37.800',2,'2.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,20,NULL,NULL,0),(340,28,17,NULL,'2009-04-30','37.800',2,'2.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,21,NULL,NULL,0),(341,28,17,NULL,'2009-05-07','37.900',2,'2.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,22,NULL,NULL,0),(342,28,17,NULL,'2009-05-14','38.000',2,'2.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,23,NULL,NULL,0),(343,28,17,NULL,'2009-05-21','38.000',2,'2.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,24,NULL,NULL,0),(344,28,17,NULL,'2009-05-28','38.100',2,'1.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,25,NULL,NULL,0),(345,28,17,NULL,'2009-06-04','38.200',2,'1.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,26,NULL,NULL,0),(346,28,17,NULL,'2009-06-11','38.200',2,'1.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,27,NULL,NULL,0),(347,28,17,NULL,'2009-06-18','38.300',2,'1.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,28,NULL,NULL,0),(348,28,17,NULL,'2009-06-25','38.400',2,'1.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,29,NULL,NULL,0),(349,28,17,NULL,'2009-07-02','38.400',2,'1.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,30,NULL,NULL,0),(350,28,17,NULL,'2009-07-09','38.500',2,'1.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,31,NULL,NULL,0),(351,28,17,NULL,'2009-07-16','38.600',2,'1.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,32,NULL,NULL,0),(352,28,17,NULL,'2009-07-23','38.600',2,'1.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,33,NULL,NULL,0),(353,28,17,NULL,'2009-07-30','38.700',2,'1.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,34,NULL,NULL,0),(354,28,17,NULL,'2009-08-06','38.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,35,NULL,NULL,0),(355,28,17,NULL,'2009-08-13','38.800',2,'1.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,36,NULL,NULL,0),(356,28,17,NULL,'2009-08-20','38.900',2,'1.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,37,NULL,NULL,0),(357,28,17,NULL,'2009-08-27','39.000',2,'1.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,38,NULL,NULL,0),(358,28,17,NULL,'2009-09-03','39.000',2,'1.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,39,NULL,NULL,0),(359,28,17,NULL,'2009-09-10','39.100',2,'0.900',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,40,NULL,NULL,0),(360,28,17,NULL,'2009-09-17','39.200',2,'0.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,41,NULL,NULL,0),(361,28,17,NULL,'2009-09-24','39.200',2,'0.800',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,42,NULL,NULL,0),(362,28,17,NULL,'2009-10-01','39.300',2,'0.700',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,43,NULL,NULL,0),(363,28,17,NULL,'2009-10-08','39.400',2,'0.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,44,NULL,NULL,0),(364,28,17,NULL,'2009-10-15','39.400',2,'0.600',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,45,NULL,NULL,0),(365,28,17,NULL,'2009-10-22','39.500',2,'0.500',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,46,NULL,NULL,0),(366,28,17,NULL,'2009-10-29','39.600',2,'0.400',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,47,NULL,NULL,0),(367,28,17,NULL,'2009-11-05','39.700',2,'0.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,48,NULL,NULL,0),(368,28,17,NULL,'2009-11-12','39.700',2,'0.300',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,49,NULL,NULL,0),(369,28,17,NULL,'2009-11-19','39.800',2,'0.200',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,50,NULL,NULL,0),(370,28,17,NULL,'2009-11-26','39.900',2,'0.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,51,NULL,NULL,0),(371,28,17,NULL,'2009-12-03','52.900',2,'0.100',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,0,52,NULL,NULL,0);
/*!40000 ALTER TABLE `loan_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_summary`
--

DROP TABLE IF EXISTS `loan_summary`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_summary` (
  `ACCOUNT_ID` int(11) NOT NULL,
  `ORIG_PRINCIPAL` decimal(10,3) default NULL,
  `ORIG_PRINCIPAL_CURRENCY_ID` smallint(6) default NULL,
  `ORIG_INTEREST` decimal(10,3) default NULL,
  `ORIG_INTEREST_CURRENCY_ID` smallint(6) default NULL,
  `ORIG_FEES` decimal(10,3) default NULL,
  `ORIG_FEES_CURRENCY_ID` smallint(6) default NULL,
  `ORIG_PENALTY` decimal(10,3) default NULL,
  `ORIG_PENALTY_CURRENCY_ID` smallint(6) default NULL,
  `PRINCIPAL_PAID` decimal(10,3) default NULL,
  `PRINCIPAL_PAID_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_PAID` decimal(10,3) default NULL,
  `INTEREST_PAID_CURRENCY_ID` smallint(6) default NULL,
  `FEES_PAID` decimal(10,3) default NULL,
  `FEES_PAID_CURRENCY_ID` smallint(6) default NULL,
  `PENALTY_PAID` decimal(10,3) default NULL,
  `PENALTY_PAID_CURRENCY_ID` smallint(6) default NULL,
  `RAW_AMOUNT_TOTAL` decimal(10,3) default NULL,
  `RAW_AMOUNT_TOTAL_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ACCOUNT_ID`),
  KEY `ORIG_PRINCIPAL_CURRENCY_ID` (`ORIG_PRINCIPAL_CURRENCY_ID`),
  KEY `ORIG_INTEREST_CURRENCY_ID` (`ORIG_INTEREST_CURRENCY_ID`),
  KEY `ORIG_FEES_CURRENCY_ID` (`ORIG_FEES_CURRENCY_ID`),
  KEY `ORIG_PENALTY_CURRENCY_ID` (`ORIG_PENALTY_CURRENCY_ID`),
  KEY `PRINCIPAL_PAID_CURRENCY_ID` (`PRINCIPAL_PAID_CURRENCY_ID`),
  KEY `INTEREST_PAID_CURRENCY_ID` (`INTEREST_PAID_CURRENCY_ID`),
  KEY `FEES_PAID_CURRENCY_ID` (`FEES_PAID_CURRENCY_ID`),
  KEY `PENALTY_PAID_CURRENCY_ID` (`PENALTY_PAID_CURRENCY_ID`),
  KEY `FK_LOAN_SUMMARY_RAW_AMOUNT_TOTAL` (`RAW_AMOUNT_TOTAL_CURRENCY_ID`),
  CONSTRAINT `FK_LOAN_SUMMARY_RAW_AMOUNT_TOTAL` FOREIGN KEY (`RAW_AMOUNT_TOTAL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_1` FOREIGN KEY (`ORIG_PRINCIPAL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_2` FOREIGN KEY (`ORIG_INTEREST_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_3` FOREIGN KEY (`ORIG_FEES_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_4` FOREIGN KEY (`ORIG_PENALTY_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_5` FOREIGN KEY (`PRINCIPAL_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_6` FOREIGN KEY (`INTEREST_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_7` FOREIGN KEY (`FEES_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_8` FOREIGN KEY (`PENALTY_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_summary_ibfk_9` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `loan_account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_summary`
--

LOCK TABLES `loan_summary` WRITE;
/*!40000 ALTER TABLE `loan_summary` DISABLE KEYS */;
INSERT INTO `loan_summary` VALUES (4,'2000.000',2,'13.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'12.700',2),(6,'10000.000',2,'649.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'648.200',2),(8,'10000.000',2,'1496.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'1495.900',2),(12,'2000.000',2,'13.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'12.700',2),(14,'10000.000',2,'649.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'648.200',2),(16,'10000.000',2,'1496.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'1495.900',2),(18,'2000.000',2,'180.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'179.500',2),(22,'2000.000',2,'7.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'6.900',2),(24,'10000.000',2,'607.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'606.400',2),(26,'10000.000',2,'781.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'780.900',2),(28,'2000.000',2,'93.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2,'92.800',2);
/*!40000 ALTER TABLE `loan_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_trxn_detail`
--

DROP TABLE IF EXISTS `loan_trxn_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `loan_trxn_detail` (
  `ACCOUNT_TRXN_ID` int(11) NOT NULL,
  `PRINCIPAL_AMOUNT` decimal(10,3) default NULL,
  `PRINCIPAL_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_AMOUNT` decimal(10,3) default NULL,
  `INTEREST_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `PENALTY_AMOUNT` decimal(10,3) default NULL,
  `PENALTY_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `MISC_FEE_AMOUNT` decimal(10,3) default NULL,
  `MISC_FEE_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `MISC_PENALTY_AMOUNT` decimal(10,3) default NULL,
  `MISC_PENALTY_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ACCOUNT_TRXN_ID`),
  KEY `PRINCIPAL_AMOUNT_CURRENCY_ID` (`PRINCIPAL_AMOUNT_CURRENCY_ID`),
  KEY `INTEREST_AMOUNT_CURRENCY_ID` (`INTEREST_AMOUNT_CURRENCY_ID`),
  KEY `PENALTY_AMOUNT_CURRENCY_ID` (`PENALTY_AMOUNT_CURRENCY_ID`),
  KEY `MISC_PENALTY_AMOUNT_CURRENCY_ID` (`MISC_PENALTY_AMOUNT_CURRENCY_ID`),
  KEY `MISC_FEE_AMOUNT_CURRENCY_ID` (`MISC_FEE_AMOUNT_CURRENCY_ID`),
  KEY `LOAN_ACCOUNT_TRXN_IDX` (`ACCOUNT_TRXN_ID`),
  CONSTRAINT `loan_trxn_detail_ibfk_1` FOREIGN KEY (`PRINCIPAL_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_trxn_detail_ibfk_2` FOREIGN KEY (`INTEREST_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_trxn_detail_ibfk_3` FOREIGN KEY (`PENALTY_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_trxn_detail_ibfk_4` FOREIGN KEY (`MISC_PENALTY_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_trxn_detail_ibfk_5` FOREIGN KEY (`MISC_FEE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `loan_trxn_detail_ibfk_6` FOREIGN KEY (`ACCOUNT_TRXN_ID`) REFERENCES `account_trxn` (`ACCOUNT_TRXN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `loan_trxn_detail`
--

LOCK TABLES `loan_trxn_detail` WRITE;
/*!40000 ALTER TABLE `loan_trxn_detail` DISABLE KEYS */;
INSERT INTO `loan_trxn_detail` VALUES (1,'2000.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2),(2,'10000.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2),(3,'2000.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2),(4,'10000.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2),(5,'2000.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2),(6,'10000.000',2,'0.000',2,'0.000',2,'0.000',2,'0.000',2);
/*!40000 ALTER TABLE `loan_trxn_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logmessages`
--

DROP TABLE IF EXISTS `logmessages`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `logmessages` (
  `LOG_ID` int(11) NOT NULL auto_increment,
  `LEVEL` varchar(20) default NULL,
  `MODULE_NAME` varchar(50) default NULL,
  `CLASS_NAME` varchar(50) default NULL,
  `METHOD_NAME` varchar(50) default NULL,
  `LINE_NUMBER` varchar(50) default NULL,
  `MESSAGE` text,
  `DATE` varchar(50) default NULL,
  `TIME` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `USERID` varchar(25) default NULL,
  `OFFICEID` varchar(25) default NULL,
  PRIMARY KEY  (`LOG_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `logmessages`
--

LOCK TABLES `logmessages` WRITE;
/*!40000 ALTER TABLE `logmessages` DISABLE KEYS */;
/*!40000 ALTER TABLE `logmessages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lookup_entity`
--

DROP TABLE IF EXISTS `lookup_entity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lookup_entity` (
  `ENTITY_ID` smallint(6) NOT NULL auto_increment,
  `ENTITY_NAME` varchar(100) NOT NULL,
  `DESCRIPTION` varchar(200) default NULL,
  PRIMARY KEY  (`ENTITY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `lookup_entity`
--

LOCK TABLES `lookup_entity` WRITE;
/*!40000 ALTER TABLE `lookup_entity` DISABLE KEYS */;
INSERT INTO `lookup_entity` VALUES (1,'ClientStatus','Client Status'),(2,'GroupStatus','Group Status'),(3,'CenterStatus','Center Status'),(4,'OfficeStatus','Office Status'),(5,'AccountState','Account States'),(6,'PersonnelStatusUnused','Personnel Status (Unused)'),(7,'GroupFlag','Group Flag'),(8,'FeeType','Fee Type'),(9,'Titles','Customer Position'),(10,'PovertyStatus','Poverty Status For Client'),(11,'Center','Center VALUES'),(12,'Group','Group VALUES'),(13,'Client','Client VALUES'),(14,'Office','Office'),(15,'Salutation','Mr/Mrs'),(16,'Gender','Male/Female'),(17,'MaritalStatus','Married/UnMarried'),(18,'Citizenship','Citizenship'),(19,'Ethinicity','Ethnicity'),(20,'EducationLevel','EducationLevel'),(21,'BusinessActivities','BusinessActivities'),(22,'Handicapped','Handicaped'),(23,'ClientFormedBy','CustomField ClientFormedBy for client'),(24,'PostalCode','ZipCode'),(25,'ProductState','Product State'),(26,'Loan','Loan'),(27,'Savings','Savings'),(29,'PersonnelTitles','CFO/Accountant'),(30,'PersonnelLevels','LoanOfficer/NonLoanOfficer'),(34,'OfficeLevels','Head Office/Regional Office/Sub Regional Office/Area Office/BranchOffice'),(35,'PrdApplicableMaster','Ceratin product categories applicable to certain types of clients'),(36,'WeekDays','Week Days List'),(37,'InterestTypes','Interest Types for PrdOfferings and Accounts'),(38,'CategoryType','This is mainly used in fees to show the categories where this fee is applicable'),(39,'InterestCalcRule','Interest calculation rule for loan prd offerings'),(41,'GracePeriodTypes','Grace Period Types for loan products'),(42,'DayRank','Day Rank'),(43,'CollateralTypes','Collateral Types for loan accounts'),(44,'OfficeCode','Office Code'),(45,'ProductCategoryStatus','ProductCategoryStatus'),(46,'ProductStatus','ProductStatus'),(47,'SavingsType','SavingsType'),(48,'RecommendedAmtUnit','RecommendedAmtUnit'),(49,'IntCalTypes','IntCalTypes'),(50,'YESNO','YESNO'),(51,'AccountType','AccountType'),(52,'SpouseFather','SpouseFather'),(53,'CustomerStatus','CustomerStatus'),(54,'FeePayment','FeePayment'),(55,'FeeFormulaMaster','FeeFormulaMaster'),(56,'PersonnelStatus','PersonnelStatus'),(57,'Personnel','Personnel'),(62,'ExternalId','External ID'),(68,'FeeStatus','FeeStatus'),(69,'AccountAction','AccountAction'),(70,'AccountFlags','AccountFlags'),(71,'PaymentType','PaymentType'),(72,'SavingsStatus','Saving Status'),(73,'Position','Position'),(74,'Language','Language'),(75,'CustomerAttendance','CustomerAttendance'),(76,'FinancialAction','Financial Action'),(77,'BulkEntry','BulkENtry'),(78,'SavingsAccountFlag','SavingsAccountFlag'),(79,'Address3','Address3'),(80,'City','City'),(81,'Interest','Interest'),(82,'LoanPurposes','Loan Purposes'),(83,'State','State'),(84,'Address1','Address1'),(85,'Address2','Address2'),(86,'GovernmentId','GovernmentId'),(87,'Permissions','Permissions'),(88,'ServiceCharge','Interest'),(89,'feeUpdationType',' fee updation can to applied to existing accounts or future accounts'),(90,'FeeFrequency','Fee Frequency'),(91,'RepaymentRule','Repayment Rule Types');
/*!40000 ALTER TABLE `lookup_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lookup_label`
--

DROP TABLE IF EXISTS `lookup_label`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lookup_label` (
  `LABEL_ID` int(11) NOT NULL auto_increment,
  `ENTITY_ID` smallint(6) default NULL,
  `LOCALE_ID` smallint(6) default NULL,
  `ENTITY_NAME` varchar(200) default NULL,
  PRIMARY KEY  (`LABEL_ID`),
  KEY `ENTITY_ID` (`ENTITY_ID`),
  KEY `LOCALE_ID` (`LOCALE_ID`),
  CONSTRAINT `lookup_label_ibfk_1` FOREIGN KEY (`ENTITY_ID`) REFERENCES `lookup_entity` (`ENTITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `lookup_label_ibfk_2` FOREIGN KEY (`LOCALE_ID`) REFERENCES `supported_locale` (`LOCALE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=173 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `lookup_label`
--

LOCK TABLES `lookup_label` WRITE;
/*!40000 ALTER TABLE `lookup_label` DISABLE KEYS */;
INSERT INTO `lookup_label` VALUES (1,1,1,NULL),(3,2,1,NULL),(5,3,1,NULL),(7,4,1,NULL),(9,5,1,NULL),(11,6,1,NULL),(13,7,1,NULL),(15,8,1,NULL),(17,9,1,NULL),(19,10,1,NULL),(21,11,1,NULL),(23,12,1,NULL),(25,13,1,NULL),(27,14,1,NULL),(29,15,1,NULL),(31,16,1,NULL),(33,17,1,NULL),(35,18,1,NULL),(37,19,1,NULL),(39,20,1,NULL),(41,21,1,NULL),(43,22,1,NULL),(47,24,1,NULL),(49,25,1,NULL),(51,26,1,NULL),(53,27,1,NULL),(57,29,1,NULL),(59,30,1,NULL),(67,34,1,NULL),(69,35,1,NULL),(71,36,1,NULL),(73,42,1,NULL),(75,37,1,NULL),(76,38,1,NULL),(77,39,1,NULL),(79,41,1,NULL),(80,43,1,NULL),(81,44,1,NULL),(83,45,1,NULL),(85,46,1,NULL),(87,47,1,NULL),(89,48,1,NULL),(91,49,1,NULL),(93,50,1,NULL),(95,51,1,NULL),(97,52,1,NULL),(99,53,1,NULL),(100,54,1,NULL),(102,55,1,NULL),(104,56,1,NULL),(106,57,1,NULL),(116,62,1,NULL),(128,68,1,NULL),(130,69,1,NULL),(132,70,1,NULL),(134,71,1,NULL),(136,72,1,NULL),(151,74,1,NULL),(154,75,1,NULL),(156,76,1,NULL),(158,77,1,NULL),(160,79,1,NULL),(162,80,1,NULL),(164,81,1,NULL),(166,82,1,NULL),(167,83,1,NULL),(168,84,1,NULL),(169,85,1,NULL),(170,86,1,NULL),(171,87,1,NULL),(172,88,1,NULL);
/*!40000 ALTER TABLE `lookup_label` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lookup_value`
--

DROP TABLE IF EXISTS `lookup_value`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lookup_value` (
  `LOOKUP_ID` int(11) NOT NULL auto_increment,
  `ENTITY_ID` smallint(6) default NULL,
  `LOOKUP_NAME` varchar(100) default NULL,
  PRIMARY KEY  (`LOOKUP_ID`),
  UNIQUE KEY `LOOKUP_NAME_IDX` (`LOOKUP_NAME`),
  KEY `LOOKUP_VALUE_IDX` (`ENTITY_ID`),
  CONSTRAINT `lookup_value_ibfk_1` FOREIGN KEY (`ENTITY_ID`) REFERENCES `lookup_entity` (`ENTITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=616 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `lookup_value`
--

LOCK TABLES `lookup_value` WRITE;
/*!40000 ALTER TABLE `lookup_value` DISABLE KEYS */;
INSERT INTO `lookup_value` VALUES (1,1,'ClientStatus-PartialApplication'),(2,1,'ClientStatus-ApplicationPendingApproval'),(3,1,'ClientStatus-Active'),(4,1,'ClientStatus-OnHold'),(5,1,'ClientStatus-Cancelled'),(6,1,'ClientStatus-Closed'),(7,2,'GroupStatus-PartialApplication'),(8,2,'GroupStatus-ApplicationPendingApproval'),(9,2,'GroupStatus-Active'),(10,2,'GroupStatus-OnHold'),(11,2,'GroupStatus-Cancelled'),(12,2,'GroupStatus-Closed'),(13,3,'CenterStatus-Active'),(14,3,'CenterStatus-Inactive'),(15,4,'OfficeStatus-Active'),(16,4,'OfficeStatus-Inactive'),(17,5,'AccountState-PartialApplication'),(18,5,'AccountState-ApplicationPendingApproval'),(19,5,'AccountState-ApplicationApproved'),(20,5,'AccountState-DisbursedToLo'),(21,5,'AccountState-ActiveInGoodStanding'),(22,5,'AccountState-ClosedObligationMet'),(23,5,'AccountState-ClosedWrittenOff'),(24,5,'AccountState-ClosedRescheduled'),(25,5,'AccountState-ActiveInBadStanding'),(26,6,'PersonnelStatusUnused-Active'),(27,6,'PersonnelStatusUnused-Inactive'),(28,7,'GroupFlag-Withdraw'),(29,7,'GroupFlag-Rejected'),(30,7,'GroupFlag-Blacklisted'),(31,7,'GroupFlag-Duplicate'),(32,7,'GroupFlag-Transferred'),(33,7,'GroupFlag-LeftProgram'),(34,7,'GroupFlag-Other'),(35,8,'FeeType-MaintenanceFee'),(36,8,'FeeType-ConsultancyFee'),(37,8,'FeeType-TrainingFee'),(38,8,'FeeType-MeetingCharges'),(41,10,'PovertyStatus-VeryPoor'),(42,10,'PovertyStatus-Poor'),(43,10,'PovertyStatus-NonPoor'),(49,16,'Gender-Male'),(50,16,'Gender-Female'),(51,25,'ProductState-Active'),(52,25,'ProductState-Inactive'),(53,25,'ProductState-Close'),(54,26,'Loan-Loan'),(55,27,'Savings-Savings'),(60,30,'PersonnelLevels-LoanOfficer'),(61,30,'PersonnelLevels-NonLoanOfficer'),(68,35,'PrdApplicableMaster-Clients'),(69,35,'PrdApplicableMaster-Groups'),(70,35,'PrdApplicableMaster-Centers'),(72,36,'WeekDays-Sunday'),(73,36,'WeekDays-Monday'),(74,36,'WeekDays-Tuesday'),(75,36,'WeekDays-Wednesday'),(76,36,'WeekDays-Thursday'),(77,36,'WeekDays-Friday'),(78,36,'WeekDays-Saturday'),(79,37,'InterestTypes-Flat'),(80,37,'InterestTypes-DecliningBalance'),(81,38,'CategoryType-AllCustomers'),(82,38,'CategoryType-Client'),(83,38,'CategoryType-Group'),(84,38,'CategoryType-Center'),(85,38,'CategoryType-AllProductTypes'),(86,38,'CategoryType-Loans'),(87,38,'CategoryType-Savings'),(88,39,'InterestCalcRule-AlwaysRecalculate'),(89,39,'InterestCalcRule-NeverRecalculate'),(96,41,'GracePeriodTypes-None'),(97,41,'GracePeriodTypes-GraceOnAllRepayments'),(98,41,'GracePeriodTypes-PrincipalOnlyGrace'),(99,42,'DayRank-First'),(100,42,'DayRank-Second'),(101,42,'DayRank-Third'),(102,42,'DayRank-Fourth'),(103,42,'DayRank-Last'),(104,34,'OfficeLevels-HeadOffice'),(105,34,'OfficeLevels-RegionalOffice'),(106,34,'OfficeLevels-DivisionalOffice'),(107,34,'OfficeLevels-AreaOffice'),(108,34,'OfficeLevels-BranchOffice'),(111,44,'OfficeCode-Code1'),(112,44,'OfficeCode-Code2'),(113,45,'ProductCategoryStatus-Inactive'),(114,45,'ProductCategoryStatus-Active'),(115,46,'ProductStatus-Active'),(116,46,'ProductStatus-Inactive'),(117,46,'ProductStatus-Closed'),(118,47,'SavingsType-Mandatory'),(119,47,'SavingsType-Voluntary'),(120,48,'RecommendedAmtUnit-PerIndividual'),(121,48,'RecommendedAmtUnit-CompleteGroup'),(122,49,'IntCalTypes-MinimumBalance'),(123,49,'IntCalTypes-AverageBalance'),(124,50,'YESNO-Yes'),(125,50,'YESNO-No'),(126,51,'AccountType-Loan'),(127,51,'AccountType-Saving'),(128,52,'SpouseFather-Spouse'),(129,52,'SpouseFather-Father'),(140,51,'AccountType-Customer'),(141,5,'AccountState-Cancel'),(142,53,'CustomerStatus-CustomerAccountActive'),(143,53,'CustomerStatus-CustomerAccountInactive'),(146,54,'FeePayment-Upfront'),(147,54,'FeePayment-TimeOfDisburstment'),(148,54,'FeePayment-TimeOfFirstLoanRepayment'),(149,55,'FeeFormulaMaster-LoanAmount'),(150,55,'FeeFormulaMaster-LoanAmountInterest'),(151,55,'FeeFormulaMaster-Interest'),(152,56,'PersonnelStatus-Active'),(153,56,'PersonnelStatus-Inactive'),(154,57,'Personnel-Personnel'),(165,68,'FeeStatus-Active'),(166,68,'FeeStatus-Inactive'),(167,69,'AccountAction-LoanRepayment'),(168,69,'AccountAction-Penalty'),(169,69,'AccountAction-MiscellenousPenalty'),(170,69,'AccountAction-Fee'),(171,69,'AccountAction-MiscellenousFee'),(172,69,'AccountAction-Deposit'),(173,69,'AccountAction-Withdrawal'),(174,70,'AccountFlags-Withdraw'),(175,70,'AccountFlags-Rejected'),(176,70,'AccountFlags-Other'),(177,71,'PaymentType-Cash'),(179,71,'PaymentType-Voucher'),(180,71,'PaymentType-Cheque'),(181,72,'SavingsStatus-PartialApplication'),(182,72,'SavingsStatus-ApplicationPendingApproval'),(183,72,'SavingsStatus-Cancelled'),(184,72,'SavingsStatus-Active'),(185,72,'SavingsStatus-Closed'),(186,73,'Position-CenterLeader'),(187,73,'Position-CenterSecretary'),(188,73,'Position-GroupLeader'),(189,74,'Language-English'),(191,69,'AccountAction-Payment'),(192,69,'AccountAction-Adjustment'),(193,69,'AccountAction-Disbursal'),(194,75,'CustomerAttendance-P'),(195,75,'CustomerAttendance-Ab'),(196,75,'CustomerAttendance-Al'),(197,75,'CustomerAttendance-L'),(198,76,'FinancialAction-Principal'),(199,76,'FinancialAction-Interest'),(200,76,'FinancialAction-Fees'),(201,76,'FinancialAction-Penalty'),(202,76,'FinancialAction-RoundingAdjustments'),(203,76,'FinancialAction-MandatoryDeposit'),(204,76,'FinancialAction-VoluntoryDeposit'),(205,76,'FinancialAction-MandatoryWithdrawal'),(206,76,'FinancialAction-VoluntoryWithdrawal'),(207,76,'FinancialAction-ReversalAdjustment'),(208,76,'FinancialAction-SavingsInterestPosting'),(209,76,'FinancialAction-Interest_posting'),(210,72,'SavingsStatus-Inactive'),(211,78,'SavingsAccountFlag-Withdraw'),(212,78,'SavingsAccountFlag-Rejected'),(213,78,'SavingsAccountFlag-Blacklisted'),(214,69,'AccountAction-Interest_posting'),(215,76,'FinancialAction-LoanDisbursement'),(216,73,'Position-GroupSecretary'),(229,76,'FinancialAction-MiscFee'),(361,76,'FinancialAction-MiscPenalty'),(362,69,'AccountAction-CustomerAccountRepayment'),(363,76,'FinancialAction-CustomerAccountFeesPosting'),(364,69,'AccountAction-CustomerAdjustment'),(365,76,'FinancialAction-CustomerAdjustment'),(366,69,'AccountAction-SavingsAdjustment'),(367,76,'FinancialAction-MandatoryDepositAdjustment'),(368,76,'FinancialAction-VoluntoryDepositAdjustment'),(369,76,'FinancialAction-MandatoryWithdrawalAdjustment'),(370,76,'FinancialAction-VoluntoryWithdrawalAdjustment'),(371,87,'Permissions-OrganizationManagement'),(372,87,'Permissions-Funds'),(373,87,'Permissions-CanCreateFunds'),(374,87,'Permissions-CanModifyFunds'),(375,87,'Permissions-Fees'),(376,87,'Permissions-CanDefineNewFeeType'),(377,87,'Permissions-CanModifyFeeInformation'),(378,87,'Permissions-Checklists'),(379,87,'Permissions-CanDefineNewChecklistType'),(380,87,'Permissions-CanModifyChecklistInformation'),(381,87,'Permissions-OfficeManagement'),(382,87,'Permissions-Offices'),(383,87,'Permissions-CanCreateNewOffice'),(384,87,'Permissions-CanModifyOfficeInformation'),(385,87,'Permissions-UserManagement'),(386,87,'Permissions-Personnel'),(387,87,'Permissions-CanCreateNewSystemUsers'),(388,87,'Permissions-CanModifyUserInformation'),(389,87,'Permissions-CanUnlockAUser'),(390,87,'Permissions-Roles'),(391,87,'Permissions-CanCreateNewRole'),(392,87,'Permissions-CanModifyARole'),(393,87,'Permissions-CanDeleteARole'),(394,87,'Permissions-ClientManagement'),(395,87,'Permissions-Clients'),(396,87,'Permissions-Clients-CanCreateNewClientInSaveForLaterState'),(397,87,'Permissions-Clients-CanCreateNewClientInSubmitForApprovalState'),(398,87,'Permissions-Clients-CanChangeStateToPartialApplication'),(399,87,'Permissions-Clients-CanChangeStateToActive'),(400,87,'Permissions-Clients-CanChangeStateToCancelled'),(401,87,'Permissions-Clients-CanChangeStateToOnHold'),(402,87,'Permissions-Clients-CanChangeStateToClosed'),(403,87,'Permissions-Clients-CanChangeStateToApplicationPendingApproval'),(404,87,'Permissions-Clients-CanMakePaymentsToClientAccounts'),(405,87,'Permissions-Clients-CanMakeAdjustmentEntriesToClientAccount'),(407,87,'Permissions-Clients-CanWaiveADueAmount'),(408,87,'Permissions-Clients-CanRemoveFeeTypesFromClientAccount'),(409,87,'Permissions-Clients-CanAddNotesToClient'),(410,87,'Permissions-Clients-CanEditMfiInformation'),(411,87,'Permissions-Clients-CanEditGroupMembership'),(412,87,'Permissions-Clients-CanEditOfficeMembership'),(413,87,'Permissions-Clients-CanEditMeetingSchedule'),(414,87,'Permissions-Clients-CanAddEditHistoricalData'),(415,87,'Permissions-Clients-CanEditFeeAmountAttachedToTheAccount'),(416,87,'Permissions-Clients-CanBlacklistAClient'),(417,87,'Permissions-Groups'),(418,87,'Permissions-Groups-CanCreateNewGroupInSaveForLaterState'),(419,87,'Permissions-Groups-CanCreateNewGroupInSubmitForApprovalState'),(420,87,'Permissions-Groups-CanChangeStateToPartialApplication'),(421,87,'Permissions-Groups-CanChangeStateToActive'),(422,87,'Permissions-Groups-CanChangeStateToCancelled'),(423,87,'Permissions-Groups-CanChangeStateToOnHold'),(424,87,'Permissions-Groups-CanChangeStateToClosed'),(425,87,'Permissions-Groups-CanChangeStateToApplicationPendingApproval'),(426,87,'Permissions-Groups-CanMakePaymentsToGroupAccounts'),(427,87,'Permissions-Groups-CanMakeAdjustmentEntriesToGroupAccount'),(429,87,'Permissions-Groups-CanWaiveADueAmount'),(430,87,'Permissions-Groups-CanRemoveFeeTypesFromGroupAccount'),(431,87,'Permissions-Groups-CanAddNotesToGroup'),(432,87,'Permissions-Groups-CanEditGroupInformation'),(433,87,'Permissions-Groups-CanEditCenterClientship'),(434,87,'Permissions-Groups-CanEditOfficeMembership'),(435,87,'Permissions-Groups-CanEditMeetingSchedule'),(436,87,'Permissions-Groups-CanAddEditHistoricalData'),(437,87,'Permissions-Groups-CanEditFeeAmountAttachedToTheAccount'),(438,87,'Permissions-Groups-CanBlacklistAGroup'),(439,87,'Permissions-Centers'),(440,87,'Permissions-Centers-CanCreateNewCenter'),(441,87,'Permissions-Centers-CanModifyCenterInformation'),(442,87,'Permissions-Centers-CanEditCenterStatus'),(443,87,'Permissions-Centers-CanMakePaymentsToCenterAccounts'),(444,87,'Permissions-Centers-CanMakeAdjustmentEntriesToCenterAccount'),(446,87,'Permissions-Centers-CanWaiveADueAmount'),(447,87,'Permissions-Centers-CanRemoveFeeTypesFromCenterAccount'),(448,87,'Permissions-Centers-CanAddNotesToCenterRecords'),(449,87,'Permissions-Centers-CanEditFeeAmountAttachedToTheAccount'),(450,87,'Permissions-ProductDefinition'),(451,87,'Permissions-ProductCategories'),(452,87,'Permissions-CanDefineNewProductCategories'),(453,87,'Permissions-CanEditProductCategoryInformation'),(454,87,'Permissions-LoanProducts'),(455,87,'Permissions-CanDefineNewLoanProductInstance'),(456,87,'Permissions-CanEditLoanProductInstances'),(457,87,'Permissions-SavingsProducts'),(458,87,'Permissions-CanDefineNewSavingsProductInstance'),(459,87,'Permissions-CanEditSavingsProductInstances'),(460,87,'Permissions-LoanManagement'),(461,87,'Permissions-LoanProcessing'),(462,87,'Permissions-CanCreateNewLoanAccountInSaveForLaterState'),(463,87,'Permissions-CanCreateNewLoanAccountInSubmitForApprovalState'),(464,87,'Permissions-LoanProcessing-CanChangeStateToPartialApplication'),(465,87,'Permissions-LoanProcessing-CanChangeStateToApproved'),(466,87,'Permissions-LoanProcessing-CanChangeStateToCancelled'),(467,87,'Permissions-LoanProcessing-CanChangeStateToDisbursedToLo'),(469,87,'Permissions-LoanProcessing-CanChangeStateToPendingApproval'),(470,87,'Permissions-LoanProcessing-CanChangeStateToClosedWrittenOff'),(471,87,'Permissions-LoanProcessing-CanChangeStateToClosedRescheduled'),(474,87,'Permissions-LoanTransactions'),(475,87,'Permissions-CanMakePaymentToTheAccount'),(476,87,'Permissions-CanMakeAdjustmentEntryToTheAccount'),(478,87,'Permissions-CanWaivePenalty'),(479,87,'Permissions-CanWaiveAFeeInstallment'),(480,87,'Permissions-CanRemoveFeeTypesAttachedToTheAccount'),(481,87,'Permissions-Clients-CanSpecifyMeetingSchedule'),(482,87,'Permissions-Groups-CanSpecifyMeetingSchedule'),(483,87,'Permissions-Clients-CanEditPersonalInformation'),(484,87,'Permissions-Centers-CanEditMeetingSchedule'),(485,87,'Permissions-Centers-CanSpecifyMeetingSchedule'),(486,87,'Permissions-CanEditLoanAccountInformation'),(487,87,'Permissions-CanApplyChargesToLoans'),(488,87,'Permissions-CanEditSelfInformation'),(489,87,'Permissions-SavingsManagement'),(490,87,'Permissions-CanCreateNewSavingsAccountInSaveForLaterState'),(491,87,'Permissions-CanUpdateSavingsAccount'),(492,87,'Permissions-CanCloseSavingsAccount'),(493,87,'Permissions-SavingsManagement-CanChangeStateToPartialApplication'),(494,87,'Permissions-ReportsManagement'),(495,87,'Permissions-CanAdministerReports'),(496,87,'Permissions-CanPreviewReports'),(497,87,'Permissions-CanUploadNewReports'),(498,87,'Permissions-ClientDetail'),(499,87,'Permissions-Center'),(500,87,'Permissions-Status'),(501,87,'Permissions-Performance'),(502,87,'Permissions-LoanProductDetail'),(503,87,'Permissions-Analysis'),(504,87,'Permissions-Miscellaneous'),(531,87,'Permissions-CanRepayLoan'),(532,87,'Permissions-CanAddNotesToLoanAccount'),(533,87,'Permissions-SavingsManagement-CanChangeStateToPendingApproval'),(534,87,'Permissions-SavingsManagement-CanChangeStateToCancel'),(535,87,'Permissions-SavingsManagement-CanChangeStateToActive'),(536,87,'Permissions-SavingsManagement-CanChangeStateToInactive'),(537,87,'Permissions-CanBlacklistSavingsAccount'),(538,87,'Permissions-CanCreateNewSavingsAccountInSubmitForApprovalState'),(539,87,'Permissions-NotImplemented'),(546,87,'Permissions-CanDoAdjustmentsForSavingsAccount'),(547,69,'AccountAction-LoanWrittenOff'),(548,69,'AccountAction-WaiveOffDue'),(549,69,'AccountAction-WaiveOffOverDue'),(550,76,'FinancialAction-LoanWrittenOff'),(551,87,'Permissions-CanWaiveDueDepositsForSavingsAccount'),(552,87,'Permissions-CanWaiveOverDueDepositsForSavingsAccount'),(553,87,'Permissions-CanDisburseLoan'),(554,87,'Permissions-CanMakeDepositWithdrawalToSavingsAccount'),(555,87,'Permissions-CanAddNotesToSavingsAccount'),(556,89,'feeUpdationType-AppliesToExistingFutureAccounts'),(557,89,'feeUpdationType-AppliesToFutureAccounts'),(558,90,'FeeFrequency-Periodic'),(559,90,'FeeFrequency-OneTime'),(560,87,'Permissions-CanApproveLoansInBulk'),(561,87,'Permissions-CanModifyLatenessDormancyDefinition'),(562,87,'Permissions-CanModifyOfficeHierarchy'),(563,87,'Permissions-CanAddNotesToPersonnel'),(564,87,'Permissions-Bulk'),(565,87,'Permissions-CanEnterCollectionSheetData'),(566,87,'Permissions-Clients-CanApplyChargesToClientAccounts'),(567,87,'Permissions-Groups-CanApplyChargesToGroupAccounts'),(568,87,'Permissions-Centers-CanApplyChargesToCenterAccounts'),(569,87,'Permissions-CanCreateMultipleLoanAccounts'),(570,87,'Permissions-CanReverseLoanDisbursals'),(571,70,'AccountFlags-LoanReversal'),(572,69,'AccountAction-LoanReversal'),(573,69,'AccountAction-DisrbursalAmountReversal'),(574,87,'Permissions-ConfigurationManagement'),(575,87,'Permissions-CanDefineLabels'),(576,91,'RepaymentRule-SameDay'),(577,91,'RepaymentRule-NextMeetingRepayment'),(578,91,'RepaymentRule-NextWorkingDay'),(579,87,'Permissions-CanDefineHiddenMandatoryFields'),(580,87,'Permissions-Clients-CanRemoveClientsFromGroups'),(581,87,'Permissions-CanViewDetailedAgingOfPortfolioAtRisk'),(582,87,'Permissions-Clients-CanAddAnExistingClientToAGroup'),(583,87,'Permissions-ProductMix'),(584,87,'Permissions-CanDefineProductMix'),(585,87,'Permissions-CanEditProductMix'),(586,87,'Permissions-CanViewActiveLoansByLoanOfficer'),(587,87,'Permissions-CanDefineLookupValues'),(588,87,'Permissions-CanUploadReportTemplate'),(589,87,'Permissions-CanViewReports'),(590,87,'Permissions-CanEditReportInformation'),(591,87,'Permissions-CanAdjustPaymentWhenAccountStatusIsClosedObligationMet'),(592,87,'Permissions-CanRedoLoanDisbursals'),(593,87,'Permissions-CanDefineAcceptedPaymentType'),(594,87,'Permissions-CanDefineNewReportCategory'),(595,87,'Permissions-CanViewReportCategory'),(596,87,'Permissions-CanDeleteReportCategory'),(597,87,'Permissions-CanDownloadReportTemplate'),(598,87,'Permissions-CanDefineCustomFields'),(599,74,'Language-Icelandic'),(600,74,'Language-Spanish'),(601,74,'Language-French'),(602,87,'Permissions-CanUploadAdminDocuments'),(603,87,'Permissions-CanViewAdminDocuments'),(604,37,'InterestTypes-DecliningBalance-EqualPrincipalInstallment'),(605,87,'Permissions-SystemInformation'),(606,87,'Permissions-CanViewSystemInformation'),(607,87,'Permissions-CanViewCollectionSheetReport'),(608,87,'Permissions-CanViewOrganizationSettings'),(609,76,'FinancialAction-LoanRescheduled'),(610,69,'AccountAction-LoanRescheduled'),(611,87,'Permissions-CanViewBranchCashConfirmationReport'),(612,87,'Permissions-CanViewBranchProgressReport'),(613,15,'LookUpOption.Ms1228423252312.1228423389392'),(614,15,'LookUpOption.Ms1228423681546.1228423817019'),(615,15,'LookUpOption.Ms1228424238375.1228424375488');
/*!40000 ALTER TABLE `lookup_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lookup_value_locale`
--

DROP TABLE IF EXISTS `lookup_value_locale`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lookup_value_locale` (
  `LOOKUP_VALUE_ID` int(11) NOT NULL auto_increment,
  `LOCALE_ID` smallint(6) NOT NULL,
  `LOOKUP_ID` int(11) NOT NULL,
  `LOOKUP_VALUE` varchar(300) default NULL,
  PRIMARY KEY  (`LOOKUP_VALUE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  KEY `LOCALE_ID` (`LOCALE_ID`),
  CONSTRAINT `lookup_value_locale_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `lookup_value_locale_ibfk_2` FOREIGN KEY (`LOCALE_ID`) REFERENCES `supported_locale` (`LOCALE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=958 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `lookup_value_locale`
--

LOCK TABLES `lookup_value_locale` WRITE;
/*!40000 ALTER TABLE `lookup_value_locale` DISABLE KEYS */;
INSERT INTO `lookup_value_locale` VALUES (1,1,1,NULL),(3,1,2,NULL),(5,1,3,NULL),(7,1,4,NULL),(9,1,5,NULL),(11,1,6,NULL),(13,1,7,NULL),(15,1,8,NULL),(17,1,9,NULL),(19,1,10,NULL),(21,1,11,NULL),(23,1,12,NULL),(25,1,13,NULL),(27,1,14,NULL),(29,1,15,NULL),(31,1,16,NULL),(33,1,17,NULL),(35,1,18,NULL),(37,1,19,NULL),(39,1,20,NULL),(41,1,21,NULL),(43,1,22,NULL),(45,1,23,NULL),(47,1,24,NULL),(49,1,25,NULL),(51,1,26,NULL),(53,1,27,NULL),(55,1,28,NULL),(57,1,29,NULL),(59,1,30,NULL),(61,1,31,NULL),(63,1,32,NULL),(65,1,33,NULL),(67,1,34,NULL),(69,1,35,NULL),(71,1,36,NULL),(73,1,37,NULL),(75,1,38,NULL),(81,1,41,NULL),(83,1,42,NULL),(85,1,43,NULL),(97,1,49,NULL),(99,1,50,NULL),(101,1,51,NULL),(103,1,52,NULL),(105,1,53,NULL),(107,1,54,NULL),(109,1,55,NULL),(119,1,60,NULL),(120,1,61,NULL),(135,1,68,NULL),(136,1,69,NULL),(137,1,70,NULL),(143,1,72,NULL),(145,1,73,NULL),(147,1,74,NULL),(149,1,75,NULL),(151,1,76,NULL),(153,1,77,NULL),(155,1,78,NULL),(157,1,79,NULL),(158,1,80,NULL),(161,1,81,NULL),(162,1,82,NULL),(163,1,83,NULL),(164,1,84,NULL),(165,1,85,NULL),(166,1,86,NULL),(167,1,87,NULL),(168,1,88,NULL),(169,1,89,NULL),(176,1,96,NULL),(177,1,97,NULL),(178,1,98,NULL),(179,1,99,NULL),(181,1,100,NULL),(183,1,101,NULL),(185,1,102,NULL),(187,1,103,NULL),(189,1,104,NULL),(191,1,105,NULL),(193,1,106,NULL),(195,1,107,NULL),(197,1,108,NULL),(201,1,111,NULL),(203,1,112,NULL),(205,1,113,NULL),(207,1,114,NULL),(209,1,115,NULL),(211,1,116,NULL),(213,1,117,NULL),(215,1,118,NULL),(217,1,119,NULL),(219,1,120,NULL),(221,1,121,NULL),(223,1,122,NULL),(225,1,123,NULL),(227,1,124,NULL),(229,1,125,NULL),(231,1,126,NULL),(233,1,127,NULL),(235,1,128,NULL),(237,1,129,NULL),(259,1,140,NULL),(261,1,141,NULL),(263,1,142,NULL),(264,1,143,NULL),(267,1,146,NULL),(269,1,147,NULL),(271,1,148,NULL),(273,1,149,NULL),(275,1,150,NULL),(277,1,151,NULL),(279,1,152,NULL),(281,1,153,NULL),(283,1,154,NULL),(305,1,165,NULL),(307,1,166,NULL),(309,1,167,NULL),(311,1,168,NULL),(313,1,169,NULL),(315,1,170,NULL),(317,1,171,NULL),(319,1,172,NULL),(321,1,173,NULL),(323,1,174,NULL),(325,1,175,NULL),(327,1,176,NULL),(329,1,177,NULL),(337,1,181,NULL),(339,1,182,NULL),(341,1,183,NULL),(343,1,184,NULL),(345,1,185,NULL),(347,1,186,NULL),(349,1,187,NULL),(351,1,188,NULL),(376,1,189,NULL),(380,1,191,NULL),(382,1,192,NULL),(384,1,193,NULL),(386,1,194,NULL),(388,1,195,NULL),(390,1,196,NULL),(392,1,197,NULL),(394,1,198,NULL),(395,1,199,NULL),(396,1,200,NULL),(397,1,201,NULL),(398,1,202,NULL),(399,1,203,NULL),(400,1,204,NULL),(401,1,205,NULL),(402,1,206,NULL),(403,1,207,NULL),(404,1,208,NULL),(405,1,209,NULL),(407,1,210,NULL),(409,1,211,NULL),(411,1,212,NULL),(413,1,213,NULL),(415,1,214,NULL),(417,1,215,NULL),(418,1,216,NULL),(440,1,229,NULL),(699,1,361,NULL),(700,1,362,NULL),(701,1,363,NULL),(702,1,364,NULL),(703,1,365,NULL),(704,1,366,NULL),(705,1,367,NULL),(706,1,368,NULL),(707,1,369,NULL),(708,1,370,NULL),(709,1,371,NULL),(710,1,372,NULL),(711,1,373,NULL),(712,1,374,NULL),(713,1,375,NULL),(714,1,376,NULL),(715,1,377,NULL),(716,1,378,NULL),(717,1,379,NULL),(718,1,380,NULL),(719,1,381,NULL),(720,1,382,NULL),(721,1,383,NULL),(722,1,384,NULL),(723,1,385,NULL),(724,1,386,NULL),(725,1,387,NULL),(726,1,388,NULL),(727,1,389,NULL),(728,1,390,NULL),(729,1,391,NULL),(730,1,392,NULL),(731,1,393,NULL),(732,1,394,NULL),(733,1,395,NULL),(734,1,396,NULL),(735,1,397,NULL),(736,1,398,NULL),(737,1,399,NULL),(738,1,400,NULL),(739,1,401,NULL),(740,1,402,NULL),(741,1,403,NULL),(742,1,404,NULL),(743,1,405,NULL),(745,1,407,NULL),(746,1,408,NULL),(747,1,409,NULL),(748,1,410,NULL),(749,1,411,NULL),(750,1,412,NULL),(751,1,413,NULL),(752,1,414,NULL),(753,1,415,NULL),(754,1,416,NULL),(755,1,417,NULL),(756,1,418,NULL),(757,1,419,NULL),(758,1,420,NULL),(759,1,421,NULL),(760,1,422,NULL),(761,1,423,NULL),(762,1,424,NULL),(763,1,425,NULL),(764,1,426,NULL),(765,1,427,NULL),(767,1,429,NULL),(768,1,430,NULL),(769,1,431,NULL),(770,1,432,NULL),(771,1,433,NULL),(772,1,434,NULL),(773,1,435,NULL),(774,1,436,NULL),(775,1,437,NULL),(776,1,438,NULL),(777,1,439,NULL),(778,1,440,NULL),(779,1,441,NULL),(780,1,442,NULL),(781,1,443,NULL),(782,1,444,NULL),(784,1,446,NULL),(785,1,447,NULL),(786,1,448,NULL),(787,1,449,NULL),(788,1,450,NULL),(789,1,451,NULL),(790,1,452,NULL),(791,1,453,NULL),(792,1,454,NULL),(793,1,455,NULL),(794,1,456,NULL),(795,1,457,NULL),(796,1,458,NULL),(797,1,459,NULL),(798,1,460,NULL),(799,1,461,NULL),(800,1,462,NULL),(801,1,463,NULL),(802,1,464,NULL),(803,1,465,NULL),(804,1,466,NULL),(805,1,467,NULL),(807,1,469,NULL),(808,1,470,NULL),(809,1,471,NULL),(812,1,474,NULL),(813,1,475,NULL),(814,1,476,NULL),(816,1,478,NULL),(817,1,479,NULL),(818,1,480,NULL),(819,1,481,NULL),(820,1,482,NULL),(821,1,483,NULL),(822,1,484,NULL),(823,1,485,NULL),(824,1,486,NULL),(825,1,487,NULL),(826,1,488,NULL),(827,1,489,NULL),(828,1,490,NULL),(829,1,491,NULL),(830,1,492,NULL),(831,1,493,NULL),(832,1,494,NULL),(833,1,495,NULL),(834,1,496,NULL),(835,1,497,NULL),(836,1,498,NULL),(837,1,499,NULL),(838,1,500,NULL),(839,1,501,NULL),(840,1,502,NULL),(841,1,503,NULL),(842,1,504,NULL),(869,1,531,NULL),(870,1,532,NULL),(871,1,533,NULL),(872,1,534,NULL),(873,1,535,NULL),(874,1,536,NULL),(875,1,537,NULL),(876,1,538,NULL),(877,1,539,NULL),(884,1,546,NULL),(885,1,547,NULL),(886,1,548,NULL),(887,1,549,NULL),(888,1,550,NULL),(889,1,551,NULL),(890,1,552,NULL),(891,1,553,NULL),(892,1,554,NULL),(893,1,555,NULL),(894,1,556,NULL),(895,1,557,NULL),(896,1,558,NULL),(897,1,559,NULL),(898,1,560,NULL),(899,1,561,NULL),(900,1,562,NULL),(901,1,563,NULL),(902,1,564,NULL),(903,1,565,NULL),(904,1,566,NULL),(905,1,567,NULL),(906,1,568,NULL),(912,1,179,NULL),(913,1,180,NULL),(914,1,569,NULL),(915,1,570,NULL),(916,1,571,NULL),(917,1,572,NULL),(918,1,573,NULL),(919,1,574,NULL),(920,1,575,NULL),(921,1,576,NULL),(922,1,577,NULL),(923,1,578,NULL),(924,1,579,NULL),(925,1,580,NULL),(926,1,581,NULL),(927,1,582,NULL),(928,1,583,NULL),(929,1,584,NULL),(930,1,585,NULL),(931,1,586,NULL),(932,1,587,NULL),(933,1,588,NULL),(934,1,589,NULL),(935,1,590,NULL),(936,1,591,NULL),(937,1,592,NULL),(938,1,593,NULL),(939,1,594,NULL),(940,1,595,NULL),(941,1,596,NULL),(942,1,597,NULL),(943,1,598,NULL),(944,1,602,NULL),(945,1,603,NULL),(946,1,604,NULL),(947,1,605,NULL),(948,1,606,NULL),(949,1,607,NULL),(950,1,608,NULL),(951,1,609,NULL),(952,1,610,NULL),(953,1,611,NULL),(954,1,612,NULL),(955,1,613,'Ms1228423252312'),(956,1,614,'Ms1228423681546'),(957,1,615,'Ms1228424238375');
/*!40000 ALTER TABLE `lookup_value_locale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `max_min_loan_amount`
--

DROP TABLE IF EXISTS `max_min_loan_amount`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `max_min_loan_amount` (
  `ACCOUNT_ID` int(11) NOT NULL auto_increment,
  `MIN_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  `MAX_LOAN_AMOUNT` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_ID`),
  CONSTRAINT `max_min_loan_amount_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `loan_account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `max_min_loan_amount`
--

LOCK TABLES `max_min_loan_amount` WRITE;
/*!40000 ALTER TABLE `max_min_loan_amount` DISABLE KEYS */;
INSERT INTO `max_min_loan_amount` VALUES (4,'100.000','10000.000'),(6,'100.000','10000.000'),(8,'100.000','10000.000'),(12,'100.000','10000.000'),(14,'100.000','10000.000'),(16,'100.000','10000.000'),(18,'100.000','10000.000'),(22,'100.000','10000.000'),(24,'100.000','10000.000'),(26,'100.000','10000.000'),(28,'100.000','10000.000');
/*!40000 ALTER TABLE `max_min_loan_amount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `max_min_no_of_install`
--

DROP TABLE IF EXISTS `max_min_no_of_install`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `max_min_no_of_install` (
  `ACCOUNT_ID` int(11) NOT NULL auto_increment,
  `MIN_NO_INSTALL` decimal(10,3) NOT NULL,
  `MAX_NO_INSTALL` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_ID`),
  CONSTRAINT `max_min_no_of_install_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `loan_account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `max_min_no_of_install`
--

LOCK TABLES `max_min_no_of_install` WRITE;
/*!40000 ALTER TABLE `max_min_no_of_install` DISABLE KEYS */;
INSERT INTO `max_min_no_of_install` VALUES (4,'1.000','1000.000'),(6,'1.000','1000.000'),(8,'1.000','1000.000'),(12,'1.000','1000.000'),(14,'1.000','1000.000'),(16,'1.000','1000.000'),(18,'1.000','1000.000'),(22,'1.000','1000.000'),(24,'1.000','1000.000'),(26,'1.000','1000.000'),(28,'1.000','1000.000');
/*!40000 ALTER TABLE `max_min_no_of_install` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meeting`
--

DROP TABLE IF EXISTS `meeting`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `meeting` (
  `MEETING_ID` int(11) NOT NULL auto_increment,
  `MEETING_TYPE_ID` smallint(6) NOT NULL,
  `MEETING_PLACE` varchar(200) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `START_TIME` date default NULL,
  `END_TIME` date default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`MEETING_ID`),
  KEY `MEETING_TYPE_ID` (`MEETING_TYPE_ID`),
  CONSTRAINT `meeting_ibfk_1` FOREIGN KEY (`MEETING_TYPE_ID`) REFERENCES `meeting_type` (`MEETING_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `meeting`
--

LOCK TABLES `meeting` WRITE;
/*!40000 ALTER TABLE `meeting` DISABLE KEYS */;
INSERT INTO `meeting` VALUES (1,1,'meetingPlace','2008-12-04',NULL,NULL,NULL,0),(2,4,'Wherever','2008-12-04',NULL,NULL,NULL,1),(3,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(4,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(5,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(6,1,'meetingPlace','2008-12-04',NULL,NULL,NULL,0),(7,4,'Wherever','2008-12-04',NULL,NULL,NULL,1),(8,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(9,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(10,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(11,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(12,1,'meetingPlace','2008-12-04',NULL,NULL,NULL,0),(13,4,'Wherever','2008-12-04',NULL,NULL,NULL,1),(14,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(15,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(16,4,'Wherever','2008-12-04',NULL,NULL,NULL,0),(17,4,'Wherever','2008-12-04',NULL,NULL,NULL,0);
/*!40000 ALTER TABLE `meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meeting_type`
--

DROP TABLE IF EXISTS `meeting_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `meeting_type` (
  `MEETING_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `MEETING_PURPOSE` varchar(50) default NULL,
  `DESCRIPTION` varchar(200) NOT NULL,
  PRIMARY KEY  (`MEETING_TYPE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `meeting_type`
--

LOCK TABLES `meeting_type` WRITE;
/*!40000 ALTER TABLE `meeting_type` DISABLE KEYS */;
INSERT INTO `meeting_type` VALUES (1,'LOANFREQUENCYOFINSTALLMENTS','Loan Frequency of istalments'),(2,'SAVINGSTIMEPERFORINTCALC','Savings Time Period for Interest Calculation'),(3,'SAVINGSFRQINTPOSTACC','Savings Frequency of Interest Posting to Accounts'),(4,'CUSTOMERMEETING','Customer Meeting'),(5,'FEEMEETING','Fees Meetings');
/*!40000 ALTER TABLE `meeting_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mfi_attribute`
--

DROP TABLE IF EXISTS `mfi_attribute`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mfi_attribute` (
  `ATTRIBUTE_ID` smallint(6) NOT NULL,
  `OFFICE_ID` smallint(6) NOT NULL,
  `ATTRIBUTE_NAME` varchar(100) NOT NULL,
  `ATTRIBUTE_VALUE` varchar(200) NOT NULL,
  PRIMARY KEY  (`ATTRIBUTE_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  CONSTRAINT `mfi_attribute_ibfk_1` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `mfi_attribute`
--

LOCK TABLES `mfi_attribute` WRITE;
/*!40000 ALTER TABLE `mfi_attribute` DISABLE KEYS */;
INSERT INTO `mfi_attribute` VALUES (1,1,'CENTER','GROUP'),(2,1,'CENTER','GROUP'),(3,1,'CENTER','GROUP'),(4,1,'CENTER','GROUP'),(5,1,'CENTER','GROUP');
/*!40000 ALTER TABLE `mfi_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mis_bank`
--

DROP TABLE IF EXISTS `mis_bank`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mis_bank` (
  `bankid` int(11) NOT NULL default '0',
  `bankname` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`bankid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `mis_bank`
--

LOCK TABLES `mis_bank` WRITE;
/*!40000 ALTER TABLE `mis_bank` DISABLE KEYS */;
INSERT INTO `mis_bank` VALUES (1,'BANK1'),(2,'BANK2'),(3,'BANK3');
/*!40000 ALTER TABLE `mis_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mis_bankbranch`
--

DROP TABLE IF EXISTS `mis_bankbranch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mis_bankbranch` (
  `bankbranchid` int(11) NOT NULL default '0',
  `bankid` int(11) NOT NULL default '0',
  `branchname` varchar(50) NOT NULL default '',
  `areaid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`bankbranchid`),
  KEY `FK_mis_bankbranch_mis_bank` (`bankid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `mis_bankbranch`
--

LOCK TABLES `mis_bankbranch` WRITE;
/*!40000 ALTER TABLE `mis_bankbranch` DISABLE KEYS */;
INSERT INTO `mis_bankbranch` VALUES (1,1,'BRANCHB1',4),(2,2,'BRANCHB2',5),(3,3,'BRANCHB3',6),(4,1,'BRANCHB11',7),(5,2,'BRANCHB21',8),(6,3,'BRANCHB31',9);
/*!40000 ALTER TABLE `mis_bankbranch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mis_geographicalarea`
--

DROP TABLE IF EXISTS `mis_geographicalarea`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mis_geographicalarea` (
  `areaid` int(11) NOT NULL default '0',
  `areaname` varchar(50) NOT NULL default '',
  `areatypeid` int(11) NOT NULL default '0',
  `parentareaid` int(11) default NULL,
  PRIMARY KEY  (`areaid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `mis_geographicalarea`
--

LOCK TABLES `mis_geographicalarea` WRITE;
/*!40000 ALTER TABLE `mis_geographicalarea` DISABLE KEYS */;
INSERT INTO `mis_geographicalarea` VALUES (1,'A1',1,NULL),(2,'A2',1,NULL),(3,'A3',1,NULL),(4,'B1',2,1),(5,'B2',2,2),(6,'B3',2,3),(7,'B11',2,1),(8,'B21',2,2),(9,'B31',2,3);
/*!40000 ALTER TABLE `mis_geographicalarea` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mis_geographicalareatype`
--

DROP TABLE IF EXISTS `mis_geographicalareatype`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mis_geographicalareatype` (
  `areatypeid` int(11) NOT NULL default '0',
  `areatypename` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`areatypeid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `mis_geographicalareatype`
--

LOCK TABLES `mis_geographicalareatype` WRITE;
/*!40000 ALTER TABLE `mis_geographicalareatype` DISABLE KEYS */;
INSERT INTO `mis_geographicalareatype` VALUES (1,'A'),(2,'B');
/*!40000 ALTER TABLE `mis_geographicalareatype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mis_shgmemberprofile`
--

DROP TABLE IF EXISTS `mis_shgmemberprofile`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mis_shgmemberprofile` (
  `groupid` int(11) NOT NULL default '0',
  `memberid` int(11) NOT NULL default '0',
  `membername` varchar(100) NOT NULL default '',
  `attendence` varchar(100) NOT NULL default '0',
  `savings` decimal(10,0) NOT NULL default '0',
  `mstatus` varchar(100) NOT NULL default '0',
  PRIMARY KEY  (`memberid`,`groupid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `mis_shgmemberprofile`
--

LOCK TABLES `mis_shgmemberprofile` WRITE;
/*!40000 ALTER TABLE `mis_shgmemberprofile` DISABLE KEYS */;
INSERT INTO `mis_shgmemberprofile` VALUES (1,1,'MEMBER1','YES','50','YES'),(1,2,'MEMBER2','YES','50','YES'),(1,3,'MEMBER3','NO','0','YES'),(1,4,'MEMBER4','YES','100','NO');
/*!40000 ALTER TABLE `mis_shgmemberprofile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mis_shgprofile`
--

DROP TABLE IF EXISTS `mis_shgprofile`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `mis_shgprofile` (
  `groupid` int(11) NOT NULL default '0',
  `groupname` varchar(50) NOT NULL default '',
  `nummembers` int(11) NOT NULL default '0',
  `areaid` int(11) NOT NULL default '0',
  `formationdate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `groupleader1` varchar(50) default NULL,
  `groupleader2` varchar(50) default NULL,
  `bankbranchid` int(11) default NULL,
  PRIMARY KEY  (`groupid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `mis_shgprofile`
--

LOCK TABLES `mis_shgprofile` WRITE;
/*!40000 ALTER TABLE `mis_shgprofile` DISABLE KEYS */;
INSERT INTO `mis_shgprofile` VALUES (1,'G1',10,4,'2008-11-11 05:39:19','LEADER 1','LEADER 2',1),(2,'G2',10,4,'2008-11-11 05:39:19','LEADER1','LEADER2',1),(3,'G3',8,5,'2008-11-11 05:39:19','LAEDER1','LEADER2',2),(4,'G4',12,4,'2008-11-11 05:39:19','LEADER1','LEADER2',1),(5,'G5',15,5,'2008-11-11 05:39:19','LEADER1','LEADER2',2),(6,'G6',11,7,'2008-11-11 05:39:19','LEADER1','LEADER2',1);
/*!40000 ALTER TABLE `mis_shgprofile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `no_of_install_from_last_loan`
--

DROP TABLE IF EXISTS `no_of_install_from_last_loan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `no_of_install_from_last_loan` (
  `NO_OF_INSTALL_FROM_LAST_LOAN_ID` smallint(6) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `START_RANGE` decimal(10,3) NOT NULL,
  `END_RANGE` decimal(10,3) NOT NULL,
  `MIN_NO_INSTALL` decimal(10,3) NOT NULL,
  `MAX_NO_INSTALL` decimal(10,3) NOT NULL,
  `DEFAULT_NO_INSTALL` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`NO_OF_INSTALL_FROM_LAST_LOAN_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `no_of_install_from_last_loan_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `loan_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `no_of_install_from_last_loan`
--

LOCK TABLES `no_of_install_from_last_loan` WRITE;
/*!40000 ALTER TABLE `no_of_install_from_last_loan` DISABLE KEYS */;
/*!40000 ALTER TABLE `no_of_install_from_last_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `no_of_install_from_loan_cycle`
--

DROP TABLE IF EXISTS `no_of_install_from_loan_cycle`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `no_of_install_from_loan_cycle` (
  `NO_OF_INSTALL_FROM_LOAN_CYCLE_ID` smallint(6) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `MIN_NO_INSTALL` decimal(10,3) NOT NULL,
  `MAX_NO_INSTALL` decimal(10,3) NOT NULL,
  `DEFAULT_NO_INSTALL` decimal(10,3) NOT NULL,
  `RANGE_INDEX` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`NO_OF_INSTALL_FROM_LOAN_CYCLE_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `no_of_install_from_loan_cycle_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `loan_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `no_of_install_from_loan_cycle`
--

LOCK TABLES `no_of_install_from_loan_cycle` WRITE;
/*!40000 ALTER TABLE `no_of_install_from_loan_cycle` DISABLE KEYS */;
/*!40000 ALTER TABLE `no_of_install_from_loan_cycle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `no_of_install_same_for_all_loan`
--

DROP TABLE IF EXISTS `no_of_install_same_for_all_loan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `no_of_install_same_for_all_loan` (
  `NO_OF_INSTALL_SAME_FOR_ALL_LOAN_ID` smallint(6) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `MIN_NO_INSTALL` decimal(10,3) NOT NULL,
  `MAX_NO_INSTALL` decimal(10,3) NOT NULL,
  `DEFAULT_NO_INSTALL` decimal(10,3) NOT NULL,
  PRIMARY KEY  (`NO_OF_INSTALL_SAME_FOR_ALL_LOAN_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `no_of_install_same_for_all_loan_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `loan_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `no_of_install_same_for_all_loan`
--

LOCK TABLES `no_of_install_same_for_all_loan` WRITE;
/*!40000 ALTER TABLE `no_of_install_same_for_all_loan` DISABLE KEYS */;
INSERT INTO `no_of_install_same_for_all_loan` VALUES (1,1,'1.000','1000.000','11.000'),(2,2,'1.000','1000.000','11.000'),(3,3,'1.000','1000.000','11.000');
/*!40000 ALTER TABLE `no_of_install_same_for_all_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offering_fund`
--

DROP TABLE IF EXISTS `offering_fund`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `offering_fund` (
  `OFFERING_FUND_ID` smallint(6) NOT NULL,
  `FUND_ID` smallint(6) default NULL,
  `PRD_OFFERING_ID` smallint(6) default NULL,
  PRIMARY KEY  (`OFFERING_FUND_ID`),
  KEY `FUND_ID` (`FUND_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `offering_fund_ibfk_1` FOREIGN KEY (`FUND_ID`) REFERENCES `fund` (`FUND_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `offering_fund_ibfk_2` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `offering_fund`
--

LOCK TABLES `offering_fund` WRITE;
/*!40000 ALTER TABLE `offering_fund` DISABLE KEYS */;
/*!40000 ALTER TABLE `offering_fund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office`
--

DROP TABLE IF EXISTS `office`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office` (
  `OFFICE_ID` smallint(6) NOT NULL auto_increment,
  `GLOBAL_OFFICE_NUM` varchar(100) NOT NULL,
  `OFFICE_LEVEL_ID` smallint(6) NOT NULL,
  `SEARCH_ID` varchar(100) NOT NULL,
  `MAX_CHILD_COUNT` int(11) NOT NULL,
  `LOCAL_REMOTE_FLAG` smallint(6) NOT NULL,
  `DISPLAY_NAME` varchar(200) NOT NULL,
  `CREATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  `OFFICE_SHORT_NAME` varchar(4) NOT NULL,
  `PARENT_OFFICE_ID` smallint(6) default NULL,
  `STATUS_ID` smallint(6) NOT NULL,
  `VERSION_NO` int(11) NOT NULL,
  `OFFICE_CODE_ID` smallint(6) default NULL,
  PRIMARY KEY  (`OFFICE_ID`),
  UNIQUE KEY `GLOBAL_OFFICE_NUM` (`GLOBAL_OFFICE_NUM`),
  UNIQUE KEY `OFFICE_GLOBAL_IDX` (`GLOBAL_OFFICE_NUM`),
  KEY `OFFICE_LEVEL_ID` (`OFFICE_LEVEL_ID`),
  KEY `PARENT_OFFICE_ID` (`PARENT_OFFICE_ID`),
  KEY `STATUS_ID` (`STATUS_ID`),
  KEY `OFFICE_CODE_ID` (`OFFICE_CODE_ID`),
  CONSTRAINT `office_ibfk_1` FOREIGN KEY (`OFFICE_LEVEL_ID`) REFERENCES `office_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_ibfk_2` FOREIGN KEY (`PARENT_OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_ibfk_3` FOREIGN KEY (`STATUS_ID`) REFERENCES `office_status` (`STATUS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_ibfk_4` FOREIGN KEY (`OFFICE_CODE_ID`) REFERENCES `office_code` (`CODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office`
--

LOCK TABLES `office` WRITE;
/*!40000 ALTER TABLE `office` DISABLE KEYS */;
INSERT INTO `office` VALUES (1,'0001',1,'1.1',2,1,'Mifos HO ',NULL,NULL,NULL,NULL,'MIF1',NULL,1,1,NULL),(2,'0002',5,'1.1.1',0,1,'Office1',1,'2008-12-04',NULL,NULL,'e1',1,1,0,NULL),(3,'0003',5,'1.1.2',0,1,'Office2',1,'2008-12-04',NULL,NULL,'e2',1,1,0,NULL),(4,'0004',5,'1.1.3',0,1,'Office3',1,'2008-12-04',NULL,NULL,'e3',1,1,0,NULL);
/*!40000 ALTER TABLE `office` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office_action_payment_type`
--

DROP TABLE IF EXISTS `office_action_payment_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office_action_payment_type` (
  `OFFICE_ID` smallint(6) default NULL,
  `PRD_TYPE_ID` smallint(6) default NULL,
  `ACCOUNT_ACTION_ID` smallint(6) NOT NULL,
  `PAYMENT_TYPE_ID` smallint(6) default NULL,
  KEY `ACCOUNT_ACTION_ID` (`ACCOUNT_ACTION_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  KEY `PAYMENT_TYPE_ID` (`PAYMENT_TYPE_ID`),
  KEY `PRD_TYPE_ID` (`PRD_TYPE_ID`),
  CONSTRAINT `office_action_payment_type_ibfk_1` FOREIGN KEY (`ACCOUNT_ACTION_ID`) REFERENCES `account_action` (`ACCOUNT_ACTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_action_payment_type_ibfk_2` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_action_payment_type_ibfk_3` FOREIGN KEY (`PAYMENT_TYPE_ID`) REFERENCES `payment_type` (`PAYMENT_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_action_payment_type_ibfk_4` FOREIGN KEY (`PRD_TYPE_ID`) REFERENCES `prd_type` (`PRD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office_action_payment_type`
--

LOCK TABLES `office_action_payment_type` WRITE;
/*!40000 ALTER TABLE `office_action_payment_type` DISABLE KEYS */;
INSERT INTO `office_action_payment_type` VALUES (NULL,NULL,1,NULL),(NULL,NULL,2,NULL),(NULL,NULL,3,NULL),(NULL,NULL,4,NULL),(NULL,NULL,5,NULL);
/*!40000 ALTER TABLE `office_action_payment_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office_address`
--

DROP TABLE IF EXISTS `office_address`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office_address` (
  `OFFICE_ADDRESS_ID` smallint(6) NOT NULL auto_increment,
  `OFFICE_ID` smallint(6) NOT NULL,
  `ADDRESS_1` varchar(200) default NULL,
  `ADDRESS_2` varchar(200) default NULL,
  `ADDRESS_3` varchar(200) default NULL,
  `CITY` varchar(100) default NULL,
  `STATE` varchar(100) default NULL,
  `COUNTRY` varchar(100) default NULL,
  `ZIP` varchar(20) default NULL,
  `TELEPHONE` varchar(20) default NULL,
  PRIMARY KEY  (`OFFICE_ADDRESS_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  CONSTRAINT `office_address_ibfk_1` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office_address`
--

LOCK TABLES `office_address` WRITE;
/*!40000 ALTER TABLE `office_address` DISABLE KEYS */;
INSERT INTO `office_address` VALUES (1,2,'','','','','','','',''),(2,3,'','','','','','','',''),(3,4,'','','','','','','','');
/*!40000 ALTER TABLE `office_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office_code`
--

DROP TABLE IF EXISTS `office_code`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office_code` (
  `CODE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`CODE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `office_code_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office_code`
--

LOCK TABLES `office_code` WRITE;
/*!40000 ALTER TABLE `office_code` DISABLE KEYS */;
INSERT INTO `office_code` VALUES (1,111),(2,112);
/*!40000 ALTER TABLE `office_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office_custom_field`
--

DROP TABLE IF EXISTS `office_custom_field`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office_custom_field` (
  `OFFICE_CUSTOM_FIELD_ID` int(11) NOT NULL auto_increment,
  `OFFICE_ID` smallint(6) NOT NULL,
  `FIELD_ID` smallint(6) NOT NULL,
  `FIELD_VALUE` varchar(200) default NULL,
  PRIMARY KEY  (`OFFICE_CUSTOM_FIELD_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  CONSTRAINT `office_custom_field_ibfk_1` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office_custom_field`
--

LOCK TABLES `office_custom_field` WRITE;
/*!40000 ALTER TABLE `office_custom_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `office_custom_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office_hierarchy`
--

DROP TABLE IF EXISTS `office_hierarchy`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office_hierarchy` (
  `HIERARCHY_ID` int(11) NOT NULL auto_increment,
  `PARENT_ID` smallint(6) NOT NULL,
  `OFFICE_ID` smallint(6) default NULL,
  `STATUS` smallint(6) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`HIERARCHY_ID`),
  KEY `PARENT_ID` (`PARENT_ID`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  KEY `OFFICE_HIERARCHY_IDX` (`OFFICE_ID`,`STATUS`),
  CONSTRAINT `office_hierarchy_ibfk_1` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_hierarchy_ibfk_2` FOREIGN KEY (`PARENT_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `office_hierarchy_ibfk_3` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office_hierarchy`
--

LOCK TABLES `office_hierarchy` WRITE;
/*!40000 ALTER TABLE `office_hierarchy` DISABLE KEYS */;
/*!40000 ALTER TABLE `office_hierarchy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office_level`
--

DROP TABLE IF EXISTS `office_level`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office_level` (
  `LEVEL_ID` smallint(6) NOT NULL,
  `PARENT_LEVEL_ID` smallint(6) default NULL,
  `LEVEL_NAME_ID` smallint(6) default NULL,
  `INTERACTION_FLAG` smallint(6) default NULL,
  `CONFIGURED` smallint(6) NOT NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`LEVEL_ID`),
  KEY `PARENT_LEVEL_ID` (`PARENT_LEVEL_ID`),
  CONSTRAINT `office_level_ibfk_1` FOREIGN KEY (`PARENT_LEVEL_ID`) REFERENCES `office_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office_level`
--

LOCK TABLES `office_level` WRITE;
/*!40000 ALTER TABLE `office_level` DISABLE KEYS */;
INSERT INTO `office_level` VALUES (1,NULL,104,0,1,NULL),(2,1,105,0,1,NULL),(3,2,106,0,1,NULL),(4,3,107,0,1,NULL),(5,4,108,1,1,NULL);
/*!40000 ALTER TABLE `office_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `office_status`
--

DROP TABLE IF EXISTS `office_status`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `office_status` (
  `STATUS_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`STATUS_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `office_status_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `office_status`
--

LOCK TABLES `office_status` WRITE;
/*!40000 ALTER TABLE `office_status` DISABLE KEYS */;
INSERT INTO `office_status` VALUES (1,15),(2,16);
/*!40000 ALTER TABLE `office_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_type`
--

DROP TABLE IF EXISTS `payment_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `payment_type` (
  `PAYMENT_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `PAYMENT_TYPE_LOOKUP_ID` int(11) default NULL,
  PRIMARY KEY  (`PAYMENT_TYPE_ID`),
  KEY `PAYMENT_TYPE_LOOKUP_ID` (`PAYMENT_TYPE_LOOKUP_ID`),
  CONSTRAINT `payment_type_ibfk_1` FOREIGN KEY (`PAYMENT_TYPE_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `payment_type`
--

LOCK TABLES `payment_type` WRITE;
/*!40000 ALTER TABLE `payment_type` DISABLE KEYS */;
INSERT INTO `payment_type` VALUES (1,177),(2,179),(3,180);
/*!40000 ALTER TABLE `payment_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `penalty`
--

DROP TABLE IF EXISTS `penalty`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `penalty` (
  `PENALTY_ID` smallint(6) NOT NULL,
  `GLOBAL_PENALTY_NUM` varchar(100) default NULL,
  `PENALTY_TYPE` varchar(100) default NULL,
  `OFFICE_ID` smallint(6) default NULL,
  `CATEGORY_ID` smallint(6) default NULL,
  `GLCODE_ID` smallint(6) NOT NULL,
  `LOOKUP_ID` int(11) default NULL,
  `RATE` decimal(13,10) NOT NULL,
  `FORMULA` varchar(100) default NULL,
  PRIMARY KEY  (`PENALTY_ID`),
  KEY `CATEGORY_ID` (`CATEGORY_ID`),
  KEY `GLCODE_ID` (`GLCODE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  CONSTRAINT `penalty_ibfk_1` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `category_type` (`CATEGORY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `penalty_ibfk_2` FOREIGN KEY (`GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `penalty_ibfk_3` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `penalty_ibfk_4` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `penalty`
--

LOCK TABLES `penalty` WRITE;
/*!40000 ALTER TABLE `penalty` DISABLE KEYS */;
/*!40000 ALTER TABLE `penalty` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel`
--

DROP TABLE IF EXISTS `personnel`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel` (
  `PERSONNEL_ID` smallint(6) NOT NULL auto_increment,
  `LEVEL_ID` smallint(6) NOT NULL,
  `GLOBAL_PERSONNEL_NUM` varchar(100) default NULL,
  `OFFICE_ID` smallint(6) default NULL,
  `TITLE` int(11) default NULL,
  `PERSONNEL_STATUS` smallint(6) default NULL,
  `PREFERRED_LOCALE` smallint(6) default NULL,
  `SEARCH_ID` varchar(100) default NULL,
  `MAX_CHILD_COUNT` int(11) default NULL,
  `PASSWORD` tinyblob,
  `LOGIN_NAME` varchar(200) default NULL,
  `EMAIL_ID` varchar(255) default NULL,
  `PASSWORD_CHANGED` smallint(6) NOT NULL,
  `DISPLAY_NAME` varchar(200) default NULL,
  `CREATED_BY` smallint(6) NOT NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  `LAST_LOGIN` date default NULL,
  `LOCKED` smallint(6) NOT NULL,
  `NO_OF_TRIES` smallint(6) NOT NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`PERSONNEL_ID`),
  UNIQUE KEY `PERSONNEL_GLOBAL_IDX` (`GLOBAL_PERSONNEL_NUM`),
  UNIQUE KEY `PERSONNEL_SEARCH_IDX` (`SEARCH_ID`),
  UNIQUE KEY `PERSONNEL_LOGIN_IDX` (`LOGIN_NAME`),
  KEY `CREATED_BY` (`CREATED_BY`),
  KEY `LEVEL_ID` (`LEVEL_ID`),
  KEY `PREFERRED_LOCALE` (`PREFERRED_LOCALE`),
  KEY `TITLE` (`TITLE`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  KEY `PERSONNEL_OFFICE_IDX` (`OFFICE_ID`),
  CONSTRAINT `personnel_ibfk_1` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_ibfk_2` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_ibfk_3` FOREIGN KEY (`LEVEL_ID`) REFERENCES `personnel_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_ibfk_4` FOREIGN KEY (`PREFERRED_LOCALE`) REFERENCES `supported_locale` (`LOCALE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_ibfk_5` FOREIGN KEY (`TITLE`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_ibfk_6` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel`
--

LOCK TABLES `personnel` WRITE;
/*!40000 ALTER TABLE `personnel` DISABLE KEYS */;
INSERT INTO `personnel` VALUES (1,2,'1',1,1,1,1,NULL,1,'hÞ8¿Jd¯&G¹\rØJÉÜŸÎ4•Ua°a','mifos',NULL,1,'mifos',1,NULL,1,NULL,'2008-12-18',0,0,4),(2,1,'0002-00002',2,NULL,1,1,NULL,NULL,'Z‹â>>dF\ZŒIßèl«Ô©hÖÙºïYÑVi','bagonza1228423252312','bagonza+1228423252312@example.com',0,'Bagonza Wilson',1,'2008-12-04',NULL,NULL,NULL,0,0,1),(3,1,'0003-00003',3,NULL,1,1,NULL,NULL,'ÉÂ{D€Âãõ:ÆÑb¦Û¨¶)\nØHrÝó','bagonza1228423681546','john+1228423681546@example.com',0,'John Okoth',1,'2008-12-04',NULL,NULL,NULL,0,0,1),(4,1,'0004-00004',4,NULL,1,1,NULL,NULL,'aNgÓ¿bœi7æi-Q|™íUe…4Y¹','bagonza1228424238375','jenna+1228424238375@example.com',0,'Jenna Barth',1,'2008-12-04',NULL,NULL,NULL,0,0,1);
/*!40000 ALTER TABLE `personnel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_custom_field`
--

DROP TABLE IF EXISTS `personnel_custom_field`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_custom_field` (
  `PERSONNEL_CUSTOM_FIELD_ID` int(11) NOT NULL auto_increment,
  `FIELD_ID` smallint(6) NOT NULL,
  `PERSONNEL_ID` smallint(6) NOT NULL,
  `FIELD_VALUE` varchar(100) default NULL,
  PRIMARY KEY  (`PERSONNEL_CUSTOM_FIELD_ID`),
  KEY `PERSONNEL_ID` (`PERSONNEL_ID`),
  KEY `FIELD_ID` (`FIELD_ID`),
  CONSTRAINT `personnel_custom_field_ibfk_1` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_custom_field_ibfk_2` FOREIGN KEY (`FIELD_ID`) REFERENCES `custom_field_definition` (`FIELD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_custom_field`
--

LOCK TABLES `personnel_custom_field` WRITE;
/*!40000 ALTER TABLE `personnel_custom_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `personnel_custom_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_details`
--

DROP TABLE IF EXISTS `personnel_details`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_details` (
  `PERSONNEL_ID` smallint(6) NOT NULL,
  `FIRST_NAME` varchar(100) NOT NULL,
  `MIDDLE_NAME` varchar(100) default NULL,
  `SECOND_LAST_NAME` varchar(100) default NULL,
  `LAST_NAME` varchar(100) default NULL,
  `GOVERNMENT_ID_NUMBER` varchar(50) default NULL,
  `DOB` date NOT NULL,
  `MARITAL_STATUS` int(11) default NULL,
  `GENDER` int(11) NOT NULL,
  `DATE_OF_JOINING_MFI` date default NULL,
  `DATE_OF_JOINING_BRANCH` date default NULL,
  `DATE_OF_LEAVING_BRANCH` date default NULL,
  `ADDRESS_1` varchar(200) default NULL,
  `ADDRESS_2` varchar(200) default NULL,
  `ADDRESS_3` varchar(200) default NULL,
  `CITY` varchar(100) default NULL,
  `STATE` varchar(100) default NULL,
  `COUNTRY` varchar(100) default NULL,
  `POSTAL_CODE` varchar(100) default NULL,
  `TELEPHONE` varchar(20) default NULL,
  KEY `PERSONNEL_ID` (`PERSONNEL_ID`),
  KEY `GENDER` (`GENDER`),
  KEY `MARITAL_STATUS` (`MARITAL_STATUS`),
  CONSTRAINT `personnel_details_ibfk_1` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_details_ibfk_2` FOREIGN KEY (`GENDER`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_details_ibfk_3` FOREIGN KEY (`MARITAL_STATUS`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_details`
--

LOCK TABLES `personnel_details` WRITE;
/*!40000 ALTER TABLE `personnel_details` DISABLE KEYS */;
INSERT INTO `personnel_details` VALUES (1,'Mifos',NULL,NULL,'MFI','123','1979-12-12',NULL,50,NULL,NULL,NULL,'Bangalore',NULL,NULL,'Bangalore','Bangalore','Bangalore',NULL,NULL),(2,'Bagonza','','','Wilson','','1968-05-02',NULL,49,'2008-12-04','2008-12-04',NULL,'','','','','','','',''),(3,'John','','','Okoth','','1968-05-02',NULL,49,'2008-12-04','2008-12-04',NULL,'','','','','','','',''),(4,'Jenna','','','Barth','','1968-05-22',NULL,50,'2008-12-04','2008-12-04',NULL,'','','','','','','','');
/*!40000 ALTER TABLE `personnel_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_hierarchy`
--

DROP TABLE IF EXISTS `personnel_hierarchy`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_hierarchy` (
  `HIERARCHY_ID` int(11) NOT NULL,
  `PARENT_ID` smallint(6) NOT NULL,
  `PERSONNEL_ID` smallint(6) default NULL,
  `STATUS` smallint(6) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date NOT NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`HIERARCHY_ID`),
  KEY `PARENT_ID` (`PARENT_ID`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  KEY `PERSONNEL_HIERARCHY_IDX` (`PERSONNEL_ID`,`STATUS`),
  CONSTRAINT `personnel_hierarchy_ibfk_1` FOREIGN KEY (`PARENT_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_hierarchy_ibfk_2` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_hierarchy_ibfk_3` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_hierarchy`
--

LOCK TABLES `personnel_hierarchy` WRITE;
/*!40000 ALTER TABLE `personnel_hierarchy` DISABLE KEYS */;
/*!40000 ALTER TABLE `personnel_hierarchy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_level`
--

DROP TABLE IF EXISTS `personnel_level`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_level` (
  `LEVEL_ID` smallint(6) NOT NULL,
  `LEVEL_NAME_ID` int(11) NOT NULL,
  `PARENT_LEVEL_ID` smallint(6) default NULL,
  `INTERACTION_FLAG` smallint(6) default NULL,
  PRIMARY KEY  (`LEVEL_ID`),
  KEY `PARENT_LEVEL_ID` (`PARENT_LEVEL_ID`),
  KEY `LEVEL_NAME_ID` (`LEVEL_NAME_ID`),
  CONSTRAINT `personnel_level_ibfk_1` FOREIGN KEY (`PARENT_LEVEL_ID`) REFERENCES `personnel_level` (`LEVEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_level_ibfk_2` FOREIGN KEY (`LEVEL_NAME_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_level`
--

LOCK TABLES `personnel_level` WRITE;
/*!40000 ALTER TABLE `personnel_level` DISABLE KEYS */;
INSERT INTO `personnel_level` VALUES (1,60,1,0),(2,61,1,0);
/*!40000 ALTER TABLE `personnel_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_movement`
--

DROP TABLE IF EXISTS `personnel_movement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_movement` (
  `PERSONNEL_MOVEMENT_ID` smallint(6) NOT NULL auto_increment,
  `PERSONNEL_ID` smallint(6) default NULL,
  `OFFICE_ID` smallint(6) NOT NULL,
  `STATUS` smallint(6) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`PERSONNEL_MOVEMENT_ID`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  KEY `PERSONNEL_MOVEMENT_IDX` (`PERSONNEL_ID`),
  CONSTRAINT `personnel_movement_ibfk_1` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_movement_ibfk_2` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_movement_ibfk_3` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_movement`
--

LOCK TABLES `personnel_movement` WRITE;
/*!40000 ALTER TABLE `personnel_movement` DISABLE KEYS */;
/*!40000 ALTER TABLE `personnel_movement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_notes`
--

DROP TABLE IF EXISTS `personnel_notes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_notes` (
  `COMMENT_ID` int(11) NOT NULL auto_increment,
  `PERSONNEL_ID` smallint(6) NOT NULL,
  `COMMENT_DATE` date NOT NULL,
  `COMMENTS` varchar(500) NOT NULL,
  `OFFICER_ID` smallint(6) default NULL,
  PRIMARY KEY  (`COMMENT_ID`),
  KEY `PERSONNEL_ID` (`PERSONNEL_ID`),
  KEY `OFFICER_ID` (`OFFICER_ID`),
  CONSTRAINT `personnel_notes_ibfk_1` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_notes_ibfk_2` FOREIGN KEY (`OFFICER_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_notes`
--

LOCK TABLES `personnel_notes` WRITE;
/*!40000 ALTER TABLE `personnel_notes` DISABLE KEYS */;
/*!40000 ALTER TABLE `personnel_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_role`
--

DROP TABLE IF EXISTS `personnel_role`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_role` (
  `PERSONNEL_ROLE_ID` int(11) NOT NULL auto_increment,
  `ROLE_ID` smallint(6) NOT NULL,
  `PERSONNEL_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`PERSONNEL_ROLE_ID`),
  KEY `PERSONNEL_ID` (`PERSONNEL_ID`),
  KEY `ROLE_ID` (`ROLE_ID`),
  CONSTRAINT `personnel_role_ibfk_1` FOREIGN KEY (`PERSONNEL_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `personnel_role_ibfk_2` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ROLE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_role`
--

LOCK TABLES `personnel_role` WRITE;
/*!40000 ALTER TABLE `personnel_role` DISABLE KEYS */;
INSERT INTO `personnel_role` VALUES (1,1,1);
/*!40000 ALTER TABLE `personnel_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personnel_status`
--

DROP TABLE IF EXISTS `personnel_status`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `personnel_status` (
  `PERSONNEL_STATUS_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`PERSONNEL_STATUS_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `personnel_status_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `personnel_status`
--

LOCK TABLES `personnel_status` WRITE;
/*!40000 ALTER TABLE `personnel_status` DISABLE KEYS */;
INSERT INTO `personnel_status` VALUES (1,152),(2,153);
/*!40000 ALTER TABLE `personnel_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `position`
--

DROP TABLE IF EXISTS `position`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `position` (
  `POSITION_ID` int(11) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`POSITION_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `position_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `position`
--

LOCK TABLES `position` WRITE;
/*!40000 ALTER TABLE `position` DISABLE KEYS */;
INSERT INTO `position` VALUES (1,186),(2,187),(3,188),(4,216);
/*!40000 ALTER TABLE `position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ppi_likelihoods`
--

DROP TABLE IF EXISTS `ppi_likelihoods`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ppi_likelihoods` (
  `LIKELIHOOD_ID` int(11) NOT NULL auto_increment,
  `SURVEY_ID` int(11) NOT NULL,
  `SCORE_FROM` int(11) NOT NULL,
  `SCORE_TO` int(11) NOT NULL,
  `BOTTOM_HALF_BELOW` decimal(10,3) NOT NULL,
  `TOP_HALF_BELOW` decimal(10,3) NOT NULL,
  `LIKELIHOOD_ORDER` int(11) NOT NULL,
  PRIMARY KEY  (`LIKELIHOOD_ID`),
  KEY `SURVEY_ID` (`SURVEY_ID`),
  CONSTRAINT `ppi_likelihoods_ibfk_1` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ppi_likelihoods`
--

LOCK TABLES `ppi_likelihoods` WRITE;
/*!40000 ALTER TABLE `ppi_likelihoods` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppi_likelihoods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ppi_survey`
--

DROP TABLE IF EXISTS `ppi_survey`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ppi_survey` (
  `COUNTRY_ID` int(11) NOT NULL,
  `SURVEY_ID` int(11) NOT NULL,
  `VERY_POOR_MIN` int(11) NOT NULL,
  `VERY_POOR_MAX` int(11) NOT NULL,
  `POOR_MIN` int(11) NOT NULL,
  `POOR_MAX` int(11) NOT NULL,
  `AT_RISK_MIN` int(11) NOT NULL,
  `AT_RISK_MAX` int(11) NOT NULL,
  `NON_POOR_MIN` int(11) NOT NULL,
  `NON_POOR_MAX` int(11) NOT NULL,
  PRIMARY KEY  (`COUNTRY_ID`),
  KEY `SURVEY_ID` (`SURVEY_ID`),
  CONSTRAINT `ppi_survey_ibfk_1` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ppi_survey`
--

LOCK TABLES `ppi_survey` WRITE;
/*!40000 ALTER TABLE `ppi_survey` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppi_survey` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ppi_survey_instance`
--

DROP TABLE IF EXISTS `ppi_survey_instance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ppi_survey_instance` (
  `INSTANCE_ID` int(11) NOT NULL,
  `BOTTOM_HALF_BELOW` decimal(10,3) default NULL,
  `TOP_HALF_BELOW` decimal(10,3) default NULL,
  PRIMARY KEY  (`INSTANCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ppi_survey_instance`
--

LOCK TABLES `ppi_survey_instance` WRITE;
/*!40000 ALTER TABLE `ppi_survey_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppi_survey_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_applicable_master`
--

DROP TABLE IF EXISTS `prd_applicable_master`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_applicable_master` (
  `PRD_APPLICABLE_MASTER_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`PRD_APPLICABLE_MASTER_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `prd_applicable_master_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_applicable_master`
--

LOCK TABLES `prd_applicable_master` WRITE;
/*!40000 ALTER TABLE `prd_applicable_master` DISABLE KEYS */;
INSERT INTO `prd_applicable_master` VALUES (1,68),(2,69),(3,70);
/*!40000 ALTER TABLE `prd_applicable_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_category`
--

DROP TABLE IF EXISTS `prd_category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_category` (
  `PRD_CATEGORY_ID` smallint(6) NOT NULL auto_increment,
  `PRD_TYPE_ID` smallint(6) NOT NULL,
  `GLOBAL_PRD_OFFERING_NUM` varchar(50) NOT NULL,
  `PRD_CATEGORY_NAME` varchar(100) NOT NULL,
  `CREATED_DATE` date default NULL,
  `CREATED_BY` int(11) default NULL,
  `OFFICE_ID` smallint(6) default NULL,
  `UPDATED_BY` int(11) default NULL,
  `UDPATED_DATE` date default NULL,
  `STATE` smallint(6) NOT NULL,
  `DESCRIPTION` varchar(500) default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`PRD_CATEGORY_ID`),
  KEY `PRD_TYPE_ID` (`PRD_TYPE_ID`),
  CONSTRAINT `prd_category_ibfk_1` FOREIGN KEY (`PRD_TYPE_ID`) REFERENCES `prd_type` (`PRD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_category`
--

LOCK TABLES `prd_category` WRITE;
/*!40000 ALTER TABLE `prd_category` DISABLE KEYS */;
INSERT INTO `prd_category` VALUES (1,1,'1-1','Other',NULL,NULL,NULL,NULL,NULL,1,NULL,1),(2,2,'1-2','Other',NULL,NULL,NULL,NULL,NULL,1,NULL,1);
/*!40000 ALTER TABLE `prd_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_category_status`
--

DROP TABLE IF EXISTS `prd_category_status`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_category_status` (
  `PRD_CATEGORY_STATUS_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`PRD_CATEGORY_STATUS_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `prd_category_status_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_category_status`
--

LOCK TABLES `prd_category_status` WRITE;
/*!40000 ALTER TABLE `prd_category_status` DISABLE KEYS */;
INSERT INTO `prd_category_status` VALUES (0,113),(1,114);
/*!40000 ALTER TABLE `prd_category_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_checklist`
--

DROP TABLE IF EXISTS `prd_checklist`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_checklist` (
  `CHECKLIST_ID` smallint(6) NOT NULL,
  `PRD_TYPE_ID` smallint(6) default NULL,
  `ACCOUNT_STATUS` smallint(6) NOT NULL,
  PRIMARY KEY  (`CHECKLIST_ID`),
  KEY `ACCOUNT_STATUS` (`ACCOUNT_STATUS`),
  KEY `PRD_TYPE_ID` (`PRD_TYPE_ID`),
  CONSTRAINT `prd_checklist_ibfk_1` FOREIGN KEY (`ACCOUNT_STATUS`) REFERENCES `account_state` (`ACCOUNT_STATE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_checklist_ibfk_2` FOREIGN KEY (`CHECKLIST_ID`) REFERENCES `checklist` (`CHECKLIST_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_checklist_ibfk_3` FOREIGN KEY (`PRD_TYPE_ID`) REFERENCES `prd_type` (`PRD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_checklist`
--

LOCK TABLES `prd_checklist` WRITE;
/*!40000 ALTER TABLE `prd_checklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `prd_checklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_fee_frequency`
--

DROP TABLE IF EXISTS `prd_fee_frequency`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_fee_frequency` (
  `PRDOFFERING_FEE_ID` smallint(6) NOT NULL,
  `FEE_ID` smallint(6) default NULL,
  `FREQUENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`PRDOFFERING_FEE_ID`),
  KEY `FEE_ID` (`FEE_ID`),
  KEY `FREQUENCY_ID` (`FREQUENCY_ID`),
  CONSTRAINT `prd_fee_frequency_ibfk_1` FOREIGN KEY (`FEE_ID`) REFERENCES `fees` (`FEE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_fee_frequency_ibfk_2` FOREIGN KEY (`PRDOFFERING_FEE_ID`) REFERENCES `prd_offering_fees` (`PRD_OFFERING_FEE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_fee_frequency_ibfk_3` FOREIGN KEY (`FREQUENCY_ID`) REFERENCES `recurrence_type` (`RECURRENCE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_fee_frequency`
--

LOCK TABLES `prd_fee_frequency` WRITE;
/*!40000 ALTER TABLE `prd_fee_frequency` DISABLE KEYS */;
/*!40000 ALTER TABLE `prd_fee_frequency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_offering`
--

DROP TABLE IF EXISTS `prd_offering`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_offering` (
  `PRD_OFFERING_ID` smallint(6) NOT NULL auto_increment,
  `PRD_APPLICABLE_MASTER_ID` smallint(6) NOT NULL,
  `GLOBAL_PRD_OFFERING_NUM` varchar(50) NOT NULL,
  `PRD_CATEGORY_ID` smallint(6) NOT NULL,
  `PRD_TYPE_ID` smallint(6) default NULL,
  `OFFICE_ID` smallint(6) default NULL,
  `START_DATE` date NOT NULL,
  `END_DATE` date default NULL,
  `GLCODE_ID` smallint(6) default NULL,
  `PRD_OFFERING_NAME` varchar(50) NOT NULL,
  `PRD_OFFERING_SHORT_NAME` varchar(50) NOT NULL,
  `OFFERING_STATUS_ID` smallint(6) default NULL,
  `DESCRIPTION` varchar(200) default NULL,
  `CREATED_DATE` date NOT NULL,
  `CREATED_BY` int(11) NOT NULL,
  `UPDATED_DATE` date default NULL,
  `UPDATED_BY` int(11) default NULL,
  `VERSION_NO` int(11) default NULL,
  `PRD_MIX_FLAG` smallint(6) default NULL,
  PRIMARY KEY  (`PRD_OFFERING_ID`),
  UNIQUE KEY `PRD_OFFERING_GLOBAL_IDX` (`GLOBAL_PRD_OFFERING_NUM`),
  KEY `GLCODE_ID` (`GLCODE_ID`),
  KEY `PRD_CATEGORY_ID` (`PRD_CATEGORY_ID`),
  KEY `OFFERING_STATUS_ID` (`OFFERING_STATUS_ID`),
  KEY `PRD_APPLICABLE_MASTER_ID` (`PRD_APPLICABLE_MASTER_ID`),
  KEY `PRD_OFFERING_OFFICE_IDX` (`OFFICE_ID`),
  KEY `PRD_TYPE_IDX` (`PRD_TYPE_ID`),
  CONSTRAINT `prd_offering_ibfk_1` FOREIGN KEY (`GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_offering_ibfk_2` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_offering_ibfk_3` FOREIGN KEY (`PRD_CATEGORY_ID`) REFERENCES `prd_category` (`PRD_CATEGORY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_offering_ibfk_4` FOREIGN KEY (`OFFERING_STATUS_ID`) REFERENCES `prd_status` (`OFFERING_STATUS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_offering_ibfk_5` FOREIGN KEY (`PRD_TYPE_ID`) REFERENCES `prd_type` (`PRD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_offering_ibfk_6` FOREIGN KEY (`PRD_APPLICABLE_MASTER_ID`) REFERENCES `prd_applicable_master` (`PRD_APPLICABLE_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_offering`
--

LOCK TABLES `prd_offering` WRITE;
/*!40000 ALTER TABLE `prd_offering` DISABLE KEYS */;
INSERT INTO `prd_offering` VALUES (1,1,'1-001',1,1,1,'2008-12-04',NULL,NULL,'FlatInterestLoanProduct1','t1',1,'auto-generated randomly-named loan product','2008-12-04',1,NULL,NULL,0,NULL),(2,1,'1-002',1,1,1,'2008-12-04',NULL,NULL,'FlatInterestLoanProductG2','tG2',1,'auto-generated randomly-named loan product','2008-12-04',1,NULL,NULL,0,NULL),(3,1,'1-003',1,1,1,'2008-12-04',NULL,NULL,'FlatInterestLoanProductG3','tG3',1,'auto-generated randomly-named loan product','2008-12-04',1,NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `prd_offering` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_offering_fees`
--

DROP TABLE IF EXISTS `prd_offering_fees`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_offering_fees` (
  `PRD_OFFERING_FEE_ID` smallint(6) NOT NULL auto_increment,
  `FEE_ID` smallint(6) default NULL,
  `PRD_OFFERING_ID` smallint(6) default NULL,
  PRIMARY KEY  (`PRD_OFFERING_FEE_ID`),
  KEY `FEE_ID` (`FEE_ID`),
  KEY `PRD_OFFERING_FEE_IDX` (`PRD_OFFERING_ID`,`FEE_ID`),
  CONSTRAINT `prd_offering_fees_ibfk_1` FOREIGN KEY (`FEE_ID`) REFERENCES `fees` (`FEE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_offering_fees_ibfk_2` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_offering_fees`
--

LOCK TABLES `prd_offering_fees` WRITE;
/*!40000 ALTER TABLE `prd_offering_fees` DISABLE KEYS */;
/*!40000 ALTER TABLE `prd_offering_fees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_offering_meeting`
--

DROP TABLE IF EXISTS `prd_offering_meeting`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_offering_meeting` (
  `PRD_OFFERING_MEETING_ID` smallint(6) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `PRD_MEETING_ID` int(11) default NULL,
  `PRD_OFFERING_MEETING_TYPE_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`PRD_OFFERING_MEETING_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  KEY `PRD_MEETING_ID` (`PRD_MEETING_ID`),
  KEY `PRD_OFFERING_MEETING_TYPE_ID` (`PRD_OFFERING_MEETING_TYPE_ID`),
  CONSTRAINT `prd_offering_meeting_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`),
  CONSTRAINT `prd_offering_meeting_ibfk_2` FOREIGN KEY (`PRD_MEETING_ID`) REFERENCES `meeting` (`MEETING_ID`),
  CONSTRAINT `prd_offering_meeting_ibfk_3` FOREIGN KEY (`PRD_OFFERING_MEETING_TYPE_ID`) REFERENCES `meeting_type` (`MEETING_TYPE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_offering_meeting`
--

LOCK TABLES `prd_offering_meeting` WRITE;
/*!40000 ALTER TABLE `prd_offering_meeting` DISABLE KEYS */;
INSERT INTO `prd_offering_meeting` VALUES (1,1,1,1),(2,2,6,1),(3,3,12,1);
/*!40000 ALTER TABLE `prd_offering_meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_offering_mix`
--

DROP TABLE IF EXISTS `prd_offering_mix`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_offering_mix` (
  `PRD_OFFERING_MIX_ID` int(11) NOT NULL auto_increment,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `PRD_OFFERING_NOT_ALLOWED_ID` smallint(6) NOT NULL,
  `CREATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`PRD_OFFERING_MIX_ID`),
  KEY `PRD_OFFERING_MIX_PRD_OFFERING_ID_1` (`PRD_OFFERING_ID`),
  KEY `PRD_OFFERING_MIX_PRD_OFFERING_ID_2` (`PRD_OFFERING_NOT_ALLOWED_ID`),
  CONSTRAINT `PRD_OFFERING_MIX_PRD_OFFERING_ID_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `PRD_OFFERING_MIX_PRD_OFFERING_ID_2` FOREIGN KEY (`PRD_OFFERING_NOT_ALLOWED_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_offering_mix`
--

LOCK TABLES `prd_offering_mix` WRITE;
/*!40000 ALTER TABLE `prd_offering_mix` DISABLE KEYS */;
/*!40000 ALTER TABLE `prd_offering_mix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_state`
--

DROP TABLE IF EXISTS `prd_state`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_state` (
  `PRD_STATE_ID` smallint(6) NOT NULL auto_increment,
  `PRD_STATE_LOOKUP_ID` int(11) default NULL,
  PRIMARY KEY  (`PRD_STATE_ID`),
  KEY `PRD_STATE_LOOKUP_ID` (`PRD_STATE_LOOKUP_ID`),
  CONSTRAINT `prd_state_ibfk_1` FOREIGN KEY (`PRD_STATE_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_state`
--

LOCK TABLES `prd_state` WRITE;
/*!40000 ALTER TABLE `prd_state` DISABLE KEYS */;
INSERT INTO `prd_state` VALUES (1,115),(2,116);
/*!40000 ALTER TABLE `prd_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_status`
--

DROP TABLE IF EXISTS `prd_status`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_status` (
  `OFFERING_STATUS_ID` smallint(6) NOT NULL auto_increment,
  `PRD_STATE_ID` smallint(6) NOT NULL,
  `PRD_TYPE_ID` smallint(6) NOT NULL,
  `CURRENTLY_IN_USE` smallint(6) NOT NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`OFFERING_STATUS_ID`),
  KEY `PRD_TYPE_ID` (`PRD_TYPE_ID`),
  KEY `PRD_STATE_ID` (`PRD_STATE_ID`),
  CONSTRAINT `prd_status_ibfk_1` FOREIGN KEY (`PRD_TYPE_ID`) REFERENCES `prd_type` (`PRD_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `prd_status_ibfk_2` FOREIGN KEY (`PRD_STATE_ID`) REFERENCES `prd_state` (`PRD_STATE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_status`
--

LOCK TABLES `prd_status` WRITE;
/*!40000 ALTER TABLE `prd_status` DISABLE KEYS */;
INSERT INTO `prd_status` VALUES (1,1,1,1,1),(2,1,2,1,1),(4,2,1,1,1),(5,2,2,1,1);
/*!40000 ALTER TABLE `prd_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prd_type`
--

DROP TABLE IF EXISTS `prd_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `prd_type` (
  `PRD_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `PRD_TYPE_LOOKUP_ID` int(11) NOT NULL,
  `LATENESS_DAYS` smallint(6) default NULL,
  `DORMANCY_DAYS` smallint(6) default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`PRD_TYPE_ID`),
  KEY `PRD_TYPE_LOOKUP_ID` (`PRD_TYPE_LOOKUP_ID`),
  CONSTRAINT `prd_type_ibfk_1` FOREIGN KEY (`PRD_TYPE_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `prd_type`
--

LOCK TABLES `prd_type` WRITE;
/*!40000 ALTER TABLE `prd_type` DISABLE KEYS */;
INSERT INTO `prd_type` VALUES (1,54,10,1,1),(2,55,12,30,1);
/*!40000 ALTER TABLE `prd_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_offering_mandatory_savings`
--

DROP TABLE IF EXISTS `product_offering_mandatory_savings`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `product_offering_mandatory_savings` (
  `PRODUCT_OFFERING_MANDATORY_SAVINGS_ID` smallint(6) NOT NULL,
  `PRODUCT_OFFERING_MANDATORY_SAVINGS_TYPE` smallint(6) default NULL,
  `PRD_OFFERING_ID` smallint(6) default NULL,
  `PRODUCT_OFFERING_MANDATORY_SAVINGS_VALUE` smallint(6) default NULL,
  `PRODUCT_OFFERING_MANDATORY_SAVINGS_RANGE` smallint(6) default NULL,
  PRIMARY KEY  (`PRODUCT_OFFERING_MANDATORY_SAVINGS_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  CONSTRAINT `product_offering_mandatory_savings_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `product_offering_mandatory_savings`
--

LOCK TABLES `product_offering_mandatory_savings` WRITE;
/*!40000 ALTER TABLE `product_offering_mandatory_savings` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_offering_mandatory_savings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `program`
--

DROP TABLE IF EXISTS `program`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `program` (
  `PROGRAM_ID` int(11) NOT NULL auto_increment,
  `OFFICE_ID` smallint(6) NOT NULL,
  `LOOKUP_ID` int(11) NOT NULL,
  `GLCODE_ID` smallint(6) default NULL,
  `PROGRAM_NAME` varchar(100) default NULL,
  `START_DATE` date NOT NULL,
  `END_DATE` date default NULL,
  `CONFIDENTIALITY` smallint(6) default NULL,
  PRIMARY KEY  (`PROGRAM_ID`),
  KEY `GLCODE_ID` (`GLCODE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  KEY `OFFICE_ID` (`OFFICE_ID`),
  CONSTRAINT `program_ibfk_1` FOREIGN KEY (`GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `program_ibfk_2` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `program_ibfk_3` FOREIGN KEY (`OFFICE_ID`) REFERENCES `office` (`OFFICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `program`
--

LOCK TABLES `program` WRITE;
/*!40000 ALTER TABLE `program` DISABLE KEYS */;
/*!40000 ALTER TABLE `program` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `program_fund`
--

DROP TABLE IF EXISTS `program_fund`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `program_fund` (
  `PROGRAM_FUND_ID` smallint(6) NOT NULL,
  `FUND_ID` smallint(6) default NULL,
  `PROGRAM_ID` int(11) default NULL,
  PRIMARY KEY  (`PROGRAM_FUND_ID`),
  KEY `FUND_ID` (`FUND_ID`),
  KEY `PROGRAM_ID` (`PROGRAM_ID`),
  CONSTRAINT `program_fund_ibfk_1` FOREIGN KEY (`FUND_ID`) REFERENCES `fund` (`FUND_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `program_fund_ibfk_2` FOREIGN KEY (`PROGRAM_ID`) REFERENCES `program` (`PROGRAM_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `program_fund`
--

LOCK TABLES `program_fund` WRITE;
/*!40000 ALTER TABLE `program_fund` DISABLE KEYS */;
/*!40000 ALTER TABLE `program_fund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_choices`
--

DROP TABLE IF EXISTS `question_choices`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `question_choices` (
  `CHOICE_ID` int(11) NOT NULL auto_increment,
  `QUESTION_ID` int(11) NOT NULL,
  `CHOICE_TEXT` varchar(200) NOT NULL,
  `CHOICE_ORDER` int(11) NOT NULL,
  `PPI` varchar(1) NOT NULL,
  `PPI_POINTS` int(11) default NULL,
  PRIMARY KEY  (`CHOICE_ID`),
  KEY `QUESTION_ID` (`QUESTION_ID`),
  CONSTRAINT `question_choices_ibfk_1` FOREIGN KEY (`QUESTION_ID`) REFERENCES `questions` (`QUESTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `question_choices`
--

LOCK TABLES `question_choices` WRITE;
/*!40000 ALTER TABLE `question_choices` DISABLE KEYS */;
/*!40000 ALTER TABLE `question_choices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `questions` (
  `QUESTION_ID` int(11) NOT NULL auto_increment,
  `ANSWER_TYPE` int(11) NOT NULL,
  `QUESTION_STATE` int(11) NOT NULL,
  `QUESTION_TEXT` varchar(1000) NOT NULL,
  `NUMERIC_MIN` int(11) default NULL,
  `NUMERIC_MAX` int(11) default NULL,
  `SHORT_NAME` varchar(50) NOT NULL,
  PRIMARY KEY  (`QUESTION_ID`),
  UNIQUE KEY `SHORT_NAME` (`SHORT_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rank_days_master`
--

DROP TABLE IF EXISTS `rank_days_master`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rank_days_master` (
  `RANK_DAYS_MASTER_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`RANK_DAYS_MASTER_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `rank_days_master_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `rank_days_master`
--

LOCK TABLES `rank_days_master` WRITE;
/*!40000 ALTER TABLE `rank_days_master` DISABLE KEYS */;
INSERT INTO `rank_days_master` VALUES (1,99),(2,100),(3,101),(4,102),(5,103);
/*!40000 ALTER TABLE `rank_days_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommended_amnt_unit`
--

DROP TABLE IF EXISTS `recommended_amnt_unit`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `recommended_amnt_unit` (
  `RECOMMENDED_AMNT_UNIT_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`RECOMMENDED_AMNT_UNIT_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `recommended_amnt_unit_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `recommended_amnt_unit`
--

LOCK TABLES `recommended_amnt_unit` WRITE;
/*!40000 ALTER TABLE `recommended_amnt_unit` DISABLE KEYS */;
INSERT INTO `recommended_amnt_unit` VALUES (1,120),(2,121);
/*!40000 ALTER TABLE `recommended_amnt_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recur_on_day`
--

DROP TABLE IF EXISTS `recur_on_day`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `recur_on_day` (
  `RECUR_ON_DAY_ID` int(11) NOT NULL auto_increment,
  `DETAILS_ID` int(11) NOT NULL,
  `DAYS` smallint(6) default NULL,
  `RANK_OF_DAYS` smallint(6) default NULL,
  `DAY_NUMBER` smallint(6) default NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`RECUR_ON_DAY_ID`),
  KEY `DETAILS_ID` (`DETAILS_ID`),
  KEY `DAYS` (`DAYS`),
  KEY `RANK_OF_DAYS` (`RANK_OF_DAYS`),
  CONSTRAINT `recur_on_day_ibfk_1` FOREIGN KEY (`DETAILS_ID`) REFERENCES `recurrence_detail` (`DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `recur_on_day_ibfk_2` FOREIGN KEY (`DAYS`) REFERENCES `week_days_master` (`WEEK_DAYS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `recur_on_day_ibfk_3` FOREIGN KEY (`RANK_OF_DAYS`) REFERENCES `rank_days_master` (`RANK_DAYS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `recur_on_day`
--

LOCK TABLES `recur_on_day` WRITE;
/*!40000 ALTER TABLE `recur_on_day` DISABLE KEYS */;
INSERT INTO `recur_on_day` VALUES (1,1,2,NULL,NULL,0),(2,2,5,NULL,NULL,1),(3,3,5,NULL,NULL,0),(4,4,5,NULL,NULL,0),(5,5,5,NULL,NULL,0),(6,6,2,NULL,NULL,0),(7,7,5,NULL,NULL,1),(8,8,5,NULL,NULL,0),(9,9,5,NULL,NULL,0),(10,10,5,NULL,NULL,0),(11,11,5,NULL,NULL,0),(12,12,2,NULL,NULL,0),(13,13,5,NULL,NULL,1),(14,14,5,NULL,NULL,0),(15,15,5,NULL,NULL,0),(16,16,5,NULL,NULL,0),(17,17,5,NULL,NULL,0);
/*!40000 ALTER TABLE `recur_on_day` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recurrence_detail`
--

DROP TABLE IF EXISTS `recurrence_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `recurrence_detail` (
  `DETAILS_ID` int(11) NOT NULL auto_increment,
  `MEETING_ID` int(11) NOT NULL,
  `RECURRENCE_ID` smallint(6) default NULL,
  `RECUR_AFTER` smallint(6) NOT NULL,
  `VERSION_NO` int(11) default NULL,
  PRIMARY KEY  (`DETAILS_ID`),
  KEY `RECURRENCE_ID` (`RECURRENCE_ID`),
  KEY `MEETING_ID` (`MEETING_ID`),
  CONSTRAINT `recurrence_detail_ibfk_1` FOREIGN KEY (`RECURRENCE_ID`) REFERENCES `recurrence_type` (`RECURRENCE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `recurrence_detail_ibfk_2` FOREIGN KEY (`MEETING_ID`) REFERENCES `meeting` (`MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `recurrence_detail`
--

LOCK TABLES `recurrence_detail` WRITE;
/*!40000 ALTER TABLE `recurrence_detail` DISABLE KEYS */;
INSERT INTO `recurrence_detail` VALUES (1,1,1,1,0),(2,2,1,1,1),(3,3,1,1,0),(4,4,1,1,0),(5,5,1,1,0),(6,6,1,1,0),(7,7,1,1,1),(8,8,1,1,0),(9,9,1,1,0),(10,10,1,1,0),(11,11,1,1,0),(12,12,1,1,0),(13,13,1,1,1),(14,14,1,1,0),(15,15,1,1,0),(16,16,1,1,0),(17,17,1,1,0);
/*!40000 ALTER TABLE `recurrence_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recurrence_type`
--

DROP TABLE IF EXISTS `recurrence_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `recurrence_type` (
  `RECURRENCE_ID` smallint(6) NOT NULL auto_increment,
  `RECURRENCE_NAME` varchar(50) default NULL,
  `DESCRIPTION` varchar(200) NOT NULL,
  PRIMARY KEY  (`RECURRENCE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `recurrence_type`
--

LOCK TABLES `recurrence_type` WRITE;
/*!40000 ALTER TABLE `recurrence_type` DISABLE KEYS */;
INSERT INTO `recurrence_type` VALUES (1,'Week(s)','Weekly Recurrence'),(2,'Month(s)','Monthly Recurrence'),(3,'Day(s)','Daily Recurrence');
/*!40000 ALTER TABLE `recurrence_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repayment_rule`
--

DROP TABLE IF EXISTS `repayment_rule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `repayment_rule` (
  `REPAYMENT_RULE_ID` smallint(6) NOT NULL auto_increment,
  `REPAYMENT_RULE_LOOKUP_ID` int(11) default NULL,
  PRIMARY KEY  (`REPAYMENT_RULE_ID`),
  KEY `REPAYMENT_RULE_LOOKUP_ID` (`REPAYMENT_RULE_LOOKUP_ID`),
  CONSTRAINT `repayment_rule_ibfk_1` FOREIGN KEY (`REPAYMENT_RULE_LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `repayment_rule`
--

LOCK TABLES `repayment_rule` WRITE;
/*!40000 ALTER TABLE `repayment_rule` DISABLE KEYS */;
INSERT INTO `repayment_rule` VALUES (1,576),(2,577),(3,578);
/*!40000 ALTER TABLE `repayment_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report` (
  `REPORT_ID` smallint(6) NOT NULL auto_increment,
  `REPORT_CATEGORY_ID` smallint(6) default NULL,
  `REPORT_NAME` varchar(100) default NULL,
  `REPORT_IDENTIFIER` varchar(100) default NULL,
  `ACTIVITY_ID` smallint(6) default NULL,
  `REPORT_ACTIVE` smallint(6) default NULL,
  PRIMARY KEY  (`REPORT_ID`),
  KEY `REPORT_CATEGORY_ID` (`REPORT_CATEGORY_ID`),
  KEY `REPORT_ibfk_2` (`ACTIVITY_ID`),
  CONSTRAINT `REPORT_ibfk_2` FOREIGN KEY (`ACTIVITY_ID`) REFERENCES `activity` (`ACTIVITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `report_ibfk_1` FOREIGN KEY (`REPORT_CATEGORY_ID`) REFERENCES `report_category` (`REPORT_CATEGORY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
INSERT INTO `report` VALUES (1,1,'Collection Sheet Report','collection_sheet_report',229,1),(2,6,'Branch Cash Confirmation Report','branch_cash_confirmation_report',231,1),(3,6,'Branch Progress Report','branch_progress_report',232,1);
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_category`
--

DROP TABLE IF EXISTS `report_category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_category` (
  `REPORT_CATEGORY_ID` smallint(6) NOT NULL auto_increment,
  `REPORT_CATEGORY_VALUE` varchar(100) default NULL,
  `ACTIVITY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`REPORT_CATEGORY_ID`),
  KEY `REPORT_CATEGORY_ibfk_1` (`ACTIVITY_ID`),
  CONSTRAINT `REPORT_CATEGORY_ibfk_1` FOREIGN KEY (`ACTIVITY_ID`) REFERENCES `activity` (`ACTIVITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_category`
--

LOCK TABLES `report_category` WRITE;
/*!40000 ALTER TABLE `report_category` DISABLE KEYS */;
INSERT INTO `report_category` VALUES (1,'Client Detail',145),(2,'Performance',148),(3,'Center',146),(4,'Loan Product Detail',149),(5,'Status',147),(6,'Analysis',150),(7,'Miscellaneous',151);
/*!40000 ALTER TABLE `report_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_datasource`
--

DROP TABLE IF EXISTS `report_datasource`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_datasource` (
  `DATASOURCE_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(255) NOT NULL default '',
  `DRIVER` varchar(255) default NULL,
  `URL` varchar(255) NOT NULL default '',
  `USERNAME` varchar(255) default NULL,
  `PASSWORD` varchar(255) default NULL,
  `MAX_IDLE` int(11) default NULL,
  `MAX_ACTIVE` int(11) default NULL,
  `MAX_WAIT` bigint(20) default NULL,
  `VALIDATION_QUERY` varchar(255) default NULL,
  `JNDI` tinyint(4) default NULL,
  PRIMARY KEY  (`DATASOURCE_ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_2` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_datasource`
--

LOCK TABLES `report_datasource` WRITE;
/*!40000 ALTER TABLE `report_datasource` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_datasource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_jasper_map`
--

DROP TABLE IF EXISTS `report_jasper_map`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_jasper_map` (
  `REPORT_ID` smallint(6) NOT NULL auto_increment,
  `REPORT_CATEGORY_ID` smallint(6) default NULL,
  `REPORT_NAME` varchar(100) default NULL,
  `REPORT_IDENTIFIER` varchar(100) default NULL,
  `REPORT_JASPER` varchar(100) default NULL,
  PRIMARY KEY  (`REPORT_ID`),
  KEY `REPORT_CATEGORY_ID` (`REPORT_CATEGORY_ID`),
  CONSTRAINT `report_jasper_map_ibfk_1` FOREIGN KEY (`REPORT_CATEGORY_ID`) REFERENCES `report_category` (`REPORT_CATEGORY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_jasper_map`
--

LOCK TABLES `report_jasper_map` WRITE;
/*!40000 ALTER TABLE `report_jasper_map` DISABLE KEYS */;
INSERT INTO `report_jasper_map` VALUES (1,1,'Collection Sheet Report','collection_sheet_report','CollectionSheetReport.rptdesign'),(2,6,'Branch Cash Confirmation Report','branch_cash_confirmation_report','BranchCashConfirmationReport.rptdesign'),(3,6,'Branch Progress Report','branch_progress_report','ProgressReport.rptdesign');
/*!40000 ALTER TABLE `report_jasper_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_parameter`
--

DROP TABLE IF EXISTS `report_parameter`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_parameter` (
  `PARAMETER_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(255) NOT NULL default '',
  `TYPE` varchar(255) NOT NULL default '',
  `CLASSNAME` varchar(255) NOT NULL default '',
  `DATA` text,
  `DATASOURCE_ID` int(11) default NULL,
  `DESCRIPTION` varchar(255) default NULL,
  PRIMARY KEY  (`PARAMETER_ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_2` (`NAME`),
  KEY `DATASOURCE_ID` (`DATASOURCE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_parameter`
--

LOCK TABLES `report_parameter` WRITE;
/*!40000 ALTER TABLE `report_parameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_parameter_map`
--

DROP TABLE IF EXISTS `report_parameter_map`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_parameter_map` (
  `REPORT_ID` int(11) NOT NULL default '0',
  `PARAMETER_ID` int(11) default NULL,
  `REQUIRED` tinyint(4) default NULL,
  `SORT_ORDER` int(11) default NULL,
  `STEP` int(11) default NULL,
  `MAP_ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`MAP_ID`),
  KEY `REPORT_ID` (`REPORT_ID`),
  KEY `PARAMETER_ID` (`PARAMETER_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_parameter_map`
--

LOCK TABLES `report_parameter_map` WRITE;
/*!40000 ALTER TABLE `report_parameter_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_parameter_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `role` (
  `ROLE_ID` smallint(6) NOT NULL,
  `ROLE_NAME` varchar(50) NOT NULL,
  `VERSION_NO` int(11) NOT NULL,
  `CREATED_BY` smallint(6) default NULL,
  `CREATED_DATE` date default NULL,
  `UPDATED_BY` smallint(6) default NULL,
  `UPDATED_DATE` date default NULL,
  PRIMARY KEY  (`ROLE_ID`),
  KEY `CREATED_BY` (`CREATED_BY`),
  KEY `UPDATED_BY` (`UPDATED_BY`),
  CONSTRAINT `role_ibfk_1` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `role_ibfk_2` FOREIGN KEY (`UPDATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Admin',1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_activity`
--

DROP TABLE IF EXISTS `roles_activity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `roles_activity` (
  `ACTIVITY_ID` smallint(6) NOT NULL,
  `ROLE_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`ACTIVITY_ID`,`ROLE_ID`),
  KEY `ROLE_ID` (`ROLE_ID`),
  CONSTRAINT `roles_activity_ibfk_1` FOREIGN KEY (`ACTIVITY_ID`) REFERENCES `activity` (`ACTIVITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `roles_activity_ibfk_2` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ROLE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `roles_activity`
--

LOCK TABLES `roles_activity` WRITE;
/*!40000 ALTER TABLE `roles_activity` DISABLE KEYS */;
INSERT INTO `roles_activity` VALUES (3,1),(4,1),(6,1),(7,1),(9,1),(10,1),(15,1),(16,1),(19,1),(20,1),(21,1),(23,1),(24,1),(25,1),(35,1),(36,1),(37,1),(38,1),(39,1),(40,1),(41,1),(42,1),(43,1),(44,1),(46,1),(47,1),(48,1),(49,1),(50,1),(51,1),(52,1),(53,1),(54,1),(55,1),(57,1),(58,1),(59,1),(60,1),(61,1),(62,1),(63,1),(64,1),(65,1),(66,1),(68,1),(69,1),(70,1),(71,1),(72,1),(73,1),(74,1),(75,1),(76,1),(77,1),(79,1),(80,1),(81,1),(82,1),(83,1),(85,1),(86,1),(87,1),(88,1),(91,1),(92,1),(94,1),(95,1),(97,1),(98,1),(101,1),(102,1),(103,1),(104,1),(105,1),(106,1),(108,1),(109,1),(110,1),(115,1),(116,1),(118,1),(119,1),(120,1),(121,1),(122,1),(126,1),(127,1),(128,1),(129,1),(131,1),(135,1),(136,1),(137,1),(138,1),(139,1),(140,1),(141,1),(145,1),(146,1),(147,1),(148,1),(149,1),(150,1),(151,1),(178,1),(179,1),(180,1),(181,1),(182,1),(183,1),(184,1),(185,1),(186,1),(187,1),(188,1),(189,1),(190,1),(191,1),(192,1),(193,1),(194,1),(195,1),(197,1),(198,1),(199,1),(200,1),(201,1),(202,1),(203,1),(204,1),(205,1),(206,1),(208,1),(209,1),(210,1),(211,1),(213,1),(214,1),(215,1),(216,1),(217,1),(218,1),(219,1),(220,1),(221,1),(222,1),(223,1),(224,1),(225,1),(226,1),(227,1),(228,1),(229,1),(230,1),(231,1),(232,1);
/*!40000 ALTER TABLE `roles_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saving_schedule`
--

DROP TABLE IF EXISTS `saving_schedule`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `saving_schedule` (
  `ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `CURRENCY_ID` smallint(6) default NULL,
  `ACTION_DATE` date default NULL,
  `DEPOSIT` decimal(10,3) NOT NULL,
  `DEPOSIT_CURRENCY_ID` smallint(6) default NULL,
  `DEPOSIT_PAID` decimal(10,3) default NULL,
  `DEPOSIT_PAID_CURRENCY_ID` smallint(6) default NULL,
  `PAYMENT_STATUS` smallint(6) NOT NULL,
  `INSTALLMENT_ID` smallint(6) NOT NULL,
  `PAYMENT_DATE` date default NULL,
  `PARENT_FLAG` smallint(6) default NULL,
  `VERSION_NO` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `CURRENCY_ID` (`CURRENCY_ID`),
  KEY `DEPOSIT_CURRENCY_ID` (`DEPOSIT_CURRENCY_ID`),
  KEY `DEPOSIT_PAID_CURRENCY_ID` (`DEPOSIT_PAID_CURRENCY_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  CONSTRAINT `saving_schedule_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `saving_schedule_ibfk_2` FOREIGN KEY (`CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `saving_schedule_ibfk_3` FOREIGN KEY (`DEPOSIT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `saving_schedule_ibfk_4` FOREIGN KEY (`DEPOSIT_PAID_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `saving_schedule_ibfk_5` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `saving_schedule`
--

LOCK TABLES `saving_schedule` WRITE;
/*!40000 ALTER TABLE `saving_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `saving_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `savings_account`
--

DROP TABLE IF EXISTS `savings_account`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `savings_account` (
  `ACCOUNT_ID` int(11) NOT NULL,
  `ACTIVATION_DATE` date default NULL,
  `SAVINGS_BALANCE` decimal(10,3) default NULL,
  `SAVINGS_BALANCE_CURRENCY_ID` smallint(6) default NULL,
  `RECOMMENDED_AMOUNT` decimal(10,3) default NULL,
  `RECOMMENDED_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `RECOMMENDED_AMNT_UNIT_ID` smallint(6) default NULL,
  `SAVINGS_TYPE_ID` smallint(6) NOT NULL,
  `INT_TO_BE_POSTED` decimal(10,3) default NULL,
  `INT_TO_BE_POSTED_CURRENCY_ID` smallint(6) default NULL,
  `LAST_INT_CALC_DATE` date default NULL,
  `LAST_INT_POST_DATE` date default NULL,
  `NEXT_INT_CALC_DATE` date default NULL,
  `NEXT_INT_POST_DATE` date default NULL,
  `INTER_INT_CALC_DATE` date default NULL,
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `INTEREST_RATE` decimal(13,10) NOT NULL,
  `INTEREST_CALCULATION_TYPE_ID` smallint(6) NOT NULL,
  `TIME_PER_FOR_INT_CALC` int(11) default NULL,
  `MIN_AMNT_FOR_INT` decimal(10,3) default NULL,
  `MIN_AMNT_FOR_INT_CURRENCY_ID` smallint(6) default NULL,
  PRIMARY KEY  (`ACCOUNT_ID`),
  KEY `SAVINGS_BALANCE_CURRENCY_ID` (`SAVINGS_BALANCE_CURRENCY_ID`),
  KEY `RECOMMENDED_AMOUNT_CURRENCY_ID` (`RECOMMENDED_AMOUNT_CURRENCY_ID`),
  KEY `INT_TO_BE_POSTED_CURRENCY_ID` (`INT_TO_BE_POSTED_CURRENCY_ID`),
  KEY `RECOMMENDED_AMNT_UNIT_ID` (`RECOMMENDED_AMNT_UNIT_ID`),
  KEY `SAVINGS_TYPE_ID` (`SAVINGS_TYPE_ID`),
  KEY `PRD_OFFERING_ID` (`PRD_OFFERING_ID`),
  KEY `INTEREST_CALCULATION_TYPE_ID` (`INTEREST_CALCULATION_TYPE_ID`),
  KEY `TIME_PER_FOR_INT_CALC` (`TIME_PER_FOR_INT_CALC`),
  KEY `MIN_AMNT_FOR_INT_CURRENCY_ID` (`MIN_AMNT_FOR_INT_CURRENCY_ID`),
  CONSTRAINT `savings_account_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_10` FOREIGN KEY (`MIN_AMNT_FOR_INT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_2` FOREIGN KEY (`SAVINGS_BALANCE_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_3` FOREIGN KEY (`RECOMMENDED_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_4` FOREIGN KEY (`INT_TO_BE_POSTED_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_5` FOREIGN KEY (`RECOMMENDED_AMNT_UNIT_ID`) REFERENCES `recommended_amnt_unit` (`RECOMMENDED_AMNT_UNIT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_6` FOREIGN KEY (`SAVINGS_TYPE_ID`) REFERENCES `savings_type` (`SAVINGS_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_7` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_8` FOREIGN KEY (`INTEREST_CALCULATION_TYPE_ID`) REFERENCES `interest_calculation_types` (`INTEREST_CALCULATION_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_account_ibfk_9` FOREIGN KEY (`TIME_PER_FOR_INT_CALC`) REFERENCES `meeting` (`MEETING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `savings_account`
--

LOCK TABLES `savings_account` WRITE;
/*!40000 ALTER TABLE `savings_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `savings_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `savings_activity_details`
--

DROP TABLE IF EXISTS `savings_activity_details`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `savings_activity_details` (
  `ID` int(11) NOT NULL auto_increment,
  `CREATED_BY` smallint(6) default NULL,
  `ACCOUNT_ID` int(11) NOT NULL,
  `CREATED_DATE` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `ACCOUNT_ACTION_ID` smallint(6) NOT NULL,
  `AMOUNT` decimal(10,3) NOT NULL,
  `AMOUNT_CURRENCY_ID` smallint(6) NOT NULL,
  `BALANCE_AMOUNT` decimal(10,3) NOT NULL,
  `BALANCE_AMOUNT_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `CREATED_BY` (`CREATED_BY`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `ACCOUNT_ACTION_ID` (`ACCOUNT_ACTION_ID`),
  KEY `AMOUNT_CURRENCY_ID` (`AMOUNT_CURRENCY_ID`),
  KEY `BALANCE_AMOUNT_CURRENCY_ID` (`BALANCE_AMOUNT_CURRENCY_ID`),
  CONSTRAINT `savings_activity_details_ibfk_1` FOREIGN KEY (`CREATED_BY`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_activity_details_ibfk_2` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_activity_details_ibfk_3` FOREIGN KEY (`ACCOUNT_ACTION_ID`) REFERENCES `account_action` (`ACCOUNT_ACTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_activity_details_ibfk_4` FOREIGN KEY (`AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_activity_details_ibfk_5` FOREIGN KEY (`BALANCE_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `savings_activity_details`
--

LOCK TABLES `savings_activity_details` WRITE;
/*!40000 ALTER TABLE `savings_activity_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `savings_activity_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `savings_offering`
--

DROP TABLE IF EXISTS `savings_offering`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `savings_offering` (
  `PRD_OFFERING_ID` smallint(6) NOT NULL,
  `INTEREST_CALCULATION_TYPE_ID` smallint(6) NOT NULL,
  `SAVINGS_TYPE_ID` smallint(6) NOT NULL,
  `RECOMMENDED_AMNT_UNIT_ID` smallint(6) default NULL,
  `RECOMMENDED_AMOUNT` decimal(10,3) default NULL,
  `RECOMMENDED_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_RATE` decimal(13,10) NOT NULL,
  `MAX_AMNT_WITHDRAWL` decimal(10,3) default NULL,
  `MAX_AMNT_WITHDRAWL_CURRENCY_ID` smallint(6) default NULL,
  `MIN_AMNT_FOR_INT` decimal(10,3) default NULL,
  `MIN_AMNT_FOR_INT_CURRENCY_ID` smallint(6) default NULL,
  `DEPOSIT_GLCODE_ID` smallint(6) NOT NULL,
  `INTEREST_GLCODE_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`PRD_OFFERING_ID`),
  KEY `RECOMMENDED_AMNT_UNIT_ID` (`RECOMMENDED_AMNT_UNIT_ID`),
  KEY `SAVINGS_TYPE_ID` (`SAVINGS_TYPE_ID`),
  KEY `INTEREST_CALCULATION_TYPE_ID` (`INTEREST_CALCULATION_TYPE_ID`),
  KEY `RECOMMENDED_AMOUNT_CURRENCY_ID` (`RECOMMENDED_AMOUNT_CURRENCY_ID`),
  KEY `MAX_AMNT_WITHDRAWL_CURRENCY_ID` (`MAX_AMNT_WITHDRAWL_CURRENCY_ID`),
  KEY `MIN_AMNT_FOR_INT_CURRENCY_ID` (`MIN_AMNT_FOR_INT_CURRENCY_ID`),
  KEY `DEPOSIT_GLCODE_ID` (`DEPOSIT_GLCODE_ID`),
  KEY `INTEREST_GLCODE_ID` (`INTEREST_GLCODE_ID`),
  CONSTRAINT `savings_offering_ibfk_1` FOREIGN KEY (`PRD_OFFERING_ID`) REFERENCES `prd_offering` (`PRD_OFFERING_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_2` FOREIGN KEY (`RECOMMENDED_AMNT_UNIT_ID`) REFERENCES `recommended_amnt_unit` (`RECOMMENDED_AMNT_UNIT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_3` FOREIGN KEY (`SAVINGS_TYPE_ID`) REFERENCES `savings_type` (`SAVINGS_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_4` FOREIGN KEY (`INTEREST_CALCULATION_TYPE_ID`) REFERENCES `interest_calculation_types` (`INTEREST_CALCULATION_TYPE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_5` FOREIGN KEY (`RECOMMENDED_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_6` FOREIGN KEY (`MAX_AMNT_WITHDRAWL_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_7` FOREIGN KEY (`MIN_AMNT_FOR_INT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_8` FOREIGN KEY (`DEPOSIT_GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_offering_ibfk_9` FOREIGN KEY (`INTEREST_GLCODE_ID`) REFERENCES `gl_code` (`GLCODE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `savings_offering`
--

LOCK TABLES `savings_offering` WRITE;
/*!40000 ALTER TABLE `savings_offering` DISABLE KEYS */;
/*!40000 ALTER TABLE `savings_offering` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `savings_performance`
--

DROP TABLE IF EXISTS `savings_performance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `savings_performance` (
  `ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `TOTAL_DEPOSITS` decimal(10,3) default NULL,
  `TOTAL_DEPOSITS_CURRENCY_ID` smallint(6) default NULL,
  `TOTAL_WITHDRAWALS` decimal(10,3) default NULL,
  `TOTAL_WITHDRAWALS_CURRENCY_ID` smallint(6) default NULL,
  `TOTAL_INTEREST_EARNED` decimal(10,3) default NULL,
  `TOTAL_INTEREST_EARNED_CURRENCY_ID` smallint(6) default NULL,
  `MISSED_DEPOSITS` smallint(6) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `TOTAL_DEPOSITS_CURRENCY_ID` (`TOTAL_DEPOSITS_CURRENCY_ID`),
  KEY `TOTAL_WITHDRAWALS_CURRENCY_ID` (`TOTAL_WITHDRAWALS_CURRENCY_ID`),
  KEY `TOTAL_INTEREST_EARNED_CURRENCY_ID` (`TOTAL_INTEREST_EARNED_CURRENCY_ID`),
  CONSTRAINT `savings_performance_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_performance_ibfk_2` FOREIGN KEY (`TOTAL_DEPOSITS_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_performance_ibfk_3` FOREIGN KEY (`TOTAL_WITHDRAWALS_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_performance_ibfk_4` FOREIGN KEY (`TOTAL_INTEREST_EARNED_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `savings_performance`
--

LOCK TABLES `savings_performance` WRITE;
/*!40000 ALTER TABLE `savings_performance` DISABLE KEYS */;
/*!40000 ALTER TABLE `savings_performance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `savings_trxn_detail`
--

DROP TABLE IF EXISTS `savings_trxn_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `savings_trxn_detail` (
  `ACCOUNT_TRXN_ID` int(11) NOT NULL,
  `DEPOSIT_AMOUNT` decimal(10,3) default NULL,
  `DEPOSIT_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `WITHDRAWAL_AMOUNT` decimal(10,3) default NULL,
  `WITHDRAWAL_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `INTEREST_AMOUNT` decimal(10,3) default NULL,
  `INTEREST_AMOUNT_CURRENCY_ID` smallint(6) default NULL,
  `BALANCE` decimal(10,3) default NULL,
  `BALANCE_CURRENCY_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`ACCOUNT_TRXN_ID`),
  KEY `DEPOSIT_AMOUNT_CURRENCY_ID` (`DEPOSIT_AMOUNT_CURRENCY_ID`),
  KEY `WITHDRAWAL_AMOUNT_CURRENCY_ID` (`WITHDRAWAL_AMOUNT_CURRENCY_ID`),
  KEY `INTEREST_AMOUNT_CURRENCY_ID` (`INTEREST_AMOUNT_CURRENCY_ID`),
  KEY `BALANCE_CURRENCY_ID` (`BALANCE_CURRENCY_ID`),
  CONSTRAINT `savings_trxn_detail_ibfk_1` FOREIGN KEY (`ACCOUNT_TRXN_ID`) REFERENCES `account_trxn` (`ACCOUNT_TRXN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_trxn_detail_ibfk_2` FOREIGN KEY (`DEPOSIT_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_trxn_detail_ibfk_3` FOREIGN KEY (`WITHDRAWAL_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_trxn_detail_ibfk_4` FOREIGN KEY (`INTEREST_AMOUNT_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `savings_trxn_detail_ibfk_5` FOREIGN KEY (`BALANCE_CURRENCY_ID`) REFERENCES `currency` (`CURRENCY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `savings_trxn_detail`
--

LOCK TABLES `savings_trxn_detail` WRITE;
/*!40000 ALTER TABLE `savings_trxn_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `savings_trxn_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `savings_type`
--

DROP TABLE IF EXISTS `savings_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `savings_type` (
  `SAVINGS_TYPE_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`SAVINGS_TYPE_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `savings_type_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `savings_type`
--

LOCK TABLES `savings_type` WRITE;
/*!40000 ALTER TABLE `savings_type` DISABLE KEYS */;
INSERT INTO `savings_type` VALUES (1,118),(2,119);
/*!40000 ALTER TABLE `savings_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduled_tasks`
--

DROP TABLE IF EXISTS `scheduled_tasks`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `scheduled_tasks` (
  `TASKID` int(11) NOT NULL auto_increment,
  `TASKNAME` varchar(200) default NULL,
  `DESCRIPTION` varchar(500) default NULL,
  `STARTTIME` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `ENDTIME` timestamp NOT NULL default '0000-00-00 00:00:00',
  `STATUS` smallint(6) default NULL,
  PRIMARY KEY  (`TASKID`)
) ENGINE=InnoDB AUTO_INCREMENT=430 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `scheduled_tasks`
--

LOCK TABLES `scheduled_tasks` WRITE;
/*!40000 ALTER TABLE `scheduled_tasks` DISABLE KEYS */;
INSERT INTO `scheduled_tasks` VALUES (1,'ProductStatus','Finished Successfully','2008-11-11 08:00:00','2008-11-11 08:00:01',1),(2,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-11 08:00:01','2008-11-11 08:00:04',1),(3,'SavingsIntCalcTask','Finished Successfully','2008-11-11 08:00:04','2008-11-11 08:00:04',1),(4,'SavingsIntPostingTask','Finished Successfully','2008-11-11 08:00:04','2008-11-11 08:00:04',1),(5,'LoanArrearsTask','Finished Successfully','2008-11-11 08:00:04','2008-11-11 08:00:07',1),(6,'ApplyCustomerFeeTask','Finished Successfully','2008-11-11 08:00:07','2008-11-11 08:00:08',1),(7,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-11 08:00:08','2008-11-11 08:00:08',1),(8,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-11 08:00:08','2008-11-11 08:00:08',1),(9,'LoanArrearsAgingTask','Finished Successfully','2008-11-11 08:00:08','2008-11-11 08:00:08',1),(10,'ApplyHolidayChangesTask','Finished Successfully','2008-11-11 08:00:08','2008-11-11 08:00:08',1),(11,'RegenerateScheduleTask','Finished Successfully','2008-11-11 08:00:08','2008-11-11 08:00:08',1),(12,'ProductStatus','Finished Successfully','2008-11-11 08:02:19','2008-11-11 08:02:23',1),(13,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-11 08:02:24','2008-11-11 08:02:25',1),(14,'SavingsIntCalcTask','Finished Successfully','2008-11-11 08:02:25','2008-11-11 08:02:25',1),(15,'LoanArrearsTask','Finished Successfully','2008-11-11 08:02:25','2008-11-11 08:02:27',1),(16,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-11 08:02:27','2008-11-11 08:02:27',1),(17,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-11 08:02:28','2008-11-11 08:02:28',1),(18,'SavingsIntPostingTask','Finished Successfully','2008-11-11 08:02:28','2008-11-11 08:02:28',1),(19,'LoanArrearsAgingTask','Finished Successfully','2008-11-11 08:02:28','2008-11-11 08:02:28',1),(20,'ApplyHolidayChangesTask','Finished Successfully','2008-11-11 08:02:28','2008-11-11 08:02:28',1),(21,'ApplyCustomerFeeTask','Finished Successfully','2008-11-11 08:02:28','2008-11-11 08:02:28',1),(22,'RegenerateScheduleTask','Finished Successfully','2008-11-11 08:02:28','2008-11-11 08:02:28',1),(23,'PortfolioAtRiskTask','Finished Successfully','2008-11-11 08:05:00','2008-11-11 08:05:00',1),(24,'PortfolioAtRiskTask','Finished Successfully','2008-11-11 08:05:00','2008-11-11 08:05:01',1),(25,'BranchReportTask','Finished Successfully','2008-11-11 08:05:01','2008-11-11 08:05:01',1),(26,'BranchReportTask','Finished Successfully','2008-11-11 08:05:01','2008-11-11 08:05:01',1),(27,'ProductStatus','Finished Successfully','2008-11-12 08:00:01','2008-11-12 08:00:01',1),(28,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-12 08:00:02','2008-11-12 08:00:02',1),(29,'SavingsIntCalcTask','Finished Successfully','2008-11-12 08:00:04','2008-11-12 08:00:04',1),(30,'SavingsIntPostingTask','Finished Successfully','2008-11-12 08:00:04','2008-11-12 08:00:04',1),(31,'LoanArrearsTask','Finished Successfully','2008-11-12 08:00:04','2008-11-12 08:00:04',1),(32,'ApplyCustomerFeeTask','Finished Successfully','2008-11-12 08:00:07','2008-11-12 08:00:07',1),(33,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-12 08:00:08','2008-11-12 08:00:08',1),(34,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-12 08:00:08','2008-11-12 08:00:08',1),(35,'LoanArrearsAgingTask','Finished Successfully','2008-11-12 08:00:08','2008-11-12 08:00:08',1),(36,'ApplyHolidayChangesTask','Finished Successfully','2008-11-12 08:00:08','2008-11-12 08:00:08',1),(37,'RegenerateScheduleTask','Finished Successfully','2008-11-12 08:00:08','2008-11-12 08:00:08',1),(38,'ProductStatus','Finished Successfully','2008-11-12 08:02:12','2008-11-12 08:02:12',1),(39,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-12 08:02:24','2008-11-12 08:02:24',1),(40,'SavingsIntCalcTask','Finished Successfully','2008-11-12 08:02:25','2008-11-12 08:02:25',1),(41,'LoanArrearsTask','Finished Successfully','2008-11-12 08:02:25','2008-11-12 08:02:25',1),(42,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-12 08:02:27','2008-11-12 08:02:27',1),(43,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-12 08:02:28','2008-11-12 08:02:28',1),(44,'SavingsIntPostingTask','Finished Successfully','2008-11-12 08:02:28','2008-11-12 08:02:28',1),(45,'LoanArrearsAgingTask','Finished Successfully','2008-11-12 08:02:28','2008-11-12 08:02:28',1),(46,'ApplyHolidayChangesTask','Finished Successfully','2008-11-12 08:02:28','2008-11-12 08:02:28',1),(47,'ApplyCustomerFeeTask','Finished Successfully','2008-11-12 08:02:28','2008-11-12 08:02:28',1),(48,'RegenerateScheduleTask','Finished Successfully','2008-11-12 08:02:28','2008-11-12 08:02:28',1),(49,'PortfolioAtRiskTask','Finished Successfully','2008-11-12 08:05:00','2008-11-12 08:05:00',1),(50,'BranchReportTask','Finished Successfully','2008-11-12 08:05:00','2008-11-12 08:05:00',1),(51,'PortfolioAtRiskTask','Finished Successfully','2008-11-12 08:05:00','2008-11-12 08:05:00',1),(52,'BranchReportTask','Finished Successfully','2008-11-12 08:05:01','2008-11-12 08:05:01',1),(53,'ProductStatus','Finished Successfully','2008-11-13 08:00:01','2008-11-13 08:00:01',1),(54,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-13 08:00:02','2008-11-13 08:00:02',1),(55,'SavingsIntCalcTask','Finished Successfully','2008-11-13 08:00:04','2008-11-13 08:00:04',1),(56,'SavingsIntPostingTask','Finished Successfully','2008-11-13 08:00:04','2008-11-13 08:00:04',1),(57,'LoanArrearsTask','Finished Successfully','2008-11-13 08:00:04','2008-11-13 08:00:04',1),(58,'ApplyCustomerFeeTask','Finished Successfully','2008-11-13 08:00:07','2008-11-13 08:00:07',1),(59,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-13 08:00:08','2008-11-13 08:00:08',1),(60,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-13 08:00:08','2008-11-13 08:00:08',1),(61,'LoanArrearsAgingTask','Finished Successfully','2008-11-13 08:00:08','2008-11-13 08:00:08',1),(62,'ApplyHolidayChangesTask','Finished Successfully','2008-11-13 08:00:08','2008-11-13 08:00:08',1),(63,'RegenerateScheduleTask','Finished Successfully','2008-11-13 08:00:08','2008-11-13 08:00:08',1),(64,'ProductStatus','Finished Successfully','2008-11-13 08:02:12','2008-11-13 08:02:12',1),(65,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-13 08:02:24','2008-11-13 08:02:24',1),(66,'SavingsIntCalcTask','Finished Successfully','2008-11-13 08:02:25','2008-11-13 08:02:25',1),(67,'LoanArrearsTask','Finished Successfully','2008-11-13 08:02:25','2008-11-13 08:02:25',1),(68,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-13 08:02:27','2008-11-13 08:02:27',1),(69,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-13 08:02:28','2008-11-13 08:02:28',1),(70,'SavingsIntPostingTask','Finished Successfully','2008-11-13 08:02:28','2008-11-13 08:02:28',1),(71,'LoanArrearsAgingTask','Finished Successfully','2008-11-13 08:02:28','2008-11-13 08:02:28',1),(72,'ApplyHolidayChangesTask','Finished Successfully','2008-11-13 08:02:28','2008-11-13 08:02:28',1),(73,'ApplyCustomerFeeTask','Finished Successfully','2008-11-13 08:02:28','2008-11-13 08:02:28',1),(74,'RegenerateScheduleTask','Finished Successfully','2008-11-13 08:02:28','2008-11-13 08:02:28',1),(75,'PortfolioAtRiskTask','Finished Successfully','2008-11-13 08:05:00','2008-11-13 08:05:00',1),(76,'BranchReportTask','Finished Successfully','2008-11-13 08:05:00','2008-11-13 08:05:00',1),(77,'PortfolioAtRiskTask','Finished Successfully','2008-11-13 08:05:00','2008-11-13 08:05:00',1),(78,'BranchReportTask','Finished Successfully','2008-11-13 08:05:01','2008-11-13 08:05:01',1),(79,'ProductStatus','Finished Successfully','2008-11-14 08:00:01','2008-11-14 08:00:01',1),(80,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-14 08:00:01','2008-11-14 08:00:02',1),(81,'SavingsIntCalcTask','Finished Successfully','2008-11-14 08:00:04','2008-11-14 08:00:04',1),(82,'SavingsIntPostingTask','Finished Successfully','2008-11-14 08:00:04','2008-11-14 08:00:04',1),(83,'LoanArrearsTask','Finished Successfully','2008-11-14 08:00:04','2008-11-14 08:00:04',1),(84,'ApplyCustomerFeeTask','Finished Successfully','2008-11-14 08:00:07','2008-11-14 08:00:07',1),(85,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-14 08:00:08','2008-11-14 08:00:08',1),(86,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-14 08:00:08','2008-11-14 08:00:08',1),(87,'LoanArrearsAgingTask','Finished Successfully','2008-11-14 08:00:08','2008-11-14 08:00:08',1),(88,'ApplyHolidayChangesTask','Finished Successfully','2008-11-14 08:00:08','2008-11-14 08:00:08',1),(89,'RegenerateScheduleTask','Finished Successfully','2008-11-14 08:00:08','2008-11-14 08:00:09',1),(90,'ProductStatus','Finished Successfully','2008-11-14 08:02:12','2008-11-14 08:02:12',1),(91,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-14 08:02:24','2008-11-14 08:02:24',1),(92,'SavingsIntCalcTask','Finished Successfully','2008-11-14 08:02:25','2008-11-14 08:02:25',1),(93,'LoanArrearsTask','Finished Successfully','2008-11-14 08:02:25','2008-11-14 08:02:25',1),(94,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-14 08:02:27','2008-11-14 08:02:27',1),(95,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-14 08:02:28','2008-11-14 08:02:28',1),(96,'SavingsIntPostingTask','Finished Successfully','2008-11-14 08:02:28','2008-11-14 08:02:28',1),(97,'LoanArrearsAgingTask','Finished Successfully','2008-11-14 08:02:28','2008-11-14 08:02:28',1),(98,'ApplyHolidayChangesTask','Finished Successfully','2008-11-14 08:02:28','2008-11-14 08:02:28',1),(99,'ApplyCustomerFeeTask','Finished Successfully','2008-11-14 08:02:28','2008-11-14 08:02:28',1),(100,'RegenerateScheduleTask','Finished Successfully','2008-11-14 08:02:28','2008-11-14 08:02:28',1),(101,'PortfolioAtRiskTask','Finished Successfully','2008-11-14 08:05:00','2008-11-14 08:05:00',1),(102,'BranchReportTask','Finished Successfully','2008-11-14 08:05:00','2008-11-14 08:05:00',1),(103,'PortfolioAtRiskTask','Finished Successfully','2008-11-14 08:05:00','2008-11-14 08:05:00',1),(104,'BranchReportTask','Finished Successfully','2008-11-14 08:05:01','2008-11-14 08:05:01',1),(105,'ProductStatus','Finished Successfully','2008-11-15 08:00:01','2008-11-15 08:00:02',1),(106,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-15 08:00:02','2008-11-15 08:00:02',1),(107,'SavingsIntCalcTask','Finished Successfully','2008-11-15 08:00:04','2008-11-15 08:00:04',1),(108,'SavingsIntPostingTask','Finished Successfully','2008-11-15 08:00:04','2008-11-15 08:00:04',1),(109,'LoanArrearsTask','Finished Successfully','2008-11-15 08:00:04','2008-11-15 08:00:04',1),(110,'ApplyCustomerFeeTask','Finished Successfully','2008-11-15 08:00:07','2008-11-15 08:00:07',1),(111,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-15 08:00:08','2008-11-15 08:00:08',1),(112,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-15 08:00:08','2008-11-15 08:00:08',1),(113,'LoanArrearsAgingTask','Finished Successfully','2008-11-15 08:00:08','2008-11-15 08:00:08',1),(114,'ApplyHolidayChangesTask','Finished Successfully','2008-11-15 08:00:08','2008-11-15 08:00:08',1),(115,'RegenerateScheduleTask','Finished Successfully','2008-11-15 08:00:08','2008-11-15 08:00:08',1),(116,'ProductStatus','Finished Successfully','2008-11-15 08:02:12','2008-11-15 08:02:12',1),(117,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-15 08:02:24','2008-11-15 08:02:24',1),(118,'SavingsIntCalcTask','Finished Successfully','2008-11-15 08:02:25','2008-11-15 08:02:25',1),(119,'LoanArrearsTask','Finished Successfully','2008-11-15 08:02:25','2008-11-15 08:02:25',1),(120,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-15 08:02:27','2008-11-15 08:02:27',1),(121,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-15 08:02:28','2008-11-15 08:02:28',1),(122,'SavingsIntPostingTask','Finished Successfully','2008-11-15 08:02:28','2008-11-15 08:02:28',1),(123,'LoanArrearsAgingTask','Finished Successfully','2008-11-15 08:02:28','2008-11-15 08:02:28',1),(124,'ApplyHolidayChangesTask','Finished Successfully','2008-11-15 08:02:28','2008-11-15 08:02:28',1),(125,'ApplyCustomerFeeTask','Finished Successfully','2008-11-15 08:02:28','2008-11-15 08:02:28',1),(126,'RegenerateScheduleTask','Finished Successfully','2008-11-15 08:02:28','2008-11-15 08:02:28',1),(127,'PortfolioAtRiskTask','Finished Successfully','2008-11-15 08:05:00','2008-11-15 08:05:00',1),(128,'BranchReportTask','Finished Successfully','2008-11-15 08:05:00','2008-11-15 08:05:00',1),(129,'PortfolioAtRiskTask','Finished Successfully','2008-11-15 08:05:00','2008-11-15 08:05:00',1),(130,'BranchReportTask','Finished Successfully','2008-11-15 08:05:01','2008-11-15 08:05:01',1),(131,'ProductStatus','Finished Successfully','2008-11-16 08:00:01','2008-11-16 08:00:01',1),(132,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-16 08:00:02','2008-11-16 08:00:02',1),(133,'SavingsIntCalcTask','Finished Successfully','2008-11-16 08:00:04','2008-11-16 08:00:04',1),(134,'SavingsIntPostingTask','Finished Successfully','2008-11-16 08:00:04','2008-11-16 08:00:04',1),(135,'LoanArrearsTask','Finished Successfully','2008-11-16 08:00:04','2008-11-16 08:00:04',1),(136,'ApplyCustomerFeeTask','Finished Successfully','2008-11-16 08:00:07','2008-11-16 08:00:07',1),(137,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-16 08:00:08','2008-11-16 08:00:08',1),(138,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-16 08:00:08','2008-11-16 08:00:08',1),(139,'LoanArrearsAgingTask','Finished Successfully','2008-11-16 08:00:08','2008-11-16 08:00:08',1),(140,'ApplyHolidayChangesTask','Finished Successfully','2008-11-16 08:00:08','2008-11-16 08:00:08',1),(141,'RegenerateScheduleTask','Finished Successfully','2008-11-16 08:00:08','2008-11-16 08:00:08',1),(142,'ProductStatus','Finished Successfully','2008-11-16 08:02:12','2008-11-16 08:02:12',1),(143,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-16 08:02:24','2008-11-16 08:02:24',1),(144,'SavingsIntCalcTask','Finished Successfully','2008-11-16 08:02:25','2008-11-16 08:02:25',1),(145,'LoanArrearsTask','Finished Successfully','2008-11-16 08:02:25','2008-11-16 08:02:25',1),(146,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-16 08:02:27','2008-11-16 08:02:27',1),(147,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-16 08:02:28','2008-11-16 08:02:28',1),(148,'SavingsIntPostingTask','Finished Successfully','2008-11-16 08:02:28','2008-11-16 08:02:28',1),(149,'LoanArrearsAgingTask','Finished Successfully','2008-11-16 08:02:28','2008-11-16 08:02:28',1),(150,'ApplyHolidayChangesTask','Finished Successfully','2008-11-16 08:02:28','2008-11-16 08:02:28',1),(151,'ApplyCustomerFeeTask','Finished Successfully','2008-11-16 08:02:28','2008-11-16 08:02:28',1),(152,'RegenerateScheduleTask','Finished Successfully','2008-11-16 08:02:28','2008-11-16 08:02:28',1),(153,'PortfolioAtRiskTask','Finished Successfully','2008-11-16 08:05:00','2008-11-16 08:05:00',1),(154,'BranchReportTask','Finished Successfully','2008-11-16 08:05:00','2008-11-16 08:05:00',1),(155,'PortfolioAtRiskTask','Finished Successfully','2008-11-16 08:05:00','2008-11-16 08:05:00',1),(156,'BranchReportTask','Finished Successfully','2008-11-16 08:05:01','2008-11-16 08:05:01',1),(157,'ProductStatus','Finished Successfully','2008-11-17 08:00:01','2008-11-17 08:00:01',1),(158,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-17 08:00:02','2008-11-17 08:00:02',1),(159,'SavingsIntCalcTask','Finished Successfully','2008-11-17 08:00:04','2008-11-17 08:00:04',1),(160,'SavingsIntPostingTask','Finished Successfully','2008-11-17 08:00:04','2008-11-17 08:00:04',1),(161,'LoanArrearsTask','Finished Successfully','2008-11-17 08:00:04','2008-11-17 08:00:04',1),(162,'ApplyCustomerFeeTask','Finished Successfully','2008-11-17 08:00:07','2008-11-17 08:00:07',1),(163,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-17 08:00:08','2008-11-17 08:00:08',1),(164,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-17 08:00:08','2008-11-17 08:00:08',1),(165,'LoanArrearsAgingTask','Finished Successfully','2008-11-17 08:00:08','2008-11-17 08:00:08',1),(166,'ApplyHolidayChangesTask','Finished Successfully','2008-11-17 08:00:08','2008-11-17 08:00:08',1),(167,'RegenerateScheduleTask','Finished Successfully','2008-11-17 08:00:08','2008-11-17 08:00:08',1),(168,'ProductStatus','Finished Successfully','2008-11-17 08:02:12','2008-11-17 08:02:12',1),(169,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-17 08:02:24','2008-11-17 08:02:24',1),(170,'SavingsIntCalcTask','Finished Successfully','2008-11-17 08:02:25','2008-11-17 08:02:25',1),(171,'LoanArrearsTask','Finished Successfully','2008-11-17 08:02:25','2008-11-17 08:02:25',1),(172,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-17 08:02:27','2008-11-17 08:02:27',1),(173,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-17 08:02:28','2008-11-17 08:02:28',1),(174,'SavingsIntPostingTask','Finished Successfully','2008-11-17 08:02:28','2008-11-17 08:02:28',1),(175,'LoanArrearsAgingTask','Finished Successfully','2008-11-17 08:02:28','2008-11-17 08:02:28',1),(176,'ApplyHolidayChangesTask','Finished Successfully','2008-11-17 08:02:28','2008-11-17 08:02:28',1),(177,'ApplyCustomerFeeTask','Finished Successfully','2008-11-17 08:02:28','2008-11-17 08:02:28',1),(178,'RegenerateScheduleTask','Finished Successfully','2008-11-17 08:02:28','2008-11-17 08:02:28',1),(179,'PortfolioAtRiskTask','Finished Successfully','2008-11-17 08:05:00','2008-11-17 08:05:00',1),(180,'BranchReportTask','Finished Successfully','2008-11-17 08:05:00','2008-11-17 08:05:01',1),(181,'PortfolioAtRiskTask','Finished Successfully','2008-11-17 08:05:00','2008-11-17 08:05:01',1),(182,'BranchReportTask','Finished Successfully','2008-11-17 08:05:01','2008-11-17 08:05:01',1),(183,'ProductStatus','Finished Successfully','2008-11-18 08:00:01','2008-11-18 08:00:01',1),(184,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-18 08:00:02','2008-11-18 08:00:02',1),(185,'SavingsIntCalcTask','Finished Successfully','2008-11-18 08:00:04','2008-11-18 08:00:04',1),(186,'SavingsIntPostingTask','Finished Successfully','2008-11-18 08:00:04','2008-11-18 08:00:04',1),(187,'LoanArrearsTask','Finished Successfully','2008-11-18 08:00:04','2008-11-18 08:00:04',1),(188,'ApplyCustomerFeeTask','Finished Successfully','2008-11-18 08:00:07','2008-11-18 08:00:08',1),(189,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-18 08:00:08','2008-11-18 08:00:08',1),(190,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-18 08:00:08','2008-11-18 08:00:08',1),(191,'LoanArrearsAgingTask','Finished Successfully','2008-11-18 08:00:08','2008-11-18 08:00:08',1),(192,'ApplyHolidayChangesTask','Finished Successfully','2008-11-18 08:00:08','2008-11-18 08:00:08',1),(193,'RegenerateScheduleTask','Finished Successfully','2008-11-18 08:00:08','2008-11-18 08:00:09',1),(194,'ProductStatus','Finished Successfully','2008-11-18 08:02:12','2008-11-18 08:02:12',1),(195,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-18 08:02:24','2008-11-18 08:02:24',1),(196,'SavingsIntCalcTask','Finished Successfully','2008-11-18 08:02:25','2008-11-18 08:02:25',1),(197,'LoanArrearsTask','Finished Successfully','2008-11-18 08:02:25','2008-11-18 08:02:25',1),(198,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-18 08:02:27','2008-11-18 08:02:27',1),(199,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-18 08:02:28','2008-11-18 08:02:28',1),(200,'SavingsIntPostingTask','Finished Successfully','2008-11-18 08:02:28','2008-11-18 08:02:28',1),(201,'LoanArrearsAgingTask','Finished Successfully','2008-11-18 08:02:28','2008-11-18 08:02:28',1),(202,'ApplyHolidayChangesTask','Finished Successfully','2008-11-18 08:02:28','2008-11-18 08:02:28',1),(203,'ApplyCustomerFeeTask','Finished Successfully','2008-11-18 08:02:28','2008-11-18 08:02:28',1),(204,'RegenerateScheduleTask','Finished Successfully','2008-11-18 08:02:28','2008-11-18 08:02:28',1),(205,'PortfolioAtRiskTask','Finished Successfully','2008-11-18 08:05:00','2008-11-18 08:05:00',1),(206,'BranchReportTask','Finished Successfully','2008-11-18 08:05:00','2008-11-18 08:05:00',1),(207,'PortfolioAtRiskTask','Finished Successfully','2008-11-18 08:05:00','2008-11-18 08:05:00',1),(208,'BranchReportTask','Finished Successfully','2008-11-18 08:05:01','2008-11-18 08:05:01',1),(209,'ProductStatus','Finished Successfully','2008-11-19 08:00:01','2008-11-19 08:00:01',1),(210,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-19 08:00:02','2008-11-19 08:00:02',1),(211,'SavingsIntCalcTask','Finished Successfully','2008-11-19 08:00:04','2008-11-19 08:00:04',1),(212,'SavingsIntPostingTask','Finished Successfully','2008-11-19 08:00:04','2008-11-19 08:00:04',1),(213,'LoanArrearsTask','Finished Successfully','2008-11-19 08:00:04','2008-11-19 08:00:04',1),(214,'ApplyCustomerFeeTask','Finished Successfully','2008-11-19 08:00:07','2008-11-19 08:00:07',1),(215,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-19 08:00:08','2008-11-19 08:00:08',1),(216,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-19 08:00:08','2008-11-19 08:00:08',1),(217,'LoanArrearsAgingTask','Finished Successfully','2008-11-19 08:00:08','2008-11-19 08:00:08',1),(218,'ApplyHolidayChangesTask','Finished Successfully','2008-11-19 08:00:08','2008-11-19 08:00:08',1),(219,'RegenerateScheduleTask','Finished Successfully','2008-11-19 08:00:08','2008-11-19 08:00:09',1),(220,'ProductStatus','Finished Successfully','2008-11-19 08:02:12','2008-11-19 08:02:12',1),(221,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-19 08:02:24','2008-11-19 08:02:24',1),(222,'SavingsIntCalcTask','Finished Successfully','2008-11-19 08:02:25','2008-11-19 08:02:25',1),(223,'LoanArrearsTask','Finished Successfully','2008-11-19 08:02:25','2008-11-19 08:02:25',1),(224,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-19 08:02:27','2008-11-19 08:02:27',1),(225,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-19 08:02:28','2008-11-19 08:02:28',1),(226,'SavingsIntPostingTask','Finished Successfully','2008-11-19 08:02:28','2008-11-19 08:02:28',1),(227,'LoanArrearsAgingTask','Finished Successfully','2008-11-19 08:02:28','2008-11-19 08:02:28',1),(228,'ApplyHolidayChangesTask','Finished Successfully','2008-11-19 08:02:28','2008-11-19 08:02:28',1),(229,'ApplyCustomerFeeTask','Finished Successfully','2008-11-19 08:02:28','2008-11-19 08:02:28',1),(230,'RegenerateScheduleTask','Finished Successfully','2008-11-19 08:02:28','2008-11-19 08:02:28',1),(231,'PortfolioAtRiskTask','Finished Successfully','2008-11-19 08:05:00','2008-11-19 08:05:00',1),(232,'BranchReportTask','Finished Successfully','2008-11-19 08:05:00','2008-11-19 08:05:01',1),(233,'PortfolioAtRiskTask','Finished Successfully','2008-11-19 08:05:00','2008-11-19 08:05:01',1),(234,'BranchReportTask','Finished Successfully','2008-11-19 08:05:01','2008-11-19 08:05:01',1),(235,'ProductStatus','Finished Successfully','2008-11-20 08:00:05','2008-11-20 08:00:10',1),(236,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-20 08:00:10','2008-11-20 08:00:12',1),(237,'SavingsIntCalcTask','Finished Successfully','2008-11-20 08:00:12','2008-11-20 08:00:12',1),(238,'SavingsIntPostingTask','Finished Successfully','2008-11-20 08:00:13','2008-11-20 08:00:13',1),(239,'LoanArrearsTask','Finished Successfully','2008-11-20 08:00:13','2008-11-20 08:00:15',1),(240,'ApplyCustomerFeeTask','Finished Successfully','2008-11-20 08:00:15','2008-11-20 08:00:16',1),(241,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-20 08:00:16','2008-11-20 08:00:16',1),(242,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-20 08:00:16','2008-11-20 08:00:17',1),(243,'LoanArrearsAgingTask','Finished Successfully','2008-11-20 08:00:17','2008-11-20 08:00:17',1),(244,'ApplyHolidayChangesTask','Finished Successfully','2008-11-20 08:00:17','2008-11-20 08:00:17',1),(245,'RegenerateScheduleTask','Finished Successfully','2008-11-20 08:00:18','2008-11-20 08:00:18',1),(246,'PortfolioAtRiskTask','Finished Successfully','2008-11-20 08:05:00','2008-11-20 08:05:01',1),(247,'BranchReportTask','Finished Successfully','2008-11-20 08:05:01','2008-11-20 08:05:02',1),(248,'ProductStatus','Finished Successfully','2008-11-21 08:04:15','2008-11-21 08:04:16',1),(249,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-21 08:04:16','2008-11-21 08:04:16',1),(250,'SavingsIntCalcTask','Finished Successfully','2008-11-21 08:04:16','2008-11-21 08:04:16',1),(251,'SavingsIntPostingTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(252,'LoanArrearsTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(253,'ApplyCustomerFeeTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(254,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(255,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(256,'LoanArrearsAgingTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(257,'ApplyHolidayChangesTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(258,'RegenerateScheduleTask','Finished Successfully','2008-11-21 08:04:17','2008-11-21 08:04:17',1),(259,'PortfolioAtRiskTask','Finished Successfully','2008-11-21 08:05:00','2008-11-21 08:05:00',1),(260,'BranchReportTask','Finished Successfully','2008-11-21 08:05:01','2008-11-21 08:05:01',1),(261,'ProductStatus','Finished Successfully','2008-11-22 08:04:16','2008-11-22 08:04:16',1),(262,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-22 08:04:16','2008-11-22 08:04:16',1),(263,'SavingsIntCalcTask','Finished Successfully','2008-11-22 08:04:16','2008-11-22 08:04:16',1),(264,'SavingsIntPostingTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(265,'LoanArrearsTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(266,'ApplyCustomerFeeTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(267,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(268,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(269,'LoanArrearsAgingTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(270,'ApplyHolidayChangesTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(271,'RegenerateScheduleTask','Finished Successfully','2008-11-22 08:04:17','2008-11-22 08:04:17',1),(272,'PortfolioAtRiskTask','Finished Successfully','2008-11-22 08:05:00','2008-11-22 08:05:00',1),(273,'BranchReportTask','Finished Successfully','2008-11-22 08:05:01','2008-11-22 08:05:01',1),(274,'ProductStatus','Finished Successfully','2008-11-23 08:04:15','2008-11-23 08:04:16',1),(275,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-23 08:04:16','2008-11-23 08:04:16',1),(276,'SavingsIntCalcTask','Finished Successfully','2008-11-23 08:04:16','2008-11-23 08:04:16',1),(277,'SavingsIntPostingTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(278,'LoanArrearsTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(279,'ApplyCustomerFeeTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(280,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(281,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(282,'LoanArrearsAgingTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(283,'ApplyHolidayChangesTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(284,'RegenerateScheduleTask','Finished Successfully','2008-11-23 08:04:17','2008-11-23 08:04:17',1),(285,'PortfolioAtRiskTask','Finished Successfully','2008-11-23 08:05:00','2008-11-23 08:05:01',1),(286,'BranchReportTask','Finished Successfully','2008-11-23 08:05:01','2008-11-23 08:05:01',1),(287,'ProductStatus','Finished Successfully','2008-11-24 08:04:15','2008-11-24 08:04:16',1),(288,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-24 08:04:16','2008-11-24 08:04:17',1),(289,'SavingsIntCalcTask','Finished Successfully','2008-11-24 08:04:17','2008-11-24 08:04:17',1),(290,'SavingsIntPostingTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(291,'LoanArrearsTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(292,'ApplyCustomerFeeTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(293,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(294,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(295,'LoanArrearsAgingTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(296,'ApplyHolidayChangesTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(297,'RegenerateScheduleTask','Finished Successfully','2008-11-24 08:04:18','2008-11-24 08:04:18',1),(298,'PortfolioAtRiskTask','Finished Successfully','2008-11-24 08:05:00','2008-11-24 08:05:01',1),(299,'BranchReportTask','Finished Successfully','2008-11-24 08:05:01','2008-11-24 08:05:01',1),(300,'ProductStatus','Finished Successfully','2008-11-25 08:04:15','2008-11-25 08:04:16',1),(301,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-25 08:04:16','2008-11-25 08:04:17',1),(302,'SavingsIntCalcTask','Finished Successfully','2008-11-25 08:04:17','2008-11-25 08:04:18',1),(303,'SavingsIntPostingTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:18',1),(304,'LoanArrearsTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:18',1),(305,'ApplyCustomerFeeTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:18',1),(306,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:18',1),(307,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:18',1),(308,'LoanArrearsAgingTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:18',1),(309,'ApplyHolidayChangesTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:18',1),(310,'RegenerateScheduleTask','Finished Successfully','2008-11-25 08:04:18','2008-11-25 08:04:19',1),(311,'PortfolioAtRiskTask','Finished Successfully','2008-11-25 08:05:00','2008-11-25 08:05:01',1),(312,'BranchReportTask','Finished Successfully','2008-11-25 08:05:01','2008-11-25 08:05:01',1),(313,'ProductStatus','Finished Successfully','2008-11-26 08:04:16','2008-11-26 08:04:17',1),(314,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-26 08:04:17','2008-11-26 08:04:17',1),(315,'SavingsIntCalcTask','Finished Successfully','2008-11-26 08:04:17','2008-11-26 08:04:18',1),(316,'SavingsIntPostingTask','Finished Successfully','2008-11-26 08:04:18','2008-11-26 08:04:18',1),(317,'LoanArrearsTask','Finished Successfully','2008-11-26 08:04:18','2008-11-26 08:04:18',1),(318,'ApplyCustomerFeeTask','Finished Successfully','2008-11-26 08:04:18','2008-11-26 08:04:18',1),(319,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-26 08:04:18','2008-11-26 08:04:19',1),(320,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-26 08:04:19','2008-11-26 08:04:19',1),(321,'LoanArrearsAgingTask','Finished Successfully','2008-11-26 08:04:19','2008-11-26 08:04:19',1),(322,'ApplyHolidayChangesTask','Finished Successfully','2008-11-26 08:04:19','2008-11-26 08:04:19',1),(323,'RegenerateScheduleTask','Finished Successfully','2008-11-26 08:04:19','2008-11-26 08:04:19',1),(324,'PortfolioAtRiskTask','Finished Successfully','2008-11-26 08:05:00','2008-11-26 08:05:01',1),(325,'BranchReportTask','Finished Successfully','2008-11-26 08:05:01','2008-11-26 08:05:01',1),(326,'ProductStatus','Finished Successfully','2008-11-27 08:04:15','2008-11-27 08:04:16',1),(327,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-27 08:04:17','2008-11-27 08:04:17',1),(328,'SavingsIntCalcTask','Finished Successfully','2008-11-27 08:04:17','2008-11-27 08:04:17',1),(329,'SavingsIntPostingTask','Finished Successfully','2008-11-27 08:04:18','2008-11-27 08:04:18',1),(330,'LoanArrearsTask','Finished Successfully','2008-11-27 08:04:18','2008-11-27 08:04:18',1),(331,'ApplyCustomerFeeTask','Finished Successfully','2008-11-27 08:04:18','2008-11-27 08:04:18',1),(332,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-27 08:04:18','2008-11-27 08:04:19',1),(333,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-27 08:04:19','2008-11-27 08:04:19',1),(334,'LoanArrearsAgingTask','Finished Successfully','2008-11-27 08:04:19','2008-11-27 08:04:19',1),(335,'ApplyHolidayChangesTask','Finished Successfully','2008-11-27 08:04:19','2008-11-27 08:04:20',1),(336,'RegenerateScheduleTask','Finished Successfully','2008-11-27 08:04:20','2008-11-27 08:04:20',1),(337,'PortfolioAtRiskTask','Finished Successfully','2008-11-27 08:05:00','2008-11-27 08:05:01',1),(338,'BranchReportTask','Finished Successfully','2008-11-27 08:05:01','2008-11-27 08:05:01',1),(339,'ProductStatus','Finished Successfully','2008-11-28 08:04:15','2008-11-28 08:04:16',1),(340,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-28 08:04:17','2008-11-28 08:04:17',1),(341,'SavingsIntCalcTask','Finished Successfully','2008-11-28 08:04:17','2008-11-28 08:04:17',1),(342,'SavingsIntPostingTask','Finished Successfully','2008-11-28 08:04:18','2008-11-28 08:04:18',1),(343,'LoanArrearsTask','Finished Successfully','2008-11-28 08:04:18','2008-11-28 08:04:18',1),(344,'ApplyCustomerFeeTask','Finished Successfully','2008-11-28 08:04:18','2008-11-28 08:04:18',1),(345,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-28 08:04:19','2008-11-28 08:04:19',1),(346,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-28 08:04:19','2008-11-28 08:04:19',1),(347,'LoanArrearsAgingTask','Finished Successfully','2008-11-28 08:04:19','2008-11-28 08:04:19',1),(348,'ApplyHolidayChangesTask','Finished Successfully','2008-11-28 08:04:19','2008-11-28 08:04:19',1),(349,'RegenerateScheduleTask','Finished Successfully','2008-11-28 08:04:20','2008-11-28 08:04:20',1),(350,'PortfolioAtRiskTask','Finished Successfully','2008-11-28 08:05:01','2008-11-28 08:05:01',1),(351,'BranchReportTask','Finished Successfully','2008-11-28 08:05:01','2008-11-28 08:05:01',1),(352,'ProductStatus','Finished Successfully','2008-11-29 08:04:15','2008-11-29 08:04:16',1),(353,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-29 08:04:17','2008-11-29 08:04:17',1),(354,'SavingsIntCalcTask','Finished Successfully','2008-11-29 08:04:17','2008-11-29 08:04:18',1),(355,'SavingsIntPostingTask','Finished Successfully','2008-11-29 08:04:18','2008-11-29 08:04:18',1),(356,'LoanArrearsTask','Finished Successfully','2008-11-29 08:04:18','2008-11-29 08:04:18',1),(357,'ApplyCustomerFeeTask','Finished Successfully','2008-11-29 08:04:18','2008-11-29 08:04:18',1),(358,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-29 08:04:19','2008-11-29 08:04:19',1),(359,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-29 08:04:19','2008-11-29 08:04:19',1),(360,'LoanArrearsAgingTask','Finished Successfully','2008-11-29 08:04:19','2008-11-29 08:04:19',1),(361,'ApplyHolidayChangesTask','Finished Successfully','2008-11-29 08:04:19','2008-11-29 08:04:19',1),(362,'RegenerateScheduleTask','Finished Successfully','2008-11-29 08:04:20','2008-11-29 08:04:20',1),(363,'PortfolioAtRiskTask','Finished Successfully','2008-11-29 08:05:01','2008-11-29 08:05:01',1),(364,'BranchReportTask','Finished Successfully','2008-11-29 08:05:01','2008-11-29 08:05:01',1),(365,'ProductStatus','Finished Successfully','2008-11-30 08:04:15','2008-11-30 08:04:16',1),(366,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-11-30 08:04:17','2008-11-30 08:04:17',1),(367,'SavingsIntCalcTask','Finished Successfully','2008-11-30 08:04:17','2008-11-30 08:04:18',1),(368,'SavingsIntPostingTask','Finished Successfully','2008-11-30 08:04:18','2008-11-30 08:04:18',1),(369,'LoanArrearsTask','Finished Successfully','2008-11-30 08:04:18','2008-11-30 08:04:18',1),(370,'ApplyCustomerFeeTask','Finished Successfully','2008-11-30 08:04:18','2008-11-30 08:04:18',1),(371,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-11-30 08:04:19','2008-11-30 08:04:19',1),(372,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-11-30 08:04:19','2008-11-30 08:04:19',1),(373,'LoanArrearsAgingTask','Finished Successfully','2008-11-30 08:04:19','2008-11-30 08:04:19',1),(374,'ApplyHolidayChangesTask','Finished Successfully','2008-11-30 08:04:19','2008-11-30 08:04:19',1),(375,'RegenerateScheduleTask','Finished Successfully','2008-11-30 08:04:20','2008-11-30 08:04:20',1),(376,'PortfolioAtRiskTask','Finished Successfully','2008-11-30 08:05:01','2008-11-30 08:05:01',1),(377,'BranchReportTask','Finished Successfully','2008-11-30 08:05:01','2008-11-30 08:05:01',1),(378,'ProductStatus','Finished Successfully','2008-12-01 08:04:16','2008-12-01 08:04:16',1),(379,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-12-01 08:04:17','2008-12-01 08:04:17',1),(380,'SavingsIntCalcTask','Finished Successfully','2008-12-01 08:04:17','2008-12-01 08:04:18',1),(381,'SavingsIntPostingTask','Finished Successfully','2008-12-01 08:04:18','2008-12-01 08:04:18',1),(382,'LoanArrearsTask','Finished Successfully','2008-12-01 08:04:18','2008-12-01 08:04:18',1),(383,'ApplyCustomerFeeTask','Finished Successfully','2008-12-01 08:04:18','2008-12-01 08:04:18',1),(384,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-12-01 08:04:19','2008-12-01 08:04:19',1),(385,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-12-01 08:04:19','2008-12-01 08:04:19',1),(386,'LoanArrearsAgingTask','Finished Successfully','2008-12-01 08:04:19','2008-12-01 08:04:19',1),(387,'ApplyHolidayChangesTask','Finished Successfully','2008-12-01 08:04:19','2008-12-01 08:04:19',1),(388,'RegenerateScheduleTask','Finished Successfully','2008-12-01 08:04:20','2008-12-01 08:04:20',1),(389,'PortfolioAtRiskTask','Finished Successfully','2008-12-01 08:05:01','2008-12-01 08:05:01',1),(390,'BranchReportTask','Finished Successfully','2008-12-01 08:05:01','2008-12-01 08:05:01',1),(391,'ProductStatus','Finished Successfully','2008-12-02 08:04:16','2008-12-02 08:04:16',1),(392,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-12-02 08:04:17','2008-12-02 08:04:17',1),(393,'SavingsIntCalcTask','Finished Successfully','2008-12-02 08:04:17','2008-12-02 08:04:17',1),(394,'SavingsIntPostingTask','Finished Successfully','2008-12-02 08:04:18','2008-12-02 08:04:18',1),(395,'LoanArrearsTask','Finished Successfully','2008-12-02 08:04:18','2008-12-02 08:04:18',1),(396,'ApplyCustomerFeeTask','Finished Successfully','2008-12-02 08:04:18','2008-12-02 08:04:18',1),(397,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-12-02 08:04:19','2008-12-02 08:04:19',1),(398,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-12-02 08:04:19','2008-12-02 08:04:19',1),(399,'LoanArrearsAgingTask','Finished Successfully','2008-12-02 08:04:19','2008-12-02 08:04:19',1),(400,'ApplyHolidayChangesTask','Finished Successfully','2008-12-02 08:04:19','2008-12-02 08:04:19',1),(401,'RegenerateScheduleTask','Finished Successfully','2008-12-02 08:04:20','2008-12-02 08:04:20',1),(402,'PortfolioAtRiskTask','Finished Successfully','2008-12-02 08:05:01','2008-12-02 08:05:01',1),(403,'BranchReportTask','Finished Successfully','2008-12-02 08:05:01','2008-12-02 08:05:01',1),(404,'ProductStatus','Finished Successfully','2008-12-03 08:04:16','2008-12-03 08:04:16',1),(405,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-12-03 08:04:17','2008-12-03 08:04:17',1),(406,'SavingsIntCalcTask','Finished Successfully','2008-12-03 08:04:17','2008-12-03 08:04:17',1),(407,'SavingsIntPostingTask','Finished Successfully','2008-12-03 08:04:18','2008-12-03 08:04:18',1),(408,'LoanArrearsTask','Finished Successfully','2008-12-03 08:04:18','2008-12-03 08:04:18',1),(409,'ApplyCustomerFeeTask','Finished Successfully','2008-12-03 08:04:18','2008-12-03 08:04:18',1),(410,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-12-03 08:04:19','2008-12-03 08:04:19',1),(411,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-12-03 08:04:19','2008-12-03 08:04:19',1),(412,'LoanArrearsAgingTask','Finished Successfully','2008-12-03 08:04:19','2008-12-03 08:04:19',1),(413,'ApplyHolidayChangesTask','Finished Successfully','2008-12-03 08:04:19','2008-12-03 08:04:19',1),(414,'RegenerateScheduleTask','Finished Successfully','2008-12-03 08:04:20','2008-12-03 08:04:20',1),(415,'PortfolioAtRiskTask','Finished Successfully','2008-12-03 08:05:01','2008-12-03 08:05:01',1),(416,'BranchReportTask','Finished Successfully','2008-12-03 08:05:01','2008-12-03 08:05:01',1),(417,'ProductStatus','Finished Successfully','2008-12-04 08:04:16','2008-12-04 08:04:17',1),(418,'CollectionSheetReportParameterCachingTask','Finished Successfully','2008-12-04 08:04:17','2008-12-04 08:04:17',1),(419,'SavingsIntCalcTask','Finished Successfully','2008-12-04 08:04:17','2008-12-04 08:04:18',1),(420,'SavingsIntPostingTask','Finished Successfully','2008-12-04 08:04:18','2008-12-04 08:04:18',1),(421,'LoanArrearsTask','Finished Successfully','2008-12-04 08:04:18','2008-12-04 08:04:18',1),(422,'ApplyCustomerFeeTask','Finished Successfully','2008-12-04 08:04:18','2008-12-04 08:04:18',1),(423,'ApplyCustomerFeeChangesTask','Finished Successfully','2008-12-04 08:04:19','2008-12-04 08:04:19',1),(424,'GenerateMeetingsForCustomerAndSavingsTask','Finished Successfully','2008-12-04 08:04:19','2008-12-04 08:04:19',1),(425,'LoanArrearsAgingTask','Finished Successfully','2008-12-04 08:04:19','2008-12-04 08:04:19',1),(426,'ApplyHolidayChangesTask','Finished Successfully','2008-12-04 08:04:19','2008-12-04 08:04:19',1),(427,'RegenerateScheduleTask','Finished Successfully','2008-12-04 08:04:20','2008-12-04 08:04:20',1),(428,'PortfolioAtRiskTask','Finished Successfully','2008-12-04 08:05:01','2008-12-04 08:05:01',1),(429,'BranchReportTask','Finished Successfully','2008-12-04 08:05:01','2008-12-04 08:05:01',1);
/*!40000 ALTER TABLE `scheduled_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spouse_father_lookup`
--

DROP TABLE IF EXISTS `spouse_father_lookup`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `spouse_father_lookup` (
  `SPOUSE_FATHER_ID` int(11) NOT NULL,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`SPOUSE_FATHER_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `spouse_father_lookup_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `spouse_father_lookup`
--

LOCK TABLES `spouse_father_lookup` WRITE;
/*!40000 ALTER TABLE `spouse_father_lookup` DISABLE KEYS */;
INSERT INTO `spouse_father_lookup` VALUES (1,128),(2,129);
/*!40000 ALTER TABLE `spouse_father_lookup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supported_locale`
--

DROP TABLE IF EXISTS `supported_locale`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `supported_locale` (
  `LOCALE_ID` smallint(6) NOT NULL,
  `COUNTRY_ID` smallint(6) default NULL,
  `LANG_ID` smallint(6) default NULL,
  `LOCALE_NAME` varchar(50) default NULL,
  `DEFAULT_LOCALE` smallint(6) default NULL,
  PRIMARY KEY  (`LOCALE_ID`),
  KEY `COUNTRY_ID` (`COUNTRY_ID`),
  KEY `LANG_ID` (`LANG_ID`),
  CONSTRAINT `supported_locale_ibfk_1` FOREIGN KEY (`COUNTRY_ID`) REFERENCES `country` (`COUNTRY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `supported_locale_ibfk_2` FOREIGN KEY (`LANG_ID`) REFERENCES `language` (`LANG_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `supported_locale`
--

LOCK TABLES `supported_locale` WRITE;
/*!40000 ALTER TABLE `supported_locale` DISABLE KEYS */;
INSERT INTO `supported_locale` VALUES (1,6,1,'EN',1),(2,7,2,'Icelandic',0),(3,8,3,'Spanish',0),(4,9,4,'French',0);
/*!40000 ALTER TABLE `supported_locale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey`
--

DROP TABLE IF EXISTS `survey`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey` (
  `SURVEY_ID` int(11) NOT NULL auto_increment,
  `SURVEY_NAME` varchar(200) NOT NULL,
  `SURVEY_APPLIES_TO` varchar(200) NOT NULL,
  `DATE_OF_CREATION` date NOT NULL,
  `STATE` int(11) NOT NULL,
  PRIMARY KEY  (`SURVEY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `survey`
--

LOCK TABLES `survey` WRITE;
/*!40000 ALTER TABLE `survey` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_instance`
--

DROP TABLE IF EXISTS `survey_instance`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey_instance` (
  `INSTANCE_ID` int(11) NOT NULL auto_increment,
  `SURVEY_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) default NULL,
  `OFFICER_ID` smallint(6) default NULL,
  `DATE_CONDUCTED` date NOT NULL,
  `COMPLETED_STATUS` int(11) NOT NULL,
  `ACCOUNT_ID` int(11) default NULL,
  `CREATING_OFFICER_ID` smallint(6) NOT NULL,
  PRIMARY KEY  (`INSTANCE_ID`),
  KEY `SURVEY_ID` (`SURVEY_ID`),
  KEY `CUSTOMER_ID` (`CUSTOMER_ID`),
  KEY `OFFICER_ID` (`OFFICER_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  KEY `CREATING_OFFICER_ID` (`CREATING_OFFICER_ID`),
  CONSTRAINT `survey_instance_ibfk_1` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `survey_instance_ibfk_2` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customer` (`CUSTOMER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `survey_instance_ibfk_3` FOREIGN KEY (`OFFICER_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `survey_instance_ibfk_4` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `survey_instance_ibfk_5` FOREIGN KEY (`CREATING_OFFICER_ID`) REFERENCES `personnel` (`PERSONNEL_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `survey_instance`
--

LOCK TABLES `survey_instance` WRITE;
/*!40000 ALTER TABLE `survey_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_questions`
--

DROP TABLE IF EXISTS `survey_questions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey_questions` (
  `SURVEYQUESTION_ID` int(11) NOT NULL auto_increment,
  `SURVEY_ID` int(11) NOT NULL,
  `QUESTION_ID` int(11) NOT NULL,
  `QUESTION_ORDER` int(11) NOT NULL,
  `MANDATORY` smallint(6) NOT NULL default '1',
  PRIMARY KEY  (`SURVEYQUESTION_ID`),
  KEY `QUESTION_ID` (`QUESTION_ID`),
  KEY `SURVEY_ID` (`SURVEY_ID`),
  CONSTRAINT `survey_questions_ibfk_1` FOREIGN KEY (`QUESTION_ID`) REFERENCES `questions` (`QUESTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `survey_questions_ibfk_2` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `survey_questions`
--

LOCK TABLES `survey_questions` WRITE;
/*!40000 ALTER TABLE `survey_questions` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_response`
--

DROP TABLE IF EXISTS `survey_response`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `survey_response` (
  `RESPONSE_ID` int(11) NOT NULL auto_increment,
  `INSTANCE_ID` int(11) NOT NULL,
  `SURVEY_QUESTION_ID` int(11) NOT NULL,
  `FREETEXT_VALUE` text,
  `CHOICE_VALUE` int(11) default NULL,
  `DATE_VALUE` date default NULL,
  `NUMBER_VALUE` decimal(16,5) default NULL,
  `MULTI_SELECT_VALUE` text,
  PRIMARY KEY  (`RESPONSE_ID`),
  UNIQUE KEY `INSTANCE_ID` (`INSTANCE_ID`,`SURVEY_QUESTION_ID`),
  KEY `SURVEY_QUESTION_ID` (`SURVEY_QUESTION_ID`),
  KEY `CHOICE_VALUE` (`CHOICE_VALUE`),
  CONSTRAINT `survey_response_ibfk_1` FOREIGN KEY (`SURVEY_QUESTION_ID`) REFERENCES `survey_questions` (`SURVEYQUESTION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `survey_response_ibfk_2` FOREIGN KEY (`INSTANCE_ID`) REFERENCES `survey_instance` (`INSTANCE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `survey_response_ibfk_3` FOREIGN KEY (`CHOICE_VALUE`) REFERENCES `question_choices` (`CHOICE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `survey_response`
--

LOCK TABLES `survey_response` WRITE;
/*!40000 ALTER TABLE `survey_response` DISABLE KEYS */;
/*!40000 ALTER TABLE `survey_response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `temp_id`
--

DROP TABLE IF EXISTS `temp_id`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `temp_id` (
  `ID` smallint(6) NOT NULL auto_increment,
  `TEMPID` smallint(6) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `temp_id`
--

LOCK TABLES `temp_id` WRITE;
/*!40000 ALTER TABLE `temp_id` DISABLE KEYS */;
/*!40000 ALTER TABLE `temp_id` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_type`
--

DROP TABLE IF EXISTS `transaction_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `transaction_type` (
  `TRANSACTION_ID` smallint(6) NOT NULL,
  `TRANSACTION_NAME` varchar(100) NOT NULL,
  PRIMARY KEY  (`TRANSACTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `transaction_type`
--

LOCK TABLES `transaction_type` WRITE;
/*!40000 ALTER TABLE `transaction_type` DISABLE KEYS */;
INSERT INTO `transaction_type` VALUES (1,'Loan Disbursement'),(2,'Loan Repayment'),(3,'Savings Deposit'),(4,'Savings Withdrawals'),(5,'Client Fees/penalty payments');
/*!40000 ALTER TABLE `transaction_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `waive_off_history`
--

DROP TABLE IF EXISTS `waive_off_history`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `waive_off_history` (
  `WAIVE_OFF_ID` int(11) NOT NULL auto_increment,
  `ACCOUNT_ID` int(11) NOT NULL,
  `WAIVE_OFF_DATE` date NOT NULL,
  `WAIVE_OFF_TYPE` varchar(20) NOT NULL,
  PRIMARY KEY  (`WAIVE_OFF_ID`),
  KEY `ACCOUNT_ID` (`ACCOUNT_ID`),
  CONSTRAINT `waive_off_history_ibfk_1` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `loan_account` (`ACCOUNT_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `waive_off_history`
--

LOCK TABLES `waive_off_history` WRITE;
/*!40000 ALTER TABLE `waive_off_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `waive_off_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `week_days_master`
--

DROP TABLE IF EXISTS `week_days_master`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `week_days_master` (
  `WEEK_DAYS_MASTER_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  `WORKING_DAY` smallint(6) NOT NULL,
  `START_OF_FISCAL_WEEK` smallint(6) NOT NULL,
  PRIMARY KEY  (`WEEK_DAYS_MASTER_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `week_days_master_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `week_days_master`
--

LOCK TABLES `week_days_master` WRITE;
/*!40000 ALTER TABLE `week_days_master` DISABLE KEYS */;
INSERT INTO `week_days_master` VALUES (1,72,0,0),(2,73,1,1),(3,74,1,0),(4,75,1,0),(5,76,1,0),(6,77,1,0),(7,78,1,0);
/*!40000 ALTER TABLE `week_days_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yes_no_master`
--

DROP TABLE IF EXISTS `yes_no_master`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `yes_no_master` (
  `YES_NO_MASTER_ID` smallint(6) NOT NULL auto_increment,
  `LOOKUP_ID` int(11) NOT NULL,
  PRIMARY KEY  (`YES_NO_MASTER_ID`),
  KEY `LOOKUP_ID` (`LOOKUP_ID`),
  CONSTRAINT `yes_no_master_ibfk_1` FOREIGN KEY (`LOOKUP_ID`) REFERENCES `lookup_value` (`LOOKUP_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `yes_no_master`
--

LOCK TABLES `yes_no_master` WRITE;
/*!40000 ALTER TABLE `yes_no_master` DISABLE KEYS */;
INSERT INTO `yes_no_master` VALUES (1,124),(2,125);
/*!40000 ALTER TABLE `yes_no_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2008-12-18 22:42:26
