# This file contains all the automation test case scenario for testing the user module

require 'watir'
require 'English'
include Watir
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
#require 'test/unit/ui/console/testrunner'
require 'test/unit/assertions'
require 'watir/WindowHelper'

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
   


  # Check user link on admin page after login
  def Check_User_Links()
    begin    
      assert($ie.contains_text("View system users")) and assert($ie.contains_text($new_user_link))
        $logger.log_results("User- Check for user links on admin page", "Click on admin link","The links should be there","Passed")
      rescue =>e
        $logger.log_results("User- Check for user links on admin page", "Click on admin link","The links should be there","Failed")
    end
  end
  
  # Check cancel in 'Define a new user' link on admin page
  def Check_New_User_cancel()
    begin

      $ie.link(:text,$new_user_link).click
      $ie.button(:value,"Cancel").click     
      verify_admin_page()      
    end
  end
 
  # Check new User link on admin page
  def Check_New_User()
    begin
      $ie.link(:text,"Admin").click
      $ie.link(:text, $new_user_link).click
      assert($ie.contains_text("Add new user - Choose office"))
        $logger.log_results("User- Check for Define a new User link on admin page", "Click on 'Define a new User' link","Access to the Define a new User page","Passed")
      rescue =>e
        $logger.log_results("User- Check for Define a new User link on admin page", "Click on admin 'Define a new User' link","Access to the Define a new User page","Failed")
    end      
  end
    
  def check_validation_error(office_name, first_name, last_name, birth_date, gender, user_hierarchy, user_name, password)
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
      if ($ie.contains_text($review_user_info))
        check_dupl_user_name_dob(user_name,first_name,last_name,birth_date)
      end
     # $ie.link(:text,"Admin").click
  end 
  
  # Check for validation messages while creating new User
  def Man_New_User()
    begin  
      $ie.button(:value,"Preview").click
      assert($ie.contains_text($new_user_page_msg)) and assert($ie.contains_text($user_firstname_msg))\
      and assert($ie.contains_text($user_lastname_msg)) and assert($ie.contains_text($user_dob_msg))\
      and assert($ie.contains_text($user_gender_msg)) and assert($ie.contains_text($user_userhierarchy_msg))\
      and assert($ie.contains_text($user_name_msg)) and assert($ie.contains_text($user_pwd_msg)) 
       $logger.log_results("User- Check for validation error while creating new User", "nothing ","All validation error message","Passed")     
      rescue  Test::Unit::AssertionFailedError=>e
       $logger.log_results("User- Check for validation error while creating new User", "nothing ","All validation error message","Failed")       
     end    
  end
  
  # Check for validation messages while creating new User with first name
  def Man_New_User_with_firstname(first_name)
  begin     
      $ie.text_field( :name, "personnelDetails.firstName").set(first_name)
      
      #$ie.text_field( :name, "personnelDetails.lastName").set(last_name)
      $ie.button(:value,"Preview").click
      max_field_len(first_name,100,$max_len_first_name)
      assert(!$ie.contains_text($user_firstname_msg)) and \
      assert($ie.contains_text($user_lastname_msg)) and assert($ie.contains_text($new_user_page_msg)) and assert($ie.contains_text($user_dob_msg))\
      and assert($ie.contains_text($user_gender_msg)) and assert($ie.contains_text($user_userhierarchy_msg))\
      and assert($ie.contains_text($user_name_msg)) and assert($ie.contains_text($user_pwd_msg)) 
      
       $logger.log_results("User-Check for validation error while creating new User", "user-first name ","All validation error message","Passed")
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "user-first name ","All validation error message","Failed")
      end
  end
  
  
  # Check for validation messages while creating new User with first name and last name
  def Man_New_User_with_lastname(last_name)
  begin     
     # $ie.text_field( :name, "personnelDetails.firstName").set(first_name)
      $ie.text_field( :name, "personnelDetails.lastName").set(last_name)
      $ie.button(:value,"Preview").click
      max_field_len(last_name,100,$max_len_last_name)
      assert(!$ie.contains_text($user_lastname_msg)) and \
      assert($ie.contains_text($new_user_page_msg)) and assert($ie.contains_text($user_dob_msg))\
      and assert($ie.contains_text($user_gender_msg)) and assert($ie.contains_text($user_userhierarchy_msg))\
      and assert($ie.contains_text($user_name_msg)) and assert($ie.contains_text($user_pwd_msg)) 
      
       $logger.log_results("User- Check for validation error while creating new User", "first name and last name ","All validation error message","Passed")
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first name and last name","All validation error message","Failed")
      end
  end

 # Check for validation messages while creating new User with  first_name, last_name, birth_date
  def Man_New_User_with_dob(birth_date)
    begin     
      #$ie.text_field( :name, "personnelDetails.firstName").set(first_name)
      #$ie.text_field( :name, "personnelDetails.lastName").set(last_name)
      
      date_array = birth_date.split("/").to_a
      $ie.text_field( :name, "dobDD").set(date_array[0])
      $ie.text_field( :name, "dobMM").set(date_array[1])
      $ie.text_field( :name, "dobYY").set(date_array[2])      
      $ie.button( :value, "Preview").click
      assert(!$ie.contains_text($user_dob_msg)) and \
      assert($ie.contains_text($new_user_page_msg))\
      and assert($ie.contains_text($user_gender_msg)) and assert($ie.contains_text($user_userhierarchy_msg))\
      and assert($ie.contains_text($user_name_msg)) and assert($ie.contains_text($user_pwd_msg)) 
       $logger.log_results("User- Check for validation error while creating new User", " first_name, last_name, birth_date ","All validation error message","Passed")
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", " first_name, last_name, birth_date","All validation error message","Failed")
      end
  end

 # Check for validation messages while creating new User with  first_name, last_name, birth_date, gender
  def Man_New_User_with_gender(gender)
    begin     
     # $ie.text_field( :name, "personnelDetails.firstName").set(first_name)
     # $ie.text_field( :name, "personnelDetails.lastName").set(last_name)
    
     # date_array = birth_date.split("/").to_a  
     # $ie.text_field( :name, "dobDD").set(date_array[0])
     # $ie.text_field( :name, "dobMM").set(date_array[1])
     # $ie.text_field( :name, "dobYY").set(date_array[2])
      
      $ie.select_list( :name, "personnelDetails.gender").select(gender)      
      
      $ie.button(:value,"Preview").click   
      assert(!$ie.contains_text($user_gender_msg))and \
      assert($ie.contains_text($new_user_page_msg)) and assert($ie.contains_text($user_userhierarchy_msg))\
      and assert($ie.contains_text($user_name_msg)) and assert($ie.contains_text($user_pwd_msg)) 
       $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender","All validation error message","Passed")
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender","All validation error message","Failed")
      end
  end

 # Check for validation messages while creating new User with  first_name, last_name, birth_date, gender, user_hierarchy
 def   Man_New_User_with_userhierarchy(user_hierarchy)
    begin     
     # $ie.text_field( :name, "personnelDetails.firstName").set(first_name)
     # $ie.text_field( :name, "personnelDetails.lastName").set(last_name)
    
     # date_array = birth_date.split("/").to_a  
     # $ie.text_field( :name, "dobDD").set(date_array[0])
     # $ie.text_field( :name, "dobMM").set(date_array[1])
     # $ie.text_field( :name, "dobYY").set(date_array[2])
      
     # $ie.select_list( :name, "personnelDetails.gender").select(gender)      
      $ie.select_list( :name, "level.levelId").select(user_hierarchy)      
      
      $ie.button(:value,"Preview").click      
      
      assert(!$ie.contains_text($user_userhierarchy_msg)) and assert($ie.contains_text($new_user_page_msg))\
      and assert($ie.contains_text($user_name_msg)) and assert($ie.contains_text($user_pwd_msg)) 
       $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy","All validation error message","Passed")
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy","All validation error message","Failed")
      end
  end

 # Check for validation messages while creating new User with  first_name, last_name, birth_date, gender, user_hierarchy, user_name
 def   Man_New_User_with_username(user_name)
    begin     
     # $ie.text_field( :name, "personnelDetails.firstName").set(first_name)
     # $ie.text_field( :name, "personnelDetails.lastName").set(last_name)
    
     # date_array = birth_date.split("/").to_a  
     # $ie.text_field( :name, "dobDD").set(date_array[0])
     # $ie.text_field( :name, "dobMM").set(date_array[1])
     # $ie.text_field( :name, "dobYY").set(date_array[2])
      
     # $ie.select_list( :name, "personnelDetails.gender").select(gender)      
     # $ie.select_list( :name, "level.levelId").select(user_hierarchy)      
      $ie.text_field( :name, "userName").set(user_name)      
      
      $ie.button(:value,"Preview").click   
      max_field_len(user_name,20,$max_len_uname)
      check_for_spaces_in_username(user_name)
      assert(!$ie.contains_text($user_name_msg)) and \
      assert($ie.contains_text($new_user_page_msg)) and assert($ie.contains_text($user_pwd_msg)) 
       $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Passed")
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Failed")
      end
  end

 # Check for validation messages while creating new User with first_name, last_name, birth_date, gender, user_hierarchy, user_name
 def   Man_New_User_with_password(password)    
    begin     
     # $ie.text_field( :name, "personnelDetails.firstName").set(first_name)
     # $ie.text_field( :name, "personnelDetails.lastName").set(last_name)
    
     # date_array = birth_date.split("/").to_a
     # $ie.text_field( :name, "dobDD").set(date_array[0])
     # $ie.text_field( :name, "dobMM").set(date_array[1])
     # $ie.text_field( :name, "dobYY").set(date_array[2])
      
     # $ie.select_list( :name, "personnelDetails.gender").select(gender)      
     # $ie.select_list( :name, "level.levelId").select(user_hierarchy)      
     # $ie.text_field( :name, "userName").set(user_name)      
      $ie.text_field( :name, "password").set(password)
      $ie.text_field( :name, "passwordRepeat").set(password)
            
      $ie.button(:value,"Preview").click   
      max_field_len(password,20,$max_len_passwd)
      assert(!$ie.contains_text($user_pwd_msg))
       $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Passed")
      # $ie.button(:value,"Cancel").click   
      rescue  Test::Unit::AssertionFailedError=>e
       $logger.log_results("User- Check for validation error while creating new User", "first_name, last_name, birth_date, gender, user_hierarchy, user_name","All validation error message","Failed")
      end
  end
  
    #check for error message for duplicate user name 
    #check for  error message that is a combination of duplicate display name and date of birth   
  def check_dupl_user_name_dob(user_name,first_name,last_name,birth_date)
    @@number_of_username=count_items("select count(LOGIN_NAME) from PERSONNEL where LOGIN_NAME='"+user_name+"'")
    date_array = birth_date.split("/").to_a
     new_date=[date_array[2],date_array[1],date_array[0]].join("-")
     number_of_display_name=count_items("select count(DISPLAY_NAME)from PERSONNEL where DISPLAY_NAME='"+first_name+" "+last_name+"'")
     number_of_dob=count_items("select count(DOB)from PERSONNEL_DETAILS where DOB='"+new_date.to_s+"'")
     
   # puts "count is"+@@number_of_username+" for username "+user_name.to_s
    if (@@number_of_username.to_i == 0 ) and   (number_of_display_name.to_i > 0) and (number_of_dob.to_i >0)
        $ie.button(:value,"Submit").click
      begin
        assert($ie.contains_text($dupl_name_dob_msg))
        $logger.log_results("Error message appears when the combination of name and date of birth already exists",first_name+" "+last_name+","+birth_date.to_s,$dupl_name_dob_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear when the combination of name and date of birth already exists",first_name+" "+last_name+","+birth_date.to_s,$dupl_name_dob_msg,"failed")      
      end
    elsif (@@number_of_username.to_i > 0 )
        $ie.button(:value,"Submit").click
      begin
        assert($ie.contains_text($dupl_user_name_msg_0+user_name.to_s+$dupl_user_name_msg_1))
        $logger.log_results("Error message appears for duplicate user name",user_name,$dupl_user_name_msg_0+user_name.to_s+$dupl_user_name_msg_1,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear for duplicate user name",user_name,$dupl_user_name_msg_0+user_name.to_s+$dupl_user_name_msg_1,"failed")
      end
    else
        $ie.button(:value,"Submit").click
    end
  end
  


  
  # checks for the unique User name functionality
  def create_User_duplicate_name(user_name, short_name, user_type, parent_user, address1 , address2, address3, city, state,
       country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
      Click_Admin_page()
      Check_New_User()
      set_User_data(user_name, short_name, user_type, parent_user, address1 , address2, address3, city, state,
       country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
       
      $ie.button(:value,"Preview").click
	  validate_preview_page(user_name, short_name, user_type, parent_user, address1 , address2, address3,city,
	  state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)

  	  $ie.button(:value,"Submit").click
  	  
  	  assert($ie.contains_text("This user name already exists in the application. Please enter a different name.")) 
        $logger.log_results("User- Check for creating new User with an existing User name",
        "existing User name","Name validation error message","Passed")
        $ie.button(:value,"Cancel").click
      rescue =>e
        $logger.log_results("User- Check for creating new User with an existing User name",
       "existing User name","Name validation error message","Failed")
  end 
  
  # Create a new User and verify the preview page and database values after submission
  def Create_New_User(office_name, first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
   birth_date, marital_status, gender, language_preferred, date_of_joining, address1 , address2, address3,city,
    state, country, postal_code, telephone,user_title, user_hierarchy, roles, user_name, password, external_id)
    
    begin
      Check_New_User()
      $ie.link(:text, office_name).click      
      set_user_data( first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
          birth_date, marital_status, gender, language_preferred, date_of_joining, address1 , address2, address3,city,
          state, country, postal_code, telephone,user_title, user_hierarchy, roles, user_name, password, external_id)
          
       $ie.button(:value,"Preview").click
      
	  validate_preview_page(office_name, first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
          birth_date, marital_status, gender, language_preferred, date_of_joining, address1 , address2, address3,city,
          state, country, postal_code, telephone,user_title, user_hierarchy, roles, user_name, external_id)
          
	  $ie.button(:value,"Submit").click
	  	  
      validate_user_creation(office_name, first_name, middle_name, second_middle_name, last_name, birth_date, gender, user_hierarchy, user_name)

      UserCreation_Conformation()
    end
  end
    
  # Check the edit data link on the preview page
  def check_editdata_onpreview(user_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO) 
  
    set_user_data(user_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    $ie.button(:value,"Preview").click  
	validate_preview_page(user_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
    $ie.button(:value,"Edit User information").click
    		
    assert($ie.contains_text("Add a new User - Enter User information"))
        $logger.log_results("User- Check for Define a new User link on admin page", "Click on 'Define a new User' link","Access to the Define a new User page","Passed")
        $ie.button(:value,"Preview").click
        validate_preview_page(User_name, short_name, user_type, parent_user, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
        $ie.button(:value,"Cancel").click 
        verify_admin_page()         
    rescue =>e
        $logger.log_results("User- Check for Define a new User link on admin page", "Click on admin 'Define a new User' link","Access to the Define a new User page","Failed")    
  end
  
  # Enter the new User data 
  def set_user_data( first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
          birth_date, marital_status, gender, language_preferred, date_of_joining, address1, address2, address3, city,
          state, country, postal_code, telephone, user_title, user_hierarchy, roles, user_name, password, external_id)
      
          if($ie.contains_text("Add a new user - Enter user information"))
            $logger.log_results("User- Create new user page", "Click on 'Define a new User' link","Access to the new User page","Passed")
          else
            $logger.log_results("User- Create new user page", "Click on admin 'Define a new User' link","Access to the new User page","Failed")
          end
             
        # Set the User Details 
        set_value_txtfield("personnelDetails.firstName", first_name)
        
        #commenting as these are not there
        #set_value_txtfield("personnelDetails.middleName", middle_name)
        #set_value_txtfield("personnelDetails.secondLastName", second_middle_name)
        set_value_txtfield("personnelDetails.lastName", last_name)
            
       # set_value_txtfield("personnelDetails.governmentIdNumber", government_id)
        set_value_txtfield("emailId", e_mail)
           
        dob_array = birth_date.split("/").to_a
        set_value_txtfield("dobDD", dob_array[0])
        set_value_txtfield("dobMM", dob_array[1])
        set_value_txtfield("dobYY", dob_array[2])
        
        set_value_selectlist("personnelDetails.maritalStatus", marital_status)
        
        set_value_selectlist("personnelDetails.gender", gender)
        set_value_selectlist("preferredLocale.localeId", language_preferred)
        
        doj_array = date_of_joining.split("/").to_a
        set_value_txtfield("dateOfJoiningMFIDD", doj_array[0])
        set_value_txtfield("dateOfJoiningMFIMM", doj_array[1])
        set_value_txtfield("dateOfJoiningMFIYY", doj_array[2])

        # Set the Address Details 
        set_value_txtfield("personnelDetails.address1", address1)
        set_value_txtfield("personnelDetails.address2", address2)
        set_value_txtfield("personnelDetails.address3", address3)
        set_value_txtfield("personnelDetails.city", city) 
        set_value_txtfield("personnelDetails.state", state)        
        #commenting as this is not there
        #set_value_txtfield("personnelDetails.country", country)  
        set_value_txtfield("personnelDetails.postalCode", postal_code) 
        #set_value_txtfield("personnelDetails.telephone", telephone)   
          
        # Set the Permissions Details 
        set_value_selectlist("title", user_title)
        set_value_selectlist("level.levelId", user_hierarchy)
        
        roles_array = roles.split(",").to_a
        count = 0 
        while count < roles_array.length
        set_value_selectlist("LeftSelect", roles_array[count])
        $ie.button(:value,"Add >>").click                
        count += 1
        end
                                
        # Set the Login Details
        set_value_txtfield("userName", user_name)
        set_value_txtfield("password", password)
        set_value_txtfield("passwordRepeat", password)
                
        # Set the Login Details
        set_value_txtfield("customField[0].fieldValue", external_id)           
 end 
  
  # Validate the preview for new check list
  def validate_preview_page(office_name, first_name, middle_name, second_middle_name, last_name, government_id, e_mail,
          birth_date, marital_status, gender, language_preferred, date_of_joining_MFI, address1 , address2, address3,city,
          state, country, postal_code, telephone, user_title, user_hierarchy, roles, user_name, external_id)
    begin
      if $ie.contains_text($review_user_info) then
		  $logger.log_results("New User- Preview", "new user inputs", "valid preview page","Passed")
	  else 
		  $logger.log_results("New User- Preview", "new user inputs" , "valid preview page","Failed")
	  end
	  dob_array = birth_date.split("/").to_a
      b_date=[dob_array[2],dob_array[1],dob_array[0]].join("-")	 
	  #puts birth_date
	  dbquery("SELECT floor((DATEDIFF(CURDATE(), '"+b_date+"'))/365)")
	  age =dbresult[0] 
	  #puts age	  
	  dbquery("select date_format(sysdate(),'%d/%m/%Y')")
	  date_of_joining = dbresult[0] 

      #if ($ie.contains_text("Office: " + office_name.to_s) \
      # and $ie.contains_text("First Name: " + first_name.to_s)\
      # and $ie.contains_text("Middle Name: " + middle_name.to_s)\
      # and $ie.contains_text("Second Last Name: " + second_middle_name.to_s)\
      # and $ie.contains_text("Last Name: " + last_name.to_s) \
      # and $ie.contains_text("Government ID: " + government_id.to_s) \
      # and $ie.contains_text("Email: " + e_mail.to_s) \
      # and $ie.contains_text("Date of Birth: " + birth_date.to_s)\
      # and $ie.contains_text("Age: " + age.to_s) \
      # and $ie.contains_text("Gender: " + gender.to_s) \
      # and $ie.contains_text("Language Preferred: " + language_preferred.to_s)\
      # and $ie.contains_text("Date of Joining MFI: " + date_of_joining_MFI.to_s) \
      # and $ie.contains_text("Date of Joining Branch: " + date_of_joining.to_s)\
      # and $ie.contains_text(address1.to_s + "," + address2.to_s + "," + address3.to_s ) \
      # and $ie.contains_text("City: " + city) \
      # and $ie.contains_text("State: " + state) \
      # and $ie.contains_text("Country: " + country) \
      # and $ie.contains_text("Postal Code: " + postal_code)\
      # and $ie.contains_text("Telephone: " + telephone)\
      # and $ie.contains_text("User Title: " + user_title)\
      # and $ie.contains_text("User Hierarchy: " + user_hierarchy)\
      # and $ie.contains_text("Roles: " + roles)\
      # and $ie.contains_text("User Name: " + user_name)\
      # and $ie.contains_text("External Id: " + external_id)\
      #        )       
       # $logger.log_results("User- validating the preview page", "Click on preview button","Valid preview page content", "Passed")
      #else
      #  $logger.log_results("User- validating the preview page", "Click on preview button","Valid preview page content", "Failed")
     # end     
     assert_on_page("Office: " + office_name.to_s)
     assert_on_page("First Name: " + first_name.to_s)
     #commented as these are not there
     #assert_on_page("Middle Name: " + middle_name.to_s)
     #assert_on_page("Second Last Name: " + second_middle_name.to_s)
     assert_on_page("Last Name: " + last_name.to_s)
     #assert_on_page("Government ID: " + government_id.to_s)
     assert_on_page("Email: " + e_mail.to_s)
     assert_on_page("Date of Birth: " + birth_date.to_s)
     assert_on_page("Age: " + age.to_s)
     assert_on_page("Gender: " + gender.to_s)
     assert_on_page("Language Preferred: " + language_preferred.to_s)
     assert_on_page("Date of Joining MFI: " + date_of_joining_MFI.to_s)
     assert_on_page("Date of Joining Branch: " + date_of_joining.to_s)
     assert_on_page(address1.to_s + ", " + address2.to_s + ", " + address3.to_s )
     
     assert_on_page("City/District: " + city)
     assert_on_page("State: " + state)
     #assert_on_page("Country: " + country)
     assert_on_page("Postal Code: " + postal_code)
     #assert_on_page("Telephone: " + telephone)
     assert_on_page("User Title: " + user_title)
     assert_on_page("User Hierarchy: " + user_hierarchy)
     assert_on_page("Roles: " + roles)
     assert_on_page("User Name: " + user_name)
     assert_on_page("External Id: " + external_id)
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
      
     # if(db_office_name.to_s == office_name.to_s\
     # and db_User_name.to_s == display_name.to_s\
     # and db_login_name.to_s == user_name.to_s\
     # and db_parent_User.to_s == user_name.to_s\
     # and created_date.to_s == current_date.to_s\
     # and db_date_of_joining_branch.to_s == current_date.to_s\
     # and db_user_birth_date.to_s ==  birth_date.to_s\
     # and db_user_gender.to_s ==  gender.to_s\
     # and 1 == status_id)
      #  $logger.log_results("User- validating the User creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Passed")
      #else
      #  $logger.log_results("User- validating the User creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Failed")
      #end  
      dbcheck("office name",office_name,db_office_name)
      dbcheck("User display name",display_name,db_User_name)
      dbcheck("Login name",user_name,db_login_name)
      dbcheck("created date",current_date,created_date)
      dbcheck("branch joining date",current_date,db_date_of_joining_branch)
      dbcheck("Date of birth",birth_date,db_user_birth_date)
      dbcheck("Gender",gender,db_user_gender)
      dbcheck("status","1",status_id)
    end 
  end  
  
  #Check for the UserCreation-Conformation page
  def UserCreation_Conformation()
    assert($ie.contains_text($user_created_msg))and assert($ie.contains_text("View user details now"))\
      and assert($ie.contains_text("Add a new user"))
       $logger.log_results("User- User created", "User created","The page should redirect to UserCreation-Conformation page","Passed")
       #$ie.link(:text, "Add a new user").click
       #Check_New_User()
    rescue =>e
       $logger.log_results("User- User created", "User created","The page should redirect to UserCreation-Conformation page","Failed")     
  end  
  
  #method to check when username contains spaces
  def check_for_spaces_in_username(user_name)
      b=(user_name =~ /\s+/)
  if (b!=nil) 
      begin
         assert($ie.contains_text($username_with_space))      
         $logger.log_results(" Error message appears when user_name contains space", user_name.to_s, $username_with_space, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
         $logger.log_results(" Error message did not appear when user_name contains space", user_name.to_s,$username_with_space,"Failed")
      end
  else
    begin
       assert(!$ie.contains_text($username_with_space))
       $logger.log_results("Error message does not appear when user_name does not contain space",user_name.to_s,"No error message","passed")
    rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Error message appears when user_name does not contain space",short_name.to_s,"No error message","failed")
    end 
  end  
  
  end  
 
  def view_details(address)
    #link_check("View user details now")
    $ie.goto(address)
  end
  
  # check the user created in view system users page and go to the details page
  def check_view_User(office_name,first_name,last_name)
    begin
    $ie.link(:text,"Admin").click
    $ie.link(:text,"View system users").click

    $ie.text_field(:index, 2).set("%")
    $ie.button(:index,2).click
    $ie.wait
    number_of_personnel=count_items("select count(PERSONNEL_ID) from PERSONNEL")
    number_of_pages=number_of_personnel.to_i/10
    number_of_pages=number_of_pages.ceil
    dbquery("select GLOBAL_PERSONNEL_NUM from personnel where PERSONNEL_ID=(select max(PERSONNEL_ID) from PERSONNEL)")
    global_personnel_num=dbresult[0].to_s
    #puts "global personnel num"+global_personnel_num.to_s
    exist_on_page=0
    count=0
    while (number_of_pages >0 and exist_on_page==0) 
      count+=1
    if ($ie.link(:url,$test_site+"/PersonnelAction.do?globalPersonnelNum="+global_personnel_num.to_s+"&method=get").exists?())
          $logger.log_results("User- "+first_name.to_s+" "+last_name.to_s+" appears on page "+count.to_s,"NA","user should exist on page","Passed")
          exist_on_page=1
    else
          $ie.link(:text,"Next").click
          exist_on_page=0
    end 
      number_of_pages-=1
    end #while end      
    if exist_on_page==0
        $logger.log_results("User name does not exist on page","NA","should exist on page","failed")
    end
    end
    view_details($test_site+"/PersonnelAction.do?globalPersonnelNum="+global_personnel_num.to_s+"&method=get")
  end 
  
  # Check for the status change from active to inactive and then inactive to active
  def check_status(user_name)
    edit_User_status(user_name, "Inactive")
    edit_User_status(user_name, "Active")  
    $ie.link(:text,"Admin").click
  end
    
   # Change the User status
  def  edit_User_status(user_name, status)    
    begin    
      $ie.link(:text,"Edit user information").click
      assert($ie.contains_text(user_name+ " - Edit user information"))
       $logger.log_results("User- Edit User information", "click on Edit User information","Edit page should be opened","Passed")
       $ie.select_list(:name,"personnelStatus").select(status)
       $ie.button(:value,"Preview").click

       assert_on_page("Status: " + status.to_s)
       $ie.button(:value,"Submit").click              
       verify_status_change(user_name, status)    
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("User- Edit User information", "click on Edit User information","Edit page should be opened","Failed")
      end      
  end 
 
    # verifies the changed status   
    def verify_status_change(user_name, status)    
     assert($ie.contains_text(status)) and assert($ie.contains_text(user_name))
       $logger.log_results("User- Edit User information", "status change","Preview page with changed status","Passed")       
     rescue =>e
       $logger.log_results("User- Edit User information", "status change","Preview page with changed status","Failed")
    end 
   
   def count_office(query)
    dbquery(query)
    count=dbresult[0]
    return count
   end
   

 # To log out from the application 
   def User_logout()
    begin
      mifos_logout()
    end
    end  
  

 end  