require 'watir'
require 'English'
include Watir
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/ui/console/testrunner'
require 'test/unit/assertions'
class LoanAccountCreateEdit<TestClass
  def database_connection()
        db_connect()
        dbquery("select o.search_id,o.office_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
        @@search_id=dbresult[0]
  end
  def login_loanaccount()
    start
	login($validname,$validpwd)
  end
  def link_Create_loan_account_check()
    begin
    $ie.link(:text,"Clients & Accounts").click
    assert($ie.contains_text("Create Loan Account"))
    $logger.log_results("Link Check Create Loan Account","Should display","displaying","Passed")
    rescue=>e
    $logger.log_results("Link Create Loan Account","Should display","Not displaying","Failed")
    end
  end
  def click_Create_Loan_Account()
    begin
    $ie.link(:text,"Create Loan Account").click
    assert($ie.contains_text($loan_select_client))
    $logger.log_results("Link Click Create Loan Account","Should work","working","Passed")
    rescue=>e
    $logger.log_results("Link Click Create Loan Account","Should work","not working","Failed")
    end
  end
  def select_client_query(typeid)
    dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and search_id like '"+@@search_id+"%'")
    @@office_id=dbresult[0]
    display_name=dbresult[1]
     if (@@office_id=="1") then
          get_next_data
          @@office_id=dbresult[0]
          display_name=dbresult[1]
     end     
     if typeid=="1" then
     dbquery("select customer_id,display_name,global_cust_num from customer where customer_level_id=2 and status_id=9 and branch_id="+@@office_id+" order by display_name")
     @@display_name=dbresult[1]   
     @@global_account_num=dbresult[2]
     dbquery("select prd_offering_id from prd_offering where prd_applicable_master_id=2 and prd_category_id=1 and offering_status_id=1")
     @@product_id=dbresult[0]
     elsif typeid=="2" then
     dbquery("select customer_id,display_name,global_cust_num from customer where customer_level_id=1 and status_id=3 and branch_id="+@@office_id+" order by display_name")
     @@display_name=dbresult[1]
     @@global_account_num=dbresult[2]
     dbquery("select prd_offering_id from prd_offering where prd_applicable_master_id="+typeid+" and prd_category_id=1 and offering_status_id=1")
     @@product_id=dbresult[0]
     end   
   end
   def search_client_cancel() 
    begin
      $ie.button(:value,"Cancel").click
      assert($ie.contains_text("Create Loan Account"))
      $logger.log_results("Cancel button working properly","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Cancel button not working","N/A","N/A","Failed")      
    end
   end
   def search_client() 
    begin
    $ie.link(:text,"Create Loan Account").click
   $ie.text_field(:name,"searchNode(searchString)").set(@@display_name)
    $ie.button(:value,"Search").click
    assert($ie.contains_text(@@display_name))
    $logger.log_results("Search displayed the resule","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Search Not displayed the resule","N/A","N/A","Failed")
    end
   end
   def select_client()
    begin
    client_data=@@display_name+":ID"+@@global_account_num
    $ie.link(:text,client_data).click
    assert($ie.contains_text($loan_select_loan_prd))
    $logger.log_results("Selection of client","Success","N/A","Passed")
    rescue=>e
    $logger.log_results("Selection of client","Failed","N/A","Failed")
    end
   end
   def select_loan_product_cancel()
    begin
      $ie.button(:value,"Cancel").click
      assert($ie.contains_text("Create Loan Account"))
      $logger.log_results("Cancel button working properly","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Cancel button not working","N/A","N/A","Failed")
    end
   end
   def select_loan_product()
    begin
      search_client()
      select_client()
      $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_enter_loan_data))
      $logger.log_results("Page redirected to Enter Loan Data page","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Page is not redirected to Enter Loan Data page","N/A","N/A","Failed")
    end
   end
   def check_Proposed_or_actual_disbursement_date()
    begin
    assert($ie.contains_text("Proposed/actual disbursement date"))
    $logger.log_results("Lable Proposed/actual disbursement date","Displaying","Should not display","Failed")
    rescue=>e
    $logger.log_results("Lable Proposed/actual disbursement date","Not Displaying","Should not display","Passed")
    end
   end
   def check_Interest_Calculation_Rule_For_Early_or_Late_Payments()
   begin
    assert($ie.contains_text("Interest Calculation Rule For Early/Late Payments"))
    $logger.log_results("Lable Interest Calculation Rule For Early/Late Payments","Displaying","Should not display","Failed")
    rescue=>e
    $logger.log_results("Lable Interest Calculation Rule For Early/Late Payments","Not Displaying","Should not display","Passed")
   end
   end
   
   def check_all_mandatory()
    begin
      $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
      $ie.text_field(:name,"loanAmount").set("")
      $ie.text_field(:name,"interestRateAmount").set("")
      $ie.text_field(:name,"noOfInstallments").set("")
      $ie.text_field(:name,"disbursementDateDD").set("")
      $ie.text_field(:name,"disbursementDateMM").set("")
      $ie.text_field(:name,"disbursementDateYY").set("")
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_loan_instance))and assert($ie.contains_text($loan_no_of_installments))and //
      assert($ie.contains_text($loan_interest_rate)) and assert($ie.contains_text($loan_grace_priod_duration))and//
      assert($ie.contains_text($loan_disbursal_date)) and assert($ie.contains_text($loan_ammount))
      $logger.log_results("All Mandatory Checks","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("All Mandatory Checks","N/A","N/A","Failed")
    end
   end
   def mandatory_with_prodname()
    begin
    $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
    $ie.text_field(:name,"loanAmount").set("")
    $ie.text_field(:name,"interestRateAmount").set("")
    $ie.text_field(:name,"noOfInstallments").set("")
    $ie.text_field(:name,"disbursementDateDD").set("")
    $ie.text_field(:name,"disbursementDateMM").set("")
    $ie.text_field(:name,"disbursementDateYY").set("")
    $ie.button(:value,"Continue").click
    assert($ie.contains_text($loan_no_of_installments))and //
    assert($ie.contains_text($loan_interest_rate)) and assert($ie.contains_text($loan_grace_priod_duration))and//
    assert($ie.contains_text($loan_disbursal_date)) and assert($ie.contains_text($loan_ammount))
    $logger.log_results("Mandatory Check with Loan Instance","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Mandatory Check with Loan Instance","N/A","N/A","Failed") 
    end
   end
   def mandatory_with_prodname_no_of_installments()
    begin
    dbquery("SELECT max_loan_amnt,def_interest_rate,def_no_installments FROM loan_offering where prd_offering_id="+@@product_id)
    @@max_loan_amount=dbresult[0]
    @@default_interest_rate=dbresult[1]
    @@default_no_installments=dbresult[2]
    $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
    $ie.text_field(:name,"noOfInstallments").set(@@default_no_installments)
    $ie.button(:value,"Continue").click
    assert($ie.contains_text($loan_interest_rate)) and assert($ie.contains_text($loan_grace_priod_duration))and//
    assert($ie.contains_text($loan_disbursal_date)) and assert($ie.contains_text($loan_ammount))
    $logger.log_results("Mandatory Check with Loan Instance and no of installments","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Mandatory Check with Loan Instance and no of installments","N/A","N/A","Failed") 
    end
   end
   def mandatory_with_prodname_no_of_installments_interest_rate()
    begin
    $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
    $ie.text_field(:name,"noOfInstallments").set(@@default_no_installments)
    $ie.text_field(:name,"interestRateAmount").set(@@default_interest_rate)
    $ie.button(:value,"Continue").click
    assert($ie.contains_text($loan_disbursal_date)) and assert($ie.contains_text($loan_ammount))
    $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","N/A","N/A","Failed") 
    end
   end
   def mandatory_excxept_disbursaldate()
    begin
    $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
    $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
    $ie.text_field(:name,"noOfInstallments").set(@@default_no_installments)
    $ie.text_field(:name,"interestRateAmount").set(@@default_interest_rate)
    $ie.text_field(:name,"loanAmount").set(@@max_loan_amount)
    $ie.text_field(:name,"disbursementDateDD").set("")
    $ie.text_field(:name,"disbursementDateMM").set("")
    $ie.text_field(:name,"disbursementDateYY").set("")
    $ie.button(:value,"Continue").click
    assert($ie.contains_text($loan_disbursal_date))
    $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","N/A","N/A","Failed") 
    end
   end
   def validate_no_of_installments_greater()
      begin
      $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
      $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
      dbquery("select max_no_installments from loan_offering where prd_offering_id="+@@product_id)
      max_no_installments=dbresult[0]
      #puts "@@Max of installments " + max_no_installments.to_s
      installments=(max_no_installments).to_i+1
      #puts "@@no of installments " + installments.to_s
      $ie.text_field(:name,"noOfInstallments").set(installments.to_s)
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_valid_installmets))
      $logger.log_results("Checking Greater Installmensts","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Not Checking Greater Installmensts","N/A","N/A","Failed")
      end
   end
   def validate_no_of_installments_lesser()
      begin
      dbquery("select min_no_installments from loan_offering where prd_offering_id="+@@product_id)
      min_no_installments=dbresult[0]
      installments=(min_no_installments).to_i-1
      $ie.text_field(:name,"noOfInstallments").set(installments.to_s)
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_valid_installmets))
      $logger.log_results("Checking Lesser Installmensts","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Not Checking Lesser Installmensts","N/A","N/A","Failed")
      end
   end
   def validate_ammount_greater()
      begin
      $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
      $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
      dbquery("select max_loan_amnt from loan_offering where prd_offering_id="+@@product_id)
      max_loan_amnt=dbresult[0]
      #puts "@@Max Loan ammount " + max_loan_amnt.to_s
      loanammount=max_loan_amnt.to_i+2
      #puts "@@laon Loan ammount " + loanammount.to_s
      $ie.text_field(:name,"loanAmount").set(loanammount.to_s)
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_valid_ammount))
      $logger.log_results("Checking Greater amount","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Not Checking Greater amount","N/A","N/A","Failed")
      end
   end
   def validate_ammount_zero()
   begin
    $ie.text_field(:name,"loanAmount").set("0")
    $ie.button(:value,"Continue").click
    assert($ie.contains_text($loan_valid_ammount))
    $logger.log_results("Checking Zero amount","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Not Checking Zero amount","N/A","N/A","Failed")
    end
   end
   def validate_ammount_decimal()
    begin
    $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
    dbquery("select min_loan_amount from loan_offering where prd_offering_id="+@@product_id)
    min_loan_amnt=dbresult[0]
    amount=Float(min_loan_amt)+Float(0.222)
    amount_check=Float(min_loan_amt)+Float(0.2)
    puts "@@loan amount decimal"+amount
    $ie.text_field(:name,"loanAmount").set(amount.to_s)
    $ie.button(:value,"Continue").click
    value="Loan Amount  "+amount_check
    puts "Ammount value"+value
    assert($ie.contains_text(value))
    $logger.log_results("Checking Decimals in amount","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Not Checking Decimals in amount","N/A","N/A","Failed")
   end
  end
    def validate_ammount_lesser()
      begin
      $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
      $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
      dbquery("select min_loan_amount from loan_offering where prd_offering_id="+@@product_id)
      min_loan_amnt=dbresult[0]
      #puts "@@min loan ammount" + min_loan_amnt.to_s
      amount=min_loan_amnt.to_i-1
      #puts "@@loan ammount" + amount.to_s
      $ie.text_field(:name,"loanAmount").set(amount.to_s)
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_valid_ammount))
      $logger.log_results("Checking lesser amount","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Not Checking Lesser amount","N/A","N/A","Failed")
      end
   end
   def validate_interest_greater()
      begin
      dbquery("select max_interest_rate from loan_offering where prd_offering_id="+@@product_id)
      max_interest_rate=dbresult[0]
      interest=max_interest_rate.to_i+1
      $ie.text_field(:name,"interestRateAmount").set(interest.to_s)
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_valid_interest))
      $logger.log_results("Checking greater interest","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Not Checking greater interest","N/A","N/A","Failed")
      end
   end
   def validate_interest_lesser()
      begin
      dbquery("select min_interest_rate from loan_offering where prd_offering_id="+@@product_id)
      min_interest_rate=dbresult[0]
      interest=min_interest_rate.to_i-1
      $ie.text_field(:name,"interestRateAmount").set(interest.to_s)
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_valid_interest))
      $logger.log_results("Checking lesser interest","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Not Checking lesser interest","N/A","N/A","Failed")
      end
   end
  def fee_select_one_by_one()
  $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
  $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@product_id)
  search_res=$dbh.real_query("SELECT fee_id FROM fees where category_id=5 and status=1 and fee_id not in(select fee_id from prd_offering_fees where prd_offering_id="+@@product_id+" )")
  dbresult1=search_res.fetch_row.to_a
  row1=search_res.num_rows()
  rowf=0
  number=3
  if row1=="0" then
  $looger.log_results("No Fess created for the group","","","Passed")
  elsif row1 < number then
    while (rowf < row1)
    fee_id=dbresult1[0]
    $ie.select_list(:name,"accountFees["+String(rowf)+"].fees.feeId").select_value(fee_id)
    dbresult1=search_res.fetch_row.to_a
    rowf+=1
    end
  else
    while(rowf < number)
    fee_id=dbresult1[0]
    $ie.select_list(:name,"accountFees["+String(rowf)+"].fees.feeId").select_value(fee_id)
    dbresult1=search_res.fetch_row.to_a
    rowf+=1
   end
  end
  end
  def click_continue()
    begin
      $ie.button(:value,"Continue").click
      assert($ie.contains_text($loan_installments_review))
      $logger.log_results("Page Redirected to Loan account Review page","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Page Not Redirected to Loan account Review page","N/A","N/A","Failed")
    end
  end
  def click_preview()
    begin
      $ie.button(:value,"Preview").click
      assert($ie.contains_text($loan_account_preview))
      $logger.log_results("Page redirect to review loan account","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Page not redirect to review loan account","N/A","N/A","Failed")
    end
  end
  def edit_loan_from_preview()
    begin
    $ie.button(:value,"Edit  account information").click
    assert($ie.contains_text($loan_select_loan_prd))
    $logger.log_results("Page redirect to Create loan account","N/A","N/A","Passed")
    fee_select_one_by_one
    click_continue()
    click_preview()
    rescue=>e
    $logger.log_results("Page not redirect to Create loan account","N/A","N/A","Failed")
    end
  end
  def click_submit(status)
    begin
    $ie.button(:value,status).click
    assert($ie.contains_text($loan_create_success))
    $logger.log_results("Loan account Creation Success","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Loan account Creation Success","N/A","N/A","Failed")
    end
  end
  def view_client_details_link_check()
    begin
    assert($ie.contains_text("View Loan account details now"))
    $logger.log_results("Link View loan account details now","Should Exist","Existed","Passed")
    rescue=>e
    $logger.log_results("Link View loan account details now","Should Exist","Not Existed","failed")
    end
  end
  def view_savings_account_creation_link()
    begin
      assert($ie.contains_text("Open a new Margin Money account"))
      $logger.log_results("Link Open a new savings account","Should Exist","Existed","Passed")
      rescue=>e
      $logger.log_results("Link Open a new savings account","Should Exist","Not Existed","Failed")
    end
  end 
  def click_savings_account_link()
    begin
      $ie.link(:text,"Open a new Margin Money account").click
      assert($ie.contains_text($savings_select_mmaccount))
      $logger.log_results("Link Open a new savings account","Should work","Working","passed")
      $ie.button(:value,"Cancel").click
      rescue=>e
      $logger.log_results("Link Open a new savings account","Should work","not Working","Failed")
    end
  end
  def view_client_details_link_click()
    begin
      $ie.link(:text,"View Loan account details now").click
      assert($ie.contains_text("Account summary"))and assert($ie.contains_text("Transactions"))
      $logger.log_results("Page entered in to account details page","N/A","N/A","Passed")
      rescue=>e
      $logger.log_results("Page entered in to account details page","N/A","N/A","Failed")
    end
  end
  def edit_account_status()
    dbquery("select max(account_id) from account")
    account_id=dbresult[0]
    dbquery("select account_state_id from account where account_id="+account_id)
    statusid=dbresult[0]
    if statusid=="1" then
    change_status_pending()
    elsif statusid=="2" then
    change_status_approved()
    end
  end
  def change_status_pending()
    begin
      $ie.link(:text,"Edit account status").click
      $ie.radio(:name,"newStatusId","2").set
      $ie.text_field(:name,"notes.comment").set("aaaa")
      $ie.button(:value,"Preview")
      $ie.button(:value,"Submit").click
      assert($ie.contains_text("Performance history "))
      view_status_history_pending()
      $logger.log_results("Status changed to Pending","NA","NA","passed") 
      rescue=>e
      $logger.log_results("Status changed to pending","NA","NA","failed") 
    end
  end
  def change_status_active()
    begin
      $ie.link(:text,"Edit account status").click
      $ie.radio(:name,"newStatusId","3").set
      $ie.text_field(:name,"notes.comment").set("aaaa")
      $ie.button(:value,"Preview")
      $ie.button(:value,"Submit").click
      assert($ie.contains_text("Performance history "))
      view_status_history_active()
      $logger.log_results("Status changed to Active","NA","NA","passed") 
      rescue=>e
      $logger.log_results("Status changed to Active","NA","NA","failed") 
    end
  end
  def view_status_history_pending()
  begin
    $ie.link(:text,"View status history" ).click
    assert($ie.contains_text("Status"))and assert($ie.contains_text("Application Pending Approval")) and assert($ie.contains_text("Partial Application"))
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed")    
    rescue=>e
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
  end
  end
  def  view_status_history_active()
  begin
    $ie.link(:text,"View status history").click
    assert($ie.contains_text("Status"))and assert($ie.contains_text("Application Pending Approval")) and assert($ie.contains_text("Application Approved"))
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed")    
    rescue=>e
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
  end
  end
  def view_change_log()
    begin
    $ie.link(:text,$loan_change_log).click
    assert($ie.contains_text("Change Log"))
    $logger.log_results("View Change Log link is working","N/A","N/A","Passed")
    $ie.button(:value,"Return to account details").click    
    rescue=>e
    $logger.log_results("View Change Log link is not working","N/A","N/A","Failed")        
    end
  end
  def view_repayment_schedules()
    begin
    $ie.link(:text,"View repayment schedule").click
    assert($ie.contains_text(" Repayment Schedule as of"))
    $logger.log_results("View repayment schedule link workin","N/A","N/A","Passed")
    $ie.button(:value,"Return to account details").click 
    rescue=>e
    $logger.log_results("View repayment schedule link not workin","N/A","N/A","Failed")    
    end
  end
  def view_account_activity()
    begin
    $ie.link(:text,"View all account activity").click
    assert($ie.contains_text("Account statement as of "))
    $logger.log_results("View all account activity link workin","N/A","N/A","Passed")
    $ie.button(:value,"Return to account details").click 
    rescue=>e
    $logger.log_results("View all account activity link not workin","N/A","N/A","Failed")    
    end
  end
  def edit_loan_account_data()
    begin
    $ie.link(:value,"Edit account information").click 
    $ie.select_list(:name,"businessActivityId").select_value(233)
    $ie.select_list(:name,"collateralTypeId").select_value(2)
    $ie.text_field(:name,"collateralNote").set("aaaaa")
    $ie.select_list(:name,"businessActivityId").select_value(443)
    $ie.buttton(:value,"Preview").click
    assert_on_page("Purpose of Loan: 0001-Cow Purchase")
    assert_on_page("Account Owner: "+@@display_name)
    assert($ie.contains_text($loan_preview_from_view))
    $logger.log_results("Page redirected to preview","N/A","N/A","Passed")
    $ie.button(:value,"Submit").click
    rescue=>e
    $logger.log_results("Page redirected to preview","N/A","N/A","Failed")
    end
  end
  def open_loan_account_in_active_state()
    begin
    dbquery("select a.GLOBAL_ACCOUNT_NUM from loan_account la, Account a where la.Account_ID = a.account_id and a.account_STATE_id = 5")
    if $row==0 then
    $logger.log_results("No accounts existed with active good standing state","N/A","N/A","Passed")
    else
      global_account_number=dbresult[0]
    end
      $ie.link(:text,"Clients & Accounts").click
      $ie.text_field(:name,"searchNode(searchString)").set(global_account_number)
      $ie.button("Search").click
      search_name="Account # "+global_account_number
      $ie.link(:text,search_name).click
    assert($ie.contains_text("Performance History"))
      $logger.log_results("Account Details page","Open","opened","passed")
    rescue=>e
    $logger.log_results("Account Details page","Open","not opened","failed")
    
  end
  end
  def edit_loan_account_with_purpose_of_loan()
  begin
  $ie.link(:text,"Edit account information").click
  $ie.select_list(:name,"businessActivityId").select_value(231)
  $ie.button(:value,"Preview").click
  assert($ie.contains_text("Preview Loan account information"))
  $logger.log_results("Can able to select purpose of loan","N/A","N/A","Passed")
  $ie.button(:value,"Submit").click
  rescue=>e
  $logger.log_results("Can able to select purpose of loan","N/A","N/A","Failed")
  end
  end
  
  
  
  
end
