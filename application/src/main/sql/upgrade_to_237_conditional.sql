
alter table fees 
modify fee_amount decimal(21,4);

alter table cust_perf_history 
modify last_loan_amnt decimal(21,4),
modify total_savings_amnt decimal(21,4),
modify delinquint_portfolio decimal(21,4);

alter table customer_detail 
modify poverty_lhood_pct decimal(21,4);

alter table loan_offering 
modify min_loan_amount decimal(21,4) default null,
modify max_loan_amnt decimal(21,4) default null,
modify default_loan_amount decimal(21,4) default null;

alter table savings_offering 
modify recommended_amount decimal(21,4),
modify max_amnt_withdrawl decimal(21,4),
modify min_amnt_for_int decimal(21,4);

alter table customer_historical_data 
modify loan_amount decimal(21,4),
modify total_amount_paid decimal(21,4),
modify interest_paid decimal(21,4);

alter table account_activity 
modify principal decimal(21,4),
modify principal_outstanding decimal(21,4);

alter table loan_account 
modify loan_amount decimal(21,4),
modify loan_balance decimal(21,4);

alter table loan_activity_details 
modify principal_amount decimal(21,4),
modify interest_amount decimal(21,4),
modify penalty_amount decimal(21,4),
modify fee_amount decimal(21,4),
modify balance_principal_amount decimal(21,4),
modify balance_interest_amount decimal(21,4),
modify balance_penalty_amount decimal(21,4),
modify balance_fee_amount decimal(21,4);

alter table loan_summary 
modify orig_principal decimal(21,4), 
modify orig_interest decimal(21,4), 
modify orig_fees decimal(21,4), 
modify orig_penalty decimal(21,4), 
modify principal_paid decimal(21,4),
modify interest_paid decimal(21,4),
modify fees_paid decimal(21,4),
modify penalty_paid decimal(21,4),
modify raw_amount_total decimal(21,4);

alter table customer_account_activity 
modify amount decimal(21,4);

alter table account_payment 
modify amount decimal(21,4) not null;

alter table customer_schedule
modify misc_fees decimal(21,4),
modify misc_fees_paid decimal(21,4), 
modify misc_penalty decimal(21,4),
modify misc_penalty_paid decimal(21,4);

alter table loan_schedule 
modify principal decimal(21,4) not null,
modify interest decimal(21,4) not null,
modify penalty decimal(21,4) not null,
modify misc_fees decimal(21,4), 
modify misc_fees_paid decimal(21,4),
modify misc_penalty decimal(21,4),
modify misc_penalty_paid decimal(21,4),
modify principal_paid decimal(21,4),
modify interest_paid decimal(21,4),
modify penalty_paid decimal(21,4);

alter table saving_schedule 
modify deposit decimal(21,4) not null,
modify deposit_paid decimal(21,4);

alter table account_fees 
modify account_fee_amnt decimal(21,4) not null,
modify fee_amnt decimal(21,4) not null;

alter table savings_account 
modify savings_balance decimal(21,4),
modify recommended_amount decimal(21,4),
modify int_to_be_posted decimal(21,4),
modify min_amnt_for_int decimal(21,4);

alter table savings_activity_details 
modify amount decimal(21,4) not null,
modify balance_amount decimal(21,4) not null;

alter table savings_performance 
modify total_deposits decimal(21,4),
modify total_withdrawals decimal(21,4),
modify total_interest_earned decimal(21,4);

alter table account_trxn 
modify amount decimal(21,4) not null;

alter table fee_trxn_detail
modify fee_amount decimal(21,4) not null;

alter table loan_fee_schedule 
modify amount decimal(21,4),
modify amount_paid decimal(21,4) not null;

alter table customer_fee_schedule
modify amount decimal(21,4),
modify amount_paid decimal(21,4);

alter table savings_trxn_detail
modify deposit_amount decimal(21,4) ,
modify withdrawal_amount decimal(21,4) ,
modify interest_amount decimal(21,4) ,
modify balance decimal(21,4);

alter table loan_trxn_detail
modify principal_amount decimal(21,4),
modify interest_amount decimal(21,4),
modify penalty_amount decimal(21,4),
modify misc_fee_amount decimal(21,4),
modify misc_penalty_amount decimal(21,4);

alter table customer_trxn_detail
modify total_amount decimal(21,4),
modify misc_fee_amount decimal(21,4),
modify misc_penalty_amount decimal(21,4);

alter table customer_loan_account_detail
modify principal decimal(21,4) not null,
modify interest decimal(21,4) not null,
modify penalty decimal(21,4) not null;

alter table financial_trxn
modify posted_amount decimal(21,4) not null,
modify balance_amount decimal(21,4) not null;

alter table coll_sheet_customer
modify total_due_savings_loan decimal(21,4) ,
modify cust_accnt_fee decimal(21,4),
modify cust__accnt_penalty decimal(21,4) ,
modify collective_ln_amnt_due decimal(21,4),
modify collective_ln_disbursal decimal(21,4),
modify collective_savings_due decimal(21,4),
modify collective_accnt_charges decimal(21,4),
modify collective_total_charges decimal(21,4),
modify collective_net_cash_in decimal(21,4);

alter table coll_sheet_loan_details
modify total_prin_due decimal(21,4) not null,
modify orig_loan_amnt decimal(21,4) not null, 
modify amnt_to_close_loan decimal(21,4) not null,
modify principal_due decimal(21,4),
modify interest_due decimal(21,4), 
modify fees_due decimal(21,4),
modify penalty_due decimal(21,4),
modify total_scheduled_amnt_due decimal(21,4),
modify principal_overdue decimal(21,4),
modify interest_overdue decimal(21,4),
modify fees_overdue decimal(21,4),
modify penalty_overdue decimal(21,4),
modify total_amnt_overdue decimal(21,4),
modify total_amnt_due decimal(21,4),
modify amnt_tobe_disbursed decimal(21,4);

alter table coll_sheet_savings_details 
modify accnt_balance decimal(21,4), 
modify recommended_amnt_due decimal(21,4),
modify amnt_overdue decimal(21,4),
modify total_savings_amnt_due decimal(21,4);

alter table client_perf_history 
modify last_loan_amnt decimal(21,4),
modify total_savings_amnt decimal(21,4),
modify delinquint_portfolio decimal(21,4);

alter table group_perf_history 
modify last_group_loan_amnt_disb decimal(21,4),
modify avg_loan_size decimal(21,4),
modify total_outstand_loan_amnt decimal(21,4),
modify portfolio_at_risk decimal(21,4),
modify total_savings_amnt decimal(21,4);

alter table loan_arrears_aging 
modify overdue_principal decimal(21,4), 
modify overdue_interest decimal(21,4),
modify overdue_balance decimal(21,4),
modify unpaid_principal decimal(21,4),
modify unpaid_interest decimal(21,4),
modify unpaid_balance decimal(21,4);


alter table ppi_survey_instance 
modify bottom_half_below decimal(21,4),
modify top_half_below decimal(21,4);

alter table ppi_likelihoods 
modify bottom_half_below decimal(21,4) not null,
modify top_half_below decimal(21,4) not null;

alter table loan_amount_from_last_loan 
modify start_range decimal(21,4) not null,
modify end_range decimal(21,4) not null,
modify min_loan_amount decimal(21,4) not null,
modify max_loan_amnt decimal(21,4) not null,
modify default_loan_amount decimal(21,4) not null;

alter table no_of_install_from_last_loan 
modify start_range decimal(21,4) not null,
modify end_range decimal(21,4) not null,
modify min_no_install decimal(21,4) not null,
modify max_no_install decimal(21,4) not null,
modify default_no_install decimal(21,4) not null;

alter table loan_amount_from_loan_cycle 
modify min_loan_amount decimal(21,4) not null,
modify max_loan_amnt decimal(21,4) not null,
modify default_loan_amount decimal(21,4) not null;

alter table no_of_install_from_loan_cycle
modify min_no_install decimal(21,4) not null,
modify max_no_install decimal(21,4) not null,
modify default_no_install decimal(21,4) not null,
modify range_index decimal(21,4) not null;

alter table loan_amount_same_for_all_loan 
modify min_loan_amount decimal(21,4) not null,
modify max_loan_amnt decimal(21,4) not null,
modify default_loan_amount decimal(21,4) not null;

alter table no_of_install_same_for_all_loan 
modify min_no_install decimal(21,4) not null,
modify max_no_install decimal(21,4) not null,
modify default_no_install decimal(21,4) not null;

alter table max_min_loan_amount 
modify min_loan_amount decimal(21,4) not null,
modify max_loan_amount decimal(21,4) not null;

alter table max_min_no_of_install 
modify min_no_install decimal(21,4) not null,
modify max_no_install decimal(21,4) not null;

alter table batch_loan_arrears_aging 
modify amount_aging decimal(21,4) not null,
modify amount_outstanding_aging decimal(21,4) not null,
modify interest_aging decimal(21,4) not null;

alter table batch_staff_summary 
modify loan_amount_outstanding decimal(21,4) not null,
modify interest_fees_outstanding decimal(21,4) not null,
modify portfolio_at_risk decimal(21,4) not null,
modify   loan_arrears_amount decimal(21,4) not null;

alter table batch_loan_details 
modify loan_amount_issued decimal(21,4) not null,
modify loan_interest_issued decimal(21,4) not null,
modify loan_outstanding_amount decimal(21,4) not null,
modify loan_outstanding_interest decimal(21,4) not null;

alter table batch_loan_arrears_profile
modify overdue_balance decimal(21,4) not null,
modify unpaid_balance decimal(21,4) not null,
modify outstanding_amount_at_risk decimal(21,4) not null,
modify overdue_amount_at_risk decimal(21,4) not null;

alter table batch_branch_confirmation_recovery
modify  due decimal(21,4) not null,
modify  actual decimal(21,4) not null,
modify  arrears decimal(21,4) not null;

alter table batch_branch_confirmation_issue
modify actual decimal(21,4) not null;

alter table batch_branch_confirmation_disbursement
modify actual decimal(21,4) not null;
