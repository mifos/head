alter table currency drop column rounding_mode;
update currency set rounding_amount = 1;

update database_version set database_version = 231 where database_version = 230;
