# This file contains all the automation test case scenario for testing the saving product module

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
  

  # Check savingproduct link on admin page after login
  def Check_ProductCategory_Links()
    begin    
      assert($ie.contains_text($view_productcategory_link)) and assert($ie.contains_text($new_productcategory_link))
        $logger.log_results("ProductCategory- Check for productcategory links on admin page", "Click on admin link","The links should be there","Passed")
      rescue =>e
        $logger.log_results("ProductCategory- Check for productcategory links on admin page", "Click on admin link","The links should be there","Failed")
    end
  end
   
  # Check cancel in 'Define new productcategory' link on admin page
  def Check_New_ProductCategory_cancel()
    begin
      $ie.link(:text, $new_productcategory_link).click
      $ie.button(:value,"Cancel").click     
      verify_admin_page()      
    end
  end
 
  # Check new ProductCategory link on admin page
  def Check_New_ProductCategory()
    begin      
      assert($ie.contains_text($new_productcategory_page_msg))
        $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Passed")
      rescue =>e
        $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Failed")
    end      
  end
    
  # Check for all mandatory validation error
  def check_validation_error(product_type, category_name,description)
      begin     
      $ie.link(:text, $new_productcategory_link).click      
      Man_New_ProductCategory()      
      Man_New_ProductCategory_with_ProductType(product_type)
      ProductCategory_with_desc(description)
      Man_New_ProductCategory_with_CategoryName(category_name)
      
      if $ie.contains_text($review_productcategory_info)
         
         create_ProductCategory_duplicate_name(category_name)
      end
      
      end
  end 
  
  # Check for validation messages while creating new ProductCategory
  def Man_New_ProductCategory()
    begin  
      $ie.button(:value,"Preview").click
      assert($ie.contains_text($new_productcategory_page_msg))\
      and assert($ie.contains_text($productcategory_product_type_msg))\
      and assert($ie.contains_text($productcategory_category_name_msg))      
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "nothing ","All validation error message","Passed")     
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "nothing ","All validation error message","Failed")       
     end    
  end
  
    # Check for validation messages while creating new ProductCategory with product_type
    def  Man_New_ProductCategory_with_ProductType(product_type)
    begin
      set_value_selectlist("productType.productTypeID", product_type)
      $ie.button(:value,"Preview").click
      assert(!$ie.contains_text($productcategory_product_type_msg)) and assert($ie.contains_text($new_productcategory_page_msg))\
      and assert($ie.contains_text($productcategory_category_name_msg))  
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type ","All validation error message for other mandatory fields","Passed")     
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type ","All validation error message for other mandatory fields","Failed")     
     end    
  end

    # Check for validation messages while creating new ProductCategory with product_type, category_name
    def     Man_New_ProductCategory_with_CategoryName(category_name)
    begin
      #set_value_selectlist("productType.productTypeID", product_type)
      set_value_txtfield("productCategoryName", category_name)
      $ie.button(:value,"Preview").click
      max_field_len(category_name,100,$productcategory_valid_category_name_msg)
      assert(!$ie.contains_text($productcategory_category_name_msg))
       #assert($ie.contains_text($review_productcategory_info))
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type, category_name", "NA","Passed")     
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "product_type, category_name", "NA","Failed")     
     end    
   end
   
   def ProductCategory_with_desc(description)
    set_value_txtfield("productCategoryDesc",description)
     $ie.button(:value,"Preview").click
    max_field_len(description,500,$productcategory_valid_description_name_msg)
   end
   
  # Check for all mandatory validation error
  def check_field_validation_error(category_name)
    begin
      Click_Admin_page()
      $ie.link(:text, $new_productcategory_link).click       
      Check_New_ProductCategory()      
      verify_CategoryName(category_name)      
      $ie.button(:value, "Cancel").click 
    end
  end
  
  # Check for field validation messages while creating new ProductCategory with category_name
  def verify_CategoryName(category_name)
    begin
      set_value_txtfield("productCategoryName", category_name)   
      $ie.button(:value,"Preview").click
                  
      assert($ie.contains_text($productcategory_valid_category_name_msg))      
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "category_name", "validation error message", "Passed")
      rescue  Test::Unit::AssertionFailedError=>e
       $logger.log_results("ProductCategory- Check for validation error while creating new ProductCategory", "category_name","validation error message","Failed")
     end
  end
  
  # checks for the unique ProductCategory name functionality
  def create_ProductCategory_duplicate_name(category_name)
     #begin
      #Click_Admin_page()
      #$ie.link(:text, $new_productcategory_link).click
      #Check_New_ProductCategory()     
      #set_ProductCategory_data(product_type, category_name, description)
        
      #$ie.button(:value,"Preview").click
	  #validate_ProductCategory_preview_page(product_type, category_name, description)

  	  #$ie.button(:value,"Submit").click
  	  #assert($ie.contains_text($duplicate_categoryname_msg)) 
       # $logger.log_results("ProductCategory- Check for creating new ProductCategory with an existing ProductCategory name",
       #   "existing ProductCategory name","Name validation error message","Passed")
      #rescue =>e
       # $logger.log_results("ProductCategory- Check for creating new ProductCategory with an existing ProductCategory name",
       #   "existing ProductCategory name","Name validation error message","Failed")
     #end
     number_of_prd_categories=count_items("select count(PRD_CATEGORY_NAME) from PRD_CATEGORY where PRD_CATEGORY_NAME='"+category_name+"'")
    
     if (number_of_prd_categories.to_i > 0) 
      $ie.button(:value,"Submit").click
      begin 
        assert($ie.contains_text($duplicate_categoryname_msg))
        $logger.log_results("Error message appears for duplicate product category name",category_name.to_s,$duplicate_categoryname_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not  appear for duplicate product category name",category_name.to_s,$duplicate_categoryname_msg,"failed")
      end
     else
       $ie.button(:value,"Submit").click 
     end
   end
  
  # Creates a new ProductCategory and verify the preview page and database values after submission 
  def Create_New_ProductCategory(product_type, category_name, description)
   
    begin
    puts "Function: Create_New_ProductCategory"
    set_ProductCategory_data(product_type, category_name, description)
     $ie.button(:value, "Preview").click
             
	 validate_ProductCategory_preview_page(product_type, category_name, description)             	
	$ie.button(:value,"Submit").click
	
    validate_ProductCategory_creation(product_type, category_name, description)
    ProductCategory_Creation_Conformation()
    end
  end
    
  # Check the edit data link on the preview page
  def check_editdata_onpreview(product_type, category_name, description)
      
     set_ProductCategory_data(product_type, category_name, description)
     $ie.button(:value,"Preview").click  
    
	 validate_ProductCategory_preview_page(product_type, category_name, description)
     $ie.button(:value, $edit_productcategory_link).click
        		
    assert($ie.contains_text($new_productcategory_page_msg))
        $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Passed")
        $ie.button(:value,"Preview").click
        validate_ProductCategory_preview_page(product_type, category_name, description)
        $ie.button(:value,"Cancel").click
        verify_admin_page()         
    rescue =>e
        $logger.log_results("ProductCategory- Check for Define a new ProductCategory link on admin page", "Click on admin 'Define a new ProductCategory' link","Access to the Define a new ProductCategory page","Failed")    
  end
  
  # Check the edit data link on the preview page
  def edit_ProductCategory_name(old_category_name, new_category_name, product_type, description)
  
    Click_Admin_page()
    $ie.link(:text, $view_productcategory_link).click
    $ie.link(:text, old_category_name).click    
    $ie.wait     
    $ie.link(:text, $edit_productcategory_link).click
    assert($ie.contains_text(old_category_name.to_s + " - Edit category information"))
        $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "click on edit ProductCategory link ", "page to edit the ProductCategory", "Passed")
        set_value_txtfield("productCategoryName", new_category_name)
        $ie.button(:value, "Preview").click

        if( $ie.contains_text("Category Name: " + new_category_name.to_s) and $ie.contains_text(description.to_s) )

             $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "validating the preview page after change", "valid preview page after change", "Passed")
          else

             $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "validating the preview page after change", "valid preview page after change", "Failed")
          end 

        $ie.button(:value, "Submit").click
        validate_productcategory_view(product_type, new_category_name, description)
    rescue =>e
        $logger.log_results("ProductCategory- Check for edit the ProductCategory with change the name", "click on edit ProductCategory link ","page to edit the ProductCategory", "Failed")
   
  end
  
  # Enter the new ProductCategory data 
  def set_ProductCategory_data(product_type, category_name, description)
    begin
         if($ie.contains_text($new_productcategory_page_msg))
            $logger.log_results("ProductCategory- Create new ProductCategory page", "Click on 'Define a new ProductCategory' link","Access to the new ProductCategory page","Passed")
         else
            $logger.log_results("ProductCategory- Create new ProductCategory page", "Click on admin 'Define a new ProductCategory' link","Access to the new ProductCategory page","Failed")
         end
      
      set_value_selectlist("productType.productTypeID", product_type)
      set_value_txtfield("productCategoryName", category_name)
      set_value_txtfield("productCategoryDesc", description)
   end
  end

  
  # Validate the preview for new check list  
  def  validate_ProductCategory_preview_page(product_type, category_name, description)
          
      if $ie.contains_text($review_productcategory_info) then
		  $logger.log_results("New ProductCategory- Preview", "new ProductCategory inputs", "valid preview page","Passed")
	  else 
		  $logger.log_results("New ProductCategory- Preview", "new ProductCategory inputs" , "valid preview page","Failed")
	  end
      
     # if ($ie.contains_text("Product Type: " + product_type.to_s)\
     #    and $ie.contains_text("Category Name: " + category_name.to_s)\
     #    and $ie.contains_text( description.to_s))
     #   $logger.log_results("ProductCategory- validating the preview page", "Click on preview button","Valid preview page content", "Passed")
     # else
     #   $logger.log_results("ProductCategory- validating the preview page", "Click on preview button","Valid preview page content", "Failed")
     # end
     assert_on_page("Product Type: " + product_type.to_s)
     assert_on_page("Category Name: " + category_name.to_s)
     #assert_on_page("Category Name: " + category_name.to_s)
     begin
     $ie.contains_text( description.to_s)
     $logger.log_results("description "+description.to_s+" appears on page", description.to_s,"NA","passed")
     rescue Test::Unit::AssertionFailedError=>e
     $logger.log_results("description "+description.to_s+" does not appear on page", description.to_s,"NA","passed")
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
	  
     # if(db_product_type.to_s ==  product_type.to_s\
     #   and db_category_name.to_s ==  category_name.to_s\
     #   and db_created_date.to_s ==  @@current_date.to_s\
     #   and db_description.to_s == description.to_s\
     #   and 1 == db_state_id.to_i)
     #   $logger.log_results("ProductCategory- validating the ProductCategory creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Passed")
     # else
     #   $logger.log_results("ProductCategory- validating the ProductCategory creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Failed")
     # end
     dbcheck("Product type",product_type.to_s,db_product_type.to_s) 
     dbcheck("Product type",category_name.to_s,db_category_name.to_s) 
     dbcheck("Product type",description.to_s,db_description.to_s) 
     dbcheck("Status","1",db_state_id.to_s) 
    end 
  end  
  
  #Check for the ProductCategoryCreation-Conformation page
  def ProductCategory_Creation_Conformation()
     assert($ie.contains_text($productcategory_created_msg)) and assert($ie.contains_text($create_productcategory_msg))\
      and assert($ie.contains_text($productcategory_view_detail_msg))
       $logger.log_results("ProductCategory- ProductCategory created", "ProductCategory created","The page should redirect to ProductCategoryCreation-Conformation page","Passed")
       $ie.link(:text, $create_productcategory_msg).click
        Check_New_ProductCategory()
    rescue =>e
       $logger.log_results("ProductCategory- ProductCategory created", "ProductCategory created","The page should redirect to ProductCategoryCreation-Conformation page","Failed")     
 end      
  
  # Mandatory check for view check list
  def check_view_productcategory(product_type, category_name, description)
    begin
      Click_Admin_page()
      $ie.link(:text, $view_productcategory_link).click
      $ie.link(:text, category_name).click    
      $ie.wait 
      validate_productcategory_view(product_type, category_name, description)    
   end 
  end 
  
  def validate_productcategory_view(product_type, category_name, description)
    begin
    assert($ie.contains_text(category_name.to_s))\
    and assert($ie.contains_text("Product Type: " + product_type.to_s))\
    and assert($ie.contains_text(description.to_s))
          $logger.log_results("ProductCategory- validating the preview page for new ProductCategory", "Click view ProductCategory link", "Valid preview page content", "Passed")
    rescue =>e
          $logger.log_results("ProductCategory- validating the preview page for new ProductCategory", "Click view ProductCategory link","Valid preview page content", "Failed")
    end
  end
  
  # Check for the status change from active to inactive and then inactive to active
  def check_status(category_name)
    edit_productcategory_status(category_name, "Inactive")
    edit_productcategory_status(category_name, "Active")  
    $ie.link(:text,"Admin").click
  end
    
   # Change the ProductCategory status
  def  edit_productcategory_status(category_name, status)
    begin    
    
      $ie.link(:text, $edit_productcategory_link).click
      assert($ie.contains_text(category_name.to_s + " - Edit category information"))
       $logger.log_results("ProductCategory- Edit Product category information ", "click on Edit Product category information ","Edit page should be opened","Passed")
       $ie.select_list(:name,"prdCategoryStatus.prdCategoryStatusId").select(status)
       $ie.button(:value,"Preview").click              
       if($ie.contains_text("Status: " + status.to_s))
        $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Passed")        
       else
        $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Failed")
       end
       $ie.button(:value,"Submit").click              
       verify_status_change(category_name, status)    
      rescue =>e
       $logger.log_results("ProductCategory- Edit ProductCategory information", "click on Edit ProductCategory information","Edit page should be opened","Failed")
      end      
  end 
 
    # verifies the changed status   
    def verify_status_change(category_name, status)    
     assert($ie.contains_text(status)) and assert($ie.contains_text(category_name.to_s))
       $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Passed")       
     rescue =>e
       $logger.log_results("ProductCategory- Edit ProductCategory information", "status change","Preview page with changed status","Failed")
    end 
   
  
 end  