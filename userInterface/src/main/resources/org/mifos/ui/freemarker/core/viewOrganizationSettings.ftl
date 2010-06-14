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
        	<div><span>[@spring.message "workingdays"/]</span>&nbsp;:&nbsp;<span>Monday, Tuesday, Wednesday, Thursday, Friday, Saturday </span></div>
            <div><span>[@spring.message "allowcalendardefinitionfornextyear"/]</span>&nbsp;:&nbsp;<span> 30 days before end of current year</span></div>
            <div><span>[@spring.message "startofWeek"/]</span>&nbsp;:&nbsp;<span>Monday</span></div>
            <div><span>[@spring.message "offdays"/]</span>&nbsp;:&nbsp;<span>Sunday </span></div>
            <div><span>[@spring.message "meetingincaseofaholiday"/]</span>&nbsp;:&nbsp;<span>same_day </span></div>

        </p>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "locale"/] </div>
        	<div><span>[@spring.message "country"/]</span>&nbsp;:&nbsp;<span>GB</span></div>
            <div><span>[@spring.message "language"/]</span>&nbsp;:&nbsp;<span>EN</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "accountingrules"/] </div>
        	<div><span>[@spring.message "currency"/]</span>&nbsp;:&nbsp;<span>INR</span></div>
            <div><span>[@spring.message "maximumInterest"/]</span>&nbsp;:&nbsp;<span>999.0</span></div>
            <div><span>[@spring.message "minimumInterest"/]</span>&nbsp;:&nbsp;<span>0.0</span></div>
            <div><span>[@spring.message "numberofdigitsafterdecimal"/]</span>&nbsp;:&nbsp;<span>1</span></div>
            <div><span>[@spring.message "numberofdigitsbeforedecimal"/]</span>&nbsp;:&nbsp;<span>7</span></div>
            <div><span>[@spring.message "numberofdigitsafterdecimalforinterest"/]</span>&nbsp;:&nbsp;<span>5</span></div>
            <div><span>[@spring.message "numberofdigitsbeforedecimalforinterest"/]</span>&nbsp;:&nbsp;<span>10</span></div>
            <div><span>[@spring.message "numberofinterestdays"/]</span>&nbsp;:&nbsp;<span>365</span></div>
            <div><span>[@spring.message "currencyRoundingMode"/]</span>&nbsp;:&nbsp;<span>HALF_UP</span></div>
            <div><span>[@spring.message "initialRoundingMode"/]</span>&nbsp;:&nbsp;<span>HALF_UP</span></div>
            <div><span>[@spring.message "finalRoundingMode"/]</span>&nbsp;:&nbsp;<span>CEILING </span></div>
            <div><span>[@spring.message "finalRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span>1</span></div>
            <div><span>[@spring.message "initialRoundOffMultiple"/]</span>&nbsp;:&nbsp;<span>1</span></div>
        </p>
        
        <p class="span-24">
        	<div class="fontBold">[@spring.message "clientrules"/]</div>
        	<div><span>[@spring.message "centerhierarchyexists"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
            <div><span>[@spring.message "groupsallowedtoapplyforloans"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
            <div><span>[@spring.message "clientcanexistoutsidegroup"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
            <div><span>[@spring.message "namesequence"/]</span>&nbsp;:&nbsp;<span>first_name, middle_name, last_name, second_last_name</span></div>
            <div><span>[@spring.message "agecheckenabled"/]</span>&nbsp;:&nbsp;<span>No</span></div>
            <div><span>[@spring.message "minimumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span>0</span></div>
            <div><span>[@spring.message "maximumallowedagefornewclients"/]</span>&nbsp;:&nbsp;<span>0</span></div>
            <div><span>[@spring.message "additionalfamilydetailsrequired"/]</span>&nbsp;:&nbsp;<span>No</span></div>
            <div><span>[@spring.message "maximumnumberoffamilymembers"/]</span>&nbsp;:&nbsp;<span>10</span></div>
        </p>
        <p class="span-24">
        	<div class="fontBold">[@spring.message "processflow/optionalstate"/]</div>
        	<div><span>[@spring.message "clientpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
            <div><span>[@spring.message "grouppendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
            <div><span>[@spring.message "loandisbursedtoloanofficerstateenabled"/]</span>&nbsp;:&nbsp;<span>No</span></div>
            <div><span>[@spring.message "loanpendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
            <div><span>[@spring.message "savingspendingapprovalstateenabled"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
        </p>
        
        <p class="span-24">
        	<div class="fontBold">[@spring.message "miscellaneous"/]</div>
        	<div><span>[@spring.message "sessiontimeout"/]</span>&nbsp;:&nbsp;<span>30 minutes</span></div>
            <div><span>[@spring.message "numberofdaysinadvancethecollectionsheetshouldbegenerated"/]</span>&nbsp;:&nbsp;<span>1</span></div>
            <div><span>[@spring.message "backdatedtransactionsallowed"/]</span>&nbsp;:&nbsp;<span>Yes</span></div>
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]