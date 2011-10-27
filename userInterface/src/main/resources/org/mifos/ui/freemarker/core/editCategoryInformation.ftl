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
<div class="content">
    <form method="post" action="editCategoryInformation.ftl" name="editCategoryInformation">
        [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "admin.viewproductcategories":"viewProductCategories.ftl",formBean.productCategoryName:""}/]
    [@widget.crumbpairs breadcrumb/]
        <div class="span-24 marginLeft20px">
            <div class="clear">&nbsp;</div>
            <p class="font15">
                <span class="fontBold">[@spring.bind "formBean.productCategoryName"/]${spring.status.value?default("")}</span>&nbsp;-&nbsp;
            <span class="orangeheading">
            [@spring.message "manageProducts.editCategory.editcategoryinformation" /]
            </span>
            </p>

            <div>
            [@spring.message "manageProducts.editCategory.edittheFieldsBelow" /]
            </div>
            <div>
                <span class="red">* </span>
            [@spring.message "fieldsmarkedwithanasteriskarerequired." /]
            </div>
        [@spring.bind "formBean"/]
        [@form.showAllErrors "formBean.*"/]
            <p class="fontBold margin20topbottom">
            [@spring.message "manageProducts.editCategory.categoryDetails" /]
            </p>

            <div class="prepend-3 span-22 last">
                <div class="span-22">
                <span class="span-4 rightAlign">
                    <span class="red"> * </span>
                [@spring.message "manageProducts.editCategory.categoryName" /]
                </span>
                <span class="span-4">
                [@spring.bind "formBean.productCategoryName"/]
                    <input type="text" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                </span>
                </div>
                <div class="span-22">
                <span class="span-4 rightAlign">
                [@spring.message "manageProducts.editCategory.description" /]
                </span>
                <span>
                [@spring.bind "formBean.productCategoryDesc"/]
                    <textarea cols="50" rows="6"
                              name="${spring.status.expression}">${spring.status.value?default("")}</textarea>
                </span>
                </div>
            </div>
            <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign">
                <span class="red"> * </span>
            [@spring.message "manageProducts.editCategory.status1" /]&nbsp;:
            </span>
            <span class="span-4 margin10left">
            [@spring.bind "formBean.productType"/]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.productTypeId"/]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.globalPrdCategoryNum"/]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                [#assign statusTypes={"1":"active","2":"inactive"}/]
            [@spring.bind "formBean.productCategoryStatusId"/]
                <select id="${spring.status.expression}" name="${spring.status.expression}">
                    <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
                    [#if statusTypes?is_hash]
                        [#list statusTypes?keys as value]
                            <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(statusTypes[value]?html)}</option>
                        [/#list]
                        [#else]
                            [#list statusTypes as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(value?html)}</option>
                            [/#list]
                    [/#if]
                </select>
            </span>
            </div>
            <div class="clear">&nbsp;</div>
            <hr/>
            <div class="prepend-10">
                <input class="buttn" type="submit" name="PREVIEW" value="[@spring.message "preview"/]"/>
                <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
            </div>
        </div>
    </form>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]