# This file contains all the automation test case scenario for testing the user module

require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/ui/console/testrunner'
require 'test/unit/assertions'
require 'watir/WindowHelper'

#include module watir
include Watir

class LoanProduct_Test_Cases < TestClass
  
  #read values
  def read_product_values(rowid)
      @prd_inst_name = arrval[rowid+=1].to_s
      # User details
      @short_name = arrval[rowid+=1].to_s
      @descrip = arrval[rowid+=1].to_s
      @descrip = check_value(@descrip)
      @prd_category = arrval[rowid+=1].to_s
      @start_date = arrval[rowid+=1].to_s  
      @end_date = arrval[rowid+=1].to_s
      @appl_for = arrval[rowid+=1].to_s    
      @loancycle_counter = arrval[rowid+=1].to_s
      @loancycle_counter = check_value(@loancycle_counter)
      # Ammount details  
      @min_ammount = arrval[rowid+=1].to_s
      @max_ammount = arrval[rowid+=1].to_s
      @def_ammount = arrval[rowid+=1].to_s
      @def_ammount = check_value(@def_ammount)  
      # Interest details
      @interestrate_type = arrval[rowid+=1].to_s
      @max_interestrate = arrval[rowid+=1].to_s
      @min_interestrate = arrval[rowid+=1].to_s
      @def_interestrate = arrval[rowid+=1].to_s
      # Installment details
      @installments_frequency = arrval[rowid+=1].to_s
      @installments_frequency = check_value(@installments_frequency)
      @max_installments = arrval[rowid+=1].to_i.to_s
      @min_installments = arrval[rowid+=1].to_i.to_s
      @def_installments = arrval[rowid+=1].to_i.to_s
      @interest_disbursement = arrval[rowid+=1].to_s
      @interest_disbursement = check_value(@interest_disbursement)
      @principal_last_installment = arrval[rowid+=1].to_s
      @principal_last_installment = check_value(@principal_last_installment)
      @grace_period_type = arrval[rowid+=1].to_s
      @grace_period_type = check_value(@grace_period_type)
      @grace_period_duration = arrval[rowid+=1].to_i.to_s 
      @grace_period_duration = check_value(@grace_period_duration)
      #Penalty details
      @attach_fee_types = arrval[rowid+=1].to_s
      @attach_fee_types = check_value(@attach_fee_types)
      @penalty_type = arrval[rowid+=1].to_s
      @penalty_type = check_value(@penalty_type)
      @penalty_rate = arrval[rowid+=1].to_i.to_s 
      @penalty_rate = check_value(@penalty_rate)
      @grace_period_penalty = arrval[rowid+=1].to_i.to_s 
      @grace_period_penalty = check_value(@grace_period_penalty)   
      @funds_sources = arrval[rowid+=1].to_s
      @funds_sources = check_value(@funds_sources)
      @interest_glcode = arrval[rowid+=1].to_i.to_s 
      @principal_glcode = arrval[rowid+=1].to_i.to_s  
      @penalities_glcode = arrval[rowid+=1].to_i.to_s 
  end
  
  def Prd_inst_name
      @prd_inst_name
  end
  
  def Short_name
      @short_name
  end
  
  def Description
      @descrip
  end
  
  def Prd_category
      @prd_category
  end
  
  def Start_date
      @start_date
  end
  
  def End_date
      @end_date
  end
  
  def Applied_for
      @appl_for
  end
  
  def Loan_cycle_counter
      @loancycle_counter
  end
  
  def Min_amt
      @min_ammount
  end
  
  def Max_amt
      @max_ammount
  end
  
  def Default_amt
      @def_ammount
  end
  
  def Interest_rate_type
      @interestrate_type
  end
  
  def Max_interest_rate
      @max_interestrate
  end
  
  def Min_interest_rate
      @min_interestrate
  end
  
  def Default_interest_rate
      @def_interestrate
  end
  
  def Installment_freq
      @installments_frequency
  end
  
  def Max_installment
      @max_installments
  end
  
  def Min_installment
      @min_installments
  end
  
  def Default_installment
      @def_installments
  end
  
  def Interest_deducted_at_disb
      @interest_disbursement
  end
  
  def Principal_due_last_inst
      @principal_last_installment
  end
  
  def Grace_type
      @grace_period_type
  end
  
  def Grace_duration
      @grace_period_duration
  end
  
  def Attach_fee_type
      @attach_fee_types
  end
  
  def Penalty_type
      @penalty_type
  end
  
  def Penalty_rate
      @penalty_rate
  end
  
  def Grace_penalty
      @grace_period_penalty
  end
  
  def Fund_source
      @fund_sources
  end
  
  def Interest_glcode
      @interest_glcode
  end
  
  def Principal_glcode
      @principal_glcode
  end
  
  def Penalty_glcode
      @penalities_glcode
  end
  
   #read properties file into a hash and then use the hash for labels,buttons etc
  def read_properties()
      @admin_properties=load_properties("modules/propertis/adminUIResources.properties")
      @loanprd_properties=load_properties("modules/propertis/ProductDefinitionResources.properties")
  end
  
   #read values from the hash into variables
  def read_hash_values()
      #labels
      dbquery("select LOOKUP_VALUE from LOOKUP_VALUE_LOCALE l where l.LOOKUP_VALUE_ID=107")
      @loan_label=dbresult[0]
      dbquery("select LOOKUP_VALUE from LOOKUP_VALUE_LOCALE l where l.LOOKUP_VALUE_ID=395")
      @interest_label=dbresult[0]
      @service_charge_ratetype=string_replace_message(@loanprd_properties['product.intratetype'],@loanprd_properties['product.interest'],@interest_label)
      @max_service_charge=string_replace_message(@loanprd_properties['product.maxintrate'],"interest",@interest_label)
      @min_service_charge=string_replace_message(@loanprd_properties['product.minintrate'],"interest",@interest_label)
      @def_service_charge=string_replace_message(@loanprd_properties['product.defintrate'],"interest",@interest_label)
      @max_loan_amount_label=string_replace_message(@loanprd_properties['product.maxloanamt'],$loan,@loan_label)
      @min_loan_amount_label=string_replace_message(@loanprd_properties['product.minloanamt'],$loan,@loan_label)
      @def_loan_amount_label=@loanprd_properties['product.default']+" "+@loan_label.to_s+" "+@loanprd_properties['product.amount']
      @status=@loanprd_properties['product.status']
      @change_log_label=string_replace_message(@loanprd_properties['product.changelog'],@loanprd_properties['product.savingsview'],"").squeeze(" ")
      #links
      @admin_link=@loanprd_properties['product.admin']
      @view_loanproduct_link=string_replace_message(@admin_properties['admin.viewloanprd'],$loan,@loan_label.to_s)
      @new_loanproduct_link=string_replace_message(@admin_properties['admin.defnewloanprd'],$loan,@loan_label.to_s)
      @edit_loanproduct_link=@loanprd_properties['produt.edit']+" "+@loan_label.to_s+" "+@loanprd_properties['product.info']
               
      @view_changelog_link=@loanprd_properties['product.changelog']
      @define_new_loanproduct_link=@loanprd_properties['product.loandefprd']
      #messages
      @add_loanproduct_msg=@loanprd_properties['product.addnew']+" "+@loan_label.to_s+" "+@loanprd_properties['product.product']
      @enter_loanproduct_msg=string_replace_message(@loanprd_properties['product.enterloanprodinfo'],$loan,@loan_label.to_s)
      @enter_loanproduct_msg=string_replace_message(@enter_loanproduct_msg,@loanprd_properties['product.pro'],@loanprd_properties['product.product'])
      @review_submit=string_replace_message(@loanprd_properties['product.review'],"amp;","")      #removing amp; from the string to get "review & submit" as properties file has it as "review &amp; submit" 
      @product_instance_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.prodinstname'])
      @shortname_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.shortname'])
      @category_msg=string_replace_message(@loanprd_properties['errors.select'],"{0}",@loanprd_properties['product.prodcat'])
      @applicable_for_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.applfor'])
      @interst_ratetype_msg=string_replace_message(@loanprd_properties['errors.select'],"{0}",@loanprd_properties['product.intratetype'])
      @max_interest_rate_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.maxintrate'])
      @min_interest_rate_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.minintrate'])
      @def_interest_rate_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.defintrate'])
      @max_no_of_installment_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.maxinst'])
      @min_no_of_installment_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.mininst'])
      @def_no_of_installment_msg=string_replace_message(@loanprd_properties['errors.mandatory'],"{0}",@loanprd_properties['product.definst'])
      @glcode_interest_msg=string_replace_message(@loanprd_properties['errors.select'],"{0}",@loanprd_properties['product.GlcodeInterest'])
      @glcode_principal_msg=string_replace_message(@loanprd_properties['errors.select'],"{0}",@loanprd_properties['product.GlcodePrincipal'])
      @glcode_penalty_msg=string_replace_message(@loanprd_properties['errors.select'],"{0}",@loanprd_properties['product.GlcodePenalties'])
      @loanproduct_successful_msg=@loanprd_properties['product.loanaddsuccessful']
      @loanproduct_def_new_msg=@loanprd_properties['product.loandefprd']
      @loanproduct_view_details_msg=@loanprd_properties['product.loanviewdet']
      @valid_maxinterest_msg=@loanprd_properties['errors.maxminIntRate']
      @valid_definterest_msg=@loanprd_properties['errors.defIntRate']
      @valid_max_loanamount_msg=@loanprd_properties['errors.maxminLoanAmount']
      @valid_def_loanamount_msg=@loanprd_properties['errors.defLoanAmount']
      @valid_max_installments_msg=string_replace_message(@loanprd_properties['errors.maxMin'],"{0}",@loanprd_properties['product.maxinst'])
      @valid_max_installments_msg=string_replace_message(@valid_max_installments_msg,"{1}",@loanprd_properties['product.mininst'])
      @valid_def_installments_msg=string_replace_message(@loanprd_properties['errors.defMinMax'],"{0}",@loanprd_properties['product.definst'])
      @valid_def_installments_msg=string_replace_message(@valid_def_installments_msg,"{1}",@loanprd_properties['product.mininst'])
      @valid_def_installments_msg=string_replace_message(@valid_def_installments_msg,"{2}",@loanprd_properties['product.maxinst'])      
      @valid_months_msg=string_replace_message(@loanprd_properties['errors.greaterthanzero'],"{0}",@loanprd_properties['product.months'])    
      @valid_weeks_msg=string_replace_message(@loanprd_properties['errors.greaterthanzero'],"{0}",@loanprd_properties['product.weeks'])            
      #error messages on UI when length of input is more than the allowable size
      @product_maxlength_msg=string_replace_message(@loanprd_properties['errors.maximumlength'],"{0}",@loanprd_properties['product.prodinstname'])
      @product_maxlength_msg=string_replace_message(@product_maxlength_msg,"{1}","50")
      @product_desc_maxlength_msg=string_replace_message(@loanprd_properties['errors.maximumlength'],"{0}",@loanprd_properties['product.categorydesc'])
      @product_desc_maxlength_msg=string_replace_message(@product_desc_maxlength_msg,"{1}","200")
      @max_loanamount_maxlength_msg=string_replace_message(@loanprd_properties['errors.decimalFormat'],"{0}",@loanprd_properties['product.maxloanamt'])
      @min_loanamount_maxlength_msg=string_replace_message(@loanprd_properties['errors.decimalFormat'],"{0}",@loanprd_properties['product.minloanamt'])
      @def_loanamount_maxlength_msg=string_replace_message(@loanprd_properties['errors.decimalFormat'],"{0}",@loanprd_properties['product.defamt'])
      @max_interest_maxlength_msg=string_replace_message(@loanprd_properties['errors.decimalFormat'],"{0}",@loanprd_properties['product.maxintrate'])
      @min_interest_maxlength_msg=string_replace_message(@loanprd_properties['errors.decimalFormat'],"{0}",@loanprd_properties['product.minintrate'])
      @def_interest_maxlength_msg=string_replace_message(@loanprd_properties['errors.decimalFormat'],"{0}",@loanprd_properties['product.defintrate'])
      @max_installment_maxlength_msg=string_replace_message(@loanprd_properties['errors.integer'],"{0}",@loanprd_properties['product.maxinst'])
      @min_installment_maxlength_msg=string_replace_message(@loanprd_properties['errors.integer'],"{0}",@loanprd_properties['product.mininst'])      
      @def_installment_maxlength_msg=string_replace_message(@loanprd_properties['errors.integer'],"{0}",@loanprd_properties['product.definst'])
      #error message when there are spaces in short name  
      @errormessage_space=string_replace_message(@loanprd_properties['errors.shortnamemask'],"{0}",@loanprd_properties['product.shortname'])
      #duplicate error messages
      @duplicate_instance_name_msg=@loanprd_properties['errors.duplprdinstname']
      @duplicate_shortname_msg=@loanprd_properties['errors.duplprdinstshortname']
      #buttons
      @cancel_button=@loanprd_properties['product.cancel']
      @preview_button=@loanprd_properties['product.preview']
      @submit_button=@loanprd_properties['product.butsubmit']
      @back_ro_details_page_button=@loanprd_properties['product.back']
  end
  
  #get labels for active and inactive for product
  def get_labels_from_db
    begin
      dbquery("select lookup_value from lookup_value_locale where lookup_id=115")
      @active_label=dbresult[0]
      dbquery("select lookup_value from lookup_value_locale where lookup_id=116")
      @inactive_label=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end 
  end
        
  # Check loanproduct link on admin page after login
  def Check_LoanProduct_Links()
    begin    
      assert($ie.contains_text(@view_loanproduct_link)) and assert($ie.contains_text(@new_loanproduct_link))
      $logger.log_results("LoanProduct- Check for loanproduct links"+@view_loanproduct_link+" and "+@new_loanproduct_link+" on admin page", "Click on admin link","Links should be present","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for loanproduct links"+@view_loanproduct_link+" and "+@new_loanproduct_link+" on admin page", "Click on admin link","The links should be there","Failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  
  # Check cancel in 'Define a new loanproduct' link on admin page
  def Check_New_LoanProduct_cancel()
    begin
      $ie.link(:text,@new_loanproduct_link).click
      $ie.button(:value,@cancel_button).click     
      verify_admin_page()      
    end
  end
  
  # Check new LoanProduct link on admin page
  def Check_New_LoanProduct()
    begin      
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg))
      $logger.log_results("LoanProduct- Check for Define a new LoanProduct link on admin page", "Click on 'Define a new LoanProduct' link","Access to the Define a new LoanProduct page","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for Define a new LoanProduct link on admin page", "Click on 'Define a new LoanProduct' link","Access to the Define a new LoanProduct page","Failed")
    rescue =>excp
      quit_on_error(excp)        
    end      
  end
  
  # Check for all mandatory validation error
  def check_validation_error(prd_inst_name, short_name,description, prd_category, start_date, appl_for, min_ammount,
                             max_ammount,default_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                             max_installments, min_installments, def_installments, interest_glcode, principal_glcode,	penalities_glcode)
    begin     
      $ie.link(:text, @new_loanproduct_link).click  
          
      Man_New_LoanProduct()
      
      Man_New_LoanProduct_with_PrdInstName(prd_inst_name)
      
      Man_New_LoanProduct_with_ShortName(short_name)
      
      Man_New_LoanProduct_with_Desc(description)
      
      Man_New_LoanProduct_with_PrdCategory(prd_category)
      
      Man_New_LoanProduct_with_ApplFor(appl_for)
      
      check_error_message_max_loan_amount(max_ammount)
      
      check_error_message_min_loan_amount(min_ammount)
      
      check_error_message_default_loan_amount(default_ammount)
      
      Man_New_LoanProduct_with_InterestRateType(interestrate_type)
      
      Man_New_LoanProduct_with_MaxInterestRate(max_interestrate)
      
      Man_New_LoanProduct_with_MinInterestRate(min_interestrate)
      
      Man_New_LoanProduct_with_DefInterestrate(def_interestrate)
      
      Man_New_LoanProduct_with_InstallmentsFrequency(installments_frequency)
      
      Man_New_LoanProduct_with_MaxInstallments(max_installments)
      
      Man_New_LoanProduct_with_MinInstallments(min_installments)
      
      Man_New_LoanProduct_with_DefInstallments(def_installments)
      
      Man_New_LoanProduct_with_InterestGlcode(interest_glcode)
      
      Man_New_LoanProduct_with_PrincipalGlcode(principal_glcode)    
      
      #Man_New_LoanProduct_with_PenalitiesGlcode(penalities_glcode) commented as penalties gl code is removed for the time being
      
      if $ie.contains_text(@add_loanproduct_msg+" - "+@review_submit)
        
        Dupl_loan_product_instance_name_shortname(prd_inst_name,short_name)
        
      end
    end    
  end 
  
  # Check for validation messages while creating new LoanProduct
  def Man_New_LoanProduct()
    begin  
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@product_instance_msg)) and\
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@shortname_msg)) and assert($ie.contains_text(@category_msg)) and\
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@interst_ratetype_msg)) and \
      assert($ie.contains_text(@max_interest_rate_msg)) and assert($ie.contains_text(@min_interest_rate_msg)) and \
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "nothing ","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "nothing ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)          
    end    
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name
  def   Man_New_LoanProduct_with_PrdInstName(prd_inst_name)
    begin
      set_value_txtfield("prdOfferingName", prd_inst_name)
      $ie.button(:value,@preview_button).click
      max_field_len(prd_inst_name,50,@product_maxlength_msg)
      
      assert(!$ie.contains_text(@product_instance_msg)) and \
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@shortname_msg)) and assert($ie.contains_text(@category_msg)) and \
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@interst_ratetype_msg)) and \
      assert($ie.contains_text(@max_interest_rate_msg)) and assert($ie.contains_text(@min_interest_rate_msg)) and \
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "product instance name ","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "product instance name ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)           
    end    
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name
  def  Man_New_LoanProduct_with_ShortName(short_name)
    begin
      #set_value_txtfield("prdOfferingName", prd_inst_name)
      set_value_txtfield("prdOfferingShortName", short_name)
      $ie.button(:value,@preview_button).click
      verify_ShortName(short_name)
      assert(!$ie.contains_text(@shortname_msg)) and \
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg))and \
      assert($ie.contains_text(@category_msg)) and \
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@interst_ratetype_msg)) and \
      assert($ie.contains_text(@max_interest_rate_msg)) and assert($ie.contains_text(@min_interest_rate_msg)) and \
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and\
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "short name ","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "short name","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)       
    end    
  end
  # enters data into field description and clicks on preview button
  def Man_New_LoanProduct_with_Desc(description)
    
    set_value_txtfield("description",description)
    $ie.button(:value,@preview_button).click
    max_field_len(description,200,@product_desc_maxlength_msg)
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category
  def   Man_New_LoanProduct_with_PrdCategory(prd_category)
    begin
      # set_value_txtfield("prdOfferingName", prd_inst_name)
      # set_value_txtfield("prdOfferingShortName", short_name)
      set_value_selectlist("prdCategory", prd_category)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@category_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@interst_ratetype_msg)) and \
      assert($ie.contains_text(@max_interest_rate_msg)) and assert($ie.contains_text(@min_interest_rate_msg)) and \
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "product category ","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "product category","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)       
    end    
  end
  
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for
  def    Man_New_LoanProduct_with_ApplFor(appl_for)
    begin
      
      set_value_selectlist("prdApplicableMaster", appl_for)
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@interst_ratetype_msg)) and \
      assert($ie.contains_text(@max_interest_rate_msg)) and assert($ie.contains_text(@min_interest_rate_msg)) and \
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", " applicable for","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", " applicable for","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end    
  end
  
  #check for error mesage when a value greater than max value is entered
  def check_error_message_max_loan_amount(max_ammount)
    begin
      set_value_txtfield("maxLoanAmount",max_ammount)
      $ie.button(:value,@preview_button).click
      max_error_message_number(max_ammount,9999999.9,@max_loanamount_maxlength_msg,"max loan amount")    
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  #check for error mesage when a value greater than max value is entered
  def check_error_message_min_loan_amount(min_ammount)
    set_value_txtfield("minLoanAmount",min_ammount)
    $ie.button(:value,@preview_button).click
    max_error_message_number(min_ammount,9999999.9,@min_loanamount_maxlength_msg ,"min loan amount")    
  end
  
  #check for error mesage when a value greater than max value is entered
  
  def check_error_message_default_loan_amount(default_ammount)
    begin
      set_value_txtfield("defaultLoanAmount",default_ammount)
      $ie.button(:value,@preview_button).click
      max_error_message_number(default_ammount,9999999.9,@def_loanamount_maxlength_msg,"default loan amount")    
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, interestrate_type
  def     Man_New_LoanProduct_with_InterestRateType(interestrate_type)
    begin
      
      set_value_selectlist("interestTypes", interestrate_type)
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@interst_ratetype_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@max_interest_rate_msg)) and assert($ie.contains_text(@min_interest_rate_msg)) and\
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Interest type ","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Interest type ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)   
    end    
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, 
  # appl_for, interestrate_type, max_interestrate
  def    Man_New_LoanProduct_with_MaxInterestRate(max_interestrate)
    begin
      
      set_value_txtfield("maxInterestRate", max_interestrate)
      $ie.button(:value,@preview_button).click
      max_error_message_number(max_interestrate,999,@max_interest_maxlength_msg,"Max interest rate")
      assert(!$ie.contains_text(@max_interest_rate_msg)) and \
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and assert($ie.contains_text(@min_interest_rate_msg)) and \
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Max interest rate ","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Max interest rate ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)    
    end    
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, max_interestrate, 
  # min_interestrate
  def  Man_New_LoanProduct_with_MinInterestRate(min_interestrate)
    begin
      
      set_value_txtfield("minInterestRate", min_interestrate)
      $ie.button(:value,@preview_button).click
      max_error_message_number(min_interestrate,999,@min_interest_maxlength_msg,"Min interest rate")
      assert(!$ie.contains_text(@min_interest_rate_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@def_interest_rate_msg)) and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and\
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Min interest rate","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Min interest rate ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)     
    end    
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate
  def   Man_New_LoanProduct_with_DefInterestrate(def_interestrate)
    begin
      # puts "in Man_New_LoanProduct_with_DefInterestrate \n"
      set_value_txtfield("defInterestRate", def_interestrate)
      $ie.button(:value,@preview_button).click
      max_error_message_number(def_interestrate,999,@def_interest_maxlength_msg,"Default interest rate")
      assert(!$ie.contains_text(@def_interest_rate_msg)) and \
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg))and assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and\
      assert($ie.contains_text(@glcode_penalty_msg))
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Default interest rate","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Default interest rate ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)     
    end    
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate, , installments_frequency
  def   Man_New_LoanProduct_with_InstallmentsFrequency(installments_frequency)
    begin
      #  puts "in Man_New_LoanProduct_with_InstallmentsFrequency"
      
      if not("" == installments_frequency)
        freqOfInstallments = installments_frequency.split(",").to_a       
        if "weeks" == freqOfInstallments[0].to_s
          
          $ie.radio(:name, "freqOfInstallments", "1").click              
          set_value_txtfield("recurAfter", freqOfInstallments[1].to_i.to_s)
        else
          if "months" == freqOfInstallments[0].to_s
            
            $ie.radio(:name, "freqOfInstallments", "2").click                        
            set_value_txtfield("recurAfter", freqOfInstallments[1].to_i.to_s)              
          end 
        end
      end      
      
      $ie.button(:value,@preview_button).click
      
      #assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg))and \
      assert($ie.contains_text(@max_no_of_installment_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg)) 
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Frequency of installment ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Frequency of installment ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate, , installments_frequency, max_installments
  def  Man_New_LoanProduct_with_MaxInstallments(max_installments)
    begin
      # puts "in Man_New_LoanProduct_with_MaxInstallments \n"
      
      set_value_txtfield("maxNoInstallments", max_installments)
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@max_no_of_installment_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@min_no_of_installment_msg)) and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg)) 
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Max Installments ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Max Installments ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate, , installments_frequency, max_installments, min_installments
  def Man_New_LoanProduct_with_MinInstallments(min_installments)
    begin
      #  puts "in Man_New_LoanProduct_with_MinInstallments\n"
      
      # set_value_txtfield("maxNoInstallments", max_installments)
      set_value_txtfield("minNoInstallments", min_installments)
      
      $ie.button(:value,@preview_button).click
      assert(!$ie.contains_text(@min_no_of_installment_msg)) and \
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg))and assert($ie.contains_text(@def_no_of_installment_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg)) 
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Min Installments ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Min Installments ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate, , installments_frequency, max_installments, min_installments,
  # def_installments
  def   Man_New_LoanProduct_with_DefInstallments(def_installments)
    begin
      #puts "in Man_New_LoanProduct_with_DefInstallments\n "
      set_value_txtfield("defNoInstallments", def_installments)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@def_no_of_installment_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg)) 
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Default Installments  ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Default Installments  ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate, , installments_frequency, max_installments, min_installments,
  # def_installments, interest_glcode
  def  Man_New_LoanProduct_with_InterestGlcode(interest_glcode)
    begin
      #puts "in  Man_New_LoanProduct_with_InterestGlcode\n "
      
      set_value_selectlist("interestGLCode", interest_glcode)      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@glcode_interest_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@glcode_principal_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg)) 
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Interest Glcode ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Interest Glcode ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate, , installments_frequency, max_installments, min_installments,
  # def_installments, interest_glcode, principal_glcode
  def  Man_New_LoanProduct_with_PrincipalGlcode(principal_glcode)
    begin
      # puts "in  Man_New_LoanProduct_with_PrincipalGlcode\n "
      set_value_selectlist("principalGLCode", principal_glcode)
      
      $ie.button(:value,@preview_button).click
      assert(!$ie.contains_text(@glcode_principal_msg)) and assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg)) and \
      assert($ie.contains_text(@glcode_penalty_msg)) 
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Principal Glcode ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Principal Glcode ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  # Check for validation messages while creating new LoanProduct with prd_inst_name, short_name, prd_category, appl_for, interestrate_type, 
  # max_interestrate, min_interestrate, def_interestrate, , installments_frequency, max_installments, min_installments,
  # def_installments, interest_glcode, principal_glcode, penalities_glcode
  def Man_New_LoanProduct_with_PenalitiesGlcode(penalities_glcode) 
    begin
      #puts "in Man_New_LoanProduct_with_PenalitiesGlcode\n"
      set_value_selectlist("penaltyGLCode", penalities_glcode)      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@glcode_penalty_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Penalty Glcode ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "Penalty Glcode ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  # Check for all mandatory validation error
  def check_field_validation_error(prd_inst_name, short_name, min_ammount,
                                   max_ammount, def_ammount, max_interestrate, min_interestrate, def_interestrate,
                                   max_installments, min_installments, def_installments)
    begin
      Click_Admin_page()
      $ie.link(:text, @new_loanproduct_link).click       
      Check_New_LoanProduct()      
      verify_PrdInstName(prd_inst_name)      
      verify_ShortName(short_name)
      
      verify_MaxInterestRate(max_interestrate)
      verify_MinInterestRate(min_interestrate)
      verify_DefInterestRate(def_interestrate)       
      
      verify_valid_MaxInterestRate(100, 105)
      verify_valid_DefInterestRate(100, 105)
      
      verify_MaxAmmount(max_ammount)
      verify_MinAmmount(min_ammount)
      verify_DefAmmount(def_ammount)       
      
      verify_valid_MaxAmmount(2000, 5000)
      verify_valid_DefAmmount(2000, 5000)
      
      verify_MaxInstallments(max_installments)
      verify_MinInstallments(min_installments)
      verify_DefInstallments(def_installments)
      
      verify_valid_MaxInstallments(2, 5)
      verify_valid_DefInstallments(2, 5)      
      
      #     verify_InstallmentFrequency_weeks()
      verify_InstallmentFrequency_months()
    rescue =>excp
      quit_on_error(excp)        
    end    
  end
  
  # Check for field validation messages while creating new LoanProduct with prd_inst_name
  def verify_PrdInstName(prd_inst_name)
    begin
      set_value_txtfield("prdOfferingName", prd_inst_name)      
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@product_maxlength_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "prd_inst_name", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "prd_inst_name","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)     
    end
  end
  
  
  # Check for validation messages while creating new LoanProduct with short_name
  def verify_ShortName(short_name) 
    
    b=(short_name =~ /\s+/)
    if (b!=nil) 
      begin
        assert($ie.contains_text(@errormessage_space))      
        $logger.log_results("LoanProduct- Error message appears when short_name contains space", short_name, @errormessage_space, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("LoanProduct- Error message did not appear when short_name contains space", short_name,@errormessage_space,"Failed")
      rescue =>excp
        quit_on_error(excp)          
      end
    else
      begin
        assert(!$ie.contains_text(@errormessage_space))
        $logger.log_results("LoanProduct- Error message does not appear when short_name does not contain space",short_name,"No error message","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("LoanProduct- Error message appears when short_name does not contain space",short_name,"No error message","failed")
      rescue =>excp
        quit_on_error(excp)         
      end 
    end
  end 
  
  # Check for validation messages while creating new LoanProduct with max_interestrate
  def verify_MaxInterestRate(max_interestrate)
    if (max_interestrate.to_f >999)
      begin
        assert($ie.contains_text(@max_interest_rate_msg))      
        $logger.log_results("Error message appears when interest rate specified is greater than max value", max_interestrate.to_s,@max_interest_rate_msg , "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear when interest rate specified is greater than max value", max_interestrate.to_s,@max_interest_rate_msg , "failed")
      rescue =>excp
        quit_on_error(excp)         
      end
    else
      begin
        assert(!$ie.contains_text(@max_interest_rate_msg))      
        $logger.log_results("No Error message appears when interest rate specified is less than max value", max_interestrate.to_s,"No error message", "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message appears when interest rate specified is less than max value", max_interestrate.to_s,"No error message", "failed")    
      rescue =>excp
        quit_on_error(excp)           
      end
    end
  end
  
  # Check for validation messages while creating new LoanProduct with def_interestrate
  def verify_DefInterestRate(def_interestrate)
    begin
      set_value_txtfield("defInterestRate", def_interestrate)    
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@def_interest_rate_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_interestrate ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_interestrate ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with min_interestrate
  def verify_MinInterestRate(min_interestrate)
    begin
      set_value_txtfield("minInterestRate", min_interestrate)
      
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@min_interest_rate_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "min_interestrate ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "min_interestrate ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with max_interestrate, min_interestrate
  def verify_valid_MaxInterestRate(max_interestrate, min_interestrate) 
    begin
      set_value_selectlist("interestTypes", "Flat")
      set_value_txtfield("maxInterestRate", max_interestrate.to_i.to_s)
      set_value_txtfield("minInterestRate", min_interestrate.to_i.to_s)
      set_value_txtfield("defInterestRate", min_interestrate.to_i.to_s)
      
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@valid_maxinterest_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_interestrate, min_interestrate", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_interestrate, min_interestrate","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with max_interestrate, min_interestrate
  def verify_valid_DefInterestRate(def_interestrate, min_interestrate) 
    begin
      set_value_selectlist("interestTypes", "Flat")
      set_value_txtfield("maxInterestRate", min_interestrate.to_i.to_s)
      set_value_txtfield("minInterestRate", min_interestrate.to_i.to_s)
      set_value_txtfield("defInterestRate", def_interestrate.to_i.to_s)      
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@valid_definterest_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_interestrate, min_interestrate", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_interestrate, min_interestrate","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)        
    end
  end
  
  # Check for validation messages while creating new LoanProduct with max_ammount
  def verify_MaxAmmount(max_ammount) 
    begin
      
      set_value_txtfield("maxLoanAmount", max_ammount)     
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@max_loanamount_maxlength_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_ammount", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_ammount","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with min_ammount
  def verify_MinAmmount(min_ammount)  
    begin
      set_value_txtfield("minLoanAmount", min_ammount)
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@min_loanamount_maxlength_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "min_ammount", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "min_ammount","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)         
    end
  end
  
  # Check for validation messages while creating new LoanProduct with def_ammount
  def verify_DefAmmount(def_ammount)  
    begin
      
      set_value_txtfield("defaultLoanAmount", def_ammount.to_i.to_s)      
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@def_loanamount_maxlength_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_ammount ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_ammount ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with max_ammount, min_ammount
  def  verify_valid_MaxAmmount(max_ammount, min_ammount) 
    begin
      
      set_value_txtfield("maxLoanAmount", max_ammount.to_i.to_s)
      set_value_txtfield("minLoanAmount", min_ammount.to_i.to_s)
      set_value_txtfield("defaultLoanAmount", min_ammount.to_i.to_s)
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@valid_max_loanamount_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_ammount, min_ammount", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_ammount, min_ammount","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with def_ammount, min_ammount
  def  verify_valid_DefAmmount(def_ammount, min_ammount) 
    begin
      set_value_txtfield("maxLoanAmount", min_ammount.to_i.to_s)
      set_value_txtfield("defaultLoanAmount", def_ammount.to_i.to_s)
      set_value_txtfield("minLoanAmount", min_ammount.to_i.to_s)
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@valid_def_loanamount_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_ammount, min_ammount", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_ammount, min_ammount","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)          
    end
  end
  
  # Check for validation messages while creating new LoanProduct with max_installments
  def verify_MaxInstallments(max_installments)
    begin        
      
      set_value_txtfield("maxNoInstallments", max_installments)     
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@max_installment_maxlength_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_installments", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_installments","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)         
    end
  end
  
  # Check for validation messages while creating new LoanProduct with min_installments
  def verify_MinInstallments(min_installments)
    begin        
      set_value_txtfield("minNoInstallments", min_installments)
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text( @min_installment_maxlength_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "min_installments", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "min_installments","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)         
    end
  end
  
  # Check for validation messages while creating new LoanProduct with def_installments
  def verify_DefInstallments(def_installments)
    begin        
      set_value_txtfield("defNoInstallments", def_installments)
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text( @def_installment_maxlength_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_installments ", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_installments ","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)         
    end
  end
  
  # Check for validation messages while creating new LoanProduct with max_installments, min_installments
  def verify_valid_MaxInstallments(max_installments, min_installments)
    begin        
      set_value_txtfield("maxNoInstallments", max_installments.to_i.to_s)
      set_value_txtfield("minNoInstallments", min_installments.to_i.to_s)
      set_value_txtfield("defNoInstallments", min_installments.to_i.to_s)
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@valid_max_installments_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_installments, min_installments", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "max_installments, min_installments","All validation error message", "Failed")
    rescue =>excp
      quit_on_error(excp)         
    end
  end
  
  # Check for validation messages while creating new LoanProduct with def_installments, min_installments
  def verify_valid_DefInstallments(def_installments, min_installments)
    begin        
      
      set_value_txtfield("maxNoInstallments", min_installments.to_i.to_s)  
      set_value_txtfield("defNoInstallments", def_installments.to_i.to_s)
      set_value_txtfield("minNoInstallments", min_installments.to_i.to_s)
      $ie.button(:value, @preview_button).click
      
      assert($ie.contains_text(@valid_def_installments_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_installments, min_installments", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "def_installments, min_installments","All validation error message", "Failed")
    rescue =>excp
      quit_on_error(excp)       
    end
  end
  
  # Check for validation messages while creating new LoanProduct with  0 as InstallmentFrequency
  def verify_InstallmentFrequency_weeks()
    begin  
      set_value_txtfield("defInterestRate", "1")
      $ie.radio(:name, "freqOfInstallments", "2").click 
      set_value_txtfield("recurAfter", "")
      $ie.radio(:name, "freqOfInstallments", "1").click             
      set_value_txtfield("recurAfter", "")
      $ie.button(:value,@preview_button).click                  
      assert($ie.contains_text(@valid_weeks_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "freqOfInstallments-weeks", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "freqOfInstallments-weeks","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)         
    end
  end
  
  def verify_InstallmentFrequency_months()
    begin
      set_value_txtfield("defInterestRate", "1")
      $ie.radio(:name, "freqOfInstallments", "2").click                        
      set_value_txtfield("recurAfter", "")              
      
      $ie.button(:value,@preview_button).click                  
      assert($ie.contains_text(@valid_months_msg))      
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "freqOfInstallments- months", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for validation error while creating new LoanProduct", "freqOfInstallments- months","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)         
    end
  end
  
  
  # checks for the unique LoanProduct name and shortname functionality
  def Dupl_loan_product_instance_name_shortname(prd_inst_name,short_name)
    
    number_of_products=count_items("select count(PRD_OFFERING_NAME) count from PRD_OFFERING where PRD_OFFERING_NAME='"+prd_inst_name+"'")
    number_of_shortname=count_items("select count(PRD_OFFERING_SHORT_NAME) count from PRD_OFFERING where PRD_OFFERING_SHORT_NAME='"+short_name+"'")
    if (number_of_products.to_i > 0) and (number_of_shortname.to_i > 0)
      $ie.button(:value,@submit_button).click
      begin 
        assert($ie.contains_text(@duplicate_instance_name_msg)) and \
        assert($ie.contains_text(@duplicate_shortname_msg))
        $logger.log_results("Error message appears for duplicate product instance name",prd_inst_name.to_s,@duplicate_instance_name_msg,"passed")
        $logger.log_results("Error message appears for duplicate product instance short name",short_name.to_s,@duplicate_shortname_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear when both instance name and short name already exist",prd_inst_name.to_s," , "+short_name.to_s,@duplicate_instance_name_msg+" and "+@duplicate_shortname_msg,"failed")      
      rescue =>excp
        quit_on_error(excp)           
      end
    elsif (number_of_products.to_i > 0) and (number_of_shortname.to_i == 0) 
      $ie.button(:value,@submit_button).click
      begin
        assert($ie.contains_text(@duplicate_instance_name_msg))
        $logger.log_results("Error message appears for duplicate product instance name",prd_inst_name.to_s,@duplicate_instance_name_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear for duplicate product instance name",prd_inst_name.to_s,@duplicate_instance_name_msg,"failed") 
      rescue =>excp
        quit_on_error(excp)    
      end
    elsif (number_of_products.to_i == 0) and (number_of_shortname.to_i > 0)
      $ie.button(:value,@submit_button).click
      begin 
        assert($ie.contains_text(@duplicate_shortname_msg))
        $logger.log_results("Error message appears for duplicate product instance short name",short_name.to_s,@duplicate_shortname_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear for duplicate product instance short name",short_name.to_s,@duplicate_shortname_msg,"failed")      
      rescue =>excp
        quit_on_error(excp)           
      end
    else
      begin
        $ie.button(:value,@submit_button).click
      rescue =>excp
        quit_on_error(excp)
      end   
    end
  end
  
  # Creates a new LoanProduct and verify the preview page and database values after submission 
  def Create_New_LoanProduct(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                             max_ammount,def_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                             max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
    
    begin
      $ie.link(:text,@new_loanproduct_link).click
      
      set_LoanProduct_data(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                           max_ammount, def_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                           max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)          
                           
      $ie.button(:value, @preview_button).click
      
      validate_LoanProduct_preview_page(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                                        max_ammount, def_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                                        max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
      
      $ie.button(:value,@submit_button).click
      
      validate_LoanProduct_creation(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                                    max_ammount, def_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                                    max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
      LoanProduct_Creation_Conformation()
      
    rescue =>excp
     quit_on_error(excp)          
    end
  end
  
  # Check the edit data link on the preview page
  def check_editdata_onpreview(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                               max_ammount, def_ammount,interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                               max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
    begin
      set_LoanProduct_data(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                         max_ammount,  def_ammount,interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                         max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
                         
      $ie.button(:value,@preview_button).click  
    
      validate_LoanProduct_preview_page(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                                      max_ammount,  def_ammount,interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                                      max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
      $ie.button(:name, "edit").click
    
      assert($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg))
      $logger.log_results("LoanProduct- Check for Define a new LoanProduct link on admin page", "Click on 'Define a new LoanProduct' link","Access to the Define a new LoanProduct page","Passed")
      $ie.button(:value,@preview_button).click
    
      validate_LoanProduct_preview_page(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                                      max_ammount,  def_ammount,interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                                      max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
                                      
      $ie.button(:value,@cancel_button).click 
      verify_admin_page()         
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Check for Define a new LoanProduct link on admin page", "Click on admin 'Define a new LoanProduct' link","Access to the Define a new LoanProduct page","Failed")    
    rescue =>excp
      quit_on_error(excp)
    end    
  end
  
  
  
  
  # Enter the new LoanProduct data 
  def set_LoanProduct_data(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
                           max_ammount, def_ammount,interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
                           max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
    begin      
      if($ie.contains_text(@add_loanproduct_msg+" - "+@enter_loanproduct_msg))
        $logger.log_results("LoanProduct- Create new user page", "Click on 'Define a new LoanProduct' link","Access to the new LoanProduct page","Passed")
      else
        $logger.log_results("LoanProduct- Create new user page", "Click on admin 'Define a new LoanProduct' link","Access to the new LoanProduct page","Failed")
      end
      
      
      # Set the LoanProduct Details 
      set_value_txtfield("prdOfferingName", prd_inst_name)
      set_value_txtfield("prdOfferingShortName", short_name)
      set_value_selectlist("prdCategory", prd_category)
      set_value_selectlist("prdApplicableMaster", appl_for)
      
      # Set the LoanProduct ammount Details
      set_value_txtfield("minLoanAmount", min_ammount.to_i.to_s)
      set_value_txtfield("maxLoanAmount", max_ammount.to_i.to_s)
      set_value_txtfield("defaultLoanAmount",def_ammount.to_i.to_s)
      
      
      
      # Set the LoanProduct Interest Rate Details 
      set_value_selectlist("interestTypes", interestrate_type)
      set_value_txtfield("maxInterestRate", max_interestrate)
      set_value_txtfield("minInterestRate", min_interestrate)
      set_value_txtfield("defInterestRate", def_interestrate)
      
      # Set the LoanProduct Installment Details
      if not("" == installments_frequency)
        freqOfInstallments = installments_frequency.split(",").to_a       
        if "weeks" == freqOfInstallments[0].to_s
          
          $ie.radio(:name, "freqOfInstallments", "1").click              
          set_value_txtfield("recurAfter", freqOfInstallments[1].to_i.to_s)
        else
          if "months" == freqOfInstallments[0].to_s            
            $ie.radio(:name, "freqOfInstallments", "2").click                        
            set_value_txtfield("recurAfter", freqOfInstallments[1].to_i.to_s)              
          end
        end
      end
      
      set_value_txtfield("maxNoInstallments", max_installments)
      set_value_txtfield("minNoInstallments", min_installments)
      set_value_txtfield("defNoInstallments", def_installments)
      
      # Set the LoanProduct glcode
      set_value_selectlist("interestGLCode", interest_glcode)
      set_value_selectlist("principalGLCode", principal_glcode)
      #set_value_selectlist("penaltyGLCode", penalities_glcode)  commented as this is currently not there
      
    rescue =>excp
      quit_on_error(excp)     
    end 
  end
  
  # Validate the preview for new check list  
  def  validate_LoanProduct_preview_page(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
    max_ammount,def_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
    max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
    
    begin
      if $ie.contains_text(@add_loanproduct_msg+" - "+@review_submit) then
        $logger.log_results("New LoanProduct- Preview", "new loan product inputs", "valid preview page","Passed")
      else 
        $logger.log_results("New LoanProduct- Preview", "new user inputs" , "valid preview page","Failed")
      end
      
      frequencyOfInstallments = installments_frequency.split(",").to_a
      
      if frequencyOfInstallments[0].to_s == "weeks"
        periods = "week"
      elsif frequencyOfInstallments[0].to_s == "months"
        periods = "month"
      end
      
      
      dbquery("select date_format(sysdate(),'%d/%m/%Y')")
      @@current_date = dbresult[0]
      
      #   if ($ie.contains_text("Product instance name : " + prd_inst_name.to_s)\
      #    and $ie.contains_text("Short name : " + short_name.to_s)\
      #    and $ie.contains_text("Product category : " + prd_category.to_s)\
      #    and $ie.contains_text("Start date : " + @@current_date.to_s)\
      #    and $ie.contains_text("Applicable for : " + appl_for.to_s)\
      #    and $ie.contains_text("Min loan amount : " + min_ammount.to_f.to_s)\
      #    and $ie.contains_text("Max loan amount : " + max_ammount.to_f.to_s)\
      #    and $ie.contains_text("Default amount : " + def_ammount.to_f.to_s)\
      #    and $ie.contains_text("Interest rate type : " + interestrate_type.to_s)\
      #    and $ie.contains_text("Max interest rate : " + max_interestrate.to_f.to_s + " %")\
      #    and $ie.contains_text("Min interest rate : " + min_interestrate.to_f.to_s + " %")\
      #    and $ie.contains_text("Default interest rate : " + def_interestrate.to_f.to_s + " %")\
      #    and $ie.contains_text("Frequency of installments : " + frequencyOfInstallments[1].to_s + " " + periods.to_s + "(s)")\
      #    and $ie.contains_text("Max # of installments : " + max_installments.to_i.to_s)\
      #    and $ie.contains_text("Min # of installments : " + min_installments.to_i.to_s)\
      #    and $ie.contains_text("Default # of installments : " + def_installments.to_i.to_s)\
      #   and $ie.contains_text("Interest : " + interest_glcode.to_s)\
      #    and $ie.contains_text("Principal : " + principal_glcode.to_s)\
      #    and $ie.contains_text("Penalties : " + penalities_glcode.to_s)\
      #    )       
      #     $logger.log_results("LoanProduct- validating the preview page", "Click on preview button","Valid preview page content", "Passed")
      #   else
      #     $logger.log_results("LoanProduct- validating the preview page", "Click on preview button","Valid preview page content", "Failed")
      #   end     
      #puts "###################### IN preview ########################"
      assert_on_page(@loanprd_properties['product.prodinstname']+" : " + prd_inst_name.to_s)
      assert_on_page(@loanprd_properties['product.shortname']+" : " + short_name.to_s)
      assert_on_page(@loanprd_properties['product.prodcat']+" : " + prd_category.to_s)
      assert_on_page(@loanprd_properties['product.startdate']+" : " + @@current_date.to_s)
      assert_on_page(@loanprd_properties['product.applfor']+" : " + appl_for.to_s)
      assert_on_page(@min_loan_amount_label+" : " + min_ammount.to_f.to_s)
      assert_on_page(@max_loan_amount_label+" : " + max_ammount.to_f.to_s)
      assert_on_page(@def_loan_amount_label+" : " + def_ammount.to_f.to_s)
      assert_on_page(@service_charge_ratetype+" : " + interestrate_type.to_s)
      assert_on_page(@max_service_charge+" : " + max_interestrate.to_f.to_s + " %")
      assert_on_page(@min_service_charge+" : " + min_interestrate.to_f.to_s + " %")
      assert_on_page(@def_service_charge+" : " + def_interestrate.to_f.to_s + " %")
      assert_on_page(@loanprd_properties['product.freqofinst']+" : " + frequencyOfInstallments[1].to_s + " " + periods.to_s + "(s)") 
      assert_on_page(@loanprd_properties['product.maxinst']+" : " + max_installments.to_i.to_s)      
      assert_on_page(@loanprd_properties['product.mininst']+" : " + min_installments.to_i.to_s)
      assert_on_page(@loanprd_properties['product.definst']+" : " + def_installments.to_i.to_s)
      assert_on_page(@loanprd_properties['product.principal']+" : " + principal_glcode.to_s)
      #assert_on_page(@loanprd_properties['product.penalties']+" : " + penalities_glcode.to_s) 
      #  puts "############## Preview Exit ###############"
    rescue =>excp
      quit_on_error(excp)       
    end 
  end  
  
  # Verify the creation of new LoanProduct from the data base  
  def     validate_LoanProduct_creation(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
    max_ammount, def_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
    max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
    
    begin
      
      # Fetch the data from database and store the values in the corresponding variables
      dbquery("select PRD_OFFERING_ID, MIN_LOAN_AMOUNT, MAX_LOAN_AMNT, INTEREST_TYPE_ID, MAX_INTEREST_RATE, MIN_INTEREST_RATE, DEF_INTEREST_RATE, MAX_NO_INSTALLMENTS, MIN_NO_INSTALLMENTS, DEF_NO_INSTALLMENTS,  INTEREST_GLCODE_ID, PRINCIPAL_GLCODE_ID, PENALTIES_GLCODE_ID from loan_offering lo where PRD_OFFERING_ID = (select  max(PRD_OFFERING_ID) from loan_offering)")
      prd_offering_id = dbresult[0]      
      db_min_ammount = dbresult[1]
      db_max_ammount = dbresult[2]
      
      interestrate_type_id = dbresult[3]
      db_max_interestrate = dbresult[4]
      db_min_interestrate = dbresult[5]
      db_def_interestrate = dbresult[6]
      
      db_max_installments = dbresult[7]
      db_min_installments = dbresult[8]
      db_def_installments = dbresult[9]
      
      db_interest_glcode_id = dbresult[10]
      db_principal_glcode_id = dbresult[11]
      db_penalities_glcode_id = dbresult[12]      
      
      dbquery("select date_format(START_DATE,'%d/%m/%Y'), PRD_APPLICABLE_MASTER_ID, PRD_OFFERING_NAME, PRD_OFFERING_SHORT_NAME, PRD_CATEGORY_NAME, OFFERING_STATUS_ID, date_format(po.CREATED_DATE,'%d/%m/%Y') from prd_offering po , prd_category pc where PRD_OFFERING_ID =" + prd_offering_id.to_s + " and po.PRD_CATEGORY_ID = pc.PRD_CATEGORY_ID ")
      db_start_date = dbresult[0]
      db_applicable_id = dbresult[1]
      
      db_prd_inst_name = dbresult[2]
      db_short_name = dbresult[3]            
      db_prd_category = dbresult[4]
      db_status_id = dbresult[5]
      db_created_date = dbresult[6]
      
      dbquery("select LOOKUP_VALUE from lookup_value_locale where LOOKUP_ID = (select LOOKUP_ID from prd_applicable_master where PRD_APPLICABLE_MASTER_ID =" + db_applicable_id.to_s + " )")
      db_apply_for = dbresult[0]
      
      dbquery("select LOOKUP_VALUE from lookup_value_locale where LOOKUP_ID = (select LOOKUP_ID from interest_types where INTEREST_TYPE_ID = " + interestrate_type_id.to_s + ")")
      db_interestrate_type = dbresult[0]
      
      dbquery("select rd.RECUR_AFTER, rt.RECURRENCE_NAME from recurrence_detail rd, recurrence_type rt where rt.RECURRENCE_ID =rd.RECURRENCE_ID and rd.MEETING_ID = (select PRD_MEETING_ID from prd_offering_meeting where PRD_OFFERING_ID = " + prd_offering_id.to_s + ")")
      db_installments_frequency = dbresult[1].to_s + "," + dbresult[0].to_s
      
      dbquery("select GLCODE_VALUE from gl_code where GLCODE_ID =" + db_interest_glcode_id.to_s )
      db_interest_glcode = dbresult[0]
      
      dbquery("select GLCODE_VALUE from gl_code where GLCODE_ID =" + db_principal_glcode_id.to_s )
      db_principal_glcode = dbresult[0]
      
      #dbquery("select GLCODE_VALUE from gl_code where GLCODE_ID =" + db_penalities_glcode_id.to_s )
      #db_penalities_glcode = dbresult[0].to_s
      
      
      #  if(db_prd_inst_name.to_s ==  prd_inst_name.to_s\
      #  and db_short_name.to_s ==  short_name.to_s\
      #  and db_prd_category.to_s ==  prd_category.to_s\
      #  and db_apply_for.to_s ==  appl_for.to_s\
      #  and db_start_date.to_s ==  @@current_date.to_s\
      #  and db_created_date.to_s ==  @@current_date.to_s\
      #  and db_max_ammount.to_f == max_ammount.to_f\
      #  and db_min_ammount.to_f == min_ammount.to_f\
      #  and db_interestrate_type.to_f == interestrate_type.to_f\
      #  and db_max_interestrate.to_f == max_interestrate.to_f\
      #  and db_min_interestrate.to_f == min_interestrate.to_f\
      #  and db_def_interestrate.to_f == def_interestrate.to_f\
      #  and db_max_installments.to_i ==  max_installments.to_i\
      #  and db_min_installments.to_i ==  min_installments.to_i\
      #  and db_def_installments.to_i ==  def_installments.to_i\
      #  and db_interest_glcode.to_s ==  interest_glcode.to_s\
      #  and db_principal_glcode.to_s ==  principal_glcode.to_s\
      #  and db_penalities_glcode.to_s ==  penalities_glcode.to_s\
      #  and 1 == db_status_id.to_i)
      #    $logger.log_results("LoanProduct- validating the LoanProduct creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Passed")
      #  else
      #    $logger.log_results("LoanProduct- validating the LoanProduct creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Failed")
      #  end  
      
      dbcheck("Product instance name",prd_inst_name.to_s,db_prd_inst_name.to_s) 
      dbcheck("Short name",short_name.to_s,db_short_name.to_s) 
      dbcheck("Product Category",prd_category.to_s,db_prd_category.to_s) 
      dbcheck("Applicable for",appl_for.to_s,db_apply_for.to_s) 
      dbcheck("start date",@@current_date.to_s,db_start_date.to_s) 
      dbcheck("Created date",@@current_date.to_s,db_created_date.to_s) 
      dbcheck("Max amount",max_ammount.to_f,db_max_ammount.to_f) 
      dbcheck("Min amount",min_ammount.to_f,db_min_ammount.to_f) 
      dbcheck("Interest rate type",interestrate_type.to_s,db_interestrate_type.to_s) 
      dbcheck("Max interest rate",max_interestrate.to_f,db_max_interestrate.to_f) 
      dbcheck("Min interest rate",min_interestrate.to_f,db_min_interestrate.to_f) 
      dbcheck("Default interest rate",def_interestrate.to_f,db_def_interestrate.to_f) 
      dbcheck("Max installments",max_installments.to_i,db_max_installments.to_i) 
      dbcheck("Min installments",min_installments.to_i,db_min_installments.to_i) 
      dbcheck("Default installments",def_installments.to_i,db_def_installments.to_i) 
      dbcheck("Interest glcode",interest_glcode.to_s,db_interest_glcode.to_s) 
      dbcheck("principal glcode",principal_glcode.to_s,db_principal_glcode.to_s) 
      #dbcheck("Penalities glcode",penalities_glcode.to_s,db_penalities_glcode.to_s) 
      
    rescue =>excp
      quit_on_error(excp)    
      
    end 
  end  
  
  #Check for the LoanProductCreation-Conformation page
  def LoanProduct_Creation_Conformation()
    begin
      assert($ie.contains_text(@loanproduct_successful_msg))and assert($ie.contains_text(@loanproduct_def_new_msg)) and \
      assert($ie.contains_text(@loanproduct_view_details_msg))
      $logger.log_results("LoanProduct- LoanProduct created", "LoanProduct created","The page should redirect to LoanProductCreation-Conformation page","Passed")
      $ie.link(:text, @define_new_loanproduct_link).click
      Check_New_LoanProduct()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- LoanProduct created", "LoanProduct created","The page should redirect to LoanProductCreation-Conformation page","Failed")     
    rescue =>excp
      quit_on_error(excp)   
    end
  end      
  
  # Mandatory check for view check list
  def check_view_loanproduct(prd_inst_name, short_name, prd_category, start_date, appl_for, min_ammount,
    max_ammount, def_ammount, interestrate_type, max_interestrate, min_interestrate, def_interestrate, installments_frequency,
    max_installments, min_installments, def_installments, interest_glcode, principal_glcode, penalities_glcode)
    begin
      Click_Admin_page()
      $ie.link(:text, @view_loanproduct_link).click
      $ie.link(:text, prd_inst_name).click    
      #$ie.wait
      frequencyOfInstallments= installments_frequency.split(",").to_a	
      
      if frequencyOfInstallments[0].to_s == "weeks"
        periods = "week"
      elsif frequencyOfInstallments[0].to_s == "months"
        periods = "month"
      end
      
      # if($ie.contains_text("Product instance name: " + prd_inst_name.to_s)\
      #   and $ie.contains_text("Short name: " + short_name.to_s)\
      #   and $ie.contains_text("Product category: " + prd_category.to_s)\
      #   and $ie.contains_text("Start date: " + @@current_date.to_s)\
      #    and $ie.contains_text("Applicable for: " + appl_for.to_s)\
      #    and $ie.contains_text("Min loan amount: " + min_ammount.to_f.to_s)\
      #    and $ie.contains_text("Max loan amount: " + max_ammount.to_f.to_s)\
      #    and $ie.contains_text("Default amount: " + def_ammount.to_f.to_s)\
      #    and $ie.contains_text("Interest rate type: " + interestrate_type.to_s)\
      #    and $ie.contains_text("Max interest rate: " + max_interestrate.to_f.to_s + " %")\
      #    and $ie.contains_text("Min interest rate: " + min_interestrate.to_f.to_s + " %")\
      #    and $ie.contains_text("Default interest rate: " + def_interestrate.to_f.to_s + " %")\
      #    and $ie.contains_text("Frequency of installments: " + frequencyOfInstallments[1].to_s + "  " + periods.to_s + "(s)")\
      #    and $ie.contains_text("Max # of installments: " + max_installments.to_i.to_s)\
      #    and $ie.contains_text("Min # of installments: " + min_installments.to_i.to_s)\
      #   and $ie.contains_text("Default # of installments: " + def_installments.to_i.to_s)\
      #    and $ie.contains_text("Interest: " + interest_glcode.to_s)\
      #    and $ie.contains_text("Principal: " + principal_glcode.to_s)\
      #    and $ie.contains_text("Penalties: " + penalities_glcode.to_s))
      #       $logger.log_results("LoanProduct- validating the preview page for new LoanProduct", "Click view LoanProduct link", "Valid preview page content", "Passed")
      # else
      #       $logger.log_results("LoanProduct- validating the preview page for new LoanProduct", "Click view LoanProduct link","Valid preview page content", "Failed")
      #   end   
      # puts "########## In view prod ####################"
      assert_on_page(@loanprd_properties['product.prodinstname']+": " + prd_inst_name.to_s)
      assert_on_page(@loanprd_properties['product.shortname']+": " + short_name.to_s)
      assert_on_page(@loanprd_properties['product.prodcat']+": " + prd_category.to_s)
      assert_on_page(@loanprd_properties['product.startdate']+": " + @@current_date.to_s)
      assert_on_page(@loanprd_properties['product.applfor']+": " + appl_for.to_s)
      assert_on_page(@min_loan_amount_label+": " + min_ammount.to_f.to_s)
      assert_on_page(@max_loan_amount_label+": " + max_ammount.to_f.to_s)
      assert_on_page(@loanprd_properties['product.defamt']+": " + def_ammount.to_f.to_s)
      assert_on_page(@service_charge_ratetype+": " + interestrate_type.to_s)
      assert_on_page(@max_service_charge+": " + max_interestrate.to_f.to_s + " %")
      assert_on_page(@min_service_charge+": " + min_interestrate.to_f.to_s + " %")
      assert_on_page(@def_service_charge+": " + def_interestrate.to_f.to_s + " %")
      assert_on_page(@loanprd_properties['product.freqofinst']+": " + frequencyOfInstallments[1].to_s + " " + periods.to_s + "(s)") 
      assert_on_page(@loanprd_properties['product.maxinst']+": " + max_installments.to_i.to_s)      
      assert_on_page(@loanprd_properties['product.mininst']+": " + min_installments.to_i.to_s)
      assert_on_page(@loanprd_properties['product.definst']+": " + def_installments.to_i.to_s)
      assert_on_page(@loanprd_properties['product.principal']+": " + principal_glcode.to_s)
      #assert_on_page(@loanprd_properties['product.penalties']+" : " + penalities_glcode.to_s) 
      #  puts "########### LEave prod ##############"
    rescue =>excp
      quit_on_error(excp)        
      
    end 
  end 
  
  # Check for the status change from active to inactive and then inactive to active and subsequnetly
  #check it in change log
  #1 is for active loan product and 4 is for inactive loan product
  def check_status(prd_inst_name)
    begin
      rowcount=2
      #puts "in check status"
      edit_loanproduct_status(prd_inst_name, @inactive_label)
      #commented as change log link has been removed currently
      change_log(prd_inst_name,"1","4",rowcount)
      #$ie.button(:value,@back_ro_details_page_button).click 
      rowcount+=1
      edit_loanproduct_status(prd_inst_name, @active_label)  
      change_log(prd_inst_name,"4","1",rowcount)
      rowcount+=1
      #$ie.button(:value,@back_ro_details_page_button).click
      $ie.link(:text,@admin_link).click
    rescue =>excp
      quit_on_error(excp)     
    end  
  end
  
  # Change the LoanProduct status
  def  edit_loanproduct_status(prd_inst_name, status)
    begin    
      $ie.link(:text,@edit_loanproduct_link).click
      #puts "in check status: " + status.to_s
      assert($ie.contains_text(prd_inst_name.to_s + " - "+@edit_loanproduct_link))
      $logger.log_results("LoanProduct- Edit user information", "click on Edit user information","Edit page should be opened","Passed")
      $ie.select_list(:name,"prdStatus").select(status)
      $ie.button(:value,@preview_button).click              
      if($ie.contains_text(@status+" : " + status.to_s))
        $logger.log_results("LoanProduct- Edit LoanProduct information", "status change","Preview page with changed status","Passed")        
      else
        $logger.log_results("LoanProduct- Edit LoanProduct information", "status change","Preview page with changed status","Failed")
      end
      $ie.button(:value,@submit_button).click              
      verify_status_change(prd_inst_name, status)    
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Edit LoanProduct information", "click on Edit LoanProduct information","Edit page should be opened","Failed")
    rescue =>excp
      quit_on_error(excp)         
    end      
  end 
  
  def change_log(prd_inst_name,old_status,new_status,rowcount)
    $ie.link(:text,@view_changelog_link).click
    begin
      assert($ie.contains_text(prd_inst_name+" - "+@change_log_label))
      $logger.log_results("navigated to change log page of "+prd_inst_name,"NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("could not navigate to change log page of "+prd_inst_name,"NA","NA","failed")
    rescue =>excp
      quit_on_error(excp)       
    end
    table_obj=$ie.table(:index,11)
    begin
      assert_equal(table_obj[rowcount][3].text,old_status)
      $logger.log_results("value in change log for old value are same ",old_status.to_s,table_obj[rowcount][3].text,"passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("value in change log for old value are different",old_status.to_s,table_obj[rowcount][3].text,"failed")
    rescue =>excp
      quit_on_error(excp)         
    end
    begin
      assert_equal(table_obj[rowcount][4].text,new_status)
      $logger.log_results("value in change log for new value are same ",new_status.to_s,table_obj[rowcount][4].text,"passed")    
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("value in change log for new value are different",new_status.to_s,table_obj[rowcount][4].text,"failed")
    rescue =>excp
      quit_on_error(excp)    
    end
  end
  
  # verifies the changed status   
  def verify_status_change(prd_inst_name, status)    
    begin
      assert($ie.contains_text(status)) and assert($ie.contains_text(prd_inst_name.to_s))
      $logger.log_results("LoanProduct- Edit LoanProduct information", "status change","Preview page with changed status","Passed")       
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("LoanProduct- Edit LoanProduct information", "status change","Preview page with changed status","Failed")
    rescue =>excp
      quit_on_error(excp)    
    end   
  end 
  
  
end  



class LoanProduct_Main
  loan_obj = LoanProduct_Test_Cases.new
  loan_obj.mifos_login
  loan_obj.read_properties()
  loan_obj.read_hash_values()
  loan_obj.get_labels_from_db
  loan_obj.Click_Admin_page
  loan_obj.Check_LoanProduct_Links
  loan_obj.Check_New_LoanProduct_cancel
  
  filename = File.expand_path(File.dirname($PROGRAM_NAME))+"/data/Loan_Data.xls"
  
  #Check for mandatory field validation messages  
  #This section deals with the all validation message and error handling 
  loan_obj.open(filename, 2)
  count = 0
  rowid = -1    
  while(rowid < $maxrow * $maxcol - 1)
    loan_obj.read_product_values(rowid)
    loan_obj.check_validation_error(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Description,loan_obj.Prd_category,loan_obj.Start_date,\
    loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
    loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
    loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
    loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)  
    
    loan_obj.Click_Admin_page
    rowid+=$maxcol
  end
  loan_obj.open(filename, 1)
  
  rowid = -1
  
  while(rowid < $maxrow * $maxcol - 1)
    loan_obj.read_product_values(rowid)
    loan_obj.Click_Admin_page
    loan_obj.Create_New_LoanProduct(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Prd_category,loan_obj.Start_date,\
                                    loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
                                    loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
                                    loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
                                    loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)
    
    loan_obj.check_editdata_onpreview(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Prd_category,loan_obj.Start_date,\
                                      loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
                                      loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
                                      loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
                                      loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)
    
    loan_obj.check_view_loanproduct(loan_obj.Prd_inst_name,loan_obj.Short_name,loan_obj.Prd_category,loan_obj.Start_date,\
                                    loan_obj.Applied_for,loan_obj.Min_amt,loan_obj.Max_amt,loan_obj.Default_amt,loan_obj.Interest_rate_type,\
                                    loan_obj.Max_interest_rate,loan_obj.Min_interest_rate,loan_obj.Default_interest_rate,\
                                    loan_obj.Installment_freq,loan_obj.Max_installment,loan_obj.Min_installment,\
                                    loan_obj.Default_installment,loan_obj.Interest_glcode,loan_obj.Principal_glcode,loan_obj.Penalty_glcode)
    
    loan_obj.check_status(loan_obj.Prd_inst_name)      
    
    rowid+=$maxcol
  end 
  loan_obj.mifos_logout()
end