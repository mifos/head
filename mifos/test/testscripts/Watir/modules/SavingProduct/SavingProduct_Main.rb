# This class call all the testcases from the SavingProduct_Test_Cases class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/SavingProduct/SavingProduct_Test_Cases'
require 'modules/common/TestClass'

class SavingProduct_Main
  saving_obj = SavingProduct_Test_Cases.new
  saving_obj.mifos_login
  saving_obj.Click_Admin_page
  saving_obj.Check_SavingProduct_Links
  saving_obj.Check_New_SavingProduct_cancel
  saving_obj.link_check($new_savingproduct_link)
  filename = File.expand_path( File.dirname($PROGRAM_NAME))+"/data/Saving_Data.xls"
  
  #Check for mandatory field validation messages only once  
  #This section deals with the all validation message and error handling 
  saving_obj.open(filename, 2)
  count = 0
  rowid = -1
   
 
  #saving_obj.Click_Admin_page
  #saving_obj.check_field_validation_error(
  # prd_inst_name, short_name, deposit_type, deposit_amount, interest_rate, max_amount_per_withdrawal,
  # min_balance_interestrate)
  
  #Check for mandatory field validation messages only once  
   # if (0 == count ) 
    #saving_obj.Click_Admin_page
   while(rowid < $maxrow * $maxcol - 1)
    saving_obj.read_user_values(rowid)
    saving_obj.check_validation_error(saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Description,saving_obj.Prd_category, saving_obj.Start_date, saving_obj.Applicable_for,
    saving_obj.Applies_to,saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Max_amt_per_withdrawal,saving_obj.Interest_rate, saving_obj.Balance_interest_calc,saving_obj.Time_interest_calc,
    saving_obj.Interest_posting_frequency,saving_obj.Min_balance_interest_rate,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)
    
    rowid+=$maxcol
    saving_obj.link_check("Admin")
    saving_obj.link_check($new_savingproduct_link)
   end
    
#This section deals with the all validation message and error handling

  saving_obj.open(filename, 1)
  count = 0
  rowid = -1
    
 while(rowid < $maxrow * $maxcol - 1)
 saving_obj.read_user_values(rowid)
 
  saving_obj.Click_Admin_page
  saving_obj.link_check($new_savingproduct_link)
  #$ie.link(:text, $new_savingproduct_link).click
  saving_obj.Check_New_SavingProduct
  
  saving_obj.Create_New_SavingProduct(
    saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Prd_category,saving_obj.Applicable_for,saving_obj.Applies_to,
    saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Interest_rate,saving_obj.Balance_interest_calc,
    saving_obj.Time_interest_calc,saving_obj.Interest_posting_frequency,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)
        
  saving_obj.check_editdata_onpreview(
    saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Prd_category,saving_obj.Applicable_for,saving_obj.Applies_to,
    saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Interest_rate,saving_obj.Balance_interest_calc,
    saving_obj.Time_interest_calc,saving_obj.Interest_posting_frequency,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)

   
  saving_obj.check_view_savingproduct( 
     saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Prd_category,saving_obj.Applicable_for,saving_obj.Applies_to,
     saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Interest_rate,saving_obj.Balance_interest_calc,
     saving_obj.Time_interest_calc,saving_obj.Interest_posting_frequency,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)

  saving_obj.check_status(saving_obj.Prd_instance_name)      

#  if count == 0 
#      saving_obj.create_SavingProduct_duplicate_name( 
#          prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
#       time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)

#  end
#  count = 1  
rowid+=$maxcol
end 
saving_obj.mifos_logout()
end
