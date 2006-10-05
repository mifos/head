#This file gives common data for all the modules
$test_site="http://aditidt128:8080/mifos"
$username="userName"
$password="password"
$log_in='Login'
$validname="mifos"
$validpwd="mifos"
$maxcol
$maxrow
$hostname="aditidt128"
$dbuser="reddy"
$dbpwd="Mifos"
$db="mifos"
$filePrefix=File.basename($PROGRAM_NAME)

#bulk entry start
$branch_office_msg="Please select the value for Branch Office"
$loan_officer_msg="Please select the value for Loan officer"
$center_msg="Please select the value for Kendra"
$mode_payment_msg="Please select the value for Mode of payment"
$date_trxn_msg="Please select the value for Date of transaction"
#bulk entry end

#center start
$additional_information_msg="Please specify required additional information"
$center_name_msg="Please specify Name"
$meeting_msg="Please select Meeting schedule"
$Loan_officer_msg="Please select loan officer"
$meetin_msg="Meeting schedule for Kendra"
$center_review="Create a new Kendra - Review & submit"
$center_success="You have successfully created a new Kendra"
#center end

#meeting start
$reccur_week_msg="Please ensure that the recurrence pattern has both week and day specified"
$location_msg="Please specify a Meeting location"
$month_msg="Please select the day of month or rank of day if month is selected"
#meeting end

# Check list Start
$checklist_item_msg = "Please specify at least one Item."
# Check list End

#Office start
$new_office_link = "Define a new office"
$review_office_info = "Add a new office - Review & submit"
$office_add_info_msg = "Please enter the value for the required additional information."
$office_name_msg = "Please specify Office name."
$office_shortname_msg = "Please specify Office short name."
$office_parent_msg = "Please select Parent office."
$office_type_msg = "Please select Office type."
$office_created_msg = "You have successfully added a new office "
$office_blankspaces_msg="Please specify character(s) other than blank space(s)."
$office_dupl_name_msg="This Office name already exists in the application. Please specify a different name."
$office_dupl_shortname_msg="This Office short name already exists in the application. Please specify a different name."
#Office end

#User Start
$user_created_msg = "You have successfully added a new user" 
$review_user_info = "Add a new user - Review & Submit"
$new_user_link = "Define new system user"
$new_user_page_msg = "Add a new user - Enter user information"
$user_firstname_msg = "Please specify First Name."
$user_lastname_msg = "Please specify Last Name."
$user_dob_msg = "Please specify Date of Birth."
$user_gender_msg = "Please select Gender."
$user_userhierarchy_msg = "Please select User Hierarchy."
$user_name_msg = "Please specify User Name."
$user_pwd_msg = "Please ensure that password and confirm password entries are made and they are the same."

$max_len_first_name="The maximum length for First Name field is 100 characters."
$max_len_middle_name="The maximum length for Middle Name field is 100 characters."
$max_len_sec_last_name="The maximum length for Second Last Name field is 100 characters."
$max_len_last_name="The maximum length for Last Name field is 100 characters."
$max_len_govt_id="The maximum length for Government ID field is 50 characters."
$max_len_addr1="The maximum length for Address 1 field is 200 characters."
$max_len_addr2="The maximum length for Address 2 field is 200 characters."
$max_len_addr3="The maximum length for Address 2 field is 200 characters."
$max_len_city="The maximum length for City field is 100 characters."
$max_len_state="The maximum length for State field is 100 characters."
$max_len_country="The maximum length for Country field is 100 characters."
$max_len_post_code="The maximum length for Postal code field is 20 characters."
$max_len_tel="The maximum length for Telephone field is 20 characters."
$max_len_uname="The maximum length for User Name field is 20 characters."
$max_len_passwd="The maximum length for Password field is 20 characters."
$dupl_user_name_msg_0="The user name specified "
$dupl_user_name_msg_1=" already exists in the application. Please specify a different user name."
$dupl_name_dob_msg="The combination of Name and Date of Birth already exists in the application. Please specify a different name. "
$username_with_space="Please do not specify [spaces] for User Name."

#User End

#Group start
$group_name_msg="Please specify Name"
$group_LO_msg="Please select Formed By"
$group_custom_msg="Please specify additional information"
$group_review="Review the information below. Click Submit if you are satisfied or click Edit to make changes. Click Cancel to return to Clients & Accounts without submitting information"
$group_select_center="Create a new Group - Select a Kendra"
$clicnts_Accounts_msg="To review or edit a Member, Group,  Kendra, or account"
$group_success="You have successfully created a new Group"
$group_select_group="Create new Group - Enter Group information" 
$group_edit="Edit Group information"
$group_edit_message_check="Edit Group information" 
$group_edit_review="Preview Group information"
$change_log="View change log"
$center_membership="Edit Kendra membership"
$center_membership_select_center="Change Kendramembership"
$center_membership_select_center_confirm="Change Kendra membership" 
$group_loan="Create Loan Account -  Enter loan account information"
$view_all_closed_accounts_group="View all closed accounts"
#Group end

#Client start
$Member_select_group="Create new Member - Select Group"
$Member_select_Member="Create New Member - Enter personal information"
$Member_select_branch="Create New Member - Select a Branch Office"
$Member_salitution_msg="Please select value for Salutation"
$Member_fname_msg="Please specify value for First Name"
$Member_lname_msg="Please specify value for Last Name"
$Member_dob_msg="Please specify value for Date Of Birth"
$Member_gender_msg="Please select value for Gender"
$Member_relesion_msg="Please select value for Religion"
$Member_education_msg="Please select value for Education Level"
$Member_spo_or_father_type_msg="Please select value for Spouse/Father Type"
$Member_spo_or_father_fname_msg="Please specify value for Spouse/Father First Name"
$Member_spo_or_father_lname_msg="Please specify value for Spouse/Father Last Name"
$Member_custom_msg="Please specify required additional information"
$Member_mfi_page_msg="Create New Member - Enter MFI information"
$Member_formedby_msg="Please select value for Formed by"
$Member_review="Create New Member - Review & submit"
$Member_success="You have successfully created a new Member"
$Member_edit_personnel="Manage Member - Edit personal information"
$Member_edit_mfi="Manage Member - Edit MFI information" 
$edit_personnel="Edit personal information"
$edit_mfi="Edit MFI information"
$review_personnel="Review & Submit Personal Information "
$review_mfi="Preview MFI information "
$change_group="Change Group membership"
$edit_group_member="Edit Group membership"
$view_all_closed_accounts_Member="View all closed accounts"
$Member_outof_group_link="Click here to continue if Group membership is not required for your Member."
$Member_charges_label="Member charges"
#Client end

#savings account start
$savingsaccount_link="Create Margin Money Account"
$savings_select_client="Create Margin Money Account - Select Member"
$savings_select_mmaccount="Create Margin Money account - Enter Margin Money account information"
$savings_enter_account_information="Create Margin Money account - Enter Margin Money account information"
$savings_preview="Create Margin Money account - Preview Margin Money account information"
$mandatory_without_client="Please specify some value"
$mandatory_without_product="Please select the value for Product Instance"
$savings_success="You have successfully created a new Margin Money account"
#savings account end
#fee started
$feecheck="Define new fees"
$f_m_nam_e="Please specify Fee Name "
$f_m_max_le="The length for fee name should not be greater than 50 characters."
$f_m_amt_e="Please specify Amount"
$f_m_max_amt="Amount should lie between 0-999999999.99"
$f_m_cat_e="Please select customers/products to which Fees Applies"
$f_m_freq_pe="Please select periodic along with frequency."
$f_m_freq_oe="Please select one time along with payment type."
$f_m_freq_we="Please select the value for recur every week if week is selected."
$f_m_freq_me="Please select the value for recur every month if month is selected."
$f_m_freq_ra="Please enter either rate or amount."
$gl_code="Please select GL Code"
$fee_success="You have successfully defined a new fee"
$f_m_rate_formula="Please enter rate along with formula."
$f_m_rate_and_formula="Rate should lie between 0-999%"
$fee_app_to={
"1"=>"All Customers",
"2"=>"Member",
"3"=>"Group",
"4"=>"Kendra",
"5"=>"Loans"
}
$default_fee={
"0"=>"No","1"=>"yes"}
$timeocrg={"1"=>"Upfront","2"=>"Time Of Disburstment","3"=>"Time of First Loan Repayment"}
$fee_freq={"1"=>"Periodic","2"=>"One time"}
$recur_type={"1"=>"week(s)","2"=>"month(s)"}
$formula={"1"=>"Loan Amount","2"=>"LoanAmount+Service Charge","3"=>"Service Charge"}
$feestatus={"1"=>"Active","2"=>"InActive"}
#fee end

#Loan Account start
$loan_select_client="Create Loan Account - Select Member"
$loan_select_loan_prd="Create Loan account -  Enter Loan account information"
$loan_enter_loan_data="Loan Product Summary"
$loan_loan_instance="Please select the Loan instance name"
$loan_no_of_installments="Please specify Number of installments"
$loan_interest_rate="Please specify rate"
$loan_grace_priod_duration="Grace period duration should be less than the total number of installments"
$loan_disbursal_date="Please specify Disbursal Date"
$loan_ammount="Please specify valid amount"
$loan_valid_installmets="Please specify valid No. of installments"
$loan_valid_ammount="Please specify valid amount"
$loan_valid_interest="Please specify valid Interest rate"
$loan_installments_review="Create Loan account -  Review installments"
$loan_account_preview="Create Loan account -  Preview Loan account information"
$loan_create_success="You have successfully created a new Loan account"
$loan_change_log="View change log"
$loan_preview_from_view="Create Loan account -  Preview Loan account information"
$Bad_Standing_text="Active in Bad Standing"
$Good_Standing_text="Active in Good Standing"
#Loan Account End
#group out fo center start
$select_branch_message="Create new Group - Choose Branch Office"
$group_details="Group details"
$g_name="Name"
$l_officer="Loan officer"
$m_schedule="Meeting schedule"
$trained="Trained"
$trained_on="Trained on"
$e_id="External Id"
$address_gc="Address"
$add_information="Additional information"
$adm_fee="Administrative set fees"
$add_fee="Additional fees"
#group out fo center end

#SavingProduct Start
$savingproduct_created_msg = "You have successfully added a new Margin Money Product"
$savingproduct_view_detail_msg = "View Margin Money Product details now"
$create_savingproduct_msg = "Define a new Margin Money Product"

$review_savingproduct_info = "Add a new Margin Money Product - Review & submit"
$new_savingproduct_link = "Define new Margin Money product"
$view_savingproduct_link = "View Margin Money products"
$new_savingproduct_page_msg = "Add a new Margin Money Product - Enter Margin Money Product information"
$edit_savingproduct_link = "Edit Margin Money product information"

# Validation messages for new SavingProduct
$prd_inst_name_msg = "Please specify the Product instance name."
$shortname_msg = "Please specify the Short name."
$prd_category_msg = "Please select a Product category."
$savingproduct_appl_for_msg = "Please select a Applicable for."
$savingproduct_type_of_diposit_msg = "Please select a Type of deposits."
$savingproduct_interestrate_msg = "Please specify the Interest rate."
$savingproduct_balance_interest_msg = "Please select a Balance used for Interest calculation."
$savingproduct_time_interest_msg= "Please specify the Time period for interest calculation."
$savingproduct_frequency_interest_msg = "Please specify the Frequency of interest posting to accounts."
$savingproduct_glcode_deposit_msg = "Please select a GL code for deposits."
$savingproduct_glcode_interest_msg = "Please select a GL code for interest."
#end of  Validation messages for new SavingProduct

#Error message for field validation Start
$savingproduct_change_startdate_msg = "The start date cannot be changed. Either the product is active or the date specified is invalid."
$savingproduct_valid_interestrate_msg = "Please enter a valid interest rate. Interest rate should be between 1 and 100."
$savingproduct_man_depositamount_msg = "Please specify a value greater than zero for Mandatory amount for deposit."

$savingproduct_valid_max_amount_per_withdrawal_msg = "Max amount per withdrawal is greater than the allowable size."
$savingproduct_valid_depositamount_msg = "Mandatory amount for deposit is greater than the allowable size."
$savingproduct_valid_min_balance_interestrate_msg = "Min balance required for interest rate calculation is greater than the allowable size."

$savings_prd_max_length="The length of Product instance name can not be greater than 50 character(s)."
$savings_desc_max_length="The length of Category Description can not be greater than 200 character(s)."
$savings_max_mandatory_amt="Mandatory amount for deposit is greater than the allowable size."
$savings_max_voluntary_amt="Voluntary amount for deposit is greater than the allowable size." 
$savings_max_amtper_withdrawl="Mandatory amount for deposit is greater than the allowable size."
$savings_min_balance="Min balance required for interest rate calculation is greater than the allowable size."
$savings_valid_int_rate="Please specify valid interest rate."
$savings_shortname_msg="Please do not specify [space] for Short name."
$savings_amount_applies_to="Please select a Amount Applies to."
$savings_dupl_short_name_message="The short name already exists in the application. Please specify a different Name."
$savings_dupl_inst_name_message="The Product instance name already exists in the application. Please specify a different Name."
#Error message for field validation End
#SavingProduct End
#labels 
$loan="loan"
$savings="savings"