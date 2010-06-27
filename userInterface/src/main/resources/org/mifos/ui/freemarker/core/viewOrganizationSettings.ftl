 [#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar ht950">
  [#include "adminLeftPane.ftl" /]
  </div> 
 <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
  		[@mifos.crumbs breadcrumbs/]
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class=" orangeheading">[@spring.message "organizationalSettings"/] </span></p>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "fiscalyear"/] </div>
        	<div><span class="fontnormal">[@spring.message "workingdays"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.workingDays}</span></div>
            <div><span class="fontnormal">[@spring.message "allowcalendardefinitionfornextyear"/]</span>&nbsp;:&nbsp;<span class="fontnormal"> 30 days before end of current year</span></div>
            <div><span class="fontnormal">[@spring.message "startofWeek"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.startOfWeek}</span></div>
            <div><span class="fontnormal">[@spring.message "offdays"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.offDays}</span></div>
            <div><span class="fontnormal">[@spring.message "meetingincaseofaholiday"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.holidayMeeting} </span></div>          
        </p>
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "locale"/] </div>
        	<div><span class="fontnormal">[@spring.message "country"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.localeCountryCode}</span></div>
            <div><span class="fontnormal">[@spring.message "language"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.localeLanguageCode}</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "accountingrules"/] </div>
            <div><span class="fontnormal">[@spring.message "maximumInterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.maxInterest}</span></div>
            <div><span class="fontnormal">[@spring.message "minimumInterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.minInterest}</span></div>
            <div><span class="fontnormal">[@spring.message "numberofdigitsbeforedecimal"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.digitsBeforeDecimal}</span></div>
            <div><span class="fontnormal">[@spring.message "numberofdigitsafterdecimalforinterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.intDigitsAfterDecimal}</span></div>
            <div><span class="fontnormal">[@spring.message "numberofdigitsbeforedecimalforinterest"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.intDigitsBeforeDecimal}</span></div>
            <div><span class="fontnormal">[@spring.message "numberofinterestdays"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.interestDays}</span></div>
            <div><span class="fontnormal">[@spring.message "currencyRoundingMode"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.currencyRoundingMode}</span></div>
            <div><span class="fontnormal">[@spring.message "initialRoundingMode"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.initialRoundingMode}</span></div>
            <div><span class="fontnormal">[@spring.message "finalRoundingMode"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.finalRoundingMode}</span></div>
       </p>

        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "currencies"/] </div>
        	[#list properties.currencies as currency]
	        	<div><span class="fontnormal">[@spring.message "currency"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.code}</span></div>
	            <div><span class="fontnormal">[@spring.message "numberofdigitsafterdecimal"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.digitsAfterDecimal}</span></div>
	            <div><span class="fontnormal">[@spring.message "finalRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.finalRoundOffMultiple}</span></div>
	            <div><span class="fontnormal">[@spring.message "initialRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${currency.initialRoundOffMultiple}</span></div>
            [/#list]
        </p>
        
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "clientrules"/]</div>
        	<div><span class="fontnormal">[@spring.message "centerhierarchyexists"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.centerHierarchyExists}</span></div>
            <div><span class="fontnormal">[@spring.message "groupsallowedtoapplyforloans"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.loansForGroups}</span></div>
            <div><span class="fontnormal">[@spring.message "clientcanexistoutsidegroup"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.clientsOutsideGroups}</span></div>
            <div><span class="fontnormal">[@spring.message "namesequence"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.nameSequence}</span></div>
            <div><span class="fontnormal">[@spring.message "agecheckenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.isAgeCheckEnabled}</span></div>
            <div><span class="fontnormal">[@spring.message "minimumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.minimumAge}</span></div>
            <div><span class="fontnormal">[@spring.message "maximumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.maximumAge}</span></div>
            <div><span class="fontnormal">[@spring.message "additionalfamilydetailsrequired"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.isFamilyDetailsRequired}</span></div>
            <div><span class="fontnormal">[@spring.message "maximumnumberoffamilymembers"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.maximumNumberOfFamilyMembers}</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "processflow/optionalstate"/]</div>
        	<div><span class="fontnormal">[@spring.message "clientpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.clientPendingState}</span></div>
            <div><span class="fontnormal">[@spring.message "grouppendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.groupPendingState}</span></div>
            <div><span class="fontnormal">[@spring.message "loandisbursedtoloanofficerstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.loanDisbursedState}</span></div>
            <div><span class="fontnormal">[@spring.message "loanpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.loanPendingState}</span></div>
            <div><span class="fontnormal">[@spring.message "savingspendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.savingsPendingState}</span></div>
        </p>
        
        <p class="span-24">
        	<div class="fontBold marginTop10">[@spring.message "miscellaneous"/]</div>
        	<div><span class="fontnormal">[@spring.message "sessiontimeout"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.sessionTimeout}</span></div>
            <div><span class="fontnormal">[@spring.message "numberofdaysinadvancethecollectionsheetshouldbegenerated"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.collectionSheetAdvanceDays}</span></div>
            <div><span class="fontnormal">[@spring.message "backdatedtransactionsallowed"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.backDatedTransactions}</span></div>
            <div><span class="fontnormal">[@spring.message "glim"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.glim}</span></div>
            <div><span class="fontnormal">[@spring.message "lsim"/]</span>&nbsp;:&nbsp;<span class="fontnormal">${properties.lsim}</span></div>            
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]