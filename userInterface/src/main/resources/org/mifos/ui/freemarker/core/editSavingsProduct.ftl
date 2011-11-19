[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
[@layout.header "title" /]
<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/switchInterestRateZero.js"></script>
<script type="text/javascript">
      $(document).ready(function () {
//          setTimeout(function() {
                fnCheckAppliesTo();
                fnCheckRecMand();
//          },200);
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
[@widget.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <span id="page.id" title="Edit_SavingsProduct"></span>
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left" style="width:50%">
                 [@spring.message "ftlDefinedLabels.manageProducts.defineSavingsProducts.savingsproductinformation"  /]
            </p>
            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right width130px" style="float:right">[@spring.message "reviewAndSubmit" /]</p>
        </div>

      <div class="margin20lefttop">
        <form method="post" action="editSavingsProduct.ftl" name="createsavingsproduct">
          [@spring.bind "savingsProduct.generalDetails.id" /]
          <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
          <p class="font15">
            [@spring.bind "savingsProduct.generalDetails.name" /]
              <span class="fontBold margin5bottom">${spring.status.value?if_exists}</span>&nbsp;-
              <span class="orangeheading">
                [@spring.message "ftlDefinedLabels.manageSavngsProducts.editsavingsproduct.editSavingsproductinformation"  /]
              </span>
          </p>
          <div>
            [@spring.message "ftlDefinedLabels.manageProducts.editsavingsproduct.editthefieldsbelow"  /]
          </div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /]</div>
          [@form.showAllErrors "savingsProduct.*"/]
          <p class="fontBold margin10topbottom">
                [@spring.message "ftlDefinedLabels.manageProducts.defineSavingsProducts.savingsproductdetails"  /]
          </p>
          <div class=" prepend-2  span-21 last">
              <div class="span-20 ">
                  <span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.productinstancename" /]:</span>
                  <span class="span-4">
                      [@spring.bind "savingsProduct.generalDetails.name" /]
                    <input type="text" id="createSavingsProduct.input.prdOfferingName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
                  </span>
              </div>
            <div class="span-20 ">
                <span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.shortname" /]:</span>
                <span class="span-4">
                    [@spring.bind "savingsProduct.generalDetails.shortName" /]
                    <input type="text" size="3" maxlength="4" id="createSavingsProduct.input.prdOfferingShortName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
                </span>
              </div>
            <div class="span-20 ">
                <span class="  span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.description" /]:</span>
                <span class="span-4">
                    [@spring.bind "savingsProduct.generalDetails.description" /]
                    <textarea rows="7" cols="55" id="createSavingsProduct.input.description" name="${spring.status.expression}">${spring.status.value?if_exists}</textarea>
                </span>
              </div>
            <div class="span-20 ">
                <span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.productcategory" /]:</span>
                <span class="span-4">
                    [@form.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedCategory", savingsProduct.generalDetails.categoryOptions, "--selectone--" /]
                </span>
            </div>

            <div class="span-20 last">
                <span class="  span-8 rightAlign"><span class="red"> * </span>[@spring.message "manageProducts.defineSavingsProducts.fromDate" /]:</span>
                [@spring.bind "savingsProduct.generalDetails.startDateDay" /]
                <span class="span-2"><input type="text" size="1" maxlength="2" id="startDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.DD"/]</span>
                [@spring.bind "savingsProduct.generalDetails.startDateMonth" /]
                  <span class="span-2"><input type="text" size="1" maxlength="2" id="startDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.MM"/]</span>
                  [@spring.bind "savingsProduct.generalDetails.startDateYear" /]
                  <span class="span-3"><input type="text" size="2" maxlength="4" id="startDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.YYYY"/]</span>
            </div>

            <div class="span-20 last">
                <span class="  span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.endDate" /]:</span>
                [@spring.bind "savingsProduct.generalDetails.endDateDay" /]
                <span class="span-2"><input type="text" size="1" maxlength="2" id="endDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.DD"/]</span>
                [@spring.bind "savingsProduct.generalDetails.endDateMonth" /]
                  <span class="span-2"><input type="text" size="1" maxlength="2" id="endDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.MM"/]</span>
                  [@spring.bind "savingsProduct.generalDetails.endDateYear" /]
                  <span class="span-3"><input type="text" size="2" maxlength="4" id="endDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.YYYY"/]</span>
            </div>

            <div class="span-20 "><span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.applicablefor" /]:</span>
                <span class="span-4">
                    [#if savingsProduct.notUpdateable]
                    [@form.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedApplicableFor", savingsProduct.generalDetails.applicableForOptions, "--selectone--", "onchange='fnCheckAppliesTo()' disabled=disabled" /]
                    [#else]
                    [@form.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedApplicableFor", savingsProduct.generalDetails.applicableForOptions, "--selectone--", "onchange='fnCheckAppliesTo()'" /]
                    [/#if]
                </span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "manageProducts.defineSavingsProducts.targetedDepositsandWithdrawalRestrictions" /]</p>
          <div class=" prepend-2  span-21 last">
              <div class="span-20 "><span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.typeofdeposits" /]:</span>
                  <span class="span-4">
                  [#if savingsProduct.notUpdateable]
                  [@form.formSingleSelectWithPrompt "savingsProduct.selectedDepositType", savingsProduct.depositTypeOptions, "--selectone--", "onchange='fnCheckRecMand()' disabled=disabled" /]
                  [#else]
                [@form.formSingleSelectWithPrompt "savingsProduct.selectedDepositType", savingsProduct.depositTypeOptions, "--selectone--", "onchange='fnCheckRecMand()'" /]
                  [/#if]
                </span>
            </div>
            <script>fnCheckRecMand();</script>
            <div class="span-20 ">
                <span class="span-8 rightAlign  " id="recamnt" >[@spring.message "manageProducts.defineSavingsProducts.recommendedAmountforDeposit" /]:</span>
                <span class="  span-8 rightAlign" id="mandamnt" style="display: none"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.mandatoryAmountforDeposit" /]:</span>
                <span class="span-4">[@spring.formInput "savingsProduct.amountForDeposit" /]</span>
              </div>
            <div class="span-20 "><span class="  span-8 rightAlign" id="appliesto"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.amountAppliesto" /]:</span>
                <span class="span-4">
                [#if savingsProduct.notUpdateable]
                   [@form.formSingleSelectWithPrompt "savingsProduct.selectedGroupSavingsApproach", savingsProduct.groupSavingsApproachOptions, "--selectone--", "disabled=disabled" /]
                   [#else]
                   [@form.formSingleSelectWithPrompt "savingsProduct.selectedGroupSavingsApproach", savingsProduct.groupSavingsApproachOptions, "--selectone--", "disabled=disabled" /]
                   [/#if]
                </span>
            </div>
            <div class="span-20 "><span class="  span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.maxamountperwithdrawal" /]:</span>
                <span class="span-4">[@spring.formInput "savingsProduct.maxWithdrawalAmount" /]</span>
              </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "loanProduct.status.description"/]</p>
          <div class=" prepend-2  span-21 last">
              <div class="span-20">
                  <span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "loanProduct.status.description"/]:</span>
                  <span class="span-6">[@form.formSingleSelectWithPrompt "savingsProduct.generalDetails.selectedStatus", savingsProduct.generalDetails.statusOptions, "--selectone--" /]</span>
            </div>
          </div>

          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "manageProducts.defineSavingsProducts.interestRate" /]</p>
          <div class=" prepend-2  span-21 last">
          	<div class="span-20">
                  <span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.interestRate" /]:</span>
                  <span class="span-6">[@spring.formInput "savingsProduct.interestRate" /]&nbsp;
                	(0 - 100)%, [@spring.message "manageProducts.defineSavingsProducts.interestRate"/] = 0: [@spring.formCheckbox "savingsProduct.interestRateZero"/]
        		  </span>
            </div>
            <div id="interestRateDetails" >
	            <div class="span-20 "><span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.balanceusedforInterestcalculation" /]:</span>
	                <span class="span-4">
	                [#if savingsProduct.notUpdateable && savingsProduct.interestRate > 0]
	                   [@form.formSingleSelectWithPrompt "savingsProduct.selectedInterestCalculation", savingsProduct.interestCaluclationOptions, "--selectone--", "disabled=disabled" /]
                   	[#else]
	                   [@form.formSingleSelectWithPrompt "savingsProduct.selectedInterestCalculation", savingsProduct.interestCaluclationOptions, "--selectone--" /]
                   	[/#if]
	                </span>
	            </div>
	            <div class="span-20 "><span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.timeperiodforInterestcalculation" /]:</span>
	                <span class="span-9">
	                    <span>
	                    [#if savingsProduct.notUpdateable && savingsProduct.interestRate > 0]
	                    	[@spring.formInput "savingsProduct.interestCalculationFrequency", "disabled=disabled" /]
	                    [#else]
	                    	[@spring.formInput "savingsProduct.interestCalculationFrequency" /]
	                    [/#if]
	                    </span>
	                    <span class="span-4">
	                    [#if savingsProduct.notUpdateable && savingsProduct.interestRate > 0]
	                       [@spring.formSingleSelect "savingsProduct.selectedFequencyPeriod", savingsProduct.frequencyPeriodOptions, "disabled=disabled" /]
                       	[#else]
	                       [@spring.formSingleSelect "savingsProduct.selectedFequencyPeriod", savingsProduct.frequencyPeriodOptions /]
                       	[/#if]
	                    </span>
	                </span>
	            </div>
	            <div class="span-20 ">
	                <span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.frequencyofInterestpostingtoaccounts" /]:</span>
	                <span class="span-9">
                    [#if savingsProduct.notUpdateable && savingsProduct.interestRate > 0]
	                    [@spring.formInput "savingsProduct.interestPostingMonthlyFrequency", "disabled=disabled"/]&nbsp;&nbsp;[@spring.message "manageProducts.defineSavingsProducts.month(s)" /]
                    [#else]
	                    [@spring.formInput "savingsProduct.interestPostingMonthlyFrequency" /]&nbsp;&nbsp;[@spring.message "manageProducts.defineSavingsProducts.month(s)" /]
                    [/#if]
	                </span>
	            </div>
	            <div class="span-20 ">
	                <span class="  span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.minimumbalancerequiredforInterestcalculation" /]:</span>
	                <span class="span-4">[@spring.formInput "savingsProduct.minBalanceRequiredForInterestCalculation" /]</span>
	              </div>
	          </div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold margin10topbottom">[@spring.message "manageProducts.defineSavingsProducts.accounting" /] </p>
          <div class=" prepend-2  span-21 last">
            <div class="span-20 "><span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodefordeposits" /]:</span>
                <span class="span-4">
                [@form.formSingleSelectWithPrompt "savingsProduct.selectedPrincipalGlCode", savingsProduct.principalGeneralLedgerOptions, "--selectone--", "disabled=disabled" /]
                 </span>
            </div>
            <div class="span-20 "><span class="  span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodeforInterest" /]:</span>
                <span class="span-4">
                    [@form.formSingleSelectWithPrompt "savingsProduct.selectedInterestGlCode", savingsProduct.interestGeneralLedgerOptions, "--selectone--", "disabled=disabled" /]
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
  [@layout.footer/]
