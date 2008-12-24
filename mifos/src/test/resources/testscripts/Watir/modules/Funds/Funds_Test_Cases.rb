# This file contains all the automation test case scenario for testing the funds module
require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'

#include module watir
include Watir

class Funds_Test_Cases < TestClass
  
  def read_fund_values(rowid)
    @fund_name = arrval[rowid+=1].to_s
    # @glcode = arrval[rowid+=1].to_i.to_s
    @glcode = arrval[rowid+=1].to_s
  end
  
  def Fund_name
    @fund_name
  end
  
  def Gl_code
    @glcode
  end
  
  #read from properties file into a hash
  def read_from_properties_file()
    begin
      @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
      @@fundprop=load_properties("modules/propertis/FundUIResources.properties")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #read values from hash into variables
  def read_values_from_hash()
    begin
       #labels
       @define_new_fund_label=@@fundprop['funds.new_fund']
       @enter_fund_label=@@fundprop['funds.enter_fund']
       @fund_name_label=string_replace_message(@@fundprop['funds.new_fund'],@@adminprop['admin.definenew'],"")
       @review_fund_label=@define_new_fund_label+" - "+@@fundprop['funds.review']+" & "+@@fundprop['funds.submit']
       @edit_fund_label=@@fundprop['funds.editfund']
       #links
       @view_funds_link=@@adminprop['admin.viewfunds']
       @define_new_fund_link=@@adminprop['admin.defnewfund']
       
       #buttons
       @cancel_button=@@fundprop['funds.cancel']
       @preview_button=@@fundprop['funds.preview']
       @submit_button=@@fundprop['funds.submit']
       @edit_fund_button=@@fundprop['funds.editfund']
       #error msg
       @fund_name_msg=string_replace_message(@@fundprop['errors.mandatory_textbox'],"{0}",@fund_name_label.to_s+@@fundprop['funds.fund_name'].to_s)
       @fund_code_msg=string_replace_message(@@fundprop['errors.mandatory_selectbox'],"{0}",@@fundprop['funds.fundcode'])
       @duplicate_name_msg=@@fundprop['errors.DuplicateFundNameException']
    rescue =>excp
      quit_on_error(excp)
    end
  end
  # Check funds link on admin page after login
  def Check_Funds_Links()
    begin    
      assert($ie.link(:text,@view_funds_link).exists?()) and assert($ie.link(:text,@define_new_fund_link).exists?())
      $logger.log_results("Funds- Check for Funds links on admin page", "Click on admin link","The links should be there","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- Check for Funds links on admin page", "Click on admin link","The links should be there","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check cancel in 'Define new Funds' link on admin page
  def Check_New_Funds_cancel()
    begin
      $ie.link(:text, @define_new_fund_link).click
      $ie.button(:value, @cancel_button).click     
      verify_admin_page()      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check new Funds link on admin page
  def Check_New_Funds()
    begin      
      assert($ie.contains_text(@define_new_fund_label+" - "+@enter_fund_label))
      $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on 'Define a new Funds' link","Access to the Define a new Funds page", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on 'Define a new Funds' link","Access to the Define a new Funds page", "Failed")
    rescue =>excp
      quit_on_error(excp)
    end      
  end
  
  def click_new_fund()
    $ie.link(:text,@define_new_fund_link).click
  end
  # Check for all mandatory validation error
  def check_validation_error(fund_name, glcode)
    begin     
      $ie.link(:text, @define_new_fund_link).click      
      Man_New_Funds()      
      Man_New_Funds_with_Name(fund_name)
      Man_New_Funds_with_GLCode(glcode)
      if $ie.contains_text(@review_fund_label)
        
        create_Funds_duplicate_name(fund_name)
      end
    rescue =>excp
      quit_on_error(excp)
    end    
  end 
  
  # Check for validation messages while creating new Funds
  def Man_New_Funds()
    begin  
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(@define_new_fund_label+" - "+@enter_fund_label))\
      and assert($ie.contains_text(@fund_name_msg))\
      and assert($ie.contains_text(@fund_code_msg))      
      $logger.log_results("Funds- Check for validation error while creating new Funds", "nothing ", "All validation error message", "Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- Check for validation error while creating new Funds", "nothing ", "All validation error message", "Failed")       
    rescue =>excp
      quit_on_error(excp)
    end    
  end
  
  # Check for validation messages while creating new Funds with fund_name
  def  Man_New_Funds_with_Name(fund_name)
    begin
      set_value_txtfield("fundName", fund_name)
      $ie.button(:value,@preview_button).click
      assert(!$ie.contains_text(@fund_name_msg)) and assert($ie.contains_text(@define_new_fund_label+" - "+@enter_fund_label))\
      and assert($ie.contains_text(@fund_code_msg))  
      $logger.log_results("Funds- Check for validation error while creating new Funds", fund_name,"All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- Check for validation error while creating new Funds", fund_name,"All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)
    end    
  end
  
  # Check for validation messages while creating new Funds with product_type, category_name
  def Man_New_Funds_with_GLCode(glcode)    
    begin
      #set_value_txtfield("fundName", fund_name)
      set_value_selectlist("fundCode", glcode)
      $ie.button(:value,@preview_button).click
      assert(!$ie.contains_text(@fund_code_msg))
      #assert($ie.contains_text(@review_fund_label))
      $logger.log_results("Funds- No error message appears when fund code is entered", glcode, "No error message for fund code field","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- error message appears when fund code is entered", glcode, "No error message for fund code field","failed")     
    rescue =>excp
      quit_on_error(excp)
    end    
  end
  
  # Check for all mandatory validation error
  def check_field_validation_error(fund_name)
    begin
      Click_Admin_page()
      $ie.link(:text, @define_new_fund_link).click       
      Check_New_Funds()      
      verify_FundName(fund_name)
      $ie.button(:value, @cancel_button).click 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check for field validation messages while creating new Funds with category_name
  def verify_FundName(fund_name)
    begin
      set_value_txtfield("fundName", fund_name)
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text("to be add"))      
      $logger.log_results("Funds- Check for validation error while creating new Funds", "fund_name", "validation error message", "Passed")
      rescue=>e
      $logger.log_results("Funds- Check for validation error while creating new Funds", "fund_name","validation error message","Failed")
    end
  end
  
  # checks for the unique Funds name functionality
  def create_Funds_duplicate_name(fund_name)
    begin
      fund_count=count_items("select count(FUND_NAME) from FUND where FUND_NAME='"+fund_name+"'")
      if fund_count.to_i > 0
        $ie.button(:value,@submit_button).click
        begin
          assert($ie.contains_text(@duplicate_name_msg)) 
          $logger.log_results("Funds- duplicate error message appears for fundname "+fund_name.to_s,fund_name.to_s,@duplicate_name_msg,"Passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Assertion failure error Funds- duplicate error message does not appear for fundname "+fund_name.to_s,fund_name.to_s,@duplicate_name_msg,"failed")
        end
      else
        $ie.button(:value,@submit_button).click    
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Creates a new Funds and verify the preview page and database values after submission 
  def Create_New_Funds(fund_name, glcode)
    begin
      #puts "Function: Create_New_Funds"
      
      set_Funds_data(fund_name, glcode)
      $ie.button(:value, @preview_button).click
      
      validate_Funds_preview_page(fund_name, glcode)
      $ie.button(:value,@submit_button).click
      
      validate_Funds_creation(fund_name, glcode)
      verify_admin_page()
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check the edit data link on the preview page
  def check_editdata_onpreview(fund_name, glcode)
    begin
      set_Funds_data(fund_name, glcode)
      $ie.button(:value,@preview_button).click  
      
      validate_Funds_preview_page(fund_name, glcode)
      $ie.button(:value, @edit_fund_button).click
      
      assert($ie.contains_text(@define_new_fund_label+" - "+@enter_fund_label))
      $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on 'Define a new Funds' link","Access to the Define a new Funds page","Passed")
      $ie.button(:value,@preview_button).click
      validate_Funds_preview_page(fund_name, glcode)
      $ie.button(:value,@cancel_button).click
      verify_admin_page()         
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- Check for Define a new Funds link on admin page", "Click on admin 'Define a new Funds' link","Access to the Define a new Funds page","Failed")    
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check the edit data link on the preview page
  def edit_Funds_name(old_fund_name, new_fund_name, glcode)
    begin
      Click_Admin_page()    
      $ie.link(:text, @view_funds_link).click    
      dbquery("select fund_id from fund where fund_name = '" + old_fund_name.to_s + "'")
      fundId = dbresult[0]
      #navigating to the first link and then getting the random number and current flow keys
      $ie.link(:text,@@fundprop['funds.edit']).click
      old_url=$ie.url
      old_url=old_url.split('&')
      randnumber=old_url[2].to_s
      curflowkey=old_url[3].to_s
      #$ie.link(:text, @view_funds_link).click    
      $ie.back
      #navigate to the correct url with the correct fundcodeid and random number and currentflowkey
      url = $test_site+"/fundAction.do?method=manage&fundCodeId=" + fundId.to_i.to_s+"&"+randnumber+"&"+curflowkey    
      #puts "url: " + url.to_s
      
      $ie.link(:url, url).click
      assert($ie.contains_text(old_fund_name.to_s + "- "+@edit_fund_label ))\
      and assert($ie.text_field(:name, "fundName").getContents().to_s == old_fund_name.to_s)
      $logger.log_results("Funds- Check for edit the Funds with change the name", "click on edit Funds link ", "page to edit the Funds", "Passed")
      set_value_txtfield("fundName", new_fund_name)
      $ie.button(:value, @preview_button).click
      if( $ie.contains_text(new_fund_name.to_s + " - "+@@fundprop['funds.preview_fundInfo']) and $ie.contains_text(@@fundprop['funds.fund_name'].to_s+": "+ new_fund_name.to_s) )
        $logger.log_results("Funds- Check for edit the Funds with change the name", "validating the preview page after change", "valid preview page after change", "Passed")
      else
        $logger.log_results("Funds- Check for edit the Funds with change the name", "validating the preview page after change", "valid preview page after change", "Failed")
      end 
      
      $ie.button(:value, @submit_button).click
      check_view_Funds(new_fund_name, glcode)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- Check for edit the Funds with change the name", "click on edit Funds link ","page to edit the Funds", "Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Enter the new Funds data 
  def set_Funds_data(fund_name, glcode)
    begin
      if($ie.contains_text(@define_new_fund_label+" - "+@enter_fund_label))
        $logger.log_results("Funds- Create new Funds page", "Click on 'Define a new Funds' link","Access to the new Funds page","Passed")
      else
        $logger.log_results("Funds- Create new Funds page", "Click on admin 'Define a new Funds' link","Access to the new Funds page","Failed")
      end
      set_value_txtfield("fundName", fund_name)
      set_value_selectlist("fundCode", glcode)
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Validate the preview for new check list  
  def  validate_Funds_preview_page(fund_name, glcode) 
    begin
      assert($ie.contains_text(@review_fund_label))
      assert_on_page(@@fundprop['funds.fund_name'].to_s+": " + fund_name.to_s)     
      assert_on_page(@@fundprop['funds.fundcode'].to_s+": " + glcode.to_s)     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure error: "+@define_new_fund_label+" - "+@@fundprop['funds.review']+" &amp; "+@@fundprop['funds.submit']+" label does not appear","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)
    end 
  end  
  
  # Verify the creation of new Funds from the data base  
  def   validate_Funds_creation(fund_name, glcode)
    begin    
      # Fetch the data from database and store the values in the corresponding variables
      dbquery("select fc.fundcode_value,f.fund_name from fund f,fund_code fc where f.fundcode_id=fc.fundcode_id and f.fund_id=(select max(fund_id) from fund)")
      db_glcode = dbresult[0]
      db_fund_name = dbresult[1]
      dbcheck("fund name",fund_name.to_s,db_fund_name.to_s) 
      dbcheck("fund code",glcode.to_s,db_glcode.to_s) 
    rescue =>excp
      quit_on_error(excp)
    end 
  end  
  
  # Mandatory check for view check list
  def check_view_Funds(fund_name, glcode)
    begin
      Click_Admin_page()
      $ie.link(:text, @view_funds_link).click
      $ie.wait 
      assert($ie.contains_text(fund_name.to_s)) and assert($ie.contains_text(glcode.to_s))
      $logger.log_results("Funds- validating the preview page for new Funds", "Click view Funds link", "Valid preview page content", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Funds- validating the preview page for new Funds", "Click view Funds link","Valid preview page content", "Failed")  
    rescue =>excp
      quit_on_error(excp)
    end 
  end  
end  


class Funds_Main
  funds_obj = Funds_Test_Cases.new
  funds_obj.read_from_properties_file
  funds_obj.read_values_from_hash
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
    funds_obj.click_new_fund
    funds_obj.Create_New_Funds(funds_obj.Fund_name,funds_obj.Gl_code)
    funds_obj.click_new_fund
    funds_obj.check_editdata_onpreview(funds_obj.Fund_name,funds_obj.Gl_code)
    funds_obj.check_view_Funds(funds_obj.Fund_name,funds_obj.Gl_code)
    
    #funds_obj.create_Funds_duplicate_name(fund_name, glcode)
    #end
    funds_obj.edit_Funds_name(funds_obj.Fund_name,funds_obj.Fund_name + "123",funds_obj.Gl_code)
    funds_obj.validate_Funds_creation(funds_obj.Fund_name+"123",funds_obj.Gl_code)
    rowid+=$maxcol
  end 
  funds_obj.mifos_logout()
end