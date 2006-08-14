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
class Feeclass<TestClass
    #logging into Mifos
    def login_fee()
        start
		login($validname,$validpwd)
		db_connect()
    end
    #admin link check
    def admin_check()
      begin
      assert($ie.contains_text("Admin"))
      $logger.log_results("Admin Link","Should dissplay","Displaying","Passed")
      $ie.link(:text,"Admin").click
      rescue=>e
      $logger.log_results("Admin Link","Should dissplay","Not Displaying","Failed")
      end
    end
    #ckecking for link define new fee
	def feelink_check()
		begin
		assert($ie.contains_text("Define new fees"))
		$logger.log_results("link","Define new fees","exists","passed")
		$ie.link(:text,"Define new fees").click
		rescue=>e
		$logger.log_results("error ","could not find Define new fees","NA","failed")
		$ie.link(:text,"Logout").click
	    end
	end
	#checking for all mandatory fileds
	def check_errormsg_all()
		begin
		$ie.button(:value,'Preview').click
		assert($ie.contains_text($f_m_nam_e)) and assert($ie.contains_text($f_m_amt_e)) and//
		assert($ie.contains_text($f_m_freq_oe)) and assert($ie.contains_text($f_m_cat_e)) and assert($ie.contains_text($gl_code))
		$logger.log_results("Mandatory check for all fields","empty values in all fields","error message","passed")
		rescue =>e
		$logger.log_results("Mandatory check for all/some fields","empty values in all fields","error message","failed")
		end
	end
	#checking for all mandatory except feename
	def check_errormsg_with_feename(feename)
		begin
		$ie.text_field(:name,'feeName').set(feename)
        strlen=feename.length
        $ie.button(:value,'Preview').click
		assert($ie.contains_text($f_m_amt_e))and assert($ie.contains_text($f_m_cat_e)) and assert($ie.contains_text($f_m_freq_oe) )and assert($ie.contains_text($gl_code))
		$logger.log_results("Input values in  feename",feename,"error message for other mandatory fields that are not filled ","passed")
		check_errormsg_len(strlen)
		rescue =>e
		$logger.log_results("Input values in  feename",feename,"error message for other mandatory fields that are not filled","failed")
		end
		
	end
	#checking the arror message length
	def check_errormsg_len(strlen)
			if (strlen>50) then
		begin
		assert($ie.contains_text($f_m_max_le))
			$logger.log_results("Mandatory check for characters greater than max length",strlen.to_s,"&lt;=50","passed")
		rescue =>e
			$logger.log_results("Mandatory check for characters greater than max length",strlen.to_s,"&lt;=50","failed")
		end		
		else
		$logger.log_results("check for max length less than max length",strlen.to_s,"&lt;=50","passed")
		end
	end
    #checking the mandatory conditions when feename and categor selected
	def check_errormsg_with_feename_catid(feename,catid)
		begin
		$ie.text_field(:name,'feeName').set(feename)
		$ie.select_list(:name,"categoryType").select(catid)
        $ie.button(:value,'Preview').click
		assert($ie.contains_text($f_m_freq_oe) ) and (assert($ie.contains_text($f_m_freq_ra)) or assert($ie.contains_text($f_m_amt_e)) )and assert($ie.contains_text($gl_code))
		$logger.log_results("Mandatory check for fields feename,fee_category",feename+","+catid,"error message for other mandatory fields that are not filled","passed")
		rescue Test::Unit::AssertionFailedError=>e
		$logger.log_results("Mandatory check for fields feename,fee_category",feename+","+catid,"error message for other mandatory fields that are not filled","failed")
		end
	end
	#checking the conditions when category id loan selected
	def fee_type_selected(catid,toc)
	   if catid=="Loans" then
	   if !($ie.checkbox(:name, "customerDefaultFee", "1").enabled?()) then
	   $logger.log_results("default fees checkbox for ",catid,"disabled","passed")
	   else
	   $logger.log_results("default fees checkbox for",catid,"disabled","failed")
	   end
	   $ie.select_list(:name,"loanCharge").select(toc)
       else
	   if ($ie.checkbox(:name, "customerDefaultFee", "1").enabled?()) then
	   $logger.log_results("default fees checkbox for",catid,"enabled","passed")
	   else
	   $logger.log_results("default fees checkbox for",catid,"enabled","failed")
	   end
	   $ie.select_list(:name,"loanCharge").select(toc)
    end
	end
    #checking for the mandatory when Loan category selected and entered loan payment id and other paymentid
	def check_errormsg_loan_field(feename,catid)
        $ie.button(:value,'Preview').click
		if catid!="Loans" then
		begin
		assert($ie.contains_text($f_m_amt_e)) 
		$logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","passed")
		rescue =>e
		$logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","failed")
		end
		else
		begin
		assert($ie.contains_text($f_m_freq_ra))
		$logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","passed")
		rescue =>e
		$logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","failed")
		end
		end
	end
    #checking the mandatory when you selct type id also
	def check_errormsg_with_type_id()
	    $ie.radio(:name,"feeFrequencyType","1").set
        $ie.button(:value,'Preview').click
        $logger.log_message("__FEENAME,FEECATEGORY,PERIODIC FEE FREQUENCY ENTERED,OTHERS BLANK__")
		begin
		assert($ie.contains_text($f_m_freq_we)) and (assert($ie.contains_text($f_m_amt_e)) or assert($ie.contains_text($f_m_freq_ra))) and assert($ie.contains_text($gl_code))
		$logger.log_results("Mandatory check for fields feename,fee_category,periodic fee_freq","feename.fee_category,periodic fee_frequency","error message for other mandatory fields not filled","passed")
		rescue =>e
		$logger.log_results("Mandatory check for fields feename,fee_category,periodic fee_freq","feename.fee_category,periodic fee_frequency","error message for other mandatory fields not filled","failed")
		end
	end
    #checking error message when week periodicity selected
	def check_weekperiodicy_errormsg()
	    $logger.log_message("__CHECK FOR PERIODICITY WITHOUT WEEK OPTION__")
	    $ie.radio(:name,"feeRecurrenceType","1").set
        $ie.button(:value,'Preview').click
		begin
		 assert($ie.contains_text($f_m_freq_we)) and  (assert($ie.contains_text($f_m_freq_ra)) or assert($ie.contains_text($f_m_amt_e)) )and assert($ie.contains_text($gl_code))
		$logger.log_results("Mandatory check for field periodic without week option","NA","NA","passed")
		rescue =>e
		$logger.log_results("Mandatory check for field periodic without week option","NA","NA","failed")
		end
	end
    #checking error message when month periodicity selected
	def check_monthperiodicy_errormsg()
        $logger.log_message("__CHECK FOR PERIODICITY WITHOUT MONTH OPTION__")
        $ie.radio(:name,"feeRecurrenceType","2").set
        $ie.button(:value,'Preview').click
		begin
		assert($ie.contains_text($f_m_freq_me)) and  (assert($ie.contains_text($f_m_freq_ra)) or assert($ie.contains_text($f_m_amt_e)) )and assert($ie.contains_text($gl_code))
		$logger.log_results("Mandatory check for field periodic without month checked","NA","NA","passed")
		rescue =>e
		$logger.log_results("Mandatory check for field periodic without month checked","NA","NA","failed")
		end
	end
	#checking the conditions when loan category id is selected
	def fee_category_loan(catid,amount,formula_id)
	 if catid=="Loans" then
     $ie.text_field(:name,"rate").set(amount)
     $ie.button(:value,'Preview').click
     check_errormsg_rate()
     $ie.select_list(:name,"feeFormula").select_value(formula_id)
     $ie.button(:value,'Preview').click
     check_errormsg_maxrate(amount)
     $ie.text_field(:name,"rate").clear
     end
    end
    
    #MANDATORY CHECK FOR RATE WITHOUT A FORMULA
	def check_errormsg_rate()
        $logger.log_message("__MANDATORY CHECK FOR RATE WITHOUT A FORMULA__")
		begin
		assert($ie.contains_text($f_m_rate_formula) )
		$logger.log_results("Mandatory check for rate without a formula ","NA","NA","passed")
		rescue =>e
		$logger.log_results("Mandatory check for rate without a formula ","NA","NA","failed")
		end
	end
    #CHECK FOR MESSAGE WHEN RATE IS GREATER THAN 999 %
	def check_errormsg_maxrate(amount)
        $logger.log_message("__CHECK FOR MESSAGE WHEN RATE IS GREATER THAN 999 %__")
		if (amount.to_f >999) then
		begin
		assert($ie.contains_text($f_m_rate_and_formula) ) 
		$logger.log_results("Mandatory check for rate greater than 999 with formula ",amount.to_f.to_s,"&lt;=999","passed")
		rescue =>e
		$logger.log_results("Mandatory check for rate greater than 999 with formula ",amount.to_f.to_s,"&lt;=999","failed")
		end
		else
		$logger.log_results("check for rate less than 999 with formula ",amount.to_f.to_s,"&lt;=999","passed")
		end
	end
    #CHECK WHEN AMOUNT ENTERED IS GREATER THAN OR LESS THAN  99999999999.99
	def errormsg_maxamt_P(amount)
        $logger.log_message("__CHECK WHEN AMOUNT ENTERED IS GREATER THAN OR LESS THAN  99999999999.99__")
        $ie.text_field(:name,"amount").set(amount)
        $ie.button(:value,'Preview').click
		if (amount.to_f >99999999999.99) then
		begin
		assert($ie.contains_text($f_m_max_amt)) 
		$logger.log_results("mandatory check for amount greater than max amt value for periodic",amount.to_f.to_s,"&lt;=99999999999.99","passed")
		rescue =>e
		$logger.log_results("mandatory check for amount greater than max amt value for periodic",amount.to_f.to_s,"&lt;=99999999999.99","failed")
		end
		else
		$logger.log_results("check for amount less than max amt value for periodic",amount.to_f.to_s,"&lt;=99999999999.99","passed")
		end
	end
	#checking the conditions when category id is not Loan
	def not_for_loan(catid,toc,amount)
      $ie.radio(:name,"feeFrequencyType","2").set
      if catid!="Loans" then
        $ie.select_list(:name,"loanCharge").select(toc)
        $ie.text_field(:name,"amount").set(amount)
      else
        $ie.select_list(:name,"loanCharge").select(toc)
        $ie.text_field(:name,"rate").set(amount)
      end
    end
    #error meessage when ammount eneterd is maximum
	def errormsg_maxamt_O(amount)
    	$ie.button(:value,'Preview').click
    	if (amount.to_f >99999999999.99) then
		begin
		assert($ie.contains_text($f_m_max_amt)) 
		$logger.log_results("Mandatory check for amount greater than max amt value for onetime",amount.to_f.to_s,"&lt;=99999999999.99","passed")
		rescue =>e
		$logger.log_results("mandatory check for amount greater than max amt value for onetime",amount.to_f.to_s,"&lt;=99999999999.99","failed")
		end
		else
		$logger.log_results("check for amount less than max amt value for onetime",amount.to_f.to_s,"&lt;=99999999999.99","passed")
		end
	end
	
	#creation of fee with all data
	def fee_create(feename,catid,def_fee,toc,freq,week,month,amt_rate,amount,formula,glcode)
	    admin_check()
	    feelink_check()
		$ie.text_field(:name,'feeName').set(feename)
		if ($ie.checkbox(:name, "customerDefaultFee", "1").enabled?() and (catid!="Loans" and def_fee=="Yes") )then
    	  $ie.checkbox(:name, "customerDefaultFee", "1").set
		elsif  (catid=="Loans" and ($ie.checkbox(:name, "customerDefaultFee", "1").enabled?())) then
	      $ie.checkbox(:name, "customerDefaultFee", "1").clear
		end
		$ie.select_list(:name,"categoryType").select(catid)
		if ((catid.to_s!="Loans") and (freq.to_s=="One Time")) then
		  $ie.radio(:name,"feeFrequencyType","2").set
		  $ie.select_list(:name,"customerCharge").select(toc)
		elsif ( (catid.to_s=="Loans" ) and freq.to_s=="One Time")then
  		  $ie.radio(:name,"feeFrequencyType","2").set
		  $ie.select_list(:name,"loanCharge").select(toc)
		else
		  $ie.radio(:name,"feeFrequencyType","1").set
  		    if (week)=="0"  
    		  $ie.radio(:name,"feeRecurrenceType","2").set
	     	  $ie.text_field(:name,"monthRecurAfter").set(month)
		    else
		      $ie.radio(:name,"feeRecurrenceType","1").set
		      $ie.text_field(:name,"weekRecurAfter").set(week)
		    end
		end  
		
		if catid!="Loans" then 
		  $ie.text_field(:name,"amount").clear()
		  $ie.text_field(:name,"amount").set(amount)
		elsif ((catid=="Loans") and (amt_rate=="0")) then
		  $ie.text_field(:name,"amount").clear()
		  $ie.text_field(:name,"amount").set(amount)
		else
		  $ie.text_field(:name,"amount").clear()
		  $ie.text_field(:name,"rate").set(amount)
		  $ie.select_list(:name,"feeFormula").select_value(formula)
		end
		$ie.select_list(:name,"glCode").select(glcode)
		$ie.button(:value,'Preview').click
	end
	#clicking on submit button
	def click_submit()
	 begin
	 $ie.button(:value,"Submit").click
	 assert($ie.contains_text($fee_success))
     $logger.log_results("In fee successfully created page for feename ","NA","NA","passed")
     rescue=>e
     $logger.log_results("successful defined a new fee page did not appear","NA","NA","failed")
	 end
	end
	#database check for the fee created
	def dbcheck(feename)
		search_res=$dbh.real_query("Select * from fees where fee_name='"+feename+"'")
		rowcount=search_res.num_rows
		if rowcount==0 then
          $logger.log_results("Data Base Check","NA","NA","failed") 
        else
          $logger.log_results("Data Base Check","NA","NA","passed")  
        end   
	end
	
	#checking that whether fee created successfull
	def edit_fee_from_success(feename)
	 begin
	 puts "edit_fee_from_success"
	 $ie.link(:text,"View fee details now").click
	 assert($ie.contains_text(feename))and assert($ie.contains_text("Edit fee information"))
	 $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","passed")
	 edit_fee_information(feename)
	 rescue=>e
	 $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","failed")
	 end
	end
	
	#edit fee information from success page
	def edit_fee_information(feename)
	 begin
	 $ie.link(:text,"Edit fee information").click
	 assert($ie.contains_text("Edit fee information"))
	 $logger.log_results("Page redirect to Edit Fee Details Page","Should redirect","redirected","passed")
	 edit_fee_information_cancel(feename)
	 rescue=>e
	 $logger.log_results("Page redirect to Edit Fee Details Page","Should redirect","redirected","Failed")
	 end
	end
	
	#checking the cancel functionality from fee details page 
	def edit_fee_information_cancel(feename)
	 begin
	 puts "edit_fee_information_cancel"
	 $ie.button(:value, "Cancel").click
	 assert($ie.contains_text(feename))and assert($ie.contains_text("Edit fee information"))
	 $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","passed")
	 rescue=>e
	 $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","failed")
	 end
	end
	#clicking on submit after editing the fee data
    def edit_fee_information_submit()
      $ie.link(:text,"Edit fee information").click
    end
    #status change conditions
    def status_change(feename)
      dbquery("select status from fees where fee_name='"+feename+"'")
      status_id=dbresult[0]
      if status_id=="1" then
      status_change_inactive(feename)
      elsif status_id=="2" then
      status_change_active(feename)
      end
    end
    #status change to active
    def status_change_active(feename)
      begin
      $ie.select_list(:name,"status").select_value("1")
      $ie.button(:value,"Preview").click
      assert($ie.contains_text(feename))and assert($ie.contains_text("Preview fee information"))
      $logger.log_results("Status Changed","Should redirect to Preview page","preview page","passed")
      $ie.button(:value,"Submit").click
      rescue=>e
      $logger.log_results("Status Changed","Should redirect to Preview page","N/A","failed")
      end
    end
    #status change to inactive
    def status_change_inactive(feename)
      begin
      $ie.select_list(:name,"status").select_value("2")
      $ie.button(:value,"Preview").click
      assert($ie.contains_text(feename))and assert($ie.contains_text("Preview fee information"))
      $logger.log_results("Status Changed","Should redirect to Preview page","preview page","passed")
      $ie.button(:value,"Submit").click
      rescue=>e
      $logger.log_results("Status Changed","Should redirect to Preview page","N/A","failed")
      end
    end
    #verifying the View fee link
    def verify_viewfeelink()
      begin
        $ie.link(:text,"Admin").click
        assert($ie.contains_text("View fees"))
        $logger.log_results("View fee link","Should display","Displaying","Passed")
        rescue=>e
        $logger.log_results("View fee link","Should display","Not Displaying","Failed")
      end
    end
    #clicking on the View Fee link
    def click_viewfeelink()
      begin
        $ie.link(:text,"View fees").click
        assert($ie.contains_text("Click on a fee below to view details and make changes or define a new fee"))
        $logger.log_results("View fee link","should work","working","Passed")
        rescue=>e
        $logger.log_results("View fee link","should work","Not working","Failed")
      end
    end
    #select and click on one fee
    def select_click_fee()
      begin
      dbquery("select fee_name from fees where status=1")
      feename=dbresult[0]
      $ie.link(:text,feename).click
      assert($ie.contains_text(feename))and assert($ie.contains_text("Edit fee information"))
	  $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","passed")
	  edit_fee_information(feename)
	  edit_fee_information_submit()
	  status_change(feename)
	  rescue=>e
	  $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","failed")
      end
    end
end