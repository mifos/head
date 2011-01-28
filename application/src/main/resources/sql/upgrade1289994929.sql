create table cash_flow_detail (
  id smallint auto_increment not null,
  cash_flow_threshold decimal(13, 10),
  indebtedness_ratio decimal(13, 10),
  repayment_capacity decimal(13, 10),
  primary key(id)
)
engine=innodb character set utf8;

alter table loan_offering drop column cashflow_threshold;
alter table loan_offering add column cash_flow_detail_id smallint;
alter table loan_offering change column cashflow_comparison_flag cash_flow_comparison_flag smallint default 0;

alter table loan_offering
  add constraint foreign key(cash_flow_detail_id)
    references cash_flow_detail(id)
      on delete no action
      on update no action;

alter table cash_flow modify capital decimal(21, 4);
alter table cash_flow modify liability decimal(21, 4);