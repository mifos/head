# This class call all the testcases from the User_Test_Cases class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/LoanProduct/LoanProduct_Test_Cases'
require 'modules/common/TestClass'

class LoanProduct_Main
  loan_obj = LoanProduct_Test_Cases.new
  loan_obj.mifos_login
  loan_obj.Click_Admin_page
  loan_obj.Check_LoanProduct_Links
  loan_obj.Check_New_LoanProduct_cancel
  
   filename = File.expand_path(File.dirname($PROGRAM_NAME))+"/data/Loan_Data.xls"
  
  #Check for mandatory field validation messages  
  #This section deals with the all validation message and error handling 
  loan_obj.open(filename, 2)
  #count = 0
  rowid = -1    
  while(rowid < $maxrow * $maxcol - 1)
  loan_obj.read_product_values(rowid)
  loan_obj.check_validation_error(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Description,loan_obj.Prd_category,loan_obj.Start_date,\
                                 loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
                                  loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
                                  loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
                                  loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)  

  loan_obj.Click_Admin_page
  rowid+=$maxcol
  end
  loan_obj.open(filename, 1)

    rowid = -1
    
   while(rowid < $maxrow * $maxcol - 1)
    loan_obj.read_product_values(rowid)
    loan_obj.Click_Admin_page
    $ie.link(:text, $new_loanproduct_link).click      
    loan_obj.Check_New_LoanProduct
    loan_obj.Create_New_LoanProduct(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Prd_category,loan_obj.Start_date,\
                                    loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
                                    loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
                                    loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
                                    loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)
        
    loan_obj.check_editdata_onpreview(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Prd_category,loan_obj.Start_date,\
                                      loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
                                      loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
                                      loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
                                      loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)
       
    loan_obj.check_view_loanproduct(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Prd_category,loan_obj.Start_date,\
                                    loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
                                    loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
                                    loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
                                    loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)
  
    loan_obj.check_status(loan_obj.Prd_inst_name)      

  rowid+=$maxcol
  end 
  loan_obj.mifos_logout()
end
