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

#Checking loan account creation and edit loans fee 
class FeePeriodic < TestClass
    
    #Connecting to database
	def data_connection()
		name_login=$validname
		dbquery("select account.account_id,global_account_num,account.office_id,loan_account.no_of_installments,customer.loan_officer_id ,recurrence_detail.meeting_id from customer , account, loan_account,fee_frequency,recurrence_detail where customer.customer_id = account.customer_id and account.account_id=loan_account.account_id and fee_frequency.frequency_meeting_id=recurrence_detail.meeting_id  and customer.customer_level_id<3 and account.account_type_id=1 and  account.account_state_id <12  order by 1 desc limit 1")
		@@account_id=dbresult[0]		
		@@global_account_num=dbresult[1]					
		dbquery("select fee_name from fees,fee_frequency,account_fees where fees.fee_id=fee_frequency.fee_id and fee_frequency.fee_frequencytype_id=1 and fees.status=1 and fees.category_id=5 and account_fees.fee_id=fees.fee_id and account_fees.fee_status is not null and account_fees.account_id="+@@account_id)
		@@fee_name=dbresult[0]
	end 
	
	#Loging into Mifos
	def loans_login()
		start
		login($validname,$validpwd)
		db_connect()
	end
	
	#Checking for to click Clients & Accounts link and check for the Create Loan Account
	def check_loans()
		begin
		$ie.link(:text,"Clients & Accounts").click
		assert($ie.contains_text("Create Loan Account"))
		$logger.log_results("Link Check","Create Loan Account","Create Loan Account","passed")
		rescue =>e
		$logger.log_results("Link Check","Create Loan Account","exists","failed")
		end		
	end
	
	#Selecting the account 
	def select_account()
		begin
		data_connection()
		$ie.button(:value,'Search').click		
		assert($ie.contains_text("Please enter the name to be searched in the application.")) and //
		assert($logger.log_results("Error message check","click on search with blank","Search","passed"))
		rescue =>e
		$logger.log_results("Error message check","Search","exists","failed")
				
		$ie.text_field(:name,"searchNode(searchString)").set(@@global_account_num)
		$ie.button(:value,'Search').click
		@@loan_account="Account # " + @@global_account_num
		$ie.link(:text, @@loan_account).click        
		end
	end
	def add_fee_man()
	 begin
      $ie.link(:text,"Apply charges").click
	  $ie.button(:value,"Submit").click
	  @@error_msg1 = "Please enter the value for charge type."
	  @@error_msg2 = "Please enter the value for amount."
	  assert($ie.contains_text(@@error_msg1)) and assert($ie.contains_text(@@error_msg2))
	  $logger.log_results("Error message check","click on submit with blank fee","Submit","passed")
	  rescue =>e
	  $logger.log_results("Error message check","Submit","exists","failed")
	 end
	end
	
	# Adding a new fee to the loan account
	def loan_account_add_fee
	 begin
	  select_account
	  add_fee_man
	  loan_account_add_fee_test_cancel
	  $ie.link(:text,"Apply charges").click
	  dbquery("select Fee_Name, f1.fee_id from Fees f1, fee_frequency f2 where f1.Fee_Id = f2.Fee_Id and f2.Fee_FrequencyType_Id = 1 and f2.Fee_Id not in (select Fee_Id from account_fees where Account_id =" + @@account_id + " and Fee_Status <> 2) order by 1 desc")
	  @@fee_id = dbresult[1]	  
	  $ie.select_list(:name,"chargeType").select_value(@@fee_id)	
	  $ie.button(:value,"Submit").click	  	         
     end
	end	
    #testing the cancel button functionality
	def loan_account_add_fee_test_cancel
     begin
	  $ie.button(:value,"Cancel").click
	  @@msg1 = "Edit account status"	 
	  @@msg2 = "View repayment schedule" 
	  assert($ie.contains_text(@@msg1)) and assert($ie.contains_text(@@msg2))
	  $logger.log_results("Error message check","click on cancel for apply charges","Cancel","passed")
		rescue =>e
	  $logger.log_results("Error message check","click on cancel for apply charges","exists","failed")
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
	  $ie.goto($test_site + "/accountAppAction.do?method=removeFees&feeId=" + @@fee_id + "&accountId=" + @@account_id + "&recordOfficeId=" + @@office_id +"&recordLoanOfficerId=" +@@personnel_id +"&createdDate="+ @@created_date)
	  assert($ie.contsins_text(@@loan_fee_name))						
	  $logger.log_results("Fee Remove","remove","removed","Failed")
	  rescue =>e
	  $logger.log_results("Fee Remove","remove","removed","Passed")
     end
	end	
	
	
end
