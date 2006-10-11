# This file contains all the automation test case scenario for testing the saving product module

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

class SavingProduct_Test_Cases < TestClass
  
  #method to read values from the array once these are dumped from the file
  def read_user_values(rowid)
    @prd_inst_name = arrval[rowid+=1].to_s
    # User details
    @short_name = arrval[rowid+=1].to_s
    @descrip = arrval[rowid+=1].to_s
    @descrip = check_value(@descrip)
    @prd_category = arrval[rowid+=1].to_s
    @start_date = arrval[rowid+=1].to_s  
    @end_date = arrval[rowid+=1].to_s
    @appl_for = arrval[rowid+=1].to_s    
    @deposit_type = arrval[rowid+=1].to_s
    # Ammount details
    @deposit_amount = arrval[rowid+=1].to_s  
    @deposit_amount = check_value(@deposit_amount)  
    @appliesto_amount = arrval[rowid+=1].to_s
    @appliesto_amount = check_value(@appliesto_amount)
    @max_amount_per_withdrawal = arrval[rowid+=1].to_s
    @max_amount_per_withdrawal = check_value(@max_amount_per_withdrawal)
    # Interest details
    @interest_rate = arrval[rowid+=1].to_s
    @balance_interestcalc = arrval[rowid+=1].to_s
    @time_interestcalc = arrval[rowid+=1].to_i.to_s
    @interest_postingfrequency = arrval[rowid+=1].to_i.to_s
    # Installment details
    @min_balance_interestrate = arrval[rowid+=1].to_s
    @min_balance_interestrate = check_value(@min_balance_interestrate)
    @deposit_glcode = arrval[rowid+=1].to_i.to_s     
    @interest_glcode = arrval[rowid+=1].to_i.to_s 
  end
  
  def Prd_instance_name
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
  
  def Applicable_for
    @appl_for
  end
  
  def Deposit_type
    @deposit_type
  end
  
  def Deposit_amount
    @deposit_amount
  end
  
  def Applies_to
    @appliesto_amount
  end
  
  def Max_amt_per_withdrawal
    @max_amount_per_withdrawal
  end
  
  def Interest_rate
    @interest_rate
  end
  
  def Balance_interest_calc
    @balance_interestcalc
  end
  
  def Time_interest_calc
    @time_interestcalc
  end
  
  def Interest_posting_frequency
    @interest_postingfrequency
  end
  
  def Min_balance_interest_rate
    @min_balance_interestrate 
  end
  
  def Deposit_glcode
    @deposit_glcode
  end
  
  def Interest_glcode
    @interest_glcode
  end
  
  #read properties file into a hash and then use the hash for labels,buttons etc
  def read_properties()
    @admin_properties=load_properties("modules/propertis/adminUIResources.properties")
    @savingprd_properties=load_properties("modules/propertis/ProductDefinitionResources.properties")
  end
  #read values from the hash into variables
  def read_hash_values()
    #label
    dbquery("select LOOKUP_VALUE from LOOKUP_VALUE_LOCALE l where l.LOOKUP_VALUE_ID=109")
    @savings_label=dbresult[0]
    dbquery("select LOOKUP_VALUE from LOOKUP_VALUE_LOCALE l where l.LOOKUP_VALUE_ID=215")
    @mandatory_label=dbresult[0]
    dbquery("select LOOKUP_VALUE from LOOKUP_VALUE_LOCALE l where l.LOOKUP_VALUE_ID=217")
    @voluntary_label=dbresult[0]
    dbquery("select LOOKUP_VALUE from LOOKUP_VALUE_LOCALE l where l.LOOKUP_VALUE_ID=395")
    @interest_label=dbresult[0]
    @service_charge_rate_label=string_replace_message(@savingprd_properties['product.intrate'],@savingprd_properties['product.interest'],@interest_label)
    @balance_usedfor_service_charge_label=string_replace_message(@savingprd_properties['product.balusedforinstcalcrule'],@savingprd_properties['product.interest'],@interest_label)
    @timeperiod_service_charge_label=@savingprd_properties['product.timeper']+" "+@interest_label+" "+@savingprd_properties['product.calc']
    @frequency_service_charge_label=@savingprd_properties['product.freq']+" "+@interest_label+" "+@savingprd_properties['product.postacc']
    @glcode_service_charge_label=@savingprd_properties['product.Glcodefor']+" "+@interest_label
    @status=@savingprd_properties['product.status']
    @change_log_label=string_replace_message(@savingprd_properties['product.changelog'],@savingprd_properties['product.savingsview'],"").squeeze(" ")
    @edit_saving_product_label=@savingprd_properties['produt.edit']+" "+@savings_label.to_s+" "+@savingprd_properties['product.productinfo']
    #links
    @view_savingproduct_link=string_replace_message(@admin_properties['admin.viewsavprds'],$savings,@savings_label.to_s)
    @new_savingproduct_link=string_replace_message(@admin_properties['admin.defnewsavprd'],$savings,@savings_label.to_s)
    @edit_savingproduct_link=@savingprd_properties['produt.edit']+" "+@savings_label.to_s+" "+@savingprd_properties['product.info']
    
    @view_changelog_link=@savingprd_properties['product.changelog']
    #buttons
    @cancel_button=@savingprd_properties['product.cancel']
    @preview_button=@savingprd_properties['product.preview']
    @submit_button=@savingprd_properties['product.butsubmit']
    #messages
    @add_savingproduct_msg=@savingprd_properties['product.addnew']+" "+@savings_label.to_s+" "+@savingprd_properties['product.product']
    @enter_savingproduct_msg=@savingprd_properties['product.enter']+" "+@savings_label+" "+@savingprd_properties['product.productinfo']
    @review_submit=string_replace_message(@savingprd_properties['product.review'],"amp;","")      #removing amp; from the string to get "review & submit" as properties file has it as "review &amp; submit" 
    @product_instance_msg=string_replace_message(@savingprd_properties['errors.mandatory'],"{0}",@savingprd_properties['product.prodinstname'])
    @shortname_msg=string_replace_message(@savingprd_properties['errors.mandatory'],"{0}",@savingprd_properties['product.shortname'])
    @category_msg=string_replace_message(@savingprd_properties['errors.select'],"{0}",@savingprd_properties['product.prodcat'])
    @applicable_for_msg=string_replace_message(@savingprd_properties['errors.select'],"{0}",@savingprd_properties['product.applfor'])
    @type_of_deposit_msg=string_replace_message(@savingprd_properties['errors.select'],"{0}",@savingprd_properties['product.typeofdep'])
    @interest_rate_msg=string_replace_message(@savingprd_properties['errors.mandatory'],"{0}",@savingprd_properties['product.intrate'])
    @balance_used_for_interest_msg=string_replace_message(@savingprd_properties['errors.select'],"{0}",@savingprd_properties['product.balusedforinstcalcrule'])
    @time_period_for_interest_msg=string_replace_message(@savingprd_properties['errors.mandatory'],"{0}",@savingprd_properties['product.timeperinstcalc'])
    @frequency_interest_msg=string_replace_message(@savingprd_properties['errors.mandatory'],"{0}",@savingprd_properties['product.freqinstpostacc'])
    @deposit_glcode_msg=string_replace_message(@savingprd_properties['errors.select'],"{0}",@savingprd_properties['product.glcodedep'])
    @interest_glcode_msg=string_replace_message(@savingprd_properties['errors.select'],"{0}",@savingprd_properties['product.glcodeint'])
    @amount_applies_to_msg=string_replace_message(@savingprd_properties['errors.select'],"{0}",@savingprd_properties['product.recamtappl'])
    @valid_interest_rate_msg=@savingprd_properties['errors.intRate']
    @mandatory_deposit_amount_msg=@savingprd_properties['errors.mandAmount']
    @valid_deposit_amount_msg=string_replace_message(@savingprd_properties['errors.decimalFormat'],"{0}",@savingprd_properties['product.mandamntdep'])
    @valid_maxamount_per_withdrawal_msg=string_replace_message(@savingprd_properties['errors.decimalFormat'],"{0}",@savingprd_properties['product.maxamtwid'])
    @min_balance_interest_msg=string_replace_message(@savingprd_properties['errors.decimalFormat'],"{0}",@savingprd_properties['product.minbalinstcalc'])
    @margin_money_success_msg=@savingprd_properties['product.marginmoneysuccess']+" "+@savings_label+" "+@savingprd_properties['product.product']
    @create_saving_product_msg=@savingprd_properties['product.savingsdefinenew']+" "+@savings_label+" "+@savingprd_properties['product.product']
    @view_saving_product_details_msg=@savingprd_properties['product.savingsview']+" "+@savings_label+" "+@savingprd_properties['product.savingprod']
    
    #max length error messages
    @instancename_maxlength_msg=string_replace_message(@savingprd_properties['errors.maximumlength'],"{0}",@savingprd_properties['product.prodinstname'])
    @instancename_maxlength_msg=string_replace_message(@instancename_maxlength_msg,"{1}","50")
    @description_maxlength_msg=string_replace_message(@savingprd_properties['errors.maximumlength'],"{0}",@savingprd_properties['product.categorydesc'])
    @description_maxlength_msg=string_replace_message(@description_maxlength_msg,"{1}","200")
    
    #duplicate error messages
    @duplicate_instance_name_msg=@savingprd_properties['errors.duplprdinstname']
    @duplicate_shortname_msg=@savingprd_properties['errors.duplprdinstshortname']
    
    #error message when there are spaces in short name  
    @errormessage_space=string_replace_message(@savingprd_properties['errors.shortnamemask'],"{0}",@savingprd_properties['product.shortname'])
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
  # Check savingproduct link on admin page after login
  def Check_SavingProduct_Links()
    begin    
      assert($ie.contains_text(@view_savingproduct_link)) and assert($ie.contains_text(@new_savingproduct_link))
      $logger.log_results("savingproduct links "+@view_savingproduct_link+" and "+@new_savingproduct_link +" exist on admin page", "NA","links should be present","Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("savingproduct links "+@view_savingproduct_link+" and "+@new_savingproduct_link +" do not exist on  admin page", "NA","links should be present","Failed")
    rescue =>excp
      quit_on_error(excp)        
    end
  end
  
  # Check cancel in 'Define new savingproduct' link on admin page
  def Check_New_SavingProduct_cancel()
    begin
      $ie.link(:text, @new_savingproduct_link).click
      $ie.button(:value,@cancel_button).click     
      verify_admin_page()      
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Checks "Add a new Margin Money Product - Enter Margin Money Product information " string on the landing page of new savings product \
  #creation
  def Check_New_SavingProduct()
    begin   
      assert_on_page(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)
    rescue =>excp
      quit_on_error(excp)      
    end      
  end
  
  # Check for all mandatory validation error
  def check_validation_error(prd_inst_name, short_name,description, prd_category, start_date, appl_for,appliesto_amount, deposit_type,deposit_amount,
    max_amount_per_withdrawal,interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency,min_balance_interestrate, deposit_glcode, interest_glcode)
    begin
         
      $ie.link(:text, @new_savingproduct_link).click  
            
      Check_New_SavingProduct()
      
      Man_New_SavingProduct()
      
      Man_New_SavingProduct_with_PrdInstName(prd_inst_name)
      
      Man_New_SavingProduct_with_ShortName(short_name)
      
      Man_New_SavingsProduct_with_Desc(description)
      
      Man_New_SavingProduct_with_PrdCategory(prd_category)
      
      Man_New_SavingProduct_with_ApplFor(appl_for,appliesto_amount)
      
      Man_New_SavingProduct_with_DepositType(deposit_type,deposit_amount)
      
      Man_New_SavingProduct_with_MaxAmtperWithdrawl(max_amount_per_withdrawal)
      
      Man_New_SavingProduct_with_InterestRate(interest_rate)
      
      Man_New_SavingProduct_with_BalanceInterestCalc(balance_interestcalc)
      
      Man_New_SavingProduct_with_TimeInterestCalc(time_interestcalc)
      
      Man_New_SavingProduct_with_InterestPostingFrequency(interest_postingfrequency)
      
      Man_New_SavingProduct_with_MinBalance(min_balance_interestrate)
      
      Man_New_SavingProduct_with_DepositGLcode(deposit_glcode)
      
      Man_New_SavingProduct_with_InterestGLcode(interest_glcode)
      
      if $ie.contains_text(@add_savingproduct_msg+" - "+@review_submit)
        
        Man_dupl_Saving_productname_shortname(prd_inst_name,short_name)
        
      end
    end   
  end 
  
  # Check for validation messages while creating new SavingProduct
  def Man_New_SavingProduct()
    begin  
      $ie.button(:value,@preview_button).click
      assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and \
      assert($ie.contains_text(@product_instance_msg)) and\
      assert($ie.contains_text(@shortname_msg)) and assert($ie.contains_text(@category_msg)) and \
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@type_of_deposit_msg)) and \
      assert($ie.contains_text(@interest_rate_msg)) and assert($ie.contains_text(@balance_used_for_interest_msg)) and \
      assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg))and \
      assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "nothing ","All validation error message","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "nothing ","All validation error message","Failed")       
    rescue =>excp
      quit_on_error(excp)     
    end    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name
  def   Man_New_SavingProduct_with_PrdInstName(prd_inst_name)
    begin
      set_value_txtfield("prdOfferingName", prd_inst_name)
      $ie.button(:value,@preview_button).click
      max_field_len(prd_inst_name,50,@instancename_maxlength_msg)
      assert(!$ie.contains_text(@product_instance_msg)) and \
      assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and \
      assert($ie.contains_text(@shortname_msg)) and assert($ie.contains_text(@category_msg)) and \
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@type_of_deposit_msg)) and \
      assert($ie.contains_text(@interest_rate_msg)) and assert($ie.contains_text(@balance_used_for_interest_msg)) and \
      assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg)) and \
      assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "product instance name","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "product instance name","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)       
    end    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name
  def  Man_New_SavingProduct_with_ShortName(short_name)
    begin
      #set_value_txtfield("prdOfferingName", prd_inst_name)
      set_value_txtfield("prdOfferingShortName", short_name)
      $ie.button(:value,@preview_button).click
      verify_ShortName(short_name)
      
      assert(!$ie.contains_text(@shortname_msg)) and assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and assert($ie.contains_text(@category_msg)) and \
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@type_of_deposit_msg)) and \
      assert($ie.contains_text(@interest_rate_msg)) and assert($ie.contains_text(@balance_used_for_interest_msg)) and \
      assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg)) and \
      assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "short_name","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "short_name ","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)    
    end    
  end
  
  
  def Man_New_SavingsProduct_with_Desc(description)
    
    set_value_txtfield("description",description)
    $ie.button(:value,@preview_button).click
    max_field_len(description,200,@description_maxlength_msg)
  end
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category
  def   Man_New_SavingProduct_with_PrdCategory(prd_category)
    begin
      #set_value_txtfield("prdOfferingName", prd_inst_name)
      #set_value_txtfield("prdOfferingShortName", short_name)
      set_value_selectlist("prdCategory", prd_category)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@category_msg)) and assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and \
      assert($ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@type_of_deposit_msg)) and \
      assert($ie.contains_text(@interest_rate_msg)) and assert($ie.contains_text(@balance_used_for_interest_msg)) and \
      assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg)) and \
      assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "product category","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "product category ","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)       
    end    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category, appl_for
  def    Man_New_SavingProduct_with_ApplFor(appl_for,appliesto_amount)
    begin
      
      set_value_selectlist("prdApplicableMaster", appl_for)
      $ie.button(:value,@preview_button).click
      if appl_for==@savingprd_properties['product.groups']
        begin
          assert($ie.contains_text(@amount_applies_to_msg))
          $logger.log_results("Error message appears when Applicable for selected is "+appl_for.to_s+" and amount applies to is not selected",appl_for.to_s,@amount_applies_to_msg,"passed")
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Error message does not appear when Applicable for selected is "+appl_for.to_s+" and amount applies to is not selected",appl_for.to_s,@amount_applies_to_msg,"failed")
       rescue =>excp
          quit_on_error(excp)              
        end
        set_value_selectlist("recommendedAmntUnit", appliesto_amount)
        $ie.button(:value,@preview_button).click
        $ie.wait()
        begin
          assert(!$ie.contains_text(@amount_applies_to_msg))
          $logger.log_results("No error message appears when Applicable for selected is "+appl_for.to_s+" and Amount applies to selected is "+appliesto_amount.to_s,appl_for.to_s+","+appliesto_amount.to_s,"No error message","passed")     
        rescue Test::Unit::AssertionFailedError=>e
          $logger.log_results("Error message appears when Applicable for selected is "+appl_for.to_s+" and Amount applies to selected is "+appliesto_amount.to_s,appl_for.to_s+","+appliesto_amount.to_s,"No error message","failed")             
        rescue =>excp
          quit_on_error(excp)         
        end
      end
      begin
        assert(!$ie.contains_text(@applicable_for_msg)) and assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and \
        assert($ie.contains_text(@type_of_deposit_msg)) and \
        assert($ie.contains_text(@interest_rate_msg)) and assert($ie.contains_text(@balance_used_for_interest_msg)) and \
        assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg)) and \
        assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
        $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "applicable for","All validation error message for other mandatory fields","Passed")     
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "applicable for","All validation error message for other mandatory fields","Failed")     
      rescue =>excp
        quit_on_error(excp)           
      end    
    end
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, deposit_type
  def Man_New_SavingProduct_with_DepositType(deposit_type,deposit_amount)
    begin
      
      set_value_selectlist("savingsType", deposit_type)
      
      $ie.button(:value,@preview_button).click
      set_value_txtfield("recommendedAmount", deposit_amount)
      $ie.button(:value,@preview_button).click
      if (@mandatory_label == deposit_type.to_s)
        
        if deposit_amount.to_f==0 
          verify_ValidRecommendedAmount(deposit_type,deposit_amount)
        end
        verify_maxRecommendedAmount(deposit_type,deposit_amount) 
        
      elsif (deposit_type.to_s==@voluntary_label)
        # set_value_txtfield("recommendedAmount", deposit_amount)
        verify_maxRecommendedAmount(deposit_type,deposit_amount)
      end
      
      assert(!$ie.contains_text(@type_of_deposit_msg)) and \
      assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and \
      assert($ie.contains_text(@interest_rate_msg)) and assert($ie.contains_text(@balance_used_for_interest_msg)) and \
      assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg)) and \
      assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "deposit type","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "deposit type","All validation error message for other mandatory fields","Failed") 
    rescue =>excp
      quit_on_error(excp)   
    end    
      
  end
  
  def Man_New_SavingProduct_with_MaxAmtperWithdrawl(max_amount_per_withdrawal)
    begin
      set_value_txtfield("maxAmntWithdrawl",max_amount_per_withdrawal)
      $ie.button(:value,@preview_button).click
      verify_MaxAmountPerWithdrawal(max_amount_per_withdrawal) 
    end
    
  end
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, deposit_type, interest_rate
  def    Man_New_SavingProduct_with_InterestRate(interest_rate)
    begin
      
      set_value_txtfield("interestRate", interest_rate)     
      
      $ie.button(:value,@preview_button).click
      verify_InterestRate(interest_rate) 
      assert(!$ie.contains_text(@interest_rate_msg)) and assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg))\
      and assert($ie.contains_text(@balance_used_for_interest_msg))\
      and assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg))\
      and assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "interest rate","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "interest rate","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)   
    end    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, deposit_type, interest_rate, balance_interestcalc
  def  Man_New_SavingProduct_with_BalanceInterestCalc(balance_interestcalc)
    begin
      
      set_value_selectlist("interestCalcType", balance_interestcalc)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@balance_used_for_interest_msg)) and  assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg))\
      and assert($ie.contains_text(@time_period_for_interest_msg)) and assert($ie.contains_text(@frequency_interest_msg))\
      and assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "balance used for interest rate calculation","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "balance used for interest rate calculation","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)   
    end    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc
  def   Man_New_SavingProduct_with_TimeInterestCalc(time_interestcalc)
    begin
      set_value_txtfield("timeForInterestCacl", time_interestcalc)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@time_period_for_interest_msg)) and \
      assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and \
      assert($ie.contains_text(@frequency_interest_msg)) and\
      assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "time period for interest rate calculation","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "time period for interest rate calculation","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)   
    end    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency
  
  def   Man_New_SavingProduct_with_InterestPostingFrequency(interest_postingfrequency)
    begin
      
      set_value_txtfield("freqOfInterest", interest_postingfrequency)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@frequency_interest_msg)) and assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg))\
      and assert($ie.contains_text(@deposit_glcode_msg)) and assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "frequency of interest posting to account","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "frequency of interest posting to account","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)   
    end    
  end
  
  
  def  Man_New_SavingProduct_with_MinBalance(min_balance_interestrate)
    
    set_value_txtfield("minAmntForInt",min_balance_interestrate)
    $ie.button(:value,@preview_button).click
    verify_MinBalanceInterestrate(min_balance_interestrate) 
    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency, 
  #  deposit_glcode
  
  
  def  Man_New_SavingProduct_with_DepositGLcode(deposit_glcode)
    begin
      
      set_value_selectlist("depositGLCode", deposit_glcode)
      
      $ie.button(:value,@preview_button).click
      
      assert(!$ie.contains_text(@deposit_glcode_msg)) and \
      assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg)) and \
      assert($ie.contains_text(@interest_glcode_msg))
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "deposit glcode","All validation error message for other mandatory fields","Passed")     
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "deposit glcode","All validation error message for other mandatory fields","Failed")     
    rescue =>excp
      quit_on_error(excp)   
    end    
  end
  
  # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
  #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency, 
  #  deposit_glcode, interest_glcode
  def  Man_New_SavingProduct_with_InterestGLcode(interest_glcode)
    begin
      set_value_selectlist("interestGLCode", interest_glcode)
      
      $ie.button(:value,@preview_button).click
      assert(!$ie.contains_text(@interest_glcode_msg))    
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "Interest glcode", "Navigated to preview page", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "Interest glcode","Navigated to preview page","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  #method to check duplicate product instance name and short name
  def Man_dupl_Saving_productname_shortname(prd_inst_name,short_name)
    
    number_of_products=count_items("select count(PRD_OFFERING_NAME) count from PRD_OFFERING where PRD_OFFERING_NAME='"+prd_inst_name+"'")
    number_of_shortname=count_items("select count(PRD_OFFERING_SHORT_NAME) count from PRD_OFFERING where PRD_OFFERING_SHORT_NAME='"+short_name+"'")
    if ((number_of_products.to_i > 0) and (number_of_shortname.to_i > 0))
      $ie.button(:value,@submit_button).click
      begin 
        assert($ie.contains_text(@duplicate_instance_name_msg)) and \
        assert($ie.contains_text(@duplicate_shortname_msg))
        $logger.log_results("Error message appears for duplicate product instance name",prd_inst_name.to_s,@duplicate_instance_name_msg,"passed")
        $logger.log_results("Error message appears for duplicate product instance short name",short_name.to_s,@duplicate_shortname_msg,"passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear when both instance name and short name already exist",prd_inst_name.to_s+","+short_name.to_s,@duplicate_instance_name_msg+" and "+@duplicate_shortname_msg,"failed")      
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
      $ie.button(:value,@submit_button).click
    end
  end
  
  
  
  # Check for all mandatory validation error
  def check_field_validation_error(
    prd_inst_name, short_name, deposit_type, deposit_amount, interest_rate, max_amount_per_withdrawal,
    min_balance_interestrate)
    
    begin
      Click_Admin_page()
      $ie.link(:text, @new_savingproduct_link).click       
      Check_New_SavingProduct()      
      verify_PrdInstName(prd_inst_name)      
      verify_ShortName(short_name)
      # the fuction call below is commented due to the failure of current interest rate validation
      # verify_InterestRate(interest_rate)
      verify_ValidRecommendedAmount(deposit_type)
      verify_maxRecommendedAmount(deposit_type, deposit_amount)
      verify_MaxAmountPerWithdrawal(max_amount_per_withdrawal)
      verify_MinBalanceInterestrate(min_balance_interestrate)
      $ie.button(:value, @cancel_button).click 
    rescue =>excp
      quit_on_error(excp)
    end
  end
  
  # Check for field validation messages while creating new SavingProduct with prd_inst_name
  def verify_PrdInstName(prd_inst_name)
    begin
      set_value_txtfield("prdOfferingName", prd_inst_name)      
      $ie.button(:value,@preview_button).click
      
      assert($ie.contains_text(@instancename_maxlength_msg))      
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name", "All validation error message", "Passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name","All validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  # Check for validation messages while creating new SavingProduct with short_name
  def verify_ShortName(short_name) 
    
    b=(short_name =~ /\s+/)
    if (b!=nil) 
      begin
        # set_value_txtfield("prdOfferingShortName", short_name)      
        # $ie.button(:value,@preview_button).click
        #puts "value of b is "+b.to_s
        assert($ie.contains_text(@errormessage_space))      
        $logger.log_results("SavingProduct- Error message appears when short_name contains space", short_name, @errormessage_space, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct- Error message did not appear when short_name contains space", short_name,@errormessage_space,"Failed")
      rescue =>excp
        quit_on_error(excp)   
      end
    else
      begin
        assert(!$ie.contains_text(@errormessage_space))
        $logger.log_results("SavingProduct- Error message does not appear when short_name does not contain space",short_name,"No error message","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct- Error message appears when short_name does not contain space",short_name,"No error message","failed")
      rescue =>excp
        quit_on_error(excp)   
      end 
    end
  end 
  
  # Check for validation messages while creating new SavingProduct with interest_rate
  def verify_InterestRate(interest_rate)
    if (interest_rate.to_f > 100)
      begin
        
        assert($ie.contains_text(@valid_interest_rate_msg))
        $logger.log_results("SavingProduct- Error message appears when interest rate is more than max value", interest_rate.to_s,@valid_interest_rate_msg , "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct- Error message does not appear when interest rate is more than max value", interest_rate.to_s,@valid_interest_rate_msg , "failed")
      rescue =>excp
        quit_on_error(excp)   
      end
    else
      begin 
        #assert(!$ie.contains_text($savingproduct_valid_interestrate_msg))      
        assert(!$ie.contains_text(@valid_interest_rate_msg))
        $logger.log_results("SavingProduct-No Error message  appears when interest rate is less than max value",interest_rate.to_s,"No error message","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct-Error message appears when interest rate is less than max value",interest_rate.to_s,"No error message","failed")
      rescue =>excp
        quit_on_error(excp)   
      end
    end
  end
  # Check for validation messages while creating new SavingProduct with deposit_type
  def verify_ValidRecommendedAmount(deposit_type,deposit_amount)
    if (deposit_amount.to_f==0.0)
      begin
        assert($ie.contains_text(@mandatory_deposit_amount_msg))      
        $logger.log_results("SavingProduct- Error message appears when mandatory deposit amount is 0", deposit_amount.to_s,@mandatory_deposit_amount_msg , "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct- Error message does not appear when mandatory deposit amount is 0", deposit_amount.to_s,@mandatory_deposit_amount_msg , "failed")
      rescue =>excp
        quit_on_error(excp)   
      end
    else
      begin
        assert(!$ie.contains_text(@mandatory_deposit_amount_msg))      
        $logger.log_results("SavingsProduct- No error message appears when mandatory deposit amount is not 0",deposit_amount.to_s,"No error message","passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingsProduct- error message appears when mandatory deposit amount is not 0",deposit_amount.to_s,"No error message","failed")    
      rescue =>excp
        quit_on_error(excp)   
      end
    end
  end
  
  # Check for validation messages while creating new SavingProduct with deposit_type, deposit_amount
  def verify_maxRecommendedAmount(deposit_type, deposit_amount)
    if (deposit_amount.to_f >9999999.9)
      begin
        assert($ie.contains_text(@valid_deposit_amount_msg))      
        $logger.log_results("SavingProduct-Error message appears when "+deposit_type+" deposit amount is greater than max value", deposit_amount.to_s,@valid_deposit_amount_msg , "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct-Error message does not appear when "+deposit_type+" deposit amount is greater than max value", deposit_amount.to_s,@valid_deposit_amount_msg , "failed")
      rescue =>excp
        quit_on_error(excp)   
      end
    else
      begin
        assert(!$ie.contains_text(@valid_deposit_amount_msg))      
        $logger.log_results("SavingProduct-No Error message appears when "+deposit_type+" deposit amount is less than max value", deposit_amount.to_s,"No error message", "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct-Error message appears when "+deposit_type+" deposit amount is less than max value", deposit_amount.to_s,"No error message", "failed")    
      rescue =>excp
        quit_on_error(excp)   
      end
    end
  end
  
  # Check for validation messages while creating new SavingProduct with deposit_type, deposit_amount
  def verify_MaxAmountPerWithdrawal(max_amount_per_withdrawal)
    if (max_amount_per_withdrawal.to_f > 9999999.9)
      begin
        #  set_value_txtfield("maxAmntWithdrawl", max_amount_per_withdrawal)
        #  $ie.button(:value,@preview_button).click
        assert($ie.contains_text(@valid_maxamount_per_withdrawal_msg))      
        $logger.log_results("SavingProduct-Error message appears when max amount per withdrawal is greater than max value", max_amount_per_withdrawal.to_s,@valid_maxamount_per_withdrawal_msg, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct-Error message does not appear when max amount per withdrawal is greater than max value", max_amount_per_withdrawal.to_s,@valid_maxamount_per_withdrawal_msg, "failed")
      rescue =>excp
        quit_on_error(excp)   
      end
    else
      begin
        assert(!$ie.contains_text(@valid_maxamount_per_withdrawal_msg))   
        $logger.log_results("SavingProduct-No Error message appears when max amount per withdrawal is less than max value", max_amount_per_withdrawal.to_s,"No error message", "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct-Error message appears when max amount per withdrawal is less than max value", max_amount_per_withdrawal.to_s,"No error message", "failed")     
      rescue =>excp
        quit_on_error(excp)   
      end
    end
  end
  # Check for validation messages while creating new SavingProduct with min_balance_interestrate
  def verify_MinBalanceInterestrate(min_balance_interestrate)
    if (min_balance_interestrate.to_f >9999999.9)
      begin
        #  set_value_txtfield("minAmntForInt", min_balance_interestrate)
        #  $ie.button(:value,@preview_button).click
        assert($ie.contains_text(@min_balance_interest_msg))      
        $logger.log_results("SavingProduct- Error message appears when minimum balance for service charge greater than max value", min_balance_interestrate.to_s, @min_balance_interest_msg, "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct- Error message does not  appear when minimum balance for service charge greater than max value", min_balance_interestrate.to_s, @min_balance_interest_msg, "failed")
      rescue =>excp
        quit_on_error(excp)   
      end
    else
      begin
        assert(!$ie.contains_text(@min_balance_interest_msg))      
        $logger.log_results("SavingProduct-No Error message appears when minimum balance for service charge is less than max value", min_balance_interestrate.to_s, "No error message", "Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct-Error message appears when minimum balance for service charge is less than max value", min_balance_interestrate.to_s, "No error message", "failed")    
      rescue =>excp
        quit_on_error(excp)   
      end   
    end
  end
  
  # checks for the unique SavingProduct name functionality
  def create_SavingProduct_duplicate_name(
    prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    begin
      Click_Admin_page()
      $ie.link(:text, @new_savingproduct_link).click
      Check_New_SavingProduct()     
      set_SavingProduct_data(
      prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
      time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
      
      $ie.button(:value,@preview_button).click
      validate_SavingProduct_preview_page(
      prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
      time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
      
      $ie.button(:value,@submit_button).click
      assert($ie.contains_text(@duplicate_instance_name_msg)) 
      $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct name",
      "existing SavingProduct name","Name validation error message","Passed")
      create_SavingProduct_duplicate_shortname(prd_inst_name, short_name)
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct name",
      "existing SavingProduct name","Name validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  def create_SavingProduct_duplicate_shortname(prd_inst_name, short_name)
    begin        
      $ie.button(:value, @edit_savingproduct_link).click
      Check_New_SavingProduct()
      set_value_txtfield("prdOfferingName", prd_inst_name +  "mifos123")
      set_value_txtfield("prdOfferingShortName", short_name)
      $ie.button(:value, @preview_button).click
      
      $ie.button(:value,@submit_button).click
      assert($ie.contains_text(@duplicate_shortname_msg)) 
      $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct short name",
      "existing SavingProduct short name","Short Name validation error message","Passed")
      $ie.button(:value,@cancel_button).click
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct short name",
      "existing SavingProduct shortname","Short Name validation error message","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  
  # Creates a new SavingProduct and verify the preview page and database values after submission 
  def Create_New_SavingProduct(
    prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    
    begin
      
      set_SavingProduct_data(
      prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
      time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
      $ie.button(:value, @preview_button).click
      
      validate_SavingProduct_preview_page(
      prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
      time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
      
      $ie.button(:value,@submit_button).click
      validate_SavingProduct_creation(
      prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
      time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
      SavingProduct_Creation_Conformation()
    end
  end
  
  # Check the edit data link on the preview page
  def check_editdata_onpreview(
    prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    
    set_SavingProduct_data(
    prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    
    $ie.button(:value,@preview_button).click  
    
    validate_SavingProduct_preview_page(
    prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    
    $ie.button(:name, "edit").click
    begin    		
      assert($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg))
      $logger.log_results("SavingProduct ", "Click on "+@edit_savingproduct_link+" button" ,"In Margin Money Product creation page","Passed")
      $ie.button(:value,@preview_button).click
      validate_SavingProduct_preview_page(
      prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
      time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
      $ie.button(:value,@cancel_button).click
      
      verify_admin_page()         
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct ", "Click on "+@edit_savingproduct_link+" button" ,"In Margin Money Product creation page","failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  # Enter the new SavingProduct data 
  def set_SavingProduct_data(prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
                             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    $ie.link(:text,@savingprd_properties['product.admin']).click
    $ie.link(:text, @new_savingproduct_link).click  
    begin
      if($ie.contains_text(@add_savingproduct_msg+" - "+@enter_savingproduct_msg))
        $logger.log_results("SavingProduct- Create new SavingProduct page", "Click on 'Define a new SavingProduct' link","Access to the new SavingProduct page","Passed")
      else
        $logger.log_results("SavingProduct- Create new SavingProduct page", "Click on admin 'Define a new SavingProduct' link","Access to the new SavingProduct page","Failed")
      end
      
      set_value_txtfield("prdOfferingName", prd_inst_name)
      set_value_txtfield("prdOfferingShortName", short_name)
      set_value_selectlist("prdCategory", prd_category)
      set_value_selectlist("prdApplicableMaster", appl_for)
      if appl_for==@savingprd_properties['product.groups']
        set_value_selectlist("recommendedAmntUnit", appliesto_amount)
    	
      end
      set_value_selectlist("savingsType", deposit_type)
      set_value_txtfield("recommendedAmount", deposit_amount)
      
      
      set_value_txtfield("interestRate", interest_rate)
      set_value_selectlist("interestCalcType", balance_interestcalc)
      set_value_txtfield("timeForInterestCacl", time_interestcalc)
      set_value_txtfield("freqOfInterest", interest_postingfrequency)
      
      set_value_selectlist("depositGLCode", deposit_glcode)
      set_value_selectlist("interestGLCode", interest_glcode)
    rescue =>excp
      quit_on_error(excp)   
    end
  end
  
  
  # Validate the preview for new check list  
  def  validate_SavingProduct_preview_page(
    prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    
    begin    
      if $ie.contains_text(@add_savingproduct_msg+" - "+@review_submit) then
        $logger.log_results("New SavingProduct- Preview", "new SavingProduct inputs", "valid preview page","Passed")
      else 
        $logger.log_results("New SavingProduct- Preview", "new SavingProduct inputs" , "valid preview page","Failed")
      end
      
      dbquery("select date_format(sysdate(),'%d/%m/%Y')")
      @@current_date = dbresult[0]
      
      # if ($ie.contains_text("Product instance name : " + prd_inst_name.to_s)\
      #    and $ie.contains_text("Short name : " + short_name.to_s)\
      #    and $ie.contains_text("Product category : " + prd_category.to_s)\
      #    and $ie.contains_text("Start date : " + @@current_date.to_s)\
      #    and $ie.contains_text("Applicable for : " + appl_for.to_s)\
      #    and $ie.contains_text("Type of deposits : " + deposit_type.to_s)\
      #    and $ie.contains_text("Mandatory amount for deposit: " + deposit_amount.to_f.to_s)\
      #    and $ie.contains_text("Interest rate : " + interest_rate.to_f.to_s + " %")\
      #    and $ie.contains_text("Balance used for Interest calculation : " + balance_interestcalc.to_s)\
      #    and $ie.contains_text("Time period for interest calculation : " + time_interestcalc.to_i.to_s + " month(s)")\
      #    and $ie.contains_text("Frequency of interest posting to accounts : " + interest_postingfrequency.to_i.to_s + " month(s)" )\
      #    and $ie.contains_text("GL code for deposits : " + deposit_glcode.to_s)\
      #    and $ie.contains_text("GL code for interest : " + interest_glcode.to_s)\
      #           )       
      #   $logger.log_results("SavingProduct- validating the preview page", "Click on preview button","Valid preview page content", "Passed")
      # else
      #   $logger.log_results("SavingProduct- validating the preview page", "Click on preview button","Valid preview page content", "Failed")
      # end
      assert_on_page(@savingprd_properties['product.prodinstname']+" : " + prd_inst_name.to_s)
      assert_on_page(@savingprd_properties['product.shortname']+" : " +short_name.to_s)
      assert_on_page(@savingprd_properties['product.prodcat']+" : " + prd_category.to_s)
      assert_on_page(@savingprd_properties['product.startdate']+" : " + @@current_date.to_s)
      assert_on_page(@savingprd_properties['product.applfor']+" : " + appl_for.to_s)
      if appl_for==@savingprd_properties['product.groups']
        assert_on_page(@savingprd_properties['product.recamtappl']+" : " + appliesto_amount.to_s)
      end
      assert_on_page(@savingprd_properties['product.typeofdep']+": " + deposit_type.to_s)
      if deposit_type==@mandatory_label
        assert_on_page(@savingprd_properties['product.mandamntdep']+": " + deposit_amount.to_f.to_s)
      elsif deposit_type==@voluntary_label
        assert_on_page(@savingprd_properties['product.recamtdep']+": " + deposit_amount.to_f.to_s)
      end
      assert_on_page(@service_charge_rate_label+" : " + interest_rate.to_f.to_s + " %")
      assert_on_page(@balance_usedfor_service_charge_label+" : " + balance_interestcalc.to_s)
      assert_on_page(@timeperiod_service_charge_label+" : " + time_interestcalc.to_i.to_s + " Day(s)")          
      assert_on_page(@frequency_service_charge_label+" : " + interest_postingfrequency.to_i.to_s + " month(s)")
      assert_on_page(@savingprd_properties['product.glcodedep']+" : " + deposit_glcode.to_s)
      assert_on_page(@glcode_service_charge_label+" : " + interest_glcode.to_s)
    end 
  end  
  
  # Verify the creation of new SavingProduct from the data base  
  def    validate_SavingProduct_creation(
    prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate, balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    
    begin
      
      # Fetch the data from database and store the values in the corresponding variables
      dbquery("select PRD_OFFERING_ID, SAVINGS_TYPE_ID, RECOMMENDED_AMOUNT, INTEREST_CALCULATION_TYPE_ID, INTEREST_RATE, DEPOSIT_GLCODE_ID, INTEREST_GLCODE_ID from savings_offering where PRD_OFFERING_ID = (select  max(PRD_OFFERING_ID) from savings_offering)")
      prd_offering_id = dbresult[0]      
      deposit_type_id = dbresult[1]
      db_deposit_amount = dbresult[2]
      
      interestcalc_type_id = dbresult[3]
      db_interest_rate = dbresult[4]
      
      db_deposit_glcode_id = dbresult[5]
      db_interest_glcode_id = dbresult[6]
      
      
      dbquery("select PRD_APPLICABLE_MASTER_ID, PRD_CATEGORY_NAME, date_format(START_DATE,'%d/%m/%Y'), PRD_OFFERING_NAME, PRD_OFFERING_SHORT_NAME, OFFERING_STATUS_ID, date_format(pof.CREATED_DATE,'%d/%m/%Y')from prd_offering  pof, prd_category pc where pof.PRD_OFFERING_ID =" + prd_offering_id.to_s + " and pof.PRD_CATEGORY_ID = pc.PRD_CATEGORY_ID and pof.PRD_TYPE_ID = pc.PRD_TYPE_ID")
      
      db_applicable_id = dbresult[0]
      db_prd_category = dbresult[1]      
      db_start_date = dbresult[2]
      
      db_prd_inst_name = dbresult[3]
      db_short_name = dbresult[4]            
      
      db_status_id = dbresult[5]
      db_created_date = dbresult[6]
      
      dbquery("select LOOKUP_VALUE from lookup_value_locale where LOOKUP_ID = (select LOOKUP_ID from prd_applicable_master where PRD_APPLICABLE_MASTER_ID =" + db_applicable_id.to_s + " )")
      db_apply_for = dbresult[0]
      
      dbquery("select LOOKUP_VALUE from lookup_value_locale where LOOKUP_ID = (select LOOKUP_ID from savings_type where SAVINGS_TYPE_ID = " + deposit_type_id.to_s + ")")
      db_deposit_type = dbresult[0]
      
      dbquery("select LOOKUP_VALUE from lookup_value_locale where LOOKUP_ID = (select INTEREST_CALCULATION_LOOKUP_ID from interest_calculation_types where INTEREST_CALCULATION_TYPE_ID = " + interestcalc_type_id.to_s + ")")
      db_balance_interestcalc = dbresult[0]
      
      
      dbquery("select RECUR_AFTER from recurrence_detail where MEETING_ID = (select PRD_MEETING_ID from prd_offering_meeting where PRD_OFFERING_ID = " + prd_offering_id.to_s + " and PRD_OFFERING_MEETING_TYPE_ID= 2) and RECURRENCE_ID = 3")
      db_time_interestcalc = dbresult[0]
      
      dbquery("select RECUR_AFTER from recurrence_detail where MEETING_ID = (select PRD_MEETING_ID from prd_offering_meeting where PRD_OFFERING_ID = " + prd_offering_id.to_s + " and PRD_OFFERING_MEETING_TYPE_ID= 3) and RECURRENCE_ID = 2")
      db_interest_postingfrequency = dbresult[0]
      
      dbquery("select GLCODE_VALUE from gl_code where GLCODE_ID =" + db_deposit_glcode_id.to_s )
      db_deposit_glcode = dbresult[0]
      
      dbquery("select GLCODE_VALUE from gl_code where GLCODE_ID =" + db_interest_glcode_id.to_s )
      db_interest_glcode = dbresult[0]
      
      # if(db_prd_inst_name.to_s ==  prd_inst_name.to_s\
      #   and db_short_name.to_s ==  short_name.to_s\
      #   and db_prd_category.to_s ==  prd_category.to_s\
      #   and db_apply_for.to_s ==  appl_for.to_s\
      #   and db_start_date.to_s ==  @@current_date.to_s\
      #   and db_created_date.to_s ==  @@current_date.to_s\
      #   and db_interest_rate.to_f == interest_rate.to_f\
      #   and db_deposit_type.to_s == deposit_type.to_s\
      #   and db_deposit_amount.to_f == deposit_amount.to_f\
      #   and db_balance_interestcalc.to_s == db_balance_interestcalc.to_s\
      #   and db_time_interestcalc.to_i ==  db_time_interestcalc.to_i\
      #   and db_interest_postingfrequency.to_i ==  db_interest_postingfrequency.to_i\
      #   and db_interest_glcode.to_s ==  interest_glcode.to_s\
      #   and db_deposit_glcode.to_s ==  deposit_glcode.to_s\
      #   and 2 == db_status_id.to_i)
      #   $logger.log_results("SavingProduct- validating the SavingProduct creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Passed")
      # else
      #   $logger.log_results("SavingProduct- validating the SavingProduct creation", "fetching the values from the database","The fetched values should be matched with the entered values", "Failed")
      # end 
      dbcheck("Product instance name",prd_inst_name,db_prd_inst_name) 
      dbcheck("Product instance short name",short_name,db_short_name) 
      dbcheck("Product category",prd_category,db_prd_category) 
      dbcheck("Applicable for",appl_for,db_apply_for) 
      dbcheck("Start Date",@@current_date,db_start_date) 
      dbcheck("Created Date",@@current_date,db_created_date) 
      dbcheck("Service charge",interest_rate.to_f,db_interest_rate.to_f) 
      dbcheck("Deposit type",deposit_type,db_deposit_type) 
      dbcheck("Deposit Amount",deposit_amount.to_f,db_deposit_amount.to_f) 
      dbcheck("Balance for service charge calculation",balance_interestcalc,db_balance_interestcalc) 
      dbcheck("Time for service charge calculation",time_interestcalc,db_time_interestcalc) 
      dbcheck("Frequency of service charge posting to accounts",interest_postingfrequency,db_interest_postingfrequency) 
      dbcheck("Interest GL code",interest_glcode,db_interest_glcode) 
      dbcheck("Deposit GL code",deposit_glcode,db_deposit_glcode) 
      dbcheck("Status ","2",db_status_id) 
    end 
  end  
  
  #Check for the SavingProductCreation-Conformation page
  def SavingProduct_Creation_Conformation()
    begin

      assert($ie.contains_text(@margin_money_success_msg))and \
      assert($ie.contains_text(@create_saving_product_msg)) and \
      assert($ie.contains_text(@view_saving_product_details_msg))
      $logger.log_results("SavingProduct- Margin Money Product created", "NA","The page should redirect to Margin Money ProductCreation-Conformation page","Passed")
      $ie.link(:text, @create_saving_product_msg).click
      Check_New_SavingProduct()
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Margin Money Product created", "NA","The page should redirect to Margin Money ProductCreation-Conformation page","Failed")     
    rescue =>excp
      quit_on_error(excp)   
    end
  end      
  
  # Mandatory check for view check list
  def check_view_savingproduct(
    prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
    time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    begin
      Click_Admin_page()
      $ie.link(:text, @view_savingproduct_link).click
      $ie.link(:text, prd_inst_name).click    
      $ie.wait
      
      #   if ($ie.contains_text("Product instance name: " + prd_inst_name.to_s)\
      #        and $ie.contains_text("Short name: " + short_name.to_s)\
      #        and $ie.contains_text("Product category: " + prd_category.to_s)\
      #        and $ie.contains_text("Start date: " + @@current_date.to_s)\
      #        and $ie.contains_text("Applicable for: " + appl_for.to_s)\
      #        and $ie.contains_text("Type of deposits: " + deposit_type.to_s)\
      #        and $ie.contains_text("Mandatory amount for deposit: " + deposit_amount.to_f.to_s)\
      #        and $ie.contains_text("Service Charge rate : " + interest_rate.to_f.to_s + " %")\
      #        and $ie.contains_text("Balance used for Service Charge calculation : " + balance_interestcalc.to_s)\
      #        and $ie.contains_text("Time period for Service Charge calculation : " + time_interestcalc.to_i.to_s + " Day(s)")\
      #        and $ie.contains_text("Frequency of Service Charge posting to accounts : " + interest_postingfrequency.to_i.to_s + " month(s)" )\
      #        and $ie.contains_text("GL code for deposits: " + deposit_glcode.to_s)\
      #        and $ie.contains_text("GL code for interest: " + interest_glcode.to_s)\
      #      )
      #         $logger.log_results("SavingProduct- validating the preview page for new SavingProduct", "Click view SavingProduct link", "Valid preview page content", "Passed")
      #   else
      #         $logger.log_results("SavingProduct- validating the preview page for new SavingProduct", "Click view SavingProduct link","Valid preview page content", "Failed")
      #     end           
      assert_on_page(@savingprd_properties['product.prodinstname']+": " + prd_inst_name.to_s)
      assert_on_page(@savingprd_properties['product.shortname']+": " +short_name.to_s)
      assert_on_page(@savingprd_properties['product.prodcat']+": " + prd_category.to_s)
      assert_on_page(@savingprd_properties['product.startdate']+": " + @@current_date.to_s)
      assert_on_page(@savingprd_properties['product.applfor']+": " + appl_for.to_s)
      if appl_for==@savingprd_properties['product.groups']
        assert_on_page(@savingprd_properties['product.recamtappl']+": " + appliesto_amount.to_s)
      end
       assert_on_page(@savingprd_properties['product.typeofdep']+": " + deposit_type.to_s)
      if deposit_type==@mandatory_label
        assert_on_page(@savingprd_properties['product.mandamntdep']+": " + deposit_amount.to_f.to_s)
      elsif deposit_type==@voluntary_label
        assert_on_page(@savingprd_properties['product.recamtdep']+": " + deposit_amount.to_f.to_s)
      end
      assert_on_page(@service_charge_rate_label+": " + interest_rate.to_f.to_s + " %")
      assert_on_page(@balance_usedfor_service_charge_label+": " + balance_interestcalc.to_s)
      assert_on_page(@timeperiod_service_charge_label+" : " + time_interestcalc.to_i.to_s + " Day(s)")          
      assert_on_page(@frequency_service_charge_label+" : " + interest_postingfrequency.to_i.to_s + " month(s)")
      assert_on_page(@savingprd_properties['product.glcodedep']+": " + deposit_glcode.to_s)
      assert_on_page(@glcode_service_charge_label+": " + interest_glcode.to_s)
    end 
  end 
  
  # Check for the status change from active to inactive and then inactive to active
  #commented change log link as it is removed for the time being
  def check_status(prd_inst_name)
    rowcount=2
    edit_savingproduct_status(prd_inst_name, @inactive_label)
    change_log(prd_inst_name,"2","5",rowcount) #change log shows 2 for active and 5 for inactive
    rowcount+=1
    $ie.button(:value,@savingprd_properties['product.back']).click
    edit_savingproduct_status(prd_inst_name, @active_label)  
    change_log(prd_inst_name,"5","2",rowcount)
    rowcount+=1
    $ie.button(:value,@savingprd_properties['product.back']).click   
    $ie.link(:text,@savingprd_properties['product.admin']).click
  end
  
  # Change the SavingProduct status
  def  edit_savingproduct_status(prd_inst_name, status)
    begin    
      $ie.link(:text,@edit_savingproduct_link).click
      begin
        assert($ie.contains_text(prd_inst_name.to_s + " - "+@edit_saving_product_label))
        $logger.log_results("SavingProduct- Edit Margin Money Product information ", "click on Edit Margin Money Product information","Edit page should be opened","Passed")
      rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct- Edit Margin Money Product information", "click on Edit Margin Money Product information ","Edit page should be opened","Failed")
      end
      $ie.select_list(:name,"status").select(status)
      $ie.button(:value,@preview_button).click              
      if($ie.contains_text( @status+" : " + status.to_s))
        $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Passed")        
      else
        $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Failed")
      end
      $ie.button(:value,@submit_button).click              
      verify_status_change(prd_inst_name, status)    
    
    rescue =>excp
      quit_on_error(excp)   
    end      
  end 
  #clicks on view change log link and checks the values under old value and new value once status is changed
  def change_log(prd_inst_name,old_status,new_status,rowcount)

    begin
      $ie.link(:text,@view_changelog_link).click
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
      $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Passed")       
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Failed")
    rescue =>excp
      quit_on_error(excp)   
    end
  end 
  
  
end  


class SavingProduct_Main
  saving_obj = SavingProduct_Test_Cases.new
  saving_obj.mifos_login
  saving_obj.read_properties()
  saving_obj.read_hash_values()
  saving_obj.get_labels_from_db
  saving_obj.Click_Admin_page
  saving_obj.Check_SavingProduct_Links
  saving_obj.Check_New_SavingProduct_cancel
  filename = File.expand_path( File.dirname($PROGRAM_NAME))+"/data/Saving_Data.xls"
  
  #Check for mandatory field validation messages only once  
  #This section deals with the all validation message and error handling 
  saving_obj.open(filename, 2)
  count = 0
  rowid = -1
  
  
  
  while(rowid < $maxrow * $maxcol - 1)
    saving_obj.read_user_values(rowid)
    saving_obj.check_validation_error(saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Description,saving_obj.Prd_category, saving_obj.Start_date, saving_obj.Applicable_for,
    saving_obj.Applies_to,saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Max_amt_per_withdrawal,saving_obj.Interest_rate, saving_obj.Balance_interest_calc,saving_obj.Time_interest_calc,
    saving_obj.Interest_posting_frequency,saving_obj.Min_balance_interest_rate,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)
    
    rowid+=$maxcol
    saving_obj.Click_Admin_page()
  end
  
  #This section deals with the all validation message and error handling
  
  saving_obj.open(filename, 1)
  count = 0
  rowid = -1
  
  while(rowid < $maxrow * $maxcol - 1)
    saving_obj.read_user_values(rowid)
    saving_obj.Click_Admin_page
    saving_obj.Create_New_SavingProduct(
    saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Prd_category,saving_obj.Applicable_for,saving_obj.Applies_to,
    saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Interest_rate,saving_obj.Balance_interest_calc,
    saving_obj.Time_interest_calc,saving_obj.Interest_posting_frequency,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)
    
    saving_obj.check_editdata_onpreview(
    saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Prd_category,saving_obj.Applicable_for,saving_obj.Applies_to,
    saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Interest_rate,saving_obj.Balance_interest_calc,
    saving_obj.Time_interest_calc,saving_obj.Interest_posting_frequency,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)
    
    
    saving_obj.check_view_savingproduct( 
    saving_obj.Prd_instance_name,saving_obj.Short_name,saving_obj.Prd_category,saving_obj.Applicable_for,saving_obj.Applies_to,
    saving_obj.Deposit_type,saving_obj.Deposit_amount,saving_obj.Interest_rate,saving_obj.Balance_interest_calc,
    saving_obj.Time_interest_calc,saving_obj.Interest_posting_frequency,saving_obj.Deposit_glcode,saving_obj.Interest_glcode)
    
    saving_obj.check_status(saving_obj.Prd_instance_name)      
    
    
    rowid+=$maxcol
  end 
  saving_obj.mifos_logout()
end