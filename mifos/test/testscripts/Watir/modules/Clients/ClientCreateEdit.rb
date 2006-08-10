require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/assertions'
#require 'modules/properties'

include Watir
class ClientCreateEdit<TestClass 
  
  
  #connecting to database and getting the searchID for office
  
  def database_connection()
    db_connect()
    dbquery("select o.search_id,o.office_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
    @@search_id=dbresult[0]
    dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=82 and locale_id=1")
    @@lookup_name_client=dbresult[0]
    dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=83 and locale_id=1")
    @@lookup_name_group=dbresult[0]
    
  end
  def read_client_values(rowid,sheetid)
    if sheetid==1 then
      @salutation=arrval[rowid+=1]
      @fname=arrval[rowid+=1]
      @lname=arrval[rowid+=1]
      @date=arrval[rowid+=1].to_i.to_s
      @month=arrval[rowid+=1].to_i.to_s
      @year=arrval[rowid+=1].to_i.to_s
      @gender=arrval[rowid+=1]
      @religion=arrval[rowid+=1]
      @sorftype=arrval[rowid+=1]
      @sorffname=arrval[rowid+=1]
      @sorflname=arrval[rowid+=1]
      @custom=arrval[rowid+=1].to_i.to_s
      @statusname=arrval[rowid+=1]
    elsif sheetid==2 then
      @salutation=arrval[rowid+=1]
      @fname=arrval[rowid+=1]
      @mname=arrval[rowid+=1]
      @sname=arrval[rowid+=1]
      @lname=arrval[rowid+=1]
      @govtid=arrval[rowid+=1]
      @date=arrval[rowid+=1].to_i.to_s
      @month=arrval[rowid+=1].to_i.to_s
      @year=arrval[rowid+=1].to_i.to_s
      @gender=arrval[rowid+=1]
      @mstatus=arrval[rowid+=1]
      @noofchildren=arrval[rowid+=1].to_i.to_s
      @religion=arrval[rowid+=1]
      @education=arrval[rowid+=1]
      @sorftype=arrval[rowid+=1]
      @sorffname=arrval[rowid+=1]
      @sorfmname=arrval[rowid+=1]
      @sorfsname=arrval[rowid+=1]
      @sorflname=arrval[rowid+=1]
      @address1=arrval[rowid+=1]
      @address2=arrval[rowid+=1]
      @address3=arrval[rowid+=1]
      @city=arrval[rowid+=1]
      @state=arrval[rowid+=1]
      @country=arrval[rowid+=1]
      @pcode=arrval[rowid+=1].to_i.to_s
      @phone=arrval[rowid+=1].to_i.to_s
      @custom=arrval[rowid+=1].to_i.to_s
      @statusname=arrval[rowid+=1]
      @externalid=arrval[rowid+=1]
      @tdate=arrval[rowid+=1].to_i.to_s
      @tmonth=arrval[rowid+=1].to_i.to_s        
      @tyear=arrval[rowid+=1].to_i.to_s
    elsif sheetid==3 then
      @salutation=arrval[rowid+=1]
      @fname=arrval[rowid+=1]
      @mname=arrval[rowid+=1]
      @sname=arrval[rowid+=1]
      @lname=arrval[rowid+=1]
      @govtid=arrval[rowid+=1]
      @date=arrval[rowid+=1].to_i.to_s
      @month=arrval[rowid+=1].to_i.to_s
      @year=arrval[rowid+=1].to_i.to_s
      @gender=arrval[rowid+=1]
      @mstatus=arrval[rowid+=1]
      @noofchildren=arrval[rowid+=1].to_i.to_s
      @religion=arrval[rowid+=1]
      @education=arrval[rowid+=1]
      @sorftype=arrval[rowid+=1]
      @sorffname=arrval[rowid+=1]
      @sorfmname=arrval[rowid+=1]
      @sorfsname=arrval[rowid+=1]
      @sorflname=arrval[rowid+=1]
      @address1=arrval[rowid+=1]
      @address2=arrval[rowid+=1]
      @address3=arrval[rowid+=1]
      @city=arrval[rowid+=1]
      @state=arrval[rowid+=1]
      @country=arrval[rowid+=1]
      @pcode=arrval[rowid+=1].to_i.to_s
      @phone=arrval[rowid+=1].to_i.to_s
      @custom=arrval[rowid+=1].to_i.to_s
      @statusname=arrval[rowid+=1]
      @externalid=arrval[rowid+=1]
      @tdate=arrval[rowid+=1].to_i.to_s
      @tmonth=arrval[rowid+=1].to_i.to_s        
      @tyear=arrval[rowid+=1].to_i.to_s
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
    end
    if @salutation=="mr" then
      @salutation="47"
    elsif @salutation=="mrs" then
      @salutation="48"
    elsif @salutation=="ms" then
      @salutation="228"
    end
    if @gender=="male"
      @gender="49"
    elsif @gender=="female" then
      @gender="50"
    end
    if @religion=="hindu" then
      @religion="130"
    elsif @religion=="muslim" then
      @religion="131"
    elsif @religion=="christian" then
      @religion="221"
    end
    if @sorftype=="spouse" then
      @sorftype="1"
    elsif @sorftype=="father" then
      @sorftype="2"
    end
    if @mstatus=="married" then
      @mstatus="66"
    elsif @mstatus=="unmarried" then
      @mstatus="67"
    elsif @mstatus=="widowed" then
      @mstatus="220"
    end
    if @education=="onlymember" then
      @education="134"
    elsif @education=="onlyhusband" then
      @education="135"
    elsif @education=="bothliterate" then
      @education="226"
    elsif @education=="bothilliterate" then
      @education="227"
    end
    if @statusname=="partial" then
      @statusname="Save For Later"
    elsif @statusname=="pending"
      @statusname="Submit For Approval"
    end
  end
  def Salutation()
    @salutation
  end
  def Fname()
    @fname
  end
  def Mname()
    @mname 
  end
  def Sname
    @sname
  end
  def Lname
    @lname
  end
  def Govtid
    @govtid
  end
  def Date
    @date
  end
  def Month
    @month
  end
  def Year
    @year
  end
  def Gender
    @gender
  end
  def Mstatus
    @mstatus
  end
  def  Noofchildren
     @noofchildren
  end
  def Religion 
     @religion
  end
  def Education 
    @education
  end
  def Sorftype
    @sorftype
  end
  def Sorffname 
    @sorffname
  end
  def Sorfmname 
    @sorfmname
  end
  def Sorfsname  
    @sorfsname
  end
  def Sorflname
    @sorflname 
  end
  def Address1
    @address1
  end
  def Address2
    @address2
  end
  def Address3
    @address3
  end
  def City
    @city
  end
  def State
    @state
  end
  def Country
    @country 
  end
  def Pcode
    @pcode
  end
  def Phone
    @phone
  end
  def Custom
    @custom
  end
  def Statusname
    @statusname
  end
  def Externalid
    @externalid
  end
  def Tdate
    @tdate
  end
  def Tmonth
    @tmonth
  end
  def Tyear
    @tyear
  end
  def Frequncymeeting
    @frequncymeeting
  end
  def Monthtype
    @monthtype
  end
  def Reccurweek
    @reccurweek
  end
  def Weekweekday
    @weekweekday
  end
  def Monthday
    @monthday
  end
  def Monthmonth 
    @monthmonth 
  end
  def Monthrank
    @monthrank
  end
  def Monthweek
    @monthweek
  end
  def Monthmonthrank
    @monthmonthrank
  end
  def Meetingplace
    @meetingplace
  end
  #logining into Mifos
  
  def client_login()
		start
		login($validname,$validpwd)
  end
  #calling the method load_properties from test class to read properties from properties file
  def propertis_load()
    @@clientprop=load_properties("modules/propertis/ClientUIResources.properties")
    @@meetingprop=load_properties("modules/propertis/Meeting.properties")
    @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
    @@menuprop=load_properties("modules/propertis/MenuResources.properties")
    
  end
  
  #checking for the link Create new client in clients&accounts section
  
  def check_createnewclient_link()
		begin
		  $ie.link(:text,"Clients & Accounts").click
		  @@createclientlabel=@@menuprop['label.createnew']+' '+@@lookup_name_client
		  assert($ie.contains_text(@@createclientlabel))
		  $logger.log_results("Link Check in Clients & Accounts Section",@@createclientlabel,@@createclientlabel,"passed")
		rescue Test::Unit::AssertionFailedError=>e
		  $logger.log_results("Link Check in Clients & Accounts Section",@@createclientlabel,"Not existed","failed")
		end
  end
  
  
  #checking the Create new client link functionality
  
  def select_grouppage_check()
    begin
      $ie.link(:text,@@createclientlabel).click
      @@createclient=@@clientprop['client.CreateTitle']+" "+@@lookup_name_client
      @@selectgrouptext=(@@createclientlabel+" - "+@@clientprop['client.select']+" "+@@lookup_name_group).squeeze(" ")
      assert($ie.contains_text(@@selectgrouptext))
      $logger.log_results(@@createclientlabel,"Link Should work","Working","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results(@@createclientlabel,"Link work","Not Working","failed")
    end
  end
  
  
  #checking cancel button functionality in select group page
 
  def click_cancel_from_group_select()
    begin
        @@button_cancel=@@clientprop['button.cancel']
        $ie.button(:value,@@button_cancel).click
        assert($ie.contains_text(@@createclientlabel))
        $logger.log_results("Cancel button in select group page","Should work","Working","Passed")
    rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Cancel button in select group page","Should work","Not Working","Passed")
    end
  end
  
  
  #selecting group before creating client in select group page
  
  def select_group()
    begin
      $ie.link(:text,"Clients & Accounts").click
      $ie.link(:text,@@createclientlabel).click
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      @@display_name=dbresult[1]
      if (@@office_id=="1") then
          get_next_data
          @@office_id=dbresult[0]
          @@display_name=dbresult[1]
      end      
      dbquery("select customer_id,display_name,branch_id from customer where customer_level_id=2 and status_id=9 and branch_id="+@@office_id)
      @@gdisplay_name=dbresult[1]  
      $ie.text_field(:name,"searchNode(searchString)").set(@@gdisplay_name)        
      $ie.button(:value,"Proceed").click
      $ie.link(:text,@@gdisplay_name).click
      $ie.wait(10)
      @@client_select_client=(@@createclient+" "+@@clientprop['client.CreatePersonalInformationTitle']).squeeze(" ")
      assert($ie.contains_text(@@client_select_client))
      $logger.log_results(@@client_select_client,"Page Should appear","appearing","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results(@@client_select_client,"Page Should appear","not appearing","failed")
    end      
  end
  
  
  #checking all the mandatory fields in enter personal information page
 
  def check_all_mandatory_fields_for_client()
    begin
      
      @@client_salitution_msg=string_replace(@@clientprop['errors.requiredSelect'],"{0}",@@clientprop['client.SalutationValue'])
      @@client_fname_msg=string_replace(@@clientprop['errors.mandatory'],"{0}",@@clientprop['client.FirstNameValue'])
      @@client_lname_msg=string_replace_message(@@clientprop['errors.mandatory'],@@clientprop['client.FirstNameValue'],@@clientprop['client.LastNameValue'])
      @@client_dob_msg=string_replace_message(@@clientprop['errors.mandatory'],@@clientprop['client.LastNameValue'],@@clientprop['client.DateOfBirthValue'])
      @@client_gender_msg=string_replace_message(@@clientprop['errors.requiredSelect'],@@clientprop['client.SalutationValue'],@@clientprop['client.GenderValue'])
      @@client_education_msg=string_replace(@@clientprop['exception.framework.fieldConfiguration.mandatory'],"{0}",@@clientprop['client.EducationLevelValue'])
      @@client_relesion=string_replace_message(@@clientprop['exception.framework.fieldConfiguration.mandatory'],@@clientprop['client.EducationLevelValue'],@@clientprop['client.CitizenshipValue'])
      @@client_spo_or_father_type_msg=string_replace_message(@@clientprop['errors.requiredSelect'],@@clientprop['client.GenderValue'],@@clientprop['client.SpouseNameTypeValue'])
      @@client_spo_or_father_fname_msg=string_replace_message(@@clientprop['errors.mandatory'],@@clientprop['client.DateOfBirthValue'],@@clientprop['client.SpouseFirstNameValue'])
      @@client_spo_or_father_lname_msg=string_replace_message(@@clientprop['errors.mandatory'],@@clientprop['client.SpouseFirstNameValue'],@@clientprop['client.SpouseLastNameValue'])
      @@client_note_msg=string_replace_message(@@clientprop['errors.mandatory'],@@clientprop['client.SpouseLastNameValue'],@@clientprop['client.Note'])      
      @@clientprop['errors.requiredCustomField']["<li>"]=""
      @@clientprop['errors.requiredCustomField']["</li>"]=""
      @@client_custom_msg=@@clientprop['errors.requiredCustomField']
      @@client_formedby_msg=@@clientprop['FormedByLoanOfficerBlankException']
      @@client_formedby_msg["<li>"]=""
      @@client_formedby_msg["</li>"]=""
      
      @@button_continue=@@clientprop['button.continue']
      @@button_preview=@@clientprop['button.preview']
      @@button_submit=@@clientprop['button.submit']
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_salitution_msg))and assert($ie.contains_text(@@client_fname_msg))and assert($ie.contains_text(@@client_lname_msg))and //
      assert($ie.contains_text(@@client_dob_msg))and assert($ie.contains_text(@@client_gender_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("Checking of all mandatory check in Enter personnel information","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Checking of all mandatory fields in Enter personnel information","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation in enter personal information page
  
  def check_all_mandatory_when_salutation_selected()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_fname_msg))and assert($ie.contains_text(@@client_lname_msg))and //
      assert($ie.contains_text(@@client_dob_msg))and assert($ie.contains_text(@@client_gender_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("All mandatory check in Enter personnel information with out salutation ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation and firstname in enter personal information page
  
  def check_all_mandatory_when_salutation_fname_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_lname_msg))and //
      assert($ie.contains_text(@@client_dob_msg))and assert($ie.contains_text(@@client_gender_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("All mandatory check in Enter personnel information with out salutation and firstname","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("All mandatory check in Enter personnel information with out salutation and firstname","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname and last name in enter personal information page
  
  def check_all_mandatory_when_salutation_fname_lname_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_dob_msg))and assert($ie.contains_text(@@client_gender_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("All mandatory check in Enter personnel information with out salutation, firstname and lastname ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e 
      $logger.log_results("All mandatory check in Enter personnel information with out salutation, firstname and lastname","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname,dob and last name in enter personal information page
  
  def check_all_mandatory_when_salutation_fname_lname_dob_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.text_field(:name,"dateOfBirthDD").set("09")
      $ie.text_field(:name,"dateOfBirthMM").set("10")
      $ie.text_field(:name,"dateOfBirthYY").set("1981")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_gender_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("all mandatory check with out salutation, firstname, lastname and DOB ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation, firstname, lastname and DOB","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname,dob,gender and last name in enter personal information page
  
  def check_all_mandatory_when_salutation_fname_lname_dob_gender_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.text_field(:name,"dateOfBirthDD").set("09")
      $ie.text_field(:name,"dateOfBirthMM").set("10")
      $ie.text_field(:name,"dateOfBirthYY").set("1981")
      $ie.select_list(:name,"customerDetail.gender").select_value("49")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, DOB and Gender ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, DOB and Gender","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname,dob,gender,religion and last name in enter personal information page
  
  def check_all_mandatory_when_salutation_fname_lname_dob_gender_religion_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.text_field(:name,"dateOfBirthDD").set("09")
      $ie.text_field(:name,"dateOfBirthMM").set("10")
      $ie.text_field(:name,"dateOfBirthYY").set("1981")
      $ie.select_list(:name,"customerDetail.gender").select_value("49")
      $ie.select_list(:name,"customerDetail.citizenship").select_value("130")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion DOB and Gender ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion DOB and Gender","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname,dob,gender,religion,educationlevel and last name in enter personal information page
  
  def check_all_mandatory_when_salutation_fname_lname_dob_gender_religion_education_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.text_field(:name,"dateOfBirthDD").set("09")
      $ie.text_field(:name,"dateOfBirthMM").set("10")
      $ie.text_field(:name,"dateOfBirthYY").set("1981")
      $ie.select_list(:name,"customerDetail.gender").select_value("49")
      $ie.select_list(:name,"customerDetail.citizenship").select_value("130")
      $ie.select_list(:name,"customerDetail.educationLevel").select_value("135")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_spo_or_father_type_msg))and assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB and Gender ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB and Gender","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname,dob,gender,religion,educationlevel,spouce or father type and last name in enter personal information page
  
  def check_all_mandatory_when_salutation_fname_lname_dob_gender_religion_education_SpouseFatherType_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.text_field(:name,"dateOfBirthDD").set("09")
      $ie.text_field(:name,"dateOfBirthMM").set("10")
      $ie.text_field(:name,"dateOfBirthYY").set("1981")
      $ie.select_list(:name,"customerDetail.gender").select_value("49")
      $ie.select_list(:name,"customerDetail.citizenship").select_value("130")
      $ie.select_list(:name,"customerDetail.educationLevel").select_value("135")
      $ie.select_list(:name,"customerNameDetail[1].nameType").select_value("1")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_spo_or_father_fname_msg))and //
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB, spouce or father type and Gender ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB, spouce or father type and Gender","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname,dob,gender,religion,educationlevel,spouce or father type and last name in enter personal information page
  
  def check_mandatory_when_SpouseorFatherLastName_AdditionalInformation_not_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.text_field(:name,"dateOfBirthDD").set("09")
      $ie.text_field(:name,"dateOfBirthMM").set("10")
      $ie.text_field(:name,"dateOfBirthYY").set("1981")
      $ie.select_list(:name,"customerDetail.gender").select_value("49")
      $ie.select_list(:name,"customerDetail.citizenship").select_value("130")
      $ie.select_list(:name,"customerDetail.educationLevel").select_value("135")
      $ie.select_list(:name,"customerNameDetail[1].nameType").select_value("1")
      $ie.text_field(:name,"customerNameDetail[1].firstName").set("AAA")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_spo_or_father_lname_msg))and assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB, spouce or father type, spouce or father first name and Gender ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB, spouce or father type, spouce or father first name and Gender","NA","NA","failed")
    end
  end
  
  
  #checking mandatory with out salutation,firstname,dob,gender,religion,educationlevel,spouce or father type and last name and spouce or father type and last name in enter personal information page
  
  def check_mandatory_when_AdditionalInformation_not_entered()
    begin
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value("47")
      $ie.text_field(:name,"customerNameDetail[0].firstName").set("aaa")
      $ie.text_field(:name,"customerNameDetail[0].lastName").set("aaa")
      $ie.text_field(:name,"dateOfBirthDD").set("09")
      $ie.text_field(:name,"dateOfBirthMM").set("10")
      $ie.text_field(:name,"dateOfBirthYY").set("1981")
      $ie.select_list(:name,"customerDetail.gender").select_value("49")
      $ie.select_list(:name,"customerDetail.citizenship").select_value("130")
      $ie.select_list(:name,"customerDetail.educationLevel").select_value("135")
      $ie.select_list(:name,"customerNameDetail[1].nameType").select_value("1")
      $ie.text_field(:name,"customerNameDetail[1].firstName").set("AAA")
      $ie.text_field(:name,"customerNameDetail[1].lastName").set("AAA")
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_custom_msg))
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB, spouce or father type, spouce or father first name,spouce or father last name and Gender ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("all mandatory check with out salutation, firstname, lastname, religion,Education level, DOB, spouce or father type, spouce or father first name,spouce or father lats name and Gender","NA","NA","failed")
    end
  end
  
  
  #Method for Create client with all mandatory data in enter personal information page
  
  def create_client_with_all_mandatory_data(nsalutation,nfname,nlname,ndate,nmonth,nyear,ngender,nreligion,nsorftype,nsorffname,nsorflname,ncustom)
     begin
      select_group()
      @@client_mfi_page_msg=(@@createclient+" "+@@clientprop['client.CreateMfiInformationTitle']).squeeze(" ")
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value(nsalutation)
      $ie.text_field(:name,"customerNameDetail[0].firstName").set(nfname)
      $ie.text_field(:name,"customerNameDetail[0].lastName").set(nlname)
      $ie.text_field(:name,"dateOfBirthDD").set(ndate)
      $ie.text_field(:name,"dateOfBirthMM").set(nmonth)
      $ie.text_field(:name,"dateOfBirthYY").set(nyear)
      $ie.select_list(:name,"customerDetail.gender").select_value(ngender)
      $ie.select_list(:name,"customerDetail.citizenship").select_value(nreligion)
      $ie.select_list(:name,"customerDetail.educationLevel").select_value("135")
      $ie.select_list(:name,"customerNameDetail[1].nameType").select_value(nsorftype)
      $ie.text_field(:name,"customerNameDetail[1].firstName").set(nsorffname)
      $ie.text_field(:name,"customerNameDetail[1].lastName").set(nsorflname)
      $ie.text_field(:name,"customField[0].fieldValue").set(ncustom)
      $ie.button(:value,@@button_continue).click
      @@c_name=String(nfname)+"   "+String(nlname)
      assert($ie.contains_text(@@client_mfi_page_msg))
      $logger.log_results("page redirected to create client enter MFI information","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("page redirected to create client enter MFI information","NA","NA","Failed")
    end
  end
  
  
  #mandatory check with out formed by in Enter MFI information page
  
  def check_create_client_mfi_mandatory()
    begin
      $ie.select_list(:name,"customerFormedById").select_value("")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@client_formedby_msg))
      $logger.log_results("Checked Mandatory_MFI Information","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Checked Mandatory_MFI Information","N/A","N/A","Failed")
    end
  end
  
  
  #Method for Create client with all mandatory data in Enter MFI information page
  
  def create_client_enter_mfidata(nsalutation,nfname,nlname,ndate,nmonth,nyear,ngender,nreligion,nsorftype,nsorffname,nsorflname,ncustom)
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
     @@client_review=@@createclient+" "+@@clientprop['client.CreatePreviewReviewSubmitTitle'].squeeze(" ")
     $ie.select_list(:name,"customerFormedById").select_value(@@personnel_id)
     $ie.button(:value,@@button_preview).click
     assert($ie.contains_text(@@client_review)) 
     $logger.log_results("Create client Review&Submit page displaying proper data","N/A","N/A","Passed")
   rescue Test::Unit::AssertionFailedError=>e
     $logger.log_results("Create client Review&Submit page displaying proper data","N/A","N/A","Failed")
   end
  end
  
  
  #method for clicking on submit or save for later while creating client
  
  def submit_data(nstatus)
    begin
      $ie.button(:value,nstatus).click
      @@client_success=@@clientprop['client.ConfirmationMessage']+" "+@@lookup_name_client
      assert($ie.contains_text(@@client_success))
      go_to_detailspage()
      $logger.log_results("client created successfully","N/A","N/A","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("client created successfully","N/A","N/A","Failed")
    end
  end
  
  
  #method for create client with all data in enter personal information page
  
  def client_create_with_all_data(nsalutation,nfname,nmname,nsname,nlname,ngovtid,ndate,nmonth,nyear,ngender,nmstatus,nnoofchildren,nreligion,neducation,nsorftype,nsorffname,nsorfmname,nsorfsname,nsorflname,naddress1,naddress2,naddress3,ncity,nstate,ncountry,npcode,nphone,ncustom)
    $ie.select_list(:name,"customerNameDetail[0].salutation").select_value(nsalutation)
    $ie.text_field(:name,"customerNameDetail[0].firstName").set(nfname)
    $ie.text_field(:name,"customerNameDetail[0].middleName").set(nmname)
    $ie.text_field(:name,"customerNameDetail[0].secondLastName").set(nsname)
    $ie.text_field(:name,"customerNameDetail[0].lastName").set(nlname)
    $ie.text_field(:name,"governmentId").set(ngovtid)
    $ie.text_field(:name,"dateOfBirthDD").set(ndate)
    $ie.text_field(:name,"dateOfBirthMM").set(nmonth)
    $ie.text_field(:name,"dateOfBirthYY").set(nyear)
    $ie.select_list(:name,"customerDetail.gender").select_value(ngender)
    $ie.select_list(:name,"customerDetail.maritalStatus").select_value(nmstatus)
    $ie.text_field(:name,"customerDetail.numChildren").set(nnoofchildren)    
    $ie.select_list(:name,"customerDetail.citizenship").select_value(nreligion)
    $ie.select_list(:name,"customerDetail.educationLevel").select_value(neducation)
    $ie.select_list(:name,"customerNameDetail[1].nameType").select_value(nsorftype)
    $ie.text_field(:name,"customerNameDetail[1].firstName").set(nsorffname)
    $ie.text_field(:name,"customerNameDetail[1].middleName").set(nsorfmname)
    $ie.text_field(:name,"customerNameDetail[1].secondLastName").set(nsorfsname)
    $ie.text_field(:name,"customerNameDetail[1].lastName").set(nsorflname)
    $ie.text_field(:name,"customerAddressDetail.line1").set(naddress1)
    $ie.text_field(:name,"customerAddressDetail.line2").set(naddress2)
    $ie.text_field(:name,"customerAddressDetail.line3").set(naddress3)
    $ie.text_field(:name,"customerAddressDetail.city").set(ncity)
    $ie.text_field(:name,"customerAddressDetail.state").set(nstate)
    $ie.text_field(:name,"customerAddressDetail.country").set(ncountry)
    $ie.text_field(:name,"customerAddressDetail.zip").set(npcode)
    $ie.text_field(:name,"customerAddressDetail.phoneNumber").set(nphone)
    $ie.text_field(:name,"customField[0].fieldValue").set(ncustom)
    @@c_name=String(nfname)+" "+String(nmname)+" "+String(nsname)+" "+String(nlname)
  end
  
  
  #method for click on continue button 
  
  def click_continue() 
    begin
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@client_mfi_page_msg))
      $logger.log_results("page redirected to create client enter MFI information","NA","NA","passed")
    rescue=>e
      $logger.log_results("page redirected to create client enter MFI information","NA","NA","passed")
    end
  end
  
  
  #method for create client with all MFI information in Enter MFI information page
  
  def client_create_enter_all_data_mfi(nexternalid,ntdate,ntmonth,ntyear)
      $ie.select_list(:name,"customerFormedById").select_value(@@personnel_id)
      $ie.text_field(:name,"externalId").set(nexternalid)
      $ie.checkbox(:name,"trained","1").set
      $ie.text_field(:name,"trainedDateDD").set(ntdate)
      $ie.text_field(:name,"trainedDateMM").set(ntmonth)      
      $ie.text_field(:name,"trainedDateYY").set(ntyear)
      fee_select_one_by_one()
  end  
  
  
  #method for click on Preview button
  
  def click_preview()
      begin
        $ie.button(:value,@@button_preview).click
        assert($ie.contains_text(@@client_review)) 
        $logger.log_results("Create client Review&Submit page displaying proper data","N/A","N/A","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Create client Review&Submit page displaying proper data","N/A","N/A","Failed")
      end
  end
  
  
  #selecting fee one by one while creating center
  
  def fee_select_one_by_one()
    search_res=$dbh.real_query("select * from fees where category_id=2 and default_admin_fee='no' and status=1")
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
  
  
  #Editing the client personal information from the review page
  
  def edit_personal_information_from_review(nsalutation,nfname,nmname,nsname,nlname,ngovtid,ndate,nmonth,nyear,ngender,nmstatus,nnoofchildren,nreligion,neducation,nsorftype,nsorffname,nsorfmname,nsorfsname,nsorflname,naddress1,naddress2,naddress3,ncity,nstate,ncountry,npcode,nphone,ncustom)
    begin
      @@client_edit_personnel=@@clientprop['client.ManageTitle']+" "+@@lookup_name_client+" "+@@clientprop['client.EditPersonalInformationTitle'].squeeze(" ")
      $ie.button(:value,@@clientprop['button.previousPersonalInfo']).click
      assert($ie.contains_text(@@client_edit_personnel))
      $logger.log_results("Page redirected to Manage client-edit personnel information","N/A","N/A","Passed")
      client_create_with_all_data(nsalutation,nfname,nmname,nsname,nlname,ngovtid,ndate,nmonth,nyear,ngender,nmstatus,nnoofchildren,nreligion,neducation,nsorftype,nsorffname,nsorfmname,nsorfsname,nsorflname,naddress1,naddress2,naddress3,ncity,nstate,ncountry,npcode,nphone,ncustom)
      click_preview()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page redirected to Manage client-edit personnel information","N/A","N/A","Failed")
    end
  end
  
  
  #Editing the client MFI information from review page
  
  def edit_mfi_information_from_review(nexternalid,ntdate,ntmonth,ntyear)
    begin
      @@client_edit_mfi=@@clientprop['client.ManageTitle']+" "+@@lookup_name_client+" "+@@clientprop['client.EditMfiInformationTitle'].squeeze(" ")
      $ie.button(:value,@@clientprop['button.previousMFIInfo']).click
      assert($ie.contains_text(@@client_edit_mfi))
      $logger.log_results("Page redirected to Manage client-edit MFI information","N/A","N/A","Passed")
      client_create_enter_all_data_mfi(nexternalid,ntdate,ntmonth,ntyear)
      click_preview()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page redirected to Manage client-edit MFI information","N/A","N/A","Failed")
    end
  end
  
  
  # Checking that whether View client details now link is working
  
  def go_to_detailspage()
    begin
      @@view_client_details=@@clientprop['client.ViewClientDetailsLink1']+" "+@@lookup_name_client+" "+@@clientprop['client.ViewClientDetailsLink2']
      @@edit_client_status=@@clientprop['client.EditLink']+" "+@@lookup_name_client+" "+@@clientprop['client.StatusLink']
      $ie.link(:text,@@view_client_details).click
      assert($ie.contains_text(@@edit_client_status))
      $logger.log_results("Opened Edit client details page","N/A","N/A","passed")
      change_status()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Opened Edit client details page","N/A","N/A","passed")
    end
  end 
  
  
  #Changing the staus of client.Getting the current status and what to change
  
  def change_status()
    dbquery("select status_id from customer where display_name='"+@@c_name+"'")
    status_id=dbresult[0]
    if status_id=="1" then
      change_status_pending()
    elsif status_id=="2" then
      change_status_active()
    end
  end
  
  
  #Changing the status from patial to pending
  
  def change_status_pending()
    begin
      $ie.link(:text,@@edit_client_status).click
      $ie.radio(:name,"statusId","2").set
      $ie.text_field(:name,"customerNote.comment").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=2 and locale_id=1")
      @@application_pending_approval=dbresult[0]
      assert($ie.contains_text(@@clientprop['client.PerformanceHistoryHeading']))and assert($ie.contains_text(@@application_pending_approval)) 
      $logger.log_results("Status changed to Pending","NA","NA","passed") 
      view_change_log_pending()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status changed to Pending","NA","NA","failed") 
    end
  end  
  
    
  #Changing the status from Pending to Active
  
  def change_status_active()
    begin
      $ie.link(:text,@@edit_client_status).click
      $ie.radio(:name,"statusId","3").set
      $ie.text_field(:name,"customerNote.comment").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=3 and locale_id=1")
      @@active=dbresult[0]
      assert($ie.contains_text(@@clientprop['client.PerformanceHistoryHeading']))and assert($ie.contains_text(@@active))
      $logger.log_results("Status changed to Active","NA","NA","passed") 
      view_change_log_active()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status changed to Active","NA","NA","failed") 
    end
  end  
  
    
  #checking the change log after changing the status to pending from partial
  
  def view_change_log_pending()
    begin
      @@change_log=@@clientprop['client.ChangeLogLink']
      @@status_label=@@clientprop['client.Status']
      @@return_to_details_page=@@clientprop['client.butbachdetpage']
      $ie.link(:text,@@change_log).click
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=1 and locale_id=1")
      @@partial_application=dbresult[0]
      assert($ie.contains_text(@@status_label))and assert($ie.contains_text(@@application_pending_approval)) and assert($ie.contains_text(@@partial_application))
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed") 
      $ie.button(:value,@@return_to_details_page).click 
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
    end
  end
  
  
  #Checking the change log after changing the status to active from pending
  
  def view_change_log_active()
    begin
      $ie.link(:text,@@change_log).click
      assert($ie.contains_text(@@status_label))and assert($ie.contains_text(@@application_pending_approval)) and assert($ie.contains_text(@@active))
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Passed")   
      $ie.button(:value,@@return_to_details_page).click  
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Change Log is displaying proper data","N/A","N/A","Failed")        
    end
  end
  
  
  #checking the database whether the created client is storing in the data base
  
  def db_check()
    db_center=$dbh.real_query("select * from customer where display_name='"+@@c_name+"' and loan_officer_id="+@@personnel_id+" and branch_id="+@@office_id)
    rowcount= db_center.num_rows
    if rowcount==0 then
      $logger.log_results("Data Base Check","NA","NA","failed") 
    else
      $logger.log_results("Data Base Check","NA","NA","passed")  
    end   
  end
  
  
  #Search for the client from clients accounts page
  
  def search_client_from_clients_accounts_page()
    begin
      dbquery("select global_cust_num from customer where display_name='"+@@c_name+"'")
      global_account_number=dbresult[0]
      $ie.link(:text,"Clients & Accounts").click
      $ie.text_field(:name,"searchNode(searchString)").set(global_account_number)
      $ie.button("Search").click
      search_name=@@c_name+": ID "+global_account_number
      $ie.link(:text,search_name).click
      assert($ie.contains_text(@@clientprop['client.PerformanceHistoryHeading']))
      $logger.log_results("client Details page","Open","opened","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("client Details page","Open","not opened","failed")
    end
  end
  
  
  #Checking whether Edit personal information link is working 
  
  def check_edit_client_personalinformation_link_from_details_page()
    begin
      $ie.link(:text,@@clientprop['client.EditPersonalInformationLink']).click
      @@client_name=(@@c_name+" "+@@clientprop['client.EditPersonalInformationTitle']).squeeze(" ")
      assert($ie.contains_text(@@client_name))
      $logger.log_results("client Details page-Edit Personnel Information","Open","opened","passed")
      $ie.button(:value,@@button_cancel).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("client Details page-Edit Personnel Information","Open","opened","failed")
    end
  end
  
  
  #Editing the client personnel information from clients details page
  
  def edit_client_personnel_enter_data(nsalutation,nfname,nmname,nsname,nlname,ngovtid,ndate,nmonth,nyear,ngender,nmstatus,nnoofchildren,nreligion,neducation,nsorftype,nsorffname,nsorfmname,nsorfsname,nsorflname,naddress1,naddress2,naddress3,ncity,nstate,ncountry,npcode,nphone,ncustom)
    begin
      @@edit_personnel=@@clientprop['client.EditPersonalInformationLink']
      $ie.link(:text,@@edit_personnel).click
      $ie.select_list(:name,"customerNameDetail[0].salutation").select_value(nsalutation)
      $ie.text_field(:name,"customerNameDetail[0].firstName").set(nfname)
      $ie.text_field(:name,"customerNameDetail[0].middleName").set(nmname)
      $ie.text_field(:name,"customerNameDetail[0].secondLastName").set(nsname)
      $ie.text_field(:name,"customerNameDetail[0].lastName").set(nlname)
      $ie.select_list(:name,"customerDetail.gender").select_value(ngender)
      $ie.select_list(:name,"customerDetail.maritalStatus").select_value(nmstatus)
      $ie.text_field(:name,"customerDetail.numChildren").set(nnoofchildren)    
      $ie.select_list(:name,"customerDetail.citizenship").select_value(nreligion)
      $ie.select_list(:name,"customerDetail.educationLevel").select_value(neducation)
      $ie.select_list(:name,"customerNameDetail[1].nameType").select_value(nsorftype)
      $ie.text_field(:name,"customerNameDetail[1].firstName").set(nsorffname)
      $ie.text_field(:name,"customerNameDetail[1].middleName").set(nsorfmname)
      $ie.text_field(:name,"customerNameDetail[1].secondLastName").set(nsorfsname)
      $ie.text_field(:name,"customerNameDetail[1].lastName").set(nsorflname)
      $ie.text_field(:name,"customerAddressDetail.line1").set(naddress1)
      $ie.text_field(:name,"customerAddressDetail.line2").set(naddress2)
      $ie.text_field(:name,"customerAddressDetail.line3").set(naddress3)
      $ie.text_field(:name,"customerAddressDetail.city").set(ncity)
      $ie.text_field(:name,"customerAddressDetail.state").set(nstate)
      $ie.text_field(:name,"customerAddressDetail.country").set(ncountry)
      $ie.text_field(:name,"customerAddressDetail.zip").set(npcode)
      $ie.text_field(:name,"customerAddressDetail.phoneNumber").set(nphone)
      $ie.text_field(:name,"customField[0].fieldValue").set(ncustom)
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@clientprop['client.EditPreviewPersonalReviewTitle']))
      $logger.log_results("Edit Personnel from details page--review&submit personal information page","n/a","n/a","passed")
      $ie.button(:value,@@button_submit).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Edit Personnel from details page--review&submit personal information page","n/a","n/a","failed")
    end
  end
  
  
  #checking whether Edit MFI information link working
  
  def check_edit_client_mfi_data_link_from_details_page()
    begin
      @@edit_mfi=@@clientprop['client.EditMfiInformationLink']
      $ie.link(:text,@@edit_mfi).click
      assert($ie.contains_text(@@edit_mfi))
      $logger.log_results("client Details page-Edit MFI Information","Open","opened","passed")
      $ie.button(:value,@@button_cancel).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("client Details page-Edit MFI Information","Open","opened","failed")
    end
  end
  
  
  #editing the MFI information from clients details page
  
  def edit_client_mfi_enter_data(nexternalid)
      begin
        @@review_mfi=@@clientprop['client.PreviewMFIInformation']
        $ie.link(:text,@@edit_mfi).click
        $ie.text_field(:name,"externalId").set(nexternalid)
        $ie.button(:value,@@button_preview).click
        assert($ie.contains_text(@@review_mfi))
        $logger.log_results("Edit Personnel from details page--review&submit MFI information page","n/a","n/a","passed")
        $ie.button(:value,@@button_submit).click
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Edit Personnel from details page--review&submit MFI information page","n/a","n/a","Failed")
      end
  end
  
  
  #Checking whether the Edit Group membership link is existed
  
  def check_edit_group_membership_link()
    begin
      @@edit_group_client=@@clientprop['client.EditGroupMembershipLink']
      assert($ie.contains_text(@@edit_group_client))
      $logger.log_results("Edit group membership link Exists",@@edit_group_client,@@edit_group_client,"Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Edit group membership link Exists",@@edit_group_client,@@edit_group_client,"Failed")    
    end
  end
  
  
  #Checking whether the Edit Group membership link is working
  
  def click_edit_group_membership_link()
    begin
      $ie.link(:text,@@edit_group_client).click
      assert($ie.contains_text($change_group))
      $logger.log_results("Edit group membership link working","should work","working","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Edit group membership link working","should work","not working","failed")
    end
  end
  
  
  #Changing the group membership
  
  def edit_group_membership()
    begin
      dbquery("select display_name from customer where customer_level_id=2 and status_id=9 and branch_id="+@@office_id+" and display_name not in ('"+@@gdisplay_name+"')")
      groupname=dbresult[0]
      $ie.text_field(:index,2).set(groupname)
      $ie.button(:index,2).click
      $ie.link(:text,groupname).click
      assert($ie.contains_text(@@edit_group_client))
      $logger.log_results("Change Group Membership confirm page","N/A","N/A","Passed")    
      $ie.button(:value,@@button_submit).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Change Group Membership confirm page","N/A","N/A","Failed")    
    end
  end
  
  
  #Checking that View all closed accounts existed and working
  
  def view_all_closed_accounts()
    begin
      @@view_all_closed_accounts_client=@@clientprop['client.ClosedAccountsLink']
      assert($ie.contains_text(@@view_all_closed_accounts_client))
      $logger.log_results("View all closed accounts link existed","NA","NA","passed")
      $ie.link(:text,@@view_all_closed_accounts_client).click
      $ie.button(:value,@@return_to_details_page).click
      assert($ie.contains_text(@@clientprop['client.AccountHeading']))
      $logger.log_results("View all closed accounts link working","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View all closed accounts link working","NA","NA","failed")   
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View all closed accounts link existed","NA","NA","failed")    
    end
  end
  
  
  #Checking that Click here to continue if Group membership is not required for your client is existed
  
  def check_create_client_out_of_group_link()
    begin
      $ie.link(:text,"Clients & Accounts").click
      $ie.link(:text,@@createclientlabel).click
      assert($ie.contains_text($Member_outof_group_link))
      $logger.log_results("Create client Out of group link","Should exist","Existed","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Create client Out of group link","Should exist","Existed","Failed")
    end
  end
  
  
  #Checking that Click here to continue if Group membership is not required for your client is working
  
  def click_create_client_out_of_group_link() 
    begin
      $ie.link(:text,$Member_outof_group_link).click
      assert($ie.contains_text($Member_select_branch))
      $logger.log_results("Page select branch out of group","Should appear","appeared","passed")
      $ie.link(:text,@@display_name).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page select branch out of group","Should appear","not appeared","failed")
    end
  end
  
  
  #Checking that create client with out admin fee
  
  def client_create_remove_admin_fee_from_review_page()
      $ie.button(:value,"Edit MFI related information").click
      search_fee=$dbh.real_query("select * from fees where category_id=2 and default_admin_fee='yes' and status=1")
      dbresult1=search_fee.fetch_row.to_a
      no_of_fee=search_fee.num_rows()
      rowf=0
      if no_of_fee=="0" then
        $looger.log_results("No Fess created for the group","","","Passed")
      else
        while (rowf < no_of_fee)
          fee_id=dbresult1[0]
          $ie.checkbox(:name,"adminFee["+String(rowf)+"].checkedFee")
          dbresult1=search_res.fetch_row.to_a
          rowf+=1
        end
      end     
      click_preview()
  end 
  # checking whether view summarized historical Data link exist
  def check_view_summarized_historical_Data_link_exist()
    begin
      assret($ie.contains_text(@@clientprop['client.HistoricalDataLink']))
      $logger.log_results("View Summarized Historical Data","Should exist","Existed","Passed")
    rescue=>e
      $logger.log_results("View Summarized Historical Data","Should exist","Existed","Failed")
    end
  end
 # checking whether view summarized historical Data link exist
 def check_view_summarized_historical_Data_link_functinality()
    begin
      $ie.link(:text,@@clientprop['client.HistoricalDataLink']).click
      assret($ie.contains_text(@@c_name +" Historical data"))
      $logger.log_results("View Summarized Historical Data","Should work","Working","Passed")
      $ie.button(:value,@@return_to_details_page).click
    rescue=>e
      $logger.log_results("View Summarized Historical Data","Should work","Working","Failed")
    end
  end
  #Selecting the loan officer while entering MFI information
  
  def select_loan_officer()
    $ie.select_list(:name,"loanOfficerId").select_value(@@personnel_id)
    meeting(@@lookup_name_client)
  end
  
  def select_office()
    begin
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and  search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      display_name=dbresult[1]
      if (display_name=="Mifos HO") then
          get_next_data
          @@office_id=dbresult[0]
          display_name=dbresult[1]
      end      
      $ie.link(:text,display_name).click 
      assert($ie.contains_text(@@client_select_client))
      $logger.log_results("page redirected to",@@client_select_client,"Page","Passed")
      rescue=>e
      $logger.log_results("page redirected to",@@client_select_client,"Page","Failed")
    end      
  end
  
  #Create client out side the group with all mandatory data
    
  def create_client_out_of_group(nsalutation,nfname,nmname,nsname,nlname,ngovtid,ndate,nmonth,nyear,ngender,nmstatus,nnoofchildren,nreligion,neducation,nsorftype,nsorffname,nsorfmname,nsorfsname,nsorflname,naddress1,naddress2,naddress3,ncity,nstate,ncountry,npcode,nphone,ncustom,nexternalid,ntdate,ntmonth,ntyear)
    begin
      select_office()
      client_create_with_all_data(nsalutation,nfname,nmname,nsname,nlname,ngovtid,ndate,nmonth,nyear,ngender,nmstatus,nnoofchildren,nreligion,neducation,nsorftype,nsorffname,nsorfmname,nsorfsname,nsorflname,naddress1,naddress2,naddress3,ncity,nstate,ncountry,npcode,nphone,ncustom)
      click_continue() 
      client_create_enter_all_data_mfi(nexternalid,ntdate,ntmonth,ntyear)
    end
  end
  
  
  #changind status when client is out side the group
  
  def change_status_client_with_out_meeting()
    $ie.link(:text,@@view_client_details).click
    dbquery("select status_id from customer where display_name='"+@@c_name+"'")
    status_id=dbresult[0]
    if status_id=="1" then
      change_status_pending_no_meting()
    elsif status_id=="2" then
      change_status_active_no_meting()
    end
  end
  
  
  #Changing status to pending when client is out side the group and no meeting created
  
  def change_status_pending_no_meting()
    begin
      $ie.link(:text,@@edit_client_status).click
      $ie.radio(:name,"statusId","2").set
      $ie.text_field(:name,"customerNote.comment").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      assert($ie.contains_text("client Status cannot be changed as loan officer is not assigned"))
      $logger.log_results("Loan Officer and Meeting date is not selected fro this client","Should not allow","Not allowing","Passes")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Loan Officer and Meeting date is not selected fro this client","Should not allow","allowing","Failed")        
    end
  end
  
  
  #Changing status to active when client is out side the group and no meeting created
  
  def change_status_active_no_meting()
    begin
      $ie.link(:text,@@edit_client_status).click
      $ie.radio(:name,"statusId","3").set
      $ie.text_field(:name,"customerNote.comment").set("AAAAA")
      $ie.button(:value,@@button_preview).click
      $ie.button(:value,@@button_submit).click
      assert($ie.contains_text("client Status cannot be changed as loan officer is not assigned"))
      $logger.log_results("Loan Officer and Meeting date is not selected fro this client","Should not allow","Not allowing","Passes")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Loan Officer and Meeting date is not selected fro this client","Should not allow","allowing","Failed")        
    end
  end
  
  
  #added method to check for label client charges  
  
  def check_client_charges_label()
    begin
      @@view_details=@@clientprop['client.viewdetails']
      @@client_charges_label=@@clientprop['client.ApplyCharges']
      $ie.link(:text,@@view_details).click
      assert($ie.contains_text(@@client_charges_label))and \
      assert($ie.document.body.innerText.scan(@@client_charges_label).size==2)
      $logger.log_results(@@client_charges_label+" label appears for customer type client","NA",@@client_charges_label,"passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results(@@client_charges_label+" label does not appear for customer type client","NA",@@client_charges_label,"failed") 
    end
  end
 
 #Check for edit branch membership link exist
 
 def check_edit_branch_member_link_exist()
  begin
    assert($ie.contains_text("Edit Branch Office membership"))
    $logger.log_results("Link","Edit branch membership","Should exist","Passed")
    rescue=>e
    $logger.log_results("Link","Edit branch membership","Should exist","Failed")
  end
 end
 
 #Check for edit branch membership link working
 
 def check_edit_branch_member_link_functionality()
  begin
    $ie.link(:text,"Edit Branch Office membership").click
    assert($ie.contains_text(@@c_name+" -"+" Edit Branch Office membership"))
    $logger.log_results("Link","Edit branch membership","Should Work","Passed")
    rescue=>e
    $logger.log_results("Link","Edit branch membership","Not Working","Failed")
  end
    
  end
 
 #Editing branch membership 
 
 def edit_branch_membership()
   begin
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and office_id not in ('"+@@office_id+"')")
      @@office_name=dbresult[1]
      $ie.link(:text,@@office_name).click
      @@header=(@@c_name+" - "+@@clientprop['client.EditPreviewPersonalReviewTitle']).squeeze(" ")
      puts @@header
      assert($ie.contains_text(@@header))
      $logger.log_results("Change Branch Membership confirm page","N/A","N/A","Passed")    
      $ie.button(:value,@@button_submit).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Change Branch Membership confirm page","N/A","N/A","Failed")    
    end
 end
 
 #Checking for add notes link
 def check_add_notes_link()
  begin
    assert($ie.contains_text(@@clientprop['client.NotesLink']))
    $logger.log_results("Link"+@@clientprop['client.NotesLink'],"Should Exist","Existed","passed")
    rescue=>e
    $logger.log_results("Link"+@@clientprop['client.NotesLink'],"Should Exist","Not Existed","Failed")
  end
 end
 
 #Clicking on Add notes link
 def click_add_notes_link()
  begin
    $ie.link(:text,@@clientprop['client.NotesLink']).click
    assert($ie.contains_text(@@c_name+" - Add note"))
    $logger.log_results("Link"+@@clientprop['client.NotesLink'],"Should Work","Working","passed")
    rescue=>e
    $logger.log_results("Link"+@@clientprop['client.NotesLink'],"Should Work","Not Working","Failed")
  end
 end
 
 #Checking for the mandatory fields in add notes page
 def check_mandatory_in_add_notes_page()
    $ie.button(:value,@@button_preview).click
    assert($ie.contains_text(@@client_note_msg))
    $logger.log_results("Mandatory Check when you don't enter any note","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Mandatory Check when you don't enter any note","N/A","N/A","Failed")
 end
 
 #Adding a note to the customer record
 def enter_data_in_add_note()
  begin
    $ie.text_field(:name,"comment").set("Adding A note")
    $ie.button(:value,@@button_preview).click
    assert($ie.contains_text(@@c_name+" - Preview note"))
    $logger.log_results("Page Redirected to Preview page","N/A","N/A","Passed")
    $ie.button(:value,@@button_submit).click
    rescue=>e
    $logger.log_results("Page not Redirected to Preview page","N/A","N/A","Failed")
  end
 end
 
 #Check for see all notes link
 def check_for_see_all_notes_link
  begin
    assert($ie.contains_text(@@clientprop['client.SeeAllNotesLink']))
    $logger.log_results("Link"+@@clientprop['client.SeeAllNotesLink'],"should Exist","Existed","Passed")
    rescue=>e
    $logger.log_results("Link"+@@clientprop['client.SeeAllNotesLink'],"should Exist","Not Existed","Failed")
  end
end

#Click on see all notes link and check the functionality
def click_see_all_notes_link()
  begin 
    $ie.link(:text,@@clientprop['client.SeeAllNotesLink']).click
    assert($ie.contains_text(@@c_name+" - Notes "))
    $logger.log_results("Link"+@@clientprop['client.SeeAllNotesLink'],"should Work","Working","Passed")
    rescue=>e
    $logger.log_results("Link"+@@clientprop['client.SeeAllNotesLink'],"should Work","Not Working","Failed")
  end
  end
end