alter table customer add constraint foreign key(parent_customer_id) references customer(customer_id) on delete no action on update no action;
alter table customer_position add constraint foreign key(parent_customer_id) references customer(customer_id) on delete no action on update no action;
alter table group_loan_counter add constraint foreign key(group_perf_id) references group_perf_history(id) on delete no action on update no action;

update database_version set database_version = 239 where database_version = 238;
