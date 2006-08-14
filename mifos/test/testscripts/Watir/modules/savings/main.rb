
#this class calls all the testcases from the savings class
#Passes the values from the Excel file
require 'mysql'
require 'test/unit'
require 'watir'
require 'Savings'
class Main 
	objnew=Savings.new
	filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
	objnew.open(filename,1)
	rowid=-1
	colid=1
	objnew.savings_login
	while(rowid<$maxrow*$maxcol-1)
	ammount=objnew.arrval[rowid+=1].to_i.to_s
	cammount=objnew.arrval[rowid+=1].to_i.to_s
	typeid=objnew.arrval[rowid+=1].to_i.to_s
	activeid=objnew.arrval[rowid+=1].to_i.to_s
	status=objnew.arrval[rowid+=1]
	nammount=objnew.arrval[rowid+=1].to_i.to_s
	validationammount=objnew.arrval[rowid+=1].to_i.to_s
	objnew.check_savings
    objnew.click_savings
	objnew.select_client_mandatory
	objnew.select_client(typeid,activeid)
	objnew.select_savings_man
	objnew.select_savings
	objnew.mandatory_check()
	objnew.validate_additional_field(validationammount)
	objnew.preview_ammount(ammount)
	objnew.edit_frompreview(cammount)
	objnew.submit_data(status)
	objnew.database_check
	objnew.savings_detailspage
	objnew.edit_savingsDetails(nammount)
	objnew.viewallAccount
	objnew.ViewTransaction
    objnew.statusChange
    
	end
	
end
