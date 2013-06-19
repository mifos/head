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
[#include "layout.ftl"]
[@adminLeftPaneLayout] <!--  Main Content Begins-->
<span id="page.id" title="view_organization_settings"></span>
  <div class="content ">
  [@widget.crumbs breadcrumbs/]
      <form method="" action="" name="formname">
    <div class="span-24 marginLeft20px lineheight1p2">
        <div class="clear">&nbsp;</div>
        <p class="font15 margin5top10bottom"><span class=" orangeheading">[@spring.message "organizationSettings.viewOrganizationSettings.organizationalSettings"/] </span></p>
        <p class="span-24">
            <div class="fontBold">[@spring.message "organizationSettings.viewOrganizationSettings.fiscalyear"/] </div>
            <div id="fiscalyear">
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.workingdays"/]</span>:&nbsp;<span class="fontnormal">${properties.workingDays}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.allowcalendardefinitionfornextyear"/]</span>:&nbsp;<span class="fontnormal"> 30 days before end of current year</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.startofWeek"/]</span>:&nbsp;<span class="fontnormal">${properties.startOfWeek}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.offdays"/]</span>:&nbsp;<span class="fontnormal">${properties.offDays}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.meetingincaseofaholiday"/]</span>:&nbsp;<span class="fontnormal">${properties.holidayMeeting} </span></div>
	        </div>
        </p>
        <p class="span-24">
            <div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.locale"/] </div>
            <div id="locale">
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.country"/]</span>:&nbsp;<span class="fontnormal">${properties.localeCountryCode}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.language"/]</span>:&nbsp;<span class="fontnormal">${properties.localeLanguageCode}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.direction"/]</span>:&nbsp;<span class="fontnormal">${properties.localeDirection}</span></div>
            </div>
        </p>
        <p class="span-24">
            <div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.accountingrules"/] </div>
            <div id="accountingrules">
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maximumInterest"/]</span>:&nbsp;<span class="fontnormal">${properties.maxInterest}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.minimumInterest"/]</span>:&nbsp;<span class="fontnormal">${properties.minInterest}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsbeforedecimal"/]</span>:&nbsp;<span class="fontnormal">${properties.digitsBeforeDecimal}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsafterdecimalforinterest"/]</span>:&nbsp;<span class="fontnormal">${properties.intDigitsAfterDecimal}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsbeforedecimalforinterest"/]</span>:&nbsp;<span class="fontnormal">${properties.intDigitsBeforeDecimal}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofinterestdays"/]</span>:&nbsp;<span class="fontnormal">${properties.interestDays}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.currencyRoundingMode"/]</span>:&nbsp;<span class="fontnormal">${properties.currencyRoundingMode}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.initialRoundingMode"/]</span>:&nbsp;<span class="fontnormal">${properties.initialRoundingMode}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.finalRoundingMode"/]</span>:&nbsp;<span class="fontnormal">${properties.finalRoundingMode}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.minCashFlowThreshold"/]</span>:&nbsp;<span class="fontnormal">${properties.minCashFlowThreshold}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maxCashFlowThreshold"/]</span>:&nbsp;<span class="fontnormal">${properties.maxCashFlowThreshold}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.minRepaymentCapacity"/]</span>:&nbsp;<span class="fontnormal">${properties.minRepaymentCapacity}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maxRepaymentCapacity"/]</span>:&nbsp;<span class="fontnormal">${properties.maxRepaymentCapacity}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.minIndebtednessRatio"/]</span>:&nbsp;<span class="fontnormal">${properties.minIndebtednessRatio}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maxIndebtednessRatio"/]</span>:&nbsp;<span class="fontnormal">${properties.maxIndebtednessRatio}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.digitsAfterDecimalForCashFlow"/]</span>:&nbsp;<span class="fontnormal">${properties.digitsAfterDecimalForCashFlow}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.GLNamesMode"/]</span>:<span class="fontnormal">
                [#switch properties.GLNamesMode]
                    [#case "1"]
                        [@spring.message "organizationSettings.viewOrganizationSettings.GLNamesMode.GLNameDashGLMode"/]
                        [#break]
                    [#case "2"]
                        [@spring.message "organizationSettings.viewOrganizationSettings.GLNamesMode.GLNameGLModeInBrackets"/]
                        [#break]
                    [#case "3"]
                        [@spring.message "organizationSettings.viewOrganizationSettings.GLNamesMode.GLNameOnly"/]
                        [#break]
                    [#case "4"]
                        [@spring.message "organizationSettings.viewOrganizationSettings.GLNamesMode.GLCodeOnly"/]
                        [#break]
                [/#switch]
                </span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.simpleAccountingModule"/]</span>:&nbsp;<span class="fontnormal">${properties.simpleAccountingModule}</span></div>
                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.overdueInterestPaidFirst"/]</span>:&nbsp;<span class="fontnormal">${properties.overdueInterestPaidFirst}</span></div>
            </div>
       </p>

        <p class="span-24">
            <div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.currencies"/] </div>
            <div id="currencies">
	            [#list properties.currencies as currency]
	                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.currency"/]</span>:&nbsp;<span class="fontnormal">${currency.code}</span></div>
	                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsafterdecimal"/]</span>:&nbsp;<span class="fontnormal">${currency.digitsAfterDecimal}</span></div>
	                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.finalRoundOffMultiple"/]</span>:&nbsp;<span class="fontnormal">${currency.finalRoundOffMultiple}</span></div>
	                <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.initialRoundOffMultiple"/]</span>:&nbsp;<span class="fontnormal">${currency.initialRoundOffMultiple}</span></div>
	            [/#list]
            </div>
        </p>

        <p class="span-24">
            <div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.clientrules"/]</div>
            <div id="clientrules">
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.centerhierarchyexists"/]</span>:&nbsp;<span class="fontnormal">${properties.centerHierarchyExists}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.groupsallowedtoapplyforloans"/]</span>:&nbsp;<span class="fontnormal">${properties.loansForGroups}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.clientcanexistoutsidegroup"/]</span>:&nbsp;<span class="fontnormal">${properties.clientsOutsideGroups}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.namesequence"/]</span>:&nbsp;<span class="fontnormal">${properties.nameSequence}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.agecheckenabled"/]</span>:&nbsp;<span class="fontnormal">${properties.isAgeCheckEnabled}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.agecheckwarningenabled"/]</span>:&nbsp;<span class="fontnormal">${properties.isAgeCheckWarningInsteadOfErrorEnabled}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.minimumallowedagefornewclients"/]</span>:&nbsp;<span class="fontnormal">${properties.minimumAge}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maximumallowedagefornewclients"/]</span>:&nbsp;<span class="fontnormal">${properties.maximumAge}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.additionalfamilydetailsrequired"/]</span>:&nbsp;<span class="fontnormal">${properties.isFamilyDetailsRequired}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maximumnumberoffamilymembers"/]</span>:&nbsp;<span class="fontnormal">${properties.maximumNumberOfFamilyMembers}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.branchManagerRoleName"/]</span>:&nbsp;<span class="fontnormal">${properties.branchManagerRoleName}</span></div>
            </div>
        </p>
        <p class="span-24">
            <div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.processflow/optionalstate"/]</div>
            <div id="processflow">
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.clientpendingapprovalstateenabled"/]</span>:&nbsp;<span class="fontnormal">${properties.clientPendingState}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.grouppendingapprovalstateenabled"/]</span>:&nbsp;<span class="fontnormal">${properties.groupPendingState}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.loanpendingapprovalstateenabled"/]</span>:&nbsp;<span class="fontnormal">${properties.loanPendingState}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.savingspendingapprovalstateenabled"/]</span>:&nbsp;<span class="fontnormal">${properties.savingsPendingState}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.approvalRequired"/]</span>:&nbsp;<span class="fontnormal">${properties.approvalRequired}</span></div>
            </div>
        </p>

        <p class="span-24">
            <div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.miscellaneous"/]</div>
            <div id="miscellaneous">
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.sessiontimeout"/]</span>:&nbsp;<span class="fontnormal">${properties.sessionTimeout}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.backdatedtransactionsallowed"/]</span>:&nbsp;<span class="fontnormal">${properties.backDatedTransactions}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.backdatedapprovalsallowed"/]</span>:&nbsp;<span class="fontnormal">${properties.backDatedApprovals}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.backDatedLoanProductCreationAllowed"/]</span>:&nbsp;<span class="fontnormal">${properties.backDatedLoanProductCreationAllowed}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.glim"/]</span>:&nbsp;<span class="fontnormal">${properties.glim}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.lsim"/]</span>:&nbsp;<span class="fontnormal">${properties.lsim}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.grouploanwithmembers"/]</span>:&nbsp;<span class="fontnormal">${properties.grouploanwithmembers}</span></div> 
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maxPointsPerPPISurvey"/]</span>:&nbsp;<span class="fontnormal">${properties.maxPointsPerPPISurvey}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.batchSizeForBatchJobs"/]</span>:&nbsp;<span class="fontnormal">${properties.batchSizeForBatchJobs}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.recordCommittingSizeForBatchJobs"/]</span>:&nbsp;<span class="fontnormal">${properties.recordCommittingSizeForBatchJobs}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.outputIntervalForBatchJobs"/]</span>:&nbsp;<span class="fontnormal">${properties.outputIntervalForBatchJobs}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.allowDataPrefetchingWhenSavingCollectionSheets"/]</span>:&nbsp;<span class="fontnormal">${properties.allowDataPrefetchingWhenSavingCollectionSheets}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.shutdownCountdownNotificationThreshold"/]</span>:&nbsp;<span class="fontnormal">${properties.shutdownCountdownNotificationThreshold}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.disbursalMax"/]</span>:&nbsp;<span class="fontnormal">${properties.disbursalMax}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.imageStorageType"/]</span>:&nbsp;<span class="fontnormal">${properties.imageStorageType}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.uploadStorageDirectory"/]</span>:&nbsp;<span class="fontnormal">${properties.uploadStorageDirectory}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.uploadQGDirectory"/]</span>:&nbsp;<span class="fontnormal">${properties.uploadQGDirectory}</span></div>
	          	<div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.imageStorageDirectory"/]</span>:&nbsp;<span class="fontnormal">${properties.imageStorageDirectory}</span></div>
	          	
	          
	            [#list pluginsPropsMap?keys as key]
	                <div><span class="fontnormal">${key}</span>:&nbsp;<span class="fontnormal">${pluginsPropsMap[key]}</span></div>
	            [/#list]
            </div>
        </p>
    </div>
       </form>
   	<div class="clear">&nbsp;</div>
   	<div class ="marginLeft20px">
    [@form.returnToPage  "AdminAction.do?method=load" "button.back" "vieworganizationsettings.button.back"/]
    </div>
  </div><!--Main Content Ends-->
  
  <br>
[/@adminLeftPaneLayout]
