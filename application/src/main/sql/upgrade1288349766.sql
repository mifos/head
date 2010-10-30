/* Added for jira issue 2514 */
create table savings_offering_historical_interest_detail (
  id integer auto_increment not null,
  period_start_date date not null,
  period_end_date date not null,
  interest_rate decimal(13, 10) not null,
  min_amnt_for_int decimal(21,4) not null,
  min_amnt_for_int_currency_id smallint not null,
  product_id smallint not null,
  primary key(id),
  foreign key (product_id) references savings_offering (prd_offering_id)
)
engine=innodb character set utf8;