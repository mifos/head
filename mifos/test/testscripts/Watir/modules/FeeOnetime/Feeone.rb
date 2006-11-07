#this class calls all the testcases from the FeeOntime class
require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/assertions'

#include module watir
include Watir
#require 'modules/FeeOnetime/FeeOnetime'
class FeeOnetime < TestClass
  
  #pick labels from the properties file and load into a hash
  def read_from_properties()
    begin
      @accountprop=load_properties("modules/propertis/accountsUIResources.properties")
      @customerprop=load_properties("modules/propertis/CustomerSearchUIResources.properties")
      @loanprop=load_properties("modules/propertis/LoanUIResources.properties")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #read values from hash into variables
  def read_from_hash()
    begin
      #buttons
      @search_button=@accountprop['accounts.search']
      @submit_button=@accountprop['accounts.submit']
      @cancel_button=@accountprop['accounts.cancel']
      #labels/strings
      @search_mandatory_msg=@customerprop['errors.namemandatory']
      #links
      @applycharges_link=@accountprop['accounts.apply_charges']
      #error message
      @charge_type_error_msg=string_replace_message(@accountprop['errors.mandatory'],"{0}",@accountprop['account.chargetype'])
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #connecting to database
  def data_connection(feetype)
    begin
      name_login=$validname
      @@fee_type=feetype
      dbquery("select personnel_id from personnel where login_name="+"'"+name_login+"'")
      @@personnel_id=dbresult[0]
      #dbquery("select account_id,global_account_num from account where account_type_id=1 and account_state_id < 5 and personnel_id="+@@personnel_id)
      dbquery("select account_id,global_account_num from account where account_type_id=1 and account_state_id in (3,5)")
      @@account_num=dbresult[0]
      @@global_account_num=dbresult[1]
      dbquery("SELECT fees.fee_name,fees.rate,fees.fee_amount,fees.fee_id,fees.discriminator FROM fees,fee_frequency where fee_frequency.fee_id=fees.fee_id and fee_frequency.fee_frequencytype_id=2  and fee_frequency.frequency_payment_id ="+@@fee_type+" and fees.status=1 and fees.category_id=5")
      @@fee_name=dbresult[0]
      @@feerate=dbresult[1]
      @@feeamnt=dbresult[2]
      @@fee_id=dbresult[3]
      discriminator=dbresult[4]
      if(discriminator.to_s=="AMOUNT") then
        @@fee_ammount=@@feeamnt.to_f
      elsif(discriminator.to_s=="RATE")
        @@fee_ammount=@@feerate.to_f
      end
      dbquery("SELECT A1.PRINCIPAL, A1.INTEREST, A1.PENALTY, A1.MISC_FEES, (SELECT SUM(A3.AMOUNT) FROM loan_fee_schedule A3, ACCOUNT_FEES A2 WHERE (A2.ACCOUNT_FEE_ID = A3.ACCOUNT_FEE_ID) AND A2.ACCOUNT_ID ="+@@account_num+" AND INSTALLMENT_ID = 1)'FEES' FROM loan_schedule A1 WHERE (A1.INSTALLMENT_ID = 1) AND (A1.ACCOUNT_ID ="+@@account_num+")")
      @@principal=dbresult[0]
      @@interest=dbresult[1]
      @@penalty=dbresult[2]
      @@misc_fee=dbresult[3]
      @@fee_added=dbresult[4]
      @@total_ammount=Float(@@principal.to_f) +Float(@@interest.to_f) +Float(@@penalty.to_f) + Float(@@misc_fee.to_f) +Float(@@fee_added.to_f)
    rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  def read_fee_values(rowid,sheetid)
    begin
      if sheetid==1 then
        @feetype=arrval[rowid+=1].to_i.to_s
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def Feetype()
    @feetype
  end
  
  #login to Mifos
  def fee_login()
    begin
      start
      login($validname,$validpwd)
      db_connect()
    rescue =>excp
      quit_on_error(excp)
    end  
  end
  
  #Mandatory check in search page
  def man_search_account()
    begin
      $ie.button(:value,@search_button).click
      assert_on_page(@search_mandatory_msg)
    rescue =>excp
      quit_on_error(excp)
    end  
  end
  
  #Search for loan account to add fee
  def search_account()
    begin
      
      #puts "Global"+ @@global_account_num
      $ie.text_field(:name,"searchString").set(@@global_account_num)
      #   $ie.text_field(:name,"searchNode(searchString)").set(@@global_account_num)
      $ie.button(:value,@search_button).click
      assert($ie.contains_text(@@global_account_num))
      $logger.log_results("Searching for account with global account num "+@@global_account_num,@@global_account_num,"Should display in search results","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure error while Searching for account with global account num "+@@global_account_num,@@global_account_num,"Should display in search results","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #clicking on the account link
  def click_account()
    begin
      @@Account_Num=@accountprop['accounts.Account']+" # "+@@global_account_num
      $ie.link(:text,@@Account_Num).click
      assert($ie.contains_text(@@global_account_num))
      assert($ie.link(:text,@accountprop['accounts.view_schd']).exists?())
      $logger.log_results("Loan Account page","click on account "+@@global_account_num,"should navigate to loan account details page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure error Loan Account page","click on account "+@@global_account_num,"should navigate to loan account details page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking for the apply charges link
  def check_applycharges()
    begin
      assert($ie.link(:text,@applycharges_link).exists?())
      $logger.log_results("Apply charges Link","NA","should exist","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure error for Apply charges Link","NA","Should exist","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #clicking on Apply chrages link and checking for the check_text
  def click_applycharges()
    begin
      $ie.link(:text,@applycharges_link).click
      check_text=@@global_account_num+"  -  "+@applycharges_link
      assert($ie.contains_text(check_text))
      $logger.log_results("check for text "+check_text,"click on apply charges link",check_text+" should exist on page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure error for text "+check_text,"click on apply charges link",check_text+" should exist on page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #Mandatory check in the apply charges page
  def add_fee_man()
    begin
      $ie.button(@submit_button).click
      assert_on_page(@charge_type_error_msg)
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  
  #checking cancel button functionality in apply charges page
  def click_cancel()
    begin
      $ie.button(@cancel_button).click
      assert($ie.contains_text(@loanprop['loan.performance_history']))
      $logger.log_results("Cancel Button","click on cancel button ","back to Details page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure error Cancel Button","click on cancel button","back to Details page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #adding one time fee to the Loan account
  def add_fee()
    begin
      click_applycharges()
      $ie.select_list(:name,"chargeType").select_value(@@fee_id)
      $ie.button(@submit_button).click
      assert($ie.contains_text(@loanprop['loan.performance_history']))
      table_obj=$ie.table(:index,17)
      assert_equal(table_obj[2][2].text,@@fee_name+" applied")
      $logger.log_results("Fee ",@@fee_name,@@fee_name+" applied"+ " should appear in details page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure error for Fee",@@fee_name,@@fee_name+" applied"+" should appear in details page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # getting data from database
  def get_data()
    begin
      dbquery("SELECT A1.PRINCIPAL, A1.INTEREST, A1.PENALTY, A1.MISC_FEES, (SELECT SUM(A3.AMOUNT) FROM loan_fee_schedule A3, ACCOUNT_FEES A2 WHERE (A2.ACCOUNT_FEE_ID = A3.ACCOUNT_FEE_ID) AND A2.ACCOUNT_ID ="+@@account_num+" AND INSTALLMENT_ID = 1)'FEES' FROM loan_schedule A1 WHERE (A1.INSTALLMENT_ID = 1) AND (A1.ACCOUNT_ID ="+@@account_num+")")
      n_principal=dbresult[0]
      n_interest=dbresult[1]
      n_penalty=dbresult[2]
      n_misc_fee=dbresult[3]
      n_fee_added=dbresult[4]
      @@n_total_ammount=Float(n_principal) + Float(n_interest) + Float(n_penalty) + Float(n_misc_fee) + Float(n_fee_added)
      @@c_total_ammount=Float(@@total_ammount) + Float(@@fee_ammount)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the data base for fee
  def db_check()
    begin
      get_data()
      if @@fee_type=="2" 
        db_check_two()
      elsif @@fee_type=="1" 
        db_check_one() 
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the repayment if fee type is upfront 
  def db_check_one()
    begin
      if @@c_total_ammount==@@n_total_ammount then
        $logger.log_results("Fee Onetime and upfront","Should add to DB","added","Passed")
      else
        $logger.log_results("Fee Onetime and upfront","Should add to DB","added","Failed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the repayment if fee type is Time of disbursement
  def db_check_two()
    begin
      if @@c_total_ammount!=@@n_total_ammount then
        $logger.log_results("Fee Onetime and time of first loan repayment","Should add to DB","added","Passed")
      else
        $logger.log_results("Fee Onetime and time of first loan repayment","Should add to DB","added","Failed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end

end  

class Feeone
  feeobj=FeeOnetime.new
  feeobj.read_from_properties
  feeobj.read_from_hash
  feeobj.fee_login
  feeobj.man_search_account
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  feeobj.open(filename,1)
  rowid=-1
  while(rowid<$maxrow*$maxcol-1)
    feeobj.read_fee_values(rowid,1)
    feeobj.data_connection(feeobj.Feetype)
    feeobj.search_account()
    feeobj.click_account
    feeobj.check_applycharges
    feeobj.click_applycharges
    feeobj.add_fee_man
    feeobj.click_cancel
    feeobj.add_fee
    feeobj.db_check
    rowid+=$maxcol
  end
  feeobj.mifos_logout()
end
