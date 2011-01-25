alter table account_payment modify column receipt_number varchar(60);

update database_version set database_version = 258 where database_version = 257;
