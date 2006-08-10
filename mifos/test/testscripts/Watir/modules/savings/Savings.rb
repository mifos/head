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
#checking saving creation and edit savings
class Savings < TestClass
    #connecting to database
	def data_connection(typeid,activeid)
		name_login=$validname
		@@type_id=typeid
		@@active_id=activeid
		dbquery("select office_id from personnel where login_name="+"'"+name_login+"'")
		@@office_id=dbresult[0]
		dbquery("select display_name,customer_id,prd_offering_name,prd_offering_id from customer,prd_offering where customer.customer_level_id="+typeid+" and customer.status_id="+activeid+" and prd_offering.prd_applicable_master_id="+typeid+" and prd_offering.offering_status_id=2 and prd_offering.office_id="+@@office_id+" order by display_name")
		@@client_name=dbresult[0]
		@@cust_id=dbresult[1]
		@@prod_name=dbresult[2]
		@@prod_id=dbresult[3]
	end 
	#loging into Mifos
	def savings_login()
		start
		login($validname,$validpwd)
		db_connect()
	end
	#checking for Clients & Accounts link
	def check_savings()
		begin
		$ie.link(:text,"Clients & Accounts").click
		assert($ie.contains_text($savingsaccount_link))
		$logger.log_results("Link Check","Create Margin Money Account","Create Margin Money Account","passed")
		rescue =>e
		$logger.log_results("Link Check","Create Margin Money Account","exists","failed")
		end
	end
	#checking for Create Savings Account link
	def click_savings()
		begin
		$ie.link(:text,$savingsaccount_link).click
		assert($ie.contains_text($savings_select_client))
		$logger.log_results("Create Margin Money Account - Select client","text","exists","passed")
		rescue =>e
		$logger.log_results("Create Margin Money Account - Select client","text","exists","failed")
		end
	end	
    #checking for mandatory fields in select client page
	def select_client_mandatory()
		begin
		$ie.button(:value,'Search').click
		assert($ie.contains_text($mandatory_without_client))
		$logger.log_results("Mandatory Error","Text","Display","Passed")
		rescue =>e
		$logger.log_results("Mandatory Error","Text","Display","Failed")
		end
	end
	#selcting the client
	def select_client(typeid,activeid)
		begin
		data_connection(typeid,activeid)
		$ie.text_field(:name,"searchNode(searchString)").set(@@client_name)
		$ie.button(:value,'Search').click
		$ie.goto($test_site+"/savingsAction.do?method=getPrdOfferings&customerId="+@@cust_id)
		assert($ie.contains_text($savings_select_mmaccount))
		$logger.log_results("Create Margin Money Account - Enter Margin Money account information","text","exists","passed")
		rescue =>e
		$logger.log_results("Create Margin Money Account - Enter Margin Money account information","text","exists","failed")
		end
	end
	#checking the mandatory in select savings instance page
	def select_savings_man()
		begin
		$ie.button(:value,'Continue').click
		assert($ie.contains_text($mandatory_without_product))
		$logger.log_results("Mandatory Error","Text","Display","Passed")
		rescue =>e
		$logger.log_results("Mandatory Error","Text","Display","Failed")
		end
	end
	#selecting the savings instance
	def select_savings()
		begin
		$ie.select_list(:name,"selectedPrdOfferingId").select_value(@@prod_id)
		$ie.button(:value,'Continue').click
		assert($ie.contains_text($savings_enter_account_information))
		$logger.log_results("Margin Money product summary","text","exists","passed")
		rescue =>e
		$logger.log_results("Margin Money product summary","text","exists","failed")
		end
	end
	#checking mandatory conditions
	def mandatory_check()
	 begin
	   $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
	   $ie.button(:value,'Preview').click
	   assert($ie.contains_text("Please select the value for Product Instance"))
	   $logger.log_results("Mandatory check with out Margin Money instance","should display error","displaying","Passed")
	   rescue=>e
	   $logger.log_results("Mandatory check with out Margin Money instance","should display error","Not displaying","Failed")
	 end
	end
	#checking validations of custom field
	def validate_additional_field(validationammount)
	 begin
	   $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@prod_id)
	   $ie.text_field(:name,"customField[0].fieldValue").set(validationammount)
	   $ie.button(:value,'Preview').click
	   assert($ie.conatins_text(validationammount))
	   $logger.log_results("Validation Cheking","Should done properly","Checking","Failed")
	   rescue=>e
	   $logger.log_results("Validation Cheking","Should done properly","Checking","Passed")
	 end
	end
	#checking the ammount in the preview page
	def preview_ammount(ammount)
		begin
		$ie.text_field(:name,"recommendedAmount").set(ammount)
		$ie.button(:value,'Preview').click
		assert($ie.contains_text(@@prod_name))
		$logger.log_results(@@prod_name,"text","exists","passed")
		rescue => e
		$logger.log_results(@@prod_name,"text","exists","failed")
		end
	end
    #edit data from preview page
	def edit_frompreview(cammount)
		begin
		@@c_ammount=cammount
		$ie.button(:value,'Edit Margin Money account information').click
		$ie.text_field(:name,"recommendedAmount").set(cammount)
		$ie.button(:value,'Preview').click
		$logger.log_results(cammount,"data","exists","passed")
		rescue =>e
		$logger.log_results(cammount,"data","exists","passed")
		end
	end
	#Submitting the savings account data
	def submit_data(status)
		begin
		$ie.button(:value,status).click
		assert($ie.contains_text($savings_success))
		$logger.log_results("Margin Money Account","Creation","successfull","passed")
		rescue =>e
		$logger.log_results("Margin Money Account","Creation","successfull","failed")
		end
	end
	#data base check for the created savings account
	def database_check()
		begin
		dbquery("select recommended_amount,prd_offering_id from savings_account where prd_offering_id="+@@prod_id+" and recommended_amount="+@@c_ammount+".000")
		d_ammount=dbresult[0]
		d_prdid=dbresult[1]
		if d_prdid==@@prod_id  then
		$logger.log_results("Database values","Matching","successfully","passed")
		else
		$logger.log_results("Database values","Matching","successfully","failed")
		end
		end
	end
	#Navigating to Savings details page
	def savings_detailspage
		begin
		$ie.link(:text,"View Margin Money account details now").click
		dbquery("select global_account_num,account_state_id from account where account_id=(select max(account_id) from account)")
		@@account_id=dbresult[0]
		@@status_id=dbresult[1]
		assert($ie.contains_text(@@account_id))
		$logger.log_results("View Margin Money Account Details Now","Link","working","passed")
		rescue=>e
		$logger.log_results("View Margin Money Account Details Now","Link","working","failed")
		end
	end
	#Id's for status change
	def statusChange
		if @@status_id=="16"
			change_statusInactive
		elsif @@status_id=="13"
			change_statusPending
		elsif @@status_id=="14"	
		    change_statusActive
		elsif @@status_id=="18"
			change_statusActive
		end
	end
	#changing status to Inactive
	def change_statusInactive
		begin
		$ie.link(:text,"Edit account status").click
		$ie.radio(:name,"newStatusId","18").set
		$ie.text_field(:name,"accountNotes.comment").set("aaaaa")
		$ie.button(:value,"Preview").click
		assert($ie.contains_text("Inactive"))
		$logger.log_results("Status Change","preview","page loaded","passed")
		$ie.button(:value,"Submit").click
		rescue =>e
		$logger.log_results("Status Change","preview","page loaded","failed")
		end
	end
    #changing status to Active
	def change_statusActive
		begin
		$ie.link(:text,"Edit account status").click
		$ie.radio(:name,"newStatusId","16").set
		$ie.text_field(:name,"accountNotes.comment").set("aaaaa")
		$ie.button(:value,"Preview").click
		assert($ie.contains_text("Active"))
		$logger.log_results("Status Change","preview","page loaded","passed")
		$ie.button(:value,"Submit").click
		close_call
		rescue =>e
		$logger.log_results("Status Change","preview","page loaded","failed")
		end
	end
	def change_statusPending
	   begin
	     $ie.link(:text,"Edit account status").click
		$ie.radio(:name,"newStatusId","14").set
		$ie.text_field(:name,"accountNotes.comment").set("aaaaa")
		$ie.button(:value,"Preview").click
		assert($ie.contains_text("Application Pending Approval"))
		$logger.log_results("Status Change","preview","page loaded","passed")
		$ie.button(:value,"Submit").click
		rescue =>e
		$logger.log_results("Status Change","preview","page loaded","failed")
	   end
	end
	#checking the view all account activity link
	def viewallAccount
		begin
		$ie.link(:text,"View all account activity").click
		assert($ie.contains_text("Account statement as of"))
		$logger.log_results("View Account Activity","page","loaded","passed")
		$ie.button(:value,"Return to account details").click
		rescue =>e
		$logger.log_results("View Account Activity","page","loaded","failed")
		end
	end
	#checking the View transaction history link
	def ViewTransaction
		begin
		$ie.link(:text,"View transaction history").click
		check_text=@@account_id+" - Transaction history"
		assert($ie.contains_text(check_text))
		$logger.log_results("View transaction history","page","loaded","passed")
		$ie.button(:value,"Return to account details").click
		rescue =>e
		$logger.log_results("View transaction history","page","loaded","failed")
		end
	end
	#editing data from savings details page
	def edit_savingsDetails(nammount)
	 begin
        $ie.link(:text,"Edit Account Information").click
        $ie.text_field(:name,"recommendedAmount").set(nammount)	
        $ie.button(:value,"Preview").click
        assert($ie.contains_text(nammount))
        $logger.log_results("Edit","ammountfrom","details page","passed")
        $ie.button(:value,"Submit").click
        rescue =>e
        $logger.log_results("Edit","ammountfrom","details page","passed")
	 end
	end
	def close_call
	   assert($ie.contains_text("Close account"))
	   close_account
	   rescue =>e
	   $logger.log_results("Close account","link","not","existed")
	end
	# changing status to close
	def close_account
	 begin
	 dbquery("select search_id from customer where customer_level_id=3  and status_id=13 and display_name='"+@@client_name+"'")
	 @@search_id_clients=dbresult[0]
	 dbquery("select customer_id,display_name from customer where search_id like '"+@@search_id_clients+"%' and customer_level_id=1")
	 member=dbresult[0]
	  $ie.link(:text,"Close account").click
      $ie.select_list(:name,"paymentTypeId").select_value("1")
      $ie.select_list(:name,"customerId").select_value(member)
      $ie.text_field(:name,"notes").set("aaaaa")
      $ie.button("Preview").click
      assert($ie.contains_text("Edit Account Information"))
      $logger.log_results("Account","close","status","Failed")
      $ie.button(:value,"Submit").click
      rescue =>e
      $logger.log_results("Account","close","status","Passed")
     end
	end
	
	
	
	
end
