create table `client_loan_account_dim` (
  `client_loan_account_id` int(15) not null auto_increment,
  `global_account_num` varchar(100) not null,
  `created_by` smallint(5) default null,
  `created_date` date not null,
  `updated_by` smallint(5) default null,
  `updated_date` date default null,
  `closed_date` date default null,
  `no_of_installments` smallint(5) not null,
  `disbursement_date` date default null,
  `grace_period_duration` smallint(5) default null,
  `interest_at_disb` smallint(5) default null,
  `client_id` int(15) not null,
  primary key  (`client_loan_account_id`),
  unique key `global_account_num` (`global_account_num`,`client_id`,`client_loan_account_id`),
  constraint `client_loan_account_ibfk1` foreign key (`client_id`) references `client_dim` (`client_id`) on delete no action on update no action
) engine=innodb default charset=utf8;
