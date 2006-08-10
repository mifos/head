# This class call all the testcases from the Office_Test_Cases class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/Office/Office_Test_cases'
require 'modules/common/TestClass'

class Office 
  office_obj = Office_Test_Cases.new  
  office_obj.Office_login
  office_obj.Click_Admin_page
  office_obj.Check_office_hierarchy
  office_obj.Check_Office_Links
  office_obj.Check_New_Office_cancel
  
  #filename = File.dirname($PROGRAM_NAME)+"/data/Office_Data.xls"
  filename=File.expand_path( File.dirname($PROGRAM_NAME))+"/data/Office_Data.xls"
  office_obj.open(filename,2) 
  count = 0
  rowid=-1
  
 while(rowid < $maxrow * $maxcol - 1)
    office_obj.read_office_values(rowid)
    office_obj.check_validation_error(office_obj.Office_name,office_obj.Office_short_name,office_obj.Office_type,office_obj.Office_parent,office_obj.Members_per_grp,office_obj.Members_per_ken)
    office_obj.link_check("Admin")
    rowid+=$maxcol
 end   
 
 rowid=-1
 office_obj.open(filename,1) 
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
 
 # end  
 # office_obj.Check_New_Office
 # office_obj.Create_New_Office(office_name, office_shortname,office_type, office_parent, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
 # office_obj.check_editdata_onpreview(office_name, office_shortname,office_type, office_parent, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)
 # office_obj.check_view_office(office_name, office_shortname,office_type, office_parent)
 # office_obj.check_status(office_name)
 # if count == 0 
 #   office_obj.create_office_duplicate_name(office_name, office_shortname,office_type, office_parent, address1 , address2, address3,city, state, country, postal_code, telephone, external_id, members_per_group, members_per_kendra, distance_HO_to_BO)  
 # end
 # count = 1  

#end
office_obj.mifos_logout()
 
end
