# This class call all the testcases from the ProductCategory_Test_Cases class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/Funds/Funds_Test_Cases'
require 'modules/common/TestClass'

class Funds_Main
  funds_obj = Funds_Test_Cases.new
  funds_obj.mifos_login
  funds_obj.Click_Admin_page
  funds_obj.Check_Funds_Links
  funds_obj.Check_New_Funds_cancel
    
  filename = File.expand_path(File.dirname($PROGRAM_NAME))+"/data/Funds_Data.xls"  

#This section deals with the all validation message and error handling

  funds_obj.open(filename, 2)
  count = 0
  rowid = -1
    
  while(rowid < $maxrow * $maxcol - 1)
    funds_obj.read_fund_values(rowid)
    funds_obj.Click_Admin_page
    funds_obj.check_validation_error(funds_obj.Fund_name,funds_obj.Gl_code)
    rowid+=$maxcol
  end
  funds_obj.open(filename, 1)  
   rowid = -1
  while(rowid < $maxrow * $maxcol - 1)
    funds_obj.read_fund_values(rowid)
    funds_obj.Click_Admin_page
    $ie.link(:text, $new_funds_link).click
    funds_obj.Check_New_Funds
    funds_obj.Create_New_Funds(funds_obj.Fund_name,funds_obj.Gl_code)
    $ie.link(:text, $new_funds_link).click
    funds_obj.check_editdata_onpreview(funds_obj.Fund_name,funds_obj.Gl_code)
    funds_obj.check_view_Funds(funds_obj.Fund_name,funds_obj.Gl_code)
   
    #funds_obj.create_Funds_duplicate_name(fund_name, glcode)
    #end
    funds_obj.edit_Funds_name(funds_obj.Fund_name,funds_obj.Fund_name + "123",funds_obj.Gl_code)
  
    rowid+=$maxcol
  end 
funds_obj.mifos_logout()
end
