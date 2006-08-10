# This file contains all the automation test case scenario for testing the office module
# except the test which test for the invalid value at a field as we are unable to 
# capture the popup window at this point of time. 

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
require 'watir/WindowHelper'


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
  
  # Login to Mifos and check the first page after login page
  def Office_login()
        start
		login($validname,$validpwd)
		if $ie.contains_text("Welcome,  "+ $validname) then
		  $logger.log_results("Office- Mandatory Check after login", "UserName: " + $validname, "Successful login","Passed")
		else 
		  $logger.log_results("Office- Mandatory Check after login", "UserName: " + $validname, "Successful login","Failed")
		end 
		db_connect
  end
    
  # Check for admin page after login
  def Click_Admin_page()
      $ie.link(:text,"Admin").click
      verify_admin_page()
  end
  
  # Verifies the admin page
  def verify_admin_page()  
    assert($ie.contains_text("Administrative tasks")) and assert($ie.contains_text("Manage organization"))
        $logger.log_results("Office- Check for the admin page", "Click on admin link","Access to the admin page","Passed")
    rescue =>e
        $logger.log_results("Office- Check for the admin page", "Click on admin link","Access to the admin page","Failed") 
  end
  
  # Check office link on admin page after login
  def Check_Office_Links()
    begin    
      assert($ie.contains_text("View offices")) and assert($ie.contains_text($new_office_link)) and assert($ie.contains_text("View office hierarchy"))
        $logger.log_results("Office- Check for office links on admin page", "Click on admin link","The links should be there","Passed")
      rescue =>e
        $logger.log_results("Office- Check for office links on admin page", "Click on admin link","The links should be there","Failed")
    end
  end
  
  # Check cancel in 'Define a new office' link on admin page
  def Check_New_Office_cancel()
    begin
      $ie.link(:text,$new_office_link).click
      $ie.button(:value,"Cancel").click
      assert($ie.contains_text("Administrative tasks"))
        $logger.log_results("Office- Check for cancel at define new office page", "Click on cancel button ","The current page should be navigated to admin page","Passed")
      rescue =>e
       $logger.log_results("Office- Check for cancel at define new office page", "Click on cancel button ","The current page should be navigated to admin page","Failed")
    end
  end
 
  # Check new Office link on admin page
  def Check_New_Office()
    begin
      $ie.link(:text,$new_office_link).click
      assert($ie.contains_text("Add a new office - Enter office information"))
        $logger.log_results("Office- Check for Define a new office link on admin page", "Click on 'Define a new office' link","Access to the Define a new office page","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Office- Check for Define a new office link on admin page", "Click on admin 'Define a new office' link","Access to the Define a new office page","Failed")
      end      
  end
  
  def check_validation_error(office_name,short_name,office_type,parent_office,members_per_group,members_per_kendra)
    Man_New_Office()
    Man_New_Office_with_name(office_name)
    Man_New_Office_with_shortname(short_name)
    Man_New_Office_with_type(office_type)
    Man_New_Office_with_parentoff(parent_office)
    Man_new_Office_with_custom_values(members_per_group,members_per_kendra)
    
    if $ie.contains_text($review_office_info)
     
      Man_office_duplicate_name_short_name(office_name,short_name)

    end
    
  end
  
  # Check for validation messages while creating new office
  def Man_New_Office()
    
      $ie.link(:text,$new_office_link).click
      $ie.button(:value,"Preview").click
      begin 
     # assert($ie.contains_text($office_add_info_msg)) and 
     assert($ie.contains_text($office_name_msg)) and assert($ie.contains_text($office_shortname_msg))\
      and assert($ie.contains_text($office_parent_msg)) and assert($ie.contains_text($office_type_msg)) 
       $logger.log_results("Office- Check for validation error while creating new office", "nothing ","All validation error message","Passed")
      # $ie.link(:text,"Admin").click
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Office- Check for validation error while creating new office", "nothing ","All validation error message","Failed")     
       end
 
  end
  
  # Check for validation messages while creating new office with only name 
  def Man_New_Office_with_name(office_name)
 
     # puts "In the fuction name only"
     # $ie.link(:text,$new_office_link).click
      $ie.text_field(:name,"officeName").set(office_name)  
      $ie.button(:value,"Preview").click
      begin
     # assert($ie.contains_text($office_add_info_msg)) and 
      assert(!$ie.contains_text($office_name_msg)) and assert($ie.contains_text($office_shortname_msg)) \
      and assert($ie.contains_text($office_type_msg))  and assert($ie.contains_text($office_parent_msg))
       $logger.log_results("Office- Check for validation error while creating new office", "office name ","All validation error message","Passed")
      # $ie.link(:text,"Admin").click
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Office- Check for validation error while creating new office", "office name ","All validation error message","Failed")
      end
  end

  # Check for validation messages while creating new office with only name and short name
  def Man_New_Office_with_shortname(short_name)
         
     # $ie.link(:text,$new_office_link).click
     # $ie.text_field(:name,"officeName").set(office_name)
      $ie.text_field(:name,"shortName").set(short_name)  
      $ie.button(:value,"Preview").click
      verify_office_ShortName(short_name)
      begin 
      assert(!$ie.contains_text($office_shortname_msg)) and assert($ie.contains_text($office_add_info_msg)) \
      and assert($ie.contains_text($office_parent_msg))  and assert($ie.contains_text($office_type_msg)) 
      $logger.log_results("Office- Check for validation error while creating new office", "office name and short name ","All validation error message","Passed")
     # $ie.link(:text,"Admin").click
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Check for validation error while creating new office", "office name and short name ","All validation error message","Failed")
      end       
  end
  
  # Check for validation messages while creating new office with only name ,short name and office type 
  def Man_New_Office_with_type(office_type)
          
     # $ie.link(:text,$new_office_link).click
     # $ie.text_field(:name,"officeName").set(office_name)
     # $ie.text_field(:name,"shortName").set(short_name)
      $ie.select_list(:name,"formOfficeType").select(office_type)  
      $ie.button(:value,"Preview").click
      begin
      #assert($ie.contains_text($office_add_info_msg)) \
      assert(!$ie.contains_text($office_type_msg)) and assert($ie.contains_text($office_parent_msg))
      $logger.log_results("Office- Check for validation error while creating new office", "office name, short name and type","All validation error message","Passed")
      #     $ie.link(:text,"Admin").click
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Check for validation error while creating new office", "office name, short name and type","All validation error message","Failed")
      end
  end
  
   # Check for validation messages while creating new office with only name ,short name, office type and parent office 
  def Man_New_Office_with_parentoff(parent_office)
         
     # $ie.link(:text,$new_office_link).click
     # $ie.text_field(:name,"officeName").set(office_name)
     # $ie.text_field(:name,"shortName").set(short_name)
     # $ie.select_list(:name,"formOfficeType").select(office_type)  
      $ie.select_list(:name,"formParentOffice").select(parent_office)  
      $ie.button(:value,"Preview").click
      begin 
      assert(!$ie.contains_text($office_parent_msg)) 
      $logger.log_results("Office- Check for validation error while creating new office",
       "office name, short name, type and parent office","All validation error message","Passed")
      #$ie.link(:text,"Admin").click
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Office- Check for validation error while creating new office",
       "office name, short name, type and parent office","All validation error message","Failed")
      end
  end
  
  #input custom values
  def Man_new_Office_with_custom_values(members_per_group,members_per_kendra)
      $ie.text_field(:name,"customField[1].fieldValue").set(members_per_group)
      $ie.text_field(:name,"customField[2].fieldValue").set(members_per_kendra)
      $ie.button(:value,"Preview").click
  end
  
  def verify_office_ShortName(short_name) 
  
    b=(short_name =~ /\s+/)
  if (b!=nil) 
      begin
     # set_value_txtfield("prdOfferingShortName", short_name)      
     # $ie.button(:value,"Preview").click
     #puts "value of b is "+b.to_s
         assert($ie.contains_text($office_blankspaces_msg))      
         $logger.log_results("Office- Error message appears when short_name contains space", short_name, $office_blankspaces_msg, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
         $logger.log_results("Office- Error message did not appear when short_name contains space", short_name,$office_blankspaces_msg,"Failed")
      end
  else
    begin
       assert(!$ie.contains_text($office_blankspaces_msg))
       $logger.log_results("Office- Error message does not appear when short_name does not contain space",short_name,"No error message","passed")
    rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Office- Error message appears when short_name does not contain space",short_name,"No error message","failed")
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
          $ie.button(:value,"Submit").click
        begin
          assert($ie.contains_text($office_dupl_name_msg)) and assert($ie.contains_text($office_dupl_shortname_msg))
          $logger.log_results("Error message appears for duplicate office name",office_name.to_s,$office_dupl_name_msg,"passed")
          $logger.log_results("Error message appears for duplicate office short name",short_name.to_s,$office_dupl_shortname_msg,"passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Error message does not appear when both office name and shortname already exist",office_name.to_s+" , "+short_name.to_s,$office_dupl_name_msg+" and "+$office_dupl_shortname_msg,"failed")
        end
      elsif (number_of_offices.to_i > 0) and (number_of_short_offices.to_i ==0)
          $ie.button(:value,"Submit").click
        begin
          assert($ie.contains_text($office_dupl_name_msg))
          $logger.log_results("Error message appears for duplicate office name",office_name.to_s,$office_dupl_name_msg,"passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Error message does not appear for duplicate office name",office_name.to_s,$office_dupl_name_msg,"failed")  
        end
      elsif (number_of_offices.to_i ==0) and (number_of_short_offices.to_i >0)
          $ie.button(:value,"Submit").click
        begin
          $logger.log_results("Error message appears for duplicate office short name",short_name.to_s,$office_dupl_shortname_msg,"passed")                
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Error message does not appear for duplicate office short name",short_name.to_s,$office_dupl_shortname_msg,"failed")
        end
      else
          $ie.button(:value,"Submit").click  
      end
   end 
       
  
  
  # Create a new office and verify the preview page and database values after submission
  def Create_New_Office(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
  
    begin
      set_office_data(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,"Preview").click  
	  validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
	  $ie.button(:value,"Submit").click
      validate_office_creation(office_name, short_name, office_type, parent_office)
      OfficeCreation_Conformation()
    end
  end
  
  def Create_New_Office_with_man_val(office_name, short_name, office_type, parent_office,members_per_group,members_per_kendra)
    begin
      set_office_data(office_name, short_name, office_type, parent_office,"","","","","","","","","",members_per_group, members_per_kendra,"")
      $ie.button(:value,"Preview").click  
      $ie.button(:value,"Submit").click
    end
  
  end
  #create office with only mandatory data
  
  # Check the edit data link on the preview page
  def check_editdata_onpreview(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO) 
    set_office_data(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    $ie.button(:value,"Preview").click  
	validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    $ie.button(:value,"Edit office information").click
	begin	
    assert($ie.contains_text("Add a new office - Enter office information"))
        $logger.log_results("Office- Check for Define a new office link on admin page", "Click on 'Define a new office' link","Access to the Define a new office page","Passed")
        $ie.button(:value,"Preview").click
        validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
        $ie.button(:value,"Cancel").click 
        verify_admin_page()         
    rescue =>e
        $logger.log_results("Office- Check for Define a new office link on admin page", "Click on admin 'Define a new office' link","Access to the Define a new office page","Failed")    
    end
  end
  
  # Enter the new office data 
  def set_office_data(office_name, short_name, office_type, parent_office, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
   if($ie.contains_text("Add a new office - Enter office information"))
       $logger.log_results("Office- Check for new office link on admin page", "Click on 'Define a new office' link","Access to the new office page","Passed")
      else
        $logger.log_results("Office- Check for new office link on admin page", "Click on admin 'Define a new office' link","Access to the new office page","Failed")
      end
      
      # Set the mandatory fields 
      $ie.text_field(:name,"officeName").set(office_name)
      $ie.text_field(:name,"shortName").set(short_name)
      $ie.select_list(:name,"formOfficeType").select(office_type)  
      $ie.select_list(:name,"formParentOffice").select(parent_office)
      
      # Set the fields for address(Non Mandatory)
      if(not ("" == address1))
        $ie.text_field(:name,"address.address1").set(address1)      
      end
      
      if(not ("" == address2))
        $ie.text_field(:name,"address.address2").set(address2)      
      end
      
      if(not ("" == address3))
        $ie.text_field(:name,"address.address3").set(address3)
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
        $ie.text_field(:name,"address.postalCode").set(postal_code)      
      end     
      
      if(not("" == telephone))
      $ie.text_field(:name,"address.telephoneNo").set(telephone)      
      end      
      
      # Set the fields for Additional information 
      # These all field are depends on the configuration of the database so it might change
      # so this piece of code need to be changed accordingly  

      $ie.text_field(:name,"customField[0].fieldValue").set(external_id) 
      $ie.text_field(:name,"customField[1].fieldValue").set(members_per_group)
      $ie.text_field(:name,"customField[2].fieldValue").set(members_per_kendra)
      $ie.text_field(:name,"customField[3].fieldValue").set(distance_HO_to_BO) 
  end 
  
  # Validate the preview for new check list
  def validate_preview_page(office_name, short_name, office_type, parent_office, address1 , address2, address3, city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    begin
      if $ie.contains_text($review_office_info) then
		  $logger.log_results("New Office- Preview", "Office_Name: " + office_name.to_s , "valid preview page","Passed")
	  else 
		  $logger.log_results("New Office- Preview", "Office_Name: " + office_name.to_s , "valid preview page","Failed")
	  end
	  puts  $ie.contains_text("Office name: " + office_name.to_s).class.to_s + " ::class"
      puts  $ie.contains_text("Office name: " + office_name.to_s).to_i.to_s + "0"
      puts  $ie.contains_text("Office short name: "+ short_name.to_s).to_i.to_s  + "1"   
      puts $ie.contains_text("Office type: " + office_type.to_s).to_i.to_s  + "2"
      puts $ie.contains_text("Parent office: " + parent_office.to_s).to_i.to_s+ "3"
      puts $ie.contains_text(address1.to_s + ", " + address2.to_s + ", " + address3.to_s ).to_i.to_s + "4"
      puts $ie.contains_text("City: " + city).to_i.to_s + "5"
      puts $ie.contains_text("State: " + state).to_i.to_s + "6"
      puts $ie.contains_text("Country: " + country).to_i.to_s + "7"
      puts $ie.contains_text("Postal Code: " + postal_code).to_i.to_s+ "8"
      puts $ie.contains_text("Telephone: " + telephone).to_i.to_s + "9"
      puts $ie.contains_text("External Id: " + external_id).to_i.to_s+ "10"
      puts $ie.contains_text("Number of Members per Group: " + members_per_group).to_i.to_s+ "11"
      puts $ie.contains_text("Number of Members per Kendra: " + members_per_kendra).to_i.to_s+ "12"
      puts $ie.contains_text("Distance from HO to BO for office: " +  distance_HO_to_BO).to_i.to_s+ "13"
       
      assert_on_page("Office name: " + office_name.to_s)
      assert_on_page("Office short name: "+ short_name.to_s)
      assert_on_page("Office type: " + office_type.to_s)
      assert_on_page("Parent office: " + parent_office.to_s)
      assert_on_page(address1.to_s + ", " + address2.to_s + ", " + address3.to_s )
      assert_on_page("City/District: " + city)
      assert_on_page("State: " + state)
     #assert_on_page("Country: " + country)
      assert_on_page("Postal Code: " + postal_code)
      assert_on_page("Telephone: " + telephone)
      assert_on_page("External Id: " + external_id)
      assert_on_page("Number of Members per Group: " + members_per_group)
      assert_on_page("Number of Members per Kendra: " + members_per_kendra)
      assert_on_page("Distance from HO to BO for office: " +  distance_HO_to_BO)
        
    #  if ($ie.contains_text("Office name: " + office_name.to_s) \
    #   and $ie.contains_text("Office short name: "+ short_name.to_s) \
    #   and $ie.contains_text("Office type: " + office_type.to_s) \
    #   and $ie.contains_text("Parent office: " + parent_office.to_s) \
    #   and $ie.contains_text(address1.to_s + "," + address2.to_s + "," + address3.to_s ) \
    #   and $ie.contains_text("City: " + city) \
    #   and $ie.contains_text("State: " + state) \
    #   and $ie.contains_text("Country: " + country) \
    #   and $ie.contains_text("Postal Code: " + postal_code)\
    #   and $ie.contains_text("Telephone: " + telephone)\
    #   and $ie.contains_text("External Id: " + external_id)\
    #   and $ie.contains_text("Number of Members per Group: " + members_per_group)\
    #   and $ie.contains_text("Number of Members per Kendra: " + members_per_kendra)\
    #   and $ie.contains_text("Distance from HO to BO for office: " +  distance_HO_to_BO)\
    #   )       
    #    $logger.log_results("Office- validating the preview page", "Click on preview button","Valid preview page content", "Passed")
    #  else
    #    $logger.log_results("Office- validating the preview page", "Click on preview button","Valid preview page content", "Failed")
    #  end          
     
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
      db_parent_office = db_parent_office_name.to_s + " (" + db_parent_office_type.to_s + ")"
      
    # if(db_office_name.to_s == office_name.to_s\
    #  and db_short_name.to_s == short_name.to_s\
    #  and db_office_type.to_s == office_type.to_s\
    #  and db_parent_office.to_s == parent_office.to_s\
    #  and 1 == status_id)
    #    $logger.log_results("Office- validating the office creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Passed")
    #  else
    #    $logger.log_results("Office- validating the office creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Failed")
    #  end   
      
      dbcheck("Office name",office_name.to_s,db_office_name.to_s)
      dbcheck("Office short name",short_name.to_s,db_short_name.to_s)
      dbcheck("Office type",office_type.to_s,db_office_type.to_s)
      dbcheck("Parent Office",parent_office.to_s,db_parent_office.to_s)
      dbcheck("Parent Office","1",db_status_id.to_s)
      
    end 
  end  
  
  #Check for the OfficeCreation-Conformation page
  def OfficeCreation_Conformation()
    assert($ie.contains_text($office_created_msg))and assert($ie.contains_text("View office details now"))\
      and assert($ie.contains_text("Add a new office"))
       $logger.log_results("Office- Office created", "Office created","The page should redirect to OfficeCreation-Conformation page","Passed")
       $ie.link(:text, "Add a new office").click
      #  Check_New_Office()
    rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Office- Office created", "Office created","The page should redirect to OfficeCreation-Conformation page","Failed")     
 end      
  
  # Mandatory check for view check list
  def check_view_office(office_name, short_name, office_type, parent_office)
    begin
    $ie.link(:text,"View offices").click
    $ie.link(:text, office_name).click
        
    if($ie.contains_text(office_name) and $ie.contains_text("Office short name: "+ short_name.to_s)and
    $ie.contains_text("Office type: " + office_type.to_s) and $ie.contains_text("Parent office: "+ parent_office.to_s))
          $logger.log_results("Office- validating the preview page for new office", "Click view office link","Valid preview page content", "Passed")
    else
          $logger.log_results("Office- validating the preview page for new office", "Click view office link","Valid preview page content", "Failed")
    end        
   end 
  end 
  
  # Check for the status change from active to inactive and then inactive to active
  def check_status(office_name)
    edit_office_status(office_name, "Inactive")
    edit_office_status(office_name, "Active")  
    $ie.link(:text,"Admin").click
  end
    
   # Change the office status
  def  edit_office_status(office_name, status)    
    begin    
      $ie.link(:text,"Edit office information").click
      assert($ie.contains_text(office_name+ " - Edit office information"))
       $logger.log_results("Office- Edit office information", "click on Edit office information","Edit page should be opened","Passed")
       $ie.select_list(:name,"formOfficeStatus").select(status)
       $ie.button(:value,"Preview").click
              
       if($ie.contains_text("Status: " + status.to_s))
        $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Passed")        
       else
        $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Failed")
       end
       $ie.button(:value,"Submit").click              
       verify_status_change(office_name, status)    
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Office- Edit office information", "click on Edit office information","Edit page should be opened","Failed")
      end      
  end 
 
    # verifies the changed status   
    def verify_status_change(office_name, status)    
     assert($ie.contains_text(status)) and assert($ie.contains_text(office_name))
       $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Passed")       
     rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Office- Edit office information", "status change","Preview page with changed status","Failed")
    end 
   
   #checks whether checkboxes for head office and branch office are disabled 
  def Check_office_hierarchy
    link_check("View office hierarchy")
    begin
    assert_equal("true",$ie.checkboxes[1].disabled.to_s) and assert_equal("true",$ie.checkboxes[5].disabled.to_s)
    $logger.log_results("checkboxes for HeadOffice and BranchOffice are disabled","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("checkboxes for HeadOffice and BranchOffice does not seem to be  disabled","NA","NA","failed")
    end
    $ie.button(:value,"Cancel").click
  end
 # To log out from the application 
   def office_logout()
    begin
      $ie.link("Logout").click
    end
    end
  
  
 end  