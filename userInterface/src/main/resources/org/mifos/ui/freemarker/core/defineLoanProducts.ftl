[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
<head>
 <script type="text/javascript">
 function addOption(root, text, value)
{
  var newOpt = new Option(text, value);
  var rootLength = root.length;
  root.options[rootLength] = newOpt;
}

function deleteOption(root, index)
{ 
  var rootLength= root.length;
  if(rootLength>0)
  {
    root.options[index] = null;
  }
}

function moveOptions(root, destination)
{
  
  var rootLength= root.length;
  var rootText = new Array();
  var rootValues = new Array();
  var rootCount = 0;
  
  var i; 
  for(i=rootLength-1; i>=0; i--)
  {
    if(root.options[i].selected)
    {
      rootText[rootCount] = root.options[i].text;
      rootValues[rootCount] = root.options[i].value;
      deleteOption(root, i);
      rootCount++;
    }
  }  
  for(i=rootCount-1; i>=0; i--)
  {
    addOption(destination, rootText[i], rootValues[i]);
  }  
}

function selectAllOptions(outSel)
{
	if(null != outSel) {
	 	var selLength =outSel.length;
		outSel.multiple=true;
		for(i=selLength-1; i>=0; i--)
		{
			outSel.options[i].selected=true;
		}
	}
}	
</script>
</head>
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 arrowIMG orangeheading">[@spring.message "manageLoanProducts.defineLoanProduct.loanproductinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="post" action="defineLoanProducts.ftl" name="formname">
          <p class="font15"><span class="fontBold" id="createLoanProduct.heading">[@spring.message "manageLoanProducts.defineLoanProduct.addanewLoanproduct" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "manageLoanProducts.defineLoanProduct.enterLoanproductinformation" /]</span></p>
          <div>[@spring.message "manageLoanProducts.defineLoanProduct.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturn" /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired" /] </div>
          <p class="error" id="error1">
          </p>
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.loanproductdetails" /]</p>
          
          <div class="prepend-2  span-24 last">
            
            <div class="span-23 ">
            	<span class="pull-3 span-8 rightAlign" id="createLoanProduct.label.prdOfferingName"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.productinstancename" /]&nbsp;:</span>
            	<span class="span-4">
            		[@spring.bind "loanProduct.name" /]
            		<input type="text" id="createLoanProduct.input.prdOffering" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
            	</span>
            </div>
            
            <div class="span-23 ">
            	<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.shortname" /]&nbsp;:</span>
            	<span class="span-4">
            		[@spring.bind "loanProduct.shortName" /]
            		<input type="text" size="3" id="createLoanProduct.input.prdOfferingShortName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
            	</span>
            </div>
            
            <div class="span-23 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.description" /]&nbsp;:</span>
            	<span class="span-4">
            		[@spring.bind "loanProduct.description" /]
                	<textarea rows="7" cols="55" id="createLoanProduct.input.description" name="${spring.status.expression}">${spring.status.value?if_exists}</textarea>
            	</span>
            </div>
            
            <div class="span-23">
            	<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.productcategory" /]&nbsp;:</span>
            	<span class="span-4">
            		[@mifos.formSingleSelectWithPrompt "loanProduct.selectedCategory", loanProduct.categoryOptions, "--select one--" /]
              	</span>
            </div>
            
            <div class="span-23 last">
            	<span class="pull-3 span-8 rightAlign"><span class="red"> * </span>[@spring.message "manageLoanProducts.defineLoanProduct.startdate" /]&nbsp;:</span>
            	[@spring.bind "loanProduct.startDateDay" /]
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />DD</span>
            	[@spring.bind "loanProduct.startDateMonth" /]
              	<span><input type="text" size="1" maxlength="2" id="startDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />MM</span>
              	[@spring.bind "loanProduct.startDateYear" /]
              	<span><input type="text" size="2" maxlength="4" id="startDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />YYYY</span>
            </div>
            
            <div class="span-23 last"> <span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.enddate" /]&nbsp;:</span>
            	[@spring.bind "loanProduct.endDateDay" /]
	            <span class="span-2"><input type="text" size="1" maxlength="2" id="endDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />DD</span>
	            [@spring.bind "loanProduct.endDateMonth" /]
	            <span class="span-2"><input type="text" size="1" maxlength="2" id="endDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />MM</span>
	            [@spring.bind "loanProduct.endDateYear" /]
	            <span class="span-3"><input type="text" size="2" maxlength="4" id="endDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />YYYY</span>
            </div>
            
            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.applicablefor" /]&nbsp;:</span>
            	<span class="span-4">
            	[@mifos.formSingleSelectWithPrompt "loanProduct.selectedApplicableFor", loanProduct.applicableForOptions, "--select one--" /]
              	</span>
            </div>
            
            <div class="span-23 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.includeinLoancyclecounter" /]&nbsp;:</span>
            	<span class="span-4">
            		[@spring.formCheckbox "loanProduct.includeInLoanCycleCounter" "createLoanProduct.checkbox.loanCounter" /]
              	</span>
            </div>
          </div>

          <div class="clear">&nbsp;</div>
          
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.loanAmount" /]</p>
          <div class="prepend-2  span-23 last">
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.calculateLoanAmountas" /]&nbsp;:</span>
            	<div class="span-17">
            		[@spring.formRadioButtons "loanProduct.selectedLoanAmountCalculationType", loanProduct.loanAmountCalculationTypeOptions, "" /]
                </div>
                
                <div class="clear">&nbsp;</div>
                
                <div id="option0" class="span-14 prepend-4">
                	<div class="span-14 bluedivs fontBold paddingLeft" >
                    	<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.minloanamount" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.maxloanamount" /]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.defineLoanProduct.defaultamount" /]</span>
                    </div>
                    <div class="clear">&nbsp;</div>
                    <div class="span-14 paddingLeft">
                    	<span class="span-4">[@spring.formInput "loanProduct.loanAmountSameForAllLoans.min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountSameForAllLoans.max" /]</span>
                        <span class="span-5 last">[@spring.formInput "loanProduct.loanAmountSameForAllLoans.theDefault" /]</span>
                    </div>
                    <div>&nbsp;</div>
                </div>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.interestrate" /] </p>
          <div class="prepend-2  span-21 last">
            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.interestratetype" /]&nbsp;:</span>
            	<span class="span-6">
            	[@mifos.formSingleSelectWithPrompt "loanProduct.selectedInterestRateCalculationType", loanProduct.interestRateCalculationTypeOptions, "--select one--" /]
            	</span> 
            </div>
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.maxInterestrate" /]&nbsp;:</span>
            <span class="span-6">[@spring.formInput "loanProduct.maxInterestRate" /]&nbsp;(0 - 999)%</span>
            </div>
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.minInterestrate" /]&nbsp;:</span>
            	<span class="span-6">[@spring.formInput "loanProduct.minInterestRate" /]&nbsp;(0 - 999)%</span>
            </div>
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.defaultInterestrate" /]&nbsp;:</span>
            	<span class="span-6">[@spring.formInput "loanProduct.defaultInterestRate" /]&nbsp;(0 - 999)%</span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.repaymentschedule" /]</p>
          <div class="prepend-2  span-23 last">
          	<div class="span-23 ">
            	<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.frequencyofinstallments" /]&nbsp;:</span>
                <div class="span-15 borders">
                	<div class="borderbtm span-15">
                	[@spring.formRadioButtons "loanProduct.installmentFrequencyPeriod", loanProduct.installmentFrequencyPeriodOptions, "" /]
                	<!--
                    	<span class="span-8"><input type="radio" name="time" value="week" checked="checked" onclick="enableWeek();" id="createLoanProduct.radio.freqOfInstallmentsWeeks"/>[@spring.message "manageLoanProducts.defineLoanProduct.weekly" /]</span>
        				<span class="span-3 last"><input type="radio" name="time" value="month" onclick="enableMonth();" id="createLoanProduct.radio.freqOfInstallmentsMonths"/>[@spring.message "manageLoanProducts.defineLoanProduct.monthly" /]</span>
        			-->
                    </div>
                    <div id="week" class="paddingLeft" id="weekDIV" >
                		<span>[@spring.message "manageLoanProducts.defineLoanProduct.ifweeks,specifythefollowing" /]</span><br />
                		[@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /][@spring.formInput "loanProduct.installmentFrequencyRecurrenceEvery" "size=3"/][@spring.message "manageLoanProducts.defineLoanProduct.week(s)" /]
                	</div>
                    <div id="month" class="paddingLeft" style="display:none">
                		<span>[@spring.message "manageLoanProducts.defineLoanProduct.ifmonths,specifythefollowing" /]</span><br />
                		[@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /][@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]
                	</div>
                </div>
            </div>

            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.calculateofInstallmentsas" /]&nbsp;</span>
            	<div class="span-14">
            		[@spring.formRadioButtons "loanProduct.selectedInstallmentsCalculationType", loanProduct.installmentsCalculationTypeOptions, "" /]
                </div>
                
                <div class="clear">&nbsp;</div>
                <div id="install0"  class="span-14 prepend-4">
                	<div class="span-14 bluedivs fontBold paddingLeft">
                    	<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.minofinstallments" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.maxofinstallments" /]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.defineLoanProduct.defaultofinstallments" /]</span>
                    </div>
                    <div class="clear">&nbsp;</div>
                    <div class="span-14 paddingLeft">
                    	<span class="span-4 ">[@spring.formInput "loanProduct.installmentsSameForAllLoans.min" /]</span>
                        <span class="span-4 ">[@spring.formInput "loanProduct.installmentsSameForAllLoans.max" /]</span>
                        <span class="span-5 last">[@spring.formInput "loanProduct.installmentsSameForAllLoans.theDefault" /]</span>
                    </div>
                    <div>&nbsp;</div>
                </div>
            </div>
            
            <div class="span-23">
            	<span class="pull-3 span-8 rightAlign" id="gracepertype">[@spring.message "manageLoanProducts.defineLoanProduct.graceperiodtype" /]&nbsp;:</span>
                <span class="span-15 last">
                	[@spring.formSingleSelect "loanProduct.selectedGracePeriodType", loanProduct.gracePeriodTypeOptions /]
                </span>
            </div>
            <div class="span-23 ">
            	<span class="pull-3 span-8 rightAlign" id="graceperdur">[@spring.message "manageLoanProducts.defineLoanProduct.graceperiodduration" /]&nbsp;:</span>
                <span class="span-15 last">[@spring.formInput "loanProduct.gracePeriodDurationInInstallments" /]&nbsp;[@spring.message "manageLoanProducts.defineLoanProduct.installments" /]</span>
            </div>
          </div>
          
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.fees" /] </p>
          <div class="prepend-2  span-23 last">
          	<div class="span-23"><span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.attachfeetypes" /]&nbsp;</span>
            	<span class="span-12 ">
                	<span class="span-8">[@spring.message "manageLoanProducts.defineLoanProduct.clickonafeetype" /]</span>
                    <span class="span-4">
            			[@spring.formMultiSelect "loanProduct.applicableFees", loanProduct.applicableFeeOptions, "class=listSize" /]
					</span>
                    <span class="span-3">
                    	<br />
                    	<input class="buttn2" name="add" type="button" id="LoanFeesList.button.add"  value="Add >>" onclick="moveOptions(this.form.applicableFees, this.form.selectedFees);" />
                    	<br /><br />
						<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="moveOptions(this.form.selectedFees, this.form.applicableFees);" />
					</span>
					<span class="span-4">
						[@spring.formMultiSelect "loanProduct.selectedFees", loanProduct.selectedFeeOptions, "class=listSize" /]
					</span>
               	</span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.accounting" /] </p>
          <div class="prepend-2  span-23 last">
          	<div class="span-23"><span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.sourcesoffunds" /]&nbsp;:</span>
            	<span class="span-12 ">
                	<span class="span-8">[@spring.message "manageLoanProducts.defineLoanProduct.clickonafund" /]</span>
                    <span class="span-4">
                    	[@spring.formMultiSelect "loanProduct.applicableFunds", loanProduct.applicableFundOptions, "class=listSize" /]
					</span>
                    <span class="span-3">
                    	<br />
                    	<input class="buttn2" name="add" type="button"  id="srcFundsList.button.add"  value="Add >>" onclick="moveOptions(this.form.applicableFunds, this.form.selectedFunds);" />
                    	<br /><br />
						<input class="buttn2" name="remove" type="button" value="<< Remove" id="srcFundsList.button.remove" onclick="moveOptions(this.form.selectedFunds, this.form.applicableFunds);" />
					</span>
					<span class="span-4">
						[@spring.formMultiSelect "loanProduct.selectedFunds", loanProduct.selectedFundOptions, "class=listSize" /]
					</span>
               	</span>
            </div>
            <div class="span-21"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.productGLcode" /]&nbsp;:</span>
            	<span class="span-6 ">
            		<span class="span-2">[@spring.message "manageLoanProducts.defineLoanProduct.interest" /]&nbsp;:</span>
            		<span class="span-3">
						[@mifos.formSingleSelectWithPrompt "loanProduct.selectedInterest", loanProduct.interestGeneralLedgerOptions, "--select one--" /]            	
              		</span>
                
                	<span class="span-2">[@spring.message "manageLoanProducts.defineLoanProduct.principal" /]&nbsp;:</span>
                		[@mifos.formSingleSelectWithPrompt "loanProduct.selectedPrincipal", loanProduct.principalGeneralLedgerOptions, "--select one--" /]	
                	<span class="span-3">
              		</span> 
               	</span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
	        <div class="prepend-9">
	        	<input class="buttn" type="submit" id="createLoanProduct.button.preview" name="preview" value="Preview" onclick="selectAllOptions(this.form.selectedFees);selectAllOptions(this.form.selectedFunds);" />
	        	<input class="buttn2" type="submit" id="createLoanProduct.button.cancel" name="CANCEL" value="Cancel" />
	      	</div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]
  