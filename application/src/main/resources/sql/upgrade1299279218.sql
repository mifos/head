alter table calculated_interest_on_payment add column interest_due_till_paid decimal(21,4);
alter table calculated_interest_on_payment add column interest_due_till_paid_currency_id smallint;
alter table calculated_interest_on_payment add constraint foreign key (interest_due_till_paid_currency_id) references currency(currency_id) on delete no action on update no action;