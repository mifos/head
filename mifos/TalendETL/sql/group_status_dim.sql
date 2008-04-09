CREATE TABLE `group_status_dim` (
  `status_id` smallint(4) NOT NULL auto_increment,
  `status_description` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
