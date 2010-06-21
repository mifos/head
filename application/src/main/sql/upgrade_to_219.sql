alter table account_payment add column comment varchar(80);

update database_version set database_version = 219 where database_version = 218;
