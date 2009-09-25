ALTER TABLE report_parameter_map ENGINE = InnoDB;
ALTER TABLE report_parameter ENGINE = InnoDB;
ALTER TABLE report_parameter_map CONVERT TO CHARACTER SET utf8;
ALTER TABLE report_parameter CONVERT TO CHARACTER SET utf8;
ALTER TABLE report_parameter CHANGE COLUMN DATA DATA text;
ALTER TABLE report_datasource CONVERT TO CHARACTER SET utf8;