alter table loan_schedule add column extra_interest decimal(21,4);
alter table loan_schedule add column extra_interest_currency_id smallint;
alter table loan_schedule add column extra_interest_paid decimal(21,4);
alter table loan_schedule add column extra_interest_paid_currency_id smallint;
alter table loan_schedule add constraint foreign key (extra_interest_currency_id) references currency(currency_id) on delete no action on update no action;
alter table loan_schedule add constraint foreign key (extra_interest_paid_currency_id) references currency(currency_id) on delete no action on update no action;
