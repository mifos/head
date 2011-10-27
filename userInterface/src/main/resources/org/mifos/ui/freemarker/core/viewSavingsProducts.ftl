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
<span id="page.id" title="View_AllSavingsProduct"></span>
<div class=" content">
[@widget.crumb url="admin.viewSavingsproducts" /]
    <p>&nbsp;&nbsp;</p>

    <p class="font15 orangeheading" style="margin-left:18px;">
         [@spring.message "ftlDefinedLabels.admin.viewSavingsproducts"  /]
    </p>

    <div style="margin-left:18px; margin-top:2px;">
        <p>
            [@spring.message "ftlDefinedLabels.manageLoanProducts.viewSavingsProducts.clickonaSavingsproductbelowtoviewdetailsandmakechangesor" /]
            <a href="defineSavingsProduct.ftl">
                [@spring.message "ftlDefinedLabels.manageLoanProducts.viewSavingsProducts.defineNewSavingPorduct"  /]
            </a>
        </p>
        [#list products as product]
            <div style="margin-top:10px;">
                <img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/> <a
                    href="viewEditSavingsProduct.ftl?productId=${product.prdOfferingId}">${product.prdOfferingName}</a>
                [#switch product.prdOfferingStatusId]
                    [#case 2]
                        [#break]
                    [#case 5]
                        <span><img
                                src="pages/framework/images/status_closedblack.gif"/>[@spring.message "inactive"/]</span>
                        [#break]
                [/#switch]
            </div>
        [/#list]
    </div>
</div>
<!--Main Content Ends-->
[/@adminLeftPaneLayout]