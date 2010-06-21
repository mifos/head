alter table prd_offering add column currency_id smallint;

alter table prd_offering add constraint foreign key (currency_id) references currency(currency_id) on delete no action on update no action;

update database_version set database_version = 226 where database_version = 225;
