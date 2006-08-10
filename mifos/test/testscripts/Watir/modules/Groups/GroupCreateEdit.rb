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
class GroupCreateEdit < TestClass
  #connecting to database
  def database_connection()
    db_connect()
    dbquery("select o.search_id,o.office_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
    @@search_id=dbresult[0]
  end
  #logining into Mifos
  def group_login()
		start
		login($validname,$validpwd)
  end
  #checking for the link Create new group 
  def check_group()
		begin
		$ie.link(:text,"Clients & Accounts").click
		assert($ie.contains_text("Create new Group"))
		$logger.log_results("Link Check","Create New group","Create New Group","passed")
		rescue =>e
		$logger.log_results("Link Check","Create New group","exists","failed")
		end
  end
  #checking the Create new group link functionality
  def select_center_check()
    begin
    $ie.link(:text,"Create new Group").click
    assert($ie.contains_text($group_select_center))
    $logger.log_results("link Create new group","working","N/A","passed")
    rescue=>e
    $logger.log_results("link Create new group","working","N/A","failed")
    end
  end
  #checking cancel button funcatiinality
  def select_center_cancel()
    begin
      $ie.button(:value,"Cancel").click
      assert($ie.contains_text($clicnts_Accounts_msg))
      $logger.log_results("Cancel button","working properly","N/A","Passed")
      rescue=>e
      $logger.log_results("Cancel button","working properly","N/A","Passed")
    end
  end
  #selecting center before creating group
  def select_center()
    begin
    $ie.link(:text,"Create new Group").click
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
        $ie.text_field(:name,"searchNode(searchString)").set(@@cdisplay_name)        
        $ie.button(:value,"Search").click
        $ie.link(:text,@@cdisplay_name).click
        $ie.wait
        assert($ie.contains_text($group_select_group))
        $logger.log_results("Page Enter Group Information","Should appear","appeared","passed")
        rescue=>e
        $logger.log_results("Page Enter Group Information","Should appear","not appeared","failed")
  end      
  end
  #checking the all mandatory fields
    def mandatory_all()
    begin
    $ie.select_list(:name,"customerFormedById").select_value("")
    $ie.button(:value,"Preview").click
    assert($ie.contains_text($group_name_msg)) and  assert($ie.contains_text($group_LO_msg))and assert($ie.contains_text($group_custom_msg))
    $logger.log_results("all mandatory check ","NA","NA","passed")
    rescue=>e
    $logger.log_results("all mandatory check ","NA","NA","failed")
    end
  end
  #checking the mandatory after entering Group name
  def mandatory_with_groupname()
    begin
    $ie.text_field(:name,"displayName").set("aaa")
    $ie.select_list(:name,"customerFormedById").select_value("")
    $ie.button(:value,"Preview").click
    assert($ie.contains_text($group_LO_msg))and assert($ie.contains_text($group_custom_msg))
    $logger.log_results("mandatory checks when Group name entered ","NA","NA","passed")
    rescue=>e
    $logger.log_results("mandatory checks when Group name entered ","NA","NA","failed")
    end
  end
    #checking the mandatory after entering group name and custom filed data
  def mandatory_with_gname_addInformation()
    begin
     $ie.text_field(:name,"displayName").set("aaa")
     $ie.text_field(:name,"customField[0].fieldValue").set("111")
     $ie.select_list(:name,"customerFormedById").select_value("")
     $ie.button(:value,"Preview").click 
     assert($ie.contains_text($group_LO_msg)) 
     $logger.log_results("mandatory checks when group name,additional information entered","NA","NA","passed")
     rescue=>e
     $logger.log_results("mandatory checks when group name,additional information entered","NA","NA","failed")
    end
  end
  
    #checking the mandatory after entering group name, custom field data and selecting Loan officer
  def review_group_with_mandatory(gname,customfield,status)
    begin
     select_center 
     puts @@office_id
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
     $ie.text_field(:name,"customField[0].fieldValue").set(customfield)
     $ie.select_list(:name,"customerFormedById").select_value(@@personnel_id)
     $ie.button(:value,"Preview").click 
     assert($ie.contains_text($group_review)) 
     $logger.log_results("mandatory checks when group name,Formed by,additional information entered","NA","NA","passed")
     rescue=>e
     $logger.log_results("mandatory checks when group name,Formed by,additional information entered","NA","NA","failed")
    end
  end
  def review_cancel()
  begin
    $ie.button(:value,"Cancel").click
    assert($ie.contains_text($clicnts_Accounts_msg))
    $logger.log_results("Cancel Funcationality from Review&submit","working properly","N/A","passed")
    rescue=>e
    $logger.log_results("Cancel Funcationality from Review&submit","working properly","N/A","failed")
  end
  end
  def create_group_with_mandatory(gname,customfield,status)
    begin
    review_group_with_mandatory(gname,customfield,status)
    $ie.button(:value,status).click
    assert($ie.contains_text($group_success))
    $logger.log_results("Group created successfully","N/A","N/A","Passed")
    go_to_detailspage(gname)
    rescue=>e
    $logger.log_results("Group created successfully","N/A","N/A","Failed")
    end
  end
  def go_to_detailspage(gname)
  begin
    $ie.link(:text,"View Group details now").click
    assert($ie.contains_text("Edit Group status"))
    $logger.log_results("Opened Edit group details page","N/A","N/A","passed")
    change_status(gname)
    rescue=>e
    $logger.log_results("Opened Edit group details page","N/A","N/A","passed")
  end
  end
  def change_status(gname)
    dbquery("select status_id from customer where display_name='"+gname+"'")
    status_id=dbresult[0]
    if status_id=="7" then
    change_status_pending()
    elsif status_id=="8" then
    change_status_active()
    end
  end
  def change_status_pending()
    begin
    $ie.link(:text,"Edit Group status").click
    $ie.radio(:name,"statusId","8").set
    $ie.text_field(:name,"customerNote.comment").set("AAAAA")
    $ie.button(:value,"Preview").click
    $ie.button(:value,"Submit").click
    assert($ie.contains_text("Performance history "))and assert($ie.contains_text("Pending Approval")) 
    $logger.log_results("Status changed to Pending","NA","NA","passed") 
    view_change_log_pending()
    rescue=>e
    $logger.log_results("Status changed to Pending","NA","NA","failed") 
    end
  end    
  def change_status_active()
    begin
    $ie.link(:text,"Edit Group status").click
    $ie.radio(:name,"statusId","9").set
    $ie.text_field(:name,"customerNote.comment").set("AAAAA")
    $ie.button(:value,"Preview").click
    $ie.button(:value,"Submit").click
    assert($ie.contains_text("Performance history "))and assert($ie.contains_text("Active"))
    $logger.log_results("Status changed to Active","NA","NA","passed") 
    view_change_log_active()
    rescue=>e
    $logger.log_results("Status changed to Active","NA","NA","failed") 
    end
  end    
 def create_group_all_data(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
  
  @@name_group=gname
  $ie.text_field(:name,"displayName").set(gname)
  $ie.select_list(:name,"customerFormedById").select_value(@@personnel_id)
  $ie.text_field(:name,"externalId").set(externalid)
  $ie.checkbox(:name,"trained","1").set
  $ie.text_field(:name,"trainedDateDD").set(gdate)
  $ie.text_field(:name,"trainedDateMM").set(gmonth)
  $ie.text_field(:name,"trainedDateYY").set(gyear)
  $ie.text_field(:name,"customerAddressDetail.line1").set(address1)
  $ie.text_field(:name,"customerAddressDetail.line2").set(address2)
  $ie.text_field(:name,"customerAddressDetail.line3").set(address3)
  $ie.text_field(:name,"customerAddressDetail.city").set(city)
  $ie.text_field(:name,"customerAddressDetail.state").set(state)
  $ie.text_field(:name,"customerAddressDetail.country").set(country)
  $ie.text_field(:name,"customerAddressDetail.zip").set(pcode)
  $ie.text_field(:name,"customerAddressDetail.phoneNumber").set(phone)
  $ie.text_field(:name,"customField[0].fieldValue").set(custom1)
  fee_select_one_by_one()
  create_preview(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status)
 end
 def create_preview(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status)
 begin
  $ie.button(:value,"Preview").click
  assert($ie.contains_text($group_review))and assert($ie.contains_text(gname)) and assert($ie.contains_text(address1)) and //
  assert($ie.contains_text(address2)) and assert($ie.contains_text(address3)) and assert($ie.contains_text(city)) //
  and assert($ie.contains_text(state)) and assert($ie.contains_text(country)) and assert($ie.contains_text(pcode)) and //
  assert($ie.contains_text(phone)) and assert($ie.contains_text(custom1))
  $logger.log_results("Create Group Revie&submit page diaplayed","N/A","N/A","Passed")
  rescue=>e
  $logger.log_results("Create Group Revie&submit page diaplayed","N/A","N/A","Failed")
  end
 end
 def edit_group_preview(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
    begin
      $ie.button(:value,"Edit Group information").click
      create_group_all_data(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
      assert($ie.contains_text($group_review)) and assert($ie.contains_text(gname))
      $logger.log_results("Group Creation","Review&submit page","opened","passed")
      group_submit(gname,status)
      rescue =>e
      $logger.log_results("Group Creation","Review&submit page","opened","failed")
    end
 end
 def group_submit(gname,status)
    begin
    $ie.button(:value,status).click
    assert($ie.contains_text($group_success)) and assert($ie.contains_text(@@name_group))
    $logger.log_results("Group Creation","successfull page","opened","passed")
    go_to_detailspage(gname)
    rescue =>e
    $logger.log_results("Group Creation","successfull page","opened","failed")
    end
  end
 def db_check(gname)
    db_center=$dbh.real_query("select * from customer where display_name='"+gname+"' and loan_officer_id="+@@personnel_id+" and branch_id="+@@office_id)
    rowcount= db_center.num_rows
    if rowcount==0 then
    $logger.log_results("Data Base Check","NA","NA","failed") 
    else
    $logger.log_results("Data Base Check","NA","NA","passed")  
    end   
  end
  def edit_group_data(gname)
    begin
    dbquery("select global_cust_num from customer where display_name='"+gname+"'")
    global_account_number=dbresult[0]
    $ie.link(:text,"Clients & Accounts").click
    $ie.text_field(:name,"searchNode(searchString)").set(global_account_number)
    $ie.button("Search").click
    search_name=gname+": ID "+global_account_number
    $ie.link(:text,search_name).click
    assert($ie.contains_text("Performance history"))
    $logger.log_results("Group Details page","Open","opened","passed")
    rescue=>e
    $logger.log_results("Group Details page","Open","not opened","failed")
    end
  end
  def edit_group_from_details_link()
    begin
    $ie.link(:text,$group_edit).click
    assert($ie.contains_text($group_edit_message_check))
    $logger.log_results("Edit Group Details Page","Open","Opened","Passed")
    rescue=>e
    $logger.log_results("Edit Group Details Page","Open","Not Opened","Failed")
    end
  end
  def edit_group_from_details(gname,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1)
    $ie.text_field(:name,"displayName").set(gname)
    $ie.text_field(:name,"externalId").set(externalid)
    $ie.text_field(:name,"customerAddressDetail.line1").set(address1)
    $ie.text_field(:name,"customerAddressDetail.line2").set(address2)
    $ie.text_field(:name,"customerAddressDetail.line3").set(address3)
    $ie.text_field(:name,"customerAddressDetail.city").set(city)
    $ie.text_field(:name,"customerAddressDetail.state").set(state)
    $ie.text_field(:name,"customerAddressDetail.country").set(country)
    $ie.text_field(:name,"customerAddressDetail.zip").set(pcode)
    $ie.text_field(:name,"customerAddressDetail.phoneNumber").set(phone)
    $ie.text_field(:name,"customField[0].fieldValue").set(custom1)
    click_preview(gname,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1)
  end
  def fee_select_one_by_one()
  search_res=$dbh.real_query("SELECT fee_id FROM fees where category_id in (1,3) and status=1 and default_admin_fee='No'")
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
  end
  def click_preview(gname,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1)
    begin
    $ie.button(:value,"Preview").click
    assert($ie.contains_text($group_edit_review))and assert($ie.contains_text(gname)) and assert($ie.contains_text(address1)) and //
    assert($ie.contains_text(address2)) and assert($ie.contains_text(address3)) and assert($ie.contains_text(city)) //
    and assert($ie.contains_text(state)) and assert($ie.contains_text(country)) and assert($ie.contains_text(pcode)) and //
    assert($ie.contains_text(phone)) and assert($ie.contains_text(custom1))
    $logger.log_results("Edit Group Revie&submit page diaplayed","N/A","N/A","Passed")
    $ie.button(:value,"Submit").click
    rescue=>e
    $logger.log_results("Edit Group Revie&submit page diaplayed","N/A","N/A","Failed")
    end
  end
  def view_change_log_pending()
  begin
    $ie.link(:text,$change_log).click
    assert($ie.contains_text("Status"))and assert($ie.contains_text("Application Pending Approval")) and assert($ie.contains_text("Partial Application"))
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed")    
    rescue=>e
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
  end
  end
  def view_change_log_active()
  begin
    $ie.link(:text,$change_log).click
    assert($ie.contains_text("Status"))and assert($ie.contains_text("Application Pending Approval")) and assert($ie.contains_text("Active"))
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed")    
    rescue=>e
    $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
  end
  end
  def edit_center_membership()
    begin
    $ie.link(:text,$center_membership).click
    assert($ie.contains_text($center_membership_select_center))
    $logger.log_results("Change Center Membership details page","N/A","N/A","Passed")    
    rescue=>e
    $logger.log_results("Change Center Membership details page","N/A","N/A","Failed")    
    end
  end
  def edit_center_membership_select_center()
      begin
      dbquery("select display_name from customer where customer_level_id=3 and status_id=13 and branch_id="+@@office_id+" and display_name not in ('"+@@cdisplay_name+"') order by display_name")
      centername=dbresult[0]
      $ie.text_field(:value,"").set(centername)
      $ie.button(:name,"").click
      $ie.link(:text,centername).click
      assert($ie.contains_text($center_membership_select_center_confirm))
      $logger.log_results("Change Center Membership confirm page","N/A","N/A","Passed")    
      $ie.button(:value,"Submit").click
      rescue=>e
      $logger.log_results("Change Center Membership confirm page","N/A","N/A","Failed")    
      end
  end
  def view_all_closed_accounts()
    begin
    assert($ie.contains_text($view_all_closed_accounts_group))
    $logger.log_results("View all closed accounts link existed","NA","NA","passed")
    $ie.link(:text,$view_all_closed_accounts_group).click
    $ie.button(:value,"Return to details page").click
    assert($ie.contains_text("Account information"))
    $logger.log_results("View all closed accounts link working","NA","NA","passed")
    rescue =>e
    $logger.log_results("View all closed accounts link working","NA","NA","failed")   
    rescue =>e
    $logger.log_results("View all closed accounts link existed","NA","NA","failed")    
    end
  end
end