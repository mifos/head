CREATE TABLE  `client_loan_fact` (
  `overdue_balance` decimal(10,3) default NULL,
  `days_in_arrears` smallint(6) default NULL,
  `loan_balance` decimal(10,3) default NULL,
  `client_loan_account_id` int(10) NOT NULL,
  `time_id` smallint(5) NOT NULL,
  PRIMARY KEY  (`client_loan_account_id`,`time_id`),
  constraint client_loan_loan_ibfk_1 foreign key (client_loan_account_id) references client_loan_account_dim (client_loan_account_id) on delete no action on update no action,
  constraint client_loan_loan_ibfk_2 foreign key (time_id) references time_dim (time_id) on delete no action on update no action
) ENGINE=InnoDB DEFAULT CHARSET=utf8;