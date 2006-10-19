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

class Feeclass<TestClass
  
  #read value from the xls file
  def read_values_from_file(rowid,sheetid)
    if sheetid==1
      @feename=arrval[rowid+=1].to_s
      @catid=arrval[rowid+=1].to_s
      @toc=arrval[rowid+=1].to_s
      @amount=arrval[rowid+=1].to_s
      @formula_id=arrval[rowid+=1].to_i.to_s
    else
      @feename=arrval[rowid+=1]
      @catid=arrval[rowid+=1]
      @def_fee=arrval[rowid+=1]
      @freq=arrval[rowid+=1]
      @toc=arrval[rowid+=1].to_s
      @week=arrval[rowid+=1].to_i.to_s
      @month=arrval[rowid+=1].to_i.to_s
      @amt_rate=arrval[rowid+=1].to_i.to_s
      @formula_id=arrval[rowid+=1].to_i.to_s
      @amount=arrval[rowid+=1].to_s
      @change_applies_to=arrval[rowid+=1]
      @newamount=arrval[rowid+=1].to_s
      @status=arrval[rowid+=1].to_i.to_s
      @glcode=arrval[rowid+=1].to_i.to_s
    end
  
  end
  
  def Feename
    @feename
  end
  def Category
    @catid
  end
  def Timeofcharge
    @toc
  end
  def Amount
    @amount
  end
  def Formula_id
    @formula_id
  end
  def Defaultfees
    @def_fee
  end
  def Frequency
    @freq
  end
  def Week
    @week
  end
  def Month
    @month
  end
  def Amt_rate
    @amt_rate
  end
  def Change_applies_to
    @change_applies_to
  end  
  def Newamount
    @newamount
  end
  def Status
    @status
  end
  def Glcode
    @glcode
  end 
  
  
  #read values from properties file into a hash
  def read_from_properties_file()
    begin
      @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
      @@feeprop=load_properties("modules/propertis/FeesUIResources.properties")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #read values from the hash into variables
  def read_values_from_hash()
    begin
      #labels
      @admin_label=@@feeprop['Fees.admin']
      @view_fees_page_label=@@feeprop['Fees.ViewFeesInstruction'].squeeze(" ")+" "+@@feeprop['Fees.smalldefinenewfee']
      #links
      @define_new_fees_link=@@adminprop['admin.defnewfees']
      @view_fees_link=@@adminprop['admin.viewfees']
      @view_fees_detail_link=@@feeprop['Fees.viewfeedetail']
      @edit_fees_link=@@feeprop['Fees.edit']
      #buttons
      @preview_button=@@feeprop['Fees.preview']
      @submit_button=@@feeprop['Fees.submit']
      @cancel_button=@@feeprop['Fees.cancel']
      #error msg's
      @mandatory_feeName_msg=string_replace_message(@@feeprop['errors.mandatory_textbox'],"{0}",@@feeprop['Fees.mandatory_feename'])
      @mandatory_amount_msg=string_replace_message(@@feeprop['errors.mandatory_textbox'],"{0}",@@feeprop['Fees.error.amount'])
      @mandatory_freq_msg=@@feeprop['error.ontimeandpaymentid']
      @mandatory_fee_appliesto_msg=@@feeprop['errors.mandatory_feescategory']
      @mandatory_glcode_msg=string_replace_message(@@feeprop['errors.mandatory_selectbox'],"{0}",@@feeprop['Fees.mandatory_glCode'])
      @mandatory_rate_or_amount_msg=@@feeprop['errors.amountOrRate']
      @mandatory_recur_week_msg=@@feeprop['error.reurweek']
      @mandatory_recur_month_msg=@@feeprop['error.reurmonth']
      @mandatory_rateformula_msg=@@feeprop['errors.rateAndFormulaId']
      @fee_success_msg=@@feeprop['Fees.successmessage']
      #maxlength error msg
      @maxlen_feename_msg=string_replace_message(@@feeprop['errors.maximumlength'],"{0}",@@feeprop['Fees.mandatory_feename'])
      @maxlen_feename_msg=string_replace_message(@maxlen_feename_msg,"{1}","50")
      @maxlen_rate_msg=string_replace_message(@@feeprop['errors.rateGreaterThen999'],"{0}",@@feeprop['Fees.error.rate'])
      @maxlen_amount_msg=string_replace_message(@@feeprop['errors.amountGreaterThan'],"{0}",@@feeprop['Fees.error.amount'])
      @invalid_recurrencepattern_msg=@@feeprop['errors.Meeting.invalidRecurAfter'] 
    rescue =>excp
        quit_on_error(excp)
    end
  end
  
  #read labels from db
  def read_labels_from_db()
    begin
      dbquery("select lookup_value from lookup_value_locale where lookup_id=559 and locale_id=1")
      @onetime_label=dbresult[0]
      dbquery("select lookup_value from lookup_value_locale where lookup_id=86 and locale_id=1")
      @loans_label=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #logging into Mifos
  def login_fee()
    begin
      start
      login($validname,$validpwd)
      db_connect()
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #admin link check
  def admin_check()
    begin
      begin
        assert($ie.contains_text(@admin_label))
        $logger.log_results("Admin Link","Should display","Displaying","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Admin Link","Should display","Not Displaying","Failed")
      end
        $ie.link(:text,@admin_label).click
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #ckecking for link define new fee
  def feelink_check()
    begin
      assert($ie.link(:text,@define_new_fees_link).exists?())
      $logger.log_results("link",@define_new_fees_link,"exists","passed")
      $ie.link(:text,@define_new_fees_link).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("error ","could not find Define new fees","NA","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking for all mandatory fileds
  def check_errormsg_all()
    begin
      $ie.button(:value,@preview_button).click
      #puts "feename "+@mandatory_feeName_msg.to_s
      #puts "amount "+@mandatory_amount_msg.to_s
      #puts "frequency "+@mandatory_freq_msg.to_s
      #puts "fees applies to "+@mandatory_fee_appliesto_msg.to_s
      #puts "glcode "+@mandatory_glcode_msg.to_s
      assert($ie.contains_text(@mandatory_feeName_msg)) and \
      assert($ie.contains_text(@mandatory_amount_msg)) and \
      assert($ie.contains_text(@mandatory_freq_msg)) and \
      assert($ie.contains_text(@mandatory_fee_appliesto_msg)) and \
      assert($ie.contains_text(@mandatory_glcode_msg))
      $logger.log_results("Mandatory check for all fields","empty values in all fields","error message","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory check for all/some fields","empty values in all fields","error message","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking for all mandatory except feename
  def check_errormsg_with_feename(feename)
    begin
      $ie.text_field(:name,'feeName').set(feename)
      strlen=feename.length
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(@mandatory_amount_msg))and assert($ie.contains_text(@mandatory_fee_appliesto_msg)) and assert($ie.contains_text(@mandatory_freq_msg) )and assert($ie.contains_text(@mandatory_glcode_msg))
      $logger.log_results("Input values in  feename",feename,"error message for other mandatory fields that are not filled ","passed")
      check_errormsg_len(strlen)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Input values in  feename",feename,"error message for other mandatory fields that are not filled","failed")
    rescue =>excp
      quit_on_error(excp)
    end
    
  end
  
  #checking the arror message length
  def check_errormsg_len(strlen)
    begin
      if (strlen>50) then
        begin
          assert($ie.contains_text(@maxlen_feename_msg))
          $logger.log_results("Mandatory check for characters greater than max length",strlen.to_s,"&lt;=50","passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Mandatory check for characters greater than max length",strlen.to_s,"&lt;=50","failed")
        end		
      else
        $logger.log_results("check for max length less than max length",strlen.to_s,"&lt;=50","passed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end

  #checking the mandatory conditions when feename and categor selected
  def check_errormsg_with_feename_catid(feename,catid)
    begin
      #$ie.text_field(:name,'feeName').set(feename)
      $ie.select_list(:name,"categoryType").select(catid)
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(@mandatory_freq_msg) ) and (assert($ie.contains_text(@mandatory_rate_or_amount_msg)) or assert($ie.contains_text(@mandatory_amount_msg)) )and assert($ie.contains_text(@mandatory_glcode_msg))
      $logger.log_results("Mandatory check for fields feename,fee_category",feename+","+catid,"error message for other mandatory fields that are not filled","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory check for fields feename,fee_category",feename+","+catid,"error message for other mandatory fields that are not filled","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the conditions when category id loan selected
  def fee_type_selected(catid,toc)
    begin
      if catid==@loans_label then
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
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking for the mandatory when Loan category selected and entered loan payment id and other paymentid
  def check_errormsg_loan_field(feename,catid)
    begin
      $ie.button(:value,@preview_button).click
      if catid!=@loans_label then
        begin
          assert($ie.contains_text(@mandatory_amount_msg)) 
          $logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","failed")
        end
      else
        begin
          assert($ie.contains_text(@mandatory_rate_or_amount_msg))
          $logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Mandatory check for fields feename,fee_category,one time fee_freq",feename+","+catid,"error message for other mandatory fields not filled","failed")
        end
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the mandatory when you selct type id also
  def check_errormsg_with_type_id()
    begin
      $ie.radio(:name,"feeFrequencyType","1").set
      $ie.button(:value,@preview_button).click
      $logger.log_message("__FEENAME,FEECATEGORY,PERIODIC FEE FREQUENCY ENTERED,OTHERS BLANK__")
      begin
        assert($ie.contains_text(@mandatory_recur_week_msg)) and (assert($ie.contains_text(@mandatory_amount_msg)) or assert($ie.contains_text(@mandatory_rate_or_amount_msg))) and assert($ie.contains_text(@mandatory_glcode_msg))
        $logger.log_results("Mandatory check for fields feename,fee_category,periodic fee_freq","feename.fee_category,periodic fee_frequency","error message for other mandatory fields not filled","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Mandatory check for fields feename,fee_category,periodic fee_freq","feename.fee_category,periodic fee_frequency","error message for other mandatory fields not filled","failed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking error message when week periodicity selected
  def check_weekperiodicy_errormsg()
    begin
      $logger.log_message("__CHECK FOR PERIODICITY WITHOUT WEEK OPTION__")
      $ie.radio(:name,"feeRecurrenceType","1").set
      $ie.button(:value,@preview_button).click
      begin
        assert($ie.contains_text(@mandatory_recur_week_msg)) and  (assert($ie.contains_text(@mandatory_rate_or_amount_msg)) or assert($ie.contains_text(@mandatory_amount_msg)) )and assert($ie.contains_text(@mandatory_glcode_msg))
        $logger.log_results("Mandatory check for field periodic without week option","NA","NA","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Mandatory check for field periodic without week option","NA","NA","failed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking error message when month periodicity selected
  def check_monthperiodicy_errormsg()
    begin
      $logger.log_message("__CHECK FOR PERIODICITY WITHOUT MONTH OPTION__")
      $ie.radio(:name,"feeRecurrenceType","2").set
      $ie.button(:value,@preview_button).click
      begin
        assert($ie.contains_text(@mandatory_recur_month_msg)) and  (assert($ie.contains_text(@mandatory_rate_or_amount_msg)) or assert($ie.contains_text(@mandatory_amount_msg)) )and assert($ie.contains_text(@mandatory_glcode_msg))
        $logger.log_results("Mandatory check for field periodic without month checked","NA","NA","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Mandatory check for field periodic without month checked","NA","NA","failed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the conditions when loan category id is selected
  def fee_category_loan(catid,amount,formula_id)
    begin
      if catid==@loans_label then
        $ie.text_field(:name,"rate").set(amount)
        $ie.button(:value,@preview_button).click
        check_errormsg_rate()
        $ie.select_list(:name,"feeFormula").select_value(formula_id)
        $ie.button(:value,@preview_button).click
        check_errormsg_maxrate(amount)
        $ie.text_field(:name,"rate").clear
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #MANDATORY CHECK FOR RATE WITHOUT A FORMULA
  def check_errormsg_rate()
    begin
      $logger.log_message("__MANDATORY CHECK FOR RATE WITHOUT A FORMULA__")
      begin
        assert($ie.contains_text(@mandatory_rateformula_msg) )
        $logger.log_results("Mandatory check for rate without a formula ","NA","NA","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Mandatory check for rate without a formula ","NA","NA","failed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #CHECK FOR MESSAGE WHEN RATE IS GREATER THAN 999 %
  def check_errormsg_maxrate(amount)
    begin
      $logger.log_message("__CHECK FOR MESSAGE WHEN RATE IS GREATER THAN 999 %__")
      if (amount.to_f >999) then
        begin
          assert($ie.contains_text(@maxlen_rate_msg) ) 
          $logger.log_results("Mandatory check for rate greater than 999 with formula ",amount.to_f.to_s,"&lt;=999","passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Mandatory check for rate greater than 999 with formula ",amount.to_f.to_s,"&lt;=999","failed")
        end
      else
        $logger.log_results("check for rate less than 999 with formula ",amount.to_f.to_s,"&lt;=999","passed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #CHECK WHEN AMOUNT ENTERED IS GREATER THAN OR LESS THAN  99999999999.99
  def errormsg_maxamt_P(amount)
    begin
      $logger.log_message("__CHECK WHEN AMOUNT ENTERED IS GREATER THAN OR LESS THAN  99999999999.99__")
      $ie.text_field(:name,"amount").set(amount)
      $ie.button(:value,@preview_button).click
      if (amount.to_f >99999999999.99) then
        begin
          assert($ie.contains_text(@maxlen_amount_msg)) 
          $logger.log_results("mandatory check for amount greater than max amt value for periodic",amount.to_f.to_s,"&lt;=99999999999.99","passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("mandatory check for amount greater than max amt value for periodic",amount.to_f.to_s,"&lt;=99999999999.99","failed")
        end
      else
        $logger.log_results("check for amount less than max amt value for periodic",amount.to_f.to_s,"&lt;=99999999999.99","passed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the conditions when category id is not Loan
  def not_for_loan(catid,toc,amount)
    begin
      $ie.radio(:name,"feeFrequencyType","2").set
      if catid!=@loans_label then
        $ie.select_list(:name,"loanCharge").select(toc)
        $ie.text_field(:name,"amount").set(amount)
      else
        $ie.select_list(:name,"loanCharge").select(toc)
        $ie.text_field(:name,"rate").set(amount)
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #error meessage when ammount eneterd is maximum
  def errormsg_maxamt_O(amount)
    begin
      $ie.button(:value,@preview_button).click
      if (amount.to_f >99999999999.99) then
        begin
          assert($ie.contains_text(@maxlen_amount_msg)) 
          $logger.log_results("Mandatory check for amount greater than max amt value for onetime",amount.to_f.to_s,"&lt;=99999999999.99","passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("mandatory check for amount greater than max amt value for onetime",amount.to_f.to_s,"&lt;=99999999999.99","failed")
        end
      else
        $logger.log_results("check for amount less than max amt value for onetime",amount.to_f.to_s,"&lt;=99999999999.99","passed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #creation of fee with all data
  def fee_create(feename,catid,def_fee,toc,freq,week,month,amt_rate,amount,formula,glcode)
    begin
      admin_check()
      feelink_check()
      $ie.text_field(:name,'feeName').set(feename)
      if ($ie.checkbox(:name, "customerDefaultFee", "1").enabled?() and (catid!=@loans_label and def_fee=="Yes") )then
        $ie.checkbox(:name, "customerDefaultFee", "1").set
      elsif  (catid==@loans_label and ($ie.checkbox(:name, "customerDefaultFee", "1").enabled?())) then
        $ie.checkbox(:name, "customerDefaultFee", "1").clear
      end
      $ie.select_list(:name,"categoryType").select(catid)
      if ((catid.to_s!=@loans_label) and (freq.to_s==@onetime_label)) then
        $ie.radio(:name,"feeFrequencyType","2").set
        $ie.select_list(:name,"customerCharge").select(toc)
      elsif ( (catid.to_s==@loans_label ) and freq.to_s==@onetime_label)then
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
      
      if catid!=@loans_label then 
        $ie.text_field(:name,"amount").clear()
        $ie.text_field(:name,"amount").set(amount)
      elsif ((catid==@loans_label) and (amt_rate=="0")) then
        $ie.text_field(:name,"amount").clear()
        $ie.text_field(:name,"amount").set(amount)
      else
        $ie.text_field(:name,"amount").clear()
        $ie.text_field(:name,"rate").set(amount)
        $ie.select_list(:name,"feeFormula").select_value(formula)
      end
      #$ie.select_list(:name,"glCode").select(glcode)
      $ie.select_list(:name,"glCode").select_value(glcode)
      $ie.button(:value,@preview_button).click
    rescue =>excp
        quit_on_error(excp)
    end
  end
  
  #clicking on submit button
  def click_submit()
    begin
      $ie.button(:value,@submit_button).click
      assert($ie.contains_text(@fee_success_msg))
      $logger.log_results("In fee successfully created page for feename ","NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("successful defined a new fee page did not appear","NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #database check for the fee created
  def dbcheck(feename)
    begin
      search_res=$dbh.real_query("Select * from fees where fee_name='"+feename+"'")
      rowcount=search_res.num_rows
      if rowcount==0 then
        $logger.log_results("Data Base Check","NA","NA","failed") 
      else
        $logger.log_results("Data Base Check","NA","NA","passed")  
      end  
    rescue =>excp
      quit_on_error(excp)
    end 
  end
  
  #checking that whether fee created successfull
  def edit_fee_from_success(feename)
    begin
      #puts "edit_fee_from_success"
      $ie.link(:text,@view_fees_detail_link).click
      assert($ie.contains_text(feename))and assert($ie.contains_text(@edit_fees_link))
      $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","passed")
      edit_fee_information(feename)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #edit fee information from success page
  def edit_fee_information(feename)
    begin
      $ie.link(:text,@edit_fees_link).click
      assert($ie.contains_text(@edit_fees_link))
      $logger.log_results("Page redirect to Edit Fee Details Page","Should redirect","redirected","passed")
      edit_fee_information_cancel(feename)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page redirect to Edit Fee Details Page","Should redirect","redirected","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #checking the cancel functionality from fee details page 
  def edit_fee_information_cancel(feename)
    begin
      #puts "edit_fee_information_cancel"
      $ie.button(:value, @cancel_button).click
      assert($ie.contains_text(feename))and assert($ie.contains_text(@edit_fees_link))
      $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #clicking on edit_fee_infomration_link
  def edit_fee_information_submit()
    begin
      $ie.link(:text,@edit_fees_link).click
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #status change conditions
  def status_change(feename)
    begin
      dbquery("select status from fees where fee_name='"+feename+"'")
      status_id=dbresult[0]
      status_change_inactive(feename)
      edit_fee_information_submit()
      status_change_active(feename)
     
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #status change to active
  def status_change_active(feename)
    begin
      $ie.select_list(:name,"feeStatus").select_value("1")
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(feename))and assert($ie.contains_text(@@feeprop['Fees.previewfeeinformation']))
      $logger.log_results("Status Changed","Should redirect to Preview page","preview page","passed")
      $ie.button(:value,@submit_button).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status Changed","Should redirect to Preview page","N/A","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #status change to inactive
  def status_change_inactive(feename)
    begin
      $ie.select_list(:name,"feeStatus").select_value("2")
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(feename))and assert($ie.contains_text(@@feeprop['Fees.previewfeeinformation']))
      $logger.log_results("Status Changed","Should redirect to Preview page","preview page","passed")
      $ie.button(:value,@submit_button).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status Changed","Should redirect to Preview page","N/A","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #verifying the View fee link
  def verify_viewfeelink()
    begin
      $ie.link(:text,@admin_label).click
      assert($ie.link(:text,@view_fees_link).exists?())
      $logger.log_results("View fee link","NA","should exist","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View fee link","NA","should exist","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #clicking on the View Fee link
  def click_viewfeelink()
    begin
      $ie.link(:text,@view_fees_link).click
      puts "page label "+@view_fees_page_label
      assert($ie.contains_text(@view_fees_page_label))
      $logger.log_results("View fee link","click on view fees link","label "+@view_fees_page_label+" should appear in page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("View fee link","click on view fees link","label "+@view_fees_page_label+" does not appear in page","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  #select and click on one fee
  def select_click_fee()
    begin
      dbquery("select fee_name from fees where status=1")
      feename=dbresult[0]
      $ie.link(:text,feename).click
      assert($ie.contains_text(feename))and assert($ie.contains_text(@edit_fees_link))
      $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","passed")
      edit_fee_information(feename)
      edit_fee_information_submit()
      status_change(feename)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page redirected to Fee Details Page","Should redirect","redirected","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
#added as part og bug#677
  def view_fees_after_backbutton_clicked()
        $ie.link(:text,@admin_label).click
        $ie.link(:text,@view_fees_link).click
        dbquery("select fee_id,fee_name from fees where status=1")
         fees_id=dbresult[0]
         feename=dbresult[1]
         $ie.link(:text,feename).click
         $ie.back()
        #selecting fee other than the previous selected fee.
         dbquery("select fee_id,fee_name from fees where status=1 and fee_id!="+fees_id+"")
         fee_id=dbresult[0]
         feename=dbresult[1]
         $ie.link(:text,feename).click
         assert($ie.contains_text(feename))and assert($ie.contains_text(@edit_fees_link))
         $logger.log_results("Bug#677-View Fees after clicking back button in IE","Should redirect","redirected","passed")
         rescue Test::Unit::AssertionFailedError=>e
         $logger.log_results("Bug#677-View Fees after clicking back button in IE","Should redirect"," not redirected","failed")
         rescue =>excp
         quit_on_error(excp)
  end
  
  #added as part of Bug#827
  def check_errormsg_for_recurrence_pattern()
    begin
      $ie.link(:text,@admin_label).click
      $ie.link(:text,@define_new_fees_link).click
      $ie.text_field(:name,"feeName").set("Feestest") 
      $ie.select_list(:name,"categoryType").select_value("1")
      $ie.radio(:name,"feeFrequencyType","1").set
      $ie.radio(:name,"feeRecurrenceType","1").set
      $ie.text_field(:name,"weekRecurAfter").set("0") 
      $ie.text_field(:name,"amount").set("100") 
      $ie.select_list(:name,"glCode").select_value("48")      
      $ie.button(:value,@preview_button).click
      $ie.button(:value,@submit_button).click
      begin
          assert($ie.contains_text(@invalid_recurrencepattern_msg)) 
          $logger.log_results("Bug#827-Invalid recurrence pattern in fees","Should display a proper error message","Displayed","Passed")
          rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Bug#827-Invalid recurrence pattern in fees","Did not display a proper error message","Not displayed","failed")
         rescue =>excp
         quit_on_error(excp)
                    
      end
    end
  end
  
end

class Fee_Test
  feeobject=Feeclass.new
  feeobject.read_from_properties_file()
  feeobject.read_values_from_hash()
  feeobject.login_fee()
  feeobject.read_labels_from_db()
  feeobject.admin_check()
  feeobject.feelink_check()
  
  filename=File.expand_path(File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  feeobject.open(filename,1)
  rowid= -1
  
  while (rowid<$maxrow*$maxcol-1)

    feeobject.read_values_from_file(rowid,1)
    feeobject.admin_check()
    feeobject.feelink_check()
    feeobject.check_errormsg_all()
    feeobject.check_errormsg_with_feename(feeobject.Feename)
    feeobject.check_errormsg_with_feename_catid(feeobject.Feename,feeobject.Category)
    feeobject.fee_type_selected(feeobject.Category,feeobject.Timeofcharge)
    feeobject.check_errormsg_loan_field(feeobject.Feename,feeobject.Category)
    feeobject.check_errormsg_with_type_id()
    feeobject.check_weekperiodicy_errormsg()
    feeobject.check_monthperiodicy_errormsg()
    feeobject.fee_category_loan(feeobject.Category,feeobject.Amount,feeobject.Formula_id)
    feeobject.errormsg_maxamt_P(feeobject.Amount)
    feeobject.not_for_loan(feeobject.Category,feeobject.Timeofcharge,feeobject.Amount)
    feeobject.errormsg_maxamt_O(feeobject.Amount)   
     
    rowid+=$maxcol
  end
  
  feeobject.open(filename,2)
  rowid=-1
  
  while (rowid<$maxrow*$maxcol-1)
    feeobject.read_values_from_file(rowid,2)
    feeobject.fee_create(feeobject.Feename,feeobject.Category,feeobject.Defaultfees,feeobject.Timeofcharge,feeobject.Frequency,feeobject.Week,feeobject.Month,feeobject.Amt_rate,feeobject.Amount,feeobject.Formula_id,feeobject.Glcode)
    feeobject.click_submit()
    feeobject.dbcheck(feeobject.Feename)
    feeobject.edit_fee_from_success(feeobject.Feename)
    feeobject.edit_fee_information_submit()
    feeobject.status_change(feeobject.Feename)
    
    rowid+=$maxcol
  end
  
  feeobject.verify_viewfeelink()
  feeobject.click_viewfeelink()
  feeobject.select_click_fee()
  #added as part of bug#677
  feeobject.view_fees_after_backbutton_clicked
 # added as part of bug#827
  feeobject.check_errormsg_for_recurrence_pattern()
  
  feeobject.mifos_logout()
end