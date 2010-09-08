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
    	<div class="bluedivs paddingLeft">
    		<a href="AdminAction.do?method=load">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<a href="viewLoanProducts.ftl">[@spring.message "manageLoanProducts.editloanproduct.viewLoanProducts"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "manageLoanProducts.editloanproduct.loanProductName"/]</span>
   		</div>
        <p>&nbsp;&nbsp;</p>
    <div class="span-22">
  		<div class="span-22 ">
       		<div class="span-18 ">
           	<span class="orangeheading">${loanProductDetails.productDetails.name}</span><br /><br />
           	[#switch loanProductDetails.productDetails.status]
            	[#case 1]
            	<span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span>
            	[#break]
            	[#case 4]
            	<span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
            	[#break]
            [/#switch] 
           	<br />
        	</div>
            <span class="span-5 rightAlign"><a href="editLoanProduct.ftl?productId=${loanProductDetails.productDetails.id}">[@spring.message "manageLoanProducts.editloanproduct.editLoanproductinformation" /]</a></span>
        </div>
        <div class="clear">&nbsp;</div>
        
        <p class="span-22 ">
        	<div class="fontBold">[@spring.message "manageLoanProducts.editloanproduct.loanproductdetails" /]</div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.productinstancename" /]</span>
            	<span>${loanProductDetails.productDetails.name}</span>
            </div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.shortname" /]</span>
            	<span>${loanProductDetails.productDetails.shortName}</span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.description" /]</span><br />
            	<span>${loanProductDetails.productDetails.description}</span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.productcategory" /]</span>
            	<span>${loanProductDetails.productDetails.categoryName}</span>
            </div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.startdate" /]</span>
            	<span>${loanProductDetails.productDetails.startDateFormatted}</span>
            </div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.enddate" /]</span>
            	<span>${loanProductDetails.productDetails.endDateFormatted}</span>
            </div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.applicablefor" /]</span>
            	[#switch loanProductDetails.productDetails.applicableFor]
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
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.includeinLoancyclecounter" /]</span>
            	<span>&nbsp;</span>
            </div>
            <div class="span-22 ">
            	<span>[@spring.message "manageLoanProducts.editloanproduct.calculateLoanAmountas" /]</span>
            	<span>&nbsp;</span>
            </div>
            <div class="span-20 ">
          		<div class="span-17 bluedivs fontBold paddingLeft">
            		<span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.minloanamount" /]</span>
                    <span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.maxloanamount" /]</span>
                	<span class="span-5 last">[@spring.message "manageLoanProducts.editloanproduct.defaultamount" /]</span>
            	</div>
            	<div class="span-17 paddingLeft">
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-5 last">&nbsp;</span>
            	</div>
          </div>
        </p>
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.interestrate" /]</div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.interestratetype" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.maxInterestrate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.minInterestrate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.defaultInterestrate" /]</span><span>&nbsp;</span></div>
        </p>
        <div class="clear">&nbsp;</div>
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.repaymentSchedule" /]</div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.frequencyofinstallments" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.calculateofinstallmentsas" /]</span><span>&nbsp;</span></div>
			<div class="span-20 ">
          		<div class="span-17 bluedivs fontBold paddingLeft">
            		<span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.minofinstallments" /]</span>
                	<span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.maxofinstallments" /]</span>
                	<span class="span-5 last">[@spring.message "manageLoanProducts.editloanproduct.defaultofinstallments" /]</span>
            	</div>
            	<div class="span-17 paddingLeft">
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-5 last">&nbsp;</span>
            	</div>
          </div>
          <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.graceperiodtype" /]</span><span>&nbsp;</span></div>
          <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.graceperiodduration" /]</span><span>&nbsp;</span></div>
        </p> 
        <p class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.fees" /]</p>
        <div class="clear">&nbsp;</div>       
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.accounting" /]</div>
            <div class="span-20 "><span>[@spring.message "manageLoanProducts.editloanproduct.sourcesoffunds" /]&nbsp;&nbsp;</span><br /><span>&nbsp;</span>
			</div>
            <div class="span-20 "><span>[@spring.message "manageLoanProducts.editloanproduct.productGLcode" /]&nbsp;&nbsp;</span></div>
            <div class="span-20 "><span>[@spring.message "manageLoanProducts.editloanproduct.interest" /]&nbsp;&nbsp;</span>
                    <span>&nbsp;</span>
			</div>
            <div class="span-20 "><span >[@spring.message "manageLoanProducts.editloanproduct.principal" /]&nbsp;&nbsp;</span>
                    <span>&nbsp;</span>
			</div>
        </p>
        <div class="clear">&nbsp;</div> 
        <p class="span-22 ">
            <div class="span-22 "><a href="#">[@spring.message "manageLoanProducts.editloanproduct.viewChangeLog" /]</a></div>
        </p>
	</div>
  </div>
  <!--Main Content Ends-->
[/@adminLeftPaneLayout]