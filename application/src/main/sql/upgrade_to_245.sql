set foreign_key_checks=0;

drop table if exists coll_sheet_savings_details;
drop table if exists coll_sheet_loan_details;
drop table if exists coll_sheet_customer;
drop table if exists coll_sheet;

set foreign_key_checks=1;

update database_version set database_version = 245 where database_version = 244;