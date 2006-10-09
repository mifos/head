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
class Check_List_Test_Cases < TestClass
  #reads values from the array into instance variables
  def read_checklist_values(rowid)
    @chklist_name = arrval[rowid+=1].to_s     
    @chklist_type = arrval[rowid+=1].to_s  
    @chklist_status = arrval[rowid+=1].to_s  
    @items = arrval[rowid+=1].to_s 
    @chklist_items = @items.split(",").to_a  
    @type = arrval[rowid+=1].to_i   
  end
  
  def Checklist_name
    @chklist_name
  end 
  
  def Checklist_type
    @chklist_type
  end
  
  def Checklist_status
    @chklist_status
  end
  
  def Checklist_items
    @chklist_items
  end
  
  def Type
    @type
  end
  
  def read_from_properties_file()
    begin
      @@customersearch_prop=load_properties("modules/propertis/CustomerSearchUIResources.properties")
      @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
      @@checklistprop=load_properties("modules/propertis/CheckListUIResources.properties")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  def read_values_from_hash()
    begin
      #labels
      @welcome_label=@@customersearch_prop['CustomerSearch.welcome']
      @adminstrative_tasks_label=@@adminprop['admin.admintasks']
      @manage_org_label=@@adminprop['admin.manageorg']
      @add_new_checklist_label=@@checklistprop['checklist.addnewchecklist']+" - "+@@checklistprop['checklist.enterchecklist_Info']
      @review_checklist_label=@@checklistprop['checklist.addnewchecklist']+" - "+@@checklistprop['checklist.reviewchecklist_Info']
      @edit_checklist_label=@@checklistprop['checklist.edit_checklist']
      #links
      @admin_link=@@checklistprop['checklist.admin']
      @view_checklist_link=@@adminprop['admin.viewcheklists']
      @define_new_checklist_link=@@adminprop['admin.defnewchecklist']
      #buttons
      @cancel_button=@@checklistprop['checklist.button_cancel']
      @preview_button=@@checklistprop['checklist.button_preview']
      @add_button=@@checklistprop['checklist.button_add']
      @submit_button=@@checklistprop['checklist.button_submit']
      #error msg
      @checklist_name_msg=string_replace_message(@@checklistprop['errors.mandatory_textbox'],"{0}",@@checklistprop['checklist.checklistName'])
      @checklist_type_msg=string_replace_message(@@checklistprop['errors.mandatory_selectbox'],"{0}",@@checklistprop['checklist.checklistType'])
      @checklist_status_msg=string_replace_message(@@checklistprop['errors.mandatory_selectbox'],"{0}",@@checklistprop['checklist.display_status'])
      @checklist_item_msg=string_replace_message(@@checklistprop['errors.mandatory_item'],"{0}",@@checklistprop['checklist.display_status'])
      @max_name_length_msg=string_replace_message(@@checklistprop['errors.maximumlength'],"{0}",@@checklistprop['checklist.checklistName'])
      @max_name_length_msg=string_replace_message(@max_name_length_msg,"{1}","50")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  # Login to Mifos and check the first page after login page
  def CheckList_login()
    start
    login($validname,$validpwd)
    if $ie.contains_text(@welcome_label.to_s+",  "+ $validname) then
      $logger.log_results("Label "+@welcome_label+",  "+ $validname+" appears in home page", "NA","Label should appear","Passed")
    else 
      $logger.log_results("Assertion failure error:Label "+@welcome_label+",  "+ $validname+" does not appear on home page", "NA","Label should appear","Failed")
    end 
    db_connect
  end
  
  # Check for admin page after login
  def Click_Admin_page()
    begin
      $ie.link(:text,@admin_link).click
      assert($ie.contains_text(@adminstrative_tasks_label)) and assert($ie.contains_text(@manage_org_label))
      $logger.log_results("Labels "+@adminstrative_tasks_label+" and "+@manage_org_label+" appear in admin page", "Click on admin link","Access to the admin page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Assertion failure:Labels "+@adminstrative_tasks_label+" and "+@manage_org_label+" do not appear in admin page", "Click on admin link","Access to the admin page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check check list link on admin page after login
  def Check_CheckList_Links()
    begin     
      assert($ie.link(:text,@view_checklist_link).exists?()) and assert($ie.link(:text,@define_new_checklist_link).exists?())
      $logger.log_results("Links "+@view_checklist_link+" and "+@define_new_checklist_link+" exists on admin page", "Click on admin link","The links should be there","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Links "+@view_checklist_link+" and "+@define_new_checklist_link+" do not exist on admin page", "Click on admin link","The links should be there","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  # Check cancel in new check list link on admin page
  def Check_New_CheckList_cancel()
    begin
      $ie.link(:text,@define_new_checklist_link).click
      $ie.button(:value,@cancel_button).click
      assert($ie.contains_text(@adminstrative_tasks_label))
      $logger.log_results("CheckList- Check for cancel at new check list link", "Click on cancel button ","The current page should be navigated to admin page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("CheckList- Check for cancel at new check list link", "Click on cancel button ","The current page should be navigated to admin page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  
  # Check new check list link on admin page
  def Check_New_CheckList()
    begin
      $ie.link(:text,@define_new_checklist_link).click
      assert($ie.contains_text(@add_new_checklist_label))
      $logger.log_results("CheckList- Check for new check list link on admin page", "Click on 'Define new checklist' link","Access to the new check list page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("CheckList- Check for new check list link on admin page", "Click on admin 'Define new checklist' link","Access to the new check list page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check for validation messages while creating new check list
  def Man_New_CheckList()
    begin
      $ie.link(:text,@define_new_checklist_link).click
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text($checklist_item_msg)) 
      assert($ie.contains_text(@checklist_name_msg)) 
      assert($ie.contains_text(@checklist_type_msg)) 
      assert($ie.contains_text(@checklist_status_msg)) 
      $logger.log_results("CheckList- Check for validation error while creating new check list", "nothing ","All validation error message","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("CheckList- Check for validation error while creating new check list", "nothing ","All validation error message","Failed")              
    rescue =>excp
      quit_on_error(excp)  
    end    
    #           $ie.button(:value,@cancel_button).click
  end
  
  # Check for validation messages while creating new check list with only name 
  def Man_New_CheckList_with_name(checklist_name)
    begin
      # $ie.link(:text,@define_new_checklist_link).click
      $ie.text_field(:name,"checklistName").set(checklist_name)            
      $ie.button(:value,@preview_button).click
      #max_field_len(checklist_name,50,$checklist_max_len)
      assert(!$ie.contains_text(@checklist_name_msg))
      assert($ie.contains_text($checklist_item_msg))
      assert($ie.contains_text(@checklist_type_msg))
      assert($ie.contains_text(@checklist_status_msg)) 
      $logger.log_results("CheckList- Check for validation error while creating new check list", "Name","All validation error message","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("CheckList- Check for validation error while creating new check list", "Name ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
    #          $ie.button(:value,@cancel_button).click
  end
  
  # Check for validation messages while creating new check list with only name and type
  def Man_New_CheckList_with_type(type)
    begin
      # $ie.link(:text,@define_new_checklist_link).click
      # $ie.text_field(:name,"checklistName").set(checklist_name)  
      $ie.select_list(:name,"type").select(type)
      $ie.button(:value,@preview_button).click
      assert(!$ie.contains_text(@checklist_type_msg)) 
      assert($ie.contains_text($checklist_item_msg)) 
      assert($ie.contains_text(@checklist_status_msg)) 
      $logger.log_results("CheckList- Check for validation error while creating new check list", "Name and type ","All validation error message","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("CheckList- Check for validation error while creating new check list", "Name and type ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)
    end
    #         $ie.button(:value,@cancel_button).click
  end
  
  # Check for validation messages while creating new check list
  def Man_New_CheckList_with_status(status)
    begin
      # $ie.link(:text,@define_new_checklist_link).click
      # $ie.text_field(:name,"checklistName").set(checklist_name)  
      # $ie.select_list(:name,"type").select(type)
      $ie.select_list(:name,"stateId").select(status)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@checklist_status_msg)) 
      assert($ie.contains_text($checklist_item_msg))
      $logger.log_results("CheckList- Check for validation error while creating new check list", "Name,type and status ","All validation error message","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("CheckList- Check for validation error while creating new check list", "Name,type and status ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)
    end
    #  $ie.button(:value,@cancel_button).click
  end
  
  # Create new check list for all possible combinations with name provided as parameter
  def Create_CheckLists(checklist_name)
    
    begin                     
      # Create the check list for all the customer 
      @@res_customer_type = $dbh.real_query("select Look.entity_name , cl.level_id  from lookup_label Look, customer_level cl where Look.entity_id in (select distinct level_name_id from customer_level) and (Look.locale_id = 1) and (Look.entity_id = cl.level_name_id)")
      @@max_customer_type_count = @@res_customer_type.num_rows() 
      
      # Iterate through each type available
      @@customer_type_count = 0       
      while @@customer_type_count < @@max_customer_type_count      
        @@resultset_customer_type = @@res_customer_type.fetch_row.to_a
        
        @@type_name = @@resultset_customer_type[0]
        @@customer_level_id = @@resultset_customer_type[1]
        
        @@res_customer_status = $dbh.real_query("select  cs.status_id, lvl.lookup_value from customer_state cs, lookup_value_locale lvl where cs.level_id ="+ @@customer_level_id+ " and cs.status_lookup_id = lvl.lookup_id and lvl.locale_id =1 and lvl.lookup_id not in (1,7,13)")
        
        # Iterate through each status for a particular type
        @@customer_status_count = 0 
        @@max_customer_status_count = @@res_customer_status.num_rows()
        
        puts "@@customer_status_count: " + @@customer_status_count.to_s
        puts "@@max_customer_status_count " + @@max_customer_status_count.to_s
        puts "@@customer_type_count " + @@customer_type_count.to_s
        
        while  @@customer_status_count < @@max_customer_status_count
          puts " in while loop for status @@customer_status_count: " + @@customer_status_count.to_s
          @@resultset_customer_status = @@res_customer_status.fetch_row.to_a
          @@customer_status_id = @@resultset_customer_status[0]        
          @@status_name = @@resultset_customer_status[1]        
          Create_New_CheckList(checklist_name + @@customer_type_count.to_s + @@customer_status_count.to_s, @@customer_type_count, @@customer_status_id,0)
          @@customer_status_count += 1            
        end        
        @@customer_type_count += 1        
        
      end 
      
      # Create the check list for all the product
      @@res_product_type = $dbh.real_query("select lvl.lookup_value, pt.prd_type_id from lookup_value_locale lvl, prd_type pt  where lvl.lookup_id in (select distinct prd_type_lookup_id from prd_type) and locale_id = 1 and pt.prd_type_lookup_id = lvl.lookup_id")      
      @@max_product_type_count = @@res_product_type.num_rows()
      
      @@product_type_count = 0       
      while @@product_type_count < @@max_product_type_count      
        
        @@resultset_product_type = @@res_product_type.fetch_row.to_a
        
        @@type_name = @@resultset_product_type[0]
        @@prd_type_id = @@resultset_product_type[1]
        
        @@res_product_status = $dbh.real_query("select  asa.account_state_id, lvl.lookup_value from account_state asa, lookup_value_locale lvl where asa.prd_type_id = "+ @@prd_type_id +" and asa.lookup_id = lvl.lookup_id and lvl.locale_id =1 and asa.account_state_id not in (1,6,11,12,13,17)")
        @@product_status_count = 0 
        @@max_product_status_count = @@res_product_status.num_rows()
        puts "@@product_type_count: "   + @@product_type_count.to_s 
        @@type_count = @@product_type_count.to_i +    @@max_customer_type_count.to_i 
        puts "@@type_count: "   + @@type_count.to_s
        
        puts "@@product_status_count " + @@product_status_count.to_s
        puts "@@max_product_status_count " + @@max_product_status_count.to_s
        puts "@@type_count " + @@type_count.to_s
        
        while  @@product_status_count < @@max_product_status_count        
          
          puts "in while loop @@product_status_count " + @@product_status_count.to_s
          @@resultset_product_status = @@res_product_status.fetch_row.to_a
          @@product_status_id = @@resultset_product_status[0]        
          @@status_name = @@resultset_product_status[1]        
          puts "@@product_status_id: " + @@product_status_id.to_s
          
          @@status_count = @@max_customer_status_count.to_i + @@product_status_count
          Create_New_CheckList(checklist_name + (@@product_type_count + @@max_customer_type_count.to_i).to_s + @@product_status_count.to_s, @@type_count, @@product_status_id, 1)
          @@product_status_count += 1            
        end              
        @@product_type_count += 1
        
      end      
      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Create a new checklist and verify the preview page and database values after submission
  def Create_New_CheckList(checklist_name, chk_type, status, check_list_items, checklist_type)
    begin
      $ie.link(:text,@admin_link).click
      $ie.link(:text,@define_new_checklist_link).click
      if($ie.contains_text(@add_new_checklist_label))
        $logger.log_results("CheckList- Check for new check list link on admin page", "Click on 'Define new checklist' link","Access to the new check list page","Passed")
      else
        $logger.log_results("CheckList- Check for new check list link on admin page", "Click on admin 'Define new checklist' link","Access to the new check list page","Failed")
      end
      
      $ie.text_field(:name,"checklistName").set(checklist_name)        
      $ie.select_list(:name,"type").select(chk_type)
      $ie.select_list(:name,"stateId").select(status)
      
      count = 0
      while count < check_list_items.length
        $ie.text_field(:name,"text").set(check_list_items[count])        
        $ie.button(:value,@add_button).click                
        if $ie.contains_text(check_list_items[count]) then
          $logger.log_results("New CheckList- Check list item add", "ItemName " + check_list_items[count],"Item should be added","Passed")
        else 
          $logger.log_results("New CheckList- Check list item add", "ItemName " + check_list_items[count],"Item should be added","Failed")
        end  
        count+=1             
      end
      
      $ie.button(:value,@preview_button).click      
      if $ie.contains_text(@review_checklist_label) then
        $logger.log_results("New CheckList- Preview", "Check_List_Name " + checklist_name , "valid preview page","Passed")
      else 
        $logger.log_results("New CheckList- Preview", "Check_List_Name " + checklist_name , "valid preview page","failed")
      end 
      
      validate_preview_page(checklist_name, chk_type, status, check_list_items, checklist_type)
    rescue =>excp
      quit_on_error(excp)
    end
  end  
  
  # Validate the preview for new check list
  def validate_preview_page(checklist_name, chk_type, status, check_list_items, checklist_type)
    begin
      #if ($ie.contains_text("Name: " + checklist_name) and $ie.contains_text("Type: "+ chk_type) and $ie.contains_text("Displayed when moving into Status: " + status))
        #$logger.log_results("Checklist- validating the preview page for new check list", "Click on preview button","Valid preview page content", "Passed")
      #else
        #$logger.log_results("CheckList- validating the preview page for new check list", "Click on preview button","Valid preview page content", "Failed")
      #end    
      assert_on_page(@@checklistprop['checklist.name'].to_s+" "+checklist_name)
      assert_on_page(@@checklistprop['checklist.type'].to_s+" "+chk_type)
      assert_on_page(@@checklistprop['checklist.displayed_status'].to_s+" "+status)
      count = 0 
      while count < check_list_items.length
        if $ie.contains_text(check_list_items[count]) then
          $logger.log_results("New CheckList- Check list item added", "ItemName " + check_list_items[count],"Item should be displayed","Passed")
        else 
          $logger.log_results("New CheckList- Check list item added", "ItemName " + check_list_items[count],"Item should be displayed","failed")
        end	  	  
        count+=1           
      end      
      $ie.button(:value,@submit_button).click
      validate_checklist_creation(checklist_name,chk_type, status, check_list_items, checklist_type)
    rescue =>excp
      quit_on_error(excp)
    end 
  end   
  
  
  # Verify the creation of new list from the data base 
  def validate_checklist_creation(checklist_name, chk_type, status, check_list_items, checklist_type)
    begin
      dbquery("select checklist_id from checklist where checklist_name = '"+ checklist_name+"' and checklist_id = (select max(checklist_id) from checklist where checklist_name = '"+ checklist_name +"')")
      @@checklist_id = dbresult[0]      
      if 0 == checklist_type then
        getdata_checklist_customer(@@checklist_id)
      else
        getdata_checklist_product(@@checklist_id)
      end      
      
      
      item_count = 0 
      max_item_count = check_list_items.length
      
      if(@@checklist_type.to_s == chk_type  and @@checklist_status.to_s == status )      
        $logger.log_results("CheckList- validating the type and status for created check list from database", "type " + @@checklist_type.to_s+ "\tstatus " + @@checklist_status.to_s,"type " + chk_type.to_s+ "\tstatus " + status.to_s, "Passed")
      else
        $logger.log_results("CheckList- validating the type and status for created check list from database", "type " + @@checklist_type.to_s+ "\tstatus " + @@checklist_status.to_s,"type " + chk_type.to_s+ "\tstatus " + status.to_s, "Failed")
      end
      res_checklist_items = $dbh.real_query("select detail_text from checklist_detail where checklist_id ="+ @@checklist_id)
      while  item_count < max_item_count        
        resultset_items =res_checklist_items.fetch_row.to_a
        item_name = resultset_items[0]
        if item_name.to_s == check_list_items[item_count].to_s then 
          $logger.log_results("New CheckList- Check list item verification from database", "ItemName " + item_name.to_s,"ItemName " + check_list_items[item_count].to_s,"Passed")
        else 
          $logger.log_results("New CheckList- Check list item verification from database", "ItemName " + item_name.to_s,"ItemName " + check_list_items[item_count].to_s,"Failed")
        end 
        item_count += 1 
      end      
      check_view_checklist(checklist_name,chk_type,status, check_list_items, checklist_type)      
    rescue =>excp
      quit_on_error(excp)
    end 
  end
  
  # Get data for customer checklist
  def getdata_checklist_customer(checklist_id)
    begin
      dbquery("select level_id, customer_status_id from customer_checklist where checklist_id =" + checklist_id)
      @@level_id = dbresult[0]
      @@customer_status_id = dbresult[1]
      
      dbquery("select Look.entity_name from lookup_label Look, customer_level cl where Look.entity_id = (select level_name_id  from customer_level where level_id = "+ @@level_id+") and Look.locale_id = 1 and Look.entity_id = cl.level_name_id")
      @@checklist_type = dbresult[0]
      
      dbquery("select lookup_value from lookup_value_locale where lookup_id = (select status_lookup_id from customer_state where status_id = " + @@customer_status_id+")and locale_id = 1")
      @@checklist_status = dbresult[0]  
    rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  # Get data for product checklist
  def getdata_checklist_product(checklist_id)
    begin
      dbquery("select prd_type_id, account_status from prd_checklist where checklist_id = " + checklist_id)
      @@prd_type_id = dbresult[0]
      @@account_status = dbresult[1]
      
      dbquery("select lookup_value from lookup_value_locale where lookup_id = (select prd_type_lookup_id from prd_type where prd_type_id = " + @@prd_type_id + ") and locale_id = 1")
      @@checklist_type = dbresult[0]
      
      dbquery("select lookup_value from lookup_value_locale where lookup_id = (select lookup_id from account_state where prd_type_id = " + @@prd_type_id + " and account_state_id = " + @@account_status + " ) and locale_id = 1")
      @@checklist_status = dbresult[0]  
    rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  # Mandatory check for view check list 
  def check_view_checklist(checklist_name,chk_type,status, check_list_items, checklist_type)
    begin 
      $ie.link(:text,@view_checklist_link).click         
      $ie.link(:text, checklist_name).click    
      dbquery("select date_format(c.created_date,'%d/%m/%Y'), p.login_name from checklist c, personnel p where c.created_by  = p.personnel_id and checklist_id =" + @@checklist_id)
      created_date = dbresult[0]
      created_by = dbresult[1]
#      if($ie.contains_text(checklist_name) and $ie.contains_text(@@checklistprop['checklist.type']+" "+ chk_type.to_s)and $ie.contains_text(@@checklistprop['checklist.status']+" "+ status.to_s) and $ie.contains_text(@@checklistprop['checklist.createdby']+" : "+ created_by.to_s) and $ie.contains_text(@@checklistprop['checklist.createddate']+" : "+ created_date.to_s))
#        $logger.log_results("CheckList- validating the preview page for new check list", "Click on preview button","Valid preview page content", "Passed")
#      else
#        $logger.log_results("CheckList- validating the preview page for new check list", "Click on preview button","Valid preview page content", "Failed")
#      end
      $logger.log_results("checking for items attached to the checklist ","NA","NA","NA")
      assert_on_page(checklist_name)
      assert_on_page(@@checklistprop['checklist.type']+" "+ chk_type.to_s)
      assert_on_page(@@checklistprop['checklist.status']+" : "+ status.to_s)
      assert_on_page(@@checklistprop['checklist.createdby']+" : "+ created_by.to_s)
      assert_on_page(@@checklistprop['checklist.createddate']+" : "+ created_date.to_s)
      count = 0 
      while count < check_list_items.length
        # if $ie.contains_text(check_list_items[count]) then
        #	 $logger.log_results("New CheckList- Check list item added", "ItemName: " + check_list_items[count],"Item should be displayed","Passed")
        # else 
        #	 $logger.log_results("New CheckList- Check list item added", "ItemName: " + check_list_items[count],"Item should be displayed","failed")
        # end
        
        assert_on_page(check_list_items[count].to_s)	  	  
        count+=1           
      end 
      edit_checklist(checklist_name)    
    rescue =>excp
      quit_on_error(excp)
    end 
  end 
  
  # Check for edit check list link
  def edit_checklist(checklist_name)
    begin
      begin    
        $ie.link(:text,@@checklistprop['checklist.edit_checklist']).click 
        assert($ie.contains_text(checklist_name+ " - "+@edit_checklist_label))
        $logger.log_results("CheckList- edit check list ", "click on Edit checklist link","Edit page should be opened","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("CheckList- edit check list ", "click on Edit checklist link","Edit page should be opened","Failed")
      end 
      $ie.button(:value,@cancel_button).click     
      $ie.link(:text,@admin_link).click
    rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  
  # To delete all checklist(As there is no option for it in User interface)
  def delete_all_checklist()
    begin    
      $dbh.real_query("delete from prd_checklist")
      $dbh.real_query("delete from customer_checklist")    
      $dbh.real_query("delete from checklist_detail")
      $dbh.real_query("delete from checklist")      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # To log out from the application 
  def checklist_logout()
    begin
      mifos_logout()
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
end  


class Check_List
  checklist_obj=Check_List_Test_Cases.new
  checklist_obj.read_from_properties_file
  checklist_obj.read_values_from_hash
  checklist_obj.CheckList_login
  checklist_obj.Click_Admin_page
  checklist_obj.Check_CheckList_Links
  checklist_obj.Check_New_CheckList_cancel
  filename = File.expand_path(File.dirname($PROGRAM_NAME))+"/data/checklist.xls"
  checklist_obj.open(filename,1)  
  count=0
  rowid=-1
  while(rowid < $maxrow * $maxcol - 1)
    checklist_obj.read_checklist_values(rowid)
    if (0 == count )
      checklist_obj.Man_New_CheckList
      checklist_obj.Man_New_CheckList_with_name(checklist_obj.Checklist_name)
      checklist_obj.Man_New_CheckList_with_type(checklist_obj.Checklist_type)
      checklist_obj.Man_New_CheckList_with_status(checklist_obj.Checklist_status)
      #$ie.link(:text,@admin_link).click
    end    
    checklist_obj.Create_New_CheckList(checklist_obj.Checklist_name,checklist_obj.Checklist_type,checklist_obj.Checklist_status,checklist_obj.Checklist_items,checklist_obj.Type )
    count = 1
    rowid+=$maxcol
  end
  #checklist_obj.delete_all_checklist()
  checklist_obj.checklist_logout()
end