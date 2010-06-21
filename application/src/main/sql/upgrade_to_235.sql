update loan_fee_schedule set amount_paid = 0 where amount_paid is null;
update loan_fee_schedule lfs set amount_paid_currency_id = lfs.amount_currency_id where amount_paid_currency_id is null;
alter table loan_fee_schedule modify column amount_paid decimal(21,4) not null;
alter table loan_fee_schedule modify column amount_paid_currency_id smallint not null;

update database_version set database_version = 235 where database_version = 234;
