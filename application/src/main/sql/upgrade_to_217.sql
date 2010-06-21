alter table report_parameter_map engine = innodb;
alter table report_parameter engine = innodb;
alter table report_parameter_map convert to character set utf8;
alter table report_parameter convert to character set utf8;
alter table report_parameter change column data data text;
alter table report_datasource convert to character set utf8;

update database_version set database_version = 217 where database_version = 216;

