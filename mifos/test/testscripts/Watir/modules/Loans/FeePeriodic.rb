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

#Checking loan account creation and edit loans fee 
class FeePeriodic < TestClass
  
  #read from properties file into a hash
  def read_from_properties_file()
    begin
      @@accountprop=load_properties("modules/propertis/accountsUIResources.properties")
      @@loanprop=load_properties("modules/propertis/LoanUIResources.properties")
      @@customerprop=load_properties("modules/propertis/CustomerSearchUIResources.properties")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #read from the hash into variables
  def read_from_hash()
    begin
      #labels
      @create_loan_account=@@accountprop['accounts.create_loan_acc']
      @apply_charges_label=@@accountprop['accounts.apply_charges']
      #buttons
      @search_button=@@accountprop['accounts.search']
      @submit_button=@@accountprop['accounts.submit']
      @cancel_button=@@accountprop['accounts.cancel']
      #error msg's
      @chargetype_msg=string_replace_message(@@accountprop['errors.mandatory'],"{0}",@@accountprop['account.chargetype'])
      @amount_msg=string_replace_message(@@accountprop['errors.mandatory'],"{0}",@@accountprop['account.amount'])
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #Connecting to database
  def data_connection()
    begin
      name_login=$validname
      #dbquery("select account.account_id,global_account_num,account.office_id,loan_account.no_of_installments,customer.loan_officer_id ,recurrence_detail.meeting_id ,recurrence_detail.recurrence_id from customer , account, loan_account,fee_frequency,recurrence_detail where customer.customer_id = account.customer_id and account.account_id=loan_account.account_id and fee_frequency.frequency_meeting_id=recurrence_detail.meeting_id  and customer.customer_level_id<3 and account.account_type_id=1 and  account.account_state_id <11  order by 1 desc limit 1")
      dbquery("select c.account_id,c.global_account_num,d.recurrence_id from recurrence_detail d ,(select b.account_id,b.global_account_num,a.meeting_id from loan_account a,account b where a.account_id=b.account_id and b.account_state_id < 11 and b.account_type_id=1) c where c.meeting_id=d.meeting_id order by 1 desc limit 1")
      @@account_id=dbresult[0]		
      @@global_account_num=dbresult[1]					
      @@recurrence_id=dbresult[2]
      #		dbquery("select fee_name from fees,fee_frequency,account_fees where fees.fee_id=fee_frequency.fee_id and fee_frequency.fee_frequencytype_id=1 and fees.status=1 and fees.category_id=5 and account_fees.fee_id=fees.fee_id and account_fees.fee_status is not null and account_fees.account_id="+@@account_id)
      #		@@fee_name=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  #Loging into Mifos
  def loans_login()
    begin
      start
      login($validname,$validpwd)
      db_connect()
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #Checking for to click Clients & Accounts link and check for the Create Loan Account
  def check_loans()
    begin
      $ie.link(:text,"Clients & Accounts").click
      assert($ie.link(:text,@create_loan_account).exists?())
      $logger.log_results(@create_loan_account+ " Link exist","NA","should exist","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results(@create_loan_account+" Link does not exist","NA","should exist","failed")
    rescue =>excp
      quit_on_error(excp)
    end		
  end
  
  #Selecting the account 
  def select_account()
    begin
        data_connection()
        $ie.button(:value,@search_button).click	
      begin	
        @errormsg=@@customerprop['errors.namemandatory'].squeeze(" ")
        assert($ie.contains_text(@errormsg)) 
        $logger.log_results("Error message check","Empty search","Error message "+@errormsg.to_s+" should appear","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message check","Empty search","Error message "+@errormsg.to_s+" should appear","failed")
      end  
        $ie.text_field(:name,"searchString").set(@@global_account_num)
        $ie.button(:value,@search_button).click
        @@loan_account=@@accountprop['accounts.Account']+" # " + @@global_account_num
        $ie.link(:text, @@loan_account).click        
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def add_fee_man()
    begin
      $ie.link(:text,@apply_charges_label).click
      $ie.button(:value,@submit_button).click
      assert($ie.contains_text(@chargetype_msg)) and assert($ie.contains_text(@amount_msg))
      $logger.log_results("Error message check","click on submit button without applying any fees","error message","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Error message check","click on submit button without applying any fees","error message","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Adding a new fee to the loan account
  def loan_account_add_fee
    begin
      select_account
      add_fee_man
      loan_account_add_fee_test_cancel
      $ie.link(:text,@apply_charges_label).click
        begin
          assert($ie.contains_text(@@global_account_num+"  -  "+@apply_charges_label))
          $logger.log_results("clicked on link "+@apply_charges_label,"NA","should navigate to apply charges page","passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("clicked on link "+@apply_charges_label,"NA","should navigate to apply charges page","failed")
        end
      dbquery("select c.fee_id,d.recurrence_id,c.fee_name,d.recur_after from recurrence_detail d,(select a.fee_id,a.fee_name,b.frequency_meeting_id from fees a,fee_frequency b where a.fee_id=b.fee_id and a.category_id=5 and a.status=1 and b.fee_frequencytype_id=1 ) c where c.frequency_meeting_id=d.meeting_id and d.recurrence_id="+@@recurrence_id+" and  c.fee_id not in (select Fee_Id from account_fees where Account_id ="+@@account_id+" and Fee_Status <> 2 ) order by 1 desc")
      @@fee_id = dbresult[0]	  
      @@feesname=dbresult[2]
      @@recur_after=dbresult[3]
      if(@@recurrence_id.to_i==2)
        @@recur_type="month(s)"
      elsif(@@recurrence_id.to_i==1)
        @@recur_type="week(s)"
      end
      $ie.select_list(:name,"chargeType").select_value(@@fee_id)	
      $ie.button(:value,@submit_button).click
      dbquery("select account_fee_amnt from account_fees where fee_id="+@@fee_id)
      @@fee_amnt=dbresult[0]
      # puts @@feesname+": "+@@fee_amnt.to_f.to_s+" ( Recur every "+@@recur_after+" "+recur_type+"" )"
      assert($ie.contains_text(@@feesname+": "+@@fee_amnt.to_f.to_s+" ( "+@@loanprop['loan.periodicityTypeRate']+" "+@@recur_after+" "+@@recur_type+")"))
      $logger.log_results("Fees "+@@feesname.to_s+" appears under recurring account fees",@@feesname,"NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Fees "+@@feesname.to_s+" does not appear under recurring account fees",@@feesname,"NA","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end	
  
  #testing the cancel button functionality
  def loan_account_add_fee_test_cancel
    begin
      $ie.button(:value,@cancel_button).click
      @@msg1 =@@loanprop['loan.edit_acc_status']	 
      @@msg2 =@@loanprop['loan.view_schd']
      assert($ie.contains_text(@@msg1)) and assert($ie.contains_text(@@msg2))
      $logger.log_results("Click on cancel button ","click on cancel button in apply charges","navigated to account details page","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Click on cancel button ","click on cancel button in apply charges","navigated to account details page","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end	
  
  # Removing the added fee from the loan account
  def loan_account_remove_fee
    begin
      dbquery("select fees.fee_name from fees where fee_id="+@@fee_id)
      @@loan_fee_name=dbresult[0]
      dbquery("select office_id,personnel_id, created_date from account where account_id = " + @@account_id)
      @@office_id = dbresult[0]
      @@personnel_id = dbresult[1]
      @@created_date = dbresult[2]
      $ie.link(:text,"Clients & Accounts").click
      $ie.text_field(:name,"searchString").set(@@global_account_num)
      $ie.button(:value,@search_button).click
      @@loan_account=@@accountprop['accounts.Account']+" # " + @@global_account_num
      $ie.link(:text, @@loan_account).click
      #$ie.goto($test_site + "/accountAppAction.do?method=removeFees&feeId=" + @@fee_id + "&accountId=" + @@account_id + "&recordOfficeId=" + @@office_id +"&recordLoanOfficerId=" +@@personnel_id +"&createdDate="+ @@created_date+"%2000:00:00.0")        
      $ie.link(:text,@@loanprop['loan.remove']).click
      $logger.log_results("clicked on Remove link to remove the periodic fees ","NA","NA","NA")
      begin
        assert(!$ie.contains_text(@@feesname+": "+@@fee_amnt+" ( "+@@loanprop['loan.periodicityTypeRate']+" "+@@recur_after+" "+@@recur_type+")"))
        $logger.log_results("Fees "+@@feesname.to_s+" does not appear under recurring account fees",@@feesname,"NA","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Fees "+@@feesname.to_s+" appears under recurring account fees",@@feesname,"NA","failed")     
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end	
  
  
end


class FeeP 
  loansobject=FeePeriodic.new
  loansobject.read_from_properties_file
  loansobject.read_from_hash
  loansobject.loans_login
  loansobject.loan_account_add_fee
  loansobject.loan_account_remove_fee
  loansobject.mifos_logout
end