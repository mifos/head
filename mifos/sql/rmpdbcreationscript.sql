-- MySQL dump 10.8
--
-- Host: 192.168.1.32    Database: jasper_training
-- ------------------------------------------------------
-- Server version	4.1.7-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE="NO_AUTO_VALUE_ON_ZERO" */;

--
-- Table structure for table `mis_bank`
--


CREATE TABLE `mis_bank` (
  `bankid` int(11) NOT NULL default '0',
  `bankname` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`bankid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



--
-- Table structure for table `mis_bankbranch`
--


CREATE TABLE `mis_bankbranch` (
  `bankbranchid` int(11) NOT NULL default '0',
  `bankid` int(11) NOT NULL default '0',
  `branchname` varchar(50) NOT NULL default '',
  `areaid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`bankbranchid`),
  KEY `FK_mis_bankbranch_mis_bank` (`bankid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mis_bankbranch`
--
--
-- Table structure for table `mis_geographicalarea`
--

CREATE TABLE `mis_geographicalarea` (
  `areaid` int(11) NOT NULL default '0',
  `areaname` varchar(50) NOT NULL default '',
  `areatypeid` int(11) NOT NULL default '0',
  `parentareaid` int(11) default NULL,
  PRIMARY KEY  (`areaid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mis_geographicalarea`
--


--
-- Table structure for table `mis_geographicalareatype`
--


CREATE TABLE `mis_geographicalareatype` (
  `areatypeid` int(11) NOT NULL default '0',
  `areatypename` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`areatypeid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mis_geographicalareatype`
--


--
-- Table structure for table `mis_shgmemberprofile`
--


CREATE TABLE `mis_shgmemberprofile` (
  `groupid` int(11) NOT NULL default '0',
  `memberid` int(11) NOT NULL default '0',
  `membername` varchar(100) NOT NULL default '',
  `attendence` varchar(100) NOT NULL default '0',
  `savings` decimal(10,0) NOT NULL default '0',
  `mstatus` varchar(100) NOT NULL default '0',
  PRIMARY KEY  (`memberid`,`groupid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mis_shgmemberprofile`
--


--
-- Table structure for table `mis_shgprofile`
--


CREATE TABLE `mis_shgprofile` (
  `groupid` int(11) NOT NULL default '0',
  `groupname` varchar(50) NOT NULL default '',
  `nummembers` int(11) NOT NULL default '0',
  `areaid` int(11) NOT NULL default '0',
  `formationdate` datetime NOT NULL default '0000-00-00 00:00:00',
  `groupleader1` varchar(50) default NULL,
  `groupleader2` varchar(50) default NULL,
  `bankbranchid` int(11) default NULL,
  PRIMARY KEY  (`groupid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `report_datasource`
--


--
-- Table structure for table `report_datasource`
--


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
  `JNDI` tinyint(1) default NULL,
  PRIMARY KEY  (`DATASOURCE_ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_2` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1; 

--
-- Dumping data for table `report_jasper_map`
--


--
-- Table structure for table `report_jasper_map`
--


CREATE TABLE `report_jasper_map` (
  `REPORT_ID` smallint(6) NOT NULL auto_increment,
  `REPORT_CATEGORY_ID` smallint(6) default NULL,
  `REPORT_NAME` varchar(100) default NULL,
  `REPORT_IDENTIFIER` varchar(100) default NULL,
  `REPORT_JASPER` varchar(100) default NULL,
  PRIMARY KEY  (`REPORT_ID`),
  KEY `REPORT_CATEGORY_ID` (`REPORT_CATEGORY_ID`),
  CONSTRAINT `report_jasper_map_ibfk_1` FOREIGN KEY (`REPORT_CATEGORY_ID`) REFERENCES `report_category` (`REPORT_CATEGORY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

--
-- Dumping data for table `report_parameter`
--


--
-- Table structure for table `report_parameter`
--


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

--
-- Dumping data for table `report_datasource`
--


--
-- Table structure for table `report_parameter_map`
--


CREATE TABLE `report_parameter_map` (
  `REPORT_ID` int(11) NOT NULL default '0',
  `PARAMETER_ID` int(11) default NULL,
  `REQUIRED` tinyint(1) default NULL,
  `SORT_ORDER` int(11) default NULL,
  `STEP` int(11) default NULL,
  `MAP_ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`MAP_ID`),
  KEY `REPORT_ID` (`REPORT_ID`),
  KEY `PARAMETER_ID` (`PARAMETER_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1; 




