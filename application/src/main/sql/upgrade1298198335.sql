create table calculated_interest_on_payment (
  loan_account_trxn_id integer not null,

  original_interest decimal(21,4) not null,
  original_interest_currency_id smallint,

  extra_interest_paid decimal(21,4),
  extra_interest_paid_currency_id smallint,

  primary key(loan_account_trxn_id),
  foreign key(loan_account_trxn_id)
    references loan_trxn_detail(account_trxn_id)
      on delete no action
      on update no action,

  foreign key(original_interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(extra_interest_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
