# This file contains all the automation test case scenario for testing the user module

require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
#require 'modules/Office/Office_Test_cases'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/assertions'
require 'watir/WindowHelper'

#include module watir
include Watir

class User_Test_Cases < TestClass
  
  #read values from the array 
  def read_user_values(rowid)
    @first_name=arrval[rowid+=1].to_s
    @middle_name=arrval[rowid+=1].to_s    
    @sec_last_name=arrval[rowid+=1].to_s
    @last_name=arrval[rowid+=1].to_s    
    @govt_id=arrval[rowid+=1].to_s    
    @e_mail=arrval[rowid+=1].to_s    
    @birth_date=arrval[rowid+=1].to_s
    @marital_status=arrval[rowid+=1].to_s    
    @gender=arrval[rowid+=1].to_s    
    @language_preferred=arrval[rowid+=1].to_s    
    @mfi_date=arrval[rowid+=1].to_s    
    @address1=arrval[rowid+=1].to_s    
    @address2=arrval[rowid+=1].to_s    
    @address3=arrval[rowid+=1].to_s    
    @city=arrval[rowid+=1].to_s    
    @state=arrval[rowid+=1].to_s    
    @country=arrval[rowid+=1].to_s    
    @postal_code=arrval[rowid+=1].to_s    
    @telephone=arrval[rowid+=1].to_s
    @user_title=arrval[rowid+=1].to_s    
    @user_hierarchy=arrval[rowid+=1].to_s   
    @roles=arrval[rowid+=1].to_s     
    @user_name=arrval[rowid+=1].to_s    
    @password=arrval[rowid+=1].to_s    
    @external_id=arrval[rowid+=1].to_i.to_s    
  end
  
  def Firstname
    @first_name
  end
  def Middlename
    @middle_name
  end
  def Second_last_name
    @sec_last_name
  end
  def Lastname
    @last_name
  end
  def Govtid
    @govt_id
  end
  def Email
    @e_mail
  end
  def Birthdate
    @birth_date
  end
  def Marital_status
    @marital_status
  end
  def Gender
    @gender
  end
  def Language_pref 
    @language_preferred
  end
  def Mfidate 
    @mfi_date
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
  def Postalcode
    @postal_code
  end
  def Tel
    @telephone
  end
  def Usertitle
    @user_title
  end
  def  Hierarchy
    @user_hierarchy
  end
  def Role
    @roles
  end
  def Username
    @user_name
  end
  def Password
    @password
  end
  def External_id
    @external_id
  end
  
  #read properties file into a hash and then use the hash for labels,buttons etc
  def read_properties()
    @personnel_properties=load_properties("modules/propertis/PersonnelUIResources.properties")
    @admin_properties=load_properties("modules/propertis/adminUIResources.properties")
  end
  
  #read values from hash
  def read_from_hash()
    begin
      #labels
      @new_user_page_label=@personnel_properties['Personnel.AddNewUser']+" - "+@personnel_properties['Personnel.EnterUserInformation']
      @review_page_label=@personnel_properties['Personnel.AddNewUser']+" - "+@personnel_properties['Personnel.ReviewSubmit']
      @user_created_label=@personnel_properties['Personnel.UserAdded']
      #links
      @admin_link=@personnel_properties['Personnel.Admin']
      @edit_user_info_link=@personnel_properties['Personnel.EditUserInformation']
      @view_user_detail_link=@personnel_properties['Personnel.ViewUserdetailsNow']
      @add_new_user_link=@personnel_properties['Personnel.AddNewUser']
      @view_system_users_link=@personnel_properties['Personnel.ViewUsers']
      #buttons
      @preview_button=@personnel_properties['button.preview']
      @cancel_button=@personnel_properties['button.cancel']
      @submit_button=@personnel_properties['button.submit']
      @edit_button=@personnel_properties['button.EditUserInformation']
      #error msg's
      @firstname_msg=@personnel_properties['error.firstName'].squeeze(" ")
      @lastname_msg=@personnel_properties['error.lastName'].squeeze(" ")
      @dateofbirth_msg=@personnel_properties['error.dob'].squeeze(" ")      
      @gender_msg=@personnel_properties['error.gender'].squeeze(" ")
      @user_hierarchy_msg=@personnel_properties['error.level'].squeeze(" ")
      @username_msg=@personnel_properties['error.username'].squeeze(" ")
      @confirm_password_msg=@personnel_properties['errors.validpassword'].squeeze(" ")
      @dupl_username_dob_msg=@personnel_properties['error.duplicate_username_or_dob'].squeeze(" ")
      @username_with_space_msg=string_replace_message(@personnel_properties['errors.spacesmask'],"{0}",@personnel_properties['Personnel.UserNameLabel'])
      @search_message=@personnel_properties['Personnel.SearchMsg']
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  #fetches labels for active and inactive from db for personnel
  def get_labels_from_db()
    begin
      dbquery("select lookup_value from lookup_value_locale where lookup_id=152")
      @active_label=dbresult[0]
      dbquery("select lookup_value from lookup_value_locale where lookup_id=153")
      @inactive_label=dbresult[0]
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Check user link on admin page after login
  def Check_User_Links()
    begin    
      assert($ie.link(:text,@personnel_properties['Personnel.ViewUsers']).exists?()) and assert($ie.link(:text,@admin_properties['admin.definenewusers']).exists?())
      $logger.log_results("Links "+@personnel_properties['Personnel.ViewUsers'].to_s+" and "+@admin_properties['admin.definenewusers'].to_s+" exist on admin page", "NA","NA","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Links "+@personnel_properties['Personnel.ViewUsers'].to_s+" and "+@admin_properties['admin.definenewusers'].to_s+" do not exist on admin page", "NA","NA","failed")
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Check cancel in 'Define a new user' link on admin page
  def Check_New_User_cancel()
    begin
      $ie.link(:text,@admin_properties['admin.definenewusers']).click
      $ie.button(:value,@personnel_properties['button.cancel']).click     
      verify_admin_page()
    rescue =>e
      quit_on_error(e)       
    end
  end
  
  # Check new User link on admin page
  def Check_New_User()
    begin
      $ie.link(:text,@personnel_properties['Personnel.Admin']).click
      $ie.link(:text,@admin_properties['admin.definenewusers']).click
      begin
        assert($ie.contains_text(@personnel_properties['Personnel.AddNewUser']+" - "+@personnel_properties['Personnel.ChooseOffice']))
        $logger.log_results("User- Check for Define a new User link on admin page", "Click on 'Define a new User' link","Access to the Define a new User page","Passed")
      rescue  Test::Unit::AssertionFailedError=>e
        $logger.log_results("User- Check for Define a new User link on admin page", "Click on admin 'Define a new User' link","Access to the Define a new User page","Failed")
      end
    rescue =>e
      quit_on_error(e)    
    end      
  end
  
  #method that calls all other mandatory checks  
  def check_validation_error(office_name, first_name, last_name, birth_date, gender, user_hierarchy, user_name, password)
    begin
      #$ie.link(:text, $new_user_link).click
      $ie.link(:text, office_name).click
      Man_New_User()
      Man_New_User_with_firstname(first_name)
      Man_New_User_with_lastname(last_name)
      Man_New_User_with_dob(birth_date)
      Man_New_User_with_gender(gender)
      Man_New_User_with_userhierarchy(user_hierarchy)
      Man_New_User_with_username(user_name)
      Man_New_User_with_password(password)
      if ($ie.contains_text(@personnel_properties['Personnel.AddNewUser']+" - "+@personnel_properties['Personnel.ReviewSubmit']))
        check_dupl_user_name_dob(user_name,first_name,last_name,birth_date)
      end
    rescue =>e
      quit_on_error(e)
    end 
    
  end 
  
  # Check for validation messages while creating new User
  def Man_New_User()
    begin  
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(@new_user_page_label)) and assert($ie.contains_text(@firstname_msg))\
      and assert($ie.contains_text(@lastname_msg)) and assert($ie.contains_text(@dateofbirth_msg))\
      and assert($ie.contains_text(@gender_msg)) and assert($ie.contains_text(@user_hierarchy_msg))\
      and assert($ie.contains_text(@username_msg)) and assert($ie.contains_text(@confirm_password_msg)) 
      $logger.log_results("User- Check for validation error while creating new User", "nothing ","All validation error message","Passed")     
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "nothing ","All validation error message","Failed")       
    rescue =>e
      quit_on_error(e) 
    end    
  end
  
  # Check for validation messages while creating new User with first name
  def Man_New_User_with_firstname(first_name)
    begin     
      $ie.text_field( :name, "firstName").set(first_name)
      $ie.button(:value,@preview_button).click
      #max_field_len(first_name,100,$max_len_first_name)
      assert(!$ie.contains_text(@firstname_msg)) and \
      assert($ie.contains_text(@lastname_msg)) and assert($ie.contains_text(@new_user_page_label)) and assert($ie.contains_text(@dateofbirth_msg))\
      and assert($ie.contains_text(@gender_msg)) and assert($ie.contains_text(@user_hierarchy_msg))\
      and assert($ie.contains_text(@username_msg)) and assert($ie.contains_text(@confirm_password_msg)) 
      $logger.log_results("User-Check for validation error while creating new User", "user-first name ","All validation error message","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "user-first name ","All validation error message","Failed")
    rescue =>e
      quit_on_error(e)          
    end
  end
  
  
  # Check for validation messages while creating new User with first name and last name
  def Man_New_User_with_lastname(last_name)
    begin     
      
      $ie.text_field( :name, "lastName").set(last_name)
      $ie.button(:value,@preview_button).click
      #max_field_len(last_name,100,$max_len_last_name)
      assert(!$ie.contains_text(@lastname_msg)) and \
      assert($ie.contains_text(@new_user_page_label)) and assert($ie.contains_text(@dateofbirth_msg))\
      and assert($ie.contains_text(@gender_msg)) and assert($ie.contains_text(@user_hierarchy_msg))\
      and assert($ie.contains_text(@username_msg)) and assert($ie.contains_text(@confirm_password_msg)) 
      $logger.log_results("User- Check for validation error while creating new User", "first name and last name ","All validation error message","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first name and last name","All validation error message","Failed")
    rescue =>e
      quit_on_error(e)          
    end
  end
  
  # Check for validation messages while creating new User with  first_name, last_name, birth_date
  def Man_New_User_with_dob(birth_date)
    begin     
      
      date_array = birth_date.split("/").to_a
      $ie.text_field( :name, "dobDD").set(date_array[0])
      $ie.text_field( :name, "dobMM").set(date_array[1])
      $ie.text_field( :name, "dobYY").set(date_array[2])      
      $ie.button( :value, @preview_button).click
      assert(!$ie.contains_text(@dateofbirth_msg)) and \
      assert($ie.contains_text(@new_user_page_label)) and \
      assert($ie.contains_text(@gender_msg)) and assert($ie.contains_text(@user_hierarchy_msg)) and \
      assert($ie.contains_text(@username_msg)) and assert($ie.contains_text(@confirm_password_msg)) 
      $logger.log_results("User- Check for validation error while creating new User", " first_name, last_name, birth_date ","All validation error message","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", " first_name, last_name, birth_date","All validation error message","Failed")
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Check for validation messages while creating new User with  first_name, last_name, birth_date, gender
  def Man_New_User_with_gender(gender)
    begin     
      $ie.select_list( :name, "gender").select(gender)      
      $ie.button(:value,@preview_button).click   
      assert(!$ie.contains_text(@gender_msg))and \
      assert($ie.contains_text(@new_user_page_label)) and assert($ie.contains_text(@user_hierarchy_msg)) and \
      assert($ie.contains_text(@username_msg)) and assert($ie.contains_text(@confirm_password_msg)) 
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender","All validation error message","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender","All validation error message","Failed")
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Check for validation messages while creating new User with  first_name, last_name, birth_date, gender, user_hierarchy
  def   Man_New_User_with_userhierarchy(user_hierarchy)
    begin     
      $ie.select_list( :name, "level").select(user_hierarchy)      
      $ie.button(:value,@preview_button).click      
      assert(!$ie.contains_text(@user_hierarchy_msg)) and assert($ie.contains_text(@new_user_page_label)) and \
      assert($ie.contains_text(@username_msg)) and assert($ie.contains_text(@confirm_password_msg)) 
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy","All validation error message","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy","All validation error message","Failed")
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Check for validation messages while creating new User with  first_name, last_name, birth_date, gender, user_hierarchy, user_name
  def   Man_New_User_with_username(user_name)
    begin     
      $ie.text_field( :name, "loginName").set(user_name)      
      $ie.button(:value,@preview_button).click   
      #max_field_len(user_name,20,$max_len_uname)
      check_for_spaces_in_username(user_name)
      assert(!$ie.contains_text(@username_msg)) and \
      assert($ie.contains_text(@new_user_page_label)) and assert($ie.contains_text(@confirm_password_msg)) 
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Failed")
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Check for validation messages while creating new User with first_name, last_name, birth_date, gender, user_hierarchy, user_name
  def   Man_New_User_with_password(password)    
    begin     
      $ie.text_field( :name, "userPassword").set(password)
      $ie.text_field( :name, "passwordRepeat").set(password)
      $ie.button(:value,@preview_button).click   
      #max_field_len(password,20,$max_len_passwd)
      assert(!$ie.contains_text(@confirm_password_msg))
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Passed")
      #$ie.button(:value,"Cancel").click   
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Failed")
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  #check for error message for duplicate user name 
  #check for  error message that is a combination of duplicate display name and date of birth   
  def check_dupl_user_name_dob(user_name,first_name,last_name,birth_date)
    begin
      @@number_of_username=count_items("select count(LOGIN_NAME) from PERSONNEL where LOGIN_NAME='"+user_name+"'")
      date_array = birth_date.split("/").to_a
      new_date=[date_array[2],date_array[1],date_array[0]].join("-")
      number_of_display_name=count_items("select count(DISPLAY_NAME)from PERSONNEL where DISPLAY_NAME='"+first_name+" "+last_name+"'")
      number_of_dob=count_items("select count(DOB)from PERSONNEL_DETAILS where DOB='"+new_date.to_s+"'")
      @dupl_username_msg=string_replace_message(@personnel_properties['error.duplicateuser'],"{0}",user_name)
      if (@@number_of_username.to_i == 0 ) and   (number_of_display_name.to_i > 0) and (number_of_dob.to_i >0)
        
        begin
          $ie.button(:value,@submit_button).click
          assert($ie.contains_text(@dupl_username_dob_msg))
          $logger.log_results("Error message appears when the combination of name and date of birth already exists",first_name+" "+last_name+","+birth_date.to_s,@dupl_username_dob_msg,"passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Error message does not appear when the combination of name and date of birth already exists",first_name+" "+last_name+","+birth_date.to_s,@dupl_username_dob_msg,"failed")      
        rescue =>e
          quit_on_error(e) 
        end
        
      elsif (@@number_of_username.to_i > 0 )
        
        begin
          $ie.button(:value,@submit_button).click
          assert($ie.contains_text(@dupl_username_msg))
          $logger.log_results("Error message appears for duplicate user name",user_name,@dupl_username_msg,"passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Error message does not appear for duplicate user name",user_name,@dupl_username_msg,"failed")
        rescue =>e
          quit_on_error(e) 
        end
        
      else
        begin
          $ie.button(:value,@submit_button).click
        rescue =>e
          quit_on_error(e) 
        end
      end
    rescue =>e
        quit_on_error(e)
    end
    
  end
  
  
  # Create a new User and verify the preview page and database values after submission
  def Create_New_User(office_name, first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
    birth_date, marital_status, gender, language_preferred, date_of_joining, address1 , address2,
    address3,city,state, country, postal_code, telephone,user_title, user_hierarchy, roles, 
    user_name, password, external_id)
    
    begin
      Check_New_User()
      $ie.link(:text, office_name).click      
      set_user_data( first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
      birth_date, marital_status, gender, language_preferred, date_of_joining, address1 , address2, address3,city,
      state, country, postal_code, telephone,user_title, user_hierarchy, roles, user_name, password, external_id)
      $ie.button(:value,@preview_button).click
      validate_preview_page(office_name, first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
      birth_date, marital_status, gender, language_preferred, date_of_joining, address1 , address2, address3,city,
      state, country, postal_code, telephone,user_title, user_hierarchy, roles, user_name, external_id)
      $ie.button(:value,@submit_button).click
      validate_user_creation(office_name, first_name, middle_name, second_middle_name, last_name, birth_date, gender, user_hierarchy, user_name)
      UserCreation_Conformation()
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Check the edit data link on the preview page
  def check_editdata_onpreview(user_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO) 
    begin
      set_user_data(user_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,@preview_button).click  
      validate_preview_page(user_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,@edit_button).click
        begin
          assert($ie.contains_text(@new_user_page_label))
          $logger.log_results("User- Check for Define a new User link on admin page", "Click on 'Define a new User' link","Access to the Define a new User page","Passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("User- Check for Define a new User link on admin page", "Click on admin 'Define a new User' link","Access to the Define a new User page","Failed")    
        end
      $ie.button(:value,@preview_button).click
      validate_preview_page(User_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      $ie.button(:value,@cancel_button).click 
      verify_admin_page()         
    
    rescue =>e
      quit_on_error(e) 
    end 
    
  end
  
  # Enter the new User data 
  def set_user_data( first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
    birth_date, marital_status, gender, language_preferred, date_of_joining, address1, address2, address3, city,
    state, country, postal_code, telephone, user_title, user_hierarchy, roles, user_name, password, external_id)
    begin      
      
      if($ie.contains_text(@new_user_page_label))
        $logger.log_results("User- Create new user page", "Click on 'Define a new User' link","Access to the new User page","Passed")
      else
        $logger.log_results("User- Create new user page", "Click on admin 'Define a new User' link","Access to the new User page","Failed")
      end
      
      # Set the User Details 
      set_value_txtfield("firstName", first_name)
      
      #commenting as these are not there
      #set_value_txtfield("personnelDetails.middleName", middle_name)
      #set_value_txtfield("personnelDetails.secondLastName", second_middle_name)
      set_value_txtfield("lastName", last_name)
      
      # set_value_txtfield("personnelDetails.governmentIdNumber", government_id)
      set_value_txtfield("emailId", e_mail)
      
      dob_array = birth_date.split("/").to_a
      set_value_txtfield("dobDD", dob_array[0])
      set_value_txtfield("dobMM", dob_array[1])
      set_value_txtfield("dobYY", dob_array[2])
      set_value_selectlist("maritalStatus", marital_status)
      set_value_selectlist("gender", gender)
      set_value_selectlist("preferredLocale", language_preferred)
      doj_array = date_of_joining.split("/").to_a
      set_value_txtfield("dateOfJoiningMFIDD", doj_array[0])
      set_value_txtfield("dateOfJoiningMFIMM", doj_array[1])
      set_value_txtfield("dateOfJoiningMFIYY", doj_array[2])
      # Set the Address Details 
      set_value_txtfield("address.line1", address1)
      set_value_txtfield("address.line2", address2)
      set_value_txtfield("address.line3", address3)
      set_value_txtfield("address.city", city) 
      set_value_txtfield("address.state", state)        
      #commenting as this is not there
      #set_value_txtfield("personnelDetails.country", country)  
      set_value_txtfield("address.zip", postal_code) 
      #set_value_txtfield("personnelDetails.telephone", telephone)   
      # Set the Permissions Details 
      set_value_selectlist("title", user_title)
      set_value_selectlist("level", user_hierarchy)
      roles_array = roles.split(",").to_a
      count = 0 
      while count < roles_array.length
        set_value_selectlist("id", roles_array[count])
        $ie.button(:value,"Add >>").click                
        count += 1
      end
      
      # Set the Login Details
      set_value_txtfield("loginName", user_name)
      set_value_txtfield("userPassword", password)
      set_value_txtfield("passwordRepeat", password)
      
      # Set the Login Details
      set_value_txtfield("customField[0].fieldValue", external_id)           
    rescue =>e
      quit_on_error(e)    
    end
  end  
  
  # Validate the preview for new check list
  def validate_preview_page(office_name, first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
    birth_date, marital_status, gender, language_preferred, date_of_joining_MFI, address1 , address2, address3,city,
    state, country, postal_code, telephone, user_title, user_hierarchy, roles, user_name, external_id)
    begin
      if  $ie.contains_text(@review_page_label) then
        $logger.log_results("New User- Preview", "new user inputs", "valid preview page","Passed")
      else 
        $logger.log_results("New User- Preview", "new user inputs" , "valid preview page","Failed")
      end
      dob_array = birth_date.split("/").to_a
      b_date=[dob_array[2],dob_array[1],dob_array[0]].join("-")	 
      
      dbquery("SELECT floor((DATEDIFF(CURDATE(), '"+b_date+"'))/365)")
      age =dbresult[0] 
      dbquery("select date_format(sysdate(),'%d/%m/%Y')")
      date_of_joining = dbresult[0] 
      
      assert_on_page(@personnel_properties['Personnel.Office']+" " + office_name.to_s)
      assert_on_page(@personnel_properties['Personnel.FirstName']+" " +first_name.to_s)
      #commented as these are not there
      #assert_on_page("Middle Name: " + middle_name.to_s)
      #assert_on_page("Second Last Name: " + second_middle_name.to_s)
      assert_on_page(@personnel_properties['Personnel.LastName']+" " +last_name.to_s)
      #assert_on_page("Government ID: " + government_id.to_s)
      assert_on_page(@personnel_properties['Personnel.Email']+" " + e_mail.to_s)
      assert_on_page(@personnel_properties['Personnel.DOB']+" " +birth_date.to_s)
      assert_on_page(@personnel_properties['Personnel.Age']+" " + age.to_s)
      assert_on_page(@personnel_properties['Personnel.Gender']+" " +gender.to_s)
      assert_on_page(@personnel_properties['Personnel.LanguagePreferred']+" " +language_preferred.to_s)
      assert_on_page(@personnel_properties['Personnel.DOJMFI']+" " +date_of_joining_MFI.to_s)
      assert_on_page(@personnel_properties['Personnel.DOJBranch']+" " + date_of_joining.to_s)
      assert_on_page(address1.to_s + ", " + address2.to_s + ", " + address3.to_s )
      assert_on_page("City/District: " + city)
      assert_on_page(@personnel_properties['Personnel.State']+" " +state)
      #assert_on_page("Country: " + country)
      assert_on_page(@personnel_properties['Personnel.PostalCode']+" " +postal_code)
      #assert_on_page("Telephone: " + telephone)
      assert_on_page(@personnel_properties['Personnel.UserTitle']+" " + user_title)
      assert_on_page(@personnel_properties['Personnel.UserHierarchy']+" " + user_hierarchy)
      assert_on_page(@personnel_properties['Personnel.Roles']+" " + roles)
      assert_on_page(@personnel_properties['Personnel.UserName']+" " + user_name)
      assert_on_page("External Id: " + external_id)
    rescue =>e
      quit_on_error(e) 
    end 
  end  
  
  # Verify the creation of new User from the data base  
  def validate_user_creation(office_name, first_name, middle_name, second_middle_name, last_name, birth_date, gender, user_hierarchy, user_name)
    begin
      #display_name = first_name.to_s + " " + middle_name.to_s + " " + second_middle_name.to_s + " " + last_name.to_s
      display_name = first_name.to_s + " " + last_name.to_s
      dbquery("select personnel_id, per.display_name, off.display_name 'office name', personnel_status, login_name, date_format(per.created_date,'%d/%m/%Y') from personnel per, office off where personnel_id = (select max(personnel_id) from personnel) and off.office_id = per.office_id")
      user_id = dbresult[0]      
      db_User_name = dbresult[1]
      db_office_name = dbresult[2]
      status_id = dbresult[3]
      db_login_name = dbresult[4]
      created_date = dbresult[5]
      
      #dbquery("select date_format(dob,'%d/%m/%Y'), lvl.lookup_value 'gender', date_format(date_of_joining_branch,'%d/%m/%Y') from personnel_details pd, lookup_value_locale lvl where pd.gender= lvl.lookup_id and pd.personnel_id = " + user_id.to_s )
      dbquery("select date_format(dob,'%d/%m/%Y'), lvl.lookup_value 'gender', date_format(date_of_joining_branch,'%d/%m/%Y') from personnel_details pd, lookup_value_locale lvl where pd.gender= lvl.lookup_id and pd.personnel_id = " + user_id.to_s )
      db_user_birth_date = dbresult[0]
      db_user_gender = dbresult[1]
      db_date_of_joining_branch = dbresult[2]
      
      dbquery("select date_format(sysdate(),'%d/%m/%Y')")
      current_date = dbresult[0] 
      
      dbcheck("office name",office_name,db_office_name)
      dbcheck("User display name",display_name,db_User_name)
      dbcheck("Login name",user_name,db_login_name)
      dbcheck("created date",current_date,created_date)
      dbcheck("branch joining date",current_date,db_date_of_joining_branch)
      dbcheck("Date of birth",birth_date,db_user_birth_date)
      dbcheck("Gender",gender,db_user_gender)
      dbcheck("status","1",status_id)
    rescue =>e
      quit_on_error(e) 
    end 
  end  
  
  #Check for the UserCreation-Conformation page
  def UserCreation_Conformation()
    begin
      assert($ie.contains_text(@user_created_label))and assert($ie.link(:text,@view_user_detail_link).exists?())\
      and assert($ie.link(:text,@add_new_user_link).exists?())
      $logger.log_results("User- User created", "User created","The page should redirect to UserCreation-Conformation page","Passed")
      #$ie.link(:text, "Add a new user").click
      #Check_New_User()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- User created", "User created","The page should redirect to UserCreation-Conformation page","Failed")     
    rescue =>e
      quit_on_error(e) 
    end
  end  
  
  #method to check when username contains spaces
  def check_for_spaces_in_username(user_name)
    b=(user_name =~ /\s+/)
    if (b!=nil) 
      begin
        assert($ie.contains_text(@username_with_space_msg))      
        $logger.log_results(" Error message appears when user_name contains space", user_name.to_s, @username_with_space_msg, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results(" Error message did not appear when user_name contains space", user_name.to_s,@username_with_space_msg,"Failed")
      end
    else
      begin
        assert(!$ie.contains_text(@username_with_space_msg))
        $logger.log_results("Error message does not appear when user_name does not contain space",user_name.to_s,"No error message","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message appears when user_name does not contain space",short_name.to_s,"No error message","failed")
      end 
    end  
    
  end  
  
  
  # check the user created in view system users page and go to the details page
  def check_view_User(office_name,first_name,last_name)
    begin
      $ie.link(:text,@admin_link).click
      $ie.link(:text,@view_system_users_link).click
      $ie.text_field(:index, 2).set("%")
      $ie.button(:index,2).click
      $ie.wait
      number_of_personnel=count_items("select count(PERSONNEL_ID) from PERSONNEL")
      number_of_pages=number_of_personnel.to_i/10
      number_of_pages=number_of_pages.ceil
      dbquery("select GLOBAL_PERSONNEL_NUM from personnel where PERSONNEL_ID=(select max(PERSONNEL_ID) from PERSONNEL)")
      global_personnel_num=dbresult[0].to_s
      exist_on_page=0
      count=1
      while (number_of_pages >0 and exist_on_page==0) 
        if ($ie.link(:text,first_name.to_s+" "+last_name.to_s).exists?())
          $logger.log_results("User- "+first_name.to_s+" "+last_name.to_s+" appears on page "+count.to_s,"NA","user should exist on page","Passed")
          exist_on_page=1
        else
          $ie.link(:text,"Next").click
          $ie.wait
          #exist_on_page=0
        end 
        number_of_pages-=1
        count+=1
      end 
      $ie.link(:text,first_name.to_s+" "+last_name.to_s).click
    rescue =>e
      quit_on_error(e)         
    end
  end 
  
  # Check for the status change from active to inactive and then inactive to active
  def check_status(user_name)
    begin
      edit_User_status(user_name, @inactive_label)
      edit_User_status(user_name, @active_label)  
      $ie.link(:text,@admin_link).click
    rescue =>e
      quit_on_error(e) 
    end
  end
  
  # Change the User status
  def  edit_User_status(user_name, status)    
    begin
      begin    
        $ie.link(:text,@edit_user_info_link).click
        assert($ie.contains_text(user_name+ " - "+@edit_user_info_link))
        $logger.log_results("User- Edit User information", "click on Edit User information","Edit page should be opened","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("User- Edit User information", "click on Edit User information","Edit page should be opened","Failed")
      end
    $ie.select_list(:name,"status").select(status)
    $ie.button(:value,@preview_button).click
    assert_on_page(@personnel_properties['Personnel.Status']+": " + status.to_s)
    $ie.button(:value,@submit_button).click              
    verify_status_change(user_name, status)    
    rescue =>e
      quit_on_error(e) 
    end      
  end 
  
  # verifies the changed status   
  def verify_status_change(user_name, status)    
    begin
      assert($ie.contains_text(status)) and assert($ie.contains_text(user_name))
      $logger.log_results("User- Edit User information", "status change","Preview page with changed status","Passed")       
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Edit User information", "status change","Preview page with changed status","Failed")
    rescue =>e
      quit_on_error(e) 
    end
  end 
  
  def count_office(query)
    dbquery(query)
    count=dbresult[0]
    return count
  end
  
  #check to see if a branch office is available
  def check_office()
    count=count_office("select count(*) from office where office_level_id=5 and status_id=1")
    if (count==0)
      $logger.log_results("Quitting test : Need atleast one Branch office to continue","NA","NA","NA")
      mifos_logout
    end
  end
  # To log out from the application 
  def user_logout()
    mifos_logout()
  end  
  
  #added as part og bug# 948
  def check_viewuserlink_on_blueband()
    begin
       $ie.link(:text,@admin_link).click
       $ie.link(:text,@view_system_users_link).click
       #dbquery("select first_name,last_name,middle_name,second_last_name from personnel_details where personnel_id =(select personnel_id  from personnel where level_id=1 limit 1)")
        dbquery("select first_name,last_name,middle_name,second_last_name from personnel_details where personnel_id =(select personnel_id  from personnel where level_id=1 and personnel_id=11)")
       first_name=dbresult[0]
       last_name=dbresult[1]
       middle_name=dbresult[2]
       second_last_name=dbresult[3]
       full_name=first_name.to_s+" "+last_name.to_s+" "+second_last_name.to_s+" "+middle_name.to_s
       $ie.form(:name,"personActionForm").text_field(:name,"searchString").set(first_name)
       $ie.form(:name,"personActionForm").button(:value,"Search").click()
       $ie.link(:text,full_name.to_s.strip()).click
       $ie.link(:text,@edit_user_info_link).click
       $ie.link(:text,@view_system_users_link).click 
       assert($ie.contains_text(@search_message))
       $logger.log_results("Bug#948-View system user link on blueband", "Click on the View system user link","Should display the search page for system users","Passed")       
       rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Bug#948-View system user link on blueband", "Click on the View system user link","Did not display the search page for system users","failed")       
       rescue =>e
       quit_on_error(e) 
   end  
  end #end of check_viewuserlink_on_blueband
  
end  


class User 
  user_obj = User_Test_Cases.new  
  user_obj.mifos_login
  user_obj.read_properties
  user_obj.read_from_hash
  user_obj.get_labels_from_db
  user_obj.Click_Admin_page
  
  user_obj.check_office
  user_obj.dbquery("select display_name from office where office_level_id=5 and status_id=1")
  Office_name=user_obj.dbresult[0]
  user_obj.Check_New_User_cancel
  user_obj.Check_New_User
  
  #read values for personnel from the data file
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/User_Data.xls"
  #input values for user start
  user_obj.open(filename,2) 
  rowid=-1
  #loop to check mandatory checks for different set of data given in worksheet2
  while(rowid < $maxrow * $maxcol - 1)
    user_obj.read_user_values(rowid)
    user_obj.check_validation_error(Office_name,user_obj.Firstname,user_obj.Lastname,user_obj.Birthdate,user_obj.Gender, user_obj.Hierarchy, user_obj.Username, user_obj.Password)
    user_obj.Check_New_User()
    rowid+=$maxcol
  end
  
  user_obj.open(filename,1)
  rowid=-1 
  #loop through different set of data given in worksheet1
  while(rowid < $maxrow * $maxcol - 1)
    user_obj.read_user_values(rowid)
    user_obj.Create_New_User(Office_name, user_obj.Firstname,user_obj.Middlename,user_obj.Second_last_name,user_obj.Lastname,user_obj.Govtid, user_obj.Email,
    user_obj.Birthdate,user_obj.Marital_status,user_obj.Gender,user_obj.Language_pref,user_obj.Mfidate,user_obj.Address1 ,user_obj.Address2,
    user_obj.Address3,user_obj.City, user_obj.State, user_obj.Country, user_obj.Postalcode, user_obj.Tel,user_obj.Usertitle, user_obj.Hierarchy,
    user_obj.Role, user_obj.Username, user_obj.Password, user_obj.External_id)
    
    user_obj.check_view_User(Office_name,user_obj.Firstname,user_obj.Lastname)
    user_obj.check_status(user_obj.Firstname+" "+user_obj.Lastname)
    user_obj.Check_New_User
    rowid=rowid+($maxcol)
  end

  user_obj.check_viewuserlink_on_blueband()
  user_obj.user_logout
  
end
