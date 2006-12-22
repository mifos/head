-- This file checks certain parts of the data for consistency
-- It does not seem to be up to date as of Dec 2006.

/** Savings consistency script **/
create table savings_check (account_id int, savings_balance decimal(10,3), calculated_balance decimal(10,3));

insert into savings_check select c.account_id, c.savings_balance, sum(ifnull(a.deposit_amount,0))-sum(ifnull(a.withdrawal_amount,0))+sum(ifnull(a.interest_amount,0)) as calc from savings_trxn_detail a, account_trxn b, savings_account c
where a.account_trxn_id=b.account_Trxn_id
and c.account_id=b.account_id
group by c.account_id,c.savings_balance;

select * from savings_check where savings_balance<>calculated_balance;

drop table savings_check;

/**Loan consistency script **/
create table loan_check (account_id int, principal_paid decimal(10,3), interest_paid decimal(10,3), fees_paid decimal(10,3), penalty_paid decimal(10,3));

insert into loan_check 
select c.account_id, sum(ifnull(a.principal_amount,0)), sum(ifnull(a.interest_amount,0)), sum(ifnull(a.penalty_amount,0)+ifnull(a.misc_penalty_amount,0)), sum(ifnull(misc_fee_amount,0)) 
from loan_trxn_detail a, account_trxn b, loan_summary c
where a.account_trxn_id=b.account_Trxn_id
and c.account_id=b.account_id
and b.account_action_id<>10
group by c.account_id;

update loan_check a set a.fees_paid=ifnull(a.fees_paid,0)+ifnull((select sum(ifnull(c.fee_amount,0)) from fee_trxn_detail c, account_trxn b
where a.account_id=b.account_id
and b.account_trxn_id=c.account_trxn_id
group by a.account_id),0);

insert into loan_check(account_id,fees_paid)
select b.account_id, sum(ifnull(a.fee_amount,0)) from fee_trxn_detail a, account_trxn b, account c
where c.account_type_id=1
and b.account_id=c.account_id
and b.account_id not in (select account_id from loan_check)
and a.account_trxn_id=b.account_trxn_id
group by b.account_id;

select * from loan_check a, loan_summary b 
where a.account_id=b.account_id
and (ifnull(a.principal_paid,0) <> ifnull(b.principal_paid,0)
or ifnull(a.interest_paid,0) <> ifnull(b.interest_paid,0)
or ifnull(a.penalty_paid,0) <> ifnull(b.penalty_paid,0)
or ifnull(a.fees_paid,0) <> ifnull(b.fees_paid,0));

drop table loan_check;


/**Payment status check script **/
select a.account_id, a.installment_id from loan_schedule a, loan_fee_schedule b
where a.id=b.id
and a.installment_id=b.installment_id
and (
ifnull(a.principal,0) <> ifnull(a.principal_paid,0)
or ifnull(a.interest,0) <> ifnull(a.interest_paid,0)
or ifnull(a.penalty,0) <> ifnull(a.penalty_paid,0)
or ifnull(a.misc_penalty,0) <> ifnull(a.misc_penalty_paid,0)
or ifnull(a.misc_fees,0) <> ifnull(a.misc_fees_paid,0)
or ifnull(b.amount,0) <> ifnull(b.amount_paid,0)
)
and a.payment_status = 1;


/**Financial consistency script**/
create table dummy_fin_cons_check (coa_id smallint, search_id varchar(20));
alter table coahierarchy add column search_id varchar(20);
alter table coahierarchy add column marked smallint default 0;
alter table coa add column search_id varchar(20);

/**root gl codes**/
update coahierarchy a set a.search_id = (select b.glcode_id from coa b where b.coa_id=a.coa_id), a.marked=1 where a.parent_coaid is null;
delete from dummy_fin_cons_check;
insert into dummy_fin_cons_check select coa_id,search_id from coahierarchy where search_id is not null and marked=1; 
update coahierarchy a,dummy_fin_cons_check b set a.marked=1 where a.coa_id=b.coa_id;

/**find children of root and append with root.***/
update coahierarchy a,dummy_fin_cons_check b set a.search_id=concat(b.search_id,'.*') where a.parent_coaid=b.coa_id and marked=0;
update coahierarchy a set a.search_id=replace(a.search_id, '*', (select b.glcode_id from coa b where b.coa_id=a.coa_id)) where a.search_id like '%*' and marked=0;

/**loop through till all records are marked**/
delete from dummy_fin_cons_check;
insert into dummy_fin_cons_check select coa_id,search_id from coahierarchy where search_id is not null and marked<>1;
update coahierarchy a,dummy_fin_cons_check b set a.marked=1 where a.coa_id=b.coa_id;
update coahierarchy a,dummy_fin_cons_check b set a.search_id=concat(b.search_id,'.*') where a.parent_coaid=b.coa_id and a.marked=0;
update coahierarchy a set a.search_id=replace(a.search_id, '*', (select b.glcode_id from coa b where b.coa_id=a.coa_id)) where a.search_id like '%*' and a.marked=0;

/**execute after the above script has looped to have all marked rows set to 1**/
update coa a, coahierarchy b set a.search_id=b.search_id where a.coa_id=b.coa_id;

create table financial_check (assets_total decimal(13,3), liabilities_total decimal(13,3), income_total decimal(13,3), expenditure_total decimal(13,3));

insert into financial_check(assets_total)
select ifnull(sum(posted_amount),0) from financial_trxn 
where glcode_id in( 
select glcode_id from coa a where a.search_id like '1.%');

update financial_check set liabilities_total=(
select ifnull(sum(posted_amount),0) from financial_trxn 
where glcode_id in( 
select glcode_id from coa a where a.search_id like '14.%'));

update financial_check set income_total=(
select ifnull(sum(posted_amount),0) from financial_trxn 
where glcode_id in( 
select glcode_id from coa a where a.search_id like '18.%'));

update financial_check set expenditure_total=(
select ifnull(sum(posted_amount),0) from financial_trxn 
where glcode_id in( 
select glcode_id from coa a where a.search_id like '53.%'));

select * from financial_check;

drop table dummy_fin_cons_check;
drop table financial_check;
alter table coahierarchy drop column search_id;
alter table coahierarchy drop column marked;
alter table coa drop column search_id;
