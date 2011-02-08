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
[#assign savings][@mifostag.mifoslabel name="Savings" /][/#assign]
<!--  Main Content Begins-->
<span id="page.id" title="SavingsProductDetails"></span>

<div class="content">
    <div>
        <div class="breadcrumb">
            <a href="AdminAction.do?method=load">
            [@spring.message "admin" /]</a>
            &nbsp;/
            <a href="viewSavingsProducts.ftl">
               [@spring.messageArgs "ftlDefinedLabels.manageSavngsProducts.editsavingsproduct.viewSavingsproducts" , [savings]  /]
            </a>&nbsp;/
            <span class="fontBold">${savingsProductDetails.productDetails.name}</span>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="marginLeft30" style="line-height:1.2">
            <div class="span-18 width90prc" style="margin-bottom:7px;">
                <span class="orangeheading">${savingsProductDetails.productDetails.name}</span>
                <div style="position:relative;top:-17px; text-align:right;">
                    <a id="SavingsProductDetails.link.editSavingsProduct" href="editSavingsProduct.ftl?productId=${savingsProductDetails.productDetails.id}">
                        [@spring.messageArgs "ftlDefinedLabels.manageSavngsProducts.editsavingsproduct.editSavingsproductinformation" , [savings]  /]
                    </a>
                </div>
                [#switch savingsProductDetails.productDetails.status]
                    [#case 2]
                        <span><img
                                src="pages/framework/images/status_activegreen.gif"/></span>&nbsp;
                                <span>
                                    [#assign active][@mifostag.mifoslabel name="ProductState-Active" /][/#assign]
                                    [@spring.messageArgs "ftlDefinedLabels.active" , [active] /]
                                </span>
                        [#break]
                    [#case 5]
                        <span><img
                                src="pages/framework/images/status_closedblack.gif"/></span>&nbsp;
                                <span>
                                    [#assign inactive][@mifostag.mifoslabel name="ProductState-Inactive" /][/#assign]
                                    [@spring.messageArgs "ftlDefinedLabels.inactive" , [inactive] /]
                                </span>
                        [#break]
                [/#switch]
            </div>
            <div style="height:5px;" class="clear">&nbsp;</div>
            <p class="span-24 ">
            <div class="fontBold black-subheading">
                [@spring.messageArgs "ftlDefinedLabels.manageSavngsProducts.editsavingsproduct.savingsproductdetails" , [savings]  /]
            </div>
            <div>
                <span>[@spring.message "manageSavngsProducts.editsavingsproduct.productinstancename" /]</span>
                <span>${savingsProductDetails.productDetails.name}</span>
            </div>
            <div>
                <span>[@spring.message "manageSavngsProducts.editsavingsproduct.shortname" /]</span>&nbsp;<span>${savingsProductDetails.productDetails.shortName}</span>
            </div>
            <div class="clear">&nbsp;</div>
            <div>
                <span>[@spring.message "manageSavngsProducts.editsavingsproduct.description" /]</span><br/><span>${savingsProductDetails.productDetails.description}</span>
            </div>
            <div class="clear">&nbsp;</div>
            <div>
                <span>[@spring.message "manageSavngsProducts.editsavingsproduct.productcategory" /]</span>&nbsp;<span>${savingsProductDetails.productDetails.categoryName}</span>
            </div>
            <div>
                <span>[@spring.message "manageSavngsProducts.editsavingsproduct.startdate" /]</span>&nbsp;<span>${savingsProductDetails.productDetails.startDateFormatted}</span>
            </div>
            <div>
                <span>[@spring.message "manageSavngsProducts.editsavingsproduct.enddate" /]</span>&nbsp;<span>${savingsProductDetails.productDetails.endDateFormatted}</span>
            </div>
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

            <div class="fontBold black-subheading">[@spring.message "manageSavngsProducts.editsavingsproduct.interestrate" /]</div>
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

            <div class="fontBold black-subheading">[@spring.message "manageSavngsProducts.editsavingsproduct.accounting" /]</div>
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

            <div class="marginTop15">
                <a href="viewSavingsProductChangeLog.ftl?productId=${savingsProductDetails.productDetails.id}">[@spring.message "manageSavngsProducts.editsavingsproduct.viewChangeLog" /]</a>
            </div>
            </p>
        </div>
    </div>
</div>
<!--Main Content Ends-->
[/@adminLeftPaneLayout]