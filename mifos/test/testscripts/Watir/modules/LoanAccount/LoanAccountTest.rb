require 'watir'
require 'modules/common/TestClass'
require 'modules/common/inputs'
require 'modules/LoanAccount/LoanAccountCreateEdit'
class LoanAccountTest
  loanobject=LoanAccountCreateEdit.new
  loanobject.database_connection
  loanobject.login_loanaccount
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  loanobject.open(filename,1)
  rowid=-1  
  while(rowid<$maxrow*$maxcol-1)
  type=loanobject.arrval[rowid+=1]
  statusname=loanobject.arrval[rowid+=1]
  type_success=loanobject.arrval[rowid+=1]
  if statusname=="partial" then
  status="Save for later"
  elsif statusname=="pending" then
  status="Submit for approval"
  end
  if type=="group" then
  typeid="1"
  elsif type=="client" then
  typeid="2"
  end
  
  loanobject.link_Create_loan_account_check
  loanobject.click_Create_Loan_Account
  loanobject.select_client_query(typeid)
  loanobject.search_client_cancel
  loanobject.search_client 
  loanobject.select_client
  loanobject.select_loan_product_cancel
  loanobject.select_loan_product
  loanobject.check_Interest_Calculation_Rule_For_Early_or_Late_Payments
  loanobject.check_Proposed_or_actual_disbursement_date
  loanobject.check_all_mandatory
  #loanobject.mandatory_with_prodname
  loanobject.mandatory_with_prodname_no_of_installments
  loanobject.mandatory_with_prodname_no_of_installments_interest_rate
  loanobject.mandatory_excxept_disbursaldate
  loanobject.validate_ammount_greater
  loanobject.validate_ammount_lesser
  loanobject.validate_interest_greater
  loanobject.validate_interest_lesser
  loanobject.validate_ammount_zero()
  loanobject.validate_no_of_installments_greater
  loanobject.validate_no_of_installments_lesser
  loanobject.fee_select_one_by_one
  #loanobject.validate_ammount_decimal()
  loanobject.click_continue
  loanobject.click_preview
  loanobject.edit_loan_from_preview
  loanobject.click_submit(status)
  loanobject.view_client_details_link_check
  if type_success==0 then
  loanobject.view_client_details_link_click
  loanobject.edit_account_status
  #commented this as this link does not exist currently
  #loanobject.view_change_log 
  loanobject.view_repayment_schedules
  loanobject.view_account_activity
  loanobject.edit_loan_account_data
  elsif type_success==1 then
  loanobject.view_savings_account_creation_link
  loanobject.click_savings_account_link
  end
 end
 loanobject.mifos_logout()
end
