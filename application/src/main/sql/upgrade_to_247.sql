/* jira issue mifos-1954 
 * John W - Ran these upgrades against EC2 instance with 1 million clients.  
 * Took about 11 minutes but didn't fall over (over a million updates, 
 * concern was log space).
 */
update group_perf_history gph
join customer c on gph.customer_id = c.customer_id
left join account a on a.customer_id = c.customer_id and account_type_id = 1 and a.account_state_id = 6
left join loan_account la on la.account_id = a.account_id
left join account_status_change_history asch on asch.account_id = a.account_id and asch.new_status = 6

set gph.last_group_loan_amnt_disb = la.loan_amount, gph.last_group_loan_amnt_disb_currency_id = la.loan_amount_currency_id

where ifnull(asch.account_status_change_id, -1) = 
        (select ifnull(max(asch2.account_status_change_id), -1) 
        from customer c2
        join account a2 on a2.customer_id = c2.customer_id and account_type_id = 1 and a2.account_state_id = 6
        join account_status_change_history asch2 on asch2.account_id = a2.account_id and asch2.new_status =6
        where c2.customer_id = c.customer_id);

update client_perf_history cph
join customer c on cph.customer_id = c.customer_id
left join account a on a.customer_id = c.customer_id and account_type_id = 1 and a.account_state_id = 6
left join loan_account la on la.account_id = a.account_id
left join account_status_change_history asch on asch.account_id = a.account_id and asch.new_status = 6

set cph.last_loan_amnt = la.loan_amount, cph.last_loan_amnt_currency_id = la.loan_amount_currency_id

where ifnull(asch.account_status_change_id, -1) = 
        (select ifnull(max(asch2.account_status_change_id), -1) 
        from customer c2
        join account a2 on a2.customer_id = c2.customer_id and account_type_id = 1 and a2.account_state_id = 6
        join account_status_change_history asch2 on asch2.account_id = a2.account_id and asch2.new_status =6
        where c2.customer_id = c.customer_id);

    
UPDATE DATABASE_VERSION SET DATABASE_VERSION = 247 WHERE DATABASE_VERSION = 246;
