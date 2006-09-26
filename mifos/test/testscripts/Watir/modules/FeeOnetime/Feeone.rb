#this class calls all the testcases from the FeeOntime class
require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/ui/console/testrunner'
require 'test/unit/assertions'

include Watir
#require 'modules/FeeOnetime/FeeOnetime'
class FeeOnetime < TestClass
  #connecting to database
  def data_connection(feetype)
		name_login=$validname
		@@fee_type=feetype
		dbquery("select personnel_id from personnel where login_name="+"'"+name_login+"'")
		@@personnel_id=dbresult[0]
		#dbquery("select account_id,global_account_num from account where account_type_id=1 and account_state_id < 5 and personnel_id="+@@personnel_id)
         dbquery("select account_id,global_account_num from account where account_type_id=1 and account_state_id < 5")
		@@account_num=dbresult[0]
		@@global_account_num=dbresult[1]
		dbquery("SELECT fees.fee_name,fees.rate_or_amount,fees.fee_id FROM fees,fee_frequency where fee_frequency.fee_id=fees.fee_id and fee_frequency.fee_frequencytype_id=2  and fee_frequency.frequency_payment_id ="+@@fee_type+" and fees.status=1 and fees.category_id=5")
		@@fee_name=dbresult[0]
		@@fee_ammount=dbresult[1]
		@@fee_id=dbresult[2]
		dbquery("SELECT A1.PRINCIPAL, A1.INTEREST, A1.PENALTY, A1.MISC_FEES, (SELECT SUM(A3.AMOUNT) FROM loan_fee_schedule A3, ACCOUNT_FEES A2 WHERE (A2.ACCOUNT_FEE_ID = A3.ACCOUNT_FEE_ID) AND A2.ACCOUNT_ID ="+@@account_num+" AND INSTALLMENT_ID = 1)'FEES' FROM loan_schedule A1 WHERE (A1.INSTALLMENT_ID = 1) AND (A1.ACCOUNT_ID ="+@@account_num+")")
		@@principal=dbresult[0]
		@@interest=dbresult[1]
		@@penalty=dbresult[2]
		@@misc_fee=dbresult[3]
		@@fee_added=dbresult[4]
		@@total_ammount=Float(@@principal.to_f) +Float(@@interest.to_f) +Float(@@penalty.to_f) + Float(@@misc_fee.to_f) +Float(@@fee_added.to_f)
  end 
  
  def read_fee_values(rowid,sheetid)
    if sheetid==1 then
      @feetype=arrval[rowid+=1].to_i.to_s
    end
  end
  def Feetype()
    @feetype
  end
  #login to Mifos
  def fee_login()
        start
		login($validname,$validpwd)
		db_connect()
  end
  #Mandatory check in search page
  def man_search_account()
    begin
      $ie.button(:value,"Search").click
      assert($ie.contains_text("Please specify the Name, System ID or Account Number to be searched for in the application"))
      $logger.log_results("Mandatory Error","Text","Display","Passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Error","Text","Display","Failed")       
    end
  end
  #Search for loan account to add fee
  def search_account()
    begin
      
      #puts "Global"+ @@global_account_num
      $ie.text_field(:name,"searchNode(searchString)").set(@@global_account_num)
      $ie.button(:value,"Search").click
      assert($ie.contains_text(@@global_account_num))
      $logger.log_results("Searching","Account","Display","Passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Searching","Account","Display","Failed")
    end
  end
  #clicking on the account link
  def click_account()
    begin
    @@Account_Num="Account # "+@@global_account_num
    $ie.link(:text,@@Account_Num).click
    assert($ie.contains_text(@@global_account_num))
    $logger.log_results("Loan Account page","Open","opening","Passed")
    rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("Loan Account page","Open","opening","Failed")
    end
  end
  #checking for the apply charges link
  def check_applycharges()
    begin
    assert($ie.contains_text("Apply charges"))
    $logger.log_results("Apply charges Link","Enabled","Enabled","Passed")
    rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("Apply charges Link","Enabled","Enabled","Failed")
    end
  end
  #clicking on Apply chrages link and checking for the check_text
  def click_applycharges()
    begin
    $ie.link(:text,"Apply charges").click
    check_text=@@global_account_num+"  -  Apply charges"
    assert($ie.contains_text(check_text))
    $logger.log_results("Text",check_text,check_text,"Passed")
    rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("Text",check_text,"Not Matching","Failed")
    end
  end
  #Mandatory check in the apply charges page
  def add_fee_man()
    begin
      $ie.button("Submit").click
      assert($ie.contains_text("Please enter the value for charge type"))
      $logger.log_results("Mandatory Check","Check","Checked","Passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check","Check","Checked","Failed")
    end
  end
  #checking cancel button functionality in apply charges page
  def click_cancel()
    begin
      $ie.button("Cancel").click
      assert($ie.contains_text("Performance history"))
      $logger.log_results("Cancel Button","Details page","Details page","Passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Cancel Button","Details page","Details page","Failed")
    end
  end
  #adding one time fee to the Loan account
  def add_fee()
    begin
      click_applycharges()
      $ie.select_list(:name,"chargeType").select_value(@@fee_id)
      $ie.button("Submit").click
      assert($ie.contains_text("Performance history"))
      $logger.log_results("Fee","add","added","Passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Fee","add","added","Failed")
    end
  end
  # getting data from database
  def get_data()
    dbquery("SELECT A1.PRINCIPAL, A1.INTEREST, A1.PENALTY, A1.MISC_FEES, (SELECT SUM(A3.AMOUNT) FROM loan_fee_schedule A3, ACCOUNT_FEES A2 WHERE (A2.ACCOUNT_FEE_ID = A3.ACCOUNT_FEE_ID) AND A2.ACCOUNT_ID ="+@@account_num+" AND INSTALLMENT_ID = 1)'FEES' FROM loan_schedule A1 WHERE (A1.INSTALLMENT_ID = 1) AND (A1.ACCOUNT_ID ="+@@account_num+")")
    n_principal=dbresult[0]
	n_interest=dbresult[1]
	n_penalty=dbresult[2]
	n_misc_fee=dbresult[3]
	n_fee_added=dbresult[4]
	@@n_total_ammount=Float(n_principal) + Float(n_interest) + Float(n_penalty) + Float(n_misc_fee) + Float(n_fee_added)
	@@c_total_ammount=Float(@@total_ammount) + Float(@@fee_ammount)
  end
  #checking the data base for fee
  def db_check()
    get_data()
    if @@fee_type=="2" then
      db_check_two()
    else
      db_check_one() 
    end
  end
  #checking the repayment if fee type is upfront 
  def db_check_one()
   	if @@c_total_ammount==@@n_total_ammount then
    	 $logger.log_results("Fee Onetime","Should add to DB","added","Passed")
	else
     $logger.log_results("Fee Onetime","Should add to DB","added","Failed")
    end
  end
    #checking the repayment if fee type is Time of First Loan Repayment
  def db_check_two()
    if @@c_total_ammount!=@@n_total_ammount then
    	 $logger.log_results("Fee Onetime","Should add to DB","added","Passed")
	else
     $logger.log_results("Fee Onetime","Should add to DB","added","Failed")
    end
  end
  
 end  
 
class Feeone
  feeobj=FeeOnetime.new
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
 # $ie.link(:text,"Logout").click
   feeobj.mifos_logout()
end
