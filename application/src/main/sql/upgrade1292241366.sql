create table original_loan_fee_schedule (
  account_fees_detail_id integer auto_increment not null,
  id integer not null,
  installment_id integer not null,
  fee_id smallint not null,
  account_fee_id integer not null,
  amount decimal(21,4),
  amount_currency_id smallint,
  amount_paid decimal(21,4) not null,
  amount_paid_currency_id smallint not null,
  version_no integer not null,
  primary key(account_fees_detail_id),
  foreign key(id)
    references original_loan_schedule(id)
      on delete no action
      on update no action,
  foreign key(amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(amount_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fee_id)
    references fees(fee_id),
  foreign key(account_fee_id)
    references account_fees(account_fee_id)
)
engine=innodb character set utf8;