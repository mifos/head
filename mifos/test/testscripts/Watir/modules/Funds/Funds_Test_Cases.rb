# This file contains all the automation test case scenario for testing the funds module
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

class Funds_Test_Cases < TestClass

  def read_fund_values(rowid)
    @fund_name = arrval[rowid+=1].to_s
    @glcode = arrval[rowid+=1].to_i.to_s
  end
  
  def Fund_name
    @fund_name
  end
  
  def Gl_code
    @glcode
  end
  # Check funds link on admin page after login
  def Check_Funds_Links()
    begin    
      assert($ie.contains_text($view_funds_link)) and assert($ie.contains_text($new_funds_link))
        $logger.log_results("Funds- Check for Funds links on admin page", "Click on admin link","The links should be there","Passed")
      rescue =>e
        $logger.log_results("Funds- Check for Funds links on admin page", "Click on admin link","The links should be there","Failed")
    end
  end
   
  # Check cancel in 'Define new Funds' link on admin page
  def Check_New_Funds_cancel()
    begin
      $ie.link(:text, $new_funds_link).click
      $ie.button(:value, "Cancel").click     
      verify_admin_page()      
    end
  end
 
  # Check new Funds link on admin page
  def Check_New_Funds()
    begin      
      assert($ie.contains_text($new_funds_page_msg))
        $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on 'Define a new Funds' link","Access to the Define a new Funds page", "Passed")
      rescue =>e
        $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on 'Define a new Funds' link","Access to the Define a new Funds page", "Failed")
    end      
  end
    
  # Check for all mandatory validation error
  def check_validation_error(fund_name, glcode)
      begin     
      $ie.link(:text, $new_funds_link).click      
      Man_New_Funds()      
      Man_New_Funds_with_Name(fund_name)
      Man_New_Funds_with_GLCode(glcode)
      if $ie.contains_text($review_funds_info)
        
        create_Funds_duplicate_name(fund_name)
      end
    end    
  end 
  
  # Check for validation messages while creating new Funds
  def Man_New_Funds()
    begin  
      $ie.button(:value,"Preview").click
      assert($ie.contains_text($new_funds_page_msg))\
      and assert($ie.contains_text($funds_FundName_msg))\
      and assert($ie.contains_text($funds_GLCode_msg))      
       $logger.log_results("Funds- Check for validation error while creating new Funds", "nothing ", "All validation error message", "Passed")     
      rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Funds- Check for validation error while creating new Funds", "nothing ", "All validation error message", "Failed")       
     end    
  end
  
    # Check for validation messages while creating new Funds with fund_name
    def  Man_New_Funds_with_Name(fund_name)
    begin
      set_value_txtfield("fundName", fund_name)
      $ie.button(:value,"Preview").click
      assert(!$ie.contains_text($funds_FundName_msg)) and assert($ie.contains_text($new_funds_page_msg))\
      and assert($ie.contains_text($funds_GLCode_msg))  
       $logger.log_results("Funds- Check for validation error while creating new Funds", "fund_name","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("Funds- Check for validation error while creating new Funds", "fund_name","All validation error message for other mandatory fields","Failed")     
     end    
  end

    # Check for validation messages while creating new Funds with product_type, category_name
    def Man_New_Funds_with_GLCode(glcode)    
    begin
      #set_value_txtfield("fundName", fund_name)
      set_value_selectlist("glCode.glCodeId", glcode)
      $ie.button(:value,"Preview").click
      assert(!$ie.contains_text($funds_GLCode_msg))
     #assert($ie.contains_text($review_funds_info))
       $logger.log_results("Funds- No error message appears when Glcode is entered", "glcode", "NA","Passed")     
     rescue Test::Unit::AssertionFailedError=>e
       $logger.log_results("Funds- error message appears when Glcode is entered", "glcode", "NA","failed")     
     end    
   end
   
  # Check for all mandatory validation error
  def check_field_validation_error(fund_name)
    begin
      Click_Admin_page()
      $ie.link(:text, $new_funds_link).click       
      Check_New_Funds()      
      verify_FundName(fund_name)
      $ie.button(:value, "Cancel").click 
    end
  end
  
  # Check for field validation messages while creating new Funds with category_name
  def verify_FundName(fund_name)
    begin
      set_value_txtfield("fundName", fund_name)
      $ie.button(:value,"Preview").click
                  
      assert($ie.contains_text("to be add"))      
       $logger.log_results("Funds- Check for validation error while creating new Funds", "fund_name", "validation error message", "Passed")
      rescue=>e
       $logger.log_results("Funds- Check for validation error while creating new Funds", "fund_name","validation error message","Failed")
     end
  end
  
  # checks for the unique Funds name functionality
  def create_Funds_duplicate_name(fund_name)
   
     # Click_Admin_page()
     # $ie.link(:text, $new_funds_link).click
     # Check_New_Funds()     
     # set_Funds_data(fund_name, glcode)
        
     # $ie.button(:value,"Preview").click
	 # validate_Funds_preview_page(fund_name, glcode)
      fund_count=count_items("select count(FUND_NAME) from FUND where FUND_NAME='"+fund_name+"'")
      
  	  if fund_count.to_i > 0
  	   $ie.button(:value,"Submit").click
  	   begin
  	      assert($ie.contains_text("The fund name " + fund_name.to_s + " already exists. Please specify another name.")) 
          $logger.log_results("Funds- duplicate error message appears for fundname "+fund_name.to_s,fund_name.to_s,"The fund name " + fund_name.to_s + " already exists. Please specify another name.","Passed")
       rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Funds- duplicate error message does not appear for fundname "+fund_name.to_s,fund_name.to_s,"The fund name " + fund_name.to_s + " already exists. Please specify another name.","failed")
       end
      else
        $ie.button(:value,"Submit").click    
      
      end
  end
  
  # Creates a new Funds and verify the preview page and database values after submission 
  def Create_New_Funds(fund_name, glcode)
    begin
    puts "Function: Create_New_Funds"
    set_Funds_data(fund_name, glcode)
     $ie.button(:value, "Preview").click
             
	validate_Funds_preview_page(fund_name, glcode)
	$ie.button(:value,"Submit").click
	
    validate_Funds_creation(fund_name, glcode)
    verify_admin_page()
    end
  end
    
  # Check the edit data link on the preview page
  def check_editdata_onpreview(fund_name, glcode)
      
     set_Funds_data(fund_name, glcode)
     $ie.button(:value,"Preview").click  
    
	 validate_Funds_preview_page(fund_name, glcode)
     $ie.button(:value, $edit_funds_link).click
        		
    assert($ie.contains_text($new_funds_page_msg))
        $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on 'Define a new Funds' link","Access to the Define a new Funds page","Passed")
        $ie.button(:value,"Preview").click
        validate_Funds_preview_page(fund_name, glcode)
        $ie.button(:value,"Cancel").click
        verify_admin_page()         
    rescue =>e
        $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on admin 'Define a new Funds' link","Access to the Define a new Funds page","Failed")    
  end
  
  # Check the edit data link on the preview page
  def edit_Funds_name(old_fund_name, new_fund_name, glcode)
    Click_Admin_page()    
    $ie.link(:text, $view_funds_link).click    
    dbquery("select fund_id from fund where fund_name = '" + old_fund_name.to_s + "'")
    fundId = dbresult[0]
    url = $test_site+"/fundAction.do?method=manage&fundId=" + fundId.to_i.to_s    
    puts "url: " + url.to_s
        
    $ie.link(:url, url).click
    assert($ie.contains_text(old_fund_name.to_s + "- Edit fund information "))\
    and assert($ie.text_field(:name, "fundName").getContents().to_s == old_fund_name.to_s)
      $logger.log_results("Funds- Check for edit the Funds with change the name", "click on edit Funds link ", "page to edit the Funds", "Passed")
      set_value_txtfield("fundName", new_fund_name)
        $ie.button(:value, "Preview").click
          if( $ie.contains_text(new_fund_name.to_s + " - Preview fund information") and $ie.contains_text("Name: " + new_fund_name.to_s) )
             $logger.log_results("Funds- Check for edit the Funds with change the name", "validating the preview page after change", "valid preview page after change", "Passed")
          else
             $logger.log_results("Funds- Check for edit the Funds with change the name", "validating the preview page after change", "valid preview page after change", "Failed")
          end 
          
        $ie.button(:value, "Submit").click
        check_view_Funds(new_fund_name, glcode)
    rescue =>e
        $logger.log_results("Funds- Check for edit the Funds with change the name", "click on edit Funds link ","page to edit the Funds", "Failed")
   
  end
  
  # Enter the new Funds data 
  def set_Funds_data(fund_name, glcode)
    begin
         if($ie.contains_text($new_funds_page_msg))
            $logger.log_results("Funds- Create new Funds page", "Click on 'Define a new Funds' link","Access to the new Funds page","Passed")
         else
            $logger.log_results("Funds- Create new Funds page", "Click on admin 'Define a new Funds' link","Access to the new Funds page","Failed")
         end
      set_value_txtfield("fundName", fund_name)
      set_value_selectlist("glCode.glCodeId", glcode)
   end
  end
  
  # Validate the preview for new check list  
  def  validate_Funds_preview_page(fund_name, glcode) 
    begin
      if $ie.contains_text($review_funds_info) then
		  $logger.log_results("New Funds- Preview", "new Funds inputs", "valid preview page","Passed")
	  else 
		  $logger.log_results("New Funds- Preview", "new Funds inputs" , "valid preview page","Failed")
	  end
      
      #if ($ie.contains_text("Name: " + fund_name.to_s)\
      #   and $ie.contains_text("GL code: " + glcode.to_s))
      #  $logger.log_results("Funds- validating the preview page", "Click on preview button","Valid preview page content", "Passed")
      #else
      #  $logger.log_results("Funds- validating the preview page", "Click on preview button","Valid preview page content", "Failed")
      #end
      assert_on_page("Name: " + fund_name.to_s)     
      assert_on_page("GL code: " + glcode.to_s)     
    end 
  end  
  
  # Verify the creation of new Funds from the data base  
  def   validate_Funds_creation(fund_name, glcode)
    begin    
      # Fetch the data from database and store the values in the corresponding variables
      dbquery("select GLCODE_VALUE, fund_name from fund f, gl_code gc where fund_id = (select  max(fund_id) from fund) and f.glcode_id = gc.glcode_id")
      db_glcode = dbresult[0]
      db_fund_name = dbresult[1]
	  
      #if(db_glcode.to_s ==  glcode.to_s\
      #  and db_fund_name.to_s ==  fund_name.to_s)        
      #  $logger.log_results("Funds- validating the Funds creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Passed")
      #else
      #  $logger.log_results("Funds- validating the Funds creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Failed")
      #end
      dbcheck("fund name",fund_name.to_s,db_fund_name.to_s) 
      dbcheck("Glcode",glcode.to_s,db_glcode.to_s) 
    end 
  end  
  
  # Mandatory check for view check list
  def check_view_Funds(fund_name, glcode)
    begin
      Click_Admin_page()
      $ie.link(:text, $view_funds_link).click
      $ie.wait 
      assert($ie.contains_text(fund_name.to_s)) and assert($ie.contains_text(glcode.to_s))
            $logger.log_results("Funds- validating the preview page for new Funds", "Click view Funds link", "Valid preview page content", "Passed")
      rescue =>e
          $logger.log_results("Funds- validating the preview page for new Funds", "Click view Funds link","Valid preview page content", "Failed")  
   end 
  end  
 end  