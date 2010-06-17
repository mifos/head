 [#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" /] 

 
 <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
  		<div class="span-22 bluedivs paddingLeft"><a href="admin.html">[@spring.message "tab.Admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "organizationalSettings"/]</span></div>
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class=" orangeheading">[@spring.message "organizationalSettings"/] </span></p>
        
        <p class="span-24">0
        	<div class="fontBold">[@spring.message "fiscalyear"/] </div>
        	<div><span>[@spring.message "workingdays"/]</span>&nbsp;:&nbsp;<span>${properties.workingDays}</span></div>
            <div><span>[@spring.message "allowcalendardefinitionfornextyear"/]</span>&nbsp;:&nbsp;<span> 30 days before end of current year</span></div>
            <div><span>[@spring.message "startofWeek"/]</span>&nbsp;:&nbsp;<span>${properties.startOfWeek}</span></div>
            <div><span>[@spring.message "offdays"/]</span>&nbsp;:&nbsp;<span>${properties.offDays}</span></div>
            <div><span>[@spring.message "meetingincaseofaholiday"/]</span>&nbsp;:&nbsp;<span>${properties.holidayMeeting} </span></div>          
        </p><br>        
        <br>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "locale"/] </div>
        	<div><span>[@spring.message "country"/]</span>&nbsp;:&nbsp;<span>${properties.localeCountryCode}</span></div>
            <div><span>[@spring.message "language"/]</span>&nbsp;:&nbsp;<span>${properties.localeLanguageCode}</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "accountingrules"/] </div>
        	<div><span>[@spring.message "currency"/]</span>&nbsp;:&nbsp;<span><!--${properties.currencies.get(0).code}-->Currency code</span></div>
            <div><span>[@spring.message "maximumInterest"/]</span>&nbsp;:&nbsp;<span>${properties.maxInterest}</span></div>
            <div><span>[@spring.message "minimumInterest"/]</span>&nbsp;:&nbsp;<span>${properties.minInterest}</span></div>
            <div><span>[@spring.message "numberofdigitsafterdecimal"/]</span>&nbsp;:&nbsp;<span><!--${properties.currencies.get(0).digitsAfterDecimal}-->digitsAfterDecimal</span></div>
            <div><span>[@spring.message "numberofdigitsbeforedecimal"/]</span>&nbsp;:&nbsp;<span>${properties.digitsBeforeDecimal}</span></div>
            <div><span>[@spring.message "numberofdigitsafterdecimalforinterest"/]</span>&nbsp;:&nbsp;<span>${properties.intDigitsAfterDecimal}</span></div>
            <div><span>[@spring.message "numberofdigitsbeforedecimalforinterest"/]</span>&nbsp;:&nbsp;<span>${properties.intDigitsBeforeDecimal}</span></div>
            <div><span>[@spring.message "numberofinterestdays"/]</span>&nbsp;:&nbsp;<span>${properties.interestDays}</span></div>
            <div><span>[@spring.message "currencyRoundingMode"/]</span>&nbsp;:&nbsp;<span>${properties.currencyRoundingMode}</span></div>
            <div><span>[@spring.message "initialRoundingMode"/]</span>&nbsp;:&nbsp;<span>${properties.initialRoundingMode}</span></div>
            <div><span>[@spring.message "finalRoundingMode"/]</span>&nbsp;:&nbsp;<span>${properties.finalRoundingMode}</span></div>
            <div><span>[@spring.message "finalRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span><!--${properties.currencies.get(0).finalRoundOffMultiple}-->finalRoundOffMultiple</span></div>
            <div><span>[@spring.message "initialRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span><!--${properties.currencies.get(0).initialRoundOffMultiple}-->initialRoundOffMultiple</span></div>
        </p>
        
        <p class="span-24">
        	<div class="fontBold">[@spring.message "clientrules"/]</div>
        	<div><span>[@spring.message "centerhierarchyexists"/]</span>&nbsp;:&nbsp;<span>${properties.centerHierarchyExists}</span></div>
            <div><span>[@spring.message "groupsallowedtoapplyforloans"/]</span>&nbsp;:&nbsp;<span>${properties.loansForGroups}</span></div>
            <div><span>[@spring.message "clientcanexistoutsidegroup"/]</span>&nbsp;:&nbsp;<span>${properties.clientsOutsideGroups}</span></div>
            <div><span>[@spring.message "namesequence"/]</span>&nbsp;:&nbsp;<span>${properties.nameSequence}</span></div>
            <div><span>[@spring.message "agecheckenabled"/]</span>&nbsp;:&nbsp;<span>${properties.isAgeCheckEnabled}</span></div>
            <div><span>[@spring.message "minimumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span>${properties.minimumAge}</span></div>
            <div><span>[@spring.message "maximumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span>${properties.maximumAge}</span></div>
            <div><span>[@spring.message "additionalfamilydetailsrequired"/]</span>&nbsp;:&nbsp;<span>${properties.isFamilyDetailsRequired}</span></div>
            <div><span>[@spring.message "maximumnumberoffamilymembers"/]</span>&nbsp;:&nbsp;<span>${properties.maximumNumberOfFamilyMembers}</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "processflow/optionalstate"/]</div>
        	<div><span>[@spring.message "clientpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>${properties.clientPendingState}</span></div>
            <div><span>[@spring.message "grouppendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>${properties.groupPendingState}</span></div>
            <div><span>[@spring.message "loandisbursedtoloanofficerstateenabled"/]</span>&nbsp;:&nbsp;<span>${properties.loanDisbursedState}</span></div>
            <div><span>[@spring.message "loanpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>${properties.loanPendingState}</span></div>
            <div><span>[@spring.message "savingspendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>${properties.savingsPendingState}</span></div>
        </p>
        
        <p class="span-24">
        	<div class="fontBold">[@spring.message "miscellaneous"/]</div>
        	<div><span>[@spring.message "sessiontimeout"/]</span>&nbsp;:&nbsp;<span>${properties.sessionTimeout}</span></div>
            <div><span>[@spring.message "numberofdaysinadvancethecollectionsheetshouldbegenerated"/]</span>&nbsp;:&nbsp;<span>${properties.collectionSheetAdvanceDays}</span></div>
            <div><span>[@spring.message "backdatedtransactionsallowed"/]</span>&nbsp;:&nbsp;<span>${properties.backDatedTransactions}</span></div>
            <div><span>[@spring.message "glim"/]</span>&nbsp;:&nbsp;<span>${properties.glim}</span></div>
            <div><span>[@spring.message "slim"/]</span>&nbsp;:&nbsp;<span>${properties.lsim}</span></div>            
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  <#assign seq = ${properties.currencies}>
<#list seq as x>
  ${x.code}<#if x_has_next>,</#if>
</#list>  
  [@mifos.footer/]