create table variable_installment_details (
  id smallint auto_increment not null,
  min_gap_in_days smallint,
  max_gap_in_days smallint,
  min_loan_amount decimal(21,4),
  primary key(id)
)
engine=innodb character set utf8;

alter table loan_offering
    add variable_installment_flag smallint default 0,
    add variable_installment_details_id smallint,
    add foreign key(variable_installment_details_id) references variable_installment_details(id)
    on delete no action
    on update no action;