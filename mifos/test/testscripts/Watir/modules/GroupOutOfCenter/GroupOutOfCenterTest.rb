require 'watir'
require 'modules/common/TestClass'
require 'modules/common/inputs'
require 'modules/GroupOutOfCenter/GroupOutOfCenter'
require 'modules/Groups/GroupCreateEdit'
class GroupOutOfCenterTest
  groupoutofcenterobject=GroupOutOfCenter.new
  groupobject=GroupCreateEdit.new
  groupoutofcenterobject.database_connection()
  groupoutofcenterobject.GroupOutOFKendra_login
  groupoutofcenterobject.check_link_CreateKendra()
  groupoutofcenterobject.click_clientsaccounts_link()
  groupoutofcenterobject.check_link_createKendra_in_ClientsandAccounts_tab
  groupoutofcenterobject.click_create_new_group()
  groupoutofcenterobject.checking_the_offices_in_select_branch_page()
  groupoutofcenterobject.select_branch()
  groupoutofcenterobject.fields_checking()
  groupobject.mandatory_all
  groupobject.mandatory_with_groupname
  groupobject.mandatory_with_gname_addInformation
  filename=File.join(File.dirname($PROGRAM_NAME),"data/testdata.xls")
  groupoutofcenterobject.open(filename,1)
  rowid=-1
  while(rowid<$maxrow*$maxcol-1)
  groupname=groupoutofcenterobject.arrval[rowid+=1]
  customfield=groupoutofcenterobject.arrval[rowid+=1].to_i.to_s
  statusname=groupoutofcenterobject.arrval[rowid+=1]
  if statusname=="partial" then
  status="Save for later"
  elsif statusname=="pending"
  status="Submit for approval"
  end
  groupoutofcenterobject.click_clientsaccounts_link()
  groupoutofcenterobject.click_create_new_group()
  groupoutofcenterobject.select_branch()
  groupoutofcenterobject.check_group_with_mandatory(groupname,customfield,status)
  groupobject.review_cancel
  groupoutofcenterobject.create_group_with_mandatory_out_of_center(groupname,customfield,status)
  groupoutofcenterobject.db_check(groupname)
  groupoutofcenterobject.go_to_detailspage(groupname)
  #groupobject.change_status(groupname)
  groupobject.view_all_closed_accounts()
  groupoutofcenterobject.check_link_edit_office_membership
  groupoutofcenterobject.click_link_edit_office_membership
  groupoutofcenterobject.change_office_membership
  groupoutofcenterobject.change_office_membership_submit
end
end
