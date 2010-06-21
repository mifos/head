create table  max_min_interest_rate(
account_id integer   auto_increment not null,
  min_interest_rate  decimal(21,4) not null,
  max_interest_rate  decimal(21,4) not null,
  primary key(account_id),
foreign key(account_id)
    references  loan_account(account_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

insert into max_min_interest_rate (account_id, min_interest_rate, max_interest_rate)
select loan_account.account_id, loan_offering.min_interest_rate, loan_offering.max_interest_rate
from loan_account, loan_offering
where loan_account.prd_offering_id = loan_offering.prd_offering_id;

update database_version set database_version = 249 where database_version = 248;