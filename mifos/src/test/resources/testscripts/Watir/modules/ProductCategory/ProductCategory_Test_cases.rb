# This file contains all the automation test case scenario for testing the saving product module

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



class ProductCategory_Test_Cases < TestClass
  
  def read_product_values(rowid)
    @product_type = arrval[rowid+=1].to_s
    @category_name = arrval[rowid+=1].to_s
    @description = arrval[rowid+=1].to_s
    @description = check_value(@description)
  end
  
  def Product_type
    @product_type
  end
  
  def Category_name
    @category_name
  end
  
  def Description
    @description
  end
  
  #read properties file into a hash and then use the hash for labels,buttons etc
  def read_properties()
      @admin_properties=load_properties("modules/propertis/adminUIResources.properties")
      @prdcat_properties=load_properties("modules/propertis/ProductDefinitionResources.properties")
  end
  
  #read values from the hash into variables
  def read_hash_values()
      @admin_link=@prdcat_properties['product.admin']
      @view_category_link=@admin_properties['admin.viewprdcat']
      @def_new_category_link=@admin_properties['admin.defnewcat']
      @prdcat_def_text=@prdcat_properties['product.define']
      @prdcat_info_text=@prdcat_properties['product.categoryinfo']
      @prdcat_review_text=@prdcat_properties['product.review']
      @prdcat_type_msg=string_replace_message(@prdcat_properties['errors.select'],"{0}",@prdcat_properties['product.producttype'])
      @prdcat_name_msg=string_replace_message(@prdcat_properties['errors.mandatory'],"{0}",@prdcat_properties['product.categoryname'])
      @prdcat_max_len_msg=string_replace_message(@prdcat_properties['errors.maximumlength'],"{0}",@prdcat_properties['product.categoryname'])
      @prdcat_max_len_msg=string_replace_message(@prdcat_max_len_msg,"{1}","100")
      @prdcat_desc_max_len=string_replace_message(@prdcat_properties['errors.maximumlength'],"{0}",@prdcat_properties['product.categorydesc'])
      @prdcat_desc_max_len=string_replace_message(@prdcat_desc_max_len,"{1}","500")
      @prdcat_dupl_name_msg=@prdcat_properties['errors.duplcategoryname']
      @prdcat_success_msg=@prdcat_properties['product.addsuccessful']
      @prdcat_def_new_msg=@prdcat_properties['product.defprdcat']
      @prdcat_view_details_msg=@prdcat_properties['product.viewdet']
      #button labels
      @cancel_button=@prdcat_properties['product.cancel']
      @preview_button=@prdcat_properties['product.preview']
      @submit_button=@prdcat_properties['product.butsubmit']
      @edit_category_button=@prdcat_properties['product.buteditcat']
      @review_submit=string_replace_message(@prdcat_properties['product.review'],"amp;","")      #removing amp; from the string to get "review & submit" as properties file has it as "review &amp; submit" 
  end
  
  #read labels from db
  def get_labels_from_db()
    begin
      dbquery("select lookup_value from lookup_value_locale where lookup_id=113")
      @inactive_label=dbresult[0]
      dbquery("select lookup_value from lookup_value_locale where lookup_id=114")
      @active_label=dbresult[0]  
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check savingproduct link on admin page after login
  def Check_ProductCategory_Links()
    begin    
      assert($ie.contains_text(@view_category_link)) and assert($ie.contains_text(@def_new_category_link))
      $logger.log_results("ProductCategory- Check for productcategory links on admin page", "Click on admin link","The links should be there","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Check for productcategory links on admin page", "Click on admin link","The links should be there","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check cancel in 'Define new productcategory' link on admin page
  def Check_New_ProductCategory_cancel()
    begin
      $ie.link(:text,@def_new_category_link).click
      $ie.button(:value,@cancel_button).click     
      verify_admin_page()      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check new ProductCategory link on admin page
  def Check_New_ProductCategory()
      $ie.link(:text, @def_new_category_link).click
    begin      
      assert($ie.contains_text(@prdcat_def_text+" - "+@prdcat_info_text))
      $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Failed")
    rescue =>excp
      quit_on_error(excp)  
    end      
  end
  
  # Check for all mandatory validation error
  def check_validation_error(product_type, category_name,description)
    begin     
      $ie.link(:text,@def_new_category_link).click      
      Man_New_ProductCategory()      
      Man_New_ProductCategory_with_ProductType(product_type)
      ProductCategory_with_desc(description)
      Man_New_ProductCategory_with_CategoryName(category_name)
      
      if $ie.contains_text(@prdcat_def_text+" - "+@review_submit)
          create_ProductCategory_duplicate_name(category_name)
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  # Check for validation messages while creating new ProductCategory
  def Man_New_ProductCategory()
    begin  
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(@prdcat_def_text+" - "+@prdcat_info_text)) and \
      assert($ie.contains_text(@prdcat_type_msg)) and \
      assert($ie.contains_text(@prdcat_name_msg))      
      $logger.log_results("ProductCategory- Check for all error message when only preview button is clicked ", "NA","NA","Passed")     
      
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Check for all error message when only preview button is clicked ", "NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)  
    end    
  end
  
  # Check for validation messages while creating new ProductCategory with product_type
  def  Man_New_ProductCategory_with_ProductType(product_type)
    begin
      set_value_selectlist("productType", product_type)
      $ie.button(:value,@preview_button).click
      assert(!$ie.contains_text(@prdcat_type_msg)) and \
      assert($ie.contains_text(@prdcat_def_text+" - "+@prdcat_info_text)) and \
      assert($ie.contains_text(@prdcat_name_msg))  
      $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type ","All validation error message for other mandatory fields","Passed")     
     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type ","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)  
    end    
  end
  
  # Check for validation messages while creating new ProductCategory with product_type, category_name
  def     Man_New_ProductCategory_with_CategoryName(category_name)
    begin
      #set_value_selectlist("productType", product_type)
      set_value_txtfield("productCategoryName", category_name)
      $ie.button(:value,@preview_button).click
      max_field_len(category_name,100,@prdcat_max_len_msg)
      assert(!$ie.contains_text(@prdcat_name_msg))
      #assert($ie.contains_text($review_productcategory_info))
      $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type, category_name", "NA","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type, category_name", "NA","Failed")     
    rescue =>excp
      quit_on_error(excp)  
    end    
  end
  
  #enter values in description field and check for error message when string length greater than 500 is given as input
  def ProductCategory_with_desc(description)
    begin
      set_value_txtfield("productCategoryDesc",description)
      $ie.button(:value,@preview_button).click
      max_field_len(description,500,@prdcat_desc_max_len)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check for all mandatory validation error
  def check_field_validation_error(category_name)
    begin
      Click_Admin_page()
      $ie.link(:text,@def_new_category_link).click           
      Check_New_ProductCategory()      
      verify_CategoryName(category_name)      
      $ie.button(:value,@cancel_button).click 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check for field validation messages while creating new ProductCategory with category_name
  def verify_CategoryName(category_name)
    begin
      set_value_txtfield("productCategoryName", category_name)   
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(@prdcat_max_len_msg))      
      $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "category_name", "validation error message", "Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "category_name","validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  
  # checks for the unique ProductCategory name functionality
  def create_ProductCategory_duplicate_name(category_name)
    number_of_prd_categories=count_items("select count(PRD_CATEGORY_NAME) from PRD_CATEGORY where PRD_CATEGORY_NAME='"+category_name+"'")
    if (number_of_prd_categories.to_i > 0) 
      $ie.button(:value,@submit_button).click
      begin 
        assert($ie.contains_text(@prdcat_dupl_name_msg))
        $logger.log_results("Error message appears for duplicate product category name",category_name.to_s,@prdcat_dupl_name_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not  appear for duplicate product category name",category_name.to_s,@prdcat_dupl_name_msg,"failed")
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
  
  # Creates a new ProductCategory and verify the preview page and database values after submission 
  def Create_New_ProductCategory(product_type, category_name, description)
    begin
      set_ProductCategory_data(product_type, category_name, description)
      $ie.button(:value,@preview_button).click
      validate_ProductCategory_preview_page(product_type, category_name, description)             	
      $ie.button(:value,@submit_button).click
      validate_ProductCategory_creation(product_type, category_name, description)
      ProductCategory_Creation_Conformation()
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check the edit data link on the preview page
  def check_editdata_onpreview(product_type, category_name, description)
    
    set_ProductCategory_data(product_type, category_name, description)
    $ie.button(:value,@preview_button).click  
    
    validate_ProductCategory_preview_page(product_type, category_name, description)
    $ie.button(:value, @edit_category_button).click
    
    assert($ie.contains_text(@prdcat_def_text+" - "+@prdcat_info_text))
    $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Passed")
    $ie.button(:value,@preview_button).click
    validate_ProductCategory_preview_page(product_type, category_name, description)
    $ie.button(:value,@cancel_button).click
    verify_admin_page()         
  rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on admin 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Failed")    
  rescue =>excp
    quit_on_error(excp)    
  end
  
  # Check the edit data link on the preview page
  def edit_ProductCategory_name(old_category_name, new_category_name, product_type, description)
    begin
      Click_Admin_page()
      $ie.link(:text, @view_category_link).click
      $ie.link(:text, old_category_name).click    
      $ie.wait     
      $ie.link(:text, @edit_category_button).click
      assert($ie.contains_text(old_category_name.to_s + " - "+@edit_category_button))
      $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "click on edit ProductCategory link ", "page to edit the ProductCategory", "Passed")
      set_value_txtfield("productCategoryName", new_category_name)
      $ie.button(:value,@preview_button).click
      
      if( $ie.contains_text(@prdcat_properties['product.categoryname']+": " + new_category_name.to_s) and $ie.contains_text(description.to_s) )
        
        $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "validating the preview page after change", "valid preview page after change", "Passed")
      else
        
        $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "validating the preview page after change", "valid preview page after change", "Failed")
      end 
      
      $ie.button(:value,@submit_button).click
      validate_productcategory_view(product_type, new_category_name, description)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "click on edit ProductCategory link ","page to edit the ProductCategory", "Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  # Enter the new ProductCategory data 
  def set_ProductCategory_data(product_type, category_name, description)
    begin
      if($ie.contains_text(@prdcat_def_text+" - "+@prdcat_info_text))
        $logger.log_results("ProductCategory- Create new ProductCategory page", "Click on 'Define a new ProductCategory' link","Access to the new ProductCategory page","Passed")
      else
        $logger.log_results("ProductCategory- Create new ProductCategory page", "Click on admin 'Define a new ProductCategory' link","Access to the new ProductCategory page","Failed")
      end
      
      set_value_selectlist("productType", product_type)
      set_value_txtfield("productCategoryName", category_name)
      set_value_txtfield("productCategoryDesc", description)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  
  # Validate the preview for new check list  
  def  validate_ProductCategory_preview_page(product_type, category_name, description)
    
    if $ie.contains_text(@prdcat_def_text+" - "+@review_submit) then
      $logger.log_results("New ProductCategory- Preview", "new ProductCategory inputs", "valid preview page","Passed")
    else 
      $logger.log_results("New ProductCategory- Preview", "new ProductCategory inputs" , "valid preview page","Failed")
    end
    

    assert_on_page(@prdcat_properties['product.producttype']+": " + product_type.to_s)
    assert_on_page(@prdcat_properties['product.categoryname']+": " + category_name.to_s)
    
    begin
      $ie.contains_text( description.to_s)
      $logger.log_results("description "+description.to_s+" appears on page", description.to_s,"NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("description "+description.to_s+" does not appear on page", description.to_s,"NA","passed")
    rescue =>excp
      quit_on_error(excp)   
    end
    
  end  
  
  # Verify the creation of new ProductCategory from the data base  
  def   validate_ProductCategory_creation(product_type, category_name, description)        
    begin
      
      # Fetch the data from database and store the values in the corresponding variables
      dbquery("select PRD_CATEGORY_ID, LOOKUP_VALUE 'Product type', PRD_CATEGORY_NAME, date_format(CREATED_DATE,'%d/%m/%Y'), STATE, DESCRIPTION from prd_category pc, lookup_value_locale lvl, prd_type pt where pc.PRD_CATEGORY_ID = (select  max(PRD_CATEGORY_ID) from prd_category) and pc.PRD_TYPE_ID = pt.PRD_TYPE_ID and pt.PRD_TYPE_LOOKUP_ID = lvl.LOOKUP_ID ")
      
      prd_category_id = dbresult[0]
      db_product_type = dbresult[1]
      db_category_name = dbresult[2]
      db_created_date = dbresult[3]
      
      db_state_id = dbresult[4]
      db_description = dbresult[5]
      
      
      dbquery("select date_format(sysdate(),'%d/%m/%Y')")
      @@current_date = dbresult[0]

      dbcheck("Product type",product_type.to_s,db_product_type.to_s) 
      dbcheck("Category Name",category_name.to_s,db_category_name.to_s) 
      dbcheck("Description",description.to_s,db_description.to_s) 
      dbcheck("Status","1",db_state_id.to_s) 
    rescue =>excp
      quit_on_error(excp)
    end 
  end  
  
  #Check for the ProductCategoryCreation-Conformation page
  def ProductCategory_Creation_Conformation()
    begin
      assert($ie.contains_text(@prdcat_success_msg)) and assert($ie.contains_text(@prdcat_def_new_msg))\
      and assert($ie.contains_text(@prdcat_view_details_msg))
      $logger.log_results("ProductCategory- ProductCategory created", "ProductCategory created","The page should redirect to ProductCategoryCreation-Conformation page","Passed")
      $ie.link(:text, @prdcat_def_new_msg).click
    #Check_New_ProductCategory()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- ProductCategory created", "ProductCategory created","The page should redirect to ProductCategoryCreation-Conformation page","Failed")     
    rescue =>excp
      quit_on_error(excp)    
    end
  end      
  
  # Mandatory check for view check list
  def check_view_productcategory(product_type, category_name, description)
    begin
      Click_Admin_page()
      $ie.link(:text, @view_category_link).click
      $ie.link(:text, category_name).click    
      $ie.wait 
      validate_productcategory_view(product_type, category_name, description)    
    rescue =>excp
      quit_on_error(excp)
    end 
  end 
  
  def validate_productcategory_view(product_type, category_name, description)
    begin
      assert($ie.contains_text(category_name.to_s))\
      and assert($ie.contains_text(@prdcat_properties['product.producttype']+": " + product_type.to_s))\
      and assert($ie.contains_text(description.to_s))
      $logger.log_results("ProductCategory- validating the preview page for new ProductCategory", "Click view ProductCategory link", "Valid preview page content", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- validating the preview page for new ProductCategory", "Click view ProductCategory link","Valid preview page content", "Failed")
    rescue =>excp
      quit_on_error(excp) 
    end
  end
  
  # Check for the status change from active to inactive and then inactive to active
  def check_status(category_name)
    begin
      edit_productcategory_status(category_name, @inactive_label)
      edit_productcategory_status(category_name, @active_label)  
      $ie.link(:text,@admin_link).click
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Change the ProductCategory status
  def  edit_productcategory_status(category_name, status)
    begin    
      
      $ie.link(:text, @edit_category_button).click
      assert($ie.contains_text(category_name.to_s + " - "+@edit_category_button))
      $logger.log_results("ProductCategory- Edit Product category information ", "click on Edit Product category information ","Edit page should be opened","Passed")
      $ie.select_list(:name,"productCategoryStatus").select(status)
      $ie.button(:value,@preview_button).click             
      if($ie.contains_text(@prdcat_properties['product.status']+": " + status.to_s))
        $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Passed")        
      else
        $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Failed")
      end
      $ie.button(:value,@submit_button).click              
      verify_status_change(category_name, status)    
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Edit ProductCategory information", "click on Edit ProductCategory information","Edit page should be opened","Failed")
    rescue =>excp
      quit_on_error(excp)      
    end      
  end 
  
  # verifies the changed status   
  def verify_status_change(category_name, status)
    begin    
      assert($ie.contains_text(status)) and assert($ie.contains_text(category_name.to_s))
      $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Passed")       
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Failed")
    rescue =>excp
      quit_on_error(excp)      
    end
  end 
  
  
end  



class ProductCategory_Main
    prdcat_obj = ProductCategory_Test_Cases.new
    prdcat_obj.read_properties()
    prdcat_obj.read_hash_values()
    prdcat_obj.mifos_login
    prdcat_obj.get_labels_from_db
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
