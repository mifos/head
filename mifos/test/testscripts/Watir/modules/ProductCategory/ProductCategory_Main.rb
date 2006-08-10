# This class call all the testcases from the ProductCategory_Test_Cases class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/ProductCategory/ProductCategory_Test_cases'
require 'modules/common/TestClass'

class ProductCategory_Main
  prdcat_obj = ProductCategory_Test_Cases.new
  prdcat_obj.mifos_login
  prdcat_obj.Click_Admin_page
  prdcat_obj.Check_ProductCategory_Links
  prdcat_obj.Check_New_ProductCategory_cancel
    
  filename = File.expand_path( File.dirname($PROGRAM_NAME))+"/data/ProductCategory_Data.xls"
  prdcat_obj.open(filename, 2)
  count = 0
  rowid = -1
    
  while(rowid < $maxrow * $maxcol - 1)
      prdcat_obj.read_product_values(rowid)
      prdcat_obj.Click_Admin_page
  #checks mandatory error messages 
      prdcat_obj.check_validation_error(prdcat_obj.Product_type,prdcat_obj.Category_name,prdcat_obj.Description)
      rowid+=$maxcol
  end
  prdcat_obj.open(filename, 1)
  count = 0
  rowid = -1
    
  while(rowid < $maxrow * $maxcol - 1)
     prdcat_obj.read_product_values(rowid)
     prdcat_obj.Click_Admin_page
     $ie.link(:text, $new_productcategory_link).click
     prdcat_obj.Check_New_ProductCategory
     prdcat_obj.Create_New_ProductCategory(prdcat_obj.Product_type,prdcat_obj.Category_name,prdcat_obj.Description)
  
     prdcat_obj.check_editdata_onpreview(prdcat_obj.Product_type,prdcat_obj.Category_name,prdcat_obj.Description)
   
     prdcat_obj.check_view_productcategory(prdcat_obj.Product_type,prdcat_obj.Category_name,prdcat_obj.Description)
     prdcat_obj.check_status(prdcat_obj.Category_name)      

 
     prdcat_obj.edit_ProductCategory_name(prdcat_obj.Category_name,prdcat_obj.Category_name + "123",prdcat_obj.Product_type,prdcat_obj.Description)
     rowid+=$maxcol
  end
prdcat_obj.mifos_logout()
end
