/* 
 * Fix for existing data: MIFOS-1705
 * update loan_summary 'paid' amounts from financial_trxn postings for written-off 
 * and rescheduled loan accounts
 */

update loan_summary ls
join account a on a.account_id = ls.account_id and a.account_state_id in (7,8)
left join    (select a.account_id,
        sum(fint.posted_amount) as amount_paid
        from account a
        join loan_account la on a.account_id = la.account_id
        join account_payment ap on ap.account_id = la.account_id
        join account_trxn at on at.payment_id = ap.payment_id
        join financial_trxn fint on fint.account_trxn_id = at.account_trxn_id
        where a.account_state_id in (7,8)
        and fint.fin_action_id = 1
        and fint.debit_credit_flag = 1
        group by a.account_id) correct on correct.account_id = ls.account_id
        
set ls.principal_paid = abs(ifnull(correct.amount_paid,0));


update loan_summary ls
join account a on a.account_id = ls.account_id and a.account_state_id in (7,8)
left join    (select a.account_id,
        sum(fint.posted_amount) as amount_paid
        from account a
        join loan_account la on a.account_id = la.account_id
        join account_payment ap on ap.account_id = la.account_id
        join account_trxn at on at.payment_id = ap.payment_id
        join financial_trxn fint on fint.account_trxn_id = at.account_trxn_id
        where a.account_state_id in (7,8)
        and fint.fin_action_id = 2
        and fint.debit_credit_flag = 1
        group by a.account_id) correct on correct.account_id = ls.account_id
        
set ls.interest_paid = abs(ifnull(correct.amount_paid,0));


update loan_summary ls
join account a on a.account_id = ls.account_id and a.account_state_id in (7,8)
left join    (select a.account_id,
        sum(fint.posted_amount) as amount_paid
        from account a
        join loan_account la on a.account_id = la.account_id
        join account_payment ap on ap.account_id = la.account_id
        join account_trxn at on at.payment_id = ap.payment_id
        join financial_trxn fint on fint.account_trxn_id = at.account_trxn_id
        where a.account_state_id in (7,8)
        and fint.fin_action_id in (3, 4)
        and fint.debit_credit_flag = 1
        group by a.account_id) correct on correct.account_id = ls.account_id
        
set ls.fees_paid = abs(ifnull(correct.amount_paid,0));


update loan_summary ls
join account a on a.account_id = ls.account_id and a.account_state_id in (7,8)
left join    (select a.account_id,
        sum(fint.posted_amount) as amount_paid
        from account a
        join loan_account la on a.account_id = la.account_id
        join account_payment ap on ap.account_id = la.account_id
        join account_trxn at on at.payment_id = ap.payment_id
        join financial_trxn fint on fint.account_trxn_id = at.account_trxn_id
        where a.account_state_id in (7,8)
        and fint.fin_action_id = 6
        and fint.debit_credit_flag = 1
        group by a.account_id) correct on correct.account_id = ls.account_id
        
set ls.penalty_paid = abs(ifnull(correct.amount_paid,0));


update database_version set database_version = 250 where database_version = 249;