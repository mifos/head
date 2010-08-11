[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
<head>
<script type="text/javascript">
//list boxes code
function SecListBox(ListBox,text,value){
	try{
		var option=document.createElement("OPTION");
		option.value=value;option.text=text;
		ListBox.options.add(option)
		}
	catch(er){alert(er)}
}
function FirstListBox(){
	try{
		var count=document.getElementById("lstBox").options.length;
		for(i=0;i<count;i++){
			if(document.getElementById("lstBox").options[i].selected){
				SecListBox(document.getElementById("ListBox1"),document.getElementById("lstBox").options[i].value,document.getElementById("lstBox").options[i].value);
				document.getElementById("lstBox").remove(i);
				break
			}
		}
	}
	catch(er){alert(er)}
}
function SortAllItems(){
	var arr=new Array();
	for(i=0;i<document.getElementById("lstBox").options.length;i++){
		arr[i]=document.getElementById("lstBox").options[i].value
	}
	arr.sort();
	RemoveAll();
	for(i=0;i<arr.length;i++){
		SecListBox(document.getElementById("lstBox"),arr[i],arr[i])
	}
}
function RemoveAll(){
	try{
		document.getElementById("lstBox").options.length=0
	}
	catch(er){alert(er)}
}
function SecondListBox(){
	try{
		var count=document.getElementById("ListBox1").options.length;
		for(i=0;i<count;i++){
			if(document.getElementById("ListBox1").options[i].selected){
				SecListBox(document.getElementById("lstBox"),document.getElementById("ListBox1").options[i].value,document.getElementById("ListBox1").options[i].value);
				document.getElementById("ListBox1").remove(i);
				break
			}
		}
		SortAllItems()
	}catch(er){alert(er)}
}
//next part

function enableMonth()
{
	document.getElementById("month").style.display="block";
	document.getElementById("week").style.display="none";
}
function enableWeek()
{
	document.getElementById("week").style.display="block";
	document.getElementById("month").style.display="none";

}
function enableAllLoans()
{
	document.getElementById("1").style.display="block";
	document.getElementById("2").style.display="none";
	document.getElementById("3").style.display="none";
}
function enableLoanAmount()
{
	document.getElementById("2").style.display="block";
	document.getElementById("1").style.display="none";
	document.getElementById("3").style.display="none";

}
function enableLoanCycle()
{
	document.getElementById("3").style.display="block";
	document.getElementById("2").style.display="none";
	document.getElementById("1").style.display="none";

}
function enableAllLoans1()
{
	document.getElementById("11").style.display="block";
	document.getElementById("22").style.display="none";
	document.getElementById("33").style.display="none";
}
function enableLoanAmount1()
{
	document.getElementById("22").style.display="block";
	document.getElementById("11").style.display="none";
	document.getElementById("33").style.display="none";

}
function enableLoanCycle1()
{
	document.getElementById("33").style.display="block";
	document.getElementById("22").style.display="none";
	document.getElementById("11").style.display="none";

}
function disable()
{
	document.getElementById("month").style.display="none";
	document.getElementById("22").style.display="none";
	document.getElementById("33").style.display="none";
	document.getElementById("2").style.display="none";
	document.getElementById("3").style.display="none";
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
        <form method="" action="" name="formname">
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
            		[@spring.formRadioButtons "loanProduct.loanAmountCalculationType", loanProduct.loanAmountCalculationTypeOptions, "" /]
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
                    	<span class="span-4">[@spring.formInput "loanProduct.minLoanAmount" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.maxLoanAmount" /]</span>
                        <span class="span-5 last">[@spring.formInput "loanProduct.defaultLoanAmount" /]</span>
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
                		[@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /][@spring.formInput "loanProduct.installmentFrequencyRecurrenceEvery" "size=3"/][@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]
                	</div>
                </div>
            </div>

            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.calculateofInstallmentsas" /]&nbsp;</span>
            	<div class="span-14">
            		[@spring.formRadioButtons "loanProduct.installmentsCalculationType", loanProduct.installmentsCalculationTypeOptions, "" /]
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
                    	<span class="span-4 ">[@spring.formInput "loanProduct.minInstallments" /]</span>
                        <span class="span-4 ">[@spring.formInput "loanProduct.maxInstallments" /]</span>
                        <span class="span-5 last">[@spring.formInput "loanProduct.defaultInstallments" /]</span>
                    </div>
                    <div>&nbsp;</div>
                </div>
            </div>
            
            <div class="span-23">
            	<span class="pull-3 span-8 rightAlign" id="gracepertype">[@spring.message "manageLoanProducts.defineLoanProduct.graceperiodtype" /]&nbsp;:</span>
                <span class="span-15 last">
                	[@mifos.formSingleSelectWithPrompt "loanProduct.selectedGracePeriodType", loanProduct.gracePeriodTypeOptions, "--select one--" /]
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
            			[@spring.formMultiSelect "loanProduct.selectedFees", loanProduct.applicableFeeOptions, "class=listSize" /]
					</span>
                    <span class="span-3">
                    	<br />
                    	<input class="buttn2" name="add" type="button" id="LoanFeesList.button.add"  value="Add >>" />
                    	<br /><br />
						<input class="buttn2" name="remove" type="button" value="<< Remove" />
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
                    	[@spring.formMultiSelect "loanProduct.selectedFunds", loanProduct.applicableFundOptions, "class=listSize" /]
					</span>
                    <span class="span-3">
                    	<br />
                    	<input class="buttn2" name="add" type="button"  id="SrcFundsList.button.add"  value="Add >>" onclick="FirstListBox();" />
                    	<br /><br />
						<input class="buttn2" name="remove" type="button" value="<< Remove" id="SrcFundsList.button.remove" onclick="SecondListBox();" />
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
            <input class="buttn" type="button" id="createLoanProduct.button.preview" name="preview" value="Preview" onclick="#"/>
            <input class="buttn2" type="button" id="createLoanProduct.button.cancel" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]
  