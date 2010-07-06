[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar ht600">
[#include "adminLeftPane.ftl" ]
</div> 
 <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
  		<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewSavingsProducts.ftl">[@spring.message "viewSavingsproducts" /]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "savingsProductName" /]</span></div>
  		<div class="clear">&nbsp;</div>
  	 	<div class="span-18 ">
            	<span class="orangeheading">[@spring.message "savingsProductName" /]</span><br /><br />
                <span><span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span><br />
        	</div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "editSavingsproductinformation" /]</a></span>       
        <div class="clear">&nbsp;</div>
        <p class="span-24 ">
        	<div class="fontBold">[@spring.message "savingsproductdetails" /]</div>
            <div><span>[@spring.message "productinstancename" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "shortname" /]</span><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div><span>[@spring.message "description" /]</span><br /><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div><span>[@spring.message "productcategory" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "startdate" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "enddate" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "applicablefor" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "typeofdeposits" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "mandatoryamountfordeposit" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "amountAppliesto" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "maxamountperwithdrawal" /]</span><span>&nbsp;</span></div>
        </p>
        <p class="span-24 ">
			<div class="fontBold">[@spring.message "interestrate" /]</div>
            <div><span>[@spring.message "interestrate" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "alanceusedforInterestcalculation" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "timeperiodforInterestcalculation" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "frequencyofInterestpostingtoaccounts" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "minimumbalancerequiredforInterestcalculation" /]</span><span>&nbsp;</span></div>
        </p>
        <p class="span-24 ">
			<div class="fontBold">[@spring.message "accounting" /]</div>
            <div><span>[@spring.message "gLcodefordeposits" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "gLcodeforInterest" /]</span><span>&nbsp;</span></div>
        </p>
        <p class="span-24 ">
            <div><a href="#">[@spring.message "viewChangeLog" /]</a></div>
        </p>
       </div>
   	</form> 
  </div><!--Main Content Ends-->
 [@mifos.footer/]