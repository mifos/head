require 'watir'
require 'modules/common/TestClass'
require 'modules/common/inputs'
require 'modules/Groups/GroupCreateEdit'
class GroupTest
groupobject=GroupCreateEdit.new
groupobject.group_login
groupobject.database_connection
groupobject.check_group
groupobject.select_center_check
groupobject.select_center_cancel
groupobject.select_center
groupobject.mandatory_all
groupobject.mandatory_with_groupname
groupobject.mandatory_with_gname_addInformation
filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
groupobject.open(filename,1)
rowid=-1
while(rowid<$maxrow*$maxcol-1)
groupname=groupobject.arrval[rowid+=1]
customfield=groupobject.arrval[rowid+=1].to_i.to_s
statusname=groupobject.arrval[rowid+=1]
if statusname=="partial" then
status="Save for later"
elsif statusname=="pending"
status="Submit for approval"
end
groupobject.review_group_with_mandatory(groupname,customfield,status)
groupobject.review_cancel
groupobject.create_group_with_mandatory(groupname,customfield,status)
groupobject.db_check(groupname)
end
groupobject.open(filename,2)
rowid=-1
while(rowid<$maxrow*$maxcol-1)
  gname=groupobject.arrval[rowid+=1]
  gdate=groupobject.arrval[rowid+=1].to_i.to_s
  gmonth=groupobject.arrval[rowid+=1].to_i.to_s
  gyear=groupobject.arrval[rowid+=1].to_i.to_s
  externalid=groupobject.arrval[rowid+=1]
  address1=groupobject.arrval[rowid+=1]
  address2=groupobject.arrval[rowid+=1]
  address3=groupobject.arrval[rowid+=1]
  city=groupobject.arrval[rowid+=1]
  state=groupobject.arrval[rowid+=1]
  country=groupobject.arrval[rowid+=1]
  pcode=groupobject.arrval[rowid+=1].to_i.to_s
  phone=groupobject.arrval[rowid+=1].to_i.to_s
  custom1=groupobject.arrval[rowid+=1].to_i.to_s
  statusname=groupobject.arrval[rowid+=1]
  edit_gname=groupobject.arrval[rowid+=1]
  edit_externalid=groupobject.arrval[rowid+=1]
  edit_address1=groupobject.arrval[rowid+=1]
  edit_address2=groupobject.arrval[rowid+=1]
  edit_address3=groupobject.arrval[rowid+=1]
  edit_city=groupobject.arrval[rowid+=1]
  edit_state=groupobject.arrval[rowid+=1]
  edit_country=groupobject.arrval[rowid+=1]
  edit_pcode=groupobject.arrval[rowid+=1].to_i.to_s
  edit_phone=groupobject.arrval[rowid+=1].to_i.to_s
  edit_custom1=groupobject.arrval[rowid+=1].to_i.to_s
  if statusname=="partial" then
  status="Save for later"
  elsif statusname=="pending"
  status="Submit for approval"
  end
  groupobject.select_center 
  groupobject.create_group_all_data(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
  groupobject.edit_group_preview(gname,gdate,gmonth,gyear,externalid,address1,address2,address3,city,state,country,pcode,phone,custom1,status) 
  groupobject.db_check(gname)
  groupobject.edit_group_data(gname)
  groupobject.edit_group_from_details_link
  groupobject.edit_group_from_details(edit_gname,edit_externalid,edit_address1,edit_address2,edit_address3,edit_city,edit_state,edit_country,edit_pcode,edit_phone,edit_custom1)  
  groupobject.view_all_closed_accounts
  groupobject.edit_center_membership
  groupobject.edit_center_membership_select_center
  
 
  end
 #groupobject.mifos_logout()
end
