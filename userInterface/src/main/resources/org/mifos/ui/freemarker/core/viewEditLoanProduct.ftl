[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
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
  <div class=" content marginLeft30">
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
            <span class="span-18 rightAlign"><a href="editLoanProduct.ftl?productId=${loanProductDetails.productDetails.id}">[@spring.message "manageLoanProducts.editloanproduct.editLoanproductinformation" /]</a></span>
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
            [#if loanProductDetails.multiCurrencyEnabled]
            <div class="span-22">
                <span class="span-8">[@spring.message "manageLoanProducts.defineLoanProduct.currency"/]:&nbsp;</span>
                <span class="span-4">${loanProductDetails.currencyCode}</span>
            </div>
            [/#if]
            <div class="span-22 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.includeinLoancyclecounter" /]</span>
                <span class="span-4">
                [#if loanProductDetails.includeInLoanCycleCounter]
                [@spring.message "boolean.yes"/]
                [#else]
                [@spring.message "boolean.no"/]
                [/#if]
                </span>
            </div>
            <div class="span-22 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.calculateLoanAmountas" /]</span>
                [#switch loanProductDetails.loanAmountDetails.calculationType]
                    [#case 1]
                    <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.sameforallLoans" /]</span>
                    [#break]
                    [#case 2]
                    <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.bylastloanAmount" /]</span>
                    [#break]
                    [#case 3]
                    <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.byloanCycle" /]</span>
                    [#break]
                [/#switch]
            </div>
            [#switch loanProductDetails.loanAmountDetails.calculationType]
              [#case 1]
              <div class="span-20 ">
                  <div class="span-17 bluedivs fontBold paddingLeft">
                    <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minloanamount"/]</span>
                    <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxloanamount"/]</span>
                    <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultamount"/]</span>
                </div>
                <div class="span-17 paddingLeft">
                    <span class="span-4 ">${loanProductDetails.loanAmountDetails.sameForAllLoanRange.min?string.number}</span>
                    <span class="span-4 ">${loanProductDetails.loanAmountDetails.sameForAllLoanRange.max?string.number}</span>
                    <span class="span-5 last">${loanProductDetails.loanAmountDetails.sameForAllLoanRange.theDefault?string.number}</span>
                </div>
                  <div>&nbsp;</div>
              </div>
              [#break]
              [#case 2]
              <div class="span-17 bluedivs fontBold paddingLeft">
                  <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.lastLoanAmount"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minloanamount"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxloanamount"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultamount"/]</span>
              </div>
              <div class="span-20 ">
                  [#list loanProductDetails.loanAmountDetails.byLastLoanAmountList as byLoanAmount]
                  <div class="span-17 paddingLeft">
                      <span class="span-4 ">${byLoanAmount.lower?string.number} - ${byLoanAmount.upper?string.number}</span>
                    <span class="span-4 ">${byLoanAmount.min?string.number}</span>
                    <span class="span-4 ">${byLoanAmount.max?string.number}</span>
                    <span class="span-5 last">${byLoanAmount.theDefault?string.number}</span>
                </div>
                [/#list]
                  <div>&nbsp;</div>
              </div>
              [#break]
              [#case 3]

              <div class="span-20 ">
                  <div class="span-17 bluedivs fontBold paddingLeft">
                      <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo"/]</span>
                    <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minloanamount"/]</span>
                    <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxloanamount"/]</span>
                    <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultamount"/]</span>
                  </div>
                      <div class="span-20 ">
                      [#assign loanCycleNumber = 1]
                      [#list loanProductDetails.loanAmountDetails.byLoanCycleList as byLoanCycle]
                      <div class="span-17 paddingLeft">
                          [#switch loanCycleNumber]
                          [#case 1]
                        <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.zero"/]</span>
                        [#break]
                        [#case 2]
                        <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.one"/]</span>
                        [#break]
                        [#case 3]
                        <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.two"/]</span>
                        [#break]
                        [#case 4]
                        <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.three"/]</span>
                        [#break]
                        [#case 5]
                        <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.four"/]</span>
                        [#break]
                        [#case 6]
                        <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.abovefour"/]</span>
                        [#break]
                          [/#switch]
                        <span class="span-4 ">${byLoanCycle.min?string.number}</span>
                        <span class="span-4 ">${byLoanCycle.max?string.number}</span>
                        <span class="span-5 last">${byLoanCycle.theDefault?string.number}</span>
                    </div>
                    [#assign loanCycleNumber = loanCycleNumber + 1]
                    [/#list]
                      <div>&nbsp;</div>
                  </div>
                  <div>&nbsp;</div>
              </div>
              [#break]
              [/#switch]
        </p>
        <p class="span-22 ">
            <div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.interestrate" /]</div>
            <div class="span-22 ">
                <span>[@spring.message "manageLoanProducts.editloanproduct.interestratetype" /]</span>
                <span>${loanProductDetails.interestRateTypeName}</span>
            </div>
            <div class="span-22 ">
                <span>[@spring.message "manageLoanProducts.editloanproduct.maxInterestrate" /]</span>
                <span>${loanProductDetails.interestRateRange.max?string.number}</span>
            </div>
            <div class="span-22 ">
                <span>[@spring.message "manageLoanProducts.editloanproduct.minInterestrate" /]</span>
                <span>${loanProductDetails.interestRateRange.min?string.number}</span>
            </div>
            <div class="span-22 ">
                <span>[@spring.message "manageLoanProducts.editloanproduct.defaultInterestrate" /]</span>
                <span>${loanProductDetails.interestRateRange.theDefault?string.number}</span>
            </div>
        </p>
        <div class="clear">&nbsp;</div>
        <p class="span-22 ">
            <div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.repaymentSchedule" /]</div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.frequencyofinstallments" /]</span>
                <span class="span-4">${loanProductDetails.repaymentDetails.recurs}
                  [#switch loanProductDetails.repaymentDetails.frequencyType]
                      [#case 1]
                    [@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]
                    [#break]
                    [#case 2]
                    [@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]
                    [#break]
                  [/#switch]
              </span>
            </div>
            <div class="span-22 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.calculateofinstallmentsas" /]</span>
                [#switch loanProductDetails.repaymentDetails.installmentCalculationDetails.calculationType]
                    [#case 1]
                    <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.sameforallLoans" /]</span>
                    [#break]
                    [#case 2]
                    <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.bylastloanAmount" /]</span>
                    [#break]
                    [#case 3]
                    <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.byloanCycle" /]</span>
                    [#break]
                   [/#switch]
            </div>
            [#switch loanProductDetails.repaymentDetails.installmentCalculationDetails.calculationType]
              [#case 1]
              <div class="span-20 ">
                <div class="span-17 bluedivs fontBold paddingLeft">
                    <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minofinstallments"/]</span>
                    <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxofinstallments"/]</span>
                    <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultofinstallments"/]</span>
                </div>
                <div class="span-17 paddingLeft">
                    <span class="span-4 ">${loanProductDetails.repaymentDetails.installmentCalculationDetails.sameForAllLoanRange.min?string.number}</span>
                    <span class="span-4 ">${loanProductDetails.repaymentDetails.installmentCalculationDetails.sameForAllLoanRange.max?string.number}</span>
                    <span class="span-5 last">${loanProductDetails.repaymentDetails.installmentCalculationDetails.sameForAllLoanRange.theDefault?string.number}</span>
                </div>
                  <div>&nbsp;</div>
              </div>
              [#break]
              [#case 2]
                  <div class="span-20 ">
                      <div class="span-17 bluedivs fontBold paddingLeft">
                          <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.lastLoanAmount"/]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minofinstallments"/]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxofinstallments"/]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultofinstallments"/]</span>
                      </div>
                      <div class="span-20 ">
                          [#list loanProductDetails.repaymentDetails.installmentCalculationDetails.byLastLoanAmountList as byLoanAmount]
                          <div class="span-17 paddingLeft">
                              <span class="span-4 ">${byLoanAmount.lower?string.number} - ${byLoanAmount.upper?string.number}</span>
                            <span class="span-4 ">${byLoanAmount.min?string.number}</span>
                            <span class="span-4 ">${byLoanAmount.max?string.number}</span>
                            <span class="span-5 last">${byLoanAmount.theDefault?string.number}</span>
                        </div>
                        [/#list]
                    </div>
                      <div>&nbsp;</div>
                  </div>
              [#break]
              [#case 3]
                  <div class="span-20 ">
                      <div class="span-17 bluedivs fontBold paddingLeft">
                          <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo"/]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minofinstallments"/]</span>
                        <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxofinstallments"/]</span>
                        <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultofinstallments"/]</span>
                      </div>
                      <div class="span-20 ">
                          [#assign loanCycleNumber = 1]
                          [#list loanProductDetails.repaymentDetails.installmentCalculationDetails.byLoanCycleList as byLoanCycle]
                          <div class="span-17 paddingLeft">
                              [#switch loanCycleNumber]
                              [#case 1]
                            <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.zero"/]</span>
                            [#break]
                            [#case 2]
                            <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.one"/]</span>
                            [#break]
                            [#case 3]
                            <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.two"/]</span>
                            [#break]
                            [#case 4]
                            <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.three"/]</span>
                            [#break]
                            [#case 5]
                            <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.four"/]</span>
                            [#break]
                            [#case 6]
                            <span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.abovefour"/]</span>
                            [#break]
                              [/#switch]
                            <span class="span-4 ">${byLoanCycle.min?string.number}</span>
                            <span class="span-4 ">${byLoanCycle.max?string.number}</span>
                            <span class="span-5 last">${byLoanCycle.theDefault?string.number}</span>
                        </div>
                        [#assign loanCycleNumber = loanCycleNumber + 1]
                        [/#list]
                          <div>&nbsp;</div>
                      </div>
                      <div>&nbsp;</div>
                  </div>
              [#break]
             [/#switch]

          <div class="span-22 ">
              <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.graceperiodtype" /]</span>
              [#switch loanProductDetails.repaymentDetails.gracePeriodType]
                [#case 1]
                <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.none" /]</span>
                [#break]
                [#case 2]
                <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.graceonallrepayments" /]</span>
                [#break]
                [#case 3]
                <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.principalonlygrace" /]</span>
                [#break]
               [/#switch]
          </div>
          <div class="span-22 ">
              <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.graceperiodduration" /]</span>
              <span class="span-4">${loanProductDetails.repaymentDetails.gracePeriodDuration?string.number}</span>
          </div>
        </p>

        <p class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.fees" /]</p>
        <div class="span-20 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.fees" /]</span>
                <span class="span-4">
                <ol>
                    [#list loanProductDetails.fees as fee]
                    <li>${fee}</li>
                    [/#list]
                </ol>
                  </span>
        </div>
        <div class="clear">&nbsp;</div>

        <p class="span-22 ">
            <div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.accounting" /]</div>
            <div class="span-20 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.sourcesoffunds" /]</span>
                <span class="span-4">
                <ol>
                    [#list loanProductDetails.funds as fund]
                    <li>${fund}</li>
                    [/#list]
                </ol>
                  </span>
            </div>
            <div class="span-20 "><span>[@spring.message "manageLoanProducts.editloanproduct.productGLcode" /]</span></div>
            <div class="span-20 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.interest" /]</span>
                <span class="span-4">${loanProductDetails.interestGlCodeValue}</span>
            </div>
            <div class="span-20 ">
                <span class="span-8">[@spring.message "manageLoanProducts.editloanproduct.principal" /]</span>
                <span class="span-4">${loanProductDetails.principalGlCodeValue}</span>
            </div>
        </p>
        <div class="clear">&nbsp;</div>
        <p class="span-22 ">
            <div class="span-22 "><a href="viewLoanProductChangeLog.ftl?productId=${loanProductDetails.productDetails.id}">[@spring.message "manageLoanProducts.editloanproduct.viewChangeLog" /]</a></div>
        </p>
    </div>
  </div>
  <!--Main Content Ends-->
[/@adminLeftPaneLayout]