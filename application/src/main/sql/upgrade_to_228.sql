alter table currency drop column display_symbol;
alter table currency drop column default_currency;
alter table currency drop column default_digits_after_decimal;

update database_version set database_version = 228 where database_version = 227;
