set foreign_key_checks=0;
alter table loan_account drop foreign key fk_loan_rankday;
alter table loan_account drop index fk_loan_rankday;
alter table loan_account drop foreign key fk_loan_monthweek;
alter table loan_account drop index fk_loan_monthweek;

alter table recur_on_day drop foreign key recur_on_day_ibfk_2;
alter table recur_on_day drop index days;
alter table recur_on_day drop foreign key recur_on_day_ibfk_3;
alter table recur_on_day drop index rank_of_days;

delete from lookup_value_locale 
where lookup_value_id = 143 
or lookup_value_id = 145 
or lookup_value_id = 147
or lookup_value_id = 149 
or lookup_value_id = 151
or lookup_value_id = 153 
or lookup_value_id = 155
or lookup_value_id = 179 
or lookup_value_id = 181 
or lookup_value_id = 183
or lookup_value_id = 185 
or lookup_value_id = 187;

delete from lookup_value 
where lookup_id = 72
or lookup_id = 73 
or lookup_id = 74 
or lookup_id = 75 
or lookup_id = 76
or lookup_id = 77 
or lookup_id = 78
or lookup_id = 99
or lookup_id = 100 
or lookup_id = 101 
or lookup_id = 102 
or lookup_id = 103;

drop table week_days_master;
drop table rank_days_master;
set foreign_key_checks=1;
update database_version set database_version = 244 where database_version = 243;