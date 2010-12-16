create table original_loan_schedule (
  id integer auto_increment not null,
  account_id integer not null,
  customer_id integer not null,
  currency_id smallint,
  action_date date,

  principal decimal(21,4) not null,
  principal_currency_id smallint,

  interest decimal(21,4) not null,
  interest_currency_id smallint,

  penalty decimal(21,4) not null,
  penalty_currency_id smallint,

  misc_fees decimal(21,4),
  misc_fees_currency_id smallint,

  misc_fees_paid decimal(21,4),
  misc_fees_paid_currency_id smallint,

  misc_penalty decimal(21,4),
  misc_penalty_currency_id smallint,

  misc_penalty_paid decimal(21,4),
  misc_penalty_paid_currency_id smallint,

  principal_paid decimal(21,4),
  principal_paid_currency_id smallint,

  interest_paid decimal(21,4),
  interest_paid_currency_id smallint,

  penalty_paid decimal(21,4),
  penalty_paid_currency_id smallint,

  payment_status smallint not null,
  installment_id smallint not null,
  payment_date date,
  parent_flag smallint,
  version_no integer not null,

  extra_interest decimal(21,4),
  extra_interest_currency_id smallint,

  extra_interest_paid decimal(21,4),
  extra_interest_paid_currency_id smallint,

  primary key(id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
 foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,


  foreign key(principal_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(penalty_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(misc_fees_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(misc_fees_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(misc_penalty_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(principal_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(interest_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(penalty_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(misc_penalty_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,

  foreign key(extra_interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(extra_interest_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
