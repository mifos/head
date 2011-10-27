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

[@layout.header "title" /]
<script type="text/javascript">
      $(document).ready(function () {
          switchFrequencyMessage();
          switchLoanAmountType();
          switchLoanInstallmentType();
    });

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

function switchFrequencyMessage()
{
    if (document.getElementById("installmentFrequencyPeriod0").checked == true)
    {
        document.getElementById("weekSpecifyMessage").style.display = "block";
        document.getElementById("weekLabelMessage").style.display = "inline";
        document.getElementById("monthSpecifyMessage").style.display = "none";
        document.getElementById("monthLabelMessage").style.display = "none";
    }
    else
    {
        document.getElementById("weekSpecifyMessage").style.display = "none";
        document.getElementById("weekLabelMessage").style.display = "none";
        document.getElementById("monthSpecifyMessage").style.display = "block";
        document.getElementById("monthLabelMessage").style.display = "inline";
    }
}

function switchLoanAmountType()
{
    if (document.getElementById("selectedLoanAmountCalculationType0").checked == true)
    {
        document.getElementById("loanamountoption0").style.display = "block";
        document.getElementById("loanamountoption1").style.display = "none";
        document.getElementById("loanamountoption2").style.display = "none";
    }
    else if (document.getElementById("selectedLoanAmountCalculationType1").checked == true)
    {
        document.getElementById("loanamountoption0").style.display = "none";
        document.getElementById("loanamountoption1").style.display = "block";
        document.getElementById("loanamountoption2").style.display = "none";
    }
    else if (document.getElementById("selectedLoanAmountCalculationType2").checked == true)
    {
        document.getElementById("loanamountoption0").style.display = "none";
        document.getElementById("loanamountoption1").style.display = "none";
        document.getElementById("loanamountoption2").style.display = "block";
    }
}

function switchLoanInstallmentType()
{
    if (document.getElementById("selectedInstallmentsCalculationType0").checked == true)
    {
        document.getElementById("installmentoption0").style.display = "block";
        document.getElementById("installmentoption1").style.display = "none";
        document.getElementById("installmentoption2").style.display = "none";
    }
    else if (document.getElementById("selectedInstallmentsCalculationType1").checked == true)
    {
        document.getElementById("installmentoption0").style.display = "none";
        document.getElementById("installmentoption1").style.display = "block";
        document.getElementById("installmentoption2").style.display = "none";
    }
    else if (document.getElementById("selectedInstallmentsCalculationType2").checked == true)
    {
        document.getElementById("installmentoption0").style.display = "none";
        document.getElementById("installmentoption1").style.display = "none";
        document.getElementById("installmentoption2").style.display = "block";
    }
}
</script>


  [@widget.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content definePageMargin">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 arrowIMG orangeheading">[@spring.message "manageLoanProducts.defineLoanProduct.loanproductinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last width130px">[@spring.message "reviewAndSubmit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="post" action="defineLoanProducts.ftl" name="formname">
          <p class="font15"><span class="fontBold" id="createLoanProduct.heading">[@spring.message "manageLoanProducts.defineLoanProduct.addanewLoanproduct" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "manageLoanProducts.defineLoanProduct.enterLoanproductinformation" /]</span></p>
          <div>[@spring.message "manageLoanProducts.defineLoanProduct.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturn" /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired" /] </div>
          [@form.showAllErrors "loanProduct.*"/]
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.loanproductdetails" /]</p>
          <div class="prepend-2  span-24 last">
            <div class="span-23 ">
                <span class="pull-3 span-8 rightAlign" id="createLoanProduct.label.prdOfferingName"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.productinstancename" /]&nbsp;:</span>
                <span class="span-4">
                    [@spring.bind "loanProduct.generalDetails.name" /]
                    <input type="text" id="createLoanProduct.input.prdOffering" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
                </span>
            </div>

            <div class="span-23 ">
                <span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.shortname" /]&nbsp;:</span>
                <span class="span-4">
                    [@spring.bind "loanProduct.generalDetails.shortName" /]
                    <input type="text" size="3" maxlength="4" id="createLoanProduct.input.prdOfferingShortName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
                </span>
            </div>

            <div class="span-23 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.description" /]&nbsp;:</span>
                <span class="span-4">
                    [@spring.bind "loanProduct.generalDetails.description" /]
                    <textarea rows="7" cols="55" id="createLoanProduct.input.description" name="${spring.status.expression}">${spring.status.value?if_exists}</textarea>
                </span>
            </div>

            <div class="span-23">
                <span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.productcategory" /]&nbsp;:</span>
                <span class="span-4">
                    [@form.formSingleSelectWithPrompt "loanProduct.generalDetails.selectedCategory", loanProduct.generalDetails.categoryOptions, "--selectone--" /]
                  </span>
            </div>

            <div class="span-23 last">
                <span class="pull-3 span-8 rightAlign"><span class="red"> * </span>[@spring.message "manageLoanProducts.defineLoanProduct.startdate" /]&nbsp;:</span>
                [@spring.bind "loanProduct.generalDetails.startDateDay" /]
                <span class="span-2"><input type="text" size="1" maxlength="2" id="startDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.DD"/]</span>
                [@spring.bind "loanProduct.generalDetails.startDateMonth" /]
                  <span><input type="text" size="1" maxlength="2" id="startDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.MM"/]</span>
                  [@spring.bind "loanProduct.generalDetails.startDateYear" /]
                  <span><input type="text" size="2" maxlength="4" id="startDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.YYYY"/]</span>
            </div>

            <div class="span-23 last"> <span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.enddate" /]&nbsp;:</span>
                [@spring.bind "loanProduct.generalDetails.endDateDay" /]
                <span class="span-2"><input type="text" size="1" maxlength="2" id="endDateDD" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.DD"/]</span>
                [@spring.bind "loanProduct.generalDetails.endDateMonth" /]
                <span class="span-2"><input type="text" size="1" maxlength="2" id="endDateMM" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.MM"/]</span>
                [@spring.bind "loanProduct.generalDetails.endDateYear" /]
                <span class="span-3"><input type="text" size="2" maxlength="4" id="endDateYY" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />[@spring.message "systemUser.enterUserDetails.YYYY"/]</span>
            </div>

            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.applicablefor" /]&nbsp;:</span>
                <span class="span-4">
                [@form.formSingleSelectWithPrompt "loanProduct.generalDetails.selectedApplicableFor", loanProduct.generalDetails.applicableForOptions, "--selectone--" /]
                  </span>
            </div>

            [#if loanProduct.multiCurrencyEnabled]
            <div class="span-23 ">
                <span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.currency" /]&nbsp;:</span>
                <span class="span-4">
                    [@spring.formSingleSelect "loanProduct.selectedCurrency" loanProduct.currencyOptions /]
                  </span>
            </div>
            [/#if]

            <div class="span-23 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.includeinLoancyclecounter" /]&nbsp;:</span>
                <span class="span-4">
                    [@spring.formCheckbox "loanProduct.includeInLoanCycleCounter" "createLoanProduct.checkbox.loanCounter" /]
                  </span>
            </div>

            <div class="span-23 ">
                <span class="pull-3 span-8 rightAlign">[@spring.message "manageLoanProducts.defineLoanProduct.interestwaiver" /]&nbsp;:</span>
                <span class="span-4">
                    [@spring.formCheckbox "loanProduct.waiverInterest" "createLoanProduct.checkbox.waiverInterest" /]
                  </span>
            </div>
          </div>

          <div class="clear">&nbsp;</div>

          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.loanAmount" /]</p>
          <div class="prepend-2  span-23 last">
            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.calculateLoanAmountas" /]&nbsp;:</span>
                <div class="span-17">
                    [@spring.formRadioButtons "loanProduct.selectedLoanAmountCalculationType", loanProduct.loanAmountCalculationTypeOptions, "", "onclick='switchLoanAmountType();'" /]
                </div>

                <div class="clear">&nbsp;</div>

                <div id="loanamountoption0" class="span-14 prepend-4">
                    <div class="span-14 bluedivs fontBold paddingLeft" >
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.minloanamount" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.maxloanamount" /]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.defineLoanProduct.defaultamount" /]</span>
                    </div>
                    <div class="span-14 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountSameForAllLoans.min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountSameForAllLoans.max" /]</span>
                        <span class="span-5 last">[@spring.formInput "loanProduct.loanAmountSameForAllLoans.theDefault" /]</span>
                    </div>
                </div>

                <div id="loanamountoption1" class="span-23" style="display:none">
                    <div class="span-23 bluedivs fontBold paddingLeft" >
                        <span class="span-10">[@spring.message "manageLoanProducts.defineLoanProduct.lastLoanAmount" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.minloanamount" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.maxloanamount" /]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.defineLoanProduct.defaultamount" /]</span>
                    </div>
                    <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[0].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[0].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[0].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[0].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[0].theDefault" "size=6"/]</span>
                    </div>
                    <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[1].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[1].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[1].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[1].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[1].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[2].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[2].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[2].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[2].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[2].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[3].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[3].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[3].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[3].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[3].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[4].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[4].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[4].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[4].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[4].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[5].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[5].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[5].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[5].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLastLoanAmount[5].theDefault" "size=6"/]</span>
                    </div>
                </div>

                <div id="loanamountoption2" class="span-17" style="display:none">
                    <div class="span-17 bluedivs fontBold paddingLeft" >
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.minloanamount" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.maxloanamount" /]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.defineLoanProduct.defaultamount" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.zero" /]</span>
                        [@spring.formHiddenInput  "loanProduct.loanAmountByLoanCycle[0].loanCycleNumber" /]
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[0].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[0].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLoanCycle[0].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.one" /]</span>
                        [@spring.formHiddenInput  "loanProduct.loanAmountByLoanCycle[1].loanCycleNumber" /]
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[1].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[1].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLoanCycle[1].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.two" /]</span>
                        [@spring.formHiddenInput  "loanProduct.loanAmountByLoanCycle[2].loanCycleNumber" /]
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[2].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[2].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLoanCycle[2].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.three" /]</span>
                        [@spring.formHiddenInput  "loanProduct.loanAmountByLoanCycle[3].loanCycleNumber" /]
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[3].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[3].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLoanCycle[3].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.four" /]</span>
                        [@spring.formHiddenInput  "loanProduct.loanAmountByLoanCycle[4].loanCycleNumber" /]
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[4].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[4].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLoanCycle[4].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.abovefour" /]</span>
                        [@spring.formHiddenInput  "loanProduct.loanAmountByLoanCycle[5].loanCycleNumber" /]
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[5].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.loanAmountByLoanCycle[5].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.loanAmountByLoanCycle[5].theDefault" /]</span>
                    </div>
                </div>
            </div>
          </div>
          <div class="clear">&nbsp;</div>

          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.interestrate" /] </p>
          <div class="prepend-2  span-21 last">
            <div class="span-23 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.interestratetype" /]&nbsp;:</span>
                <span class="span-6">
                [@form.formSingleSelectWithPrompt "loanProduct.selectedInterestRateCalculationType", loanProduct.interestRateCalculationTypeOptions, "--selectone--" /]
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
          <p class="fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.repaymentSchedule" /]</p>
          <div class="prepend-2  span-23 last">
              <div class="span-23 ">
                <span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.frequencyofinstallments" /]&nbsp;:</span>
                <div class="span-12 borders">
                    <div class="borderbtm span-12">
                    [@spring.formRadioButtons "loanProduct.installmentFrequencyPeriod", loanProduct.installmentFrequencyPeriodOptions, "", "onClick='switchFrequencyMessage();'" /]
                    </div>
                    <div id="week" class="paddingLeft" id="weekDIV" >
                        <span id="weekSpecifyMessage">[@spring.message "manageLoanProducts.defineLoanProduct.ifweeks,specifythefollowing" /]</span>
                        <span id="monthSpecifyMessage" style="display:none">[@spring.message "manageLoanProducts.defineLoanProduct.ifmonths,specifythefollowing" /]</span>
                        <br />
                        [@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /][@spring.formInput "loanProduct.installmentFrequencyRecurrenceEvery" "size=3"/]
                        <span id="weekLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.week(s)" /]</span>
                        <span id="monthLabelMessage" style="display:none">[@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]</span>
                    </div>
                </div>
            </div>

            <div class="span-23"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageLoanProducts.defineLoanProduct.calculateofInstallmentsas" /]&nbsp;</span>
                <div class="span-14">
                    [@spring.formRadioButtons "loanProduct.selectedInstallmentsCalculationType", loanProduct.installmentsCalculationTypeOptions, "", "onclick='switchLoanInstallmentType();'" /]
                </div>

                <div class="clear">&nbsp;</div>
                <div id="installmentoption0"  class="span-14 prepend-4">
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
                </div>

                <div id="installmentoption1" class="span-23" style="display:none">
                    <div class="span-23 bluedivs fontBold paddingLeft" >
                        <span class="span-10">[@spring.message "manageLoanProducts.defineLoanProduct.lastLoanAmount" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.minofinstallments" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.maxofinstallments" /]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.defineLoanProduct.defaultofinstallments" /]</span>
                    </div>
                    <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[0].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[0].upper" "size=6" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[0].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[0].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[0].theDefault" "size=6"/]</span>
                    </div>
                    <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[1].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[1].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[1].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[1].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[1].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[2].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[2].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[2].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[2].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[2].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[3].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[3].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[3].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[3].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[3].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[4].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[4].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[4].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[4].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[4].theDefault" "size=6"/]</span>
                    </div>
                     <div class="span-23 paddingLeft">
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[5].lower" "size=6"/]</span>
                        <span class="span-2"> - </span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[5].upper" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[5].min" "size=6"/]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[5].max" "size=6"/]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLastLoanAmount[5].theDefault" "size=6"/]</span>
                    </div>
                </div>

                <div id="installmentoption2" class="span-17" style="display:none">
                    <div class="span-17 bluedivs fontBold paddingLeft" >
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.minofinstallments" /]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.maxofinstallments" /]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.defineLoanProduct.defaultofinstallments" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.zero" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[0].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[0].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLoanCycle[0].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.one" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[1].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[1].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLoanCycle[1].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.two" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[2].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[2].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLoanCycle[2].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.three" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[3].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[3].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLoanCycle[3].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.four" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[4].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[4].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLoanCycle[4].theDefault" /]</span>
                    </div>
                    <div class="span-17 paddingLeft">
                        <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.abovefour" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[5].min" /]</span>
                        <span class="span-4">[@spring.formInput "loanProduct.installmentsByLoanCycle[5].max" /]</span>
                        <span class="span-4 last">[@spring.formInput "loanProduct.installmentsByLoanCycle[5].theDefault" /]</span>
                    </div>
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
                        <input class="buttn2" name="add" type="button" id="LoanFeesList.button.add"  value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.applicableFees, this.form.selectedFees);" />
                        <br /><br />
                        <input class="buttn2" name="remove" type="button" value="<< [@spring.message "remove"/]" onclick="moveOptions(this.form.selectedFees, this.form.applicableFees);" />
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
                        <input class="buttn2" name="add" type="button"  id="srcFundsList.button.add"  value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.applicableFunds, this.form.selectedFunds);" />
                        <br /><br />
                        <input class="buttn2" name="remove" type="button" value="<< [@spring.message "remove"/]" id="srcFundsList.button.remove" onclick="moveOptions(this.form.selectedFunds, this.form.applicableFunds);" />
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
                        [@form.formSingleSelectWithPrompt "loanProduct.selectedInterest", loanProduct.interestGeneralLedgerOptions, "--selectone--" /]
                      </span>

                    <span class="span-2">[@spring.message "manageLoanProducts.defineLoanProduct.principal" /]&nbsp;:</span>
                        [@form.formSingleSelectWithPrompt "loanProduct.selectedPrincipal", loanProduct.principalGeneralLedgerOptions, "--selectone--" /]
                    <span class="span-3">
                      </span>
                   </span>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
            <div class="prepend-9">
                <input class="buttn" type="submit" id="CreateLoanProduct.button.preview" name="preview" value="[@spring.message "preview"/]" onclick="selectAllOptions(this.form.selectedFees);selectAllOptions(this.form.applicableFees);selectAllOptions(this.form.selectedFunds);selectAllOptions(this.form.applicableFunds);" />
                <input class="buttn2" type="submit" id="CreateLoanProduct.button.cancel" name="CANCEL" value="[@spring.message "cancel"/]" />
              </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
[@layout.footer/]