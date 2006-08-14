#this class calls all the testcases from the CenterCreateEdit class
require 'watir'
require 'modules/common/TestClass'
require 'modules/common/inputs'
require 'modules/Centers/CenterCreateEdit'

class CenterTest
  centerobject=CenterCreateEdit.new
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  centerobject.open(filename,1)
  rowid=-1
  colid=1
  centerobject.database_connection
  centerobject.center_login
  centerobject.check_center
  centerobject.select_office
  centerobject.mandatory_all
  centerobject.mandatory_with_centername
  centerobject.mandatory_with_cname_addInformation
  centerobject.mandatory_with_cname_addInformation_LO
  centerobject.meeting
  while(rowid<$maxrow*$maxcol-1)
  centername=centerobject.arrval[rowid+=1]
  external_id=centerobject.arrval[rowid+=1].to_i.to_s
  mfi_date=centerobject.arrval[rowid+=1].to_i.to_s
  mfi_month=centerobject.arrval[rowid+=1].to_i.to_s
  mfi_year=centerobject.arrval[rowid+=1].to_i.to_s
  address1=centerobject.arrval[rowid+=1]
  address2=centerobject.arrval[rowid+=1]
  address3=centerobject.arrval[rowid+=1]
  city=centerobject.arrval[rowid+=1]
  state=centerobject.arrval[rowid+=1]
  country=centerobject.arrval[rowid+=1]
  postal_code=centerobject.arrval[rowid+=1].to_i.to_s
  telephone=centerobject.arrval[rowid+=1].to_i.to_s
  custom1=centerobject.arrval[rowid+=1].to_i.to_s
  frequncymeeting=centerobject.arrval[rowid+=1].to_i.to_s
  monthtype=centerobject.arrval[rowid+=1].to_i.to_s
  reccurweek=centerobject.arrval[rowid+=1].to_i.to_s
  weekweekday=centerobject.arrval[rowid+=1].to_i.to_s
  monthday=centerobject.arrval[rowid+=1].to_i.to_s
  monthmonth=centerobject.arrval[rowid+=1].to_i.to_s
  monthrank=centerobject.arrval[rowid+=1].to_i.to_s
  monthweek=centerobject.arrval[rowid+=1].to_i.to_s
  monthmonthrank=centerobject.arrval[rowid+=1].to_i.to_s
  meetingplace=centerobject.arrval[rowid+=1]
  flag=centerobject.arrval[rowid+=1].to_i.to_s
  centerobject.check_center
  centerobject.select_office
  centerobject.center_create_test_duplicate(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
  if flag=="1" then
  centerobject.edit_center_preview(centername,external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone,custom1,frequncymeeting,monthtype,reccurweek,weekweekday,monthday,monthmonth,monthrank,monthweek,monthmonthrank,meetingplace)
  elsif flag=="0" then
  centerobject.center_submit()
  end
  centerobject.db_check(centername)
  centerobject.edit_center_details(external_id,mfi_date,mfi_month,mfi_year,address1,address2,address3,city,state,country,postal_code,telephone, custom1)
  centerobject.edit_status(centername)
  centerobject.view_all_closed_accounts
  centerobject.view_change_log
  end
  centerobject.mifos_logout()
end
