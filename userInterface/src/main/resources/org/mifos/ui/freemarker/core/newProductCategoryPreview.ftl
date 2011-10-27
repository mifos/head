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
            <p class="span-17 silverheading completeIMG padding20left"
               style="width:50%">[@spring.message "manageProducts.defineNewCategory.productcategoryinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit" /]</p>
        </div>
        <div class="margin20lefttop">
        <form method="POST" action="newProductCategoryPreview.ftl" name="newCategoryPreview">
          <p class="font15"><span class="fontBold">[@spring.message"admin.definenewcategory"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "reviewAndSubmit"/]</span></p>
          <div>[@spring.message "manageProduct.editCategory.PreviewTheFieldsBelow.ThenClickSubmit"/]</div>
          <p class="clear">&nbsp; </p>
          <div class="fontBold margin10bottom">[@spring.message "manageProducts.editCategory.categoryDetails"/] </div>
          [@form.showAllErrors "formBean.*"/]
          <p class="margin10bottom"">
              <span class="fontBold">[@spring.message "manageProducts.defineNewCategory.productType"/]</span><span>:&nbsp;[#switch formBean.productTypeId]
                    [#case "1"]
                        <span>
                            [@spring.message "ftlDefinedLabels.manageProducts.editCategory.loan"  /]
                        </span>
                    [#break]
                    [#case "2"]
                        <span></span>&nbsp;<span>
                             [@spring.message "ftlDefinedLabels.manageProducts.editCategory.savings"  /]
                        </span>
                    [#break]
                [/#switch] [@spring.bind "formBean.productTypeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/></span><br />
            <span class="fontBold">
                [@spring.message "manageProducts.editCategory.categoryName"/]:&nbsp;
            </span>
            <span>${formBean.productCategoryName}
                [@spring.bind "formBean.productCategoryName"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            </span><br />
          </p>
          <div class="fontBold">[@spring.message "manageProducts.editCategory.description"/]</div>
          <p class="margin30bottom">
            <span>${formBean.productCategoryDesc}[@spring.bind "formBean.productCategoryDesc"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>[@spring.showErrors "<br />"/]</span><br />
          </p>
          <p class="">
            <span><input class="insidebuttn" type="submit" name="EDIT" value="[@spring.message "manageProducts.editCategory.editcategoryinformation"/]"/></span>
          </p>

          <div class="clear">&nbsp;</div>
          <div class="buttonsSubmitCancel margin20right">
            <input class="buttn" type="submit" name="SUBMIT" value="[@spring.message "submit"/]"/>
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