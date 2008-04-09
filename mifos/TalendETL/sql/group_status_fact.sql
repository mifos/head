CREATE TABLE  `group_status_fact` (
  `group_id` int(15) NOT NULL,
  `status_id` smallint(4) NOT NULL,
  `time_id` smallint(4) default NULL,
  CONSTRAINT `group_status_fk1` FOREIGN KEY (`group_id`) REFERENCES `group_dim` (`group_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `group_status_fk2` FOREIGN KEY (`status_id`) REFERENCES `group_status_dim` (`status_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `group_status_fk3` FOREIGN KEY (`time_id`) REFERENCES `time_dim` (`time_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;