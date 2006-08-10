require 'mysql'
require 'test/unit'
require 'watir'
require 'Feeclass'

class Fee_Test
  feeobject=Feeclass.new
  feeobject.login_fee()
  feeobject.admin_check()
  feeobject.feelink_check()
  
  filename=File.join(File.dirname($PROGRAM_NAME),"data/testdata.xls")
  feeobject.open(filename,1)
  rowid= -1
  
  while (rowid<$maxrow*$maxcol-1)
    feename=feeobject.arrval[rowid+=1].to_s
    catid=feeobject.arrval[rowid+=1].to_s
    toc=feeobject.arrval[rowid+=1].to_s
    amount=feeobject.arrval[rowid+=1].to_s
    formula_id=feeobject.arrval[rowid+=1].to_i.to_s
    
    feeobject.admin_check()
    feeobject.feelink_check()
    
    feeobject.check_errormsg_all()
    feeobject.check_errormsg_with_feename(feename)
    feeobject.check_errormsg_with_feename_catid(feename,catid)
    feeobject.fee_type_selected(catid,toc)
    feeobject.check_errormsg_loan_field(feename,catid)
    feeobject.check_errormsg_with_type_id()
    feeobject.check_weekperiodicy_errormsg()
    feeobject.check_monthperiodicy_errormsg()
    feeobject.fee_category_loan(catid,amount,formula_id)
    feeobject.errormsg_maxamt_P(amount)
    feeobject.not_for_loan(catid,toc,amount)
    feeobject.errormsg_maxamt_O(amount)    
  end
  
  feeobject.open(filename,2)
  rowid=-1
  while (rowid<$maxrow*$maxcol-1)
    feename=feeobject.arrval[rowid+=1]
  	catid=feeobject.arrval[rowid+=1]
  	def_fee=feeobject.arrval[rowid+=1]
  	freq=feeobject.arrval[rowid+=1]
  	toc=feeobject.arrval[rowid+=1]
  	week=feeobject.arrval[rowid+=1].to_i.to_s
  	month=feeobject.arrval[rowid+=1].to_i.to_s
  	amt_rate=feeobject.arrval[rowid+=1].to_i.to_s
  	formula=feeobject.arrval[rowid+=1].to_i.to_s
  	amount=feeobject.arrval[rowid+=1].to_s
  	change_applies_to=feeobject.arrval[rowid+=1]
  	newamount=feeobject.arrval[rowid+=1].to_s
  	status=feeobject.arrval[rowid+=1].to_i.to_s
  	glcode=feeobject.arrval[rowid+=1].to_i.to_s
  	 

  	feeobject.fee_create(feename, catid, def_fee,toc,freq,week,month,amt_rate,amount,formula,glcode)

  	feeobject.click_submit()
  	
  	feeobject.dbcheck(feename)
  	feeobject.edit_fee_from_success(feename)
  	feeobject.edit_fee_information_submit()
  	puts "After edit_fee_information_submit"  	
  	feeobject.status_change(feename)
  end
  
  feeobject.verify_viewfeelink()
  feeobject.click_viewfeelink()
  feeobject.select_click_fee()
  feeobject.mifos_logout()
end
