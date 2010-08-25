[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
<script type="text/javascript">
  	$(document).ready(function () {
  		fnCheckAppliesTo();
  		fnCheckRecMand();
	});

	function fnCheckAppliesTo() {
		if(document.getElementById("generalDetails.selectedApplicableFor").selectedIndex==2) {
			document.getElementById("selectedGroupSavingsApproach").disabled=false;
		}
		else {
			document.getElementById("selectedGroupSavingsApproach")[0].selectedIndex=0;
			document.getElementById("selectedGroupSavingsApproach").disabled=true;
		}
	}
	
	function fnCheckRecMand() {
	
		if(document.getElementById("selectedDepositType").selectedIndex==1) {
			document.getElementById("mandamnt").style.display = "inline";
			document.getElementById("recamnt").style.display = "none";
		}
		else {
			document.getElementById("mandamnt").style.display = "none";
			document.getElementById("recamnt").style.display = "inline";
		}
	}
</script>
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <span id="page.id" title="CreateSavingsProduct" />
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "manageProducts.defineSavingsProducts.savingsproductinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="post" action="defineSavingsProduct.ftl" name="createsavingsproduct">
          <p class="font15">
          	<span class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.addanewSavingsProduct" /]</span>&nbsp;--&nbsp;
          	<span class="orangeheading">[@spring.message "manageProducts.defineSavingsProducts.enterSavingsproductinformation" /]</span>
          </p>
          <div>[@spring.message "manageProducts.defineSavingsProducts.completethefieldsbelow" /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /]</div>
          [@mifos.showAllErrors "savingsProduct.*"/]
          <p class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.savingsproductdetails" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 ">
          		<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.productinstancename" /]&nbsp;</span>
          		<span class="span-4">
          			[@spring.bind "savingsProduct.generalDetails.name" /]
            		<input type="text" id="createSavingsProduct.input.prdOfferingName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
          		</span>
  			</div>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.shortname" /]&nbsp;</span>
            	<span class="span-4">
            		[@spring.bind "savingsProduct.generalDetails.shortName" /]
            		<input type="text" size="3" maxlength="4" id="createSavingsProduct.input.prdOfferingShortName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
            	</span>
  			</div>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.description" /]&nbsp;</span>
            	<span class="span-4">
            		[@spring.bind "savingsProduct.generalDetails.description" /]
                	<textarea rows="7" cols="55" id="createSavingsProduct.input.description" name="${spring.status.expression}">${spring.status.value?if_exists}</textarea>
            	</span>
  			</div>
        	<div class="span-20 ">
        		<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.productcategory" /]&nbsp;</span>
        		<span class="span-4">
        			[@mifos.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedCategory", savingsProduct.generalDetails.categoryOptions, "--select one--" /]
				</span>
			</div>
			
			<div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign"><span class="red"> * </span>[@spring.message "manageProducts.defineSavingsProducts.fromDate" /]&nbsp;:</span>
            	[@spring.bind "savingsProduct.generalDetails.startDateDay" /]
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />DD</span>
            	[@spring.bind "savingsProduct.generalDetails.startDateMonth" /]
              	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />MM</span>
              	[@spring.bind "savingsProduct.generalDetails.startDateYear" /]
              	<span class="span-3"><input type="text" size="2" maxlength="4" id="startDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />YYYY</span>
            </div>
            
            <div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.endDate" /]&nbsp;:</span>
            	[@spring.bind "savingsProduct.generalDetails.endDateDay" /]
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="endDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />DD</span>
            	[@spring.bind "savingsProduct.generalDetails.endDateMonth" /]
              	<span class="span-2"><input type="text" size="1" maxlength="2" id="endDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />MM</span>
              	[@spring.bind "savingsProduct.generalDetails.endDateYear" /]
              	<span class="span-3"><input type="text" size="2" maxlength="4" id="endDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />YYYY</span>
            </div>
			
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.applicablefor" /]&nbsp;</span>
	            <span class="span-4">
	            	[@mifos.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedApplicableFor", savingsProduct.generalDetails.applicableForOptions, "--select one--", "onchange='fnCheckAppliesTo()'" /]
				</span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.targetedDepositsandWithdrawalRestrictions" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.typeofdeposits" /]&nbsp;:</span>
          		<span class="span-4">
          		[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedDepositType", savingsProduct.depositTypeOptions, "--select one--", "onchange='fnCheckRecMand()'" /]
				</span>
			</div>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign" id="recamnt">[@spring.message "manageProducts.defineSavingsProducts.recommendedAmountforDeposit" /]&nbsp;:</span>
            	<span class="pull-3 span-8 rightAlign" id="mandamnt" style="display: none"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.mandatoryAmountforDeposit" /]&nbsp;:</span>
            	<span class="span-4">[@spring.formInput "savingsProduct.amountForDeposit" /]</span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign" id="appliesto"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.amountAppliesto" /]&nbsp;</span>
            	<span class="span-4">
   				[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedGroupSavingsApproach", savingsProduct.groupSavingsApproachOptions, "--select one--", "disabled=disabled" /]
				</span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.maxamountperwithdrawal" /]&nbsp;</span>
            	<span class="span-4">[@spring.formInput "savingsProduct.maxWithdrawalAmount" /]</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">Interest rate</p>
          <div class="prepend-3  span-21 last">
	      	<div class="span-20">
	      		<span class="pull-3 span-8 rightAlign"><span class="red">* </span>Interest rate&nbsp;</span>
		      	<span class="span-6">[@spring.formInput "savingsProduct.interestRate" /]&nbsp;(0 - 100)%</span>
            </div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.balanceusedforInterestcalculation" /]&nbsp;</span>
            	<span class="span-4">
   				[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedInterestCalculation", savingsProduct.interestCaluclationOptions, "--select one--" /]
				</span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.timeperiodforInterestcalculation" /]&nbsp;</span>
            	<span class="span-9">
                	<span class="span-2">[@spring.formInput "savingsProduct.interestCalculationFrequency" /]</span>
                    <span class="span-4">
   					[@spring.formSingleSelect "savingsProduct.selectedFequencyPeriod", savingsProduct.frequencyPeriodOptions /]
				    </span>
				</span>
			</div>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.frequencyofInterestpostingtoaccounts" /]&nbsp;</span>
            	<span class="span-9">
                	[@spring.formInput "savingsProduct.interestPostingMonthlyFrequency" /]&nbsp;&nbsp;[@spring.message "manageProducts.defineSavingsProducts.month(s)" /]
                </span>
			</div>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.minimumbalancerequiredforInterestcalculation" /]&nbsp;</span>
            	<span class="span-4">[@spring.formInput "savingsProduct.minBalanceRequiredForInterestCalculation" /]</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.accounting" /] </p>
          <div class="prepend-3  span-21 last">
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodefordeposits" /]&nbsp;</span>
            	<span class="span-4">
            	[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedPrincipalGlCode", savingsProduct.principalGeneralLedgerOptions, "--select one--" /]
				 </span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodeforinterest" /]&nbsp;</span>
	            <span class="span-4">
	            	[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedInterestGlCode", savingsProduct.interestGeneralLedgerOptions, "--select one--" /]
				 </span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="submit" id="createSavingsProduct.button.preview" class="buttn" name="preview" value="Preview" />
            	<input class="buttn2" type="submit" name="CANCEL" value="Cancel"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]