require 'mysql'
require 'modules/common/TestClass'
class BulkEntry < TestClass
    #opens the browser and login in to the Mifos
	def init
	    db_connect()
	    start()
        login($validname,$validpwd)
        link_check("Enter Collection Sheet Data")
	end
	#Checking all the mandatory fields in bulk entry page
	def check_errormsg_all()
		begin
		$ie.button(:value,'Continue').click
		assert($ie.contains_text($branch_office_msg)) and assert($ie.contains_text($loan_officer_msg))//
		and assert($ie.contains_text($center_msg)) and assert($ie.contains_text($mode_payment_msg)) and //
		assert($ie.contains_text($date_trxn_msg))
		$logger.log_results("all mandatory check ","NA","NA","passed");
		rescue Test::Unit::AssertionFailedError=>e
		$logger.log_results("all mandatory check ","NA","NA","failed");
		end
	end
	#Checking all the mandatory fields except Branch Office in bulk entry page
	def err_msg_with_only_BOselected()
	   begin
	   dbquery("select office.office_id,personnel_id,personnel.display_name as name,office.display_name  from office,personnel where office_level_id=5 and status_id=1 and personnel_status=1 and login_name='"+$validname+"'")
	   @@office_id=dbresult[0]  
       $temp=@@office_id
	   $ie.select_list(:name,"officeId").select_value(@@office_id)
	   $ie.wait
	   $ie.button(:value,'Continue').click
	   assert($ie.contains_text($loan_officer_msg))and assert($ie.contains_text($center_msg)) and //
	   assert($ie.contains_text($mode_payment_msg)) and assert($ie.contains_text($date_trxn_msg))
	   $logger.log_results("mandatory checks when branch office is selected ","NA","NA","passed")
	   rescue Test::Unit::AssertionFailedError=>e
	   $logger.log_results("mandatory checks when branch office is selected","NA","NA","failed")
	   end
	end
	#Checking all the mandatory fields except Branch Office and Loan Officer in bulk entry page
	def err_msg_with_BOandLOselected()
	   begin
	   
	   dbquery("select personnel_id from personnel where office_id="+@@office_id+" and personnel_status=1")
	   @@personnel_id=dbresult[0]
	   $ie.select_list(:name,"loanOfficerId").select_value(@@personnel_id)
	   $ie.button(:value,'Continue').click
	   assert($ie.contains_text($center_msg)) and  assert($ie.contains_text($mode_payment_msg)) and //
	   assert($ie.contains_text($date_trxn_msg))
	   $logger.log_results("mandatory checks when branch office and loan officer is selected ","NA","NA","passed");
	   rescue Test::Unit::AssertionFailedError=>e
	   $logger.log_results("mandatory checks when branch office and loan officer is selected ","NA","NA","failed");
	   end
	end
	#Checking all the mandatory fields except Branch Office, Loan Officer and Center in bulk entry page	
	def err_msg_with_BO_LO_Center_selected()
	   begin
	   dbquery("select * from customer where customer_level_id=3 and status_id=13 and loan_officer_id="+@@personnel_id)
	   @@center_id=dbresult[0]
	   $ie.select_list(:name,"customerId").select_value(@@center_id)
	   $ie.button(:value,'Continue').click
	   assert($ie.contains_text($mode_payment_msg)) 
	   $logger.log_results("mandatory checks when branch office,loan officer,center is selected","NA","NA","passed")
	   rescue Test::Unit::AssertionFailedError=>e
	   $logger.log_results("mandatory checks when branch office,loan officer,center is selected","NA","NA","failed")
	   end
	end
    #Checking error message if there is no collection due for that center
	def msg_no_collection_due()
	   begin
	   $ie.select_list(:name,"paymentId").select_value("1")
	   $ie.button(:value,'Continue').click
	   assert($ie.button(:value,'Preview').exists?())
	   $logger.log_results("In preview page and preview button exists ","NA","NA","passed")
	   if !($ie.button(:value,'Preview').enabled?())then
	   assert($ie.contains_text('No collection due for the selected Center'))
	   $logger.log_results("No collection due for the selected Center message appears","NA","NA","passed")
	   $logger.log_results("Preview button disabled","NA","disabled","passed")
	   end
	   rescue Test::Unit::AssertionFailedError=>e
	   $logger.log_results("text:No collection due for the selected Center not found","NA","NA","failed")
	   end
	end
	#checking the ammount in the bulk entry with database
	def check_ammount(flag)
	 rowrow=String(@@countrow)
	 rowcol=String(@@count_cal)
	 if flag==1
	 nameoftextbox= "depositAmountEntered["+rowrow+"]["+rowcol+"]"
	 elsif flag==0
     nameoftextbox="enteredAmount["+rowrow+"]["+rowcol+"]"
     end
	 val1=$ie.text_field(:name,nameoftextbox).getContents
	 if Float(val1)==@@total_ammount then
	 $logger.log_results("Ammount is Mathing with",String(val1),String(@@total_ammount),"Passed")
	 case @@type_ammount
	 when 0
	 $ie.text_field(:name,nameoftextbox).set(String(0))
	 when 1
	 $ie.text_field(:name,nameoftextbox).set(String(8))
	 when 2
	 $ie.text_field(:name,nameoftextbox).set(String(@@total_ammount))
	 end
	 else
	 $logger.log_results("Ammount is Mathing with",String(val1),String(@@total_ammount),"Failed")
	 end
	end
	#checking for the account short name in the bulk entry
	def check_short_name(prd_short_name,flag)
	 if prd_short_name ==@@short_name then
	   check_ammount(flag)
	 else
	 end
	end
    #Getting the Loan Account short names for the assigned center
	def loan_account_check_ammount()
	 get_list_account=$dbh.real_query("select distinct(po.prd_offering_id),po.prd_offering_short_name from account a,account_actions_date ad,prd_offering po,loan_account la,customer c where a.account_state_id in (5,9) and c.search_id like '"+@@search_id+"%' and a.customer_id=ad.customer_id and a.account_id = ad.account_id and la.account_id=a.account_id and la.prd_offering_id=po.prd_offering_id and ad.action_date='"+@@action_date+"' and a.customer_id=c.customer_id and ad.customer_id=c.customer_id order by c.customer_level_id, po.prd_offering_short_name")
	 dbresultl=get_list_account.fetch_row.to_a
	 laccount=get_list_account.num_rows()
	 @@rowlac=-1
	 @@count_cal=0
	 flag=0
	 while(@@rowlac < laccount)
      	prd_short_name=dbresultl[1]
      	check_short_name(prd_short_name,flag)
      	dbresultl=get_list_account.fetch_row.to_a
      	@@rowlac+=1
      	@@count_cal+=1
	 end
	end
    #Getting the Savings Account short names for the assigned center
	def savings_account_check_ammount()
	 get_list_account=$dbh.real_query("select distinct(po.prd_offering_id),po.prd_offering_short_name from account a,account_actions_date ad,prd_offering po,savings_account sa,customer c where a.account_state_id in (16) and c.search_id like '"+@@search_id+"%' and a.customer_id=ad.customer_id and a.account_id = ad.account_id and sa.account_id=a.account_id and sa.prd_offering_id=po.prd_offering_id and ad.action_date='"+@@action_date+"' and a.customer_id=c.customer_id and ad.customer_id=c.customer_id order by c.customer_level_id, po.prd_offering_short_name")
	 dbresultl=get_list_account.fetch_row.to_a
	 laccount=get_list_account.num_rows()
	 @@rowlaca=-1
	 @@count_cal-=1	 
	 while(@@rowlaca < laccount)
      	prd_short_name=dbresultl[1]
      	flag=1 
      	check_short_name(prd_short_name,flag)
      	dbresultl=get_list_account.fetch_row.to_a
      	@@rowlaca+=1
      	@@count_cal+=1
	 end
	end
	#Getting the total ammount to be paid in this bulk entry for Loan account
	def get_ammount(accountno,act_date_get)
	   get_amm=$dbh.real_query("SELECT A1.PRINCIPAL, A1.INTEREST, A1.PENALTY, A1.MISC_FEES, (select afad.amount from account_fees_action_detail afad, account_actions_date aad  where afad.INSTALLMENT_ID = aad.INSTALLMENT_ID and aad.ACCOUNT_ID ="+accountno+" and aad.action_date='"+act_date_get+"' and afad.id = aad.id)'FEES' FROM ACCOUNT_ACTIONS_DATE A1 WHERE (A1.ACTION_DATE<= '"+act_date_get+"' AND A1.PAYMENT_STATUS=0) AND (A1.ACCOUNT_ID ="+accountno+")")
	   dbresult2=get_amm.fetch_row.to_a
	   row2=get_amm.num_rows()
	   rowget=-1
	   @@total_ammount=0
	   while(rowget<row2-1)
	     principal=dbresult2[0]
	     interest=dbresult2[1]
	     penalty=dbresult2[2]
	     misc_fee=dbresult2[3]
	     fee=dbresult2[4]
	     if row2==nil then
	      principal=0
	      interest=0
	      penalty=0
	      misc_fee=0
	      fee=0
	     end
	     if fee==nil then
	     fee=0
	     end
	     @@total_ammount=Float(@@total_ammount)+Float(principal)+Float(interest)+Float(penalty)+Float(misc_fee)+Float(fee)
	     loan_account_check_ammount()
	     dbresult2=get_amm.fetch_row.to_a
	     rowget+=1
	   end
	end
	#Getting the total ammount to be paid in this bulk entry for savings account
	def get_ammount_savings(accountno, act_date_get)
	    search_res_saving =$dbh.real_query("select deposit from account_actions_date a, savings_account sa where a.account_id = sa.account_id and sa.account_id =  "+accountno +" and a.action_date = '"+act_date_get+"' and payment_status = 0") 	     
	    dbresult_saving = search_res_saving.fetch_row.to_a
        row_saving=search_res_saving.num_rows()
        if row_saving == 0 then
         @@total_ammount= Float(0.0)
         else
         @@total_ammount= dbresult_saving[0]	     
        end 
        savings_account_check_ammount()
	    
	end
	#checking for the loan accounts activated for the specific client	 
	def search_product_loan(cust_id,act_date)
	   
	   search_res=$dbh.real_query("select a.account_id,po.prd_offering_short_name from account a,account_actions_date ad,prd_offering po,loan_account la where a.account_state_id in (5,9) and a.customer_id ="+cust_id+" and a.customer_id=ad.customer_id and a.account_id = ad.account_id and la.account_id=a.account_id and la.prd_offering_id=po.prd_offering_id and ad.action_date='"+act_date+"' order by po.prd_offering_short_name")
       dbresult1=search_res.fetch_row.to_a
       row1=search_res.num_rows()
       rowcount=-1
       if row1==0 then
        $logger.log_results("No Loan Accounts Activated as of this meeting date",@@cdisplay_name,act_date,"passed")
       else
        while(rowcount<row1-1)
	     account_id=dbresult1[0]
	     @@short_name=dbresult1[1]
	     get_ammount(account_id,act_date)
	     dbresult1=search_res.fetch_row.to_a
	     rowcount+=1
	    end
	 end
    end
	#checking for the Savings accounts activated for the specific client    
     def search_product_savings(cust_id,act_date)
	   search_res_savings = $dbh.real_query("select a.account_id,po.prd_offering_short_name from account a,account_actions_date ad,prd_offering po,savings_account sa where a.account_state_id in (16) and a.customer_id ="+cust_id+" and a.customer_id=ad.customer_id and a.account_id = ad.account_id and sa.account_id=a.account_id and sa.prd_offering_id=po.prd_offering_id and ad.action_date='"+act_date+"' order by po.prd_offering_short_name")
       dbresult_savings=search_res_savings.fetch_row.to_a
       row1=search_res_savings.num_rows()
       rowcount=-1
       if row1==0 then
        $logger.log_results("No saving Accounts Activated as of this meeting date","Some thing",act_date,"passed")
       else
        while(rowcount<row1-1)
	     account_id=dbresult_savings[0]
	     @@short_name=dbresult_savings[1]
	     get_ammount_savings(account_id,act_date)
	     dbresult_savings=search_res_savings.fetch_row.to_a
	     rowcount+=1
	    end
	 end
    end
    
    #getting the customer id and display name for the assigned client
    def client_bulk_entry(ss_id)
    search_client=$dbh.real_query("select display_name,customer_id from customer where search_id like '"+ss_id+"%' and branch_id="+ @@office_id+" and status_id in (3,4,9,10) and customer_level_id=1 order by display_name")
    dbresult3=search_client.fetch_row.to_a 
    rowl= search_client.num_rows
    rowlc=-1
    if rowl==0 then
      $logger.log_results("No Clients Under","Group","Group","passed")
    else
    while(rowlc < rowl-1)
      @@cdisplay_name=dbresult3[0]	
      ccustomer_id=dbresult3[1]
      dbresult3=search_client.fetch_row.to_a
      search_product_loan(ccustomer_id,@@action_date)
      search_product_savings(ccustomer_id,@@action_date)
      @@countrow+=1      
      rowlc+=1
    end
    end
    end
    #Getting the action date,serach_id and calling the methods as per this Action date and center_id  
	def bulk_entry(type_val)
	   @@type_ammount=type_val
	   dbquery("select max(action_date) from account_actions_date where customer_id ="+@@center_id+" and action_date <= current_date")
	   @@action_date=dbresult[0]
	   dbquery("select search_id from customer where customer_id="+@@center_id)
	   @@search_id=dbresult[0]
	   dbquery("select display_name,customer_id,search_id from customer where search_id like '"+@@search_id+"%' and branch_id="+ @@office_id+" and status_id in (3,4,9,10) and customer_level_id=2 order by display_name")
	   rowc=-1
	   @@countrow=0
	   if $row==0 then
	   $logger.log_results("No groups Under",@@center_id,@@center_id,"passed")
	   client_next_data(search_id)
	   else
	   while (rowc < $row-1) 
       display_name=dbresult[0]	
       customer_id=dbresult[1]
       s_id=dbresult[2]
       client_bulk_entry(s_id)
       search_product_loan(customer_id,@@action_date)
       search_product_savings(customer_id,@@action_date)
       get_next_data
       preview_submit
	   @@countrow+=1
	   rowc+=1
	   
	   end
	   end
	   
	end
	#checking the preview and submit button actions
	def preview_submit()
	
	   if @@type_ammount==1 then
	   begin
	   $ie.button(:value,"Preview").click
	   assert($ie.contains_text("The amount entered for")) and assert($ie.contains_text("is invalid. Please enter exact amount or zero."))
	   $logger.log_results("The ammount which you entered","","not correct","passed")
	   rescue Test::Unit::AssertionFailedError=>e
 	   $logger.log_results("No Text","Found","","Failed") 	   
	   end 
	   else
	   $ie.button(:value,"Preview").click
	   $ie.button(:value,"Submit").click
	   
	   end
	end
	#Testing the same with different set of values
	def setvalue_diff(typevalue)
	   type_val=typevalue
	   link_check("Enter Collection Sheet Data")
	   check_errormsg_all
	   err_msg_with_only_BOselected
	   err_msg_with_BOandLOselected
	   err_msg_with_BO_LO_Center_selected
       msg_no_collection_due
       bulk_entry(type_val)
	end
	
	def clean()
	   $ie.link(:text,'Logout').click
        clean_up()
	end
end