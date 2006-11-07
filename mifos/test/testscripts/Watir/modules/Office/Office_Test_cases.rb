# This file contains all the automation test case scenario for testing the office module
# except the test which test for the invalid value at a field as we are unable to 
# capture the popup window at this point of time. 

require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/assertions'
require 'watir/WindowHelper'

#include module watir
include Watir

class Office_Test_Cases < TestClass
  

  
  #read values from xls file
  def read_office_values(rowid)
    @office_name = arrval[rowid+=1].to_s
    @office_shortname = arrval[rowid+=1].to_s
    @office_type = arrval[rowid+=1].to_s
    @office_parent = arrval[rowid+=1].to_s
    @address1 = arrval[rowid+=1].to_s  
    @address2 = arrval[rowid+=1].to_s
    @address3 = arrval[rowid+=1].to_s
    @city = arrval[rowid+=1].to_s 
    
    @state =arrval[rowid+=1].to_s
    @country =arrval[rowid+=1].to_s
    @code = arrval[rowid+=1]  
    if 0 == @code.to_i  then
      @postal_code = @code.to_s
    else 
      @postal_code = @code.to_i.to_s
    end 
    
    @telephone =arrval[rowid+=1].to_i.to_s    
    @external_id = arrval[rowid+=1].to_i.to_s
    @members_per_group = arrval[rowid+=1].to_i.to_s
    @members_per_kendra = arrval[rowid+=1].to_i.to_s
    @distance_HO_to_BO = arrval[rowid+=1].to_i.to_s
    
  end
  
  def Office_name
    @office_name
  end
  
  def Office_short_name
    @office_shortname
  end
  
  def Office_type
    @office_type
  end
  
  def Office_parent
    @office_parent
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
  
  def Code
    @code
  end
  
  def Postal_code
    @postal_code
  end
  
  def Tel
    @telephone
  end
  
  def External_id
    @external_id
  end
  
  def Members_per_grp
    @members_per_group
  end
  
  def Members_per_ken
    @members_per_kendra
  end
  
  def Distance_ho_bo
    @distance_HO_to_BO
  end
  
  #load the propertis file into a hash
  def read_from_properties_file()
    begin
      @@officeprop=load_properties("modules/propertis/OfficeUIResources.properties")
      @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
      @@fundprop=load_properties("modules/propertis/FundUIResources.properties") 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #load values from the hash into variables
  def read_values_from_hash()
    begin
      #labels
      @admin_label=@@officeprop['Office.labelLinkAdmin']
      @enter_office_information_page_label=@@officeprop['Office.labelAddNewOffice']+" "+@@officeprop['Office.labelEnterOfficeInformation']
      @review_submit_label=string_replace_message(@@officeprop['Office.labelReviewAndSubmit'],"&amp;","&")
      @review_page_label=@@officeprop['Office.labelAddNewOffice']+@review_submit_label.to_s
      #links
      @view_office_link=@@adminprop['admin.viewoff']
      @define_new_office_link=@@adminprop['admin.defnewoff']
      @view_office_hier_link=@@adminprop['admin.viewoffhier']
      @edit_office_info_link=@@officeprop['Office.edit']
      #buttons
      @cancel_button=@@officeprop['Office.cancel']
      @preview_button=@@officeprop['Office.preview']
      @submit_button=@@fundprop['funds.submit']
      #error msg's
      @office_name_msg=string_replace_message(@@officeprop['error.office.mandatory_field'],"{0}",@@officeprop['Office.officeName'])
      @short_name_msg=string_replace_message(@@officeprop['error.office.mandatory_field'],"{0}",@@officeprop['Office.officeShortName'])
      @office_type_msg=string_replace_message(@@officeprop['error.office.mandatory_field'],"{0}",@@officeprop['Office.officeType'])
      @parent_office_msg=string_replace_message(@@officeprop['error.office.mandatory_field'],"{0}",@@officeprop['Office.parentOffice'])
      @required_additional_info_msg=@@officeprop['error.office.provideadditionalInformation']
      @blankspace_msg=@@officeprop['Office.officeName.maskmsg']
      @duplicate_officename_msg=@@officeprop['error.office.duplicateName']
      @duplicate_shortname_msg=@@officeprop['error.office.duplicateShortName']
      @define_new_regional_office=@@officeprop['Office.labelAddNew']+" " +@@officeprop['Office.labelDivisionalOffice']
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #get labels for active and inactive for office from db
  def get_labels_from_db()
    begin
      dbquery("select lookup_value from lookup_value_locale where lookup_id=15")
      @active_label=dbresult[0]
      dbquery("select lookup_value from lookup_value_locale where lookup_id=16")
      @inactive_label=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end
  end
  # Login to Mifos and check the first page after login page
  def Office_login()
    begin
      start
      login($validname,$validpwd)
      db_connect
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check for admin page after login
  def Click_Admin_page()
    begin
      $ie.link(:text,@admin_label).click
      verify_admin_page()
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Verifies the admin page
  def verify_admin_page()  
    begin
      assert($ie.contains_text(@@adminprop['admin.admintasks'])) and assert($ie.contains_text(@@adminprop['admin.manageorg']))
      $logger.log_results("Office- Check for the admin page", "Click on admin link","Access to the admin page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Check for the admin page", "Click on admin link","Access to the admin page","Failed") 
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Check office link on admin page after login
  def Check_Office_Links()
    begin    
      assert($ie.link(:text,@view_office_link).exists?()) and assert($ie.link(:text,@define_new_office_link).exists?()) and assert($ie.link(:text,@view_office_hier_link).exists?())
      $logger.log_results("Office- Check for office links "+@view_office_link+","+@define_new_office_link+","+@view_office_hier_link +" on admin page", "Click on admin link","The links should be there","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Check for office links "+@view_office_link+","+@define_new_office_link+","+@view_office_hier_link +" on admin page", "Click on admin link","The links should be there","Failed")
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Check cancel in 'Define a new office' link on admin page
  def Check_New_Office_cancel()
    begin
      $ie.link(:text,@define_new_office_link).click
      $ie.button(:value,@cancel_button).click
      assert($ie.contains_text(@@adminprop['admin.admintasks']))
      $logger.log_results("Office- Check for cancel at define new office page", "Click on cancel button ","The current page should be navigated to admin page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Check for cancel at define new office page", "Click on cancel button ","The current page should be navigated to admin page","Failed")
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Check new Office link on admin page
  def Check_New_Office()
    begin
      $ie.link(:text,@admin_label).click
      $ie.link(:text,@define_new_office_link).click
      assert($ie.contains_text(@enter_office_information_page_label))
      $logger.log_results("Office- Check for Define a new office link on admin page", "Click on 'Define a new office' link","Access to the Define a new office page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Check for Define a new office link on admin page", "Click on admin 'Define a new office' link","Access to the Define a new office page","Failed")
    rescue =>excp
      quit_on_error(excp) 
    end      
  end
  
  def check_validation_error(office_name,short_name,office_type,parent_office,members_per_group,members_per_kendra)
    begin
      $ie.link(:text,@admin_label).click
      Man_New_Office()
      Man_New_Office_with_name(office_name)
      Man_New_Office_with_shortname(short_name)
      Man_New_Office_with_type(office_type)
      Man_New_Office_with_parentoff(parent_office)
      Man_new_Office_with_custom_values(members_per_group,members_per_kendra)
      
      if $ie.contains_text(@review_page_label)
        
        Man_office_duplicate_name_short_name(office_name,short_name)
        
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check for validation messages while creating new office
  def Man_New_Office()
    begin
      $ie.link(:text,@define_new_office_link).click
      $ie.button(:value,@preview_button).click
      begin 
        # assert($ie.contains_text($office_add_info_msg)) and 
        assert($ie.contains_text(@office_name_msg)) and \
        assert($ie.contains_text(@short_name_msg)) and \
        assert($ie.contains_text(@parent_office_msg)) and assert($ie.contains_text(@office_type_msg)) 
        $logger.log_results("Office- Check for validation error while creating new office", "nothing ","All validation error message","Passed")
        # $ie.link(:text,@admin_label).click
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Check for validation error while creating new office", "nothing ","All validation error message","Failed")     
      end
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Check for validation messages while creating new office with only name 
  def Man_New_Office_with_name(office_name)
    begin    
  
      $ie.text_field(:name,"officeName").set(office_name)  
      $ie.button(:value,@preview_button).click
      begin
        # assert($ie.contains_text($office_add_info_msg)) and 
        assert(!$ie.contains_text(@office_name_msg)) and assert($ie.contains_text(@short_name_msg)) \
        and assert($ie.contains_text(@office_type_msg))  and assert($ie.contains_text(@parent_office_msg))
        $logger.log_results("Office- Check for validation error while creating new office", "office name ","All validation error message","Passed")
        # $ie.link(:text,@admin_label).click
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Check for validation error while creating new office", "office name ","All validation error message","Failed")
      end
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Check for validation messages while creating new office with only name and short name
  def Man_New_Office_with_shortname(short_name)
    begin
      $ie.text_field(:name,"shortName").set(short_name)  
      $ie.button(:value,@preview_button).click
      verify_office_ShortName(short_name)
      begin 
        assert(!$ie.contains_text(@short_name_msg)) and assert($ie.contains_text(@required_additional_info_msg)) \
        and assert($ie.contains_text(@parent_office_msg))  and assert($ie.contains_text(@office_type_msg)) 
        $logger.log_results("Office- Check for validation error while creating new office", "office name and short name ","All validation error message","Passed")
        # $ie.link(:text,@admin_label).click
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Check for validation error while creating new office", "office name and short name ","All validation error message","Failed")
      end
    rescue =>excp
      quit_on_error(excp) 
    end       
  end
  
  # Check for validation messages while creating new office with only name ,short name and office type 
  def Man_New_Office_with_type(office_type)
    begin
      $ie.select_list(:name,"officeLevel").select(office_type)  
      $ie.button(:value,@preview_button).click
      begin
        #assert($ie.contains_text(@required_additional_info_msg)) \
        assert(!$ie.contains_text(@office_type_msg)) and assert($ie.contains_text(@parent_office_msg))
        $logger.log_results("Office- Check for validation error while creating new office", "office name, short name and type","All validation error message","Passed")
        #     $ie.link(:text,@admin_label).click
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Check for validation error while creating new office", "office name, short name and type","All validation error message","Failed")
      end
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Check for validation messages while creating new office with only name ,short name, office type and parent office 
  def Man_New_Office_with_parentoff(parent_office)
    begin

      $ie.select_list(:name,"parentOfficeId").select(parent_office)  
      $ie.button(:value,@preview_button).click
      begin 
        assert(!$ie.contains_text(@parent_office_msg)) 
        $logger.log_results("Office- Check for validation error while creating new office",
        "office name, short name, type and parent office","All validation error message","Passed")
        #$ie.link(:text,@admin_label).click
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Check for validation error while creating new office",
        "office name, short name, type and parent office","All validation error message","Failed")
      end
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  #input custom values
  def Man_new_Office_with_custom_values(members_per_group,members_per_kendra)
    begin
      $ie.text_field(:name,"customField[1].fieldValue").set(members_per_group)
      $ie.text_field(:name,"customField[2].fieldValue").set(members_per_kendra)
      $ie.button(:value,@preview_button).click
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def verify_office_ShortName(short_name) 
    
    b=(short_name =~ /\s+/)
    if (b!=nil) 
      begin
        # set_value_txtfield("prdOfferingShortName", short_name)      
        # $ie.button(:value,@preview_button).click
        #puts "value of b is "+b.to_s
        assert($ie.contains_text(@blankspace_msg))      
        $logger.log_results("Office- Error message appears when short_name contains space", short_name, @blankspace_msg, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Error message did not appear when short_name contains space", short_name,@blankspace_msg,"Failed")
      rescue =>excp
        quit_on_error(excp) 
      end
    else
      begin
        assert(!$ie.contains_text(@blankspace_msg))
        $logger.log_results("Office- Error message does not appear when short_name does not contain space",short_name,"No error message","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Error message appears when short_name does not contain space",short_name,"No error message","failed")
      rescue =>excp
        quit_on_error(excp) 
      end 
    end
  end 
  
  #method to check duplicate office name and short name
  def Man_office_duplicate_name_short_name(office_name,short_name)
    
    
    #Click_Admin_page()
    #Check_New_Office()
    #set_office_data(office_name, short_name, office_type, parent_office, address1 , address2, address3, city, state,
    # country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    number_of_offices=count_items("select count(DISPLAY_NAME) from OFFICE where DISPLAY_NAME='"+office_name+"'")
    number_of_short_offices=count_items("select count(OFFICE_SHORT_NAME) from OFFICE where OFFICE_SHORT_NAME='"+short_name+"'")
    if (number_of_offices.to_i > 0) and (number_of_short_offices.to_i > 0) 
      begin
        $ie.button(:value,@submit_button).click
        assert($ie.contains_text(@duplicate_officename_msg)) and assert($ie.contains_text(@duplicate_shortname_msg))
        $logger.log_results("Error message appears for duplicate office name",office_name.to_s,@duplicate_officename_msg,"passed")
        $logger.log_results("Error message appears for duplicate office short name",short_name.to_s,@duplicate_shortname_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear when both office name and shortname already exist",office_name.to_s+" , "+short_name.to_s,@duplicate_officename_msg+" and "+@duplicate_shortname_msg,"failed")
      rescue =>excp
        quit_on_error(excp) 
      end
      
    elsif (number_of_offices.to_i > 0) and (number_of_short_offices.to_i ==0)
      begin
        $ie.button(:value,@submit_button).click
        assert($ie.contains_text(@duplicate_officename_msg))
        $logger.log_results("Error message appears for duplicate office name",office_name.to_s,@duplicate_officename_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear for duplicate office name",office_name.to_s,@duplicate_officename_msg,"failed")  
      rescue =>excp
        quit_on_error(excp) 
      end
      
    elsif (number_of_offices.to_i ==0) and (number_of_short_offices.to_i >0)
      
      begin
        $ie.button(:value,@submit_button).click
        $logger.log_results("Error message appears for duplicate office short name",short_name.to_s,@duplicate_shortname_msg,"passed")                
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear for duplicate office short name",short_name.to_s,@duplicate_shortname_msg,"failed")
      rescue =>excp
        quit_on_error(excp) 
      end
      
    else
      begin
        $ie.button(:value,@submit_button).click  
      rescue =>excp
        quit_on_error(excp) 
      end
    end
  end 
  
  
  
  # Create a new office and verify the preview page and database values after submission
  def Create_New_Office(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    
    begin
      set_office_data(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,@preview_button).click  
      validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,@submit_button).click
      validate_office_creation(office_name, short_name, office_type, parent_office)
      OfficeCreation_Conformation()
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  def Create_New_Office_with_man_val(office_name, short_name, office_type, parent_office,members_per_group,members_per_kendra)
    begin
      set_office_data(office_name, short_name, office_type, parent_office,"","","","","","","","","",members_per_group, members_per_kendra,"")
      $ie.button(:value,@preview_button).click  
      #added part of 843
      $ie.button(:value,@submit_button).click
    rescue =>excp
      quit_on_error(excp) 
    end
    
  end
  #create office with only mandatory data
  
  # Check the edit data link on the preview page
  def check_editdata_onpreview(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO) 
    begin  
      set_office_data(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,@preview_button).click  
      validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,@edit_office_info_link).click
      begin	
        assert($ie.contains_text(@enter_office_information_page_label))
        $logger.log_results("Office- Check for Define a new office link on admin page", "Click on 'Define a new office' link","Access to the Define a new office page","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Check for Define a new office link on admin page", "Click on admin 'Define a new office' link","Access to the Define a new office page","Failed")    
      end
        $ie.button(:value,@preview_button).click
        validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
        $ie.button(:value,@cancel_button).click 
        verify_admin_page()         

    rescue =>excp
      quit_on_error(excp) 
    end
    
  end
  
  # Enter the new office data 
  def set_office_data(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    begin
    if($ie.contains_text(@enter_office_information_page_label))
      $logger.log_results("Office- Check for new office link on admin page", "Click on 'Define a new office' link","Access to the new office page","Passed")
    else
      $logger.log_results("Office- Check for new office link on admin page", "Click on admin 'Define a new office' link","Access to the new office page","Failed")
    end
    
    # Set the mandatory fields 
    $ie.text_field(:name,"officeName").set(office_name)
    $ie.text_field(:name,"shortName").set(short_name)
    $ie.select_list(:name,"officeLevel").select(office_type)  
    $ie.select_list(:name,"parentOfficeId").select(parent_office)
    
    # Set the fields for address(Non Mandatory)
    if(not ("" == address1))
      $ie.text_field(:name,"address.line1").set(address1)      
    end
    
    if(not ("" == address2))
      $ie.text_field(:name,"address.line2").set(address2)      
    end
    
    if(not ("" == address3))
      $ie.text_field(:name,"address.line3").set(address3)
    end
    
    if(not ("" == city))
      $ie.text_field(:name,"address.city").set(city)      
    end
    
    if(not("" == state))
      $ie.text_field(:name,"address.state").set(state)      
    end
    #commented as country field is not there
    #  if(not("" == country))
    #   $ie.text_field(:name,"address.country").set(country)   
    #  end     
    
    if(not("" == postal_code))
      $ie.text_field(:name,"address.zip").set(postal_code)      
    end     
    
    if(not("" == telephone))
      $ie.text_field(:name,"address.phoneNumber").set(telephone)      
    end      
    
    # Set the fields for Additional information 
    # These all field are depends on the configuration of the database so it might change
    # so this piece of code need to be changed accordingly  
    
    $ie.text_field(:name,"customField[0].fieldValue").set(external_id) 
    $ie.text_field(:name,"customField[1].fieldValue").set(members_per_group)
    $ie.text_field(:name,"customField[2].fieldValue").set(members_per_kendra)
    $ie.text_field(:name,"customField[3].fieldValue").set(distance_HO_to_BO) 
    rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  # Validate the preview for new check list
  def validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3, city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    begin
      if $ie.contains_text(@review_page_label) then
        $logger.log_results("New Office- Preview", "Office_Name: " + office_name.to_s , "valid preview page","Passed")
      else 
        $logger.log_results("New Office- Preview", "Office_Name: " + office_name.to_s , "valid preview page","Failed")
      end
      
      assert_on_page(@@officeprop['Office.labelOfficeName']+" " + office_name.to_s)
      assert_on_page(@@officeprop['Office.labelOfficeShortName']+" "+ short_name.to_s)
      assert_on_page(@@officeprop['Office.labelOfficeType']+" " + office_type.to_s)
      assert_on_page(@@officeprop['Office.labelParentOffice']+" " + parent_office.to_s)
      assert_on_page(address1.to_s + ", " + address2.to_s + ", " + address3.to_s )
      assert_on_page(@@officeprop['Office.labelCity']+" "  + city)
      assert_on_page(@@officeprop['Office.labelState']+" " + state)
      #assert_on_page("Country: " + country)
      assert_on_page(@@officeprop['Office.labelPostalCode']+" " + postal_code)
      assert_on_page(@@officeprop['Office.labelTelephone']+" " + telephone)
      assert_on_page("External Id: "+ external_id)
      assert_on_page(@@officeprop['Office.numberOfMembersPerGroup']+": " + members_per_group)
      assert_on_page(@@officeprop['Office.numberOfMembersPerKendra']+": " + members_per_kendra)
      assert_on_page("Distance from HO to BO for office: " +  distance_HO_to_BO)
      
    rescue =>excp
      quit_on_error(excp)       
    end 
  end  
  
  # Verify the creation of new office from the data base 
  def validate_office_creation(office_name, short_name, office_type, parent_office)
    begin
      dbquery("select office_id from office where display_name = '"+ office_name + "'")
      office_id = dbresult[0]
      
      dbquery("select of.DISPLAY_NAME, of.OFFICE_SHORT_NAME, lvl.lookup_value, of.PARENT_OFFICE_Id, of.STATUS_ID from office of, office_level ol, lookup_value_locale lvl where of.office_id = " + office_id + " and of.OFFICE_LEVEL_ID = ol.LEVEL_ID and ol.level_name_id = lvl.lookup_id")
      db_office_name = dbresult[0]
      db_short_name = dbresult[1]
      db_office_type = dbresult[2]
      parent_office_id = dbresult[3]
      db_status_id = dbresult[4]
      
      dbquery("select of.DISPLAY_NAME, lvl.lookup_value from office of, office_level ol, lookup_value_locale lvl where of.office_id = " + parent_office_id + " and of.OFFICE_LEVEL_ID = ol.LEVEL_ID and ol.level_name_id = lvl.lookup_id")
      db_parent_office_name = dbresult[0]
      db_parent_office_type = dbresult[1]
      db_parent_office = db_parent_office_type.to_s + "(" + db_parent_office_name.to_s + ")"
      
      dbcheck("Office name",office_name.to_s,db_office_name.to_s)
      dbcheck("Office short name",short_name.to_s,db_short_name.to_s)
      dbcheck("Office type",office_type.to_s,db_office_type.to_s)
      dbcheck("Parent Office",parent_office.to_s,db_parent_office.to_s)
      dbcheck("Parent Office","1",db_status_id.to_s)
    rescue =>excp
      quit_on_error(excp)        
    end 
  end  
  
  #Check for the OfficeCreation-Conformation page
  def OfficeCreation_Conformation()
    begin
      assert($ie.contains_text(@@officeprop['Office.labelCreatedSuccessfully']))and assert($ie.contains_text(@@officeprop['Office.labelViewOfficeDetails']))\
      and assert($ie.contains_text(@@officeprop['Office.labelAddNewOfficeNow']))
      $logger.log_results("Office- Office created", "Office created","The page should redirect to OfficeCreation-Conformation page","Passed")
      $ie.link(:text, @@officeprop['Office.labelAddNewOfficeNow']).click
      #  Check_New_Office()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Office created", "Office created","The page should redirect to OfficeCreation-Conformation page","Failed")     
    rescue =>excp
      quit_on_error(excp) 
    end
  end      
  
  # Mandatory check for view check list
  def check_view_office(office_name, short_name, office_type, parent_office)
    begin
      $ie.link(:text,@view_office_link).click
      $ie.link(:text, office_name).click
      begin
        assert($ie.contains_text(office_name)) and \
        assert($ie.contains_text(@@officeprop['Office.labelOfficeShortName']+" "+ short_name.to_s))and \
        assert($ie.contains_text(@@officeprop['Office.labelOfficeType']+" " + office_type.to_s)) and \
        assert($ie.contains_text(@@officeprop['Office.labelParentOffice']+" "+ parent_office.to_s))
        $logger.log_results("Office- validating the preview page for new office", "Click view office link","Valid preview page content", "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- validating the preview page for new office", "Click view office link","Valid preview page content", "Failed")
      end
    rescue =>excp
      quit_on_error(excp)        
    end 
  end 
  
  # Check for the status change from active to inactive and then inactive to active
  def check_status(office_name)
    begin
      edit_office_status(office_name, @inactive_label)
      edit_office_status(office_name, @active_label)  
      $ie.link(:text,@admin_label).click
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Change the office status
  def  edit_office_status(office_name, status)    
    begin    
      $ie.link(:text,@edit_office_info_link).click
      assert($ie.contains_text(office_name+ " - "+@edit_office_info_link))
      $logger.log_results("Office- Edit office information", "click on Edit office information","Edit page should be opened","Passed")
      $ie.select_list(:name,"officeStatus").select(status)
      $ie.button(:value,@preview_button).click
      
      if($ie.contains_text(@@officeprop['Office.labelStatus']+": " + status.to_s))
        $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Passed")        
      else
        $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Failed")
      end
      $ie.button(:value,@submit_button).click              
      verify_status_change(office_name, status)    
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Edit office information", "click on Edit office information","Edit page should be opened","Failed")
    rescue =>excp
      quit_on_error(excp)     
    end      
  end 
  
  # verifies the changed status   
  def verify_status_change(office_name, status)    
    begin
      assert($ie.contains_text(status)) and assert($ie.contains_text(office_name))
      $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Passed")       
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Failed")
    rescue =>excp
      quit_on_error(excp) 
    end
  end 
  
  #checks whether checkboxes for head office and branch office are disabled 
  def Check_office_hierarchy
    begin
      link_check(@view_office_hier_link)
      begin
        assert_equal("true",$ie.checkboxes[1].disabled.to_s) and assert_equal("true",$ie.checkboxes[5].disabled.to_s)
        $logger.log_results("checkboxes for HeadOffice and BranchOffice are disabled","NA","NA","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("checkboxes for HeadOffice and BranchOffice does not seem to be  disabled","NA","NA","failed")
      end
      $ie.button(:value,@cancel_button).click
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  #added as part of bug#660
  def check_office_level_hierarchy()
  
    $ie.link(:text,@admin_label).click  
     $ie.link(:text,@view_office_link).click

    begin
        assert($ie.contains_text(@define_new_regional_office))
        $logger.log_results("Bug#660-Check the define new Divisional Office link","Define new Divisional Office link should be present","Present","passed")
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Bug#660-Check the define new Divisional Office link","Define new Divisional Office link should be present","Not Present","failed")
        rescue =>excp
        quit_on_error(excp) 
    end
    $ie.link(:text,@admin_label).click  
    $ie.link(:text,@view_office_hier_link).click
    $ie.checkbox(:name,"subRegionalOffice").set(set_or_clear=false)  #unchecking the divisional office
    $ie.button(:value,@submit_button).click()
    $ie.link(:text,@view_office_link).click()
      begin
        assert(!($ie.contains_text(@define_new_regional_office)))
        $logger.log_results("Bug#660-Check the define new Divsional Office link","Define new Divsional Office link should not be present ","Not Present","passed")
        $ie.link(:text,@admin_label).click  
        $ie.link(:text,@view_office_hier_link).click
        $ie.checkbox(:name,"subRegionalOffice").set(set_or_clear=true)  #checking the divisional office back
        $ie.button(:value,@submit_button).click()
    
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Bug#660-Check the define new Regional Office link","Define new Regional Office link should not be present ","Present","failed")
        $ie.link(:text,@admin_label).click  
        $ie.link(:text,@view_office_hier_link).click
        $ie.checkbox(:name,"subRegionalOffice").set(set_or_clear=true)  #checking the divisional office back
        $ie.button(:value,@submit_button).click()
    
        rescue =>excp
        $ie.link(:text,@admin_label).click  
        $ie.link(:text,@view_office_hier_link).click
        $ie.checkbox(:name,"subRegionalOffice").set(set_or_clear=true)  #checking the divisional office back
         $ie.button(:value,@submit_button).click()
        quit_on_error(excp)     
      end  
  end
end


class Office 
  office_obj = Office_Test_Cases.new  
  office_obj.read_from_properties_file
  office_obj.read_values_from_hash
  office_obj.Office_login
  office_obj.get_labels_from_db()
  office_obj.Click_Admin_page
  office_obj.Check_office_hierarchy
  office_obj.Check_Office_Links
  office_obj.Check_New_Office_cancel
  #added as part of Bug#660
   office_obj.check_office_level_hierarchy()

  filename=File.expand_path( File.dirname($PROGRAM_NAME))+"/data/Office_Data.xls"
  office_obj.open(filename,2) 
  count = 0
  rowid=-1
  # mandatory checks
  while(rowid < $maxrow * $maxcol - 1)
    office_obj.read_office_values(rowid)
    office_obj.check_validation_error(office_obj.Office_name,office_obj.Office_short_name,office_obj.Office_type,office_obj.Office_parent,office_obj.Members_per_grp,office_obj.Members_per_ken)
    
    rowid+=$maxcol
  end   
  
  rowid=-1
  office_obj.open(filename,1) 
  #inserts data and validates with database
  while(rowid < $maxrow * $maxcol - 1)
    office_obj.read_office_values(rowid)
    office_obj.Check_New_Office
    office_obj.Create_New_Office(office_obj.Office_name,office_obj.Office_short_name,office_obj.Office_type,office_obj.Office_parent,\
    office_obj.Address1 ,office_obj.Address2,office_obj.Address3,office_obj.City,office_obj.State, office_obj.Country,\
    office_obj.Postal_code,office_obj.Tel,office_obj.External_id,office_obj.Members_per_grp,office_obj.Members_per_ken,office_obj.Distance_ho_bo)
    
    office_obj.check_editdata_onpreview(office_obj.Office_name,office_obj.Office_short_name,office_obj.Office_type,office_obj.Office_parent,\
    office_obj.Address1 ,office_obj.Address2,office_obj.Address3,office_obj.City,office_obj.State, office_obj.Country,\
    office_obj.Postal_code,office_obj.Tel,office_obj.External_id,office_obj.Members_per_grp,office_obj.Members_per_ken,office_obj.Distance_ho_bo)
    
    
    office_obj.check_view_office(office_obj.Office_name,office_obj.Office_short_name,office_obj.Office_type,office_obj.Office_parent)                                 
    office_obj.check_status(office_obj.Office_name)
    rowid+=$maxcol
  end
  

  office_obj.mifos_logout()  
end