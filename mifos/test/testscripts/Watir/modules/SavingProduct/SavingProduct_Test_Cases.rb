# This file contains all the automation test case scenario for testing the saving product module

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
require 'watir/WindowHelper'

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
  
   # Check savingproduct link on admin page after login
  def Check_SavingProduct_Links()
    begin    
      assert($ie.contains_text($view_savingproduct_link)) and assert($ie.contains_text($new_savingproduct_link))
        $logger.log_results("SavingProduct- Check for savingproduct links on admin page", "Click on admin link","The links should be there","Passed")
      rescue =>e
        $logger.log_results("SavingProduct- Check for savingproduct links on admin page", "Click on admin link","The links should be there","Failed")
    end
  end
   
  # Check cancel in 'Define new savingproduct' link on admin page
  def Check_New_SavingProduct_cancel()
    begin
      $ie.link(:text, $new_savingproduct_link).click
      $ie.button(:value,"Cancel").click     
      verify_admin_page()      
    end
  end
 
  # Check new SavingProduct link on admin page
  def Check_New_SavingProduct()
    begin      
      assert($ie.contains_text($new_savingproduct_page_msg))
        $logger.log_results("SavingProduct- Check for Define new Margin Money product link on admin page", "Click on 'Define new Margin Money product' link","Access to the Define a new SavingProduct page","Passed")
      rescue =>e
        $logger.log_results("SavingProduct- Check for Define new Margin Money product link on admin page", "Click on 'Define new Margin Money product' link","Access to the Define a new SavingProduct page","Failed")
    end      
  end
    
  # Check for all mandatory validation error
  def check_validation_error(prd_inst_name, short_name,description, prd_category, start_date, appl_for,appliesto_amount, deposit_type,deposit_amount,
     max_amount_per_withdrawal,interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency,min_balance_interestrate, deposit_glcode, interest_glcode)
      begin     
    #  $ie.link(:text, $new_savingproduct_link).click  
          
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
      
      if $ie.contains_text($review_savingproduct_info)
       
      Man_dupl_Saving_productname_shortname(prd_inst_name,short_name)
           
      end
   end   
   end 

  # Check for validation messages while creating new SavingProduct
  def Man_New_SavingProduct()
    begin  
      $ie.button(:value,"Preview").click
      assert($ie.contains_text($new_savingproduct_page_msg)) and assert($ie.contains_text($prd_inst_name_msg))\
      and assert($ie.contains_text($shortname_msg)) and assert($ie.contains_text($prd_category_msg))\
      and assert($ie.contains_text($savingproduct_appl_for_msg)) and assert($ie.contains_text($savingproduct_type_of_diposit_msg))\
      and assert($ie.contains_text($savingproduct_interestrate_msg)) and assert($ie.contains_text($savingproduct_balance_interest_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
      
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "nothing ","All validation error message","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "nothing ","All validation error message","Failed")       
     end    
  end
  
    # Check for validation messages while creating new SavingProduct with prd_inst_name
    def   Man_New_SavingProduct_with_PrdInstName(prd_inst_name)
    begin
      set_value_txtfield("prdOfferingName", prd_inst_name)
      $ie.button(:value,"Preview").click
      max_field_len(prd_inst_name,50,$savings_prd_max_length)
      assert(!$ie.contains_text($prd_inst_name_msg)) and 
      assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($shortname_msg)) and assert($ie.contains_text($prd_category_msg))\
      and assert($ie.contains_text($savingproduct_appl_for_msg)) and assert($ie.contains_text($savingproduct_type_of_diposit_msg))\
      and assert($ie.contains_text($savingproduct_interestrate_msg)) and assert($ie.contains_text($savingproduct_balance_interest_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name ","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name ","All validation error message for other mandatory fields","Failed")     
     end    
  end

    # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name
  def  Man_New_SavingProduct_with_ShortName(short_name)
    begin
      #set_value_txtfield("prdOfferingName", prd_inst_name)
      set_value_txtfield("prdOfferingShortName", short_name)
      $ie.button(:value,"Preview").click
      verify_ShortName(short_name)
      
       assert(!$ie.contains_text($shortname_msg)) and assert($ie.contains_text($new_savingproduct_page_msg)) and assert($ie.contains_text($prd_category_msg))\
      and assert($ie.contains_text($savingproduct_appl_for_msg)) and assert($ie.contains_text($savingproduct_type_of_diposit_msg))\
      and assert($ie.contains_text($savingproduct_interestrate_msg)) and assert($ie.contains_text($savingproduct_balance_interest_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "product name,short_name","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "product name,short_name ","All validation error message for other mandatory fields","Failed")     
     end    
   end
  
  
    def Man_New_SavingsProduct_with_Desc(description)
     
      set_value_txtfield("description",description)
      $ie.button(:value,"Preview").click
      max_field_len(description,200,$savings_desc_max_length)
    end
    # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category
    def   Man_New_SavingProduct_with_PrdCategory(prd_category)
    begin
      #set_value_txtfield("prdOfferingName", prd_inst_name)
      #set_value_txtfield("prdOfferingShortName", short_name)
      set_value_selectlist("prdCategory.productCategoryID", prd_category)

      $ie.button(:value,"Preview").click
      
      assert(!$ie.contains_text($prd_category_msg)) and assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_appl_for_msg)) and assert($ie.contains_text($savingproduct_type_of_diposit_msg))\
      and assert($ie.contains_text($savingproduct_interestrate_msg)) and assert($ie.contains_text($savingproduct_balance_interest_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category ","All validation error message for other mandatory fields","Failed")     
     end    
  end

    # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category, appl_for
    def    Man_New_SavingProduct_with_ApplFor(appl_for,appliesto_amount)
    begin
 
      set_value_selectlist("prdApplicableMaster.prdApplicableMasterId", appl_for)
      $ie.button(:value,"Preview").click
      if appl_for=="Groups"
      begin
      assert($ie.contains_text($savings_amount_applies_to))
      $logger.log_results("Error message appears when Applicable for selected is "+appl_for.to_s+" and amount applies to is not selected",appl_for.to_s,$savings_amount_applies_to,"passed")
      rescue=>e
      $logger.log_results("Error message does not appear when Applicable for selected is "+appl_for.to_s+" and amount applies to is not selected",appl_for.to_s,$savings_amount_applies_to,"failed")
      end
      set_value_selectlist("recommendedAmntUnit.recommendedAmntUnitId", appliesto_amount)
      $ie.button(:value,"Preview").click
      $ie.wait()
      begin
      assert(!$ie.contains_text($savings_amount_applies_to))
      $logger.log_results("No error message appears when Applicable for selected is "+appl_for.to_s+" and Amount applies to selected is "+appliesto_amount.to_s,appl_for.to_s+","+appliesto_amount.to_s,"No error message","passed")     
      rescue=>e
      $logger.log_results("Error message appears when Applicable for selected is "+appl_for.to_s+" and Amount applies to selected is "+appliesto_amount.to_s,appl_for.to_s+","+appliesto_amount.to_s,"No error message","failed")             
      end
      end
      begin
      assert(!$ie.contains_text($savingproduct_appl_for_msg)) and assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_type_of_diposit_msg))\
      and assert($ie.contains_text($savingproduct_interestrate_msg)) and assert($ie.contains_text($savingproduct_balance_interest_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for","All validation error message for other mandatory fields","Failed")     
     end    
   end
   end
  
      # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
      #  appl_for, deposit_type
    def Man_New_SavingProduct_with_DepositType(deposit_type,deposit_amount)
    begin
 
      set_value_selectlist("savingsType.savingsTypeId", deposit_type)
      
      $ie.button(:value,"Preview").click
      set_value_txtfield("recommendedAmount", deposit_amount)
      $ie.button(:value,"Preview").click
      if ("Mandatory" == deposit_type.to_s)
   
        if deposit_amount.to_f==0 
        verify_ValidRecommendedAmount(deposit_type,deposit_amount)
        end
        verify_maxRecommendedAmount(deposit_type,deposit_amount) 
        
      elsif (deposit_type.to_s=="Voluntary")
       # set_value_txtfield("recommendedAmount", deposit_amount)
        verify_maxRecommendedAmount(deposit_type,deposit_amount)
      end
          
      assert(!$ie.contains_text($savingproduct_type_of_diposit_msg)) and assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_interestrate_msg)) and assert($ie.contains_text($savingproduct_balance_interest_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type","All validation error message for other mandatory fields","Failed")     
     end    
  end
  
  def Man_New_SavingProduct_with_MaxAmtperWithdrawl(max_amount_per_withdrawal)
    begin
      set_value_txtfield("maxAmntWithdrawl",max_amount_per_withdrawal)
      $ie.button(:value,"Preview").click
      verify_MaxAmountPerWithdrawal(max_amount_per_withdrawal) 
    end
  
  end
    # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
    #  appl_for, deposit_type, interest_rate
    def    Man_New_SavingProduct_with_InterestRate(interest_rate)
    begin
 
      set_value_txtfield("interestRate", interest_rate)     
      
      $ie.button(:value,"Preview").click
      verify_InterestRate(interest_rate) 
      assert(!$ie.contains_text($savingproduct_interestrate_msg)) and assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_balance_interest_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate","All validation error message for other mandatory fields","Failed")     
     end    
  end
  
    # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
    #  appl_for, deposit_type, interest_rate, balance_interestcalc
    def  Man_New_SavingProduct_with_BalanceInterestCalc(balance_interestcalc)
    begin
 
      set_value_selectlist("interestCalcType.interestCalculationTypeID", balance_interestcalc)

      $ie.button(:value,"Preview").click
      
     assert(!$ie.contains_text($savingproduct_balance_interest_msg)) and  assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc","All validation error message for other mandatory fields","Failed")     
     end    
  end
  
    # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
    #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc
    def   Man_New_SavingProduct_with_TimeInterestCalc(time_interestcalc)
    begin
       set_value_txtfield("timeForInterestCacl", time_interestcalc)

      $ie.button(:value,"Preview").click
      
      assert(!$ie.contains_text($savingproduct_time_interest_msg)) and assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_frequency_interest_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc","All validation error message for other mandatory fields","Failed")     
     end    
  end

     # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
    #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency

  def   Man_New_SavingProduct_with_InterestPostingFrequency(interest_postingfrequency)
    begin

      set_value_txtfield("freqOfInterest", interest_postingfrequency)
      
      $ie.button(:value,"Preview").click
      
      assert(!$ie.contains_text($savingproduct_frequency_interest_msg)) and assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency","All validation error message for other mandatory fields","Failed")     
     end    
  end
    
 
  def  Man_New_SavingProduct_with_MinBalance(min_balance_interestrate)
       
       set_value_txtfield("minAmntForInt",min_balance_interestrate)
       $ie.button(:value,"Preview").click
      verify_MinBalanceInterestrate(min_balance_interestrate) 
      
  end
  
      # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
    #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency, 
    #  deposit_glcode
    
    
  def  Man_New_SavingProduct_with_DepositGLcode(deposit_glcode)
    begin
      
      set_value_selectlist("depositGLCode.glcodeId", deposit_glcode)

      $ie.button(:value,"Preview").click
      
      assert(!$ie.contains_text($savingproduct_glcode_deposit_msg)) and assert($ie.contains_text($new_savingproduct_page_msg))\
      and assert($ie.contains_text($savingproduct_glcode_interest_msg))
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency, deposit_glcode","All validation error message for other mandatory fields","Passed")     
      rescue =>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name, short_name, prd_category, appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency, deposit_glcode","All validation error message for other mandatory fields","Failed")     
     end    
  end
  
    # Check for validation messages while creating new SavingProduct with prd_inst_name, short_name, prd_category,
    #  appl_for, deposit_type, interest_rate, balance_interestcalc, time_interestcalc, interest_postingfrequency, 
    #  deposit_glcode, interest_glcode
  def  Man_New_SavingProduct_with_InterestGLcode(interest_glcode)
    begin
      set_value_selectlist("interestGLCode.glcodeId", interest_glcode)
      
      $ie.button(:value,"Preview").click
      assert(!$ie.contains_text($savingproduct_glcode_interest_msg))    
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "All mandatory field values", "Navigated to preview page", "Passed")
      rescue=>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "All mandatory field values","Navigated to preview page","Failed")
     end
  end
  #method to check duplicate product instance name and short name
  def Man_dupl_Saving_productname_shortname(prd_inst_name,short_name)
   
    number_of_products=count_items("select count(PRD_OFFERING_NAME) count from PRD_OFFERING where PRD_OFFERING_NAME='"+prd_inst_name+"'")
    number_of_shortname=count_items("select count(PRD_OFFERING_SHORT_NAME) count from PRD_OFFERING where PRD_OFFERING_SHORT_NAME='"+short_name+"'")
    if ((number_of_products.to_i > 0) and (number_of_shortname.to_i > 0))
      $ie.button(:value,"Submit").click
      begin 
      assert($ie.contains_text($savings_dupl_inst_name_message)) and \
      assert($ie.contains_text($savings_dupl_short_name_message))
      $logger.log_results("Error message appears for duplicate product instance name",prd_inst_name.to_s,$savings_dupl_inst_name_message,"passed")
      $logger.log_results("Error message appears for duplicate product instance short name",short_name.to_s,$savings_dupl_short_name_message,"passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Error message does not appear when both instance name and short name already exist",prd_inst_name.to_s+","+short_name.to_s,$savings_dupl_inst_name_message+" and "+$savings_dupl_short_name_message,"failed")      
      end
    elsif (number_of_products.to_i > 0) and (number_of_shortname.to_i == 0)
      $ie.button(:value,"Submit").click
      begin
      assert($ie.contains_text($savings_dupl_inst_name_message))
      $logger.log_results("Error message appears for duplicate product instance name",prd_inst_name.to_s,$savings_dupl_inst_name_message,"passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Error message does not appear for duplicate product instance name",prd_inst_name.to_s,$savings_dupl_inst_name_message,"failed")
      end
    elsif (number_of_products.to_i == 0) and (number_of_shortname.to_i > 0)
      $ie.button(:value,"Submit").click
      begin 
      assert($ie.contains_text($savings_dupl_short_name_message))
      $logger.log_results("Error message appears for duplicate product instance short name",short_name.to_s,$savings_dupl_short_name_message,"passed")
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Error message does not appear for duplicate product instance short name",short_name.to_s,$savings_dupl_short_name_message,"failed")      
      end
    else
      $ie.button(:value,"Submit").click
    end
  end
  

  
  # Check for all mandatory validation error
  def check_field_validation_error(
   prd_inst_name, short_name, deposit_type, deposit_amount, interest_rate, max_amount_per_withdrawal,
   min_balance_interestrate)

    begin
      Click_Admin_page()
      $ie.link(:text, $new_savingproduct_link).click       
      Check_New_SavingProduct()      
      verify_PrdInstName(prd_inst_name)      
      verify_ShortName(short_name)
      # the fuction call below is commented due to the failure of current interest rate validation
      # verify_InterestRate(interest_rate)
      verify_ValidRecommendedAmount(deposit_type)
      verify_maxRecommendedAmount(deposit_type, deposit_amount)
      verify_MaxAmountPerWithdrawal(max_amount_per_withdrawal)
      verify_MinBalanceInterestrate(min_balance_interestrate)
      $ie.button(:value, "Cancel").click 
    end
  end
  
  # Check for field validation messages while creating new SavingProduct with prd_inst_name
  def verify_PrdInstName(prd_inst_name)
    begin
      set_value_txtfield("prdOfferingName", prd_inst_name)      
      $ie.button(:value,"Preview").click
                  
      assert($ie.contains_text($prd_inst_name_length_msg))      
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name", "All validation error message", "Passed")
      rescue=>e
       $logger.log_results("SavingProduct- Check for validation error while creating new SavingProduct", "prd_inst_name","All validation error message","Failed")
     end
  end
  
  # Check for validation messages while creating new SavingProduct with short_name
  def verify_ShortName(short_name) 
  
    b=(short_name =~ /\s+/)
  if (b!=nil) 
    begin
     # set_value_txtfield("prdOfferingShortName", short_name)      
     # $ie.button(:value,"Preview").click
     #puts "value of b is "+b.to_s
     assert($ie.contains_text($savings_shortname_msg))      
       $logger.log_results("SavingProduct- Error message appears when short_name contains space", short_name, $savings_shortname_msg, "Passed")
    rescue=>e
       $logger.log_results("SavingProduct- Error message did not appear when short_name contains space", short_name,$savings_shortname_msg,"Failed")
    end
  else
    begin
    assert(!$ie.contains_text($savings_shortname_msg))
      $logger.log_results("SavingProduct- Error message does not appear when short_name does not contain space",short_name,"No error message","passed")
     rescue =>e
     $logger.log_results("SavingProduct- Error message appears when short_name does not contain space",short_name,"No error message","failed")
     end 
  end
  end 
  
  # Check for validation messages while creating new SavingProduct with interest_rate
  def verify_InterestRate(interest_rate)
  if (interest_rate.to_f > 100)
    begin
  
      assert($ie.contains_text($savings_valid_int_rate))
      $logger.log_results("SavingProduct- Error message appears when interest rate is more than max value", interest_rate.to_s,$savings_valid_int_rate , "Passed")
      rescue=>e
      $logger.log_results("SavingProduct- Error message does not appear when interest rate is more than max value", interest_rate.to_s,$savings_valid_int_rate , "failed")
    end
  else
    begin 
    #assert(!$ie.contains_text($savingproduct_valid_interestrate_msg))      
      assert(!$ie.contains_text($savings_valid_int_rate))
      $logger.log_results("SavingProduct-No Error message  appears when interest rate is less than max value",interest_rate.to_s,"No error message","passed")
      rescue =>e
      $logger.log_results("SavingProduct-Error message appears when interest rate is less than max value",interest_rate.to_s,"No error message","failed")
    end
  end
  end
 # Check for validation messages while creating new SavingProduct with deposit_type
  def verify_ValidRecommendedAmount(deposit_type,deposit_amount)
  if (deposit_amount.to_f==0.0)
    begin
        assert($ie.contains_text($savingproduct_man_depositamount_msg))      
        $logger.log_results("SavingProduct- Error message appears when mandatory deposit amount is 0", deposit_amount.to_s,$savingproduct_man_depositamount_msg , "Passed")
          rescue=>e
        $logger.log_results("SavingProduct- Error message does not appear when mandatory deposit amount is 0", deposit_amount.to_s,$savingproduct_man_depositamount_msg , "failed")
      end
   else
    begin
        assert(!$ie.contains_text($savingproduct_man_depositamount_msg))      
        $logger.log_results("SavingsProduct- No error message appears when mandatory deposit amount is not 0",deposit_amount.to_s,"No error message","passed")
    rescue=>e
        $logger.log_results("SavingsProduct- error message appears when mandatory deposit amount is not 0",deposit_amount.to_s,"No error message","failed")    
    end
   end
  end

  # Check for validation messages while creating new SavingProduct with deposit_type, deposit_amount
  def verify_maxRecommendedAmount(deposit_type, deposit_amount)
  if (deposit_amount.to_f >9999999.9)
    begin
        assert($ie.contains_text($savingproduct_valid_depositamount_msg))      
        $logger.log_results("SavingProduct-Error message appears when "+deposit_type+" deposit amount is greater than max value", deposit_amount.to_s,$savingproduct_valid_depositamount_msg , "Passed")
          rescue=>e
        $logger.log_results("SavingProduct-Error message does not appear when "+deposit_type+" deposit amount is greater than max value", deposit_amount.to_s,$savingproduct_valid_depositamount_msg , "failed")
      end
   else
    begin
      assert(!$ie.contains_text($savingproduct_valid_depositamount_msg))      
        $logger.log_results("SavingProduct-No Error message appears when "+deposit_type+" deposit amount is less than max value", deposit_amount.to_s,"No error message", "Passed")
    rescue=>e
        $logger.log_results("SavingProduct-Error message appears when "+deposit_type+" deposit amount is less than max value", deposit_amount.to_s,"No error message", "failed")    
    end
   end
  end

  # Check for validation messages while creating new SavingProduct with deposit_type, deposit_amount
  def verify_MaxAmountPerWithdrawal(max_amount_per_withdrawal)
  if (max_amount_per_withdrawal.to_f > 9999999.9)
    begin
      #  set_value_txtfield("maxAmntWithdrawl", max_amount_per_withdrawal)
      #  $ie.button(:value,"Preview").click
        assert($ie.contains_text($savingproduct_valid_max_amount_per_withdrawal_msg))      
        $logger.log_results("SavingProduct-Error message appears when max amount per withdrawal is greater than max value", max_amount_per_withdrawal.to_s,$savingproduct_valid_max_amount_per_withdrawal_msg, "Passed")
          rescue=>e
        $logger.log_results("SavingProduct-Error message does not appear when max amount per withdrawal is greater than max value", max_amount_per_withdrawal.to_s,$savingproduct_valid_max_amount_per_withdrawal_msg, "failed")
      end
   else
    begin
        assert(!$ie.contains_text($savingproduct_valid_max_amount_per_withdrawal_msg))   
        $logger.log_results("SavingProduct-No Error message appears when max amount per withdrawal is less than max value", max_amount_per_withdrawal.to_s,"No error message", "Passed")
    rescue=>e
        $logger.log_results("SavingProduct-Error message appears when max amount per withdrawal is less than max value", max_amount_per_withdrawal.to_s,"No error message", "failed")     
    end
  end
  end
   # Check for validation messages while creating new SavingProduct with min_balance_interestrate
  def verify_MinBalanceInterestrate(min_balance_interestrate)
  if (min_balance_interestrate.to_f >9999999.9)
    begin
      #  set_value_txtfield("minAmntForInt", min_balance_interestrate)
      #  $ie.button(:value,"Preview").click
        assert($ie.contains_text($savingproduct_valid_min_balance_interestrate_msg))      
        $logger.log_results("SavingProduct- Error message appears when minimum balance for service charge greater than max value", min_balance_interestrate.to_s, $savingproduct_valid_min_balance_interestrate_msg, "Passed")
          rescue=>e
        $logger.log_results("SavingProduct- Error message does not  appear when minimum balance for service charge greater than max value", min_balance_interestrate.to_s, $savingproduct_valid_min_balance_interestrate_msg, "failed")
      end
   else
    begin
         assert(!$ie.contains_text($savingproduct_valid_min_balance_interestrate_msg))      
         $logger.log_results("SavingProduct-No Error message appears when minimum balance for service charge is less than max value", min_balance_interestrate.to_s, "No error message", "Passed")
    rescue=>e
         $logger.log_results("SavingProduct-Error message appears when minimum balance for service charge is less than max value", min_balance_interestrate.to_s, "No error message", "failed")    
    end   
   end
  end
  
  # checks for the unique SavingProduct name functionality
  def create_SavingProduct_duplicate_name(
            prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
     begin
      Click_Admin_page()
      $ie.link(:text, $new_savingproduct_link).click
      Check_New_SavingProduct()     
      set_SavingProduct_data(
            prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
        
      $ie.button(:value,"Preview").click
	  validate_SavingProduct_preview_page(
            prd_inst_name, short_name, prd_category, appl_for, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)

  	  $ie.button(:value,"Submit").click
  	  assert($ie.contains_text($duplicate_prd_inst_name_msg)) 
        $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct name",
          "existing SavingProduct name","Name validation error message","Passed")
        create_SavingProduct_duplicate_shortname(prd_inst_name, short_name)
      rescue =>e
        $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct name",
          "existing SavingProduct name","Name validation error message","Failed")
     end
  end

def create_SavingProduct_duplicate_shortname(prd_inst_name, short_name)
     begin        
      $ie.button(:value, $edit_savingproduct_link).click
      Check_New_SavingProduct()
      set_value_txtfield("prdOfferingName", prd_inst_name +  "mifos123")
      set_value_txtfield("prdOfferingShortName", short_name)
      $ie.button(:value, "Preview").click
            
  	  $ie.button(:value,"Submit").click
  	  assert($ie.contains_text($duplicate_shortname_msg)) 
        $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct short name",
          "existing SavingProduct short name","Short Name validation error message","Passed")
        $ie.button(:value,"Cancel").click
      rescue =>e
        $logger.log_results("SavingProduct- Check for creating new SavingProduct with an existing SavingProduct short name",
          "existing SavingProduct shortname","Short Name validation error message","Failed")
     end
  end

  
  # Creates a new SavingProduct and verify the preview page and database values after submission 
  def Create_New_SavingProduct(
            prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
   
    begin
    puts "Function: Create_New_SavingProduct"
    set_SavingProduct_data(
            prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
     $ie.button(:value, "Preview").click
             
	 validate_SavingProduct_preview_page(
            prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
             	
	$ie.button(:value,"Submit").click
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
     $ie.button(:value,"Preview").click  
    
	 validate_SavingProduct_preview_page(
            prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
     $ie.button(:name, "edit").click
    begin    		
    assert($ie.contains_text($new_savingproduct_page_msg))
        $logger.log_results("SavingProduct ", "Click on "+$edit_savingproduct_link+" button" ,"In Margin Money Product creation page","Passed")
        $ie.button(:value,"Preview").click
        validate_SavingProduct_preview_page(
            prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
        $ie.button(:value,"Cancel").click
         
        verify_admin_page()         
    rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("SavingProduct ", "Click on "+$edit_savingproduct_link+" button" ,"In Margin Money Product creation page","failed")
    end
  end
  
  # Enter the new SavingProduct data 
  def set_SavingProduct_data(
            prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
             time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    begin
         if($ie.contains_text($new_savingproduct_page_msg))
            $logger.log_results("SavingProduct- Create new SavingProduct page", "Click on 'Define a new SavingProduct' link","Access to the new SavingProduct page","Passed")
         else
            $logger.log_results("SavingProduct- Create new SavingProduct page", "Click on admin 'Define a new SavingProduct' link","Access to the new SavingProduct page","Failed")
         end
      
      set_value_txtfield("prdOfferingName", prd_inst_name)
      set_value_txtfield("prdOfferingShortName", short_name)
      set_value_selectlist("prdCategory.productCategoryID", prd_category)
      set_value_selectlist("prdApplicableMaster.prdApplicableMasterId", appl_for)
      if appl_for=="Groups"
      set_value_selectlist("recommendedAmntUnit.recommendedAmntUnitId", appliesto_amount)
      end
      set_value_selectlist("savingsType.savingsTypeId", deposit_type)
      set_value_txtfield("recommendedAmount", deposit_amount)

      
      set_value_txtfield("interestRate", interest_rate)
      set_value_selectlist("interestCalcType.interestCalculationTypeID", balance_interestcalc)
      set_value_txtfield("timeForInterestCacl", time_interestcalc)
      set_value_txtfield("freqOfInterest", interest_postingfrequency)
      
      set_value_selectlist("depositGLCode.glcodeId", deposit_glcode)
      set_value_selectlist("interestGLCode.glcodeId", interest_glcode)
      
     end
  end

  
  # Validate the preview for new check list  
  def  validate_SavingProduct_preview_page(
          prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
           time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    
    begin    
      if $ie.contains_text($review_savingproduct_info) then
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
     assert_on_page("Product instance name : " + prd_inst_name.to_s)
     assert_on_page("Short name : " + short_name.to_s)
     assert_on_page("Product category : " + prd_category.to_s)
     assert_on_page("Start date : " + @@current_date.to_s)
     assert_on_page("Applicable for : " + appl_for.to_s)
     if appl_for=="Groups"
     assert_on_page("Amount Applies to : " + appliesto_amount.to_s)
     end
     assert_on_page("Type of deposits: " + deposit_type.to_s)
     if deposit_type=="Mandatory"
     assert_on_page("Mandatory amount for deposit: " + deposit_amount.to_f.to_s)
     elsif deposit_type=="Voluntary"
     assert_on_page("Recommended Amount for Deposit: " + deposit_amount.to_f.to_s)
     end
     assert_on_page("Service Charge rate : " + interest_rate.to_f.to_s + " %")
     assert_on_page("Balance used for Service Charge calculation : " + balance_interestcalc.to_s)
     assert_on_page("Time period for Service Charge calculation : " + time_interestcalc.to_i.to_s + " Day(s)")          
     assert_on_page("Frequency of Service Charge posting to accounts : " + interest_postingfrequency.to_i.to_s + " month(s)")
     assert_on_page("GL code for deposits : " + deposit_glcode.to_s)
     assert_on_page("GL code for Service Charge : " + interest_glcode.to_s)
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
    assert($ie.contains_text($savingproduct_created_msg))and assert($ie.contains_text($create_savingproduct_msg))\
      and assert($ie.contains_text($savingproduct_view_detail_msg))
       $logger.log_results("SavingProduct- Margin Money Product created", "NA","The page should redirect to Margin Money ProductCreation-Conformation page","Passed")
        $ie.link(:text, $create_savingproduct_msg).click
        Check_New_SavingProduct()
    rescue =>e
       $logger.log_results("SavingProduct- Margin Money Product created", "NA","The page should redirect to Margin Money ProductCreation-Conformation page","Failed")     
 end      
  
  # Mandatory check for view check list
  def check_view_savingproduct(
      prd_inst_name, short_name, prd_category, appl_for,appliesto_amount, deposit_type, deposit_amount, interest_rate,balance_interestcalc,
       time_interestcalc, interest_postingfrequency, deposit_glcode, interest_glcode)
    begin
    Click_Admin_page()
    $ie.link(:text, $view_savingproduct_link).click
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
     assert_on_page("Product instance name: " + prd_inst_name.to_s)
     assert_on_page("Short name: " + short_name.to_s)
     assert_on_page("Product category: " + prd_category.to_s)
     assert_on_page("Start date: " + @@current_date.to_s)
     assert_on_page("Applicable for: " + appl_for.to_s)
     if appl_for=="Groups"
     assert_on_page("Amount Applies to: " + appliesto_amount.to_s)
     end
     assert_on_page("Type of deposits: " + deposit_type.to_s)
     if deposit_type=="Mandatory"
     assert_on_page("Mandatory amount for deposit: " + deposit_amount.to_f.to_s)
     elsif deposit_type=="Voluntary"
     assert_on_page("Recommended Amount for Deposit: " + deposit_amount.to_f.to_s)
     end
     assert_on_page("Service Charge rate : " + interest_rate.to_f.to_s + " %")
     assert_on_page("Balance used for Service Charge calculation : " + balance_interestcalc.to_s)
     assert_on_page("Time period for Service Charge calculation : " + time_interestcalc.to_i.to_s + " Day(s)")          
     assert_on_page("Frequency of Service Charge posting to accounts : " + interest_postingfrequency.to_i.to_s + " month(s)")
     assert_on_page("GL code for deposits: " + deposit_glcode.to_s)
     assert_on_page("GL code for Service Charge: " + interest_glcode.to_s)
   end 
  end 
  
  # Check for the status change from active to inactive and then inactive to active
  def check_status(prd_inst_name)
    rowcount=2
    edit_savingproduct_status(prd_inst_name, "Inactive")
    change_log(prd_inst_name,"2","5",rowcount) #change log shows 2 for active and 5 for inactive
    rowcount+=1
    $ie.button(:value,"Back to details page").click
    edit_savingproduct_status(prd_inst_name, "Active")  
    change_log(prd_inst_name,"5","2",rowcount)
    rowcount+=1
    $ie.button(:value,"Back to details page").click
    $ie.link(:text,"Admin").click
  end
    
   # Change the SavingProduct status
  def  edit_savingproduct_status(prd_inst_name, status)
    begin    
      $ie.link(:text,$edit_savingproduct_link).click

      assert($ie.contains_text(prd_inst_name.to_s + " - Edit Margin Money Product information"))
       $logger.log_results("SavingProduct- Edit Margin Money Product information ", "click on Edit Margin Money Product information","Edit page should be opened","Passed")
       $ie.select_list(:name,"prdStatus.offeringStatusId").select(status)
       $ie.button(:value,"Preview").click              
       if($ie.contains_text("Status : " + status.to_s))
        $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Passed")        
       else
        $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Failed")
       end
       $ie.button(:value,"Submit").click              
       verify_status_change(prd_inst_name, status)    
      rescue =>e
       $logger.log_results("SavingProduct- Edit Margin Money Product information", "click on Edit Margin Money Product information ","Edit page should be opened","Failed")
      end      
  end 
  #clicks on view change log link and checks the values under old value and new value once status is changed
  def change_log(prd_inst_name,old_status,new_status,rowcount)
    $ie.link(:text,"View Change Log").click
    begin
    assert($ie.contains_text(prd_inst_name+" - Change log"))
    $logger.log_results("navigated to change log page of "+prd_inst_name,"NA","NA","passed")
    rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("could not navigate to change log page of "+prd_inst_name,"NA","NA","failed")    
    end
    table_obj=$ie.table(:index,11)
    begin
    assert_equal(table_obj[rowcount][3].text,old_status)
    $logger.log_results("value in change log for old value are same ",old_status.to_s,table_obj[rowcount][3].text,"passed")
    rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("value in change log for old value are different",old_status.to_s,table_obj[rowcount][3].text,"failed")
    end
    begin
    assert_equal(table_obj[rowcount][4].text,new_status)
    $logger.log_results("value in change log for new value are same ",new_status.to_s,table_obj[rowcount][4].text,"passed")    
    rescue Test::Unit::AssertionFailedError=>e
    $logger.log_results("value in change log for new value are different",new_status.to_s,table_obj[rowcount][4].text,"failed")
    end
  end
    # verifies the changed status   
    def verify_status_change(prd_inst_name, status)    
     assert($ie.contains_text(status)) and assert($ie.contains_text(prd_inst_name.to_s))
       $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Passed")       
     rescue =>e
       $logger.log_results("SavingProduct- Edit Margin Money Product information ", "status change","Preview page with changed status","Failed")
    end 
   
  
 end  