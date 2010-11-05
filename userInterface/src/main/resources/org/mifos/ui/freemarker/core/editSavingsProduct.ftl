[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
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
[#assign savings][@mifostag.mifoslabel name="Savings" /][/#assign]
  <!--  Main Content Begins-->
  <span id="page.id" title="Edit_SavingsProduct" />
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left" style="width:50%">
                 [@spring.messageArgs "ftlDefinedLabels.manageProducts.defineSavingsProducts.savingsproductinformation" , [savings]  /]                
            </p>
            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right" style="float:right">[@spring.message "reviewAndSubmit" /]</p>
        </div>

      <div class="margin20lefttop">
        <form method="post" action="editSavingsProduct.ftl" name="createsavingsproduct">
          [@spring.bind "savingsProduct.generalDetails.id" /]
          <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
          <p class="font15">
            [@spring.bind "savingsProduct.generalDetails.name" /]
          	<span class="fontBold margin5bottom">${spring.status.value?if_exists}</span>&nbsp;-
          	<span class="orangeheading">
                [@spring.messageArgs "ftlDefinedLabels.manageProducts.defineSavingsProducts.enterSavingsproductinformation" , [savings]  /]                
              </span>
          </p>
          <div>[@spring.message "manageProducts.defineSavingsProducts.completethefieldsbelow" /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /]</div>
          [@mifos.showAllErrors "savingsProduct.*"/]
          <p class="fontBold margin10topbottom">
                [@spring.messageArgs "ftlDefinedLabels.manageProducts.defineSavingsProducts.savingsproductdetails" , [savings]  /]                
          </p>
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
        			[@mifos.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedCategory", savingsProduct.generalDetails.categoryOptions, "--selectone--" /]
				</span>
			</div>
			
			<div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign"><span class="red"> * </span>[@spring.message "manageProducts.defineSavingsProducts.fromDate" /]&nbsp;:</span>
            	[@spring.bind "savingsProduct.generalDetails.startDateDay" /]
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.DD"/]</span>
            	[@spring.bind "savingsProduct.generalDetails.startDateMonth" /]
              	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.MM"/]</span>
              	[@spring.bind "savingsProduct.generalDetails.startDateYear" /]
              	<span class="span-3"><input type="text" size="2" maxlength="4" id="startDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.YYYY"/]</span>
            </div>
            
            <div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.endDate" /]&nbsp;:</span>
            	[@spring.bind "savingsProduct.generalDetails.endDateDay" /]
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="endDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.DD"/]</span>
            	[@spring.bind "savingsProduct.generalDetails.endDateMonth" /]
              	<span class="span-2"><input type="text" size="1" maxlength="2" id="endDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.MM"/]</span>
              	[@spring.bind "savingsProduct.generalDetails.endDateYear" /]
              	<span class="span-3"><input type="text" size="2" maxlength="4" id="endDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.YYYY"/]</span>
            </div>
			
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.applicablefor" /]&nbsp;</span>
	            <span class="span-4">
	                [#if savingsProduct.notUpdateable]
	            	[@mifos.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedApplicableFor", savingsProduct.generalDetails.applicableForOptions, "--selectone--", "onchange='fnCheckAppliesTo()' disabled=disabled" /]
	            	[#else]
	            	[@mifos.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedApplicableFor", savingsProduct.generalDetails.applicableForOptions, "--selectone--", "onchange='fnCheckAppliesTo()'" /]
	            	[/#if]
				</span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "manageProducts.defineSavingsProducts.targetedDepositsandWithdrawalRestrictions" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.typeofdeposits" /]&nbsp;:</span>
          		<span class="span-4">
          		[#if savingsProduct.notUpdateable]
          		[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedDepositType", savingsProduct.depositTypeOptions, "--selectone--", "onchange='fnCheckRecMand()' disabled=disabled" /]
          		[#else]
				[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedDepositType", savingsProduct.depositTypeOptions, "--selectone--", "onchange='fnCheckRecMand()'" /]
          		[/#if]
				</span>
			</div>
			<script>fnCheckRecMand();</script>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign" id="recamnt">[@spring.message "manageProducts.defineSavingsProducts.recommendedAmountforDeposit" /]&nbsp;:</span>
            	<span class="pull-3 span-8 rightAlign" id="mandamnt" style="display: none"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.mandatoryAmountforDeposit" /]&nbsp;:</span>
            	<span class="span-4">[@spring.formInput "savingsProduct.amountForDeposit" /]</span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign" id="appliesto"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.amountAppliesto" /]&nbsp;</span>
            	<span class="span-4">
            	[#if savingsProduct.notUpdateable]
   				[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedGroupSavingsApproach", savingsProduct.groupSavingsApproachOptions, "--selectone--", "disabled=disabled" /]
   				[#else]
   				[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedGroupSavingsApproach", savingsProduct.groupSavingsApproachOptions, "--selectone--", "disabled=disabled" /]
   				[/#if]
				</span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.maxamountperwithdrawal" /]&nbsp;</span>
            	<span class="span-4">[@spring.formInput "savingsProduct.maxWithdrawalAmount" /]</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "loanProduct.status.description"/]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20">
	      		<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "loanProduct.status.description"/]&nbsp;</span>
		      	<span class="span-6">[@mifos.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedStatus", savingsProduct.generalDetails.statusOptions, "--selectone--" /]</span>
            </div>
          </div>
          
          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "manageProducts.defineSavingsProducts.interestRate" /]</p>
          <div class="prepend-3  span-21 last">
	      	<div class="span-20">
	      		<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.interestRate" /]&nbsp;</span>
		      	<span class="span-6">[@spring.formInput "savingsProduct.interestRate" /]&nbsp;(0 - 100)%</span>
            </div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.balanceusedforInterestcalculation" /]&nbsp;</span>
            	<span class="span-4">
            	[#if savingsProduct.notUpdateable]
   				[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedInterestCalculation", savingsProduct.interestCaluclationOptions, "--selectone--", "disabled=disabled" /]
   				[#else]
   				[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedInterestCalculation", savingsProduct.interestCaluclationOptions, "--selectone--" /]
   				[/#if]
				</span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.timeperiodforInterestcalculation" /]&nbsp;</span>
            	<span class="span-9">
                	<span class="span-2">
                	[#if savingsProduct.notUpdateable]
                	[@spring.formInput "savingsProduct.interestCalculationFrequency", "disabled=disabled" /]
                	[#else]
                	[@spring.formInput "savingsProduct.interestCalculationFrequency" /]
                	[/#if]
                	</span>
                    <span class="span-4">
                    [#if savingsProduct.notUpdateable]
   					[@spring.formSingleSelect "savingsProduct.selectedFequencyPeriod", savingsProduct.frequencyPeriodOptions, "disabled=disabled" /]
   					[#else]
   					[@spring.formSingleSelect "savingsProduct.selectedFequencyPeriod", savingsProduct.frequencyPeriodOptions /]
   					[/#if]
				    </span>
				</span>
			</div>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.frequencyofInterestpostingtoaccounts" /]&nbsp;</span>
            	<span class="span-9">
            		[#if savingsProduct.notUpdateable]
                	[@spring.formInput "savingsProduct.interestPostingMonthlyFrequency", "disabled=disabled"/]&nbsp;&nbsp;[@spring.message "manageProducts.defineSavingsProducts.month(s)" /]
                	[#else]
                	[@spring.formInput "savingsProduct.interestPostingMonthlyFrequency" /]&nbsp;&nbsp;[@spring.message "manageProducts.defineSavingsProducts.month(s)" /]
                	[/#if]
                </span>
			</div>
            <div class="span-20 ">
            	<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.minimumbalancerequiredforInterestcalculation" /]&nbsp;</span>
            	<span class="span-4">[@spring.formInput "savingsProduct.minBalanceRequiredForInterestCalculation" /]</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "manageProducts.defineSavingsProducts.accounting" /] </p>
          <div class="prepend-3  span-21 last">
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodefordeposits" /]&nbsp;</span>
            	<span class="span-4">
            	[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedPrincipalGlCode", savingsProduct.principalGeneralLedgerOptions, "--selectone--", "disabled=disabled" /]
				 </span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodeforInterest" /]&nbsp;</span>
	            <span class="span-4">
	            	[@mifos.formSingleSelectWithPrompt "savingsProduct.selectedInterestGlCode", savingsProduct.interestGeneralLedgerOptions, "--selectone--", "disabled=disabled" /]
				 </span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <div class="buttonsSubmitCancel margin20right">
            	<input class="buttn" type="submit" id="createSavingsProduct.button.preview" class="buttn" name="preview" value="[@spring.message "preview"/]" />
            	<input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]