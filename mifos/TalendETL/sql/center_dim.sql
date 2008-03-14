drop table if exists  `center_dim`;
create table `center_dim` (
  `center_id` smallint(5) not null auto_increment,
  `customer_level_id` smallint(5) not null,
  `global_cust_num` varchar(100) not null default '',
  `display_name` varchar(200) default null,
  `branch_id` smallint(4) default null,
  `loan_officer_id` smallint(5) default null,
  `status_id` smallint(5) default null,
  `external_id` varchar(50) default null,
  `created_date` date default null,
  `updated_date` date default null,
  `mfi_joining_date` date default null,
  `customer_activation_date` date default null,
  `created_by` smallint(5) default null,
  `updated_by` smallint(5) default null,
  `version_no` int(10) not null,
  primary key  (`center_id`)
) engine=innodb default charset=utf8;