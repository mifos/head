# This class call all the testcases from the User_Test_Cases class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/User/User_Test_Cases'
require 'modules/common/TestClass'
require 'modules/Office/Office_Test_cases'
class User 
  user_obj = User_Test_Cases.new  
  office_obj=Office_Test_Cases.new
  user_obj.mifos_login
  user_obj.Click_Admin_page
  office_obj.Check_Office_Links
  
  user_obj.Check_New_User_cancel
  filename = File.expand_path(File.dirname(File.dirname($PROGRAM_NAME)))+"/Office/data/Office_Data.xls"
  
  office_obj.open(filename,1) 
  
  rowid=-1
 
 #values for creating office with mandatory values
  office_obj.read_office_values(rowid)
  count=user_obj.count_office("select count(DISPLAY_NAME) from OFFICE where DISPLAY_NAME='"+office_obj.Office_name+"'")
  
  if (count.to_i ==0)
  
  #call method in office to create office before creating user only if the office does not exist
  office_obj.Check_New_Office()
  office_obj.Create_New_Office_with_man_val(office_obj.Office_name, office_obj.Office_short_name,office_obj.Office_type, office_obj.Office_parent, office_obj.Members_per_grp, office_obj.Members_per_ken)
  user_obj.Check_New_User
  else
  user_obj.Check_New_User
  end
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/User_Data.xls"
  #input values for user start
  user_obj.open(filename,2) 
  
  rowid=-1
  while(rowid < $maxrow * $maxcol - 1)
    user_obj.read_user_values(rowid)
    user_obj.check_validation_error(office_obj.Office_name,user_obj.Firstname,user_obj.Lastname,user_obj.Birthdate,user_obj.Gender, user_obj.Hierarchy, user_obj.Username, user_obj.Password)
    user_obj.Check_New_User()
    rowid+=$maxcol
  end
  
  user_obj.open(filename,1)
  rowid=-1 
  while(rowid < $maxrow * $maxcol - 1)
    user_obj.read_user_values(rowid)
    
    user_obj.Create_New_User(office_obj.Office_name, user_obj.Firstname,user_obj.Middlename,user_obj.Second_last_name,user_obj.Lastname,user_obj.Govtid, user_obj.Email,
                             user_obj.Birthdate,user_obj.Marital_status,user_obj.Gender,user_obj.Language_pref,user_obj.Mfidate,user_obj.Address1 ,user_obj.Address2,
                             user_obj.Address3,user_obj.City, user_obj.State, user_obj.Country, user_obj.Postalcode, user_obj.Tel,user_obj.Usertitle, user_obj.Hierarchy,
                             user_obj.Role, user_obj.Username, user_obj.Password, user_obj.External_id)
    
    user_obj.check_view_User(office_obj.Office_name,user_obj.Firstname,user_obj.Lastname)
            
    user_obj.check_status(user_obj.Firstname+" "+user_obj.Lastname)
    
    user_obj.Check_New_User
    
    rowid=rowid+($maxcol)
  end
  user_obj.User_logout

 end
