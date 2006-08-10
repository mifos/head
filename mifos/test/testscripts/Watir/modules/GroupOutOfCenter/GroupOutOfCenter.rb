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
class GroupOutOfCenter<TestClass
  #connecting to database
  def database_connection()
    db_connect()
    dbquery("select o.search_id,o.office_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
    @@search_id=dbresult[0]
  end
  #logining into Mifos
  def GroupOutOFKendra_login()
		start
		login($validname,$validpwd)
  end
  def check_link_CreateKendra()
    begin
    assert($ie.contains_text("Create new Kendra"))
    $logger.log_results("Link Check","Create New Member","Create New Member","Failed")
    rescue=>e
    $logger.log_results("Link Check","Create New Member","Create New Member","Passed")
    end
  end
  def click_clientsaccounts_link()
    begin
      $ie.link(:text,"Clients & Accounts").click
      assert($ie.contains_text("Clients & Accounts Tasks"))
      $logger.log_results("Link Clients & Accounts","should redirect proprely","Working","Passed")
      rescue=>e
      $logger.log_results("Link Clients & Accounts","should redirect proprely","Not Working","Failed")
    end
  end
  def check_link_createKendra_in_ClientsandAccounts_tab()
    begin
    assert($ie.contains_text("Create new Kendra"))
    $logger.log_results("Link Check","Create New Member","Create New Member","Failed")
    rescue=>e
    $logger.log_results("Link Check","Create New Member","Create New Member","Passed")
    end
  end
  def click_create_new_group()
    begin
    $ie.link(:text,"Create new Group").click
    assert($ie.contains_text($select_branch_message))
    $logger.log_results("Link Create new Group","Should work properly","Working","Passed")
    rescue=>e
    $logger.log_results("Link Create new Group","Should work properly","Not Working","Failed")
    end
  end
  def checking_the_offices_in_select_branch_page()
    dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and search_id like '"+@@search_id+"%'")
    rowcount=0
    if $row==0 then
      $logger.log_results("No Branc offices exists","N/A","N/A","passed")
    else
    while rowcount<$row
    display_name=dbresult[1]
    check_branch(display_name)
    get_next_data
    rowcount+=1
    end
    end
  end
  def check_branch(display_name)
    begin
    assert($ie.contains_text(display_name))
    $logger.log_results("Branch Name",String(display_name),"Existed","Passed")
    rescue=>e
    $logger.log_results("Branch Name",String(display_name),"Existed","Failed")    
    end
  end
  def select_branch()
    begin
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      display_name=dbresult[1]
      
      $ie.link(:text,display_name).click
      assert($ie.contains_text($group_select_group))
      $logger.log_results("Link",String(display_name),"Working","passed")
      rescue=>e
      $logger.log_results("Link",String(display_name),"Not Working","Failed")
    end
  end
  def fields_checking()
  begin
      assert($ie.contains_text($group_details)) and assert($ie.contains_text($g_name)) and //
      assert($ie.contains_text($l_officer)) and assert($ie.contains_text($m_schedule)) and assert($ie.contains_text($trained)) and //
      assert($ie.contains_text($trained_on))and assert($ie.contains_text($e_id))and assert($ie.contains_text($address_gc)) and //
      assert($ie.contains_text($add_information))and assert($ie.contains_text($adm_fee))and assert($ie.contains_text($add_fee))
      $logger.log_results("data","Should display properly","displaying","passed")
      rescue=>e
      $logger.log_results("data","Should display properly","displaying","failed")
  end
  end
  def check_group_with_mandatory(gname,customfield,status)
    begin
     if ($validname=="mifos") then
     dbquery("select personnel_id,display_name from personnel where office_id="+@@office_id)
     @@personnel_id=dbresult[0]
     @@loan_officer=dbresult[1]
     else
     dbquery("select personnel_id,display_name from personnel where login_name='"+$validname+"'")
     @@personnel_id=dbresult[0]
     @@loan_officer=dbresult[1]
     end
     $ie.text_field(:name,"displayName").set(gname)
     $ie.text_field(:name,"customField[0].fieldValue").set(customfield)
     $ie.select_list(:name,"customerFormedById").select_value(@@personnel_id)
     $ie.button(:value,"Preview").click 
     assert($ie.contains_text($group_review)) 
     $logger.log_results("mandatory checks when group name,Formed by,additional information entered","NA","NA","passed")
     rescue=>e
     $logger.log_results("mandatory checks when group name,Formed by,additional information entered","NA","NA","failed")
    end
  end
  def create_group_with_mandatory_out_of_center(gname,customfield,status)
   begin
    click_clientsaccounts_link()
    click_create_new_group()
    select_branch()
    check_group_with_mandatory(gname,customfield,status)
    $ie.button(:value,status).click
    assert($ie.contains_text($group_success))
    $logger.log_results("Group created successfully","N/A","N/A","Passed")
    rescue=>e
    $logger.log_results("Group created successfully","N/A","N/A","Failed")
    end
  end
  def db_check(gname)
    db_center=$dbh.real_query("select * from customer where display_name='"+gname+"' and loan_officer_id="+@@personnel_id+" and branch_id="+@@office_id)
    rowcount= db_center.num_rows
    if rowcount==0 then
    $logger.log_results("Data Base Check","NA","NA","failed") 
    else
    $logger.log_results("Data Base Check","NA","NA","passed")  
    end   
  end
  def go_to_detailspage(gname)
  begin
    $ie.link(:text,"View Group details now").click
    assert($ie.contains_text("Edit Group status"))
    $logger.log_results("Opened Edit group details page","N/A","N/A","passed")
    rescue=>e
    $logger.log_results("Opened Edit group details page","N/A","N/A","passed")
  end
  end
  def check_link_edit_office_membership()
    begin
      assert($ie.contains_text("Edit office membership"))
      $logger.log_results("Edit office membership","Should Exist","Existed","Passed")
      rescue=>e
      $logger.log_results("Edit office membership","Should Exist","Not Existed","Failed")
    end
  end
  def click_link_edit_office_membership()
    begin
      $ie.link(:text,"Edit office membership").click
      assret($ie.contains_text("Choose Branch Office - To transfer Group"))
      $logger.log_results("Edit Office membership","Should redirect properly","redirecting","Passed")
      rescue=>e
      $logger.log_results("Edit Office membership","Should redirect properly","Not redirecting","Failed")
    end
  end
  def change_office_membership()
    begin
    dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and office_id not in ("+@@office_id+") and search_id like '"+@@search_id+"%'")
    display_name=dbresult[1]
    $ie.link(:text,display_name).click
    assert($ie.contains_text("Edit office membership - Confirm"))
    $logger.log_results("Edit branch membershilp","Should redirect to confirm page","redirected","passed")
    rescue=>e
    $logger.log_results("Edit branch membershilp","Should redirect to confirm page","Not redirected","failed")
    end
  end
  def change_office_membership_submit()
    begin
      $ie.button(:value,"Submit").click
      assert($ie.contains_text("Account information"))
      $logger.log_results("Edit branch membership","changed the membership","N/A","Passed")
      rescue=>e
      $logger.log_results("Edit branch membership","not changed the membership","N/A","Failed")
    end
  end
end
