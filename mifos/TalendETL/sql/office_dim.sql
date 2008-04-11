create table `head_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `status_id` smallint(6) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `head_office_global_idx` (`global_office_num`)
) engine=innodb default charset=utf8;

create table `regional_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `head_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `regional_office_global_idx` (`global_office_num`),
  key `head_office_id` (`head_office_id`),
  constraint `regional_office_ibfk_2` foreign key (`head_office_id`) references `head_office_dim` (`office_id`) on delete no action on update no action
) engine=innodb default charset=utf8;


create table  `subregional_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `regional_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `subregional_office_global_idx` (`global_office_num`),
  key `regional_office_id` (`regional_office_id`),
  constraint `subregional_office_ibfk_2` foreign key (`regional_office_id`) references `regional_office_dim` (`office_id`) on delete no action on update no action
) engine=innodb default charset=utf8;


create table  `area_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `subregional_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `area_office_global_idx` (`global_office_num`),
  key `subregional_office_id` (`subregional_office_id`),
  constraint `area_office_ibfk_2` foreign key (`subregional_office_id`) references `subregional_office_dim` (`office_id`) on delete no action on update no action
) engine=innodb default charset=utf8;


create table  `branch_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `area_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `branch_office_global_idx` (`global_office_num`),
  key `area_office_id` (`area_office_id`),
  constraint `branch_office_ibfk_2` foreign key (`area_office_id`) references `area_office_dim` (`office_id`) on delete no action on update no action
) engine=innodb default charset=utf8;