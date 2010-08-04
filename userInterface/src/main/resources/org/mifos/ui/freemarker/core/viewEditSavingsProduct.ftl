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
  		<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewSavingsProducts.ftl">[@spring.message "manageSavngsProducts.editsavingsproduct.viewSavingsproducts" /]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.savingsProductName" /]</span></div>
  		<div class="clear">&nbsp;</div>
  	 	<div class="span-18 ">
            	<span class="orangeheading">[@spring.message "manageSavngsProducts.editsavingsproduct.savingsProductName" /]</span><br /><br />
                <span><span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span><br />
        	</div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "manageSavngsProducts.editsavingsproduct.editSavingsproductinformation" /]</a></span>       
        <div class="clear">&nbsp;</div>
        <p class="span-24 ">
        	<div class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.savingsproductdetails" /]</div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.productinstancename" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.shortname" /]</span><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.description" /]</span><br /><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.productcategory" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.startdate" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.enddate" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.applicablefor" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.typeofdeposits" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.mandatoryamountfordeposit" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.amountAppliesto" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.maxamountperwithdrawal" /]</span><span>&nbsp;</span></div>
        </p>
        <p class="span-24 ">
			<div class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.interestrate" /]</div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.interestrate" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.alanceusedforInterestcalculation" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.timeperiodforInterestcalculation" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "frequencyofInterestpostingtoaccounts" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.minimumbalancerequiredforInterestcalculation" /]</span><span>&nbsp;</span></div>
        </p>
        <p class="span-24 ">
			<div class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.accounting" /]</div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.gLcodefordeposits" /]</span><span>&nbsp;</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.gLcodeforInterest" /]</span><span>&nbsp;</span></div>
        </p>
        <p class="span-24 ">
            <div><a href="#">[@spring.message "manageSavngsProducts.editsavingsproduct.viewChangeLog" /]</a></div>
        </p>
       </div>
   	</form> 
  </div><!--Main Content Ends-->
 [@mifos.footer/]