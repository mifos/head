drop table if exists prd_offering_mix;

drop table if exists customer_family_detail;

drop table if exists ppi_likelihoods;
drop table if exists ppi_survey;
drop table if exists survey_response_date;
drop table if exists survey_response_number;
drop table if exists survey_response_freetext;
drop table if exists survey_response;
drop table if exists question_choices;
drop table if exists survey_response_choice;
drop table if exists survey_questions;
drop table if exists questions;
drop table if exists ppi_survey_instance;
drop table if exists survey_instance;
drop table if exists survey;

drop table if exists  financial_trxn;
drop table if exists  coahierarchy;
drop table if exists  financial_action;
drop table if exists  coa_idmapper;
drop table if exists  coa;

drop table if exists customer_attendance;

drop table if exists product_offering_mandatory_savings;
drop table if exists prd_offering_meeting;

drop table if exists customer_loan_account_detail;

drop table if exists loan_trxn_detail;

drop table if exists savings_trxn_detail;

drop table if exists customer_trxn_detail;

drop table if exists loan_arrears_aging;

drop table if exists account_fees_action_detail;

drop table if exists loan_fee_schedule;

drop table if exists customer_fee_schedule;

drop table if exists fee_trxn_detail;

drop table if exists account_trxn;

drop table if exists savings_activity_details;

drop table if exists savings_account;

drop table if exists account_fees;

drop table if exists customer_checklist;

drop table if exists account_actions_date;

drop table if exists customer_schedule;
drop table if exists loan_schedule;
drop table if exists saving_schedule;

drop table if exists account_survey;

drop table if exists account_payment;

drop table if exists prd_checklist;

drop table if exists loan_penalty;

drop table if exists checklist_detail;

drop table if exists attendance;

drop table if exists customer_account_activity;

drop table if exists customer_account;

drop table if exists change_log_detail;

drop table if exists loan_summary;

drop table if exists waive_off_history;

drop table if exists account_non_trxn_details;

drop table if exists loan_activity_details;

drop table if exists loan_perf_history;

drop table if exists max_min_no_of_install;

drop table if exists max_min_interest_rate;

drop table if exists max_min_loan_amount;

drop table if exists loan_account;

drop table if exists customer_movement;

drop table if exists account_notes;

drop table if exists account_flag_detail;

drop table if exists account_status_change_history;

drop table if exists account_state_flag;

drop table if exists account_activity;

drop table if exists savings_performance;

drop table if exists account_custom_field;

drop table if exists account;

drop table if exists checklist;
drop table if exists spouse_father_lookup;
drop table if exists customer_attendance_types;
drop table if exists customer_name_detail;

drop table if exists customer_flag_detail;

drop table if exists customer_hierarchy;

drop table if exists office_hierarchy;

-- DROP TABLE IF EXISTS PRD_SURVEY;

drop table if exists prd_fee_frequency;

drop table if exists customer_position;

drop table if exists customer_note;

drop table if exists loan_offering_fund;

drop table if exists customer_program;

drop table if exists customer_meeting_detail;

drop table if exists inherited_meeting;

drop table if exists customer_meeting;

drop table if exists recur_on_day;

drop table if exists recurrence_detail;

drop table if exists fee_frequency;

drop table if exists meeting;

drop table if exists personnel_role;
drop table if exists personnel_status;
drop table if exists roles_activity;

drop table if exists personnel_custom_field;

drop table if exists program_fund;

drop table if exists prd_offering_fees;

drop table if exists offering_fund;

drop table if exists customer_custom_field;

drop table if exists customer_address_detail;

drop table if exists customer_historical_data;

drop table if exists customer_picture;

drop table if exists field_configuration;

drop table if exists custom_field_definition;

drop table if exists entity_master;

drop table if exists system_configuration;

drop table if exists config_key_value_integer;

drop table if exists change_log;

drop table if exists personnel_notes;

drop table if exists personnel_movement;

drop table if exists personnel_details;

drop table if exists insurance_offering;

drop table if exists savings_offering;

drop table if exists no_of_install_same_for_all_loan;

drop table if exists loan_amount_same_for_all_loan;

drop table if exists no_of_install_from_loan_cycle ;

drop table if exists loan_amount_from_loan_cycle;

drop table if exists no_of_install_from_last_loan ;

drop table if exists loan_amount_from_last_loan ;

drop table if exists loan_offering;

drop table if exists client_initial_savings_offering;

drop table if exists prd_offering;

drop table if exists customer_detail;

drop table if exists customer_survey;

drop table if exists cust_perf_history;

drop table if exists client_perf_history;

drop table if exists group_perf_history;

drop table if exists customer;

drop table if exists office_action_payment_type;

drop table if exists penalty;

drop table if exists role;

drop table if exists personnel_hierarchy;

drop table if exists fee_frequency_type;
drop table if exists feelevel;
drop table if exists fees;
drop table if exists fee_status;
drop table if exists fee_formula_master;
drop table if exists fee_update_type;

drop table if exists imported_transactions_files;

drop table if exists personnel;

drop table if exists program;

drop table if exists customer_state_flag;

drop table if exists lookup_label;

drop table if exists lookup_value_locale;

drop table if exists branch_ho_update;

drop table if exists holiday;
drop table if exists repayment_rule;

drop table if exists mfi_attribute;

drop table if exists office_address;

drop table if exists  office_custom_field;

drop table if exists office;

drop table if exists office_status;

drop table if exists office_code;


drop table if exists interest_calc_rule;

drop table if exists fee_payments_categories_type;

drop table if exists supported_locale;

drop table if exists customer_state;

drop table if exists interest_types;

drop table if exists prd_status;

drop table if exists prd_pmnt_type;

drop table if exists accepted_payment_type;

drop table if exists admin_document_acc_state_mix;

drop table if exists account_state;

drop table if exists customer_level;

drop table if exists category_type;

drop table if exists position;

drop table if exists prd_category;

drop table if exists prd_applicable_master;

drop table if exists office_level;

drop table if exists personnel_level;

drop table if exists savings_type;

drop table if exists yes_no_master;

drop table if exists prd_state;

drop table if exists prd_category_status;

drop table if exists recommended_amnt_unit;

drop table if exists prd_type;

drop table if exists grace_period_type;

drop table if exists interest_calculation_types;

drop table if exists fund;

drop table if exists fee_payment;

drop table if exists freq_of_deposits;

drop table if exists fee_type;

drop table if exists account_type;







drop table if exists meeting_type;

drop table if exists finanical_action;

drop table if exists language;

drop table if exists account_action;

drop table if exists survey_template;

drop table if exists recurrence_type;

drop table if exists report;
drop table if exists report_datasource;
drop table if exists report_parameter;
drop table if exists report_jasper_map;
drop table if exists report_category;
drop table if exists report_parameter_map;

drop table if exists activity;

drop table if exists gl_code;

drop table if exists country;

drop table if exists supported_modes;

drop table if exists transaction_type;

drop table if exists payment_type;

drop table if exists lookup_value;

drop table if exists lookup_entity;



drop table if exists scheduled_tasks;

drop table if exists temp_id;

drop table if exists currency;

drop table if exists glcode;


drop table if exists fund_code;

drop table if exists loan_counter;
drop table if exists group_loan_counter;

drop table if exists loan_monitoring;

drop table if exists admin_document;

drop table if exists database_version;

drop table if exists batch_client_summary;
drop table if exists batch_staff_summary;
drop table if exists batch_loan_arrears_aging;
drop table if exists batch_staffing_level_summary;
drop table if exists batch_loan_details;
drop table if exists batch_loan_arrears_profile;
drop table if exists batch_branch_report;

drop table if exists batch_branch_confirmation_recovery;
drop table if exists batch_branch_confirmation_issue;
drop table if exists batch_branch_confirmation_disbursement;
drop table if exists batch_branch_cash_confirmation_report;
drop table if exists office_holiday;





