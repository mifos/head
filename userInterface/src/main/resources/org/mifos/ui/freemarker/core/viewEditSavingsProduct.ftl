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
[/@adminLeftPaneLayout]