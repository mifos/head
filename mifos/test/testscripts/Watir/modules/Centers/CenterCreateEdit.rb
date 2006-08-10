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
class CenterCreateEdit < TestClass
  #connecting to database
  def database_connection()
    db_connect()
    dbquery("select o.search_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
    @@search_id=dbresult[0]
  end
  #logining into Mifos
  def center_login()
		start
		login($validname,$validpwd)
		
  end
  #checking for the link Create new center 
  def check_center()
		begin
		$ie.link(:text,"Clients & Accounts").click
		assert($ie.contains_text("Create new Kendra"))
		$logger.log_results("Link Check","Create New Kendra","Create New Kendra","passed")
		rescue =>e
		$logger.log_results("Link Check","Create New Kendra","exists","failed")
		end
		
  end
  #select the office while creating center
  def select_office()
    begin
    $ie.link(:text,"Create new Kendra").click
    dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and  search_id like '"+@@search_id+"%'")
    @@office_id=dbresult[0]
    display_name=dbresult[1]
     if (display_name=="Mifos HO") then
          get_next_data
          @@office_id=dbresult[0]
          display_name=dbresult[1]
     end      
        $ie.link(:text,display_name).click
        assert($ie.contains_text("Create a new Kendra - Enter Kendra information"))
        $logger.log_results("Page Enter Kendra Information","Should appear","appeared","passed")
        rescue=>e
        $logger.log_results("Page Enter Kendra Information","Should appear","not appeared","failed")
  end      
  end
 # checking all the mandatory fields in center creation page
  def mandatory_all()
    begin
    $ie.button(:value,"Preview").click
    assert($ie.contains_text($additional_information_msg)) and  assert($ie.contains_text($center_name_msg))
    $logger.log_results("all mandatory check ","NA","NA","passed");
    rescue=>e
    $logger.log_results("all mandatory check ","NA","NA","failed");
    end
  end
  #checking the mandatory after entering center name
  def mandatory_with_centername()
    begin
    $ie.text_field(:name,"displayName").set("aaa")
    $ie.button(:value,"Preview").click
    assert($ie.contains_text($additional_information_msg))
    $logger.log_results("mandatory checks when Kendra name entered ","NA","NA","passed")
    rescue=>e
    $logger.log_results("mandatory checks when Kendra name entered ","NA","NA","failed")
    end
  end
    #checking the mandatory after entering center name and custom filed data
  def mandatory_with_cname_addInformation()
    begin
     $ie.text_field(:name,"displayName").set("aaa")
     $ie.text_field(:name,"customField[1].fieldValue").set("111")
     $ie.button(:value,"Preview").click 
     assert($ie.contains_text($meeting_msg)) and  assert($ie.contains_text($Loan_officer_msg))
     $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","passed")
     rescue=>e
     $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","failed")
    end
  end
    #checking the mandatory after entering center name, custom field data and selecting Loan officer
  def mandatory_with_cname_addInformation_LO()
    begin
     if ($validname=="mifos") then
     dbquery("select personnel_id,display_name from personnel where office_id="+@@office_id)
     @@personnel_id=dbresult[0]
     @@loan_officer=dbresult[1]
     else
     dbquery("select personnel_id,display_name from personnel where login_name='"+$validname+"'")
     @@personnel_id=dbresult[0]
     @@loan_officer=dbresult[1]
     end
     $ie.text_field(:name,"displayName").set("aaa")
     $ie.text_field(:name,"customField[1].fieldValue").set("111")
     $ie.select_list(:name,"loanOfficerId").select_value(@@personnel_id)
     $ie.button(:value,"Preview").click 
     assert($ie.contains_text($meeting_msg)) and  assert($ie.contains_text($Loan_officer_msg))
     $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","passed")
     rescue=>e
     $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","failed")
    end
  end
  #create center enter all data in enter center information page 
  def center_create(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      begin
      $ie.text_field(:name,"displayName").set(centername) 
      $ie.select_list(:name,"loanOfficerId").select_value(@@personnel_id)
      create_meeting(frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      @@name_center=centername
      $ie.text_field(:name,"externalId").set(external_id)
      $ie.text_field(:name,"mfiJoiningDateDD").set(mfi_date)
      $ie.text_field(:name,"mfiJoiningDateMM").set(mfi_month)
      $ie.text_field(:name,"mfiJoiningDateYY").set(mfi_year)
      $ie.text_field(:name,"customerAddressDetail.line1").set(address1)
      $ie.text_field(:name,"customerAddressDetail.line2").set(address2)
      #$ie.text_field(:name,"customerAddressDetail.line3").set(address3)
      $ie.text_field(:name,"customerAddressDetail.city").set(city)
      $ie.text_field(:name,"customerAddressDetail.state").set(state)
      $ie.text_field(:name,"customerAddressDetail.country").set(country)
      $ie.text_field(:name,"customerAddressDetail.zip").set(postal_code)
      $ie.text_field(:name,"customerAddressDetail.phoneNumber").set(telephone)
      $ie.text_field(:name,"customField[1].fieldValue").set(custom1)
      $ie.text_field(:name,"customField[0].fieldValue").set(custom1)      
      
      $ie.button(:value,"Preview").click
      end
  end
  #edit center from the review&submit page
  def edit_center_preview(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
  $ie.button(:value,"Edit Kendra Information").click
      begin
      center_create(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      assert($ie.contains_text($center_review)) and assert($ie.contains_text(centername))
      $logger.log_results("Kendra Creation","Review&submit page","opened","passed")
      center_submit()
      rescue =>e
      $logger.log_results("Kendra Creation","Review&submit page","opened","failed")
      end
  end
  #submittiong the data from the review & submit page
  def center_submit()
    begin
    $ie.button(:value,"Submit").click
    assert($ie.contains_text($center_success)) and assert($ie.contains_text(@@name_center))
    $logger.log_results("Kendra Creation","successfull page","opened","passed")
    $ie.link(:text,"View Kendra details now").click
    rescue =>e
    $logger.log_results("Kendra Creation","successfull page","opened","failed")
    end
  end
  #checking that Schedule meeting link is working or not
  def meeting()
  begin
    $ie.link(:text,"Schedule Meeting").click
    assert($ie.contains_text($meetin_msg))
    $logger.log_results("Meeeting page Opened","NA","NA","Passed")
    meeting_mandatory_week()
    rescue =>e
    $logger.log_results("Meeeting page Opened","NA","NA","Failed")
  end
  end
  #checking mandatory for meeting week
  def meeting_mandatory_week()
    begin
    $ie.button(:value,"Save").click
    assert($ie.contains_text($reccur_week_msg)) and assert($ie.contains_text($location_msg))
    $logger.log_results("all mandatory check for week ","NA","NA","passed");
    meeting_mandatory_month()
    rescue=>e
    $logger.log_results("all mandatory check for week","NA","NA","failed");
    meeting_mandatory_month()
    end
  end
    #checking mandatory for meeting month
  def meeting_mandatory_month()
    begin
    $ie.radio(:name,"frequency","2").set
    $ie.button(:value,"Save").click
    assert($ie.contains_text($month_msg))and assert($ie.contains_text($location_msg))
    $logger.log_results("all mandatory check for month ","NA","NA","passed");
    meeting_cancel()
    rescue=>e
    $logger.log_results("all mandatory check for month","NA","NA","failed");
    meeting_cancel()
    end
  end
  #checking the cancel button functionality from create meeting page
  def meeting_cancel()
  begin
    $ie.button(:value,"Cancel").click
    assert($ie.contains_text("Administrative set fees"))
    $logger.log_results("Meetin cancel","NA","NA","passed");
    rescue=>e
    $logger.log_results("Meetin cancel","NA","NA","failed");
  end
  end
  #checking for duplicate centers creation
  def center_create_test_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    dbquery("select * from customer where display_name='"+centername+"'")
    if $row==0 then
      center_non_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    else
      center_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    end
  end
  #checking the functionality what if you enter non duplicate centers 
  def center_non_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      begin
      center_create(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      assert($ie.contains_text($center_review)) and assert($ie.contains_text(centername))
      $logger.log_results("Kendra Creation","Review&submit page","opened","passed")
      rescue=>e
      $logger.log_results("Kendra Creation","Review&submit page","opened","failed")
      end
  end
    #checking the functionality what if you enter duplicate centers 
  def center_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      begin
      center_create(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      error_message="The entered name, "+centername+" already exists in the system. Please enter a different name"
      assert($ie.contains_text("Create a new Kendra - Review & submit")) 
      $logger.log_results("duplicate check","NA","NA","passed")
      rescue=>e
      $logger.log_results("duplicate check","NA","NA","failed")
      end
  end
  #creation of meeting
  def create_meeting(frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
  begin
     $ie.link(:text,"Schedule Meeting").click
    if frequncymeeting=="1" then
      $ie.radio(:name,"frequency","1").set
      $ie.text_field(:name,"recurWeek").set(reccurweek)
      $ie.select_list(:name,"weekDay").select_value(weekweekday)
      $ie.text_field(:name,"meetingPlace").set(meetingplace)
      $ie.button(:value,"Save").click
    elsif frequncymeeting=="2" then
      $ie.radio(:name,"frequency","2").set
      if monthtype=="1" then
      $ie.radio(:name,"monthType","1").set
      $ie.text_field(:name,"monthDay").set(monthday)
      $ie.text_field(:name,"monthMonth").set(monthmonth)
      $ie.text_field(:name,"meetingPlace").set(meetingplace)
      $ie.button(:value,"Save").click
      elsif monthtype=="2" then
      $ie.select_list(:name,"monthRank").select_value(monthrank)
      $ie.select_list(:name,"monthWeek").select_value(monthweek)
      $ie.text_field(:name,"monthMonthRank").set(monthmonthrank)
      $ie.text_field(:name,"meetingPlace").set(meetingplace)
      $ie.button(:value,"Save").click
      end
    end
    assert($ie.contains_text("Administrative set fees"))
    $logger.log_results("Meeting creation","NA","NA","passed");
    rescue=>e
    $logger.log_results("Meeting creation","NA","NA","failed");
    end
    
  end
  #checking the database after submitting the data
  def db_check(centername)
    db_center=$dbh.real_query("select * from customer where display_name='"+centername+"' and loan_officer_id="+@@personnel_id+" and branch_id="+@@office_id)
    rowcount= db_center.num_rows
    if rowcount==0 then
    $logger.log_results("Data Base Check","NA","NA","failed"); 
    else
    $logger.log_results("Data Base Check","NA","NA","passed");    
    end   
  end
  #changing the status 
  def edit_status(centername)
      dbquery("select status_id from customer where display_name='"+centername+"'")
      status_id=dbresult[0]
      if status_id=="13" then
        status_inactive()
      elsif status_id=="14" then
        status_active()
      end
  end
  #changing the status from inactive to active
  def status_active()
    begin
    $ie.link(:text,"Edit Kendra Status").click
    $ie.radio(:name,"statusId","13").set
    $ie.text_field(:name,"customerNote.comment").set("AAAAA")
    $ie.button(:value,"Preview").click
    $ie.button(:value,"Submit").click
    assert($ie.contains_text("Performance History ")) and assert($ie.contains_text("Active"))
    $logger.log_results("Status changed to active","NA","NA","passed"); 
    rescue
    $logger.log_results("Status changed to active","NA","NA","failed"); 
    end
  end
  #changing the status from active to inactive
  def status_inactive()
    begin
    $ie.link(:text,"Edit Kendra Status").click
    $ie.radio(:name,"statusId","14").set
    $ie.text_field(:name,"customerNote.comment").set("AAAAA")
    $ie.button(:value,"Preview").click
    $ie.button(:value,"Submit").click
    assert($ie.contains_text("Performance History ")) and assert($ie.contains_text("inactive"))
    $logger.log_results("Status changed to inactive","NA","NA","passed"); 
    rescue
    $logger.log_results("Status changed to inactive","NA","NA","failed"); 
  end
 end
 #checking the functionality of View All closed accounts link
 def view_all_closed_accounts()
    begin
    assert($ie.contains_text("View all closed accounts"))
    $logger.log_results("View all closed accounts link existed","NA","NA","passed");
    $ie.link(:text,"View all closed accounts").click
    $ie.button(:value,"Return to details page").click
    assert($ie.contains_text("Account information"))
    $logger.log_results("View all closed accounts link working","NA","NA","passed");
    rescue =>e
    $logger.log_results("View all closed accounts link working","NA","NA","failed");   
    rescue =>e
    $logger.log_results("View all closed accounts link existed","NA","NA","failed");    
    end
  end
   #checking the functionality of View change log link
  def view_change_log()
    begin
    assert($ie.contains_text("View change log"))
    $logger.log_results("View change log  link existed","NA","NA","passed");
    $ie.link(:text,"View change log").click
    $ie.button(:value,"Return to details page").click
    assert($ie.contains_text("Account information"))
    $logger.log_results("View change log link working","NA","NA","passed");
    rescue =>e
    $logger.log_results("View change log link working","NA","NA","failed");   
    rescue =>e
    $logger.log_results("View change log link existed","NA","NA","failed");    
    end
  end
  #checking the conditions for Editing the center details form center details page
  def edit_center_details(external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone, custom1)
    begin
    $ie.link(:text,"Edit Kendra details").click
    $ie.button(:value,"Cancel").click
    assert($ie.contains_text("Account information"))
    $logger.log_results("View change log link working","NA","NA","passed")
    enter_data_edit_page(external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone, custom1)
    rescue=>e
    $logger.log_results("View change log link working","NA","NA","failed")
    end
   end
     #Editing the center details form center details page
  def enter_data_edit_page(external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone, custom1)
    begin
    $ie.link(:text,"Edit Kendra details").click
    $ie.text_field(:name,"externalId").set(external_id)
    $ie.text_field(:name,"mfiJoiningDateDD").set(mfi_date)
    $ie.text_field(:name,"mfiJoiningDateMM").set(mfi_month)
    $ie.text_field(:name,"mfiJoiningDateYY").set(mfi_year)
    $ie.text_field(:name,"customerAddressDetail.line1").set(address1)
    $ie.text_field(:name,"customerAddressDetail.line2").set(address2)
    #$ie.text_field(:name,"customerAddressDetail.line3").set(address3)
    $ie.text_field(:name,"customerAddressDetail.city").set(city)
    $ie.text_field(:name,"customerAddressDetail.state").set(state)
    $ie.text_field(:name,"customerAddressDetail.country").set(country)
    $ie.text_field(:name,"customerAddressDetail.zip").set(postal_code)
    $ie.text_field(:name,"customerAddressDetail.phoneNumber").set(telephone)
    $ie.text_field(:name,"customField[1].fieldValue").set(custom1)
    $ie.text_field(:name,"customField[0].fieldValue").set(custom1) 
    $ie.button(:value,"Preview").click
    assert($ie.contains_text(external_id))and assert($ie.contains_text(address1))and assert($ie.contains_text(address2)) and assert($ie.contains_text(address3)) and assert($ie.contains_text(city))and assert($ie.contains_text(state))and assert($ie.contains_text(state))and assert($ie.contains_text(country))
    $logger.log_results("Edit Kendra","Review&submit page","opened","passed")
    $ie.button(:value,"Submit").click
    rescue=>e
    $logger.log_results("Edit Kendra","Review&submit page","opened","failed")
    end
  end 
end
