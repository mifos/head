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
[@adminLeftPaneLayout]
 <!--  Main Content Begins-->  
  <div class=" content">
    <div class="span-24">
  		<div class="bluedivs paddingLeft">
  			<a href="AdminAction.do?method=load">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewSavingsProducts.ftl">[@spring.message "manageSavngsProducts.editsavingsproduct.viewSavingsproducts" /]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.savingsProductName" /]</span>
  		</div>
  		<div class="clear">&nbsp;</div>
  	 	<div class="span-18 ">
           	<span class="orangeheading">${savingsProductDetails.productDetails.name}</span><br /><br />
           	[#switch savingsProductDetails.productDetails.status]
            	[#case 2]
            	<span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span>
            	[#break]
            	[#case 5]
            	<span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
            	[#break]
            [/#switch] 
           	<br />
        </div>
        <span class="span-5 rightAlign"><a href="editSavingsProduct.ftl?productId=${savingsProductDetails.productDetails.id}">[@spring.message "manageSavngsProducts.editsavingsproduct.editSavingsproductinformation" /]</a></span>       
        <div class="clear">&nbsp;</div>
        <p class="span-24 ">
        	<div class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.savingsproductdetails" /]</div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.productinstancename" /]</span>
            	<span>${savingsProductDetails.productDetails.name}</span>
            </div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.shortname" /]</span><span>${savingsProductDetails.productDetails.shortName}</span></div>
            <div class="clear">&nbsp;</div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.description" /]</span><br /><span>${savingsProductDetails.productDetails.description}</span></div>
            <div class="clear">&nbsp;</div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.productcategory" /]</span><span>${savingsProductDetails.productDetails.categoryName}</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.startdate" /]</span><span>${savingsProductDetails.productDetails.startDateFormatted}</span></div>
            <div><span>[@spring.message "manageSavngsProducts.editsavingsproduct.enddate" /]</span><span>${savingsProductDetails.productDetails.endDateFormatted}</span></div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.applicablefor" /]</span>
            [#switch savingsProductDetails.productDetails.applicableFor]
            	[#case 1]
            	<span>[@spring.message "manageProducts.defineSavingsProducts.clients" /]</span>
            	[#break]
            	[#case 2]
            	<span>[@spring.message "manageProducts.defineSavingsProducts.groups" /]</span>
            	[#break]
            	[#case 3]
            	<span>[@spring.message "manageProducts.defineSavingsProducts.centers" /]</span>
            	[#break]
            	[#case 4]
            	<span>[@spring.message "manageProducts.defineSavingsProducts.allcustomers" /]</span>
            	[#break]
            [/#switch] 
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.typeofdeposits" /]</span>
            	[#switch savingsProductDetails.depositType]
	            	[#case 1]
	            	<span>[@spring.message "manageProducts.defineSavingsProducts.mandatory" /]</span>
	            	[#break]
	            	[#case 2]
	            	<span>[@spring.message "manageProducts.defineSavingsProducts.voluntary" /]</span>
	            	[#break]
            	[/#switch] 
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.mandatoryamountfordeposit" /]</span>
            	<span>${savingsProductDetails.amountForDeposit}</span>
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.amountAppliesto" /]</span>
				[#switch savingsProductDetails.groupMandatorySavingsType]
					[#case 0]
					<span></span>
					[#break]
	            	[#case 1]
	            	<span>[@spring.message "manageProducts.defineSavingsProducts.perIndividual" /]</span>
	            	[#break]
	            	[#case 2]
	            	<span>[@spring.message "manageProducts.defineSavingsProducts.completeGroup" /]</span>
	            	[#break]
            	[/#switch] 
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.maxamountperwithdrawal" /]</span>
            	<span>${savingsProductDetails.maxWithdrawal}</span>
            </div>
        </p>
        <p class="span-24 ">
			<div class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.interestrate" /]</div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.interestrate" /]</span>
            	<span>${savingsProductDetails.interestRate}</span>
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.alanceusedforInterestcalculation" /]</span>
            	[#switch savingsProductDetails.interestCalculationType]
	            	[#case 1]
	            	<span>[@spring.message "manageProducts.defineSavingsProducts.minimumBalance" /]</span>
	            	[#break]
	            	[#case 2]
	            	<span>[@spring.message "manageProducts.defineSavingsProducts.averageBalance" /]</span>
	            	[#break]
            	[/#switch]
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.timeperiodforInterestcalculation" /]</span>
            	[#switch savingsProductDetails.interestCalculationFrequencyPeriod]
	            	[#case 2]
	            	<span>${savingsProductDetails.interestCalculationFrequency} [@spring.message "manageProducts.defineSavingsProducts.month(s)" /]</span>
	            	[#break]
	            	[#case 3]
	            	<span>${savingsProductDetails.interestCalculationFrequency} [@spring.message "manageProducts.defineSavingsProducts.day(s)" /]</span>
	            	[#break]
            	[/#switch]
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.frequencyofInterestpostingtoaccounts" /]</span>
            	<span>${savingsProductDetails.interestPostingMonthlyFrequency} [@spring.message "manageProducts.defineSavingsProducts.month(s)" /]</span>
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.minimumbalancerequiredforInterestcalculation" /]</span>
            	<span>${savingsProductDetails.minBalanceForInterestCalculation}</span>
            </div>
        </p>
        <p class="span-24 ">
			<div class="fontBold">[@spring.message "manageSavngsProducts.editsavingsproduct.accounting" /]</div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.gLcodefordeposits" /]</span>
            	<span>${savingsProductDetails.depositGlCodeValue}</span>
            </div>
            <div>
            	<span>[@spring.message "manageSavngsProducts.editsavingsproduct.gLcodeforInterest" /]</span>
            	<span>${savingsProductDetails.interestGlCodeValue}</span>
            </div>
        </p>
        <p class="span-24 ">
            <div><a href="viewSavingsProductChangeLog.ftl?productId=${savingsProductDetails.productDetails.id}">[@spring.message "manageSavngsProducts.editsavingsproduct.viewChangeLog" /]</a></div>
        </p>
       </div>
  </div>
  <!--Main Content Ends-->
[/@adminLeftPaneLayout]