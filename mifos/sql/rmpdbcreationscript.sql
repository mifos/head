-- These are tables for the Reports Mini Portal

CREATE TABLE mis_bank (
  bankid int default '0' NOT NULL,
  bankname varchar(50) default '' NOT NULL,
  PRIMARY KEY  (bankid)
) ENGINE=MyISAM CHARACTER SET latin1;

CREATE TABLE mis_bankbranch (
  bankbranchid int default '0' NOT NULL,
  bankid int default '0' NOT NULL,
  branchname varchar(50) default '' NOT NULL,
  areaid int default '0' NOT NULL,
  PRIMARY KEY  (bankbranchid),
  INDEX FK_mis_bankbranch_mis_bank (bankid)
) ENGINE=MyISAM CHARACTER SET latin1;

CREATE TABLE mis_geographicalarea (
  areaid int default '0' NOT NULL,
  areaname varchar(50) default '' NOT NULL,
  areatypeid int default '0' NOT NULL,
  parentareaid int default NULL,
  PRIMARY KEY  (areaid)
) ENGINE=MyISAM CHARACTER SET latin1;

CREATE TABLE mis_geographicalareatype (
  areatypeid int default '0' NOT NULL,
  areatypename varchar(20) default '' NOT NULL,
  PRIMARY KEY  (areatypeid)
) ENGINE=MyISAM CHARACTER SET latin1;

CREATE TABLE mis_shgmemberprofile (
  groupid int default '0' NOT NULL,
  memberid int default '0' NOT NULL,
  membername varchar(100) default '' NOT NULL,
  attendence varchar(100) default '0' NOT NULL,
  savings decimal(10,0) default '0' NOT NULL,
  mstatus varchar(100) default '0' NOT NULL,
  PRIMARY KEY  (memberid,groupid)
) ENGINE=MyISAM CHARACTER SET latin1;

CREATE TABLE mis_shgprofile (
  groupid int default '0' NOT NULL,
  groupname varchar(50) default '' NOT NULL,
  nummembers int default '0' NOT NULL,
  areaid int default '0' NOT NULL,
  formationdate timestamp,
  groupleader1 varchar(50) default NULL,
  groupleader2 varchar(50) default NULL,
  bankbranchid int default NULL,
  PRIMARY KEY  (groupid)
) ENGINE=MyISAM CHARACTER SET latin1;

CREATE TABLE report_datasource (
  DATASOURCE_ID int auto_increment NOT NULL,
  NAME varchar(255) default '' NOT NULL,
  DRIVER varchar(255) default NULL,
  URL varchar(255) default '' NOT NULL,
  USERNAME varchar(255) default NULL,
  PASSWORD varchar(255) default NULL,
  MAX_IDLE int default NULL,
  MAX_ACTIVE int default NULL,
  MAX_WAIT bigint default NULL,
  VALIDATION_QUERY varchar(255) default NULL,
  JNDI tinyint default NULL,
  PRIMARY KEY  (DATASOURCE_ID),
  UNIQUE (NAME),
  UNIQUE (NAME)
) ENGINE=InnoDB CHARACTER SET latin1; 

CREATE TABLE report_jasper_map (
  REPORT_ID smallint auto_increment NOT NULL,
  REPORT_CATEGORY_ID smallint default NULL,
  REPORT_NAME varchar(100) default NULL,
  REPORT_IDENTIFIER varchar(100) default NULL,
  REPORT_JASPER varchar(100) default NULL,
  PRIMARY KEY  (REPORT_ID),
  INDEX REPORT_CATEGORY_ID (REPORT_CATEGORY_ID),
  CONSTRAINT report_jasper_map_ibfk_1 FOREIGN KEY (REPORT_CATEGORY_ID) REFERENCES report_category (REPORT_CATEGORY_ID) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB CHARACTER SET utf8; 

CREATE TABLE report_parameter (
  PARAMETER_ID int auto_increment NOT NULL,
  NAME varchar(255) default '' NOT NULL,
  TYPE varchar(255) default '' NOT NULL,
  CLASSNAME varchar(255) default '' NOT NULL,
  DATA text,
  DATASOURCE_ID int default NULL,
  DESCRIPTION varchar(255) default NULL,
  PRIMARY KEY  (PARAMETER_ID),
  UNIQUE (NAME),
  UNIQUE (NAME),
  INDEX DATASOURCE_ID (DATASOURCE_ID)
) ENGINE=MyISAM CHARACTER SET latin1; 

CREATE TABLE report_parameter_map (
  REPORT_ID int default '0' NOT NULL,
  PARAMETER_ID int default NULL,
  REQUIRED tinyint default NULL,
  SORT_ORDER int default NULL,
  STEP int default NULL,
  MAP_ID int auto_increment NOT NULL,
  PRIMARY KEY  (MAP_ID),
  INDEX REPORT_ID (REPORT_ID),
  INDEX PARAMETER_ID (PARAMETER_ID)
) ENGINE=MyISAM CHARACTER SET latin1; 
