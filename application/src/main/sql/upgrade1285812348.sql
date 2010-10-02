create table cash_flow  (
  id int auto_increment not null primary key,
  capital decimal(13, 10) not null,
  liability decimal(13, 10) not null
) engine=innodb character set utf8;

create table monthly_cash_flow_details(
  id int auto_increment not null primary key,
  cash_flow_id int,
  revenue decimal(13, 10) not null,
  expenses decimal(13, 10) not null,
  notes varchar(300),
  foreign key(cash_flow_id)
  references cash_flow(id)
   on delete no action
   on update no action
) engine=innodb character set utf8;

create table loan_cash_flow  (
  loan_id int not null,
  cash_flow_id int not null,
  foreign key(loan_id)
  references loan_account(account_id)
   on delete no action
   on update no action,
  foreign key(cash_flow_id)
  references cash_flow(id)
   on delete no action
   on update no action
) engine=innodb character set utf8;