 [#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
  <div class="content ">
  [@mifos.crumbs breadcrumbs/]
  	<form method="" action="" name="formname">
    <div class="span-24">  		
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class=" orangeheading">[@spring.message "organizationSettings.viewOrganizationSettings.organizationalSettings"/] </span></p>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "organizationSettings.viewOrganizationSettings.fiscalyear"/] </div>
        	<div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.workingdays"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.workingDays}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.allowcalendardefinitionfornextyear"/]</span>&nbsp;:&nbsp;<span class="fontnormal"> 30 days before end of current year</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.startofWeek"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.startOfWeek}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.offdays"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.offDays}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.meetingincaseofaholiday"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.holidayMeeting} </span></div>          
        </p>
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.locale"/] </div>
        	<div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.country"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.localeCountryCode}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.language"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.localeLanguageCode}</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.accountingrules"/] </div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maximumInterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.maxInterest}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.minimumInterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.minInterest}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsbeforedecimal"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.digitsBeforeDecimal}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsafterdecimalforinterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.intDigitsAfterDecimal}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsbeforedecimalforinterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.intDigitsBeforeDecimal}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofinterestdays"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.interestDays}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.currencyRoundingMode"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.currencyRoundingMode}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.initialRoundingMode"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.initialRoundingMode}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.finalRoundingMode"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.finalRoundingMode}</span></div>
       </p>

        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.currencies"/] </div>
        	[#list properties.currencies as currency]
	        	<div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.currency"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.code}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdigitsafterdecimal"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.digitsAfterDecimal}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.finalRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.finalRoundOffMultiple}</span></div>
	            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.initialRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.initialRoundOffMultiple}</span></div>
            [/#list]
        </p>
        
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.clientrules"/]</div>
        	<div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.centerhierarchyexists"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.centerHierarchyExists}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.groupsallowedtoapplyforloans"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.loansForGroups}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.clientcanexistoutsidegroup"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.clientsOutsideGroups}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.namesequence"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.nameSequence}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.agecheckenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.isAgeCheckEnabled}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.minimumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.minimumAge}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maximumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.maximumAge}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.additionalfamilydetailsrequired"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.isFamilyDetailsRequired}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.maximumnumberoffamilymembers"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.maximumNumberOfFamilyMembers}</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.processflow/optionalstate"/]</div>
        	<div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.clientpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.clientPendingState}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.grouppendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.groupPendingState}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.loanpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.loanPendingState}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.savingspendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.savingsPendingState}</span></div>
        </p>
        
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "organizationSettings.viewOrganizationSettings.miscellaneous"/]</div>
        	<div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.sessiontimeout"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.sessionTimeout}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.numberofdaysinadvancethecollectionsheetshouldbegenerated"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.collectionSheetAdvanceDays}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.backdatedtransactionsallowed"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.backDatedTransactions}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.glim"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.glim}</span></div>
            <div><span class="fontnormal">[@spring.message "organizationSettings.viewOrganizationSettings.lsim"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.lsim}</span></div>            
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]