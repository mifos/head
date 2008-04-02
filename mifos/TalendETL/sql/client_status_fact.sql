CREATE TABLE  `client_status_fact` (
  `client_status_fact_id` int(15) auto_increment,
  `client_id` int(15) NOT NULL,
  `status_id` smallint(4) not null,
  `time_id` smallint(4),
  primary key  (client_status_fact_id),
  constraint client_status_fk1 foreign key (client_id) references client_dim (client_id) on delete no action on update no action,
  constraint client_status_fk2 foreign key (status_id) references client_status_dim (status_id) on delete no action on update no action,
  constraint client_status_fk3 foreign key (time_id) references time_dim (time_id) on delete no action on update no action
) ENGINE=InnoDB DEFAULT CHARSET=utf8;