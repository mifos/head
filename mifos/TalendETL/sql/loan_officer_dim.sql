CREATE TABLE  `loan_officer_dim` (
  `loan_officer_id` smallint(6) auto_increment NOT NULL,
  `global_personnel_num` varchar(100) default NULL,
  `display_name` varchar(100) default NULL,
  `first_name` varchar(100) default NULL,
  `middle_name` varchar(100) default NULL,
  `second_last_name` varchar(100) default NULL,
  `last_name` varchar(100) default NULL,
  `marital_status` smallint(5) default NULL,
  `gender` smallint(5) default NULL,
  `city` varchar(100) default NULL,
  `state` varchar(100) default NULL,
  `country` varchar(100) default NULL,
  `description` varchar(100) default NULL,
  `branch_office_id` smallint(6) not null,
  unique key `global_personnel_num` (`global_personnel_num`),
  constraint `loan_officer_id_ibfk_2` foreign key (`branch_office_id`) references `branch_office_dim` (`office_id`) on delete no action on update no action,
  PRIMARY KEY  (`loan_officer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;