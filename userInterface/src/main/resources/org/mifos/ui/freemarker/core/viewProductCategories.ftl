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
<span id="page.id" title="viewProductCategories"></span>
<div class="content">
[@widget.crumbs breadcrumbs/]
    <div style="margin-left:15px; margin-top:15px;">
        <p class="font15 orangeheading margin5topbottom">[@spring.message "manageProducts.viewProductCategories.viewproductcategories" /]</p>

        <p class="margin5top10bottom">[@spring.message "manageProducts.viewProductCategories.clickonacategorybelowtoviewdetailsandmakechangesor"/]
            <a href="defineNewCategory.ftl">[@spring.message "admin.definenewproductcategory"/]</a></p>

        <div class="lineheight1p2">
            <span class="fontBold">
                [@spring.message "ftlDefinedLabels.manageProducts.viewProductCategories.loans"  /]
            </span>
            <ul>
                [#list dto.productCategoryTypeList as typeList]
                    [#list dto.productCategoryDtoList as dtoList]
                        [#if typeList.productName == "Loan-Loan" && typeList_index == dtoList_index]
                            <li type="circle"><a
                                    href="viewProductCategoryDetails.ftl?globalPrdCategoryNum=${dtoList.globalProductCategoryNumber}">${dtoList.productCategoryName}</a>
                            </li>
                        [/#if]
                    [/#list]
                [/#list]
            </ul>
        </div>
        <div class="lineheight1p2">
            <span class="fontBold">
                [@spring.message "ftlDefinedLabels.manageProducts.viewProductCategories.savings" /]
            </span>
            <ul>
                [#list dto.productCategoryTypeList as typeList]
                    [#list dto.productCategoryDtoList as dtoList]
                        [#if typeList.productName=="Savings-Savings" && typeList_index==dtoList_index]
                            <li type="circle"><a
                                    href="viewProductCategoryDetails.ftl?globalPrdCategoryNum=${dtoList.globalProductCategoryNumber}">${dtoList.productCategoryName}</a>
                            </li>
                        [/#if]
                    [/#list]
                [/#list]
            </ul>
        </div>
    </div>
</div><!--Main Content Ends -->

[/@adminLeftPaneLayout]