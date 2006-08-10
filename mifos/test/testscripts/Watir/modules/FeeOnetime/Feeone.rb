#this class calls all the testcases from the FeeOntime class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/FeeOnetime/FeeOnetime'
class Feeone
  feeobj=FeeOnetime.new
  feeobj.fee_login
  feeobj.man_search_account
  filename=File.join(File.dirname($PROGRAM_NAME),"data/testdata.xls")
  feeobj.open(filename,1)
  rowid=-1
  while(rowid<$maxrow*$maxcol-1)
  feetype=feeobj.arrval[rowid+=1].to_i.to_s
  feeobj.search_account(feetype)
  feeobj.click_account
  feeobj.check_applycharges
  feeobj.click_applycharges
  feeobj.add_fee_man
  feeobj.click_cancel
  feeobj.add_fee
  feeobj.db_check
  end
  $ie.link(:text,"Logout").click
end
