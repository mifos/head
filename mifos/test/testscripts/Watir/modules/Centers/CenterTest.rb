#this class calls all the testcases from the CenterCreateEdit class
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
class CenterCreateEdit < TestClass
  
  def read_center_values(rowid,sheetid)
    if sheetid==1 then
      @centername=arrval[rowid+=1]
      @centername1=@centername+Time.now.strftime("%d%m%Y%H%M%S")
      @external_id=arrval[rowid+=1].to_i.to_s
      @mfi_date=arrval[rowid+=1].to_i.to_s
      @mfi_month=arrval[rowid+=1].to_i.to_s
      @mfi_year=arrval[rowid+=1].to_i.to_s
      @address1=arrval[rowid+=1]
      @address2=arrval[rowid+=1]
      @address3=arrval[rowid+=1]
      @city=arrval[rowid+=1]
      @state=arrval[rowid+=1]
      @country=arrval[rowid+=1]
      @postal_code=arrval[rowid+=1].to_i.to_s
      @telephone=arrval[rowid+=1].to_i.to_s
      @custom1=arrval[rowid+=1].to_i.to_s
      @frequncymeeting=arrval[rowid+=1].to_i.to_s
      @monthtype=arrval[rowid+=1].to_i.to_s
      @reccurweek=arrval[rowid+=1].to_i.to_s
      @weekweekday=arrval[rowid+=1].to_i.to_s
      @monthday=arrval[rowid+=1].to_i.to_s
      @monthmonth=arrval[rowid+=1].to_i.to_s
      @monthrank=arrval[rowid+=1].to_i.to_s
      @monthweek=arrval[rowid+=1].to_i.to_s
      @monthmonthrank=arrval[rowid+=1].to_i.to_s
      @meetingplace=arrval[rowid+=1]
      @flag=arrval[rowid+=1].to_i.to_s
      @notes=arrval[rowid+=1]
    end
  end
  def Centername()
    @centername1
  end
  def External_id()
    @external_id
  end
  def Mfi_date()
    @mfi_date
  end
  def Mfi_month()
    @mfi_month
  end
  def Mfi_year()
    @mfi_year
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
  def Postal_code()
    @postal_code
  end
  def Telephone()
    @telephone
  end
  def Customfield()
    @custom1
  end
  
  def Frequncymeeting()
    @frequncymeeting
  end
  def Monthtype()
    @monthtype
  end
  
  def Reccurweek()
    @reccurweek
  end
  def Weekweekday()
    @weekweekday
  end
  def Monthday()
    @monthday
  end
  def Monthmonth()
    @monthmonth
  end
  def Monthrank()
    @monthrank 
  end
  def Monthweek()
    @monthweek
  end
  def Monthmonthrank()
    @monthmonthrank
  end
  def Meetingplace()
    @meetingplace
  end
  def Flag()
    @flag
  end
  def Notes()
    @notes
  end
  
  #connecting to database
  def database_connection()
    begin
      start()
      db_connect()
      dbquery("select o.search_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
      @@search_id=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=82 and locale_id=1")
      @@lookup_name_client=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=83 and locale_id=1")
      @@lookup_name_group=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=84 and locale_id=1")
      @@lookup_name_center=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #logining into Mifos
  def center_login()
    begin
      login($validname,$validpwd)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #calling the method load_properties from test class to read properties from properties file
  def properties_load()
    begin
      @@groupprop=load_properties("modules/propertis/GroupUIResources.properties")
      @@meetingprop=load_properties("modules/propertis/Meeting.properties")
      @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
      @@menuprop=load_properties("modules/propertis/MenuResources.properties")
      @@custprop=load_properties("modules/propertis/CustomerUIResources.properties")
      @@centerprop=load_properties("modules/propertis/CenterUIResources.properties")
      @@officeprop=load_properties("modules/propertis/OfficeUIResources.properties")
    rescue =>excp
     quit_on_error(excp)
    end
  end
  
  #Getting the lables from properties file
  def geting_lables_from_proprtis()
    begin
      @@createcenterlabel=@@menuprop['label.createnew']+' '+@@lookup_name_center
      @@creategrouplabel=@@menuprop['label.createnew']+' '+@@lookup_name_group
      @@createclientlabel=@@menuprop['label.createnew']+' '+@@lookup_name_client
      @@center_center_information=@@centerprop['Center.CreateNew']+" "+@@lookup_name_center+" - "+@@centerprop['Center.Enter']+" "+@@lookup_name_center+" "+@@centerprop['Center.Information']
      @@additional_information_msg=remove_li_tag(@@centerprop['errors.Customer.specifyCustomFieldValue'])
      @@center_name_msg=remove_li_tag(@@centerprop['errors.Customer.specifyName'])
      @@meeting_msg=remove_li_tag(@@centerprop['errors.Customer.specifyMeeting'])
      @@Loan_officer_msg=remove_li_tag(@@centerprop['errors.Customer.specifyLoanOfficer'])
      @@edit_center_information=@@centerprop['Center.Edit']+" "+@@lookup_name_center+" "+@@centerprop['Center.Information']
      @@center_review=@@centerprop['Center.CreateNew']+" "+@@lookup_name_center+" - "+@@centerprop['Center.ReviewSubmit']
      @@center_success=@@centerprop['Center.ConfirmationMessage']+" "+@@lookup_name_center
      @@meetin_msg=@@meetingprop['meeting.labelMeetingScheduleFor']+" "+@@lookup_name_center
      @@schedule_meeting_link=@@centerprop['Center.MeetingScheduleLink']
      @@reccur_week_msg=@@meetingprop['errors.Meeting.specifyWeekDayAndRecurAfter']
      @@location_msg=@@meetingprop['errors.Meeting.invalidMeetingPlace']
      @@view_all_closed_accounts=@@centerprop['Center.ClosedAccountsLink']
      @@account_information_label=@@centerprop['Center.AccountHeading']
      @@edit_center_status=@@centerprop['Center.Edit']+" "+@@lookup_name_center+" "+@@centerprop['Center.Status1']
      @@performance_history=@@centerprop['Center.PerformanceHistoryHeading']
      @@view_change_log=@@centerprop['Center.ChangeLogLink']
      @@edit_center_details=@@centerprop['Center.Edit']+" "+@@lookup_name_center+" "+@@centerprop['Center.GroupsLink3']
      @@button_preview=@@centerprop['button.preview']
      @@button_submit=@@centerprop['button.submit']
      @@button_search=@@centerprop['button.Search']
      @@button_cancel=@@centerprop['button.cancel']
      @@button_save=@@meetingprop['meeting.button.save']
      @@admin_link=@@officeprop['Office.labelLinkAdmin']
      @@view_office=@@officeprop['Office.labelLinkViewOffices']
      @@edit_office=@@officeprop['Office.labelEditOfficeInfo']
      @@edit_office_error=@@officeprop['Office.error.hasActivePersonnel']
      @view_center_details=@@centerprop['Center.View']+" "+@@lookup_name_center+" "+@@centerprop['Center.DetailsNow']
      @month_msg=@@meetingprop['errors.Meeting.specifyDayNumAndRecurAfter']
      @@back_to_details_page=@@centerprop['Center.backtodetailspage']
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking for the link Create new center in clients&Accounts section
  def check_center()
    begin
      $ie.link(:text,"Clients & Accounts").click
      assert($ie.contains_text(@@createcenterlabel))
      $logger.log_results("Link Check","Create New Kendra","Create New Kendra","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Link Check","Create New Kendra","exists","failed")
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  #select the office while creating center in select office page
  def select_office()
    begin
      $ie.link(:text,@@createcenterlabel).click
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and  search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      display_name=dbresult[1]
      if (display_name=="Mifos HO") then
        get_next_data
        @@office_id=dbresult[0]
        display_name=dbresult[1]
      end      
      $ie.link(:text,display_name).click
      assert($ie.contains_text(@@center_center_information))
      $logger.log_results("Page Enter Kendra Information","Should appear","appeared","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page Enter Kendra Information","Should appear","not appeared","failed")
    rescue =>excp
      quit_on_error(excp) 
    end      
  end
  # checking all the mandatory fields in center creation page
  def check_mandatory_all()
    begin
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@additional_information_msg)) and  assert($ie.contains_text(@@center_name_msg)) and assert($ie.contains_text(@@meeting_msg))
      $logger.log_results("all mandatory check ","NA","NA","passed");
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check ","NA","NA","failed");
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking the mandatory after entering center name
  def check_mandatory_with_centername()
    begin
      $ie.text_field(:name,"displayName").set("aaa")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@additional_information_msg)) and assert($ie.contains_text(@@meeting_msg))
      $logger.log_results("mandatory checks when Kendra name entered ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("mandatory checks when Kendra name entered ","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking the mandatory after entering center name and custom filed data
  def check_mandatory_with_cname_addInformation()
    begin
      $ie.text_field(:name,"displayName").set("aaa")
      $ie.text_field(:name,"customField[1].fieldValue").set("111")
      $ie.button(:value,@@button_preview).click 
      assert($ie.contains_text(@@meeting_msg)) and  assert($ie.contains_text(@@Loan_officer_msg))
      $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking the mandatory after entering center name, custom field data and selecting Loan officer
  def check_mandatory_with_cname_addInformation_LO()
    begin
      if ($validname=="mifos") then
        dbquery("select personnel_id,display_name from personnel where level_id=1 and office_id="+@@office_id)
        @@personnel_id=dbresult[0]
        @@loan_officer=dbresult[1]
      else
        dbquery("select personnel_id,display_name from personnel where level_id=1 and login_name='"+$validname+"'")
        @@personnel_id=dbresult[0]
        @@loan_officer=dbresult[1]
      end
      $ie.text_field(:name,"displayName").set("aaa")
      $ie.text_field(:name,"customField[1].fieldValue").set("111")
      $ie.select_list(:name,"loanOfficerId").select_value(@@personnel_id)
      $ie.button(:value,@@button_preview).click 
      assert($ie.contains_text(@@meeting_msg)) 
      $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("mandatory checks when Kendra name,additional information entered","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)     
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
      $ie.text_field(:name,"address.line1").set(address1)
      $ie.text_field(:name,"address.line2").set(address2)
      #$ie.text_field(:name,"address.line3").set(address3)
      $ie.text_field(:name,"address.city").set(city)
      $ie.text_field(:name,"address.state").set(state)
      #$ie.text_field(:name,"address.country").set(country)
      $ie.text_field(:name,"address.zip").set(postal_code)
      #$ie.text_field(:name,"address.phoneNumber").set(telephone)
      $ie.text_field(:name,"customField[1].fieldValue").set(custom1)
      $ie.text_field(:name,"customField[0].fieldValue").set(custom1)      
      $ie.button(:value,@@button_preview).click
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #edit center from the review&submit page
  def edit_center_preview(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    begin
      $ie.button(:value,@@edit_center_information).click
      begin
        center_create(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
        assert($ie.contains_text(@@center_review)) and assert($ie.contains_text(centername))
        $logger.log_results("Kendra Creation","Review&amp;submit page","opened","passed")
        center_submit()
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Kendra Creation","Review&amp;submit page","opened","failed")
      end
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  #submittiong the data from the review & submit page
  def center_submit()
    begin
      $ie.button(:value,@@button_submit).click
      assert($ie.contains_text(@@center_success)) and assert($ie.contains_text(@@name_center))
      $logger.log_results("Kendra Creation","successfull page","opened","passed")
      $ie.link(:text,@view_center_details).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Kendra Creation","successfull page","opened","failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking that Schedule meeting link is working or not
  def meeting()
    begin
      $ie.link(:text,@@schedule_meeting_link).click
      assert($ie.contains_text(@@meetin_msg))
      $logger.log_results("Meeeting page Opened","NA","NA","Passed")
      meeting_mandatory_week()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Meeeting page Opened","NA","NA","Failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking mandatory for meeting week
  def meeting_mandatory_week()
    begin
      $ie.button(:value,@@button_save).click
      assert($ie.contains_text(@@reccur_week_msg)) and assert($ie.contains_text(@@location_msg))
      $logger.log_results("all mandatory check for week ","NA","NA","passed");
      meeting_mandatory_month()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check for week","NA","NA","failed");
      meeting_mandatory_month()
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking mandatory for meeting month
  def meeting_mandatory_month()
    begin
      $ie.radio(:name,"frequency","2").set
      $ie.button(:value,@@button_save).click
      #assert($ie.contains_text($month_msg))and assert($ie.contains_text(@@location_msg))
      assert($ie.contains_text(@month_msg))and assert($ie.contains_text(@@location_msg))
      $logger.log_results("all mandatory check for month ","NA","NA","passed");
      meeting_cancel()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check for month","NA","NA","failed");
      meeting_cancel()
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking the cancel button functionality from create meeting page
  def meeting_cancel()
    begin
      $ie.button(:value,@@button_cancel).click
      assert($ie.contains_text(@@centerprop['Center.AdministrativeFeesHeading']))
      $logger.log_results("Meeting cancel","NA","NA","passed");
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Meeting cancel","NA","NA","failed");
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking for duplicate centers creation
  def center_create_test_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    begin
      dbquery("select * from customer where display_name='"+ centername + "'")
      if $row==0 then
        center_non_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      else
        center_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      end
    rescue =>excp
      quit_on_error(excp)
    end     
  end
  #checking the functionality what if you enter non duplicate centers 
  def center_non_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    begin
      center_create(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      assert($ie.contains_text(@@center_review)) and assert($ie.contains_text(centername))
      $logger.log_results("Kendra Creation","Review&amp;submit page","opened","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Kendra Creation","Review&amp;submit page","opened","failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking the functionality what if you enter duplicate centers 
  def center_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    begin
      center_create(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      #error_message="The entered name, "+centername+" already exists in the system. Please enter a different name"
      error_message=string_replace(@@centerprop['Customer.DuplicateCustomerName'],"{0}",centername)
      assert($ie.contains_text(@@center_review)) 
      $logger.log_results("duplicate check","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("duplicate check","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #creation of meeting
  def create_meeting(frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    begin
      $ie.link(:text,@@schedule_meeting_link).click
      if frequncymeeting=="1" then
        $ie.radio(:name,"frequency","1").set
        $ie.text_field(:name,"recurWeek").set(reccurweek)
        $ie.select_list(:name,"weekDay").select_value(weekweekday)
        $ie.text_field(:name,"meetingPlace").set(meetingplace)
        $ie.button(:value,@@button_save).click
      elsif frequncymeeting=="2" then
        $ie.radio(:name,"frequency","2").set
        if monthtype=="1" then
          $ie.radio(:name,"monthType","1").set
          $ie.text_field(:name,"monthDay").set(monthday)
          $ie.text_field(:name,"dayRecurMonth").set(monthmonth)
          $ie.text_field(:name,"meetingPlace").set(meetingplace)
          $ie.button(:value,@@button_save).click
        elsif monthtype=="2" then
          $ie.select_list(:name,"monthRank").select_value(monthrank)
          $ie.select_list(:name,"monthWeek").select_value(monthweek)
          $ie.text_field(:name,"monthMonthRank").set(monthmonthrank)
          $ie.text_field(:name,"meetingPlace").set(meetingplace)
          $ie.button(:value,@@button_save).click
        end
      end
      assert($ie.contains_text(@@center_center_information))
      $logger.log_results("Meeting creation","NA","NA","passed");
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Meeting creation","NA","NA","failed");
    rescue =>excp
      quit_on_error(excp)     
    end
    
  end
  
  #Edit Center from details page
  def editi_center_from_details_page(flag,centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
    begin
      if flag=="1" then
        edit_center_preview(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
      elsif flag=="0" then
        center_submit()
      end
    rescue =>excp
      quit_on_error(excp)
    end       
  end
  #checking the database after submitting the data
  def db_check(centername)
    begin
      db_center=$dbh.real_query("select * from customer where display_name='"+centername+"' and loan_officer_id="+@@personnel_id+" and branch_id="+@@office_id)
      rowcount= db_center.num_rows
      if rowcount==0 then
        $logger.log_results("Data Base Check","NA","NA","failed"); 
      else
        $logger.log_results("Data Base Check","NA","NA","passed");    
      end
    rescue =>excp
      quit_on_error(excp) 
    end      
  end
  #changing the status 
  def edit_status(centername)
    begin
      dbquery("select status_id from customer where display_name='"+centername+"'")
      status_id=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=14")
      @@lookup_inactive=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=13")
      @@lookup_active=dbresult[0]
      if status_id=="13" then
        status_inactive()
      elsif status_id=="14" then
        status_active()
      end
    rescue =>excp
      quit_on_error(excp)       
    end
  end
  #changing the status from inactive to active
  def status_active()
    begin
      $ie.link(:text,@@edit_center_status).click
      $ie.radio(:name,"newStatusId","13").set
      $ie.text_field(:name,"notes").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      assert($ie.contains_text(@@performance_history)) and assert($ie.contains_text(@@lookup_active))
      $logger.log_results("Status changed to active","NA","NA","passed")
      #commented :currently change log link is removed
      view_change_log_active_or_inactive() 
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status changed to active","NA","NA","failed") 
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #changing the status from active to inactive
  def status_inactive()
    begin
      $ie.link(:text,@@edit_center_status).click
      $ie.radio(:name,"newStatusId","14").set
      $ie.text_field(:name,"notes").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      assert($ie.contains_text(@@performance_history)) and assert($ie.contains_text(@@lookup_inactive))
      $logger.log_results("Status changed to inactive","NA","NA","passed")
      
      view_change_log_active_or_inactive()
      status_active()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status changed to inactive","NA","NA","failed") 
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  #checking the functionality of View All closed accounts link
  def view_all_closed_accounts()
    begin
      begin
        assert($ie.link(:text,@@view_all_closed_accounts).exists?())
        $logger.log_results("View all closed accounts link existed","NA","NA","passed")
      rescue Test::Unit::AssertionFailedError=>e 
        $logger.log_results("View all closed accounts link existed","NA","NA","failed")
      end
      $ie.link(:text,@@view_all_closed_accounts).click
      $ie.button(:value,@@custprop['label.backtodetailspage']).click
      if($ie.contains_text(@@account_information_label))
        $logger.log_results("View all closed accounts link working","NA","NA","passed")
      else 
        $logger.log_results("View all closed accounts link working","NA","NA","failed")
      end
        
    rescue =>excp
      quit_on_error(excp)    
    end
    
  end
  #checking the functionality of View change log link
  def view_change_log_active_or_inactive()
    begin
      $ie.link(:text,@@view_change_log).click
      assert($ie.contains_text(@@lookup_inactive)) and \
      assert($ie.contains_text(@@lookup_active))
      $logger.log_results("View change log link working","NA","NA","passed")
      $ie.button(:value,@@back_to_details_page).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View change log link working","NA","NA","failed")   
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  #checking the conditions for Editing the center details form center details page
  def edit_center_details(external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone, custom1)
    begin
      $ie.link(:text,@@edit_center_details).click
      $ie.button(:value,@@button_cancel).click
      assert($ie.contains_text(@@account_information_label))
      $logger.log_results("Cancel Button in Edit Center Details Page","Should Work","Working","Passed")
      enter_data_edit_page(external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone, custom1)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Cancel Button in Edit Center Details Page","Should Work","Working","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  #Editing the center details form center details page
  def enter_data_edit_page(external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone, custom1)
    begin
      $ie.link(:text,@@edit_center_details).click
      $ie.text_field(:name,"externalId").set(external_id)
      $ie.text_field(:name,"mfiJoiningDateDD").set(mfi_date)
      $ie.text_field(:name,"mfiJoiningDateMM").set(mfi_month)
      $ie.text_field(:name,"mfiJoiningDateYY").set(mfi_year)
      $ie.text_field(:name,"address.line1").set(address1)
      $ie.text_field(:name,"address.line2").set(address2)
      #$ie.text_field(:name,"customerAddressDetail.line3").set(address3)
      $ie.text_field(:name,"address.city").set(city)
      $ie.text_field(:name,"address.state").set(state)
      #$ie.text_field(:name,"customerAddressDetail.country").set(country)
      $ie.text_field(:name,"address.zip").set(postal_code)
      #$ie.text_field(:name,"customerAddressDetail.phoneNumber").set(telephone)
      $ie.text_field(:name,"customField[1].fieldValue").set(custom1)
      $ie.text_field(:name,"customField[0].fieldValue").set(custom1) 
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(external_id))and assert($ie.contains_text(address1))and assert($ie.contains_text(address2)) and  assert($ie.contains_text(city))and assert($ie.contains_text(state))
      $logger.log_results("Edit Kendra","Review&amp;submit page","opened","passed")
      $ie.button(:value,@@button_submit).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Edit Kendra","Review&amp;submit page","opened","failed")
    rescue =>excp
      quit_on_error(excp)      
    end
  end 
  #Checking for add notes link
  def check_add_notes_link()
    begin
      @@notes=@@centerprop['Center.NotesLink'].squeeze(" ")
      assert($ie.contains_text(@@notes))
      $logger.log_results("Link "+@@centerprop['Center.NotesLink'],"Should Exist","Existed","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Link "+@@centerprop['Center.NotesLink'],"Should Exist","Not Existed","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #Clicking on Add notes link
  def click_add_notes_link()
    begin
      $ie.link(:text,@@notes).click
      assert($ie.contains_text(@@name_center+" - "+@@custprop['Customer.addnoteheading']))
      $logger.log_results("Link "+@@centerprop['Center.NotesLink'],"Should Work","Working","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Link "+@@centerprop['Center.NotesLink'],"Should Work","Not Working","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #Checking for the mandatory fields in add notes page
  def check_mandatory_in_add_notes_page()
    begin
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@custprop['errors.mandatorytextarea']))
      $logger.log_results("Mandatory Check when you don't enter any note","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check when you don't enter any note","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  #Checking for boundary condition for the field notes
  
  def enter_more_data_in_notes_field(nnotes)
    begin
      $ie.text_field(:name,"comment").set(nnotes)
      $ie.button(:value,@@button_preview).click
      @@center_note_error_message="The maximum length for Note field is"+" 500"
      assert($ie.contains_text(@@center_note_error_message))
      $logger.log_results("Boundary Check for Notes ","Should display Proper Error Message","Displaying","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Boundary Check for Notes ","Should display Proper Error Message","Not Displaying","Failed")    
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  #Adding a note to the customer record
  def enter_data_in_add_note()
    begin
      $ie.text_field(:name,"comment").set("Adding A note")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@name_center+" - "+@@custprop['Customer.PreviewNote']))
      $logger.log_results("Page Redirected to Preview page","N/A","N/A","Passed")
      $ie.button(:value,@@button_submit).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page not Redirected to Preview page","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  
  #Check for see all notes link
  def check_for_see_all_notes_link
    begin
      $ie.wait(10)
      assert($ie.contains_text(@@centerprop['center.SeeAllNotesLink']))
      $logger.log_results(@@centerprop['center.SeeAllNotesLink'],"Link should Exist","Existed","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results(@@centerprop['center.SeeAllNotesLink'],"Link should Exist","Not Existed","Failed")
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  
  #Click on see all notes link and check the functionality
  def click_see_all_notes_link()
    begin 
      $ie.link(:text,@@centerprop['center.SeeAllNotesLink']).click
        begin
          assert($ie.contains_text(@@name_center+"  - "+@@custprop['Customer.addnoteheading'] ))
          $logger.log_results(@@centerprop['center.SeeAllNotesLink'],"Link should Work","Working","Passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results(@@centerprop['center.SeeAllNotesLink'],"Link should Work","Not Working","Failed")
        end
      $ie.link(:text,@@name_center).click

    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  def make_office_inactive()
    dbquery("select office_id,display_name from office where office_id in (select branch_id from customer where customer_level_id=3)")
    officeid=dbresult[0] 
    officename=dbresult[1]
    $ie.link(:text,@@admin_link).click
    $ie.link(:text,@@view_office).click
    $ie.link(:text,officename).click
    $ie.link(:text,@@edit_office).click
    $ie.select_list(:name,"officeStatus").select_value("2")
    $ie.button(:value,@@button_preview).click 
    $ie.button(:value,@@button_submit).click
    assert($ie.contains_text(@@edit_office_error))
    $logger.log_results("Bug#830-Change the status of office","Change the status of an office to inactive having active customers","Should not allow to make inactive","Passed")
  rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("Bug#830-Change the status of office","Change the status of an office to inactive having active customers","Allowed to make inactive","failed")
  rescue =>excp
    quit_on_error(excp)    
  end
end

class CenterTest
  centerobject=CenterCreateEdit.new
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  centerobject.open(filename,1)
  rowid=-1
  colid=1
  centerobject.database_connection
  centerobject.properties_load
  centerobject.geting_lables_from_proprtis
  centerobject.center_login
  centerobject.check_center
  centerobject.select_office
  centerobject.check_mandatory_all
  centerobject.check_mandatory_with_centername
  centerobject.check_mandatory_with_cname_addInformation
  centerobject.check_mandatory_with_cname_addInformation_LO
  centerobject.meeting
  while(rowid<$maxrow*$maxcol-1)
    centerobject.check_center
    centerobject.select_office
    centerobject.read_center_values(rowid,1)
    centerobject.center_create_test_duplicate(centerobject.Centername,centerobject.External_id,centerobject.Mfi_date,\
    centerobject.Mfi_month,centerobject.Mfi_year,centerobject.Address1,centerobject.Address2,\
    centerobject.Address3,centerobject.City,centerobject.State,centerobject.Country,centerobject.Postal_code,\
    centerobject.Telephone,centerobject.Customfield,centerobject.Frequncymeeting,centerobject.Monthtype,\
    centerobject.Reccurweek,centerobject.Weekweekday,centerobject.Monthday,centerobject.Monthmonth,\
    centerobject.Monthrank,centerobject.Monthweek,centerobject.Monthmonthrank,centerobject.Meetingplace)
    centerobject.editi_center_from_details_page(centerobject.Flag,centerobject.Centername,centerobject.External_id,centerobject.Mfi_date,\
    centerobject.Mfi_month,centerobject.Mfi_year,centerobject.Address1,centerobject.Address2,\
    centerobject.Address3,centerobject.City,centerobject.State,centerobject.Country,centerobject.Postal_code,\
    centerobject.Telephone,centerobject.Customfield,centerobject.Frequncymeeting,centerobject.Monthtype,\
    centerobject.Reccurweek,centerobject.Weekweekday,centerobject.Monthday,centerobject.Monthmonth,\
    centerobject.Monthrank,centerobject.Monthweek,centerobject.Monthmonthrank,centerobject.Meetingplace)
    centerobject.db_check(centerobject.Centername)
    centerobject.edit_center_details(centerobject.External_id,centerobject.Mfi_date,\
    centerobject.Mfi_month,centerobject.Mfi_year,centerobject.Address1,centerobject.Address2,\
    centerobject.Address3,centerobject.City,centerobject.State,centerobject.Country,centerobject.Postal_code,\
    centerobject.Telephone,centerobject.Customfield)
    centerobject.edit_status(centerobject.Centername)
    #Adding a note
    centerobject.check_add_notes_link
    centerobject.click_add_notes_link
    centerobject.check_mandatory_in_add_notes_page
    #Checking for the Add notes and see all notes links    
    centerobject.enter_data_in_add_note
    centerobject.check_for_see_all_notes_link
    centerobject.click_see_all_notes_link     
    centerobject.view_all_closed_accounts
    rowid+=$maxcol
  end
  #added as part of bug#830
  centerobject.make_office_inactive()
  centerobject.mifos_logout()
end
