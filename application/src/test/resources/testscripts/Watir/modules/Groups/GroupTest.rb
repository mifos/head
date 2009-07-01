require 'modules/common/TestClass'

class GroupCreateEdit < TestClass
  
  #connecting to database and getting the searchID for office
  
  def database_connection()
    db_connect()
    dbquery("select o.search_id,o.office_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
    @@search_id=dbresult[0]
    dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=82 and locale_id=1")
    @@lookup_name_client=dbresult[0]
    dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=83 and locale_id=1")
    @@lookup_name_group=dbresult[0]
    dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=84 and locale_id=1")
    @@lookup_name_center=dbresult[0]
  end
  #reading the values form excel files
  
  def read_group_values(rowid,sheetid)
    if sheetid==1 then
      @groupname=arrval[rowid+=1]
      #added by Dilip so that i can access original groupname in the function create_group_mandatary_forfees()
      #also no need of changing the excel sheet for every run
      @groupname1=@groupname+Time.now.strftime("%d%m%Y%H%M%S")
      @customfield=arrval[rowid+=1].to_i.to_s
      @statusname=arrval[rowid+=1]
    elsif sheetid==2 then
      @groupname=arrval[rowid+=1]
      @groupname1=@groupname+Time.now.strftime("%d%m%Y%H%M%S")
      @gdate=arrval[rowid+=1].to_i.to_s
      @gmonth=arrval[rowid+=1].to_i.to_s
      @gyear=arrval[rowid+=1].to_i.to_s
      @externalid=arrval[rowid+=1]
      @address1=arrval[rowid+=1]
      @address2=arrval[rowid+=1]
      @address3=arrval[rowid+=1]
      @city=arrval[rowid+=1]
      @state=arrval[rowid+=1]
      @country=arrval[rowid+=1]
      @pcode=arrval[rowid+=1].to_i.to_s
      @phone=arrval[rowid+=1].to_i.to_s
      @customfield=arrval[rowid+=1].to_i.to_s
      @statusname=arrval[rowid+=1]
      @edit_gname=arrval[rowid+=1]
      #added by Dilip
      @edit_gname1=@edit_gname+Time.now.strftime("%d%m%Y%H%M%S")
      @edit_externalid=arrval[rowid+=1]
      @edit_address1=arrval[rowid+=1]
      @edit_address2=arrval[rowid+=1]
      @edit_address3=arrval[rowid+=1]
      @edit_city=arrval[rowid+=1]
      @edit_state=arrval[rowid+=1]
      @edit_country=arrval[rowid+=1]
      @edit_pcode=arrval[rowid+=1].to_i.to_s
      @edit_phone=arrval[rowid+=1].to_i.to_s
      @edit_customfield=arrval[rowid+=1].to_i.to_s
    end
    if @statusname=="partial" then
      @statusname="Save for later"
    elsif @statusname=="pending"
      @statusname="Submit for approval"
    end
  end
  def Groupname()
    @groupname1
  end
  def Gdate()
    @gdate
  end
  def Gmonth()
    @gmonth
  end
  
  def Gyear()
    @gyear
  end
  
  def Externalid()
    @externalid
  end
  def Address1()
    @address1
  end
  def Address2()
    @address2
  end
  
  def Address3()
    @address3
  end
  def City()
    @city
  end
  def State()
    @state
  end
  def Country()
    @country
  end
  def Pcode()
    @pcode
  end
  def Phone()
    @phone
  end
  def Customfield()
    @customfield
  end
  def Statusname()
    @statusname
  end
  def Edit_gname
    @edit_gname1
  end
  def Edit_externalid
    @edit_externalid
  end
  def Edit_address1
    @edit_address1
  end
  def Edit_address2
    @edit_address2
  end
  def Edit_address3
    @edit_address3
  end
  def Edit_city()
    @edit_city
  end
  def Edit_state()
    @edit_state
  end
  def Edit_country()
    @edit_country
  end
  def Edit_pcode()
    @edit_pcode
  end
  def Edit_phone()
    @edit_phone
  end
  def Edit_customfield()
    @edit_customfield
  end
  
  #logining into Mifos
  def group_login()
    begin
      start
      login($validname,$validpwd)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #calling the method load_properties from test class to read properties from properties file
  def properties_load()
    @@groupprop=load_properties("modules/propertis/GroupUIResources.properties")
    @@meetingprop=load_properties("modules/propertis/Meeting.properties")
    @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
    @@menuprop=load_properties("modules/propertis/MenuResources.properties")
    @@custprop=load_properties("modules/propertis/CustomerUIResources.properties")
    @@centerprop=load_properties("modules/propertis/CenterUIResources.properties")
    @@accountprop=load_properties("modules/propertis/accountsUIResources.properties")
    @@loanprop=load_properties("modules/propertis/LoanUIResources.properties")
  end
  #Getting the lables from properties file
  
  def geting_lables_from_proprtis()
    @@createcenterlabel=@@menuprop['label.createnew']+' '+@@lookup_name_center
    @@creategrouplabel=@@menuprop['label.createnew']+' '+@@lookup_name_group
    @@createclientlabel=@@menuprop['label.createnew']+' '+@@lookup_name_client
    @@selectcentertext=@@centerprop['Center.CreateNew']+" "+@@lookup_name_group+' - '+@@centerprop['Center.SearchSelectCenterHeading']+" "+@@lookup_name_center
    @@group_select_group=@@creategrouplabel+" - "+@@groupprop['Group.enter']+" "+@@lookup_name_group+' '+@@groupprop['Group.groupinformation']
    @@group_name_msg=string_replace_message(@@groupprop['errors.mandatory'],"{0}",@@groupprop['Group.groupnameMsg'])
    @@group_custom_msg=@@groupprop['errors.requiredCustomField']
    @@group_LO_msg=@@groupprop['FormedByLoanOfficerBlankException']
    @@group_review=@@creategrouplabel+" - "+@@groupprop['Group.reviewandsubmit']
    @@group_success=@@groupprop['Group.createsuccess']+" "+@@lookup_name_group
    @@view_group_details_now=@@groupprop['Group.view']+" "+@@groupprop['Group.groupdetails']+" "+@@groupprop['Group.now']
    @@edit_group_status=@@groupprop['Group.edit']+" "+@@lookup_name_group+" "+@@groupprop['Group.status1']
    @@edit_group_information=@@groupprop['Group.edit']+" "+@@lookup_name_group+" "+@@groupprop['Group.groupinformation']
    @@performance_history=@@groupprop['Group.performancehistory']
    @@group_preview=@@groupprop['Group.preview']+" "+@@lookup_name_group+" "+@@groupprop['Group.groupinformation']
    @@change_log=@@groupprop['Group.viewchangelog']
    @@center_membership=@@groupprop['Group.edit']+" "+@@lookup_name_center+" "+@@groupprop['Group.membership']
    @@change_center_member=@@groupprop['Group.change']+" "+@@lookup_name_center+" "+@@groupprop['Group.membership']
    @@view_all_closed_accounts_group=@@groupprop['Group.viewallclosedaccounts']
    @@mandatory_search_client=@@centerprop['errors.nosearchstring']
    @@mandatory_search_client["<li>"]=""
    @@mandatory_search_client["</li>"]=""
    @@button_cancel=@@centerprop['button.cancel']
    @@button_preview=@@groupprop['button.preview']
    @@button_submit=@@groupprop['button.submit']
    @@button_search=@@groupprop['button.search']
    @@view_details=@@groupprop['Group.viewdetails']
    @@group_applypayment=@@groupprop['group.applypayment']
    @@view_all_account_activities=@@groupprop['Group.viewallactivities']
    @@mandatory_feeamount_error=@@groupprop['errors.Customer.specifyFeeAmount']
    @@miscchargeapplicable=@@accountprop['error.miscchargenotapplicable']
    @@group_applycharges=@@groupprop['group.applycharges']
    @@group_amountdue=@@groupprop['Group.amountdue']
    @@return_account_details=@@accountprop['Account.returnToAccountDetails']
    @@back_to_details_page=@@centerprop['Center.backtodetailspage']  
    @@reviewadjustment = @@accountprop['accounts.btn_reviewAdjustment']
    @@reviewtransaction = @@loanprop['loan.reviewtransaction']
    @@group_apply_adjustment = @@groupprop['Group.applyAdjustment']
    @@group_waive = @@groupprop['Group.waive']
  end
  #checking for the link Create new group in Clients&Accounts page 
  
  def check_group()
    begin
      $ie.link(:text,"Clients & Accounts").click  #clients & accounts is currently not there in properties file.Would be updated later
      assert($ie.contains_text(@@creategrouplabel))
      $logger.log_results("Link Check",@@creategrouplabel,"Create New Group","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Link Check",@@creategrouplabel,"exists","failed")
    rescue =>excp
      quit_on_error(excp)	
    end
  end
  
  #checking the Create new group link functionality in Clients&Accounts page
  
  def check_for_create_new_group_link()
    begin
      $ie.link(:text,@@creategrouplabel).click
      assert($ie.contains_text(@@selectcentertext))
      $logger.log_results("link Create new group","working","N/A","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("link Create new group","working","N/A","failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #Mandatory check when you don't enter any searh name while searching for center
  def check_mandatory_in_search_center_page()
    begin
      $ie.button(:value,@@button_search).click
      assert($ie.contains_text(@@mandatory_search_client))
      $logger.log_results("Mandatory Check When no data in search",@@mandatory_search_client,"Displaying","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check When no data in search",@@mandatory_search_client,"Not Displaying","Failed") 
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #check for center when there is no such center in database
  
  def check_center_not_existed_in_db()
    begin
      $ie.text_field(:name,"searchString").set("%^")
      $ie.button(:value,@@button_search).click
      assert($ie.contains_text("No results found"))
      $logger.log_results("Displaying No results when there is no Center Names in database","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Displaying results when there is no Center Names in database","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #check whether there is more then 10 centers exist or not
  
  def check_for_no_of_centers()
    begin
      no_of_groups=count_items("select count(*) from customer where customer_level_id=3")
      if (no_of_groups.to_i > 10)
        check_next_link_exist_while_search()
      else
        $logger.log_results("No of Centers are less than 10","N/A","N/A","Passed")
      end
    rescue =>excp
      quit_on_error(excp)
    end    
  end
  
  #Check for next link when there is more than 10 centers displayed in search
  
  def check_next_link_exist_while_search()
    begin
      $ie.text_field(:name,"searchString").set("%")
      $ie.button(:value,@@button_search).click
      assert($ie.link(:text,"Next").enabled?())
      $logger.log_results("Next link enabled when there are more than 10 centers in Db","N/A","N/A","Passed")
      click_next_link_in_search_page()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Next link does not seem to be enabled when there are more than 10 centers in Db","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  #Clicking and checking for the next link functionality in search center page
  def click_next_link_in_search_page()
    begin
      $ie.text_field(:name,"searchString").set("%")
      $ie.button(:value,@@button_search).click 
      $ie.link(:text,"Next").click
      assert($ie.contains_text("Results 11"))
      $logger.log_results("Next link","click on next link","should show the next page in search results","Passed")
      click_previous_link_in_search_page()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Next link","click on next link","show the next page of search results","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  #Clicking and checking for the previous link functionality in search center page
  def click_previous_link_in_search_page()
    begin
      $ie.link(:text,"Previous").click
      assert($ie.contains_text("Results 1"))
      $logger.log_results("Previous Button","Should Work","Working","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Previous Button","Should Work","Working","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #checking cancel button funcatiinality in select center page
  
  def select_center_cancel()
    begin
      $ie.button(:value,@@button_cancel).click
      assert($ie.contains_text(@@createcenterlabel))
      assert($ie.contains_text(@@creategrouplabel)) 
      assert($ie.contains_text(@@createclientlabel))
      $logger.log_results("Cancel button","working properly","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Cancel button","working properly","N/A","Passed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #selecting center before creating group in select center page
  
  def select_center()
    begin
      $ie.link(:text,"Clients & Accounts").click
      $ie.link(:text,@@creategrouplabel).click
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and  search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      display_name=dbresult[1]
      if (@@office_id=="1") then
        get_next_data
        @@office_id=dbresult[0]
        display_name=dbresult[1]
      end      
      dbquery("select customer_id,display_name,branch_id from customer where customer_level_id=3 and status_id=13 and branch_id="+@@office_id+" order by display_name")
      @@cdisplay_name=dbresult[1]  
      @@customer_id=dbresult[0]
      $ie.text_field(:name,"searchString").set(@@cdisplay_name)        
      $ie.button(:value,@@button_search).click
      $ie.link(:text,@@cdisplay_name).click
      $ie.wait
      assert($ie.contains_text(@@group_select_group))
      $logger.log_results("Page Enter Group Information","Should appear","appeared","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page Enter Group Information","Should appear","not appeared","failed")
    rescue =>excp
      quit_on_error(excp)    
    end      
  end
  
  #checking the all mandatory fields
  def mandatory_all()
    begin
      $ie.select_list(:name,"formedByPersonnel").select_value("")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@group_name_msg)) and  assert(@@ie.contains_text(@@group_LO_msg))and assert(@@ie.contains_text(@@group_custom_msg))
      $logger.log_results("all mandatory check ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check ","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  #checking the mandatory after entering Group name
  def mandatory_with_groupname()
    begin
      $ie.text_field(:name,"displayName").set("aaa")
      $ie.select_list(:name,"formedByPersonnel").select_value("")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@group_LO_msg))and assert($ie.contains_text(@@group_custom_msg))
      $logger.log_results("mandatory checks when Group name entered ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("mandatory checks when Group name entered ","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  #checking the mandatory after entering group name and custom filed data
  def mandatory_with_gname_addInformation()
    begin
      $ie.text_field(:name,"displayName").set("aaa")
      $ie.text_field(:name,"customField[0].fieldValue").set("111")
      $ie.select_list(:name,"formedByPersonnel").select_value("")
      $ie.button(:value,@@button_preview).click 
      assert($ie.contains_text(@@group_LO_msg)) 
      $logger.log_results("mandatory checks when group name,additional information entered","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("mandatory checks when group name,additional information entered","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #added by Dilip as part of Bug#727 
  def mandatory_with_feesselected()
    begin
      $ie.text_field(:name,"displayName").set("aaa")
      $ie.text_field(:name,"customField[0].fieldValue").set("111")
      feearr=$ie.select_list(:name,"selectedFee[0].feeId").getAllContents()
      $ie.select_list(:name,"selectedFee[0].feeId").select(feearr[1].to_s)
      $ie.text_field(:name,"selectedFee[0].amount").set("")
      $ie.button(:value,@@button_preview).click 
      assert($ie.contains_text(@@mandatory_feeamount_error)) 
      $logger.log_results("Bug#727-Mandatory checks when fee selected","Remove the Fee amount","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Bug#727-Mandatory checks when fee selected","Remove the Fee amount","NA","failed")
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  
  #checking the mandatory after entering group name, custom field data and selecting Loan officer
  def review_group_with_mandatory(gname,customfield,status)
    begin
      select_center 
      if ($validname=="mifos") then
        dbquery("select personnel_id,display_name from personnel where office_id="+@@office_id)
        @@personnel_id=dbresult[0]
        @@loan_officer=dbresult[1]
      else
        dbquery("select personnel_id,display_name from personnel where login_name='"+$validname+"'")
        @@personnel_id=dbresult[0]
        @@loan_officer=dbresult[1]
      end
      $ie.text_field(:name,"displayName").set(gname)
      custom_config(customfield)
      #$ie.text_field(:name,"customField[0].fieldValue").set(customfield)
      #$ie.select_list(:name,"formedByPersonnel").select_value(@@personnel_id)
      $ie.button(:value,@@button_preview).click 
      assert($ie.contains_text(@@group_review)) 
      $logger.log_results("mandatory checks when group name,Formed by,additional information entered","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("mandatory checks when group name,Formed by,additional information entered","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def custom_config(ncustom)
    begin
      search_group=$dbh.real_query("SELECT entity_id FROM custom_field_definition where entity_type=12 and mandatory_flag=1")
      dbresult1=search_group.fetch_row.to_a
      rowl= search_group.num_rows
      rowc=0
      while(rowc < rowl)
        @@entity_id=dbresult1[0]
        if @@entity_id=="59" then
          insert_data_in_to_first_custom_field(ncustom)
        end
        dbresult1=search_group.fetch_row.to_a
        rowc+=1
      end
    rescue =>excp
      quit_on_error(excp)        
    end
  end
  
  def insert_data_in_to_first_custom_field(ncustom)
    begin
      $ie.text_field(:name,"customField[0].fieldValue").set(ncustom)
    rescue =>excp
      quit_on_error(excp)
    end    
  end
  
  def review_cancel()
    begin
      $ie.button(:value,@@button_cancel).click
      assert($ie.contains_text(@@createcenterlabel)) 
      assert($ie.contains_text(@@creategrouplabel)) 
      assert($ie.contains_text(@@createclientlabel))
      $logger.log_results("Cancel Funcationality from Review&amp;submit","working properly","N/A","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Cancel Funcationality from Review&amp;submit","working properly","N/A","failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def create_group_with_mandatory(gname,customfield,status)
    begin
      review_group_with_mandatory(gname,customfield,status)
      $ie.button(:value,status).click
      assert($ie.contains_text(@@group_success))
      $logger.log_results("Group created successfully","N/A","N/A","Passed")
      go_to_detailspage(gname)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Group created successfully","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #added by Dilip as the function to by pass the go_to_details function on 2/10/2006
  #this function creates a group with a pending status
  def create_group_with_mandatory_forfees(gname,customfield,status)
    begin
      review_group_with_mandatory(gname,customfield,status)
      $ie.button(:value,status).click
      assert($ie.contains_text(@@group_success))
      $logger.log_results("Group created successfully","N/A","N/A","Passed")
      $ie.link(:text,@@view_group_details_now).click
      #$ie.link(:text,@@edit_group_status).click
      #change_status_pending
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Group created successfully","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end #end of function
  
  
  def go_to_detailspage(gname)
    begin
      $ie.link(:text,@@view_group_details_now).click
      begin
        assert($ie.contains_text(@@edit_group_status))
        $logger.log_results("Opened Edit group details page","N/A","N/A","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Opened Edit group details page","N/A","N/A","passed")
      end
      change_status(gname)
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def change_status(gname)
    begin
      dbquery("select status_id from customer where display_name='"+gname+"'")
      status_id=dbresult[0]
      if status_id=="7" then
        change_status_pending()
      elsif status_id=="8" then
        change_status_active()
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def change_status_pending()
    begin
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=7")
      @@status_partial_name=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=8")
      @@status_pending_name=dbresult[0]
      $ie.link(:text,@@edit_group_status).click
      $ie.radio(:name,"newStatusId","8").set
      $ie.text_field(:name,"notes").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      begin
        assert($ie.contains_text(@@performance_history))and assert($ie.contains_text(@@status_pending_name)) 
        $logger.log_results("Status changed to Pending","NA","NA","passed") 
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Status changed to Pending","NA","NA","failed") 
      end
      view_change_log_pending()
    rescue =>excp
      quit_on_error(excp)    
    end
  end 
  
  def change_status_active()
    begin
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=9")
      @@status_active_name=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=8")
      @@status_pending_name=dbresult[0]
      $ie.link(:text,@@edit_group_status).click
      $ie.radio(:name,"newStatusId","9").set
      $ie.text_field(:name,"notes").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      begin
        assert($ie.contains_text(@@performance_history))and assert($ie.contains_text(@@status_active_name))
        $logger.log_results("Status changed to Active","NA","NA","passed") 
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Status changed to Active","NA","NA","failed") 
      end
      view_change_log_active()
      view_all_closed_accounts()
    rescue =>excp
      quit_on_error(excp)    
    end
  end    
  
  def create_group_all_data(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
    begin
      @@name_group=gname
      $ie.text_field(:name,"displayName").set(gname)
      $ie.text_field(:name,"externalId").set(externalid)
      $ie.checkbox(:name,"trained","1").set
      $ie.text_field(:name,"trainedDateDD").set(gdate)
      $ie.text_field(:name,"trainedDateMM").set(gmonth)
      $ie.text_field(:name,"trainedDateYY").set(gyear)
      $ie.text_field(:name,"address.line1").set(address1)
      $ie.text_field(:name,"address.line2").set(address2)
      $ie.text_field(:name,"address.line3").set(address3)
      $ie.text_field(:name,"address.city").set(city)
      $ie.text_field(:name,"address.state").set(state)
      $ie.text_field(:name,"address.country").set(country)
      $ie.text_field(:name,"address.zip").set(pcode)
      $ie.text_field(:name,"address.phoneNumber").set(phone)
      #$ie.text_field(:name,"customField[0].fieldValue").set(custom1)
      custom_config(custom1)
      fee_select_one_by_one()
      create_preview(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def create_preview(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status)
    begin
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@group_review))and assert($ie.contains_text(gname)) and assert($ie.contains_text(address1)) and //
      assert($ie.contains_text(address2)) and assert($ie.contains_text(address3)) and assert($ie.contains_text(city)) //
      and assert($ie.contains_text(state)) and assert($ie.contains_text(country)) and assert($ie.contains_text(pcode)) and //
      assert($ie.contains_text(phone)) and assert($ie.contains_text(custom1))
      $logger.log_results("Create Group Revie&amp;submit page diaplayed","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Create Group Revie&amp;submit page diaplayed","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def edit_group_preview(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
    begin
      $ie.button(:value,@@edit_group_information).click
      create_group_all_data(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
      begin
        assert($ie.contains_text(@@group_review)) and assert($ie.contains_text(gname))
        $logger.log_results("Group Creation","Review&amp;submit page","opened","passed")
      rescue  Test::Unit::AssertionFailedError=>e
        $logger.log_results("Group Creation","Review&amp;submit page","opened","failed")
      end
      group_submit(gname,status)
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def group_submit(gname,status)
    begin
      $ie.button(:value,status).click
      begin
        assert($ie.contains_text(@@group_success)) and assert($ie.contains_text(@@name_group))
        $logger.log_results("Group Creation","successfull page","opened","passed")
      rescue  Test::Unit::AssertionFailedError=>e
        $logger.log_results("Group Creation","successfull page","opened","failed")
      end
      go_to_detailspage(gname)
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def db_check(gname)
    begin
      db_center=$dbh.real_query("select * from customer where display_name='"+gname+"'")
      rowcount= db_center.num_rows
      if rowcount==0 then
        $logger.log_results("Data Base Check","NA","NA","failed") 
      else
        $logger.log_results("Data Base Check","NA","NA","passed")  
      end
    rescue =>excp
      quit_on_error(excp)      
    end   
  end
  
  
  def edit_group_data(gname)
    begin
      dbquery("select global_cust_num from customer where display_name='"+gname+"'")
      global_account_number=dbresult[0]
      $ie.link(:text,"Clients & Accounts").click
      $ie.text_field(:name,"searchString").set(global_account_number)
      $ie.button(@@button_search).click
      $ie.button(@@button_search).click
      search_name=gname+": ID "+global_account_number.to_s
      $ie.link(:text,search_name).click
      assert($ie.contains_text(@@performance_history))
      $logger.log_results("Group Details page","Open","opened","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Group Details page","Open","not opened","failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def edit_group_from_details_link(gname)
    begin
      $ie.link(:text,@@edit_group_information).click
      label_to_check=gname+" - "+@@edit_group_information
      assert($ie.contains_text(label_to_check))
      $logger.log_results("Edit Group Details Page","Open","Opened","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Edit Group Details Page","Open","Not Opened","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def edit_group_from_details(gname,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1)
    begin
      $ie.text_field(:name,"displayName").set(gname)
      $ie.text_field(:name,"externalId").set(externalid)
      $ie.text_field(:name,"address.line1").set(address1)
      $ie.text_field(:name,"address.line2").set(address2)
      $ie.text_field(:name,"address.line3").set(address3)
      $ie.text_field(:name,"address.city").set(city)
      $ie.text_field(:name,"address.state").set(state)
      $ie.text_field(:name,"address.country").set(country)
      $ie.text_field(:name,"address.zip").set(pcode)
      $ie.text_field(:name,"address.phoneNumber").set(phone)
      #$ie.text_field(:name,"customField[0].fieldValue").set(custom1)
      custom_config(custom1)
      click_preview(gname,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1)
    rescue =>excp
      quit_on_error(excp) 
    end   
  end
  
  def fee_select_one_by_one()
    begin
      #search_res=$dbh.real_query("SELECT fee_id FROM fees where category_id in (1,3) and status=1 and default_admin_fee='No'")
      search_res=$dbh.real_query("select a.fee_id,a.fee_name,c.recurrence_id from fees a,fee_frequency b,recurrence_detail c where a.fee_id=b.fee_id and (b.frequency_meeting_id=c.meeting_id or b.frequency_meeting_id is null )and c.recurrence_id=(select recurrence_id from recurrence_detail a ,customer_meeting cm where a.meeting_id=cm.meeting_id and customer_id = "+@@customer_id+") and a.fee_id not in (select fee_id from feelevel) and a.category_id in (1,3) and status=1 group by a.fee_id")      
      dbresult1=search_res.fetch_row.to_a
      row1=search_res.num_rows()
      rowf=0
      number=3
      if row1=="0" then
        $looger.log_results("No Fess created for the group","","","Passed")
      elsif row1 < number then
        while (rowf < row1)
          fee_id=dbresult1[0]
          $ie.select_list(:name,"selectedFee["+String(rowf)+"].feeId").select_value(fee_id)
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
  
  def click_preview(gname,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1)
    begin
      $ie.button(:value,@@button_preview).click
      label_to_check=gname+" - "+@@group_preview
      begin
        assert($ie.contains_text(label_to_check))and assert($ie.contains_text(gname)) and assert($ie.contains_text(address1)) and //
        assert($ie.contains_text(address2)) and assert($ie.contains_text(address3)) and assert($ie.contains_text(city)) //
        and assert($ie.contains_text(state)) and assert($ie.contains_text(country)) and assert($ie.contains_text(pcode)) and //
        assert($ie.contains_text(phone)) and assert($ie.contains_text(custom1))
        $logger.log_results("Edit Group Review&amp;submit page diaplayed","N/A","N/A","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Edit Group Review&amp;submit page diaplayed","N/A","N/A","Failed")
      end
      $ie.button(:value,@@button_submit).click
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def view_change_log_pending()
    begin
      $ie.link(:text,@@change_log).click
      assert($ie.contains_text(@@status_pending_name)) and assert($ie.contains_text(@@status_partial_name))
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed")    
      $ie.button(:value,@@back_to_details_page).click()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def view_change_log_active()
    begin
      $ie.link(:text,@@change_log).click
      assert($ie.contains_text(@@status_pending_name)) and assert($ie.contains_text(@@status_active_name))
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed")    
      $ie.button(:value,@@back_to_details_page).click()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def edit_center_membership(gname)
    begin
      $ie.link(:text,@@center_membership).click
      center_membership_select_center=gname+"- "+@@change_center_member
      assert($ie.contains_text(center_membership_select_center))
      $logger.log_results("Change Center Membership details page","N/A","N/A","Passed")    
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Change Center Membership details page","N/A","N/A","Failed")    
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def edit_center_membership_select_center(gname)
    begin
      dbquery("select display_name from customer where customer_level_id=3 and status_id=13 and branch_id="+@@office_id+" and display_name not in ('"+@@cdisplay_name+"') order by display_name")
      centername=dbresult[0]
      $ie.text_field(:value,"").set(centername)
      $ie.button(:name,"").click
      $ie.link(:text,centername).click
      $ie.button(:value,@@button_submit).click
      assert($ie.contains_text(@@change_log))
      $logger.log_results("Center Membership","Should Change Successfully","Changed","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Center Membership","Should Change Successfully","Changed","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def view_all_closed_accounts()
    begin
      begin
        assert($ie.contains_text(@@view_all_closed_accounts_group))
        $logger.log_results("View all closed accounts link exist","NA","NA","passed")
      rescue  Test::Unit::AssertionFailedError=>e
        $logger.log_results("View all closed accounts link does not exist","NA","NA","failed")  
      end
      $ie.link(:text,@@view_all_closed_accounts_group).click
      $ie.button(:value,@@custprop['label.backtodetailspage']).click
      begin
        assert($ie.contains_text(@@performance_history))
        $logger.log_results("View all closed accounts link working","NA","NA","passed")
      rescue  Test::Unit::AssertionFailedError=>e
        $logger.log_results("View all closed accounts link working","NA","NA","failed")   
      end
    rescue =>excp
      quit_on_error(excp)        
    end
  end
  
  def Activate_Group()
    begin
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and  search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      display_name=dbresult[1]
      if (@@office_id=="1") then
        get_next_data
        @@office_id=dbresult[0]
        display_name=dbresult[1]
      end      
      @@edit_group_status=@@groupprop['Group.edit']+" "+@@lookup_name_group+" "+@@groupprop['Group.status1']
      @@button_preview=@@groupprop['button.preview']
      @@button_submit=@@groupprop['button.submit']
      @@performance_history=@@groupprop['Group.performancehistory']
      
      groupsarr= $dbh.real_query("select customer_id,global_cust_num,display_name,status_id from customer where customer_level_id=2 and status_id in (7,8) and branch_id="+@@office_id+" limit 2")
      dbresult1=groupsarr.fetch_row.to_a
      row1=groupsarr.num_rows
      rowc=0
      $ie.link(:text,"Clients & Accounts").click
      while (rowc < row1) do
        global_acct_num=dbresult1[1]
        groupstatus=dbresult1[3]
        groupname=dbresult1[2]
        $ie.link(:text,"Clients & Accounts").click
        $ie.text_field(:name,"searchString").set(global_acct_num)
        $ie.button(:value,"Search").click
        search_name=groupname+": ID "+global_acct_num
        $ie.link(:text,search_name).click
        
        if(groupstatus.to_i==7)
          change_status_pending()
          change_status_active()
        elsif(groupstatus.to_i==8)
          change_status_active()
        end  
        dbresult1=groupsarr.fetch_row.to_a
        rowc+=1
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #Added by Dilip as partof the bug#397 on 2/10/2006
  def check_feeApplied_status()
    begin
      count=count_records("Select count(global_cust_num) from Customer where Status_ID in (8)")
      if(count.to_i==0) # if  there are not groups with status pending then create one
        create_group_with_mandatory_forfees(@groupname+Time.now.strftime("%d%m%Y%H%M%S"),"351",@statusname)
        #create_group_with_mandatory_forfees("group"+Time.now.strftime("%d%m%Y%H%M%S"),"351","Submit for approval")
        apply_feestogroup
      else
        apply_feestogroup
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end # end of check_feeApplied_status function
  
  #added by Dilip on 2/10/2006.This function apply charges to inactive group and checks whether its displayed
  def apply_feestogroup()
    begin
      #FOR GROUPS
      dbquery("Select global_cust_num,Display_name,status_id,customer_id from Customer where Status_ID in (8)")
      @@global_cust_num=dbresult[0]
      @@Display_name=dbresult[1]
      @@status_id=dbresult[2]
      @@customer_id=dbresult[3]
      search_client(@@global_cust_num) 
      $ie.link(:text,@@Display_name.strip()+": ID "+@@global_cust_num).click
      $ie.link(:text,@@view_details).click
      $ie.link(:text,@@group_applycharges).click
      
      #added as part of Bug#802
      apply_Miscfees_to_inactivecustomer()
      
      feetypearr=$ie.select_list(:name,"chargeType").getAllContents()
      $ie.select_list(:name,"chargeType").select(feetypearr[1].to_s)
      #    arr=$ie.select_list(:name,"chargeType").getSelectedItems()
      $ie.button(:value,@@button_submit).click
      search_client(@@global_cust_num) 
      $ie.link(:text,@@Display_name.strip()+": ID "+@@global_cust_num).click
      table_obj=$ie.table(:index,15)
      begin
        assert(table_obj[2][0].text==@@group_amountdue+" 0.0")
        $logger.log_results("Bug#397-Fee applied to a customer","Fee applied a group which is not active","Fee should not be applied till the group is active","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Bug#397-Fee applied to a customer","Fee applied a group which is not active","Fee displayed for inactive group","failed")
      end
      change_status(@@Display_name)
      # again verifying the view details page to check whether the fees applied is displayed now.
      search_client(@@global_cust_num) 
      $ie.link(:text,@@Display_name.strip()+": ID "+@@global_cust_num).click
      table_obj=$ie.table(:index,15)
      chargeamount=gettotalfeesapplied(@@customer_id)
      begin
        assert(table_obj[2][0].text==@@group_amountdue+" "+chargeamount.to_f.to_s+"")
        $logger.log_results("Bug#397-Fee applied to a customer","Customer is made Active","Fee applied should be displayed","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Bug#397-Fee applied to a customer","fees is not displayed for active group","","failed")
      end      
        #added by Dilip as part of bug#716
        check_applypayment_cancel()
    rescue =>excp
      quit_on_error(excp)
    end   

  end # end of funtion apply_feestogroup
  
  #added by Dilip as part of the bug# 802
  def apply_Miscfees_to_inactivecustomer()
    begin
      $ie.select_list(:name,"chargeType").select_value("-1")
      $ie.text_field(:name,"chargeAmount").set("34")
      $ie.button(:value,@@button_submit).click
      assert($ie.contains_text(@@miscchargeapplicable))
      $logger.log_results("Bug#802-Misc Fee applied to a inactive customer","Misc Fee applied to a group which is not active","Should be not allowed to apply fees","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Bug#802-Misc Fee applied to a inactive customer","Misc Fee applied to a group which is not active","Allowed to apply fees","failed")
    rescue=>excp
      quit_on_error(excp)
    end
  end
  
  #added by Dilip on 3/10/2006 as part of bug 716
  def check_applypayment_cancel()
    begin
      $ie.link(:text,@@view_details).click
      $ie.link(:text,@@group_applypayment).click
      $ie.button(:value,@@button_cancel).click
      assert($ie.contains_text(@@view_all_account_activities))
      $logger.log_results("Bug#716-Click Cancel on Apply Payment","Cancel clicked","The Group charges page should be displayed","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Bug#716-Click Cancel on Apply Payment","Cancel clicked","The Group charges page not displayed","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #added by Dilip.This function returns the count for any query.
  def count_records(query)
    dbquery(query)
    count=dbresult[0]
    return count.to_i
  end
  
  #To get the  total fees applied to a customer added by Dilip as part of bug#397
  
  def gettotalfeesapplied(customerid)
    dbquery("select sum(account_fee_amnt) from account a,account_fees af where a.account_id=af.account_id and a.customer_id="+customerid+" group by a.account_id")
    totalfees=dbresult[0]
    return totalfees
  end
  
  #added by Dilip. This function searches for any customer number given as input 
  def search_client(custnum)
    begin
      $ie.link(:text,"Clients & Accounts").click
      $ie.text_field(:name,"searchString").set(custnum)
      $ie.button(:value,"Search").click
      #workaround  will be removed
      $ie.button(:value,"Search").click
      #***End***
      assert($ie.contains_text(custnum))
      $logger.log_results("Search results","Should work","Working","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Search results","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end #end of search_client method
  
  def check_waive_amount()
    begin
    dbquery("select global_cust_num,display_name from customer where customer_level_id=2 and status_id=9")
    customer_number=dbresult[0]
    customer_name=dbresult[1]
    search_client(customer_number)
    $ie.link(:text,customer_name.strip()+": ID "+customer_number).click
    $ie.link(:text,@@view_details).click
    
      #applying Misc fees 
      apply_charges()

      #applying payment
      apply_payment()

      #Apply adjustment
      apply_adjustment()
      
     $ie.link(:text,@@group_waive).click()
      begin
         assert($ie.contains_text(@@group_applypayment))
         $logger.log_results("Bug#964-Waive Amount","Should work","Working","Passed")
         rescue Test::Unit::AssertionFailedError=>e
         $logger.log_results("Bug#964-Waive Amount","NA","NA","failed")
         rescue =>excp
         quit_on_error(excp)   
       end 
    end
  end
  
  def apply_adjustment()
    begin
     $ie.link(:text,@@group_apply_adjustment).click             
    $ie.checkbox(:name,"adjustcheckbox").set(set_or_clear=true) 
    $ie.text_field(:name,"adjustmentNote").set("Amount Adjusted")
    $ie.button(:value,@@reviewadjustment).click
    $ie.button(:value,@@button_submit).click

    end
  end
  
  def apply_payment()
    begin
    $ie.link(:text,@@group_applypayment).click
    $ie.select_list(:name,"paymentTypeId").select("Cash")
    $ie.button(:value,@@reviewtransaction).click  
    $ie.button(:value,@@button_submit).click
    end
  end
  
  def apply_charges()
    begin
      $ie.link(:text,@@group_applycharges).click
      $ie.select_list(:name,"chargeType").select_value("-1")
      $ie.text_field(:name,"chargeAmount").set("34")
      $ie.button(:value,@@button_submit).click
    end
  end
  
  
end #end of class

class GroupTest
  groupobject=GroupCreateEdit.new
  
  groupobject.group_login
  groupobject.database_connection
  groupobject.properties_load
  groupobject.geting_lables_from_proprtis
  groupobject.check_group
  groupobject.check_for_create_new_group_link
  groupobject.check_mandatory_in_search_center_page
  groupobject.check_center_not_existed_in_db
  groupobject.check_for_no_of_centers
  groupobject.select_center_cancel
  groupobject.select_center
  groupobject.mandatory_all
  groupobject.mandatory_with_groupname
  groupobject.mandatory_with_gname_addInformation
  #added as part of bug#727
  groupobject.mandatory_with_feesselected
  
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  groupobject.open(filename,1)
  rowid=-1
  while(rowid<$maxrow*$maxcol-1)
    groupobject.read_group_values(rowid,1)
    groupobject.review_group_with_mandatory(groupobject.Groupname,groupobject.Customfield,groupobject.Statusname)
    groupobject.review_cancel
    groupobject.create_group_with_mandatory(groupobject.Groupname,groupobject.Customfield,groupobject.Statusname)
    groupobject.db_check(groupobject.Groupname)
    rowid+=$maxcol
  end
  groupobject.open(filename,2)
  rowid=-1
  while(rowid<$maxrow*$maxcol-1)
    groupobject.read_group_values(rowid,2)
    groupobject.select_center 
    groupobject.create_group_all_data(groupobject.Groupname,groupobject.Gdate,groupobject.Gmonth,\
    groupobject.Gyear,groupobject.Externalid,groupobject.Address1,\
    groupobject.Address2,groupobject.Address3,groupobject.City,\
    groupobject.State,groupobject.Country,groupobject.Pcode,groupobject.Phone,\
    groupobject.Customfield,groupobject.Statusname) 
    groupobject.edit_group_preview(groupobject.Groupname,groupobject.Gdate,groupobject.Gmonth,\
    groupobject.Gyear,groupobject.Externalid,groupobject.Address1,\
    groupobject.Address2,groupobject.Address3,groupobject.City,\
    groupobject.State,groupobject.Country,groupobject.Pcode,groupobject.Phone,\
    groupobject.Customfield,groupobject.Statusname)
    groupobject.db_check(groupobject.Groupname)
    groupobject.edit_group_data(groupobject.Groupname)
    groupobject.edit_group_from_details_link(groupobject.Groupname)
    groupobject.edit_group_from_details(groupobject.Edit_gname,groupobject.Edit_externalid,groupobject.Edit_address1,\
    groupobject.Edit_address2,groupobject.Edit_address3,groupobject.Edit_city,\
    groupobject.Edit_state,groupobject.Edit_country,groupobject.Edit_pcode,groupobject.Edit_phone,\
    groupobject.Edit_customfield)  
   # groupobject.view_all_closed_accounts
    groupobject.edit_center_membership(groupobject.Edit_gname)
    groupobject.edit_center_membership_select_center(groupobject.Edit_gname)
    rowid+=$maxcol
  end
  groupobject.Activate_Group
  
  #added by Dilip as part of bug#397
  groupobject.check_feeApplied_status()
  
  #added as part of Bug#964
  groupobject.check_waive_amount()
  
  groupobject.mifos_logout()
end
