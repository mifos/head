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
  #get label for loan from db
  def get_labels_from_db
    begin
      dbquery("select lookup_value from lookup_value_locale where lookup_id=54")
      @@loan_label=dbresult[0]
      dbquery("select lookup_value from lookup_Value_locale where lookup_id=17")
      @@partial_application_label=dbresult[0]
      dbquery("select lookup_value from lookup_Value_locale where lookup_id=18")
      @@pending_application_label=dbresult[0]
      dbquery("select lookup_value from lookup_Value_locale where lookup_id=19")
      @@approved_application_label=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #read from a properties file into a hash
  def read_from_properties_file()
    begin
      @@loanprop=load_properties("modules/propertis/LoanUIResources.properties")
      @@accountprop=load_properties("modules/propertis/accountsUIResources.properties")
      @@menuprop=load_properties("modules/propertis/MenuResources.properties")      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #read from hash into variables
  def read_values_from_hash()
    begin
      #links
      @@loan_account_link=@@accountprop['accounts.create_loan_acc']
      @@loan_select_customer=@@loan_account_link+" - "+@@accountprop['accounts.SelectACustomer']
      @@view_loan_account_details=@@loanprop['loan.view_loan_acc1']+" "+@@loan_label+" "+@@loanprop['loan.view_loan_acc2']
      #msg
      @@loan_account_info=@@accountprop['accounts.create']+" "+@@loan_label+" "+ @@accountprop['accounts.account']+" -  "+@@loanprop['loan.Enter']+" "+@@loan_label+" "+@@accountprop['accounts.acc_info']
      @@loan_installments_review=@@accountprop['accounts.create']+" "+@@loan_label+" "+ @@accountprop['accounts.account']+" -  "+@@loanprop['loan.review&edit']
      @@loan_account_preview=@@accountprop['accounts.create']+" "+@@loan_label+" "+ @@accountprop['accounts.account']+" -  "+@@loanprop['loan.preview']+" "+@@loan_label+" "+@@accountprop['accounts.acc_info']
      @@loan_create_success=@@loanprop['loan.successful_creation']+" "+@@loan_label+" "+@@accountprop['accounts.account']
      @@loan_disburse_loan = @@loanprop['loan.disburseloan']
      @@loan_submit = @@loanprop['loan.submit']
      @@loan_reviewtransaction = @@loanprop['loan.reviewtransaction']
      @@loan_edit_acc_status = @@loanprop['loan.edit_acc_status']
      @@loan_installment_paid = @@loanprop['loan.instt_paid']
      @@loan_view_repayment_schedule = @@loanprop['loan.view_schd']
      @@loan_apply_charges = @@loanprop['loan.apply_charges']
      @@loan_search = @@loanprop['loan.search']
      @@loan_return_to_account_details = @@loanprop['loan.returnToAccountDetails']
      @@loan_apply_payment = @@loanprop['loan.apply_payment']
      @@loan_apply_adjustment = @@loanprop['loan.apply_adjustment']
      @@loan_installment_details = @@loanprop['loan.view_inst_details']
      @@loan_total_amount_due = @@loanprop['loan.totalAmtDue']      
      @@change_acct_status=@@menuprop['label.change']+" "+@@menuprop['label.accountSmall']+" "+@@menuprop['label.status']
      @@loan_no_search_result_found_msg=@@loanprop['errors.nosearchresults']
      @@loan_waive = @@loanprop['loan.waive']
      @@loan_view_account_activity = @@loanprop['loan.view_acc_activity']
      @@repayloan = @@loanprop['loan.repayloan']
#      puts "account link "+@@loan_account_link.to_s
#      puts "select cust "+@@loan_select_customer.to_s
#      puts "view "+@@view_loan_account_details.to_s
#      puts "loan acc info "+@@loan_account_info.to_s
#      puts "loan acc pre "+@@loan_account_preview.to_s
#      puts "loan instl "+@@loan_installments_review.to_s
#      puts "succ "+@@loan_create_success.to_s
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def load_error_messages()
    begin
      @@loan_no_of_installments=string_replace_message(@@loanprop['errors.defMinMax'],"{0}",@@loanprop['loan.no_of_inst'])
      @@loan_no_of_installments=string_replace_message(@@loan_no_of_installments,"{0}",@@loanprop['loan.no_of_inst'])
      @@loan_no_of_installments=string_replace_message(@@loan_no_of_installments,"{1}",@@min_no_installments)
      @@loan_no_of_installments=string_replace_message(@@loan_no_of_installments,"{2}",@@max_no_installments)
      @@loan_interest_rate=string_replace_message(@@loanprop['errors.defMinMax'],"{0}",@@accountprop['accounts.interest_rate'])
      @@loan_interest_rate=string_replace_message(@@loan_interest_rate,"{0}",@@accountprop['accounts.interest_rate'])
      @@loan_interest_rate=string_replace_message(@@loan_interest_rate,"{1}",@@min_interest_rate)
      @@loan_interest_rate=string_replace_message(@@loan_interest_rate,"{2}",@@max_interest_rate)
      @@loan_ammount=string_replace_message(@@loanprop['errors.defMinMax'],"{0}",@@accountprop['accounts.fee_amt'])
      @@loan_ammount=string_replace_message(@@loan_ammount,"{0}",@@accountprop['accounts.fee_amt'])
      @@loan_ammount=string_replace_message(@@loan_ammount,"{1}",@@min_loan_amount)
      @@loan_ammount=string_replace_message(@@loan_ammount,"{2}",@@max_loan_amount)
      @@loan_disbursal_date=string_replace_message(@@loanprop['errors.validandmandatory'],"{0}",@@loanprop['loan.proposed_date'])
   
      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #get values of max,amount,min amount etc from loan offering.Used in error messages at loan creation level
  def get_values_from_loanoffering()
    begin
      dbquery("select MIN_LOAN_AMOUNT,MAX_LOAN_AMNT,MAX_INTEREST_RATE,MIN_INTEREST_RATE,MAX_NO_INSTALLMENTS,MIN_NO_INSTALLMENTS  from loan_offering where PRD_OFFERING_ID="+@@product_id)
      @@min_loan_amount=dbresult[0].to_f.to_s
      @@max_loan_amount=dbresult[1].to_f.to_s
      @@max_interest_rate=dbresult[2].to_f.to_s
      @@min_interest_rate=dbresult[3].to_f.to_s
      @@max_no_installments=dbresult[4].to_f.to_s
      @@min_no_installments=dbresult[5].to_f.to_s
      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def database_connection()
    begin
      db_connect()
      dbquery("select o.search_id,o.office_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
      @@search_id=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def login_loanaccount()
    begin
      start
      login($validname,$validpwd)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checks for create loan account link
  def link_Create_loan_account_check()
    begin
      $ie.link(:text,"Clients & Accounts").click
      assert($ie.link(:text,@@loan_account_link).exists?())
      $logger.log_results("Link Check Create Loan Account","Should display","displaying","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Link Create Loan Account","Should display","Not displaying","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #click on create loan account link 
  def click_Create_Loan_Account()
    begin
      $ie.link(:text,@@loan_account_link).click
      assert($ie.contains_text(@@loan_select_customer))
      $logger.log_results("Text "+@@loan_select_customer.to_s+" appears on page","click on "+@@loan_account_link.to_s+" link","select customer page of loan account","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Text "+@@loan_select_customer.to_s+" does not appear on page","click on "+@@loan_account_link.to_s+" link","select customer page of loan account","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def select_client_query(typeid)
    begin
      #query to search for an active branch office
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      display_name=dbresult[1]
      if (@@office_id=="1") then
        get_next_data
        @@office_id=dbresult[0]
        display_name=dbresult[1]
      end     
      
      if typeid=="1" then
        #query to search an active group under the branch office queried above
        dbquery("select customer_id,display_name,global_cust_num from customer where customer_level_id=2 and status_id=9 and branch_id="+@@office_id+" order by display_name")
        @@customer_id=dbresult[0]
        @@display_name=dbresult[1]   
        @@global_account_num=dbresult[2]
        #     dbquery("select prd_offering_id from prd_offering where prd_applicable_master_id= and prd_category_id=1 and offering_status_id=1")
        
        #first find the recurrence of the customer
        dbquery("select a.recurrence_id ,a.recur_after from recurrence_detail a,customer_meeting b where b.meeting_id=a.meeting_id and b.customer_id="+@@customer_id+"")
        @@customerRecurr_ID=dbresult[0]
        @@customerRecurr_after=dbresult[1]
        
        # find the prdoffering id for the corresponding group matching the meeting schedule
        dbquery("select e.recurrence_id,e.recur_after ,f.prd_offering_id from recurrence_detail e , (select a.prd_offering_id,b.prd_meeting_id from prd_offering a,prd_offering_meeting b  where a.prd_offering_id=b.prd_offering_id and a.prd_applicable_master_id=2 and a.offering_status_id=1 and a.prd_type_id=1) f where e.meeting_id=f.prd_meeting_id and e.recurrence_id="+@@customerRecurr_ID+" and (select mod(e.recur_after,"+@@customerRecurr_after+")=0)")
        @@product_id=dbresult[2]
        
      elsif typeid=="2" then
        #query to search for an active client 
        dbquery("select customer_id,display_name,global_cust_num from customer where customer_level_id=1 and status_id=3 and branch_id="+@@office_id+" order by display_name")
        @@customer_id=dbresult[0]
        @@display_name=dbresult[1]
        @@global_account_num=dbresult[2]
        
        dbquery("select a.recurrence_id ,a.recur_after from recurrence_detail a,customer_meeting b where b.meeting_id=a.meeting_id and b.customer_id="+@@customer_id+"")
        @@customerRecurr_ID=dbresult[0]
        @@customerRecurr_after=dbresult[1]
        
        #query to find list of applicable loan product offerings for the customer
        #dbquery("select prd_offering_id from prd_offering where prd_applicable_master_id="+typeid+" and prd_category_id=1 and offering_status_id=1")
        dbquery("select e.recurrence_id,e.recur_after ,f.prd_offering_id from recurrence_detail e , (select a.prd_offering_id,b.prd_meeting_id from prd_offering a,prd_offering_meeting b  where a.prd_offering_id=b.prd_offering_id and a.prd_applicable_master_id=1 and a.offering_status_id=1 and a.prd_type_id=1) f where e.meeting_id=f.prd_meeting_id and e.recurrence_id="+@@customerRecurr_ID+" and (select mod(e.recur_after,"+@@customerRecurr_after+")=0)")
        @@product_id=dbresult[2]
      end 
  
    rescue =>excp
      quit_on_error(excp)
    end 
  end
  
  #checks cancel button functionality
  def search_client_cancel() 
    begin
      $ie.button(:value,@@accountprop['accounts.cancel']).click
      assert($ie.contains_text(@@loan_account_link))
      $logger.log_results("Cancel button working properly","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Cancel button not working","N/A","N/A","Failed")      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def search_client() 
    begin
      $ie.link(:text,@@loan_account_link).click
      $ie.text_field(:name,"searchString").set(@@display_name)
      $ie.button(:value,@@loanprop['loan.search']).click
      assert($ie.contains_text(@@display_name))
      $logger.log_results("Search results contains "+@@display_name.to_s,"N/A",@@display_name.to_s+" should exist","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Search results does not contain "+@@display_name.to_s,"N/A",@@display_name.to_s+" should exist","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  
  def select_client()
    begin
      client_data=@@display_name+":ID"+@@global_account_num
      $ie.link(:text,client_data).click
      assert($ie.contains_text(@@loan_account_info))
      $logger.log_results("Selection of client","Success","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Selection of client","Failed","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checks cancel button functionality in enter loan account creation page
  def select_loan_product_cancel()
    begin
      $ie.button(:value,@@accountprop['accounts.cancel']).click
      assert($ie.contains_text(@@loan_account_link))
      $logger.log_results("Cancel button working properly","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Cancel button not working","N/A","N/A","Failed")
    rescue =>excp
    quit_on_error(excp)
    end
  end
  
  #selects loan product offering from the drop down
  def select_loan_product()
    begin
      search_client()
      select_client()
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_account_info))
      $logger.log_results("Page redirected to Enter Loan Data page","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page is not redirected to Enter Loan Data page","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def check_Proposed_or_actual_disbursement_date()
    begin
      assert($ie.contains_text(@@loanprop['loan.dis_date']))
      $logger.log_results("Label Proposed/actual disbursement date","Displaying","Should not display","Failed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Label Proposed/actual disbursement date","Not Displaying","Should not display","Passed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def check_Interest_Calculation_Rule_For_Early_or_Late_Payments()
    begin
      assert($ie.contains_text(@@loanprop['loan.interest_cal_payments']))
      $logger.log_results("Label Interest Calculation Rule For Early/Late Payments","Displaying","Should not display","Failed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Label Interest Calculation Rule For Early/Late Payments","Not Displaying","Should not display","Passed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def check_all_mandatory()
    begin
      #$ie.select_list(:name,"prdOfferingId").select_value("")
      $ie.text_field(:name,"loanAmount").set("")
      $ie.text_field(:name,"interestRate").set("")
      $ie.text_field(:name,"noOfInstallments").set("")
      $ie.text_field(:name,"disbursementDateDD").set("")
      $ie.text_field(:name,"disbursementDateMM").set("")
      $ie.text_field(:name,"disbursementDateYY").set("")
      $ie.button(:value,@@loanprop['loan.continue']).click
      #assert($ie.contains_text($loan_loan_instance))and
      assert($ie.contains_text(@@loan_no_of_installments))and \
      assert($ie.contains_text(@@loan_interest_rate)) and \
      #assert($ie.contains_text($loan_grace_priod_duration))and \
      assert($ie.contains_text(@@loan_disbursal_date)) and assert($ie.contains_text(@@loan_ammount))
      $logger.log_results("All Mandatory Checks","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("All Mandatory Checks","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def mandatory_with_prodname()
    begin
      #$ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      $ie.text_field(:name,"loanAmount").set("")
      $ie.text_field(:name,"interestRateAmount").set("")
      $ie.text_field(:name,"noOfInstallments").set("")
      $ie.text_field(:name,"disbursementDateDD").set("")
      $ie.text_field(:name,"disbursementDateMM").set("")
      $ie.text_field(:name,"disbursementDateYY").set("")
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_no_of_installments))and \
      assert($ie.contains_text(@@loan_interest_rate)) and \
      #assert($ie.contains_text($loan_grace_priod_duration))and \
      assert($ie.contains_text(@@loan_disbursal_date)) and assert($ie.contains_text(@@loan_ammount))
      $logger.log_results("Mandatory Check with Loan Instance","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check with Loan Instance","N/A","N/A","Failed") 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def mandatory_with_prodname_no_of_installments()
    begin
      dbquery("SELECT max_loan_amnt,def_interest_rate,def_no_installments FROM loan_offering where prd_offering_id="+@@product_id)
      @@max_loan_amount=dbresult[0]
      @@default_interest_rate=dbresult[1]
      @@default_no_installments=dbresult[2]
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      $ie.text_field(:name,"noOfInstallments").set(@@default_no_installments)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_interest_rate)) and \
      #assert($ie.contains_text($loan_grace_priod_duration))and\
      assert($ie.contains_text(@@loan_disbursal_date)) and assert($ie.contains_text(@@loan_ammount))
      $logger.log_results("Mandatory Check with Loan Instance and no of installments","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check with Loan Instance and no of installments","N/A","N/A","Failed") 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #this is currently failing 
  def mandatory_with_prodname_no_of_installments_interest_rate()
    begin
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      $ie.text_field(:name,"noOfInstallments").set(@@default_no_installments)
      $ie.text_field(:name,"interestRate").set(@@default_interest_rate)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_disbursal_date)) and assert($ie.contains_text(@@loan_ammount))
      $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","NA","NA","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","NA","NA","Failed") 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
    #this is currently failing 
  def mandatory_excxept_disbursaldate()
    begin
      #$ie.select_list(:name,"prdOfferingId").select_value("")
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      $ie.text_field(:name,"noOfInstallments").set(@@default_no_installments)
      $ie.text_field(:name,"interestRate").set(@@default_interest_rate)
      $ie.text_field(:name,"loanAmount").set(@@max_loan_amount)
      $ie.text_field(:name,"disbursementDateDD").set("")
      $ie.text_field(:name,"disbursementDateMM").set("")
      $ie.text_field(:name,"disbursementDateYY").set("")
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_disbursal_date))
      $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","NA","NA","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check with Loan Instance,Interest Rate and no of installments","NA","NA","Failed") 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  
  def validate_no_of_installments_greater()
    begin
      #$ie.select_list(:name,"prdOfferingId").select_value("")
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      dbquery("select max_no_installments from loan_offering where prd_offering_id="+@@product_id)
      max_no_installments=dbresult[0]
      #puts "@@Max of installments " + max_no_installments.to_s
      installments=(max_no_installments).to_i+1
      #puts "@@no of installments " + installments.to_s
      $ie.text_field(:name,"noOfInstallments").set(installments.to_s)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_no_of_installments))
      $logger.log_results("Checking Greater Installmensts","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking Greater Installmensts","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def validate_no_of_installments_lesser()
    begin
      dbquery("select min_no_installments from loan_offering where prd_offering_id="+@@product_id)
      min_no_installments=dbresult[0]
      installments=(min_no_installments).to_i-1
      $ie.text_field(:name,"noOfInstallments").set(installments.to_s)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_no_of_installments))
      $logger.log_results("Checking Lesser Installmensts","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking Lesser Installmensts","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def validate_ammount_greater()
    begin
      #$ie.select_list(:name,"prdOfferingId").select_value("")
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      dbquery("select max_loan_amnt from loan_offering where prd_offering_id="+@@product_id)
      max_loan_amnt=dbresult[0]
      #puts "@@Max Loan ammount " + max_loan_amnt.to_s
      loanammount=max_loan_amnt.to_i+2
      #puts "@@laon Loan ammount " + loanammount.to_s
      $ie.text_field(:name,"loanAmount").set(loanammount.to_s)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_ammount))
      $logger.log_results("Checking Greater amount","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking Greater amount","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def validate_ammount_zero()
    begin
      $ie.text_field(:name,"loanAmount").set("0")
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_ammount))
      $logger.log_results("Checking Zero amount","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking Zero amount","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def validate_ammount_decimal()
    begin
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      dbquery("select min_loan_amount from loan_offering where prd_offering_id="+@@product_id)
      min_loan_amnt=dbresult[0]
      amount=Float(min_loan_amt)+Float(0.2)
      amount_check=Float(min_loan_amt)+Float(0.2)
      puts "@@loan amount decimal"+amount
      $ie.text_field(:name,"loanAmount").set(amount.to_s)
      $ie.button(:value,@@loanprop['loan.continue']).click
      value="Loan Amount  "+amount_check
      puts "Ammount value"+value
      assert($ie.contains_text(value))
      $logger.log_results("Checking Decimals in amount","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking Decimals in amount","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def validate_ammount_lesser()
    begin
      #$ie.select_list(:name,"prdOfferingId").select_value("")
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      dbquery("select min_loan_amount from loan_offering where prd_offering_id="+@@product_id)
      min_loan_amnt=dbresult[0]
      #puts "@@min loan ammount" + min_loan_amnt.to_s
      amount=min_loan_amnt.to_i-1
      #puts "@@loan ammount" + amount.to_s
      $ie.text_field(:name,"loanAmount").set(amount.to_s)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_ammount))
      $logger.log_results("Checking lesser amount","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking Lesser amount","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def validate_interest_greater()
    begin
      dbquery("select max_interest_rate from loan_offering where prd_offering_id="+@@product_id)
      max_interest_rate=dbresult[0]
      interest=max_interest_rate.to_i+1
      $ie.text_field(:name,"interestRate").set(interest.to_s)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_interest_rate))
      $logger.log_results("Checking greater interest","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking greater interest","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def validate_interest_lesser()
    begin
      dbquery("select min_interest_rate from loan_offering where prd_offering_id="+@@product_id)
      min_interest_rate=dbresult[0]
      interest=min_interest_rate.to_i-1
      $ie.text_field(:name,"interestRate").set(interest.to_s)
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_interest_rate))
      $logger.log_results("Checking lesser interest","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not Checking lesser interest","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def fee_select_one_by_one()
    begin
      # $ie.select_list(:name,"prdOfferingId").select_value("")
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      #search_res=$dbh.real_query("SELECT fee_id FROM fees where category_id=5 and status=1 and fee_id not in(select fee_id from prd_offering_fees where prd_offering_id="+@@product_id+" )")
      #query was not searching on the basis of recurrence id of customer
      search_res=$dbh.real_query("select a.fee_id,a.fee_name,c.recurrence_id,c.recur_after from fees a,fee_frequency b,recurrence_detail c where a.fee_id=b.fee_id and (b.frequency_meeting_id=c.meeting_id or b.frequency_meeting_id is null )and c.recurrence_id="+@@customerRecurr_ID+" and a.fee_id not in (select fee_id from feelevel) and a.fee_id not in(select fee_id from prd_offering_fees where prd_offering_id=4 ) and a.category_id=5  and (select mod(c.recur_after,"+@@customerRecurr_after+")=0) and status=1 group by a.fee_id")
     puts "@@customerRecurr_ID"+ @@customerRecurr_ID.to_s
    puts "@@customerRecurr_after"+@@customerRecurr_after.to_s
      
      dbresult1=search_res.fetch_row.to_a
      row1=search_res.num_rows()
      puts "row 1 "+row1.to_s
      rowf=0
      number=3
      if row1.to_s=="0" then
        $logger.log_results("No Fees created for the group","NA","NA","Passed")
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
          $ie.select_list(:name,"selectedFee["+String(rowf)+"].feeId").select_value(fee_id)
          dbresult1=search_res.fetch_row.to_a
          rowf+=1
        end
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def click_continue()
    begin
      $ie.link(:text,"Clients & Accounts").click
      $ie.link(:text,@@loan_account_link).click
      $ie.text_field(:name,"searchString").set(@@display_name)
      $ie.button(:value,@@loanprop['loan.search']).click
      client_data=@@display_name+":ID"+@@global_account_num
      $ie.link(:text,client_data).click
      $ie.select_list(:name,"prdOfferingId").select_value(@@product_id)
      $ie.button(:value,@@loanprop['loan.continue']).click  
      $ie.text_field(:name,"loanAmount").set(@@max_loan_amount)  
      $ie.button(:value,@@loanprop['loan.continue']).click
      assert($ie.contains_text(@@loan_installments_review))
      $logger.log_results("Page Redirected to Loan account Review page","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page Not Redirected to Loan account Review page","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def click_preview()
    begin
      $ie.button(:value,@@loanprop['loan.preview']).click
      assert($ie.contains_text(@@loan_account_preview))
      $logger.log_results("Page redirect to review loan account","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page not redirect to review loan account","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def edit_loan_from_preview()
    begin
      $ie.button(:value,@@loanprop['loan.edit_loan_acc']).click
      assert($ie.contains_text(@@loan_account_info))
      $logger.log_results("Page redirect to Create loan account","N/A","N/A","Passed")
     # fee_select_one_by_one
      click_continue
      click_preview
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page not redirect to Create loan account","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def click_submit(status)
    begin
      $ie.button(:value,status).click
      assert($ie.contains_text(@@loan_create_success))
      $logger.log_results("Loan account Creation Success","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Loan account Creation Success","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def view_client_details_link_check()
    begin
      assert($ie.contains_text(@@view_loan_account_details))
      $logger.log_results("Link View loan account details now","Should Exist","Existed","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Link View loan account details now","Should Exist","Not Existed","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
 
  def view_client_details_link_click()
    begin
      $ie.link(:text,@@view_loan_account_details).click
      assert($ie.contains_text(@@loanprop['loan.acc_summary'])) and assert($ie.contains_text(@@loanprop['loan.trxn']))
      $logger.log_results("Page entered in to account details page","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page entered in to account details page","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def edit_account_status()
    begin
      dbquery("select max(account_id) from account")
      account_id=dbresult[0]
      dbquery("select account_state_id from account where account_id="+account_id)
      statusid=dbresult[0]
      if statusid=="1" then
        change_status_pending()
      elsif statusid=="2" then
        change_status_active()
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def change_status_pending()
    begin
      $ie.link(:text,@@loanprop['loan.edit_acc_status']).click
      $ie.radio(:name,"newStatusId","2").set
      $ie.text_field(:name,"notes").set("aaaa")
      $ie.button(:value,@@loanprop['loan.preview']).click
      $ie.button(:value,@@loanprop['loan.submit']).click
      assert($ie.contains_text(@@loanprop['loan.performance_history']))
      view_status_history_pending()
      $logger.log_results("Status changed to Pending","NA","NA","passed") 
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status changed to pending","NA","NA","failed") 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def change_status_active()
    begin
      $ie.link(:text,@@loanprop['loan.edit_acc_status']).click
      $ie.radio(:name,"newStatusId","3").set
      $ie.text_field(:name,"notes").set("aaaa")
      $ie.button(:value,@@loanprop['loan.preview']).click
      $ie.button(:value,@@loanprop['loan.submit']).click
      assert($ie.contains_text(@@loanprop['loan.performance_history']))
      view_status_history_active()
      $logger.log_results("Status changed to Active","NA","NA","passed") 
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status changed to Active","NA","NA","failed") 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def view_status_history_pending()
    begin
      puts "label1 "+@@pending_application_label.to_s
      puts "label2 "+@@partial_application_label.to_s
      puts "label3 "+@@loanprop['loan.view_status_history'].to_s
      $ie.link(:text,@@loanprop['loan.view_status_history']).click
      assert($ie.contains_text(@@accountprop['Account.StatusHistory']))and assert($ie.contains_text(@@pending_application_label)) and assert($ie.contains_text(@@partial_application_label))
      $logger.log_results("Navigated to status history page when status is Pending","NA","should be in status history page","Passed")    
      $ie.button(:value,@@loanprop['loan.returnToAccountDetails']).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not in status history page when status is Pending","NA","should be in status history page","Failed")    
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def  view_status_history_active()
    begin
      $ie.link(:text,@@loanprop['loan.view_status_history']).click
      assert($ie.contains_text(@@accountprop['Account.StatusHistory']))and assert($ie.contains_text(@@pending_application_label)) and assert($ie.contains_text(@@approved_application_label))
      $logger.log_results("Navigated to status history page when status is active","NA","NA","Passed")    
      $ie.button(:value,@@loanprop['loan.returnToAccountDetails']).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Not in status history page when status is active","NA","should be in status history page","Failed")    
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def view_change_log()
    begin
      $ie.link(:text,@@loanprop['loan.view_change_log']).click
      assert($ie.contains_text(@@loanprop['loan.change_log']))
      $logger.log_results("View Change Log link is working","NA","NA","Passed")
      $ie.button(:value,@@loanprop['loan.returnToAccountDetails']).click    
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Change Log link is not working","NA","NA","Failed")        
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def view_repayment_schedules()
    begin
      $ie.link(:text,@@loanprop['loan.view_schd']).click
      assert($ie.contains_text(@@loanprop['loan.repayment_sched']))
      $logger.log_results("View repayment schedule link working","N/A","N/A","Passed")
      $ie.button(:value,@@loanprop['loan.returnToAccountDetails']).click 
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View repayment schedule link not working","N/A","N/A","Failed")    
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def view_account_activity()
    begin
      $ie.link(:text,@@loanprop['loan.view_acc_activity']).click
      assert($ie.contains_text(@@loanprop['loan.acc_statement']))
      $logger.log_results("View all account activity link working","N/A","N/A","Passed")
      $ie.button(:value,@@loanprop['loan.returnToAccountDetails']).click 
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View all account activity link not working","N/A","N/A","Failed")    
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def edit_loan_account_data()
    begin
      $ie.link(:text,@@loanprop['loan.edit_acc_info']).click 
      $ie.select_list(:name,"businessActivityId").select_value("233")
      $ie.select_list(:name,"collateralTypeId").select_value("2")
      $ie.text_field(:name,"collateralNote").set("aaaaa")
      $ie.select_list(:name,"businessActivityId").select_value("231")
      $ie.button(:value,@@loanprop['loan.preview']).click
      assert_on_page("Purpose of Loan:  0001-Cow Purchase")
      assert_on_page("Account Owner:  "+@@display_name)
      assert($ie.contains_text(@@loanprop['loan.preview']+" "+@@loan_label+" "+@@accountprop['accounts.acc_info']))
      $logger.log_results("Page redirected to preview","NA","NA","Passed")
      $ie.button(:value,@@loanprop['loan.submit']).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page redirected to preview","NA","NA","Failed")
    rescue =>excp
      quit_on_error(excp)
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
      $ie.text_field(:name,"searchString").set(global_account_number)
      $ie.button(@@loanprop['loan.search']).click
      search_name="Account # "+global_account_number
      $ie.link(:text,search_name).click
      assert($ie.contains_text(@@loanprop['loan.performance_history']))
      $logger.log_results("Account Details page","Open","opened","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Account Details page","Open","not opened","failed")
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  
  def edit_loan_account_with_purpose_of_loan()
    begin
      $ie.link(:text,@@loanprop['loan.edit_acc_info']).click
      $ie.select_list(:name,"businessActivityId").select_value(231)
      $ie.button(:value,@@loanprop['loan.preview']).click
      assert($ie.contains_text(@@loanprop['loan.preview']+" "+@@loan_label+" "+@@accountprop['accounts.acc_info']))
      $logger.log_results("Can able to select purpose of loan","N/A","N/A","Passed")
      $ie.button(:value,@@loanprop['loan.submit']).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Can able to select purpose of loan","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #added as part og Bug#824
  def disburse_loan() 
   begin
      $ie.link(:text,@@loan_disburse_loan).click    
      $ie.select_list(:name,"paymentTypeId").select("Cash")
      if($ie.select_list(:name,"paymentModeOfPayment").enabled?())
       $ie.select_list(:name,"paymentModeOfPayment").select("Cash")
      end
            
      $ie.button(:value,@@loan_reviewtransaction).click  
      $ie.button(:value,@@loan_submit).click
      assert($ie.contains_text(@@loan_edit_acc_status))
      $logger.log_results("Disburse loan","Disburse a loan","Successful","Passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Disburse loan","Disburse a loan","Unsuccessful","failed")
      rescue =>excp
      quit_on_error(excp)
    end
end # end of function disburse loan()
  
 #added as part of Bug#824
 
 def check_Miscfees_applied_to_loan()
      begin 
      

        # Getting the necessary data through query
        dbquery("select m.account_id,m.global_account_num,prd_offering_name from prd_offering po, " +
      "(select ddd.account_id,ddd.global_account_num,laa.prd_offering_id from loan_account laa, " +
      "(select a.global_account_num,a.account_id from Loan_Schedule la, account a where a.account_id=la.account_id " +
      "and account_type_id=1 and account_state_id =3 and Installment_Id = 1 and PAYMENT_DATE is NULL )ddd " + 
      "where ddd.account_id=laa.account_id and laa.disbursement_date=current_date) m " + 
      "where m.prd_offering_id=po.prd_offering_id")
        
        # Storing  results that are retrieved
        @@account_id=dbresult[0]      
        @@global_cust_num=dbresult[1]
        @@prd_offering_name=dbresult[2]
        
        # Clicking Clients & Accounts link
        $ie.link(:text,"Clients & Accounts").click      
        # Searching the specific loan data from the query retrieved
        $ie.text_field(:name,"searchString").set(@@global_cust_num.to_s) 
        $ie.button(:value,@@loan_search).click   
  
        # Clicking the loan account link
        $ie.link(:text,"Account # " + @@global_cust_num).click             
        
        # Click Apply Charges Link
        $ie.link(:text,@@loan_apply_charges).click             
        # Select Misc Fees and enter some value
        $ie.select_list(:name,"chargeType").select_value("-1")
        $ie.text_field(:name,"chargeAmount").set("34")
        $ie.button(:value,@@loan_submit).click
  
        $ie.link(:text,@@loan_view_repayment_schedule).click             
       #Checking whether Installment Paid is present      
       begin 
           assert($ie.contains_text(@@loan_installment_paid))
              $logger.log_results("Bug#824-Apply fees to Loan accounts","Apply fees to loan account","Displayed as installement paid","Failed")
           rescue Test::Unit::AssertionFailedError=>e
              $logger.log_results("Bug#824-Apply fees to Loan accounts","Apply fees to loan account","Not displayed as installement paid","passed")
           rescue =>excp       
              quit_on_error(excp)    
           end
       end
       
       $ie.button(:value,@@loan_return_to_account_details).click   
  
        # Calling disburse_loan() function
          disburse_loan()
        #view all account activity moved here as it visible only for disbursed loans
          view_account_activity()
        #added as part of Bug#720     
          check_next_installment_date()
        #added as part of Bug#736     
          check_apply_payment_for_loans() 
        #added as part of Bug#744    
          check_waive_fees()
        #added as part of Bug#867
          check_repay_loan()
        #added to create active accounts for feeone.rb file
        activate_loan_accounts()
          
end

 #added as part of #720
 # This function will check the date shown in view installment page in loan
def check_next_installment_date()
    begin
      
      # Getting the next installment date through query
      dbquery("select date_format(action_date,'%d/%m/%Y') from loan_schedule " + 
      "where account_id=" + @@account_id + " and action_date>=current_date order by action_date")
      next_installment_date=dbresult[0]
     $ie.link(:text,@@loan_installment_details).click  
      # Asserting whether the next installment in displaying form the above query
      begin
        assert($ie.contains_text(@@loan_total_amount_due + " " + next_installment_date))
        $logger.log_results("Bug#720-Next Installment Date", "Message","Displaying","Passed")
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Bug#720-Next Installment Date", "Message","Not Displaying","Failed")
        rescue =>excp
        quit_on_error(excp)
      end              
      $ie.button(:value,@@loan_return_to_account_details).click     
    end    
end
 
 #added as part of Bug#730
  def check_change_acct_status()
    begin
        # Getting the office_name, personnel, customer_id, account_id from the Query
        dbquery("Select o.display_name as office_name,p.display_name as personnel,lac.customer_id,lac.account_id from customer c, " +
        "(select a.customer_id,a.account_id from loan_account la, account a where a.account_id=la.account_id and account_state_id=2) lac, " +
        "(select personnel_id,display_name,office_id from personnel where level_id=1) p, " + 
        "(select office_id,display_name from office where office_level_id=5 and status_id=1) o " + 
        " where p.office_id=o.office_id and p.personnel_id=c.loan_officer_id " + 
        " and lac.customer_id=c.customer_id") 
        
        # Storing results that are retrieved
        @@office_name=dbresult[0]
        @@personnel=dbresult[1]
        $ie.link(:text,"Clients & Accounts").click    
        $ie.link(:text,@@change_acct_status).click    
        # Selecting all the select boxes
        $ie.select_list(:name,"officeId").select(@@office_name.to_s)        
        $ie.select_list(:name,"personnelId").select(@@personnel.to_s)
        $ie.button(:value,@@loan_search).click   
        $ie.checkbox(:name,"selectAll1").set(set_or_clear=true)        
        $ie.button(:value,@@loan_submit).click    
      begin
       assert($ie.contains_text("Please specify notes."))     
       $logger.log_results("Bug#730-Check Mandatory Notes field", "Do not enter notes","Displaying error message","Passed")
       rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Bug#730-Check Mandatory Notes field", "Do not enter notes","Not displaying error message","failed")
       rescue =>excp
       quit_on_error(excp)
       end                
    end
  
  end
  
  
  #added as part og Bug#736
  def check_apply_payment_for_loans()
    begin
        dbquery("select action_date,account_id,sum(principal-PRINCIPAL_PAID) p, " +
                "sum(misc_fees-MISC_FEES_PAID)  m , " +
                "SUM(interest-INTEREST_PAID) i , " +
                "((principal-PRINCIPAL_PAID)+(misc_fees-MISC_FEES_PAID)+(interest-INTEREST_PAID)) total " +
                "from loan_schedule where account_id=" +@@account_id+ " and " +
                "action_date>=current_date group by action_date order by action_date limit 1") 

        @@get_next_installment_fee_amount = dbresult[3].to_f
        $ie.link(:text,@@loan_apply_payment).click
        $ie.text_field(:name,"amount").set(@@get_next_installment_fee_amount.to_s) 
        $ie.select_list(:name,"paymentTypeId").select("Cash")
        $ie.button(:value,@@loan_reviewtransaction).click  
        $ie.button(:value,@@loan_submit).click
        
        #excuting the same query to get the misc amount after applying the payment
        dbquery("select action_date,account_id,sum(principal-PRINCIPAL_PAID) p, " +
                "sum(misc_fees-MISC_FEES_PAID)  m , " +
                "SUM(interest-INTEREST_PAID) i , " +
                "((principal-PRINCIPAL_PAID)+(misc_fees-MISC_FEES_PAID)+(interest-INTEREST_PAID)) total " +
                "from loan_schedule where account_id=" +@@account_id+ " and " +
                "action_date>=current_date group by action_date order by action_date limit 1")
        @@get_next_installment_fee_amount = dbresult[3].to_f        
        
       $ie.link(:text,@@loan_installment_details).click    
        table_obj=$ie.table(:index,12) 
   	    @@get_fees_from_next_installment_page = table_obj[5][2].text.to_f
   	    
   	    puts  "@@get_fees_from_next_installment_page->"+@@get_fees_from_next_installment_page.to_s
   	    puts "@@get_next_installment_fee_amount->"+@@get_next_installment_fee_amount.to_s

        # Checking whether Misc fees is shown is present   
        begin
           assert(@@get_fees_from_next_installment_page == @@get_next_installment_fee_amount)
             $logger.log_results("Bug#736-Apply payment in loans", "Message","Found","Passed")
           rescue Test::Unit::AssertionFailedError=>e
             $logger.log_results("Bug#736-Apply payment in loans", "Message","Not Found","Failed")
           rescue =>excp
             quit_on_error(excp)
        end  
    end
  end # end of function check_apply_payment_for_loans()
  
  #added as part of Bug#744
  def check_waive_fees()
    begin
     $ie.link(:text,@@loan_apply_charges).click
     $ie.select_list(:name,"chargeType").select_value("-1")
     $ie.text_field(:name,"chargeAmount").set("34")
     $ie.button(:value,@@loan_submit).click  
     puts "@@acouint_id=>"+@@account_id.to_s
     # query to get the remaining fees amount and the  total from the DB in view all account activity page.
     dbquery("select account_id,"+
            "round((sum(principal)- sum(principal_paid))) p,"+
            "(sum(misc_fees)-sum(misc_fees_paid)) m,"+
            "(sum(interest)-sum(interest_paid)) i," +
            "round((sum(principal)- sum(principal_paid)))+sum(misc_fees)-sum(misc_fees_paid)+sum(interest)-sum(interest_paid) grandtotal " +
            " from loan_schedule where account_id="+@@account_id+" group by account_id")
     @@get_misc_fee_amount = dbresult[2].to_f
     @@get_total=dbresult[4].to_f
     #variable to check the running balance
     running_balance=@@get_total-@@get_misc_fee_amount
     
     $ie.link(:text,@@loan_installment_details).click
     $ie.link(:text,@@loan_waive).click
     $ie.button(:value,@@loan_return_to_account_details).click         
     $ie.link(:text,@@loan_view_account_activity).click               
     table_obj=$ie.table(:index,13)  # New Value that is matching in the page
     
     get_fees_from_all_activity_page =  table_obj[3][11].text.to_f
     get_running_balance_from_all_activity_page = table_obj[3][12].text.to_f
    
      puts "running_balance=>"+running_balance.to_s
      
    # query to get the remaining fees from the DB in view all account activity page after waiving off the fees.
        dbquery("select account_id,"+
                "round((sum(principal)- sum(principal_paid))) p,"+
                "(sum(misc_fees)-sum(misc_fees_paid)) m,"+
                "(sum(interest)-sum(interest_paid)) i," +
                "round((sum(principal)- sum(principal_paid)))+sum(misc_fees)-sum(misc_fees_paid)+sum(interest)-sum(interest_paid) grandtotal " +
                " from loan_schedule where account_id="+@@account_id+" group by account_id")
       @@get_misc_fee_amount = dbresult[2].to_f
       begin
            assert((get_fees_from_all_activity_page == @@get_misc_fee_amount)&&(get_running_balance_from_all_activity_page == running_balance))
            $logger.log_results("Bug#744-Waive Fees", "Waive the fee amount","The fees should be waived","Passed")
            rescue Test::Unit::AssertionFailedError=>e
            $logger.log_results("Bug#744-Waive Fees", "Waive the fee amount","The fees should is not waived","failed")
            rescue =>excp
            quit_on_error(excp) 
        end
      $ie.button(:value,@@loan_return_to_account_details).click         
    end
  end
  
  #added as part of Bug#964
  def check_repay_loan()
    begin
      $ie.link(:text,@@repayloan).click   
      $ie.select_list(:name,"paymentTypeId").select("Cash")
      $ie.button(:value,@@loan_reviewtransaction).click  
      $ie.button(:value,@@loan_submit).click
      $ie.link(:text,@@loan_view_account_activity).click          
      table_obj=$ie.table(:index,11)  # New Value that is matching in the page
      total_amount= table_obj[3][12].text.to_f
      
      #query to get the total left in the DB
       dbquery("select account_id,"+
                "round((sum(principal)- sum(principal_paid))) p,"+
                "(sum(misc_fees)-sum(misc_fees_paid)) m,"+
                "(sum(interest)-sum(interest_paid)) i," +
                "round((sum(principal)- sum(principal_paid)))+sum(misc_fees)-sum(misc_fees_paid)+sum(interest)-sum(interest_paid) grandtotal " +
                " from loan_schedule where account_id="+@@account_id+" group by account_id")
      
      loan_balance=dbresult[4].to_f
      
      puts "loan_balance=>"+loan_balance.to_s
      puts "total_amount=>"+total_amount.to_s
       puts "account_id=>"+@@account_id.to_s
      
          begin
            assert(total_amount == loan_balance)
            $logger.log_results("Bug#867-Check repay Loan", "Repay the Loan","Total should be 0","Passed")
            rescue Test::Unit::AssertionFailedError=>e
            $logger.log_results("Bug#867-Check repay Loan", "Repay the Loan","Total should not 0","failed")
            rescue =>excp
            quit_on_error(excp) 
        end
    end
  end
  
  def activate_loan_accounts()
    begin
    dbquery("select global_account_num,account_state_id from account where account_type_id=1 and account_state_id=2")
    global_account_number=dbresult[0]
    $ie.link(:text,"Clients & Accounts").click
    $ie.text_field(:name,"searchString").set(global_account_number)
    $ie.button(@@loanprop['loan.search']).click
    search_name="Account # "+global_account_number
    $ie.link(:text,search_name).click
    change_status_active()
    end
  end
  
  
end # end of class

class LoanAccountTest
  loanobject=LoanAccountCreateEdit.new
  loanobject.login_loanaccount()
  loanobject.database_connection()
  loanobject.get_labels_from_db()
  loanobject.read_from_properties_file()
  loanobject.read_values_from_hash()
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
    loanobject.get_values_from_loanoffering
    loanobject.load_error_messages()
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
    #loanobject.fee_select_one_by_one
    #loanobject.validate_ammount_decimal()
    loanobject.click_continue
    loanobject.click_preview
    loanobject.edit_loan_from_preview
    loanobject.click_submit(status)
    loanobject.view_client_details_link_check
    loanobject.view_client_details_link_click
    loanobject.edit_account_status
      #commented this as this link does not exist currently
      #loanobject.view_change_log 
    loanobject.view_repayment_schedules
  #  loanobject.view_account_activity
    loanobject.edit_loan_account_data
  end
  
  #added as part of Bug#824
  loanobject.check_Miscfees_applied_to_loan()
  #added as part of Bug#730
 loanobject.check_change_acct_status()
  
  loanobject.mifos_logout()
end