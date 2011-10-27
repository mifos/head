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
<div class=" content">
    <form method="post" action="categoryPreview.ftl" name="formname">
        <div class="span-24">
        [@widget.crumbs breadcrumbs/]
            <div class="margin20lefttop">
                <p class="font15">
                    <span class="fontBold">[@spring.message "manageProducts.editCategory.categoryName"/]</span>&nbsp;-&nbsp;<span
                        class="orangeheading">[@spring.message "manageProducts.editCategory.editcategoryinformation"/]</span>
                </p>

                <div>[@spring.message "manageProduct.editCategory.PreviewTheFieldsBelow.ThenClickSubmit"/]</div>

                <div class="clear">&nbsp;</div>
                <p class="fontBold margin10bottom">[@spring.message "manageProducts.editCategory.categoryDetails"/]</p>
                [@form.showAllErrors "formBean.*"/]
                <p class="margin10bottom"><span class="fontBold">[@spring.message "manageProducts.editCategory.categoryName"/]:&nbsp;</span>
                    <span class="">[@spring.bind "formBean.productCategoryName"/]<input type="hidden" name="${spring.status.expression}"
                                                                 value="${spring.status.value?default("")}"/>${formBean.productCategoryName}
                    </span>
                </p>

                <p class="fontBold margin10bottom">[@spring.message "manageProducts.editCategory.description"/]:&nbsp;</p>

                <div class="margin10bottom">
                [@spring.bind "formBean.productCategoryDesc"/]
                    <input type="hidden" name="${spring.status.expression}"
                           value="${spring.status.value?default("")}"/>${formBean.productCategoryDesc}
                </div>
                <p class="margin10bottom">
                    <span class="fontBold">[@spring.message "manageProducts.editCategory.status1"/]:&nbsp;</span>
            <span class="">
                [@spring.bind "formBean.productCategoryStatusId"/]
                    <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    [#switch formBean.productCategoryStatusId]
                        [#case "1"]
                            <span>[@spring.message "active"/]</span>
                            [#break]
                        [#case "2"]
                            <span>[@spring.message "inactive"/]</span>
                            [#break]
                    [/#switch]
            </span>
                    <br/>
                </p>
            [@spring.bind "formBean.productType"/]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.productTypeId"/]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.globalPrdCategoryNum"/]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>

                <div class="clear">&nbsp;</div>
                <div><input class="insidebuttn" type="submit" name="EDIT"
                            value="[@spring.message "manageProducts.editCategory.editcategoryinformation"/]"/></div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel">
                    <input class="buttn" type="submit" name="SUBMIT" value="[@spring.message "submit"/]"/>
                    <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </div>
        </div>
    </form>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]