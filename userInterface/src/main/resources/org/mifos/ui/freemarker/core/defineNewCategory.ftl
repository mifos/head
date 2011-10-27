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
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
[@layout.header "title" /]
  [@widget.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left"
               style="width:50%">[@spring.message "manageProducts.defineNewCategory.productcategoryinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit" /]</p>
        </div>
        <div class="margin20lefttop">
        <form method="post" action="defineNewCategory.ftl" name="defineNewCategory">
        <p class="font15">
            <span class="fontBold">[@spring.message "admin.definenewcategory" /]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "manageProducts.defineNewCategory.enterProductcategoryinformation" /]</span>
        </p>
        <p>&nbsp;&nbsp;</p>
        <div>[@spring.message "manageProducts.defineNewCategory.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation" /]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
        <p>&nbsp;&nbsp;</p>
        [@form.showAllErrors "formBean.*"/]
        <p class="fontBold margin10bottom">[@spring.message "manageProducts.defineNewCategory.categoryDetails" /]</p>
        <div class="prepend-3  span-21 last">
            <div class="span-20 ">
                <span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineNewCategory.productType" /]</span>
                <span class="span-4">
                    [#--[@spring.bind "formBean.productTypeId"/]
                    <select id="${spring.status.expression}" name="${spring.status.expression}">
                        <option value="" [#if spring.status.value?exists != 1 && spring.status.value?if_exists != 2] selected=="selected"[/#if] >[@spring.message "--Select--"/]</option>
                        <option value="1" [#if spring.status.value?if_exists == 1] selected=="selected"[/#if] >[@spring.message "Loan-Loan"/]</option>
                        <option value="2" [#if spring.status.value?if_exists == 2] selected=="selected"[/#if] >[@spring.message "Savings-Savings"/]</option>
                    </select>--]
                    [@spring.bind "formBean.productTypeId" /]
                        <select id="${spring.status.expression}" name="${spring.status.expression}">
                            <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
                            [#if typeList?is_hash]
                                [#list typeList?keys as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>
                                    [@spring.message "${typeList[value]}"  /]
                                </option>
                                [/#list]
                            [#else]
                                [#list typeList as value]
                                    <option value="${value?html}"[@spring.checkSelected value/]>
                                        [@spring.message "${typeList[value]}"  /]
                                    </option>
                                [/#list]
                            [/#if]
                        </select>

                </span>
            </div>
            <p>&nbsp;&nbsp;</p>
            <div class="span-20 ">
                <span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineNewCategory.categoryName" /]</span>
                <span class="span-4">
                    [@spring.bind "formBean.productCategoryName"/]
                    <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?default("")}" />
                </span>
              </div>
              <p>&nbsp;&nbsp;</p>
            <div class="span-20 ">
                <span class="span-4 rightAlign">[@spring.message "manageProducts.defineNewCategory.categoryDescription" /]</span>
                <span>
                [@spring.bind "formBean.productCategoryDesc"/]
                    <textarea cols="50" rows="6" name="${spring.status.expression}" id="${spring.status.expression}">${spring.status.value?default("")}</textarea>
                [@spring.showErrors "<br/>"/]
                </span>[@spring.bind "formBean.productCategoryStatusId"/]
                    <input type="hidden" name="${spring.status.expression}" id="${spring.status.expression}" value="1" />
              </div>
          </div>
          <div class="clear">&nbsp;</div>
          <div class="buttonsSubmitCancel margin20right">
              <input class="buttn" type="submit" name="PREVIEW" value="[@spring.message "preview"/]"/>
              <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@layout.footer/]