create table `center_dim` (
  `center_id` smallint(5) not null auto_increment,
  `global_cust_num` varchar(100) not null,
  `display_name` varchar(200) default null,
  `loan_officer_id` smallint(6) default null,
  `external_id` varchar(50) default null,
  `created_date` date default null,
  `updated_date` date default null,
  `mfi_joining_date` date default null,
  `customer_activation_date` date default null,
  `created_by` smallint(5) default null,
  `updated_by` smallint(5) default null,
  unique key `global_cust_num` (`global_cust_num`),
  constraint `loan_officer_id_ibfk` foreign key (`loan_officer_id`) references `loan_officer_dim` (`loan_officer_id`) on delete no action on update no action,
  primary key  (`center_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;