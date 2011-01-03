alter table currency drop column rounding_mode;
update currency set rounding_amount = 1;
