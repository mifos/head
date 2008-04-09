CREATE TABLE  `client_loan_account_dim` (
  `client_loan_account_id` int(15) NOT NULL auto_increment,
  `global_account_num` varchar(100) NOT NULL,
  `created_by` smallint(5) default NULL,
  `created_date` date NOT NULL,
  `updated_by` smallint(5) default NULL,
  `updated_date` date default NULL,
  `closed_date` date default NULL,
  `NO_OF_INSTALLMENTS` smallint(5) NOT NULL,
  `DISBURSEMENT_DATE` date default NULL,
  `GRACE_PERIOD_DURATION` smallint(5) default NULL,
  `INTEREST_AT_DISB` smallint(5) default NULL,
  `client_id` int(10) NOT NULL,
  PRIMARY KEY  (`client_loan_account_id`),
  UNIQUE KEY `global_account_num` (`global_account_num`,`client_id`,`client_loan_account_id`),
  KEY `client_loan_account_ibfk_1` (`client_id`),
  CONSTRAINT `client_loan_account_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `client_dim` (`client_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;